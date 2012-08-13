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
package com.krawler.spring.crm.integration;
import com.krawler.common.admin.User;
import java.util.ArrayList;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.crm.database.tables.Finalgoalmanagement;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import java.util.Date;
import java.util.HashMap;

public class hrmsIntDAOImpl extends BaseDAO implements hrmsIntDAO {

    public KwlReturnObject getFinalGoals(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        int start = 0;
        int limit = 25;
        String serverSearch = "";
        try {
            if (requestParams.containsKey("start") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
                start = Integer.parseInt(requestParams.get("start").toString());
            }
            if (requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("limit").toString())) {
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }

            String SELECT_USER_INFO = " select c from Finalgoalmanagement c "+crmManagerCommon.getJoinQuery(requestParams);
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            SELECT_USER_INFO += filterQuery;

            if (requestParams.containsKey("ss") && !StringUtil.isNullOrEmpty(requestParams.get("ss").toString())) {
                serverSearch = requestParams.get("ss").toString();
                StringUtil.insertParamSearchString(filter_params, serverSearch, 2);
                String searchQuery = StringUtil.getSearchString(serverSearch, "and", new String[]{"c.manager.firstName", "c.manager.lastName"});
                SELECT_USER_INFO +=searchQuery;
            }
            String orderQuery = " order by c.createdOn desc ";
            if(requestParams.containsKey("field")) {
                String dbname = crmManagerCommon.getFieldDbName(requestParams);
                if(dbname!=null){
                    String dir = requestParams.get("direction").toString();
                    orderQuery = " order by "+dbname+" "+dir+" ";
                }
            }
            ll = executeQuery( SELECT_USER_INFO, filter_params.toArray());
            dl = ll.size();
            if (requestParams.containsKey("start") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()) && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("limit").toString())) {
                ll = executeQueryPaging( SELECT_USER_INFO+orderQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("hrmsIntImpl.getFinalGoals : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public String goalsdelete(String id) throws ServiceException {
        String userid="";
        try {
            Finalgoalmanagement fgmt = (Finalgoalmanagement) get(Finalgoalmanagement.class, id);
            fgmt.setDeleted(true);
            saveOrUpdate(fgmt);
            userid = fgmt.getUserID().getUserID();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("hrmsIntImpl.goalsdelete", ex);
        }
        return userid;
    }

    public void goalsArchive(String id) throws ServiceException {
        List tabledata = null;
        try {
            String hql = "from Finalgoalmanagement where id=?";
            tabledata = executeQuery( hql, id);
            if (!tabledata.isEmpty()) {
                Finalgoalmanagement fgmt = (Finalgoalmanagement) tabledata.get(0);
                fgmt.setArchivedflag(1);
                saveOrUpdate(fgmt);
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("hrmsIntImpl.goalsArchive", ex);
        }
        
    }

   public int goalsDateCheck(HashMap<String, Object> requestParams) throws ServiceException{
        List list=null;
        try {
            int relatedto = Integer.parseInt(requestParams.get("relatedTo").toString());
            Long startdate =  Long.parseLong(requestParams.get("startdate").toString());
            Long enddate = Long.parseLong(requestParams.get("enddate").toString());
            User employee = (User) requestParams.get("employee");
            String hqldatechk = "From Finalgoalmanagement where relatedto = ? " +
                            "and (((? between startdate and enddate) or (? between startdate and enddate)) or " +
                            " (?<=startdate and ?>=enddate)) and userID = ? and deleted = ?";
            list = executeQuery( hqldatechk,
                            new Object[]{relatedto,startdate,enddate,startdate,enddate,employee,false});
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("hrmsIntImpl.goalsDateCheck", ex);
        }
        return list.size();
    }

    public String goalsInsert(HashMap requestParams) throws ServiceException {
        String goalid="";
        try {
            Finalgoalmanagement fgmt = null;
            if(requestParams.containsKey("id") && (!StringUtil.isNullOrEmpty(requestParams.get("id").toString()))){
                fgmt = (Finalgoalmanagement) get(Finalgoalmanagement.class, requestParams.get("id").toString());
                fgmt.setUpdatedon(new Date());
            }else {
                fgmt = new Finalgoalmanagement();
            }
            if(requestParams.containsKey("apprmanager") && requestParams.get("apprmanager")!=null){
                fgmt.setAssignedby(requestParams.get("apprmanager").toString());
            }
            if(requestParams.containsKey("user") && requestParams.get("user")!=null){
                fgmt.setManager((User) requestParams.get("user"));
            }
            if(requestParams.containsKey("description") && requestParams.get("description")!=null){
                fgmt.setGoaldesc(requestParams.get("description").toString());
            }
            if(requestParams.containsKey("goalname") && requestParams.get("goalname")!=null){
                fgmt.setGoalname(requestParams.get("goalname").toString());
            }
            if(requestParams.containsKey("startdate") && requestParams.get("startdate")!=null){
                fgmt.setStartdate((Long)requestParams.get("startdate"));
            }
            if(requestParams.containsKey("enddate") && requestParams.get("enddate")!=null){
                fgmt.setEnddate((Long)requestParams.get("enddate"));
            }
            if(requestParams.containsKey("createdon") && requestParams.get("createdon")!=null){
                fgmt.setCreatedOn((Long) requestParams.get("createdon"));
            }
            if(requestParams.containsKey("employee") && requestParams.get("employee")!=null){
                fgmt.setUserID((User) requestParams.get("employee"));
            }
            if(requestParams.containsKey("relatedTo") && requestParams.get("relatedTo")!=null){
                fgmt.setRelatedto(Integer.parseInt(requestParams.get("relatedTo").toString()));
            }
            if(requestParams.containsKey("targeted") && requestParams.get("targeted")!=null){
                fgmt.setTargeted(Long.parseLong(requestParams.get("targeted").toString()));
            }
            fgmt.setArchivedflag(0);
            
            saveOrUpdate(fgmt);
            goalid = fgmt.getId();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("hrmsIntImpl.goalsInsert", ex);
        }
        return goalid;
    }
}
