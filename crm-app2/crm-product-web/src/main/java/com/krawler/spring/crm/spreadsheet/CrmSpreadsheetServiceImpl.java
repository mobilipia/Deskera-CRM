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
package com.krawler.spring.crm.spreadsheet;

import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.SpreadSheetConfig;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.spreadsheet.bizservice.SpreadsheetService;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.dashboard.CrmDashboardHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CrmSpreadsheetServiceImpl implements CrmSpreadsheetService {

    private SpreadsheetService spreadsheetService;
    private fieldManagerDAO fieldManagerDAOobj;
    private CrmDashboardHandler crmdashboardHandler; 

   	public void setCrmdashboardHandler(CrmDashboardHandler crmdashboardHandler) {
		this.crmdashboardHandler = crmdashboardHandler;
	}

	public void setSpreadsheetService(SpreadsheetService spreadsheetService) {
        this.spreadsheetService = spreadsheetService;
    }

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }
    private static Log LOG = LogFactory.getLog(CrmSpreadsheetServiceImpl.class);

    @Override
    public JSONObject getSpreadsheetConfig(HashMap<String, Integer> reportsPermMap, String module, String userId, String companyid,Locale locale) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            SpreadSheetConfig config = spreadsheetService.getSpreadsheetConfig(module, userId);
            if (config != null){
            	jobj.put("cid",config.getCid());
                if (!StringUtil.isNullOrEmpty(config.getRules())){
                	jobj.put("rules",new JSONObject(config.getRules()));
                }
                if (!StringUtil.isNullOrEmpty(config.getState())){
                	jobj.put("state",new JSONObject(config.getState()));
                }
            }
            JSONArray jarr = new JSONArray();
            jarr.put(jobj);
            jobj = new JSONObject();
            jobj.put("Header", getModuleHeader(module,companyid));
            getReportLinks(reportsPermMap, jarr, module, locale);
            jobj.put("data", jarr);
            jobj.put("success", true);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        } finally {
            return jobj;
        }
    }

    private JSONArray getModuleHeader(String module, String companyid) {
        KwlReturnObject kmsg = null;
        JSONArray jarr1 = new JSONArray();
        try {
            ArrayList filter_params = new ArrayList();
            ArrayList filter_names = new ArrayList();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            filter_names.add("dh.moduleName");
            filter_params.add(module);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", module);

            kmsg = fieldManagerDAOobj.getDefaultHeader(requestParams);
            List<DefaultHeader> defaultHeaders = kmsg.getEntityList();
            List<String> headerIds = new ArrayList<String>();
            for (DefaultHeader obj : defaultHeaders) {
                headerIds.add(obj.getId());
            }

            Map<String, Object[]> results = getColumnHeaderMap(fieldManagerDAOobj, headerIds, companyid);
            for (DefaultHeader obj : defaultHeaders) {
                if (results.containsKey(obj.getDefaultHeader())) {
                    Object[] mapEntry = results.get(obj.getDefaultHeader());
                    ColumnHeader obj1 = (ColumnHeader) mapEntry[1];
                    DefaultHeader obj2 = (DefaultHeader) mapEntry[0];
                    JSONObject jtemp = getHeaderObject(obj2);
                    jtemp.put("newheader", StringUtil.isNullOrEmpty(obj1.getNewHeader()) ? obj2.getDefaultHeader() : obj1.getNewHeader());
                    jtemp.put("ismandotory", obj1.isMandotory());
                    jtemp.put("id", obj1.getId());
                    jtemp.put("column_name", StringUtil.isNullOrEmpty(obj2.getDbcolumnname()) && obj2.getConfigid().equals("1") ? "" : obj2.getDbcolumnname());
                    jarr1.put(jtemp);
                } else if (!obj.isCustomflag()) {
                    JSONObject jtemp = getHeaderObject(obj);
                    jtemp.put("newheader", obj.getDefaultHeader());
                    jtemp.put("ismandotory", obj.isMandatory());
                    jtemp.put("id", "");
                    jarr1.put(jtemp);
                }
            }
        } catch(Exception ex) {
            LOG.warn(ex);
        } finally {
            return jarr1;
        }
    }
    
    public HashMap<String, Integer> getReportsPermission(HttpServletRequest request, String module) throws ServiceException, SessionExpiredException {
        HashMap<String, Integer> reportsPermMap = new HashMap<String, Integer>();
        if (Constants.moduleMap.containsKey(module)) {
            switch (Constants.moduleMap.get(module)) {
                case 1: // Lead                    
                    reportsPermMap.put(Constants.LEAD_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.LEAD_REPORT_PERMNAME));
                    break;
                case 2: // Opportunity
                    reportsPermMap.put(Constants.OPP_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.OPP_REPORT_PERMNAME));
//                    reportPerm = sessionHandlerImpl.getPerms(request,"Opportunity Report");
                    reportsPermMap.put(Constants.SALES_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.SALES_REPORT_PERMNAME));
                    break;
                case 3: // Account
                    reportsPermMap.put(Constants.ACCOUNT_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.ACCOUNT_REPORT_PERMNAME));
                    break;
                case 4: // Contact
                    reportsPermMap.put(Constants.CONTACT_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.CONTACT_REPORT_PERMNAME));
                    break;
                case 5: // Product
                    reportsPermMap.put(Constants.PRODUCT_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.PRODUCT_REPORT_PERMNAME));
                    break;
                case 6: // Case
                    reportsPermMap.put(Constants.CASE_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.CASE_REPORT_PERMNAME));
                    break;
                case 7: // Activity
                    reportsPermMap.put(Constants.ACTIVITY_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.ACTIVITY_REPORT_PERMNAME));
                    break;
                case 8: // Campaign
                    reportsPermMap.put(Constants.CAMPAIGN_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.CAMPAIGN_REPORT_PERMNAME));
                    break;
            }
        } else {
            if (module.endsWith("Activity")) {
                reportsPermMap.put(Constants.ACTIVITY_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.ACTIVITY_REPORT_PERMNAME));
            } else {
                if (module.endsWith("Contact")) {
                    reportsPermMap.put(Constants.CONTACT_REPORT_PERMNAME, sessionHandlerImpl.getPerms(request, Constants.CONTACT_REPORT_PERMNAME));
                }
            }
        }
        return reportsPermMap;
    }

    private void getReportLinks(HashMap<String, Integer> reportsPermMap, JSONArray jarr, String module,Locale locale) throws ServiceException {
        if (Constants.moduleMap.containsKey(module)) {
            JSONArray arr = new JSONArray();
            switch (Constants.moduleMap.get(module)) {
                case 1: // Lead
                    int reportPerm = reportsPermMap.containsKey(Constants.LEAD_REPORT_PERMNAME)?reportsPermMap.get(Constants.LEAD_REPORT_PERMNAME):0;
                    jarr.put(crmdashboardHandler.getLeadsReportsLink(reportPerm, new ArrayList(), locale));
                    break;
                case 2: // Opportunity
                    reportPerm = reportsPermMap.containsKey(Constants.OPP_REPORT_PERMNAME)?reportsPermMap.get(Constants.OPP_REPORT_PERMNAME):0;
                    arr = crmdashboardHandler.getOpportunityReportsLink(reportPerm, new ArrayList(), locale);
                    reportPerm = reportsPermMap.containsKey(Constants.SALES_REPORT_PERMNAME)?reportsPermMap.get(Constants.SALES_REPORT_PERMNAME):0;
                    JSONObject temp = crmdashboardHandler.getSalesReportsLink(reportPerm, new ArrayList(), locale);
                    if (temp != null) {
                        arr.put(temp);
                    }
                    jarr.put(arr);
                    break;
                case 3: // Account
                    reportPerm = reportsPermMap.containsKey(Constants.ACCOUNT_REPORT_PERMNAME)?reportsPermMap.get(Constants.ACCOUNT_REPORT_PERMNAME):0;
                    jarr.put(crmdashboardHandler.getAccountReportsLink(reportPerm, new ArrayList(), locale));
                    break;
                case 4: // Contact
                    reportPerm = reportsPermMap.containsKey(Constants.CONTACT_REPORT_PERMNAME)?reportsPermMap.get(Constants.CONTACT_REPORT_PERMNAME):0;
                    jarr.put(crmdashboardHandler.getContactReportsLink(reportPerm, new ArrayList(), locale));
                    break;
                case 5: // Product
                    reportPerm = reportsPermMap.containsKey(Constants.PRODUCT_REPORT_PERMNAME)?reportsPermMap.get(Constants.PRODUCT_REPORT_PERMNAME):0;
                    jarr.put(crmdashboardHandler.getProductReportsLink(reportPerm, new ArrayList(), locale));
                    break;
                case 6: // Case
                    reportPerm = reportsPermMap.containsKey(Constants.CASE_REPORT_PERMNAME)?reportsPermMap.get(Constants.CASE_REPORT_PERMNAME):0;
                    jarr.put(crmdashboardHandler.getCaseReportsLink(reportPerm, new ArrayList(), locale));
                    break;
                case 7: // Activity
                    reportPerm = reportsPermMap.containsKey(Constants.ACTIVITY_REPORT_PERMNAME)?reportsPermMap.get(Constants.ACTIVITY_REPORT_PERMNAME):0;
                    jarr.put(crmdashboardHandler.getActivityReportsLink(reportPerm, new ArrayList(), locale));
                    break;
                case 8: // Campaign
                    reportPerm = reportsPermMap.containsKey(Constants.CAMPAIGN_REPORT_PERMNAME)?reportsPermMap.get(Constants.CAMPAIGN_REPORT_PERMNAME):0;
                    jarr.put(crmdashboardHandler.getCampaignReportsLink(reportPerm, new ArrayList(), locale));
                    break;
            }
        } else {
            if (module.endsWith("Activity")) {
                int reportPerm = reportsPermMap.containsKey(Constants.ACTIVITY_REPORT_PERMNAME)?reportsPermMap.get(Constants.ACTIVITY_REPORT_PERMNAME):0;
                jarr.put(crmdashboardHandler.getActivityReportsLink(reportPerm, new ArrayList(), locale));
            } else {
                if (module.endsWith("Contact")) {
                    int reportPerm = reportsPermMap.containsKey(Constants.CONTACT_REPORT_PERMNAME)?reportsPermMap.get(Constants.CONTACT_REPORT_PERMNAME):0;
                    jarr.put(crmdashboardHandler.getContactReportsLink(reportPerm, new ArrayList(), locale));
                }
            }
        }

    }
    
    private static JSONObject getHeaderObject(DefaultHeader dh) throws JSONException {
        JSONObject jtemp = new JSONObject();
        jtemp.put("oldheader",dh.getDefaultHeader());
        jtemp.put("recordname", dh.getRecordname());
        jtemp.put("customflag", dh.isCustomflag());
        jtemp.put("dbname", dh.getPojoheadername());
        return jtemp;
    }

    private static Map<String, Object[]> getColumnHeaderMap(fieldManagerDAO fieldManagerDAOobj, List<String> headerIds, String companyId) {
	    Map<String, Object[]> result = new HashMap<String, Object[]>();
	    List<Object[]> colList = fieldManagerDAOobj.getColumnHeader(companyId, headerIds);
	    if (colList != null) {
	        for (Object[] col: colList) {
	            DefaultHeader dh = (DefaultHeader) col[0];
	            result.put(dh.getDefaultHeader(), col);
	        }
	    }
        return result;
    }
}
