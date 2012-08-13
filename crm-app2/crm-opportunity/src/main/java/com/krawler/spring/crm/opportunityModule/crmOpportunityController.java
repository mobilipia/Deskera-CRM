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
import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.opportunity.bizservice.OpportunityManagementService;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.common.CrmCommonService;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.JSONException;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import com.krawler.common.notification.bizservice.NotificationManagementService;
import com.krawler.common.notification.web.NotificationConstants;
import com.krawler.common.notification.web.NotificationConstants.CHANNEL;
import com.krawler.common.notification.web.NotificationConstants.NOTIFICATIONSTATUS;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.opportunity.dm.OpportunityOwnerInfo;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.esp.web.resource.Links;
import java.util.Arrays;
import java.util.Map;
import static com.krawler.common.notification.web.NotificationConstants.*;

public class crmOpportunityController extends MultiActionController {
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private CrmCommonService crmCommonService;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private commentDAO crmCommentDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private fieldDataManager fieldDataManagercntrl;
    private OpportunityManagementService opportunityManagementService;
    private CometManagementService CometManagementService;
    private NotificationManagementService NotificationManagementServiceDAO;
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

    public void setOpportunityManagementService(OpportunityManagementService opportunityManagementService) {
        this.opportunityManagementService = opportunityManagementService;
    }

    public OpportunityManagementService getOpportunityManagementService() {
        return this.opportunityManagementService;
    }

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }
    public crmOpportunityDAO getcrmOpportunityDAO(){
        return crmOpportunityDAOObj;
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

    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImpl1) {
        this.sessionHandlerImplObj = sessionHandlerImpl1;
    }

    public ModelAndView getAllAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        int dl = 0;
        KwlReturnObject kmsg = null;
        try{
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

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            if(!heirarchyPerm) {
                filter_names.add("INao.usersByUserid.userID");
                filter_params.add(usersList);
            }

            kmsg = crmAccountDAOObj.getAllAccounts(requestParams, filter_names, filter_params);

            dl = kmsg.getEntityList().size();
            Iterator ite = kmsg.getEntityList().iterator();
            JSONObject tmpOb = crmManagerCommon.insertNone();
            jarr.put(tmpOb);
            while (ite.hasNext()) {
                CrmAccount obj = (CrmAccount) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getAccountid());
                tmpObj.put("name", obj.getAccountname());
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
       } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveOpportunities(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        CrmOpportunity opp = null;
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try{
            boolean oppCreated = false, ownerChanged = false;
            String userid = sessionHandlerImpl.getUserid(request);
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            Integer operationCode = CrmPublisherHandler.ADDRECORDCODE;
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String id = jobj.getString("oppid");
            String[] arrayId = new String[]{id};
            jobj.put("userid", userid);
            jobj.put("companyid", companyid);
            jobj.put("updatedon", new Date().getTime());
            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
            JSONArray jcustomarray = null;
            if(jobj.has("customfield")){
                jcustomarray = jobj.getJSONArray("customfield");
            }
            if (id.equals("0")) {
                if(jobj.has("oppownerid") && !jobj.getString("oppownerid").equals(userid)) {
                   ownerChanged = true;
                }
                id = java.util.UUID.randomUUID().toString();
//                jobj.put("createdon", new Date());
                jobj.put("oppid", id);

//                jobj = StringUtil.setcreatedonDate(jobj,request);
                kmsg = crmOpportunityDAOObj.addOpportunities(jobj);
                opp = (CrmOpportunity) kmsg.getEntityList().get(0);
                if (opp.getValidflag() == 1) {
                    oppCreated = true;
                    auditTrailDAOObj.insertAuditLog(AuditAction.OPPORTUNITY_CREATE,
                            ((opp.getOppname()==null)?"":opp.getOppname()) + " - Opportunity created ",
                            request, id);
                }
                HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                customrequestParams.put("customarray", jcustomarray);
                customrequestParams.put("modulename", Constants.Crm_Opportunity_modulename);
                customrequestParams.put("moduleprimarykey", Constants.Crm_Opportunityid);
                customrequestParams.put("modulerecid", id);
                customrequestParams.put("companyid", companyid);
                customrequestParams.put("customdataclasspath", Constants.Crm_opportunity_custom_data_classpath);
                if(jobj.has("customfield")){
                    KwlReturnObject customDataresult =fieldDataManagercntrl.setCustomData(customrequestParams);
                    if(customDataresult!=null && customDataresult.getEntityList().size() > 0){
                        jobj.put("CrmOpportunityCustomDataobj", id);
                        kmsg = crmOpportunityDAOObj.editOpportunities(jobj);
                    }
                    
                   
//                    fieldManager.storeCustomFields(jcustomarray,"opportunity",true,id);
                }
                
             // fetch auto-number columns only
                HashMap<String, Object> fieldrequestParams = new HashMap<String, Object>();
                fieldrequestParams.put("isexport", true);
                fieldrequestParams.put("filter_names", Arrays.asList("companyid", "moduleid", "fieldtype"));
                fieldrequestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_opportunity_moduleid, Constants.CUSTOM_FIELD_AUTONUMBER));
                KwlReturnObject AutoNoFieldMap = fieldManagerDAOobj.getFieldParams(fieldrequestParams);
                
                // increment auto number if exist
                if(AutoNoFieldMap.getEntityList().size()>0) {
                     JSONArray autoNoData = fieldDataManagercntrl.setAutoNumberCustomData(customrequestParams, AutoNoFieldMap.getEntityList());
                     jobj.put(com.krawler.common.util.Constants.AUTOCUSTOMFIELD, autoNoData);
                }
                // END logic - auto no
            } else {
                // using old and new record validflag, check record becames valid or not
                List<String> recordIds = new ArrayList<String>();
                recordIds.add(id);
                List<CrmOpportunity> oldOppRecord =  crmOpportunityDAOObj.getOpportunities(recordIds);
                if (oldOppRecord != null)
                {
                    for (CrmOpportunity opportunity: oldOppRecord)
                    {
                        if(opportunity.getValidflag()==0 && jobj.getString("validflag").equals("1")) {
                            oppCreated = true;
                        }
                    }
                }
               // check if opportunity owner changed
               String ownerId = crmOpportunityHandler.getMainOppOwner(crmOpportunityDAOObj,id);
               if(!StringUtil.isNullOrEmpty(ownerId)) {
                   if(!ownerId.equals(jobj.optString("oppownerid",ownerId))) {
                           ownerChanged = true;
                   }
               }
               operationCode = CrmPublisherHandler.UPDATERECORDCODE;
                if(jobj.has("customfield")){
                    HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                    customrequestParams.put("customarray", jcustomarray);
                    customrequestParams.put("modulename", Constants.Crm_Opportunity_modulename);
                    customrequestParams.put("moduleprimarykey", Constants.Crm_Opportunityid);
                    customrequestParams.put("modulerecid", id);
                    customrequestParams.put("companyid", companyid);
                    customrequestParams.put("customdataclasspath", Constants.Crm_opportunity_custom_data_classpath);
                    KwlReturnObject customDataresult =fieldDataManagercntrl.setCustomData(customrequestParams);
                    if(customDataresult!=null && customDataresult.getEntityList().size() > 0){
                        jobj.put("CrmOpportunityCustomDataobj", id);
                    }
//                    fieldManager.storeCustomFields(jcustomarray,"opportunity",false,id);
                }
                if(jobj.has("createdon")){
                    jobj.put("createdon", jobj.getLong("createdon"));
                }
                kmsg = crmOpportunityDAOObj.editOpportunities(jobj);
                opp = (CrmOpportunity) kmsg.getEntityList().get(0);
                if (opp.getValidflag() == 1) {
                    auditTrailDAOObj.insertAuditLog(AuditAction.OPPORTUNITY_UPDATE,
                            jobj.getString("auditstr") + " Opportunity - " + ((opp.getOppname()==null)?"":opp.getOppname()) + " ",
                            request, id);
                }
            }
            id = opp.getOppid();
            if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
         	   if(arrayId[0].equals("0"))
         		   crmCommonService.validateMassupdate(new String[]{opp.getOppid()}, "Opportunity", companyid);
         	   else
            	   crmCommonService.validateMassupdate(arrayId, "Account", companyid);
            }

            myjobj.put("success", true);
            myjobj.put("ID", opp.getOppid());
            myjobj.put("createdon",jobj.has("createdon") ? jobj.getLong("createdon"):"");
            myjobj.put(Constants.AUTOCUSTOMFIELD, jobj.has(Constants.AUTOCUSTOMFIELD) ? jobj.getJSONArray(Constants.AUTOCUSTOMFIELD) : "");
            txnManager.commit(status);

            JSONObject cometObj = jobj;
            if(!StringUtil.isNullObject(opp)) {
                if(opp.getCreatedOn()!=null) {
                    cometObj.put("createdon", opp.getCreatedOn());
                }
                if(opp.getClosingdate()!=null) {
                    cometObj.put("closingdate", opp.getClosingdate());
                }
            }

            publishOpportunityModuleInformation(request, cometObj, operationCode, companyid, userid);
            if (sessionHandlerImpl.getCompanyNotifyOnFlag(request)) {// send Notification if set flag at company level
               if (ownerChanged||oppCreated){   // Send Notification if owner changed and if new opportunity has been assigned.
                   List<String> recepients = new ArrayList();
                   recepients.add(jobj.getString("oppownerid"));
                   Map refTypeMap = new HashMap();
                   Map refIdMap = new HashMap();
                   refIdMap.put("refid1", id);
                   refTypeMap.put("reftype1", Constants.Crm_opportunity_classpath);
                   refIdMap.put("refid2", userid);
                   refTypeMap.put("reftype2", Constants.USERS_CLASSPATH);
                   int notification_type= oppCreated? OPPORTUNITY_CREATION:OPPORTUNITY_ASSIGNED;
                   String loginURL = URLUtil.getRequestPageURL(request, Links.loginpageFull);
                   HashMap<String, Object> extraParams = new HashMap<String, Object>();
                   extraParams.put(NotificationConstants.LOGINURL, loginURL);
                   extraParams.put(NotificationConstants.PARTNERNAME, sessionHandlerImplObj.getPartnerName(request));
                   if (recepients.size() > 0) {
                       NotificationManagementServiceDAO.sendNotificationRequest(CHANNEL.EMAIL, notification_type, NOTIFICATIONSTATUS.REQUEST, userid, recepients, refIdMap, refTypeMap, extraParams);
                   }
               }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    
	public ModelAndView updateMassOpportunities(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject myjobj = new JSONObject();
		CrmOpportunity opp = null;
		KwlReturnObject kmsg = null;
		// Create transaction
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		try {
			boolean oppCreated = false, ownerChanged = false;
			String userid = sessionHandlerImpl.getUserid(request);
			JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
			Integer operationCode = CrmPublisherHandler.UPDATERECORDCODE;
			String companyid = sessionHandlerImplObj.getCompanyid(request);
			String oppIds = jobj.getString("oppid");
			String arrayId[] = oppIds.split(",");
			List<CrmOpportunity> ownerChangedOpps=null;
            String mainownerId="";
			jobj.put("userid", userid);
			jobj.put("companyid", companyid);
            if (jobj.has("updatedon")) {
                jobj.put("updatedon", jobj.getLong("updatedon"));
            } else {
                jobj.put("updatedon", new Date().getTime());
            }
			jobj.put("oppid", arrayId);
			jobj.put("tzdiff", sessionHandlerImpl.getTimeZoneDifference(request));
			JSONArray jcustomarray = null;
			if (jobj.has("customfield")) {
				jcustomarray = jobj.getJSONArray("customfield");
			}
			
            if(jobj.optString("oppownerid",null)!=null){
            	String newOwnerId=jobj.getString("oppownerid");
            	ownerChangedOpps=crmOpportunityHandler.getOwnerChangedOpportunities(crmOpportunityDAOObj, arrayId, newOwnerId);
            }

			if (jobj.has("customfield")) {
				HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
				customrequestParams.put("customarray", jcustomarray);
				customrequestParams.put("modulename", Constants.Crm_Opportunity_modulename);
				customrequestParams.put("moduleprimarykey", Constants.Crm_Opportunityid);
				customrequestParams.put("companyid", companyid);
				customrequestParams.put("customdataclasspath", Constants.Crm_opportunity_custom_data_classpath);
			    KwlReturnObject customDataresult=null;
	            for(String id : arrayId){
	               customrequestParams.put("modulerecid", id);
	               customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
	            }
				if (customDataresult != null && customDataresult.getEntityList().size() > 0) {
					jobj.put("CrmOpportunityCustomDataobj", true);
				}
					// fieldManager.storeCustomFields(jcustomarray,"opportunity",false,id);
			}
			if (jobj.has("createdon")) {
				jobj.put("createdon", jobj.getLong("createdon"));
			}
			kmsg = crmOpportunityDAOObj.updateMassOpportunities(jobj);
				
//				 TODO : How to insert audit log when mass update
//				opp = (CrmOpportunity) kmsg.getEntityList().get(0);
//				if (opp.getValidflag() == 1) {
//					auditTrailDAOObj.insertAuditLog(AuditAction.OPPORTUNITY_UPDATE, jobj.getString("auditstr") + " Opportunity - " + ((opp.getOppname() == null) ? "" : opp.getOppname()) + " ",
//							request, id);
//				}
			
			txnManager.commit(status);
	        JSONObject cometObj = jobj;
	        if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
	            crmCommonService.validateMassupdate(arrayId, "Opportunity", companyid);
	            cometObj.put("oppid", oppIds);
	            cometObj.put("ismassedit", true);
	        }

            myjobj.put("success", true);
            myjobj.put("ID", oppIds);
            myjobj.put("createdon", jobj.has("createdon") ? jobj.getLong("createdon") : "");
            if (!StringUtil.isNullObject(opp)) {
                if (opp.getCreatedon()!=null) {
                    cometObj.put("createdon", opp.getCreatedonGMT());
                }
            }

			publishOpportunityModuleInformation(request, cometObj, operationCode, companyid, userid);
				
            if (ownerChangedOpps!=null && ownerChangedOpps.size()>0) {// Send Notification if owner changed
                List<String> recepients = new ArrayList();
                recepients.add(jobj.getString("oppownerid"));
                String loginURL = URLUtil.getRequestPageURL(request, Links.loginpageFull);
            	HashMap<String, Object> extraParams = new HashMap<String, Object>();
            	extraParams.put(NotificationConstants.LOGINURL, loginURL);
            	extraParams.put(NotificationConstants.PARTNERNAME, sessionHandlerImpl.getPartnerName(request));   
            	StringBuffer variableData=new StringBuffer();
            	DateFormat df=new SimpleDateFormat("MMMM d, yyyy");
            	String opname="";
            	String opacname="";
            	String opstagename="";
            	int srno=0;
               	Map refTypeMap = new HashMap();
        		Map refIdMap = new HashMap();
            	refIdMap.put("refid1",ownerChangedOpps.get(0).getOppid());//any id is taken from updated records just for getting invoker class object 
    			refTypeMap.put("reftype1", Constants.Crm_opportunity_classpath);
    			refIdMap.put("refid2", userid);
    			refTypeMap.put("reftype2", Constants.USERS_CLASSPATH);
    			
    			if(ownerChangedOpps.size()>1){
    				for(CrmOpportunity oppObj:ownerChangedOpps){
    					srno++;
    					opname=(!StringUtil.isNullOrEmpty(oppObj.getOppname()))?oppObj.getOppname():"";
    					opacname=(!StringUtil.isNullObject(oppObj.getCrmAccount()))?oppObj.getCrmAccount().getAccountname():"";
    					opstagename=(!StringUtil.isNullObject(oppObj.getCrmCombodataByOppstageid()))?oppObj.getCrmCombodataByOppstageid().getValue():"";
    					variableData.append("<tr>");
    					variableData.append("<td>"+ srno +"</td>");
    					variableData.append("<td>"+ opname +"</td>");
    					variableData.append("<td>"+ opstagename +"</td>");
    					variableData.append("<td>"+ df.format(new Date(oppObj.getClosingdate())) + "</td>");
    					variableData.append("<td>"+ opacname +"</td>");
    					variableData.append("</tr>");
    				}
    				extraParams.put(Constants.VARIABLEDATA, variableData);
    				NotificationManagementServiceDAO.sendNotificationRequest(CHANNEL.EMAIL, OPPORTUNITY_ASSIGNED_MASS_UPDATE, NOTIFICATIONSTATUS.REQUEST, userid, recepients, refIdMap, refTypeMap, extraParams);
    				
              }else{
            	  NotificationManagementServiceDAO.sendNotificationRequest(CHANNEL.EMAIL, OPPORTUNITY_ASSIGNED, NOTIFICATIONSTATUS.REQUEST, userid, recepients, refIdMap, refTypeMap, extraParams);
              }
			}
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			txnManager.rollback(status);
		}
		return new ModelAndView("jsonView", "model", myjobj.toString());
	}
    
    
    private void publishOpportunityModuleInformation(HttpServletRequest request, JSONObject cometObj, int operationCode, String companyid, String userid) throws ServiceException, JSONException, SessionExpiredException, ServiceException, com.krawler.utils.json.base.JSONException {
        String randomNumber = request.getParameter("randomnumber");
        CometManagementService.publishModuleInformation(companyid, userid, randomNumber, Constants.Crm_Opportunity_modulename, Constants.Crm_opportunityid, operationCode, cometObj, CometConstants.CRMUPDATES);
    }

    public ModelAndView getOpportunities(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
       try{
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            String iscustomcolumn = request.getParameter("iscustomcolumn");
            String xtype = request.getParameter("xtype");
            String xfield = request.getParameter("xfield");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");

            jobj = getOpportunityManagementService().getOpportunities(companyid, userid, currencyid, selectExport, accountFlag, isarchive, mapid, searchJson,
                    ss, config, isExport, heirarchyPerm, field, direction, iscustomcolumn, xtype, xfield, start, limit, dateFormat, usersList);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView opportunityExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        String view = "jsonView";
        try {
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);

            jobj = getOpportunityManagementService().opportunityExport(companyid, userid, currencyid, selectExport, accountFlag,
                    isarchive, mapid, searchJson, ss, config, isExport, heirarchyPerm, field, direction, dateFormat, usersList);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.OPPORTUNITY_EXPORT,
                    "Opportunity data exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

     public ModelAndView deleteOpportunity(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        CrmOpportunity opp = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        JSONArray jarr = null;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            ArrayList oppids = new ArrayList();
            myjobj = new JSONObject();
            myjobj.put("success", false);
            if (StringUtil.bNull(request.getParameter("jsondata"))) {
                String jsondata = request.getParameter("jsondata");
                jarr = new JSONArray("[" + jsondata + "]");
                
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobject = jarr.getJSONObject(i);
                    oppids.add(jobject.getString("oppid").toString());
                }

                String[] arrayid = (String[]) oppids.toArray(new String[]{});
                JSONObject jobj = new JSONObject();

                jobj.put("deleteflag", 1);
                jobj.put("oppid", arrayid);
                jobj.put("userid", userid);
                jobj.put("updatedon", new Date().getTime());
                jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
                KwlReturnObject kmsg = crmOpportunityDAOObj.updateMassOpportunities(jobj);

                List<CrmOpportunity> ll = crmOpportunityDAOObj.getOpportunities(oppids);

                if(ll!=null){
                    for(int i =0 ; i< ll.size() ; i++){
                        CrmOpportunity oppaudit = (CrmOpportunity)ll.get(i);
                        if (oppaudit.getValidflag() == 1) {
                            auditTrailDAOObj.insertAuditLog(AuditAction.OPPORTUNITY_DELETE,
                                  oppaudit.getOppname() + " - Opportunity deleted ",
                                    request, oppaudit.getOppid());
                        }
                    }
                }
            }
            myjobj.put("success", true);
            myjobj.put("ID", oppids.toArray());
            txnManager.commit(status);

            if(jarr!=null) {
                JSONObject cometObj = new JSONObject();
                cometObj.put("ids",  jarr);
                publishOpportunityModuleInformation(request, cometObj, CrmPublisherHandler.DELETERECORDCODE, sessionHandlerImpl.getCompanyid(request), userid);
            }

        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView saveOppOwners(HttpServletRequest request, HttpServletResponse response)
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
            String oppid = request.getParameter("leadid");
            String owners = request.getParameter("owners");
            String mainowner = request.getParameter("mainOwner");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("oppid", oppid);
            requestParams.put("owners", owners);
            requestParams.put("mainOwner", mainowner);
            kmsg = crmOpportunityDAOObj.saveOppOwners(requestParams);
            // Fetch subowners name list
            List<String> idsList = new ArrayList<String>();
            idsList.add(oppid);
            Map<String, List<OpportunityOwnerInfo>> ownersMap = crmOpportunityDAOObj.getOpportunityOwners(idsList);
            String[] ownerInfo = crmOpportunityHandler.getAllOppOwners(ownersMap.get(oppid));
            myjobj.put("subowners", ownerInfo[1]);

            myjobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
           txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView getExistingOppOwners(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
       try{
            String[] ownerInfo = crmOpportunityHandler.getAllOppOwners(crmOpportunityDAOObj, request.getParameter("leadid"));
            jobj.put("mainOwner",ownerInfo[2] );
            jobj.put("ownerids", ownerInfo[3]);
       } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
}
