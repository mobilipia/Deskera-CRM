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
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.wrapper.remoteApiJsonErrorCode;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.campaignModule.crmCampaignDAO;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.caseModule.crmCaseHandler;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.productModule.crmProductDAO;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class remoteApiJsonController extends MultiActionController {
    private SimpleDateFormat onlyDateFormat = new SimpleDateFormat(Constants.yyyyMMdd);
    private static final Log LOG = LogFactory.getLog(remoteApiJsonController.class);
    private crmLeadDAO crmLeadDAOObj;
    private crmCaseDAO crmCaseDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private crmProductDAO crmProductDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmContactDAO crmContactDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private permissionHandlerDAO permissionHandlerDAOObj;
    private crmCampaignDAO crmCampaignDAOObj;
    private String successView;
    private String userid = "99f1eb77-cac9-41bb-977b-c8bc17fb3daa";
    public String getSuccessView() {
        return successView;
    }

    public void setcrmCaseDAO(crmCaseDAO crmCaseDAOObj1) {
        this.crmCaseDAOObj = crmCaseDAOObj1;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }
    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }
    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }
    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }
    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }
    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }
    public void setcrmProductDAO(crmProductDAO crmProductDAOObj1) {
        this.crmProductDAOObj = crmProductDAOObj1;
    }
    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }
    public void setpermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
    }
    public void setcrmCampaignDAO(crmCampaignDAO crmCampaignDAOObj1) {
        this.crmCampaignDAOObj = crmCampaignDAOObj1;
    }


    /**
     * 
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ModelAndView getAllLeads(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jresponse  = new JSONObject();
        KwlReturnObject kmsg = null;
       try{
            int start =0;
            JSONObject errorObj  = new JSONObject();
            JSONObject result  = new JSONObject();
            JSONArray jarr = new JSONArray();
            // TODO - uncomment below line
//            String userid = request.getRemoteUser();
            User userObj = profileHandlerDAOObj.getUserObject(userid);
            if(userObj !=null) {
                String companyid = userObj.getCompany().getCompanyID();
                ArrayList filter_names = new ArrayList();
                ArrayList filter_params = new ArrayList();
                boolean heirarchyPerm = chkHeirarchyPermForModule(Constants.Crm_Lead_modulename,userid,companyid);
                if(!heirarchyPerm) {
                    StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
                    filter_names.add("INlo.usersByUserid.userID");
                    filter_params.add(usersList);
                }

                filter_names.add("c.deleteflag");
                filter_params.add(0);
                // TODO - is required
                filter_names.add("c.validflag");
                filter_params.add(1);
                filter_names.add("c.company.companyID");
                filter_params.add(companyid);
                filter_names.add("c.istransfered");
                filter_params.add("0");
    //            filter_names.add("c.isarchive");
    //            filter_params.add(false);
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                if(!StringUtil.isNullOrEmpty(request.getParameter("limit"))) {
                    requestParams.put("pagingFlag", true);
                    requestParams.put("start", start);
                    requestParams.put("limit", request.getParameter("limit"));
                }

                requestParams.put("filterQaulified", true);
                kmsg = crmLeadDAOObj.getAllLeads(requestParams, filter_names, filter_params);
                List<CrmLead> leadList = kmsg.getEntityList();
                if(leadList != null && !leadList.isEmpty()) {
                    for(CrmLead leadObj : leadList) {
                        JSONObject jtemp = new JSONObject();
                        jtemp.put("leadname", leadObj.getLastname());
                        jtemp.put("email", leadObj.getEmail());
                        jtemp.put("createdon", onlyDateFormat.format(leadObj.getCreatedOn()));
                        jtemp.put("status", StringUtil.isNullObject(leadObj.getCrmCombodataByLeadstatusid()) ? "" : leadObj.getCrmCombodataByLeadstatusid().getValue());
                        jtemp.put("source", StringUtil.isNullObject(leadObj.getCrmCombodataByLeadsourceid()) ? "" : leadObj.getCrmCombodataByLeadsourceid().getValue());
                        jarr.put(jtemp);
                    }
                }
                JSONObject jobj = new JSONObject();
                jobj.put("lead", jarr);
                JSONObject leads  = new JSONObject();
                leads.put("leads",jobj);
                result.put("result",leads);
                
            } else {
                errorObj = getErrorCodeJson(remoteApiJsonErrorCode.UserNotFoundCode);
                result.put("error",errorObj);
            }
            jresponse.put("response",result);
       } catch(Exception e) {
            LOG.info("Can't fetch getAllLeads:", e);
        }
        return new ModelAndView("jsonView", "model", jresponse.toString());
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ModelAndView getAllProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jresponse = new JSONObject();
       KwlReturnObject kmsg = null;
       try{
            JSONArray jarr = new JSONArray();
            JSONObject result  = new JSONObject();
            JSONObject errorObj  = new JSONObject();
            // TODO - uncomment below line
//            String userid = request.getRemoteUser();
            User userObj = profileHandlerDAOObj.getUserObject(userid);
            if(userObj !=null) {
                String companyid = userObj.getCompany().getCompanyID();
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("companyid", companyid);
//                if(request.getParameter("config") != null) {
                    requestParams.put("config", "true");// need to fetch only valid record
//                }

                requestParams.put("export", request.getParameter("reportid"));
                if(!StringUtil.isNullOrEmpty(request.getParameter("limit"))) {
                    requestParams.put("start", 0);
                    requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
                } else {
                    requestParams.put("export", "true");
                }
                boolean heirarchyPerm = chkHeirarchyPermForModule(Constants.Crm_Product_modulename,userid,companyid);
                requestParams.put("heirarchyPerm", heirarchyPerm);
                StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
                kmsg = crmProductDAOObj.getProducts(requestParams, usersList);
                List<CrmProduct> prodList = kmsg.getEntityList();
                if(prodList != null && !prodList.isEmpty()) {
                    for(CrmProduct prodObj : prodList) {
                        JSONObject jtemp = new JSONObject();
                        jtemp.put("productname", prodObj.getProductname());
                        jtemp.put("createdon", onlyDateFormat.format(prodObj.getCreatedOn()));
                        jarr.put(jtemp);
                    }
                }
                JSONObject jobj = new JSONObject();
                jobj.put("product", jarr);
                JSONObject opportunity  = new JSONObject();
                opportunity.put("products",jobj);
                result.put("result",opportunity);
            } else {
                errorObj = getErrorCodeJson(remoteApiJsonErrorCode.UserNotFoundCode);
                result.put("error",errorObj);
            }
            jresponse.put("response",result);
       } catch(Exception e) {
          LOG.info("Can't fetch getAllProducts:", e);
       }
       return new ModelAndView("jsonView", "model", jresponse.toString());
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ModelAndView getAllAccounts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jresponse  = new JSONObject();
        KwlReturnObject kmsg = null;
       try{
            int start =0;
            JSONArray jarr = new JSONArray();
            JSONObject result  = new JSONObject();
            JSONObject errorObj  = new JSONObject();
            // TODO - uncomment below line
//            String userid = request.getRemoteUser();
            User userObj = profileHandlerDAOObj.getUserObject(userid);
            if(userObj !=null) {
                String companyid = userObj.getCompany().getCompanyID();
                ArrayList filter_names = new ArrayList();
                ArrayList filter_params = new ArrayList();
                filter_names.add("c.deleteflag");
                filter_params.add(0);
                filter_names.add("c.validflag");
                filter_params.add(1);
                filter_names.add("c.company.companyID");
                filter_params.add(companyid);
    //            filter_names.add("c.isarchive");
    //            filter_params.add(false);

                boolean heirarchyPerm = chkHeirarchyPermForModule(Constants.Crm_Account_modulename,userid,companyid);
                if(!heirarchyPerm) {
                    StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
                    filter_names.add("INao.usersByUserid.userID");
                    filter_params.add(usersList);
                }

                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                if(!StringUtil.isNullOrEmpty(request.getParameter("limit"))) {
                    requestParams.put("pagingFlag", true);
                    requestParams.put("start", start);
                    requestParams.put("limit", request.getParameter("limit"));
                }
                kmsg = crmAccountDAOObj.getAllAccounts(requestParams, filter_names, filter_params);
                List<CrmAccount> accList = kmsg.getEntityList();
                if(accList != null && !accList.isEmpty()) {
                    for(CrmAccount accObj : accList) {
                        JSONObject jtemp = new JSONObject();
                        jtemp.put("accountname", accObj.getAccountname());
                        jtemp.put("createdon", onlyDateFormat.format(accObj.getCreatedon()));
                        jtemp.put("email", accObj.getEmail());
                        jtemp.put("type", StringUtil.isNullObject(accObj.getCrmCombodataByAccounttypeid()) ? "" : accObj.getCrmCombodataByAccounttypeid().getValue());
                        jtemp.put("website", accObj.getWebsite());
                        jarr.put(jtemp);
                    }
                }
                JSONObject jobj = new JSONObject();
                jobj.put("account", jarr);
                JSONObject accounts  = new JSONObject();
                accounts.put("accounts",jobj);
                result.put("result",accounts);
            } else {
                errorObj = getErrorCodeJson(remoteApiJsonErrorCode.UserNotFoundCode);
                result.put("error",errorObj);
            }
            jresponse.put("response",result);

       } catch(Exception e) {
           LOG.info("Can't fetch getAllAccounts:", e);
        }
        return new ModelAndView("jsonView", "model", jresponse.toString());
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ModelAndView getAllOpportunities(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jresponse = new JSONObject();
        KwlReturnObject kmsg = null;
       try{
            JSONArray jarr = new JSONArray();
            JSONObject result  = new JSONObject();
            JSONObject errorObj  = new JSONObject();
            // TODO - uncomment below line
//            String userid = request.getRemoteUser();
            int start =0;
            User userObj = profileHandlerDAOObj.getUserObject(userid);
            if(userObj !=null) {
                String companyid = userObj.getCompany().getCompanyID();
                boolean archive = Boolean.parseBoolean(request.getParameter("isarchive"));
                ArrayList filter_names = new ArrayList();
                ArrayList filter_params = new ArrayList();
    //            if(Integer.parseInt(request.getParameter("flag")) == 62) {
    //                filter_names.add("c.crmAccount.accountid");
    //                filter_params.add(request.getParameter("mapid"));
    //            }
                filter_names.add("c.isarchive");
                filter_params.add(archive);
                filter_names.add("c.company.companyID");
                filter_params.add(companyid);
                filter_names.add("c.deleteflag");
                filter_params.add(0);

                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                if(!StringUtil.isNullOrEmpty(request.getParameter("limit"))) {
                    requestParams.put("start", start);
                    requestParams.put("limit", request.getParameter("limit"));
                } else {
                    requestParams.put("export", "true");
                }

                StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);

                boolean heirarchyPerm = chkHeirarchyPermForModule(Constants.Crm_Opportunity_modulename,userid,companyid);
                requestParams.put("heirarchyPerm", heirarchyPerm);

                requestParams.put("export", request.getParameter("reportid"));
                requestParams.put("config", "true");// need to fetch only valid record
                kmsg = crmOpportunityDAOObj.getOpportunities(requestParams, usersList, filter_names, filter_params);
                List<CrmOpportunity> oppList = kmsg.getEntityList();
                if(oppList != null && !oppList.isEmpty()) {
                    for(CrmOpportunity crmOpp : oppList) {
                        JSONObject jtemp = new JSONObject();
                        jtemp.put("oppname", crmOpp.getOppname());
                        jtemp.put("accountname", StringUtil.isNullObject(crmOpp.getCrmAccount()) ? "" : crmOpp.getCrmAccount().getAccountname());
                        jtemp.put("createdon", onlyDateFormat.format(crmOpp.getCreatedOn()));
                        jtemp.put("closedon", crmOpp.getClosingdate());
                        jtemp.put("stage", StringUtil.isNullObject(crmOpp.getCrmCombodataByOppstageid()) ? "" : crmOpp.getCrmCombodataByOppstageid().getValue());
                        jtemp.put("source", StringUtil.isNullObject(crmOpp.getCrmCombodataByLeadsourceid()) ? "" : crmOpp.getCrmCombodataByLeadsourceid().getValue());
                        jarr.put(jtemp);
                    }
                }
                JSONObject jobj = new JSONObject();
                jobj.put("opportunity", jarr);
                JSONObject opportunity  = new JSONObject();
                opportunity.put("opportunities",jobj);
                result.put("result",opportunity);
            } else {
                errorObj = getErrorCodeJson(remoteApiJsonErrorCode.UserNotFoundCode);
                result.put("error",errorObj);
            }
            jresponse.put("response",result);
        } catch (Exception e) {
            LOG.info("Can't fetch getAllOpportunities:", e);
        }
        return new ModelAndView("jsonView", "model", jresponse.toString());
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ModelAndView getAllContacts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jresponse  = new JSONObject();
       try{
           JSONArray jarr = new JSONArray();
//            String relatedName_importContacts = request.getParameter("relatedName");
//            String recId_importContacts = request.getParameter("recId");
            JSONObject result  = new JSONObject();
            JSONObject errorObj  = new JSONObject();
            // TODO - uncomment below line
//            String userid = request.getRemoteUser();
            int start =0;
            User userObj = profileHandlerDAOObj.getUserObject(userid);
            if(userObj !=null) {
                String companyid = userObj.getCompany().getCompanyID();

                ArrayList filter_names = new ArrayList();
                ArrayList filter_params = new ArrayList();
                filter_names.add("c.deleteflag");
                filter_params.add(0);
                filter_names.add("c.validflag");
                filter_params.add(1);
                filter_names.add("c.company.companyID");
                filter_params.add(companyid);
    //            filter_names.add("c.isarchive");
    //            filter_params.add(false);

                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                if(!StringUtil.isNullOrEmpty(request.getParameter("limit"))) {
                    requestParams.put("pagingFlag", true);
                    requestParams.put("start", start);
                    requestParams.put("limit", request.getParameter("limit"));
                }

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_params);

                boolean heirarchyPerm = chkHeirarchyPermForModule(Constants.Crm_Contact_modulename,userid,companyid);
                if(!heirarchyPerm){
                    StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
                    filter_names.add("INco.usersByUserid.userID");filter_params.add(usersList);
                }
                kmsg = crmContactDAOObj.getAllContacts(requestParams);
                List<CrmContact> conmtactList = kmsg.getEntityList();
                if(conmtactList != null && !conmtactList.isEmpty()) {
                    for(CrmContact contactObj : conmtactList) {
                        JSONObject jtemp = new JSONObject();
                        jtemp.put("contactname", (StringUtil.sNull(contactObj.getFirstname()) + " " +  StringUtil.sNull(contactObj.getLastname())).trim());
                        jtemp.put("accountname", StringUtil.sNull(contactObj.getCrmAccount().getAccountname()));
                        jtemp.put("source", StringUtil.isNullObject(contactObj.getCrmCombodataByLeadsourceid()) ? "" : contactObj.getCrmCombodataByLeadsourceid().getValue());
                        jtemp.put("email", contactObj.getEmail());
                        jtemp.put("createdon", onlyDateFormat.format(contactObj.getCreatedon()));
                        jtemp.put("designation", contactObj.getTitle());
                        jarr.put(jtemp);
                    }
                }
                JSONObject jobj = new JSONObject();
                jobj.put("contact", jarr);
                JSONObject contact  = new JSONObject();
                contact.put("contacts",jobj);
                result.put("result",contact);
            } else {
                errorObj = getErrorCodeJson(remoteApiJsonErrorCode.UserNotFoundCode);
                result.put("error",errorObj);
            }
            jresponse.put("response",result);

       } catch(Exception e) {
            LOG.info("Can't fetch getAllContacts:", e);
        }
        return new ModelAndView("jsonView", "model", jresponse.toString());
    }

    /**
     * 
     * @param request
     * @param response
     * @return
     * @throws javax.servlet.ServletException
     */
    public ModelAndView getAllCases(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jresponse  = new JSONObject();
       try{
           JSONArray jarr = new JSONArray();
            JSONObject result  = new JSONObject();
            JSONObject errorObj  = new JSONObject();
            // TODO - uncomment below line
//            String userid = request.getRemoteUser();
            int start =0;
            User userObj = profileHandlerDAOObj.getUserObject(userid);
            if(userObj !=null) {
                String companyid = userObj.getCompany().getCompanyID();

                ArrayList filter_names = new ArrayList();
                ArrayList filter_params = new ArrayList();
                filter_names.add("c.deleteflag");
                filter_params.add(0);
                filter_names.add("c.company.companyID");
                filter_params.add(companyid);
    //            filter_names.add("c.isarchive");
    //            filter_params.add(false);
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                if(!StringUtil.isNullOrEmpty(request.getParameter("limit"))) {
                    requestParams.put("start", start);
                    requestParams.put("limit", request.getParameter("limit"));
                } else {
                    requestParams.put("export", "true");
                }

                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_params", filter_params);
                requestParams.put("config", "true");// need to fetch only valid record
                boolean heirarchyPerm = chkHeirarchyPermForModule(Constants.Crm_Case_modulename,userid,companyid);
                if(!heirarchyPerm){
                    StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
                    requestParams.put("userlist", usersList);
                    requestParams.put("heirarchyPerm", heirarchyPerm);
                }
                kmsg = crmCaseDAOObj.getCases(requestParams);
                List<CrmCase> caseList = kmsg.getEntityList();
                if(caseList != null && !caseList.isEmpty()) {
                    for(CrmCase caseObj : caseList) {
                        JSONObject jtemp = new JSONObject();
                        jtemp.put("casename", StringUtil.sNull(caseObj.getCasename()));
                        jtemp.put("subject", StringUtil.sNull(caseObj.getSubject()));
                        jtemp.put("status", StringUtil.isNullObject(caseObj.getCrmCombodataByCasestatusid())? "":caseObj.getCrmCombodataByCasestatusid().getValue());
                        jtemp.put("Priority", StringUtil.isNullObject(caseObj.getCrmCombodataByCasepriorityid())? "":caseObj.getCrmCombodataByCasepriorityid().getValue());
                        jtemp.put("createdon", caseObj.getCreatedon()!=null ? onlyDateFormat.format(caseObj.getCreatedon()) : "");
                        jtemp.put("accountname", StringUtil.isNullObject(caseObj.getCrmAccount()) ? "":caseObj.getCrmAccount().getAccountname());
                        jtemp.put("contactname", StringUtil.isNullObject(caseObj.getCrmContact()) ? "":(StringUtil.sNull(caseObj.getCrmContact().getFirstname()) + " " +  StringUtil.sNull(caseObj.getCrmContact().getLastname())).trim());
                        String[] productInfo=crmCaseHandler.getCaseProducts(crmCaseDAOObj, caseObj.getCaseid());
                        jtemp.put("productname", productInfo[2]);
                        jarr.put(jtemp);
                    }
                }
                JSONObject jobj = new JSONObject();
                jobj.put("case", jarr);
                JSONObject contact  = new JSONObject();
                contact.put("cases",jobj);
                result.put("result",contact);
            } else {
                errorObj = getErrorCodeJson(remoteApiJsonErrorCode.UserNotFoundCode);
                result.put("error",errorObj);
            }
            jresponse.put("response",result);

       } catch(Exception e) {
            LOG.info("Can't fetch getAllCases:", e);
        }
        return new ModelAndView("jsonView", "model", jresponse.toString());
    }

    public ModelAndView getCampaignByName(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jresponse  = new JSONObject();
       try{
           JSONArray jarr = new JSONArray();
            JSONObject result  = new JSONObject();
            JSONObject errorObj  = new JSONObject();
            // TODO - uncomment below line
//            String userid = request.getRemoteUser();
            int start =0;
            if(!StringUtil.isNullOrEmpty(request.getParameter("campaignname"))) {
                User userObj = profileHandlerDAOObj.getUserObject(userid);
                if(userObj !=null) {
                    String companyid = userObj.getCompany().getCompanyID();

                    ArrayList filter_names = new ArrayList();
                    ArrayList filter_params = new ArrayList();
        //            filter_names.add("c.isarchive");
        //            filter_params.add(false);

                    HashMap<String, Object> requestParams = new HashMap<String, Object>();
                    if(!StringUtil.isNullOrEmpty(request.getParameter("limit"))) {
                        requestParams.put("start", start);
                        requestParams.put("limit", request.getParameter("limit"));
                    } else {
                        requestParams.put("export", "true");
                    }
                    requestParams.put("ss", request.getParameter("campaignname"));
                    requestParams.put("companyid", companyid);
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_values", filter_params);

                    boolean heirarchyPerm = chkHeirarchyPermForModule(Constants.CRM_CAMPAIGN_MODULENAME,userid,companyid);
                    requestParams.put("heirarchyPerm", heirarchyPerm);
                    StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
                    kmsg = crmCampaignDAOObj.getCampaigns(requestParams, usersList);
                    List<CrmCampaign> campaignList = kmsg.getEntityList();
                    if(campaignList != null && !campaignList.isEmpty()) {
                        for(CrmCampaign campaignObj : campaignList) {
                            JSONObject jtemp = new JSONObject();
                            jtemp.put("campaignname", campaignObj.getCampaignname());
                            jtemp.put("objective", campaignObj.getObjective());
                            jtemp.put("type", StringUtil.isNullObject(campaignObj.getCrmCombodataByCampaigntypeid()) ? "" : campaignObj.getCrmCombodataByCampaigntypeid().getValue());
                            jtemp.put("status", StringUtil.isNullObject(campaignObj.getCrmCombodataByCampaignstatusid()) ? "" : campaignObj.getCrmCombodataByCampaignstatusid().getValue());
                            jtemp.put("createdon", onlyDateFormat.format(campaignObj.getCreatedon()));
                            jtemp.put("response", campaignObj.getExpectedresponse());
                            jarr.put(jtemp);
                        }
                    }
                    JSONObject jobj = new JSONObject();
                    jobj.put("campaigns", jarr);
                    JSONObject contact  = new JSONObject();
                    contact.put("campaign",jobj);
                    result.put("result",contact);
                } else {
                    errorObj = getErrorCodeJson(remoteApiJsonErrorCode.UserNotFoundCode);
                    result.put("error",errorObj);
                }
            } else {
                errorObj = getErrorCodeJson(remoteApiJsonErrorCode.CampaignNameMissing);
                result.put("error",errorObj);
            }
            jresponse.put("response",result);
       } catch(Exception e) {
            LOG.info("Can't fetch campaign name:", e);
        }
        return new ModelAndView("jsonView", "model", jresponse.toString());
    }
    /**
     * 
     * @param moduleName
     * @param userid
     * @param companyid
     * @return
     * @throws com.krawler.common.service.ServiceException
     * @throws com.krawler.utils.json.base.JSONException
     */
    private boolean chkHeirarchyPermForModule(String moduleName, String userid, String companyid) throws ServiceException,JSONException{
        boolean permsion=false;
        KwlReturnObject kmsg = null;
        try{
            JSONObject jsnObj = new JSONObject();
            Integer permissionCode = 0;
            CompanyPreferences cmpPref = (CompanyPreferences) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CompanyPreferences", companyid);
            jsnObj = crmManagerCommon.getCompanyPreferencesJSON(cmpPref, jsnObj);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid",userid);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("feature.featureName");
            filter_params.add(moduleName);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            kmsg = permissionHandlerDAOObj.getUserPermission(requestParams);
            List list = kmsg.getEntityList();
            if(list !=null && !list.isEmpty()) {
                Iterator ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    Object[] roww = (Object[]) ite.next();
                    long perl = 0;
                    perl = (Long) roww[1];
                    permissionCode = (int) perl;
                }
            }
            permsion = crmManagerCommon.chkHeirarchyPermFromJson(jsnObj,moduleName,permissionCode);
        } catch(Exception ex) {
            LOG.info("Error while checking heirarchy permission for module :"+moduleName + " and user :"+userid, ex);
        }
        return permsion;
    }

    private JSONObject getErrorCodeJson(Integer code) {
        JSONObject errorObj  = new JSONObject();
        try {
            switch(code) {
                case 1001 :
                    errorObj.put("code", code);
                    errorObj.put("message", "User not exist");
                    break;
                case 1002 :
                    errorObj.put("code", code);
                    errorObj.put("message", "Campaignname missing");
                    break;

            }
        } catch(JSONException ex) {
            LOG.info("Error while generating error code ", ex);
        } finally{
            return errorObj;
        }
    }
}
