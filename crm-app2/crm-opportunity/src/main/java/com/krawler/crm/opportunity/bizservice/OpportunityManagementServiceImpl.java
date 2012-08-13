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

package com.krawler.crm.opportunity.bizservice;

import com.krawler.baseline.crm.bizservice.el.ExpressionManager;
import com.krawler.baseline.crm.bizservice.el.ExpressionVariables;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.crm.utils.Constants;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.StringUtil;

import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmOpportunityCustomData;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import java.text.DateFormat;
import java.util.Iterator;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.opportunity.dm.OpportunityOwnerInfo;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityHandler;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.hibernate.ObjectNotFoundException;


/**
 *
 * @author sagar
 */
public class OpportunityManagementServiceImpl  implements OpportunityManagementService {
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private fieldDataManager fieldDataManagercntrl;
    private fieldManagerDAO fieldManagerDAOobj;
    private ExpressionManager expressionManager;
    private commentDAO crmCommentDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private static final Log LOGGER = LogFactory.getLog(OpportunityManagementService.class);
    private crmCommonDAO crmCommonDAO;

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

    public crmOpportunityDAO getcrmOpportunityDAO(){
        return crmOpportunityDAOObj;
    }

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
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
    public JSONObject getOpportunities(String companyid, String userid, String currencyid, String selectExportJson, String accountFlag,
            String isarchive, String mapid, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xtype, String xfield,
            String start, String limit, DateFormat dateFormat, StringBuffer usersList) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            boolean archive = Boolean.parseBoolean(isarchive);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if (Integer.parseInt(accountFlag) == 62) {
                filter_names.add("c.crmAccount.accountid");
                filter_params.add(mapid);
            }
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.isarchive");
            filter_params.add(archive);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", isarchive);
            if(searchJson != null && !StringUtil.isNullOrEmpty(searchJson)) {
                requestParams.put("searchJson", searchJson);
            }
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
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
                if(xtype.equals(Constants.AUTONO)) {
                    crmManagerCommon.getAutoNoPrefixSuffix(fieldManagerDAOobj, companyid, xfield,Constants.Crm_opportunity_moduleid,requestParams);
            }
            }
            requestParams.put("start", StringUtil.checkForNull(start));
            requestParams.put("limit", StringUtil.checkForNull(limit));

            kmsg = getcrmOpportunityDAO().getOpportunities(requestParams, usersList, filter_names, filter_params);
            jobj = getOpportunityJson(kmsg.getEntityList(), kmsg.getRecordTotalCount(),false, dateFormat, userid, companyid, currencyid, selectExportJson, usersList);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject opportunityExport(String companyid, String userid, String currencyid, String selectExportJson, String accountFlag,
            String isarchive, String mapid, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, DateFormat dateFormat, StringBuffer usersList) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try{
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if (Integer.parseInt(accountFlag) == 62) {
                filter_names.add("c.crmAccount.accountid");
                filter_params.add(mapid);
            }
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.isarchive");
            filter_params.add(false);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isarchive", isarchive);
            if(searchJson != null && !StringUtil.isNullOrEmpty(searchJson)) {
                requestParams.put("searchJson", searchJson);
            }
            if(ss != null && !StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
            if (config != null) {
                requestParams.put("config", config);
            }

            requestParams.put("export", isExport);
            requestParams.put("heirarchyPerm", heirarchyPerm);
            if(!StringUtil.isNullOrEmpty(field)) {
                requestParams.put("field", field);
                requestParams.put("direction", direction);
            }

            String jsondata = selectExportJson;
            List<CrmOpportunity> ll;
            int totalSize = 0;
            if (StringUtil.isNullOrEmpty(jsondata)) {//Export/Print All records
                kmsg = crmOpportunityDAOObj.getOpportunities(requestParams, usersList, filter_names, filter_params);
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
                ll = crmOpportunityDAOObj.getOpportunities(idsList);
            }

            jobj = getOpportunityJsonExport(ll, totalSize, true, dateFormat, userid, companyid, currencyid, selectExportJson, usersList);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public JSONObject getOpportunityJsonObject(Map<String, ExpressionVariables> exprVarMap, CrmOpportunity obj, JSONObject tmpObj, String companyid, String currencyid, HashMap<String, Integer> FieldMap, DateFormat dateFormat,
            HashMap<String, DefaultMasterItem> defaultMasterMap, HashMap<String, CrmOpportunityCustomData> oppCustomDataMap, Map<String, List<OpportunityOwnerInfo>> owners, Map<String, List<CrmProduct>> products) {
        try {
            tmpObj.put("oppid", obj.getOppid());
            String[] ownerInfo=crmOpportunityHandler.getAllOppOwners(owners.get(obj.getOppid()));
            tmpObj.put("oppownerid",ownerInfo[2]);
            tmpObj.put("oppowner",ownerInfo == null? "": ownerInfo[0] );
            tmpObj.put("oppowner", ownerInfo[5]);
            tmpObj.put("oppname", StringUtil.checkForNull(obj.getOppname()));
            tmpObj.put("closingdate", obj.getClosingdate()!=null?obj.getClosingdate():"");
            tmpObj.put("closingdat", obj.getClosingdate()!=null?obj.getClosingdate():"");
            tmpObj.put("probability", StringUtil.checkForNull(obj.getProbability()));
            tmpObj.put("revenue", StringUtil.checkForNull(obj.getRevenue()));
            tmpObj.put("recurrevenue", StringUtil.checkForNull(obj.getRecurrevenue()));
            tmpObj.put("price", StringUtil.checkForNull(obj.getPrice()));

            //Fix Me - Add Currency Render
            tmpObj.put("exportprice", obj.getPrice() != null && !obj.getPrice().equals("") ? obj.getPrice() : "");
            tmpObj.put("keyname", StringUtil.checkForNull(obj.getKeyname()));
            tmpObj.put("keytitleid", StringUtil.checkForNull(obj.getKeytitleid()));
            tmpObj.put("keyaddstreet", StringUtil.checkForNull(obj.getKeyaddstreet()));
            tmpObj.put("keyaddcity", StringUtil.checkForNull(obj.getKeyaddcity()));
            tmpObj.put("keyaddstate", StringUtil.checkForNull(obj.getKeyaddstate()));
            tmpObj.put("keyaddcountry", StringUtil.checkForNull(obj.getKeyaddcountry()));
            tmpObj.put("keyaddzip", StringUtil.checkForNull(obj.getKeyaddzip()));
            tmpObj.put("description", StringUtil.checkForNull(obj.getDescription()));
            tmpObj.put("competitorname", StringUtil.checkForNull(obj.getCompetitorname()));
            tmpObj.put("createdon",  obj.getCreatedOn() == null ? "" :obj.getCreatedOn());
            tmpObj.put("creatdon",  obj.getCreatedOn() == null ? "" : obj.getCreatedOn());
            tmpObj.put("updatedon", obj.getUpdatedOn() == null ? "" : obj.getUpdatedOn());
            tmpObj.put("salesamount", StringUtil.checkForNull(obj.getSalesamount()));
            //            Fix Me - Add Currency Render
            tmpObj.put("exportsalesamount", obj.getSalesamount() != null && !obj.getSalesamount().equals("") ? obj.getSalesamount() : "");
            tmpObj.put("accountid", crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid"));
            JSONObject tOb = crmManagerCommon.insertNone();
            String accntname = tOb.getString("name");
            tmpObj.put("accountname", (obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : accntname));
            tmpObj.put("oppstageid", StringUtil.hNull(obj.getOppstageID()));
            String stage = "";
            if(obj.getOppstageID() != null && defaultMasterMap.containsKey(obj.getOppstageID())) {
                stage = defaultMasterMap.get(obj.getOppstageID()).getValue();
            }
            tmpObj.put("stage", stage);
            tmpObj.put("oppstage", stage);
            tmpObj.put("opptypeid", StringUtil.hNull(obj.getOpptypeID()));
            String type = "";
            if(obj.getOpptypeID() != null && defaultMasterMap.containsKey(obj.getOpptypeID())) {
                type = defaultMasterMap.get(obj.getOpptypeID()).getValue();
            }
            tmpObj.put("type", type);
            tmpObj.put("opptype", type);
            tmpObj.put("oppregionid", StringUtil.hNull(obj.getRegionID()));
            String region = "";
            if(obj.getRegionID() != null && defaultMasterMap.containsKey(obj.getRegionID())) {
                region = defaultMasterMap.get(obj.getRegionID()).getValue();
            }
            tmpObj.put("region", region);
            tmpObj.put("oppregion", region);
            tmpObj.put("leadsourceid", StringUtil.hNull(obj.getLeadsourceID()));
            String leadsource = "";
            if(obj.getLeadsourceID() != null && defaultMasterMap.containsKey(obj.getLeadsourceID())) {
                leadsource = defaultMasterMap.get(obj.getLeadsourceID()).getValue();
            }
            tmpObj.put("leadsource", leadsource);
            if(products.get(obj.getOppid()) != null) {
                String[] productInfo=crmOpportunityHandler.getOpportunityProducts(products.get(obj.getOppid()));
                tmpObj.put("productserviceid", productInfo[0]);
                tmpObj.put("product", productInfo[1]);
                tmpObj.put("exportmultiproduct", productInfo[2]);
            } else {
                tmpObj.put("productserviceid", "");
                tmpObj.put("product", "");
                tmpObj.put("exportmultiproduct", "");
            }
            tmpObj.put("accountnameid", crmManagerCommon.moduleObjNull(obj.getCrmAccount(), "Accountid"));

            tmpObj.put("validflag", obj.getValidflag());
            if (obj.getCellstyle() != null && !obj.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(obj.getCellstyle()));
            }
            getCustomColumnJSON(obj, tmpObj, FieldMap, oppCustomDataMap, exprVarMap);
            getCalculatedCustomColumnJSON(obj, tmpObj, exprVarMap);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return tmpObj;
    }

    public JSONObject getOpportunityJson(List ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson, StringBuffer usersList) throws SessionExpiredException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Object totalcomment = 0;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_opportunity_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmOpportunityHandler.getOpportunityDefaultMasterItemsMap(companyid, crmCommonDAO, kwlCommonTablesDAOObj, usersList);
            HashMap<String, String> commentCountMap = crmCommentDAOObj.getNewCommentCount(userid);

             int fromIndex = 0;
             int maxNumbers = Constants.maxrecordsMapCount;
             int totalCount = ll.size();
             while(fromIndex < totalCount) {
                 List<CrmOpportunity> subList;
                 if(totalCount <= maxNumbers) {
                    subList = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    subList = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (CrmOpportunity obj : subList) {
                    idsList.add(obj.getOppid());
                 }
                 HashMap<String, String> totalCommentCountMap = crmCommentDAOObj.getTotalCommentsCount(idsList, companyid);
                 HashMap<String, CrmOpportunityCustomData> oppCustomDataMap = crmOpportunityDAOObj.getOpportunityCustomDataMap(idsList, companyid);
                 Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(subList, oppCustomDataMap, companyid, FieldMap, replaceFieldMap, isexport, dateFormat);

                 // get owners
                 Map<String, List<OpportunityOwnerInfo>> owners = crmOpportunityDAOObj.getOpportunityOwners(idsList);

                 // get products
                 Map<String, List<CrmProduct>> products = crmOpportunityDAOObj.getOpportunityProducts(idsList);
                 for (CrmOpportunity obj : subList) {
                    requestParams.clear();
                    requestParams.put("recid", obj.getOppid());
                    requestParams.put("companyid", companyid);
                    JSONObject tmpObj = new JSONObject();
                    totalcomment = 0;
                    if(totalCommentCountMap.containsKey(obj.getOppid())) {
                        totalcomment = totalCommentCountMap.get(obj.getOppid());
                    }
                    tmpObj.put("totalcomment", totalcomment);
                    tmpObj = getOpportunityJsonObject(exprVarMap, obj, tmpObj, companyid, currencyid, FieldMap, dateFormat, defaultMasterMap, oppCustomDataMap, owners, products);
                    tmpObj.put("commentcount", (commentCountMap.containsKey(obj.getOppid())?commentCountMap.get(obj.getOppid()):"0"));
                    jarr.put(tmpObj);
                 }
                 fromIndex += maxNumbers+1;
             }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public JSONObject getOpportunityJsonExport(List ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson, StringBuffer usersList) throws SessionExpiredException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_opportunity_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmOpportunityHandler.getOpportunityDefaultMasterItemsMap(companyid, crmCommonDAO, kwlCommonTablesDAOObj, usersList);

             int fromIndex = 0;
             int maxNumbers = Constants.maxrecordsMapCount;
             int totalCount = ll.size();
             while(fromIndex < totalCount) {
                 List<CrmOpportunity> subList;
                 if(totalCount <= maxNumbers) {
                    subList = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    subList = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (CrmOpportunity obj : subList) {
                      idsList.add(obj.getOppid());
                 }
                 HashMap<String, CrmOpportunityCustomData> oppCustomDataMap = crmOpportunityDAOObj.getOpportunityCustomDataMap(idsList, companyid);
                 Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(subList, oppCustomDataMap, companyid, FieldMap, replaceFieldMap, isexport, dateFormat);

                 // get owners
                 Map<String, List<OpportunityOwnerInfo>> owners = crmOpportunityDAOObj.getOpportunityOwners(idsList);

                 // get products
                 Map<String, List<CrmProduct>> products = crmOpportunityDAOObj.getOpportunityProducts(idsList);
                 for (CrmOpportunity obj : subList) {
                    requestParams.clear();
                    requestParams.put("recid", obj.getOppid());
                    requestParams.put("companyid", companyid);
                    JSONObject tmpObj = new JSONObject();
                    tmpObj = getOpportunityJsonObject(exprVarMap, obj, tmpObj, companyid, currencyid, FieldMap, dateFormat, defaultMasterMap, oppCustomDataMap, owners, products);
                    jarr.put(tmpObj);
                }
                fromIndex += maxNumbers+1;
             }

            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    @Override
    public Map<String, ExpressionVariables> expressionVariableMap(List<CrmOpportunity> ll, HashMap<String, CrmOpportunityCustomData> customDataMap, String companyid,
            HashMap<String, Integer> FieldMap, HashMap<String, String> replaceFieldMap, boolean isExport,DateFormat dateFormat) {
        Map<String, ExpressionVariables> exprVarMap = new HashMap<String, ExpressionVariables>();
        try {
            List<Object[]> formulae = fieldManagerDAOobj.getModuleCustomFormulae(Constants.Crm_opportunity_moduleid, companyid);
            if (ll != null && !ll.isEmpty()) {
                for (CrmOpportunity obj : ll) {
                    ExpressionVariables var = new ExpressionVariables();
                    Map<String, Object> variableMapForFormula = new HashMap<String, Object>();
                    Map<String, Object> variableMap = new HashMap<String, Object>();
                    variableMap.put(Constants.Crm_opportunity_modulename, obj);
                    variableMapForFormula.put(Constants.Crm_opportunity_modulename, obj);
                    setCustomColumnValues(obj, FieldMap, replaceFieldMap, variableMap, variableMapForFormula, customDataMap, isExport, dateFormat);
                    var.setVariablesMap(variableMap);
                    var.setVariablesMapForFormulae(variableMapForFormula);
                    exprVarMap.put(obj.getOppid(), var);
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

    @Override
    public void getCustomColumnJSON(CrmOpportunity obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap,
            HashMap<String, CrmOpportunityCustomData> customDataMap, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        // create json object of custom columns added in lead grid
        if (exprVarMap.containsKey(obj.getOppid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getOppid());

            for (Entry<String, Object> varEntry : expVar.getVariablesMap().entrySet()) {
                if (!Constants.Crm_opportunity_modulename.equals(varEntry.getKey())) {
                    String coldata = varEntry.getValue().toString();
                    if (!StringUtil.isNullOrEmpty(coldata)) {
                        tmpObj.put(varEntry.getKey(), coldata);
                    }
                }
            }
        } else {
            if(customDataMap.containsKey(obj.getOppid())) {
                CrmOpportunityCustomData customDataobj = customDataMap.get(obj.getOppid());
                for (Entry<String, Integer> field : FieldMap.entrySet()) {
                    if (field.getValue() > 0) {
                        String coldata = customDataobj.getCol(field.getValue());
                        if (!StringUtil.isNullOrEmpty(coldata)) {
                            tmpObj.put(field.getKey(), coldata);
                        }
                    }
                }
            }
        }
    }

    protected void getCalculatedCustomColumnJSON(CrmOpportunity obj, JSONObject tmpObj, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        if (exprVarMap.containsKey(obj.getOppid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getOppid());
            for (Entry<String, Object> varEntry : expVar.getOutputMap().entrySet()) {
                if (!Constants.Crm_opportunity_modulename.equals(varEntry.getKey())) {
                    tmpObj.put(varEntry.getKey(), varEntry.getValue());
                }
            }
        }
    }

    protected void setCustomColumnValues(CrmOpportunity obj, HashMap<String, Integer> fieldMap, Map<String, String> replaceFieldMap,
            Map<String, Object> variableMap, Map<String, Object> variableMapForFormula, HashMap<String, CrmOpportunityCustomData> customDataMap, boolean isExport, DateFormat dateFormat) {
        if (customDataMap.containsKey(obj.getOppid())) {
            CrmOpportunityCustomData customData = customDataMap.get(obj.getOppid());
            for (Entry<String, Integer> field : fieldMap.entrySet()) {
                Integer colnumber = field.getValue();
                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
                    Integer isref = fieldMap.get(field.getKey()+"#"+ colnumber);// added '#' while creating map collection for custom fields.
                                                                                // Without this change, it creates problem if two custom columns having name like XYZ and XYZ1
                    String coldata = null;
                    if (isref != null) {
                        try {
                            coldata = customData.getCol(colnumber);
                            if (!StringUtil.isNullOrEmpty(coldata)) {
                                if (coldata.length() > 1 && isExport) {
                                    if (isref == 1) {
                                        coldata = fieldDataManagercntrl.getMultiSelectColData(coldata);
                                    } else if (isref == 0) {
                                        coldata = customData.getRefCol(colnumber);
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

}
