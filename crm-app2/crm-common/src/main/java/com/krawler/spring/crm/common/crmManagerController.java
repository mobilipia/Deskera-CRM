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
import com.krawler.common.admin.Company;
import com.krawler.common.admin.FieldComboData;
import com.krawler.common.admin.FieldParams;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmCombomaster;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.utils.AuditAction;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.FieldConstants;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.companyDetails.companyDetailsDAO;
import com.krawler.spring.companyDetails.companyDetailsHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class crmManagerController extends MultiActionController {
    private crmManagerDAO crmManagerDAOObj;
    private String successView;
    private sessionHandlerImpl sessionHandlerImplObj;
    private HibernateTransactionManager txnManager;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private fieldManagerDAO fieldManagerDAOobj;
    private companyDetailsDAO companyDetailsDAOObj;
    private CrmCommonService crmCommonService;
    private auditTrailDAO auditTrailDAOObj;
    
    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }
    
    public void setcompanyDetailsDAO(companyDetailsDAO companyDetailsDAOObj1) {
        this.companyDetailsDAOObj = companyDetailsDAOObj1;
    }

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
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

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }
    public crmManagerDAO getcrmManagerDAO() {
		return crmManagerDAOObj;
	}
    public void setcrmCommonService(CrmCommonService crmCommonService){
    	this.crmCommonService=crmCommonService;
    }
    public CrmCommonService getcrmCommonService(){
    	return crmCommonService;
    }
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public ModelAndView getComboData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       JSONArray jarr = new JSONArray();
       int dl = 0;
       try{
            int customflag = 0;
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            if(request.getParameter("customflag") != null) {
                customflag = Integer.parseInt(request.getParameter("customflag"));
            }
            if(customflag == 0) {
                String comboname = StringUtil.checkForNull(request.getParameter("comboname"));
                boolean moduleReq = StringUtil.isNullOrEmpty(request.getParameter("moduleReq"))?false :true;
                String companyid = sessionHandlerImpl.getCompanyid(request);
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                
                filter_names.add("d.company.companyID");
                filter_params.add(companyid);
                if(comboname.equalsIgnoreCase("Lead Status") && moduleReq) {
                    filter_names.add("!d.mainID");
                    filter_params.add(Constants.LEADSTATUSID_QUALIFIED);
                }
                if(comboname.equalsIgnoreCase(Constants.Lead_Source)) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
                    if(!heirarchyPerm){
                        String userid = sessionHandlerImpl.getUserid(request);
                        StringBuffer usersList = new StringBuffer();
                        usersList = sessionHandlerImpl.getRecursiveUsersList(request);
                        requestParams.put("userlist_value", usersList);
                    }
                    order_by.add("d.itemsequence");
                    order_type.add("asc");
                    requestParams.put("companyid", companyid);
                }
                order_by.add("d.itemsequence");
                order_type.add("asc");
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_params", filter_params);
                requestParams.put("order_by", order_by);
                requestParams.put("order_type", order_type);
                List ll = crmManagerDAOObj.getComboData(comboname, requestParams);
                dl = ll.size();
                Iterator ite = ll.iterator();
                boolean hasAccess = true ;
                requestParams.put(Constants.companyid, companyid);
                while (ite.hasNext()) {
                    DefaultMasterItem crmCombodata = (DefaultMasterItem) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    String name = crmCombodata.getValue();
                    tmpObj.put("id", crmCombodata.getID());
                    tmpObj.put("mainid", crmCombodata.getMainID());
                    tmpObj.put("name", name);
                    tmpObj.put("itemsequence", crmCombodata.getItemsequence());
                    tmpObj.put("percentStage", crmCombodata.getPercentStage());
                    
                    if(comboname.equalsIgnoreCase(Constants.Lead_Source)) {
                            String tempcomboname = crmCombodata.getCrmCombomaster().getComboname();
                            if(tempcomboname.equalsIgnoreCase(Constants.Campaign_Source)) {
                                requestParams.put(Constants.Crm_campaignid, crmCombodata.getMainID());
                                hasAccess = crmManagerDAOObj.isCrmCampaignArchived(requestParams);
                            }
                    }
                    tmpObj.put(Constants.Crm_hasAccess,hasAccess);
                    jarr.put(tmpObj);
                }
            } else {
                String companyid = sessionHandlerImpl.getCompanyid(request);
                String fieldid = request.getParameter("configid");
                KwlReturnObject result = null;
                KwlReturnObject result1 = null;
                boolean hasAccess = true;
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                    requestParams.put(Constants.filter_names, Arrays.asList(FieldConstants.Crm_fieldid,FieldConstants.Crm_deleteflag));
                    requestParams.put(Constants.filter_values, Arrays.asList(fieldid,0));
                    order_by.add("itemsequence");
                    order_type.add("asc");
                    requestParams.put(Constants.order_by,order_by );
                    requestParams.put(Constants.order_type, order_type);
                    result = fieldManagerDAOobj.getCustomCombodata(requestParams);
                    List lst = result.getEntityList();
                    Iterator ite = lst.iterator();
                    while (ite.hasNext()) {
                         FieldComboData tmpcontyp = (FieldComboData) ite.next();
                         JSONObject jobjTemp = new JSONObject();
                         jobjTemp.put(FieldConstants.Crm_id, tmpcontyp.getId());
                         jobjTemp.put(FieldConstants.Crm_name, tmpcontyp.getValue());
                         jobjTemp.put(Constants.Crm_hasAccess,hasAccess);
                         jobjTemp.put("itemsequence",tmpcontyp.getItemsequence());
                         
                         HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
                            requestParams1.put("filter_names", Arrays.asList("id"));
                            
                            requestParams1.put("filter_values", Arrays.asList(tmpcontyp.getFieldid()));
                            result1 = fieldManagerDAOobj.getFieldParams(requestParams1);
                            List lst1 = result1.getEntityList();
                            Iterator ite1 = lst1.iterator();
                            while (ite1.hasNext()) {
                                FieldParams fp = (FieldParams) ite1.next();
                                jobjTemp.put(FieldConstants.Crm_maxlength, fp.getMaxlength());
                            }
                         jarr.put(jobjTemp);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
       } catch(Exception e) {
          System.out.println(e.getMessage());
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getRefComboData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       JSONArray jarr = new JSONArray();
       int dl = 0;
       try{
           
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            ArrayList order_by =  new ArrayList();
            ArrayList order_type =  new ArrayList();
            String configid = StringUtil.checkForNull(request.getParameter("configid"));
            String comboname = crmManagerDAOObj.getComboName(configid);
            
                
            boolean moduleReq = StringUtil.isNullOrEmpty(request.getParameter("moduleReq"))?false :true;
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            filter_names.add("d.company.companyID");
            filter_params.add(companyid);
            if(comboname.equalsIgnoreCase("Lead Status") && moduleReq) {
                filter_names.add("!d.mainID");
                filter_params.add(Constants.LEADSTATUSID_QUALIFIED);
            }
            if(comboname.equalsIgnoreCase(Constants.Lead_Source)) {
                boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
                if(!heirarchyPerm){
                    String userid = sessionHandlerImpl.getUserid(request);
                    StringBuffer usersList = new StringBuffer();
                    usersList = sessionHandlerImpl.getRecursiveUsersList(request);
                    requestParams.put("userlist_value", usersList);
                }
                order_by.add("d.itemsequence");
                order_type.add("asc");
                requestParams.put("companyid", companyid);
            }
            order_by.add("d.itemsequence");
            order_type.add("asc");
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            List ll = crmManagerDAOObj.getComboData(comboname, requestParams);
            dl = ll.size();
            Iterator ite = ll.iterator();
            boolean hasAccess = true ;
            requestParams.put(Constants.companyid, companyid);
            while (ite.hasNext()) {
                DefaultMasterItem crmCombodata = (DefaultMasterItem) ite.next();
                JSONObject tmpObj = new JSONObject();
                String name = crmCombodata.getValue();
                tmpObj.put("id", crmCombodata.getID());
                tmpObj.put("mainid", crmCombodata.getMainID());
                tmpObj.put("name", name);
                tmpObj.put("itemsequence", crmCombodata.getItemsequence());
                tmpObj.put("percentStage", crmCombodata.getPercentStage());

                if(comboname.equalsIgnoreCase(Constants.Lead_Source)) {
                        String tempcomboname = crmCombodata.getCrmCombomaster().getComboname();
                        if(tempcomboname.equalsIgnoreCase(Constants.Campaign_Source)) {
                            requestParams.put(Constants.Crm_campaignid, crmCombodata.getMainID());
                            hasAccess = crmManagerDAOObj.isCrmCampaignArchived(requestParams);
                        }
                }
                tmpObj.put(Constants.Crm_hasAccess,hasAccess);
                jarr.put(tmpObj);
            }
            
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
       } catch(Exception e) {
          System.out.println(e.getMessage());
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView addEditMasterData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        String result = "";
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String name = request.getParameter("name");
            String comboid = request.getParameter("configid");
            String id = request.getParameter("id");
            String action = request.getParameter("action");
            String percentStage = request.getParameter("percentStage");
            String sequence=request.getParameter("sequence");
            name= name.replaceAll("'", "&#39;");
                int customflag = 0;
                if(request.getParameter("customflag") != null) {
                    customflag = Integer.parseInt(request.getParameter("customflag"));
                }
                if(customflag == 0) {
                    boolean duplicateMasterDataEntryCheck = crmManagerDAOObj.checkForDuplicateEntryInMasterData(name, comboid, companyid, id);

                    if(!duplicateMasterDataEntryCheck) {
                        //Create transaction
                        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                        def.setName("JE_Tx");
                        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
                        TransactionStatus status = txnManager.getTransaction(def);
                        try{
                            HashMap<String, Object> requestParams = new HashMap<String, Object>();
                            requestParams.put("companyid", companyid);
                            requestParams.put("name", name);
                            requestParams.put("configid", comboid);
                            requestParams.put("id", id);
                            requestParams.put("action", action);
                            requestParams.put("sequence", sequence);
                            if(!StringUtil.isNullOrEmpty(percentStage)){
                                requestParams.put("percentStage", percentStage);
                            }else {
                                requestParams.put("percentStage", 1);
                            }

                                if(action.equals("Add")) {
                                    kmsg = crmManagerDAOObj.addMasterData(requestParams);
                                    DefaultMasterItem masterObj = (DefaultMasterItem) kmsg.getEntityList().get(1);
                                    auditTrailDAOObj.insertAuditLog(com.krawler.common.admin.AuditAction.ADMIN_Config_update,
                                        "Master data '"+masterObj.getValue() + "' added for '"+masterObj.getCrmCombomaster().getComboname()+"'.", request, id);
                                } else {
                                    kmsg = crmManagerDAOObj.editMasterData(requestParams);
                                    DefaultMasterItem masterObj = (DefaultMasterItem) kmsg.getEntityList().get(1);
                                    String oldValue = (String) kmsg.getEntityList().get(2);
                                    auditTrailDAOObj.insertAuditLog(com.krawler.common.admin.AuditAction.ADMIN_Config_update,
                                        "Master data '"+ oldValue + "' updated to '"+masterObj.getValue()+"' for '"+masterObj.getCrmCombomaster().getComboname()+"'.", request, id);
                                }

                            result = kmsg.getEntityList().get(0).toString();
                            txnManager.commit(status);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            txnManager.rollback(status);
                        }
                    } else {
                        result = "{success:true,data:{'msg':'<b>"+name+"</b> record is already present in the list.'}}";
                    }

                } else {

                        boolean duplicateMasterDataEntryCheck = fieldManagerDAOobj.checkForDuplicateEntryInMasterData(name, comboid);
                        
                        if(!duplicateMasterDataEntryCheck) {

                            int seq=Integer.parseInt(sequence);
                            if(action.equals("Add")) {
                                kmsg = fieldManagerDAOobj.addCustomComboData(comboid, name, seq);
                                FieldComboData fcd = (FieldComboData) kmsg.getEntityList().get(1);
                                FieldParams fp = (FieldParams) kmsg.getEntityList().get(2);
                                auditTrailDAOObj.insertAuditLog(com.krawler.common.admin.AuditAction.ADMIN_Config_update,
                                        "Custom combo data '"+ fcd.getValue() + "' added for the field '"+ fp.getFieldlabel() +"'.", request, id);
                            } else {
                                kmsg = fieldManagerDAOobj.editCustomComboData(id, name);
                                FieldComboData fcd = (FieldComboData) kmsg.getEntityList().get(1);
                                FieldParams fp = (FieldParams) kmsg.getEntityList().get(2);
                                String oldValue = (String) kmsg.getEntityList().get(3);
                                auditTrailDAOObj.insertAuditLog(com.krawler.common.admin.AuditAction.ADMIN_Config_update,
                                        "Custom combo data '"+ oldValue + "' updated to '"+ fcd.getValue() +"' for the field '"+ fp.getFieldlabel() +"'.", request, id);
                            }
                            result = kmsg.getEntityList().get(0).toString();

                         } else {
                            result = "{success:true,data:{'msg':'<b>"+name+"</b> record is already present in the list.'}}";
                         }
                }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", result);
    }

	// Saving Master Data Sequence;
	public ModelAndView saveMasterDataSequence(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONArray jArr = null;
		JSONObject jobj = new JSONObject();
		JSONObject jtemp = null;
		try {
			String customflag = request.getParameter("customflag");
			jArr = new JSONArray(request.getParameter("jsonOb"));

			Map<String, Integer> map = new HashMap<String, Integer>();
			for (int i = 0; i < jArr.length(); i++) {
				jtemp = jArr.getJSONObject(i);
				map.put(jtemp.getString("id"), jtemp.getInt("seq"));
			}

			crmCommonService.saveMasterDataSequence(map, customflag);

			jobj.put("success", true);
			jobj.put("msg", "Sequence saved successfully");

		} catch (Exception e) {
			try {
				jobj.put("success", false);
				jobj.put("msg", e.getMessage());
			} catch (JSONException je) {
				je.printStackTrace();
			}
		}

		return new ModelAndView("jsonView", "model", jobj.toString());
	}
	
	public ModelAndView getDefaultCaseOwner(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject jobj = new JSONObject();
		try {
			String companyid = sessionHandlerImpl.getCompanyid(request);
					
			String defaultCaseOwner= crmCommonService.getDefaultCaseOwner(companyid);

			jobj.put("companyid", companyid);
			jobj.put("caseownerid", defaultCaseOwner);

		} catch (Exception e) {
				logger.warn(e.getMessage());
				e.printStackTrace();
		}
		return new ModelAndView("jsonView", "model", jobj.toString());
	}
	
	public ModelAndView saveDefaultCaseOwner(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject jobj = new JSONObject();
		try {
			String companyid = sessionHandlerImpl.getCompanyid(request);
			String ownerid = request.getParameter("owner");
			String ownername = request.getParameter("ownername");
			crmCommonService.saveDefaultCaseOwner(companyid , ownerid);
                        auditTrailDAOObj.insertAuditLog(com.krawler.common.admin.AuditAction.ADMIN_Config_update, 
                                "Default Case Owner has been modified to '"+ownername+"' for the company", request, companyid);
			jobj.put("success", true);
			jobj.put("msg", "Default Case owner has been saved successfully");

		} catch (Exception e) {
			try {
				jobj.put("success", false);
				jobj.put("msg", e.getMessage());
			} catch (JSONException je) {
				je.printStackTrace();
				logger.warn(je.getMessage());
			}
			logger.warn(e.getMessage());
		}

		return new ModelAndView("jsonView", "model", jobj.toString());
	}
    
    public ModelAndView deleteMasterData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        String result = "";
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String configid = request.getParameter(CrmCommonConstants.Crm_configid);
            String parentid = request.getParameter(CrmCommonConstants.Crm_parentid);
            String id = request.getParameter(CrmCommonConstants.Crm_id);
            int customflag = 0;
            if(request.getParameter("customflag") != null) {
                customflag = Integer.parseInt(request.getParameter("customflag"));
            }
            if(customflag == 0) {
                //Create transaction
               DefaultTransactionDefinition def = new DefaultTransactionDefinition();
               def.setName("JE_Tx");
               def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
               def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
               TransactionStatus status = txnManager.getTransaction(def);
               try{
                    HashMap<String, Object> requestParams = new HashMap<String, Object>();
                    requestParams.put("companyid", companyid);
                    requestParams.put("id", id);
                    requestParams.put(CrmCommonConstants.Crm_configid, configid);
                    requestParams.put(CrmCommonConstants.Crm_parentid, parentid);

                    kmsg = crmManagerDAOObj.deleteMasterData(requestParams);
                    DefaultMasterItem masterObj = (DefaultMasterItem) kmsg.getEntityList().get(1);
                    auditTrailDAOObj.insertAuditLog(com.krawler.common.admin.AuditAction.ADMIN_Config_update,
                        "Master data '"+masterObj.getValue() + "' deleted for '"+masterObj.getCrmCombomaster().getComboname()+"'.", request, id);
                    
                    result = kmsg.getEntityList().get(0).toString();
                    txnManager.commit(status);
               } catch (Exception e) {
                    System.out.println(e.getMessage());
                    txnManager.rollback(status);
               }
            } else { // to do check with sagar
              //  kmsg = fieldManager.deleteCustomComboData(id, companyid);
                result = kmsg.getEntityList().get(0).toString();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", result);
    }

    public JSONObject getMasterJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmCombomaster obj = (CrmCombomaster) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getMasterid());
                tmpObj.put("name", obj.getComboname());
                tmpObj.put("parentid", obj.getParentid());
                tmpObj.put("customflag", 0);
                tmpObj.put("modulename", "---");
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView getMasterComboData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            HashMap<String, Object> requestParams =  new HashMap<String, Object>();
            kmsg = crmManagerDAOObj.getMaster(requestParams);
            List lst = kmsg.getEntityList();
            jobj = getMasterJson(lst, request, lst.size());

            int customflag = 0;
            if(request.getParameter("customflag") != null) {
                customflag = Integer.parseInt(request.getParameter("customflag"));
            }
            if(customflag == 1) {
                String companyid = sessionHandlerImpl.getCompanyid(request);
                List ls = fieldManagerDAOobj.getCustomComboNames(companyid);
                JSONArray jarr = jobj.getJSONArray("data");
                Iterator ite = ls.iterator();
                while (ite.hasNext()) {
                    Object[] row = (Object[]) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("id", row[0]);
                    tmpObj.put("name", row[1]);
                    tmpObj.put("customflag", 1);
                    int moduleid = Integer.parseInt(row[3].toString());
                    String modulename = "";
                    if(moduleid == 1) {
                        modulename = "Account";
                    }else if(moduleid == 2) {
                        modulename = "Lead";
                    }else if(moduleid == 6) {
                        modulename = "Contact";
                    }else if(moduleid == 5) {
                        modulename = "Opportunity";
                    }else if(moduleid == 4) {
                        modulename = "Product";
                    }else if(moduleid == 3) {
                        modulename = "Case";
                    }
                    tmpObj.put("modulename", modulename);
                    tmpObj.put("maxlength", row[4]);
                    jarr.put(tmpObj);
                }

                jobj.put("success", true);
                jobj.put("data", jarr);
                jobj.put("totalCount", kmsg.getRecordTotalCount()+ls.size());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getCompanyInformation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.companyID");
            filter_params.add(companyid);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);

            kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams);
            jobj = companyDetailsHandler.getCompanyJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            jobj = getCompanyPreferencesFromCmplist(kmsg.getEntityList(), jobj);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    //Company preference
    public ModelAndView getCompanyPreference(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            jobj = getCompanyPreferencesFromCmpid(companyid, jobj);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getAllCompanyDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.companyID");
            filter_params.add(companyid);
            filter_names.add("c.deleted");
            filter_params.add(0);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);

            kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams);
            jobj = companyDetailsHandler.getCompanyJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            jobj = getCompanyPreferencesFromCmplist(kmsg.getEntityList(), jobj);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView setCompanyPref(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String cmpid = sessionHandlerImplObj.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", cmpid);
            if (request.getParameter("category").equalsIgnoreCase("upper")) {
                if (request.getParameter("heirarchypermisssioncampaign") != null) {
                    requestParams.put("heirarchypermisssioncampaign", request.getParameter("heirarchypermisssioncampaign"));
                }
                if (request.getParameter("heirarchypermisssionleads") != null) {
                    requestParams.put("heirarchypermisssionleads", request.getParameter("heirarchypermisssionleads"));
                }
                if (request.getParameter("heirarchypermisssionaccounts") != null) {
                    requestParams.put("heirarchypermisssionaccounts", request.getParameter("heirarchypermisssionaccounts"));
                }
                if (request.getParameter("heirarchypermisssioncontacts") != null) {
                    requestParams.put("heirarchypermisssioncontacts", request.getParameter("heirarchypermisssioncontacts"));
                }
                if (request.getParameter("heirarchypermisssionopportunity") != null) {
                    requestParams.put("heirarchypermisssionopportunity", request.getParameter("heirarchypermisssionopportunity"));
                }
                if (request.getParameter("heirarchypermisssioncases") != null) {
                    requestParams.put("heirarchypermisssioncases", request.getParameter("heirarchypermisssioncases"));
                }
                if (request.getParameter("heirarchypermisssionproduct") != null) {
                    requestParams.put("heirarchypermisssionproduct", request.getParameter("heirarchypermisssionproduct"));
                }
                if (request.getParameter("heirarchypermisssionactivity") != null) {
                    requestParams.put("heirarchypermisssionactivity", request.getParameter("heirarchypermisssionactivity"));
                }
                if (!StringUtil.isNullOrEmpty(request.getParameter("convertedleadeditpermisssion"))) {
                    requestParams.put("convertedleadeditpermisssion", request.getParameter("convertedleadeditpermisssion"));
                }
            } else if(request.getParameter("category").equalsIgnoreCase("notificationtype")) {
                int notifyType = 0;
                if (!StringUtil.isNullOrEmpty(request.getParameter("emailnotification"))) {
                    notifyType = 1;
                }
                requestParams.put("emailnotification", notifyType);
                request.getSession().setAttribute(com.krawler.common.util.Constants.SESSION_NOTIFYON, notifyType >0 ? true : false);
            } else if(request.getParameter("category").equalsIgnoreCase("leadrounting")) {
                int leadrounting = Integer.parseInt(request.getParameter("defaultroutingradio"));
                if (leadrounting > 0) {
                    saveLeadRoutingUsers(request.getParameter("addid"),request.getParameter("delid"),cmpid);
                }
                requestParams.put("leadrounting", leadrounting);
            } else {
                if (!StringUtil.isNullOrEmpty(request.getParameter("companydependentLeadTyperadio"))) {
                    requestParams.put("companydependentLeadTyperadio", request.getParameter("companydependentLeadTyperadio"));
                }
            }
            requestParams.put("category", request.getParameter("category"));
            kmsg = crmManagerDAOObj.setCompanyPref(requestParams);
            String auditMsg = (String) kmsg.getEntityList().get(1);
            if(!StringUtil.isNullOrEmpty(auditMsg)) {
                auditTrailDAOObj.insertAuditLog(com.krawler.common.admin.AuditAction.ADMIN_Config_update, auditMsg, request, cmpid);
            }
            
            JSONObject obj = new JSONObject();
            request.getSession(true).setAttribute("companyPreferences", getCompanyPreferencesFromCmpid(cmpid, obj));
            myjobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public JSONObject getCompanyPreferencesFromCmplist(List companyList, JSONObject jobj) {
        try {
            Iterator ite = companyList.iterator();
            while (ite.hasNext()) {
                Company company =(Company)ite.next();
                jobj = getCompanyPreferencesFromCmpid(company.getCompanyID(), jobj);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    private void saveLeadRoutingUsers (String addId, String delId, String companyid) throws ServiceException{
        try {
            crmManagerDAOObj.deleteLeadRoutingUsers(companyid);
            if(!StringUtil.isNullOrEmpty(addId)) {
                String[] addIds = addId.split(",");
//            crmManagerDAOObj.deleteLeadRoutingUsers(addIds);
                crmManagerDAOObj.addLeadRoutingUsers(addIds);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmManagerController.saveLeadRoutingUsers", ex);
        }
    }
    public JSONObject getCompanyPreferencesFromCmpid(String cmpid, JSONObject obj)
			throws ServiceException {
//        List ll = new ArrayList();
		try {
		    CompanyPreferences cmpPref=null;
            cmpPref = (CompanyPreferences) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CompanyPreferences", cmpid);
            obj = crmManagerCommon.getCompanyPreferencesJSON(cmpPref, obj);
            Company cmp = (Company) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", cmpid);
            obj.put("emailnotification", cmp.getNotificationtype()==0 ? false : true);
//            ll.add(obj);
		} catch (Exception e) {
			throw ServiceException.FAILURE("crmManagerController.getCompanyPreferencesFromCmpid", e);
		}
        return obj;
//		return new KwlReturnObject(true, KwlReturnMsg.S01, "", ll, 0);
	}

    public ModelAndView getUnAssignedLeadRoutingUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
       try{
            JSONArray jArray = new JSONArray();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("pagingFlag", false);
            KwlReturnObject kmsg = crmManagerDAOObj.getUnAssignedLeadRoutingUsers(sessionHandlerImplObj.getCompanyid(request),requestParams);
            List<User> users =  kmsg.getEntityList();
            if (users != null && !users.isEmpty()) {
                for (User obj : users) {
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("fullname", StringUtil.getFullName(obj));
//                    tmpObj.put("role", obj.getRoleID().g);
                    tmpObj.put("userid", obj.getUserID());
                    jArray.put(tmpObj);
                }
            }
            jobj.put("data", jArray);
            jobj.put("count", kmsg.getRecordTotalCount());
       } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getAssignedLeadRoutingUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try{
            JSONArray jArray = new JSONArray();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
            requestParams.put("pagingFlag", false);
            KwlReturnObject kmsg = crmManagerDAOObj.getAssignedLeadRoutingUsers(sessionHandlerImplObj.getCompanyid(request),requestParams);
            List<User> users =  kmsg.getEntityList();
            if (users != null && !users.isEmpty()) {
                for (User obj : users) {
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("fullname", StringUtil.getFullName(obj));
//                    tmpObj.put("role", obj.getRoleID().g);
                    tmpObj.put("userid", obj.getUserID());
                    jArray.put(tmpObj);
                }
            }
            jobj.put("data", jArray);
            jobj.put("count", kmsg.getRecordTotalCount());
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
}
