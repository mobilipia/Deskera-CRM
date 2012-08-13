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

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import org.hibernate.SessionFactory;
import java.util.ArrayList;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.BuildCriteria;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmAccountActivity;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.CrmCampaignActivity;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmCaseActivity;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmContactActivity;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmLeadActivity;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmOpportunityActivity;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.dao.DataAccessException;
import com.krawler.crm.utils.Constants;
import com.krawler.esp.utils.ConfigReader;

public class crmActivityDAOImpl extends BaseDAO implements crmActivityDAO
{

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityDAO#getActiveActivity
     * (java.lang.String)
     */
    public KwlReturnObject getActiveActivity(String activityid) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        try
        {
            String Hql = "select c from CrmActivityMaster c  where  c.deleteflag=0  and c.isarchive= ? and c.activityid = ?";
            ll = executeQuery(Hql, new Object[] { false, activityid });
            dl = ll.size();
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getActiveActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, "002", "", ll, dl);
    }

    /**
     * @param requestParams
     * @param usersList
     * @param filter_params
     * @param Hql
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject buildActivityQuery(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_params, String Hql,String countHql) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        try
        {
            String appendCase = "and";
            requestParams.put(Constants.moduleid, 8);
            String Searchjson = "";
            if (requestParams.containsKey("searchJson") && requestParams.get("searchJson") != null)
            {
                Searchjson = requestParams.get("searchJson").toString();
                if (!StringUtil.isNullOrEmpty(Searchjson))
                {
                    requestParams.put("Searchjson", Searchjson);
                    requestParams.put("appendCase", appendCase);
                    String mySearchFilterString = String.valueOf(StringUtil.getMyAdvanceSearchString(requestParams).get("myResult"));
                    Hql += mySearchFilterString;
                    countHql += mySearchFilterString;
                    StringUtil.insertParamAdvanceSearchString(filter_params, Searchjson);
                }
            }

            if (requestParams.containsKey("ss") && requestParams.get("ss") != null)
            {
                String ss = requestParams.get("ss").toString();
                if (!StringUtil.isNullOrEmpty(ss))
                {
                    String[] searchcol = new String[] { "c.crmActivityMaster.subject" };
                    StringUtil.insertParamSearchString(filter_params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    Hql += searchQuery;
                    countHql += searchQuery;
                }
            }

            String status = "";
            if (requestParams.containsKey("status") && requestParams.get("status") != null)
            {
                status = requestParams.get("status").toString();
                if (!StringUtil.isNullOrEmpty(status))
                {
                    Hql += " and c.crmActivityMaster.crmCombodataByStatusid.value = '" + status + "'";
                    countHql += " and c.crmActivityMaster.crmCombodataByStatusid.value = '" + status + "'";
                }
            }

            int start = 0;
            int limit = 25;
            if (requestParams.containsKey("start") && requestParams.containsKey("limit"))
            {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
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
                selectInQuery = Hql + " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ") ";
                selectCountInQuery = countHql + " and c.crmActivityMaster.usersByUserid.userID in (" + usersList + ") ";
            }
            if((requestParams.containsKey("from") && requestParams.get("from")!=null) && (requestParams.containsKey("to") && requestParams.get("to")!=null)){
            	selectInQuery += " and ((c.crmActivityMaster.startDate >=" +requestParams.get("from")+ "  and c.crmActivityMaster.startDate <= " +requestParams.get("to")+ ") or (c.crmActivityMaster.endDate >= " +requestParams.get("from")+ " and c.crmActivityMaster.endDate <= " +requestParams.get("to")+ ") or (c.crmActivityMaster.startDate <= " +requestParams.get("from")+ " and c.crmActivityMaster.endDate >= " +requestParams.get("to")+ "))";
            	selectCountInQuery += " and ((c.crmActivityMaster.startDate >=" +requestParams.get("from")+ "  and c.crmActivityMaster.startDate <= " +requestParams.get("to")+ ") or (c.crmActivityMaster.endDate >= " +requestParams.get("from")+ " and c.crmActivityMaster.endDate <= " +requestParams.get("to")+ ") or (c.crmActivityMaster.startDate <= " +requestParams.get("from")+ " and c.crmActivityMaster.endDate >= " +requestParams.get("to")+ "))";
            }
            
            if (requestParams.containsKey("field") && requestParams.get("xfield") != null)
            {
                String field = requestParams.get("xfield").toString();
                String dbname = crmManagerCommon.getComboDbName(field);
                String dir = requestParams.get("direction").toString();
                if (StringUtil.isNullOrEmpty(dbname))
                {
                    selectInQuery = selectInQuery + "order by c.crmActivityMaster." + field + " " + dir + " ";
                    selectCountInQuery = selectCountInQuery + "order by c.crmActivityMaster." + field + " " + dir + " ";
                } else
                {
                    selectInQuery = selectInQuery + "order by " + dbname + " " + dir + " ";
                    selectCountInQuery = selectCountInQuery + "order by " + dbname + " " + dir + " ";
                }
            } else
            {
                selectInQuery = selectInQuery + " order by c.crmActivityMaster.createdOn desc ";
                selectCountInQuery = selectCountInQuery + " order by c.crmActivityMaster.createdOn desc ";
            }
            String export = "";
            if (requestParams.containsKey("export") && requestParams.get("export") != null) {
                export = requestParams.get("export").toString();
            }
            boolean calFlag=false;
            if(requestParams.containsKey("calflag") && requestParams.get("calflag")!=null){
            	calFlag=Boolean.parseBoolean(requestParams.get("calflag").toString());
            }
            if (StringUtil.isNullOrEmpty(export) && !calFlag) { // If not Export or not to display activities in calendar then get Total Count and get data list
                ll = executeQuery(selectCountInQuery, filter_params.toArray());
                dl = ((Number)ll.get(0)).intValue();
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
            } else { // get data List
                ll = executeQuery(selectInQuery, filter_params.toArray());
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmActivityDAOImpl.buildActivityQuery : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityDAO#getAccountActivity
     * (java.util.HashMap, java.lang.StringBuffer, java.util.ArrayList,
     * java.util.ArrayList)
     */
    public KwlReturnObject getAccountActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        KwlReturnObject kmsg = null;
        try
        {
            String Hql = "select c.crmActivityMaster,c.crmAccount from CrmAccountActivity c ";
            String countHql = "select count (distinct c) from CrmAccountActivity c ";
            
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;
            countHql += filterQuery;
            kmsg = buildActivityQuery(requestParams, usersList, filter_params, Hql,countHql);
            ll = kmsg.getEntityList();
            dl = kmsg.getRecordTotalCount();
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getAccountActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @seecom.krawler.spring.crm.activityModule.crmActivityDAO#
     * getAccountActivityForTable(java.util.HashMap, boolean)
     */
    public KwlReturnObject getAccountActivityForTable(HashMap<String, Object> queryParams, boolean allflag) throws ServiceException
    {
        KwlReturnObject kmsg = null;
        try
        {
            kmsg = getTableData(queryParams, allflag);
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getAccountActivityForTable : " + e.getMessage(), e);
        }
        return kmsg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityDAO#getLeadActivity(
     * java.util.HashMap, java.lang.StringBuffer, java.util.ArrayList,
     * java.util.ArrayList)
     */
    public KwlReturnObject getLeadActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        KwlReturnObject kmsg = null;
        try
        {
            String Hql = "select c.crmActivityMaster,c.crmLead from CrmLeadActivity c ";
            String countHql = "select count (distinct c) from CrmLeadActivity c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;
            countHql+= filterQuery;
            kmsg = buildActivityQuery(requestParams, usersList, filter_params, Hql,countHql);
            ll = kmsg.getEntityList();
            dl = kmsg.getRecordTotalCount();
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getLeadActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityDAO#getCampaignActivity
     * (java.util.HashMap, java.lang.StringBuffer, java.util.ArrayList,
     * java.util.ArrayList)
     */
    public KwlReturnObject getCampaignActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        KwlReturnObject kmsg = null;
        try
        {
            String Hql = "select c.crmActivityMaster,c.crmCampaign from CrmCampaignActivity c ";
            String countHql = "select count (distinct c) from CrmCampaignActivity c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;
            countHql+= filterQuery;
            kmsg = buildActivityQuery(requestParams, usersList, filter_params, Hql,countHql);
            ll = kmsg.getEntityList();
            dl = kmsg.getRecordTotalCount();
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getCampaignActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public  String getPrimaryKey(String recordId,int moduleId){
        String primaryKey = null;
        Criteria crit = null;
        List ll=null;
        String activityPropertyName = Constants.activityPropertyName;
        Session session = getSession();
        switch(moduleId){
            case 2:
                        crit = session.createCriteria(CrmAccountActivity.class);
                        BuildCriteria.buildCriteria(recordId, BuildCriteria.EQ, crit, activityPropertyName);
                        ll = crit.list();
                        if(ll!=null && ll.size() > 0){
                            CrmAccountActivity ca = (CrmAccountActivity) ll.get(0);
                            primaryKey = ca.getCrmAccount().getAccountid();
                        }
                break;
            case 3:
                        crit = session.createCriteria(CrmContactActivity.class);
                        BuildCriteria.buildCriteria(recordId, BuildCriteria.EQ, crit, activityPropertyName);
                        ll = crit.list();
                        if(ll!=null && ll.size() > 0){
                            CrmContactActivity ca = (CrmContactActivity) ll.get(0);
                            primaryKey = ca.getCrmContact().getContactid();
                        }
                break;
            case 4:
                        crit = session.createCriteria(CrmLeadActivity.class);
                        BuildCriteria.buildCriteria(recordId, BuildCriteria.EQ, crit, activityPropertyName);
                        ll = crit.list();
                        if(ll!=null && ll.size() > 0){
                            CrmLeadActivity ca = (CrmLeadActivity) ll.get(0);
                            primaryKey = ca.getCrmLead().getLeadid();
                        }
                break;
            case 5:
                        crit = session.createCriteria(CrmCaseActivity.class);
                        BuildCriteria.buildCriteria(recordId, BuildCriteria.EQ, crit, activityPropertyName);
                        ll = crit.list();
                        if(ll!=null && ll.size() > 0){
                            CrmCaseActivity ca = (CrmCaseActivity) ll.get(0);
                            primaryKey = ca.getCrmCase().getCaseid();
                        }
                break;
           case 6:
                        crit = session.createCriteria(CrmCampaignActivity.class);
                        BuildCriteria.buildCriteria(recordId, BuildCriteria.EQ, crit, activityPropertyName);
                        ll = crit.list();
                        if(ll!=null && ll.size() > 0){
                            CrmCampaignActivity ca = (CrmCampaignActivity) ll.get(0);
                            primaryKey = ca.getCrmCampaign().getCampaignid();
                        }
                break;
           case 7:
                        crit = session.createCriteria(CrmOpportunityActivity.class);
                        BuildCriteria.buildCriteria(recordId, BuildCriteria.EQ, crit, activityPropertyName);
                        ll = crit.list();
                        if(ll!=null && ll.size() > 0){
                            CrmOpportunityActivity ca = (CrmOpportunityActivity) ll.get(0);
                            primaryKey = ca.getCrmOpportunity().getOppid();
                        }
                break;

        }
        return primaryKey;
    }
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityDAO#getLeadActivityForTable
     * (java.util.HashMap, boolean)
     */
    public KwlReturnObject getLeadActivityForTable(HashMap<String, Object> queryParams, boolean allflag) throws ServiceException
    {
        KwlReturnObject kmsg = null;
        try
        {
            kmsg = getTableData(queryParams, allflag);
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getLeadActivityForTable : " + e.getMessage(), e);
        }
        return kmsg;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityDAO#getOpportunityActivity
     * (java.util.HashMap, java.lang.StringBuffer, java.util.ArrayList,
     * java.util.ArrayList)
     */
    public KwlReturnObject getOpportunityActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        KwlReturnObject kmsg = null;
        try
        {
            String Hql = "select c.crmActivityMaster,c.crmOpportunity from CrmOpportunityActivity c ";
            String countHql = "select count (distinct c) from CrmOpportunityActivity c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;
            countHql += filterQuery;
            kmsg = buildActivityQuery(requestParams, usersList, filter_params, Hql,countHql);
            ll = kmsg.getEntityList();
            dl = kmsg.getRecordTotalCount();
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getOpportunityActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityDAO#getContactActivity
     * (java.util.HashMap, java.lang.StringBuffer, java.util.ArrayList,
     * java.util.ArrayList)
     */
    public KwlReturnObject getContactActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        KwlReturnObject kmsg = null;
        try
        {
            String Hql = "select c.crmActivityMaster,c.crmContact from CrmContactActivity c ";
            String countHql = "select count (distinct c) from CrmContactActivity c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;
            countHql += filterQuery;
            kmsg = buildActivityQuery(requestParams, usersList, filter_params, Hql,countHql);
            ll = kmsg.getEntityList();
            dl = kmsg.getRecordTotalCount();
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getContactActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityDAO#getCaseActivity(
     * java.util.HashMap, java.lang.StringBuffer, java.util.ArrayList,
     * java.util.ArrayList)
     */
    public KwlReturnObject getCaseActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        KwlReturnObject kmsg = null;
        try
        {
            String Hql = "select c.crmActivityMaster,c.crmCase from CrmCaseActivity c ";
            String countHql = "select count (distinct c) from CrmCaseActivity c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;
            countHql += filterQuery;
            kmsg = buildActivityQuery(requestParams, usersList, filter_params, Hql,countHql);
            ll = kmsg.getEntityList();
            dl = kmsg.getRecordTotalCount();
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getCaseActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityDAO#addActivity(com.
     * krawler.utils.json.base.JSONObject)
     */
    public KwlReturnObject addActivity(JSONObject jobj) throws ServiceException
    {
        JSONObject myjobj = new JSONObject();
        List ll = new ArrayList();
        int dl = 0;
        try
        {
            String companyid = null;
            String userid = null;
            String id = "";
            CrmActivityMaster crmActivityMaster = new CrmActivityMaster();
            if (jobj.has("activityid"))
            {
                id = jobj.getString("activityid");
                crmActivityMaster.setActivityid(id);
            }
            if (jobj.has("companyid"))
            {
                companyid = jobj.getString("companyid");
                crmActivityMaster.setCompany((Company) get(Company.class, companyid));
            }
            if (jobj.has("email"))
            {
                crmActivityMaster.setEmail(jobj.getString("email"));
            }
            if (jobj.has("flag"))
            {
                crmActivityMaster.setFlag(jobj.getString("flag"));
            }
            if (jobj.has("phone"))
            {
                crmActivityMaster.setPhone(jobj.getString("phone"));
            }
            if (jobj.has("priorityid"))
            {
                crmActivityMaster.setCrmCombodataByPriorityid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("priorityid")));
            }
            if (jobj.has("enddate"))
            {
                crmActivityMaster.setEndDate(jobj.getLong("enddate"));
            }
            if (jobj.has("startdate"))
            {
                crmActivityMaster.setStartDate(jobj.getLong("startdate"));
            }
            if (jobj.has("statusid"))
            {
                crmActivityMaster.setCrmCombodataByStatusid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("statusid")));
            }
            if (jobj.has("subject"))
            {
                crmActivityMaster.setSubject(jobj.getString("subject"));
            }
            if (jobj.has("typeid"))
            {
                crmActivityMaster.setCrmCombodataByTypeid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("typeid")));
            }
            if (jobj.has("ownerid"))
            {
                crmActivityMaster.setUsersByUserid((User) get(User.class, jobj.getString("ownerid")));
            }
            if (jobj.has("userid"))
            {
                userid = jobj.getString("userid");
                crmActivityMaster.setUsersByUpdatedbyid((User) get(User.class, userid));
                crmActivityMaster.setUsersByCreatedbyid((User) get(User.class, userid));
            }
            if (jobj.has("updatedon"))
            {
                crmActivityMaster.setUpdatedOn(System.currentTimeMillis());
            }
            if (jobj.has("validflag"))
            {
                crmActivityMaster.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if (jobj.has("createdon"))
            {
            crmActivityMaster.setCreatedOn(jobj.getLong(Constants.createdon));
            }
            if (jobj.has("deleteflag"))
            {
                crmActivityMaster.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if (jobj.has("allday"))
            {
                crmActivityMaster.setIsallday(Boolean.parseBoolean(jobj.getString("allday")));
            }
            if (jobj.has("scheduleType"))
            {
                crmActivityMaster.setScheduleType(Integer.parseInt(jobj.getString("scheduleType")));
            }
            if (jobj.has("tilldate"))
            {
                crmActivityMaster.setTilldat( jobj.getLong("tilldate"));
            }
            if (jobj.has("calendarid") && !StringUtil.isNullOrEmpty(jobj.getString("calendarid")))
            {
                crmActivityMaster.setCalendarid(jobj.getString("calendarid"));
            }

            saveOrUpdate(crmActivityMaster);

            String relatedtoid = jobj.getString("relatedtoid");
            String relatedtonameid = jobj.getString("relatedtonameid");
            if (relatedtoid.equals("Account"))
            {
                CrmAccountActivity crmaa = new CrmAccountActivity();
                crmaa.setCrmActivityMaster(crmActivityMaster);
                crmaa.setRid(java.util.UUID.randomUUID().toString());
                crmaa.setCrmAccount((CrmAccount) get(CrmAccount.class, relatedtonameid));
                save(crmaa);
                crmActivityMaster.setMapwith(2);
                myjobj.put("validflag", crmaa.getCrmActivityMaster().getValidflag());
                myjobj.put("auditmsg", crmaa.getCrmActivityMaster().getFlag() + " activity added for Account " + crmaa.getCrmAccount().getAccountname() + " - Activity created ");
            } else if (relatedtoid.equals("Contact"))
            {
                CrmContactActivity crmca = new CrmContactActivity();
                crmca.setCrmActivityMaster(crmActivityMaster);
                crmca.setCrmContact((CrmContact) get(CrmContact.class, relatedtonameid));
                crmca.setRid(java.util.UUID.randomUUID().toString());
                save(crmca);
                crmActivityMaster.setMapwith(3);
                myjobj.put("validflag", crmca.getCrmActivityMaster().getValidflag());
                myjobj.put("auditmsg", crmca.getCrmActivityMaster().getFlag() + " activity added for Contact " + (StringUtil.checkForNull(crmca.getCrmContact().getFirstname()) + " " + StringUtil.checkForNull(crmca.getCrmContact().getLastname())).trim() + " - Activity created ");
            } else if (relatedtoid.equals("Lead"))
            {
                CrmLeadActivity crmla = new CrmLeadActivity();
                crmla.setCrmActivityMaster(crmActivityMaster);
                crmla.setRid(java.util.UUID.randomUUID().toString());
                crmla.setCrmLead((CrmLead) get(CrmLead.class, relatedtonameid));
                save(crmla);
                crmActivityMaster.setMapwith(4);
                myjobj.put("validflag", crmla.getCrmActivityMaster().getValidflag());
                myjobj.put("auditmsg", crmla.getCrmActivityMaster().getFlag() + " activity added for Lead " + StringUtil.checkForNull(crmla.getCrmLead().getFirstname()) + " " + crmla.getCrmLead().getLastname() + " - Activity created ");
            } else if (relatedtoid.equals("Case"))
            {
                CrmCaseActivity crmca = new CrmCaseActivity();
                crmca.setCrmActivityMaster(crmActivityMaster);
                crmca.setRid(java.util.UUID.randomUUID().toString());
                crmca.setCrmCase((CrmCase) get(CrmCase.class, relatedtonameid));
                save(crmca);
                crmActivityMaster.setMapwith(5);
                myjobj.put("validflag", crmca.getCrmActivityMaster().getValidflag());
                myjobj.put("auditmsg", crmca.getCrmActivityMaster().getFlag() + " activity added for Case " + crmca.getCrmCase().getSubject() + " - Activity created ");
            } else if (relatedtoid.equals("Campaign"))
            {
                CrmCampaignActivity crmca = new CrmCampaignActivity();
                crmca.setCrmActivityMaster(crmActivityMaster);
                crmca.setRid(java.util.UUID.randomUUID().toString());
                crmca.setCrmCampaign((CrmCampaign) get(CrmCampaign.class, relatedtonameid));
                save(crmca);
                crmActivityMaster.setMapwith(6);
                myjobj.put("validflag", crmca.getCrmActivityMaster().getValidflag());
                myjobj.put("auditmsg", crmca.getCrmActivityMaster().getFlag() + " activity added for Campaign " + crmca.getCrmCampaign().getCampaignname() + " - Activity created ");
            } else if (relatedtoid.equals("Opportunity"))
            {
                CrmOpportunityActivity crmoa = new CrmOpportunityActivity();
                crmoa.setCrmActivityMaster(crmActivityMaster);
                crmoa.setRid(java.util.UUID.randomUUID().toString());
                crmoa.setCrmOpportunity((CrmOpportunity) get(CrmOpportunity.class, relatedtonameid));
                save(crmoa);
                crmActivityMaster.setMapwith(7);
                myjobj.put("validflag", crmoa.getCrmActivityMaster().getValidflag());
                myjobj.put("auditmsg", crmoa.getCrmActivityMaster().getFlag() + " activity added for Opportunity " + crmoa.getCrmOpportunity().getOppname() + " - Activity created ");
            } else
            {
                // crmActivityMaster.setMapwith(0);
            }
            saveOrUpdate(crmActivityMaster);

            ll.add(crmActivityMaster);
            myjobj.put("success", true);
            myjobj.put("ID", id);
            ll.add(myjobj);
        } catch (JSONException e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.addActivity : " + e.getMessage(), e);
        } catch (DataAccessException e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.addActivity : " + e.getMessage(), e);
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.addActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.activityModule.crmActivityDAO#editActivity(com
     * .krawler.utils.json.base.JSONObject)
     */
    public KwlReturnObject editActivity(JSONObject jobj) throws ServiceException
    {
        JSONObject myjobj = new JSONObject();
        List ll = new ArrayList();
        int dl = 0;
        try
        {
            String companyid = null;
            String userid = null;
            String id = "";
            CrmActivityMaster crmActivityMaster = null;
            if (jobj.has("activityid"))
            {
                id = jobj.getString("activityid");
                crmActivityMaster = (CrmActivityMaster) get(CrmActivityMaster.class, id);
            }
            if (jobj.has("companyid"))
            {
                companyid = jobj.getString("companyid");
                crmActivityMaster.setCompany((Company) get(Company.class, companyid));
            }
            if (jobj.has("email"))
            {
                crmActivityMaster.setEmail(jobj.getString("email"));
            }
            if (jobj.has("flag"))
            {
                crmActivityMaster.setFlag(jobj.getString("flag"));
            }
            if (jobj.has("phone"))
            {
                crmActivityMaster.setPhone(jobj.getString("phone"));
            }
            if (jobj.has("priorityid"))
            {
                crmActivityMaster.setCrmCombodataByPriorityid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("priorityid")));
            }
            if (jobj.has("enddate"))
            {
                crmActivityMaster.setEndDate(jobj.getLong("enddate"));
            }
            if (jobj.has("startdate"))
            {
                crmActivityMaster.setStartDate(jobj.getLong("startdate"));
            }
            if (jobj.has("statusid"))
            {
                crmActivityMaster.setCrmCombodataByStatusid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("statusid")));
            }
            if (jobj.has("subject"))
            {
                crmActivityMaster.setSubject(jobj.getString("subject"));
            }
            if (jobj.has("typeid"))
            {
                crmActivityMaster.setCrmCombodataByTypeid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("typeid")));
            }
            if (jobj.has("ownerid"))
            {
                crmActivityMaster.setUsersByUserid((User) get(User.class, jobj.getString("ownerid")));
            }
            if (jobj.has("userid"))
            {
                userid = jobj.getString("userid");
                crmActivityMaster.setUsersByUpdatedbyid((User) get(User.class, userid));                
            }
            if (jobj.has("updatedon"))
            {
                crmActivityMaster.setUpdatedOn(System.currentTimeMillis());
            }
            if (jobj.has("validflag"))
            {
                crmActivityMaster.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if (jobj.has("deleteflag"))
            {
                crmActivityMaster.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if (jobj.has("allday"))
            {
                crmActivityMaster.setIsallday(Boolean.parseBoolean(jobj.getString("allday")));
            }
            if (jobj.has("scheduleType"))
            {
                crmActivityMaster.setScheduleType(Integer.parseInt(jobj.getString("scheduleType")));
            }
            if (jobj.has("tilldate"))
            {
                crmActivityMaster.setTilldat(jobj.getLong("tilldate"));
            }

            if (jobj.has("calendarid") && !StringUtil.isNullOrEmpty(jobj.getString("calendarid")))
            {
                crmActivityMaster.setCalendarid(jobj.getString("calendarid"));
            }            
            saveOrUpdate(crmActivityMaster);
            if (!jobj.has("deleteflag"))
            {
                String relatedtoid = jobj.getString("relatedtoid");
                String relatedtonameid = jobj.getString("relatedtonameid");
                if ((int) crmActivityMaster.getMapwith() == 0)
                {
                    if (relatedtoid.equals("Account"))
                    {
                        CrmAccount crmAcc = (CrmAccount) get(CrmAccount.class, relatedtonameid);
                        CrmAccountActivity crmaa = new CrmAccountActivity();
                        crmaa.setCrmActivityMaster(crmActivityMaster);
                        crmaa.setRid(java.util.UUID.randomUUID().toString());
                        crmaa.setCrmAccount(crmAcc);
                        save(crmaa);
                        
                        myjobj.put("validflag", crmActivityMaster.getValidflag());
                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Account - " + crmAcc.getAccountname() + " ");
                        crmActivityMaster.setMapwith(2);

                    } else if (relatedtoid.equals("Contact"))
                    {
                        CrmContact crmCon = (CrmContact) get(CrmContact.class, relatedtonameid);
                        CrmContactActivity crmca = new CrmContactActivity();
                        crmca.setCrmActivityMaster(crmActivityMaster);
                        crmca.setCrmContact(crmCon);
                        crmca.setRid(java.util.UUID.randomUUID().toString());
                        save(crmca);

                        myjobj.put("validflag", crmActivityMaster.getValidflag());
                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Contact - " + (StringUtil.checkForNull(crmCon.getFirstname()) + " " + StringUtil.checkForNull(crmCon.getLastname())).trim() + " ");
                        crmActivityMaster.setMapwith(3);

                    } else if (relatedtoid.equals("Lead"))
                    {
                        CrmLead crmLead = (CrmLead) get(CrmLead.class, relatedtonameid);
                        CrmLeadActivity crmla = new CrmLeadActivity();
                        crmla.setCrmActivityMaster(crmActivityMaster);
                        crmla.setRid(java.util.UUID.randomUUID().toString());
                        crmla.setCrmLead(crmLead);
                        save(crmla);

                        myjobj.put("validflag", crmActivityMaster.getValidflag());
                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Lead - " + StringUtil.checkForNull(crmLead.getFirstname()) + " " + StringUtil.checkForNull(crmLead.getLastname()) + " ");
                        crmActivityMaster.setMapwith(4);

                    } else if (relatedtoid.equals("Case"))
                    {
                        CrmCase crmCase = (CrmCase) get(CrmCase.class, relatedtonameid);
                        CrmCaseActivity crmca = new CrmCaseActivity();
                        crmca.setCrmActivityMaster(crmActivityMaster);
                        crmca.setRid(java.util.UUID.randomUUID().toString());
                        crmca.setCrmCase(crmCase);
                        save(crmca);

                        myjobj.put("validflag", crmActivityMaster.getValidflag());
                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Case - " + crmCase.getSubject() + " ");
                        crmActivityMaster.setMapwith(5);

                    } else if (relatedtoid.equals("Campaign"))
                    {
                        CrmCampaign crmCamp = (CrmCampaign) get(CrmCampaign.class, relatedtonameid);
                        CrmCampaignActivity crmca = new CrmCampaignActivity();
                        crmca.setCrmActivityMaster(crmActivityMaster);
                        crmca.setRid(java.util.UUID.randomUUID().toString());
                        crmca.setCrmCampaign(crmCamp);
                        save(crmca);

                        myjobj.put("validflag", crmActivityMaster.getValidflag());
                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Campaign - " + crmCamp.getCampaignname() + " ");
                        crmActivityMaster.setMapwith(6);

                    } else if (relatedtoid.equals("Opportunity"))
                    {
                        CrmOpportunity crmOpp = (CrmOpportunity) get(CrmOpportunity.class, relatedtonameid);
                        CrmOpportunityActivity crmoa = new CrmOpportunityActivity();
                        crmoa.setCrmActivityMaster(crmActivityMaster);
                        crmoa.setRid(java.util.UUID.randomUUID().toString());
                        crmoa.setCrmOpportunity(crmOpp);
                        save(crmoa);

                        myjobj.put("validflag", crmActivityMaster.getValidflag());
                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Opportunity - " + crmOpp.getOppname() + " ");
                        crmActivityMaster.setMapwith(7);
                    }
                    saveOrUpdate(crmActivityMaster);
                } else if ((int) crmActivityMaster.getMapwith() == 4)
                {
                    CrmLead crmLead = (CrmLead) get(CrmLead.class, relatedtonameid);
                    myjobj.put("validflag", crmActivityMaster.getValidflag());
                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Lead - " + StringUtil.checkForNull(crmLead.getFirstname()) + " " + StringUtil.checkForNull(crmLead.getLastname()) + " ");
                } else if ((int) crmActivityMaster.getMapwith() == 3)
                {
                    CrmContact crmCon = (CrmContact) get(CrmContact.class, relatedtonameid);
                    myjobj.put("validflag", crmActivityMaster.getValidflag());
                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Contact - " + (StringUtil.checkForNull(crmCon.getFirstname()) + " " + StringUtil.checkForNull(crmCon.getLastname())).trim() + " ");
                } else if ((int) crmActivityMaster.getMapwith() == 2)
                {
                    CrmAccount crmAcc = (CrmAccount) get(CrmAccount.class, relatedtonameid);
                    myjobj.put("validflag", crmActivityMaster.getValidflag());
                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for  Account - " + crmAcc.getAccountname() + " ");
                } else if ((int) crmActivityMaster.getMapwith() == 5)
                {
                    CrmCase crmCas = (CrmCase) get(CrmCase.class, relatedtonameid);
                    myjobj.put("validflag", crmActivityMaster.getValidflag());
                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Case - " + crmCas.getSubject() + " ");
                } else if ((int) crmActivityMaster.getMapwith() == 6)
                {
                    CrmCampaign crmCam = (CrmCampaign) get(CrmCampaign.class, relatedtonameid);
                    myjobj.put("validflag", crmActivityMaster.getValidflag());
                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Campaign - " + crmCam.getCampaignname() + " ");
                } else if ((int) crmActivityMaster.getMapwith() == 7)
                {
                    CrmOpportunity crmOpp = (CrmOpportunity) get(CrmOpportunity.class, relatedtonameid);
                    myjobj.put("validflag", crmActivityMaster.getValidflag());
                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Opportunity - " + crmOpp.getOppname() + " ");
                }
            }
            // hibernateTemplate.saveOrUpdate(crmActivityMaster);
            ll.add(crmActivityMaster);
            myjobj.put("success", true);
            myjobj.put("flag", crmActivityMaster.getFlag());
            myjobj.put("ID", id);
            ll.add(myjobj);
        } catch (JSONException e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.editActivity : " + e.getMessage(), e);
        } catch (DataAccessException e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.editActivity : " + e.getMessage(), e);
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.editActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject getDetailPanelRecentActivity(HashMap<String, Object> requestParams) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        try
        {
            Date today = (Date) requestParams.get("today");
            Date maxDateUpdate = (Date) requestParams.get("maxDateUpdate");
            Date lastMaxDateUpdate = (Date) requestParams.get("lastMaxDateUpdate");

            String module = requestParams.get("module").toString();
            StringBuffer usersList = (StringBuffer) requestParams.get("usersList");
            boolean OneTimeFlag = (Boolean) requestParams.get("OneTimeFlag");
            String recid = requestParams.get("recid").toString();

            String activityCreate = "";
            String recQuery = "";
            String subquery = "";
            // recent activity
            subquery = " and (((c.crmActivityMaster.startDate <= ? or (c.crmActivityMaster.startDate<=? and c.crmActivityMaster.startDate>=?)) and c.crmActivityMaster.scheduleType in (1,2,3)) and " + "(c.crmActivityMaster.tilldate > ? or c.crmActivityMaster.tilldate is null))";
            if (Constants.moduleMap.containsKey(module))
            {
                switch (Constants.moduleMap.get(module))
                {
                case 1: // Lead
                    activityCreate = "select distinct c.crmActivityMaster,c.crmLead from CrmLeadActivity c inner join c.crmLead l inner join l.leadOwners lo where c.crmLead.deleteflag=0 and c.crmActivityMaster.deleteflag=0 " + " and lo.usersByUserid.userID in (" + usersList
                            + ") and c.crmActivityMaster.mapwith = 4 and c.crmLead.leadid = ? and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
                    recQuery = activityCreate + subquery;
                    break;
                case 3: // Account
                    activityCreate = "select distinct c.crmActivityMaster,c.crmAccount from CrmAccountActivity c inner join c.crmAccount l inner join l.accountOwners lo where c.crmAccount.deleteflag=0 and c.crmActivityMaster.deleteflag=0 " + " and lo.usersByUserid.userID in (" + usersList
                            + ") and c.crmActivityMaster.mapwith = 2 and c.crmAccount.accountid = ? and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
                    recQuery = activityCreate + subquery;
                    break;
                case 4: // Contact
                    activityCreate = "select c.crmActivityMaster,c.crmContact from CrmContactActivity c inner join c.crmContact l inner join l.contactOwners lo where c.crmContact.deleteflag=0 and c.crmActivityMaster.deleteflag=0 " + " and lo.usersByUserid.userID in (" + usersList
                            + ") and c.crmActivityMaster.mapwith = 3 and c.crmContact.contactid = ?  and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
                    recQuery = activityCreate + subquery;
                    break;
                case 2: // Opportunity
                    activityCreate = "select c.crmActivityMaster,c.crmOpportunity from CrmOpportunityActivity c inner join c.crmOpportunity l inner join l.oppOwners lo where c.crmOpportunity.deleteflag=0 and c.crmActivityMaster.deleteflag=0 " + " and lo.usersByUserid.userID in (" + usersList
                            + ") and c.crmActivityMaster.mapwith = 7 and c.crmOpportunity.oppid = ?  and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
                    recQuery = activityCreate + subquery;
                    break;
                case 6: // Case
                    activityCreate = "select c.crmActivityMaster,c.crmCase from CrmCaseActivity c where c.crmCase.deleteflag=0 and c.crmActivityMaster.deleteflag=0 " + " and c.crmCase.usersByUserid.userID in (" + usersList + ") and c.crmActivityMaster.mapwith = 5 and c.crmCase.caseid = ? and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
                    recQuery = activityCreate + subquery;
                    break;
                case 8: // Campaign
                    activityCreate = "select c.crmActivityMaster,c.crmCampaign from CrmCampaignActivity c where c.crmCampaign.deleteflag=0 and c.crmActivityMaster.deleteflag=0 " + " and c.crmCampaign.usersByUserid.userID in (" + usersList + ") and c.crmActivityMaster.mapwith = 6 and c.crmCampaign.campaignid = ? and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
                    recQuery = activityCreate + subquery;
                    break;
                }
            }
            if (!StringUtil.isNullOrEmpty(recQuery))
            {
                if (OneTimeFlag)
                {
                    // One time activity
                    String oneTimeActivity = " and (c.crmActivityMaster.startDate >= ? or c.crmActivityMaster.startDate<=?) and c.crmActivityMaster.scheduleType=0";
                    activityCreate = activityCreate + oneTimeActivity;
                    ll = executeQuery(activityCreate, new Object[] { recid, lastMaxDateUpdate.getTime(), maxDateUpdate.getTime() });
                } else
                {
                    ll = executeQuery(recQuery, new Object[] { recid, today.getTime(), maxDateUpdate.getTime(), today.getTime(), today });
                }
            }
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getAccountActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }


    public KwlReturnObject getDetailPanelTopActivity(long today,long upto, StringBuffer usersList) throws ServiceException
    {
        List ll = null;
        List mainList=new ArrayList();
        int dl = 0;
        try{
            String activityCreate = "";
            String recQuery = "";
            String subquery = "";
            subquery = " and c.crmActivityMaster.startDate >= ? and c.crmActivityMaster.startDate <? order by c.crmActivityMaster.startDate desc";
            boolean UserFilter = true;
            if(StringUtil.isNullOrEmpty(usersList.toString())) {
                UserFilter = false;
            }
            activityCreate = "select distinct c.crmActivityMaster,c.crmAccount.accountid,c.crmAccount.accountname,2 from CrmAccountActivity c inner join c.crmAccount l inner join l.accountOwners lo where c.crmAccount.deleteflag=0 and c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.mapwith = 2 and c.crmActivityMaster.validflag = 1 ";
            if(UserFilter) {
                activityCreate += " and lo.usersByUserid.userId in (" + usersList + ")";
            }
            
            recQuery = activityCreate + subquery;
            if (!StringUtil.isNullOrEmpty(recQuery)){
                ll = executeQuery(recQuery, new Object[] { today ,upto});
                 mainList.addAll(ll);
            }
          //  firstName||' '||initial||' '||upper(lastName)
            activityCreate = "select c.crmActivityMaster,c.crmContact.contactid,c.crmContact.title||' '||c.crmContact.firstname||' '||c.crmContact.lastname,3 from CrmContactActivity c inner join c.crmContact l inner join l.contactOwners lo where c.crmContact.deleteflag=0 and c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.mapwith = 3   and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
            if(UserFilter) {
                activityCreate += " and lo.usersByUserid.userId in (" + usersList+ ")";
            }
            recQuery = activityCreate + subquery;
            if (!StringUtil.isNullOrEmpty(recQuery)){
                ll = executeQuery(recQuery, new Object[] { today ,upto});
                 mainList.addAll(ll);
            }

            activityCreate = "select distinct c.crmActivityMaster,c.crmLead.leadid,c.crmLead.title||' '||c.crmLead.firstname||' '||c.crmLead.lastname,4 from CrmLeadActivity c inner join c.crmLead l inner join l.leadOwners lo where c.crmLead.deleteflag=0 and c.crmActivityMaster.deleteflag=0  and c.crmActivityMaster.mapwith = 4 and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
            if(UserFilter) {
                activityCreate += " and lo.usersByUserid.userId in (" + usersList+ ")";
            }

            recQuery = activityCreate + subquery;
            if (!StringUtil.isNullOrEmpty(recQuery)){
                ll = executeQuery(recQuery, new Object[] { today ,upto});
                 mainList.addAll(ll);
            }

            activityCreate = "select c.crmActivityMaster,c.crmCase.caseid,c.crmCase.casename,5 from CrmCaseActivity c where c.crmCase.deleteflag=0 and c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.mapwith = 5  and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
            if(UserFilter) {
                activityCreate += " and c.crmCase.usersByUserid.userId in (" + usersList+ ")";
            }

            recQuery = activityCreate + subquery;
            if (!StringUtil.isNullOrEmpty(recQuery)){
                ll = executeQuery(recQuery, new Object[] { today ,upto});
                 mainList.addAll(ll);
            }

            activityCreate = "select c.crmActivityMaster,c.crmCampaign.campaignid,c.crmCampaign.campaignname,6 from CrmCampaignActivity c where c.crmCampaign.deleteflag=0 and c.crmActivityMaster.deleteflag=0 "+
                    " and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
            if(UserFilter) {
                activityCreate += " and c.crmCampaign.usersByUserid.userId in (" + usersList + ")";
            }

            recQuery = activityCreate + subquery;
            if (!StringUtil.isNullOrEmpty(recQuery)){
                ll = executeQuery(recQuery, new Object[] { today ,upto});
                 mainList.addAll(ll);
            }

            activityCreate = "select c.crmActivityMaster,c.crmOpportunity.oppid,c.crmOpportunity.oppname,7 from CrmOpportunityActivity c inner join c.crmOpportunity l inner join l.oppOwners lo where c.crmOpportunity.deleteflag=0 and c.crmActivityMaster.deleteflag=0 and c.crmActivityMaster.mapwith = 7 and c.crmActivityMaster.validflag = 1 and c.crmActivityMaster.isarchive = false";
            if(UserFilter) {
                activityCreate += " and lo.usersByUserid.userId in (" + usersList + ")";
            }

            recQuery = activityCreate + subquery;
            if (!StringUtil.isNullOrEmpty(recQuery)){
                ll = executeQuery(recQuery, new Object[] { today ,upto});
                 mainList.addAll(ll);
            }
        } catch (Exception e){
            throw ServiceException.FAILURE("crmActivityDAOImpl.getDetailPanelTopActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", mainList, dl);
    }

    @Override
    public List<CrmActivityMaster> getActivities(List<String> recordIds)
    {
        if (recordIds == null || recordIds.isEmpty())
        {
            return null;
        }
        StringBuilder hql = new StringBuilder("from CrmActivityMaster where activityid in (");
        
        for (String record: recordIds)
        {
            hql.append("'" + record + "',");
        }
        
        hql.deleteCharAt(hql.length() - 1);
        hql.append(")");

        return executeQuery(hql.toString());
    }

    @Override
    public KwlReturnObject getOverdueactivities() {
        List lst = null;
        try {
            String hql = "";
            DateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            String lastDate = dt.format(cal.getTime());
            cal.add(Calendar.DATE, -1);
            String lastBeforeDate = dt.format(cal.getTime());
            String serverTimezone = ConfigReader.getinstance().get("SERVER_TIMEZONE");//"-05:00";
            hql = "select concat(fname,' ',lname) as name, am.subject, " +
                    " am.statusid, am.priorityid, users.userid, am.activityid " +
                    " from crm_activity_master as am " +
                    " inner join users on am.userid = users.userid " +
                    " inner join timezone as tz on tz.timzoneid = users.timezone "+
                    " left join defaultmasteritem dm on dm.id = am.statusid " +
                    " where CONVERT_TZ(FROM_UNIXTIME(am.enddate/1000),'"+serverTimezone+"',tz.difference) > ? " +
                    " and CONVERT_TZ(FROM_UNIXTIME(am.enddate/1000),'"+serverTimezone+"',tz.difference) <= ? " +
                    " and (dm.mainID != ? or am.statusid is null) ";
            ArrayList param = new ArrayList();
            param.add(lastBeforeDate);
            param.add(lastDate);
            param.add(Constants.ACTIVITYSTATUSID_COMPLETED);
            lst = executeNativeQuery(hql, param.toArray());
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmActivityDAOImpl.getOverdueactivities : "+e.getMessage(), e);
        } finally {
            return new KwlReturnObject(true, KWLErrorMsgs.S01, "", lst, lst.size());
        }
    }
// ToDo (Kuldeep Singh) : Currently working for Delete Functionality. Please implement the commented code's logic for Mass Update functionality.
    public KwlReturnObject updateMassActivity(JSONObject jobj) throws ServiceException
    {
        JSONObject myjobj = new JSONObject();
        List ll = new ArrayList();
        String hqlVarPart = "";
        List<Object> params = new ArrayList<Object>();
        int dl = 0;
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy HH:mm:ss");
            String id = "";
            String[] activityids = (String[])jobj.get("activityid");
            
            if (jobj.has("companyid"))
            {
                hqlVarPart += " company = ?,";
            	params.add(get(Company.class, jobj.getString("companyid")));
            }
            if (jobj.has("email"))
            {
                hqlVarPart += " email = ?,";
            	params.add(jobj.getString("email"));
            }
            if (jobj.has("flag"))
            {
                hqlVarPart += " flag = ?,";
            	params.add(jobj.getString("flag"));
            }
            if (jobj.has("phone"))
            {
                hqlVarPart += " phone = ?,";
            	params.add(jobj.getString("phone"));
            }
            if (jobj.has("priorityid"))
            {
                hqlVarPart += " crmCombodataByPriorityid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("priorityid")));
            }
            if (jobj.has("enddate") && !jobj.getString("enddate").equals(""))
            {
                hqlVarPart += " endDate = ?,";
            	params.add(((Date) jobj.get("enddate")).getTime());
            }
            if (jobj.has("startdate") && !jobj.getString("startdate").equals(""))
            {
                hqlVarPart += " startDate = ?,";
            	params.add(((Date) jobj.get("startdate")).getTime());
            }
            if (jobj.has("statusid"))
            {
                hqlVarPart += " crmCombodataByStatusid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("statusid")));
            }
            if (jobj.has("subject"))
            {
                hqlVarPart += " subject = ?,";
            	params.add(jobj.getString("subject"));
            }
            if (jobj.has("typeid"))
            {
                hqlVarPart += " crmCombodataByTypeid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("typeid")));
            }
            if (jobj.has("ownerid"))
            {
                hqlVarPart += " usersByUserid = ?,";
            	params.add(get(User.class, jobj.getString("ownerid")));
            }
            if (jobj.has("userid"))
            {
                hqlVarPart += " usersByUpdatedbyid = ?,";
            	params.add(get(User.class, jobj.getString("userid")));
            }
            if (jobj.has("updatedon"))
            {
                hqlVarPart += " updatedOn = ?,";
            	params.add(authHandler.getCurrentDate().getTime());
            }
            if (jobj.has("validflag"))
            {
                hqlVarPart += " validflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("validflag")));
            }
            if (jobj.has("deleteflag"))
            {
                hqlVarPart += " deleteflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if (jobj.has("allday"))
            {
                hqlVarPart += " isallday = ?,";
            	params.add(Boolean.parseBoolean(jobj.getString("allday")));
            }
            if (jobj.has("scheduleType"))
            {
                hqlVarPart += " scheduleType = ?,";
            	params.add(Integer.parseInt(jobj.getString("scheduleType")));
            }
            if (jobj.has("tilldate"))
            {
                hqlVarPart += " tilldate = ?,";
            	params.add( jobj.getLong("tilldate"));
            }

            if (jobj.has("calendarid") && !StringUtil.isNullOrEmpty(jobj.getString("calendarid")))
            {
                hqlVarPart += " calendarid = ?,";
            	params.add(jobj.getString("calendarid"));
            }

            hqlVarPart = hqlVarPart.substring(0, Math.max(0,hqlVarPart.lastIndexOf(',')));
            String hql = "update CrmActivityMaster set "+hqlVarPart+" where activityid in (:activityids)";
            Map map = new HashMap();
            map.put("activityids", activityids);
            executeUpdate(hql, params.toArray(), map);
// TODO (Kuldeep Singh) : 
//            saveOrUpdate(crmActivityMaster);
//            if (!jobj.has("deleteflag"))
//            {
//                String relatedtoid = jobj.getString("relatedtoid");
//                String relatedtonameid = jobj.getString("relatedtonameid");
//                if ((int) crmActivityMaster.getMapwith() == 0)
//                {
//                    if (relatedtoid.equals("Account"))
//                    {
//                        CrmAccount crmAcc = (CrmAccount) get(CrmAccount.class, relatedtonameid);
//                        CrmAccountActivity crmaa = new CrmAccountActivity();
//                        crmaa.setCrmActivityMaster(crmActivityMaster);
//                        crmaa.setRid(java.util.UUID.randomUUID().toString());
//                        crmaa.setCrmAccount(crmAcc);
//                        save(crmaa);
//
//                        myjobj.put("validflag", crmActivityMaster.getValidflag());
//                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Account - " + crmAcc.getAccountname() + " ");
//                        crmActivityMaster.setMapwith(2);
//
//                    } else if (relatedtoid.equals("Contact"))
//                    {
//                        CrmContact crmCon = (CrmContact) get(CrmContact.class, relatedtonameid);
//                        CrmContactActivity crmca = new CrmContactActivity();
//                        crmca.setCrmActivityMaster(crmActivityMaster);
//                        crmca.setCrmContact(crmCon);
//                        crmca.setRid(java.util.UUID.randomUUID().toString());
//                        save(crmca);
//
//                        myjobj.put("validflag", crmActivityMaster.getValidflag());
//                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Contact - " + (StringUtil.checkForNull(crmCon.getFirstname()) + " " + StringUtil.checkForNull(crmCon.getLastname())).trim() + " ");
//                        crmActivityMaster.setMapwith(3);
//
//                    } else if (relatedtoid.equals("Lead"))
//                    {
//                        CrmLead crmLead = (CrmLead) get(CrmLead.class, relatedtonameid);
//                        CrmLeadActivity crmla = new CrmLeadActivity();
//                        crmla.setCrmActivityMaster(crmActivityMaster);
//                        crmla.setRid(java.util.UUID.randomUUID().toString());
//                        crmla.setCrmLead(crmLead);
//                        save(crmla);
//
//                        myjobj.put("validflag", crmActivityMaster.getValidflag());
//                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Lead - " + StringUtil.checkForNull(crmLead.getFirstname()) + " " + StringUtil.checkForNull(crmLead.getLastname()) + " ");
//                        crmActivityMaster.setMapwith(4);
//
//                    } else if (relatedtoid.equals("Case"))
//                    {
//                        CrmCase crmCase = (CrmCase) get(CrmCase.class, relatedtonameid);
//                        CrmCaseActivity crmca = new CrmCaseActivity();
//                        crmca.setCrmActivityMaster(crmActivityMaster);
//                        crmca.setRid(java.util.UUID.randomUUID().toString());
//                        crmca.setCrmCase(crmCase);
//                        save(crmca);
//
//                        myjobj.put("validflag", crmActivityMaster.getValidflag());
//                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Case - " + crmCase.getSubject() + " ");
//                        crmActivityMaster.setMapwith(5);
//
//                    } else if (relatedtoid.equals("Campaign"))
//                    {
//                        CrmCampaign crmCamp = (CrmCampaign) get(CrmCampaign.class, relatedtonameid);
//                        CrmCampaignActivity crmca = new CrmCampaignActivity();
//                        crmca.setCrmActivityMaster(crmActivityMaster);
//                        crmca.setRid(java.util.UUID.randomUUID().toString());
//                        crmca.setCrmCampaign(crmCamp);
//                        save(crmca);
//
//                        myjobj.put("validflag", crmActivityMaster.getValidflag());
//                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Campaign - " + crmCamp.getCampaignname() + " ");
//                        crmActivityMaster.setMapwith(6);
//
//                    } else if (relatedtoid.equals("Opportunity"))
//                    {
//                        CrmOpportunity crmOpp = (CrmOpportunity) get(CrmOpportunity.class, relatedtonameid);
//                        CrmOpportunityActivity crmoa = new CrmOpportunityActivity();
//                        crmoa.setCrmActivityMaster(crmActivityMaster);
//                        crmoa.setRid(java.util.UUID.randomUUID().toString());
//                        crmoa.setCrmOpportunity(crmOpp);
//                        save(crmoa);
//
//                        myjobj.put("validflag", crmActivityMaster.getValidflag());
//                        myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Opportunity - " + crmOpp.getOppname() + " ");
//                        crmActivityMaster.setMapwith(7);
//                    }
//                    saveOrUpdate(crmActivityMaster);
//                } else if ((int) crmActivityMaster.getMapwith() == 4)
//                {
//                    CrmLead crmLead = (CrmLead) get(CrmLead.class, relatedtonameid);
//                    myjobj.put("validflag", crmActivityMaster.getValidflag());
//                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Lead - " + StringUtil.checkForNull(crmLead.getFirstname()) + " " + StringUtil.checkForNull(crmLead.getLastname()) + " ");
//                } else if ((int) crmActivityMaster.getMapwith() == 3)
//                {
//                    CrmContact crmCon = (CrmContact) get(CrmContact.class, relatedtonameid);
//                    myjobj.put("validflag", crmActivityMaster.getValidflag());
//                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Contact - " + (StringUtil.checkForNull(crmCon.getFirstname()) + " " + StringUtil.checkForNull(crmCon.getLastname())).trim() + " ");
//                } else if ((int) crmActivityMaster.getMapwith() == 2)
//                {
//                    CrmAccount crmAcc = (CrmAccount) get(CrmAccount.class, relatedtonameid);
//                    myjobj.put("validflag", crmActivityMaster.getValidflag());
//                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for  Account - " + crmAcc.getAccountname() + " ");
//                } else if ((int) crmActivityMaster.getMapwith() == 5)
//                {
//                    CrmCase crmCas = (CrmCase) get(CrmCase.class, relatedtonameid);
//                    myjobj.put("validflag", crmActivityMaster.getValidflag());
//                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Case - " + crmCas.getSubject() + " ");
//                } else if ((int) crmActivityMaster.getMapwith() == 6)
//                {
//                    CrmCampaign crmCam = (CrmCampaign) get(CrmCampaign.class, relatedtonameid);
//                    myjobj.put("validflag", crmActivityMaster.getValidflag());
//                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Campaign - " + crmCam.getCampaignname() + " ");
//                } else if ((int) crmActivityMaster.getMapwith() == 7)
//                {
//                    CrmOpportunity crmOpp = (CrmOpportunity) get(CrmOpportunity.class, relatedtonameid);
//                    myjobj.put("validflag", crmActivityMaster.getValidflag());
//                    myjobj.put("auditmsg", jobj.getString("auditstr") + " " + crmActivityMaster.getFlag() + " for Opportunity - " + crmOpp.getOppname() + " ");
//                }
//            }
            
//            ll.add(crmActivityMaster);
//            myjobj.put("success", true);
//            myjobj.put("flag", crmActivityMaster.getFlag());
//            myjobj.put("ID", id);
//            ll.add(myjobj);
        } catch (JSONException e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.editActivity : " + e.getMessage(), e);
        } catch (DataAccessException e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.editActivity : " + e.getMessage(), e);
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmActivityDAOImpl.editActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
}
