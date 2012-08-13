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
package com.krawler.spring.crm.targetModule;

import com.krawler.crm.utils.AuditAction;
import com.krawler.common.admin.User;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.TargetModule;
import com.krawler.crm.dbhandler.crmManagerCommon;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.userModule.crmUserDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.HashMap;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author Karthik
 */
public class crmTargetReportController extends MultiActionController {

    private crmTargetReportDAO targetReportDAOObj;
    private crmUserDAO crmUserDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;

    public crmTargetReportDAO getcrmTargetReportDAO(){
        return targetReportDAOObj;
    }
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    
    public void setcrmTargetReportDAO(crmTargetReportDAO targetReportDAOObj1) {
        this.targetReportDAOObj = targetReportDAOObj1;
    }

    public void setcrmUserDAO(crmUserDAO crmUserDAOObj1) {
        this.crmUserDAOObj = crmUserDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public JSONObject getTargetReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                TargetModule obj = (TargetModule) ite.next();

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("name", (StringUtil.checkForNull(obj.getFirstname()) + " " + StringUtil.checkForNull(obj.getLastname())).trim());
                tmpObj.put("owner", obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("createddate", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
                tmpObj.put("email", obj.getEmail());

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                jobjTemp.put("header", "Target Name");
                jobjTemp.put("tip", "Target Name");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "name");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", "Owner");
                jobjTemp.put("tip", "Owner");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "owner");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", "Target Creation Date");
                jobjTemp.put("tip", "Target Creation Date");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("align", "center");
                jobjTemp.put("dataIndex", "createddate");
                jobjTemp.put("xtype","datefield");
                jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", "Email");
                jobjTemp.put("tip", "Email");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "email");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "name");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "owner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createddate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "email");
                jarrRecords.put(jobjTemp);

                commData.put("columns", jarrColumns);
                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return commData;
    }

    public ModelAndView targetsByOwnerReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImplObj.getUserid(request);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.CRM_TARGET_MODULENAME);

            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("usercomboid", StringUtil.checkForNull(request.getParameter("userCombo")));
            requestParams.put("heirarchyPerm", heirarchyPerm);
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));

            kmsg = targetReportDAOObj.targetsByOwner(requestParams, usersList);
            jobj = getTargetReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView targetsByOwnerExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImplObj.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("usercomboid", StringUtil.checkForNull(request.getParameter("userCombo")));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));

            kmsg = targetReportDAOObj.targetsByOwner(requestParams, usersList);
            jobj = getTargetReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
                String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
                String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
                String  GenerateDate = kwlCommonTablesDAOObj.getUserDateFormatter(dateFormatId, timeFormatId, timeZoneDiff).format(new Date());
                jobj.put("GenerateDate", GenerateDate);
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Targets_Owner,
                    "Targets by Owner Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView targetsByOwnerPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String userid = sessionHandlerImplObj.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);

            kmsg = crmUserDAOObj.getOwner(companyid, userid, usersList);
            dl = kmsg.getRecordTotalCount();
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                User obj = (User) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getUserID());
                tmpObj.put("name",(StringUtil.checkForNull(obj.getFirstName()) + " " + StringUtil.checkForNull(obj.getLastName())).trim());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = targetReportDAOObj.getTargetOwnerChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView targetsByOwnerBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String userid = sessionHandlerImplObj.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);

            kmsg = crmUserDAOObj.getOwner(companyid, userid, usersList);
            dl = kmsg.getRecordTotalCount();
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                User obj = (User) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getUserID());
                tmpObj.put("name", (StringUtil.checkForNull(obj.getFirstName()) + " " + StringUtil.checkForNull(obj.getLastName())).trim());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < max; k++) {
                kmsg = targetReportDAOObj.getTargetOwnerChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }
}
