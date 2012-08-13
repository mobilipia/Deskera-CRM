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
package com.krawler.spring.crm.campaignModule; 

import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.campaign.bizservice.CampaignManagementService;
import com.krawler.crm.database.tables.CrmCampaign;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.spring.crm.common.CrmCommonService;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.util.Date;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.spring.crm.emailMarketing.CampaignConstants;
import java.util.ArrayList;
import java.util.List;
public class crmCampaignController extends MultiActionController {

    private crmCampaignDAO crmCampaignDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private commentDAO crmCommentDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private CampaignManagementService campaignManagementService;
    private CometManagementService CometManagementService;
    private CrmCommonService crmCommonService;

        
	public void setCrmCommonService(CrmCommonService crmCommonService) {
		this.crmCommonService = crmCommonService;
	}

    public void setCometManagementService(CometManagementService CometManagementService) {
        this.CometManagementService = CometManagementService;
    }

    public void setCampaignManagementService(CampaignManagementService campaignManagementService) {
        this.campaignManagementService = campaignManagementService;
    }

    public CampaignManagementService getCampaignManagementService() {
        return this.campaignManagementService;
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

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }

    public void setcrmCampaignDAO(crmCampaignDAO crmCampaignDAOObj1) {
        this.crmCampaignDAOObj = crmCampaignDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public ModelAndView getCampaigns(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            String isarchive = request.getParameter("isarchive");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String emailcampaign = request.getParameter("emailcampaign");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            String iscustomcolumn = request.getParameter("iscustomcolumn");
            String xtype = request.getParameter("xtype");
            String xfield = request.getParameter("xfield");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");

            jobj = getCampaignManagementService().getCampaigns(companyid, userid, currencyid, selectExport, isarchive,
                    searchJson, ss, config, isExport, heirarchyPerm, field, direction, iscustomcolumn, xtype, xfield, start, limit, dateFormat,emailcampaign);

        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveCampaigns(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        CrmCampaign camp = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            Integer operationCode = CrmPublisherHandler.ADDRECORDCODE;
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String id = jobj.getString("campaignid");
            String[] arrayId=new String[]{id};
            jobj.put("userid", userid);
            jobj.put("companyid", companyid);
            jobj.put("updatedon", new Date().getTime());
            if (jobj.has("startdate")) {
                jobj.put("startdate", jobj.getLong("startdate"));
            }
            if (jobj.has("enddate")) {
                jobj.put("enddate", jobj.getLong("enddate"));
            }
            boolean reloadLeadSourceStore=false;
            int prevValidFlagState=1;
            if (id.equals("0")) {
                id = java.util.UUID.randomUUID().toString();
                jobj.put("campaignid", id);
                kmsg = crmCampaignDAOObj.addCampaigns(jobj);
                camp = (CrmCampaign) kmsg.getEntityList().get(0);

                if (camp.getValidflag() == 1) {
                    auditTrailDAOObj.insertAuditLog(AuditAction.CAMPAIGN_CREATE,
                            camp.getCampaignname() + " - Campaign created ",
                            request, id);
                }
            } else {
                operationCode = CrmPublisherHandler.UPDATERECORDCODE;
                if(jobj.has("createdon")){
                    jobj.put("createdon", jobj.getLong("createdon"));
                }
                kmsg = crmCampaignDAOObj.editCampaigns(jobj);
                camp = (CrmCampaign) kmsg.getEntityList().get(0);
                if (camp.getValidflag() == 1) {
                    auditTrailDAOObj.insertAuditLog(AuditAction.CAMPAIGN_UPDATE,
                            jobj.getString("auditstr") + " Campaign - " + camp.getCampaignname() + " ",
                            request, id);
                }
            }
            if (camp.getValidflag() == prevValidFlagState || jobj.has("campaignname") ) {
                crmCampaignDAOObj.updateDefaultMasterItemForCampaign(camp);
                reloadLeadSourceStore=true;
            }
            if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
          	   if(arrayId[0].equals("0"))
          		   crmCommonService.validateMassupdate(new String[]{camp.getCampaignid()}, "Campaign", companyid);
          	   else
             	   crmCommonService.validateMassupdate(arrayId, "Campaign", companyid);
             }
            myjobj.put("success", true);
            myjobj.put("ID", camp.getCampaignid());
            myjobj.put("reloadLeadSourceStore", reloadLeadSourceStore);
            myjobj.put("validflag", camp.getValidflag());
            myjobj.put("campaignname", camp.getCampaignname());
            myjobj.put("campaigntype", StringUtil.isNullObject(camp.getCrmCombodataByCampaigntypeid())?"":camp.getCrmCombodataByCampaigntypeid().getValue());
            txnManager.commit(status);
            
            JSONObject cometObj = jobj;
            if(!StringUtil.isNullObject(camp)) {
                if(!StringUtil.isNullObject(camp.getCreatedon())) {
                    cometObj.put("createdon", camp.getCreatedonGMT());
                }
            }
            publishCampaignModuleInformation(request, cometObj, operationCode, companyid, userid);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    private void publishCampaignModuleInformation(HttpServletRequest request, JSONObject cometObj, int operationCode, String companyid, String userid) throws ServiceException, JSONException, SessionExpiredException {
        String randomNumber = request.getParameter("randomnumber");
        CometManagementService.publishModuleInformation(companyid, userid, randomNumber, com.krawler.common.util.Constants.CRM_CAMPAIGN_MODULENAME, Constants.Crm_campaignid, operationCode, cometObj, CometConstants.CRMUPDATES);
    }

    public ModelAndView deleteCampaign(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        KwlReturnObject kmsg = null;
        CrmCampaign camp = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            myjobj = new JSONObject();
            myjobj.put(Constants.success, false);
            JSONArray deletejarr =new JSONArray() ;
            JSONArray failDeletejarr =new JSONArray() ;
            ArrayList campaignids = new ArrayList();
            if (StringUtil.bNull(request.getParameter(Constants.jsondata))) {
                String jsondata = request.getParameter(Constants.jsondata);
                JSONArray jarr = new JSONArray("[" + jsondata + "]");
                JSONObject jobjmsg = new JSONObject();

                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobject = jarr.getJSONObject(i);
                    String campaignid = jobject.getString(CampaignConstants.campaignid);
                    
                    boolean iscampaignused =  crmManagerDAOObj.checkMasterDataisUsed(campaignid,null, Constants.leadsource_Combo,  sessionHandlerImpl.getCompanyid(request));
                    camp = crmCampaignDAOObj.getCampaignById(campaignid);
                    if(camp!=null) { // campaign with id not found
                        jobjmsg.put(Constants.name, camp.getCampaignname());
                        jobjmsg.put(Constants.moduleName,Constants.MODULE_Campaign);
                        if (!iscampaignused) {
                            campaignids.add(campaignid);
                            deletejarr.put(jobjmsg);
                        }else{
                            failDeletejarr.put(jobjmsg);
                        }
                    }
                }

                String[] arrayid = (String[]) campaignids.toArray(new String[]{});
                if(arrayid.length>0){
                    JSONObject jobj = new JSONObject();

                    jobj.put(Constants.deleteflag, 1);
                    jobj.put(CampaignConstants.campaignid, arrayid);
                    jobj.put(Constants.userid, userid);
                    jobj.put(Constants.updatedon, new Date());
                    jobj.put("tzdiff",timeZoneDiff);

                    kmsg  = crmCampaignDAOObj.updateMassCampaigns(jobj);
                }
                List<CrmCampaign> ll = crmCampaignDAOObj.getCampaigns(campaignids);

                if(ll!=null){
                    for(int i =0 ; i< ll.size() ; i++){
                        CrmCampaign campaignaudit = (CrmCampaign)ll.get(i);
                        if (campaignaudit.getValidflag() == 1) {
                            auditTrailDAOObj.insertAuditLog(AuditAction.CAMPAIGN_DELETE,
                                  campaignaudit.getCampaignname() + " - Campaign deleted ",
                                    request, campaignaudit.getCampaignid());
                        }
                    }

                }
                    
                JSONObject cometObj = new JSONObject();
                cometObj.put("ids",  new JSONArray("[" + jsondata + "]"));
                publishCampaignModuleInformation(request, cometObj, CrmPublisherHandler.DELETERECORDCODE, sessionHandlerImpl.getCompanyid(request),userid);
            }
            if(request.getAttribute("failDelete") != null){
                
                myjobj.put(Constants.failDelete, (JSONArray)request.getAttribute("failDelete"));
            }
            myjobj.put(Constants.successDeleteArr, deletejarr);
            myjobj.put(Constants.success, true);
            myjobj.put("ID", campaignids.toArray());
            if(camp!=null) {
                myjobj.put("validflag", camp.getValidflag());
                myjobj.put("campaignname", camp.getCampaignname());
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

    public ModelAndView exportCampaign(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        String view = "jsonView";
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            String isarchive = request.getParameter("isarchive");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Campaign");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            String iscustomcolumn = request.getParameter("iscustomcolumn");
            String xtype = request.getParameter("xtype");
            String xfield = request.getParameter("xfield");

            jobj = getCampaignManagementService().campaignExport(companyid, userid, currencyid, selectExport, isarchive,
                    searchJson, ss, config, isExport, heirarchyPerm, field, direction, iscustomcolumn, xtype, xfield, dateFormat);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.CAMPAIGN_EXPORT,
                    "Campaign list exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }
    
    public ModelAndView updateMassCampaigns(HttpServletRequest request,HttpServletResponse response) throws ServletException {
		JSONObject myjobj = new JSONObject();
		KwlReturnObject kmsg = null;
		CrmCampaign camp = null;
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		try {
			JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
			String userid = sessionHandlerImpl.getUserid(request);
			String campaignIds = jobj.getString("campaignid");
			String arrayId[] = campaignIds.split(",");
			Integer operationCode = CrmPublisherHandler.UPDATERECORDCODE;
			String companyid = sessionHandlerImpl.getCompanyid(request);
			jobj.put("userid", userid);
			jobj.put("companyid", companyid);
			if (jobj.has("updatedon")
					&& !StringUtil.isNullOrEmpty(jobj.getString("updatedon"))) {
				jobj.put("updatedon", jobj.getLong("updatedon"));
			} else {
				jobj.put("updatedon", new Date().getTime());
			}
			jobj.put("campaignid", arrayId);
			jobj.put("tzdiff", sessionHandlerImpl.getTimeZoneDifference(request));
			if (jobj.has("createdon")) {
				jobj.put("createdon", jobj.getLong("createdon"));
			}
			kmsg = crmCampaignDAOObj.updateMassCampaigns(jobj);
			// TODO : How to insert audit log when mass update
			txnManager.commit(status);
			JSONObject cometObj = jobj;
			if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
				crmCommonService.validateMassupdate(arrayId, "Campaign",companyid);
				cometObj.put("campaignid", campaignIds);
				cometObj.put("ismassedit", true);
			}
			myjobj.put("success", true);
			myjobj.put("ID", campaignIds);
			myjobj.put("createdon", jobj.has("createdon") ? jobj.getLong("createdon") : "");
			publishCampaignModuleInformation(request, cometObj, operationCode, companyid, userid);
		} catch (Exception e) {
			logger.warn("Exception while Campaign Mass Update:", e);
			txnManager.rollback(status);
		}
		return new ModelAndView("jsonView", "model", myjobj.toString());
	}

}
