/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.dspace.eperson.EPerson;
import org.hibernate.annotations.Type;

/**
 * Class representing an item in DSpace.
 * <P>
 * This class holds in memory the item Dublin Core metadata, the bundles in the
 * item, and the bitstreams in those bundles. When modifying the item, if you
 * modify the Dublin Core or the "in archive" flag, you must call
 * <code>update</code> for the changes to be written to the database. Creating,
 * adding or removing bundles or bitstreams has immediate effect in the
 * database.
 *
 * @author Robert Tansley
 * @author Martin Hald
 * @version $Revision$
 */
@Entity
@Table(name = "event")
public class Event extends DSpaceObject implements DSpaceObjectLegacySupport {

	/**
	 * Wild card for Dublin Core metadata qualifiers/languages
	 */
	public static final String ANY = "*";
	@Column(name = "event_id", insertable = false, updatable = false)
	private Integer legacyId;
	@Column(name = "title")
	private String title;
	@Column(name = "action")
	private Integer action;
	@Column(name = "action_date", columnDefinition = "timestamp with time zone")
	@Temporal(TemporalType.TIMESTAMP)
	private Date action_date = new Date();
	@Column(name = "dspaceobjecttype")
	private Integer dspaceobjecttype;

	@Column(name = "dspaceobjectid")
	@Type(type = "pg-uuid")
	private UUID dspaceobjectid;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "userid")
	private EPerson user = null;

	@Column(name = "parencollection")
	@Type(type = "pg-uuid")
	private UUID parenCollection;

	@Column(name = "parencommunity")
	@Type(type = "pg-uuid")
	private UUID parenCommunity;

	@Column(name = "documenttype")
	private int documenttype;
	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "geolocationeventid")
	private GeolocationEvent GeolocationEvent = null;

	@Override
	public Integer getLegacyId() {
		return legacyId;
	}

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

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public String getName() {
		return "Event";
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

	public GeolocationEvent getGeolocationEvent() {
		return GeolocationEvent;
	}

	public void setGeolocationEvent(GeolocationEvent GeolocationEvent) {
		this.GeolocationEvent = GeolocationEvent;
	}

	public EPerson getUser() {
		return user;
	}

	public void setUser(EPerson user) {
		this.user = user;
	}

	public int getDocumenttype() {
		return documenttype;
	}

	public void setDocumenttype(int documenttype) {
		this.documenttype = documenttype;
	}

}
