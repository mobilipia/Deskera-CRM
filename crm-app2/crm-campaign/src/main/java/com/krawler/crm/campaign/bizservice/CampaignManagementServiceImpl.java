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

package com.krawler.crm.campaign.bizservice;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.StringUtil;

import com.krawler.crm.database.tables.CrmCampaign;
import java.text.DateFormat;
import java.util.Iterator;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.campaignModule.crmCampaignDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author sagar
 */
public class CampaignManagementServiceImpl implements CampaignManagementService {

    private crmCampaignDAO crmCampaignDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private fieldDataManager fieldDataManagercntrl;
    private commentDAO crmCommentDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private static final Log LOGGER = LogFactory.getLog(CampaignManagementServiceImpl.class);

    public void setcrmCampaignDAO(crmCampaignDAO crmCampaignDAOObj1) {
        this.crmCampaignDAOObj = crmCampaignDAOObj1;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }

    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getCampaigns(String companyid, String userid, String currencyid, String selectExportJson,
            String isarchive, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xtype, String xfield,
            String start, String limit, DateFormat dateFormat,String emailcampaign) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            boolean archive = Boolean.parseBoolean(isarchive);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", archive);
            if(searchJson != null && !StringUtil.isNullOrEmpty(searchJson)) {
                requestParams.put("searchJson", searchJson);
            }
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
            if (config != null) {
                requestParams.put("config", config);
            }
            requestParams.put("companyid", companyid);
            requestParams.put("export", isExport);
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(field)) {
                requestParams.put("field", field);
                requestParams.put("direction", direction);
                requestParams.put("iscustomcolumn", iscustomcolumn);
                requestParams.put("xfield", xfield);
                requestParams.put("xtype", xtype);
            }
            if (emailcampaign != null) {
                requestParams.put("emailcampaign", StringUtil.checkForNull(emailcampaign));
            }
            requestParams.put("start", StringUtil.checkForNull(start));
            requestParams.put("limit", StringUtil.checkForNull(limit));

            KwlReturnObject kmsg = crmCampaignDAOObj.getCampaigns(requestParams, usersList);
            jobj = getCampaignJson(kmsg.getEntityList(), kmsg.getRecordTotalCount(), false, dateFormat, userid, companyid, currencyid, selectExportJson);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject campaignExport(String companyid, String userid, String currencyid, String selectExportJson,
            String isarchive, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xtype, String xfield, DateFormat dateFormat) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try{
            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", isarchive);
            if(searchJson != null && !StringUtil.isNullOrEmpty(searchJson)) {
                requestParams.put("searchJson", searchJson);
            }
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
            requestParams.put("companyid", companyid);
            if (config != null) {
                requestParams.put("config", config);
            }

            requestParams.put("export", isExport);
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(field)) {
                requestParams.put("field", field);
                requestParams.put("direction", direction);
                requestParams.put("iscustomcolumn", iscustomcolumn);
                requestParams.put("xfield", xfield);
                requestParams.put("xtype", xtype);
            }

            kmsg = crmCampaignDAOObj.getCampaigns(requestParams, usersList);
            jobj = getCampaignJson(kmsg.getEntityList(), kmsg.getRecordTotalCount(), true, dateFormat, userid, companyid, currencyid, selectExportJson);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public JSONObject getCampaignJsonObject(CrmCampaign obj, JSONObject tmpObj, String companyid, String currencyid,boolean isexport, DateFormat dateFormat) {
        try {
            tmpObj.put("campaignid", obj.getCampaignid());
            tmpObj.put("objective", obj.getObjective());
            tmpObj.put("active", StringUtil.checkForNull(obj.getActive()));
            tmpObj.put("actualcost", StringUtil.checkForNull(obj.getActualcost()));
            tmpObj.put("budgetedcost", StringUtil.checkForNull(obj.getBudgetedcost()));
            tmpObj.put("campaignname", StringUtil.checkForNull(obj.getCampaignname()));
            tmpObj.put("campaignownerid", obj.getUsersByUserid().getUserID());
            tmpObj.put("owner", obj.getUsersByUserid().getFirstName() + "" + obj.getUsersByUserid().getLastName());
            tmpObj.put("enddate",obj.getEndingdate());
            tmpObj.put("startdate",obj.getStartingdate());

            tmpObj.put("expectedresponse", StringUtil.checkForNull(obj.getExpectedresponse()));
            tmpObj.put("expectedrevenue", StringUtil.checkForNull(obj.getExpectedrevenue()));
            tmpObj.put("numsent", StringUtil.checkForNull(obj.getNumsent()));
            tmpObj.put("numsent", StringUtil.checkForNull(obj.getNumsent()));
            tmpObj.put("createdon",obj.getCreatedOn()!=null?obj.getCreatedOn():"");
            tmpObj.put("creatdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
            tmpObj.put("updatedon", obj.getUpdatedOn()!=null?obj.getUpdatedOn():"");
            tmpObj.put("validflag", obj.getValidflag());
            tmpObj.put("campaignstatusid", crmManagerCommon.comboNull(obj.getCrmCombodataByCampaignstatusid()));
            tmpObj.put("status", (obj.getCrmCombodataByCampaignstatusid() != null ? obj.getCrmCombodataByCampaignstatusid().getValue() : ""));
            tmpObj.put("campaigntypeid", crmManagerCommon.comboNull(obj.getCrmCombodataByCampaigntypeid()));
            tmpObj.put("type", (obj.getCrmCombodataByCampaigntypeid() != null ? obj.getCrmCombodataByCampaigntypeid().getValue() : ""));

            tmpObj.put("typemainid", (obj.getCrmCombodataByCampaigntypeid() != null ? obj.getCrmCombodataByCampaigntypeid().getMainID() : ""));
//            tmpObj.put("commentcount", crmCommentDAOObj.getNewCommentList(sessionHandlerImpl.getUserid(request), obj.getCampaignid()));
            if (obj.getCellstyle() != null && !obj.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(obj.getCellstyle()));
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return tmpObj;
    }

    public JSONObject getCampaignJson(List ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson) throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int dl = 0;
        KwlReturnObject kmsg = null;
        Object totalcomment = 0;
        dl = ll.size();
        Iterator ite = ll.iterator();
        try {
            String jsondata = selectExportJson;
            HashMap<String, String> commentCountMap = crmCommentDAOObj.getNewCommentCount(userid);
            if (StringUtil.isNullOrEmpty(jsondata)) {
                while (ite.hasNext()) {
                    CrmCampaign obj = (CrmCampaign) ite.next();
                    HashMap<String, Object> requestParams = new HashMap<String, Object>();
                    requestParams.put("recid", obj.getCampaignid());
                    requestParams.put("companyid", companyid);
                    totalcomment = crmCommentDAOObj.getCommentsCountForRecord(requestParams);
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("totalcomment", totalcomment);
                    tmpObj = getCampaignJsonObject(obj, tmpObj, companyid, currencyid, isexport, dateFormat);
                    tmpObj.put("commentcount", (commentCountMap.containsKey(obj.getCampaignid())?commentCountMap.get(obj.getCampaignid()):"0"));
                    jarr.put(tmpObj);
                }
            } else {
                JSONArray jArr = new JSONArray("[" + jsondata + "]");
                dl = jArr.length();
                for (int i = 0; i < dl; i++) {
                    JSONObject jObj = jArr.getJSONObject(i);
                    CrmCampaign obj = (CrmCampaign) kwlCommonTablesDAOObj.getObject("com.krawler.crm.database.tables.CrmCampaign", jObj.getString("id"));
                    JSONObject tmpObj = new JSONObject();
                    tmpObj = getCampaignJsonObject(obj, tmpObj, companyid, currencyid, isexport, dateFormat);
                    tmpObj.put("commentcount", (commentCountMap.containsKey(obj.getCampaignid())?commentCountMap.get(obj.getCampaignid()):"0"));
                    jarr.put(tmpObj);
                }
            }
        jobj.put("success", true);
        jobj.put("data", jarr);
        jobj.put("totalCount", totalSize);
        } catch (Exception e) {
           LOGGER.warn(e.getMessage(),e);
        }
        return jobj;
    }

}
