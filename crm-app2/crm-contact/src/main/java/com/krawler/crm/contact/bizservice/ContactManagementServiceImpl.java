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
package com.krawler.crm.contact.bizservice;

import com.krawler.baseline.crm.bizservice.el.ExpressionManager;
import com.krawler.baseline.crm.bizservice.el.ExpressionVariables;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.utils.AuditAction;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.crm.utils.Constants;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.contact.dm.ContactOwnerInfo;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmContactCustomData;
import com.krawler.crm.database.tables.CrmCustomer;
import com.krawler.crm.database.tables.DefaultMasterItem;
import java.text.DateFormat;
import java.util.Iterator;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.contactModule.crmContactHandler;
import com.krawler.spring.importFunctionality.ImportHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.trilead.ssh2.log.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.ObjectNotFoundException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 *
 * @author sm
 */
public class ContactManagementServiceImpl implements ContactManagementService {
	private Log logger = LogFactory.getLog(ContactManagementServiceImpl.class);
    private crmContactDAO crmContactDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private crmCommonDAO crmCommonDAO;
    private fieldManagerDAO fieldManagerDAOobj;
    private ExpressionManager expressionManager;

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    public ExpressionManager getExpressionManager() {
        return expressionManager;
    }

    public void setExpressionManager(ExpressionManager expressionManager) {
        this.expressionManager = expressionManager;
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

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    @Override
    public JSONObject saveContact(String companyId, String userId, String timeZoneDiff, String ipAddress, JSONObject jsonData) throws ServiceException, JSONException{
        JSONObject resultJsonObj = new JSONObject();
        CrmContact contact = null;
        resultJsonObj.put("success", false);
        String auditmsg = "";
        KwlReturnObject kmsg = null;
        String contactId = null;
        if(jsonData.has("contactid")) {
            contactId = jsonData.getString("contactid");
        } else {
            contactId = "0";
        }

        if(!StringUtil.isNullOrEmpty(userId))
            jsonData.put("userid", userId);
        if(!StringUtil.isNullOrEmpty(companyId))
            jsonData.put("companyid", companyId);
        if(!StringUtil.isNullOrEmpty(timeZoneDiff))
            jsonData.put("tzdiff", timeZoneDiff);

        jsonData.put("updatedon", new Date().getTime());
        if (contactId.equals("0")) {
            contactId = java.util.UUID.randomUUID().toString();
            jsonData.put("contactid", contactId);
            kmsg = crmContactDAOObj.addContacts(jsonData);
            contact = (CrmContact) kmsg.getEntityList().get(0);
            auditmsg = (StringUtil.checkForNull(contact.getFirstname()) + " " + StringUtil.checkForNull(contact.getLastname())).trim() + " - Contact created ";
            createAuditLog(contact, auditmsg, AuditAction.CONTACT_CREATE, ipAddress, userId, contactId);
        } else {
           kmsg = crmContactDAOObj.editContacts(jsonData);
           contact = (CrmContact) kmsg.getEntityList().get(0);
           if(jsonData.has("auditstr"))
               auditmsg = jsonData.getString("auditstr");
           auditmsg = auditmsg + " Contact - " + (StringUtil.checkForNull(contact.getFirstname()) + " " + StringUtil.checkForNull(contact.getLastname())).trim();
           createAuditLog(contact, auditmsg, AuditAction.CONTACT_UPDATE, ipAddress, userId, contactId);
        }
        resultJsonObj.put("success", kmsg.isSuccessFlag());
        if (contact != null) {
            resultJsonObj.put("ID", contact.getContactid());
            resultJsonObj.put("createdon", contact.getCreatedOn());
            resultJsonObj.put("updatedon", contact.getUpdatedOn());
        }
        return resultJsonObj;
    }
    public String getSHA1(String inStr) throws ServiceException {
        String outStr = inStr;
        try {
            byte[] theTextToDigestAsBytes = inStr.getBytes("utf-8");

            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] digest = sha.digest(theTextToDigestAsBytes);

            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                String h = Integer.toHexString(b & 0xff);
                if (h.length() == 1) {
                    sb.append("0" + h);
                } else {
                    sb.append(h);
                }
            }
            outStr = sb.toString();
        } catch (UnsupportedEncodingException e) {
        	logger.info("Error in Encoding");
        } catch (NoSuchAlgorithmException e) {
            logger.info("Error in Algorithm");
        }
        return outStr;
    }

    @Override
    public boolean verifyCurrentPass(String curpass, String customerid) throws ServiceException{
    	String enc_pass=getSHA1(curpass);
    	CrmCustomer cob=crmContactDAOObj.getCustomer(customerid);
    	if(cob.getPasswd().equalsIgnoreCase(enc_pass))
    		return true;
    	else
    		return false;
    }

    public JSONObject saveLogins(User creator, String loginurl,String companyId, String contactId, String emailId,Boolean setActive, String partnerNames) throws ServiceException, JSONException{
    	JSONObject resultJsonObj = new JSONObject();
    	JSONObject jsonData = new JSONObject();
        if(crmContactDAOObj.isEmailIdExist(emailId,companyId)){
        		resultJsonObj.put("mailIdExist", true);
        }else{
    	resultJsonObj.put("success", false);
    	 KwlReturnObject kmsg = null;
    	 String username=creator.getFullname();
         String creatoremail = creator.getEmailID();
    	String uuid = UUID.randomUUID().toString();
    	String pswd =  RandomStringUtils.random(8, true, true);//UUID.randomUUID().toString().substring(0, 8);
    	String encodedpswd=getSHA1(pswd);
    	if(!StringUtil.isNullOrEmpty(emailId))
        jsonData.put("emailId", emailId);
    	if(!StringUtil.isNullOrEmpty(contactId))
        jsonData.put("contactId", contactId);
    	if(!StringUtil.isNullOrEmpty(companyId))
        jsonData.put("companyId", companyId);
    	jsonData.put("customerid", uuid);
    	jsonData.put("pswd", encodedpswd);
    	jsonData.put("setActive", setActive);
    	kmsg = crmContactDAOObj.loginMail(jsonData);
    	if(kmsg.isSuccessFlag()){
    	String passwordString = "\n\nUsername: " + emailId + " \nPassword: " + pswd;
        String passwordHtmlString = "<p>Username: " + emailId + " </p><p>Password: " + pswd+"</p>";
    	String fname=((Object[])kmsg.getEntityList().get(1))[0]!=null?((Object[])kmsg.getEntityList().get(1))[0].toString():"";
    	String lname=((Object[])kmsg.getEntityList().get(1))[1]!=null?((Object[])kmsg.getEntityList().get(1))[1].toString():"";
    	Object name=  (fname +" "+lname).trim();
    	Object companyName=kmsg.getEntityList().toArray()[2];
    	String msgMailInvite = "Dear %s,\n\n%s has created an account for you at %s." + passwordString + "\n\nYou can log in at:\n%s\n\n\n - %s";
    	String pmsg = String.format(msgMailInvite, name,companyName,username,loginurl,username);
    	String msgMailInviteUsernamePassword = "<html><head><title>"+partnerNames+" CRM - Your Account</title></head><style type='text/css'>" + "a:link, a:visited, a:active {\n" + " 	color: #03C;" + "}\n" + "body {\n" + "	font-family: Arial, Helvetica, sans-serif;" + "	color: #000;" + "	font-size: 13px;" + "}\n" + "</style><body>" + "	<div>" + "		<p>Dear <strong>%s</strong>,</p>" + "		<p>%s has created an account for you at %s.</p>" + passwordHtmlString + "		<p>You can log in to your account at: <a href=%s>%s</a></p>" + "		<br/><p> - %s</p>" + "	</div></body></html>";
        String htmlmsg = String.format(msgMailInviteUsernamePassword, name, username,  companyName,loginurl,loginurl,username);
            try {
    				SendMailHandler.postMail(new String[] { emailId },"Welcome to "+companyName, htmlmsg, pmsg, creatoremail ,companyName+" Admin");
    		    }catch (MessagingException e){
    		    	e.printStackTrace();
    		    }catch (UnsupportedEncodingException ue){
    		    	ue.printStackTrace();
    		   }
    	}
	    resultJsonObj.put("success", kmsg.isSuccessFlag());
	    resultJsonObj.put("msg", kmsg.getMsg());
        }
    	return resultJsonObj;
    }

    @Override
    public void custPassword_Change(String newpass,String customerid)throws ServiceException{
    	String enc_pass=getSHA1(newpass);
    	crmContactDAOObj.custPassword_Change(enc_pass, customerid);
    }

    protected void createAuditLog(CrmContact contact, String auditmsg, String actionId, String ipAddress, String userId, String id) throws ServiceException, JSONException {
        if (contact.getValidflag() == 1) {
            auditTrailDAOObj.insertAuditLog(actionId, auditmsg, ipAddress, userId, id);
        }
    }

    private crmManagerDAO crmManagerDAOObj;
    private fieldDataManager fieldDataManagercntrl;
    private commentDAO crmCommentDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private static final Log LOGGER = LogFactory.getLog(ContactManagementServiceImpl.class);

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }

    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }

    /**
     *
     * @param obj
     * @param tmpObj
     * @param FieldMap
     * @throws com.krawler.utils.json.base.JSONException
     */
    public void getCustomColumnJSON(CrmContact obj, JSONObject tmpObj,HashMap<String, Integer> FieldMap) throws JSONException {
        // create json object of custom columns added in lead grid
        if(obj.getCrmContactCustomDataobj()!=null){
                CrmContactCustomData CrmContactCustomDataobj = obj.getCrmContactCustomDataobj();
                Iterator keyit = FieldMap.keySet().iterator();
                while(keyit.hasNext()){
                    String fieldname = (String) keyit.next();
                    if(FieldMap.get(fieldname) > 0){
                        String coldata = CrmContactCustomDataobj.getCol(FieldMap.get(fieldname));
                        if(!StringUtil.isNullOrEmpty(coldata)){
                            tmpObj.put(fieldname, coldata);
                        }
                    }
                }
            }
    }

    /**
     *
     * @param obj
     * @param tmpObj
     * @param FieldMap
     * @param isexport
     * @param dateFormat
     * @throws com.krawler.utils.json.base.JSONException
     */
    public void getCustomColumnJSON(CrmContact obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap, boolean isexport, DateFormat dateFormat) throws JSONException {
        // create json object of custom columns added in lead grid
        if (obj.getCrmContactCustomDataobj() != null && isexport) {
            CrmContactCustomData CrmContactCustomDataobj = obj.getCrmContactCustomDataobj();
            Iterator keyit = FieldMap.keySet().iterator();
            while (keyit.hasNext()) {
                String fieldname = (String) keyit.next();
                Integer colnumber = FieldMap.get(fieldname);

                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
                    Integer isref = FieldMap.get(fieldname + colnumber);
                    String coldata = null;
                    if (isref != null) {
                        coldata = CrmContactCustomDataobj.getCol(FieldMap.get(fieldname));
                        if (!StringUtil.isNullOrEmpty(coldata) && coldata.length() > 1) {
                            if (isref == 1) {
                                coldata = fieldDataManagercntrl.getMultiSelectColData(coldata);
                            } else if (isref == 0) {
                                coldata = CrmContactCustomDataobj.getRefCol(FieldMap.get(fieldname));
                            }
                        }
                        if (!StringUtil.isNullOrEmpty(coldata)) {
                            tmpObj.put(fieldname, coldata);
                        }
                    }
                }
            }
        } else {
            getCustomColumnJSON(obj, tmpObj, FieldMap);
        }
    }

    /**
     *
     * @param obj
     * @param tmpObj
     * @param companyid
     * @param currencyid
     * @param FieldMap
     * @param isexport
     * @param dateFormat
     * @return
     */
    public JSONObject getContactJsonObject(CrmContact obj, JSONObject tmpObj, String companyid, String currencyid, HashMap<String, Integer> FieldMap,boolean isexport, DateFormat dateFormat, Map<String, List<ContactOwnerInfo>> owners) {
        try {
            tmpObj.put("contactid", obj.getContactid());
            String[] ownerInfo = crmContactHandler.getAllContactOwners(owners.get(obj.getContactid()));
            tmpObj.put("subowners", ownerInfo[1]);
            tmpObj.put("contactowner",ownerInfo == null? "": ownerInfo[0] );
            tmpObj.put("contactownerid", ownerInfo[2]);
            tmpObj.put("owner", ownerInfo[5]);
            tmpObj.put("firstname", StringUtil.checkForNull(obj.getFirstname()));
            tmpObj.put("lastname", StringUtil.checkForNull(obj.getLastname()));
            tmpObj.put("fullname", StringUtil.checkForNull(obj.getFirstname()) + " "+StringUtil.checkForNull(obj.getLastname()));
            tmpObj.put("phoneno", StringUtil.checkForNull(obj.getPhoneno()));
            if(obj.getCrmAccount()!=null) {
                tmpObj.put("accountid",StringUtil.checkForNull(obj.getCrmAccount().getAccountid()));
            } else {
                tmpObj.put("accountid","");
            }
            if(obj.getLead()!=null) {
                tmpObj.put("leadid",StringUtil.checkForNull(obj.getLead().getLeadid()));
            } else {
                tmpObj.put("leadid","");
            }
            tmpObj.put("mobileno", obj.getMobileno() != null ? obj.getMobileno() : "");
            tmpObj.put("email", StringUtil.checkForNull(obj.getEmail()));
            tmpObj.put("street", StringUtil.checkForNull(obj.getMailstreet()));
            tmpObj.put("description", obj.getDescription() != null ? obj.getDescription() : "");
            tmpObj.put("createdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
            tmpObj.put("creatdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
            tmpObj.put("updatedon", obj.getUpdatedOn()!=null?obj.getUpdatedOn():"");
            tmpObj.put("validflag", obj.getValidflag());
            tmpObj.put("leadsourceid", crmManagerCommon.comboNull(obj.getCrmCombodataByLeadsourceid()));
            tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
            tmpObj.put("title", obj.getTitle() != null ? obj.getTitle() : "");
            tmpObj.put("pdfrelatedname", obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : "" );
            tmpObj.put("industryid", crmManagerCommon.comboNull(obj.getCrmCombodataByIndustryid()));
            tmpObj.put("industry", (obj.getCrmCombodataByIndustryid() != null ? obj.getCrmCombodataByIndustryid().getValue() : ""));

            if (obj.getCellstyle() != null && !obj.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(obj.getCellstyle()));
            }
            getCustomColumnJSON(obj,tmpObj,FieldMap,isexport,dateFormat);
            tmpObj = fieldDataManagercntrl.applyColumnFormulae(companyid, currencyid, tmpObj, "6", "Contact");
        } catch (Exception e) {
            LOGGER.warn("Exception in crmContactController.getContactJsonObject:", e);
        }
        return tmpObj;
    }

    /**
     *
     * @param exprVarMap
     * @param obj
     * @param tmpObj
     * @param companyid
     * @param currencyid
     * @param FieldMap
     * @param isexport
     * @param dateFormat
     * @param defaultMasterMap
     * @param contactCustomDataMap
     * @param owners
     * @return
     */
    public JSONObject getContactJsonObject(Map<String, ExpressionVariables> exprVarMap, CrmContact obj, JSONObject tmpObj, String companyid, String currencyid,
            HashMap<String, Integer> FieldMap,boolean isexport, DateFormat dateFormat, HashMap<String, DefaultMasterItem> defaultMasterMap,
            HashMap<String, CrmContactCustomData> contactCustomDataMap, Map<String, List<ContactOwnerInfo>> owners) {
        try {
            tmpObj.put("contactid", obj.getContactid());
            String[] ownerInfo = crmContactHandler.getAllContactOwners(owners.get(obj.getContactid()));
            tmpObj.put("contactowner",ownerInfo == null? "": ownerInfo[0] );
            tmpObj.put("subowners", ownerInfo[1]);
            tmpObj.put("contactownerid", ownerInfo[2]);
            tmpObj.put("owner", ownerInfo[5]);
            tmpObj.put("firstname", StringUtil.checkForNull(obj.getFirstname()));
            tmpObj.put("lastname", StringUtil.checkForNull(obj.getLastname()));
            tmpObj.put("fullname", StringUtil.checkForNull(obj.getFirstname()) + " "+StringUtil.checkForNull(obj.getLastname()));
            tmpObj.put("phoneno", StringUtil.checkForNull(obj.getPhoneno()));
            if(obj.getCrmAccount()!=null) {
                tmpObj.put("accountid",StringUtil.checkForNull(obj.getCrmAccount().getAccountid()));
            } else {
                tmpObj.put("accountid","");
            }
            if(obj.getLead()!=null) {
                tmpObj.put("leadid",StringUtil.checkForNull(obj.getLead().getLeadid()));
            } else {
                tmpObj.put("leadid","");
            }
            tmpObj.put("mobileno", obj.getMobileno() != null ? obj.getMobileno() : "");
            tmpObj.put("email", StringUtil.checkForNull(obj.getEmail()));
            tmpObj.put("street", StringUtil.checkForNull(obj.getMailstreet()));
            tmpObj.put("description", obj.getDescription() != null ? obj.getDescription() : "");
            tmpObj.put("createdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
            tmpObj.put("creatdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
            tmpObj.put("updatedon", obj.getUpdatedOn()!=null?obj.getUpdatedOn():"");
            tmpObj.put("validflag", obj.getValidflag());
            tmpObj.put("leadsourceid", StringUtil.hNull(obj.getLeadsourceID()));
            String source = "";
            if(obj.getLeadsourceID() != null && defaultMasterMap.containsKey(obj.getLeadsourceID())) {
                source = defaultMasterMap.get(obj.getLeadsourceID()).getValue();
            }
            tmpObj.put("leadsource", source);
            tmpObj.put("industryid", StringUtil.hNull(obj.getIndustryID()));
            String industry = "";
            if(obj.getIndustryID() != null && defaultMasterMap.containsKey(obj.getIndustryID())) {
                industry = defaultMasterMap.get(obj.getIndustryID()).getValue();
            }
            tmpObj.put("industry", industry);
            tmpObj.put("title", obj.getTitle() != null ? obj.getTitle() : "");
            tmpObj.put("pdfrelatedname", obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : "" );

            if (obj.getCellstyle() != null && !obj.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(obj.getCellstyle()));
            }
            getCustomColumnJSON(obj, tmpObj, FieldMap, contactCustomDataMap, exprVarMap);
            getCalculatedCustomColumnJSON(obj, tmpObj, exprVarMap);
        } catch (Exception e) {
            LOGGER.warn("Exception in crmContactController.getContactJsonObject:", e);
        }
        return tmpObj;
    }

    /**
     *
     * @param ll
     * @param totalSize
     * @param isexport
     * @param dateFormat
     * @param userid
     * @param companyid
     * @param currencyid
     * @param selectExportJson
     * @return
     * @throws com.krawler.common.session.SessionExpiredException
     */
    public JSONObject getContactJson(List ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson, StringBuffer usersList) throws SessionExpiredException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Object totalcomment = 0;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_contact_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);
            
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmContactHandler.getContactDefaultMasterItemsMap(companyid, usersList, crmCommonDAO, kwlCommonTablesDAOObj);
            HashMap<String, String> commentCountMap = crmCommentDAOObj.getNewCommentCount(userid);

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
                 HashMap<String, String> totalCommentCountMap = crmCommentDAOObj.getTotalCommentsCount(idsList, companyid);
                 HashMap<String, CrmContactCustomData> contactCustomDataMap = crmContactDAOObj.getContactCustomDataMap(idsList, companyid);
                 Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(subList, contactCustomDataMap, companyid, FieldMap, replaceFieldMap, isexport, dateFormat);
                 Map<String, Integer> customloginInfo = crmContactDAOObj.getLoginState(idsList);
                 //get Accounts
                 Map<String, CrmAccount> accountInfo = crmContactDAOObj.getContactAccount(idsList);
                 // get owners
                 Map<String, List<ContactOwnerInfo>> owners = crmContactDAOObj.getContactOwners(idsList);
                 for (CrmContact obj : subList) {
                    requestParams.clear();
                    requestParams.put("recid", obj.getContactid());
                    requestParams.put("companyid", companyid);
                    JSONObject tmpObj = new JSONObject();
                    String aid = accountInfo.containsKey(obj.getContactid()) ? ((CrmAccount)accountInfo.get(obj.getContactid())).getAccountid() : "";
                    String aname = accountInfo.containsKey(obj.getContactid()) ? ((CrmAccount)accountInfo.get(obj.getContactid())).getAccountname() : "";
                    if(StringUtil.isNullOrEmpty(aid) || aid.equals("99")){
                        JSONObject jobjNone = new JSONObject();
                        jobjNone = crmManagerCommon.insertNone();
                        aid=jobjNone.getString("id");
                        aname=jobjNone.getString("name");
                    }
                    tmpObj.put("relatedname", aname);
                    tmpObj.put("relatednameid", aid);
                    tmpObj.put("oldrelatedname", aid);
                    tmpObj.put("loginstate", customloginInfo.containsKey(obj.getContactid()) ? customloginInfo.get(obj.getContactid()) : 0);
                    totalcomment = 0;
                    if(totalCommentCountMap.containsKey(obj.getContactid())) {
                        totalcomment = totalCommentCountMap.get(obj.getContactid());
                    }
                    tmpObj.put("totalcomment", totalcomment);
                    tmpObj = getContactJsonObject(exprVarMap, obj, tmpObj, companyid, currencyid, FieldMap,isexport, dateFormat, defaultMasterMap, contactCustomDataMap, owners);
                    tmpObj.put("commentcount", (commentCountMap.containsKey(obj.getContactid())?commentCountMap.get(obj.getContactid()):"0"));
                    jarr.put(tmpObj);
                 }
                 fromIndex += maxNumbers+1;
             }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
        	LOGGER.warn("Exception in crmContactController.getContactJson:", e);
        }
        return jobj;
    }

    /**
     *
     * @param ll
     * @param totalSize
     * @param isexport
     * @param dateFormat
     * @param userid
     * @param companyid
     * @param currencyid
     * @param selectExportJson
     * @return
     * @throws com.krawler.common.session.SessionExpiredException
     */
    public JSONObject getContactJsonExport(List<CrmContact> ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson, StringBuffer usersList) throws SessionExpiredException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_contact_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmContactHandler.getContactDefaultMasterItemsMap(companyid, usersList, crmCommonDAO, kwlCommonTablesDAOObj);

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
                 HashMap<String, CrmContactCustomData> contactCustomDataMap = crmContactDAOObj.getContactCustomDataMap(idsList, companyid);
                 Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(subList, contactCustomDataMap, companyid, FieldMap, replaceFieldMap, isexport, dateFormat);

                 // get owners
                 Map<String, List<ContactOwnerInfo>> owners = crmContactDAOObj.getContactOwners(idsList);

                 for (CrmContact obj : subList) {
                    requestParams.clear();
                    requestParams.put("companyid", companyid);
                    requestParams.put("recid", obj.getContactid());
                    JSONObject tmpObj = new JSONObject();
                    String aid = crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid");
                    if(StringUtil.isNullOrEmpty(aid)){
                        JSONObject jobjNone = new JSONObject();
                        jobjNone = crmManagerCommon.insertNone();
                        aid=jobjNone.getString("id");
                    }
                    tmpObj.put("relatedname", aid);
                    tmpObj.put("oldrelatedname", aid);
                    tmpObj = getContactJsonObject(exprVarMap, obj, tmpObj, companyid, currencyid, FieldMap,isexport, dateFormat, defaultMasterMap, contactCustomDataMap, owners);
                    jarr.put(tmpObj);
                }
                fromIndex += maxNumbers+1;
             }

            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
        	LOGGER.warn("Exception in crmContactController.getContactJson:", e);
        }
        return jobj;
    }


    /**
     *
     * @param companyid
     * @param userid
     * @param currencyid
     * @param selectExportJson
     * @param LeadAccountFlag
     * @param isarchive
     * @param LeadAccountName
     * @param mapid
     * @param searchJson
     * @param ss
     * @param config
     * @param isExport
     * @param heirarchyPerm
     * @param field
     * @param direction
     * @param iscustomcolumn
     * @param xtype
     * @param xfield
     * @param start
     * @param limit
     * @param dateFormat
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getContacts(String companyid, String userid, String currencyid, String selectExportJson, String LeadAccountFlag,
            String isarchive, String LeadAccountName, String mapid, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xtype, String xfield,
            String start, String limit, DateFormat dateFormat, StringBuffer usersList) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            boolean archive = Boolean.parseBoolean(isarchive);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if (LeadAccountFlag != null) {
               if (Integer.parseInt(LeadAccountFlag) == 60) {
                    if(!StringUtil.isNullOrEmpty(LeadAccountName) && LeadAccountName.equals("LeadContact")){
                        filter_names.add("c.Lead.leadid");
                    } else {
                        filter_names.add("c.crmAccount.accountid");
                    }
                    filter_params.add(mapid);
               }
            }
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.isarchive");
            filter_params.add(archive);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", isarchive);
            if(searchJson != null && !StringUtil.isNullOrEmpty(searchJson)) {
                requestParams.put("searchJson", searchJson);
            }
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
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
                requestParams.put("dateasnum", true);
                if(xtype.equals(Constants.AUTONO)) {
                    crmManagerCommon.getAutoNoPrefixSuffix(fieldManagerDAOobj, companyid, xfield,Constants.Crm_contact_moduleid,requestParams);
                }
            }
            if(!StringUtil.isNullOrEmpty(start) || !StringUtil.isNullOrEmpty(limit)) {
                requestParams.put("start", StringUtil.checkForNull(start));
                requestParams.put("limit", StringUtil.checkForNull(limit));
            }

            kmsg = crmContactDAOObj.getContacts(requestParams, usersList, filter_names, filter_params);
            jobj = getContactJson(kmsg.getEntityList(), kmsg.getRecordTotalCount(),false, dateFormat, userid, companyid, currencyid, selectExportJson, usersList);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    /**
     *
     * @param companyid
     * @param userid
     * @param currencyid
     * @param selectExportJson
     * @param LeadAccountFlag
     * @param isarchive
     * @param LeadAccountName
     * @param mapid
     * @param searchJson
     * @param ss
     * @param config
     * @param isExport
     * @param heirarchyPerm
     * @param field
     * @param direction
     * @param dateFormat
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject ContactExport(String companyid, String userid, String currencyid, String selectExportJson, String LeadAccountFlag,
            String isarchive, String LeadAccountName, String mapid, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, DateFormat dateFormat, StringBuffer usersList) throws ServiceException
    {
            JSONObject jobj = new JSONObject();
            KwlReturnObject kmsg = null;

        try{
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if (LeadAccountFlag != null) {
               if (Integer.parseInt(LeadAccountFlag) == 60) {
                    if(!StringUtil.isNullOrEmpty(LeadAccountName) && LeadAccountName.equals("LeadContact")){
                        filter_names.add("c.Lead.leadid");
                    } else {
                        filter_names.add("c.crmAccount.accountid");
                    }
                    filter_params.add(mapid);
               }
            }
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.isarchive");
            filter_params.add(false);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", isarchive);
            if(searchJson != null && !StringUtil.isNullOrEmpty(searchJson)) {
                requestParams.put("searchJson", searchJson);
            }
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
            if (config != null) {
                requestParams.put("config", config);
            }

            requestParams.put("export", isExport);
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(field)) {
                requestParams.put("field", field);
                requestParams.put("direction", direction);
            }

            String jsondata = selectExportJson;
            List<CrmContact> ll;
            int totalSize = 0;
            if (StringUtil.isNullOrEmpty(jsondata)) {//Export/Print All records
                kmsg = crmContactDAOObj.getContacts(requestParams, usersList, filter_names, filter_params);
                ll = kmsg.getEntityList();
                totalSize = kmsg.getRecordTotalCount();
            } else {
                JSONArray jArr = new JSONArray("[" + jsondata + "]");
                totalSize = jArr.length();
                List<String> idsList = new ArrayList<String>();
                for (int i = 0; i < totalSize; i++) {
                    JSONObject jObj = jArr.getJSONObject(i);
                    idsList.add(jObj.getString("id"));
                }
                ll = crmContactDAOObj.getContacts(idsList);
            }
            boolean isexport = true;
            jobj = getContactJsonExport(ll, totalSize, isexport, dateFormat, userid, companyid, currencyid, selectExportJson, usersList);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public Map<String, ExpressionVariables> expressionVariableMap(List<CrmContact> ll, HashMap<String, CrmContactCustomData> customDataMap, String companyid,
            HashMap<String, Integer> FieldMap, HashMap<String, String> replaceFieldMap, boolean isExport,DateFormat dateFormat) {
        Map<String, ExpressionVariables> exprVarMap = new HashMap<String, ExpressionVariables>();
        try {
            List<Object[]> formulae = fieldManagerDAOobj.getModuleCustomFormulae(Constants.Crm_contact_moduleid, companyid);
            if (ll != null && !ll.isEmpty()) {
                for (CrmContact obj : ll) {
                    ExpressionVariables var = new ExpressionVariables();
                    Map<String, Object> variableMapForFormula = new HashMap<String, Object>();
                    Map<String, Object> variableMap = new HashMap<String, Object>();
                    variableMap.put(Constants.Crm_contact_modulename, obj);
                    variableMapForFormula.put(Constants.Crm_contact_modulename, obj);
                    setCustomColumnValues(obj, FieldMap, replaceFieldMap, variableMap, variableMapForFormula, customDataMap, isExport, dateFormat);
                    var.setVariablesMap(variableMap);
                    var.setVariablesMapForFormulae(variableMapForFormula);
                    exprVarMap.put(obj.getContactid(), var);
                }
            }

            if (formulae != null) {
                for (Object[] formula : formulae) {
                    getExpressionManager().evaluateExpression((String) formula[0], (String) formula[1], exprVarMap);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return exprVarMap;
        }
    }

    public void getCustomColumnJSON(CrmContact obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap,
            HashMap<String, CrmContactCustomData> customDataMap, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        // create json object of custom columns added in lead grid
        if (exprVarMap.containsKey(obj.getContactid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getContactid());

            for (Entry<String, Object> varEntry : expVar.getVariablesMap().entrySet()) {
                if (!Constants.Crm_contact_modulename.equals(varEntry.getKey())) {
                    String coldata = varEntry.getValue().toString();
                    if (!StringUtil.isNullOrEmpty(coldata)) {
                        tmpObj.put(varEntry.getKey(), coldata);
                    }
                }
            }
        } else {
            if(customDataMap.containsKey(obj.getContactid())) {
                CrmContactCustomData customDataobj = customDataMap.get(obj.getContactid());
                for (Entry<String, Integer> field : FieldMap.entrySet()) {
                    if (field.getValue() > 0) {
                        String coldata = customDataobj.getCol(field.getValue());
                        if (!StringUtil.isNullOrEmpty(coldata)) {
                            tmpObj.put(field.getKey(), coldata);
                        }
                    }
                }
            }
        }
    }

    protected void getCalculatedCustomColumnJSON(CrmContact obj, JSONObject tmpObj, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        if (exprVarMap.containsKey(obj.getContactid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getContactid());
            for (Entry<String, Object> varEntry : expVar.getOutputMap().entrySet()) {
                if (!Constants.Crm_contact_modulename.equals(varEntry.getKey())) {
                    tmpObj.put(varEntry.getKey(), varEntry.getValue());
                }
            }
        }
    }

    protected void setCustomColumnValues(CrmContact obj, HashMap<String, Integer> fieldMap, Map<String, String> replaceFieldMap,
            Map<String, Object> variableMap, Map<String, Object> variableMapForFormula, HashMap<String, CrmContactCustomData> customDataMap, boolean isExport, DateFormat dateFormat) {
        if (customDataMap.containsKey(obj.getContactid())) {
            CrmContactCustomData customData = customDataMap.get(obj.getContactid());
            for (Entry<String, Integer> field : fieldMap.entrySet()) {
                Integer colnumber = field.getValue();
                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
                    Integer isref = fieldMap.get(field.getKey() +"#"+ colnumber);
                    String coldata = null;
                    if (isref != null) {
                        try {
                            coldata = customData.getCol(colnumber);
                            if (!StringUtil.isNullOrEmpty(coldata)) {
                                if (coldata.length() > 1 && isExport) {
                                    if (isref == 1) {
                                        coldata = fieldDataManagercntrl.getMultiSelectColData(coldata);
                                    } else if (isref == 0) {
                                        coldata = customData.getRefCol(colnumber);
                                    }
                                }
                                variableMap.put(field.getKey(), coldata);
                                try {
                                    variableMapForFormula.put(replaceFieldMap.get(field.getKey()), Double.parseDouble(coldata));
                                } catch (Exception ex) {
                                    variableMapForFormula.put(replaceFieldMap.get(field.getKey()), 0);
                                }
                            }
                        } catch (IllegalArgumentException ex) {
                            ex.printStackTrace();
                        } catch (ObjectNotFoundException ex) {
                            ex.printStackTrace();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

	@Override
	public JSONObject updateMassContact(String companyId, String userId, String timeZoneDiff, String ipAddress, JSONObject jsonData) throws ServiceException, JSONException {
		JSONObject resultJsonObj = new JSONObject();
		CrmContact contact = null;
		resultJsonObj.put("success", false);
		String auditmsg = "";
		KwlReturnObject kmsg = null;
		String contactId = null;
		if (jsonData.has("contactid")) {
			contactId = jsonData.getString("contactid");
		} else {
			contactId = "0";
		}

		if (!StringUtil.isNullOrEmpty(userId))
			jsonData.put("userid", userId);
		if (!StringUtil.isNullOrEmpty(companyId))
			jsonData.put("companyid", companyId);
		if (!StringUtil.isNullOrEmpty(timeZoneDiff))
			jsonData.put("tzdiff", timeZoneDiff);

//		jsonData.put("updatedon", new Date().getTime());
		kmsg = crmContactDAOObj.updateMassContacts(jsonData);
		//contact = (CrmContact) kmsg.getEntityList().get(0);
//		if (jsonData.has("auditstr"))
//			auditmsg = jsonData.getString("auditstr");
//		auditmsg = auditmsg + " Contact - " + (StringUtil.checkForNull(contact.getFirstname()) + " " + StringUtil.checkForNull(contact.getLastname())).trim();
		//TODO how create audit log for multiple updates
		//createAuditLog(contact, auditmsg, AuditAction.CONTACT_UPDATE, ipAddress, userId, contactId);
		resultJsonObj.put("success", kmsg.isSuccessFlag());
		if (contact != null) {
			resultJsonObj.put("ID", contact.getContactid());
			resultJsonObj.put("createdon", contact.getCreatedOn());
		}
		return resultJsonObj;
	}

}
