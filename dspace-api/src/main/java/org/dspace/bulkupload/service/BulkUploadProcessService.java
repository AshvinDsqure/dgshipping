/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bulkupload.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.dspace.authorize.AuthorizeException;
import org.dspace.bulkupload.BulkUploadProcess;
import org.dspace.bulkupload.BulkUploadProcessStatus;
import org.dspace.content.service.DSpaceObjectLegacySupportService;
import org.dspace.content.service.DSpaceObjectService;
import org.dspace.core.Context;

public interface BulkUploadProcessService
    extends DSpaceObjectService<BulkUploadProcess>, DSpaceObjectLegacySupportService<BulkUploadProcess> {

    BulkUploadProcess create(Context context, BulkUploadProcess process) throws SQLException, AuthorizeException;

    BulkUploadProcess find(Context context, UUID uuid) throws SQLException;

    List<BulkUploadProcess> findAllByStatus(Context context, BulkUploadProcessStatus status) throws SQLException;

    List<BulkUploadProcess> findByEPerson(Context context, UUID epersonId) throws SQLException;

    void update(Context context, BulkUploadProcess process) throws SQLException, AuthorizeException;

    void updateStatus(Context context, UUID uuid, BulkUploadProcessStatus status) throws SQLException;

    void updateProgress(Context context, UUID uuid, int processedRows, int totalRows) throws SQLException;

    void delete(Context context, BulkUploadProcess process) throws SQLException, AuthorizeException, IOException;

    List<BulkUploadProcess> findAll(Context context, int pageSize, int offset) throws SQLException;

    int countTotal(Context context) throws SQLException;

}
