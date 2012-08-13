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

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.Quotation;
import com.krawler.crm.database.tables.QuotationDetail;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.quotation.bizservice.QuotationService;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class QuotationController  extends MultiActionController {
    private String successView;
    private HibernateTransactionManager txnManager;
    private crmLeadDAO crmLeadDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private QuotationDAO QuotationDAOObj;
    private QuotationService QuotationServiceObj;
    private auditTrailDAO auditTrailDAOObj;
    
    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setQuotationService(QuotationService QuotationServiceObj) {
        this.QuotationServiceObj = QuotationServiceObj;
    }
    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
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

    public void setQuotationDAO(QuotationDAO QuotationDAOObj) {
        this.QuotationDAOObj = QuotationDAOObj;
    }
    
    public ModelAndView saveQuotation(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj=new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Quotation_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
        	jobj = saveQuotation(request);
        	txnManager.commit(status);
        } catch (Exception ex) {
            txnManager.rollback(status);
            ex.printStackTrace();
			logger.warn(ex.getMessage(), ex);
        } finally {
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject saveQuotation(HttpServletRequest request) throws SessionExpiredException, ServiceException {
    	Quotation quotation = null;
        JSONObject jobj=new JSONObject();
        boolean issuccess = false;
        String msg = "";
        try {
            String taxid = null;
            taxid = request.getParameter("taxid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String entryNumber = request.getParameter("number");
            String costCenterId = request.getParameter("costcenter");
            String nextAutoNumber="";
            HashMap<String, Object> soDataMap = new HashMap<String, Object>();

            KwlReturnObject socnt = QuotationDAOObj.getQuotationCount(entryNumber, companyid);
            if (socnt.getRecordTotalCount() > 0) {
               // msg = "Quotation number '" + entryNumber + "' already exists.";
//                throw new ServiceException("Sales Order number '" + entryNumber + "' already exists.");
                String moduleid = request.getParameter("moduleid");
                String id = request.getParameter("billto");
                soDataMap.put("id", id);
                soDataMap.put("entrynumber", entryNumber);
                soDataMap.put("memo", request.getParameter("memo"));
                soDataMap.put("customerid", request.getParameter("customer"));
                soDataMap.put("moduleid", moduleid);
                soDataMap.put("orderdate", request.getParameter("billdate"));
                soDataMap.put("duedate", request.getParameter("duedate"));
                soDataMap.put("total", (Double.valueOf(request.getParameter("subTotal"))-Double.valueOf(request.getParameter("discountAmount"))+Double.valueOf(request.getParameter("taxAmount"))));
                soDataMap.put("templateid", request.getParameter("templateid"));
                soDataMap.put("discount", request.getParameter("discount"));
                soDataMap.put("taxid", taxid);
                soDataMap.put("isTaxable", request.getParameter("isTaxable"));
                soDataMap.put("perdiscount", request.getParameter("perdiscount"));
                soDataMap.put("discountAmount", request.getParameter("discountAmount"));
                soDataMap.put("taxAmount", request.getParameter("taxAmount"));
                soDataMap.put("isProductTax", request.getParameter("isProductTax"));
                soDataMap.put("companyid", companyid);
                if(!StringUtil.isNullOrEmpty(costCenterId)){
                    soDataMap.put("costCenterId", costCenterId);
                }
                KwlReturnObject soresult = QuotationDAOObj.editQuotation(soDataMap);
                quotation = (Quotation) soresult.getEntityList().get(0);

                soDataMap.put("id", quotation.getID());
                HashSet sodetails = saveQuotationRows(request, quotation, companyid);
                msg = "Quotation has been edited successfully";
                issuccess = true;
//                String filename = "Quotation" + entryNumber +".pdf";
//                if(moduleid.equals(Constants.MODULEID_LEAD)) {
//                    auditTrailDAOObj.insertAuditLog(com.krawler.crm.utils.AuditAction.LEAD_QUOTATIONCREATE, "The Quotation '"+filename+"' has been created for Lead",
//                               request, request.getParameter("customer"));
//                } else if(moduleid.equals(Constants.MODULEID_ACCOUNT)) {
//                    auditTrailDAOObj.insertAuditLog(com.krawler.crm.utils.AuditAction.ACCOUNT_QUOTATIONCREATE, "The Quotation '"+filename+"' has been created for Account",
//                               request, request.getParameter("customer"));
//                }

            }
            else{
            	nextAutoNumber = QuotationDAOObj.getNextAutoNumber(companyid, com.krawler.crm.utils.Constants.AUTONUM_QUOTATION);
                String moduleid = request.getParameter("moduleid");
                soDataMap.put("entrynumber", entryNumber);
                soDataMap.put("autogenerated", nextAutoNumber.equals(entryNumber));
                soDataMap.put("memo", request.getParameter("memo"));
                soDataMap.put("customerid", request.getParameter("customer"));
                soDataMap.put("moduleid", moduleid);
                soDataMap.put("orderdate", request.getParameter("billdate"));
                soDataMap.put("duedate", request.getParameter("duedate"));
                soDataMap.put("total", (Double.valueOf(request.getParameter("subTotal"))-Double.valueOf(request.getParameter("discountAmount"))+Double.valueOf(request.getParameter("taxAmount"))));
                soDataMap.put("templateid", request.getParameter("templateid"));
                soDataMap.put("discount", request.getParameter("discount"));
                soDataMap.put("taxid", taxid);
                soDataMap.put("isTaxable", request.getParameter("isTaxable"));
                soDataMap.put("perdiscount", request.getParameter("perdiscount"));
                soDataMap.put("discountAmount", request.getParameter("discountAmount"));
                soDataMap.put("taxAmount", request.getParameter("taxAmount"));
                soDataMap.put("isProductTax", request.getParameter("isProductTax"));
                soDataMap.put("companyid", companyid);
                if(!StringUtil.isNullOrEmpty(costCenterId)){
                    soDataMap.put("costCenterId", costCenterId);
                }
                KwlReturnObject soresult = QuotationDAOObj.saveQuotation(soDataMap);
                quotation = (Quotation) soresult.getEntityList().get(0);

                soDataMap.put("id", quotation.getID());
                HashSet sodetails = saveQuotationRows(request, quotation, companyid);
                msg = "Quotation has been saved successfully";
                issuccess = true;
                String filename = "Quotation" + entryNumber +".pdf";
                if(moduleid.equals(Constants.MODULEID_LEAD)) {
                    auditTrailDAOObj.insertAuditLog(com.krawler.crm.utils.AuditAction.LEAD_QUOTATIONCREATE, "The Quotation '"+filename+"' has been created for Lead",
                               request, request.getParameter("customer"));
                } else if(moduleid.equals(Constants.MODULEID_ACCOUNT)) {
                    auditTrailDAOObj.insertAuditLog(com.krawler.crm.utils.AuditAction.ACCOUNT_QUOTATIONCREATE, "The Quotation '"+filename+"' has been created for Account",
                               request, request.getParameter("customer"));
                }


            }
//            if (taxid != null && !taxid.isEmpty()) {
//                Tax tax = (Tax) kwlCommonTablesDAOObj.getClassObject(Tax.class.getName(), taxid);
//                if (tax == null) {
//                    throw new AccountingException("The Tax code(s) used in this transaction has been deleted.");
//                }
//                soDataMap.put("taxid", taxid);
//            }

        } catch (Exception ex) {
            msg = "Error occured at server side while saving data";
            msg = "" + ex.getMessage();
            throw ServiceException.FAILURE("saveQuotation : " + ex.getMessage(), ex);
        } finally {
            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
            } catch (JSONException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
        return jobj;
    }

    public HashSet saveQuotationRows(HttpServletRequest request, Quotation quotation, String companyid) throws ServiceException {
        HashSet rows = new HashSet();
        try {
            JSONArray jArr = new JSONArray(request.getParameter("detail"));
            for(int i=0; i<jArr.length(); i++){
                JSONObject jobj = jArr.getJSONObject(i);
                HashMap<String, Object> sodDataMap = new HashMap<String, Object>();
                sodDataMap.put("srno", i+1);
                sodDataMap.put("companyid", companyid);
                sodDataMap.put("soid", quotation.getID());
                sodDataMap.put("productid", jobj.getString("productid"));
                sodDataMap.put("rate", jobj.getDouble("rate"));//CompanyHandler.getCalCurrencyAmount(session,request,jobj.getDouble("rate"),request.getParameter("currencyid"),null));
                sodDataMap.put("quantity", jobj.getInt("quantity"));
                sodDataMap.put("remark", jobj.optString("remark"));
                sodDataMap.put("discount", jobj.optString("prdiscount"));
                sodDataMap.put("taxamount", jobj.optString("taxamount"));
                sodDataMap.put("tax", jobj.optString("prtaxid"));
                String rowtaxid = jobj.getString("prtaxid");
//                if (!StringUtil.isNullOrEmpty(rowtaxid)) {
//                    KwlReturnObject txresult = accountingHandlerDAOobj.getObject(Tax.class.getName(),rowtaxid); // (Tax)session.get(Tax.class, taxid);
//                    Tax rowtax = (Tax) txresult.getEntityList().get(0);
//                    if (rowtax == null)
//                        throw new AccountingException("The Tax code(s) used in this transaction has been deleted.");
//                    else
//                        sodDataMap.put("rowtaxid", rowtaxid);
//                 }
                      //  row.setTax(rowtax);
                KwlReturnObject id= QuotationDAOObj.getQuotationItemsId(quotation.getID(),jobj.getString("productid"));
                if (id.getEntityList() != null && !id.getEntityList().isEmpty()) {
                String qdid=(String)id.getEntityList().get(0);
                 if(qdid!=null){
                	 			sodDataMap.put("id", qdid);
                	 			}
                }
                KwlReturnObject result = QuotationDAOObj.saveQuotationDetails(sodDataMap);
                QuotationDetail row = (QuotationDetail) result.getEntityList().get(0);
                rows.add(row);
            }
        } catch (JSONException ex) {
            throw ServiceException.FAILURE("saveQuotationRows : " + ex.getMessage(), ex);
        }
        return rows;
    }

    public ModelAndView getRecordName(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj=new JSONObject();
        try {
            boolean heirarchyPerm = true;
            String moduleid = request.getParameter("moduleid");
            String ss = request.getParameter("query");
            ss=Matcher.quoteReplacement(ss);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            if(moduleid.equals(Constants.MODULEID_LEAD)) {
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Lead_modulename);
            } else if(moduleid.equals(Constants.MODULEID_ACCOUNT)) {
                heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Account_modulename);
            }
            jobj = QuotationServiceObj.getRecordName(moduleid, usersList, companyid, heirarchyPerm, ss);
        } catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
        } finally {
            
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView getQuotationList(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj=new JSONObject();
        String msg = "";
        boolean issuccess = false;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String ss = request.getParameter("ss");
            String moduleid = request.getParameter("moduleid");
            int start=Integer.parseInt(request.getParameter("start"));
            int limit=Integer.parseInt(request.getParameter("limit"));
            jobj = QuotationServiceObj.getQuotationList(companyid, userid, ss, moduleid,start,limit);
        } catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
        } finally {

        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getQuotationItems(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj=new JSONObject();
        String msg = "";
        boolean issuccess = false;
        try {
//            String userid = sessionHandlerImpl.getUserid(request);
//            String companyid = sessionHandlerImpl.getCompanyid(request);
            String id = request.getParameter("bills");
            jobj = QuotationServiceObj.getQuotationItems(id);
            
        } catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
        } finally {

        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteQuotations(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj=new JSONObject();
        String msg = "";
        boolean issuccess = false;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("Quotation_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String jsondata = request.getParameter("data");
            JSONArray jarr = new JSONArray(jsondata);
            ArrayList ids = new ArrayList();
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobject = jarr.getJSONObject(i);
                ids.add(jobject.getString("billid").toString());
            }
            String[] arrayid = (String[]) ids.toArray(new String[]{});
            KwlReturnObject result = QuotationDAOObj.deleteQuotations(arrayid);
            issuccess = true;
            txnManager.commit(status);
        } catch (Exception ex) {
            txnManager.rollback(status);
            ex.printStackTrace();
            msg = "Error occured at server side while deleting data";
            msg = " " + ex.getMessage();
			logger.warn(ex.getMessage(), ex);
        } finally {
            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
            } catch (JSONException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView invoiceExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        String view = "jsonView";
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            QuotationServiceObj.invoiceExport(request,response, currencyid);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView getNextAutoNumber(HttpServletRequest request, HttpServletResponse response) {
        String num = "";
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            int from = Integer.valueOf(request.getParameter("from"));

            num = QuotationDAOObj.getNextAutoNumber(companyid, from);
            jobj.put("data", num);
            jobj.put("success", true);
            jobj.put("msg", "");
        } catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView sendInvoiceMail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, JSONException {
        JSONObject jobj = new JSONObject();
        boolean issuccess = false;
        try {
            String[] emails = request.getParameter("emailid").split(";");
            String personid = request.getParameter("personid");
            String htmlMsg = request.getParameter("message");
            String plainmsg = request.getParameter("plainmsg");
            String subject = request.getParameter("subject");
            boolean sendPdf = Boolean.parseBoolean((String) request.getParameter("sendpdf"));
            String billid=request.getParameter("billid");
            String customername=request.getParameter("customername");
            String address=request.getParameter("address");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String currency = sessionHandlerImpl.getCurrencyID(request);
            Quotation quote = QuotationServiceObj.sendInvoiceMail(request, response, companyid, userid, currency, billid,customername, address, emails,
                    personid, htmlMsg, plainmsg, subject, sendPdf, getServletContext());
            String filename = "Quotation" + quote.getquotationNumber() +".pdf";
            String moduleid = quote.getModuleid();
            if(moduleid.equals(Constants.MODULEID_LEAD)) {
                auditTrailDAOObj.insertAuditLog(com.krawler.crm.utils.AuditAction.LEAD_QUOTATIONSENT, "The Quotation '"+filename+"' has been sent for Lead",
                           request, quote.getCustomer());
            } else if(moduleid.equals(Constants.MODULEID_ACCOUNT)) {
                auditTrailDAOObj.insertAuditLog(com.krawler.crm.utils.AuditAction.ACCOUNT_QUOTATIONSENT, "The Quotation '"+filename+"' has been sent for Account",
                           request, quote.getCustomer());
            }


        } catch (Exception e) {
            issuccess = false;
        } finally {
            jobj.put("success", issuccess);
            jobj.put("msg", "Mail has been sent successfully");
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView deleteQuotationItems(HttpServletRequest request, HttpServletResponse response) {
    	 JSONObject jobj=new JSONObject();
         String msg = "";
         boolean issuccess = false;
         DefaultTransactionDefinition def = new DefaultTransactionDefinition();
         def.setName("Quotation_Tx");
         def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
         TransactionStatus status = txnManager.getTransaction(def);
         try {
        	 String billid = request.getParameter("billid");
             String productId = request.getParameter("productid");
             KwlReturnObject result = QuotationDAOObj.deleteQuotationItems(billid,productId);
             issuccess = true;
             txnManager.commit(status);
         } catch (Exception ex) {
             txnManager.rollback(status);
             ex.printStackTrace();
             msg = "Error occured at server side while deleting data";
             msg = " " + ex.getMessage();
 			logger.warn(ex.getMessage(), ex);
         } finally {
             try {
                 jobj.put("success", issuccess);
                 jobj.put("msg", msg);
             } catch (JSONException ex) {
                 logger.warn(ex.getMessage(), ex);
             }
         }
         return new ModelAndView("jsonView", "model", jobj.toString());
    }
}
