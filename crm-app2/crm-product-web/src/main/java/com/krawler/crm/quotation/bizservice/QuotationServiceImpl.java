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
package com.krawler.crm.quotation.bizservice;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.Docs;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.Quotation;
import com.krawler.crm.database.tables.QuotationDetail;
import com.krawler.crm.quotation.QuotationDAO;
import com.krawler.common.util.Constants;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.documents.documentDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class QuotationServiceImpl implements QuotationService{
    private static final Log LOGGER = LogFactory.getLog(QuotationServiceImpl.class);
    private documentDAO documentDAOObj;
    private storageHandlerImpl storageHandlerImplObj;
    private QuotationDAO QuotationDAOObj;
    private crmLeadDAO crmLeadDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private exportDAOImpl exportDAOImplObj;

    public void setdocumentDAO(documentDAO documentDAOObj1) {
        this.documentDAOObj = documentDAOObj1;
    }

    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setQuotationDAO(QuotationDAO QuotationDAOObj) {
        this.QuotationDAOObj = QuotationDAOObj;
    }

    @Override
    public JSONObject getQuotationList(String companyid, String userid, String ss, String moduleid,int start,int limit) {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", Arrays.asList("company.companyID","deleted","moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid,false,moduleid));
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
            kmsg = QuotationDAOObj.getQuotationList(requestParams);
            jobj = getQuotationListJSON(moduleid, kmsg.getEntityList(),kmsg.getRecordTotalCount());
        } catch(Exception ex) {
            LOGGER.warn(ex.getMessage(),ex);
        }
        return jobj;
    }

    private JSONObject getQuotationListJSON (String moduleid, List<Quotation> ll, int totalCnt) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        JSONObject jobjQI = new JSONObject();
        JSONArray jarrQI = new JSONArray();
        try {
            List<String> idsList = new ArrayList<String>();
            for (Quotation obj : ll) {
                idsList.add(obj.getCustomer());
            }
            List<String> parentInfo = new ArrayList<String>();
            Map<String, List<String>> parentNames = new HashMap<String, List<String>>();

            if(moduleid.equals(Constants.MODULEID_LEAD)) {
                List<CrmLead> parentll = crmLeadDAOObj.getLeads(idsList);
                if(parentll!=null) {
                    for (CrmLead obj : parentll) {
                        parentInfo= new ArrayList<String>();
                        parentInfo.add(StringUtil.getFullName(obj.getFirstname(), obj.getLastname()));
                        parentInfo.add(obj.getAddstreet());
                        parentInfo.add(obj.getEmail());
                        parentNames.put(obj.getLeadid(), parentInfo);
                    }
                }
            } else if(moduleid.equals(Constants.MODULEID_ACCOUNT)) {
                List<CrmAccount> parentll = crmAccountDAOObj.getAccounts(idsList);
                if(parentll!=null) {
                    for (CrmAccount obj : parentll) {
                        parentInfo= new ArrayList<String>();
                        parentInfo.add(obj.getAccountname());
                        parentInfo.add(obj.getEmail());
                        parentInfo.add(obj.getEmail());
                        parentNames.put(obj.getAccountid(), parentInfo);
                    }
                }
            }
            
            for (Quotation obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("billid", obj.getID());
                tmpObj.put("billno", obj.getquotationNumber());
                tmpObj.put("customer", obj.getCustomer());
                tmpObj.put("customername", parentNames.get(obj.getCustomer()).get(0));
                tmpObj.put("shipping", parentNames.get(obj.getCustomer()).get(1));
                tmpObj.put("personemail", parentNames.get(obj.getCustomer()).get(2));
                tmpObj.put("date", obj.getQuotationDate());
                tmpObj.put("duedate", obj.getDueDate());
                tmpObj.put("memo", obj.getMemo());
                tmpObj.put("amount", obj.getTotalamount());
                tmpObj.put("subtotal", (obj.getTotalamount() +obj.getDiscountAmount() - obj.getTaxAmount()));
                tmpObj.put("discount", obj.getDiscount());
                tmpObj.put("discountamount", obj.getDiscountAmount());
                tmpObj.put("taxamount", obj.getTaxAmount());
                tmpObj.put("perdiscount", obj.isPerDiscount());
                tmpObj.put("istaxable", obj.isTaxable());
                tmpObj.put("taxid", obj.getTax()!=null?obj.getTax().getId():"");
                tmpObj.put("taxname", obj.getTax()!=null?obj.getTax().getName():"");
                tmpObj.put("isproducttax", obj.isProductTax());
                tmpObj.put("status", obj.getStatus()==0 ? "Draft": "Mail Sent");
                tmpObj.put("templateid", obj.getTemplateid().getTempid());
                tmpObj.put("templatename", obj.getTemplateid().getTempname());
                jobjQI=getQuotationItems(obj.getID());
                tmpObj.put("dataItem", jobjQI);
//                tmpObj.put("billno", obj);
                jarr.put(tmpObj);
               
            }
             jobj.put("success", true);
             jobj.put("data", jarr);
             //jobj.put("dataItem", jarrQI);
             jobj.put("count", totalCnt);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    @Override
    public JSONObject getQuotationItems(String id) {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", Arrays.asList("quotation.ID"));
            requestParams.put("filter_values", Arrays.asList(id));
            kmsg = QuotationDAOObj.getQuotationItems(requestParams);
            jobj = getQuotationItemJSON(kmsg.getEntityList(),kmsg.getRecordTotalCount());
        } catch(Exception ex) {
            LOGGER.warn(ex.getMessage(),ex);
        }
        return jobj;
    }
    private JSONObject getQuotationItemJSON (List<QuotationDetail> ll, int totalCnt) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            for (QuotationDetail obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("rowid", obj.getID());
                tmpObj.put("productid", obj.getProduct().getProductid());
                tmpObj.put("productname", obj.getProduct().getProductname());
                tmpObj.put("description", obj.getProduct().getDescription()!=null?obj.getProduct().getDescription():"");
                tmpObj.put("quantity", obj.getQuantity());
                tmpObj.put("orderrate", obj.getRate());
                tmpObj.put("prtaxid", obj.getTax()!=null?obj.getTax().getId():"");
                tmpObj.put("taxpercent", obj.getTax()!=null?obj.getTax().getPercent():"");
                tmpObj.put("prdiscountamount", obj.getDiscount()!=0?(obj.getDiscount()/100*(obj.getQuantity() * obj.getRate())):0);
                tmpObj.put("prdiscount", obj.getDiscount()!=0?obj.getDiscount():0);
                tmpObj.put("taxamount", obj.getTaxAmount()!=0?obj.getTaxAmount():0);
                tmpObj.put("amount", (obj.getQuantity() * obj.getRate())+obj.getTaxAmount()-(obj.getQuantity() * obj.getRate()*obj.getDiscount()/100));
                jarr.put(tmpObj);
            }
             jobj.put("success", true);
             jobj.put("data", jarr);
             jobj.put("count", totalCnt);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    @Override
    public JSONObject getRecordName(String moduleid, StringBuffer usersList, String companyid, boolean heirarchyPerm, String ss) {
        JSONObject jobj = new JSONObject();
        try {
            if(moduleid.equals(Constants.MODULEID_LEAD)) {
                jobj = getLeadNames(companyid,heirarchyPerm,usersList,ss);
            } else if(moduleid.equals(Constants.MODULEID_ACCOUNT)) {
                jobj = getAccountNames(companyid,heirarchyPerm,usersList,ss);
            }
        } catch(Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);
        }
        return jobj;
    }

    private JSONObject getLeadNames(String companyid, boolean heirarchyPerm, StringBuffer usersList, String ss) {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            JSONArray jArr = new JSONArray();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", false);
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
                requestParams.put("searchcolarray",new String[]{"c.lastname","c.firstname"});
            }
            requestParams.put("heirarchyPerm", heirarchyPerm);
            requestParams.put("start", 0);
            requestParams.put("limit", 20);
            requestParams.put("companyid", companyid);
            kmsg = crmLeadDAOObj.getLeads(requestParams, usersList);
            List<CrmLead> ll = kmsg.getEntityList();
            for (CrmLead obj : ll) {
                JSONObject jObj = new JSONObject();
                jObj.put("id", obj.getLeadid());
                jObj.put("name", StringUtil.getFullName(obj.getFirstname(), obj.getLastname()));
//                jObj.put("address", obj.getAddstreet());
                jArr.put(jObj);
            }
            jobj.put("success", true);
            jobj.put("data", jArr);
        } catch(Exception ex) {
            LOGGER.warn(ex.getMessage(),ex);
        }
        return jobj;
    }
    private JSONObject getAccountNames(String companyid, boolean heirarchyPerm, StringBuffer usersList, String ss) {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            JSONArray jArr = new JSONArray();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", false);
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
            requestParams.put("heirarchyPerm", heirarchyPerm);
            requestParams.put("start", 0);
            requestParams.put("limit", 20);
            requestParams.put("companyid", companyid);
            kmsg = crmAccountDAOObj.getAccounts(requestParams, usersList);
            List<CrmAccount> ll = kmsg.getEntityList();
            for (CrmAccount obj : ll) {
                JSONObject jObj = new JSONObject();
                jObj.put("id", obj.getAccountid());
                jObj.put("name", obj.getAccountname());
//                jObj.put("address", "");
                jArr.put(jObj);
            }
            jobj.put("success", true);
            jobj.put("data", jArr);
        } catch(Exception ex) {
            LOGGER.warn(ex.getMessage(),ex);
        }
        return jobj;
    }

    @Override
    public void invoiceExport(HttpServletRequest request, HttpServletResponse response,String currencyid) {
        JSONObject jobj = new JSONObject();
        try {
            Map<String, Object> DataInfo = new HashMap<String, Object>();
            Company company= null;
            JSONArray productDetails = new JSONArray();
            ArrayList ids = new ArrayList();
            String billid = request.getParameter("billid");
            ids.add(billid);
            List<Quotation> ll= QuotationDAOObj.getQuotations(ids);
            Quotation quote = ll.get(0);
            company = quote.getCompany();
            DataInfo.put("invno", quote.getquotationNumber());
            DataInfo.put("entrydate", new java.util.Date(quote.getQuotationDate()));
            DataInfo.put("customername", request.getParameter("customername"));
            DataInfo.put("address", request.getParameter("address"));
            DataInfo.put("memo", quote.getMemo());
            DataInfo.put("config", quote.getTemplateid().getConfigstr());
            DataInfo.put("quotationdisc", quote.getDiscountAmount());
            DataInfo.put("quotationtax", quote.getTaxAmount());
            if(quote.getTax()!=null) {
                DataInfo.put("quotationtaxname", quote.getTax().getName());
                DataInfo.put("quotationtaxpercent", quote.getTax().getPercent());
            }
            DataInfo.put("totalamount", quote.getTotalamount());
            String letterHead=quote.getTemplateid().getLetterHead()!=null?quote.getTemplateid().getLetterHead():"";
            String preText= quote.getTemplateid().getPreText()!=null?quote.getTemplateid().getPreText():"";
            String postText=quote.getTemplateid().getPostText()!=null?quote.getTemplateid().getPostText():"";
            DataInfo.put("heading", "Quotation");
            DataInfo.put("filename", quote.getquotationNumber());
            String fileType = request.getParameter("filetype");
            int mode = Integer.parseInt(request.getParameter("mode"));
//            ids.clear();
//            ids.add(quote.getTemplateid());
//            exportPdfTemplDAO.getTemplateList(ids);
            productDetails = getQuotationItems(billid).getJSONArray("data");
            boolean isWriteToResponse = true;
//            JSONObject quoteObj = QuotationServiceObj.getQuotationList(companyid, userid, ss, moduleid);
            exportDAOImplObj.processInvoiceGenerateRequest(request, response, jobj,DataInfo,company,currencyid,productDetails,isWriteToResponse, fileType , mode,letterHead,preText,postText);
            
        } catch (Exception ex) {
            LOGGER.warn(ex.getMessage(),ex);
        }
    }

    @Override
    public Quotation sendInvoiceMail(HttpServletRequest request, HttpServletResponse response, String companyid, String userid, String currencyid, String billid,
            String customername,String address, String[] emails, String personid, String htmlMsg, String plainMsg, String subject,
            boolean sendPdf, ServletContext servletContext) {
        KwlReturnObject kmsg = null;
        Quotation quote = null;
        try {
            boolean issuccess = false;
            JSONObject jobj = new JSONObject();
            ByteArrayOutputStream baos = null;
            String path = "";
            String preText="";
            String postText="";
            String letterHead="";
            Company company= null;
            String filename= "";
            ArrayList ids = new ArrayList();
            ids.add(billid);
            List<Quotation> ll= QuotationDAOObj.getQuotations(ids);
            quote = ll.get(0);
            String moduleid = quote.getModuleid();
            company = quote.getCompany();
            if(sendPdf){
                Map<String, Object> DataInfo = new HashMap<String, Object>();
                JSONArray productDetails = new JSONArray();
                DataInfo.put("invno", quote.getquotationNumber());
                DataInfo.put("entrydate", new java.util.Date(quote.getQuotationDate()));
                DataInfo.put("customername", customername);
                DataInfo.put("address", address);
                DataInfo.put("memo", quote.getMemo());
                DataInfo.put("config", quote.getTemplateid().getConfigstr());
                DataInfo.put("quotationdisc", quote.getDiscountAmount());
                DataInfo.put("quotationtax", quote.getTaxAmount());
                if(quote.getTax()!=null) {
                    DataInfo.put("quotationtaxname", quote.getTax().getName());
                    DataInfo.put("quotationtaxpercent", quote.getTax().getPercent());
                }
                DataInfo.put("totalamount", quote.getTotalamount());
                letterHead=quote.getTemplateid().getLetterHead()!=null?quote.getTemplateid().getLetterHead():"";
                preText= quote.getTemplateid().getPreText()!=null?quote.getTemplateid().getPreText():"";
                postText=quote.getTemplateid().getPostText()!=null?quote.getTemplateid().getPostText():"";
                DataInfo.put("heading", "Quotation");
                DataInfo.put("filename", quote.getquotationNumber());
                productDetails = getQuotationItems(billid).getJSONArray("data");
    //            double amount=Double.parseDouble((String)request.getParameter("amount"));
                int mode=Integer.parseInt(request.getParameter("mode"));
                String fileType= "pdf";
                filename = DataInfo.get("heading").toString() + DataInfo.get("filename").toString() +"." +fileType;
//                Date invDate = new Date();
                boolean isWriteToResponse= false;
                baos = exportDAOImplObj.processInvoiceGenerateRequest(request, response, jobj,DataInfo,company,currencyid,productDetails,isWriteToResponse, fileType , mode,letterHead,preText,postText);
                
                kmsg = documentDAOObj.saveFileWithDocEntry(baos,userid , companyid, filename, servletContext);
                List docList = kmsg.getEntityList();
                Docs d = (Docs) docList.get(0);
                jobj.put("docid", d.getDocid());
                jobj.put("companyid", companyid);
                if(moduleid.equals(Constants.MODULEID_LEAD)) {
                    jobj.put("map", "1");
                } else if(moduleid.equals(Constants.MODULEID_ACCOUNT)) {
                    jobj.put("map", "4");
                }
                jobj.put("refid", personid);
                documentDAOObj.saveDocumentMapping(jobj);
                
                path = docList.get(1).toString();

                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("tag", d.getDocid()+",,quotation,sent,"+customername);

                kmsg = documentDAOObj.addTag(requestParams);

                baos.close();
            }
            String fromID = StringUtil.isNullOrEmpty(company.getEmailID())?"admin@deskera.com":company.getEmailID();
            try {
                if (emails.length > 0) {
                    if(StringUtil.isNullOrEmpty(path))
                        SendMailHandler.postMail(emails, subject, htmlMsg, plainMsg, fromID);
                    else
                        SendMailHandler.postMailAttachment(emails, subject, htmlMsg, plainMsg, fromID, path, filename);
                    issuccess=true;
                }
                if(quote!=null) {
                    HashMap<String, Object> soDataMap = new HashMap<String, Object>();
                    soDataMap.put("id", quote.getID());
                    soDataMap.put("status", "1"); // status 1 : sent email
                    QuotationDAOObj.saveQuotation(soDataMap);
                }
            } catch (MessagingException e) {
                issuccess=false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return quote;
        }
    }

}
