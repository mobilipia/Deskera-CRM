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
package com.krawler.spring.crm.caseModule;

import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * @author Karthik
 */
public class crmCaseReportDAOImpl extends BaseDAO implements crmCaseReportDAO
{

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#caseByStatusReport
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject caseByStatusReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        String filterCombo = requestParams.containsKey("filterCombo") ? requestParams.get("filterCombo").toString() : "";
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
        String selQuery = "select c ";
        String selCountQuery = "select count(c) ";
        String Hql = "from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ?   and c.validflag=1 ";
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
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.subject" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        if (!StringUtil.isNullOrEmpty(filterCombo)) {
            Hql += " and c.crmCombodataByCasestatusid.ID='" + filterCombo + "' ";
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
        }
        if(!StringUtil.isNullOrEmpty(export)){
        	selCountQuery = selQuery;
        }
        
        ll = executeQuery(selCountQuery+selectInQuery, filter_params.toArray());
        
        if (StringUtil.isNullOrEmpty(export))
        {
        	dl = ((Number)ll.get(0)).intValue();
            ll = executeQueryPaging(selQuery+selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#monthlyCasesReport
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject monthlyCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = 0;
        if (requestParams.containsKey("month"))
        {
            month = Integer.valueOf(requestParams.get("month").toString());
        }

        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()))
        {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        if (requestParams.containsKey("year") && !StringUtil.isNullOrEmpty(requestParams.get("year").toString()))
        {
            year = Integer.parseInt(requestParams.get("year").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String selQuery = "select c ";
        String selCountQuery = "select count(c) ";
        String Hql = "from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and YEAR(FROM_UNIXTIME(c.createdOn/1000)) = ? ";
        filter_params.add(companyid);
        filter_params.add(year);
        if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals(""))
        {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1)
            {
                if (requestParams.containsKey("frm") && requestParams.containsKey("to"))
                {
                	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.subject" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        if (month > 0 && month < 13)
        {
            Hql += " and MONTH(FROM_UNIXTIME(c.createdOn/1000))= " + month;
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by c.createdOn ";
        
        if(!StringUtil.isNullOrEmpty(export)){
        	selCountQuery = selQuery;
        }

        ll = executeQuery(selCountQuery+selectInQuery, filter_params.toArray());
        
        if (StringUtil.isNullOrEmpty(export))
        {
        	dl = ((Number)ll.get(0)).intValue();
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
        }

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
//TODO vishnu kant gupta
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#newlyAddedCasesReport
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject newlyAddedCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
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
        String Hql = "select c from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCasestatusid.mainID = '" + Constants.CASESTATUS_NEWCASE + "'";
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
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.subject" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
        }
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export))
        {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#pendingCasesReport
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject pendingCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
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
        String Hql = "select c from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCasestatusid.mainID = '" + Constants.CASESTATUS_PENDING + "'";
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
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.subject" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
        }
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export))
        {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
        }

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#escalatedCasesReport
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject escalatedCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
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
        String Hql = "select c from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCasestatusid.mainID = '" + Constants.CASESTATUS_ESCALATED + "'";
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
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.subject" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
        }
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export))
        {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
        }

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#getCasesByStatusChart
     * (java.lang.String, java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject getCasesByStatusChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "from CrmCase c where   c.crmCombodataByCasestatusid.ID=? and c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ?  and c.validflag=1 ";
        params = new Object[] { name, companyid };
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#getMonthlyCasesChart
     * (int, java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject getMonthlyCasesChart(int month, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        String companyid = requestParams.get("companyid").toString();
        if (requestParams.containsKey("year"))
        {
            year = Integer.parseInt(requestParams.get("year").toString());
        }

        String Hql = "select c from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and MONTH(FROM_UNIXTIME(c.createdOn/1000))=? and YEAR(FROM_UNIXTIME(c.createdOn/1000))=?";
        params = new Object[] { companyid, month, year };
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#getNewlyAddedCasesChart
     * (java.lang.String, java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject getNewlyAddedCasesChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCasepriorityid.ID =? and c.crmCombodataByCasestatusid.mainID='" + Constants.CASESTATUS_NEWCASE + "'";
        params = new Object[] { companyid, name };
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#getPendingCasesChart
     * (java.lang.String, java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject getPendingCasesChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCasepriorityid.ID =? and c.crmCombodataByCasestatusid.mainID='" + Constants.CASESTATUS_PENDING + "'";
        params = new Object[] { companyid, name };
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#getEscalatedCasesChart
     * (java.lang.String, java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject getEscalatedCasesChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCasepriorityid.ID =? and c.crmCombodataByCasestatusid.mainID='" + Constants.CASESTATUS_ESCALATED + "'";
        params = new Object[] { companyid, name };
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#contactsWithCasesReport
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject contactsWithCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
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
        String Hql = "select distinct c from CrmCase c inner join c.crmContact cc inner join cc.contactOwners co where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and cc.deleteflag =0 and cc.validflag=1 and cc.isarchive= 'F'";
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
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.subject" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPermCas = false;
        if (requestParams.containsKey("heirarchyPermCas") && requestParams.get("heirarchyPermCas") != null)
        {
            heirarchyPermCas = Boolean.parseBoolean(requestParams.get("heirarchyPermCas").toString());
        }
        if (!heirarchyPermCas)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
        }
        boolean heirarchyPermCon = false;
        if (requestParams.containsKey("heirarchyPermCon") && requestParams.get("heirarchyPermCon") != null)
        {
            heirarchyPermCon = Boolean.parseBoolean(requestParams.get("heirarchyPermCon").toString());
        }
        if (!heirarchyPermCon)
        {
            selectInQuery += " and co.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by cc.lastname ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export))
        {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
        }

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#getContactCaseChart
     * (java.lang.String, java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject getContactCaseChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmContact.contactid=?";
        params = new Object[] { companyid, name };
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")  ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.krawler.spring.crm.caseModule.crmCaseReportDAO#
     * getContactHighPriorityChart(java.lang.String, java.util.HashMap,
     * java.lang.StringBuffer)
     */
    public KwlReturnObject getContactHighPriorityChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCasepriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "'";
        params = new Object[] { companyid};
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")  ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.krawler.spring.crm.caseModule.crmCaseReportDAO#
     * contactsHighPriorityCasesReport(java.util.HashMap,
     * java.lang.StringBuffer)
     */
    public KwlReturnObject contactsHighPriorityCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
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
        String Hql = "select c.crmContact from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCasepriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' ";
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
                    Hql += " and c.crmContact.createdOn >= ? and c.crmContact.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.usersByUserid.firstName", "c.usersByUserid.lastName" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
        }
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export))
        {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#accountsWithCaseReport
     * (java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject accountsWithCaseReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
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
        String Hql = "select c from CrmCase c inner join c.crmAccount ca where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 ";
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
                    Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.subject" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPermCas = false;
        if (requestParams.containsKey("heirarchyPermCas") && requestParams.get("heirarchyPermCas") != null)
        {
            heirarchyPermCas = Boolean.parseBoolean(requestParams.get("heirarchyPermCas").toString());
        }
        if (!heirarchyPermCas)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export))
        {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.krawler.spring.crm.caseModule.crmCaseReportDAO#
     * accountHighPriorityCasesReport(java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject accountHighPriorityCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
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
        String priorityId = requestParams.get("priorityid").toString();
        String Hql = "select c.crmAccount, c from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmAccount.isarchive='F' and c.crmAccount.deleteflag=0 and c.crmAccount.validflag=1 ";
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
                    Hql += " and c.crmAccount.createdOn >= ? and c.crmAccount.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(priorityId))
        {
            Hql += " and c.crmCombodataByCasepriorityid.ID = '" + priorityId + "' ";
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "c.crmAccount.accountname" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export))
        {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseReportDAO#getAccountCasesChart
     * (java.lang.String, java.util.HashMap, java.lang.StringBuffer)
     */
    @Override
    public KwlReturnObject getAccountCasesChart(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        StringBuilder Hql =null;
        
        String companyid = requestParams.get("companyid").toString();

        Hql =  new StringBuilder("select count(c.caseid) as count ,c.crmAccount.accountid as id,c.crmAccount.accountname as name from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 ");
        params = new Object[] { companyid };

        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());

        if (!heirarchyPerm)
            Hql.append(" and c.usersByUserid.userID in (").append(usersList).append(")  ");

        Hql.append("  group by c.crmAccount.accountid  ");
        ll = executeQuery(Hql.toString(), params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.krawler.spring.crm.caseModule.crmCaseReportDAO#
     * getAccountHighPriorityChart(java.lang.String, java.util.HashMap,
     * java.lang.StringBuffer)
     */
    public KwlReturnObject getAccountHighPriorityChart(String id, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "from CrmCase c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmAccount.isarchive='F' and c.crmAccount.deleteflag=0 and c.crmAccount.validflag=1 ";
        boolean groupby = requestParams.containsKey("groupby") ;
        if(groupby){
            Hql = " select count(c.caseid) as count ,c.crmAccount.accountid as id,c.crmAccount.accountname as name "+Hql ;
            params = new Object[] {companyid};
        }else{
            Hql += " and c.crmAccount.accountid=? ";
            params = new Object[] { companyid, id };
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        selectInQuery += " and c.crmCombodataByCasepriorityid.mainID = '" + Constants.CASEPRIORITY_HIGH + "' ";

        if (!heirarchyPerm)
        {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")   ";
        }
        if(groupby){
            selectInQuery += " group by c.crmAccount.accountid ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /**
     * 
     * @param requestParams
     * @param usersList
     * @return KwlReturnObject
     * @throws com.krawler.common.service.ServiceException
     */
    public KwlReturnObject productCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
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
        String priorityId = "";
        if (requestParams.containsKey("priorityId"))
        {
            priorityId = requestParams.get("priorityId").toString();
        }

        String Hql = "select cp.productId , cp.caseid from CaseProducts cp where cp.caseid.deleteflag=0 and cp.caseid.isarchive= 'F' and cp.caseid.company.companyID= ? and cp.caseid.validflag=1  and cp.productId.productid is not NULL";
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
                    Hql += " and cp.caseid.createdOn >= ? and cp.caseid.createdOn <= ? ";
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                }
            }
        }
        if (!StringUtil.isNullOrEmpty(priorityId))
        {
            Hql += " and cp.caseid.crmCombodataByCasepriorityid.ID = '" + priorityId + "' ";
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            String[] searchcol = new String[] { "cp.productId.productname" };
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and cp.caseid.usersByUserid.userID in (" + usersList + ")   ";
        }
        boolean heirarchyPermPro = false;
        if (requestParams.containsKey("heirarchyPermPro") && requestParams.get("heirarchyPermPro") != null)
        {
            heirarchyPermPro = Boolean.parseBoolean(requestParams.get("heirarchyPermPro").toString());
        }
        if (!heirarchyPermPro)
        {
            selectInQuery += " and cp.productId.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export))
        {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.krawler.spring.crm.caseModule.crmCaseReportDAO#
     * getProductHighPriorityChart(java.lang.String, java.util.HashMap,
     * java.lang.StringBuffer)
     */
    public KwlReturnObject getProductHighPriorityChart(String id, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "from CaseProducts cp where cp.caseid.deleteflag=0 and cp.caseid.isarchive= 'F' and cp.caseid.company.companyID= ? and cp.caseid.validflag=1 and cp.caseid.crmCombodataByCasepriorityid.mainID='" + Constants.CASEPRIORITY_HIGH + "' and cp.productId.productid=?";
        params = new Object[] { companyid, id };
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
        {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm)
        {
            selectInQuery += " and cp.caseid.usersByUserid.userID in (" + usersList + ")   ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
}
