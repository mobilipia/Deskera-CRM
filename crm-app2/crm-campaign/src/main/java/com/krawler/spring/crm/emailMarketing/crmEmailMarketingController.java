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
package com.krawler.spring.crm.emailMarketing;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gdata.data.introspection.Collection;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.database.tables.CampaignLog;
import com.krawler.crm.database.tables.CampaignTarget;
import com.krawler.crm.database.tables.CampaignTimeLog;
import com.krawler.crm.database.tables.DefaultTemplates;
import com.krawler.crm.database.tables.EmailMarketing;
import com.krawler.crm.database.tables.EmailMarketingDefault;
import com.krawler.crm.database.tables.EmailMarkteingTargetList;
import com.krawler.crm.database.tables.EmailTemplate;
import com.krawler.crm.database.tables.EmailTemplateFiles;
import com.krawler.crm.database.tables.EnumEmailType;
import com.krawler.crm.database.tables.TargetList;
import com.krawler.crm.database.tables.TargetListTargets;
import com.krawler.crm.database.tables.UrlTrackLog;
import com.krawler.crm.database.tables.colorThemeGroup;
import com.krawler.crm.database.tables.scheduledmarketing;
import com.krawler.crm.database.tables.templateColorTheme;
import com.krawler.crm.database.tables.themeImages;
import com.krawler.crm.utils.AuditAction;
import com.krawler.crm.utils.Constants;
import com.krawler.esp.handlers.APICallHandlerService;
import com.krawler.esp.handlers.Receiver;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.esp.web.resource.Links;
import com.krawler.notify.NotificationException;
import com.krawler.notify.SenderCache;
import com.krawler.notify.email.EmailNotification;
import com.krawler.notify.email.EmailSender;
import com.krawler.notify.email.SimpleEmailNotification;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class crmEmailMarketingController extends MultiActionController implements ApplicationListener<ContextRefreshedEvent> ,MessageSourceAware{
    private crmEmailMarketingDAO crmEmailMarketingDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private auditTrailDAO auditTrailDAOObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private profileHandlerDAO profileHandlerDAOObj;
    private bounceHandlerImpl bounceHandlerImplObj;
	private APICallHandlerService apiCallHandlerService;
	private boolean appStarted=false;
	private MessageSource mSource;
	

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    
    public void setApiCallHandlerService(APICallHandlerService apiCallHandlerService) {
		this.apiCallHandlerService = apiCallHandlerService;
	}

	public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setcrmEmailMarketingDAO(crmEmailMarketingDAO crmEmailMarketingDAOObj1) {
        this.crmEmailMarketingDAOObj = crmEmailMarketingDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }
    public void setbounceHandlerImpl(bounceHandlerImpl bounceHandlerImplObj) {
        this.bounceHandlerImplObj = bounceHandlerImplObj;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public ModelAndView getEmailTemplateList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            Company companyObj = (Company) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("templateList", request.getParameter("templateList"));
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            requestParams.put("companyid", companyid);
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("sort"))) {
                requestParams.put("field", request.getParameter("sort"));
                requestParams.put("direction", request.getParameter("dir"));
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("defaultOnly"))) {
                requestParams.put("defaultOnly", request.getParameter("defaultOnly"));
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("excludeDefault"))) {
                requestParams.put("excludeDefault", request.getParameter("excludeDefault"));
            }
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getEmailTemplateList(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                crmEmailTemplateInterface obj = (crmEmailTemplateInterface) ite.next();
                JSONObject jtemp = new JSONObject();
                String classname = obj.getClass().getSimpleName();
                Long createdon = obj.getCreatedOn();
                if(StringUtil.equal(classname, Constants.EMAIL_TEMPLATE_DEFAULT_TYPE)){
                    createdon = companyObj.getCreatedon();
                }
                jtemp.put("templateid",obj.getTemplateid());
                jtemp.put("templatename",obj.getName());
                jtemp.put("description",obj.getDescription());
                jtemp.put("subject",obj.getSubject());
                jtemp.put("thumbnail",obj.getThumbnail());
//                jtemp.put("bodyhtml",obj.getBody_html());
                jtemp.put("createdon", createdon!=null?createdon:"");
                jtemp.put("templateclass",classname);
                jarr.put(jtemp);
            }
            jobj.put("totalCount", kmsg.getRecordTotalCount());
            jobj.put("data", jarr);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getEmailTypeList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("sort"))) {
                requestParams.put("field", request.getParameter("sort"));
                requestParams.put("direction", request.getParameter("dir"));
            }
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getEmailTypeList(requestParams);
            List<EnumEmailType> enumEmailList = kmsg.getEntityList();
            for(EnumEmailType eet : enumEmailList) {

                JSONObject jtemp = new JSONObject();
                jtemp.put("templateid",eet.getTypeid());
                jtemp.put("templatename",eet.getName());
                jtemp.put("description",eet.getDescription());
                jtemp.put("subject",eet.getSubject());
                jtemp.put("bodyhtml",eet.getBody_html());
                jtemp.put("plaintext",eet.getPlaintext());
                jtemp.put("createdon",eet.getCreatedOn()!=null?eet.getCreatedOn():"");
                jarr.put(jtemp);
            }
            jobj.put("totalCount", kmsg.getRecordTotalCount());
            jobj.put("data", jarr);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView saveEmailTemplate(HttpServletRequest request, HttpServletResponse response)
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
            myjobj.put("success", false);
            int mode = Integer.parseInt(request.getParameter("mode"));
            String templateClass = request.getParameter("templateclass");
            JSONObject jobj = new JSONObject();
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String templatename = request.getParameter("tname");
            templatename = templatename.replaceAll("\\s+"," ").trim();
            jobj.put("userid", sessionHandlerImpl.getUserid(request));
            jobj.put("tbody", request.getParameter("tbody"));
            jobj.put("description", request.getParameter("tdesc"));
            jobj.put("subject", request.getParameter("tsub"));
            jobj.put("name", templatename);
            
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.name");
            filter_params.add(templatename);
            filter_names.add("c.creator.company.companyID");
            filter_params.add(companyid);
            filter_names.add("c.deleted");
            filter_params.add(0);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);

            KwlReturnObject kmsg1 = crmEmailMarketingDAOObj.getTemplateContent(requestParams);
            if(kmsg1.getRecordTotalCount()>0 && (mode==0 || templateClass.equals("DefaultTemplates"))){
                myjobj.put("msg", "Email template with ' "+templatename+" ' name already exists. Please change the name and save it again.");
            }else {
                if (mode == 0) {
                    jobj.put("modifiedon", new Date().getTime());
                    jobj.put("createdon", new Date().getTime());
                    kmsg = crmEmailMarketingDAOObj.addEmailTemplate(jobj);
                } else {
                    jobj.put("modifiedon", new Date().getTime());
                    jobj.put("deleted", "0");
                    if(templateClass.equals("DefaultTemplates")) {
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("userid", sessionHandlerImpl.getUserid(request));
                        params.put("templateid", request.getParameter("tid"));
                        KwlReturnObject copyMsg = crmEmailMarketingDAOObj.copyDefaultTemplates(params);
                        List templateid = copyMsg.getEntityList();
                        jobj.put("tid", templateid.get(0));
                    } else {
                        jobj.put("tid", request.getParameter("tid"));
                    }
                    kmsg = crmEmailMarketingDAOObj.editEmailTemplate(jobj);
                }
            }
            myjobj.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView saveEmailType(HttpServletRequest request, HttpServletResponse response)
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
            myjobj.put("success", false);
            JSONObject jobj = new JSONObject();
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String templatename = request.getParameter("tname");
            templatename = templatename.replaceAll("\\s+"," ").trim();
            jobj.put("userid", sessionHandlerImpl.getUserid(request));
            jobj.put("tbody", request.getParameter("tbody"));
            jobj.put("description", request.getParameter("tdesc"));
            jobj.put("subject", request.getParameter("tsub"));
            jobj.put("name", templatename);
            jobj.put("plaintext", request.getParameter("plaintext"));
            jobj.put("companyid", companyid);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.name");
            filter_params.add(templatename);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            KwlReturnObject kmsg1 = crmEmailMarketingDAOObj.getEmailTypeContent(requestParams);
            if(kmsg1.getRecordTotalCount()>0 ) {
                List<EnumEmailType> enumEmailList = kmsg1.getEntityList();
                for(EnumEmailType eet : enumEmailList) {
                    jobj.put("modifiedon", new Date().getTime());
                    jobj.put("tid", eet.getTypeid());
                }
                kmsg = crmEmailMarketingDAOObj.editEmailType(jobj);
            } else {
                jobj.put("modifiedon", new Date().getTime());
                jobj.put("createdon", new Date().getTime());
                kmsg = crmEmailMarketingDAOObj.addEmailType(jobj);
            }
           
            myjobj.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView deleteemailMarketing(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        KwlReturnObject kmsg = null;
        KwlReturnObject kmsg1 = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            myjobj = new JSONObject();
            JSONArray deletejarr =new JSONArray() ;
            JSONArray failDeletejarr =new JSONArray() ;
            myjobj.put(Constants.success, false);
            if (StringUtil.bNull(request.getParameter(Constants.jsondata))) {
                String jsondata = request.getParameter(Constants.jsondata);
                JSONArray jarr = new JSONArray("[" + jsondata + "]");
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobj = new JSONObject();
                    String templateid =jarr.getJSONObject(i).getString(Constants.templateid);
                    List<EmailMarketing> ll = crmEmailMarketingHandler.getCampEmailMarketList(crmEmailMarketingDAOObj, templateid);
                    JSONObject jobjmsg = new JSONObject();

                    if(ll.isEmpty()){
                        if(StringUtil.equal(templateid,Constants.DEFAULT_BASIC_TEMPLATEID)||StringUtil.equal(templateid, Constants.DEFAULT_LEFTCOLUMN_TEMPLATEID)||StringUtil.equal(templateid, Constants.DEFAULT_POSTCARD_TEMPLATEID)||StringUtil.equal(templateid, Constants.DEFAULT_RICHTEXT_TEMPLATEID)||StringUtil.equal(templateid, Constants.DEFAULT_RIGHTCOLUMN_TEMPLATEID)){
                            HashMap requestParams = new HashMap();
                            ArrayList filter_names = new ArrayList();
                            ArrayList filter_params = new ArrayList();
                            filter_names.add("c.templateid");
                            filter_params.add(templateid);
                            requestParams.put("filter_names", filter_names);
                            requestParams.put("filter_params", filter_params);
                            kmsg1 = crmEmailMarketingDAOObj.getDefaultEmailTemplate(requestParams);
                            DefaultTemplates obj =  (DefaultTemplates) kmsg1.getEntityList().get(0);
                            jobjmsg.put(Constants.name, obj.getName());
                            jobjmsg.put(Constants.moduleName,"Default Template");
                            failDeletejarr.put(jobjmsg);
                        }else {
                            jobj.put(Constants.modifiedon, new Date().getTime());
                            jobj.put(Constants.tid, templateid);
                            jobj.put(Constants.deleted, 1);
                            kmsg = crmEmailMarketingDAOObj.editEmailTemplate(jobj);
                            if(kmsg.isSuccessFlag()){
                                EmailTemplate obj =  (EmailTemplate) kmsg.getEntityList().get(0);
                                jobjmsg.put(Constants.name, obj.getName());
                                jobjmsg.put(Constants.moduleName,Constants.MODULE_EmailTemplate);
                                deletejarr.put(jobjmsg);
                            }
                        }

                    }else{
                        EmailTemplate obj =  ll.get(0).getTemplateid();
                        String emarkt="";
                        for(EmailMarketing em:ll){
                        emarkt += em.getName()+", ";
                        }
                        emarkt = emarkt.substring(0, emarkt.length() - 2);
                        jobjmsg.put(Constants.name, obj.getName());
                        jobjmsg.put(Constants.moduleName,emarkt);
                        failDeletejarr.put(jobjmsg);
                    }
                }
                myjobj.put(Constants.success, true);
                if(failDeletejarr.length()>0){
                    myjobj.put(Constants.successDeleteArr, deletejarr);
                    myjobj.put(Constants.failDelete, failDeletejarr);
                }
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

    public ModelAndView getTargetList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("deleted");
            filter_params.add(0);
            filter_names.add("creator.company.companyID");
            filter_params.add(sessionHandlerImpl.getCompanyid(request));
            filter_names.add("saveflag");
            filter_params.add(1);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("sort"))) {
                requestParams.put("field", request.getParameter("sort"));
                requestParams.put("direction", request.getParameter("dir"));
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("allflag"))) {
                requestParams.put("allflag",true);
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("avoidblanklist"))) {
            	requestParams.put("avoidblanklist",true);
            }
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getTargetList(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                TargetList obj = (TargetList) ite.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("listid", obj.getId());
                jtemp.put("listname", obj.getName());
                jtemp.put("description", obj.getDescription());
                jtemp.put("creator", StringUtil.getFullName(obj.getCreator().getFirstName(), obj.getCreator().getLastName()));
                jtemp.put("targetsrc", obj.getTargetsource());
                jtemp.put("createdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
                jtemp.put("updatedon", obj.getModifiedOn()!=null?obj.getModifiedOn():"");
                jarr.put(jtemp);
            }
            jobj.put("totalCount", kmsg.getRecordTotalCount());
            jobj.put("data", jarr);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getEmailMarkTargetList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            if (!StringUtil.isNullOrEmpty(request.getParameter("emailmarkid"))) {
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("emailmarkid", request.getParameter("emailmarkid"));
                KwlReturnObject kmsg = crmEmailMarketingDAOObj.getEmailMarkTargetList(requestParams);
                Iterator ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    EmailMarkteingTargetList obj = (EmailMarkteingTargetList) ite.next();
                    JSONObject jtemp = new JSONObject();
                    jtemp.put("listid", obj.getTargetlistid().getId());
                    jtemp.put("listname", obj.getTargetlistid().getName());
                    jarr.put(jtemp);
                }
            }
            jobj.put("data", jarr);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getUnAssignEmailMarkTargetList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("emailmarkid", request.getParameter("emailmarkid"));
            requestParams.put("campID", request.getParameter("campID"));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getUnAssignEmailMarkTargetList(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                TargetList obj = (TargetList) ite.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("listid", obj.getId());
                jtemp.put("listname", obj.getName());
                jarr.put(jtemp);
            }
            jobj.put("data", jarr);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getTargetListTargets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("listID", request.getParameter("listID"));
            if(!StringUtil.isNullOrEmpty(request.getParameter("start")) && !StringUtil.isNullOrEmpty(request.getParameter("limit"))) {
                requestParams.put("allflag", false);
                requestParams.put("start", request.getParameter("start"));
                requestParams.put("limit", request.getParameter("limit"));
            }
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();

            filter_names.add("targetlisttargets.targetlistid.id");
            filter_params.add(request.getParameter("listID"));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            requestParams.put("companyid", companyid);
            kmsg = crmEmailMarketingDAOObj.getTargetListTargets(requestParams);
            jobj = getTargetListJson(kmsg.getEntityList());
            jobj.put("totalCount", kmsg.getRecordTotalCount());
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView targetListExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            if(request.getParameter("searchJson") != null && !StringUtil.isNullOrEmpty(request.getParameter("searchJson"))) {
                requestParams.put("searchJson", request.getParameter("searchJson"));
            }
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            requestParams.put("companyid", companyid);
            if (request.getParameter("config") != null) {
                requestParams.put("config", request.getParameter("config"));
            }

            if(!StringUtil.isNullOrEmpty(request.getParameter("start")) && !StringUtil.isNullOrEmpty(request.getParameter("limit"))) {
                requestParams.put("allflag", false);
                requestParams.put("start", request.getParameter("start"));
                requestParams.put("limit", request.getParameter("limit"));
            }

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();

            filter_names.add("targetlisttargets.targetlistid.id");
            filter_params.add(request.getParameter("listID"));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            kmsg = crmEmailMarketingDAOObj.getTargetListTargets(requestParams);
            jobj = getTargetListJson(kmsg.getEntityList());
            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }

            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.TARGET_EXPORT,
                    "Target data exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public JSONObject getTargetListJson(List ll) throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                TargetListTargets obj = (TargetListTargets) ite.next();
                JSONObject jtemp = new JSONObject();
                String name = "";
                String email = "";
                String phone = "";
                String relatedmodule="";
                String recipientCompany="";
                boolean putArch = false;
                int putDel = 0;
                String classpath = "";
                switch (obj.getRelatedto()) {

                    case 1: // Lead
                        classpath = "com.krawler.crm.database.tables.CrmLead";
                        Object invoker =  KwlCommonTablesDAOObj.getClassObject(classpath, obj.getRelatedid());
                        if(invoker != null) {
                            Class cl = invoker.getClass();
                            Class  arguments[] = new Class[] {};
                            Object[] obj1 = new Object[]{};

                            java.lang.reflect.Method objMethod = cl.getMethod("getFirstname", arguments);
                            name = (String) objMethod.invoke(invoker, obj1);
                            name=StringUtil.isNullOrEmpty(name)?"":name;
                            objMethod = cl.getMethod("getLastname", arguments);
                            name = (StringUtil.checkForNull(name) + " " + StringUtil.checkForNull((String) objMethod.invoke(invoker, obj1))).trim();
                            
                            objMethod = cl.getMethod("getEmail", arguments);
                            email = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getPhone", arguments);
                            phone = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getIsarchive", arguments);
                            putArch = (Boolean) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getDeleteflag", arguments);
                            putDel = (Integer) objMethod.invoke(invoker, obj1);
                            relatedmodule=Constants.MODULE_LEAD;
                        }
                        break;
                    case 2: // Contact
                        classpath = "com.krawler.crm.database.tables.CrmContact";
                        invoker =  KwlCommonTablesDAOObj.getClassObject(classpath, obj.getRelatedid());
                        if(invoker != null) {
                            Class cl = invoker.getClass();
                            Class  arguments[] = new Class[] {};
                            Object[] obj1 = new Object[]{};

                            java.lang.reflect.Method objMethod = cl.getMethod("getFirstname", arguments);
                            name = (String) objMethod.invoke(invoker, obj1);
                            name=StringUtil.isNullOrEmpty(name)?"":name;
                            objMethod = cl.getMethod("getLastname", arguments);
                            name = (StringUtil.checkForNull(name) + " " + StringUtil.checkForNull((String) objMethod.invoke(invoker, obj1))).trim();
                            objMethod = cl.getMethod("getEmail", arguments);
                            email = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getPhoneno", arguments);
                            phone = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getIsarchive", arguments);
                            putArch = (Boolean) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getDeleteflag", arguments);
                            putDel = (Integer) objMethod.invoke(invoker, obj1);
                            relatedmodule=Constants.MODULE_CONTACT;
                        }
                        break;
                    case 3: // Users
                        classpath = "com.krawler.common.admin.User";
                        invoker =  KwlCommonTablesDAOObj.getClassObject(classpath, obj.getRelatedid());
                        if(invoker != null) {
                            Class cl = invoker.getClass();
                            Class  arguments[] = new Class[] {};
                            Object[] obj1 = new Object[]{};

                            java.lang.reflect.Method objMethod = cl.getMethod("getFirstName", arguments);
                            name = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getLastName", arguments);
                            name = name + " " + (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getEmailID", arguments);
                            email = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getContactNumber", arguments);
                            phone = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getDeleteflag", arguments);
                            putDel = (Integer) objMethod.invoke(invoker, obj1);
                            relatedmodule=Constants.MODULE_USER;
                        }
                        break;
                    case 4: // Target Module
                        classpath = "com.krawler.crm.database.tables.TargetModule";
                        invoker =  KwlCommonTablesDAOObj.getClassObject(classpath, obj.getRelatedid());
                        if(invoker != null) {
                            Class cl = invoker.getClass();
                            Class  arguments[] = new Class[] {};
                            Object[] obj1 = new Object[]{};

                            java.lang.reflect.Method objMethod = cl.getMethod("getFirstname", arguments);
                            if(objMethod.invoke(invoker, obj1)!=null)
                                name = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getLastname", arguments);
                            if(objMethod.invoke(invoker, obj1)!=null)
                                name = name + " " + (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getEmail", arguments);
                            if(objMethod.invoke(invoker, obj1)!=null)
                                email = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getPhoneno", arguments);
                            if(objMethod.invoke(invoker, obj1)!=null)
                                phone = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getIsarchive", arguments);
                            if(objMethod.invoke(invoker, obj1)!=null)
                                putArch = (Boolean) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getCompanyname", arguments);
                            if(objMethod.invoke(invoker, obj1)!=null)
                                recipientCompany = (String) objMethod.invoke(invoker, obj1);
                            objMethod = cl.getMethod("getDeleteflag", arguments);
                            putDel = (Integer) objMethod.invoke(invoker, obj1);
                            relatedmodule=Constants.MODULE_TARGET;
                        }
                        break;
                    default:
                        break;
                }

                jtemp.put("id", obj.getId());
                jtemp.put("name", name);
                jtemp.put("related", relatedmodule);
                jtemp.put("relatedto", obj.getRelatedto());
                jtemp.put("relatedid", obj.getRelatedid());
                jtemp.put("emailid", email);
                jtemp.put("company", recipientCompany);
                jtemp.put("phone", phone);
                if (putArch || putDel == 1) {
                } else {
                        jarr.put(jtemp);
                }
            }
            jobj.put("data", jarr);
        }  catch (IllegalAccessException e) {
            logger.warn(e.getMessage(),e);
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage(),e);
        } catch (InvocationTargetException e) {
            logger.warn(e.getMessage(),e);
        } catch (NoSuchMethodException e) {
            logger.warn(e.getMessage(),e);
        } catch (SecurityException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }
    
    public ModelAndView getCampEmailMarketList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            requestParams.put("campid", request.getParameter("campid"));
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            int tzdiff=TimeZone.getTimeZone("GMT"+sessionHandlerImpl.getTimeZoneDifference(request)).getOffset(System.currentTimeMillis());
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("em.campaignid.campaignid");
            filter_params.add(request.getParameter("campid"));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getCampEmailMarketList(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                EmailMarketing obj = (EmailMarketing) ite.next();
                JSONObject jtemp = new JSONObject();

                requestParams.clear();
                requestParams.put("emailmarketingid", obj.getId());
                requestParams.put("tzdiff", tzdiff);
                KwlReturnObject kmsg1 = crmEmailMarketingDAOObj.getCampEmailMarketCount(requestParams);
                KwlReturnObject kmsg2 = crmEmailMarketingDAOObj.getCampaignLog(requestParams);
                Iterator ite1 = kmsg2.getEntityList().iterator();
                JSONArray CampaignLogData = new JSONArray();
                while (ite1.hasNext()) {
                    JSONObject tempCampaignLogData = new JSONObject();
                    Object[] logObj = (Object[])ite1.next();
                    tempCampaignLogData.put("activitydate",logObj[0]);
                    tempCampaignLogData.put("totalsent",logObj[1]);
                    tempCampaignLogData.put("viewed",logObj[2]);
                    tempCampaignLogData.put("failed",logObj[3]);
                    tempCampaignLogData.put("usercount",logObj[4]);
                    tempCampaignLogData.put("targetlistid",logObj[5]);
                    tempCampaignLogData.put("targetlistname",logObj[6]);
                    CampaignLogData.put(tempCampaignLogData);
                }
                ArrayList filter_names1 = new ArrayList();
                ArrayList filter_params1 = new ArrayList();
                filter_names1.add("emailmarketingid.id");
                filter_params1.add(obj.getId());
                requestParams.put("filter_values",filter_params1);
                requestParams.put("filter_names", filter_names1);
                KwlReturnObject kmsgforScedule = crmEmailMarketingDAOObj.getFutureScheduleEmailMarketingById(requestParams);
                List<scheduledmarketing> scheduledmarketingList = kmsgforScedule.getEntityList();
                JSONObject CaseSheduleLog = new JSONObject();
                if(scheduledmarketingList !=null && !scheduledmarketingList.isEmpty()) {
                    for (scheduledmarketing scheduleObj: scheduledmarketingList) {
                        JSONObject CaseShedule = new JSONObject();
                        String starttime=scheduleObj.getScheduledtime();
                        Long startdate =  scheduleObj.getScheduledDate();
                        CaseShedule.put("Date",startdate);
                        CaseShedule.put("Time",starttime);
                        CaseShedule.put("id",scheduleObj.getId());
                        CaseSheduleLog.append("SMarketingData", CaseShedule);
                    }
                }
                String subject = obj.getTemplateid().getSubject();
                if(!StringUtil.isNullOrEmpty(obj.getSubject())){
                    subject = obj.getSubject();
                }
                jtemp.put("targetcount",kmsg1.getRecordTotalCount());
                jtemp.put("fromaddress",obj.getFromaddress());
                jtemp.put("captureLead",obj.isCaptureLead());
                jtemp.put("fromname",obj.getFromname());
                jtemp.put("id",obj.getId());
                jtemp.put("name",obj.getName());
                jtemp.put("templatename",obj.getTemplateid().getName());
                jtemp.put("templateid",obj.getTemplateid().getTemplateid());
                jtemp.put("templatedescription",obj.getTemplateid().getDescription());
                jtemp.put("templatesubject",obj.getTemplateid().getSubject());
                jtemp.put("marketingsubject",subject);
                jtemp.put("replymail",obj.getReplytoaddress());
                jtemp.put("unsub",obj.getUnsubscribelink());
                jtemp.put("fwdfriend",obj.getFwdfriendlink());
                jtemp.put("archive",obj.getArchivelink());
                jtemp.put("updatelink",obj.getUpdateprofilelink());
                jtemp.put("createdon", (obj.getCreatedOn()!=null?obj.getCreatedOn():""));
                jtemp.put("campaignlog", CampaignLogData);
                jtemp.put("SMarketing", CaseSheduleLog);
                jtemp.put("lastrunstatus", obj.getLastRunStatus());
                jarr.put(jtemp);
            }
            jobj.put("totalCount", kmsg.getRecordTotalCount());
            jobj.put("data", jarr);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView sendEmailMarketMail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        String msg = "";
        try {
            jobj.put("success", false);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("campaignid", request.getParameter("campid"));
            requestParams.put("emailmarkid", request.getParameter("emailmarkid"));
            requestParams.put("resume", Boolean.parseBoolean(request.getParameter("resume")));
            requestParams.put("baseurl", URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull));
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.sendEmailMarketMail(requestParams);
            jobj.put("success", kmsg.isSuccessFlag());
            msg = kmsg.getEntityList().get(0).toString();
            jobj.put("msgs", msg);
            txnManager.commit(status);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView sendTestEmailMarketMail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            jobj.put("success", false);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("emailmarkid", request.getParameter("emailmarkid"));
            requestParams.put("reciepientMailId", request.getParameter("reciepientMailId"));
            requestParams.put("baseurl", URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull));
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.sendTestEmailMarketMail(requestParams);
            jobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    
    public ModelAndView getCampaignTarget(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("deleted");
            filter_params.add(0);
            filter_names.add("targetlist.deleted");
            filter_params.add(0);
            filter_names.add("campaign.campaignid");
            filter_params.add(request.getParameter("campID"));

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getCampaignTarget(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                CampaignTarget ct = (CampaignTarget) ite.next();
                TargetList obj = ct.getTargetlist();
                JSONObject jtemp = new JSONObject();
                jtemp.put("ctid", ct.getId());
                jtemp.put("listid", obj.getId());
                jtemp.put("listname", obj.getName());
                jtemp.put("description", obj.getDescription());
                jtemp.put("createdon",obj.getCreatedOn()!=null?obj.getCreatedOn():"");
                jarr.put(jtemp);
            }
            jobj.put("data", jarr);
            jobj.put("totalCount", kmsg.getRecordTotalCount());
        }  catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveCampaignTarget(HttpServletRequest request, HttpServletResponse response)
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
            myjobj.put("success", false);
            int type = Integer.parseInt(request.getParameter("type"));
            JSONObject jobj = new JSONObject();

            if (type == 1) {
                String targID = request.getParameter("targID");
                String campID = request.getParameter("campID");
                String userid = sessionHandlerImpl.getUserid(request);
                String camptargetid = java.util.UUID.randomUUID().toString();

                jobj.put("userid", userid);
                jobj.put("campid", campID);
                jobj.put("targetid", targID);
                jobj.put("camptargetid", camptargetid);
                jobj.put("userid", userid);
                jobj.put("createdon", new Date().getTime());
                kmsg = crmEmailMarketingDAOObj.addCampaignTarget(jobj);
            } else if (type == 2) {
                String camptargetid = request.getParameter("ctID");
                jobj.put("camptargetid", camptargetid);
                jobj.put("deleted", 1);
                kmsg = crmEmailMarketingDAOObj.editCampaignTarget(jobj);
            }
            myjobj.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView getUnAssignCampaignTarget(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("campID", request.getParameter("campID"));
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("deleted");
            filter_params.add(0);
            filter_names.add("creator.company.companyID");
            filter_params.add(sessionHandlerImpl.getCompanyid(request));
            filter_names.add("saveflag");
            filter_params.add(1);
            
            HashMap<String, Object> subRequestParams = new HashMap<String, Object>();
            subRequestParams.put("allflag", true);
            ArrayList subfilter_names = new ArrayList();
            ArrayList subfilter_params = new ArrayList();
            subfilter_names.add("targetlist.deleted");
            subfilter_params.add(0);
            subfilter_names.add("campaign.campaignid");
            subfilter_params.add(request.getParameter("campID"));
            subfilter_names.add("deleted");
            subfilter_params.add(0);
            
            subRequestParams.put("filter_names", subfilter_names);
            subRequestParams.put("filter_params", subfilter_params);
            
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getCampaignTarget(subRequestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            StringBuffer assTLID = new StringBuffer();
            while (ite.hasNext()) {
                CampaignTarget obj = (CampaignTarget) ite.next();
                assTLID.append("'" + obj.getTargetlist().getId() + "',");
            }
            if(assTLID.length()>0) {
                filter_names.add("NOTINid");
                String ids = assTLID.substring(0,assTLID.length()-1);
                filter_params.add(ids);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            requestParams.put("allflag",true);
            kmsg = crmEmailMarketingDAOObj.getTargetList(requestParams);
            ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                TargetList obj = (TargetList) ite.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("listid", obj.getId());
                jtemp.put("listname", obj.getName());
                jarr.put(jtemp);
            }
            jobj.put("data", jarr);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getColorThemes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getColorThemes();
            Iterator ite = kmsg.getEntityList().iterator();
            while(ite.hasNext()) {
                templateColorTheme ct = (templateColorTheme) ite.next();
                JSONObject temp = new JSONObject();
                temp.put("id", ct.getId());
                temp.put("theme", ct.getTheme());
                colorThemeGroup grp = ct.getGroupid();
                temp.put("groupid", grp.getId());
                temp.put("background", ct.getBackground());
                temp.put("headerbackground", ct.getHeaderbackground());
                temp.put("headertext", ct.getHeadertext());
                temp.put("footerbackground", ct.getFooterbackground());
                temp.put("footertext", ct.getFootertext());
                temp.put("bodybackground", ct.getBodybackground());
                temp.put("bodytext", ct.getBodytext());
                jobj.append("data", temp);
            }
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getColorThemeGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getColorThemeGroup();
            Iterator ite = kmsg.getEntityList().iterator();
            while(ite.hasNext()) {
                colorThemeGroup grp = (colorThemeGroup) ite.next();
                JSONObject temp = new JSONObject();
                temp.put("id", grp.getId());
                temp.put("groupname", grp.getGroupname());
                jobj.append("data", temp);
            }
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getEmailMrktContent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("marketid", request.getParameter("marketid"));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getEmailMrktContent(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            if(ite.hasNext()){
                EmailMarketing obj = (EmailMarketing) ite.next();
                JSONObject temp = new JSONObject();
                temp.put("html", URLDecoder.decode(obj.getHtmltext(), "utf-8"));
                templateColorTheme thm = obj.getColortheme();
                if(thm != null)
                    temp.put("theme", thm.getId());
//                temp.put("plain", obj.getPlaintext());
                jobj.put("data", temp);
            }
            jobj.put("success", kmsg.isSuccessFlag());
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getTemplateContent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.templateid");
            filter_params.add(request.getParameter("templateid"));
           
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            requestParams.put("templateclass", request.getParameter("templateClass"));
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getTemplateContent(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            if(ite.hasNext()){
                crmEmailTemplateInterface obj = (crmEmailTemplateInterface) ite.next();
                JSONObject temp = new JSONObject();
                temp.put("html", URLDecoder.decode(obj.getBody_html(), "utf-8"));
                jobj.put("data", temp);
            }
            jobj.put("success", kmsg.isSuccessFlag());
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getEmailTypeContent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.typeid");
            filter_params.add(request.getParameter("typeid"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getEmailTypeContent(requestParams);
            List<EnumEmailType> enumEmailList = kmsg.getEntityList();
            for(EnumEmailType eet : enumEmailList) {
                JSONObject temp = new JSONObject();
                temp.put("html", URLDecoder.decode(eet.getBody_html(), "utf-8"));
                jobj.put("data", temp);
            }
            jobj.put("success", kmsg.isSuccessFlag());
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView scheduleEmailMarketing(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            jobj.put("success", false);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            requestParams.put("emailmarketingid", request.getParameter("emailmarketingid"));
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));
            Long scheduledate = Long.parseLong(request.getParameter("scheduledate"));
            String scheduletime=request.getParameter("scheduletime");
            requestParams.put("scheduledate", scheduledate);
            requestParams.put("scheduletime", scheduletime);

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.scheduleEmailMarketing(requestParams);
            jobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } 
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteEmailMarketingSchedule(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            jobj.put("success", false);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names1 = new ArrayList();
            ArrayList filter_params1 = new ArrayList();
            filter_names1.add("id");
            filter_params1.add(request.getParameter("id"));
            requestParams.put("filter_values",filter_params1);
            requestParams.put("filter_names", filter_names1);
            KwlReturnObject kmsgforScedule = crmEmailMarketingDAOObj.deleteEmailMarketingSchedule(requestParams);
            if(kmsgforScedule.getEntityList().size() > 0) {
                jobj.put("success", true);
                txnManager.commit(status);
            }
        } catch (ServiceException e) {
            logger.warn("Can't delete email marketing Schedule : ", e);
            txnManager.rollback(status);
        } catch (JSONException e) {
            logger.warn("Can't delete email marketing Schedule : ", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView campEmailMarketingStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            String userId = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = this.crmManagerDAOObj.recursiveUsers(userId);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userslist", usersList);
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.campEmailMarketingStatus(requestParams);
            jobj = crmEmailMarketingHandler.getcampEmailMarketingStatusJson(kmsg.getEntityList(), kmsg.getRecordTotalCount());
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } 
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView emailTemplateView(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String htmlString = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><link rel=\"shortcut icon\" href=\"../../images/deskera/deskera.png\"/></head>";
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("uid", request.getParameter("uid"));
            requestParams.put("mid", request.getParameter("mid"));
            requestParams.put("tuid", request.getParameter("tuid"));
            requestParams.put("cdomain", request.getParameter("cdomain"));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getTemplateHTMLContent(requestParams);
            htmlString += kmsg.getEntityList().get(0).toString();
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } finally {
            htmlString += "</html>";
        }
        return new ModelAndView("chartView", "model", htmlString);
    }

    public ModelAndView unsubscribeUserMarketMail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String htmlString = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><link rel=\"shortcut icon\" href=\"../../images/deskera/deskera.png\"/></head>";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("trackid", request.getParameter("trackid"));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.unsubscribeUserMarketMail(requestParams);
            htmlString += kmsg.getEntityList().get(0).toString();
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            htmlString = "<div style='font-color:red;'><b>Failed to process request</b></div>";
            txnManager.rollback(status);
        } finally {
            htmlString += "</html>";
        }
        return new ModelAndView("chartView", "model", htmlString);
    }

    public ModelAndView subscribeUserMarketMail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String htmlString = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><link rel=\"shortcut icon\" href=\"../../images/deskera/deskera.png\"/></head>";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("trackid", request.getParameter("trackid"));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.subscribeUserMarketMail(requestParams);
            htmlString += kmsg.getEntityList().get(0).toString();
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            htmlString = "<div style='font-color:red;'><b>Failed to process request</b></div>";
            txnManager.rollback(status);
        } finally {
            htmlString += "</html>";
        }
        return new ModelAndView("chartView", "model", htmlString);
    }

    public ModelAndView viewedEmailMarketMail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String htmlString = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><link rel=\"shortcut icon\" href=\"../../images/deskera/deskera.png\"/></head>";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("trackid", request.getParameter("trackid"));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.viewedEmailMarketMail(requestParams);
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            htmlString = "<div style='font-color:red;'><b>Failed to process request</b></div>";
            txnManager.rollback(status);
        } finally {
            htmlString += "</html>";
        }
        return new ModelAndView("chartView", "model", htmlString);
    }

    public ModelAndView confirmSubscribeUserMarketMail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String htmlString = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><link rel=\"shortcut icon\" href=\"../../images/deskera/deskera.png\"/></head>";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("trackid", request.getParameter("trackid"));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.confirmsubscribeUserMarketMail(requestParams);
            htmlString += kmsg.getEntityList().get(0).toString();
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            htmlString = "<div style='font-color:red;'><b>Failed to process request</b></div>";
            txnManager.rollback(status);
        } finally {
            htmlString += "</html>";
        }
        return new ModelAndView("chartView", "model", htmlString);
    }

    public ModelAndView saveTargetListTargets(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String retMsg = "";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("mode", request.getParameter("mode"));
            requestParams.put("listid", request.getParameter("listid"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));
            requestParams.put("name", request.getParameter("name"));//StringUtil.serverHTMLStripper(request.getParameter("name")));
            requestParams.put("desc", request.getParameter("desc"));//StringUtil.serverHTMLStripper(request.getParameter("desc")));
            requestParams.put("targets", request.getParameter("targets"));
            requestParams.put("targetsource", request.getParameter("targetsource"));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.saveTargetListTargets(requestParams);
            retMsg = kmsg.getEntityList().get(0).toString();
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", retMsg);
    }

    public ModelAndView deleteTargetList(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String retMsg = "{success:false}";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            if (!StringUtil.isNullOrEmpty(request.getParameter("jsondata"))) {
                String jsondata = request.getParameter("jsondata");
                JSONArray jarr = new JSONArray("[" + jsondata + "]");
                for (int i = 0; i < jarr.length(); i++) {
                    String listid = jarr.getJSONObject(i).getString("listid");
                    HashMap<String, Object> requestParams = new HashMap<String, Object>();
                    requestParams.put("mode", 2);
                    requestParams.put("listid", listid);
                    KwlReturnObject kmsg = crmEmailMarketingDAOObj.saveTargetListTargets(requestParams);
                    
                    HashMap<String, Object> subRequestParams = new HashMap<String, Object>();
                    ArrayList filter_names = new ArrayList();
                    ArrayList filter_params = new ArrayList();
                    filter_names.add("deleted");
                    filter_params.add(0);
                    filter_names.add("targetlist.id");
                    filter_params.add(listid);
                    subRequestParams.put("filter_names", filter_names);
                    subRequestParams.put("filter_params", filter_params);
                    subRequestParams.put("allflag", true);
                    KwlReturnObject subkmsg = crmEmailMarketingDAOObj.getCampaignTarget(subRequestParams);
                    Iterator ite = subkmsg.getEntityList().iterator();
                    while (ite.hasNext()) {
                        CampaignTarget ct = (CampaignTarget) ite.next();
                        JSONObject jObj = new JSONObject();
                        jObj.put("camptargetid", ct.getId());
                        jObj.put("deleted", 1);
                        crmEmailMarketingDAOObj.editCampaignTarget(jObj);
                    }
                }
            }
            retMsg = "{success:true}";
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", retMsg);
    }

    public ModelAndView saveCampEmailMarketConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String retMsg = "{success:false}";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String emailMarketingID = null;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));
            requestParams.put("templateid", request.getParameter("templateid"));
            KwlReturnObject copyMsg = crmEmailMarketingDAOObj.copyDefaultTemplates(requestParams);
            String templateid = request.getParameter("templateid");
            if(copyMsg.isSuccessFlag()) {
                List template = copyMsg.getEntityList();
                templateid = template.get(0).toString();
            }
            requestParams = new HashMap<String, Object>();
            requestParams.put("mode", request.getParameter("mode"));
            requestParams.put("emailmarkid", request.getParameter("emailmarkid"));
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));
            requestParams.put("name", request.getParameter("name"));
            requestParams.put("fromname", request.getParameter("fromname"));
            requestParams.put("fromaddress", request.getParameter("fromaddress"));
            requestParams.put("replyaddress", request.getParameter("replyaddress"));
            requestParams.put("inboundemail", request.getParameter("inboundemail"));
            requestParams.put("htmlcont", request.getParameter("htmlcont"));
            requestParams.put("plaincont", request.getParameter("plaincont"));
            requestParams.put("unsub", request.getParameter("unsub"));
            requestParams.put("marketingsubject", request.getParameter("marketingsubject"));
            requestParams.put("fwdfriend", request.getParameter("fwdfriend"));
            requestParams.put("archive", request.getParameter("archive"));
            requestParams.put("updatelink", request.getParameter("updatelink"));
            requestParams.put("templateid", templateid);
            requestParams.put("campid", request.getParameter("campid"));
            requestParams.put("captureLead", Boolean.parseBoolean(request.getParameter("captureLead")));
            requestParams.put("colortheme", request.getParameter("colortheme"));
            requestParams.put("targetlist", request.getParameter("targetlist"));
            requestParams.put("csaccept", Boolean.parseBoolean(request.getParameter("csaccept")));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.saveCampEmailMarketConfig(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                EmailMarketing emobj = (EmailMarketing) ite.next();
                HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
                emailMarketingID = emobj.getId();
                requestParams1.put("emailmarkid",emailMarketingID );
                KwlReturnObject kmsg1 = crmEmailMarketingDAOObj.deleteEmailmarketingTargetList(requestParams1);
                requestParams1.put("targetlist", request.getParameter("targetlist"));
                KwlReturnObject kmsg2 = crmEmailMarketingDAOObj.mapEmailmarketingTargetList(requestParams1);
                retMsg = kmsg2.getEntityList().get(0).toString();
            }
            
            HashMap<String, Object> requestParams2 = new HashMap<String, Object>();
            requestParams2.put("campaignid", request.getParameter("campid"));
            crmEmailMarketingDAOObj.deleteCampaignTarget(requestParams2);
            requestParams2.put("targetlist", request.getParameter("targetlist"));
            requestParams2.put("userid", sessionHandlerImpl.getUserid(request));
            KwlReturnObject kmsg4 = crmEmailMarketingDAOObj.saveCampaignTargetList(requestParams2);
            
            // To Get Default Values of Email Marketing Template
            if(emailMarketingID!=null){
                JSONArray jArr = new JSONArray(request.getParameter("defaulttemplatestore"));
                crmEmailMarketingDAOObj.removeEmailMarketingDefaults(emailMarketingID);
                EmailMarketing em=crmEmailMarketingDAOObj.getEmailMarketing(emailMarketingID);
                EmailMarketingDefault emDefault;
                for(int i=0; i<jArr.length();i++){
                    JSONObject obj = jArr.getJSONObject(i);
                    emDefault = new EmailMarketingDefault();
                    emDefault.setEmailMarketing(em);
                    emDefault.setVariableName(obj.getString("varname"));
                    emDefault.setDefaultValue(obj.getString("varval"));
                    crmEmailMarketingDAOObj.saveEmailMarketingDefault(emDefault);
                }
            }


            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", retMsg);
    }

    public ModelAndView saveModuleTargetsForTemplate(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("relatedto", request.getParameter("relatedto"));
            requestParams.put("listid", request.getParameter("listid"));
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));
            requestParams.put("data", request.getParameter("data"));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.saveModuleTargetsForTemp(requestParams);
            TargetList tl = (TargetList) kmsg.getEntityList().get(0);

            jobj.put("success", true);
            jobj.put("TLID", tl.getId());
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage());
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView targetListForImport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            HashMap<String, Object> innerrequestParams = new HashMap<String, Object>();
            String companyid = sessionHandlerImpl.getCompanyid(request);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("deleted");
            filter_params.add(0);
            filter_names.add("creator.company.companyID");
            filter_params.add(companyid);
            filter_names.add("saveflag");
            filter_params.add(1);
            filter_names.add("!id");
            filter_params.add(request.getParameter("tlid"));
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getTargetList(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                TargetList obj = (TargetList) ite.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("relatedid", obj.getId());
                jtemp.put("name", obj.getName());
                jtemp.put("targetlistDescription", obj.getDescription());
                innerrequestParams.clear();
                innerrequestParams.put("listID", obj.getId());
                filter_names.clear();
                filter_params.clear();

                filter_names.add("targetlisttargets.targetlistid.id");
                filter_params.add(obj.getId());
                innerrequestParams.put("filter_names", filter_names);
                innerrequestParams.put("filter_params", filter_params);
                innerrequestParams.put("companyid", companyid);
                KwlReturnObject innerkmsg = crmEmailMarketingDAOObj.getTargetListTargets(innerrequestParams);
                jtemp.put("targetscount", innerkmsg.getRecordTotalCount());
                jarr.put(jtemp);
            }

            jobjTemp = new JSONObject();
            jobjTemp.put("header",mSource.getMessage("crm.targetlists.targetlistname", null, RequestContextUtils.getLocale(request)));//"Target List Name");
            jobjTemp.put("tip", mSource.getMessage("crm.targetlists.targetlistname", null, RequestContextUtils.getLocale(request)));//"Target List Name");
            jobjTemp.put("pdfwidth", 60);
            jobjTemp.put("dataIndex", "name");
            jarrColumns.put(jobjTemp);

            jobjTemp = new JSONObject();
            jobjTemp.put("header", mSource.getMessage("crm.targetlists.targetlistname", null, RequestContextUtils.getLocale(request)));//"No of Targets");
            jobjTemp.put("tip", mSource.getMessage("crm.targetlists.targetlistname", null, RequestContextUtils.getLocale(request)));//"No of Targets");
            jobjTemp.put("pdfwidth", 60);
            jobjTemp.put("dataIndex", "targetscount");
            jarrColumns.put(jobjTemp);

            jobjTemp = new JSONObject();
            jobjTemp.put("header", mSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)));//"Description");
            jobjTemp.put("tip", mSource.getMessage("crm.case.defaultheader.desc", null, RequestContextUtils.getLocale(request)));//"Description");
            jobjTemp.put("pdfwidth", 60);
            jobjTemp.put("dataIndex", "targetlistDescription");
            jarrColumns.put(jobjTemp);

            jobjTemp = new JSONObject();
            jobjTemp.put("name", "targetscount");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "name");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "emailid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "relatedto");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "company");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "targetlistDescription");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "relatedid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "fname");
            jarrRecords.put(jobjTemp);

            jobj.put("columns", jarrColumns);
            jMeta.put("totalProperty", "totalCount");
            jMeta.put("root", "data");
            jMeta.put("fields", jarrRecords);
            jobj.put("metaData", jMeta);
            jobj.put("totalCount", kmsg.getRecordTotalCount());
            jobj.put("data", jarr);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView importTargetList(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("importtl", request.getParameter("importtl"));
            requestParams.put("listid", request.getParameter("listid"));
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.importTargetList(requestParams);
            TargetList tl = (TargetList) kmsg.getEntityList().get(0);

            jobj.put("success", true);
            jobj.put("TLID", tl.getId());
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteTargets(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String retMsg = "";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("relatedid", request.getParameter("relatedid"));
            requestParams.put("listid", request.getParameter("listid"));
            if(!StringUtil.isNullOrEmpty(request.getParameter("targetid"))){
            	requestParams.put("targetid", request.getParameter("targetid"));
            }
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.deleteTargets(requestParams);
            retMsg = kmsg.getEntityList().get(0).toString();
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", retMsg);
    }

    public JSONObject getEmailTemplateFilesJson(List ll, HttpServletRequest request, int totalSize) {
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            Company companyObj = (Company) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyid);
            String subDomain = companyObj.getSubDomain();
            String domainURL = URLUtil.getRequestPageURL(request, Links.loginpageFull);
            String fType = request.getParameter("type");
            
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                JSONObject temp = new JSONObject();
                EmailTemplateFiles obj = (EmailTemplateFiles) ite.next();
                temp.put("id", obj.getId());
                temp.put("imgname", obj.getName());
                String url = domainURL + "video.jsp?c=" + companyid + "&f=" + obj.getId().concat(obj.getExtn()) + "&t=" + fType;
                temp.put("url", url);
                jobj.append("data", temp);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }
    
    public ModelAndView getEmailTemplateFiles(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("type", request.getParameter("type"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));

            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getEmailTemplateFiles(requestParams);
            jobj = getEmailTemplateFilesJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView saveEmailTemplateFiles(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	JSONObject result = new JSONObject();
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
        	result.put("success", false);
        	int file_type = 1;
            String fType = request.getParameter("type");
            if (fType != null && fType.compareTo("img") == 0) {
                file_type = 0;
            }
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String filename = "";
            ServletFileUpload fu = new ServletFileUpload(new DiskFileItemFactory(DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD, new File("/tmp")));
            if (fu.isMultipartContent(request)) {
            	List<FileItem> fileItems = fu.parseRequest(request);
                for (FileItem field:fileItems) {
                    if (!field.isFormField()) {
                        String fname = new String(field.getName().getBytes(), "UTF8");
                        String file_id = java.util.UUID.randomUUID().toString();
                        String file_extn = fname.substring(fname.lastIndexOf("."));
                        filename = file_id.concat(file_extn);
                        boolean isUploaded = false;
                        fname = fname.substring(fname.lastIndexOf("\\") + 1);
                        if (field.getSize() != 0) {
                        	String basePath = StorageHandler.GetDocStorePath() + companyid + "/" + fType;
                            File destDir = new File(basePath);
                            if (!destDir.exists()) {
                                destDir.mkdirs();
                            }
                            File uploadFile = new File(basePath + "/" + filename);
                            field.write(uploadFile);
                            isUploaded = true;
                            String id = request.getParameter("fileid");
                            if(StringUtil.isNullOrEmpty(id)) {
                                id = file_id;
                            }

                            crmEmailMarketingDAOObj.saveEmailTemplateFile(id,fname, file_extn, new Date(), file_type, sessionHandlerImplObj.getUserid());                           
                        }
                    }
                }
            }
            txnManager.commit(status);
            result.put("success", true);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
            try{result.put("msg", e.getMessage());}catch (Exception je) {}
        } catch (UnsupportedEncodingException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
            try{result.put("msg", ex.getMessage());}catch (Exception je) {}
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
            try{result.put("msg", e.getMessage());}catch (Exception je) {}
        }
        return new ModelAndView("jsonView", "model", result.toString());
    }

    public JSONObject getThemeJson(List ll, int totalSize) {
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                JSONObject temp = new JSONObject();
                themeImages obj = (themeImages) ite.next();
                temp.put("id", obj.getId());
                temp.put("name", obj.getImagename());
                temp.put("url", obj.getUrl());
                temp.put("height", obj.getHeight());
                jobj.append("data", temp);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }
   
    public ModelAndView getThemeImages(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getThemeImages();
            jobj = getThemeJson(kmsg.getEntityList(), kmsg.getRecordTotalCount());
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getBounceReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String emailmarketingid = request.getParameter("emailmarketingid");
            HashMap requestParams = new HashMap();
            if(!StringUtil.isNullOrEmpty(emailmarketingid)){
                requestParams.put("emailmarketingid", emailmarketingid);
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("start"))){
                requestParams.put("start", request.getParameter("start"));
                requestParams.put("limit", request.getParameter("limit"));
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("type"))){
                requestParams.put("type", request.getParameter("type"));
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("ss"))){
                requestParams.put("ss", request.getParameter("ss"));
            }
                
            kmsg = bounceHandlerImplObj.getBounceReport(requestParams);
            jobj = (JSONObject) kmsg.getEntityList().get(0);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getBounceReportExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String view = "jsonView";
        try {
            String emailmarketingid = request.getParameter("emailmarketid");
            HashMap requestParams = new HashMap();
            requestParams.put("isexport", true);
            if(!StringUtil.isNullOrEmpty(emailmarketingid)){
                requestParams.put("emailmarketingid", emailmarketingid);
            }
//            if(!StringUtil.isNullOrEmpty(request.getParameter("start"))){
//                requestParams.put("start", request.getParameter("start"));
//                requestParams.put("limit", request.getParameter("limit"));
//            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("bouncereportcombo"))){
                requestParams.put("type", request.getParameter("bouncereportcombo"));
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("ss"))){
                requestParams.put("ss", request.getParameter("ss"));
            }

            kmsg = bounceHandlerImplObj.getBounceReport(requestParams);
            jobj = (JSONObject) kmsg.getEntityList().get(0);
            
            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.TARGET_EXPORT,
                    "Target data exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView checkBounceStatus(HttpServletRequest request, HttpServletResponse response)
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
            myjobj.put("success", false);
            kmsg = bounceHandlerImplObj.checkBounceStatus();
            myjobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView deleteBouncedTargets(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            myjobj = new JSONObject();
            myjobj.put("success", false);
            String[] targets = request.getParameter("targets").split(",");
            kmsg = bounceHandlerImplObj.deleteBouncedTargets(targets);
            myjobj.put("success", kmsg.isSuccessFlag());
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

    public ModelAndView sendTemplateTestMail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            myjobj = new JSONObject();
            String bodyhtml = StringUtil.checkForNull(request.getParameter("bodyhtml"));
            String subject = StringUtil.checkForNull(request.getParameter("tsubject"));
            User user = profileHandlerDAOObj.getUserObject(sessionHandlerImpl.getUserid(request));
            String mailid = StringUtil.checkForNull(user.getEmailID());
            myjobj.put("valid", true);
            if(!StringUtil.isNullOrEmpty(mailid)) {
                try {
                    String baseUrl = com.krawler.common.util.URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull);
                    bodyhtml = bodyhtml.replaceAll("src=\"[^\"]*?video.jsp", "src=\""+baseUrl + "video.jsp");
                    SendMailHandler.postMail(new String[] {mailid}, subject, bodyhtml, "", storageHandlerImpl.GetNewsLetterAddress());
                    myjobj.put("data", "{success: true, msg: Test mail sent to your registerd mail id}");
                } catch(Exception e) {
                    myjobj.put("data", "{success: true, msg: " + e.getMessage() + "}");
                }
            } else {
                myjobj.put("data", "{success: false, errormsg: No emailid specified}");
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
    
    public RedirectView trackUrl(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String origUrl = request.getParameter("origurl");
    	String trackid = request.getParameter("trackid");
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            crmEmailMarketingDAOObj.trackUrl(trackid, origUrl, new Date());
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } 
    	return new RedirectView(origUrl);
    }

    public JSONObject getViewedEmailMarketingJSON(KwlReturnObject retObj, String dateFormatId, String timeFormatId, String timeZoneDiff, Boolean export) {
        JSONObject jobj = new JSONObject();
        try {
            List<Object[]> list = retObj.getEntityList();
            if (export) {
                jobj.put("coldata", new JSONArray());
            } else {
                jobj.put("data", new JSONArray());
            }

            for (Object[] rows : list) {
                JSONObject obj = new JSONObject();
                Long lastViewTime = null;
                if (rows[1] != null) {
                    lastViewTime = (Long) rows[6];
                }
                try {
                    obj.put("username", StringUtil.getFullName((String) rows[4], (String) rows[5]));
                } catch (Exception e) {
                    obj.put("username", "");
                }
                obj.put("hitcount", rows[1]);
                obj.put("campaignlogid", rows[0]);
                obj.put("recentview", lastViewTime);
                obj.put("marketingname", rows[3]);
                obj.put("campaignname", rows[2]);
                obj.put("emailid", rows[7]);
                obj.put("targetlistname", rows[8]);

                if (export) {
                    jobj.append("coldata", obj);
                } else {
                    jobj.append("data", obj);
                }
            }
            jobj.put("count", retObj.getRecordTotalCount());
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return jobj;
    }
    
    public ModelAndView getViewedEmailMarketing(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String emailMarketingid = request.getParameter("emailmarketingid");
    	String orderbyField = request.getParameter("orderby");
    	String filterUsernameOrEmail = request.getParameter("ss");
    	JSONObject jobj = new JSONObject();
    	int start=0, limit=25;
    	try {
    		start = Integer.parseInt(request.getParameter("start"));
    		limit = Integer.parseInt(request.getParameter("limit"));
    	}catch(Exception e){
    	}

        try {
            String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            KwlReturnObject retObj=crmEmailMarketingDAOObj.getViewedEmailMarketing(emailMarketingid, orderbyField, filterUsernameOrEmail, start, limit);

            jobj = getViewedEmailMarketingJSON(retObj,dateFormatId,timeFormatId,timeZoneDiff,false);
            
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } 
    	return new ModelAndView("jsonView", "model", jobj.toString());
    }


    public ModelAndView getViewedEmailMarketingExport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String emailMarketingid = request.getParameter("emailmarketid");
    	String orderbyField = request.getParameter("orderby");
    	String filterUsernameOrEmail = request.getParameter("ss");
        String view = "jsonView";
    	JSONObject jobj = new JSONObject();
    	int start=0, limit=0;
    	try {
    		start = Integer.parseInt(request.getParameter("start"));
    		limit = Integer.parseInt(request.getParameter("limit"));
    	}catch(Exception e){
    	}

        try {
            String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            KwlReturnObject retObj=crmEmailMarketingDAOObj.getViewedEmailMarketing(emailMarketingid, orderbyField, filterUsernameOrEmail, start, limit);
            
            jobj = getViewedEmailMarketingJSON(retObj,dateFormatId,timeFormatId,timeZoneDiff,true);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);

        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
    	return new ModelAndView(view, "model", jobj.toString());
    }

    
    public ModelAndView getViewedEmailMarketingTiming(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String campaignlogid = request.getParameter("campaignlogid");
    	JSONObject jobj = new JSONObject();
        try {
            List<CampaignTimeLog> list=crmEmailMarketingDAOObj.getViewedEmailMarketingTiming(campaignlogid);
            jobj.put("data", new JSONArray());
            for(CampaignTimeLog tl:list){
            	JSONObject obj = new JSONObject();
            	CampaignLog cl = tl.getCampaignLog();
            	obj.put("viewtime",tl.getViewedon());
            	obj.put("marketingname", cl.getEmailmarketingid().getName());
            	obj.put("campaignname", cl.getCampaignid().getCampaignname());
            	jobj.append("data", obj);
            }
            
        }  catch (JSONException e) {
             logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } 
    	return new ModelAndView("jsonView", "model", jobj.toString());
    }
    public JSONObject getUrlTrackingJSON(KwlReturnObject retObj, String dateFormatId, String timeFormatId, String timeZoneDiff, Boolean export) {
        JSONObject jobj = new JSONObject();
        try {
            List<Object[]> list = retObj.getEntityList();
            if (export) {
                jobj.put("coldata", new JSONArray());
            } else {
                jobj.put("data", new JSONArray());
            }
            //0"id"1"campaignname"2"name"3"fname"4"lname"5"hitcount"6"latesttime"7"latesturl",8"emailid"
            for (Object[] rows : list) {
                JSONObject obj = new JSONObject();
                Long lastClickTime =null;
                if (rows[6] != null && rows[6] instanceof Long) {
                    lastClickTime = (Long) rows[6];
                }
                try {
                    obj.put("username", StringUtil.getFullName((String) rows[3], (String) rows[4]));
                } catch (Exception e) {
                    obj.put("uname", "");
                }
                obj.put("hitcount", rows[5]);
                obj.put("campaignlogid", rows[0]);
                obj.put("recenthit", lastClickTime);
                obj.put("marketingname", rows[2]);
                obj.put("campaignname", rows[1]);
                obj.put("recentlink", rows[7]);
                obj.put("emailid", rows[8]);
                obj.put("targetlistname", rows[9]);

                if (export) {
                    jobj.append("coldata", obj);
                } else {
                    jobj.append("data", obj);
                }
            }
            jobj.put("count", retObj.getRecordTotalCount());
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return jobj;
    }
    public ModelAndView getUrlTracking(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String emailMarketingid = request.getParameter("emailmarketingid");
    	String orderbyField = request.getParameter("orderby");
    	String filterUsernameOrEmail = request.getParameter("ss");
    	JSONObject jobj = new JSONObject();
    	int start=0, limit=25;
    	try {
    		start = Integer.parseInt(request.getParameter("start"));
    		limit = Integer.parseInt(request.getParameter("limit"));
    	}catch(Exception e){
    	}

        try {
            String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            KwlReturnObject retObj=crmEmailMarketingDAOObj.getUrlTracking(emailMarketingid, orderbyField, filterUsernameOrEmail, start, limit);

            jobj = getUrlTrackingJSON(retObj,dateFormatId,timeFormatId,timeZoneDiff,false);
            
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
        }  catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } 
    	return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getUrlTrackingExport(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String emailMarketingid = request.getParameter("emailmarketid");
    	String orderbyField = request.getParameter("orderby");
    	String filterUsernameOrEmail = request.getParameter("ss");
        String view = "jsonView";
    	JSONObject jobj = new JSONObject();
    	int start=0, limit=0;
    	try {
    		start = Integer.parseInt(request.getParameter("start"));
    		limit = Integer.parseInt(request.getParameter("limit"));
    	}catch(Exception e){
    	}

        try {
            String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            KwlReturnObject retObj=crmEmailMarketingDAOObj.getUrlTracking(emailMarketingid, orderbyField, filterUsernameOrEmail, start, limit);

            jobj = getUrlTrackingJSON(retObj,dateFormatId,timeFormatId,timeZoneDiff,true);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);

        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
        }  catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
    	return new ModelAndView(view, "model", jobj.toString());
    }
   
    public ModelAndView getUrlTrackingDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String campaignlogid = request.getParameter("campaignlogid");
    	JSONObject jobj = new JSONObject();
        try {
            List<UrlTrackLog> list=crmEmailMarketingDAOObj.getUrlTrackingDetail(campaignlogid);
            String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            jobj.put("data", new JSONArray());
            for(UrlTrackLog tl:list){
            	JSONObject obj = new JSONObject();
            	CampaignLog cl = tl.getCampaignLog();
            	obj.put("hittime",tl.getClickedOn());
            	obj.put("hiturl", tl.getUrl());
            	obj.put("marketingname", cl.getEmailmarketingid().getName());
            	obj.put("campaignname", cl.getCampaignid().getCampaignname());
            	jobj.append("data", obj);
            }
            
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
         } catch (JSONException e) {
             logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } 
    	return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView getEmailMarketingDefaults(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String emid = request.getParameter("emailmarketingid");
    	JSONObject jobj = new JSONObject();
        try {
            List<EmailMarketingDefault> list=crmEmailMarketingDAOObj.getEmailMarketingDefaults(emid);
            jobj.put("data", new JSONArray());
            for(EmailMarketingDefault emd:list){
            	JSONObject obj = new JSONObject();
            	obj.put("id", emd.getId());
            	obj.put("varname", emd.getVariableName());
            	obj.put("varval", emd.getDefaultValue());
            	jobj.append("data", obj);
            }
            
        } catch (JSONException e) {
             logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } 
    	return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView updateEmailMarketingDefaults(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	String emid = request.getParameter("emailmarketingid");
    	JSONObject jobj = new JSONObject();
    	EmailMarketingDefault emDefault;

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
        	JSONArray jArr = new JSONArray(request.getParameter("defaults"));
        	crmEmailMarketingDAOObj.removeEmailMarketingDefaults(emid);
        	EmailMarketing em=crmEmailMarketingDAOObj.getEmailMarketing(emid);
        	for(int i=0; i<jArr.length();i++){
        		JSONObject obj = jArr.getJSONObject(i);
        		emDefault = new EmailMarketingDefault();
        		emDefault.setEmailMarketing(em);
        		emDefault.setVariableName(obj.getString("varname"));
        		emDefault.setDefaultValue(obj.getString("varval"));
        		crmEmailMarketingDAOObj.saveEmailMarketingDefault(emDefault);
        	}
            txnManager.commit(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        } 
    	return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView resetEmailType(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("success", false);
            String companyid = sessionHandlerImpl.getCompanyid(request);

            crmEmailMarketingDAOObj.deleteEmailType(companyid);

            jobj.put("success", true);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    private Receiver getCrmURLReceiver(HashMap<String, Object> requestParams) {
        return new Receiver() {
            private HashMap<String, Object> requestParams = null;
            public Receiver setValues(HashMap<String, Object> requestParams) {
                this.requestParams = requestParams;
                return this;
            }
            @Override
            public void receive(Object resultObj) {
                JSONObject jobj = null;

                try {
                	jobj = (JSONObject) resultObj;
                    String loginUrl = ConfigReader.getinstance().get("crmURL",null);
                    if(jobj!=null&&jobj.has(com.krawler.common.util.Constants.CRMURL) && !StringUtil.isNullOrEmpty(jobj.getString(com.krawler.common.util.Constants.CRMURL))) {
                        loginUrl = jobj.getString(com.krawler.common.util.Constants.CRMURL);
                    }
                    requestParams.put("baseurl", StringUtil.appendSubDomain(loginUrl, requestParams.get("subdomain").toString(), false));
                    crmEmailMarketingDAOObj.sendEmailMarketMail(requestParams);
                } catch (Exception ex) {
                    logger.warn("Cannot get CRM URL from Apps: " + ex.toString());
                }
            }
        }.setValues(requestParams);
    }
        
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if(appStarted)return;
		appStarted = true;
		List emids=new ArrayList();
        try {
        	List<Object[]> rows = crmEmailMarketingDAOObj.getInterruptedEmailMarketings();
        	for(Object[] row : rows){
	            HashMap<String, Object> requestParams = new HashMap<String, Object>();
	            emids.add(row[0]);
	            requestParams.put("userid", row[1]);
	            requestParams.put("emailmarkid", row[0]);
	            requestParams.put("resume", true);
	            requestParams.put("subdomain", (String)row[3]);
	        	String platformUrl = ConfigReader.getinstance().get("platformURL",null);
	            if(platformUrl==null) {
	                String loginUrl = URLUtil.getDomainURL((String)row[3], false);
	                requestParams.put("baseurl", loginUrl);
	                crmEmailMarketingDAOObj.sendEmailMarketMail(requestParams);
	            } else {
	                String companyid = (String)row[2];
	                JSONObject temp = new JSONObject();
	                temp.put("companyid", companyid);
	                apiCallHandlerService.callApp(platformUrl, temp, companyid, "13", false, getCrmURLReceiver(requestParams));
	            }
        	}
        	if(!emids.isEmpty())
        		logger.info("Resuming campaigns:"+Arrays.toString(emids.toArray()));
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
		
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.mSource=messageSource;
		
	}
}
