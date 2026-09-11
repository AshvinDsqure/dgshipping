/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.dspace.app.rest.model.BulkUploadProcessRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.bulkupload.BulkUploadProcess;
import org.springframework.stereotype.Component;

@Component
public class BulkUploadProcessConverter extends DSpaceObjectConverter<BulkUploadProcess, BulkUploadProcessRest> {

    @Override
    public BulkUploadProcessRest convert(BulkUploadProcess obj, Projection projection) {
        BulkUploadProcessRest rest = super.convert(obj, projection);
        rest.setProcessName(obj.getProcessName());
        rest.setStatus(obj.getStatus() != null ? obj.getStatus().name() : null);
        rest.setProgress(obj.getProgress());
        rest.setProcessedRows(obj.getProcessedRows());
        rest.setTotalRows(obj.getTotalRows());
        rest.setMessage(obj.getMessage());
        rest.setValidationResultJson(obj.getValidationResultJson());
        rest.setCreatedAt(obj.getCreatedAt());
        rest.setUpdatedAt(obj.getUpdatedAt());
        return rest;
    }

    @Override
    public Class<BulkUploadProcess> getModelClass() {
        return BulkUploadProcess.class;
    }

    @Override
    protected BulkUploadProcessRest newInstance() {
        return new BulkUploadProcessRest();
    }
}
