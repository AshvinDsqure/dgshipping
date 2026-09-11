/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.Enum;

import java.util.EnumSet;

public enum DmsObject {

    BITSTREAM("BITSTREAM",0),
    BUNDLE("BUNDLE",1),
    ITEM("ITEM",2),
    COLLECTION("COLLECTION",3),
    COMMUNITY("COMMUNITY",4),
    SITE("SITE",5),
    GROUP("GROUP",6),
    DOCEUMNTTYPE("DOCEUMNTTYPE",7),
    EPERSON("EPERSON",8),
    CITATION("CITATION",9),
    SEARCH("SEARCH",10),
    LOGIN("LOGIN",11);
    DmsObject(String type, int id) {
        this.action=type;
        this.id=id;
    }
    public static DmsObject find(Integer actionID) {
      return   EnumSet.allOf(DmsObject.class)
                .stream()
                .filter(e -> e.id.equals(actionID))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", actionID)));
    }

    private String action;
    private Integer id;

    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


}
