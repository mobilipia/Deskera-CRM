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
package com.krawler.spring.crm.activityModule;

import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * @author Karthik
 */
public class crmActivityReportDAOImpl extends BaseDAO implements crmActivityReportDAO
{

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityReportDAO#accountActivities
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject accountActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()))
        {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;

        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c.crmActivityMaster, c.crmAccount from CrmAccountActivity c ";
        String countHql = "select count(distinct c) from CrmAccountActivity c ";
        String conditionHql = "where c.crmAccount.deleteflag=0 and c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.isarchive= 'F' and c.crmActivityMaster.validflag= 1 and c.crmActivityMaster.company.companyID= ? ";
        Hql = Hql+conditionHql;
        countHql = countHql+conditionHql;
        if (!requestParams.containsKey("allActivities"))
        {
        Hql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "'";
        countHql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "'";
        }
        
        if(requestParams.containsKey("priorityfilter")){
        	Hql+=" and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        	countHql+=" and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        }
        
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals(""))
        {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1)
            {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to"))
                {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    countHql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (requestParams.containsKey("relatedid"))
        {
            Hql += " and c.crmAccount.accountid = ? ";
            countHql += " and c.crmAccount.accountid = ? ";
            filter_params.add(requestParams.get("relatedid").toString());
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.crmActivityMaster.usersByUserid.firstName", "c.crmActivityMaster.usersByUserid.lastName" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
            countHql += StringUtil.getSearchquery(quickSearch, searchcol, new ArrayList());
        }
        String selectInQuery = Hql;
        String selectCountInQuery = countHql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
            selectCountInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
        }
        if(!StringUtil.isNullOrEmpty(export)){
            selectCountInQuery = selectInQuery;
        }
        ll = executeQuery(selectCountInQuery, filter_params.toArray());
        
        if (StringUtil.isNullOrEmpty(export))
        {
            dl = ((Number)ll.get(0)).intValue();
            if (requestParams.containsKey("allActivities")){
                ll = executeQuery(selectInQuery, filter_params.toArray());
            }else{
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
            }
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityReportDAO#leadActivities
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject leadActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()))
        {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c.crmActivityMaster, c.crmLead from CrmLeadActivity c ";
        String countHql = "select count(distinct c) from CrmLeadActivity c ";
        String conditionHql = " where c.crmLead.deleteflag=0 and c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.isarchive= 'F' and c.crmActivityMaster.company.companyID= ?  and c.crmActivityMaster.validflag=1 " ;
        Hql = Hql+conditionHql;
        countHql = countHql+conditionHql;
        if (!requestParams.containsKey("allActivities"))
        {
            Hql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "' ";
            countHql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "' ";
        }
        
        if(requestParams.containsKey("priorityfilter")){
        	Hql+=" and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        	countHql += " and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        }
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals(""))
        {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1)
            {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to"))
                {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    countHql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (requestParams.containsKey("relatedid"))
        {
            Hql += " and c.crmLead.leadid = ? ";
            countHql += " and c.crmLead.leadid = ? ";
            filter_params.add(requestParams.get("relatedid").toString());
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.crmActivityMaster.usersByUserid.firstName", "c.crmActivityMaster.usersByUserid.lastName" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
            countHql += StringUtil.getSearchquery(quickSearch, searchcol, new ArrayList());
        }
        String selectInQuery = Hql;
        String selectCountInQuery = countHql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
            selectCountInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
        }
        if(!StringUtil.isNullOrEmpty(export)){
            selectCountInQuery = selectInQuery;
        }
        ll = executeQuery(selectCountInQuery, filter_params.toArray());
        
        if (StringUtil.isNullOrEmpty(export))
        {
            dl = ((Number)ll.get(0)).intValue();
            if (requestParams.containsKey("allActivities")){
                ll = executeQuery(selectInQuery, filter_params.toArray());
            }else{            
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
            }
        }

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityReportDAO#contactActivities
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject contactActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()))
        {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c.crmActivityMaster, c.crmContact from CrmContactActivity c ";
        String countHql = "select count(distinct c) from CrmContactActivity c  ";
        String conditionHql = " where c.crmContact.deleteflag=0 and  c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.isarchive= 'F' and c.crmActivityMaster.company.companyID= ?  and c.crmActivityMaster.validflag=1 ";
        Hql = Hql+conditionHql;
        countHql = countHql+conditionHql;
        if (!requestParams.containsKey("allActivities"))
        {
            Hql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "' ";
            countHql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "' ";
        }
        
        if(requestParams.containsKey("priorityfilter")){
        	Hql+=" and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        	countHql += " and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        }
        
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals(""))
        {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1)
            {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to"))
                {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    countHql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (requestParams.containsKey("relatedid"))
        {
            Hql += " and c.crmContact.contactid = ? ";
            countHql += " and c.crmContact.contactid = ? ";
            filter_params.add(requestParams.get("relatedid").toString());
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.crmActivityMaster.usersByUserid.firstName", "c.crmActivityMaster.usersByUserid.lastName" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
            countHql += StringUtil.getSearchquery(quickSearch, searchcol, new ArrayList());
        }
        String selectInQuery = Hql;
        String selectCountInQuery = countHql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
            selectCountInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
        }
        if(!StringUtil.isNullOrEmpty(export)){
            selectCountInQuery = selectInQuery;
        }
        ll = executeQuery(selectCountInQuery, filter_params.toArray());
        
        if (StringUtil.isNullOrEmpty(export))
        {
            dl = ((Number)ll.get(0)).intValue();
            if (requestParams.containsKey("allActivities")){
                ll = executeQuery(selectInQuery, filter_params.toArray());
            }else{
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
            }
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.krawler.spring.crm.activityModule.crmActivityReportDAO#
     * opportunityActivities(java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject opportunityActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()))
        {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c.crmActivityMaster, c.crmOpportunity from CrmOpportunityActivity c ";
        String countHql = "select  count(distinct c) from CrmOpportunityActivity c  ";
        String conditionHql = " where c.crmOpportunity.deleteflag=0 and c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.isarchive= 'F' and c.crmActivityMaster.company.companyID= ?  and c.crmActivityMaster.validflag=1 ";
        Hql =Hql+conditionHql;
        countHql = countHql+conditionHql;
        if (!requestParams.containsKey("allActivities"))
        {
            Hql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "' ";
            countHql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "' ";
        }
        
        if(requestParams.containsKey("priorityfilter")){
        	Hql+=" and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        	countHql += " and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        }
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals(""))
        {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1)
            {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to"))
                {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    countHql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (requestParams.containsKey("relatedid"))
        {
            Hql += " and c.crmOpportunity.oppid = ? ";
            countHql += " and c.crmOpportunity.oppid = ? ";
            filter_params.add(requestParams.get("relatedid").toString());
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.crmActivityMaster.usersByUserid.firstName", "c.crmActivityMaster.usersByUserid.lastName" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
            countHql += StringUtil.getSearchquery(quickSearch, searchcol, new ArrayList());
        }
        String selectInQuery = Hql;
        String selectCountInQuery = countHql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
            selectCountInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
        }
        if(!StringUtil.isNullOrEmpty(export)){
            selectCountInQuery = selectInQuery;
        }
        ll = executeQuery(selectCountInQuery, filter_params.toArray());
        
        if (StringUtil.isNullOrEmpty(export))
        {
            dl = ((Number)ll.get(0)).intValue();
            if (requestParams.containsKey("allActivities")){
                ll = executeQuery(selectInQuery, filter_params.toArray());
            }else{
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
            }
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityReportDAO#caseActivities
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject caseActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()))
        {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c.crmActivityMaster, c.crmCase from CrmCaseActivity c ";
        String countHql = "select count(distinct c) from CrmCaseActivity c  ";
        String conditionHql  = " where c.crmCase.deleteflag=0 and  c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.isarchive= 'F' and c.crmActivityMaster.company.companyID= ? and c.crmActivityMaster.validflag=1 ";
        Hql = Hql+conditionHql;
        countHql = countHql+conditionHql;
        if (!requestParams.containsKey("allActivities"))
        {
            Hql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "'  ";
            countHql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "'  ";
        }
        
        if(requestParams.containsKey("priorityfilter")){
        	Hql+=" and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        	countHql += " and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        }

        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals(""))
        {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1)
            {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to"))
                {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    countHql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (requestParams.containsKey("relatedid"))
        {
            Hql += " and c.crmCase.caseid = ? ";
            countHql += " and c.crmCase.caseid = ? ";
            filter_params.add(requestParams.get("relatedid").toString());
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.crmActivityMaster.usersByUserid.firstName", "c.crmActivityMaster.usersByUserid.lastName" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
            countHql += StringUtil.getSearchquery(quickSearch, searchcol, new ArrayList());
        }
        String selectInQuery = Hql;
        String selectCountInQuery = countHql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
            selectCountInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
        }
        if(!StringUtil.isNullOrEmpty(export)){
            selectCountInQuery = selectInQuery;
        }
        ll = executeQuery(selectCountInQuery, filter_params.toArray());
        
        if (StringUtil.isNullOrEmpty(export))
        {
            dl = ((Number)ll.get(0)).intValue();
            if (requestParams.containsKey("allActivities")){
                ll = executeQuery(selectInQuery, filter_params.toArray());
            }else{
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
            }
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityReportDAO#campaignActivities
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject campaignActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()))
        {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c.crmActivityMaster, c.crmCampaign from CrmCampaignActivity c  ";
        String countHql = "select count(distinct c) from CrmCampaignActivity c  ";
        String conditionHql = " where c.crmCampaign.deleteflag=0 and c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.isarchive= 'F' and c.crmActivityMaster.company.companyID= ? and  c.crmActivityMaster.validflag=1 ";
        Hql = Hql+conditionHql;
        countHql = countHql+conditionHql;
        if (!requestParams.containsKey("allActivities"))
        {
            Hql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "' ";
            countHql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "' ";
        }
        
        if(requestParams.containsKey("priorityfilter")){
        	Hql+=" and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        	countHql+=" and c.crmActivityMaster.crmCombodataByPriorityid.ID='"+ requestParams.get("priorityfilter").toString()+"'";
        }
        filter_params.add(companyid);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals(""))
        {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1)
            {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to"))
                {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    countHql += " and c.crmActivityMaster.createdOn >= ? and c.crmActivityMaster.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (requestParams.containsKey("relatedid"))
        {
            Hql += " and c.crmCampaign.campaignid = ? ";
            countHql += " and c.crmCampaign.campaignid = ? ";
            filter_params.add(requestParams.get("relatedid").toString());
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.crmActivityMaster.usersByUserid.firstName", "c.crmActivityMaster.usersByUserid.lastName" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
            countHql += StringUtil.getSearchquery(quickSearch, searchcol, new ArrayList());
        }
        String selectInQuery = Hql;
        String selectCountInQuery = countHql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
            selectCountInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
        }
        if(!StringUtil.isNullOrEmpty(export)){
            selectCountInQuery = selectInQuery;
        }
        ll = executeQuery(selectCountInQuery, filter_params.toArray());
        
        if (StringUtil.isNullOrEmpty(export))
        {
            dl = ((Number)ll.get(0)).intValue();
            if (requestParams.containsKey("allActivities")){
                ll = executeQuery(selectInQuery, filter_params.toArray());
            }else{
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
            }
        }

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.krawler.spring.crm.activityModule.crmActivityReportDAO#
     * getHighPriorityActivityChart(java.lang.String, java.util.HashMap,
     * java.lang.StringBuffer)
     */
    public KwlReturnObject getHighPriorityActivityChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c.crmActivityMaster, c.crm" + name + " from Crm" + name + "Activity c where c.crm" + name +".deleteflag=0 and c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.isarchive= 'F' and c.crmActivityMaster.validflag= 1 and c.crmActivityMaster.company.companyID= ? ";
        Hql += " and c.crmActivityMaster.crmCombodataByPriorityid.mainID= '" + Constants.CASEPRIORITY_HIGH + "' and c.crmActivityMaster.crmCombodataByStatusid.mainID='" + Constants.CASESTATUS_NOTSTARTED + "' and c.crmActivityMaster.validflag=1 ";

        params = new Object[] { companyid };
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ")  ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
}
