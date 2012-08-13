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
package com.krawler.crm.quotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.google.gdata.util.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.Quotation;
import com.krawler.crm.database.tables.QuotationDetail;
import com.krawler.crm.database.tables.Tax;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.opportunityModule.crmOpportunityHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class TaxController extends MultiActionController{
	
	private String successView;
    private HibernateTransactionManager txnManager;
    private TaxDAO taxDAO;
    
	public void setSuccessView(String successView) {
		this.successView = successView;
	}
	public void setTxnManager(HibernateTransactionManager txnManager) {
		this.txnManager = txnManager;
	}
	
	public void setTaxDAO(TaxDAO taxDAO) {
		this.taxDAO = taxDAO;
	}
	public ModelAndView getTax(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj=new JSONObject();
        String msg = "";
        KwlReturnObject kmsg = null;
        boolean issuccess = false;
        try {
            String companyId = sessionHandlerImpl.getCompanyid(request);
            kmsg=taxDAO.getTax(companyId);
            JSONArray dataJArr=getTaxJSON(kmsg.getEntityList());
            jobj.put( "data",dataJArr);
            jobj.put( "count",kmsg.getRecordTotalCount());
            jobj.put( "success",true);
           
   
        } catch (Exception ex) {
        	 try {
				jobj.put( "success",false);
			} catch (JSONException e) {
				logger.warn("TaxController: "+e.getMessage(), e);
			}
			logger.warn("TaxController: "+ex.getMessage(), ex);
        } 
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
	
	 private JSONArray getTaxJSON ( List<Tax> ll) {
		 JSONArray jArr = new JSONArray();
	        try {
	            
	        	for (Tax obj : ll) {
	        		JSONObject jobj = new JSONObject();
	        		jobj.put("taxid", obj.getId());
	        		jobj.put("taxname", obj.getName());
	        		jobj.put("percent", obj.getPercent());
	        		//jobj.put("applydate", obj.getApplyDate());
	        		//jobj.put("taxcode", obj.getTaxCode());
	        		jArr.put(jobj);
	                
	               
	            }
	        	
	        } catch (Exception e) {
	        	logger.warn(e.getMessage(), e);
	        }
	        return jArr;
	    }
	 
	 public ModelAndView saveTax(HttpServletRequest request, HttpServletResponse response) {
	        JSONObject jobj=new JSONObject();
	        String msg="";
	        boolean issuccess = false;

	        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
	        def.setName("Tax_Tx");
	        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

	        TransactionStatus status = txnManager.getTransaction(def);
	        try {
	            saveTax(request);
	            issuccess = true;
	            msg = "Tax details updated successfully";
	            txnManager.commit(status);
	        } catch (SessionExpiredException ex) {
	            txnManager.rollback(status);
	            msg = ex.getMessage();
	            logger.error("TaxController :"+ex.getMessage());
	        } catch (Exception ex) {
	            txnManager.rollback(status);
	            msg = ""+ex.getMessage();
	            logger.error("TaxController :"+ex.getMessage());
	        } finally{
	            try {
	                jobj.put( "success",issuccess);
	                jobj.put( "msg",msg);
	            } catch (JSONException ex) {
	            	logger.error("TaxController :"+ex.getMessage());
	            }
	        }
	        return new ModelAndView("jsonView", "model", jobj.toString());
	    }
	 public void saveTax(HttpServletRequest request) throws ServiceException, SessionExpiredException {
	        try {
	            JSONArray jArr = new JSONArray(request.getParameter("data"));
	            JSONArray jDelArr = new JSONArray(request.getParameter("deleteddata"));
	            String companyId = sessionHandlerImpl.getCompanyid(request);
	            String taxid = "";
	            KwlReturnObject result;
	            int delCount = 0;
	            for (int i = 0; i < jDelArr.length(); i++) {
	                JSONObject jobj = jDelArr.getJSONObject(i);
	                if (StringUtil.isNullOrEmpty(jobj.getString("taxid")) == false) {
	                    taxid = jobj.getString("taxid");
	                        try {
								result = taxDAO.deleteTax(taxid, companyId);
								delCount += result.getRecordTotalCount();
							} catch (com.krawler.common.service.ServiceException e) {
								
								logger.error("TaxController  saveTax():"+e.getMessage());
							}
	                        
	                    	
	                }
	            }
	            String auditMsg;
	            String auditID;
	            Tax tax;
//	            TaxList taxlist;
	            KwlReturnObject taxresult;
	            Map<String, Object> taxMap;
////	            String fullName = AuthHandler.getFullName(session, AuthHandler.getUserid(request));
//	            if (delCount > 0) {
//	                auditTrailObj.insertAuditLog(AuditAction.TAX_DETAIL_DELETED, "User " + sessionHandlerImpl.getUserFullName(request) + " deleted " + delCount + " Tax Details", request, "0");
//	            }
	            for (int i = 0; i < jArr.length(); i++) {
	                JSONObject jobj = jArr.getJSONObject(i);
	                if (jobj.getBoolean("modified") == false) {
	                    continue;
	                }

//	                tax.setName(jobj.getString("taxname"));
//	                tax.setTaxCode(jobj.getString("taxcode"));//accountid
//	                tax.setCompany(company);
//	                tax.setAccount((Account) session.get(Account.class, jobj.getString("accountid")));
	                taxMap = new HashMap<String, Object>();
	                taxMap.put("taxname", jobj.getString("taxname"));
	                taxMap.put("percent", jobj.getString("percent"));
	               // taxMap.put("applydate", request.getParameter("applydate"));
	                //taxMap.put("taxcode",jobj.getString("taxcode"));
	                taxMap.put( "companyid",companyId);

	                if (StringUtil.isNullOrEmpty(jobj.getString("taxid"))) {
//	                    auditMsg = "added";
//	                    auditID = AuditAction.TAX_DETAIL_CREATED;
	                    try {
							taxresult = taxDAO.addTax(taxMap);
						} catch (com.krawler.common.service.ServiceException e) {
							
							logger.error("TaxController  saveTax():"+e.getMessage());
						}
	                }
//	                else {
//	                    auditMsg = "updated";
//	                    auditID = AuditAction.TAX_DETAIL_DELETED;
//	                    taxMap.put(TAXID, jobj.getString(TAXID));
//	                    taxresult = accTaxObj.updateTax(taxMap);
//	                }
//	                tax = (Tax) taxresult.getEntityList().get(0);
//	                taxlist = setNewTax(request, jobj, tax);
//	                auditTrailObj.insertAuditLog(auditID, "User " + sessionHandlerImpl.getUserFullName(request) + " " + auditMsg +" "+ tax.getName(), request, tax.getID());
	            }
	        } catch (JSONException ex) {
	            throw new ServiceException(ex.getMessage(), ex);
	        }
	    }
	 public ModelAndView getTaxRefrences(HttpServletRequest request, HttpServletResponse response) {
		 JSONObject jobj=new JSONObject();
		 KwlReturnObject resultQuotationDetails;
         KwlReturnObject resultQuotation;
         String message="";
         String taxid = "";
		 try {
			 taxid = request.getParameter("taxid");
         	resultQuotationDetails=taxDAO.getTaxReferencesFromQuotationDetails(taxid);
         	resultQuotation=taxDAO.getTaxReferencesFromQuotation(taxid);
         	//message="You can not delete this. It is in used of the following Quotation: <br><b>";
         		 if (resultQuotationDetails.getEntityList().size() > 0) {
         	            List ls = resultQuotationDetails.getEntityList();
         	            Iterator ite = ls.iterator();
         	            while(ite.hasNext()) {
         	            	QuotationDetail quotationDetail = (QuotationDetail) ite.next();
         	            	if(!message.contains(quotationDetail.getQuotation().getQuotationNumber())){
         	            		message+=quotationDetail.getQuotation().getQuotationNumber()+", ";
         	            	}
         	            	
         	                
         	                }
         	            }
         		 if (resultQuotation.getEntityList().size() > 0) {
         	            List ls = resultQuotation.getEntityList();
         	            Iterator ite = ls.iterator();
         	            while(ite.hasNext()) {
         	            	Quotation quotation = (Quotation) ite.next();
         	            	if(!message.contains(quotation.getQuotationNumber())){
         	            		message+=quotation.getQuotationNumber()+", ";
         	            	}
         	            	
         	                
         	                }
         	            }
         		 if(message==""){
         			jobj.put( "success",true);
         			 
         		 }else{
         			jobj.put( "success",false);
         			jobj.put( "msg",message);
         		 }
         	     
         	
         } catch (com.krawler.common.service.ServiceException e) {
         	logger.error("TaxController  getTaxRefrences():"+e.getMessage());
			} catch (JSONException e) {
				logger.error("TaxController  getTaxRefrences():"+e.getMessage());
			e.printStackTrace();
		}
	        return new ModelAndView("jsonView", "model", jobj.toString());
	    }

}
