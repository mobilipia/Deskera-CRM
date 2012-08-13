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

package com.krawler.crm.common.bizservice;

import java.util.logging.Level;
import java.util.logging.Logger;


import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.util.CompressService;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.account.bizservice.AccountManagementService;
import com.krawler.crm.activity.bizservice.ActivityManagementService;
import com.krawler.crm.campaign.bizservice.CampaignManagementService;
import com.krawler.crm.cases.bizservice.CaseManagementService;
import com.krawler.crm.contact.bizservice.ContactManagementService;
import com.krawler.crm.lead.bizservice.LeadManagementService;
import com.krawler.crm.opportunity.bizservice.OpportunityManagementService;
import com.krawler.crm.product.bizservice.ProductManagementService;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
/**
 *
 * @author sagar
 */
public class CRMBackupServiceImpl implements CRMBackupService {

    private final String Lead_Headers = "exportType,firstname,lastname,email,owner,createdon,exportmultiproduct,exportrevenue,exportprice,title,status,rating,source,industry,phone,addstreet,";
    private final String Lead_Titles = "Type ,First Name ,Last Name/ Company Name ,Email,Owner ,Lead%20Creation%20Date,Product,Expected%20Revenue,Price,Title/Designation,Lead%20Status,Rating,Lead%20Source,Industry,Phone,Address,";
    private final String Lead_ModuleName = "Leads";
    private final String Product_Headers = "productname,exportprice,owner,category,description,vendornamee,vendoremail,vendorphoneno,createdon,";
    private final String Product_Titles = "Product Name ,Unit%20Price,Owner ,Category,Product%20Description,Vendor%20Name,Vendor%20Email,Vendor%20Contact%20No,Product%20Creation%20Date,";
    private final String Product_ModuleName = "Product";
    private final String Contact_Headers = "firstname,lastname,email,owner,pdfrelatedname,title,leadsource,industry,phoneno,mobileno,street,description,createdon,";
    private final String Contact_Titles = "First%20Name,Last Name ,Email,Owner ,Account%20Name,Title,Lead%20Source,Industry,Phone,number,Address,Description,Contact%20Creation%20Date,";
    private final String Contact_ModuleName = "Contacts";
    private final String Account_Headers = "accountname,email,accountownername,exportmultiproduct,exportrevenue,exportprice,type,industry,phone,website,description,createdon,";
    private final String Account_Titles = "Account Name ,Email,Account Owner ,Product,Revenue,Price,Type,Industry,Phone%20Number,Website,Description,Account%20Creation%20Date,";
    private final String Account_ModuleName = "Accounts";
    private final String Opportunity_Headers = "oppname,oppowner,accountname,exportmultiproduct,stage,closingdate,exportprice,exportsalesamount,type,leadsource,probability,createdon,";
    private final String Opportunity_Titles = "Opportunity Name ,Owner ,Account Name ,Product,Stage,Close Date ,Price,Sales%20Amount,Type,Lead%20Source,Probability,Opportunity%20Creation%20Date,";
    private final String Opportunity_ModuleName = "Opportunity";
    private final String Case_Headers = "subject,description,casename,owner,status,priority,type,accountname,contactname,exportmultiproduct,createdon,";
    private final String Case_Titles = "Subject ,Description,Case%20Name,Owner ,Status ,Priority ,Type,Account Name ,Contact%20Name,Product Name ,Case%20Creation%20Date,";
    private final String Case_ModuleName = "Cases";
    private final String Campaign_Headers = "campaignname,objective,owner,startdate,enddate,type,status,expectedresponse,createdon";
    private final String Campaign_Titles = "Campaign Name ,Objective,Owner ,Start Date ,End Date ,Type,Status,Response,Campaign Creation Date";
    private final String Campaign_ModuleName = "Campaign";
    private final String Activity_Headers = "relatedname,flag,owner,type,subject,status,startdat,starttime,enddat,endtime,priority,phone";
    private final String Activity_Titles = "Related To, Task / Event ,Owner ,Type,Subject,Status ,Start Date ,Start%20Time,End Date ,End%20Time,Priority,Phone";

    
    private LeadManagementService leadManagementService;
    private ProductManagementService productManagementService;
    private ContactManagementService contactManagementService;
    private AccountManagementService accountManagementService;
    private OpportunityManagementService opportunityManagementService;
    private CaseManagementService caseManagementService;
    private CampaignManagementService campaignManagementService;
    private ActivityManagementService activityManagementService;
    private exportDAOImpl exportDAOImplObj;
    private crmCommonDAO crmCommonDAOObj;

    /**
     *
     * @param exportDAOImplObj
     */
    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    /**
     * @return leadManagementService
     */
    public LeadManagementService getLeadManagementService()
    {
        return leadManagementService;
    }

    /**
     * @param leadManagementService
     */
    public void setLeadManagementService(LeadManagementService leadManagementService)
    {
        this.leadManagementService = leadManagementService;
    }

    /**
     * @param productManagementService
     *
     */
    public ProductManagementService getProductManagementService()
    {
        return productManagementService;
    }

    /**
     *
     * @param productManagementService
     */
    public void setProductManagementService(ProductManagementService productManagementService)
    {
        this.productManagementService = productManagementService;
    }

    /**
     * @param contactManagementService
     * @return
     */
    public ContactManagementService getContactManagementService()
    {
        return contactManagementService;
    }

    /**
     * @param contactManagementService
     */
    public void setContactManagementService(ContactManagementService contactManagementService)
    {
        this.contactManagementService = contactManagementService;
    }

    /**
     * @param accountManagementService
     * @return
     */
    public AccountManagementService getAccountManagementService()
    {
        return accountManagementService;
    }

    /**
     * @param accountManagementService
     */
    public void setAccountManagementService(AccountManagementService accountManagementService)
    {
        this.accountManagementService = accountManagementService;
    }

    /**
     * @param opportunityManagementService
     * @return
     */
    public OpportunityManagementService getOpportunityManagementService()
    {
        return opportunityManagementService;
    }

    /**
     * @param opportunityManagementService
     */
    public void setOpportunityManagementService(OpportunityManagementService opportunityManagementService)
    {
        this.opportunityManagementService = opportunityManagementService;
    }

    /**
     * @param caseManagementService
     * @return
     */
    public CaseManagementService getCaseManagementService()
    {
        return caseManagementService;
    }

    /**
     * @param caseManagementService
     */
    public void setCaseManagementService(CaseManagementService caseManagementService)
    {
        this.caseManagementService = caseManagementService;
    }

    /**
     * @param campaignManagementService
     * @return
     */
    public CampaignManagementService getCampaignManagementService()
    {
        return campaignManagementService;
    }

    /**
     * @param campaignManagementService
     */
    public void setCampaignManagementService(CampaignManagementService campaignManagementService)
    {
        this.campaignManagementService = campaignManagementService;
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

    /**
     *
     * @param crmCommonDAOObj1
     */
    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    @Override
    public void backupData(Map<String, Object> requestParams, StringBuffer usersList) {
        if (requestParams != null && !requestParams.isEmpty()) {
			try {
				DateFormat dateFormat = (DateFormat) requestParams.get("dateFormat");
                String userid = requestParams.get("userid").toString();
                String companyid = requestParams.get("companyid").toString();
                String currencyid = requestParams.get("currencyid").toString();

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String  GenerateDate = df.format(new Date());
                String companyname = requestParams.get("companyname").toString();
                String folderName = companyname+"_"+GenerateDate;
                String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "companybackup" + "/" + folderName;

                java.io.File destDir = new java.io.File(destinationDirectory);
                if (!destDir.exists()) { //Create destination folder if not present
                    destDir.mkdirs();
                }

                boolean product_heirarchyPerm = (Boolean) requestParams.get("product_heirarchyPerm");
                backupProducts(product_heirarchyPerm, destinationDirectory, userid, companyid, currencyid, dateFormat);

                boolean editconvertedlead =(Boolean) requestParams.get("editconvertedlead");
                boolean lead_heirarchyPerm = (Boolean) requestParams.get("lead_heirarchyPerm");
                backupLeads(editconvertedlead, lead_heirarchyPerm, destinationDirectory, userid, companyid, currencyid, dateFormat);

                boolean account_heirarchyPerm = (Boolean) requestParams.get("account_heirarchyPerm");
                backupAccounts(account_heirarchyPerm, destinationDirectory, userid, companyid, currencyid, dateFormat, usersList);

                boolean contact_heirarchyPerm = (Boolean) requestParams.get("contact_heirarchyPerm");
                backupContacts(contact_heirarchyPerm, destinationDirectory, userid, companyid, currencyid, dateFormat, usersList);

                boolean opp_heirarchyPerm = (Boolean) requestParams.get("opp_heirarchyPerm");
                backupOpportunities(opp_heirarchyPerm, destinationDirectory, userid, companyid, currencyid, dateFormat, usersList);

                boolean case_heirarchyPerm = (Boolean) requestParams.get("case_heirarchyPerm");
                backupCases(case_heirarchyPerm, destinationDirectory, userid, companyid, currencyid, dateFormat, usersList);

                boolean campaign_heirarchyPerm = (Boolean) requestParams.get("campaign_heirarchyPerm");
                backupCampaigns(campaign_heirarchyPerm, destinationDirectory, userid, companyid, currencyid, dateFormat);

                boolean activity_heirarchyPerm = (Boolean) requestParams.get("activity_heirarchyPerm");
                String timeZoneDiff = requestParams.get("timeZoneDiff").toString();
                String timeFormat = requestParams.get("timeFormat").toString();
                backupActivity(activity_heirarchyPerm, timeZoneDiff, timeFormat, destinationDirectory, userid, companyid, currencyid, dateFormat,usersList);

                CompressService.zipDirectory(destinationDirectory, destinationDirectory + ".zip");
//                exportDAOImplObj.downloadFile(folderName+".zip", destinationDirectory+".zip", response);


                String userEmailID = requestParams.get("userEmailID").toString();
                String sysytemEmailId = requestParams.get("sysemailid").toString();
                String parterName = requestParams.get("partnarname").toString();
				String htmltxt = "This is a notification mail to inform you that you have successfully performed a full data backup.<br/>";
				htmltxt += "<br/><br/>Attachment is the zip file containing backup files for each module.";
				htmltxt += "<br/>For queries, email us at "+sysytemEmailId+"<br/>";
				htmltxt += "<br/>"+parterName+" Team";

				String plainMsg = "This is a notification mail to inform you that you have successfully performed a full data backup.\n";
				plainMsg += "\n\nAttached is the zip file containing backup files for each module.";
				plainMsg += "\nFor queries, email us at "+sysytemEmailId+"\n";
				plainMsg += "\n"+parterName+" Team";

                String filenameEmail = destinationDirectory.substring(destinationDirectory.lastIndexOf("/")+1)+".zip";
				SendMailHandler.postMailAttachment(new String[] { userEmailID }, parterName+" CRM - Notification for a CRM data backup", htmltxt,
                        plainMsg, parterName+" Admin<"+sysytemEmailId+">", destinationDirectory + ".zip", filenameEmail);
			} catch (Exception ex) {
				Logger.getLogger(CRMBackupServiceImpl.class.getName()).log(
						Level.SEVERE, null, ex);
			} finally {
			}
		}
    }

    public JSONObject getColumnHeaders(String module, String companyid, String headers, String titles) {
       JSONObject jobj = new JSONObject();
       KwlReturnObject kmsg = null;
       try{
            ArrayList filter_params = new ArrayList();
            ArrayList filter_names = new ArrayList();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.clear();
            filter_names.clear();
            filter_params.clear();
            filter_names.add("c.defaultheader.moduleName");
            filter_params.add(module);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            filter_names.add("c.defaultheader.customflag");
            filter_params.add(true);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            kmsg = crmCommonDAOObj.getColumnHeader(requestParams);
            Iterator ite2 = kmsg.getEntityList().iterator();
            while(ite2.hasNext()){
                ColumnHeader obj2 = (ColumnHeader) ite2.next();
                String header2="";
                if(!StringUtil.isNullOrEmpty(obj2.getNewHeader())){
                    header2=obj2.getNewHeader();
                }else{
                    header2=obj2.getDefaultheader().getDefaultHeader();
                }
                titles += header2 + ",";
                headers += obj2.getDefaultheader().getRecordname() + ",";
            }
            headers = headers.substring(0, headers.length()-1);
            titles = titles.substring(0, titles.length()-1);
            jobj.put("titles", titles);
            jobj.put("headers", headers);
       } catch(Exception e) {
          System.out.println(e.getMessage());
       }
       return jobj;
    }

    public void backupLeads(boolean editconvertedlead, boolean heirarchyPerm, String destinationDirectory, String userid,
            String companyid, String currencyid, DateFormat dateFormat) {
        try{
            JSONObject jobj = new JSONObject();

//            boolean editconvertedlead =crmLeadHandler.editConvertedLead(request);
//            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            String transfered = "0";//request.getParameter("transfered");
            String isconverted = "0";//request.getParameter("isconverted");
            String isExport = Lead_ModuleName;//request.getParameter("reportid");
            String field = "lastname";//request.getParameter("field");
            String direction = "ASC";//request.getParameter("direction");
            String moduleName = Lead_ModuleName;

            String selectExport = "";//request.getParameter("selectExport");
            String isarchive = "false";//request.getParameter("isarchive");
            String searchJson = "";//request.getParameter("searchJson");
            String ss = "";//request.getParameter("ss");
            String config = "";//request.getParameter("config");

            jobj = getLeadManagementService().LeadExport(companyid, userid, currencyid, selectExport, editconvertedlead,
                    isarchive, transfered, isconverted, searchJson, ss, config, isExport, heirarchyPerm, field, direction, dateFormat);

            String head = Lead_Headers;
            String tit = Lead_Titles;
            JSONObject colHeaders = getColumnHeaders("Lead", companyid, head, tit);
            head = colHeaders.getString("headers");
            tit = colHeaders.getString("titles");

            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

        } catch (Exception e) {
            Logger.getLogger(CRMBackupServiceImpl.class.getName()).log(
						Level.SEVERE, null, e);
        }
    }

    public void backupActivity(boolean heirarchyPerm, String timeZoneDiff, String timeFormat, String destinationDirectory, String userid,
            String companyid, String currencyid, DateFormat dateFormat,StringBuffer usersList) {
        try{
            JSONObject jobj = new JSONObject();
            String field = "";//request.getParameter("field");
            String direction = "";//request.getParameter("direction");
            String selectExport = "";//request.getParameter("selectExport");
            boolean isArchive = false;//request.getParameter("isarchive");
            String searchJson = "";//request.getParameter("searchJson");
            String ss = "";//request.getParameter("ss");
            String config = "";//request.getParameter("config");
            String iscustomcolumn = "";
            String xfield = "";
            String xtype = "";
            String mapid = "";

            String status = "";
            String head = Activity_Headers;
            String tit = Activity_Titles;
            String start = "";
            String limit = "";
            
            //Lead Activity
            String moduleName = "LeadActivity";
            String isExport = "LeadActivity";//request.getParameter("reportid");
            jobj = getActivityManagementService().getActivity(companyid, userid, currencyid, selectExport,
                    isArchive, searchJson, ss, config, isExport, status, heirarchyPerm, field, direction, iscustomcolumn,
                    xfield, xtype, timeZoneDiff, timeFormat, Constants.Lead, mapid,dateFormat, start, limit,usersList);
            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

            //Account Activity
            moduleName = "AccountActivity";
            isExport = "AccountActivity";//request.getParameter("reportid");
            jobj = getActivityManagementService().getActivity(companyid, userid, currencyid, selectExport,
                    isArchive, searchJson, ss, config, isExport, status, heirarchyPerm, field, direction, iscustomcolumn,
                    xfield, xtype, timeZoneDiff, timeFormat, Constants.Account, mapid,dateFormat, start, limit,usersList);
            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

            //Contact Activity
            moduleName = "ContactActivity";
            isExport = "ContactActivity";//request.getParameter("reportid");
            jobj = getActivityManagementService().getActivity(companyid, userid, currencyid, selectExport,
                    isArchive, searchJson, ss, config, isExport, status, heirarchyPerm, field, direction, iscustomcolumn,
                    xfield, xtype, timeZoneDiff, timeFormat, Constants.Contact, mapid,dateFormat, start, limit,usersList);
            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

            //Contact Activity
            moduleName = "OpportunityActivity";
            isExport = "OpportunityActivity";//request.getParameter("reportid");
            jobj = getActivityManagementService().getActivity(companyid, userid, currencyid, selectExport,
                    isArchive, searchJson, ss, config, isExport, status, heirarchyPerm, field, direction, iscustomcolumn,
                    xfield, xtype, timeZoneDiff, timeFormat, Constants.Opportunity, mapid,dateFormat, start, limit,usersList);
            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

            //Case Activity
            moduleName = "CaseActivity";
            isExport = "CaseActivity";//request.getParameter("reportid");
            jobj = getActivityManagementService().getActivity(companyid, userid, currencyid, selectExport,
                    isArchive, searchJson, ss, config, isExport, status, heirarchyPerm, field, direction, iscustomcolumn,
                    xfield, xtype, timeZoneDiff, timeFormat, Constants.Case, mapid,dateFormat, start, limit,usersList);
            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

            //Campaign Activity
            moduleName = "CampaignActivity";
            isExport = "CampaignActivity";//request.getParameter("reportid");
            jobj = getActivityManagementService().getActivity(companyid, userid, currencyid, selectExport,
                    isArchive, searchJson, ss, config, isExport, status, heirarchyPerm, field, direction, iscustomcolumn,
                    xfield, xtype, timeZoneDiff, timeFormat, Constants.Campaign, mapid,dateFormat, start, limit,usersList);
            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

        } catch (Exception e) {
            Logger.getLogger(CRMBackupServiceImpl.class.getName()).log(
						Level.SEVERE, null, e);
        }
    }

    public void backupProducts(boolean heirarchyPerm, String destinationDirectory, String userid,
            String companyid, String currencyid, DateFormat dateFormat) {
        try{
            JSONObject jobj = new JSONObject();

//            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            String isExport = Product_ModuleName;//request.getParameter("reportid");
            String field = "createdon";//request.getParameter("field");
            String direction = "DESC";//request.getParameter("direction");
            String moduleName = Product_ModuleName;

            String selectExport = "";//request.getParameter("selectExport");
            String isarchive = "false";//request.getParameter("isarchive");
            String searchJson = "";//request.getParameter("searchJson");
            String ss = "";//request.getParameter("ss");
            String config = "";//request.getParameter("config");

            jobj = getProductManagementService().ProductExport(companyid, userid, currencyid, selectExport, isarchive, searchJson,
                    ss, config, isExport, heirarchyPerm, field, direction, dateFormat);

            String head = Product_Headers;
            String tit = Product_Titles;
            JSONObject colHeaders = getColumnHeaders("Product", companyid, head, tit);
            head = colHeaders.getString("headers");
            tit = colHeaders.getString("titles");

            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

        } catch (Exception e) {
            Logger.getLogger(CRMBackupServiceImpl.class.getName()).log(
						Level.SEVERE, null, e);
        }
    }

    public void backupContacts(boolean heirarchyPerm, String destinationDirectory, String userid,
            String companyid, String currencyid, DateFormat dateFormat, StringBuffer usersList) {
        try{
            JSONObject jobj = new JSONObject();

//            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            String isExport = Contact_ModuleName;//request.getParameter("reportid");
            String field = "lastname";//request.getParameter("field");
            String direction = "ASC";//request.getParameter("direction");
            String moduleName = Contact_ModuleName;

            String selectExport = "";//request.getParameter("selectExport");
            String isarchive = "false";//request.getParameter("isarchive");
            String searchJson = "";//request.getParameter("searchJson");
            String ss = "";//request.getParameter("ss");
            String config = "";//request.getParameter("config");
            String LeadAccountFlag = "6";
            String LeadAccountName = "";
            String mapid = "";

            jobj = getContactManagementService().ContactExport(companyid, userid, currencyid, selectExport, LeadAccountFlag, isarchive, LeadAccountName, mapid,
                    searchJson, ss, config, isExport, heirarchyPerm, field, direction, dateFormat, usersList);

            String head = Contact_Headers;
            String tit = Contact_Titles;
            JSONObject colHeaders = getColumnHeaders("Contact", companyid, head, tit);
            head = colHeaders.getString("headers");
            tit = colHeaders.getString("titles");

            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

        } catch (Exception e) {
            Logger.getLogger(CRMBackupServiceImpl.class.getName()).log(
						Level.SEVERE, null, e);
        }
    }

    public void backupAccounts(boolean heirarchyPerm, String destinationDirectory, String userid,
            String companyid, String currencyid, DateFormat dateFormat, StringBuffer usersList) {
        try{
            JSONObject jobj = new JSONObject();

//            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            String isExport = Account_ModuleName;//request.getParameter("reportid");
            String field = "createdOn";//request.getParameter("field");
            String direction = "DESC";//request.getParameter("direction");
            String moduleName = Account_ModuleName;

            String selectExport = "";//request.getParameter("selectExport");
            String isarchive = "false";//request.getParameter("isarchive");
            String searchJson = "";//request.getParameter("searchJson");
            String ss = "";//request.getParameter("ss");
            String config = "";//request.getParameter("config");

            jobj = getAccountManagementService().AccountExport(companyid, userid, currencyid, selectExport, isarchive, searchJson,
                    ss, config, isExport, heirarchyPerm, field, direction, dateFormat, usersList);

            String head = Account_Headers;
            String tit = Account_Titles;
            JSONObject colHeaders = getColumnHeaders("Account", companyid, head, tit);
            head = colHeaders.getString("headers");
            tit = colHeaders.getString("titles");

            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

        } catch (Exception e) {
            Logger.getLogger(CRMBackupServiceImpl.class.getName()).log(
						Level.SEVERE, null, e);
        }
    }

    public void backupOpportunities(boolean heirarchyPerm, String destinationDirectory, String userid,
            String companyid, String currencyid, DateFormat dateFormat, StringBuffer usersList) {
        try{
            JSONObject jobj = new JSONObject();

//            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            String isExport = Opportunity_ModuleName;//request.getParameter("reportid");
            String field = "createdOn";//request.getParameter("field");
            String direction = "DESC";//request.getParameter("direction");
            String moduleName = Opportunity_ModuleName;

            String selectExport = "";//request.getParameter("selectExport");
            String isarchive = "false";//request.getParameter("isarchive");
            String searchJson = "";//request.getParameter("searchJson");
            String ss = "";//request.getParameter("ss");
            String config = "";//request.getParameter("config");
            String accountFlag = "7";

            jobj = getOpportunityManagementService().opportunityExport(companyid, userid, currencyid, selectExport, accountFlag, isarchive, field,
                    searchJson, ss, config, isExport, heirarchyPerm, field, direction, dateFormat, usersList);

            String head = Opportunity_Headers;
            String tit = Opportunity_Titles;
            JSONObject colHeaders = getColumnHeaders("Opportunity", companyid, head, tit);
            head = colHeaders.getString("headers");
            tit = colHeaders.getString("titles");

            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

        } catch (Exception e) {
            Logger.getLogger(CRMBackupServiceImpl.class.getName()).log(
						Level.SEVERE, null, e);
        }
    }

    public void backupCases(boolean heirarchyPerm, String destinationDirectory, String userid,
            String companyid, String currencyid, DateFormat dateFormat, StringBuffer usersList) {
        try{
            JSONObject jobj = new JSONObject();

//            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            String isExport = Case_ModuleName;//request.getParameter("reportid");
            String field = "createdOn";//request.getParameter("field");
            String direction = "DESC";//request.getParameter("direction");
            String moduleName = Case_ModuleName;

            String selectExport = "";//request.getParameter("selectExport");
            String isarchive = "false";//request.getParameter("isarchive");
            String searchJson = "";//request.getParameter("searchJson");
            String ss = "";//request.getParameter("ss");
            String config = "";//request.getParameter("config");
            String accountFlag = "16";

            jobj = getCaseManagementService().caseExport(companyid, userid, currencyid, selectExport, accountFlag, isarchive, field,
                    searchJson, ss, config, isExport, heirarchyPerm, field, direction, dateFormat, usersList);

            String head = Case_Headers;
            String tit = Case_Titles;
            JSONObject colHeaders = getColumnHeaders("Case", companyid, head, tit);
            head = colHeaders.getString("headers");
            tit = colHeaders.getString("titles");

            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

        } catch (Exception e) {
            Logger.getLogger(CRMBackupServiceImpl.class.getName()).log(
						Level.SEVERE, null, e);
        }
    }

    public void backupCampaigns(boolean heirarchyPerm, String destinationDirectory, String userid,
            String companyid, String currencyid, DateFormat dateFormat) {
        try{
            JSONObject jobj = new JSONObject();

//            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            String isExport = Campaign_ModuleName;//request.getParameter("reportid");
            String field = "createdOn";//request.getParameter("field");
            String direction = "DESC";//request.getParameter("direction");
            String moduleName = Campaign_ModuleName;

            String selectExport = "";//request.getParameter("selectExport");
            String isarchive = "false";//request.getParameter("isarchive");
            String searchJson = "";//request.getParameter("searchJson");
            String ss = "";//request.getParameter("ss");
            String config = "";//request.getParameter("config");

            jobj = getCampaignManagementService().campaignExport(companyid, userid, currencyid, selectExport, isarchive,
                    searchJson, ss, config, isExport, heirarchyPerm, field, direction, searchJson, ss, field, dateFormat);

            String head = Campaign_Headers;
            String tit = Campaign_Titles;

            exportDAOImplObj.createCsvFileForBackup(head, tit, moduleName, destinationDirectory, jobj);

        } catch (Exception e) {
            Logger.getLogger(CRMBackupServiceImpl.class.getName()).log(
						Level.SEVERE, null, e);
        }
    }

}
