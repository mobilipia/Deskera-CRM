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

import com.krawler.common.admin.FieldComboData;
import com.krawler.common.util.DataInvalidateException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmAccountCustomData;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.TargetList;
import com.krawler.crm.database.tables.TargetModule;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import com.krawler.common.util.Constants;
import com.krawler.crm.database.tables.CrmContactCustomData;
import com.krawler.crm.database.tables.CrmLeadCustomData;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmOpportunityCustomData;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.targetModule.crmTargetDAO;
import com.krawler.spring.importFunctionality.ImportHandler;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author krawler
 */
public class ImportRecordAdvisor implements MethodInterceptor {
    private static final Log logger = LogFactory.getLog(ImportRecordAdvisor.class);
    private crmCommonDAO crmCommonDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private fieldDataManager fieldDataManagercntrl;
    private fieldManagerDAO fieldManagerDAOobj;
    private crmLeadDAO crmLeadDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmContactDAO crmContactDAOObj;
    private crmTargetDAO crmTargetDAOObj;

    public void setCrmAccountDAO(crmAccountDAO crmAccountDAOObj) {
        this.crmAccountDAOObj = crmAccountDAOObj;
    }

    public void setCrmContactDAO(crmContactDAO crmContactDAOObj) {
        this.crmContactDAOObj = crmContactDAOObj;
    }

    public void setCrmLeadDAO(crmLeadDAO crmLeadDAOObj) {
        this.crmLeadDAOObj = crmLeadDAOObj;
    }

    public void setCrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj;
    }

    public void setCrmTargetDAO(crmTargetDAO crmTargetDAOObj) {
        this.crmTargetDAOObj = crmTargetDAOObj;
    }

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }
    
    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public crmManagerDAO getcrmManagerDAO() {
        return crmManagerDAOObj;
    }

    public Object invoke(MethodInvocation mi) throws Throwable {
        Object result = null;
        boolean proceed = true;
        String methodName = mi.getMethod().getName();

        if(methodName.equals("saveRecord")) {
            proceed = BeforeSaveRecord(mi);
            // Throw DataInvalidateException : To stop execution and to log Invalidate data message or any other error
            // Return false : To stop execution without any error log
            // Return true  : To continue the execution
            if(proceed) {

                result = mi.proceed(); //Execute main method
                AfterSaveRecord(mi, result);
            }
        } else if(methodName.equals("getRefModuleData")) {
            BeforeGetRefModuleData(mi);
            result = mi.proceed(); //Execute main method
            AfterGetRefModuleData(mi, result);
        } else if(methodName.equals("getCustomComboID")) {
            result = mi.proceed(); //Execute main method
            AfterGetCustomComboID(mi, result);
        } else {
            result = mi.proceed();
        }

        return result;
    }

    private boolean BeforeSaveRecord(MethodInvocation mi) throws DataInvalidateException {
        boolean proceed = true;
        //public Object saveRecord(HttpServletRequest request, HashMap<String, Object> dataMap, CsvReader csvReader, String modeName, String classPath, String primaryKey)
        Object arguments[] = mi.getArguments();
        String mode = (String) arguments[3];
        try {
            HashMap<String, Object> dataMap = (HashMap<String, Object>) arguments[1];
            HashMap<String, Object> requestParams = (HashMap<String, Object>) arguments[0];
            dataMap.put(Constants.Validflag, 2);

            if(!dataMap.containsKey("Createdon") || dataMap.get("Createdon") == null){
                dataMap.put("Createdon", new Date());
            } else if (StringUtil.isNullOrEmpty(dataMap.get("Createdon").toString())) {
                dataMap.put("Createdon", new Date());
            }

            if(mode.equalsIgnoreCase("contact") && !StringUtil.isNullOrEmpty(requestParams.get("mapid").toString())) {
                if(!StringUtil.isNullOrEmpty(requestParams.get("relatedName").toString())) {
                    if(requestParams.get("relatedName").toString().equals("Lead"))
                        dataMap.put("Lead", requestParams.get("mapid").toString());
                    else
                        dataMap.put("CrmAccount", requestParams.get("mapid").toString());
                }
            }
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw new DataInvalidateException("Failed to create account for "+mode+": "+ex.getMessage());
        }

        return proceed;
    }

    private void AfterSaveRecord(MethodInvocation mi, Object result) throws DataInvalidateException {
        Object arguments[] = mi.getArguments();
        String mode = (String) arguments[3];
        try {
            HashMap<String, Object> requestParams = (HashMap<String, Object>) arguments[0];
            String companyid = requestParams.get("companyid").toString();
            String module = (String) arguments[3];
            if(mode.equalsIgnoreCase("lead")) {
                HashMap<String, Object> dataMap = (HashMap<String, Object>) arguments[1];
                String ownerid = dataMap.get("UsersByCreatedbyid").toString();
                String leadid = ((CrmLead) result).getLeadid();
                crmLeadDAOObj.setMainLeadOwner(new String[]{leadid}, ownerid);
                if(dataMap.containsKey("CrmProduct") && dataMap.get("CrmProduct")!=null){
                    String productid = dataMap.get("CrmProduct").toString();
                    crmLeadDAOObj.saveLeadProducts(new String[]{leadid},productid.split(","));
                }
                JSONArray customfield = (JSONArray) arguments[7];
                if(customfield.length() > 0) {
                        HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                        customrequestParams.put("customarray", customfield);
                        customrequestParams.put("modulename", Constants.Crm_Lead_modulename);
                        customrequestParams.put("moduleprimarykey", Constants.Crm_Leadid);
                        customrequestParams.put("modulerecid", leadid);
                        customrequestParams.put("companyid", companyid);
                        customrequestParams.put("customdataclasspath", Constants.Crm_lead_custom_data_classpath);
//                    fieldManager.storeCustomFields(jcustomarray,"lead",true,id);
                        KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                        if (customDataresult.getEntityList().size() > 0) {
                            crmLeadDAOObj.setCustomData((CrmLead) result,(CrmLeadCustomData) customDataresult.getEntityList().get(0));
                        }
//                    fieldManager.storeCustomFields(customfield,mode.toLowerCase(),true,leadid);
                }

                //Validate records
//                crmCommonDAOObj.validaterecorsingledHB(module, leadid, companyid, hibernateTemplate.getSessionFactory().getCurrentSession());
            }else if(mode.equalsIgnoreCase("opportunity")) {
                HashMap<String, Object> dataMap = (HashMap<String, Object>) arguments[1];
                String ownerid = dataMap.get("UsersByCreatedbyid").toString();
                String oppid = ((CrmOpportunity) result).getOppid();
                crmOpportunityDAOObj.setMainOppOwner(new String[]{oppid}, ownerid);
                if(dataMap.containsKey("CrmProduct") && dataMap.get("CrmProduct")!=null){
                    String productid = dataMap.get("CrmProduct").toString();
                    crmOpportunityDAOObj.saveOpportunityProducts(new String[]{oppid},productid.split(","));
                }
                JSONArray customfield = (JSONArray) arguments[7];
                if(customfield.length() > 0) {
                        HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                        customrequestParams.put("customarray", customfield);
                        customrequestParams.put("modulename", Constants.Crm_Opportunity_modulename);
                        customrequestParams.put("moduleprimarykey", Constants.Crm_Opportunityid);
                        customrequestParams.put("modulerecid", oppid);
                        customrequestParams.put("companyid", companyid);
                        customrequestParams.put("customdataclasspath", Constants.Crm_opportunity_custom_data_classpath);
                        KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                        if (customDataresult.getEntityList().size() > 0) {
                            crmOpportunityDAOObj.setCustomData((CrmOpportunity) result,(CrmOpportunityCustomData) customDataresult.getEntityList().get(0));
                        }
                }

                //Validate records
//                crmCommonDAOObj.validaterecorsingledHB(module, oppid, companyid, hibernateTemplate.getSessionFactory().getCurrentSession());
            }else if(mode.equalsIgnoreCase("account")) {
                HashMap<String, Object> dataMap = (HashMap<String, Object>) arguments[1];
                String ownerid = dataMap.get("UsersByCreatedbyid").toString();
                String accountid = ((CrmAccount) result).getAccountid();
                crmAccountDAOObj.setMainAccOwner(new String[]{accountid}, ownerid);
                if(dataMap.containsKey("CrmProduct") && dataMap.get("CrmProduct")!=null){
                    String productid = dataMap.get("CrmProduct").toString();
                    crmAccountDAOObj.saveAccountProducts(new String[]{accountid},productid.split(","));
                }

                JSONArray customfield = (JSONArray) arguments[7];
                if(customfield.length() > 0) {
                        HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                        customrequestParams.put("customarray", customfield);
                        customrequestParams.put("modulename", Constants.Crm_Account_modulename);
                        customrequestParams.put("moduleprimarykey", Constants.Crm_Accountid);
                        customrequestParams.put("modulerecid", accountid);
                        customrequestParams.put("companyid", companyid);
                        customrequestParams.put("customdataclasspath", Constants.Crm_account_custom_data_classpath);
                        KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                        if (customDataresult.getEntityList().size() > 0) {
                            crmAccountDAOObj.setCustomData((CrmAccount) result,(CrmAccountCustomData) customDataresult.getEntityList().get(0));
                        }
                }

                //Validate records
//                crmCommonDAOObj.validaterecorsingledHB(module, accountid, companyid, hibernateTemplate.getSessionFactory().getCurrentSession());
            }else if(mode.equalsIgnoreCase("contact")) {
                HashMap<String, Object> dataMap = (HashMap<String, Object>) arguments[1];
                String ownerid = dataMap.get("UsersByCreatedbyid").toString();
                String contactid = ((CrmContact) result).getContactid();
                crmContactDAOObj.setMainContactOwner(new String[]{contactid}, ownerid);
                JSONArray customfield = (JSONArray) arguments[7];
                if(customfield.length() > 0) {
                        HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                        customrequestParams.put("customarray", customfield);
                        customrequestParams.put("modulename", Constants.Crm_Contact_modulename);
                        customrequestParams.put("moduleprimarykey", Constants.Crm_Contactid);
                        customrequestParams.put("modulerecid", contactid);
                        customrequestParams.put("companyid", companyid);
                        customrequestParams.put("customdataclasspath", Constants.Crm_contact_custom_data_classpath);
                        KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
                        if (customDataresult.getEntityList().size() > 0) {
                            crmContactDAOObj.setCustomData((CrmContact) result,(CrmContactCustomData) customDataresult.getEntityList().get(0));
                        }
                }

                //Validate records
//                crmCommonDAOObj.validaterecorsingledHB(module, contactid, companyid, hibernateTemplate.getSessionFactory().getCurrentSession());
            }else if(mode.equalsIgnoreCase("Target")) {
                HashMap<String, Object> dataMap = (HashMap<String, Object>) arguments[1];
                TargetList targetList = (TargetList) arguments[6];

                String fname = "";
                if(dataMap.get("Firstname") != null) {
                    fname = dataMap.get("Firstname").toString();
                }
                if(dataMap.get("Lastname") != null) {
                    String lname = "";
                    if(!StringUtil.isNullOrEmpty(dataMap.get("Lastname").toString())) {
                        lname = dataMap.get("Lastname").toString();
                    }
                    if(StringUtil.isNullOrEmpty(fname)) {
                        fname += lname;
                    } else {
                        fname += " "+lname;
                    }
                }
                crmTargetDAOObj.saveTargetListTargets(((TargetModule) result).getId(),(String)dataMap.get("Email"), targetList, fname);

            }

        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw new DataInvalidateException("Failed to import records for "+mode+": "+ex.getMessage());
        }
    }

    private void BeforeGetRefModuleData(MethodInvocation mi) throws DataInvalidateException {
        Object arguments[] = mi.getArguments();
        String module = (String) arguments[1];
        try {
            HashMap<String, Object> requestParams = (HashMap<String, Object>) arguments[0];
            String companyid = requestParams.get(Constants.companyid).toString();
            ArrayList<String> filterNames = (ArrayList<String>) arguments[4];
            ArrayList<Object> filterValues = (ArrayList<Object>) arguments[5];
            if(module.equalsIgnoreCase(Constants.DefaultMasterItem)) {
                String configid = (String) arguments[3];
                if(requestParams.containsKey(Constants.defaultheader) && requestParams.get(Constants.defaultheader)!= null){
                    // handled case for Lead Source having conbined entries of Lead Source from master configuation + Campaign source
                    if(StringUtil.equal(requestParams.get(Constants.defaultheader).toString(), Constants.Lead_Source)) {
                        filterNames.add(Constants.INcrmCombomaster_masterid);
                        configid = "'"+configid +"','"+Constants.CRMCOMBOMASTERID_CAMPAIGNSOURCE+"'";
                    } else {
                        filterNames.add(Constants.crmCombomaster_masterid);
                    }
                } else {
                    filterNames.add(Constants.crmCombomaster_masterid);
                }
                filterValues.add(configid);
                filterValues.add(companyid);
                filterNames.add(Constants.company_companyID);
            }else if(module.equalsIgnoreCase(Constants.Crm_product_pojo)) {
                filterValues.add(companyid);
                filterNames.add(Constants.company_companyID);
                filterValues.add(0);
                filterNames.add(Constants.deleteflag);
                filterValues.add(1);
                filterNames.add(Constants.validflag);
            }else if(module.equalsIgnoreCase(Constants.Crm_account_pojo)) {
                filterValues.add(companyid);
                filterNames.add(Constants.company_companyID);
                filterValues.add(0);
                filterNames.add(Constants.deleteflag);
                filterValues.add(1);
                filterNames.add(Constants.validflag);
            }else if(module.equalsIgnoreCase(Constants.Crm_case_pojo)) {
                filterValues.add(companyid);
                filterNames.add(Constants.company_companyID);
                filterValues.add(0);
                filterNames.add(Constants.deleteflag);
                filterValues.add(1);
                filterNames.add(Constants.validflag);
            }else if(module.equalsIgnoreCase(Constants.Crm_contact_pojo)) {
                filterValues.add(companyid);
                filterNames.add(Constants.company_companyID);
                filterValues.add(0);
                filterNames.add(Constants.deleteflag);
                filterValues.add(1);
                filterNames.add(Constants.validflag);
            }else if(module.equalsIgnoreCase(Constants.Crm_lead_pojo)) {
                filterValues.add(companyid);
                filterNames.add(Constants.company_companyID);
                filterValues.add(0);
                filterNames.add(Constants.deleteflag);
                filterValues.add(1);
                filterNames.add(Constants.validflag);
            }else if(module.equalsIgnoreCase(Constants.Crm_opportunity_pojo)) {
                filterValues.add(companyid);
                filterNames.add(Constants.company_companyID);
                filterValues.add(0);
                filterNames.add(Constants.deleteflag);
                filterValues.add(1);
                filterNames.add(Constants.validflag);
            }
        }catch(Exception ex){
            logger.warn(ex.getMessage(), ex);
        }
    }

    private void AfterGetRefModuleData(MethodInvocation mi, Object result) throws DataInvalidateException {
        if(result!=null) {
            List masterList = (List) result;
            if(masterList.size()==0) {
                Object arguments[] = mi.getArguments();
                String module = (String) arguments[1];
                if (ImportHandler.isMasterTable(module)) { //Check for referencing to master
                    try {
                        HashMap<String, Object> requestParams = (HashMap<String, Object>) arguments[0];
                        if(requestParams.containsKey("companyid") && requestParams.containsKey("doAction") && requestParams.containsKey("masterPreference")) {
                            String companyid = requestParams.get("companyid").toString();
                            String doAction = requestParams.get("doAction").toString();
                            String pref = (String) requestParams.get("masterPreference"); //0:Skip Record, 1:Skip Column, 2:Add new
    //                        String addMissingMaster = (String) requestParams.get("addMissingMaster");
                            if (doAction.compareToIgnoreCase("import") == 0 && pref != null && pref.compareToIgnoreCase("2") == 0) {
                                String comboConfigid = (String) arguments[3];
                                ArrayList<Object> filterValues = (ArrayList<Object>) arguments[5];

                                if (module.equalsIgnoreCase("DefaultMasterItem") || module.equalsIgnoreCase("com.krawler.common.admin.DefaultMasterItem")) {
                                    String configid = (String) arguments[3];
                                    String masterName = filterValues.get(0) != null ? filterValues.get(0).toString() : "";
                                    masterName = masterName.length() > 50 ? masterName.substring(0, 50) : masterName; //Maxlength for value is 50 so truncate extra string
                                    HashMap<String, Object> addParams = new HashMap<String, Object>();
                                    addParams.put("companyid", companyid);
                                    addParams.put("name", masterName);
                                    addParams.put("configid", comboConfigid);

                                    KwlReturnObject kmsg = crmManagerDAOObj.addMasterData(addParams);

                                    JSONObject jResultObj = new JSONObject(kmsg.getEntityList().get(0).toString());
                                    if(jResultObj.has("data")) {
                                        if(jResultObj.getJSONObject("data").has("id"))
                                            masterList.add(jResultObj.getJSONObject("data").getString("id"));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn(e.getMessage(),e);
                    }
                }
            }
        }
    }

    private void AfterGetCustomComboID(MethodInvocation mi, Object result) throws DataInvalidateException {
        if(result!=null) {
            List masterList = (List) result;
            if(masterList.size()==0) {
                Object arguments[] = mi.getArguments();
                try {
                    HashMap<String, Object> requestParams = (HashMap<String, Object>) arguments[0];
                    if (requestParams.containsKey("doAction") && requestParams.containsKey("masterPreference")) {
//                    String module = (String) arguments[1];
//                    String companyid = requestParams.get("companyid").toString();
                        String doAction = requestParams.get("doAction").toString();
                        String pref = (String) requestParams.get("masterPreference"); //0:Skip Record, 1:Skip Column, 2:Add new
                        String addMissingMaster = (String) requestParams.get("addMissingMaster");
                        if (doAction.compareToIgnoreCase("import") == 0 && pref != null && pref.compareToIgnoreCase("2") == 0) {
                            ArrayList<Object> filterValues = (ArrayList<Object>) arguments[3];
                            String fieldid = filterValues.get(1).toString();
                            String combovalue = (String) filterValues.get(0);

                            HashMap<String, Object> comborequestParams = new HashMap<String, Object>();
                            comborequestParams.put("Fieldid", fieldid);
                            comborequestParams.put("Value", combovalue);
                            KwlReturnObject kmsg = fieldManagerDAOobj.insertfieldcombodata(comborequestParams);
//                        JSONObject jobj = new JSONObject(fieldManager.addCustomComboData(fieldid, combovalue));
                            if (kmsg.getEntityList().size() > 0) {
                                Iterator ite = kmsg.getEntityList().iterator();
                                FieldComboData fieldData = (FieldComboData) ite.next();
                                masterList.add(fieldData.getId());
                            }
                        }
                    }
                }catch(Exception e){
                    logger.warn(e.getMessage(),e);
                }
            }
        }
    }
}
