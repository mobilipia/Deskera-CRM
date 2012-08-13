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
import com.krawler.common.session.SessionExpiredException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.fileupload.FileItem;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.JsonArrayHandler;
import com.krawler.esp.hibernate.impl.mb_configmasterdata;
import com.krawler.esp.hibernate.impl.mb_dashportlet;
import com.krawler.esp.hibernate.impl.mb_docs;
import com.krawler.esp.hibernate.impl.mb_docsmap;
import com.krawler.esp.hibernate.impl.mb_forms;
import com.krawler.esp.hibernate.impl.mb_gridconfig;
import com.krawler.esp.hibernate.impl.mb_moduleConfigMap;
import com.krawler.esp.hibernate.impl.mb_modulegr;
import com.krawler.esp.hibernate.impl.mb_permactions;
import com.krawler.esp.hibernate.impl.mb_permgrmaster;
import com.krawler.esp.hibernate.impl.mb_reportlist;
import com.krawler.esp.hibernate.impl.mb_stdConfigs;
import com.krawler.esp.hibernate.impl.pm_conditionMaster;
import com.krawler.esp.hibernate.impl.pm_taskmaster;
import com.krawler.esp.hibernate.impl.prereq;
import com.krawler.esp.hibernate.impl.prereqgroup;
import com.krawler.esp.hibernate.impl.renderer;
import com.krawler.esp.hibernate.impl.reportMethods;
import com.krawler.esp.utils.PropsValues;
import com.krawler.formbuilder.servlet.ModuleBuilderController;
import com.krawler.formbuilder.servlet.ReportBuilderDao;
import com.krawler.formbuilder.servlet.AccessRightDao;
import com.krawler.portal.tools.ServiceBuilder;
import com.krawler.portal.util.TextFormatter;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.workflow.module.bizservice.DataObjectOperations;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.fileupload.DiskFileUpload;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class ModuleBuilderDaoImpl extends BaseBuilderDao implements ModuleBuilderDao {
    private ReportBuilderDao reportDao;
    private DataObjectOperations dataObjectOperationObj;
    private AccessRightDao accessRightDao;

    public void setAccessRightDao(AccessRightDao accessRightDao) {
        this.accessRightDao = accessRightDao;
    }
    
    public void setDataObjectOperationObj(DataObjectOperations dataObjectOperationObj) {
        this.dataObjectOperationObj = dataObjectOperationObj;
    }



    public void setReportDao(ReportBuilderDao reportDao) {
        this.reportDao = reportDao;
    }

	public String checkFilterRulesQuery(mb_reportlist moduleid, int roleid, int filterFlag, String comboname) {
          String res1 = "";
          String res2 = "";
          try{
              String sql = "select prereqgroup.groupid as ruleid, prereqgroup.seq from "+PropsValues.PACKAGE_PATH+".prereqgroup as prereqgroup " +
                      "where prereqgroup.moduleid=? and prereqgroup.roleid.roleid=? and prereqgroup.filterflag=? order by prereqgroup.seq";
              List rsgrpprereq = find(sql, new Object[]{moduleid, roleid, filterFlag});
//              String sql = "select prereqgroup.groupid as ruleid, prereqgroup.seq from "+PropsValues.PACKAGE_PATH+".prereqgroup as prereqgroup where prereqgroup.moduleid=? order by prereqgroup.seq";
//              List rsgrpprereq = find(sql, new Object[]{moduleid});
                String[] grpruleid = new String[rsgrpprereq.size()];
                int grpctr=0;
                Iterator ite = rsgrpprereq.iterator();
                while(ite.hasNext()){
                    Object[] row = (Object[]) ite.next();
                    grpruleid[grpctr] = row[0].toString();
                    grpctr++;
                }

                for(int grpcnt=0; grpcnt<grpruleid.length; grpcnt++){
                    if(grpcnt==0){
                        res1 = checksubgrpFilterRulesQuery(grpruleid[grpcnt], filterFlag, comboname);
                    }
                    if(grpcnt<grpruleid.length-1){
                        res2 = "";
                        res2 = checksubgrpFilterRulesQuery(grpruleid[grpcnt+1], filterFlag, comboname);
                        prereqgroup prereqgroupid1 = (prereqgroup) get(prereqgroup.class, grpruleid[grpcnt]);
                        prereqgroup prereqgroupid2 = (prereqgroup) get(prereqgroup.class, grpruleid[grpcnt+1]);
                        sql = "select prereqgroupmap.ruletype from "+PropsValues.PACKAGE_PATH+".prereqgroupmap as prereqgroupmap where prereqgroupmap.group1=? and prereqgroupmap.group2=?";
                        List rsprereqgrpmap = find(sql, new Object[]{prereqgroupid1,prereqgroupid2});
                        Iterator itegrpmap = rsprereqgrpmap.iterator();
                        if(itegrpmap.hasNext()){
                            String ruletype = (String) itegrpmap.next();
                            if(!StringUtil.isNullOrEmpty(res2)) {
                                res1 = "("+res1 +" "+ ruletype +" "+ res2+")";
                            }
                        } else {
                            if(!StringUtil.isNullOrEmpty(res1))
                                res1 = "("+res1+")";
                        }
                    }
                 }
           } catch(Exception e) {
               logger.warn(e.getMessage(), e);
                res1 = "";
           }
           return res1;
      }

    public String checksubgrpFilterRulesQuery(String groupid1,int filterFlag,String comboname) throws Exception {
            String res1 = "";
            String res2 = "";
            int ctr=0;
            try{
                prereqgroup groupid = (prereqgroup) get(prereqgroup.class, groupid1);
                String sql = "select groupmap.ruleid, groupmap.seq from "+PropsValues.PACKAGE_PATH+".groupmap as groupmap where groupmap.groupid=? order by groupmap.seq";
                List rsprereq = find(sql, new Object[]{groupid});
                Object[] ruleid = new Object[rsprereq.size()];

                Iterator ite = rsprereq.iterator();
                while(ite.hasNext()){
                    Object[] row = (Object[]) ite.next();
                    ruleid[ctr] = row[0];
                    ctr++;
                }
                for(int cnt=0;cnt<ruleid.length;cnt++){
                    if(cnt==0){
                        prereq prereqObj = (prereq) ruleid[cnt];
                        String value1 = prereqObj.getValue1();
                        String value2 = prereqObj.getValue2();
                        String cid = prereqObj.getAttribute();
                        String comboName1 = cid.split(PropsValues.REPORT_HARDCODE_STR)[1];

                        mb_gridconfig gridConfObj = (mb_gridconfig) prereqObj.getAttributeid();
                        String refColumn = gridConfObj.getReftable();
                        String combogridconfig = gridConfObj.getCombogridconfig();
                        boolean masterFlag = false;
                        if(filterFlag == 1 && !StringUtil.isNullOrEmpty(comboname)) {
                            if(StringUtil.isNullOrEmpty(refColumn) && !combogridconfig.equals("-1")) {
                                refColumn = "mb_configmasterdata";
                            }
                        } else {
                            if(StringUtil.isNullOrEmpty(refColumn) && !combogridconfig.equals("-1")) {
                                //For report, combo field from master table is not set as foreign key.
                                masterFlag = true;
                            }
                            refColumn = cid.replaceAll(PropsValues.REPORT_HARDCODE_STR, ".");
                        }

                        String configtype = prereqObj.getXtype();
                        int ruletype = prereqObj.getRuletype();
                        if(StringUtil.isNullOrEmpty(comboname) || (!StringUtil.isNullOrEmpty(comboname) && comboName1.equals(comboname))){

                                if(ruletype==1){//Exact
                                    if(configtype.equals("combo") || configtype.equals("Combobox")) {
                                      String[] valueArray = value1.split(",");
                                      String str = "";
                                      for(int i = 0; i < valueArray.length; i++) {
                                            str += "'"+valueArray[i].toString()+"',";
                                      }
                                      str = str.substring(0, str.length()-1);
                                      if(masterFlag) {
                                        res1 = refColumn+ " in ("+str+") ";
                                      } else {
                                        res1 = refColumn+ ".id" + " in ("+str+") ";
                                      }
                                    } else if(configtype.equals("Number") || configtype.equals("Number(Integer)") || configtype.equals("Number(Float)")) {
                                      res1 = refColumn + " = "+Double.parseDouble(value1);
                                    } else {
                                      res1 = refColumn + " = '"+value1+"'";
                                    }
                                }else if(ruletype==2){//Range
                                    if(value1.equals(" ") && !value2.equals(" ")){
                                        if(configtype.equals("Date")){//Date
                                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value2"));
//                                            if(dt.compareTo(dt1)<0){
//                                                res1 = true;
//                                            }
                                        }else{//Number field
                                            res1 = refColumn + " < "+Double.parseDouble(value2);
                                        }
                                    }else if(value2.equals(" ")  && !value1.equals(" ")){
                                        if(configtype.equals("Date")){//Date
                                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value1"));
//                                            if(dt.compareTo(dt1)>0){
//                                                res1 = true;
//                                            }
                                        }else{ //Number field
                                            res1 = refColumn + " > "+Double.parseDouble(value1);
                                        }
                                    }else{
                                        if(configtype.equals("Date")){//Date
                                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value1"));
//                                            Date dt2 = sdf.parse(rsprereqval.getString("value2"));
//                                            if(dt.compareTo(dt1)>0&&dt.compareTo(dt2)<0){
//                                                res1 = true;
//                                            }
                                        }else{//Number field
                                            res1 = "("+refColumn + " > "+Double.parseDouble(value1)+" and " + refColumn + " < "+Double.parseDouble(value2)+")";
                                        }
                                    }
                                }
                        }
                    }
                    if(cnt<ruleid.length-1){
                        prereq prereqObj = (prereq) ruleid[cnt+1];
                        String value1 = prereqObj.getValue1();
                        String value2 = prereqObj.getValue2();
                        String cid = prereqObj.getAttribute();
                        String comboName1 = cid.split(PropsValues.REPORT_HARDCODE_STR)[1];
                        mb_gridconfig gridConfObj = (mb_gridconfig) prereqObj.getAttributeid();
                        String refColumn = gridConfObj.getReftable();
                        String combogridconfig = gridConfObj.getCombogridconfig();
                        boolean masterFlag = false;
                        if(filterFlag == 1 && !StringUtil.isNullOrEmpty(comboname)) {
                            if(StringUtil.isNullOrEmpty(refColumn) && !combogridconfig.equals("-1")) {
                                refColumn = "mb_configmasterdata";
                            }
                        } else {
                            if(StringUtil.isNullOrEmpty(refColumn) && !combogridconfig.equals("-1")) {
                                //For report, combo field from master table is not set as foreign key.
                                masterFlag = true;
                            }
                            refColumn = cid.replaceAll(PropsValues.REPORT_HARDCODE_STR, ".");
                        }

                        String configtype = prereqObj.getXtype();
                        int ruletype = prereqObj.getRuletype();
                        if(StringUtil.isNullOrEmpty(comboname) || (!StringUtil.isNullOrEmpty(comboname) && comboName1.equals(comboname))){
                                res2 = "";
                                if(ruletype==1){//Exact
                                    if(configtype.equals("combo") || configtype.equals("Combobox")) {
                                      String[] valueArray = value1.split(",");
                                      String str = "";
                                      for(int i = 0; i < valueArray.length; i++) {
                                            str += "'"+valueArray[i].toString()+"',";
                                      }
                                      str = str.substring(0, str.length()-1);
                                      if(masterFlag) {
                                        res2 = refColumn+ " in ("+str+") ";
                                      } else {
                                        res2 = refColumn+ ".id" + " in ("+str+") ";
                                      }
                                    } else if(configtype.equals("Number") || configtype.equals("Number(Integer)") || configtype.equals("Number(Float)")) {
                                      res2 = refColumn + " = "+Double.parseDouble(value1);
                                    } else {
                                      res2 = refColumn + " = '"+value1+"'";
                                    }
                                }else if(ruletype==2){//Range
                                    if(value1.equals(" ") && !value2.equals(" ")){
                                        if(configtype.equals("Date")){//Date
                                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value2"));
//                                            if(dt.compareTo(dt1)<0){
//                                                res2 = true;
//                                            }
                                        }else{//Number field
                                            res2 = refColumn + " < "+Double.parseDouble(value2);
                                        }
                                    }else if(value2.equals(" ")  && !value1.equals(" ")){
                                        if(configtype.equals("Date")){//Date
                                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value1"));
//                                            if(dt.compareTo(dt1)>0){
//                                                res2 = true;
//                                            }
                                        }else{//Number field
                                            res2 = refColumn + " > "+Double.parseDouble(value1);
                                        }
                                    }else{
                                        if(configtype.equals("Date")){//Date
                                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value1"));
//                                            Date dt2 = sdf.parse(rsprereqval.getString("value2"));
//                                            if(dt.compareTo(dt1)>0&&dt.compareTo(dt2)<0){
//                                                res2 = true;
//                                            }
                                        }else{//Number Field
                                            res2 = "("+refColumn + " > "+Double.parseDouble(value1)+" and " + refColumn + " < "+Double.parseDouble(value2)+")";
                                        }
                                    }
                                }else{
                                    if(configtype.equals("Date")){//Date
                                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                        Date dt = sdf.parse(configstr);
//                                        Date dt1 = sdf.parse(rsprereqval.getString("value1"));
//                                        Date dt2 = sdf.parse(rsprereqval.getString("value2"));
//                                        if(dt.compareTo(dt1)>0&&dt.compareTo(dt2)<0){
//                                            res2 = true;
//                                        }
                                    }else{//Number Field
                                        res2 = "("+refColumn + " > "+Double.parseDouble(value1)+" and " + refColumn + " < "+Double.parseDouble(value2)+")";
                                    }
                                }
                        }
                        prereq ruleid1 = (prereq) ruleid[cnt];
                        prereq ruleid2 = (prereq) ruleid[cnt+1];
                        sql = "select prereqmap.ruletype from "+PropsValues.PACKAGE_PATH+".prereqmap as prereqmap where prereqmap.rule1=? and prereqmap.rule2=?";
                        Iterator rsprereqmap = find(sql, new Object[]{ruleid1,ruleid2}).iterator();
                        if(rsprereqmap.hasNext()){
                            String ruletype1 = (String) rsprereqmap.next();
                            if(!StringUtil.isNullOrEmpty(res2)) {
                                res1 = "("+res1 +" "+ ruletype1 +" "+ res2+")";
                            }
                        } else {
                            if(!StringUtil.isNullOrEmpty(res1))
                                res1 = "("+res1+")";
                        }
                    }
                }

          }catch(Exception e){
              logger.warn(e.getMessage(), e);
          }
          return res1;
      }

    public String buildConditionQuery(HttpServletRequest request) throws Exception {
            String res1 = "";
            String res2 = "";
            int cnt=0;
            String conditiontype = "";
            try{
                String taskid = request.getParameter("taskid");
                String sql = "from "+PropsValues.PACKAGE_PATH+".pm_conditionMaster as pm_conditionMaster "+//inner join pm_conditionMaster.taskderid as pm_taskderivationmap " +
                        "where pm_conditionMaster.taskderid.childtaskid.taskid=? order by pm_conditionMaster.taskderid, pm_conditionMaster.seq ";
                List rsprereq = find(sql, new Object[]{taskid});
                Iterator ite = rsprereq.iterator();
                String pretaskderid = "";
                while(ite.hasNext()){
                    pm_conditionMaster condObj = (pm_conditionMaster) ite.next();
                    if(cnt==0){
                        String value1 = condObj.getValue1();
                        String value2 = condObj.getValue2();
                        String colName = condObj.getAttribute();
                        String refColumn = colName.replaceAll(PropsValues.REPORT_HARDCODE_STR, ".");
                        String configtype = condObj.getXtype();
                        int ruletype = condObj.getRuletype();
                        conditiontype = condObj.getConditiontype();

                        if(ruletype==1){//Exact
                            if(configtype.equals("combo") || configtype.equals("Combobox")) {
                              //This case not occured right now
                            } else if(configtype.equals("Number") || configtype.equals("Number(Integer)") || configtype.equals("Number(Float)")) {
                              res1 = refColumn + " = "+Double.parseDouble(value1);
                            } else {
                              res1 = refColumn + " = '"+value1+"'";
                            }
                        }else if(ruletype==2){//Range
                            if(value1.equals(" ") && !value2.equals(" ")){
                                if(configtype.equals("Date")){//Date
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                                }else{//Number field
                                    res1 = refColumn + " < "+Double.parseDouble(value2);
                                }
                            }else if(value2.equals(" ")  && !value1.equals(" ")){
                                if(configtype.equals("Date")){//Date
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");//
                                }else{ //Number field
                                    res1 = refColumn + " > "+Double.parseDouble(value1);
                                }
                            }else{
                                if(configtype.equals("Date")){//Date
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                                }else{//Number field
                                    res1 = "("+refColumn + " > "+Double.parseDouble(value1)+" and " + refColumn + " < "+Double.parseDouble(value2)+")";
                                }
                            }
                        }
                    } else {
                        String value1 = condObj.getValue1();
                        String value2 = condObj.getValue2();
                        String colName = condObj.getAttribute();
                        String refColumn = colName.replaceAll(PropsValues.REPORT_HARDCODE_STR, ".");
                        String configtype = condObj.getXtype();
                        int ruletype = condObj.getRuletype();

                        res2 = "";
                        if(ruletype==1){//Exact
                            if(configtype.equals("combo") || configtype.equals("Combobox")) {
                              //Not the case
                            } else if(configtype.equals("Number") || configtype.equals("Number(Integer)") || configtype.equals("Number(Float)")) {
                              res2 = refColumn + " = "+Double.parseDouble(value1);
                            } else {
                              res2 = refColumn + " = '"+value1+"'";
                            }
                        }else if(ruletype==2){//Range
                            if(value1.equals(" ") && !value2.equals(" ")){
                                if(configtype.equals("Date")){//Date
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                                }else{//Number field
                                    res2 = refColumn + " < "+Double.parseDouble(value2);
                                }
                            }else if(value2.equals(" ")  && !value1.equals(" ")){
                                if(configtype.equals("Date")){//Date
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                                }else{//Number field
                                    res2 = refColumn + " > "+Double.parseDouble(value1);
                                }
                            }else{
                                if(configtype.equals("Date")){//Date
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                                }else{//Number Field
                                    res2 = "("+refColumn + " > "+Double.parseDouble(value1)+" and " + refColumn + " < "+Double.parseDouble(value2)+")";
                                }
                            }
                        }else{
                            if(configtype.equals("Date")){//Date
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
                            }else{//Number Field
                                res2 = "("+refColumn + " > "+Double.parseDouble(value1)+" and " + refColumn + " < "+Double.parseDouble(value2)+")";
                            }
                        }

                        if(!StringUtil.isNullOrEmpty(res2)) {
                            res1 = res1 +" "+ conditiontype +" "+ res2;
                            conditiontype = condObj.getConditiontype();
                        }
                    }
                    cnt++;
                }

          }catch(Exception e){
              logger.warn(e.getMessage(), e);
          }
          return res1;
      }

    public Object getFieldValue(Object modObj, String methodName) {
        Object result = null;
        try{
            Class clrefTable = modObj.getClass();
            Class[] arguments = new Class[]{};
            java.lang.reflect.Method objMethod = clrefTable.getMethod("get"+TextFormatter.format(methodName, TextFormatter.G), arguments);
            result = objMethod.invoke(modObj,new Object[]{});
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return result;
    }

    public boolean checkFilterRules(pm_taskmaster taskid, mb_reportlist moduleid, Object recordObj, int filterFlag) {
          boolean res1 = true;
          boolean res2 = true;
          try{
              String sql = "select prereqgroup.groupid as ruleid, prereqgroup.seq from "+PropsValues.PACKAGE_PATH+".prereqgroup as prereqgroup " +
                      "where prereqgroup.moduleid=? and prereqgroup.filterflag=? order by prereqgroup.seq";
              List rsgrpprereq = find(sql, new Object[]{moduleid, filterFlag});
                String[] grpruleid = new String[rsgrpprereq.size()];
                int grpctr=0;
                Iterator ite = rsgrpprereq.iterator();
                while(ite.hasNext()){
                    Object[] row = (Object[]) ite.next();
                    grpruleid[grpctr] = row[0].toString();
                    grpctr++;
                }

                for(int grpcnt=0; grpcnt<grpruleid.length; grpcnt++){
                    if(grpcnt==0){
                        res1 = checksubgrpFilterRules(grpruleid[grpcnt], recordObj, filterFlag);
                    }
                    if(grpcnt<grpruleid.length-1){
                        res2 = checksubgrpFilterRules(grpruleid[grpcnt+1], recordObj, filterFlag);
                        prereqgroup prereqgroupid1 = (prereqgroup) get(prereqgroup.class, grpruleid[grpcnt]);
                        prereqgroup prereqgroupid2 = (prereqgroup) get(prereqgroup.class, grpruleid[grpcnt+1]);
                        sql = "select prereqgroupmap.ruletype from "+PropsValues.PACKAGE_PATH+".prereqgroupmap as prereqgroupmap where prereqgroupmap.group1=? and prereqgroupmap.group2=?";
                        List rsprereqgrpmap = find(sql, new Object[]{prereqgroupid1,prereqgroupid2});
                        Iterator itegrpmap = rsprereqgrpmap.iterator();
                        if(itegrpmap.hasNext()){
                            String ruletype = (String) itegrpmap.next();
                            if(ruletype.toLowerCase().equals("or")) {
                                res1 = (res1 || res2);
                            } else {
                                res1 = (res1 && res2);
                            }
                        }
                    }
                 }
           } catch(Exception e) {
                res1 = false;
           }
           return res1;
      }

    public boolean checksubgrpFilterRules(String groupid1, Object recordObj, int filterFlag) throws Exception {
            boolean res1 = true;
            boolean res2 = true;
            int ctr=0;
            try{
                prereqgroup groupid = (prereqgroup) get(prereqgroup.class, groupid1);
                String sql = "select groupmap.ruleid, groupmap.seq from "+PropsValues.PACKAGE_PATH+".groupmap as groupmap where groupmap.groupid=? order by groupmap.seq";
                List rsprereq = find(sql, new Object[]{groupid});
                Object[] ruleid = new Object[rsprereq.size()];

                Iterator ite = rsprereq.iterator();
                while(ite.hasNext()){
                    Object[] row = (Object[]) ite.next();
                    ruleid[ctr] = row[0];
                    ctr++;
                }
                for(int cnt=0;cnt<ruleid.length;cnt++){
                    if(cnt==0){
                        res1 = false;
                        prereq prereqObj = (prereq) ruleid[cnt];
                        String value1 = prereqObj.getValue1();
                        String value2 = prereqObj.getValue2();
                        String cid = prereqObj.getAttribute();

                        mb_gridconfig gridConfObj = (mb_gridconfig) prereqObj.getAttributeid();
                        String refColumn = gridConfObj.getReftable();
                        String refCol = "";
                        String combogridconfig = gridConfObj.getCombogridconfig();
                        boolean masterFlag = false;
                        Object refValue = null;

//                         if(filterFlag == 2) {
                            if(StringUtil.isNullOrEmpty(refColumn) && !combogridconfig.equals("-1")) {
                                //For report, combo field from master table is not set as foreign key.
                                masterFlag = true;
                            }
                            refColumn = cid.replaceAll(PropsValues.REPORT_HARDCODE_STR, ".");
                            refCol = cid.split(PropsValues.REPORT_HARDCODE_STR)[1];
//                         }

                        String configtype = prereqObj.getXtype();
                        int ruletype = prereqObj.getRuletype();
//                        if(StringUtil.isNullOrEmpty(comboname) || (!StringUtil.isNullOrEmpty(comboname) && comboName1.equals(comboname))){

                            if(ruletype==1){//Exact
                                if(configtype.equals("combo") || configtype.equals("Combobox")) {
                                  if(!masterFlag) {
                                      Object parentObj = getFieldValue(recordObj, refCol);
                                      String className = parentObj.getClass().toString();
                                      Object parentid = getFieldValue(parentObj, getPrimaryColName(className));
                                      String[] valueArray = value1.split(",");
                                      for(int i = 0; i < valueArray.length; i++) {
                                          if(parentid.toString().equals(valueArray[i].toString())) {
                                              res1 = true;
                                              break;
                                          }
                                      }
                                  }
                                } else if(configtype.equals("Number") || configtype.equals("Number(Integer)") || configtype.equals("Number(Float)")) {
                                  refValue = getFieldValue(recordObj, refCol);
                                  if(Double.parseDouble(refValue.toString()) == Double.parseDouble(value1)) {
                                        res1 = true;
                                  }
                                } else {
                                    refValue = getFieldValue(recordObj, refCol);
                                    if(refValue.equals(value1)) {
                                        res1 = true;
                                    }
                                }
                            }else if(ruletype==2){//Range
                                if(value1.equals(" ") && !value2.equals(" ")){
                                    if(configtype.equals("Date")){//Date
                                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value2"));
//                                            if(dt.compareTo(dt1)<0){
//                                                res1 = true;
//                                            }
                                    }else{//Number field
                                        refValue = getFieldValue(recordObj, refCol);
                                        if((Double.parseDouble(refValue.toString()) < Double.parseDouble(value2))) {
                                             res1 = true;
                                        }
                                    }
                                }else if(value2.equals(" ")  && !value1.equals(" ")){
                                    if(configtype.equals("Date")){//Date
                                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value1"));
//                                            if(dt.compareTo(dt1)>0){
//                                                res1 = true;
//                                            }
                                    }else{ //Number field
                                        refValue = getFieldValue(recordObj, refCol);
                                        if((Double.parseDouble(refValue.toString()) > Double.parseDouble(value1))) {
                                             res1 = true;
                                        }
                                    }
                                }else{
                                    if(configtype.equals("Date")){//Date
                                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value1"));
//                                            Date dt2 = sdf.parse(rsprereqval.getString("value2"));
//                                            if(dt.compareTo(dt1)>0&&dt.compareTo(dt2)<0){
//                                                res1 = true;
//                                            }
                                    }else{//Number field
                                        refValue = getFieldValue(recordObj, refCol);
                                        if(((Double.parseDouble(refValue.toString()) > Double.parseDouble(value1))
                                                && (Double.parseDouble(refValue.toString()) < Double.parseDouble(value2)))) {
                                             res1 = true;
                                        }
                                    }
                                }
                            }
//                        }
                    }
                    if(cnt<ruleid.length-1){
                        prereq prereqObj = (prereq) ruleid[cnt+1];
                        String value1 = prereqObj.getValue1();
                        String value2 = prereqObj.getValue2();
                        String cid = prereqObj.getAttribute();
                        mb_gridconfig gridConfObj = (mb_gridconfig) prereqObj.getAttributeid();
                        String refColumn = gridConfObj.getReftable();
                        String combogridconfig = gridConfObj.getCombogridconfig();
                        boolean masterFlag = false;
                        String refCol = "";
                        Object refValue = null;
//                        if(filterFlag == 2){
                            if(StringUtil.isNullOrEmpty(refColumn) && !combogridconfig.equals("-1")) {
                                //For report, combo field from master table is not set as foreign key.
                                masterFlag = true;
                            }
                            refColumn = cid.replaceAll(PropsValues.REPORT_HARDCODE_STR, ".");
                            refCol = cid.split(PropsValues.REPORT_HARDCODE_STR)[1];
//                        }

                        String configtype = prereqObj.getXtype();
                        int ruletype = prereqObj.getRuletype();

                        res2 = false;
                        if(ruletype==1){//Exact
                            if(configtype.equals("combo") || configtype.equals("Combobox")) {
                                 if(!masterFlag) {
                                      Object parentObj = getFieldValue(recordObj, refCol);
                                      String className = parentObj.getClass().toString();
                                      Object parentid = getFieldValue(parentObj, getPrimaryColName(className));
                                      String[] valueArray = value1.split(",");
                                      for(int i = 0; i < valueArray.length; i++) {
                                          if(parentid.toString().equals(valueArray[i].toString())) {
                                              res2 = true;
                                              break;
                                          }
                                      }
                                  }
                            } else if(configtype.equals("Number") || configtype.equals("Number(Integer)") || configtype.equals("Number(Float)")) {
                                refValue = getFieldValue(recordObj, refCol);
                                if(Double.parseDouble(refValue.toString()) == Double.parseDouble(value1)) {
                                    res2 = true;
                                }
                            } else {
                                refValue = getFieldValue(recordObj, refCol);
                                if(refValue.equals(value1)) {
                                    res2 = true;
                                }
                            }
                        }else if(ruletype==2){//Range
                            if(value1.equals(" ") && !value2.equals(" ")){
                                if(configtype.equals("Date")){//Date
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value2"));
//                                            if(dt.compareTo(dt1)<0){
//                                                res2 = true;
//                                            }
                                }else{//Number field
                                    refValue = getFieldValue(recordObj, refCol);
                                    if((Double.parseDouble(refValue.toString()) < Double.parseDouble(value2))) {
                                        res2 = true;
                                    }
                                }
                            }else if(value2.equals(" ")  && !value1.equals(" ")){
                                if(configtype.equals("Date")){//Date
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value1"));
//                                            if(dt.compareTo(dt1)>0){
//                                                res2 = true;
//                                            }
                                }else{//Number field
                                    refValue = getFieldValue(recordObj, refCol);
                                    if((Double.parseDouble(refValue.toString()) > Double.parseDouble(value1))) {
                                        res2 = true;
                                    }
                                }
                            }else{
                                if(configtype.equals("Date")){//Date
                                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                            Date dt = sdf.parse(configstr);
//                                            Date dt1 = sdf.parse(rsprereqval.getString("value1"));
//                                            Date dt2 = sdf.parse(rsprereqval.getString("value2"));
//                                            if(dt.compareTo(dt1)>0&&dt.compareTo(dt2)<0){
//                                                res2 = true;
//                                            }
                                }else{//Number Field
                                    refValue = getFieldValue(recordObj, refCol);
                                    if(((Double.parseDouble(refValue.toString()) > Double.parseDouble(value1))
                                            && (Double.parseDouble(refValue.toString()) < Double.parseDouble(value2)))) {
                                         res2 = true;
                                    }
                                }
                            }
                        }else{
                            if(configtype.equals("Date")){//Date
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy");
//                                        Date dt = sdf.parse(configstr);
//                                        Date dt1 = sdf.parse(rsprereqval.getString("value1"));
//                                        Date dt2 = sdf.parse(rsprereqval.getString("value2"));
//                                        if(dt.compareTo(dt1)>0&&dt.compareTo(dt2)<0){
//                                            res2 = true;
//                                        }
                            }else{//Number Field
                                refValue = getFieldValue(recordObj, refCol);
                                if(((Double.parseDouble(refValue.toString()) > Double.parseDouble(value1))
                                        && (Double.parseDouble(refValue.toString()) < Double.parseDouble(value2)))) {
                                     res2 = true;
                                }
                            }
                        }

                        prereq ruleid1 = (prereq) ruleid[cnt];
                        prereq ruleid2 = (prereq) ruleid[cnt+1];
                        sql = "select prereqmap.ruletype from "+PropsValues.PACKAGE_PATH+".prereqmap as prereqmap where prereqmap.rule1=? and prereqmap.rule2=?";
                        Iterator rsprereqmap = find(sql, new Object[]{ruleid1,ruleid2}).iterator();
                        if(rsprereqmap.hasNext()){
                            String ruletype1 = (String) rsprereqmap.next();
                            if(ruletype1.toLowerCase().equals("or")) {
                                res1 = res1 || res2;
                            } else {
                                res1 = res1 && res2;
                            }
                        }
                    }
                }

          }catch(Exception e){
              logger.warn(e.getMessage(), e);
          }
          return res1;
      }

    public String loadComboDataForRules(HttpServletRequest request) throws ServiceException {
       com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
       String result = null;
       try{
            String moduleid = request.getParameter("moduleid");
            mb_reportlist moduleObj = (mb_reportlist) get(mb_reportlist.class, moduleid);
            String tablename = "";
            if(moduleObj.getType() == 1) {//For grid
                //In report grid, for the combo from master table, name is stored as report table name + X_X + name
                //For combo from another module, name is stored as parent table name + X_X + name
                //In module builder, For combo from another module / master table, name is stored as report table name + X_X + name
                if(request.getParameter("combogridconfig").equals("-1")) {
                    tablename = request.getParameter("reftable");
                } else {
                    tablename = getReportTableName(moduleid);
                }
            } else {//For module
                tablename = getReportTableName(moduleid);
            }
            String name = request.getParameter("name");

            String sql = "select mb_gridconfig.combogridconfig, mb_gridconfig.reftable from "+PropsValues.PACKAGE_PATH+".mb_gridconfig as mb_gridconfig where mb_gridconfig.name = ? and mb_gridconfig.reportid = ? ";
            List ls = find(sql, new Object[]{tablename + PropsValues.REPORT_HARDCODE_STR + name, moduleObj});
            String combogridconfig = "-1";
            String reftable = "";
            Iterator ite = ls.iterator();
            if(ite.hasNext()){
                Object[] row = (Object[]) ite.next();
                combogridconfig = row[0].toString();
                reftable = row[1].toString();
            }
            String str1 = "";
            Object obj = null;

            if(combogridconfig.equals("-1")) {
                if(moduleObj.getType() != 1)
                    name = name.substring(0, name.length()-2);

                sql = "select "+ reftable +".id, "+ reftable +"."+name+" from "+PropsValues.PACKAGE_PATH+"."+reftable+" as "+ reftable+" where "+ reftable + ".deleteflag = 0 " + str1 ;
                ls = executeQuery(sql);
            } else {
                sql = "select mb_configmasterdata.masterid as id, mb_configmasterdata.masterdata as name from "+PropsValues.PACKAGE_PATH+".mb_configmasterdata " +
                        "as mb_configmasterdata where mb_configmasterdata.configid = ? order by mb_configmasterdata.masterdata ";
                ls = find(sql, new Object[]{combogridconfig});
            }
            ite = ls.iterator();
            while(ite.hasNext()){
                Object[] row = (Object[]) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("id", row[0]);
                //renderer renderer=(renderer) row[4];
                if(combogridconfig.equals("-1") && row[1].toString().startsWith("com.krawler")) {
                    mb_configmasterdata mb_configmasterdata=(mb_configmasterdata)row[1];
                    jtemp.put("name", mb_configmasterdata.getMasterdata());
                }else{
                    jtemp.put("name", row[1]);
                }
                jobj.append("data", jtemp);
            }
            if(jobj.length() > 0) {
                result = jobj.toString();
            } else {
                result = "{'data':[]}";
            }

        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE("FormServlet.getComboData", e);
        }
        return result;
   }

    public String getComboData(HttpServletRequest request) throws ServiceException {
       com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
       String result = null;
       try{
            String moduleid = request.getParameter("moduleid");
            String tablename = getReportTableName(moduleid);
            String name = request.getParameter("name");

            String cascadeComboName = "";
            String cascadeComboRefName = "";
            String cascadeComboValue = "";
            if(request.getParameter("cascadeCombo") != null) {
                cascadeComboName = request.getParameter("cascadeComboName").split(PropsValues.REPORT_HARDCODE_STR)[1];
                cascadeComboRefName = request.getParameter("cascadeComboRefName");
                cascadeComboValue = request.getParameter("cascadeComboValue");
            }

            String filterQuery = "";
            //Make filter query from filter rules
//            ArrayList permArray = AuthHandler.getRealRoleids(request);
//            mb_reportlist basemodule = (mb_reportlist) session.load(mb_reportlist.class, moduleid);
//            for(int i = 0; i < permArray.size(); i++) {
//                int roleid = Integer.parseInt(permArray.get(i).toString());
//                String res = checkFilterRulesQuery(session, basemodule, roleid, 1, name);
//                if(!StringUtil.isNullOrEmpty(res)) {
//                    res = "("+res +")";
//                    if(!StringUtil.isNullOrEmpty(filterQuery))
//                        filterQuery = res + " or " + filterQuery;
//                    else
//                        filterQuery = res;
//                }
//            }
            if(!StringUtil.isNullOrEmpty(filterQuery))
                filterQuery = " and " + filterQuery;

            mb_reportlist baseModuleObj = (mb_reportlist) get(mb_reportlist.class, request.getParameter("moduleid"));
            String sql = "select mb_gridconfig.combogridconfig, mb_gridconfig.reftable from "+PropsValues.PACKAGE_PATH+".mb_gridconfig as mb_gridconfig where mb_gridconfig.name = ? and mb_gridconfig.reportid = ? ";
            List ls = find(sql, new Object[]{tablename + PropsValues.REPORT_HARDCODE_STR + name, baseModuleObj});
            String combogridconfig = "-1";
            String reftable = "";
            Iterator ite = ls.iterator();
            if(ite.hasNext()){
                Object[] row = (Object[]) ite.next();
                combogridconfig = row[0].toString();
                reftable = row[1].toString();
            }
            String cascadeQuery = "";
            Object obj = null;
            //Need to check how cascadeComboName comes? Is it appended with table name or not?
            if(!StringUtil.isNullOrEmpty(cascadeComboName)) {
                cascadeQuery = " and "+ reftable + "." + cascadeComboName +" = ? ";
                ls = find(sql, new Object[]{tablename + PropsValues.REPORT_HARDCODE_STR + cascadeComboRefName, baseModuleObj});
                ite = ls.iterator();

                if(ite.hasNext()){
                    Object[] row = (Object[]) ite.next();
                    if(row[0].toString().equals("-1")){
                        String tempname = PropsValues.PACKAGE_PATH+"."+row[1].toString();
                        Class cl = Class.forName(tempname);
                        obj =  get(cl,cascadeComboValue);
                    }else{
                        obj =  get(mb_configmasterdata.class,cascadeComboValue);
                    }
                 }
            }

            Object[] paramArray = null;
            if(combogridconfig.equals("-1")) {
                if(!StringUtil.isNullOrEmpty(cascadeComboName)) {
                    paramArray = new Object[] {obj};
                }
            } else {
                paramArray = new Object[]{combogridconfig};
            }

            //Get implementation class object and call before combo load method.
          //  Class cl1 = Class.forName(PropsValues.PACKAGE_PATH+".impl_"+tablename);
         ////   java.lang.reflect.Constructor co1 = cl1.getConstructor();
         //   Object invoker1 = co1.newInstance();
         //   Class  arguments1[] = new Class[] {class, HttpServletRequest.class,Object[].class,String.class,String.class};

           // java.lang.reflect.Method objMethod1 = cl1.getMethod("beforeComboLoad",arguments1);
         //   Object[] obj1 = new Object[]{hibernateTemplate, request, paramArray, filterQuery, cascadeQuery};
         //   Object result11 = objMethod1.invoke(invoker1, obj1);

           // if(!StringUtil.isNullOrEmpty(result11.toString())) {
            //    sql = result11.toString();
            //    ls = find(sql, paramArray);
            //}
            if(combogridconfig.equals("-1")) {
                name = name.substring(0, name.length()-2);
                sql = "select "+ reftable +".id, "+ reftable +"."+name+" from "+PropsValues.PACKAGE_PATH+"."+reftable+" as "+ reftable+" where "+ reftable + ".deleteflag = 0 " + filterQuery + cascadeQuery ;
                if(!StringUtil.isNullOrEmpty(cascadeComboName)) {
                    ls = find(sql,new Object[] {obj});
                } else {
                    ls = executeQuery(sql);
                }
            } else {
                sql = "select mb_configmasterdata.masterid as id, mb_configmasterdata.masterdata as name from "+PropsValues.PACKAGE_PATH+".mb_configmasterdata " +
                        "as mb_configmasterdata where mb_configmasterdata.configid = ? "+ filterQuery +" order by mb_configmasterdata.masterdata ";
                ls = find(sql, new Object[]{combogridconfig});
            }
            ite = ls.iterator();
            while(ite.hasNext()){
                Object[] row = (Object[]) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("id", row[0]);
                //renderer renderer=(renderer) row[4];
                if(combogridconfig.equals("-1") && row[1].toString().startsWith("com.krawler")) {
                    mb_configmasterdata mb_configmasterdata=(mb_configmasterdata)row[1];
                    jtemp.put("name", mb_configmasterdata.getMasterdata());
                }else{
                    jtemp.put("name", row[1]);
                }
                jobj.append("data", jtemp);
            }
            if(jobj.length() > 0) {
                result = jobj.toString();
            } else {
                result = "{'data':[]}";
            }

        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE("FormServlet.getComboData", e);
        }
        return result;
   }
    public String saveForm(String formid,String moduleid,String formjson, String parentmodule, String jsondata, String tbar, String bbar) throws SQLException,ServiceException {
        String result = "{\"success\":false}";
        String hql = null;//"update com.krawler.esp.hibernate.impl.mb_forms as mb_forms set mb_forms.data = ? where mb_forms.formid = ? " ;
        Query query = null;
		try {
                mb_forms formObj = (mb_forms) get(mb_forms.class, formid);
                formObj.setData(jsondata);
                formObj.setDeployedInd(false);
                saveOrUpdate(formObj);

                String tName = createServiceXMLForTable(formid,moduleid,formjson, parentmodule);
                JSONArray jarr = new JSONArray("[" + jsondata + "]");
                String newConf = storeReportConf(jarr, tName, tbar, bbar);
                String moduleId = getModuleid(formid);
                reportDao.storeToolbarConf(moduleId, tbar, bbar);
                newConf = newConf.substring(1, (newConf.length() - 1));
                formObj.setData(newConf);
                String companyId = sessionHandlerDao.getCompanyid();
                formObj.setCompanyid(companyId);
                saveOrUpdate(formObj);

                result = "{\"success\":true}";
//            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.saveForm", e);
        }
        return result;
    }
    public String buildModule(String moduleid,String tablename){
           String result = "{data:{success:false}}";
           
        try {
         
            ServiceBuilder buildObj = new ServiceBuilder();
                buildObj._buildModule(dataObjectOperationObj, tablename, moduleid);
                String mbFormsQuery = " from mb_forms where moduleid.reportid=?";
            List formList  = executeQuery(mbFormsQuery, moduleid);

            if(!formList.isEmpty()){
                mb_forms mbformsObj = (mb_forms)formList.get(0);
                mbformsObj.setDeployedInd(Boolean.TRUE);
                saveOrUpdate(mbformsObj);
            }
                result = "{data:{success:true}}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return result;
    }
    public String storeReportConf(JSONArray conf, String pTable, String tbar, String bbar)throws SQLException,ServiceException {
        String ret = "";
        try{
            for(int i = 0; i < conf.length(); i++){
                JSONObject temp = conf.getJSONObject(i);
                if(temp.has("xtype")){
                    String xtype = temp.getString("xtype");
                    if(StringUtil.equal(xtype, "WtfGridPanel") || StringUtil.equal(xtype, "WtfEditorGridPanel")){
                        JSONObject jObj = new JSONObject(pTable);
                        conf.getJSONObject(i).remove("moduleGrid");
                        if(temp.has("reportConf")){
                            String rConf = temp.getString("reportConf");
                            JSONArray tabConf = new JSONArray(rConf);
                            JSONObject tObj = new JSONObject();
                            String moduleTableName = jObj.getString("tablename")+"."+(getPrimaryColName(jObj.getString("tablename")));
                            tObj.put("displayfield", moduleTableName);
                            tObj.put("xtype","None");
                            tObj.put("combogridconfig","-1");
                            tObj.put("hidden", true);
                            tObj.put("name", moduleTableName);
                            tObj.put("countflag", false);
                            tObj.put("reftable", jObj.getString("tablename"));
                            tObj.put("renderer", "");
                            tObj.put("filter", "");
                            tObj.put("summaryType", "");
                            tObj.put("defaultValue", "");
                            tabConf.put(tObj);
                            rConf = tabConf.toString();
                            String r = reportDao.saveReportGridConfig(rConf, temp.getString("reportId"), true, tbar, bbar);
                            JSONObject t = new JSONObject(r);
                            if(t.getBoolean("success")){
                                String tName = t.getString("tablename");
                                mb_reportlist reportObj = (mb_reportlist) get(mb_reportlist.class, temp.getString("reportId"));
                                reportObj.setTablename(tName);
                                saveOrUpdate(reportObj);

                                //Set grid table name for grid entry of module
                                String confName = jObj.getString("tablename") + PropsValues.REPORT_HARDCODE_STR + temp.getString("name");
                                reportObj = (mb_reportlist) get(mb_reportlist.class, jObj.getString("reportid"));
                                String hql = "SELECT id FROM " + PropsValues.PACKAGE_PATH + ".mb_gridconfig as mb_gridconfig WHERE mb_gridconfig.reportid = ? AND name = ?";
                                List ls = find(hql, new Object[] {reportObj, confName} );
                                Iterator ite = ls.iterator();
                                while(ite.hasNext()){
                                    Object idobj = ite.next();
                                    mb_gridconfig gridConfObj = (mb_gridconfig) get(mb_gridconfig.class, idobj.toString());
                                    gridConfObj.setReftable(tName);
                                    saveOrUpdate(gridConfObj);
                                }
                                hql = "SELECT id FROM " + PropsValues.PACKAGE_PATH + ".mb_gridconfig as mb_gridconfig WHERE mb_gridconfig.reportid.reportid = ?";
                                ls = find(hql, new Object[] {t.getString("reportId")} );
                                ite = ls.iterator();
//                                JSONObject cObj = new JSONObject(temp.getString("columnArray"));
                                JSONObject cObj = temp.getJSONObject("columnArray");

                                while(ite.hasNext()){
                                    Object idobj = ite.next();
                                    mb_gridconfig gridConfObj = (mb_gridconfig) get(mb_gridconfig.class, idobj.toString());
                                    for(int cnt = 0; cnt < cObj.length(); cnt++){
                                        JSONObject tempCa = cObj.getJSONObject(Integer.toString(cnt));
                                        if(tempCa.getString("header").equals(gridConfObj.getDisplayfield())){
                                            tempCa.put("dataIndex", gridConfObj.getName());
                                            break;
                                        }
                                    }
                                }
                            }
                            conf.getJSONObject(i).remove("reportConf");
                        }
                    }
                }
                if(temp.has("items")){
                    JSONArray items = temp.getJSONArray("items");
                    if(items.length() != 0){
                        ret = storeReportConf(items, pTable, tbar, bbar);
                    }
                }
            }
            ret = conf.toString();
        } catch (JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("FormServlet.storeReportConf", e);
        } catch (ServiceException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("FormServlet.storeReportConf", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("FormServlet.storeReportConf", e);
        }
        return ret;
    }
    public String getAllForms(String moduleid) throws IOException, ServiceException {
        String result = "{\"success\":true}";
        JSONObject ret = new JSONObject();
        try {
            ret.put("valid", true);
            String[] key = {"formid", "name","jdata"};
            String companyId = sessionHandlerDao.getCompanyid();
            String hql ="select count(*) as count from com.krawler.esp.hibernate.impl.mb_forms as mb_forms where mb_forms.moduleid.reportid = ? and (mb_forms.companyid is null or mb_forms.companyid = ?)";
            List list = find(hql,new Object[] {moduleid, companyId});
            Iterator ite = list.iterator();
            long count = 0;
            if( ite.hasNext() ) {
               count = ((Number)ite.next()).longValue();
            }

            hql ="select mb_forms.formid, mb_forms.name, mb_forms.data from com.krawler.esp.hibernate.impl.mb_forms as mb_forms " +
                     "where mb_forms.moduleid.reportid = ? and (mb_forms.companyid is null or mb_forms.companyid = ?)";
            list = find(hql,new Object[] {moduleid, companyId});
            ite = list.iterator();
            String[][] values = new String[(int)count][key.length];
            int i = 0;
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                values[i][0] = row[0].toString();
                values[i][1] = row[1].toString();
                values[i++][2] = "{\"jsondata\":\""+java.net.URLEncoder.encode(row[2].toString())+"\"}";
            }
            String buttonConf = reportDao.getButtonConf(moduleid);
            String formConf = createJsonStart() + createJson(key, values, count) + createJsonEnd();
            ret.put("success", true);
            ret.put("data", new JSONObject(formConf));
            ret.put("buttonConf", buttonConf);
            result = ret.toString();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"valid\": true, \"success\":false}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return result;
    }

    public String createModule(HttpServletRequest request) throws ServiceException {
        String result = "{'success':false}";
        HashMap<String,String> arrParam = new HashMap<String,String>();
        ArrayList<FileItem> fi = new ArrayList<FileItem>();
        boolean fileUpload=false;
        parseRequest(request, arrParam, fi, fileUpload);
        try {
            String companyId = sessionHandlerDao.getCompanyid();
            String query ="select max(mb_modules.reportkey) as count from com.krawler.esp.hibernate.impl.mb_reportlist as mb_modules";
            List list = executeQuery(query);
            Iterator ite = list.iterator();
            String mkey ="1";
            if(ite.hasNext()) {
                Object cnt = (Object) ite.next();
                if(cnt == null) {
                    mkey = "1";
                } else {
                    mkey = Integer.toString(Integer.parseInt(cnt.toString())+1);
                }
            }
            mkey = reportDao.toLZ(Integer.parseInt(mkey), 3);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(ModuleBuilderController.USER_DATEPREF);
            java.sql.Timestamp timestamp1 = Timestamp.valueOf(sdf.format(new java.util.Date()));
//            String moduelname = request.getParameter("name").replace(" ","");
            String moduelname = arrParam.get("name").replace(" ","").toLowerCase();
            String tableName = "mb_"+mkey+"_"+moduelname;

            mb_reportlist module = new com.krawler.esp.hibernate.impl.mb_reportlist();

//            module.setReportname(request.getParameter("name"));
            module.setReportname(arrParam.get("name"));
            module.setTablename(tableName);
            module.setReportkey(Integer.parseInt(mkey));
            module.setCompanyid(companyId);
//            module.setReportlabel(request.getParameter("label"));
            module.setCreateddate(timestamp1);
            module.setCreatedby(sessionHandlerDao.getUserid());
            module.setModifieddate(timestamp1);
            
            module.setDisplayconf(Integer.parseInt(arrParam.get("displayconfig")));
            save(module);

            String formid =UUID.randomUUID().toString();
            String defaultdata =
                  "{" +
                    "xtype:'form'," +
                    "border:false," +
                    "autoHeight:true,"+
                    "cls:'quickInsFrmPnl',"+
                    "items:[{layout:'column'," +
                    "id:'1_"+formid+"'," +
                    "border:false," +
                    "items:[" +
                        "{columnWidth:0.5,id:'2_"+formid+"',border:false," +
                            "bodyStyle:'border : 0pt solid'," +
                            "items:[{xtype:'fieldset',id:'3_"+formid+"'," +
                                    "bodyStyle:'border : 0pt solid'," +
                                    "autoHeight:true}" +
                             "]}," +
                        "{columnWidth:0.5,id:'5_"+formid+"',border:false," +
                            "items:[{xtype:'fieldset',id:'6_"+formid+"'," +
                                    "border:false,autoHeight:true" +
                                   "}]" +
                        "}," +
                        "{columnWidth:1,id:'7_"+formid+"',border:false," +
                            "items:[{xtype:'fieldset',id:'UserFieldsFS'," +
                                    "border:false,autoHeight:true,bodyStyle:'width:500px'" +
                                   "}]" +
                        "}" +
                    "]}]}";
            com.krawler.esp.hibernate.impl.mb_forms form = new com.krawler.esp.hibernate.impl.mb_forms();
            form.setData(defaultdata);
            form.setFormid(formid);
            form.setModuleid(module);
            form.setCompanyid(companyId);
            String abstractInd = arrParam.get("abstractInd");
            form.setAbstractInd("on".equals(abstractInd)? true: false);
            form.setName(arrParam.get("name"));
//            form.setName(request.getParameter("name"));
            save(form);
            //moduleBuilderGenerateTable.createModuleConfigTable(session,module.getModuleid());
            result = "{'success':true}";
//            String actionType = "Add Module";
//            String details = arrParam.get("name")+" Module Added";
//            long actionId = AuditTrialHandler.getActionId(session, actionType);

            if(fi.size()>0){
                    com.krawler.esp.handlers.genericFileUpload uploader = new com.krawler.esp.handlers.genericFileUpload();
                    String destinationdir = PropsValues.STORE_PATH;
                    uploader.uploadFile(fi.get(0),destinationdir,module.getReportid());
                    if(!uploader.ErrorMsg.equals("")){
                        result = "{'success':true,error:'"+uploader.ErrorMsg+"'}";
                    }
            }

            //Add access right permissions
                com.krawler.esp.hibernate.impl.mb_permmaster permmaster = null;
                mb_permgrmaster permgrmaster = new mb_permgrmaster();
//                accessRight.addPermGrp(session, permgrmaster, modObj, taskObj);
                String grname = module.getReportname() +" Permissions ("+"mb_"+module.getReportkey()+"_"+module.getReportname()+")";
                permgrmaster.setPermgrname(grname);
                permgrmaster.setDescription(grname);
                permgrmaster.setReportid(module);
//              permgrmaster.setTaskid(taskObj);
                permgrmaster.setTaskflag(1);
                save(permgrmaster);

                for (int i = 2; i < 10; i++) {
//                    if (i < 5 || ((i == 5 || i == 6) && commentFlag) || ((i == 7 || i == 8) && docFlag)) {
                        permmaster = new com.krawler.esp.hibernate.impl.mb_permmaster();
                        mb_permactions permaction = (mb_permactions) get(mb_permactions.class, i);
                        permmaster.setPermaction(permaction);
                        permmaster.setPermname(permaction.getName());
                        permmaster.setDescription(permaction.getName());
                        permmaster.setPermgrid(permgrmaster);
                        permmaster.setPermid(accessRightDao.getMaxPermid(permgrmaster.getPermgrid()));
                        save(permmaster);
//                    }
                }

//            com.krawler.esp.hibernate.impl.mb_permmaster permmaster = null;
//            mb_permgrmaster permgrmaster = new mb_permgrmaster();
//            accessRight.addPermGrp(session,permgrmaster,module);
//            for(int i=2;i<9;i++) {
//                permmaster = new com.krawler.esp.hibernate.impl.mb_permmaster();
//                mb_permactions permaction = (mb_permactions) session.load(mb_permactions.class,i);
//                permmaster.setPermaction(permaction);
//                permmaster.setPermname(permaction.getName());
//                permmaster.setDescription(permaction.getName());
//                permmaster.setPermgrid(permgrmaster);
//                permmaster.setPermid(accessRight.getMaxPermid(session,permgrmaster.getPermgrid()));
//                session.save(permmaster);
//            }
            //Done by sm/anup
             JSONObject jobj = new JSONObject();
             JSONObject jtemp2 = new JSONObject();
             jtemp2.put("modulename",module.getReportname());
             jtemp2.put("moduleid",module.getReportid());
             jtemp2.put("tablename",module.getTablename());
             jtemp2.put("reportkey",module.getReportkey());
             jtemp2.put("dateval",module.getCreateddate());
             jtemp2.put("id","module_"+module.getReportid());
             jtemp2.put("formid",form.getFormid());
             jobj.append("data", jtemp2.toString());
             jobj.put("success", "true");
             result = jobj.toString();

//            AuditTrialHandler.insertAuditLog(session, actionId, details, request);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{'success':false}";
            throw ServiceException.FAILURE("FormServlet.createModule", e);
        } finally {
            return result;
        }
    }

    public String editModule(HttpServletRequest request) throws ServiceException {
        String result = "{'success':false}";
        HashMap<String, String> arrParam = new HashMap<String, String>();
        ArrayList<FileItem> fi = new ArrayList<FileItem>();
        boolean fileUpload = false;
        parseRequest(request, arrParam, fi, fileUpload);
        try {
            mb_reportlist module = (mb_reportlist) get(mb_reportlist.class, arrParam.get("id"));
            module.setDisplayconf(Integer.parseInt(arrParam.get("displayconf")));
            save(module);
//            String actionType = "Edit Module";
//            String details = arrParam.get("modulename") + " Module edited";
//            long actionId = AuditTrialHandler.getActionId(session, actionType);

            if (fi.size() > 0) {
                com.krawler.esp.handlers.genericFileUpload uploader = new com.krawler.esp.handlers.genericFileUpload();
                String destinationdir = PropsValues.STORE_PATH;
                uploader.uploadFile(fi.get(0), destinationdir, module.getReportid());
                if (!uploader.ErrorMsg.equals("")) {
                    result = "{'success':true,error:'" + uploader.ErrorMsg + "'}";
                }
            }
            JSONObject jobj = new JSONObject();
            jobj.put("success", "true");
            result = jobj.toString();
//            AuditTrialHandler.insertAuditLog(session, actionId, details, request);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{'success':false}";
            throw ServiceException.FAILURE("FormServlet.createModule", e);
        } finally {
            return result;
        }
    }
    public String getAllModules(String ss, String sort, String dir, int start, int limit) throws ServiceException {
        JSONObject jobj = new JSONObject();
        String result = "{'TotalCount':0,'data':[]}";
        long count = 0;
        try {
            ArrayList<Object> al = new ArrayList<Object>();
            String s1 = "";
            String companyId = sessionHandlerDao.getCompanyid();
            al.add(companyId);
            if(ss != null){
                s1= StringUtil.getSearchString(ss, "and", new String[] {"mb_modules.reportname"});
                StringUtil.insertParamSearchString(al, ss, 1);
            }
            
            String hql = "select count(mb_forms.moduleid) as count " +
                    "from com.krawler.esp.hibernate.impl.mb_forms as mb_forms inner join mb_forms.moduleid as mb_modules where mb_modules.deleteflag = 0 and mb_modules.type = 0 and (mb_forms.companyid is null or mb_forms.companyid = ?) "+ s1;
            List list = find(hql, al.toArray());
            Iterator ite = list.iterator();
            if( ite.hasNext() ) {
                count = ((Number) ite.next()).longValue();
            }
            hql = "select mb_modules.reportid, mb_modules.reportname, mb_modules.tablename, mb_modules.createddate,mb_forms.formid,mb_modules.reportkey,mb_modules.displayconf, mb_forms.deployedInd, mb_forms.abstractInd " +
                    "from "+PropsValues.PACKAGE_PATH+".mb_forms as mb_forms inner join mb_forms.moduleid as mb_modules where mb_modules.deleteflag = 0 and mb_modules.type = 0 and (mb_forms.companyid is null or mb_forms.companyid = ?) "+ s1 +" order by ";//mb_modules.modulename ";
            String subhql = "";
            if(sort!=null && !sort.equals("")){
                subhql = " mb_modules."+sort+" "+dir;
            }else{
                subhql = " mb_modules.reportname";
            }
            hql += subhql;

//TODO
            List<Object[]> moduleList = executeQueryPaging(hql, al.toArray(), new Integer[] {start, limit});
            if (moduleList != null && !moduleList.isEmpty())
            {
                for (Object[] row: moduleList)
                {
                    JSONObject jtemp2 = new JSONObject();
                    jtemp2.put("moduleid", row[0]);
                    jtemp2.put("modulename", row[1]);
                    jtemp2.put("tablename", row[2]);
                    jtemp2.put("dateval", row[3]);
                    jtemp2.put("formid", row[4]);
                    jtemp2.put("reportkey", row[5]);
                    jtemp2.put("displayconf", row[6]);
                    jtemp2.put("displayInd", row[7]);
                    jtemp2.put("abstractInd", row[8]);
                    jobj.append("data", jtemp2);
                }
            }
            
            jobj.put("TotalCount", count);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{'TotalCount':0,'data':[]}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{'TotalCount':0,'data':[]}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        } finally {
            if(count > 0) {
                return jobj.toString();
            } else {
                return result;
            }
        }
    }
     public String  getModuleForCombo(HttpServletRequest request) throws ServiceException {
            JSONObject jobj = new JSONObject();
        String result = "{'data':[]}";
        try {
            String sql = "";
            List ls = null;
            Iterator ite = null;
            String companyId = sessionHandlerDao.getCompanyid();
            sql = "select mb_reportlist.reportid, mb_reportlist.reportname, mb_reportlist.tablename, 0 as type from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist where mb_reportlist.deleteflag = 0 and (mb_reportlist.companyid is null or mb_reportlist.companyid = ?) order by mb_reportlist.reportname ";
            ls = executeQuery(sql, companyId);
            ite = ls.iterator();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject  jtemp2 = new JSONObject();
                jtemp2.put("moduleid", row[0]);
                jtemp2.put("modulename", row[1]);
                jtemp2.put("tablename", row[2]);
                jtemp2.put("mastertype", row[3]);
                jobj.append("data", jtemp2);
            }

            result = jobj.toString();
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{'data':[]}";
            throw ServiceException.FAILURE("FormServlet.getAllModulesForCombo", e);
        } finally {
            return result;
        }
     }
    public String getAllModulesForCombo(HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        String result = "{'data':[]}";
        try {
            String sql = "";
            List ls = null;
            Iterator ite = null;
            String companyId = sessionHandlerDao.getCompanyid();
            if(request.getParameter("reportFlag") == null) {
                sql = "select mb_reportlist.reportid, mb_reportlist.reportname, mb_reportlist.tablename, 0 as type " +
                        "from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist " +
                        "where mb_reportlist.reportid !=? and mb_reportlist.deleteflag = 0 and (mb_reportlist.companyid is null or mb_reportlist.companyid = ?) order by mb_reportlist.reportname ";
                ls = find(sql,new Object[]{request.getParameter("moduleid"), companyId});
                ite = ls.iterator();
                while (ite.hasNext()) {
                    Object[] row = (Object[]) ite.next();
                    JSONObject  jtemp2 = new JSONObject();
                    jtemp2.put("moduleid", row[0]);
                    jtemp2.put("modulename", row[1]);
                    jtemp2.put("tablename", row[2]);
                    jtemp2.put("mastertype", row[3]);
                    jobj.append("data", jtemp2);
                }
            }
            sql = " select mb_configmaster.configid as moduleid, mb_configmaster.name as modulename,'mb_configmasterdata' as tablename, 1 as mastertype " +
                    "from com.krawler.esp.hibernate.impl.mb_configmaster as mb_configmaster " +
                    "order by mb_configmaster.name ";
            ls = executeQuery(sql);
            ite = ls.iterator();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject  jtemp2 = new JSONObject();
                jtemp2.put("moduleid", row[0]);
                jtemp2.put("modulename", row[1]);
                jtemp2.put("tablename", row[2]);
                jtemp2.put("mastertype", row[3]);
                jobj.append("data", jtemp2);
            }
            result = jobj.toString();
        } catch(Exception ex){
              logger.warn(ex.getMessage(), ex);
        }finally {
            return result;
        }
    }

    public String getForm(String id,String reportid, String taskid) throws IOException, ServiceException {
        String result = "{\"success\":true}";
        try {
            String hql ="select mb_forms.data from com.krawler.esp.hibernate.impl.mb_forms as mb_forms " +
                     "where mb_forms.formid = ?";
            List list = executeQuery(hql,id);
            Iterator ite = list.iterator();
            String[][] values = new String[1][1];
            if( ite.hasNext() ) {
                Object data = (Object) ite.next();
                if(data!=null) {
                    if(!StringUtil.isNullOrEmpty(data.toString()) && !data.toString().equals("{}")) {
                        values[0][0] = java.net.URLEncoder.encode(data.toString());
                        result = createJsonStart() + createJson(new String[]{"jsondata"}, values, 1) + createJsonEnd();
                    } else
                        result = createJsonStart() +createJsonEnd();
                }
                else {
                    result = createJsonStart() +createJsonEnd();
                }
            } else {
                return "{\"success\":false}";
            }
            String permObj = reportDao.getModulePermission(reportid);
            result = result.substring(0,result.length()-1)+"," +permObj.substring(1);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return result;
    }
    public String deleteForm(String formid) throws ServiceException {
        String result = "{\"success\":true}";
        try {
            String hql = "delete from com.krawler.esp.hibernate.impl.mb_forms as mb_forms where mb_forms.formid = ? " ;
            int num=executeUpdate(hql, formid);
            if (num == 0) {
                return "{\"success\":false}";
            }
//            String formName = AuditTrialHandler.getFormName(session, formId);
//            String details = formName+" Form Deleted";
//            String actionType = "Delete Form";
//            long actionId = AuditTrialHandler.getActionId(session, actionType);
//            AuditTrialHandler.insertAuditLog(session, actionId, details, request);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return result;
    }

    public String deleteModule(String moduleid) {
        String result = "{\"success\":true}";
        try {
            String formTableName = getReportTableName(moduleid);
//            SQLQuery sqlQuery=null;
//            sqlQuery = session.createSQLQuery("drop table " + formTableName);
//            sqlQuery.executeUpdate();
            if (!reportDao.isReferred(formTableName, moduleid)) {
                ServiceBuilder sb = new ServiceBuilder();
                sb.deleteModuleStuf(formTableName);
                String deleteQuery = "update com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist set mb_reportlist.deleteflag = 1 where mb_reportlist.reportid = ?";
                int num = executeUpdate(deleteQuery,moduleid);
                if (num == 0) {
                    return "{\"success\":false,\"msg\":\"Error occurred at server\"}";
                }
//                String moduleId = request.getParameter("moduleid");
//                String moduleName = AuditTrialHandler.getReportName(session, moduleId);
//                String details = moduleName+" Module Deleted";
//                String actionType = "Delete Module";
//                long actionId = AuditTrialHandler.getActionId(session, actionType);
//                AuditTrialHandler.insertAuditLog(session, actionId, details, request);
            }else{
                result = "{\"success\":false,\"msg\":\"Some module references this module\"}";
            }
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"msg\":\"Error occurred at server\"}";
        }
        return result;
    }
    public static String createJsonStart() {
        return "{data:[";
    }

    public String createJson(String[] key, String[][] value, long count)throws ServiceException{
        String str = "";
        try {
            for (int i = 0; i < count; i++) {
                str += "{";
                for (int j = 0; j < key.length; j++) {
                    str += key[j] + ":'" + value[i][j] + "',";
                }
                str = str.substring(0, str.length() - 1) + "},";
            }
            if (str.equals("")) {
                return "";
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("FormServlet.createJson", e);
        }
        return str.substring(0, str.length() - 1);
    }

    public static String createJsonEnd() {
        return "]}";
    }
    public ArrayList<Hashtable<String, String>> getColumnInfo(String moduleid) {
        ArrayList<Hashtable<String, String>> aList = new ArrayList<Hashtable<String, String>>();
        mb_reportlist basemodule = (mb_reportlist) get(mb_reportlist.class, moduleid);
        String SELECT_QUERY = "select mb_gridconfig.name,mb_gridconfig.xtype,mb_gridconfig.displayfield,mb_gridconfig.reftable, " +
                "mb_gridconfig.combogridconfig from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig where reportid = ? order by columnindex ";
        List list = executeQuery(SELECT_QUERY, basemodule);
        Iterator ite = list.iterator();
        int i = 0;
        while( ite.hasNext() ) {
            Object[] row = (Object[]) ite.next();
            aList.add(i,new Hashtable<String, String>());
            aList.get(i).put("name", row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[1]);
            aList.get(i).put("configtype", row[1].toString());
            aList.get(i).put("displayfield", row[2]==null ? "" : row[2].toString());
            aList.get(i).put("reftable", row[3]==null ? "" : row[3].toString());
            aList.get(i).put("refflag", row[4].toString());
            i++;
        }
        return aList;
    }
    public String getColumndata(Object value,Hashtable<String, String> ht,
            String tableName, mb_reportlist basemodule, JSONObject jtemp, pm_taskmaster taskObj) throws ServiceException{
        String result = value.toString();
        if(ht.get("configtype").equals("combo")){
            String columnName = ht.get("name").substring(0, ht.get("name").length()-2);
            if(ht.get("refflag").equals("-1")) { // not combogridconfig
                try {
                    Class clrefTable = value.getClass();
                    Class[] arguments = new Class[]{};

                    //check for delete flag
                    java.lang.reflect.Method objMethod = clrefTable.getMethod("getDeleteflag", arguments);
                    Object result1 = objMethod.invoke(value,new Object[]{});
                    if(Double.parseDouble(result1.toString()) == 0) {
                        boolean conditionFlag = true;//ModuleBuilderController.checkFilterRules(session, taskObj, basemodule, value, 2);
                        if(conditionFlag) {
                            String methodName =  "get"+TextFormatter.format(getPrimaryColName(ht.get("reftable")), TextFormatter.G);
                            objMethod = clrefTable.getMethod("get"+TextFormatter.format(columnName, TextFormatter.G), arguments);
                            result1 = objMethod.invoke(value,new Object[]{});
                            result = result1.toString();
                            if (result.startsWith("com.krawler")){
                                 mb_configmasterdata mb_configmasterdata=(mb_configmasterdata)result1;
                                result= mb_configmasterdata.getMasterdata();
                            }
                            objMethod = clrefTable.getMethod(methodName, arguments);
                            result1 = objMethod.invoke(value,new Object[]{});
                            result = result1.toString()+PropsValues.REPORT_HARDCODE_STR+result;

                            //fetch read only fields data
                            String SELECT_QUERY = "select mb_gridconfig.name,mb_gridconfig.displayfield,mb_gridconfig.reftable, " +
                                    "mb_gridconfig.combogridconfig from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig where reportid = ? and mb_gridconfig.xtype = ? and mb_gridconfig.name like ? order by columnindex ";
                            List list = find(SELECT_QUERY, new Object[] {basemodule, "readOnlyCmp", "%"+PropsValues.REPORT_HARDCODE_STR+ht.get("name")});
                            Iterator ite = list.iterator();
                            while( ite.hasNext() ) {
                                Object[] row = (Object[]) ite.next();
                                String[] parentFieldName = row[0].toString().split(PropsValues.REPORT_HARDCODE_STR);

                                objMethod = clrefTable.getMethod("get"+TextFormatter.format(parentFieldName[1], TextFormatter.G), arguments);
                                result1 = objMethod.invoke(value,new Object[]{});
                                jtemp.put(parentFieldName[0], result1.toString());
                            }
                        } else {
                            result = "conditionnotmatch";
                        }
                    } else {
                        result = "deleted";
                    }

                } catch (IllegalAccessException ex) {
                    logger.warn(ex.getMessage(), ex);
                } catch (IllegalArgumentException ex) {
                    logger.warn(ex.getMessage(), ex);
                } catch (InvocationTargetException ex) {
                    logger.warn(ex.getMessage(), ex);
                } catch (NoSuchMethodException e) {
                    logger.warn(e.getMessage(), e);
                   throw ServiceException.FAILURE("FormServlet.getColumndata", e);
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                   throw ServiceException.FAILURE("FormServlet.getColumndata", e);
                }

            }else{
                mb_configmasterdata obj = (mb_configmasterdata) value;
                result = obj.getMasterid()+PropsValues.REPORT_HARDCODE_STR+obj.getMasterdata();
            }
        }else if( ht.get("configtype").equals("select")){
            if(result.length()>0) {
                String[] valueArray=result.split(",");
                String str="(";
                for(int j=0;j<valueArray.length;j++ ){
                    str+="'"+valueArray[j]+"',";
                }
                str=str.substring(0, str.length()-1);
                str+=")";

                String sql = "";
                List li = null;
                Iterator ite = null;
                if(ht.get("refflag").equals("-1")) {// now combogridconfig
                        String columnName = ht.get("name").substring(0, ht.get("name").length()-2);
                        sql = "select t1."+columnName+" from "+PropsValues.PACKAGE_PATH +"."+ht.get("reftable")+" as t1  where t1.id in "+str;
                        li = executeQuery(sql);
                        ite = li.iterator();
    //                    rs = DbUtil.executeQuery(conn,sql,new Object[]{value});
                    } else {
                        sql = "select t1.masterdata as name from "+PropsValues.PACKAGE_PATH +".mb_configmasterdata as t1 where t1.configid = ? and t1.masterid in "+str+" order by t1.masterdata";
                        li = find(sql, new Object[]{ht.get("refflag")});
                        ite = li.iterator();
    //                    rs = DbUtil.executeQuery(conn,sql, new Object[]{ht.get("reftable"),value});
                    }
                    str="";
                    while(ite.hasNext()){
                        str+=ite.next()+",";
                    }
                    str = str.length()>0 ? str.substring(0, str.length()-1) : "";
                    result += PropsValues.REPORT_HARDCODE_STR + str;
            }
        }else if(ht.get("configtype").equals("file")){
            if(!result.equals("")){
                mb_docs docObj = (mb_docs) get(mb_docs.class,result);

                 String Url = "fileDownload.jsp?url=" + tableName + "/" +docObj.getStorename() + "&docid=" + result + "&attachment=true";
//               String Url = "fileDownload.jsp?url="+tableName+"/"+result+"&attachment=true";
               result = "<img src='images/download.png' style='cursor: pointer;' onclick='setDldUrl(\"" + Url + "\")'  title='Click to download'>";

            }else{
                result = "File not exists";
            }
        }else if(ht.get("configtype").equals("datefield")){
               result=result.split(" ")[0];
        }
        return result;
    }

    public String moduleData(String moduleid) throws ServiceException {
        String retStr = "";
        JSONObject ret = new JSONObject();
        try{            
            ArrayList<Hashtable<String, String>> columnList = getColumnInfo(moduleid);
            retStr += makeColumnHeader(moduleid,columnList);
//            JSONObject temp = new JSONObject();
            ret.put("data", retStr);
            ret.put("buttonConf", reportDao.getButtonConf(moduleid));
            mb_reportlist moduleObj = (mb_reportlist) get(mb_reportlist.class,moduleid);
            ret.put("displayConf", moduleObj.getDisplayconf());
            JSONObject jobj = new JSONObject();
            jobj.put("data", ret);
            jobj.put("valid", true);
            retStr = jobj.toString();
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
//        retStr +=  "{columnheader:["
//              + makeColumnHeader(session,request.getParameter("moduleid"),columnList)
//                            + "]}";
//        retStr += ","+getStdConfig(session,request.getParameter("moduleid"))+"}";
        return retStr;
    }

    public String makeColumnHeader(String moduleid,ArrayList<Hashtable<String, String>> columnList) throws ServiceException {
        String returnColumnHeader = "";
        JSONObject jtemp = new JSONObject();
        try {
            jtemp.put("columnheader", new JSONArray());
//            String configtableName = PropsValues.PACKAGE_PATH +"."+getConfigTableName(session, moduleid);
            String tableName = getReportTableName(moduleid);
            mb_reportlist reportid = (mb_reportlist) get(mb_reportlist.class, moduleid);
            String SELECT_QUERY = "select mb_gridconfig.name,mb_gridconfig.hidden,mb_gridconfig.xtype,mb_gridconfig.displayfield,mb_gridconfig.renderer,mb_gridconfig.summaryType from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig where mb_gridconfig.reportid = ? order by columnindex ";
            List list = find(SELECT_QUERY,new Object[]{reportid});
            Iterator ite = list.iterator();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject jObj = new JSONObject();
                String nameVal = "";
                if(row[2].toString().equals("readOnlyCmp"))
                    nameVal = row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[0];
                else
                    nameVal = row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[1];

                if(!row[2].toString().equals("default") || nameVal.equals("name"))
                    jObj.put("0",row[3].toString());            //  column header
                else
                    jObj.put("0",nameVal);
                jObj.put("1",nameVal);  // dataindex
                jObj.put("3",Boolean.parseBoolean(row[1].toString())?true:false);    // is hidden
                jObj.put("4",true);//groupable
                jObj.put("5",true);//sortable
                jObj.put("conftype", row[2]); //xtype
                if("file".equals(row[2]))
                    jObj.put("fileinfo", getFileStore(tableName, nameVal));
                renderer renderer = (renderer) row[4];
                if (!renderer.getId().equals("0") && !renderer.getId().equals("")) {
                        jObj.put("6", renderer.getRendererValue());  //renderergridconfig
                }
                if (row[5] !=null && !row[5].toString().equalsIgnoreCase("None")){
                    jObj.put("7",row[5]) ; // summaryType
                }

                jtemp.append("columnheader", jObj);
//                returnColumnHeader = jObj.toString()+","+returnColumnHeader;
                //returnColumnHeader += jObj.toString()+",";
            }
            for (int i = 0; i <columnList.size() ; i++) {
                if(columnList.get(i).get("configtype").equals("combo") || columnList.get(i).get("configtype").equals("select")) {
                    JSONObject jObj = new JSONObject();
                    String dataIndex = columnList.get(i).get("name")+"_id";
                    jObj.put("0",dataIndex);
                    jObj.put("1",dataIndex);
                    jObj.put("3",true);
                    jObj.put("4",true);
                    jObj.put("5",true);
                    //jObj.put("6",row[2].toString());//xtype
                    jtemp.append("columnheader", jObj);
//                    returnColumnHeader = jObj.toString()+","+returnColumnHeader;
                    //returnColumnHeader += jObj.toString()+",";
                }
             }

             SELECT_QUERY = "select mb_moduleConfigMap.configid.configid from com.krawler.esp.hibernate.impl.mb_moduleConfigMap " +
                    "as mb_moduleConfigMap where mb_moduleConfigMap.moduleid = ? ";

            list = find(SELECT_QUERY, new Object[]{reportid});
            ite = list.iterator();
            while( ite.hasNext() ) {
                int configid = (Integer) (ite.next());
                JSONObject jObj = new JSONObject();
                jObj = new JSONObject();
                if(configid == 1) { // Comments
                    jObj.put("0","Comments");            // column header
                    jObj.put("1","comments");                       // dataindex
                    jObj.put("2","None");            // editor for editgridpanel
                    jObj.put("3",false);// is hidden
                    jObj.put("4","mb_stdConfigs");            // reference table
                    jObj.put("5","-1");  //combogridconfig
                    jtemp.append("columnheader", jObj);
//                    returnColumnHeader += jObj.toString()+",";
                }else if(configid == 2){     //documents
                    jObj.put("0","Documents");           // column header
                    jObj.put("1","docs_id");      // dataindex
                    jObj.put("3",false);            // is hidden
                    jObj.put("4",true);             //groupable
                    jObj.put("5",true);             //sortable
                    jtemp.append("columnheader", jObj);
//                    returnColumnHeader = jObj.toString()+","+returnColumnHeader;
                }
            }
             jtemp.put("stdconfig", getStdConfig(moduleid));



        }
        catch(Exception e) {
            logger.warn(e.getMessage(), e);
          throw ServiceException.FAILURE("FormServlet.makeColumnHeader", e);
        }
        return jtemp.toString();
    }

    private JSONArray getFileStore(String tname, String fname) throws JSONException{
        String query = "select docname, storename,docid from mb_docs where docid in (select "+fname+" from "+tname+")";
        List l = (List)executeNativeQuery(query);
        JSONArray jArr=new JSONArray();
        for (Object object : l) {
            Object[] row = (Object[])object;
            JSONObject obj = new JSONObject();
            obj.put("docname", row[0]);
            obj.put("docpath", tname+'/'+row[1]);
            obj.put("docid", row[2]);
            jArr.put(obj);
        }
        return jArr;
    }

    public ArrayList<String> getSearchfield(String moduleid,String formTableName) throws ServiceException{
        String result = "";
        String query = "from "+PropsValues.PACKAGE_PATH+".mb_gridconfig as mb_gridconfig  where mb_gridconfig.reportid = ? and mb_gridconfig.hidden !=?";
        mb_reportlist obj = (mb_reportlist) get(mb_reportlist.class, moduleid);
        List li = find(query,new Object[] {obj,true});
        ArrayList<String> al = new ArrayList<String>();
        Iterator ite = li.iterator();
        while(ite.hasNext()){
            mb_gridconfig gridConfigObj = (mb_gridconfig) ite.next();
            if(gridConfigObj.getXtype().equals("combo")){
                if(!gridConfigObj.getCombogridconfig().equals("-1")){
                    al.add(formTableName+"."+gridConfigObj.getName().split(PropsValues.REPORT_HARDCODE_STR)[1]+".masterdata");
                }else{
                    String colName = formTableName+"."+gridConfigObj.getName().split(PropsValues.REPORT_HARDCODE_STR)[1]+"."+gridConfigObj.getName().split(PropsValues.REPORT_HARDCODE_STR)[1];
                    al.add(colName);
                }
            }else if(gridConfigObj.getXtype().equals("textfield") || gridConfigObj.getXtype().equals("textarea")){
                String colName = gridConfigObj.getName().replaceAll(PropsValues.REPORT_HARDCODE_STR,".");
                al.add(colName);
            }
        }
        return al;
    }/*
    public static String getInnerQuery(Session session,String moduleid,JSONArray jsonArry,ArrayList<String> searchAl) throws ServiceException{
        String result = "";
        String formTableName = moduleBuilderGenerateTable.getFormTableName(conn, moduleid) ;
        try {
            for(int i=0;i<jsonArry.length();i++){
                JSONObject jobj = jsonArry.getJSONObject(i);
                if(jobj.getString("configtype").equals("combo")){
                    result += " inner join ";
                    if(jobj.getString("refflag").equals("0")) {
                        result += "`"+jobj.getString("reftable")+"` on `" +jobj.getString("reftable")+"`.id = `"+formTableName+"`."+jobj.getString("name");
                        searchAl.add("`" +jobj.getString("reftable")+"`.`name`");
                    } else {
                           result +="(select masterid,masterdata from mb_configmasterdata where configid= '"+jobj.getString("reftable")+"') as t1 on t1.masterid = `"+formTableName+"`."+jobj.getString("name")+" ";
//                         result += "`mb_configmasterdata` on `mb_configmasterdata`.masterid = `"+formTableName+"`."+jobj.getString("name")+" ";
                         searchAl.add("`t1`.`masterdata`");
                    }
                }else{
                    searchAl.add("`"+jobj.getString("name")+"`");
                }
            }
        } catch (JSONException ex) {
              throw ServiceException.FAILURE("formServelate.getInnerQuery", ex);
        }
//        String result = "";
//        String configtableName = getConfigTableName(conn, moduleid);
//        String formTableName = getFormTableName(conn, moduleid) ;
//        String query = "Select name,refflag, reftable,configtype from `"+configtableName+"` where searchable = '1'" ;
//        ArrayList<String> al = new ArrayList<String>();
//        DbResults rs = DbUtil.executeQuery(conn, query);
//        while(rs.next()){
//             if(rs.getString("configtype").equals("combo")){
//                 result += " inner join ";
//                 if(rs.getInt("refflag") == 0) {
//                    result += "`"+rs.getString("reftable")+"` on `" +rs.getString("reftable")+"`.id = `"+formTableName+"`."+rs.getString("name");
//                } else {
//                     result += "`mb_configmasterdata` on `mb_configmasterdata`.masterid = `"+formTableName+"`."+rs.getString("name")+" ";
//                }
//             }
//        }
        return result;
    }
     */

    public String createInnerQuery(String moduleid,ArrayList<Hashtable<String, String>> arrList) throws ServiceException{
        String result = "";
        String innerQuery = "";
        for(int i = 0;i<arrList.size();i++){
            Hashtable<String, String> ht = arrList.get(i);
            if(ht.get("configtype").equals("combo")){
                 if(ht.get("refflag").equals("0")) {
                     innerQuery += "inner join "+PropsValues.PACKAGE_PATH +"."+ht.get("reftable")+" as "+ht.get("reftable")+" ";
                 }else{
                     innerQuery += "inner join "+PropsValues.PACKAGE_PATH +".mb_configmasterdata as mb_configmasterdata ";
                 }
            }
        }
        return innerQuery;
    }

    public String getGridData(HttpServletRequest request) throws ServiceException{
        String retStr = "";
        PreparedStatement pstmt = null;
        JSONObject jobj = new JSONObject();
        String searchFilter="";
        try {
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String formtablename = reportDao.getReportTableName(request.getParameter("moduleid"));
            ArrayList<Object> al = new ArrayList<Object>();
            String s1 = "";
            String ss = "";
            String moduleid = request.getParameter("moduleid");
            mb_reportlist reportid = (mb_reportlist) get(mb_reportlist.class, moduleid);

            //Build filter query
//            ArrayList permArray = AuthHandler.getRealRoleids(request);
            String filterQuery = "";
//            for(int i = 0; i < permArray.size(); i++) {
//                int roleid = Integer.parseInt(permArray.get(i).toString());
//                String res = checkFilterRulesQuery(session, reportid, roleid, 0, "");
//                if(!StringUtil.isNullOrEmpty(res)) {
//                    res = "("+res +")";
//                    if(!StringUtil.isNullOrEmpty(filterQuery))
//                        filterQuery = res + " or " + filterQuery;
//                    else
//                        filterQuery = res;
//                }
//            }

            if(!StringUtil.isNullOrEmpty(filterQuery))
                filterQuery = " and " + filterQuery;

            //Build condition query
            String conditionQuery = buildConditionQuery(request);
            if(!StringUtil.isNullOrEmpty(conditionQuery))
                conditionQuery = " and " + conditionQuery;

            //Check for comments and documents
            String SELECT_QUERY = "select mb_moduleConfigMap.configid.configid from com.krawler.esp.hibernate.impl.mb_moduleConfigMap " +
                    "as mb_moduleConfigMap where mb_moduleConfigMap.moduleid = ? ";
            List configlist = find(SELECT_QUERY, new Object[]{reportid});
            Iterator ite = configlist.iterator();
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

            String name = PropsValues.PACKAGE_PATH+"."+formtablename;
            Class cl = Class.forName(name);
            if(request.getParameter("ss") != null){
                ss = request.getParameter("ss");
                ArrayList<String> searchAl = getSearchfield(request.getParameter("moduleid"),formtablename);
                String[] keywordList = searchAl.toArray(new String[0]);
//                s1 = getInnerQuery(conn,request.getParameter("moduleid"));
                s1 += StringUtil.getSearchString(ss, "and", keywordList);
                StringUtil.insertParamSearchString(al, ss, keywordList.length);
            }
            ArrayList<Hashtable<String, String>> columnList = getColumnInfo(request.getParameter("moduleid"));
            String innerQuery = "";//createInnerQuery(request.getParameter("moduleid"),columnList);
            String sql = "from "+name+" as "+formtablename+" "+innerQuery+" where "+formtablename +".deleteflag = 0 "+filterQuery+conditionQuery+s1;
            String subhql = "";

            if(Boolean.parseBoolean(request.getParameter("isFilter"))) {
                String filterQry = "and "+formtablename+"."+request.getParameter("filterfield").split(PropsValues.REPORT_HARDCODE_STR)[1]+" = '"+request.getParameter("filtervalue")+"'";
                sql += filterQry;
            }
             if(request.getParameter("sort")!=null && !request.getParameter("sort").equals("")){
                 String sortColumnName = request.getParameter("sort");
                subhql = " Order by  "+sortColumnName+" "+request.getParameter("dir");
            }
            if (request.getParameter("filterJson") != null && !request.getParameter("filterJson").equals("")) {

                JSONArray filterJsonObj = new JSONArray(request.getParameter("filterJson"));

                JSONObject ObjJSONObject = null;
                String substr="";
                for (int i = 0; i < filterJsonObj.length(); i++) {
                    ObjJSONObject = (JSONObject) filterJsonObj.get(i);
                    if (ObjJSONObject.getString("xtype").equals("datefield")){
                            String[] splitString=ObjJSONObject.getString("searchText").split(",");
                            String fromDate=splitString[0];
                            String toDate=splitString[1];
                            substr =" >= '" + fromDate + " 00:00:00" + "'"+" and " + formtablename + "." + ObjJSONObject.getString("column")+ " <= '" + toDate + " 00:00:00" + "'";
                    }else if (ObjJSONObject.getString("xtype").equals("numberfield")){
                        substr=" = " + ObjJSONObject.getString("searchText") ;
                    }else if (ObjJSONObject.getString("xtype").equals("radio") || ObjJSONObject.getString("xtype").equals("checkbox")){
                        substr=" = " + Boolean.parseBoolean(ObjJSONObject.getString("searchText")) ;
                    }else if (ObjJSONObject.getString("xtype").equals("timefield")){
                        substr=" = '" + ObjJSONObject.getString("searchText")+ "'";
                    }else if (ObjJSONObject.getString("xtype").equals("combo")) {
                            substr = " = '" + ObjJSONObject.getString("searchText") + "'";
                    }else {
                        substr=" like '%" + ObjJSONObject.getString("searchText") + "%'";
                    }
                    searchFilter += " and " + formtablename + "." + ObjJSONObject.getString("column") + substr;
                }
            }

            sql += searchFilter;
            sql += subhql;

            String countQuery="";
            countQuery="select count(*) ";
            List li = find(countQuery + sql, al.toArray());
            long count=0;
            Iterator ite12 = li.iterator();
            if (ite12.hasNext()) {
                count = ((Number)ite12.next()).longValue();
            }

            li = executeQueryPaging(sql, al.toArray(),new Integer[]{start,limit});
            ite = li.iterator();
            String priColumn = getPrimaryColName(formtablename);
            String recid = "";
            while(ite.hasNext()){
                Class  arguments[] = new Class[] {};
                JSONObject jtemp = new JSONObject();
                Object row = (Object) ite.next();
                boolean delFlag = false;
                for(int i = 0; i <columnList.size() ; i++) {
                    String type = columnList.get(i).get("configtype");
                    String columnName = columnList.get(i).get("name");
                    if(StringUtil.equal(type, "WtfGridPanel") || StringUtil.equal(type, "WtfEditorGridPanel")){
                        String _methodName = "get"+TextFormatter.format("id", TextFormatter.G);
                        java.lang.reflect.Method objMethod = cl.getMethod(_methodName,arguments);
                        Object result1 = objMethod.invoke(cl.cast(row), arguments);
                        String gridRec = getGridData(request, columnList.get(i), result1, formtablename);
//                        putGridData(jtemp, gridRec);
                        jtemp.put(columnName, gridRec);
                    } else if(!StringUtil.equal(type, "readOnlyCmp")){
                        String _methodName = "get"+TextFormatter.format(columnName, TextFormatter.G);
                        java.lang.reflect.Method objMethod = cl.getMethod(_methodName,arguments);
                        Object result1 = objMethod.invoke(cl.cast(row), arguments);
                        if(result1!=null) {
                            //@@@
                            pm_taskmaster taskObj = (pm_taskmaster) get(pm_taskmaster.class, request.getParameter("taskid"));
                            String str = getColumndata(result1,columnList.get(i),formtablename, reportid, jtemp, taskObj);
                            if(!str.equals("deleted") && !str.equals("conditionnotmatch")){
                                String[] dataArray =  str.split(PropsValues.REPORT_HARDCODE_STR);
                                if(dataArray.length > 1) {
                                    jtemp.put(columnName, dataArray[1]);
                                    jtemp.put(columnName+"_id", dataArray[0]);
                                } else
                                    jtemp.put(columnName, dataArray[0]);
                                if(priColumn.equals(columnName)){
                                    recid = dataArray[0];
                                }
                            } else {
                                delFlag = true;
                                break;
                            }
                        }
                    }
                }
                if(delFlag) {
                  continue;
                }

                ArrayList<String> countFieldNameArray = new ArrayList<String>();
                reportMethods rh = new reportMethods(reportDao, sessionHandlerDao);
                rh.getCountCommentDocuments(countFieldNameArray, "", "", commentFlag, docFlag, recid, jtemp);
                jobj.append("data", jtemp);
            }
            if(jobj.has("data")) {
                jobj.put("count", count);
                retStr = jobj.toString();
            }else{
                 retStr = "{data:[],count : 0}";
            }
        }catch(Exception ex){
            logger.warn(ex.getMessage(), ex);
              throw ServiceException.FAILURE("FormServlet.getGridData", ex);
        }finally{
            return retStr;
        }
    }

    public String getGridData(HttpServletRequest request, Hashtable coldata, Object id, String pTable)  {
            String result = "{data:[],count : 0}";
        try {
//            int start = Integer.parseInt(request.getParameter("start"));
//            int limit = Integer.parseInt(request.getParameter("limit"));
            String tName = coldata.get("reftable").toString();
            String reportid = reportDao.getReportIdFromTable(tName);
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
                   finalQuery = ", " + str;
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
            String filtertablename = PropsValues.PACKAGE_PATH+"."+pTable;
            Class modcl = Class.forName(filtertablename);
            Object modObj = get(modcl, id.toString());
            String filterQry = " where "+ tName +"." + pTable + "id=?";
            if(!StringUtil.isNullOrEmpty(delQuery)) {
                if(!StringUtil.isNullOrEmpty(filterQry)) {
                    delQuery = filterQry+" and "+delQuery;
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
                }
                if(configid == 2) { // Documents
                    commentFlag = true;
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

            String searchFilter = "";
            String sortQuery = "";

            JSONObject jobj = new JSONObject();
            Object[] paramArray = new Object[] {modObj};
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
                Class  arguments1[] = new Class[] {HibernateTemplate.class, HttpServletRequest.class,String.class,String.class,String.class,String.class,
                                String.class,String.class,String.class,ArrayList.class,ArrayList.class,Boolean.class,Boolean.class,JSONObject.class,Boolean.class,Object[].class};

                java.lang.reflect.Method objMethod1 = cl1.getMethod("beforeGridLoadData",arguments1);
                Object[] obj1 = new Object[]{getHibernateTemplate(),request,finalQuery,fieldQuery,delQuery,searchFilter,ruleFilterQuery,
                                                    sortQuery,reportTName,fieldNameArray,countFieldNameArray,commentFlag,docFlag, jobj,false, paramArray};
                Object result11 = objMethod1.invoke(invoker1, obj1);
            }
            if(jobj.has("data")) {
//                jobj.put("count", count);
                result = jobj.toString();
            }else{
                 result = "{data:[]}";
            }

        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            result = "{data:[]}";
            throw ServiceException.FAILURE("reportMethods.loadData", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{data:[]}";
            throw ServiceException.FAILURE("reportMethods.loadData", e);
        } finally {
            return result;
        }
    }

    public long getdocCount(String recid) throws ServiceException{
        int num = 0;
        String hql = "select count(*) as count from "+PropsValues.PACKAGE_PATH+".mb_docsmap as mb_docsmap where mb_docsmap.recid = ?";
        List li = executeQuery(hql,recid);
        Iterator ite = li.iterator();
        long count = 0;
        if( ite.hasNext() ) {
            count = ((Number)ite.next()).longValue();
        }
        return count;
    }

    public String getComboField(String moduleid)throws ServiceException{
        String result = "";
//        String configTableName = moduleBuilderGenerateTable.getConfigTableName(hibernateTemplate, request.getParameter("moduleid"));
        String query = "select mb_gridconfig.name, mb_gridconfig.combogridconfig, mb_gridconfig.reftable, mb_gridconfig.xtype , mb_gridconfig.displayfield " +
                "from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig  where mb_gridconfig.reportid = ? and mb_gridconfig.xtype !='default'";
        JSONObject res = new JSONObject();
        try{
            res.put("data", new JSONArray());
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class,moduleid);
            List ls = find(query,new Object[]{report});
            int i = 1;
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject temp = new JSONObject();
                temp.put("name",row[0]);
                temp.put("refflag",row[1]);
                temp.put("reftable",row[2]);
                temp.put("configtype",row[3]);
                temp.put("displayfield",row[4]);
                temp.put("id",i++);
                res.append("data", temp);
            }
            result = res.toString();
        } catch (JSONException ex){
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("formServelet.getComboField", ex);
        }
        return result;
    }

    public String getModuleConfig(String moduleid) throws ServiceException {
        String result = "{\"data\":[]}";
        try {
            JSONObject jobj = new JSONObject();
             mb_reportlist module = (mb_reportlist) get(mb_reportlist.class, moduleid);
//             String configtableName = getConfigClassName(hibernateTemplate, moduleid);
             String query = "select mb_gridconfig.name, mb_gridconfig.displayfield, mb_gridconfig.hidden, mb_gridconfig.columnindex, " +
                     "mb_gridconfig.id,renderer.id as renderer1,mb_gridconfig.summaryType,mb_gridconfig.xtype from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig inner join mb_gridconfig.renderer as renderer where mb_gridconfig.reportid = ? order by columnindex ";
             List list = find(query,new Object[]{module});
             Iterator ite = list.iterator();
            while(ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject jtemp2 = new JSONObject();
                jtemp2.put("columnname", row[0]);
                jtemp2.put("displayfield", row[1]);
                jtemp2.put("hidden", row[2]);
                jtemp2.put("seq", row[3]);
                jtemp2.put("id", row[4]);
                jtemp2.put("renderer", row[5]);
                jtemp2.put("summaryType", row[6]);
                jtemp2.put("xtype", row[7]);
                jobj.append("data", jtemp2);
             }

            JSONObject rObj = new JSONObject();
            if(jobj.has("data")){
                result = jobj.toString();
             } else {
                result = "{\"data\":[]}";
             }
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.saveModuleConfig", e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.saveModuleConfig", e);
        }
        return result;
    }

    public String saveModuleGridConfig(String jsonstr) throws ServiceException {
        String result = "{\"success\":true}";
        try {
            JSONObject jobj = new JSONObject();
            JSONArray jsonArray = new JSONArray(jsonstr);
            for (int k = 0; k < jsonArray.length(); k++) {
                jobj = jsonArray.getJSONObject(k);

                mb_gridconfig gridConf = (mb_gridconfig) get(mb_gridconfig.class, jobj.getString("id"));
                gridConf.setColumnindex(Integer.parseInt(jobj.getString("seq")));
                gridConf.setDisplayfield(jobj.getString("displayfield"));
                gridConf.setHidden(Boolean.parseBoolean(jobj.getString("hidden")));
                gridConf.setSummaryType(jobj.getString("summaryType"));
                renderer render = null;
                if (jobj.getString("renderer").length() > 0) {
                    render = (renderer) get(renderer.class, jobj.getString("renderer"));
                } else {
                    render = (renderer) get(renderer.class, "0");
                }
                gridConf.setRenderer(render);

                saveOrUpdate(gridConf);
             }
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.saveReportGridConfig", e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.saveReportGridConfig", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.saveReportGridConfig", e);
             }
        return result;
    }

    public String openSubModules(String basemode, String moduleid, String reportid, String taskid) throws ServiceException{
        String result = "{'success' : true}";
        String query = "select mb_modulegr.submodule, mb_modulegr.columnname from " +
                       " com.krawler.esp.hibernate.impl.mb_modulegr as mb_modulegr where basemodule = ?";
        try {
            mb_reportlist basemodule = (mb_reportlist) get(mb_reportlist.class, moduleid);
            JSONObject subTabObj = new JSONObject();
            if("0".equals(basemode)) {
                result = getForm(getFormId(moduleid),reportid, taskid);
                result  = result.substring(0, result.length()-1) + " ,'success':true}";
            } else {

            }
            List list = find(query,new Object[]{basemodule});
            Iterator ite = list.iterator();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject tempObj = new JSONObject();
                mb_reportlist submodule =(mb_reportlist ) row[0];
                int subMode = submodule.getType();
                if(subMode == 0) {
                    tempObj.put("data", getForm(getFormId(submodule.getReportid()),reportid, taskid));
                }
                tempObj.put("name", submodule.getReportname());
                tempObj.put("moduleid", submodule.getReportid());
                tempObj.put("refcolumn", row[1].toString());
                tempObj.put("mode", subMode);
                subTabObj.append("subtabs", tempObj);
            }
            if(subTabObj.has("subtabs"))
                result  = result.substring(0, result.length()-1) + ","+subTabObj.toString().substring(1,subTabObj.toString().length()-1)+"}";
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{'success' : false}";
            ServiceException.FAILURE("formServelet.getOtherModules", ex);
        }
        return result;
    }

    public String getOtherModules(String moduleid, String mode) throws ServiceException {
        JSONObject jobj = new JSONObject();
        String result = "{'data':[]}";
        String query ="";
        List list = null;
        try {
            mb_reportlist basemodule = (mb_reportlist) get(mb_reportlist.class, moduleid);
            if("0".equals(mode)) {
                // fetch all modules
                query = "select mb_reportlist.reportid, mb_reportlist.reportname, 1 as columnname,type as mode from com.krawler.esp.hibernate.impl.mb_reportlist" +
                        " as mb_reportlist where mb_reportlist.reportid != ?"+
                        " and mb_reportlist.reportid not in(select submodule.reportid from com.krawler.esp.hibernate.impl.mb_modulegr as mb_modulegr " +
                        " where mb_modulegr.basemodule=?) and mb_reportlist.deleteflag=0 order by mb_reportlist.reportname";
                list = find(query,new Object[]{moduleid,basemodule});
                Iterator ite = list.iterator();
                while( ite.hasNext() ) {
                    Object[] row = (Object[]) ite.next();
                    com.krawler.utils.json.base.JSONObject  jtemp2 = new com.krawler.utils.json.base.JSONObject();
                    jtemp2.put("moduleid", row[0]);
                    jtemp2.put("modulename", row[1]);
                    jtemp2.put("columnname", row[2]);
                    jtemp2.put("basemode", row[3]);
                    jobj.append("data", jtemp2);
                }
            } else {
                query = "select mb_modulegr.basemodule, mb_modulegr.submodule, mb_modulegr.columnname from " +
                        "com.krawler.esp.hibernate.impl.mb_modulegr as mb_modulegr where mb_modulegr.basemodule = ?";
                list = find(query,new Object[]{basemodule});
                Iterator ite = list.iterator();
                while( ite.hasNext() ) {
                    Object[] row = (Object[]) ite.next();
                    com.krawler.utils.json.base.JSONObject  jtemp2 = new com.krawler.utils.json.base.JSONObject();

                    mb_reportlist subModule = (mb_reportlist) row[1];
                    mb_reportlist baseModule = (mb_reportlist) row[0];
                    jtemp2.put("moduleid", subModule.getReportid());
                    jtemp2.put("modulename", subModule.getReportname());
                    jtemp2.put("columnname", row[2]);
                    jtemp2.put("basemode", baseModule.getType());
                    jtemp2.put("submode", subModule.getType());
                    jobj.append("data", jtemp2);
                }
            }

            if(jobj.has("data"))
                result = jobj.toString();
        }catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{'data':[]}";
            throw ServiceException.FAILURE("formServelet.getOtherModules", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{'data':[]}";
            throw ServiceException.FAILURE("formServelet.getOtherModules", e);
        } finally {
            return result;
        }
    }

    public String configSubtabModules(String basemodule, String submodule, String mode, String columnname) {
        String result = "{'success' : true}";
        String query = "";
        try {
            mb_reportlist basemod = (mb_reportlist) get(mb_reportlist.class ,basemodule);
            mb_reportlist submod = (mb_reportlist) get(mb_reportlist.class,submodule);
            if("0".equals(mode)) {
                query = "delete from com.krawler.esp.hibernate.impl.mb_modulegr as mb_modulegr where mb_modulegr.basemodule = ? and mb_modulegr.submodule = ?";
                int num = executeUpdate(query,new Object[]{basemod,submod});
                if (num == 0){
                    result = "{\"success\":false}";
                }
            } else if("1".equals(mode)) {
                mb_modulegr modulegr = new mb_modulegr();
                modulegr.setBasemodule(basemod);
                modulegr.setSubmodule(submod);
                modulegr.setColumnname(columnname);
                save(modulegr);
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return result;
    }

    public String getStdCongifType(String reportid) throws ServiceException{
        String result = "";
        JSONObject jobj = new JSONObject();
         try {
            String hql = "from "+PropsValues.PACKAGE_PATH+".mb_stdConfigs as mb_stdConfigs";
            List li = executeQuery(hql);
            hql = "select mb_moduleConfigMap.configid from "+PropsValues.PACKAGE_PATH+". mb_moduleConfigMap as  mb_moduleConfigMap where mb_moduleConfigMap.moduleid.reportid = ?";
            List li2 = executeQuery(hql,reportid);
            li = ListUtils.subtract(li, li2);
            Iterator ite = li.iterator();
            while(ite.hasNext()){
                JSONObject jtemp = new JSONObject();
                mb_stdConfigs obj = (mb_stdConfigs) ite.next();
                jtemp.put("id", obj.getConfigid());
                jtemp.put("configtype",obj.getConfigname());
                jobj.append("data", jtemp);
            }
           if(jobj.toString().equals("{}")){
                result = "{'data':[]}";
            }else{
                result = jobj.toString();
            }
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{'data':[]}";
            throw ServiceException.FAILURE("formServelet.getStdCongifType", ex);
        }

        return result;

    }

    public String getModuleCongifType(int configId, String add, String reportid, String deleteconfig)throws ServiceException{
        String result = "";
        JSONObject jobj = new JSONObject();
        try {
            if(add!=null){
                mb_moduleConfigMap insObj = new mb_moduleConfigMap();
                insObj.setConfigid((mb_stdConfigs)get(mb_stdConfigs.class, configId));
                insObj.setModuleid((mb_reportlist)get(mb_reportlist.class,reportid));
                save(insObj);
            }else if(deleteconfig!=null){
                String hql = "delete from "+PropsValues.PACKAGE_PATH+".mb_moduleConfigMap as mb_moduleConfigMap where " +
                        "mb_moduleConfigMap.configid.configid = ? and mb_moduleConfigMap.moduleid.reportid = ? ";
                executeUpdate(hql,new Object[]{configId, reportid});

            }
            String hql = "from "+PropsValues.PACKAGE_PATH+".mb_moduleConfigMap as mb_moduleConfigMap where mb_moduleConfigMap.moduleid.reportid = ?";
            List li = executeQuery(hql,reportid);
            Iterator ite = li.iterator();
            while(ite.hasNext()){
                JSONObject jtemp = new JSONObject();
                mb_moduleConfigMap obj = (mb_moduleConfigMap) ite.next();
                jtemp.put("id", obj.getConfigid().getConfigid());
                jtemp.put("configtype",obj.getConfigid().getConfigname());
                jobj.append("data", jtemp);
            }
            if(jobj.toString().equals("{}")){
                result = "{'data':[]}";
            }else{
                result = jobj.toString();
            }

        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{'data':[]}";
            throw ServiceException.FAILURE("formServelet.getStdCongifType", ex);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{'data':[]}";
            throw ServiceException.FAILURE("formServelet.getStdCongifType", e);
        }

        return result;
    }

    public String getReadOnlyFields(String moduleid, String[] comboIDs, List<ModuleClause> clauses, DateFormat formatter)throws ServiceException{
        String result = "";
        try {
            mb_reportlist basemodule = (mb_reportlist) get(mb_reportlist.class , moduleid);
            String hql = "select mb_gridconfig.name, mb_gridconfig.reftable from "+PropsValues.PACKAGE_PATH+".mb_gridconfig as mb_gridconfig where mb_gridconfig.reportid = ? and mb_gridconfig.xtype = ? ";
            List li = find(hql,new Object[] { basemodule, "readOnlyCmp" });
            Iterator ite = li.iterator();
            JSONObject jtemp = new JSONObject();
            while(ite.hasNext()){
                Object[] row = (Object[]) ite.next();
                String[] columnName = row[0].toString().split(PropsValues.REPORT_HARDCODE_STR);
                String refTName = row[1].toString();

                List objectList;
                objectList = dataObjectOperationObj.getAllDataObjects(refTName, clauses, formatter);
                String childcol = columnName[0];
                String parentcol = columnName[1];
                for (Object modObj : objectList) {
                    try {
                        JSONObject jobj = new JSONObject((Map)modObj);
                        String comboid = "";
                        for(int i = 0; i < comboIDs.length; i++) {
                            if(comboIDs[i].contains(childcol)) {
                                comboid = comboIDs[i];
                                jtemp.put(comboid, jobj.get(parentcol));
                            }
                        }
                    } catch (JSONException ex) {
                        logger.warn(ex.getMessage(), ex);
                    }
                }
            }
            if(jtemp.length() > 0){
                result = jtemp.toString();
            }else{
                result = "{'data':[]}";
            }

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{'data':[]}";
            throw ServiceException.FAILURE("formServelet.getStdCongifType", ex);
        }

        return result;
    }

    public String getAllModules1(String moduleid) throws ServiceException {
        JSONObject jobj = new JSONObject();
        String result = "{'data':[]}";
        try {
            String sql = "";
            List ls = null;
            Iterator ite = null;
            sql = "select mb_reportlist.reportid, mb_reportlist.reportname, mb_reportlist.tablename, 0 as type " +
                    "from "+PropsValues.PACKAGE_PATH+".mb_reportlist as mb_reportlist " +
                    "where mb_reportlist.reportid !=? and mb_reportlist.deleteflag = 0 and (mb_reportlist.companyid is null or mb_reportlist.companyid = ?) order by mb_reportlist.reportname ";
            ls = find(sql,new Object[]{moduleid, sessionHandlerDao.getCompanyid()});
            ite = ls.iterator();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject  jtemp2 = new JSONObject();
                jtemp2.put("moduleid", row[0]);
                jtemp2.put("modulename", row[1]);
                jtemp2.put("tablename", row[2]);
                jtemp2.put("mastertype", row[3]);
                jobj.append("data", jtemp2);
            }

            result = jobj.toString();
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{'data':[]}";
            throw ServiceException.FAILURE("FormServlet.getAllModulesForCombo", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{'data':[]}";
            throw ServiceException.FAILURE("FormServlet.getAllModulesForCombo", e);
        } finally {
            return result;
        }
    }

    public String getformWithParentvalue(String parentmodule,String childmodule,String modulevar) throws ServiceException{
        String result = "{\"success\":true}";
        JSONObject ret = new JSONObject();
        try {
            ret.put("valid", true);
            String[] key = {"formid", "name","jdata"};
			mb_reportlist basemodule = (mb_reportlist) get(mb_reportlist.class, childmodule);
            String hql ="select count(*) as count from com.krawler.esp.hibernate.impl.mb_forms as mb_forms where mb_forms.moduleid.reportid = ?";
            List list = executeQuery(hql, childmodule);
            Iterator ite = list.iterator();
            long count = 0;
            if( ite.hasNext() ) {
               count = ((Number)ite.next()).longValue();
            }

            String parentJson = getParentJson(parentmodule,modulevar);
            hql ="select mb_forms.formid, mb_forms.name, mb_forms.data from com.krawler.esp.hibernate.impl.mb_forms as mb_forms " +
                     "where mb_forms.moduleid.reportid = ?";
            list = executeQuery(hql, childmodule);
            ite = list.iterator();
            String[][] values = new String[(int)count][key.length];
            int i = 0;
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                values[i][0] = row[0].toString();
                values[i][1] = row[1].toString();
                String jsondata = row[2].toString();
                String s1 =  jsondata;
                if(!StringUtil.isNullOrEmpty(parentJson))
                    s1 = parentJson;
//                    s1 = combineJson(parentJson,jsondata);
                values[i++][2] = "{\"jsondata\":\""+java.net.URLEncoder.encode(s1)+"\"}";
            }
            String buttonConf = reportDao.getButtonConf(childmodule);
            String formConf = createJsonStart() + createJson(key, values, count)  + createJsonEnd();
            ret.put("success", true);
            ret.put("data", new JSONObject(formConf.trim()));
            ret.put("buttonConf", buttonConf);
            result = ret.toString();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"valid\": true, \"success\":false}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return result;
    }

    public String getParentJson(String moduleid,String modulevar) throws JSONException {

        String[] var = modulevar.split(",");
        String json = getJsonFromDb(moduleid);
        String filteredJson = JsonArrayHandler.getFileteredJson(json,var);
        return filteredJson;
    }

    public String getJsonFromDb(String moduleid) {
        String json = "";
        String hql ="select mb_forms.data from com.krawler.esp.hibernate.impl.mb_forms as mb_forms " +
                     "where mb_forms.moduleid.reportid = ?";
        List list = executeQuery(hql,moduleid);
        Iterator ite = list.iterator();
        while(ite.hasNext()) {
            json = (String) ite.next();
        }
        return json;
    }

    @Override
    public String getTableColumn(String parameter) throws ServiceException {
        return reportDao.getTableColumn(parameter);
    }

    @Override
    public String createNewRecord(Object moduleObj) throws ServiceException {
        boolean flag = dataObjectOperationObj.createDataObject(moduleObj);

        return "{success:"+flag+"}";
    }

    @Override
    public String updateRecord(Object moduleObj, String key) throws ServiceException {
        boolean flag = dataObjectOperationObj.updateDataObject(moduleObj, key);

        return "{success:"+flag+"}";
    }

    public String getModuleRecords(Object moduleObj, DateFormat formatter) throws ServiceException {
         List objectList;
        if(moduleObj instanceof String)
            objectList = dataObjectOperationObj.getAllDataObjects((String)moduleObj, formatter);
        else
            objectList = dataObjectOperationObj.getAllDataObjects(moduleObj.getClass().getSimpleName(),moduleObj.getClass());

        PropertyDescriptor[] pdesc =  PropertyUtils.getPropertyDescriptors(moduleObj);
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("data", new JSONArray());
            for (Object modObj : objectList) {
                JSONObject jobj;
                if(moduleObj instanceof String)
                    jobj = new JSONObject((Map)modObj);
                else
                    jobj = new JSONObject(modObj);
                dataObj.append("data", jobj);
            }
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return dataObj.toString();
    }


    public String getModuleRecords(String moduleid, Object moduleObj, List<ModuleClause> clauses, DateFormat formatter) throws ServiceException {
        List readOnlyComp = getReadOnlyFields(moduleid);
        List objectList = getModuleRecords(moduleObj, clauses, formatter);
        JSONObject dataObj = new JSONObject();
        try {
            dataObj.put("data", new JSONArray());
            for (Object modObj : objectList) {
                JSONObject jobj;
                if(moduleObj instanceof String)
                    jobj = new JSONObject((Map)modObj);
                else
                    jobj = new JSONObject(modObj);

                if(readOnlyComp.size() > 0) {
                    jobj = getDependentComboData(jobj, readOnlyComp);
                }
                dataObj.append("data", jobj);
             }
         } catch (Exception ex) {
             logger.warn(ex.getMessage(), ex);
         }
        return dataObj.toString();
    }
    
    public List getModuleRecords(Object moduleObj, List<ModuleClause> clauses, DateFormat formatter){
        List objectList;
        if(moduleObj instanceof String)
            objectList = dataObjectOperationObj.getAllDataObjects((String)moduleObj, clauses, formatter);
        else
            objectList = dataObjectOperationObj.getAllDataObjects(moduleObj.getClass().getSimpleName(),moduleObj.getClass(), clauses);

        return objectList;
    }

    public List getReadOnlyFields(String moduleid) throws ServiceException {
        List li = new java.util.ArrayList();
        try {
            mb_reportlist basemodule = (mb_reportlist) get(mb_reportlist.class , moduleid);
            String hql = "select mb_gridconfig.name, mb_gridconfig.reftable from "+PropsValues.PACKAGE_PATH+".mb_gridconfig as mb_gridconfig where mb_gridconfig.reportid = ? and mb_gridconfig.xtype = ? ";
            li = find(hql,new Object[] { basemodule, "readOnlyCmp" });
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return li;
    }

    public JSONObject getDependentComboData(JSONObject jobj, List li) {
        try {
            DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
            Iterator ite = li.iterator();
            while(ite.hasNext()){
                Object[] row = (Object[]) ite.next();
                String[] columnName = row[0].toString().split(PropsValues.REPORT_HARDCODE_STR);
                String refTName = row[1].toString();
                String childcol = columnName[0];
                String parentcol = columnName[1];
                String comboName = columnName[2];
                String comboValueId = jobj.getString(comboName);
                List<ModuleClause> clauses = new ArrayList<ModuleClause>();
                clauses.add(new ModuleClause("id", "=", comboValueId));

                List objectList;
                objectList = dataObjectOperationObj.getAllDataObjects(refTName, clauses, DATE_FORMAT);
                
                for (Object modObj : objectList) {
                    try {
                        JSONObject jtemp = new JSONObject((Map)modObj);
                        jobj.put(childcol, jtemp.get(parentcol));
                    } catch (Exception ex) {
                        logger.warn(ex.getMessage(), ex);
                    }
                }
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return jobj;
    }

    @Override
    public String deleteRecord(String key, String id, String moduleid) throws ServiceException {
        Map arrParams = new HashMap();
        arrParams.put(key, id);
        arrParams.put("modifieddate", new java.util.Date());
        arrParams.put("deleteflag",1.0);
        String tablename=getReportTableName(moduleid);
        return editRecord(tablename, arrParams, new ArrayList(), key);
    }

    @Override
    public String editRecord(Map arrParams, List fi, String key, String moduleid) throws ServiceException {
        arrParams.put("modifieddate", new java.util.Date());
        arrParams.put("deleteflag", 0.0);
        String tablename=getReportTableName(moduleid);
        return editRecord(tablename, arrParams, fi, key);
    }

    public String editRecord(String tablename, Map arrParam,List<FileItem> fi, String key) throws ServiceException {
        String result = "{'success':false, 'msg':'Error occured at server.'}";
        try {
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

                dataObjectOperationObj.updateDataObject(tablename, key, arrParam);
                if(arrParam.containsKey("jsondata")){
      // TODO             //insertGridValues(hibernateTemplate, sessionHandlerDao,reportDao, (String)arrParam.get("jsondata"), sessionHandlerDao.getUserid(), ptablename, invoker);
                }
                result = "{'success':true}";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{'success':false, 'msg':'Error occured at server.'}";
            throw ServiceException.FAILURE("moduleBuilderMethods.editRecord", e);
        }
        return result;
    }

    @Override
    public String getAttachment(HttpServletRequest request)throws ServiceException{
            String result = "";
            String reftable = getReportTableName(request.getParameter("moduleid"));
            String recid = request.getParameter("recid");
            JSONObject jobj = new JSONObject();
            if(request.getParameter("deleteFile")!=null){
                deleteDoc(request);
            }
            String hql = " from "+PropsValues.PACKAGE_PATH+".mb_docsmap as mb_docsmap where mb_docsmap.recid = ? and reftable = ?";
            List li = find(hql,new Object[] {recid,reftable});
            Iterator ite = li.iterator();
            int count =1;
            while(ite.hasNext()){
                try {
                    JSONObject jtemp = new JSONObject();
                    mb_docsmap docMapObj = (mb_docsmap) ite.next();
                    String dacName = docMapObj.getDocid().getDocname();
                    String size = docMapObj.getDocid().getDocsize();
                    String docid = docMapObj.getDocid().getDocid();
                    String storeName = docMapObj.getDocid().getStorename();
                    String Url = "fileDownload.jsp?url=" + reftable + "/" + storeName + "&docid=" + docid + "&attachment=true";
                    result = "<span style=\"color:gray !important;\">" + (count++) + ") " + dacName + " (" + getSizeKb(size) + "K)  </span><a href='javascript:void(0)' title='Download' onclick='setDldUrl(\"" + Url + "\")'>download</a>" ;
                    jtemp.put("link", result);
                    jtemp.put("docid",docid);
                    jtemp.put("docpath", reftable + "/" + storeName);
                    jobj.append("data", jtemp);
                } catch (JSONException ex) {
                    logger.warn(ex.getMessage(), ex);
                    throw ServiceException.FAILURE("moduleBuilderMethods.getAttachment", ex);
                }
            }
            if(jobj.toString().equals("{}")){
                result = "{data:[]}";
            }else{
                result = jobj.toString();
            }
            return result;
     }

    public static int getSizeKb(String size) {
        int no = ((Integer.parseInt(size)) / 1024);
        if (no >= 1) {
            return no;
        } else {
            return 1;
        }
    }

    public String deleteDoc(HttpServletRequest request)throws ServiceException{
         String result = "";
         String filePath = PropsValues.STORE_PATH+request.getParameter("docpath");
         File file = new File(filePath);
         if(file.exists()){
             file.delete();
         }

         String hql = "delete from "+PropsValues.PACKAGE_PATH+".mb_docsmap as mb_docsmap  where mb_docsmap.docid.docid = ?";
         executeUpdate(hql,  request.getParameter("docid"));
         hql = "delete from "+PropsValues.PACKAGE_PATH+".mb_docs as mb_docs  where mb_docs.docid = ?";
         executeUpdate(hql,  request.getParameter("docid"));
         return result;
     }

     @Override
    public String createNewRecord(Map arrParams, List fi, String moduleid) throws ServiceException {
        arrParams.put("id", UUID.randomUUID().toString());
        try {
            arrParams.put("createdby", sessionHandlerDao.getUserid());
        } catch (SessionExpiredException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("Can not get current user", ex);
    }
        arrParams.put("createddate", new java.util.Date());
        arrParams.put("modifieddate", new java.util.Date());
        arrParams.put("deleteflag", 0.0);
        String tablename=getReportTableName(moduleid);
        return createNewRecord(tablename, arrParams, fi);
    }

    public String createNewRecord(String tablename, Map arrParam, List<FileItem> fi) throws ServiceException {
        String result = "{'success':false, 'msg':'Error occured at server.'}";
        try {
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

                dataObjectOperationObj.createDataObject(tablename, arrParam);
                if(arrParam.containsKey("jsondata")){
      // TODO             //insertGridValues(hibernateTemplate, sessionHandlerDao,reportDao, (String)arrParam.get("jsondata"), sessionHandlerDao.getUserid(), ptablename, invoker);
                }
                result = "{'success':true}";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{'success':false, 'msg':'Error occured at server.'}";
            throw ServiceException.FAILURE("moduleBuilderMethods.editRecord", e);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.krawler.formbuilder.servlet.ModuleBuilderDao#undeployModule(java.lang.String)
     */
    public void undeployModule(String moduleId)
    {
        List<mb_forms> forms = executeQuery("from mb_forms where moduleId = ?", moduleId);
        
        if (forms != null && !forms.isEmpty())
        {
            mb_forms form = forms.get(0);
            boolean tableDropped = deleteTable(form.getModuleid().getTablename());
            if(tableDropped){
                
                form.setDeployedInd(false);
                saveOrUpdate(form);

                String hql = "DELETE FROM " + PropsValues.PACKAGE_PATH + ".mb_dashlinks AS mb_dashlinks WHERE mb_dashlinks.processid.reportid = ?";
                executeUpdate(hql, new Object[] { moduleId });

                hql = "DELETE FROM " + PropsValues.PACKAGE_PATH + ".mb_dashportlet AS mb_dashportlet WHERE mb_dashportlet.reportid.reportid = ?";
                executeUpdate(hql, new Object[] { moduleId });
            }
        }
    }

 
    // TODO
    protected boolean deleteTable(String name)
    {
      return dataObjectOperationObj.dropTable(name);
    }

    public String getComboData(String moduleid, String fieldname) throws ServiceException {
       JSONObject jobj = new JSONObject();
       String result = null;
       try{
            String tablename = getReportTableName(moduleid);

            String sql = "select mb_gridconfig.combogridconfig, mb_gridconfig.reftable from "+PropsValues.PACKAGE_PATH+".mb_gridconfig as mb_gridconfig where mb_gridconfig.name = ? and mb_gridconfig.reportid.reportid = ? ";
            List ls = find(sql, new Object[]{tablename + PropsValues.REPORT_HARDCODE_STR + fieldname, moduleid});
            String combogridconfig = "-1";
            String reftable = "";
            Iterator ite = ls.iterator();
            if(ite.hasNext()){
                Object[] row = (Object[]) ite.next();
                combogridconfig = row[0].toString();
                reftable = row[1].toString();
            }

            if(combogridconfig.equals("-1")) {
                String refModDetailsQuery = "select mfc.primaryField, rl.shared from mb_reportlist rl inner join mb_form_config mfc on mfc.moduleid = rl.reportid where rl.tablename = ?";
                List lst = (List)executeNativeQuery(refModDetailsQuery, new Object[]{reftable});
                String pkField = "id";
                List params = new ArrayList();
                String companyCondition ="";
                if(lst.size() > 0){
                    Object[] row = (Object[])lst.get(0);
                    pkField = row[0].toString();
                    if(row[1]!=null){
                        params.add(sessionHandlerDao.getCompanyid());
                        companyCondition = " and company=?";
                    }

                }
                fieldname = fieldname.substring(0, fieldname.length()-2);
                sql = "select "+ reftable +"."+pkField+", "+ reftable +"."+fieldname+" from "+reftable+" where "+ reftable + ".deleteflag = 0"+companyCondition ;
                ls = (List)executeNativeQuery(sql, params.toArray());
            } else {
                sql = "select mb_configmasterdata.masterid as id, mb_configmasterdata.masterdata as name from "+PropsValues.PACKAGE_PATH+".mb_configmasterdata as mb_configmasterdata " +
                        " where mb_configmasterdata.configid = ? order by mb_configmasterdata.masterdata ";
                ls = (List)executeQuery(sql, combogridconfig);
            }
            ite = ls.iterator();
            while(ite.hasNext()){
                Object[] row = (Object[]) ite.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("id", row[0]);
                //renderer renderer=(renderer) row[4];
//                if(combogridconfig.equals("-1") && row[1].toString().startsWith("com.krawler")) {
//                    mb_configmasterdata mb_configmasterdata=(mb_configmasterdata)row[1];
//                    jtemp.put("name", mb_configmasterdata.getMasterdata());
//                }else{
                    jtemp.put("name", row[1]);
//                }
                jobj.append("data", jtemp);
            }
            if(jobj.length() > 0) {
                result = jobj.toString();
            } else {
                result = "{'data':[]}";
            }

        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE("FormServlet.getComboData", e);
        }
        return result;
   }

    public String createServiceXMLForTable(String formid, String moduleid,String formjson, String parentmodule) throws ServiceException, SQLException{
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
                // Shri - New table is created for module with reference to existing module.
//                if(!StringUtil.isNullOrEmpty(parentmodule)){
//                    tableName = reportDao.getReportTableName(parentmodule);
//                    mb_reportlist module = (mb_reportlist)get(mb_reportlist.class, getModuleid(formid));
//                    module.setTablename(tableName);
//                    save(module);
//                    /*String filename = sb.getXmlFileName(tableName);
//                    generateServiceXml getXml = new generateServiceXml();
//                    result = getXml.getXmlContent(filename,jsondata,session,request);*/
//                } else {

                    tableName = getReportTableName(getModuleid(formid));
//                }
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
                            objArrField = new Object[] {"name","type","foreignid","xtype","displayfield","reftable","refflag","hidden","configid","default","reffield"};
                            if(obj.getString("xtype").equals("select")){
                                objArr = new Object[] {obj.getString("name"),type,false,obj.getString("xtype"),obj.getString("fieldLabel"),refTableName,refflag,false,obj.getString("datastore"),defaultValue,""};

                            }
                            else{

                                String refModDetailsQuery = "select mfc.primaryField from mb_reportlist rl inner join mb_form_config mfc on mfc.moduleid = rl.reportid where rl.tablename = ? and companyid=?";
                                List lst = (List)executeNativeQuery(refModDetailsQuery, new Object[]{refTableName,sessionHandlerDao.getCompanyid()});
                                String pkField = "id";
                                if(lst.size() > 0){
                                    pkField = lst.get(0).toString();
                                }
                                objArr = new Object[] {obj.getString("name"),type,true,obj.getString("xtype"),obj.getString("fieldLabel"),refTableName,refflag,false,obj.getString("datastore"),defaultValue,pkField};
                            }

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
            throw ServiceException.FAILURE("createFormTable", ex);
        } catch(Exception ex){
            logger.warn(ex.getMessage(), ex);
             stmt.close();
            throw ServiceException.FAILURE("createFormTable", ex);
        }
        return result;
    }
        public void makeEntryToArrayList(int cnt,ArrayList<Hashtable<String, Object>> aList,Object[] objArr,Object[] objArrField){
        int i=0;
        aList.add(cnt,new Hashtable<String, Object>());
        for(i=0;i<objArr.length;i++){
            if(objArrField[i] !=null && objArr[i] != null){
            aList.get(cnt).put(objArrField[i].toString(),objArr[i]);
            }
        }
        cnt++;
    }
    public void makeEntryToArrayListReadOnly(int cnt,ArrayList<Hashtable<String, Object>> aList1,Object[] objArr,Object[] objArrField){
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
            throw ServiceException.FAILURE("createFormTable", ex);
        }
        return result;
    }
        public String getModuleid(String formid) throws ServiceException{
        String moduleid = "";
        String SELECT_QUERY="Select mb_forms.moduleid.reportid from com.krawler.esp.hibernate.impl.mb_forms as mb_forms "
                      +" where mb_forms.formid = ?";
        try {
            List ls = find(SELECT_QUERY, new Object[] {formid} );
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
            throw ServiceException.FAILURE("getModuleName", ex);
        }
        return result;
    }

    public String getModuleName(String moduleid) throws ServiceException{
        String result = "{'success' : true}";
        String SELECT_QUERY = "select mb_modules.reportname from com.krawler.esp.hibernate.impl.mb_reportlist as mb_modules  where mb_modules.reportid = ?";
        try {
            List list = (List)executeQuery(SELECT_QUERY, moduleid);
            Iterator ite = list.iterator();
            if( ite.hasNext() ) {
                result = ite.next().toString();
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("modulebuilderdaoimpl.getModuleName", ex);
        }
        return result;
    }

     public String uploadFile(HttpServletRequest request) throws ServiceException{
        String result = "";
        DiskFileUpload fu = new DiskFileUpload();
        FileItem fi1 = null;
        List fileItems = null;
        HashMap<String,String> arrParam = new HashMap<String,String>();
        boolean fileUpload = false;
        ArrayList<FileItem> fi = new ArrayList<FileItem>();
        try {
            parseRequest(request,arrParam,fi,fileUpload);
            User userObj = (User) get(User.class,sessionHandlerDao.getUserid());

//            if(fileUpload){
                for(int cnt = 0 ; cnt< fi.size();cnt++){
                    String fileName = new String(fi.get(cnt).getName().getBytes(), "UTF8");
                    String Ext = "";
                    if (fileName.contains(".")){
                        Ext = fileName.substring(fileName.lastIndexOf("."));
                    }


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
                    if(Ext.length()>0)
                          fileid = fileid+Ext;
                    docObj.setStorename(fileid);
                    saveOrUpdate(docObj);
                     String reftable = getReportTableName(arrParam.get("moduleid"));
                    mb_docsmap  docmapObj = new mb_docsmap();
                    docmapObj.setDocid(docObj);
                    docmapObj.setRecid(arrParam.get("recid"));
                    docmapObj.setReftable(reftable);
                    save(docmapObj);

                    uploadFile(fi.get(cnt),PropsValues.STORE_PATH+reftable,fileid);
                }
//            }
        }catch (java.io.UnsupportedEncodingException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("uploadFile", e);
        }catch(com.krawler.common.session.SessionExpiredException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("uploadFile", e);
        }
        return result;
    }

    @Override
    public List getModuleInfo(String moduleid){
        String sql = "Select mb_gridconfig.name, mb_gridconfig.xtype, mb_gridconfig.displayfield, " +
                " mb_gridconfig.reftable, mb_gridconfig.combogridconfig from "+ PropsValues.PACKAGE_PATH +".mb_gridconfig as mb_gridconfig" +
                " where mb_gridconfig.reportid.reportid = ? order by mb_gridconfig.name ";

        return executeQuery(sql, moduleid);
    }

    public String getPortletData(HttpServletRequest request) throws ServiceException{
        String ret = "<table class='servicesT'>";
        try{
            mb_dashportlet portlet = (mb_dashportlet) get(mb_dashportlet.class, request.getParameter("portletid"));
            mb_reportlist report = portlet.getReportid();
            JSONArray conf = new JSONArray(portlet.getConfig());
            String tablename = report.getTablename();
            ret += "<thead><tr>";
            String hql = "SELECT ";
            String getDispName = "SELECT mb_gridconfig.displayfield FROM " + PropsValues.PACKAGE_PATH +".mb_gridconfig AS mb_gridconfig " +
                "WHERE mb_gridconfig.name = ? AND mb_gridconfig.reportid = ?";
            for(int cnt = 0; cnt < conf.length(); cnt++){
                List nameLst = find(getDispName, new Object[] {conf.getJSONObject(cnt).getString("columnname"), report});
                Iterator nameIte = nameLst.iterator();
                String colName = conf.getJSONObject(cnt).getString("columnname").split(PropsValues.REPORT_HARDCODE_STR)[1];
                hql += tablename + "." + colName + ", ";
                if(nameIte.hasNext())
                    colName = nameIte.next().toString();
                ret += "<th class = 'servHd'>" + colName + "</th>";
            }
            ret += "</tr></thead><tbody>";
            hql = hql.substring(0, (hql.length() - 2));
            Object moduleObj  = Class.forName("com.krawler.esp.hibernate.impl."+tablename).newInstance();

            List lst;
            if(moduleObj instanceof String)
                lst = dataObjectOperationObj.getAllDataObjects((String)moduleObj, new SimpleDateFormat("yyyy-MM-dd"));
            else
                lst = dataObjectOperationObj.getAllDataObjects(moduleObj.getClass().getSimpleName(),moduleObj.getClass());
            Iterator ite = lst.iterator();
            com.krawler.utils.json.base.JSONObject jobData = new com.krawler.utils.json.base.JSONObject();
            for (Object modObj : lst) {
                JSONObject jobj = new JSONObject(modObj);
                Iterator itr = jobj.keys();
                ret += "<tr class='servBodL'>";
                while(itr.hasNext()){
                    String key = itr.next().toString();
                    String objVal = jobj.get(key).toString();

                    ret += "<td><span style='margin-left: 3px;'>" + objVal + "</span></td>";

                }
                ret += "</tr>";

            }
            /*while(ite.hasNext()){

                Object[] row;
                if(conf.length() == 1){
                    row = new Object[] {ite.next()};
                } else {
                    row = (Object[]) ite.next();
                }
//                row = (Object[]) ite.next();
                ret += "<tr class='servBodL'>";
                for(int cnt = 0; cnt < row.length; cnt++){
                    ret += "<td><span style='margin-left: 3px;'>" + row[cnt].toString() + "</span></td>";
                }
                ret += "</tr>";
            }*/
            ret += "</tbody></table>";
            ret = "<div class='modName'>" + report.getReportname() + "</div>" + ret;
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }
}
