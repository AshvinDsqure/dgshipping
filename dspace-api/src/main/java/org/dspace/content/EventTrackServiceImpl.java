/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.dao.EventDAO;
import org.dspace.content.service.EventTrackService;
import org.dspace.core.Context;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service implementation for the Item object. This class is responsible for all
 * business logic calls for the Item object and is autowired by spring. This
 * class should never be accessed directly.
 *
 * @author kevinvandevelde at atmire.com
 */
public class EventTrackServiceImpl extends DSpaceObjectServiceImpl<Event> implements EventTrackService {

    /**
     * log4j category
     */
    private static final Logger log = org.apache.logging.log4j.LogManager
            .getLogger(EventTrackServiceImpl.class);

    @Autowired(required = true)
    protected EventDAO eventDAO;

    @Override
    public Event find(Context context, UUID id) throws SQLException {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return null;
    }

    @Override
    public void updateLastModified(Context context, Event dso) throws SQLException, AuthorizeException {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Context context, Event dso) throws SQLException, AuthorizeException, IOException {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getSupportsTypeConstant() {
        return 0;
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Event findByIdOrLegacyId(Context context, String id) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Event findByLegacyId(Context context, int id) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Event create(Context context, Event event) throws Exception {
        return eventDAO.create(context, event);
    }

    @Override
    public int FindcountBydate(Context context, Integer limit, Date startdate, Date enddate, int dspaceObjectid, String email, String s) throws Exception {
        return eventDAO.FindcountBydate(context, limit, startdate, enddate, dspaceObjectid, email, s);
    }

    @Override
    public int viewCount(Context context, UUID dspaceObjectid) throws Exception {
        return eventDAO.viewCount(context, dspaceObjectid);
    }
    public int viewCountbyType(Context context, UUID dspaceObjectid,int type) throws Exception {
        return eventDAO.viewCountbyType(context, dspaceObjectid,type);
    }
    

    @Override
    public int bitViewCount(Context context, UUID dspaceObjectid) throws Exception {
        return eventDAO.bitViewCount(context, dspaceObjectid);
    }

    @Override
    public List<Object[]> FindcountBydateforChart(Context context, String oderby, Date startdate, Date enddate, int dspaceObjectid, String email, boolean fromtable, int o, String s, String order) throws Exception {
        return eventDAO.FindcountBydateforChart(context, oderby, startdate, enddate, dspaceObjectid, email, fromtable, o, s, order);
    }

    @Override
    public List<Object> findEventBYDate(Context context, Integer limit, Date startdate, Date enddate, int dspaceObjectid, String email, boolean fromtable, int o, String s, String order) throws Exception {
        return eventDAO.findEventBYDate(context, limit, startdate, enddate, dspaceObjectid, email, fromtable, o, s, order);
    }

    @Override
    public List<Object[]> findEventTop10item(Context context, int limit, int Objecttype, int action, String collactionID, String communityID, Date startDate, Date endDate) throws Exception {
        return eventDAO.findEventTop10item(context, limit, Objecttype, action, collactionID, communityID, startDate, endDate);
    }

    @Override
    public List<Object[]> findEventTop10Community(Context context, int limit, int Objecttype, int action, String collactionID, String communityID, Date startDate, Date endDate) throws Exception {
        return eventDAO.findEventTop10Community(context, limit, Objecttype, action, collactionID, communityID, startDate, endDate);
    }

    @Override
    public List<Object[]> findEventTop10Collaction(Context context, int limit, int Objecttype, int action, String collactionID, String communityID, Date startDate, Date endDate) throws Exception {
        return eventDAO.findEventTop10Collaction(context, limit, Objecttype, action, collactionID, communityID, startDate, endDate);
    }

    @Override
    public List<Object[]> findEventTop10Search(Context context, int limit, int Objecttype, int action, Date startDate, Date endDate) throws Exception {
        return eventDAO.findEventTop10Search(context, limit, Objecttype, action, startDate, endDate);
    }

    @Override
    public List<Object[]> findEventTop10CommunityByCountry(Context context, int limit, int Objecttype, int action, String collactionID, String communityID, Date startDate, Date endDate, String iteamid) throws Exception {
        return eventDAO.findEventTop10CommunityByCountry(context, limit, Objecttype, action, collactionID, communityID, startDate, endDate, iteamid);
    }

    @Override
    public List<Object[]> getIteamByMonthCount(Context context, String query) throws Exception {
        return eventDAO.getIteamByMonthCount(context, query);
    }

    @Override
    public List<Object[]> getBitstreamByMonthCount(Context context, int limit, int Objecttype, int action, String iteamid) throws Exception {
        return eventDAO.getBitstreamByMonthCount(context, limit, Objecttype, action, iteamid);
    }

    @Override
    public List<Object[]> findEventTop10ItemByCountry(Context context, int limit, int Objecttype, int action, String iteamID) throws Exception {
        return eventDAO.findEventTop10ItemByCountry(context, limit, Objecttype, action, iteamID);
    }

    @Override
    public List<Event> getDataByFilter(Context context, String query, int count) throws Exception {
        return eventDAO.getDataByFilter(context, query, count);
    }

    @Override
    public Long getDataByFilterCount(Context context, String query) throws Exception {
        return eventDAO.getDataByFilterCount(context, query);
    }

    @Override
    public Iterator getDataCountByFilter(Context context, String query, String GroubBy) throws Exception {
        return eventDAO.getDataCountByFilter(context, query, GroubBy);
    }

    @Override
    public List<Object[]> itemBarChart(Context cntxt, String Query, String GroupBy) throws Exception {
        return eventDAO.itemBarChart(cntxt, Query, GroupBy);
    }

    @Override
    public List<Object[]> itemLineChart(Context cntxt, String Query, String GroupBy) throws Exception {
        return eventDAO.itemLineChart(cntxt, Query, GroupBy);
    }

    @Override
    public List<Object[]> ItemByDate(Context cntxt, String string) throws Exception {
        return eventDAO.ItemByDate(cntxt, string);
    }

    @Override
    public int totalItemView(Context cntxt, String Query) throws Exception {
        return eventDAO.totalItemView(cntxt, Query);
    }
    @Override
    public List<Object[]> ItemViewBYDate(Context cntxt, String Query) throws Exception {
        return eventDAO.ItemViewBYDate(cntxt, Query);
    }
    @Override
    public List<Object[]> ItemViewDownloadBYDate(Context cntxt, String Query) throws Exception{
         return eventDAO.ItemViewDownloadBYDate(cntxt, Query);
    }
    @Override
    public List<Object[]> ItemDownlodeBYDate(Context cntxt, String Query) throws Exception {
        return eventDAO.ItemDownlodeBYDate(cntxt, Query);
    }

    @Override
    public int totalItemDownlode(Context cntxt, String string) throws Exception {
        return eventDAO.totalItemDownlode(cntxt, string);
    }

    @Override
    public List<Object[]> getGeolocationData(Context cntxt, String string) throws Exception {
        return eventDAO.getGeolocationData(cntxt, string);
    }
    @Override
    public List<Object[]> getTrendingCommunitie(Context cntxt, String Query, String GroupBy) throws Exception {
        return eventDAO.getTrendingCommunitie(cntxt, Query, GroupBy);
    }
    @Override
    public List<Object[]> getTrendingCollection(Context cntxt, String Query, String GroupBy) throws Exception{
        return eventDAO.getTrendingCollection(cntxt, Query, GroupBy);
    }

    @Override
    public List<Object[]> CommunitieViewBYDate(Context cntxt, String string) throws Exception {
        return eventDAO.CommunitieViewBYDate(cntxt, string);
    }
    @Override
    public List<Object[]> CollectionViewBYDate(Context cntxt, String string) throws Exception {
        return eventDAO.CollectionViewBYDate(cntxt, string);
    }
    @Override
    public List<Object[]> SearchBYDate(Context cntxt, String Query) throws Exception {
        return eventDAO.SearchBYDate(cntxt, Query);
    }
    @Override
    public List<Object[]> itemTypeChart(Context cntxt, String Query, String GroupBy) throws Exception {
         return eventDAO.itemTypeChart(cntxt, Query,GroupBy);
    }

    @Override
    public List<Object[]> TypeViewBYDate(Context cntxt, String Query) throws Exception {
        return eventDAO.TypeViewBYDate(cntxt, Query);
    }
    @Override
    public List<Object[]> ItemByType(Context cntxt, String Query) throws Exception {
        return eventDAO.ItemByType(cntxt, Query);
    }

    @Override
    public List<Object[]> getTrendingSearch(Context cntxt, String Query, String groupBy) throws Exception {
         return eventDAO.getTrendingSearch(cntxt, Query,groupBy);
    }
    @Override
    public List<Object[]> itemLineChartAll(Context cntxt, String Query, String GroupBy) throws Exception {
        return eventDAO.itemLineChartAll(cntxt, Query,GroupBy);
    }
    @Override
    public List<Object> getcountry(Context cntxt, String Query, String GroupBy) throws Exception {
        return eventDAO.getcountry(cntxt, Query, GroupBy);
    }

    @Override
    public List<Object[]> getCountByObject(Context cntxt, String Query, String GroupBy) throws Exception {
        return eventDAO.getCountByObject(cntxt, Query, GroupBy);
    }

    @Override
    public List<Object[]> getcityByCountry(Context cntxt, String Query, String country, String GroupBy) throws Exception {
        return eventDAO.getcityByCountry(cntxt, Query, country, GroupBy);
    }
    @Override
    public List<Object[]> getcityByObject(Context cntxt, String Query, String GroupBy) throws Exception {
        return eventDAO.getcityByObject(cntxt, Query, GroupBy);
    }
    @Override
    public List<Object[]> getcityBycommunity(Context cntxt, String Query, String GroupBy) throws Exception {
        return eventDAO.getcityBycommunity(cntxt, Query, GroupBy);
    }
    @Override
    public List<Object[]> getcityBycollection(Context cntxt, String Query, String GroupBy) throws Exception {
        return eventDAO.getcityByCollection(cntxt, Query, GroupBy);
    }
    @Override
    public List<Object[]> getcityBySearch(Context cntxt, String Query, String GroupBy) throws Exception {
         return eventDAO.getcityBySearch(cntxt, Query, GroupBy);
    }
        @Override
    public List<Object[]> ViewCountReport(Context cntxt, String Query) throws Exception {
       return eventDAO.ViewCountReport(cntxt, Query);
    }
    
    @Override
    public List<Object[]> getcountryBySearch(Context cntxt, String Query, String GroupBy) throws Exception {
         return eventDAO.getcountryBySearch(cntxt, Query, GroupBy);
    }
    @Override
    public List<Object[]> getcitycountryBySearch(Context cntxt, String Query, String Country, String GroupBy) throws Exception {
         return eventDAO.getcitycountryBySearch(cntxt, Query, Country, GroupBy);
    }
    @Override
    public List<Object[]> getcountryBycommunity(Context cntxt, String Query, String GroupBy) throws Exception {
         return eventDAO.getcountryBycommunity(cntxt, Query, GroupBy);
    }
    @Override
    public List<Object[]> getcitycountryBycommunity(Context cntxt, String Query, String Country, String GroupBy) throws Exception {
         return eventDAO.getcitycountryBycommunity(cntxt, Query, Country, GroupBy);
    }
    
    @Override
    public List<Object[]> getcountryByCollection(Context cntxt, String Query, String GroupBy) throws Exception {
         return eventDAO.getcountryByCollection(cntxt, Query, GroupBy);
    }
    @Override
    public List<Object[]> getcitycountryByCollection(Context cntxt, String Query, String Country, String GroupBy) throws Exception {
         return eventDAO.getcitycountryByCollection(cntxt, Query, Country, GroupBy);
    }
    
}
