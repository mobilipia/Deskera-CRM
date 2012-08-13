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
package com.krawler.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A place to keep commonly-used constants.
 */
public class Constants {
    //Image Path
    public static final String DESKERA ="Deskera";
    public static final String CRMURL ="crmurl";
    public static final String tzdiff="tzdiff";
    public static final String GMT="GMT";
    public static final String createdon="createdon";
    public static final String stringInitVal = "";
    public static final String ImgBasePath = "images/store/";
    private static final String defaultImgPath = "images/defaultuser.png";
    private static final String defaultCompanyImgPath = "images/logo.gif";
    public static final String filter_names = "filter_names";
    public static final String data = "data";
    public static final String Searchjson = "Searchjson";
    public static final String appendCase = "appendCase";
    public static final String myResult = "myResult";
    public static final String success = "success";
    public static final String Refcolnum = "Refcolnum";
    public static final String Colnum = "Colnum";
    public static final String hql = "hql";
    public static final String success1 = "success1";
    public static final String msg1 = "msg1";
    public static final String success2 = "success2";
    public static final String msg2 = "msg2";
    public static final String defaultvalue = "defaultvalue";
    public static final String Fieldtype = "Fieldtype";
    public static final String msg = "msg";
    public static final String model = "model";
    public static final String filter_values = "filter_values";
    public static final String filter_params = "filter_params";
    public static final String Crm_account = "crm_account";
    public static final String Crm_lead = "crm_lead";
    public static final String Crm_case = "crm_case";
    public static final String Crm_product = "crm_product";
    public static final String Crm_opportunity = "crm_opportunity";
    public static final String Crm_contact = "crm_contact";

    public static final String Crm_accountid = "accountid";
    public static final String Crm_leadid = "leadid";
    public static final String Crm_caseid = "caseid";
    public static final String Crm_productid = "productid";
    public static final String Crm_opportunityid = "oppid";
    public static final String Crm_contactid = "contactid";
    public static final String Crm_campaignid = "campaignid";
    public static final String Crm_activityid = "activityid";

    public static final String Crm_account_modulename = "account";
    public static final String Crm_lead_modulename = "lead";
    public static final String Crm_case_modulename = "cases";
    public static final String Crm_product_modulename = "product";
    public static final String Crm_opportunity_modulename = "opportunity";
    public static final String Crm_contact_modulename = "contact";

    public static final String Crm_Leadid = "Leadid";
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
    public static final String Crm_Lead_modulename = "Lead";
    public static final String Crm_Account_modulename = "Account";
    public static final String Crm_Case_modulename = "Case";
    public static final String Crm_Contact_modulename = "Contact";
    public static final String Crm_Product_modulename = "Product";
    public static final String Crm_Opportunity_modulename = "Opportunity";
    public static final String CRM_CAMPAIGN_MODULENAME = "Campaign";
    public static final String CRM_ACTIVITY_MODULENAME = "Activity";
    public static final String CRM_TARGET_MODULENAME = "TargetModule";

    public static final String Crm_custom_field = "fieldname";
    public static final String Crm_custom_fieldId = "fieldid";
    public static final int CUSTOM_FIELD_AUTONUMBER = 9;
    public static final String CUSTOM_FIELD_PREFIX = "prefix";
    public static final String CUSTOM_FIELD_SUFFIX = "suffix";
    public static final String AUTOCUSTOMFIELD = "autocustomfield";
    public static final String field_data_undefined = "undefined";
    public static final String USERS_CLASSPATH = "com.krawler.common.admin.User";
    public static final String VARIABLEDATA="VARIABLEDATA";
    public static final String Crm_lead_custom_data_classpath = "com.krawler.crm.database.tables.CrmLeadCustomData";
    public static final String Crm_lead_classpath = "com.krawler.crm.database.tables.CrmLead";
    public static final String Crm_lead_custom_data_pojo = "CrmLeadCustomData";

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

    public static final String Crm_users_pojo = "User";
    public static final String Crm_account_pojo = "CrmAccount";
    public static final String Crm_lead_pojo = "CrmLead";
    public static final String Crm_case_pojo = "CrmCase";
    public static final String Crm_product_pojo = "CrmProduct";
    public static final String Crm_opportunity_pojo = "CrmOpportunity";
    public static final String Crm_contact_pojo = "CrmContact";
    public static final String Crm_campaign_pojo = "CrmCampaign";
    public static final String CRM_ACTIVITY_POJO = "CrmActivityMaster";
    public static final String Crm_target_pojo = "TargetModule";

    public static final String Crm_account_pojo_ref = "CrmAccountCustomDataobj";
    public static final String Crm_lead_pojo_ref = "CrmLeadCustomDataobj";
    public static final String Crm_case_pojo_ref = "CrmCaseCustomDataobj";
    public static final String Crm_product_pojo_ref = "CrmProductCustomDataobj";
    public static final String Crm_opportunity_pojo_ref = "CrmOpportunityCustomDataobj";
    public static final String Crm_contact_pojo_ref = "CrmContactCustomDataobj";

    public static final String CRM_CUSTOM_ACCOUNT_TABLE = "crmaccountcustomdata";
    public static final String CRM_CUSTOM_LEAD_TABLE = "crmleadcustomdata";
    public static final String CRM_CUSTOM_CONTACT_TABLE = "crmcontactcustomdata";
    public static final String CRM_CUSTOM_OPPORTUNITY_TABLE = "crmopportunitycustomdata";
    public static final String CRM_CUSTOM_CASE_TABLE = "crmcasecustomdata";
    public static final String CRM_CUSTOM_PRODUCT_TABLE = "crmproductcustomdata";

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
    public static final String emailRegex ="^[\\w-]+([\\w!#$%&'*+/=?^`{|}~-]+)*(\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[\\w-]+)$";
    public static final String contactRegex = "^(\\(?\\+?[0-9]*\\)?)?[0-9_\\- \\(\\)]*$";

    public static final int SECONDS_PER_MINUTE = 60;
    public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * 60;
    public static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * 24;
    public static final int SECONDS_PER_WEEK = SECONDS_PER_DAY * 7;
    public static final int SECONDS_PER_MONTH = SECONDS_PER_DAY * 31;
    // IDS from Table crm_combodata
    public static final String LEADSTATUSID_QUALIFIED ="f01e5a6f-7011-4e2d-b93e-58b5c6270239";
    public static final String LEADSTATUSID_CONTACTED ="c5c96e60-16a2-4222-99a1-125d00fe80f1";
    public static final String LEADSTATUSID_OPEN ="5c74d621-304c-491a-9217-74acb18549b6";
    public static final String CASEPRIORITY_HIGH ="3a047d1c-9945-4361-bd91-26cac9be1d97";
    public static final String CASESTATUS_NOTSTARTED ="1434ca1a-c7f1-470b-951c-a9e804ab2f30";
    public static final String OPPSTAGEID_CLOSEDWON ="667946c6-f7b0-49ee-8040-26573b820d2e";
    public static final String CRMCOMBOMASTERID_CAMPAIGNSOURCE = "3b1e9726-12ea-4adf-a6ef-f0950075fec4";
    //Full MS-OUTLOOK CSV Header
    //public static final String[] CSV_HEADER_MSOUTLOOK = {"Title","First Name","Middle Name","Last Name","Suffix","Company","Department","Job Title","Business Street","Business Street 2","Business Street 3","Business City","Business State","Business Postal Code","Business Country/Region","Home Street","Home Street 2","Home Street 3","Home City","Home State","Home Postal Code","Home Country/Region","Other Street","Other Street 2","Other Street 3","Other City","Other State","Other Postal Code","Other Country/Region","Assistant's Phone","Business Fax","Business Phone","Business Phone 2","Callback","Car Phone","Company Main Phone","Home Fax","Home Phone","Home Phone 2","ISDN","Mobile Phone","Other Fax","Other Phone","Pager","Primary Phone","Radio Phone","TTY/TDD Phone","Telex","Account","Anniversary","Assistant's Name","Billing Information","Birthday","Business Address PO Box","Categories","Children","Directory Server","E-mail Address","E-mail Type","E-mail Display Name","E-mail 2 Address","E-mail 2 Type","E-mail 2 Display Name","E-mail 3 Address","E-mail 3 Type","E-mail 3 Display Name","Gender","Government ID Number","Hobby","Home Address PO Box","Initials","Internet Free Busy","Keywords","Language","Location","Manager's Name","Mileage","Notes","Office Location","Organizational ID Number","Other Address PO Box","Priority","Private","Profession","Referred By","Sensitivity","Spouse","User 1","User 2","User 3","User 4","Web Page"};
    //Header used for our contact export
        public static final String[] CSV_HEADER_MSOUTLOOK = {"\"First Name\"","\"E-mail Address\"","\"Business Phone\"","\"Business Street\""};

    public static final String CURRENCY_DEFAULT ="1";
    public static final String TIMEZONE_DEFAULT ="1";
    public static final String NEWYORK_TIMEZONE_ID ="23";
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
     public static final Map<String,String> JoinMap = new HashMap<String,String>();
        static{
        JoinMap.put("c.crmCombodataByOppstageid.ID", "c.crmCombodataByOppstageid");
        JoinMap.put("c.crmCombodataByOpptypeid.ID", "c.crmCombodataByOpptypeid");
        JoinMap.put("c.crmCombodataByRegionid.ID", "c.crmCombodataByRegionid");
        JoinMap.put("c.crmCombodataByLeadsourceid.ID", "c.crmCombodataByLeadsourceid");
        JoinMap.put("ao.usersByUserid.userID", "ao.usersByUserid");
        JoinMap.put("c.crmCombodataByAccounttypeid.ID", "c.crmCombodataByAccounttypeid");
        JoinMap.put("c.crmCombodataByIndustryid.ID", "c.crmCombodataByIndustryid");
        JoinMap.put("lo.usersByUserid.userID", "lo.usersByUserid");
        JoinMap.put("c.crmCombodataByLeadstatusid.ID", "c.crmCombodataByLeadstatusid");
        JoinMap.put("c.crmCombodataByRatingid.ID", "c.crmCombodataByRatingid");
        JoinMap.put("co.usersByUserid.userID", "co.usersByUserid");
        JoinMap.put("c.usersByUserid.userID", "c.usersByUserid");
        JoinMap.put("c.crmAccount.accountid", "c.crmAccount");
        JoinMap.put("c.crmContact.contactid", "c.crmContact");
        JoinMap.put("c.crmCombodataByCasestatusid.ID", "c.crmCombodataByCasestatusid");
        JoinMap.put("c.crmCombodataByCasepriorityid.ID", "c.crmCombodataByCasepriorityid");
        JoinMap.put("c.crmCombodataByCasetypeid.ID", "c.crmCombodataByCasetypeid");
        JoinMap.put("c.assignedto.userID", "c.assignedto");
        JoinMap.put("c.crmCombodataByCategoryid.ID", "c.crmCombodataByCategoryid");
        JoinMap.put("c.crmCombodataByCampaigntypeid.ID", "c.crmCombodataByCampaigntypeid");
        JoinMap.put("c.crmCombodataByCampaignstatusid.ID", "c.crmCombodataByCampaignstatusid");
    }

    // Ellipsis Length for Audit trail entry
    public static final int ELLIPSIS_LENGTH =35;
    public static final String auditDetailsRegex = "\\w{"+ELLIPSIS_LENGTH+",}";


    public static final int Custom_Column_Combo_limit = 10;
    public static final int Custom_Column_Master_limit = 10;
    public static final int Custom_Column_User_limit = 5;
    public static final int Custom_Column_Normal_limit = 35;

    public static final int Custom_Column_Combo_start = 0;
    public static final int Custom_Column_Master_start = 100;
    public static final int Custom_Column_User_start = 110;
    public static final int Custom_Column_Normal_start = 1000;
    public static final int query_batch_count=50;
    public static final int REMOTE_STORE_PAGE_LIMIT = 15;

    public static final String Custom_Column_Default_value = "null";
    public static final String Custom_Column_Prefix = "Col";
    public static final String Custom_Record_Prefix = "Custom_";
    public static final String Custom_column_Prefix = "col";
    public static final String DefaultTimeZone = "GMT"; // with GMT+00
    public static final String Custom_Column_Sep = ",";

    // ModuleId
    public static final String MODULEID_LEAD = "e1e72896-bf85-102d-b644-001e58a64cb6";
    public static final String MODULEID_ACCOUNT = "2904a010-7d32-11df-8c4a-0800200c9a66";
    public static final String MODULEID_CONTACT = "8B9B8DB6-7E03-11DF-BC3F-FC8FDFD72085";
    public static final String MODULEID_TARGET = "77D74BC2-82AD-11DF-B883-2774DFD72085";
    public static final String MODULEID_OPPORTUNITY = "14a254d9-2a2b-4c7f-8353-023586078f77";
    public static final String MODULEID_CASE = "41f2d2e6-349a-4864-8bcd-35c1aae9a227";
    public static final String MODULEID_PRODUCT = "ab60263f-7c72-4727-965c-85effb77e81f";
    public static final String MODULEID_CALIBRATION = "0ecc8ee0-c8ce-11e0-9572-0800200c9a66";
    public static final String MASTERCONFIG_HIDECOMBO = "'Campaign Source' ,'Event Status' ,'Salutation','Currency','Manufacturer','Reminder','Scale','Number Of Employees','Title','Tax','Contract Status','Product'";
    public static final String MASTERCONFIG_HIDECOMBO_EXTRA = "'Lead Type' ,'Related To','Reports To'";
    public static final String ROLENAME_ADMIN = "admin";
    public static final String ROLENAME_MANAGER = "manager";
    public static final String ROLENAME_EMPLOYEE = "emp";
    public static final String Crm_account_product_pojo_ref = "AccountProducts";
    public static final String Crm_lead__productpojo_ref = "LeadProducts";
    public static final String Crm_case_productpojo_ref = "CaseProducts";
    public static final String Crm_opportunity_product_pojo_ref = "OppurtunityProducts";
    public static final String Crm_targetid = "id";
    public static final String moduleid="moduleid";
    public static final String Cannotaddnew="Cannot add. Column with the same name already exists in the following modules- <br/>";
    public static final String joinname="joinname";
    public static final String inner="inner";
    public static final String root="root";
    public static final String xfield="xfield";
    public static final String field="field";
    public static final String xtype="xtype";
    public static final String searchText="searchText";
    public static final String column="column";
    public static final String iscustomcolumn="iscustomcolumn";
    public static final String combo="combo";
    public static final String fieldtype="fieldtype";
    public static final String refdbname="refdbname";
    public static final String Combo="Combo";
    public static final String Ref="Ref";
    public static final String dotID=".ID";
    public static final String dotid=".id";
    public static final String likeq=" like ? ";
    public static final String NineNine="99";
    public static final String select="select";
    public static final String datefield="datefield";
    public static final String searchjoin="searchjoin";
    public static final String or=" or ";
    public static final String and=" and ";
    public static final String dotRef=".Ref";
    public static final String seven="7";
    public static final String eight="8";
    public static final String four="4";
    public static final String dot=".";
    public static final String cdot="c.";
    public static final String c="c";
    public static final String C="C";
    public static final String percent="%";
    public static final String space=" ";
    public static final String join=" join ";
    public static final String joincdot=" join c. ";
    public static final String isnull=" is null ";
    public static final String MMMMdyyyy="MMMM d, yyyy";
    public static final String yyyyMMdd="yyyy-MM-dd";
    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String moduleidarray="moduleidarray";
    public static final String companyid = "companyid";
    public static final String validflag = "validflag";
    public static final String Validflag = "Validflag";
    public static final String defaultheader = "defaultheader";
    public static final String DefaultMasterItem = "DefaultMasterItem";
    public static final String deleteflag = "deleteflag";
    public static final String company_companyID = "company.companyID";
    public static final String INmoduleid="INmoduleid";
    public static final String Lead_Source="Lead Source";
    public static final String crmCombomaster_masterid="crmCombomaster.masterid";
    public static final String INcrmCombomaster_masterid="INcrmCombomaster.masterid";

    //Audit Trails Constants used for Lucene search
    public static final String AUDIT_INDEX_details = "details";
    public static final String AUDIT_INDEX_transactionId = "transactionId";
    public static final String AUDIT_INDEX_actionId = "actionId";
    public static final String AUDIT_INDEX_ipAddr = "ipAddr";
    public static final String AUDIT_INDEX_userName = "userName";
    public static final String AUDIT_INDEX_companyId = "companyId";
    public static final String AUDIT_INDEX_auditGroupId = "auditGroupId";
    public static final String AUDIT_INDEX_action = "action";
    public static final String AUDIT_INDEX_auditTime = "auditTime";
    public static final String Document = "29";
    public final static String CAMPAIGN_DOC_UPLOAD = "100";
    public final static String LEAD_DOC_UPLOAD = "101";
    public final static String CONTACT_DOC_UPLOAD = "102";
    public final static String PRODUCT_DOC_UPLOAD = "103";
    public final static String ACCOUNT_DOC_UPLOAD = "104";
    public final static String OPPORTUNITY_DOC_UPLOAD = "105";
    public final static String CASE_DOC_UPLOAD = "106";
    public final static String ACTIVITY_DOC_UPLOAD = "107";
    public final static String CAMPAIGN_DOC_DOWNLOAD = "120";
    public final static String LEAD_DOC_DOWNLOAD = "121";
    public final static String CONTACT_DOC_DOWNLOAD = "122";
    public final static String PRODUCT_DOC_DOWNLOAD = "123";
    public final static String ACCOUNT_DOC_DOWNLOAD = "124";
    public final static String OPPORTUNITY_DOC_DOWNLOAD = "125";
    public final static String CASE_DOC_DOWNLOAD = "126";
    public final static String ACTIVITY_DOC_DOWNLOAD = "127";
    public final static String CAMPAIGN_DOC_DELETED= "130";
    public final static String LEAD_DOC_DELETED = "131";
    public final static String CONTACT_DOC_DELETED = "132";
    public final static String PRODUCT_DOC_DELETED = "133";
    public final static String ACCOUNT_DOC_DELETED = "134";
    public final static String OPPORTUNITY_DOC_DELETED = "135";
    public final static String CASE_DOC_DELETED = "136";
    public final static String ACTIVITY_DOC_DELETED = "137";

    public static final String SESSION_COMPANY_ID = "companyid";
    public static final String SESSION_CURRENCY_ID = "currencyid";
    public static final String SESSION_TIMEZONE_ID = "timezoneid";
    public static final String SESSION_TZ_ID = "tzid";
    public static final String SESSION_TZDIFF = "tzdiff";
    public static final String SESSION_DATEFORMAT_ID = "dateformatid";
    public static final String SESSION_INITIALIZED = "initialized";
    public static final String SESSION_USERNAME = "username";
    public static final String SESSION_USERID = "userid";
    public static final String SESSION_COMPANY_NAME = "company";
    public static final String SESSION_SYS_EMAILID = "systememailid";
    public static final String SESSION_PARTNERNAME = "partnername";
    public static final String SESSION_ROLE_ID = "roleid";
    public static final String SESSION_CALL_WITH = "callwith";
    public static final String SESSION_COMPANY_PREF = "companyPreferences";
    public static final String SESSION_TIMEFORMAT = "timeformat";
    public static final String SESSION_NOTIFYON = "notifyon";
    public static final String SESSION_USERLIST = "recursiveUsersList";
    public static final String SESSION_USEROBJECT = "userObject";
    public static final String SESSION_USERCOMPANY = "userCompany";
    public static final String SESSION_LEADROUTING = "leadrouting";

    public static final String activityPropertyName = "crmActivityMaster.activityid";

    public static final String start = "start";
    public static final String limit = "limit";
    public static final String user_userID = "user.userID";
    public static final String updatedOn = "updatedOn";
    public static final String SESSION_CUSTOMER_EMAIL = "email";
	public static final String SESSION_CONTACT_ID = "contactid";
	public static final String SESSION_CONTACT_NAME = "contactname";
	public static final String SESSION_CUSTOMER_ID = "customerid";

    public static final String LEAD_REPORT_PERMNAME = "Lead Report";
    public static final String OPP_REPORT_PERMNAME = "OpportunityReport";
    public static final String SALES_REPORT_PERMNAME = "SalesReport";
    public static final String ACCOUNT_REPORT_PERMNAME = "AccountReport";
    public static final String CONTACT_REPORT_PERMNAME = "ContactReport";
    public static final String PRODUCT_REPORT_PERMNAME = "ProductReport";
    public static final String CASE_REPORT_PERMNAME = "CaseReport";
    public static final String ACTIVITY_REPORT_PERMNAME = "ActivityReport";
    public static final String CAMPAIGN_REPORT_PERMNAME = "CampaignReport";

}


