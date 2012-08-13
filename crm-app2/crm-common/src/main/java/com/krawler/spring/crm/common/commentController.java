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

import com.krawler.spring.comments.*;
import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.Comment;
import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;
//import com.krawler.crm.database.tables.CrmAccount;
//import com.krawler.crm.database.tables.CrmActivityMaster;
//import com.krawler.crm.database.tables.CrmCampaign;
//import com.krawler.crm.database.tables.CrmCase;
//import com.krawler.crm.database.tables.CrmContact;
//import com.krawler.crm.database.tables.CrmLead;
//import com.krawler.crm.database.tables.CrmOpportunity;
//import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.AuditAction;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class commentController extends MultiActionController {

    private commentDAO crmCommentDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private HibernateTemplate hibernateTemplate;
    private String successView;
    private CometManagementService cometManagementService;
    private HibernateTransactionManager txnManager;

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }
    
    public void setCometManagementService(CometManagementService cometManagementService) {
		this.cometManagementService = cometManagementService;
	}
    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
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
    
    public JSONObject getCommentJson(List ll, HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                JSONObject temp = new JSONObject();
                Comment t = (Comment) ite.next();
                temp.put("comment", t.getComment());
                temp.put("postedon", KwlCommonTablesDAOObj.getUserDateFormatter(dateFormatId, timeFormatId, timeZoneDiff).format(t.getPostedon()));
                temp.put("addedby", profileHandlerDAOObj.getUserFullName(t.getuserId().getUserID()));
                jarr.put(temp);
                kmsg = crmCommentDAOObj.deleteComments(userid, t.getId());
            }
            jobj.put("commPerm", ((sessionHandlerImpl.getPerms(request, "Comments") & 2) == 2));
            jobj.put("commList", jarr);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public ModelAndView getComments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String recid = StringUtil.checkForNull(request.getParameter("recid"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("recid", recid);
            kmsg = crmCommentDAOObj.getComments(requestParams);
            jobj = getCommentJson(kmsg.getEntityList(), request);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView addComments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        Object c;
        String details = "";
        String auditAction = "";
        String id = java.util.UUID.randomUUID().toString();
        String randomnumber=request.getParameter("randomnumber");
        String moduleName=request.getParameter("modulename");
        String jsondata = request.getParameter("jsondata");
        String map="";
        String refid="";
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            JSONArray jarr = new JSONArray("[" + jsondata + "]");
            for (int i = 0; i < jarr.length(); i++) {
                jobj = jarr.getJSONObject(i);
                map = jobj.getString("mapid");
                refid = jobj.getString("leadid");
                String commStrAudit = StringUtil.serverHTMLStripper(jobj.getString("comment"));
                commStrAudit = commStrAudit.replaceAll("&nbsp;", "");
                String cid = java.util.UUID.randomUUID().toString();
                String commentid = jobj.getString("commentid");
                jobj.put("userid", userid);
                jobj.put("companyid", companyid);
                jobj.put("cid", cid);
                jobj.put("refid", refid);
                if(StringUtil.isNullOrEmpty(commentid)){ // Add Mode
                    jobj.put("id", id);
                	if(moduleName.equals(Constants.Case)){//Case                    						
                		crmCommentDAOObj.addCaseComments(jobj);
                	}else{
                		kmsg = crmCommentDAOObj.addComments(jobj);
                    }
                } else { // Edit Mode
                	
        			jobj.put("id", commentid.trim());
        			if(moduleName.equals(Constants.Case)){
        				kmsg = crmCommentDAOObj.editCaseComments(jobj);
        			}else{
        				   kmsg = crmCommentDAOObj.editComments(jobj);
                    }
                }

                //@@@ - Need to restructure this code
                if (map.equals("0")) {
                     c = hibernateTemplate.get("com.krawler.crm.database.tables.CrmCampaign", refid);
                    details = " Campaign - ";
                    details+=StringUtil.isNullOrEmpty(BeanUtils.getProperty(c, "campaignname"))?"":BeanUtils.getProperty(c, "campaignname");
                    auditAction = AuditAction.CAMPAIGN_ADD_COMMENTS;
                } else if (map.equals("1")) {
                      c = hibernateTemplate.get("com.krawler.crm.database.tables.CrmLead", refid);
                    details = " Lead - ";
                    details+= StringUtil.isNullOrEmpty(BeanUtils.getProperty(c, "firstname"))?"":BeanUtils.getProperty(c, "firstname")+" ";
                    details+=StringUtil.isNullOrEmpty(BeanUtils.getProperty(c,"lastname"))?"":BeanUtils.getProperty(c,"lastname");
                    auditAction = AuditAction.LEAD_ADD_COMMENTS;
                } else if (map.equals("2")) {
                     c =  hibernateTemplate.get("com.krawler.crm.database.tables.CrmContact", refid);
                    details = " Contact - ";
                    details+=StringUtil.isNullOrEmpty(BeanUtils.getProperty(c,"firstname"))?"":BeanUtils.getProperty(c,"firstname");
                    details+=StringUtil.isNullOrEmpty(BeanUtils.getProperty(c,"lastname"))?"":BeanUtils.getProperty(c,"lastname");
                    auditAction = AuditAction.CONTACT_ADD_COMMENTS;
                } else if (map.equals("3")) {
                    c =  hibernateTemplate.get("com.krawler.crm.database.tables.CrmProduct", refid);
                    details = " Product - ";
                    details+= StringUtil.isNullOrEmpty(BeanUtils.getProperty(c,"productname"))?"":BeanUtils.getProperty(c,"productname");
                    auditAction = AuditAction.PRODUCT_ADD_COMMENTS;
                } else if (map.equals("4")) {
                     c =  hibernateTemplate.get("com.krawler.crm.database.tables.CrmAccount", refid);
                    details = " Account - ";
                    details+= StringUtil.isNullOrEmpty(BeanUtils.getProperty(c,"accountname"))?"":BeanUtils.getProperty(c,"accountname");
                    auditAction = AuditAction.ACCOUNT_ADD_COMMENTS;
                } else if (map.equals("5")) {
                    c =  hibernateTemplate.get("com.krawler.crm.database.tables.CrmOpportunity", refid);
                    details = " Opportunity - ";
                    details+= StringUtil.isNullOrEmpty(BeanUtils.getProperty(c,"oppname"))?"":BeanUtils.getProperty(c,"oppname");
                    auditAction = AuditAction.OPPORTUNITY_ADD_COMMENTS;
                } else if (map.equals("6")) {
                     c =  hibernateTemplate.get("com.krawler.crm.database.tables.CrmCase", refid);
                    details = " Case - ";
                    details+=StringUtil.isNullOrEmpty(BeanUtils.getProperty(c,"subject"))?"":BeanUtils.getProperty(c,"subject");
                    auditAction = AuditAction.CASE_ADD_COMMENTS;
                } else if (map.equals("7")) {
                     c =  hibernateTemplate.get("com.krawler.crm.database.tables.CrmActivityMaster", refid);
                      String subject = StringUtil.isNullOrEmpty(BeanUtils.getProperty(c, "subject"))?"":"("+BeanUtils.getProperty(c, "subject")+")";
//                      String aname = BeanUtils.getProperty(c,"crmCombodataByStatusid.aliasName");
//                    if(!StringUtil.isNullOrEmpty(aname)){
//                        comboStatus=aname;
//                    }
                    details = " Activity - " + BeanUtils.getProperty(c, "flag") + " " + subject;
                    auditAction = AuditAction.ACTIVITY_ADD_COMMENTS;
                }
                auditTrailDAOObj.insertAuditLog(auditAction,
                        " Comment: '" + commStrAudit + "' , added for " + details,
                        request, refid, id);
            }
            myjobj.put("ID", id);
            txnManager.commit(status);
            
            // add comment on other logins for same user
            try{
            	JSONObject commetMap=new JSONObject();
            	boolean isCommentAdded=kmsg.isSuccessFlag();
            	int operationCode=CrmPublisherHandler.ADDCOMMENT;
            	commetMap.put("isCommentAdded", isCommentAdded);
            	commetMap.put("recid", refid);
            	commetMap.put("modulename", moduleName);
                cometManagementService.publishModuleInformation(companyid,userid,randomnumber,moduleName,refid,operationCode,commetMap,CometConstants.CRMUPDATES);
			}catch(Exception e){}
	    } catch (Exception e) {
            System.out.println(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView deleteOriginalComment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setName("JE_Tx");
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
//        TransactionStatus status = txnManager.getTransaction(def);
        try {
            myjobj.put("success", true);
            String deletedId = request.getParameter("id");
            String moduleName = request.getParameter("moduleName");
            if(moduleName.equals(Constants.Case)){
            	crmCommentDAOObj.deleteCaseComment(deletedId);
            }
            crmCommentDAOObj.deleteOriginalComment(deletedId);
            myjobj.put("ID", deletedId);
            myjobj.put("success", true);
//            txnManager.commit(status);
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
//            txnManager.rollback(status);
        } catch (Exception e) {
            System.out.println(e.getMessage());
//            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    
	
    
    
}
