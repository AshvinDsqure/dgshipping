/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;


import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author root
 */
public class ItemBarchartDTO {
    //private List<String> labels= new ArrayList<>();
    private List<ItemBardataset> datasets=new ArrayList<>();
    
    int count=0;
    
    private String lable = null;
    /*
    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
*/
    public ItemBarchartDTO() {
    	super();
    }

    public List<ItemBardataset> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<ItemBardataset> datasets) {
        this.datasets = datasets;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getLable() {
        return lable;
    }

    public void setLable(String lable) {
        this.lable = lable;
    }  
    
}
