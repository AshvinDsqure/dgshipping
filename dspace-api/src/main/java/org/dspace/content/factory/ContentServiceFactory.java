/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.factory;

import java.util.List;

import org.dspace.content.DSpaceObject;
import org.dspace.content.InProgressSubmission;
import org.dspace.content.RelationshipMetadataService;
import org.dspace.content.WorkspaceItem;
import org.dspace.content.service.*;
import org.dspace.services.factory.DSpaceServicesFactory;
import org.dspace.workflow.factory.WorkflowServiceFactory;

/**
 * Abstract factory to get services for the content package, use ContentServiceFactory.getInstance() to retrieve an
 * implementation
 *
 * @author kevinvandevelde at atmire.com
 */
public abstract class ContentServiceFactory {

    public abstract List<DSpaceObjectService<? extends DSpaceObject>> getDSpaceObjectServices();

    public abstract List<DSpaceObjectLegacySupportService<? extends DSpaceObject>>
        getDSpaceObjectLegacySupportServices();

    public abstract BitstreamFormatService getBitstreamFormatService();

    public abstract BitstreamService getBitstreamService();

    public abstract BundleService getBundleService();

    public abstract CollectionService getCollectionService();

    public abstract CommunityService getCommunityService();

    public abstract ItemService getItemService();

    public abstract MetadataFieldService getMetadataFieldService();

    public abstract MetadataSchemaService getMetadataSchemaService();

    public abstract MetadataValueService getMetadataValueService();

    public abstract WorkspaceItemService getWorkspaceItemService();


    public abstract InstallItemService getInstallItemService();

    public abstract SupervisedItemService getSupervisedItemService();

    public abstract SiteService getSiteService();

    /**
     * Return the implementation of the RelationshipTypeService interface
     *
     * @return the RelationshipTypeService
     */
    public abstract RelationshipTypeService getRelationshipTypeService();

    /**
     * Return the implementation of the RelationshipService interface
     *
     * @return the RelationshipService
     */
    public abstract RelationshipService getRelationshipService();

    /**
     * Return the implementation of the EntityTypeService interface
     *
     * @return the EntityTypeService
     */
    public abstract EntityTypeService getEntityTypeService();

    /**
     * Return the implementation of the EntityService interface
     *
     * @return the EntityService
     */
    public abstract EntityService getEntityService();

    public abstract RelationshipMetadataService getRelationshipMetadataService();

    public InProgressSubmissionService getInProgressSubmissionService(InProgressSubmission inProgressSubmission) {
        if (inProgressSubmission instanceof WorkspaceItem) {
            return getWorkspaceItemService();
        } else {
            return WorkflowServiceFactory.getInstance().getWorkflowItemService();
        }
    }

    public <T extends DSpaceObject> DSpaceObjectService<T> getDSpaceObjectService(T dso) {
        // No need to worry when supressing, as long as our "getDSpaceObjectManager" method is properly implemented
        // no casting issues should occur
        @SuppressWarnings("unchecked")
        DSpaceObjectService<T> manager = getDSpaceObjectService(dso.getType());
        return manager;
    }

    @SuppressWarnings("unchecked")
    public <T extends DSpaceObject> DSpaceObjectService<T> getDSpaceObjectService(int type) {
        for (int i = 0; i < getDSpaceObjectServices().size(); i++) {
            DSpaceObjectService<? extends DSpaceObject> objectService = getDSpaceObjectServices().get(i);
            if (objectService.getSupportsTypeConstant() == type) {
                return (DSpaceObjectService<T>) objectService;
            }
        }
        throw new UnsupportedOperationException("Unknown DSpace type: " + type);
    }

    public DSpaceObjectLegacySupportService<? extends DSpaceObject> getDSpaceLegacyObjectService(int type) {
        for (int i = 0; i < getDSpaceObjectLegacySupportServices().size(); i++) {
            DSpaceObjectLegacySupportService<? extends DSpaceObject> objectLegacySupportService =
                getDSpaceObjectLegacySupportServices()
                    .get(i);
            if (objectLegacySupportService.getSupportsTypeConstant() == type) {
                return objectLegacySupportService;
            }

        }
        throw new UnsupportedOperationException("Unknown DSpace type: " + type);
    }

    public static ContentServiceFactory getInstance() {
        return DSpaceServicesFactory.getInstance().getServiceManager()
                                    .getServiceByName("contentServiceFactory", ContentServiceFactory.class);
    }

}
