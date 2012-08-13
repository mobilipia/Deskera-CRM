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
package com.krawler.spring.crm.targetModule;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.database.tables.TargetModule;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class crmTargetController extends MultiActionController {
    private crmTargetDAO crmTargetDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private sessionHandlerImpl sessionHandlerImpl;

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImpl1) {
        this.sessionHandlerImpl = sessionHandlerImpl1;
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

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setcrmTargetDAO(crmTargetDAO crmTargetDAOObj1) {
        this.crmTargetDAOObj = crmTargetDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public JSONObject getTargetJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                TargetModule obj = (TargetModule) ite.next();

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("targetModuleid", obj.getId());
                tmpObj.put("targetModuleowner", obj.getFirstname());
                tmpObj.put("targetModuleownerid", obj.getUsersByUserid().getUserID());
                tmpObj.put("owner", obj.getUsersByUserid().getFirstName() + " " + obj.getUsersByUserid().getLastName());
                tmpObj.put("firstname", StringUtil.checkForNull(obj.getFirstname()));
                tmpObj.put("lastname", StringUtil.checkForNull(obj.getLastname()));
                tmpObj.put("phoneno", obj.getPhoneno()!=null?obj.getPhoneno():"");
                tmpObj.put("mobileno", obj.getMobileno() != null ? obj.getMobileno() : "");
                tmpObj.put("email", StringUtil.checkForNull(obj.getEmail()));
                tmpObj.put("address", obj.getAddress() != null?obj.getAddress():"");
                tmpObj.put("description", obj.getDescription() != null ? obj.getDescription() : "");
                tmpObj.put("createdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
                tmpObj.put("validflag", obj.getValidflag());

                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
       } catch(Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }

    public ModelAndView getTargets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", request.getParameter("isarchive"));
            if(request.getParameter("searchJson") != null && !StringUtil.isNullOrEmpty(request.getParameter("searchJson"))) {
                requestParams.put("searchJson", request.getParameter("searchJson"));
            }
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            if (request.getParameter("config") != null) {
                requestParams.put("config", request.getParameter("config"));
            }

            if(!StringUtil.isNullOrEmpty(request.getParameter("field"))) {
                requestParams.put("field", request.getParameter("field"));
                requestParams.put("direction", request.getParameter("direction"));
                requestParams.put("iscustomcolumn", request.getParameter("iscustomcolumn"));
                requestParams.put("xfield", request.getParameter("xfield"));
                requestParams.put("xtype", request.getParameter("xtype"));
            }
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            
            kmsg = crmTargetDAOObj.getTargets(requestParams, usersList);
            jobj = getTargetJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getTargetToEmailJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                TargetModule obj = (TargetModule) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("relatedto", 4);
                tmpObj.put("relatedid", obj.getId());
                tmpObj.put("name", obj.getFirstname() + " " + obj.getLastname());
                tmpObj.put("emailid", obj.getEmail());

                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
       } catch(Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }

    // Not Used
    
//    public ModelAndView getTargetsToEmail(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException {
//        JSONObject jobj = new JSONObject();
//        KwlReturnObject kmsg = null;
//        try {
//            String userid = sessionHandlerImpl.getUserid(request);
//            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
//            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            requestParams.put("isarchive", false);
//            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
////            requestParams.put("export", true);
//            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
//                requestParams.put("ss", request.getParameter("ss"));
//            }
//            requestParams.put("start", request.getParameter("start"));
//            requestParams.put("limit", request.getParameter("limit"));
//            requestParams.put("config", true);
//            requestParams.put("email", true);
//
//            kmsg = crmTargetDAOObj.getTargets(requestParams, usersList);
//            jobj = getTargetToEmailJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
//        } catch (Exception e) {
//            logger.warn(e.getMessage(),e);
//        }
//        return new ModelAndView("jsonView", "model", jobj.toString());
//    }
    
    public ModelAndView getAllTargets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
       try{
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            JSONArray jarr = new JSONArray();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.validflag");
            filter_params.add(1);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            filter_names.add("c.isarchive");
            filter_params.add(false);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            kmsg = crmTargetDAOObj.getAllTargets(requestParams, usersList, filter_names, filter_params);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                TargetModule obj = (TargetModule) ite.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("cusername", obj.getFirstname() + " " + obj.getLastname());
                jtemp.put("cuserid", obj.getId());
                jtemp.put("cemailid", obj.getEmail());
                jtemp.put("caddress", obj.getAddress());
                jtemp.put("ccontactno", obj.getPhoneno());
                jarr.put(jtemp);
            }
            jobj.put("data", jarr);
       } catch(Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView targetExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", request.getParameter("isarchive"));
            if(request.getParameter("searchJson") != null && !StringUtil.isNullOrEmpty(request.getParameter("searchJson"))) {
                requestParams.put("searchJson", request.getParameter("searchJson"));
            }
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            if (request.getParameter("config") != null) {
                requestParams.put("config", request.getParameter("config"));
            }

            if(!StringUtil.isNullOrEmpty(request.getParameter("field"))) {
                requestParams.put("field", request.getParameter("field"));
                requestParams.put("direction", request.getParameter("direction"));
                requestParams.put("iscustomcolumn", request.getParameter("iscustomcolumn"));
                requestParams.put("xfield", request.getParameter("xfield"));
                requestParams.put("xtype", request.getParameter("xtype"));
            }
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            
            kmsg = crmTargetDAOObj.getTargets(requestParams, usersList);
            jobj = getTargetJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.TARGET_EXPORT,
                    "Target data exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView saveTargets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        TargetModule target = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try{
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String id = jobj.getString("targetModuleid");
            jobj.put("userid", sessionHandlerImpl.getUserid(request));
            jobj.put("companyid", sessionHandlerImpl.getCompanyid(request));
            jobj.put("updatedon", new Date().getTime());
            if (id.equals("0")) {
                id = java.util.UUID.randomUUID().toString();
                jobj.put("createdon", new Date().getTime());
                jobj.put("targetModuleid", id);

                kmsg = crmTargetDAOObj.addTargets(jobj);
                target = (TargetModule) kmsg.getEntityList().get(0);
                if (target.getValidflag() == 1) {
                    auditTrailDAOObj.insertAuditLog(AuditAction.TARGET_CREATE,
                            target.getFirstname() + " " + target.getLastname() + " - Target created ",
                            request, id);
                }
            } else {
                kmsg = crmTargetDAOObj.editTargets(jobj);
                target = (TargetModule) kmsg.getEntityList().get(0);
                if (target.getValidflag() == 1) {
                    auditTrailDAOObj.insertAuditLog(AuditAction.TARGET_UPDATE,
                            jobj.getString("auditstr") + " Target - " + target.getFirstname() + " " + target.getLastname() + " ",
                            request, id);
                }
            }
            myjobj.put("success", true);
            myjobj.put("ID", target.getId());
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView newTargetAddress(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        TargetModule target = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = request.getParameter("userid");
            String[] username = request.getParameter("username").split(" ");
            String fname = username[0];
            String lname = username[1];
            String emailid = request.getParameter("emailid");
            String address = request.getParameter("address");
            String contactno = request.getParameter("contactno");
            String id = java.util.UUID.randomUUID().toString();
            JSONObject jobj = new JSONObject();

            jobj.put("targetModuleid", id);
            jobj.put("companyid", companyid);
            jobj.put("targetModuleownerid", userid);
            jobj.put("userid", userid);
            jobj.put("firstname", fname);
            jobj.put("lastname", lname);
            jobj.put("phone", contactno);
            jobj.put("address", address);
            jobj.put("validflag", 1);
            jobj.put("email", emailid);
            jobj.put("createdon", new Date().getTime());
            kmsg = crmTargetDAOObj.addTargets(jobj);
            target = (TargetModule) kmsg.getEntityList().get(0);

            myjobj.put("success", true);
            myjobj.put("ID", target.getId());
            txnManager.commit(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView repTarget(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        String jsondata = request.getParameter("val");
        KwlReturnObject kmsg = null;
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
                String recid = jobj.getString("targetModuleid");
                String lname="";
                String fname="";

                int pos=jobj.getString("username").indexOf(' ');
                if(pos==0)
                    lname=jobj.getString("username").trim();
                else if(pos==jobj.getString("username").length()){
                    fname=jobj.getString("username").trim();
                } else {
                    String[] username=jobj.getString("username").split(" ");
                    fname=username[0];
                    lname=username[1];
                }
                String emailid = jobj.getString("emailid");
                String contactno = jobj.getString("contactno");

                jobj.put("firstname", fname);
                jobj.put("lastname", lname);
                jobj.put("email", emailid);
                jobj.put("phone", contactno);
                kmsg = crmTargetDAOObj.editTargets(jobj);
                myjobj.append("rec", jobj);
            }
            txnManager.commit(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
}
