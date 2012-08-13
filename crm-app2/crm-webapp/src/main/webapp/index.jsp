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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title id="Deskeracrmtitle">CRM</title>
		<script type="text/javascript">
		/*<![CDATA[*/
			function _r(url){ window.top.location.href = url;}
		/*]]>*/
		</script>
        <!-- css -->
        <link rel="stylesheet" type="text/css" href="../../lib/resources/css/wtf-all.css"/>
        <link rel="stylesheet" type="text/css" href="../../style/view.css?v=3"/>
        <link rel="stylesheet" type="text/css" href="../../style/SpreadSheetLook.css"/>
        <link rel="stylesheet" type="text/css" href="../../style/portal.css?v=3"/>
        <!--link rel="stylesheet" type="text/css" href="../../spreadsheet/xtheme-blue.css?v=3"/>
        <link rel="stylesheet" type="text/css" href="../../spreadsheet/main.css?v=3"/-->
        <link rel="stylesheet" type="text/css" href="../../style/spreadsheet/spreadsheet.css?v=3"/>
        <link rel = "stylesheet" type = "text/css" href = "../../style/orgChart.css"></link>
        <link rel = "stylesheet" type = "text/css" href = "../../style/chart.css"></link>
        <link rel = "stylesheet" type = "text/css" href = "../../style/dashboardstyles.css"></link>
        <link rel = "stylesheet" type = "text/css" href = "../../style/template-editor.css"></link>
        <link rel = "stylesheet" type = "text/css" href = "../../style/cal.css"></link>
        <link rel = "stylesheet" type = "text/css" href = "../../style/ImportWizard.css"></link>
        <link rel = "stylesheet" type = "text/css" href = "../../style/quotations.css"></link>
        <link rel="stylesheet" type="text/css" href="../../style/modulebuilder.css?v=1"/>
	<!--[if lte IE 6]>
        <link rel="stylesheet" type="text/css" href="../../style/ielte6hax.css" />
    <![endif]-->
    <!--[if IE 7]>
            <link rel="stylesheet" type="text/css" href="../../style/ie7hax.css" />
    <![endif]-->
    <!--[if gte IE 8]>
            <link rel="stylesheet" type="text/css" href="../../style/ie8hax.css" />
    <![endif]-->
<!-- /css -->
		<link rel="shortcut icon" href="../../images/deskera/deskera.png"/>
	</head>
	<body>
		<div id="loading-mask" style="width:100%;height:100%;background:#c3daf9;position:absolute;z-index:20000;left:0;top:0;">&#160;</div>
		<div id="loading">
			<div class="loading-indicator"><img src="../../images/loading.gif" style="width:16px;height:16px; vertical-align:middle" alt="Loading" />&#160;Loading...</div>
		</div>
<!-- js -->
        <script type="text/javascript" src="../../lib/adapter/wtf/wtf-base.js"></script>
        <script type="text/javascript" src="../../lib/wtf-all-debug.js"></script>
        <script type="text/javascript" src="../../scripts/WtfLibOverride.js"></script>
        <script type="text/javascript" src="crm/common/wtf-lang-locale.js"></script>
        <script type="text/javascript" src="crm/common/msgs/messages.js"></script>
        <!--script type="text/javascript" src="../../lib/raphael-min.js"></script-->
        <script type="text/javascript" src="../../scripts/common/RowNumbererWithNew.js"></script>
        <script type="text/javascript" src="../../scripts/common/PagingStore.js"></script>
        <script type="text/javascript" src="../../scripts/common/WtfMultiSelectModel.js"></script>
        <script type="text/javascript" src="../../scripts/common/WtfClosableTab.js"></script>
        <script type="text/javascript" src="../../scripts/common/WtfKWLJsonReader.js"></script>
        <!--script type="text/javascript" src="../../scripts/common/expressionManager.js"></script-->
        <script type="text/javascript" src="../../scripts/common/WtfTextField.js"></script>
        <script type="text/javascript" src="../../scripts/common/BufferView.js"></script>
        <script type="text/javascript" src="../../scripts/common/WtfRowExpander.js"></script>
        <script type="text/javascript" src="../../scripts/common/quickadd.js"></script>
        <script type="text/javascript" src="../../scripts/common/WtfPagingMemProxy.js"></script>
        <script type="text/javascript" src="../../scripts/common/importInterface.js"></script>
        <script type="text/javascript" src="../../scripts/common/Encoder.js"></script>
        <script type="text/javascript" src="../../scripts/common/WtfCommetHandler.js"></script>
        <script type="text/javascript" src="../../scripts/common/KWLNotification.js"></script>
        <script type="text/javascript" src="../../scripts/common/MultiUpload.js"></script>
        <script type="text/javascript" src="../../scripts/WtfGlobal.js"></script>
        <script type="text/javascript" src="../../scripts/editHelp.js"></script>
        <script type="text/javascript" src="../../scripts/WtfSettings.js"></script>
        <script type="text/javascript" src="../../scripts/WtfChannel.js"></script>
        <script type="text/javascript" src="../../scripts/WtfPortal.js"></script>
        <script type="text/javascript" src="../../scripts/WtfMain-ex.js"></script>
        <script type="text/javascript" src="../../scripts/WtfWidgetComponent.js"></script>
        <script type="text/javascript" src="../../scripts/WtfCustomPanel.js"></script>
        <script type="text/javascript" src="../../scripts/deskeracrm/common/WtfNewSpreadSheet.js"></script>
        <script type="text/javascript" src="../../scripts/deskeracrm/common/WtfSpreadSheet.js"></script>
        <!--script type="text/javascript" src="../../scripts/deskeracrm/common/WtfSpreadSheet1.js"></script-->
        <!--script type="text/javascript" src="../../scripts/deskeracrm/common/WtfSpreadSheet1.js"></script-->
<script type="text/javascript" src="../../scripts/deskeracrm/common/Wizard.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/FormConvertLeadWindow.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/amchart.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/WtfGridRowExpander.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/GroupCheckboxSelection.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/AllReportTab.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/outboundEmail.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/userprofile.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/myGoals.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/goalforemployee.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/WtfAddComment.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/WtfUploadFile.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/DetailPanel.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/WtfGetDocsAndCommentList.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/advanceSearch.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/attributeComponent.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/WtfAlert.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/kComponents.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/WtfButton.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/WtfListPanel.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/WtfSearchBar.js"></script>
<!--script type="text/javascript" src="../../scripts/deskeracrm/common/csvMappingInterface.js"></script-->
<script type="text/javascript" src="../../scripts/deskeracrm/common/comboPlugin.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/WtfAuditTrail.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/activityDetailPanel.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/ExportInterface.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/gContact.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/WtfCrmCommonFunction.js"></script>
<script type="text/javascript" src="../../scripts/reportBuilder/builder.js"></script>
<script type="text/javascript" src="../../scripts/reportBuilder/reportForm.js"></script>
<script type="text/javascript" src="../../scripts/reportBuilder/selectTemplateWin.js"></script>

<script type="text/javascript" src="../../scripts/deskeracrm/editor/accountEditor.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/leadProfileView.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/leadEditor.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/NewLeadEditor.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/opportunityEditor.js"></script>

<script type="text/javascript" src="../../scripts/deskeracrm/editor/contactEditor.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/crmEmailTypes.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/activityEditor.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/productEditor.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/campaignURLReport.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/campaignTargets.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/EmailTemplateEditor.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/campaignEditor.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/newEmailTemplate.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/common/ClosablePanel.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/campaignDetails.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/caseEditor.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/targetModuleEditor.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/editor/targetlistTargets.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/WtfAccTree.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/crmWidget.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/emailCampaignList.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/customizeHeader.js"></script>
<script type="text/javascript" src="../../scripts/docs/WtfDocListView.js?v=10"></script>
<script type="text/javascript" src="../../scripts/docs/WtfDocTagsTree.js?v=10"></script>


<!-- Bind scripts -->
<script type="text/javascript" src="../../scripts/common/WtfBindBase.js"></script>
<script type="text/javascript" src="../../scripts/common/WtfBind.js"></script>

<!-- Calendar scripts -->
<script type="text/javascript" src="../../scripts/cal/WtfCalInit.js"></script>
<script type="text/javascript" src="../../scripts/cal/WtfHelp.js"></script>
<script type="text/javascript" src="../../scripts/cal/WtfCalSettings.js"></script>
<script type="text/javascript" src="../../scripts/cal/WtfCalDragPlugin.js"></script>
<script type="text/javascript" src="../../scripts/cal/AddingEvent/WtfEventDetails.js"></script>
<script type="text/javascript" src="../../scripts/cal/CalendarTree/WtfCalTree.js"></script>
<script type="text/javascript" src="../../scripts/cal/WtfCalManager.js"></script>

<!-- Master Configuration Files                -->

<script type="text/javascript" src="../../scripts/deskeracrm/master/AddEditMaster.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/master/masterConfiguration.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/master/commisionEditor.js"></script>

<script type="text/javascript" src="../../scripts/superUser/WtfSystemAdmin.js"></script>
<script type="text/javascript" src="../../scripts/superUser/WtfCompanyUser.js"></script>
<script type="text/javascript" src="../../scripts/superUser/CreateCompany.js"></script>
<script type="text/javascript" src="../../scripts/default/WtfDefaultManager.js?v=7"></script>
<script type="text/javascript" src="../../scripts/common/WtfGridView.js"></script>
<script type="text/javascript" src="../../scripts/common/WtfProjectFeatures.js?v=3"></script>
<script type="text/javascript" src="../../scripts/common/WtfProjectRoles.js?v=3"></script>
<script type="text/javascript" src="../../scripts/common/WtfPaging.js?v=3"></script>
<script type="text/javascript" src="../../scripts/common/WtfPagingPlugin.js?v=3"></script>
<script type="text/javascript" src="../../scripts/common/WtfCreateUser.js?v=3"></script>
<script type="text/javascript" src="../../scripts/common/WtfPermissions.js?v=3"></script>
<script type="text/javascript" src="../../scripts/common/WtfRoles.js?v=3"></script>
<script type="text/javascript" src="../../scripts/common/WtfAssignManager.js?v=3"></script>
<script type="text/javascript" src="../../scripts/common/WtfUserGrid.js?v=3"></script>
<script type="text/javascript" src="../../scripts/common/WtfAdminControl.js?v=3"></script>
<script type="text/javascript" src="../../scripts/common/KwlEditorGrid.js"></script>
<script type="text/javascript" src="../../scripts/common/KwlGridPanel.js"></script>
<script type="text/javascript" src="../../scripts/common/KwlPagingEditorGrid.js"></script>
<script type="text/javascript" src="../../scripts/common/WtfEditorPaging.js"></script>
<script type="text/javascript" src="../../scripts/common/WtfEditorPagingPlugin.js"></script>
<script type="text/javascript" src="../../scripts/common/Select.js"></script>
<script type="text/javascript" src="../../scripts/common/editorSearch.js"></script>
<script type="text/javascript" src="../../scripts/common/QuickSearch.js"></script>
<script type="text/javascript" src="../../scripts/common/WtfUpdateProfile.js"></script>
<script type="text/javascript" src="../../scripts/common/WtfQuickSearch.js"></script>
<script type="text/javascript" src="../../scripts/common/WtfNotify.js"></script>
<script type="text/javascript" src="../../scripts/alerts/WtfComAlert.js"></script>
<script type="text/javascript" src="../../scripts/alerts/ResponseAlert.js"></script>
<script type="text/javascript" src="../../scripts/msgforum/commonForumMSg.js"></script>
<script type="text/javascript" src="../../scripts/msgforum/mailTree.js"></script>
<script type="text/javascript" src="../../scripts/msgforum/personalMsg.js"></script>
<script type="text/javascript" src="../../scripts/deskeracrm/campaignleads.js"></script>
<!-- Organization scripts -->
<script src="../../scripts/OrgChart/WtfChartNode.js" type="text/javascript"></script>
<script src="../../scripts/OrgChart/WtfUnmappedContainer.js" type="text/javascript"></script>
<script src="../../scripts/OrgChart/WtfChartContainer.js" type="text/javascript"></script>
<script src="../../scripts/OrgChart/WtfChartDragPlugin.js" type="text/javascript"></script>
<!--script type="text/javascript" src="../../scripts/editorpanel.js"></script-->
<script type="text/javascript" src="../../scripts/deskeracrm/compaignEmailTemplate.js"></script>
<script type="text/javascript" src="../../scripts/common/CommonFunction.js?v=3"></script>
<script src="../../scripts/deskeracrm/quotation/TaxTypeWindow.js" type="text/javascript"></script>
<script src="../../scripts/deskeracrm/quotation/InvoiceGrid.js" type="text/javascript"></script>
<script src="../../scripts/deskeracrm/quotation/Invoice.js" type="text/javascript"></script>
<script src="../../scripts/deskeracrm/quotation/InvoiceList.js" type="text/javascript"></script>
<script src="../../scripts/deskeracrm/quotation/MailWindow.js" type="text/javascript"></script>
 <!--Form Builder Js
        <script type="text/javascript" src="../../scripts/common/newComments.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/common/radio_checkbox_grouping.js?v=3"></script>
        
        <script type="text/javascript" src="../../scripts/common/GroupCheckboxSelection.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/common/advancedsearch.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/reportBuilder/wf_queryField.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/reportBuilder/wf_openReport.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/reportBuilder/WtfReportGridField.js?v=3"></script>


        <script type="text/javascript" src="../../scripts/formbuilder/openModule.js?v=3"></script>

        <script src="../../scripts/formbuilder/include.js" type="text/javascript" ></script>
        <script src="../../scripts/formbuilder/Wtfsill.js" type="text/javascript"></script>
        <script src="../../scripts/formbuilder/cfg.js" type="text/javascript"></script>
        <script src="../../scripts/formbuilder/components.js" type="text/javascript"></script>
        <script src="../../scripts/formbuilder/formBuilder.js" type="text/javascript"></script>



        <script type="text/javascript" src="../../scripts/accessRight/WtfMainAccessPerm.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/formbuilder/WtfGridPanel.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/formbuilder/select.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/formbuilder/WtfQuickCreate.js" ></script>
        <script type="text/javascript" src="../../scripts/formbuilder/masterCombo.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/formbuilder/modulerenderer.js"></script>
        <script type="text/javascript" src="../../scripts/formbuilder/WtfButtonConfigWindow.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/dashboard/WtfCustomPanel.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/formbuilder/modulesHome.js?v=3"></script>


        End Form Builder Js-->
        <!-- CRM Custom Reports -->
        <script type="text/javascript" src="../../scripts/common/WtfGridSummary.js"></script>
        <script type="text/javascript" src="../../scripts/common/WtfCheckColumn.js"></script>
        <script type="text/javascript" src="../../scripts/customReports/configureReportGrid.js?v=3"></script>
        <script type="text/javascript" src="../../scripts/customReports/openReportGrid.js?v=3"></script>

		<script type="text/javascript">
		/*<![CDATA[*/
			PostProcessLoad = function(){
				setTimeout(function(){Wtf.get('loading').remove(); Wtf.get('loading-mask').fadeOut({remove: true});}, 250);
				Wtf.EventManager.un(window, "load", PostProcessLoad);
			}
			Wtf.EventManager.on(window, "load", PostProcessLoad);
		/*]]>*/
		</script>
<!-- /js -->
<!-- html -->
		<div id="header" style="position: relative;">
			<img id="companyLogo" src="http://apps.deskera.com/b/<%=com.krawler.common.util.URLUtil.getDomainName(request)%>/images/store/?company=true" alt="logo"/>
                        <img src="../../images/crm-right-logo.gif" alt="crm" style="float:left;margin-left:4px;margin-top:1px;" />
			<div class="userinfo">
            	<span id="whoami"></span><br /><a href="#" onclick="signOut('signout');"><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.signout"))</script></a>&nbsp;&nbsp;<a href="#" onclick="showPersonProfile();" wtf:qtip='myaccoment'><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.myaccount"))</script></a>&nbsp;&nbsp;<a href="#" onclick="showPersnProfile1();"wtf:qtip='Change your password.'><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.changepassword"))</script></a>&nbsp;&nbsp;<a href="#" onclick="loadMailPage();" wtf:qtip='Get instant access to your Inbox here.'><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.mymails"))</script></a>&nbsp;&nbsp;<a href="#" onclick="loadCalTab();" wtf:qtip='Get instant access to your Calendar here.'><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.calendar"))</script></a>&nbsp;&nbsp;<a href="#"  id="organisationlink" onclick="loadOrganizationPage();" wtf:qtip='Effortlessly create an organization chart to clearly identify user hierarchy levels in the organization.'><script>document.write(WtfGlobal.getLocaleText("crm.dashboard.myorganization"))</script></a>
            </div>
			<div id="serchForIco"></div>
            <div id="fbLikeButton"><iframe src="https://www.facebook.com/plugins/like.php?app_id=142534032495014&amp;href=www.deskera.com&amp;send=false&amp;layout=button_count&amp;width=225&amp;show_faces=false&amp;action=like&amp;colorscheme=light&amp;font&amp;height=21" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:47px; height:21px;" allowTransparency="true"></iframe></div>
			<div id="searchBar"></div>            
			<div id="navareaaccounts"></div>
			<div id="shortcuts" class="shortcuts">
			</div>
		</div>
		<div id='centerdiv'></div>
        <div id="fcue-360-mask" class="wtf-el-mask" style="display:none;z-index:1999999;opacity:0.3;">&nbsp;</div>
		<div style="display:none;">
			<iframe id="downloadframe"></iframe>
		</div>
                <input id="cursor_bin" type="text" style="display:none;"/>
<!-- /html -->
	</body>
</html>
