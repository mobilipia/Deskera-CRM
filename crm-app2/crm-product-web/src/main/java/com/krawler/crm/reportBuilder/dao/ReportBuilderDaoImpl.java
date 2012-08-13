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

package com.krawler.crm.reportBuilder.dao;

import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CustomReportColumns;
import com.krawler.crm.database.tables.CustomReportList;
import com.krawler.crm.utils.Constants;
import com.krawler.dao.BaseDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.KwlSpringJsonConverter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 *
 * @author Sagar A
 */
public class ReportBuilderDaoImpl extends BaseDAO implements ReportBuilderDao {

    @Override
    public KwlReturnObject getCustomReportConfig(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = null;
            String Hql = "from CustomReportColumns c ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "where");
                Hql += filterQuery;
            }
            Hql += " order by c.displayorder ";
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("ReportBuilderDAOImpl.getCustomReportConfig", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject getModules() throws ServiceException {
        List<Object[]> list = null;
        int dl = 0;
        try{
            String query="select moduleid,modulename from modules order by modulename ";
            list = executeNativeQuery(query);
            dl = list.size();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);            
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", list, dl);
    }

    public KwlReturnObject getRendererFunctions() throws ServiceException {
        List<Object[]> list = null;
        int dl = 0;
        try{
            String hql = "select renderer.id,renderer.name,renderer.rendererValue,renderer.isstatic from com.krawler.esp.hibernate.impl.renderer as renderer";
            list = executeQuery(hql);
            dl = list.size();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", list, dl);
    }

    public KwlReturnObject saveReportColumnConfig(String reportcolumnsetting, CustomReportList CustomReportListObj, String userid) throws ServiceException {
        List<Object[]> list = null;
        int dl = 0;
        try{
             boolean summaryflag = false;
             boolean groupflag = false;
             JSONArray columnSetting = new JSONArray(reportcolumnsetting);
             deleteReportColumnConfig(CustomReportListObj.getRno());
             for(int cnt = 0; cnt < columnSetting.length();cnt++) {
                JSONObject reportConfObj = columnSetting.getJSONObject(cnt);
                CustomReportColumns customReCol = new CustomReportColumns();
                customReCol.setColumname(reportConfObj.getString("name"));
                customReCol.setDefaultheader((DefaultHeader) get(DefaultHeader.class, reportConfObj.getString("headerid")));
                customReCol.setDisplayname(reportConfObj.getString("displayfield"));
                String summarytype = reportConfObj.getString("summaryType");
                if(summarytype.equals(Constants.None)) {
                    summarytype = "";
                }
                if(!summaryflag && !StringUtil.isNullOrEmpty(summarytype)) {
                    summaryflag = true;
                }
                customReCol.setSummarytype(summarytype);
                customReCol.setRenderer(reportConfObj.getString("renderer"));
                customReCol.setXtype(reportConfObj.getString("columntype"));
                customReCol.setDisplayorder(Integer.parseInt(reportConfObj.getString("displayorder")));
                customReCol.setReportno(CustomReportListObj);
                customReCol.setUsersByUpdatedbyid((User) get(User.class, userid));
                customReCol.setUsersByCreatedbyid((User) get(User.class, userid));
                customReCol.setDataIndex(reportConfObj.getString("dataindex"));
                customReCol.setRefTable(reportConfObj.getString("reftablename"));
                customReCol.setQuicksearch(reportConfObj.getBoolean("qsearch"));
                if(!groupflag && reportConfObj.getBoolean("groupflag")) {
                    groupflag = true;
                }
                customReCol.setGroupflag(reportConfObj.getBoolean("groupflag"));
                save(customReCol);

                CustomReportListObj.setSummaryflag(summaryflag);
                CustomReportListObj.setGroupflag(groupflag);
                save(CustomReportListObj);
             }
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", list, dl);
    }

    public int deleteReportColumnConfig(int rNo) throws ServiceException {
        int dl = 0;
        try{
            String hql = "delete from CustomReportColumns where reportno.rno = ?";
            dl = executeUpdate(hql, new Object[]{rNo});
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        } finally {
            return dl;
        }
    }

    public KwlReturnObject saveReportDesc(Map<String,Object> requestParams) throws ServiceException {
        List list = new ArrayList();
        int dl = 0;
        try {
            CustomReportList reportinfo = null;
            String userid = null;
            boolean isNew = false;
            if(requestParams.containsKey("id")) {
                String ID = requestParams.get("id").toString();
                if(ID.equals("0")) {
                    reportinfo = new CustomReportList();
                    isNew = true;
                } else {
                    reportinfo = (CustomReportList) get(CustomReportList.class, ID);
                }
            }
            if(requestParams.containsKey("rname")) {
                reportinfo.setRname(requestParams.get("rname").toString());
            }
            if(requestParams.containsKey("rdescription")) {
                reportinfo.setRdescription(requestParams.get("rdescription").toString());
            }
            if(requestParams.containsKey("rcategory")&& !StringUtil.isNullObject(requestParams.get("rcategory"))) {
                reportinfo.setRcategory(requestParams.get("rcategory").toString());
            }
            if(requestParams.containsKey("rfilterjson")&& !StringUtil.isNullObject(requestParams.get("rfilterjson"))) {
                reportinfo.setRfilterjson(requestParams.get("rfilterjson").toString());
            }
            if(requestParams.containsKey("userid")) {
                userid = requestParams.get("userid").toString();
                reportinfo.setUsersByUpdatedbyid((User) get(User.class, userid));
                if(isNew)
                    reportinfo.setUsersByCreatedbyid((User) get(User.class, userid));
            }

            reportinfo.setUpdatedon(new Date().getTime());

            if(isNew) {
                reportinfo.setCreatedon(new Date().getTime());
            }
            save(reportinfo);
            list.add(reportinfo);
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", list, dl);
    }

    @Override
    public boolean  deleteCustomReport (int reportno) throws ServiceException {
        boolean successFlag = false;
        try {
            int dl = deleteReportColumnConfig(reportno);
            if(dl > 0) {
                String hql = "delete from CustomReportList where rno = ?";
                dl = executeUpdate(hql, new Object[]{reportno});
                if(dl > 0) {
                    successFlag = true;
                }
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("ReportBuilderDaoImpl.deleteCustomReport : " + e.getMessage(), e);
        }
        return successFlag;
    }
    
    @Override
    public KwlReturnObject getDataCustomReport(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String appendCase = "and";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            String selectInQuery = requestParams.get("query").toString();
            String countQuery = requestParams.get("countquery").toString();
            String Searchjson = "";
            if(requestParams.containsKey("searchJson") && requestParams.get("searchJson") != null) {
                Searchjson = requestParams.get("searchJson").toString();
                if (!StringUtil.isNullOrEmpty(Searchjson)) {
                    requestParams.put(Constants.Searchjson, Searchjson);
                    requestParams.put(Constants.appendCase, appendCase);
                    String mySearchFilterString = String.valueOf(StringUtil.getReportAdvanceSearchString(requestParams).get(Constants.myResult));
                    filterQuery += mySearchFilterString;
                    StringUtil.insertParamAdvanceSearchString(filter_params, Searchjson);
                }
            }
            if (requestParams.containsKey("fromDate") && requestParams.containsKey("toDate") && requestParams.containsKey("filterCombo")){
                Long fromDate =Long.parseLong(requestParams.get("fromDate").toString());
                Long toDate = Long.parseLong(requestParams.get("toDate").toString());
                String filterCombo = requestParams.get("filterCombo").toString();
                filterQuery += " and "+filterCombo+" >= ? and "+filterCombo+" <= ? ";
                filter_params.add(fromDate);
                filter_params.add(toDate);
            }
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null && requestParams.containsKey("quicksearchcol") && requestParams.get("quicksearchcol") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = (String[])requestParams.get("quicksearchcol");
                    StringUtil.insertParamSearchString(filter_params, ss, searchcol.length);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    filterQuery +=searchQuery;
                }
            }
            if(requestParams.containsKey("filterSS") && requestParams.get("filterSS") != null && requestParams.containsKey("filterCol") && requestParams.get("filterCol") != null) {
                String ss=requestParams.get("filterSS").toString();
                String filterCol=requestParams.get("filterCol").toString().replace("#", ".");
                if(!StringUtil.isNullOrEmpty(ss)){
                    if(ss.equals("(Blank)")) {
                        filterQuery += " and ( "+filterCol+" is null )";
                    } else {
                        filterQuery += " and ( "+filterCol+" = ? )";
                        filter_params.add(ss);
                    }
                }
            }
            if(requestParams.containsKey("extrafilter") && requestParams.get("extrafilter") != null){
                filterQuery += " and "+requestParams.get("extrafilter").toString();
            }
            String groupBy = "";
            if(requestParams.containsKey("groupByColumns")) {
                groupBy = " group by " +requestParams.get("groupByColumns").toString();
            }
            selectInQuery += filterQuery + groupBy;
            countQuery += filterQuery + groupBy;
            
            if(requestParams.containsKey("groupByColumns")) {
                countQuery = "select count(*) from ( " + countQuery + " ) as temp";
            }
            ArrayList order_by = null;
            ArrayList order_type = null;
            if(requestParams.containsKey("order_by"))
                order_by =(ArrayList) requestParams.get("order_by");
            if(requestParams.containsKey("order_type"))
                order_type = (ArrayList) requestParams.get("order_type");
            String orderQuery = StringUtil.orderQuery(order_by,order_type);
            if(!StringUtil.isNullOrEmpty(orderQuery)) {
                selectInQuery += orderQuery;
            }
            boolean pagingFlag = false;
            if(requestParams.containsKey("pagingFlag") && requestParams.get("pagingFlag") != null) {
                pagingFlag = Boolean.parseBoolean(requestParams.get("pagingFlag").toString());
                if(pagingFlag) {
                    ll = executeNativeQuery(countQuery, filter_params.toArray());
                    dl = Integer.parseInt(ll.get(0).toString());
                    int start = 0;
                    int limit = 25;
                    if (requestParams.containsKey("start") && requestParams.containsKey("limit")) {
                        start = Integer.parseInt(requestParams.get("start").toString());
                        limit = Integer.parseInt(requestParams.get("limit").toString());
                    }
                    filter_params.add(limit);
                    filter_params.add(start);
                    SqlRowSet rs = queryForRowSetJDBC(selectInQuery + " limit ? offset ? ", filter_params.toArray());
                    ll.clear();
                    ll.add(rs);
                } else {
                    SqlRowSet rs = queryForRowSetJDBC(selectInQuery, filter_params.toArray());
                    ll.add(rs);
                }
            }

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmLeadDAOImpl.getLeadsDataCustomReport : "+ ex.getMessage(), ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
}
