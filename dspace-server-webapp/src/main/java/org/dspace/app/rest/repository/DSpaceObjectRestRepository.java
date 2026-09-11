/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;

import java.sql.SQLException;
import java.util.UUID;

import com.google.gson.Gson;
import org.dspace.app.rest.converter.MetadataConverter;
import org.dspace.app.rest.dspaceevent.AnalyticsServerImpl;
import org.dspace.app.rest.dspaceevent.models.DspaceEventInfo;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.DSpaceObjectRest;
import org.dspace.app.rest.model.patch.Patch;
import org.dspace.app.rest.repository.patch.ResourcePatch;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.service.DSpaceObjectService;
import org.dspace.core.Context;
import org.dspace.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import javax.servlet.http.HttpServletRequest;

/**
 * Base class for DSpaceObject-based Rest Repositories, providing common functionality.
 *
 * @param <M> the specific type of DSpaceObject.
 * @param <R> the corresponding DSpaceObjectRest.
 */
public abstract class DSpaceObjectRestRepository<M extends DSpaceObject, R extends DSpaceObjectRest>
        extends DSpaceRestRepository<R, UUID> implements ReloadableEntityObjectRepository<M, UUID> {

    final DSpaceObjectService<M> dsoService;

    @Autowired
    AnalyticsServerImpl analyticsServerImp;

    @Autowired
    ResourcePatch<M> resourcePatch;
    @Autowired
    MetadataConverter metadataConverter;

    DSpaceObjectRestRepository(DSpaceObjectService<M> dsoService) {
        this.dsoService = dsoService;
    }

    /**
     * Updates the DSpaceObject according to the given Patch.
     *
     * @param apiCategory the api category.
     * @param model the api model.
     * @param id the id of the DSpaceObject.
     * @param patch the patch to apply.
     * @throws AuthorizeException if the action is unauthorized.
     * @throws ResourceNotFoundException if the DSpace object was not found.
     * @throws SQLException if a database error occurs.
     * @throws UnprocessableEntityException if the patch attempts to modify an unmodifiable attribute of the object.
     */
    protected void patchDSpaceObject(String apiCategory, String model, UUID id, Patch patch)
            throws AuthorizeException, ResourceNotFoundException, SQLException, UnprocessableEntityException {
        HttpServletRequest req = getRequestService().getCurrentRequest().getHttpServletRequest();

        Context context = obtainContext();
        M dso = dsoService.find(context, id);
        if (dso == null) {
            throw new ResourceNotFoundException(apiCategory + "." + model + " with id: " + id + " not found");
        }
        resourcePatch.patch(obtainContext(), dso, patch.getOperations());
        dsoService.update(obtainContext(), dso);
        if(dso.getType()!=7) {
            try {
                DspaceEventInfo dspaceEventInfo = analyticsServerImp.getDspaceEventInfo(Event.MODIFY_METADATA, dso.getID(), dso.getType());
                if (context.getCurrentUser() != null) {
                    dspaceEventInfo.setUserid(context.getCurrentUser().getID());
                }
                dspaceEventInfo.setTitle(dso.getName());
                dspaceEventInfo.setIp(req.getRemoteAddr());
                // System.out.println("dspaceEventInfo::::" + new Gson().toJson(dspaceEventInfo));
                analyticsServerImp.storeEvent(dspaceEventInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void trackDspaceEvent(Context context,int action,DSpaceObject dso){
        try{
            HttpServletRequest req = getRequestService().getCurrentRequest().getHttpServletRequest();
            DspaceEventInfo dspaceEventInfo=analyticsServerImp.getDspaceEventInfo(action,dso.getID(), dso.getType());
            if(context.getCurrentUser() != null){
                dspaceEventInfo.setUserid(context.getCurrentUser().getID());
            }
            dspaceEventInfo.setTitle(dso.getName());
            dspaceEventInfo.setIp(req.getRemoteAddr());
            if (dso instanceof Collection) {
                Collection collection = (Collection) dso;
                if (collection.getCommunities().size() != 0) {
                    Community community = collection.getCommunities().get(0);
                    dspaceEventInfo.setParenCommunity(community.getID());
                }
            }
            if (dso instanceof Community) {
                Community community = (Community) dso;
                if (community.getParentCommunities().size() != 0) {
                    dspaceEventInfo.setParenCommunity(community.getParentCommunities().get(0).getID());
                }
            }
            if (dso instanceof Item) {
                Item item = (Item) dso;
                Collection collection = item.getOwningCollection();
                dspaceEventInfo.setParenCollection(collection.getID());
                if (collection.getCommunities().size() != 0) {
                    dspaceEventInfo.setParenCommunity(collection.getCommunities().get(0).getID());
                }
            }
            System.out.println("dspaceEventInfo:::n"+new Gson().toJson(dspaceEventInfo));
            analyticsServerImp.storeEvent(dspaceEventInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public M findDomainObjectByPk(Context context, UUID uuid) throws SQLException {
        return dsoService.find(context, uuid);
    }

    @Override
    public Class<UUID> getPKClass() {
        return UUID.class;
    }
}
