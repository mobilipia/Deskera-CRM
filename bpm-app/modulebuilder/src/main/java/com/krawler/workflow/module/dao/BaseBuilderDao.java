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

package com.krawler.workflow.module.dao;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.hibernate.impl.mb_docs;
import com.krawler.esp.hibernate.impl.mb_reportlist;
import com.krawler.esp.hibernate.impl.mb_stdConfigs;
import com.krawler.esp.utils.PropsValues;
import com.krawler.portal.util.TextFormatter;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author krawler-user
 */
public class BaseBuilderDao extends BaseDAO {
    protected sessionHandlerImpl sessionHandlerDao;

    public void setSessionHandlerDao(sessionHandlerImpl sessionHandlerDao) {
        this.sessionHandlerDao = sessionHandlerDao;
    }

    public String getStdConfig(String moduleid) throws ServiceException{
       String retStr = "";
       JSONArray jObj = new JSONArray();
       String hql = "select mb_moduleConfigMap.configid from com.krawler.esp.hibernate.impl.mb_moduleConfigMap " +
                    "as mb_moduleConfigMap where mb_moduleConfigMap.moduleid.reportid = ? ";
       try{
            List list = executeQuery(hql, new Object[]{moduleid});
            Iterator ite = list.iterator();
            while(ite.hasNext()){
                mb_stdConfigs obj = (mb_stdConfigs) ite.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("id", obj.getConfigid());
                jtemp.put("name", obj.getConfigname());
                jObj.put(jtemp);
            }
            retStr=new JSONObject().put("stdconfig", jObj).toString();
       }catch (Exception e) {
            logger.warn(e.getMessage(), e);
            retStr = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return retStr;
    }

    public String editRecord(HttpServletRequest request) throws ServiceException {
        String result = "{'success':false, 'msg':'Error occured at server.'}";
        try {

            HashMap<String,String> arrParam = new HashMap<String,String>();
            boolean fileUpload = false;
            ArrayList<FileItem> fi = new ArrayList<FileItem>();
            if(request.getParameter("fileAdd")!=null){
                parseRequest(request,arrParam,fi,fileUpload);
            }else{
                arrParam = getParamList(request.getParameterMap());
            }

            String moduleid = arrParam.get("moduleid");//request.getParameter("moduleid");
            mb_reportlist  basemodule = (mb_reportlist) get(mb_reportlist.class, moduleid);

            String id = arrParam.get("id");//request.getParameter("id");
            String tablename = getReportTableName(moduleid);


    //            if(fileUpload){
                    for(int cnt = 0 ; cnt< fi.size();cnt++){
                        String fileName = new String(fi.get(cnt).getName().getBytes(), "UTF8");
                        String Ext = "";
                        if (fileName.contains(".")){
                              Ext = fileName.substring(fileName.lastIndexOf("."));
                        }
                          User userObj = (User) get(User.class,sessionHandlerDao.getUserid());

                        mb_docs docObj = new mb_docs();
                        docObj.setDocname(fileName);
                        docObj.setUserid(userObj);
                        docObj.setStorename("");
                        docObj.setDoctype("");
                        docObj.setUploadedon(new Date());
                        docObj.setStorageindex(1);

    //                    int size = getSize(fi.get(cnt).getSize());
                        docObj.setDocsize(fi.get(cnt).getSize()+"");
                        save(docObj);
                        String fileid = docObj.getDocid();
                        arrParam.put(fi.get(cnt).getFieldName(), fileid);
                        if(Ext.length()>0){
                              fileid = fileid+Ext;
                        }
                        docObj.setStorename(fileid);
                        saveOrUpdate(docObj);

                        uploadFile(fi.get(cnt),PropsValues.STORE_PATH+"/"+tablename,fileid);
                    }
    //            }
                String sql = "Select mb_gridconfig.name, mb_gridconfig.xtype, mb_gridconfig.displayfield, " +
                        " mb_gridconfig.reftable, mb_gridconfig.combogridconfig from "+ PropsValues.PACKAGE_PATH +".mb_gridconfig as mb_gridconfig" +
                        " where mb_gridconfig.reportid = ? order by mb_gridconfig.name ";

                List ls = executeQuery(sql, basemodule);
    //            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-d HH:mm:ss");
    //            String timestamp1 = sdf.format(new java.util.Date());
                java.util.Date timestamp1 = new java.util.Date();

                String[] namesArray = new String[ls.size()+2];
                Object[] namesValues = new Object[ls.size()+2];

                int cnt = 0;
                Iterator ite = ls.iterator();
                while(ite.hasNext()) {
                    Object[] row = (Object[]) ite.next();

                    String name = row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[1];
                    String xtype = row[1].toString();

                    if (xtype.equals("checkbox") || xtype.equals("radio")) {
    //                    if(request.getParameter(name) != null) {
                        if (arrParam.get(name) != null) {
                            namesArray[cnt] = name;
                            namesValues[cnt] = true;//Boolean.parseBoolean(request.getParameter(name));
    //                        if(Boolean.parseBoolean(request.getParameter(name))) {
    //                            namesValues[cnt] = 1;
    //                        } else {
    //                            namesValues[cnt] = 0;
    //                        }
                        } else {
                            namesArray[cnt] = name;
                            namesValues[cnt] = false;
                        }
                    }else if(xtype.equals("combo") ){
                         String refTableName = "";
                         if(row[4].toString().equals("-1")) {
                            refTableName = row[3].toString();
                         }else{
                            refTableName = "mb_configmasterdata";
                         }
                         Class clrefTable = Class.forName(PropsValues.PACKAGE_PATH+"."+refTableName);
                         Object obj = get(clrefTable, arrParam.get(name));
                         namesArray[cnt] = name;
                         namesValues[cnt] = obj;

                    } else {
                        if (xtype.equals("checkboxgroup")) {

                            if (arrParam.get(name) != null) {
                                String[] checkboxArray = request.getParameterValues(name);

                                String checkboxstr = "";
                                for (int i = 0; i < checkboxArray.length; i++) {
                                    if ((i + 1) == checkboxArray.length) {
                                        checkboxstr = checkboxstr + checkboxArray[i];
                                    } else {
                                        checkboxstr = checkboxstr + checkboxArray[i] + ",";
                                    }

                                }

                                namesArray[cnt] = name;
                                namesValues[cnt] = checkboxstr;
                            } else {
                                namesArray[cnt] = name;
                                namesValues[cnt] = "";
                            }
                        } else {
                            if (arrParam.get(name) != null) {
                                namesArray[cnt] = name;
                                namesValues[cnt] = arrParam.get(name);// request.getParameter(name);
                            } else {
                                namesArray[cnt] = "";
                                namesValues[cnt] = "";
                            }
                        }
                    }
                    cnt++;
                }

                namesArray[cnt] = "modifieddate";
                namesValues[cnt++] = timestamp1;
                namesArray[cnt] = "deleteflag";
                namesValues[cnt] = 0.0;
                String ptablename = tablename;
                tablename = PropsValues.PACKAGE_PATH+"."+tablename;
                Class cl = Class.forName(tablename);
                java.lang.reflect.Constructor co = cl.getConstructor();
                Object invoker = get(cl, id);

                for( int i = 0; i < namesArray.length; i++ ){
                    if(!namesArray[i].toString().equals("")){
                        Field  field = invoker.getClass().getDeclaredField(namesArray[i].toString());
                        Class type = field.getType();
                        Class  arguments[] = new Class[] {type};

                        java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(namesArray[i], TextFormatter.G),arguments);
                        Object objVal = typeCastForValue(type.getName(),namesValues[i]);
                        Object[] obj = new Object[]{objVal};
    //                    Object[] obj = new Object[]{namesValues[i]};
                        Object result1 = objMethod.invoke(invoker, obj);
                    }
                }
            //Get implementation class object and call before insert method.
            Class cl1 = Class.forName(PropsValues.PACKAGE_PATH+".impl_"+getReportTableName(moduleid));
            java.lang.reflect.Constructor co1 = cl1.getConstructor();
            Object invoker1 = co1.newInstance();
            Class  arguments1[] = new Class[] {HibernateTemplate.class, HttpServletRequest.class, HashMap.class,Object.class};

            java.lang.reflect.Method objMethod1 = cl1.getMethod("beforeEdit",arguments1);
            Object[] obj1 = new Object[]{getHibernateTemplate(), request, arrParam,invoker};
            Object result11 = objMethod1.invoke(invoker1, obj1);
            if(Boolean.parseBoolean(result11.toString())) {
                saveOrUpdate(invoker);
                if(arrParam.containsKey("jsondata")){
                    insertGridValues(arrParam.get("jsondata"), sessionHandlerDao.getUserid(), ptablename, invoker);
                }
                result = "{'success':true}";
//                String moduleName = AuditTrialHandler.getReportName(session, moduleid);
//                String actionType = "Edit Form Record";
//                String details = "Record edited in "+moduleName;
//                long actionId = AuditTrialHandler.getActionId(session, actionType);
//                AuditTrialHandler.insertAuditLog(session, actionId, details, request);
                objMethod1 = cl1.getMethod("afterEdit",arguments1);
                result11 = objMethod1.invoke(invoker1, obj1);
                if(!Boolean.parseBoolean(result11.toString())) {
                    result = "{'success':false, 'msg':'Error occured at server.'}";
                }
            } else { //beforeInsert false
                result = "{'success':false, 'msg':'Error occured at server.'}";
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{'success':false, 'msg':'Error occured at server.'}";
            throw ServiceException.FAILURE("moduleBuilderMethods.editRecord", e);
        }
        return result;
    }

    public String getReportTableName(String reportid) throws ServiceException{
        String reportTableName = "";
         mb_reportlist reporList = (mb_reportlist) get(mb_reportlist.class, reportid);
        if(reporList!=null){
            if(reporList.getTablename() != null)
                reportTableName = reporList.getTablename();
        }else{
            String SELECT_QUERY="Select mb_reportlist.tablename from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist"
                          +" where mb_reportlist.reportid = ?";
            try {
                List list = executeQuery(SELECT_QUERY, reportid);
                Iterator ite = list.iterator();
                if( ite.hasNext() ) {
                    Object[] row = (Object[]) ite.next();
                    reportTableName = row[0].toString();
                }
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE("FormServlet.getReportTableName", e);
            }
        }
        return reportTableName;
    }


    public Object typeCastForValue(String classname,Object value) {
        String ValObjClass = value.getClass().getName();
        try {
            if(classname.equals("java.util.Date") && !ValObjClass.equals("java.util.Date")) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    value = format.parse(value.toString());
            } else if((classname.equals("java.lang.Boolean") || classname.equals("java.lang.Boolean")) && !ValObjClass.equals("java.lang.Boolean")) {
                    value = Boolean.parseBoolean(value.toString());
            } else if(classname.equals("java.lang.Double") && !ValObjClass.equals("java.lang.Integer")) {
                    value = Double.parseDouble(value.toString());
            } else if(classname.equals("java.lang.Integer") && !ValObjClass.equals("java.lang.Integer")) {
                    value = Integer.parseInt(value.toString());
            } else if(classname.equals("java.lang.Double") && !ValObjClass.equals("java.lang.Double")) {
                    value = Double.parseDouble(value.toString());
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            value = null;
        }
        finally {
            return value;
        }

    }

    public void uploadFile(FileItem fi,String destinationDirectory,String fileName) throws ServiceException{
        try {
            File destDir = new File(destinationDirectory);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File uploadFile = new File(destinationDirectory, fileName);
            fi.write(uploadFile);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
           throw ServiceException.FAILURE("moduleBuilderMethods.uploadFile", ex);
        }

    }
    public void parseRequest(HttpServletRequest request,HashMap<String,String> arrParam,ArrayList<FileItem> fi,boolean fileUpload) throws ServiceException{
                DiskFileUpload fu = new DiskFileUpload();
                FileItem fi1 = null;
                List fileItems = null;
                try {
                    fileItems = fu.parseRequest(request);
                } catch (FileUploadException e) {
                    logger.warn(e.getMessage(), e);
                    throw ServiceException.FAILURE("Admin.createUser", e);
                }
                for (Iterator k = fileItems.iterator(); k.hasNext();) {
                    fi1 = (FileItem) k.next();
                    if(fi1.isFormField()){
                        try {
							arrParam.put(fi1.getFieldName(), fi1.getString("UTF-8"));
						} catch (UnsupportedEncodingException e) {
							logger.error(e.getMessage());
						}
                    }else{
                            if (fi1.getSize() != 0) {
                                fi.add(fi1);
                                fileUpload = true;
                            }
                    }
                }

    }

    public static HashMap<String,String> getParamList(Map<String,String[]> list){
        HashMap<String,String> resultList = new HashMap<String,String>();
        java.util.Set s1 = list.keySet();
        java.util.Iterator ite = s1.iterator();
        while(ite.hasNext()){
            String key =  (String) ite.next();
            String[] val = list.get(key);
            resultList.put(key,val[0]);

        }
        return resultList;
    }

    public void insertGridValues(String jdata, String uid, String ptablename, Object pInvoker) throws ServiceException{
        try{
            JSONArray jarr = new JSONArray(jdata);
            for(int a = 0; a < jarr.length(); a++){
                JSONObject temp = jarr.getJSONObject(a);
                Iterator key = temp.keys();
                while(key.hasNext()){
                    String keyVal = key.next().toString();
                    JSONArray valArr = temp.getJSONArray(keyVal);
                    insertInToGridTable(keyVal, valArr.toString(), uid, ptablename, pInvoker);
                }
            }
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("moduleBuilderMethods.createNewRecords", e);
        }
    }

    public void insertInToGridTable(String reportid, String jsonstr, String userid, String ptablename, Object pInvoker) throws ServiceException{
        java.util.Iterator ite = null;
        try {
            String reportTName = getReportTableName(reportid);
            HashMap<String, Object> invokerObj = new HashMap<String, Object> ();
            HashMap<String, Class> clObj = new HashMap<String, Class> ();

            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
                String SELECT_QUERY = "select mb_gridconfig.name, mb_gridconfig.reftable, mb_gridconfig.xtype, mb_gridconfig.combogridconfig from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig" +
                        " where mb_gridconfig.reportid = ? ";
            List list = executeQuery(SELECT_QUERY, new Object[]{report});

            JSONArray jsonArray = new JSONArray(jsonstr);
            JSONObject jobj = new JSONObject();
            if(!StringUtil.isNullOrEmpty(reportTName)) {
                for (int k = 0; k < jsonArray.length(); k++) {
                    invokerObj = new HashMap<String, Object> ();
                    clObj = new HashMap<String, Class> ();

                    jobj = jsonArray.getJSONObject(k);
                    ite = list.iterator();
                    while( ite.hasNext() ) {
                        Object[] row = (Object[]) ite.next();
                        if(StringUtil.isNullOrEmpty(jobj.getString(reportTName+PropsValues.REPORT_HARDCODE_STR+"id"))) {
                            insertGridRecord(reportTName, jobj, userid, list, row, invokerObj, clObj);
                        } else {
                            editGridRecord(reportTName, jobj, userid, list, row, invokerObj, clObj);
                        }
                    }
                    //Insert parent table object(pInvoker)
                    Class cl = null;
                    Object invoker = null;
                    if(invokerObj.containsKey(reportTName)) {
                        cl = clObj.get(reportTName);
                        invoker = invokerObj.get(reportTName);
                    } else {
                        cl = Class.forName(PropsValues.PACKAGE_PATH+"."+reportTName);
                        java.lang.reflect.Constructor co = cl.getConstructor();
                        invoker = co.newInstance();
                        clObj.put(reportTName, cl);
                        invokerObj.put(reportTName, invoker);
                    }

//                    // set default values
//                    java.util.Date timestamp1 = new java.util.Date();
//                    String[] namesArray = new String[4];
//                    namesArray[0] = "createdby";namesArray[1] = "createddate"; namesArray[2] = "modifieddate";namesArray[3] = ptablename+"id";
//                    Object[] namesValues = new Object[4];
//                    namesValues[0] = userid;namesValues[1] = timestamp1;namesValues[2] = timestamp1;namesValues[3] = pInvoker;
//                    for(int defaultCnt=0;defaultCnt < namesArray.length;defaultCnt++ )  {
//                        Field  field = invoker.getClass().getDeclaredField(namesArray[defaultCnt].toString());
//                        Class type = field.getType();
//                        Class  arguments[] = new Class[] {type};
//
//                        java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(namesArray[defaultCnt], TextFormatter.G),arguments);
//                        Object objVal = moduleBuilderMethods.typeCastForValue(type.getName(),namesValues[defaultCnt]);
//                        Object[] obj = new Object[]{objVal};
//                        Object result1 = objMethod.invoke(invoker, obj);
//                    }

                    Iterator temp = invokerObj.keySet().iterator();
                     //Get implementation class object and call before insert method.
//                    Class cl1 = Class.forName(PropsValues.PACKAGE_PATH+".impl_"+reportTName);
//                    java.lang.reflect.Constructor co1 = cl1.getConstructor();
//                    Object invoker1 = co1.newInstance();
//                    Class  arguments1[] = new Class[] {class, HttpServletRequest.class,Object.class};
//                    String beforeEventName = "";
//                    String afterEventName = "";
//                    if(StringUtil.isNullOrEmpty(jobj.getString(reportTName+PropsValues.REPORT_HARDCODE_STR+"id"))) {
//                        beforeEventName = "beforeInsert";
//                        afterEventName = "afterInsert";
//                    }else{
//                        beforeEventName = "beforeEdit";
//                        afterEventName = "afterEdit";
//                    }
//                    java.lang.reflect.Method objMethod1 = cl1.getMethod(beforeEventName,arguments1);
//                    Object reportObj = invokerObj.get(reportTName);
//                    Object[] obj1 = new Object[]{hibernateTemplate, request,reportObj};
//                    Object result11 = objMethod1.invoke(invoker1, obj1);
//
//                    if(Boolean.parseBoolean(result11.toString())) {
                        while(temp.hasNext()) {
                            String refTName = (String) temp.next();
                            Object ttemp = (Object) invokerObj.get(refTName);

                            // set default values
                            Class cl2 = ttemp.getClass();
                            String[] namesArray = null;
                            Object[] namesValues = null;
                            java.util.Date timestamp1 = new java.util.Date();
                            if(refTName.equals(reportTName)) {//For grid/report table
                                namesArray = new String[5];
                                namesArray[0] = "createdby";namesArray[1] = "createddate"; namesArray[2] = "modifieddate"; namesArray[3] = "deleteflag"; namesArray[4] = ptablename+"id";
                                namesValues = new Object[5];
                                namesValues[0] = sessionHandlerDao.getUserid();namesValues[1] = timestamp1;namesValues[2] = timestamp1;namesValues[3] = 0.0; namesValues[4] = pInvoker;
                            } else { //For other table
                                namesArray = new String[1];
                                namesArray[0] = "modifieddate";
                                namesValues = new Object[1];
                                namesValues[0] = timestamp1;
                            }
                            for(int defaultCnt=0;defaultCnt < namesArray.length;defaultCnt++ )  {
                                Field field = ttemp.getClass().getDeclaredField(namesArray[defaultCnt].toString());
                                Class type = field.getType();
                                Class arguments[] = new Class[] {type};

                                java.lang.reflect.Method objMethod = cl2.getMethod("set"+TextFormatter.format(namesArray[defaultCnt], TextFormatter.G),arguments);
                                Object objVal = typeCastForValue(type.getName(),namesValues[defaultCnt]);
                                Object[] obj = new Object[]{objVal};
                                Object result1 = objMethod.invoke(ttemp, obj);
                            }

                            save(ttemp);
                        }
                        //Call after insert method
//                        objMethod1 = cl1.getMethod(afterEventName,arguments1);
//                        result11 = objMethod1.invoke(invoker1, obj1);
//                        if(!Boolean.parseBoolean(result11.toString())) {
////                            result = "{'success':false, 'msg':'Error occured at server.'}";
//                        }
//                    } else { //beforeInsert false
////                        result = "{'success':false, 'msg':'Error occured at server.'}";
//                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("reportMethods.insertRecord", e);
        }
    }


    public void insertGridRecord(String reportTName, JSONObject jobj, String userid,
            List list, Object[] row, HashMap<String, Object> invokerObj, HashMap<String, Class> clObj) throws ServiceException {
        try {
            String refTName = row[1].toString();
            String xtype = row[2].toString();
            String combogridconfig = "-1";
            if(row[3] != null)
                combogridconfig = row[3].toString();
            String fieldName = (String)row[0];

            if(jobj.has(fieldName)) {
                if(!xtype.equals("None")) {
                    Object invoker = null;
                    Class cl = null;
                    if(xtype.equals("Combobox")) {
                            //To do - Check if combobox is new or from other module or new combo from other module as combo in current module
                            //For this need to change the logic below.
                            // && !StringUtil.isNullOrEmpty(refTName)
                            if(combogridconfig.equals("-1")) { //Foreign key combo from other table
                                if(!clObj.containsKey(reportTName)) {
//                                            String id = refTName+PropsValues.REPORT_HARDCODE_STR+"id";
                                    String tablename = PropsValues.PACKAGE_PATH+"."+reportTName;
                                    cl = Class.forName(tablename);
                                    java.lang.reflect.Constructor co = cl.getConstructor();
                                    invoker = co.newInstance();
//                                            invoker = session.load(cl, jobj.getString(id));
                                    clObj.put(reportTName, cl);
                                    invokerObj.put(reportTName, invoker);
                                } else {
                                    cl = clObj.get(reportTName);
                                    invoker = invokerObj.get(reportTName);
                                }

                                String fkTableClassPath = PropsValues.PACKAGE_PATH+"."+refTName;
                                Class fkClass = Class.forName(fkTableClassPath);
                                try{
                                    Object fkObj = get(fkClass, jobj.getString(fieldName));

                                    String primaryCol = refTName+getPrimaryColName(refTName);
                                    Field  field = invoker.getClass().getDeclaredField(primaryCol);
                                    Class type = field.getType();
                                    Class  arguments[] = new Class[] {type};

                                    java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(primaryCol, TextFormatter.G),arguments);
                                    Object[] obj = new Object[]{fkObj};
                                    Object result1 = objMethod.invoke(invoker, obj);
                                }catch(Exception e){
                                    logger.warn(e.getMessage(), e);
                                }
                            } else { //New field is combo
                                String tname = reportTName;
                                if(!StringUtil.isNullOrEmpty(refTName)) {//New field of other table added as combo
                                    tname = refTName;
                                }
                                if(!invokerObj.containsKey(tname)) {
                                    String tablename = PropsValues.PACKAGE_PATH+"."+tname;
                                    cl = Class.forName(tablename);
                                    java.lang.reflect.Constructor co = cl.getConstructor();
                                    if(!tname.equals(reportTName)) {
                                        String id = tname+PropsValues.REPORT_HARDCODE_STR+"id";
                                        try {
                                            invoker = get(cl, jobj.getString(id));
                                        } catch (Exception e) {
                                            logger.warn(e.getMessage(), e);
                                        }
                                    } else {
                                        invoker = co.newInstance();
                                        }
                                    clObj.put(tname, cl);
                                    invokerObj.put(tname, invoker);
                                } else {
                                    cl = clObj.get(tname);
                                    invoker = invokerObj.get(tname);
                                }

                                String classfieldName = fieldName.split(PropsValues.REPORT_HARDCODE_STR)[1];
                                Field  field = invoker.getClass().getDeclaredField(classfieldName);
                                Class type = field.getType();
                                Class  arguments[] = new Class[] {type};

                                java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(classfieldName, TextFormatter.G),arguments);
                                Object objVal = typeCastForValue(type.getName(),jobj.getString(fieldName));
                                Object[] obj = new Object[]{objVal};
                                Object result1 = objMethod.invoke(invoker, obj);
                            }
                    } else { //Other than combobox
                        if(!invokerObj.containsKey(refTName)) {
                            String tablename = PropsValues.PACKAGE_PATH+"."+refTName;
                            cl = Class.forName(tablename);
                            java.lang.reflect.Constructor co = cl.getConstructor();
                            if(!refTName.equals(reportTName)) {
                                String id = refTName+PropsValues.REPORT_HARDCODE_STR+"id";
                                invoker = get(cl, jobj.getString(id));
                            } else {
                                invoker = co.newInstance();
//                                // set default values
//                                java.util.Date timestamp1 = new java.util.Date();
//                                String[] namesArray = new String[4];
//                                namesArray[0] = "createdby";namesArray[1] = "createddate"; namesArray[2] = "modifieddate"; namesArray[3] = "deleteflag";
//                                Object[] namesValues = new Object[4];
//                                namesValues[0] = userid;namesValues[1] = timestamp1;namesValues[2] = timestamp1;namesValues[3] = 0.0;
//                                for(int defaultCnt=0;defaultCnt < namesArray.length;defaultCnt++ )  {
//                                    Field  field = invoker.getClass().getDeclaredField(namesArray[defaultCnt].toString());
//                                    Class type = field.getType();
//                                    Class  arguments[] = new Class[] {type};
//
//                                    java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(namesArray[defaultCnt], TextFormatter.G),arguments);
//                                    Object objVal = moduleBuilderMethods.typeCastForValue(type.getName(),namesValues[defaultCnt]);
//                                    Object[] obj = new Object[]{objVal};
//                                    Object result1 = objMethod.invoke(invoker, obj);
//                                }
                            }
                            clObj.put(refTName, cl);
                            invokerObj.put(refTName, invoker);
                        } else {
                            cl = clObj.get(refTName);
                            invoker = invokerObj.get(refTName);
                        }

                        String classfieldName = fieldName.split(PropsValues.REPORT_HARDCODE_STR)[1];
                        Field  field = invoker.getClass().getDeclaredField(classfieldName);
                        Class type = field.getType();
                        Class  arguments[] = new Class[] {type};
                        if(!jobj.getString(fieldName).equals("")){
                            java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(classfieldName, TextFormatter.G),arguments);
                            Object objVal = typeCastForValue(type.getName(),jobj.getString(fieldName));
                            Object[] obj = new Object[]{objVal};
                            Object result1 = objMethod.invoke(invoker, obj);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("reportMethods.insertRecord", e);
        }
    }

    public void editGridRecord(String reportTName, JSONObject jobj, String userid,
            List list, Object[] row, HashMap<String, Object> invokerObj, HashMap<String, Class> clObj) throws ServiceException {
        try {
            String refTName = row[1].toString();
            String xtype = row[2].toString();
            String combogridconfig = "-1";
            if(row[3] != null)
                combogridconfig = row[3].toString();
            String fieldName = (String)row[0];

            if(jobj.has(fieldName)) {
                if(!xtype.equals("None")) {
                    Object invoker = null;
                    Class cl = null;
                    if(xtype.equals("Combobox")) {
                        if(!StringUtil.isNullOrEmpty(reportTName)) {
                            if(combogridconfig.equals("-1")) { //Foreign key combo from other table
                                if(!clObj.containsKey(reportTName)) {
                                    String id = reportTName+PropsValues.REPORT_HARDCODE_STR+"id";
                                    String tablename = PropsValues.PACKAGE_PATH+"."+reportTName;
                                    cl = Class.forName(tablename);
                                    invoker = get(cl, jobj.getString(id));
                                    clObj.put(reportTName, cl);
                                    invokerObj.put(reportTName, invoker);
                                } else {
                                    cl = clObj.get(reportTName);
                                    invoker = invokerObj.get(reportTName);
                                }
                                String fkTableClassPath = PropsValues.PACKAGE_PATH+"."+refTName;
                                Class fkClass = Class.forName(fkTableClassPath);
                                Object fkObj = get(fkClass, jobj.getString(fieldName));
                                String primaryCol = refTName+getPrimaryColName(refTName);

                                Field  field = invoker.getClass().getDeclaredField(primaryCol);
                                Class type = field.getType();
                                Class  arguments[] = new Class[] {type};

                                java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(primaryCol, TextFormatter.G),arguments);
                                Object[] obj = new Object[]{fkObj};
                                Object result1 = objMethod.invoke(invoker, obj);
                            } else { //New field is combo
                                String tname = reportTName;
                                if(!StringUtil.isNullOrEmpty(refTName)) {//New field of other table added as combo
                                    tname = refTName;
                                }
                                if(!invokerObj.containsKey(tname)) {
                                    String tablename = PropsValues.PACKAGE_PATH+"."+tname;
                                    cl = Class.forName(tablename);
//                                            java.lang.reflect.Constructor co = cl.getConstructor();
                                    String id = tname+PropsValues.REPORT_HARDCODE_STR+"id";
                                    invoker = get(cl, jobj.getString(id));
                                    clObj.put(tname, cl);
                                    invokerObj.put(tname, invoker);
                                } else {
                                    cl = clObj.get(tname);
                                    invoker = invokerObj.get(tname);
                                }

                                String classfieldName = fieldName.split(PropsValues.REPORT_HARDCODE_STR)[1];
                                Field  field = invoker.getClass().getDeclaredField(classfieldName);
                                Class type = field.getType();
                                Class  arguments[] = new Class[] {type};

                                java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(classfieldName, TextFormatter.G),arguments);
                                Object objVal = typeCastForValue(type.getName(),jobj.getString(fieldName));
                                Object[] obj = new Object[]{objVal};
                                Object result1 = objMethod.invoke(invoker, obj);
                            }
                        }
                    } else { //Other than combobox
                        if(!invokerObj.containsKey(refTName)) {
                            String id = refTName+PropsValues.REPORT_HARDCODE_STR+"id";
                            String tablename = PropsValues.PACKAGE_PATH+"."+refTName;
                            cl = Class.forName(tablename);
//                                    java.lang.reflect.Constructor co = cl.getConstructor();
                            invoker = get(cl, jobj.getString(id));
                            clObj.put(refTName, cl);
                            invokerObj.put(refTName, invoker);
                        } else {
                            cl = clObj.get(refTName);
                            invoker = invokerObj.get(refTName);
//                            // update default values
//                            Field  field = invoker.getClass().getDeclaredField("modifieddate");
//                            Class type = field.getType();
//                            Class  arguments[] = new Class[] {type};
//                            java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format("modifieddate", TextFormatter.G),arguments);
//                            Object objVal = moduleBuilderMethods.typeCastForValue(type.getName(),new java.util.Date());
//                            Object[] obj = new Object[]{objVal};
//                            Object result1 = objMethod.invoke(invoker, obj);
                        }

                        String classfieldName = fieldName.split(PropsValues.REPORT_HARDCODE_STR)[1];
                        Field  field = invoker.getClass().getDeclaredField(classfieldName);
                        Class type = field.getType();
                        Class  arguments[] = new Class[] {type};

                        java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(classfieldName, TextFormatter.G),arguments);
                        Object objVal = typeCastForValue(type.getName(),jobj.getString(fieldName));
                        Object[] obj = new Object[]{objVal};
                        Object result1 = objMethod.invoke(invoker, obj);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("reportMethods.editRecord", e);
        }
    }

    public String getPrimaryColName(String className) {
        //TODO replace if required
        return "";
    }

    public Configuration getConfig(){
        //TODO replace if required
        return null;
    }
}
