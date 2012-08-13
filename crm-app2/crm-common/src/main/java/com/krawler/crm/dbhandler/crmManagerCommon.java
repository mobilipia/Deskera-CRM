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
package com.krawler.crm.dbhandler; 
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.admin.FieldParams;
import com.krawler.common.admin.ProjectFeature;
import com.krawler.common.util.Constants;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmCombodata;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.customFieldMaster.fieldManagerController;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.sun.corba.se.impl.orbutil.closure.Constant;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

public class crmManagerCommon {

    public static JSONObject getCompanyPreferencesJSON(CompanyPreferences cmpPref, JSONObject obj)
			throws ServiceException {
		try {
            if(cmpPref != null){
                obj.put("campaign",cmpPref.isCampaign());
                obj.put("lead",cmpPref.isLead());
                obj.put("account",cmpPref.isAccount());
                obj.put("contact",cmpPref.isContact());
                obj.put("opportunity",cmpPref.isOpportunity());
                obj.put("cases",cmpPref.isCases());
                obj.put("product",cmpPref.isProduct());
                obj.put("activity",cmpPref.isActivity());
                obj.put("convertedlead",cmpPref.isEditconvertedlead());
                obj.put("leadtype",cmpPref.isDefaultleadtype());
                obj.put(Constants.SESSION_LEADROUTING,cmpPref.getLeadrouting());
            }else{
                obj.put("campaign",false);
                obj.put("lead",false);
                obj.put("account",false);
                obj.put("contact",false);
                obj.put("opportunity",false);
                obj.put("cases",false);
                obj.put("product",true);
                obj.put("activity",false);
                obj.put("convertedlead",false);
                obj.put("leadtype",false);
                obj.put(Constants.SESSION_LEADROUTING,0);
            }
		} catch (Exception e) {
			throw ServiceException.FAILURE("companyDetailsDAOImpl.getCompanyPreferences", e);
		}
        return obj;
	}

    public static String userNull(User cc) {
        String ret = null;
        if (cc == null) {
            ret = "";
        } else {
            ret = cc.getUserID();
        }
        return ret;
    }

    public static String dateNull(Date d) {
        String ret = "";
        if (d == null) {
            ret = "";
        } else {
            try {
//                Need to check from Ashutosh as hardcoded date format is used in function getDateMDYFormatter.
//                ret = authHandler.getDateMDYFormatter(request).format(d);
                ret = new SimpleDateFormat(Constants.MMMMdyyyy).format(d);
            } catch (Exception ex) {
                Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    public static String exportDateNull(Date d, DateFormat dateFormat) {
        String ret = "";
        if (d == null) {
            ret = "";
        } else {
            try {
                ret = dateFormat.format(d);
            } catch (Exception ex) {
                Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    public static String comboNull(CrmCombodata cc) {
        String ret = null;
        if (cc == null) {
            ret = "";
        } else {
            ret = cc.getValueid();
        }
        return ret;
    }

    public static String comboNotNullRaw(CrmCombodata cc) {
        String ret = null;
        if (cc == null) {
            ret = "";
        } else {
            ret = cc.getRawvalue();
        }
        return ret;
    }

    public static String comboNull(DefaultMasterItem cc) {
        String ret = null;
        if (cc == null) {
            ret = "";
        } else {
            ret = cc.getID();
        }
        return ret;
    }

    public static String comboNotNullRaw(DefaultMasterItem cc) {
        String ret = null;
        if (cc == null) {
            ret = "";
        } else {
            ret = cc.getValue();
        }
        return ret;
    }

    public static String comboValue(DefaultMasterItem df) {
        String status = "";
        if (df != null) {
//            if (!StringUtil.isNullOrEmpty(df.getAliasName())) {
//                status = df.getAliasName();
//            } else {
            status = df.getValue();
//            }
        }
        return status;
    }

    public static String dateRendererReport() {
        return "function(v) { if(!v) return v;return v.format(WtfGlobal.getOnlyDateFormat());}";
    }

    public static JSONObject insertNone() {
       JSONObject tmpOb = new JSONObject();
        try {
            tmpOb.put("id", "99");
            tmpOb.put("name", "-None-");
            tmpOb.put("hasAccess", true);
        } catch (JSONException ex) {
            Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tmpOb;
    }

    public static String moduleObjNull(Object invoker, String fieldName) {
        String ret = null;
        try{
            if (invoker == null) {
                JSONObject retJOBJ = insertNone();
                 ret = retJOBJ.get("id").toString();
            } else {
                Class cl = invoker.getClass();
                Class[] arguments = new Class[]{};
                java.lang.reflect.Method delMethod = cl.getMethod("getDeleteflag", arguments);
                int deleteFlag = (Integer) delMethod.invoke(invoker,new Object[]{});
                java.lang.reflect.Method validMethod = cl.getMethod("getValidflag", arguments);
                int validFlag = (Integer) validMethod.invoke(invoker,new Object[]{});
                if (deleteFlag == 1 || validFlag != 1) {
                    JSONObject retJOBJ = insertNone();
                    ret = retJOBJ.get("id").toString();
                } else {
                    java.lang.reflect.Method objMethod = cl.getMethod("get" + fieldName, arguments);
                    Object result1 = objMethod.invoke(invoker, new Object[]{});
                    ret = (String) result1;
                }
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public static String userObjNull(User userObj) {
        String ret = null;
        try{
            if (userObj == null) {
                JSONObject retJOBJ = insertNone();
                 ret = retJOBJ.get("id").toString();
            } else {
                int deleteFlag = userObj.getDeleteflag();
                if (deleteFlag == 1) {
                    JSONObject retJOBJ = insertNone();
                    ret = retJOBJ.get("id").toString();
                } else {
                    ret = userObj.getUserID();
                }
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(crmManagerCommon.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public static int getLeadRoutingOption(HttpServletRequest req)
			throws ServiceException,JSONException,SessionExpiredException {
		int permsion=0;
		try {
			JSONObject jsnObj = new JSONObject(sessionHandlerImpl.getCompanyPreference(req));
            permsion = jsnObj.getInt(Constants.SESSION_LEADROUTING);
		} catch (com.krawler.utils.json.base.JSONException e) {
			throw ServiceException.FAILURE("Auth.getLeadRoutingOption", e);
		}
		return permsion;
	}

    public static boolean  chkHeirarchyPerm(HttpServletRequest req, String moduleName)
			throws ServiceException,JSONException,SessionExpiredException {
		boolean permsion=false;
		try {
			JSONObject obj = new JSONObject();
            String cmppref = req.getSession().getAttribute(Constants.SESSION_COMPANY_PREF).toString();
//            JSONObject jsnObj = new JSONObject(cmppref.substring(1, cmppref.length()-1));
            JSONObject jsnObj = new JSONObject(cmppref);
            permsion = chkHeirarchyPermFromJson(jsnObj,moduleName,sessionHandlerImpl.getPerms(req, moduleName));
		} catch (com.krawler.utils.json.base.JSONException e) {
			throw ServiceException.FAILURE("Auth.chkHeirarchyPerm", e);
		}
		return permsion;
	}

    public static boolean chkHeirarchyPermFromJson(JSONObject jsnObj, String moduleName, Integer permissionCode)
			throws ServiceException,JSONException,SessionExpiredException {
		boolean permsion=false;
		try {
            if(StringUtil.equal(moduleName, Constants.Crm_Lead_modulename)){
                if((permissionCode & 128) == 128) {
                    //"View All" Permission Check... 2 to the power 7
                    permsion = true;
                } else
                    permsion = jsnObj.getBoolean("lead");
            } else if(StringUtil.equal(moduleName, Constants.Crm_Account_modulename)){
                if((permissionCode & 64) == 64) { // 2 to the power 6
                    permsion = true;
                } else
                    permsion = jsnObj.getBoolean("account");
            } else if(StringUtil.equal(moduleName, Constants.Crm_Contact_modulename)){
                if((permissionCode & 64) == 64) { // 2 to the power 6
                    permsion = true;
                } else
                    permsion = jsnObj.getBoolean("contact");
            } else if(StringUtil.equal(moduleName, Constants.Crm_Opportunity_modulename)){
                if((permissionCode & 32) == 32) {
                    permsion = true;
                } else
                    permsion = jsnObj.getBoolean("opportunity");
            } else if(StringUtil.equal(moduleName, "Cases") || StringUtil.equal(moduleName, Constants.Crm_Case_modulename)){// "Case" value come for fetched case owner combo request
                if((permissionCode & 32) == 32) {
                    permsion = true;
                } else
                    permsion = jsnObj.getBoolean("cases");
            } else if(StringUtil.equal(moduleName, Constants.Crm_Product_modulename)){
                if((permissionCode & 64) == 64) {
                    permsion = true;
                } else
                    permsion = jsnObj.getBoolean("product");
            } else if(StringUtil.equal(moduleName, Constants.CRM_ACTIVITY_MODULENAME)){
                if((permissionCode & 32) == 32) {
                    permsion = true;
                } else
                   permsion = jsnObj.getBoolean("activity");
            } else if(StringUtil.equal(moduleName, Constants.CRM_CAMPAIGN_MODULENAME)){
                if((permissionCode & 32) == 32) {
                    permsion = true;
                } else
                    permsion = jsnObj.getBoolean("campaign");
            } else if(StringUtil.equal(moduleName, Constants.CRM_TARGET_MODULENAME)){
                if((permissionCode & 64) == 64) { // 2 to the power 6
                    permsion = true;
                } else
                    permsion = false;
            }

		} catch (com.krawler.utils.json.base.JSONException e) {
			throw ServiceException.FAILURE("crmManagerCommon.chkHeirarchyPermFromJson", e);
		}
		return permsion;
	}

    public static String getComboDbName(String field) throws ServiceException {
        String dbName = "";
        try {

            if (field.equals("leadownerid") || field.equals("contactownerid") || field.equals("ownerid") || field.equals("accountownerid") || field.equals("oppownerid") || field.equals("caseownerid") || field.equals("campaignownerid")) {
                dbName = "c.usersByUserid.firstName";
            } else if (field.equals("productserviceid")) {
                dbName = "p.productId.productid";
            } else if (field.equals("productid") || field.equals("productnameid")) {
                dbName = "c.crmProduct.productid";
            } else if (field.equals("leadstatusid")) {
                dbName = "c.crmCombodataByLeadstatusid.ID";
            } else if (field.equals("ratingid")) {
                dbName = "c.crmCombodataByRatingid.ID";
            } else if (field.equals("leadsourceid")) {
                dbName = "c.crmCombodataByLeadsourceid.ID";
            } else if (field.equals("industryid")) {
                dbName = "c.crmCombodataByIndustryid.ID";
            } else if (field.equals("categoryid") ) {
                dbName = "c.crmCombodataByCategoryid.ID";
            } else if (field.equals("accounttypeid")) {
                dbName = "c.crmCombodataByAccounttypeid.ID";
            } else if (field.equals("accountnameid") || field.equals("relatedname")) {
                dbName = "c.crmAccount.accountid";
            } else if (field.equals("oppstageid")) {
                dbName = "c.crmCombodataByOppstageid.ID";
            } else if (field.equals("opptypeid")) {
                dbName = "c.crmCombodataByOpptypeid.ID";
            } else if (field.equals("oppregionid")) {
                dbName = "c.crmCombodataByRegionid.ID";
            } else if (field.equals("casestatusid")) {
                dbName = "c.crmCombodataByCasestatusid.ID";
            } else if (field.equals("casepriorityid")) {
                dbName = "c.crmCombodataByCasepriorityid.ID";
            } else if (field.equals("casetypeid")) {
                dbName = "c.crmCombodataByCasetypeid.ID";
            } else if (field.equals("contactnameid")) {
                dbName = "c.crmContact.contactid";
            } else if (field.equals("typeid")) {
                dbName = "c.crmActivityMaster.crmCombodataByTypeid.ID";
            } else if (field.equals("statusid")) {
                dbName = "c.crmActivityMaster.crmCombodataByStatusid.ID";
            } else if (field.equals("priorityid")) {
                dbName = "c.crmActivityMaster.crmCombodataByPriorityid.ID";
            } else if (field.equals("creatdon")) {
                dbName = "c.createdOn";
            }else if (field.equals("startdate")) {
                dbName = "c.crmActivityMaster.startDate";
            }

        } catch(Exception e) {
            throw ServiceException.FAILURE("crmManager.getComboDbName", e);
        }
        return dbName;
    }
    public static String getJoinQuery(HashMap<String, Object> requestParams)  {
        String JoinQuery = "";
        if(!requestParams.containsKey("field") || requestParams.get("field")==null){
            return JoinQuery;
        }
        String field = requestParams.get("field").toString();
        boolean iscustomcolumn = false;
        if(requestParams.get("xfield")!=null){
            field = requestParams.get("xfield").toString();
            iscustomcolumn = Boolean.parseBoolean(requestParams.get("iscustomcolumn").toString());
        }
        try {
            if (iscustomcolumn){
                int moduleid = Integer.parseInt(requestParams.get("moduleid").toString());
                String xtype = requestParams.get("xtype").toString();
                String moduleref = fieldManagerController.getmoduledataRefName(moduleid);
                JoinQuery =" left join  c."+moduleref;
                field = field.replaceFirst("C", "c");
                    if(xtype.equals("combo") || xtype.equals("select")) { // left join for combo fields to show values where combo value is null
                    JoinQuery = JoinQuery+".Ref"+field;
                }
                JoinQuery +=" ";
            }else {
                if (Constants.JoinMap.containsKey(field)) {
                    JoinQuery = " left join  " + Constants.JoinMap.get(field) + " ";
                }else{
                    JoinQuery = " ";
                }
            }

        } catch (Exception e) {
            return " ";
        }
        return JoinQuery;
    }
    public static String getFieldDbName(HashMap<String, Object> requestParams)  {
        String dbName = "";
        String field = requestParams.get("field").toString();
        boolean iscustomcolumn = false;
        if(requestParams.get("xfield")!=null){
            field = requestParams.get("xfield").toString();
            iscustomcolumn = Boolean.parseBoolean(requestParams.get("iscustomcolumn").toString());
        }
        try {
            if (iscustomcolumn){
                int moduleid = Integer.parseInt(requestParams.get("moduleid").toString());
                String xtype = requestParams.get("xtype").toString();
                String moduleref = fieldManagerController.getmoduledataRefName(moduleid);
                field = field.replaceFirst("C", "c");
                if(xtype.equals("combo") || xtype.equals("select")) {
                    field = "Ref"+field+".value";
                }
                dbName = "c."+moduleref+"."+field;
                if(xtype.equals("numberfield")) {
                    dbName = " "+dbName+"*1 "; //"ifnull(CONVERT("+dbName+",DECIMAL(64,4)),0) ";
                }else if(xtype.equals("datefield")) {
                	if(requestParams.containsKey("dateasnum"))
                		dbName = " "+dbName+"*1 ";
                	else
                		dbName = "ifnull(str_to_date("+dbName+",'%M %d,%Y'),0) ";
                } else if (xtype.equals(com.krawler.crm.utils.Constants.AUTONO)) {
                    String prefixReplace = requestParams.containsKey(Constants.CUSTOM_FIELD_PREFIX) ? requestParams.get(Constants.CUSTOM_FIELD_PREFIX).toString() :"";
                    String suffixReplace = requestParams.containsKey(Constants.CUSTOM_FIELD_SUFFIX) ? requestParams.get(Constants.CUSTOM_FIELD_SUFFIX).toString() :"";
                    String dbNameQuery = "";
                    if(!StringUtil.isNullOrEmpty(prefixReplace)) {
                        dbNameQuery = "replace("+dbName+",'"+prefixReplace+"','')";
                    }
                    if (!StringUtil.isNullOrEmpty(suffixReplace)) {
                        if(StringUtil.isNullOrEmpty(dbNameQuery)) {
                            dbNameQuery = "replace("+dbName+",'"+suffixReplace+"','')";
                        } else {
                            dbNameQuery = "replace("+dbNameQuery+",'"+suffixReplace+"','')";
                        }
                    }
                    dbName = " "+dbNameQuery+"*1 ";
                }

                
            }else {
                if (field.equals("leadownerid") || field.equals("contactownerid") || field.equals("ownerid") || field.equals("accountownerid") || field.equals("oppownerid") || field.equals("caseownerid") || field.equals("campaignownerid")) {
                    dbName = "c.usersByUserid.firstName";
                } else if (field.equals("productserviceid")) {
                    dbName = "p.productId.productid";
                } else if (field.equals("productid") || field.equals("productnameid")) {
                    dbName = "c.crmProduct.productid";
                } else if (field.equals("leadstatusid")) {
                    dbName = "c.crmCombodataByLeadstatusid.ID";
                } else if (field.equals("ratingid")) {
                    dbName = "c.crmCombodataByRatingid.ID";
                } else if (field.equals("leadsourceid")) {
                    dbName = "c.crmCombodataByLeadsourceid.ID";
                } else if (field.equals("industryid")) {
                    dbName = "c.crmCombodataByIndustryid.ID";
                } else if (field.equals("categoryid")) {
                    dbName = "c.crmCombodataByCategoryid.ID";
                } else if (field.equals("accounttypeid")) {
                    dbName = "c.crmCombodataByAccounttypeid.ID";
                } else if (field.equals("accountnameid") || field.equals("relatedname")) {
                    dbName = "c.crmAccount.accountid";
                } else if (field.equals("oppstageid")) {
                    dbName = "c.crmCombodataByOppstageid.ID";
                } else if (field.equals("opptypeid")) {
                    dbName = "c.crmCombodataByOpptypeid.ID";
                } else if (field.equals("oppregionid")) {
                    dbName = "c.crmCombodataByRegionid.ID";
                } else if (field.equals("casestatusid")) {
                    dbName = "c.crmCombodataByCasestatusid.ID";
                } else if (field.equals("casepriorityid")) {
                    dbName = "c.crmCombodataByCasepriorityid.ID";
                } else if (field.equals("casetypeid")) {
                    dbName = "c.crmCombodataByCasetypeid.ID";
                } else if (field.equals("contactnameid")) {
                    dbName = "c.crmContact.contactid";
                } else if (field.equals("typeid")) {
                    dbName = "c.crmActivityMaster.crmCombodataByTypeid.ID";
                } else if (field.equals("statusid")) {
                    dbName = "c.crmActivityMaster.crmCombodataByStatusid.ID";
                } else if (field.equals("priorityid")) {
                    dbName = "c.crmActivityMaster.crmCombodataByPriorityid.ID";
                } else if (field.equals("creatdon") || field.equals("createdon")) {
                    dbName = "c.createdOn";
                }else if (field.equals("updatedon")) {
                    dbName = "c.modifiedOn";
                } else if (field.equals("listname") || field.equals("templatename")) {
                    dbName = "c.name";
                } else if (field.equals("gstartdate")) {
                    dbName = "c.startdate";
                } else if (field.equals("genddate")) {
                    dbName = "c.enddate";
                }else if (field.equals("gassignedby")) {
                    dbName = "c.assignedby";
                }else if (field.equals("c.crmAccount.acacountname")) {
                    dbName = "c.assignedby";
                } else if (field.equals("c.crmCombodataByOppstageid.ID")) {
                    dbName = "c.crmCombodataByOppstageid.value";
                }else if (field.equals("c.crmCombodataByOpptypeid.ID")) {
                    dbName = "c.crmCombodataByOpptypeid.value";
                }else if (field.equals("c.crmCombodataByRegionid.ID")) {
                    dbName = "c.crmCombodataByRegionid.value";
                }else if (field.equals("c.crmCombodataByLeadsourceid.ID")) {
                    dbName = "c.crmCombodataByLeadsourceid.value";
                }else if (field.equals("ao.usersByUserid.userID")) {
                    dbName = "concat(ao.usersByUserid.firstName,' ',ao.usersByUserid.lastName)";
                }else if (field.equals("c.crmCombodataByAccounttypeid.ID")) {
                    dbName = "c.crmCombodataByAccounttypeid.value";
                }else if (field.equals("c.crmCombodataByIndustryid.ID")) {
                    dbName = "c.crmCombodataByIndustryid.value";
                }else if (field.equals("lo.usersByUserid.userID")) {
                   dbName = "concat(lo.usersByUserid.firstName,' ',lo.usersByUserid.lastName)";
                }else if (field.equals("c.crmCombodataByLeadstatusid.ID")) {
                   dbName = "c.crmCombodataByLeadstatusid.value";
                }else if (field.equals("c.crmCombodataByRatingid.ID")) {
                   dbName = "c.crmCombodataByRatingid.value";
                }else if (field.equals("co.usersByUserid.userID")) {
                   dbName = "concat(co.usersByUserid.firstName,' ',co.usersByUserid.lastName)";
                }else if (field.equals("c.usersByUserid.userID")) {
                   dbName = "concat(c.usersByUserid.firstName,' ',c.usersByUserid.lastName)";
                }else if (field.equals("c.crmAccount.accountid")) {
                   dbName = "c.crmAccount.accountname";
                }else if (field.equals("c.crmContact.contactid")) {
                   dbName = "concat(c.crmContact.firstname,' ',c.crmContact.lastname)";
                }else if (field.equals("c.crmCombodataByCasestatusid.ID")) {
                   dbName = "c.crmCombodataByCasestatusid.value";
                }else if (field.equals("c.crmCombodataByCasepriorityid.ID")) {
                   dbName = "c.crmCombodataByCasepriorityid.value";
                }else if (field.equals("c.crmCombodataByCasetypeid.ID")) {
                   dbName = "c.crmCombodataByCasetypeid.value";
                }else if (field.equals("c.crmCombodataByCategoryid.ID")) {
                   dbName = "c.crmCombodataByCategoryid.value";
                }else if (field.equals("c.crmCombodataByCampaigntypeid.ID")) {
                   dbName = "c.crmCombodataByCampaigntypeid.value";
                }else if (field.equals("c.crmCombodataByCampaignstatusid.ID")) {
                   dbName = "c.crmCombodataByCampaignstatusid.value";
                }else if (field.equals("c.assignedto.userID")) {
                   dbName = "concat(c.assignedto.firstName,' ',c.assignedto.lastName)";
                }else{
                    dbName = field;
                }
            } 

        } catch (Exception e) {
            return null;
        }
        return dbName;
    }
    
    public static void getAutoNoPrefixSuffix(fieldManagerDAO fieldManagerDAOobj, String companyid, String xfield, int moduleId,HashMap<String, Object> requestParams) {
        HashMap<String, Object> fieldParams = new HashMap<String, Object>();
        fieldParams.put(Constants.filter_names, Arrays.asList("companyid","moduleid","fieldtype","colnum"));
        fieldParams.put(Constants.filter_values, Arrays.asList(companyid,moduleId,Constants.CUSTOM_FIELD_AUTONUMBER,Integer.parseInt(xfield.replace(Constants.Custom_Column_Prefix, ""))));
        KwlReturnObject result = fieldManagerDAOobj.getFieldParams(fieldParams);
        List lst = result.getEntityList();
        if(lst!=null && lst.size() > 0) {
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                FieldParams tmpcontyp = (FieldParams) ite.next();
                requestParams.put(com.krawler.common.util.Constants.CUSTOM_FIELD_PREFIX, tmpcontyp.getPrefix());
                requestParams.put(com.krawler.common.util.Constants.CUSTOM_FIELD_SUFFIX, tmpcontyp.getSuffix());
            }
        }
    }
    
    public static boolean  hasViewProjPerm(HttpServletRequest request)
			throws ServiceException {
		boolean hasViewProjPerm=false;
		try {
            if(request.getSession().getAttribute("viewProject") != null)
                hasViewProjPerm =(Boolean)request.getSession().getAttribute("viewProject");
		} catch (Exception e) {
			throw ServiceException.FAILURE("crmManagerCommon.hasViewProjPerm", e);
		}
		return hasViewProjPerm;
	}
    public static boolean  hasCreateProjPerm(HttpServletRequest request)
			throws ServiceException {
		boolean hasCreateProjPerm=false;
		try {
            if(request.getSession().getAttribute("createProject") != null)
                hasCreateProjPerm =(Boolean)request.getSession().getAttribute("createProject");
		} catch (Exception e) {
			throw ServiceException.FAILURE("crmManagerCommon.hasCreateProjPerm", e);
		}
		return hasCreateProjPerm;
	}    

    public static boolean  hasSyncAccountingPerm(HttpServletRequest request)
			throws ServiceException {
		boolean syncAccounting=false;
		try {
            if(request.getSession().getAttribute("syncAccounting") != null)
                syncAccounting =(Boolean)request.getSession().getAttribute("syncAccounting");
		} catch (Exception e) {
			throw ServiceException.FAILURE("crmManagerCommon.hasSyncAccountingPerm", e);
		}
		return syncAccounting;
	}
    
    public static List getHeaderName(crmCommonDAO crmCommonDAOObj,String modName) throws ServiceException {
        ArrayList filter_params = new ArrayList();
        ArrayList filter_names = new ArrayList();
        filter_names.add("c.moduleName");
        filter_params.add(modName);
        filter_names.add("c.customflag");
        filter_params.add(false);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        return crmCommonDAOObj.getDefaultColumnHeader(requestParams).getEntityList();
    }
    public static String getNewColumnHeader(crmCommonDAO crmCommonDAOObj,String defaultHeader,String modName,String companyid) throws ServiceException {
        ArrayList filter_params = new ArrayList();
        ArrayList filter_names = new ArrayList();

        filter_names.add("c.defaultheader.moduleName");
        filter_params.add(modName);

        filter_names.add("c.defaultheader.defaultHeader");
        filter_params.add(defaultHeader);

        filter_names.add("c.company.companyID");
        filter_params.add(companyid);

        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        String newHeader="";
        Iterator ite1= crmCommonDAOObj.getColumnHeader(requestParams).getEntityList().iterator();
        if(ite1.hasNext()){
            ColumnHeader obj1 = (ColumnHeader) ite1.next();
            if(!StringUtil.isNullOrEmpty(obj1.getNewHeader())){
                newHeader=obj1.getNewHeader();
            }
        }
        return newHeader;
    }

}
