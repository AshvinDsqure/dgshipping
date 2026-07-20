/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.enums;

public enum Priority {
    High("High"), Medium("Medium"), Low("Low");
    private String name;
    Priority(String name) {
        this.name = name;
    }
    public String getPriorityName() {
        return name;
    }

}
