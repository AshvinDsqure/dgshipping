/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.dspaceevent.models;
import java.util.Date;
import java.util.UUID;
public class DspaceEventInfo {
    private String title;
    private Integer action;
    private Date action_date = new Date();
    private Integer dspaceobjecttype;
    private UUID dspaceobjectid;
    private UUID userid = null;
    private UUID parenCollection=null;
    private UUID parenCommunity=null;
    private  String ip;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Date getAction_date() {
        return action_date;
    }

    public void setAction_date(Date action_date) {
        this.action_date = action_date;
    }

    public Integer getDspaceobjecttype() {
        return dspaceobjecttype;
    }

    public void setDspaceobjecttype(Integer dspaceobjecttype) {
        this.dspaceobjecttype = dspaceobjecttype;
    }

    public UUID getDspaceobjectid() {
        return dspaceobjectid;
    }

    public void setDspaceobjectid(UUID dspaceobjectid) {
        this.dspaceobjectid = dspaceobjectid;
    }

    public UUID getUserid() {
        return userid;
    }

    public void setUserid(UUID userid) {
        this.userid = userid;
    }

    public UUID getParenCollection() {
        return parenCollection;
    }

    public void setParenCollection(UUID parenCollection) {
        this.parenCollection = parenCollection;
    }

    public UUID getParenCommunity() {
        return parenCommunity;
    }

    public void setParenCommunity(UUID parenCommunity) {
        this.parenCommunity = parenCommunity;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
