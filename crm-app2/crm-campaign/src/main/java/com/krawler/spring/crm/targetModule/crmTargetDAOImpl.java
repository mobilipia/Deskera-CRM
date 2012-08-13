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
package com.krawler.spring.crm.targetModule; 
import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.crm.database.tables.TargetList;
import java.util.ArrayList;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.TargetModule;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.crm.database.tables.TargetListTargets;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.HashMap;
import org.springframework.dao.DataAccessException;

public class crmTargetDAOImpl extends BaseDAO implements crmTargetDAO {

    @Override
    public KwlReturnObject getTargets(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            boolean archive = false;
            if(requestParams.containsKey("isarchive") && requestParams.get("isarchive") != null) {
                archive = Boolean.parseBoolean(requestParams.get("isarchive").toString());
            }
            String companyid = requestParams.get("companyid").toString();
            String appendCase = "and";
            ArrayList filter_params = new ArrayList();

            String Hql = "select c from TargetModule c "+crmManagerCommon.getJoinQuery(requestParams)+" where  c.deleteflag=0 and c.isarchive= ? and c.company.companyID= ? ";
            filter_params.add(archive);
            filter_params.add(companyid);

            if(requestParams.containsKey("email") && requestParams.get("email") != null) {
                Hql += " and c.email != '' ";
            }
            String Searchjson = "";
            if(requestParams.containsKey("searchJson") && requestParams.get("searchJson") != null) {
                Searchjson = requestParams.get("searchJson").toString();
                if (!StringUtil.isNullOrEmpty(Searchjson)) {
                    String mySearchFilterString = StringUtil.getMyAdvanceSearchString(Searchjson, appendCase);
                    Hql += mySearchFilterString;
                }
                if (!StringUtil.isNullOrEmpty(Searchjson)) {
                    StringUtil.insertParamAdvanceSearchString(filter_params, Searchjson);
                }
            }

            if(requestParams.containsKey("config") && requestParams.get("config") != null) {
                Hql += " and c.validflag=1 ";
            }
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"c.firstname","c.lastname", "c.usersByUserid.firstName", "c.usersByUserid.lastName"};
                    StringUtil.insertParamSearchString(filter_params, ss, 4);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    Hql +=searchQuery;
                }
            }
            
            int start = 0;
            int limit = 25;
            if (requestParams.containsKey("start") && requestParams.containsKey("limit")) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            String selectInQuery = Hql + " and c.usersByUserid.userID in (" + usersList + ")  ";
            String orderQuery = " order by c.createdOn desc ";
            if(requestParams.containsKey("field") && requestParams.get("xfield") != null) {
                String dbname = crmManagerCommon.getFieldDbName(requestParams);
               if(dbname!=null){
                    String dir = requestParams.get("direction").toString();
                    orderQuery = " order by "+dbname+" "+dir+" ";
                }
            }

            selectInQuery = selectInQuery + orderQuery;

            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl = ll.size();
            String export = "";
            if(requestParams.containsKey("export") && requestParams.get("export") != null) {
                export = requestParams.get("export").toString();
            }
            if(StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTargetDAOImpl.getTargets : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getAllTargets(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
            String Hql = "select c from TargetModule c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;
            String selectInQuery = Hql ;
            selectInQuery = Hql + " and c.usersByUserid.userID in (" + usersList + ") ";
            selectInQuery += " order by c.firstname ";
            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl= ll.size();
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject addTargets(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            TargetModule targetModule = new TargetModule();
            if(jobj.has("description")) {
                targetModule.setDescription(jobj.getString("description"));
            }
            if(jobj.has("email")) {
                targetModule.setEmail(jobj.getString("email"));
            }
            if(jobj.has("firstname")) {
                targetModule.setFirstname(jobj.getString("firstname"));
            }
            if(jobj.has("lastname")) {
                targetModule.setLastname(jobj.getString("lastname"));
            }
            if(jobj.has("address")) {
                targetModule.setAddress(jobj.getString("address"));
            }
            if(jobj.has("mobile")) {
                targetModule.setMobileno(jobj.getString("mobile"));
            }
            if(jobj.has("phone")) {
                targetModule.setPhoneno(jobj.getString("phone"));
            }
            if(jobj.has("targetModuleownerid")) {
                targetModule.setUsersByUserid((User) get(User.class, jobj.getString("targetModuleownerid")));
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                targetModule.setUsersByCreatedbyid((User) get(User.class, userid));
                targetModule.setUsersByUpdatedbyid((User) get(User.class, userid));
            }
            if(jobj.has("updatedon")) {
                targetModule.setUpdatedOn(new Date().getTime());
            }
            if(jobj.has("validflag")) {
                targetModule.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("targetModuleid")) {
                id = jobj.getString("targetModuleid");
                targetModule.setId(id);
            }
            if(jobj.has("companyid")) {
                companyid =  jobj.getString("companyid");
                targetModule.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("company")) {
                targetModule.setCompanyname(jobj.getString("company"));
            }
            if(jobj.has("createdon")) {
                targetModule.setCreatedOn(new Date().getTime());
            }
            if(jobj.has("deleteflag")) {
                targetModule.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            save(targetModule);
            ll.add(targetModule);
            
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTargetDAOImpl.addTargets : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTargetDAOImpl.addTargets : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTargetDAOImpl.addTargets : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject editTargets(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";            
            if(jobj.has("targetModuleid")) {
                id = jobj.getString("targetModuleid");
            }
            TargetModule targetModule = (TargetModule) get(TargetModule.class, id);
            if(jobj.has("description")) {
                targetModule.setDescription(jobj.getString("description"));
            }
            if(jobj.has("email")) {
                targetModule.setEmail(jobj.getString("email"));
            }
            if(jobj.has("firstname")) {
                targetModule.setFirstname(jobj.getString("firstname"));
            }
            if(jobj.has("lastname")) {
                targetModule.setLastname(jobj.getString("lastname"));
            }
            if(jobj.has("address")) {
                targetModule.setAddress(jobj.getString("address"));
            }
            if(jobj.has("mobile")) {
                targetModule.setMobileno(jobj.getString("mobile"));
            }
            if(jobj.has("phone")) {
                targetModule.setPhoneno(jobj.getString("phone"));
            }
            if(jobj.has("targetModuleownerid")) {
                targetModule.setUsersByUserid((User) get(User.class, jobj.getString("targetModuleownerid")));
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
//                targetModule.setUsersByCreatedbyid((User) get(User.class, userid));
                targetModule.setUsersByUpdatedbyid((User) get(User.class, userid));
            }
            if(jobj.has("updatedon")) {
                targetModule.setUpdatedOn(new Date().getTime());
            }
            if(jobj.has("validflag")) {
                targetModule.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("companyid")) {
                companyid =  jobj.getString("companyid");
                targetModule.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("company")) {
                targetModule.setCompanyname(jobj.getString("company"));
            }
            if(jobj.has("createdon")) {
                targetModule.setCreatedOn(new Date().getTime());
            }
            if(jobj.has("deleteflag")) {
                targetModule.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            saveOrUpdate(targetModule);
            ll.add(targetModule);
           
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTargetDAOImpl.editTargets : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTargetDAOImpl.editTargets : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmTargetDAOImpl.editTargets : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public void saveTargetListTargets(String id, String email, TargetList targetList, String name) {
                TargetListTargets targetObj = new TargetListTargets();
                targetObj.setRelatedid(id);
                targetObj.setRelatedto(4);
                if(email!=null) {
                    targetObj.setEmailid(email);
                }
                targetObj.setTargetlistid(targetList);
                targetObj.setLname(name);
                targetObj.setDeleted(0);
                save(targetObj);
    }
}
