/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

public class ExcelDTO {

   private  String title;
    private String type;
    private String issue;
    private String caseDetail;
    private String uploaddate;
    private String uploadedby;

    private String hierarchy;
    private String email;


    public ExcelDTO(String title, String type, String issue, String caseDetail, String uploaddate, String uploadedby,String hierarchy,String email) {
        this.title = title;
        this.type = type;
        this.issue = issue;
        this.caseDetail = caseDetail;
        this.uploaddate = uploaddate;
        this.uploadedby = uploadedby;
        this.hierarchy =hierarchy;
        this.email=email;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getCaseDetail() {
        return caseDetail;
    }

    public void setCaseDetail(String caseDetail) {
        this.caseDetail = caseDetail;
    }

    public String getUploaddate() {
        return uploaddate;
    }

    public void setUploaddate(String uploaddate) {
        this.uploaddate = uploaddate;
    }

    public String getUploadedby() {
        return uploadedby;
    }

    public void setUploadedby(String uploadedby) {
        this.uploadedby = uploadedby;
    }

    public String getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(String hierarchy) {
        this.hierarchy = hierarchy;
    }

    public String  getCashDetails(){

        return  this.type+"/"+this.title+"/"+this.issue;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
