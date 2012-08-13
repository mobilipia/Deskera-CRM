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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.QuotationDetail;
import com.krawler.crm.database.tables.TargetListTargets;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.quotation.QuotationDAO;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.accountModule.crmAccountHandler;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.caseModule.crmCaseHandler;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.emailMarketing.crmEmailMarketingDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.leadModule.crmLeadHandler;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityHandler;
import com.krawler.spring.crm.productModule.crmProductController;
import com.krawler.spring.crm.productModule.crmProductDAO;
//import com.krawler.spring.importFunctionality.ImportDAO;
import com.krawler.spring.importFunctionality.ImportThreadExecutor;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
/**
 *
 * @author trainee
 */
public class crmCommonHandlerController extends MultiActionController {   

    private HibernateTemplate hibernateTemplate;
    private crmAccountDAO crmAccountDAOObj;
    private crmContactDAO crmContactDAOObj;
    private crmCaseDAO crmCaseDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmLeadDAO crmLeadDAOObj;
//    private ImportDAO importDao;
    private crmProductController crmProductControllerObj;
    private crmProductDAO crmProductDAOObj;
    private crmEmailMarketingDAO crmEmailMarketingDAOObj;
    private fieldManagerDAO fieldManagerDAOobj;
    private HibernateTransactionManager txnManager;
    protected ImportThreadExecutor importThreadExecutor;
    public CRMBackupHandler crmBackupHandler;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private crmCommonDAO crmCommonDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private QuotationDAO quotationDAO;
    
    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }
    
    /**
     * @param KwlCommonTablesDAOObj1
     */
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public void setCrmCommonDAO(crmCommonDAO crmCommonDAOObj) {
		this.crmCommonDAOObj = crmCommonDAOObj;
	}

	/**
     * 
     * @param crmBackupHandler
     */
    public void setCrmBackupHandler(CRMBackupHandler crmBackupHandler) {
        this.crmBackupHandler = crmBackupHandler;
    }

    /**
     * @param importThreadExecutor
     */
    public void setImportThreadExecutor(ImportThreadExecutor importThreadExecutor) {
		this.importThreadExecutor = importThreadExecutor;
	}
    
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    public void setcrmEmailMarketingDAO(crmEmailMarketingDAO crmEmailMarketingDAOObj1) {
        this.crmEmailMarketingDAOObj = crmEmailMarketingDAOObj1;
    }
    public crmProductDAO getcrmProductDAO(){
        return crmProductDAOObj;
    }
    public void setcrmProductDAO(crmProductDAO crmProductDAOObj1) {
        this.crmProductDAOObj = crmProductDAOObj1;
    }
//    public void setimportDAO(ImportDAO importDao) {
//        this.importDao = importDao;
//    }
    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }
    public void setcrmProductController(crmProductController crmProductControllerObj){
        this.crmProductControllerObj=crmProductControllerObj;
    }
    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }

    public void setcrmCaseDAO(crmCaseDAO crmCaseDAOObj1) {
        this.crmCaseDAOObj = crmCaseDAOObj1;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }


    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
    
    
    public void setQuotationDAO(QuotationDAO quotationDAO) {
		this.quotationDAO = quotationDAO;
	}

	public ModelAndView singleClickBackup(HttpServletRequest request,HttpServletResponse response ) throws ServletException{
        JSONObject jobj1 = new JSONObject();
        String view = "jsonView";
        try {
            jobj1.put("success", false);
            if(sessionHandlerImpl.getRole(request).equals(Constants.COMPANY_ADMIN)) {
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
                String userid = sessionHandlerImpl.getUserid(request);
                String companyid = sessionHandlerImpl.getCompanyid(request);
                String currencyid = sessionHandlerImpl.getCurrencyID(request);
                String companyname = sessionHandlerImpl.getCompanyName(request);

                boolean product_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
                boolean editconvertedlead =crmLeadHandler.editConvertedLead(request);
                boolean lead_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
                boolean account_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
                boolean contact_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
                boolean opp_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
                boolean case_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Case");
                boolean campaign_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
                boolean activity_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");
                String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
                String timeFormat = sessionHandlerImpl.getUserTimeFormat(request);

                requestParams.put("dateFormat", dateFormat);
                requestParams.put("userid", userid);
                requestParams.put("companyid", companyid);
                requestParams.put("currencyid", currencyid);
                requestParams.put("companyname", companyname);
                requestParams.put("product_heirarchyPerm", product_heirarchyPerm);
                requestParams.put("editconvertedlead", editconvertedlead);
                requestParams.put("lead_heirarchyPerm", lead_heirarchyPerm);
                requestParams.put("account_heirarchyPerm", account_heirarchyPerm);
                requestParams.put("contact_heirarchyPerm", contact_heirarchyPerm);
                requestParams.put("opp_heirarchyPerm", opp_heirarchyPerm);
                requestParams.put("case_heirarchyPerm", case_heirarchyPerm);
                requestParams.put("campaign_heirarchyPerm", campaign_heirarchyPerm);
                requestParams.put("activity_heirarchyPerm", activity_heirarchyPerm);
                requestParams.put("timeZoneDiff", timeZoneDiff);
                requestParams.put("timeFormat", timeFormat);
                requestParams.put("sysemailid", sessionHandlerImpl.getSystemEmailId(request));
                requestParams.put("partnarname", sessionHandlerImpl.getPartnerName(request));
                User user = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
                requestParams.put("userEmailID", user.getEmailID());
                StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);

                crmBackupHandler.setRequestParams(requestParams);
                crmBackupHandler.setUsersList(usersList);
                importThreadExecutor.startThread(crmBackupHandler);
                jobj1.put("success", true);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj1.toString());
    }

    public ModelAndView deleteCustomComboData(HttpServletRequest request,HttpServletResponse response ) throws ServletException{
        KwlReturnObject kmsg = null;
        String result = "{success:true,  msg:'Selected master data has been deleted successfully.'}";
        String errorresult = "{success:false, msg:'Error while deleting master data.'}";
        String failureResult = "{success:false, msg:'Selected master data can not be deleted as it is already in use.'}";
        boolean deleteAction = true;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String comboid = request.getParameter("configid");
            String parentid = request.getParameter("parentid");
            String id = request.getParameter("id");
            int customflag = 0;
            if(request.getParameter("customflag") != null) {
                customflag = Integer.parseInt(request.getParameter("customflag"));
            }
            if(customflag != 0) {
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names", Arrays.asList("dh.pojoheadername"));
                requestParams.put("filter_values", Arrays.asList(comboid));
                kmsg = fieldManagerDAOobj.getDefaultHeader(requestParams);
                List defaultHeaders = kmsg.getEntityList();
                if(defaultHeaders.size()> 0) {
                    Iterator ite = defaultHeaders.iterator();
                    while(ite.hasNext()) {
                        DefaultHeader defaultHeader = (DefaultHeader) ite.next();
                        String moduleid = defaultHeader.getModule().getId();
                        Integer xtype = Integer.parseInt(defaultHeader.getXtype());
                        String columnname = defaultHeader.getDbcolumnname().toLowerCase();
                        String like="";
                        String searchid = id;
                        if(xtype==7){
                            like = "LIKE";
                            searchid="%"+id+"%";
                        }
                        if(moduleid.equals(com.krawler.common.util.Constants.MODULEID_LEAD)) {
                            requestParams.clear();
                            requestParams.put("filter_names", Arrays.asList("lead.deleteflag",like+columnname));
                            requestParams.put("filter_values", Arrays.asList(0,searchid));
                            List li = crmLeadDAOObj.getCrmLeadCustomData(requestParams).getEntityList();
                            if(li.size()>0) {
                                result = failureResult;
                                deleteAction = false;
                                break;
                            }
                        } else if(moduleid.equals(com.krawler.common.util.Constants.MODULEID_CONTACT)) {
                            requestParams.clear();
                            requestParams.put("filter_names", Arrays.asList("contact.deleteflag",like+columnname));
                            requestParams.put("filter_values", Arrays.asList(0,searchid));
                            List li = crmContactDAOObj.getCrmContactCustomData(requestParams).getEntityList();
                            if(li.size()>0) {
                                result = failureResult;
                                deleteAction = false;
                                break;
                            }
                        } else if(moduleid.equals(com.krawler.common.util.Constants.MODULEID_ACCOUNT)) {
                            requestParams.clear();
                            requestParams.put("filter_names", Arrays.asList("account.deleteflag",like+columnname));
                            requestParams.put("filter_values", Arrays.asList(0,searchid));
                            List li = crmAccountDAOObj.getCrmAccountCustomData(requestParams).getEntityList();
                            if(li.size()>0) {
                                result = failureResult;
                                deleteAction = false;
                                break;
                            }
                        } else if(moduleid.equals(com.krawler.common.util.Constants.MODULEID_OPPORTUNITY)) {
                            requestParams.clear();
                            requestParams.put("filter_names", Arrays.asList("opportunity.deleteflag",like+columnname));
                            requestParams.put("filter_values", Arrays.asList(0,searchid));
                            List li = crmOpportunityDAOObj.getCrmOpportunityCustomData(requestParams).getEntityList();
                            if(li.size()>0) {
                                result = failureResult;
                                deleteAction = false;
                                break;
                            }
                        } else if(moduleid.equals(com.krawler.common.util.Constants.MODULEID_CASE)) {
                            requestParams.clear();
                            requestParams.put("filter_names", Arrays.asList("deleteflag",like+Constants.Crm_case_pojo_ref+"."+columnname));
                            requestParams.put("filter_values", Arrays.asList(0,searchid));
                            List li = crmCaseDAOObj.getCrmCaseCustomData(requestParams).getEntityList();
                            if(li.size()>0) {
                                result = failureResult;
                                deleteAction = false;
                                break;
                            }
                        } else if(moduleid.equals(com.krawler.common.util.Constants.MODULEID_PRODUCT)) {
                            requestParams.clear();
                            requestParams.put("filter_names", Arrays.asList("product.deleteflag",like+columnname));
                            requestParams.put("filter_values", Arrays.asList(0,searchid));
                            List li = crmProductDAOObj.getCrmProductCustomData(requestParams).getEntityList();
                            if(li.size()>0) {
                                result = failureResult;
                                deleteAction = false;
                                break;
                            }
                        }
                    }
                }
                if(deleteAction) {
                    requestParams.clear();
                    requestParams.put("filter_names", Arrays.asList("id","fieldid"));
                    requestParams.put("filter_values", Arrays.asList(id,comboid));
                    kmsg = fieldManagerDAOobj.deleteCustomCombodata(requestParams);
                    String comboname = (String) kmsg.getEntityList().get(1);
                    String combovalue = (String) kmsg.getEntityList().get(0);
                    auditTrailDAOObj.insertAuditLog(com.krawler.common.admin.AuditAction.ADMIN_Config_update,
                            "Custom combo data '"+ combovalue + "' deleted for the field '"+ comboname +"'.", request, id);
                    txnManager.commit(status);
                }else{
                    txnManager.rollback(status);
                }
            }
        } catch(Exception ex) {
            result = errorresult;
            logger.warn(ex.getMessage(),ex);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", result);
    }
    
    public ModelAndView saveAccounts(HttpServletRequest request,HttpServletResponse response ) throws ServletException{
        JSONObject myjobj = new JSONObject();
        try{
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String id = jobj.getString("accountid");
            String accountId =  "'"+id+"'";
            StringBuffer moduleName= new StringBuffer("");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            Hashtable ht = checkAccountReferences(accountId,companyid,moduleName);
            if(ht.containsKey(id)){
                myjobj.put("revert", true);
                myjobj.put("moduleName", ht.get(id));
            }else{
                // since no references in other modules are obtained let invalid record be saved in db
                getServletContext().getRequestDispatcher(Constants.URLS.get("AccountSave")).forward(request, response);
            }
        }catch(Exception ex){
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    public ModelAndView saveProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        try {
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String id = jobj.getString("productid");
            String productId =  "'"+id+"'";
            StringBuffer moduleName = new StringBuffer("");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            Hashtable ht = checkProductReferences(productId, companyid, moduleName);
            if (ht.containsKey(id)) {
                myjobj.put("revert", true);
                myjobj.put("moduleName", ht.get(id));
            } else {
                // since no references in other modules are obtained let invalid record be saved in db
                getServletContext().getRequestDispatcher(Constants.URLS.get("ProductSave")).forward(request, response);
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    
	public ModelAndView saveCampaigns(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject myjobj = new JSONObject();
		try {
			JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
			String id = jobj.getString("campaignid");
			String campaignId = "'" + id + "'";
			StringBuffer moduleName = new StringBuffer("");
			String companyid = sessionHandlerImpl.getCompanyid(request);
			Hashtable ht = checkCampaignReferences(campaignId, companyid, moduleName);
			if (ht.containsKey(id)) {
				myjobj.put("revert", true);
				myjobj.put("moduleName", ht.get(id));
			} else {
				// since no references in other modules are obtained let invalid
				// record be saved in db
				getServletContext().getRequestDispatcher(Constants.URLS.get("CampaignSave")).forward(request, response);
			}
		} catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
		}
		return new ModelAndView("jsonView", "model", myjobj.toString());
	}
   
    public ModelAndView deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        try {
            myjobj.put("success", false);
            JSONObject jobj ;
            CrmProduct crmProduct;
            JSONArray deletejarr =new JSONArray() ;
            JSONArray failDeletejarr =new JSONArray() ;
            StringBuffer moduleName ;
            String companyid = sessionHandlerImpl.getCompanyid(request);
            JSONArray jarr = new JSONArray("[" +request.getParameter("jsondata")+ "]");
            String id;
            String productId = "";
            String productIds = "";
            for (int i = 0; i < jarr.length(); i++) {
                jobj = jarr.getJSONObject(i);
                id = jobj.getString("productid");
                productId += "'"+id+"',";
                productIds += id+",";
            }
            productId = productId.substring(0, productId.length()-1);
            productIds = productIds.substring(0, productIds.length()-1);
            
            moduleName = new StringBuffer("");
            Hashtable ht =  checkProductReferences(productId, companyid, moduleName);
            String [] productArray = productIds.split(",");
            for(int j =0; j< productArray.length; j++) {
                jobj = new JSONObject();
                id = productArray[j];
                if(ht.containsKey(id)) {
                    jobj.put("moduleName", ht.get(id));
                    crmProduct = (CrmProduct) hibernateTemplate.get(CrmProduct.class, id);
                    jobj.put("name", crmProduct.getProductname());
                    failDeletejarr.put(jobj);
                } else {
                    jobj.put("productid", id);
                    deletejarr.put(jobj);
                }
            }
                
            if(deletejarr.length() > 0){
                // deletejarr -> records which can be deleted since there are no references of them in other modules
                // failDeletejarr -> records which could not be deleted since there are references of them in other modules

               request.setAttribute("deletejarr", deletejarr);
               request.setAttribute("failDelete", failDeletejarr);
               getServletContext().getRequestDispatcher(Constants.URLS.get("ProductDelete")).forward(request, response);
            }else{
                myjobj.put("successDeleteArr", deletejarr);
                myjobj.put("failDelete", failDeletejarr);
            }
            myjobj.put("success", true);
                
        } catch (Exception ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    public ModelAndView deleteAccount(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        try {
            myjobj.put("success", false);
            JSONObject jobj ;
            CrmAccount crmAccount;
            JSONArray deletejarr =new JSONArray() ;
            JSONArray failDeletejarr =new JSONArray() ;
            StringBuffer moduleName ;
            String companyid = sessionHandlerImpl.getCompanyid(request);
            JSONArray jarr = new JSONArray("[" +request.getParameter("jsondata")+ "]");
            String id;
            String accountId = "";
            String accountIds = "";
            for (int i = 0; i < jarr.length(); i++) {
                jobj = jarr.getJSONObject(i);
                id = jobj.getString("accid");
                accountId += "'"+id+"',";
                accountIds += id+",";
            }
            accountId = accountId.substring(0, accountId.length()-1);
            accountIds = accountIds.substring(0, accountIds.length()-1);

            moduleName = new StringBuffer("");
            Hashtable ht =  checkAccountReferences(accountId, companyid, moduleName);
            String [] accountArray = accountIds.split(",");
            for(int j =0; j< accountArray.length; j++) {
                jobj = new JSONObject();
                id = accountArray[j];
                if(ht.containsKey(id)) {
                    jobj.put("moduleName", ht.get(id));
                    crmAccount = (CrmAccount) hibernateTemplate.get(CrmAccount.class, id);
                    jobj.put("name", crmAccount.getAccountname());
                    failDeletejarr.put(jobj);
                } else {
                    jobj.put("accid", id);
                    deletejarr.put(jobj);
                }
            }
            if(deletejarr.length() > 0){
                // deletejarr -> records which can be deleted since there are no references of them in other modules
                // failDeletejarr -> records which could not be deleted since there are references of them in other modules

               request.setAttribute("deletejarr", deletejarr);
               request.setAttribute("failDelete", failDeletejarr);
               getServletContext().getRequestDispatcher(Constants.URLS.get("AccountDelete")).forward(request, response);
            }else{
                myjobj.put("successDeleteArr", deletejarr);
                myjobj.put("failDelete", failDeletejarr);
            }
            myjobj.put("success", true);

        } catch (Exception ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    
    
    
    
	public ModelAndView deleteCampaign(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject myjobj = new JSONObject();
		try {
			myjobj.put("success", false);
			JSONObject jobj;
			CrmCampaign crmCampaign;
			JSONArray deletejarr = new JSONArray();
			JSONArray failDeletejarr = new JSONArray();
			StringBuffer moduleName;
			String companyid = sessionHandlerImpl.getCompanyid(request);
			JSONArray jarr = new JSONArray("[" + request.getParameter("jsondata") + "]");
			String id;
			String campaignId = "";
			String campaignIds = "";
			for (int i = 0; i < jarr.length(); i++) {
				jobj = jarr.getJSONObject(i);
				id = jobj.getString("campaignid");
				campaignId += "'" + id + "',";
				campaignIds += id + ",";
			}
			campaignId = campaignId.substring(0, campaignId.length() - 1);
			campaignIds = campaignIds.substring(0, campaignIds.length() - 1);

			moduleName = new StringBuffer("");
			Hashtable ht = checkCampaignReferences(campaignId, companyid, moduleName);
			String[] campaignArray = campaignIds.split(",");
			for (int j = 0; j < campaignArray.length; j++) {
				jobj = new JSONObject();
				id = campaignArray[j];
				if (ht.containsKey(id)) {
					jobj.put("moduleName", ht.get(id));
					crmCampaign = (CrmCampaign) hibernateTemplate.get(CrmCampaign.class, id);
					jobj.put("name", crmCampaign.getCampaignname());
					failDeletejarr.put(jobj);
				} else {
					jobj.put("campaignid", id);
					deletejarr.put(jobj);
				}
			}
			if (deletejarr.length() > 0) {
				// deletejarr -> records which can be deleted since there are no
				// references of them in other modules
				// failDeletejarr -> records which could not be deleted since
				// there are references of them in other modules

				request.setAttribute("deletejarr", deletejarr);
				request.setAttribute("failDelete", failDeletejarr);
				getServletContext().getRequestDispatcher(Constants.URLS.get("CampaignDelete")).forward(request, response);
			} else {
				myjobj.put("successDeleteArr", deletejarr);
				myjobj.put("failDelete", failDeletejarr);
			}
			myjobj.put("success", true);

		} catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
		}
		return new ModelAndView("jsonView", "model", myjobj.toString());
	}
    public ModelAndView saveContacts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        try {
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String id = jobj.getString("contactid");
            String contactsId =  "'"+id+"'";
            StringBuffer moduleName = new StringBuffer("");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            Hashtable ht = checkContactReferences(contactsId, companyid, moduleName);
            if (ht.containsKey(id)) {
                myjobj.put("revert", true);
                myjobj.put("moduleName", ht.get(id));
            } else {
                // since no references in other modules are obtained let invalid record be saved in db
                getServletContext().getRequestDispatcher(Constants.URLS.get("ContactSave")).forward(request, response);
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView deleteContact(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        try {
            myjobj.put("success", false);
            JSONObject jobj ;
            CrmContact crmContact;
            JSONArray deletejarr =new JSONArray() ;
            JSONArray failDeletejarr =new JSONArray() ;
            StringBuffer moduleName ;            String companyid = sessionHandlerImpl.getCompanyid(request);
            JSONArray jarr = new JSONArray("[" +request.getParameter("jsondata")+ "]");
            String id;
            String contactsId = "";
            String contactsIds = "";
            for (int i = 0; i < jarr.length(); i++) {
                jobj = jarr.getJSONObject(i);
                id = jobj.getString("contactid");
                contactsId += "'"+id+"',";
                contactsIds += id+",";
            }
            contactsId = contactsId.substring(0, contactsId.length()-1);
            contactsIds = contactsIds.substring(0, contactsIds.length()-1);
            
            moduleName = new StringBuffer("");
            Hashtable ht = checkContactReferences(contactsId, companyid, moduleName);
            String [] contactArray = contactsIds.split(",");
            for(int j =0; j< contactArray.length; j++) {
                jobj = new JSONObject();
                id = contactArray[j];
                if(ht.containsKey(id)) {
                    jobj.put("moduleName", ht.get(id));
                    crmContact = (CrmContact) hibernateTemplate.get(CrmContact.class, id);
                    jobj.put("name", (StringUtil.checkForNull(crmContact.getFirstname()) + " " + StringUtil.checkForNull(crmContact.getLastname())).trim());
                    failDeletejarr.put(jobj);
                } else {
                    jobj.put("contactid", id);
                    deletejarr.put(jobj);
                }
            }
            if(deletejarr.length() > 0){
                // deletejarr -> records which can be deleted since there are no references of them in other modules
                // failDeletejarr -> records which could not be deleted since there are references of them in other modules
               request.setAttribute("deletejarr", deletejarr);
               request.setAttribute("failDelete", failDeletejarr);
               getServletContext().getRequestDispatcher(Constants.URLS.get("ContactDelete")).forward(request, response);
            }else{
                myjobj.put("successDeleteArr", deletejarr);
                myjobj.put("failDelete", failDeletejarr);
            }

            myjobj.put("success", true);

        } catch (Exception ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    
    
    //checkCampaignReferences in lead , Opportunity and Contact
	public Hashtable checkCampaignReferences(String campaignid, String companyid, StringBuffer moduleName) throws ServiceException {
		Hashtable hashtable = new Hashtable();
		String id = "";

		List<DefaultMasterItem> leadsourceid = crmCommonDAOObj.getLeadSourceId(campaignid);
		for (int i = 0; i < leadsourceid.size(); i++) {
			id += "'" + leadsourceid.get(i).getID() + "',";
		}
		if (!StringUtil.isNullOrEmpty(id)) {
			id = id.substring(0, id.length() - 1);

			KwlReturnObject kmsg = null;
			StringBuffer usersList = new StringBuffer("");
			ArrayList filter_names = new ArrayList();
			ArrayList filter_params = new ArrayList();
			filter_names.add("c.deleteflag");
			filter_params.add(0);
			filter_names.add("c.company.companyID");
			filter_params.add(companyid);
			filter_names.add("INc.leadsourceID");
			filter_params.add(id);
			//
			HashMap<String, Object> requestParams = new HashMap<String, Object>();
			requestParams.put("filter_names", filter_names);
			requestParams.put("filter_params", filter_params);

			String modName = "Lead";
			kmsg = crmLeadDAOObj.getLeads(requestParams);

			if (kmsg.getEntityList().size() > 0) {
				List ls = kmsg.getEntityList();
				Iterator ite = ls.iterator();
				while (ite.hasNext()) {
					CrmLead leadObj = (CrmLead) ite.next();
					String name = null;
					try {
						name = StringUtil.getFullName(leadObj.getFirstname(), leadObj.getLastname());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					updateHashTable(hashtable, leadObj.getCrmCombodataByLeadsourceid().getCrmCombodata().getValueid(), modName + "(" + name + ")");
				}
			}
			requestParams.clear();
			filter_names.clear();
			filter_params.clear();
			filter_names.add("c.deleteflag");
			filter_params.add(0);
			filter_names.add("c.company.companyID");
			filter_params.add(companyid);
			filter_names.add("INc.leadsourceID");
			filter_params.add(id);
			requestParams.put("filter_names", filter_names);
			requestParams.put("filter_params", filter_params);
			modName = "Opportunity";
			kmsg = crmOpportunityDAOObj.getOpportunities(requestParams);
			if (kmsg.getEntityList().size() > 0) {
				List ls = kmsg.getEntityList();
				Iterator ite = ls.iterator();
				while (ite.hasNext()) {
					CrmOpportunity oppObj = (CrmOpportunity) ite.next();
					updateHashTable(hashtable, oppObj.getCrmCombodataByLeadsourceid().getCrmCombodata().getValueid(), modName + "(" + oppObj.getOppname() + ")");
				}
			}

			requestParams.clear();
			filter_names.clear();
			filter_params.clear();
			filter_names.add("c.deleteflag");
			filter_params.add(0);
			filter_names.add("c.company.companyID");
			filter_params.add(companyid);
			filter_names.add("INc.leadsourceID");
			filter_params.add(id);
			requestParams.put("filter_names", filter_names);
			requestParams.put("filter_params", filter_params);
			modName = "Contact";
			kmsg = crmContactDAOObj.getContacts(requestParams);
			if (kmsg.getEntityList().size() > 0) {
				List ls = kmsg.getEntityList();
				Iterator ite = ls.iterator();
				while (ite.hasNext()) {
					CrmContact contactObj = (CrmContact) ite.next();
					updateHashTable(hashtable, contactObj.getCrmCombodataByLeadsourceid().getCrmCombodata().getValueid(), modName + "(" + (StringUtil.checkForNull(contactObj.getFirstname()) + " " + StringUtil.checkForNull(contactObj.getLastname())).trim() + ")");
				}
			}

		}

		return hashtable;
	}
    
    //checkContactReferences in Case TargetList
    public Hashtable checkContactReferences(String contactid, String companyid, StringBuffer moduleName) throws ServiceException {
        Hashtable hashtable  = new Hashtable();
        
        KwlReturnObject kmsg = null;
        StringBuffer usersList = new StringBuffer("");
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("c.deleteflag");
        filter_params.add(0);
        filter_names.add("c.company.companyID");
        filter_params.add(companyid);
        filter_names.add("INc.crmContact.contactid");//INc.crmContact.contactid
        filter_params.add(contactid);
        //
        HashMap<String, Object> requestParams = new HashMap<String, Object>();

        String modName = "Case";
        requestParams.put("heirarchyPerm", true);
        requestParams.put("export", true);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        requestParams.put("userlist", usersList);
        kmsg = crmCaseDAOObj.getCases(requestParams);
        if (kmsg.getEntityList().size() > 0) {
            List ls = kmsg.getEntityList();
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                CrmCase caseObj = (CrmCase) ite.next();
                String casesubject = StringUtil.isNullOrEmpty(caseObj.getSubject())?"":"("+caseObj.getSubject()+")";
                hashtable.put(caseObj.getCrmContact().getContactid(), modName+casesubject);
            }
        }

        requestParams.clear();
        filter_names.clear();
        filter_params.clear();

        filter_names.add("INtargetlisttargets.relatedid");
        filter_params.add(contactid);
        filter_names.add("targetlisttargets.targetlistid.deleted");
        filter_params.add(0);
        filter_names.add("targetlisttargets.targetlistid.creator.company.companyID");
        filter_params.add(companyid);
        filter_names.add("targetlisttargets.targetlistid.saveflag");
        filter_params.add(1);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        requestParams.put("companyid", companyid);
        kmsg = crmEmailMarketingDAOObj.getTargetListTargets(requestParams);
        modName = "Target List";
        if (kmsg.getEntityList().size() > 0) {
            List ls = kmsg.getEntityList();
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                TargetListTargets tlObj = (TargetListTargets) ite.next();
                String key = tlObj.getRelatedid();
                updateHashTable(hashtable,key,modName+"("+tlObj.getTargetlistid().getName()+")");
            }
        }
        return hashtable;
    }
    // checkAccountReferences in Case Opportunity Contact
    public Hashtable checkAccountReferences(String accountid, String companyid, StringBuffer moduleName) throws ServiceException {
        Hashtable hashtable  = new Hashtable();
        KwlReturnObject kmsg = null;
        StringBuffer usersList = new StringBuffer("");
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("c.deleteflag");
        filter_params.add(0);
        filter_names.add("c.company.companyID");
        filter_params.add(companyid);
        filter_names.add("INc.crmAccount.accountid");
        filter_params.add(accountid);

        HashMap<String, Object> requestParams = new HashMap<String, Object>();

        String modName = "Case";
        requestParams.put("heirarchyPerm", true); // irrespective of heirarchy
        requestParams.put("export", true); // to avoid paging query
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        requestParams.put("userlist", usersList);
        kmsg = crmCaseDAOObj.getCases(requestParams);
        if (kmsg.getEntityList().size() > 0) {
            List ls = kmsg.getEntityList();
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                CrmCase caseObj = (CrmCase) ite.next();
                hashtable.put(caseObj.getCrmAccount().getAccountid(), modName+"("+caseObj.getCasename()+")");
            }
        }
        
        modName = "Opportunity";
        filter_names.clear();
        filter_params.clear();
        filter_names.add("c.deleteflag");
        filter_params.add(0);
        filter_names.add("c.company.companyID");
        filter_params.add(companyid);
        filter_names.add("INc.crmAccount.accountid");
        filter_params.add(accountid);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        kmsg = crmOpportunityDAOObj.getOpportunities(requestParams);
        if (kmsg.getEntityList().size() > 0) {
            List ls = kmsg.getEntityList();
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                CrmOpportunity oppObj = (CrmOpportunity) ite.next();
                String accid = oppObj.getCrmAccount().getAccountid();
                updateHashTable(hashtable,accid,modName+"("+oppObj.getOppname()+")");
            }
        }

        modName = "Contact";
        filter_names.clear();
        filter_params.clear();
        filter_names.add("c.deleteflag");
        filter_params.add(0);
        filter_names.add("c.company.companyID");
        filter_params.add(companyid);
        filter_names.add("INc.crmAccount.accountid");
        filter_params.add(accountid);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        kmsg = crmContactDAOObj.getContacts(requestParams);
        if (kmsg.getEntityList().size() > 0) {
            List ls = kmsg.getEntityList();
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                CrmContact contactObj = (CrmContact) ite.next();
                String accid = contactObj.getCrmAccount().getAccountid();
                updateHashTable(hashtable,accid,modName+"("+(StringUtil.checkForNull(contactObj.getFirstname()) + " " + StringUtil.checkForNull(contactObj.getLastname())).trim()+")");
            }
        }
        return hashtable;
    }

    //  checkProductReferences in  Lead Opportunity Account Case 
    public Hashtable checkProductReferences(String productid, String companyid, StringBuffer moduleName) throws ServiceException {
        Hashtable hashtable  = new Hashtable();
        KwlReturnObject kmsg = null;
        StringBuffer usersList = new StringBuffer("");
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("c.deleteflag");
        filter_params.add(0);
        filter_names.add("c.company.companyID");
        filter_params.add(companyid);
        filter_names.add("INp.productId.productid");
        filter_params.add(productid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);

        String modName = "Lead";
        kmsg = crmLeadDAOObj.getLeads(requestParams);

        if (kmsg.getEntityList().size() > 0) {
            List ls = kmsg.getEntityList();
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                CrmLead leadObj = (CrmLead) ite.next();
                String[] productInfo=crmLeadHandler.getLeadProducts(crmLeadDAOObj, leadObj.getLeadid());
                String prodid[] = productInfo[0].split(",");
                for(int i=0; i<prodid.length; i++) {
                    updateHashTable(hashtable,prodid[i],modName+"("+leadObj.getLastname()+")");
                }
            }
        }

        modName = "Account";
        filter_names.clear();filter_params.clear();
        filter_names.add("c.deleteflag");
        filter_params.add(0);
        filter_names.add("c.company.companyID");
        filter_params.add(companyid);
        filter_names.add("INp.productId.productid");
        filter_params.add(productid);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        kmsg = crmAccountDAOObj.getAccounts(requestParams);
        if (kmsg.getEntityList().size() > 0) {
            List ls = kmsg.getEntityList();
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                CrmAccount accObj = (CrmAccount) ite.next();
                String[] productInfo=crmAccountHandler.getAccountProducts(crmAccountDAOObj, accObj.getAccountid());
                String prodid[] = productInfo[0].split(",");
                for(int i=0; i<prodid.length; i++) {
                    updateHashTable(hashtable,prodid[i],modName+"("+accObj.getAccountname()+")");
                }
            }
        }

        modName = "Case";
        filter_names.clear();filter_params.clear();
        filter_names.add("c.deleteflag");
        filter_params.add(0);
        filter_names.add("c.company.companyID");
        filter_params.add(companyid);
        filter_names.add("INp.productId.productid");
        filter_params.add(productid);
        requestParams.put("heirarchyPerm", true);// irrespective of hierarchy
        requestParams.put("export", true); // to avoid Paging Query
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        requestParams.put("userlist", usersList);
        kmsg = crmCaseDAOObj.getCases(requestParams);
        if (kmsg.getEntityList().size() > 0) {
            List ls = kmsg.getEntityList();
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                CrmCase caseObj = (CrmCase) ite.next();

                String[] productInfo=crmCaseHandler.getCaseProducts(crmCaseDAOObj, caseObj.getCaseid());
                String prodid[] = productInfo[0].split(",");
                for(int i=0; i<prodid.length; i++) {
                    updateHashTable(hashtable,prodid[i],modName+"("+caseObj.getSubject()+")");
                   
                }
            }
        }

        requestParams.clear();
        filter_params.clear();
        filter_names.clear();
        filter_names.add("c.deleteflag");
        filter_params.add(0);
        filter_names.add("c.company.companyID");
        filter_params.add(companyid);
        filter_names.add("INp.productId.productid");
        filter_params.add(productid);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        modName = "Opportunity";
        kmsg = crmOpportunityDAOObj.getOpportunities(requestParams);
        if (kmsg.getEntityList().size() > 0) {
            List ls = kmsg.getEntityList();
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                CrmOpportunity oppObj = (CrmOpportunity) ite.next();
                String[] productInfo=crmOpportunityHandler.getOpportunityProducts(oppObj);
                String[] prodid = productInfo[0].split(",");
                for(int i=0; i<prodid.length; i++) {
                    updateHashTable(hashtable,prodid[i],modName+"("+oppObj.getOppname()+")");
                }
            }
        }
        requestParams.clear();
        filter_params.clear();
        filter_names.clear();
        filter_names.add("INp.productid");
        filter_params.add(productid);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        modName = "Quotation";
        kmsg = quotationDAO.getQuotation(requestParams);
        if (kmsg.getEntityList().size() > 0) {
            List ls = kmsg.getEntityList();
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
            	QuotationDetail quotationDetail = (QuotationDetail) ite.next();
            	String productId = quotationDetail.getProduct().getProductid();
               
                    updateHashTable(hashtable,productId,modName+"("+quotationDetail.getQuotation().getquotationNumber()+")");
            }
        }
        return hashtable;
    }

    public void updateHashTable(Hashtable hashtable, String key, String modName) {
        if(hashtable.containsKey(key)) {
            String oldModName = hashtable.get(key).toString();
            if(!oldModName.contains(modName)) {
                StringBuffer moduleNames = new StringBuffer();
                moduleNames.append(oldModName);
                moduleNames.append(", ");
                moduleNames.append(modName);
                hashtable.put(key, moduleNames);
            }
        } else {
            hashtable.put(key, modName);
        }
    }
}
