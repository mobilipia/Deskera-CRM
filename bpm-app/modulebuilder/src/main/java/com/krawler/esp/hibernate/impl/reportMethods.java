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
package com.krawler.esp.hibernate.impl;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.utils.PropsValues;
import com.krawler.esp.database.ReportHandlers;
import com.krawler.formbuilder.servlet.ReportBuilderDao;
import com.krawler.portal.util.TextFormatter;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.lang.reflect.Field;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.*;
import java.util.*;
import java.util.ArrayList;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class reportMethods extends ReportHandlers {
    private ReportBuilderDao reportDao;

    public reportMethods(ReportBuilderDao reportDao, sessionHandlerImpl sessionHandlerDao) {
        super();
        this.reportDao = reportDao;
        this.sessionHandlerDao = sessionHandlerDao;
    }

    public String loadData(HttpServletRequest request) throws ServiceException {
        String result = "{data:[],count : 0}";
        try {
            String reportid = request.getParameter("reportid");
            String query = null;
            Configuration cfg = getConfig();
            java.util.Iterator itr = cfg.getTableMappings();
            HashMap<String, Table> tableObj = new HashMap<String, Table> ();
            while(itr.hasNext()) {
                Table table = (Table)itr.next();
                tableObj.put(PropsValues.PACKAGE_PATH+"."+table.getName(), table);
            }
            //Fetch all reference tables in report
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
            query = "Select distinct mb_gridconfig.reftable as tablename from "+PropsValues.PACKAGE_PATH+".mb_gridconfig as mb_gridconfig where mb_gridconfig.reportid = ? and mb_gridconfig.countflag = ? ";
            List ls = find(query, new Object[] {report, false});
            java.util.Iterator ite = ls.iterator();
            String str = "";
            ArrayList<String> refTableList1 = new ArrayList<String> ();
            HashMap<String, String> shortTableNames = new HashMap<String, String> ();
            while(ite.hasNext()) {
                String reftablename = (String) ite.next();
                if(!StringUtil.isNullOrEmpty(reftablename)) {
                    refTableList1.add(PropsValues.PACKAGE_PATH+"."+reftablename);
                    shortTableNames.put(PropsValues.PACKAGE_PATH+"."+reftablename, reftablename);
                }
            }
            //If new table created not present in arraylist then it is added forcefully
            String reportTName = reportDao.getReportTableName(reportid);
            if(!StringUtil.isNullOrEmpty(reportTName) && !refTableList1.contains(PropsValues.PACKAGE_PATH+"."+reportTName)) {
                refTableList1.add(PropsValues.PACKAGE_PATH+"."+reportTName);
                shortTableNames.put(PropsValues.PACKAGE_PATH+"."+reportTName, reportTName);
            }

            ArrayList<String> refTableList = new ArrayList<String> ();
            ArrayList<String> primaryTableList = new ArrayList<String> ();
            HashMap<String, ArrayList<String>> parentTableObj = new HashMap<String, ArrayList<String>> ();
            HashMap<String, ArrayList<String>> colNameObj = new HashMap<String, ArrayList<String>> ();
            //Make list of reference tables which does not have any relationship with another ref. tables
            //Sort all reference tables in relationship order i.e. form child - parent
            for(int i=0; i < refTableList1.size(); i++) {
                String reftablename =refTableList1.get(i);
                Table tb = tableObj.get(reftablename);
                Iterator FkIte = tb.getForeignKeyIterator();
                if(FkIte.hasNext()) {
                    boolean flg = false;
                    ArrayList<String> parentTName1 = new ArrayList<String> ();
                    ArrayList<String> colName1 = new ArrayList<String> ();
                    while(FkIte.hasNext()) { //Always keep foreign key name = parent tablename + primary column name
                        ForeignKey obj = (ForeignKey) FkIte.next();
                        String parentTName = obj.getReferencedEntityName();
                        Column col = (Column) obj.getColumns().iterator().next();
                        String colName = col.getName();
                        if(refTableList1.contains(parentTName)) {
                            if(primaryTableList.contains(parentTName)) {
                                primaryTableList.remove(parentTName);
                            }
                            if(refTableList.contains(parentTName)) {
                                refTableList.remove(parentTName);
                                if(!refTableList.contains(reftablename)) {
                                    refTableList.add(reftablename);
                                }
                                refTableList.add(parentTName);
                            } else if(!refTableList.contains(reftablename)) {
                                refTableList.add(reftablename);
                            }
                            parentTName1.add(parentTName);
                            colName1.add(colName);

                            flg = true;
                        } else {
                            if(!refTableList.contains(reftablename)) {
                                refTableList.add(reftablename);
                                flg = true;
                            }
                            continue;
                        }
                    }

                    parentTableObj.put(reftablename, parentTName1);
                    colNameObj.put(reftablename, colName1);

                    if(!flg) {
                        primaryTableList.add(reftablename);
                    }
                } else if(!FkIte.hasNext()) {
                    //Check whether reference table is already part of parentTableObj.
                    //If yes then not to add in primaryList
                    boolean flg = true;
                    Iterator temp = parentTableObj.keySet().iterator();
                    while(temp.hasNext()) {
                        ArrayList<String> ttemp = (ArrayList<String>) parentTableObj.get(temp.next());
                        for(int l = 0; l < ttemp.size(); l++) {
                            if(ttemp.contains(reftablename)) {
                                flg = false;
                                break;
                            }
                        }
                        if(!flg) {
                            break;
                        }
                    }
                    if(flg) {
                        primaryTableList.add(reftablename);
                    }
                }
            }
            String delQuery = "";
            for(int i=0; i < refTableList.size(); i++) {
                String reftablename =refTableList.get(i);
                ArrayList<String> parentTName = parentTableObj.get(reftablename);
                ArrayList<String> colName = colNameObj.get(reftablename);

                for(int j = 0; j < parentTName.size(); j++) {
                   String shortRefTName = shortTableNames.get(reftablename);
                   String shortParentTName = shortTableNames.get(parentTName.get(j));
                   if(StringUtil.isNullOrEmpty(str)) {
                        str += reftablename + " as " + shortRefTName + " inner join " + shortRefTName + "."+colName.get(j) + " as " + shortParentTName;
                        delQuery += shortRefTName +".deleteflag = 0 and "+shortParentTName+".deleteflag = 0 ";
                   } else if(str.contains(shortRefTName)){
                       //if reference table is already part of query then use alias
                        str += " inner join " + shortRefTName + "."+colName.get(j)+ " as " + shortParentTName ;
                        if(!delQuery.contains(shortParentTName)) {
                            delQuery += " and "+shortParentTName+".deleteflag = 0 ";
                        }
                   } else {
                       //otherwise use full path
                        str += " inner join " + reftablename + "."+colName.get(j)+ " as " + shortParentTName ;
                        if(!delQuery.contains(shortParentTName)) {
                            delQuery += " and "+shortParentTName+".deleteflag = 0 ";
                        }
                   }
                }
            }

            String primaryTableQuery = "";
            for(int i=0; i<primaryTableList.size(); i++) {
                String reftablename = primaryTableList.get(i);
                String shortRefTName = shortTableNames.get(reftablename);

                if(StringUtil.isNullOrEmpty(delQuery)) {
                   delQuery += shortRefTName +".deleteflag = 0 ";
                } else if(!delQuery.contains(shortRefTName)) {
                   delQuery += " and " + shortRefTName+".deleteflag = 0 ";
                }

                if(StringUtil.isNullOrEmpty(primaryTableQuery)) {
                    primaryTableQuery += " from "+ reftablename+ " as " + shortRefTName;
                } else {
                    primaryTableQuery += " , "+ reftablename+ " as " + shortRefTName;
                }
            }
            String finalQuery = "";
            if(!StringUtil.isNullOrEmpty(primaryTableQuery)) {
                finalQuery += primaryTableQuery;
            }
            if(!StringUtil.isNullOrEmpty(str)) {
                if(StringUtil.isNullOrEmpty(finalQuery)) {
                   finalQuery = " from "+ str;
                } else {
                   finalQuery += ", " + str;
                }
            }

            //Fetch all display columns
            String SELECT_QUERY = "select mb_gridconfig.name, mb_gridconfig.xtype, mb_gridconfig.combogridconfig, mb_gridconfig.countflag, mb_gridconfig.reftable from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig" +
                    " where mb_gridconfig.reportid = ?";
            List list = find(SELECT_QUERY, new Object[]{report});
            ite = list.iterator();
            String fieldQuery = "";
            ArrayList<String> fieldNameArray = new ArrayList<String> ();
            ArrayList<String> countFieldNameArray = new ArrayList<String> ();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                String fieldName = row[0].toString();
                String xtype = row[1].toString();
                String combogridconfig = "-1";
                if(row[2] != null) {
                    combogridconfig = row[2].toString();
                }
                //Check for count flag
                if(row[3] != null && Boolean.parseBoolean(row[3].toString())) {//countFlag
                    countFieldNameArray.add(row[4].toString());
                } else {
                    fieldNameArray.add(fieldName);
                    String className = "";
                    if(xtype.equals("Combobox") && combogridconfig.equals("-1")) {
                        className = fieldName.split(PropsValues.REPORT_HARDCODE_STR)[0];
                        fieldName = className+"."+getPrimaryColName(className);
                    } else {
                        fieldName = fieldName.replaceAll(PropsValues.REPORT_HARDCODE_STR, ".");
                    }
                        fieldQuery += fieldName+",";
                }
            }

            if(StringUtil.isNullOrEmpty(fieldQuery)) {
                fieldQuery = "Select * ";
            } else {
                fieldQuery = "Select "+fieldQuery.substring(0, fieldQuery.length()-1);
            }

            //Check for mapping filter
            String filterQry = "";
            if(Boolean.parseBoolean(request.getParameter("isFilter"))) {
//                filterQry = " "+reportTName+"."+request.getParameter("filterfield").split(PropsValues.REPORT_HARDCODE_STR)[1]+" = '"+request.getParameter("filtervalue")+"'";
                filterQry = request.getParameter("filterfield").replaceAll(PropsValues.REPORT_HARDCODE_STR, ".")+" = '"+request.getParameter("filtervalue")+"'";
            }

            if(!StringUtil.isNullOrEmpty(delQuery)) {
                if(!StringUtil.isNullOrEmpty(filterQry)) {
                    delQuery = " where "+filterQry+" and "+delQuery;
                } else {
                    delQuery = " where "+delQuery;
                }
            }

            //Check for comments
            SELECT_QUERY = "select mb_moduleConfigMap.configid.configid from com.krawler.esp.hibernate.impl.mb_moduleConfigMap " +
                    "as mb_moduleConfigMap where mb_moduleConfigMap.moduleid = ? ";
            List configlist = find(SELECT_QUERY, new Object[]{report});
            ite = configlist.iterator();
            boolean commentFlag = false;
            boolean docFlag = false;
            while( ite.hasNext() ) {
                int configid = (Integer) ite.next();
                if(configid == 1) { // Comments
                    commentFlag = true;
                } else if(configid == 2) { // Documents
                    docFlag = true;
                }
            }

            JSONObject jobj = new JSONObject();
            int count = 0;

            String searchFilter="";

            if (request.getParameter("filterJson") != null && !request.getParameter("filterJson").equals("")) {
                JSONArray filterJsonObj = new JSONArray(request.getParameter("filterJson"));
                JSONObject ObjJSONObject = null;
                String substr = "";
                String reportClassName="";
                
                for (int i = 0; i < filterJsonObj.length(); i++) {
                    ObjJSONObject = (JSONObject) filterJsonObj.get(i);
                    reportClassName=ObjJSONObject.getString("column").split(PropsValues.REPORT_HARDCODE_STR)[0];
                    if (ObjJSONObject.getString("xtype").equals("Date")) {
                            String[] splitString=ObjJSONObject.getString("searchText").split(",");
                            String fromDate=splitString[0];
                            String toDate=splitString[1];
                            substr =" >= '" + fromDate + " 00:00:00" + "'"+" and " + reportClassName + "." + ObjJSONObject.getString("column").split(PropsValues.REPORT_HARDCODE_STR)[1]+ " <= '" + toDate + " 00:00:00" + "'";
                    } else if (ObjJSONObject.getString("xtype").equals("Number(Integer)") || ObjJSONObject.getString("xtype").equals("Number(Float)")) {
                        substr = " = " + ObjJSONObject.getString("searchText");

                    } else if (ObjJSONObject.getString("xtype").equals("radio") || ObjJSONObject.getString("xtype").equalsIgnoreCase("checkbox")) {
                        substr = " = " + Boolean.parseBoolean(ObjJSONObject.getString("searchText"));
                    }else if (ObjJSONObject.getString("xtype").equals("Combobox")) {
                            substr = " = '" + ObjJSONObject.getString("searchText") + "'";
                            
                        if (!ObjJSONObject.getString("combogridconfig").equals("-1")) {
                            searchFilter += " and " + reportClassName + "." + ObjJSONObject.getString("column").split(PropsValues.REPORT_HARDCODE_STR)[1] + substr;
                        } else {
                            if (reportTName.equals("")) {
                                searchFilter += " and " + reportClassName + ".id" + substr;
                            } else {
                                searchFilter += " and " + reportClassName + "id" + substr;
                            }
                        }
                            
                    } else {
                        substr = " like '%" + ObjJSONObject.getString("searchText") + "%'";
                    }
                    if (!ObjJSONObject.getString("xtype").equals("Combobox")) {
                        searchFilter += " and " + reportClassName + "." + ObjJSONObject.getString("column").split(PropsValues.REPORT_HARDCODE_STR)[1] + substr;
                    }
                }
            }

            //Generate rules filter query for report grid
            String ruleFilterQuery = "";
//            ArrayList permArray = AuthHandler.getRealRoleids(request);
//            for(int i = 0; i < permArray.size(); i++) {
//                int roleid = Integer.parseInt(permArray.get(i).toString());
//                String res = ModuleBuilderController.checkFilterRulesQuery(session, report, roleid, 0, "");
//                if(!StringUtil.isNullOrEmpty(res)) {
//                    res = "("+res +")";
//                    if(!StringUtil.isNullOrEmpty(ruleFilterQuery))
//                        ruleFilterQuery = res + " or " + ruleFilterQuery;
//                    else
//                        ruleFilterQuery = res;
//                }
//            }
            if(!StringUtil.isNullOrEmpty(ruleFilterQuery))
                ruleFilterQuery = " and " + ruleFilterQuery;
            String sortQuery = "";
            if(request.getParameter("sort")!=null && !request.getParameter("sort").equals("")){
                 String sortColumnName = request.getParameter("sort").replaceAll(PropsValues.REPORT_HARDCODE_STR, ".");
                sortQuery = " Order by  "+sortColumnName+" "+request.getParameter("dir");
            }
            Object[] paramArray = null;
            if(!StringUtil.isNullOrEmpty(finalQuery)) {
                 //Get implementation class object and call before dataLoad method.
                //Check for reportTName is null;
                String className = reportTName;
                if(StringUtil.isNullOrEmpty(className)) {
                    className = "rb_"+reportDao.toLZ(report.getReportkey(), 3)+"_"+report.getReportname().replace(" ", "").toLowerCase();;
                }
                Class cl1 = Class.forName(PropsValues.PACKAGE_PATH+".impl_"+className);
                java.lang.reflect.Constructor co1 = cl1.getConstructor();
                Object invoker1 = co1.newInstance();
                Class  arguments1[] = new Class[] {HibernateTemplate.class, HttpServletRequest.class,String.class,String.class,String.class,String.class,
                                String.class,String.class,String.class,ArrayList.class,ArrayList.class,Boolean.class,Boolean.class,JSONObject.class,Boolean.class,Object[].class};

                java.lang.reflect.Method objMethod1 = cl1.getMethod("beforeGridLoadData",arguments1);
                Object[] obj1 = new Object[]{getHibernateTemplate(),request,finalQuery,fieldQuery,delQuery,searchFilter,ruleFilterQuery,
                                                    sortQuery,reportTName,fieldNameArray,countFieldNameArray,commentFlag,docFlag, jobj, true, paramArray};
                Object result11 = objMethod1.invoke(invoker1, obj1);
            }
            if(jobj.has("data")) {
                jobj.put("count", count);
                result = jobj.toString();
            }else{
                 result = "{data:[],count : 0}";
            }

        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            result = "{data:[],count : 0}";
            throw ServiceException.FAILURE("reportMethods.loadData", e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            result = "{data:[],count : 0}";
            throw ServiceException.FAILURE("reportMethods.loadData", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{data:[],count : 0}";
            throw ServiceException.FAILURE("reportMethods.loadData", e);
        } finally {
            return result;
        }
    }

    public void getCountCommentDocuments(ArrayList<String> countFieldNameArray, String reportTName,
            Object invoker, boolean commentFlag, boolean docFlag, String idVal, JSONObject jtemp) throws ServiceException {
        try{
            //Create count query
            for(int i = 0; i < countFieldNameArray.size(); i++) {
                String hql = "Select count(id) as count from "+ PropsValues.PACKAGE_PATH+"."+ countFieldNameArray.get(i) + " where "+ reportTName +"id = ?";
                List ls1 = find(hql, new Object[] {invoker});
                Iterator ite1 = ls1.iterator();
                if(ite1.hasNext()) {
                    long count1 = Long.parseLong(ite1.next().toString());
                    jtemp.put(countFieldNameArray.get(i)+PropsValues.REPORT_HARDCODE_STR+"id", count1);
                }
            }
            if(commentFlag){ //Comments
                String commentQuery = "Select count(comments.id) from com.krawler.esp.hibernate.impl.comments as comments where comments.recordid = ? ";
                List ls1 = find(commentQuery, new Object[] {idVal});
                Iterator ite1 = ls1.iterator();
                if (ite1.hasNext()) {
                    long count1 = Long.parseLong(ite1.next().toString());
                    String comments = "<img src='images/comment.gif' class='showComments' alt='sorry cannot load image' style='cursor: pointer;vertical-align:bottom;' title='Click to see comments'>" +
                            " <span style='text-align: center !important; font-size:10px !' >(" + count1 + ")</span>";
                    jtemp.put("comments", comments);
                }
            }
            if(docFlag) { // Documents
                 long docCount = getdocCount(idVal);
                 String dwd = "<img src='images/document12.gif' class='showDocuments' style='cursor: pointer;vertical-align:bottom;' title='Click to download'>" +
                             " <span style='text-align: center !important; font-size:10px !' id = 'doccount_"+idVal+"'>("+docCount+")</span>";
                 jtemp.put("docs_id",dwd);
            }
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("reportMethods.getCountQueryData", ex);
        }
    }

     public static ArrayList<String> getSearchfield(HibernateTemplate hibernateTemplate,String moduleid,String repTableName) throws ServiceException{
        String result = "";
        String query = "from "+PropsValues.PACKAGE_PATH+".mb_gridconfig as mb_gridconfig  where mb_gridconfig.reportid = ? and mb_gridconfig.hidden !=?";
        mb_reportlist obj = (mb_reportlist) hibernateTemplate.get(mb_reportlist.class, moduleid);
        List li = hibernateTemplate.find(query,new Object[] {obj,true});
        ArrayList<String> al = new ArrayList<String>();
        Iterator ite = li.iterator();
        while(ite.hasNext()){
            mb_gridconfig gridConfigObj = (mb_gridconfig) ite.next();
            if(gridConfigObj.getXtype().equals("Combobox") && gridConfigObj.getReftable().equals("")){
                al.add("mb_configmasterdata.masterdata");
            }else {
                String colName = gridConfigObj.getName().replaceAll(PropsValues.REPORT_HARDCODE_STR,".");
                al.add(colName);
            }
        }
        return al;
    }
    public String insertRecord(HttpServletRequest request) {
        String result = "{'success':true, 'msg':'Record inserted successfully.'}";
        java.util.Iterator ite = null;
        try {
            String reportid = request.getParameter("reportid");
            String reportTName = reportDao.getReportTableName(reportid);
            HashMap<String, Object> invokerObj = new HashMap<String, Object> ();
            HashMap<String, Class> clObj = new HashMap<String, Class> ();

           
            
                String jsonstr = request.getParameter("jsondata");

                mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
                    String SELECT_QUERY = "select mb_gridconfig.name, mb_gridconfig.reftable, mb_gridconfig.xtype, mb_gridconfig.combogridconfig from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig" +
                            " where mb_gridconfig.reportid = ? ";
                List list = find(SELECT_QUERY, new Object[]{report});

                JSONArray jsonArray = new JSONArray(jsonstr);
                JSONObject jobj = new JSONObject();
                if(!StringUtil.isNullOrEmpty(reportTName)) {
                    for (int k = 0; k < jsonArray.length(); k++) {
                        jobj = jsonArray.getJSONObject(k);
                        ite = list.iterator();
                        while( ite.hasNext() ) {
                            Object[] row = (Object[]) ite.next();

                            insertGridRecord(reportTName, jobj, sessionHandlerDao.getUserid(), list,
                                        row, invokerObj, clObj);
                        }
                    }

                    Iterator temp = invokerObj.keySet().iterator();
                     //Get implementation class object and call before insert method.
                    Class cl1 = Class.forName(PropsValues.PACKAGE_PATH+".impl_"+reportTName);
                    java.lang.reflect.Constructor co1 = cl1.getConstructor();
                    Object invoker1 = co1.newInstance();
                    Class  arguments1[] = new Class[] {HibernateTemplate.class, HttpServletRequest.class,Object.class};

                    java.lang.reflect.Method objMethod1 = cl1.getMethod("beforeInsert",arguments1);
                    Object reportObj = invokerObj.get(reportTName);
                    Object[] obj1 = new Object[]{getHibernateTemplate(), request,reportObj};
                    Object result11 = objMethod1.invoke(invoker1, obj1);

                    if(Boolean.parseBoolean(result11.toString())) {
                        while(temp.hasNext()) {
                            String refTName = (String) temp.next();
                            Object ttemp = (Object) invokerObj.get(refTName);

                            // set default values
                            Class cl = ttemp.getClass();
                            String[] namesArray = null;
                            Object[] namesValues = null;
                            java.util.Date timestamp1 = new java.util.Date();
                            if(refTName.equals(reportTName)) {//For grid/report table
                                namesArray = new String[4];
                                namesArray[0] = "createdby";namesArray[1] = "createddate"; namesArray[2] = "modifieddate"; namesArray[3] = "deleteflag";
                                namesValues = new Object[4];
                                namesValues[0] = sessionHandlerDao.getUserid();namesValues[1] = timestamp1;namesValues[2] = timestamp1;namesValues[3] = 0.0;
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

                                java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(namesArray[defaultCnt], TextFormatter.G),arguments);
                                Object objVal = typeCastForValue(type.getName(),namesValues[defaultCnt]);
                                Object[] obj = new Object[]{objVal};
                                Object result1 = objMethod.invoke(ttemp, obj);
                            }

                            save(ttemp);
                        }
//                        String actionType = "Add Report Data";
//                        String reportName = AuditTrialHandler.getReportName(session, reportid);
//                        String details = "New record added in "+reportName;
//                        long actionId = AuditTrialHandler.getActionId(session, actionType);
//                        AuditTrialHandler.insertAuditLog(session, actionId, details, request);
//                         //Call after insert method
                        objMethod1 = cl1.getMethod("afterInsert",arguments1);
                        result11 = objMethod1.invoke(invoker1, obj1);
                        if(!Boolean.parseBoolean(result11.toString())) {
                            result = "{'success':false, 'msg':'Error occured at server.'}";
                        }
                    } else { //beforeInsert false
                        result = "{'success':false, 'msg':'Error occured at server.'}";
                    }
                }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{'success':false, 'msg':'Error occured at server.'}";
            throw ServiceException.FAILURE("reportMethods.insertRecord", e);
        } finally {
            return result;
        }
    }

    public String editRecord(HttpServletRequest request) {
        String result = "{'success':true, 'msg':'Record edited successfully.'}";
        java.util.Iterator ite = null;
        try {
            String reportid = request.getParameter("reportid");
            String reportTName = reportDao.getReportTableName(reportid);
            HashMap<String, Object> invokerObj = new HashMap<String, Object> ();
            HashMap<String, Class> clObj = new HashMap<String, Class> ();

            
                String jsonstr = request.getParameter("jsondata");

                mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
                    String SELECT_QUERY = "select mb_gridconfig.name, mb_gridconfig.reftable, mb_gridconfig.xtype, mb_gridconfig.combogridconfig from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig" +
                            " where mb_gridconfig.reportid = ? ";
                List list = find(SELECT_QUERY, new Object[]{report});

                JSONArray jsonArray = new JSONArray(jsonstr);
                JSONObject jobj = new JSONObject();
                for (int k = 0; k < jsonArray.length(); k++) {
                    jobj = jsonArray.getJSONObject(k);
                    ite = list.iterator();
                    while( ite.hasNext() ) {
                        Object[] row = (Object[]) ite.next();

                        editGridRecord(reportTName, jobj, sessionHandlerDao.getUserid(), list,
                                        row, invokerObj, clObj);
                    }
                }

                Iterator temp = invokerObj.keySet().iterator();

                Object result11 = "false";
                Object invoker1 = null;
                Class cl1 = null;
                java.lang.reflect.Method objMethod1 = null;
                Object[] obj1 = null;
                //Check for reportTName is null;
                String className = reportTName;
                Object reportObj = null;
                if(StringUtil.isNullOrEmpty(className)) {
                    className = "rb_"+reportDao.toLZ(report.getReportkey(), 3)+"_"+report.getReportname().replace(" ", "").toLowerCase();
                } else {
                    reportObj = invokerObj.get(reportTName);
                }
                //Get implementation class object and call before insert method.
                cl1 = Class.forName(PropsValues.PACKAGE_PATH+".impl_"+className);
                java.lang.reflect.Constructor co1 = cl1.getConstructor();
                invoker1 = co1.newInstance();
                Class  arguments1[] = new Class[] {HibernateTemplate.class, HttpServletRequest.class,Object.class};
                objMethod1 = cl1.getMethod("beforeEdit",arguments1);
                obj1 = new Object[]{getHibernateTemplate(), request,reportObj};
                result11 = objMethod1.invoke(invoker1, obj1);                
                
                if(Boolean.parseBoolean(result11.toString())) {
                    while(temp.hasNext()) {
                        String refTName = (String) temp.next();
                        Object ttemp = (Object) invokerObj.get(refTName);

                        //Update modified date
                        Class cl = ttemp.getClass();
                        Field  field = ttemp.getClass().getDeclaredField("modifieddate");
                        Class type = field.getType();
                        Class  arguments[] = new Class[] {type};
                        java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format("modifieddate", TextFormatter.G),arguments);
                        Object objVal = typeCastForValue(type.getName(),new java.util.Date());
                        Object[] obj = new Object[]{objVal};
                        Object result1 = objMethod.invoke(ttemp, obj);
                        
                        saveOrUpdate(ttemp);
                    }
//                    String actionType = "Edit Report Data";
//                    String reportName = AuditTrialHandler.getReportName(session, reportid);
//                    String details = "Record edited in "+reportName;
//                    long actionId = AuditTrialHandler.getActionId(session, actionType);
//                    AuditTrialHandler.insertAuditLog(session, actionId, details, request);

                    //Call afterEdit method of report class.
                    arguments1 = new Class[] {Session.class, HttpServletRequest.class,Object.class};
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
            throw ServiceException.FAILURE("reportMethods.editRecord", e);
        } finally {
            return result;
        }
    }

    public String deleteRecord(HttpServletRequest request) {
        String result = "{'success':true, 'msg':'Record deleted successfully.'}";
        try {
            String reportid = request.getParameter("reportid");
            String reportTName = reportDao.getReportTableName(reportid);
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
                String id = request.getParameter("id");
                String tablename = PropsValues.PACKAGE_PATH+"."+reportTName;
                Class cl = Class.forName(tablename);
                Object invoker = get(cl, id);

                String classfieldName = "deleteflag";
                Field field = invoker.getClass().getDeclaredField(classfieldName);
                Class type = field.getType();
                Class arguments[] = new Class[] {type};

                java.lang.reflect.Method objMethod = cl.getMethod("set"+TextFormatter.format(classfieldName, TextFormatter.G),arguments);
                Object objVal = typeCastForValue(type.getName(),"1");
                Object[] obj = new Object[]{objVal};
                Object result1 = objMethod.invoke(invoker, obj);

                Object result11 = "false";
                Object invoker1 = null;
                Class cl1 = null;
                java.lang.reflect.Method objMethod1 = null;
                Object[] obj1 = null;
                //Check for reportTName is null;
                String className = reportTName;
                if(StringUtil.isNullOrEmpty(className)) {
                    className = "rb_"+reportDao.toLZ(report.getReportkey(), 3)+"_"+report.getReportname().replace(" ", "").toLowerCase();
                }

                //Get implementation class object and call before insert method.
                cl1 = Class.forName(PropsValues.PACKAGE_PATH+".impl_"+className);
                java.lang.reflect.Constructor co1 = cl1.getConstructor();
                invoker1 = co1.newInstance();
                Class  arguments1[] = new Class[] {HibernateTemplate.class, HttpServletRequest.class,Object.class};

                objMethod1 = cl1.getMethod("beforeDelete",arguments1);
                obj1 = new Object[]{getHibernateTemplate(), request,invoker};
                result11 = objMethod1.invoke(invoker1, obj1);
                
                if(Boolean.parseBoolean(result11.toString())) {
                    saveOrUpdate(invoker);

                    //call after delete event
                    arguments1 = new Class[] {Session.class, HttpServletRequest.class,Object.class};                    
                    objMethod1 = cl1.getMethod("afterDelete",arguments1);
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
            throw ServiceException.FAILURE("reportMethods.editRecord", e);
        } finally {
            return result;
        }
    }

    //Set/create new object/record
    public boolean setObject(String className, String jsondata, HttpServletRequest request) throws ServiceException {
        boolean result = false;
        try{
            Class cl1 = Class.forName(PropsValues.PACKAGE_PATH+className);
            java.lang.reflect.Constructor co1 = cl1.getConstructor();
            Object invoker1 = co1.newInstance();

            java.util.Date timestamp1 = new java.util.Date();
            String[] namesArray = new String[4];
            namesArray[0] = "createdby";namesArray[1] = "createddate"; namesArray[2] = "modifieddate"; namesArray[3] = "deleteflag";
            Object[] namesValues = new Object[4];
            namesValues[0] = sessionHandlerDao.getUserid();namesValues[1] = timestamp1;namesValues[2] = timestamp1;namesValues[3] = 0.0;

            for(int defaultCnt=0;defaultCnt < namesArray.length;defaultCnt++ )  {
                Field field = cl1.getDeclaredField(namesArray[defaultCnt].toString());
                Class type = field.getType();
                Class arguments[] = new Class[] {type};

                java.lang.reflect.Method objMethod = cl1.getMethod("set"+TextFormatter.format(namesArray[defaultCnt], TextFormatter.G),arguments);
                Object objVal = typeCastForValue(type.getName(),namesValues[defaultCnt]);
                Object[] obj = new Object[]{objVal};
                Object result1 = objMethod.invoke(invoker1, obj);
            }
            
            JSONArray jobj = new JSONArray(jsondata);
            for(int i = 0; i < jobj.length(); i++) {
                String key = jobj.getJSONObject(i).get("key").toString();
                Object value = jobj.getJSONObject(i).get("value");

                Field field = cl1.getDeclaredField(key);
                Class type = field.getType();
                Class arguments[] = new Class[] {type};

                java.lang.reflect.Method objMethod = cl1.getMethod("set"+TextFormatter.format(key, TextFormatter.G),arguments);
                Object objVal = typeCastForValue(type.getName(),value);
                Object[] obj = new Object[]{objVal};
                Object result1 = objMethod.invoke(invoker1, obj);
            }

            result = true;
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            result = false;
            throw ServiceException.FAILURE("reportMethods.setObj", e);
        }
        return result;
    }

     //Set/create new object/record
    public boolean updateObject(String className, String jsondata, String id, HttpServletRequest request) throws ServiceException {
        boolean result = false;
        try{
            Class cl1 = Class.forName(PropsValues.PACKAGE_PATH+className);
            Object invoker1 = get(cl1, id);

            java.util.Date timestamp1 = new java.util.Date();
            String[] namesArray = new String[1];
            namesArray[0] = "modifieddate";
            Object[] namesValues = new Object[1];
            namesValues[1] = timestamp1;

            for(int defaultCnt=0;defaultCnt < namesArray.length;defaultCnt++ )  {
                Field field = cl1.getDeclaredField(namesArray[defaultCnt].toString());
                Class type = field.getType();
                Class arguments[] = new Class[] {type};

                java.lang.reflect.Method objMethod = cl1.getMethod("set"+TextFormatter.format(namesArray[defaultCnt], TextFormatter.G),arguments);
                Object objVal = typeCastForValue(type.getName(),namesValues[defaultCnt]);
                Object[] obj = new Object[]{objVal};
                Object result1 = objMethod.invoke(invoker1, obj);
            }

            JSONArray jobj = new JSONArray(jsondata);
            for(int i = 0; i < jobj.length(); i++) {
                String key = jobj.getJSONObject(i).get("key").toString();
                Object value = jobj.getJSONObject(i).get("value");

                Field field = cl1.getDeclaredField(key);
                Class type = field.getType();
                Class arguments[] = new Class[] {type};

                java.lang.reflect.Method objMethod = cl1.getMethod("set"+TextFormatter.format(key, TextFormatter.G),arguments);
                Object objVal = typeCastForValue(type.getName(),value);
                Object[] obj = new Object[]{objVal};
                Object result1 = objMethod.invoke(invoker1, obj);
            }

            result = true;
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            result = false;
            throw ServiceException.FAILURE("reportMethods.setObj", e);
        }
        return result;
    }

      //Set/create new object/record
    public boolean deleteObject(String className, String id, HttpServletRequest request) throws ServiceException {
        boolean result = false;
        try{
            Class cl1 = Class.forName(PropsValues.PACKAGE_PATH+className);
            Object invoker1 = get(cl1, id);

            java.util.Date timestamp1 = new java.util.Date();
            String[] namesArray = new String[1];
            namesArray[0] = "modifieddate";
            Object[] namesValues = new Object[1];
            namesValues[1] = timestamp1;

            for(int defaultCnt=0;defaultCnt < namesArray.length;defaultCnt++ )  {
                Field field = cl1.getDeclaredField(namesArray[defaultCnt].toString());
                Class type = field.getType();
                Class arguments[] = new Class[] {type};

                java.lang.reflect.Method objMethod = cl1.getMethod("set"+TextFormatter.format(namesArray[defaultCnt], TextFormatter.G),arguments);
                Object objVal = typeCastForValue(type.getName(),namesValues[defaultCnt]);
                Object[] obj = new Object[]{objVal};
                Object result1 = objMethod.invoke(invoker1, obj);
            }

            Field field = cl1.getDeclaredField("deleteflag");
            Class type = field.getType();
            Class arguments[] = new Class[] {type};

            java.lang.reflect.Method objMethod = cl1.getMethod("set"+TextFormatter.format("deleteflag", TextFormatter.G),arguments);
            Object objVal = typeCastForValue(type.getName(),1);
            Object[] obj = new Object[]{objVal};
            Object result1 = objMethod.invoke(invoker1, obj);

            result = true;
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            result = false;
            throw ServiceException.FAILURE("reportMethods.setObj", e);
        }
        return result;
    }

     public JSONObject getModuleData(HttpServletRequest request) {
        String result = "{data:[],count : 0}";
        JSONObject jobj = new JSONObject();
        try {
            String reportid = request.getParameter("moduleid");
            String query = null;
            Configuration cfg = getConfig();
            java.util.Iterator itr = cfg.getTableMappings();
            HashMap<String, Table> tableObj = new HashMap<String, Table> ();
            while(itr.hasNext()) {
                Table table = (Table)itr.next();
                tableObj.put(PropsValues.PACKAGE_PATH+"."+table.getName(), table);
            }
            //Fetch all reference tables in report
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
            query = "Select distinct mb_gridconfig.reftable as tablename from "+PropsValues.PACKAGE_PATH+".mb_gridconfig as mb_gridconfig where mb_gridconfig.reportid = ? and mb_gridconfig.countflag = ? ";
            List ls = find(query, new Object[] {report, false});
            java.util.Iterator ite = ls.iterator();
            String str = "";
            ArrayList<String> refTableList1 = new ArrayList<String> ();
            HashMap<String, String> shortTableNames = new HashMap<String, String> ();
            while(ite.hasNext()) {
                String reftablename = (String) ite.next();
                if(!StringUtil.isNullOrEmpty(reftablename)) {
                    refTableList1.add(PropsValues.PACKAGE_PATH+"."+reftablename);
                    shortTableNames.put(PropsValues.PACKAGE_PATH+"."+reftablename, reftablename);
                }
            }
            //If new table created not present in arraylist then it is added forcefully
            String reportTName = reportDao.getReportTableName(reportid);
            if(!StringUtil.isNullOrEmpty(reportTName) && !refTableList1.contains(PropsValues.PACKAGE_PATH+"."+reportTName)) {
                refTableList1.add(PropsValues.PACKAGE_PATH+"."+reportTName);
                shortTableNames.put(PropsValues.PACKAGE_PATH+"."+reportTName, reportTName);
            }

            ArrayList<String> refTableList = new ArrayList<String> ();
            ArrayList<String> primaryTableList = new ArrayList<String> ();
            HashMap<String, ArrayList<String>> parentTableObj = new HashMap<String, ArrayList<String>> ();
            HashMap<String, ArrayList<String>> colNameObj = new HashMap<String, ArrayList<String>> ();
            //Make list of reference tables which does not have any relationship with another ref. tables
            //Sort all reference tables in relationship order i.e. form child - parent
            for(int i=0; i < refTableList1.size(); i++) {
                String reftablename =refTableList1.get(i);
                Table tb = tableObj.get(reftablename);
                Iterator FkIte = tb.getForeignKeyIterator();
                if(FkIte.hasNext()) {
                    boolean flg = false;
                    ArrayList<String> parentTName1 = new ArrayList<String> ();
                    ArrayList<String> colName1 = new ArrayList<String> ();
                    while(FkIte.hasNext()) { //Always keep foreign key name = parent tablename + primary column name
                        ForeignKey obj = (ForeignKey) FkIte.next();
                        String parentTName = obj.getReferencedEntityName();
                        Column col = (Column) obj.getColumns().iterator().next();
                        String colName = col.getName();
                        if(refTableList1.contains(parentTName)) {
                            if(primaryTableList.contains(parentTName)) {
                                primaryTableList.remove(parentTName);
                            }
                            if(refTableList.contains(parentTName)) {
                                refTableList.remove(parentTName);
                                if(!refTableList.contains(reftablename)) {
                                    refTableList.add(reftablename);
                                }
                                refTableList.add(parentTName);
                            } else if(!refTableList.contains(reftablename)) {
                                refTableList.add(reftablename);
                            }
                            parentTName1.add(parentTName);
                            colName1.add(colName);

                            flg = true;
                        } else {
                            if(!refTableList.contains(reftablename)) {
                                refTableList.add(reftablename);
                                flg = true;
                            }
                            continue;
                        }
                    }

                    parentTableObj.put(reftablename, parentTName1);
                    colNameObj.put(reftablename, colName1);

                    if(!flg) {
                        primaryTableList.add(reftablename);
                    }
                } else if(!FkIte.hasNext()) {
                    //Check whether reference table is already part of parentTableObj.
                    //If yes then not to add in primaryList
                    boolean flg = true;
                    Iterator temp = parentTableObj.keySet().iterator();
                    while(temp.hasNext()) {
                        ArrayList<String> ttemp = (ArrayList<String>) parentTableObj.get(temp.next());
                        for(int l = 0; l < ttemp.size(); l++) {
                            if(ttemp.contains(reftablename)) {
                                flg = false;
                                break;
                            }
                        }
                        if(!flg) {
                            break;
                        }
                    }
                    if(flg) {
                        primaryTableList.add(reftablename);
                    }
                }
            }
            String delQuery = "";
            for(int i=0; i < refTableList.size(); i++) {
                String reftablename =refTableList.get(i);
                ArrayList<String> parentTName = parentTableObj.get(reftablename);
                ArrayList<String> colName = colNameObj.get(reftablename);

                for(int j = 0; j < parentTName.size(); j++) {
                   String shortRefTName = shortTableNames.get(reftablename);
                   String shortParentTName = shortTableNames.get(parentTName.get(j));
                   if(StringUtil.isNullOrEmpty(str)) {
                        str += reftablename + " as " + shortRefTName + " inner join " + shortRefTName + "."+colName.get(j) + " as " + shortParentTName;
                        delQuery += shortRefTName +".deleteflag = 0 and "+shortParentTName+".deleteflag = 0 ";
                   } else if(str.contains(shortRefTName)){
                       //if reference table is already part of query then use alias
                        str += " inner join " + shortRefTName + "."+colName.get(j)+ " as " + shortParentTName ;
                        if(!delQuery.contains(shortParentTName)) {
                            delQuery += " and "+shortParentTName+".deleteflag = 0 ";
                        }
                   } else {
                       //otherwise use full path
                        str += " inner join " + reftablename + "."+colName.get(j)+ " as " + shortParentTName ;
                        if(!delQuery.contains(shortParentTName)) {
                            delQuery += " and "+shortParentTName+".deleteflag = 0 ";
                        }
                   }
                }
            }

            String primaryTableQuery = "";
            for(int i=0; i<primaryTableList.size(); i++) {
                String reftablename = primaryTableList.get(i);
                String shortRefTName = shortTableNames.get(reftablename);

                if(StringUtil.isNullOrEmpty(delQuery)) {
                   delQuery += shortRefTName +".deleteflag = 0 ";
                } else if(!delQuery.contains(shortRefTName)) {
                   delQuery += " and " + shortRefTName+".deleteflag = 0 ";
                }

                if(StringUtil.isNullOrEmpty(primaryTableQuery)) {
                    primaryTableQuery += " from "+ reftablename+ " as " + shortRefTName;
                } else {
                    primaryTableQuery += " , "+ reftablename+ " as " + shortRefTName;
                }
            }
            String finalQuery = "";
            if(!StringUtil.isNullOrEmpty(primaryTableQuery)) {
                finalQuery += primaryTableQuery;
            }
            if(!StringUtil.isNullOrEmpty(str)) {
                if(StringUtil.isNullOrEmpty(finalQuery)) {
                   finalQuery = " from "+ str;
                } else {
                   finalQuery += ", " + str;
                }
            }

            //Fetch all display columns
            String SELECT_QUERY = "select mb_gridconfig.name, mb_gridconfig.xtype, mb_gridconfig.combogridconfig, mb_gridconfig.countflag, mb_gridconfig.reftable, mb_gridconfig.displayfield from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig" +
                    " where mb_gridconfig.reportid = ?";
            List list = find(SELECT_QUERY, new Object[]{report});
            ite = list.iterator();
            String fieldQuery = "";
            ArrayList<String> fieldNameArray = new ArrayList<String> ();
            ArrayList<String> countFieldNameArray = new ArrayList<String> ();
            ArrayList<String> displayFieldArray = new ArrayList<String> ();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                String fieldName = row[0].toString();
                String displayField = row[5].toString();
                String xtype = row[1].toString();
                String combogridconfig = "-1";
                if(row[2] != null) {
                    combogridconfig = row[2].toString();
                }
                //Check for count flag
                if(row[3] != null && Boolean.parseBoolean(row[3].toString())) {//countFlag
                    countFieldNameArray.add(row[4].toString());
                } else {
                    fieldNameArray.add(fieldName);
                    displayFieldArray.add(displayField);
                    String className = "";
                    if(xtype.equals("Combobox") && combogridconfig.equals("-1")) {
                        className = fieldName.split(PropsValues.REPORT_HARDCODE_STR)[0];
                        fieldName = className+"."+getPrimaryColName(className);
                    } else {
                        fieldName = fieldName.replaceAll(PropsValues.REPORT_HARDCODE_STR, ".");
                    }
                        fieldQuery += fieldName+",";
                }
            }

            if(StringUtil.isNullOrEmpty(fieldQuery)) {
                fieldQuery = "Select * ";
            } else {
                fieldQuery = "Select "+fieldQuery.substring(0, fieldQuery.length()-1);
            }

            //Check for mapping filter
            String filterQry = "";

            if(!StringUtil.isNullOrEmpty(delQuery)) {
                if(!StringUtil.isNullOrEmpty(filterQry)) {
                    delQuery = " where "+filterQry+" and "+delQuery;
                } else {
                    delQuery = " where "+delQuery;
                }
            }

            //Check for comments
            boolean commentFlag = false;
            boolean docFlag = false;
            String ruleFilterQuery = "";
            if(!StringUtil.isNullOrEmpty(ruleFilterQuery))
                ruleFilterQuery = " and " + ruleFilterQuery;
            
            if(!StringUtil.isNullOrEmpty(finalQuery)) {
                //Get implementation class object and call before dataLoad method.
                //Check for reportTName is null;
                String className = reportTName;
                if(StringUtil.isNullOrEmpty(className)) {
                    className = "rb_"+reportDao.toLZ(report.getReportkey(), 3)+"_"+report.getReportname().replace(" ", "").toLowerCase();
                }
                Class cl1 = Class.forName(PropsValues.PACKAGE_PATH+".impl_"+className);
                java.lang.reflect.Constructor co1 = cl1.getConstructor();
                Object invoker1 = co1.newInstance();
                Class  arguments1[] = new Class[] {HibernateTemplate.class, HttpServletRequest.class,String.class,String.class,
                                String.class,String.class,String.class,ArrayList.class,ArrayList.class,ArrayList.class,Boolean.class,Boolean.class,JSONObject.class};

                java.lang.reflect.Method objMethod1 = cl1.getMethod("beforeGridExportData",arguments1);
                Object[] obj1 = new Object[]{getHibernateTemplate(),request,finalQuery,fieldQuery,delQuery,ruleFilterQuery,
                                                    reportTName,displayFieldArray,fieldNameArray,countFieldNameArray,commentFlag,docFlag, jobj};
                Object result11 = objMethod1.invoke(invoker1, obj1);
            }
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            result = "{data:[],count : 0}";
            throw ServiceException.FAILURE("reportMethods.loadData", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{data:[],count : 0}";
            throw ServiceException.FAILURE("reportMethods.loadData", e);
        } finally {
            return jobj;
        }
    }

    public long getdocCount(String recid) throws ServiceException{
        int num = 0;
        String hql = "select count(*) as count from "+PropsValues.PACKAGE_PATH+".mb_docsmap as mb_docsmap where mb_docsmap.recid = ?";
        List li = executeQuery(hql,recid);
        Iterator ite = li.iterator();
        long count = 0;
        if( ite.hasNext() ) {
            count = Long.parseLong(ite.next().toString());
        }
        return count;
    }
}
