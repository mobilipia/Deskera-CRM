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

import com.krawler.spring.crm.common.*;
import com.krawler.spring.crm.spreadSheet.spreadSheetController;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.dashboard.CrmDashboardHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class crmSpreadsheetController extends spreadSheetController {

    private fieldManagerDAO fieldManagerDAOobj;
    private CrmSpreadsheetService crmspreadsheetService;

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

	public void setCrmspreadsheetService(CrmSpreadsheetService spreadsheetService) {
		this.crmspreadsheetService = spreadsheetService;
	}

    @Override
    public ModelAndView getSpreadsheetConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String module = request.getParameter("module");
            HashMap<String, Integer> reportsPermMap = crmspreadsheetService.getReportsPermission(request, module);
            jobj = crmspreadsheetService.getSpreadsheetConfig(reportsPermMap, module, userid, companyid,RequestContextUtils.getLocale(request));
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getSpreadsheetCM(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            ModelAndView model = super.getSpreadsheetConfig(request, response);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            jobj = new JSONObject(model.getModel().get("model").toString());
            JSONArray jarr = jobj.getJSONArray("data");
            jobj = new JSONObject();

            String module = request.getParameter("module");
            ArrayList filter_params = new ArrayList();
            ArrayList filter_names = new ArrayList();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            JSONArray jarr1 = new JSONArray();

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
                    jtemp.put("column_name", StringUtil.isNullOrEmpty(obj2.getDbcolumnname())  && obj2.getConfigid().equals("1") ? "" : obj2.getDbcolumnname());
                    jarr1.put(jtemp);
                } else if (!obj.isCustomflag()) {
                    JSONObject jtemp = getHeaderObject(obj);
                    jtemp.put("newheader", obj.getDefaultHeader());
                    jtemp.put("ismandotory", obj.isMandatory());
                    jtemp.put("id", "");
                    jarr1.put(jtemp);
                }
            }

            JSONArray cm = jarr.getJSONObject(0).getJSONArray("state");
            for (int i = 0; i < cm.length(); i++) {
                JSONObject obj = cm.getJSONObject(i);
                obj.put("pdfwidth", 60);
                obj.put("header", "");
                //obj.put("id", obj.get("id"));
                obj.put("sortable", "");
                obj.put("xtype", "");
                obj.put("custom", "");
                obj.put("mandatory", "");
            }
            jobj.put("Header", jarr1);

//            if(Constants.moduleMap.containsKey(module)) {
//                JSONArray arr = new JSONArray();
//                switch(Constants.moduleMap.get(module)) {
//                    case 1: // Lead
//                            jarr.put(CrmDashboardHandler.getLeadsReportsLink(request, new ArrayList()));
//                            break;
//                    case 2: // Opportunity
//                            arr=CrmDashboardHandler.getOpportunityReportsLink(request, new ArrayList());
//                            JSONObject temp=CrmDashboardHandler.getSalesReportsLink(request, new ArrayList());
//                            if(temp != null ){
//                                arr.put(temp);
//                            }
//                            jarr.put(arr);
//                            break;
//                    case 3: // Account
//                            jarr.put(CrmDashboardHandler.getAccountReportsLink(request, new ArrayList()));
//                            break;
//                    case 4: // Contact
//                            jarr.put(CrmDashboardHandler.getContactReportsLink(request, new ArrayList()));
//                            break;
//                    case 5: // Product
//                            jarr.put(CrmDashboardHandler.getProductReportsLink(request, new ArrayList()));
//                            break;
//                    case 6: // Case
//                            jarr.put(CrmDashboardHandler.getCaseReportsLink(request, new ArrayList()));
//                            break;
//                    case 7: // Activity
//                            jarr.put(CrmDashboardHandler.getActivityReportsLink(request, new ArrayList()));
//                            break;
//                    case 8: // Campaign
//                            jarr.put(CrmDashboardHandler.getCampaignReportsLink(request, new ArrayList()));
//                            break;
//                }
//            }else{
//                if(module.endsWith("Activity")){
//                    jarr.put(CrmDashboardHandler.getActivityReportsLink(request, new ArrayList()));
//                }else{
//                    if(module.endsWith("Contact")){
//                        jarr.put(CrmDashboardHandler.getContactReportsLink(request, new ArrayList()));
//                    }
//                }
//            }
            jobj.put("data", jarr);
            jobj.put("success", true);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    private static JSONObject getHeaderObject(DefaultHeader dh) throws JSONException {
        JSONObject jtemp = new JSONObject();
        jtemp.put("oldheader", dh.getDefaultHeader());
        jtemp.put("recordname", dh.getRecordname());
        jtemp.put("customflag", dh.isCustomflag());
        jtemp.put("dbname", dh.getPojoheadername());
        return jtemp;
    }

    private static Map<String, Object[]> getColumnHeaderMap(fieldManagerDAO fieldManagerDAOobj, List<String> headerIds, String companyId) {
        Map<String, Object[]> result = new HashMap<String, Object[]>();
        List<Object[]> colList = fieldManagerDAOobj.getColumnHeader(companyId, headerIds);

        if (colList != null) {
            for (Object[] col : colList) {
                DefaultHeader dh = (DefaultHeader) col[0];
                result.put(dh.getDefaultHeader(), col);
            }
        }
        return result;
    }
}
