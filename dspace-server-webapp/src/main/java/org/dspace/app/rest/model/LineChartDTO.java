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
public class LineChartDTO {
    String type;    
    String name;
    List<LineChartdataPointsDTO> dataPoints = new ArrayList<>();

    public LineChartDTO() {
    	super();
    }

    public LineChartDTO(String type, String name, List<LineChartdataPointsDTO> dataPoints) {
        this.type = type;        
        this.name = name;
        this.dataPoints = dataPoints;
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

    public List<LineChartdataPointsDTO> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<LineChartdataPointsDTO> dataPoints) {
        this.dataPoints = dataPoints;
    }

}
