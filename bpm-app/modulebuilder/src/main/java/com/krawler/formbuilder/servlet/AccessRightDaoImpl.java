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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.dao.BaseDAO;
import java.util.List;
import java.util.Iterator;
import com.krawler.esp.hibernate.impl.*;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;

import com.krawler.esp.utils.PropsValues;
import java.util.ArrayList;

/**
 *
 * @author sagar
 */
public class AccessRightDaoImpl extends BaseDAO implements AccessRightDao {

    public String fetchrolegrdata(HttpServletRequest request)throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        try{
            String hql = "from com.krawler.esp.hibernate.impl.mb_rolegrmaster as mb_rolegrmaster ";
            List ls = executeQuery(hql);
            Iterator ite = ls.iterator();
            while(ite.hasNext()){
                mb_rolegrmaster row = (mb_rolegrmaster) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("groupid", row.getGroupid());
                jtemp.put("groupname", row.getGroupname());
                jtemp.put("description", row.getDescription());
                jtemp.put("del", "''");
                jtemp.put("copy", "''");
                jobj.append("data", jtemp);

            }
            if(jobj.length() > 0) {
                result = jobj.toString();
            } else {
                result = "{'data':[]}";
            }
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("accessRight.fetchrolegrdata", e);
        }
        return result;
    }
    public String fetchroledata(HttpServletRequest request)throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        try{
            String hql = "from com.krawler.esp.hibernate.impl.mb_rolemaster as mb_rolemaster ";
            List ls = executeQuery(hql);
            Iterator ite = ls.iterator();
            while(ite.hasNext()){
                mb_rolemaster row = (mb_rolemaster) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("roleid", row.getRoleid());
                jtemp.put("rolename", row.getRolename());
                jtemp.put("description", row.getDescription());
                jobj.append("data", jtemp);

            }
            if(jobj.length() > 0) {
                result = jobj.toString();
            } else {
                result = "{'data':[]}";
            }
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("accessRight.fetchrolegrdata", e);
        }
        return result;
    }
    public String fetchAllRoleAuthData(String groupid,HttpServletRequest request)throws ServiceException {
        String result = "";
        com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
        long count = 0;
        try{
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            String ss = "";
            ArrayList<Object> al = new ArrayList<Object>();
            String taskid = "";
            if(request.getParameter("taskid") != null) {
                taskid = request.getParameter("taskid");
            }
            String subStr = "and mb_permgrmaster.taskflag = 0 ";
            if(!StringUtil.isNullOrEmpty(taskid)) {
//                pm_taskmaster taskObj = (pm_taskmaster) get(pm_taskmaster.class, taskid);
                mb_reportlist taskObj = (mb_reportlist) get(mb_reportlist.class, taskid);
                subStr = " and mb_permgrmaster.taskflag = 1 and mb_permgrmaster.reportid = ? ";
                al.add(taskObj);
            }

            String s1 = "";
            if(request.getParameter("ss") != null) {
                ss = request.getParameter("ss");
                s1= StringUtil.getSearchString(ss, "and", new String[] {"mb_permgrmaster.permgrname"});
                StringUtil.insertParamSearchString(al, ss, 1);
            }

            String hql =  "select count(mb_permgrmaster.permgrid) as count from "+PropsValues.PACKAGE_PATH+".mb_permgrmaster as mb_permgrmaster " +
                    " left join mb_permgrmaster.reportid  as mb_reportlist where mb_reportlist.deleteflag = 0 "+subStr+s1;

            List list = executeQuery(hql, al.toArray());
            Iterator iteCnt = list.iterator();
            if( iteCnt.hasNext() ) {
                count = Long.parseLong(iteCnt.next().toString());
            }

            hql =  "select mb_permgrmaster.permgrid,mb_permgrmaster.permgrname from "+PropsValues.PACKAGE_PATH+".mb_permgrmaster as mb_permgrmaster " +
                    " left join mb_permgrmaster.reportid  as mb_reportlist where mb_reportlist.deleteflag = 0 "+subStr+s1;

            List ls = executeQueryPaging(hql, al.toArray(), new Integer[] {start, limit});
            Iterator ite = ls.iterator();
            while(ite.hasNext()) {
                Object[] row = (Object[]) ite.next();

                String permgrid = row[0].toString();
                String permgrName = row[1].toString();
                mb_permgrmaster permGrObj = (mb_permgrmaster) get(mb_permgrmaster.class,Integer.parseInt(permgrid));
//To do - remove not in query once commnet and document functionality is added.
                String PERM_QUERY = "select mb_permmaster.permid, mb_permmaster.permname from "+PropsValues.PACKAGE_PATH+".mb_permmaster as mb_permmaster " +
                        " where mb_permmaster.permgrid = ? and mb_permmaster.permid not in (3,4,5,6) ";

                List permLS = executeQuery(PERM_QUERY, permGrObj);
                Iterator permITE = permLS.iterator();
                while(permITE.hasNext()) {
                    com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                    Object[] permrow = (Object[]) permITE.next();
                    jtemp.put("permid", permrow[0]);
                    jtemp.put("permname", permrow[1]);
                    jtemp.put("access-rights-set", permgrName);
                    jtemp.put("permgrid", permgrid);
                    jobj.append("data", jtemp);
                }
            }

            String colArray[] = {"permid","permname","access-rights-set","permgrid"};
            for(int i= 0;i<colArray.length;i++) {
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("0",colArray[i]);
                jobj.append("columnheader", jtemp);
            }
            if(!StringUtil.isNullOrEmpty(groupid)) {
                hql = " from "+PropsValues.PACKAGE_PATH+".mb_rolemaster as mb_rolemaster where mb_rolemaster.groupid.groupid = ?";
                ls = executeQuery(hql, Integer.parseInt(groupid));
                ite = ls.iterator();
                while(ite.hasNext()){
                    mb_rolemaster row = (mb_rolemaster) ite.next();
                    com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                    jtemp.put("0", row.getRolename());
                    jtemp.put("1", row.getRoleid());
                    jobj.append("columnheader", jtemp);
                }
            }

            if(jobj.has("data")) {
                JSONArray dataArr = jobj.getJSONArray("data");
                for(int i=0;i<dataArr.length();i++){
                    ite = ls.iterator();
                    int cnt = 0;
                    while(ite.hasNext()){
                        mb_rolemaster row = (mb_rolemaster) ite.next();

                        int roleid = row.getRoleid();
                        int permid = Integer.parseInt(dataArr.getJSONObject(i).get("permid").toString());
                        int permgrid = Integer.parseInt(dataArr.getJSONObject(i).get("permgrid").toString());
                        mb_permgrmaster permgridObj = (mb_permgrmaster) get(mb_permgrmaster.class,permgrid);
                        hql = " from "+PropsValues.PACKAGE_PATH+".mb_roleperm as mb_roleperm where mb_roleperm.permgrid = ? and mb_roleperm.roleid = ?";
                        List permMapls = executeQuery(hql,new Object[]{permgridObj,row});
                        Iterator permMapite = permMapls.iterator();
                        if(permMapite.hasNext()){
                            mb_roleperm rolePermObj = (mb_roleperm) permMapite.next();
                            Boolean bassigned= false;
                            int localpermViewVal = rolePermObj.getPermvalview();//Integer.parseInt(rs.getString("permvalview"));
                            if(localpermViewVal!=0) {
                               int resultval = (int)(Math.pow(2, Double.valueOf(permid))) & localpermViewVal;
                               if(resultval!=0){
                                    dataArr.getJSONObject(i).put(row.getRolename(), "1");
                                    bassigned = true;
                               }
                            }
                            if(!bassigned){
                            int localpermEditVal = rolePermObj.getPermvaledit();//Integer.parseInt(rs.getString("permvaledit"));
                            if(localpermEditVal!=0) {
                               int resultval = (int)(Math.pow(2, Double.valueOf(permid))) & localpermEditVal;
                                   if(resultval!=0){
                                    dataArr.getJSONObject(i).put(row.getRolename(), "2");
                                        bassigned = true;
                                   }
                                }
                            }
                            if(!bassigned)
                                dataArr.getJSONObject(i).put(row.getRolename(), "0");
                         }else{
                            dataArr.getJSONObject(i).put(row.getRolename(), "0");
                        }
                    }
                }
                jobj.put("TotalCount", count);
                result = jobj.toString();
            } else {
                result = "{'data' : [],'TotalCount' : '0' }";
            }

        }catch(JSONException e){
            logger.warn(e.getMessage(), e);
              throw ServiceException.FAILURE("accessRight.fetchAllRoleAuthData", e);
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
              throw ServiceException.FAILURE("accessRight.fetchAllRoleAuthData", e);
        }
         return result;
    }

    public String fetchGridColumns(String groupid,HttpServletRequest request)throws ServiceException {
        String result = "";
        com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
        long count = 0;
        try{

            String colArray[] = {"permid","permname","access-rights-set","permgrid"};
            for(int i= 0;i<colArray.length;i++) {
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("0",colArray[i]);
                jobj.append("columnheader", jtemp);
            }
            if(!StringUtil.isNullOrEmpty(groupid)) {
                String hql = " from "+PropsValues.PACKAGE_PATH+".mb_rolemaster as mb_rolemaster where mb_rolemaster.groupid.groupid = ?";
                List ls = executeQuery(hql, Integer.parseInt(groupid));
                Iterator ite = ls.iterator();
                while(ite.hasNext()){
                    mb_rolemaster row = (mb_rolemaster) ite.next();
                    com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                    jtemp.put("0", row.getRolename());
                    jtemp.put("1", row.getRoleid());
                    jobj.append("columnheader", jtemp);
                }
            }
            result = jobj.toString();
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
              throw ServiceException.FAILURE("accessRight.fetchAllRoleAuthData", e);
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
              throw ServiceException.FAILURE("accessRight.fetchAllRoleAuthData", e);
        }
         return result;
    }

    public void addrolegr(String grname, String grdesc)throws ServiceException {
         mb_rolegrmaster grMaster = new  mb_rolegrmaster();
            grMaster.setDescription(grdesc);
            grMaster.setGroupname(grname);
            save(grMaster);


    }

    public void deleteRoleGroup(String groupid)throws ServiceException {

            String query = "delete from lrolegrmaster where groupid = ?";
            String deleteQuery = "delete from "+PropsValues.PACKAGE_PATH+".mb_rolegrmaster as mb_rolegrmaster where mb_rolegrmaster.groupid = ?";
            int num = executeUpdate(deleteQuery,groupid);

    }

    public void updaterolegr(String groupid, String data, String column)throws ServiceException {
            String hql = "update "+PropsValues.PACKAGE_PATH+".mb_rolegrmaster as mb_rolegrmaster set mb_rolegrmaster."+column+" = ? where mb_rolegrmaster.groupid = ? " ;
            executeUpdate(hql, new Object[]{data, groupid});
    }


    public String fetchroledata()throws ServiceException {
        String result ="";
        com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
        try{
            String hql = "select mb_rolegrmaster.groupid, mb_rolegrmaster.groupname, mb_rolegrmaster.description " +
                    "from com.krawler.esp.hibernate.impl.mb_rolegrmaster as mb_rolegrmaster ";
            String selectQuery = "from "+PropsValues.PACKAGE_PATH+".mb_rolemaster as mb_rolemaster";
            String query = "select roleid,lrolegrmaster.groupname,lrolegrmaster.groupid,rolename,roledesc from lrolemaster inner join lrolegrmaster on lrolegrmaster.groupid = lrolemaster.groupid";
            List ls = executeQuery(selectQuery);
            Iterator ite = ls.iterator();
            while(ite.hasNext()){
                mb_rolemaster row = (mb_rolemaster) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("roleid", row.getRoleid());
                jtemp.put("groupid", row.getGroupid().getGroupid());
                jtemp.put("rolename", row.getRolename());
                jtemp.put("description", row.getDescription());
                jobj.append("data", jtemp);

            }
           if(jobj.length() > 0) {
                result = jobj.toString();
            } else {
                result = "{'data':[]}";
            }
        }catch(JSONException e){
            logger.warn(e.getMessage(), e);
              throw ServiceException.FAILURE("accessRight.fetchroledata", e);
        }

        return result;
    }

    public void addrole(String rolename,String roledesc, String grid)throws ServiceException {

            mb_rolegrmaster  grMaster = (mb_rolegrmaster) get(mb_rolegrmaster.class,Integer.parseInt(grid));
            mb_rolemaster roleMaster = new mb_rolemaster();
            roleMaster.setDescription(roledesc);
            roleMaster.setRolename(rolename);
            roleMaster.setDescription("");
            roleMaster.setGroupid(grMaster);
            save(roleMaster);


    }

    public void deleteRole(String roleid)throws ServiceException {
            String query = "delete from "+PropsValues.PACKAGE_PATH+".mb_rolemaster as mb_rolemaster where mb_rolemaster.roleid = ?";
            int num = executeUpdate(query,roleid);

    }

    public void updaterole(String roleid,String rolename, String roledesc, String groupid)throws ServiceException {
            try{
                mb_rolegrmaster grMaster = (mb_rolegrmaster) get(mb_rolegrmaster.class, Integer.parseInt(groupid));
                String hql = "update "+PropsValues.PACKAGE_PATH+".mb_rolemaster as mb_rolemaster set mb_rolemaster.rolename = ?," +
                         "mb_rolemaster.description = ?,mb_rolemaster.groupid = ? where mb_rolemaster.roleid = ? " ;
                int num = executeUpdate(hql,new Object[] {rolename,roledesc,grMaster,Integer.parseInt(roleid)});
            }catch(Exception ex){
                logger.warn(ex.getMessage(), ex);
                throw ServiceException.FAILURE("accessRight.fetchSingleRoleGrpData", ex);
            }
    }

     public String fetchSingleRoleGrpData(String grpid) throws ServiceException {
        String result = "";
        com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
        try{
//            mb_rolegrmaster grMaster = (mb_rolegrmaster) get(mb_rolegrmaster.class, grpid);
            String query = "from "+PropsValues.PACKAGE_PATH+".mb_rolemaster as mb_rolemaster where mb_rolemaster.groupid.groupid = ?";
            List ls = executeQuery(query,Integer.parseInt(grpid));
            Iterator ite = ls.iterator();
            while(ite.hasNext()){
                mb_rolemaster row = (mb_rolemaster) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("roleid", row.getRoleid());
                jtemp.put("rolename", row.getRolename());
                jobj.append("data", jtemp);
            }
            if(jobj.length() > 0) {
                result = jobj.toString();
            } else {
                result = "{'data':[]}";
            }
        }catch(JSONException e){
            logger.warn(e.getMessage(), e);
              throw ServiceException.FAILURE("accessRight.fetchSingleRoleGrpData", e);
        }

        return result;
    }

      public String fetchPermGrpData(HttpServletRequest request) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        try{
            String taskid = "";
            if(request.getParameter("taskid") != null) {
                taskid = request.getParameter("taskid");
            }
            Object[] paramArray = new Object[]{};
            String subStr = " where mb_permgrmaster.taskflag = 0 ";
            if(!StringUtil.isNullOrEmpty(taskid)) {
//                pm_taskmaster taskObj = (pm_taskmaster) get(pm_taskmaster.class, taskid);
                mb_reportlist taskObj = (mb_reportlist) get(mb_reportlist.class, taskid);
                subStr = " where mb_permgrmaster.taskflag = 1 and mb_permgrmaster.reportid = ? ";
                paramArray = new Object[] {taskObj};
            }

            String query = "from "+PropsValues.PACKAGE_PATH +".mb_permgrmaster as mb_permgrmaster "+ subStr;
            List ls = executeQuery(query, paramArray);
            Iterator ite = ls.iterator();
            while(ite.hasNext()){
                mb_permgrmaster row = (mb_permgrmaster) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("permgrid", row.getPermgrid());
                jtemp.put("access-rights-set", row.getPermgrname());
                jobj.append("data", jtemp);
            }
            if(jobj.length() > 0) {
                result = jobj.toString();
            } else {
                result = "{'data':[]}";
            }
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
              throw ServiceException.FAILURE("accessRight.fetchPermGrpData", e);
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
              throw ServiceException.FAILURE("accessRight.fetchPermGrpData", e);
        }
        return result;
    }
    public String fetchSinglePermGrpData(String permid) throws ServiceException {
        String result = "";
        JSONObject jobj = new JSONObject();
        try{
            mb_permgrmaster permgr = (mb_permgrmaster) get(mb_permgrmaster.class, Integer.parseInt(permid));
            String query = "from "+PropsValues.PACKAGE_PATH+".mb_permmaster as mb_permmaster where mb_permmaster.permgrid = ?";
            List ls = executeQuery(query,new Object[]{permgr});
            Iterator ite = ls.iterator();
            while(ite.hasNext()){
                mb_permmaster row = (mb_permmaster) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("permid", row.getPermid());
                jtemp.put("tasks", row.getPermname());
                jobj.append("data", jtemp);
            }
            result = jobj.toString();
//            String query = "select permid, permname as Tasks from lpermmaster where permgrid = ?";
        } catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("accessRight.fetchSinglePermGrpData", e);
        }
        return result;
    }

    public String addPermGrp(String grname) throws ServiceException {
        String result = "{success : false}";
        mb_permgrmaster permgrmaster = new mb_permgrmaster();
        permgrmaster.setPermgrname(grname);
        permgrmaster.setDescription(grname);
        permgrmaster.setTaskflag(0);
        save(permgrmaster);
        result = "{success : true}";
        return result;
    }

    public void addPermGrp(mb_permgrmaster permgrmaster, mb_reportlist report, pm_taskmaster taskObj) throws ServiceException {
              String grname = report.getReportname() +" Permissions ("+"mb_"+report.getReportkey()+"_"+report.getReportname()+")";
              permgrmaster.setPermgrname(grname);
              permgrmaster.setDescription(grname);
              permgrmaster.setReportid(report);
//              permgrmaster.setTaskid(taskObj);
              permgrmaster.setTaskflag(1);
              save(permgrmaster);
    }

    public int getModPermGrp(String reportid) throws ServiceException {
        int permgrid = 0;
        try {
            String qry = "select mb_permgrmaster.permgrid from "+PropsValues.PACKAGE_PATH+".mb_permgrmaster as mb_permgrmaster where mb_permgrmaster.reportid.reportid = ?";
            List PermLs = executeQuery(qry, new Object[]{reportid});
            Iterator ite = PermLs.iterator();
            if (ite.hasNext()) {
                permgrid = Integer.parseInt(ite.next().toString());
            }
        } finally {
            return permgrid;
        }

    }

    public void addPerm(String permname, String grid) throws ServiceException {

            mb_permmaster permMaster = new mb_permmaster();
            permMaster.setPermid(getMaxPermid(Integer.parseInt(grid)));
            permMaster.setPermname(permname);
            permMaster.setDescription(permname);
            mb_permgrmaster permgrid = (mb_permgrmaster) get(mb_permgrmaster.class,Integer.parseInt(grid));
            permMaster.setPermgrid(permgrid);
            save(permMaster);

    }

    public int getMaxPermid(int grid) throws ServiceException {
        int permid = 0;
        try{
            String qry = "select max(mb_permmaster.permid) from "+PropsValues.PACKAGE_PATH+".mb_permmaster as mb_permmaster where mb_permmaster.permgrid.permgrid = ?";
            List PermLs = executeQuery(qry, new Object[]{grid});
            Iterator ite = PermLs.iterator();
            if (ite.hasNext()) {
                permid = Integer.parseInt(ite.next().toString());
            }
            permid += 1;
        } finally {
            return permid;
        }
    }

    public void updatePerm(String permid, String permname, String groupid) throws ServiceException {
            String query = "update "+PropsValues.PACKAGE_PATH+".mb_permmaster as mb_permmaster set mb_permmaster.permname= ? where permid = ?";
             int num = executeUpdate(query,new Object[] {permname,Integer.parseInt(permid)});

    }


    public void insertPermVal(HttpServletRequest request)throws ServiceException {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(request.getParameter("data"));
            JSONObject JObj = new JSONObject();
            for (int k = 0; k < jsonArray.length(); k++) {
                JObj = jsonArray.getJSONObject(k);
                String permgrid = JObj.getString("permgrid");
                String roleid = JObj.getString("roleid");
                String permvalview = JObj.getString("permvalview");
                String permvaledit = JObj.getString("pervaledit");
//                dbcon.insertPermVal(JObj.getString("permgrid"),JObj.getString("roleid"),JObj.getString("permvalview"),JObj.getString("pervaledit"),request);

                String query = "from "+PropsValues.PACKAGE_PATH+".mb_roleperm as mb_roleperm where mb_roleperm.permgrid.permgrid = ? and mb_roleperm.roleid.roleid = ?";
                List rolePermLs = executeQuery(query,new Object[]{Integer.parseInt(permgrid),Integer.parseInt(roleid)});
                Iterator ite = rolePermLs.iterator();
                mb_roleperm obj = null;
                if(ite.hasNext()){
                    obj = (mb_roleperm) ite.next();
                }
                if(!permvalview.equals("0") || !permvaledit.equals("0")) {
                    if(obj==null){
                        obj = new mb_roleperm();
                        obj.setPermgrid((mb_permgrmaster)get(mb_permgrmaster.class, Integer.parseInt(permgrid)));
                        obj.setRoleid((mb_rolemaster)get(mb_rolemaster.class, Integer.parseInt(roleid)));
                        obj.setPermvalview(Integer.parseInt(permvalview));
                        obj.setPermvaledit(Integer.parseInt(permvaledit));
                        save(obj);
                    }else{
                        obj.setPermgrid((mb_permgrmaster)get(mb_permgrmaster.class, Integer.parseInt(permgrid)));
                        obj.setRoleid((mb_rolemaster)get(mb_rolemaster.class, Integer.parseInt(roleid)));
                        obj.setPermvalview(Integer.parseInt(permvalview));
                        obj.setPermvaledit(Integer.parseInt(permvaledit));
                        saveOrUpdate(obj);
                    }
                }else{
                    if(obj!=null){
                        String DELETE_QUERY = "delete from "+PropsValues.PACKAGE_PATH+".mb_roleperm as mb_roleperm where mb_roleperm.id = ?";
                        executeUpdate(DELETE_QUERY,obj.getId());
                    }
                }
            }
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }
      public String deleteUPG(String groupid)throws ServiceException {
//        ResultSet rs = null;
//        PreparedStatement pstmt = null;
//        JSONObject jobj =  new JSONObject();
//        try{
//            String query = "select count(*) as count from lrolegrmaster " +
//                    "inner join lrolemaster on lrolemaster.groupid = lrolegrmaster.groupid " +
//                    "inner join luserrolemapping on luserrolemapping.roleid = lrolemaster.roleid " +
//                    "where lrolegrmaster.groupid = ?";
//            pstmt = conn.prepareStatement(query);
//            pstmt.setString(1, groupid);
//            rs = pstmt.executeQuery();
//            if(rs.next()) {
//                int count = rs.getInt("count");
//                if(count > 0) {
//                    jobj.put("data", "no");
//                } else {
//                    query = "delete from lrolegrmaster where lrolegrmaster.groupid = ?";
//                    pstmt = conn.prepareStatement(query);
//                    pstmt.setString(1, groupid);
//                    pstmt.executeUpdate();
//
//                    jobj.put("data", "yes");
//                }
//            } else {
//                jobj.put("data", "error");
//            }
//        } catch (SQLException e) {
//                throw ServiceException.FAILURE("authorization.updaterole", e);
//        } catch (JSONException e) {
//                throw ServiceException.FAILURE("authorization.updaterole", e);
//        } finally {
//                    DbPool.closeStatement(pstmt);
//        }
        return "";
    }

      public String fetchSingleRoleGrpDataCopy(String grpid) throws ServiceException {
          String result = "";
          JSONObject jobj = new JSONObject();
          try{
              String query = "from "+PropsValues.PACKAGE_PATH+".mb_rolemaster as mb_rolemaster where mb_rolemaster.groupid.groupid = ? ";
              List rolePermLs = executeQuery(query,new Object[]{Integer.parseInt(grpid)});
              Iterator ite = rolePermLs.iterator();
              while (ite.hasNext()){
                  mb_rolemaster objRoleMaster = (mb_rolemaster) ite.next();
                  JSONObject jtemp = new JSONObject();
                  jtemp.put("roleid",objRoleMaster.getRoleid() );
                  jtemp.put("rolename", "Copy of " + objRoleMaster.getRolename());
                  jobj.append("data", jtemp);
              }
          }catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("accessRight.fetchSinglePermGrpData", e);
          }
          return result;
//        String returnStr = "";
//        ResultSet rs = null;
//        PreparedStatement pstmt = null;
//        JSONObject jobj = new JSONObject();
//        JSONObject jtemp = new JSONObject();
//        try {
//            String query = "select roleid, rolename from lrolemaster where groupid = ?";
//            pstmt = conn.prepareStatement(query);
//            pstmt.setString(1, grpid);
//            rs = pstmt.executeQuery();
//            while(rs.next()) {
//                jtemp.put("roleid", rs.getString("roleid"));
//                jtemp.put("rolename", "Copy of " + rs.getString("rolename"));
//                jobj.append("data", jtemp);
//                jtemp = new JSONObject();
//            }
//            returnStr = jobj.toString();
//        } catch (SQLException e) {
//            throw ServiceException.FAILURE("deleteRoleGroup.fetchrolegrdata", e);
//        } catch (JSONException e) {
//            throw ServiceException.FAILURE("deleteRoleGroup.fetchrolegrdata", e);
//        } finally {
//            DbPool.closeStatement(pstmt);
//        }
//        return returnStr;
    }

    public String getAllRoles() throws ServiceException {
          String result = "";
          JSONObject jobj = new JSONObject();
          try{
              String query = "from "+PropsValues.PACKAGE_PATH+".mb_rolemaster as mb_rolemaster";
              List rolePermLs = executeQuery(query);
              Iterator ite = rolePermLs.iterator();
              while (ite.hasNext()){
                  mb_rolemaster objRoleMaster = (mb_rolemaster) ite.next();
                  JSONObject jtemp = new JSONObject();
                  jtemp.put("roleid",objRoleMaster.getRoleid() );
                  jtemp.put("rolename", objRoleMaster.getRolename());
                  jobj.append("data", jtemp);
              }
              result = jobj.toString();
          }catch(JSONException e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("accessRight.fetchSinglePermGrpData", e);
          }
          return result;
    }

    public String copyUPG(String originalGrid, String grname, String grdesc, JSONArray jarr)throws ServiceException {
     JSONObject jobj =  new JSONObject();
     try{
        jobj.put("success", false);
        mb_rolegrmaster grMasterObj = new mb_rolegrmaster();
        grMasterObj.setDescription(grdesc);
        grMasterObj.setGroupname(grname);
        save(grMasterObj);
        String hql = "";
        for(int i=0;i<jarr.length();i++){
            hql = "from "+PropsValues.PACKAGE_PATH+".mb_rolemaster as mb_rolemaster where mb_rolemaster.roleid = ? ";
            List li = executeQuery(hql, Integer.parseInt(jarr.getJSONObject(i).getString("roleid")));
            Iterator ite = li.iterator();
            if(ite.hasNext()){
                mb_rolemaster roleMasterObj = new mb_rolemaster();
                roleMasterObj.setGroupid(grMasterObj);
                roleMasterObj.setRolename(jarr.getJSONObject(i).getString("rolename"));
                roleMasterObj.setDescription("");
                save(roleMasterObj);

                hql = "from "+PropsValues.PACKAGE_PATH+".mb_roleperm as mb_roleperm where mb_roleperm.roleid.roleid = ? ";
                li = executeQuery(hql, Integer.parseInt(jarr.getJSONObject(i).getString("roleid")));
                ite = li.iterator();
                while(ite.hasNext()){
                    mb_roleperm obj = (mb_roleperm) ite.next();
                    mb_roleperm newObj = new mb_roleperm();
                    newObj.setPermgrid(obj.getPermgrid());
                    newObj.setRoleid(roleMasterObj);
                    newObj.setPermvaledit(obj.getPermvaledit());
                    newObj.setPermvalview(newObj.getPermvalview());
                    save(newObj);
                }
            }
        }
//        ResultSet rs = null;
//        PreparedStatement pstmt = null;
//        JSONObject jobj =  new JSONObject();
//
//
//            jobj.put("success", false);
//
//            String grroleid = java.util.UUID.randomUUID().toString();
//            String query = "insert into lrolegrmaster(groupid,groupname,description) values (?,?,?)";
//            pstmt = conn.prepareStatement(query);
//            pstmt.setString(1, grroleid);
//            pstmt.setString(2, grname);
//            pstmt.setString(3, grdesc);
//            pstmt.executeUpdate();
//
//            for(int i = 0; i < jarr.length(); i++) {
//                query = "select roleid, groupid, rolename, num, grtemp, rltemp, featureview from lrolemaster where roleid = ?";
//                pstmt = conn.prepareStatement(query);
//                pstmt.setString(1, jarr.getJSONObject(i).getString("roleid"));
//                rs = pstmt.executeQuery();
//
//                if(rs.next()) {
//                    String newrid = UUID.randomUUID().toString();
//                    query = "insert into lrolemaster(roleid, groupid, rolename, num, grtemp, rltemp, featureview) values(?, ?, ?, ?, ?, ?, ?)";
//                    pstmt = conn.prepareStatement(query);
//                    pstmt.setString(1, newrid);
//                    pstmt.setString(2, grroleid);
//                    pstmt.setString(3, jarr.getJSONObject(i).getString("rolename"));
//                    pstmt.setInt(4, rs.getInt("num"));
//                    pstmt.setString(5, rs.getString("grtemp"));
//                    pstmt.setString(6, rs.getString("rltemp"));
//                    pstmt.setString(7, rs.getString("featureview"));
//                    pstmt.executeUpdate();
//
//                    DbPool.closeStatement(pstmt);
//
//                    query = "select permgrid, roleid, permvalview, permvaledit from lroleperms where roleid = ?";
//                    pstmt = conn.prepareStatement(query);
//                    pstmt.setString(1, jarr.getJSONObject(i).getString("roleid"));
//                    rs = pstmt.executeQuery();
//
//                    while(rs.next()) {
//                        query = "insert into lroleperms(permgrid, roleid, permvalview, permvaledit) values(?, ?, ?, ?)";
//                        pstmt = conn.prepareStatement(query);
//                        pstmt.setString(1, rs.getString("permgrid"));
//                        pstmt.setString(2, newrid);
//                        pstmt.setInt(3, rs.getInt("permvalview"));
//                        pstmt.setInt(4, rs.getInt("permvaledit"));
//                        pstmt.executeUpdate();
//
//                        DbPool.closeStatement(pstmt);
//                    }
//                    //DbPool.closeStatement(pstmt);
//                }
//
//                DbPool.closeResults(rs);
//                DbPool.closeStatement(pstmt);
//
//            }

            jobj.put("success", true);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("authorization.updaterole", e);
        }
        return jobj.toString();
    }

    public JSONArray getMBRolePermisionSet(String userid) throws ServiceException {
        JSONArray accesset = new JSONArray();
        try {
            String package_path = "com.krawler.esp.hibernate.impl";
            String SELECT_USER_ROLES ="select  mb_roleperm.roleid.roleid,mb_roleperm.permgrid.permgrid, mb_roleperm.permvalview, mb_roleperm.permvaledit," +
                    " mb_rolemaster.groupid.groupid " +
                    "from "+package_path+".mb_roleperm as mb_roleperm inner join mb_roleperm.roleid as mb_rolemaster " +
                    "where mb_rolemaster.roleid in ( select distinct userrolemapping.roleid " +
                    "from "+package_path+".userrolemapping as userrolemapping where userrolemapping.userid.userID = ? )";
            List list = executeQuery(SELECT_USER_ROLES, new Object[] {userid});
            Iterator ite = list.iterator();
            JSONArray roleset = new JSONArray();
            String prevrolegrpid = "";
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject jobjloc = new JSONObject();
                jobjloc.put("roleid", row[0]);
                jobjloc.put("permgrid", row[1]);
                jobjloc.put("permvalview", row[2]);
                jobjloc.put("permvaledit", row[3]);
//                jobjloc.put("permgrname", rs1.getString("permgrname"));
                if (StringUtil.isNullOrEmpty(prevrolegrpid)) {
                        prevrolegrpid =  row[4].toString();
                }
                if (!prevrolegrpid.equals(row[4].toString())) {
                        JSONObject jrolegrp = new JSONObject();
                        jrolegrp.put("rolegroupid", prevrolegrpid);
                        jrolegrp.put("roleset", roleset);
                        accesset.put(jrolegrp);
                        roleset = new JSONArray();
                        roleset.put(jobjloc);
                        prevrolegrpid =  row[4].toString();
                } else {
                        roleset.put(jobjloc);
                }
            }
            //add the final block
            JSONObject jrolegrp = new JSONObject();
            jrolegrp.put("rolegroupid", prevrolegrpid);
            jrolegrp.put("roleset", roleset);
            accesset.put(jrolegrp);

        } catch (Exception sex) {
            logger.warn(sex.getMessage(), sex);
                throw ServiceException.FAILURE("permissionHandlerDAOImpl.getRolePermisionSet", sex);
        }
        return accesset;
    }

    public JSONArray getMBRealRoleIds(String userid) throws ServiceException{
            JSONArray roles = new JSONArray();
            try {
                String package_path = "com.krawler.esp.hibernate.impl";
                String SELECT_USER_REALROLES ="select distinct userrolemapping.roleid.roleid from "+package_path+".userrolemapping " +
                        "as userrolemapping inner join userrolemapping.userid as users " +
                        "where users.userID=?";
                List list = executeQuery(SELECT_USER_REALROLES, new Object[] {userid});
                Iterator ite = list.iterator();
                while( ite.hasNext() ) {
//                    Object[] row = (Object[]) ite.next();
                    JSONObject j = new JSONObject();
                    j.put("val", ite.next().toString());
                    roles.put(j);
                }
            } catch (JSONException e) {
                logger.warn(e.getMessage(), e);
                    throw ServiceException.FAILURE("AuthHandler.getRolePermisionSet", e);
            }
            return roles;
    }
}
