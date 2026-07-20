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
public class LineChartdataPointsDTO {

    String name;
    List<Series> series = new ArrayList<>();

    public LineChartdataPointsDTO() {
    	super();
    }

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public List<Series> getSeries() {
		return series;
	}

	public void setSeries(List<Series> series) {
		this.series = series;
	}  
	
}
