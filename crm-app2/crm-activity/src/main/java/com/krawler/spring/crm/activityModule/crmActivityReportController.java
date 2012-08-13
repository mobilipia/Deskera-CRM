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
package com.krawler.spring.crm.activityModule;

import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.utils.json.base.JSONException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author Karthik
 */
public class crmActivityReportController extends MultiActionController implements MessageSourceAware{
    private class CrmAllActComparator implements Comparator<Object> {
        @Override
        public int compare(Object row1, Object row2) {
            JSONObject actMasterobj1 = (JSONObject ) row1;
            JSONObject  actMasterobj2 = (JSONObject ) row2;
            Long startdate1=null;
            Long startdate2=null;
                startdate1 = actMasterobj1.optLong("startdate");
                startdate2=actMasterobj2.optLong("startdate");
            return startdate1.compareTo(startdate2);
        }
    }

    private crmActivityReportDAO activityReportDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private MessageSource messageSource;
    
    
    public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	private static final Log LOGGER = LogFactory.getLog(crmActivityReportController.class);
    
    public crmActivityReportDAO getcrmActivityReportDAO(){
        return activityReportDAOObj;
    }
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmActivityReportDAO(crmActivityReportDAO activityReportDAOObj1) {
        this.activityReportDAOObj = activityReportDAOObj1;
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

    public List HighPriorityActivitiesJson(List ll, HttpServletRequest request, List actList, String module) {
        try {
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Object row[] = (Object[]) ite.next();
                CrmActivityMaster obj = (CrmActivityMaster) row[0];
                JSONObject tmpObj = new JSONObject();
                if (module.equals("Account")) {
                    CrmAccount obj2 = (CrmAccount) row[1];
                    tmpObj.put("relatednameid", obj2.getAccountid());
                    tmpObj.put("relatedname", obj2.getAccountname());
                } else if (module.equals("Campaign")) {
                    CrmCampaign obj2 = (CrmCampaign) row[1];
                    tmpObj.put("relatednameid", obj2.getCampaignid());
                    tmpObj.put("relatedname", obj2.getCampaignname());
                } else if (module.equals("Case")) {
                    CrmCase obj2 = (CrmCase) row[1];
                    tmpObj.put("relatednameid", obj2.getCaseid());
                    tmpObj.put("relatedname", obj2.getCasename());
                } else if (module.equals("Contact")) {
                    CrmContact obj2 = (CrmContact) row[1];
                    tmpObj.put("relatednameid", obj2.getContactid());
                    tmpObj.put("relatedname", obj2.getFirstname()!=null?obj2.getFirstname():"" + " " + obj2.getLastname()!=null?obj2.getLastname():"");
                } else if (module.equals("Lead")) {
                    CrmLead obj2 = (CrmLead) row[1];
                    tmpObj.put("relatednameid", obj2.getLeadid());
                    tmpObj.put("relatedname", obj2.getLastname());
                } else if (module.equals("Opportunity")) {
                    CrmOpportunity obj2 = (CrmOpportunity) row[1];
                    tmpObj.put("relatednameid", obj2.getOppid());
                    tmpObj.put("relatedname", obj2.getOppname());
                }
                tmpObj.put("activityid", obj.getActivityid());
                tmpObj.put("ownerid", obj.getUsersByUserid().getUserID());
                tmpObj.put("owner", obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("subject", obj.getSubject());
                tmpObj.put("statusid", crmManagerCommon.comboNull(obj.getCrmCombodataByStatusid()));
                tmpObj.put("status", (obj.getCrmCombodataByStatusid() != null ? obj.getCrmCombodataByStatusid().getValue() : ""));
                tmpObj.put("priorityid", crmManagerCommon.comboNull(obj.getCrmCombodataByPriorityid()));
                tmpObj.put("priority", (obj.getCrmCombodataByPriorityid() != null ? obj.getCrmCombodataByPriorityid().getValue() : ""));
                tmpObj.put("typeid", crmManagerCommon.comboNull(obj.getCrmCombodataByTypeid()));
                tmpObj.put("type", (obj.getCrmCombodataByTypeid() != null ? obj.getCrmCombodataByTypeid().getValue() : ""));
                tmpObj.put("phone", obj.getPhone());
                tmpObj.put("email", obj.getEmail());
                tmpObj.put("startdate",obj.getStartDate()==null?"":obj.getStartDate());
                tmpObj.put("enddate", obj.getEndDate()==null?"":obj.getEndDate());
                if(!obj.isIsallday()){
                tmpObj.put("starttime",obj.getStartDate()==null?"":obj.getStartDate());
                tmpObj.put("endtime", obj.getEndDate()==null?"":obj.getEndDate());
                }
                tmpObj.put("flag", obj.getFlag());
                tmpObj.put("createdon",obj.getCreatedOn() == null? "": obj.getCreatedOn());
                tmpObj.put("relatedto", module);
                tmpObj.put("validflag", obj.getValidflag());
                actList.add(tmpObj);
//                jarr.put(tmpObj);
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return actList;//jarr;
    }

    public JSONObject HighPriorityActivitiesColumnJson(JSONArray jarr, boolean export, int totalSize,Locale locale) {
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            commData.put("coldata", jarr);
            if (export) {
                jobjTemp.put("header", messageSource.getMessage("crm.account.defaultheader.accountowner", null, locale));
                jobjTemp.put("tip",  messageSource.getMessage("crm.account.defaultheader.accountowner", null, locale));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "owner");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.report.activity.taskorevent", null, locale));
                jobjTemp.put("tip", messageSource.getMessage("crm.report.activity.taskorevent", null, locale));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "combo");
                jobjTemp.put("dataIndex", "flag");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.case.defaultheader.subject", null, locale));
                jobjTemp.put("tip", messageSource.getMessage("crm.case.defaultheader.subject", null, locale));
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "subject");
                jobjTemp.put("editor", "new Wtf.ux.TextField({allowBlank:false})");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.case.defaultheader.type", null, locale));
                jobjTemp.put("tip", messageSource.getMessage("crm.case.defaultheader.type", null, locale));
                jobjTemp.put("id", "type");
                jobjTemp.put("title", "type");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "combo");
                jobjTemp.put("renderer","Wtf.ux.comboBoxRendererStore(Wtf.typeStore,'id','name')");
                jobjTemp.put("editor", "new Wtf.form.ComboBox({store:Wtf.typeStore,valueField:'id',displayField:'name',typeAhead:true,selectOnFocus:true,mode:'local',triggerAction:'all'})");
                jobjTemp.put("dataIndex", "typeid");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.mydocuments.header.relatedto", null, locale));//"Related To");
                jobjTemp.put("tip", messageSource.getMessage("crm.mydocuments.header.relatedto", null, locale));//"Related To");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "relatedto");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.mydocuments.header.relatedname", null, locale)); // "Related Name");
                jobjTemp.put("tip", messageSource.getMessage("crm.mydocuments.header.relatedname", null, locale)); //"Related Name");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("dataIndex", "relatedname");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.lead.defaultheader.phone", null, locale));// "Phone");
                jobjTemp.put("tip", messageSource.getMessage("crm.lead.defaultheader.phone", null, locale));//"Phone");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "numberfield");
                jobjTemp.put("dataIndex", "phone");
                jobjTemp.put("editor", "new Wtf.ux.TextField({})");
                jobjTemp.put("renderer","WtfGlobal.renderContactToCall");
                jobjTemp.put("align", "right");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header",messageSource.getMessage("crm.account.defaultheader.email", null, locale));// "Email");
                jobjTemp.put("tip", messageSource.getMessage("crm.account.defaultheader.email", null, locale));//"Email");
                jobjTemp.put("xtype", "textfield");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "email");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.case.defaultheader.priority", null, locale));//Priority
                jobjTemp.put("tip", messageSource.getMessage("crm.case.defaultheader.priority", null, locale));//Priority
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("id", "priority");
                jobjTemp.put("xtype", "combo");
                jobjTemp.put("title", "priority");
                jobjTemp.put("renderer","Wtf.ux.comboBoxRendererStore(Wtf.cpriorityStore,'id','name')");
                jobjTemp.put("editor", "new Wtf.form.ComboBox({store:Wtf.cpriorityStore,valueField:'id',displayField:'name',typeAhead:true,selectOnFocus:true,mode:'local',triggerAction:'all'})");
                jobjTemp.put("dataIndex", "priorityid");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.case.defaultheader.status", null, locale));//"Status");
                jobjTemp.put("tip", messageSource.getMessage("crm.case.defaultheader.status", null, locale));//"Status");
                jobjTemp.put("id", "status");
                jobjTemp.put("xtype", "combo");
                jobjTemp.put("title", "status");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "statusid");
                jobjTemp.put("renderer","Wtf.ux.comboBoxRendererStore(Wtf.statusStore,'id','name')");
                jobjTemp.put("editor", "new Wtf.form.ComboBox({store:Wtf.statusStore,valueField:'id',displayField:'name',typeAhead:true,selectOnFocus:true,mode:'local',triggerAction:'all'})");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.report.creationdate", null, locale));//"Creation Date");
                jobjTemp.put("tip", messageSource.getMessage("crm.report.creationdate", null, locale));//"Creation Date");
                jobjTemp.put("title", "createdon");
                jobjTemp.put("xtype", "datefield");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "createdon");
                jobjTemp.put("align", "center");
//                jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header",messageSource.getMessage("crm.campaign.defaultheader.startdate", null, locale));// "Start Date");
                jobjTemp.put("tip", messageSource.getMessage("crm.campaign.defaultheader.startdate", null, locale));//"Start Date");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "datefield");
                jobjTemp.put("dataIndex", "startdate");
                jobjTemp.put("align", "center");
//                jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header",messageSource.getMessage("crm.calendar.eventdetails.starttime", null, locale));// "Start Time");
                jobjTemp.put("tip", messageSource.getMessage("crm.calendar.eventdetails.starttime", null, locale));//"Start Time");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "starttime");
                jobjTemp.put("xtype", "timefield");
//                jobjTemp.put("editor", "new Wtf.form.TimeField({format:WtfGlobal.getLoginUserTimeFormat()})");
                jobjTemp.put("renderer","WtfGlobal.loginUserTimeRendererTZ");
                jobjTemp.put("align", "center");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.campaign.defaultheader.enddate", null, locale));//"End Date");
                jobjTemp.put("tip", messageSource.getMessage("crm.campaign.defaultheader.enddate", null, locale));//"End Date");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "datefield");
                jobjTemp.put("dataIndex", "enddate");
//                jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                jobjTemp.put("align", "center");
                jobjTemp.put("renderer","WtfGlobal.onlyDateRendererTZ");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.calendar.eventdetails.endtime", null, locale));// "End Time");
                jobjTemp.put("tip", messageSource.getMessage("crm.calendar.eventdetails.endtime", null, locale));//"End Time");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("xtype", "timefield");
                jobjTemp.put("dataIndex", "endtime");
//                jobjTemp.put("editor", "new Wtf.form.TimeField({format:WtfGlobal.getLoginUserTimeFormat()})");
                jobjTemp.put("renderer","WtfGlobal.loginUserTimeRendererTZ");
                jobjTemp.put("align", "center");
                StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                jarrColumns.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "owner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "flag");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "subject");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "typeid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "relatedto");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "relatedname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "relatednameid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "phone");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "email");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priority");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "priorityid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "status");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "statusid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "activityid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "startdate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "starttime");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "enddate");
                jobjTemp.put("dateFormat", "time");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "endtime");
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
            LOGGER.warn(e.getMessage(), e);
        }
        return commData;
    }

    public ModelAndView HighPriorityActivitiesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        List actList=new ArrayList();
        KwlReturnObject kmsg = null;
        try {
        	
        	int start=0;
            int limit=25;
            String startstr=StringUtil.checkForNull(request.getParameter("start"));
            String limitstr=StringUtil.checkForNull(request.getParameter("limit"));
            if(!StringUtil.isNullOrEmpty(startstr)&&!StringUtil.isNullOrEmpty(limitstr)){
                start=Integer.parseInt(startstr);
                limit=Integer.parseInt(limitstr);
            }
            String export = request.getParameter("reportid");
            String module = request.getParameter("filterCombo");
            String relatedid = request.getParameter("relatedid");
            String userid = sessionHandlerImplObj.getUserid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            if(!StringUtil.isNullOrEmpty(relatedid)){
                requestParams.put("relatedid", relatedid);
            }
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(StringUtil.equal(module, "All"))
            {
            
            kmsg = activityReportDAOObj.accountActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Account");

            kmsg = activityReportDAOObj.caseActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Case");

            kmsg = activityReportDAOObj.contactActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Contact");

            kmsg = activityReportDAOObj.leadActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Lead");

            kmsg = activityReportDAOObj.opportunityActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Opportunity");

            kmsg = activityReportDAOObj.campaignActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Campaign");
            }
            else
            {
            	requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
                requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            if(StringUtil.equal(module, "Account")){
                kmsg = activityReportDAOObj.accountActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Account");
            }else if(StringUtil.equal(module, "Case")){
                kmsg = activityReportDAOObj.caseActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Case");
            }else if(StringUtil.equal(module, "Contact")){
                kmsg = activityReportDAOObj.contactActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Contact");
            }else if(StringUtil.equal(module, "Lead")){
                kmsg = activityReportDAOObj.leadActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Lead");
            }else if(StringUtil.equal(module, "Opportunity")){
                kmsg = activityReportDAOObj.opportunityActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Opportunity");
            
            }else { // For Campaign
                kmsg = activityReportDAOObj.campaignActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Campaign");
                
            }
            }
            Collections.sort(actList,new CrmAllActComparator());
            int endlimit=actList.size();
             if(start+limit<actList.size()){
                 endlimit=start+limit;
             }
             jarr = new JSONArray(actList.subList(start, endlimit));
             jobj = HighPriorityActivitiesColumnJson(jarr, true, actList.size(),RequestContextUtils.getLocale(request));

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getAllActivitiesReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        List actList=new ArrayList();
        KwlReturnObject kmsg = null;
        try {
            int start=0;
            int limit=25;
            String startstr=StringUtil.checkForNull(request.getParameter("start"));
            String limitstr=StringUtil.checkForNull(request.getParameter("limit"));
            if(!StringUtil.isNullOrEmpty(startstr)&&!StringUtil.isNullOrEmpty(limitstr)){
                start=Integer.parseInt(startstr);
                limit=Integer.parseInt(limitstr);
            }
            String export = request.getParameter("reportid");
            String relatedid = request.getParameter("relatedid");
            String userid = sessionHandlerImplObj.getUserid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            if(!StringUtil.isNullOrEmpty(request.getParameter("frm")) && !StringUtil.isNullOrEmpty(request.getParameter("to"))){
            	requestParams.put("frm", request.getParameter("frm"));
            	requestParams.put("to", request.getParameter("to"));
            }
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("allActivities", true);
            if(request.getParameter("filterCombo")!=null && request.getParameter("filterCombo")!="" )
            	requestParams.put("priorityfilter", request.getParameter("filterCombo"));
            if(!StringUtil.isNullOrEmpty(relatedid)){
                requestParams.put("relatedid", relatedid);
            }
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            kmsg = activityReportDAOObj.accountActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Account");

            kmsg = activityReportDAOObj.caseActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Case");

            kmsg = activityReportDAOObj.contactActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Contact");

            kmsg = activityReportDAOObj.leadActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Lead");

            kmsg = activityReportDAOObj.opportunityActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Opportunity");

            kmsg = activityReportDAOObj.campaignActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Campaign");

           Collections.sort(actList,new CrmAllActComparator());
           int endlimit=actList.size();
            if(start+limit<actList.size()){
                endlimit=start+limit;
            }
            jarr = new JSONArray(actList.subList(start, endlimit));
            jobj = HighPriorityActivitiesColumnJson(jarr, true, actList.size(),RequestContextUtils.getLocale(request));

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    public ModelAndView getAllActivitiesExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        List actList=new ArrayList();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
//            int start=0;
//            int limit=25;

//            String startstr=StringUtil.checkForNull(request.getParameter("start"));
//            String limitstr=StringUtil.checkForNull(request.getParameter("limit"));
//            if(!StringUtil.isNullOrEmpty(startstr)&&!StringUtil.isNullOrEmpty(limitstr)){
//                start=Integer.parseInt(startstr);
//                limit=Integer.parseInt(limitstr);
//            }
            String export = request.getParameter("reportid");
            String userid = sessionHandlerImplObj.getUserid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to", request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("allActivities", true);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
            requestParams.put("heirarchyPerm", heirarchyPerm);

                kmsg = activityReportDAOObj.accountActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Account");

                kmsg = activityReportDAOObj.caseActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Case");

                kmsg = activityReportDAOObj.contactActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Contact");

                kmsg = activityReportDAOObj.leadActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Lead");

                kmsg = activityReportDAOObj.opportunityActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Opportunity");

                kmsg = activityReportDAOObj.campaignActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Campaign");


//            int endlimit=actList.size();
//            if(start+limit<actList.size()){
//                endlimit=start+limit;
//            }
//            jarr = new JSONArray(actList.subList(start, endlimit));
            jarr = new JSONArray(actList);
            jobj = HighPriorityActivitiesColumnJson(jarr, true, actList.size(),RequestContextUtils.getLocale(request));

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.High_Pri_Activity,
                    "High Priority Activities Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView HighPriorityActivitiesExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        List actList=new ArrayList();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String export = request.getParameter("reportid");
            String module = request.getParameter("filterCombo");
            String userid = sessionHandlerImplObj.getUserid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("reportid", StringUtil.checkForNull(export));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("cd", StringUtil.checkForNull(request.getParameter("cd")));
            requestParams.put("frm", request.getParameter("frm"));
            requestParams.put("to",request.getParameter("to"));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(StringUtil.equal(module, "All"))
            {
            
            kmsg = activityReportDAOObj.accountActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Account");

            kmsg = activityReportDAOObj.caseActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Case");

            kmsg = activityReportDAOObj.contactActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Contact");

            kmsg = activityReportDAOObj.leadActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Lead");

            kmsg = activityReportDAOObj.opportunityActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Opportunity");

            kmsg = activityReportDAOObj.campaignActivities(requestParams, usersList);
            actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request, actList, "Campaign");
            }
            else
            {
            
            	 requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
                 requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            if(StringUtil.equal(module, "Account")){
                jarr = new JSONArray();
                kmsg = activityReportDAOObj.accountActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Account");
            }else if(StringUtil.equal(module, "Case")){
                jarr = new JSONArray();
                kmsg = activityReportDAOObj.caseActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Case");
            }else if(StringUtil.equal(module, "Contact")){
                jarr = new JSONArray();
                kmsg = activityReportDAOObj.contactActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Contact");
            }else if(StringUtil.equal(module, "Lead")){
                jarr = new JSONArray();
                kmsg = activityReportDAOObj.leadActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Lead");
            }else if(StringUtil.equal(module, "Opportunity")){
                jarr = new JSONArray();
                kmsg = activityReportDAOObj.opportunityActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Opportunity");

            }else { // For Campaign
                jarr = new JSONArray();
                kmsg = activityReportDAOObj.campaignActivities(requestParams, usersList);
                actList=HighPriorityActivitiesJson(kmsg.getEntityList(), request,  actList, "Campaign");

            }
           }
            jarr = new JSONArray(actList);
            jobj = HighPriorityActivitiesColumnJson(jarr, true, kmsg.getRecordTotalCount(),RequestContextUtils.getLocale(request));

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.High_Pri_Activity,
                    "High Priority Activities Report exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView highPriorityActivityPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        List ll = null;
        int dl = 0;
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String userid = sessionHandlerImplObj.getUserid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
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
                if(!jarr.getJSONObject(j).get("name").toString().equals("None")) {
                    kmsg = activityReportDAOObj.getHighPriorityActivityChart(jarr.getJSONObject(j).get("name").toString(), requestParams, usersList);
                    if (kmsg.getRecordTotalCount() > 0) {
                        result += "<slice title=\"" + jarr.getJSONObject(j).get("name").toString() + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
                    }
                }
            }
            result += "</pie>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

    public ModelAndView highPriorityActivityBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr1 = new JSONArray();
        String result = "";
        KwlReturnObject kmsg = null;
        List ll = null;
        int dl = 0;
        try {
           String companyid = sessionHandlerImplObj.getCompanyid(request);
            String userid = sessionHandlerImplObj.getUserid(request);
            String comboName = request.getParameter("comboname");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
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
            result = "<chart><series>";
            for (int j = 0; j < Integer.parseInt(jobj.get("totalCount").toString()); j++) {
                if(!jarr.getJSONObject(j).get("name").toString().equals("None"))
                    result += "<value xid=\"" + j + "\" >" + jarr.getJSONObject(j).get("name").toString() + "</value>";
            }
            result += "</series><graphs><graph gid=\"0\">";
            for (int k = 0; k < Integer.parseInt(jobj.get("totalCount").toString()); k++) {
                if(!jarr.getJSONObject(k).get("name").toString().equals("None")) {
                    kmsg = activityReportDAOObj.getHighPriorityActivityChart(jarr.getJSONObject(k).get("name").toString(), requestParams, usersList);
                    result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
                }
            }
            result += "</graph></graphs></chart>";
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }
}
