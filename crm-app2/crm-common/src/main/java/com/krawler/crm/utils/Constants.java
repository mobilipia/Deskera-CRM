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
package com.krawler.crm.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A place to keep commonly-used constants.
 */
public class Constants {
    //Image Path
        public static final String leadsource_Combo="9dc0376d-0323-4aa3-8092-67956ad5728b";
        public static final String templateid = "templateid";
        public static final String deleteflag = "deleteflag";
        public static final String userid = "userid";
        public static final String updatedon = "updatedon";
        public static final String successDeleteArr = "successDeleteArr";
        public static final String failDelete = "failDelete";
        public static final String modifiedon = "modifiedon";
        public static final String tid = "tid";
        public static final String deleted = "deleted";
        public static final String jsondata = "jsondata";
        public static final String MODULE_EmailTemplate ="EmailTemplate";
        public static final String moduleName="moduleName";
        public static final String tzdiff="tzdiff";
        public static final String GMT="GMT";
        public static final String createdon="createdon";
	public static final String ImgBasePath = "images/store/";
	private static final String defaultImgPath = "images/defaultuser.png";
        private static final String defaultCompanyImgPath = "images/logo.gif";
        public static final String filter_names = "filter_names";
        public static final String data = "data";
        public static final String success = "success";
        public static final String model = "model";
        public static final String filter_values = "filter_values";
        public static final String filter_params = "filter_params";
        public static final String order_by = "order_by";
        public static final String order_type = "order_type";
        public static final String userlist_value = "userlist_value";
        public static final String id = "id";
        public static final String where = "where";
        public static final String name = "name";
        public static final String phone = "phone";
        public static final String email = "email";
        public static final String Crm_account = "crm_account";
        public static final String Account = "Account";
        public static final String Contact = "Contact";
        public static final String Campaign = "Campaign";
        public static final String Lead = "Lead";
        public static final String Lead_Source = "Lead Source";
        public static final String Campaign_Source = "Campaign Source";
        public static final String Case = "Case";
        public static final String c_deleteflag = "c.deleteflag";
        public static final String c_validflag = "c.validflag";
        public static final String c_company_companyID = "c.company.companyID";
        public static final String Opportunity = "Opportunity";
        public static final String Crm_hasAccess = "hasAccess";
        public static final String Crm_lead = "crm_lead";
        public static final String Crm_case = "crm_case";
        public static final String Crm_product = "crm_product";
        public static final String Crm_opportunity = "crm_opportunity";
        public static final String Crm_contact = "crm_contact";

        public static final String Crm_accountid = "accountid";
        public static final String Crm_campaignid = "campaignid";
        public static final String Crm_leadid = "leadid";
        public static final String Crm_Leadid = "Leadid";
        public static final String Crm_caseid = "caseid";
        public static final String Crm_productid = "productid";
        public static final String Crm_opportunityid = "oppid";
        public static final String Crm_contactid = "contactid";
        public static final String Crm_activityid = "activityid";
        public static final String companyid = "companyid";

        public static final String Crm_account_modulename = "account";
        public static final String Crm_campaign_modulename = "campaign";
        public static final String Crm_lead_modulename = "lead";
        public static final String Crm_Lead_modulename = "Lead";
        public static final String Crm_case_modulename = "cases";
        public static final String Crm_product_modulename = "product";
        public static final String Crm_opportunity_modulename = "opportunity";
        public static final String Crm_contact_modulename = "contact";
        public static final char CASE_COMMENT_USERFLAG = '1';

        public static final String TextField_default = "0";
        public static final String NumberField_default = "0.0";
        public static final String TimeField_default = "8:00 AM";

	public static final long MILLIS_PER_SECOND = 1000;
	public static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
	public static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
	public static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;
	public static final long MILLIS_PER_WEEK = MILLIS_PER_DAY * 7;
	public static final long MILLIS_PER_MONTH = MILLIS_PER_DAY * 31;
    // Regex for email and phone
    public static final String emailRegex = "^[\\w-]+([\\w!#$%&'*+/=?^`{|}~-]+)*(\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[\\w-]+)$";
    public static final String contactRegex = "^(\\(?\\+?[0-9]*\\)?)?[0-9_\\- \\(\\)]*$";
    public static final String numberRegex = "^-?\\d+(\\.\\d*)?$";
    public static final String dateRegex_yyyymmdd = "^(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$";//(yyyy-mm-dd) 
    
	public static final int SECONDS_PER_MINUTE = 60;
	public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;
	public static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;
	public static final int SECONDS_PER_WEEK = SECONDS_PER_DAY * 7;
	public static final int SECONDS_PER_MONTH = SECONDS_PER_DAY * 31;
    
    //Max no. of records in HashMap
    public static final int maxrecordsMapCount = 50;

    // IDS from Table crmCombomaster table
    public static final String LEAD_STATUSID ="60548516-75db-472e-8d75-00b40696ecf6";
    public static final String LEAD_SOURCEID ="9dc0376d-0323-4aa3-8092-67956ad5728b";
    public static final String CAMPAIGN_SOURCEID ="3b1e9726-12ea-4adf-a6ef-f0950075fec4";
    public static final String LEAD_RATINGID ="70b1c417-dd3f-4572-8db6-7cf5bafb3ba8";
    public static final String LEAD_INDUSTRYID ="fc7c4e27-56bf-4524-a146-41d873829345";
    public static final String ACCOUNT_TYPEID ="24761544-c561-43eb-aa94-f5bcfad31dcb";
    public static final String OPP_STAGEID ="d49609c2-0abc-47ce-8d5a-5850c03b7291";
    public static final String OPP_TYPEID ="e47acf7c-cfe1-49e3-8fc5-9294f40a5631";
    public static final String OPP_REGIONID ="b3090c63-8ff9-43af-ab3f-25721a86290c";
    public static final String CASE_STATUSID ="500d6f95-a6ab-4f5f-bb26-2ef6161760a1";
    public static final String CASE_SLAID = "17CC54E8-76F9-11E0-9BCF-94324924019B";
    public static final String CASE_USERID_CUSTOM = "users";
    public static final String CASE_TYPEID ="6614300c-eb30-4f1e-86db-4f693ee272b0";
    public static final String CASE_PRIORITYID ="c82d6115-6c38-4d1f-abb2-3b75ec30a1df";
    public static final String PRODUCT_CATEGORYID ="e30643eb-2416-4a9d-a70e-d5268995891d";
    public static final String ACTIVITY_STATUSID ="cce4b390-5f73-4687-b9d1-fb8c2bad98c9";
    public static final String ACTIVITY_TYPEID ="1baae1fc-2a21-4582-8369-56f3aab3725f";
    public static final String ACTIVITY_PRIORITYID ="c82d6115-6c38-4d1f-abb2-3b75ec30a1df";

    // IDS from Table crm_combodata
    public static final String ACTIVITYSTATUSID_COMPLETED ="701441c3-f609-47d4-a613-9b4290980887";
    public static final String LEADSTATUSID_QUALIFIED ="f01e5a6f-7011-4e2d-b93e-58b5c6270239";
    public static final String LEADSTATUSID_PREQUALIFIED ="94b9007e-696b-4e1b-9b97-0866dbc10c01";
    public static final String CAMPAIGN_COMPLETE = "0fca425b-458f-4ceb-ba3e-5b7aab469d7e";
    public static final String CAMPAIGN_EMAILMARKET = "b0e71040-b46d-4fc0-bfe3-1fccca96016f";
    public static final String CASESTATUS_PENDING = "443dd38f-1c39-43c3-8f41-6d490dcf8302";
    public static final String CASESTATUS_CLOSED = "e436d5c7-e369-4b12-b4b8-deeb393d1234";
    public static final String CASESTATUS_NEWCASE = "00962c0b-42c3-4640-b3c7-8be8ea922ed3";
    public static final String CASESTATUS_ESCALATED = "98e4ed03-259b-4d62-8c06-e0fb630170f8";
    public static final String LEADSTATUSID_CONTACTED ="c5c96e60-16a2-4222-99a1-125d00fe80f1";
    public static final String LEADSTATUSID_OPEN ="5c74d621-304c-491a-9217-74acb18549b6";
    public static final String LEADTYPECOMBOID = "b6ad01f9-0f3f-457d-8adb-f3a3d1969b68";
    public static final String CASEPRIORITY_HIGH ="3a047d1c-9945-4361-bd91-26cac9be1d97";
    public static final String CASESTATUS_NOTSTARTED ="1434ca1a-c7f1-470b-951c-a9e804ab2f30";
	public static final String OPPSTAGEID_CLOSEDWON ="667946c6-f7b0-49ee-8040-26573b820d2e";
    public static final String OPPSTAGEID_FINAL ="dada5762-f13b-4ad2-af91-016a20907305";
    public static final String TASKSTATUS_NOTSTARTED = "1434ca1a-c7f1-470b-951c-a9e804ab2f30";

    public static final HashMap<String,String> mastersData = new HashMap<String,String>();
        static{
            mastersData.put("LEADSTATUSID_QUALIFIED", LEADSTATUSID_QUALIFIED);
            mastersData.put("LEADSTATUSID_PREQUALIFIED", LEADSTATUSID_PREQUALIFIED);
            mastersData.put("CAMPAIGN_COMPLETE", CAMPAIGN_COMPLETE);
            mastersData.put("CAMPAIGN_EMAILMARKET", CAMPAIGN_EMAILMARKET);
            mastersData.put("CASESTATUS_PENDING", CASESTATUS_PENDING);
            mastersData.put("CASESTATUS_NEWCASE", CASESTATUS_NEWCASE);
            mastersData.put("CASESTATUS_ESCALATED", CASESTATUS_ESCALATED);
            mastersData.put("LEADSTATUSID_CONTACTED", LEADSTATUSID_CONTACTED);
            mastersData.put("LEADSTATUSID_OPEN", LEADSTATUSID_OPEN);
            mastersData.put("LEADTYPECOMBOID", LEADTYPECOMBOID);
            mastersData.put("CASEPRIORITY_HIGH", CASEPRIORITY_HIGH);
            mastersData.put("CASESTATUS_NOTSTARTED", CASESTATUS_NOTSTARTED);
            mastersData.put("OPPSTAGEID_CLOSEDWON", OPPSTAGEID_CLOSEDWON);
            mastersData.put("TASKSTATUS_NOTSTARTED", TASKSTATUS_NOTSTARTED);
        }
         public static final Map<String,String> JoinPropertyMap = new HashMap<String,String>();
        static{
        JoinPropertyMap.put("crmCombodataByLeadstatusid.id", "leadstatusID");
        JoinPropertyMap.put("crmCombodataByLeadsourceid.id", "leadsourceID");
        JoinPropertyMap.put("crmCombodataByIndustryid.id", "industryID");
        JoinPropertyMap.put("crmCombodataByRatingid.id", "ratingID");
        JoinPropertyMap.put("crmCombodataByRegionid.id", "regionID");
        JoinPropertyMap.put("crmCombodataBySalutationid.id", "salutationID");
        JoinPropertyMap.put("crmCombodataByTitleid.id", "titleID");
    }

    //Full MS-OUTLOOK CSV Header
    //public static final String[] CSV_HEADER_MSOUTLOOK = {"Title","First Name","Middle Name","Last Name","Suffix","Company","Department","Job Title","Business Street","Business Street 2","Business Street 3","Business City","Business State","Business Postal Code","Business Country/Region","Home Street","Home Street 2","Home Street 3","Home City","Home State","Home Postal Code","Home Country/Region","Other Street","Other Street 2","Other Street 3","Other City","Other State","Other Postal Code","Other Country/Region","Assistant's Phone","Business Fax","Business Phone","Business Phone 2","Callback","Car Phone","Company Main Phone","Home Fax","Home Phone","Home Phone 2","ISDN","Mobile Phone","Other Fax","Other Phone","Pager","Primary Phone","Radio Phone","TTY/TDD Phone","Telex","Account","Anniversary","Assistant's Name","Billing Information","Birthday","Business Address PO Box","Categories","Children","Directory Server","E-mail Address","E-mail Type","E-mail Display Name","E-mail 2 Address","E-mail 2 Type","E-mail 2 Display Name","E-mail 3 Address","E-mail 3 Type","E-mail 3 Display Name","Gender","Government ID Number","Hobby","Home Address PO Box","Initials","Internet Free Busy","Keywords","Language","Location","Manager's Name","Mileage","Notes","Office Location","Organizational ID Number","Other Address PO Box","Priority","Private","Profession","Referred By","Sensitivity","Spouse","User 1","User 2","User 3","User 4","Web Page"};
    //Header used for our contact export
    public static final String[] CSV_HEADER_MSOUTLOOK = {"\"First Name\"","\"E-mail Address\"","\"Business Phone\"","\"Business Street\""};
    //Default ids
    public static final String CURRENCY_DEFAULT ="1";
    public static final String TIMEZONE_DEFAULT ="1";
    public static final String NEWYORK_TIMEZONE_ID ="23";

    public static final String MODULE_ACCOUNT ="Account";
    public static final String MODULE_LEAD ="Lead";
    public static final String MODULE_Campaign ="Campaign";
    public static final String MODULE_CASE ="Cases";
    public static final String MODULE_CONTACT ="Contact";
    public static final String MODULE_OPPORTUNITY ="Opportunity";
    public static final String MODULE_PRODUCT ="Product";
    public static final String MODULE_ACTIVITY ="Activity";
    public static final String MODULE_DOCUMENTS ="Documents";
    public static final String MODULE_USER = "User";
    public static final String MODULE_TARGET = "Target";

    //Role Ids
    public static final String COMPANY_ADMIN = "ff8080812235ee49012236133c090002";
    public static final String COMPANY_SALES_MANAGER = "ff8080812235ee4901223619e6070003";
    public static final String COMPANY_SALES_EXECUTIVE = "ff80808122361c6a01223661e34b0001";

    public static final String LEAD_REVENUE = "b39db77f-cb0e-4371-b175-fbf16c5c1405";
    public static final String LEAD_PRICE = "57527cc8-c3cb-47f2-9a0d-a5acd7343859";
    public static final String LEAD_PHONE = "887329d1-4020-4050-896b-ee57c510ec18";
    public static final String LEAD_NAME = "40fd3edb-24f8-4adf-b79d-5febb608ac1c";
    public static final String LEAD_INDUSTRY = "ba4b87ca-a5b6-44d1-9073-f6516c2cbb46";
    public static final String LEAD_PRODUCT = "a7ec1a67-d495-4512-bd0c-c262afc60fbf";
    public static final String LEAD_EMAIL = "a7712c4e-8efc-47f5-8bc2-563076e5b2e1";
    public static final String LEAD_LEADSOURCE = "c7667615-90c6-41fe-aaff-3b586df2beff";
    public static final String LEAD_TITLE = "b1777aa6-f01a-40b9-a8c5-e5452b719a3f";

    public static final String ACCOUNT_REVENUE = "41592959-37c3-4b21-8d6c-44ac4237f56f";
    public static final String ACCOUNT_PRICE = "623000ae-86dc-44b0-9a8d-068ec9928fc5";
    public static final String ACCOUNT_PHONE = "8cceafd4-30b7-4328-94a8-21f82122c3c5";
    public static final String ACCOUNT_NAME = "c20fbde3-055c-410c-a1bd-3a3687c090fd";
    public static final String ACCOUNT_INDUSTRY = "d9f79b73-369e-48cd-a2d5-9f883c435ea1";
    public static final String ACCOUNT_PRODUCT = "db826f2b-4f0b-4e95-ae99-251868e0c544";

    public static final String OPPORTUNITY_NAME = "bf11f1ce-0124-4b96-b74f-6fafb5003384";
    public static final String OPPORTUNITY_SALESAMOUNT = "df073217-a44d-42b8-8946-1ac39e003bfd";
    public static final String OPPORTUNITY_PRICE = "9b4f4dbe-bfde-4f84-bded-7337d4cf3363";
    public static final String OPPORTUNITY_LEADSOURCE = "2b617ca1-56fc-4d7f-8982-01d1fecfc685";
    public static final String OPPORTUNITY_PRODUCT = "dfb3e294-d9c9-4986-8fbf-0926fafb6491";

    public static final String CONTACT_LASTNAME = "db050499-38e0-4508-a9cd-f0b0c0b95201";
    public static final String CONTACT_PHONE = "123808c8-0573-4634-8808-44550bbeec2c";
    public static final String CONTACT_EMAIL = "fc3b85c1-a727-4d0c-a44c-61790a01e337";
    public static final String CONTACT_LEADSOURCE = "7c47c3d1-83aa-469b-be45-df317b9bb266";
    public static final String CONTACT_TITLE = "7fbcf11f-3209-4a18-88cc-ddd4332bc047";

    public static final String PRODUCTTYPE_COMBOID ="e30643eb-2416-4a9d-a70e-d5268995891d";
    public static final String CASE_PRODUCT = "86559fd6-caed-42b8-ba9b-6cacf9d6b47c";

    public static final String MASTERCONFIG_HIDECOMBO ="'Campaign Source' ,'Event Status' ,'Salutation','Currency','Manufacturer','Reminder','Scale','Number Of Employees','Title','Tax','Contract Status','Product'";
    
    public static final HashMap<String,Integer> iphone = new HashMap<String,Integer>();
     public static final HashMap<Integer,String> ACTIVITYMODULEIDMAP = new HashMap<Integer,String>();
        static{
            ACTIVITYMODULEIDMAP.put(2, "Account");
            ACTIVITYMODULEIDMAP.put(3, "Contact");
            ACTIVITYMODULEIDMAP.put(4, "Lead");
            ACTIVITYMODULEIDMAP.put(5, "Case");
            ACTIVITYMODULEIDMAP.put(6, "Campaign");
            ACTIVITYMODULEIDMAP.put(7, "Opportunity");
        }

    public static final HashMap<String,String> URLS = new HashMap<String,String>();
        static{
            iphone.put("LeadsbySource", 1);
            iphone.put("LeadsbyIndustry", 2);
            iphone.put("ConvertedLeads", 3);
            iphone.put("QualifiedLeads", 4);
            iphone.put("ContactedLeads", 5);
            iphone.put("OpenLeads", 6);
            iphone.put("LeadPipeline", 7);

            iphone.put("SourcesOfOpportunity", 8);
            iphone.put("OpportunityByType", 9);
            iphone.put("StuckOpportunities", 10);
            iphone.put("ClosedOpportunities", 11);
            iphone.put("OpportunitybySource", 12);
            iphone.put("OpportunitybyStage", 13);
            iphone.put("SalesbySource", 14);
            iphone.put("AccountOpportunity", 14);
            iphone.put("OpportunityPipeline", 15);
            iphone.put("OpportunityByProduct", 16);

            iphone.put("IndustryAccountTypes", 17);
            iphone.put("MonthlyAccounts", 18);
            iphone.put("KeyAccounts", 19);

            iphone.put("CasesbyStatus", 20);
            iphone.put("ContactHighPriority", 21);
            iphone.put("MonthlyCases", 22);
            iphone.put("NewlyAddedCases", 23);
            iphone.put("PendingCases", 24);
            iphone.put("EscalatedCases", 25);
            iphone.put("AccountCases", 26);
            iphone.put("ContactCase", 27);

            iphone.put("CampaignType", 28);
            iphone.put("CompletedCampaign", 29);
            iphone.put("CampaignResponse", 30);

            iphone.put("LeadSourceContacts", 31);
            iphone.put("ConvertedLeadAccount", 32);
            iphone.put("ConvertedLeadOpp", 33);
            iphone.put("ConvertedLeadContact", 34);

            URLS.put("AccountSave", "/crm/Account/action/saveAccounts.do");
            URLS.put("AccountDelete", "/crm/Account/action/deleteAccount.do");
            URLS.put("ContactSave", "/crm/Contact/action/saveContacts.do");
            URLS.put("ContactDelete", "/crm/Contact/action/deleteContact.do");
            URLS.put("ProductSave", "/crm/Product/action/saveProducts.do");
            URLS.put("ProductDelete", "/crm/Product/action/deleteProduct.do");
            URLS.put("CampaignSave", "/crm/Campaign/action/saveCampaigns.do");
            URLS.put("CampaignDelete", "/crm/Campaign/action/deleteCampaign.do");
            
        }
        public static final String Crm_custom_field = "fieldname";
        public static final int CUSTOM_FIELD_AUTONUMBER = 9;
        public static final String AUTONO ="autono";
        public static final String Crm_lead_custom_data_classpath = "com.krawler.crm.database.tables.CrmLeadCustomData";
        public static final String Crm_lead_classpath = "com.krawler.crm.database.tables.CrmLead";
        public static final int Custom_Column_limit = 50;
        public static final String Custom_Column_Prefix = "Col";
        public static final String Crm_Opportunityid = "Oppid";
        public static final String Crm_Accountid = "Accountid";
        public static final String Crm_Caseid = "Caseid";
        public static final String Crm_Productid = "Productid";
        public static final String Crm_Contactid = "Contactid";
        public static final Integer Crm_lead_moduleid = 2;
        public static final Integer Crm_case_moduleid = 3;
        public static final Integer Crm_product_moduleid = 4;
        public static final Integer Crm_account_moduleid = 1;
        public static final Integer Crm_contact_moduleid = 6;
        public static final Integer Crm_opportunity_moduleid = 5;
        public static final String Crm_Account_modulename = "Account";
        public static final String Crm_Case_modulename = "Case";
        public static final String Crm_Contact_modulename = "Contact";
        public static final String Crm_Product_modulename = "Product";
        public static final String Crm_Opportunity_modulename = "Opportunity";
        public static final String CRM_ACTIVITY_MODULENAME = "Activity";

        public static final String field_data_undefined = "undefined";

        public static final String Crm_lead_custom_data_pojo = "CrmLeadCustomData";
        
        public static final String USERS_CLASSPATH = "com.krawler.common.admin.User";
        public static final String COMPANY_CLASSPATH = "com.krawler.common.admin.Company";
        public static final String GOAL_MANAGEMENT_CLASSPATH = "com.krawler.crm.database.tables.Finalgoalmanagement";
        public static final String GOAL_TYPE_CLASSPATH = "com.krawler.crm.database.tables.GoalType";

        public static final String CRM_ACTIVITY_CLASSPATH = "com.krawler.crm.database.tables.CrmActivityMaster";
        public static final String CRM_CAMPAIGN_CLASSPATH = "com.krawler.crm.database.tables.CrmCampaign";
        
        public static final String Crm_account_custom_data_classpath = "com.krawler.crm.database.tables.CrmAccountCustomData";
        public static final String Crm_account_classpath = "com.krawler.crm.database.tables.CrmAccount";
        public static final String Crm_account_custom_data_pojo = "CrmAccountCustomData";

        public static final String Crm_case_custom_data_classpath = "com.krawler.crm.database.tables.CrmCaseCustomData";
        public static final String Crm_case_classpath = "com.krawler.crm.database.tables.CrmCase";
        public static final String Crm_case_custom_data_pojo = "CrmCaseCustomData";

        public static final String Crm_product_custom_data_classpath = "com.krawler.crm.database.tables.CrmProductCustomData";
        public static final String Crm_product_classpath = "com.krawler.crm.database.tables.CrmProduct";
        public static final String Crm_product_custom_data_pojo = "CrmProductCustomData";

        public static final String Crm_opportunity_custom_data_classpath = "com.krawler.crm.database.tables.CrmOpportunityCustomData";
        public static final String Crm_opportunity_classpath = "com.krawler.crm.database.tables.CrmOpportunity";
        public static final String Crm_opportunity_custom_data_pojo = "CrmOpportunityCustomData";

        public static final String Crm_contact_custom_data_classpath = "com.krawler.crm.database.tables.CrmContactCustomData";
        public static final String Crm_contact_classpath = "com.krawler.crm.database.tables.CrmContact";
        public static final String Crm_contact_custom_data_pojo = "CrmContactCustomData";

        public static final String Crm_account_pojo = "CrmAccount";
        public static final String Crm_lead_pojo = "CrmLead";
        public static final String Crm_case_pojo = "CrmCase";
        public static final String Crm_product_pojo = "CrmProduct";
        public static final String Crm_opportunity_pojo = "CrmOpportunity";
        public static final String Crm_contact_pojo = "CrmContact";
        public static final String Crm_campaign_pojo = "CrmCampaign";

        public static final String Crm_account_table = "crm_account";
        public static final String Crm_lead_table = "crm_lead";
        public static final String Crm_case_table = "crm_case";
        public static final String Crm_product_table = "crm_product";
        public static final String Crm_opportunity_table = "crm_opportunity";
        public static final String Crm_contact_table = "crm_contact";
        public static final String Crm_campaign_table = "crm_campaign";
        
        public static final String Crm_lead_product_key = Constants.Crm_lead_table+"#"+Constants.Crm_productid;
        public static final String Crm_opportunity_product_key = Constants.Crm_opportunity_table+"#"+Constants.Crm_productid;
        public static final String Crm_account_product_key = Constants.Crm_account_table+"#"+Constants.Crm_productid;
        public static final String Crm_case_product_key = Constants.Crm_case_table+"#"+Constants.Crm_productid;
        
        public static final String Crm_account_pojo_ref = "CrmAccountCustomDataobj";
        public static final String Crm_lead_pojo_ref = "CrmLeadCustomDataobj";
        public static final String Crm_case_pojo_ref = "CrmCaseCustomDataobj";
        public static final String Crm_product_pojo_ref = "CrmProductCustomDataobj";
        public static final String Crm_opportunity_pojo_ref = "CrmOpportunityCustomDataobj";
        public static final String Crm_contact_pojo_ref = "CrmContactCustomDataobj";

        public static final String Crm_account_product_pojo_ref = "AccountProducts";
        public static final String Crm_lead__productpojo_ref = "LeadProducts";
        public static final String Crm_case_productpojo_ref = "CaseProducts";
        public static final String Crm_opportunity_product_pojo_ref = "OppurtunityProducts";

        public static final String Crm_account_product_pojo_table = "crm_accountProducts";
        public static final String Crm_lead__productpojo_table = "crm_leadProducts";
        public static final String Crm_case_productpojo_table = "crm_caseProducts";
        public static final String Crm_opportunity_product_pojo_table = "crm_oppurtunityProducts";

        public static final String Searchjson = "Searchjson";
        public static final String appendCase = "appendCase";
        public static final String myResult = "myResult";
        public static final String moduleid="moduleid";

        public static final HashMap<String,String> defaultLeadConvertColumns = new HashMap<String,String>();
        static{
            defaultLeadConvertColumns.put("accountname", Crm_Account_modulename);
            defaultLeadConvertColumns.put("accountownerid", Crm_Account_modulename);
            
            defaultLeadConvertColumns.put("closingdate", Crm_Opportunity_modulename);
            defaultLeadConvertColumns.put("oppownerid", Crm_Opportunity_modulename);
            defaultLeadConvertColumns.put("oppname", Crm_Opportunity_modulename);
            defaultLeadConvertColumns.put("oppstageid", Crm_Opportunity_modulename);
            defaultLeadConvertColumns.put("accountnameid", Crm_Opportunity_modulename);

            defaultLeadConvertColumns.put("lastname", Crm_Contact_modulename);
            defaultLeadConvertColumns.put("contactownerid", Crm_Contact_modulename);
        }

        public static final String GOAL_TYPE_NO_OF_LEADS = "No of Leads";
        public static final String GOAL_TYPE_LEAD_REVENUE = "Total revenue from closed leads";
        public static final String GOAL_TYPE_NO_OF_ACCOUNTS = "No of Accounts";
        public static final String GOAL_TYPE_ACCOUNT_REVENUE = "Total revenue from accounts";
        public static final String GOAL_TYPE_NO_OF_OPPORTUNITY = "No of Opportunities";
        public static final String GOAL_TYPE_OPPORTUNITY_REVENUE = "Total salesamount from opportunities";

        public static final String EMAIL_TEMPLATE_DEFAULT_TYPE = "DefaultTemplates";

        public static final String EMAILTYPE_STATUSREPORT = "Status Report";
        public static final String EMAILTYPE_STATUSREPORT_ID = "1";

        public static final String DEFAULT_RIGHTCOLUMN_TEMPLATEID = "2";
        public static final String DEFAULT_LEFTCOLUMN_TEMPLATEID = "3";
        public static final String DEFAULT_BASIC_TEMPLATEID = "4";
        public static final String DEFAULT_RICHTEXT_TEMPLATEID = "5";
        public static final String DEFAULT_POSTCARD_TEMPLATEID = "6";

        public static final String KRAWLERBUSINESSSOFTWARE_COMPANYID = "963b6fdc-316a-4a5f-b430-6a05c5b34363";

        public static final String DEFAULT_CALENDAR_NAME = "CRM Activities";

        public static final String activityPropertyName = "crmActivityMaster.activityid";

        public static final String CRM_CUSTOM_ACCOUNT_TABLE = "crmaccountcustomdata";
        public static final String CRM_CUSTOM_LEAD_TABLE = "crmleadcustomdata";
        public static final String CRM_CUSTOM_CONTACT_TABLE = "crmcontactcustomdata";
        public static final String CRM_CUSTOM_OPPORTUNITY_TABLE = "crmopportunitycustomdata";
        public static final String CRM_CUSTOM_CASE_TABLE = "crmcasecustomdata";
        public static final String CRM_CUSTOM_PRODUCT_TABLE = "crmproductcustomdata";
        
        public static final HashMap<String,Integer> moduleMap = new HashMap<String,Integer>();
            static{
            moduleMap.put("Lead", 1);
            moduleMap.put("Opportunity", 2);
            moduleMap.put("Account", 3);
            moduleMap.put("Contact", 4);
            moduleMap.put("Product", 5);
            moduleMap.put("Case", 6);
            moduleMap.put("Activity", 7);
            moduleMap.put("Campaign", 8);
        }

        public static final HashMap<String,String> modulenameMap = new HashMap<String,String>();
            static{
            modulenameMap.put("Lead", "Lead");
            modulenameMap.put("Opportunity", "Opportunity");
            modulenameMap.put("Account", "Account");
            modulenameMap.put("Contact", "Contact");
            modulenameMap.put("Product", "Product");
            modulenameMap.put("Case", "Case");
            modulenameMap.put("Activity", "Activity");
            modulenameMap.put("Campaign", "Campaign");
        }

//    public static final String tzdateRenderer = "function(v) { if(!v||!(v instanceof Date)) return v;"+
//    	"return new Date(v.getTime()+1*(v.getTimezoneOffset()*60000+Wtf.pref.tzoffset)).format(WtfGlobal.getOnlyDateFormat());}";
////    public static final String currencyRenderer = "function(value) { var v=parseFloat(value);if(isNaN(v)) return '';v = (Math.round((v-0)*100))/100;v = (v == Math.floor(v)) ? v + '.00' : ((v*10 == Math.floor(v*10)) ? v + '0' : v); v = String(v);var ps = v.split('.');var whole = ps[0]"+
////            "var sub = ps[1] ? '.'+ ps[1] : '.00';var r = /(\\d+)(\\d{3})/;while (r.test(whole)) {whole = whole.replace(r, '$1' + ',' + '$2');}"+
////            "v = whole + sub;if(v.charAt(0) == '-'){v= '-'+ WtfGlobal.getCurrencySymbol() + v.substr(1);}" +
////            "else{v= WtfGlobal.getCurrencySymbol() +' '+v;}" +
////            "return '<div class=\"currency\">'+v+'</div>';}";
//    public static final String currencyRenderer = "Currency";
//    public static final HashMap<String,String> rendererData = new HashMap<String,String>();
//    static{
//        rendererData.put("Date", tzdateRenderer);
//        rendererData.put("Currency", currencyRenderer);
//    }
        public static final HashMap<String,String> rendererData = new HashMap<String,String>();
        static{
            rendererData.put("Date", "Date");
            rendererData.put("Currency", "Currency");
            rendererData.put("Email", "Email");
        }

        public static final String CUSTOMREPORT_HIDEFIELD_LEAD ="'"+ACCOUNT_PRODUCT+"','"+OPPORTUNITY_PRODUCT+"','"+CASE_PRODUCT+"'";
        public static final String CUSTOMREPORT_HIDEFIELD_ACCOUNT ="'"+LEAD_PRODUCT+"','"+OPPORTUNITY_PRODUCT+"','"+CASE_PRODUCT+"'";
        public static final String CUSTOMREPORT_HIDEFIELD_OPPORTUNITY ="'"+LEAD_PRODUCT+"','"+ACCOUNT_PRODUCT+"','"+CASE_PRODUCT+"'";
        public static final String CUSTOMREPORT_HIDEFIELD_CASE ="'"+LEAD_PRODUCT+"','"+ACCOUNT_PRODUCT+"','"+OPPORTUNITY_PRODUCT+"'";
        public static final String CUSTOMREPORT_HIDEFIELD ="'"+LEAD_PRODUCT+"','"+ACCOUNT_PRODUCT+"','"+OPPORTUNITY_PRODUCT+"','"+CASE_PRODUCT+"'";

        public static final HashMap<String,String> leadJoinMap = new HashMap<String,String>();
            static{
            leadJoinMap.put("mainQuery", " from crm_lead inner join crm_leadOwners on crm_leadOwners.leadid = crm_lead.leadid " +
                    "left join defaultmasteritem dm on dm.id = crm_lead.leadstatusid");
            leadJoinMap.put("crm_leadOwners", " inner join crm_leadOwners on crm_lead.leadid = crm_leadOwners.leadid ");
            leadJoinMap.put("company", " inner join company on company.companyid = crm_lead.companyid ");
            leadJoinMap.put("crm_product", " left join crm_leadProducts on crm_lead.leadid = crm_leadProducts.leadid left join crm_product on crm_product.productid = crm_leadProducts.productid ");
            leadJoinMap.put("crmleadcustomdata", " left join crmleadcustomdata on crm_lead.leadid = crmleadcustomdata.leadid ");
        }

        public static final HashMap<String,String> accountJoinMap = new HashMap<String,String>();
            static{
            accountJoinMap.put("mainQuery", " from crm_account inner join crm_accountOwners on crm_accountOwners.accountid = crm_account.accountid ");
            accountJoinMap.put("crm_accountOwners", " inner join crm_accountOwners on crm_accountOwners.accountid = crm_account.accountid ");
            accountJoinMap.put("company", " inner join company on company.companyid = crm_account.companyid ");
            accountJoinMap.put("crm_product", " left join crm_product on crm_product.productid = crm_account.productid ");
            accountJoinMap.put("crm_lead", " left join crm_lead on crm_lead.leadid = crm_account.convertedleadid ");
            accountJoinMap.put("crmaccountcustomdata", " left join crmaccountcustomdata on crm_account.accountid = crmaccountcustomdata.accountid ");
            accountJoinMap.put("crmleadcustomdata", " left join crmleadcustomdata on crm_lead.leadid = crmleadcustomdata.leadid ");
        }

        public static final HashMap<String,String> contactJoinMap = new HashMap<String,String>();
            static{
            contactJoinMap.put("mainQuery", " from crm_contact inner join crm_contactOwners on crm_contactOwners.contactid = crm_contact.contactid ");
            contactJoinMap.put("crm_contactOwners", " inner join crm_contactOwners on crm_contactOwners.contactid = crm_contact.contactid ");
            contactJoinMap.put("company", " inner join company on company.companyid = crm_contact.companyid ");
//            contactJoinMap.put("crm_product", " left join crm_product on crm_product.productid = crm_contact.productid ");
            contactJoinMap.put("crmcontactcustomdata", " left join crmcontactcustomdata on crm_contact.contactid = crmcontactcustomdata.contactid ");
            contactJoinMap.put("crm_lead", " left join crm_lead on crm_lead.leadid = crm_contact.leadid ");
            contactJoinMap.put("crmleadcustomdata", " left join crmleadcustomdata on crm_lead.leadid = crmleadcustomdata.leadid ");
            contactJoinMap.put("crm_account", " left join crm_account on crm_account.accountid = crm_contact.accountnameid ");
            contactJoinMap.put("crmaccountcustomdata", " left join crmaccountcustomdata on crm_account.accountid = crmaccountcustomdata.accountid ");
        }

        public static final HashMap<String,String> opportunityJoinMap = new HashMap<String,String>();
            static{
            opportunityJoinMap.put("mainQuery", " from crm_opportunity inner join crm_opportunityOwners on crm_opportunityOwners.opportunityid = crm_opportunity.oppid ");
            opportunityJoinMap.put("crm_opportunityOwners", " inner join crm_opportunityOwners on crm_opportunityOwners.opportunityid = crm_opportunity.oppid ");
            opportunityJoinMap.put("company", " inner join company on company.companyid = crm_opportunity.companyid ");
            opportunityJoinMap.put("crmopportunitycustomdata", " left join crmopportunitycustomdata on crm_opportunity.oppid = crmopportunitycustomdata.oppid ");
            opportunityJoinMap.put("crm_lead", " left join crm_lead on crm_lead.leadid = crm_opportunity.convertedleadid ");
            opportunityJoinMap.put("crmleadcustomdata", " left join crmleadcustomdata on crm_lead.leadid = crmleadcustomdata.leadid ");
            opportunityJoinMap.put("crm_account", " left join crm_account on crm_account.accountid = crm_opportunity.accountnameid ");
            opportunityJoinMap.put("crmaccountcustomdata", " left join crmaccountcustomdata on crm_account.accountid = crmaccountcustomdata.accountid ");
        }

        public static final HashMap<String,String> productJoinMap = new HashMap<String,String>();
            static{
            productJoinMap.put("mainQuery", " from crm_product ");
            productJoinMap.put("company", " inner join company on company.companyid = crm_product.companyid ");
            productJoinMap.put("crmproductcustomdata", " left join crmproductcustomdata on crm_product.productid = crmproductcustomdata.productid ");
            productJoinMap.put("defaultmasteritem.category", " left join defaultmasteritem on crm_product.categoryid = defaultmasteritem.id ");
        }

        public static final HashMap<String,String> caseJoinMap = new HashMap<String,String>();
            static{
            caseJoinMap.put("mainQuery", " from crm_case  ");
            caseJoinMap.put("company", " inner join company on company.companyid = crm_account.companyid ");
            caseJoinMap.put("crmcasecustomdata", " left join crmcasecustomdata on crm_case.caseid = crmcasecustomdata.caseid ");
            caseJoinMap.put("crm_account", " left join crm_account on crm_account.accountid = crm_case.accountnameid ");
            caseJoinMap.put("crmaccountcustomdata", " left join crmaccountcustomdata on crm_account.accountid = crmaccountcustomdata.accountid ");
            caseJoinMap.put("crm_contact", " left join crm_contact on crm_contact.contactid = crm_case.contactnameid ");
            caseJoinMap.put("crmcontactcustomdata", " left join crmcontactcustomdata on crm_contact.contactid = crmcontactcustomdata.contactid ");
        }

        public static final int AUTONUM_QUOTATION = 50;
        public static final String PATTERN_QUOTATION = "QN000000";
        public static final String QUOTATION_TABLE = "Quotation";
        public static final String QUOTATION_FIELD = "quotationNumber";
        public static final String USER_FLAG="1";
        public static final String RELATEDTO_ID="6";
        public static final String DELETED="F";
        public static final String None = "None";
        public static final String DESKERA_APPLICATION_ID_CRM = "2";
}
