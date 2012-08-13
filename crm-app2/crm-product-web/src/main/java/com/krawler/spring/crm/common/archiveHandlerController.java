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

import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.spring.archive.archiveHandlerDAO;
import com.krawler.utils.json.base.JSONException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import javax.servlet.http.HttpServletRequest;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.campaignModule.crmCampaignDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.net.URLDecoder;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class archiveHandlerController extends MultiActionController {

    private archiveHandlerDAO archiveHandlerDAOObj;
    private auditTrailDAO auditTraildao;
    private String successView;
    private crmManagerDAO crmManagerDAOObj;
    private HibernateTransactionManager txnManager;
    private crmCampaignDAO crmCampaignDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private CometManagementService CometManagementService;
    public void setCometManagementService(CometManagementService CometManagementService) {
        this.CometManagementService = CometManagementService;
    }
    
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    
    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
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

    public void setarchiveHandlerDAO(archiveHandlerDAO archiveHandlerDAOObj1) {
        this.archiveHandlerDAOObj = archiveHandlerDAOObj1;
    }

    public void setauditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTraildao = auditTrailDAOObj1;
    }

    public void setcrmCampaignDAO(crmCampaignDAO crmCampaignDAOObj1) {
        this.crmCampaignDAOObj = crmCampaignDAOObj1;
    }

    public ModelAndView archiveGlobal(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        JSONArray jarr = null;
        try {
            myjobj.put("success", false);
            int operationCode = CrmPublisherHandler.ARCHIVEDRECORDCODE;
            String moduleName = request.getParameter("module");
            String classname = ConfigReader.getinstance().get("CrmClassPath")+moduleName;
            String module = request.getParameter("auditMod");
            String auditVal = request.getParameter("auditNo");
            if (StringUtil.bNull(request.getParameter("jsondata"))) {
                String jsondata = request.getParameter("jsondata");
                jarr = new JSONArray("[" + jsondata + "]");
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jo = jarr.getJSONObject(i);
                    if((jo.getString("id").equals("0")))
                    	continue;
                    String archive = request.getParameter("text");
                    jo.put("classname", classname);
                    if (archive.equals("Archive")) {
                        jo.put("archive", true);
                    } else {
                        operationCode = CrmPublisherHandler.UNARCHIVEDRECORDCODE;
                        jo.put("archive", false);
                    }
                    kmsg = archiveHandlerDAOObj.archiveGlobal(jo);
                    int valid = (Integer) kmsg.getEntityList().get(0);
                    String name = "";
                    if (!StringUtil.isNullOrEmpty(jo.getString("name"))) {
                        name = URLDecoder.decode(jo.getString("name"), "UTF-8");
                    }
                    if (archive.equals("Archive") && valid == 1) {
                        auditTraildao.insertAuditLog(auditVal,
                                name + " - " + module + " archived ",
                                request, jo.getString("id"));
                    } else if (valid == 1) {
                        auditTraildao.insertAuditLog(auditVal,
                                name + " - " + module + " restored ",
                                request, jo.getString("id"));
                    }
                    if(request.getParameter("module").equals("CrmCampaign")) {
                        CrmCampaign crmCampaign = (CrmCampaign) kmsg.getEntityList().get(1);
                        crmCampaignDAOObj.updateDefaultMasterItemForCampaign(crmCampaign);
                    }
                
               }

            }
            myjobj.put("success", true);
            txnManager.commit(status);

            if(jarr!=null) {
                JSONObject cometObj = new JSONObject();
                cometObj.put("ids",  jarr);
                String moduleRecIdName ="";
                if(Constants.Crm_lead_pojo.equals(moduleName)) {
                    moduleRecIdName = Constants.Crm_leadid;
                    moduleName = Constants.Crm_Lead_modulename;
                } else if(Constants.Crm_account_pojo.equals(moduleName)) {
                    moduleRecIdName = Constants.Crm_accountid;
                    moduleName = Constants.Crm_Account_modulename;
                } else if(Constants.Crm_case_pojo.equals(moduleName)) {
                    moduleRecIdName = Constants.Crm_caseid;
                    moduleName = Constants.Crm_Case_modulename;
                } else if(Constants.Crm_contact_pojo.equals(moduleName)) {
                    moduleRecIdName = Constants.Crm_contactid;
                    moduleName = Constants.Crm_Contact_modulename;
                } else if(Constants.Crm_product_pojo.equals(moduleName)) {
                    moduleRecIdName = Constants.Crm_productid;
                    moduleName = Constants.Crm_Product_modulename;
                } else if(Constants.Crm_opportunity_pojo.equals(moduleName)) {
                    moduleRecIdName = Constants.Crm_opportunityid;
                    moduleName = Constants.Crm_Opportunity_modulename;
                } else if(Constants.Crm_campaign_pojo.equals(moduleName)) {
                    moduleRecIdName = Constants.Crm_campaignid;
                    moduleName = Constants.CRM_CAMPAIGN_MODULENAME;
                } else if(Constants.CRM_ACTIVITY_POJO.equals(moduleName)) {
                    moduleRecIdName = Constants.Crm_activityid;
                    moduleName = Constants.CRM_ACTIVITY_MODULENAME;
                }
                publishArchiveInformation(request, cometObj, moduleName, moduleRecIdName, operationCode, sessionHandlerImplObj.getCompanyid(request));
            }
            
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    private void publishArchiveInformation(HttpServletRequest request, JSONObject cometObj, String moduleName, String moduleRecIdName, int operationCode, String companyid) throws SessionExpiredException, ServiceException, JSONException {
        String userid = sessionHandlerImplObj.getUserid(request);
        String randomNumber = request.getParameter("randomnumber");
        CometManagementService.publishModuleInformation(companyid, userid, randomNumber, moduleName, moduleRecIdName, operationCode, cometObj, CometConstants.CRMUPDATES);
    }
}
