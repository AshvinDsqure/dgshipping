/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

/**
 *
 * @author root
 */
public class DocType {
    int typeindex;
    String doctype;

    public DocType(int typeindex, String doctype) {
        this.typeindex = typeindex;
        this.doctype = doctype;
    }
    
    public int getTypeindex() {
        return typeindex;
    }

    public void setTypeindex(int typeindex) {
        this.typeindex = typeindex;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }
    
}
