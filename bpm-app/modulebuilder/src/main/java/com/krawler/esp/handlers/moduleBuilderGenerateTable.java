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
package com.krawler.esp.handlers;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.dao.BaseDAO;
import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.*;
import com.krawler.portal.tools.*;
//import org.hibernate.*;
import com.krawler.esp.hibernate.impl.*;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.esp.utils.PropsValues;
import com.krawler.formbuilder.servlet.ReportBuilderDao;
//import org.springframework.orm.hibernate3.HibernateTemplate;
import com.krawler.workflow.module.bizservice.DataObjectOperations;
public class moduleBuilderGenerateTable extends BaseDAO{

    public String createServiceXMLForTable(DataObjectOperations dataObjectOperationObj, ReportBuilderDao reportDao,String formid, String moduleid,String formjson, String parentmodule) throws ServiceException, SQLException{
        String result = "";
        PreparedStatement stmt = null;
        ArrayList<Hashtable<String, Object>> aList = new ArrayList<Hashtable<String, Object>>();
        ArrayList<Hashtable<String, Object>> aList1 = new ArrayList<Hashtable<String, Object>>();//For read only fields
        int cnt = 0;

        try{
            JSONObject r = new JSONObject();
            if(formjson.length()>0) {
                Object[] objArr ;
                Object[] objArrField ;
                ServiceBuilder sb = new ServiceBuilder();
                com.krawler.utils.json.base.JSONArray jsondata =  (new com.krawler.utils.json.base.JSONObject("{'data':["+formjson+"]}")).getJSONArray("data");
                String tableName = "";
                if(!StringUtil.isNullOrEmpty(parentmodule)){
                    tableName = reportDao.getReportTableName(parentmodule);
                    mb_reportlist module = (mb_reportlist) get(mb_reportlist.class, getModuleid(formid));
                    module.setTablename(tableName);
                    save(module);
                    /*String filename = sb.getXmlFileName(tableName);
                    generateServiceXml getXml = new generateServiceXml();
                    result = getXml.getXmlContent(filename,jsondata,session,request);*/
                } else {
                    
                    tableName = reportDao.getReportTableName(getModuleid(formid));
                }
                    objArr = new Object[] {"id","String","true","default","","",0, true,""};
                    objArrField = new Object[] {"name","type","primaryid","xtype","displayfield","reftable","refflag","hidden","default"};
                    makeEntryToArrayList(cnt,aList,objArr,objArrField);


                    objArrField = new Object[] {"name","type","xtype","displayfield","reftable","refflag","hidden","default"};

                    objArr = new Object[] {"createdby","String","default","","",0,true,""};
                    makeEntryToArrayList(cnt,aList,objArr,objArrField);

                    objArr = new Object[] {"createddate","Date","default","","",0,true,""};
                    makeEntryToArrayList(cnt,aList,objArr,objArrField);

                    objArr = new Object[] {"modifieddate","Date","default","","",0,true,""};
                    makeEntryToArrayList(cnt,aList,objArr,objArrField);

                    objArr = new Object[] {"deleteflag","double","default","","",0,true,""};
                    makeEntryToArrayList(cnt,aList,objArr,objArrField);

                    for(int i=0;i<jsondata.length();i++){
                        objArrField = new Object[] {"name","type","xtype","displayfield","reftable","refflag","hidden","default"};
                        String refTableName = "";
                        int refflag = 0;
                        JSONObject obj = jsondata.getJSONObject(i);
                        String type = "";
                        String defaultValue="";
                        if(obj.getString("xtype").equals("datefield")){
                            type = "Date";

                        }else if(obj.getString("xtype").equals("textarea")){
                            type = "String";

                        }else if(obj.getString("xtype").equals("combo") || obj.getString("xtype").equals("select")
                                || (obj.getString("xtype").equals("readOnlyCmp") && obj.getString("autoPopulate").equals("true"))){
                            type = "String";
                             refflag = Integer.parseInt(obj.getString("mastertype"));
                            if(refflag == 0){
                                refTableName = obj.getString("datastore");
                            }else{
                                refTableName = "mb_configmasterdata";
                            }
                        }else if(obj.getString("xtype").equals("checkbox")||obj.getString("xtype").equals("radio")){
                            type = "boolean";

                        }else if(obj.getString("xtype").equals("numberfield")){
                            if(obj.has("allowDecimals")&&obj.getString("allowDecimals").equals("false")){
                                type = "int";
                            }else{
                                type = "double";
                            }

                        }else{
                            type = "String";
                        }

                        try{
                            defaultValue = obj.getString("value").toString();
                        }catch(Exception e) {
                            defaultValue = "";
                        }

                        if(obj.getString("xtype").equals("combo") || obj.getString("xtype").equals("select")){
                            objArrField = new Object[] {"name","type","foreignid","xtype","displayfield","reftable","refflag","hidden","configid","default"};
                            if(obj.getString("xtype").equals("select"))
                                objArr = new Object[] {obj.getString("name"),type,false,obj.getString("xtype"),obj.getString("fieldLabel"),refTableName,refflag,false,obj.getString("datastore"),defaultValue};
                            else
                                objArr = new Object[] {obj.getString("name"),type,true,obj.getString("xtype"),obj.getString("fieldLabel"),refTableName,refflag,false,obj.getString("datastore"),defaultValue};
                            makeEntryToArrayList(cnt,aList,objArr,objArrField);
                        }else if(obj.getString("xtype").equals("readOnlyCmp")){
                            if(obj.getString("autoPopulate").equals("true")) {
                                objArr = new Object[] {obj.getString("name"),type,obj.getString("xtype"),obj.getString("fieldLabel"),refTableName,refflag,false,defaultValue};
                                makeEntryToArrayListReadOnly(cnt,aList1,objArr,objArrField);
                            }
                        }else{
                            objArr = new Object[] {obj.getString("name"),type,obj.getString("xtype"),obj.getString("fieldLabel"),refTableName,refflag,false,defaultValue};
                            makeEntryToArrayList(cnt,aList,objArr,objArrField);
                        }
                    }

                    sb.createServiceXMLFile(aList, tableName);
                    sb.createServiceTableEntry(dataObjectOperationObj,aList, tableName,moduleid);
                    sb.createJavaFile(tableName, false);
                    //sb.createModuleDef(aList,tableName);
                    //sb.createBusinessProcessforCRUD(tableName,"");
                    String rid = insertConfigTable(moduleid,formid,aList, tableName, aList1);
                    r.put("tablename", tableName);
                    r.put("reportid", rid);
                    result = r.toString();
            }
        }catch(JSONException ex){
            logger.warn(ex.getMessage(), ex);
             stmt.close();
            throw ServiceException.FAILURE("moduleBuilderGenerateTable.createFormTable", ex);
        } catch(Exception ex){
            logger.warn(ex.getMessage(), ex);
             stmt.close();
            throw ServiceException.FAILURE("moduleBuilderGenerateTable.createFormTable", ex);
        }
        return result;
    }
    public static void makeEntryToArrayList(int cnt,ArrayList<Hashtable<String, Object>> aList,Object[] objArr,Object[] objArrField){
        int i=0;
        aList.add(cnt,new Hashtable<String, Object>());
        for(i=0;i<objArr.length;i++){
            aList.get(cnt).put(objArrField[i].toString(),objArr[i]);
        }
        cnt++;
    }
    public static void makeEntryToArrayListReadOnly(int cnt,ArrayList<Hashtable<String, Object>> aList1,Object[] objArr,Object[] objArrField){
        int i=0;
        aList1.add(cnt,new Hashtable<String, Object>());
        for(i=0;i<objArr.length;i++){
            aList1.get(cnt).put(objArrField[i].toString(),objArr[i]);
        }
        cnt++;
    }
    public String insertConfigTable(String moduleid, String formid,ArrayList<Hashtable<String, Object>> aList,
            String tableName, ArrayList<Hashtable<String, Object>> aList1) throws ServiceException{
        String result = "";
        String deleteQuery = "delete from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig " +
                "where mb_gridconfig.reportid = ? " ;
        try {
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, getModuleid(formid));
            int num = executeUpdate(deleteQuery, new Object[] {report});
            for(int cnt=0;cnt<aList.size();cnt++){
                  com.krawler.esp.hibernate.impl.mb_gridconfig gridConf = new com.krawler.esp.hibernate.impl.mb_gridconfig();
                  Hashtable<String, Object> obj = aList.get(cnt);
                  gridConf.setColumnindex(cnt);
                  gridConf.setHidden(Boolean.parseBoolean(obj.get("hidden").toString()));
                  gridConf.setCountflag(false);
                  gridConf.setDisplayfield(obj.get("displayfield").toString());
                  gridConf.setFilter("");
                  if(obj.get("xtype").toString().equals("readOnlyCmp")) {
                      gridConf.setName(obj.get("name").toString());
                  } else {
                      if(obj.get("name").toString().indexOf(".") > -1) {
                            String[] tablecolumn = obj.get("name").toString().split("\\.");
                            gridConf.setName(tablecolumn[0] + PropsValues.REPORT_HARDCODE_STR+tablecolumn[1].toLowerCase());
                      } else {
                            if(obj.get("name").toString().indexOf(PropsValues.REPORT_HARDCODE_STR)==-1)
                                gridConf.setName(tableName + PropsValues.REPORT_HARDCODE_STR+obj.get("name").toString().toLowerCase());
                      }
                  }
                  if(obj.get("refflag").toString().equals("0")) {
                      gridConf.setReftable(obj.get("reftable").toString());
                      gridConf.setCombogridconfig("-1");
                  } else {
                      gridConf.setReftable("");
                      gridConf.setCombogridconfig(obj.get("configid").toString());
                  }
                  renderer render =(renderer) get(renderer.class, "0");
                  gridConf.setRenderer(render);
                  gridConf.setReportid(report);
                  gridConf.setXtype(obj.get("xtype").toString());
                  gridConf.setDefaultValue(obj.get("default").toString());
                  save(gridConf);
            }
            for(int cnt=0;cnt<aList1.size();cnt++){
                  com.krawler.esp.hibernate.impl.mb_gridconfig gridConf = new com.krawler.esp.hibernate.impl.mb_gridconfig();
                  Hashtable<String, Object> obj = aList1.get(cnt);
                  gridConf.setColumnindex(cnt);
                  gridConf.setHidden(Boolean.parseBoolean(obj.get("hidden").toString()));
                  gridConf.setCountflag(false);
                  gridConf.setDisplayfield(obj.get("displayfield").toString());
                  gridConf.setFilter("");
                  if(obj.get("xtype").toString().equals("readOnlyCmp")) {
                      gridConf.setName(obj.get("name").toString());
                  } else {
                      if(obj.get("name").toString().indexOf(".") > -1) {
                            String[] tablecolumn = obj.get("name").toString().split("\\.");
                            gridConf.setName(tablecolumn[0] + PropsValues.REPORT_HARDCODE_STR+tablecolumn[1].toLowerCase());
                      } else {
                            if(obj.get("name").toString().indexOf(PropsValues.REPORT_HARDCODE_STR)==-1)
                                gridConf.setName(tableName + PropsValues.REPORT_HARDCODE_STR+obj.get("name").toString().toLowerCase());
                      }
                  }

                  gridConf.setReftable(obj.get("reftable").toString());
                  gridConf.setCombogridconfig("-1");
                  renderer render =(renderer) get(renderer.class, "0");
                  gridConf.setRenderer(render);
                  gridConf.setReportid(report);
                  gridConf.setXtype(obj.get("xtype").toString());
                  save(gridConf);
            }
            result = moduleid;
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("moduleBuilderGenerateTable.createFormTable", ex);
        }
        return result;
    }

    public String getModuleid(String formid) throws ServiceException{
        String moduleid = "";
        String SELECT_QUERY="Select mb_forms.moduleid.reportid from com.krawler.esp.hibernate.impl.mb_forms as mb_forms "
                      +" where mb_forms.formid = ?";
        try {
            List ls = executeQuery(SELECT_QUERY, new Object[] {formid} );
            Iterator ite = ls.iterator();
            if( ite.hasNext() ) {
                Object row = ite.next();
                moduleid = row.toString();
			}
		} catch (Exception e) {
                    logger.warn(e.getMessage(), e);
			throw ServiceException.FAILURE("Auth.verifyLogin", e);
		}
//        DbResults rs=null;
//        rs = DbUtil.executeQuery(conn, "select moduleid from mb_forms where formid = ?",new Object[]{formid});
//        if (rs.next()) {
//           moduleid = rs.getString("moduleid");
//        }
        return moduleid;
    }

    public String getFormId(String moduleid) throws ServiceException{
        String result = "{'success' : true}";
        String SELECT_QUERY = "select mb_forms.formid from com.krawler.esp.hibernate.impl.mb_forms as mb_forms where mb_forms.moduleid = ?";
        try {
            mb_reportlist basemodule = (mb_reportlist) get(mb_reportlist.class, moduleid);
            List list = executeQuery(SELECT_QUERY, basemodule);
            Iterator ite = list.iterator();
            if( ite.hasNext() ) {
                result = ite.next().toString();
            }

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("moduleBuilderGenerateTable.getModuleName", ex);
        }
        return result;
    }

    public String getModuleName(String moduleid) throws ServiceException{
        String result = "{'success' : true}";
        String SELECT_QUERY = "select mb_modules.reportname from com.krawler.esp.hibernate.impl.mb_reportlist as mb_modules  where mb_modules.reportid = ?";
        try {
            List list = executeQuery(SELECT_QUERY, moduleid);
            Iterator ite = list.iterator();
            if( ite.hasNext() ) {
                result = ite.next().toString();
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("moduleBuilderGenerateTable.getModuleName", ex);
        }
        return result;
    }

}
