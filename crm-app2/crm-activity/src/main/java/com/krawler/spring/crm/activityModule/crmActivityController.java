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
import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.SystemUtil;
import com.krawler.common.util.URLUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import java.util.Iterator;
import com.krawler.crm.utils.Constants;
import javax.servlet.http.HttpServletRequest;

import com.krawler.crm.activity.bizservice.ActivityManagementService;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.esp.web.resource.Links;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class crmActivityController extends MultiActionController {

    private crmActivityDAO crmActivityDAOObj;

    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImpl;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private String successView;

    private crmAccountDAO crmAccountDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmContactDAO crmContactDAOObj;
    private crmLeadDAO crmLeadDAOObj;
    private crmCaseDAO crmCaseDAOObj;
    private HibernateTransactionManager txnManager;
    private crmCommonDAO crmCommonDAOObj;

    private ActivityManagementService activityManagementService;

    private static final Log LOGGER = LogFactory.getLog(crmActivityController.class);
    private CometManagementService CometManagementService;

    public void setCometManagementService(CometManagementService CometManagementService) {
        this.CometManagementService = CometManagementService;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setcrmCaseDAO(crmCaseDAO crmCaseDAOObj1) {
        this.crmCaseDAOObj = crmCaseDAOObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setcrmActivityDAO(crmActivityDAO crmActivityDAOObj1) {
        this.crmActivityDAOObj = crmActivityDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImpl1) {
        this.sessionHandlerImpl = sessionHandlerImpl1;
    }

    public ModelAndView getActivity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       try{
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);

            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            String selectExport = request.getParameter("selectExport");
            boolean isArchive = Boolean.parseBoolean(request.getParameter("isarchive"));
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String iscustomcolumn = request.getParameter("iscustomcolumn");
            String xfield = request.getParameter("xfield");
            String xtype = request.getParameter("xtype");
            String mapid = request.getParameter("mapid");
            String status = request.getParameter("status");
            String isExport = request.getParameter("reportid");
            String start = StringUtil.checkForNull(request.getParameter("start"));
            String limit = StringUtil.checkForNull(request.getParameter("limit"));

            String module = request.getParameter("module");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            
            jobj = activityManagementService.getActivity(companyid, userid, currencyid, selectExport,
                    isArchive, searchJson, ss, config, isExport, status, heirarchyPerm, field, direction, iscustomcolumn,
                    xfield, xtype, timeZoneDiff, timeFormatId, module, mapid, dateFormat, start, limit,usersList);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView activityExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        String view = "jsonView";
        try {
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);

            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            String selectExport = request.getParameter("selectExport");
            boolean isArchive = false;//request.getParameter("isarchive");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String iscustomcolumn = request.getParameter("iscustomcolumn");
            String xfield = request.getParameter("xfield");
            String xtype = request.getParameter("xtype");
            String mapid = request.getParameter("mapid");
            String status = request.getParameter("status");
            String isExport = request.getParameter("reportid");
            String start = "";
            String limit = "";

            String module = "";
            String auditAction = "";
            if(isExport.contains("Account")) {
                module = Constants.Account;
                auditAction = AuditAction.ACCOUNT_ACTIVITY_EXPORT;
            } else if(isExport.contains("Lead")) {
                module = Constants.Lead;
                auditAction = AuditAction.LEAD_ACTIVITY_EXPORT;
            } else if(isExport.contains("Contact")) {
                module = Constants.Contact;
                auditAction = AuditAction.CONTACT_ACTIVITY_EXPORT;
            } else if(isExport.contains("Opportunity")) {
                module = Constants.Opportunity;
                auditAction = AuditAction.OPPORTUNITY_ACTIVITY_EXPORT;
            } else if(isExport.contains("Case")) {
                module = Constants.Case;
                auditAction = AuditAction.CASE_ACTIVITY_EXPORT;
            } else if(isExport.contains("Campaign")) {
                module = Constants.Campaign;
                auditAction = AuditAction.CAMPAIGN_ACTIVITY_EXPORT;
            }
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            
            jobj = getActivityManagementService().getActivity(companyid, userid, currencyid, selectExport,
                    isArchive, searchJson, ss, config, isExport, status, heirarchyPerm, field, direction, iscustomcolumn,
                    xfield, xtype, timeZoneDiff, timeFormatId, module, mapid, dateFormat, start, limit,usersList);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(auditAction, module+"-Activity data exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView saveActivity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject resultJson = new JSONObject();
        try
        {
            // extract ip address
            String ipAddress = SystemUtil.getIpAddress(request);
            // extract company id, user id, time format id, etc
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userId = sessionHandlerImpl.getUserid(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            String jsonData = request.getParameter("jsondata");
            Integer operationCode = CrmPublisherHandler.ADDRECORDCODE;
            String id = new JSONObject(jsonData).getString("activityid");
            JSONObject jobj = new JSONObject(jsonData);
            if("true".equals(jobj.optString("allday","false"))){
            	timeZoneDiff="+0:00";
            }
            if(jobj.has("enddate")) {
                jobj.put("enddate",jobj.getLong("enddate"));
            }
            if(jobj.has("startdate")) {
                jobj.put("startdate",jobj.getLong("startdate"));
            }
            if(jobj.has("tilldate")) {
                jobj.put("tilldate",jobj.getLong("tilldate"));
            }
            if(jobj.has("calendarid")) {
                if(!jobj.getString("calendarid").equals("")){
                    jobj.put("calendarid", (jobj.getString("calendarid")));
                } else {
                    jobj.put("calendarid", companyid);
                }
            }
          
            if (!id.equals("0")) {
                operationCode = CrmPublisherHandler.UPDATERECORDCODE;
            }
            boolean notifyFlag = sessionHandlerImpl.getCompanyNotifyOnFlag(request);
            String loginURL = URLUtil.getRequestPageURL(request, Links.loginpageFull);
            String partnerName = sessionHandlerImpl.getPartnerName(request);
            resultJson = activityManagementService.saveActivity(companyid, userId, timeFormatId, timeZoneDiff, ipAddress, jobj, notifyFlag,loginURL,partnerName);

            JSONObject cometObj = new JSONObject(resultJson.getString("data"));
            if(resultJson.has("success") && resultJson.getBoolean("success")) {
                   cometObj.put("activityid", resultJson.has("ID")?resultJson.getString("ID"):"");
            }
            
            cometObj.put("calid", cometObj.has("calendarid") ? cometObj.getString("calendarid") : "");   // added for commet
            publishActivityModuleInformation(request, cometObj, operationCode, companyid, userId);
        } catch (ServiceException ex) {
            Logger.getLogger(crmActivityController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(crmActivityController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(crmActivityController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SessionExpiredException se)
        {

        }

        return new ModelAndView("jsonView", "model", resultJson.toString());
    }

    private void publishActivityModuleInformation(HttpServletRequest request, JSONObject cometObj, int operationCode, String companyid, String userid) throws ServiceException, JSONException, SessionExpiredException {
        String randomNumber = request.getParameter("randomnumber");
        CometManagementService.publishModuleInformation(companyid, userid, randomNumber, Constants.CRM_ACTIVITY_MODULENAME, Constants.Crm_activityid, operationCode, cometObj, CometConstants.CRMUPDATES);
    }
    public ModelAndView deleteActivity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        KwlReturnObject kmsg = null;
        CrmActivityMaster activity = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            ArrayList activityids = new ArrayList();
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            myjobj = new JSONObject();
            myjobj.put("success", false);
            if (StringUtil.bNull(request.getParameter("jsondata"))) {
                String jsondata = request.getParameter("jsondata");
                JSONArray jarr = new JSONArray("[" + jsondata + "]");

                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobject = jarr.getJSONObject(i);
                    activityids.add(jobject.getString("activityid").toString());
                }
                
                String[] arrayid = (String[]) activityids.toArray(new String[]{});
                JSONObject jobj = new JSONObject();
                jobj.put("deleteflag", 1);
                jobj.put("activityid", arrayid);
                jobj.put("userid", userid);
                jobj.put("updatedon", new Date());
                jobj.put("tzdiff",timeZoneDiff);

                kmsg = crmActivityDAOObj.updateMassActivity(jobj);

                List<CrmActivityMaster> ll = crmActivityDAOObj.getActivities(activityids);

                if(ll!=null){
                    for(int i =0 ; i< ll.size() ; i++){
                        CrmActivityMaster activityaudit = (CrmActivityMaster)ll.get(i);
                        if (activityaudit.getValidflag() == 1) {
                            auditTrailDAOObj.insertAuditLog(AuditAction.ACTIVITY_DELETE,
                                  activityaudit.getFlag() + " - Activity deleted ",
                                    request, activityaudit.getActivityid());
                        }
                    }
                }
                
                JSONObject cometObj = new JSONObject();
                cometObj.put("ids",  jarr);
                publishActivityModuleInformation(request, cometObj, CrmPublisherHandler.DELETERECORDCODE, sessionHandlerImpl.getCompanyid(request),userid);
            }
            myjobj.put("success", true);
            myjobj.put("ID", activityids.toArray());
            txnManager.commit(status);
        } catch (JSONException e) {
            LOGGER.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView getRelatedToCombo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String relatedto = request.getParameter("relatedtoid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            JSONArray jarr = new JSONArray();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add(Constants.c_deleteflag);
            filter_params.add(0);
            filter_names.add(Constants.c_validflag);
            filter_params.add(1);
            filter_names.add(Constants.c_company_companyID);
            filter_params.add(companyid);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            if (relatedto.equals(Constants.Account)) {
                boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Account);
                if(!heirarchyPerm) {
                    filter_names.add(ActivityConstants.Crm_INao_usersByUserid_userID);
                    filter_params.add(usersList);
                }
                kmsg = crmAccountDAOObj.getAllAccounts(requestParams, filter_names, filter_params);
                Iterator ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmAccount obj = (CrmAccount) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put(Constants.id, obj.getAccountid());
                    tmpObj.put(Constants.name, obj.getAccountname());
                    tmpObj.put(Constants.phone, obj.getPhone());
                    tmpObj.put(Constants.email, obj.getEmail());
                    tmpObj.put(ActivityConstants.Crm_isarchive, obj.getIsarchive());
                    tmpObj.put(ActivityConstants.Crm_hasAccess,!obj.getIsarchive());
                    jarr.put(tmpObj);
                }
            }
            if (relatedto.equals(Constants.Opportunity)) {
                boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Opportunity);
                if(!heirarchyPerm){
                    filter_names.add(ActivityConstants.Crm_INoo_usersByUserid_userID);filter_params.add(usersList);
                }
                requestParams.put(Constants.filter_names, filter_names);
                requestParams.put(Constants.filter_values, filter_params);

                kmsg = crmOpportunityDAOObj.getAllOpportunities(requestParams);
                Iterator ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmOpportunity obj = (CrmOpportunity) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put(Constants.id, obj.getOppid());
                    tmpObj.put(Constants.name, obj.getOppname());
                    tmpObj.put(Constants.phone, 0);
                    tmpObj.put(Constants.email, "");
                    tmpObj.put(ActivityConstants.Crm_hasAccess,!obj.getIsarchive());
                    jarr.put(tmpObj);
                }
            }
            if (relatedto.equals(Constants.Contact)) {
                boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Contact);
                if(!heirarchyPerm){
                    filter_names.add(ActivityConstants.Crm_INco_usersByUserid_userID);filter_params.add(usersList);
                }
                requestParams.put(Constants.filter_names, filter_names);
                requestParams.put(Constants.filter_values, filter_params);

                kmsg = crmContactDAOObj.getAllContacts(requestParams);
                Iterator ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmContact obj = (CrmContact) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put(Constants.id, obj.getContactid());
                    tmpObj.put(Constants.name, (StringUtil.checkForNull(obj.getFirstname()) + " " + StringUtil.checkForNull(obj.getLastname())).trim());
                    tmpObj.put(Constants.phone, obj.getPhoneno());
                    tmpObj.put(Constants.email, obj.getEmail());
                    tmpObj.put(ActivityConstants.Crm_hasAccess,!obj.getIsarchive());
                    jarr.put(tmpObj);
                }
            }
            if (relatedto.equals(Constants.Lead)) {
                filter_names.add("c.istransfered");
                filter_params.add("0");
                boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Lead);
                if(!heirarchyPerm) {
                    filter_names.add(ActivityConstants.Crm_INlo_usersByUserid_userID);
                    filter_params.add(usersList);
                }
                requestParams.put("filterQaulified", true);
                kmsg = crmLeadDAOObj.getAllLeads(requestParams, filter_names, filter_params);
                Iterator ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmLead obj = (CrmLead) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put(Constants.id, obj.getLeadid());
                    tmpObj.put(Constants.name, obj.getLastname());
                    tmpObj.put(Constants.phone, obj.getPhone());
                    tmpObj.put(Constants.email, obj.getEmail());
                    tmpObj.put(ActivityConstants.Crm_hasAccess,!obj.getIsarchive());
                    jarr.put(tmpObj);
                }
            }
            if (relatedto.equals(Constants.Case)) {
                boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
                requestParams.put("heirarchyPerm", heirarchyPerm);
                requestParams.put(Constants.filter_names, filter_names);
                requestParams.put(Constants.filter_params, filter_params);
                requestParams.put("userlist", usersList);
                kmsg = crmCaseDAOObj.getCases(requestParams);
                Iterator ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmCase obj = (CrmCase) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put(Constants.id, obj.getCaseid());
                    tmpObj.put(Constants.name, obj.getSubject());
                    tmpObj.put(Constants.phone, 0);
                    tmpObj.put(Constants.email, "");
                    tmpObj.put(ActivityConstants.Crm_hasAccess,!obj.getIsarchive());
                    jarr.put(tmpObj);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", kmsg.getRecordTotalCount());

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    /**
     * @return the activityManagementService
     */
    public ActivityManagementService getActivityManagementService()
    {
        return activityManagementService;
    }

    /**
     * @param activityManagementService the activityManagementService to set
     */
    public void setActivityManagementService(ActivityManagementService activityManagementService)
    {
        this.activityManagementService = activityManagementService;
    }
}
