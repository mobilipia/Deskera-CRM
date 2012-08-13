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
package com.krawler.spring.crm.campaignModule;

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Header;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CampaignLog;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.TargetList;
import com.krawler.crm.database.tables.TargetListTargets;
import com.krawler.utils.json.base.JSONException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.emailMarketing.CampaignConstants;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author Karthik
 */
public class crmCampaignReportController extends MultiActionController implements MessageSourceAware{

    private crmCampaignReportDAO crmCampaignReportDAOObj;
    private crmCampaignDAO crmCampaignDAOObj;
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
	public void setCrmCampaignDAO(crmCampaignDAO crmCampaignDAOObj) {
        this.crmCampaignDAOObj = crmCampaignDAOObj;
    }
    public crmCampaignReportDAO getcrmCampaignReportDAO(){
        return crmCampaignReportDAOObj;
    }
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setcrmCampaignReportDAO(crmCampaignReportDAO crmCampaignReportDAOObj1) {
        this.crmCampaignReportDAOObj = crmCampaignReportDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    
    public JSONObject getCampaignReportJson(List ll, HttpServletRequest request, boolean export, int totalSize, DateFormat dateFormat) throws ServiceException {
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
                CrmCampaign obj = (CrmCampaign) ite.next();

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("campaignid", obj.getCampaignid());
                tmpObj.put("objective", obj.getObjective());
                tmpObj.put("active", obj.getActive());
                tmpObj.put("actualcost", obj.getActualcost());
                tmpObj.put("budgetedcost", obj.getBudgetedcost());
                tmpObj.put("campaignname", obj.getCampaignname());
                tmpObj.put("campaignownerid", obj.getUsersByUserid().getUserID());
                tmpObj.put("owner", obj.getUsersByUserid().getFirstName() + "" + obj.getUsersByUserid().getLastName());
                tmpObj.put("enddate",obj.getEndingdate()!=null?obj.getEndingdate():"");
                tmpObj.put("startdate",obj.getStartingdate()!=null?obj.getStartingdate():"");
                tmpObj.put("expectedresponse", obj.getExpectedresponse());
                tmpObj.put("expectedrevenue", obj.getExpectedrevenue());
                tmpObj.put("numsent", obj.getNumsent());
                tmpObj.put("numsent", obj.getNumsent());
                tmpObj.put("createdon",obj.getCreatedOn() == null? "":obj.getCreatedOn());
                tmpObj.put("updatedon", obj.getUpdatedOn() == null? "":obj.getUpdatedOn());
                tmpObj.put("validflag", obj.getValidflag());
                tmpObj.put("campaignstatusid", crmManagerCommon.comboNull(obj.getCrmCombodataByCampaignstatusid()));
                tmpObj.put("status", (obj.getCrmCombodataByCampaignstatusid() != null ? obj.getCrmCombodataByCampaignstatusid().getValue() : ""));
                tmpObj.put("campaigntypeid", crmManagerCommon.comboNull(obj.getCrmCombodataByCampaigntypeid()));
                tmpObj.put("type", (obj.getCrmCombodataByCampaigntypeid() != null ? obj.getCrmCombodataByCampaigntypeid().getValue() : ""));

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Campaign");
                Iterator ite1 = ll1.iterator();

                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Campaign",companyid);
                    if(StringUtil.equal(Header.CAMPAIGNNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.campaignname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.CAMPAIGNNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "campaignname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNSTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.status", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.status", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "status");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.campaignreport.campaigntype", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.campaignreport.campaigntype", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNOBJECTIVEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.objective", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip",  StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.objective", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "objective");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNSTARTDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.startdate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.startdate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "startdate");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNENDDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.enddate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.enddate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "enddate");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNRESPONSEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.response", null, RequestContextUtils.getLocale(request))+"(%)":newHeader+"(%)");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.response", null, RequestContextUtils.getLocale(request))+"(%)":newHeader+"(%)");
                        jobjTemp.put("align", "right");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "expectedresponse");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
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
                jobjTemp.put("name", "campaignname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "objective");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "startdate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "enddate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "status");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "expectedresponse");
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
            logger.warn(e.getMessage(),e);
        }
        return commData;
    }
    public JSONObject getcampaignByTypeReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) throws ServiceException {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmCampaign obj = (CrmCampaign) ite.next();

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("campaignid", obj.getCampaignid());
                tmpObj.put("objective", obj.getObjective());
                tmpObj.put("active", obj.getActive());
                tmpObj.put("actualcost", obj.getActualcost());
                tmpObj.put("budgetedcost", obj.getBudgetedcost());
                tmpObj.put("campaignname", obj.getCampaignname());
                tmpObj.put("campaignownerid", obj.getUsersByUserid().getUserID());
                tmpObj.put("owner", obj.getUsersByUserid().getFirstName() + "" + obj.getUsersByUserid().getLastName());
                tmpObj.put("enddate",obj.getEndingdate()!=null?obj.getEndingdate():"");
                tmpObj.put("startdate",obj.getStartingdate()!=null?obj.getStartingdate():"");
                tmpObj.put("expectedresponse", obj.getExpectedresponse());
                tmpObj.put("expectedrevenue", obj.getExpectedrevenue());
                tmpObj.put("numsent", obj.getNumsent());
                tmpObj.put("numsent", obj.getNumsent());
                tmpObj.put("createdon",obj.getCreatedOn() == null? "":obj.getCreatedOn());
                tmpObj.put("updatedon", obj.getUpdatedOn() == null? "":obj.getUpdatedOn());
                tmpObj.put("validflag", obj.getValidflag());
                tmpObj.put("campaignstatusid", crmManagerCommon.comboNull(obj.getCrmCombodataByCampaignstatusid()));
                tmpObj.put("status", (obj.getCrmCombodataByCampaignstatusid() != null ? obj.getCrmCombodataByCampaignstatusid().getValue() : ""));
                tmpObj.put("campaigntypeid", crmManagerCommon.comboNull(obj.getCrmCombodataByCampaigntypeid()));
                tmpObj.put("type", (obj.getCrmCombodataByCampaigntypeid() != null ? obj.getCrmCombodataByCampaigntypeid().getValue() : ""));

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Campaign");
                Iterator ite1 = ll1.iterator();

                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Campaign",companyid);
                    if(StringUtil.equal(Header.CAMPAIGNNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.campaignname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.CAMPAIGNNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "campaignname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNSTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.status", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.status", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "status");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.campaignreport.campaigntype", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.campaignreport.campaigntype", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNOBJECTIVEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.objective", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip",  StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.objective", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "objective");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNSTARTDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.startdate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.startdate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "startdate");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNENDDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.enddate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.enddate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "enddate");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
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
                jobjTemp.put("name", "campaignname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "objective");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "startdate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "enddate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "status");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "expectedresponse");
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
            logger.warn(e.getMessage(),e);
        }
        return commData;
    }

    public ModelAndView campaignByTypeReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
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
            requestParams.put("filterCombo", StringUtil.checkForNull(request.getParameter("filterCombo")));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            KwlReturnObject kmsg = crmCampaignReportDAOObj.campaignByTypeReport(requestParams, usersList);
            jobj = getcampaignByTypeReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView campaignByTypeExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
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
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            KwlReturnObject kmsg = crmCampaignReportDAOObj.campaignByTypeReport(requestParams, usersList);
            jobj = getcampaignByTypeReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Campaign_Type,
                    "Campaign by Type Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }
    public JSONObject getcompletedCampaignByTypeReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) throws ServiceException {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmCampaign obj = (CrmCampaign) ite.next();

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("campaignid", obj.getCampaignid());
                tmpObj.put("objective", obj.getObjective());
                tmpObj.put("active", obj.getActive());
                tmpObj.put("actualcost", obj.getActualcost());
                tmpObj.put("budgetedcost", obj.getBudgetedcost());
                tmpObj.put("campaignname", obj.getCampaignname());
                tmpObj.put("campaignownerid", obj.getUsersByUserid().getUserID());
                tmpObj.put("owner", obj.getUsersByUserid().getFirstName() + "" + obj.getUsersByUserid().getLastName());
                tmpObj.put("enddate",obj.getEndingdate()!=null?obj.getEndingdate():"");
                tmpObj.put("startdate", obj.getStartingdate()!=null?obj.getStartingdate():"");
                tmpObj.put("expectedresponse", obj.getExpectedresponse());
                tmpObj.put("expectedrevenue", obj.getExpectedrevenue());
                tmpObj.put("numsent", obj.getNumsent());
                tmpObj.put("numsent", obj.getNumsent());
                tmpObj.put("createdon",obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("updatedon", obj.getUpdatedOn() == null? "": obj.getUpdatedOn());
                tmpObj.put("validflag", obj.getValidflag());
                tmpObj.put("campaignstatusid", crmManagerCommon.comboNull(obj.getCrmCombodataByCampaignstatusid()));
                tmpObj.put("status", (obj.getCrmCombodataByCampaignstatusid() != null ? obj.getCrmCombodataByCampaignstatusid().getValue() : ""));
                tmpObj.put("campaigntypeid", crmManagerCommon.comboNull(obj.getCrmCombodataByCampaigntypeid()));
                tmpObj.put("type", (obj.getCrmCombodataByCampaigntypeid() != null ? obj.getCrmCombodataByCampaigntypeid().getValue() : ""));

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Campaign");
                Iterator ite1 = ll1.iterator();

                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Campaign",companyid);
                    if(StringUtil.equal(Header.CAMPAIGNNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.campaignname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.CAMPAIGNNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header",Hdr );
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "campaignname");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNSTATUSHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.status", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.status", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "status");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.campaignreport.campaigntype", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.campaignreport.campaigntype", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "type");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNOBJECTIVEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.objective", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip",  StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.objective", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "objective");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNSTARTDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.startdate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.startdate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "startdate");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNENDDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.enddate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.enddate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "enddate");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CAMPAIGNCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.campaign.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("xtype","datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        jarrColumns.put(jobjTemp);
                   }	
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "campaignname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "objective");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "startdate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "enddate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
               jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "status");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "expectedresponse");
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
            logger.warn(e.getMessage(),e);
        }
        return commData;
    }

    public ModelAndView completedCampaignByTypeReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
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
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            KwlReturnObject kmsg = crmCampaignReportDAOObj.completedCampaignByTypeReport(requestParams, usersList);
            jobj = getcompletedCampaignByTypeReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView completedCampaignByTypeExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
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
            requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
            requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            KwlReturnObject kmsg = crmCampaignReportDAOObj.completedCampaignByTypeReport(requestParams, usersList);
            jobj = getcompletedCampaignByTypeReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Completed_Campaigns,
                    "Completed Campaigns by Type Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView campaignWithGoodResponseReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
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
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            KwlReturnObject kmsg = crmCampaignReportDAOObj.campaignWithGoodResponseReport(requestParams, usersList);
            jobj = getCampaignReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(),dateFormat);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView campaignWithGoodResponseExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
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
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            KwlReturnObject kmsg = crmCampaignReportDAOObj.campaignWithGoodResponseReport(requestParams, usersList);
            jobj = getCampaignReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(),dateFormat);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Camp_Good_Res,
                    "Campaigns with Good Response Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView campaignResponsePieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        List ll = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
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
            result =getChartView(result,requestParams, usersList,"Pie");
            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView campaignResponseBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        List ll = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
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
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                result += "<value xid=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "<value xid=\"Undefined\" >Undefined</value>";
            result += "</series><graphs><graph gid=\"0\">";
            
            result =getChartView(result,requestParams, usersList,"Bar");
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }
    private String getChartView(String result,HashMap<String, Object> requestParams,StringBuffer usersList,String bartype) throws ServiceException{
            KwlReturnObject kmsg = null;
            kmsg = crmCampaignReportDAOObj.getCampaignResponseChart("ALL", requestParams, usersList);
            List lst = kmsg.getEntityList();
            Iterator itr = lst.iterator();
            while(itr.hasNext()) {
                Object[] row = (Object[]) itr.next();
                if(bartype.equals("Pie"))
                    result += getPieChartView(row[0].toString(),row[1].toString());
                else
                    result += getBarChartView(row[0].toString(),row[1].toString());
            }
            kmsg = crmCampaignReportDAOObj.getCampaignResponseChart("Undefined", requestParams, usersList);
            if (kmsg.getRecordTotalCount() > 0) {
                if(bartype.equals("Pie"))
                    result  = result +  getPieChartView("Undefined",kmsg.getRecordTotalCount());
                else
                    result  = result +  getBarChartView("Undefined",kmsg.getRecordTotalCount());
            }
            return result;
    }
    private String getBarChartView(String id,Object value){
         return "<value xid=\"" + id + "\" >" + value + "</value>";
    }
    private String getPieChartView(String id,Object value){
       return "<slice title=\"" + id  + "\">" + value + "</slice>";
    }
    public ModelAndView campaignTypePieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
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
                kmsg = crmCampaignReportDAOObj.getCampaignTypeChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
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

    public ModelAndView campaignTypeBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
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
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < max; k++) {
                kmsg = crmCampaignReportDAOObj.getCampaignTypeChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView completedCampaignPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
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
                kmsg = crmCampaignReportDAOObj.getCompletedCampaignChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
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

    public ModelAndView completedCampaignBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
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
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < max; k++) {
                kmsg = crmCampaignReportDAOObj.getCompletedCampaignChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", result);
    }

     public ModelAndView getCampaignMailStatusChart(HttpServletRequest request, HttpServletResponse response) throws ServiceException,SessionExpiredException, JSONException {
        JSONObject jobj = new JSONObject();
        JSONObject sentJobj = new JSONObject();
        JSONObject viewJobj = new JSONObject();
        JSONObject optedJobj = new JSONObject();
        JSONObject failedJobj = new JSONObject();
        KwlReturnObject res = null;
        String result = "";
        try {
            String campID = request.getParameter("campID");
            String mailMarID = request.getParameter("mailMarID");
            String targetlistid = request.getParameter("targetid");

            int leadSent = 0;
            int contSent = 0;
            int userSent = 0;
            int targSent = 0;

            int leadView = 0;
            int contView = 0;
            int userView = 0;
            int targView = 0;

            int leadOpted = 0;
            int contOpted = 0;
            int userOpted = 0;
            int targOpted = 0;

            int leadFailed = 0;
            int contFailed =0;
            int userFailed = 0;
            int targFailed =0;


            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList fnames = new ArrayList(Arrays.asList("campaignid.campaignid","emailmarketingid.id")),fvalues = new ArrayList(Arrays.asList(campID, mailMarID));
            if(!StringUtil.isNullOrEmpty(targetlistid)){
            	fnames.add("targetlistid.id");
            	fvalues.add(targetlistid);
            }
            requestParams.put("filter_names", fnames);
            requestParams.put("filter_values", fvalues);
            res = crmCampaignDAOObj.getCampaignLog(requestParams);
            List CLll = res.getEntityList();
            Iterator ite = CLll.iterator();
            while (ite.hasNext()) {
                CampaignLog cl = (CampaignLog) ite.next();
                TargetList tl = cl.getTargetlistid();
                TargetListTargets tlt = cl.getTargetid();
                int views = cl.getViewed()>0?1:0;
                String activityType = cl.getActivitytype();
                int failed = cl.getSendingfailed();
                switch (tlt.getRelatedto()) {
                    case 1:
                        leadView += views;
                        leadSent++;
                        if (activityType.equals(CampaignConstants.Crm_isunsubscribe)) {
                            leadOpted += 1;
                        }
                        if(failed == 1){
                            leadFailed +=1;
                        }
                        break;
                    case 2:
                        contView += views;
                        contSent++;
                        if (activityType.equals(CampaignConstants.Crm_isunsubscribe)) {
                            contOpted += 1;
                        }
                         if(failed == 1){
                            contFailed +=1;
                        }
                        break;
                    case 3:
                        userView += views;
                        userSent++;
                        if (activityType.equals(CampaignConstants.Crm_isunsubscribe)) {
                            userOpted += 1;
                        }
                         if(failed == 1){
                            userFailed +=1;
                        }
                        break;
                    case 4:
                        targView += views;
                        targSent++;
                        if (activityType.equals(CampaignConstants.Crm_isunsubscribe)) {
                            targOpted += 1;
                        }
                         if(failed == 1){
                            targFailed +=1;
                        }
                        break;
                    default:
                        break;
                }
            }
            sentJobj.put("LeadsSent", leadSent);
            sentJobj.put("ContactsSent", contSent);
            sentJobj.put("UsersSent", userSent);
            sentJobj.put("TargetsSent", targSent);

            viewJobj.put("LeadsView", leadView);
            viewJobj.put("ContactsView", contView);
            viewJobj.put("UsersView", userView);
            viewJobj.put("TargetsView", targView);

            optedJobj.put("LeadsOpted", leadOpted);
            optedJobj.put("ContactsOpted", contOpted);
            optedJobj.put("UsersOpted", userOpted);
            optedJobj.put("TargetsOpted", targOpted);

            failedJobj.put("LeadsFailed", leadFailed);
            failedJobj.put("ContactsFailed", contFailed);
            failedJobj.put("UsersFailed", userFailed);
            failedJobj.put("TargetsFailed", targFailed);

            jobj.put("Sent", sentJobj);
            jobj.put("View", viewJobj);
            jobj.put("Opted", optedJobj);
            jobj.put("Failed", failedJobj);
            jobj.put("success", true);
            String[] array = {"Leads", "Contacts", "Users", "Targets"};
            String[] status = {"Sent", "View", "Opted","Failed"};
            result = "<chart><series>";
            for (int i = 0; i < array.length; i++) {
                result += "<value xid='" + i + "'>" + array[i] + "</value>";
            }
            result += "</series><graphs>";
            
            for (int gid = 0; gid < status.length; gid++) {
                JSONObject sts = jobj.getJSONObject(status[gid]);
                result += "<graph gid='" + gid + "'>";
                for (int xid = 0; xid < array.length; xid++) {
                    result += "<value xid='" + xid + "'>";
                    result += sts.getString(array[xid] + status[gid]);
                    result += "</value>";
                }
                result += "</graph>";
            }
            result += "</graphs></chart>";
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        } 
        return new ModelAndView("chartView", "model", result);
    }

}
