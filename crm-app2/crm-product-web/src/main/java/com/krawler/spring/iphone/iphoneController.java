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
package com.krawler.spring.iphone;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.krawler.common.admin.FieldComboData;
import com.krawler.common.admin.FieldParams;
import com.krawler.common.admin.ProjectFeature;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.SystemUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.contact.bizservice.ContactManagementService;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.TargetModule;
import com.krawler.crm.database.tables.iDeskeraCrmAuth;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.utils.AuditAction;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.FieldConstants;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.authHandler.authHandlerController;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountController;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.accountModule.crmAccountHandler;
import com.krawler.spring.crm.accountModule.crmAccountReportController;
import com.krawler.spring.crm.accountModule.crmAccountReportDAO;
import com.krawler.spring.crm.activityModule.crmActivityReportController;
import com.krawler.spring.crm.activityModule.crmActivityReportDAO;
import com.krawler.spring.crm.campaignModule.crmCampaignDAO;
import com.krawler.spring.crm.campaignModule.crmCampaignReportController;
import com.krawler.spring.crm.campaignModule.crmCampaignReportDAO;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.caseModule.crmCaseReportController;
import com.krawler.spring.crm.caseModule.crmCaseReportDAO;
import com.krawler.spring.crm.common.crmManagerController;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.crm.contactModule.crmContactController;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.contactModule.crmContactHandler;
import com.krawler.spring.crm.contactModule.crmContactReportController;
import com.krawler.spring.crm.contactModule.crmContactReportDAO;
import com.krawler.spring.crm.dashboard.CrmDashboardController;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.leadModule.crmLeadHandler;
import com.krawler.spring.crm.leadModule.crmLeadReportController;
import com.krawler.spring.crm.leadModule.crmLeadReportDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityController;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityHandler;
import com.krawler.spring.crm.opportunityModule.crmOpportunityReportController;
import com.krawler.spring.crm.opportunityModule.crmOpportunityReportDAO;
import com.krawler.spring.crm.productModule.crmProductController;
import com.krawler.spring.crm.productModule.crmProductDAO;
import com.krawler.spring.crm.targetModule.crmTargetReportController;
import com.krawler.spring.crm.targetModule.crmTargetReportDAO;
import com.krawler.spring.crm.userModule.crmUserController;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.service.IChartService;

public class iphoneController extends MultiActionController {
    private authHandlerController authHandlerControllerObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private iphoneDAO iphoneDAOObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private permissionHandlerDAO permissionHandlerDAOObj;
    private CrmDashboardController crmDashboardController;
    private crmManagerController crmManagerControllerObj;
    private crmUserController crmUserControllerObj;
    private crmContactController crmContactControllerObj;
    private crmAccountController crmAccountControllerObj;
    private crmOpportunityController crmOpportunityControllerObj;
    private crmCaseReportController crmCaseReportControllerObj;
    private crmActivityReportController crmActivityReportControllerObj;
    private crmContactReportController crmContactReportControllerObj;
    private crmProductController crmProductControllerObj;
    private crmAccountReportController crmAccountReportControllerObj;
    private crmTargetReportController crmTargetReportControllerObj;
    private crmCampaignReportController crmCampaignReportControllerObj;

    private crmManagerDAO crmManagerDAOObj;
    private crmLeadReportDAO leadReportDAOObj;
    private crmAccountReportDAO accountReportDAOObj;
    private crmContactReportDAO contactReportDAOObj;
    private crmCaseReportDAO caseReportDAOObj;
    private crmOpportunityReportDAO opportunityReportDAOObj;
    private crmActivityReportDAO activityReportDAOObj;
    private crmCampaignReportDAO campaignReportDAOObj;

    private crmTargetReportDAO targetReportDAOObj;
    private crmCampaignDAO crmCampaignDAOObj;
    private ContactManagementService contactManagementService;

    private IChartService chartServiceObj;

    public void setChartService(IChartService IChartServiceObj) {
        this.chartServiceObj = IChartServiceObj;
    }

    public void setContactManagementService(ContactManagementService contactManagementService) {
        this.contactManagementService = contactManagementService;
    }

    public void setpermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
    }

    public void setcrmTargetReportDAO(crmTargetReportDAO targetReportDAOObj1) {
        this.targetReportDAOObj = targetReportDAOObj1;
    }

    public void setcrmCampaignDAO(crmCampaignDAO crmCampaignDAOObj1) {
        this.crmCampaignDAOObj = crmCampaignDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setcrmLeadReportDAO(crmLeadReportDAO leadReportDAOObj1) {
        this.leadReportDAOObj = leadReportDAOObj1;
    }

    public void setcrmAccountReportDAO(crmAccountReportDAO accountReportDAOObj1) {
        this.accountReportDAOObj = accountReportDAOObj1;
    }

    public void setcrmContactReportDAO(crmContactReportDAO contactReportDAOObj1) {
        this.contactReportDAOObj = contactReportDAOObj1;
    }

    public void setcrmCaseReportDAO(crmCaseReportDAO caseReportDAOObj1) {
        this.caseReportDAOObj = caseReportDAOObj1;
    }

    public void setcrmOpportunityReportDAO(crmOpportunityReportDAO opportunityReportDAOObj1) {
        this.opportunityReportDAOObj = opportunityReportDAOObj1;
    }

    public void setcrmActivityReportDAO(crmActivityReportDAO activityReportDAOObj1) {
        this.activityReportDAOObj = activityReportDAOObj1;
    }

    public void setcrmCampaignReportDAO(crmCampaignReportDAO campaignReportDAOObj1) {
        this.campaignReportDAOObj = campaignReportDAOObj1;
    }

    private crmLeadDAO crmLeadDAOObj;
    private crmContactDAO crmContactDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmCaseDAO crmCaseDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private crmProductDAO crmProductDAO;
    private crmOpportunityDAO crmOpportunityDAO;

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmCaseDAO(crmCaseDAO crmCaseDAOObj1) {
        this.crmCaseDAOObj = crmCaseDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public void setcampaignReportController(crmCampaignReportController crmCampaignReportControllerObj){
        this.crmCampaignReportControllerObj=crmCampaignReportControllerObj;
    }
    public void settargetReportController(crmTargetReportController crmTargetReportControllerObj){
        this.crmTargetReportControllerObj=crmTargetReportControllerObj;
    }
    public void setaccountReportController(crmAccountReportController crmAccountReportControllerObj){
        this.crmAccountReportControllerObj=crmAccountReportControllerObj;
    }
    public void setcrmProductController(crmProductController crmProductControllerObj){
        this.crmProductControllerObj=crmProductControllerObj;
    }
    public void setcontactReportController(crmContactReportController crmContactReportControllerObj){
        this.crmContactReportControllerObj=crmContactReportControllerObj;
    }
    public void setactivityReportController(crmActivityReportController crmActivityReportControllerObj){
        this.crmActivityReportControllerObj=crmActivityReportControllerObj;
    }
    public void setcaseReportController (crmCaseReportController crmCaseReportControllerObj){
        this.crmCaseReportControllerObj=crmCaseReportControllerObj;
    }
    public void setcrmOpportunityController (crmOpportunityController crmOpportunityControllerObj){
        this.crmOpportunityControllerObj=crmOpportunityControllerObj;
    }

    private crmOpportunityReportController crmOpportunityReportControllerObj;
    public void setopportunityReportController(crmOpportunityReportController crmOpportunityReportControllerObj ){
        this.crmOpportunityReportControllerObj=crmOpportunityReportControllerObj;
    }

    private crmLeadReportController crmLeadReportControllerObj;
    public void setleadReportController(crmLeadReportController crmLeadReportControllerObj){
        this.crmLeadReportControllerObj=crmLeadReportControllerObj;
    }

    public void setcrmAccountController (crmAccountController crmAccountControllerObj){
        this.crmAccountControllerObj=crmAccountControllerObj;
    }
    public void setcrmContactController ( crmContactController crmContactControllerObj){
        this.crmContactControllerObj=crmContactControllerObj;
    }

    public void setcrmUserController(crmUserController crmUserControllerObj){
        this.crmUserControllerObj=crmUserControllerObj;
    }
    public void setcrmManagerController(crmManagerController crmManagerControllerObj){
        this.crmManagerControllerObj=crmManagerControllerObj;
    }

    public void setiphoneDAO(iphoneDAO iphoneDAOObj){
        this.iphoneDAOObj=iphoneDAOObj;
    }
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setauthHandlerController(authHandlerController authHandlerControllerObj) {
        this.authHandlerControllerObj = authHandlerControllerObj;
    }
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public void setCrmProductDAO(crmProductDAO crmProductDAO) {
		this.crmProductDAO = crmProductDAO;
	}
    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOO) {
        this.crmOpportunityDAO = crmOpportunityDAOO;
    }
	public ModelAndView deskeraCRMMOB_V1(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JSONObject jobj = new JSONObject();
        ModelAndView model;
        String result = "";
        try {
            int action = Integer.parseInt(request.getParameter("act"));
            int mode = Integer.parseInt(request.getParameter("mode"));
            if (action != 0) {
                try {
                    if (StringUtil.isNullOrEmpty(sessionHandlerImpl.getUserid(request))) {
                        setUserSession(request, response);
                    }
                } catch (SessionExpiredException ex) {
                    logger.warn("Exception in iphoneController:deskeraCRMMOB_V1() - Session has not set. Need to create new session.");
//                    logger.warn(ex.getMessage(), ex);
                    setUserSession(request, response);
                }
            }

            switch (action) {
                case 0: // generate application id
                    jobj = generateAppID(request, response);
                    result=jobj.toString();
                    break;
                case 1: // dashboard request
                    switch (mode) {

                        case 0: // get Modules Updates
                            model=getCrmDashboardController().getAllUpdatesForWidget(request, response);
                            result= model.getModel().get("model").toString();
                            break;
                        case 1: // get Reports
                            result = getModuleReports(request);
                            break;
                        case 2:// view modules
                            result = getModules(request);
                            break;
                        case 3:// view modules
                            result = getModules(request);
                            break;
                        case 4:// get Messages

                            String url = "/Common/MailIntegration/mailIntegrate.do?action=EmailUIAjax";
                            RequestDispatcher dispatcher = request.getRequestDispatcher(url);
                            dispatcher.forward(request, response);
                            break;
                        case 5:// get owner List
                            //jobj = crmDbcon.getOwner(request);
                            //result = jobj.toString();
                            model =crmUserControllerObj.getOwner(request, response);
                            jobj =  new JSONObject(model.getModel().get("model").toString());
                            result=jobj.toString();
                            break;
                        case 6:// get 1.Lead Status 2. Account Type 3. Indusrty
                            //String comboname = request.getParameter("comboname");
                            model = crmManagerControllerObj.getComboData(request, response);
                            jobj =  new JSONObject(model.getModel().get("model").toString());
                            result=jobj.toString();
                            break;
                        case 7:// get Account Name
                            jobj = getAllAccounts(request, response);
                            result = jobj.toString();
                            break;
                        case 8:// 1.get owner List 2.lead source 3.Stage 4.Account Name
                            jobj = getAllComboValueForProduct(request, response);
                            result = jobj.toString();
                            break;
                        case 9:// 1.get owner List 2.get category
                            jobj = getAllComboValueForOpportunity(request, response);
                            result = jobj.toString();
                            break;
                        case 10://sign out
                            sessionHandlerImplObj.destroyUserSession(request, response);
                            result = "{\"success\":\"true\"}";
                            break;
                        case 11://get user profile data
                            result = getUserDetails(request);
                            break;
                        case 12:// get Campaign Data

                            jobj = getCampaign(request, response);
                            result = jobj.toString();
                            break;
                        case 13:// get Lead Data
                            jobj = getLead(request, response);
                            result = jobj.toString();
                            break;
                        case 14:// get Contact Data
                            jobj = getContact(request, response);
                            result = jobj.toString();
                            break;
                        case 16:// get Account Data
                            jobj = getAccount(request, response);
                            result = jobj.toString();
                            break;
                        case 18:// get Case Data
                            jobj = getCase(request, response);
                            result = jobj.toString();
                            break;
                        case 19:// get product Data
                            jobj = getProduct(request, response);
                            result = jobj.toString();
                            break;
                        case 20:// get Opportunity Data
                            jobj = getopportunity(request, response);
                            result = jobj.toString();
                            break;
                    }
                    break;
                //<editor-fold defaultstate="collapsed" desc="Report Case Section. Click on the + sign on the left to edit the code.">
                case 2: // Get Reports
                    switch (mode) {
                        case 0://revenue by opp source &
                            result = getSalesbyLeadsource(request, response);
                            break;
                        case 1: //  revenue by stage
                            result = oppByStageReport(request, response);
                            break;
                        case 2:// Leads by Source
                            result = getLeadbySource(request, response);
                            break;
                        case 3://Key Accounts
                            result = getKeyAccounts(request);
                            break;
                        case 4://Cases by Status
                            result = getCaseByStatus(request);
                            break;
                        case 5:// Converted leads
                            result = getConvertedLeads(request, response);
                            break;
                        case 6://Sales by Source
                            result = getSalesbysource(request, response);
                            break;
                        case 7:// Leads by Industry
                            result = getLeadsByIndustry(request, response);
                            break;
                        case 8:// Closed Opportunities
                            result = getOpportunityForReports(request, response, 1);
                            break;
                        case 9:// Opportunities By Type
                            result = getOpportunityForReports(request, response, 2);
                            break;
                        case 10:// Opportunity Product Report
                            result = getOpportunityForReports(request, response, 3);
                            break;
                        case 11:// Stuck Opportunities
                            result = getOpportunityForReports(request, response, 4);
                            break;
                        case 12://Monthly Account
                            result = getAccountPerMonth(request);
                            break;
                        case 13://Account Owners
                            result = getAccountOwners(request);
                            break;
                        case 14:// Opporunity Source
                            result = getOpporunitySource(request);
                            break;
                        case 15://HighPriorityActivities
                            result = getHighPriorityActivities(request);
                            break;
                        case 16://Contacts with High Priority Cases
                            result = getContactCases(request);
                            break;
                        case 17://ProductCases
                            result = getProductCases(request);
                            break;
                        case 18://Account with High Priority Cases
                            result = getAccountCases(request);
                            break;
                        case 19://Monthly Cases
                            result = getMonthlyCases(request);
                            break;
                        case 20://Industry-Account Type Report
                            result = getIndustryAccountType(request);
                            break;
                        case 21://Campaigns by Type
                            result = getCampaignType(request);
                            break;
                        case 22://Completed Campaigns by Type
                            result = getCompletedCampaign(request);
                            break;
                        case 23: //Qualified Leads
                            result = getQualifiedLeads(request);
                            break;
                        case 24: //Accounts with Contacts
                            result = getAccountWithContacts(request);
                            break;
                        case 25://Campaigns with Good Response
                            result = getCampaignResponse(request);
                            break;
                        case 26: //Contacted Leads
                            result = getContactedLeads(request);
                            break;
                        case 27: //Contacts by Lead Source
                            result = getLeadContacts(request);
                            break;
                        case 28: //Accounts with Opportunities
                            result = getAccountOpp(request);
                            break;
                        case 30://Newly Added Cases
                            result = getNewlyAddedCase(request);
                            break;
                        case 31://Pending Cases
                            result = getPendingCases(request);
                            break;
                        case 32://Escalated Cases
                            result = getEscalatedCases(request);
                            break;
                        case 33: //Accounts with Cases
                            result = getAccCases(request);
                            break;
                        case 34: //open Leads
                            result = getOpenLeads(request);
                            break;
                        case 35: //open Leads
                            result = getContactWithCase(request);
                            break;
                        case 36: //Converted Leads to Account
                            result = getConvertedLeadsAccount(request);
                            break;
                        case 37: //Converted Leads to Opportunity
                            result = getConvertedLeadsOpp(request);
                            break;
                        case 38: //Converted Leads to Contacts
                            result = getConvertedLeadsContact(request);
                            break;
                        case 39://Targets by Owner
                            result = getTargetOwner(request);
                            break;
                        case 40://Opp Pipelined
                            result = getOpenOppPipelined(request);
                            break;
                        case 41://Lead pipeline
                            result = getLeadPipelined(request);
                            break;
                    }
                    break;
                case 3://update recordsde
                    switch (mode) {
                        case 0: //update user
                            result = updateUser(request);
                            break;
                        case 1:
                            result = sendMail(request, response);
                            break;
                        case 13:// insert Lead Data

                            jobj = insertLead(request);
                            result = jobj.toString();
                            break;
                        case 14:// insert Contact Data

                            jobj = insertContact(request);
                            result = jobj.toString();
                            break;
                        case 16:// insert Account Data

                            jobj = insertAccount(request);
                            result = jobj.toString();
                            break;
                        case 18:// insert Case Data

                            jobj = insertCase(request);
                            result = jobj.toString();
                            break;
                        case 19:// insert product Data

                            jobj = insertProduct(request);
                            result = jobj.toString();
                            break;
                        case 20:// insert Opportunity Data

                            jobj = insertOpportunity(request);
                            result = jobj.toString();
                            break;
                    }
                    break;
                //<editor-fold defaultstate="collapsed" desc="chart Section. Click on the + sign on the left to edit the code.">
                case 4:// get Chart Data
                    switch (mode) {
                        case 1: //Leads by Source
                            result = getChart(request, "Lead Source","LeadsbySource");
                            break;
                        case 2: //Leads By Industry
                            result = getChart(request, "Industry","LeadsbyIndustry");
                            break;
                        case 3:// cases by status
                            result = getChart(request, "Case Status","CasesbyStatus");
                            break;
                        case 8:
                            result = getChart(request, "Lead Source","SourcesOfOpportunity");
                            break;
                        case 9:
                            result = getHighPriorityActivityChart(request, "Related To");
                            break;
                        case 10:
                            result = getChart(request, "Title","ContactHighPriority");
                            break;
                        case 11:
                            result = getProductHighPriorityChart(request);// no chart
                            break;
                        case 12:
                            result = getAccountHighPriorityChart(request);// no chart
                            break;
                        case 14:
                            result = getChart(request, "Industry","IndustryAccountTypes");
                            break;
                        case 15:
                            result = getChart(request, "Opportunity Type","OpportunityByType");
                            break;
                        case 16:
                            result = opportunityByProduct( request);
                            break;
                        case 17:
                            result = accountsByOwner( request);
                            break;
                        case 18:
                            result = getChart(request, "Opportunity Stage","StuckOpportunities");
                            break;
                        case 20:
                            result = closedOppPieChart(request);
                            break;
                        case 21:
                            result = getChart(request, "Lead Source","ConvertedLeads");
                            break;
                        case 25:
                            result = getChart(request, "Lead Source","OpportunitybySource");
                            break;
                        case 26:
                            result = getChart(request, "Opportunity Stage","OpportunitybyStage");
                            break;
                        case 28:
                            result = getKeyAccountsPie( request);
                            break;
                        case 29:
                            result = getChart(request, "Lead Source","SalesbySource");
                            break;
                        case 35:
                            result = getMonthlyChartPie(request,"MonthlyAccounts");
                            break;
                        case 42:
                            result = getMonthlyChartPie(request,"MonthlyCases");
                            break;
                        case 44:
                            result = getChart( request, "Campaign Type","CampaignType");
                            break;
                        case 46:
                            result = getChart( request, "Campaign Type","CompletedCampaign");
                            break;
                        case 48:
                            result = getChart(request, "Lead Source","QualifiedLeads");
                            break;
                        case 50:
                            result = getAccountContactPie(request);
                            break;
                        case 52:
                            result = getChart( request, "Campaign Type","CampaignResponse");
                            break;
                        case 54:
                            result = getChart(request, "Lead Source","ContactedLeads");
                            break;
                        case 56://  Contacts by Lead Source
                            result = getChart(request, "Lead Source","LeadSourceContacts");
                            break;
                        case 58:
                            result = getAccountOpportunityPie(request);
                            break;
                        case 62:
                            result = getChart(request, "Priority","NewlyAddedCases");
                            break;
                        case 64:
                            result = getChart(request, "Priority","PendingCases");
                            break;
                        case 66:
                            result = getChart(request, "Priority","EscalatedCases");
                            break;
                        case 68:
                            result = getAccountCasesPie( request);
                            break;
                        case 70:
                            result = getOpenLeadsPie(request);
                            break;
                        case 72:
                            result = getContactCasePie( request);
                            break;
                        case 74:
                            result = getChart( request, "Lead Source","ConvertedLeadAccount");
                            break;
                        case 76:
                            result = getChart( request, "Lead Source","ConvertedLeadOpp");
                            break;
                        case 78:
                            result = getChart( request, "Lead Source","ConvertedLeadContact");
                            break;
                        case 80:
                            result = getTargetOwnerPie(request);
                            break;
                        case 84:
                            result = getChart(request, "Opportunity Stage","OpportunityPipeline");
                            break;
                        case 86:
                            result = getChart(request, "Lead Status","LeadPipeline");
                            break;
                    }
                    break;
            }
        }  catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }catch (SessionExpiredException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return new ModelAndView("jsonView-ex", "model", result);
    }

    private String updateUser(HttpServletRequest request) {
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String userid = request.getParameter("userid");
            String fname = request.getParameter("firstname");
            HashMap<String, Object> userRequestParams = new HashMap<String, Object>();
            userRequestParams.put("addUser", false);
            userRequestParams.put("userid", userid);
            userRequestParams.put("firstName", fname);
            userRequestParams.put("lastName", request.getParameter("lastname"));
            userRequestParams.put("emailID", request.getParameter("email"));
            userRequestParams.put("address", request.getParameter("address"));
            userRequestParams.put("contactNumber", request.getParameter("contactno"));
            kmsg = profileHandlerDAOObj.saveUser(userRequestParams);
            result = "{\"success\":\"true\"}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String sendMail(HttpServletRequest request, HttpServletResponse response) throws JSONException {
        String result = null;
        try {
            DiskFileUpload fu = new DiskFileUpload();
            java.util.List fileItems = null;
            String imageName = "";

            fileItems = fu.parseRequest(request);

            java.util.HashMap arrParam = new java.util.HashMap();
            java.util.Iterator k = null;
            for (k = fileItems.iterator(); k.hasNext();) {
                FileItem fi1 = (FileItem) k.next();
                arrParam.put(fi1.getFieldName(), fi1.getString());
            }

            String to = request.getParameter("mailto");
            String from = request.getParameter("from");
            String accountid = StringUtil.serverHTMLStripper(arrParam.get("from").toString().replaceAll("[^\\w|\\s|'|\\-|\\[|\\]|\\(|\\)]", "").trim());
            String subject = StringUtil.serverHTMLStripper(arrParam.get("subject").toString().replaceAll("[^\\w|\\s|'|\\-|\\[|\\]|\\(|\\)]", "").trim());
            String body = arrParam.get("body").toString();//StringUtil.serverHTMLStripper(arrParam.get("body").toString().replaceAll("[^\\w|\\s|'|\\-|\\[|\\]|\\(|\\)]", "").trim());

            String addressFrom1 = URLEncoder.encode(accountid, "ISO-8859-1");
            String fromAccount = URLEncoder.encode(accountid, "ISO-8859-1");
            String sendDescription = URLEncoder.encode(body, "ISO-8859-1");
            String sendSubject = URLEncoder.encode(subject, "ISO-8859-1");
            String sendTo = URLEncoder.encode(to, "ISO-8859-1");
            String subject1 = URLEncoder.encode(subject, "ISO-8859-1");

            String Url = "action=EmailUIAjax&addressFrom1=" + addressFrom1 + "&addressTo1=" + sendTo + "&composeType=" +
                    "&emailUIAction=sendEmail&fromAccount=" + fromAccount + "&krawler_body_only=true" +
                    "&module=Emails&saveToKrawler=1&sendCharset=ISO-8859-1&sendDescription=" + sendDescription + "" +
                    "&sendSubject=" + sendSubject + "&sendTo=" + sendTo + "&setEditor=1&subject1=" + subject1 + "&to_pdf=true";


            RequestDispatcher dispatcher = request.getRequestDispatcher("/Common/MailIntegration/mailIntegrate.do?" + Url);
            dispatcher.forward(request, response);

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while sending mail(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private JSONObject insertLead(HttpServletRequest request) throws JSONException {
        JSONObject jobj = null;
        KwlReturnObject kmsg = null;
        try {
            jobj = new JSONObject();
            String lastName = request.getParameter("lastname");
            String firstName = request.getParameter("firstname");
            String ownerName = request.getParameter("ownername");
            String ownerId = request.getParameter("ownerid");
            String email = request.getParameter("email");
            String company = request.getParameter("company");
            String leadStatus = request.getParameter("leadstatus");
            String leadStatusId = request.getParameter("leadstatusid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String id = java.util.UUID.randomUUID().toString();

            jobj.put("lastname", lastName);
            jobj.put("firstname", firstName);
            jobj.put("email", email);
            jobj.put("leadstatusid", leadStatusId);
            jobj.put("isconverted", "0");
            jobj.put("istransfered", "0");
            jobj.put("companyid", companyid);
            jobj.put("userid", userid);
            jobj.put("leadid", id);
            jobj.put("type", "0");
            jobj.put("leadownerid", ownerId);
            jobj.put("validflag", 1);
            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
            kmsg = crmLeadDAOObj.addLeads(jobj);

            CrmLead crmLead = (CrmLead) kmsg.getEntityList().get(0);
            if (crmLead.getValidflag() == 1) {
                auditTrailDAOObj.insertAuditLog(AuditAction.LEAD_CREATE,
                        crmLead.getLastname() + " - Lead created ",
                        request, id);
            }
            jobj = new JSONObject();
            jobj.put("success", true);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while saving new lead (" + ex.toString() + ")");
        }
        return jobj;
    }

    private JSONObject insertContact(HttpServletRequest request) throws JSONException {
        JSONObject jobj = null;
        KwlReturnObject kmsg = null;
        try {
            jobj = new JSONObject();
            String firstName = request.getParameter("firstname");
            String lastName = request.getParameter("lastname");
            String contactno = request.getParameter("contactno");
            String email = request.getParameter("email");
            String title = request.getParameter("title");
            String accountid = request.getParameter("accountid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String ipAddress = SystemUtil.getIpAddress(request);
            jobj.put("firstname", firstName);
            jobj.put("lastname", lastName);
            jobj.put("email", email);
            jobj.put("phoneno", contactno);
            jobj.put("title", title);
            jobj.put("contactownerid", userid);
            jobj.put("validflag", 1);
            jobj.put("accountid", accountid);
            contactManagementService.saveContact(companyid, userid, sessionHandlerImpl.getTimeZoneDifference(request), ipAddress, jobj);

            jobj = new JSONObject();
            jobj.put("success", true);

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while saving new contact (" + ex.toString() + ")");
        }
        return jobj;
    }

    private JSONObject insertAccount(HttpServletRequest request) throws JSONException {
        JSONObject jobj = null;
        KwlReturnObject kmsg = null;
        try {
            jobj = new JSONObject();
            String accountname = request.getParameter("accountname");
            String revenue = request.getParameter("revenue");
            String email = request.getParameter("email");
            String contactno = request.getParameter("contactno");
            String industryid = request.getParameter("industryid");
            String indusrty = request.getParameter("indusrty");
            String type = request.getParameter("type");
            String typeid = request.getParameter("typeid");
            String accountowner = request.getParameter("accountowner");
            String accountownerid = request.getParameter("accountownerid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String id = java.util.UUID.randomUUID().toString();

            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
            jobj.put("accountname", accountname);
            jobj.put("accounttypeid", typeid);
            jobj.put("phone", contactno);
            jobj.put("revenue", revenue);
            jobj.put("companyid", companyid);
            jobj.put("userid", userid);
            jobj.put("accountid", id);
            jobj.put("accountownerid", accountownerid);
            jobj.put("validflag", 1);
            jobj.put("industryid", industryid);
            jobj.put("email", email);
            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
            kmsg = crmAccountDAOObj.addAccounts(jobj);

            CrmAccount crmAccount = (CrmAccount) kmsg.getEntityList().get(0);
            if (crmAccount.getValidflag() == 1) {
                auditTrailDAOObj.insertAuditLog(AuditAction.ACCOUNT_CREATE,
                        crmAccount.getAccountname() + " - Account created ",
                        request, id);
            }
            jobj = new JSONObject();
            jobj.put("success", true);

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while saving new account (" + ex.toString() + ")");
        }
        return jobj;
    }

    private JSONObject insertCase(HttpServletRequest request) throws JSONException {
        JSONObject jobj = null;
        KwlReturnObject kmsg = null;
        try {
            jobj = new JSONObject();

            String subject = request.getParameter("subject");
            String accountid = request.getParameter("accountid");
            String priorityid = request.getParameter("priorityid");
            String statusid = request.getParameter("statusid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String id = java.util.UUID.randomUUID().toString();

            jobj.put("casepriorityid", priorityid);
            jobj.put("casestatusid", statusid);
            jobj.put("companyid", companyid);
            jobj.put("userid", userid);
            jobj.put("accountnameid", accountid);
            jobj.put("subject", subject);
            jobj.put("caseownerid", userid);
            jobj.put("validflag", 1);
            jobj.put("updatedon", new Date());
            jobj.put("caseid", id);
            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
            kmsg = crmCaseDAOObj.addCases(jobj);

            CrmCase crmCase = (CrmCase) kmsg.getEntityList().get(0);
            if (crmCase.getValidflag() == 1) {
                auditTrailDAOObj.insertAuditLog(AuditAction.CASE_CREATE,
                        crmCase.getSubject() + " - Case created ",
                        request, id);
            }
            jobj = new JSONObject();
            jobj.put("success", true);

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while saving new case (" + ex.toString() + ")");
        }
        return jobj;
    }

    private String getKeyAccounts(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = accountReportDAOObj.keyAccountsReport(requestParams, usersList);
            commData.put("totalCount", kmsg.getRecordTotalCount());
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmAccount aCrmAccount = (CrmAccount) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", aCrmAccount.getAccountname());
                String amt = "$0";
                if (!StringUtil.isNullOrEmpty(aCrmAccount.getRevenue())) {
                    amt = crmManagerDAOObj.currencyRender(aCrmAccount.getRevenue(), currencyid);
                    if (amt.contains("&#36;")) {
                        amt = amt.replace("&#36;", "$");
                    }
                }
                tmpObj.put("secondary", "Revenue : ".concat(amt));
                String[] ownerInfo=crmAccountHandler.getAllAccOwners(crmAccountDAOObj, aCrmAccount.getAccountid());
                tmpObj.put("desc", "Account owner : " + ownerInfo[5]);
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getAccountPerMonth(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("year", StringUtil.checkForNull(request.getParameter("year")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = accountReportDAOObj.monthlyAccountsReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmAccount aCrmAccount = (CrmAccount) ite.next();
                String[] ownerInfo=crmAccountHandler.getAllAccOwners(crmAccountDAOObj, aCrmAccount.getAccountid());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", aCrmAccount.getAccountname());
                tmpObj.put("secondary", "Account owner : " + ownerInfo[5]);
                tmpObj.put("desc", "Created on : " + aCrmAccount.getCreatedon());
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getCaseByStatus(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.caseByStatusReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCasename());
                tmpObj.put("secondary", "Case status : " + (obj.getCrmCombodataByCasestatusid() != null ? obj.getCrmCombodataByCasestatusid().getValue() : ""));
                tmpObj.put("desc", "Case owner : " + obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getAccountOwners(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = accountReportDAOObj.accountOwnersReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmAccount aCrmAccount = (CrmAccount) ite.next();
                String[] ownerInfo=crmAccountHandler.getAllAccOwners(crmAccountDAOObj, aCrmAccount.getAccountid());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", ownerInfo[5]);
                tmpObj.put("secondary", "Account Name : " + aCrmAccount.getAccountname());
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();

        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getOpporunitySource(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = opportunityReportDAOObj.sourceOfOppReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getOppname());
                tmpObj.put("desc", "Account name : " + (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("secondary", "Lead source : " + (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
//        } catch (SessionExpiredException ex) {
//            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getHighPriorityActivities(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        JSONObject commData = new JSONObject();
        int dl = 0;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = activityReportDAOObj.accountActivities(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            dl = kmsg.getRecordTotalCount();
            while (ite.hasNext()) {
                Object row[] = (Object[]) ite.next();
                CrmActivityMaster obj = (CrmActivityMaster) row[0];
                CrmAccount obj2 = (CrmAccount) row[1];

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("desc", "Status : " + crmManagerCommon.comboValue(obj.getCrmCombodataByStatusid()));
                tmpObj.put("primary", obj.getFlag());
                tmpObj.put("secondary", "Related name : " + obj2.getAccountname());
                jarr.put(tmpObj);
            }

            kmsg = activityReportDAOObj.campaignActivities(requestParams, usersList);
            ite = kmsg.getEntityList().iterator();
            dl += kmsg.getRecordTotalCount();
            while (ite.hasNext()) {
                Object row[] = (Object[]) ite.next();
                CrmCampaign obj2 = (CrmCampaign) row[1];
                CrmActivityMaster obj = (CrmActivityMaster) row[0];

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("desc", "Status :" + crmManagerCommon.comboValue(obj.getCrmCombodataByStatusid()));
                tmpObj.put("primary", obj.getFlag());
                tmpObj.put("secondary", "Related name :" + obj2.getCampaignname());
                jarr.put(tmpObj);
            }

            kmsg = activityReportDAOObj.caseActivities(requestParams, usersList);
            ite = kmsg.getEntityList().iterator();
            dl += kmsg.getRecordTotalCount();
            while (ite.hasNext()) {
                Object row[] = (Object[]) ite.next();
                CrmCase obj2 = (CrmCase) row[1];
                CrmActivityMaster obj = (CrmActivityMaster) row[0];

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("desc", "Status :" + crmManagerCommon.comboValue(obj.getCrmCombodataByStatusid()));
                tmpObj.put("primary", obj.getFlag());
                tmpObj.put("secondary", "Related name :" + obj2.getCasename());
                jarr.put(tmpObj);
            }

            kmsg = activityReportDAOObj.contactActivities(requestParams, usersList);
            ite = kmsg.getEntityList().iterator();
            dl += kmsg.getRecordTotalCount();
            while (ite.hasNext()) {
                Object row[] = (Object[]) ite.next();
                CrmContact obj2 = (CrmContact) row[1];
                CrmActivityMaster obj = (CrmActivityMaster) row[0];

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("desc", "Status :" + crmManagerCommon.comboValue(obj.getCrmCombodataByStatusid()));
                tmpObj.put("primary", obj.getFlag());
                tmpObj.put("secondary", "Related name :" + obj2.getFirstname() + " " + obj2.getLastname());
                jarr.put(tmpObj);
            }

            kmsg = activityReportDAOObj.leadActivities(requestParams, usersList);
            ite = kmsg.getEntityList().iterator();
            dl += kmsg.getRecordTotalCount();
            while (ite.hasNext()) {
                Object row[] = (Object[]) ite.next();
                CrmActivityMaster obj = (CrmActivityMaster) row[0];
                CrmLead obj2 = (CrmLead) row[1];

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("desc", "Status :" + crmManagerCommon.comboValue(obj.getCrmCombodataByStatusid()));
                tmpObj.put("primary", obj.getFlag());
                tmpObj.put("secondary", "Related name :"+ obj2.getLastname());
                jarr.put(tmpObj);
            }

            kmsg = activityReportDAOObj.opportunityActivities(requestParams, usersList);
            ite = kmsg.getEntityList().iterator();
            dl += kmsg.getRecordTotalCount();
            while (ite.hasNext()) {
                Object row[] = (Object[]) ite.next();
                CrmOpportunity obj2 = (CrmOpportunity) row[1];
                CrmActivityMaster obj = (CrmActivityMaster) row[0];

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("desc", "Status :" + crmManagerCommon.comboValue(obj.getCrmCombodataByStatusid()));
                tmpObj.put("primary", obj.getFlag());
                tmpObj.put("secondary", "Related name :" + obj2.getOppname());

                jarr.put(tmpObj);
            }

            commData.put("data", jarr);
            commData.put("success", true);
            commData.put("totalCount", dl);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (SessionExpiredException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getContactCases(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.contactsWithCasesReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCrmContact().getFirstname());
                tmpObj.put("secondary", "Title : " + obj.getCrmContact().getTitle() != null ? obj.getCrmContact().getTitle() : "" );
                tmpObj.put("desc", "Lead source : " + crmManagerCommon.comboValue(obj.getCrmContact().getCrmCombodataByLeadsourceid()));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getProductCases(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            KwlReturnObject kmsg = caseReportDAOObj.productCasesReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                Object [] cp = (Object[]) ite.next();
                CrmProduct crmProduct = (CrmProduct) cp[0];
                CrmCase caseobj = (CrmCase) cp[1];
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", crmProduct.getProductname());
                tmpObj.put("secondary", "Category : " + (crmProduct.getCrmCombodataByCategoryid() != null ? crmProduct.getCrmCombodataByCategoryid().getValue() : ""));
                tmpObj.put("desc", "vendor name : " + crmProduct.getVendornamee());
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getAccountCases(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.accountsWithCaseReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCrmAccount().getAccountname());
                tmpObj.put("secondary", "Industry : " + crmManagerCommon.comboValue(obj.getCrmAccount().getCrmCombodataByIndustryid()));
                String[] ownerInfo=crmAccountHandler.getAllAccOwners(crmAccountDAOObj, obj.getCrmAccount().getAccountid());
                tmpObj.put("desc", "Account owner : " + ownerInfo[5]);
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getMonthlyCases(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("year", StringUtil.checkForNull(request.getParameter("year")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.monthlyCasesReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCasename());
                tmpObj.put("secondary", "Case owner : " + obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("desc", "Subject : " + obj.getSubject());
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getIndustryAccountType(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = accountReportDAOObj.industryAccountTypeReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmAccount obj = (CrmAccount) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getAccountname());
                tmpObj.put("secondary", "Account type : " + (obj.getCrmCombodataByAccounttypeid() != null ? obj.getCrmCombodataByAccounttypeid().getValue() : ""));
                tmpObj.put("desc", "Industry : " + (obj.getCrmCombodataByIndustryid() != null ? obj.getCrmCombodataByIndustryid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getQualifiedLeads(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = leadReportDAOObj.qualifiedLeadsReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getLastname());
                tmpObj.put("secondary", "Type : " + crmLeadHandler.getLeadTypeName(obj.getType()));
                tmpObj.put("desc", "Industry : " + (obj.getCrmCombodataByIndustryid() != null ? obj.getCrmCombodataByIndustryid().getValue() : " Undefined "));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getCampaignType(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            KwlReturnObject kmsg = campaignReportDAOObj.campaignByTypeReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCampaign obj = (CrmCampaign) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCampaignname());
                tmpObj.put("desc", "Status : " + (obj.getCrmCombodataByCampaignstatusid() != null ? obj.getCrmCombodataByCampaignstatusid().getValue() : ""));
                tmpObj.put("secondary", "Type : " + (obj.getCrmCombodataByCampaigntypeid() != null ? obj.getCrmCombodataByCampaigntypeid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getCompletedCampaign(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            KwlReturnObject kmsg = campaignReportDAOObj.completedCampaignByTypeReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCampaign obj = (CrmCampaign) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCampaignname());
                tmpObj.put("desc", "End date : " + crmManagerDAOObj.preferenceDate(request, new Date(obj.getEndingdate()), 0));
                tmpObj.put("secondary", "Type : " + (obj.getCrmCombodataByCampaigntypeid() != null ? obj.getCrmCombodataByCampaigntypeid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getCampaignResponse(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            KwlReturnObject kmsg = campaignReportDAOObj.campaignWithGoodResponseReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCampaign obj = (CrmCampaign) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCampaignname());
                tmpObj.put("secondary", "Response : " + obj.getExpectedresponse());
                tmpObj.put("desc", "Type : " + (obj.getCrmCombodataByCampaigntypeid() != null ? obj.getCrmCombodataByCampaigntypeid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getAccountWithContacts(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = contactReportDAOObj.accountsWithContactReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCrmAccount().getAccountname());
                tmpObj.put("secondary", "Contact Name : " + obj.getFirstname() + " " + obj.getLastname());
                tmpObj.put("desc", "Industry : " + (obj.getCrmAccount().getCrmCombodataByIndustryid() != null ? obj.getCrmAccount().getCrmCombodataByIndustryid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getContactedLeads(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = leadReportDAOObj.contactedLeadsReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getLastname());
                tmpObj.put("secondary", "Type : " + crmLeadHandler.getLeadTypeName(obj.getType()));
                tmpObj.put("desc", "Lead Status : " + (obj.getCrmCombodataByLeadstatusid() != null ? obj.getCrmCombodataByLeadstatusid().getValue() : " None "));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getLeadContacts(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = contactReportDAOObj.contactsByLeadSourceReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getFirstname() + " " + obj.getLastname());
                tmpObj.put("secondary", "Lead source : " + (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                tmpObj.put("desc", "Account Name :" + (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getAccountOpp(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = opportunityReportDAOObj.accountsWithOpportunityReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("secondary", "Opportunity Name : " + obj.getOppname());
                tmpObj.put("desc", "Opportunity stage :" + (obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getNewlyAddedCase(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.newlyAddedCasesReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCasename());
                tmpObj.put("secondary", "Subject : " + obj.getSubject());
                tmpObj.put("desc", "Priority : " + (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getPendingCases(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.pendingCasesReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCasename());
                tmpObj.put("secondary", "Subject : " + obj.getSubject());
                tmpObj.put("desc", "Priority : " + (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getEscalatedCases(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.escalatedCasesReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCasename());
                tmpObj.put("secondary", "Subject : " + obj.getSubject());
                tmpObj.put("desc", "Priority : " + (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getOpenLeads(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = leadReportDAOObj.openLeadsReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getLastname());
                tmpObj.put("secondary", "Type : " + crmLeadHandler.getLeadTypeName(obj.getType()));
                tmpObj.put("desc", "Industry : " + (obj.getCrmCombodataByIndustryid() != null ? obj.getCrmCombodataByIndustryid().getValue() : " Undefined "));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getAccCases(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.accountsWithCaseReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getCrmAccount().getAccountname());
                tmpObj.put("secondary", "Case subject : " + obj.getSubject());
                tmpObj.put("desc", "Case priority : " + (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getContactWithCase(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.contactsWithCasesReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", (obj.getCrmContact() != null ? obj.getCrmContact().getFirstname() + " " + obj.getCrmContact().getLastname() : ""));
                tmpObj.put("secondary", "Case subject : " + obj.getSubject());
                tmpObj.put("desc", "Case priority : " + (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getConvertedLeadsAccount(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = accountReportDAOObj.convertedLeadsToAccountReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmAccount objAcc = (CrmAccount) ite.next();
                CrmLead objLead = objAcc.getCrmLead();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", objLead.getLastname());
                tmpObj.put("secondary", "Account Name : " + objAcc.getAccountname());
                String amt = "$0";
                if (!StringUtil.isNullOrEmpty(objAcc.getRevenue())) {
                    amt = !StringUtil.isNullOrEmpty(objAcc.getRevenue()) ? crmManagerDAOObj.currencyRender(objAcc.getRevenue(), currencyid) : "$0";
                    if (amt.contains("&#36;")) {
                        amt = amt.replace("&#36;", "$");
                    }
                }
                tmpObj.put("desc", "Revenue : " + amt);
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (SessionExpiredException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getConvertedLeadsOpp(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            int day = 0;

            kmsg = opportunityReportDAOObj.convertedLeadsToOpportunityReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmOpportunity objOpp = (CrmOpportunity) ite.next();
                CrmLead objLead = objOpp.getCrmLead();
                day = StringUtil.getDaysDiff(objLead.getCreatedon().toString(), objOpp.getCreatedon().toString());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", objLead.getLastname());
                tmpObj.put("secondary", "Opportunity Name : " + objOpp.getOppname());
                tmpObj.put("desc", "Days taken to convert : " + (day > 1 ? day + "  Days" : day + " Day"));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
//        } catch (SessionExpiredException ex) {
//            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getConvertedLeadsContact(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = contactReportDAOObj.convertedLeadsToContactReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmContact objCon = (CrmContact) ite.next();
                CrmLead objLead = objCon.getCrmLead();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", objLead.getLastname());
                tmpObj.put("secondary", "Contact Name : " + objCon.getFirstname() + " " + objCon.getLastname());
                tmpObj.put("desc", "Type : " + crmLeadHandler.getLeadTypeName(objLead.getType()));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
//        } catch (SessionExpiredException ex) {
//            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getTargetOwner(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));

            kmsg = targetReportDAOObj.targetsByOwner(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                TargetModule obj = (TargetModule) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getFirstname() + " " + obj.getLastname());
                tmpObj.put("secondary", "Owner : " + obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("desc", "Email : " + obj.getEmail());
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getOpenOppPipelined(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            kmsg = opportunityReportDAOObj.oppPipelineReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                String stageName = (String) row[0];
                String stage = (String) row[1];
                String revenue = (String) row[2];
                String price = (String) row[3];
                long count = (Long) row[4];
                double amount = 0;
                double grossProfit = 0;
                if (!StringUtil.isNullOrEmpty(stage) && !StringUtil.isNullOrEmpty(revenue) && !StringUtil.isNullOrEmpty(price)) {
                    double percentStage = Double.parseDouble(stage);
                    double totalSalesAmount = Double.parseDouble(revenue);
                    double totalPrice = Double.parseDouble(price);
                    grossProfit = totalSalesAmount - totalPrice;
                    amount = (percentStage/100) * (totalSalesAmount - totalPrice);
                }

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", stageName);
                tmpObj.put("secondary", "Percent : " + (!StringUtil.isNullOrEmpty(stage)?stage+" %":""));
                String amt = crmManagerDAOObj.currencyRender(String.valueOf(amount), currencyid);
                if (amt.contains("&#36;")) {
                    amt = amt.replace("&#36;", "$");
                }
                tmpObj.put("desc", "Amount : " + amt);
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getLeadPipelined(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            kmsg = leadReportDAOObj.leadsPipelineReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                String stageName = (String) row[0];
                String stage = (String) row[1];
                String revenue = (String) row[2];
                String price = (String) row[3];
                long count = (Long) row[4];
                double amount = 0;
                double grossProfit = 0;
                if (!StringUtil.isNullOrEmpty(stage) && !StringUtil.isNullOrEmpty(revenue) && !StringUtil.isNullOrEmpty(price)) {
                    double percentStage = Double.parseDouble(stage);
                    double totalSalesAmount = Double.parseDouble(revenue);
                    double totalPrice = Double.parseDouble(price);
                    grossProfit = totalSalesAmount - totalPrice;
                    amount = (percentStage/100) * (totalSalesAmount - totalPrice);
                }

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", stageName);
                String amt = crmManagerDAOObj.currencyRender(String.valueOf(amount), currencyid);
                if (amt.contains("&#36;")) {
                    amt = amt.replace("&#36;", "$");
                }
                tmpObj.put("secondary", "Amount : " + amt);
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getSalesbyLeadsource(HttpServletRequest request,HttpServletResponse response) throws ServiceException {
        JSONObject commData = new JSONObject();
        String result = null;
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = opportunityReportDAOObj.revenueByOppSourceReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getOppname());
                tmpObj.put("secondary", "Stage : ".concat((obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : "")));
                tmpObj.put("desc", "Sales amount : " + (!StringUtil.isNullOrEmpty(obj.getSalesamount()) ? crmManagerDAOObj.currencyRender(obj.getSalesamount(), currencyid) : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        }catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }
    private String oppByStageReport(HttpServletRequest request,HttpServletResponse response) throws ServiceException {
        JSONObject commData = new JSONObject();
        String result = null;
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = opportunityReportDAOObj.oppByStageReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getOppname());
                tmpObj.put("secondary", "Stage : ".concat((obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : "")));
                tmpObj.put("desc", "Sales amount : " + (!StringUtil.isNullOrEmpty(obj.getSalesamount()) ? crmManagerDAOObj.currencyRender(obj.getSalesamount(), currencyid) : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        }catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }
    private String getSalesbysource(HttpServletRequest request,HttpServletResponse response) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = opportunityReportDAOObj.salesReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();
                String amt = "$0";
                if (!StringUtil.isNullOrEmpty(obj.getSalesamount())) {
                    amt = crmManagerDAOObj.currencyRender(obj.getSalesamount(), currencyid);
                    if (amt.contains("&#36;")) {
                        amt = amt.replace("&#36;", "$");
                    }
                }
                tmpObj.put("secondary", "Sales amount : " + amt);
                tmpObj.put("primary", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                tmpObj.put("desc", "Account name: " + (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }
    private String getOpportunityForReports(HttpServletRequest request,HttpServletResponse response, int flagReport) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        
        try {
            //JSONObject temp = crmReports.getOpportunityForReports(session, request, flagReport);
        	String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            ModelAndView model=null;
            switch(flagReport){
                case 1:
                       model=crmOpportunityReportControllerObj.closedOppReport(request, response);
                       break;
                case 2:
                       model=crmOpportunityReportControllerObj.oppByTypeReport(request, response);
                       break;
                case 3:
                       model=crmOpportunityReportControllerObj.oppProductReport(request, response);
                       break;
                case 4:
                       model=crmOpportunityReportControllerObj.stuckOppReport(request, response);
                       break;
            }

            JSONObject temp =  new JSONObject(model.getModel().get("model").toString());
            commData.put("totalCount", temp.getString("totalCount"));
            JSONArray jArray = temp.getJSONArray("coldata");
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject j = jArray.getJSONObject(i);
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", j.getString("oppname"));
                if (flagReport == 1) {
                    tmpObj.put("desc", "closing date : " + authHandler.getDateFormatter(timeFormatId, timeZoneDiff).format(j.getLong("closingdate")));
                    tmpObj.put("secondary", "Account name : " + j.getString("accountname"));
                } else if (flagReport == 2) {
                    tmpObj.put("desc", "Stage : " + j.getString("oppstage"));
                    tmpObj.put("secondary", "Type : " + j.getString("type"));
                } else if (flagReport == 3) {
                    tmpObj.put("desc", "Stage : " + j.getString("oppstage"));
                    tmpObj.put("secondary", "Product : " + j.getString("exportmultiproduct"));
                } else if (flagReport == 4) {
                    tmpObj.put("secondary", "Stage : " + j.getString("oppstage"));
                    tmpObj.put("desc", "Probability : " + j.getString("probability"));
                }
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
//        } catch (SessionExpiredException ex) {
//            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (ServletException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (SessionExpiredException ex) {
        	 logger.warn(ex.getMessage(), ex);
             result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
		}
        return result;
    }

    private String getLeadbySource(HttpServletRequest request,HttpServletResponse response) throws ServiceException {
        String result = null;
        JSONArray jarr = new JSONArray();
        JSONObject commData = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("sourceid", StringUtil.checkForNull(request.getParameter("filterCombo")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = leadReportDAOObj.leadsBySourceReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getLastname());
                tmpObj.put("secondary", "Lead source : " + (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : " None "));
                tmpObj.put("desc", "Lead status : " + (obj.getCrmCombodataByLeadstatusid() != null ? obj.getCrmCombodataByLeadstatusid().getValue() : " None "));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);

            result = commData.toString();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }
    private String getConvertedLeads(HttpServletRequest request,HttpServletResponse response) throws ServiceException {
        String result = null;
        JSONObject commData = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = leadReportDAOObj.convertedLeadsReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getLastname());
                tmpObj.put("desc", "Type : " + crmLeadHandler.getLeadTypeName(obj.getType()));
                tmpObj.put("secondary", "Lead source : " + (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : " None "));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getChart(HttpServletRequest request, String comboname,String chart) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        int value=0;
        Iterator ite1=null;
        String result = "";
        KwlReturnObject kmsg = null;
        try {

            int chartCase=Constants.iphone.get(chart);

            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            String module="";
            switch(chartCase){
                case 1:
                    module="Lead";
                    requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
                    requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
                    break;
                case 2:
                    module="Lead";
                    requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
                    requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
                    break;
                case 3:
                    module="Lead";
                    break;
                case 4:
                    module="Lead";
                    break;
                case 5:
                    module="Lead";
                    break;
                case 7:
                    module="Lead";
                    break;
                case 8:
                    module="Opportunity";
                    break;
                case 9:
                    module="Opportunity";
                    break;
                case 10:
                    module="Opportunity";
                    break;
                case 12:
                    module="Opportunity";
                    break;
                case 13:
                    module="Opportunity";
                    break;
                case 14:
                    module="Opportunity";
                    break;
                case 15:
                    module="Opportunity";
                    requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
                    requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
                    break;
                case 17:
                    module="Account";
                    break;
                case 20:
                    module="Cases";
                    break;
                case 21:
                    module="Cases";
                    break;
                case 23:
                    module="Cases";
                    break;
                case 24:
                    module="Cases";
                    break;
                case 25:
                    module="Cases";
                    break;
                case 28:
                    module="Campaign";
                    break;
                case 29:
                    module="Campaign";
                    break;
                case 30:
                    module="Campaign";
                    break;
                case 31:
                    module="Contact";
                    break;
                case 32:
                    module="Lead";
                    break;
                case 33:
                    module="Lead";
                    break;
                case 34:
                    module="Lead";
                    break;
            }
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, module);
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboname.equalsIgnoreCase("Lead Source")) {
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            comboRequestParams.put(Constants.companyid, companyid);
            List ll = crmManagerControllerObj.getcrmManagerDAO().getComboData(comboname, comboRequestParams);
            int size =  ll.size();
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                DefaultMasterItem crmCombodata = (DefaultMasterItem) ite.next();
                String name = crmCombodata.getValue();
                String masterID = crmCombodata.getID();
                //String Jsondata = crmReports.getOpportunityBySourceChart(jarr.getJSONObject(j).get("name").toString(), session, request);
                // kmsg = contactReportDAOObj.getLeadSourceContactsChart(jarr.getJSONObject(j).get("name").toString(), requestParams, usersList);
                switch(chartCase){
                    case 1:
                        kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getLeadsBySourceChart(masterID, requestParams, usersList);
                        break;
                    case 2:
                        kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getLeadsByIndustryChart(masterID, requestParams, usersList);
                        break;
                    case 3:
                        kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getConvertedLeadsChart(masterID, requestParams, usersList);
                        break;
                    case 4:
                        kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getQualifiedLeadsChart(masterID, requestParams, usersList);
                        break;
                    case 5:
                        kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getContactedLeadsChart(masterID, requestParams, usersList);
                        break;
                    case 7:
                        kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().leadsPipelineChart(masterID, requestParams, usersList);
                        break;
                    case 8:
                        kmsg=crmOpportunityReportControllerObj.getcrmOpportunityReportDAO().getSourceOffOppChart(masterID, requestParams, usersList);
                        break;
                    case 9:
                        kmsg =crmOpportunityReportControllerObj.getcrmOpportunityReportDAO().getOpportunityByTypeChart(masterID, requestParams, usersList);
                        break;
                    case 10:
                        kmsg =crmOpportunityReportControllerObj.getcrmOpportunityReportDAO().getStuckOpportunitiesChart(masterID, requestParams, usersList);
                        break;
                    case 12:
                        kmsg =crmOpportunityReportControllerObj.getcrmOpportunityReportDAO().getOpportunityBySourceChart(masterID, requestParams, usersList);
                        break;
                    case 13:
                        kmsg =crmOpportunityReportControllerObj.getcrmOpportunityReportDAO().getOpportunityByStageChart(masterID, requestParams, usersList);
                        break;
                    case 14:
                        kmsg =crmOpportunityReportControllerObj.getcrmOpportunityReportDAO().getSalesBySourceChart(masterID, requestParams, usersList);
                        break;
                    case 15:                        
                        kmsg =crmOpportunityReportControllerObj.getcrmOpportunityReportDAO().oppPipelineChart(masterID, requestParams, usersList);
                        break;
                    case 17:
                        kmsg =crmAccountReportControllerObj.getcrmAccountReportDAO().getIndustryAccountTypeChart(masterID, requestParams, usersList);
                        break;
                    case 20:
                        kmsg =crmCaseReportControllerObj.getcrmCaseReportDAO().getCasesByStatusChart(masterID, requestParams, usersList);
                        break;
                    case 21:
                        kmsg=crmCaseReportControllerObj.getcrmCaseReportDAO().getContactHighPriorityChart(name, requestParams, usersList);
                        break;
                    case 23:
                        kmsg=crmCaseReportControllerObj.getcrmCaseReportDAO().getNewlyAddedCasesChart(masterID, requestParams, usersList);
                        break;
                    case 24:
                        kmsg=crmCaseReportControllerObj.getcrmCaseReportDAO().getPendingCasesChart(masterID, requestParams, usersList);
                        break;
                    case 25:
                        kmsg=crmCaseReportControllerObj.getcrmCaseReportDAO().getEscalatedCasesChart(masterID, requestParams, usersList);
                        break;
                    case 28:
                        kmsg = crmCampaignReportControllerObj.getcrmCampaignReportDAO().getCampaignTypeChart(masterID, requestParams, usersList);
                        break;
                    case 29:
                        kmsg = crmCampaignReportControllerObj.getcrmCampaignReportDAO().getCompletedCampaignChart(masterID, requestParams, usersList);
                        break;
                    case 30:
                        kmsg = crmCampaignReportControllerObj.getcrmCampaignReportDAO().getCampaignResponseChart(name, requestParams, usersList);
                        break;
                    case 31:
                        kmsg = crmContactReportControllerObj.getcrmContactReportDAO().getLeadSourceContactsChart(masterID, requestParams, usersList);
                        break;
                    case 32:
                        kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getConvertedLeadAccountPie(masterID, requestParams, usersList);
                        break;
                    case 33:
                        kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getConvertedLeadOppPie(masterID, requestParams, usersList);
                        break;
                    case 34:
                        kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getConvertedLeadContactPie(masterID, requestParams, usersList);
                        break;
                }
                if (kmsg.getRecordTotalCount() > 0) {

                    if(chartCase == 12 ){
                        value = 0;
                        ite1 = kmsg.getEntityList().iterator();
                        while (ite1.hasNext()) {
                            CrmOpportunity crmOpp = (CrmOpportunity) ite1.next();
                            try {
                                value += Integer.parseInt(crmOpp.getSalesamount());
                            } catch (Exception e) {
                                logger.warn(e.getMessage(), e);
                            }
                        }
                    }else{
                        value=kmsg.getRecordTotalCount();
                    }

                        if (size < 10) {
                            JSONObject tempObj = new JSONObject();
                            tempObj.put("name", name);
                            tempObj.put("value",value);
                            jArray.put(tempObj);
                            totalCount++;
                        } else {
                            arrname[totalCount] = name;
                            String fv = String.valueOf(value).concat("." + totalCount + "");
                            arrvalue[totalCount] = Float.parseFloat(fv);
                            totalCount++;
                        }
                    }

            }
            kmsg =null;
            String undefinedStr="";
            switch(chartCase){
                case 2:
                    undefinedStr="Undefined";
                    kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getLeadsByIndustryChart(undefinedStr, requestParams, usersList);
                    break;
                case 3:
                    undefinedStr="None";
                    kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getConvertedLeadsChart(undefinedStr, requestParams, usersList);
                    break;
                case 4:
                    undefinedStr="Undefined";
                    kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getQualifiedLeadsChart(undefinedStr, requestParams, usersList);
                    break;
                case 5:
                    undefinedStr="Undefined";
                    kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getContactedLeadsChart(undefinedStr, requestParams, usersList);
                    break;

                    case 30:
                    undefinedStr="Undefined";
                    kmsg = crmCampaignReportControllerObj.getcrmCampaignReportDAO().getCampaignResponseChart(undefinedStr, requestParams, usersList);
                    break;
                    case 31:
                    undefinedStr="Undefined";
                    kmsg = crmContactReportControllerObj.getcrmContactReportDAO().getLeadSourceContactsChart(undefinedStr, requestParams, usersList);
                    break;
                case 32:
                    undefinedStr="Undefined";
                    kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getConvertedLeadAccountPie(undefinedStr, requestParams, usersList);
                    break;
                case 33:
                    undefinedStr="Undefined";
                    kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getConvertedLeadOppPie(undefinedStr, requestParams, usersList);
                    break;
                case 34:
                    undefinedStr="Undefined";
                    kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getConvertedLeadContactPie(undefinedStr, requestParams, usersList);
                    break;

            }
            if(kmsg != null){
                if (kmsg.getRecordTotalCount() > 0) {
                    if (size < 10) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("name", undefinedStr);
                        tempObj.put("value", kmsg.getRecordTotalCount());
                        jArray.put(tempObj);
                        totalCount++;
                    } else {
                        arrname[totalCount] = undefinedStr;
                        String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                        arrvalue[totalCount] = Float.parseFloat(fv);
                        totalCount++;
                    }
                }
            }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("count", jArray.length());
            jFinal.put("success", true);
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }
    public String closedOppPieChart(HttpServletRequest request)
            throws ServletException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        int totalCount = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);

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

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            if(!heirarchyPerm){
                filter_names.add("INoo.usersByUserid.userID");filter_params.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            kmsg = crmOpportunityControllerObj.getcrmOpportunityDAO().getAllOpportunities(requestParams);
            int size = kmsg.getRecordTotalCount();
            Iterator ite = kmsg.getEntityList().iterator();
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            String name="";
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                name=obj.getOppname();
                kmsg =crmOpportunityReportControllerObj.getcrmOpportunityReportDAO().getClosedOppChart(obj.getOppid(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    if (size < 10) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("name", name);
                        tempObj.put("value",kmsg.getRecordTotalCount());
                        jArray.put(tempObj);
                        totalCount++;
                    } else {
                        arrname[totalCount] = name;
                        String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                        arrvalue[totalCount] = Float.parseFloat(fv);
                        totalCount++;
                    }
                }
            }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("success", true);
            jFinal.put("count", jArray.length());
            result = jFinal.toString();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

     public String getOpenLeadsPie(HttpServletRequest request)
            throws ServletException {
        String result = "";
        KwlReturnObject kmsg = null;
        int under30 = 0;
        int under60 = 0;
        int under90 = 0;
        int over90 = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = crmLeadReportControllerObj.getcrmLeadReportDAO().getOpenLeadChart(requestParams, usersList);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            Date today = cal.getTime();

            Iterator ite = kmsg.getEntityList().iterator();
            while(ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                int day=StringUtil.getDaysDiff(obj.getCreatedon().toString(), sdf.format(today));
                if(day <= 30) {
                    under30++;
                } else if (day > 30 && day <= 60) {
                    under60++;
                } else if (day > 60 && day <= 90) {
                    under90++;
                } else if (day > 90) {
                    over90++;
                }
            }
            if (kmsg.getRecordTotalCount() > 0) {
                result = "{\"success\":\"true\",\"data\":[";

                result += "{\"name\":\"0 - 30 Days Old\",\"value\":\"" + under30 + "\"},";
                result += "{\"name\":\"31 - 60 Days Old\",\"value\":\"" + under60 + "\"},";
                result += "{\"name\":\"61 - 90 Days Old\",\"value\":\"" + under90 + "\"},";
                result += "{\"name\":\"Greater than 90 Days Old\",\"value\":\"" + over90 + "\"}";
                result += "],\"count\":\"4\"}";
            } else {
                result = "{\"success\":\"true\",\"data\":[],\"count\":\"0\"}";
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return result;
    }

    private String getLeadsByIndustry(HttpServletRequest request,HttpServletResponse response) throws ServiceException {
        String result = null;
        JSONObject commData = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("industryid", StringUtil.checkForNull(request.getParameter("filterCombo")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = leadReportDAOObj.leadsByIndustryReport(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            commData.put("totalCount", kmsg.getRecordTotalCount());
            while (ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("primary", obj.getLastname());
                tmpObj.put("desc", "Type : " + crmLeadHandler.getLeadTypeName(obj.getType()));
                tmpObj.put("secondary", "Industry : " + (obj.getCrmCombodataByIndustryid() != null ? obj.getCrmCombodataByIndustryid().getValue() : " Undefined "));
                jarr.put(tmpObj);
            }
            commData.put("data", jarr);
            commData.put("success", true);
            result = commData.toString();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }


private String opportunityByProduct(HttpServletRequest request) throws ServiceException, JSONException {

        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        String result = "";
        int totalCount = 0;
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.validflag");filter_values.add(1);
            filter_names.add("c.isarchive");filter_values.add(false);

            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.productname");
            order_type.add("asc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            if(!heirarchyPerm){
                filter_names.add("INc.usersByUserid.userID");filter_values.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("companyid", companyid);
            kmsg =crmProductControllerObj.getcrmProductDAO().getAllProducts(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            int size =  kmsg.getRecordTotalCount();
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            while (ite.hasNext()) {
                CrmProduct obj = (CrmProduct) ite.next();
                String name = obj.getProductname();
                kmsg =crmOpportunityReportControllerObj.getcrmOpportunityReportDAO().getOpportunityByProductChart(obj.getProductid(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    if (size < 10) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("name", name);
                        tempObj.put("value", kmsg.getRecordTotalCount());
                        jArray.put(tempObj);
                        totalCount++;
                    } else {
                        arrname[totalCount] = name;
                        String fv =String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                        arrvalue[totalCount] = Float.parseFloat(fv);
                        totalCount++;
                    }
                }
            }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("success", true);
            jFinal.put("count", jArray.length());
            result = jFinal.toString();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }
//    //getLeadsByIndustryData
//    private String getLeadsbyIndustryChart(HttpServletRequest request,HttpServletResponse response, String comboname) throws ServiceException, JSONException {
//        JSONObject jFinal = new JSONObject();
//        JSONArray jArray = new JSONArray();
//        String result = "";
//        int totalCount = 0;
//        KwlReturnObject kmsg = null;
//        try {
//            String companyid = sessionHandlerImpl.getCompanyid(request);
//            String userid = sessionHandlerImpl.getUserid(request);
//            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
//            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            requestParams.put("companyid", companyid);
//            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
//            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
//            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
//            requestParams.put("heirarchyPerm", heirarchyPerm);
//            ArrayList filter_names = new ArrayList();
//            ArrayList filter_params = new ArrayList();
//            ArrayList order_by =  new ArrayList();
//            ArrayList order_type =  new ArrayList();
//            filter_names.add("d.company.companyID");
//            filter_params.add(companyid);
//            if(comboname.equalsIgnoreCase("Lead Source")) {
//                filter_names.add("d.validflag");
//                filter_params.add(1);
//                order_by.add("d.crmCombomaster.comboname");
//                order_type.add("desc");
//            }
//            order_by.add("d.value");
//            order_type.add("asc");
//            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
//            comboRequestParams.put("filter_names", filter_names);
//            comboRequestParams.put("filter_params", filter_params);
//            comboRequestParams.put("order_by", order_by);
//            comboRequestParams.put("order_type", order_type);
//            List ll = crmManagerControllerObj.getcrmManagerDAO().getComboData(comboname, comboRequestParams);
//            int size =  ll.size();
//            String[] arrname = new String[size];
//            Float[] arrvalue = new Float[size];
//            Iterator ite = ll.iterator();
//            while (ite.hasNext()) {
//                DefaultMasterItem crmCombodata = (DefaultMasterItem) ite.next();
//                String name = crmCombodata.getValue();
//                //String Jsondata = crmReports.getLeadsByIndustryChart(jarr.getJSONObject(j).get("name").toString(), session, request);
//                kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getLeadsByIndustryChart(name, requestParams, usersList);
//                if (kmsg.getRecordTotalCount() > 0) {
//                    if (size < 10) {
//                        JSONObject tempObj = new JSONObject();
//                        tempObj.put("name", name);
//                        tempObj.put("value", kmsg.getRecordTotalCount());
//                        jArray.put(tempObj);
//                        totalCount++;
//                    } else {
//                        arrname[totalCount] = name;
//                        String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
//                        arrvalue[totalCount] = Float.parseFloat(fv);
//                        totalCount++;
//                    }
//                }
//            }
//            kmsg =crmLeadReportControllerObj.getcrmLeadReportDAO().getLeadsByIndustryChart("Undefined", requestParams, usersList);
//            if (kmsg.getRecordTotalCount() > 0) {
//                if (size < 10) {
//                    JSONObject tempObj = new JSONObject();
//                    tempObj.put("name", "Undefined");
//                    tempObj.put("value", kmsg.getRecordTotalCount());
//                    jArray.put(tempObj);
//                    totalCount++;
//                } else {
//                    arrname[totalCount] = "Undefined";
//                    String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
//                    arrvalue[totalCount] = Float.parseFloat(fv);
//                    totalCount++;
//                }
//            }
//            if (size > 10) {
//                jArray = getData(arrname, arrvalue, totalCount);
//                jFinal.put("data", jArray);
//            } else {
//                jFinal.put("data", jArray);
//            }
//            jFinal.put("success", true);
//            jFinal.put("count", jArray.length());
//            result = jFinal.toString();
//        } catch (Exception ex) {
//            logger.warn(ex.getMessage(), ex);
//            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
//        }
//        return result;
//    }

    private String getHighPriorityActivityChart(HttpServletRequest request, String comboname) throws ServiceException, JSONException, SessionExpiredException, ServletException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList =crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboname.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            List ll = crmManagerControllerObj.getcrmManagerDAO().getComboData(comboname, comboRequestParams);
            int size =  ll.size();
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                DefaultMasterItem crmCombodata = (DefaultMasterItem) ite.next();
                String name = crmCombodata.getValue();
                //String Jsondata = crmReports.getHighPriorityActivityChart(jarr.getJSONObject(j).get("name").toString(), session, request);
                if (!name.equals("None")) {
                    kmsg=crmActivityReportControllerObj.getcrmActivityReportDAO().getHighPriorityActivityChart(name, requestParams, usersList);
                    if (kmsg.getRecordTotalCount() > 0) {

                        if (size < 10) {
                            JSONObject tempObj = new JSONObject();
                            tempObj.put("name", name);
                            tempObj.put("value", kmsg.getRecordTotalCount());
                            jArray.put(tempObj);
                            totalCount++;
                        } else {
                            arrname[totalCount] = name;
                            String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                            arrvalue[totalCount] = Float.parseFloat(fv);
                            totalCount++;
                        }
                    }
                }
            }

            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("count", jArray.length());
            jFinal.put("success", true);
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }  catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }


    private String getProductHighPriorityChart(HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.validflag");filter_values.add(1);
            filter_names.add("c.isarchive");filter_values.add(false);

            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.productname");
            order_type.add("asc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            if(!heirarchyPerm){
                filter_names.add("INc.usersByUserid.userID");filter_values.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("companyid", companyid);
            kmsg =crmProductControllerObj.getcrmProductDAO().getAllProducts(requestParams);
            int size =kmsg.getRecordTotalCount();
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            Iterator ite = kmsg.getEntityList().iterator();
            String productName="";
            while (ite.hasNext()) {
                CrmProduct obj = (CrmProduct) ite.next();
                productName=obj.getProductname();
                String productId=obj.getProductid();
                //String Jsondata = crmReports.getProductHighPriorityChart(jarr.getJSONObject(j).get("name").toString(), session, request);
                    kmsg=crmCaseReportControllerObj.getcrmCaseReportDAO().getProductHighPriorityChart(productId, requestParams, usersList);
                    if (kmsg.getRecordTotalCount() > 0) {
                        if (size < 10) {
                            JSONObject tempObj = new JSONObject();
                            tempObj.put("name", productName);
                            tempObj.put("value", kmsg.getRecordTotalCount());
                            jArray.put(tempObj);
                            totalCount++;
                        } else {
                            arrname[totalCount] = productName;
                            String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                            arrvalue[totalCount] = Float.parseFloat(fv);
                            totalCount++;
                        }
                    }
                }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("count", jArray.length());
            jFinal.put("success", true);
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String accountsByOwner(HttpServletRequest request) throws ServiceException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg =crmUserControllerObj.getcrmUserDAO().getOwner(companyid, userid, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            int size =kmsg.getRecordTotalCount();
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            String owner="";
            while (ite.hasNext()) {
                User obj = (User) ite.next();
                owner=obj.getFirstName() + "  " + obj.getLastName();
                //String Jsondata = crmReports.getAccountsByOwnerChart(jarr.getJSONObject(j).get("id").toString(), session, request);
                kmsg =crmAccountReportControllerObj.getcrmAccountReportDAO().getAccountsByOwnerChart(obj.getUserID(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    if (size < 10) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("name", owner);
                        tempObj.put("value", kmsg.getRecordTotalCount());
                        jArray.put(tempObj);
                        totalCount++;
                    } else {
                        arrname[totalCount] = owner;
                        String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                        arrvalue[totalCount] = Float.parseFloat(fv);
                        totalCount++;
                    }
                }
            }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("success", true);
            jFinal.put("count", jArray.length());
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }
    private String getTargetOwnerPie(HttpServletRequest request) throws ServiceException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg =crmUserControllerObj.getcrmUserDAO().getOwner(companyid, userid, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            int size =kmsg.getRecordTotalCount();
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            String owner="";
            while (ite.hasNext()) {
                User obj = (User) ite.next();
                owner=obj.getFirstName() + "  " + obj.getLastName();
                kmsg =crmTargetReportControllerObj.getcrmTargetReportDAO().getTargetOwnerChart(obj.getUserID(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    if (size < 10) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("name", owner);
                        tempObj.put("value", kmsg.getRecordTotalCount());
                        jArray.put(tempObj);
                        totalCount++;
                    } else {
                        arrname[totalCount] = owner;
                        String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                        arrvalue[totalCount] = Float.parseFloat(fv);
                        totalCount++;
                    }
                }
            }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("success", true);
            jFinal.put("count", jArray.length());
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getKeyAccountsPie( HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        String result = "";
        KwlReturnObject kmsg = null;
        JSONObject tempObj=null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg =crmAccountReportControllerObj.getcrmAccountReportDAO().getKeyAccountschart(requestParams, usersList);
            Iterator ite = kmsg.getEntityList().iterator();
            double revenue;
            while (ite.hasNext()) {
                CrmAccount obj = (CrmAccount) ite.next();
                revenue = 0.0;
                if (!StringUtil.isNullOrEmpty(obj.getRevenue())) {
                    revenue = Double.parseDouble(obj.getRevenue());
                }
                if(revenue != 0.0){
                    tempObj = new JSONObject();
                    tempObj.put("name", obj.getAccountname());
                    tempObj.put("value", revenue);//jobj.getString("Count"));
                    jArray.put(tempObj);
                    totalCount++;
                }
            }
            jFinal.put("data", jArray);
            jFinal.put("success", true);
            jFinal.put("count", totalCount);
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }  catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }
    private String getMonthlyChartPie(HttpServletRequest request,String chart) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        String result = "";
        KwlReturnObject kmsg = null;
        try {

            int chartCase=Constants.iphone.get(chart);

            String companyid = sessionHandlerImpl.getCompanyid(request);
            Calendar cal=Calendar.getInstance();
            String year = StringUtil.isNullOrEmpty(request.getParameter("year"))?""+cal.get(Calendar.YEAR):request.getParameter("year");
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("year", year);
            String module="";
             switch(chartCase) {
                 case 18 :
                        module="Account";
                    break;
                 case 22 :
                        module="Cases";
                    break;
             }
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, module);
            requestParams.put("heirarchyPerm", heirarchyPerm);

            String monthArray[] = {"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            int size = monthArray.length;
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            for (int j = 1; j <= 12; j++) {
                 switch(chartCase) {
                     case 18 :
                        kmsg =crmAccountReportControllerObj.getcrmAccountReportDAO().getMonthlyAccountschart(j, requestParams, usersList);
                        break;
                     case 22 :
                        kmsg =crmCaseReportControllerObj.getcrmCaseReportDAO().getMonthlyCasesChart(j, requestParams, usersList);
                        break;
                 }
                if (kmsg.getRecordTotalCount() > 0) {
                    arrname[totalCount] = monthArray[j];
                    String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                    arrvalue[totalCount] = Float.parseFloat(fv);
                    totalCount++;
                }
            }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("count", jArray.length());
            jFinal.put("success", true);
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        } catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getAccountHighPriorityChart( HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        String result = "";
        KwlReturnObject kmsg = null;
        try {

            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.validflag");
            filter_params.add(1);
            filter_names.add("c.isarchive");
            filter_params.add(false);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            if(!heirarchyPerm) {
                filter_names.add("INao.usersByUserid.userID");
                filter_params.add(usersList);
            }

            kmsg = crmAccountControllerObj.getcrmAccountDAO().getAllAccounts(requestParams, filter_names, filter_params);
            Iterator ite = kmsg.getEntityList().iterator();
            int size =kmsg.getRecordTotalCount();
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            while (ite.hasNext()) {
                CrmAccount obj = (CrmAccount) ite.next();
                //String Jsondata = crmReports.getAccountHighPriorityChart(jarr.getJSONObject(j).get("name").toString(), session, request);
                kmsg =crmCaseReportControllerObj.getcrmCaseReportDAO().getAccountHighPriorityChart(obj.getAccountid(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    if (size < 10) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("name", obj.getAccountname());
                        tempObj.put("value", kmsg.getRecordTotalCount());
                        jArray.put(tempObj);
                        totalCount++;
                    } else {
                        arrname[totalCount] = obj.getAccountname();
                        String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                        arrvalue[totalCount] = Float.parseFloat(fv);
                        totalCount++;
                    }
                }
            }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("success", true);
            jFinal.put("count", jArray.length());
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }  catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }
    private String getAccountCasesPie( HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        try {

            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            kmsg = crmCaseReportControllerObj.getcrmCaseReportDAO().getAccountCasesChart(requestParams, usersList);
            jArray = chartServiceObj.getPieChartJson(kmsg.getEntityList());

            jFinal.put("data", jArray);
            jFinal.put("success", true);
            jFinal.put("count", jArray.length());
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }  catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getAccountOpportunityPie( HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        String result = "";
        KwlReturnObject kmsg = null;
        try {

            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.validflag");
            filter_params.add(1);
            filter_names.add("c.isarchive");
            filter_params.add(false);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            if(!heirarchyPerm) {
                filter_names.add("INao.usersByUserid.userID");
                filter_params.add(usersList);
            }

            kmsg = crmAccountControllerObj.getcrmAccountDAO().getAllAccounts(requestParams, filter_names, filter_params);
            Iterator ite = kmsg.getEntityList().iterator();
            int size =kmsg.getRecordTotalCount();
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            while (ite.hasNext()) {
                CrmAccount obj = (CrmAccount) ite.next();
                //String Jsondata = crmReports.getAccountHighPriorityChart(jarr.getJSONObject(j).get("name").toString(), session, request);
                kmsg =crmOpportunityReportControllerObj.getcrmOpportunityReportDAO().getAccountOpportunityChart(obj.getAccountid(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    if (size < 10) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("name", obj.getAccountname());
                        tempObj.put("value", kmsg.getRecordTotalCount());
                        jArray.put(tempObj);
                        totalCount++;
                    } else {
                        arrname[totalCount] = obj.getAccountname();
                        String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                        arrvalue[totalCount] = Float.parseFloat(fv);
                        totalCount++;
                    }
                }
            }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("success", true);
            jFinal.put("count", jArray.length());
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }  catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private String getAccountContactPie( HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        String result = "";
        KwlReturnObject kmsg = null;
        try {

            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.validflag");
            filter_params.add(1);
            filter_names.add("c.isarchive");
            filter_params.add(false);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            if(!heirarchyPerm) {
                filter_names.add("INao.usersByUserid.userID");
                filter_params.add(usersList);
            }

            kmsg = crmAccountControllerObj.getcrmAccountDAO().getAllAccounts(requestParams, filter_names, filter_params);
            Iterator ite = kmsg.getEntityList().iterator();
            int size =kmsg.getRecordTotalCount();
            String[] arrname = new String[size+1];
            Float[] arrvalue = new Float[size+1];
            while (ite.hasNext()) {
                CrmAccount obj = (CrmAccount) ite.next();
                //String Jsondata = crmReports.getAccountContactPie(jarr.getJSONObject(j).get("name").toString(), session, request);
                kmsg =crmContactReportControllerObj.getcrmContactReportDAO().getAccountContactChart(obj.getAccountid(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    if (size < 10) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("name", obj.getAccountname());
                        tempObj.put("value", kmsg.getRecordTotalCount());
                        jArray.put(tempObj);
                        totalCount++;
                    } else {
                        arrname[totalCount] =obj.getAccountname();
                        String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                        arrvalue[totalCount] = Float.parseFloat(fv);
                        totalCount++;
                    }
                }
            }

            kmsg =crmContactReportControllerObj.getcrmContactReportDAO().getAccountContactChart("Undefined", requestParams, usersList);
            if (kmsg.getRecordTotalCount() > 0) {
                if (size < 10) {
                    JSONObject tempObj = new JSONObject();
                    tempObj.put("name", "Undefined");
                    tempObj.put("value",kmsg.getRecordTotalCount());
                    jArray.put(tempObj);
                    totalCount++;
                } else {
                    arrname[totalCount] = "Undefined";
                    String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                    arrvalue[totalCount] = Float.parseFloat(fv);
                    totalCount++;
                }
            }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("success", true);
            jFinal.put("count", jArray.length());
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }  catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }
    private String getContactCasePie( HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        JSONObject jFinal = new JSONObject();
        JSONArray jArray = new JSONArray();
        int totalCount = 0;
        String result = "";
        KwlReturnObject kmsg = null;
        try {

            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerControllerObj.getcrmManagerDAO().recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.validflag");
            filter_params.add(1);
            filter_names.add("c.isarchive");
            filter_params.add(false);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            if(!heirarchyPerm){
                filter_names.add("INco.usersByUserid.userID");filter_params.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            kmsg = crmContactControllerObj.getcrmContactDAO().getAllContacts(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            int size =kmsg.getRecordTotalCount();
            String[] arrname = new String[size];
            Float[] arrvalue = new Float[size];
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();
                kmsg =crmCaseReportControllerObj.getcrmCaseReportDAO().getContactCaseChart(obj.getContactid(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    if (size < 10) {
                        JSONObject tempObj = new JSONObject();
                        tempObj.put("name", obj.getFirstname()+" "+obj.getLastname());
                        tempObj.put("value", kmsg.getRecordTotalCount());
                        jArray.put(tempObj);
                        totalCount++;
                    } else {
                        arrname[totalCount] = obj.getFirstname()+" "+obj.getLastname();
                        String fv = String.valueOf(kmsg.getRecordTotalCount()).concat("." + totalCount + "");
                        arrvalue[totalCount] = Float.parseFloat(fv);
                        totalCount++;
                    }
                }
            }
            if (size > 10) {
                jArray = getData(arrname, arrvalue, totalCount);
                jFinal.put("data", jArray);
            } else {
                jFinal.put("data", jArray);
            }
            jFinal.put("success", true);
            jFinal.put("count", jArray.length());
            result = jFinal.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }  catch (NumberFormatException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }
        return result;
    }

    private JSONArray getData(String[] arrName, Float[] value, int cnt) {
        JSONArray jarr = null;
        try {
            jarr = new JSONArray();
            Float [] vArr = new Float[cnt];
            float divisor=1;int temp=cnt;
            while(temp > 0){
               temp=temp/10;
               divisor=divisor*10;
            }
            String[] nArr = new String[cnt];
            for (int i = 0; i < cnt; i++) {
                int ind = value[i].toString().indexOf(".");
                float cvalue = Float.parseFloat(value[i].toString().substring(0, ind));
                float lval =  (float) (cvalue + (float)(i/divisor));
                vArr[i] =  lval;
                nArr[i] = arrName[i];
            }
            Arrays.sort(vArr, Collections.reverseOrder());
            int otherValue = 0;
            for (int i = 0; i < cnt; i++) {
                JSONObject jobj = new JSONObject();
                float cval = Float.parseFloat(vArr[i].toString().substring(0, vArr[i].toString().indexOf(".")));
                int index = Math.round((vArr[i] - cval) * divisor);
                int  cvalue = (int) cval;
                if (cnt == 10) {
                    jobj.put("name", nArr[index]);
                    jobj.put("value", cvalue);
                    jarr.put(jobj);
                }else if (i < 9) {
                    jobj.put("name", nArr[index]);
                    jobj.put("value", cvalue);
                    jarr.put(jobj);
                }else {
                    otherValue = otherValue + cvalue;
                    if (i == (cnt - 1)) {
                        jobj.put("name", "Others");
                        jobj.put("value", otherValue);
                        jarr.put(jobj);
                    }
                }
            }
       } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            String exname = ex.toString();
        }
        return jarr;
    }

   private JSONObject getCampaign(HttpServletRequest request,HttpServletResponse response) throws ServiceException, JSONException {
   JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", request.getParameter("isarchive"));
            if(request.getParameter("searchJson") != null && !StringUtil.isNullOrEmpty(request.getParameter("searchJson"))) {
                requestParams.put("searchJson", request.getParameter("searchJson"));
            }
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            if (request.getParameter("config") != null) {
                requestParams.put("config", request.getParameter("config"));
            }

            requestParams.put("export", request.getParameter("reportid"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(request.getParameter("field"))) {
                requestParams.put("field", request.getParameter("field"));
                requestParams.put("direction", request.getParameter("direction"));
            }
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));

            KwlReturnObject kmsg = crmCampaignDAOObj.getCampaigns(requestParams, usersList);
            jobj.put("totalCount", kmsg.getRecordTotalCount());
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmCampaign obj = (CrmCampaign) ite.next();
                JSONObject j = new JSONObject();

                j.put("title", StringUtil.checkForNull(obj.getCampaignname()));
              //Remove following comment after timezone related changes in campaign..
//                j.put("primary", "Start date : " + crmManagerDAOObj.preferenceDate(request, new Date(obj.getStartingdate()), 0));
//                j.put("secondary", "End date : " + crmManagerDAOObj.preferenceDate(request, new Date(obj.getEndingdate()), 0));
                j.put("row0", "Objective : " + obj.getObjective());
                j.put("row1", "Owner : " + obj.getUsersByUserid().getFirstName() + "" + obj.getUsersByUserid().getLastName());
              //Remove following comment after timezone related changes in campaign..
//                j.put("row2", "Start date : " + crmManagerDAOObj.preferenceDate(request, new Date(obj.getStartingdate()), 0));
//                j.put("row3", "End date : " + crmManagerDAOObj.preferenceDate(request, new Date(obj.getEndingdate()), 0));// crmReports.preferenceDate(session, request, obj.getEndindate(), 0));
                j.put("row4", "Type : " + (obj.getCrmCombodataByCampaigntypeid() != null ? obj.getCrmCombodataByCampaigntypeid().getValue() : ""));
                j.put("row5", "Status : " + (obj.getCrmCombodataByCampaignstatusid() != null ? obj.getCrmCombodataByCampaignstatusid().getValue() : ""));
                j.put("row6", "Response : " + StringUtil.checkForNull(obj.getExpectedresponse()));
                jarr.put(j);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("rowCount", "7");
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while retreiving  campaign data (" + ex.toString() + ")");
        }
        return jobj;
    }

    private String getUserDetails(HttpServletRequest request) throws ServiceException {
        String result = null;
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String lid = request.getParameter("lid");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("u.company.companyID");
            filter_params.add(sessionHandlerImpl.getCompanyid(request));
            filter_names.add("u.deleteflag");
            filter_params.add(0);
            if (StringUtil.isNullOrEmpty(lid) == false) {
                filter_names.add("u.userID");
                filter_params.add(lid);
            }
            if (StringUtil.isNullOrEmpty(start) == false && StringUtil.isNullOrEmpty(limit) == false) {
                requestParams.put("start", start);
                requestParams.put("limit", limit);
            }
            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);

            Iterator itr = kmsg.getEntityList().iterator();
            JSONArray jArr = new JSONArray();
            while (itr.hasNext()) {
                User user = (User) itr.next();
                UserLogin ul = user.getUserLogin();
                JSONObject obj = new JSONObject();
                obj.put("userid", user.getUserID());
                obj.put("username", ul.getUserName());
                obj.put("fname", user.getFirstName());
                obj.put("lname", user.getLastName());
                obj.put("image", user.getImage());
                obj.put("emailid", user.getEmailID());
                obj.put("lastlogin", (ul.getLastActivityDate() == null ? "" : authHandler.getDateFormatter(timeFormatId, timeZoneDiff).format(ul.getLastActivityDate())));
                obj.put("aboutuser", user.getAboutUser());
                obj.put("address", user.getAddress());
                obj.put("contactno", user.getContactNumber());
                obj.put("formatid", (user.getDateFormat() == null ? "" : user.getDateFormat().getFormatID()));
                obj.put("tzid", (user.getTimeZone() == null ? "23" : user.getTimeZone().getTimeZoneID())); // 23 is id of New York Time Zone. [default]
                obj.put("callwithid", user.getCallwith());
                obj.put("timeformat", (user.getTimeformat() != 1 && user.getTimeformat() != 2) ? 2 : user.getTimeformat()); // 2 is id for '24 hour timeformat'. [default]
                String roleStr = "";
                kmsg = permissionHandlerDAOObj.getRoleofUser(user.getUserID());
                Iterator ite3 = kmsg.getEntityList().iterator();
                while(ite3.hasNext()) {
                    Object[] row = (Object[]) ite3.next();
                    String roleid = row[0].toString();
                    if (roleid.equals(Constants.COMPANY_ADMIN)) {
                        roleStr = roleStr + " Company Admin,";
                    }
                    if (roleid.equals(Constants.COMPANY_SALES_MANAGER)) {
                        roleStr = roleStr + " Sales Manager,";
                    }
                    if (roleid.equals(Constants.COMPANY_SALES_EXECUTIVE)) {
                        roleStr = roleStr + " Sales Executive,";
                    }
                }
                int len = roleStr.length() - 1;
                if (len > 0) {
                    roleStr = roleStr.substring(0, len);
                }
                obj.put("roles", roleStr);
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            result = jobj.toString();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":\"false\",\"error\":\"Error occured while retreiving data(" + ex.toString() + ")\",\"data\":[]}";
        }

        return result;
    }

    private JSONObject getAllAccounts(HttpServletRequest request,HttpServletResponse response)
            throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        try {
            ModelAndView model=crmContactControllerObj.getAllAccounts(request, response);
            jobj =  new JSONObject(model.getModel().get("model").toString());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while retrieving the info.(" + e.toString() + ")");
        }
        return jobj;
    }

    private JSONObject getLead(HttpServletRequest request,HttpServletResponse response)
            throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", request.getParameter("isarchive"));
            if(request.getParameter("searchJson") != null && !StringUtil.isNullOrEmpty(request.getParameter("searchJson"))) {
                requestParams.put("searchJson", request.getParameter("searchJson"));
            }
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("transfered", request.getParameter("transfered"));
            requestParams.put("isconverted", request.getParameter("isconverted"));
//            requestParams.put("email", true);
            if (request.getParameter("config") != null) {
                requestParams.put("config", request.getParameter("config"));
            }

            requestParams.put("export", request.getParameter("reportid"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(request.getParameter("field"))) {
                requestParams.put("field", request.getParameter("field"));
                requestParams.put("direction", request.getParameter("direction"));
            }
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));

            kmsg = crmLeadDAOObj.getLeads(requestParams, usersList);
            jobj.put("totalCount", kmsg.getRecordTotalCount());
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                String[] ownerInfo=crmLeadHandler.getAllLeadOwners(crmLeadDAOObj, obj.getLeadid());
                JSONObject tmpObj = new JSONObject();

                tmpObj.put("title", obj.getLastname());
                tmpObj.put("primary", "Status : " + (obj.getCrmCombodataByLeadstatusid() != null ? obj.getCrmCombodataByLeadstatusid().getValue() : ""));
                tmpObj.put("secondary", "Type : " + crmLeadHandler.getLeadTypeName(obj.getType()));
                tmpObj.put("row0", "First Name : " + StringUtil.checkForNull(obj.getFirstname()));
                tmpObj.put("row1", "Last Name : " + StringUtil.checkForNull(obj.getLastname()));
                tmpObj.put("row2", "Email : " + StringUtil.checkForNull(obj.getEmail()));
//                tmpObj.put("row3", "Contact No. : " + (obj.getPhone() != null ? obj.getPhone() : ""));
                tmpObj.put("row3", "Owner : " + ownerInfo[5]);
                tmpObj.put("row4", "Creation date : " + crmManagerCommon.dateNull(obj.getCreatedon()));
                tmpObj.put("row5", "Title : " + (obj.getTitle() != null ? obj.getTitle() : ""));
                tmpObj.put("row6", "Industry : " + (obj.getCrmCombodataByIndustryid() != null ? obj.getCrmCombodataByIndustryid().getValue() : ""));
                tmpObj.put("row7", "Rating : " + (obj.getCrmCombodataByRatingid() != null ? obj.getCrmCombodataByRatingid().getValue() : ""));
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("rowCount", "7");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while retrieving the info.(" + e.toString() + ")");
        }
        return jobj;
    }
    private JSONObject getContact(HttpServletRequest request,HttpServletResponse response) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
       try{
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            boolean archive = Boolean.parseBoolean(request.getParameter("isarchive"));
            String companyid = sessionHandlerImpl.getCompanyid(request);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
           if (request.getParameter("flag") != null) {
               if (Integer.parseInt(request.getParameter("flag")) == 60) {
                   String relatedName = request.getParameter("relatedName");
                   if ((!StringUtil.isNullOrEmpty(relatedName) && relatedName.equals("Lead")) || (!StringUtil.isNullOrEmpty(request.getParameter("name")) && request.getParameter("name").equals("LeadContact"))) {
                       filter_names.add("c.Lead.leadid");
                   } else {
                       filter_names.add("c.crmAccount.accountid");
                   }
                   filter_params.add(request.getParameter("mapid"));
               }
           }
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.isarchive");
            filter_params.add(archive);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", request.getParameter("isarchive"));
            if(request.getParameter("searchJson") != null && !StringUtil.isNullOrEmpty(request.getParameter("searchJson"))) {
                requestParams.put("searchJson", request.getParameter("searchJson"));
            }
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            if(request.getParameter("config") != null) {
                requestParams.put("config", request.getParameter("config"));
            }

            requestParams.put("export", request.getParameter("reportid"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(request.getParameter("field"))) {
                requestParams.put("field", request.getParameter("field"));
                requestParams.put("direction", request.getParameter("direction"));
            }
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));

            kmsg = crmContactDAOObj.getContacts(requestParams, usersList, filter_names, filter_params);
            jobj.put("totalCount", kmsg.getRecordTotalCount());
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();
                JSONObject j = new JSONObject();
                String[] ownerInfo=crmContactHandler.getAllContactOwners(crmContactDAOObj, obj.getContactid());
                j.put("title", StringUtil.checkForNull(obj.getFirstname()).concat(" ").concat(StringUtil.checkForNull(obj.getLastname())));
                j.put("primary", "Owner : " + ownerInfo[5]);
                j.put("secondary", "Account name : " + (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : "" ));
                j.put("row0", "First Name :" + StringUtil.checkForNull(obj.getFirstname()));
                j.put("row1", "Last Name :" + StringUtil.checkForNull(obj.getLastname()));
                j.put("row2", "Account Name :" + (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : "" ));
                j.put("row3", "Email : " + StringUtil.checkForNull(obj.getEmail()));
                j.put("row4", "Contact No : " + StringUtil.checkForNull(obj.getPhoneno()));
                j.put("row5", "Title : " + (obj.getTitle() != null ? obj.getTitle() : ""));
                j.put("row6", "Address : " + StringUtil.checkForNull(obj.getMailstreet()));
                j.put("row7", "Lead source : " + (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                j.put("firstname", StringUtil.checkForNull(obj.getFirstname()));
                j.put("lastname", StringUtil.checkForNull(obj.getLastname()));
                jarr.put(j);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("rowCount", "7");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while retrieving the info.(" + e.toString() + ")");
        }
        return jobj;
    }

    private JSONObject getAccount(HttpServletRequest request,HttpServletResponse response) throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", request.getParameter("isarchive"));
            if(request.getParameter("searchJson") != null && !StringUtil.isNullOrEmpty(request.getParameter("searchJson"))) {
                requestParams.put("searchJson", request.getParameter("searchJson"));
            }
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            if (request.getParameter("config") != null) {
                requestParams.put("config", request.getParameter("config"));
            }

            requestParams.put("export", request.getParameter("reportid"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(request.getParameter("field"))) {
                requestParams.put("field", request.getParameter("field"));
                requestParams.put("direction", request.getParameter("direction"));
            }
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));

            kmsg = crmAccountDAOObj.getAccounts(requestParams, usersList);
            jobj.put("totalCount", kmsg.getRecordTotalCount());
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmAccount aCrmAccount = (CrmAccount) ite.next();
                JSONObject tmpObj = new JSONObject();
                String[] ownerInfo=crmAccountHandler.getAllAccOwners(crmAccountDAOObj, aCrmAccount.getAccountid());
                tmpObj.put("row0", "Account Name : " + aCrmAccount.getAccountname());
                tmpObj.put("row1", "Account owner : " + ownerInfo[5]);
                tmpObj.put("row2", "Email : " + aCrmAccount.getEmail());
                tmpObj.put("title", aCrmAccount.getAccountname());
//                tmpObj.put("row6", "Website : " + (aCrmAccount.getWebsite() != null ? aCrmAccount.getWebsite() : ""));
                tmpObj.put("row3", "Contact No. : " + StringUtil.checkForNull(aCrmAccount.getPhone()));
                String value = "0";
                if(!StringUtil.isNullOrEmpty(aCrmAccount.getRevenue())) {
                    value = crmManagerDAOObj.currencyRender(aCrmAccount.getRevenue(),sessionHandlerImpl.getCurrencyID(request));
                }
                if (!StringUtil.isNullOrEmpty(value) ){
                    if (value.contains("&#36;")) {
                        value = value.replace("&#36;", "$");
                    }
                    tmpObj.put("row4", "Revenue : " + value);
                    tmpObj.put("primary", "Revenue : " + value);
                } else {
                    tmpObj.put("row4", "Revenue : ");
                    tmpObj.put("primary", "Revenue : ");
                }
//                tmpObj.put("row4", "Product : " + (aCrmAccount.getCrmProduct() != null ? aCrmAccount.getCrmProduct().getProductname() : ""));
                tmpObj.put("row5", "Type : " + (aCrmAccount.getCrmCombodataByAccounttypeid() != null ? aCrmAccount.getCrmCombodataByAccounttypeid().getValue() : ""));
                tmpObj.put("secondary", "Creation date : " + crmManagerCommon.dateNull(aCrmAccount.getCreatedon()));
                tmpObj.put("row6", "Industry : " + (aCrmAccount.getCrmCombodataByIndustryid() != null ? aCrmAccount.getCrmCombodataByIndustryid().getValue() : ""));
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("rowCount", "7");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while retrieving the info.(" + e.toString() + ")");
        }
        return jobj;
    }
    private JSONObject getCase(HttpServletRequest request,HttpServletResponse response)
            throws ServiceException, JSONException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
       try{
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            boolean archive = Boolean.parseBoolean(request.getParameter("isarchive"));
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if(request.getParameter("flag") != null){
                if(Integer.parseInt(request.getParameter("flag")) == 64) {
                    filter_names.add("c.crmAccount.accountid");
                    filter_params.add(request.getParameter("mapid"));
                }
            }
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.isarchive");
            filter_params.add(archive);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", request.getParameter("isarchive"));
            if(request.getParameter("searchJson") != null && !StringUtil.isNullOrEmpty(request.getParameter("searchJson"))) {
                requestParams.put("searchJson", request.getParameter("searchJson"));
            }
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            if(request.getParameter("config") != null) {
                requestParams.put("config", request.getParameter("config"));
            }

            requestParams.put("export", request.getParameter("reportid"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(request.getParameter("field"))) {
                requestParams.put("field", request.getParameter("field"));
                requestParams.put("direction", request.getParameter("direction"));
            }
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            requestParams.put("userlist", usersList);
            kmsg = crmCaseDAOObj.getCases(requestParams);
            jobj.put("totalCount", kmsg.getRecordTotalCount());
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("title", StringUtil.checkForNull(obj.getSubject()));//obj.getSubject());
                tmpObj.put("row3", "Account name : " + (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("row4", "Contact name : " + (obj.getCrmContact() != null ? obj.getCrmContact().getFirstname() + " " + obj.getCrmContact().getLastname() : ""));
//                tmpObj.put("row3", "Type : " + (obj.getCrmCombodataByCasetypeid() != null ? obj.getCrmCombodataByCasetypeid().getValue() : ""));
                tmpObj.put("row1", "Status : " + (obj.getCrmCombodataByCasestatusid() != null ? obj.getCrmCombodataByCasestatusid().getValue() : ""));
                tmpObj.put("secondary", "Status : " + (obj.getCrmCombodataByCasestatusid() != null ? obj.getCrmCombodataByCasestatusid().getValue() : ""));
                tmpObj.put("row2", "Priority : " + (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                tmpObj.put("primary", "Priority : " + (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                tmpObj.put("row0", "Subject : " + StringUtil.checkForNull(obj.getSubject()));
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("rowCount", "6");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while retrieving the info.(" + e.toString() + ")");
        }
        return jobj;
    }

    private String getModules(HttpServletRequest request) {
        String result = null;
        result = "{\"data\":[";
        String addFlag = request.getParameter("add");
        boolean flag = false;
        if (!StringUtil.isNullOrEmpty(addFlag)) {
            flag = Boolean.parseBoolean(addFlag);
        }
        try {

            if (StringUtil.isNullOrEmpty(addFlag) || (!flag)) {
                if ((sessionHandlerImpl.getPerms(request, ProjectFeature.campaignFName) & 2) == 2) {
                    result += "{\"name\" : \"Campaign\", \"view\" : \"true\",\"moduleid\":\"12\" },";
                } else {
                    result += "{\"name\" : \"Campaign\", \"view\" : \"false\",\"moduleid\":\"12\" },";
                }
            }

            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.leadFName) & 2) == 2) {
                result += "{\"name\" : \"Lead\", \"view\" : \"true\",\"moduleid\":\"13\" },";
            }
//            else {
//                result += "{\"name\" : \"Lead\", \"view\" : \"false\",\"moduleid\":\"13\" },";
//            }

            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.contactFName) & 2) == 2) {
                result += "{\"name\" : \"Contact\", \"view\" : \"true\",\"moduleid\":\"14\" },";
            }
//            else {
//                result += "{\"name\" : \"Contact\", \"view\" : \"false\",\"moduleid\":\"14\" },";
//            }

            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.accountFName) & 2) == 2) {
                result += "{\"name\" : \"Account\", \"view\" : \"true\",\"moduleid\":\"16\" },";
            }
//            else {
//                result += "{\"name\" : \"Account\", \"view\" : \"false\",\"moduleid\":\"16\" },";
//            }

            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.caseFName) & 2) == 2) {
                result += "{\"name\" : \"Case\", \"view\" : \"true\",\"moduleid\":\"18\" },";
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.opportunityFName) & 2) == 2) {
                result += "{\"name\" : \"Opportunity\", \"view\" : \"true\",\"moduleid\":\"17\" },";
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.productFName) & 2) == 2) {
                result += "{\"name\" : \"Product\", \"view\" : \"true\",\"moduleid\":\"15\" }";
            }
//            else {
//                result += "{\"name\" : \"Case\", \"view\" : \"false\",\"moduleid\":\"18\" }";
//            }
//            if((sessionHandlerImpl.getPerms(request, "Campaign") & 2) == 2)
//                result += "{\"name\" : \"Campaign\", \"view\" : \"true\" }";
//            else
//                result += "{\"name\" : \"Campaign\", \"view\" : \"false\" }";
//            if((sessionHandlerImpl.getPerms(request, "Campaign") & 2) == 2)
//                result += "{\"name\" : \"Campaign\", \"view\" : \"true\" }";
//            else
//                result += "{\"name\" : \"Campaign\", \"view\" : \"false\" }";
//            if((sessionHandlerImpl.getPerms(request, "Campaign") & 2) == 2)
//                result += "{\"name\" : \"Campaign\", \"view\" : \"true\" }";
//            else
//                result += "{\"name\" : \"Campaign\", \"view\" : \"false\" }";
            result += "],\"success\":\"true\"}";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":\"false\",\"data\":[]}";
        }
        return result;
    }

    private void getLeadsReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "Lead Report") & 1) == 1) {
                li.add("{\"id\":\"1\",\"reportname\":\"Leads by Industry\",\"desc\":\"Monitor your leads grouped by type of industry\",\"flag\":\"7\",\"dataflag\":\"2\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "Lead Report") & 2) == 2) {
                li.add("{\"id\":\"2\",\"reportname\":\"Converted Leads\",\"desc\":\"View list of converted leads\",\"flag\":\"5\",\"dataflag\":\"21\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "Lead Report") & 4) == 4) {
                li.add("{\"id\":\"3\",\"reportname\":\"Leads by Source\",\"desc\":\"Monitor your leads grouped by corresponding source\",\"flag\":\"2\",\"dataflag\":\"1\"}");

            }
//            if ((sessionHandlerImpl.getPerms(request, "Lead Report") & 8) == 8) {
//                li.add("{\"reportname\":\"Qualified Leads\",\"desc\":\"Get the list of leads who have their status as qualified\",\"flag\":\"23\",\"dataflag\":\"48\"}");
//
//            }
            if ((sessionHandlerImpl.getPerms(request, "Lead Report") & 16) == 16) {
                li.add("{\"id\":\"4\",\"reportname\":\"Contacted Leads\",\"desc\":\"Get the list of Leads who have their status as Contacted\",\"flag\":\"26\",\"dataflag\":\"54\"}");


            }
            if ((sessionHandlerImpl.getPerms(request, "Lead Report") & 32) == 32) {
                li.add("{\"id\":\"5\",\"reportname\":\"Open Leads\",\"desc\":\"Get the list of Leads who have their status as Open\",\"flag\":\"34\",\"dataflag\":\"70\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "Lead Report") & 64) == 64) {
                li.add("{\"id\":\"6\",\"reportname\":\"Converted Leads to Account\",\"desc\":\"Get the list of Leads who are converted to Accounts\",\"flag\":\"36\",\"dataflag\":\"74\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "Lead Report") & 128) == 128) {
                li.add("{\"id\":\"7\",\"reportname\":\"Converted Leads to Opportunity\",\"desc\":\"Get the list of Leads who are converted to Opportunity\",\"flag\":\"37\",\"dataflag\":\"76\"}");

            }
//            if ((sessionHandlerImpl.getPerms(request, "Lead Report") & 256) == 256) {
//                li.add("{\"reportname\":\"Converted Leads to Contacts\",\"desc\":\"Get the list of Leads who are converted to Contacts\",\"flag\":\"38\",\"dataflag\":\"78\"}");
//            }
            if ((sessionHandlerImpl.getPerms(request, "Lead Report") & 512) == 512) {
                li.add("{\"id\":\"8\",\"reportname\":\"Leads Pipeline Report\",\"desc\":\"Get the list of Leads Pipeline data\",\"flag\":\"41\",\"dataflag\":\"86\"}");

            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private void getAccountReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "AccountReport") & 1) == 1) {
                li.add("{\"id\":\"9\",\"reportname\":\"Key Accounts\",\"desc\":\"Monitor your key accounts ordered by corresponding revenues\",\"flag\":\"3\",\"dataflag\":\"28\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "AccountReport") & 2) == 2) {
                li.add("{\"id\":\"10\",\"reportname\":\"Monthly Accounts\",\"desc\":\"Get the list of Accounts created by month\",\"flag\":\"12\",\"dataflag\":\"35\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "AccountReport") & 4) == 4) {
                li.add("{\"id\":\"11\",\"reportname\":\"Accounts Owners\",\"desc\":\"Get the list of Accounts and their respective Owners\",\"flag\":\"13\",\"dataflag\":\"17\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "AccountReport") & 8) == 8) {
                li.add("{\"id\":\"12\",\"reportname\":\"Account with High Priority Cases\",\"desc\":\"Get the list of Accounts who have Cases with High Priority and are yet to Start\",\"flag\":\"18\",\"dataflag\":\"12\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "AccountReport") & 16) == 16) {
                li.add("{\"id\":\"13\",\"reportname\":\"Industry-Account Type Report\",\"desc\":\"Get the list of Accounts and the Industry they belong\",\"flag\":\"20\",\"dataflag\":\"14\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "AccountReport") & 32) == 32) {
                li.add("{\"id\":\"14\",\"reportname\":\"Accounts with Contacts\",\"desc\":\"Get the list of Accounts who have most number of contacts\",\"flag\":\"24\",\"dataflag\":\"50\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "AccountReport") & 64) == 64) {
                li.add("{\"id\":\"15\",\"reportname\":\"Accounts with Opportunities\",\"desc\":\"Get the list of Accounts who have most number of opportunities\",\"flag\":\"28\",\"dataflag\":\"58\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "AccountReport") & 128) == 128) {
                li.add("{\"id\":\"16\",\"reportname\":\"Accounts with Cases\",\"desc\":\"Get the list of Accounts who have most number of cases\",\"flag\":\"33\",\"dataflag\":\"68\"}");

            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private void getContactReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "ContactReport") & 1) == 1) {
                li.add("{\"id\":\"17\",\"reportname\":\"Contacts with High Priority Cases\",\"desc\":\"Get the list of Products who have Cases with High Priority and are yet to Start\",\"flag\":\"16\",\"dataflag\":\"10\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "ContactReport") & 2) == 2) {
                li.add("{\"id\":\"18\",\"reportname\":\"Contacts by Lead Source\",\"desc\":\"Get the list of lead source who have Contacts\",\"flag\":\"27\",\"dataflag\":\"56\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "ContactReport") & 4) == 4) {
                li.add("{\"id\":\"19\",\"reportname\":\"Contacts with Cases\",\"desc\":\"Get the list of Contacts who have Cases\",\"flag\":\"35\",\"dataflag\":\"72\"}");

            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private void getOpportunityReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "OpportunityReport") & 1) == 1) {
                li.add("{\"id\":\"20\",\"reportname\":\"Revenue by Opportunity Source\",\"desc\":\"Monitor your opportunities grouped by type of lead source\",\"flag\":\"0\",\"dataflag\":\"25\"}");
                //     li.add("{\"update\":\"<a href=# onclick='addAllReportTab(2)' wtf:qtip=''> </a>\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "OpportunityReport") & 2) == 2) {

                li.add("{\"id\":\"21\",\"reportname\":\"Opportunities by Stage\",\"desc\":\"Monitor your opportunities grouped by corresponding stage such as qualified, closed and won\",\"flag\":\"1\",\"dataflag\":\"26\"}");
                //     li.add("{\"update\":\"<a href=# onclick='addAllReportTab(3)' wtf:qtip=''> </a>\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "OpportunityReport") & 4) == 4) {
                li.add("{\"id\":\"22\",\"reportname\":\"Closed Opportunities\",\"desc\":\"Get the list of Opportunities who are Closed-won\",\"flag\":\"8\",\"dataflag\":\"20\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "OpportunityReport") & 8) == 8) {
                li.add("{\"id\":\"23\",\"reportname\":\"Opportunities by Type\",\"desc\":\"Get the list of Opportunities with respect to their Type\",\"flag\":\"9\",\"dataflag\":\"15\"}");
            }
            if ((sessionHandlerImpl.getPerms(request, "OpportunityReport") & 16) == 16) {
                li.add("{\"id\":\"24\",\"reportname\":\"Stuck Opportunities\",\"desc\":\"Get the list of Opportunities whose Probability is less than 50%\",\"flag\":\"11\",\"dataflag\":\"18\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "OpportunityReport") & 32) == 32) {
                li.add("{\"id\":\"25\",\"reportname\":\"Sources of Opportunities\",\"desc\":\"Get the list of Opportunities with respect to their Source\",\"flag\":\"14\",\"dataflag\":\"8\"}");

            }
//            if ((sessionHandlerImpl.getPerms(request, "Opportunity Report") & 64) == 64) {
//                li.add("{\"reportname\":\"Opportunities by Lead Source<\",\"desc\":\"Get the list of lead source who have Opportunities\",\"flag\":\"29\",\"dataflag\":\"60\"}");
//            }
            if ((sessionHandlerImpl.getPerms(request, "OpportunityReport") & 128) == 128) {
                li.add("{\"id\":\"26\",\"reportname\":\"Opportunities Pipeline Report\",\"desc\":\"Get the list of Opportunities Pipeline data\",\"flag\":\"40\",\"dataflag\":\"84\"}");

            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private void getActivityReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "ActivityReport") & 1) == 1) {
                li.add("{\"id\":\"27\",\"reportname\":\"High Priority Activities\",\"desc\":\"Get the list of Activities who are of High Priority and having status as Not Started\",\"flag\":\"15\",\"dataflag\":\"9\"}");
            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private void getCaseReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "CaseReport") & 1) == 1) {
                li.add("{\"id\":\"28\",\"reportname\":\"Cases by Status\",\"desc\":\"Monitor customer cases grouped by corresponding status such as new, pending and escalated\",\"flag\":\"4\",\"dataflag\":\"3\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "CaseReport") & 2) == 2) {
                li.add("{\"id\":\"29\",\"reportname\":\"Monthly Cases\",\"desc\":\"Get the list of Cases created by month\",\"flag\":\"19\",\"dataflag\":\"42\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "CaseReport") & 4) == 4) {
                li.add("{\"id\":\"30\",\"reportname\":\"Newly Added Cases\",\"desc\":\"Get the list of Newly Added Cases\",\"flag\":\"30\",\"dataflag\":\"62\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "CaseReport") & 8) == 8) {
                li.add("{\"id\":\"31\",\"reportname\":\"Pending Cases\",\"desc\":\"Get the list of Pending Cases\",\"flag\":\"31\",\"dataflag\":\"64\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "CaseReport") & 16) == 16) {
                li.add("{\"id\":\"32\",\"reportname\":\"Escalated Cases\",\"desc\":\"Get the list of Escalated Cases\",\"flag\":\"32\",\"dataflag\":\"66\"}");

            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private void getProductReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "ProductReport") & 1) == 1) {
                li.add("{\"id\":\"33\",\"reportname\":\"Products with Cases by Priority\",\"desc\":\"Get the list of Products who have Cases with High Priority and are yet to Start\",\"flag\":\"17\",\"dataflag\":\"11\"}");

            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private void getOpportunityProductReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "OpporunityProductReport") & 1) == 1) {
                li.add("{\"id\":\"34\",\"reportname\":\"Opportunity Product Report\",\"desc\":\"Get the list of Opportunities and the Product that attracted them\",\"flag\":\"10\",\"dataflag\":\"16\"}");

            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private void getSalesReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "SalesReport") & 1) == 1) {
                li.add("{\"id\":\"35\",\"reportname\":\"Sales by Source\",\"desc\":\"Monitor your sales grouped by type of lead source\",\"flag\":\"6\",\"dataflag\":\"29\"}");

            }
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private void getCampaignReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "CampaignReport") & 1) == 1) {
                li.add("{\"id\":\"36\",\"reportname\":\"Campaigns by Type\",\"desc\":\"Get the list of Campaigns according to their type\",\"flag\":\"21\",\"dataflag\":\"44\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "CampaignReport") & 2) == 2) {
                li.add("{\"id\":\"37\",\"reportname\":\"Completed Campaigns by Type\",\"desc\":\"Get the list of Campaigns who have their status marked as complete\",\"flag\":\"22\",\"dataflag\":\"46\"}");

            }
            if ((sessionHandlerImpl.getPerms(request, "CampaignReport") & 4) == 4) {
                li.add("{\"id\":\"38\",\"reportname\":\"Campaigns with Good Response\",\"desc\":\"Get the list of Campaigns who have generated response more than 70%\",\"flag\":\"25\",\"dataflag\":\"52\"}");
            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private void getTargetReportsLink(HttpServletRequest request, List li) throws ServiceException {
        try {
            if ((sessionHandlerImpl.getPerms(request, "TargetReport") & 1) == 1) {
                li.add("{\"id\":\"39\",\"reportname\":\"Targets by Owner\",\"desc\":\"Get the list of Targets sorted in the order of their Creator\",\"flag\":\"39\",\"dataflag\":\"80\"}");

            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    private String getModuleReports(HttpServletRequest request) throws ServiceException {
        String jdata = "";
        try {
            ArrayList li = new ArrayList();
            getLeadsReportsLink(request, li);
            getAccountReportsLink(request, li);
            getContactReportsLink(request, li);
            getOpportunityReportsLink(request, li);
            getActivityReportsLink(request, li);
            getCaseReportsLink(request, li);
            getProductReportsLink(request, li);
            getOpportunityProductReportsLink(request, li);
            getSalesReportsLink(request, li);
            getCampaignReportsLink(request, li);
            getTargetReportsLink(request, li);

            int start = 0; //Integer.parseInt(request.getParameter("start"));
            int limit = 45;//Integer.parseInt(request.getParameter("limit"));
            String limitReport = request.getParameter("limitReport");
            if (!StringUtil.isNullOrEmpty(limitReport)) {
                limit = Integer.parseInt(limitReport);
            }
            limit = (start + limit) > li.size() ? li.size() : (start + limit);
            List currli = (List) li.subList(start, limit);
            Iterator it = currli.iterator();
            ArrayList newArr = new ArrayList();
            while (it.hasNext()) {
                newArr.add(it.next());
            }
            JSONObject jobj = new JSONObject("{\"count\":" + li.size() + ",\"data\":" + newArr.toString() + "}");
            jdata = jobj.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            String esc = ex.toString();
            jdata = "{'success':'false','data':[]}";
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            jdata = "{'success':'false','data':[]}";
        }
        return jdata;
    }

    private JSONObject generateAppID(HttpServletRequest request, HttpServletResponse response) throws ServiceException, JSONException {
       JSONObject jobj1 = new JSONObject();
       KwlReturnObject kmsg = null;
       try{
           ModelAndView model=authHandlerControllerObj.verifyLoginForIphone(request, response);
           JSONObject jobj =  new JSONObject(model.getModel().get("model").toString());
           jobj =jobj.getJSONObject("data");
           if (jobj.has("success") && (jobj.get("success").equals(true))) {
                String userid = jobj.getString("lid");
                String deviceid = request.getParameter("udid");
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("userid", StringUtil.checkForNull(userid));
                requestParams.put("deviceid", StringUtil.checkForNull(deviceid));
                requestParams.put("domain", StringUtil.checkForNull(URLUtil.getDomainName(request)));
                kmsg=iphoneDAOObj.generateAppID(requestParams);
                jobj1=(JSONObject) kmsg.getEntityList().get(0);
                if (jobj1.has("success") && (jobj1.get("success").equals(true))) {
                   request.getSession().setAttribute("iPhoneCRM", true);
                }
           }else{
                jobj1.put("success", false);
                jobj1.put("error", "Authentication failed");
           }
       } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            jobj1.put("success", false);
            jobj1.put("error", "Error occurred while authentication " + e.toString());
            logger.warn(e.getMessage(),e);
        }
       return jobj1;
    }
    private void setUserSession(HttpServletRequest request, HttpServletResponse response) {
        try {
            String userID = request.getParameter("userid");
//            String appid = request.getParameter("appid");
//            String domain = request.getParameter("domain");
            User userObj = (User) KwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User",userID);
            String user = userObj.getUserLogin().getUserName();
            String pwd = userObj.getUserLogin().getPassword();
            String domain = userObj.getCompany().getSubDomain();
            JSONObject jobj=authHandlerControllerObj.verifyUserLogin(request, response,user,pwd,request.getParameter("blank"),domain);
            jobj =jobj.getJSONObject("data");
            if (jobj.has("success") && (jobj.get("success").equals(true))) {
                request.getSession().setAttribute("iPhoneCRM", true);
            } else {
                return;
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            return;
        }

    }

    public CrmDashboardController getCrmDashboardController()
    {
        return crmDashboardController;
    }

    public void setCrmDashboardController(CrmDashboardController crmDashboardController)
    {
        this.crmDashboardController = crmDashboardController;
    }

    
	public JSONObject getAllComboValueForOpportunity(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		JSONObject jobj = new JSONObject();
		JSONObject jobjOwner = new JSONObject();
		JSONObject jobjCategory = new JSONObject();
		JSONObject jobjAccount = new JSONObject();
		JSONObject jobjStage = new JSONObject();
		ModelAndView model;
		try {
			String companyId = sessionHandlerImpl.getCompanyid(request);
			String comboName=request.getParameter("stage");
			model = crmUserControllerObj.getOwner(request, response);
			jobjOwner = new JSONObject(model.getModel().get("model").toString());
			if (jobjOwner.has("success") && (jobjOwner.get("success").equals(true))) {
				jobj.append("owner", jobjOwner);
				}
			model = crmManagerControllerObj.getComboData(request, response);
			jobjCategory =  new JSONObject(model.getModel().get("model").toString());
			if (jobjOwner.has("success") && (jobjOwner.get("success").equals(true))) {
				jobj.append("LeadSource", jobjCategory);
				}
			model = crmOpportunityControllerObj.getAllAccounts(request, response);
			jobjAccount =  new JSONObject(model.getModel().get("model").toString());
			if (jobjOwner.has("success") && (jobjOwner.get("success").equals(true))) {
				jobj.append("AccountName", jobjAccount);
				}
			jobjStage=getComboData(companyId,comboName);
			if (jobjOwner.has("success") && (jobjOwner.get("success").equals(true))) {
				jobj.append("Stage", jobjStage);
				}
			
		} catch (JSONException e) {
			logger.warn(e.getMessage(), e);
		} catch (ServletException e) {
			logger.warn(e.getMessage(), e);
		} catch (SessionExpiredException e) {
			logger.warn(e.getMessage(), e);
		}

		return jobj;

	}
	private JSONObject insertOpportunity(HttpServletRequest request) throws JSONException {
        JSONObject jobj = null;
        KwlReturnObject kmsg = null;
        boolean oppCreated = false;
        try {
            jobj = new JSONObject();

            String opportunityName = request.getParameter("opportunityname");
            String aopportunityOwnerId = request.getParameter("opportunityownerid");
            String leadSourceId = request.getParameter("leadsourceid");
            String oppStageId = request.getParameter("oppstageid");
            String accountNameId = request.getParameter("accountnameid");
            String closeingDate = request.getParameter("closeingdate");
            String companyId = sessionHandlerImpl.getCompanyid(request);
            String userId = sessionHandlerImpl.getUserid(request);
            String id = java.util.UUID.randomUUID().toString();

            jobj.put("oppname", opportunityName);
            jobj.put("oppownerid", aopportunityOwnerId);
            jobj.put("leadsourceid", leadSourceId);
            jobj.put("companyid", companyId);
            jobj.put("userid", userId);
            jobj.put("oppstageid", oppStageId);
            jobj.put("accountnameid", accountNameId);
            jobj.put("closingdate", new Date(Long.valueOf(closeingDate)).getTime());
            jobj.put("validflag", 1);
            jobj.put("updatedon", new Date());
            jobj.put("oppid", id);
            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
            kmsg = crmOpportunityDAO.addOpportunities(jobj);
            CrmOpportunity  opp = (CrmOpportunity) kmsg.getEntityList().get(0);
            if (opp.getValidflag() == 1) {
                oppCreated = true;
                auditTrailDAOObj.insertAuditLog(AuditAction.OPPORTUNITY_CREATE,
                        ((opp.getOppname()==null)?"":opp.getOppname()) + " - Opportunity created ",
                        request, id);
            }
            jobj = new JSONObject();
            jobj.put("success", true);

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while saving new opportunity (" + ex.toString() + ")");
        }
        return jobj;
    }
	private JSONObject getopportunity(HttpServletRequest request, HttpServletResponse response) throws ServiceException, JSONException {
		JSONObject jobj = new JSONObject();
		JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
        	String userId = sessionHandlerImpl.getUserid(request);
        	 String timeZoneDiff=sessionHandlerImpl.getTimeZoneDifference(request);
        	int offSet=TimeZone.getTimeZone("GMT"+timeZoneDiff).getRawOffset();
        	String dateFormate=iphoneDAOObj.getLoginDateFormate(userId);
        	SimpleDateFormat sdf = new SimpleDateFormat(dateFormate);
        	sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userId);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.company.companyID");
            filter_params.add(sessionHandlerImpl.getCompanyid(request));

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", request.getParameter("isarchive"));
            if(request.getParameter("searchJson") != null && !StringUtil.isNullOrEmpty(request.getParameter("searchJson"))) {
                requestParams.put("searchJson", request.getParameter("searchJson"));
            }
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            if (request.getParameter("config") != null) {
                requestParams.put("config", request.getParameter("config"));
            }

            requestParams.put("export", request.getParameter("reportid"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            requestParams.put("start",  StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));

            kmsg = crmOpportunityDAO.getOpportunities(requestParams, usersList, filter_names, filter_params);
            jobj.put("totalCount", kmsg.getRecordTotalCount());
			Iterator ite = kmsg.getEntityList().iterator();
			while (ite.hasNext()) {
				CrmOpportunity obj = (CrmOpportunity) ite.next();
				JSONObject tmpObj = new JSONObject();
				tmpObj.put("oppname", StringUtil.checkForNull(obj.getOppname()));
				tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
				tmpObj.put("oppowner", obj.getOppmainowner()!=null?obj.getOppmainowner().getFirstName()+""+obj.getOppmainowner().getLastName():"");
				tmpObj.put("stage", (obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : ""));
				tmpObj.put("closedate", (obj.getClosingdate() != null ? sdf.format(new Date(obj.getClosingdate())) : ""));
				tmpObj.put("accoutName", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
				
				 jarr.put(tmpObj);
			}
			jobj.put("success", true);
			jobj.put("data", jarr);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			jobj.put("success", false);
			jobj.put("error", "Error occurred while retrieving the info.(" + e.toString() + ")");
		}
		return jobj;
	}
	
	private JSONObject getProduct(HttpServletRequest request, HttpServletResponse response) throws ServiceException, JSONException {
		 JSONObject jobj = new JSONObject();
	        JSONArray jarr = new JSONArray();
	        KwlReturnObject kmsg = null;
	        try {
	            String userid = sessionHandlerImpl.getUserid(request);
	            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
	            HashMap<String, Object> requestParams = new HashMap<String, Object>();
	            requestParams.put("isarchive", request.getParameter("isarchive"));
	            if(request.getParameter("searchJson") != null && !StringUtil.isNullOrEmpty(request.getParameter("searchJson"))) {
	                requestParams.put("searchJson", request.getParameter("searchJson"));
	            }
	            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
	                requestParams.put("ss", request.getParameter("ss"));
	            }
	            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
	            if (request.getParameter("config") != null) {
	                requestParams.put("config", request.getParameter("config"));
	            }

	            requestParams.put("export", request.getParameter("reportid"));
	            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
	            requestParams.put("heirarchyPerm", heirarchyPerm);
	            if(!StringUtil.isNullOrEmpty(request.getParameter("field"))) {
	                requestParams.put("field", request.getParameter("field"));
	                requestParams.put("direction", request.getParameter("direction"));
	            }
	            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
	            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
			kmsg = crmProductDAO.getProducts(requestParams, usersList);
			jobj.put("totalCount", kmsg.getRecordTotalCount());
			Iterator ite = kmsg.getEntityList().iterator();
			while (ite.hasNext()) {
				CrmProduct obj = (CrmProduct) ite.next();
				JSONObject tmpObj = new JSONObject();
				tmpObj.put("productname", StringUtil.checkForNull(obj.getProductname()));
				String value="";
				if(!StringUtil.isNullOrEmpty(obj.getUnitprice())) {
                    value = crmManagerDAOObj.currencyRender(obj.getUnitprice(),sessionHandlerImpl.getCurrencyID(request));
                }
				if (!StringUtil.isNullOrEmpty(value) ){
                    if (value.contains("€")) {
                        value = value.replace("€", "&#128;");
                    }
				}
				tmpObj.put("unitprice", (value != "" ? value : ""));
				tmpObj.put("ownername", obj.getUsersByUserid()!=null?StringUtil.checkForNull(obj.getUsersByUserid().getFirstName())+""+StringUtil.checkForNull(obj.getUsersByUserid().getLastName()):"");
				tmpObj.put("description", (obj.getDescription() != null ? obj.getDescription() : ""));
				tmpObj.put("Category", (obj.getCrmCombodataByCategoryid() != null ? obj.getCrmCombodataByCategoryid().getValue() : ""));
				tmpObj.put("vendername", (obj.getVendornamee() != null ? obj.getVendornamee() : ""));
				tmpObj.put("venderemail", (obj.getVendoremail() != null ? obj.getVendoremail() : ""));
				
				 jarr.put(tmpObj);
			}
			jobj.put("success", true);
			jobj.put("data", jarr);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			jobj.put("success", false);
			jobj.put("error", "Error occurred while retrieving the info.(" + e.toString() + ")");
		}
		return jobj;
	}
	private JSONObject insertProduct(HttpServletRequest request) throws JSONException {
        JSONObject jobj = null;
        KwlReturnObject kmsg = null;
        try {
            jobj = new JSONObject();

            String productName = request.getParameter("productname");
            String accountOwnerId = request.getParameter("accountownerid");
            String categoryId = request.getParameter("categoryid");
            String description = request.getParameter("description");
            String unitPrice = request.getParameter("unitprice");
            String venderName = request.getParameter("vendername");
            String venderEmail = request.getParameter("venderemail");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String id = java.util.UUID.randomUUID().toString();

            jobj.put("pname", productName);
            jobj.put("categoryid", categoryId);
            jobj.put("companyid", companyid);
            jobj.put("userid", userid);
            jobj.put("description", description);
            jobj.put("unitprice", unitPrice);
            jobj.put("ownerid", accountOwnerId);
            jobj.put("vendornameid", venderName);
            jobj.put("vendoremail", venderEmail);
            jobj.put("validflag", 1);
            jobj.put("updatedon", new Date());
            jobj.put("productid", id);
            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
            kmsg = crmProductDAO.addProducts(jobj);
            CrmProduct  prod = (CrmProduct) kmsg.getEntityList().get(0);
            if (prod.getValidflag() == 1) {
                auditTrailDAOObj.insertAuditLog(AuditAction.PRODUCT_CREATE,
                        ((prod.getProductname()==null)?"":prod.getProductname()) + "  - Product created ",
                        request, id);
            }
            jobj = new JSONObject();
            jobj.put("success", true);

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            jobj.put("success", false);
            jobj.put("error", "Error occurred while saving new product (" + ex.toString() + ")");
        }
        return jobj;
    }

	public JSONObject getAllComboValueForProduct(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
		JSONObject jobj = new JSONObject();
		JSONObject jobjOwner = new JSONObject();
		JSONObject jobjCategory = new JSONObject();
		ModelAndView model;
		try {
			model = crmUserControllerObj.getOwner(request, response);
			jobjOwner = new JSONObject(model.getModel().get("model").toString());
			if (jobjOwner.has("success") && (jobjOwner.get("success").equals(true))) {
				jobj.append("owner", jobjOwner);
				}
			model = crmManagerControllerObj.getComboData(request, response);
			jobjCategory =  new JSONObject(model.getModel().get("model").toString());
			if (jobjOwner.has("success") && (jobjOwner.get("success").equals(true))) {
				jobj.append("category", jobjCategory);
				}
			
		} catch (JSONException e) {
			logger.warn(e.getMessage(), e);
		} catch (ServletException e) {
			logger.warn(e.getMessage(), e);
		}

		return jobj;

	}

	public JSONObject getComboData(String companyId,String comboName) throws ServletException {
		JSONObject jobj = new JSONObject();
		JSONArray jarr = new JSONArray();
		int dl = 0;
		try {
			int customflag = 0;
			ArrayList filter_names = new ArrayList();
			ArrayList filter_params = new ArrayList();
			ArrayList order_by = new ArrayList();
			ArrayList order_type = new ArrayList();
				HashMap<String, Object> requestParams = new HashMap<String, Object>();

				filter_names.add("d.company.companyID");
				filter_params.add(companyId);
				order_by.add("d.itemsequence");
				order_type.add("asc");
				requestParams.put("filter_names", filter_names);
				requestParams.put("filter_params", filter_params);
				requestParams.put("order_by", order_by);
				requestParams.put("order_type", order_type);
				List ll = crmManagerDAOObj.getComboData(comboName, requestParams);
				dl = ll.size();
				Iterator ite = ll.iterator();
				boolean hasAccess = true;
				requestParams.put(Constants.companyid, companyId);
				while (ite.hasNext()) {
					DefaultMasterItem crmCombodata = (DefaultMasterItem) ite.next();
					JSONObject tmpObj = new JSONObject();
					String name = crmCombodata.getValue();
					tmpObj.put("id", crmCombodata.getID());
					tmpObj.put("mainid", crmCombodata.getMainID());
					tmpObj.put("name", name);
					tmpObj.put("itemsequence", crmCombodata.getItemsequence());
					tmpObj.put("percentStage", crmCombodata.getPercentStage());
					tmpObj.put(Constants.Crm_hasAccess, hasAccess);
					jarr.put(tmpObj);
				}
			 
			jobj.put("success", true);
			jobj.put("data", jarr);
			jobj.put("totalCount", dl);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return jobj;
	}

}
