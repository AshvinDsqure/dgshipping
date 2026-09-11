/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bulkupload.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.bulkupload.BulkUploadProcess;
import org.dspace.bulkupload.BulkUploadProcessStatus;
import org.dspace.bulkupload.service.BulkUploadRuleEngine.ColumnRule;
import org.dspace.content.*;
import org.dspace.content.service.*;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.EPersonService;
import org.dspace.handle.service.HandleService;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

public class BulkUploadServiceImpl implements BulkUploadService {

    private static final Logger log = LogManager.getLogger(BulkUploadServiceImpl.class);

    @Autowired
    private BulkUploadProcessService processService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private WorkspaceItemService workspaceItemService;

    @Autowired
    private HandleService handleService;

    @Autowired
    private BulkUploadRuleEngine ruleEngine;

    @Autowired
    private BitstreamService bitstreamService;

    @Autowired
    private BundleService bundleService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private EPersonService epersonService;

    @Autowired
    private InstallItemService installItemService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private BitstreamFormatService bitstreamFormatService;


    @Override
    public BulkUploadProcess startUpload(Context context, UUID processId, InputStream csvStream, UUID epersonId)
            throws SQLException, IOException, AuthorizeException {

        BulkUploadProcess process = processService.find(context, processId);
        if (process == null) {
            throw new IllegalArgumentException("Process not found: " + processId);
        }
        process.setStatus(BulkUploadProcessStatus.RUNNING);
        process.setMessage("Bulk Upload processing has been started.");
        processService.update(context, process);
        context.commit();

        CompletableFuture.runAsync(() -> {
            Context asyncContext = new Context();
            asyncContext.turnOffAuthorisationSystem();
            try {
                if (epersonId != null) {
                    EPerson eperson = epersonService.find(asyncContext, epersonId);
                    if (eperson != null) {
                        asyncContext.setCurrentUser(eperson);
                    }
                }
                processCsvUpload(asyncContext, processId, csvStream);
            } catch (Exception e) {
                log.error("Bulk upload failed for process {}: {}", processId, e.getMessage(), e);
                try {
                    processService.updateStatus(asyncContext, processId, BulkUploadProcessStatus.FAILED);
                    BulkUploadProcess p = processService.find(asyncContext, processId);
                    if (p != null) {
                        p.setMessage("Upload failed: " + e.getMessage());
                        processService.update(asyncContext, p);
                    }
                    asyncContext.commit();
                } catch (SQLException | AuthorizeException ex) {
                    log.error("Failed to update process status: {}", ex.getMessage());
                }
            } finally {
                try {
                    asyncContext.restoreAuthSystemState();
                    asyncContext.complete();
                } catch (Exception e) {
                    log.warn("Failed to complete async context: {}", e.getMessage());
                }
            }
        });

        return process;
    }

    private void processCsvUpload(Context context, UUID processId, InputStream csvStream)
            throws IOException, SQLException, AuthorizeException {

        StringWriter resultCsvWriter = new StringWriter();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            try (CSVParser parser = csvFormat.parse(reader)) {
                Map<String, Integer> headerMap = parser.getHeaderMap();
                List<CSVRecord> records = parser.getRecords();
                int totalRows = records.size();
                int processedRows = 0;

                Map<String, String> cleanHeaderMap = new LinkedHashMap<>();
                for (String header : headerMap.keySet()) {
                    String cleanHeader = header != null && header.startsWith("\uFEFF")
                            ? header.substring(1) : header;
                    cleanHeaderMap.put(header, cleanHeader);
                }

                String[] resultHeaders = new String[cleanHeaderMap.size() + 2];
                int hIdx = 0;
                for (String ch : cleanHeaderMap.values()) {
                    resultHeaders[hIdx++] = ch;
                }
                resultHeaders[cleanHeaderMap.size()] = "submitted";
                resultHeaders[cleanHeaderMap.size() + 1] = "message";

                CSVPrinter csvPrinter = new CSVPrinter(resultCsvWriter,
                        CSVFormat.DEFAULT.builder()
                                .setHeader(resultHeaders)
                                .build());

                BulkUploadProcess progP = processService.find(context, processId);
                if (progP != null) {
                    progP.setStatus(BulkUploadProcessStatus.INPROGRESS);
                    progP.setMessage("Processing in progress...");
                    progP.setTotalRows(totalRows);
                    processService.update(context, progP);
                    context.commit();
                }

                for (CSVRecord record : records) {
                    String rowStatus = "success";
                    String rowMessage = "";
                    try {
                        String collectionValue = "";
                        for (String header : headerMap.keySet()) {
                            String cleanHeader = cleanHeaderMap.get(header);
                            ColumnRule rule = ruleEngine.getRule(cleanHeader);
                            if (rule != null && rule.isCollection()) {
                                collectionValue = record.isSet(header) ? record.get(header) : "";
                                break;
                            }
                        }
                        Collection collection = null;
                        if (collectionValue != null && !collectionValue.trim().isEmpty()) {
                            DSpaceObject dso = handleService.resolveToObject(context, collectionValue.trim());
                            if (dso instanceof Collection) {
                                collection = (Collection) dso;
                            }
                            if (collection == null) {
                                try {
                                    List<Collection> collections = collectionService.findAll(context);
                                    for (Collection col : collections) {
                                        if (col.getName() != null && col.getName().trim().equalsIgnoreCase(collectionValue.trim())) {
                                            collection = col;
                                            break;
                                        }
                                    }
                                } catch (SQLException e) {
                                    log.warn("Failed to search collections by name: {}", e.getMessage());
                                }
                            }
                        }
                        if (collection == null) {
                            log.warn("Skipping row {}: no valid collection specified", record.getRecordNumber());
                            rowStatus = "failed";
                            rowMessage = "No valid collection specified";
                            List<Object> failedRowValues = new ArrayList<>();
                            for (String header : headerMap.keySet()) {
                                failedRowValues.add(record.isSet(header) ? record.get(header) : "");
                            }
                            failedRowValues.add(rowStatus);
                            failedRowValues.add(rowMessage);
                            csvPrinter.printRecord(failedRowValues);
                            continue;
                        }
                        WorkspaceItem workspaceItem = workspaceItemService.create(context, collection, false);
                        Item item = workspaceItem.getItem();
                        for (String header : headerMap.keySet()) {
                            String cleanHeader = cleanHeaderMap.get(header);
                            ColumnRule headerRule = ruleEngine.getRule(cleanHeader);
                            if (headerRule != null && headerRule.isCollection()
                                    && (headerRule.getMetadata() == null || headerRule.getMetadata().isBlank())) {
                                continue;
                            }
                            if (headerRule != null && headerRule.isFile()) {
                                String fileValue = record.isSet(header) ? record.get(header) : "";
                                if (fileValue != null && !fileValue.trim().isEmpty()) {
                                    String separator = headerRule.hasMultiple() ? headerRule.getSeparator() : null;
                                    String[] filePaths = separator != null
                                            ? fileValue.split(java.util.regex.Pattern.quote(separator)) : new String[]{fileValue};
                                    for (String fp : filePaths) {
                                        String trimmedPath = fp.trim();
                                        if (trimmedPath.isEmpty()) {
                                            continue;
                                        }
                                        String basePath = configurationService.getProperty("bulkupload.file.basepath", "");
                                        String fullFilePath = trimmedPath;
                                        if (basePath != null && !basePath.trim().isEmpty()) {
                                            fullFilePath = basePath.trim() + File.separator + trimmedPath;
                                        }
                                        File fileToUpload = new File(fullFilePath);
                                        if (!fileToUpload.exists() || !fileToUpload.isFile()) {
                                            throw new IOException("File not found: " + trimmedPath);
                                        }
                                        Bundle originalBundle = null;
                                        List<Bundle> bundles = itemService.getBundles(item, "ORIGINAL");
                                        if (bundles != null && !bundles.isEmpty()) {
                                            originalBundle = bundles.get(0);
                                        }
                                        if (originalBundle == null) {
                                            originalBundle = bundleService.create(context, item, "ORIGINAL");
                                        }
                                        try (FileInputStream fis = new FileInputStream(fileToUpload)) {
                                            Bitstream bitstream = bitstreamService.create(context, originalBundle, fis);
                                            bitstream.setName(context, fileToUpload.getName());
                                            BitstreamFormat bitstreamFormat = bitstreamFormatService.guessFormat(context, bitstream);
                                            bitstreamService.setFormat(context, bitstream, bitstreamFormat);
                                            bitstreamService.update(context, bitstream);
                                        }
                                        String processedPath = configurationService.getProperty("bulkupload.file.processedpath", "");
                                        if (processedPath != null && !processedPath.trim().isEmpty()) {
                                            File processedDir = new File(processedPath.trim());
                                            if (!processedDir.exists()) {
                                                processedDir.mkdirs();
                                            }
                                            File destFile = new File(processedDir, fileToUpload.getName());
                                            if (destFile.exists()) {
                                                destFile = new File(processedDir, System.currentTimeMillis() + "_" + fileToUpload.getName());
                                            }
                                            try {
                                                java.nio.file.Files.copy(fileToUpload.toPath(), destFile.toPath());
                                            } catch (IOException copyEx) {
                                                log.warn("Failed to copy file '{}' to processed folder: {}", fileToUpload.getName(), copyEx.getMessage());
                                            }
                                        }
                                    }
                                }
                                continue;
                            }
                            String value = record.isSet(header) ? record.get(header) : "";
                            if (value != null && !value.trim().isEmpty()) {
                                ColumnRule rule = ruleEngine.getRule(cleanHeader);

                                String metadataField = cleanHeader;
                                if (rule != null && rule.getMetadata() != null && !rule.getMetadata().isBlank()) {
                                    metadataField = rule.getMetadata();
                                }
                                String[] parts = metadataField.split("\\.", 3);
                                String schema = parts.length > 0 ? parts[0] : MetadataSchemaEnum.DC.getName();
                                String element = parts.length > 1 ? parts[1] : metadataField;
                                String qualifier = parts.length > 2 ? parts[2] : null;

                                if (rule != null && rule.hasMultiple()) {
                                    String separator = rule.getSeparator();
                                    String[] values = value.split(java.util.regex.Pattern.quote(separator));
                                    for (String v : values) {
                                        String trimmed = v.trim();
                                        if (!trimmed.isEmpty()) {
                                            itemService.addMetadata(context, item, schema, element, qualifier, null, trimmed);
                                        }
                                    }
                                } else {
                                    itemService.addMetadata(context, item, schema, element, qualifier, null, value);
                                }
                            }
                        }
                        itemService.update(context, item);
                        workspaceItemService.update(context, workspaceItem);
                        installItemService.installItem(context, workspaceItem);
                        processedRows++;

                        if (processedRows % 5 == 0 || processedRows == totalRows) {
                            processService.updateProgress(context, processId, processedRows, totalRows);
                            context.commit();
                        }
                    } catch (Exception e) {
                        log.warn("Failed to process row {}: {}", record.getRecordNumber(), e.getMessage());
                        rowStatus = "failed";
                        rowMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
                    }

                    List<Object> rowValues = new ArrayList<>();
                    for (String header : headerMap.keySet()) {
                        rowValues.add(record.isSet(header) ? record.get(header) : "");
                    }
                    rowValues.add("success".equals(rowStatus) ? "true" : "false");
                    rowValues.add(rowMessage);
                    csvPrinter.printRecord(rowValues);
                }

                csvPrinter.flush();
                csvPrinter.close();

                int failedRows = totalRows - processedRows;
                BulkUploadProcessStatus finalStatus;
                String finalMessage;
                if (processedRows == totalRows && failedRows == 0) {
                    finalStatus = BulkUploadProcessStatus.COMPLETED;
                    finalMessage = "Bulk Upload completed. " + processedRows + " rows processed.";
                } else if (processedRows == 0) {
                    finalStatus = BulkUploadProcessStatus.FAILED;
                    finalMessage = "Bulk Upload failed. 0 of " + totalRows + " rows processed.";
                } else {
                    finalStatus = BulkUploadProcessStatus.COMPLETED;
                    finalMessage = "Bulk Upload completed. " + processedRows + " of " + totalRows
                            + " rows processed. " + failedRows + " row(s) failed.";
                }
                BulkUploadProcess p = processService.find(context, processId);
                if (p != null) {
                    p.setProcessedRows(processedRows);
                    p.setTotalRows(totalRows);
                    if (totalRows > 0) {
                        p.setProgress((processedRows * 100) / totalRows);
                    }
                    p.setStatus(finalStatus);
                    p.setMessage(finalMessage);
                    p.setResultCsv(resultCsvWriter.toString());
                    processService.update(context, p);
                }
                context.commit();
            }
        }
    }

    @Override
    public BulkUploadProcess getProcessStatus(Context context, UUID processId) throws SQLException {
        return processService.find(context, processId);
    }
}
