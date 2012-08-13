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

public class AuditAction {
    public final static String CAMPAIGN_CREATE = "20";
    public final static String CAMPAIGN_UPDATE = "21";
    public final static String CAMPAIGN_DELETE = "22";
    public final static String CAMPAIGN_ADD_COMMENTS = "23";
    public final static String CAMPAIGN_EXPORT = "24";
    public final static String CAMPAIGN_ARCHIVE = "25";
    public final static String CAMPAIGN_ACTIVITY_EXPORT = "26";

    
    public final static String LEAD_CREATE = "30";
    public final static String LEAD_QUOTATIONCREATE = "78";
    public final static String LEAD_QUOTATIONSENT = "79";
    public final static String LEAD_UPDATE = "31";
    public final static String LEAD_DELETE = "32";
    public final static String LEAD_ADD_COMMENTS = "33";
    public final static String LEAD_EXPORT = "34";
    public final static String LEAD_CONVERT = "35";
    public final static String LEAD_ARCHIVE = "36";
    public final static String LEAD_IMPORT = "37";
    public final static String LEAD_ACTIVITY_EXPORT = "38";
    public final static String LEAD_CONTACT_EXPORT = "39";

    public final static String CONTACT_CREATE = "40";
    public final static String CONTACT_UPDATE = "41";
    public final static String CONTACT_DELETE = "42";
    public final static String CONTACT_ADD_COMMENTS = "43";
    public final static String CONTACT_EXPORT = "44";
    public final static String CONTACT_IMPORT = "45";
    public final static String CONTACT_ARCHIVE = "46";
    public final static String CONTACT_ACTIVITY_EXPORT = "47";

    public final static String PRODUCT_CREATE = "50";
    public final static String PRODUCT_UPDATE = "51";
    public final static String PRODUCT_DELETE = "52";
    public final static String PRODUCT_ADD_COMMENTS = "53";
    public final static String PRODUCT_EXPORT = "54";
    public final static String PRODUCT_ARCHIVE = "55";
    public final static String PRODUCT_IMPORT_ACCOUNTING = "56";


    public final static String ACCOUNT_CREATE = "60";
    public final static String ACCOUNT_UPDATE = "61";
    public final static String ACCOUNT_DELETE = "62";
    public final static String ACCOUNT_ADD_COMMENTS = "63";
    public final static String ACCOUNT_EXPORT = "64";
    public final static String ACCOUNT_ARCHIVE = "65";
    public final static String ACCOUNT_IMPORT = "66";
    public final static String ACCOUNT_ACTIVITY_EXPORT = "67";
    public final static String ACCOUNT_OPPORTUNITY_EXPORT = "68";
    public final static String ACCOUNT_CONTACT_EXPORT = "69";
    public final static String ACCOUNT_CASE_EXPORT = "349";
    public final static String ACCOUNT_QUOTATIONCREATE = "96";
    public final static String ACCOUNT_QUOTATIONSENT = "97";


    public final static String OPPORTUNITY_CREATE = "70";
    public final static String OPPORTUNITY_UPDATE = "71";
    public final static String OPPORTUNITY_DELETE = "72";
    public final static String OPPORTUNITY_ADD_COMMENTS = "73";
    public final static String OPPORTUNITY_EXPORT = "74";
    public final static String OPPORTUNITY_ARCHIVE = "75";
    public final static String OPPORTUNITY_CONTACT_EXPORT = "76";
    public final static String OPPORTUNITY_ACTIVITY_EXPORT = "77";


    public final static String CASE_CREATE = "80";
    public final static String CASE_UPDATE = "81";
    public final static String CASE_DELETE = "82";
    public final static String CASE_ADD_COMMENTS = "83";
    public final static String CASE_EXPORT = "84";
    public final static String CASE_ARCHIVE = "85";
    public final static String CASE_ACTIVITY_EXPORT = "86";
    

    public final static String ACTIVITY_CREATE = "90";
    public final static String ACTIVITY_UPDATE = "91";
    public final static String ACTIVITY_DELETE = "92";
    public final static String ACTIVITY_ADD_COMMENTS = "93";
    public final static String ACTIVITY_EXPORT = "94";
    public final static String ACTIVITY_ARCHIVE = "95";

    public final static String TARGET_IMPORT = "300";
    public final static String TARGET_EXPORT = "301";
    public final static String TARGET_DELETE = "302";
    public final static String TARGET_CREATE = "303";
    public final static String TARGET_UPDATE = "304";
    public final static String TARGET_ARCHIVE = "305";

    public final static String CAMPAIGN_DOC_UPLOAD = "100";
    public final static String LEAD_DOC_UPLOAD = "101";
    public final static String CONTACT_DOC_UPLOAD = "102";
    public final static String PRODUCT_DOC_UPLOAD = "103";
    public final static String ACCOUNT_DOC_UPLOAD = "104";
    public final static String OPPORTUNITY_DOC_UPLOAD = "105";
    public final static String CASE_DOC_UPLOAD = "106";
    public final static String ACTIVITY_DOC_UPLOAD = "107";
    public final static String MY_DOC_UPLOAD = "109";
    public final static String CAMPAIGN_DOC_DOWNLOAD = "120";
    public final static String LEAD_DOC_DOWNLOAD = "121";
    public final static String CONTACT_DOC_DOWNLOAD = "122";
    public final static String PRODUCT_DOC_DOWNLOAD = "123";
    public final static String ACCOUNT_DOC_DOWNLOAD = "124";
    public final static String OPPORTUNITY_DOC_DOWNLOAD = "125";
    public final static String CASE_DOC_DOWNLOAD = "126";
    public final static String ACTIVITY_DOC_DOWNLOAD = "127";
    public final static String MY_DOC_DOWNLOAD = "129";
//    public final static String DOC_TAG_ADDED = "128";
    public final static String CAMPAIGN_DOC_DELETED = "130";
    public final static String LEAD_DOC_DELETED = "131";
    public final static String CONTACT_DOC_DELETED = "132";
    public final static String PRODUCT_DOC_DELETED = "133";
    public final static String ACCOUNT_DOC_DELETED = "134";
    public final static String OPPORTUNITY_DOC_DELETED = "135";
    public final static String CASE_DOC_DELETED = "136";
    public final static String ACTIVITY_DOC_DELETED = "137";
    public final static String MY_DOC_DELETED = "139";

//    public final static String ADMIN_Role = "150";
//    public final static String ADMIN_Permission = "151";
    public final static String ADMIN_Organization = "152";

    public final static String Lead_By_Industry = "200";
    public final static String Converted_Lead = "201";
    public final static String Opp_Source = "202";
    public final static String Opp_Stage = "203";
    public final static String Case_By_Status = "204";
    public final static String Key_Account = "205";
    public final static String Sales_By_Source = "206";
    public final static String Leads_By_Source = "207";
    public final static String Opp_by_Stage = "208";
    public final static String Opp_by_SalesPerson = "251";
    public final static String Opp_by_Region = "252";
    public final static String Revenue_by_OppSource = "209";
    public final static String Closed_Opp = "210";
    public final static String Opp_by_Type = "211";
    public final static String Opp_Product = "212";
    public final static String Stuck_Opp = "213";
    public final static String Monthly_Accounts = "214";
    public final static String Accounts_Owners = "215";
    public final static String Sources_Opp = "216";
    public final static String High_Pri_Activity = "217";
    public final static String Contact_High_Pri_Cases = "218";
    public final static String Product_High_Pri_Cases = "219";
    public final static String Account_High_Pri_Cases = "220";
    public final static String Monthly_Cases = "221";
    public final static String Industry_Account = "222";
    public final static String Completed_Campaigns = "223";
    public final static String Qualified_Leads = "224";
    public final static String Accounts_Contacts = "225";
    public final static String Camp_Good_Res = "226";
    public final static String Contacted_Leads = "227";
    public final static String Contacts_Lead_Source = "228";
    public final static String Accounts_Opportunity = "229";
    public final static String Opp_Lead_Source = "230";
    public final static String Newly_Added_Cases = "231";
    public final static String Pending_Cases = "232";
    public final static String Escalated_Cases = "233";
    public final static String Account_Cases = "234";
    public final static String Open_Leads = "235";
    public final static String Contact_Cases = "236";
    public final static String Converted_Leads_Accounts = "237";
    public final static String Converted_Leads_Opp = "238";
    public final static String Converted_Leads_Contact = "239";
    public final static String Targets_Owner = "240";
    public final static String Campaign_Type = "241";

    public final static String User_Profile_update = "250";
}
