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

import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.bulkupload.BulkUploadProcess;
import org.dspace.bulkupload.BulkUploadProcessStatus;
import org.dspace.bulkupload.dao.BulkUploadProcessDAO;
import org.dspace.content.DSpaceObjectServiceImpl;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.springframework.beans.factory.annotation.Autowired;

public class BulkUploadProcessServiceImpl extends DSpaceObjectServiceImpl<BulkUploadProcess>
    implements BulkUploadProcessService {

    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(BulkUploadProcessServiceImpl.class);

    @Autowired(required = true)
    protected BulkUploadProcessDAO bulkUploadProcessDAO;

    protected BulkUploadProcessServiceImpl() {
        super();
    }

    @Override
    public BulkUploadProcess findByIdOrLegacyId(Context context, String id) throws SQLException {
        return null;
    }

    @Override
    public BulkUploadProcess findByLegacyId(Context context, int id) throws SQLException {
        return null;
    }

    @Override
    public void updateLastModified(Context context, BulkUploadProcess dso) throws SQLException, AuthorizeException {
        update(context, dso);
        context.addEvent(new Event(Event.MODIFY, Constants.ITEM, dso.getID(), null, getIdentifiers(context, dso)));
    }

    @Override
    public void delete(Context context, BulkUploadProcess dso) throws SQLException, AuthorizeException, IOException {
        bulkUploadProcessDAO.delete(context, dso);
    }

    @Override
    public int getSupportsTypeConstant() {
        return 15;
    }

    @Override
    public BulkUploadProcess create(Context context, BulkUploadProcess process) throws SQLException, AuthorizeException {
        process = bulkUploadProcessDAO.create(context, process);
        return process;
    }

    @Override
    public BulkUploadProcess find(Context context, UUID uuid) throws SQLException {
        return bulkUploadProcessDAO.findByID(context, BulkUploadProcess.class, uuid);
    }

    @Override
    public List<BulkUploadProcess> findAllByStatus(Context context, BulkUploadProcessStatus status)
        throws SQLException {
        return bulkUploadProcessDAO.findAllByStatus(context, status);
    }

    @Override
    public List<BulkUploadProcess> findByEPerson(Context context, UUID epersonId) throws SQLException {
        return bulkUploadProcessDAO.findByEPerson(context, epersonId);
    }

    @Override
    public void update(Context context, BulkUploadProcess process) throws SQLException, AuthorizeException {
        bulkUploadProcessDAO.save(context, process);
    }

    @Override
    public void updateStatus(Context context, UUID uuid, BulkUploadProcessStatus status) throws SQLException {
        BulkUploadProcess process = bulkUploadProcessDAO.findByUuid(context, uuid);
        if (process != null) {
            process.setStatus(status);
            bulkUploadProcessDAO.save(context, process);
        }
    }

    @Override
    public void updateProgress(Context context, UUID uuid, int processedRows, int totalRows) throws SQLException {
        BulkUploadProcess process = bulkUploadProcessDAO.findByUuid(context, uuid);
        if (process != null) {
            process.setProcessedRows(processedRows);
            process.setTotalRows(totalRows);
            if (totalRows > 0) {
                process.setProgress((processedRows * 100) / totalRows);
            }
            bulkUploadProcessDAO.save(context, process);
        }
    }

    @Override
    public List<BulkUploadProcess> findAll(Context context, int pageSize, int offset) throws SQLException {
        return bulkUploadProcessDAO.findAll(context, pageSize, offset);
    }

    @Override
    public int countTotal(Context context) throws SQLException {
        return bulkUploadProcessDAO.countTotal(context);
    }
}
