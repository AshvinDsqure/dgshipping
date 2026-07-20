/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.enums;

public enum Dispatch {
    ELECTRIC("Electric"),
    PHYSICAL("Physical");
    private String name;
    Dispatch(String name) {
        this.name = name;
    }
    public String getDispatchMode() {
        return name;
    }
}
