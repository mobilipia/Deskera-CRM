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
package com.krawler.spring.crm.accountModule;

import com.krawler.crm.utils.AuditAction;
import com.krawler.common.admin.User;
import com.krawler.common.util.Header;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.crm.account.dm.AccountOwnerInfo;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.leadModule.crmLeadHandler;
import com.krawler.spring.crm.userModule.crmUserDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class crmAccountReportController extends MultiActionController implements MessageSourceAware {

    private crmAccountReportDAO accountReportDAOObj;
    private crmUserDAO crmUserDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private crmCommonDAO crmCommonDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private MessageSource messageSource;
    
    public crmAccountReportDAO getcrmAccountReportDAO(){
        return accountReportDAOObj;
    }
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setcrmAccountReportDAO(crmAccountReportDAO accountReportDAOObj1) {
        this.accountReportDAOObj = accountReportDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
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

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImpl1) {
        this.sessionHandlerImplObj = sessionHandlerImpl1;
    }
    
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
    public JSONObject getkeyAccountsJson(List<CrmAccount> ll, HttpServletRequest request, boolean export, int totalSize, Map<String, List<AccountOwnerInfo>> owners) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String currencySymbol=crmManagerDAOObj.currencySymbol(currencyid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmAccount aCrmAccount : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("accountid", aCrmAccount.getAccountid());
                tmpObj.put("accountname", aCrmAccount.getAccountname());
                String[] ownerInfo = crmAccountHandler.getAllAccOwners(owners.get(aCrmAccount.getAccountid()));
                tmpObj.put("accountowner", ownerInfo[4]);
                tmpObj.put("exportmultiowners", ownerInfo[5]);
                tmpObj.put("revenue", !StringUtil.isNullOrEmpty(aCrmAccount.getRevenue()) ? crmManagerDAOObj.currencyRender(aCrmAccount.getRevenue(), currencyid) : "");
                tmpObj.put("createdon", aCrmAccount.getCreatedOn());
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Account");
                for (DefaultHeader obj : ll1) {
                    String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, obj.getDefaultHeader(), "Account", companyid);
                    if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)?"Account Name":newHeader;
                        if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?messageSource.getMessage("crm.account.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountowner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountowner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "accountowner");
                        jobjTemp.put("xtype","textfield");
                        jobjTemp.put("title", "exportmultiowners");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTREVENUEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.revenue", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.revenue", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "right");
                        jobjTemp.put("xtype","numerfield");
                        jobjTemp.put("dataIndex", "revenue");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "revenue");
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
            logger.warn("Exception in crmAccountReportController.getkeyAccountsJson", e);
        }
        return commData;
    }

    public ModelAndView keyAccountsReport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.keyAccountsReport(requestParams, usersList);
            List<CrmAccount> accountList = kmsg.getEntityList();
            Map<String, List<AccountOwnerInfo>> owners = getAccountOwnerInfor(accountList);
            jobj = getkeyAccountsJson(accountList, request, true, kmsg.getRecordTotalCount(), owners);
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.keyAccountsReport", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView keyAccountsExport(HttpServletRequest request, HttpServletResponse response)
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
            requestParams.put("to",request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.keyAccountsReport(requestParams, usersList);
            List<CrmAccount> accountList = kmsg.getEntityList();
            Map<String, List<AccountOwnerInfo>> owners = getAccountOwnerInfor(accountList);
            jobj = getkeyAccountsJson(accountList, request, false, kmsg.getRecordTotalCount(),owners);
            String fileType = request.getParameter("filetype");

            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Key_Account,
                    "Key Accounts Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.keyAccountsExport", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }
    public JSONObject getmonthlyAccountsJson(List<CrmAccount> ll, HttpServletRequest request, boolean export, int totalSize, Map<String, List<AccountOwnerInfo>> owners) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmAccount aCrmAccount : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("accountid", aCrmAccount.getAccountid());
                tmpObj.put("accountname", aCrmAccount.getAccountname());
                String[] ownerInfo = crmAccountHandler.getAllAccOwners(owners.get(aCrmAccount.getAccountid()));
                tmpObj.put("accountowner", ownerInfo[4]);
                tmpObj.put("exportmultiowners", ownerInfo[5]);
                tmpObj.put("createdon", aCrmAccount.getCreatedOn()!=null?aCrmAccount.getCreatedOn():"");
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Account");
                for (DefaultHeader obj : ll1) {
                    String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, obj.getDefaultHeader(), "Account", companyid);
                    if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)?messageSource.getMessage("crm.account.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountowner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountowner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "accountowner");
                        jobjTemp.put("xtype","textfield");
                        jobjTemp.put("title", "exportmultiowners");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
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
        	logger.warn("Exception in crmAccountReportController.getmonthlyAccountsJson", e);
        }
        return commData;
    }

    public ModelAndView monthlyAccountsReport(HttpServletRequest request, HttpServletResponse response)
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
            requestParams.put("to",request.getParameter("to"));
            requestParams.put("year", StringUtil.checkForNull(request.getParameter("year")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("month", StringUtil.isNullOrEmpty(request.getParameter("month"))?0:Integer.parseInt(request.getParameter("month")));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.monthlyAccountsReport(requestParams, usersList);
            List<CrmAccount> accountList = kmsg.getEntityList();
            Map<String, List<AccountOwnerInfo>> owners = getAccountOwnerInfor(accountList);
            jobj = getmonthlyAccountsJson(accountList, request, true, kmsg.getRecordTotalCount(), owners);
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.monthlyAccountsReport", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView monthlyAccountsExport(HttpServletRequest request, HttpServletResponse response)
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
            requestParams.put("year", StringUtil.checkForNull(request.getParameter("year")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.monthlyAccountsReport(requestParams, usersList);
            List<CrmAccount> accountList = kmsg.getEntityList();
            Map<String, List<AccountOwnerInfo>> owners = getAccountOwnerInfor(accountList);
            jobj = getmonthlyAccountsJson(accountList, request, false, kmsg.getRecordTotalCount(), owners);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Monthly_Accounts,
                    "Monthly Accounts Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.monthlyAccountsExport", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }
    public JSONObject getaccountOwnersJson(List<CrmAccount> ll, HttpServletRequest request, boolean export, int totalSize, Map<String, List<AccountOwnerInfo>> owners) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmAccount aCrmAccount : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("accountid", aCrmAccount.getAccountid());
                tmpObj.put("accountname", aCrmAccount.getAccountname());
                String[] ownerInfo = crmAccountHandler.getAllAccOwners(owners.get(aCrmAccount.getAccountid()));
                tmpObj.put("accountowner", ownerInfo[5]);
                tmpObj.put("exportmultiowners", ownerInfo[5]);
                tmpObj.put("createdon", aCrmAccount.getCreatedOn()!=null?aCrmAccount.getCreatedOn():"");
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Account");
                for (DefaultHeader obj : ll1) {
                    String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, obj.getDefaultHeader(), "Account", companyid);
                    if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","textfield");
                        jobjTemp.put("dataIndex", "accountname");

                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");

                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountowner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountowner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","textfield");
                        jobjTemp.put("dataIndex", "accountowner");
                        jobjTemp.put("title", "exportmultiowners");

                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");

                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");

                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");

                        jarrColumns.put(jobjTemp);
                   }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
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
        	logger.warn("Exception in crmAccountReportController.getaccountOwnersJson", e);
        }
        return commData;
    }

    public ModelAndView accountOwnersReport(HttpServletRequest request, HttpServletResponse response)
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
            requestParams.put("to",request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.accountOwnersReport(requestParams, usersList);
            List<CrmAccount> accountList = kmsg.getEntityList();
            Map<String, List<AccountOwnerInfo>> owners = getAccountOwnerInfor(accountList);
            jobj = getaccountOwnersJson(accountList, request, true, kmsg.getRecordTotalCount(), owners);
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.accountOwnersReport", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView accountOwnersExport(HttpServletRequest request, HttpServletResponse response)
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
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.accountOwnersReport(requestParams, usersList);
            List<CrmAccount> accountList = kmsg.getEntityList();
            Map<String, List<AccountOwnerInfo>> owners = getAccountOwnerInfor(accountList);
            jobj = getaccountOwnersJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), owners);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Accounts_Owners,
                    "Account Owners Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.accountOwnersExport", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public Map<String, List<AccountOwnerInfo>> getAccountOwnerInfor(List<CrmAccount> accountList) {
            List<String> idsList = new ArrayList<String>();
            for (CrmAccount obj : accountList) {
                idsList.add(obj.getAccountid());
            }
            // get owners
            return crmAccountDAOObj.getAccountOwners(idsList);
    }
    public JSONObject getindustryAccountTypeJson(List<CrmAccount> ll, HttpServletRequest request, boolean export, int totalSize, Map<String, List<AccountOwnerInfo>> owners) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmAccountHandler.getAccountDefaultMasterItemsMap(companyid, crmCommonDAOObj);
            
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmAccount aCrmAccount : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("accountid", aCrmAccount.getAccountid());
                tmpObj.put("accountname", aCrmAccount.getAccountname());
                String[] ownerInfo = crmAccountHandler.getAllAccOwners(owners.get(aCrmAccount.getAccountid()));
                tmpObj.put("accountowner", ownerInfo[4]);
                tmpObj.put("exportmultiowners", ownerInfo[5]);
                tmpObj.put("createdon", aCrmAccount.getCreatedOn()!=null?aCrmAccount.getCreatedOn():"");
                if(aCrmAccount.getIndustryID() != null && defaultMasterMap.containsKey(aCrmAccount.getIndustryID())) {
                    tmpObj.put("industry", defaultMasterMap.get(aCrmAccount.getIndustryID()).getValue());
                }
                if(aCrmAccount.getAccounttypeID() != null && defaultMasterMap.containsKey(aCrmAccount.getAccounttypeID())) {
                    tmpObj.put("type", defaultMasterMap.get(aCrmAccount.getAccounttypeID()).getValue());
                }
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Account");
                for (DefaultHeader obj : ll1) {
                    String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, obj.getDefaultHeader(), "Account", companyid);
                    if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)?messageSource.getMessage("crm.account.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountowner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountowner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","textfield");
                        jobjTemp.put("dataIndex", "accountowner");
                        jobjTemp.put("title", "exportmultiowners");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.industryaccounttype.acctype", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.industryaccounttype.acctype", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   } else if(StringUtil.equal(Header.ACCOUNTINDUSTRYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.industry", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.industry", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","combo");
                        jobjTemp.put("dataIndex", "industry");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.ACCOUNTCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "industry");
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
        	logger.warn("Exception in crmAccountReportController.getindustryAccountTypeJson", e);
        }
        return commData;
    }
    public ModelAndView industryAccountTypeReport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.industryAccountTypeReport(requestParams, usersList);
            List<CrmAccount> accountList = kmsg.getEntityList();
            Map<String, List<AccountOwnerInfo>> owners = getAccountOwnerInfor(accountList);
            jobj = getindustryAccountTypeJson(accountList, request, true, kmsg.getRecordTotalCount(), owners);
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.industryAccountTypeReport", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView industryAccountTypeExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.industryAccountTypeReport(requestParams, usersList);
            List<CrmAccount> accountList = kmsg.getEntityList();
            Map<String, List<AccountOwnerInfo>> owners = getAccountOwnerInfor(accountList);
            jobj = getindustryAccountTypeJson(accountList, request, false, kmsg.getRecordTotalCount(), owners);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Industry_Account,
                    "Industry-Account Type Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.industryAccountTypeExport", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView keyAccountsPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.getKeyAccountschart(requestParams, usersList);
            List<CrmAccount> ll = kmsg.getEntityList();
            for (CrmAccount obj : ll) {
                double revenue = 0;
                JSONObject tmpObj = new JSONObject();
                if (!StringUtil.isNullOrEmpty(obj.getRevenue())) {
                    revenue = Double.parseDouble(obj.getRevenue());
                }
                tmpObj.put("account", obj.getAccountname());
                tmpObj.put("Revenue", revenue);
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("Count", kmsg.getRecordTotalCount());

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.getString("Count")); j++) {
                String revenue = jarr.getJSONObject(j).get("Revenue").toString();
                if (!StringUtil.equal(revenue, "0.0")) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("account").toString() + "\">" + revenue + "</slice>";
                }
            }
            result += "</pie>";
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.keyAccountsPieChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView keyAccountsBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.getKeyAccountschart(requestParams, usersList);
            List<CrmAccount> ll = kmsg.getEntityList();
            for(CrmAccount obj : ll) {
                double revenue = 0;
                JSONObject tmpObj = new JSONObject();
                if (!StringUtil.isNullOrEmpty(obj.getRevenue())) {
                    revenue = Double.parseDouble(obj.getRevenue());
                }
                tmpObj.put("account", obj.getAccountname());
                tmpObj.put("revenue", revenue);
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("Count", kmsg.getRecordTotalCount());

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<chart><series>";
            for (int j = 0; j < Integer.parseInt(jobj.getString("Count")); j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("account").toString() + "</value>";
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < Integer.parseInt(jobj.get("Count").toString()); k++) {
                result += "<value xid=\"" + k + "\" >" + jarr.getJSONObject(k).get("revenue").toString() + "</value>";
            }
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.keyAccountsBarChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView monthlyAccountsPieChart(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            String monthArray[] = {"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            result = "<pie>";
            for (int j = 1; j <= 12; j++) {
                kmsg = accountReportDAOObj.getMonthlyAccountschart(j, requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + monthArray[j] + "\">" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }
            result += "</pie>";
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.monthlyAccountsPieChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView monthlyAccountsBarChart(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            result = "<chart><series>";
            result += "<value xid=\"" + 0 + "\" >January</value>";
            result += "<value xid=\"" + 1 + "\" >February</value>";
            result += "<value xid=\"" + 2 + "\" >March</value>";
            result += "<value xid=\"" + 3 + "\" >April</value>";
            result += "<value xid=\"" + 4 + "\" >May</value>";
            result += "<value xid=\"" + 5 + "\" >June</value>";
            result += "<value xid=\"" + 6 + "\" >July</value>";
            result += "<value xid=\"" + 7 + "\" >August</value>";
            result += "<value xid=\"" + 8 + "\" >September</value>";
            result += "<value xid=\"" + 9 + "\" >October</value>";
            result += "<value xid=\"" + 10 + "\" >November</value>";
            result += "<value xid=\"" + 11 + "\" >December</value>";
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 1; k <= 12; k++) {
                kmsg = accountReportDAOObj.getMonthlyAccountschart(k, requestParams, usersList);
                temp = k - 1;
                result += "<value xid=\"" + temp + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.monthlyAccountsBarChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView accountsByOwnerPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = null;//crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!heirarchyPerm) {
                usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            }
            kmsg = crmUserDAOObj.getOwner(companyid, userid, usersList);
            List<User> userLL = kmsg.getEntityList();
            for(User obj : userLL) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getUserID());
                tmpObj.put("name", obj.getFirstName() + "  " + obj.getLastName());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", kmsg.getRecordTotalCount());

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = accountReportDAOObj.getAccountsByOwnerChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }
            result += "</pie>";
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.accountsByOwnerPieChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView accountsByOwnerBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = null;//crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!heirarchyPerm) {
                usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            }
            kmsg = crmUserDAOObj.getOwner(companyid, userid, usersList);
            List<User> userll = kmsg.getEntityList();
            for(User obj : userll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getUserID());
                tmpObj.put("name", obj.getFirstName() + "  " + obj.getLastName());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", kmsg.getRecordTotalCount());

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<chart><series>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < Integer.parseInt(jobj.get("totalCount").toString()); k++) {
                kmsg = accountReportDAOObj.getAccountsByOwnerChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.accountsByOwnerBarChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }



    public ModelAndView industryAccountTypePieChart(HttpServletRequest request, HttpServletResponse response)
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
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
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
            for(DefaultMasterItem crmCombodata : ll) {
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
                kmsg = accountReportDAOObj.getIndustryAccountTypeChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.industryAccountTypePieChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView industryAccountTypeBarChart(HttpServletRequest request, HttpServletResponse response)
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
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
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
            for(DefaultMasterItem crmCombodata : ll) {
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
                kmsg = accountReportDAOObj.getIndustryAccountTypeChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.industryAccountTypeBarChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    //Lead report
    
    public ModelAndView convertedLeadsToAccountReport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPermAcc = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPermAcc", heirarchyPermAcc);
            boolean heirarchyPermLea = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPermLea", heirarchyPermLea);
            
            kmsg = accountReportDAOObj.convertedLeadsToAccountReport(requestParams, usersList);
            jobj = getConvertedLeadsReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), "Account");
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.convertedLeadsToAccountReport", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView convertedLeadsToAccountExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = accountReportDAOObj.convertedLeadsToAccountReport(requestParams, usersList);
            jobj = getConvertedLeadsReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), "Account");

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Converted_Leads_Accounts,
                    "Converted Leads to Accounts Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
        	logger.warn("Exception in crmAccountReportController.convertedLeadsToAccountExport", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public JSONObject getConvertedLeadsReportJson(List<CrmAccount> ll, HttpServletRequest request, boolean export, int totalSize, String convertedTo) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        CrmLead objLead = null;
        int day = 0;
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String currencySymbol=crmManagerDAOObj.currencySymbol(currencyid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmAccount objAcc : ll) {
                JSONObject tmpObj = new JSONObject();
                if (convertedTo.equals("Account")) {
                    objLead = objAcc.getCrmLead();
                    tmpObj.put("accname", objAcc.getAccountname());
                    tmpObj.put("revenue", !StringUtil.isNullOrEmpty(objAcc.getRevenue()) ? crmManagerDAOObj.currencyRender(objAcc.getRevenue(), currencyid) : "");
                    tmpObj.put("convertedon", objAcc.getCreatedOn()!=null?objAcc.getCreatedOn():"");
                    day = StringUtil.getDaysDiff(objLead.getCreatedOn(), objAcc.getCreatedOn());
                }
                tmpObj.put("leadname", objLead.getLastname());
                tmpObj.put("type", crmLeadHandler.getLeadTypeName(objLead.getType()));
                tmpObj.put("datecreate",objLead.getCreatedOn()!=null?objLead.getCreatedOn():"");
                tmpObj.put("leadsource", (objLead.getCrmCombodataByLeadsourceid() != null ? objLead.getCrmCombodataByLeadsourceid().getValue() : " None "));
                tmpObj.put("industry", (objLead.getCrmCombodataByIndustryid() != null ? objLead.getCrmCombodataByIndustryid().getValue() : " Undefined "));
                tmpObj.put("datediff", Math.abs(day) > 1 ? day + "  Days" : day + " Day");

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Lead");
                for (DefaultHeader obj : ll1) {
                    String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, obj.getDefaultHeader(), "Lead", companyid);
                    if(StringUtil.equal(Header.LEADNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.convertedleadsreport.leadname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.LEADNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","textfield");
                        jobjTemp.put("dataIndex", "leadname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.LEADTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.type", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.type", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.LEADSOURCEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","combo");
                        jobjTemp.put("dataIndex", "leadsource");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.LEADINDUSTRYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.industry", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.industry", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "industry");
                        jobjTemp.put("xtype","combo");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.LEADCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadcreatedon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadcreatedon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "datecreate");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }

                List<DefaultHeader> ll2=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Account");
                for (DefaultHeader obj2 : ll2) {
                    String newHeader2 =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj2.getDefaultHeader(),"Account",companyid);
                    if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, obj2.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)?messageSource.getMessage("crm.account.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)?messageSource.getMessage("crm.account.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","textfield");
                        jobjTemp.put("dataIndex", "accname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.ACCOUNTREVENUEHEADER, obj2.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.account.defaultheader.revenue", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader2+"("+currencySymbol+")");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.account.defaultheader.revenue", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader2+"("+currencySymbol+")");
                        jobjTemp.put("align", "right");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","numberfield");
                        jobjTemp.put("dataIndex", "revenue");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   } else if(StringUtil.equal(Header.ACCOUNTCREATIONDATEHEADER, obj2.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", messageSource.getMessage("crm.report.allreport.convertedoncolumnheader", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip", messageSource.getMessage("crm.report.allreport.convertedoncolumnheader", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "convertedon");
                        jobjTemp.put("renderer", crmManagerCommon.dateRendererReport());
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   } else if(StringUtil.equal(Header.ACCOUNTDESCRIPTIONHEADER, obj2.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", messageSource.getMessage("crm.report.allreport.daystoconvert", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip", messageSource.getMessage("crm.report.allreport.daystoconvert", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype","numberfield");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "datediff");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                }

                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "datediff");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsource");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "industry");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "revenue");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "convertedon"); // account creation date
                jobjTemp.put("type", "date");
                jobjTemp.put("dateFormat", "time");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "datecreate"); // lead creation date
                jobjTemp.put("type", "date");
                jobjTemp.put("dateFormat", "time");
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
        	logger.warn("Exception in crmAccountReportController.getConvertedLeadsReportJson", e);
        }
        return commData;
    }
}
