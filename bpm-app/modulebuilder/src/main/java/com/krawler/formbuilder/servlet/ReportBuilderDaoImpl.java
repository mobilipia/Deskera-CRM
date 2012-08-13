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

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.utils.PropsValues;
import java.io.*;
import javax.servlet.http.*;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.ArrayList;
import com.krawler.esp.database.ReportHandlers;
import java.util.Hashtable;
import java.util.List;
import java.util.HashSet;
import com.krawler.esp.hibernate.impl.*;
import com.krawler.portal.tools.ServiceBuilder;
import com.krawler.esp.handlers.*;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.workflow.module.dao.BaseBuilderDao;
import java.util.HashMap;
import java.util.UUID;
import org.apache.commons.fileupload.FileItem;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class ReportBuilderDaoImpl extends BaseBuilderDao implements ReportBuilderDao {
    private AccessRightDao accessRightDao;

    public void setAccessRightDao(AccessRightDao accessRightDao) {
        this.accessRightDao = accessRightDao;
    }

    public void staticEntries() throws ServiceException {
        try{
//            jspSession.beginTransaction();
//            users user = new users();
//            user.setUsername("admin");
//            user.setFname("admin");
//            user.setLname("");
//            user.setRoleid("");
//            user.setEmailid("");
//            user.setDeleteflag(0);
//            jspSession.save(user);
//
//            userlogin ulogin = new userlogin();
//            ulogin.setUserid(user.getUserid());
//            ulogin.setUsername("admin");
//            ulogin.setPassword("7110eda4d09e062aa5e4a390b0a572ac0d2c0220");
//            ulogin.setDeleteflag(0);
//            jspSession.save(ulogin);
//
//            renderer myRender = new renderer();
//            myRender.setId("0");
//            myRender.setName("None");
//            myRender.setRendererValue("None");
//            myRender.setIsstatic(Boolean.parseBoolean("false"));
//            jspSession.save(myRender);
//
//            mb_reportlist module = new mb_reportlist();
//            module.setReportname("Users");
//            module.setTablename("users");
//            module.setReportkey(0);
//            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            java.sql.Timestamp timestamp1 = Timestamp.valueOf(sdf.format(new java.util.Date()));
//            module.setCreateddate(new Date());
//            module.setCreatedby(user.getUserid());
//            module.setModifieddate(timestamp1);
//            jspSession.save(module);
//
//            String repS = PropsValues.REPORT_HARDCODE_STR;
//            String name[] = {"users"+repS+"userid", "users"+repS+"username", "users"+repS+"fname", "users"+repS+"lname", "users"+repS+"roleid", "users"+repS+"emailid", "users"+repS+"deleteflag"};
//            String displayName[] = {"UserID", "Username", "Firstname", "Lastname", "RoleID", "EmailID", "Delete"};
//            String xType[] = {"default", "textfield", "textfield", "textfield", "default", "default", "default"};
//            int len = name.length;
//            com.krawler.esp.hibernate.impl.mb_gridconfig gridConf = null;
//            for(int i=0; i<len; i++){
//                gridConf = new com.krawler.esp.hibernate.impl.mb_gridconfig();
//                gridConf.setName(name[i]);
//                gridConf.setDisplayfield(displayName[i]);
//                gridConf.setReftable("");
//                gridConf.setXtype(xType[i]);
//                gridConf.setRenderer(myRender);
//                gridConf.setFilter("");
//                gridConf.setSummaryType("");
//                gridConf.setHidden(Boolean.parseBoolean("false"));
//                gridConf.setCountflag(Boolean.parseBoolean("false"));
//                gridConf.setCombogridconfig("-1");
//                gridConf.setColumnindex(i);
//                gridConf.setReportid(module);
//                jspSession.save(gridConf);
//            }
//
//            module = new mb_reportlist();
//            module.setReportname("Userlogin");
//            module.setTablename("userlogin");
//            module.setReportkey(0);
//            module.setCreateddate(new Date());
//            module.setCreatedby(user.getUserid());
//            module.setModifieddate(timestamp1);
//            jspSession.save(module);
//
//            String name1[] = {"userlogin"+repS+"userid", "userlogin"+repS+"username", "userlogin"+repS+"password", "userlogin"+repS+"deleteflag"};
//            String displayName1[] = {"UserID", "Username", "Password", "Delete"};
//            String xType1[] = {"default", "textfield", "textfield", "default"};
//            len = name1.length;
//            for(int i=0; i<len; i++){
//                gridConf = new com.krawler.esp.hibernate.impl.mb_gridconfig();
//                gridConf.setName(name1[i]);
//                gridConf.setDisplayfield(displayName1[i]);
//                gridConf.setReftable("");
//                gridConf.setXtype(xType1[i]);
//                gridConf.setRenderer(myRender);
//                gridConf.setFilter("");
//                gridConf.setSummaryType("");
//                gridConf.setHidden(Boolean.parseBoolean("false"));
//                gridConf.setCountflag(Boolean.parseBoolean("false"));
//                gridConf.setCombogridconfig("-1");
//                gridConf.setColumnindex(i);
//                gridConf.setReportid(module);
//                jspSession.save(gridConf);
//            }

            String configName[] = {"Comments","Documents"};
            int len = configName.length;
            mb_stdConfigs stdConfigs = null;
            for(int i=0; i<len; i++){
                stdConfigs = new mb_stdConfigs();
                stdConfigs.setConfigid(i+1);
                stdConfigs.setConfigname(configName[i]);
                save(stdConfigs);
            }
            
            String perName[] = {"Button","Add Record","Update Record","Delete Record", "Add comment", "Delete Comment", "Add Document", "Delete Document", "Module View Tab"};
            len = perName.length;
            mb_permactions permactions = null;
            for(int i=0; i<len; i++){
                permactions = new mb_permactions();
                permactions.setAction(i);
                permactions.setName(perName[i]);
                save(permactions);
            }

            mb_rolegrmaster rolegrmaster = new mb_rolegrmaster();
            rolegrmaster.setGroupid(1);
            rolegrmaster.setGroupname("Administration");
            rolegrmaster.setDescription("");
            save(rolegrmaster);

            mb_rolemaster rolemaster1 = new mb_rolemaster();
            rolemaster1.setGroupid(rolegrmaster);
            rolemaster1.setRolename("Finance");
            rolemaster1.setDescription("");
            save(rolemaster1);
//
//            mb_rolemaster rolemaster2 = new mb_rolemaster();
//            rolemaster2.setGroupid(rolegrmaster);
//            rolemaster2.setRolename("SCO");
//            rolemaster2.setDescription("");
//            jspSession.save(rolemaster2);
//
//            userrolemapping rolemapping = new userrolemapping();
//            rolemapping.setId(1);
//            rolemapping.setRoleid(rolemaster1);
//            rolemapping.setUserid(user);
//            jspSession.save(rolemapping);
//
//            AuditGroups auditGroups = null;
//            String groups[] = {"General", "Module Builder","Report Builder","Master Configuration"};
//            len = groups.length;
//            for(int i=0; i<len; i++){
//                auditGroups = new AuditGroups();
//                auditGroups.setGroupName(groups[i]);
//                jspSession.save(auditGroups);
//            }
//
//
//            AuditTrailType auditTrailType = null;
//            String trailtype[] = {"Sign In","Sign Out","Add Module","Edit Module","Delete Module","Add Form",
//                    "Edit Form","Delete Form","Add Form Record","Edit Form Record","Delete Form Record",
//                    "Add Report","Edit Report","Delete Report","Add Report Grid Config","Add Report Grid Config Table",
//                    "Add Report Data","Edit Report Data","Delete Report Data","Add Config","Edit Config","Delete Config",
//                    "Add Master Record","Edit Master Record","Delete Master Record","Clone Master"};
//            len = trailtype.length;
//            for(int i=0; i<len; i++){
//                auditTrailType = new AuditTrailType();
//                auditTrailType.setActionType(trailtype[i]);
//                jspSession.save(auditTrailType);
//            }
//
//            jspSession.getTransaction().commit();
//
//            jspSession.beginTransaction();
//            long[][] a = {{1,1}, {1,2}, {2,3}, {2,4}, {2,5}, {2,6}, {2,7}, {2,8}, {2,9}, {2,10}, {2,11}, {3,12}, {3,13}, {3,14},
//                        {3,15}, {3,16}, {3,17}, {3,18}, {3,19}, {4,20}, {4,21}, {4,22}, {4,23}, {4,24}, {4,25}, {4,26}};
//            len = a.length;
//            for(int i=0; i<len; i++){
//                auditGroups = (AuditGroups) jspSession.load(AuditGroups.class, a[i][0]);
//                auditTrailType = (AuditTrailType) jspSession.load(AuditTrailType.class, a[i][1]);
//                auditGroups.getTrailType().add(auditTrailType);
//            }

            mb_linkgroup linkgroup = null;
            String linkName[] = {"Quick Links","Shortcut Links"};
            len = linkName.length;
            for(int i=0; i<len; i++){
                linkgroup = new mb_linkgroup();
                linkgroup.setGrouptext(linkName[i]);
                save(linkgroup);
            }
            //Check applicationid or use constant
            mb_dashboard dashboard = new mb_dashboard();
            dashboard.setApplicationid(applicationid);
            save(dashboard);
        } catch(Exception ex) {
//            jspSession.getTransaction().rollback();
            logger.warn("Record Insertion Failed", ex);
            throw ServiceException.FAILURE("AuthHandler.getRolePermisionSet", ex);
        }
    }

    public String insertReportData(HttpServletRequest request) throws ServiceException {
        String retStr = "";
        String reportid = request.getParameter("reportid");
        ReportHandlers rhandler = new reportMethods(this, sessionHandlerDao);
        retStr = rhandler.insertRecord(request);
        return retStr;
    }

    public String updateReportData(HttpServletRequest request) throws ServiceException {
        String retStr = "";
        String reportid = request.getParameter("reportid");
        ReportHandlers rhandler = new reportMethods(this, sessionHandlerDao);
        retStr = rhandler.editRecord(request);
        return retStr;
    }

    public String deleteReportRecord(HttpServletRequest request) throws ServiceException {
        String retStr = "";
        String reportid = request.getParameter("reportid");
        ReportHandlers rhandler = new reportMethods(this, sessionHandlerDao);
        retStr = rhandler.deleteRecord(request);
        return retStr;
    }

    public String getReportData(HttpServletRequest request) throws ServiceException {
        String retStr = "";
        String reportid = request.getParameter("reportid");
        ReportHandlers rhandler = new reportMethods(this, sessionHandlerDao);
        retStr = rhandler.loadData(request);
        return retStr;
    }

    public String reportData(HttpServletRequest request) throws ServiceException {
        String retStr = "";
        String reportid = request.getParameter("reportid");
//        ArrayList<Hashtable<String, String>> columnList = getColumnInfo(session,reportid);
//        retStr +=  "{columnheader:["
//              + makeColumnHeader(session,request.getParameter("reportid"))
//                            + "],tablename :\""+getReportTableName(session, reportid)+"\"}";
        retStr =  makeColumnHeader(request.getParameter("reportid"));
        return retStr;
    }

    public String comboFilterConfig(HttpServletRequest request) throws ServiceException {
        String retStr = "";
        String id = request.getParameter("id");
        JSONObject jtemp = new JSONObject();
        try {
            mb_gridconfig gridObj = (mb_gridconfig) get(mb_gridconfig.class, id);
            String SELECT_QUERY = " from com.krawler.esp.hibernate.impl.mb_comboFilterConfig as mb_comboFilterConfig where mb_comboFilterConfig.gridconfigid = ? order by mb_comboFilterConfig.xtype ";
            List list = find(SELECT_QUERY, new Object[]{gridObj});
            Iterator ite = list.iterator();
            while( ite.hasNext() ) {
                mb_comboFilterConfig obj = (mb_comboFilterConfig) ite.next();
                JSONObject jObj = new JSONObject();
                jObj.put("gridconfigid", obj.getGridconfigid().getId());
                jObj.put("fieldname", obj.getFieldname());
                jObj.put("displayfield", obj.getDisplayfield());
                jObj.put("xtype", obj.getXtype());
                jObj.put("reftable", obj.getReftable());
                jObj.put("refmoduleid", obj.getRefmoduleid());
                jObj.put("refcol", obj.getRefcol());
                jtemp.append("data", jObj);
            }
            retStr = jtemp.toString();
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
          throw ServiceException.FAILURE("reportbuilder.comboFilterConfig", e);
        }
        return retStr;
    }

    public String comboFilterData(HttpServletRequest request) throws ServiceException {
        String retStr = "";
        String refmoduleid = request.getParameter("refmoduleid");
        String refcol = request.getParameter("refcol");
        JSONObject jtemp = new JSONObject();
        try {
            String reftable = getReportTableName(refmoduleid);
            String SELECT_QUERY = "select id, "+refcol+" from "+PropsValues.PACKAGE_PATH+"."+ reftable +" order by "+refcol;
            List list = executeQuery(SELECT_QUERY);
            Iterator ite = list.iterator();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject jObj = new JSONObject();
                jObj.put("id", row[0]);
                jObj.put("name", row[1]);
                jtemp.append("data", jObj);
            }
            retStr = jtemp.toString();
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
          throw ServiceException.FAILURE("reportbuilder.comboFilterData", e);
        }
        return retStr;
    }

    public String moduleGridData(HttpServletRequest request) throws ServiceException {
        String retStr = "";
        try{
            String reportid = request.getParameter("reportid");
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
            String SELECT_QUERY = "select mb_gridconfig.name, mb_gridconfig.xtype, mb_gridconfig.displayfield, mb_gridconfig.reftable, mb_gridconfig.renderer, " +
                "mb_gridconfig.hidden, mb_gridconfig.combogridconfig from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig where mb_gridconfig.reportid = ? order by mb_gridconfig.columnindex ";
            String tName = getReportTableName(reportid);
            List list = find(SELECT_QUERY, new Object[]{report});
            Iterator ite = list.iterator();
            JSONObject r = new JSONObject();
            while( ite.hasNext() ) {
                JSONObject jObj = new JSONObject();
                Object[] row = (Object[]) ite.next();
                jObj = new JSONObject();
                jObj.put("displayfield",row[2].toString());
                jObj.put("name",row[0]);
                jObj.put("xtype",row[1].toString());
                jObj.put("hidden",Boolean.parseBoolean(row[5].toString()));
                jObj.put("reftable",row[3].toString());
                jObj.put("renderer",row[4].toString());
                jObj.put("combogridconfig",row[6].toString());
                r.append("column", jObj);
            }
            JSONObject tr = new JSONObject();
//            tr.put("valid", true);
            r.put("tablename", tName);
//            tr.put("reportid", reportid);
//            tr.put("data", r.toString());
            retStr = r.toString();
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
        }
        return retStr;
    }

   public String makeColumnHeader(String reportid) throws ServiceException {
        String returnColumnHeader = "";
        JSONObject jtemp = new JSONObject();
        try {
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
            String SELECT_QUERY = "select mb_gridconfig.name, mb_gridconfig.xtype, mb_gridconfig.displayfield, mb_gridconfig.reftable, mb_gridconfig.renderer, " +
                "mb_gridconfig.hidden, mb_gridconfig.combogridconfig,mb_gridconfig.summaryType, mb_gridconfig.id from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig where mb_gridconfig.reportid = ? order by mb_gridconfig.columnindex ";

            List list = find(SELECT_QUERY, new Object[]{report});
            Iterator ite = list.iterator();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject jObj = new JSONObject();
                jObj = new JSONObject();
                jObj.put("0",row[2]);            // column header
                jObj.put("1",row[0]);                       // dataindex
                jObj.put("2",row[1]);            // editor for editgridpanel
                if(row[5]!=null){
                    jObj.put("3",Boolean.parseBoolean(row[5].toString()));// is hidden
                }
//                if(row[3] != null)
                    jObj.put("4",row[3]);            // reference table
//                else
//                    jObj.put("4","");            // reference table
                jObj.put("5",row[6]);  //combogridconfig
                jObj.put("conftype",row[1]) ;
                jObj.put("gridconfigid",row[8]) ;
                renderer renderer=(renderer) row[4];

                if (!renderer.getId().equals("0") && !renderer.getId().equals("")){
                        jObj.put("6",renderer.getRendererValue());  //renderergridconfig
                }
//                returnColumnHeader += jObj.toString()+",";
                if(row[7]!=null){
                    if (!row[7].toString().equalsIgnoreCase("None")){
                        jObj.put("7",row[7]) ; // summaryType
                }
                }
                jtemp.append("columnheader", jObj);
//                returnColumnHeader += jObj.toString()+",";
            }

            SELECT_QUERY = "select mb_moduleConfigMap.configid.configid from com.krawler.esp.hibernate.impl.mb_moduleConfigMap " +
                    "as mb_moduleConfigMap where mb_moduleConfigMap.moduleid = ? ";

            list = find(SELECT_QUERY, new Object[]{report});
            ite = list.iterator();
            while( ite.hasNext() ) {
                int configid = (Integer) (ite.next());
                if(configid == 1) { // Comments
                     JSONObject jObj = new JSONObject();
                     jObj = new JSONObject();
                    jObj.put("0","Comments");            // column header
                    jObj.put("1","comments");                       // dataindex
                    jObj.put("2","None");            // editor for editgridpanel
                    jObj.put("3",false);// is hidden
                    jObj.put("4","mb_stdConfigs");            // reference table
                    jObj.put("5","-1");  //combogridconfig
                    jtemp.append("columnheader", jObj);
//                    returnColumnHeader += jObj.toString()+",";
                }else if(configid == 2){     //documents
                    JSONObject jObj = new JSONObject();
                    jObj = new JSONObject();
                    jObj.put("0","Documents");            // column header
                    jObj.put("1","docs_id");                       // dataindex
                    jObj.put("2","None");            // editor for editgridpanel
                    jObj.put("3",false);// is hidden
                    jObj.put("4","mb_stdConfigs");            // reference table
                    jObj.put("5","-1");  //combogridconfig
                    jtemp.append("columnheader", jObj);
//                    returnColumnHeader += jObj.toString()+",";
                }
            }
             jtemp.put("stdconfig", getStdConfig(reportid));
             jtemp.put("tablename",getReportTableName(reportid));
             jtemp.put("buttonConf", getButtonConf(reportid));
             jtemp.put("stdbtnConf", getStdButtonConf(reportid));
        }

        catch(Exception e) {
            logger.warn(e.getMessage(), e);
          throw ServiceException.FAILURE("reportbuilder.makeColumnHeader", e);
        }
        return jtemp.toString();
    }

    public ArrayList<Hashtable<String, String>> getColumnInfo(String reportid) throws ServiceException{
        ArrayList<Hashtable<String, String>> aList = new ArrayList<Hashtable<String, String>>();
        mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
        String SELECT_QUERY = "select mb_gridconfig.name, mb_gridconfig.xtype, mb_gridconfig.displayfield, mb_gridconfig.reftable, mb_gridconfig.renderer, mb_gridconfig.filter," +
                "mb_gridconfig.hidden from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig where mb_gridconfig.reportid = ? ";
        List list = executeQuery(SELECT_QUERY, new Object[]{report});
        Iterator ite = list.iterator();
        int i = 0;
        while( ite.hasNext() ) {
            Object[] row = (Object[]) ite.next();
            aList.add(i,new Hashtable<String, String>());
            aList.get(i).put("name", row[0].toString());
            aList.get(i).put("xtype", row[1].toString());
            aList.get(i).put("displayfield", row[2].toString());
            aList.get(i).put("reftable", row[3].toString());
            aList.get(i).put("renderer", row[4].toString());
            aList.get(i).put("filter", row[5].toString());
            aList.get(i).put("hidden", row[6].toString());
            i++;
        }
        return aList;
    }

    public String getAllLinkGroups(HttpServletRequest request) throws ServiceException{
        String ret= "";
        try{
            String hql = "SELECT mb_linkgroup.groupid, mb_linkgroup.grouptext FROM " + PropsValues.PACKAGE_PATH + ".mb_linkgroup AS mb_linkgroup";
            List lst = executeQuery(hql);
            Iterator ite = lst.iterator();
            JSONObject r = new JSONObject();
            while(ite.hasNext()){
                JSONObject temp = new JSONObject();
                Object[] row = (Object[]) ite.next();
                temp.put("groupid", row[0].toString());
                temp.put("groupname", row[1].toString());
                r.append("data", temp);
            }
            r.put("valid", true);
            ret = r.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }
    public String getAllPortlets(HttpServletRequest request) throws ServiceException{
        String ret= "";
        try{
            String hql = "SELECT mb_dashportlet.portletid, mb_dashportlet.config, mb_reportlist.reportname, mb_reportlist.reportid, mb_dashportlet.portlettitle " +
                "FROM " + PropsValues.PACKAGE_PATH + ".mb_dashportlet AS mb_dashportlet INNER JOIN mb_dashportlet.reportid as mb_reportlist";
            List lst = executeQuery(hql);
            Iterator ite = lst.iterator();
            JSONObject r = new JSONObject();
            while(ite.hasNext()){
                JSONObject temp = new JSONObject();
                Object[] row = (Object[]) ite.next();
                temp.put("portletid", row[0].toString());
                temp.put("config", row[1].toString());
                temp.put("reportname", row[2].toString());
                temp.put("reportid", row[3].toString());
                temp.put("portlettitle", row[4].toString());
//                temp.put(ret, lst);
                r.append("data", temp);
            }
            r.put("valid", true);
            ret = r.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }
    public String getAllLinks(HttpServletRequest request) throws ServiceException{
        String ret= "";
        try{
//            String hql = "SELECT mb_dashlinks.linkid, mb_dashlinks.linktext, mb_processChart.processname, mb_processChart.processid, " +
//                "mb_linkgroup.groupid, mb_linkgroup.grouptext FROM " + PropsValues.PACKAGE_PATH + ".mb_dashlinks AS mb_dashlinks " +
//                "INNER JOIN mb_dashlinks.processid AS mb_processChart INNER JOIN mb_dashlinks.groupid AS mb_linkgroup " +
//                "WHERE mb_dashlinks.dashboardid.dashboardid = ?";
            String hql = "SELECT mb_dashlinks.linkid, mb_dashlinks.linktext, mb_reportlist.reportname, mb_reportlist.reportid, " +
                "mb_linkgroup.groupid, mb_linkgroup.grouptext FROM " + PropsValues.PACKAGE_PATH + ".mb_dashlinks AS mb_dashlinks " +
                "INNER JOIN mb_dashlinks.processid AS mb_reportlist INNER JOIN mb_dashlinks.groupid AS mb_linkgroup " +
                "WHERE mb_dashlinks.dashboardid.dashboardid = ?";
            List lst = find(hql, new Object[] { 1 });
            Iterator ite = lst.iterator();
            JSONObject r = new JSONObject();
            while(ite.hasNext()){
                JSONObject temp = new JSONObject();
                Object[] row = (Object[]) ite.next();
                temp.put("linkid", row[0].toString());
                temp.put("linktext", row[1].toString());
                temp.put("processname", row[2].toString());
                temp.put("processid", row[3].toString());
                temp.put("groupid", row[4].toString());
                temp.put("grouptext", row[5].toString());
                r.append("data", temp);
            }
            r.put("valid", true);
            ret = r.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }
    public String getAllModules(HttpServletRequest request) throws ServiceException{
        String ret= "";
        try{
            String hql = "SELECT mb_reportlist.reportid, mb_reportlist.reportname FROM " + PropsValues.PACKAGE_PATH + ".mb_reportlist AS mb_reportlist where mb_reportlist.deleteflag = 0";
            List lst = executeQuery(hql);
            Iterator ite = lst.iterator();
            JSONObject r = new JSONObject();
            while(ite.hasNext()){
                JSONObject temp = new JSONObject();
                Object[] row = (Object[]) ite.next();
                temp.put("moduleid", row[0].toString());
                temp.put("modulename", row[1].toString());
                temp.put("columns", makeColumnHeader(row[0].toString()));
                r.append("data", temp);
            }
            r.put("valid", true);
            ret = r.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }

    public String getAllProcesses(HttpServletRequest request) throws ServiceException{
        String ret= "";
        try{
            String hql = "SELECT mb_processChart.processid, mb_processChart.processname FROM " + PropsValues.PACKAGE_PATH + ".mb_processChart AS mb_processChart ";
            List lst = executeQuery(hql);
            Iterator ite = lst.iterator();
            JSONObject r = new JSONObject();
            while(ite.hasNext()){
                JSONObject temp = new JSONObject();
                Object[] row = (Object[]) ite.next();
                temp.put("processid", row[0].toString());
                temp.put("processname", row[1].toString());
                r.append("data", temp);
            }
            r.put("valid", true);
            ret = r.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }

    public String storeStortcutConf(HttpServletRequest request) throws ServiceException{
        String ret = "";
        try{
            JSONObject conf = new JSONObject(request.getParameter("config"));
            if(conf.getString("shortcutType").equals("link")){
//                mb_processChart process = (mb_processChart) get(mb_processChart.class, request.getParameter("processid"));
                mb_reportlist process = (mb_reportlist) get(mb_reportlist.class, request.getParameter("processid"));
                String gId = conf.getString("groupid");
                mb_linkgroup grp = null;
                if(gId.equals("newrec")){
                    grp = new mb_linkgroup();
//                    grp.setGroupid(UUID.randomUUID().toString());
                    grp.setGrouptext(conf.getString("grpName"));
                    save(grp);
                } else {
                    grp = (mb_linkgroup) get(mb_linkgroup.class, gId);
                }
                mb_dashlinks lnk = new mb_dashlinks();
                lnk.setGroupid(grp);
                lnk.setLinkid(UUID.randomUUID().toString());
                lnk.setLinktext(conf.getString("linkText"));
                lnk.setProcessid(process);
                save(lnk);
            } else {
                mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, request.getParameter("reportid"));
                mb_dashportlet portlet = new mb_dashportlet();
                portlet.setPortletid(UUID.randomUUID().toString());
                portlet.setPortlettitle(conf.getString("portletTitle"));
                portlet.setReportid(report);
                portlet.setConfig(conf.getString("pConf"));
                save(portlet);
            }
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }
    public String storeDashboardConf(HttpServletRequest request) throws ServiceException{
        String ret = "{'success': true}";
        HashMap<String,String> arrParam = new HashMap<String,String>();        
        try{
            String temp = "";
            ArrayList<FileItem> fi = new ArrayList<FileItem>();
            boolean linksAddFlag = Boolean.parseBoolean(request.getParameter("linksAddFlag"));
            if(!linksAddFlag) {
                boolean fileUpload=false;
                parseRequest(request, arrParam, fi, fileUpload);
                temp = arrParam.get("grpjson");
                mb_dashboard dash = (mb_dashboard) get(mb_dashboard.class, 1);
                HashMap idMap = new HashMap();
                HashMap grpMap = new HashMap();
                if(!StringUtil.isNullOrEmpty(temp)){
                    JSONArray grpArr = new JSONArray(temp);
                    JSONArray grp = new JSONObject(getAllLinkGroups(request)).getJSONArray("data");
                    for(int i = 0; i < grp.length(); i++){
                        JSONObject tObj = grp.getJSONObject(i);
                        grpMap.put(tObj.getString("groupid"), tObj.getString("groupname"));
                    }
                    for(int i = 0; i < grpArr.length(); i++){
                        JSONObject tObj = grpArr.getJSONObject(i);
                        if(!grpMap.containsKey(tObj.getString("groupid"))){
                            com.krawler.esp.hibernate.impl.mb_linkgroup lg = new mb_linkgroup();
    //                        lg.setGroupid(UUID.randomUUID().toString());
                            lg.setGrouptext(tObj.getString("groupname"));
                            save(lg);
                            idMap.put(tObj.getString("groupid"), lg.getGroupid());
                        } else {
                            grpMap.remove(tObj.getString("groupid"));
                        }
                    }
                }
                temp = arrParam.get("linkjson");
                String hql = "DELETE FROM " + PropsValues.PACKAGE_PATH + ".mb_dashlinks AS mb_dashlinks WHERE mb_dashlinks.dashboardid = ?";
                executeUpdate(hql, new Object[] { dash });
                if(!StringUtil.isNullOrEmpty(temp)){
                    JSONArray linkArr = new JSONArray(temp);
                    for(int i = 0; i < linkArr.length(); i++){
                        JSONObject tObj = linkArr.getJSONObject(i);
                        if(idMap.containsKey(tObj.getString("groupid"))){
                            tObj.put("groupid", idMap.get(tObj.getString("groupid")));
                        }
                        storeDashboardLink(tObj);
                    }
                }
                Object[] grpA = grpMap.keySet().toArray();
                for(int i = 0; i < grpA.length; i++){
                    deleteLinkGroup(grpA[i].toString());
                }
                temp = arrParam.get("portletjson");
                hql = "DELETE FROM " + PropsValues.PACKAGE_PATH + ".mb_dashportlet AS mb_dashportlet WHERE mb_dashportlet.dashboardid = ?";
                executeUpdate(hql, new Object[] { dash });
                if(!StringUtil.isNullOrEmpty(temp)){
                    JSONArray portletArr = new JSONArray(temp);
                    for(int cnt = 0; cnt < portletArr.length(); cnt++){
                        JSONObject tObj = portletArr.getJSONObject(cnt);
                        com.krawler.esp.hibernate.impl.mb_dashportlet port = new mb_dashportlet();
                        port.setDashboardid(dash);
                        port.setPortletid(UUID.randomUUID().toString());
                        mb_reportlist tempReport = (mb_reportlist) get(mb_reportlist.class, tObj.getString("moduleid"));
                        port.setReportid(tempReport);
                        port.setConfig(tObj.getString("colconfig"));
                        port.setPortlettitle(tObj.getString("portlet"));
                        save(port);
                    }
                }
                if (fi.size() > 0) {
                    com.krawler.esp.handlers.genericFileUpload uploader = new com.krawler.esp.handlers.genericFileUpload();
                    String destinationdir = PropsValues.STORE_PATH;
                    uploader.uploadFile(fi.get(0), destinationdir, "logo");
                    if (!uploader.ErrorMsg.equals("")) {
                        ret = "{'success':true,error:'" + uploader.ErrorMsg + "'}";
                    }
                }
            } else {
                temp = request.getParameter("linkjson");
                if(!StringUtil.isNullOrEmpty(temp)){
                    JSONArray linkArr = new JSONArray(temp);
                    for(int i = 0; i < linkArr.length(); i++){
                        JSONObject tObj = linkArr.getJSONObject(i);
                        boolean duplicateName = checkDuplicateLinkName(tObj);
                        if(!duplicateName) {
                            storeDashboardLink(tObj);
                        } else {
                            ret = "{'success':false,'error':'Link name is already present for the same module.'}";
                            break;
                        }
                    }
                }
            }
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }
    public void deleteLinkGroup(String groupid) throws ServiceException{
        try{
            String hql = "DELETE FROM " + PropsValues.PACKAGE_PATH + ".mb_dashlinks AS mb_dashlinks WHERE mb_dashlinks.groupid.groupid = ?";
            executeUpdate(hql, new Object[] { groupid });
            hql = "DELETE FROM " + PropsValues.PACKAGE_PATH + ".mb_linkgroup AS mb_linkgroup WHERE mb_linkgroup.groupid = ?";
            executeUpdate(hql, new Object[] { groupid });
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }
    public void storeDashboardLink(JSONObject link) throws ServiceException{
        try{
            mb_dashboard dash = (mb_dashboard) get(mb_dashboard.class, 1);
            mb_linkgroup lg = (mb_linkgroup) get(mb_linkgroup.class, link.getInt("groupid"));
            if(lg == null)
                lg = (mb_linkgroup) get(mb_linkgroup.class, link.getInt("groupid"));
            mb_reportlist tempReport = (mb_reportlist) get(mb_reportlist.class, link.getString("processid"));
//            mb_processChart process = (mb_processChart) get(mb_processChart.class, link.getString("processid"));
            mb_dashlinks dl = new mb_dashlinks();
            dl.setLinktext(link.getString("link"));
            dl.setLinkid(UUID.randomUUID().toString());
            dl.setDashboardid(dash);
            dl.setGroupid(lg);
            dl.setProcessid(tempReport);
            save(dl);
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public boolean checkDuplicateLinkName(JSONObject link) throws ServiceException{
        boolean duplicateName = false;
        try{
            String hql = "SELECT mb_dashlinks FROM " + PropsValues.PACKAGE_PATH + ".mb_dashlinks AS mb_dashlinks " +
                "WHERE mb_dashlinks.groupid.groupid = ? and  mb_dashlinks.linktext = ? and mb_dashlinks.processid.reportid = ? ";
            List lst = find(hql, new Object[] {link.getInt("groupid"), link.getString("link"), link.getString("processid")});

            if(lst.size() > 0) {
                duplicateName = true;
            }
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return duplicateName;
    }

    public String getDashboardGroupLinks(HttpServletRequest request) throws ServiceException{
        String ret = "";
        try{
            String strLinks = "";
            int groupid =  Integer.parseInt(request.getParameter("groupid"));
            mb_linkgroup grp = (mb_linkgroup) get(mb_linkgroup.class, groupid);
            ret = "<div class='dashlinkspanel'>" +
                "<ul id='quicklinksUL_"+groupid+"'>";
//            ret += "<li>" + grp.getGrouptext() + "<ul>";
            String hql = "SELECT mb_dashlinks FROM " + PropsValues.PACKAGE_PATH + ".mb_dashlinks AS mb_dashlinks " +
                "WHERE mb_dashlinks.groupid = ?";
            List linklst = find(hql, new Object[] { grp });
            Iterator linkite = linklst.iterator();
            while(linkite.hasNext()){
                mb_dashlinks lnk = (mb_dashlinks) linkite.next();
                mb_reportlist modObj = lnk.getProcessid();
                JSONObject permObj1 = getModulePermission(modObj.getReportid(), 9);
                JSONObject recordperm = new JSONObject(permObj1.get("recordperm").toString());
                JSONObject jobj = new JSONObject(recordperm.get("9").toString());
                int permgrid = Integer.parseInt(jobj.get("permgrid").toString());
                int perm = Integer.parseInt(jobj.get("perm").toString());
                String str = checktabperms(permgrid, perm, request);
                if(!StringUtil.isNullOrEmpty(str) && !str.equals("false")) {
                    strLinks += "<li><a onclick=\"openModuleTab('" + lnk.getProcessid().getReportid() + "')\" href=#>" + lnk.getLinktext() +
                        "</a></li>";
                }
            }
            if(StringUtil.isNullOrEmpty(strLinks)) {
                strLinks += "<li>No links to display.</li>";
            }
            ret += strLinks;
//            ret += "</ul></li>";
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret += "</ul></div>";
    }
    public String getDashboardLinks(HttpServletRequest request) throws ServiceException{
        JSONObject jsonData = new JSONObject();

        try{
//            String hql = "SELECT mb_linkgroup FROM " + PropsValues.PACKAGE_PATH + ".mb_linkgroup AS mb_linkgroup " +
//                    "WHERE mb_linkgroup.groupid NOT IN (?,?)";
//            List lst = find(hql, new Object[] {1, 2});
//            Iterator ite = lst.iterator();
//            while(ite.hasNext()){
//                mb_linkgroup grp = (mb_linkgroup) ite.next();
//                ret += "<li>" + grp.getGrouptext() + "<ul>";
//                hql = "SELECT mb_dashlinks FROM " + PropsValues.PACKAGE_PATH + ".mb_dashlinks AS mb_dashlinks " +
//                    "WHERE mb_dashlinks.groupid = ?";
//                List linklst = find(hql, new Object[] { grp });
//                Iterator linkite = linklst.iterator();
//                while(linkite.hasNext()){
//                    mb_dashlinks lnk = (mb_dashlinks) linkite.next();
//                    mb_reportlist modObj = lnk.getProcessid();
//                    ret += "<li><a onclick=\"openModuleTab('" + lnk.getProcessid().getReportid() + "')\" href=#>" + lnk.getLinktext() +
//                                "</a></li>";
//
////                    hql = "Select pm_taskstepmap.stepid, pm_taskstepmap.taskid from " + PropsValues.PACKAGE_PATH + ".pm_taskstepmap as pm_taskstepmap " +
////                            "inner join pm_taskstepmap.taskid as pm_taskmaster where pm_taskmaster.processid = ? ";
////                    List lst2 = find(hql, new Object[] {lnk.getProcessid()});
////                    Iterator ite2 = lst2.iterator();
////                    String taskids = "";
////                    String moduleids = "";
////                    while(ite2.hasNext()) {
////                        Object[] row = (Object[]) ite2.next();
////                        pm_taskmaster taskObj = (pm_taskmaster) row[1];
////                        String stepid = row[0].toString();
////                        mb_reportlist modObj = (mb_reportlist) get(mb_reportlist.class, stepid);
////
////                        String permObj1 = getModulePermission(stepid, taskObj.getTaskid());
////                        JSONArray recordperm = new JSONObject(permObj1).getJSONArray("recordperm");
////                        for(int i = 0; i < 2; i++) {
////                            JSONObject jobj = new JSONObject(new JSONArray(recordperm.getJSONObject(i).getJSONArray(i+2+"").toString()).get(0).toString());
////                            int permgrid = Integer.parseInt(jobj.get("permgrid").toString());
////                            int perm = Integer.parseInt(jobj.get("perm").toString());
////                            String str = checktabperms(permgrid, perm, request);
////                            if(!StringUtil.isNullOrEmpty(str) && !str.equals("false")) {
//////                                if(!moduleids.contains(stepid)) {
////                                    taskids += taskObj.getTaskid()+",";
////                                    moduleids += stepid+",";
//////                                }
////                                break;
////                            }
////                        }
////                    }
////                    if(!StringUtil.isNullOrEmpty(taskids)) {
////                        taskids = taskids.substring(0, taskids.length()-1);
////                        moduleids = moduleids.substring(0, moduleids.length()-1);
////                        ret += "<li><a onclick=\"navigate('link', '" + lnk.getProcessid().getReportid() + "', '"+taskids+"', '"+moduleids+"', '"+lnk.getLinktext()+"')\" href=#>" + lnk.getLinktext() +
////                                "</a></li>";
////                    }
//                }
//                ret += "</ul></li>";
//            }
            String strLinks = "";
            mb_linkgroup ql = (mb_linkgroup) get(mb_linkgroup.class, 1);
            String hql = "SELECT mb_dashlinks FROM " + PropsValues.PACKAGE_PATH + ".mb_dashlinks AS mb_dashlinks " +
                "WHERE mb_dashlinks.groupid.groupid in (?, ?) ";
            List lst = find(hql, new Object[] {1, 2});
            Iterator ite = lst.iterator();
            JSONArray jarray= new JSONArray();
            while(ite.hasNext()){
                mb_dashlinks lnk = (mb_dashlinks) ite.next();
                mb_reportlist modObj = lnk.getProcessid();
                JSONObject permObj1 = getModulePermission(modObj.getReportid(), 9);
                JSONObject recordperm = new JSONObject(permObj1.get("recordperm").toString());
                JSONObject jobj = new JSONObject(recordperm.get("9").toString());
                int permgrid = Integer.parseInt(jobj.get("permgrid").toString());
                int perm = Integer.parseInt(jobj.get("perm").toString());
                String str = checktabperms(permgrid, perm, request);
                if(!StringUtil.isNullOrEmpty(str) && !str.equals("false")) {
                    JSONObject jobjTemp = new JSONObject();
                    jobjTemp.put("moduleid", lnk.getProcessid().getReportid());
                    jobjTemp.put("linktext", lnk.getLinktext());

                   jarray.put(jobjTemp);
                }
//                hql = "Select pm_taskstepmap.stepid, pm_taskstepmap.taskid from " + PropsValues.PACKAGE_PATH + ".pm_taskstepmap as pm_taskstepmap " +
//                            "inner join pm_taskstepmap.taskid as pm_taskmaster where pm_taskmaster.processid = ? ";
//                List lst2 = find(hql, new Object[] {lnk.getProcessid()});
//                Iterator ite2 = lst2.iterator();
//                String taskids = "";
//                String moduleids = "";
//                while(ite2.hasNext()) {
//                    Object[] row = (Object[]) ite2.next();
//                    pm_taskmaster taskObj = (pm_taskmaster) row[1];
//                    String stepid = row[0].toString();
//                    mb_reportlist modObj = (mb_reportlist) get(mb_reportlist.class, stepid);
//
//                    String permObj1 = getModulePermission(stepid, taskObj.getTaskid());
//                    JSONArray recordperm = new JSONObject(permObj1).getJSONArray("recordperm");
//                    for(int i = 0; i < 2; i++) {
//                        JSONObject jobj = new JSONObject(new JSONArray(recordperm.getJSONObject(i).getJSONArray(i+2+"").toString()).get(0).toString());
//                        int permgrid = Integer.parseInt(jobj.get("permgrid").toString());
//                        int perm = Integer.parseInt(jobj.get("perm").toString());
//                        String str = checktabperms(permgrid, perm, request);
//                        if(!StringUtil.isNullOrEmpty(str) && !str.equals("false")) {
////                            if(!moduleids.contains(stepid)) {
//                                taskids += taskObj.getTaskid()+",";
//                                moduleids += stepid+",";
////                            }
//                            break;
//                        }
//                    }
//                }
//                if(!StringUtil.isNullOrEmpty(taskids)) {
//                    taskids = taskids.substring(0, taskids.length()-1);
//                    moduleids = moduleids.substring(0, moduleids.length()-1);
//                    ret += "<li><a onclick=\"navigate('link', '" + lnk.getProcessid().getReportid() + "', '"+taskids+"', '"+moduleids+"', '"+lnk.getLinktext()+"')\" href=#>" + lnk.getLinktext() +
//                            "</a></li>";
//                }
            }
//            if(StringUtil.isNullOrEmpty(strLinks)) {
//                strLinks += "<li>No links to display.</li>";
//            }
//
            jsonData.put("data", jarray);
            jsonData.put("data",new JSONObject(jsonData.toString()));
            jsonData.put("valid",true);
            jsonData.put("success",true);
            //ret += strLinks;
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        
        return jsonData.toString();
    }
    public String getReportDetails(HttpServletRequest request) throws ServiceException{
        String ret = "";
        try{
            JSONObject jobj = new JSONObject();
            String[] reportids = request.getParameter("reportids").split(",");
            String[] taskids = request.getParameter("taskids").split(",");
            for(int i = 0; i < reportids.length; i++) {
                String reportid = reportids[i];
                String taskid = taskids[i];

                JSONObject r = new JSONObject();
                mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
                boolean isReport = false;
                if(report.getType() == 1){
                    isReport = true;
                } else {
                    String hql = "SELECT mb_forms.data FROM " + PropsValues.PACKAGE_PATH + ".mb_forms AS mb_forms WHERE mb_forms.moduleid = ?";
                    List lst = find(hql, new Object[] { report });
                    Iterator ite = lst.iterator();
                    if(ite.hasNext()){
                        r.put("jdata", ite.next().toString());
                    }
                }
                r.put("isreport", isReport);
                r.put("reportid", reportid);
                r.put("taskid", taskid);
                r.put("reportname", report.getReportname());
                r.put("perms", getModulePermission(reportid));

                jobj.append("reportdata", r);
            }
            ret = jobj.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }

    public String checkCompanyMBPermission(String companyid) throws ServiceException{
        String ret = "";
        try{
            int mbflag = 0;
            JSONObject jobj = new JSONObject();
            String query = "select mbflag from mb_companymap where companyid = ?";
            List<Integer> l = (List)executeNativeQuery(query, new Object[] {companyid});
            if(l != null && !l.isEmpty()) {
                mbflag = l.get(0);
            }
            jobj.put("mbflag", mbflag);
            ret = jobj.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }

    public String getDashboardData(HttpServletRequest request) throws ServiceException{
        String ret = "";
        try{
            String hql = "SELECT mb_dashportlet.portletid, mb_dashportlet.config, mb_dashportlet.portlettitle, mb_dashportlet.reportid " +
                "FROM " + PropsValues.PACKAGE_PATH + ".mb_dashportlet AS mb_dashportlet";
            List lst = executeQuery(hql);
            Iterator ite = lst.iterator();
            JSONObject r = new JSONObject();
            while(ite.hasNext()){
                JSONObject temp = new JSONObject();
                Object[] row = (Object[]) ite.next();
                mb_reportlist reportObj = (mb_reportlist) row[3];
                JSONObject permObj1 = getModulePermission(reportObj.getReportid(), 9);
                JSONObject recordperm = new JSONObject(permObj1.get("recordperm").toString());
                JSONObject jobj = new JSONObject(recordperm.get("9").toString());
                int permgrid = Integer.parseInt(jobj.get("permgrid").toString());
                int perm = Integer.parseInt(jobj.get("perm").toString());
                String str = checktabperms(permgrid, perm, request);
                if(!StringUtil.isNullOrEmpty(str) && !str.equals("false")) {
                    temp.put("portletid", row[0].toString());
                    temp.put("config", row[1].toString());
                    temp.put("portlettitle", row[2].toString());
                    temp.put("reportid", row[3].toString());
                    r.append("portletdata", temp);
                }
            }

            hql = "SELECT mb_linkgroup FROM " + PropsValues.PACKAGE_PATH + ".mb_linkgroup AS mb_linkgroup " +
                    "WHERE mb_linkgroup.groupid NOT IN (?,?)";
            lst = find(hql, new Object[] {1, 2});
            ite = lst.iterator();
            while(ite.hasNext()){
                mb_linkgroup grp = (mb_linkgroup) ite.next();
                JSONObject temp = new JSONObject();
                temp.put("groupid", grp.getGroupid());
                temp.put("grouptext", grp.getGrouptext());
                r.append("groupdata", temp);
            }
            r.put("valid", true);
            ret = r.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ret;
    }

    public String createNewReport (HttpServletRequest request) throws ServiceException {
        String result = "{\"success\":true}";
        try {
            String query ="select max(mb_reportlist.reportkey) as count from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist ";
            List list = executeQuery(query);
            Iterator ite = list.iterator();
            String mkey ="";
            if(ite.hasNext()) {
                Object cnt = (Object) ite.next();
                if(cnt == null) {
                    mkey = "1";
                } else {
                    mkey = Integer.toString(Integer.parseInt(cnt.toString())+1);
                }
            }
            mkey = toLZ(Integer.parseInt(mkey), 3);
            String reportname = request.getParameter("name");//.replace(" ","").toLowerCase();
//            String tableName = "report_"+mkey+"_"+reportname;

            com.krawler.esp.hibernate.impl.mb_reportlist report = new com.krawler.esp.hibernate.impl.mb_reportlist();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(ModuleBuilderController.USER_DATEPREF);
            java.sql.Timestamp timestamp1 = Timestamp.valueOf(sdf.format(new java.util.Date()));
            report.setReportname(reportname);
            report.setReportkey(Integer.parseInt(mkey));
            report.setCreateddate(timestamp1);
            report.setModifieddate(timestamp1);
            report.setType(1);
            report.setCreatedby(sessionHandlerDao.getUserid());
            report.setTableflag(Integer.parseInt(request.getParameter("tableflag")));
            save(report);
//            result = "{\"success\":true, \"reportid\":\""+report.getReportid()+"\",\"reportkey\":\""+report.getReportkey()+"\",\"title\":\""+reportname+"\"}";
//            String actionType = "Add Report";
//            String details = request.getParameter("name") + " Report Added";
//            long actionId = AuditTrialHandler.getActionId(session, actionType);
            //Changes done by sm and anup
            JSONObject jobj = new JSONObject();
             JSONObject jtemp2 = new JSONObject();

             jtemp2.put("reportname",report.getReportname());
             jtemp2.put("reportid",report.getReportid());
             jtemp2.put("tablename",report.getTablename());
             jtemp2.put("reportkey",report.getReportkey());
             jtemp2.put("createddate",report.getCreateddate());
             jtemp2.put("title",reportname);
             jtemp2.put("id","report_"+report.getReportid());
             jtemp2.put("tableflag",report.getTableflag());

             jobj.append("data", jtemp2.toString());
             jobj.put("success", "true");
             result = jobj.toString();
//            AuditTrialHandler.insertAuditLog(session, actionId, details, request);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.createNewReport", e);
        }
        return result;
    }

    public String getReportsList(HttpServletRequest request) throws ServiceException {
        JSONObject jobj = new JSONObject();
        String result = "{'TotalCount':0,'data':[]}";
        long count = 0;
        try {
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String ss = "";
            ArrayList<Object> al = new ArrayList<Object>();
            String s1 = "";
            if(request.getParameter("ss") != null){
                ss = request.getParameter("ss");
                s1= StringUtil.getSearchString(ss, "and", new String[] {"mb_reportlist.reportname"});
                StringUtil.insertParamSearchString(al, ss, 1);
            }
            int tableflag = Integer.parseInt(request.getParameter("tableflag"));
            String hql = "select count(*) as count " +
                    "from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist where mb_reportlist.deleteflag = 0 and mb_reportlist.type = 1 and mb_reportlist.tableflag = "+tableflag + s1;
            List list = find(hql, al.toArray());
            Iterator ite = list.iterator();
            if( ite.hasNext() ) {
                count = Long.parseLong(ite.next().toString());
            }

            hql = "select mb_reportlist.reportid, mb_reportlist.reportname,mb_reportlist.createddate,mb_reportlist.tablename,mb_reportlist.reportkey " +
                    "from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist where mb_reportlist.deleteflag = 0  and mb_reportlist.type = 1 and mb_reportlist.tableflag = "+tableflag
                    +s1+" order by mb_reportlist.reportname";
            list = executeQueryPaging(hql, al.toArray(), new Integer[] {start, limit});
            ite = list.iterator();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject jtemp2 = new JSONObject();
                jtemp2.put("reportid", row[0]);
                jtemp2.put("reportname", row[1]);
                jtemp2.put("createddate", row[2]);
                jtemp2.put("tablename", row[3]);
                jtemp2.put("reportkey", row[4]);
                jobj.append("data", jtemp2);
            }
            jobj.put("TotalCount", count);
        } finally {
            if(count > 0) {
                return jobj.toString();
            } else {
                return result;
            }
        }
    }

//    public String saveReportGridConfig(HttpServletRequest request) throws ServiceException {
    public String saveReportGridConfig(String jsonstr, String reportid, boolean createTable, String tbar, String bbar) throws ServiceException {
        String result = "{\"success\":true}";
        String tableName = "";
//        String jsonstr = request.getParameter("jsondata");
        try {
            JSONObject jobj = new JSONObject();
//            String reportid = request.getParameter("reportid");
//            boolean createTable = Boolean.parseBoolean(request.getParameter("createtable"));
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
            if(createTable) {
                tableName = "rb_"+toLZ(report.getReportkey(), 3)+"_"+report.getReportname().replace(" ", "").toLowerCase();
            }else {
                tableName = report.getTablename();
            }
            HashSet<String> hashSet = new HashSet<String>();
            HashSet<String> finalHashSet = new HashSet<String>();

            String hql = "delete from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig where mb_gridconfig.reportid = ? " ;
            int numDelRec = executeUpdate(hql,new Object[]{report});
            JSONArray jsonArray = new JSONArray(jsonstr);
            int confCnt = 0;
            for (int k = 0; k < jsonArray.length(); k++) {
                jobj = jsonArray.getJSONObject(k);
                if(!jobj.getString("name").equals("id")) {
                    com.krawler.esp.hibernate.impl.mb_gridconfig gridConf = new com.krawler.esp.hibernate.impl.mb_gridconfig();
    //                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-d HH:mm:ss");
    //                java.sql.Timestamp timestamp1 = Timestamp.valueOf(sdf.format(new java.util.Date()));
                    if(jobj.getString("name").indexOf(".") > -1) {
                        String[] tablecolumn = jobj.getString("name").split("\\.");
                        gridConf.setName(tablecolumn[0] + PropsValues.REPORT_HARDCODE_STR+tablecolumn[1].toLowerCase());
                    } else {
                        if(jobj.getString("name").indexOf(PropsValues.REPORT_HARDCODE_STR)==-1){
//                            String Columnname = moduleBuilderMethods.getColumnName(moduleBuilderMethods.getcolumnNameStr(jobj.getString("name").toLowerCase()));
                             String Columnname = jobj.getString("name").toLowerCase();
                            gridConf.setName(tableName + PropsValues.REPORT_HARDCODE_STR+Columnname);
                        }
                    }

                    if(StringUtil.isNullOrEmpty(jobj.getString("displayfield")))
                        gridConf.setDisplayfield(jobj.getString("name"));
                    else
                        gridConf.setDisplayfield(jobj.getString("displayfield"));

                    if(!StringUtil.isNullOrEmpty(jobj.getString("reftable"))) {
                        gridConf.setReftable(jobj.getString("reftable"));
                    } else if(StringUtil.isNullOrEmpty(jobj.getString("reftable")) &&
                                !jobj.getString("combogridconfig").equals("-1")) {
                         gridConf.setReftable("");
                    } else {
                       if(createTable)
                          gridConf.setReftable(tableName);
                    }
                    gridConf.setXtype(jobj.getString("xtype"));
                    renderer render = null;
                    if(jobj.getString("renderer").length()>0) {
                        render =(renderer) get(renderer.class, jobj.getString("renderer"));
                    } else {
                        render =(renderer) get(renderer.class, "0");
                    }//
                    gridConf.setRenderer(render);
                   // gridConf.setFilter(jobj.getString("filter"));
                    gridConf.setSummaryType(jobj.getString("summaryType"));
                    gridConf.setDefaultValue(jobj.getString("defaultValue"));
                    gridConf.setHidden(Boolean.parseBoolean(jobj.getString("hidden")));
                    gridConf.setCountflag(Boolean.parseBoolean(jobj.getString("countflag")));
                    String combogridconfig = "-1";
                    String refTable = jobj.getString("reftable");
                    String xtype = jobj.getString("xtype");
                    if(xtype.equals("Combobox") && !StringUtil.isNullOrEmpty(refTable) && !refTable.equals(tableName)) {
                        String SELECT_QUERY="Select mb_reportlist.reportid from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist "
                              +" where mb_reportlist.tablename = ?";
                        List list = find(SELECT_QUERY, new Object[]{refTable});

                        Iterator ite = list.iterator();
                        String reportid1 = null;
                        if( ite.hasNext() ) {
                            reportid1 = (String) ite.next();
                        }
                        if(reportid1 != null) {
                            String name = null;
                            if(jobj.getString("name").indexOf(".") > -1) {
                                String[] tablecolumn = jobj.getString("name").split("\\.");
                                    name = tablecolumn[0] + PropsValues.REPORT_HARDCODE_STR+tablecolumn[1].toLowerCase();
                            } else {
                                if(jobj.getString("name").indexOf(PropsValues.REPORT_HARDCODE_STR)==-1)
                                    name = tableName + PropsValues.REPORT_HARDCODE_STR+jobj.getString("name").toLowerCase();
                            }
                            mb_reportlist report1 = (mb_reportlist) get(mb_reportlist.class, reportid1);
                            SELECT_QUERY = "select mb_gridconfig.combogridconfig from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig " +
                                    "where mb_gridconfig.reportid = ? and mb_gridconfig.name = ?";
                            list = find(SELECT_QUERY, new Object[]{report1, name});
                            ite = list.iterator();
                            if( ite.hasNext() ) {
                                combogridconfig = (String) ite.next();
                            }
                        }
                    } else if(!jobj.getString("combogridconfig").equals("-1")) {
                        combogridconfig = jobj.getString("combogridconfig");
                    }

                        gridConf.setCombogridconfig(combogridconfig);
                        gridConf.setColumnindex(k);
                        gridConf.setReportid(report);
                        save(gridConf);
                        String strid=gridConf.getId();
                        confCnt++;
                        if(!StringUtil.isNullOrEmpty(jobj.getString("reftable")) && !jobj.getString("reftable").equals(tableName)) {
                            String fkKeyName = jobj.getString("reftable")+"."+(getPrimaryColName(jobj.getString("reftable")));
                            if(fkKeyName.equals(jobj.getString("name"))) {
                                hashSet.add(fkKeyName);
                                finalHashSet.remove(fkKeyName);
                            } else if(!hashSet.contains(fkKeyName)){
                                finalHashSet.add(fkKeyName);
                            }
                        }
                }
            }

            if(finalHashSet.size()>0) {
                Iterator itr = finalHashSet.iterator();
                while(itr.hasNext()) {
                    //Insert id fields of reference tables
                    com.krawler.esp.hibernate.impl.mb_gridconfig gridConf = new com.krawler.esp.hibernate.impl.mb_gridconfig();
                    String tablecolumn = itr.next().toString();
                    tablecolumn = tablecolumn.replace(".", PropsValues.REPORT_HARDCODE_STR);
                    gridConf.setName(tablecolumn);
                    gridConf.setDisplayfield(tablecolumn);
                    gridConf.setReftable(tablecolumn.split(PropsValues.REPORT_HARDCODE_STR)[0]);
                    gridConf.setXtype("None");
                    gridConf.setHidden(true);
                    renderer render =(renderer) get(renderer.class, "0");
                    gridConf.setRenderer(render);
                    gridConf.setColumnindex(confCnt++);
                    gridConf.setReportid(report);
                    gridConf.setCombogridconfig("-1");
                    //gridConf.setFilter("");
                    gridConf.setCountflag(false);
                    save(gridConf);
                }
//            String actionType = "Add Report Grid Config";
//            String details = "Grid Config added for Report "+report.getReportname();
//            long actionId = AuditTrialHandler.getActionId(session, actionType);
//            AuditTrialHandler.insertAuditLog(session, actionId, details, request);
            }

            if(createTable) {
                int cnt = 0;
                //Insert id field of new table
                com.krawler.esp.hibernate.impl.mb_gridconfig gridConf = new com.krawler.esp.hibernate.impl.mb_gridconfig();
                gridConf.setName(tableName+PropsValues.REPORT_HARDCODE_STR+"id");
                gridConf.setDisplayfield("id");
                gridConf.setReftable(tableName);
                gridConf.setXtype("None");
                gridConf.setHidden(true);
                renderer render =(renderer) get(renderer.class, "0");
                gridConf.setRenderer(render);
                gridConf.setColumnindex(confCnt++);
                gridConf.setReportid(report);
                gridConf.setCombogridconfig("-1");
    //            gridConf.setFilter("");
                gridConf.setCountflag(false);
                save(gridConf);

                // save report table name
                report.setTablename(tableName);
                save(report);

                ArrayList<Hashtable<String, Object>> aList = new ArrayList<Hashtable<String, Object>>();

                Object[] objArrField = new Object[] {"name","type","primaryid","default"};
                 Object[] objArr = new Object[] {"id","String","true",""};
                moduleBuilderGenerateTable.makeEntryToArrayList(cnt,aList,objArr,objArrField);

                objArrField = new Object[] {"name","type","default"};
                objArr = new Object[] {"createdby","String",""};
                moduleBuilderGenerateTable.makeEntryToArrayList(cnt,aList,objArr,objArrField);

                objArr = new Object[] {"createddate","Date",""};
                moduleBuilderGenerateTable.makeEntryToArrayList(cnt,aList,objArr,objArrField);

                objArr = new Object[] {"modifieddate","Date",""};
                moduleBuilderGenerateTable.makeEntryToArrayList(cnt,aList,objArr,objArrField);

                objArr = new Object[] {"deleteflag","double",""};
                moduleBuilderGenerateTable.makeEntryToArrayList(cnt,aList,objArr,objArrField);

                HashSet<String> hs = new HashSet<String>();
                for (int k = 0; k < jsonArray.length(); k++) {
                    JSONObject obj = jsonArray.getJSONObject(k);

                    if(!StringUtil.isNullOrEmpty(obj.getString("reftable")) && !obj.getString("reftable").equals(tableName)) {
                        if(!Boolean.parseBoolean(obj.getString("countflag"))) {
                            if(hs.add(obj.getString("reftable"))){
                                 Object[] objArrField1 = new Object[] {"name","reftable","type","foreignid","default"};
                                 String fkKeyName = obj.getString("reftable").concat(getPrimaryColName(obj.getString("reftable")));
                                 objArr = new Object[] {fkKeyName,obj.getString("reftable"),"String",true,obj.getString("defaultValue")};
                                 moduleBuilderGenerateTable.makeEntryToArrayList(cnt,aList,objArr,objArrField1);
                            }
                        }
                    } else {
                        if(!obj.getString("name").equals("id")) {
                            String type = "";
                             if(obj.getString("xtype").equals("Checkbox")||obj.getString("xtype").equals("Radio")) {
                                type = "boolean";
                            } else if(obj.getString("xtype").equals("Date")){
                                type = "Date";
                            } else if(obj.getString("xtype").equals("Number(Integer)")){
                                type = "int";
                            } else if(obj.getString("xtype").equals("Number(Float)")){
                                type = "double";
                            } else if(obj.getString("xtype").equals("Combobox")){
                                type = "String";
                            }else {
                                type = "String";
                            }

                            objArr = new Object[] {obj.getString("name").toLowerCase(),type,obj.getString("defaultValue")};
                            moduleBuilderGenerateTable.makeEntryToArrayList(cnt,aList,objArr,objArrField);
                        }
                    }
                }
                hs.clear();
                ServiceBuilder sb = new ServiceBuilder();
//                sb.createServiceXMLFile(aList, tableName);
                sb.createJavaFile(tableName, true);
//                String actionType = "Add Report Grid Config Table";
//                String details = "Grid Cofig Table added for Report "+report.getReportname();
//                long actionId = AuditTrialHandler.getActionId(session, actionType);
//                AuditTrialHandler.insertAuditLog(session, actionId, details, request);
            } else {
                String className = "rb_"+toLZ(report.getReportkey(), 3)+"_"+report.getReportname().replace(" ", "").toLowerCase();
                // save report table name
                //report.setTablename(className);
                //session.save(report);
                //Create only implementation java class for report for which no new table is created.
                ServiceBuilder sb = new ServiceBuilder();
                sb.createImplJavaFile(className, true);
            }

//            if(numDelRec==0) { // if first time store then add permission entry for add/edit/delete action
//                mb_permgrmaster permgrmaster = new mb_permgrmaster();
//                accessRight.addPermGrp(session,permgrmaster,report);
//                com.krawler.esp.hibernate.impl.mb_permmaster permmaster = null;
//                for(int i=2;i<9;i++) {
//                    permmaster = new com.krawler.esp.hibernate.impl.mb_permmaster();
//                    mb_permactions permaction = (mb_permactions) session.load(mb_permactions.class,i);
//                    permmaster.setPermaction(permaction);
//                    permmaster.setPermname(permaction.getName());
//                    permmaster.setDescription(permaction.getName());
//                    permmaster.setPermgrid(permgrmaster);
//                    permmaster.setPermid(accessRight.getMaxPermid(session, permgrmaster.getPermgrid()));
//                    session.save(permmaster);
//                }
//            }

            storeToolbarConf(reportid, tbar, bbar);

            hql = "SELECT mb_gridconfig.columnindex,mb_gridconfig.hidden,mb_gridconfig.reftable,mb_gridconfig.renderer,mb_gridconfig.xtype,mb_gridconfig.displayfield,mb_gridconfig.name " +
                "FROM com.krawler.esp.hibernate.impl.mb_gridconfig AS mb_gridconfig " +
                "WHERE mb_gridconfig.reportid = ?";
            List list = find(hql, new Object[]{report});
            Iterator ite = list.iterator();
            JSONObject r = new JSONObject();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject temp = new JSONObject();
                temp.put("index", row[0]);
                temp.put("hidden", row[1]);
                temp.put("reftable", row[2]);
                temp.put("renderer", row[3]);
                temp.put("xtype", row[4]);
                temp.put("displayfield", row[5]);
                temp.put("name", row[6]);
                r.append("data", temp);
            }
            r.put("success", true);
            r.put("reportId", reportid);
            r.put("tablename", tableName);
            result = r.toString();
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

    public void storeButtonConf(JSONObject temp, mb_reportlist report, String toolbarType) throws ServiceException{
        try{
            String btnText = temp.getString("text");
            com.krawler.esp.hibernate.impl.mb_buttonConf tempButton = new com.krawler.esp.hibernate.impl.mb_buttonConf();
            tempButton.setButtonid(UUID.randomUUID().toString());
            tempButton.setCaption(btnText);
            tempButton.setFunctext(temp.getString("handler"));
            tempButton.setReportid(report);
            tempButton.setButtontype(temp.getString("type"));
            tempButton.setToolbartype(toolbarType);
            save(tempButton);
            if(temp.getString("type").equals("jsp")){
                createJspFileForButton(temp.getString("handler"));
            }

            int permgrid = accessRightDao.getModPermGrp(report.getReportid());
            com.krawler.esp.hibernate.impl.mb_permmaster perm = new com.krawler.esp.hibernate.impl.mb_permmaster();
            perm.setDescription(btnText);
            perm.setPermname(btnText);
            mb_permactions permaction = (mb_permactions) get(mb_permactions.class,1);
            perm.setPermaction(permaction);
            mb_permgrmaster permgrmaster = (mb_permgrmaster) get(mb_permgrmaster.class, permgrid);
            perm.setPermgrid(permgrmaster);
            perm.setPermid(accessRightDao.getMaxPermid(permgrid));
            save(perm);
            com.krawler.esp.hibernate.impl.mb_btnpermmap perMap = new com.krawler.esp.hibernate.impl.mb_btnpermmap();
            perMap.setId(UUID.randomUUID().toString());
            perMap.setButtonid(tempButton);
            perMap.setPermid(perm);
            save(perMap);
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public void storeToolbarConf(String moduleid, String tbar, String bbar) throws ServiceException{
        try{
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, moduleid);
            if(!StringUtil.isNullOrEmpty(bbar)){
                JSONArray buttonConf = new JSONArray(bbar);
                deleteButtonConf(report, "bbar");
                for(int cnt = 0; cnt < buttonConf.length(); cnt++){
                    JSONObject temp = buttonConf.getJSONObject(cnt);
                    storeButtonConf(temp, report, "bbar");
                }
            }
            if(!StringUtil.isNullOrEmpty(tbar)){
                JSONArray buttonConf = new JSONArray(tbar);
                deleteButtonConf(report, "tbar");
                for(int cnt = 0; cnt < buttonConf.length(); cnt++){
                    JSONObject temp = buttonConf.getJSONObject(cnt);
                    storeButtonConf(temp, report, "tbar");
                }
            }
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }
    public void deleteButtonConf(mb_reportlist report, String type) throws ServiceException{
        String hql = "SELECT mb_buttonConf.buttonid FROM " + PropsValues.PACKAGE_PATH + ".mb_buttonConf as mb_buttonConf " +
            "WHERE mb_buttonConf.toolbartype = ? AND mb_buttonConf.reportid = ?";
        try{
            List lst = find(hql, new Object[] { type, report });
            Iterator ite = lst.iterator();
            while(ite.hasNext()){
                String bid = ite.next().toString();
                hql = "SELECT mb_btnpermmap FROM " + PropsValues.PACKAGE_PATH + ".mb_btnpermmap AS mb_btnpermmap " +
                        "WHERE mb_btnpermmap.buttonid.buttonid = ?";
                List temp = find(hql, new Object[]{ bid });
                Iterator tIte = temp.iterator();
                while(tIte.hasNext()){
                    mb_btnpermmap btnpermmap = (mb_btnpermmap)tIte.next();
                    hql = "DELETE FROM " + PropsValues.PACKAGE_PATH + ".mb_btnpermmap AS mb_btnPermMap WHERE mb_btnPermMap = ?";
                    int num = executeUpdate(hql, new Object[] { btnpermmap });
                    hql = "DELETE FROM " + PropsValues.PACKAGE_PATH + ".mb_permmaster AS mb_permMaster WHERE mb_permMaster = ?";
                    num = executeUpdate(hql, new Object[] { btnpermmap.getPermid() });
                }
            }
            hql = "DELETE FROM " + PropsValues.PACKAGE_PATH + ".mb_buttonConf AS mb_buttonconf " +
                "WHERE mb_buttonconf.reportid = ? AND mb_buttonconf.toolbartype = ?" ;
            int num = executeUpdate(hql,new Object[]{ report, type});
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }
    public void createJspFileForButton(String jspName) throws ServiceException{
        try{
            String[] temp = jspName.split("\\?");
            File f = new File(PropsValues.JSP_FILE_PATH + "/" + temp[0]);
            if(!f.exists()){
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                fw.write(PropsValues.JSP_FILE_CONTENT);
                fw.close();
            }
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }
    public String reportConfig(HttpServletRequest request) throws ServiceException {
        String result = "{\"data\":[]}";
        try {
            JSONObject jobj = new JSONObject();
            String reportid = request.getParameter("reportid");
            String reportTableName = getReportTableName(reportid);
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
            String hql = "select mb_gridconfig.name, mb_gridconfig.displayfield, mb_gridconfig.reftable, mb_gridconfig.xtype, renderer.id as renderer1, mb_gridconfig.filter," +
                    " mb_gridconfig.hidden, mb_gridconfig.columnindex, mb_gridconfig.combogridconfig, mb_gridconfig.countflag,mb_gridconfig.id,mb_gridconfig.summaryType,mb_gridconfig.defaultValue " +
                    " from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig inner join mb_gridconfig.renderer as renderer where mb_gridconfig.reportid = ? order by mb_gridconfig.columnindex " ;
            List list = find(hql,new Object[]{report});
            Iterator ite = list.iterator();
            while(ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject jtemp2 = new JSONObject();
                if(reportTableName!=null && reportTableName.length()>0 && row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[0].equals(reportTableName))
                    jtemp2.put("columnname", row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[1]);
                else
                    jtemp2.put("columnname", row[0].toString().replace(PropsValues.REPORT_HARDCODE_STR,"."));
                jtemp2.put("name", row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[1]);
                jtemp2.put("displayfield", row[1]);
                jtemp2.put("reftable", row[2]);
                jtemp2.put("xtype", row[3]);
                jtemp2.put("renderer", row[4]);
                jtemp2.put("hidden", row[6]);
                jtemp2.put("seq", row[7]);
                jtemp2.put("combogridconfig", row[8]);
                jtemp2.put("countflag", row[9]);
                jtemp2.put("id", row[10]);
                jtemp2.put("summaryType", row[11]);
                jtemp2.put("defaultValue", row[12]);
                jobj.append("data", jtemp2);
            }

            JSONObject rObj = new JSONObject();
            if(jobj.has("data")){
                rObj.put("success", true);
                rObj.put("data", jobj.toString());
                rObj.put("buttonConf", getButtonConf(reportid));
                result = rObj.toString();
            } else {
                result = "{\"success\":false,\"data\":[]}";
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        } finally {
        }
        return result;
    }
    public String getButtonConf(String reportid) throws ServiceException{
        String buttonConf = "";
        try{
            JSONObject conf = new JSONObject();
            String hql = "SELECT mb_buttonConf.id, mb_buttonConf.caption, mb_buttonConf.functext, mb_buttonConf.buttontype " +
                "FROM com.krawler.esp.hibernate.impl.mb_buttonConf AS mb_buttonConf " +
                "WHERE mb_buttonConf.reportid.reportid = ? AND mb_buttonConf.toolbartype = 'tbar'";
            List lst = find(hql, new Object[] { reportid });
            Iterator ite = lst.iterator();
            while(ite.hasNext()){
                JSONObject temp = new JSONObject();
                Object[] row = (Object[]) ite.next();
                temp.put("id", row[0].toString());
                temp.put("text", row[1].toString());
                temp.put("functext", row[2].toString());
                String query = "select mb_permmaster.permid, mb_permmaster.permgrid.permgrid from "+PropsValues.PACKAGE_PATH+".mb_btnpermmap" +
                        " as mb_btnpermmap inner join mb_btnpermmap.permid as mb_permmaster inner join mb_btnpermmap.buttonid as mb_buttonConf " +
                        " where mb_buttonConf.buttonid = ?";
                List lst1 = find(query, new Object[] { row[0].toString() });
                Iterator ite1 = lst1.iterator();
                if(ite1.hasNext()){
                    Object[] row1 = (Object[]) ite1.next();
                    temp.put("perm", row1[0].toString());
                    temp.put("permgrid", row1[1].toString());
                }
                temp.put("type", row[3].toString());
                conf.append("tbar", temp);
            }
            hql = "SELECT mb_buttonConf.id, mb_buttonConf.caption, mb_buttonConf.functext, mb_buttonConf.buttontype " +
                "FROM com.krawler.esp.hibernate.impl.mb_buttonConf AS mb_buttonConf " +
                "WHERE mb_buttonConf.reportid.reportid = ? AND mb_buttonConf.toolbartype = 'bbar'";
            lst = find(hql, new Object[] { reportid });
            ite = lst.iterator();
            while(ite.hasNext()){
                JSONObject temp = new JSONObject();
                Object[] row = (Object[]) ite.next();
                temp.put("id", row[0].toString());
                temp.put("text", row[1].toString());
                temp.put("functext", row[2].toString());
                String query = "select mb_permmaster.permid, mb_permmaster.permgrid.permgrid from "+PropsValues.PACKAGE_PATH+".mb_btnpermmap" +
                        " as mb_btnpermmap inner join mb_btnpermmap.permid as mb_permmaster inner join mb_btnpermmap.buttonid as mb_buttonConf " +
                        " where mb_buttonConf.buttonid = ?";
                List lst1 = find(query, new Object[] { row[0].toString() });
                Iterator ite1 = lst1.iterator();
                if(ite1.hasNext()){
                    Object[] row1 = (Object[]) ite1.next();
                    temp.put("perm", row1[0].toString());
                    temp.put("permgrid", row1[1].toString());
                }
                temp.put("type", row[3].toString());
                conf.append("bbar", temp);
            }
            buttonConf = conf.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return buttonConf;
    }

    public String getStdButtonConf(String reportid) throws ServiceException{
        String buttonConf = "";
        try{
            JSONObject ret = new JSONObject();
            String hql = "SELECT mb_buttonConf.id, mb_buttonConf.caption, mb_buttonConf.functext FROM com.krawler.esp.hibernate.impl.mb_buttonConf AS mb_buttonConf" +
                " WHERE mb_buttonConf.reportid.reportid = ?";
            List lst = find(hql, new Object[] { reportid });
            Iterator ite = lst.iterator();
            while(ite.hasNext()){
                JSONObject temp = new JSONObject();
                Object[] row = (Object[]) ite.next();
                temp.put("id", row[0].toString());
                temp.put("text", row[1].toString());
                temp.put("functext", row[2].toString());
                ret.append("conf", temp);
            }
            buttonConf = ret.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return buttonConf;
    }

    public String getReportIdFromTable(String tablename) throws ServiceException{
        String reportId = "";
        String SELECT_QUERY="Select mb_reportlist.reportid from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist"
                      +" where mb_reportlist.tablename = ?";
        try {
            List list = executeQuery(SELECT_QUERY, tablename);
            Iterator ite = list.iterator();
            if( ite.hasNext() ) {
                reportId = ite.next().toString();
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("FormServlet.getReportTableName", e);
        }
        return reportId;
    }
    public String deleteReport(HttpServletRequest request) throws ServiceException {
        String result = "{\"success\":false,\"msg\":\"Error occurred at server\"}";
        try {
            String reportid = request.getParameter("reportid");
            String TableName = getReportTableName(reportid);
            if (!isReferred(TableName, reportid)) {
                mb_reportlist basemodule = (mb_reportlist) get(mb_reportlist.class, reportid);
                basemodule.setDeleteflag(1);
                ServiceBuilder sb = new ServiceBuilder();
                sb.deleteModuleStuf(TableName);

                String actionType = "Delete Report";
//                String reportName = AuditTrialHandler.getReportName(session, reportid);
//                String details = reportName + " Report Deleted";
//                long actionId = AuditTrialHandler.getActionId(session, actionType);
//                AuditTrialHandler.insertAuditLog(session, actionId, details, request);
                result = "{\"success\":true}";
            }else{
                result = "{\"success\":false,\"msg\":\"Some module references this report\"}";
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"msg\":\"Error occurred at server\"}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return result;
    }

    public String loadComboStore(HttpServletRequest request) throws ServiceException {
        String result = "{\"success\":true}";
        JSONObject jobj = new JSONObject();
        try {
            String hql = null;
            List list = null;
            String moduleid = request.getParameter("reportid");

            String selectedColumns = request.getParameter("columnname");
            String filterQuery = "";
            //To do - Name should be fetched from selectedColumns variable.
            String name = request.getParameter("columnname").split(",")[0].split(PropsValues.REPORT_HARDCODE_STR)[1];
            //Make filter query from filter rules
//            ArrayList permArray = AuthHandler.getRealRoleids(request);
//            mb_reportlist basemodule = (mb_reportlist) session.load(mb_reportlist.class, moduleid);
//            for(int i = 0; i < permArray.size(); i++) {
//                int roleid = Integer.parseInt(permArray.get(i).toString());
//                String res = ModuleBuilderController.checkFilterRulesQuery(session, basemodule, roleid, 1, name);
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

            if(request.getParameter("combogridconfig").equals("-1")) {
                String[] colArray = selectedColumns.split(",");
                String subQuery = "";
                for (int cnt = 0; cnt<colArray.length;cnt++) {
                            subQuery += colArray[cnt].replace(PropsValues.REPORT_HARDCODE_STR,".")+",";
                }
                if(subQuery.length()>0) {
                    subQuery = subQuery.substring(0,subQuery.length()-1);
                }
                String tablename = request.getParameter("reftable");
                hql = "select "+ tablename +".id, "+ subQuery+" from "+PropsValues.PACKAGE_PATH+"."+tablename+" as "+ tablename + " where "+ tablename +".deleteflag = 0 "+filterQuery;

                Object[] paramArray = null;
                //Get implementation class object and call before combo load method.
                String reporttablename = getReportTableName(request.getParameter("reportid"));
                if(!StringUtil.isNullOrEmpty(reporttablename)) {
                    Class cl1 = Class.forName(PropsValues.PACKAGE_PATH+".impl_"+reporttablename);
                    java.lang.reflect.Constructor co1 = cl1.getConstructor();
                    Object invoker1 = co1.newInstance();
                    Class  arguments1[] = new Class[] {HibernateTemplate.class, HttpServletRequest.class,Object[].class,String.class,String.class,String[].class};

                    java.lang.reflect.Method objMethod1 = cl1.getMethod("beforeComboLoad",arguments1);
                    Object[] obj1 = new Object[]{getHibernateTemplate(), request, paramArray, filterQuery, subQuery, colArray};
                    Object result11 = objMethod1.invoke(invoker1, obj1);

                    if(!StringUtil.isNullOrEmpty(result11.toString())) {
                        hql = result11.toString();
                    }
                }

                list =  find(hql, paramArray);
                Iterator ite = list.iterator();
                while(ite.hasNext() ) {
                    Object[] row = (Object[]) ite.next();
                    JSONObject jtemp2 = new JSONObject();
                    jtemp2.put("id", row[0]);
                    colArray = selectedColumns.split(",");
                    for (int cnt = 0; cnt<colArray.length;cnt++) {
                        jtemp2.put(colArray[cnt], row[cnt+1]);
                            }
                    jobj.append("data", jtemp2);
                }
            } else {
                hql = "select mb_configmasterdata.masterid as id, mb_configmasterdata.masterdata as name " +
                        "from "+PropsValues.PACKAGE_PATH+".mb_configmasterdata as mb_configmasterdata " +
                        "where mb_configmasterdata.configid = ? "+filterQuery+" order by mb_configmasterdata.masterdata ";
                list = find(hql, new Object[]{request.getParameter("combogridconfig")});
                Iterator ite = list.iterator();
                while(ite.hasNext() ) {
                    Object[] row = (Object[]) ite.next();
                    JSONObject jtemp2 = new JSONObject();
                    jtemp2.put("id", row[0]);
                    jtemp2.put(selectedColumns, row[1]);
                    jobj.append("data", jtemp2);
                }
            }
            result = jobj.toString();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return result;
    }

    public String getComments(HttpServletRequest request) throws ServiceException {
        String result = "{\"data\":[]}";
        try {
            JSONObject jobj = new JSONObject();
            String recordId = request.getParameter("recordId");
            if (recordId != null && !recordId.equals("")) {

                int start = Integer.parseInt(request.getParameter("start"));
                int limit = Integer.parseInt(request.getParameter("limit"));
                String quickSearchFilter="";
                if (request.getParameter("ss") != null){
                    quickSearchFilter="and comments.comment like '%"+request.getParameter("ss")+"%'";
                }

                String searchFilter="";
                if (request.getParameter("filterJson") != null) {

                    JSONArray filterJsonObj = new JSONArray(request.getParameter("filterJson"));

                    JSONObject ObjJSONObject = null;
                    String substr = "";
                    for (int i = 0; i < filterJsonObj.length(); i++) {
                        ObjJSONObject = (JSONObject) filterJsonObj.get(i);
                        if (ObjJSONObject.getString("xtype").equals("datefield")) {
                            String[] splitString=ObjJSONObject.getString("searchText").split(",");
                            String fromDate=splitString[0];
                            String toDate=splitString[1];
                            substr =" >= '" + fromDate + " 00:00:00" + "'"+" and comments." + ObjJSONObject.getString("column")+ " <= '" + toDate + " 00:00:00" + "'";
                        } else if (ObjJSONObject.getString("xtype").equals("numberfield")) {
                            substr = " = " + ObjJSONObject.getString("searchText");
                        } else if (ObjJSONObject.getString("xtype").equals("radio") || ObjJSONObject.getString("xtype").equals("checkbox")) {
                            substr = " = " + Boolean.parseBoolean(ObjJSONObject.getString("searchText"));
                        }else if (ObjJSONObject.getString("xtype").equals("userscombo")) {
                            substr = " = '" + ObjJSONObject.getString("searchText") + "'";
                        } else {
                            substr = " like '%" + ObjJSONObject.getString("searchText") + "%'";
                        }
                        searchFilter += " and comments." + ObjJSONObject.getString("column") + substr;
                    }
                }

                String query="from com.krawler.esp.hibernate.impl.comments as comments where comments.recordid = ? "+quickSearchFilter+searchFilter;
                String commentQuery = "Select comments.id,comments.comment,comments.addedby,comments.createddate "+query;

                List li = find("select count(*) "+query,new Object[]{recordId});
                long count = 0;
                Iterator ite1 = li.iterator();
                if (ite1.hasNext()) {
                    count = Long.parseLong(ite1.next().toString());
                }
                li = executeQueryPaging(commentQuery,new Object[]{recordId}, new Integer[]{start,limit});
                ite1 = li.iterator();

                JSONObject jtemp2 = null;
                while (ite1.hasNext()) {
                    Object[] row = (Object[]) ite1.next();
                    jtemp2 = new JSONObject();
                    jtemp2.put("id", row[0].toString());
                    jtemp2.put("comment", row[1].toString());
                    jtemp2.put("recordId", recordId);
                    User users = (User) get(User.class, row[2].toString());
                    jtemp2.put("addedBy", users.getFirstName() + " " + users.getLastName());
                    jtemp2.put("Time", row[3].toString());
                    jobj.append("data", jtemp2);
                }

                if (jobj.has("data")) {
                    jobj.put("count", count);
                    result = jobj.toString();
                } else {
                    result = "{data:[],count : 0}";
                }

            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        } finally {
        }
        return result;
    }

    public String insertComment(HttpServletRequest request) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            String recordid = request.getParameter("recordid");
            String reftable = "";
            reftable = getReportTableName(request.getParameter("moduleid"));

            String commentstr =  request.getParameter("comment");
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(ModuleBuilderController.USER_DATEPREF);
            java.sql.Timestamp timestamp1 = Timestamp.valueOf(sdf.format(new java.util.Date()));

            comments comment = new comments();
            comment.setAddedby(sessionHandlerDao.getUserid());
            comment.setComment(commentstr);
            comment.setCreateddate(timestamp1);
            comment.setDeleteflag(0);
            comment.setRecordid(recordid);
            comment.setReftable(reftable);
            save(comment);
            result = "{\"success\":true, 'msg':'Comment added successfully.'}";
//            String actionType = "Delete Report";
//            String reportName = AuditTrialHandler.getReportName(session, reportid);
//            String details = reportName + " Report Deleted";
//            long actionId = AuditTrialHandler.getActionId(session, actionType);
//            AuditTrialHandler.insertAuditLog(session, actionId, details, request);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return result;
    }

    public String deleteComment(HttpServletRequest request) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            String commentid = request.getParameter("commentid");
            String hql = "delete from com.krawler.esp.hibernate.impl.comments as comments where comments.id = ?  ";
            int num = executeUpdate(hql, new Object[]{commentid});
            if (num == 0){
                result = "{\"success\":false}";
            }else{
                result = "{\"success\":true, 'msg':'Comment added successfully.'}";
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return result;
    }

    public String getUsers(HttpServletRequest request) throws ServiceException {
       com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
       String result = null;
        Iterator ite=null;
       try{


            String sql="select users.userid,users.fname,users.lname from com.krawler.esp.hibernate.impl.users as users ";
            List list = executeQuery(sql);
            ite = list.iterator();
            while(ite.hasNext()){
                Object[] row = (Object[]) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("id", row[0]);
                jtemp.put("name", row[1].toString()+" "+row[2].toString());
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

    public String createRenderer(HttpServletRequest request) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            renderer renderer = new renderer();
            renderer.setId(UUID.randomUUID().toString());
            renderer.setName(request.getParameter("name"));
            renderer.setRendererValue(request.getParameter("value"));
            renderer.setIsstatic(false);
            save(renderer);
            result = "{\"success\":true,\"id\":\""+renderer.getId()+"\"}";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return result;
    }

  public String editRenderer(HttpServletRequest request) throws ServiceException {
         String result = "{\"success\":false}";
        try {
            renderer renderer = (renderer) get(renderer.class, request.getParameter("id"));

            renderer.setName(request.getParameter("name"));
            renderer.setRendererValue(request.getParameter("value"));
            renderer.setIsstatic(false);
            save(renderer);
            result = "{\"success\":true,\"id\":\""+renderer.getId()+"\"}";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("FormServlet.getAllForms", e);
        }
        return result;
    }

    public String getRendererFunctions(HttpServletRequest request) throws ServiceException {
        String result = "{\"data\":[]}";
        try {
            JSONObject jobj = new JSONObject();
            String hql = "select renderer.id,renderer.name,renderer.rendererValue,renderer.isstatic from com.krawler.esp.hibernate.impl.renderer as renderer";
            List list = executeQuery(hql);
            Iterator ite = list.iterator();
            JSONObject jtemp2=null;
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                jtemp2 = new JSONObject();
                jtemp2.put("id", row[0]);
                jtemp2.put("name", row[1]);
                jtemp2.put("value", row[2]);
                jtemp2.put("isstatic", row[3]);
                jobj.append("data", jtemp2);
            }

            jtemp2 = new JSONObject();
            jtemp2.put("id", "0");
            jtemp2.put("name", "Create New renderer");
            jtemp2.put("value", "");
            jtemp2.put("isstatic", "false");
            jobj.append("data", jtemp2);

            JSONObject rObj = new JSONObject();
            if (jobj.has("data")) {
                rObj.put("success", true);
                rObj.put("data", jobj.toString());
                result = rObj.toString();
            } else {
                result = "{\"success\":false,\"data\":[]}";
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        }
        return result;
    }

    public String getModulePermission(String moduleid) throws ServiceException {
        String result = "{\"data\":[]}";
        JSONObject jobj = new JSONObject();
        try {
//            String hql = "select mb_permmaster.permid, mb_permgrmaster.permgrid, mb_permmaster.permaction  " +
//                    " from com.krawler.esp.hibernate.impl.mb_permmaster as mb_permmaster inner join mb_permmaster.permgrid as mb_permgrmaster " +
//                    " where mb_permgrmaster.reportid = ? and mb_permgrmaster.taskid = ? ";
            String hql = "select mb_permmaster.permid, mb_permgrmaster.permgrid, mb_permmaster.permaction  " +
                    " from com.krawler.esp.hibernate.impl.mb_permmaster as mb_permmaster inner join mb_permmaster.permgrid as mb_permgrmaster " +
                    " where mb_permgrmaster.reportid = ?";
            mb_reportlist reportObj = (mb_reportlist) get(mb_reportlist.class,moduleid);
//            pm_taskmaster taskObj = (pm_taskmaster) get(pm_taskmaster.class, taskid);
            List list = find(hql,new Object[]{reportObj});
            Iterator ite = list.iterator();
            JSONObject jtemp2=null;
            JSONObject recordPerm = new JSONObject();
            JSONObject btnPerm = new JSONObject();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                jtemp2 = new JSONObject();
                jtemp2.put("perm", row[0]);
                jtemp2.put("permgrid", row[1]);
                mb_permactions permaction = (mb_permactions) row[2];
                if(permaction.getAction()!=0) {
                    recordPerm.put(String.valueOf(permaction.getAction()), jtemp2.toString());
                } else {
                    String query = "select buttonid from "+PropsValues.PACKAGE_PATH+".mb_btnpermmap as mb_btnpermmap where mb_btnpermmap.permid.permid = ?";
                    List list1 = find(query,new Object[]{row[0]});
                    Iterator ite1 = list1.iterator();
                    if(ite1.hasNext()) {
                        btnPerm.put(ite1.next().toString(), jtemp2.toString());
                    }
                }
            }
            jobj.put("recordperm", recordPerm);
            jobj.put("btnperm", btnPerm);
            result = jobj.toString();
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        }
        return result;
    }

    public JSONObject getModulePermission(String moduleid, int actions) throws ServiceException {
        String result = "{\"data\":[]}";
        JSONObject jobj = new JSONObject();
        try {
//            String hql = "select mb_permmaster.permid, mb_permgrmaster.permgrid, mb_permmaster.permaction  " +
//                    " from com.krawler.esp.hibernate.impl.mb_permmaster as mb_permmaster inner join mb_permmaster.permgrid as mb_permgrmaster " +
//                    " where mb_permgrmaster.reportid = ? and mb_permgrmaster.taskid = ? ";
            String hql = "select mb_permmaster.permid, mb_permgrmaster.permgrid, mb_permmaster.permaction  " +
                    " from com.krawler.esp.hibernate.impl.mb_permmaster as mb_permmaster inner join mb_permmaster.permgrid as mb_permgrmaster " +
                    " where mb_permgrmaster.reportid = ? and mb_permmaster.permaction.action = ? ";
            mb_reportlist reportObj = (mb_reportlist) get(mb_reportlist.class, moduleid);
//            pm_taskmaster taskObj = (pm_taskmaster) get(pm_taskmaster.class, taskid);
            List list = find(hql,new Object[]{reportObj, actions});
            Iterator ite = list.iterator();
            JSONObject jtemp2=null;
            JSONObject recordPerm = new JSONObject();
            JSONObject btnPerm = new JSONObject();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                jtemp2 = new JSONObject();
                jtemp2.put("perm", row[0]);
                jtemp2.put("permgrid", row[1]);
                mb_permactions permaction = (mb_permactions) row[2];
                if(permaction.getAction()!=0) {
                    recordPerm.put(String.valueOf(permaction.getAction()), jtemp2.toString());
                } else {
                    String query = "select buttonid from "+PropsValues.PACKAGE_PATH+".mb_btnpermmap as mb_btnpermmap where mb_btnpermmap.permid.permid = ?";
                    List list1 = find(query,new Object[]{row[0]});
                    Iterator ite1 = list1.iterator();
                    if(ite1.hasNext()) {
                        btnPerm.put(ite1.next().toString(), jtemp2.toString());
                    }
                }
            }
            jobj.put("recordperm", recordPerm);
            jobj.put("btnperm", btnPerm);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        }
        return jobj;
    }

    public String getModuleDisplayFields(HttpServletRequest request) throws ServiceException {
        String result = "{\"data\":[]}";
        try {
            JSONObject jobj = new JSONObject();
            String reportid = request.getParameter("reportid");
            String reportTableName = getReportTableName(reportid);
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
            String hql = "select mb_gridconfig.name, mb_gridconfig.displayfield, mb_gridconfig.reftable, mb_gridconfig.xtype, " +
                    " mb_gridconfig.hidden, mb_gridconfig.columnindex, mb_gridconfig.combogridconfig, mb_gridconfig.countflag , mb_gridconfig.id " +
                    " from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig" +
                    " where mb_gridconfig.reportid = ? and mb_gridconfig.xtype != ? and " +
                    " mb_gridconfig.xtype in ('numberfield','combo','Number(Integer)','Number(Float)','Combobox')" +
                    " order by mb_gridconfig.columnindex " ;
            List list = find(hql,new Object[]{report,"default"});
            Iterator ite = list.iterator();
            while(ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject jtemp2 = new JSONObject();
                jtemp2.put("columnname", row[0].toString());
                jtemp2.put("name", row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[1]);
                jtemp2.put("displayfield", row[1]);
                jtemp2.put("reftable", row[2]);
                jtemp2.put("xtype", row[3]);
                jtemp2.put("hidden", row[4]);
                jtemp2.put("seq", row[5]);
                jtemp2.put("combogridconfig", row[6]);
                jtemp2.put("countflag", row[7]);
                jtemp2.put("id", row[8]);
                jobj.append("data", jtemp2);
            }

            JSONObject rObj = new JSONObject();
            if(jobj.has("data")){
                result = jobj.toString();
            } else {
                result = "{\"data\":[]}";
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        }
        return result;
    }

    public String getModuleFieldsForFilter(HttpServletRequest request) throws ServiceException {
        String result = "{\"data\":[]}";
        try {
            JSONObject jobj = new JSONObject();
            String reportid = request.getParameter("reportid");
            String filterQuery = "('Text','textfield','textarea','numberfield','combo','Number(Integer)','Number(Float)','Combobox')";
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportid);
            if(report.getType() == 1) {//For report
                String hql1 = "Select distinct(mb_gridconfig.reftable) from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig " +
                        " where mb_gridconfig.reportid = ? ";
                List ls = find(hql1, new Object[] {report});
                Iterator ite1 = ls.iterator();
                while(ite1.hasNext()) {
                    String refTable = (String) ite1.next();
                    if(!StringUtil.isNullOrEmpty(refTable)) {
                        String reportid1 = getReportIdFromTable(refTable);
                        report = (mb_reportlist) get(mb_reportlist.class, reportid1);
                        if(!reportid.equals(reportid1)) {
                            filterQuery = "('Text','textfield','textarea','numberfield','Number(Integer)','Number(Float)')";
                        } else {
                            filterQuery = "('Text','textfield','textarea','numberfield','combo','Number(Integer)','Number(Float)','Combobox')";
                        }

                        String hql = "select mb_gridconfig.name, mb_gridconfig.displayfield, mb_gridconfig.reftable, mb_gridconfig.xtype, " +
                                " mb_gridconfig.hidden, mb_gridconfig.columnindex, mb_gridconfig.combogridconfig, mb_gridconfig.countflag , mb_gridconfig.id " +
                                " from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig" +
                                " where mb_gridconfig.reportid = ? and mb_gridconfig.xtype != ? and " +
                                " mb_gridconfig.xtype in " + filterQuery +
                                " order by mb_gridconfig.columnindex " ;
                        List list = find(hql,new Object[]{report,"default"});
                        Iterator ite = list.iterator();
                        while(ite.hasNext() ) {
                            Object[] row = (Object[]) ite.next();
                            JSONObject jtemp2 = new JSONObject();
                            jtemp2.put("columnname", row[0].toString());
                            jtemp2.put("name", row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[1]);
                            if(row[2] != null && !StringUtil.isNullOrEmpty(row[2].toString()))
                                jtemp2.put("displayfield", row[1] + " ["+report.getReportname()+"]");
                            else
                                jtemp2.put("displayfield", row[1]);
                            jtemp2.put("reftable", row[2]);
                            jtemp2.put("xtype", row[3]);
                            jtemp2.put("hidden", row[4]);
                            jtemp2.put("seq", row[5]);
                            jtemp2.put("combogridconfig", row[6]);
                            jtemp2.put("countflag", row[7]);
                            jtemp2.put("id", row[8]);
                            jobj.append("data", jtemp2);
                        }
                    }
               }
            } else {//For modules
                String hql = "select mb_gridconfig.name, mb_gridconfig.displayfield, mb_gridconfig.reftable, mb_gridconfig.xtype, " +
                        " mb_gridconfig.hidden, mb_gridconfig.columnindex, mb_gridconfig.combogridconfig, mb_gridconfig.countflag , mb_gridconfig.id " +
                        " from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig" +
                        " where mb_gridconfig.reportid = ? and mb_gridconfig.xtype != ? and " +
                        " mb_gridconfig.xtype in " + filterQuery +
                        " order by mb_gridconfig.columnindex " ;
                List list = find(hql,new Object[]{report,"default"});
                Iterator ite = list.iterator();
                while(ite.hasNext() ) {
                    Object[] row = (Object[]) ite.next();
                    JSONObject jtemp2 = new JSONObject();
                    jtemp2.put("columnname", row[0].toString());
                    jtemp2.put("name", row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[1]);
                    if(row[2] != null && !StringUtil.isNullOrEmpty(row[2].toString()))
                        jtemp2.put("displayfield", row[1] + "["+row[2]+"]");
                    else
                        jtemp2.put("displayfield", row[1]);
                    jtemp2.put("reftable", row[2]);
                    jtemp2.put("xtype", row[3]);
                    jtemp2.put("hidden", row[4]);
                    jtemp2.put("seq", row[5]);
                    jtemp2.put("combogridconfig", row[6]);
                    jtemp2.put("countflag", row[7]);
                    jtemp2.put("id", row[8]);
                    jobj.append("data", jtemp2);
                }
            }

            if(jobj.has("data")){
                result = jobj.toString();
            } else {
                result = "{\"data\":[]}";
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.reportConfig", e);
        }
        return result;
    }
    public boolean isReferred(String tableName, String reportid) throws ServiceException {
        boolean isReferred = false;
        mb_reportlist basemodule = (mb_reportlist) get(mb_reportlist.class, reportid);
        String hql = "Select mb_gridconfig.reportid as reportid from " + PropsValues.PACKAGE_PATH + ".mb_gridconfig as mb_gridconfig " +
                "where mb_gridconfig.reportid != ? and mb_gridconfig.reftable = ?";
        List ls = find(hql, new Object[] {basemodule, tableName});
        Iterator ite = ls.iterator();
        while(ite.hasNext()) {
            mb_reportlist reportObj = (mb_reportlist) ite.next();
            if(reportObj.getDeleteflag() == 0) {
                isReferred = true;
                break;
            }
        }
        return isReferred;
    }

     public String getColumns(HttpServletRequest request)throws ServiceException{
        String result = "";
        String query="select mb_reportlist.reportid,mb_reportlist.reportname from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist where mb_reportlist.tablename=?";
        String refTable=request.getParameter("refTable");
        List ls = find(query,new Object[]{refTable});
        Iterator ite = ls.iterator();
        String reportId="";
         if(ite.hasNext()){
             Object[] row = (Object[]) ite.next();
             reportId=row[0].toString();
         }

        query = "select mb_gridconfig.name, mb_gridconfig.combogridconfig, mb_gridconfig.reftable, mb_gridconfig.xtype , mb_gridconfig.displayfield " +
                "from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig  where mb_gridconfig.reportid = ? and mb_gridconfig.xtype !='default'";
        JSONObject res = new JSONObject();
        try{
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class,reportId);
            ls = find(query,new Object[]{report});
            ite = ls.iterator();
            while(ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject temp = new JSONObject();
                temp.put("name",row[0]);
                temp.put("refflag",row[1]);
                temp.put("reftable",row[2]);
                temp.put("configtype",row[3]);
                temp.put("displayfield",row[4]);
                temp.put("reportid",reportId);
                res.append("data", temp);
            }
            result = res.toString();
        } catch (JSONException ex){
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("reportbuilder.getColumns", ex);
        }
        return result;
    }

     public String createComboFilterConfig(HttpServletRequest request) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            String xtype=request.getParameter("xtype");
            String refcol="";
            String fieldName=request.getParameter("fieldname");
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, request.getParameter("refmoduleid"));
            if (xtype.equals("Combobox") || xtype.equals("combo")){
                refcol=fieldName.split(PropsValues.REPORT_HARDCODE_STR)[1];
                if(report.getType() == 0){
                    refcol = refcol.substring(0, refcol.length()-2);
                }
            }
            mb_comboFilterConfig mb_comboFilterConfig = new mb_comboFilterConfig();
            mb_comboFilterConfig.setFieldname(fieldName);
            mb_comboFilterConfig.setDisplayfield(request.getParameter("displayfield"));
            mb_gridconfig mb_gridconfig = (mb_gridconfig) get(mb_gridconfig.class, request.getParameter("gridconfigid"));
            mb_comboFilterConfig.setGridconfigid( mb_gridconfig);
            mb_comboFilterConfig.setRefcol(refcol);
            mb_comboFilterConfig.setRefmoduleid(report);
            mb_comboFilterConfig.setXtype(xtype);
            mb_comboFilterConfig.setReftable(request.getParameter("reftable"));
            save(mb_comboFilterConfig);
            result = "{\"success\":true,\"id\":\""+mb_comboFilterConfig.getId()+"\"}";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.createComboFilter", e);
        }
        return result;
    }

     public String getComboFiltersConfig(HttpServletRequest request) throws ServiceException {
        String result = "{\"data\":[]}";
        try {
            JSONObject jobj = new JSONObject();
            String hql = "select mb_comboFilterConfig.id,mb_comboFilterConfig.fieldname,mb_comboFilterConfig.xtype,mb_comboFilterConfig.reftable,mb_comboFilterConfig.refcol,mb_comboFilterConfig.gridconfigid.id,mb_comboFilterConfig.refmoduleid.id,mb_comboFilterConfig.displayfield from com.krawler.esp.hibernate.impl.mb_comboFilterConfig as mb_comboFilterConfig where mb_comboFilterConfig.gridconfigid=?";
            mb_gridconfig mb_gridconfig = (mb_gridconfig) get(mb_gridconfig.class, request.getParameter("gridconfigid"));
            List list = find(hql,new Object[]{mb_gridconfig});
            int count=list.size();
            Iterator ite = list.iterator();
            JSONObject jtemp2=null;

            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                jtemp2 = new JSONObject();
                jtemp2.put("id", row[0]);
                jtemp2.put("fieldname", row[1]);
                jtemp2.put("xtype", row[2]);
                jtemp2.put("reftable", row[3]);
                jtemp2.put("reftable", row[4]);
                jtemp2.put("gridconfigid", row[5]);
                jtemp2.put("reportid", row[6]);
                jtemp2.put("displayfield", row[7]);
                jobj.append("data", jtemp2);
            }

            if (jobj.has("data")) {
                jobj.put("count", count);
                result = jobj.toString();
            } else {
                result = "{data:[],count : 0}";
            }

        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.getComboFilters", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.getComboFilters", e);
        }  finally {
        }
        return result;
    }

   public String deleteComboFilterConfig(HttpServletRequest request) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            String comboFilterId = request.getParameter("comboFilterId");
            if (!StringUtil.isNullOrEmpty(comboFilterId)) {
                String hql = "delete from com.krawler.esp.hibernate.impl.mb_comboFilterConfig as mb_comboFilterConfig where mb_comboFilterConfig.id = ?  ";
                int num = executeUpdate(hql, new Object[]{comboFilterId});
                if (num == 0) {
                    result = "{\"success\":false,\"msg\":\"Error occured while deleting filter\"}";
                } else {
                    result = "{\"success\":true}";
                }
            }else{
                result = "{\"success\":false,\"msg\":\"id for filter not found\"}";
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
        }
        return result;
    }
   public String getTables() throws ServiceException {
        String ret = "";
        try{

            String query="select mb_reportlist.reportid,mb_reportlist.reportname,mb_reportlist.tablename from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist where mb_reportlist.deleteflag=0 and mb_reportlist.tablename!=null ";
            List list = executeQuery(query);
             Iterator itr = list.iterator();
             JSONObject rObj = new JSONObject();
             while(itr.hasNext()){
                Object[] row = (Object[]) itr.next();
                JSONObject t = new JSONObject();
                t.put("name", row[2]);
                t.put("column", getTableColumn(row[0].toString()));
                t.put("displayname", row[1]);
                rObj.append("data", t);
             }
             ret = rObj.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            ret = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.getTables", e);
        }
        return ret;
}
    public String getTableColumn(String reportId) throws ServiceException {
        String ret = "";
        //JSONObject rObj = new JSONObject();
        //Iterator cols = table.getColumnIterator();
//             while(cols.hasNext()){
//                JSONObject temp = new JSONObject();
//                Column col = (Column)cols.next();
//                temp.put("column", col.getName());
//                temp.put("type", col.getSqlType());
//                rObj.append("data", temp);
//            }
        //ret = rObj.toString();

        String query = "select mb_gridconfig.name, mb_gridconfig.combogridconfig, mb_gridconfig.reftable, mb_gridconfig.xtype , mb_gridconfig.displayfield " +
                "from com.krawler.esp.hibernate.impl.mb_gridconfig as mb_gridconfig  where mb_gridconfig.reportid = ? and mb_gridconfig.xtype !='default'";
        JSONObject rObj = new JSONObject();
        try {
            mb_reportlist report = (mb_reportlist) get(mb_reportlist.class, reportId);
            List ls = find(query, new Object[]{report});
            Iterator cols = ls.iterator();
            String reftable="";
            String columnName="";
            String displayName="";
            while (cols.hasNext()) {
                Object[] row = (Object[]) cols.next();
                columnName=row[0].toString().split(PropsValues.REPORT_HARDCODE_STR)[1];
                displayName=row[4].toString();
                boolean isTableColumn=true;
                if (report.getType() == 1){
                    reftable=row[2].toString();
                    if (!reftable.equals(report.getTablename()) && !reftable.equals("")){
                        if (columnName.equals("id")){
                            columnName=reftable+columnName;
                            displayName=columnName;
                        }else{
                            isTableColumn=false;
                        }
                    }
                }
                if (isTableColumn) {
                    JSONObject temp = new JSONObject();
                    temp.put("column", columnName);
                    temp.put("type", row[3]);
                    temp.put("displayname", displayName);
                    rObj.append("data", temp);
                }
            }
            ret = rObj.toString();
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            ret = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.getTableColumn", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            ret = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.getTableColumn", e);
        }
        return ret;
    }
    public String toLZ(int i, int len) {
        // converts integer to left-zero padded string, len chars long.
        String s = Integer.toString(i);
        String retNum = s;
        if (s.length() > len)
            retNum =  s.substring(0, len);
        else if (s.length() < len) // pad on left with zeros
            retNum =  "000000000000000000000000000".substring(0, len - s.length())
                    + s;
//        return "MB"+retNum;
        return retNum;
    }

    public String checktabperms(int permgrid,int perm,HttpServletRequest request){
            String result = "";
          try{
            String roleperms = getMBPermissions(request);
            JSONArray jarr = new JSONArray(roleperms);
            int size = jarr.getJSONObject(0).getJSONArray("roleset").length();
            for(int ctr=0;ctr<size;ctr++){
                if(Integer.parseInt(jarr.getJSONObject(0).getJSONArray("roleset").getJSONObject(ctr).getString("permgrid"))==permgrid){

                    int permedit = Integer.parseInt(jarr.getJSONObject(0).getJSONArray("roleset").getJSONObject(ctr).getString("permvaledit"));
                    int permview = Integer.parseInt(jarr.getJSONObject(0).getJSONArray("roleset").getJSONObject(ctr).getString("permvalview"));
                    if((permview & (int)Math.pow(2,perm))>0){
                        if((permedit & (int)Math.pow(2,perm))>0){
                            result = "edit";
                        }else{
                            result = "view";
                        }
                    }else{
                        if((permedit & (int)Math.pow(2,perm))>0){
                            result = "edit";
                        }else{
                            result = "false";
                        }
                    }
                }
            }
          }catch(Exception ex){
            logger.warn(ex.getMessage(), ex);
          }
          return result;
    }

    public String getMBPermissions(HttpServletRequest request)
            throws SessionExpiredException {
            String mb_perms = request.getSession().getAttribute("roleperms").toString();
        return mb_perms;
    }
}
