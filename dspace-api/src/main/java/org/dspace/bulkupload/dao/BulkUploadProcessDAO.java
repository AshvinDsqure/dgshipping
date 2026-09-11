/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bulkupload.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.dspace.bulkupload.BulkUploadProcess;
import org.dspace.bulkupload.BulkUploadProcessStatus;
import org.dspace.content.dao.DSpaceObjectLegacySupportDAO;
import org.dspace.core.Context;

public interface BulkUploadProcessDAO extends DSpaceObjectLegacySupportDAO<BulkUploadProcess> {

    BulkUploadProcess findByUuid(Context context, UUID uuid) throws SQLException;

    List<BulkUploadProcess> findAllByStatus(Context context, BulkUploadProcessStatus status) throws SQLException;

    List<BulkUploadProcess> findAll(Context context, int pageSize, int offset) throws SQLException;

    int countTotal(Context context) throws SQLException;

    List<BulkUploadProcess> findByEPerson(Context context, UUID epersonId) throws SQLException;

}
