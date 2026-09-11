/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bulkupload.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.bulkupload.BulkUploadProcess;
import org.dspace.bulkupload.BulkUploadProcessStatus;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BulkUploadValidationServiceImpl implements BulkUploadValidationService {

    private static final Logger log = LogManager.getLogger(BulkUploadValidationServiceImpl.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private BulkUploadRuleEngine ruleEngine;

    @Autowired
    private BulkUploadProcessService processService;

    private boolean lastValidationValid = false;

    @Override
    public BulkUploadProcess validate(Context context, InputStream csvStream, UUID epersonId)
        throws IOException, SQLException, AuthorizeException {

        StringWriter resultCsvWriter = new StringWriter();
        int[] rowCount = {0};
        List<BulkUploadRuleEngine.ColumnValidationResult> columnResults = validateColumns(csvStream, resultCsvWriter, rowCount);
        boolean allValid = columnResults.stream().allMatch(BulkUploadRuleEngine.ColumnValidationResult::isValid);
        lastValidationValid = allValid;

        BulkUploadProcess process = new BulkUploadProcess();
        process.setProcessName("Bulk Upload");
        process.setEpersonId(epersonId);
        process.setTotalRows(rowCount[0]);
        process.setStatus(allValid ? BulkUploadProcessStatus.VALIDATED : BulkUploadProcessStatus.VALIDATION_FAILED);
        process.setMessage(allValid ? "Validation passed" : "Validation failed");

        String validationJson = mapper.writeValueAsString(buildValidationResponse(columnResults));
        process.setValidationResultJson(validationJson);
        process.setResultCsv(resultCsvWriter.toString());

        return processService.create(context, process);
    }

    @Override
    public List<BulkUploadRuleEngine.ColumnValidationResult> validateColumns(InputStream csvStream)
        throws IOException {
        return validateColumns(csvStream, null, null);
    }

    private List<BulkUploadRuleEngine.ColumnValidationResult> validateColumns(InputStream csvStream,
        StringWriter resultCsvWriter, int[] rowCount)
        throws IOException {

        List<BulkUploadRuleEngine.ColumnValidationResult> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {

            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

            try (CSVParser parser = csvFormat.parse(reader)) {
                Map<String, Integer> headerMap = parser.getHeaderMap();

                Map<String, String> cleanHeaderMap = new LinkedHashMap<>();
                for (String header : headerMap.keySet()) {
                    String cleanHeader = header != null && header.startsWith("\uFEFF")
                        ? header.substring(1) : header;
                    cleanHeaderMap.put(header, cleanHeader);
                    BulkUploadRuleEngine.ColumnValidationResult colResult =
                        new BulkUploadRuleEngine.ColumnValidationResult();
                    colResult.setName(cleanHeader);
                    colResult.setValid(true);
                    colResult.setMessage("");
                    results.add(colResult);
                }

                CSVPrinter csvPrinter = null;
                if (resultCsvWriter != null) {
                    String[] resultHeaders = new String[cleanHeaderMap.size() + 2];
                    int idx = 0;
                    for (String ch : cleanHeaderMap.values()) {
                        resultHeaders[idx++] = ch;
                    }
                    resultHeaders[idx] = "submitted";
                    resultHeaders[idx + 1] = "message";
                    csvPrinter = new CSVPrinter(resultCsvWriter,
                        CSVFormat.DEFAULT.builder()
                            .setHeader(resultHeaders)
                            .build());
                }

                try {
                    for (CSVRecord record : parser) {
                        if (rowCount != null) {
                            rowCount[0]++;
                        }
                        List<String> rowErrors = new ArrayList<>();
                        for (String header : headerMap.keySet()) {
                            String cleanHeader = cleanHeaderMap.get(header);
                            String value = record.isSet(header) ? record.get(header) : "";
                            BulkUploadRuleEngine.ColumnValidationResult colResult =
                                ruleEngine.validate(cleanHeader, value);
                            if (!colResult.isValid()) {
                                rowErrors.add(colResult.getMessage());
                                BulkUploadRuleEngine.ColumnValidationResult existing = results.stream()
                                    .filter(r -> r.getName().equals(cleanHeader))
                                    .findFirst()
                                    .orElse(null);
                                if (existing != null && existing.isValid()) {
                                    existing.setValid(false);
                                    existing.setMessage(colResult.getMessage() +
                                        " (row " + parser.getRecordNumber() + ")");
                                }
                            }
                        }

                        if (csvPrinter != null) {
                            List<Object> rowValues = new ArrayList<>();
                            for (String header : headerMap.keySet()) {
                                rowValues.add(record.isSet(header) ? record.get(header) : "");
                            }
                            if (rowErrors.isEmpty()) {
                                rowValues.add("true");
                                rowValues.add("");
                            } else {
                                rowValues.add("false");
                                rowValues.add(String.join("; ", rowErrors));
                            }
                            csvPrinter.printRecord(rowValues);
                        }
                    }

                    if (csvPrinter != null) {
                        csvPrinter.flush();
                    }
                } finally {
                    if (csvPrinter != null) {
                        csvPrinter.close();
                    }
                }
            }
        }

        return results;
    }

    @Override
    public boolean isValid() {
        return lastValidationValid;
    }

    private Map<String, Object> buildValidationResponse(
        List<BulkUploadRuleEngine.ColumnValidationResult> results) {
        Map<String, Object> response = new HashMap<>();
        boolean allValid = results.stream().allMatch(
            BulkUploadRuleEngine.ColumnValidationResult::isValid);
        response.put("success", allValid);

        List<Map<String, Object>> columns = new ArrayList<>();
        for (BulkUploadRuleEngine.ColumnValidationResult r : results) {
            Map<String, Object> col = new HashMap<>();
            col.put("name", r.getName());
            col.put("valid", r.isValid());
            col.put("message", r.getMessage() != null ? r.getMessage() : "");
            columns.add(col);
        }
        response.put("columns", columns);
        return response;
    }
}
