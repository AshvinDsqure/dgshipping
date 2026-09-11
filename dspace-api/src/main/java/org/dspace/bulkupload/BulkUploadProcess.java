/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bulkupload;

import java.util.Date;
import java.util.UUID;


import org.dspace.content.DSpaceObject;
import org.dspace.content.DSpaceObjectLegacySupport;

import javax.persistence.*;

@Entity
@Table(name = "bulk_upload_process")
public class BulkUploadProcess extends DSpaceObject implements DSpaceObjectLegacySupport {

    private static final long serialVersionUID = 1L;

    @Column(name = "legacy_id", insertable = false, updatable = false)
    private Integer legacyId;

    @Column(name = "process_name", nullable = false)
    private String processName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BulkUploadProcessStatus status;

    @Column(name = "total_rows")
    private int totalRows;

    @Column(name = "processed_rows")
    private int processedRows;

    @Column(name = "progress")
    private int progress;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "csv_file_path", length = 1000)
    private String csvFilePath;

    @Column(name = "validation_result_json", columnDefinition = "TEXT")
    private String validationResultJson;

    @Column(name = "result_csv", columnDefinition = "TEXT")
    private String resultCsv;

    @Column(name = "eperson_uuid")
    private UUID epersonId;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    public BulkUploadProcess() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.status = BulkUploadProcessStatus.PENDING;
        this.processName = "Bulk Upload";
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getName() {
        return processName;
    }

    @Override
    public Integer getLegacyId() {
        return this.legacyId;
    }

    public void setLegacyId(Integer legacyId) {
        this.legacyId = legacyId;
    }

    public String getProcessName() { return processName; }
    public void setProcessName(String processName) { this.processName = processName; }

    public BulkUploadProcessStatus getStatus() { return status; }
    public void setStatus(BulkUploadProcessStatus status) {
        this.status = status;
        this.updatedAt = new Date();
    }

    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }

    public int getProcessedRows() { return processedRows; }
    public void setProcessedRows(int processedRows) { this.processedRows = processedRows; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCsvFilePath() { return csvFilePath; }
    public void setCsvFilePath(String csvFilePath) { this.csvFilePath = csvFilePath; }

    public String getValidationResultJson() { return validationResultJson; }
    public void setValidationResultJson(String validationResultJson) {
        this.validationResultJson = validationResultJson;
    }

    public String getResultCsv() { return resultCsv; }
    public void setResultCsv(String resultCsv) { this.resultCsv = resultCsv; }

    public UUID getEpersonId() { return epersonId; }
    public void setEpersonId(UUID epersonId) { this.epersonId = epersonId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}
