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
import static com.krawler.common.notification.web.NotificationConstants.FCFS_LEADROUTING;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.Docs;
import com.krawler.common.admin.FieldComboData;
import com.krawler.common.admin.FieldParams;
import com.krawler.common.admin.User;
import com.krawler.common.notification.bizservice.NotificationManagementService;
import com.krawler.common.notification.web.NotificationConstants;
import com.krawler.common.notification.web.NotificationConstants.CHANNEL;
import com.krawler.common.notification.web.NotificationConstants.NOTIFICATIONSTATUS;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.DataInvalidateException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.SystemUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.activity.bizservice.ActivityManagementService;
import com.krawler.crm.contact.bizservice.ContactManagementService;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmLeadCustomData;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.LeadConversionMappings;
import com.krawler.crm.database.tables.LeadRoutingUsers;
import com.krawler.crm.database.tables.webtoleadform;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.utils.AuditAction;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.esp.handlers.APICallHandlerService;
import com.krawler.esp.handlers.Receiver;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.handlers.webtoLeadFormHandler;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.esp.web.resource.Links;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.companyDetails.companyDetailsDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.productModule.crmProductDAO;
import com.krawler.spring.documents.documentDAO;
import com.krawler.spring.importFunctionality.ImportDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class crmLeadCommonController extends MultiActionController {
    private crmManagerDAO crmManagerDAOObj;
    private crmCommonDAO crmCommonDAOObj;
    private ImportDAO importDao;
    private auditTrailDAO auditTrailDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private crmAccountDAO crmAccountDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmContactDAO crmContactDAOObj;
    private crmLeadDAO crmLeadDAOObj;
    private crmProductDAO crmProductDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private webtoLeadFormHandler webtoLeadFormHandlerObj;
    private companyDetailsDAO companyDetailsDAOObj;
    private fieldDataManager fieldDataManagercntrl;
    private ActivityManagementService activityManagementService;
    private ContactManagementService contactManagementService;
    private commentDAO crmCommentDAOObj;
    private documentDAO crmDocumentDAOObj;
    private NotificationManagementService NotificationManagementServiceDAO;
    private APICallHandlerService apiCallHandlerService;
    private String supportEmailAddress;
    private HibernateTemplate hibernateTemplate;

    public void setApiCallHandlerService(APICallHandlerService apiCallHandlerService)
    {
        this.apiCallHandlerService = apiCallHandlerService;
    }

    public void setNotificationManagementServiceDAO(NotificationManagementService NotificationManagementServiceDAO) {
        this.NotificationManagementServiceDAO = NotificationManagementServiceDAO;
    }

    public void setContactManagementService(ContactManagementService contactManagementService) {
        this.contactManagementService = contactManagementService;
    }
     public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }
    public ActivityManagementService getActivityManagementService() {
        return activityManagementService;
    }

    public void setActivityManagementService(ActivityManagementService activityManagementService) {
        this.activityManagementService = activityManagementService;
    }
    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }
    
    public void setcompanyDetailsDAO(companyDetailsDAO companyDetailsDAOObj1) {
        this.companyDetailsDAOObj = companyDetailsDAOObj1;
    }

    public void setwebtoLeadFormHandler(webtoLeadFormHandler webtoLeadFormHandlerObj1) {
        this.webtoLeadFormHandlerObj = webtoLeadFormHandlerObj1;
    }

    public void setimportDAO(ImportDAO importDao) {
        this.importDao = importDao;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
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

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setcrmDocumentDAO(documentDAO crmDocumentDAOObj1) {
        this.crmDocumentDAOObj = crmDocumentDAOObj1;
    }
    
    public void setSupportEmailAddress(String supportEmailAddress) {
		this.supportEmailAddress = supportEmailAddress;
	}
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
    
    public void setcrmProductDAO(crmProductDAO crmProductDAOObj1) {
        this.crmProductDAOObj = crmProductDAOObj1;
    }

	public JSONObject getModuleRecord(HttpServletRequest request, String moduleName,String companyid,JSONObject leadjObj, Map<String, Object> moduleFields, Map<String, Object> ColumnMappedList) throws ServiceException, JSONException, DataInvalidateException, SessionExpiredException{
        KwlReturnObject kmsg = null;
        JSONArray leadcustomFieldJArray = leadjObj.getJSONArray("customfield");
        JSONArray moduleCustomfieldArray = new JSONArray();
        JSONObject modulejObj = new JSONObject();
        moduleFields.clear();
        ColumnMappedList.clear();
        try {
                ArrayList filter_params = new ArrayList();
                ArrayList filter_names = new ArrayList();
                filter_names.add("m.company.companyID");
                filter_params.add(companyid);
                filter_names.add("m.modulefield.moduleName");
                filter_params.add(moduleName);
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_params", filter_params);

                kmsg = crmCommonDAOObj.getMappedHeaders(requestParams);

                HashMap<String,String> customFieldMap =  new HashMap<String, String>();
                for (int cnt = 0; cnt < leadcustomFieldJArray.length(); cnt++) {
                    JSONObject custfindObj = leadcustomFieldJArray.getJSONObject(cnt);
                    String fieldname = custfindObj.getString(Constants.Crm_custom_field);
                    String fielddbname = custfindObj.getString(fieldname);
                    customFieldMap.put(custfindObj.getString("filedid"),custfindObj.getString(fielddbname));
                }
                List list = kmsg.getEntityList();
                Iterator ite = list.iterator();
                StringBuilder refNotFoundStr = new StringBuilder();

                while(ite.hasNext()) {
                    boolean refNotFoundFlag = false;
                    LeadConversionMappings mapobj = (LeadConversionMappings) ite.next();
                    DefaultHeader leadDefault  = mapobj.getLeadfield();
                    DefaultHeader moduleDefault  = mapobj.getModulefield();
                    String newVal = "";
                    if(leadDefault.isCustomflag()) {
                        String fieldId = leadDefault.getPojoheadername();
                        newVal = customFieldMap.get(fieldId);
                        if(leadDefault.getXtype().equals("4")) { //  normal custom combo
                            ArrayList filterNames = new ArrayList<String>();
                            ArrayList filterValues = new ArrayList<Object>();
                            filterNames.add("id");
                            filterValues.add(newVal);
                            filterNames.add("fieldid");
                            filterValues.add(fieldId);
                            filterNames.add(LeadConstants.Crm_deleteflag);
                            filterValues.add(0);
                            requestParams.clear();
                            requestParams.put("companyid", companyid);
                            requestParams.put("addMissingMaster", "false");
                            List li = importDao.getCustomComboID(requestParams,"value",filterNames,filterValues);
                            if(li.size()>0) {
                                newVal = li.get(0).toString();
                            } else {
                                refNotFoundFlag = true;
                            }
                        } else if(leadDefault.getXtype().equals("7")) {
                                String multiVal = StringUtil.getmultiSelectedColumnValue(newVal);
                                newVal = "";
                                    ArrayList filterNames = new ArrayList<String>();
                                    ArrayList filterValues = new ArrayList<Object>();
                                    filterNames.add("INid");
                                    filterValues.add(multiVal);
                                    filterNames.add("fieldid");
                                    filterValues.add(fieldId);
                                    filterNames.add(LeadConstants.Crm_deleteflag);
                                    filterValues.add(0);
                                    requestParams.clear();
                                    requestParams.put("companyid", companyid);
                                    requestParams.put("addMissingMaster", "false");
                                    List<String> li = importDao.getCustomComboID(requestParams,"value",filterNames,filterValues);

                                    StringBuilder newValbuild = new StringBuilder();
                                    
                                    for(String s : li){
                                        newValbuild.append(s).append(",");
                                    }
                                    
                                    if(newValbuild.length()>0) {
                                        newVal = newValbuild.substring(0,newValbuild.length()-1);
                                    } else {
                                        refNotFoundFlag = true;
                                    }
                                    
                        } else if(leadDefault.getXtype().equals("8")) { // reference custom combo
                            ArrayList filterNames = new ArrayList<String>();
                            ArrayList filterValues = new ArrayList<Object>();
                            filterNames.add(leadDefault.getRefFetchColumn_HbmName());
                            filterValues.add(newVal);
                            requestParams.clear();
                            requestParams.put("companyid", companyid);
                            requestParams.put("defaultheader", moduleDefault.getDefaultHeader().trim());
                            requestParams.put("addMissingMaster", "false");
                            List<String> li = importDao.getRefModuleData(requestParams,leadDefault.getRefModule_PojoClassName(),leadDefault.getRefDataColumn_HbmName(), leadDefault.getConfigid(), filterNames,filterValues);
                            if(li.size()>0) {
                                newVal = li.get(0);
                            } else {
                                refNotFoundFlag = true;
                            }
                        }
                    } else {
                        newVal = leadjObj.getString(leadDefault.getRecordname());
                        if(!StringUtil.isNullOrEmpty(newVal)) {// get value from default column
                            if(leadDefault.getXtype().equals("4")){ // Single-Select drop down case handled : Kuldeep Singh
                                ArrayList filterNames = new ArrayList<String>();
                                ArrayList filterValues = new ArrayList<Object>();
                                filterNames.add(leadDefault.getRefFetchColumn_HbmName());
                                filterValues.add(newVal);
                                requestParams.clear();
                                requestParams.put("companyid", companyid);
                                requestParams.put("defaultheader", moduleDefault.getDefaultHeader().trim());
                                List<String> li = importDao.getRefModuleData(requestParams,leadDefault.getRefModule_PojoClassName(),leadDefault.getRefDataColumn_HbmName(), leadDefault.getConfigid(), filterNames,filterValues);
                                if(li.size()>0) {
                                    newVal = li.get(0);
                                } else {
                                    refNotFoundFlag = true;
                                }
                            } else if(leadDefault.getXtype().equals("7")){ // Multi-Select drop down case handled : Kuldeep Singh
                                String multiVal = StringUtil.getmultiSelectedColumnValue(newVal);
                                newVal = "";
                                
                                    ArrayList filterNames = new ArrayList<String>();
                                    ArrayList filterValues = new ArrayList<Object>();
                                    filterNames.add("IN"+leadDefault.getRefFetchColumn_HbmName());
                                    filterValues.add(multiVal);
                                    requestParams.clear();
                                    requestParams.put("companyid", companyid);
                                    requestParams.put("defaultheader", moduleDefault.getDefaultHeader().trim());
                                    List<String> li = importDao.getRefModuleData(requestParams,leadDefault.getRefModule_PojoClassName(),leadDefault.getRefDataColumn_HbmName(), leadDefault.getConfigid(), filterNames,filterValues);

                                    StringBuilder newValbuild = new StringBuilder();
                                    for(String s : li){
                                        newValbuild.append(s).append(",");
                                    }
                                    
                                    if(newValbuild.length()>0) {
                                        newVal = newValbuild.substring(0,newValbuild.length()-1);
                                    } else {
                                        refNotFoundFlag = true;
                                    }
                            }

                        }
                    }

                    if(moduleDefault.isCustomflag()) {
                        if(!StringUtil.isNullOrEmpty(newVal)) {
                            String moduleDefaultfieldId = moduleDefault.getPojoheadername();
                            if(moduleDefault.getXtype().equals("4")) {//default to normal custom column
                                ArrayList filterNames = new ArrayList<String>();
                                ArrayList filterValues = new ArrayList<Object>();
                                filterNames.add("value");
                                filterValues.add(newVal);
                                filterNames.add("fieldid");
                                filterValues.add(moduleDefaultfieldId);
                                filterNames.add(LeadConstants.Crm_deleteflag);
                                filterValues.add(0);
                                requestParams.clear();
                                requestParams.put("companyid", companyid);
                                requestParams.put("addMissingMaster", "false");
                                List<String> li = importDao.getCustomComboID(requestParams, "id",filterNames,filterValues);
                                if(li.size()>0) {
                                    newVal = li.get(0);
                                } else {
                                    refNotFoundFlag = true;
                                }
                            } else if(moduleDefault.getXtype().equals("7")) {
                                String multiVal = StringUtil.getmultiSelectedColumnValue(newVal);
                                newVal = "";
                                                                
                                    ArrayList filterNames = new ArrayList<String>();
                                    ArrayList filterValues = new ArrayList<Object>();
                                    filterNames.add("INvalue");
                                    filterValues.add(multiVal);
                                    filterNames.add("fieldid");
                                    filterValues.add(moduleDefaultfieldId);
                                    filterNames.add(LeadConstants.Crm_deleteflag);
                                    filterValues.add(0);
                                    requestParams.clear();
                                    requestParams.put("companyid", companyid);
                                    requestParams.put("addMissingMaster", "false");
                                    List<String> li = importDao.getCustomComboID(requestParams,"id",filterNames,filterValues);

                                    StringBuilder newValbuild = new StringBuilder();
                                    for(String s : li){
                                        newValbuild.append(s).append(",");
                                    }

                                    if(newValbuild.length() > 0) {
                                        newVal = newValbuild.substring(0,newValbuild.length()-1);
                                    } else {
                                        refNotFoundFlag = true;
                                    }

                            } else if(moduleDefault.getXtype().equals("8")) { // For default to reference custom combo
                                ArrayList filterNames = new ArrayList<String>();
                                ArrayList filterValues = new ArrayList<Object>();
                                filterNames.add(moduleDefault.getRefDataColumn_HbmName());
                                filterValues.add(newVal);
                                requestParams.clear();
                                requestParams.put("companyid", companyid);
                                requestParams.put("defaultheader", moduleDefault.getDefaultHeader().trim());
                                requestParams.put("addMissingMaster", "false");
                                List li = importDao.getRefModuleData(requestParams,moduleDefault.getRefModule_PojoClassName(),moduleDefault.getRefFetchColumn_HbmName(), moduleDefault.getConfigid(), filterNames,filterValues);
                                if(li.size()>0) {
                                    newVal = li.get(0).toString();
                                } else {
                                    refNotFoundFlag = true;
                                }
                            }
                            if(refNotFoundStr.length() == 0 && !refNotFoundFlag) {
                                JSONObject tempJobj = new JSONObject();
                                tempJobj.put("filedid", moduleDefaultfieldId);
                                tempJobj.put(Constants.Crm_custom_field, moduleDefault.getDefaultHeader());
                                tempJobj.put(moduleDefault.getDefaultHeader(), moduleDefault.getDbcolumnname());
                                tempJobj.put(moduleDefault.getDbcolumnname(), newVal);
                                tempJobj.put("refcolumn_name",Constants.Custom_Column_Prefix+ moduleDefault.getDbcolumnrefname());
                                tempJobj.put("xtype", moduleDefault.getXtype());

                                moduleCustomfieldArray.put(tempJobj);
                            } else {
                                if(refNotFoundFlag) {
                                    refNotFoundStr.append(moduleDefault.getDefaultHeader()).append(": ").append(newVal).append(", <BR />");
                                }
                            }
                        }
                        moduleFields.put(moduleDefault.getRecordname(), newVal);
                    } else {
                        if(!StringUtil.isNullOrEmpty(newVal)) {
                            if(moduleDefault.getXtype().equals("4")){ // Single-Select drop down case handled : Kuldeep Singh
                                ArrayList filterNames = new ArrayList<String>();
                                ArrayList filterValues = new ArrayList<Object>();
                                filterNames.add(moduleDefault.getRefDataColumn_HbmName());
                                filterValues.add(newVal);
                                requestParams.clear();
                                requestParams.put("companyid", companyid);
                                requestParams.put("defaultheader", moduleDefault.getDefaultHeader().trim());
                                requestParams.put("addMissingMaster", "false");
                                List li = importDao.getRefModuleData(requestParams,moduleDefault.getRefModule_PojoClassName(),moduleDefault.getRefFetchColumn_HbmName(), moduleDefault.getConfigid(), filterNames,filterValues);
                                if(li.size()>0) {
                                    newVal = li.get(0).toString();
                                } else {
                                    refNotFoundFlag = true;
                                }
                            } else if(moduleDefault.getXtype().equals("7")){ // Multi-Select drop down case handled : Kuldeep Singh
                                String multiVal = StringUtil.getmultiSelectedColumnValue(newVal);
                                newVal = "";
                                
                                    ArrayList filterNames = new ArrayList<String>();
                                    ArrayList filterValues = new ArrayList<Object>();
                                    filterNames.add("IN"+moduleDefault.getRefDataColumn_HbmName());
                                    filterValues.add(multiVal);
                                    requestParams.clear();
                                    requestParams.put("companyid", companyid);
                                    requestParams.put("defaultheader", moduleDefault.getDefaultHeader().trim());
	                            requestParams.put("addMissingMaster", "false");
                                    List<String> li = importDao.getRefModuleData(requestParams,moduleDefault.getRefModule_PojoClassName(),moduleDefault.getRefFetchColumn_HbmName(), moduleDefault.getConfigid(), filterNames,filterValues);

                                    StringBuilder newValbuild = new StringBuilder();
                                    for(String s : li){
                                        newValbuild.append(s).append(",");
                                    }

                                    if(newValbuild.length() > 0) {
                                        newVal = newValbuild.substring(0,newValbuild.length()-1);
                                    } else {
                                        refNotFoundFlag = true;
                                    }
                           }

                        }
                        moduleFields.put(moduleDefault.getRecordname(), newVal);
                        if(refNotFoundStr.length() == 0  && !refNotFoundFlag) {
                            modulejObj.put(moduleDefault.getRecordname(), newVal);
                        } else {
                            if(refNotFoundFlag) {
                                refNotFoundStr.append(moduleDefault.getDefaultHeader()).append(": ").append(newVal).append(", <BR />");
                            }
                        }
                    }
                    ColumnMappedList.put(moduleDefault.getRecordname(), leadDefault); // mapped module record name with lead Display name
                }
                if(refNotFoundStr.length() == 0) {
                    modulejObj.put("customfield", moduleCustomfieldArray);
                    modulejObj.put("success", true);
                } else {
                    modulejObj.put("success", false);
                    modulejObj.put("msg", refNotFoundStr.substring(0, refNotFoundStr.length()-1));
                }
        }catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return modulejObj;
    }
    
    public ModelAndView convertLeads(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        boolean refNotFoundFlag = false;
        String refNotFoundStr = "";
        KwlReturnObject kmsg = null;
        CrmLead lead = null;
        CrmAccount acc = null;
        CrmOpportunity opp = null;
        String contactid = null;
        boolean b = false;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        JSONObject accModulejObj = new JSONObject();
        JSONObject oppModulejObj = new JSONObject();
        JSONObject contactModulejObj = new JSONObject();
        JSONObject resJson = new JSONObject();
        try {
            Map<String, Object> moduleFields = new HashMap<String, Object>();
            Map<String, Object> columnMappedList = new HashMap<String, Object>();
            JSONArray moduleCustomfieldArray;
            myjobj = new JSONObject();
            myjobj.put("success", false);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            boolean convertedflag = true;
            boolean companyNotifyFlag = sessionHandlerImpl.getCompanyNotifyOnFlag(request);
            
            // extract ip address
            String ipAddress = SystemUtil.getIpAddress(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            if (StringUtil.bNull(request.getParameter("jsondata"))) {
                String userid = sessionHandlerImpl.getUserid(request);
                String conversiontype = " ";
                String jsondata = request.getParameter("jsondata");
                JSONArray jarr = new JSONArray("[" + jsondata + "]");
                JSONObject jobj = jarr.getJSONObject(0);
                String leadid = jobj.getString("leadid");
                CrmLead CrmLeadObj = crmLeadDAOObj.getLead(leadid);
                if(CrmLeadObj==null || CrmLeadObj.getIsconverted().equals("1")) {
                    convertedflag=false;
                    refNotFoundStr = " Selected lead already converted successfully.";
                }
                if(convertedflag){

                    String accflag = jobj.getString("accflag");
                    String conflag = jobj.optString("conflag");
                    String oppflag = jobj.getString("oppflag");
                    String taskflag = jobj.getString("taskflag");

                    Long closingdate = jobj.getLong("closingdate");
                    String lastname = jobj.getString("lastname");
                    String accountname = jobj.getString("accountname");
                    String oppParentaccountid = jobj.getString("oppparentaccountid");
                    String oppname = jobj.getString("oppname");
                    String oppstageid = jobj.getString("oppstageid");
                    String validflag = jobj.getString("validflag");
                    String leadType = jobj.getString("type");
                    boolean transfer = false;
                    String details = "";
                    String accid = java.util.UUID.randomUUID().toString();

                     // check all mandatory column mapped
                    resJson.put("success", true);
                    if (accflag.equals("1")) {
                        accModulejObj=getModuleRecord(request,Constants.MODULE_ACCOUNT,companyid,jobj,moduleFields,columnMappedList);
                        checkManadatoryColumnMapped(resJson, Constants.MODULE_ACCOUNT,companyid, moduleFields,columnMappedList);
                    }
                    if (oppflag.equals("1")) {
                        oppModulejObj=getModuleRecord(request,Constants.MODULE_OPPORTUNITY,companyid,jobj,moduleFields,columnMappedList);
                        // check all mandatory column mapped
                        checkManadatoryColumnMapped(resJson, Constants.MODULE_OPPORTUNITY,companyid, moduleFields,columnMappedList);
                    }
                    if (conflag.equals("1")){
                        contactModulejObj=getModuleRecord(request,Constants.MODULE_CONTACT,companyid,jobj,moduleFields,columnMappedList);
                        // check all mandatory column mapped
                        checkManadatoryColumnMapped(resJson, Constants.MODULE_CONTACT, companyid, moduleFields,columnMappedList);
                    }
                    if(!resJson.getBoolean("success")) {
                        refNotFoundFlag = true;
                        refNotFoundStr = resJson.getString("msg");
                    }
                    if(!refNotFoundFlag) {
                        // Convert into Account
                        if (accflag.equals("1")) {
                            conversiontype = "1";
                            refNotFoundStr = "";
                            if(accModulejObj.getBoolean("success")) {
                                accModulejObj.put("accountname", accountname);
                                accModulejObj.put("leadid", leadid);
                                accModulejObj.put("companyid", companyid);
                                accModulejObj.put("userid", userid);
                                accModulejObj.put("updatedon", new Date());
                                accModulejObj.put("accountid", accid);
                                accModulejObj.put("validflag", "0");
                                accModulejObj.put("tzdiff",timeZoneDiff);
                                kmsg = crmAccountDAOObj.addAccounts(accModulejObj);

                                moduleCustomfieldArray=accModulejObj.getJSONArray("customfield");
                                if(moduleCustomfieldArray.length() > 0) {
                                    KwlReturnObject customDataresult =fieldDataManagercntrl.setcustomdata(moduleCustomfieldArray, Constants.Crm_account_moduleid,accid,companyid,true);
                                    if (customDataresult!=null && customDataresult.getEntityList().size() > 0) {
                                       JSONObject accJObj = new JSONObject();
                                       accJObj.put("accountid", accid);
                                       accJObj.put("CrmAccountCustomDataobj", accid);
                                       accJObj.put("tzdiff",timeZoneDiff);
                                       kmsg = crmAccountDAOObj.editAccounts(accJObj);
                                    }
        //                            fieldManager.storeCustomFields(moduleCustomfieldArray,"account",true,accid,"1");
                                }
                                crmCommentDAOObj.CreateDuplicateComments(leadid,accid);

                                documentConversion(leadid,accid,"4");

                                auditTrailDAOObj.insertAuditLog(AuditAction.ACCOUNT_CREATE,
                                                accountname + " - Account created from Lead - "+lastname,
                                                request, accid);
                                acc = (CrmAccount) kmsg.getEntityList().get(0);
                                //Save Account Owner
                                saveAccOwnersFromLead(leadid,accid);
                                details += " Account - [ "+accountname+" ], ";
                                if (oppflag.equals("1")) {
                                    transfer = false;
                                } else {
                                    transfer = true;
                                }

                                if (leadType.equals("1")) { // checked for Lead Type - if Company then associated contacts must be linked with newly added Account
                                    linkLeadsContactsToAccount(leadid, accid, userid, companyid, timeZoneDiff, ipAddress);
                                }
                                
                             } else {
                                myjobj.put("success", false);
                                refNotFoundStr += "Data not found in Account module for following fields - <BR /> " + accModulejObj.getString("msg") +"<BR /><BR />";
                                refNotFoundFlag = true;
                            }

                            // Add Activity
                            if (taskflag.equals("1")) {
                                if (StringUtil.bNull(request.getParameter("activitydata"))) {
                                    String activityjson = request.getParameter("activitydata");
                                    JSONObject activityJSON = new JSONObject(activityjson);
                                    activityJSON.put("relatedtoid", Constants.MODULE_ACCOUNT);
                                    activityJSON.put("relatedtonameid", accid);
                                    String loginURL = URLUtil.getRequestPageURL(request, Links.loginpageFull);
                                    String partnerName = sessionHandlerImpl.getPartnerName(request);
                                    activityManagementService.saveActivity(companyid, userid, timeFormatId, timeZoneDiff, ipAddress, activityJSON,companyNotifyFlag,loginURL,partnerName);
                                }
                            }
                        }

                        String oppid = java.util.UUID.randomUUID().toString();

                        // Convert into Opportunity
                        if (oppflag.equals("1")) {
                            conversiontype = "2";
                            if(oppModulejObj.getBoolean("success")) {
                                oppModulejObj.put("oppid", oppid);
                                oppModulejObj.put("companyid", companyid);
                                oppModulejObj.put("userid", userid);
                                oppModulejObj.put("updatedon", new Date().getTime());
                                oppModulejObj.put("tzdiff",timeZoneDiff);
            //                    oppJObj.put("createdon", new Date());
                                oppModulejObj.put("leadid", leadid);
                                if (accflag.equals("1")) {
                                    oppModulejObj.put("accountnameid", accid);
                                } else {
                                    oppModulejObj.put("accountnameid", oppParentaccountid);
                                }
        //                        String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
        //                        String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
                                if(jobj.has("closingdate") ){
                                    oppModulejObj.put("closingdate",jobj.getLong("closingdate"));
                                }
                                oppModulejObj.put("validflag", "0");
                                oppModulejObj.put("oppstageid", oppstageid);
                                oppModulejObj.put("currencyid", "0");
                                oppModulejObj.put("oppname", oppname);
                                kmsg = crmOpportunityDAOObj.addOpportunities(oppModulejObj);

                                moduleCustomfieldArray=oppModulejObj.getJSONArray("customfield");
                                if(moduleCustomfieldArray.length() > 0) {
        //                            fieldManager.storeCustomFields(moduleCustomfieldArray,"opportunity",true,oppid,"5");
                                    KwlReturnObject customDataresult =fieldDataManagercntrl.setcustomdata(moduleCustomfieldArray, Constants.Crm_opportunity_moduleid,oppid,companyid, true);
                                    if (customDataresult!=null && customDataresult.getEntityList().size() > 0) {
                                       JSONObject oppJObj = new JSONObject();
                                       oppJObj.put("oppid", oppid);
                                       oppJObj.put("CrmOpportunityCustomDataobj", oppid);
                                       oppJObj.put("tzdiff",timeZoneDiff);
                                       kmsg = crmOpportunityDAOObj.editOpportunities(oppJObj);
                                    }
                                }
                                opp = (CrmOpportunity) kmsg.getEntityList().get(0);
                                //Save Opportunity Owner
                                saveOppOwnersFromLead(leadid,oppid);
                                crmCommentDAOObj.CreateDuplicateComments(leadid,oppid);

                                documentConversion(leadid,oppid,"5");
                                
                                auditTrailDAOObj.insertAuditLog(AuditAction.OPPORTUNITY_CREATE,
                                                oppname + " - Opportunity created from Lead - "+lastname,
                                                request, oppid);
                                details +="Opportunity [ "+oppname+" ], ";

                                if (accflag.equals("1")) {
                                    //  transfer = true;
                                }
                                b = true;
                            } else {
                                myjobj.put("success", false);
                                refNotFoundStr += "Data not found in Opportunity module for following fields - <BR /> " + oppModulejObj.getString("msg") +"<BR /><BR />";
                                refNotFoundFlag = true;
                            }
                        }

                        // Convert into Contact
                        if (conflag.equals("1")) {
                            if (leadType.equals("0")) { // checked for Lead Type - if Company then don't convert to contact
                                if(contactModulejObj.getBoolean("success")) {
                                    //   conversiontype="3";
                                    contactModulejObj.put("leadid", leadid);
                                    if (accflag.equals("1")) {
                                        contactModulejObj.put("accountid", accid);
                                    } else if (oppflag.equals("1")) {
                                        contactModulejObj.put("accountid", oppParentaccountid);
                                    }
                                    contactModulejObj.put("validflag", "0");

                                    // add new contact
                                    JSONObject contactJson = contactManagementService.saveContact(companyid, userid, timeZoneDiff, ipAddress, contactModulejObj);
                                    contactid = contactJson.getString("ID");

                                    moduleCustomfieldArray=contactModulejObj.getJSONArray("customfield");
                                    if(moduleCustomfieldArray.length() > 0) {
        //                                fieldManager.storeCustomFields(moduleCustomfieldArray,"contact",true,conid,"6");
                                        KwlReturnObject customDataresult =fieldDataManagercntrl.setcustomdata(moduleCustomfieldArray, Constants.Crm_contact_moduleid,contactid,companyid, true);
                                        if (customDataresult!=null && customDataresult.getEntityList().size() > 0) {
                                           JSONObject contactJObj = new JSONObject();
                                           contactJObj.put("contactid", contactid);
                                           contactJObj.put("CrmContactCustomDataobj", contactid);
                                           contactJObj.put("tzdiff", timeZoneDiff);
                                           contactJson = contactManagementService.saveContact(companyid, userid, timeZoneDiff, ipAddress, contactJObj);
                                        }
                                    }


                                    
                                    //Save Contact Owner
                                    saveContactOwnersFromLead(leadid,contactid);
                //                    auditTrailDAOObj.insertAuditLog(AuditAction.CONTACT_CREATE,
                //                                    firstname + " " + lastname + " - Contact created from Lead - "+kmsg.getEntityList().getFirstname()+" "+leadd.getLastname(),
                //                                    request, conid);
                                    String firstname="";
                                    String contactName="";
                                    String lName="";
                                    if(contactModulejObj.has("firstname")){
                                        firstname= contactModulejObj.get("firstname").toString().trim();
                                    }if(contactModulejObj.has("lastname")){
                                        lName= contactModulejObj.get("lastname").toString().trim();
                                    }
                                    contactName=(firstname+ " " +lName).trim();
                                    
                                    crmCommentDAOObj.CreateDuplicateComments(leadid,contactid);
                                    documentConversion(leadid,contactid,"2");

                                    auditTrailDAOObj.insertAuditLog(AuditAction.CONTACT_CREATE,
                                                    contactName + " - Contact created from Lead - "+lastname,
                                                    request, contactid);
                                    if (oppflag.equals("1")) {
                                        transfer = false;
                                    } else {
                                        transfer = true;
                                    }
                                    b = true;

                                } else {
                                    myjobj.put("success", false);
                                    refNotFoundStr += "Data not found in Contact module for following fields - <BR /> " + contactModulejObj.getString("msg") +"<BR /><BR />";
                                    refNotFoundFlag = true;
                                }
                            }else{
                                if (accflag.equals("1")) {
                                    accid=acc.getAccountid();
                                } else if (oppflag.equals("1")) {
                                    if(opp.getCrmAccount()!=null)
                                        accid=opp.getCrmAccount().getAccountid();
                                    else
                                        accid="";
                                }else{
                                    accid="";
                                }
                                updateContacts(leadid,accid, timeZoneDiff, ipAddress);
                           }
                    }

                }
                if(!refNotFoundFlag) {
                    kmsg = crmManagerDAOObj.getMasterIDCompany(companyid, Constants.LEADSTATUSID_QUALIFIED);
                    DefaultMasterItem obj = (DefaultMasterItem) kmsg.getEntityList().get(0);
                    JSONObject leadJObj = new JSONObject();
                    if (transfer) {
                        leadJObj.put("istransfered", "1");
                    }

                    leadJObj.put("leadid", leadid);
                    leadJObj.put("leadstatusid", obj.getID());
                    leadJObj.put("conversiontype", conversiontype);
                    leadJObj.put("leadconversiondate", conversiontype);
                    leadJObj.put("isconverted", "1");
                    leadJObj.put("updatedon", new Date());
                    leadJObj.put("tzdiff",timeZoneDiff);
                    kmsg = crmLeadDAOObj.editLeads(leadJObj);
                    lead = (CrmLead) kmsg.getEntityList().get(0);
                    auditTrailDAOObj.insertAuditLog(AuditAction.LEAD_CONVERT,
                                        details + " created by converting Lead - "+lead.getLastname(),
                            request, leadid);
                    b = true;
                    myjobj.put("success", b);
                }
              } // end of if
            }

            if(!refNotFoundFlag && convertedflag) {
                txnManager.commit(status);
                    if(acc!=null)
                        crmCommonDAOObj.validaterecorsingledHB("Account", acc.getAccountid(), companyid);
                    if(opp!=null)
                        crmCommonDAOObj.validaterecorsingledHB("Opportunity", opp.getOppid(), companyid);
                    if(contactid!=null)
                        crmCommonDAOObj.validaterecorsingledHB("Contact", contactid, companyid);
            } else {
                myjobj.put("msg", refNotFoundStr);

                txnManager.rollback(status);
            }
        } catch (JSONException e) {
            txnManager.rollback(status);
            logger.warn(e.getMessage(),e);
        } catch (Exception e) {
            txnManager.rollback(status);
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(successView, "model", myjobj.toString());
    }

    private void linkLeadsContactsToAccount(String leadid, String accountid, String userid, String companyid, String timeZoneDiff, String ipAddress) throws ServiceException, JSONException {
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            filter_names.add("c.Lead.leadid");
            filter_params.add(leadid);
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("export", "true");
            KwlReturnObject kmsg = crmContactDAOObj.getContacts(requestParams, usersList, filter_names, filter_params);
            List<CrmContact> contactList = kmsg.getEntityList();
            if(contactList !=null && !contactList.isEmpty()) {
                for(CrmContact contactObj : contactList) {
                    JSONObject contactJObj = new JSONObject();
                    contactJObj.put("contactid", contactObj.getContactid());
                    contactJObj.put("accountid", accountid);
                    contactJObj.put("tzdiff", timeZoneDiff);
                    contactManagementService.saveContact(companyid, userid, timeZoneDiff, ipAddress, contactJObj);
                }
            }
    }
    
    private void checkManadatoryColumnMapped(JSONObject resJson, String moduleName, String companyid, Map<String, Object> moduleFields, Map<String, Object> columnMappedList) throws ServiceException {
        try {
            List<Object[]> ll = getColumnHeader(moduleName, companyid);
            StringBuilder refNotFoundStr = new StringBuilder(),refWithNotMandatoryStr = new StringBuilder();
            if(ll!=null) {
                for (Object[] rec : ll) { // rec[1] = recordname rec[0] = header
                    if (!moduleFields.containsKey(rec[1]) && !Constants.defaultLeadConvertColumns.containsKey(rec[1])) {// if not mapped
                        if (refNotFoundStr.length() == 0) {
                            refNotFoundStr.append( "<br/>Following mandatory fields of ").append(moduleName).append(" module are not mapped -");
                        }
                        refNotFoundStr.append(" <br/> ").append(rec[0]); // dhrec[0] = header
                        resJson.put("success", false);
                    } else if (moduleFields.containsKey(rec[1]) && !Constants.defaultLeadConvertColumns.containsKey(rec[1])
                            && StringUtil.isNullOrEmpty(moduleFields.get(rec[1]).toString())) {// if mapped with non mandatory column
                        if (refWithNotMandatoryStr.length() == 0) {
                            refWithNotMandatoryStr.append( "<br/>Following Lead module's mapped field(s) should be mandatory -");
                        }
                        HashMap<String, Object> requestParams = new HashMap<String, Object>();
                        DefaultHeader leadDH = (DefaultHeader) columnMappedList.get(rec[1]);
                        String leadColumn = leadDH.getDefaultHeader();

                        // check for modified column name in columnHeader table
                        ArrayList filter_params = new ArrayList();
                        ArrayList filter_names = new ArrayList();
                        filter_names.add("c.defaultheader.id");
                        filter_params.add(leadDH.getId());
                        filter_names.add("c.company.companyID");
                        filter_params.add(companyid);
                        requestParams.put("filter_names", filter_names);
                        requestParams.put("filter_params", filter_params);
                        KwlReturnObject kmsg = crmCommonDAOObj.getColumnHeader(requestParams);
                        if(kmsg.getEntityList().size() > 0) {
                            ColumnHeader ch = (ColumnHeader)kmsg.getEntityList().get(0);
                            if(!StringUtil.isNullOrEmpty(ch.getNewHeader()))
                                leadColumn = ch.getNewHeader();
                        }
                        refWithNotMandatoryStr.append( " <br/> <b>" ).append(leadColumn).append("</b>: required for <b>").append(rec[0]).append("</b>(").append(moduleName).append(")");
                        resJson.put("success", false);
                    }
                }
            }
            if(!resJson.getBoolean("success")) {
                StringBuilder errorString = new StringBuilder();
                if(resJson.has("msg")) {
                    errorString.append(resJson.getString("msg"));
                }
                errorString.append(refNotFoundStr).append(refWithNotMandatoryStr);
                resJson.put("msg", errorString);
            }
        } catch (JSONException ex ) {
            logger.warn("Can't get column header for "+moduleName+" in checkManadatoryColumnMapped() : ", ex);
        } finally {
        }
    }

    private List getColumnHeader(String modName, String companyid) {
        List ll = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", modName);
            ll = crmCommonDAOObj.getAllMandatoryColumnHeader(requestParams,"defaultHeader,recordname","newHeader,ch.defaultheader.defaultHeader,ch.defaultheader.recordname");
            List<Object[]> dh = (List<Object[]>) ll.get(0);
            List<Object[]> ch = (List<Object[]>) ll.get(1);
            if(!ch.isEmpty()) {
                for(Object[] chrec : ch) {
                    Object[] newchrec = {(StringUtil.isNullOrEmpty((String)chrec[0])) ? chrec[1] : chrec[0],chrec[2]};
                    dh.add(newchrec);
                }
            }
            ll = dh;
        } catch(ServiceException ex) {
            logger.warn("getColumnHeader() : ", ex);
        }
        return ll;
    }
    public void saveOppOwnersFromLead(String leadid,String oppid) throws ServiceException, Exception{
        String[] ownerInfo=crmLeadHandler.getAllLeadOwners(crmLeadDAOObj, leadid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("oppid", oppid);
        requestParams.put("owners", ownerInfo[3]);
        requestParams.put("mainOwner", ownerInfo[2]);
        KwlReturnObject kmsg = crmOpportunityDAOObj.saveOppOwners(requestParams);
//        crmOpportunityDAOObj.saveOppOwners(oppid,ownerInfo[2],ownerInfo[3]);
    }
    public void saveAccOwnersFromLead(String leadid,String accid) throws ServiceException, Exception{
        String[] ownerInfo=crmLeadHandler.getAllLeadOwners(crmLeadDAOObj, leadid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("accid", accid);
        requestParams.put("owners", ownerInfo[3]);
        requestParams.put("mainOwner", ownerInfo[2]);
        KwlReturnObject kmsg = crmAccountDAOObj.saveAccOwners(requestParams);
//        crmAccountDAOObj.saveAccOwners(accid,ownerInfo[2],ownerInfo[3]);
    }
    public void saveContactOwnersFromLead(String leadid,String contactid) throws ServiceException, Exception{
        String[] ownerInfo=crmLeadHandler.getAllLeadOwners(crmLeadDAOObj, leadid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("contactid", contactid);
        requestParams.put("owners", ownerInfo[3]);
        requestParams.put("mainOwner", ownerInfo[2]);
        KwlReturnObject kmsg = crmContactDAOObj.saveContactOwners(requestParams);
//        crmContactDAOObj.saveContactOwners(contactid,ownerInfo[2],ownerInfo[3]);
    }

    private void updateContacts(String leadid,String accid, String tzDiff, String ipAddress) throws ServiceException, Exception{

        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();

        filter_names.add("c.Lead.leadid");
        filter_params.add(leadid);
        CrmLead lead = (CrmLead) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CrmLead", leadid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("export", "true");
        requestParams.put("heirarchyPerm", "true");

        KwlReturnObject kmsg = crmContactDAOObj.getContacts(requestParams, null, filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();
        while(ite.hasNext()){
            CrmContact obj=(CrmContact)ite.next();

            JSONObject conJObj = new JSONObject();
            String conid = obj.getContactid();
            //   conversiontype="3";
            conJObj.put("contactid", conid);
//            conJObj.put("updatedon", new Date());
            conJObj.put("leadid", leadid);
            conJObj.put("tzdiff",tzDiff);
            if(!StringUtil.isNullOrEmpty(accid)){
                conJObj.put("accountid", accid);
            }
            if(lead.getCrmCombodataByIndustryid() != null && obj.getCrmCombodataByIndustryid() == null  ){
                conJObj.put("industryid", lead.getCrmCombodataByIndustryid());
            }
            if(lead.getCrmCombodataByLeadsourceid() != null && obj.getCrmCombodataByLeadsourceid() == null){
                conJObj.put("leadsourceid", lead.getCrmCombodataByLeadsourceid());
            }

//            kmsg = crmContactDAOObj.editContacts(conJObj);
            contactManagementService.saveContact(null, null, tzDiff, ipAddress, conJObj);
            saveContactOwnersFromLead(leadid,obj.getContactid());
        }
    }

    public ModelAndView saveEditWTLForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String formfields = request.getParameter("formfields");
            String formname = request.getParameter("formname");
            String formdomain = request.getParameter("formdomain");
            String redirecturl = request.getParameter("redirectURL");
            String leadowner = request.getParameter("leadowner");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String formid=request.getParameter("formid");

            jobj=  webtoLeadFormHandlerObj.saveEditWTLForm(formname, formdomain, redirecturl, formfields, companyid, formid,leadowner);

            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(successView, "model", jobj.toString());
    }

    public ModelAndView deleteWTLForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String formid=request.getParameter("formid");
            jobj=  webtoLeadFormHandlerObj.deleteWTLForm(formid);

            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(successView, "model", jobj.toString());
    }

    public ModelAndView getWebtoleadFormlist(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobjresult = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        KwlReturnObject kmsg = null;
        try {
            String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            kmsg =  webtoLeadFormHandlerObj.getWebtoleadFormlist(requestParams);
            Iterator itr = kmsg.getEntityList().iterator();
            while (itr.hasNext()) {
                JSONObject jobj = new JSONObject();
                webtoleadform wtlformObj = (webtoleadform) itr.next();
                jobj.accumulate("formname", wtlformObj.getFormname());
                jobj.accumulate("formdomain", wtlformObj.getFormdomain());
                jobj.accumulate("redirecturl", wtlformObj.getRedirecturl());
                jobj.accumulate("formid", wtlformObj.getFormid());
                jobj.accumulate("lastupdatedon", kwlCommonTablesDAOObj.getUserDateFormatter(dateFormatId, timeFormatId, timeZoneDiff).format(wtlformObj.getLastupdatedon()));
                jobj.accumulate("formfields", wtlformObj.getFormfield());
                jobj.accumulate("leadowner", ((User)wtlformObj.getLeadowner()).getUserID());
                jobjresult.append("data", jobj);
            }
            if(!jobjresult.has("data")) {
                jobjresult.put("data", "");
            }
            jobjresult.accumulate("totalCount", kmsg.getRecordTotalCount());
            jobjresult.accumulate("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(successView, "model", jobjresult.toString());
    }

	public ModelAndView storeLead(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		String result = "Problem while submitting your form.";
		HashMap model = new HashMap();
		Map paramMap1 = (java.util.Map) request.getParameterMap();
		HashMap paramMap = new HashMap();
		User user = null;
		User sender = null;
		// Create transaction
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		try {
			model.put("successFlag", "1");
			model.put("redirectFlag", "0");
			String successMsg = "Thank you for submitting your information.";
			String val = "";
			int mtyfieldCntr = 0;
			Set arrayKeys = paramMap1.keySet();
			HashMap fieldMap = new HashMap();
			JSONObject fieldJSON = null;
			if(!StringUtil.isNullOrEmpty(request.getParameter("fieldJSON"))){
				try{
					fieldJSON = new JSONObject(request.getParameter("fieldJSON"));
				}catch(JSONException je){
					logger.warn("crmLeadCommonController.storeLead :- "+je.getMessage());
				}
				finally{
					fieldJSON=null;
				}
			}
			
			HashMap<String, Object> mailFieldsMap = new HashMap<String, Object>();
			HashMap<String, Object> mailValueMap = new HashMap<String, Object>();
			for (Object key : arrayKeys) {
				if (((String) key).startsWith("wl_")) {
					fieldMap.put(key, paramMap1.get((String) key) != null ? new String[] { StringUtil.serverHTMLStripper(((String[]) paramMap1.get((String) key))[0]) } : null);
					if (paramMap1.get((String) key) != null) {
						val = StringUtil.serverHTMLStripper(((String[]) paramMap1.get((String) key))[0]);
						if (val.trim().equals("")) {
							mtyfieldCntr++;
						}
					}
				}
			}
			if (mtyfieldCntr != fieldMap.size()) {
				paramMap.putAll(paramMap1);
				paramMap.putAll(fieldMap);
				String subdomain = URLUtil.getDomainName(request);
				String companyid = companyDetailsDAOObj.getCompanyid(subdomain);
				Company company = (Company) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyid);
				sender = company.getCreator();
				String acceptURL = URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull) + "crm/common/acceptweblead.do";
				int leadRoutingUser = 0;
				CompanyPreferences cmpPref = (CompanyPreferences) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CompanyPreferences", companyid);
				JSONObject companyPrefObj = crmManagerCommon.getCompanyPreferencesJSON(cmpPref, new JSONObject());
				user = (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", ((String[]) paramMap.get("leadOwner"))[0]);
				if (companyPrefObj.getInt(com.krawler.common.util.Constants.SESSION_LEADROUTING) == LeadRoutingUsers.ROUNDROBIN) {
					user = crmManagerDAOObj.getNextLeadRoutingUsers(companyid).get(0);
					leadRoutingUser = LeadRoutingUsers.ROUNDROBIN;
				}
				if (companyPrefObj.getInt(com.krawler.common.util.Constants.SESSION_LEADROUTING) == LeadRoutingUsers.FCFS) {
					leadRoutingUser = LeadRoutingUsers.FCFS;
				}


				JSONObject resultObj = webtoLeadFormHandlerObj.storeLead(paramMap, companyid, user.getUserID(), acceptURL, leadRoutingUser);


				result = resultObj.optString("msg", result);
				if (!result.equals("Success")) {
					successMsg = result;
					model.put("successFlag", "0");
					txnManager.rollback(status);
				} else {
					CrmLead l = (CrmLead) hibernateTemplate.get(CrmLead.class, resultObj.getString("leadid"));
					if(fieldJSON!=null){
						Iterator itr = fieldJSON.keys();
						String keyStr = "";
						String tmp = "";
						String str = "";
						String tmpAr="";
						int xtype = 999;
						String[] valuesArr = null;
						DefaultMasterItem defItem = null;
						FieldComboData fieldCombodata = null;
						Object invoker=null;
						Class cl=null;
						CrmLeadCustomData lcdata=null;
						Method setter=null;
						List<String> tmpProductlist = new ArrayList<String>();
						while (itr.hasNext()) {
							keyStr = (String) itr.next();
							mailFieldsMap.put(keyStr, fieldJSON.get(keyStr));
						}

						for (String tmpStr : mailFieldsMap.keySet()) {
							if (tmpStr.endsWith("_DROPDOWN#")) {
								str = tmpStr;
								str = str.replace("_DROPDOWN#", "");
								if (tmpStr.startsWith("custom_field")) {
									str = str.replace("custom_field", "");
									if (paramMap.get("wl_custom_field" + str) != null) {
										fieldCombodata = (FieldComboData) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.FieldComboData", ((String[]) paramMap.get("wl_custom_field" + str))[0]);
										if (fieldCombodata == null) { //Custom reference combo check- Since some	custom column may have reference of default combos.
											defItem = (DefaultMasterItem) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.DefaultMasterItem", ((String[]) paramMap.get("wl_custom_field" + str))[0]);
											if (defItem != null) {
												mailValueMap.put(tmpStr, defItem.getValue());
											}
										} else {
											FieldParams fieldParamObj = fieldCombodata.getField();
											xtype = fieldParamObj.getFieldtype();
											if (xtype == 7) { // for Multi select dropdown
												if (request.getParameterValues("wl_custom_field" + str) != null) {
													valuesArr = request.getParameterValues("wl_custom_field" + str);
													tmp = "";
													tmpAr="";
													for (int i = 0; i < valuesArr.length; i++) {
														fieldCombodata = (FieldComboData) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.FieldComboData", valuesArr[i]);
														if (i == valuesArr.length - 1){
															tmp += fieldCombodata.getValue();
															tmpAr+=valuesArr[i];
														}else{
															tmp += fieldCombodata.getValue() + ",";
															tmpAr+=valuesArr[i]+ ",";
														}
													}
													mailValueMap.put(tmpStr, tmp);
												//Since multiselect combo list has not been saved becoz parammap contains only single value as there are multiple values selected
													if(((String[])paramMap.get("wl_custom_field" + str)).length<valuesArr.length){
														try{
															lcdata=l.getCrmLeadCustomDataobj();
															cl = Class.forName("com.krawler.crm.database.tables.CrmCustomData");
															invoker = (Object)lcdata;
															setter = cl.getMethod("setCol"+fieldParamObj.getColnum(),String.class);
															setter.invoke(invoker, tmpAr);
															l.setCrmLeadCustomDataobj((CrmLeadCustomData)invoker);
														}catch(Exception e){
															logger.warn(e.getMessage());
														}
													}	
												} else
													mailValueMap.put(tmpStr, "");
											} else {
												fieldCombodata = (FieldComboData) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.FieldComboData", ((String[]) paramMap.get("wl_custom_field" + str))[0]);
												mailValueMap.put(tmpStr, fieldCombodata.getValue());
											}
										}
									} else {
										mailValueMap.put(tmpStr, "");
									}
								} else if (tmpStr.startsWith("productid")) {
									if (request.getParameterValues("wl_" + str) != null) {
										valuesArr = request.getParameterValues("wl_" + str);
										tmp = "";
										for (int n = 0; n < valuesArr.length; n++) {
											if (!tmpProductlist.isEmpty()) {
												tmpProductlist.remove(0);
											}
											tmpProductlist.add(valuesArr[n]);
											tmp += ((crmProductDAOObj.getProducts(tmpProductlist)).get(0)).getProductname() + ",";
										}
										tmp = tmp.substring(0, tmp.lastIndexOf(','));
										mailValueMap.put(tmpStr, tmp);
										if(((String[])paramMap.get("wl_productid")).length<valuesArr.length){
									//Since products list has not been saved becoz parammap contains only single value as there are multiple values selected
											try{
												crmLeadDAOObj.saveLeadProducts(new String[]{l.getLeadid()}, valuesArr);
											}catch(Exception ex){
												logger.warn(ex.getMessage());
											}
										}
									} else
										mailValueMap.put(tmpStr, "");
								} else if (tmpStr.startsWith("type")) {
									tmp = ((String[]) paramMap.get("wl_" + str))[0];
									if (tmp == "1")
										mailValueMap.put(tmpStr, "Company");
									else
										mailValueMap.put(tmpStr, "Individual");

								} else {
									defItem = (DefaultMasterItem) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.DefaultMasterItem", ((String[]) paramMap.get("wl_" + str))[0]);
									if (defItem != null)
										mailValueMap.put(tmpStr, defItem.getValue());
									else
										mailValueMap.put(tmpStr, "");
								}
							} else
								mailValueMap.put(tmpStr, ((String[]) paramMap.get("wl_" + tmpStr))[0] != null ? ((String[]) paramMap.get("wl_" + tmpStr))[0] : "");
						}
					}
					String id = java.util.UUID.randomUUID().toString();
					String ipaddr = null;
					if (StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))) {
						ipaddr = request.getRemoteAddr();
					} else {
						ipaddr = request.getHeader("x-real-ip");
					}
					auditTrailDAOObj.insertAuditLog(AuditAction.LEAD_CREATE, ((l.getLastname() == null) ? "" : l.getLastname()) + " - Lead created through Web to Lead Form ", ipaddr, user.getUserID(), id);

                    crmCommonDAOObj.validaterecorsingledHB(Constants.MODULE_LEAD, l.getLeadid(), companyid);

					txnManager.commit(status);
					if (resultObj.has("leadid")) {
						String leadid = resultObj.getString("leadid");
						if (leadRoutingUser == LeadRoutingUsers.ROUNDROBIN) {
							crmManagerDAOObj.setLastUsedFlagForLeadRouting(user.getUserID(), companyid);
							if(fieldJSON!=null)
								sendMailToLeadOwner(user, leadid, mailFieldsMap, mailValueMap);
							else
								sendMailToLeadOwner(user, leadid, null, null);
						} else if (leadRoutingUser == LeadRoutingUsers.FCFS) {
							List<String> recepients = new ArrayList();
							KwlReturnObject kmsg = crmManagerDAOObj.getAssignedLeadRoutingUsers(companyid, new HashMap<String, Object>());
							List<User> userlist = kmsg.getEntityList();
							for (User userObj : userlist) {
								recepients.add(userObj.getUserID());
							}
							Map refTypeMap = new HashMap();
							Map refIdMap = new HashMap();
							refIdMap.put("refid1", leadid);
							refTypeMap.put("reftype1", Constants.Crm_lead_classpath);
							HashMap<String, Object> extraParams = new HashMap<String, Object>();
							extraParams.put(NotificationConstants.LOGINURL, acceptURL);
							if (recepients.size() > 0) {
								String platformUrl = ConfigReader.getinstance().get("platformURL");
								if (platformUrl != null) {
									getPartnerURL(platformUrl, company, CHANNEL.EMAIL, NOTIFICATIONSTATUS.REQUEST, company.getCreator().getUserID(), recepients, refIdMap, refTypeMap, extraParams);
								} else {
									extraParams.put(NotificationConstants.PARTNERNAME, company.getCompanyName());
									sendMailToAssignedLeadRoutingUsers(CHANNEL.EMAIL, NOTIFICATIONSTATUS.REQUEST, company.getCreator().getUserID(), recepients, refIdMap, refTypeMap, extraParams);
								}
							}
						} else {
							if(fieldJSON!=null)
								sendMailToLeadOwner(user, leadid, mailFieldsMap, mailValueMap);
							else
								sendMailToLeadOwner(user, leadid, null, null);						}
					}
				}
				model.put("successMsg", successMsg);
				String returnurl = request.getParameter("returnurl");
				if (!StringUtil.isNullOrEmpty(returnurl)) {
					model.put("redirectFlag", "1");
					model.put("returnurl", returnurl);
				}
			} else {
				model.put("successMsg", "Form should not be empty");
				model.put("successFlag", "0");
				txnManager.rollback(status);
			}

		} catch (Exception e) {
			txnManager.rollback(status);
			logger.warn(e.getMessage(), e);
			if (user != null) {
				sender = user;
			}
			if (sender != null) {
				sendMailToLeadOwner(sender, paramMap1);
			}
		}
		return new ModelAndView("captureLead", "model", model);
	}

    private void sendMailToLeadOwner(User user, Map map) {
        try {
            String subject = user.getCompany().getCompanyName() + " CRM - Failed to genrate Web lead";
            String mailbodyContent = "Hi %s,<p>Following web lead has not been generated in the system</p>" +
            "<table width='80%'><th><td>Property</td><td>Value</td></th>";
            for(Map.Entry e : (Set<Map.Entry>)map.entrySet()){
            	String key = (String)e.getKey();
            	if(key!=null&&key.startsWith("wl_")){
            			key = key.substring(3);
            	}
            	mailbodyContent+="<tr>"+
            		"<td>" +StringUtils.capitalize(key)+ "</td>"+
            		"<td>" +e.getValue()+ "</td>"+
            		"</tr>";
            }
            mailbodyContent+="</table>"+
                    "<p>Please Note that lead cannot be displayed in the list with out regenerating.</p>" +
                    "<p></p><p>- Thanks,</p><p>Administrator</p>";
            String mailbody = String.format(mailbodyContent, StringUtil.getFullName(user));
            String htmlmsg = "<html><head><link rel='shortcut icon' href='../../images/deskera/deskera.png'/><title>New Web Lead</title></head><style type='text/css'>" + "a:link, a:visited, a:active {\n" + " 	color: #03C;" + "}\n" + "body {\n" + "	font-family: Arial, Helvetica, sans-serif;" + "	color: #000;" + "	font-size: 13px;" + "}\n" + "</style><body>" + "	<div>" + mailbody + "	</div></body></html>";
            String pmsg = htmlmsg;
            String fromAddress = user.getCompany().getCreator().getEmailID();
            SendMailHandler.postMail(new String[]{supportEmailAddress}, subject, htmlmsg, pmsg, fromAddress, "");
        } catch (Exception ex) {
            logger.warn("Mail for web lead generation failure can not send :"+map,ex);
        }
    }
    
    private void sendMailToLeadOwner(User user, String leadId,HashMap<String, Object> fields,HashMap<String, Object> values) {
        try {
            CrmLead lead = (CrmLead) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CrmLead", leadId);
            String recipients = user.getEmailID();
            String subject = user.getCompany().getCompanyName() + " CRM - Web lead has been allocated to " + StringUtil.getFullName(user);
            String mailbodyContent="";
            String htmlmsg="";
                        
            if(values==null){
            	mailbodyContent = "Hi %s,<p>Following web lead has been allocated to you</p><p>Lead Name: %s</p><p>Phone Number: %s</p>" +
            		"<p>Email Address: %s</p><p>Please Note that lead will be displayed in your list.</p>" +
            		"<p></p><p>- Thanks,</p><p>Administrator</p>";
            		String mailbody = String.format(mailbodyContent, StringUtil.getFullName(user),(lead.getFirstname() + " " + lead.getLastname()).trim(), lead.getPhone(), lead.getEmail());
            		htmlmsg = "<html><head><link rel='shortcut icon' href='../../images/deskera/deskera.png'/><title>New Web Lead</title></head><style type='text/css'>" + "a:link, a:visited, a:active {\n" + " 	color: #03C;" + "}\n" + "body {\n" + "	font-family: Arial, Helvetica, sans-serif;" + "	color: #000;" + "	font-size: 13px;" + "}\n" + "</style><body>" + "	<div>" + mailbody + "	</div></body></html>";
            }else{
            	mailbodyContent = "Hi "+ StringUtil.getFullName(user)+",<p>Following web lead has been allocated to you</p>";
            	for(String k:fields.keySet()){
            		if(values.containsKey(k)){
            			mailbodyContent+="<p><b>"+fields.get(k).toString()+"</b>  : "+values.get(k).toString()+"</p>";
            		}
            	}
            	mailbodyContent+="<p>Please Note that lead will be displayed in your list.</p><p></p><p>- Thanks,</p><p>Administrator</p>";
            	htmlmsg = "<html><head><link rel='shortcut icon' href='../../images/deskera/deskera.png'/><title>New Web Lead</title></head><style type='text/css'>" + "a:link, a:visited, a:active {\n" + " 	color: #03C;" + "}\n" + "body {\n" + "	font-family: Arial, Helvetica, sans-serif;" + "	color: #000;" + "	font-size: 13px;" + "}\n" + "</style><body>" + "	<div>" + mailbodyContent + "	</div></body></html>";
            }
            String pmsg = htmlmsg;
            String fromAddress = user.getCompany().getCreator().getEmailID();
            SendMailHandler.postMail(new String[]{recipients}, subject, htmlmsg, pmsg, fromAddress, "");
        } catch (Exception ex) {
            logger.warn(ex.getMessage(),ex);
        }
    }
    private void sendMailToAssignedLeadRoutingUsers(CHANNEL channel, NOTIFICATIONSTATUS notifyStatus, String userid,
            List<String> recepients, Map refIdMap, Map refTypeMap, Map<String, Object> extraParams) throws ServiceException{
        NotificationManagementServiceDAO.sendNotificationRequest(channel, FCFS_LEADROUTING, notifyStatus, userid, recepients, refIdMap, refTypeMap, extraParams);
    }

    private void getPartnerURL(String platformUrl, Company company,CHANNEL channel, NOTIFICATIONSTATUS notifyStatus, String userid,
            List<String> recepients, Map refIdMap, Map refTypeMap, Map<String, Object> extraParams) {
        try{
            String companyName = company.getCompanyName();
            // set platform url
            JSONObject temp = new JSONObject();
            String companyId = company.getCompanyID();
            temp.put("companyid", company.getCompanyID());
            apiCallHandlerService.callApp(platformUrl, temp, companyId, "13", false, new Receiver() {
                String companyName = com.krawler.common.util.Constants.DESKERA;
                CHANNEL channel;
                NOTIFICATIONSTATUS notifyStatus;
                String userid;
                List<String> recepients;
                Map refIdMap;
                Map refTypeMap;
                Map<String, Object> extraParams;
                public Receiver setCompany(String companyName,CHANNEL channel, NOTIFICATIONSTATUS notifyStatus, String userid,
                    List<String> recepients, Map refIdMap, Map refTypeMap, Map<String, Object> extraParams) {
                    this.companyName = companyName;
                    this.channel = channel;
                    this.notifyStatus = notifyStatus;
                    this.userid = userid;
                    this.recepients = recepients;
                    this.refIdMap = refIdMap;
                    this.refTypeMap = refTypeMap;
                    this.extraParams = extraParams;
                    return this;
                }
                @Override
                public void receive(Object obj) {
                    JSONObject jobj = null;
                    if (obj instanceof JSONObject) {
                        jobj = (JSONObject) obj;
                    }
                    try {
                        String partnerURL = jobj.optString(com.krawler.common.util.Constants.SESSION_PARTNERNAME, this.companyName);
                        if(jobj.optString(com.krawler.common.util.Constants.SESSION_PARTNERNAME, this.companyName).equals(com.krawler.common.util.Constants.DESKERA)) {
                            partnerURL = this.companyName;
                        }
                        extraParams.put(NotificationConstants.PARTNERNAME, partnerURL);
                        sendMailToAssignedLeadRoutingUsers(CHANNEL.EMAIL, NOTIFICATIONSTATUS.REQUEST, userid, recepients, refIdMap, refTypeMap, extraParams);
                    } catch (Exception ex) {
                        logger.warn("Cannot store partnername: " + ex.toString());
                    }
                }
            }.setCompany(companyName, channel, notifyStatus, userid, recepients, refIdMap, refTypeMap, extraParams));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public ModelAndView acceptweblead(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "Problem while handling your request.";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String userid = request.getParameter("userid");
            String leadid = request.getParameter("leadid");
            boolean isAlreadyAssigned = crmLeadDAOObj.checkWebLeadAssignedOwner(leadid);
            String replaceStr = "<p id='unsubContent'><p>Lead has been successfully allocated to you and can be viewed in your lead list.</p>";
            String headerContent = "<head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'>" +
                    "<title>New Web Lead</title>" +
                    "<style type='text/css'>" +
                    "body {font-family: 'trebuchet MS', tahoma, verdana, arial, helvetica, sans-serif;" +
                    "background-color: #eee;margin: 0;} " +
                    "#content {width: 100%;position:absolute;padding:20px;top:5%;}" +
                    ".content {width: 600px;margin:auto;padding:20px;border:10px solid #ccc;background-color: white;}" +
                    ".alert {font-size:20px;line-height:200%;font-family:Arial;font-weight:bold;}" +
                    "p, label, .formText {line-height:150%;font-family:Arial;font-size: 14px;color: #333333;}" +
                    "</style>" +
                    "</head>";

            if(isAlreadyAssigned) {
                replaceStr = "<p id='unsubContent'><p>Sorry, lead cannot be allocated as it has been already allocated to a different team member.</p>";
            } else {
                String oldOwnerId = crmLeadDAOObj.confirmWebLeadOwner(leadid, userid);
                if(!StringUtil.isNullOrEmpty(oldOwnerId) && !oldOwnerId.equals(userid)) {
                    User oldOwner = (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", oldOwnerId);
                    User newOwner = (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
                    CrmLead lead = (CrmLead) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CrmLead", leadid);
                    String recipients = oldOwner.getEmailID();
                    String subject = oldOwner.getCompany().getCompanyName() + " CRM - Web lead has been allocated to "+ StringUtil.getFullName(newOwner);
                    String mailbodyContent = "Hi %s,<p>Following web lead has been allocated to %s</p><p>Lead Name: %s</p><p>Phone Number: %s</p>" +
                            "<p>Email Address: %s</p><p>Please Note that lead will not be displayed in your list.</p>" +
                            "<p></p><p>- Thanks,</p><p>Administrator</p>";
                    String mailbody = String.format(mailbodyContent, StringUtil.getFullName(oldOwner), StringUtil.getFullName(newOwner),
                            (lead.getFirstname()+" "+lead.getLastname()).trim(),lead.getPhone(),lead.getEmail());
                    String htmlmsg = "<html><head><link rel='shortcut icon' href='../../images/deskera/deskera.png'/><title>New Web Lead</title></head><style type='text/css'>" + "a:link, a:visited, a:active {\n" + " 	color: #03C;" + "}\n" + "body {\n" + "	font-family: Arial, Helvetica, sans-serif;" + "	color: #000;" + "	font-size: 13px;" + "}\n" + "</style><body>" + "	<div>" +mailbody+ "	</div></body></html>";
                    String pmsg = htmlmsg;
                    String fromAddress = newOwner.getCompany().getCreator().getEmailID();
                    SendMailHandler.postMail(new String[]{recipients}, subject, htmlmsg, pmsg, fromAddress, "");
                }
            }

            String bodyContent = "<body>" +
                    "<div id='content'><div id=unsubThankYouPage class='content'>" +
                    "<div class='alert' id='unsubTitle'>New Web Lead</div>" +
                    replaceStr +
                    "</p></div></div>" +
                    "</div>" +
                    "</body>";
            result = "<html><link rel='shortcut icon' href='../../images/deskera/deskera.png'/>" + headerContent + bodyContent + "</html>";
            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
            logger.warn(e.getMessage(),e);
            e.printStackTrace();
        }
        return new ModelAndView("chartView", "model", result);
    }
    
    // Kuldeep Singh :  Documents carry forward while Lead Conversion
    // sourceid is leadid and targetid is accountid, contactid, opportunityid etc
    private void documentConversion(String sourceid,String targetid, String map) throws ServiceException, Exception{

        HashMap<String, Object> doclistparams = new HashMap<String, Object>();
        doclistparams.put("recid", sourceid);
        KwlReturnObject doclist = crmDocumentDAOObj.getDocuments(doclistparams);
        int docCount = doclist.getEntityList().size();
        if(docCount>0){

            for( int i=0 ; i < docCount ; i++){
                Docs d = (Docs) doclist.getEntityList().get(i);
                JSONObject docJson = new JSONObject();
                docJson.put("docid", d.getDocid());
                docJson.put("refid", targetid);
                docJson.put("map", map);

                crmDocumentDAOObj.saveDocumentMapping(docJson);
            }

        }
    }
}
