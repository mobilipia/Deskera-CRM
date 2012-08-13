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
package com.krawler.crm.activity.bizservice;


import com.krawler.common.notification.bizservice.NotificationManagementService;
import com.krawler.common.notification.web.NotificationConstants;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.crm.activityModule.crmActivityDAO;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.stats.StatUtil;
import com.krawler.common.util.StringUtil;
import java.text.ParseException;
import com.krawler.crm.utils.Constants;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.calendar.calendarmodule.CalendarDao;
import com.krawler.calendar.calendarmodule.DeskeraCalendar;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import static com.krawler.common.notification.web.NotificationConstants.*;

/**
 * @author Ashutosh
 *
 */
public class ActivityManagementServiceImpl implements ActivityManagementService
{
    
    private crmActivityDAO crmActivityDAO;
    private NotificationManagementService NotificationManagementServiceDAO;
    private auditTrailDAO audiTrailDAO;
    
    /*private profileHandlerDAO profileHandlerDAO;*/
    
    private crmManagerDAO crmManagerDAO;
    
    private crmCommonDAO crmCommonDAO;
    
    private fieldManagerDAO fieldManagerDAO;

    private commentDAO crmCommentDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private static final Log LOGGER = LogFactory.getLog(ActivityManagementServiceImpl.class);
    private static Date dateWithZeroTime = new Date();
    static {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(cal.getTimeInMillis());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        dateWithZeroTime = cal.getTime();
    }

    private CalendarDao calendarDao;

    
    public void setCalendarDao(CalendarDao calendarDao) {
		this.calendarDao = calendarDao;
	}
    
    /**
     * @param fieldManagerDAO the fieldManagerDAO to set
     */
    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAO)
    {
        this.fieldManagerDAO = fieldManagerDAO;
    }

    public NotificationManagementService getNotificationManagementServiceDAO() {
        return NotificationManagementServiceDAO;
    }

    public void setNotificationManagementServiceDAO(NotificationManagementService NotificationManagementServiceDAO) {
        this.NotificationManagementServiceDAO = NotificationManagementServiceDAO;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject saveActivity(String companyid, String userId, String timeFormatId, String timeZoneDiff, String ipAddress, JSONObject jobj, boolean notifyFlag, String loginURL, String partnerName) throws ServiceException , JSONException, ParseException, SessionExpiredException
    {
        JSONObject myjobj = new JSONObject();
        myjobj.put("success", false);
        boolean ownerChanged = false;
        KwlReturnObject kmsg = null;
        CrmActivityMaster activity = null;
            String id = jobj.getString("activityid");
            jobj.put("userid", userId);
            jobj.put("companyid", companyid);
            jobj.put("updatedon", System.currentTimeMillis());
            jobj.put("tzdiff",timeZoneDiff);
            if(!jobj.has("statusid")){
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                List filter_names = new ArrayList();
                List filter_params = new ArrayList();

                filter_names.add("c.crmCombodata.valueid");
                filter_params.add(Constants.TASKSTATUS_NOTSTARTED);

                filter_names.add("c.company.companyID");
                filter_params.add(companyid);

                requestParams.put(Constants.filter_names, filter_names);
                requestParams.put(Constants.filter_params, filter_params);

                kmsg = getCrmCommonDAO().getDefaultMasterItem(requestParams);
                String statusid  = ((DefaultMasterItem) kmsg.getEntityList().get(0)).getID();
                jobj.put("statusid", statusid);
            }
            JSONArray jcustomarray = null;
            if(jobj.has("customfield")){
                jcustomarray = jobj.getJSONArray("customfield");
            }
            if (id.equals("0"))
            {
                id = java.util.UUID.randomUUID().toString();
                jobj.put("activityid", id);
                kmsg = getCrmActivityDAO().addActivity(jobj);
                activity = (CrmActivityMaster) kmsg.getEntityList().get(0);
                myjobj = (JSONObject)  kmsg.getEntityList().get(1);
                createAuditLog(kmsg, AuditAction.ACTIVITY_CREATE, userId, ipAddress, id);
            } else
            {
                kmsg = getCrmActivityDAO().editActivity(jobj);
                activity = (CrmActivityMaster) kmsg.getEntityList().get(0);
                myjobj = (JSONObject)  kmsg.getEntityList().get(1);
                createAuditLog(kmsg, AuditAction.ACTIVITY_UPDATE, userId, ipAddress, id);
            }
            myjobj.put("success", true);
            myjobj.put("ID", activity.getActivityid());

            if(jobj.has("startdate"))
                jobj.put("startdate",jobj.getLong("startdate"));
            if(jobj.has("enddate"))
            	jobj.put("enddate",jobj.getLong("enddate"));
            myjobj.put("data", jobj);

        if (notifyFlag) {// send Notification if set flag at company level
            if (jobj.has("ownerid") && !jobj.getString("ownerid").equals(userId) && activity.getValidflag()==1) {// Send notification if activity assigned to other user
                List<String> recepients = new ArrayList();
                recepients.add(jobj.getString("ownerid"));
                Map refTypeMap = new HashMap();
                Map refIdMap = new HashMap();
                refIdMap.put("refid1", id);
                refTypeMap.put("reftype1", Constants.CRM_ACTIVITY_CLASSPATH);
                String relatedtoid = jobj.getString("relatedtoid");
                int notifyType = ACT_REMAINDER;
                String classPath="";
                if (relatedtoid.equals("Account")) {
                    classPath = Constants.Crm_account_classpath;
                    notifyType = ACCOUNTACTIVITY_ASSIGNED;
                } else if (relatedtoid.equals("Contact")) {
                    classPath = Constants.Crm_contact_classpath;
                    notifyType = CONTACTACTIVITY_ASSIGNED;
                } else if (relatedtoid.equals("Lead")) {
                    classPath = Constants.Crm_lead_classpath;
                    notifyType = LEADACTIVITY_ASSIGNED;
                } else if (relatedtoid.equals("Case")) {
                    classPath = Constants.Crm_case_classpath;
                    notifyType = CASEACTIVITY_ASSIGNED;
                } else if (relatedtoid.equals("Campaign")) {
                    classPath = Constants.CRM_CAMPAIGN_CLASSPATH;
                    notifyType = CAMPAIGNACTIVITY_ASSIGNED;
                } else if (relatedtoid.equals("Opportunity")) {
                    classPath = Constants.Crm_opportunity_classpath;
                    notifyType = OPPACTIVITY_ASSIGNED;
                } 
                refIdMap.put("refid2", jobj.getString("relatedtonameid"));
                refTypeMap.put("reftype2", classPath);
                refIdMap.put("refid3", userId);
                refTypeMap.put("reftype3", Constants.USERS_CLASSPATH);
                HashMap<String, Object> extraParams = new HashMap<String, Object>();
                extraParams.put(NotificationConstants.LOGINURL, loginURL);
                extraParams.put(NotificationConstants.PARTNERNAME, partnerName);
                NotificationManagementServiceDAO.sendNotificationRequest(CHANNEL.EMAIL, notifyType, NOTIFICATIONSTATUS.REQUEST, userId, recepients, refIdMap, refTypeMap, extraParams);
            }
        }

        return myjobj;
    }
    
    protected void createAuditLog(KwlReturnObject kmsg, String actionId, String userId, String ipAddress, String id) throws ServiceException, JSONException
    {
        CrmActivityMaster activity = (CrmActivityMaster) kmsg.getEntityList().get(0);
        JSONObject myjobj = (JSONObject) kmsg.getEntityList().get(1);
        if (activity.getValidflag() == 1 && myjobj.has("auditmsg")) {
            getAudiTrailDAO().insertAuditLog(actionId, myjobj.getString("auditmsg"), ipAddress, userId, id);
        }
    }

    /**
     * @return the crmManagerDAO
     */
    public crmManagerDAO getCrmManagerDAO()
    {
        return crmManagerDAO;
    }

    /**
     * @param crmManagerDAO the crmManagerDAO to set
     */
    public void setCrmManagerDAO(crmManagerDAO crmManagerDAO)
    {
        this.crmManagerDAO = crmManagerDAO;
    }

    /**
     * @return the crmCommonDAO
     */
    public crmCommonDAO getCrmCommonDAO()
    {
        return crmCommonDAO;
    }

    /**
     * @param crmCommonDAO the crmCommonDAO to set
     */
    public void setCrmCommonDAO(crmCommonDAO crmCommonDAO)
    {
        this.crmCommonDAO = crmCommonDAO;
    }

    /**
     * @return the crmActivityDAO
     */
    public crmActivityDAO getCrmActivityDAO()
    {
        return crmActivityDAO;
    }

    /**
     * @param crmActivityDAO the crmActivityDAO to set
     */
    public void setCrmActivityDAO(crmActivityDAO crmActivityDAO)
    {
        this.crmActivityDAO = crmActivityDAO;
    }

    /**
     * @return the audiTrailDAO
     */
    public auditTrailDAO getAudiTrailDAO()
    {
        return audiTrailDAO;
    }

    /**
     * @param audiTrailDAO the audiTrailDAO to set
     */
    public void setAudiTrailDAO(auditTrailDAO audiTrailDAO)
    {
        this.audiTrailDAO = audiTrailDAO;
    }
    
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }

    /**
     *
     * @param companyid
     * @param userid
     * @param currencyid
     * @param selectExportJson
     * @param isArchive
     * @param searchJson
     * @param ss
     * @param config
     * @param isExport
     * @param status
     * @param heirarchyPerm
     * @param field
     * @param direction
     * @param iscustomcolumn
     * @param xfield
     * @param xtype
     * @param timeZoneDiff
     * @param timeFormat
     * @param module
     * @param mapid
     * @param dateFormat
     * @return
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getActivity(String companyid, String userid, String currencyid, String selectExportJson,
            boolean isArchive, String searchJson, String ss, String config, String isExport, String status,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xfield, String xtype,
            String timeZoneDiff, String timeFormat, String module, String mapid, DateFormat dateFormat, String start, String limit ,StringBuffer usersList) throws ServiceException
    {
            JSONObject jobj = new JSONObject();
            KwlReturnObject kmsg = null;

        try{

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", isArchive);
            if(searchJson != null && !StringUtil.isNullOrEmpty(searchJson)) {
                requestParams.put("searchJson", searchJson);
            }
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
            requestParams.put("status", status);
            if (config != null) {
                requestParams.put("config", config);
            }

            requestParams.put("export", isExport);
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(field)) {
                requestParams.put("field", field);
                requestParams.put("direction", direction);
                requestParams.put("iscustomcolumn", iscustomcolumn);
                requestParams.put("xfield", xfield);
                requestParams.put("xtype", xtype);
            }
            if(!StringUtil.isNullOrEmpty(start) && !StringUtil.isNullOrEmpty(limit)) {
                requestParams.put("start", start);
                requestParams.put("limit", limit);
            }

            ArrayList filter_names = new ArrayList((List<String>) Arrays.asList("c.crmActivityMaster.deleteflag", "c.crmActivityMaster.isarchive", "c.crmActivityMaster.company.companyID"));
            ArrayList filter_params = new ArrayList((List) Arrays.asList(0, isArchive, companyid));

            if(Constants.Lead.equals(module)) {
                if(!StringUtil.isNullOrEmpty(mapid)) {
                    filter_names.add("c.crmLead.leadid");
                    filter_params.add(mapid);
                }
                filter_names.add("c.crmLead.deleteflag");
                filter_params.add(0);
                kmsg = crmActivityDAO.getLeadActivity(requestParams, usersList, filter_names, filter_params);
            } else if(Constants.Opportunity.equals(module)) {
                if(!StringUtil.isNullOrEmpty(mapid)) {
                    filter_names.add("c.crmOpportunity.oppid");
                    filter_params.add(mapid);
                }
                filter_names.add("c.crmOpportunity.deleteflag");
                filter_params.add(0);
                kmsg = crmActivityDAO.getOpportunityActivity(requestParams, usersList, filter_names, filter_params);
            } else if(Constants.Contact.equals(module)) {
                if(!StringUtil.isNullOrEmpty(mapid)) {
                    filter_names.add("c.crmContact.contactid");
                    filter_params.add(mapid);
                }
                filter_names.add("c.crmContact.deleteflag");
                filter_params.add(0);
                kmsg = crmActivityDAO.getContactActivity(requestParams, usersList, filter_names, filter_params);
            } else if(Constants.Account.equals(module)) {
                if(!StringUtil.isNullOrEmpty(mapid)) {
                    filter_names.add("c.crmAccount.deleteflag");
                    filter_params.add(0);
                }
                filter_names.add("c.crmAccount.accountid");
                filter_params.add(mapid);
                kmsg = crmActivityDAO.getAccountActivity(requestParams, usersList, filter_names, filter_params);
            } else if(Constants.Campaign.equals(module)) {
                if(!StringUtil.isNullOrEmpty(mapid)) {
                    filter_names.add("c.crmCampaign.campaignid");
                    filter_params.add(mapid);
                }
                filter_names.add("c.crmCampaign.deleteflag");
                filter_params.add(0);
                kmsg = crmActivityDAO.getCampaignActivity(requestParams, usersList, filter_names, filter_params);
            } else if(Constants.Case.equals(module)) {
                if(!StringUtil.isNullOrEmpty(mapid)) {
                    filter_names.add("c.crmCase.caseid");
                    filter_params.add(mapid);
                }
                filter_names.add("c.crmCase.deleteflag");
                filter_params.add(0);
                kmsg = crmActivityDAO.getCaseActivity(requestParams, usersList, filter_names, filter_params);
            }
            if(StringUtil.isNullOrEmpty(isExport)) {
                jobj = getActivityJson(kmsg.getEntityList(), kmsg.getRecordTotalCount(), module, selectExportJson, userid, timeZoneDiff, timeFormat,dateFormat, companyid);
            } else {
                jobj = getActivityJsonExport(kmsg.getEntityList(), kmsg.getRecordTotalCount(), module, selectExportJson, userid, timeZoneDiff, timeFormat,dateFormat, companyid);
            }

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public JSONObject getActivityJsonObject(CrmActivityMaster obj, JSONObject tmpObj, String userid, String timeZoneDiff, String timeFormat, DateFormat dateFormat, HashMap<String, DefaultMasterItem> defaultMasterMap) {
        try {
        	 if(obj.isIsallday()){
        		 timeZoneDiff="+0:00";
        	 }
            tmpObj.put("activityid", obj.getActivityid());
            tmpObj.put("ownerid", obj.getUsersByUserid().getUserID());
            tmpObj.put("owner", obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
            tmpObj.put("subject", StringUtil.checkForNull(obj.getSubject()));
            tmpObj.put("statusid", StringUtil.hNull(obj.getStatusID()));
            String status = "";
            if(obj.getStatusID() != null && defaultMasterMap.containsKey(obj.getStatusID())) {
                status = defaultMasterMap.get(obj.getStatusID()).getValue();
            }
            tmpObj.put("status", status);
            tmpObj.put("priorityid", StringUtil.hNull(obj.getPriorityID()));
            String priority = "";
            if(obj.getPriorityID() != null && defaultMasterMap.containsKey(obj.getPriorityID())) {
                priority = defaultMasterMap.get(obj.getPriorityID()).getValue();
            }
            tmpObj.put("priority", priority);
            tmpObj.put("typeid", StringUtil.hNull(obj.getTypeID()));
            String type = "";
            if(obj.getTypeID() != null && defaultMasterMap.containsKey(obj.getTypeID())) {
                type = defaultMasterMap.get(obj.getTypeID()).getValue();
            }
            tmpObj.put("type", type);
            tmpObj.put(Constants.phone, StringUtil.checkForNull(obj.getPhone()));
            tmpObj.put(Constants.email, StringUtil.checkForNull(obj.getEmail()));
            tmpObj.put("startdate", obj.getStartDate()!=null?obj.getStartDate():"");
            tmpObj.put("enddate", obj.getEndDate()!=null?obj.getEndDate():"");
            tmpObj.put("startdat", obj.getStartDate()!=null?obj.getStartDate():"");
            tmpObj.put("enddat", obj.getEndDate()!=null?obj.getEndDate():"");
            tmpObj.put("flag", StringUtil.checkForNull(obj.getFlag()));
            tmpObj.put("createdon",obj.getCreatedOn()!=null?obj.getCreatedOn():"");
            tmpObj.put("validflag", obj.getValidflag());
            if (obj.getCellstyle() != null && !obj.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(obj.getCellstyle()));
            }
            String calname = Constants.DEFAULT_CALENDAR_NAME;
            String calid = obj.getCalendarid();
            if(!StringUtil.isNullOrEmpty(calid) && (!StringUtil.equal(calid, obj.getCompany().getCompanyID()))){
                List<DeskeraCalendar> cals = calendarDao.getCalendars(calid);
                if(!cals.isEmpty())
                	calname = cals.get(0).getName();
            }
            tmpObj.put("isallday", obj.isIsallday());
            tmpObj.put("calname", calname );
            tmpObj.put("calid", StringUtil.checkForNull(obj.getCalendarid()));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return tmpObj;
    }
    public JSONObject getActivityRelatedJson(String activityid, int moduleId) {
        JSONObject dataJobj = new JSONObject();
        String moduleRecId = crmActivityDAO.getPrimaryKey(activityid, moduleId);
        try {
            JSONObject jobj = new JSONObject();
            jobj.put("moduleRecId", moduleRecId);
            dataJobj.put("success", true);
            dataJobj.append("data", jobj);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return dataJobj;
    }
    public JSONObject getActivityJson(List<Object[]> ll,int totalSize, String module, String selectExportJson, String userid, String timeZoneDiff, String timeFormat, DateFormat dateFormat, String companyid) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        Object totalcomment = 0;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            List filter_names = new ArrayList();
            List filter_params = new ArrayList();
            //Get ids of crmCombomaster table
            String masterIds = "'"+Constants.ACTIVITY_STATUSID+"',";
            masterIds += "'"+Constants.ACTIVITY_PRIORITYID+"',";
            masterIds += "'"+Constants.ACTIVITY_TYPEID+"'";
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(Constants.filter_names, filter_names);
            requestParams.put(Constants.filter_params, filter_params);
            HashMap<String, DefaultMasterItem> defaultMasterMap = getCrmCommonDAO().getDefaultMasterItemsMap(requestParams);
            HashMap<String, String> commentCountMap = crmCommentDAOObj.getNewCommentCount(userid);

            int fromIndex = 0;
             int maxNumbers = Constants.maxrecordsMapCount;
             int totalCount = ll.size();
             while(fromIndex < totalCount) {
                 List<Object[]> subList;
                 if(totalCount <= maxNumbers) {
                    subList = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    subList = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (Object row[] : subList) {
                    idsList.add(((CrmActivityMaster) row[0]).getActivityid());
                 }
                 HashMap<String, String> totalCommentCountMap = crmCommentDAOObj.getTotalCommentsCount(idsList, companyid);
                 for (Object row[] : subList) {
                    CrmActivityMaster obj = (CrmActivityMaster) row[0];
                    JSONObject tmpObj = new JSONObject();
                    if (Constants.Account.equals(module)) {
                        CrmAccount obj2 = (CrmAccount) row[1];
                        tmpObj.put("relatednameid", obj2.getAccountid());
                        tmpObj.put("relatedname", obj2.getAccountname());
                    } else if (Constants.Lead.equals(module)) {
                        CrmLead obj2 = (CrmLead) row[1];
                        tmpObj.put("relatednameid", obj2.getLeadid());
                        tmpObj.put("relatedname", obj2.getLastname());
                    } else if (Constants.Opportunity.equals(module)) {
                        CrmOpportunity obj2 = (CrmOpportunity) row[1];
                        tmpObj.put("relatednameid", obj2.getOppid());
                        tmpObj.put("relatedname", obj2.getOppname());
                    } else if (Constants.Contact.equals(module)) {
                        CrmContact obj2 = (CrmContact) row[1];
                        tmpObj.put("relatednameid", obj2.getContactid());
                        tmpObj.put("relatedname", obj2.getLastname());
                    } else if (Constants.Case.equals(module)) {
                        CrmCase obj2 = (CrmCase) row[1];
                        tmpObj.put("relatednameid", obj2.getCaseid());
                        tmpObj.put("relatedname", obj2.getSubject());
                    } else if (Constants.Campaign.equals(module)) {
                        CrmCampaign obj2 = (CrmCampaign) row[1];
                        tmpObj.put("relatednameid", obj2.getCampaignid());
                        tmpObj.put("relatedname", obj2.getCampaignname());
                    }
                    tmpObj.put("relatedto", module);
                    tmpObj.put("relatedtoold", module);
                    totalcomment = 0;
                    if(totalCommentCountMap.containsKey(obj.getActivityid())) {
                        totalcomment = totalCommentCountMap.get(obj.getActivityid());
                    }
                    tmpObj.put("totalcomment", totalcomment);
                    tmpObj.put("commentcount", (commentCountMap.containsKey(obj.getActivityid())?commentCountMap.get(obj.getActivityid()):"0"));
                    getActivityJsonObject(obj, tmpObj, userid, timeZoneDiff, timeFormat, dateFormat, defaultMasterMap);
                    jarr.put(tmpObj);
                 }
                 fromIndex += maxNumbers+1;
             }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public JSONObject getActivityJsonExport(List<Object[]> ll,int totalSize, String module, String selectExportJson, String userid, String timeZoneDiff, String timeFormat, DateFormat dateFormat, String companyid) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            List filter_names = new ArrayList();
            List filter_params = new ArrayList();
            //Get ids of crmCombomaster table
            String masterIds = "'"+Constants.ACTIVITY_STATUSID+"',";
            masterIds += "'"+Constants.ACTIVITY_PRIORITYID+"',";
            masterIds += "'"+Constants.ACTIVITY_TYPEID+"'";
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(Constants.filter_names, filter_names);
            requestParams.put(Constants.filter_params, filter_params);
            HashMap<String, DefaultMasterItem> defaultMasterMap = getCrmCommonDAO().getDefaultMasterItemsMap(requestParams);
            
            String jsondata = selectExportJson;
            if (StringUtil.isNullOrEmpty(jsondata)) {
                    for (Object row[] : ll) {
                        CrmActivityMaster obj = (CrmActivityMaster) row[0];
                        JSONObject tmpObj = new JSONObject();
                        if (Constants.Account.equals(module)) {
                            CrmAccount obj2 = (CrmAccount) row[1];
                            tmpObj.put("relatednameid", obj2.getAccountid());
                            tmpObj.put("relatedname", obj2.getAccountname());
                        } else if (Constants.Lead.equals(module)) {
                            CrmLead obj2 = (CrmLead) row[1];
                            tmpObj.put("relatednameid", obj2.getLeadid());
                            tmpObj.put("relatedname", obj2.getLastname());
                        } else if (Constants.Opportunity.equals(module)) {
                            CrmOpportunity obj2 = (CrmOpportunity) row[1];
                            tmpObj.put("relatednameid", obj2.getOppid());
                            tmpObj.put("relatedname", obj2.getOppname());
                        } else if (Constants.Contact.equals(module)) {
                            CrmContact obj2 = (CrmContact) row[1];
                            tmpObj.put("relatednameid", obj2.getContactid());
                            tmpObj.put("relatedname", obj2.getLastname());
                        } else if (Constants.Case.equals(module)) {
                            CrmCase obj2 = (CrmCase) row[1];
                            tmpObj.put("relatednameid", obj2.getCaseid());
                            tmpObj.put("relatedname", obj2.getSubject());
                        } else if (Constants.Campaign.equals(module)) {
                            CrmCampaign obj2 = (CrmCampaign) row[1];
                            tmpObj.put("relatednameid", obj2.getCampaignid());
                            tmpObj.put("relatedname", obj2.getCampaignname());
                        }
                        tmpObj.put("relatedto", module);
                        tmpObj.put("relatedtoold", module);
                        getActivityJsonObject(obj, tmpObj, userid, timeZoneDiff, timeFormat, dateFormat, defaultMasterMap);
                        jarr.put(tmpObj);
                    }
            } else {
                JSONArray jArr = new JSONArray("[" + jsondata + "]");
                totalSize = jArr.length();
                for (int i = 0; i < totalSize; i++) {
                    JSONObject jObj = jArr.getJSONObject(i);
                    CrmActivityMaster obj = (CrmActivityMaster) kwlCommonTablesDAOObj.getObject("com.krawler.crm.database.tables.CrmActivityMaster", jObj.getString("id"));
                    JSONObject tmpObj = new JSONObject();
                    tmpObj = getActivityJsonObject(obj, tmpObj, userid, timeZoneDiff, timeFormat, dateFormat, defaultMasterMap);
                    jarr.put(tmpObj);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }


}
