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
package com.krawler.spring.crm.common;

import com.krawler.common.admin.Assignmanager;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.utils.DateUtil;
import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.crm.database.tables.CrmCombomaster;
import com.krawler.crm.database.tables.LeadConversionMappings;
import com.krawler.crm.utils.AuditAction;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.model.AuditReportHitsPerModuleDTO;
import com.krawler.model.AuditReportModuleUsageDTO;
import com.krawler.service.IAuditLoggerService;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.userModule.crmUserDAO;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.ObjectNotFoundException;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSource;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author Karthik
 */
public class crmCommonController extends MultiActionController implements MessageSourceAware {

    private crmCommonDAO crmCommonDAOObj;
    private crmUserDAO crmUserDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private permissionHandlerDAO permissionHandlerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private HibernateTransactionManager txnManager;
    private profileHandlerDAO profileHandlerDAOObj;
    private String successView;
    private CrmCommonService crmCommonService;
    private IAuditLoggerService auditLoggerService;
    private fieldManagerDAO fieldManagerDAOobj;
    private MessageSource messageSource;
    
    public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    
    private static final String GRAPH_LEGEND_OTHER = "Other";
    /**
     * @return the crmCommonService
     */
    public CrmCommonService getCrmCommonService()
    {
        return crmCommonService;
    }

    /**
     * @param crmCommonService the crmCommonService to set
     */
    public void setCrmCommonService(CrmCommonService crmCommonService)
    {
        this.crmCommonService = crmCommonService;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }
    
    public void setpermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }
    
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setcrmUserDAO(crmUserDAO crmUserDAOObj1) {
        this.crmUserDAOObj = crmUserDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }
    
    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }
    
    public void setAuditLoggerService(IAuditLoggerService auditLoggerService) {
        this.auditLoggerService = auditLoggerService;
    }

    //Permission Handler - delete role
    public ModelAndView deleteRole(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String roleid = request.getParameter("roleid");
            String msg = "";
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("roleid", roleid);

            if (!roleid.equals(Constants.COMPANY_ADMIN)) {
                kmsg = permissionHandlerDAOObj.deleteRole(requestParams);
                msg = kmsg.getEntityList().get(0).toString();
            } else {
                msg = "Company Admin role cannot be deleted";
            }
            jobj.put("msg", msg);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    // Organisation Chart - insert node
    public ModelAndView insertNode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        String retMsg = "";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String fromId = StringUtil.checkForNull(request.getParameter("fromId"));
            String toId = StringUtil.checkForNull(request.getParameter("userid"));
            requestParams.put("userid", toId);
            requestParams.put("fromId", fromId);
            requestParams.put("addFlag", true);
            String id = java.util.UUID.randomUUID().toString();
            requestParams.put("assignid", id);

            // get Parent and Child Use's role
            String parentRole = "", childRole = "";
            kmsg = permissionHandlerDAOObj.getRoleofUser(fromId);
            Iterator ite2 = kmsg.getEntityList().iterator();
            if (ite2.hasNext()) {
                Object[] row = (Object[]) ite2.next();
                parentRole = row[0].toString();
            }
            kmsg = permissionHandlerDAOObj.getRoleofUser(toId);
            ite2 = kmsg.getEntityList().iterator();
            if (ite2.hasNext()) {
                Object[] row = (Object[]) ite2.next();
                childRole = row[0].toString();
            }
            if (crmCommonDAOObj.isPerfectRole(parentRole, childRole)) {
                kmsg = crmCommonDAOObj.insertNode(requestParams);
                User user = (User) kmsg.getEntityList().get(0);
                User parent = (User) kmsg.getEntityList().get(1);
                retMsg = (String) kmsg.getEntityList().get(2).toString();

                if (kmsg.getEntityList().get(2) != null) {
                    Assignmanager am = (Assignmanager) kmsg.getEntityList().get(2);
                    auditTrailDAOObj.insertAuditLog(AuditAction.ADMIN_Organization,
                            user.getFirstName() + " " + user.getLastName() + " assigned to " + parent.getUserLogin().getUserName() + " [ " + parent.getFirstName() + " " + parent.getLastName() + " ] ",
                            request, am.getId());
                }
                jobj.put("data", "{success:true}");
            } else {
                jobj.put("data", "{success:false,msg:\"Couldn't assign, parent node has lower role.\"}");
            }
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    // Organisation Chart - update node
    public ModelAndView updateNode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        String retMsg = "";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String nodeId = StringUtil.checkForNull(request.getParameter("nodeid"));
            String fromId = StringUtil.checkForNull(request.getParameter("fromId"));
            requestParams.put("nodeid", nodeId);
            requestParams.put("fromId", fromId);
            
            // get Parent and Child Use's role
            String parentRole = "", childRole = "";
            kmsg = permissionHandlerDAOObj.getRoleofUser(fromId);
            Iterator ite2 = kmsg.getEntityList().iterator();
            if (ite2.hasNext()) {
                Object[] row = (Object[]) ite2.next();
                parentRole = row[0].toString();
            }
            kmsg = permissionHandlerDAOObj.getRoleofUser(nodeId);
            ite2 = kmsg.getEntityList().iterator();
            if (ite2.hasNext()) {
                Object[] row = (Object[]) ite2.next();
                childRole = row[0].toString();
            }
            requestParams.put("childRole", childRole);
            requestParams.put("parentRole", parentRole);
            kmsg = crmCommonDAOObj.updateNode(requestParams);
            retMsg = (String) kmsg.getEntityList().get(0).toString();
            User user = (User) kmsg.getEntityList().get(1);
            User parent = (User) kmsg.getEntityList().get(2);

            if (kmsg.getEntityList().get(3) != null) {
                auditTrailDAOObj.insertAuditLog(AuditAction.ADMIN_Organization,
                        user.getFirstName() + " " + user.getLastName() + " re-assigned to " + parent.getFirstName() + " " + parent.getLastName(),
                        request, "0");
            }
            jobj.put("data", retMsg);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    // Profile Handler - delete user
    public ModelAndView deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String[] ids = request.getParameterValues("userids");
            String msg = "User deleted successfully";
            for (int i = 0; i < ids.length; i++) {
//                User user = (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", ids[i]);
                KwlReturnObject kmsg = permissionHandlerDAOObj.getRoleofUser(ids[i]);
                Iterator ite3 = kmsg.getEntityList().iterator();
                if(ite3.hasNext()) {
                    Object[] row = (Object[]) ite3.next();
                    String roleid = row[0].toString();
                    if(!roleid.equals(Constants.COMPANY_ADMIN)) {
                        profileHandlerDAOObj.deleteUser(ids[i]);
                    } else {
                        msg = "Company Admin cannot be deleted";
                    }
                }
            }
            jobj.put("msg", msg);
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getColumnHeader(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            String modName= request.getParameter("modulename");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            jobj = getColumnHeaderJSON(modName,companyid,false,RequestContextUtils.getLocale(request));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    public ModelAndView getMappedHeaders(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        ArrayList filter_params = new ArrayList();
        ArrayList filter_names = new ArrayList();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String modName= request.getParameter("modulename");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            filter_params = new ArrayList();
            filter_names = new ArrayList();
            filter_names.add("m.company.companyID");
            filter_params.add(companyid);
            filter_names.add("m.modulefield.moduleName");
            filter_params.add(modName);
            JSONObject tmpObj;
            requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            kmsg = crmCommonDAOObj.getMappedHeaders(requestParams);
            List<LeadConversionMappings> LeadConversionMappingsList = kmsg.getEntityList();
            if(LeadConversionMappingsList!=null) {
                for (LeadConversionMappings obj : LeadConversionMappingsList) {
                    if (obj.getModulefield() != null && obj.getLeadfield() != null) {
                        try {
                            tmpObj = new JSONObject();
                            tmpObj.put("modulefieldname", obj.getModulefield().getRecordname());
                            tmpObj.put("leadfieldname", obj.getLeadfield().getRecordname());
                            tmpObj.put("isDefaultMapping", obj.isDefaultMapping());
                            tmpObj.put("modulefieldid", obj.getModulefield().getId());
                            tmpObj.put("leadfieldid", obj.getLeadfield().getId());
                            tmpObj.put("modulefieldxtype", obj.getModulefield().getXtype());
                            tmpObj.put("leadfieldxtype", obj.getLeadfield().getXtype());
                            jarr.put(tmpObj);
                        } catch (ObjectNotFoundException oe) {
                            oe.printStackTrace();
                        }
                    }
                }
            }
            jobj.put("data", jarr);
           
            jobj.put("success", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    public ModelAndView getMappingHeaders(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        ArrayList filter_params = new ArrayList();
        ArrayList filter_names = new ArrayList();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            JSONObject tmpObj;
            HashMap<String,JSONObject> moduleheaderinfo = new HashMap<String,JSONObject>();
            HashMap<String,JSONObject> leadheaderinfo = new HashMap<String,JSONObject>();
            String modName= request.getParameter("modulename");
            String companyid = sessionHandlerImpl.getCompanyid(request);

            // Module(Account/Opportunity/Contact) Headers
            filter_names.add("dh.moduleName");
            filter_params.add(modName);
            filter_names.add("!dh.configid");
            filter_params.add("1");
            filter_names.add("dh.allowImport");
            filter_params.add(true);
            filter_names.add("dh.allowMapping");
            filter_params.add(true);
            filter_names.add("dh.customflag");
            filter_params.add(false);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", modName);

            kmsg = fieldManagerDAOobj.getDefaultHeader(requestParams);
            List<DefaultHeader> defaultHeaders = kmsg.getEntityList();
            List<String> headerIds = new ArrayList<String>();
            for (DefaultHeader obj: defaultHeaders) {
                headerIds.add(obj.getId());
            }

            Map<String, Object[]> results = getColumnHeaderMap(fieldManagerDAOobj, headerIds, companyid);
            for (DefaultHeader obj: defaultHeaders) {
                tmpObj = new JSONObject();
                if (results.containsKey(obj.getDefaultHeader()))
                {
                    Object[] mapEntry = results.get(obj.getDefaultHeader());
                    ColumnHeader obj1 = (ColumnHeader) mapEntry[1];
                    DefaultHeader obj2 = (DefaultHeader) mapEntry[0];
                    tmpObj.put("header", StringUtil.isNullOrEmpty(obj1.getNewHeader())? obj2.getDefaultHeader() :obj1.getNewHeader().trim());
                    tmpObj.put("isMandatory", obj1.isMandotory());
                } else if(!obj.isCustomflag()) {
                    tmpObj.put("header", obj.getDefaultHeader());
                    tmpObj.put("isMandatory", obj.isMandatory());
                }
                if(tmpObj.has("header")){
                    tmpObj.put("modulefieldid", obj.getId());
                    tmpObj.put("modulefieldxtype", obj.getXtype());
                    moduleheaderinfo.put(obj.getId(), tmpObj);
                }
            }
            
            // Lead Headers
            filter_params = new ArrayList();
            filter_names = new ArrayList();
            filter_names.add("dh.moduleName");
            filter_params.add("Lead");
            filter_names.add("!dh.configid");
            filter_params.add("1");
            filter_names.add("dh.allowImport");
            filter_params.add(true);
            filter_names.add("dh.allowMapping");
            filter_params.add(true);
            filter_names.add("dh.customflag");
            filter_params.add(false);
            requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", "Lead");
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);

            kmsg = fieldManagerDAOobj.getDefaultHeader(requestParams);
            defaultHeaders = kmsg.getEntityList();
            headerIds = new ArrayList<String>();
            for (DefaultHeader obj: defaultHeaders) {
                headerIds.add(obj.getId());
            }

            results = getColumnHeaderMap(fieldManagerDAOobj, headerIds, companyid);
            for (DefaultHeader obj: defaultHeaders) {
                tmpObj = new JSONObject();
                if (results.containsKey(obj.getDefaultHeader()))
                {
                    Object[] mapEntry = results.get(obj.getDefaultHeader());
                    ColumnHeader obj1 = (ColumnHeader) mapEntry[1];
                    DefaultHeader obj2 = (DefaultHeader) mapEntry[0];
                    tmpObj.put("columnname", StringUtil.isNullOrEmpty(obj1.getNewHeader())? obj2.getDefaultHeader() :obj1.getNewHeader().trim());
                    tmpObj.put("isMandatory", obj1.isMandotory());
                } else if(!obj.isCustomflag()) {
                    tmpObj.put("columnname", obj.getDefaultHeader());
                    tmpObj.put("isMandatory", obj.isMandatory());
                }
                if(tmpObj.has("columnname")){
                    tmpObj.put("leadfieldid", obj.getId());
                    tmpObj.put("leadfieldxtype", obj.getXtype());
                    leadheaderinfo.put(obj.getId(), tmpObj);
                }
            }
            
            filter_params = new ArrayList();
            filter_names = new ArrayList();
            filter_names.add("m.company.companyID");
            filter_params.add(companyid);
            filter_names.add("m.modulefield.moduleName");
            filter_params.add(modName);

            requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", modName);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);

            kmsg = crmCommonDAOObj.getMappedHeaders(requestParams);
            List<LeadConversionMappings> objList = kmsg.getEntityList();
            for (LeadConversionMappings obj : objList) {
                tmpObj = new JSONObject();
                if(moduleheaderinfo.get(obj.getModulefield().getId()) != null &&  leadheaderinfo.get(obj.getLeadfield().getId()) != null ){
                    tmpObj.put("modulefieldname", ((JSONObject)(moduleheaderinfo.get(obj.getModulefield().getId()))).get("header"));
                    tmpObj.put("leadfieldname",((JSONObject)(leadheaderinfo.get(obj.getLeadfield().getId()))).get("columnname"));
                    tmpObj.put("isDefaultMapping",obj.isDefaultMapping());
                    tmpObj.put("modulefieldid",obj.getModulefield().getId());
                    tmpObj.put("leadfieldid",obj.getLeadfield().getId());
                    tmpObj.put("modulefieldxtype",obj.getModulefield().getXtype());
                    tmpObj.put("leadfieldxtype",obj.getLeadfield().getXtype());
                    moduleheaderinfo.remove(obj.getModulefield().getId());
                    leadheaderinfo.remove(obj.getLeadfield().getId());
                    jarr.put(tmpObj);
                }
            }
            jobj.put("mappedHeaders", jarr);

            jarr = new JSONArray();
            for(Object key:moduleheaderinfo.keySet()){
                tmpObj = (JSONObject)moduleheaderinfo.get(key);
                tmpObj.put("isDefaultMapping", false);
                jarr.put(tmpObj);
            }
            jobj.put("moduleHeaders", jarr);

            jarr = new JSONArray();
            for(Object key:leadheaderinfo.keySet()){
                tmpObj = (JSONObject)leadheaderinfo.get(key);
                tmpObj.put("isDefaultMapping", false);
                jarr.put(tmpObj);
            }
            jobj.put("leadHeaders", jarr);

            jobj.put("success", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getUserReportConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("success", false);
            String userid = request.getParameter("userid");

            ArrayList<String> filter_names = new ArrayList<String>();
            ArrayList<Object> filter_values = new ArrayList<Object>();
            filter_names.add("dc.userid");
            filter_values.add(userid);

            KwlReturnObject kr = crmCommonDAOObj.getDashboardReportConfig(filter_names, filter_values);
            List<Object[]> configList = kr.getEntityList();
            JSONArray reportArr = new JSONArray();
            for (Object[] row : configList) {
                String rid = row[2].toString();
                String rname = row[3].toString();
                JSONObject reportWidgetConfig = new JSONObject();
                reportWidgetConfig.put("reportcode", rid);
                reportWidgetConfig.put("reportname", rname);
                reportWidgetConfig.put("dashboardreport", row[4].toString());
                reportWidgetConfig.put("emailreport", row[5].toString());
                reportArr.put(reportWidgetConfig);
            }
            jobj.put("reportconfig", reportArr);
            jobj.put("success", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.warn(ex.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    public ModelAndView setDashboardReportConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("success", false);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = request.getParameter("userid");
            String reportid = request.getParameter("reportid");
            String reportname = request.getParameter("reportname");
            int dashboardflag=0;
            if(request.getParameter("dashdisplay") != null && request.getParameter("dashdisplay").equals("1"))
                dashboardflag = 1;
            int emailreportflag=0;
            if(request.getParameter("emailreport") != null && request.getParameter("emailreport").equals("1"))
                emailreportflag = 1;

            ArrayList<String> filter_names = new ArrayList<String>();
             ArrayList<Object> filter_values = new ArrayList<Object>();
//             filter_names.add("dc.reportid");
//             filter_values.add(46);
             filter_names.add("dc.userid");
             filter_values.add(userid);
             filter_names.add("dc.reportid");
             filter_values.add(reportid);

             KwlReturnObject kr = crmCommonDAOObj.getDashboardReportConfig(filter_names, filter_values);
             List<Object[]> configList = kr.getEntityList();
             String dashreportid = java.util.UUID.randomUUID().toString();
             boolean editFlag = false;
             ArrayList filter_params=new ArrayList();
             if(configList.size() > 0) {
                editFlag = true;
                filter_params.add(reportname);
                filter_params.add(dashboardflag);
                filter_params.add(emailreportflag);
                filter_params.add(userid);
                filter_params.add(reportid);
             } else {
                filter_params.add(dashreportid);
                filter_params.add(companyid);
                filter_params.add(userid);
                filter_params.add(reportid);
                filter_params.add(reportname);
                filter_params.add(dashboardflag);
                filter_params.add(emailreportflag);
             }
             crmCommonDAOObj.setDashboardReportConfig(filter_params, editFlag);
             jobj.put("success", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.warn(ex.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
  }

  public ModelAndView saveMappedheaders(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            JSONObject jsondata = new JSONObject(request.getParameter("jsondata"));
            JSONArray jsonarray = new JSONArray(jsondata.get("root").toString());
            JSONObject tempobj;
            String moduleName=request.getParameter("moduleName");
            HashMap<String, Object> requestParams ;
            ArrayList filter_params=new ArrayList();;
            ArrayList filter_names= new ArrayList();
            filter_names.add("m.company.companyID");
            filter_params.add(companyid);
            filter_names.add("m.moduleName");
            filter_params.add(moduleName);
            filter_names.add("m.defaultMapping");
            filter_params.add(false);

            if(jsonarray.length() > 0){
                for(int i=0;i< jsonarray.length();i++){
                    tempobj = jsonarray.getJSONObject(i);
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("companyid", companyid);
                    requestParams.put("modulefieldid", tempobj.get("modulefieldid"));
                    requestParams.put("leadfieldid", tempobj.get("leadfieldid"));
                    requestParams.put("moduleName", moduleName);
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_params", filter_params);
                    crmCommonDAOObj.saveMappedheaders(requestParams);
                }
            }else{
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("companyid", companyid);
                    requestParams.put("modulefieldid", "");
                    requestParams.put("leadfieldid", "");
                    requestParams.put("moduleName", moduleName);
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_params", filter_params);
                    crmCommonDAOObj.saveMappedheaders(requestParams);
            }
            jobj.put("success", true);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
   private JSONObject getColumnHeaderJSON(String modName, String companyid,boolean quickinsertform,Locale lc) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        ArrayList filter_params = new ArrayList();
        ArrayList filter_names = new ArrayList();
        KwlReturnObject kmsg = null;
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            filter_names.add("dh.moduleName");
            filter_params.add(modName);
            filter_names.add("!dh.configid");
            filter_params.add("1");
            filter_names.add("dh.allowImport");
            filter_params.add(true);
            filter_names.add("dh.customflag");
            filter_params.add(false);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", modName);

            if(!quickinsertform){
                requestParams.put("order_by", true);
                requestParams.put("order_type", "defaultHeader");
            } else {
                requestParams.put("order_by", true);
                requestParams.put("order_type", "fieldsequence");
            }

            kmsg = fieldManagerDAOobj.getDefaultHeader(requestParams);
            int dl = kmsg.getRecordTotalCount();
            List<DefaultHeader> defaultHeaders = kmsg.getEntityList();
            List<String> headerIds = new ArrayList<String>();
            for (DefaultHeader obj: defaultHeaders) {
                headerIds.add(obj.getId());
            }

            Map<String, Object[]> results = getColumnHeaderMap(fieldManagerDAOobj, headerIds, companyid);
            for (DefaultHeader obj: defaultHeaders) {
                String customcol = messageSource.getMessage("crm.common.defaultColumn",null,lc);//"Default Column";
                if (results.containsKey(obj.getDefaultHeader()))
                {
                    if(obj.isCustomflag()){
                        customcol = messageSource.getMessage("crm.common.customColumn",null,lc);//"Custom Column";
                    }
                    Object[] mapEntry = results.get(obj.getDefaultHeader());
                    ColumnHeader obj1 = (ColumnHeader) mapEntry[1];
                    DefaultHeader obj2 = (DefaultHeader) mapEntry[0];
                    JSONObject jtemp = getHeaderObject(obj2);
                    jtemp.put("newheader", StringUtil.isNullOrEmpty(obj1.getNewHeader())? "" :obj1.getNewHeader());
                    jtemp.put("ismandotory",obj1.isMandotory());
                    jtemp.put("id", obj1.getId());
                    jtemp.put("configid", obj1.getId());
                    jtemp.put("columntype", customcol);
                    jtemp.put("refcolumn_number",Constants.Custom_Column_Prefix+obj2.getDbcolumnrefname());
                    jtemp.put("column_number", StringUtil.isNullOrEmpty(obj2.getDbcolumnname()) && obj2.getConfigid().equals("1")?"":obj2.getDbcolumnname()); // confidid = 1 for Owner column... now set some value for custom report
                    
                    jarr.put(jtemp);
                } else if(!obj.isCustomflag()) {
                    JSONObject jtemp = getHeaderObject(obj);
                    jtemp.put("header", StringUtil.isNullOrEmpty(jtemp.getString("localekey"))?obj.getDefaultHeader():messageSource.getMessage(jtemp.getString("localekey"),null, lc));
                    jtemp.put("newheader", "");
                    jtemp.put("columntype", customcol);
                    jtemp.put("ismandotory",obj.isMandatory());
                    jtemp.put("id","");
                    jarr.put(jtemp);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
        } catch(JSONException ex) {
            System.out.println(ex.getMessage());
        } 
        return jobj;
    }
    
//    public ModelAndView saveColumnHeader(HttpServletRequest request, HttpServletResponse response) throws ServletException {
//        JSONObject jobj = new JSONObject();
//        KwlReturnObject kmsg = null;
////        KwlReturnObject kmsgDup=null;
////        KwlReturnObject kmsgDup1=null;
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setName("JE_Tx");
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
//        TransactionStatus status = txnManager.getTransaction(def);
//        Session session = HibernateUtil.getCurrentSession();
//        Transaction tx = session.beginTransaction();
//        try {
//            jobj.put("success", false);
//            String newheader = request.getParameter("newheader");
//            String oldheader = request.getParameter("oldheader");
//            String modulename = request.getParameter("modulename");
//            String id = request.getParameter("id");
////            boolean customflag = Boolean.parseBoolean(request.getParameter("customflag"));
//            String ismandatory = request.getParameter("isMandatory");
//            String pojoname = request.getParameter("pojoname");
//            String xtype = request.getParameter("xtype");
//            String headerid = request.getParameter("headerid");
//            String userid = sessionHandlerImpl.getUserid(request);
//            String companyid = sessionHandlerImpl.getCompanyid(request);
//            HashMap<String, Object> requestParams = new HashMap<String, Object>();
////            HashMap<String, Object> requestParamsForDefault = new HashMap<String, Object>();
//            requestParams.put("newheader", newheader);
//            requestParams.put("oldheader", oldheader);
//            requestParams.put("modulename", modulename);
//            requestParams.put("headerid", headerid);
//            requestParams.put("id", id);
//            requestParams.put("isMandatory", ismandatory);
//            requestParams.put("pojoname", pojoname);
//            requestParams.put("xtype", xtype);
//            requestParams.put("userid", userid);
//            requestParams.put("companyid", companyid);
//            kmsg = permissionHandlerDAOObj.getRoleofUser(userid);
//            Iterator ite2 = kmsg.getEntityList().iterator();
//            if (ite2.hasNext()) {
//                Object[] row = (Object[]) ite2.next();
//                requestParams.put("roleid", row[0]);
//            }
////            ArrayList filter_params = new ArrayList();
////            ArrayList filter_names = new ArrayList();
////            filter_names.add("c.moduleName");
////            filter_params.add(modulename);
////            filter_names.add("c.defaultHeader");
////            filter_params.add(newheader);
////            requestParamsForDefault.put("filter_names", filter_names);
////            requestParamsForDefault.put("filter_params", filter_params);
////            kmsgDup = crmCommonDAOObj.getDefaultColumnHeader(requestParamsForDefault);
////
////            Iterator defaultite = kmsgDup.getEntityList().iterator();
////            boolean flag = true;
////            boolean flagcolumn = false;
////            if (defaultite.hasNext()) {
////                flag = true;
////                DefaultHeader dh = (DefaultHeader) defaultite.next();
////                if (headerid.equals(dh.getId())) {
////                    flag = false;
////                }
////            }
//            String selectQuery = "select dh.id from default_header dh inner join modules mo on mo.id = dh.module where mo.modulename = ? " +
//                "and (dh.customflag = 'F' or dh.customflag = '0') and dh.id not in (select defaultHeader " +
//                "from column_header where company = ?)  and defaultHeader = ? and dh.id !=?" +
//                " UNION " +
//                "select ch.id from column_header ch inner join default_header dh on dh.id = ch.defaultHeader " +
//                "inner join modules mo on mo.id = dh.module where company = ? and mo.modulename = ? and dh.id !=? and " +
//                "(ch.newHeader = ? or dh.defaultHeader = ?)";
//            SQLQuery dupSql=session.createSQLQuery(selectQuery);
//            dupSql.setParameter(0, modulename);
//            dupSql.setParameter(1, companyid);
//            dupSql.setParameter(2, newheader);
//            dupSql.setParameter(3, headerid);
//            dupSql.setParameter(4, companyid);
//            dupSql.setParameter(5, modulename);
//            dupSql.setParameter(6, headerid);
//            dupSql.setParameter(7, newheader);
//            dupSql.setParameter(8, newheader);
//            ArrayList dupListcol = (ArrayList)dupSql.list();
//            if(!dupListcol.isEmpty() && !StringUtil.isNullOrEmpty(newheader)) {
//                jobj.put("success", false);
//                jobj.put("msg", "Column with the same name already exists.");
//            }else{
//                kmsg = crmCommonDAOObj.saveColumnHeader(requestParams);
////                if(Boolean.parseBoolean(ismandatory) && !StringUtil.isNullOrEmpty(request.getParameter("defaultvalue"))) {
////                    String defaultvalue = request.getParameter("defaultvalue");
////                    if(customflag) {// if custom column
////                        String header = StringUtil.isNullOrEmpty(newheader) ? oldheader : newheader;
////                        fieldManager.storeDefaultCstmData(fieldManager.getModuleId(modulename), Integer.parseInt(pojoname), header, defaultvalue, Integer.parseInt(xtype),companyid);
//////                        storeDefaultCstmData(moduleid, fieldid, fieldlabel,defaultvalue, fieldtype);
////                    } else { // if default column
//////                        crmCommonDAOObj.setDefaultValue(modulename,headerid,companyid,defaultvalue);
////                    }
////                }
//////                crmCommonDAOObj.validaterecords(modulename, companyid, session);
//                crmCommonDAOObj.validaterecords(modulename, companyid);
//                jobj.put("success", kmsg.isSuccessFlag());
//                if(StringUtil.checkResultobjList(kmsg))
//                     jobj.put("id", kmsg.getEntityList().get(0));
//            }
//             txnManager.commit(status);
//            tx.commit();
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//            txnManager.rollback(status);
//            tx.rollback();
//        }finally{
//            HibernateUtil.closeSession(session);
//        }
//        return new ModelAndView("jsonView", "model", jobj.toString());
//    }

    public ModelAndView getconfigdata(HttpServletRequest request,HttpServletResponse response) throws ServiceException {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        ArrayList filter_params = new ArrayList();
        ArrayList filter_names = new ArrayList();
        try {
            boolean quickinsertform = Boolean.parseBoolean(request.getParameter("quickinsertform"));
            boolean getall = Boolean.parseBoolean(request.getParameter("getallfields"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            Locale lc=RequestContextUtils.getLocale(request);
            JSONObject resjobj = getColumnHeaderJSON(request.getParameter("configType"),sessionHandlerImpl.getCompanyid(request),quickinsertform,RequestContextUtils.getLocale(request));
            JSONArray tempJArr = resjobj.getJSONArray("data");
            for(int cnt =0; cnt<tempJArr.length();cnt++) {
                JSONObject colmnJSON = tempJArr.getJSONObject(cnt);
                if(colmnJSON.getBoolean("ismandotory")||getall) {
                    JSONObject tmpObj = new JSONObject();
                    if(!StringUtil.isNullOrEmpty(colmnJSON.getString("localekey")))
                    		tmpObj.put("fieldname", StringUtil.isNullOrEmpty(colmnJSON.getString("newheader")) ? messageSource.getMessage(colmnJSON.getString("localekey"), null,lc): colmnJSON.getString("newheader"));
                    else
                			tmpObj.put("fieldname", StringUtil.isNullOrEmpty(colmnJSON.getString("newheader")) ? colmnJSON.getString("defaultheader"): colmnJSON.getString("newheader"));
                    tmpObj.put("configid", colmnJSON.getString("id"));
                    tmpObj.put("recordname", colmnJSON.isNull("recordname") ? "" : colmnJSON.getString("recordname"));
                    tmpObj.put("configtype", colmnJSON.getString("xtype"));
                    tmpObj.put("allowblank", !colmnJSON.getBoolean("ismandotory"));
                    tmpObj.put("customflag", colmnJSON.getBoolean("customflag"));
                    tmpObj.put("pojoname", colmnJSON.getString("pojoname"));
                    tmpObj.put("maxlength", colmnJSON.getString("maxlength"));
                    
                    if(colmnJSON.getBoolean("customflag")){
                        tmpObj.put("pojomethodname", colmnJSON.getString("pojomethodname"));
                        tmpObj.put("refcolumn_number",colmnJSON.getString("refcolumn_number"));
                        tmpObj.put("column_number",colmnJSON.getString("column_number"));
                    }
                    if(colmnJSON.getString("xtype").equals("4") || colmnJSON.getString("xtype").equals("8") || colmnJSON.getString("xtype").equals("7")){//Combo, Multiselect & //Reference combo
                        filter_names.clear();
                        filter_params.clear();
                        requestParams.clear();
                        String masterconfid = colmnJSON.getString("masterconfid");
                        if(masterconfid.equals("1") || masterconfid.equals("2") || masterconfid.equals("3") || masterconfid.equals("4")) {// if configid in default_header table is 1 then fetch owner data
                            tmpObj.append("masterconfigid", masterconfid);
                            tmpObj.append("module", request.getParameter("configType"));
                        } else {
                            filter_names.add("masterid");
                            filter_params.add(colmnJSON.getString("masterconfid"));
                            requestParams.put("filter_names", filter_names);
                            requestParams.put("filter_params", filter_params);
                            requestParams.put("allowLeadType", true);
                            result = crmManagerDAOObj.getMaster(requestParams);
                            List<CrmCombomaster> CrmCombomasterList = result.getEntityList();
                            if(CrmCombomasterList != null) {
                                for (CrmCombomaster obj : CrmCombomasterList) {
                                    tmpObj.append("comboname", obj.getComboname());
                                }
                            }
                        }
                    }
                    jarr.put(tmpObj);
                }
            }
            jobj.put("data", jarr);
    } catch (Exception e) {
            e.printStackTrace();
    } finally {

    }
    return new ModelAndView("jsonView","model",jobj.toString());
}

    private JSONObject getHeaderObject(DefaultHeader obj) throws JSONException {
        JSONObject tmpObj = new JSONObject();
        tmpObj.put("header", obj.getDefaultHeader());
        tmpObj.put("headerid", obj.getId());
        tmpObj.put("defaultheader",obj.getDefaultHeader());
        tmpObj.put("pojoname", obj.getPojoheadername());
        tmpObj.put("xtype", obj.getXtype());
        tmpObj.put("required", obj.isRequired());
        tmpObj.put("recordname", obj.getRecordname());
        tmpObj.put("masterconfid", obj.getConfigid());
        tmpObj.put("customflag", obj.isCustomflag());
        tmpObj.put("modulename", obj.getModuleName());
        tmpObj.put("pojomethodname", obj.getPojoMethodName());
        tmpObj.put("maxlength", obj.getMaxLength());
        tmpObj.put("localekey", StringUtil.isNullOrEmpty(obj.getLocalekey())?"":obj.getLocalekey());
        return tmpObj;
    }
        
    private static Map<String, Object[]> getColumnHeaderMap(fieldManagerDAO fieldManagerDAOobj, List<String> headerIds, String companyId)
    {
	    Map<String, Object[]> result = new HashMap<String, Object[]>();
	    List<Object[]> colList = fieldManagerDAOobj.getColumnHeader(companyId, headerIds);

	    if (colList != null)
	    {
	        for (Object[] col: colList)
	        {
	            DefaultHeader dh = (DefaultHeader) col[0];
	            result.put(dh.getDefaultHeader(), col);
	        }
	    }
        return result;
    }
    
    public ModelAndView saveColumnHeader(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
            KwlReturnObject kmsg = null;
//        KwlReturnObject kmsgDup=null;
//        KwlReturnObject kmsgDup1=null;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            jobj.put("success", false);
            String newheader = request.getParameter("newheader");
            String oldheader = request.getParameter("oldheader");
            String modulename = request.getParameter("modulename");
            String id = request.getParameter("id");
            String ismandatory = request.getParameter("isMandatory");
            String pojoname = request.getParameter("pojoname");
            String xtype = request.getParameter("xtype");
            String headerid = request.getParameter("headerid");
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            HashMap<String, Object> requestParamsForDefault = new HashMap<String, Object>();
            requestParams.put("newheader", newheader);
            requestParams.put("oldheader", oldheader);
            requestParams.put("modulename", modulename);
            requestParams.put("headerid", headerid);
            requestParams.put("id", id);
            requestParams.put("isMandatory", ismandatory);
            requestParams.put("pojoname", pojoname);
            requestParams.put("xtype", xtype);
            requestParams.put("userid", userid);
            requestParams.put("companyid", companyid);
            kmsg = permissionHandlerDAOObj.getRoleofUser(userid);
            Iterator ite2 = kmsg.getEntityList().iterator();
            if (ite2.hasNext()) {
                Object[] row = (Object[]) ite2.next();
                requestParams.put("roleid", row[0]);
            }
            boolean isPresent = crmCommonService.columnExists(modulename, companyid, newheader, headerid);
            if(isPresent) {
                jobj.put("success", false);
                jobj.put("msg", "Column with the same name already exists.");
            }else{
                kmsg = crmCommonDAOObj.saveColumnHeader(requestParams);
                crmCommonDAOObj.validaterecords(modulename, companyid);
                jobj.put("success", kmsg.isSuccessFlag());
                if(StringUtil.checkResultobjList(kmsg))
                     jobj.put("id", kmsg.getEntityList().get(0));
            }
             txnManager.commit(status);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView openModuleUsagePie(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        try
        {
            int max = 5;
            result = "<pie>";
            List<AuditReportModuleUsageDTO> auditReportModuleUsageList = auditLoggerService.getUsageReportData();
            int i=0;
            long otherTime = 0L;
            long moduleLTime=0L;
            String moduleDHMS=null;
            for (AuditReportModuleUsageDTO auditReportModuleUsage : auditReportModuleUsageList)
            {
                moduleLTime=Long.valueOf(auditReportModuleUsage.getTime());
                moduleDHMS=DateUtil.millisToShortDHMS(moduleLTime);
                if(i>=max){
                    otherTime+=moduleLTime;
                }
                else if(!auditReportModuleUsage.getTime().equalsIgnoreCase("0")){
                    result += "<slice title=\"" 
                        + auditReportModuleUsage.getModuleName()+"\" description= \""+moduleDHMS
                            + "\">" + auditReportModuleUsage.getTime() + "</slice>";
                }                
                i++;
            }
            if(otherTime>0){
                String otherDHMS=DateUtil.millisToShortDHMS(otherTime);
                result += "<slice title=\""

                    + GRAPH_LEGEND_OTHER+"\" description= \""+otherDHMS
                        + "\">" + otherTime + "</slice>";
            }
            result += "</pie>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }
    
    public ModelAndView moduleBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       String result = "";
        try {
            int max =5;// Integer.parseInt(jobj.get("totalCount").toString());
            result = null;
            List<AuditReportHitsPerModuleDTO>  auditReportHitsPerModuleList=  auditLoggerService.getHitCountReportData();
            int i=1;
            String series = "<chart><series>";
            String graphs = "";
            Long otherTime = 0L;
            //Build value tags
            for (AuditReportHitsPerModuleDTO auditReportHitsPerModule : auditReportHitsPerModuleList)
            {
                if(i>=max){
                    otherTime+=Long.valueOf(auditReportHitsPerModule.getHitCount());
                }
                else if (!auditReportHitsPerModule.getHitCount().equalsIgnoreCase("0"))
                {
                    graphs += "<value xid=\"" + i + "\" >" + auditReportHitsPerModule.getHitCount() + "</value>";
                }
                i++;
            }            
            i=1;
            //Build series tags
            for(AuditReportHitsPerModuleDTO auditReportHitsPerModule:auditReportHitsPerModuleList){
                if(!auditReportHitsPerModule.getHitCount().equalsIgnoreCase("0")){
                    series += "<value xid=\"" + (i) + "\" >" 
                        + auditReportHitsPerModule.getModuleName() 
                            + "</value>";
                }
             if(i==max) break;i++;
            }
            if(otherTime>0){
                graphs += "<value xid=\"" + i + "\" >" + otherTime + "</value>";
                series += "<value xid=\"" + (i) + "\" >" 
                + GRAPH_LEGEND_OTHER 
                    + "</value>";
            }
            result += series + "</series><graphs><graph gid=\"0\">";
            result += graphs + "</graph></graphs></chart>";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("chartView", "model", result);
    }

}
