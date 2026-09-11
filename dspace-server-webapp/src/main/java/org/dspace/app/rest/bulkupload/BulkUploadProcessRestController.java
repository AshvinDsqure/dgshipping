/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.bulkupload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.converter.DSpaceConverter;
import org.dspace.app.rest.model.BulkUploadProcessRest;
import org.dspace.app.rest.model.BulkUploadValidationResultRest;
import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.bulkupload.BulkUploadProcess;
import org.dspace.bulkupload.service.BulkUploadRuleEngine;
import org.dspace.bulkupload.service.BulkUploadService;
import org.dspace.bulkupload.service.BulkUploadValidationService;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.services.ConfigurationService;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/core/bulk-upload")
public class BulkUploadProcessRestController implements InitializingBean {

    private static final Logger log = LogManager.getLogger();

    @Autowired
    private org.dspace.app.rest.DiscoverableEndpointsService discoverableEndpointsService;

    @Autowired
    private DSpaceConverter<BulkUploadProcess, BulkUploadProcessRest> bulkUploadProcessConverter;

    @Autowired
    private BulkUploadRuleEngine bulkUploadRuleEngine;

    @Override
    public void afterPropertiesSet() throws Exception {
        discoverableEndpointsService.register(this,
            Arrays.asList(
                Link.of("/api/core/bulk-upload/validate", "bulk-upload-validate"),
                Link.of("/api/core/bulk-upload/process/{uuid}", "bulk-upload-process-status"),
                Link.of("/api/core/bulk-upload/process/{uuid}/result", "bulk-upload-process-result"),
                Link.of("/api/core/bulk-upload/sample-csv", "bulk-upload-sample-csv"),
                Link.of("/api/core/bulk-upload/rules", "bulk-upload-rules")
            ));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/validate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BulkUploadValidationResultRest> validate(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Context context = ContextUtil.obtainContext(request);
        EPerson eperson = context.getCurrentUser();

        try {
            byte[] fileBytes = file.getBytes();

            BulkUploadValidationService validationService = DSpaceServicesFactory.getInstance()
                .getServiceManager()
                .getServiceByName(BulkUploadValidationService.class.getName(),
                    BulkUploadValidationService.class);

            BulkUploadProcess process = validationService.validate(context,
                new ByteArrayInputStream(fileBytes), eperson != null ? eperson.getID() : null);
            context.commit();

            BulkUploadValidationResultRest resultRest = buildValidationResultRest(
                process.getValidationResultJson());

            if (resultRest.isSuccess()) {
                BulkUploadService uploadService = DSpaceServicesFactory.getInstance()
                    .getServiceManager()
                    .getServiceByName(BulkUploadService.class.getName(),
                        BulkUploadService.class);

                uploadService.startUpload(context, process.getID(),
                    new ByteArrayInputStream(fileBytes), eperson != null ? eperson.getID() : null);
                context.commit();

                resultRest.setProcessId(process.getID());
            }

            return ResponseEntity.ok(resultRest);
        } catch (IOException | SQLException | org.dspace.authorize.AuthorizeException e) {
            log.error("Bulk upload validation failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            try {
                context.complete();
            } catch (Exception e) {
                log.warn("Failed to complete context: {}", e.getMessage());
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/process/{uuid}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BulkUploadProcessRest> getProcessStatus(
            HttpServletRequest request,
            @PathVariable UUID uuid) {

        Context context = ContextUtil.obtainContext(request);

        try {
            BulkUploadService uploadService = DSpaceServicesFactory.getInstance()
                .getServiceManager()
                .getServiceByName(BulkUploadService.class.getName(),
                    BulkUploadService.class);

            BulkUploadProcess process = uploadService.getProcessStatus(context, uuid);
            if (process == null) {
                return ResponseEntity.notFound().build();
            }

            BulkUploadProcessRest rest = bulkUploadProcessConverter.convert(process, null);
            return ResponseEntity.ok(rest);
        } catch (SQLException e) {
            log.error("Failed to get bulk upload process status: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            try {
                context.complete();
            } catch (Exception e) {
                log.warn("Failed to complete context: {}", e.getMessage());
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/process/{uuid}/result")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<byte[]> downloadResultCsv(
            HttpServletRequest request,
            @PathVariable UUID uuid) {

        Context context = ContextUtil.obtainContext(request);

        try {
            BulkUploadService uploadService = DSpaceServicesFactory.getInstance()
                .getServiceManager()
                .getServiceByName(BulkUploadService.class.getName(),
                    BulkUploadService.class);

            BulkUploadProcess process = uploadService.getProcessStatus(context, uuid);
            if (process == null) {
                return ResponseEntity.notFound().build();
            }

            String resultCsv = process.getResultCsv();
            if (resultCsv == null || resultCsv.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            byte[] csvBytes = resultCsv.getBytes(StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=\"bulk-upload-result-" + uuid + ".csv\"")
                .body(csvBytes);
        } catch (SQLException e) {
            log.error("Failed to get bulk upload result CSV: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            try {
                context.complete();
            } catch (Exception e) {
                log.warn("Failed to complete context: {}", e.getMessage());
            }
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/rules")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> uploadRulesJson(
            HttpServletRequest request,
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file provided");
        }

        try {
            byte[] fileBytes = file.getBytes();

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.readTree(fileBytes);

            ConfigurationService configService = DSpaceServicesFactory.getInstance().getConfigurationService();
            String rulesPath = configService.getProperty("bulkupload.rules.path");

            if (rulesPath == null || rulesPath.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    "bulkupload.rules.path is not configured. Set it in local.cfg or dspace.cfg to enable rules upload.");
            }

            Path targetPath = Paths.get(rulesPath.trim());
            if (targetPath.getParent() != null && !Files.exists(targetPath.getParent())) {
                Files.createDirectories(targetPath.getParent());
            }
            Files.write(targetPath, fileBytes);

            bulkUploadRuleEngine.loadRules(new ByteArrayInputStream(fileBytes));

            log.info("Bulk upload rules updated and reloaded from uploaded file");
            return ResponseEntity.ok("Rules uploaded and reloaded successfully");
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Invalid JSON in uploaded rules file: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid JSON: " + e.getMessage());
        } catch (IOException e) {
            log.error("Failed to save uploaded rules file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to save rules: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to reload rules: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to reload rules: " + e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/sample-csv")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<byte[]> downloadSampleCsv(HttpServletRequest request) {
        try {
            List<BulkUploadRuleEngine.ColumnRule> rules = bulkUploadRuleEngine.getRules();
            if (rules == null || rules.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            String csvHeader = rules.stream()
                .map(BulkUploadRuleEngine.ColumnRule::getName)
                .collect(Collectors.joining(","));

            byte[] csvBytes = csvHeader.getBytes(StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=\"bulk-upload-sample.csv\"")
                .body(csvBytes);
        } catch (Exception e) {
            log.error("Failed to generate sample CSV: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/rules")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<byte[]> downloadRulesJson(HttpServletRequest request) {
        try {
            List<BulkUploadRuleEngine.ColumnRule> rules = bulkUploadRuleEngine.getRules();
            if (rules == null || rules.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.node.ObjectNode root = mapper.createObjectNode();
            com.fasterxml.jackson.databind.node.ArrayNode columnsNode = mapper.createArrayNode();

            for (BulkUploadRuleEngine.ColumnRule rule : rules) {
                com.fasterxml.jackson.databind.node.ObjectNode colNode = mapper.createObjectNode();
                colNode.put("name", rule.getName());
                if (rule.getMetadata() != null) {
                    colNode.put("metadata", rule.getMetadata());
                }
                colNode.put("required", rule.isRequired());
                if (rule.getMaxLength() >= 0) {
                    colNode.put("maxLength", rule.getMaxLength());
                }
                if (rule.getRegex() != null) {
                    colNode.put("regex", rule.getRegex());
                }
                if (rule.getDateFormat() != null) {
                    colNode.put("dateFormat", rule.getDateFormat());
                }
                if (rule.getAllowedValues() != null && !rule.getAllowedValues().isEmpty()) {
                    com.fasterxml.jackson.databind.node.ArrayNode allowedNode = mapper.createArrayNode();
                    for (String val : rule.getAllowedValues()) {
                        allowedNode.add(val);
                    }
                    colNode.set("allowedValues", allowedNode);
                }
                if (rule.isCollection()) {
                    colNode.put("isCollection", true);
                }
                if (rule.isFile()) {
                    colNode.put("isFile", true);
                }
                if (rule.hasMultiple()) {
                    colNode.put("hasMultiple", true);
                    colNode.put("separator", rule.getSeparator());
                }
                columnsNode.add(colNode);
            }
            root.set("columns", columnsNode);

            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
            byte[] jsonBytes = json.getBytes(StandardCharsets.UTF_8);

            return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .header("Content-Disposition", "attachment; filename=\"bulkupload-rules.json\"")
                .body(jsonBytes);
        } catch (Exception e) {
            log.error("Failed to generate rules JSON: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private BulkUploadValidationResultRest buildValidationResultRest(String json) {
        BulkUploadValidationResultRest result = new BulkUploadValidationResultRest();
        if (json == null || json.isBlank()) {
            result.setSuccess(false);
            return result;
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(json);
            result.setSuccess(root.get("success").asBoolean());

            com.fasterxml.jackson.databind.JsonNode columnsNode = root.get("columns");
            if (columnsNode != null && columnsNode.isArray()) {
                List<BulkUploadValidationResultRest.ColumnValidation> columns = new java.util.ArrayList<>();
                for (com.fasterxml.jackson.databind.JsonNode colNode : columnsNode) {
                    BulkUploadValidationResultRest.ColumnValidation col =
                        new BulkUploadValidationResultRest.ColumnValidation();
                    col.setName(colNode.get("name").asText());
                    col.setValid(colNode.get("valid").asBoolean());
                    col.setMessage(colNode.has("message") ? colNode.get("message").asText() : "");
                    columns.add(col);
                }
                result.setColumns(columns);
            }
        } catch (Exception e) {
            log.error("Failed to parse validation result JSON: {}", e.getMessage());
            result.setSuccess(false);
        }

        return result;
    }
}
