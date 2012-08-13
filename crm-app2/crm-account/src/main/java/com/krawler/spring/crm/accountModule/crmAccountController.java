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
package com.krawler.spring.crm.accountModule; 

import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import com.krawler.common.notification.bizservice.NotificationManagementService;
import com.krawler.common.notification.web.NotificationConstants;
import com.krawler.common.notification.web.NotificationConstants.CHANNEL;
import com.krawler.common.notification.web.NotificationConstants.NOTIFICATIONSTATUS;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.account.bizservice.AccountManagementService;
import com.krawler.crm.account.dm.AccountOwnerInfo;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import java.text.DateFormat;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.esp.web.resource.Links;
import com.krawler.spring.crm.common.CrmCommonService;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import static com.krawler.common.notification.web.NotificationConstants.*;

import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class crmAccountController extends MultiActionController {

    private crmAccountDAO crmAccountDAOObj;
    private commentDAO crmCommentDAOObj;
    private CrmCommonService crmCommonService;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImpl;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private fieldDataManager fieldDataManagercntrl;
    private AccountManagementService accountManagementService;
    private CometManagementService CometManagementService;
    private NotificationManagementService NotificationManagementServiceDAO;
    private fieldManagerDAO fieldManagerDAOobj;
    private static Log LOG = LogFactory.getLog(crmAccountController.class);

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    public NotificationManagementService getNotificationManagementServiceDAO() {
        return NotificationManagementServiceDAO;
    }

    public void setNotificationManagementServiceDAO(NotificationManagementService NotificationManagementServiceDAO) {
        this.NotificationManagementServiceDAO = NotificationManagementServiceDAO;
    }

    public void setCometManagementService(CometManagementService CometManagementService) {
        this.CometManagementService = CometManagementService;
    }

    /**
     * @return the accountManagementService
     */
    public AccountManagementService getAccountManagementService()
    {
        return accountManagementService;
    }

    /**
     * @param AccountManagementService the AccountManagementService to set
     */
    public void setAccountManagementService(AccountManagementService accountManagementService)
    {
        this.accountManagementService = accountManagementService;
    }

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }

    public crmAccountDAO getcrmAccountDAO(){
        return crmAccountDAOObj;
    }
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    
    public String getSuccessView() {
        return successView;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImpl1) {
        this.sessionHandlerImpl = sessionHandlerImpl1;
    }

    public ModelAndView getAccountFromId(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, SessionExpiredException {
        JSONObject jobj = new JSONObject();
        boolean isexport=false;
         HashMap<String, Object> requestParams = new HashMap<String, Object>();
        String companyid = sessionHandlerImpl.getCompanyid(request);
        String currencyid = sessionHandlerImpl.getCurrencyID(request);
        requestParams.put("isexport",isexport);
        requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
        requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_account_moduleid));
        HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap(requestParams);
       try{
            CrmAccount obj = (CrmAccount) kwlCommonTablesDAOObj.getObject("com.krawler.crm.database.tables.CrmAccount", request.getParameter("recid"));
            JSONObject tmpObj = new JSONObject();
            DateFormat dateFormat = getDateFormatterWithTimeZone(request);
            tmpObj = getAccountManagementService().getAccountJsonObject(obj, tmpObj, companyid, currencyid, FieldMap, isexport, dateFormat);
            jobj.put("data", tmpObj);
       } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            String isarchive = request.getParameter("isarchive");            
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            String iscustomcolumn = request.getParameter("iscustomcolumn");
            String xtype = request.getParameter("xtype");
            String xfield = request.getParameter("xfield");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            
            jobj = getAccountManagementService().getAccounts(companyid, userid, currencyid, selectExport, isarchive, searchJson, ss, config, isExport, heirarchyPerm,
                    field, direction, iscustomcolumn, xtype, xfield, start, limit, dateFormat, usersList);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    protected DateFormat getDateFormatterWithTimeZone(HttpServletRequest request) throws SessionExpiredException {
        String tZStr = sessionHandlerImpl.getTimeZoneDifference(request);
        
        DateFormat df = authHandler.getDateMDYFormatter(request);
        if (tZStr != null)
        {
            TimeZone zone = TimeZone.getTimeZone("GMT" + tZStr);
            df.setTimeZone(zone);
        }
        
        return df;
    }
    
    protected DateFormat getDateFormatterWithTimeZoneForExport(HttpServletRequest request) throws SessionExpiredException {
        String tZStr = sessionHandlerImpl.getTimeZoneDifference(request);
        
        DateFormat df = authHandler.getNewDateFormatter(request);
        if (tZStr != null)
        {
            TimeZone zone = TimeZone.getTimeZone("GMT" + tZStr);
            df.setTimeZone(zone);
        }
        
        return df;
    }

    public ModelAndView getAllAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
       try{
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            JSONArray jarr = new JSONArray();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.validflag");
            filter_params.add(1);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            filter_names.add("c.isarchive");
            filter_params.add(false);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            if(!heirarchyPerm) {
                filter_names.add("INao.usersByUserid.userID");
                filter_params.add(usersList);
            }

            kmsg = crmAccountDAOObj.getAllAccounts(requestParams, filter_names, filter_params);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmAccount obj = (CrmAccount) ite.next();
                JSONObject jtemp = new JSONObject();

                jtemp.put("caccountname", obj.getAccountname());
                jtemp.put("caccountid", obj.getAccountid());
                jtemp.put("crevenue", obj.getRevenue());
                jtemp.put("cwebsite", obj.getWebsite());
                jtemp.put("ccontactno", obj.getPhone());
                jarr.put(jtemp);
            }
            jobj.put("data", jarr);
       } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        CrmAccount acc = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try{
            boolean ownerChanged = false;
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String userid = sessionHandlerImpl.getUserid(request);
            String accountIds = jobj.getString("accountid");
            String arrayId[] = accountIds.split(",");
            Integer operationCode = CrmPublisherHandler.ADDRECORDCODE;
            String companyid = sessionHandlerImpl.getCompanyid(request);
            for (int i = 0; i < arrayId.length; i++) {
               String id = arrayId[i];
               jobj.put("userid", userid);
               jobj.put("companyid", companyid);
               jobj.put("updatedon", System.currentTimeMillis());
               jobj.put("accountid", id);
               JSONArray jcustomarray = null;
               if (jobj.has("customfield")) {
                   jcustomarray = jobj.getJSONArray("customfield");
               }

               if (id.equals("0")) {
                    if(jobj.has("accountownerid") && !jobj.getString("accountownerid").equals(userid)) {
                       ownerChanged = true;
                    }

                   id = java.util.UUID.randomUUID().toString();
                   jobj.put("accountid", id);
//                   if (jobj.has("customfield")) {
//                       fieldManager.storeCustomFields(jcustomarray, "account", true, id);
//                   }
//                   jobj = StringUtil.setcreatedonDate(jobj, request);
                   kmsg = crmAccountDAOObj.addAccounts(jobj);
                   HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                   customrequestParams.put("customarray", jcustomarray);
                   customrequestParams.put("modulename", Constants.Crm_Account_modulename);
                   customrequestParams.put("moduleprimarykey", Constants.Crm_Accountid);
                   customrequestParams.put("modulerecid", id);
                   customrequestParams.put("companyid", companyid);
                   customrequestParams.put("customdataclasspath", Constants.Crm_account_custom_data_classpath);
                   acc = (CrmAccount) kmsg.getEntityList().get(0);
                   if (jobj.has("customfield")) {
                       KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                       if (customDataresult!=null && customDataresult.getEntityList().size() > 0) {
                           jobj.put("CrmAccountCustomDataobj", id);
                           kmsg = crmAccountDAOObj.editAccounts(jobj);
                       }

                       

                       //                    fieldManager.storeCustomFields(jcustomarray,"account",true,id);
                   }
                // fetch auto-number columns only
                   HashMap<String, Object> fieldrequestParams = new HashMap<String, Object>();
                   fieldrequestParams.put("isexport", true);
                   fieldrequestParams.put("filter_names", Arrays.asList("companyid", "moduleid", "fieldtype"));
                   fieldrequestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_account_moduleid, Constants.CUSTOM_FIELD_AUTONUMBER));
                   KwlReturnObject AutoNoFieldMap = fieldManagerDAOobj.getFieldParams(fieldrequestParams);

                   // increment auto number if exist
                   if (AutoNoFieldMap.getEntityList().size() > 0) {
                       JSONArray autoNoData = fieldDataManagercntrl.setAutoNumberCustomData(customrequestParams, AutoNoFieldMap.getEntityList());
                       jobj.put(Constants.AUTOCUSTOMFIELD, autoNoData);
                   }   // END logic - auto no
                   if (acc.getValidflag() == 1) {
                       auditTrailDAOObj.insertAuditLog(AuditAction.ACCOUNT_CREATE,
                               ((acc.getAccountname() == null) ? "" : acc.getAccountname()) + " - Account created ",
                               request, id);
                   }

               } else {
                   // check if account owner changed
                   String mainownerId = crmAccountHandler.getMainAccOwner(crmAccountDAOObj,id);
                   if(!StringUtil.isNullOrEmpty(mainownerId)) {
                       if(!mainownerId.equals(jobj.optString("accountownerid",mainownerId))) {
                           ownerChanged = true;
                       }
                   }

                   operationCode = CrmPublisherHandler.UPDATERECORDCODE;
                   if (jobj.has("customfield")) {
                       HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                       customrequestParams.put("customarray", jcustomarray);
                       customrequestParams.put("modulename", Constants.Crm_Account_modulename);
                       customrequestParams.put("moduleprimarykey", Constants.Crm_Accountid);
                       customrequestParams.put("modulerecid", id);
                       customrequestParams.put("companyid", companyid);
                       customrequestParams.put("customdataclasspath", Constants.Crm_account_custom_data_classpath);
                       KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                       if (customDataresult!=null && customDataresult.getEntityList().size() > 0) {
                           jobj.put("CrmAccountCustomDataobj", id);
                       }
                   }
                    if(jobj.has("createdon")){
                       jobj.put("createdon", jobj.getLong("createdon"));
                   }
                   kmsg = crmAccountDAOObj.editAccounts(jobj);
                   acc = (CrmAccount) kmsg.getEntityList().get(0);
                   if (acc.getValidflag() == 1) {
                       auditTrailDAOObj.insertAuditLog(AuditAction.ACCOUNT_UPDATE,
                               jobj.getString("auditstr") + " Account - " + ((acc.getAccountname() == null) ? "" : acc.getAccountname()) + " ",
                               request, id);
                   }
               }
           }
            txnManager.commit(status);
            JSONObject cometObj = jobj;
           if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
        	   if(arrayId[0].equals("0"))
        		   crmCommonService.validateMassupdate(new String[]{acc.getAccountid()}, "Account", companyid);
        	   else
        		   crmCommonService.validateMassupdate(arrayId, "Account", companyid);
               cometObj.put("accountid",  accountIds);
               cometObj.put("ismassedit", true);
           }
               
           myjobj.put("success", true);
           myjobj.put("ID", acc.getAccountid());
           myjobj.put("createdon", jobj.has("createdon") ? jobj.getLong("createdon") : "");
           cometObj.put("createdon", acc.getCreatedon());
           myjobj.put("updatedon", jobj.has("updatedon") ? jobj.getLong("updatedon") : "");
           myjobj.put(Constants.AUTOCUSTOMFIELD, jobj.has(Constants.AUTOCUSTOMFIELD) ? jobj.getJSONArray(Constants.AUTOCUSTOMFIELD) : "");
           cometObj.put("updatedon", acc.getUpdatedon());
           cometObj.put(Constants.AUTOCUSTOMFIELD, jobj.has(Constants.AUTOCUSTOMFIELD) ? jobj.getJSONArray(Constants.AUTOCUSTOMFIELD) : "");
           publishAccountModuleInformation(request, cometObj, operationCode, companyid, userid);

              if (sessionHandlerImpl.getCompanyNotifyOnFlag(request)) {// send Notification if set flag at company level
                   if (ownerChanged) {// Send Notification if owner changed
                       List<String> recepients = new ArrayList();
                       recepients.add(jobj.getString("accountownerid"));
                       Map refTypeMap = new HashMap();
                       Map refIdMap = new HashMap();
                       refIdMap.put("refid1", acc.getAccountid());
                       refTypeMap.put("reftype1", Constants.Crm_account_classpath);
                       refIdMap.put("refid2", userid);
                       refTypeMap.put("reftype2", Constants.USERS_CLASSPATH);
                       String loginURL = URLUtil.getRequestPageURL(request, Links.loginpageFull);
                       HashMap<String, Object> extraParams = new HashMap<String, Object>();
                       extraParams.put(NotificationConstants.LOGINURL, loginURL);
                       extraParams.put(NotificationConstants.PARTNERNAME, sessionHandlerImpl.getPartnerName(request));
                       if (recepients.size() > 0) {
                           NotificationManagementServiceDAO.sendNotificationRequest(CHANNEL.EMAIL, ACCOUNT_ASSIGNED, NOTIFICATIONSTATUS.REQUEST, userid, recepients, refIdMap, refTypeMap, extraParams);
                       }
                   }
               }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    
    private void publishAccountModuleInformation(HttpServletRequest request, JSONObject cometObj, int operationCode, String companyid, String userid) throws ServiceException, JSONException, SessionExpiredException, ServiceException, com.krawler.utils.json.base.JSONException {
        String randomNumber = request.getParameter("randomnumber");
        CometManagementService.publishModuleInformation(companyid, userid, randomNumber, Constants.Crm_Account_modulename, Constants.Crm_accountid, operationCode, cometObj, CometConstants.CRMUPDATES);
    }

    public ModelAndView newAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        CrmAccount acc = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String userid = request.getParameter("userid");
            String accountname = request.getParameter("accountname");
            String website = request.getParameter("website");
            String description = request.getParameter("description");
            String contactno = request.getParameter("contactno");
            String revenue = request.getParameter("revenue");
            String id = java.util.UUID.randomUUID().toString();

            String companyid = sessionHandlerImpl.getCompanyid(request);
            jobj.put("accountid", id);
            jobj.put("accountownerid", userid);
            jobj.put("companyid", companyid);
            jobj.put("userid", userid);
            jobj.put("accountname", accountname);
            jobj.put("revenue", revenue);
            jobj.put("phone", contactno);
            jobj.put("description", description);
            jobj.put("website", website);
            jobj.put("validflag", 1);
            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
//            jobj.put("createdon", new Date());
            kmsg = crmAccountDAOObj.addAccounts(jobj);
            acc = (CrmAccount) kmsg.getEntityList().get(0);
            myjobj.put("success", true);
            myjobj.put("ID", acc.getAccountid());
            myjobj.put("validflag", acc.getValidflag());
            myjobj.put("accountname", acc.getAccountname());
            txnManager.commit(status);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView repAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        String jsondata = request.getParameter("val");
        KwlReturnObject kmsg = null;
        CrmAccount acc = null;
        JSONObject jobj;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            jobj = new JSONObject(jsondata);
            JSONArray jarr = new JSONArray();
            jarr = jobj.getJSONArray("userdata");

            for (int ctr = 0; ctr < jarr.length(); ctr++) {
                jobj = jarr.getJSONObject(ctr);
                String contactno = jobj.getString("contactno");
                jobj.put("phone", contactno);
                jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
                kmsg = crmAccountDAOObj.editAccounts(jobj);
                acc = (CrmAccount) kmsg.getEntityList().get(0);
            }
            myjobj.put("success", true);
            myjobj.put("ID", acc.getAccountid());
            txnManager.commit(status);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView deleteAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        KwlReturnObject kmsg = null;
        CrmAccount acc = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);

            myjobj = new JSONObject();
            myjobj.put("success", false);
            JSONArray jarr = null;

            if (request.getAttribute("deletejarr") != null) {
                jarr = (JSONArray) request.getAttribute("deletejarr");
            } else {
                String jsondata = request.getParameter("jsondata");
                jarr = new JSONArray("[" + jsondata + "]");
            }

            ArrayList accountids = new ArrayList();
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobject = jarr.getJSONObject(i);
                accountids.add(jobject.getString("accid").toString());
            }

            String[] arrayid = (String[]) accountids.toArray(new String[]{});
            JSONObject jobj = new JSONObject();
            jobj.put("deleteflag", 1);
            jobj.put("accountid", arrayid);
            jobj.put("userid", userid);
            jobj.put("updatedon", new Date().getTime());
            jobj.put("tzdiff", timeZoneDiff);

            kmsg = crmAccountDAOObj.updateMassAccount(jobj);

            List<CrmAccount> ll = crmAccountDAOObj.getAccounts(accountids);

            if (ll != null) {
                for (int i = 0; i < ll.size(); i++) {
                    CrmAccount accountaudit = (CrmAccount) ll.get(i);
                    if (accountaudit.getValidflag() == 1) {
                        auditTrailDAOObj.insertAuditLog(AuditAction.ACCOUNT_DELETE,
                                accountaudit.getAccountname() + " - Account deleted ",
                                request, accountaudit.getAccountid());
                    }
                }

            }

            if (request.getAttribute("failDelete") != null) {
                myjobj.put("failDelete", (JSONArray) request.getAttribute("failDelete"));
            }
            myjobj.put("successDeleteArr", jarr);
            myjobj.put("success", true);
            myjobj.put("ID", accountids.toArray());
            txnManager.commit(status);

            JSONObject cometObj = new JSONObject();
            cometObj.put("ids", jarr);
            publishAccountModuleInformation(request, cometObj, CrmPublisherHandler.DELETERECORDCODE, sessionHandlerImpl.getCompanyid(request), userid);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView accountExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        String view = "jsonView";
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            String isarchive = request.getParameter("isarchive");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);

            jobj = getAccountManagementService().AccountExport(companyid, userid, currencyid, selectExport, isarchive, searchJson,
                    ss, config, isExport, heirarchyPerm, field, direction, dateFormat, usersList);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.ACCOUNT_EXPORT,
                    "Account data exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
        	logger.warn("Exception while exporting accounts:", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView saveAccOwners(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try{
            String accid = request.getParameter("leadid");
            String owners = request.getParameter("owners");
            String mainowner = request.getParameter("mainOwner");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("accid", accid);
            requestParams.put("owners", owners);
            requestParams.put("mainOwner", mainowner);
            kmsg = crmAccountDAOObj.saveAccOwners(requestParams);
            // Fetch subowners name list
            List<String> idsList = new ArrayList<String>();
            idsList.add(accid);
            Map<String, List<AccountOwnerInfo>> ownersMap = crmAccountDAOObj.getAccountOwners(idsList);
            String[] ownerInfo = crmAccountHandler.getAllAccOwners(ownersMap.get(accid));
            myjobj.put("subowners", ownerInfo[1]);
            myjobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (SessionExpiredException e) {
        	logger.warn("SessionExpiredException while saving account owner:", e);
            txnManager.rollback(status);
        } catch (JSONException e) {
        	logger.warn("JSONException while saving account owner:", e);
            txnManager.rollback(status);
        } catch (ServiceException e) {
        	logger.warn("ServiceException while saving account owner:", e);
            txnManager.rollback(status);
        } catch (Exception e) {
        	logger.warn("Exception while saving account owner:", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView getExistingAccOwners(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
       try{
            String[] ownerInfo = crmAccountHandler.getAllAccOwners(crmAccountDAOObj, request.getParameter("leadid"));
            jobj.put("mainOwner",ownerInfo[2] );
            jobj.put("ownerids", ownerInfo[3]);
       } catch(Exception e) {
    	   logger.warn("Exception while getting Account owners:", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    /**
     * @param crmCommonService the crmCommonService to set
     */
    public void setCrmCommonService(CrmCommonService crmCommonService)
    {
        this.crmCommonService = crmCommonService;
    }

    public ModelAndView updateMassAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        CrmAccount acc = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            boolean ownerChanged = false;
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String userid = sessionHandlerImpl.getUserid(request);
            String accountIds = jobj.getString("accountid");
            String arrayId[] = accountIds.split(",");
            Integer operationCode = CrmPublisherHandler.UPDATERECORDCODE;
            String companyid = sessionHandlerImpl.getCompanyid(request);
            List<CrmAccount> ownerChangedAccounts=null;//new ArrayList<Object>();
            
            jobj.put("userid", userid);
            jobj.put("companyid", companyid);
            if (jobj.has("updatedon") && !StringUtil.isNullOrEmpty(jobj.getString("updatedon"))) {
               jobj.put("updatedon", jobj.getLong("updatedon"));
            } else {
               jobj.put("updatedon", new Date().getTime());
            }
            jobj.put("accountid", arrayId);
            jobj.put("tzdiff", sessionHandlerImpl.getTimeZoneDifference(request));
            JSONArray jcustomarray = null;
            if (jobj.has("customfield")) {
               jcustomarray = jobj.getJSONArray("customfield");
            }

            if(jobj.optString("accountownerid",null)!=null){
            	String newOwnerId=jobj.getString("accountownerid");
            	ownerChangedAccounts=crmAccountHandler.getOwnerChangedAccounts(crmAccountDAOObj, arrayId, newOwnerId);
            }

            if (jobj.has("customfield")) {
            	HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                customrequestParams.put("customarray", jcustomarray);
                customrequestParams.put("modulename", Constants.Crm_Account_modulename);
                customrequestParams.put("moduleprimarykey", Constants.Crm_Accountid);
                customrequestParams.put("companyid", companyid);
                customrequestParams.put("customdataclasspath", Constants.Crm_account_custom_data_classpath);
                KwlReturnObject customDataresult=null;
                for(String id : arrayId){
                    customrequestParams.put("modulerecid", id);
                    customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                }
                if (customDataresult != null && customDataresult.getEntityList().size() > 0) {
                   jobj.put("CrmAccountCustomDataobj", true);
                }
            }
            
            if (jobj.has("createdon") ) {
                jobj.put("createdon", jobj.getLong("createdon"));
            }
            kmsg = crmAccountDAOObj.updateMassAccount(jobj);
            
			// TODO : How to insert audit log when mass update
			// acc = (CrmAccount) kmsg.getEntityList().get(0);
			// if (acc.getValidflag() == 1) {
			// auditTrailDAOObj.insertAuditLog(AuditAction.ACCOUNT_UPDATE,
			// jobj.getString("auditstr") + " Account - " +
			// ((acc.getAccountname() == null) ? "" : acc.getAccountname()) +
			// " ",
			// request, id);
			// }

			// }
            
            txnManager.commit(status);
            JSONObject cometObj = jobj;
            if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
                crmCommonService.validateMassupdate(arrayId, "Account", companyid);
                cometObj.put("accountid", accountIds);
                cometObj.put("ismassedit", true);
            }

            myjobj.put("success", true);
            myjobj.put("ID", accountIds);
            myjobj.put("createdon", jobj.has("createdon") ? jobj.getLong("createdon") : "");
            if (!StringUtil.isNullObject(acc)) {
                if (!StringUtil.isNullObject(acc.getCreatedon())) {
                    cometObj.put("createdon", acc.getCreatedonGMT());
                }
            }

            publishAccountModuleInformation(request, cometObj, operationCode, companyid, userid);

            // TODO  : send Email when owner changed
            if (ownerChangedAccounts!=null && ownerChangedAccounts.size() > 0) {// Send Notification if owner changed
                List<String> recepients = new ArrayList<String>();
                recepients.add(jobj.getString("accountownerid"));
                String loginURL = URLUtil.getRequestPageURL(request, Links.loginpageFull);
            	HashMap<String, Object> extraParams = new HashMap<String, Object>();
            	extraParams.put(NotificationConstants.LOGINURL, loginURL);
            	extraParams.put(NotificationConstants.PARTNERNAME, sessionHandlerImpl.getPartnerName(request));   
            	StringBuffer variableData=new StringBuffer();
            	String accname="";
            	String type="";
            	int srno=0;
            	Map refTypeMap = new HashMap();
        		Map refIdMap = new HashMap();
            	refIdMap.put("refid1",(ownerChangedAccounts.get(0)).getAccountid());
    			refTypeMap.put("reftype1", Constants.Crm_account_classpath);
    			refIdMap.put("refid2", userid);
    			refTypeMap.put("reftype2", Constants.USERS_CLASSPATH);
    			
    			if(ownerChangedAccounts.size() > 1){
    				for(CrmAccount accountObj:ownerChangedAccounts){
    					srno++;
    					accname=!StringUtil.isNullOrEmpty(accountObj.getAccountname())?accountObj.getAccountname():"";
    					type=(!StringUtil.isNullObject(accountObj.getCrmCombodataByAccounttypeid()))?accountObj.getCrmCombodataByAccounttypeid().getValue():"";
    					variableData.append("<tr>");
    					variableData.append("<td> "+ srno +"</td>");
    					variableData.append("<td> "+ accname +"</td>");
    					variableData.append("<td> "+ type +"</td>");
    					variableData.append("</tr>");
    				}
    				extraParams.put(Constants.VARIABLEDATA,variableData);
    				NotificationManagementServiceDAO.sendNotificationRequest(CHANNEL.EMAIL, ACCOUNT_ASSIGNED_MASS_UPDATE, NOTIFICATIONSTATUS.REQUEST, userid, recepients, refIdMap, refTypeMap, extraParams);
    			}else{
    				NotificationManagementServiceDAO.sendNotificationRequest(CHANNEL.EMAIL, ACCOUNT_ASSIGNED, NOTIFICATIONSTATUS.REQUEST, userid, recepients, refIdMap, refTypeMap, extraParams);
    			}
    		
            }
            
            
        } catch (Exception e) {
            logger.warn("Exception while Accoount Mass Update:", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    
}
