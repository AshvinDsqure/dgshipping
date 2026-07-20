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

/**
 *
 * @author root
 */
public class BarChartDTO {
	/*
    String type;    
    String name;
    String markerType="circle";
    boolean showInLegend =true;
    int markerSize=4;
    String color;
    int lineThickness=4;
    String yValueFormatString;
    */
    List<BarChartdataPointsDTO> dataPoints = new ArrayList<>();
    //BarChartdataPointsDTO dataPoints;
    //String legendMarkerColor;

    public BarChartDTO() {
    	super();
    }
    /*
    public BarChartDTO(String type, String name, String yValueFormatString, List<BarChartdataPointsDTO> dataPointsList,String lineColor) {
        this.type = type;        
        this.name = name;
        this.yValueFormatString = yValueFormatString;
        this.dataPoints = dataPointsList;
        this.color=lineColor;
        this.legendMarkerColor=lineColor;
    }
    

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

   

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isShowInLegend() {
        return showInLegend;
    }

    public void setShowInLegend(boolean showInLegend) {
        this.showInLegend = showInLegend;
    }

    public int getMarkerSize() {
        return markerSize;
    }

    public void setMarkerSize(int markerSize) {
        this.markerSize = markerSize;
    }

    public String getyValueFormatString() {
        return yValueFormatString;
    }

    public void setyValueFormatString(String yValueFormatString) {
        this.yValueFormatString = yValueFormatString;
    }
*/
    public List<BarChartdataPointsDTO> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<BarChartdataPointsDTO> dataPoints) {
        this.dataPoints = dataPoints;
    }
}
