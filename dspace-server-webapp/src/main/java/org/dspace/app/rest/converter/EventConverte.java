/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.converter;

import org.apache.logging.log4j.Logger;
import org.dspace.app.rest.Enum.DmsObject;
import org.dspace.app.rest.model.EPersonRest;
import org.dspace.app.rest.model.EventRest;
import org.dspace.app.rest.projection.Projection;
import org.dspace.content.Event;
import org.dspace.content.Item;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.discovery.IndexableObject;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.service.EPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * This is the converter from/to the Item in the DSpace API data model and the
 * REST data model
 *
 * @author Andrea Bollini (andrea.bollini at 4science.it)
 */
@Component
public class EventConverte extends DSpaceObjectConverter<Event, EventRest>
        implements IndexableObjectConverter<Event, EventRest> {


    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(EventConverte.class);
    @Autowired
    ItemConverter itemConverter;

    @Autowired
    ItemService itemService;
    @Autowired
    EPersonConverter ePersonConverter;

    @Autowired
    EPersonService ePersonService;


    @Override
    public EventRest convert(Event obj, Projection projection) {
        EventRest dspaceEventRest = new EventRest();
        try {
            dspaceEventRest.setId(obj.getID().toString());
            dspaceEventRest.setUuid(obj.getID().toString());
            dspaceEventRest.setAction(obj.getAction().toString());
            dspaceEventRest.setActionDate(obj.getAction_date());
            if(obj.getUser() != null) {
                dspaceEventRest.setePersonRest(ePersonConverter.convert(obj.getUser(),projection));
            }
//            if(obj.getItem() != null){
//                dspaceEventRest.setItem(itemConverter.convertWihoutMataData(obj.getItem(),projection));
//            }
            /*if(obj.getDocumentTypeTree() != null){
                dspaceEventRest.setDocumenttypenameRest(documentTypeTreeConverter.convertTODocumentType(obj.getDocumentTypeTree(),projection));
            }*/
//            if(obj.getDescription() != null){
//                dspaceEventRest.setDescription(obj.getDescription());
//            }
            if(obj.getTitle() != null) {
                dspaceEventRest.setDescription(obj.getTitle());
                dspaceEventRest.setTitle(obj.getTitle());
            }
            if(obj.getDspaceobjecttype() != null){
                dspaceEventRest.setDspaceObject(DmsObject.find(obj.getDspaceobjecttype()).getAction());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return dspaceEventRest;
    }

    public EventRest convertbyDTO(Object[] o, Context context,Projection projection) {
        EventRest dspaceEventRest = new EventRest();
        try {
            if(o[0]!=null){
              dspaceEventRest.setId(o[0].toString());
              dspaceEventRest.setUuid(o[0].toString());
            }
            if (o[1] != null) {
                dspaceEventRest.setDescription(o[1].toString());
                dspaceEventRest.setTitle(o[1].toString());
            }
            if (o[2] != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    Date date = formatter.parse(o[2].toString());
                    dspaceEventRest.setActionDate(date);
                    //System.out.println("Converted Date: " + date);
                } catch (ParseException e) {
                    System.out.println("Error parsing date: " + e.getMessage());
                }

            }
            if (o[3] != null) {
                Integer i=Integer.valueOf(o[3].toString());
                dspaceEventRest.setAction(getAction(i));
            }
            if (o[4] != null) {
                dspaceEventRest.setDspaceObject(DmsObject.find(Integer.parseInt(o[4].toString())).getAction());
            }
            //user
            if (o[5] != null) {
                EPerson e = ePersonService.find(context, UUID.fromString(o[5].toString()));
                if (e != null) {
                    EPersonRest rest = ePersonConverter.convert(e, projection);
                    dspaceEventRest.setePersonRest(rest);
                }
            }
            if (o[6] != null) {
                Item i = itemService.find(context, UUID.fromString(o[6].toString()));
                if (i != null) {
                    dspaceEventRest.setItem(itemConverter.convert(i, projection));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dspaceEventRest;
    }

    public static String getAction(Integer action){

        if(action==org.dspace.event.Event.LOGIN){
            return "Login";
        }else if(action==org.dspace.event.Event.VIEW){
            return "View";
        }else if(action==org.dspace.event.Event.SEARCH){
            return "Search";
        }else if(action==org.dspace.event.Event.DOWNLOAD){
            return "Download";
        }else if(action==org.dspace.event.Event.CREATE){
          return "Create";
        }else if(action==org.dspace.event.Event.MODIFY_METADATA){
            return "Edit";
        }else if(action==org.dspace.event.Event.REMOVE){
            return "Delete";
        }else{
            return "Other";
        }
    }


    @Override
    protected EventRest newInstance() {
        return new EventRest();
    }

    @Override
    public Class<Event> getModelClass() {
        return Event.class;
    }

    @Override
    public boolean supportsModel(IndexableObject idxo) {
        return idxo.getIndexedObject() instanceof Event;
    }
}
