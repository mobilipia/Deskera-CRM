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

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.esp.Search.SearchBean;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.utils.json.base.JSONException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.List;
import com.krawler.crm.utils.Constants;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.accountModule.crmAccountHandler;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.contactModule.crmContactHandler;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.leadModule.crmLeadHandler;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityHandler;
import com.krawler.spring.documents.DocumentConstants;
import com.krawler.spring.documents.DocumentHelper;
import com.krawler.spring.profileHandler.profileHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author Karthik
 */
public class globalSearchController extends MultiActionController {

    private globalSearchDAO globalSearchDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private String successView;
    private crmManagerDAO crmManagerDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmCaseDAO crmCaseDAOObj;
    private crmLeadDAO crmLeadDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmContactDAO crmContactDAOObj;
    private DocumentHelper documentHeplerObj;

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public void setglobalSearchDAO(globalSearchDAO globalSearchDAOObj1) {
        this.globalSearchDAOObj = globalSearchDAOObj1;
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

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }

    public void setcrmCaseDAO(crmCaseDAO crmCaseDAOObj1) {
        this.crmCaseDAOObj = crmCaseDAOObj1;
    }
    
    public void setDocumentHelper(DocumentHelper documentHeplerObj) {
        this.documentHeplerObj = documentHeplerObj;
    }
    public JSONObject globalSearchJson(List ll, HttpServletRequest request, int totalSize, String type) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            if (type.equals("user")) {
                while (ite.hasNext()) {
                    User obj = (User) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("id", obj.getUserID());
                    tmpObj.put("name", obj.getFirstName() + " " + obj.getLastName());
                    tmpObj.put("img", "owners-window.jpg");
                    tmpObj.put("moduleName", "User");
                    tmpObj.put("createdon", (obj.getCreatedon() == null)?"":obj.getCreatedon());
                    jarr.put(tmpObj);
                }
            } else if (type.equals("cam")) {
                String classname = "CrmCampaign";
                jarr = getModuleJson(ll, classname);
            } else if (type.equals("acc")) {
                String classname = "CrmAccount";
                jarr = getModuleJson(ll, classname);
            } else if (type.equals("opp")) {
                String classname = "CrmOpportunity";
                jarr = getModuleJson(ll, classname);
            } else if (type.equals("lea")) {
                String classname = "CrmLead";
                jarr = getModuleJson(ll, classname);
            } else if (type.equals("con")) {
                String classname = "CrmContact";
                jarr = getModuleJson(ll, classname);
            } else if (type.equals("cas")) {
                String classname = "CrmCase";
                jarr = getModuleJson(ll, classname);
            } else if (type.equals("pro")) {
                String classname = "CrmProduct";
                jarr = getModuleJson(ll, classname);
            } else if (type.equals("docs")) {
                String classname = "Docs";
                jarr = getModuleJson(ll, classname);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }

    public JSONArray getModuleJson(List ll, String classname) throws ServiceException {
        JSONArray jarr = new JSONArray();
        try {
            String className = ConfigReader.getinstance().get("CrmClassPath") + classname;
            if(StringUtil.equal("Docs", classname)){
                className = ConfigReader.getinstance().get("DocsClassPath") + classname;
            }
            Class cl = Class.forName(className);
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Object invoker = (Object) ite.next();
                JSONObject tmpObj = new JSONObject();
                if (invoker != null) {
                    java.lang.reflect.Method objMethod;
                    if (classname.equals("CrmCampaign")) {
                        objMethod = cl.getMethod("getCampaignid");
                        Object Id = objMethod.invoke(invoker);
                        tmpObj.put("id", Id);
                        objMethod = cl.getMethod("getCampaignname");
                        Object name = objMethod.invoke(invoker);
                        tmpObj.put("name", name);
                        tmpObj.put("img", "Campaigns.gif");
                        tmpObj.put("moduleName", Constants.MODULE_Campaign);
                        objMethod = cl.getMethod("getUsersByUserid");
                        User ownerid = (User) objMethod.invoke(invoker);
                        tmpObj.put("owners",profileHandler.getUserFullName(ownerid) );
                        objMethod = cl.getMethod("getCreatedon");
                        Object createdOn = objMethod.invoke(invoker);
                        tmpObj.put("createdon", (createdOn == null)?"":createdOn);
                        jarr.put(tmpObj);
                    } else if (classname.equals("CrmAccount")) {
                        objMethod = cl.getMethod("getAccountid");
                        Object Id = objMethod.invoke(invoker);
                        tmpObj.put("id", Id);
                        objMethod = cl.getMethod("getAccountname");
                        Object name = objMethod.invoke(invoker);
                        tmpObj.put("name", name);
                        tmpObj.put("moduleName", Constants.MODULE_ACCOUNT);
                        tmpObj.put("img", "accounts.gif");
                        objMethod = cl.getMethod("getCreatedon");
                        Object createdOn = objMethod.invoke(invoker);
                        tmpObj.put("createdon", (createdOn == null)?"":createdOn);
                        tmpObj.put("owners", crmAccountHandler.getAllAccOwners(crmAccountDAOObj,Id.toString())[5]);
                        jarr.put(tmpObj);
                    } else if (classname.equals("CrmOpportunity")) {
                        objMethod = cl.getMethod("getOppid");
                        Object Id = objMethod.invoke(invoker);
                        tmpObj.put("id", Id);
                        objMethod = cl.getMethod("getOppname");
                        Object name = objMethod.invoke(invoker);
                        tmpObj.put("name", name);
                        tmpObj.put("img", "opportunity1.gif");
                        tmpObj.put("moduleName", Constants.MODULE_OPPORTUNITY);
                        objMethod = cl.getMethod("getCreatedon");
                        Object createdOn = objMethod.invoke(invoker);
                        tmpObj.put("createdon", (createdOn == null)?"":createdOn);
                        tmpObj.put("owners", crmOpportunityHandler.getAllOppOwners(crmOpportunityDAOObj, Id.toString())[5]);
                        jarr.put(tmpObj);
                    } else if (classname.equals("CrmLead")) {
                        String fullName = "";
                        objMethod = cl.getMethod("getLeadid");
                        Object Id = objMethod.invoke(invoker);
                        tmpObj.put("id", Id);
                        objMethod = cl.getMethod("getLastname");
                        Object lastName = objMethod.invoke(invoker);
                        fullName = lastName.toString();
                        tmpObj.put("name", fullName);
                        tmpObj.put("img", "leads.gif");
                        tmpObj.put("moduleName", Constants.MODULE_LEAD);
                        objMethod = cl.getMethod("getCreatedon");
                        Object createdOn = objMethod.invoke(invoker);
                        tmpObj.put("createdon", (createdOn == null)?"":createdOn);
                        tmpObj.put("owners", crmLeadHandler.getAllLeadOwners(crmLeadDAOObj, Id.toString())[5]);
                        jarr.put(tmpObj);
                    } else if (classname.equals("CrmContact")) {
                        String fullName = "";
                        objMethod = cl.getMethod("getContactid");
                        Object Id = objMethod.invoke(invoker);
                        tmpObj.put("id", Id);
                        objMethod = cl.getMethod("getFirstname");
                        Object firstName = objMethod.invoke(invoker);
                        objMethod = cl.getMethod("getLastname");
                        Object lastName = objMethod.invoke(invoker);
                        fullName =( ((firstName == null) ? "":firstName.toString()) + " " + ((lastName == null) ? "":lastName.toString())).trim();
                        tmpObj.put("name", fullName);
                        tmpObj.put("img", "contacts3.gif");
                        tmpObj.put("moduleName", Constants.MODULE_CONTACT);
                        objMethod = cl.getMethod("getCreatedon");
                        Object createdOn = objMethod.invoke(invoker);
                        tmpObj.put("createdon", (createdOn == null)?"":createdOn);
                        tmpObj.put("owners", crmContactHandler.getAllContactOwners(crmContactDAOObj, Id.toString())[5]);
                        jarr.put(tmpObj);
                    } else if (classname.equals("CrmCase")) {
                        objMethod = cl.getMethod("getCaseid");
                        Object Id = objMethod.invoke(invoker);
                        tmpObj.put("id", Id);
                        objMethod = cl.getMethod("getSubject");
                        Object name = objMethod.invoke(invoker);
                        tmpObj.put("name", name);
                        tmpObj.put("img", "cases.gif");
                        tmpObj.put("moduleName", Constants.MODULE_CASE);
                        objMethod = cl.getMethod("getCreatedon");
                        Object createdOn = objMethod.invoke(invoker);
                        tmpObj.put("createdon", (createdOn == null)?"":createdOn);
                        objMethod = cl.getMethod("getUsersByUserid");
                        User ownerid = (User) objMethod.invoke(invoker);
                        tmpObj.put("owners",profileHandler.getUserFullName(ownerid) );
                        jarr.put(tmpObj);
                    } else if (classname.equals("CrmProduct")) {
                        objMethod = cl.getMethod("getProductid");
                        Object Id = objMethod.invoke(invoker);
                        tmpObj.put("id", Id);
                        objMethod = cl.getMethod("getProductname");
                        Object name = objMethod.invoke(invoker);
                        tmpObj.put("name", name);
                        tmpObj.put("img", "Products.gif");
                        tmpObj.put("moduleName", Constants.MODULE_PRODUCT);
                        objMethod = cl.getMethod("getCreatedon");
                        Object createdOn = objMethod.invoke(invoker);
                        tmpObj.put("createdon", (createdOn == null)?"":createdOn);
                        objMethod = cl.getMethod("getUsersByUserid");
                        User ownerid = (User) objMethod.invoke(invoker);
                        tmpObj.put("owners",profileHandler.getUserFullName(ownerid) );
                        jarr.put(tmpObj);
                    } else if (classname.equals("Docs")) {
                        objMethod = cl.getMethod("getDocid");
                        Object Id = objMethod.invoke(invoker);
                        tmpObj.put(DocumentConstants.JSON_docid, Id);
                        objMethod = cl.getMethod("getDocname");
                        Object name = objMethod.invoke(invoker);
                        tmpObj.put(DocumentConstants.JSON_name, name);

                        String imgpath = documentHeplerObj.getDocImg(name.toString());
                        tmpObj.put(DocumentConstants.JSON_fileimage, imgpath);
                        objMethod = cl.getMethod("getDocsize");
                        Object docsize = objMethod.invoke(invoker);
                        tmpObj.put(DocumentConstants.JSON_size, StringUtil.sizeRenderer(docsize.toString()));
                        objMethod = cl.getMethod("getTags");
                        Object tags = objMethod.invoke(invoker);
                        tmpObj.put(DocumentConstants.JSON_Tags, tags);

                        tmpObj.put("moduleName", Constants.MODULE_DOCUMENTS);
                        objMethod = cl.getMethod("getUserid");
                        User ownerid = (User) objMethod.invoke(invoker);
                        tmpObj.put(DocumentConstants.JSON_author, profileHandler.getUserFullName(ownerid) );
                        objMethod = cl.getMethod("getUploadedon");
                        Object createdOn = objMethod.invoke(invoker);
                        tmpObj.put(DocumentConstants.JSON_uploadeddate, (createdOn == null)?"":createdOn);
                        jarr.put(tmpObj);
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("globalSearchDAOImpl.getModuleJson : " + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("globalSearchDAOImpl.getModuleJson : " + ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("globalSearchDAOImpl.getModuleJson : " + ex.getMessage(), ex);
        } catch (ClassNotFoundException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("globalSearchDAOImpl.getModuleJson : " + ex.getMessage(), ex);
        } catch (NoSuchMethodException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("globalSearchDAOImpl.getModuleJson : " + ex.getMessage(), ex);
        } catch (SecurityException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("globalSearchDAOImpl.getModuleJson : " + ex.getMessage(), ex);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("globalSearchDAOImpl.getModuleJson : " + ex.getMessage(), ex);
        }
        return jarr;
    }

    public ModelAndView globalQuickSearch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            SearchBean bean = new SearchBean();
            String querytxt = request.getParameter("keyword");
            String type = request.getParameter("type");
			String userid = sessionHandlerImpl.getUserid(request);
            String companyid=sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
//(Kuldeep Singh)  Fix me : docs name search on basis of their summary by lucene search
//            if(type.equalsIgnoreCase("docs")){
//                kmsg = globalSearchDAOObj.searchIndex(bean, querytxt,
//                        request.getParameter("numhits"), request
//                                .getParameter("perpage"), request
//                                .getParameter("start"), companyid, userid, dateFmt);
//                jobj= (JSONObject) kmsg.getEntityList().get(0);
//            }
            if(type.equalsIgnoreCase("all")){
                jobj = AllSearchData(request, bean,type,querytxt, companyid, userid);
            }else{
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("type", StringUtil.checkForNull(type));
                requestParams.put("keyword", StringUtil.checkForNull(querytxt));
                requestParams.put("companyid", companyid);
                requestParams.put("userid", userid);
                
                if(StringUtil.equal(type,"cam")){
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_Campaign);
                    if(!heirarchyPerm){
                        requestParams.put("usersList", usersList);
                    }
                } else if(StringUtil.equal(type,"lea")){
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_LEAD);
                    if(!heirarchyPerm){
                        requestParams.put("usersList", usersList);
                    }
                } else if(StringUtil.equal(type,"acc")){
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_ACCOUNT);
                    if(!heirarchyPerm){
                        requestParams.put("usersList", usersList);
                    }
                } else if(StringUtil.equal(type,"con")){
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_CONTACT);
                    if(!heirarchyPerm){
                        requestParams.put("usersList", usersList);
                    }
                } else if(StringUtil.equal(type,"opp")){
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_OPPORTUNITY);
                    if(!heirarchyPerm){
                        requestParams.put("usersList", usersList);
                    }
                } else if(StringUtil.equal(type,"cas")){
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_CASE);
                    if(!heirarchyPerm){
                        requestParams.put("usersList", usersList);
                    }
                } else if(StringUtil.equal(type,"pro")){
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_PRODUCT);
                    if(!heirarchyPerm){
                        requestParams.put("usersList", usersList);
                    }
                } else {
                    requestParams.put("usersList", usersList);
                }

                kmsg = globalSearchDAOObj.globalQuickSearch(requestParams);
                jobj = globalSearchJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount(), request.getParameter("type"));

                if(StringUtil.equal(type,"docs")){
                    //Append document content search result
                    JSONArray docs = jobj.getJSONArray("data");
                    JSONArray docContents = getDocContentSearch(querytxt, companyid, userid);
                    for(int i=0; i<docContents.length(); i++){
                       docs.put(docContents.getJSONObject(i));
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject AllSearchData(HttpServletRequest request, SearchBean bean, String type,String querytxt,
        String companyid,String userid) throws IOException, ServiceException {

        JSONObject jObj = new JSONObject();
        JSONArray allsearchobj = new JSONArray();
        KwlReturnObject kmsg = null;
		try {
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("keyword", StringUtil.checkForNull(querytxt));
            requestParams.put("companyid", companyid);
            requestParams.put("userid", userid);
            
            requestParams.put("type", "user");
            requestParams.put("usersList", usersList);
            kmsg = globalSearchDAOObj.globalQuickSearch(requestParams);
            jObj = globalSearchJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount(), "user");
            JSONArray user = jObj.getJSONArray("data");

            requestParams.put("type", "con");// Contact
            if(!crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_CONTACT)) { // check heirarchy permission for contact
                requestParams.put("usersList", usersList);
            } else{
                requestParams.put("usersList", null);
            }
            kmsg = globalSearchDAOObj.globalQuickSearch(requestParams);
            jObj = globalSearchJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount(), "con");
            JSONArray contact = jObj.getJSONArray("data");

            requestParams.put("type", "acc");
            if(!crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_ACCOUNT)) { // check heirarchy permission for account
                requestParams.put("usersList", usersList);
            } else{
                requestParams.put("usersList", null);
            }
            kmsg = globalSearchDAOObj.globalQuickSearch(requestParams);
            jObj = globalSearchJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount(), "acc");
            JSONArray account = jObj.getJSONArray("data");

            requestParams.put("type", "cam");
            if(!crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_Campaign)) { // check heirarchy permission for campaign
                requestParams.put("usersList", usersList);
            } else{
                requestParams.put("usersList", null);
            }
            kmsg = globalSearchDAOObj.globalQuickSearch(requestParams);
            jObj = globalSearchJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount(), "cam");
            JSONArray campaign = jObj.getJSONArray("data");

            requestParams.put("type", "lea");
            if(!crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_LEAD)) { // check heirarchy permission for lead
                requestParams.put("usersList", usersList);
            } else{
                requestParams.put("usersList", null);
            }
            kmsg = globalSearchDAOObj.globalQuickSearch(requestParams);
            jObj = globalSearchJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount(), "lea");
            JSONArray lead = jObj.getJSONArray("data");

            requestParams.put("type", "pro");
            if(!crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_PRODUCT)) { // check heirarchy permission for product
                requestParams.put("usersList", usersList);
            } else{
                requestParams.put("usersList", null);
            }
            kmsg = globalSearchDAOObj.globalQuickSearch(requestParams);
            jObj = globalSearchJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount(), "pro");
            JSONArray product = jObj.getJSONArray("data");

            requestParams.put("type", "opp");
            if(!crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_OPPORTUNITY)) { // check heirarchy permission for opportunity
                requestParams.put("usersList", usersList);
            } else{
                requestParams.put("usersList", null);
            }
            kmsg = globalSearchDAOObj.globalQuickSearch(requestParams);
            jObj = globalSearchJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount(), "opp");
            JSONArray opportunity = jObj.getJSONArray("data");

            requestParams.put("type", "cas");
            if(!crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_CASE)) { // check heirarchy permission for case
                requestParams.put("usersList", usersList);
            } else{
                requestParams.put("usersList", null);
            }
            kmsg = globalSearchDAOObj.globalQuickSearch(requestParams);
            jObj = globalSearchJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount(), "cas");
            JSONArray casearr = jObj.getJSONArray("data");
            //JSONArray opportunity = SearchHandler.searchOpportunity(session, querytxt, companyid,userid);
            //JSONArray casearr = SearchHandler.searchCase(session, querytxt, companyid,userid);
            //JSONArray product = SearchHandler.searchProduct(session, querytxt, companyid,userid);
            //JSONArray lead = SearchHandler.searchLead(session, querytxt, companyid,userid);
//opportunity.length() > 0 || casearr.length() > 0 || product.length() > 0 || lead.length() > 0 ||

            JSONArray docContents = getDocContentSearch(querytxt, companyid, userid);
            
            requestParams.put("type", "docs");
            requestParams.put("usersList", usersList);
            kmsg = globalSearchDAOObj.globalQuickSearch(requestParams);
            jObj = globalSearchJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount(), "docs");
            JSONArray docs=jObj.getJSONArray("data");
            if(user.length() > 0 || contact.length() > 0 || lead.length() > 0 || account.length() > 0 ||  docs.length() > 0 ||  docContents.length() > 0 ||  campaign.length() > 0 || product.length() > 0 || casearr.length() > 0 || opportunity.length() > 0){
                jObj = new JSONObject();
                jObj.put("docs", docs);
                jObj.put("docContents", docContents);
                jObj.put("contact", contact);
                jObj.put("account", account);
                jObj.put("campaign", campaign);
                jObj.put("lead", lead);
                jObj.put("opportunity", opportunity);
                jObj.put("product", product);
                jObj.put("casearr", casearr);
                jObj.put("user", user);
                allsearchobj.put(jObj);
            }
            jObj=new JSONObject().put("data", allsearchobj);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
             throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
		return jObj;
	}

    public JSONArray getDocContentSearch(String querytxt, String companyid, String userid){
        JSONArray docContents = new JSONArray();
        JSONObject jObj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> filterParams = new HashMap<String, Object>();
            filterParams.put("ss", querytxt);
            filterParams.put("companyid", companyid);
            StringBuffer usersListIds = crmManagerDAOObj.recursiveUserIds(userid);
            filterParams.put("usersListIds", usersListIds);

            kmsg = documentHeplerObj.documentIndexSearch(filterParams);
            jObj= (JSONObject) kmsg.getEntityList().get(0);
            docContents = jObj.getJSONArray("data");
        } catch(Exception ex){
            Logger.getLogger(globalSearchController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return docContents;
    }
}
