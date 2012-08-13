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
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.lead.bizservice.LeadManagementService;
import com.krawler.crm.utils.AuditAction;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.CrmCommonService;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.common.util.Header;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.lead.dm.LeadOwnerInfo;
import java.util.Collections;
import java.util.Map;

public class crmLeadController extends MultiActionController implements MessageSourceAware {
    private crmLeadDAO crmLeadDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private CrmCommonService crmCommonService;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private fieldDataManager fieldDataManagercntrl;
    private CometManagementService CometManagementService;
    private LeadManagementService leadManagementService;
    private crmCommonDAO crmCommonDAOObj;
    private MessageSource mSource;
    
    public void setCometManagementService(CometManagementService CometManagementService) {
        this.CometManagementService = CometManagementService;
    }

    /**
     * @return the activityManagementService
     */
    public LeadManagementService getLeadManagementService()
    {
        return leadManagementService;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }
    /**
     * @param leadManagementService the leadManagementService to set
     */
    public void setLeadManagementService(LeadManagementService leadManagementService)
    {
        this.leadManagementService = leadManagementService;
    }

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }

    /**
     * @param crmCommonService the crmCommonService to set
     */
    public void setCrmCommonService(CrmCommonService crmCommonService)
    {
        this.crmCommonService = crmCommonService;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public ModelAndView getLeads(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            boolean editconvertedlead =crmLeadHandler.editConvertedLead(request);
            String isarchive = request.getParameter("isarchive");
            String transfered = request.getParameter("transfered");
            String isconverted = request.getParameter("isconverted");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            String iscustomcolumn = request.getParameter("iscustomcolumn");
            String xtype = request.getParameter("xtype");
            String xfield = request.getParameter("xfield");
            String type = request.getParameter("type");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            String status = request.getParameter("status");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);

            jobj = getLeadManagementService().getLeads(companyid, userid, currencyid, selectExport, editconvertedlead, isarchive,
                    transfered, isconverted, searchJson, ss, config, isExport, heirarchyPerm, field, direction, iscustomcolumn,
                    xtype, xfield, type, start, limit, status, dateFormat, usersList);

            // add spreadsheet config
            /*JSONObject sConfig = getSpreadsheetHelper().getSpreadsheetConfigJson(SpreadsheetConstants.MODULE_LEAD, userid);
            jobj.append("config", sConfig);*/

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getLeadToEmailJson(List ll, HashMap<String, DefaultMasterItem> defaultMasterMap, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        JSONObject jobjTemp = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String[] myStringArray = new String[] {"leadownerid", "addstreet", "createdon", "title", "productid", "type"};
            ArrayList DO_NOT_SHOW_COLUMNS = new ArrayList();
            Collections.addAll(DO_NOT_SHOW_COLUMNS, myStringArray);
            String companyid = sessionHandlerImplObj.getCompanyid();

            // fetch data
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("relatedto", 1);
                tmpObj.put("relatedid", obj.getLeadid());
                tmpObj.put("firstname", obj.getFirstname());
                tmpObj.put("lastname", obj.getLastname());
                tmpObj.put("email", obj.getEmail());
                tmpObj.put("fname", obj.getFirstname());
                tmpObj.put("name", obj.getLastname());
                tmpObj.put("emailid", obj.getEmail());
                if(obj.getRatingID() != null && defaultMasterMap.containsKey(obj.getRatingID())) {
                    tmpObj.put("ratingid", defaultMasterMap.get(obj.getRatingID()).getValue());
                }
                if(obj.getLeadstatusID() != null && defaultMasterMap.containsKey(obj.getLeadstatusID())) {
                    tmpObj.put("leadstatusid", defaultMasterMap.get(obj.getLeadstatusID()).getValue());
                }
                if(obj.getIndustryID() != null && defaultMasterMap.containsKey(obj.getIndustryID())) {
                    tmpObj.put("industryid", defaultMasterMap.get(obj.getIndustryID()).getValue());
                }
                if(obj.getLeadsourceID() != null && defaultMasterMap.containsKey(obj.getLeadsourceID())) {
                    tmpObj.put("leadsourceid", defaultMasterMap.get(obj.getLeadsourceID()).getValue());
                }
                tmpObj.put("phone", obj.getPhone());
                tmpObj.put("revenue", obj.getRevenue());
                tmpObj.put("addstreet", obj.getAddstreet());
                jarr.put(tmpObj);
            }

            // fetch columns
            ArrayList filter_params = new ArrayList();
            ArrayList filter_names = new ArrayList();
            filter_names.add("dh.moduleName");
            filter_params.add(Constants.Crm_Lead_modulename);
            filter_names.add("dh.customflag");
            filter_params.add(false);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            requestParams.put("order_by", true);
            requestParams.put("order_type", "fieldsequence");
            JSONArray columnJSONArray = crmCommonService.getModuleColumns(requestParams, companyid, Constants.Crm_Lead_modulename);
            for(int cnt =0; cnt < columnJSONArray.length(); cnt++) {
                JSONObject columnObj = columnJSONArray.getJSONObject(cnt);
                if(!DO_NOT_SHOW_COLUMNS.contains(columnObj.getString("recordname"))) {
                        jobjTemp = new JSONObject();
                    jobjTemp.put("header", columnObj.getString("columnName"));
                    jobjTemp.put("tip", columnObj.getString("columnName"));
                    jobjTemp.put("width", 100);
                    jobjTemp.put("sortable", false);
                    jobjTemp.put("dataIndex", columnObj.getString("recordname"));
                        jarrColumns.put(jobjTemp);

                        jobjTemp = new JSONObject();
                    jobjTemp.put("name", columnObj.getString("recordname"));
                    jarrRecords.put(jobjTemp);
                    }
                }
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "relatedto");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "relatedid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "fname");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "name");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "emailid");
            jarrRecords.put(jobjTemp);

            jobj.put("columns", jarrColumns);
            jMeta.put("totalProperty", "totalCount");
            jMeta.put("root", "data");
            jMeta.put("fields", jarrRecords);
            jobj.put("metaData", jMeta);
        jobj.put("success", true);
        jobj.put("data", jarr);
        jobj.put("totalCount", totalSize);
       } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public ModelAndView getLeadsToEmail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", false);
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("email", true);
            requestParams.put("config", true);
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }

            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            kmsg = crmLeadDAOObj.getLeads(requestParams, usersList);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAOObj);

            jobj = getLeadToEmailJson(kmsg.getEntityList(), defaultMasterMap, request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView leadExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        String view = "jsonView";
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            boolean editconvertedlead =crmLeadHandler.editConvertedLead(request);
            String isarchive = request.getParameter("isarchive");
            String transfered = request.getParameter("transfered");
            String isconverted = request.getParameter("isconverted");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);

            jobj = getLeadManagementService().LeadExport(companyid, userid, currencyid, selectExport, editconvertedlead,
                    isarchive, transfered, isconverted, searchJson, ss, config, isExport, heirarchyPerm, field, direction, dateFormat);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.LEAD_EXPORT,
                    "Lead data exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView getAllLeads(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
       try{
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImplObj.getRecursiveUsersList(request);
            JSONArray jarr = new JSONArray();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.validflag");
            filter_params.add(1);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            filter_names.add("c.istransfered");
            filter_params.add("0");
            filter_names.add("c.isarchive");
            filter_params.add(false);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            if(!heirarchyPerm) {
                filter_names.add("INlo.usersByUserid.userID");
                filter_params.add(usersList);
            }
            requestParams.put("filterQaulified", true);
            kmsg = crmLeadDAOObj.getAllLeads(requestParams, filter_names, filter_params);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CrmLead obj = (CrmLead) ite.next();
                JSONObject jtemp = new JSONObject();

                jtemp.put("cusername", obj.getLastname());
                jtemp.put("cuserid", obj.getLeadid());
                jtemp.put("cemailid", obj.getEmail());
//                jtemp.put("ccompany", obj.getCompanyname());
                jtemp.put("caddress", obj.getAddstreet());
                jtemp.put("ccontactno", obj.getPhone());
                jarr.put(jtemp);
            }
            jobj.put("data", jarr);
       } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveLeads(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        CrmLead lead = null;

       try{
            String timezone = sessionHandlerImpl.getTimeZoneDifference(request);
            String defaultLeadType = crmLeadHandler.getDefaultLeadType(request);
            Integer operationCode = CrmPublisherHandler.ADDRECORDCODE;
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String userid = sessionHandlerImplObj.getUserid(request);
            String leadIds = jobj.getString("leadid");
            String arrayId[] = leadIds.split(",");

            String moveToLead = request.getParameter("movetolead");
            int duplicateLead = 0;


            if(jobj.has("productid")) {
                String[] arrayPid = jobj.getString("productid").split(",");
                jobj.put("productsId",arrayPid);
            }
            for(int i=0;i<arrayId.length;i++){
                String id=arrayId[i];

                // Kuldeep Singh :Check Duplicate Entry for Lead while Move to Lead from Campaign
                if(!StringUtil.isNullOrEmpty(moveToLead)){

                    String firstname = jobj.getString("firstname");
                    String lastname = jobj.getString("lastname");
                    String email = jobj.getString("email");

                    duplicateLead = crmCommonDAOObj.chekcDuplicateEntryForLead(firstname, lastname, email, companyid);
                }

                // Kuldeep Singh : Here Duplicacy is checked only for Move To Lead from Campaign
                if(duplicateLead==0){

                    // Kuldeep Singh : Save Lead business logic
                    JSONObject leadjson = leadManagementService.saveLead(id,userid,companyid,timezone, defaultLeadType, operationCode,jobj);

                    lead = (CrmLead) leadjson.get("lead");
                    jobj = leadjson.getJSONObject("jobj");
                    String successMsg = leadjson.getString("msg");
                    operationCode = leadjson.getInt("operationCode");

                    // Audit trial entry for Valid recors
                    if(lead.getValidflag() == 1 && operationCode==CrmPublisherHandler.ADDRECORDCODE ){
                           auditTrailDAOObj.insertAuditLog(AuditAction.LEAD_CREATE,
                    		   ((lead.getLastname()==null)?"":lead.getLastname()) + " - Lead created ",
                                   request, id);

                    } else if(lead.getValidflag() == 1 && operationCode==CrmPublisherHandler.UPDATERECORDCODE ){

                           auditTrailDAOObj.insertAuditLog(AuditAction.LEAD_UPDATE,
                               jobj.getString("auditstr") + " Lead - " + ((lead.getLastname()==null)?"":lead.getLastname()) + " ",
                                   request, id);
                        }

                    myjobj.put("success", true);
                    myjobj.put("ID", lead.getLeadid());
                    myjobj.put("msg", successMsg);
                    myjobj.put("createdon", jobj.has("createdon") ? jobj.getLong("createdon") : "");
                    myjobj.put("updatedon", jobj.has("updatedon") ? jobj.getLong("updatedon") : "");
                    myjobj.put(Constants.AUTOCUSTOMFIELD, jobj.has(Constants.AUTOCUSTOMFIELD) ? jobj.getJSONArray(Constants.AUTOCUSTOMFIELD) : "");
                    myjobj.put("movetoleadMsg", "Lead has been saved successfully.");

                } else {
                    myjobj.put("movetoleadMsg", "Lead with same firstname, lastname and email is already present in Lead list.");
                }

            }
            txnManager.commit(status);
            JSONObject cometObj = jobj;
            if(!StringUtil.isNullObject(lead)) {
                if((lead.getCreatedOn()!=null)) {
                    cometObj.put("createdon", lead.getCreatedOn());
                }
            }
            if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
            	if(arrayId[0].equals("0"))
            		crmCommonService.validateMassupdate(new String[]{lead.getLeadid()},"Lead",companyid);
            	else
                crmCommonService.validateMassupdate(arrayId,"Lead",companyid);
                cometObj.put("leadid",  leadIds);
                cometObj.put("ismassedit",  true);
            }
            if(duplicateLead==0){
                publishLeadModuleInformation(request, cometObj, operationCode, companyid, userid);
            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

	public ModelAndView updateMassLeads(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject myjobj = new JSONObject();
		KwlReturnObject kmsg = null;
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		HashMap<String, Object> requestParams = new HashMap<String, Object>();
		ArrayList filter_names = new ArrayList();
		ArrayList filter_params = new ArrayList();

		try {
			String successMSG = "";
			Integer operationCode = CrmPublisherHandler.UPDATERECORDCODE;
			JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
			String companyid = sessionHandlerImplObj.getCompanyid(request);
			String userid = sessionHandlerImplObj.getUserid(request);
			String leadIds = jobj.getString("leadid");
			List<String> rejectedleadids = new ArrayList<String>();
			List<String> selectedleadids = new ArrayList<String>(Arrays.asList(leadIds.split(",")));
			if (jobj.has("productid")) {
				String[] arrayPid = jobj.getString("productid").split(",");
				jobj.put("productsId", arrayPid);
			}
			jobj.put("userid", userid);
			jobj.put("companyid", companyid);
			jobj.put("isconverted", "0");
			jobj.put("istransfered", "0");
			if (jobj.has("updatedon")) {
                jobj.put("updatedon", jobj.getLong("updatedon"));
            } else {
                jobj.put("updatedon", new Date().getTime());
            }
			jobj.put("tzdiff", sessionHandlerImpl.getTimeZoneDifference(request));
			if (jobj.has("companyname")) {
				jobj.put("company", jobj.getString("companyname"));
			}
			if (jobj.has("addstreet")) {
				jobj.put("street", jobj.getString("addstreet"));
			}
			JSONArray jcustomarray = null;
			if (jobj.has("customfield")) {
				jcustomarray = jobj.getJSONArray("customfield");
			}

			if (jobj.has("type") && StringUtil.isNullOrEmpty(jobj.getString("type"))) {
				jobj.put("type", crmLeadHandler.getDefaultLeadType(request));
			}

			// Check lead status - Qualified status can't be changed or
			// Lead status cannot be changed to Qualified unless it is
			// converted into Opportunity or Account
			operationCode = CrmPublisherHandler.UPDATERECORDCODE;
			if (jobj.has("leadstatusid")) {
				DefaultMasterItem qualified = getLeadStatus(Constants.LEADSTATUSID_QUALIFIED, companyid);
				String qualifiedStatusId = qualified.getID(), oldStatusId = "", name = qualified.getValue();
				requestParams.clear();
				filter_names.clear();
				filter_params.clear();

				filter_names.add("c.deleteflag");
				filter_params.add(0);

				filter_names.add("c.isarchive");
				filter_params.add(false);

				filter_names.add("INc.id");
				filter_params.add("'" + leadIds.replaceAll(",", "','") + "'");
				requestParams.put("filter_names", filter_names);
				requestParams.put("filter_params", filter_params);
				KwlReturnObject leadReObj = crmLeadDAOObj.getLeads(requestParams);
				List<CrmLead> l = leadReObj.getEntityList();
				for (CrmLead crmlead : l) {
					if (crmlead.getCrmCombodataByLeadstatusid() != null) {
						oldStatusId = crmlead.getCrmCombodataByLeadstatusid().getID();
						if ((jobj.getString("leadstatusid").equals(qualifiedStatusId) && !oldStatusId.equals(qualifiedStatusId))) {
							// general --> qualified
							selectedleadids.remove(crmlead.getLeadid());
							rejectedleadids.add(crmlead.getLeadid());
						} else if ((!jobj.getString("leadstatusid").equals(qualifiedStatusId) && oldStatusId.equals(qualifiedStatusId))) {
							// qualified --> general
							selectedleadids.remove(crmlead.getLeadid());
							rejectedleadids.add(crmlead.getLeadid());
						}
					}
				}
			}
			jobj.put("leadid", selectedleadids.toArray(new String[] {}));
			if (jobj.has("createdon")) {
				jobj.put("createdon", jobj.getLong("createdon"));
			}
			if (jobj.has("customfield")) {
				HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
				customrequestParams.put("customarray", jcustomarray);
				customrequestParams.put("modulename", Constants.Crm_Lead_modulename);
				customrequestParams.put("moduleprimarykey", Constants.Crm_Leadid);

				customrequestParams.put("companyid", companyid);
				customrequestParams.put("customdataclasspath", Constants.Crm_lead_custom_data_classpath);
				KwlReturnObject customDataresult = null;
				for (String id : selectedleadids) {
					customrequestParams.put("modulerecid", id);
					customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
				}
				if (customDataresult != null && customDataresult.getEntityList().size() > 0) {
					jobj.put("CrmLeadCustomDataobj", true);
				}
			}
			kmsg = crmLeadDAOObj.updateMassLeads(jobj);
			// TODO add audit logging
			// lead = (CrmLead) kmsg.getEntityList().get(0);
			// if (lead.getValidflag() == 1) {
			// auditTrailDAOObj.insertAuditLog(AuditAction.LEAD_UPDATE,
			// jobj.getString("auditstr") + " Lead - " + ((jobj.has("lastname"))
			// ? jobj.getString("lastname") : "") + " ", request, id);
			// }

			myjobj.put("success", true);
			myjobj.put("ID", selectedleadids);
			myjobj.put("msg", successMSG);
			myjobj.put("createdon", jobj.has("createdon") ? jobj.getLong("createdon") : "");

			txnManager.commit(status);
			JSONObject cometObj = jobj;
			// TODO post comet details
			// if (!StringUtil.isNullObject(lead)) {
			// if (!StringUtil.isNullObject(lead.getCreatedon())) {
			// cometObj.put("createdon", lead.getCreatedonGMT());
			// }
			// }
			if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
				crmCommonService.validateMassupdate(selectedleadids.toArray(new String[] {}), "Lead", companyid);
				cometObj.put("leadid", leadIds);
				cometObj.put("ismassedit", true);
			}

			publishLeadModuleInformation(request, cometObj, operationCode, companyid, userid);
		} catch (SessionExpiredException e) {
			logger.warn(e.getMessage(), e);
			txnManager.rollback(status);
		} catch (JSONException e) {
			logger.warn(e.getMessage(), e);
			txnManager.rollback(status);
		} catch (ServiceException e) {
			logger.warn(e.getMessage(), e);
			txnManager.rollback(status);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			txnManager.rollback(status);
		}
		return new ModelAndView("jsonView", "model", myjobj.toString());
	}

	private DefaultMasterItem getLeadStatus(String status, String companyid) throws ServiceException{
		ArrayList filter_names = new ArrayList();
		ArrayList filter_params = new ArrayList();
		ArrayList order_by = new ArrayList();
		ArrayList order_type = new ArrayList();
		filter_names.add("d.company.companyID");
		filter_params.add(companyid);
		filter_names.add("d.mainID");
		filter_params.add(status);
		order_by.add("d.value");
		order_type.add("asc");
		HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
		comboRequestParams.put("filter_names", filter_names);
		comboRequestParams.put("filter_params", filter_params);
		comboRequestParams.put("order_by", order_by);
		comboRequestParams.put("order_type", order_type);
		List ll = crmManagerDAOObj.getComboData("Lead Status", comboRequestParams);
		DefaultMasterItem crmCombodata = null;
		if (!ll.isEmpty()) {
			crmCombodata = (DefaultMasterItem) ll.get(0);
		}
		return crmCombodata;
	}

    private void publishLeadModuleInformation(HttpServletRequest request, JSONObject cometObj, int operationCode, String companyid, String userid) throws ServiceException, JSONException, SessionExpiredException {
        String randomNumber = request.getParameter("randomnumber");
        CometManagementService.publishModuleInformation(companyid, userid, randomNumber, Constants.Crm_Lead_modulename, Constants.Crm_leadid, operationCode, cometObj, CometConstants.CRMUPDATES);
    }

    public ModelAndView saveLeadOwner(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try{
            String leadid = request.getParameter("leadid");
            String owners = request.getParameter("owners");
            String mainowner = request.getParameter("mainOwner");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("leadid", leadid);
            requestParams.put("owners", owners);
            requestParams.put("mainOwner", mainowner);
            kmsg = crmLeadDAOObj.saveLeadOwners(requestParams);

            // Fetch subowners name list
            List<String> idsList = new ArrayList<String>();
            idsList.add(leadid);
            Map<String, List<LeadOwnerInfo>> ownersMap = crmLeadDAOObj.getLeadOwners(idsList);
            String[] ownerInfo = crmLeadHandler.getAllLeadOwners(ownersMap.get(leadid));
            myjobj.put("subowners", ownerInfo[1]);
            myjobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView getExistingLeadOwners(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
       try{
            String[] ownerInfo = crmLeadHandler.getAllLeadOwners(crmLeadDAOObj, request.getParameter("leadid"));
            jobj.put("mainOwner",ownerInfo[2] );
            jobj.put("ownerids", ownerInfo[3]);
       } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView newLead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        String userid = request.getParameter("userid");
        KwlReturnObject kmsg = null;
        CrmLead lead = null;
        String[] username = request.getParameter("username").split(" ");
        String fname = username[0];
        String lname = username[1];
        String emailid = request.getParameter("emailid");
        String address = request.getParameter("address");
        String contactno = request.getParameter("contactno");
        String company = request.getParameter("company");
        String id = java.util.UUID.randomUUID().toString();
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            jobj.put("leadid", id);
            jobj.put("companyid", companyid);
            jobj.put("leadownerid", userid);
            jobj.put("userid", userid);
            jobj.put("firstname", fname);
            jobj.put("lastname", lname);
            jobj.put("company", company);
            jobj.put("phone", contactno);
            jobj.put("street", address);
            jobj.put("isconverted", "0");
            jobj.put("istransfered", "0");
            jobj.put("validflag", 1);
            jobj.put("email", emailid);
            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
//            jobj.put("createdon", new Date());
            kmsg = crmLeadDAOObj.addLeads(jobj);
            lead = (CrmLead) kmsg.getEntityList().get(0);

            myjobj.put("success", true);
            myjobj.put("ID", lead.getLeadid());
            txnManager.commit(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView repLead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        String jsondata = request.getParameter("val");
        KwlReturnObject kmsg = null;
        CrmLead lead = null;
        JSONObject jobj;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            jobj = new JSONObject(jsondata);
            JSONArray jarr = new JSONArray();
            jarr = jobj.getJSONArray("userdata");
            for (int ctr = 0; ctr < jarr.length(); ctr++) {
                jobj = jarr.getJSONObject(ctr);
                String[] username = jobj.getString("username").split(" ");
                String fname = username[0];
                String lname = username[1];
                String emailid = jobj.getString("emailid");
                String address = jobj.getString("address");
                String contactno = jobj.getString("contactno");

                jobj.put("firstname", fname);
                jobj.put("lastname", lname);
                jobj.put("email", emailid);
                jobj.put("phone", contactno);
                jobj.put("street", address);
                jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));

                kmsg = crmLeadDAOObj.editLeads(jobj);
                lead = (CrmLead) kmsg.getEntityList().get(0);

                myjobj.put("success", true);
                myjobj.put("ID", lead.getLeadid());
            }
            txnManager.commit(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView deleteLead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        KwlReturnObject kmsg = null;
        CrmLead lead = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);

            myjobj = new JSONObject();
            myjobj.put("success", false);

            if (StringUtil.bNull(request.getParameter("jsondata"))) {
                String jsondata = request.getParameter("jsondata");
                JSONArray jarr = new JSONArray("[" + jsondata + "]");

                ArrayList leadids = new ArrayList();
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobject = jarr.getJSONObject(i);
                    leadids.add(jobject.getString("leadid").toString());
                }

                String[] arrayid = (String[]) leadids.toArray(new String[]{});
                JSONObject jobj = new JSONObject();
                jobj.put("deleteflag", 1);
                jobj.put("userid", userid);
                jobj.put("leadid", arrayid);
                jobj.put("updatedon", new Date().getTime());
                jobj.put("tzdiff", timeZoneDiff);

                kmsg = crmLeadDAOObj.updateMassLeads(jobj);

                List<CrmLead> ll = crmLeadDAOObj.getLeads(leadids);

                if (ll != null) {
                    for (int i = 0; i < ll.size(); i++) {
                        CrmLead leadaudit = (CrmLead) ll.get(i);
                        if (leadaudit.getValidflag() == 1) {
                            auditTrailDAOObj.insertAuditLog(AuditAction.LEAD_DELETE,
                                    StringUtil.getFullName(leadaudit.getFirstname(), leadaudit.getLastname()) + " - Lead deleted ",
                                    request, leadaudit.getLeadid());
                        }
                    }

                }

                JSONObject cometObj = new JSONObject();
                cometObj.put("ids", new JSONArray("[" + jsondata + "]"));
                publishLeadModuleInformation(request, cometObj, CrmPublisherHandler.DELETERECORDCODE, sessionHandlerImpl.getCompanyid(request), userid);

                myjobj.put("success", true);
                myjobj.put("ID", leadids.toArray());
            }

            txnManager.commit(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.mSource=messageSource;
		
	}
}
