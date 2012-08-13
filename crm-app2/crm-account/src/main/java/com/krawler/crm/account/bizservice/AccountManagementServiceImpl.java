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

package com.krawler.crm.account.bizservice;

import com.krawler.baseline.crm.bizservice.el.ExpressionManager;
import com.krawler.baseline.crm.bizservice.el.ExpressionVariables;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.account.dm.AccountOwnerInfo;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmAccountCustomData;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.accountModule.crmAccountHandler;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.Map.Entry;
import org.hibernate.ObjectNotFoundException;
/**
 *
 * @author sagar
 */
public class AccountManagementServiceImpl implements AccountManagementService {
    private crmAccountDAO crmAccountDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private fieldDataManager fieldDataManagercntrl;
    private commentDAO crmCommentDAOObj;
    private fieldManagerDAO fieldManagerDAOobj;
    private ExpressionManager expressionManager;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private static final Log LOGGER = LogFactory.getLog(AccountManagementServiceImpl.class);
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
    
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
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
     *
     * @param obj
     * @param tmpObj
     * @param FieldMap
     * @throws com.krawler.utils.json.base.JSONException
     */
    public void getCustomColumnJSON(CrmAccount obj, JSONObject tmpObj,HashMap<String, Integer> FieldMap) throws JSONException {
        // create json object of custom columns added in account grid
        if(obj.getCrmAccountCustomDataobj()!=null){
                CrmAccountCustomData CrmAccountCustomDataobj = obj.getCrmAccountCustomDataobj();
                Iterator keyit = FieldMap.keySet().iterator();
                while(keyit.hasNext()){
                    String fieldname = (String) keyit.next();
                    if(FieldMap.get(fieldname) > 0){
                        String coldata = CrmAccountCustomDataobj.getCol(FieldMap.get(fieldname));
                        if(!StringUtil.isNullOrEmpty(coldata)){
                            tmpObj.put(fieldname, coldata);
                        }
                    }
                }
            }
    }

    /**
     *
     * @param obj
     * @param tmpObj
     * @param FieldMap
     * @param isexport
     * @param dateFormat
     * @throws com.krawler.utils.json.base.JSONException
     */
    public void getCustomColumnJSON(CrmAccount obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap, boolean isexport, DateFormat dateFormat) throws JSONException {
        // create json object of custom columns added in account grid
        if (obj.getCrmAccountCustomDataobj() != null && isexport) {
            CrmAccountCustomData CrmAccountCustomDataobj = obj.getCrmAccountCustomDataobj();
            Iterator keyit = FieldMap.keySet().iterator();
            while (keyit.hasNext()) {
                String fieldname = (String) keyit.next();
                Integer colnumber = FieldMap.get(fieldname);

                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
                    Integer isref = FieldMap.get(fieldname + colnumber);
                    String coldata = null;
                    if (isref != null) {
                        coldata = CrmAccountCustomDataobj.getCol(FieldMap.get(fieldname));
                        if (!StringUtil.isNullOrEmpty(coldata) && coldata.length() > 1) {
                            if (isref == 1) {
                                coldata = fieldDataManagercntrl.getMultiSelectColData(coldata);
                            } else if (isref == 0) {
                                coldata = CrmAccountCustomDataobj.getRefCol(FieldMap.get(fieldname));
                            }else if (isref == -3) {
                                coldata =  crmManagerCommon.exportDateNull(new Date(coldata),dateFormat);
                            }
                        }
                        if (!StringUtil.isNullOrEmpty(coldata)) {
                            tmpObj.put(fieldname, coldata);
                        }
                    }
                }
            }
        } else {
            getCustomColumnJSON(obj, tmpObj, FieldMap);
        }
    }

    /**
     * 
     * @param aCrmAccount
     * @param tmpObj
     * @param companyid
     * @param currencyid
     * @param FieldMap
     * @param isexport
     * @param dateFormat
     * @return
     */
    public JSONObject getAccountJsonObject(CrmAccount aCrmAccount, JSONObject tmpObj, String companyid, String currencyid, HashMap<String, Integer> FieldMap,boolean isexport, DateFormat dateFormat) {
        try {
            tmpObj.put("accountid", aCrmAccount.getAccountid());
            tmpObj.put("account", aCrmAccount.getAccountname());
            String[] ownerInfo=crmAccountHandler.getAllAccOwners(aCrmAccount);
            tmpObj.put("subowners", ownerInfo[1]);
            tmpObj.put("accountownerid",ownerInfo[2] );
            tmpObj.put("accountownername", ownerInfo[5]);
            tmpObj.put("website", (aCrmAccount.getWebsite() != null ? aCrmAccount.getWebsite() : ""));
            tmpObj.put("email", StringUtil.checkForNull(aCrmAccount.getEmail())  );
            tmpObj.put("phone", StringUtil.checkForNull(aCrmAccount.getPhone()));
            tmpObj.put("address", StringUtil.checkForNull(aCrmAccount.getMailstreet()));
            tmpObj.put("revenue",  StringUtil.checkForNull(aCrmAccount.getRevenue()));
            //            Fix Me - Add Currency Render
            tmpObj.put("exportrevenue", aCrmAccount.getRevenue() != null && !aCrmAccount.getRevenue().equals("") ? aCrmAccount.getRevenue() : "");
            tmpObj.put("description", (aCrmAccount.getDescription() != null ? aCrmAccount.getDescription() : ""));
            tmpObj.put("createdon", aCrmAccount.getCreatedOn());
            tmpObj.put("creatdon",aCrmAccount.getCreatedOn());
            tmpObj.put("updatedon", aCrmAccount.getUpdatedOn());
            tmpObj.put("accountname", aCrmAccount.getAccountname());
             String[] productInfo=crmAccountHandler.getAccountProducts(aCrmAccount);
            tmpObj.put("productid",productInfo[0]);
            tmpObj.put("product", productInfo[1]);
            tmpObj.put("exportmultiproduct", productInfo[2]);
            tmpObj.put("price", (aCrmAccount.getPrice() != null ? aCrmAccount.getPrice() : ""));
            //            Fix Me - Add Currency Render
            tmpObj.put("exportprice", (aCrmAccount.getPrice() != null && !aCrmAccount.getPrice().equals("") ? aCrmAccount.getPrice() : ""));
            tmpObj.put("accounttypeid", crmManagerCommon.comboNull(aCrmAccount.getCrmCombodataByAccounttypeid()));
            String accounttype = (aCrmAccount.getCrmCombodataByAccounttypeid() != null ? aCrmAccount.getCrmCombodataByAccounttypeid().getValue() : "");
            tmpObj.put("type", accounttype);
            tmpObj.put("accounttype", accounttype);
            tmpObj.put("industryid", crmManagerCommon.comboNull(aCrmAccount.getCrmCombodataByIndustryid()));
            tmpObj.put("industry", (aCrmAccount.getCrmCombodataByIndustryid() != null ? aCrmAccount.getCrmCombodataByIndustryid().getValue() : ""));

            tmpObj.put("validflag", aCrmAccount.getValidflag());
            if (aCrmAccount.getCellstyle() != null && !aCrmAccount.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(aCrmAccount.getCellstyle()));
            }
            getCustomColumnJSON(aCrmAccount,tmpObj,FieldMap,isexport,dateFormat);
            tmpObj = fieldDataManagercntrl.applyColumnFormulae(companyid, currencyid, tmpObj, "1", "Account");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return tmpObj;
    }

    /**
     *
     * @param obj
     * @param tmpObj
     * @param FieldMap
     * @param isexport
     * @param dateFormat
     * @param accountCustomDataMap
     * @throws com.krawler.utils.json.base.JSONException
     */
    public void getCustomColumnJSON(CrmAccount obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap, boolean isexport, DateFormat dateFormat, HashMap<String, CrmAccountCustomData> accountCustomDataMap) throws JSONException {
        // create json object of custom columns added in account grid
        if (accountCustomDataMap.containsKey(obj.getAccountid()) && isexport) {
            CrmAccountCustomData CrmAccountCustomDataobj = accountCustomDataMap.get(obj.getAccountid());
            Iterator keyit = FieldMap.keySet().iterator();
            while (keyit.hasNext()) {
                String fieldname = (String) keyit.next();
                Integer colnumber = FieldMap.get(fieldname);

                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
                    Integer isref = FieldMap.get(fieldname + colnumber);
                    String coldata = null;
                    if (isref != null) {
                        coldata = CrmAccountCustomDataobj.getCol(FieldMap.get(fieldname));
                        if (!StringUtil.isNullOrEmpty(coldata) && coldata.length() > 1) {
                            if (isref == 1) {
                                coldata = fieldDataManagercntrl.getMultiSelectColData(coldata);
                            } else if (isref == 0) {
                                coldata = CrmAccountCustomDataobj.getRefCol(FieldMap.get(fieldname));
                            }
                        }
                        if (!StringUtil.isNullOrEmpty(coldata)) {
                            tmpObj.put(fieldname, coldata);
                        }
                    }
                }
            }
        } else {
            getCustomColumnJSON(obj, tmpObj, FieldMap, accountCustomDataMap);
        }
    }

    /**
     *
     * @param obj
     * @param tmpObj
     * @param FieldMap
     * @param accountCustomDataMap
     * @throws com.krawler.utils.json.base.JSONException
     */
    public void getCustomColumnJSON(CrmAccount obj, JSONObject tmpObj,HashMap<String, Integer> FieldMap, HashMap<String, CrmAccountCustomData> accountCustomDataMap) throws JSONException {
        // create json object of custom columns added in account grid
        if(accountCustomDataMap.containsKey(obj.getAccountid())){
                CrmAccountCustomData CrmAccountCustomDataobj = accountCustomDataMap.get(obj.getAccountid());
                Iterator keyit = FieldMap.keySet().iterator();
                while(keyit.hasNext()){
                    String fieldname = (String) keyit.next();
                    if(FieldMap.get(fieldname) > 0){
                        String coldata = CrmAccountCustomDataobj.getCol(FieldMap.get(fieldname));
                        if(!StringUtil.isNullOrEmpty(coldata)){
                            tmpObj.put(fieldname, coldata);
                        }
                    }
                }
            }
    }

    /**
     * 
     * @param list
     * @param aCrmAccount
     * @param tmpObj
     * @param companyid
     * @param currencyid
     * @param FieldMap
     * @param isexport
     * @param dateFormat
     * @param defaultMasterMap
     * @param accountCustomDataMap
     * @param products 
     * @param owners 
     * @return
     */
    public JSONObject getAccountJsonObject(Map<String, ExpressionVariables> exprVarMap, CrmAccount aCrmAccount, JSONObject tmpObj, String companyid,
            String currencyid, HashMap<String, Integer> FieldMap, DateFormat dateFormat, HashMap<String, DefaultMasterItem> defaultMasterMap, HashMap<String, CrmAccountCustomData> accountCustomDataMap, Map<String, List<AccountOwnerInfo>> owners, Map<String, List<CrmProduct>> products) {
        try {
            tmpObj.put("accountid", aCrmAccount.getAccountid());
            tmpObj.put("account", aCrmAccount.getAccountname());
            String[] ownerInfo = crmAccountHandler.getAllAccOwners(owners.get(aCrmAccount.getAccountid()));
            tmpObj.put("subowners", ownerInfo == null? "": ownerInfo[1]);
            tmpObj.put("accountownerid",ownerInfo == null? "": ownerInfo[2] );
            tmpObj.put("accountowner",ownerInfo == null? "": ownerInfo[0] );
            tmpObj.put("accountownername", ownerInfo == null? "": ownerInfo[5]);
            tmpObj.put("website", (aCrmAccount.getWebsite() != null ? aCrmAccount.getWebsite() : ""));
            tmpObj.put("email", StringUtil.checkForNull(aCrmAccount.getEmail())  );
            tmpObj.put("phone", StringUtil.checkForNull(aCrmAccount.getPhone()));
            tmpObj.put("address", StringUtil.checkForNull(aCrmAccount.getMailstreet()));
            tmpObj.put("revenue",  StringUtil.checkForNull(aCrmAccount.getRevenue()));
            //            Fix Me - Add Currency Render
            tmpObj.put("exportrevenue", aCrmAccount.getRevenue() != null && !aCrmAccount.getRevenue().equals("") ? aCrmAccount.getRevenue() : "");
            tmpObj.put("description", (aCrmAccount.getDescription() != null ? aCrmAccount.getDescription() : ""));
            tmpObj.put("createdon", aCrmAccount.getCreatedOn());
            tmpObj.put("creatdon",aCrmAccount.getCreatedOn());
            tmpObj.put("updatedon", aCrmAccount.getUpdatedOn());
            tmpObj.put("accountname", aCrmAccount.getAccountname());
            if(products.get(aCrmAccount.getAccountid()) != null) {
                String[] productInfo=crmAccountHandler.getAccountProducts(products.get(aCrmAccount.getAccountid()));
                tmpObj.put("productid",productInfo[0]);
                tmpObj.put("product", productInfo[1]);
                tmpObj.put("exportmultiproduct", productInfo[2]);
            } else {
                tmpObj.put("productid","");
                tmpObj.put("product", "");
                tmpObj.put("exportmultiproduct", "");
            }
            tmpObj.put("price", (aCrmAccount.getPrice() != null ? aCrmAccount.getPrice() : ""));
            //            Fix Me - Add Currency Render
            tmpObj.put("exportprice", (aCrmAccount.getPrice() != null && !aCrmAccount.getPrice().equals("") ? aCrmAccount.getPrice() : ""));
            tmpObj.put("accounttypeid", StringUtil.hNull(aCrmAccount.getAccounttypeID()));
            String type = "";
            if(aCrmAccount.getAccounttypeID() != null && defaultMasterMap.containsKey(aCrmAccount.getAccounttypeID())) {
                type = defaultMasterMap.get(aCrmAccount.getAccounttypeID()).getValue();
            }
            tmpObj.put("type", type);
            String accounttype = (aCrmAccount.getCrmCombodataByAccounttypeid() != null ? aCrmAccount.getCrmCombodataByAccounttypeid().getValue() : "");
            tmpObj.put("accounttype", accounttype);
            tmpObj.put("industryid", StringUtil.hNull(aCrmAccount.getIndustryID()));
            String industry = "";
            if(aCrmAccount.getIndustryID() != null && defaultMasterMap.containsKey(aCrmAccount.getIndustryID())) {
                industry = defaultMasterMap.get(aCrmAccount.getIndustryID()).getValue();
            }
            tmpObj.put("industry", industry);

            tmpObj.put("validflag", aCrmAccount.getValidflag());
            if (aCrmAccount.getCellstyle() != null && !aCrmAccount.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(aCrmAccount.getCellstyle()));
            }
            getCustomColumnJSON(aCrmAccount,tmpObj,FieldMap, accountCustomDataMap,exprVarMap);
            getCalculatedCustomColumnJSON(aCrmAccount, tmpObj, exprVarMap);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return tmpObj;
    }
    
    /*public JSONObject getAccountJsonObject1(List list, CrmAccount aCrmAccount, JSONObject tmpObj, String companyid, String currencyid, HashMap<String, Integer> FieldMap,boolean isexport, DateFormat dateFormat, HashMap<String, DefaultMasterItem> defaultMasterMap, HashMap<String, CrmAccountCustomData> accountCustomDataMap) {
        try {
            tmpObj.put("accountid", aCrmAccount.getAccountid());
            tmpObj.put("account", aCrmAccount.getAccountname());
            String[] ownerInfo=crmAccountHandler.getAllAccOwners(aCrmAccount);
            tmpObj.put("subowners", ownerInfo[1]);
            tmpObj.put("accountownerid",ownerInfo[2] );
            tmpObj.put("accountownername", ownerInfo[5]);
            tmpObj.put("website", (aCrmAccount.getWebsite() != null ? aCrmAccount.getWebsite() : ""));
            tmpObj.put("email", StringUtil.checkForNull(aCrmAccount.getEmail())  );
            tmpObj.put("phone", StringUtil.checkForNull(aCrmAccount.getPhone()));
            tmpObj.put("address", StringUtil.checkForNull(aCrmAccount.getMailstreet()));
            tmpObj.put("revenue",  StringUtil.checkForNull(aCrmAccount.getRevenue()));
            //            Fix Me - Add Currency Render
            tmpObj.put("exportrevenue", aCrmAccount.getRevenue() != null && !aCrmAccount.getRevenue().equals("") ? aCrmAccount.getRevenue() : "");
            tmpObj.put("description", (aCrmAccount.getDescription() != null ? aCrmAccount.getDescription() : ""));
            tmpObj.put("createdon", aCrmAccount.getCreatedOn() == null ? "": dateFormat.format(aCrmAccount.getCreatedon()));
            tmpObj.put("creatdon",aCrmAccount.getCreatedOn() == null ? "": dateFormat.format(aCrmAccount.getCreatedon()));
            tmpObj.put("accountname", aCrmAccount.getAccountname());
             String[] productInfo=crmAccountHandler.getAccountProducts(aCrmAccount);
            tmpObj.put("productid",productInfo[0]);
            tmpObj.put("product", productInfo[1]);
            tmpObj.put("exportmultiproduct", productInfo[2]);
            tmpObj.put("price", (aCrmAccount.getPrice() != null ? aCrmAccount.getPrice() : ""));
            //            Fix Me - Add Currency Render
            tmpObj.put("exportprice", (aCrmAccount.getPrice() != null && !aCrmAccount.getPrice().equals("") ? aCrmAccount.getPrice() : ""));
            tmpObj.put("accounttypeid", StringUtil.hNull(aCrmAccount.getAccounttypeID()));
            String type = "";
            if(aCrmAccount.getAccounttypeID() != null && defaultMasterMap.containsKey(aCrmAccount.getAccounttypeID())) {
                type = defaultMasterMap.get(aCrmAccount.getAccounttypeID()).getValue();
            }
            tmpObj.put("type", type);
            tmpObj.put("industryid", StringUtil.hNull(aCrmAccount.getIndustryID()));
            String industry = "";
            if(aCrmAccount.getIndustryID() != null && defaultMasterMap.containsKey(aCrmAccount.getIndustryID())) {
                industry = defaultMasterMap.get(aCrmAccount.getIndustryID()).getValue();
            }
            tmpObj.put("industry", industry);

            tmpObj.put("validflag", aCrmAccount.getValidflag());
            if (aCrmAccount.getCellstyle() != null && !aCrmAccount.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(aCrmAccount.getCellstyle()));
            }
            getCustomColumnJSON(aCrmAccount,tmpObj,FieldMap,isexport,dateFormat, accountCustomDataMap);
            tmpObj = fieldDataManagercntrl.applyColumnFormulae(list, currencyid, tmpObj, "Account");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return tmpObj;
    }*/

    /**
     *
     * @param ll
     * @param totalSize
     * @param isexport
     * @param dateFormat
     * @param userid
     * @param companyid
     * @param currencyid
     * @param selectExportJson
     * @return
     * @throws com.krawler.common.session.SessionExpiredException
     */
    public JSONObject getAccountJson(List ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson) throws SessionExpiredException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Object totalcomment = 0;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_account_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmAccountHandler.getAccountDefaultMasterItemsMap(companyid, crmCommonDAO);

//            List list = fieldDataManagercntrl.getCustomColumnFormulae(new Object[]{companyid, "1"});
            
             int fromIndex = 0;
             int maxNumbers = Constants.maxrecordsMapCount;
             int totalCount = ll.size();
             while(fromIndex < totalCount) {
                 List<CrmAccount> subList;
                 if(totalCount <= maxNumbers) {
                    subList = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    subList = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (CrmAccount obj : subList) {
                    idsList.add(obj.getAccountid());
                 }
                 HashMap<String, String> totalCommentCountMap = crmCommentDAOObj.getTotalCommentsCount(idsList, companyid);
                 HashMap<String, CrmAccountCustomData> accountCustomDataMap = crmAccountDAOObj.getAccountCustomDataMap(idsList, companyid);
                 Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(subList,accountCustomDataMap, companyid,FieldMap,replaceFieldMap, isexport, dateFormat);
                 // get owners
                 Map<String, List<AccountOwnerInfo>> owners = crmAccountDAOObj.getAccountOwners(idsList);
                 
                 // get products
                 Map<String, List<CrmProduct>> products = crmAccountDAOObj.getAccountProducts(idsList);

                 for (CrmAccount aCrmAccount : subList) {
                    requestParams.clear();
                    requestParams.put("recid", aCrmAccount.getAccountid());
                    requestParams.put("companyid", companyid);totalcomment = 0;
                    if(totalCommentCountMap.containsKey(aCrmAccount.getAccountid())) {
                        totalcomment = totalCommentCountMap.get(aCrmAccount.getAccountid());
                    }
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("totalcomment", totalcomment);
                    tmpObj = getAccountJsonObject(exprVarMap, aCrmAccount, tmpObj, companyid, currencyid, FieldMap,
                            dateFormat, defaultMasterMap, accountCustomDataMap, owners, products);
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

    public JSONObject getAccountJsonExport(List<CrmAccount> ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson) throws SessionExpiredException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();        
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_account_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmAccountHandler.getAccountDefaultMasterItemsMap(companyid, crmCommonDAO);
//            List list = fieldDataManagercntrl.getCustomColumnFormulae(new Object[]{companyid, "1"});
            
//            String jsondata = selectExportJson;
//            if (StringUtil.isNullOrEmpty(jsondata)) {//Export/Print All records
                 int fromIndex = 0;
                 int maxNumbers = Constants.maxrecordsMapCount;
                 int totalCount = ll.size();
                 while(fromIndex < totalCount) {
                     List<CrmAccount> subList;
                     if(totalCount <= maxNumbers) {
                        subList = ll;
                     } else {
                        int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                        subList = ll.subList(fromIndex, toIndex);
                     }

                     List<String> idsList = new ArrayList<String>();
                     for (CrmAccount aCrmAccount : subList) {
                          idsList.add(aCrmAccount.getAccountid());
                     }
                     HashMap<String, CrmAccountCustomData> accountCustomDataMap = crmAccountDAOObj.getAccountCustomDataMap(idsList, companyid);
                     Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(subList,accountCustomDataMap, companyid,FieldMap,replaceFieldMap, isexport, dateFormat);
                     
                  // get owners
                     Map<String, List<AccountOwnerInfo>> owners = crmAccountDAOObj.getAccountOwners(idsList);
                     
                     // get products
                     Map<String, List<CrmProduct>> products = crmAccountDAOObj.getAccountProducts(idsList);
                     
                     for (CrmAccount aCrmAccount : subList) {
                        requestParams.clear();
                        requestParams.put("recid", aCrmAccount.getAccountid());
                        requestParams.put("companyid", companyid);
                        JSONObject tmpObj = new JSONObject();
                        tmpObj = getAccountJsonObject(exprVarMap, aCrmAccount, tmpObj, companyid, currencyid, FieldMap,
                                dateFormat, defaultMasterMap, accountCustomDataMap, owners, products);
                        jarr.put(tmpObj);
                    }
                    fromIndex += maxNumbers+1;
                 }
//             }
//            else {//Selected export or print option.
//                JSONArray jArr = new JSONArray("[" + jsondata + "]");
//                totalSize = jArr.length();
//                List<String> idsList = new ArrayList<String>();
//                for (int i = 0; i < totalSize; i++) {
//                    JSONObject jObj = jArr.getJSONObject(i);
//                    idsList.add(jObj.getString("id"));
//                }
//                HashMap<String, CrmAccountCustomData> accountCustomDataMap = crmAccountDAOObj.getAccountCustomDataMap(idsList, companyid);
//
//                // get owners
//                Map<String, List<AccountOwnerInfo>> owners = crmAccountDAOObj.getAccountOwners(idsList);
//
//                // get products
//                Map<String, List<CrmProduct>> products = crmAccountDAOObj.getAccountProducts(idsList);
//
//                for (int i = 0; i < totalSize; i++) {
//                    JSONObject jObj = jArr.getJSONObject(i);
//                    CrmAccount obj = (CrmAccount) kwlCommonTablesDAOObj.getObject("com.krawler.crm.database.tables.CrmAccount", jObj.getString("id"));
//                    JSONObject tmpObj = new JSONObject();
//                    tmpObj = getAccountJsonObject(list, obj, tmpObj, companyid, currencyid, FieldMap, isexport, dateFormat, defaultMasterMap, accountCustomDataMap, owners, products);
//                    jarr.put(tmpObj);
//                }
//             }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
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
     * @param isarchive
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
     * @param start
     * @param limit
     * @param dateFormat
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getAccounts(String companyid, String userid, String currencyid, String selectExportJson,
            String isarchive, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xtype, String xfield,
            String start, String limit, DateFormat dateFormat, StringBuffer usersList) throws ServiceException {
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
                requestParams.put("dateasnum", true);
                if(xtype.equals(Constants.AUTONO)) {
                    crmManagerCommon.getAutoNoPrefixSuffix(fieldManagerDAOobj, companyid, xfield,Constants.Crm_account_moduleid,requestParams);
                }
            }
            requestParams.put("start", StringUtil.checkForNull(start));
            requestParams.put("limit", StringUtil.checkForNull(limit));

            kmsg = crmAccountDAOObj.getAccounts(requestParams, usersList);
            boolean exportflag = false;
            jobj = getAccountJson(kmsg.getEntityList(), kmsg.getRecordTotalCount(), exportflag, dateFormat, userid, companyid, currencyid, selectExportJson);

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
     * @param isarchive
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
     */
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject AccountExport(String companyid, String userid, String currencyid, String selectExportJson, String isarchive,
            String searchJson, String ss, String config, String isExport, boolean heirarchyPerm, String field, String direction, DateFormat dateFormat, StringBuffer usersList) throws ServiceException
    {
            JSONObject jobj = new JSONObject();
            KwlReturnObject kmsg = null;

        try{
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

            List<CrmAccount> ll;
            int totalSize = 0;
            if (StringUtil.isNullOrEmpty(jsondata)) {//Export/Print All records
                kmsg = crmAccountDAOObj.getAccounts(requestParams, usersList);
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
                ll = crmAccountDAOObj.getAccounts(idsList);
            }
            boolean isexport = true;
            jobj = getAccountJsonExport(ll, totalSize, isexport, dateFormat, userid, companyid, currencyid, selectExportJson);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public Map<String, ExpressionVariables> expressionVariableMap(List<CrmAccount> ll, HashMap<String, CrmAccountCustomData> leadCustomDataMap, String companyid, HashMap<String, Integer> FieldMap, HashMap<String, String> replaceFieldMap, boolean isExport,DateFormat dateFormat) {
        Map<String, ExpressionVariables> exprVarMap = new HashMap<String, ExpressionVariables>();
        try {
            List<Object[]> formulae = fieldManagerDAOobj.getModuleCustomFormulae(Constants.Crm_account_moduleid, companyid);
            if (ll != null && !ll.isEmpty()) {
                for (CrmAccount obj : ll) {
                    ExpressionVariables var = new ExpressionVariables();
                    Map<String, Object> variableMapForFormula = new HashMap<String, Object>();
                    Map<String, Object> variableMap = new HashMap<String, Object>();
                    variableMap.put(Constants.Crm_account_modulename, obj);
                    variableMapForFormula.put(Constants.Crm_account_modulename, obj);
                    setCustomColumnValues(obj, FieldMap, replaceFieldMap, variableMap, variableMapForFormula, leadCustomDataMap, isExport, dateFormat);
                    var.setVariablesMap(variableMap);
                    var.setVariablesMapForFormulae(variableMapForFormula);
                    exprVarMap.put(obj.getAccountid(), var);
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

    public void getCustomColumnJSON(CrmAccount obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap, HashMap<String, CrmAccountCustomData> accountCustomDataMap, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        // create json object of custom columns added in lead grid
        if (exprVarMap.containsKey(obj.getAccountid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getAccountid());

            for (Entry<String, Object> varEntry : expVar.getVariablesMap().entrySet()) {
                if (!Constants.Crm_account_modulename.equals(varEntry.getKey())) {
                    String coldata = varEntry.getValue().toString();
                    if (!StringUtil.isNullOrEmpty(coldata)) {
                        tmpObj.put(varEntry.getKey(), coldata);
                    }
                }
            }
        } else {
            if(accountCustomDataMap.containsKey(obj.getAccountid())) {
                CrmAccountCustomData crmAccountCustomDataobj = accountCustomDataMap.get(obj.getAccountid());
                for (Entry<String, Integer> field : FieldMap.entrySet()) {
                    if (field.getValue() > 0) {
                        String coldata = crmAccountCustomDataobj.getCol(field.getValue());
                        if (!StringUtil.isNullOrEmpty(coldata)) {
                            tmpObj.put(field.getKey(), coldata);
                        }
                    }
                }
            }
        }
    }

    protected void getCalculatedCustomColumnJSON(CrmAccount obj, JSONObject tmpObj, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        if (exprVarMap.containsKey(obj.getAccountid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getAccountid());
            for (Entry<String, Object> varEntry : expVar.getOutputMap().entrySet()) {
                if (!Constants.Crm_account_modulename.equals(varEntry.getKey())) {
                    tmpObj.put(varEntry.getKey(), varEntry.getValue());
                }
            }
        }
    }

    protected void setCustomColumnValues(CrmAccount account, HashMap<String, Integer> fieldMap, Map<String, String> replaceFieldMap, Map<String, Object> variableMap, Map<String, Object> variableMapForFormula, HashMap<String, CrmAccountCustomData> accountCustomDataMap, boolean isExport, DateFormat dateFormat) {
        if (accountCustomDataMap.containsKey(account.getAccountid())) {
            CrmAccountCustomData crmLeadCustomData = accountCustomDataMap.get(account.getAccountid());
            for (Entry<String, Integer> field : fieldMap.entrySet()) {
                Integer colnumber = field.getValue();
                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
                    Integer isref = fieldMap.get(field.getKey() +"#"+ colnumber);
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

}
