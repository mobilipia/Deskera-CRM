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
package com.krawler.formbuilder.servlet;

import static com.krawler.formbuilder.servlet.ModuleBuilderConstants.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.krawler.common.service.ServiceException;
import com.krawler.esp.utils.PropsValues;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.workflow.module.bizservice.ModuleBuilderService;
import com.krawler.workflow.module.dao.ModuleBuilderDao;
import com.krawler.workflow.module.dao.ModuleClause;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class ModuleBuilderController extends MultiActionController {

    private ModuleBuilderService moduleBuilderService;
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    public static final String USER_DATEPREF = "yyyy-MM-dd HH:mm:ss";
    private ModuleBuilderDao moduleDao;
    private HibernateTransactionManager txnManager;
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setModuleDao(ModuleBuilderDao moduleDao) {
        this.moduleDao = moduleDao;
    }
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
   public ModelAndView form(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
            String result = "";
            boolean isFormSubmit=false;
            boolean commit =false;
            //Create transaction
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setName("JE_Tx");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
            TransactionStatus status = txnManager.getTransaction(def);
            try {
                int action = Integer.parseInt(request.getParameter("action"));
                switch (action) {
                    case 0:
                        commit = true;
                        String formid=request.getParameter("formid");
                        String moduleid=request.getParameter("moduleid");
                        String formjson=request.getParameter("formjson");
                        String parentmodule=request.getParameter("parentmodule");
                        String jsondata=request.getParameter("jsondata");
                        String tbar=request.getParameter("tbar");
                        String bbar=request.getParameter("bbar");
                        
                        // TODO add company id in param
                        result = moduleDao.saveForm(formid, moduleid, formjson, parentmodule, jsondata, tbar, bbar);
                        break;
                    case 1:
                        isFormSubmit = true;
                        result = moduleDao.getAllForms(request.getParameter("moduleid"));
                        break;
//                    case 2:
//                        result = getForm(session, request.getParameter("formid"),request);
//                        break;
                    case 3:
                        commit = true;
                        isFormSubmit=true;
                        result = moduleDao.deleteForm(request.getParameter("id"));
                        break;
                    case 4:
                        // TODO add company check
                        int start=Integer.parseInt(request.getParameter("start"));
                        int limit=Integer.parseInt(request.getParameter("limit"));

                        result = moduleDao.getAllModules(request.getParameter("ss"),request.getParameter("sort"),request.getParameter("dir"), start, limit);
                        break;
                    case 5:
                        isFormSubmit=true;
                        commit = true;
                        // TODO add company check
                        result = moduleDao.createModule(request);
                        break;
                    /*case 6:
    //                    result = moduleBuilderGenerateTable.deleteFields(conn,request);
                        break;
                    */case 7:
                        // TODO add company check
                        result = moduleDao.getAllModulesForCombo(request);
                        break;
                    case 8:
                        isFormSubmit = true;
                        result = moduleDao.moduleData(request.getParameter("moduleid"));
                        break;
                    case 9:
                        commit = true;
                        isFormSubmit=true;
                        result = moduleDao.deleteModule(request.getParameter("moduleid"));
                        break;
                   case 10:
                        //result = moduleDao.getGridData(request);
                        String tname = request.getParameter("tablename");
                        moduleid = request.getParameter("moduleid");
                        Object modObj  = Class.forName("com.krawler.esp.hibernate.impl."+tname).newInstance();

                        result = moduleDao.getModuleRecords(moduleid, modObj, prepareClauses(request), DATE_FORMAT);
                        break;
                    case 11:
                        result = moduleDao.getModuleConfig(request.getParameter("moduleid"));
                        break;
                    /*case 12:
                        result = getSearchFieldSet(session,request);
                        break;*/
                    case 13:
                        commit = true;
                        result = moduleDao.saveModuleGridConfig(request.getParameter("jsondata"));
                        break;
                    /*case 14:
                        result = setSearchField(session,request);
                        break;*/
                    case 15:
                        result = moduleDao.getComboField(request.getParameter("moduleid"));
                        break;
                    case 21:
                        result = moduleDao.getComboData(request.getParameter("moduleid"),request.getParameter("name"));
                        break;
                    case 22:
                        commit = true;
                        isFormSubmit=true;
                        result = "{'success':false, 'msg':'Error occured at server.'}";
                        //this function will not be called (Parto of old modulebuilder)
                        
                        List fileItems = new ArrayList();
                        Map arrParams = prepareData(request, fileItems);
                        List modInfo=moduleDao.getModuleInfo((String)arrParams.get("moduleid"));

                        result = moduleDao.createNewRecord(filterData(arrParams, modInfo), fileItems,(String)arrParams.get("moduleid"));//moduleBuilderMethods.createNewRecord(session,request);
                        break;
                    case 23:
                        commit = true;
                        isFormSubmit=true;
                        result = "{'success':false, 'msg':'Error occured at server.'}";
                        
                        List fileItems1 = new ArrayList();
                        Map arrParams1 = prepareData(request, fileItems1);
                        List modInfo1=moduleDao.getModuleInfo((String)arrParams1.get("moduleid"));

                        result = moduleDao.editRecord(filterData(arrParams1, modInfo1), fileItems1, "id",(String)arrParams1.get("moduleid"));//moduleBuilderMethods.editRecord(session,request);
                        break;
                    case 24:
                        commit = true;
//                        isFormSubmit=true;
                        result = moduleDao.deleteRecord("id",request.getParameter("id"),request.getParameter("moduleid"));//moduleBuilderMethods.deleteRecord(session,request);
                        break;
                    case 25:
                        String basemode=request.getParameter("basemode");
                        String mdlid=request.getParameter("moduleid");
                        String reportid=request.getParameter("reportid");
                        String taskid=request.getParameter("taskid");
                        result = moduleDao.openSubModules(basemode, mdlid, reportid, taskid);
                        break;
                    case 26:
                        result = moduleDao.getOtherModules(request.getParameter("moduleid"), request.getParameter("mode"));
                        break;
                    case 27:
                        commit = true;
                        result = moduleDao.configSubtabModules(request.getParameter("basemodule")
                                ,request.getParameter("submodule")
                                ,request.getParameter("mode")
                                ,request.getParameter("columnname"));
                        break;
                    case 29:
                        commit = true;
                        isFormSubmit=true;
                        result = moduleDao.uploadFile(request);//moduleBuilderMethods.uploadFile(session,request);
                        break;
                    case 30:
                        result = moduleDao.getAttachment(request);//moduleBuilderMethods.getAttachment(session, request);
                        break;
                    case 31:
                        result = moduleDao.getStdCongifType(request.getParameter("reportid"));
                        break;
                    case 32:
                        commit = true;
                        int configid = 0;
                        try {
                            configid = Integer.parseInt(request.getParameter("configid"));
                        }catch(Exception ex){
                            logger.warn(ex.getMessage(), ex);
                        }
                        result = moduleDao.getModuleCongifType(configid
                                ,request.getParameter("add")
                                ,request.getParameter("reportid")
                                ,request.getParameter("deleteconfig"));
                        break;
                    case 33:
                        String comboValueId = request.getParameter("comboValueId");
                        List<ModuleClause> clauses = new ArrayList<ModuleClause>();
                        clauses.add(new ModuleClause("id", "=", comboValueId));

                        result = moduleDao.getReadOnlyFields(request.getParameter("moduleid")
                                ,request.getParameterValues("appenid")
                                ,clauses
                                ,DATE_FORMAT);
                        break;
                    case 34:
                        result = moduleDao.getModuleForCombo(request);
                        break;
                    case 35:
                        commit = true;
                       result = deployProject();
                       break;
                    case 36:
                        commit = true;
                        isFormSubmit=true;
                        result = moduleDao.editModule(request);
                        break;
                    case 37:
//                        isFormSubmit=true;
                        result = moduleDao.getAllModules1(request.getParameter("moduleid"));
                        break;
                    case 38:
                        result = moduleDao.getTableColumn(request.getParameter("moduleid"));
                        break;
                    case 39:
                        isFormSubmit = true;
                        result = moduleDao.getformWithParentvalue(request.getParameter("parentmodule")
                                ,request.getParameter("childmodule")
                                ,request.getParameter("modulevar"));
                        break;
                    case 40:
                        commit = true;
                        String moduleidParam = request.getParameter("moduleid");
                        String tablename1 = request.getParameter(PARAM_TABLE_NAME);
                        result = moduleDao.buildModule(moduleidParam,tablename1);
                        
                        break;
                    case 45:// Not find any reference
                        String tablename = request.getParameter(PARAM_TABLE_NAME);
                        Object moduleObj  = Class.forName("com.krawler.esp.hibernate.impl."+tablename).newInstance();
                        
                        result = moduleDao.getModuleRecords(moduleObj, DATE_FORMAT);
                        break;
                    case 46:
                        moduleid = request.getParameter("moduleid");
                        result = moduleDao.getModuleRecords(moduleid, request.getParameter("tablename"),prepareClauses(request),DATE_FORMAT);
                        break;
                    case 47:
                        moduleid = request.getParameter("moduleid");
                        result = moduleDao.getModuleRecords(moduleid, request.getParameter("tablename"),prepareClauses(request),DATE_FORMAT);
                        break;
                    case 48:
                        isFormSubmit = true;
                        result = moduleDao.getPortletData(request);
                        break;
                }
                if (commit) {
                    txnManager.commit(status);
                } else {
                    txnManager.rollback(status);
                }
            } catch (NumberFormatException e) {
                logger.warn(e.getMessage(), e);
                txnManager.rollback(status);
            } catch (ServiceException ex) {
                logger.warn(ex.getMessage(), ex);
                txnManager.rollback(status);
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
                txnManager.rollback(status);
            }
            if(isFormSubmit){
                return new ModelAndView("jsonView-ex", "model", result);
            }
            return new ModelAndView("jsonView", "model", result);
    }

   public ModelAndView undeployModule(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
       String result = "{'success' : false}";
       String moduleId = request.getParameter(PARAM_MODULED_ID);

       if (moduleId != null)
       {
           getModuleBuilderService().undeployModule(moduleId);
       }

       return new ModelAndView("jsonView", "model", result);
    }

    public String deployProject() {
        String result = "{'success' : false}";
        try{
            String command = "ant -f build.xml deploy-local";
            String basePath = PropsValues.PROJECT_HOME;//cr.get("save_json_path");
            File buildXMLFile = new File(basePath);
//            Process process = Runtime.getRuntime().exec(command);
            Process process = Runtime.getRuntime().exec(command, null, buildXMLFile);
            BufferedReader br = null;
            // Get the input stream and read from it
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = null, output = "";
            while ((line = br.readLine()) != null) {
                output += line + "\n";
            }
            if (br != null) {
                br.close();
            }
            result = "{'success' : true}";
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
            ServiceException.FAILURE("FormServlet.deployProject", ex);
        }
        return result;
    }
    
    /**
     * @return the moduleDao
     */
    public ModuleBuilderDao getModuleDao()
    {
        return moduleDao;
    }
    
    /**
     * @return the moduleBuilderService
     */
    public ModuleBuilderService getModuleBuilderService()
    {
        return moduleBuilderService;
    }
    
    /**
     * @param moduleBuilderService the moduleBuilderService to set
     */
    public void setModuleBuilderService(ModuleBuilderService moduleBuilderService)
    {
        this.moduleBuilderService = moduleBuilderService;
    }

    private List<ModuleClause> prepareClauses(HttpServletRequest request) {
        List<ModuleClause> clauses = new ArrayList<ModuleClause>();
        String js = request.getParameter(PARAM_FILTER_JSON);
        try{
            clauses.add(new ModuleClause("deleteflag", "=", 0.0));
            JSONArray jArr = new JSONArray(js);
            ModuleClause mc;
            for(int i=0;i<jArr.length();i++){
                JSONObject objJSONObject = (JSONObject) jArr.get(i);
                if (objJSONObject.getString(CONST_XTYPE).equals(PARAM_DATEFIELD)) {
                    String[] splitString = objJSONObject.getString(PARAM_SEARCH_TEXT).split(COMMA);
                    String fromDate = splitString[0];
                    String toDate = splitString[1];
                    mc=new ModuleClause(objJSONObject.getString(CONST_COLUMN), OP_GREATER_THAN_EQUAL, fromDate + CONST_DAY_START_TIME);
                    clauses.add(mc);
                    mc=new ModuleClause(objJSONObject.getString(CONST_COLUMN), OP_LESS_THAN_EQUAL, toDate + CONST_DAY_START_TIME);
                    clauses.add(mc);
                } else if (objJSONObject.getString(CONST_XTYPE).equals(PARAM_NUMBERFIELD)) {
                    Number num;
                    try{
                        num = Long.parseLong(objJSONObject.getString(PARAM_SEARCH_TEXT));
                    }catch(NumberFormatException ex){
                        num = Double.parseDouble(objJSONObject.getString(PARAM_SEARCH_TEXT));
                    }
                    mc=new ModuleClause(objJSONObject.getString(CONST_COLUMN), PARAM_OP_EQUAL, num);
                    clauses.add(mc);
                } else if (objJSONObject.getString(CONST_XTYPE).equals(PARAM_RADIO) || objJSONObject.getString(CONST_XTYPE).equals(PARAM_CHECKBOX)) {
                    mc=new ModuleClause(objJSONObject.getString(CONST_COLUMN), PARAM_OP_EQUAL, Boolean.parseBoolean(objJSONObject.getString(PARAM_SEARCH_TEXT)));
                    clauses.add(mc);
                } else if (objJSONObject.getString(CONST_XTYPE).equals(PARAM_TIMEFIELD)) {
                    mc=new ModuleClause(objJSONObject.getString(CONST_COLUMN), PARAM_OP_EQUAL, objJSONObject.getString(PARAM_SEARCH_TEXT));
                    clauses.add(mc);
                } else if (objJSONObject.getString(CONST_XTYPE).equals(PARAM_COMBO)) {
                    mc=new ModuleClause(objJSONObject.getString(CONST_COLUMN), PARAM_OP_EQUAL, objJSONObject.getString(PARAM_SEARCH_TEXT));
                    clauses.add(mc);
                } else {
                    mc=new ModuleClause(objJSONObject.getString(CONST_COLUMN), PARAM_OP_LIKE, PARAM_OP_MATCH + objJSONObject.getString(PARAM_SEARCH_TEXT) + PARAM_OP_MATCH);
                    clauses.add(mc);
                }
            }
        }catch(JSONException ex){
            logger.warn(ex.getMessage(), ex);
        }
        return clauses;
    }

    private Map prepareData(HttpServletRequest request, List fileItems) throws ServiceException{
        Map temp;
        if (ServletFileUpload.isMultipartContent(request)) {
            temp = parseRequest(request, fileItems);
        } else {
            temp = getParamList(request.getParameterMap());
        }

        return temp;
    }

    private Map filterData(Map reqParam, List list){
        Map arrParam = new HashMap();
        for(int cnt=0;cnt<list.size();cnt++) {
            Object[] row = (Object[]) list.get(cnt);

            String name = row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[1];
            String xtype = row[1].toString();

            if (xtype.equals("checkbox") || xtype.equals("radio")) {
                    arrParam.put(name, reqParam.get(name) != null);
            } else if (xtype.equals("datefield")) {
                try {
                    arrParam.put(name, DATE_FORMAT.parse((String) reqParam.get(name)));
                } catch (ParseException ex) {
                    arrParam.put(name, null);
                }
            } else if (xtype.equals("numberfield")) {
                try {
                    arrParam.put(name, Double.parseDouble((String) reqParam.get(name)));
                } catch (NumberFormatException ex) {
                    arrParam.put(name, null);
                }
            } else if (xtype.equals("checkboxgroup")) {
                if (reqParam.get(name) == null) {
                    arrParam.put(name, "");
                }else
                    arrParam.put(name, reqParam.get(name));
            } else {
                arrParam.put(name, reqParam.get(name));
            }
        }

        return arrParam;
    }

    public HashMap<String,String> getParamList(Map<String,String[]> list){
        HashMap<String,String> resultList = new HashMap<String,String>();
        java.util.Set s1 = list.keySet();
        java.util.Iterator ite = s1.iterator();
        while(ite.hasNext()){
            String key =  (String) ite.next();
            String[] val = list.get(key);
            if(val==null) continue;
            if(val.length>1){
                String str=Arrays.toString(val);
                resultList.put(key, str.substring(1, str.length()-1));
            }else{
                resultList.put(key,val[0]);
            }
        }
        return resultList;
    }

    public Map parseRequest(HttpServletRequest request,List<FileItem> fi) throws ServiceException{
        Map arrParam = new HashMap();
        FileItemFactory factory = new DiskFileItemFactory(4096, new File("/tmp"));
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1000000);
        FileItem fi1 = null;
        List fileItems = null;
        try {
            fileItems = upload.parseRequest(request);
        } catch (FileUploadException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("File upload has some problem", e);
        }
        for (Iterator k = fileItems.iterator(); k.hasNext();) {
            fi1 = (FileItem) k.next();
            if(fi1.isFormField()){
                String key = fi1.getFieldName();
                try {
					if(arrParam.containsKey(key)){
					    arrParam.put(key, arrParam.get(key)+", "+fi1.getString("UTF-8"));
					}else{
					    arrParam.put(fi1.getFieldName(), fi1.getString("UTF-8"));
					}
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage());
				}
            }else{
                if (fi1.getSize() != 0) {
                    fi.add(fi1);
                }
            }
        }
        return arrParam;
    }
}
