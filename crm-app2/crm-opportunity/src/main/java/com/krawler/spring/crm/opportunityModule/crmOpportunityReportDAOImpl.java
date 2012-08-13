/*
 * Copyright (C) 2012  Krawler Information Systems Pvt Ltd
 * All rights reserved.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package com.krawler.spring.crm.opportunityModule;

import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Karthik
 */
public class crmOpportunityReportDAOImpl extends BaseDAO implements crmOpportunityReportDAO {

    final static int probabilityForOppByRegionF = 75;

    @Override
    public KwlReturnObject revenueByOppSourceReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        String filterCombo = requestParams.containsKey("filterCombo") ? requestParams.get("filterCombo").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag=0  and c.validflag=1 and c.salesamount != ' ' and c.salesamount != 0 and c.crmCombodataByLeadsourceid is not NULL ";
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.oppname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        if (!StringUtil.isNullOrEmpty(filterCombo)) {
            Hql += " and c.crmCombodataByLeadsourceid.ID='" + filterCombo + "' ";
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by c.crmCombodataByOppstageid.value ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject oppByStageReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        String filterCombo = requestParams.containsKey("filterCombo") ? requestParams.get("filterCombo").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag=0  and c.validflag=1 ";
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.oppname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        if (!StringUtil.isNullOrEmpty(filterCombo)) {
            Hql += " and c.crmCombodataByOppstageid.ID='" + filterCombo + "' ";
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by c.crmCombodataByOppstageid.value ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject oppBySalesPersonReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c " +
                "where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag=0  and c.validflag=1 ";
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.closingdate >= ? and c.closingdate <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.oppname", "oo.usersByUserid.firstName", "oo.usersByUserid.lastName"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by oo.usersByUserid.firstName ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public List<Object[]> oppBySalesPersonCountList(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        ArrayList filter_params = new ArrayList();
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select count(oo.id) as count, oo.usersByUserid from opportunityOwners oo inner join oo.opportunity c " +
                "where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag=0  and c.validflag=1 and oo.mainOwner = ? ";
        filter_params.add(companyid);
        filter_params.add(true);
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " group by oo.usersByUserid ";
//        selectInQuery += " order by oo.usersByUserid.firstName ";
        List<Object[]> ll = executeQuery(selectInQuery, filter_params.toArray());
        return ll;
    }

    @Override
    public KwlReturnObject oppByRegionHReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c.crmCombodataByRegionid.id, c.crmCombodataByRegionid.value as value, oo.usersByUserid.userID, " +
                "oo.usersByUserid.firstName||' '||oo.usersByUserid.lastName as name, sum(COALESCE(c.salesamount, 0)*1) as salesamount, count(c.oppid) as oppcount " +
                "from opportunityOwners oo inner join oo.opportunity c left join c.crmCombodataByRegionid r " +
                "where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag=0  and c.validflag=1 and oo.mainOwner = ?  ";
        filter_params.add(companyid);
        filter_params.add(true);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"oo.usersByUserid.firstName", "oo.usersByUserid.lastName", "c.crmCombodataByRegionid.value"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " group by c.crmCombodataByRegionid, oo.usersByUserid.userID ";
//        selectInQuery += " order by oo.crmCombodataByRegionid ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public long oppByRegionwiseFinalStageCount(HashMap<String, Object> requestParams) throws ServiceException {
        ArrayList filter_params = new ArrayList();
        String companyid = requestParams.get("companyid").toString();
        String userid = requestParams.get("ownerid").toString();
        String regionid = requestParams.get("regionid").toString();

        String Hql = "select count(c.oppid) as count from opportunityOwners oo inner join oo.opportunity c " +
                "where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag=0  and c.validflag=1 " +
                "and oo.mainOwner = ? and oo.usersByUserid.userID = ? and c.crmCombodataByOppstageid.mainID = ? ";
        Hql += StringUtil.isNullOrEmpty(regionid)?" and c.crmCombodataByRegionid is NULL ":" and c.crmCombodataByRegionid = '"+regionid+"' ";

        filter_params.add(companyid);
        filter_params.add(true);
        filter_params.add(userid);
        filter_params.add(Constants.OPPSTAGEID_FINAL);
        
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }

        String selectInQuery = Hql;
        List<Object> ll = executeQuery(selectInQuery, filter_params.toArray());
        long count = 0;
        if(!ll.isEmpty()) {
            count = ((Number)ll.get(0)).longValue();
        }
        return count;
    }

    @Override
    public List<Object[]> oppByRegionCountList(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        ArrayList filter_params = new ArrayList();
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select count(distinct c.oppid) as count, c.crmCombodataByRegionid from opportunityOwners oo inner join oo.opportunity c left join c.crmCombodataByRegionid r " +
                "where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag=0  and c.validflag=1 " +
                "and oo.mainOwner = ? ";

        filter_params.add(companyid);
        filter_params.add(true);
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " group by c.crmCombodataByRegionid ";
//        selectInQuery += " order by oo.usersByUserid.firstName ";
        List<Object[]> ll = executeQuery(selectInQuery, filter_params.toArray());
        return ll;
    }

    @Override
    public List<Object[]> oppSalesamountDashboardPieChart(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        ArrayList filter_params = new ArrayList();
        String companyid = requestParams.get("companyid").toString();
        String groupbyField = requestParams.get("groupbyField").toString();//c.crmCombodataByRegionid
        String idField = requestParams.get("idField").toString();//c.crmCombodataByRegionid.ID
        String valueField = requestParams.get("valueField").toString();//c.crmCombodataByRegionid.value
        String Hql = " select sum(COALESCE(c.salesamount, 0)) as salesamount, " + idField + ", "+ valueField +
                " from opportunityOwners oo inner join oo.opportunity c " +
                (groupbyField.equals("oo.usersByUserid")?"":" left join "+ groupbyField +" r ") +
                " where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag=0  and c.validflag=1 " +
                " and oo.mainOwner = ? ";

        filter_params.add(companyid);
        filter_params.add(true);
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.closingdate >= ? and c.closingdate < ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        selectInQuery += " group by "+ groupbyField;
        List<Object[]> ll = executeQuery(selectInQuery, filter_params.toArray());
        return ll;
    }

    /**
     * This function is used to calculate average gross margine for secutech company.
     * @param requestParams
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    @Override
    public Object getCustomColumnAvgGM(HashMap<String, Object> requestParams) throws ServiceException {
        List<Object> ll = null;
        Object count = 0;
        try {
            String fieldParamId = requestParams.get("fieldParamId").toString();
            String companyid = requestParams.get("companyid").toString();
            String regionid = requestParams.get("regionid").toString();
            String userid = requestParams.get("ownerid").toString();
            ArrayList filter_params = new ArrayList();
            filter_params.add(fieldParamId);
            String Hql = "select dbcolumnname from default_header dh where pojoheadername = ? ";
            ll = executeNativeQuery(Hql, filter_params.toArray());
            String custom_col_name = "";
            for(Object name : ll) {
                custom_col_name = name.toString().toLowerCase();
            }

            Hql = "select avg(ocd."+custom_col_name+") " +
                    "from opportunityOwners oo inner join oo.opportunity c inner join c.CrmOpportunityCustomDataobj ocd left join c.crmCombodataByRegionid r " +
                "where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag=0  and c.validflag=1 " +
                " and oo.mainOwner = ? and oo.usersByUserid.userID = ?  ";
            Hql += StringUtil.isNullOrEmpty(regionid)?" and c.crmCombodataByRegionid is NULL ":" and c.crmCombodataByRegionid = '"+regionid+"' ";
            filter_params.clear();
            filter_params.add(companyid);
            filter_params.add(true);
            filter_params.add(userid);

            if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
                int chk = Integer.parseInt(requestParams.get("cd").toString());
                if (chk == 1) {
                    if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                    	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
    					Long toDate = Long.parseLong(requestParams.get("to").toString());
                        Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                        filter_params.add(fromDate);
                        filter_params.add(toDate);
                    }
                }
            }
            ll = executeQuery(Hql, filter_params.toArray());
            for(Object count1 : ll) {
                count = count1;
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getCustomColumnAvgGM", ex);
        }
        return count;
    }

    @Override
    public KwlReturnObject oppByRegionFunnelReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        boolean projFlag = requestParams.containsKey("projFlag") ? Boolean.parseBoolean(requestParams.get("projFlag").toString()) : false;
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c.crmCombodataByRegionid.id, c.crmCombodataByRegionid.value as value,  sum(COALESCE(c.salesamount, 0)) as salesamount " +
                "from opportunityOwners oo inner join oo.opportunity c left join c.crmCombodataByRegionid r " +
                "where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag=0  and c.validflag=1 and oo.mainOwner = ?  ";
        filter_params.add(companyid);
        filter_params.add(true);
        if(projFlag) {
            Hql += " and cast(COALESCE(c.probability, 0) as integer) >= ? ";
            filter_params.add(probabilityForOppByRegionF);
        }
        
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.closingdate >= ? and c.closingdate < ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"oo.usersByUserid.firstName", "oo.usersByUserid.lastName", "c.crmCombodataByRegionid.value"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " group by c.crmCombodataByRegionid ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export) && !projFlag) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject closedOppReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where c.isarchive= 'F' and c.deleteflag=0 and c.company.companyID= ? and c.crmCombodataByOppstageid.mainID='" + Constants.OPPSTAGEID_CLOSEDWON + "' and c.validflag=1";
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.oppname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by c.createdOn ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject oppByTypeReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        String filterCombo = requestParams.containsKey("filterCombo") ? requestParams.get("filterCombo").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where c.isarchive= 'F' and c.deleteflag=0 and c.company.companyID= ? and c.crmCombodataByOpptypeid  is not NULL and c.validflag=1";
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.oppname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        if (!StringUtil.isNullOrEmpty(filterCombo)) {
            Hql += " and c.crmCombodataByOpptypeid.ID='" + filterCombo + "' ";
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by c.crmCombodataByOpptypeid.value ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject stuckOppReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where c.isarchive= 'F' and c.deleteflag=0 and c.company.companyID= ? and c.probability<50 and c.validflag=1";
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.oppname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by c.createdOn ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject sourceOfOppReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c left join c.crmCombodataByLeadsourceid ls where c.isarchive= 'F' and c.deleteflag=0 and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid is not NULL ";
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.oppname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by ls.value ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject oppByLeadSourceReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid is not NULL ";
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.oppname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by c.crmCombodataByLeadsourceid ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject oppProductReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from OppurtunityProducts op inner join op.oppid c inner join c.oppOwners oo where c.deleteflag=0 and c.company.companyID= ?  and c.validflag=1"
                + "  and c.isarchive=false and op.productId.deleteflag = 0 and op.productId.isarchive = false and op.productId.validflag = 1";
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.oppname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by c.createdOn ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject salesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where  c.isarchive= 'F' and c.company.companyID= ? and c.deleteflag =  ? and c.crmCombodataByOppstageid.mainID= '" + Constants.OPPSTAGEID_CLOSEDWON + "'  and c.validflag=1  and c.crmCombodataByLeadsourceid is not null ";
        filter_params.add(companyid);
        filter_params.add(0);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.oppname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
        }
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject oppPipelineReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
            String companyid = requestParams.get("companyid").toString();
            String condition = " where  c.deleteflag=0 and c.company.companyID= ?  and c.validflag=1 and c.isarchive='F' and c.crmLead is not null ";
            filter_params.add(companyid);
            String Hql = "select c.crmCombodataByOppstageid.value, c.crmCombodataByOppstageid.percentStage, sum(c.salesamount), sum(c.price), count(*) from CrmOpportunity c  ";
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))) {
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
//				condition+=" and cast(FROM_UNIXTIME(c.createdOn/1000) as date) between cast(? as date) and cast(? as date) ";
//				filter_params.add(fromDate);
//                filter_params.add(toDate);
                condition += " and c.createdOn >= ? and c.createdOn <= ? ";
                filter_params.add(fromDate);
                filter_params.add(toDate);
            }
            String[] searchcol = new String[]{"c.crmCombodataByOppstageid.value"};
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                condition += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
            }
            String selectInQuery = Hql + condition;
            boolean heirarchyPerm = false;
            if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if (!heirarchyPerm) {
            	filter_params.add(companyid);
                StringUtil.insertParamSearchString(filter_params, quickSearch, searchcol.length);
                selectInQuery += " and c.oppid in ( select distinct c.oppid from opportunityOwners oo inner join oo.opportunity c " + condition + " and oo.usersByUserid.userID in (" + usersList + ") )  ";
            }
            selectInQuery += " group by c.crmCombodataByOppstageid.value order by (c.crmCombodataByOppstageid.percentStage*1) desc";
            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject allOppPipelineReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
            String companyid = requestParams.get("companyid").toString();
            String condition = " where  c.deleteflag=0 and c.company.companyID= ?  and c.validflag=1 and c.isarchive='F'";
            String Hql = "select c.crmCombodataByOppstageid.value, c.oppname, c.salesamount, c.price, c.crmCombodataByOppstageid.percentStage from CrmOpportunity c ";

            String[] searchcol = new String[]{"c.crmCombodataByOppstageid.value"};
            filter_params.add(companyid);
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                condition += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
            }
            String selectInQuery = Hql + condition;
            boolean heirarchyPerm = false;
            if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if (!heirarchyPerm) {
                filter_params.add(companyid);
                StringUtil.insertParamSearchString(filter_params, quickSearch, searchcol.length);
                selectInQuery += " and c.oppid in ( select distinct c.oppid from opportunityOwners oo inner join oo.opportunity c " + condition + " and oo.usersByUserid.userID in (" + usersList + ") )  ";
            }
            selectInQuery += " order by c.crmCombodataByOppstageid.value ";
            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject oppPipelineChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        ArrayList params = new ArrayList();
        List ll = null;
        String Hql = "";
        Long fromDate=null,toDate=null;
            String companyid = requestParams.get("companyid").toString();
            String condition = " where  c.deleteflag=0 and c.company.companyID= ?  and c.crmCombodataByOppstageid.ID=? and c.validflag=1 and c.isarchive='F' and c.crmLead is not null ";//and cast(FROM_UNIXTIME(c.createdOn/1000) as date) between cast(? as date) and cast(? as date) ";
            params.add(companyid);
            params.add(name);
            Hql = "select c.crmCombodataByOppstageid.value, c.crmCombodataByOppstageid.percentStage, sum(c.salesamount), sum(c.price), count(*) from CrmOpportunity c  ";
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))) {
            	 fromDate =Long.parseLong(requestParams.get("frm").toString());
				 toDate = Long.parseLong(requestParams.get("to").toString());
                condition += " and c.createdOn >= ? and c.createdOn <= ? ";
                params.add(fromDate);
                params.add(toDate);
            }
            
            //params = new Object[]{companyid, name, fromDate, toDate};

            String selectInQuery = Hql + condition;
            boolean heirarchyPerm = false;
            if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if (!heirarchyPerm) {
                selectInQuery += " and c.oppid in ( select distinct c.oppid from opportunityOwners oo inner join oo.opportunity c " + condition + " and oo.usersByUserid.userID in (" + usersList + ") )  ";
                params.add(companyid);
                params.add(name);
            }
            selectInQuery += " group by c.crmCombodataByOppstageid.value ";
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getOpportunityByTypeChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c " +
                "where c.crmCombodataByOpptypeid.ID=? and c.isarchive= 'F' and c.deleteflag=0 and c.company.companyID= ?  and c.validflag=1 ";
        params = new Object[]{name, companyid};
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getOpportunityByProductChart(String id, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        StringBuilder Hql = new StringBuilder(" from OppurtunityProducts op inner join op.oppid c inner join c.oppOwners oo "
                + " where c.deleteflag=0  and c.company.companyID= ? and c.validflag=1 and c.isarchive=false "
                + " and op.productId.deleteflag = 0  and op.productId.isarchive = false and op.productId.validflag = 1 ");
        boolean groupby = requestParams.containsKey("groupby") ;
        if(groupby){
            Hql = new StringBuilder(" select count(op.oppid) as count ,op.productId.productid as id,op.productId.productname as name ").append(Hql) ;
            params = new Object[] {companyid};
        }else{
            Hql = new StringBuilder(" select c ")
                        .append(Hql)
                        .append(" and op.productId.productid=? ");
            
            params = new Object[] {companyid,id};
        }
//        params = new Object[]{id, companyid};
        StringBuilder selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery.append(" and oo.usersByUserid.userID in (")
                    .append(usersList)
                    .append(")   ");
        }
        if(groupby){
            selectInQuery.append(" group by op.productId.productid ");
        }
        ll = executeQuery(selectInQuery.toString(), params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getStuckOpportunitiesChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c " +
                " where c.crmCombodataByOppstageid.ID=? and c.probability < 50 and c.isarchive= 'F' and c.deleteflag=0   and c.company.companyID= ?  and c.validflag=1 ";
        params = new Object[]{name, companyid};
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, "001", "", ll, dl);
    }

    @Override
    public KwlReturnObject getOpportunityBySourceChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = " select distinct c from opportunityOwners oo inner join oo.opportunity c where c.crmCombodataByLeadsourceid.ID=? and c.isarchive= 'F' and c.deleteflag=0   and c.company.companyID= ?  and c.validflag=1 and c.salesamount != ''  ";
        params = new Object[]{name, companyid};
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, "001", "", ll, dl);
    }

    @Override
    public KwlReturnObject getOpportunityByStageChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = " select distinct c from opportunityOwners oo inner join oo.opportunity c where   c.crmCombodataByOppstageid.ID=? and c.deleteflag=0   and c.company.companyID= ?  and c.validflag=1 and c.isarchive= 'F' ";
        params = new Object[]{name, companyid};
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, "001", "", ll, dl);
    }

    @Override
    public KwlReturnObject getSalesBySourceChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        ArrayList params = new ArrayList();
        List ll = null;
        SimpleDateFormat sdf=new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
        String companyid = requestParams.get("companyid").toString();
        String condition="";
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c";      
        if (!name.equals("Undefined")) {
            condition = "  where   c.crmCombodataByLeadsourceid.ID=? and c.isarchive= 'F' and c.deleteflag=0 and c.company.companyID= ? and c.crmCombodataByOppstageid.mainID='" + Constants.OPPSTAGEID_CLOSEDWON + "' and c.validflag=1 ";
            params.add(name);
            params.add(companyid);	            
        } else {
            condition = " where c.crmCombodataByLeadsourceid is null and c.isarchive= 'F' and c.deleteflag=0 and c.company.companyID= ?   and c.validflag=1  and c.crmCombodataByOppstageid.mainID='" + Constants.OPPSTAGEID_CLOSEDWON + "' ";
            params.add(companyid);
        }
        try {
            if(requestParams.containsKey("frm")&& !StringUtil.isNullOrEmpty(requestParams.get("frm").toString())){
                condition+=" and c.createdOn >= ? ";
                            params.add(Long.parseLong(requestParams.get("frm").toString()));
            }
            if(requestParams.containsKey("to")&& !StringUtil.isNullOrEmpty(requestParams.get("to").toString())){
                condition+=" and c.createdOn <= ? ";
                params.add(Long.parseLong(requestParams.get("to").toString()));
            }
        }catch (Exception e) {
            logger.warn("Error while Parsing", e);
        }
        Hql+=condition;
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params.toArray());
        dl = ll.size();

        return new KwlReturnObject(true, "001", "", ll, dl);
    }

    @Override
    public KwlReturnObject getSourceOffOppChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where c.crmCombodataByLeadsourceid.ID=? and c.isarchive= 'F' and c.deleteflag=0   and c.company.companyID= ?  and c.validflag=1 ";
        params = new Object[]{name, companyid};
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, "001", "", ll, dl);
    }

    @Override
    public KwlReturnObject getClosedOppChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where c.oppid=? and c.isarchive= 'F' and c.deleteflag=0 and c.company.companyID= ? and c.crmCombodataByOppstageid.mainID='" + Constants.OPPSTAGEID_CLOSEDWON + "' and c.validflag=1";
        params = new Object[]{name, companyid};
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, "001", "", ll, dl);
    }

    @Override
    public KwlReturnObject getLeadOpportunityChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String Hql = "";
        String companyid = requestParams.get("companyid").toString();
        if (!name.equals("Undefined")) {
            Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid.ID=? ";
            params = new Object[]{companyid, name};
        } else {
            Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid is null ";
            params = new Object[]{companyid};
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, "001", "", ll, dl);
    }

    @Override
    public KwlReturnObject accountsWithOpportunityReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select distinct co from CrmOpportunity co inner join co.crmAccount ca inner join co.oppOwners coo inner join ca.accountOwners cao where co.company.companyID= ? and co.deleteflag=0 and co.validflag=1 and co.isarchive='F' and ca.validflag=1 and ca.deleteflag=0 and ca.isarchive='F' ";
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
    				Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and co.createdOn >= ? and co.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"co.crmAccount.accountname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPermOpp = false;
        boolean heirarchyPermAcc = false;
        if (requestParams.containsKey("heirarchyPermOpp") && requestParams.get("heirarchyPermOpp") != null) {
            heirarchyPermOpp = Boolean.parseBoolean(requestParams.get("heirarchyPermOpp").toString());
        }
        if (!heirarchyPermOpp) {
            selectInQuery += " and coo.usersByUserid.userID in (" + usersList + ") ";
        }
        if (requestParams.containsKey("heirarchyPermAcc") && requestParams.get("heirarchyPermAcc") != null) {
            heirarchyPermAcc = Boolean.parseBoolean(requestParams.get("heirarchyPermAcc").toString());
        }
        if (!heirarchyPermAcc) {
            selectInQuery += " and cao.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by co.crmAccount.accountname ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    //Account report
    @Override
    public KwlReturnObject getAccountOpportunityChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = " from opportunityOwners oo inner join oo.opportunity c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 ";
//        String Hql = " from  CrmOpportunity c inner join c.crmAccount ca inner join c.oppOwners oo inner join ca.accountOwners cao" +
//                        " where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 ";
        boolean groupby = requestParams.containsKey("groupby") ;
        if(groupby){
            Hql = " select count(distinct c.oppid) as count ,c.crmAccount.accountid as id,c.crmAccount.accountname as name "+Hql +" and c.crmAccount.deleteflag=0 and c.crmAccount.isarchive= 'F' and c.crmAccount.validflag=1 ";
            params = new Object[] {companyid};
        }else{
            Hql += " and c.crmAccount.accountid=? ";
            params = new Object[] { companyid, name };
        }

        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery = Hql + " and oo.usersByUserid.userID in (" + usersList + ")   ";
        }
         if(groupby){
            selectInQuery += " group by c.crmAccount.accountid ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    //Lead Report
    @Override
    public KwlReturnObject convertedLeadsToOpportunityReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        try {
            String companyid = requestParams.get("companyid").toString();
            String Hql = "select distinct co From CrmOpportunity co inner join co.crmLead cl inner join cl.leadOwners clo inner join co.oppOwners coo where co.company.companyID = ? and co.deleteflag = 0 and co.validflag=1 and co.isarchive= 'F' and cl.deleteflag=0 and cl.validflag =1 and cl.isarchive='F' and cl.isconverted=1 ";
            filter_params.add(companyid);
            if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
                int chk = Integer.parseInt(requestParams.get("cd").toString());
                if (chk == 1) {
                    if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                    	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
        				Long toDate = Long.parseLong(requestParams.get("to").toString());
                        Hql += " and cl.createdOn >= ? and cl.createdOn <= ? ";
                        filter_params.add(fromDate);
                        filter_params.add(toDate);
                    }
                }
            }
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                String[] searchcol = new String[]{"cl.lastname"};
                Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
            }
            String selectInQuery = Hql;
            boolean heirarchyPermOpp = false;
            if (requestParams.containsKey("heirarchyPermOpp") && requestParams.get("heirarchyPermOpp") != null) {
                heirarchyPermOpp = Boolean.parseBoolean(requestParams.get("heirarchyPermOpp").toString());
            }
            if (!heirarchyPermOpp) {
                selectInQuery = Hql + " and coo.usersByUserid.userID in (" + usersList + ")   ";
            }
            boolean heirarchyPermLea = false;
            if (requestParams.containsKey("heirarchyPermLea") && requestParams.get("heirarchyPermLea") != null) {
                heirarchyPermLea = Boolean.parseBoolean(requestParams.get("heirarchyPermLea").toString());
            }
            if (!heirarchyPermLea) {
                selectInQuery += " and clo.usersByUserid.userID in (" + usersList + ")   ";
            }
            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmLeadReportDAOImpl.convertedLeadsToOpportunityReport", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
}
