/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author root
 */
public class CountryDTO {

    private int views;
    private int downloads;
    private int searches;
    private String city;
    private int value;
    private float  latitude;
    private float  longitude;
    private Map<String,String> content =new HashMap<>();
    private List<CountryDTO> CountryDTO = new ArrayList<CountryDTO>();
    Map<String, CountryDTO> cityDTOmap = new LinkedHashMap<>();

    public Map<String, CountryDTO> getCityDTOmap() {
        return cityDTOmap;
    }

    public void setCityDTOmap(Map<String, CountryDTO> cityDTOmap) {
        this.cityDTOmap = cityDTOmap;
    }

    public List<CountryDTO> getCountryDTO() {
        return CountryDTO;
    }

    public void setCountryDTO(List<CountryDTO> CountryDTO) {
        this.CountryDTO = CountryDTO;
    }

    public CountryDTO() {
        super();
    }
    public CountryDTO(int views, int downloads, int searches) {
        this.views = views;
        this.downloads = downloads;
        this.searches = searches;
    }


    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public int getSearches() {
        return searches;
    }

    public void setSearches(int searches) {
        this.searches = searches;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Map<String, String> getContent() {
        content.put("content", "<strong class=\"country_name\">"+this.city+"</strong><br /><span class=\"support3lbl\">Views: "+this.views+"</span></br> <span class=\"support3lbl\">Downloads: "+this.downloads+"</span> </br><span class=\"support3lbl\"> Searches: "+this.searches+" </span>");
        return content;
    }

    public void setContent(Map<String, String> content) {
        this.content = content;
    }



    public int getValue() {
        this.value=this.views +this.downloads +this.searches;
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }




}
