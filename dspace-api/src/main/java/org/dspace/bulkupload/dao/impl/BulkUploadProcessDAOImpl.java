/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.bulkupload.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.dspace.bulkupload.BulkUploadProcess;
import org.dspace.bulkupload.BulkUploadProcessStatus;
import org.dspace.bulkupload.dao.BulkUploadProcessDAO;
import org.dspace.core.AbstractHibernateDAO;
import org.dspace.core.Context;

import javax.persistence.criteria.*;

public class BulkUploadProcessDAOImpl extends AbstractHibernateDAO<BulkUploadProcess>
    implements BulkUploadProcessDAO {

    @Override
    public BulkUploadProcess findByLegacyId(Context context, int legacyId, Class<BulkUploadProcess> clazz)
        throws SQLException {
        return null;
    }

    @Override
    public BulkUploadProcess findByUuid(Context context, UUID uuid) throws SQLException {
        return findByID(context, BulkUploadProcess.class, uuid);
    }

    @Override
    public List<BulkUploadProcess> findAllByStatus(Context context, BulkUploadProcessStatus status)
        throws SQLException {
        CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
        CriteriaQuery<BulkUploadProcess> criteriaQuery = getCriteriaQuery(criteriaBuilder,
            BulkUploadProcess.class);
        Root<BulkUploadProcess> root = criteriaQuery.from(BulkUploadProcess.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("status"), status));
        return list(context, criteriaQuery, false, BulkUploadProcess.class, -1, -1);
    }

    @Override
    public List<BulkUploadProcess> findByEPerson(Context context, UUID epersonId) throws SQLException {
        CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
        CriteriaQuery<BulkUploadProcess> criteriaQuery = getCriteriaQuery(criteriaBuilder,
            BulkUploadProcess.class);
        Root<BulkUploadProcess> root = criteriaQuery.from(BulkUploadProcess.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("epersonId"), epersonId));
        return list(context, criteriaQuery, false, BulkUploadProcess.class, -1, -1);
    }

    @Override
    public List<BulkUploadProcess> findAll(Context context, int pageSize, int offset) throws SQLException {
        CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
        CriteriaQuery<BulkUploadProcess> criteriaQuery = getCriteriaQuery(criteriaBuilder,
            BulkUploadProcess.class);
        Root<BulkUploadProcess> root = criteriaQuery.from(BulkUploadProcess.class);
        criteriaQuery.select(root);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));
        return list(context, criteriaQuery, false, BulkUploadProcess.class, pageSize, offset);
    }

    @Override
    public int countTotal(Context context) throws SQLException {
        CriteriaBuilder criteriaBuilder = getCriteriaBuilder(context);
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<BulkUploadProcess> root = criteriaQuery.from(BulkUploadProcess.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        return Math.toIntExact(countLong(context, criteriaQuery, criteriaBuilder, root));
    }
}
