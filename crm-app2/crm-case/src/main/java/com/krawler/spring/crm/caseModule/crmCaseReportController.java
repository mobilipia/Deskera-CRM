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
package com.krawler.spring.crm.caseModule;

import com.krawler.common.service.ServiceException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.User;
import com.krawler.common.util.Header;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.account.dm.AccountOwnerInfo;
import com.krawler.crm.database.tables.CaseProducts;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.utils.AuditAction;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.accountModule.crmAccountHandler;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.contactModule.crmContactHandler;
import com.krawler.spring.crm.productModule.crmProductDAO;
import com.krawler.spring.crm.productModule.crmProductHandler;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import com.krawler.service.IChartService;
import java.util.Map;

public class crmCaseReportController extends MultiActionController implements MessageSourceAware {
    private crmCaseReportDAO caseReportDAOObj;
    private crmContactDAO crmContactDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmProductDAO crmProductDAOObj;
    private String successView;
    private crmCaseDAO crmCaseDAOObj;
    private crmCommonDAO crmCommonDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private MessageSource messageSource;
        
    private static final Log LOGGER = LogFactory.getLog(crmCaseReportController.class);
    private IChartService chartServiceObj;

    public void setcrmCaseDAO(crmCaseDAO crmCaseDAOObj1) {
        this.crmCaseDAOObj = crmCaseDAOObj1;
    }
    
    public void setChartService(IChartService IChartServiceObj) {
        this.chartServiceObj = IChartServiceObj;
    }
    
    public crmCaseReportDAO getcrmCaseReportDAO(){
        return caseReportDAOObj;
    }
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setcrmProductDAO(crmProductDAO crmProductDAOObj1) {
        this.crmProductDAOObj = crmProductDAOObj1;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmCaseReportDAO(crmCaseReportDAO caseReportDAOObj1) {
        this.caseReportDAOObj = caseReportDAOObj1;
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

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    
    public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
    public JSONObject getCaseReportJson(List<CrmCase> ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            for (CrmCase obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("casename", obj.getCasename());
                tmpObj.put("subject", obj.getSubject());
                tmpObj.put("createdon",obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("createdo",  obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("casestatus", (obj.getCrmCombodataByCasestatusid() != null ? obj.getCrmCombodataByCasestatusid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("caseowner", obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("priority", (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                jobjTemp.put("header",messageSource.getMessage("crm.case.defaultheader.subject", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.case.defaultheader.subject", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "subject");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "combo");
                jobjTemp.put("dataIndex", "casestatus");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.case.defaultheader.accountname", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.case.defaultheader.accountname", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "accountname");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.report.casereport.caseowner", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.report.casereport.caseowner", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "caseowner");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "combo");
                jobjTemp.put("dataIndex", "priority");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.report.creationdate", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.report.creationdate", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("title", "createdo");
                jobjTemp.put("dataIndex", "createdo");
                jobjTemp.put("align", "center");
                jobjTemp.put("xtype", "datefield");
                jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "subject");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "casestatus");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "caseowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdo");
                jobjTemp.put("dateFormat", "time");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priority");
                jarrRecords.put(jobjTemp);
                commData.put("columns", jarrColumns);

                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return commData;
    }
    public JSONObject getcaseByStatusReportJson(List<CrmCase> ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmCase obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("casename", obj.getCasename());
                tmpObj.put("subject", obj.getSubject());
                tmpObj.put("createdon", obj.getCreatedOn() == null? "":obj.getCreatedOn());
                tmpObj.put("createdo",  obj.getCreatedOn() == null? "":obj.getCreatedOn());
                tmpObj.put("casestatus", (obj.getCrmCombodataByCasestatusid() != null ? obj.getCrmCombodataByCasestatusid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("caseowner", obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("priority", (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader>ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Case");
                for (DefaultHeader obj : ll1) {
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Case",companyid);
                    if(StringUtil.equal(Header.CASESTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "casestatus");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASEACCOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.accountname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.accountname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASEOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.caseowner", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.caseowner", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "caseowner");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASEPRIORITYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "priority");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASECREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("title", "createdo");
                        jobjTemp.put("dataIndex", "createdo");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASESUBJECTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.subject", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.CASESUBJECTHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "subject");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "subject");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "casestatus");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "caseowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdo");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priority");
                jarrRecords.put(jobjTemp);
                commData.put("columns", jarrColumns);

                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return commData;
    }

    public ModelAndView caseByStatusReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("filterCombo", StringUtil.checkForNull(request.getParameter("filterCombo")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.caseByStatusReport(requestParams, usersList);
            jobj = getcaseByStatusReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView caseByStatusExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm",request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.caseByStatusReport(requestParams, usersList);
            jobj = getcaseByStatusReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Case_By_Status,
                    "Case by Status Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }
    public JSONObject getmonthlyCasesReportJson(List<CrmCase> ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmCase obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("casename", obj.getCasename());
                tmpObj.put("subject", obj.getSubject());
                tmpObj.put("createdon",obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("createdo",obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("casestatus", (obj.getCrmCombodataByCasestatusid() != null ? obj.getCrmCombodataByCasestatusid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("caseowner", obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("priority", (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Case");
                for (DefaultHeader obj : ll1) {
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Case",companyid);
                    if(StringUtil.equal(Header.CASESTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "casestatus");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }  else if(StringUtil.equal(Header.CASEOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.caseowner", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.caseowner", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "caseowner");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASECREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("title", "createdo");
                        jobjTemp.put("dataIndex", "createdo");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASESUBJECTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.subject", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.CASESUBJECTHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "subject");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "subject");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "casestatus");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "caseowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdo");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priority");
                jarrRecords.put(jobjTemp);
                commData.put("columns", jarrColumns);

                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return commData;
    }

    public ModelAndView monthlyCasesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("year", StringUtil.checkForNull(request.getParameter("year")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("month", StringUtil.isNullOrEmpty(request.getParameter("month"))?0:Integer.parseInt(request.getParameter("month")));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.monthlyCasesReport(requestParams, usersList);
            jobj = getmonthlyCasesReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView monthlyCasesExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("year", StringUtil.checkForNull(request.getParameter("year")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.monthlyCasesReport(requestParams, usersList);
            jobj = getmonthlyCasesReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Monthly_Cases,
                    "Monthly Case Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }
    public JSONObject getnewlyAddedCasesReportJson(List<CrmCase> ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String qucikSerachFields = request.getParameter("quickSearchFields");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            for (CrmCase obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("casename", obj.getCasename());
                tmpObj.put("subject", obj.getSubject());
                tmpObj.put("createdon",obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("createdo", obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("casestatus", (obj.getCrmCombodataByCasestatusid() != null ? obj.getCrmCombodataByCasestatusid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("caseowner", obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("priority", (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("contactname", (obj.getCrmContact() != null ? (StringUtil.checkForNull(obj.getCrmContact().getFirstname()) + " " + StringUtil.checkForNull(obj.getCrmContact().getLastname())).trim() : ""));
                tmpObj.put("description", obj.getDescription());
                tmpObj.put("type", (obj.getCrmCombodataByCasetypeid() != null ? obj.getCrmCombodataByCasetypeid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Case");
                for (DefaultHeader obj : ll1) {
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Case",companyid);
                    if(StringUtil.equal(Header.CASESTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "casestatus");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASEDESCRIPTIONHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "description");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASESUBJECTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.subject", null, RequestContextUtils.getLocale(request)) :newHeader;
                        if(StringUtil.equal(Header.CASESUBJECTHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "subject");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASECONTACTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "contactname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASEPRIORITYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "priority");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASETYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casetype", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casetype", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     }else if(StringUtil.equal(Header.CASEACCOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.accountname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.accountname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASECREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("title", "createdo");
                        jobjTemp.put("dataIndex", "createdo");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "description");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "contactname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "subject");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "casestatus");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "caseowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdo");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priority");
                jarrRecords.put(jobjTemp);
                commData.put("columns", jarrColumns);

                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return commData;
    }

    public ModelAndView newlyAddedCasesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.newlyAddedCasesReport(requestParams, usersList);
            jobj = getnewlyAddedCasesReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView newlyAddedCasesExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            kmsg = caseReportDAOObj.newlyAddedCasesReport(requestParams, usersList);
            jobj = getnewlyAddedCasesReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Newly_Added_Cases,
                    "Newly Added Case Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }
    public JSONObject getpendingCasesReportJson(List<CrmCase> ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmCase obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("casename", obj.getCasename());
                tmpObj.put("subject", obj.getSubject());
                tmpObj.put("createdon",obj.getCreatedOn() == null? "":obj.getCreatedOn());
                tmpObj.put("createdo",  obj.getCreatedOn() == null? "":obj.getCreatedOn());
                tmpObj.put("casestatus", (obj.getCrmCombodataByCasestatusid() != null ? obj.getCrmCombodataByCasestatusid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("caseowner", obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("priority", (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : "-None-"));
                tmpObj.put("contactname", (obj.getCrmContact() != null ? (StringUtil.checkForNull(obj.getCrmContact().getFirstname()) + " " + StringUtil.checkForNull(obj.getCrmContact().getLastname())).trim() : "-None-"));
                tmpObj.put("description", obj.getDescription());
                tmpObj.put("type", (obj.getCrmCombodataByCasetypeid() != null ? obj.getCrmCombodataByCasetypeid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Case");
                for (DefaultHeader obj : ll1) {
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Case",companyid);
                    if(StringUtil.equal(Header.CASESTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "casestatus");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASEDESCRIPTIONHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "description");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASESUBJECTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.subject", null, RequestContextUtils.getLocale(request)) :newHeader;
                        if(StringUtil.equal(Header.CASESUBJECTHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "subject");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASECONTACTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "contactname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASEPRIORITYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "priority");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASETYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casetype", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casetype", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     }else if(StringUtil.equal(Header.CASEACCOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.accountname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.accountname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASECREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("title", "createdo");
                        jobjTemp.put("dataIndex", "createdo");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        jobjTemp.put("align", "center");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "description");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "contactname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "subject");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "casestatus");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "caseowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdo");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priority");
                jarrRecords.put(jobjTemp);
                commData.put("columns", jarrColumns);

                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return commData;
    }

    public ModelAndView pendingCasesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.pendingCasesReport(requestParams, usersList);
            jobj = getpendingCasesReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView pendingCasesExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to",request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.pendingCasesReport(requestParams, usersList);
            jobj = getpendingCasesReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Pending_Cases,
                    "Pending Cases Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }
    public JSONObject getescalatedCasesReportJson(List<CrmCase> ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmCase obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("casename", obj.getCasename());
                tmpObj.put("subject", obj.getSubject());
                tmpObj.put("createdon",obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("createdo",obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("casestatus", (obj.getCrmCombodataByCasestatusid() != null ? obj.getCrmCombodataByCasestatusid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("caseowner", obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("priority", (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("contactname", (obj.getCrmContact() != null ? (StringUtil.checkForNull(obj.getCrmContact().getFirstname()) + " " + StringUtil.checkForNull(obj.getCrmContact().getLastname())).trim() : ""));
                tmpObj.put("description", obj.getDescription());
                tmpObj.put("type", (obj.getCrmCombodataByCasetypeid() != null ? obj.getCrmCombodataByCasetypeid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Case");
                for (DefaultHeader obj : ll1) {
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Case",companyid);
                    if(StringUtil.equal(Header.CASESTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "casestatus");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASEDESCRIPTIONHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "description");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASESUBJECTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.subject", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.CASESUBJECTHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "subject");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASECONTACTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "contactname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASEPRIORITYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "priority");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASETYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casetype", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casetype", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     }else if(StringUtil.equal(Header.CASEACCOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.accountname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.accountname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASECREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("title", "createdo");
                        jobjTemp.put("dataIndex", "createdo");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "description");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "contactname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "subject");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "casestatus");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "caseowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdo");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priority");
                jarrRecords.put(jobjTemp);
                commData.put("columns", jarrColumns);

                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return commData;
    }

    public ModelAndView escalatedCasesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.escalatedCasesReport(requestParams, usersList);
            jobj = getescalatedCasesReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView escalatedCasesExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.escalatedCasesReport(requestParams, usersList);
            jobj = getescalatedCasesReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Escalated_Cases,
                    "Escalated Cases Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                        request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView casesByStatusPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            for (DefaultMasterItem crmCombodata : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", crmCombodata.getValue());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = caseReportDAOObj.getCasesByStatusChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView casesByStatusBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);

            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            for (DefaultMasterItem crmCombodata : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", crmCombodata.getValue());
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
                kmsg = caseReportDAOObj.getCasesByStatusChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView monthlyCasesPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String year = request.getParameter("year");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("year", year);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            String monthArray[] = {"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            result = "<pie>";
            for (int j = 1; j <= 12; j++) {
                kmsg = caseReportDAOObj.getMonthlyCasesChart(j, requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + monthArray[j] + "\">" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }
            result += "</pie>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView monthlyCasesBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        int temp = 0;
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String year = request.getParameter("year");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("year", year);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            result = "<chart><series>";
            result += "<value xid=\"0\">January</value>";
            result += "<value xid=\"1\">Feburary</value>";
            result += "<value xid=\"2\">March</value>";
            result += "<value xid=\"3\">April</value>";
            result += "<value xid=\"4\">May</value>";
            result += "<value xid=\"5\">June</value>";
            result += "<value xid=\"6\">July</value>";
            result += "<value xid=\"7\">August</value>";
            result += "<value xid=\"8\">September</value>";
            result += "<value xid=\"9\">October</value>";
            result += "<value xid=\"10\">November</value>";
            result += "<value xid=\"11\">December</value>";
            result += "</series><graphs><graph gid=\"0\">";

            for (int k = 1; k <= 12; k++) {
                kmsg = caseReportDAOObj.getMonthlyCasesChart(k, requestParams, usersList);
                temp = k - 1;
                result += "<value xid=\"" + temp + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView newlyAddedCasesPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);

            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            for (DefaultMasterItem crmCombodata : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", crmCombodata.getValue());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = caseReportDAOObj.getNewlyAddedCasesChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView newlyAddedCasesBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);

            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            for (DefaultMasterItem crmCombodata : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", crmCombodata.getValue());
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
                kmsg = caseReportDAOObj.getNewlyAddedCasesChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView pendingCasesPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            for (DefaultMasterItem crmCombodata : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", crmCombodata.getValue());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = caseReportDAOObj.getPendingCasesChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView pendingCasesBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            for (DefaultMasterItem crmCombodata : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", crmCombodata.getValue());
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
                kmsg = caseReportDAOObj.getPendingCasesChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView escalatedCasesPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            for (DefaultMasterItem crmCombodata : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", crmCombodata.getValue());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = caseReportDAOObj.getEscalatedCasesChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView escalatedCasesBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            for (DefaultMasterItem crmCombodata : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", crmCombodata.getValue());
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
                kmsg = caseReportDAOObj.getEscalatedCasesChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }
    public JSONObject getaccountsWithCaseReportJson(List<CrmCase> ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmCase obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("casename", obj.getCasename());
                tmpObj.put("subject", obj.getSubject());
                tmpObj.put("createdon",obj.getCreatedOn() == null? "":obj.getCreatedOn());
                tmpObj.put("createdo", obj.getCreatedOn() == null? "":obj.getCreatedOn());
                tmpObj.put("casestatus", (obj.getCrmCombodataByCasestatusid() != null ? obj.getCrmCombodataByCasestatusid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("type", (obj.getCrmCombodataByCasetypeid() != null ? obj.getCrmCombodataByCasetypeid().getValue() : ""));
                tmpObj.put("priority", (obj.getCrmCombodataByCasepriorityid() != null ? obj.getCrmCombodataByCasepriorityid().getValue() : ""));
                tmpObj.put("description", obj.getDescription());
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, Header.ACCOUNTNAMEHEADER, "Account", companyid);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?"Account Name":newHeader);
                jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?"Account Name":newHeader);
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "accountname");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                List<DefaultHeader> ll2=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Case");
                for (DefaultHeader obj1 : ll2) {
                    String newHeader2 =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj1.getDefaultHeader(),"Case",companyid);
                    if(StringUtil.equal(Header.CASESUBJECTHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.casereport.casesubject", null, RequestContextUtils.getLocale(request)):newHeader2;
                        if(StringUtil.equal(Header.CASESUBJECTHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "subject");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASEPRIORITYHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.casereport.casepriority", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.casereport.casepriority", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "priority");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASETYPEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.casereport.casetype", null, RequestContextUtils.getLocale(request)) :newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.casereport.casetype", null, RequestContextUtils.getLocale(request)) :newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.CASESTATUSHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "casestatus");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.CASEDESCRIPTIONHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)) :newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)) :newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "description");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.CASECREATIONDATEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("title", "createdo");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "createdo");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "subject");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "casestatus");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "caseowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdo");
                jobjTemp.put("dateFormat", "time");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priority");
                jarrRecords.put(jobjTemp);
                commData.put("columns", jarrColumns);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "description");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdo");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return commData;
    }

    //Account reports
    public ModelAndView accountsWithCaseReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm",request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("filterCombo", StringUtil.checkForNull(request.getParameter("filterCombo")));
            boolean heirarchyPermCas = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            boolean heirarchyPermAcc = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPermCas", heirarchyPermCas);
            requestParams.put("heirarchyPermAcc", heirarchyPermAcc);

            kmsg = caseReportDAOObj.accountsWithCaseReport(requestParams, usersList);
            jobj = getaccountsWithCaseReportJson( kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView accountsWithCaseExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.accountsWithCaseReport(requestParams, usersList);
            jobj = getaccountsWithCaseReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Accounts_Contacts,
                    "Account with Contacts Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView accountCasesPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        StringBuilder chart = null;
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            kmsg = caseReportDAOObj.getAccountCasesChart(requestParams, usersList);
            chart = chartServiceObj.getPieChart(kmsg.getEntityList());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", chart.toString());
    }

    public ModelAndView accountCasesBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        StringBuilder chart = null;
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            kmsg = caseReportDAOObj.getAccountCasesChart(requestParams, usersList);
            chart = chartServiceObj.getBarChart(kmsg.getEntityList());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", chart.toString());
    }

    public ModelAndView accountHighPriorityCasesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String companyid  = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("priorityid", request.getParameter("filterCombo"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.accountHighPriorityCasesReport(requestParams, usersList);
          
			Iterator ir = kmsg.getEntityList().iterator();
			ArrayList<CrmAccount> ac_list = new ArrayList();
			ArrayList<String> priority = new ArrayList<String>();
			String pr="";
			while (ir.hasNext()) {
				Object[] obs = (Object[]) ir.next();
				CrmAccount accobj = (CrmAccount) obs[0];
				CrmCase caseobj = (CrmCase) obs[1];
				if (accobj != null)
					ac_list.add(accobj);
				pr=caseobj.getCrmCombodataByCasepriorityid()!=null?caseobj.getCrmCombodataByCasepriorityid().getValue():"";
				priority.add(pr);
			}
            Map<String, List<AccountOwnerInfo>> accowners = getAccountOwnerInfor(ac_list); //crmAccountDAOObj.getAccountOwners(idsList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = getAccountMasterData(companyid);
			jobj = crmAccountHandler.getAccountReportJson(crmAccountDAOObj, ac_list, request, true, kmsg.getRecordTotalCount(), crmManagerDAOObj, crmCommonDAOObj, accowners, defaultMasterMap);
			if (jobj.has("columns")) {
				List<DefaultHeader> l2 = crmManagerCommon.getHeaderName(crmCommonDAOObj, "Case");
				JSONObject jtemp = null;
				for (DefaultHeader ob : l2) {
					String hdr = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, ob.getDefaultHeader(), "Case", sessionHandlerImpl.getCompanyid(request));
					if (StringUtil.equal(Header.CASEPRIORITYHEADER, ob.getDefaultHeader())) {
						jtemp = new JSONObject();
						jtemp.put("header", StringUtil.isNullOrEmpty(hdr) ? messageSource.getMessage("crm.report.casereport.casepriority", null, RequestContextUtils.getLocale(request)) : hdr);
						jtemp.put("tip", StringUtil.isNullOrEmpty(hdr) ? messageSource.getMessage("crm.report.casereport.casepriority", null, RequestContextUtils.getLocale(request)) : hdr);
						jtemp.put("pdfwidth", 60);
						jtemp.put("align", "right");
						jtemp.put("dataIndex", "casepriority");
                        StringUtil.escapeJSONObject(jtemp, "header","tip");
						jobj.getJSONArray("columns").put(jtemp);
						break;
					}
				}
				if (jobj.has("metaData") && jobj.getJSONObject("metaData").has("fields")) {
					jtemp = new JSONObject();
					jtemp.put("name", "casepriority");
					jobj.getJSONObject("metaData").getJSONArray("fields").put(jtemp);
				}
				int k=0;
				for (String item : priority){
					if (jobj.has("coldata")) {
						jobj.getJSONArray("coldata").getJSONObject(k).put("casepriority", item);
						k++;
					}
				}
					
			}

		} catch (Exception e) {
			LOGGER.warn(e.getMessage(), e);
		}
		return new ModelAndView("jsonView", "model", jobj.toString());
	}

    public Map<String, List<AccountOwnerInfo>> getAccountOwnerInfor(List<CrmAccount> accountList) {
            List<String> idsList = new ArrayList<String>();
            for (CrmAccount obj : accountList) {
                idsList.add(obj.getAccountid());
            }
            // get owners
            return crmAccountDAOObj.getAccountOwners(idsList);
    }

    public HashMap<String, DefaultMasterItem> getAccountMasterData(String companyid) throws ServiceException {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            List filter_names = new ArrayList();
            List filter_params = new ArrayList();
            //Get ids of crmCombomaster table
            String masterIds = "'"+com.krawler.crm.utils.Constants.ACCOUNT_TYPEID+"',";
            masterIds += "'"+com.krawler.crm.utils.Constants.LEAD_INDUSTRYID+"'";
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(com.krawler.crm.utils.Constants.filter_names, filter_names);
            requestParams.put(com.krawler.crm.utils.Constants.filter_params, filter_params);
            return crmCommonDAOObj.getDefaultMasterItemsMap(requestParams);
    }
    public ModelAndView accountHighPriorityCasesExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("priorityid", request.getParameter("filterCombo"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.accountHighPriorityCasesReport(requestParams, usersList);
			
            Iterator ir = kmsg.getEntityList().iterator();
			ArrayList ac_list = new ArrayList();
			ArrayList<String> priority = new ArrayList<String>();
			String pr="";
			while (ir.hasNext()) {
				Object[] obs = (Object[]) ir.next();
				CrmAccount accobj = (CrmAccount) obs[0];
				CrmCase caseobj = (CrmCase) obs[1];
				if (accobj != null)
					ac_list.add(accobj);
				pr=caseobj.getCrmCombodataByCasepriorityid()!=null?caseobj.getCrmCombodataByCasepriorityid().getValue():"";
				priority.add(pr);
			}
            Map<String, List<AccountOwnerInfo>> accowners = getAccountOwnerInfor(ac_list); //crmAccountDAOObj.getAccountOwners(idsList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = getAccountMasterData(companyid);
			jobj = crmAccountHandler.getAccountReportJson(crmAccountDAOObj, ac_list, request, true, kmsg.getRecordTotalCount(), crmManagerDAOObj, crmCommonDAOObj, accowners, defaultMasterMap);
			if (jobj.has("columns")) {
				List<DefaultHeader> l2 = crmManagerCommon.getHeaderName(crmCommonDAOObj, "Case");
				JSONObject jtemp = null;
				for (DefaultHeader ob : l2) {
					String hdr = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, ob.getDefaultHeader(), "Case", sessionHandlerImpl.getCompanyid(request));
					if (StringUtil.equal(Header.CASEPRIORITYHEADER, ob.getDefaultHeader())) {
						jtemp = new JSONObject();
						jtemp.put("header", StringUtil.isNullOrEmpty(hdr) ? messageSource.getMessage("crm.report.casereport.casepriority", null, RequestContextUtils.getLocale(request)) : hdr);
						jtemp.put("tip", StringUtil.isNullOrEmpty(hdr) ? messageSource.getMessage("crm.report.casereport.casepriority", null, RequestContextUtils.getLocale(request)) : hdr);
						jtemp.put("pdfwidth", 60);
						jtemp.put("align", "right");
						jtemp.put("dataIndex", "casepriority");
						jobj.getJSONArray("columns").put(jtemp);
						break;
					}
				}
				if (jobj.has("metaData") && jobj.getJSONObject("metaData").has("fields")) {
					jtemp = new JSONObject();
					jtemp.put("name", "casepriority");
					jobj.getJSONObject("metaData").getJSONArray("fields").put(jtemp);
				}
				int k=0;
				for (String item : priority){
					if (jobj.has("coldata")) {
						jobj.getJSONArray("coldata").getJSONObject(k).put("casepriority", item);
						k++;
					}
				}
					
			}

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Account_High_Pri_Cases,
                    "Accounts with High Priority Cases Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView accountHighPriorityPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        StringBuilder chart = new StringBuilder("<pie>");
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("groupby", true);
            kmsg = caseReportDAOObj.getAccountHighPriorityChart(null, requestParams, usersList);
            chart = chartServiceObj.getPieChart(kmsg.getEntityList());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", chart.toString());
    }

    public ModelAndView accountHighPriorityBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        StringBuilder chart = new StringBuilder("<chart>");
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("groupby", true);
            kmsg = caseReportDAOObj.getAccountHighPriorityChart(null, requestParams, usersList);
            chart = chartServiceObj.getBarChart(kmsg.getEntityList());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", chart.toString());
    }

    //Contact report
    public JSONObject contactsWithCasesReportJson(List<CrmCase> ll, HttpServletRequest request, boolean export, int totalSize, Map<String, List<CrmProduct>> products, Map<String, CrmAccount> accounts, Map<String, User> owners, Map<String, CrmContact> contacts,HashMap<String, DefaultMasterItem> defaultMasterMap) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String qucikSerachFields = request.getParameter("quickSearchFields");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            for(CrmCase obj : ll ) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("caseid", obj.getCaseid());
                User owner = owners.get(obj.getCaseid());
                String ownername =owner.getFirstName() + " " + owner.getLastName();
                tmpObj.put("caseownerid", owner.getUserID());
                tmpObj.put("owner", ownername);
                tmpObj.put("casename", obj.getCasename());
                tmpObj.put("subject", obj.getSubject());
                CrmAccount account = accounts.get(obj.getCaseid());
                tmpObj.put("accountnameid", crmManagerCommon.moduleObjNull(account, "Accountid"));
                tmpObj.put("accountname", (account != null ? account.getAccountname() : ""));
                CrmContact contact = contacts.get(obj.getCaseid());
                tmpObj.put("contactnameid", crmManagerCommon.moduleObjNull(contact, "Contactid"));
                tmpObj.put("contactname", (contact != null ? (StringUtil.checkForNull(contact.getFirstname()) + " " + StringUtil.checkForNull(contact.getLastname())).trim() : ""));
                String[] productInfo=crmCaseHandler.getCaseProducts(products.get(obj.getCaseid()));
                tmpObj.put("productnameid", productInfo[0]);
                tmpObj.put("productname", productInfo[1]);
                tmpObj.put("casetypeid", StringUtil.hNull(obj.getCasetypeID()));
                if(obj.getCasetypeID() != null && defaultMasterMap.containsKey(obj.getCasetypeID())) {
                    tmpObj.put("type", defaultMasterMap.get(obj.getCasetypeID()).getValue());
                }
                tmpObj.put("casestatusid", StringUtil.hNull(obj.getCasestatusID()));
                if(obj.getCasestatusID() != null && defaultMasterMap.containsKey(obj.getCasestatusID())) {
                    tmpObj.put("status", defaultMasterMap.get(obj.getCasestatusID()).getValue());
                }
                tmpObj.put("casepriorityid", StringUtil.hNull(obj.getCasepriorityID()));
                if(obj.getCasepriorityID() != null && defaultMasterMap.containsKey(obj.getCasepriorityID())) {
                    tmpObj.put("priority", defaultMasterMap.get(obj.getCasepriorityID()).getValue());
                }
                tmpObj.put("createdon",obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("createdo", obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("updatedon", obj.getUpdatedOn() == null? "": obj.getUpdatedOn());
                tmpObj.put("description", obj.getDescription());
                tmpObj.put("validflag", obj.getValidflag());

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Case");
                for (DefaultHeader obj : ll1) {
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Case",companyid);
                    if(StringUtil.equal(Header.CASECONTACTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "contactname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CASESUBJECTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)?"Case Subject":newHeader;
                        if(StringUtil.equal(Header.CASESUBJECTHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?"Case Subject":newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "subject");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASEPRIORITYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casepriority", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casepriority", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "priority");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASETYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casetype", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casetype", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASESTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.casereport.casestatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "status");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASEDESCRIPTIONHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "description");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CASECREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.case.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdo");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "createdo");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "subject");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priority");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "contactname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "status");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "description");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdo");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
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
            LOGGER.warn(e.getMessage(),e);
        }
        return commData;
    }

    public JSONObject contactsHighPriorityCasesReportJson( List<CrmContact> ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            for (CrmContact obj : ll) {

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("contactid", obj.getContactid());
                tmpObj.put("contactowner", StringUtil.checkForNull(obj.getFirstname()));
                String[] ownerInfo=crmContactHandler.getAllContactOwners(crmContactDAOObj, obj.getContactid());
                tmpObj.put("owner", ownerInfo[4]);
                tmpObj.put("exportmultiowners", ownerInfo[5]);
                tmpObj.put("firstname", StringUtil.checkForNull(obj.getFirstname()));
                tmpObj.put("accname", obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : "");
                tmpObj.put("accindustry", (obj.getCrmAccount() != null && obj.getCrmAccount().getCrmCombodataByIndustryid() != null) ? obj.getCrmAccount().getCrmCombodataByIndustryid().getValue() : "");
                tmpObj.put("acctype", (obj.getCrmAccount() != null && obj.getCrmAccount().getCrmCombodataByAccounttypeid() != null) ? obj.getCrmAccount().getCrmCombodataByAccounttypeid().getValue() : "");
                tmpObj.put("lastname", obj.getLastname());
                tmpObj.put("contactname", (StringUtil.checkForNull(obj.getFirstname()) + " " + StringUtil.checkForNull(obj.getLastname())).trim());
                tmpObj.put("phoneno", obj.getPhoneno());
                tmpObj.put("mobileno", obj.getMobileno() != null ? obj.getMobileno() : "");
                tmpObj.put("email", obj.getEmail());
                tmpObj.put("street", obj.getMailstreet());
                tmpObj.put("description", obj.getDescription() != null ? obj.getDescription() : "");
                tmpObj.put("createdon", obj.getCreatedOn()!=null? obj.getCreatedOn():new Date().getTime());
                tmpObj.put("createdo", obj.getCreatedOn()!=null? obj.getCreatedOn():new Date().getTime());
                tmpObj.put("validflag", obj.getValidflag());
                tmpObj.put("leadsourceid", crmManagerCommon.comboNull(obj.getCrmCombodataByLeadsourceid()));
                tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                tmpObj.put("titleid", "");
                tmpObj.put("title", obj.getTitle() != null ? obj.getTitle() : "");

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Contact");
                for (DefaultHeader obj : ll1) {
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Contact",companyid);
                    if(StringUtil.equal(Header.CONTACTOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.contactreport.contactowner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.contactreport.contactowner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "owner");
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("title", "exportmultiowners");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CONTACTFIRSTNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.fname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.fname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "firstname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CONTACTLASTNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.lname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.lname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "lastname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CONTACTTITLEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.desig", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.desig", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "title");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CONTACTLEADSOURCEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "leadsource");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CONTACTPHONEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.phone", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.phone", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "phoneno");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CONTACTMOBILEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.mob", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.mob", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "mobileno");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.CONTACTEMAILHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.email", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.email", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "email");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }else if(StringUtil.equal(Header.CONTACTADDRESSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.address", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.address", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "street");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   } else if(StringUtil.equal(Header.CONTACTCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.contact.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdo");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "createdo");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "owner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsource");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "firstname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "lastname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "title");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "email");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "phoneno");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "street");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdo");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
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
            LOGGER.warn(e.getMessage(),e);
        }
        return commData;
    }

    public ModelAndView contactsWithCasesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", companyid);
            boolean heirarchyPermCas = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPermCas", heirarchyPermCas);
            boolean heirarchyPermCon = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPermCon", heirarchyPermCon);

            kmsg = caseReportDAOObj.contactsWithCasesReport(requestParams, usersList);
            List<CrmCase> lst = kmsg.getEntityList();
            List<String> idsList = new ArrayList<String>();
            for (CrmCase obj : lst) {
                idsList.add(obj.getCaseid());
            }
            Map<String, List<CrmProduct>> products = crmCaseDAOObj.getCaseProducts(idsList);
            Map<String, CrmAccount> accounts = crmCaseDAOObj.getCaseAccounts(idsList);
            Map<String, User> owners = crmCaseDAOObj.getCaseOwners(idsList);
            Map<String, CrmContact> contacts = crmCaseDAOObj.getCaseContacts(idsList);

            requestParams.clear();
            List filter_names = new ArrayList();
            List filter_params = new ArrayList();
            //Get ids of crmCombomaster table
            String masterIds = "'"+Constants.CASE_PRIORITYID+"',";
            masterIds += "'"+Constants.CASE_STATUSID+"',";
            masterIds += "'"+Constants.CASE_TYPEID+"'";
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(Constants.filter_names, filter_names);
            requestParams.put(Constants.filter_params, filter_params);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmCommonDAOObj.getDefaultMasterItemsMap(requestParams);

            jobj = contactsWithCasesReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), products, accounts, owners, contacts,defaultMasterMap);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView contactsWithCasesExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.contactsWithCasesReport(requestParams, usersList);
            List<CrmCase> lst = kmsg.getEntityList();
            List<String> idsList = new ArrayList<String>();
            for (CrmCase obj : lst) {
                idsList.add(obj.getCaseid());
            }
            Map<String, List<CrmProduct>> products = crmCaseDAOObj.getCaseProducts(idsList);
            Map<String, CrmAccount> accounts = crmCaseDAOObj.getCaseAccounts(idsList);
            Map<String, User> owners = crmCaseDAOObj.getCaseOwners(idsList);
            Map<String, CrmContact> contacts = crmCaseDAOObj.getCaseContacts(idsList);

            requestParams.clear();
            List filter_names = new ArrayList();
            List filter_params = new ArrayList();
            //Get ids of crmCombomaster table
            String masterIds = "'"+Constants.CASE_PRIORITYID+"',";
            masterIds += "'"+Constants.CASE_STATUSID+"',";
            masterIds += "'"+Constants.CASE_TYPEID+"'";
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(Constants.filter_names, filter_names);
            requestParams.put(Constants.filter_params, filter_params);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmCommonDAOObj.getDefaultMasterItemsMap(requestParams);

            jobj = contactsWithCasesReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), products, accounts, owners, contacts,defaultMasterMap);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Contact_Cases,
                    "Contacts with Cases Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView contactsHighPriorityCasesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.contactsHighPriorityCasesReport(requestParams, usersList);
            jobj = contactsHighPriorityCasesReportJson( kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView contactsHighPriorityCasesExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = caseReportDAOObj.contactsHighPriorityCasesReport(requestParams, usersList);
            jobj = contactsHighPriorityCasesReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Contact_High_Pri_Cases,
                    "Contacts with High Priority Cases Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView contactHighPriorityPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        List<DefaultMasterItem> ll = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            for (DefaultMasterItem crmCombodata : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", crmCombodata.getValue());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = caseReportDAOObj.getContactHighPriorityChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }
            result += "</pie>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView contactHighPriorityBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        List<DefaultMasterItem> ll = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                filter_names.add("d.validflag");
                filter_params.add(1);
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            for (DefaultMasterItem crmCombodata : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", crmCombodata.getValue());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<chart><series>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                if (!jarr.getJSONObject(j).get("name").toString().equals("None")) {
                    result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
                }
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < Integer.parseInt(jobj.get("totalCount").toString()); k++) {
                if (!jarr.getJSONObject(k).get("name").toString().equals("None")) {
                    kmsg = caseReportDAOObj.getContactHighPriorityChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                    result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
                }
            }
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView contactCasePieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.validflag");
            filter_params.add(1);
            filter_names.add("c.isarchive");
            filter_params.add(false);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            if(!heirarchyPerm){
                filter_names.add("INco.usersByUserid.userID");filter_params.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);

            kmsg = crmContactDAOObj.getAllContacts(requestParams);
            dl = kmsg.getEntityList().size();
            List<CrmContact> contactLL = kmsg.getEntityList();
            JSONObject tmpOb = crmManagerCommon.insertNone();
            jarr1.put(tmpOb);
            for (CrmContact obj : contactLL) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getContactid());
                tmpObj.put("name", (StringUtil.checkForNull(obj.getFirstname()) +" "+ StringUtil.checkForNull(obj.getLastname())).trim());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j <= Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                if (!jarr.getJSONObject(j).get("name").toString().equals("None")) {
                    kmsg = caseReportDAOObj.getContactCaseChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                    if (kmsg.getRecordTotalCount() > 0) {
                        result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                    }
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView contactCaseBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        String result2 = "";
        KwlReturnObject kmsg = null;
        int k = 0;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.validflag");
            filter_params.add(1);
            filter_names.add("c.isarchive");
            filter_params.add(false);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            if(!heirarchyPerm){
                filter_names.add("INco.usersByUserid.userID");filter_params.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);

            kmsg = crmContactDAOObj.getAllContacts(requestParams);
            dl = kmsg.getEntityList().size();
            List<CrmContact> contactLL = kmsg.getEntityList();
            JSONObject tmpOb = crmManagerCommon.insertNone();
            jarr1.put(tmpOb);
            for (CrmContact obj : contactLL) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getContactid());
                tmpObj.put("name", (StringUtil.checkForNull(obj.getFirstname()) +" "+ StringUtil.checkForNull(obj.getLastname())).trim());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j <= max; j++) {
                kmsg = caseReportDAOObj.getContactCaseChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<value xid=\"" + k + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
                    result2 += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
                    k++;
                }
            }

            result += "</series><graphs><graph gid=\"0\">";
            result2 += "</graph></graphs></chart>";
            result += result2;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    //Product Report
        /**
     *
     * @param request
     * @param response
     * @return org.springframework.web.servlet.ModelAndView : JSONObject
     * @throws javax.servlet.ServletException
     */
    public ModelAndView productCasesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm",request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("priorityId", request.getParameter("filterCombo"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            boolean heirarchyPermPro = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            requestParams.put("heirarchyPermPro", heirarchyPermPro);
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            KwlReturnObject kmsg = caseReportDAOObj.productCasesReport(requestParams, usersList);
            if (StringUtil.isNullOrEmpty(export)) {
                jobj = getProductReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), crmManagerDAOObj,crmCommonDAOObj,dateFormat);
            } else {
                jobj = getProductReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), crmManagerDAOObj,crmCommonDAOObj,dateFormat);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    /**
     *
     * @param request
     * @param response
     * @return org.springframework.web.servlet.ModelAndView : JSONObject
     * @throws javax.servlet.ServletException
     */
    public ModelAndView productCaseExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("priorityId", request.getParameter("filterCombo"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            kmsg = caseReportDAOObj.productCasesReport(requestParams, usersList);
            if (StringUtil.isNullOrEmpty(export)) {
                jobj = getProductReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), crmManagerDAOObj,crmCommonDAOObj,dateFormat);
            } else {
                jobj = getProductReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), crmManagerDAOObj,crmCommonDAOObj,dateFormat);
            }

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Product_High_Pri_Cases,
                    "Product with High Priority Cases Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView productCasesPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.validflag");filter_values.add(1);
            filter_names.add("c.isarchive");filter_values.add(false);

            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.productname");
            order_type.add("asc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            if(!heirarchyPerm){
                filter_names.add("INc.usersByUserid.userID");filter_values.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("priorityId", request.getParameter("filterCombo"));
            kmsg = crmProductDAOObj.getAllProducts(requestParams);
            dl = kmsg.getRecordTotalCount();
            List<CrmProduct> prodLL = kmsg.getEntityList();
            for (CrmProduct obj : prodLL) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getProductid());
                tmpObj.put("name", obj.getProductname());
                tmpObj.put("unitprice", obj.getUnitprice());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
            heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            requestParams1.put("companyid", companyid);
            requestParams1.put("heirarchyPerm", heirarchyPerm);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = caseReportDAOObj.getProductHighPriorityChart(jarr.getJSONObject(j).get("id").toString(), requestParams1, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView productCasesBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        String result2 = "";
        KwlReturnObject kmsg = null;
        int k = 0;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.validflag");filter_values.add(1);
            filter_names.add("c.isarchive");filter_values.add(false);

            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.productname");
            order_type.add("asc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            if(!heirarchyPerm){
                filter_names.add("INc.usersByUserid.userID");filter_values.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("companyid", companyid);

            kmsg = crmProductDAOObj.getAllProducts(requestParams);
            dl = kmsg.getRecordTotalCount();
            List<CrmProduct> prodLL = kmsg.getEntityList();
            for (CrmProduct obj : prodLL) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getProductid());
                tmpObj.put("name", obj.getProductname());
                tmpObj.put("unitprice", obj.getUnitprice());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<chart><series>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = caseReportDAOObj.getProductHighPriorityChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<value xid=\"" + k + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
                    result2 += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
                    k++;
                }
            }
            result += "</series><graphs><graph gid=\"0\">";
            result2 += "</graph></graphs></chart>";
            result += result2;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }
     /**
     *
     * @param ll
     * @param request
     * @param export
     * @param totalSize
     * @param crmManagerDAOObj
     * @return JSONObject
     */
    public JSONObject getProductReportJson(List ll, HttpServletRequest request, boolean export, int totalSize, crmManagerDAO crmManagerDAOObj,crmCommonDAO crmCommonDAOObj, DateFormat dateFormat) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String currencySymbol=crmManagerDAOObj.currencySymbol(currencyid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            while (ite.hasNext()) {
                Object [] cp = (Object[]) ite.next();
                CrmProduct crmProduct = (CrmProduct) cp[0];
                CrmCase caseobj = (CrmCase) cp[1];
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("productid", crmProduct.getProductid());
                tmpObj.put("name", crmProduct.getProductname());
                tmpObj.put("ownerid", crmProduct.getUsersByUserid().getUserID());
                tmpObj.put("owner", crmProduct.getUsersByUserid().getFirstName() + " " + crmProduct.getUsersByUserid().getLastName());
                tmpObj.put("code", crmProduct.getCode());
                tmpObj.put("commisionrate", crmProduct.getCommisionrate());
                tmpObj.put("createdon",crmProduct.getCreatedOn() == null? "": crmProduct.getCreatedOn());
                tmpObj.put("updatedon", crmProduct.getUpdatedOn() == null? "":crmProduct.getUpdatedOn());
                tmpObj.put("currencyid", crmProduct.getCurrencyid());
                tmpObj.put("description", crmProduct.getDescription());
                tmpObj.put("productname", crmProduct.getProductname());
                tmpObj.put("quantityindemand", crmProduct.getQuantityindemand());
                tmpObj.put("quantitylevel", crmProduct.getQuantitylevel());
                tmpObj.put("stockquantity", crmProduct.getStockquantity());
                tmpObj.put("taxincurred", crmProduct.getTaxincurred());
                tmpObj.put("threshold", crmProduct.getThreshold());
                tmpObj.put("unitprice", crmProduct.getUnitprice() != null && !crmProduct.getUnitprice().equals("") ? crmManagerDAOObj.currencyRender(crmProduct.getUnitprice(), currencyid) : "");
                tmpObj.put("categoryid", crmManagerCommon.comboNull(crmProduct.getCrmCombodataByCategoryid()));
                tmpObj.put("category", (crmProduct.getCrmCombodataByCategoryid() != null ? crmProduct.getCrmCombodataByCategoryid().getValue() : ""));
                tmpObj.put("priority", (caseobj.getCrmCombodataByCasepriorityid() != null ? caseobj.getCrmCombodataByCasepriorityid().getValue() : ""));
                tmpObj.put("vendornameid", crmProduct.getVendornamee());
                tmpObj.put("validflag", crmProduct.getValidflag());

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll2=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Product");
                for (DefaultHeader obj1 : ll2) {
                    String newHeader2 =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj1.getDefaultHeader(),"Product",companyid);
                    if(StringUtil.equal(Header.PRODUCTNAMEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.product.defaultheader.productname", null, RequestContextUtils.getLocale(request)):newHeader2;
                        if(StringUtil.equal(Header.PRODUCTNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "name");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }if(StringUtil.equal(Header.PRODUCTOWNERHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.productreport.productowner", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.productreport.productowner", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "owner");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.PRODUCTUNITPRICEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.product.defaultheader.unitprice", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader2+"("+currencySymbol+")");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.product.defaultheader.unitprice", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader2+"("+currencySymbol+")");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "unitprice");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.PRODUCTCATEGORYHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.product.defaultheader.category", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.product.defaultheader.category", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "category");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.PRODUCTVENDORNAMEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.product.defaultheader.vendorname", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.product.defaultheader.vendorname", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "vendornameid");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.PRODUCTCREATIONDATEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.product.defaultheader.productcreatedon", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.product.defaultheader.productcreatedon", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                   
                }

                List<DefaultHeader> ll3=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Case");
                   for (DefaultHeader obj2 : ll3) {
                        String newHeader3 =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj2.getDefaultHeader(),"Case",companyid);
                        if(StringUtil.equal(Header.CASEPRIORITYHEADER, obj2.getDefaultHeader())) {
                            jobjTemp = new JSONObject();
                            jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader3)? messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)):newHeader3);
                            jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader3)? messageSource.getMessage("crm.case.defaultheader.priority", null, RequestContextUtils.getLocale(request)):newHeader3);
                            jobjTemp.put("pdfwidth", 60);
                            jobjTemp.put("xtype","combo");
                            jobjTemp.put("dataIndex", "priority");
                            StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                            jarrColumns.put(jobjTemp);
                        }

                }

                jobjTemp = new JSONObject();
                jobjTemp.put("name", "owner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priority");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "name");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "unitprice");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "category");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "vendornameid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdon");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
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
            LOGGER.warn(e.getMessage(),e);
        }
        return commData;
    }
}
