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

package com.krawler.spring.crm.treeModule;
 
import com.krawler.common.admin.Docs;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmAccountActivity;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmLeadActivity;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.contactOwners;
import com.krawler.crm.database.tables.opportunityOwners;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.activityModule.crmActivityDAO;
import com.krawler.spring.crm.campaignModule.crmCampaignDAO;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.productModule.crmProductDAO;
import com.krawler.spring.documents.documentDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

public class crmTreeController  extends MultiActionController implements MessageSourceAware{
    private String successView;
    private crmAccountDAO crmAccountDAOObj;
    private crmCampaignDAO crmCampaignDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private crmActivityDAO crmActivityDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmContactDAO crmContactDAOObj;
    private crmCaseDAO crmCaseDAOObj;
    private crmLeadDAO crmLeadDAOObj;
    private crmProductDAO crmProductDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private documentDAO documentDAOObj;
    private MessageSource messageSource;

    public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
		
	}
    public String getSuccessView() {
		return successView;
	}

    public void setSuccessView(String successView) {
		this.successView = successView;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj) {
        this.crmAccountDAOObj = crmAccountDAOObj;
    }

    public void setcrmCampaignDAO(crmCampaignDAO crmCampaignDAOObj1) {
        this.crmCampaignDAOObj = crmCampaignDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAO1) {
        this.crmManagerDAOObj = crmManagerDAO1;
    }

    public void setcrmActivityDAO(crmActivityDAO crmActivityDAOObj1) {
        this.crmActivityDAOObj = crmActivityDAOObj1;
    }

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmCaseDAO(crmCaseDAO crmCaseDAOObj1) {
        this.crmCaseDAOObj = crmCaseDAOObj1;
    }

    public void setcrmProductDAO(crmProductDAO crmProductDAOObj1) {
        this.crmProductDAOObj = crmProductDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setdocumentDAO(documentDAO documentDAOObj1) {
        this.documentDAOObj = documentDAOObj1;
    }

    public ModelAndView getTree (HttpServletRequest request, HttpServletResponse response) throws ServiceException {
        JSONObject jobj = new JSONObject();
        String returnStr = "";
        JSONArray rootArr = new JSONArray();
        int nodeCount = 0;
        try {
            int flag = Integer.parseInt(request.getParameter("mode"));
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            StringBuffer usersList = new StringBuffer();
            usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            switch (flag) {
                case 0:
                    JSONObject accChild = new JSONObject();
                    jobj.put("nodemode", "0");
                    jobj.put("nodetype", "1");
                    jobj.put("level", "0");
                    jobj.put("mode", "0");
                    jobj.put("text", "<span wtf:qtip='Easily add entries such as campaigns, leads and contacts right from the dashboard.'>"+messageSource.getMessage("crm.dashboard.westpanel.quickview",null,RequestContextUtils.getLocale(request))+"</span>");//Quick View
                    if ((sessionHandlerImpl.getPerms(request, "Account") & 1) == 1) {
                        accChild = getAccountForTree(request,userid,companyid,usersList);
                        jobj.append("children", accChild);
                        nodeCount++;
                    }
                    if ((sessionHandlerImpl.getPerms(request, "Lead") & 1) == 1) {
                        accChild = getLeadForTree(request,userid,companyid,usersList);
                        jobj.append("children", accChild);
                        nodeCount++;
                    }
                    if ((sessionHandlerImpl.getPerms(request, "Product") & 1) == 1) {
                        accChild = getProductForTree(request,userid,companyid,usersList);
                        jobj.append("children", accChild);
                        nodeCount++;
                    }
                    if ((sessionHandlerImpl.getPerms(request, "Campaign") & 1) == 1) {
                        accChild = getCampaignForTree(request,userid,companyid,usersList);
                        jobj.append("children", accChild);
                        nodeCount++;
                    }
                    if ((sessionHandlerImpl.getPerms(request, "Document") & 1) == 1) {
                        accChild = getDocumentsforTree(request,userid,companyid,usersList);
                        jobj.append("children", accChild);
                        nodeCount++;
                    }
                    if (nodeCount == 0) {
                        jobj.put("expanded", false);
                        jobj.put("leaf", true);
                    } else {
                        jobj.put("expanded", true);
                    }
                    returnStr = rootArr.put(jobj).toString();
                    break;
                case 1 :
                    returnStr = getAccActivityForTree(request,userid,companyid,usersList);
                    break;
                case 2 :
                    returnStr = getAccOpportunityForTree(request,userid,companyid,usersList);
                    break;
                case 3 :
                    returnStr = getAccContactForTree(request,userid,companyid,usersList);
                    break;
                case 4 :
                    returnStr = getAccCaseForTree(request,userid,companyid,usersList);
                    break;
                case 8:                                 ///////get account cases
                    returnStr = getLeadActivityForTree(request,userid,companyid,usersList);
                    break;
                case 21 :
                    accChild = getAccountForTree(request,userid,companyid,usersList);
                    if(accChild.has("children")) {
                        returnStr = accChild.getJSONArray("children").toString();
                    }
                    break;
                case 22 :
                    accChild = getLeadForTree(request,userid,companyid,usersList);
                    if(accChild.has("children")) {
                        returnStr = accChild.getJSONArray("children").toString();
                    }
                    break;
                case 23 :
                    accChild = getProductForTree(request,userid,companyid,usersList);
                    if(accChild.has("children")) {
                        returnStr = accChild.getJSONArray("children").toString();
                    }
                    break;
                case 24 :
                    accChild = getCampaignForTree(request,userid,companyid,usersList);
                    if(accChild.has("children")) {
                        returnStr = accChild.getJSONArray("children").toString();
                    }
                    break;
                case 25:
                    accChild = getDocumentsforTree(request,userid,companyid,usersList);
                    returnStr = accChild.getJSONArray("children").toString();
                    break;
                case 26:  // Permission check '1' is for View permission and '2' si for manage 
                    if ((sessionHandlerImpl.getPerms(request, "Account") & 1) == 1) {
                        accChild = getAccountForTree(request,userid,companyid,usersList);
                        if(accChild.has("children")) {
                            returnStr = accChild.getJSONArray("children").toString();
                        }
                    }
                    if ((sessionHandlerImpl.getPerms(request, "Lead") & 1) == 1) {
                        accChild = getLeadForTree(request,userid,companyid,usersList);
                        if(accChild.has("children")) {
                            returnStr += accChild.getJSONArray("children").toString();
                        }
                    }
                    if ((sessionHandlerImpl.getPerms(request, "Product") & 1) == 1) {
                        accChild = getProductForTree(request,userid,companyid,usersList);
                        if(accChild.has("children")) {
                            returnStr += accChild.getJSONArray("children").toString();
                        }
                    }
                    if ((sessionHandlerImpl.getPerms(request, "Campaign") & 1) == 1) {
                        accChild = getCampaignForTree(request,userid,companyid,usersList);
                        if(accChild.has("children")) {
                            returnStr += accChild.getJSONArray("children").toString();
                        }
                    }
                    if ((sessionHandlerImpl.getPerms(request, "Document") & 1) == 1) {
                        accChild = getDocumentsforTree(request,userid,companyid,usersList);
                        if(accChild.has("children")) {
                            returnStr += accChild.getJSONArray("children").toString();
                        }
                    }
                    break;
                default:
                    break;
            }
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getTree", e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getTree", e);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getTree", e);
        }
        return new ModelAndView("jsonView-ex", "model", returnStr);
    }

     public String getAccActivityForTree (HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        List ll = null;
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            String aid = request.getParameter("mapid");

            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.crmAccount.deleteflag");filter_values.add(0);
            filter_names.add("c.crmAccount.accountid");filter_values.add(aid);
            filter_names.add("c.crmActivityMaster.company.companyID");filter_values.add(companyid);
            filter_names.add("c.crmActivityMaster.deleteflag");filter_values.add(0);
            filter_names.add("c.crmActivityMaster.validflag");filter_values.add(1);

            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.crmActivityMaster.createdOn");
            order_type.add("desc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            requestParams.put("table_name", "CrmAccountActivity");
            requestParams.put("userlist_param", "c.crmActivityMaster.usersByUserid.userID");
            requestParams.put("userlist_value", usersList);
            kmsg = crmActivityDAOObj.getAccountActivityForTable(requestParams, true);
            ll = kmsg.getEntityList();

            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmAccountActivity accObj = (CrmAccountActivity) ite.next();
                CrmActivityMaster obj = accObj.getCrmActivityMaster();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("text", obj.getFlag() + " " + crmManagerCommon.comboNotNullRaw(obj.getCrmCombodataByTypeid()));
                tmpObj.put("nodeid", obj.getActivityid());
                tmpObj.put("leaf", true);
                jarr.put(tmpObj);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getAccActivityForTree", e);
        }
//        return new ModelAndView("jsonView", "model", jarr.toString());
         return jarr.toString();
    }

    public String getAccOpportunityForTree (HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        boolean archive = Boolean.parseBoolean(request.getParameter("isarchive"));
        int dl = 0;
        try {
            int start = 0;
            int limit = 10;
            String aid = request.getParameter("mapid");

            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.crmAccount.accountid");filter_values.add(aid);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.isarchive");filter_values.add(archive);
            filter_names.add("c.validflag");filter_values.add(1);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Opportunity");
            if(!heirarchyPerm){
                filter_names.add("INoo.usersByUserid.userID");filter_values.add(usersList);
            }
            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.createdOn");
            order_type.add("desc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("pagingFlag", true);
            requestParams.put("countFlag", false);
            kmsg = crmOpportunityDAOObj.getAllOpportunities(requestParams);
            ll = kmsg.getEntityList();
            dl = ll.size();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmOpportunity obj = (CrmOpportunity) ite.next();
                JSONObject tmpObj = new JSONObject();

                tmpObj.put("text", obj.getOppname());
                tmpObj.put("nodeid", obj.getOppid());
                tmpObj.put("leaf", true);
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
            result = jarr.toString();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getAccOpportunityForTree", e);
        }
        return result;
//        return new ModelAndView("jsonView", "model", result);
    }

    public String getAccContactForTree(HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        String result = "";
        List ll = null;
        KwlReturnObject kmsg = null;
        JSONArray jarr = new JSONArray();
        try {
            int start = 0;
            int limit = 10;
            String aid = request.getParameter("mapid");

            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.crmAccount.accountid");filter_values.add(aid);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.isarchive");filter_values.add(false);
            filter_names.add("c.validflag");filter_values.add(1);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
            if(!heirarchyPerm){
                filter_names.add("INco.usersByUserid.userID");filter_values.add(usersList);
            }
            
            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.createdOn");
            order_type.add("desc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("pagingFlag", true);
            requestParams.put("countFlag", false);
            kmsg = crmContactDAOObj.getAllContacts(requestParams);
            ll = kmsg.getEntityList();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("text", ((obj.getFirstname()==null)?"":obj.getFirstname()) + " " + ((obj.getLastname()==null)?"":obj.getLastname()));
                tmpObj.put("nodeid", obj.getContactid());
                tmpObj.put("leaf", true);
                jarr.put(tmpObj);
            }
            result = jarr.toString();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getAccContactForTree", e);
        }
        return result;
    }

    public String getAccCaseForTree(HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        List ll = null;
        String result= null;
        JSONArray jarr = new JSONArray();
        try {
            int start = 0;
            int limit = 10;
            String accid = request.getParameter("mapid");

            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.crmAccount.accountid");filter_values.add(accid);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.isarchive");filter_values.add(false);
            filter_names.add("c.validflag");filter_values.add(1);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Cases");
            if(!heirarchyPerm){
                filter_names.add("INc.usersByUserid.userID");filter_values.add(usersList);
            }

            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.casename");
            order_type.add("asc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("pagingFlag", true);
            requestParams.put("countFlag", false);
            KwlReturnObject accCasesObj = crmCaseDAOObj.getAllCases(requestParams);
            ll = accCasesObj.getEntityList();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmCase obj = (CrmCase) ite.next();
                String nodeText = obj.getSubject();
                if (nodeText.length() > 10) {
                    nodeText = (obj.getSubject()).substring(0, 8) + "...";
                }
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("text", nodeText);
                tmpObj.put("nodeid", obj.getCaseid());
                tmpObj.put("leaf", true);
                jarr.put(tmpObj);
            }
            result = jarr.toString();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getAccCaseForTree", e);
        }
        return result;
    }

    public String getLeadActivityForTree(HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        List ll = null;
        String result = null;
        JSONArray jarr = new JSONArray();
        int dl = 0;
        try {
            String lid = request.getParameter("mapid");

            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.crmActivityMaster.deleteflag");filter_values.add(0);
            filter_names.add("c.crmLead.leadid");filter_values.add(lid);
            filter_names.add("c.crmActivityMaster.company.companyID");filter_values.add(companyid);
            filter_names.add("c.crmActivityMaster.isarchive");filter_values.add(false);
            filter_names.add("c.crmActivityMaster.validflag");filter_values.add(1);

            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.crmActivityMaster.createdOn");
            order_type.add("asc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            requestParams.put("table_name", "CrmLeadActivity");
            requestParams.put("userlist_param", "c.crmActivityMaster.usersByUserid.userID");
            requestParams.put("userlist_value", usersList);
            KwlReturnObject leadActObj = crmActivityDAOObj.getLeadActivityForTable(requestParams,true);
            ll = leadActObj.getEntityList();
            dl = ll.size();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmLeadActivity leadObj = (CrmLeadActivity) ite.next();
                CrmActivityMaster obj = leadObj.getCrmActivityMaster();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("text", obj.getFlag() + " " + crmManagerCommon.comboNotNullRaw(obj.getCrmCombodataByTypeid()));
                tmpObj.put("nodeid", obj.getActivityid());
                tmpObj.put("leaf", true);
                jarr.put(tmpObj);
            }
            result = jarr.toString();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getAccActivityForTree", e);
        }
        return result;
    }

    public String getMyDocuments(HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        List ll = null;
        String result = null;
        JSONObject jobj = new JSONObject();
        JSONArray rootArr = new JSONArray();
        try {
            if ((sessionHandlerImplObj.getPerms(request, "Document") & 1) == 1) {
                int actCnt = 0;
                ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
                filter_names.add("c.deleteflag");filter_values.add(0);
                filter_names.add("c.company.companyID");filter_values.add(companyid);

                ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
                order_by.add("c.uploadedOn");
                order_type.add("desc");

                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_values", filter_values);
                requestParams.put("order_by", order_by);
                requestParams.put("order_type", order_type);
                requestParams.put("table_name", "Docs");
                requestParams.put("userlist_param", "c.docid.userid.userID");
                requestParams.put("userlist_value", usersList);

                KwlReturnObject kmsg = documentDAOObj.getDocumentsForTable(requestParams, true);
                ll = kmsg.getEntityList();
                actCnt = kmsg.getRecordTotalCount();
                Iterator ite = ll.iterator();
                while (ite.hasNext()) {
                    Docs doc = (Docs) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("nodeid", doc.getDocid());
                    tmpObj.put("text", doc.getDocname());
                    String tags[] = doc.getTags().split(",");

                    for (int i = 0; i < tags.length && !(tags[i].equals("")); i++) {
                        JSONObject accChild = new JSONObject();
                        accChild.put("text", tags[i]);
                        accChild.put("leaf", true);
                        accChild.put("iconCls", "pwndCRM doctreetags");
                        tmpObj.append("children", accChild);
                    }
                    if (tags[0].equals("")) {
                        tmpObj.put("leaf", true);
                        tmpObj.put("iconCls", "pwndCRM doctreeicon");
                    }
                    jobj.append("children", tmpObj);
                }

                if (actCnt != 0) {
                    jobj.put("text", " <span> My Documents</span>");
                    jobj.put("expanded", false);
                } else {
                    JSONObject accChild = new JSONObject();
                    accChild.put("text", "<span> No Documents </span>");
                    accChild.put("leaf", true);
                    jobj.put("text", "<span> My Documents</span>");
                    jobj.put("children", accChild);
                    jobj.put("expanded", true);
                }
            } else {
                jobj.put("text", "<span class='dashboardcontent'>Insufficient privilege to view Documents</span>");
                jobj.put("leaf", true);
            }
            result = rootArr.put(jobj).toString();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getDocuments", e);
        }
        return result;
    }

    public JSONObject getAccountForTree(HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        List ll = null;
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            int start = 0;
            int limit = 10;

            String expandAccount = request.getParameter("expandaccount");
            String idExpand = request.getParameter("idexpand");
            int actCnt = 0;

            boolean showActivity = false;
            boolean showOpp = false;
            boolean showContact = false;
            boolean showCase = false;

            if ((sessionHandlerImpl.getPerms(request, "Activity") & 1) == 1) {
                showActivity = true;
            }
            if ((sessionHandlerImpl.getPerms(request, "Opportunity") & 1) == 1) {
                showOpp = true;
            }
            if ((sessionHandlerImpl.getPerms(request, "Contact") & 1) == 1) {
                showContact = true;
            }
            if ((sessionHandlerImpl.getPerms(request, "Case") & 1) == 1) {
                showCase = true;
            }

            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.validflag");filter_values.add(1);
            filter_names.add("c.isarchive");filter_values.add(false);

            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.createdOn");
            order_type.add("desc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("pagingFlag", true);
            requestParams.put("countFlag", false);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Account");
            if(!heirarchyPerm) {
                filter_names.add("INao.usersByUserid.userID");filter_values.add(usersList);
            }

            kmsg = crmAccountDAOObj.getAllAccounts(requestParams, filter_names, filter_values);
            actCnt = kmsg.getRecordTotalCount();
            ll = kmsg.getEntityList();

            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmAccount aCrmAccount = (CrmAccount) ite.next();
                JSONObject tmpObj = new JSONObject();
                String accId = aCrmAccount.getAccountid();
                tmpObj.put("nodeid", aCrmAccount.getAccountid());
                tmpObj.put("id", aCrmAccount.getAccountid());
                if (!StringUtil.isNullOrEmpty(idExpand) && aCrmAccount.getAccountid().equals(idExpand)) {
                    tmpObj.put("expanded", true);
                }
                tmpObj.put("text", aCrmAccount.getAccountname());
                tmpObj.put("iconCls", "pwndCRM accounttreeicon");
                tmpObj.put("mode", "11");// for account

                JSONObject accChild = new JSONObject();
                if (showActivity) {
//                    filter_names.clear();filter_values.clear();order_by.clear();order_type.clear();
//                    filter_names.add("c.crmAccount.deleteflag");filter_values.add(0);
//                    filter_names.add("c.crmAccount.accountid");filter_values.add(accId);
//                    filter_names.add("c.crmActivityMaster.company.companyID");filter_values.add(companyid);
//                    filter_names.add("c.crmActivityMaster.deleteflag");filter_values.add(0);
//                    requestParams.clear();
//                    requestParams.put("config", true);
//                    requestParams.put("isarchive", false);
//                    KwlReturnObject accActObj = crmActivityDAOObj.getAccountActivity(requestParams, usersList, filter_names, filter_values);// getAccountActivityList(session, request, accId);
//                    List accActivityList = accActObj.getEntityList();
//                    if (accActivityList.size() == 0) {
//                        accChild.put("text", "Activities");
//                        accChild.put("leaf", true);
//                    } else {
//                       // accChild.put("text", "Activities (" + accActivityList.size() + ")");
                        accChild.put("text", "Activities");
//                    }
                    accChild.put("iconCls", "pwndCRM todolisttreepane");
                    accChild.put("mapid", accId);
                    accChild.put("mode", "1");
                    accChild.put("expanded", true);
                    accChild.put("level", "1");
                    tmpObj.append("children", accChild);
                }
                if (showOpp) {
//                    heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "opportunity");
//                    requestParams.put("heirarchyPerm", heirarchyPerm);
//                    filter_names.clear();filter_values.clear();order_by.clear();order_type.clear();
//                    filter_names.add("c.deleteflag");filter_values.add(0);
//                    filter_names.add("c.crmAccount.accountid");filter_values.add(accId);
//                    filter_names.add("c.isarchive");filter_values.add(false);
//                    filter_names.add("c.company.companyID");filter_values.add(companyid);
//                    filter_names.add("c.validflag");filter_values.add(1);
                    accChild = new JSONObject();
//                    KwlReturnObject accOppObj = crmOpportunityDAOObj.getAllOpportunities(requestParams,usersList, filter_names, filter_values);
//                    List acOppList = accOppObj.getEntityList();
//                    if (acOppList.size() == 0) {
//                        accChild.put("text", "Opportunities");
//                        accChild.put("leaf", true);
//                    } else {
//                       // accChild.put("text", "Opportunities (" + acOppList.size() + ")");
                        accChild.put("text", "Opportunities");
//                    }
                    accChild.put("iconCls", "pwndCRM opportunitytreeIcon");
                    accChild.put("mapid", accId);
                    accChild.put("mode", "2");
                    accChild.put("level", "1");
                    if (!StringUtil.isNullOrEmpty(request.getParameter("expandopp"))) {
                        accChild.put("expanded", true);
                    }
                    tmpObj.append("children", accChild);
                }
                if (showContact) {
                    accChild = new JSONObject();
//                    heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");
//                    requestParams.put("heirarchyPerm", heirarchyPerm);
//                    filter_names.clear();filter_values.clear();order_by.clear();order_type.clear();
//                    filter_names.add("c.deleteflag");filter_values.add(0);
//                    filter_names.add("c.crmAccount.accountid");filter_values.add(accId);
//                    filter_names.add("c.isarchive");filter_values.add(false);
//                    filter_names.add("c.company.companyID");filter_values.add(companyid);
//                    filter_names.add("c.validflag");filter_values.add(1);
//                    KwlReturnObject accContactObj = crmContactDAOObj.getAllContacts(requestParams, usersList, filter_names, filter_values);
//                    List acContactList = accContactObj.getEntityList();
//                    if (acContactList.size() == 0) {
//                        accChild.put("text", "Contacts");
//                        accChild.put("leaf", true);
//                    } else {
//                       // accChild.put("text", "Contacts (" + acContactList.size() + ")");
                        accChild.put("text", "Contacts");
//                    }
                    accChild.put("iconCls", "pwndCRM contactstreeIcon");
                    accChild.put("mapid", accId);
                    accChild.put("mode", "3");
                    accChild.put("level", "1");
                    if (!StringUtil.isNullOrEmpty(request.getParameter("expandcontact"))) {
                        accChild.put("expanded", true);
                    }
                    tmpObj.append("children", accChild);
                }
                if (showCase) {
                    accChild = new JSONObject();
//                    filter_names.clear();filter_values.clear();order_by.clear();order_type.clear();
//                    filter_names.add("c.deleteflag");filter_values.add(0);
//                    filter_names.add("c.company.companyID");filter_values.add(companyid);
//                    filter_names.add("c.crmAccount.accountid");filter_values.add(accId);
//                    filter_names.add("c.validflag");filter_values.add(1);
//                    filter_names.add("c.isarchive");filter_values.add(false);
//                    order_by.add("c.createdOn");
//                    order_type.add("desc");
//
//                    requestParams.clear();
//                    requestParams.put("filter_names", filter_names);
//                    requestParams.put("filter_values", filter_values);
//                    requestParams.put("order_by", order_by);
//                    requestParams.put("order_type", order_type);
//                    requestParams.put("table_name", "CrmCase");
//                    requestParams.put("userlist_param", "c.usersByUserid.userID");
//                    requestParams.put("userlist_value", usersList);
//                    KwlReturnObject accCasesObj = crmCaseDAOObj.getCasesForTable(requestParams,true);
//                    List accCaseList = accCasesObj.getEntityList();
//                    if (accCaseList.size() == 0) {
//                        accChild.put("text", "Cases");
//                        accChild.put("leaf", true);
//                    } else {
//                       // accChild.put("text", "Cases (" + accCaseList.size() + ")");
                        accChild.put("text", "Cases");
//                    }
                    accChild.put("iconCls", "pwndCRM casetreeIcon");
                    accChild.put("mapid", accId);
                    accChild.put("mode", "4");
                    accChild.put("level", "1");
                    if (!StringUtil.isNullOrEmpty(request.getParameter("expandcase"))) {
                        accChild.put("expanded", true);
                    }
                    tmpObj.append("children", accChild);
                }
                jobj.append("children", tmpObj);
                jarr.put(tmpObj);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getAccountForTree", e);
        }
        return jobj;
    }

    public JSONObject getLeadForTree(HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        List ll = null;
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String expanded = request.getParameter("expanded");
        String expandLead = request.getParameter("expandlead");
        String leadExpand = request.getParameter("idexpand");
        int actCnt = 0;
        String transfer = "0";
        try {
            int start = 0;
            int limit = 10;
            boolean showActivity = false;
            if ((sessionHandlerImpl.getPerms(request, "Activity") & 1) == 1) {
                showActivity = true;
            }

            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();

            ArrayList order_by = new ArrayList();
            ArrayList order_type = new ArrayList();


            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", request.getParameter("isarchive"));           
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("transfered", request.getParameter("transfered"));
            requestParams.put("config", 1);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Lead");
            requestParams.put("heirarchyPerm", heirarchyPerm);           
            requestParams.put("start", start);
            requestParams.put("limit", limit);

            kmsg = crmLeadDAOObj.getLeads(requestParams, usersList);
            actCnt = kmsg.getRecordTotalCount();
            ll = kmsg.getEntityList();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmLead aCrmLead = (CrmLead) ite.next();
                JSONObject tmpObj = new JSONObject();
                String leadId = aCrmLead.getLeadid();
                tmpObj.put("accid", aCrmLead.getLeadid());
                tmpObj.put("id", aCrmLead.getLeadid());
                tmpObj.put("text", aCrmLead.getLastname());
                tmpObj.put("iconCls", "pwndCRM leadtreeicon");
                tmpObj.put("mode", "5");// for Lead

                if (!StringUtil.isNullOrEmpty(leadExpand) && aCrmLead.getLeadid().equals(leadExpand)) {
                    tmpObj.put("expanded", true);
                }
                JSONObject accChild = new JSONObject();
                if (showActivity) {
//                    filter_names.clear();filter_values.clear();order_by.clear();order_type.clear();
//                    filter_names.add("c.crmActivityMaster.deleteflag");filter_values.add(0);
//                    filter_names.add("c.crmLead.leadid");filter_values.add(leadId);
//                    filter_names.add("c.crmActivityMaster.company.companyID");filter_values.add(companyid);
//                    filter_names.add("c.crmActivityMaster.deleteflag");filter_values.add(0);
//                    filter_names.add("c.crmActivityMaster.validflag");filter_values.add(1);
//                    filter_names.add("c.crmActivityMaster.isarchive");filter_values.add(false);
//                    requestParams.clear();
//                    requestParams.put("config", true);
//                    KwlReturnObject leadActObj = crmActivityDAOObj.getLeadActivity(requestParams, usersList, filter_names, filter_values);// getAccountActivityList(session, request, accId);
//                    List leadActivityList = leadActObj.getEntityList();
//                    if (leadActivityList.size() == 0) {
//                        accChild.put("text", "Activities");
//                        accChild.put("leaf", true);
//                    } else {
//                       // accChild.put("text", "Activities (" + leadActivityList.size() + ")");
                        accChild.put("text", "Activities");
//                    }
                    accChild.put("iconCls", "pwndCRM todolisttreepane");
                    accChild.put("mapid", leadId);
                    accChild.put("mode", "8");
                    accChild.put("level", "2");
                    if (!StringUtil.isNullOrEmpty(request.getParameter("expandleadact"))) {
                        accChild.put("expanded", true);
                    }
                    tmpObj.append("children", accChild);
                }

                jobj.append("children", tmpObj);
                jarr.put(tmpObj);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getLeadForTree", e);
        }
        return jobj;
    }

    public JSONObject getProductForTree(HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        List ll = null;
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        int actCnt = 0;
        KwlReturnObject kmsg = null;
        String expanded = request.getParameter("expanded");
        String expandProduct = request.getParameter("expandproduct");
        try {
            int limit = 10;
            int start = 0;

            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.validflag");filter_values.add(1);
            filter_names.add("c.isarchive");filter_values.add(false);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            if(!heirarchyPerm){
                filter_names.add("INc.usersByUserid.userID");filter_values.add(usersList);
            }
            
            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.createdOn");
            order_type.add("desc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("pagingFlag", true);
            requestParams.put("countFlag", false);
            kmsg = crmProductDAOObj.getAllProducts(requestParams);
//            actCnt = kmsg.getRecordTotalCount();
            ll = kmsg.getEntityList();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmProduct aCrmProduct = (CrmProduct) ite.next();
                JSONObject tmpObj = new JSONObject();
                String productId = aCrmProduct.getProductid();
                tmpObj.put("accid", productId);
                tmpObj.put("id", productId);
                tmpObj.put("text", aCrmProduct.getProductname());
                tmpObj.put("iconCls", "pwndCRM producttreeicon");
                tmpObj.put("mode", "6");// for account
                tmpObj.put("leaf", true);

                jobj.append("children", tmpObj);
                jarr.put(tmpObj);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getProductForTree", e);
        }
        return jobj;
    }

    public JSONObject getCampaignForTree(HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        List ll = null;
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        int actCnt = 0;
        KwlReturnObject kmsg = null;
        String expanded = request.getParameter("expanded");
        String expandCampaign = request.getParameter("expandcampaign");
        try {
            int limit = 10;
            int start = 0;

            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.validflag");filter_values.add(1);
            filter_names.add("c.isarchive");filter_values.add(false);

            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.createdOn");
            order_type.add("desc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            requestParams.put("start", start);
            requestParams.put("limit", limit);
            requestParams.put("userlist_param", "c.usersByUserid.userID");
            requestParams.put("userlist_value", usersList);
            requestParams.put("table_name", "CrmCampaign");
            kmsg = crmCampaignDAOObj.getCampaignsForTable(requestParams, false);
            actCnt = kmsg.getRecordTotalCount();
            ll = kmsg.getEntityList();

            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CrmCampaign aCrmCampaign = (CrmCampaign) ite.next();
                JSONObject tmpObj = new JSONObject();
                String campaignId = aCrmCampaign.getCampaignid();
                tmpObj.put("accid", campaignId);
                tmpObj.put("id", campaignId);
                tmpObj.put("text", aCrmCampaign.getCampaignname());
                tmpObj.put("iconCls", "pwndCRM campaigntreeicon");
                tmpObj.put("mode", "7");// for campaign
                tmpObj.put("leaf", true);

                JSONObject accChild = new JSONObject();

                tmpObj.append("children", accChild);

                jobj.append("children", tmpObj);
                jarr.put(tmpObj);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmManager.getCampaignForTree", e);
        }
        return jobj;
    }

    public JSONObject getDocumentsforTree(HttpServletRequest request, String userid, String companyid, StringBuffer usersList) throws ServiceException {
        List ll = null;
        JSONObject jobj = new JSONObject();
        int start = 0;
        int limit = 10;
        KwlReturnObject kmsg = null;
        try {
            if ((sessionHandlerImplObj.getPerms(request, "Document") & 1) == 1) {
                int actCnt = 0;

                ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
                filter_names.add("c.deleteflag");filter_values.add(0);
                filter_names.add("c.company.companyID");filter_values.add(companyid);

                ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
                order_by.add("c.uploadedOn");
                order_type.add("desc");

                String tag = request.getParameter("tag");
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("tag", StringUtil.checkForNull(tag));
                requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));
                if (!StringUtil.isNullOrEmpty(request.getParameter("tagSearch"))) {
                    requestParams.put("tagSearch", request.getParameter("tagSearch"));
                }
                requestParams.put("start", 0);
                requestParams.put("limit", 10);
                requestParams.put("companyid", companyid);

                kmsg = documentDAOObj.getDocumentList(requestParams, usersList);
                actCnt = kmsg.getRecordTotalCount();
                ll = kmsg.getEntityList();
                Iterator ite = ll.iterator();
                while (ite.hasNext()) {
                     com.krawler.common.admin.Docmap t = (com.krawler.common.admin.Docmap) ite.next();
                      Docs doc = t.getDocid();
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("nodeid", doc.getDocid());
                    tmpObj.put("text", doc.getDocname());
                    String tags[] = doc.getTags().split(",");

                    for (int i = 0; i < tags.length && !(tags[i].equals("")); i++) {
                        JSONObject accChild = new JSONObject();
                        accChild.put("text", tags[i]);
                        accChild.put("leaf", true);
                        accChild.put("iconCls", "pwndCRM doctreetags");
                        accChild.put("tag", true);
                        accChild.put("mode", "25");
                        tmpObj.append("children", accChild);
                    }
                    if (tags[0].equals("")) {
                        tmpObj.put("leaf", true);
                        tmpObj.put("iconCls", "pwndCRM doctreeicon");
                    }
                    jobj.append("children", tmpObj);
                }
              //  String treeText = "<span wtf:qtip='View all your documents and their respective tags.' wtf:qtitle='My Documents'> My Documents (" + actCnt + ") </span>";
                String treeText = "<span wtf:qtip='View all your documents and their respective tags.' wtf:qtitle='My Documents'> My Documents  </span>";
                if (actCnt != 0) {
                    if (actCnt > limit) {
                       // jobj.put("text", treeText + "(<span class='treeSpan' onclick='loadDocumentPage()'>More...</span>)");
                        jobj.put("text", treeText + "<span class='treeSpan' onclick='loadDocumentPage()'></span>");
                    } else {
                        jobj.put("text", treeText);
                    }
                    jobj.put("expanded", false);
                } else {
                    JSONObject accChild = new JSONObject();
                    accChild.put("text", " No Documents");
                    accChild.put("leaf", true);
                    jobj.put("text", "<span> My Documents</span>");
                    jobj.put("children", accChild);
                    jobj.put("expanded", true);
                }
            } else {
                jobj.put("text", "<span class='dashboardcontent'>You do not have enough privilieges to view documents</span>");
                jobj.put("leaf", true);
            }
            jobj.put("level", "5");
            jobj.put("mode", "25");
            jobj.put("id", "documentnode");
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getDocumentsforTree", e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTreeController.getDocumentsforTree", e);
        }
        return jobj;
    }
}
