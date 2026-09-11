/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.Parameter;
import org.dspace.app.rest.SearchRestMethod;
import org.dspace.app.rest.converter.BulkUploadProcessConverter;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.BulkUploadProcessRest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.bulkupload.BulkUploadProcess;
import org.dspace.bulkupload.BulkUploadProcessStatus;
import org.dspace.bulkupload.service.BulkUploadProcessService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component(BulkUploadProcessRest.CATEGORY + "." + BulkUploadProcessRest.NAME)
public class BulkUploadProcessRestRepository extends DSpaceObjectRestRepository<BulkUploadProcess, BulkUploadProcessRest> {


    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(
        BulkUploadProcessRestRepository.class);

    @Autowired
    BulkUploadProcessService bulkUploadProcessService;

    @Autowired
    BulkUploadProcessConverter bulkUploadProcessConverter;

    public BulkUploadProcessRestRepository(BulkUploadProcessService dsoService) {
        super(dsoService);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    protected BulkUploadProcessRest createAndReturn(Context context)
        throws AuthorizeException {
        HttpServletRequest req = getRequestService().getCurrentRequest().getHttpServletRequest();
        ObjectMapper mapper = new ObjectMapper();
        BulkUploadProcessRest rest = null;
        BulkUploadProcess process = null;
        try {
            rest = mapper.readValue(req.getInputStream(), BulkUploadProcessRest.class);
            process = new BulkUploadProcess();
            process.setProcessName(rest.getProcessName());
            process.setStatus(BulkUploadProcessStatus.PENDING);
            if (context.getCurrentUser() != null) {
                process.setEpersonId(context.getCurrentUser().getID());
            }
            bulkUploadProcessService.create(context, process);
        } catch (Exception e) {
            log.error("Error creating bulk upload process: {}", e.getMessage(), e);
            throw new UnprocessableEntityException(
                "error parsing the body... maybe this is not the right error code");
        }
        return bulkUploadProcessConverter.convert(process, utils.obtainProjection());
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    protected BulkUploadProcessRest put(Context context, HttpServletRequest request, String apiCategory,
                                        String model, UUID id, JsonNode jsonNode) {
        ObjectMapper mapper = new ObjectMapper();
        BulkUploadProcessRest rest = null;
        try {
            rest = mapper.readValue(jsonNode.toString(), BulkUploadProcessRest.class);
            BulkUploadProcess process = bulkUploadProcessService.find(context, id);
            if (process == null) {
                throw new ResourceNotFoundException("BulkUploadProcess with id: " + id + " not found");
            }
            if (rest.getProcessName() != null) {
                process.setProcessName(rest.getProcessName());
            }
            if (rest.getStatus() != null) {
                process.setStatus(BulkUploadProcessStatus.valueOf(rest.getStatus()));
            }
            if (rest.getMessage() != null) {
                process.setMessage(rest.getMessage());
            }
            if (rest.getProgress() != 0) {
                process.setProgress(rest.getProgress());
            }
            if (rest.getProcessedRows() != 0) {
                process.setProcessedRows(rest.getProcessedRows());
            }
            if (rest.getTotalRows() != 0) {
                process.setTotalRows(rest.getTotalRows());
            }
            bulkUploadProcessService.update(context, process);
            rest = bulkUploadProcessConverter.convert(process, utils.obtainProjection());
            context.commit();
        } catch (Exception e) {
            log.error("Error updating bulk upload process: {}", e.getMessage(), e);
        }
        return rest;
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public BulkUploadProcessRest findOne(Context context, UUID uuid) {
        BulkUploadProcessRest rest = null;
        try {
            BulkUploadProcess process = bulkUploadProcessService.find(context, uuid);
            if (process != null) {
                rest = bulkUploadProcessConverter.convert(process, utils.obtainProjection());
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return rest;
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<BulkUploadProcessRest> findAll(Context context, Pageable pageable) {
        int total = 0;
        List<BulkUploadProcess> processes = null;
        List<BulkUploadProcessRest> rests = null;
        try {
            total = bulkUploadProcessService.countTotal(context);
            processes = bulkUploadProcessService.findAll(context,
                Math.toIntExact(pageable.getPageSize()), Math.toIntExact(pageable.getOffset()));
            if (processes != null) {
                rests = processes.stream().map(d -> {
                    return bulkUploadProcessConverter.convert(d, utils.obtainProjection());
                }).collect(Collectors.toList());
            }
            return new PageImpl<>(rests, pageable, total);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    protected void delete(Context context, UUID id) throws AuthorizeException {
        BulkUploadProcess process = null;
        try {
            process = bulkUploadProcessService.find(context, id);
            if (process == null) {
                throw new ResourceNotFoundException(BulkUploadProcessRest.CATEGORY + "." +
                    BulkUploadProcessRest.NAME + " with id: " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        try {
            bulkUploadProcessService.delete(context, process);
            context.commit();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @SearchRestMethod(name = "findByStatus")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<BulkUploadProcessRest> findByStatus(
        @Parameter(value = "status", required = true) String status, Pageable pageable) {
        try {
            Context context = obtainContext();
            BulkUploadProcessStatus processStatus = BulkUploadProcessStatus.valueOf(status.toUpperCase());
            List<BulkUploadProcess> processes = bulkUploadProcessService.findAllByStatus(context, processStatus);
            List<BulkUploadProcessRest> rests = processes.stream().map(d -> {
                return bulkUploadProcessConverter.convert(d, utils.obtainProjection());
            }).collect(Collectors.toList());
            return new PageImpl<>(rests, pageable, rests.size());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @SearchRestMethod(name = "findByEPerson")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Page<BulkUploadProcessRest> findByEPerson(
        @Parameter(value = "eperson", required = true) UUID epersonId, Pageable pageable) {
        try {
            Context context = obtainContext();
            List<BulkUploadProcess> processes = bulkUploadProcessService.findByEPerson(context, epersonId);
            List<BulkUploadProcessRest> rests = processes.stream().map(d -> {
                return bulkUploadProcessConverter.convert(d, utils.obtainProjection());
            }).collect(Collectors.toList());
            return new PageImpl<>(rests, pageable, rests.size());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Class<BulkUploadProcessRest> getDomainClass() {
        return BulkUploadProcessRest.class;
    }
}
