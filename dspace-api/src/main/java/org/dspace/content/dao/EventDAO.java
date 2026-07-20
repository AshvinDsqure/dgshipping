/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.content.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.dspace.content.Event;
import org.dspace.core.Context;

/**
 * Database Access Object interface class for the Item object. The
 * implementation of this class is responsible for all database calls for the
 * Item object and is autowired by spring This class should only be accessed
 * from a single service and should never be exposed outside of the API
 *
 * @author kevinvandevelde at atmire.com
 */
public interface EventDAO extends DSpaceObjectLegacySupportDAO<Event> {

	public int FindcountBydate(Context context, Integer limit, Date startdate, Date enddate, int dspaceObjectid,
			String email, String s) throws Exception;

	public List<Object[]> FindcountBydateforChart(Context context, String oderby, Date startdate, Date enddate,
			int dspaceObjectid, String email, boolean fromtable, int o, String s, String order) throws Exception;

	public List<Object> findEventBYDate(Context context, Integer limit, Date startdate, Date enddate,
			int dspaceObjectid, String email, boolean fromtable, int o, String s, String order) throws Exception;

	public List<Object[]> findEventTop10item(Context context, int limit, int Objecttype, int action,
			String collactionID, String communityID, Date startDate, Date endDate) throws Exception;

	public List<Object[]> findEventTop10Community(Context context, int limit, int Objecttype, int action,
			String collactionID, String communityID, Date startDate, Date endDate) throws Exception;

	public List<Object[]> findEventTop10Collaction(Context context, int limit, int Objecttype, int action,
			String collactionID, String communityID, Date startDate, Date endDate) throws Exception;

	public List<Object[]> findEventTop10Search(Context context, int limit, int Objecttype, int action, Date startDate,
			Date endDate) throws Exception;

	public List<Object[]> findEventTop10CommunityByCountry(Context context, int limit, int Objecttype, int action,
			String collactionID, String communityID, Date startDate, Date endDate, String iteamid) throws Exception;

	public List<Object[]> getIteamByMonthCount(Context context, String query) throws Exception;

	public List<Object[]> getBitstreamByMonthCount(Context context, int limit, int Objecttype, int action,
			String iteamid) throws Exception;

	public List<Object[]> findEventTop10ItemByCountry(Context context, int limit, int Objecttype, int action,
			String iteamID) throws Exception;

	public int viewCount(Context context, UUID dspaceObjectid) throws Exception;

	public int viewCountbyType(Context context, UUID dspaceObjectid, int type) throws Exception;

	public int bitViewCount(Context context, UUID dspaceObjectid) throws Exception;

	public Iterator getDataCountByFilter(Context context, String query, String GroubBy) throws Exception;

	public Long getDataByFilterCount(Context context, String query) throws Exception;

	public List<Event> getDataByFilter(Context context, String query, int count) throws Exception;

	public List<Object[]> itemBarChart(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> itemLineChart(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> itemLineChartAll(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> ItemByDate(Context cntxt, String Query) throws Exception;

	public int totalItemView(Context cntxt, String Query) throws Exception;

	public int totalItemDownlode(Context cntxt, String Query) throws Exception;

	public List<Object[]> ItemViewBYDate(Context cntxt, String Query) throws Exception;

	public List<Object[]> ItemDownlodeBYDate(Context cntxt, String Query) throws Exception;

	public List<Object[]> ItemViewDownloadBYDate(Context cntxt, String Query) throws Exception;

	public List<Object[]> getGeolocationData(Context cntxt, String Query) throws Exception;

	public List<Object[]> getTrendingCommunitie(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> getTrendingCollection(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> CommunitieViewBYDate(Context cntxt, String Query) throws Exception;

	public List<Object[]> CollectionViewBYDate(Context cntxt, String Query) throws Exception;

	public List<Object[]> SearchBYDate(Context cntxt, String Query) throws Exception;

	public List<Object[]> itemTypeChart(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> TypeViewBYDate(Context cntxt, String Query) throws Exception;

	public List<Object[]> ItemByType(Context cntxt, String Query) throws Exception;

	public List<Object[]> getTrendingSearch(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object> getcountry(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> getCountByObject(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> getcityByCountry(Context cntxt, String Query, String country, String GroupBy)
			throws Exception;

	public List<Object[]> getcityByObject(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> getcityBycommunity(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> getcityByCollection(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> getcityBySearch(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> ViewCountReport(Context cntxt, String Query) throws Exception;

	public List<Object[]> getcountryBySearch(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> getcitycountryBySearch(Context cntxt, String Query, String country, String GroupBy)
			throws Exception;

	public List<Object[]> getcountryBycommunity(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> getcitycountryBycommunity(Context cntxt, String Query, String country, String GroupBy)
			throws Exception;

	public List<Object[]> getcountryByCollection(Context cntxt, String Query, String GroupBy) throws Exception;

	public List<Object[]> getcitycountryByCollection(Context cntxt, String Query, String country, String GroupBy)
			throws Exception;

}
