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
package com.krawler.customFieldMaster;

import com.krawler.common.admin.FieldParams;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.HashMap;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.utils.json.base.JSONArray;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


/**
 *
 * @author krawler
 */
public class fieldDataManager {

    private fieldDataManagerDAO fieldDataManagerDAOobj;
    private fieldManagerDAO fieldManagerDAOobj;

    public void setFieldDataManagerDAO(fieldDataManagerDAO fieldDataManagerDAOobj) {
        this.fieldDataManagerDAOobj = fieldDataManagerDAOobj;
    }

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }
    private Integer Isreferencefield(int fieldtype){
        if(fieldtype==4 || fieldtype==8)
            return 0;
        if(fieldtype==7)
            return 1;
        if(fieldtype==3)
            return -3;
        return -1;
    }
    public String getMultiSelectColData(String id) {
        String data = "";
        if(!StringUtil.isNullOrEmpty(id) && id.length() > 1){
            String[] mids = id.split(Constants.Custom_Column_Sep);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            for(int i=0;i<mids.length;i++){
                requestParams.clear();
                requestParams.put("dataid", mids[i]);
                data = data + fieldManagerDAOobj.getFieldComboDatadata(requestParams)+",";
            }
            if(!StringUtil.isNullOrEmpty(data) && data.length() > 1){
                data = data.substring(0,data.length()-1);
            }
        }
        return data;
    }
    public HashMap<String, Integer> getFieldParamsMap(HashMap<String, Object> requestParams) {
        // Following function is used to build Map of columns and Fields for specified modules which will be used to fetch json for each record from custom data table
        KwlReturnObject result = null;
        Boolean isexport = (Boolean) requestParams.get("isexport");
        requestParams.remove("isexport");
        HashMap<String, Integer> FieldMap = new HashMap<String, Integer>();
        result = fieldManagerDAOobj.getFieldParams(requestParams);
        List lst = result.getEntityList();
        Iterator ite = lst.iterator();
        while (ite.hasNext()) {
            FieldParams tmpcontyp = (FieldParams) ite.next();
            Integer isref = Isreferencefield(tmpcontyp.getFieldtype());
                FieldMap.put(tmpcontyp.getFieldname(), tmpcontyp.getColnum());
                FieldMap.put(tmpcontyp.getFieldname()+tmpcontyp.getColnum(), isref);
        }
        return FieldMap;
    }

    public HashMap<String, Integer> getFieldParamsMap1(HashMap<String, Object> requestParams, HashMap<String, String> replaceFieldMap) {
        // Following function is used to build Map of columns and Fields for specified modules which will be used to fetch json for each record from custom data table
        KwlReturnObject result = null;
        Boolean isexport = (Boolean) requestParams.get("isexport");
        requestParams.remove("isexport");
        HashMap<String, Integer> FieldMap = new HashMap<String, Integer>();
        result = fieldManagerDAOobj.getFieldParams(requestParams);
        List lst = result.getEntityList();
        Iterator ite = lst.iterator();
        while (ite.hasNext()) {
            FieldParams tmpcontyp = (FieldParams) ite.next();
            Integer isref = Isreferencefield(tmpcontyp.getFieldtype());
                FieldMap.put(tmpcontyp.getFieldname(), tmpcontyp.getColnum());
                FieldMap.put(tmpcontyp.getFieldname()+"#"+tmpcontyp.getColnum(), isref);// added '#' while creating map collection for custom fields.
                                                                                // Without this change, it creates problem if two custom columns having name like XYZ and XYZ1
                replaceFieldMap.put(tmpcontyp.getFieldname(), "custom_"+tmpcontyp.getId());
        }
        return FieldMap;
    }

    public KwlReturnObject setCustomData(HashMap<String, Object> customrequestParams) throws JSONException, ServiceException {
        KwlReturnObject result = null;
        boolean atleatonefield = false;
        JSONArray jarray = (JSONArray) customrequestParams.get("customarray");
        boolean allowautonomap = customrequestParams.containsKey("allowautonomap") ? true : false;
        String modulename= (String) customrequestParams.get("modulename");
        String moduleprimarykey= (String) customrequestParams.get("moduleprimarykey");
        String modulerecid=(String) customrequestParams.get("modulerecid");
        String customdataclasspath=(String) customrequestParams.get("customdataclasspath");
        String companyid =(String) customrequestParams.get("companyid");

        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put(modulename, modulerecid); 
        requestParams.put(moduleprimarykey, modulerecid);
        requestParams.put("Company", companyid);

        for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jobj = jarray.getJSONObject(i);
                    if(jobj.has(Constants.Crm_custom_field)){
                        String fieldname = jobj.getString(Constants.Crm_custom_field);
                        String fielddbname = jobj.getString(fieldname);
                        String fieldValue = jobj.getString(fielddbname);
                        atleatonefield = true;
                        fielddbname = fielddbname.replace("c", "C");
                        Integer xtype = Integer.parseInt(jobj.getString("xtype"));
                        if(xtype==9 && !allowautonomap) continue; // if autonumber then continue;
                        if(!StringUtil.isNullOrEmpty(fieldValue) && !StringUtil.isNullOrEmpty(fieldValue.trim()) && !fieldValue.equalsIgnoreCase(Constants.field_data_undefined)){
                            requestParams.put(fielddbname, fieldValue);
                            if(xtype==7){
                                    String reffielddbname = jobj.getString("refcolumn_name");
                                    if(!StringUtil.isNullOrEmpty(reffielddbname)){
                                        requestParams.put(reffielddbname, fieldValue.split(Constants.Custom_Column_Sep)[0]);
                                    }
                            }
                        }else{
                            if(xtype==7 || xtype==8 || xtype==4){
                                requestParams.put(fielddbname,null);
                                if(xtype==7){
                                    String reffielddbname = jobj.getString("refcolumn_name");
                                    requestParams.put(reffielddbname,null);
                                }
                            }
                            else
                                requestParams.put(fielddbname,"");
                        }
                    }
        }
        if(atleatonefield){
            requestParams.put("customdataclasspath", customdataclasspath);
            requestParams.put("moduleprimarykey", moduleprimarykey);
            result = fieldDataManagerDAOobj.setCustomData(requestParams);
        }
        
        return result;
    }

    public JSONArray setAutoNumberCustomData(HashMap<String, Object> customrequestParams, List<FieldParams> AutoNoFieldMap) throws JSONException, ServiceException {
        JSONArray customAutoNofieldData = new JSONArray();

        String modulename = (String) customrequestParams.get("modulename");
        String moduleprimarykey = (String) customrequestParams.get("moduleprimarykey");
        String modulerecid = (String) customrequestParams.get("modulerecid");
        String customdataclasspath = (String) customrequestParams.get("customdataclasspath");
        String companyid = (String) customrequestParams.get("companyid");

        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put(modulename, modulerecid);
        requestParams.put(moduleprimarykey, modulerecid);
        requestParams.put("Company", companyid);

        for (FieldParams fieldparamsobj1 : AutoNoFieldMap) {
            String fielddbname = Constants.Custom_Column_Prefix + fieldparamsobj1.getColnum();
            String prefix = fieldparamsobj1.getPrefix();
            String suffix = fieldparamsobj1.getSuffix();
            String maxNo = fieldManagerDAOobj.getMaxAutoNumber(fielddbname.toLowerCase(), customdataclasspath, companyid, prefix, suffix);
            if (StringUtil.isNullOrEmpty(maxNo)) {
                maxNo = getNextAutoNo(prefix, fieldparamsobj1.getStartingnumber(), suffix);
            } else {
                maxNo = getNextAutoNo(prefix, Integer.parseInt(maxNo.replace(prefix, "").replace(suffix, "")) + 1, suffix);
            }
            JSONObject returnJSON = new JSONObject();
            returnJSON.put(fieldparamsobj1.getFieldname(), maxNo);
            customAutoNofieldData.put(returnJSON);
            requestParams.put(fielddbname, maxNo);
        }
        requestParams.put("customdataclasspath", customdataclasspath);
        requestParams.put("moduleprimarykey", moduleprimarykey);
        fieldDataManagerDAOobj.setCustomData(requestParams);

        return customAutoNofieldData;
    }

    public static String getNextAutoNo(String prefix, Integer No, String suffix) {
        return prefix.concat(No.toString()).concat(suffix);
    }
    public KwlReturnObject setcustomdata(JSONArray jarray,Integer moduleid,String modulerecid,String companyid)throws Exception{
        HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
        customrequestParams.put("customarray", jarray);
        customrequestParams.put("modulename",fieldManagerController.getModuleName(moduleid));
        customrequestParams.put("moduleprimarykey", getPrimarycolumn(moduleid));
        customrequestParams.put("modulerecid", modulerecid);
        customrequestParams.put("companyid", companyid);
        customrequestParams.put("customdataclasspath", getmoduledataTableName(moduleid));
        KwlReturnObject result = setCustomData(customrequestParams);
        return result;
    }

    public KwlReturnObject setcustomdata(JSONArray jarray,Integer moduleid,String modulerecid,String companyid, boolean allowautonomap)throws Exception{
        HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
        customrequestParams.put("customarray", jarray);
        customrequestParams.put("modulename",fieldManagerController.getModuleName(moduleid));
        customrequestParams.put("moduleprimarykey", getPrimarycolumn(moduleid));
        customrequestParams.put("modulerecid", modulerecid);
        customrequestParams.put("companyid", companyid);
        customrequestParams.put("customdataclasspath", getmoduledataTableName(moduleid));
        customrequestParams.put("allowautonomap", allowautonomap);
        KwlReturnObject result = setCustomData(customrequestParams);
        return result;
    }
    public static String getmoduledataTableName(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.Crm_account_custom_data_classpath;
                break;
            case 2:
                module = Constants.Crm_lead_custom_data_classpath;
                break;
            case 3:
                module = Constants.Crm_case_custom_data_classpath;
                break;
            case 4:
                module = Constants.Crm_product_custom_data_classpath;
                break;
            case 5:
                module = Constants.Crm_opportunity_custom_data_classpath;
                break;
            case 6:
                module = Constants.Crm_contact_custom_data_classpath;
                break;
        }
        return module;
    }
    public static String getPrimarycolumn(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.Crm_Accountid;
                break;
            case 2:
                module = Constants.Crm_Leadid;
                break;
            case 3:
                module = Constants.Crm_Caseid;
                break;
            case 4:
                module = Constants.Crm_Productid;
                break;
            case 5:
                module = Constants.Crm_Opportunityid;
                break;
            case 6:
                module = Constants.Crm_Contactid;
                break;
        }
        return module;
    }
    
    public JSONObject applyColumnFormulae(String companyid, String currencyid, JSONObject tmpObj, String moduleid, String modName) throws ServiceException, JSONException {
            String fieldname = "";
        try {
            /*Some tested formulaes -
            1. revenue+price - All module fields
            2. ((revenue+price)*NumField1)+NumField1 - Module + custome fields
            3. NumField1/NumField3 - All custom fields
            4. (NumField1+NumField3)+((#~100#+#250#)*#3#) - custom fields + constants */

            String operatorRegex = "[\\+\\-\\*\\/]"; // Regex to get find the operators in the formulae.
            String bracketRegex = "[\\(\\)]"; // Regex to get find the operators in the formulae.
            String numberRegex ="^\\#\\~?([0-9]*|\\d*\\.\\d{1}?\\d*)\\#$";
            /**For constant numbers
            1. Use format : #no. value#
            2. Negative Constants : #~ No. Value#
            3. #(No. Value)# : will not work. Instead use: (#No. Value#)
            */
            String operandArr[] = null;
            String calStr = "";
            String Hql = "select formulae,fieldname from CustomColumnFormulae where companyid.companyID=? and moduleid=? ";
            List list = fieldDataManagerDAOobj.executeQuery(Hql, new Object[]{companyid, moduleid});
            Iterator ite = list.iterator();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                String formulae = (String) row[0];
                fieldname = (String) row[1];

                Object invoker = null;
                String primaryKey = "";
                java.lang.reflect.Method objMethod;
                calStr = formulae.replaceAll(operatorRegex, ",");
                calStr = calStr.replaceAll(bracketRegex, "");
                operandArr = calStr.split(",");
                Class cl = Class.forName("com.krawler.crm.database.tables.Crm"+modName+"");
                if (modName.equals("Lead")) {
                    primaryKey = "leadid";
                } else if (modName.equals("Contact")) {
                    primaryKey = "contactid";
                } else if (modName.equals("Product")) {
                    primaryKey = "productid";
                } else if (modName.equals("Account")) {
                    primaryKey = "accountid";
                } else if (modName.equals("Opportunity")) {
                    primaryKey = "oppid";
                } else if (modName.equals("Case")) {
                    primaryKey = "caseid";
                }
                invoker = fieldDataManagerDAOobj.get(cl, tmpObj.getString(primaryKey));
                if (invoker != null) {
                    for (int i = 0; i < operandArr.length; i++) {
                        String operand = "";
                        try{ //For module columns - Value is fetched from database.
                            String methodStr = operandArr[i].substring(0, 1).toUpperCase() + operandArr[i].substring(1).toLowerCase(); // Make first letter of operand capital.
                            objMethod = cl.getMethod("get" + methodStr + ""); // Gets the value of the operand
                            operand = (String) objMethod.invoke(invoker);
                        } catch(NoSuchMethodException ex) {
                            if (operandArr[i].matches(numberRegex)) {// For constant numbers in the format of #no. value#
                                operand = operandArr[i].substring(1, operandArr[i].length()-1);
                                if(operand.substring(0, 1).equals("~")) {
                                    operand = operand.replace("~", "-");
                                }
                            } else {
                                //For custom columns - taking column value from already created json.
                                //This will not work if $ or something like that appended in json value of custom column.
                                if(tmpObj.has(operandArr[i])){
                                    if (StringUtil.isNullOrEmpty(tmpObj.getString(operandArr[i]))) {
                                        operand = "0";
                                    } else { // this is to find numbers from strings
                                        Pattern numberPattern = Pattern.compile("(\\d+)(((.+)(\\d+))*)");
                                        Matcher m = numberPattern.matcher(tmpObj.getString(operandArr[i]));
                                        if (m.find()){
                                          operand =m.group(0);
                                        }else
                                        operand = tmpObj.getString(operandArr[i]);
                                    }
                                }else{
                                    operand = "0";
                                }
                            }
                        }
                        if (StringUtil.isNullOrEmpty(operand)) {
                            operand = "0";
                        } else if(operand.substring(0, 1).equals("-")) {
                            operand = "("+operand+")";
                        }
                        formulae = formulae.replaceAll(operandArr[i].toString(), operand); //Put the value in the formulae.
                    }
                }
                try{
                    ScriptEngineManager mgr = new ScriptEngineManager();
                    ScriptEngine engine = mgr.getEngineByName("js");
                    double ans = (Double) engine.eval(formulae);
                    String custom_ans = !String.valueOf(ans).equals("NaN") ? String.valueOf(ans) : "";
                    tmpObj.put(fieldname, currencyRender(custom_ans, currencyid));
                } catch(ScriptException e) {
                    System.out.println(formulae);
                    tmpObj.put(fieldname, "");
                }
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (SecurityException ex) {
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (SessionExpiredException ex) {
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
        } catch (JSONException ex) {
            ex.printStackTrace();
            tmpObj.put(fieldname, "");
        } catch (Exception ex) {
            ex.printStackTrace();
            tmpObj.put(fieldname, "");
        } finally {
        }
        return tmpObj;
    }
    
    public JSONObject applyColumnFormulae(List lst, String currencyid, JSONObject tmpObj, String modName) throws ServiceException, JSONException {
        String fieldname = "";
    try {
        /*Some tested formulaes -
        1. revenue+price - All module fields
        2. ((revenue+price)*NumField1)+NumField1 - Module + custome fields
        3. NumField1/NumField3 - All custom fields
        4. (NumField1+NumField3)+((#~100#+#250#)*#3#) - custom fields + constants */

        String operatorRegex = "[\\+\\-\\*\\/]"; // Regex to get find the operators in the formulae.
        String bracketRegex = "[\\(\\)]"; // Regex to get find the operators in the formulae.
        String numberRegex ="^\\#\\~?([0-9]*|\\d*\\.\\d{1}?\\d*)\\#$";
        /**For constant numbers
        1. Use format : #no. value#
        2. Negative Constants : #~ No. Value#
        3. #(No. Value)# : will not work. Instead use: (#No. Value#)
        */
        String operandArr[] = null;
        String calStr = "";        
        Iterator ite = lst.iterator();
        while (ite.hasNext()) {
            Object[] row = (Object[]) ite.next();
            String formulae = (String) row[0];
            fieldname = (String) row[1];

            Object invoker = null;
            String primaryKey = "";
            java.lang.reflect.Method objMethod;
            calStr = formulae.replaceAll(operatorRegex, ",");
            calStr = calStr.replaceAll(bracketRegex, "");
            operandArr = calStr.split(",");
            Class cl = Class.forName("com.krawler.crm.database.tables.Crm"+modName+"");
            if (modName.equals("Lead")) {
                primaryKey = "leadid";
            } else if (modName.equals("Contact")) {
                primaryKey = "contactid";
            } else if (modName.equals("Product")) {
                primaryKey = "productid";
            } else if (modName.equals("Account")) {
                primaryKey = "accountid";
            } else if (modName.equals("Opportunity")) {
                primaryKey = "oppid";
            } else if (modName.equals("Case")) {
                primaryKey = "caseid";
            }
            invoker = fieldDataManagerDAOobj.get(cl, tmpObj.getString(primaryKey));
            if (invoker != null) {
                for (int i = 0; i < operandArr.length; i++) {
                    String operand = "";
                    try{ //For module columns - Value is fetched from database.
                        String methodStr = operandArr[i].substring(0, 1).toUpperCase() + operandArr[i].substring(1).toLowerCase(); // Make first letter of operand capital.
                        objMethod = cl.getMethod("get" + methodStr + ""); // Gets the value of the operand
                        operand = (String) objMethod.invoke(invoker);
                    } catch(NoSuchMethodException ex) {
                        if (operandArr[i].matches(numberRegex)) {// For constant numbers in the format of #no. value#
                            operand = operandArr[i].substring(1, operandArr[i].length()-1);
                            if(operand.substring(0, 1).equals("~")) {
                                operand = operand.replace("~", "-");
                            }
                        } else {
                            //For custom columns - taking column value from already created json.
                            //This will not work if $ or something like that appended in json value of custom column.
                            if(tmpObj.has(operandArr[i])){
                                if (StringUtil.isNullOrEmpty(tmpObj.getString(operandArr[i]))) {
                                    operand = "0";
                                } else { // this is to find numbers from strings
                                    Pattern numberPattern = Pattern.compile("(\\d+)(((.+)(\\d+))*)");
                                    Matcher m = numberPattern.matcher(tmpObj.getString(operandArr[i]));
                                    if (m.find()){
                                      operand =m.group(0);
                                    }else
                                    operand = tmpObj.getString(operandArr[i]);
                                }
                            }else{
                                operand = "0";
                            }
                        }
                    }
                    if (StringUtil.isNullOrEmpty(operand)) {
                        operand = "0";
                    } else if(operand.substring(0, 1).equals("-")) {
                        operand = "("+operand+")";
                    }
                    formulae = formulae.replaceAll(operandArr[i].toString(), operand); //Put the value in the formulae.
                }
            }
            try{
                ScriptEngineManager mgr = new ScriptEngineManager();
                ScriptEngine engine = mgr.getEngineByName("js");
                double ans = (Double) engine.eval(formulae);
                String custom_ans = !String.valueOf(ans).equals("NaN") ? String.valueOf(ans) : "";
                tmpObj.put(fieldname, currencyRender(custom_ans, currencyid));
            } catch(ScriptException e) {
                System.out.println(formulae);
                tmpObj.put(fieldname, "");
            }
        }
    } catch (IllegalAccessException ex) {
        ex.printStackTrace();
        throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
    } catch (IllegalArgumentException ex) {
        ex.printStackTrace();
        throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
    } catch (InvocationTargetException ex) {
        ex.printStackTrace();
        throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
    } catch (ClassNotFoundException ex) {
        ex.printStackTrace();
        throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
    } catch (SecurityException ex) {
        ex.printStackTrace();
        throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
    } catch (SessionExpiredException ex) {
        ex.printStackTrace();
        throw ServiceException.FAILURE("fieldManager.applyColumnFormulae : " + ex.getMessage(), ex);
    } catch (JSONException ex) {
        ex.printStackTrace();
        tmpObj.put(fieldname, "");
    } catch (Exception ex) {
        ex.printStackTrace();
        tmpObj.put(fieldname, "");
    } finally {
    }
    return tmpObj;
}

    public String currencyRender(String currency, String currencyID) throws SessionExpiredException {
        if (!StringUtil.isNullOrEmpty(currency)) {
            KWLCurrency cur = (KWLCurrency) fieldDataManagerDAOobj.get(KWLCurrency.class, currencyID);
            String symbol = cur.getHtmlcode();
            char temp = (char) Integer.parseInt(symbol, 16);
            symbol = Character.toString(temp);
            float v = 0;
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            if (currency.equals("")) {
                return symbol;
            }
            v = Float.parseFloat(currency);
            String fmt = decimalFormat.format(v);
            fmt = symbol + " " + fmt;
            return fmt;
        } else {
            return "";
        }
    }

    public List getCustomColumnFormulae(Object[] params)
    {
        return fieldDataManagerDAOobj.getCustomColumnFormulae(params);
    }
}
