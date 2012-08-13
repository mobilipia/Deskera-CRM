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
package com.krawler.spring.crm.opportunityModule;

import com.krawler.baseline.crm.bizservice.el.ExpressionVariables;
import com.krawler.common.util.Header;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.User;
import com.krawler.crm.utils.Constants;
import com.krawler.common.utils.DateUtil;
import com.krawler.crm.database.tables.CrmOpportunityCustomData;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.opportunity.bizservice.OpportunityManagementService;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.service.IChartService;
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
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.leadModule.crmLeadHandler;
import com.krawler.spring.crm.productModule.crmProductDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

public class crmOpportunityReportController extends MultiActionController implements MessageSourceAware {

    private crmOpportunityReportDAO opportunityReportDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmProductDAO crmProductDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private crmCommonDAO crmCommonDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private IChartService chartServiceObj;
    private OpportunityManagementService opportunityManagementService;
    private fieldDataManager fieldDataManagercntrl;
    private MessageSource messageSource;
    
    
    public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    public void setChartService(IChartService IChartServiceObj) {
        this.chartServiceObj = IChartServiceObj;
    }
    
    public crmOpportunityReportDAO getcrmOpportunityReportDAO(){
        return opportunityReportDAOObj;
    }

    public void setOpportunityManagementService(OpportunityManagementService opportunityManagementService) {
        this.opportunityManagementService = opportunityManagementService;
    }

    public OpportunityManagementService getOpportunityManagementService() {
        return this.opportunityManagementService;
    }

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
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

    public void setcrmOpportunityReportDAO(crmOpportunityReportDAO opportunityReportDAOObj1) {
        this.opportunityReportDAOObj = opportunityReportDAOObj1;
    }

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }

    public void setcrmProductDAO(crmProductDAO crmProductDAOObj1) {
        this.crmProductDAOObj = crmProductDAOObj1;
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

    public JSONObject getOpportunityReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String head = request.getParameter("head");
            String sourceflag = request.getParameter("sf");
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String currencySymbol = crmManagerDAOObj.currencySymbol(currencyid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();
                String[] ownerInfo=crmOpportunityHandler.getAllOppOwners(crmOpportunityDAOObj, obj.getOppid());
                tmpObj.put("oppowner", ownerInfo[4]);
                tmpObj.put("exportmultiowners", ownerInfo[5]);

                tmpObj.put("oppname", obj.getOppname());
                tmpObj.put("closingdate", obj.getClosingdate()!=null?obj.getClosingdate():"");
                tmpObj.put("createdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
                tmpObj.put("salesamount", !StringUtil.isNullOrEmpty(obj.getSalesamount()) ? crmManagerDAOObj.currencyRender(obj.getSalesamount(), currencyid) : "");
                tmpObj.put("oppstageid", crmManagerCommon.comboNull(obj.getCrmCombodataByOppstageid()));
                tmpObj.put("leadsourceid", crmManagerCommon.comboNull(obj.getCrmCombodataByLeadsourceid()));
                tmpObj.put("accountnameid", crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid"));
                tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                tmpObj.put("oppstage", (obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                String[] productInfo=crmOpportunityHandler.getOpportunityProducts(obj);
                tmpObj.put("productserviceid", productInfo[0]);
                tmpObj.put("productname", productInfo[1]);
                tmpObj.put("exportmultiproduct", productInfo[2]);
                tmpObj.put("probability", obj.getProbability());
                tmpObj.put("type", (obj.getCrmCombodataByOpptypeid() != null ? obj.getCrmCombodataByOpptypeid().getValue() : ""));
                tmpObj.put("region", (obj.getCrmCombodataByRegionid() != null ? obj.getCrmCombodataByRegionid().getValue() : ""));
                tmpObj.put("price", (obj.getPrice() != null ? obj.getPrice() : ""));
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Opportunity");
                Iterator ite1 = ll1.iterator();
                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Opportunity",companyid);
                    if(StringUtil.equal(Header.OPPORTUNITYOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.owner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip",  StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.owner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "oppowner");
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("title", "exportmultiowners");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "oppname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYACCOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.type", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip",  StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.type", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYLEADSOURCEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "leadsource");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYSTAGEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.stage", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.stage", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "oppstage");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYPROBABILITYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.probability", null, RequestContextUtils.getLocale(request))+"(%)":newHeader+"(%)");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.probability", null, RequestContextUtils.getLocale(request))+"(%)":newHeader+"(%)");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "probability");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYPRODUCTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.product", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.product", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "productname");
                        jobjTemp.put("title", "exportmultiproduct");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYPRICEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.price", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.price", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "price");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYSALESAMOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.salesammount", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.salesammount", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "salesamount");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYCLOSEDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.closeddate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.closeddate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("dataIndex", "closingdate");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsource");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsourceid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountnameid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "productname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "closingdate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstage");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstageid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "salesamount");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "probability");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "price");
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
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }

    public JSONObject getOppPipelinedReportJson(List ll, HttpServletRequest request, boolean export, int totalSize,List ll1,int dl1) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        String stage="0";
        try {
            double sumAmount = 0;
            Iterator ite = ll.iterator();
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            String currencySymbol = crmManagerDAOObj.currencySymbol(currencyid);
            Iterator iteSum = ll.iterator();
            Iterator ite1 = ll1.iterator();
            Map opportunityStage = new HashMap();
            while(ite1.hasNext()){
                DefaultMasterItem dm = (DefaultMasterItem) ite1.next();
                opportunityStage.put(dm.getValue(),dm.getPercentStage()!=null?dm.getPercentStage():"0");
            }
            while(iteSum.hasNext()) {
                Object[] row = (Object[]) iteSum.next();
                 stage = (String) row[1];
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
                 stage = (String) row[1];
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
                tmpObj.put("stagename", stageName);
                tmpObj.put("percent", !StringUtil.isNullOrEmpty(stage)?stage+" %":"0 %");
                tmpObj.put("count", count);
                tmpObj.put("xtype", "numberfield");
                tmpObj.put("gp", crmManagerDAOObj.currencyRender(decimalFormat.format(grossProfit), currencyid));
                tmpObj.put("amount", crmManagerDAOObj.currencyRender(decimalFormat.format(amount), currencyid));

                jarr.put(tmpObj);
                //ll1.remove(stage);
                opportunityStage.remove(stageName);
            }
                Iterator opportunityStageIte = opportunityStage.keySet().iterator();
                while(opportunityStageIte.hasNext()){
                    Object al = (Object) opportunityStageIte.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("stagename",al );
                    tmpObj.put("percent", opportunityStage.get(al).toString()+" %");
                    tmpObj.put("count", 0);
                    tmpObj.put("xtype", "numberfield");
                    tmpObj.put("gp", crmManagerDAOObj.currencyRender(decimalFormat.format(0), currencyid));
                    tmpObj.put("amount", crmManagerDAOObj.currencyRender(decimalFormat.format(0), currencyid));

                    jarr.put(tmpObj);
                
            }
            if (totalSize >= ll.size()) {
                JSONObject tmpObj = new JSONObject();
                if (export) {
                    tmpObj.put("stagename", "<div style='font-size:14px;font-weight:bold;font-family:tahoma,helvetica,sans-serif;'>TOTAL :</div>");
                } else {
                    tmpObj.put("stagename", "TOTAL :");
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
                String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,Header.OPPORTUNITYSTAGEHEADER,"Opportunity",companyid);
                jobjTemp = new JSONObject();
                String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.opportunityreport.opportunitystage", null, RequestContextUtils.getLocale(request)):newHeader;
                if(StringUtil.equal(Header.OPPORTUNITYSTAGEHEADER, qucikSerachFields)){
                    jobjTemp.put("qucikSearchText", Hdr);
                }
                jobjTemp.put("header", Hdr);
                jobjTemp.put("tip", Hdr);
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "stagename");
                jobjTemp.put("xtype", "textfield");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.report.pipelinereport.stagegrossprofit", null, RequestContextUtils.getLocale(request)));
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
                jobjTemp.put("header", messageSource.getMessage("crm.report.pipelinereport.totalnoofOB", new Object[]{messageSource.getMessage("crm.OPPORTUNITY.plural", null, RequestContextUtils.getLocale(request))}, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.report.pipelinereport.totalnoofOB", new Object[]{messageSource.getMessage("crm.OPPORTUNITY.plural", null, RequestContextUtils.getLocale(request))}, RequestContextUtils.getLocale(request)));
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
                jobjTemp.put("name", "stagename");
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

    public JSONObject getallOppPipelinedReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                String stageName = (String) row[0];
                String oppName = (String) row[1];
                String revenue = (String) row[2];
                String price = (String) row[3];
                String percent = (String) row[4];
                double amount = 0;
                double grossProfit = 0;
                if (!StringUtil.isNullOrEmpty(percent)) {
                    double percentStage = Double.parseDouble(percent);
                    double totalSalesAmount = 0;
                    double totalPrice = 0;
                    if(!StringUtil.isNullOrEmpty(revenue)) {
                        try {
                            totalSalesAmount = Double.parseDouble(revenue);
                        } catch(NumberFormatException e) {
                            totalSalesAmount = 0;
                        }
                    }
                    if(!StringUtil.isNullOrEmpty(price)) {
                        try {
                            totalPrice = Double.parseDouble(price);
                        } catch (NumberFormatException e) {
                            totalPrice = 0;
                        }
                    }
                    grossProfit = totalSalesAmount - totalPrice;
                    amount = (percentStage/100) * (totalSalesAmount - totalPrice);
                }
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("oppname", oppName);
                tmpObj.put("stagename", stageName);
                tmpObj.put("xtype", "numberfield");
                tmpObj.put("gp", crmManagerDAOObj.currencyRender(decimalFormat.format(grossProfit), currencyid));
                tmpObj.put("amount", crmManagerDAOObj.currencyRender(decimalFormat.format(amount), currencyid));

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,Header.OPPORTUNITYNAMEHEADER,"Opportunity",companyid);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader);
                jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader);
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "oppname");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,Header.OPPORTUNITYSTAGEHEADER,"Opportunity",companyid);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.opportunityreport.opportunitystage", null, RequestContextUtils.getLocale(request)):newHeader);
                jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.opportunityreport.opportunitystage", null, RequestContextUtils.getLocale(request)):newHeader);
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "stagename");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header",  messageSource.getMessage("crm.report.pipelinereport.grossprofit", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip",  messageSource.getMessage("crm.report.pipelinereport.grossprofit", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("align", "right");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("dataIndex", "gp");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header",  messageSource.getMessage("crm.report.pipelinereport.pipelinevalue", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip",  messageSource.getMessage("crm.report.pipelinereport.pipelinevalue", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("align", "right");
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "amount");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "stagename");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "gp");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "amount");
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

    public ModelAndView revenueByOppSourceReport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to",request.getParameter("to"));
            }
            requestParams.put("filterCombo", StringUtil.checkForNull(request.getParameter("filterCombo")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.revenueByOppSourceReport(requestParams, usersList);
            jobj = getrevenueByOppSourceReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView revenueByOppSourceExport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.revenueByOppSourceReport(requestParams, usersList);
            request.setAttribute("sf", 1);
            jobj = getrevenueByOppSourceReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Revenue_by_OppSource,
                    "Revenue by Opportunity Source Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }
    public JSONObject getoppByStageReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String head = request.getParameter("head");
            String sourceflag = request.getParameter("sf");
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String currencySymbol = crmManagerDAOObj.currencySymbol(currencyid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();

                tmpObj.put("oppname", obj.getOppname());
                tmpObj.put("closingdate",obj.getClosingdate()!=null?obj.getClosingdate():"");
                tmpObj.put("salesamount", !StringUtil.isNullOrEmpty(obj.getSalesamount()) ? crmManagerDAOObj.currencyRender(obj.getSalesamount(), currencyid) : "");
                tmpObj.put("oppstageid", crmManagerCommon.comboNull(obj.getCrmCombodataByOppstageid()));
                tmpObj.put("leadsourceid", crmManagerCommon.comboNull(obj.getCrmCombodataByLeadsourceid()));
                tmpObj.put("accountnameid", crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid"));
                tmpObj.put("createdon",obj.getCreatedOn()!=null?obj.getCreatedOn():"");
                tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                tmpObj.put("oppstage", (obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("probability", obj.getProbability());

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Opportunity");
                Iterator ite1 = ll1.iterator();
                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Opportunity",companyid);
                    if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr );
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "oppname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYACCOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYSTAGEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.stage", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.stage", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "oppstage");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }  else if(StringUtil.equal(Header.OPPORTUNITYSALESAMOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.salesammount", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.salesammount", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "salesamount");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYCLOSEDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.opportunityreport.expectedclosedate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.opportunityreport.expectedclosedate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "closingdate");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
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
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsource");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsourceid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountnameid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "productname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "closingdate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstage");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstageid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "salesamount");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "probability");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "price");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdon");
                jobjTemp.put("type", "date");
                jobjTemp.put("dateFormat", "time");
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
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }

    public ModelAndView oppByStageReport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to",request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("filterCombo", StringUtil.checkForNull(request.getParameter("filterCombo")));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.oppByStageReport(requestParams, usersList);
            jobj = getoppByStageReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView oppByStageExport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.oppByStageReport(requestParams, usersList);
            request.setAttribute("sf", "");
            jobj = getoppByStageReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Opp_by_Stage,
                    "Opportunity by Stage Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }


    public JSONObject getoppBySalesPersonReportJson(List<CrmOpportunity> ll, HttpServletRequest request, boolean export, int totalSize, HashMap<String, String> countMap) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            boolean customFlag = false;
            //Get Custom columns.
             KwlReturnObject kr = crmCommonDAOObj.getCustomColumnHeader("44", companyid);
             List<Object[]> customColList = kr.getEntityList();
             if(customColList.size() > 0) {
                customFlag = true;
             }

            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Opportunity");
                for (DefaultHeader obj: ll1) {
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Opportunity",companyid);
                    if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header",Hdr );
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "oppname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.owner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip",  StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.owner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "oppowner");
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("title", "exportmultiowners");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYSTAGEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.stage", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.stage", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "oppstage");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYCLOSEDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.opportunityreport.closingdate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.opportunityreport.closingdate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("dataIndex", "closingdate");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.type", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.type", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("header",  messageSource.getMessage("crm.report.opportunityreport.oppbysalesperson.noofenq", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip",  messageSource.getMessage("crm.report.opportunityreport.oppbysalesperson.noofenq", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "oppcount");
                jobjTemp.put("align", "right");
                jarrColumns.put(jobjTemp);

                //Build custom column json list.
                 for (Object[] obj: customColList) {
                    jobjTemp = new JSONObject();
                    jobjTemp.put("header", obj[0]);
                    jobjTemp.put("tip", obj[1]);
                    jobjTemp.put("pdfwidth", 60);
                    jobjTemp.put("dataIndex", obj[2]);
                    jobjTemp.put("align", "right");
                    StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                    jarrColumns.put(jobjTemp);

                    jobjTemp = new JSONObject();
                    jobjTemp.put("name", obj[2]);
                    jarrRecords.put(jobjTemp);
                 }

                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "closingdate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstage");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstageid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "opptypeid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppcount");
                jarrRecords.put(jobjTemp);
                commData.put("columns", jarrColumns);
                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }

            HashMap<String, CrmOpportunityCustomData> oppCustomDataMap = new HashMap<String, CrmOpportunityCustomData>();
            HashMap<String, Integer> FieldMap = new HashMap<String, Integer>();
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            Map<String, ExpressionVariables> exprVarMap = new HashMap<String, ExpressionVariables>();
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            boolean isexport = false;
            if(customFlag) {
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("isexport",isexport);
                requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
                requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_opportunity_moduleid));
                FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);

                List<String> idsList = new ArrayList<String>();
                for (CrmOpportunity obj : ll) {
                   idsList.add(obj.getOppid());
                }
                oppCustomDataMap = crmOpportunityDAOObj.getOpportunityCustomDataMap(idsList, companyid);
                exprVarMap = getOpportunityManagementService().expressionVariableMap(ll, oppCustomDataMap, companyid, FieldMap, replaceFieldMap, export, dateFormat);
            }

            for (CrmOpportunity obj : ll) {
                JSONObject tmpObj = new JSONObject();
                String[] ownerInfo=crmOpportunityHandler.getAllOppOwners(crmOpportunityDAOObj, obj.getOppid());
                tmpObj.put("oppowner", ownerInfo[4]);
                tmpObj.put("exportmultiowners", ownerInfo[5]);

                tmpObj.put("oppname", obj.getOppname());
                tmpObj.put("closingdate", obj.getClosingdate()!=null?obj.getClosingdate():"");
                tmpObj.put("oppstageid", crmManagerCommon.comboNull(obj.getCrmCombodataByOppstageid()));                                                
                tmpObj.put("oppstage", (obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : ""));
                tmpObj.put("opptypeid", crmManagerCommon.comboNull(obj.getCrmCombodataByOpptypeid()));
                tmpObj.put("type", (obj.getCrmCombodataByOpptypeid() != null ? obj.getCrmCombodataByOpptypeid().getValue() : ""));
                tmpObj.put("oppcount", countMap.containsKey(ownerInfo[2])?countMap.get(ownerInfo[2]):0);

                if(customFlag) {//Get custom columns data                    
                    getOpportunityManagementService().getCustomColumnJSON(obj, tmpObj, FieldMap, oppCustomDataMap, exprVarMap);
                }
                jarr.put(tmpObj);
            }
            
            commData.put("coldata", jarr);

            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }

    public ModelAndView oppBySalesPersonReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String tZStr = sessionHandlerImpl.getTimeZoneDifference(request);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + tZStr));

            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
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
//            if (!StringUtil.isNullOrEmpty(request.getParameter("cd"))) {
//                int chk = Integer.parseInt(request.getParameter("cd"));
//                if (chk == 1) {
//                    Date fromDate = new Date(StringUtil.checkForNull(request.getParameter("frm")));
//                    Date toDate = new Date(StringUtil.checkForNull(request.getParameter("to")));
//                    fromDate = sdf.parse(sdf.format(fromDate));
//                    toDate = sdf.parse(sdf.format(toDate));
//                    long startDate = DateUtil.getStrippedDateAsLong(fromDate, 0);
//                    long endDate = DateUtil.getStrippedDateAsLong(toDate, 0);
//                    requestParams.put("frm", startDate);
//                    requestParams.put("to", endDate);
//                }
//            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = opportunityReportDAOObj.oppBySalesPersonReport(requestParams, usersList);

            List<Object[]> ll = opportunityReportDAOObj.oppBySalesPersonCountList(requestParams, usersList);
            HashMap<String, String> countMap = new HashMap<String, String>();
            Object count = 0;
            for(Object[] row : ll) {
                count = (Object) row[0];
                User userObj = (User) row[1];
                countMap.put(userObj.getUserID(), String.valueOf(count));
            }

            jobj = getoppBySalesPersonReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), countMap);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView oppBySalesPersonExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String tZStr = sessionHandlerImpl.getTimeZoneDifference(request);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + tZStr));
            
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
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
//            if (!StringUtil.isNullOrEmpty(request.getParameter("cd"))) {
//                int chk = Integer.parseInt(request.getParameter("cd"));
//                if (chk == 1) {
//                    Date fromDate = new Date(StringUtil.checkForNull(request.getParameter("frm")));
//                    Date toDate = new Date(StringUtil.checkForNull(request.getParameter("to")));
//                    fromDate = sdf.parse(sdf.format(fromDate));
//                    toDate = sdf.parse(sdf.format(toDate));
//                    long startDate = DateUtil.getStrippedDateAsLong(fromDate, 0);
//                    long endDate = DateUtil.getStrippedDateAsLong(toDate, 0);
//                    requestParams.put("frm", startDate);
//                    requestParams.put("to", endDate);
//                }
//            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = opportunityReportDAOObj.oppBySalesPersonReport(requestParams, usersList);

            List<Object[]> ll = opportunityReportDAOObj.oppBySalesPersonCountList(requestParams, usersList);
            HashMap<String, String> countMap = new HashMap<String, String>();
            Object count = 0;
            for(Object[] row : ll) {
                count = (Object) row[0];
                User userObj = (User) row[1];
                countMap.put(userObj.getUserID(), String.valueOf(count));
            }

            request.setAttribute("sf", "");
            jobj = getoppBySalesPersonReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), countMap);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Opp_by_SalesPerson,
                    "Opportunity by Sales Person Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public JSONObject getoppByRegionHReportJson(List<Object[]> ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {   
        	int sumOppcount =0;
        	int sumFinaloppcount=0;
        	double sumSalesamount=0;
           	String currencyid = sessionHandlerImpl.getCurrencyID(request);
           	String currencySymbol=crmManagerDAOObj.currencySymbol(currencyid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            boolean customFlag = false;
            //Get Custom columns.
             KwlReturnObject kr = crmCommonDAOObj.getCustomColumnHeader("45", companyid);
             List<Object[]> customColList = kr.getEntityList();
             if(customColList.size() > 0) {
                customFlag = true;
             }

            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Opportunity");
                for (DefaultHeader obj: ll1) {
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Opportunity",companyid);
                    if(StringUtil.equal(Header.OPPORTUNITYOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.owner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip",  StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.owner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "oppowner");
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("title", "oppowner");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYREGIONHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.region", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.OPPORTUNITYREGIONHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "oppregion");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("header",  messageSource.getMessage("crm.report.opportunityreport.totalnoopp", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip",  messageSource.getMessage("crm.report.opportunityreport.totalnoopp.ttip", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("dataIndex", "oppcount");
                jobjTemp.put("align", "right");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header",  messageSource.getMessage("crm.report.opportunityreport.nofinalstageopp", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip",  messageSource.getMessage("crm.report.opportunityreport.nofinalstageopp.ttip", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("dataIndex", "finaloppcount");
                jobjTemp.put("align", "right");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header",  messageSource.getMessage("crm.report.opportunityreport.salesvalue", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")");
                jobjTemp.put("tip",  messageSource.getMessage("crm.report.opportunityreport.salesvalue", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("dataIndex", "salesamount");
                jobjTemp.put("align", "right");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);

                //Build custom column json list.
                 for (Object[] obj: customColList) {
                    jobjTemp = new JSONObject();
                    jobjTemp.put("header", obj[0]);
                    jobjTemp.put("tip", obj[1]);
                    jobjTemp.put("pdfwidth", 60);
                    jobjTemp.put("dataIndex", obj[2]);
                    jobjTemp.put("align", "right");
                    StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                    jarrColumns.put(jobjTemp);

                    jobjTemp = new JSONObject();
                    jobjTemp.put("name", obj[2]);
                    jarrRecords.put(jobjTemp);
                 }

                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppregion");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppregionid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppcount");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "finaloppcount");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "salesamount");
                jarrRecords.put(jobjTemp);
                commData.put("columns", jarrColumns);
                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
         
            String cd = StringUtil.checkForNull(request.getParameter("cd"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            for (Object[] obj : ll) {
                JSONObject tmpObj = new JSONObject();
                String regionid = (obj[0] != null)?obj[0].toString():"";
                String ownerid = obj[2].toString();
                tmpObj.put("oppregionid", regionid);
                tmpObj.put("oppregion", obj[1]);
                tmpObj.put("oppowner", obj[3]);
                tmpObj.put("oppcount", obj[5]);//countMap.containsKey(obj[2])?countMap.get(obj[2].toString()):0);

                requestParams.clear();
                requestParams.put("cd", cd);
                if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
                	requestParams.put("frm", request.getParameter("frm"));
                	requestParams.put("to", request.getParameter("to"));
                }
                requestParams.put("companyid", companyid);
                requestParams.put("ownerid", ownerid);
                requestParams.put("regionid", regionid);
                long finalStageOppCount = opportunityReportDAOObj.oppByRegionwiseFinalStageCount(requestParams);
                sumFinaloppcount+=finalStageOppCount;
                sumSalesamount+=((Number)obj[4]).doubleValue();
                sumOppcount+=((Number)obj[5]).intValue();
                tmpObj.put("finaloppcount", finalStageOppCount);
                tmpObj.put("salesamount", crmManagerDAOObj.currencyRender(decimalFormat.format(obj[4]), currencyid));//crmManagerDAOObj.currencyRender(decimalFormat.format(obj[4].toString()), currencyid));
                if(customFlag) {//For secutech company
                    for(Object[] customObj: customColList) {
                        if(Integer.parseInt(customObj[3].toString()) == 1) {//If average flag is set for the column
                            requestParams.clear();
                            requestParams.put("cd", cd);
                            requestParams.put("frm", request.getParameter("frm"));
                            requestParams.put("to", request.getParameter("to"));
                            requestParams.put("companyid", companyid);
                            requestParams.put("ownerid", ownerid);
                            requestParams.put("regionid", regionid);
                            requestParams.put("fieldParamId", customObj[4]);
                            Object avgGM = opportunityReportDAOObj.getCustomColumnAvgGM(requestParams);
                            if (avgGM == null){
                            	tmpObj.put(customObj[2].toString()," ");
                            }else{
                             	tmpObj.put(customObj[2].toString(), avgGM +" %");
                            }
                        }
                    }
                }
                
                jarr.put(tmpObj);
            }
            if (totalSize >= ll.size()) {
                JSONObject tmpObj = new JSONObject();
                if (export) {
                tmpObj.put("oppregion", "<div style='font-size:14px;font-weight:bold;font-family:tahoma,helvetica,sans-serif;'>TOTAL :</div>");
                tmpObj.put("oppcount", "<div style='font-size:14px;font-weight:bold;'>" + sumOppcount + "</div>");
                tmpObj.put("finaloppcount", "<div style='font-size:14px;font-weight:bold;'>" + sumFinaloppcount + "</div>");
                tmpObj.put("salesamount", "<div style='font-size:14px;font-weight:bold;'>" + crmManagerDAOObj.currencyRender(decimalFormat.format(sumSalesamount), currencyid));
                }
                else{
                    tmpObj.put("oppregion","TOTAL:");
                    tmpObj.put("oppcount",  sumOppcount);
                    tmpObj.put("finaloppcount",  sumFinaloppcount);
                    tmpObj.put("salesamount",  crmManagerDAOObj.currencyRender(decimalFormat.format(sumSalesamount), currencyid));
                }
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }

    public ModelAndView oppByRegionHReport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = opportunityReportDAOObj.oppByRegionHReport(requestParams, usersList);

            jobj = getoppByRegionHReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView oppByRegionHExport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = opportunityReportDAOObj.oppByRegionHReport(requestParams, usersList);
            request.setAttribute("sf", "");
            jobj = getoppByRegionHReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Opp_by_Region,
                    "Opportunity by Region Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public JSONObject getoppByRegionFunnelReportJson(List<Object[]> ll, HttpServletRequest request, boolean export, int totalSize, HashMap<String, String> q1Map, HashMap<String, String> q2Map) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            if (export) {
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.opportunity.defaultheader.region", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.opportunity.defaultheader.region", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "combo");
                jobjTemp.put("dataIndex", "oppregion");
//                jobjTemp.put("align", "right");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header",messageSource.getMessage("crm.report.opportunityreport.totalsalesfunnel", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.report.opportunityreport.totalsalesfunnel", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("dataIndex", "totalsales");
                jobjTemp.put("align", "right");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header",messageSource.getMessage("crm.report.opportunityreport.projectionnextquarter", null, RequestContextUtils.getLocale(request))); 
                jobjTemp.put("tip", messageSource.getMessage("crm.report.opportunityreport.projectionnextquarter", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "projQ1");
                jobjTemp.put("align", "right");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header",messageSource.getMessage("crm.report.opportunityreport.projectionnexttonextquarter", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("tip", messageSource.getMessage("crm.report.opportunityreport.projectionnexttonextquarter", null, RequestContextUtils.getLocale(request)));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "projQ2");
                jobjTemp.put("align", "right");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppregion");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppregionid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "totalsales");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "projQ1");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "projQ2");
                jarrRecords.put(jobjTemp);
                commData.put("columns", jarrColumns);
                
                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            
            for (Object[] obj : ll) {
                JSONObject tmpObj = new JSONObject();
                String regionid = (obj[0] != null)?obj[0].toString():"None";
                String region = (obj[1] != null)?obj[1].toString():"None";
                tmpObj.put("oppregionid", regionid);
                tmpObj.put("oppregion", region);
                tmpObj.put("totalsales", obj[2]);
                tmpObj.put("projQ1", q1Map.containsKey(regionid)?q1Map.get(regionid):0);//obj[5]);
                tmpObj.put("projQ2", q2Map.containsKey(regionid)?q2Map.get(regionid):0);//obj[4]);
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);

            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }

    public ModelAndView oppByRegionFunnelReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String export = request.getParameter("reportid");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            
            Date todayDate = new Date();
            int stmonth = getCurrentDateQuarter(todayDate);
            
            Calendar startcal = Calendar.getInstance();
            startcal.set(startcal.HOUR, 0);
            startcal.set(startcal.SECOND, 0);
            startcal.set(startcal.MINUTE, 0);

            int year = startcal.get(startcal.YEAR);
            int month = todayDate.getMonth();
            if(month == 0 || month == 1) {//Jan, Feb
                year = year - 1;
            }
            startcal.set(year, stmonth, 1);
            Long startDt = startcal.getTimeInMillis();
            Long endDt = startcal.getTimeInMillis();

            startcal.add(startcal.MONTH, 3);
            startDt = startcal.getTimeInMillis();
            startcal.add(startcal.MONTH, 3);
            endDt = startcal.getTimeInMillis();
            requestParams.put("cd", 1);
            requestParams.put("frm", startDt);
            requestParams.put("to", endDt);
            requestParams.put("projFlag", true);
            List<Object[]> ll1 = opportunityReportDAOObj.oppByRegionFunnelReport(requestParams, usersList).getEntityList();
            HashMap<String, String> q1Map = new HashMap<String, String>();
            Object count = 0;
            for(Object[] row : ll1) {
                count = (Object) row[2];
                String regionid = (row[0]!=null)?row[0].toString():"None";
                q1Map.put(regionid, String.valueOf(count));
            }

            startDt = endDt;
            startcal.add(startcal.MONTH, 3);
            endDt = startcal.getTimeInMillis();
            requestParams.put("cd", 1);
            requestParams.put("frm", startDt);
            requestParams.put("to", endDt);
            requestParams.put("projFlag", true);
            List<Object[]> ll2 = opportunityReportDAOObj.oppByRegionFunnelReport(requestParams, usersList).getEntityList();
            HashMap<String, String> q2Map = new HashMap<String, String>();
            count = 0;
            for(Object[] row : ll2) {
                count = (Object) row[2];
                String regionid = (row[0]!=null)?row[0].toString():"None";
                q2Map.put(regionid, String.valueOf(count));
            }

            requestParams.put("cd", 0);
            requestParams.put("projFlag", false);
            kmsg = opportunityReportDAOObj.oppByRegionFunnelReport(requestParams, usersList);
            jobj = getoppByRegionFunnelReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), q1Map, q2Map);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public int getCurrentDateQuarter(Date todayDate) {
        int startMonth = 0;
        try {
            int month = todayDate.getMonth();
            if(month == 11 || month == 0 || month == 1) {//Dec, Jan, Feb
                startMonth = 11;
            }else if(month == 2 || month == 3 || month == 4) {//Mar, Apr, May
                startMonth = 2;
            }else if(month == 5 || month == 6 || month == 7) {//Jun, Jul, Aug
                startMonth = 5;
            }else if(month == 8 || month == 9 || month == 10) {//Sept, Oct, Nov
                startMonth = 8;
            }
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return startMonth;
    }

    public JSONObject getrevenueByOppSourceReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String head = request.getParameter("head");
            String sourceflag = request.getParameter("sf");
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            String currencySymbol=crmManagerDAOObj.currencySymbol(currencyid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();

                tmpObj.put("oppname", obj.getOppname());
                tmpObj.put("closingdate",obj.getClosingdate()!=null?obj.getClosingdate():"");
                tmpObj.put("salesamount", !StringUtil.isNullOrEmpty(obj.getSalesamount()) ? crmManagerDAOObj.currencyRender(obj.getSalesamount(), currencyid) : "");
                tmpObj.put("oppstageid", crmManagerCommon.comboNull(obj.getCrmCombodataByOppstageid()));
                tmpObj.put("leadsourceid", crmManagerCommon.comboNull(obj.getCrmCombodataByLeadsourceid()));
                tmpObj.put("accountnameid", crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid"));
                tmpObj.put("createdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
                tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                tmpObj.put("oppstage", (obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("probability", obj.getProbability());

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Opportunity");
                Iterator ite1 = ll1.iterator();
                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Opportunity",companyid);
                    if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "oppname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYACCOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYLEADSOURCEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "leadsource");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYSTAGEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.stage", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.stage", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "oppstage");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }  else if(StringUtil.equal(Header.OPPORTUNITYSALESAMOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.salesammount", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.salesammount", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "salesamount");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYCLOSEDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.opportunityreport.expectedclosedate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.report.opportunityreport.expectedclosedate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "closingdate");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsource");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsourceid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountnameid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "productname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "closingdate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstage");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstageid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "salesamount");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "probability");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "price");
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
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }

    public ModelAndView closedOppReport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm",request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.closedOppReport(requestParams, usersList);
            jobj = getOpportunityReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView closedOppExport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.closedOppReport(requestParams, usersList);
            jobj = getOpportunityReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Closed_Opp,
                    "Closed Opportunity Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView oppByTypeReport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("filterCombo", StringUtil.checkForNull(request.getParameter("filterCombo")));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.oppByTypeReport(requestParams, usersList);
            jobj = getOpportunityReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView oppByTypeExport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            kmsg = opportunityReportDAOObj.oppByTypeReport(requestParams, usersList);
            jobj = getOpportunityReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Opp_by_Type,
                    "Opportunity by Type Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView stuckOppReport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.stuckOppReport(requestParams, usersList);
            jobj = getOpportunityReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView stuckOppExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.stuckOppReport(requestParams, usersList);
            jobj = getOpportunityReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Stuck_Opp,
                    "Stuck Opportunity Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView oppPipelineReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        KwlReturnObject kmsg1 = null;
        try {
        	
        	String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
        	String comboname ="Opportunity Stage"; 
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
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            kmsg = opportunityReportDAOObj.oppPipelineReport(requestParams, usersList);
            jobj = getOppPipelinedReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(),kmsg1.getEntityList(),dl1);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView oppPipelineExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        KwlReturnObject kmsg1 = null;
        String view = "jsonView";
        try {
        	String export = request.getParameter("reportid");
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
        	String comboname ="Opportunity Stage"; 
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
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            kmsg = opportunityReportDAOObj.oppPipelineReport(requestParams, usersList);
            jobj = getOppPipelinedReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(),kmsg1.getEntityList(),dl1);

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

    public ModelAndView allOppPipelineReport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            kmsg = opportunityReportDAOObj.allOppPipelineReport(requestParams, usersList);
            jobj = getallOppPipelinedReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView allOppPipelineExport(HttpServletRequest request, HttpServletResponse response)
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
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            kmsg = opportunityReportDAOObj.allOppPipelineReport(requestParams, usersList);
            jobj = getallOppPipelinedReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

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
    public JSONObject getsourceOfOppReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String head = request.getParameter("head");
            String sourceflag = request.getParameter("sf");
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();
                String[] ownerInfo=crmOpportunityHandler.getAllOppOwners(crmOpportunityDAOObj, obj.getOppid());
                tmpObj.put("oppowner", ownerInfo[4]);
                tmpObj.put("exportmultiowners", ownerInfo[5]);

                tmpObj.put("oppname", obj.getOppname());
                tmpObj.put("createdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
                tmpObj.put("closingdate",obj.getClosingdate()!=null?obj.getClosingdate():"");
                tmpObj.put("salesamount", !StringUtil.isNullOrEmpty(obj.getSalesamount()) ? crmManagerDAOObj.currencyRender(obj.getSalesamount(), currencyid) : "");
                tmpObj.put("oppstageid", crmManagerCommon.comboNull(obj.getCrmCombodataByOppstageid()));
                tmpObj.put("leadsourceid", crmManagerCommon.comboNull(obj.getCrmCombodataByLeadsourceid()));
                tmpObj.put("accountnameid", crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid"));
                tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : "-None-"));
                tmpObj.put("oppstage", (obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("probability", obj.getProbability());

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Opportunity");
                Iterator ite1 = ll1.iterator();
                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Opportunity",companyid);
                    if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header",Hdr );
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "oppname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.OPPORTUNITYOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.owner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip",  StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.owner", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "oppowner");
                        jobjTemp.put("title", "exportmultiowners");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }  else if(StringUtil.equal(Header.OPPORTUNITYACCOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYLEADSOURCEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "leadsource");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsource");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsourceid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountnameid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "productname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "closingdate");
                jobjTemp.put("dateFormat", "time");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstage");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstageid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "salesamount");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "probability");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "price");
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
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }

    public ModelAndView sourceOfOppReport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.sourceOfOppReport(requestParams, usersList);
            jobj = getsourceOfOppReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView sourceOfOppExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.sourceOfOppReport(requestParams, usersList);
            jobj = getsourceOfOppReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Sources_Opp,
                    "Sources of Opportunity Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView oppByLeadSourceReport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.oppByLeadSourceReport(requestParams, usersList);
            jobj = getOpportunityReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView oppByLeadSourceExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.oppByLeadSourceReport(requestParams, usersList);
            jobj = getOpportunityReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Opp_Lead_Source,
                    "Opportunity by Lead Source Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView oppProductReport(HttpServletRequest request, HttpServletResponse response)
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
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.oppProductReport(requestParams, usersList);
            jobj = getOpportunityReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView oppProductExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.oppProductReport(requestParams, usersList);
            jobj = getOpportunityReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Opp_Lead_Source,
                    "Opportunity by Lead Source Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }
    public JSONObject getsalesReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String head = request.getParameter("head");
            String sourceflag = request.getParameter("sf");
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencySymbol=crmManagerDAOObj.currencySymbol(currencyid);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();

                tmpObj.put("oppname", obj.getOppname());
                tmpObj.put("closingdate", obj.getClosingdate()!=null?obj.getClosingdate():"");
                tmpObj.put("salesamount", !StringUtil.isNullOrEmpty(obj.getSalesamount()) ? crmManagerDAOObj.currencyRender(obj.getSalesamount(), currencyid) : "");
                tmpObj.put("oppstageid", crmManagerCommon.comboNull(obj.getCrmCombodataByOppstageid()));
                tmpObj.put("leadsourceid", crmManagerCommon.comboNull(obj.getCrmCombodataByLeadsourceid()));
                tmpObj.put("accountnameid", crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid"));
                tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                tmpObj.put("oppstage", (obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : ""));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("createdon",obj.getCreatedOn()!=null?obj.getCreatedOn():"");
                tmpObj.put("type", (obj.getCrmCombodataByOpptypeid() != null ? obj.getCrmCombodataByOpptypeid().getValue() : ""));
                tmpObj.put("region", (obj.getCrmCombodataByRegionid() != null ? obj.getCrmCombodataByRegionid().getValue() : ""));
                tmpObj.put("price", obj.getPrice());
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Opportunity");
                Iterator ite1 = ll1.iterator();
                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Opportunity",companyid);
                    if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader;
                        if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", Hdr);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "oppname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYACCOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accountname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYLEADSOURCEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.lead.defaultheader.leadsource", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "leadsource");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYSTAGEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.stage", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.stage", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "oppstage");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYSALESAMOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.salesammount", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.salesammount", null, RequestContextUtils.getLocale(request))+"("+currencySymbol+")":newHeader+"("+currencySymbol+")");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "salesamount");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYCLOSEDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.closeddate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.closeddate", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "closingdate");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                     } else if(StringUtil.equal(Header.OPPORTUNITYCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader);
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
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsource");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsourceid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountnameid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "productname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "closingdate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstage");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppstageid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "salesamount");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "probability");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "price");
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
                jMeta.put("id", "asd");
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }

    public ModelAndView salesReport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.salesReport(requestParams, usersList);
            jobj = getsalesReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView salesExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.salesReport(requestParams, usersList);
            jobj = getsalesReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Sales_By_Source,
                    "Sales by Source Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView opportunityByTypePieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
                kmsg = opportunityReportDAOObj.getOpportunityByTypeChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }
            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityByTypeBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
                kmsg = opportunityReportDAOObj.getOpportunityByTypeChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
       }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityByProductPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        StringBuilder chart = new StringBuilder("<pie>");
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("groupby", true);
            kmsg = opportunityReportDAOObj.getOpportunityByProductChart(null, requestParams, usersList);
            chart = chartServiceObj.getPieChart(kmsg.getEntityList());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", chart.toString());
    }

    public ModelAndView opportunityByProductBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        StringBuilder chart = new StringBuilder("<chart>");
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("groupby", true);
            kmsg = opportunityReportDAOObj.getOpportunityByProductChart(null, requestParams, usersList);
            chart = chartServiceObj.getBarChart(kmsg.getEntityList());
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("chartView", "model", chart.toString());
    }

    public ModelAndView stuckOpportunitiesPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
                kmsg = opportunityReportDAOObj.getStuckOpportunitiesChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView stuckOpportunitiesBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
                kmsg = opportunityReportDAOObj.getStuckOpportunitiesChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityBySourcePieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
                kmsg = opportunityReportDAOObj.getOpportunityBySourceChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                int amountVal = 0;
                ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmOpportunity crmOpp = (CrmOpportunity) ite.next();
                    try {
                        amountVal += Integer.parseInt(crmOpp.getSalesamount());
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + amountVal + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityBySourceBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < max; k++) {
                kmsg = opportunityReportDAOObj.getOpportunityBySourceChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                int amountVal = 0;
                ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmOpportunity crmOpp = (CrmOpportunity) ite.next();
                    try {
                        amountVal += Integer.parseInt(crmOpp.getSalesamount());
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
                result += "<value xid=\"" + k + "\" >" + amountVal + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityByStagePieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
                kmsg = opportunityReportDAOObj.getOpportunityByStageChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityByStageRevenuePieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
                kmsg = opportunityReportDAOObj.getOpportunityByStageChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                double sumAmount = 0;
                List ll1 = kmsg.getEntityList();
                Iterator ite1 = ll1.iterator();
                while (ite1.hasNext()) {
                    CrmOpportunity opp = (CrmOpportunity) ite1.next();
                   sumAmount+=opp.getSalesamountnum();

                }
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + sumAmount + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityByStageBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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

           for (int j = 0; j < max; j++) {
                kmsg = opportunityReportDAOObj.getOpportunityByStageChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<value xid=\"" + j + "\" >" + kmsg.getRecordTotalCount() + "</value>";
                }
            }
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

      public ModelAndView opportunityByStageRevenueBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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

           for (int j = 0; j < max; j++) {
                kmsg = opportunityReportDAOObj.getOpportunityByStageChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                double sumAmount = 0;
                List ll1 = kmsg.getEntityList();
                Iterator ite1 = ll1.iterator();
                while (ite1.hasNext()) {
                    CrmOpportunity opp = (CrmOpportunity) ite1.next();
                   sumAmount+=opp.getSalesamountnum();

                }
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<value xid=\"" + j + "\" >" + sumAmount + "</value>";
                }
            }
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityBySalesPersonBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        String resultVal = "";
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            List<Object[]> ll = opportunityReportDAOObj.oppBySalesPersonCountList(requestParams, usersList);
            HashMap<User, String> countMap = new HashMap<User, String>();
            Object count = 0;
            for(Object[] row : ll) {
                count = (Object) row[0];
                User userObj = (User) row[1];
                countMap.put(userObj, String.valueOf(count));
            }

            result = "<chart><series>";
            Iterator ite = countMap.keySet().iterator();
            int j = 0;
            while(ite.hasNext()){
               User userObj = (User) ite.next();
               result += "<value xid=\"" + j + "\" >" + userObj.getFirstName() +" "+ userObj.getLastName() + "</value>";
               if (Integer.parseInt(countMap.get(userObj)) > 0) {
                    resultVal += "<value xid=\"" + j + "\" >" + countMap.get(userObj) + "</value>";
               }
               j++;
            }
            result += "</series><graphs><graph gid=\"0\">";
            result += resultVal;
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityBySalesPersonPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            List<Object[]> ll = opportunityReportDAOObj.oppBySalesPersonCountList(requestParams, usersList);
            HashMap<User, String> countMap = new HashMap<User, String>();
            Object count = 0;
            for(Object[] row : ll) {
                count = (Object) row[0];
                User userObj = (User) row[1];
                countMap.put(userObj, String.valueOf(count));
            }

            result = "<pie>";
            Iterator ite = countMap.keySet().iterator();
            int j = 0;
            while(ite.hasNext()){
               User userObj = (User) ite.next();
               if (Integer.parseInt(countMap.get(userObj)) > 0) {
                    result += "<slice title=\"" + userObj.getFirstName() +" "+ userObj.getLastName() + "\" >" + countMap.get(userObj) + "</slice>";
               }
               j++;
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityByRegionHBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        String resultVal = "";
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            List<Object[]> ll = opportunityReportDAOObj.oppByRegionCountList(requestParams, usersList);
            HashMap<DefaultMasterItem, String> countMap = new HashMap<DefaultMasterItem, String>();
            Object count = 0;
            for(Object[] row : ll) {
                count = (Object) row[0];
                DefaultMasterItem regionObj = null;
                if(row[1] != null) {
                    regionObj = (DefaultMasterItem) row[1];
                } else {
                    regionObj = new DefaultMasterItem();
                    regionObj.setID("Region-None");
                    regionObj.setValue("-None-");
                }
                countMap.put(regionObj, String.valueOf(count));
            }

            result = "<chart><series>";
            Iterator ite = countMap.keySet().iterator();
            int j = 0;
            while(ite.hasNext()){
               DefaultMasterItem regionObj = (DefaultMasterItem) ite.next();
               result += "<value xid=\"" + j + "\" >" + regionObj.getValue() + "</value>";
               if (Integer.parseInt(countMap.get(regionObj)) > 0) {
                    resultVal += "<value xid=\"" + j + "\" >" + countMap.get(regionObj) + "</value>";
               }
               j++;
            }
            result += "</series><graphs><graph gid=\"0\">";
            result += resultVal;
            result += "</graph></graphs></chart>";           
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView opportunityByRegionHPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            List<Object[]> ll = opportunityReportDAOObj.oppByRegionCountList(requestParams, usersList);
            HashMap<DefaultMasterItem, String> countMap = new HashMap<DefaultMasterItem, String>();
            Object count = 0;
            for(Object[] row : ll) {
                count = (Object) row[0];
                DefaultMasterItem regionObj = null;
                if(row[1] != null) {
                    regionObj = (DefaultMasterItem) row[1];
                } else {
                    regionObj = new DefaultMasterItem();
                    regionObj.setID("Region-None");
                    regionObj.setValue("-None-");
                }
                countMap.put(regionObj, String.valueOf(count));
            }
            result = "<pie>";
            Iterator ite = countMap.keySet().iterator();
            int j = 0;
            while(ite.hasNext()){
               DefaultMasterItem regionObj = (DefaultMasterItem) ite.next();
               if (Integer.parseInt(countMap.get(regionObj)) > 0) {
                    result += "<slice title=\"" + regionObj.getValue() + "\" >" + countMap.get(regionObj) + "</slice>";
               }
               j++;
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView oppSalesamountDashboardPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            String groupbyField = request.getParameter("groupbyField");
            String valueField = (groupbyField.equals("oo.usersByUserid")?"oo.usersByUserid.firstName||' '||oo.usersByUserid.lastName as name":(groupbyField + "." + request.getParameter("valueField")));
            String idField = groupbyField + "." + request.getParameter("idField");
            requestParams.put("valueField", valueField);
            requestParams.put("groupbyField", groupbyField);
            requestParams.put("idField", idField);

            result = "<pie>";
            int j = 0;
            List<Object[]> ll = opportunityReportDAOObj.oppSalesamountDashboardPieChart(requestParams, usersList);
            for(Object[] row : ll) {
                Object salesamount = (Object) row[0];
                String mastername = "";
                if(row[1] != null) {
                    mastername = row[2].toString();
                } else {
                    mastername = "None";
                }
                if (Double.parseDouble(String.valueOf(salesamount)) > 0) {
                    result += "<slice title=\"" + mastername + "\" >" + String.valueOf(salesamount) + "</slice>";
                }
                j++;
            }
            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView oppSalesamountDashboardBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        String resultVal = "";

        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            String groupbyField = request.getParameter("groupbyField");
            String valueField = (groupbyField.equals("oo.usersByUserid")?"oo.usersByUserid.firstName||' '||oo.usersByUserid.lastName as name":(groupbyField + "." + request.getParameter("valueField")));
            String idField = groupbyField + "." + request.getParameter("idField");
            requestParams.put("valueField", valueField);
            requestParams.put("groupbyField", groupbyField);
            requestParams.put("idField", idField);

            result = "<chart><series>";
            int j = 0;
            List<Object[]> ll = opportunityReportDAOObj.oppSalesamountDashboardPieChart(requestParams, usersList);
            for(Object[] row : ll) {
                Object salesamount = (Object) row[0];
                String mastername = "";
                if(row[1] != null) {
                    mastername = row[2].toString();
                } else {
                    mastername = "None";
                }

                result += "<value xid=\"" + j + "\" >" + mastername + "</value>";
                if (Double.parseDouble(String.valueOf(salesamount)) > 0) {
                    resultVal += "<value xid=\"" + j + "\" >" + String.valueOf(salesamount) + "</value>";
                }
                j++;
            }
            result += "</series><graphs><graph gid=\"0\">";
            result += resultVal;
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView salesBySourcePieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            requestParams.put("frm", (request.getParameter("frm")));
            requestParams.put("to", (request.getParameter("to")));
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
                kmsg = opportunityReportDAOObj.getSalesBySourceChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                int amountVal = 0;
                ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmOpportunity crmOpp = (CrmOpportunity) ite.next();
                    amountVal += Integer.parseInt(StringUtil.isNullOrEmpty(crmOpp.getSalesamount())?"0":crmOpp.getSalesamount());
                }
                if (kmsg.getRecordTotalCount() > 0 && amountVal > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + amountVal + "</slice>";
                }
            }
            kmsg = opportunityReportDAOObj.getSalesBySourceChart("Undefined", requestParams, usersList);
            if (kmsg.getRecordTotalCount() > 0) {
                int amountVal = 0;
                ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmOpportunity crmOpp = (CrmOpportunity) ite.next();
                    amountVal += Integer.parseInt(StringUtil.isNullOrEmpty(crmOpp.getSalesamount())?"0":crmOpp.getSalesamount());
                }
                result += "<slice title=\"Undefined\" >" + amountVal + "</slice>";
            }
            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView salesBySourceBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("frm",request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboName.equalsIgnoreCase("Lead Source")) {
//                filter_names.add("d.validflag");
//                filter_params.add(1);
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
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }

            kmsg = opportunityReportDAOObj.getSalesBySourceChart("Undefined", requestParams, usersList);
            if (kmsg.getRecordTotalCount() > 0) {
                     result += "<value xid=\"" + (max+1) + "\" >" + "Undefined" + "</value>";
            }

            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < max; k++) {
                kmsg = opportunityReportDAOObj.getSalesBySourceChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                int amountVal = 0;
                ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmOpportunity crmOpp = (CrmOpportunity) ite.next();
                    amountVal += Integer.parseInt(StringUtil.isNullOrEmpty(crmOpp.getSalesamount())?"0":crmOpp.getSalesamount());
                }
                result += "<value xid=\"" + k + "\" >" + amountVal + "</value>";
            }
            kmsg = opportunityReportDAOObj.getSalesBySourceChart("Undefined", requestParams, usersList);
            if (kmsg.getRecordTotalCount() > 0) {
                int amountVal = 0;
                ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    CrmOpportunity crmOpp = (CrmOpportunity) ite.next();
                    amountVal += Integer.parseInt(StringUtil.isNullOrEmpty(crmOpp.getSalesamount())?"0":crmOpp.getSalesamount());
                }
               result += "<value xid=\"" + (max+1) + "\" >" + amountVal + "</value>";
            }
            
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView sourceOffOppPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
                kmsg = opportunityReportDAOObj.getSourceOffOppChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView sourceOffOppBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < max; k++) {
                kmsg = opportunityReportDAOObj.getSourceOffOppChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView closedOppPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        int dl = 0;
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
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            filter_names.add("c.isarchive");
            filter_params.add(false);

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            if(!heirarchyPerm){
                filter_names.add("INoo.usersByUserid.userID");filter_params.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            kmsg = crmOpportunityDAOObj.getAllOpportunities(requestParams);
            dl = kmsg.getRecordTotalCount();
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getOppid());
                tmpObj.put("name", obj.getOppname());
                tmpObj.put("phone", 0);
                tmpObj.put("email", "");
                tmpObj.put("salesamt",obj.getSalesamount());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = opportunityReportDAOObj.getClosedOppChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                	if(jarr.getJSONObject(j).get("salesamt")!=null && jarr.getJSONObject(j).get("salesamt")!="")
                		result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + jarr.getJSONObject(j).get("salesamt") + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView closedOppBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        String result2 = "";
        int k = 0;
        KwlReturnObject kmsg = null;
        int dl = 0;
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
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            filter_names.add("c.isarchive");
            filter_params.add(false);

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            if(!heirarchyPerm){
                filter_names.add("INoo.usersByUserid.userID");filter_params.add(usersList);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            kmsg = crmOpportunityDAOObj.getAllOpportunities(requestParams);
            dl = kmsg.getRecordTotalCount();
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getOppid());
                tmpObj.put("name", obj.getOppname());
                tmpObj.put("phone", 0);
                tmpObj.put("email", "");
                tmpObj.put("salesamt",obj.getSalesamount());
                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            result = "<chart><series>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = opportunityReportDAOObj.getClosedOppChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<value xid=\"" + k + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
                    result2 += "<value xid=\"" + k + "\" >" + jarr.getJSONObject(j).get("salesamt").toString() + "</value>";
                    k++;
                }
            }
            result += "</series><graphs><graph gid=\"0\">";
            result2 += "</graph></graphs></chart>";
            result += result2;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView leadOpportunityPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
                kmsg = opportunityReportDAOObj.getLeadOpportunityChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                }
            }
            kmsg = opportunityReportDAOObj.getLeadOpportunityChart("Undefined", requestParams, usersList);
            if (kmsg.getRecordTotalCount() > 0) {
                result += "<slice title=\"Undefined\" >" + kmsg.getRecordTotalCount() + "</slice>";
            }
            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView leadOpportunityBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
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
            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "<value xid=\"" + max + "\" >Undefined</value>";
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < max; k++) {
                kmsg = opportunityReportDAOObj.getLeadOpportunityChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }
            int k = Integer.parseInt(jobj.get("totalCount").toString());
            kmsg = opportunityReportDAOObj.getLeadOpportunityChart("Undefined", requestParams, usersList);
            result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    //Account reports
    public JSONObject getAccountOppReportJson(List ll, HttpServletRequest request, boolean export, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("oppid", obj.getOppid());
                tmpObj.put("oppname", obj.getOppname());
                tmpObj.put("closingdate",obj.getClosingdate()!=null?obj.getClosingdate():"");
                tmpObj.put("probability", obj.getProbability());
                tmpObj.put("revenue", obj.getRevenue());
                tmpObj.put("recurrevenue", obj.getRecurrevenue());
                tmpObj.put("price", obj.getPrice());
                tmpObj.put("exportprice", obj.getPrice() != null && !obj.getPrice().equals("") ? crmManagerDAOObj.currencyRender(obj.getPrice(), currencyid) : "");
                tmpObj.put("keyname", obj.getKeyname());
                tmpObj.put("keytitleid", obj.getKeytitleid());
                tmpObj.put("keyaddstreet", obj.getKeyaddstreet());
                tmpObj.put("keyaddcity", obj.getKeyaddcity());
                tmpObj.put("keyaddstate", obj.getKeyaddstate());
                tmpObj.put("keyaddcountry", obj.getKeyaddcountry());
                tmpObj.put("keyaddzip", obj.getKeyaddzip());
                tmpObj.put("description", obj.getDescription());
                tmpObj.put("competitorname", obj.getCompetitorname());
                tmpObj.put("createdon",obj.getCreatedOn()!=null?obj.getCreatedOn():"");
                tmpObj.put("updatedon", obj.getUpdatedOn()!=null?obj.getUpdatedOn():"");
                tmpObj.put("salesamount", obj.getSalesamount());
                tmpObj.put("exportsalesamount", obj.getSalesamount() != null && !obj.getSalesamount().equals("") ? crmManagerDAOObj.currencyRender(obj.getSalesamount(), currencyid) : "");
                tmpObj.put("accountid", crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid"));
                tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : ""));
                tmpObj.put("oppstageid", crmManagerCommon.comboNull(obj.getCrmCombodataByOppstageid()));
                tmpObj.put("stage", (obj.getCrmCombodataByOppstageid() != null ? obj.getCrmCombodataByOppstageid().getValue() : ""));
                tmpObj.put("opptypeid", crmManagerCommon.comboNull(obj.getCrmCombodataByOpptypeid()));
                tmpObj.put("type", (obj.getCrmCombodataByOpptypeid() != null ? obj.getCrmCombodataByOpptypeid().getValue() : ""));
                tmpObj.put("oppregionid", crmManagerCommon.comboNull(obj.getCrmCombodataByRegionid()));
                tmpObj.put("region", (obj.getCrmCombodataByRegionid() != null ? obj.getCrmCombodataByRegionid().getValue() : ""));
                tmpObj.put("leadsourceid", crmManagerCommon.comboNull(obj.getCrmCombodataByLeadsourceid()));
                tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, Header.ACCOUNTNAMEHEADER, "Account", companyid);
                jobjTemp = new JSONObject();
                String Hdr = StringUtil.isNullOrEmpty(newHeader)? messageSource.getMessage("crm.opportunity.defaultheader.accountname", null, RequestContextUtils.getLocale(request)):newHeader;
                if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, qucikSerachFields)){
                    jobjTemp.put("qucikSearchText", Hdr);
                }
                jobjTemp.put("header", Hdr);
                jobjTemp.put("tip",Hdr);
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "accountname");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                List ll2=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Opportunity");
                Iterator ite2 = ll2.iterator();
                while (ite2.hasNext()) {
                    DefaultHeader obj1 = (DefaultHeader) ite2.next();
                    String newHeader2 =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj1.getDefaultHeader(),"Opportunity",companyid);
                    if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, obj1.getDefaultHeader())) {
                            jobjTemp = new JSONObject();
                            jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader2);
                            jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader2);
                            jobjTemp.put("pdfwidth", 60);
                            jobjTemp.put("xtype", "textfield");
                            jobjTemp.put("dataIndex", "oppname");
                            StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                            jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYSTAGEHEADER, obj1.getDefaultHeader())) {
                            jobjTemp = new JSONObject();
                            jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.opportunityreport.opportunitystage", null, RequestContextUtils.getLocale(request)):newHeader2);
                            jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.opportunityreport.opportunitystage", null, RequestContextUtils.getLocale(request)):newHeader2);
                            jobjTemp.put("pdfwidth", 60);
                            jobjTemp.put("xtype", "combo");
                            jobjTemp.put("dataIndex", "stage");
                            StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                            jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYCLOSEDATEHEADER, obj1.getDefaultHeader())) {
                            jobjTemp = new JSONObject();
                            jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.opportunityreport.oppclosedate", null, RequestContextUtils.getLocale(request)):newHeader2);
                            jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.report.opportunityreport.oppclosedate", null, RequestContextUtils.getLocale(request)):newHeader2);
                            jobjTemp.put("align", "center");
                            jobjTemp.put("pdfwidth", 60);
                            jobjTemp.put("xtype", "datefield");
                            jobjTemp.put("dataIndex", "closingdate");
                            jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                            jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                            StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                            jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYCREATIONDATEHEADER, obj1.getDefaultHeader())) {
                            jobjTemp = new JSONObject();
                            jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader2);
                            jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.opportunity.defaultheader.createdon", null, RequestContextUtils.getLocale(request)):newHeader2);
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
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "stage");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "closingdate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
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
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }

    public ModelAndView accountsWithOpportunityReport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPermOpp = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            boolean heirarchyPermAcc = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            requestParams.put("heirarchyPermOpp", heirarchyPermOpp);
            requestParams.put("heirarchyPermAcc", heirarchyPermAcc);
            
            kmsg = opportunityReportDAOObj.accountsWithOpportunityReport(requestParams, usersList);
            jobj = getAccountOppReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView accountsWithOpportunityExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.accountsWithOpportunityReport(requestParams, usersList);
            jobj = getAccountOppReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount());

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Accounts_Contacts,
                    "Account with Contacts Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView accountOpportunityPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        StringBuilder chart = new StringBuilder();
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("groupby", true);
            kmsg = opportunityReportDAOObj.getAccountOpportunityChart(null, requestParams, usersList);
            chart = chartServiceObj.getPieChart(kmsg.getEntityList());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", chart.toString());
    }

    public ModelAndView accountOpportunityBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        StringBuilder chart = new StringBuilder();
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("groupby", true);
            kmsg = opportunityReportDAOObj.getAccountOpportunityChart(null, requestParams, usersList);
            chart = chartServiceObj.getBarChart(kmsg.getEntityList());
        } catch (Exception e) {
           logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", chart.toString());
    }

    public ModelAndView oppPipelinePieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
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
                String name = crmCombodata.getValue();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", name);

                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            result = "<pie>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                kmsg = opportunityReportDAOObj.oppPipelineChart(jarr.getJSONObject(j).get("id").toString(), requestParams, usersList);
                if (kmsg.getRecordTotalCount() > 0) {
                    Object [] objarr = (Object[])kmsg.getEntityList().get(0);
                    result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + objarr[4] + "</slice>";
                }
            }

            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView oppPipelineBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
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
                String name = crmCombodata.getValue();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", name);

                jarr1.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr1);
            jobj.put("totalCount", dl);

            JSONArray jarr = jobj.getJSONArray("data");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            int max = Integer.parseInt(jobj.get("totalCount").toString());
            result = "<chart><series>";
            for (int j = 0; j < max; j++) {
                result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < max; k++) {
                kmsg = opportunityReportDAOObj.oppPipelineChart(jarr.getJSONObject(k).get("id").toString(), requestParams, usersList);
                result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
            }

            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    //Lead Report
    public ModelAndView convertedLeadsToOpportunityReport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPermOpp = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPermOpp", heirarchyPermOpp);
            boolean heirarchyPermLea = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPermLea", heirarchyPermLea);
            
            kmsg = opportunityReportDAOObj.convertedLeadsToOpportunityReport(requestParams, usersList);
            jobj = getConvertedLeadsReportJson(kmsg.getEntityList(), request, true, kmsg.getRecordTotalCount(), "Opportunity");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView convertedLeadsToOpportunityExport(HttpServletRequest request, HttpServletResponse response)
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
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            
            kmsg = opportunityReportDAOObj.convertedLeadsToOpportunityReport(requestParams, usersList);
            jobj = getConvertedLeadsReportJson(kmsg.getEntityList(), request, false, kmsg.getRecordTotalCount(), "Opportunity");

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.Converted_Leads_Opp,
                    "Converted Leads to Opportunity Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
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
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            while (ite.hasNext()) {
                JSONObject tmpObj = new JSONObject();
                if (convertedTo.equals("Opportunity")) {
                    CrmOpportunity objOpp = (CrmOpportunity) ite.next();
                    objLead = objOpp.getCrmLead();
                    tmpObj.put("oppname", objOpp.getOppname());
                    tmpObj.put("oppdate", objOpp.getCreatedOn()!=null?objOpp.getCreatedOn():"");
                    day = StringUtil.getDaysDiff(objLead.getCreatedOn(), objOpp.getCreatedOn());
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
                        jobjTemp.put("header",Hdr );
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
                List ll2=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Opportunity");
                Iterator ite2 = ll2.iterator();
                while (ite2.hasNext()) {
                    DefaultHeader obj1 = (DefaultHeader) ite2.next();
                    String newHeader2 =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj1.getDefaultHeader(),"Opportunity",companyid);
                    if(StringUtil.equal(Header.OPPORTUNITYNAMEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)? messageSource.getMessage("crm.opportunity.defaultheader.opportunityname", null, RequestContextUtils.getLocale(request)):newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "oppname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYCREATIONDATEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header",  messageSource.getMessage("crm.report.opportunityreport.convertedon", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip",  messageSource.getMessage("crm.report.opportunityreport.convertedon", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("dataIndex", "oppdate");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.OPPORTUNITYPROBABILITYHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header",  messageSource.getMessage("crm.report.allreport.daystoconvert", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("tip",  messageSource.getMessage("crm.report.allreport.daystoconvert", null, RequestContextUtils.getLocale(request)));
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
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
                jobjTemp.put("name", "oppname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "oppdate");
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
            logger.warn(e.getMessage(), e);
        }
        return commData;
    }
}
