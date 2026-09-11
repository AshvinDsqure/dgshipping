/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.Enum;


import org.dspace.content.Item;
import org.dspace.content.service.EventTrackService;
import org.dspace.eperson.EPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.EnumSet;

public enum DmsAction {
    LOGIN("Login",0),
    VIEWDOC("View Document",1),
    VIEWMEAREDOC("View Mearge Document",2),
    DOWNLOAD("Download",3),
    EDIT("Edit",4),
    DELETE("Delete",5),
    MERGEPDF("Merge PDF",6);

    DmsAction(String action,int id) {
        this.action=action;
        this.actionID=id;
    }
    public static DmsAction find(Integer actionID) {
      return   EnumSet.allOf(DmsAction.class)
                .stream()
                .filter(e -> e.actionID.equals(actionID))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", actionID)));
    }
    private EventTrackService EventService;
    private String action;
    private Integer id;
    private Integer actionID;
    private EPerson ePerson;
    private Item item;
    private String description;
    private String title;
    private  DmsObject dsDmsObject;
    @Component
    public static class ServiceInjector {
        @Autowired
        private EventTrackService EventService;
        @PostConstruct
        public void postConstruct() {
            for (DmsAction rt : EnumSet.allOf(DmsAction.class)) {
                rt.setEventService(EventService);
            }
        }

        public EventTrackService getEventService() {
            return EventService;
        }

        public void setEventService(EventTrackService EventService) {
            this.EventService = EventService;
        }
    }

    public EventTrackService getEventService() {
        return EventService;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getActionID() {
        return actionID;
    }

    public void setActionID(Integer actionID) {
        this.actionID = actionID;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public EPerson getePerson() {
        return ePerson;
    }

    public void setePerson(EPerson ePerson) {
        this.ePerson = ePerson;
    }



    public void setEventService(EventTrackService EventService) {
        this.EventService = EventService;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DmsObject getDsDmsObject() {
        return dsDmsObject;
    }

    public void setDsDmsObject(DmsObject dsDmsObject) {
        this.dsDmsObject = dsDmsObject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if(item != null){
            this.title=item.getName();
        }else {
            this.title = title;
        }
    }
}
