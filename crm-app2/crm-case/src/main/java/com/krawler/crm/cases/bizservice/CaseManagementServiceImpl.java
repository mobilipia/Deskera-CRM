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

package com.krawler.crm.cases.bizservice;

import com.krawler.baseline.crm.bizservice.el.ExpressionManager;
import com.krawler.baseline.crm.bizservice.el.ExpressionVariables;
import com.krawler.crm.utils.Constants;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.StringUtil;

import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmCaseCustomData;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Iterator;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.caseModule.crmCaseHandler;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.net.URLDecoder;
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
public class CaseManagementServiceImpl implements CaseManagementService {

    private crmCaseDAO crmCaseDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private fieldDataManager fieldDataManagercntrl;
    private commentDAO crmCommentDAOObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private static final Log LOGGER = LogFactory.getLog(CaseManagementServiceImpl.class);
    private crmCommonDAO crmCommonDAO;
    private ExpressionManager expressionManager;
    private fieldManagerDAO fieldManagerDAOobj;
    private profileHandlerDAO profileHandlerDAOObj;

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

    public void setcrmCaseDAO(crmCaseDAO crmCaseDAOObj1) {
        this.crmCaseDAOObj = crmCaseDAOObj1;
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
    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    /**
     *
     * @param companyid
     * @param userid
     * @param currencyid
     * @param selectExportJson
     * @param accountFlag
     * @param isarchive
     * @param mapid
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
    public JSONObject getCases(String companyid, String userid, String currencyid, String selectExportJson, String accountFlag,
            String isarchive, String mapid, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xtype, String xfield,
            String start, String limit, DateFormat dateFormat, StringBuffer usersList) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            boolean archive = Boolean.parseBoolean(isarchive);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if(accountFlag != null) {
                if (Integer.parseInt(accountFlag) == 64) {
                    filter_names.add("c.crmAccount.accountid");
                    filter_params.add(mapid);
                }
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
                    crmManagerCommon.getAutoNoPrefixSuffix(fieldManagerDAOobj, companyid, xfield, Constants.Crm_case_moduleid, requestParams);
                }
            }
            requestParams.put("start", StringUtil.checkForNull(start));
            requestParams.put("limit", StringUtil.checkForNull(limit));
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            requestParams.put("userlist", usersList);

            kmsg = crmCaseDAOObj.getCases(requestParams);

            Map kwlcommentz = getCommentMap(kmsg.getEntityList());

            jobj = getCaseJson(kmsg.getEntityList(), kmsg.getRecordTotalCount(), false, dateFormat, userid, companyid, currencyid, selectExportJson,kwlcommentz);

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
     * @param accountFlag
     * @param isarchive
     * @param mapid
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
    public JSONObject caseExport(String companyid, String userid, String currencyid, String selectExportJson, String accountFlag,
            String isarchive, String mapid, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, DateFormat dateFormat, StringBuffer usersList) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try{
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if (Integer.parseInt(accountFlag) == 64) {
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
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            requestParams.put("userlist", usersList);

            String jsondata = selectExportJson;
            List<CrmCase> ll;
            int totalSize = 0;
            if (StringUtil.isNullOrEmpty(jsondata)) {//Export/Print All records
                kmsg = crmCaseDAOObj.getCases(requestParams);
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
                ll = crmCaseDAOObj.getCases(idsList);
            }

            jobj = getCaseJsonExport(ll, totalSize, true, dateFormat, userid, companyid, currencyid, selectExportJson);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }
     
    /**
     *
     * @param obj
     * @param tmpObj
     * @param FieldMap
     * @param caseCustomDataMap
     * @throws com.krawler.utils.json.base.JSONException
     */
    public void getCustomColumnJSON(CrmCase obj, JSONObject tmpObj,HashMap<String, Integer> FieldMap, HashMap<String, CrmCaseCustomData> caseCustomDataMap) throws JSONException {
        // create json object of custom columns added in case grid
        if(caseCustomDataMap.containsKey(obj.getCaseid())){
                CrmCaseCustomData CrmCaseCustomDataobj = caseCustomDataMap.get(obj.getCaseid());
                Iterator keyit = FieldMap.keySet().iterator();
                while(keyit.hasNext()){
                    String fieldname = (String) keyit.next();
                    if(FieldMap.get(fieldname) > 0){
                        String coldata = CrmCaseCustomDataobj.getCol(FieldMap.get(fieldname));
                        if(!StringUtil.isNullOrEmpty(coldata)){
                            tmpObj.put(fieldname, coldata);
                        }
                    }
                }
            }
    }

    // NOT IN USED
//    /**
//     *
//     * @param obj
//     * @param tmpObj
//     * @param FieldMap
//     * @param isexport
//     * @param dateFormat
//     * @param caseCustomDataMap
//     * @throws com.krawler.utils.json.base.JSONException
//     */
//    public void getCustomColumnJSON(CrmCase obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap, boolean isexport, DateFormat dateFormat, HashMap<String, CrmCaseCustomData> caseCustomDataMap) throws JSONException {
//        // create json object of custom columns added in case grid
//        if (caseCustomDataMap.containsKey(obj.getCaseid()) && isexport) {
//            CrmCaseCustomData CrmCaseCustomDataobj = caseCustomDataMap.get(obj.getCaseid());
//            Iterator keyit = FieldMap.keySet().iterator();
//            while (keyit.hasNext()) {
//                String fieldname = (String) keyit.next();
//                Integer colnumber = FieldMap.get(fieldname);
//
//                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
//                    Integer isref = FieldMap.get(fieldname + colnumber);
//                    String coldata = null;
//                    if (isref != null) {
//                        coldata = CrmCaseCustomDataobj.getCol(FieldMap.get(fieldname));
//                        if (!StringUtil.isNullOrEmpty(coldata) && coldata.length() > 1) {
//                            if (isref == 1) {
//                                coldata = fieldDataManagercntrl.getMultiSelectColData(coldata);
//                            } else if (isref == 0) {
//                                coldata = CrmCaseCustomDataobj.getRefCol(FieldMap.get(fieldname));
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
//            getCustomColumnJSON(obj, tmpObj, FieldMap, caseCustomDataMap);
//        }
//    }

    /**
     *
     * @param list
     * @param obj
     * @param tmpObj
     * @param companyid
     * @param currencyid
     * @param FieldMap
     * @param isexport
     * @param dateFormat
     * @param defaultMasterMap
     * @param caseCustomDataMap
     * @return
     */
    public JSONObject getCaseJsonObject(Map<String, ExpressionVariables> exprVarMap, CrmCase obj, JSONObject tmpObj, String companyid, String currencyid,HashMap<String, Integer> FieldMap,boolean isexport, HashMap<String, DefaultMasterItem> defaultMasterMap, HashMap<String, CrmCaseCustomData> caseCustomDataMap, Map<String, List<CrmProduct>> products, Map<String, CrmAccount> accounts, Map<String, CrmContact> contacts, Map<String, User> owners,Map<String, List<String>> kwlcommentz) {
        try {            

            String comment = StringUtil.getCommentForRecord(kwlcommentz , obj.getCaseid());
            String caseCreatedBy="";
            String firstName="";
            String lastName="";
            tmpObj.put("comment", comment);
            tmpObj.put("caseid", obj.getCaseid());
            User owner = owners.get(obj.getCaseid());
            tmpObj.put("caseownerid", owner.getUserID());
            String ownername =owner.getFirstName() + " " + owner.getLastName();
            tmpObj.put("owner", ownername);
            tmpObj.put("caseowner", ownername);
            tmpObj.put("caseassignedtoid", crmManagerCommon.userObjNull(obj.getAssignedto()));
            String assignedto = StringUtil.isNullObject(obj.getAssignedto())?"":StringUtil.getFullName(obj.getAssignedto().getFirstName(), obj.getAssignedto().getLastName());
            tmpObj.put("caseassignedto", assignedto);
            tmpObj.put("assignedto", assignedto);
            tmpObj.put("casename", StringUtil.checkForNull(obj.getCasename()));
            tmpObj.put("subject", StringUtil.checkForNull(obj.getSubject()));
            CrmAccount account = accounts.get(obj.getCaseid());
            tmpObj.put("accountnameid", crmManagerCommon.moduleObjNull(account, "Accountid"));
            tmpObj.put("accountname", (account != null ? account.getAccountname() : ""));
            CrmContact contact = contacts.get(obj.getCaseid());
            tmpObj.put("contactnameid", crmManagerCommon.moduleObjNull(contact, "Contactid"));
            tmpObj.put("contactname", (contact != null ? (StringUtil.checkForNull(contact.getFirstname()) + " " + StringUtil.checkForNull(contact.getLastname())).trim() : ""));
            String[] productInfo=crmCaseHandler.getCaseProducts(products.get(obj.getCaseid()));
            tmpObj.put("productnameid",productInfo[0]);
            tmpObj.put("productname", productInfo[1]);
            tmpObj.put("exportmultiproduct", productInfo[2]);
            tmpObj.put("casetypeid", StringUtil.hNull(obj.getCasetypeID()));
            String type = "";
            if(obj.getCasetypeID() != null && defaultMasterMap.containsKey(obj.getCasetypeID())) {
                type = defaultMasterMap.get(obj.getCasetypeID()).getValue();
            }
            tmpObj.put("casetype", type);
            tmpObj.put("type", type);
            tmpObj.put("casestatusid", StringUtil.hNull(obj.getCasestatusID()));
            String status = "";
            if(obj.getCasestatusID() != null && defaultMasterMap.containsKey(obj.getCasestatusID())) {
                status = defaultMasterMap.get(obj.getCasestatusID()).getValue();
            }
            tmpObj.put("status", status);
            tmpObj.put("casestatus", status); // used at JS side -- using remote store so need display field to show in grid
            tmpObj.put("casepriorityid", StringUtil.hNull(obj.getCasepriorityID()));
            String priority = "";
            if(obj.getCasepriorityID() != null && defaultMasterMap.containsKey(obj.getCasepriorityID())) {
                priority = defaultMasterMap.get(obj.getCasepriorityID()).getValue();
            }
            tmpObj.put("casepriority", priority);
            tmpObj.put("priority", priority);
            tmpObj.put("createdon",obj.getCreatedOn());
            tmpObj.put("creatdon", obj.getCreatedOn());
            tmpObj.put("updatedon", obj.getUpdatedOn());
            tmpObj.put("description", StringUtil.checkForNull(obj.getDescription()));
            tmpObj.put("validflag", obj.getValidflag());
            if (obj.getCellstyle() != null && !obj.getCellstyle().equals("")) {
                tmpObj.put("cellStyle", new JSONObject(obj.getCellstyle()));
            }
            int createdByFlag=obj.getCreatedByFlag();
            tmpObj.put("createdbyflag", createdByFlag);
            caseCreatedBy=StringUtil.isNullOrEmpty(obj.getCaseCreatedBy())?"":obj.getCaseCreatedBy();
            if(createdByFlag==1){
            	Object[] row =crmCommentDAOObj.getCustomerName(caseCreatedBy);
        		if (row!=null) {    					
    				firstName = StringUtil.isNullOrEmpty((String) row[0])?"":(String) row[0];
    				lastName = StringUtil.isNullOrEmpty((String) row[1])?"":(String) row[1];
    			}     
            	 tmpObj.put("casecreatedby", firstName+" "+lastName.trim());
            }else{
            	tmpObj.put("casecreatedby",profileHandlerDAOObj.getUserFullName(caseCreatedBy));
            }
            getCustomColumnJSON(obj,tmpObj,FieldMap, caseCustomDataMap,exprVarMap);
            getCalculatedCustomColumnJSON(obj, tmpObj, exprVarMap);
//            getCustomColumnJSON(obj,tmpObj,FieldMap,isexport,dateFormat,caseCustomDataMap);
//            tmpObj = fieldDataManagercntrl.applyColumnFormulae(list, currencyid, tmpObj, "Case");
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return tmpObj;
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
     * @param selectExportJson
     * @return
     * @throws com.krawler.common.session.SessionExpiredException
     */
    public JSONObject getCaseJson(List<CrmCase> ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson,Map<String, List<String>> kwlcommentz) throws SessionExpiredException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();        
        try {
            Object totalcomment = 0;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_case_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmCaseHandler.getCasesDefaultMasterItemsMap(companyid, crmCommonDAO);

//             List list = fieldDataManagercntrl.getCustomColumnFormulae(new Object[]{companyid, "3"});
            HashMap<String, String> commentCountMap = crmCommentDAOObj.getNewCommentCount(userid);

             int fromIndex = 0;
             int maxNumbers = Constants.maxrecordsMapCount;
             int totalCount = ll.size();
             while(fromIndex < totalCount) {
                 List<CrmCase> subList;
                 if(totalCount <= maxNumbers) {
                    subList = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    subList = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (CrmCase obj : subList) {
                    idsList.add(obj.getCaseid());
                 }
                 HashMap<String, String> totalCommentCountMap = crmCommentDAOObj.getTotalCaseCommentsCount(idsList, companyid);
                 HashMap<String, CrmCaseCustomData> caseCustomDataMap = crmCaseDAOObj.getCaseCustomDataMap(idsList, companyid);
                 Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(subList,caseCustomDataMap, companyid,FieldMap,replaceFieldMap, isexport, dateFormat);
                 
                 Map<String, List<CrmProduct>> products = crmCaseDAOObj.getCaseProducts(idsList);
                 Map<String, CrmAccount> accounts = crmCaseDAOObj.getCaseAccounts(idsList);
                 Map<String, User> owners = crmCaseDAOObj.getCaseOwners(idsList);
                 Map<String, CrmContact> contacts = crmCaseDAOObj.getCaseContacts(idsList);

                 for (CrmCase obj : subList) {
                    requestParams.clear();
                    requestParams.put("recid", obj.getCaseid());
                    requestParams.put("companyid", companyid);
                    JSONObject tmpObj = new JSONObject();
                    totalcomment = 0;
                    if(totalCommentCountMap.containsKey(obj.getCaseid())) {
                        totalcomment = totalCommentCountMap.get(obj.getCaseid());
                    }
                    tmpObj.put("totalcomment", totalcomment);

                    tmpObj = getCaseJsonObject(exprVarMap, obj, tmpObj, companyid, currencyid, FieldMap, isexport, defaultMasterMap, caseCustomDataMap, products,accounts, contacts, owners, kwlcommentz);

                    tmpObj.put("commentcount", (commentCountMap.containsKey(obj.getCaseid())?commentCountMap.get(obj.getCaseid()):"0"));
                    jarr.put(tmpObj);
                 }
                 fromIndex += maxNumbers+1;
             }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return jobj;
    }

    public JSONObject getCaseJsonExport(List ll, int totalSize,boolean isexport, DateFormat dateFormat, String userid, String companyid, String currencyid, String selectExportJson) throws SessionExpiredException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("isexport",isexport);
            requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
            requestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_case_moduleid));
            HashMap<String, String> replaceFieldMap = new HashMap<String, String>();
            HashMap<String, Integer> FieldMap = fieldDataManagercntrl.getFieldParamsMap1(requestParams, replaceFieldMap);

            HashMap<String, DefaultMasterItem> defaultMasterMap = crmCaseHandler.getCasesDefaultMasterItemsMap(companyid, crmCommonDAO);
             int fromIndex = 0;
             int maxNumbers = Constants.maxrecordsMapCount;
             int totalCount = ll.size();
             while(fromIndex < totalCount) {
                 List<CrmCase> subList;
                 if(totalCount <= maxNumbers) {
                    subList = ll;
                 } else {
                    int toIndex = (((fromIndex+maxNumbers)<totalCount)?(fromIndex+maxNumbers)+1:totalCount);//Add +1 as subList exclude toIndex item
                    subList = ll.subList(fromIndex, toIndex);
                 }

                 List<String> idsList = new ArrayList<String>();
                 for (CrmCase obj : subList) {
                      idsList.add(obj.getCaseid());
                 }
                 HashMap<String, CrmCaseCustomData> caseCustomDataMap = crmCaseDAOObj.getCaseCustomDataMap(idsList, companyid);
                 Map<String, ExpressionVariables> exprVarMap = expressionVariableMap(subList,caseCustomDataMap, companyid,FieldMap,replaceFieldMap, isexport, dateFormat);

                 Map<String, List<CrmProduct>> products = crmCaseDAOObj.getCaseProducts(idsList);
                 Map<String, CrmAccount> accounts = crmCaseDAOObj.getCaseAccounts(idsList);
                 Map<String, User> owners = crmCaseDAOObj.getCaseOwners(idsList);
                 Map<String, CrmContact> contacts = crmCaseDAOObj.getCaseContacts(idsList);

                 Map kwlcommentz = getCommentMap(ll);

                 for (CrmCase obj : subList) {
                    requestParams.clear();
                    requestParams.put("recid", obj.getCaseid());
                    requestParams.put("companyid", companyid);
                    JSONObject tmpObj = new JSONObject();

                    tmpObj = getCaseJsonObject(exprVarMap, obj, tmpObj, companyid, currencyid, FieldMap, isexport, defaultMasterMap, caseCustomDataMap, products,accounts, contacts, owners, kwlcommentz);

                    jarr.put(tmpObj);
                }
                fromIndex += maxNumbers+1;
             }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(),e);
        }
        return jobj;
    }

    public Map<String, ExpressionVariables> expressionVariableMap(List<CrmCase> ll, HashMap<String, CrmCaseCustomData> caseCustomDataMap, String companyid, HashMap<String, Integer> FieldMap, HashMap<String, String> replaceFieldMap, boolean isExport,DateFormat dateFormat) {
        Map<String, ExpressionVariables> exprVarMap = new HashMap<String, ExpressionVariables>();
        try {
            List<Object[]> formulae = fieldManagerDAOobj.getModuleCustomFormulae(Constants.Crm_case_moduleid, companyid);
            if (ll != null && !ll.isEmpty()) {
                for (CrmCase obj : ll) {
                    ExpressionVariables var = new ExpressionVariables();
                    Map<String, Object> variableMapForFormula = new HashMap<String, Object>();
                    Map<String, Object> variableMap = new HashMap<String, Object>();
                    variableMap.put(Constants.Crm_case_modulename, obj);
                    variableMapForFormula.put(Constants.Crm_case_modulename, obj);
                    setCustomColumnValues(obj, FieldMap, replaceFieldMap, variableMap, variableMapForFormula, caseCustomDataMap, isExport, dateFormat);
                    var.setVariablesMap(variableMap);
                    var.setVariablesMapForFormulae(variableMapForFormula);
                    exprVarMap.put(obj.getCaseid(), var);
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

    public void getCustomColumnJSON(CrmCase obj, JSONObject tmpObj, HashMap<String, Integer> FieldMap, HashMap<String, CrmCaseCustomData> caseCustomDataMap, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        // create json object of custom columns added in case grid
        if (exprVarMap.containsKey(obj.getCaseid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getCaseid());

            for (Entry<String, Object> varEntry : expVar.getVariablesMap().entrySet()) {
                if (!Constants.Crm_case_modulename.equals(varEntry.getKey())) {
                    String coldata = varEntry.getValue().toString();
                    if (!StringUtil.isNullOrEmpty(coldata)) {
                        tmpObj.put(varEntry.getKey(), coldata);
                    }
                }
            }
        } else {
            if(caseCustomDataMap.containsKey(obj.getCaseid())) {
                CrmCaseCustomData crmCaseCustomDataobj = caseCustomDataMap.get(obj.getCaseid());
                for (Entry<String, Integer> field : FieldMap.entrySet()) {
                    if (field.getValue() > 0) {
                        String coldata = crmCaseCustomDataobj.getCol(field.getValue());
                        if (!StringUtil.isNullOrEmpty(coldata)) {
                            tmpObj.put(field.getKey(), coldata);
                        }
                    }
                }
            }
        }
    }

    protected void getCalculatedCustomColumnJSON(CrmCase obj, JSONObject tmpObj, Map<String, ExpressionVariables> exprVarMap) throws JSONException {
        if (exprVarMap.containsKey(obj.getCaseid())) {
            ExpressionVariables expVar = exprVarMap.get(obj.getCaseid());
            for (Entry<String, Object> varEntry : expVar.getOutputMap().entrySet()) {
                if (!Constants.Crm_case_modulename.equals(varEntry.getKey())) {
                    tmpObj.put(varEntry.getKey(), varEntry.getValue());
                }
            }
        }
    }

    protected void setCustomColumnValues(CrmCase caseObj, HashMap<String, Integer> fieldMap, Map<String, String> replaceFieldMap, Map<String, Object> variableMap, Map<String, Object> variableMapForFormula, HashMap<String, CrmCaseCustomData> caseCustomDataMap, boolean isExport, DateFormat dateFormat) {
        if (caseCustomDataMap.containsKey(caseObj.getCaseid())) {
            CrmCaseCustomData crmCaseCustomData = caseCustomDataMap.get(caseObj.getCaseid());
            for (Entry<String, Integer> field : fieldMap.entrySet()) {
                Integer colnumber = field.getValue();
                if (colnumber > 0) { // colnumber will be 0 if key is part of reference map
                    Integer isref = fieldMap.get(field.getKey()+"#"+ colnumber);// added '#' while creating map collection for custom fields.
                                                                                // Without this code it creates problem if two custom columns having name like XYZ and XYZ1
                    String coldata = null;
                    if (isref != null) {
                        try {
                            coldata = crmCaseCustomData.getCol(colnumber);
                            if (!StringUtil.isNullOrEmpty(coldata)) {
                                if (coldata.length() > 1 && isExport) {
                                    if (isref == 1) {
                                        coldata = fieldDataManagercntrl.getMultiSelectColData(coldata);
                                    } else if (isref == 0) {
                                        coldata = crmCaseCustomData.getRefCol(colnumber);
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

    public Map<String, CrmCase> getCommentMap(List<CrmCase> ll ) {
        Map kwlcommentz = new HashMap();
            if(ll.size()>0){
                List<CrmCase> CrmCaseList = ll;
                List<String> idsList = new ArrayList<String>();

                for(CrmCase crmCase : CrmCaseList){
                    idsList.add(crmCase.getCaseid());
                }
                kwlcommentz = crmCommentDAOObj.getCaseCommentz(idsList);
            }
        return kwlcommentz;
    }
 
}
