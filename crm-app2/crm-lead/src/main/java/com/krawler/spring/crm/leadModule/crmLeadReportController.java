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
package com.krawler.spring.crm.leadModule;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Header;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;

import javax.mail.MessageAware;
import javax.mail.MessageContext;
import com.krawler.crm.lead.dm.LeadOwnerInfo;
import com.krawler.utils.json.base.JSONException;
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
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class crmLeadReportController extends MultiActionController implements MessageSourceAware{

    private crmLeadReportDAO leadReportDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private String successView;
    private crmLeadDAO crmLeadDAOObj;
    private crmCommonDAO crmCommonDAOObj;
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    
    public crmLeadReportDAO getcrmLeadReportDAO(){
        return leadReportDAOObj;
    }
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setcrmLeadReportDAO(crmLeadReportDAO leadReportDAOObj1) {
        this.leadReportDAOObj = leadReportDAOObj1;
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

    public JSONObject getLeadReportJson(List<CrmLead> ll, HttpServletRequest request, boolean export, int totalSize, HashMap<String, DefaultMasterItem> defaultMasterMap) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String qucikSerachFields = request.getParameter("quickSearchFields");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String reportid = request.getParameter("reportId");
            
            // get owners
            List<String> idsList = new ArrayList<String>();
            for (CrmLead obj : ll) {
                idsList.add(obj.getLeadid());
            }
            Map<String, List<LeadOwnerInfo>> owners = crmLeadDAOObj.getLeadOwners(idsList);
            
            for (CrmLead obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("leadname", obj.getLastname());
                tmpObj.put("convertedon", obj.getConvertedOn()==null?"":obj.getConvertedOn());
                tmpObj.put("type", crmLeadHandler.getLeadTypeName(obj.getType()));
                tmpObj.put("datecreate",obj.getCreatedOn() != null?obj.getCreatedOn():"");
                String status = " None ";
                if(obj.getLeadstatusID() != null && defaultMasterMap.containsKey(obj.getLeadstatusID())) {
                    status = defaultMasterMap.get(obj.getLeadstatusID()).getValue();
                }
                tmpObj.put("leadstatus", status);
                String source = " None ";
                if(obj.getLeadsourceID() != null && defaultMasterMap.containsKey(obj.getLeadsourceID())) {
                    source = defaultMasterMap.get(obj.getLeadsourceID()).getValue();
                }
                tmpObj.put("leadsource", source);
                String industry = " None ";
                if(obj.getIndustryID() != null && defaultMasterMap.containsKey(obj.getIndustryID())) {
                    industry = defaultMasterMap.get(obj.getIndustryID()).getValue();
                }
                tmpObj.put("industry", industry);
                String[] ownerInfo = crmLeadHandler.getAllLeadOwners(owners.get(obj.getLeadid()));
                tmpObj.put("leadowner", ownerInfo[4]);
                tmpObj.put("exportmultiowners", ownerInfo[5]);
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Lead");
                for (DefaultHeader obj : ll1) {
                    String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, obj.getDefaultHeader(), "Lead", companyid);
                    if (StringUtil.equal(Header.LEADSOURCEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)) : newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)): newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "leadsource");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if (StringUtil.equal(Header.LEADNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.lname", null, RequestContextUtils.getLocale(request)) : newHeader;
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
                    } else if(StringUtil.equal(Header.LEADINDUSTRYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.industry", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.industry", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "industry");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if (StringUtil.equal(Header.LEADTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.type", null, RequestContextUtils.getLocale(request)) : newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.type", null, RequestContextUtils.getLocale(request)) : newHeader);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if (StringUtil.equal(Header.LEADSTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.leadstatus", null, RequestContextUtils.getLocale(request)): newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.leadstatus", null, RequestContextUtils.getLocale(request)) : newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "leadstatus");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if (StringUtil.equal(Header.LEADOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.owner", null, RequestContextUtils.getLocale(request)) : newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.owner", null, RequestContextUtils.getLocale(request)) : newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "leadowner");
                        jobjTemp.put("title", "exportmultiowners");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if (StringUtil.equal(Header.LEADCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.leadcreatedon", null, RequestContextUtils.getLocale(request)) : newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader) ? messageSource.getMessage("crm.lead.defaultheader.leadcreatedon", null, RequestContextUtils.getLocale(request)) : newHeader);
                        jobjTemp.put("title", "datecreate");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "datecreate");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("align", "center");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                        
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", messageSource.getMessage("crm.report.allreport.convertedoncolumnheader", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip", messageSource.getMessage("crm.report.allreport.convertedoncolumnheader", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("title", "convertedon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("hidden", StringUtil.equal(reportid, "10")?true:false);
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("dataIndex", "convertedon");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        jobjTemp.put("align", "center");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadstatus");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsource");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "industry");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "datecreate");
                jobjTemp.put("dateFormat","time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "convertedon");
                jobjTemp.put("dateFormat","time");
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
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }
    
    public JSONObject getOpenLeadsReportJson(List<CrmLead> ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String qucikSerachFields = request.getParameter("quickSearchFields");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            for (CrmLead obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("leadname", obj.getLastname());
                tmpObj.put("type", crmLeadHandler.getLeadTypeName(obj.getType()));
                tmpObj.put("title", obj.getTitle() != null ? obj.getTitle() : "");
                tmpObj.put("leadstatus", (obj.getCrmCombodataByLeadstatusid() != null ? obj.getCrmCombodataByLeadstatusid().getValue() : " None "));
                tmpObj.put("rating", (obj.getCrmCombodataByRatingid() != null ? obj.getCrmCombodataByRatingid().getValue() : " None "));
                tmpObj.put("datecreate",  obj.getCreatedOn() != null?obj.getCreatedOn():"");
                tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : " None "));
                tmpObj.put("industry", (obj.getCrmCombodataByIndustryid() != null ? obj.getCrmCombodataByIndustryid().getValue() : " None "));
                String[] ownerInfo=crmLeadHandler.getAllLeadOwners(crmLeadDAOObj, obj.getLeadid());
                tmpObj.put("leadowner", ownerInfo[4]);
                tmpObj.put("exportmultiowners", ownerInfo[5]);
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
                Date today = cal.getTime();

                int day=StringUtil.getDaysDiff(sdf.format(obj.getCreatedon()), sdf.format(today));
                tmpObj.put("age", day>1?day+"  Days Old":day+" Day Old");
                
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Lead");
                for (DefaultHeader obj : ll1) {
                    String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, obj.getDefaultHeader(), "Lead", companyid);
                    if(StringUtil.equal(Header.LEADNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.lname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.LEADNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header",Hdr );
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "leadname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.LEADTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.type", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.type", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.LEADINDUSTRYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.industry", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.industry", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "industry");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.LEADTITLEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.desig", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.desig", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "title");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.LEADSOURCEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "leadsource");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   } else if(StringUtil.equal(Header.LEADSTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadstatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadstatus", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "leadstatus");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   } else if(StringUtil.equal(Header.LEADRATINGHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.rating", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.rating", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "rating");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   } else if(StringUtil.equal(Header.LEADCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", messageSource.getMessage("crm.report.openleads.age", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip", messageSource.getMessage("crm.report.openleads.age", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "age");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);

                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadcreatedon", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadcreatedon", null, RequestContextUtils.getLocale(request)) :newHeader);
                        jobjTemp.put("title", "datecreate");
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

                jobjTemp = new JSONObject();
                jobjTemp.put("name", "rating");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadstatus");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "title");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsource");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "industry");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "datecreate");
                jobjTemp.put("dateFormat", "time");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "age");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "datecreate");
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
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }
       
    public JSONObject getLeadPipelinedReportJson(List ll, HttpServletRequest request, boolean export, int totalSize,List<DefaultMasterItem> ll1,int dl1) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            double sumAmount = 0;
            Iterator ite = ll.iterator();
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            String currencySymbol=crmManagerDAOObj.currencySymbol(currencyid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            Iterator iteSum = ll.iterator();
            Map leadStatus = new HashMap();
            for (DefaultMasterItem dm : ll1){
                leadStatus.put(dm.getValue(),dm.getPercentStage()!=null?dm.getPercentStage():"0");
            }
            while(iteSum.hasNext()) {
                Object[] row = (Object[]) iteSum.next();
                String stage = (String) row[1];
                String revenue = (String) row[2];
                String price = (String) row[3];
                if (!StringUtil.isNullOrEmpty(stage) && !StringUtil.isNullOrEmpty(revenue) && !StringUtil.isNullOrEmpty(price)) {
                    double percentStage = Double.parseDouble(stage);
                    double totalSalesAmount = Double.parseDouble(revenue);
                    double totalPrice = Double.parseDouble(price);
                    sumAmount += (percentStage/100) * (totalSalesAmount - totalPrice);
                }
            }
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                String stageName = (String) row[0];
                String stage = (String) row[1];
                String revenue = (String) row[2];
                String price = (String) row[3];
                long count = (Long) row[4];
                double amount = 0;
                double grossProfit = 0;
                if (!StringUtil.isNullOrEmpty(stage) && !StringUtil.isNullOrEmpty(revenue) && !StringUtil.isNullOrEmpty(price)) {
                    double percentStage = Double.parseDouble(stage);
                    double totalSalesAmount = Double.parseDouble(revenue);
                    double totalPrice = Double.parseDouble(price);
                    grossProfit = totalSalesAmount - totalPrice;
                    amount = (percentStage/100) * (totalSalesAmount - totalPrice);
                }

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("statusname", stageName);
                tmpObj.put("percent", !StringUtil.isNullOrEmpty(stage)?stage+" %":"0 %");
                tmpObj.put("count", count);
                tmpObj.put("gp", crmManagerDAOObj.currencyRender(decimalFormat.format(grossProfit), currencyid));
                tmpObj.put("amount", crmManagerDAOObj.currencyRender(decimalFormat.format(amount), currencyid));

                jarr.put(tmpObj);
                leadStatus.remove(stageName);
            }
            Iterator leadStatusIte = leadStatus.keySet().iterator();
            while(leadStatusIte.hasNext()){
                Object al = (Object) leadStatusIte.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("statusname",al );
                tmpObj.put("percent", leadStatus.get(al).toString()+" %");
                tmpObj.put("count", 0);
                tmpObj.put("gp", crmManagerDAOObj.currencyRender(decimalFormat.format(0), currencyid));
                tmpObj.put("amount", crmManagerDAOObj.currencyRender(decimalFormat.format(0), currencyid));

                jarr.put(tmpObj);
            }
            if (totalSize >= ll.size()) {
                JSONObject tmpObj = new JSONObject();
                if (export) {
                    tmpObj.put("statusname", "<div style='font-size:14px;font-weight:bold;font-family:tahoma,helvetica,sans-serif;'>TOTAL :</div>");
                } else {
                    tmpObj.put("statusname", "TOTAL :");
                }
                tmpObj.put("percent", "");
                tmpObj.put("count", "");
                tmpObj.put("gp", "");
                if (export) {
                    tmpObj.put("amount", "<div style='font-size:14px;font-weight:bold;'>" + crmManagerDAOObj.currencyRender(decimalFormat.format(sumAmount), currencyid) + "</div>");
                } else {
                    tmpObj.put("amount", crmManagerDAOObj.currencyRender(decimalFormat.format(sumAmount), currencyid));
                }

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, Header.LEADSTATUSHEADER, "Lead", companyid);
                jobjTemp = new JSONObject();
                String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadstatus", null, RequestContextUtils.getLocale(request)) :newHeader;
                if(StringUtil.equal(Header.LEADSTATUSHEADER, qucikSerachFields)){
                    jobjTemp.put("qucikSearchText", Hdr);
                }
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "statusname");
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("header", Hdr);
                jobjTemp.put("tip", Hdr);
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header",messageSource.getMessage("crm.report.pipelinereport.stagegrossprofit", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.report.pipelinereport.stagegrossprofit.ttip", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("align", "right");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("dataIndex", "gp");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.report.pipelinereport.weightageinpipeline", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.report.pipelinereport.weightageinpipeline", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("align", "right");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("dataIndex", "percent");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.report.pipelinereport.totalnoofOB", new Object[]{messageSource.getMessage("crm.LEAD.plural", null, RequestContextUtils.getLocale(request))}, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.report.pipelinereport.totalnoofOB", new Object[]{messageSource.getMessage("crm.LEAD.plural", null, RequestContextUtils.getLocale(request))}, RequestContextUtils.getLocale(request)));
                jobjTemp.put("align", "right");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("dataIndex", "count");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.report.pipelinereport.amount", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")");
                jobjTemp.put("tip", messageSource.getMessage("crm.report.pipelinereport.amount", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")");
                jobjTemp.put("align", "right");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("dataIndex", "amount");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "statusname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "gp");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "amount");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "percent");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "count");
                jarrRecords.put(jobjTemp);

                commData.put("columns", jarrColumns);
                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", dl1+1);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }

    public ModelAndView leadsByIndustryReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("industryid", StringUtil.checkForNull(request.getParameter("filterCombo")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.leadsByIndustryReport(requestParams, usersList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);
            jobj = getLeadReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), defaultMasterMap);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView leadsByIndustryExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.leadsByIndustryReport(requestParams, usersList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);
            jobj = getLeadReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), defaultMasterMap);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Lead_By_Industry,
                    "Leads by Industry Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView convertedLeadsReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.convertedLeadsReport(requestParams, usersList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);
            jobj = getLeadReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), defaultMasterMap);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView convertedLeadsExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm",request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.convertedLeadsReport(requestParams, usersList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);
            jobj = getLeadReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), defaultMasterMap);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Converted_Lead,
                    "Converted Leads Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView leadsPipelineReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        KwlReturnObject kmsg1 = null;
        try {
            String export = request.getParameter("reportid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboname ="Lead Status";
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            order_by.add("d.value");
            order_type.add("asc");
            requestParams1.put("filter_names", filter_names);
            requestParams1.put("filter_params", filter_params);
            requestParams1.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams1.put("order_by", order_by);
            requestParams1.put("order_type", order_type);
            requestParams1.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams1.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            kmsg1 = crmManagerDAOObj.getComboDataPaging(comboname, requestParams1);
            int dl1 = kmsg1.getRecordTotalCount();
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            kmsg = leadReportDAOObj.leadsPipelineReport(requestParams, usersList);
            jobj = getLeadPipelinedReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(),kmsg1.getEntityList(),dl1);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView leadsPipelineExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        KwlReturnObject kmsg1 = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboname ="Lead Status";
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            order_by.add("d.value");
            order_type.add("asc");
            requestParams1.put("filter_names", filter_names);
            requestParams1.put("filter_params", filter_params);
            requestParams1.put("order_by", order_by);
            requestParams1.put("order_type", order_type);
            requestParams1.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams1.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            kmsg1 = crmManagerDAOObj.getComboDataPaging(comboname, requestParams1);
            int dl1 = kmsg1.getRecordTotalCount();

            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.leadsPipelineReport(requestParams, usersList);
            jobj = getLeadPipelinedReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(),kmsg1.getEntityList(),dl1);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView leadsBySourceReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("sourceid", StringUtil.checkForNull(request.getParameter("filterCombo")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.leadsBySourceReport(requestParams, usersList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);
            jobj = getLeadReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), defaultMasterMap);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView leadsBySourceExport(HttpServletRequest request, HttpServletResponse response)
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
            requestParams.put("frm",request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.leadsBySourceReport(requestParams, usersList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);
            jobj = getLeadReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), defaultMasterMap);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Leads_By_Source,
                    "Leads by Source Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView qualifiedLeadsReport(HttpServletRequest request, HttpServletResponse response)
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
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.qualifiedLeadsReport(requestParams, usersList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);
            jobj = getLeadReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), defaultMasterMap);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView qualifiedLeadsExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.qualifiedLeadsReport(requestParams, usersList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);
            jobj = getLeadReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), defaultMasterMap);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Qualified_Leads,
                    "Qualified Leads Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView contactedLeadsReport(HttpServletRequest request, HttpServletResponse response)
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
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.contactedLeadsReport(requestParams, usersList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);
            jobj = getLeadReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), defaultMasterMap);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView contactedLeadsExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.contactedLeadsReport(requestParams, usersList);
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);
            jobj = getLeadReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), defaultMasterMap);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Contacted_Leads,
                    "Contacted Leads Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView openLeadsReport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.openLeadsReport(requestParams, usersList);
            jobj = getOpenLeadsReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView openLeadsExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.openLeadsReport(requestParams, usersList);
            jobj = getOpenLeadsReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Open_Leads,
                    "Open Leads Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView leadsByIndustryPieChart(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
	            requestParams.put("frm", request.getParameter("frm"));
    	        requestParams.put("to", request.getParameter("to"));
        	}
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ll = getLeadComboDataList(companyid, comboName);
            dl = ll.size();
            jarr1 = getComboDataJSONArray(ll);
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            String strFrmDate=request.getParameter("frm");
            String strToDate=request.getParameter("to");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = leadReportDAOObj.getLeadsByIndustryChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" url=\"javascript:amClick('"+jarr.getJSONObject(j).get("id").toString()+"',0,'"+strFrmDate+"','"+strToDate+"');\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }
//            kmsg = leadReportDAOObj.getLeadsByIndustryChart("Undefined", requestParams, usersList);
//            if (kmsg.getRecordTotalCount() > 0) {
//                result += "<slice title=\"Undefined\" >" + kmsg.getRecordTotalCount() + "</slice>";
//            }
            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView leadsByIndustryBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        String result1 = "";
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int cnt = 0;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ll = getLeadComboDataList(companyid, comboName);
            dl = ll.size();
            jarr1 = getComboDataJSONArray(ll);
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            String strFrmDate=request.getParameter("frm");
            String strToDate=request.getParameter("to");
            result1 = "<chart><series>";
            for (int k = 0; k < max; k++) { 
                kmsg = leadReportDAOObj.getLeadsByIndustryChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                if(kmsg.getRecordTotalCount() > 0) {
                    result += "<value xid=\"" + cnt + "\" url=\"javascript:amClick('"+jarr.getJSONObject(k).get("id").toString()+"',0,'"+strFrmDate+"','"+strToDate+"');\">" + kmsg.getRecordTotalCount() + "</value>";
                    result1 += "<value xid=\"" + cnt + "\" >" + jarr.getJSONObject(k).get("name").toString() + "</value>";
                    cnt++;
                }
            }
//            kmsg = leadReportDAOObj.getLeadsByIndustryChart("Undefined", requestParams, usersList);
//            if(kmsg.getRecordTotalCount() > 0) {
//                result1 += "<value xid=\"" + cnt + "\" >Undefined</value>";
//                result += "<value xid=\"" + cnt + "\" >" + kmsg.getRecordTotalCount() + "</value>";
//            }
            result1 += "</series><graphs><graph gid=\"0\">";
            result1 += result;
            result1 += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result1);
    }

    public ModelAndView convertedLeadsBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String reportId = request.getParameter("reportid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String view = request.getParameter("view");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            result = "<chart><series>";
            int year = 0;
            Calendar cal = Calendar.getInstance();
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm"))){
            	Long from = Long.parseLong(request.getParameter("frm"));
            	Date frmdate = new Date(from);
                year = frmdate.getYear() + 1900;
            } else {
                year = cal.get(Calendar.YEAR);
            }

            if(view.equals("1")) { // Weekday View
                result += "<value xid=\"0\" >"+messageSource.getMessage("day.monday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"1\" >"+messageSource.getMessage("day.tuesday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"2\" >"+messageSource.getMessage("day.wednesday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"3\" >"+messageSource.getMessage("day.thursday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"4\" >"+messageSource.getMessage("day.friday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"5\" >"+messageSource.getMessage("day.saturday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"6\" >"+messageSource.getMessage("day.sunday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "</series><graphs><graph gid=\"0\">";
                kmsg = leadReportDAOObj.getConvertedLeadsWeekDayViewChart(requestParams, usersList, result, reportId);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph>";
                result += "<graph gid=\"1\">";
                kmsg = leadReportDAOObj.getConvertedLeadsWeekDayViewChart(requestParams, usersList, result, "0");
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph></graphs></chart>";
            } else if (view.equals("2")) { //Month View
                result += "<value xid=\"0\" >Jan' "+year+"</value>";
                result += "<value xid=\"1\" >Feb' "+year+"</value>";
                result += "<value xid=\"2\" >March' "+year+"</value>";
                result += "<value xid=\"3\" >April' "+year+"</value>";
                result += "<value xid=\"4\" >May' "+year+"</value>";
                result += "<value xid=\"5\" >June' "+year+"</value>";
                result += "<value xid=\"6\" >July' "+year+"</value>";
                result += "<value xid=\"7\" >Aug' "+year+"</value>";
                result += "<value xid=\"8\" >Sep' "+year+"</value>";
                result += "<value xid=\"9\" >Oct' "+year+"</value>";
                result += "<value xid=\"10\" >Nov' "+year+"</value>";
                result += "<value xid=\"11\" >Dec' "+year+"</value>";
                result += "</series><graphs><graph gid=\"0\">";
                kmsg = leadReportDAOObj.getConvertedLeadsMonthViewChart(requestParams, usersList, result, reportId);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph>";
                result += "<graph gid=\"1\">";
                kmsg = leadReportDAOObj.getConvertedLeadsMonthViewChart(requestParams, usersList, result, "0");
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph></graphs></chart>";
            } else if (view.equals("4")) { // Year view
                kmsg = leadReportDAOObj.getConvertedLeadsYearViewChart(requestParams, usersList, result, reportId,true);
                result = kmsg.getEntityList().get(0).toString();
                result +="</series><graphs><graph gid=\"0\">";
                kmsg = leadReportDAOObj.getConvertedLeadsYearViewChart(requestParams, usersList, result, reportId,false);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph>";
                result += "<graph gid=\"1\">";
                kmsg = leadReportDAOObj.getConvertedLeadsYearViewChart(requestParams, usersList, result, "0",false);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph></graphs></chart>";
            } else if (view.equals("3")) { // Quaterly View
                result += "<value xid=\"0\" >"+messageSource.getMessage("month.january", null, RequestContextUtils.getLocale(request))+" - "+messageSource.getMessage("month.march", null, RequestContextUtils.getLocale(request))+year+"</value>";
                result += "<value xid=\"1\" > "+messageSource.getMessage("month.april", null, RequestContextUtils.getLocale(request))+" - "+messageSource.getMessage("month.june", null, RequestContextUtils.getLocale(request))+year+"</value>";
                result += "<value xid=\"2\" >"+messageSource.getMessage("month.july", null, RequestContextUtils.getLocale(request))+" - "+messageSource.getMessage("month.september", null, RequestContextUtils.getLocale(request))+year+"</value>";
                result += "<value xid=\"3\" >"+messageSource.getMessage("month.october", null, RequestContextUtils.getLocale(request))+" - "+messageSource.getMessage("month.december", null, RequestContextUtils.getLocale(request))+year+"</value>";
                result += "</series><graphs><graph gid=\"0\">";
                kmsg = leadReportDAOObj.getConvertedQuaterlyViewChart(requestParams, usersList, result, reportId);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph>";
                result += "<graph gid=\"1\">";
                kmsg = leadReportDAOObj.getConvertedQuaterlyViewChart(requestParams, usersList, result, "0");
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph></graphs></chart>";
            }
            
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }
    
    public ModelAndView convertedLeadToBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String view = request.getParameter("view");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("reportid", StringUtil.checkForNull(request.getParameter("reportid")));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            result = "<chart><series>";
          
            int year = 0;
            Calendar cal = Calendar.getInstance();
            if (!StringUtil.isNullOrEmpty(request.getParameter("frm"))) {
            	Long from = Long.parseLong(request.getParameter("frm"));
            	Date frmdate = new Date(from);
                year = frmdate.getYear() + 1900;
            } else {
                year = cal.get(Calendar.YEAR);
            }

            if(view.equals("1")) { // Weekday View
            	result += "<value xid=\"0\" >"+messageSource.getMessage("day.monday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"1\" >"+messageSource.getMessage("day.tuesday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"2\" >"+messageSource.getMessage("day.wednesday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"3\" >"+messageSource.getMessage("day.thursday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"4\" >"+messageSource.getMessage("day.friday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"5\" >"+messageSource.getMessage("day.saturday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "<value xid=\"6\" >"+messageSource.getMessage("day.sunday", null, RequestContextUtils.getLocale(request)) +"</value>";
                result += "</series><graphs><graph gid=\"0\">";
                kmsg = leadReportDAOObj.getConvertedLeadsToWeekDayViewChart(requestParams, usersList, result);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph>";
                result += "<graph gid=\"1\">";
                kmsg = leadReportDAOObj.getConvertedLeadsWeekDayViewChart(requestParams, usersList, result, null);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph></graphs></chart>";
            } else if (view.equals("2")) { //Month View
                result += "<value xid=\"0\" >Jan' "+year+"</value>";
                result += "<value xid=\"1\" >Feb' "+year+"</value>";
                result += "<value xid=\"2\" >March' "+year+"</value>";
                result += "<value xid=\"3\" >April' "+year+"</value>";
                result += "<value xid=\"4\" >May' "+year+"</value>";
                result += "<value xid=\"5\" >June' "+year+"</value>";
                result += "<value xid=\"6\" >July' "+year+"</value>";
                result += "<value xid=\"7\" >Aug' "+year+"</value>";
                result += "<value xid=\"8\" >Sep' "+year+"</value>";
                result += "<value xid=\"9\" >Oct' "+year+"</value>";
                result += "<value xid=\"10\" >Nov' "+year+"</value>";
                result += "<value xid=\"11\" >Dec' "+year+"</value>";
                result += "</series><graphs><graph gid=\"0\">";
                kmsg = leadReportDAOObj.getConvertedLeadsToMonthViewChart(requestParams, usersList, result);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph>";
                result += "<graph gid=\"1\">";
                kmsg = leadReportDAOObj.getConvertedLeadsMonthViewChart(requestParams, usersList, result, null);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph></graphs></chart>";
            } else if (view.equals("4")) { // Year view
                kmsg = leadReportDAOObj.getConvertedLeadsToYearViewChart(requestParams, usersList, result);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph>";
                result += "<graph gid=\"1\">";
                kmsg = leadReportDAOObj.getConvertedLeadsYearViewChart(requestParams, usersList, result, null,false);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph></graphs></chart>";
            } else if (view.equals("3")) { // Quaterly View
            	result += "<value xid=\"0\" >"+messageSource.getMessage("month.january", null, RequestContextUtils.getLocale(request))+" - "+messageSource.getMessage("month.march", null, RequestContextUtils.getLocale(request))+year+"</value>";
                result += "<value xid=\"1\" >"+messageSource.getMessage("month.april", null, RequestContextUtils.getLocale(request))+" - "+messageSource.getMessage("month.june", null, RequestContextUtils.getLocale(request))+year+"</value>";
                result += "<value xid=\"2\" >"+messageSource.getMessage("month.july", null, RequestContextUtils.getLocale(request))+" - "+messageSource.getMessage("month.september", null, RequestContextUtils.getLocale(request))+year+"</value>";
                result += "<value xid=\"3\" >"+messageSource.getMessage("month.october", null, RequestContextUtils.getLocale(request))+" - "+messageSource.getMessage("month.december", null, RequestContextUtils.getLocale(request))+year+"</value>";
                result += "</series><graphs><graph gid=\"0\">";
                kmsg = leadReportDAOObj.getConvertedQuaterlyToViewChart(requestParams, usersList, result);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph>";
                result += "<graph gid=\"1\">";
                kmsg = leadReportDAOObj.getConvertedQuaterlyViewChart(requestParams, usersList, result, null);
                result = kmsg.getEntityList().get(0).toString();
                result += "</graph></graphs></chart>";
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView leadsPipelinePieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            	requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            }
            
            ll = getLeadComboDataList(companyid, comboName);
            dl = ll.size();
            jarr1 = getComboDataJSONArray(ll);
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = leadReportDAOObj.leadsPipelineChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                Double amount = getPipelineChartAmount(kmsg.getEntityList());
                if (!StringUtil.equal(String.valueOf(amount), "0.0")) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + amount + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    private List<DefaultMasterItem> getLeadComboDataList(String companyid, String comboName) throws ServiceException {
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        ArrayList order_by = new ArrayList();
        ArrayList order_type = new ArrayList();
        filter_names.add("d.company.companyID");
        filter_params.add(companyid);
        if (comboName.equalsIgnoreCase("Lead Source")) {
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
        comboRequestParams.put("companyid", companyid);
        return crmManagerDAOObj.getComboData(comboName, comboRequestParams);
    }

    private JSONArray getComboDataJSONArray(List<DefaultMasterItem> ll) throws JSONException {
        JSONArray jarr1 = new JSONArray();
        for (DefaultMasterItem crmCombodata : ll) {
            JSONObject tmpObj = new JSONObject();
            String name = crmCombodata.getValue();
            tmpObj.put("id", crmCombodata.getID());
            tmpObj.put("mainid", crmCombodata.getMainID());
            tmpObj.put("name", name);
            jarr1.put(tmpObj);
        }
        return jarr1;
    }
    public ModelAndView leadsPipelineBarChart(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            ll = getLeadComboDataList(companyid, comboName);
            dl = ll.size();
            jarr1 = getComboDataJSONArray(ll);
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < max; k++) {
                kmsg = leadReportDAOObj.leadsPipelineChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                Double amount = getPipelineChartAmount(kmsg.getEntityList());
              
                    result += "<value xid=\"" + k + "\" >" + (amount) + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public Double getPipelineChartAmount(List ll) {

    double amount = 0;
    try {
        Iterator iterator = ll.iterator();
        while (iterator.hasNext()) {
            Object[] row = (Object[]) iterator.next();

            String stage = (String) row[0];
            String revenue = (String) row[1];
            String price = (String) row[2];
            double grossProfit = 0;
            if (!StringUtil.isNullOrEmpty(stage) && !StringUtil.isNullOrEmpty(revenue) && !StringUtil.isNullOrEmpty(price)) {
                double percentStage = Double.parseDouble(stage);
                double totalSalesAmount = Double.parseDouble(revenue);
                double totalPrice = Double.parseDouble(price);
                grossProfit = totalSalesAmount - totalPrice;
                amount = (percentStage/100) * (grossProfit);
            }
        }
    } catch (Exception e) {
            logger.warn(e.getMessage(), e);
    }
    return amount;
        
    }
    
    public ModelAndView leadsBySourcePieChart(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            ll = getLeadComboDataList(companyid, comboName);
            dl = ll.size();
            jarr1 = getComboDataJSONArray(ll);
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            String strFrmDate=request.getParameter("frm");
            String strToDate=request.getParameter("to");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = leadReportDAOObj.getLeadsBySourceChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" url=\"javascript:amClick('"+jarr.getJSONObject(j).get("id").toString()+"',7,'"+strFrmDate+"','"+strToDate+"');\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }
            /*kmsg = leadReportDAOObj.getLeadsBySourceChart("Undefined", requestParams, usersList);
            if (kmsg.getRecordTotalCount() > 0) {
                result += "<slice title=\"Undefined\" >" + kmsg.getRecordTotalCount() + "</slice>";
            }*/
            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView leadsBySourceBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        String result1 = "";
        int cnt = 0;
        List<DefaultMasterItem> ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm",request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ll = getLeadComboDataList(companyid, comboName);
            dl = ll.size();
            jarr1 = getComboDataJSONArray(ll);
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            String strFrmDate=request.getParameter("frm");
            String strToDate=request.getParameter("to");
            result1 = "<chart><series>";
            for (int k = 0; k < max; k++) {
                kmsg = leadReportDAOObj.getLeadsBySourceChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                if(kmsg.getRecordTotalCount() > 0) {
                    result += "<value xid=\"" + cnt + "\" url=\"javascript:amClick('"+jarr.getJSONObject(k).get("id").toString()+"',7,'"+strFrmDate+"','"+strToDate+"');\">" + kmsg.getRecordTotalCount() + "</value>";
                    result1 += "<value xid=\"" + cnt + "\" >" + jarr.getJSONObject(k).get("name").toString() + "</value>";
                    cnt++;
                }
            }
           /* kmsg = leadReportDAOObj.getLeadsBySourceChart("Undefined", requestParams, usersList);
            if(kmsg.getRecordTotalCount() > 0) {
                result1 += "<value xid=\"" + cnt + "\" >Undefined</value>";
                result += "<value xid=\"" + cnt + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }*/
            result1 += "</series><graphs><graph gid=\"0\">";
            result1 += result;
            result1 += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result1);
    }

    public ModelAndView openLeadPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        KwlReturnObject kmsg = null;
        int under30 = 0;
        int under60 = 0;
        int under90 = 0;
        int over90 = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
                
            kmsg = leadReportDAOObj.getOpenLeadChart(requestParams, usersList);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            Date today = cal.getTime();

            Iterator ite = kmsg.getEntityList().iterator();
            while(ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                int day=StringUtil.getDaysDiff(sdf.format(obj.getCreatedon()), sdf.format(today));
                if(day <= 30) {
                    under30++;
                } else if (day > 30 && day <= 60) {
                    under60++;
                } else if (day > 60 && day <= 90) {
                    under90++;
                } else if (day > 90) {
                    over90++;
                }
            }
            result = "<pie>";
            result += "<slice title=\"0 - 30 Days Old\">" + under30 + "</slice>";
            result += "<slice title=\"31 - 60 Days Old\">" + under60 + "</slice>";
            result += "<slice title=\"61 - 90 Days Old\">" + under90 + "</slice>";
            result += "<slice title=\"Greater than 90 Days Old\">" + over90 + "</slice>";
            result += "</pie>";
            
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView openLeadBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        KwlReturnObject kmsg = null;
        int under30 = 0;
        int under60 = 0;
        int under90 = 0;
        int over90 = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = leadReportDAOObj.getOpenLeadChart(requestParams, usersList);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
            Date today = cal.getTime();

            Iterator ite = kmsg.getEntityList().iterator();
            while(ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                int day=StringUtil.getDaysDiff(sdf.format(obj.getCreatedon()), sdf.format(today));
                if(day <= 30) {
                    under30++;
                } else if (day > 30 && day <= 60) {
                    under60++;
                } else if (day > 60 && day <= 90) {
                    under90++;
                } else if (day > 90) {
                    over90++;
                }
            }
            result = "<chart><series>";
            result += "<value xid=\"0\" >0 - 30 Days Old</value>";
            result += "<value xid=\"1\" >31 - 60 Days Old</value>";
            result += "<value xid=\"2\" >61 - 90 Days Old</value>";
            result += "<value xid=\"3\" >Greater than 90 Days Old</value>";
            result += "</series><graphs><graph gid=\"0\">";
            result += "<value xid=\"0\" >" + under30 + "</value>";
            result += "<value xid=\"1\" >" + under60 + "</value>";
            result += "<value xid=\"2\" >" + under90 + "</value>";
            result += "<value xid=\"3\" >" + over90 + "</value>";
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }
	
}
