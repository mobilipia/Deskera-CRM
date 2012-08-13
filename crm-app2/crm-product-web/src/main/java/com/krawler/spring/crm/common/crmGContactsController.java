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
package com.krawler.spring.crm.common;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.SystemUtil;
import com.krawler.crm.contact.bizservice.ContactManagementService;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.TargetModule;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.gcontacts.ContactsExample;
import com.krawler.crm.utils.AuditAction;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.companyDetails.companyDetailsDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.leadModule.crmLeadHandler;
import com.krawler.spring.crm.targetModule.crmTargetDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class crmGContactsController extends MultiActionController {
    private String successView;
    private sessionHandlerImpl sessionHandlerImplObj;
    private HibernateTransactionManager txnManager;
    private crmLeadDAO crmLeadDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmTargetDAO crmTargetDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private crmCommonDAO crmCommonDAOObj;
    private ContactManagementService contactManagementService;

    public void setContactManagementService(ContactManagementService contactManagementService) {
        this.contactManagementService = contactManagementService;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }


    public void setcrmTargetDAO(crmTargetDAO crmTargetDAOObj1) {
        this.crmTargetDAOObj = crmTargetDAOObj1;
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

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public ModelAndView saveGoogleContacts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try{
            String moduleName = request.getParameter("moduleName");
            String jsondata = request.getParameter("jsondata");
            JSONArray jarr = new JSONArray("["+ jsondata +"]" );
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobj2 = jarr.getJSONObject(i);
                if(StringUtil.equal(moduleName, "Lead")){
                    jobj = savegoogleLeads(request, jobj2);
                } else if(StringUtil.equal(moduleName, "Contact")){
                    jobj = savegoogleContacts(request, jobj2);
                } else if(StringUtil.equal(moduleName, "Target")){
                    jobj = savegoogleTargets(request, jobj2);
                }
            }
            jobj.put("success", true);
            txnManager.commit(status);
       } catch(Exception e) {
          txnManager.rollback(status);
          logger.warn(e.getMessage(),e);
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject savegoogleLeads(HttpServletRequest request, JSONObject jobj2) throws Exception {
        JSONObject jobj1 = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String googleEmail =request.getParameter("username");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String fname=jobj2.getString("firstName");
            String lname=jobj2.getString("lastName");
            lname = getLeadFullName(fname ,lname);

            String id = java.util.UUID.randomUUID().toString();
            jobj1.put("leadid", id);
            jobj1.put("userid", userid);
            jobj1.put("companyid", companyid);
            jobj1.put("type", crmLeadHandler.getDefaultLeadType(request));

            jobj1.put("lastname", lname);
            jobj1.put("email", jobj2.getString("email"));
            jobj1.put("phone", jobj2.getString("phone"));
            jobj1.put("street", jobj2.getString("address"));
            jobj1.put("istransfered", "0");
            jobj1.put("isconverted", "0");
            jobj1.put("leadownerid", userid);
            jobj1.put("updatedon", new Date());
            jobj1.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
//            jobj1.put("createdon", new Date());

            kmsg = crmLeadDAOObj.addLeads(jobj1);
            CrmLead lead = (CrmLead) kmsg.getEntityList().get(0);

            //Validate record
            crmCommonDAOObj.validaterecord(Constants.MODULE_LEAD, id, companyid);

            String auditTrail =lead.getLastname() + " - Lead imported from Google Account ( "+googleEmail+" )";
            if(StringUtil.isNullOrEmpty(lead.getLastname())){
                auditTrail = lead.getEmail() + " - Lead imported from Google Account ( "+googleEmail+" )";
            }
            auditTrailDAOObj.insertAuditLog(AuditAction.LEAD_IMPORT,auditTrail,request, id);

            jobj1 = new JSONObject();
            jobj1.put("success", true);

        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            jobj1.put("success", false);
            throw ServiceException.FAILURE("crmCommonController.savegoogleLeads", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            jobj1.put("success", false);
            throw ServiceException.FAILURE("crmCommonController.savegoogleLeads", e);
        } finally{
            return jobj1;
        }
    }

    public JSONObject savegoogleContacts(HttpServletRequest request, JSONObject jobj1) throws Exception {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String account = request.getParameter("account");
            String leadid = request.getParameter("mapid");
            String googleEmail =request.getParameter("username");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            String ipAddress = SystemUtil.getIpAddress(request);
            String firstName = jobj1.getString("firstName");
            String lastName = jobj1.getString("lastName");
            String email = jobj1.getString("email");
            jobj.put("firstname", firstName);
            jobj.put("lastname", lastName);
            jobj.put("email", email);
            jobj.put("phone", jobj1.getString("phone"));
            jobj.put("street", jobj1.getString("address"));
            jobj.put("accountid", account);
            if(!StringUtil.isNullOrEmpty(leadid)){
                jobj.put("leadid", leadid);
            }
            jobj.put("contactownerid", userid);
            jobj.put("updatedon", new Date());

            JSONObject contactResponse = contactManagementService.saveContact(companyid, userid, timeZoneDiff, ipAddress, jobj);
            String id = contactResponse.getString("ID");

            //Validate record
            crmCommonDAOObj.validaterecord(Constants.MODULE_CONTACT, id, companyid);

            String contactName = (StringUtil.isNullOrEmpty(firstName)+" "+StringUtil.isNullOrEmpty(lastName)).trim();
            String auditTrail =  contactName+ " - Contact imported from Google Account ( "+googleEmail+" )";
            if(StringUtil.isNullOrEmpty(contactName)){
                auditTrail = email + " - Contact imported from Google Account ( "+googleEmail+" )";
            }
            auditTrailDAOObj.insertAuditLog(AuditAction.CONTACT_IMPORT,auditTrail,request, id);

            jobj = new JSONObject();
            jobj.put("success", true);

        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            jobj.put("success", false);
            throw ServiceException.FAILURE("crmCommonController.savegoogleContacts", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            jobj.put("success", false);
            throw ServiceException.FAILURE("crmCommonController.savegoogleContacts", e);
        } finally{
            return jobj;
        }
    }

    public JSONObject savegoogleTargets(HttpServletRequest request, JSONObject jobj1) throws Exception {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String googleEmail =request.getParameter("username");
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String userid = sessionHandlerImplObj.getUserid(request);

            String id = java.util.UUID.randomUUID().toString();
            jobj.put("userid", userid);
            jobj.put("companyid", companyid);
            jobj.put("targetModuleid", id);
            jobj.put("firstname", jobj1.getString("firstName"));
            jobj.put("lastname", jobj1.getString("lastName"));
            jobj.put("email", jobj1.getString("email"));
            jobj.put("phone", jobj1.getString("phone"));
            jobj.put("address", jobj1.getString("address"));
            if(!StringUtil.isNullOrEmpty(jobj1.getString("lastName"))){
                jobj.put("validflag", "1");
            } else {
                jobj.put("validflag", "0");
            }
            jobj.put("targetModuleownerid", userid);
            jobj.put("updatedon", new Date());
            jobj.put("createdon", new Date());

            kmsg = crmTargetDAOObj.addTargets(jobj);
            TargetModule crmTarget = (TargetModule) kmsg.getEntityList().get(0);
            String auditTrail = crmTarget.getFirstname() + " " + crmTarget.getLastname() + " - Target imported from Google Account ( "+googleEmail+" )";
            if(StringUtil.isNullOrEmpty(crmTarget.getFirstname())&&StringUtil.isNullOrEmpty(crmTarget.getLastname())){
                auditTrail = crmTarget.getEmail()+" - Target imported from Google Account ( "+googleEmail+" )";
            }
            auditTrailDAOObj.insertAuditLog(AuditAction.TARGET_IMPORT,auditTrail,request, id);

            jobj = new JSONObject();
            jobj.put("success", true);

        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            jobj.put("success", false);
            throw ServiceException.FAILURE("crmCommonController.savegoogleTargets", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            jobj.put("success", false);
            throw ServiceException.FAILURE("crmCommonController.savegoogleTargets", e);
        } finally{
            return jobj;
        }
    }

    public ModelAndView getGoogleContacts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jObj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String jsondata= request.getParameter("jsondata");
            JSONArray jarr = new JSONArray("[" + jsondata + "]");
            JSONObject jobj = jarr.getJSONObject(0);
            String uname=jobj.getString("username");
            String password=jobj.getString("password");
            if(!(StringUtil.isNullOrEmpty(uname)||StringUtil.isNullOrEmpty(password)))
                jObj = ContactsExample.getGoogleContacts(uname,password);
            if(Boolean.parseBoolean(request.getParameter("importAll"))){
                jObj =  importAllgoogleContacts(request,jObj);
            }
            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jObj.toString());
    }

    public JSONObject importAllgoogleContacts(HttpServletRequest request,JSONObject jobj) throws ServiceException, JSONException {
        JSONObject jObjRet = new JSONObject();
        try {
            String moduleName = request.getParameter("moduleName");
            for (int i = 0; i < jobj.getJSONArray("data").length(); i++) {
                JSONObject jobj1 = jobj.getJSONArray("data").getJSONObject(i);
                if(StringUtil.equal(moduleName, "Lead")){
                    jObjRet = savegoogleLeads(request,jobj1);
                } else if(StringUtil.equal(moduleName, "Contact")){
                    jObjRet = savegoogleContacts(request,jobj1);
                } else if(StringUtil.equal(moduleName, "Target")){
                    jObjRet = savegoogleTargets(request,jobj1);
                }
            }
            jObjRet.put("success", true);
            jObjRet.put("importAll", true);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            jObjRet.put("success", false);
            throw ServiceException.FAILURE("crmCommonController.savegoogleContacts", ex);
        } finally {
            return jObjRet;
        }
    }

    public ModelAndView getAllAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
       try{
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            String companyid = sessionHandlerImplObj.getCompanyid(request);
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
            jobj.put("totalCount", kmsg.getRecordTotalCount());
       } catch(Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public static String getLeadFullName(String fname ,String lname) throws Exception {
        String fullname = lname;
        if(!StringUtil.isNullOrEmpty(fname))
                fullname = fname+" "+lname;
        return fullname;
    }
}
