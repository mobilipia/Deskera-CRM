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

package com.krawler.common.wrapper;

import java.util.Arrays;
import java.util.HashMap;

public class staticUrlMapping {

    public final static HashMap<String,Object> staticurlmap = new HashMap<String, Object>();
    
    public staticUrlMapping() {
        
        /*-----------accountingintegration-servlet.xml-----------*/
        staticurlmap.put("/crm/Integration/AccountIntegration/*.do","accIntController");


        /*-----------calendar-servlet.xml-----------*/
        staticurlmap.put("/calendar/common/*.do","calCommonController");
        staticurlmap.put("/calendar/calendarevent/*.do","calendarEventController");
        staticurlmap.put("/calendar/calendar/*.do","calendarController");

        /*-----------common-servlet.xml-----------*/
        staticurlmap.put("/crm/common/Lead/*.do","crmLeadCommonController");
        staticurlmap.put("/crm/common/GoogleContacts/*.do","crmGContactsController");
        staticurlmap.put("/crm/common/ImportRecords/*.do","crmImportController");
        staticurlmap.put("/crm/common/DetailPanel/*.do","detailPanelController");
        staticurlmap.put("/crm/common/Archive/*.do","archiveHandlerController");
        staticurlmap.put("/crm/common/HRMSIntegration/*.do","hrmsIntController");
        staticurlmap.put("/crm/common/PROJECTIntegration/*.do","projectIntController");
        staticurlmap.put("/crm/common/ZohoImportRecords/*.do","zohoImportController");
        staticurlmap.put("/crm/common/GlobalSearch/*.do","globalSearchController");
        staticurlmap.put("/crm/common/Document/*.do","crmDocumentController");
        staticurlmap.put("/crm/common/notification/*.do","sendemailnotification");
        staticurlmap.put("/crm/common/Document/*.do","crmDocumentController");
        staticurlmap.put("/crm/common/crmCommonHandler/*.do","crmCommonHandlerController");
        staticurlmap.put("/crm/common/notification/*.do","sendemailnotification");
        staticurlmap.put("/crm/common/fieldmanager/*.do","fieldmanagerController");

         /*-----------crmcalendarexport-servlet.xml-----------*/
        staticurlmap.put("/exportICS.ics","crmCalendarExportcontroller");

        /*-----------crmRemoteAPI-servlet.xml-----------*/
        staticurlmap.put("/remoteapi.jsp","crmRemoteAPIcontroller");

        /*-----------crmdashboard-servlet.xml-----------*/
        staticurlmap.put("/crm/Dashboard/*.do","crmDashboardController");
        /*-----------crmtree-servlet.xml-----------*/
        staticurlmap.put("/crm/crmTree/*.do","crmTreeController");

        /*-----------dispatcher-servlet.xml-----------*/
        staticurlmap.put("/Common/CRMManager/*.do","crmManagerController");
        staticurlmap.put("/Common/User/*.do","crmUserController");
        staticurlmap.put("/Common/AuditTrail/*.do","auditTrailController");
        staticurlmap.put("/Common/OrganizationChart/*.do","organizationChartController");
        staticurlmap.put("/Common/ProfileHandler/*.do","profileHandlerController");
        staticurlmap.put("/Common/PermissionHandler/*.do","permissionHandlercontroller");
        staticurlmap.put("/Common/AuthHandler/*.do","authHandlercontroller");
        staticurlmap.put("/Common/CompanyDetails/*.do","companyDetailsController");
        staticurlmap.put("/Common/KwlCommonTables/*.do","kwlCommonTablesController");
        staticurlmap.put("/Common/Comment/*.do","crmCommentController");
        staticurlmap.put("/Common/ChartXmlSetting/*.do","chartXmlSettingController");
        staticurlmap.put("/Common/ExportPdfTemplate/*.do","exportPdfTemplateController");
        staticurlmap.put("/Common/Spreadsheet/*.do","spreadSheetController");
        staticurlmap.put("/Common/FirstRunHelp/*.do","firstRunHelpController");
        staticurlmap.put("/Common/CRMCommon/*.do","crmCommonController");
        staticurlmap.put("/Common/MailIntegration/*.do","mailIntegrationcontroller");
        staticurlmap.put("/Common/ImportRecords/*.do","importcontroller");
        staticurlmap.put("/Common/error.do","errorMessagecontroller");

        /*-----------account-servlet.xml-----------*/
        staticurlmap.put("/crm/Account/action/*.do","crmAccountController");
        staticurlmap.put("/crm/Account/accountReport/*.do","accountReportController");

        /*-----------activity-servlet.xml-----------*/
        staticurlmap.put("/crm/Activity/action/*.do","crmActivityController");
        staticurlmap.put("/crm/Activity/activityReport/*.do","activityReportController");

        /*-----------target-servlet.xml-----------*/
        staticurlmap.put("/crm/Target/action/*.do","crmTargetController");
        staticurlmap.put("/crm/Target/targetReport/*.do","targetReportController");

        /*-----------case-servlet.xml-----------*/
        staticurlmap.put("/crm/Case/action/*.do","crmCaseController");
        staticurlmap.put("/crm/Case/caseReport/*.do","caseReportController");

        /*-----------contact-servlet.xml-----------*/
        staticurlmap.put("/crm/Contact/action/*.do","crmContactController");
        staticurlmap.put("/crm/Contact/contactReport/*.do","contactReportController");

        /*-----------lead-servlet.xml-----------*/
        staticurlmap.put("/crm/Lead/action/*.do","crmLeadController");
        staticurlmap.put("/crm/Lead/leadReport/*.do","leadReportController");

        /*-----------opportunity-servlet.xml-----------*/
        staticurlmap.put("/crm/Opportunity/action/*.do","crmOpportunityController");
        staticurlmap.put("/crm/Opportunity/opportunityReport/*.do","opportunityReportController");

        /*-----------product-servlet.xml-----------*/
        staticurlmap.put("/crm/Product/action/*.do","crmProductController");

        /*-----------widget-servlet.xml-----------*/
        staticurlmap.put("/Dashboard/*.do","widgetController");

        /*-----------campaign-servlet.xml-----------*/
        staticurlmap.put("/crm/Campaign/action/*.do","crmCampaignController");
        staticurlmap.put("/crm/Campaign/campaignReport/*.do","campaignReportController");

        /*-----------emailmarketing-servlet.xml-----------*/
        staticurlmap.put("/crm/emailMarketing/action/*.do","crmEmailMarketingController");
        staticurlmap.put("/crm/emailMarketing/mail/*.do","sendScheduleCampaign");

        /*-----------emailViewer-servlet.xml-----------*/
        staticurlmap.put("/newsletter.jsp","emailViewercontroller");

        /*-----------crmReportBuilderAPI-servlet.xml-----------*/
        staticurlmap.put("crm/CustomReport/rbuild/*.do","CrmReportBuilderController");
    }
}
