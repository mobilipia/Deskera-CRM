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

import com.krawler.common.admin.CustomColumnFormulae;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.FieldComboData;
import com.krawler.common.admin.FieldParams;
import com.krawler.common.admin.Modules;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.JSONArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Transaction;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author krawler
 */
public class fieldManagerController extends MultiActionController {

    private String successView;
    private HibernateTransactionManager txnManager;
    private fieldManagerDAO fieldManagerDAOobj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private FieldManagerService fieldManagerService;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;

    

    /**
     * @return the fieldManagerService
     */
    public FieldManagerService getFieldManagerService()
    {
        return fieldManagerService;
    }

    /**
     * @param FieldManagerService the fieldManagerService to set
     */
    public void setFieldManagerService(FieldManagerService fieldManagerService)
    {
        this.fieldManagerService = fieldManagerService;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

     public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
     
     public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
         this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
     }

 

    private HashMap<String, Object> getcolumn_number(String companyid,Integer moduleid,Integer fieldtype, int moduleflag) throws SessionExpiredException, JSONException {
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        boolean Notreachedlimit = true;
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        try{
                Integer custom_column_start=0,Custom_Column_limit=0;

                switch(fieldtype){
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                    case 9://  auto number
                    case 6:
                        custom_column_start = Constants.Custom_Column_Normal_start;
                        Custom_Column_limit=Constants.Custom_Column_Normal_limit;
                        requestParams.put("filter_names", Arrays.asList("companyid","moduleid","INfieldtype",">colnum","<=colnum"));
                        requestParams.put("filter_values", Arrays.asList(companyid,moduleid,"1,2,3,5,6,7,9",custom_column_start,custom_column_start+Custom_Column_limit));
                        break;
                    case 4:
                    case 7:
                        custom_column_start = Constants.Custom_Column_Combo_start;
                        Custom_Column_limit=Constants.Custom_Column_Combo_limit;
                        requestParams.put("filter_names", Arrays.asList("companyid","moduleid","INfieldtype"));
                        requestParams.put("filter_values", Arrays.asList(companyid,moduleid,"4,7"));
                        break;
                    case 8:
                        if(moduleflag == 1) {
                            custom_column_start = Constants.Custom_Column_User_start;
                            Custom_Column_limit=Constants.Custom_Column_User_limit;
                        } else {
                            custom_column_start = Constants.Custom_Column_Master_start;
                            Custom_Column_limit=Constants.Custom_Column_Master_limit;
                        }

                        requestParams.put("filter_names", Arrays.asList("companyid","moduleid","fieldtype","moduleflag"));
                        requestParams.put("filter_values", Arrays.asList(companyid,moduleid,fieldtype,moduleflag));
                        break;
                }
                        Integer colcount = 1;

                        result = fieldManagerDAOobj.getFieldParams(requestParams);
                        List lst = result.getEntityList();
                        colcount = lst.size();
                        if (colcount == Custom_Column_limit) {
                            jobj.put("success", "msg");
                            jobj.put("title", "Alert");
                            jobj.put("msg", "Cannot add new field. Maximum custom field limit reached.");
                            jobj.put("moduleName",getModuleName(moduleid));
                            Notreachedlimit = false;
                        }
                        if (Notreachedlimit) {
                                Iterator ite = lst.iterator();
                                int[] countchk = new int[Custom_Column_limit+1];
                                while (ite.hasNext()) {
                                    FieldParams tmpcontyp = (FieldParams) ite.next();

                              // check added to refer to reference column in case of multiselect combo field instead of refering to column number field
                                    if((fieldtype==4 || fieldtype==7) && tmpcontyp.getFieldtype()==7){//FieldComboData as drop-down.  Start from col1
                                        countchk[tmpcontyp.getRefcolnum()-custom_column_start] = 1;
                                    }else{
                                        countchk[tmpcontyp.getColnum()-custom_column_start] = 1;
                                    }
                                }
                                for (int i = 1; i <= Custom_Column_limit; i++) {
                                    if (countchk[i] == 0) {
                                        colcount = i;
                                        break;
                                    }
                                }
                    }
               requestParams.put("response", jobj);
               requestParams.put("column_number", colcount+custom_column_start);
               requestParams.put("success", Notreachedlimit?"True":"false");
        }catch(Exception e) {
            e.printStackTrace();
        }
       return requestParams;
    }
    public ModelAndView getFieldParams(HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jresult = new JSONObject();
        String module = request.getParameter(Constants.moduleid);
        String[] moduleidarray = request.getParameterValues(Constants.moduleidarray);
        String commaSepratedModuleids = "";
        if(moduleidarray!=null){
            for(int i=0;i<moduleidarray.length;i++){
                if(!StringUtil.isNullOrEmpty(moduleidarray[i])){
                    commaSepratedModuleids += moduleidarray[i]+",";
                }
            }
            if(moduleidarray.length > 1){
                commaSepratedModuleids = commaSepratedModuleids.substring(0,commaSepratedModuleids.length()-1);
            }
        }
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            Integer colcount = 1;
            if( StringUtil.isNullOrEmpty(commaSepratedModuleids) && StringUtil.isNullOrEmpty(module)){
                requestParams.put(Constants.filter_names, Arrays.asList(Constants.companyid));
                requestParams.put(Constants.filter_values, Arrays.asList(companyid));
            }else if(StringUtil.isNullOrEmpty(commaSepratedModuleids)){
                Integer moduleid = Integer.parseInt(module);
                requestParams.put(Constants.filter_names, Arrays.asList(Constants.companyid, Constants.moduleid));
                requestParams.put(Constants.filter_values, Arrays.asList(companyid, moduleid));
            }else{
                requestParams.put(Constants.filter_names, Arrays.asList(Constants.companyid, Constants.INmoduleid));
                requestParams.put(Constants.filter_values, Arrays.asList(companyid, commaSepratedModuleids));
            }
            
            result = fieldManagerDAOobj.getFieldParams(requestParams);
            List lst = result.getEntityList();
            colcount = lst.size();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                 FieldParams tmpcontyp = (FieldParams) ite.next();
                 JSONObject jobj = new JSONObject();
                 jobj.put("fieldname", tmpcontyp.getFieldname());
                 jobj.put("fieldlabel",tmpcontyp.getFieldlabel());
                 jobj.put("isessential", tmpcontyp.getIsessential());
                 jobj.put("maxlength",tmpcontyp.getMaxlength());
                 jobj.put("validationtype", tmpcontyp.getValidationtype());
                 jobj.put("fieldid", tmpcontyp.getId());
                 jobj.put("moduleid", tmpcontyp.getModuleid());
                 jobj.put("fieldtype", tmpcontyp.getFieldtype());
                 jobj.put("iseditable", tmpcontyp.getIseditable());
                 jobj.put("comboid", tmpcontyp.getComboid());
                 jobj.put("comboname",tmpcontyp.getComboname());
                 jobj.put("moduleflag", tmpcontyp.getModuleflag());
                 jobj.put("refcolumn_number",Constants.Custom_Column_Prefix+tmpcontyp.getRefcolnum());
                 jobj.put("column_number",Constants.Custom_Column_Prefix+tmpcontyp.getColnum());
                 jresult.append("data",jobj);
            }
            if(colcount == 0){
                jresult.put("data", new JSONArray());
            }
            jresult.put("valid", true);
            jresult.put("success", result.isSuccessFlag());
        } catch (SessionExpiredException ex) {
            Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ModelAndView("jsonView-ex", "model", jresult.toString());
    }

    public ModelAndView getCustomCombodata (HttpServletRequest request, HttpServletResponse response) {
        KwlReturnObject result = null;
        JSONObject jresult = new JSONObject();
        String fieldid = request.getParameter(FieldConstants.Crm_fieldid);
        String flag = request.getParameter(FieldConstants.Crm_flag);
        String jsonview = flag!=null ? "jsonView":"jsonView-ex";
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        try {
            Integer colcount = 1;
            requestParams.put(Constants.filter_names, Arrays.asList(FieldConstants.Crm_fieldid,FieldConstants.Crm_deleteflag));
            requestParams.put(Constants.filter_values, Arrays.asList(fieldid,0));
            result = fieldManagerDAOobj.getCustomCombodata(requestParams);
            List lst = result.getEntityList();
            colcount = lst.size();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                 FieldComboData tmpcontyp = (FieldComboData) ite.next();
                 JSONObject jobjTemp = new JSONObject();
                 jobjTemp.put(FieldConstants.Crm_id, tmpcontyp.getId());
                 jobjTemp.put(FieldConstants.Crm_name, tmpcontyp.getValue());
                 jresult.append(Constants.data,jobjTemp);
            }
            if(colcount == 0){
                jresult.put(Constants.data, new JSONArray());
            }
//            jresult.put("valid", true);
            jresult.put(Constants.success, result.isSuccessFlag());
        } catch (JSONException ex) {
            Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ModelAndView(jsonview,Constants.model, jresult.toString());
    }
    public HashMap<String, Integer> getFieldParamsMap(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        HashMap<String, Integer> FieldMap = new HashMap<String, Integer>();
        result = fieldManagerDAOobj.getFieldParams(requestParams);
        List lst = result.getEntityList();
        Iterator ite = lst.iterator();
        while (ite.hasNext()) {
            FieldParams tmpcontyp = (FieldParams) ite.next();
            FieldMap.put(tmpcontyp.getFieldname(), tmpcontyp.getColnum());
        }
        return FieldMap;
    }
    
    public static String getModuleNamesArr(String[] moduleIDarr) {
        String module="";
        for (int cnt = 0; cnt < moduleIDarr.length; cnt++) {
            int moduleid = Integer.parseInt(moduleIDarr[cnt]);
            if (moduleid == 1) {
                module+= "'Account'";
            } else if (moduleid == 2) {
                module+= "'Lead'";
            } else if (moduleid == 6) {
                module+= "'Contact'";
            } else if (moduleid == 5) {
                module+= "'Opportunity'";
            } else if (moduleid == 4) {
                module+= "'Product'";
            } else if (moduleid == 3) {
                module+= "'Case'";
            }
            module += ",";
        }
        module = module.substring(0, module.length()-1);
        return module;
    }

    public static String getModuleName(int moduleid) {
    String module="";
        if (moduleid == 1) {
            module= "Account";
        } else if (moduleid == 2) {
            module= "Lead";
        } else if (moduleid == 6) {
            module= "Contact";
        } else if (moduleid == 5) {
            module= "Opportunity";
        } else if (moduleid == 4) {
            module= "Product";
        } else if (moduleid == 3) {
            module= "Case";
        }
    return module;
    }
    private HashMap<String, Object> createDefaultHeadrEntry(boolean allowmapping,String Refcolumn_number,String column_name,Integer isessential,String companyid,String comboid,String RefModule,String RefDataColumn,String RefFetchColumn,Integer fieldtype,Integer moduleid,Integer fieldmaxlen,String fieldlabel,String fieldid) throws SessionExpiredException, JSONException, ServiceException {
            HashMap<String, Object> requestParams= new HashMap<String, Object>();

            requestParams.put("Customflag", true);
            requestParams.put("DefaultHeader",fieldlabel);
            requestParams.put("Recordname",Constants.Custom_Record_Prefix+fieldlabel);
            requestParams.put("Dbcolumnrefname",Refcolumn_number);
            requestParams.put("Dbcolumnname",column_name);
            requestParams.put("PojoMethodName",Constants.Custom_Record_Prefix+fieldlabel);
            requestParams.put("Pojoheadername",fieldid+"");
            requestParams.put("MaxLength",fieldmaxlen);

            HashMap<String, Object> moduleParams= new HashMap<String, Object>();
            moduleParams.put("filter_names", Arrays.asList("moduleName"));
            moduleParams.put("filter_values", Arrays.asList(getModuleName(moduleid)));
            KwlReturnObject kmsg = fieldManagerDAOobj.getModules(moduleParams);
            String modulename = "";
            if (kmsg.isSuccessFlag()) {
                    Modules modObj = (Modules) kmsg.getEntityList().get(0);
                    modulename =modObj.getModuleName();
                    requestParams.put("ModuleName",modulename);
                    requestParams.put("Module",modObj.getId());
                }

            requestParams.put("Xtype",fieldtype+"");
            requestParams.put("Configid","0");
            requestParams.put("HbmNotNull",false);
            requestParams.put("AllowImport",allowmapping);
            requestParams.put("AllowMapping",allowmapping);
            String ValidateType=null;
            if(fieldtype == 1 || fieldtype == 9) {
                ValidateType = "string";
            }else if(fieldtype == 2) {
                ValidateType = "double";
            }else if(fieldtype == 3) {
                ValidateType = "date";
            }else if(fieldtype == 4) {
                ValidateType = "dropdown";
            }else if(fieldtype == 5) {
                ValidateType = "time";
            }else if(fieldtype == 7) {
                ValidateType = "multiselect";
            }else if(fieldtype == 8) {
                ValidateType = "refdropdown";
                requestParams.put("RefModule_PojoClassName",RefModule);
                requestParams.put("RefDataColumn_HbmName",RefDataColumn);
                requestParams.put("RefFetchColumn_HbmName",RefFetchColumn);
                requestParams.put("Configid",comboid);
            }
            requestParams.put("ValidateType",ValidateType);
//            df.setMandatory(isessential==0?false:true);

            
                requestParams.put("Mandatory", isessential==0?false:true);
                kmsg = fieldManagerDAOobj.insertdefaultheader(requestParams);
                JSONObject resultJson = new JSONObject();
                requestParams.clear();
                resultJson.put("success", kmsg.isSuccessFlag());
                if (kmsg.isSuccessFlag()) {
                    DefaultHeader dh = (DefaultHeader) kmsg.getEntityList().get(0);
                    requestParams.put("Defaultheader", dh.getId());
                    requestParams.put("Company", companyid);
                    requestParams.put("NewHeader", "");
                    requestParams.put("Mandotory", isessential==0?false:true);
                    requestParams.put("Required", false);
                    requestParams.put("ModuleName", modulename);
                    kmsg = fieldManagerDAOobj.insertcolumnheader(requestParams);
                }
        return requestParams;
    }

    private HashMap<String, Object> processrequest(HttpServletRequest request, String moduelId) throws SessionExpiredException, JSONException, ServiceException {
        HashMap<String, Object> requestParams;
        Integer moduleid = Integer.parseInt(moduelId);
        String companyid = sessionHandlerImpl.getCompanyid(request);
        Integer fieldtype = Integer.parseInt(request.getParameter("fieldType"));
        int moduleflag = 0;
        if(!StringUtil.isNullOrEmpty(request.getParameter("moduleflag"))) {
            moduleflag = Integer.parseInt(request.getParameter("moduleflag"));
        }
        HashMap<String, Object> colParams =null;
        HashMap<String, Object> RefcolParams = null;
        String maxlength = "";
        if (fieldtype == 7) {// multiselect
            RefcolParams = getcolumn_number(companyid, moduleid, fieldtype, moduleflag);
            if (!Boolean.parseBoolean((String) RefcolParams.get("success"))) {
                colParams = RefcolParams;
            } else {
                //  colnumber accessed as per normal field
                colParams = getcolumn_number(companyid, moduleid, 1, moduleflag);
            }
            maxlength = "1000";
        } else {
            colParams = getcolumn_number(companyid, moduleid, fieldtype, moduleflag);
            if (fieldtype == 2) { // number fields
                maxlength = "15";
            } else if (fieldtype == 3) { // date
                maxlength = "50";
            } else if (fieldtype == 4) { // dropdown
                maxlength = "50";
            } else if (fieldtype == 5) { // timefield
                maxlength = "25";
            } else if (fieldtype == 8) { // reference dropdown
                maxlength = "50";
            } else if (fieldtype == 9) { // auto number
                maxlength = "150";
            }
        }
        if (Boolean.parseBoolean((String) colParams.get("success"))) {

            requestParams = new HashMap<String, Object>();

            String fieldlabel = request.getParameter("fieldlabel");
            String formulae = request.getParameter("rules");
            String editable = request.getParameter("iseditable");

            Integer fieldmaxlen = 12;
            if (request.getParameter("maxlength") != null && !StringUtil.isNullOrEmpty(request.getParameter("maxlength"))) {
                maxlength = request.getParameter("maxlength");
            }
            if (!StringUtil.isNullOrEmpty(maxlength)) {
                fieldmaxlen = Integer.parseInt(maxlength);
            }

            Integer validationtype = 0;


            String isessential = request.getParameter("isessential");
            String customregex = request.getParameter("customregex");

            String combodata = request.getParameter("combodata");


            int essential = 0;
            boolean allowmapping = false;
            if (StringUtil.isNullOrEmpty(formulae)) {
                if(moduleflag==1 && fieldtype==8) {
                    allowmapping = false;
                } else if(fieldtype==9){
                	allowmapping = false;
                }else {
                    allowmapping = true;
                }
                if ((!com.krawler.common.util.StringUtil.isNullOrEmpty(isessential) && isessential.equals("false")) || fieldtype==9) {// if field is auto no then no need to mark as mandatory
                    essential = 0;
                } else if (!com.krawler.common.util.StringUtil.isNullOrEmpty(isessential)) {
                    essential = 1;
                }
            }
            requestParams.put("Maxlength", fieldmaxlen);
            requestParams.put("Isessential", essential);
            requestParams.put("Fieldtype", fieldtype);

            requestParams.put("Validationtype", validationtype);
            requestParams.put("Customregex", customregex);
            requestParams.put("Fieldname",Constants.Custom_Record_Prefix+fieldlabel);

            requestParams.put("Fieldlabel", fieldlabel);
            requestParams.put("Companyid", companyid);
            requestParams.put("Moduleid", moduleid);

            requestParams.put("Iseditable", editable);
            String RefModule = null;
            String RefDataColumn = null;
            String RefFetchColumn = null;
            String comboid ="";
            if (fieldtype == 8) {//Reference Module
                comboid = request.getParameter("comboid");
                requestParams.put("Comboname", request.getParameter("comboname"));
                requestParams.put("Comboid", comboid);
                requestParams.put("Moduleflag", Integer.parseInt(request.getParameter("moduleflag")));

                if (request.getParameter("moduleflag").equals("0")) {
                    RefModule = "DefaultMasterItem";
                    RefDataColumn = "value";
                    RefFetchColumn = "id";
                } else if (request.getParameter("comboname").equals("Account")) {
                    RefModule = "CrmAccount";
                    RefDataColumn = "accountname";
                    RefFetchColumn = "accountid";
                } else if (request.getParameter("comboname").equals("Product")) {
                    RefModule = "CrmProduct";
                    RefDataColumn = "productname";
                    RefFetchColumn = "productid";
                } else if (request.getParameter("comboname").equals("Contact")) {
                    RefModule = "CrmContact";
                    RefDataColumn = "lastname";
                    RefFetchColumn = "contactid";
                } else if (request.getParameter("comboname").equals("Case")) {
                    RefModule = "CrmCase";
                    RefDataColumn = "subject";
                    RefFetchColumn = "caseid";
                } else if (request.getParameter("comboname").equals("Opportunity")) {
                    RefModule = "CrmOpportunity";
                    RefDataColumn = "oppname";
                    RefFetchColumn = "oppid";
                } else if (request.getParameter("comboname").equals("Lead")) {
                    RefModule = "CrmLead";
                    RefDataColumn = "lastname";
                    RefFetchColumn = "leadid";
                } else if (request.getParameter("comboname").equals("Users")) {
                    RefModule = "User";
                    RefDataColumn = "lastName";
                    RefFetchColumn = "userID";
                }
            } else {
                requestParams.put("Comboname", "");
                requestParams.put("Comboid",comboid);
                requestParams.put("Moduleflag", 0);
            }

            if (fieldtype == 9) {//auto number
                requestParams.put("Startingnumber", Integer.parseInt(request.getParameter("startingnumber")));
                requestParams.put("Prefix", request.getParameter("prefix"));
                requestParams.put("Suffix", request.getParameter("suffix"));
            }
            requestParams.put("Colnum", colParams.get("column_number"));
            String Refcolumn_number = "0";
            String refcolumnname = null;
            if(fieldtype == 7 && RefcolParams!=null){
                requestParams.put("Refcolnum", RefcolParams.get("column_number"));
                refcolumnname = Constants.Custom_column_Prefix+RefcolParams.get("column_number");
                Refcolumn_number = RefcolParams.get("column_number").toString();
            }


            JSONObject resultJson = new JSONObject();
            KwlReturnObject kmsg = null;
            FieldParams fp = null;
            kmsg = fieldManagerDAOobj.insertfield(requestParams);
            resultJson.put("success", kmsg.isSuccessFlag());
            requestParams.put("success", kmsg.isSuccessFlag()?1:0);
            if (kmsg.isSuccessFlag()) {
                fp = (FieldParams) kmsg.getEntityList().get(0);
                resultJson.put("ID", fp.getId());
                resultJson.put("msg", kmsg.getMsg());
                String defaultvalue = request.getParameter("defaultval");
                defaultvalue = insertfieldcombodata(combodata, fp.getId(), defaultvalue);
                requestParams.put("defaultvalue", defaultvalue);
                String colname = Constants.Custom_column_Prefix + colParams.get("column_number");
                String column_name = Constants.Custom_Column_Prefix + requestParams.get("Colnum");

                createDefaultHeadrEntry(allowmapping,Refcolumn_number, column_name, essential, companyid, comboid, RefModule, RefDataColumn, RefFetchColumn, fieldtype, moduleid, fieldmaxlen, fieldlabel, fp.getId());
                if (!StringUtil.isNullOrEmpty(formulae)) {
                    setCustomColumnFormulae(request,true);
                }
                
            } else {
                resultJson.put("msg", "Error Processing request");
            }
            requestParams.put("response", resultJson);
            

        } else {
            return colParams;
        }
        return requestParams;
    }
    public static String getTableName(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.Crm_account_pojo;
                break;
            case 2:
                module = Constants.Crm_lead_pojo;
                break;
            case 3:
                module = Constants.Crm_case_pojo;
                break;
            case 4:
                module = Constants.Crm_product_pojo;
                break;
            case 5:
                module = Constants.Crm_opportunity_pojo;
                break;
            case 6:
                module = Constants.Crm_contact_pojo;
                break;
        }
        return module;
    }
    public static String getmoduledataTableName(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.Crm_account_custom_data_pojo;
                break;
            case 2:
                module = Constants.Crm_lead_custom_data_pojo;
                break;
            case 3:
                module = Constants.Crm_case_custom_data_pojo;
                break;
            case 4:
                module = Constants.Crm_product_custom_data_pojo;
                break;
            case 5:
                module = Constants.Crm_opportunity_custom_data_pojo;
                break;
            case 6:
                module = Constants.Crm_contact_custom_data_pojo;
                break;
        }
        return module;
    }
    public static String getPrimarycolumn(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.Crm_accountid;
                break;
            case 2:
                module = Constants.Crm_leadid;
                break;
            case 3:
                module = Constants.Crm_caseid;
                break;
            case 4:
                module = Constants.Crm_productid;
                break;
            case 5:
                module = Constants.Crm_opportunityid;
                break;
            case 6:
                module = Constants.Crm_contactid;
                break;
        }
        return module;
    }
    public static String getModuleCustomTableName(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.CRM_CUSTOM_ACCOUNT_TABLE;
                break;
            case 2:
                module = Constants.CRM_CUSTOM_LEAD_TABLE;
                break;
            case 3:
                module = Constants.CRM_CUSTOM_CASE_TABLE;
                break;
            case 4:
                module = Constants.CRM_CUSTOM_PRODUCT_TABLE;
                break;
            case 5:
                module = Constants.CRM_CUSTOM_OPPORTUNITY_TABLE;
                break;
            case 6:
                module = Constants.CRM_CUSTOM_CONTACT_TABLE;
                break;
        }
        return module;
    }
    public static String getmoduledataRefName(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.Crm_account_pojo_ref;
                break;
            case 2:
                module = Constants.Crm_lead_pojo_ref;
                break;
            case 3:
                module = Constants.Crm_case_pojo_ref;
                break;
            case 4:
                module = Constants.Crm_product_pojo_ref;
                break;
            case 5:
                module = Constants.Crm_opportunity_pojo_ref;
                break;
            case 6:
                module = Constants.Crm_contact_pojo_ref;
                break;
        }
        return module;
    }
    public static String getDefaultValue(int xtype) {
        String module = "";
        switch (xtype) {
            case 1:
                module = Constants.TextField_default;
                break;
            case 2:
                module = Constants.NumberField_default;
                break;
            case 3:
                module = new Date().toString();
                break;
            case 5:
                module = Constants.TimeField_default;
                break;
        }
        return module;
    }
    public JSONObject setCustomColumnFormulae(HttpServletRequest request,boolean addprefix) throws ServiceException, JSONException, SessionExpiredException  {
    JSONObject resultJson = new JSONObject();
        String companyid = sessionHandlerImpl.getCompanyid(request);
        String moduleid = request.getParameter("moduleid");
        String formulae = request.getParameter("rules");
        String formulaeWithColname = request.getParameter("rulesWithColname");
        String fieldlabel = addprefix?Constants.Custom_Record_Prefix:"";
        fieldlabel=fieldlabel+request.getParameter("fieldlabel");
        KwlReturnObject resultformule = null;
        KwlReturnObject kmsg = null;
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
                JSONObject jobj = new JSONObject();
                requestParams.put("filter_names", Arrays.asList("companyid.companyID","moduleid","fieldname"));
                requestParams.put("filter_values", Arrays.asList(companyid,moduleid,fieldlabel));
                resultformule = fieldManagerDAOobj.getCustomColumnFormulae(requestParams);
                Integer colcount = resultformule.getEntityList().size();
                formulae = formulae.replaceAll("&#43;", "+");
                formulae = formulae.replaceAll("&#45;", "-");
                formulae = formulae.replaceAll("&#42;", "*");
                formulae = formulae.replaceAll("&#47;", "/");
                formulaeWithColname = formulaeWithColname.replaceAll("&#43;", "+");
                formulaeWithColname = formulaeWithColname.replaceAll("&#45;", "-");
                formulaeWithColname = formulaeWithColname.replaceAll("&#42;", "*");
                formulaeWithColname = formulaeWithColname.replaceAll("&#47;", "/");
                if (colcount >0 ) {
                        CustomColumnFormulae cobj = (CustomColumnFormulae) resultformule.getEntityList().get(0);
                        cobj.setFormulae(formulae);
                        cobj.setFormulaColname(formulaeWithColname);
                        requestParams.put("CustomColumnFormulae", cobj);
                        kmsg = fieldManagerDAOobj.saveCustomColumnFormulae(requestParams);
                        resultJson.put("msg", kmsg.getMsg());
                        resultJson.put("success", true);
                } else {
                    requestParams.clear();
                    requestParams.put("Companyid", companyid);
                    requestParams.put("Fieldname",fieldlabel);
                    requestParams.put("Formulae", formulae);
                    requestParams.put("Moduleid", moduleid);
                    requestParams.put("FormulaColname", formulaeWithColname);
                    kmsg = fieldManagerDAOobj.insertformule(requestParams);
                    resultJson.put("msg", kmsg.getMsg());
                    resultJson.put("success", true);

                }
        return resultJson;
    }
    public ModelAndView addCustomColumnFormulae(HttpServletRequest request, HttpServletResponse response) throws ServiceException, JSONException, SessionExpiredException  {
        String companyid = sessionHandlerImplObj.getCompanyid(request);
        Integer moduleid = Integer.parseInt(request.getParameter("moduleid"));
        String formulae = request.getParameter("rules");
        String fieldlabel = request.getParameter("fieldlabel");
        String fieldid = request.getParameter("fieldid");
        String ismandatory = request.getParameter("ismandatory");

        JSONObject resultJson=null;
        KwlReturnObject kmsg = null;
        FieldParams fp = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            
                    if(!StringUtil.isNullOrEmpty(formulae)) {
                        resultJson = setCustomColumnFormulae(request,false);
                        if(resultJson.has("success")){
                            HashMap<String, Object> requestParams = new HashMap<String, Object>();
                            requestParams.put("moduleid", moduleid);
                            requestParams.put("companyid", companyid);
                            requestParams.put("fieldname", fieldlabel);
                            requestParams.put("fieldid", fieldid);
                            requestParams.put("ismandatory", ismandatory);
                            // update default header function return object from there
                            kmsg = fieldManagerDAOobj.updateDefaultheader(requestParams);
                            kmsg = fieldManagerDAOobj.updateColumnHeader(requestParams);
                            kmsg = fieldManagerDAOobj.updateFieldParams(requestParams);
                            
                        }
                    }else{
                        resultJson = new JSONObject();
                        resultJson.put("success", false);
                    }
            txnManager.commit(status);
        } catch (SessionExpiredException ex) {
            Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        } catch (JSONException ex) {
            Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        } catch (ServiceException ex) {
            Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", resultJson.toString());

    }
    
    public String insertfieldcombodata(String combodata,String fieldid,String defaultvalue) throws ServiceException {
        boolean isdefaultvalue = !StringUtil.isNullOrEmpty(defaultvalue);
        if (!StringUtil.isNullOrEmpty(combodata)) {
                //@somnath default value map to fetch default values id which will insert in next step
                HashMap combohash = new HashMap();
                if(isdefaultvalue){
                    String[] combodefaultvalues = defaultvalue.split(",");
                    for(int cnt1=0;cnt1 < combodefaultvalues.length;cnt1++){
                            String trimval=combodefaultvalues[cnt1].trim();
                            if(!StringUtil.isNullOrEmpty(trimval))
                            combohash.put(combodefaultvalues[cnt1],1);
                    }
                }
                //@somnath default value map end
                String Defaultcombodata="";

                String[] combovalues = combodata.split(";");
                for (int cnt = 0; cnt < combovalues.length; cnt++) {
                    String trimArray = combovalues[cnt].trim();
                    if (!StringUtil.isNullOrEmpty(trimArray)) {
                        HashMap<String, Object> comborequestParams = new HashMap<String, Object>();
                        comborequestParams.put("Fieldid", fieldid);
                        comborequestParams.put("Value", combovalues[cnt]);
                        KwlReturnObject kmsg = fieldManagerDAOobj.insertfieldcombodata(comborequestParams);
                        FieldComboData fc = null;
                        // check default value is same as current combo value if yes add Id it to default value
                        if(isdefaultvalue && combohash.containsKey(combovalues[cnt])){
                            fc = (FieldComboData) kmsg.getEntityList().get(0);
                            Defaultcombodata = Defaultcombodata + fc.getId() + ",";
                        }
                    }
                }

                //@somnath remove "," from default value which is inserted at the end in case of multiselect combo
                if (isdefaultvalue && Defaultcombodata.length() > 0) {
                    defaultvalue = Defaultcombodata.substring(0, Defaultcombodata.length() - 1);
                } else {
                    defaultvalue = "";
                }
            }
        return defaultvalue;
    }

    public ModelAndView insertfield(HttpServletRequest request, HttpServletResponse response) {
        JSONObject resultJson = new JSONObject();
        KwlReturnObject kmsg = null,fresult = null;
        FieldParams fp = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
                String columnExistInModules = Constants.stringInitVal;
                String companyid = sessionHandlerImplObj.getCompanyid(request);
                ArrayList moduleArr = new ArrayList();
                moduleArr.add(Integer.parseInt(request.getParameter("moduleid")));
                boolean createField = true;
                if (request.getParameter("columncreationlead") != null) {
                    moduleArr.add(Constants.Crm_lead_moduleid);
                }
                if (request.getParameter("columncreationaccount") != null) {
                    moduleArr.add(Constants.Crm_account_moduleid);
                }
                if (request.getParameter("columncreationcontact") != null) {
                    moduleArr.add(Constants.Crm_contact_moduleid);
                }
                if (request.getParameter("columncreationproduct") != null) {
                    moduleArr.add(Constants.Crm_product_moduleid);
                }
                if (request.getParameter("columncreationopportunity") != null) {
                    moduleArr.add(Constants.Crm_opportunity_moduleid);
                }
                if (request.getParameter("columncreationcase") != null) {
                    moduleArr.add(Constants.Crm_case_moduleid);
                }
                for(int cnt=0;cnt<moduleArr.size();cnt++) {//Duplicate Name check
                    Integer moduleid = Integer.parseInt(moduleArr.get(cnt).toString());
                    String moduleName= getModuleName(moduleid);
                    String fieldlabel = request.getParameter("fieldlabel");
                    String selectQuery = "select dh.id from default_header dh inner join modules mo on mo.id = dh.module where mo.modulename = ? " +
                        "and (dh.customflag = 'F' or dh.customflag = '0') and dh.id not in (select defaultHeader " +
                        "from column_header where company = ?)  and defaultHeader = ?" +
                        " UNION " +
                        "select ch.id from column_header ch inner join default_header dh on dh.id = ch.defaultHeader " +
                        "inner join modules mo on mo.id = dh.module where company = ? and mo.modulename = ? and " +
                        "(ch.newHeader = ? or dh.defaultHeader = ?)";                    
                    
                    List dupListcol = fieldManagerDAOobj.executeNativeQuery(selectQuery, new String[]{moduleName, companyid, fieldlabel, companyid, moduleName, fieldlabel, fieldlabel});
                    if(dupListcol.size()>0) {
                        createField = false;
                        columnExistInModules += moduleName +", ";
                    }
                }
                if (createField) {
                    HashMap<Integer, HashMap<String, Object>> modulerequestParams = new HashMap<Integer, HashMap<String, Object>>();
                    HashMap<String, Object> requestParams =null;
                    ArrayList<String> ll=new ArrayList<String>();
                    for(int cnt=0;cnt<moduleArr.size();cnt++) {//Create new field
                        Integer moduleid = Integer.parseInt(moduleArr.get(cnt).toString());
                        requestParams = processrequest(request,moduleArr.get(cnt).toString());
                        modulerequestParams.put(moduleid,requestParams);
                    }
                    txnManager.commit(status);
                    for(int cnt=0;cnt<moduleArr.size();cnt++) {//Batch update existing records with default value
                        Integer moduleid = Integer.parseInt(moduleArr.get(cnt).toString());
                        if(modulerequestParams.containsKey(moduleid)){
                            requestParams =modulerequestParams.get(moduleid);
                            requestParams.put(Constants.moduleid, moduleid);
                            requestParams.put(Constants.companyid, companyid);
                            fieldManagerDAOobj.storeDefaultCstmData(request,requestParams);
                            ll.add(requestParams.get("response").toString());
                        }
                    }
                    resultJson.put("sucess", ll);
                    resultJson.put(Constants.moduleid, moduleArr);
                    
                }else{
                    resultJson.put(Constants.success, Constants.msg);
                    resultJson.put("title", "Alert");
                    if(columnExistInModules.length() > 0) {
                        columnExistInModules = columnExistInModules.trim();
                        columnExistInModules = columnExistInModules.substring(0, columnExistInModules.length()-1);
                    }
                    resultJson.put(Constants.msg,Constants.Cannotaddnew +columnExistInModules);
                    txnManager.rollback(status);
                }
            
        } catch (SessionExpiredException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);            
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);            
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);            
        }
        return new ModelAndView("jsonView", "model", resultJson.toString());
    }
     public ModelAndView editfield(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
        JSONObject resultJson = new JSONObject();
        KwlReturnObject kmsg = null;
        FieldParams fp = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        Transaction htc = null;
        boolean isuccess=false;
        String msg="";
        try {
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String maxlength = request.getParameter("maxlength");
            String newCustomHeader = request.getParameter("fieldname");
            String fieldid = request.getParameter("fieldid");
            String isdatefield = request.getParameter("isdatefield");
            Integer fieldtype = 1;
            Map<String,Object> dataMap=new HashMap<String, Object>();
            boolean changeFieldataFlag=false;
            if(request.getParameter("fieldType")!=null)
                fieldtype = Integer.parseInt(request.getParameter("fieldType"));

            Integer fieldmaxlen = 12;
            if (request.getParameter("maxlength") != null && !StringUtil.isNullOrEmpty(request.getParameter("maxlength"))) {
                maxlength = request.getParameter("maxlength");
            }
            if (!StringUtil.isNullOrEmpty(maxlength)) {
                fieldmaxlen = Integer.parseInt(maxlength);
            }
            fp=fieldManagerDAOobj.getFieldParamsObject(fieldid);
            if(fp.getFieldtype()==3 && fieldtype==1){ //Since if fieldtype is changing from  date to text we need to change data related to field 
            	changeFieldataFlag=true;
            }
            DefaultHeader dHeader=null;
            String sqldatepattern ="%Y-%m-%d";
            int modid= Integer.parseInt(request.getParameter("moduleid"));
            if(changeFieldataFlag){
            	DateFormat dateFormat=null;
            	String datformatid=sessionHandlerImpl.getDateFormatID(request);
            	String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
                String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            	dateFormat=kwlCommonTablesDAOObj.getUserDateFormatter(datformatid,timeFormatId,timeZoneDiff);//new SimpleDateFormat(request.getParameter("dateformat"));
            	
            	HashMap<String,Object> hm =new HashMap<String, Object>();
            	List names=new ArrayList();
            	List values=new ArrayList();
            	names.add("dh.pojoheadername");
            	values.add(fieldid);
            	hm.put("filter_names", names);
            	hm.put("filter_values", values);
            	kmsg=fieldManagerDAOobj.getDefaultHeader(hm);
            	List l=kmsg.getEntityList();
            	Iterator it=l.iterator();
            	DefaultHeader dh=null;
             	while(it.hasNext()){
            		 dh=(DefaultHeader)it.next();
            		 dHeader=dh;
            		 dataMap=fieldManagerDAOobj.getCustomDataToBeChange(dh.getDbcolumnname(), modid,dateFormat,companyid);
            	}
            	
            }
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("maxlength", maxlength);
            requestParams.put("fieldid", fieldid);
            requestParams.put("fieldtype", fieldtype);
            if(!StringUtil.isNullOrEmpty(newCustomHeader)){
                requestParams.put("newCustomHeader", newCustomHeader);
                requestParams.put("companyid", companyid);
                kmsg = fieldManagerDAOobj.updateColumnHeader(requestParams);
            }
            kmsg = fieldManagerDAOobj.updateDefaultheader(fieldtype, fieldmaxlen, fieldid);
            kmsg = fieldManagerDAOobj.updateFieldParams(fieldtype, fieldmaxlen, fieldid, companyid);
            if(changeFieldataFlag && dataMap.size()>0){
            	fieldManagerDAOobj.changeCustomDateToString(dHeader.getDbcolumnname(), modid, dataMap, companyid);
            }
            txnManager.commit(status);
            resultJson.put("success", kmsg.isSuccessFlag());
            resultJson.put("msg", kmsg.getMsg());

        } catch (SessionExpiredException ex) {
            Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
            if (htc != null) {
                htc.rollback();
            }
        } catch (JSONException ex) {
            Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
            txnManager.rollback(status);
            if (htc != null) {
                htc.rollback();
            }
        } catch (Exception ne){
        	if(isuccess){
				try {
					resultJson.put("msg", "Custom column is edited but data could not be edited");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				}
        	 Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ne);
             txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", resultJson.toString());
    }
     
    public ModelAndView deletefield(HttpServletRequest request,
                    HttpServletResponse response) {
            JSONObject resultJson = new JSONObject();
            KwlReturnObject kmsg = null;
            FieldParams fp = null;
            // Create transaction
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setName("JE_Tx");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
            TransactionStatus status = txnManager.getTransaction(def);
            String moduleID = request.getParameter("moduleid");
            String moduleIDarr[] = moduleID.split(",");
            String origFieldId = request.getParameter("fieldid");
            String origFieldLabel = request.getParameter("fieldlabel");
            int origModid = -1;

            try {
                    String companyid = sessionHandlerImplObj.getCompanyid(request);
                    String modNames = getModuleNamesArr(moduleIDarr);
                    String msg = fieldManagerDAOobj.checkCustomFieldID(modNames,origFieldId,companyid);
                    if(!StringUtil.isNullOrEmpty(msg)) {
                        txnManager.rollback(status);
                        resultJson.put("success", false);
                        resultJson.put("msg", msg);
                        return new ModelAndView("jsonView", "model", resultJson.toString());
                    }
                    for (int cnt = 0; cnt < moduleIDarr.length; cnt++) {
                            HashMap<String, Object> requestParams = new HashMap<String, Object>();
                            int modid = Integer.parseInt(moduleIDarr[cnt]);
                            String modName = getModuleName(modid);
                            String fieldid = fieldManagerDAOobj.getCustomFieldID(modName,origFieldId,companyid);
                            if(fieldid==null){
                                    continue;
                            }else if(fieldid.equals(origFieldId)){
                                    origModid = modid;
                                    continue;
                            }
                            requestParams.put("fieldlabel", origFieldLabel);
                            requestParams.put("fieldid", fieldid);
                            requestParams.put("modulename", modName);
                            requestParams.put("moduleid", moduleIDarr[cnt]);// getmoduledataTableName
                            requestParams.put("customdata", getmoduledataTableName(modid));
                            requestParams.put("companyid", companyid);
                            kmsg = fieldManagerDAOobj.deletefield(requestParams);
                    }

                    HashMap<String, Object> requestParams = new HashMap<String, Object>();

                    String modName = getModuleName(origModid);
                    requestParams.put("fieldlabel", origFieldLabel);
                    requestParams.put("fieldid", origFieldId);
                    requestParams.put("modulename", modName);
                    requestParams.put("moduleid", String.valueOf(origModid));// getmoduledataTableName
                    requestParams.put("customdata", getmoduledataTableName(origModid));
                    requestParams.put("companyid", companyid);
                    kmsg = fieldManagerDAOobj.deletefield(requestParams);			
                    txnManager.commit(status);
                    resultJson.put("success", kmsg.isSuccessFlag());
                    resultJson.put("msg", kmsg.getMsg());
            } catch (SessionExpiredException ex) {
                    Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
                    txnManager.rollback(status);
            } catch (JSONException ex) {
                    Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
                    txnManager.rollback(status);
            } catch (ServiceException ex) {
                    Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
                    txnManager.rollback(status);
            }
            return new ModelAndView("jsonView", "model", resultJson.toString());
    }

    public ModelAndView getRefComboNames(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj1 = new JSONObject();
        try {
            jobj1 = getFieldManagerService().getRefComboNames();
            
        } catch (Exception ex) {
            Logger.getLogger(fieldManagerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }
}
