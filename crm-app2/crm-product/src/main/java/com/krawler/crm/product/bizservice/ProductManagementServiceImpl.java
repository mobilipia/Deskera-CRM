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

package com.krawler.crm.product.bizservice;

import com.krawler.baseline.crm.bizservice.el.ExpressionManager;
import com.krawler.baseline.crm.bizservice.el.ExpressionVariables;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.CrmProductCustomData;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.crm.productModule.crmProductDAO;
import com.krawler.spring.crm.productModule.crmProductHandler;
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
public class ProductManagementServiceImpl implements ProductManagementService {

    private crmProductDAO crmProductDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private fieldDataManager fieldDataManagercntrl;
    private commentDAO crmCommentDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private static final Log LOGGER = LogFactory.getLog(ProductManagementServiceImpl.class);
    private crmCommonDAO crmCommonDAO;
    private fieldManagerDAO fieldManagerDAOobj;
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

    public crmProductDAO getcrmProductDAO(){
        return crmProductDAOObj;
    }

    public void setcrmProductDAO(crmProductDAO crmProductDAOObj1) {
        this.crmProductDAOObj = crmProductDAOObj1;
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
    public JSONObject getProducts(String companyid, String userid, String currencyid, String selectExportJson, 
            String isarchive, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xtype, String xfield, String type,
            String start, String limit, DateFormat dateFormat, StringBuffer usersList)
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
                    crmManagerCommon.getAutoNoPrefixSuffix(fieldManagerDAOobj, companyid, xfield,Constants.Crm_product_moduleid,requestParams);
                }
            }
            if(!StringUtil.isNullOrEmpty(type)) {
                requestParams.put("type", type);
            }
            if(!StringUtil.isNullOrEmpty(start) || !StringUtil.isNullOrEmpty(limit)) {
                requestParams.put("start", StringUtil.checkForNull(start));
                requestParams.put("limit", StringUtil.checkForNull(limit));
            }
            kmsg = crmProductDAOObj.getProducts(requestParams, usersList);
            boolean exportflag = false;
            jobj = getProductJson(kmsg.getEntityList(), kmsg.getRecordTotalCount(), exportflag, dateFormat, userid, companyid, currencyid, selectExportJson);

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
    public JSONObject ProductExport(String companyid, String userid, String currencyid, String selectExportJson,
            String isarchive, String searchJson, String ss, String config, String isExport,
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
            List<CrmProduct> ll;
            int totalSize = 0;
            if (StringUtil.isNullOrEmpty(jsondata)) {//Export/Print All records
                kmsg = crmProductDAOObj.getProducts(requestParams, usersList);
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
                ll = crmProductDAOObj.getProducts(idsList);
            }

//            kmsg = crmProductDAOObj.getProducts(requestParams, usersList);
            boolean isexport = true;
            jobj = getProductJsonExport(ll, totalSize, isexport, dateFormat, userid, companyid, currencyid, selectExportJson);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

//    public void getCustomColumnJSON(CrmProduct obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap, HashMap<String, CrmProductCustomData> productCustomDataMap) throws JSONException {
//        // create json object of custom columns added in lead grid
//        if(productCustomDataMap.containsKey(obj.getProductid())){
//                CrmProductCustomData CrmProductCustomDataobj = productCustomDataMap.get(obj.getProductid());
//                Iterator keyit = FieldMap.keySet().iterator();
//                while(keyit.hasNext()){
//                    String fieldname = (String) keyit.next();
//                    if(FieldMap.get(fieldname) > 0){
//                        String coldata = CrmProductCustomDataobj.getCol(FieldMap.get(fieldname));
//                        if(!StringUtil.isNullOrEmpty(coldata)){
//                            tmpObj.put(fieldname, coldata);
//                        }
//                    }
//                }
//            }
//    }

//    public void getCustomColumnJSON(CrmProduct obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap, boolean isexport, DateFormat dateFormat, HashMap<String, CrmProductCustomData> productCustomDataMap) throws JSONException {
//        // create json object of custom columns added in lead grid
//        if (productCustomDataMap.containsKey(obj.getProductid()) && isexport) {
//            CrmProductCustomData CrmProductCustomDataobj = productCustomDataMap.get(obj.getProductid());
//            Iterator keyit = FieldMap.keySet().iterator();
//            while (keyit.hasNext()) {
//                String fieldname = (String) keyit.next();
//                Integer colnumber = FieldMap.get(fieldname);
//
//                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
//                    Integer isref = FieldMap.get(fieldname + colnumber);
//                    String coldata = null;
//                    if (isref != null) {
//                        coldata = CrmProductCustomDataobj.getCol(FieldMap.get(fieldname));
//                        if (!StringUtil.isNullOrEmpty(coldata) && coldata.length() > 1) {
//                            if (isref == 1) {
//                                coldata = fieldDataManagercntrl.getMultiSelectColData(coldata);
//                            } else if (isref == 0) {
//                                coldata = CrmProductCustomDataobj.getRefCol(FieldMap.get(fieldname));
//                            }else if (isref == -3) {
//                                coldata =  crmManagerCommon.exportDateNull(new Date(coldata),dateFormat) ;
//                            }
//                        }
//                        if (!StringUtil.isNullOrEmpty(coldata)) {
//                            tmpObj.put(fieldname, coldata);
//                        }
//                    }
//                }
//            }
//        } else {
//            getCustomColumnJSON(obj, tmpObj, FieldMap, productCustomDataMap);
//        }
//    }

    public JSONObject getProductJsonObject(Map<String, ExpressionVariables> exprVarMap, CrmProduct crmProduct, Map<String, User> owners, JSONObject tmpObj, String companyid, String currencyid,
            HashMap<String, Integer> FieldMap,boolean isexport, DateFormat dateFormat, HashMap<String, DefaultMasterItem> defaultMasterMap, HashMap<String, CrmProductCustomData> productCustomDataMap) {
        try {
            tmpObj.put("productid", crmProduct.getProductid());
            tmpObj.put("name", crmProduct.getProductname());
            User user = owners.get(crmProduct.getProductid());
            tmpObj.put("ownerid", user.getUserID());
            tmpObj.put("owner", StringUtil.getFullName(user));
            tmpObj.put("code", StringUtil.checkForNull(crmProduct.getCode()));
            tmpObj.put("commisionrate", StringUtil.checkForNull(crmProduct.getCommisionrate()));
            tmpObj.put("createdon",crmProduct.getCreatedOn()!=null?crmProduct.getCreatedOn():"");
            tmpObj.put("creatdon", crmProduct.getCreatedOn()!=null?crmProduct.getCreatedOn():"");
            tmpObj.put("currencyid", StringUtil.checkForNull(crmProduct.getCurrencyid()));
            tmpObj.put("description", StringUtil.checkForNull(crmProduct.getDescription()));
            tmpObj.put("productname", StringUtil.checkForNull(crmProduct.getProductname()));
            tmpObj.put("quantityindemand", StringUtil.checkForNull(crmProduct.getQuantityindemand()));
            tmpObj.put("quantitylevel", StringUtil.checkForNull(crmProduct.getQuantitylevel()));
            tmpObj.put("stockquantity", StringUtil.checkForNull(crmProduct.getStockquantity()));
            tmpObj.put("taxincurred", StringUtil.checkForNull(crmProduct.getTaxincurred()));
            tmpObj.put("threshold", StringUtil.checkForNull(crmProduct.getThreshold()));
            tmpObj.put("unitprice", StringUtil.checkForNull(crmProduct.getUnitprice()));
            // Fix Me - Add Currency Render
            // tmpObj.put("exportprice", crmProduct.getUnitprice() != null && !crmProduct.getUnitprice().equals("") ? crmManagerDAOObj.currencyRender(crmProduct.getUnitprice(), currencyid) : "");
            tmpObj.put("exportprice", crmProduct.getUnitprice() != null && !crmProduct.getUnitprice().equals("") ? crmProduct.getUnitprice() : "");
            tmpObj.put("updatedon", crmProduct.getUpdatedOn()!=null?crmProduct.getUpdatedOn():"");
            tmpObj.put("categoryid", StringUtil.hNull(crmProduct.getProductcategoryID()));
            String category = "";
            if(crmProduct.getProductcategoryID() != null && defaultMasterMap.containsKey(crmProduct.getProductcategoryID())) {
                category = defaultMasterMap.get(crmProduct.getProductcategoryID()).getValue();
            }
            tmpObj.put("category", category);
            tmpObj.put("vendornamee", StringUtil.checkForNull(crmProduct.getVendornamee()));
            tmpObj.put("vendorphoneno", crmProduct.getVendorphoneno() != null ? crmProduct.getVendorphoneno() : "");
            tmpObj.put("vendoremail", crmProduct.getVendoremail() != null ? crmProduct.getVendoremail() : "");
            tmpObj.put("validflag", crmProduct.getValidflag());

            // tmpObj.put("commentcount", crmCommentDAOObj.getNewCommentList(sessionHandlerImpl.getUserid(request), crmProduct.getProductid()));
            if (crmProduct.getCellstyle() != null && !crmProduct.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(crmProduct.getCellstyle()));
            }
//            getCustomColumnJSON(crmProduct,tmpObj,FieldMap,isexport,dateFormat, productCustomDataMap);
            // tmpObj = fieldManager.getCustomColumnJSON(request, tmpObj, "product", crmProduct.getProductid(), "4");
            getCustomColumnJSON(crmProduct,tmpObj,FieldMap,productCustomDataMap,exprVarMap);
            getCalculatedCustomColumnJSON(crmProduct, tmpObj, exprVarMap);
//            tmpObj = fieldDataManagercntrl.applyColumnFormulae(formulaeList, currencyid, tmpObj, "Product");
        } catch (Exception e) {
          LOGGER.warn(e.getMessage(), e);
        }
        return tmpObj;
    }

    /**
     *
     * @param List - List of CrmProduct class objects.
     * @param request
     * @return JSONObject - It contains all the fields of CrmProduct class
     * including total comments and unread comments count
     */
    public JSONObject getProductJson(List<CrmProduct> ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson) throws SessionExpiredException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            Object totalcomment = 0;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_product_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);

            requestParams.clear();
            
            HashMap<String, DefaultMasterItem> defaultMasterMap = crmProductHandler.getProductDefaultMasterItemsMap(companyid, crmCommonDAO);
//             List formulaeList = fieldDataManagercntrl.getCustomColumnFormulae(new Object[]{companyid, "4"});
             HashMap<String, String> commentCountMap = crmCommentDAOObj.getNewCommentCount(userid);

             int fromIndex = 0;
             int maxNumbers = Constants.maxrecordsMapCount;
             int totalCount = ll.size();
             while(fromIndex < totalCount) {
                 List<CrmProduct> subList;
                 if(totalCount <= maxNumbers) {
                    subList = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    subList = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (CrmProduct product : subList)
                {
//                    CrmProduct product = (CrmProduct) obj[0];
                    idsList.add(product.getProductid());
                }
                 HashMap<String, String> totalCommentCountMap = crmCommentDAOObj.getTotalCommentsCount(idsList, companyid);
                 HashMap<String, CrmProductCustomData> productCustomDataMap = crmProductDAOObj.getProductCustomDataMap(idsList, companyid);
                 Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(subList,productCustomDataMap, companyid,FieldMap,replaceFieldMap, isexport, dateFormat);
                 Map<String, User> owners = crmProductDAOObj.getProductOwners(idsList);
                 for (CrmProduct product : subList) {
                    requestParams.clear();
//                    CrmProduct product = (CrmProduct) obj[0];
//                    User user = (User) product.getUsersByUserid();
                    requestParams.put("recid", product.getProductid());
                    requestParams.put("companyid", companyid);
                    JSONObject tmpObj = new JSONObject();
                    totalcomment = 0;
                    if(totalCommentCountMap.containsKey(product.getProductid())) {
                        totalcomment = totalCommentCountMap.get(product.getProductid());
                    }
                    tmpObj.put("totalcomment", totalcomment);
                    tmpObj = getProductJsonObject(exprVarMap, product, owners, tmpObj, companyid, currencyid, FieldMap, isexport, dateFormat, defaultMasterMap, productCustomDataMap);
                    tmpObj.put("commentcount", (commentCountMap.containsKey(product.getProductid())?commentCountMap.get(product.getProductid()):"0"));
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

    public JSONObject getProductJsonExport(List<CrmProduct> ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson) throws SessionExpiredException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_product_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmProductHandler.getProductDefaultMasterItemsMap(companyid, crmCommonDAO);

            int fromIndex = 0;
            int maxNumbers = Constants.maxrecordsMapCount;
            int totalCount = ll.size();
            while(fromIndex < totalCount) {
                 List<CrmProduct> subList;
                 if(totalCount <= maxNumbers) {
                    subList = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    subList = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (CrmProduct product : subList)
                {
//                        CrmProduct product = (CrmProduct) obj[0];
                    idsList.add(product.getProductid());
                }
                 HashMap<String, CrmProductCustomData> productCustomDataMap = crmProductDAOObj.getProductCustomDataMap(idsList, companyid);
                Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(subList,productCustomDataMap, companyid,FieldMap,replaceFieldMap, isexport, dateFormat);
                Map<String, User> owners = crmProductDAOObj.getProductOwners(idsList);
                 for (CrmProduct product : subList) {
//                         CrmProduct product = (CrmProduct) obj[0];
//                     User user = product.getUsersByUserid();
                    requestParams.clear();
                    requestParams.put("recid", product.getProductid());
                    requestParams.put("companyid", companyid);
                    JSONObject tmpObj = new JSONObject();
                    tmpObj = getProductJsonObject(exprVarMap, product, owners, tmpObj, companyid, currencyid, FieldMap, isexport, dateFormat, defaultMasterMap, productCustomDataMap);
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

    public Map<String, ExpressionVariables> expressionVariableMap(List<CrmProduct> ll, HashMap<String, CrmProductCustomData> productCustomDataMap,
            String companyid, HashMap<String, Integer> FieldMap, HashMap<String, String> replaceFieldMap, boolean isExport,DateFormat dateFormat) {
        Map<String, ExpressionVariables> exprVarMap = new HashMap<String, ExpressionVariables>();
        try {
            List<Object[]> formulae = fieldManagerDAOobj.getModuleCustomFormulae(Constants.Crm_product_moduleid, companyid);
            if (ll != null && !ll.isEmpty()) {
                for (CrmProduct obj : ll) {
                    ExpressionVariables var = new ExpressionVariables();
                    Map<String, Object> variableMapForFormula = new HashMap<String, Object>();
                    Map<String, Object> variableMap = new HashMap<String, Object>();
                    variableMap.put(Constants.Crm_product_modulename, obj);
                    variableMapForFormula.put(Constants.Crm_product_modulename, obj);
                    setCustomColumnValues(obj, FieldMap, replaceFieldMap, variableMap, variableMapForFormula, productCustomDataMap, isExport, dateFormat);
                    var.setVariablesMap(variableMap);
                    var.setVariablesMapForFormulae(variableMapForFormula);
                    exprVarMap.put(obj.getProductid(), var);
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

    public void getCustomColumnJSON(CrmProduct obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap, HashMap<String, CrmProductCustomData> productCustomDataMap, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        // create json object of custom columns added in product grid
        if (exprVarMap.containsKey(obj.getProductid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getProductid());

            for (Entry<String, Object> varEntry : expVar.getVariablesMap().entrySet()) {
                if (!Constants.Crm_product_modulename.equals(varEntry.getKey())) {
                    String coldata = varEntry.getValue().toString();
                    if (!StringUtil.isNullOrEmpty(coldata)) {
                        tmpObj.put(varEntry.getKey(), coldata);
                    }
                }
            }
        } else {
            if(productCustomDataMap.containsKey(obj.getProductid())) {
                CrmProductCustomData crmProductCustomDataobj = productCustomDataMap.get(obj.getProductid());
                for (Entry<String, Integer> field : FieldMap.entrySet()) {
                    if (field.getValue() > 0) {
                        String coldata = crmProductCustomDataobj.getCol(field.getValue());
                        if (!StringUtil.isNullOrEmpty(coldata)) {
                            tmpObj.put(field.getKey(), coldata);
                        }
                    }
                }
            }
        }
    }

    protected void getCalculatedCustomColumnJSON(CrmProduct obj, JSONObject tmpObj, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        if (exprVarMap.containsKey(obj.getProductid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getProductid());
            for (Entry<String, Object> varEntry : expVar.getOutputMap().entrySet()) {
                if (!Constants.Crm_product_modulename.equals(varEntry.getKey())) {
                    tmpObj.put(varEntry.getKey(), varEntry.getValue());
                }
            }
        }
    }

    protected void setCustomColumnValues(CrmProduct product, HashMap<String, Integer> fieldMap, Map<String, String> replaceFieldMap, Map<String, Object> variableMap, Map<String, Object> variableMapForFormula, HashMap<String, CrmProductCustomData> productCustomDataMap, boolean isExport, DateFormat dateFormat) {
        if (productCustomDataMap.containsKey(product.getProductid())) {
            CrmProductCustomData crmProductCustomData = productCustomDataMap.get(product.getProductid());
            for (Entry<String, Integer> field : fieldMap.entrySet()) {
                Integer colnumber = field.getValue();
                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
                    Integer isref = fieldMap.get(field.getKey()+"#" + colnumber);// added '#' while creating map collection for custom fields.
                                                                                // Without this change, it creates problem if two custom columns having name like XYZ and XYZ1
                    String coldata = null;
                    if (isref != null) {
                        try {
                            coldata = crmProductCustomData.getCol(colnumber);
                            if (!StringUtil.isNullOrEmpty(coldata)) {
                                if (coldata.length() > 1 && isExport) {
                                    if (isref == 1) {
                                        coldata = fieldDataManagercntrl.getMultiSelectColData(coldata);
                                    } else if (isref == 0) {
                                        coldata = crmProductCustomData.getRefCol(colnumber);
//                                    } else if (isref == -3) {
//                                    	try{
//                                    		coldata = Long.getLong(coldata).toString();
//                                    	}catch(){
//                                    		
//                                    	}
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
