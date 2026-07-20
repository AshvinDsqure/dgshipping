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
public class ItemCartDTO {

	/*
    private String DocumentType;
    private String uuid;
    private String Title;
    private String Auther;
    private String collectionName;
    private String communityName;
    private int total_View;
    private int total_View_item;
    private int total_Downlode;
    private int total_Downlode_Item;
    private int total_Reader;
    private int total_Reader_item;
    private String url;
    */
    private ItemBarchartDTO viewDatapoint=new ItemBarchartDTO();
    private ItemBarchartDTO DownlodeDatapoint=new ItemBarchartDTO();
    private ItemBarchartDTO ReaderDatapoint=new ItemBarchartDTO();
  /*  
    public String getDocumentType() {
        return DocumentType;
    }

    public void setDocumentType(String DocumentType) {
        this.DocumentType = DocumentType;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getAuther() {
        return Auther;
    }

    public void setAuther(String Auther) {
        this.Auther = Auther;
    }

    public int getTotal_View() {
        return total_View;
    }

    public void setTotal_View(int total_View) {
        this.total_View = total_View;
    }

    public int getTotal_View_item() {
        return total_View_item;
    }

    public void setTotal_View_item(int total_View_item) {
        this.total_View_item = total_View_item;
    }

    public int getTotal_Downlode() {
        return total_Downlode;
    }

    public void setTotal_Downlode(int total_Downlode) {
        this.total_Downlode = total_Downlode;
    }

    public int getTotal_Downlode_Item() {
        return total_Downlode_Item;
    }

    public void setTotal_Downlode_Item(int total_Downlode_Item) {
        this.total_Downlode_Item = total_Downlode_Item;
    }

    public int getTotal_Reader() {
        return total_Reader;
    }

    public void setTotal_Reader(int total_Reader) {
        this.total_Reader = total_Reader;
    }

    public int getTotal_Reader_item() {
        return total_Reader_item;
    }

    public void setTotal_Reader_item(int total_Reader_item) {
        this.total_Reader_item = total_Reader_item;
    }
*/
    public ItemBarchartDTO getViewDatapoint() {
        return viewDatapoint;
    }

    public void setViewDatapoint(ItemBarchartDTO viewDatapoint) {
        this.viewDatapoint = viewDatapoint;
    }

    public ItemBarchartDTO getDownlodeDatapoint() {
        return DownlodeDatapoint;
    }

    public void setDownlodeDatapoint(ItemBarchartDTO DownlodeDatapoint) {
        this.DownlodeDatapoint = DownlodeDatapoint;
    }

    public ItemBarchartDTO getReaderDatapoint() {
        return ReaderDatapoint;
    }

    public void setReaderDatapoint(ItemBarchartDTO ReaderDatapoint) {
        this.ReaderDatapoint = ReaderDatapoint;
    }
/*
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
    */
    
   
}
