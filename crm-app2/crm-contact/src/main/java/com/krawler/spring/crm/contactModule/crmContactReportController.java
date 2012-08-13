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
package com.krawler.spring.crm.contactModule;

import com.krawler.common.util.Header;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.leadModule.crmLeadHandler;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

public class crmContactReportController extends MultiActionController implements MessageSourceAware{

    private crmContactReportDAO contactReportDAOObj;
    private crmContactDAO crmContactDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private crmCommonDAO crmCommonDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private MessageSource messageSource;
    
    
    public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	public crmContactReportDAO getcrmContactReportDAO(){
        return contactReportDAOObj;
    }
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmContactReportDAO(crmContactReportDAO contactReportDAOObj1) {
        this.contactReportDAOObj = contactReportDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
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

    public ModelAndView contactsByLeadSourceReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm",request.getParameter("frm"));
            requestParams.put("to",request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = contactReportDAOObj.contactsByLeadSourceReport(requestParams, usersList);
            jobj = crmContactHandler.contactsReportJson(crmContactDAOObj, kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(),crmCommonDAOObj,crmManagerDAOObj);
        } catch (Exception e) {
            logger.warn("Exception in crmContactReportController.contactsByLeadSourceReport", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView contactsByLeadSourceExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm",request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = contactReportDAOObj.contactsByLeadSourceReport(requestParams, usersList);
            jobj = crmContactHandler.contactsReportJson(crmContactDAOObj, kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(),crmCommonDAOObj,crmManagerDAOObj);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Contacts_Lead_Source,
                    "Contacts by Lead Source Cases Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
        	logger.warn("Exception in crmContactReportController.contactsByLeadSourceExport", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView leadSourceContactsPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        List ll = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("companyid", companyid);
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                DefaultMasterItem crmCombodata = (DefaultMasterItem) ite.next();
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
                if (!jarr.getJSONObject(j).get("name").toString().equals("None")) {
                    kmsg = contactReportDAOObj.getLeadSourceContactsChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                    if (kmsg.getRecordTotalCount() > 0) {
                        result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                    }
                }
            }
            kmsg = contactReportDAOObj.getLeadSourceContactsChart("Undefined", requestParams, usersList);
            if (kmsg.getRecordTotalCount() > 0) {
                result += "<slice title=\"Undefined\">" + kmsg.getRecordTotalCount() + "</slice>";
            }
            result += "</pie>";
        } catch (Exception e) {
        	logger.warn("Exception in crmContactReportController.leadSourceContactsPieChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView leadSourceContactsBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        List ll = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
                order_by.add("d.crmCombomaster.comboname");
                order_type.add("desc");
            }
            order_by.add("d.value");
            order_type.add("asc");
            HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
            comboRequestParams.put("companyid", companyid);
            comboRequestParams.put("filter_names", filter_names);
            comboRequestParams.put("filter_params", filter_params);
            comboRequestParams.put("order_by", order_by);
            comboRequestParams.put("order_type", order_type);
            ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
            dl = ll.size();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                DefaultMasterItem crmCombodata = (DefaultMasterItem) ite.next();
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
                    kmsg = contactReportDAOObj.getLeadSourceContactsChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                    result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
                }
            }
            kmsg = contactReportDAOObj.getLeadSourceContactsChart("Undefined", requestParams, usersList);
            result += "<value xid=\"" + Integer.parseInt(jobj.get("totalCount").toString()) + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
        	logger.warn("Exception in crmContactReportController.leadSourceContactsBarChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }
    
    //Account Module
    public JSONObject getAccountContactReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("contactid", obj.getContactid());
                tmpObj.put("contactowner", StringUtil.checkForNull(obj.getFirstname()));
                tmpObj.put("firstname", StringUtil.checkForNull(obj.getFirstname()));
                tmpObj.put("accname", obj.getCrmAccount().getAccountname());
                tmpObj.put("accindustry", obj.getCrmAccount().getCrmCombodataByIndustryid() != null ? obj.getCrmAccount().getCrmCombodataByIndustryid().getValue() : "");
                tmpObj.put("acctype", obj.getCrmAccount().getCrmCombodataByAccounttypeid() != null ? obj.getCrmAccount().getCrmCombodataByAccounttypeid().getValue() : "");
                tmpObj.put("lastname", obj.getLastname());
                tmpObj.put("contactname", (StringUtil.checkForNull(obj.getFirstname()) + " " + StringUtil.checkForNull(obj.getLastname())).trim());
                tmpObj.put("phoneno", obj.getPhoneno());
                tmpObj.put("mobileno", obj.getMobileno() != null ? obj.getMobileno() : "");
                tmpObj.put("email", obj.getEmail());
                tmpObj.put("street", obj.getMailstreet());
                tmpObj.put("description", obj.getDescription() != null ? obj.getDescription() : "");
                tmpObj.put("createdon", obj.getCrmAccount().getCreatedOn()!=null?obj.getCrmAccount().getCreatedOn():"");
                tmpObj.put("validflag", obj.getValidflag());
                tmpObj.put("leadsourceid", crmManagerCommon.comboNull(obj.getCrmCombodataByLeadsourceid()));
                tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                tmpObj.put("titleid", "");
                tmpObj.put("title", obj.getTitle() != null ? obj.getTitle() : "");
                String aid = crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid").equals("") ? "0" : crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid");
                tmpObj.put("relatedname", aid);
                tmpObj.put("oldrelatedname", aid);

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Account");
                Iterator ite1 = ll1.iterator();

                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Account",companyid);
                    if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header",Hdr );
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.ACCOUNTTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.industryaccounttype.acctype", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.industryaccounttype.acctype", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "acctype");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.ACCOUNTINDUSTRYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.industry", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.industry", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "accindustry");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.ACCOUNTCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.account.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                }

                List ll2=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Contact");
                Iterator ite2 = ll2.iterator();
                while (ite2.hasNext()) {
                    DefaultHeader obj1 = (DefaultHeader) ite2.next();
                    String newHeader2 =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj1.getDefaultHeader(),"Contact",companyid);
                    if(StringUtil.equal(Header.CONTACTFIRSTNAMEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header",  messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip",  messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "contactname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CONTACTTITLEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.contact.defaultheader.desig", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.contact.defaultheader.desig", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "title");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "acctype");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accindustry");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "contactname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "title");
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
        	logger.warn("Exception in crmContactReportController.getAccountContactReportJson", e);
        }
        return commData;
    }

    public ModelAndView accountsWithContactReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPermCon = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            boolean heirarchyPermAcc = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPermCon", heirarchyPermCon);
            requestParams.put("heirarchyPermAcc", heirarchyPermAcc);
            
            kmsg = contactReportDAOObj.accountsWithContactReport(requestParams, usersList);
            jobj = getAccountContactReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
        	logger.warn("Exception in crmContactReportController.accountsWithContactReport", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView accountsWithContactExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = contactReportDAOObj.accountsWithContactReport(requestParams, usersList);
            jobj = getAccountContactReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Accounts_Contacts,
                    "Account with Contacts Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
        	logger.warn("Exception in crmContactReportController.accountsWithContactExport", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView accountContactPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
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

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            if(!heirarchyPerm) {
                filter_names.add("INao.usersByUserid.userID");
                filter_params.add(usersList);
            }

            kmsg = crmAccountDAOObj.getAllAccounts(requestParams, filter_names, filter_params);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmAccount obj = (CrmAccount) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getAccountid());
                tmpObj.put("name", obj.getAccountname());
                tmpObj.put("phone", obj.getPhone());
                tmpObj.put("email", obj.getEmail());
                tmpObj.put("productid", crmManagerCommon.moduleObjNull(obj.getCrmProduct(), "Productid"));
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", kmsg.getRecordTotalCount());

            result = "<pie>";
            JSONArray jarr = jobj.getJSONArray("data");
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = contactReportDAOObj.getAccountContactChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\">" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }
            kmsg = contactReportDAOObj.getAccountContactChart("Undefined", requestParams, usersList);
            if (kmsg.getRecordTotalCount() > 0) {
                result += "<slice title=\"Undefined\">" + kmsg.getRecordTotalCount() + "</slice>";
            }
            result += "</pie>";
        } catch (Exception e) {
        	logger.warn("Exception in crmContactReportController.accountContactPieChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView accountContactBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        String result2 = "";
        int k = 0;
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
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

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            if(!heirarchyPerm) {
                filter_names.add("INao.usersByUserid.userID");
                filter_params.add(usersList);
            }

            kmsg = crmAccountDAOObj.getAllAccounts(requestParams, filter_names, filter_params);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmAccount obj = (CrmAccount) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getAccountid());
                tmpObj.put("name", obj.getAccountname());
                tmpObj.put("phone", obj.getPhone());
                tmpObj.put("email", obj.getEmail());
                tmpObj.put("productid", crmManagerCommon.moduleObjNull(obj.getCrmProduct(), "Productid"));
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", kmsg.getRecordTotalCount());

            JSONArray jarr = jobj.getJSONArray("data");
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                kmsg = contactReportDAOObj.getAccountContactChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<value xid=\"" + k + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
                    result2 += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
                    k++;
                }
            }
            kmsg = contactReportDAOObj.getAccountContactChart("Undefined", requestParams, usersList);
            if (kmsg.getRecordTotalCount() > 0) {
                result += "<value xid=\"" + k + "\" >Undefined</value>";
                result2 += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</series><graphs><graph gid=\"0\">";
            result2 += "</graph></graphs></chart>";

            result += result2;
        } catch (Exception e) {
        	logger.warn("Exception in crmContactReportController.accountContactBarChart", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    //Lead Report
    
    public ModelAndView convertedLeadsToContactReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to",request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPermCon = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPermCon", heirarchyPermCon);
            boolean heirarchyPermLea = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPermLea", heirarchyPermLea);

            kmsg = contactReportDAOObj.convertedLeadsToContactReport(requestParams, usersList);
            jobj = getConvertedLeadsReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), "Contact");
        } catch (Exception e) {
        	logger.warn("Exception in crmContactReportController.convertedLeadsToContactReport", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView convertedLeadsToContactExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm",request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = contactReportDAOObj.convertedLeadsToContactReport(requestParams, usersList);
            jobj = getConvertedLeadsReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), "Contact");

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Converted_Leads_Contact,
                    "Converted Leads to Contact Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
        	logger.warn("Exception in crmContactReportController.convertedLeadsToContactExport", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public JSONObject getConvertedLeadsReportJson(List ll, HttpServletRequest request, boolean export, int totalSize, String convertedTo) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        CrmLead objLead = null;
        int day = 0;
        try {
            Iterator ite = ll.iterator();
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            while (ite.hasNext()) {
                JSONObject tmpObj = new JSONObject();
                if (convertedTo.equals("Contact")) {
                    Object [] temp = (Object[]) ite.next();
                    CrmContact objCon = (CrmContact) temp[0];
                    objLead = (CrmLead) temp[1];
                    tmpObj.put("conname", (StringUtil.checkForNull(objCon.getFirstname()) + " " + StringUtil.checkForNull(objCon.getLastname())).trim());
                    tmpObj.put("condate",objCon.getCreatedOn()!=null?objLead.getCreatedOn():"");
                    day = StringUtil.getDaysDiff(objLead.getCreatedOn(), objCon.getCreatedOn());
                }
                tmpObj.put("leadname", objLead.getLastname());
                tmpObj.put("type", crmLeadHandler.getLeadTypeName(objLead.getType()));
                tmpObj.put("datecreate", objLead.getCreatedOn()!=null?objLead.getCreatedOn():"");
                tmpObj.put("leadsource", (objLead.getCrmCombodataByLeadsourceid() != null ? objLead.getCrmCombodataByLeadsourceid().getValue() : " None "));
                tmpObj.put("industry", (objLead.getCrmCombodataByIndustryid() != null ? objLead.getCrmCombodataByIndustryid().getValue() : " Undefined "));
                tmpObj.put("datediff", Math.abs(day) > 1 ? day + "  Days" : day + " Day");

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Lead");
                Iterator ite1 = ll1.iterator();
                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
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
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "leadname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.LEADTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.type", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.type", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.LEADSOURCEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "leadsource");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.LEADINDUSTRYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.industry", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.industry", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "industry");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.LEADCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadcreatedon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadcreatedon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("dataIndex", "datecreate");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     }
                }

                List ll2=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Contact");
                Iterator ite2 = ll2.iterator();

                while (ite2.hasNext()) {
                    DefaultHeader obj1 = (DefaultHeader) ite2.next();
                    String newHeader2 =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj1.getDefaultHeader(),"Contact",companyid);
                    if(StringUtil.equal(Header.CONTACTFIRSTNAMEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header",  messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip",  messageSource.getMessage("crm.case.defaultheader.contactname", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "conname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CONTACTCREATIONDATEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header",  messageSource.getMessage("crm.report.opportunityreport.convertedon", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip",  messageSource.getMessage("crm.report.opportunityreport.convertedon", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("dataIndex", "condate");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.CONTACTDESCRIPTIONHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header",  messageSource.getMessage("crm.report.allreport.daystoconvert", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip",  messageSource.getMessage("crm.report.allreport.daystoconvert", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "numberfield");
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
                jobjTemp.put("name", "conname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "condate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "datecreate"); // lead creation date
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
        	logger.warn("Exception in crmContactReportController.getConvertedLeadsReportJson", e);
        }
        return commData;
    }
}
