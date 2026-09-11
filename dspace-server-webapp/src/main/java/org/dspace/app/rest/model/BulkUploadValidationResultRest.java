/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import java.util.ArrayList;
import java.util.List;

public class BulkUploadValidationResultRest {

    private boolean success;
    private java.util.UUID processId;
    private List<ColumnValidation> columns = new ArrayList<>();

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public java.util.UUID getProcessId() { return processId; }
    public void setProcessId(java.util.UUID processId) { this.processId = processId; }

    public List<ColumnValidation> getColumns() { return columns; }
    public void setColumns(List<ColumnValidation> columns) { this.columns = columns; }

    public static class ColumnValidation {
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
