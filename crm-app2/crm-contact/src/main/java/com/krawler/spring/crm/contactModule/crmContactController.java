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
package com.krawler.spring.crm.contactModule; 
import com.krawler.common.admin.Company;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.User;
import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.text.DateFormat;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.common.CrmCommonService;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountHandler;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import com.krawler.crm.utils.Constants;
import com.krawler.common.util.SystemUtil;
import com.krawler.crm.contact.bizservice.ContactManagementService;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.common.util.Header;
import com.krawler.crm.account.dm.AccountOwnerInfo;
import com.krawler.crm.contact.dm.ContactOwnerInfo;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.web.resource.Links;
import com.krawler.spring.crm.common.crmCommonDAO;
import java.util.Collections;
import java.util.Map;

public class crmContactController extends MultiActionController implements MessageSourceAware {

    private crmContactDAO crmContactDAOObj;
    private CrmCommonService crmCommonService;
    private crmAccountDAO crmAccountDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private commentDAO crmCommentDAOObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private fieldDataManager fieldDataManagercntrl;
    private ContactManagementService contactManagementService;
    private CometManagementService CometManagementService;
    private crmCommonDAO crmCommonDAOObj;
    private MessageSource mSource;
    private fieldManagerDAO fieldManagerDAOobj;

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    public void setCometManagementService(CometManagementService CometManagementService) {
        this.CometManagementService = CometManagementService;
    }

    public void setContactManagementService(ContactManagementService contactManagementService) {
        this.contactManagementService = contactManagementService;
    }

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }
    public crmContactDAO getcrmContactDAO(){
        return crmContactDAOObj;
    }

     public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }
    /**
     * @param crmCommonService the crmCommonService to set
     */
    public void setCrmCommonService(CrmCommonService crmCommonService)
    {
        this.crmCommonService = crmCommonService;
    }
    
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
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

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
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
        KwlReturnObject kmsg = null;
        int dl = 0;
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
            String hierarchy= request.getParameter("hierarchy");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            if(!StringUtil.isNullOrEmpty(hierarchy)  && Boolean.parseBoolean(hierarchy)){
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
            Iterator ite = kmsg.getEntityList().iterator();
            JSONObject tmpOb = crmManagerCommon.insertNone();
            jarr.put(tmpOb);
            boolean hasAccess ;
            List<String> idsList = new ArrayList<String>();
            while(ite.hasNext()){
                CrmAccount obj = (CrmAccount) ite.next();
                idsList.add(obj.getAccountid());
            }
            Map<String, List<AccountOwnerInfo>> owners = crmAccountDAOObj.getAccountOwners(idsList);
            
            ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmAccount obj = (CrmAccount) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getAccountid());
                tmpObj.put("name", obj.getAccountname());
                tmpObj.put(ContactConstants.Crm_isarchive, obj.getIsarchive());
                if(!obj.getIsarchive() && StringUtil.isNullOrEmpty(hierarchy) ){
                    if(!heirarchyPerm) {
                        hasAccess= crmAccountHandler.hasAccountAccess(owners.get(obj.getAccountid()),usersList);
                    }else{
                        hasAccess = true;
                    }
                    tmpObj.put(ContactConstants.Crm_hasAccess,hasAccess );
                }else{
                    tmpObj.put(ContactConstants.Crm_hasAccess,!obj.getIsarchive());
                }
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
       } catch(Exception e) {
    	   logger.warn("Exception in crmContactController.getAllAccounts:", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveContacts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        String contactId = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try{
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            JSONObject contactJSON = new JSONObject();
            Integer operationCode = CrmPublisherHandler.ADDRECORDCODE;
            String userid= sessionHandlerImpl.getUserid(request);
            String companyid= sessionHandlerImpl.getCompanyid(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            // extract ip address
            String ipAddress = SystemUtil.getIpAddress(request);
            String contactIds =jobj.getString("contactid");
            String arrayId[] = contactIds.split(",");
            for (int i = 0; i < arrayId.length; i++) {
               String id = arrayId[i];
               jobj.put("contactid", id);
               JSONArray jcustomarray = null;
               if (jobj.has("customfield")) {
                   jcustomarray = jobj.getJSONArray("customfield");
               }
               if (id.equals("0")) {
                   
                   contactJSON = contactManagementService.saveContact(companyid, userid, timeZoneDiff, ipAddress, jobj);
                   contactId = contactJSON.getString("ID");
                   jobj.put("contactid", contactId);
                   HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                   customrequestParams.put("customarray", jcustomarray);
                   customrequestParams.put("modulename", Constants.Crm_Contact_modulename);
                   customrequestParams.put("moduleprimarykey", Constants.Crm_Contactid);
                   customrequestParams.put("modulerecid", contactId);
                   customrequestParams.put("companyid", companyid);
                   customrequestParams.put("customdataclasspath", Constants.Crm_contact_custom_data_classpath);
                   if (jobj.has("customfield")) {
                       KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                       if (customDataresult!=null && customDataresult.getEntityList().size() > 0) {
                           jobj.put("CrmContactCustomDataobj", contactId);
                           JSONObject contactJObj = new JSONObject();
                           contactJObj.put("contactid", contactId);
                           contactJObj.put("CrmContactCustomDataobj", contactId);
                           contactJObj.put("tzdiff", timeZoneDiff);
                           contactManagementService.saveContact(companyid, userid, timeZoneDiff, ipAddress, contactJObj);
                       }
                   }
                // fetch auto-number columns only
                   HashMap<String, Object> fieldrequestParams = new HashMap<String, Object>();
                   fieldrequestParams.put("isexport", true);
                   fieldrequestParams.put("filter_names", Arrays.asList("companyid", "moduleid", "fieldtype"));
                   fieldrequestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_contact_moduleid, Constants.CUSTOM_FIELD_AUTONUMBER));
                   KwlReturnObject AutoNoFieldMap = fieldManagerDAOobj.getFieldParams(fieldrequestParams);

                   // increment auto number if exist
                   if (AutoNoFieldMap.getEntityList().size() > 0) {
                       JSONArray autoNoData = fieldDataManagercntrl.setAutoNumberCustomData(customrequestParams, AutoNoFieldMap.getEntityList());
                       jobj.put(com.krawler.common.util.Constants.AUTOCUSTOMFIELD, autoNoData);
              }   // END logic - auto no
               } else {
                  operationCode = CrmPublisherHandler.UPDATERECORDCODE;
                   if (jobj.has("customfield")) {
                       HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                       customrequestParams.put("customarray", jcustomarray);
                       customrequestParams.put("modulename", Constants.Crm_Contact_modulename);
                       customrequestParams.put("moduleprimarykey", Constants.Crm_Contactid);
                       customrequestParams.put("modulerecid", id);
                       customrequestParams.put("companyid", companyid);
                       customrequestParams.put("customdataclasspath", Constants.Crm_contact_custom_data_classpath);
                       KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                       if (customDataresult!=null && customDataresult.getEntityList().size() > 0) {
                           jobj.put("CrmContactCustomDataobj", id);
                       }
                   }
                    if(jobj.has("createdon")){
                       jobj.put("createdon", jobj.getLong("createdon"));
                   }
                   contactJSON = contactManagementService.saveContact(companyid, userid, timeZoneDiff, ipAddress, jobj);
                   contactId = contactJSON.getString("ID");
               }
           }
           txnManager.commit(status);
           JSONObject cometObj = jobj;
           if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
        	   if(arrayId[0].equals("0"))
        		   crmCommonService.validateMassupdate(new String[]{contactId}, "Contact", companyid);
        	   else
        		   crmCommonService.validateMassupdate(arrayId, "Contact", companyid);
               cometObj.put("contactid",  contactIds);
               cometObj.put("ismassedit", true);
           }
           myjobj.put("success", true);
           myjobj.put("ID", contactId);
           myjobj.put("createdon", jobj.has("createdon") ? jobj.getLong("createdon") : "");
           myjobj.put("updatedon", jobj.has("updatedon") ? jobj.getLong("updatedon") : "");
           myjobj.put(com.krawler.common.util.Constants.AUTOCUSTOMFIELD, jobj.has(com.krawler.common.util.Constants.AUTOCUSTOMFIELD) ? jobj.getJSONArray(com.krawler.common.util.Constants.AUTOCUSTOMFIELD) : "");

           if(jobj.has("accountid"))
                cometObj.put("relatedname", jobj.getString("accountid"));   // added for commet... JS side, store's dataIndex kye must contains in the json object also
           
           if(contactJSON.has("createdon")) {
                cometObj.put("createdon", contactJSON.has("createdon")?contactJSON.getLong("createdon"):"");
           }
           if(contactJSON.has("updatedon")) {
                cometObj.put("updatedon", contactJSON.has("updatedon")?contactJSON.getLong("updatedon"):"");
           }

           publishContactModuleInformation(request, cometObj, operationCode, companyid, userid);

        } catch (Exception e) {
        	logger.warn("Exception in crmContactController.saveContacts:", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

	public ModelAndView saveLogin(HttpServletRequest request, HttpServletResponse response)
	    throws ServletException {
	    JSONObject myjobj = new JSONObject();
	    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
	    def.setName("JE_Tx");
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	    def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
	    TransactionStatus status = txnManager.getTransaction(def);
	    
	try{
		    String contactId =request.getParameter("contactid");
			String emailId =request.getParameter("emailid");
			String companyId =request.getParameter("companyid");
			String setActive= request.getParameter("setActive");
			User creator = sessionHandlerImpl.getUser(request);
            String url= URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull);
            String partnerName = sessionHandlerImplObj.getPartnerName();
			String loginurl = url.concat("caselogin.jsp");
			boolean actFlag = true;
			if(!StringUtil.isNullOrEmpty(setActive)){
				if(setActive.equalsIgnoreCase("true")){
					actFlag = true; 
				}else{
					actFlag = false;
				}
			}
			myjobj=contactManagementService.saveLogins(creator, loginurl,companyId, contactId, emailId,actFlag,partnerName);
			 txnManager.commit(status);
		}  catch (Exception e) {
			logger.warn("Exception in crmContactController.saveContacts:", e);
		    txnManager.rollback(status);
		}
	    return new ModelAndView("jsonView", "model", myjobj.toString());
	}

	public ModelAndView activate_deactivateLogin(HttpServletRequest request, HttpServletResponse response)throws ServletException {
		        JSONObject myjobj = new JSONObject();
		        try {
		        	myjobj.put("success", false);
		            String contactid = request.getParameter("contactid");
		            boolean active = Boolean.parseBoolean(request.getParameter("active"));
		            crmContactDAOObj.activate_deactivateLogin(contactid,active);
		            myjobj.put("success", true);
		        }catch (JSONException e) {
		           	logger.warn("JSONException in crmContactController.deleteContact:", e);
		        } catch (Exception e) {
		            	logger.warn("Exception in crmContactController.deleteContact:", e);
		        }
		return new ModelAndView("jsonView", "model", myjobj.toString());
	}
     	
    private void publishContactModuleInformation(HttpServletRequest request, JSONObject cometObj, int operationCode, String companyid, String userid) throws ServiceException, JSONException, SessionExpiredException {
        String randomNumber = request.getParameter("randomnumber");
        CometManagementService.publishModuleInformation(companyid, userid, randomNumber, Constants.Crm_Contact_modulename, Constants.Crm_contactid, operationCode, cometObj, CometConstants.CRMUPDATES);
    }

    public ModelAndView getContactFromId(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, SessionExpiredException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        String companyid = sessionHandlerImplObj.getCompanyid(request);
        String currencyid = sessionHandlerImpl.getCurrencyID(request);
        requestParams.put("isexport",false);
        requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
        requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_contact_moduleid));
        HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap(requestParams);
       try{
            CrmContact obj = (CrmContact) kwlCommonTablesDAOObj.getObject("com.krawler.crm.database.tables.CrmContact", request.getParameter("recid"));
            JSONObject tmpObj = new JSONObject();
            String aid = crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid");
            tmpObj.put("relatedname", aid);
            tmpObj.put("oldrelatedname", aid);
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            List<String> idsList = new ArrayList<String>();
            idsList.add(obj.getContactid());
            // get owners
            Map<String, List<ContactOwnerInfo>> owners = crmContactDAOObj.getContactOwners(idsList);
            tmpObj = contactManagementService.getContactJsonObject(obj, tmpObj, companyid, currencyid, FieldMap, false, dateFormat, owners);
            jobj.put("data", tmpObj);
       } catch(Exception e) {
    	   logger.warn("Exception in crmContactController.getContactFromId:", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getAllContacts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
       try{
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            String relatedName_importContacts = request.getParameter("relatedName");
            String recId_importContacts = request.getParameter("recId");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            boolean archive =Boolean.parseBoolean("false");
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
            filter_params.add(archive);

            if (!StringUtil.isNullOrEmpty(relatedName_importContacts) && !StringUtil.equal(relatedName_importContacts, "undefined")) {
                filter_names.add("c.crmAccount.accountid");
                filter_params.add(recId_importContacts);
            }

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            if(!heirarchyPerm){
                filter_names.add("INco.usersByUserid.userID");filter_params.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            kmsg = crmContactDAOObj.getAllContacts(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();
                JSONObject jtemp = new JSONObject();

                jtemp.put("cusername", (StringUtil.checkForNull(obj.getFirstname()) + " " + StringUtil.checkForNull(obj.getLastname())).trim());
                jtemp.put("cuserid", obj.getContactid());
                jtemp.put("cemailid", obj.getEmail());
                jtemp.put("caddress", obj.getMailstreet());
                jtemp.put("ccontactno", obj.getPhoneno());
                jarr.put(jtemp);
            }
            jobj.put("data", jarr);
       } catch(Exception e) {
    	   logger.warn("Exception in crmContactController.getAllContacts:", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }


    public JSONObject getContactToEmailJson(List ll, HashMap<String, DefaultMasterItem> defaultMasterMap, Map<String, CrmAccount> accountInfo, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        JSONObject jobjTemp = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String[] myStringArray = new String[] {"createdon", "title", "contactownerid"};
            ArrayList DO_NOT_SHOW_COLUMNS = new ArrayList();
            Collections.addAll(DO_NOT_SHOW_COLUMNS, myStringArray);

            Iterator ite = ll.iterator();
            String companyid = sessionHandlerImplObj.getCompanyid();
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("relatedto", 2);
                tmpObj.put("relatedid", obj.getContactid());
                tmpObj.put("fname", obj.getFirstname());
                tmpObj.put("name", obj.getLastname());
                tmpObj.put("emailid", obj.getEmail());
                tmpObj.put("email", obj.getEmail());
                tmpObj.put("firstname", obj.getFirstname());
                tmpObj.put("lastname", obj.getLastname());
                tmpObj.put("accountid", accountInfo.containsKey(obj.getContactid()) ? ((CrmAccount)accountInfo.get(obj.getContactid())).getAccountname() : "");
                if(obj.getIndustryID() != null && defaultMasterMap.containsKey(obj.getIndustryID())) {
                    tmpObj.put("industryid", defaultMasterMap.get(obj.getIndustryID()).getValue());
                }
                if(obj.getLeadsourceID() != null && defaultMasterMap.containsKey(obj.getLeadsourceID())) {
                    tmpObj.put("leadsourceid", defaultMasterMap.get(obj.getLeadsourceID()).getValue());
                }
                tmpObj.put("description", obj.getDescription());
                tmpObj.put("phoneno", obj.getPhoneno());
                tmpObj.put("mobileno", obj.getMobileno());
                tmpObj.put("street", obj.getMailstreet());
                
                jarr.put(tmpObj);
            }

            ArrayList filter_params = new ArrayList();
            ArrayList filter_names = new ArrayList();
            filter_names.add("dh.moduleName");
            filter_params.add(Constants.Crm_Contact_modulename);
            filter_names.add("dh.customflag");
            filter_params.add(false);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            requestParams.put("order_by", true);
            requestParams.put("order_type", "fieldsequence");
            JSONArray columnJSONArray = crmCommonService.getModuleColumns(requestParams, companyid, Constants.Crm_Contact_modulename);
            for(int cnt =0; cnt < columnJSONArray.length(); cnt++) {
                JSONObject columnObj = columnJSONArray.getJSONObject(cnt);
                if(!DO_NOT_SHOW_COLUMNS.contains(columnObj.getString("recordname"))) {
                    jobjTemp = new JSONObject();
                    jobjTemp.put("header", columnObj.getString("columnName"));
                    jobjTemp.put("tip", columnObj.getString("columnName"));
                    jobjTemp.put("width", 100);
                    jobjTemp.put("sortable", false);
                    jobjTemp.put("dataIndex", columnObj.getString("recordname"));
                    jarrColumns.put(jobjTemp);

                    jobjTemp = new JSONObject();
                    jobjTemp.put("name", columnObj.getString("recordname"));
                    jarrRecords.put(jobjTemp);
                }
            }
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "relatedto");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "relatedid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "fname");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "name");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "emailid");
            jarrRecords.put(jobjTemp);

            jobj.put("columns", jarrColumns);
            jMeta.put("totalProperty", "totalCount");
            jMeta.put("root", "data");
            jMeta.put("fields", jarrRecords);
            jobj.put("metaData", jMeta);
            
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
       } catch(Exception e) {
    	   logger.warn("Exception in crmContactController.getContactToEmailJson:", e);
        }
        return jobj;
    }

    public ModelAndView getContactToEmail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            boolean archive = Boolean.parseBoolean("false");
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.validflag");
            filter_params.add(1);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            filter_names.add("c.isarchive");
            filter_params.add(archive);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            requestParams.put("email", true);
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            kmsg = crmContactDAOObj.getContacts(requestParams, usersList, filter_names, filter_params);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmContactHandler.getContactDefaultMasterItemsMap(companyid, usersList, crmCommonDAOObj, kwlCommonTablesDAOObj);

            List<CrmContact> lst = kmsg.getEntityList();
            List<String> idsList = new ArrayList<String>();
            for (CrmContact obj : lst) {
                idsList.add(obj.getContactid());
            }
            //get Accounts
            Map<String, CrmAccount> accountInfo = crmContactDAOObj.getContactAccount(idsList);

            jobj = getContactToEmailJson(lst, defaultMasterMap, accountInfo, request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
        	logger.warn("Exception in crmContactController.getContactToEmail:", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getContactname(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
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
            String ss = request.getParameter("query");
            if(!StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("pagingFlag", true);
                requestParams.put("start", 0);
                requestParams.put("limit", com.krawler.common.util.Constants.REMOTE_STORE_PAGE_LIMIT);
                requestParams.put("ss", ss);
            }
            
            String hierarchy= request.getParameter("hierarchy");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            if(!StringUtil.isNullOrEmpty(hierarchy) && Boolean.parseBoolean(hierarchy)){
                if(!heirarchyPerm){
                    filter_names.add("INco.usersByUserid.userID");filter_params.add(usersList);
                }
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);

            kmsg = crmContactDAOObj.getAllContacts(requestParams);
            
            int dl = kmsg.getRecordTotalCount();
            JSONObject tmpOb = crmManagerCommon.insertNone();
            jarr.put(tmpOb);
            boolean hasAccess=true ;

            List<CrmContact> ll = kmsg.getEntityList();
             int fromIndex = 0;
             int maxNumbers = Constants.maxrecordsMapCount;
             int totalCount = ll.size();
             while(fromIndex < totalCount) {
                 List<CrmContact> subList;
                 if(totalCount <= maxNumbers) {
                    subList = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    subList = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (CrmContact obj : subList) {
                    idsList.add(obj.getContactid());
                 }
                 Map<String, List<ContactOwnerInfo>> owners = crmContactDAOObj.getContactOwners(idsList);
                 for (CrmContact obj : subList) {
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("id", obj.getContactid());
                    tmpObj.put("name", (StringUtil.checkForNull(obj.getFirstname()) + " " + StringUtil.checkForNull(obj.getLastname())).trim());
                    tmpObj.put("phone", obj.getPhoneno());
                    tmpObj.put("email", obj.getEmail());
                    if(StringUtil.isNullOrEmpty(hierarchy) ){
                        if(!heirarchyPerm) {
                            hasAccess=crmContactHandler.hasContactAccess(owners.get(obj.getContactid()),usersList);
                        }else{
                            hasAccess = !obj.getIsarchive();
                        }
                    }else{
                        hasAccess = !obj.getIsarchive();
                    }
                    tmpObj.put(ContactConstants.Crm_hasAccess,hasAccess);
                    jarr.put(tmpObj);
                 }
                 fromIndex += maxNumbers+1;
             }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
       } catch(Exception e) {
    	   logger.warn("Exception in crmContactController.getContactname:", e);
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView repContact(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String jsondata = request.getParameter("val");
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
                String lname="";
                String fname="";
                int pos=jobj.getString("username").indexOf(' ');
                if(pos==0)
                    lname=jobj.getString("username").trim();
                else if(pos==jobj.getString("username").length()){
                    fname=jobj.getString("username").trim();
                } else {
                    String[] username=jobj.getString("username").split(" ");
                    fname=username[0];
                    lname=username[1];
                }
                String emailid = jobj.getString("emailid");
                String address = jobj.getString("address");
                String contactno = jobj.getString("contactno");
                jobj.put("firstname", fname);
                jobj.put("lastname", lname);
                jobj.put("email", emailid);
                jobj.put("street", address);
                jobj.put("mobile", contactno);
                jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
                
                kmsg = crmContactDAOObj.editContacts(jobj);
            }
            txnManager.commit(status);
        } catch (JSONException e) {
        	logger.warn("JSONException in crmContactController.repContact:", e);
            txnManager.rollback(status);
        } catch (Exception e) {
        	logger.warn("Exception in crmContactController.repContact:", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView newContact(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        CrmContact contact = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = request.getParameter("userid");
            String[] username = request.getParameter("username").split(" ");
            String fname = username[0];
            String lname = username[1];
            String emailid = request.getParameter("emailid");
            String address = request.getParameter("address");
            String contactno = request.getParameter("contactno");
            String id = java.util.UUID.randomUUID().toString();

            jobj.put("contactid", id);
            jobj.put("contactownerid", userid);
            jobj.put("companyid", companyid);
            jobj.put("userid", userid);
            jobj.put("firstname", fname);
            jobj.put("lastname", lname);
            jobj.put("phone", contactno);
            jobj.put("street", address);
            jobj.put("email", emailid);
            jobj.put("validflag", 1);
            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
            kmsg = crmContactDAOObj.addContacts(jobj);
            contact = (CrmContact) kmsg.getEntityList().get(0);

            myjobj.put("success", true);
            myjobj.put("ID", contact.getContactid());
            txnManager.commit(status);
        } catch (JSONException e) {
        	logger.warn("JSONException in crmContactController.newContact:", e);
            txnManager.rollback(status);
        } catch (Exception e) {
        	logger.warn("Exception in crmContactController.newContact:", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    
    public ModelAndView getContacts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
       try{
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            String LeadAccountFlag = request.getParameter("flag");
            String isarchive = request.getParameter("isarchive");
            String LeadAccountName = request.getParameter("name");
            String mapid = request.getParameter("mapid");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            String iscustomcolumn = request.getParameter("iscustomcolumn");
            String xtype = request.getParameter("xtype");
            String xfield = request.getParameter("xfield");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);

            jobj = contactManagementService.getContacts(companyid, userid, currencyid, selectExport, LeadAccountFlag, isarchive, LeadAccountName, mapid, searchJson,
                    ss, config, isExport, heirarchyPerm, field, direction, iscustomcolumn, xtype, xfield, start, limit, dateFormat, usersList);

        } catch (Exception e) {
        	logger.warn("Exception in crmContactController.getContacts:", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView contactExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        String view = "jsonView";
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            String LeadAccountFlag = request.getParameter("flag");
            String isarchive = request.getParameter("isarchive");
            String LeadAccountName = request.getParameter("name");
            String mapid = request.getParameter("mapid");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);

            jobj = contactManagementService.ContactExport(companyid, userid, currencyid, selectExport, LeadAccountFlag,
                    isarchive, LeadAccountName, mapid, searchJson, ss, config, isExport, heirarchyPerm, field, direction, dateFormat, usersList);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.CONTACT_EXPORT,
                    "Contact data exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
        	logger.warn("Exception in crmContactController.contactExport:", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView deleteContact(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String ipAddress = SystemUtil.getIpAddress(request);
            
            myjobj = new JSONObject();
            myjobj.put("success", false);
            JSONArray jarr = null;
            if (request.getAttribute("deletejarr") != null) {
                jarr = (JSONArray) request.getAttribute("deletejarr");
            } else {
                String jsondata = request.getParameter("jsondata");
                jarr = new JSONArray("[" + jsondata + "]");
            }
            ArrayList contactids = new ArrayList();
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobject = jarr.getJSONObject(i);
                contactids.add(jobject.getString("contactid").toString());
            }
            
            String[] arrayid = (String[]) contactids.toArray(new String[]{});
            JSONObject jobj = new JSONObject();
            jobj.put("deleteflag", 1);
            jobj.put("contactid", arrayid);
            jobj.put("userid", userid);
            jobj.put("updatedon", new Date().getTime());
            jobj.put("tzdiff",timeZoneDiff);

            JSONObject contactJSON = contactManagementService.updateMassContact(companyid, userid, timeZoneDiff, ipAddress, jobj);

            List<CrmContact> ll = crmContactDAOObj.getContacts(contactids);

            if(ll!=null){
                for(int i =0 ; i< ll.size() ; i++){
                    CrmContact contactaudit = (CrmContact)ll.get(i);
                    if (contactaudit.getValidflag() == 1) {
                        auditTrailDAOObj.insertAuditLog(AuditAction.CONTACT_DELETE,
                              StringUtil.getFullName(contactaudit.getFirstname(), contactaudit.getLastname()) + " - Contact deleted ",
                                request, contactaudit.getContactid());
                    }
                }
            
            }
            
            if(request.getAttribute("failDelete") != null){
                myjobj.put("failDelete", (JSONArray)request.getAttribute("failDelete"));
            }
            myjobj.put("successDeleteArr", jarr);
            myjobj.put("success", true);
            myjobj.put("ID", contactids.toArray());
            txnManager.commit(status);

            JSONObject cometObj = new JSONObject();
            cometObj.put("ids",  jarr);
            publishContactModuleInformation(request, cometObj, CrmPublisherHandler.DELETERECORDCODE, sessionHandlerImpl.getCompanyid(request),userid);
        } catch (JSONException e) {
        	logger.warn("JSONException in crmContactController.deleteContact:", e);
            txnManager.rollback(status);
        } catch (Exception e) {
        	logger.warn("Exception in crmContactController.deleteContact:", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView saveContactOwners(HttpServletRequest request, HttpServletResponse response)
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
            String contactid = request.getParameter("leadid");
            String owners = request.getParameter("owners");
            String mainowner = request.getParameter("mainOwner");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("contactid", contactid);
            requestParams.put("owners", owners);
            requestParams.put("mainOwner", mainowner);
            kmsg = crmContactDAOObj.saveContactOwners(requestParams);
            // Fetch subowners name list
            List<String> idsList = new ArrayList<String>();
            idsList.add(contactid);
            Map<String, List<ContactOwnerInfo>> ownersMap = crmContactDAOObj.getContactOwners(idsList);
            String[] ownerInfo = crmContactHandler.getAllContactOwners(ownersMap.get(contactid));
            myjobj.put("subowners", ownerInfo[1]);

            myjobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (SessionExpiredException e) {
        	logger.warn("SessionExpiredException in crmContactController.saveContactOwners:", e);
            txnManager.rollback(status);
        } catch (JSONException e) {
        	logger.warn("JSONException in crmContactController.saveContactOwners:", e);
            txnManager.rollback(status);
        } catch (ServiceException e) {
        	logger.warn("ServiceException in crmContactController.saveContactOwners:", e);
            txnManager.rollback(status);
        } catch (Exception e) {
        	logger.warn("Exception in crmContactController.saveContactOwners:", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView getExistingContactOwners(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
       try{
            String[] ownerInfo = crmContactHandler.getAllContactOwners(crmContactDAOObj, request.getParameter("leadid"));
            jobj.put("mainOwner",ownerInfo[2] );
            jobj.put("ownerids", ownerInfo[3]);
       } catch(Exception e) {
    	   logger.warn("Exception in crmContactController.getExistingContactOwners:", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView importContact(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        boolean bsuccess = false;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            JSONObject contactJson = new JSONObject();
            String type = request.getParameter("importtype");
            if(type.equals("account")) {
                contactJson.put("accountid", request.getParameter("id"));
            } else if(type.equals("lead")) {
                contactJson.put("leadid", request.getParameter("id"));
            }
            contactJson.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
            JSONArray jarr = new JSONArray("[" + request.getParameter("finalContacts")+ "]");

            for(int i=0;i<jarr.length();i++) {
                JSONObject jobj = jarr.getJSONObject(i);
                contactJson.put("contactid", jobj.get("contactid"));
                kmsg = crmContactDAOObj.editContacts(contactJson);
            }

            if(!StringUtil.isNullOrEmpty(request.getParameter("dupContacts"))) {
                JSONArray dupjarr = new JSONArray("[" + request.getParameter("dupContacts")+ "]");
                for(int i=0;i<dupjarr.length();i++) {
                    JSONObject jobj = dupjarr.getJSONObject(i);
                    contactJson.put("contactid", jobj.get("contactid"));
                    kmsg = crmContactDAOObj.editContacts(contactJson);
                }
            }
            txnManager.commit(status);
            bsuccess = true;
        } catch (JSONException ex) {
        	logger.warn("JSONException in crmContactController.importContact:", ex);
            txnManager.rollback(status);
            bsuccess = false;
        } catch (SessionExpiredException e)
        {
        }
        try {
            if(bsuccess) {
                myjobj.put("success", true);
            } else {
                myjobj.put("success", false);
            }
        } catch (JSONException ex) {
        	logger.warn("JSONException in crmContactController.importContact:", ex);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

	public ModelAndView updateMassContacts(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject myjobj = new JSONObject();
		//String contactId = null;
		// Create transaction
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		try {
			JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
			JSONObject contactJSON = new JSONObject();
			Integer operationCode = CrmPublisherHandler.UPDATERECORDCODE;
			String userid = sessionHandlerImpl.getUserid(request);
			String companyid = sessionHandlerImpl.getCompanyid(request);
			String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
			// extract ip address
			String ipAddress = SystemUtil.getIpAddress(request);
			String contactIds = jobj.getString("contactid");
			String arrayId[] = contactIds.split(",");
			jobj.put("contactid", arrayId);
			JSONArray jcustomarray = null;
			if (jobj.has("customfield")) {
				jcustomarray = jobj.getJSONArray("customfield");
			}

			if (jobj.has("customfield")) {
				HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
				customrequestParams.put("customarray", jcustomarray);
				customrequestParams.put("modulename", Constants.Crm_Contact_modulename);
				customrequestParams.put("moduleprimarykey", Constants.Crm_Contactid);

				customrequestParams.put("companyid", companyid);
				customrequestParams.put("customdataclasspath", Constants.Crm_contact_custom_data_classpath);
				KwlReturnObject customDataresult = null;
				for (String id : arrayId) {
					customrequestParams.put("modulerecid", id);
					customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
				}
				if (customDataresult != null && customDataresult.getEntityList().size() > 0) {
					jobj.put("CrmContactCustomDataobj", true);
				}
			}
            if (jobj.has("updatedon") && !StringUtil.isNullOrEmpty(jobj.getString("updatedon"))) {
                jobj.put("updatedon", jobj.getLong("updatedon"));
            } else {
                jobj.put("updatedon", new Date().getTime());
            }
			if (jobj.has("createdon") && !StringUtil.isNullOrEmpty(jobj.getString("createdon"))) {
				jobj.put("createdon", jobj.getString("createdon"));
			}

			contactJSON = contactManagementService.updateMassContact(companyid, userid, timeZoneDiff, ipAddress, jobj);
			//contactId = contactJSON.getString("ID");

			txnManager.commit(status);
			JSONObject cometObj = jobj;
			if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
				crmCommonService.validateMassupdate(arrayId, "Contact", companyid);
				cometObj.put("contactid", contactIds);
				cometObj.put("ismassedit", true);
			}
			myjobj.put("success", true);
			myjobj.put("ID", contactIds);
			myjobj.put("createdon", jobj.has("createdon") ? jobj.getLong("createdon") : "");

			if (jobj.has("accountid"))
				cometObj.put("relatedname", jobj.getString("accountid")); // added
			/* for commet... JS side, store's dataIndex kye	must contains in the json object also */
			if (contactJSON.has("createdon")) {
				cometObj.put("createdon", contactJSON.has("createdon") ? contactJSON.getString("createdon") : "");
			}
			publishContactModuleInformation(request, cometObj, operationCode, companyid, userid);

		} catch (Exception e) {
			logger.warn("Exception in crmContactController.saveContacts:", e);
			txnManager.rollback(status);
		}
		return new ModelAndView("jsonView", "model", myjobj.toString());
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.mSource=messageSource;
		
	}
}
