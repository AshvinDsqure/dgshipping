/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bulkupload.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dspace.content.Collection;
import org.dspace.content.DSpaceObject;
import org.dspace.content.service.CollectionService;
import org.dspace.core.Context;
import org.dspace.handle.service.HandleService;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BulkUploadRuleEngine {

    private static final Logger log = LogManager.getLogger(BulkUploadRuleEngine.class);

    private static final ObjectMapper mapper = new ObjectMapper();

    private List<ColumnRule> rules = new ArrayList<>();

    private Map<String, ColumnRule> ruleMap = new HashMap<>();

    @Autowired
    private HandleService handleService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private ConfigurationService configurationService;

    public void init() {
        String configPath = configurationService.getProperty("bulkupload.rules.path");
        if (configPath != null && !configPath.trim().isEmpty()) {
            try (InputStream is = new FileInputStream(configPath.trim())) {
                if (is != null) {
                    loadRules(is);
                    log.info("Loaded bulkupload-rules.json from configured path: {}", configPath);
                    return;
                }
            } catch (IOException e) {
                log.error("Failed to load bulkupload-rules.json from configured path '{}': {}", configPath, e.getMessage(), e);
            }
        }
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("bulkupload-rules.json")) {
            if (is != null) {
                loadRules(is);
            } else {
                log.warn("bulkupload-rules.json not found on classpath, no validation rules loaded");
            }
        } catch (IOException e) {
            log.error("Failed to load bulkupload-rules.json: {}", e.getMessage(), e);
        }
    }

    public void loadRules(InputStream inputStream) throws IOException {
        JsonNode root = mapper.readTree(inputStream);
        JsonNode columnsNode = root.get("columns");
        if (columnsNode != null && columnsNode.isArray()) {
            for (JsonNode colNode : columnsNode) {
                ColumnRule rule = new ColumnRule();
                rule.setName(colNode.get("name").asText());
                rule.setRequired(colNode.has("required") && colNode.get("required").asBoolean());
                rule.setDateFormat(colNode.has("dateFormat") ? colNode.get("dateFormat").asText() : null);
                rule.setRegex(colNode.has("regex") ? colNode.get("regex").asText() : null);
                rule.setMaxLength(colNode.has("maxLength") ? colNode.get("maxLength").asInt() : -1);
                rule.setMetadata(colNode.has("metadata") ? colNode.get("metadata").asText() : null);
                rule.setCollection(colNode.has("isCollection") && colNode.get("isCollection").asBoolean());
                rule.setFile(colNode.has("isFile") && colNode.get("isFile").asBoolean());
                rule.setMultiple(colNode.has("hasMultiple") && colNode.get("hasMultiple").asBoolean());
                rule.setSeparator(colNode.has("separator") ? colNode.get("separator").asText() : "||");
                if (colNode.has("aliases") && colNode.get("aliases").isArray()) {
                    List<String> aliases = new ArrayList<>();
                    for (JsonNode alias : colNode.get("aliases")) {
                        aliases.add(alias.asText());
                    }
                    rule.setAliases(aliases);
                }
                if (colNode.has("allowedValues") && colNode.get("allowedValues").isArray()) {
                    List<String> allowed = new ArrayList<>();
                    for (JsonNode val : colNode.get("allowedValues")) {
                        allowed.add(val.asText());
                    }
                    rule.setAllowedValues(allowed);
                }
                rules.add(rule);
                ruleMap.put(rule.getName(), rule);
                if (rule.getAliases() != null) {
                    for (String alias : rule.getAliases()) {
                        ruleMap.put(alias, rule);
                    }
                }
            }
        }
        log.info("Loaded {} bulk upload validation rules", rules.size());
    }

    public List<ColumnRule> getRules() {
        return rules;
    }

    public ColumnRule getRule(String columnName) {
        ColumnRule rule = ruleMap.get(columnName);
        if (rule != null) {
            return rule;
        }
        if (columnName != null) {
            String lowerCol = columnName.toLowerCase();
            for (ColumnRule r : rules) {
                if (r.isFile() && lowerCol.startsWith(r.getName().toLowerCase())) {
                    return r;
                }
            }
        }
        return null;
    }

    public ColumnValidationResult validate(String columnName, String value) {
        ColumnValidationResult result = new ColumnValidationResult();
        result.setName(columnName);

        ColumnRule rule = getRule(columnName);
        if (rule == null) {
            result.setValid(true);
            result.setMessage("");
            return result;
        }

        if (rule.isRequired() && (value == null || value.trim().isEmpty())) {
            result.setValid(false);
            result.setMessage("Column '" + columnName + "' is required but value is empty");
            return result;
        }

        if (value == null || value.trim().isEmpty()) {
            result.setValid(true);
            result.setMessage("");
            return result;
        }

        if (rule.getMaxLength() > 0 && value.length() > rule.getMaxLength()) {
            result.setValid(false);
            result.setMessage("Value exceeds max length of " + rule.getMaxLength());
            return result;
        }

        if (rule.hasMultiple()) {
            String separator = rule.getSeparator();
            String[] values = value.split(Pattern.quote(separator));
            for (String v : values) {
                String trimmed = v.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                ColumnValidationResult subResult = validateSingle(rule, trimmed);
                if (!subResult.isValid()) {
                    result.setValid(false);
                    result.setMessage(subResult.getMessage());
                    return result;
                }
            }
            result.setValid(true);
            result.setMessage("");
            return result;
        }

        return validateSingle(rule, value);
    }

    private ColumnValidationResult validateSingle(ColumnRule rule, String value) {
        ColumnValidationResult result = new ColumnValidationResult();
        result.setName(rule.getName());

        if (rule.getMaxLength() > 0 && value.length() > rule.getMaxLength()) {
            result.setValid(false);
            result.setMessage("Value exceeds max length of " + rule.getMaxLength());
            return result;
        }

        if (rule.getRegex() != null && !rule.getRegex().isBlank()) {
            if (!Pattern.matches(rule.getRegex(), value)) {
                result.setValid(false);
                result.setMessage("Value does not match required pattern: " + rule.getRegex());
                return result;
            }
        }

        if (rule.getAllowedValues() != null && !rule.getAllowedValues().isEmpty()) {
            if (!rule.getAllowedValues().contains(value)) {
                result.setValid(false);
                result.setMessage("Value '" + value + "' is not in allowed values: " + rule.getAllowedValues());
                return result;
            }
        }

        if (rule.isCollection()) {
            if (value == null || value.trim().isEmpty()) {
                result.setValid(false);
                result.setMessage("Collection handle is required");
                return result;
            }
            if (!isValidCollection(value)) {
                result.setValid(false);
                result.setMessage("Collection not found: '" + value + "'");
                return result;
            }
        }

        if (rule.isFile()) {
            if (value != null && !value.trim().isEmpty()) {
                if (rule.hasMultiple()) {
                    String separator = rule.getSeparator();
                    String[] filePaths = value.split(Pattern.quote(separator));
                    for (String fp : filePaths) {
                        String trimmed = fp.trim();
                        if (trimmed.isEmpty()) {
                            continue;
                        }
                        if (!isValidFile(trimmed)) {
                            result.setValid(false);
                            result.setMessage("File '" + trimmed + "' does not exist");
                            return result;
                        }
                    }
                } else {
                    if (!isValidFile(value)) {
                        result.setValid(false);
                        result.setMessage("Value '" + value + "' is not a valid file path or file does not exist");
                        return result;
                    }
                }
            }
        }

        if (rule.getDateFormat() != null && !rule.getDateFormat().isBlank()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(rule.getDateFormat());
                LocalDate.parse(value, formatter);
            } catch (DateTimeParseException e) {
                result.setValid(false);
                result.setMessage("Value '" + value + "' does not match date format: " + rule.getDateFormat());
                return result;
            }
        }

        result.setValid(true);
        result.setMessage("");
        return result;
    }

    private boolean isValidFile(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        String basePath = configurationService.getProperty("bulkupload.file.basepath", "");
        String filePath = value.trim();
        if (basePath != null && !basePath.trim().isEmpty()) {
            filePath = basePath.trim() + File.separator + filePath;
        }
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    private boolean isValidCollection(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Context context = new Context();
            try {
                DSpaceObject dso = handleService.resolveToObject(context, value);
                if (dso instanceof Collection) {
                    return true;
                }
                List<Collection> collections = collectionService.findAll(context);
                for (Collection col : collections) {
                    if (col.getName() != null && col.getName().trim().equalsIgnoreCase(value.trim())) {
                        return true;
                    }
                }
                return false;
            } finally {
                context.complete();
            }
        } catch (Exception e) {
            log.warn("Failed to validate collection '{}': {}", value, e.getMessage());
            return false;
        }
    }

    public static class ColumnRule {
        private String name;
        private boolean required;
        private String dateFormat;
        private String regex;
        private int maxLength;
        private boolean isCollection;
        private boolean isFile;
        private boolean hasMultiple;
        private String separator = "||";
        private String metadata;
        private List<String> allowedValues;
        private List<String> aliases;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public String getDateFormat() { return dateFormat; }
        public void setDateFormat(String dateFormat) { this.dateFormat = dateFormat; }
        public String getRegex() { return regex; }
        public void setRegex(String regex) { this.regex = regex; }
        public int getMaxLength() { return maxLength; }
        public void setMaxLength(int maxLength) { this.maxLength = maxLength; }
        public boolean isCollection() { return isCollection; }
        public void setCollection(boolean isCollection) { this.isCollection = isCollection; }
        public boolean isFile() { return isFile; }
        public void setFile(boolean isFile) { this.isFile = isFile; }
        public boolean hasMultiple() { return hasMultiple; }
        public void setMultiple(boolean hasMultiple) { this.hasMultiple = hasMultiple; }
        public String getSeparator() { return separator; }
        public void setSeparator(String separator) { this.separator = separator; }
        public String getMetadata() { return metadata; }
        public void setMetadata(String metadata) { this.metadata = metadata; }
        public List<String> getAllowedValues() { return allowedValues; }
        public void setAllowedValues(List<String> allowedValues) { this.allowedValues = allowedValues; }
        public List<String> getAliases() { return aliases; }
        public void setAliases(List<String> aliases) { this.aliases = aliases; }
    }

    public static class ColumnValidationResult {
        private String name;
        private boolean valid;
        private String message;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
