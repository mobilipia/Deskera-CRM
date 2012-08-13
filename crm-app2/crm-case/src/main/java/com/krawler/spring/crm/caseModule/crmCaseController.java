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
import com.krawler.common.admin.Docs;
import com.krawler.common.admin.User;
import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import com.krawler.common.notification.bizservice.NotificationManagementService;
import com.krawler.common.notification.web.NotificationConstants;
import static com.krawler.common.notification.web.NotificationConstants.*;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.crm.utils.Constants;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.account.dm.AccountOwnerInfo;
import com.krawler.crm.cases.bizservice.CaseManagementService;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.esp.handlers.FileUploadHandler;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.esp.web.resource.Links;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.common.CrmCommonService;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountHandler;
import com.krawler.spring.documents.documentDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class crmCaseController extends MultiActionController {
    private crmCaseDAO crmCaseDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private CrmCommonService crmCommonService;
    private exportDAOImpl exportDAOImplObj;
    private crmContactDAO crmContactDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private commentDAO crmCommentDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private fieldDataManager fieldDataManagercntrl;
    private CaseManagementService caseManagementService;
    private CometManagementService CometManagementService;
    private NotificationManagementService NotificationManagementServiceDAO;
    private documentDAO crmDocumentDAOObj;
    private fieldManagerDAO fieldManagerDAOobj;

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

    public void setCaseManagementService(CaseManagementService caseManagementService) {
        this.caseManagementService = caseManagementService;
    }

    public CaseManagementService getCaseManagementService() {
        return this.caseManagementService;
    }
    
    private static final Log LOGGER = LogFactory.getLog(crmCaseController.class);

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    public void setCrmCommonService(CrmCommonService crmCommonService)
    {
        this.crmCommonService = crmCommonService;
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

    public void setcrmCaseDAO(crmCaseDAO crmCaseDAOObj1) {
        this.crmCaseDAOObj = crmCaseDAOObj1;
    }

    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    public void setcrmDocumentDAO(documentDAO crmDocumentDAOObj1) {
        this.crmDocumentDAOObj = crmDocumentDAOObj1;
    }
    
       
    public ModelAndView getAllAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       KwlReturnObject kmsg = null;
       int dl = 0;
       try{
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
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
            
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            boolean heirarchyPerm=crmManagerCommon.chkHeirarchyPerm(request, "Account");
            String hierarchy= request.getParameter("hierarchy");
            if(!StringUtil.isNullOrEmpty(hierarchy) && Boolean.parseBoolean(hierarchy)){
                if(!heirarchyPerm) {
                    filter_names.add("INao.usersByUserid.userID");
                    filter_params.add(usersList);
                }
            }

            String ss = request.getParameter("query");
            if(!StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("pagingFlag", true);
                requestParams.put("start", 0);
                requestParams.put("limit", com.krawler.common.util.Constants.REMOTE_STORE_PAGE_LIMIT);
                requestParams.put("ss", ss);
            }

            kmsg = crmAccountDAOObj.getAllAccounts(requestParams, filter_names, filter_params);
            dl = kmsg.getEntityList().size();

            JSONObject tmpOb = crmManagerCommon.insertNone();
            jarr.put(tmpOb);
            boolean hasAccess ;

            List<CrmAccount> ll = kmsg.getEntityList();
             int fromIndex = 0;
             int maxNumbers = Constants.maxrecordsMapCount;
             int totalCount = ll.size();
             while(fromIndex < totalCount) {
                 List<CrmAccount> subList;
                 if(totalCount <= maxNumbers) {
                    subList = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    subList = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (CrmAccount obj : subList) {
                    idsList.add(obj.getAccountid());
                 }
                 Map<String, List<AccountOwnerInfo>> owners = crmAccountDAOObj.getAccountOwners(idsList);

                 for (CrmAccount obj : subList) {
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("id", obj.getAccountid());
                    tmpObj.put("name", obj.getAccountname());
                    tmpObj.put("phone", obj.getPhone());
                    tmpObj.put("email", obj.getEmail());
                    tmpObj.put(CaseConstants.Crm_isarchive, obj.getIsarchive());
                    tmpObj.put("productid", crmManagerCommon.moduleObjNull(obj.getCrmProduct(), "Productid"));
                    if(!obj.getIsarchive() && StringUtil.isNullOrEmpty(hierarchy) ){
                        if(!heirarchyPerm) {
                            hasAccess= crmAccountHandler.hasAccountAccess(owners.get(obj.getAccountid()),usersList);
                        }else{
                            hasAccess = true;
                        }
                        tmpObj.put(CaseConstants.Crm_hasAccess,hasAccess );
                    }else{
                        tmpObj.put(CaseConstants.Crm_hasAccess,!obj.getIsarchive());
                    }
                    jarr.put(tmpObj);
                }
                fromIndex += maxNumbers+1;
             }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
       } catch(Exception e) {
           LOGGER.warn(e.getMessage(),e);
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getAllContacts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       KwlReturnObject kmsg = null;
       int dl = 0;
       try{
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            if(!heirarchyPerm){
                filter_names.add("INco.usersByUserid.userID");filter_params.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);

            kmsg = crmContactDAOObj.getAllContacts(requestParams);
            dl = kmsg.getRecordTotalCount();
            JSONObject tmpOb = crmManagerCommon.insertNone();
            jarr.put(tmpOb);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getContactid());
                tmpObj.put("name", (StringUtil.checkForNull(obj.getFirstname()) + " " + obj.getLastname()).trim());
                tmpObj.put("phone", obj.getPhoneno());
                tmpObj.put("email", obj.getEmail());
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
       } catch(Exception e) {
           LOGGER.warn(e.getMessage(),e);
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveCases(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject myjobj = new JSONObject();
       KwlReturnObject kmsg = null;
       CrmCase cases = null;
       //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try{
            boolean caseClosed = false;
            boolean assignedUserChanged = false;
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            Integer operationCode = CrmPublisherHandler.ADDRECORDCODE;
            String id = jobj.getString("caseid");
            String[] arrayId = new String[]{id};
            jobj.put("userid", userid);
            jobj.put("companyid",companyid);
            jobj.put("updatedon", new Date().getTime());
            JSONArray jcustomarray = null;
            if(jobj.has("customfield")){
                jcustomarray = jobj.getJSONArray("customfield");
            }
            if (id.equals("0")) {
                id = java.util.UUID.randomUUID().toString();
                jobj.put("caseid", id);
                jobj.put("casecreatedby", userid);
                kmsg = crmCaseDAOObj.addCases(jobj);
                cases = (CrmCase) kmsg.getEntityList().get(0);
                if (cases.getValidflag() == 1) {
                    auditTrailDAOObj.insertAuditLog(AuditAction.CASE_CREATE,
                                    ((cases.getSubject()==null)?"":cases.getSubject()) + " - Case created ",
                                    request, id);
                }
                HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                customrequestParams.put("customarray", jcustomarray);
                customrequestParams.put("modulename", Constants.Crm_Case_modulename);
                customrequestParams.put("moduleprimarykey", Constants.Crm_Caseid);
                customrequestParams.put("modulerecid", id);
                customrequestParams.put("companyid", companyid);
                customrequestParams.put("customdataclasspath", Constants.Crm_case_custom_data_classpath);
                if(jobj.has("customfield")){
                    KwlReturnObject customDataresult =fieldDataManagercntrl.setCustomData(customrequestParams);
                    if(customDataresult!=null && customDataresult.getEntityList().size() > 0){
                        jobj.put("CrmCaseCustomDataobj", id);
                        kmsg = crmCaseDAOObj.editCases(jobj);
                    }  
                }
             // fetch auto-number columns only
                HashMap<String, Object> fieldrequestParams = new HashMap<String, Object>();
                fieldrequestParams.put("isexport", true);
                fieldrequestParams.put("filter_names", Arrays.asList("companyid", "moduleid", "fieldtype"));
                fieldrequestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_case_moduleid, Constants.CUSTOM_FIELD_AUTONUMBER));
                KwlReturnObject AutoNoFieldMap = fieldManagerDAOobj.getFieldParams(fieldrequestParams);

                // increment auto number if exist
                if (AutoNoFieldMap.getEntityList().size() > 0) {
                    JSONArray autoNoData = fieldDataManagercntrl.setAutoNumberCustomData(customrequestParams, AutoNoFieldMap.getEntityList());
                    jobj.put(com.krawler.common.util.Constants.AUTOCUSTOMFIELD, autoNoData);
                }   // END logic - auto no
            } else {
                operationCode = CrmPublisherHandler.UPDATERECORDCODE;
                // using old and new record validflag, check record becames valid or not
                List<String> recordIds = new ArrayList<String>();
                recordIds.add(id);
                List<CrmCase> oldCaseRecord =  crmCaseDAOObj.getCases(recordIds);
                String caseAssignedId = null;
                if (oldCaseRecord != null)
                {
                    for (CrmCase caseObj: oldCaseRecord)
                    {
                        caseAssignedId = (caseObj.getAssignedto()!=null?caseObj.getAssignedto().getUserID():"");
                        if(jobj.has("caseassignedtoid")) {
                            if(!caseAssignedId.equals(jobj.getString("caseassignedtoid"))) {
                               assignedUserChanged = true;
                            }
                        }
                        if(caseObj.getCrmCombodataByCasestatusid() != null && caseObj.getCrmCombodataByCasestatusid().getMainID().equals(Constants.CASESTATUS_CLOSED)) {
                            caseClosed = true;
                        }
                    }
                }
                if(jobj.has("customfield")){

                    HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                    customrequestParams.put("customarray", jcustomarray);
                    customrequestParams.put("modulename", Constants.Crm_Case_modulename);
                    customrequestParams.put("moduleprimarykey", Constants.Crm_Caseid);
                    customrequestParams.put("modulerecid", id);
                    customrequestParams.put("companyid", companyid);
                    customrequestParams.put("customdataclasspath", Constants.Crm_case_custom_data_classpath);
                    KwlReturnObject customDataresult =fieldDataManagercntrl.setCustomData(customrequestParams);
                    if(customDataresult!=null && customDataresult.getEntityList().size() > 0){
                        jobj.put("CrmCaseCustomDataobj", id);
                    }
                }
                if(jobj.has("createdon")){
                    jobj.put("createdon", jobj.getLong("createdon"));
                }
                kmsg = crmCaseDAOObj.editCases(jobj);
                cases = (CrmCase) kmsg.getEntityList().get(0);
                if (cases.getValidflag() == 1) {
                    auditTrailDAOObj.insertAuditLog(AuditAction.CASE_UPDATE,
                                    jobj.getString("auditstr") + " Case - " + ((cases.getSubject()==null)?"":cases.getSubject()) + " ",
                                    request, id);

                    String loginURL = URLUtil.getRequestPageURL(request, Links.loginpageFull);
                    HashMap<String, Object> extraParams = new HashMap<String, Object>();
                    extraParams.put(NotificationConstants.LOGINURL, loginURL);
                    extraParams.put(NotificationConstants.PARTNERNAME, sessionHandlerImplObj.getPartnerName());
                    if(jobj.has("casestatusid") && !caseClosed && cases.getCrmCombodataByCasestatusid() != null &&  cases.getCrmCombodataByCasestatusid().getMainID().equals(Constants.CASESTATUS_CLOSED)) {
                        sendNotificationOnCaseClosed(cases, userid, companyid, extraParams, sessionHandlerImplObj.getSystemEmailId());
                    }
                    if (assignedUserChanged && cases.getAssignedto() != null) {
                        sendNotificationOnCaseAssigned(cases, userid, extraParams);
                    }
                }
            }
            if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
          	   if(arrayId[0].equals("0"))
          		   crmCommonService.validateMassupdate(new String[]{cases.getCaseid()}, "Case", companyid);
          	   else
             	   crmCommonService.validateMassupdate(arrayId, "Account", companyid);
            }
            myjobj.put("success", true);
            myjobj.put("ID", cases.getCaseid());
            myjobj.put("createdon",jobj.has("createdon") ? jobj.getLong("createdon"):"");
            myjobj.put(com.krawler.common.util.Constants.AUTOCUSTOMFIELD, jobj.has(com.krawler.common.util.Constants.AUTOCUSTOMFIELD) ? jobj.getJSONArray(com.krawler.common.util.Constants.AUTOCUSTOMFIELD) : "");
            txnManager.commit(status);

            JSONObject cometObj = jobj;
            if(!StringUtil.isNullObject(cases)) {
                if(!StringUtil.isNullObject(cases.getCreatedon())) {
                    cometObj.put("createdon", cases.getCreatedonGMT());
                }
            }
            publishCasesModuleInformation(request, cometObj, operationCode, companyid, userid);
       } catch (Exception e) {
           LOGGER.warn(e.getMessage(),e);
           txnManager.rollback(status);
       }
       return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public void sendNotificationOnCaseAssigned(CrmCase caseObj, String userid, HashMap<String, Object> extraPlaceHolders) {
        try{
           List<String> recepients = new ArrayList();
           recepients.add(caseObj.getAssignedto().getUserID());
           Map refTypeMap = new HashMap();
           Map refIdMap = new HashMap();
           refIdMap.put("refid1", caseObj.getCaseid());
           refTypeMap.put("reftype1", Constants.Crm_case_classpath);
           refIdMap.put("refid2", userid);
           refTypeMap.put("reftype2", Constants.USERS_CLASSPATH);
           if (recepients.size() > 0) {
               NotificationManagementServiceDAO.sendNotificationRequest(CHANNEL.EMAIL, CASE_ASSIGNED, NOTIFICATIONSTATUS.REQUEST, userid, recepients, refIdMap, refTypeMap, extraPlaceHolders);
           }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    public void sendNotificationOnCaseClosed(CrmCase caseObj, String userid, String companyid, HashMap<String, Object> extraPlaceHolders, String sysEmailId) {
        try{
            CrmContact contactObj = caseObj.getCrmContact();
            if(contactObj!=null && !StringUtil.isNullOrEmpty(contactObj.getEmail())) {
                String contactname = contactObj.getFullname();
                String contactemail = contactObj.getEmail();
                String subject = caseObj.getSubject();
                String accountname = caseObj.getCrmAccount()!=null?caseObj.getCrmAccount().getAccountname():"";
                User closedBy = caseObj.getAssignedto()!=null?caseObj.getAssignedto():caseObj.getUsersByUserid();
                String timezoneName = closedBy.getTimeZone().getSname()!=null?closedBy.getTimeZone().getSname():"";
                Object createdate = caseObj.getCreatedOn() == null? "": caseObj.getCreatedOn();
                Object closingdate = caseObj.getUpdatedOn() == null? "": caseObj.getUpdatedOn();
                String partnerName = extraPlaceHolders.containsKey(NotificationConstants.PARTNERNAME) ? extraPlaceHolders.get(NotificationConstants.PARTNERNAME).toString():"";
                
                String emailhtml = "Hi <b>"+contactname+"</b>,<br><br>";
                emailhtml += "Case - <b>"+subject+"</b>"+(!StringUtil.isNullOrEmpty(accountname)?" for the Account - <b>"+accountname+"</b>":"")+" opened on "+createdate+" "+timezoneName+" has been marked close on "+closingdate+" "+timezoneName+".<br><br>";
                emailhtml += "--"+partnerName+" Admin";

                String plaintext = "Hi <b>"+contactname+"</b>,\n\n";
                plaintext += "Case - <b>"+subject+"</b>"+(!StringUtil.isNullOrEmpty(accountname)?" for the Account - <b>"+accountname+"</b>":"")+" opened on "+createdate+" "+timezoneName+" has been marked close on "+closingdate+" "+timezoneName+".\n\n";
                plaintext += "--"+partnerName+" Admin";
                
                if(!StringUtil.isNullOrEmpty(contactemail)) {
                    SendMailHandler.postMail(new String[]{contactemail}, "["+partnerName+" CRM] Case - "+subject+" has been closed.", emailhtml, plaintext, sysEmailId,partnerName+" Admin");
                }
            }

           //Send mail to owner and assigned user.
           List<String> recepients = new ArrayList();
           if(companyid.equals(ConfigReader.getinstance().get("secutech_companyid"))) {//Customization for Secutech company.
               HashMap<String, Object> requestParams = new HashMap<String, Object>();
               requestParams.put("caseid", caseObj.getCaseid());
               requestParams.put("companyid", companyid);
               recepients = crmCaseDAOObj.getCaseCustomUserID(requestParams);
           }
           if(!recepients.contains(caseObj.getUsersByUserid().getUserID())) {
              recepients.add(caseObj.getUsersByUserid().getUserID());
           }
           if(caseObj.getAssignedto()!=null) {
               if(!recepients.contains(caseObj.getAssignedto().getUserID())) {
                   recepients.add(caseObj.getAssignedto().getUserID());
               }
           }
           Map refTypeMap = new HashMap();
           Map refIdMap = new HashMap();
           refIdMap.put("refid1", caseObj.getCaseid());
           refTypeMap.put("reftype1", Constants.Crm_case_classpath);
           refIdMap.put("refid2", userid);
           refTypeMap.put("reftype2", Constants.USERS_CLASSPATH);
           if (recepients.size() > 0) {
               NotificationManagementServiceDAO.sendNotificationRequest(CHANNEL.EMAIL, CASE_CLOSED, NOTIFICATIONSTATUS.REQUEST, userid, recepients, refIdMap, refTypeMap,extraPlaceHolders);
           }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
    
	public ModelAndView updateMassCases(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject myjobj = new JSONObject();
		KwlReturnObject kmsg = null;
		CrmCase cases = null;
		// Create transaction
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		try {
			JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
			String companyid = sessionHandlerImpl.getCompanyid(request);
			String userid = sessionHandlerImpl.getUserid(request);
			Integer operationCode = CrmPublisherHandler.UPDATERECORDCODE;
			String caseIds = jobj.getString("caseid");
			String arrayId[] = caseIds.split(",");
			jobj.put("userid", userid);
			jobj.put("companyid", companyid);
			if (jobj.has("updatedon") && !StringUtil.isNullOrEmpty(jobj.getString("updatedon"))) {
                jobj.put("updatedon", jobj.getLong("updatedon"));
            } else {
                jobj.put("updatedon", new Date().getTime());
            }
			jobj.put("caseid", arrayId);
			jobj.put("tzdiff", sessionHandlerImpl.getTimeZoneDifference(request));
			JSONArray jcustomarray = null;
			if (jobj.has("customfield")) {
				jcustomarray = jobj.getJSONArray("customfield");
			}

			if (jobj.has("customfield")) {
				HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
				customrequestParams.put("customarray", jcustomarray);
				customrequestParams.put("modulename", Constants.Crm_Case_modulename);
				customrequestParams.put("moduleprimarykey", Constants.Crm_Caseid);
				customrequestParams.put("companyid", companyid);
				customrequestParams.put("customdataclasspath", Constants.Crm_case_custom_data_classpath);
				KwlReturnObject customDataresult = null;
				for (String id : arrayId) {
					customrequestParams.put("modulerecid", id);
					customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
				}
				if (customDataresult != null && customDataresult.getEntityList().size() > 0) {
					jobj.put("CrmCaseCustomDataobj", true);

				}
			}
			if (jobj.has("createdon")) {
				jobj.put("createdon", jobj.getLong("createdon"));
			}
			kmsg = crmCaseDAOObj.updateMassCases(jobj);
			// TODO : How to insert audit log when mass update
			// cases = (CrmCase) kmsg.getEntityList().get(0);
			// if (cases.getValidflag() == 1) {
			// auditTrailDAOObj.insertAuditLog(AuditAction.CASE_UPDATE,
			// jobj.getString("auditstr") + " Case - " + ((acc.getCasename() ==
			// null) ? "" : acc.getCasename()) + " ",
			// request, id);
			// }

			// }
			
			JSONObject cometObj = jobj;
			if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
				crmCommonService.validateMassupdate(arrayId, "Case", companyid);
				cometObj.put("caseid", caseIds);
				cometObj.put("ismassedit", true);
			}

			myjobj.put("success", true);
			myjobj.put("ID", caseIds);
			myjobj.put("createdon", jobj.has("createdon") ? jobj.getLong("createdon") : "");

			if (!StringUtil.isNullObject(cases)) {
				if (cases.getCreatedOn()!=null) {
					cometObj.put("createdon", cases.getCreatedonGMT());
				}
			}
			publishCasesModuleInformation(request, cometObj, operationCode, companyid, userid);
			txnManager.commit(status);
		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
			txnManager.rollback(status);
		}
		return new ModelAndView("jsonView", "model", myjobj.toString());
	}

    private void publishCasesModuleInformation(HttpServletRequest request, JSONObject cometObj, int operationCode, String companyid, String userid) throws ServiceException, JSONException, SessionExpiredException  {
        String randomNumber = request.getParameter("randomnumber");
        CometManagementService.publishModuleInformation(companyid, userid, randomNumber,Constants.Crm_Case_modulename, Constants.Crm_caseid, operationCode, cometObj, CometConstants.CRMUPDATES);
    }

    public ModelAndView deleteCase(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        KwlReturnObject kmsg = null;
        CrmCase cases = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        JSONArray jarr = null;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            ArrayList caseids = new ArrayList();
            myjobj = new JSONObject();
            myjobj.put("success", false);
            if (StringUtil.bNull(request.getParameter("jsondata"))) {
                String jsondata = request.getParameter("jsondata");
                jarr = new JSONArray("[" + jsondata + "]");
                
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobject = jarr.getJSONObject(i);
                    caseids.add(jobject.getString("caseid").toString());
                }
                String[] arrayid = (String[]) caseids.toArray(new String[]{});
                JSONObject jobj = new JSONObject();
                jobj.put("deleteflag", 1);
                jobj.put("caseid", arrayid);
                jobj.put("userid", userid);
                jobj.put("updatedon", new Date().getTime());
                kmsg = crmCaseDAOObj.updateMassCases(jobj);

                List<CrmCase> ll = crmCaseDAOObj.getCases(caseids);

                if(ll!=null){
                    for(int i =0 ; i< ll.size() ; i++){
                        CrmCase caseaudit = (CrmCase)ll.get(i);
                        if (caseaudit.getValidflag() == 1) {
                            auditTrailDAOObj.insertAuditLog(AuditAction.CASE_DELETE,
                                  caseaudit.getSubject() + " - Case deleted ",
                                    request, caseaudit.getCaseid());
                        }
                    }

                }
            }
            myjobj.put("success", true);
            myjobj.put("ID", caseids.toArray());
            txnManager.commit(status);

            if(jarr!=null) {
                JSONObject cometObj = new JSONObject();
                cometObj.put("ids",  jarr);
                publishCasesModuleInformation(request, cometObj, CrmPublisherHandler.DELETERECORDCODE, sessionHandlerImpl.getCompanyid(request),userid);
            }

        } catch (JSONException e) {
            LOGGER.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView getCases(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       try{
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            String accountFlag = request.getParameter("flag");
            String isarchive = request.getParameter("isarchive");
            String mapid = request.getParameter("mapid");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            String iscustomcolumn = request.getParameter("iscustomcolumn");
            String xtype = request.getParameter("xtype");
            String xfield = request.getParameter("xfield");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);

            jobj = getCaseManagementService().getCases(companyid, userid, currencyid, selectExport, accountFlag, isarchive, mapid,
                    searchJson, ss, config, isExport, heirarchyPerm, field, direction, iscustomcolumn, xtype, xfield, start, limit, dateFormat, usersList);

       } catch(Exception e) {
           LOGGER.warn(e.getMessage(),e);
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView caseExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       KwlReturnObject kmsg = null;
       String view = "jsonView";
       try{
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            String accountFlag = request.getParameter("flag");
            String isarchive = request.getParameter("isarchive");
            String mapid = request.getParameter("mapid");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);

            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            
            jobj = getCaseManagementService().caseExport(companyid, userid, currencyid, selectExport, accountFlag, isarchive, mapid,
                    searchJson, ss, config, isExport, heirarchyPerm, field, direction, dateFormat, usersList);
            
            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.CASE_EXPORT,
                    "Case data exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
       } catch(Exception e) {
           LOGGER.warn(e.getMessage(),e);
       }
       return new ModelAndView(view, "model", jobj.toString());
    }


}
