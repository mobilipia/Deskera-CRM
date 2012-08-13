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
package com.krawler.spring.crm.dashboard;

import java.net.URLDecoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.ocsp.Request;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.krawler.common.admin.AuditTrail;
import com.krawler.common.admin.Comment;
import com.krawler.common.admin.ProjectFeature;
import com.krawler.common.admin.SavedSearchQuery;
import com.krawler.common.admin.widgetManagement;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.utils.DateUtil;
import com.krawler.crm.activity.bizservice.ActivityManagementService;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.CustomReportList;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.savedsearch.bizservice.SavedSearchService;
import com.krawler.crm.utils.Constants;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.spring.auditTrailModule.DashboardUpdate;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.crm.savedsearch.web.SavedSearchJson;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.activityModule.crmActivityDAO;
import com.krawler.spring.crm.campaignModule.crmCampaignDAO;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.emailMarketing.crmEmailMarketingDAO;
import com.krawler.spring.crm.emailMarketing.crmEmailMarketingHandler;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.productModule.crmProductDAO;
import com.krawler.spring.crm.spreadsheet.CrmSpreadsheetService;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;

public class CrmDashboardController extends MultiActionController implements MessageSourceAware {

  private class CrmActComparator implements Comparator<Object[]> {
        @Override
        public int compare(Object[] row1, Object[] row2) {
            CrmActivityMaster actMasterobj1 = (CrmActivityMaster) row1[0];
            CrmActivityMaster actMasterobj2 = (CrmActivityMaster) row2[0];
            return actMasterobj1.getStartDate().compareTo(actMasterobj2.getStartDate());
        }
    }
    private static Log LOG = LogFactory.getLog(CrmDashboardController.class);
    private crmCommonDAO crmCommonDAOObj;
    private CrmDashboardDAO crmDashboardDAO;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private commentDAO commentDAOObj;
    private crmCampaignDAO crmCampaignDAOObj;
    private CrmDashboardHandler crmdashboardHandler;

    private crmEmailMarketingDAO crmEmailMarketingDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private permissionHandlerDAO permissionHandlerDAOObj;
    private String successView;

    private crmLeadDAO crmleadDAO;

    private crmAccountDAO accountDAO;

    private crmContactDAO contactDAO;

    private crmOpportunityDAO opportunityDAO;

    private crmCaseDAO caseDAO;

    private crmActivityDAO activityDAO;

    private crmProductDAO productDAO;

    private ActivityManagementService activityManagementService;

    private SavedSearchService SaveSearchServiceObj;

    private SavedSearchJson SaveSearchJsonObj;
    private CometManagementService cometManagementService;
    private CrmSpreadsheetService crmspreadsheetService;
	private MessageSource messageSource;

	public void setCrmspreadsheetService(CrmSpreadsheetService spreadsheetService) {
		this.crmspreadsheetService = spreadsheetService;
	}

	public void setCometManagementService(
			CometManagementService cometManagementService) {
		this.cometManagementService = cometManagementService;
	}
    
    public crmCommonDAO getCrmCommonDAO() {
        return crmCommonDAOObj;
    }

    public void setCrmCommonDAO(crmCommonDAO crmCommonDAOObj) {
        this.crmCommonDAOObj = crmCommonDAOObj;
    }
    
    public void setSaveSearchService(SavedSearchService SaveSearchServiceObj) {
        this.SaveSearchServiceObj = SaveSearchServiceObj;
    }

    public void setSaveSearchJson(SavedSearchJson SaveSearchJsonObj) {
        this.SaveSearchJsonObj = SaveSearchJsonObj;
    }

    public void setActivityManagementService(ActivityManagementService activityManagementService)
    {
        this.activityManagementService = activityManagementService;
    }

    public String getSuccessView() {
		return successView;
	}

    public void setSuccessView(String successView) {
		this.successView = successView;
    }

    public void setpermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setauditTrailDAO(auditTrailDAO auditTrailDAOObj) {
        this.auditTrailDAOObj = auditTrailDAOObj;
    }

    public void setcommentDAO(commentDAO commentDAOObj) {
        this.commentDAOObj = commentDAOObj;
    }

    public void setcrmCampaignDAO(crmCampaignDAO crmCampaignDAOObj) {
        this.crmCampaignDAOObj = crmCampaignDAOObj;
    }

    public void setcrmEmailMarketingDAO(crmEmailMarketingDAO crmEmailMarketingDAOObj1) {
        this.crmEmailMarketingDAOObj = crmEmailMarketingDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

   	public void setCrmdashboardHandler(CrmDashboardHandler crmdashboardHandler) {
		this.crmdashboardHandler = crmdashboardHandler;
	}

	public ModelAndView getWidgetStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject resultJson = new JSONObject();
        try {
            String userId = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = this.crmManagerDAOObj.recursiveUserIds(userId);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userId);
            kmsg = getCrmDashboardDAO().getWidgetStatus(requestParams);
            int start = 0;
            int limit = 5;
            try
            {
                start = Integer.parseInt(request.getParameter("start"));
                limit = Integer.parseInt(request.getParameter("limit"));
            }
            catch (NumberFormatException e){}
            List<widgetManagement> widgets = kmsg.getEntityList();
            if (widgets != null && !widgets.isEmpty())
            {
                widgetManagement wmObj = widgets.get(0);
                JSONObject empty = new JSONObject(wmObj.getWidgetstate());
                JSONArray jarr = new JSONArray("[" + empty + "]");
                List<String> widgetNames = new ArrayList<String>();

                if (jarr.length() > 0)
                {
                    JSONObject jobj1 = jarr.getJSONObject(0);
                    String colA= jobj1.getString("col1");
                    String colB= jobj1.getString("col2");
                    String colC= jobj1.getString("col3");
                    JSONArray jarrColA = new JSONArray(colA);
                    JSONArray jarrColB = new JSONArray(colB);
                    JSONArray jarrColC = new JSONArray(colC);

                    for(int a=0;a <jarrColA.length();a++){
                        JSONObject jobjA = jarrColA.getJSONObject(a);
                        widgetNames.add(jobjA.getString("id"));
                    }

                    for(int b=0 ; b <jarrColB.length();b++){
                        JSONObject jobjB = jarrColB.getJSONObject(b);
                        widgetNames.add(jobjB.getString("id"));
                    }

                    for(int c=0;c<jarrColC.length();c++){
                        JSONObject jobjC = jarrColC.getJSONObject(c);
                        widgetNames.add(jobjC.getString("id"));
                    }
                }

                    String companyId = sessionHandlerImpl.getCompanyid(request);
                    JSONObject jobj = new JSONObject();
                    getColumnWiseWidget(request, widgetNames, usersList, jobj , start, limit, companyId);

                    resultJson.put("colLength",empty.toString());
                    resultJson.put("widgetData",jobj.toString());
                
            }
        } catch(JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch(ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch(SessionExpiredException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView", "model", resultJson.toString());
    }

    public ModelAndView getWidgetFrame(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject resultJson = new JSONObject();
        try {
            String userId = sessionHandlerImpl.getUserid(request);
           
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userId);
            kmsg = getCrmDashboardDAO().getWidgetStatus(requestParams);
            
            List<widgetManagement> widgets = kmsg.getEntityList();
            if (widgets != null && !widgets.isEmpty())
            {
                widgetManagement wmObj = widgets.get(0);
                JSONObject empty = new JSONObject(wmObj.getWidgetstate());
                JSONArray jarr = new JSONArray("[" + empty + "]");
                List<String> widgetNames = new ArrayList<String>();
                StringBuffer widgetdata = new StringBuffer();
                
                if (jarr.length() > 0)
                {
                    JSONObject jobj1 = jarr.getJSONObject(0);
                    String colA= jobj1.getString("col1");
                    String colB= jobj1.getString("col2");
                    String colC= jobj1.getString("col3");
                    JSONArray jarrColA = new JSONArray(colA);
                    JSONArray jarrColB = new JSONArray(colB);
                    JSONArray jarrColC = new JSONArray(colC);

                    for(int a=0;a <jarrColA.length();a++){
                        JSONObject jobjA = jarrColA.getJSONObject(a);

                        boolean nonCrmModuleFlag = NonCrmModuleFlag(jobjA); // Check Whether current portlet is Crm module or other portlet like Links,Report etc

                        if(nonCrmModuleFlag){
                            widgetNames.add(jobjA.getString("id"));
                        } else {
                            widgetdata.append(jobjA.getString("id"));
                            widgetdata.append(",");
                        }
                        
                    }

                    for(int b=0 ; b <jarrColB.length();b++){
                        JSONObject jobjB = jarrColB.getJSONObject(b);

                        boolean nonCrmModuleFlag = NonCrmModuleFlag(jobjB); // Check Whether current portlet is Crm module or other portlet like Links,Report etc

                        if(nonCrmModuleFlag){
                            widgetNames.add(jobjB.getString("id"));
                        } else{
                            widgetdata.append(jobjB.getString("id"));
                            widgetdata.append(",");
                        }
                    }

                    for(int c=0;c<jarrColC.length();c++){
                        JSONObject jobjC = jarrColC.getJSONObject(c);

                        boolean nonCrmModuleFlag = NonCrmModuleFlag(jobjC); // Check Whether current portlet is Crm module or other portlet like Links,Report etc

                        if(nonCrmModuleFlag){
                            widgetNames.add(jobjC.getString("id"));
                        } else {
                            widgetdata.append(jobjC.getString("id"));
                            widgetdata.append(",");
                        }
                    }
                }
                // get report widgets for logged in user
                ArrayList<String> filter_names = new ArrayList<String>();
                ArrayList<Object> filter_values = new ArrayList<Object>();
                filter_names.add("dc.dashboard");
                filter_values.add(1);
                filter_names.add("dc.userid");
                filter_values.add(userId);

                KwlReturnObject kr = crmCommonDAOObj.getDashboardReportConfig(filter_names, filter_values);
                List<Object[]> configList = kr.getEntityList();
                JSONArray reportArr = new JSONArray();
                for (Object[] row : configList) {
                    String rid = row[2].toString();
                    String rname = row[3].toString();
                    JSONObject reportWidgetConfig = new JSONObject();
                    reportWidgetConfig.put("reportcode", rid);
                    reportWidgetConfig.put("reportname", rname);
                    reportArr.put(reportWidgetConfig);
                }
                resultJson.put("reportwidget",reportArr);
                String widgetStr = widgetdata.toString();
                if(!StringUtil.isNullOrEmpty(widgetStr)){
                    widgetStr = widgetStr.substring(0,(widgetStr.length()-1));
                }
                resultJson.put("colLength",empty.toString());
                resultJson.put("widgetFrame",widgetStr);

                String widgetDataStr = getWidgetDataString(request, widgetNames);
                
                resultJson.put("widgetData",widgetDataStr);

            }
        } catch(JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch(SessionExpiredException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView", "model", resultJson.toString());
    }
    
    public ModelAndView getWidgetData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject resultJson = new JSONObject();
        try {
            String lst = request.getParameter("widgetFrame");
            List<String> widgetNames = Arrays.asList(lst.split(","));

            String widgetDataStr = getWidgetDataString(request, widgetNames);
            resultJson.put("widgetData",widgetDataStr);


        } catch(JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView", "model", resultJson.toString());
    }
    
     public String getWidgetDataString (HttpServletRequest request,List<String> widgetNames) {
        JSONObject jobj = new JSONObject();
        try{
            String userId = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = this.crmManagerDAOObj.recursiveUserIds(userId);
            int start = 0;
            int limit = 5;
            try{
                start = Integer.parseInt(request.getParameter("start"));
                limit = Integer.parseInt(request.getParameter("limit"));
            }
            catch (NumberFormatException e){
            }

            String companyId = sessionHandlerImpl.getCompanyid(request);

            getColumnWiseWidget(request, widgetNames, usersList, jobj , start, limit, companyId);

        } catch(JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch(ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch(SessionExpiredException ex) {
            logger.warn(ex.getMessage(), ex);
        }

        return jobj.toString();
    }
    public boolean NonCrmModuleFlag (JSONObject jobj){
        boolean flag = false;
        try {
            
                if (StringUtil.equal(jobj.getString("id"), "crmmodule_drag")) {
                    flag = true;
                } else if (StringUtil.equal(jobj.getString("id"), "DSBMyWorkspaces")) {
                    flag = true;
                } else if (StringUtil.equal(jobj.getString("id"), "reports_drag")) {
                    flag = true;
                } else if (StringUtil.equal(jobj.getString("id"), "marketing_drag")) {
                    flag = true;
                } else if (StringUtil.equal(jobj.getString("id"), "crm_admin_widget")) {
                    flag = true;
                } else if (StringUtil.equal(jobj.getString("id"), "DSBAdvanceSearch")) {
                    flag = true;
                } else if (StringUtil.equal(jobj.getString("id"), "campaign_reports_drag")) {
                    flag = true;
                }
            
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        }
      return flag;
    }
    public ModelAndView getReportWidgets(HttpServletRequest request, HttpServletResponse response) {
        String resultStr = null;
        try {
            resultStr = getReportWidgetLinks(request);
        } catch(ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView", "model", resultStr);
    }
    public ModelAndView getCustomReportWidgets(HttpServletRequest request, HttpServletResponse response) {
        String resultStr = null;
        try{
            List ll = getCustomReporList(request);
//            if(ll!=null && ll.size()>0){
                resultStr = getCustomReportWidgetLinks(request,ll);
//            }
        } catch(ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView", "model", resultStr);
    }

    public List getCustomReporList(HttpServletRequest request) {
        List ll = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            KwlReturnObject kmsg = crmCommonDAOObj.getCustomReport(requestParams);
            ll=kmsg.getEntityList();
        } catch (SessionExpiredException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return ll;
    }
    
    public ModelAndView getCampaignReportWidget(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultStr = new JSONObject();
        JSONObject finalRes = new JSONObject();
        try {
            getCrmModuleWidget(request , resultStr);
            finalRes = resultStr.getJSONObject("CrmModuleDrag");
        } catch(JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch(SessionExpiredException ex) {
            logger.warn(ex.getMessage(),ex);
        } catch(ServiceException ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", finalRes.toString());
    }

    public ModelAndView getCrmModuleWidget(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultStr = new JSONObject();
        JSONObject finalRes = new JSONObject();
        try {
            getCrmModuleWidget(request , resultStr);
            finalRes = resultStr.getJSONObject("CrmModuleDrag");
        } catch(JSONException ex) {
            logger.warn(ex.getMessage(),ex);
        } catch(SessionExpiredException ex) {
            logger.warn(ex.getMessage(),ex);
        } catch(ServiceException ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", finalRes.toString());
    }

    public ModelAndView getAllUpdatesForWidget(HttpServletRequest request, HttpServletResponse response) {
        String resultStr = null;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = this.crmManagerDAOObj.recursiveUserIds(userid);
            resultStr = getUpdatesForWidgets(request, usersList, null);
        } catch(ServiceException ex) {
            logger.warn(ex.getMessage(),ex);
        } catch(SessionExpiredException ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", resultStr);
    }

    public ModelAndView getAdminWidget(HttpServletRequest request, HttpServletResponse response) {
        String resultStr = null;
        try {
            resultStr = getAdminLinks(request);
        } catch(SessionExpiredException ex) {
            logger.warn(ex.getMessage(),ex);
        } catch(JSONException ex) {
            logger.warn(ex.getMessage(),ex);
        } catch(ServiceException ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", resultStr);
    }

    public ModelAndView getCampaignLinks(HttpServletRequest request, HttpServletResponse response) {
        String resultStr = null;
        try {
            resultStr = getCampaignLinks(request);
        } catch(SessionExpiredException ex) {
            logger.warn(ex.getMessage(),ex);
        } catch(JSONException ex) {
            logger.warn(ex.getMessage(),ex);
        } catch(ServiceException ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", resultStr);
    }

    private String getAdminLinks(HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String resultStr = null;
        JSONObject tempObjMarketing = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONObject jobj = new JSONObject();
        JSONArray jArrMarketing = new JSONArray();
        String companyid = sessionHandlerImpl.getCompanyid(request);
        if(sessionHandlerImplObj.getRole(request).equals(Constants.COMPANY_ADMIN)) {
            tempObjMarketing = new JSONObject();
            tempObjMarketing.put("name", messageSource.getMessage("crm.dashboard.masterconfig", null, RequestContextUtils.getLocale(request)));
            tempObjMarketing.put("onclick", "showConfigMaster()");
            tempObjMarketing.put("img","../../images/master-configuration.png");
            tempObjMarketing.put("qtip",messageSource.getMessage("crm.dashboard.masterconfig.ttip", null, RequestContextUtils.getLocale(request)));//"Configure master settings.");
            jArrMarketing.put(tempObjMarketing);

            tempObjMarketing = new JSONObject();
            tempObjMarketing.put("name", messageSource.getMessage("crm.dashboard.usermanagement", null, RequestContextUtils.getLocale(request)));//"User Management");
            tempObjMarketing.put("onclick", "loadAdminPage()");
            tempObjMarketing.put("img","../../images/user-management-icon.png");
            tempObjMarketing.put("qtip",messageSource.getMessage("crm.dashboard.usermanagement.ttip", null, RequestContextUtils.getLocale(request)));//"Easily manage all users in the system. Assign permission to individual users in accordance with their work functions.");
            jArrMarketing.put(tempObjMarketing);

            tempObjMarketing = new JSONObject();
            tempObjMarketing.put("name", messageSource.getMessage("crm.dashboard.emailtypes", null, RequestContextUtils.getLocale(request)));//"Email Types");
            tempObjMarketing.put("onclick", "crmEmailTypes()");
            tempObjMarketing.put("img","../../images/email-type-icon.png");
            tempObjMarketing.put("qtip",messageSource.getMessage("crm.dashboard.emailtypes.ttip", null, RequestContextUtils.getLocale(request)));//"Edit and Customize the System Generated E-mail formats and personalize them as per your organizational requirements.");
            jArrMarketing.put(tempObjMarketing);

            tempObjMarketing = new JSONObject();
            tempObjMarketing.put("name", messageSource.getMessage("crm.dashboard.systembackup", null, RequestContextUtils.getLocale(request)));//"System Backup");
            tempObjMarketing.put("onclick", "takeBackup()");
            tempObjMarketing.put("img","../../images/backup-icon.png");
            tempObjMarketing.put("qtip",messageSource.getMessage("crm.dashboard.systembackup.ttip", null, RequestContextUtils.getLocale(request)));//"Take backup of the whole system.");
            jArrMarketing.put(tempObjMarketing);

            if(StringUtil.equal(companyid, Constants.KRAWLERBUSINESSSOFTWARE_COMPANYID)){
                tempObjMarketing = new JSONObject();
                tempObjMarketing.put("name", messageSource.getMessage("crm.dashboard.modulechart", null, RequestContextUtils.getLocale(request)));//"Modules Chart");
                tempObjMarketing.put("onclick", "getChart()");
                tempObjMarketing.put("img","../../images/chart-icon.png");
                tempObjMarketing.put("qtip",messageSource.getMessage("crm.dashboard.modulechart.ttip", null, RequestContextUtils.getLocale(request)));//"Get the graphical view of Modules.");
                jArrMarketing.put(tempObjMarketing);
            }
        }
        jobj1.put("data", jArrMarketing);
        jobj.put("crm_admin_widget", jobj1);
        resultStr = jobj1.toString();
        return resultStr;
    }

    public ModelAndView campEmailMarketingStatus(HttpServletRequest request, HttpServletResponse response) {
        String resultStr = null;
        JSONObject jobj = new JSONObject();
        try {
            String userId = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = this.crmManagerDAOObj.recursiveUsers(userId);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userslist", usersList);
            requestParams.put("companyid", companyid);
            requestParams.put("activeCampaign", true);
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.campEmailMarketingStatus(requestParams);
            jobj = crmEmailMarketingHandler.getcampEmailMarketingStatusJson(kmsg.getEntityList(), kmsg.getRecordTotalCount());
            resultStr = jobj.toString();
        } catch(SessionExpiredException ex) {
            logger.warn(ex.getMessage(),ex);
        } catch(ServiceException ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", resultStr);
    }

    private String getCampaignLinks(HttpServletRequest request) throws ServiceException, JSONException, SessionExpiredException {
        String resultStr = null;
        JSONObject tempObjMarketing = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONObject jobj = new JSONObject();
        JSONArray jArrMarketing = new JSONArray();
        if ((sessionHandlerImpl.getPerms(request, ProjectFeature.campaignFName) & 1) == 1) {
            tempObjMarketing.put("name", messageSource.getMessage("crm.dashboard.campaigns", null, RequestContextUtils.getLocale(request)));//"Campaigns");
            tempObjMarketing.put("onclick", "addCampaignTab()");
            tempObjMarketing.put("img","../../images/Campaigns-icon.png");
            tempObjMarketing.put("qtip",messageSource.getMessage("crm.dashboard.campaigns.ttip", null, RequestContextUtils.getLocale(request)));//"Maintain comprehensive details of marketing initiatives such as an advertisement, direct mail, or conference that you conduct in order to generate prospects and build brand awareness.");
            jArrMarketing.put(tempObjMarketing);

            tempObjMarketing = new JSONObject();
            tempObjMarketing.put("name", messageSource.getMessage("crm.dashboard.emailcampaign", null, RequestContextUtils.getLocale(request)));//"Email Campaigns");
            tempObjMarketing.put("onclick", "showEmailCampaigns()");
            tempObjMarketing.put("img","../../images/Email-Template.png");
            tempObjMarketing.put("qtip",messageSource.getMessage("crm.dashboard.emailcampaign", null, RequestContextUtils.getLocale(request)));//"Email Campaigns.");
            jArrMarketing.put(tempObjMarketing);
        }

        if ((sessionHandlerImpl.getPerms(request, ProjectFeature.targetFName) & 1) == 1) {
            tempObjMarketing = new JSONObject();
            tempObjMarketing.put("name", messageSource.getMessage("crm.dashboard.viewlist", null, RequestContextUtils.getLocale(request)));//"View Lists");
            tempObjMarketing.put("onclick", "addTargetListTab()");
            tempObjMarketing.put("img","../../images/Target-List.png");
            tempObjMarketing.put("qtip",messageSource.getMessage("crm.dashboard.viewlist.ttip", null, RequestContextUtils.getLocale(request)));//"Compile and Save lists of targets for Email Campaigns. Use existing Leads, Contacts etc. or upload from convenient file formats.");
            jArrMarketing.put(tempObjMarketing);
        }

        if ((sessionHandlerImpl.getPerms(request, ProjectFeature.campaignFName) & 2) == 2) {
            tempObjMarketing = new JSONObject();
            tempObjMarketing.put("name", messageSource.getMessage("crm.campaign.create",null, RequestContextUtils.getLocale(request)));//"Create Campaign"
            tempObjMarketing.put("onclick", "addNewCampaignTab()");
            tempObjMarketing.put("img","../../images/cam-add.png");
            tempObjMarketing.put("qtip",messageSource.getMessage("crm.campaign.create.ttip", null, RequestContextUtils.getLocale(request)));//"Create a new Marketing Campaign.");
            jArrMarketing.put(tempObjMarketing);

        }

        if ((sessionHandlerImpl.getPerms(request, ProjectFeature.targetFName) & 2) == 2) {

            tempObjMarketing = new JSONObject();
            tempObjMarketing.put("name", messageSource.getMessage("crm.dashboard.createlist", null, RequestContextUtils.getLocale(request)));//"Create List");
            tempObjMarketing.put("onclick", "addNewTargetListTab()");
            tempObjMarketing.put("img","../../images/Target-List-icon.png");
            tempObjMarketing.put("qtip",messageSource.getMessage("crm.dashboard.createlist.ttip", null, RequestContextUtils.getLocale(request)));//"Create a new list of Targets for Email Marketing.");
            jArrMarketing.put(tempObjMarketing);
        }
        
        jobj1.put("data", jArrMarketing);
        jobj.put("CrmModuleDrag", jobj1);
        resultStr = jobj1.toString();
        return resultStr;
    }
/*    private JSONObject getUpdatesForDashboardWidgets(StringBuffer usersList, HttpServletRequest request, List<DashboardItemInfo> items, int start, int limit, String companyId) throws ServiceException, SessionExpiredException
    {
        JSONObject jobj = new JSONObject();

        if (items != null)
        {
            List<String> widgetList = new ArrayList<String>();
            for (DashboardItemInfo item : items)
            {
                try
                {
                    JSONArray jarr = new JSONArray("[" + item.dashboardItem + "]");
                    for (int i = 0; i < item.colACount; i++)
                    {
                        JSONObject jobj1 = jarr.getJSONObject(0);
                        String widget = jobj1.getString("colA" + i);
                        widgetList.add(widget);
                        //getColumnWiseWidget(request, widget, usersList, jobj, start, limit, companyId);
                        //item.dashboardUpdate = jobj.toString();
                    }
                    for (int i = 0; i < item.colBCount; i++)
                    {
                        JSONObject jobj1 = jarr.getJSONObject(0);
                        String widget = jobj1.getString("colB" + i);
                        widgetList.add(widget);
                        //getColumnWiseWidget(request, widget, usersList, jobj, start, limit, companyId);
                        //item.dashboardUpdate = jobj.toString();
                    }
                    for (int i = 0; i < item.colCCount; i++)
                    {
                        JSONObject jobj1 = jarr.getJSONObject(0);
                        String widget = jobj1.getString("colC" + i);
                        widgetList.add(widget);
                        //getColumnWiseWidget(request, widget, usersList, jobj, start, limit, companyId);
                        //item.dashboardUpdate = jobj.toString();
                    }
                }catch (JSONException e)
                {
                    throw ServiceException.FAILURE("crmDashboardController.getUpdatesForDashboardWidgets:" + e.getMessage(), e);
                }
            }
            try
            {
                getColumnWiseWidget(request, widgetList, usersList, jobj, start, limit, companyId);
            } catch (JSONException e)
            {
                throw ServiceException.FAILURE("crmDashboardController.getUpdatesForDashboardWidgets:" + e.getMessage(), e);
            }
        }
        return jobj;
    }*/

    private void getColumnWiseWidget(HttpServletRequest request, List<String> widgets, StringBuffer usersList, JSONObject jobj, int start, int limit, String companyId) throws ServiceException, JSONException, SessionExpiredException
    {
        if (widgets != null)
        {
            List<Integer> types = new ArrayList<Integer>();
            List<String> typeDef = new ArrayList<String>();

            // crm dashboard update widgets
            for (String widget : widgets)
            {
                if (StringUtil.equal(widget, "case_drag"))
                {
                    types.add(5);
                    typeDef.add("Case");
                } else if (StringUtil.equal(widget, "contact_drag"))
                {
                    types.add(3);
                    typeDef.add("Contact");
                } else if (StringUtil.equal(widget, "account_drag"))
                {
                    types.add(2);
                    typeDef.add("Account");
                } else if (StringUtil.equal(widget, "activity_drag"))
                {
                    types.add(6);
                    typeDef.add("Activity");
                } else if (StringUtil.equal(widget, "campaign_drag"))
                {
                    types.add(0);
                    typeDef.add("Campaign");
                } else if (StringUtil.equal(widget, "lead_drag"))
                {
                    types.add(1);
                    typeDef.add("Lead");
                } else if (StringUtil.equal(widget, "opportunity_drag"))
                {
                    types.add(4);
                    typeDef.add("Opportunity");
                } else if (StringUtil.equal(widget, "product_drag"))
                {
                    types.add(7);
                    typeDef.add("Product");
                }
            }

            List<Map<String, Object>> widgetDataList = getDetailForWidget(request, usersList, types, start, limit, companyId);

            for (String widget : widgets)
            {
                JSONObject jobjColumn = null;
                if (StringUtil.equal(widget, "crmmodule_drag"))
                {
                    jobjColumn = getCrmModuleWidget(request, jobj);
                } else if (StringUtil.equal(widget, "DSBMyWorkspaces"))
                {
                    String updates = getUpdatesForWidgets(request, usersList, companyId);
                    JSONObject jobj2 = new JSONObject(updates);
                    jobjColumn = jobj.append("ModuleUpdates", jobj2);
                } else if (StringUtil.equal(widget, "reports_drag"))
                {
                    String reportsJdata = getReportWidgetLinks(request);
                    JSONObject jobj2 = new JSONObject(reportsJdata);
                    jobjColumn = jobj.append("ReportUpdates", jobj2);
                }else if (StringUtil.equal(widget, "crm_custom_reports"))
                {    String reportsJdata="";
                     List ll = getCustomReporList(request);
//                     if(ll!=null && ll.size()>0){
                         reportsJdata = getCustomReportWidgetLinks(request,ll);
//                     }
                    JSONObject jobj2 = new JSONObject(reportsJdata);
                    jobjColumn = jobj.append("CustomReportUpdates", jobj2);
                } else if (StringUtil.equal(widget, "marketing_drag"))
                {
                    JSONObject jobj2 = new JSONObject(getCampaignLinks(request));
                    jobjColumn = jobj.put("marketing_drag", jobj2);
                } else if(StringUtil.equal(widget, "crm_admin_widget")){
                    JSONObject jobj2  = new JSONObject(getAdminLinks(request));
                    jobjColumn = jobj.put("crm_admin_widget", jobj2);
                }
                else if(StringUtil.equal(widget, "DSBAdvanceSearch")) {
                    String userId = sessionHandlerImpl.getUserid(request);
                    List<SavedSearchQuery> ll = SaveSearchServiceObj.getSavedSearchQueries(userId, 0, 5);
                    JSONObject jobj2 = SaveSearchJsonObj.getSavedSearchQueries(ll, SaveSearchServiceObj.getSavedSearchQueries(userId));
                    jobjColumn = jobj.put("DSBAdvanceSearch", jobj2);
                }
                else if (StringUtil.equal(widget, "campaign_reports_drag"))
                {
                    JSONObject jobj1 = new JSONObject();
                    jobj1.put("success", true);
                    jobj1.put("data", new JSONArray());
                    jobj1.put("campaignReport", true);
                    jobj1.put("totalCount", 0);

                    jobjColumn = jobj.append("CampaignReportUpdates", jobj1);
                } else
                {
                    if (StringUtil.equal(widget, "case_drag"))
                    {
                        setDashboardUpdateData(5, widgetDataList, types, typeDef, jobj);
                    } else if (StringUtil.equal(widget, "contact_drag"))
                    {
                        setDashboardUpdateData(3, widgetDataList, types, typeDef, jobj);
                    } else if (StringUtil.equal(widget, "account_drag"))
                    {
                        setDashboardUpdateData(2, widgetDataList, types, typeDef, jobj);
                    } else if (StringUtil.equal(widget, "activity_drag"))
                    {
                        setDashboardUpdateData(6, widgetDataList, types, typeDef, jobj);
                    } else if (StringUtil.equal(widget, "campaign_drag"))
                    {
                        setDashboardUpdateData(0, widgetDataList, types, typeDef, jobj);
                    } else if (StringUtil.equal(widget, "lead_drag"))
                    {
                        setDashboardUpdateData(1, widgetDataList, types, typeDef, jobj);
                    } else if (StringUtil.equal(widget, "opportunity_drag"))
                    {
                        setDashboardUpdateData(4, widgetDataList, types, typeDef, jobj);
                    } else if (StringUtil.equal(widget, "product_drag"))
                    {
                        setDashboardUpdateData(7, widgetDataList, types, typeDef, jobj);
                    }
//                    else if (StringUtil.equal(widget, "top_activity_drag"))
//                    {
//                        setDashboardUpdateData(8, widgetDataList, types, typeDef, jobj);
//                    }
                }
            }

        }
    }

    private void setDashboardUpdateData(int typeVal, List<Map<String, Object>> widgetDataList, List<Integer> types, List<String> typeDef, JSONObject jobj) throws JSONException
    {
        int i = 0;
        for (int type: types)
        {
            if (typeVal == type)
            {
                String def = typeDef.get(i);
                // find the widget data in widgetDta
                for (Map<String, Object> widgetData: widgetDataList)
                {
                    if (widgetData.get("type").equals(type))
                    {
                        WidgetUpdateData widgetUpdateData = (WidgetUpdateData) widgetData.get("widgetData");
                        JSONObject wObj = new JSONObject();
                        if (widgetUpdateData == null)
                        {
                            continue;
                        }
                        wObj.put("count", widgetUpdateData.count);

                        JSONArray dataJArray = new JSONArray();

                        if (widgetUpdateData.data != null)
                        {
                            for (String str: widgetUpdateData.data)
                            {
                                JSONObject obj = new JSONObject();
                                obj.put("update", str);
                                dataJArray.put(obj);
                            }
                        }
                        wObj.put("data", dataJArray);
                        jobj.append(def, wObj);
                    }
                }
                break;
            }
            i++;
        }
    }

    private void publishSpreadSheetConfig(HashMap<String, Integer> reportsPermMap, String companyid, String userid, String module,Locale locale) {
        new Thread(){
            private String userid;
            private String module;
            private String companyid;
            private HashMap<String, Integer> reportsPermMap;
            private Locale lcl;
            
            public Thread setValues(HashMap<String, Integer> reportsPermMap, String companyid,String userid, String module,Locale locale){
                this.userid = userid;
                this.module = module;
                this.reportsPermMap = reportsPermMap;
                this.companyid = companyid;
                this.lcl=locale;
                return this;
            }
            @Override
            public void run() {
                try {
                    JSONObject jobj = new JSONObject("{cid:0,rules:{rules:[]},state:{columns:false}}");
                    jobj = crmspreadsheetService.getSpreadsheetConfig(reportsPermMap, module, userid, companyid,lcl);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("data",jobj);
                    map.put("module",module);
                    cometManagementService.publishInformation(map,new String[]{"crm","spreadsheetconfig",userid});
                } catch (Exception e){
                    LOG.warn("Can't load spreadsheet config", e);
                }
            }
        }.setValues(reportsPermMap, companyid, userid, module, locale).start();
    }
    
    private JSONObject getCrmModuleWidget(HttpServletRequest request ,JSONObject jobj ) throws ServiceException, JSONException, SessionExpiredException {
            JSONObject tempobj = new JSONObject();
            JSONObject jobj1 = new JSONObject();
            JSONArray jArr = new JSONArray();
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.leadFName) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.leads", null, RequestContextUtils.getLocale(request)));//"Leads");
                tempobj.put("onclick", "addLeadTab()");
                tempobj.put("img","../../images/leads-icon.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.leads.ttip", null, RequestContextUtils.getLocale(request)));//"Capture all relevant information on potential sales opportunities or prospects i.e. individuals who have expressed some interest in your product or company.");
                jArr.put(tempobj);
                HashMap<String, Integer> reportsPermMap = crmspreadsheetService.getReportsPermission(request, ProjectFeature.leadFName);
                publishSpreadSheetConfig(reportsPermMap, companyid, userid,ProjectFeature.leadFName,RequestContextUtils.getLocale(request));
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.contactFName) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.contacts", null, RequestContextUtils.getLocale(request)));//"Contacts");
                tempobj.put("onclick", "addContactTab()");
                tempobj.put("img","../../images/contact-icon.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.contacts.ttip", null, RequestContextUtils.getLocale(request)));//"Maintain complete information about the individuals you know in an account and interact with.");
                jArr.put(tempobj);
                HashMap<String, Integer> reportsPermMap = crmspreadsheetService.getReportsPermission(request, ProjectFeature.contactFName);
                publishSpreadSheetConfig(reportsPermMap, companyid, userid,ProjectFeature.contactFName,RequestContextUtils.getLocale(request));
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.productFName) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.products", null, RequestContextUtils.getLocale(request)));//"Products");
                tempobj.put("onclick", "addProductMasterTab()");
                tempobj.put("img","../../images/products-icon.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.products.ttip", null, RequestContextUtils.getLocale(request)));//"Maintain comprehensive details of items or services that you sell to your customers. You can also record associated vendor details here.");
                jArr.put(tempobj);
                HashMap<String, Integer> reportsPermMap = crmspreadsheetService.getReportsPermission(request, ProjectFeature.productFName);
                publishSpreadSheetConfig(reportsPermMap, companyid, userid,ProjectFeature.productFName,RequestContextUtils.getLocale(request));
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.accountFName) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.accounts", null, RequestContextUtils.getLocale(request)));//"Accounts");
                tempobj.put("onclick", "addAccountTab()");
                tempobj.put("img","../../images/Accounts.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.accounts.ttip", null, RequestContextUtils.getLocale(request)));//"Maintain comprehensive details of the organization or company you want to track such as customers, partners, or competitors. Easily track your existing customers as well as prospective clients.");
                jArr.put(tempobj);
                HashMap<String, Integer> reportsPermMap = crmspreadsheetService.getReportsPermission(request, ProjectFeature.accountFName);
                publishSpreadSheetConfig(reportsPermMap, companyid, userid,ProjectFeature.accountFName,RequestContextUtils.getLocale(request));
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.opportunityFName) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.opportunities", null, RequestContextUtils.getLocale(request)));//"Opportunities");
                tempobj.put("onclick", "addOpportunityTab()");
                tempobj.put("img","../../images/Opportunities-icon.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.opportunities.ttip", null, RequestContextUtils.getLocale(request)));//"Maintain complete information related to specific sales and pending deals that need to be cracked. Furthermore, you can record all related contacts and activities information for each opportunity.");
                jArr.put(tempobj);
                HashMap<String, Integer> reportsPermMap = crmspreadsheetService.getReportsPermission(request, ProjectFeature.opportunityFName);
                publishSpreadSheetConfig(reportsPermMap, companyid, userid,ProjectFeature.opportunityFName,RequestContextUtils.getLocale(request));
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.caseFName) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.cases", null, RequestContextUtils.getLocale(request)));//"Cases");
                tempobj.put("onclick", "addCaseTab()");
                tempobj.put("img","../../images/cases-icon.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.cases.ttip", null, RequestContextUtils.getLocale(request)));//"Capture detailed description of a customers feedback, problems or questions. Effectively manage cases through regular  tracking of customer queries.");
                jArr.put(tempobj);
                HashMap<String, Integer> reportsPermMap = crmspreadsheetService.getReportsPermission(request, ProjectFeature.caseFName);
                publishSpreadSheetConfig(reportsPermMap, companyid, userid,ProjectFeature.caseFName,RequestContextUtils.getLocale(request));
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.goalFName) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.goalsettings", null, RequestContextUtils.getLocale(request)));//"Goal Settings");
                tempobj.put("onclick", "goalSettings()");
                tempobj.put("img","../../images/goal-setting.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.goalsettings.ttip", null, RequestContextUtils.getLocale(request)));//"Set individual Goals and Targets for sales reps and track their status.");
                jArr.put(tempobj);
            }
             tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.mygoals", null, RequestContextUtils.getLocale(request)));//"My Goals");
                tempobj.put("onclick", "myGoals()");
                tempobj.put("img","../../images/my-goals.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.mygoals.ttip", null, RequestContextUtils.getLocale(request)));//"View your own Goals and Targets and track their status.");
                jArr.put(tempobj);

            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.zohoImport) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name",messageSource.getMessage("crm.dashboard.import", null, RequestContextUtils.getLocale(request)));// "Import");
                tempobj.put("onclick", "importzoho()");
                tempobj.put("img","../../images/zoho-crm-icon.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.import.ttip", null, RequestContextUtils.getLocale(request)));// "Import from ZOHO CRM.");
                jArr.put(tempobj);
            }
            if (((sessionHandlerImpl.getPerms(request, ProjectFeature.leadFName) & 64) == 64) || ((sessionHandlerImpl.getPerms(request, ProjectFeature.accountFName) & 32) == 32) || ((sessionHandlerImpl.getPerms(request, ProjectFeature.contactFName) & 32) == 32) || ((sessionHandlerImpl.getPerms(request, ProjectFeature.targetFName) & 32) == 32)) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.importlog", null, RequestContextUtils.getLocale(request)));//"Import log");
                tempobj.put("onclick", "callImportFilesLog()");
                tempobj.put("img","../../images/import-log.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.importlog.ttip", null, RequestContextUtils.getLocale(request)));//"Imported file\'s log.");
                jArr.put(tempobj);
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.document) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.mydocuments", null, RequestContextUtils.getLocale(request)));//"My Documents");
                tempobj.put("onclick", "loadDocumentPage()");
                tempobj.put("img","../../images/my-documents.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.mydocuments.ttip", null, RequestContextUtils.getLocale(request)));//"Store, retrieve, share and efficiently manage all your documents.");
                jArr.put(tempobj);
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.auditTrail) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.audittrail", null, RequestContextUtils.getLocale(request)));//"Audit Trail");
                tempobj.put("onclick", "callAuditTrail()");
                tempobj.put("img","../../images/audit-trail.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.audittrail.ttip", null, RequestContextUtils.getLocale(request)));//"Track all user activities through comprehensive CRM system records.");
                jArr.put(tempobj);
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.activityFName) & 1) == 1) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.viewactivities", null, RequestContextUtils.getLocale(request)));//"View Activities");
                tempobj.put("onclick", "addAllActivityReportTab()");
                tempobj.put("img","../../images/view-activities.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.viewactivities.ttip", null, RequestContextUtils.getLocale(request)));//"Track all user activities through comprehensive CRM system records.");
                jArr.put(tempobj);
                HashMap<String, Integer> reportsPermMap = crmspreadsheetService.getReportsPermission(request, ProjectFeature.activityFName);
                publishSpreadSheetConfig(reportsPermMap, companyid, userid,ProjectFeature.activityFName,RequestContextUtils.getLocale(request));
            }

            if (sessionHandlerImpl.getCompanyid(request).equals(ConfigReader.getinstance().get("sunrise_companyid"))) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.calibcertificate", null, RequestContextUtils.getLocale(request)));//"Calibration Certificate");
                tempobj.put("onclick", "addSunRiseCalibrationGridTab()");
                tempobj.put("img","../../images/calibration-certificate.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.calibcertificate", null, RequestContextUtils.getLocale(request)));//"Calibration Certificate.");
                jArr.put(tempobj);
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.customReport) & 4) == 4) {
                tempobj = new JSONObject();
                tempobj.put("name", messageSource.getMessage("crm.dashboard.newcustomreport", null, RequestContextUtils.getLocale(request)));//"New Custom Report");
                tempobj.put("onclick", "loadCreateReportGrid()");
                tempobj.put("img","../../images/new-custom-report-icon.png");
                tempobj.put("qtip",messageSource.getMessage("crm.dashboard.newcustomreport.ttip", null, RequestContextUtils.getLocale(request)));//"Click to create new custom report.");
                jArr.put(tempobj);
            }
/*
            tempobj = new JSONObject();
            tempobj.put("name", "Lead SpreadSheet");
            tempobj.put("onclick", "addLeadTab1()");
            tempobj.put("img","../../images/leads-icon.png");
            tempobj.put("qtip","Capture all relevant information on potential sales opportunities or prospects i.e. individuals who have expressed some interest in your product or company.");
            jArr.put(tempobj);
*/
            /* Global Activity Module Removed
            if ((AuthHandler.getPerms(request, ProjectFeature.activityFName) & 1) == 1)
                jdata += "{\"update\":\"<a href=# onclick='addActivityMasterTab()' wtf:qtip='Maintain complete details of all activities including tasks and events associated with existing and prospective customers'>Activities</a>\"},";*/

// Target Module not shown in module links
//            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.targetFName) & 1) == 1) {
//                tempobj = new JSONObject();
//                tempobj.put("name", "Targets");
//                tempobj.put("onclick", "addTargetModuleTab()");
//                tempobj.put("img","../../images/target.png");
//                tempobj.put("qtip","Enhance the effectiveness of your marketing campaigns by building lists of prospective customers on whom you want to focus. You can easily add or import targets in convenient file formats.");
//                jArr.put(tempobj);
//            }
            jobj1.put("data", jArr);
            jobj.put("CrmModuleDrag", jobj1);
       return jobj;
    }

    public ModelAndView getUpdatesForSingleWidgets(HttpServletRequest request, HttpServletResponse response) throws ServiceException
    {
        JSONObject wObj = new JSONObject();
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = this.crmManagerDAOObj.recursiveUserIds(userid);

            int type = Integer.parseInt(request.getParameter("type"));
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String companyId = sessionHandlerImpl.getCompanyid(request);

            List<Map<String, Object>> widgetDataList = getDetailForWidget(request, usersList, Collections.singletonList(type), start, limit, companyId);

            if (widgetDataList != null && !widgetDataList.isEmpty())
            {
                Map<String, Object> widgetData = widgetDataList.get(0);
                WidgetUpdateData widgetUpdateData = (WidgetUpdateData) widgetData.get("widgetData");
                wObj.put("count", widgetUpdateData.count);

                JSONArray dataJArray = new JSONArray();

                if (widgetUpdateData.data != null)
                {
                    for (String str : widgetUpdateData.data)
                    {
                        JSONObject obj = new JSONObject();
                        obj.put("update", str);
                        dataJArray.put(obj);
                    }
                }
                wObj.put("data", dataJArray);
            }

            // TODO finish this
            //getColumnWiseWidget(request, Collections.singletonList(DashboardConstants.WIDGETS[type]), usersList, jobj, start, limit, companyId);

        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE("crmDashboardController.getUpdatesForSingleWidgets:" + e.getMessage(), e);
        } catch (JSONException e) {
            throw ServiceException.FAILURE("crmDashboardController.getUpdatesForSingleWidgets:" + e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", wObj.toString());
    }

    private String getUpdatesForWidgets(HttpServletRequest request, StringBuffer usersList, String companyId) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            int start1 = Integer.parseInt(start);
            int limit1 = Integer.parseInt(limit);
            String userid = sessionHandlerImpl.getUserid(request);

            if (companyId == null)
            {
                companyId = sessionHandlerImpl.getCompanyid(request);
            }
            String userName="";
            // load company if company id is null

//            StringBuffer usersList = this.crmManagerDAOObj.recursiveUsers(userid);
//            usersList.append("'" + userid + "'");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", userid);

            String groups = "";
            kmsg = permissionHandlerDAOObj.getUserPermission(requestParams);
            List<Object[]> rows = kmsg.getEntityList();
            List<String> groupList = new ArrayList<String>();
            for(Object[] row: rows) {
                String keyName = row[0].toString();
                String value = row[1].toString();
                int perm = Integer.parseInt(value);
                if ((perm & 1) == 1) {
                    groupList.add(keyName);
                }
            }

            int interval = 7;
            requestParams = new HashMap<String, Object>();
            requestParams.put("userslist", usersList);
            requestParams.put("groups", groupList);
            requestParams.put("start", start1);
            requestParams.put("limit", limit1);
            requestParams.put("interval", interval);
            requestParams.put("companyid", companyId);

            kmsg = this.auditTrailDAOObj.getAuditDetails(requestParams);
            List<AuditTrail> auditTrailList = kmsg.getEntityList();
            JSONArray jArr = new JSONArray();

            String tZStr = sessionHandlerImpl.getTimeZoneDifference(request);

            DateFormat df = authHandler.getDateFormatter(authHandler.getUserTimeFormat(request));
            if (tZStr != null)
            {
                TimeZone zone = TimeZone.getTimeZone("GMT" + tZStr);
                df.setTimeZone(zone);
            }

            for (AuditTrail auditTrail: auditTrailList)
            {
                JSONObject obj = new JSONObject();
                String username = StringUtil.getFullName(auditTrail.getUser());
                String details = "";
                try {
                    details = URLDecoder.decode(auditTrail.getDetails());
                } catch (Exception e) {
                    details = auditTrail.getDetails();
                }
                details = StringUtil.stringEllipsis(details);

                Date auditTime = auditTrail.getAuditTime();
                String time = "";
                if (auditTime != null)
                {
                    try
                    {
                        time = df.format(auditTime);
                    }
                    catch (Exception e){}
                }

                String updateDiv = "";

                if(request.getSession().getAttribute("iPhoneCRM") != null){
                    obj.put("desc", details);
                    obj.put("by", username);
                    obj.put("date", time);
                }
                else{
                    updateDiv += details;
                    updateDiv += " by <span style=\"color:#083772; !important;\">  <a href=# onclick=\"showProfilePage('" + auditTrail.getUser().getUserID() + "','" + StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeJavaScript(username)) + "')\">" + username + "</a>  </span>";
                    updateDiv += "<span style=\"color:gray;font-size:11px\"> on " + time + "</span>";
                    obj.put("update", getContentSpan(updateDiv));
                }

                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj.put("count", kmsg.getRecordTotalCount());
            jobj.put("success", true);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboardController.getUpdatesAudit:" + e.getMessage(), e);
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmDashboardController.getUpdatesForWidgets", ex);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj.toString();
    }

    private String getContentSpan(String textStr) {
        String span = "<div>" + textStr + "<div style='clear:both;visibility:hidden;height:0;line-height:0;'></div></div>";
        return span;
    }

    private List<Map<String, Object>> getDetailForWidget(HttpServletRequest request, StringBuffer usersList, List<Integer> types, int start, int limit, String companyId) throws ServiceException, JSONException, SessionExpiredException
    {
        List<Map<String, Object>> requestParamsList = new ArrayList<Map<String, Object>>();
        int interval = 7;
        for (Integer type : types)
        {
            KwlReturnObject kmsg = null;
            boolean heirarchyPerm = false;
            Map<String, Object> requestParams = new HashMap<String, Object>();
            String grp = "";
            switch (type)
            {
            case 0:
                grp = ProjectFeature.campaignFName;
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_Campaign);
                break;
            case 1:
                grp = ProjectFeature.leadFName;
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_LEAD);
                break;
            case 2:
                grp = ProjectFeature.accountFName;
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_ACCOUNT);
                break;
            case 3:
                grp = ProjectFeature.contactFName;
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_CONTACT);
                break;
            case 4:
                grp = ProjectFeature.opportunityFName;
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_OPPORTUNITY);
                break;
            case 5:
                grp = ProjectFeature.caseFName;
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_CASE);
                break;
            case 6:
                grp = ProjectFeature.activityFName;
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_ACTIVITY);
                break;
            case 7:
                grp = ProjectFeature.productFName;
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_PRODUCT);
                break;
           case 8:
               grp = ProjectFeature.activityFName;
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.MODULE_ACTIVITY);
                break;
            }
            /*
             * get audit action, details for user. with permission and also
             * audit of users under him
             */
            if (!heirarchyPerm)
            {
                requestParams.put("userslist", usersList);
            }
            requestParams.put("groups", grp);
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("type", type);
            requestParamsList.add(requestParams);
        }
        List<KwlReturnObject> results = auditTrailDAOObj.getAuditDetails(requestParamsList, start, limit, interval, companyId);

        processAuditLogData(request, results, requestParamsList);

        return requestParamsList;
    }

    private void processAuditLogData(HttpServletRequest request, List<KwlReturnObject> results, List<Map<String, Object>> requestParamsList) throws SessionExpiredException, ServiceException, JSONException
    {
        int i = 0;
        String tZStr = sessionHandlerImpl.getTimeZoneDifference(request);

        DateFormat df = authHandler.getDateFormatter(authHandler.getUserTimeFormat(request));
        if (tZStr != null)
        {
            TimeZone zone = TimeZone.getTimeZone("GMT" + tZStr);
            df.setTimeZone(zone);
        }

        Map<String, KwlReturnObject> resultMap = new HashMap<String, KwlReturnObject>();

        for (KwlReturnObject kmsg: results)
        {
            resultMap.put(kmsg.getMsg(), kmsg);
        }

        for (Map<String, Object> params: requestParamsList)
        {
            KwlReturnObject kmsg =  resultMap.get((String) params.get("groups"));

            if (kmsg == null)
            {
                continue;
            }
            int type = (Integer) params.get("type");
            List<DashboardUpdate> auditTrailList = kmsg.getEntityList();
            List<String> updateDivList = new ArrayList<String>();

            Map<String, List<AuditTrailDetail>> auditTrailDetailMap = new HashMap<String, List<AuditTrailDetail>>();
            List<String> recordIds = new ArrayList<String>();
            List<AuditTrailDetail> origList = new ArrayList<AuditTrailDetail>();
            WidgetUpdateData wData = new WidgetUpdateData();
            if(type!=8){
            if (auditTrailList != null)
            {
                for (DashboardUpdate auditTrailArr: auditTrailList)
                {
                    // TODO Fixme
                    AuditTrailDetail auditTrailDetail = new AuditTrailDetail();
                    auditTrailDetail.userName = getFullName(auditTrailArr.getFirstName(), auditTrailArr.getLastName());
                    auditTrailDetail.userId = auditTrailArr.getUserId();
                    String recId = auditTrailArr.getRecid();
                    auditTrailDetail.recId = recId;

                    String details =auditTrailArr.getDetails();
                    recordIds.add(recId);
                    try {
                        auditTrailDetail.detail = URLDecoder.decode(details);

                    } catch (Exception e) {
                        auditTrailDetail.detail = details;
                    }

                    if (auditTrailDetailMap.containsKey(recId))
                    {
                        List<AuditTrailDetail> list = auditTrailDetailMap.get(recId);
                        list.add(auditTrailDetail);
                    }
                    else
                    {
                        List<AuditTrailDetail> list = new ArrayList<AuditTrailDetail>();
                        list.add(auditTrailDetail);
                        auditTrailDetailMap.put(recId, list);
                    }

                    // set time
                    Long auditTimeLong =(Long) auditTrailArr.getAuditTime();
                    Date auditTime = null;
                    if (auditTimeLong != null)
                    {
                        auditTime = new Date(auditTimeLong);
                    }
                    if (auditTime != null)
                    {
                        try
                        {
                            auditTrailDetail.time = df.format(auditTime);
                        }
                        catch (Exception e){}
                    }
                    origList.add(auditTrailDetail);
                }
            }

            switch (type)
            {
                case 0:
                    getCampaigns(auditTrailDetailMap, recordIds);
                    break;
                case 1:
                    getLeads(auditTrailDetailMap, recordIds);
                    break;
                case 2:
                     getAccounts(auditTrailDetailMap, recordIds);
                    break;
                case 3:
                    getContacts(auditTrailDetailMap, recordIds);
                    break;
                case 4:
                    getOpportunities(auditTrailDetailMap, recordIds);
                    break;
                case 5:
                    getCases(auditTrailDetailMap, recordIds);
                    break;
                case 6 :
                    getActivities(auditTrailDetailMap, recordIds);
                    break;
                case 7:
                    getProducts(auditTrailDetailMap, recordIds);
                    break;
            }

            for (AuditTrailDetail detail: origList)
            {
                setAuditInfo(detail);
                if (detail.updateDiv != null)
                {
                    updateDivList.add(detail.updateDiv);
                }
                else if (detail.detail != null)
                {
                    updateDivList.add(detail.detail);
                }
            }


            wData.data = updateDivList;
            wData.count = kmsg.getRecordTotalCount();

           }else{
                int start=(Integer) params.get("start");
                int limit=(Integer)params.get("limit");
                StringBuffer usersList=new StringBuffer();
                if(params.containsKey("userslist")){
                    usersList= (StringBuffer) params.get("userslist");
                }
                int count=getTopActivities(updateDivList,usersList,df,tZStr,start,limit);
                wData.count = count;
                wData.data = updateDivList;
           }
            params.put("widgetData", wData);

        }
    }

    private String getFullName(String fName, String lName)
    {
        String fullname = fName;
        if (fullname != null && lName != null)
        {
            fullname += " " + lName;
        }
        return fullname;
    }

    private void getCampaigns(Map<String, List<AuditTrailDetail>> auditTrailDetailMap, List<String> recordIds)
    {
        String itemname;
        try{
            List<CrmCampaign> campaigns = crmCampaignDAOObj.getCampaigns(recordIds);

            if (campaigns != null)
            {
                for (CrmCampaign campaign: campaigns)
                {
                    List<AuditTrailDetail> list = auditTrailDetailMap.get(campaign.getCampaignid());

                    for (AuditTrailDetail auditDetail: list)
                    {
                        itemname = campaign.getCampaignname();
                        if(!campaign.getIsarchive() && campaign.getDeleteflag() != 1) {
                            auditDetail.detail = auditDetail.detail.replaceAll(Matcher.quoteReplacement(itemname), "<a href=# onclick=\"addCampaignTab('" + campaign.getCampaignid() + "')\">" + Matcher.quoteReplacement(itemname) + "</a>");
                        }
                    }
                }
            }

        }catch(Exception ex){
            logger.warn(ex.getMessage(),ex);
        }
    }

    private void getLeads(Map<String, List<AuditTrailDetail>> auditTrailDetailMap, List<String> recordIds)
    {
        String itemname;
        try{
            List<CrmLead> leads = getCrmleadDAO().getLeads(recordIds);

            if (leads != null)
            {
                for (CrmLead lead: leads)
                {
                    List<AuditTrailDetail> list = auditTrailDetailMap.get(lead.getLeadid());

                    for (AuditTrailDetail auditDetail: list)
                    {
                        itemname = lead.getLastname();
                        if(!lead.getIsarchive() && lead.getDeleteflag() != 1) {
                            auditDetail.detail = auditDetail.detail.replaceAll(Matcher.quoteReplacement(itemname), "<a href=# onclick=\"addLeadTab('" + lead.getLeadid() + "')\">" + Matcher.quoteReplacement(itemname) + "</a>");
                        }
                    }
                }
            }

        }catch(Exception ex){
            logger.warn(ex.getMessage(),ex);
        }
    }

    private void getAccounts(Map<String, List<AuditTrailDetail>> auditTrailDetailMap, List<String> recordIds)
    {
        String itemname;
        try{
            List<CrmAccount> accounts = getAccountDAO().getAccounts(recordIds);

            if (accounts != null)
            {
                for (CrmAccount account: accounts)
                {
                    List<AuditTrailDetail> list = auditTrailDetailMap.get(account.getAccountid());

                    for (AuditTrailDetail auditDetail: list)
                    {
                        itemname = account.getAccountname();
                        if(!account.getIsarchive() && account.getDeleteflag() != 1) {
                            auditDetail.detail = auditDetail.detail.replaceAll(Matcher.quoteReplacement(itemname), "<a href=# onclick=\"addAccountTab('" + account.getAccountid() + "')\">" + Matcher.quoteReplacement(itemname) + "</a>");
                        }
                    }
                }
            }

        }catch(Exception ex){
            logger.warn(ex.getMessage(),ex);
        }
    }

    private void getContacts(Map<String, List<AuditTrailDetail>> auditTrailDetailMap, List<String> recordIds)
    {
        String itemname;
        try{
            List<CrmContact> contacts = getContactDAO().getContacts(recordIds);

            if (contacts != null)
            {
                for (CrmContact contact: contacts)
                {
                    List<AuditTrailDetail> list = auditTrailDetailMap.get(contact.getContactid());

                    for (AuditTrailDetail auditDetail: list)
                    {
                        itemname = contact.getFirstname() + " " + contact.getLastname();
                        if(!contact.getIsarchive() && contact.getDeleteflag() != 1) {
                            auditDetail.detail = auditDetail.detail.replaceAll(Matcher.quoteReplacement(itemname), "<a href=# onclick=\"addContactTab('" + contact.getContactid() + "')\">" + Matcher.quoteReplacement(itemname) + "</a>");
                        }
                    }
                }
            }

        }catch(Exception ex){
            logger.warn(ex.getMessage(),ex);
        }
    }

    private void getOpportunities(Map<String, List<AuditTrailDetail>> auditTrailDetailMap, List<String> recordIds)
    {
        String itemname;
        try{
            List<CrmOpportunity> opportunities = getOpportunityDAO().getOpportunities(recordIds);

            if (opportunities != null)
            {
                for (CrmOpportunity opportunity: opportunities)
                {
                    List<AuditTrailDetail> list = auditTrailDetailMap.get(opportunity.getOppid());

                    for (AuditTrailDetail auditDetail: list)
                    {
                        itemname = opportunity.getOppname();
                        if(!opportunity.getIsarchive() && opportunity.getDeleteflag() != 1) {
                            auditDetail.detail = auditDetail.detail.replaceAll(Matcher.quoteReplacement(itemname), "<a href=# onclick=\"addOpportunityTab('" + opportunity.getOppid() + "')\">" + Matcher.quoteReplacement(itemname) + "</a>");
                        }
                    }
                }
            }

        }catch(Exception ex){
            logger.warn(ex.getMessage(),ex);
        }
    }

    private void getCases(Map<String, List<AuditTrailDetail>> auditTrailDetailMap, List<String> recordIds)
    {
        String itemname;
        try{
            List<CrmCase> cases = getCaseDAO().getCases(recordIds);

            if (cases != null)
            {
                for (CrmCase caseObj: cases)
                {
                    List<AuditTrailDetail> list = auditTrailDetailMap.get(caseObj.getCaseid());

                    for (AuditTrailDetail auditDetail: list)
                    {
                        itemname = caseObj.getSubject();
                        if(!caseObj.getIsarchive() && caseObj.getDeleteflag() != 1) {
                            auditDetail.detail = auditDetail.detail.replaceAll(Matcher.quoteReplacement(itemname), "<a href=# onclick=\"addCaseTab('" + caseObj.getCaseid() + "')\">" + Matcher.quoteReplacement(itemname) + "</a>");
                        }
                    }
                }
            }

        }catch(Exception ex){
            logger.warn(ex.getMessage(),ex);
        }
    }

    private void getActivities(Map<String, List<AuditTrailDetail>> auditTrailDetailMap, List<String> recordIds)
    {
        String itemname;
        try{
            List<CrmActivityMaster> activities = getActivityDAO().getActivities(recordIds);

            if (activities != null)
            {
                for (CrmActivityMaster activity: activities)
                {
                    List<AuditTrailDetail> list = auditTrailDetailMap.get(activity.getActivityid());

                    for (AuditTrailDetail auditDetail: list)
                    {
                         itemname = activity.getFlag();
                         if(!activity.getIsarchive() && activity.getDeleteflag() != 1) {
                             auditDetail.detail = auditDetail.detail.replaceAll(Matcher.quoteReplacement(itemname), "<a href=# onclick=\"addModuleActivityMasterTab('" + auditDetail.getRecId() + "',"+activity.getMapwith()+")\">" + Matcher.quoteReplacement(itemname) + "</a>");
                         }
                    }
                }
            }

        }catch(Exception ex){
            logger.warn(ex.getMessage(),ex);
        }
    }
    
    private int getTopActivities(List<String> updateDivList,StringBuffer usersList,DateFormat df,String tZStr,int start,int limit) throws  ServiceException{
       List<Object[]> list   = new  ArrayList();
       int count=0;
       try{
           SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           sdf.setTimeZone(TimeZone.getTimeZone("GMT" + tZStr));
           String topActivityDaysStr = ConfigReader.getinstance().get("top_activity_days");
           int topActivityDays=StringUtil.isNullOrEmpty(topActivityDaysStr)?2:Integer.parseInt(topActivityDaysStr);
           Date today = new Date();
           today = sdf.parse(sdf.format(today));

           Calendar todayEndTime = new GregorianCalendar();
           todayEndTime.setTimeInMillis(today.getTime());
           todayEndTime.set(todayEndTime.get(Calendar.YEAR), todayEndTime.get(Calendar.MONTH), todayEndTime.get(Calendar.DAY_OF_MONTH),
                   23, 59, 59);
           
           long todayLong =today.getTime();//DateUtil.getStrippedDateAsLong(today, 0);
           long uptoLong = DateUtil.getStrippedDateAsLong(today, (topActivityDays));
           uptoLong += (todayEndTime.getTimeInMillis() - todayLong);
           int tempCount = 0;
           list   = getActivityDAO().getDetailPanelTopActivity(todayLong,uptoLong,usersList).getEntityList();
           Collections.sort(list,new CrmActComparator());
           if(list != null){
                if(tempCount >= start&&tempCount<start+limit|| tempCount+list.size() >= start&&tempCount+list.size()<start+limit){
                    for (Object[] rows: list){
                        CrmActivityMaster actMasterobj = (CrmActivityMaster) rows[0];
                        String rec = (String) rows[2];
                        String recname = StringUtil.isNullOrEmpty(rec)?"": ("-"+rec);
                        int moduleno = (Integer) rows[3];
//                        String eventStartDate = crmManagerDAOObj.preferenceDatejsformat(tZStr, actMasterobj.getStartdate(), df);
                        String eventStartDate = df.format(actMasterobj.getStartdate());
                        if(tempCount>=start&&tempCount<start+limit){
                            if(!actMasterobj.getIsarchive() && actMasterobj.getDeleteflag() != 1) {
                                updateDivList.add("<div> <a href=# onclick=\"addModuleActivityMasterTab('" + actMasterobj.getActivityid() + "',"+actMasterobj.getMapwith()+")\">" + actMasterobj.getFlag()+ ( StringUtil.isNullOrEmpty(actMasterobj.getSubject())?"": " ("+actMasterobj.getSubject()+")")  +"</a> for "+Constants.ACTIVITYMODULEIDMAP.get(moduleno)+recname+ " will start on "+eventStartDate+"</div>");
                            } else {
                                updateDivList.add("<div> " + actMasterobj.getFlag()+ ( StringUtil.isNullOrEmpty(actMasterobj.getSubject())?"": " ("+actMasterobj.getSubject()+")")  +" for "+Constants.ACTIVITYMODULEIDMAP.get(moduleno)+recname+ " will start on "+eventStartDate+"</div>");
                            }                            
                        }
                        tempCount++;
                    }
                }else{
                    tempCount+=list.size();
                }                
                count+=list.size();
            }
        }catch(Exception ex){
            logger.warn(ex.getMessage(),ex);
        }
       return count;
    }

    public ModelAndView getAcitvityRelatedId(HttpServletRequest request, HttpServletResponse response) {
        JSONObject dataJobj = null;
        String activityid = request.getParameter("activityid");
        int moduleId = Integer.parseInt(request.getParameter("moduleId"));
        dataJobj = activityManagementService.getActivityRelatedJson(activityid, moduleId);
        return new ModelAndView("jsonView", "model", dataJobj.toString());
    }
    private void getProducts(Map<String, List<AuditTrailDetail>> auditTrailDetailMap, List<String> recordIds)
    {
        String itemname;
        try{
            List<CrmProduct> products = getProductDAO().getProducts(recordIds);

            if (products != null)
            {
                for (CrmProduct product: products)
                {
                    List<AuditTrailDetail> list = auditTrailDetailMap.get(product.getProductid());

                    for (AuditTrailDetail auditDetail: list)
                    {
                        itemname = product.getProductname();
                        if(!product.getIsarchive() && product.getDeleteflag() != 1) {
                            auditDetail.detail = auditDetail.detail.replaceAll(Matcher.quoteReplacement(itemname), "<a href=# onclick=\"addProductMasterTab('" + product.getProductid() + "')\">" + Matcher.quoteReplacement(itemname) + "</a>");
                        }
                    }
                }
            }

        }catch(Exception ex){
            logger.warn(ex.getMessage(),ex);
        }
    }

    private void setAuditInfo(AuditTrailDetail auditDetail)
    {
        try
        {
            auditDetail.detail = StringUtil.stringEllipsis(auditDetail.detail);
        } catch (ServiceException e){}
        StringBuilder updateDiv = new StringBuilder();
        updateDiv.append(auditDetail.detail);
        updateDiv.append(" by <span style=\"color:#083772; !important;\">  <a href=# onclick=\"showProfilePage('").append(auditDetail.getUserId()).append("','").append(StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeJavaScript(auditDetail.userName))).append("')\">").append(auditDetail.userName).append("</a> </span>");
        updateDiv.append("<span style=\"color:gray;font-size:11px\"> on ").append(auditDetail.time).append("</span>");
        auditDetail.updateDiv = getContentSpan(updateDiv.toString());
    }

    private String getdetailsforComment(String commentId , String details, String modName) throws ServiceException {
        KwlReturnObject kmsg = null;
        try{
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("recid", commentId);
            kmsg = commentDAOObj.getComments(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            String Comment="";
            if (ite.hasNext()) {
                Comment crmComment = (Comment) ite.next();
                Comment=crmComment.getComment();
            }

            String commentLink = details.replaceFirst("Comment:","");
            String addedFor = "";
            addedFor = commentLink.substring(commentLink.lastIndexOf(","), commentLink.length());
            commentLink = commentLink.substring(0, commentLink.lastIndexOf(","));
            details="Comment:<a href=# onclick=\"getHTMLComment('"+Comment.replace("\"","")+"','"+modName+"')\">"+commentLink+"</a>"+addedFor;
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboardController.getdetailsforComment", e);
        }
        return details;
    }

    private String getReportWidgetLinks(HttpServletRequest request) throws ServiceException {
        String jdata = "";
        try {
            ArrayList li = new ArrayList();
            crmdashboardHandler.getLeadsReportsLink(sessionHandlerImpl.getPerms(request, "Lead Report"), li, RequestContextUtils.getLocale(request));
            crmdashboardHandler.getAccountReportsLink(sessionHandlerImpl.getPerms(request, "AccountReport"), li, RequestContextUtils.getLocale(request));
            crmdashboardHandler.getContactReportsLink(sessionHandlerImpl.getPerms(request, "ContactReport"), li, RequestContextUtils.getLocale(request));
            crmdashboardHandler.getOpportunityReportsLink(sessionHandlerImpl.getPerms(request, "OpportunityReport"), li ,RequestContextUtils.getLocale(request));
            crmdashboardHandler.getActivityReportsLink(sessionHandlerImpl.getPerms(request, "ActivityReport"), li, RequestContextUtils.getLocale(request));
            crmdashboardHandler.getCaseReportsLink(sessionHandlerImpl.getPerms(request, "CaseReport"), li, RequestContextUtils.getLocale(request));
            crmdashboardHandler.getProductReportsLink(sessionHandlerImpl.getPerms(request, "ProductReport"), li, RequestContextUtils.getLocale(request));
            crmdashboardHandler.getOpportunityProductReportsLink(request, li, RequestContextUtils.getLocale(request));
            crmdashboardHandler.getSalesReportsLink(sessionHandlerImpl.getPerms(request, "SalesReport"), li, RequestContextUtils.getLocale(request));
            crmdashboardHandler.getCampaignReportsLink(sessionHandlerImpl.getPerms(request, "CampaignReport"), li, RequestContextUtils.getLocale(request));
            crmdashboardHandler.getTargetReportsLink(request, li,RequestContextUtils.getLocale(request));
            crmdashboardHandler.getGoalReportsLink(request, li, RequestContextUtils.getLocale(request));
            li = getBubbleSortList(li);
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String limitReport = request.getParameter("limitReport");
            if(!StringUtil.isNullOrEmpty(limitReport)){
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
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return jdata;
    }

    private String getCustomReportWidgetLinks(HttpServletRequest request,List ll) throws ServiceException {
        String jdata = "";
        try {
           
            List li= crmdashboardHandler.getCustomReportLinks(request, ll, RequestContextUtils.getLocale(request));
           // li = getBubbleSortList(li);
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String limitReport = request.getParameter("limitReport");
            if(!StringUtil.isNullOrEmpty(limitReport)){
                limit = Integer.parseInt(limitReport);
            }
            limit = (start + limit) > li.size() ? li.size() : (start + limit);
            List currli = (List) li.subList(start, limit);
            Iterator it = currli.iterator();
            JSONArray newArr = new JSONArray();
            while (it.hasNext()) {
                newArr.put(new JSONObject(it.next().toString()));
            }
            JSONObject jobj = new JSONObject();
            jobj.put("count", li.size());
            jobj.put("data", newArr);
            jdata = jobj.toString();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return jdata;
    }

    private ArrayList getBubbleSortList(ArrayList lis) throws ServiceException, JSONException {
        for (int x=1; x < lis.size(); x++) {
            for (int i=0; i < lis.size()-x; i++) {
                JSONObject jobj = new JSONObject(lis.get(i).toString());
                String reportname =jobj.get("name").toString().toLowerCase();
                JSONObject jobj1 = new JSONObject(lis.get(i+1).toString());
                String reportname1 =jobj1.get("name").toString().toLowerCase();
                if(reportname.compareToIgnoreCase(reportname1)>0){
                    Object temp = lis.get(i);
                    lis.set(i,lis.get(i+1));
                    lis.set(i+1, temp);
                }
            }
        }
        return lis;
    }

public JSONObject getCamapignWidgetReportsLink(HttpServletRequest request) throws ServiceException{
        List ll = null;
        List ll1 = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int dl = 0;
        int dl1 = 0;
        boolean isExists = false;
        KwlReturnObject result = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", Arrays.asList("campaignid.company.companyID","INcampaignid.usersByUserid.userID","campaignid.deleteflag"));//
            requestParams.put("filter_values", Arrays.asList(companyid,usersList,0));
            requestParams.put("select", "select emailmarketingid.name, campaignid.campaignname, sum(viewed) ,count(*),emailmarketingid.id");
            requestParams.put("order_by", Arrays.asList("sum(viewed)"));
            requestParams.put("order_type", Arrays.asList("desc"));
            requestParams.put("start", 0);
            requestParams.put("limit", 5);
            requestParams.put("allflag", false);
            requestParams.put("group_by", Arrays.asList("emailmarketingid"));
            result = crmCampaignDAOObj.getCampaignLog(requestParams);
            ll = result.getEntityList();
            dl = result.getRecordTotalCount();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Object row[] = (Object[]) ite.next();
                String obj = (String) row[0];
                String obj2 = (String) row[1];
                Long obj3 = row[2] != null ? Long.valueOf(row[2].toString()) : null;
				Long obj4 = row[3] != null ? Long.valueOf(row[3].toString()) : null;
                String marketingId = (String) row[4];

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("marketingid", marketingId);
                tmpObj.put("marketingname", obj);
                tmpObj.put("campaignname", obj2);
                tmpObj.put("viewed", obj3);
                tmpObj.put("sentmail", obj4);
                jarr.put(tmpObj);
            }

            requestParams.clear();
            requestParams.put("filter_names", Arrays.asList("campaignid.company.companyID","INcampaignid.usersByUserid.userID","campaignid.deleteflag"));
            requestParams.put("filter_values", Arrays.asList(companyid,usersList,0));
            requestParams.put("select", "select emailmarketingid.name, campaignid.campaignname, sum(viewed) ,count(*), emailmarketingid.id");
            requestParams.put("order_by", Arrays.asList("count(*)"));
            requestParams.put("order_type", Arrays.asList("desc"));
            requestParams.put("start", 0);
            requestParams.put("limit", 5);
            requestParams.put("allflag", false);
            requestParams.put("group_by", Arrays.asList("emailmarketingid"));
            result = crmCampaignDAOObj.getCampaignLog(requestParams);
            ll1 = result.getEntityList();
            dl1 = result.getRecordTotalCount();
            Iterator ite1 = ll1.iterator();
            while (ite1.hasNext()) {
                Object row[] = (Object[]) ite1.next();
                String obj = (String) row[0];
                String obj2 = (String) row[1];
                Long obj3 = row[2] != null ? Long.valueOf(row[2].toString()) : null;
				Long obj4 = row[3] != null ? Long.valueOf(row[3].toString()) : null;
                String marketingId = (String) row[4];

                isExists = crmEmailMarketingHandler.isAlreadyExists(jarr, marketingId);
                if (!isExists) {
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("marketingid", marketingId);
                    tmpObj.put("marketingname", obj);
                    tmpObj.put("campaignname", obj2);
                    tmpObj.put("viewed", obj3);
                    tmpObj.put("sentmail", obj4);
                    jarr.put(tmpObj);
                }
            }

            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("campaignReport", true);
            jobj.put("totalCount", dl+dl1);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmManager.getModule", e);
        }
        return jobj;
    }

public CrmDashboardDAO getCrmDashboardDAO()
{
    return crmDashboardDAO;
}

public void setCrmDashboardDAO(CrmDashboardDAO crmDashboardDAO)
{
    this.crmDashboardDAO = crmDashboardDAO;
}

    class DashboardItemInfo
    {
        private String id;
        private String dashboardUpdate;

        public String getDashboardUpdate()
        {
            return dashboardUpdate;
        }

        public void setDashboardUpdate(String dashboardUpdate)
        {
            this.dashboardUpdate = dashboardUpdate;
        }

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

    }

    class AuditTrailDetail
    {
        private String recId;
        private String userName;
        private String detail;
        private String time;
        private String userId;
        private String updateDiv;

        public String getRecId()
        {
            return recId;
        }
        public void setRecId(String recId)
        {
            this.recId = recId;
        }
        public String getUserName()
        {
            return userName;
        }
        public void setUserName(String userName)
        {
            this.userName = userName;
        }
        public String getDetail()
        {
            return detail;
        }
        public void setDetail(String detail)
        {
            this.detail = detail;
        }
        public String getTime()
        {
            return time;
        }
        public void setTime(String time)
        {
            this.time = time;
        }
        public String getUserId()
        {
            return userId;
        }
        public void setUserId(String userId)
        {
            this.userId = userId;
        }
        public String getUpdateDiv()
        {
            return updateDiv;
        }
        public void setUpdateDiv(String updateDiv)
        {
            this.updateDiv = updateDiv;
        }

    }

    class WidgetUpdateData
    {
        private List<String> data;
        private int count;
    }

    public crmLeadDAO getCrmleadDAO()
    {
        return crmleadDAO;
    }

    public void setCrmleadDAO(crmLeadDAO crmleadDAO)
    {
        this.crmleadDAO = crmleadDAO;
    }

    public crmAccountDAO getAccountDAO()
    {
        return accountDAO;
    }

    public void setAccountDAO(crmAccountDAO accountDAO)
    {
        this.accountDAO = accountDAO;
    }

    public crmContactDAO getContactDAO()
    {
        return contactDAO;
    }

    public void setContactDAO(crmContactDAO contactDAO)
    {
        this.contactDAO = contactDAO;
    }

    public crmOpportunityDAO getOpportunityDAO()
    {
        return opportunityDAO;
    }

    public void setOpportunityDAO(crmOpportunityDAO opportunityDAO)
    {
        this.opportunityDAO = opportunityDAO;
    }

    public crmCaseDAO getCaseDAO()
    {
        return caseDAO;
    }

    public void setCaseDAO(crmCaseDAO caseDAO)
    {
        this.caseDAO = caseDAO;
    }

    public crmActivityDAO getActivityDAO()
    {
        return activityDAO;
    }

    public void setActivityDAO(crmActivityDAO activityDAO)
    {
        this.activityDAO = activityDAO;
    }

    public crmProductDAO getProductDAO()
    {
        return productDAO;
    }

    public void setProductDAO(crmProductDAO productDAO)
    {
        this.productDAO = productDAO;
    }

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
		
	}

}
