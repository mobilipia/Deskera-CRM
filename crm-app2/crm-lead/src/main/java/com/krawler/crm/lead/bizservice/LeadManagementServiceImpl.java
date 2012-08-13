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

package com.krawler.crm.lead.bizservice;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.krawler.baseline.crm.bizservice.el.ExpressionManager;
import com.krawler.baseline.crm.bizservice.el.ExpressionVariables;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmLeadCustomData;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.lead.dm.LeadOwnerInfo;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.spring.comments.commentDAO;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.leadModule.crmLeadHandler;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.Map;
import java.util.Map.Entry;
import org.hibernate.ObjectNotFoundException;

/**
 *
 * @author sagar
 */
public class LeadManagementServiceImpl implements LeadManagementService {
    private crmLeadDAO crmLeadDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private fieldDataManager fieldDataManagercntrl;
    private commentDAO crmCommentDAOObj;
    private fieldManagerDAO fieldManagerDAOobj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private static final Log LOGGER = LogFactory.getLog(LeadManagementServiceImpl.class);
    private crmCommonDAO crmCommonDAO;
    private ExpressionManager expressionManager;

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    public ExpressionManager getExpressionManager() {
        return expressionManager;
    }

    public void setExpressionManager(ExpressionManager expressionManager) {
        this.expressionManager = expressionManager;
    }


     /**
     * @return the crmCommonDAO
     */
    public crmCommonDAO getCrmCommonDAO()
    {
        return crmCommonDAO;
    }

    /**
     * @param crmCommonDAO the crmCommonDAO to set
     */
    public void setCrmCommonDAO(crmCommonDAO crmCommonDAO)
    {
        this.crmCommonDAO = crmCommonDAO;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
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

    /**
     * @param
     * @param companyid
     * @param userid
     * @param currencyid
     * @param selectExportJson
     * @param editconvertedlead
     * @param isarchive
     * @param transfered
     * @param isconverted
     * @param searchJson
     * @param ss
     * @param config
     * @param isExport
     * @param heirarchyPerm
     * @param field
     * @param direction
     * @param iscustomcolumn
     * @param xtype
     * @param xfield
     * @param type
     * @param start
     * @param limit
     * @param status
     * @param dateFormat
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getLeads(String companyid, String userid, String currencyid, String selectExportJson, boolean editconvertedlead,
            String isarchive, String transfered, String isconverted, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xtype, String xfield, String type,
            String start, String limit, String status, DateFormat dateFormat, StringBuffer usersList)
            throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", isarchive);
            if(searchJson != null && !StringUtil.isNullOrEmpty(searchJson)) {
                requestParams.put("searchJson", searchJson);
            }
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
            requestParams.put("companyid", companyid);
            requestParams.put("transfered", transfered);
            requestParams.put("isconverted", isconverted);
//            requestParams.put("email", true);
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
                requestParams.put("dateasnum", true);
                if(xtype.equals(Constants.AUTONO)) {
                    crmManagerCommon.getAutoNoPrefixSuffix(fieldManagerDAOobj, companyid, xfield,Constants.Crm_lead_moduleid,requestParams);
                }
            }
            if(!StringUtil.isNullOrEmpty(type)) {
                requestParams.put("type", type);
            }
            requestParams.put("start", StringUtil.checkForNull(start));
            requestParams.put("limit", StringUtil.checkForNull(limit));
            requestParams.put("status", StringUtil.checkForNull(status));
            kmsg = crmLeadDAOObj.getLeads(requestParams, usersList);

            Map kwlcommentz = getCommentMap(kmsg.getEntityList());

            boolean exportflag = false;
            jobj = getLeadJson(kmsg.getEntityList(), kmsg.getRecordTotalCount(),exportflag, dateFormat, userid, companyid, currencyid, editconvertedlead, selectExportJson,kwlcommentz,usersList);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    /**
     *
     * @param companyid
     * @param userid
     * @param currencyid
     * @param selectExportJson
     * @param editconvertedlead
     * @param isarchive
     * @param transfered
     * @param isconverted
     * @param searchJson
     * @param ss
     * @param config
     * @param isExport
     * @param heirarchyPerm
     * @param field
     * @param direction
     * @param dateFormat
     * @return
     * @throws com.krawler.common.service.ServiceException
     * @throws com.krawler.common.session.SessionExpiredException
     */

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject LeadExport(String companyid, String userid, String currencyid, String selectExportJson, boolean editconvertedlead,
            String isarchive, String transfered, String isconverted, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, DateFormat dateFormat) throws ServiceException
    {
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
            requestParams.put("transfered", transfered);
            requestParams.put("isconverted", isconverted);
            if (config != null) {
                requestParams.put("config", config);
            }

            requestParams.put("export", isExport);
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(field)) {
                requestParams.put("field", field);
                requestParams.put("direction", direction);
            }
            // send parameter of export
            String jsondata = selectExportJson;
            List<CrmLead> ll;
            int totalSize = 0;
            if (StringUtil.isNullOrEmpty(jsondata)) {//Export/Print All records
                kmsg = crmLeadDAOObj.getLeads(requestParams, usersList);
                ll = kmsg.getEntityList();
                totalSize = kmsg.getRecordTotalCount();
            } else {
                JSONArray jArr = new JSONArray("[" + jsondata + "]");
                totalSize = jArr.length();
                List<String> idsList = new ArrayList<String>();
                for (int i = 0; i < totalSize; i++) {
                    JSONObject jObj = jArr.getJSONObject(i);
                    idsList.add(jObj.getString("id"));
                }
                ll = crmLeadDAOObj.getLeads(idsList);
            }
            Map kwlcommentz = getCommentMap(ll);
            
            boolean isexport = true;
            jobj = getLeadJsonExport(ll, totalSize, isexport, dateFormat, userid, companyid, currencyid, editconvertedlead, selectExportJson,kwlcommentz, usersList);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }
    public Map<String, CrmLead> getCommentMap(List<CrmLead> ll ) {
        Map kwlcommentz = new HashMap();
            if(ll.size()>0){
                List<CrmLead> CrmLeadList = ll;
                List<String> idsList = new ArrayList<String>();

                for(CrmLead crmLead : CrmLeadList){
                    idsList.add(crmLead.getLeadid());
                }
                kwlcommentz = crmCommentDAOObj.getCommentz(idsList);
            }
        return kwlcommentz;
    }
    public JSONObject getLeadJsonObject(Map<String, ExpressionVariables> exprVarMap, CrmLead obj, JSONObject tmpObj, String companyid, String currencyid, boolean editconvertedlead,
            HashMap<String, Integer> FieldMap, DateFormat dateFormat, HashMap<String, DefaultMasterItem> defaultMasterMap, HashMap<String, CrmLeadCustomData> leadCustomDataMap, Map<String, List<LeadOwnerInfo>> owners, Map<String, List<CrmProduct>> products,Map<String, List<String>> kwlcommentz) {
        try {

            String comment = StringUtil.getCommentForRecord(kwlcommentz, obj.getLeadid());
            
            tmpObj.put("comment", comment);
            tmpObj.put("leadid", obj.getLeadid());
            String[] ownerInfo = crmLeadHandler.getAllLeadOwners(owners.get(obj.getLeadid()));
            tmpObj.put("leadownerid",ownerInfo == null? "": ownerInfo[2] );
            tmpObj.put("leadowner",ownerInfo == null? "": ownerInfo[0] );
            tmpObj.put("owner", ownerInfo == null? "": ownerInfo[5]);
            tmpObj.put("subowners", ownerInfo == null? "": ownerInfo[1]);

//            tmpObj.put("firstname", StringUtil.checkForNull(obj.getFirstname()));
            tmpObj.put("lastname", obj.getLastname());
            tmpObj.put("firstname", obj.getFirstname());
//            tmpObj.put("companyname", obj.getCompanyname());
            tmpObj.put("type", obj.getType());
            tmpObj.put("exportType",crmLeadHandler.getLeadTypeName(obj.getType()));
            tmpObj.put("phone", obj.getPhone() != null ? obj.getPhone() : "");
            tmpObj.put("email", StringUtil.checkForNull(obj.getEmail()));
            tmpObj.put("description", StringUtil.checkForNull(obj.getDescription()));
            tmpObj.put("createdon",obj.getCreatedOn()!=null?obj.getCreatedOn():"");
            tmpObj.put("creatdon", obj.getCreatedOn()!=null?obj.getCreatedOn():"");
            tmpObj.put("updatedon", obj.getUpdatedOn()!=null?obj.getUpdatedOn():"");
            tmpObj.put("isconverted", obj.getIsconverted());
            //@@@ No need to fetch in loop . Change the logic.
//            boolean editconvertedlead =crmLeadHandler.editConvertedLead(request);
            tmpObj.put("editconvertedlead",editconvertedlead);

            tmpObj.put("title", obj.getTitle() != null ? obj.getTitle() : "");
            tmpObj.put("leadstatusid", StringUtil.hNull(obj.getLeadstatusID()));
            String status = "";
            if(obj.getLeadstatusID() != null && defaultMasterMap.containsKey(obj.getLeadstatusID())) {
                status = defaultMasterMap.get(obj.getLeadstatusID()).getValue();
            }
            tmpObj.put("status", status);
            tmpObj.put("leadstatus", status);
            tmpObj.put("ratingid", StringUtil.hNull(obj.getRatingID()));
            String rating = "";
            if(obj.getRatingID() != null && defaultMasterMap.containsKey(obj.getRatingID())) {
                rating = defaultMasterMap.get(obj.getRatingID()).getValue();
            }
            tmpObj.put("rating", rating);
            tmpObj.put("leadsourceid", StringUtil.hNull(obj.getLeadsourceID()));
            String source = "";
            if(obj.getLeadsourceID() != null && defaultMasterMap.containsKey(obj.getLeadsourceID())) {
                source = defaultMasterMap.get(obj.getLeadsourceID()).getValue();
            }
            tmpObj.put("source", source);
            tmpObj.put("leadsource", source);
            tmpObj.put("industryid", StringUtil.hNull(obj.getIndustryID()));
            String industry = "";
            if(obj.getIndustryID() != null && defaultMasterMap.containsKey(obj.getIndustryID())) {
                industry = defaultMasterMap.get(obj.getIndustryID()).getValue();
            }
            tmpObj.put("industry", industry);

            tmpObj.put("revenue", obj.getRevenue());
            tmpObj.put("price", obj.getPrice());
//            Fix Me : Add Currency Render
//            tmpObj.put("exportrevenue", crmManagerDAOObj.currencyRender(obj.getRevenue(), currencyid));
//            tmpObj.put("exportprice", crmManagerDAOObj.currencyRender(obj.getPrice(), currencyid));
            tmpObj.put("exportrevenue", !StringUtil.isNullOrEmpty(obj.getRevenue()) ? obj.getRevenue() : "");
            tmpObj.put("exportprice", !StringUtil.isNullOrEmpty(obj.getPrice()) ? obj.getPrice() : "");
            tmpObj.put("website", StringUtil.checkForNull(obj.getWebsite()));
            tmpObj.put("mobileno", StringUtil.checkForNull(obj.getMobileno()));
            tmpObj.put("faxno", StringUtil.checkForNull(obj.getFax()));
            tmpObj.put("addstreet", obj.getAddstreet() != null ? obj.getAddstreet() : "");
            tmpObj.put("city", StringUtil.checkForNull(obj.getCity()));
            tmpObj.put("state", StringUtil.checkForNull(obj.getState()));
            tmpObj.put("country", StringUtil.checkForNull(obj.getCountry()));
            tmpObj.put("zip", StringUtil.checkForNull(obj.getZip()));
            tmpObj.put("description", StringUtil.checkForNull(obj.getDescription()));
            tmpObj.put("validflag", obj.getValidflag());
            String[] productInfo = crmLeadHandler.getLeadProducts(products.get(obj.getLeadid()));
            tmpObj.put("productid", productInfo == null? "": productInfo[0]);
            tmpObj.put("productname", productInfo == null? "": productInfo[1]);
            tmpObj.put("exportmultiproduct", productInfo == null? "": productInfo[2]);

            if (obj.getCellstyle() != null && !obj.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(obj.getCellstyle()));
            }
            getCustomColumnJSON(obj,tmpObj,FieldMap,leadCustomDataMap,exprVarMap);
            getCalculatedCustomColumnJSON(obj, tmpObj, exprVarMap);
        } catch  (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return tmpObj;
    }

    public JSONObject getLeadJson(List<CrmLead> ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, boolean editconvertedlead, String selectExportJson,Map<String, List<String>> kwlcommentz, StringBuffer usersList) throws SessionExpiredException, ServiceException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        Object totalcomment = 0;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_lead_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);
            
            
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAO);
//             List list = fieldDataManagercntrl.getCustomColumnFormulae(new Object[]{companyid, "2"});
             HashMap<String, String> commentCountMap = crmCommentDAOObj.getNewCommentCount(userid);

             int fromIndex = 0;
             int maxNumbers = Constants.maxrecordsMapCount;
             int totalCount = ll.size();
             while(fromIndex < totalCount) {
                 List<CrmLead> leadSublist;
                 if(totalCount <= maxNumbers) {
                    leadSublist = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    leadSublist = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (CrmLead obj : leadSublist) {
                    idsList.add(obj.getLeadid());
                 }
                 HashMap<String, String> totalCommentCountMap = crmCommentDAOObj.getTotalCommentsCount(idsList, companyid);
                 HashMap<String, CrmLeadCustomData> leadCustomDataMap = crmLeadDAOObj.getLeadCustomDataMap(idsList, companyid);
                 Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(leadSublist,leadCustomDataMap, companyid,FieldMap,replaceFieldMap, isexport, dateFormat);

                 // get owners
                 Map<String, List<LeadOwnerInfo>> owners = crmLeadDAOObj.getLeadOwners(idsList);

                 // get products
                 Map<String, List<CrmProduct>> products = crmLeadDAOObj.getLeadProducts(idsList);

                 for (CrmLead obj : leadSublist) {
                    requestParams.clear();
                    requestParams.put("companyid", companyid);
                    requestParams.put("recid", obj.getLeadid());
                    totalcomment = 0;
                    if(totalCommentCountMap.containsKey(obj.getLeadid())) {
                        totalcomment = totalCommentCountMap.get(obj.getLeadid());
                    }
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("totalcomment", totalcomment);
                    tmpObj = getLeadJsonObject(exprVarMap, obj, tmpObj, companyid, currencyid, editconvertedlead, FieldMap, dateFormat, defaultMasterMap, leadCustomDataMap, owners, products,kwlcommentz);
                    tmpObj.put("commentcount", (commentCountMap.containsKey(obj.getLeadid())?commentCountMap.get(obj.getLeadid()):"0"));
                    jarr.put(tmpObj);
                 }
                 fromIndex += maxNumbers+1;
             }
             jobj.put("success", true);
             jobj.put("data", jarr);
             jobj.put("totalCount", totalSize);
       } catch(Exception e) {
            LOGGER.warn(e.getMessage(), e);
       }
       return jobj;
    }

    /**
     *
     * @param ll
     * @param totalSize
     * @param isexport
     * @param dateFormat
     * @param userid
     * @param companyid
     * @param currencyid
     * @param editconvertedlead
     * @param selectExportJson
     * @return
     * @throws com.krawler.common.session.SessionExpiredException
     * @throws com.krawler.common.service.ServiceException
     */
    public JSONObject getLeadJsonExport(List<CrmLead> ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, boolean editconvertedlead, String selectExportJson,Map<String, List<String>> kwlcommentz, StringBuffer usersList) throws SessionExpiredException, ServiceException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_lead_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);
//            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap(requestParams);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmLeadHandler.getLeadsReleatedDefaultMasterData(companyid, usersList, kwlCommonTablesDAOObj, crmCommonDAO);
//            List list = fieldDataManagercntrl.getCustomColumnFormulae(new Object[]{companyid, "2"});



//             String jsondata = selectExportJson;
//             if (StringUtil.isNullOrEmpty(jsondata)) {//Export/Print All records
                 int fromIndex = 0;
                 int maxNumbers = Constants.maxrecordsMapCount;
                 int totalCount = ll.size();
                 while(fromIndex < totalCount) {
                     List<CrmLead> leadSublist;
                     if(totalCount <= maxNumbers) {
                        leadSublist = ll;
                     } else {
                        int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                        leadSublist = ll.subList(fromIndex, toIndex);
                     }

                     List<String> idsList = new ArrayList<String>();
                     for (CrmLead obj : leadSublist) {
                          idsList.add(obj.getLeadid());
                     }
                     HashMap<String, CrmLeadCustomData> leadCustomDataMap = crmLeadDAOObj.getLeadCustomDataMap(idsList, companyid);
                     Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(leadSublist,leadCustomDataMap,companyid,FieldMap,replaceFieldMap, isexport , dateFormat);

                     // get owners
                     Map<String, List<LeadOwnerInfo>> owners = crmLeadDAOObj.getLeadOwners(idsList);

                     // get products
                     Map<String, List<CrmProduct>> products = crmLeadDAOObj.getLeadProducts(idsList);

                     for (CrmLead obj : leadSublist) {
                        requestParams.clear();
                        requestParams.put("companyid", companyid);
                        requestParams.put("recid", obj.getLeadid());
                        JSONObject tmpObj = new JSONObject();
                        tmpObj = getLeadJsonObject(exprVarMap, obj, tmpObj, companyid, currencyid, editconvertedlead, FieldMap, dateFormat, defaultMasterMap, leadCustomDataMap, owners, products,kwlcommentz);
                        jarr.put(tmpObj);
                    }
                    fromIndex += maxNumbers+1;
                 }
//             } else {//Selected export or print option.
//                JSONArray jArr = new JSONArray("[" + jsondata + "]");
//                totalSize = jArr.length();
//                List<String> idsList = new ArrayList<String>();
//                for (int i = 0; i < totalSize; i++) {
//                    JSONObject jObj = jArr.getJSONObject(i);
//                    idsList.add(jObj.getString("id"));
//                }
//                HashMap<String, CrmLeadCustomData> leadCustomDataMap = crmLeadDAOObj.getLeadCustomDataMap(idsList, companyid);
//                Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(ll,leadCustomDataMap,companyid,FieldMap,replaceFieldMap, isexport, dateFormat);
//                for (int i = 0; i < totalSize; i++) {
//                    JSONObject jObj = jArr.getJSONObject(i);
//                    CrmLead obj = (CrmLead) kwlCommonTablesDAOObj.getObject("com.krawler.crm.database.tables.CrmLead", jObj.getString("id"));
//
//                    // get owners
//                    Map<String, List<LeadOwnerInfo>> owners = crmLeadDAOObj.getLeadOwners(idsList);
//
//                    // get products
//                    Map<String, List<CrmProduct>> products = crmLeadDAOObj.getLeadProducts(idsList);
//
//                    JSONObject tmpObj = new JSONObject();
//                    tmpObj = getLeadJsonObject(exprVarMap, obj, tmpObj, companyid, currencyid, editconvertedlead, FieldMap,isexport, dateFormat, defaultMasterMap, leadCustomDataMap, owners, products);
//                    jarr.put(tmpObj);
//                }
//             }

             jobj.put("success", true);
             jobj.put("data", jarr);
             jobj.put("totalCount", totalSize);
       } catch(Exception e) {
            LOGGER.warn(e.getMessage(), e);
       }
       return jobj;
    }
    
    public Map<String, ExpressionVariables> expressionVariableMap(List<CrmLead> ll, HashMap<String, CrmLeadCustomData> leadCustomDataMap, String companyid, HashMap<String, Integer> FieldMap, HashMap<String, String> replaceFieldMap, boolean isExport,DateFormat dateFormat) {
        Map<String, ExpressionVariables> exprVarMap = new HashMap<String, ExpressionVariables>();
        try {
            List<Object[]> formulae = fieldManagerDAOobj.getModuleCustomFormulae(Constants.Crm_lead_moduleid, companyid);
            if (ll != null && !ll.isEmpty()) {
                for (CrmLead obj : ll) {
                    ExpressionVariables var = new ExpressionVariables();
                    Map<String, Object> variableMapForFormula = new HashMap<String, Object>();
                    Map<String, Object> variableMap = new HashMap<String, Object>();
                    variableMap.put(Constants.Crm_lead_modulename, obj);
                    variableMapForFormula.put(Constants.Crm_lead_modulename, obj);
                    setCustomColumnValues(obj, FieldMap, replaceFieldMap, variableMap, variableMapForFormula, leadCustomDataMap, isExport, dateFormat);
                    var.setVariablesMap(variableMap);
                    var.setVariablesMapForFormulae(variableMapForFormula);
                    exprVarMap.put(obj.getLeadid(), var);
                }
            }

            if (formulae != null) {
                for (Object[] formula : formulae) {
                    getExpressionManager().evaluateExpression((String) formula[0], (String) formula[1], exprVarMap);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return exprVarMap;
        }
    }
    
    public void getCustomColumnJSON(CrmLead obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap, HashMap<String, CrmLeadCustomData> leadCustomDataMap, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        // create json object of custom columns added in lead grid
        if (exprVarMap.containsKey(obj.getLeadid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getLeadid());

            for (Entry<String, Object> varEntry : expVar.getVariablesMap().entrySet()) {
                if (!Constants.Crm_lead_modulename.equals(varEntry.getKey())) {
                    String coldata = varEntry.getValue().toString();
                    if (!StringUtil.isNullOrEmpty(coldata)) {
                        tmpObj.put(varEntry.getKey(), coldata);
                    }
                }
            }
        } else {
            if(leadCustomDataMap.containsKey(obj.getLeadid())) {
                CrmLeadCustomData crmLeadCustomDataobj = leadCustomDataMap.get(obj.getLeadid());
                for (Entry<String, Integer> field : FieldMap.entrySet()) {
                    if (field.getValue() > 0) {
                        String coldata = crmLeadCustomDataobj.getCol(field.getValue());
                        if (!StringUtil.isNullOrEmpty(coldata)) {
                            tmpObj.put(field.getKey(), coldata);
                        }
                    }
                }
            }
        }
    }

    protected void getCalculatedCustomColumnJSON(CrmLead obj, JSONObject tmpObj, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        if (exprVarMap.containsKey(obj.getLeadid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getLeadid());
            for (Entry<String, Object> varEntry : expVar.getOutputMap().entrySet()) {
                if (!Constants.Crm_lead_modulename.equals(varEntry.getKey())) {
                    tmpObj.put(varEntry.getKey(), varEntry.getValue());
                }
            }
        }
    }

    protected void setCustomColumnValues(CrmLead lead, HashMap<String, Integer> fieldMap, Map<String, String> replaceFieldMap, Map<String, Object> variableMap, Map<String, Object> variableMapForFormula, HashMap<String, CrmLeadCustomData> leadCustomDataMap, boolean isExport, DateFormat dateFormat) {
        if (leadCustomDataMap.containsKey(lead.getLeadid())) {
            CrmLeadCustomData crmLeadCustomData = leadCustomDataMap.get(lead.getLeadid());
            for (Entry<String, Integer> field : fieldMap.entrySet()) {
                Integer colnumber = field.getValue();
                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
                    Integer isref = fieldMap.get(field.getKey()+"#" + colnumber);
                    String coldata = null;
                    if (isref != null) {
                        try {
                            coldata = crmLeadCustomData.getCol(colnumber);
                            if (!StringUtil.isNullOrEmpty(coldata)) {
                                if (coldata.length() > 1 && isExport) {
                                    if (isref == 1) {
                                        coldata = fieldDataManagercntrl.getMultiSelectColData(coldata);
                                    } else if (isref == 0) {
                                        coldata = crmLeadCustomData.getRefCol(colnumber);
                                    } 
                                }
                                variableMap.put(field.getKey(), coldata);
                                try {
                                    variableMapForFormula.put(replaceFieldMap.get(field.getKey()), Double.parseDouble(coldata));
                                } catch (Exception ex) {
                                    variableMapForFormula.put(replaceFieldMap.get(field.getKey()), 0);
                                }
                            }
                        } catch (IllegalArgumentException ex) {
                            ex.printStackTrace();
                        } catch (ObjectNotFoundException ex) {
                            ex.printStackTrace();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
   public JSONObject saveLead(String id, String userid, String companyid, String timezone, String defaultLeadType, Integer operationCode,JSONObject jobj) throws ServiceException {
        JSONObject leadjson = new JSONObject();
        String successMSG = "";
        CrmLead lead = null;
        KwlReturnObject kmsg = null;
        HashMap<String,Object> requestParams = new HashMap<String,Object>();
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        try {
            
               jobj.put("userid", userid);
               jobj.put("companyid", companyid);
               jobj.put("isconverted", "0");
               jobj.put("istransfered", "0");
               jobj.put("updatedon", new Date().getTime());
               jobj.put("tzdiff",timezone);
               if (jobj.has("companyname")) {
                   jobj.put("company", jobj.getString("companyname"));
               }
               if (jobj.has("addstreet")) {
                   jobj.put("street", jobj.getString("addstreet"));
               }
               JSONArray jcustomarray = null;
               if (jobj.has("customfield")) {
                   jcustomarray = jobj.getJSONArray("customfield");
               }

               if (jobj.has("type") && StringUtil.isNullOrEmpty(jobj.getString("type"))) {
                   jobj.put("type", defaultLeadType);
               }
               if (id.equals("0")) {
                   id = java.util.UUID.randomUUID().toString();
                   jobj.put("leadid", id);
                   // handled case while creating lead from dashboard
                   if(!jobj.has("type")) {
                        jobj.put("type", defaultLeadType);
                   }

                   kmsg = crmLeadDAOObj.addLeads(jobj);
                   lead = (CrmLead) kmsg.getEntityList().get(0);
                   HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                   customrequestParams.put("customarray", jcustomarray);
                   customrequestParams.put("modulename", Constants.Crm_Lead_modulename);
                   customrequestParams.put("moduleprimarykey", Constants.Crm_Leadid);
                   customrequestParams.put("modulerecid", lead.getLeadid());
                   customrequestParams.put("companyid", companyid);
                   customrequestParams.put("customdataclasspath", Constants.Crm_lead_custom_data_classpath);
                   if (jobj.has("customfield")) {
                       KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                       if (customDataresult!=null && customDataresult.getEntityList().size() > 0) {
                           jobj.put("CrmLeadCustomDataobj", lead.getLeadid());
                           kmsg = crmLeadDAOObj.editLeads(jobj);
                       }
                   }
                // fetch auto-number columns only
                   HashMap<String, Object> fieldrequestParams = new HashMap<String, Object>();
                   fieldrequestParams.put("isexport", true);
                   fieldrequestParams.put("filter_names", Arrays.asList("companyid", "moduleid", "fieldtype"));
                   fieldrequestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_lead_moduleid, Constants.CUSTOM_FIELD_AUTONUMBER));
                   KwlReturnObject AutoNoFieldMap = fieldManagerDAOobj.getFieldParams(fieldrequestParams);

                   // increment auto number if exist
                   if(AutoNoFieldMap.getEntityList().size()>0) {
                       JSONArray autoNoData = fieldDataManagercntrl.setAutoNumberCustomData(customrequestParams, AutoNoFieldMap.getEntityList());
                       jobj.put(com.krawler.common.util.Constants.AUTOCUSTOMFIELD, autoNoData);
                   }
                    
               } else {
                   // Check lead status - Qualified status can't be changed or Lead status cannot be changed to Qualified unless it is converted into Opportunity or Account
                   operationCode = CrmPublisherHandler.UPDATERECORDCODE;
                   if (jobj.has("leadstatusid")) {
                       String statusId = "", oldStatusId = "", name = "";
                       requestParams.clear();
                       filter_names.clear();
                       filter_params.clear();

                       filter_names.add("c.deleteflag");
                       filter_params.add(0);

                       filter_names.add("c.isarchive");
                       filter_params.add(false);

                       filter_names.add("c.id");
                       filter_params.add(id);
                       requestParams.put("filter_names", filter_names);
                       requestParams.put("filter_params", filter_params);
                       KwlReturnObject leadReObj = crmLeadDAOObj.getLeads(requestParams);
                       Iterator ite = leadReObj.getEntityList().iterator();
                       if (ite.hasNext()) {
                           CrmLead crmlead = (CrmLead) ite.next();
                           if (crmlead.getCrmCombodataByLeadstatusid() != null) {
                               if (!StringUtil.isNullOrEmpty(crmlead.getCrmCombodataByLeadstatusid().toString())) {
                                   oldStatusId = crmlead.getCrmCombodataByLeadstatusid().getID();
                               }
                           }
                       }
                       if (jobj.has("leadstatusid") && !jobj.getString("leadstatusid").equals(oldStatusId)) {
                           String comboName = "Lead Status";
                           filter_names = new ArrayList();
                           filter_params = new ArrayList();
                           ArrayList order_by = new ArrayList();
                           ArrayList order_type = new ArrayList();
                           filter_names.add("d.company.companyID");
                           filter_params.add(companyid);
                           filter_names.add("d.mainID");
                           filter_params.add(Constants.LEADSTATUSID_QUALIFIED);
                           if (comboName.equalsIgnoreCase("Lead Source")) {
                               filter_names.add("d.validflag");
                               filter_params.add(1);
                               order_by.add("d.crmCombomaster.comboname");
                               order_type.add("desc");
                           }
                           order_by.add("d.value");
                           order_type.add("asc");
                           HashMap<String, Object> comboRequestParams = new HashMap<String, Object>();
                           comboRequestParams.put("filter_names", filter_names);
                           comboRequestParams.put("filter_params", filter_params);
                           comboRequestParams.put("order_by", order_by);
                           comboRequestParams.put("order_type", order_type);
                           List ll = crmManagerDAOObj.getComboData(comboName, comboRequestParams);
                           ite = ll.iterator();
                           while (ite.hasNext()) {
                               DefaultMasterItem crmCombodata = (DefaultMasterItem) ite.next();
                               statusId = crmCombodata.getID();
                               name = crmCombodata.getValue();
                           }

                           if (oldStatusId.equals(statusId)) {
                               successMSG = "Lead Status of " + name + " Lead cannot be updated.";
                               jobj.put("leadstatusid", statusId);
                           } else if (jobj.has("leadstatusid") && jobj.getString("leadstatusid").equals(statusId)) {
                               successMSG = "Lead status cannot be changed to Qualified, unless it is converted into Opportunity or Account.";
                               jobj.put("leadstatusid", oldStatusId);
                           }
                       }
                   }
                   jobj.put("leadid", id);
                   if (jobj.has("createdon")) {
                       jobj.put("createdon", jobj.getLong("createdon"));
                   }
                   if (jobj.has("customfield")) {
                       HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                       customrequestParams.put("customarray", jcustomarray);
                       customrequestParams.put("modulename", Constants.Crm_Lead_modulename);
                       customrequestParams.put("moduleprimarykey", Constants.Crm_Leadid);
                       customrequestParams.put("modulerecid", id);
                       customrequestParams.put("companyid", companyid);
                       customrequestParams.put("customdataclasspath", Constants.Crm_lead_custom_data_classpath);
                       KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                       if (customDataresult!=null && customDataresult.getEntityList().size() > 0) {
                           jobj.put("CrmLeadCustomDataobj", id);
                       }
                   }
                   kmsg = crmLeadDAOObj.editLeads(jobj);
                   lead = (CrmLead) kmsg.getEntityList().get(0);
               }

               leadjson.put("msg", successMSG);
               leadjson.put("lead", lead);
               leadjson.put("jobj", jobj);
               leadjson.put("operationCode", operationCode);

       } catch(Exception e) {
            LOGGER.warn(e.getMessage(), e);
       }
       return leadjson;
    }
 }
