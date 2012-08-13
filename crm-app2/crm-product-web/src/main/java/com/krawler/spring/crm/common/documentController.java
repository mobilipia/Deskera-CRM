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

import com.krawler.common.service.ServiceException;
import java.text.DateFormat;
import com.krawler.spring.documents.*;
import com.krawler.common.admin.DocOwners;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.common.admin.Docs;
import com.krawler.common.admin.ProjectFeature;
import com.krawler.common.admin.User;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.utils.AuditAction;
import com.krawler.esp.handlers.FileUploadHandler;
import com.krawler.esp.utils.KrawlerApp;
import com.krawler.esp.utils.LuceneSearchConstants;
import com.krawler.luceneSearchService.LuceneSearch;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
//import com.krawler.crm.database.tables.CrmAccount;
//import com.krawler.crm.database.tables.CrmActivityMaster;
//import com.krawler.crm.database.tables.CrmCampaign;
//import com.krawler.crm.database.tables.CrmCase;
//import com.krawler.crm.database.tables.CrmContact;
//import com.krawler.crm.database.tables.CrmLead;
//import com.krawler.crm.database.tables.CrmOpportunity;
//import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.accountModule.crmAccountHandler;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.contactModule.crmContactHandler;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.leadModule.crmLeadHandler;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityHandler;
import com.krawler.spring.profileHandler.profileHandler;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.lucene.document.Document;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import static com.krawler.spring.documents.DocumentConstants.*;

/**
 *
 * @author Karthik
 */
public class documentController extends MultiActionController {

    private crmContactDAO crmContactDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmLeadDAO crmLeadDAOObj;
    private documentDAO crmDocumentDAOObj;
    private crmCaseDAO crmCaseDAOObj;
    private DocumentHelper documentHeplerObj;
    private auditTrailDAO auditTrailDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private HibernateTemplate hibernateTemplate;
    private storageHandlerImpl storageHandlerImplObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private String successView;
    private LuceneSearch LuceneSearchObj;
    private HibernateTransactionManager txnManager;

    public void setCrmAccountDAO(crmAccountDAO crmAccountDAOObj) {
        this.crmAccountDAOObj = crmAccountDAOObj;
    }

    public void setCrmContactDAO(crmContactDAO crmContactDAOObj) {
        this.crmContactDAOObj = crmContactDAOObj;
    }

   public void setCrmLeadDAO(crmLeadDAO crmLeadDAOObj) {
        this.crmLeadDAOObj = crmLeadDAOObj;
    }

    public void setCrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj;
    }

    public void setCrmCaseDAO(crmCaseDAO crmCaseDAOObj) {
        this.crmCaseDAOObj = crmCaseDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setcrmDocumentDAO(documentDAO crmDocumentDAOObj1) {
        this.crmDocumentDAOObj = crmDocumentDAOObj1;
    }

    public void setDocumentHelper(DocumentHelper documentHeplerObj) {
        this.documentHeplerObj = documentHeplerObj;
    }

    public void setLuceneSearch(LuceneSearch LuceneSearchObj) {
        this.LuceneSearchObj = LuceneSearchObj;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }



	public JSONObject getDocumentJson(List ll, HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 1;
        try {
            Iterator ite = ll.iterator();
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            while (ite.hasNext()) {
                JSONObject temp = new JSONObject();
                Docs t = (Docs) ite.next();
                temp.put("srno", count++);
                temp.put("docid", t.getDocid());
                temp.put("Name", t.getDocname());
                temp.put("Size", StringUtil.sizeRenderer(t.getDocsize()));
                temp.put("Type", t.getDoctype());
                temp.put("uploadedby", profileHandlerDAOObj.getUserFullName(t.getUserid().getUserID()));
                temp.put("uploadedon", t.getUploadedOn()!=null?dateFormat.format(t.getUploadedon()):"");
                jarr.put(temp);
            }
            jobj.put("docPerm", ((sessionHandlerImplObj.getPerms(request, "Document") & 4) == 4));
            jobj.put("docList", jarr);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }

    public ModelAndView getDocuments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("recid", StringUtil.checkForNull(request.getParameter("recid")));

            kmsg = crmDocumentDAOObj.getDocuments(requestParams);
            jobj = getDocumentJson(kmsg.getEntityList(), request);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }


    public ModelAndView addDocuments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject myjobj = new JSONObject();
        List fileItems = null;
        String details = "";
        KwlReturnObject kmsg = null;
        String auditAction = "";
        String id = java.util.UUID.randomUUID().toString();
       // PrintWriter out = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            response.setContentType("text/html;charset=UTF-8");
           // out = response.getWriter();
            String userid = sessionHandlerImpl.getUserid(request);
            String companyID = sessionHandlerImpl.getCompanyid(request);
            String map = request.getParameter("mapid");
            HashMap<String, String> arrParam = new HashMap<String, String>();
            boolean fileUpload = false;
            ArrayList<FileItem> fi = new ArrayList<FileItem>();
            if (request.getParameter("fileAdd") != null) {
                DiskFileUpload fu = new DiskFileUpload();
                fileItems = fu.parseRequest(request);
                crmDocumentDAOObj.parseRequest(fileItems, arrParam, fi, fileUpload);
            }
            String docID="";
            for (int cnt = 0; cnt < fi.size(); cnt++) {
                kmsg = crmDocumentDAOObj.uploadFile(fi.get(cnt), userid, companyID, getServletContext());
                Docs doc = (Docs) kmsg.getEntityList().get(0);
                docID = doc.getDocid();
                String refid = arrParam.get("refid");
                jobj.put("userid", userid);
                jobj.put("docid", docID);
                jobj.put("companyid", companyID);
                jobj.put("id", id);
                jobj.put("map", map);
                jobj.put("refid", refid);
                crmDocumentDAOObj.saveDocumentMapping(jobj);
                String randomid = java.util.UUID.randomUUID().toString();
                crmDocumentDAOObj.insertDocumentOwnerEntry(randomid,userid,docID);
                if (map.equals("0")) {
                    CrmCampaign c = (CrmCampaign) hibernateTemplate.get(CrmCampaign.class, refid);
                    details = " Campaign - ";
                    details+= StringUtil.isNullOrEmpty(c.getCampaignname())?"":c.getCampaignname();
                    auditAction = AuditAction.CAMPAIGN_DOC_UPLOAD;
                } else if (map.equals("1")) {
                    CrmLead l = (CrmLead) hibernateTemplate.get(CrmLead.class, refid);
                    details = " Lead - ";
                    details+= StringUtil.isNullOrEmpty(l.getFirstname())?"":l.getFirstname()+" ";
                    details+= StringUtil.isNullOrEmpty(l.getLastname())?"":l.getLastname();
                    auditAction = AuditAction.LEAD_DOC_UPLOAD;
                } else if (map.equals("2")) {
                    CrmContact c = (CrmContact) hibernateTemplate.get(CrmContact.class, refid);
                    details = " Contact - ";
                    details+= StringUtil.isNullOrEmpty(c.getFirstname())?"":c.getFirstname()+" ";
                    details+= StringUtil.isNullOrEmpty(c.getLastname())?"":c.getLastname();
                    auditAction = AuditAction.CONTACT_DOC_UPLOAD;
                } else if (map.equals("3")) {
                    CrmProduct p = (CrmProduct) hibernateTemplate.get(CrmProduct.class, refid);
                    details = " Product - ";
                    details+= StringUtil.isNullOrEmpty(p.getProductname())?"":p.getProductname();
                    auditAction = AuditAction.PRODUCT_DOC_UPLOAD;
                } else if (map.equals("4")) {
                    CrmAccount a = (CrmAccount) hibernateTemplate.get(CrmAccount.class, refid);
                    details = " Account - ";
                    details+= StringUtil.isNullOrEmpty(a.getAccountname())?"":a.getAccountname();
                    auditAction = AuditAction.ACCOUNT_DOC_UPLOAD;
                } else if (map.equals("5")) {
                    CrmOpportunity o = (CrmOpportunity) hibernateTemplate.get(CrmOpportunity.class, refid);
                    details = " Opportunity - ";
                    details+=StringUtil.isNullOrEmpty(o.getOppname())?"":o.getOppname();
                    auditAction = AuditAction.OPPORTUNITY_DOC_UPLOAD;
                } else if (map.equals("6")) {
                    CrmCase c = (CrmCase) hibernateTemplate.get(CrmCase.class, refid);
                    details = " Case - ";
                    details+= StringUtil.isNullOrEmpty(c.getSubject())?"":c.getSubject();
                    auditAction = AuditAction.CASE_DOC_UPLOAD;
                } else if (map.equals("7")) {
                    CrmActivityMaster am = (CrmActivityMaster) hibernateTemplate.get(CrmActivityMaster.class, refid);
                    details = " Activity - ";
                    details+= StringUtil.isNullOrEmpty(am.getFlag())?"":am.getFlag()+" ";
                    details+= StringUtil.isNullOrEmpty(am.getCrmCombodataByStatusid().getValue())?"":am.getCrmCombodataByStatusid().getValue();
                    auditAction = AuditAction.ACTIVITY_DOC_UPLOAD;
                }
                else if (map.equals("-1")) {
                    details = " My - Document ";
                    auditAction = AuditAction.MY_DOC_UPLOAD;
                }
                auditTrailDAOObj.insertAuditLog(auditAction,
                        " Docment: '" + doc.getDocname() + "' , uploaded for " + details,
                        request, refid, id);
            }
            myjobj.put("ID", docID);
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } finally {
           // out.close();
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public Hashtable getDocumentDownloadHash(List ll) {
        Hashtable<String, String> ht = new Hashtable<String, String>();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                com.krawler.common.admin.Docmap cDocMap = (com.krawler.common.admin.Docmap) ite.next();
                ht.put("relatedto", cDocMap.getRelatedto());
                ht.put("recid", cDocMap.getRecid());

                com.krawler.common.admin.Docs t = cDocMap.getDocid();
                ht.put("docid", t.getDocid());
                ht.put("Name", t.getDocname());
                ht.put("Size", t.getDocsize());
                ht.put("Type", t.getDoctype());
                ht.put("svnname", t.getStorename());
                ht.put("storeindex", String.valueOf(t.getStorageindex()));
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return ht;
    }

    public ModelAndView downloadDocuments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String details = "";
        String auditAction = "";
        try {
            String url = request.getParameter("url");
            url = StringUtil.checkForNull(url);
            kmsg = crmDocumentDAOObj.downloadDocument(url);
            Hashtable ht = getDocumentDownloadHash(kmsg.getEntityList());
            String src = storageHandlerImplObj.GetDocStorePath();
//            String src = "/home/trainee/";
            if (request.getParameter("mailattch") != null) {
                    src = src + ht.get("svnname");
            } else {
                src = src + ht.get("userid").toString() + "/" + ht.get("svnname");
            }

            File fp = new File(src);
            byte[] buff = new byte[(int) fp.length()];
            FileInputStream fis = new FileInputStream(fp);
            int read = fis.read(buff);
            javax.activation.FileTypeMap mmap = new javax.activation.MimetypesFileTypeMap();
            response.setContentType(mmap.getContentType(src));
            response.setContentLength((int) fp.length());
            response.setHeader("Content-Disposition", request.getParameter("dtype") + "; filename=\"" + ht.get("Name") + "\";");
            response.getOutputStream().write(buff);
            response.getOutputStream().flush();
            response.getOutputStream().close();
            String map = ht.get("relatedto").toString();
            String refid = ht.get("recid").toString();

            if (map.equals("0")) {
                CrmCampaign c = (CrmCampaign) hibernateTemplate.get(CrmCampaign.class, refid);
                details = " Campaign - ";
                details+= StringUtil.isNullOrEmpty(c.getCampaignname())?"":c.getCampaignname();
                auditAction = AuditAction.CAMPAIGN_DOC_DOWNLOAD;
            } else if (map.equals("1")) {
                CrmLead l = (CrmLead) hibernateTemplate.get(CrmLead.class, refid);
                details = " Lead - ";
                details+= StringUtil.isNullOrEmpty(l.getFirstname())?"":l.getFirstname()+" ";
                details+= StringUtil.isNullOrEmpty(l.getLastname())?"":l.getLastname();
                auditAction = AuditAction.LEAD_DOC_DOWNLOAD;
            } else if (map.equals("2")) {
                CrmContact c = (CrmContact) hibernateTemplate.get(CrmContact.class, refid);
                details = " Contact - ";
                details+= StringUtil.isNullOrEmpty(c.getFirstname())?"":c.getFirstname()+" ";
                details+= StringUtil.isNullOrEmpty(c.getLastname())?"":c.getLastname();
                auditAction = AuditAction.CONTACT_DOC_DOWNLOAD;
            } else if (map.equals("3")) {
                CrmProduct p = (CrmProduct) hibernateTemplate.get(CrmProduct.class, refid);
                details = " Product - ";
                details+= StringUtil.isNullOrEmpty(p.getProductname())?"":p.getProductname();
                auditAction = AuditAction.PRODUCT_DOC_DOWNLOAD;
            } else if (map.equals("4")) {
                CrmAccount a = (CrmAccount) hibernateTemplate.get(CrmAccount.class, refid);
                details = " Account - ";
                details+= StringUtil.isNullOrEmpty(a.getAccountname())?"":a.getAccountname();
                auditAction = AuditAction.ACCOUNT_DOC_DOWNLOAD;
            } else if (map.equals("5")) {
                CrmOpportunity o = (CrmOpportunity) hibernateTemplate.get(CrmOpportunity.class, refid);
                details = " Opportunity - ";
                details+=StringUtil.isNullOrEmpty(o.getOppname())?"":o.getOppname();
                auditAction = AuditAction.OPPORTUNITY_DOC_DOWNLOAD;
            } else if (map.equals("6")) {
                CrmCase c = (CrmCase) hibernateTemplate.get(CrmCase.class, refid);
                details = " Case - ";
                details+= StringUtil.isNullOrEmpty(c.getSubject())?"":c.getSubject();
                auditAction = AuditAction.CASE_DOC_DOWNLOAD;
            } else if (map.equals("7")) {
                CrmActivityMaster am = (CrmActivityMaster) hibernateTemplate.get(CrmActivityMaster.class, refid);
                details = " Activity - ";
                details+= StringUtil.isNullOrEmpty(am.getFlag())?"":am.getFlag()+" ";
                details+= StringUtil.isNullOrEmpty(am.getCrmCombodataByStatusid().getValue())?"":am.getCrmCombodataByStatusid().getValue();
                auditAction = AuditAction.ACTIVITY_DOC_DOWNLOAD;
            }
            else if (map.equals("-1")) {
                details = " My - Document ";
                auditAction = AuditAction.MY_DOC_DOWNLOAD;
            }

            auditTrailDAOObj.insertAuditLog(auditAction,
                    " Document - '" + ht.get("Name").toString() + "' downloaded for " + details + " ",
                    request, ht.get("docid").toString());

            myjobj.put("success", true);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView downloadAttachment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String url = request.getParameter("url");
            url = StringUtil.checkForNull(url);
            kmsg = crmDocumentDAOObj.downloadDocument(url);
            Hashtable ht = getDocumentDownloadHash(kmsg.getEntityList());
            String src = storageHandlerImplObj.GetDocStorePath();
            if (request.getParameter("mailattch") != null) {
                if(Boolean.parseBoolean(request.getParameter("mailattch"))) {
                    src = storageHandlerImplObj.GetEmailUploadFilePath();
                    src = src + url;
                }
                else
                    src = src + ht.get("svnname");
            } else {
                src = src + ht.get("userid").toString() + "/" + ht.get("svnname");
            }

            File fp = new File(src);
            byte[] buff = new byte[(int) fp.length()];
            FileInputStream fis = new FileInputStream(fp);
            int read = fis.read(buff);
            javax.activation.FileTypeMap mmap = new javax.activation.MimetypesFileTypeMap();
            response.setContentType(mmap.getContentType(src));
            response.setContentLength((int) fp.length());
            response.setHeader("Content-Disposition", request.getParameter("dtype") + "; filename=\"" + request.getParameter("fname") + "\";");
            response.getOutputStream().write(buff);
            response.getOutputStream().flush();
            response.getOutputStream().close();
            myjobj.put("success", true);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public JSONObject getDocumentListJson(List ll, HttpServletRequest request, int totalSize,String start,String limit) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            int strt = Integer.parseInt(start);
            int lmt = Integer.parseInt(limit);
            int end = Math.min(ll.size(), strt+lmt);
            Iterator ite = ll.iterator();
            String[] ownerinfo;
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            for(int i = strt ; i < end ; i++ ){
                try{
                JSONObject temp = new JSONObject();
                if(ll.get(i)!=null){
                com.krawler.common.admin.Docmap t = (com.krawler.common.admin.Docmap) ll.get(i);
                Docs cd = t.getDocid();
                temp.put(JSON_docid, cd.getDocid());

                temp.put(JSON_name, cd.getDocname());
                temp.put(JSON_size, cd.getDocsize());
                temp.put(JSON_type, cd.getDoctype());
                temp.put(JSON_uploadeddate, cd.getUploadedOn()!=null?dateFormat.format(cd.getUploadedon()):"");
                if(cd.getUserid()==null){
                	temp.put(JSON_uploadername, crmDocumentDAOObj.getDocUploadedCustomername(t.getDocid().getDocid()));
                }else{
                	temp.put(JSON_uploadername, profileHandlerDAOObj.getUserFullName(cd.getUserid().getUserID()));
                	temp.put(JSON_userid, cd.getUserid().getUserID());
                }
                temp.put(JSON_Tags, cd.getTags());
                switch (Integer.parseInt(t.getRelatedto())) {
                    case -1:
                        temp.put(JSON_relatedname, "Personal");
                        temp.put(JSON_relatedto, "CRM");
                        temp.put(JSON_author, profileHandlerDAOObj.getUserFullName(cd.getUserid().getUserID()));
                        break;
                    case 0:
                        CrmCampaign camp = (CrmCampaign) KwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CrmCampaign", t.getRecid());
                        if(camp!=null){
                        temp.put(JSON_relatedname, camp.getCampaignname());
                        temp.put(JSON_relatedto, "Campaign");
                        temp.put(JSON_author, StringUtil.getFullName(camp.getUsersByUserid()));
                        }
                        break;
                    case 1:
                        CrmLead lead = (CrmLead) KwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CrmLead", t.getRecid());
                        if(lead!=null){
                        temp.put(JSON_relatedname,  lead.getLastname());
                        temp.put(JSON_relatedto, "Leads");
                        ownerinfo=crmLeadHandler.getAllLeadOwners(crmLeadDAOObj, lead.getLeadid());
                          temp.put(JSON_author, ownerinfo[0]);
                        }
                        break;
                    case 2:
                        CrmContact contact = (CrmContact) KwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CrmContact", t.getRecid());;
                        if(contact!=null){
                        temp.put(JSON_relatedname, contact.getFirstname() + " " + contact.getLastname());
                        temp.put(JSON_relatedto, "Contacts");
                        ownerinfo=crmContactHandler.getAllContactOwners(crmContactDAOObj,contact.getContactid());
                        temp.put(JSON_author, ownerinfo[0]);
                        }
                        break;
                    case 3:
                        CrmProduct product = (CrmProduct) KwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CrmProduct", t.getRecid());;;
                        if(product!=null){
                        temp.put(JSON_relatedname, product.getProductname());
                        temp.put(JSON_relatedto, "Product");
                        temp.put(JSON_author, StringUtil.getFullName(product.getUsersByUserid()));
                        }
                        break;
                    case 4:
                        CrmAccount account = (CrmAccount) hibernateTemplate.get(CrmAccount.class, t.getRecid());
                        if(account!=null){
                        temp.put(JSON_relatedname, account.getAccountname());
                        temp.put(JSON_relatedto, "Account");
                          ownerinfo=crmAccountHandler.getAllAccOwners(crmAccountDAOObj,account.getAccountid());
                          temp.put(JSON_author, ownerinfo[0]);
                        }
                        break;
                    case 5:
                        CrmOpportunity opportunity = (CrmOpportunity) hibernateTemplate.get(CrmOpportunity.class, t.getRecid());
                        if(opportunity!=null){
                        temp.put(JSON_relatedname, opportunity.getOppname());
                        temp.put(JSON_relatedto, "Opportunity");
                            ownerinfo=crmOpportunityHandler.getAllOppOwners(crmOpportunityDAOObj,opportunity.getOppid());
                            temp.put(JSON_author, ownerinfo[0]);
                        }
                        break;
                    case 6:
                        CrmCase cases = (CrmCase) hibernateTemplate.get(CrmCase.class, t.getRecid());
                        if(cases!=null){
                        temp.put(JSON_relatedname, cases.getSubject());
                        temp.put(JSON_relatedto, "Cases");
                       	temp.put(JSON_author, cases.getUsersByUserid()!=null?StringUtil.getFullName(cases.getUsersByUserid()):"");
                        }
                        break;
                    case 7:
                        CrmActivityMaster activity = (CrmActivityMaster) hibernateTemplate.get(CrmActivityMaster.class, t.getRecid());
                        if(activity!=null){
                        temp.put(JSON_relatedname, activity.getCrmCombodataByStatusid().getValue());
                        temp.put(JSON_relatedto, "Activity");
                        temp.put(JSON_author, StringUtil.getFullName(activity.getUsersByUserid()));
                        }
                        break;
                }
                }
                jarr.put(temp);
                }  catch (Exception e) {
                    logger.warn(e.getMessage(),e);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }

    public ModelAndView getDocumentList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        KwlReturnObject kmsgShared = null;
        try {
            String tag = request.getParameter("tag");
            String ss = StringUtil.checkForNull(request.getParameter("ss"));
            String userid = sessionHandlerImplObj.getUserid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("tag", StringUtil.checkForNull(tag));
            requestParams.put("ss", ss);
            boolean tagSearch = false;
            if(!StringUtil.isNullOrEmpty(request.getParameter("tagSearch"))){
                requestParams.put("tagSearch", request.getParameter("tagSearch"));
                tagSearch = request.getParameter("tagSearch").equalsIgnoreCase("true");
            }
            String start = StringUtil.checkForNull(request.getParameter("start"));
            String limit = StringUtil.checkForNull(request.getParameter("limit"));
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));

            boolean campaign_module = false;
            boolean lead_module = false;
            boolean account_module = false;
            boolean contact_module = false;
            boolean opportunity_module = false;
            boolean case_module = false;
            boolean product_module = false;
            boolean activity_module = false;

            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.campaignFName) & 1) == 1) {
                campaign_module = true;
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.leadFName) & 1) == 1) {
                lead_module = true;
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.accountFName) & 1) == 1) {
                account_module = false;
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.contactFName) & 1) == 1) {
                contact_module = true;
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.opportunityFName) & 1) == 1) {
                opportunity_module = true;
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.caseFName) & 1) == 1) {
                case_module = true;
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.productFName) & 1) == 1) {
                product_module = true;
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.activityFName) & 1) == 1) {
                activity_module = true;
            }

            requestParams.put("campaign_module", campaign_module);
            requestParams.put("lead_module", lead_module);
            requestParams.put("account_module", account_module);
            requestParams.put("contact_module", contact_module);
            requestParams.put("opportunity_module", opportunity_module);
            requestParams.put("case_module", case_module);
            requestParams.put("product_module", product_module);
            requestParams.put("activity_module", activity_module);

            boolean campaign_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            boolean lead_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            boolean account_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            boolean contact_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            boolean opp_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            boolean product_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            boolean case_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Case");
            boolean activity_heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Activity");

            requestParams.put("campaign_heirarchyPerm", campaign_heirarchyPerm);
            requestParams.put("lead_heirarchyPerm", lead_heirarchyPerm);
            requestParams.put("account_heirarchyPerm", account_heirarchyPerm);
            requestParams.put("contact_heirarchyPerm", contact_heirarchyPerm);
            requestParams.put("opp_heirarchyPerm", opp_heirarchyPerm);
            requestParams.put("product_heirarchyPerm", product_heirarchyPerm);
            requestParams.put("case_heirarchyPerm", case_heirarchyPerm);
            requestParams.put("activity_heirarchyPerm", activity_heirarchyPerm);

            String searchType = request.getParameter("searchType");
            searchType = searchType==null?"":searchType;
            if(searchType.equalsIgnoreCase("0") || tagSearch || (searchType.equals("1") && StringUtil.isNullOrEmpty(ss))){ //0=search by Name OR Tag

                kmsg = crmDocumentDAOObj.getDocumentList(requestParams, usersList);
                StringBuffer docIDs =getDocIds(kmsg.getEntityList());

                kmsgShared = crmDocumentDAOObj.getSharedDocumentList(requestParams,kmsg.getEntityList(), userid,docIDs);

                jobj = getDocumentListJson(kmsgShared.getEntityList(), request, kmsgShared.getRecordTotalCount(),start,limit);
            } else if(searchType.equalsIgnoreCase("1")){ //1=search by Content
                StringBuffer usersListIds = crmManagerDAOObj.recursiveUserIds(userid);
                requestParams.put("usersListIds", usersListIds);
                kmsg = documentHeplerObj.documentIndexSearch(requestParams);
                jobj = (JSONObject) kmsg.getEntityList().get(0);
            }
        } catch (Exception e) {
        logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView addTag(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String tag = request.getParameter("tag");
            String newTag = request.getParameter("newTag");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("tag", StringUtil.checkForNull(tag));

            kmsg = crmDocumentDAOObj.addTag(requestParams);
            Docs d = (Docs) kmsg.getEntityList().get(0);
            auditTrailDAOObj.insertAuditLog(com.krawler.common.admin.AuditAction.DOC_TAG_ADDED,
                    " Document - '" + newTag + "' tag added for "+d.getDocname(),
                    request, d.getDocid());
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deletedocument(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        String docid = "";
        if(request.getParameter("docid")!=null)
            docid = request.getParameter("docid");
        try {
            crmDocumentDAOObj.deletedocument(docid);
//            auditTrailDAOObj.insertAuditLog(AuditAction.DOC_TAG_ADDED,
//                    " Document - '" + newTag + "' tag added for "+d.getDocname(),
//                    request, d.getDocid());
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView reloadDocumentLuceneIndex(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        StringBuilder msg = new StringBuilder();
        int batchSize = 50;
        int i = 1;
        int totalIndexed = 0, totalDocs = 0;
        try {
            msg.append("LU(ENE Reload started at ");msg.append(new Date());
            if(crmDocumentDAOObj.checkReloadDocumentIndex()){
                String indexPath = storageHandlerImpl.GetDocIndexPath();
                String storePath = storageHandlerImpl.GetDocStorePath();
                ServletContext servletContext = getServletContext();

                //Delete all old indexed file
                LuceneSearchObj.clearIndex(indexPath);

                ArrayList<Document> luceneDocuments = new ArrayList<Document>();

                kmsg = crmDocumentDAOObj.getReloadDocumentLuceneIndex();
                List<Docs> docsList = kmsg.getEntityList();
                totalDocs = kmsg.getRecordTotalCount();

                for (Docs doc : docsList) {
                    try {
                        String filePath = storePath + doc.getStorename();
                        File uploadFile = new File(filePath);

                        FileInputStream fin = null;
                        byte[] b = null;
                        try {
                            fin = new FileInputStream(uploadFile);
                            b = new byte[(int) uploadFile.length()];
                            fin.read(b);
                        } finally {
                            if (fin != null) {
                                fin.close();
                            }
                        }

                        String fileType = servletContext.getMimeType(filePath);
                        String contentType = KrawlerApp.getContentType(fileType, filePath, b);
                        int flag1 = 0;
                        if (!StringUtil.isNullOrEmpty(contentType)) {
                            if (contentType.equals("application/vnd.ms-excel") ||
                                    contentType.equals("application/msword") || contentType.equals("application/vnd.ms-word") ||
                                    contentType.equals("application/vnd.ms-powerpoint") ||
                                    contentType.equals("text/plain") || contentType.equals("text/csv") || contentType.equals("text/xml") || contentType.equals("text/css") || contentType.equals("text/html") || contentType.equals("text/cs") || contentType.equals("text/x-javascript") || contentType.equals("File") ||
                                    contentType.equals("application/pdf") ||
                                    contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                                    contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                                flag1 = 1;
                            }
                        }
                        if (flag1 == 1) {
                            ArrayList<String> indexNames = new ArrayList<String>();
                            ArrayList<Object> indexValues = new ArrayList<Object>();

                            String companyId = doc.getCompany().getCompanyID();
                            indexNames.add(LuceneSearchConstants.DOCUMENT_FileName);
                            indexValues.add(doc.getDocname());
                            indexNames.add(LuceneSearchConstants.DOCUMENT_Author);
                            indexValues.add(doc.getUserid().getUserId());
                            indexNames.add(LuceneSearchConstants.DOCUMENT_DocumentId);
                            indexValues.add(doc.getDocid());
                            indexNames.add(LuceneSearchConstants.DOCUMENT_CompanyId);
                            indexValues.add(companyId);
                            String plaintext = LuceneSearchObj.parseDocument(filePath, contentType);
                            indexNames.add(LuceneSearchConstants.DOCUMENT_PlainText);
                            indexValues.add(plaintext);
                            indexNames.add(LuceneSearchConstants.DOCUMENT_IndexedText);
                            indexValues.add(plaintext.toLowerCase());//All lower case for comparision

                            Document luceneDoc = LuceneSearchObj.createLuceneDocument(indexValues, indexNames, new ArrayList<String>());
                            luceneDocuments.add(luceneDoc);
                            totalIndexed++;
                        }
                    } catch (Exception ex) {//Skip file if not found OR any exception
                    }

                    if (i % batchSize == 0) { //Index in batch
                        LuceneSearchObj.writeIndex(luceneDocuments, indexPath);
                        luceneDocuments = new ArrayList<Document>();
                    }
                    i++;
                }

                if (luceneDocuments.size() > 0) { //Index remaining entries
                    LuceneSearchObj.writeIndex(luceneDocuments, indexPath);
                }
                crmDocumentDAOObj.resetDocumentIndexFlag();//Make reloadIndex=0
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            msg.append(", LU(ENE Reload Exception: ");msg.append(e.getMessage());
        }
        msg.append(", Total Docs=");msg.append(totalDocs);
        msg.append(", completed at");msg.append(new Date());
        msg.append(", Indexed ");msg.append(totalIndexed);msg.append(" documents");

        return new ModelAndView("jsonView", "model", msg.toString());
    }

    public ModelAndView getExistingDocOwners(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
       try{
            String[] ownerInfo = getAllDocOwners(request.getParameter("docid"));
            jobj.put("mainOwner",ownerInfo[2] );
            jobj.put("ownerids", ownerInfo[3]);
       } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    public String[] getAllDocOwners(String docid) throws ServiceException {

        KwlReturnObject kmsg = null;
        kmsg = crmDocumentDAOObj.getDocumentOwners(docid);
        Iterator ite = kmsg.getEntityList().iterator();
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        while(ite.hasNext()){
             DocOwners docOwnersObj=(DocOwners) ite.next();
            if(docOwnersObj.isMainOwner()){
                mainLeadOwner=docOwnersObj.getUsersByUserid();
            }else{
                ownerId+=docOwnersObj.getUsersByUserid().getUserID()+",";
                ownerNames+=profileHandler.getUserFullName(docOwnersObj.getUsersByUserid())+", ";
            }
        }
        String mainOwner = "";
        if(mainLeadOwner!=null)
          mainOwner=profileHandler.getUserFullName(mainLeadOwner);

        String tooltip;
        String gridName;
        if(!StringUtil.isNullOrEmpty(ownerNames)){
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerId = ownerId.substring(0,ownerId.length()-1);
            tooltip="<b>"+mainOwner+"</b>, "+ownerNames+".";
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+"</b>.";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Opportunities'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }

    public ModelAndView saveDocOwners(HttpServletRequest request, HttpServletResponse response)
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
            String docid = request.getParameter("leadid");
            String owners = request.getParameter("owners");
            String mainowner = request.getParameter("mainOwner");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("docid", docid);
            requestParams.put("owners", owners);
            requestParams.put("mainOwner", mainowner);
            kmsg = crmDocumentDAOObj.saveDocOwners(requestParams);

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

    public StringBuffer getDocIds(List ll) throws ServiceException {
        StringBuffer docsIdList = new StringBuffer();
        try {
            Iterator ite = ll.iterator();
            int count=1;
            while(ite.hasNext()){
                com.krawler.common.admin.Docmap t = (com.krawler.common.admin.Docmap) ite.next();
                Docs cd = t.getDocid();
                docsIdList.append("'" + cd.getDocid() + "'");
                if(count!=ll.size()){
                    docsIdList.append(",");
                }
                count++;

            }

        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.recursiveUsers", e);
        }
        return docsIdList;
    }
    public ModelAndView deleteDocumentFromModule(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject jobj = new JSONObject();
		KwlReturnObject kmsg = null;
		//Create transaction
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		String docid = "";
		 String details = "";
	     String auditAction = "";
		if(request.getParameter("docid")!=null)
		docid = request.getParameter("docid");
		String map = request.getParameter("mapid");
        String refId = request.getParameter("recid");
        String docName = request.getParameter("docName");
		try {
		    crmDocumentDAOObj.deleteDocumentFromModule(docid);
		    if (map.equals("0")) {
                CrmCampaign c = (CrmCampaign) hibernateTemplate.get(CrmCampaign.class, refId);
                details = " Campaign - ";
                details+= StringUtil.isNullOrEmpty(c.getCampaignname())?"":c.getCampaignname();
                auditAction = AuditAction.CAMPAIGN_DOC_DELETED;
            } else if (map.equals("1")) {
                CrmLead l = (CrmLead) hibernateTemplate.get(CrmLead.class, refId);
                details = " Lead - ";
                details+= StringUtil.isNullOrEmpty(l.getFirstname())?"":l.getFirstname()+" ";
                details+= StringUtil.isNullOrEmpty(l.getLastname())?"":l.getLastname();
                auditAction = AuditAction.LEAD_DOC_DELETED;
            } else if (map.equals("2")) {
                CrmContact c = (CrmContact) hibernateTemplate.get(CrmContact.class, refId);
                details = " Contact - ";
                details+= StringUtil.isNullOrEmpty(c.getFirstname())?"":c.getFirstname()+" ";
                details+= StringUtil.isNullOrEmpty(c.getLastname())?"":c.getLastname();
                auditAction = AuditAction.CONTACT_DOC_DELETED;
            } else if (map.equals("3")) {
                CrmProduct p = (CrmProduct) hibernateTemplate.get(CrmProduct.class, refId);
                details = " Product - ";
                details+= StringUtil.isNullOrEmpty(p.getProductname())?"":p.getProductname();
                auditAction = AuditAction.PRODUCT_DOC_DELETED;
            } else if (map.equals("4")) {
                CrmAccount a = (CrmAccount) hibernateTemplate.get(CrmAccount.class, refId);
                details = " Account - ";
                details+= StringUtil.isNullOrEmpty(a.getAccountname())?"":a.getAccountname();
                auditAction = AuditAction.ACCOUNT_DOC_DELETED;
            } else if (map.equals("5")) {
                CrmOpportunity o = (CrmOpportunity) hibernateTemplate.get(CrmOpportunity.class, refId);
                details = " Opportunity - ";
                details+=StringUtil.isNullOrEmpty(o.getOppname())?"":o.getOppname();
                auditAction = AuditAction.OPPORTUNITY_DOC_DELETED;
            } else if (map.equals("6")) {
                CrmCase c = (CrmCase) hibernateTemplate.get(CrmCase.class, refId);
                details = " Case - ";
                details+= StringUtil.isNullOrEmpty(c.getSubject())?"":c.getSubject();
                auditAction = AuditAction.CASE_DOC_DELETED;
            } else if (map.equals("7")) {
                CrmActivityMaster am = (CrmActivityMaster) hibernateTemplate.get(CrmActivityMaster.class, refId);
                details = " Activity - ";
                details+= StringUtil.isNullOrEmpty(am.getFlag())?"":am.getFlag()+" ";
                details+= StringUtil.isNullOrEmpty(am.getCrmCombodataByStatusid().getValue())?"":am.getCrmCombodataByStatusid().getValue();
                auditAction = AuditAction.ACTIVITY_DOC_DELETED;
            }
            else if (map.equals("-1")) {
                details = " My - Document ";
                auditAction = AuditAction.MY_DOC_DELETED;
            }
            auditTrailDAOObj.insertAuditLog(auditAction,
                    " Docment: '" + docName + "' , deleted for " + details,
                    request, refId, docid);
        
		//    auditTrailDAOObj.insertAuditLog(AuditAction.DOC_TAG_ADDED,
		//            " Document - '" + newTag + "' tag added for "+d.getDocname(),
		//            request, d.getDocid());
		    txnManager.commit(status);
		} catch (Exception e) {
		    logger.warn(e.getMessage(),e);
		    txnManager.rollback(status);
		}
		return new ModelAndView("jsonView", "model", jobj.toString());
}

}
