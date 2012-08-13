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
import java.util.ArrayList;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import org.hibernate.HibernateException;
import com.krawler.crm.database.tables.Commission;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.crm.database.tables.UserCommissionPlan;
import com.krawler.dao.BaseDAO;
import java.util.Date;

public class accIntDAOImpl extends BaseDAO implements accIntDAO {

    public KwlReturnObject getCommisionPlans(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException{
        List list = null;
        int totalCount = 0;
		try {
            String query="from Commission ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            query += filterQuery;
            list = executeQuery(query, filter_params.toArray());
            totalCount = list.size();

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("accIntDAOImpl.getCommisionPlans", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", list, totalCount);
    }

    public KwlReturnObject getUserCommisionPlans(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException{
        List list = null;
        int totalCount = 0;
		try {
            String query="from UserCommissionPlan ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            query += filterQuery;
            list = executeQuery(query, filter_params.toArray());
            totalCount = list.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("accIntDAOImpl.getUserCommisionPlans", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", list, totalCount);
    }

    public KwlReturnObject saveCommisionPlans(HashMap<String, Object> requestParams) throws ServiceException, HibernateException {
        List list = new ArrayList();
        try {

            JSONArray jArrRes = new JSONArray();
            JSONArray jArr = new JSONArray(requestParams.get("data").toString());
            JSONArray jDelArr = new JSONArray(requestParams.get("deleteddata").toString());
            String userid = requestParams.get("userid").toString();
            String companyid = requestParams.get("companyid").toString();
            Commission commision;
            Company company = (Company) get(Company.class, companyid);
            for (int i = 0; i < jDelArr.length(); i++) {
                JSONObject jobj = jDelArr.getJSONObject(i);
                commision = (Commission) get(Commission.class, jobj.getString("id"));
                String isAssignHQL = "from UserCommissionPlan where commissionplan = ?";
                List ll = executeQuery(isAssignHQL, commision);
                if(ll.size()>0) {
                    JSONObject tempObj = new JSONObject();
                    tempObj.put("name", commision.getName());
                    jArrRes.put(tempObj);
                }
                else
                    commision.setDeleted(1);
            }

            for (int i = 0; i < jArr.length(); i++) {
                JSONObject jobj = jArr.getJSONObject(i);
                if (jobj.getString("id") .equals("")) {
                    commision = new Commission();
                    commision.setCreatedon(new Date());
                }
                else
                    commision = (Commission) get(Commission.class, jobj.getString("id"));
                commision.setCompany(company);

                commision.setCreator((User) get(User.class, userid));
                commision.setIsPercent(Boolean.parseBoolean(jobj.getString("ispercent")));
                commision.setName(jobj.getString("name"));
                commision.setValue(Double.parseDouble(jobj.getString("value")));
                commision.setTarget(Double.parseDouble(jobj.getString("target")));
                commision.setGoalperiod(Integer.parseInt(jobj.getString("goalperiod")));
                commision.setGoaltype(Integer.parseInt(jobj.getString("goaltype")));
                saveOrUpdate(commision);
            }
            list.add(jArrRes);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("accIntDAOImpl.saveCommisionPlans", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("accIntDAOImpl.saveCommisionPlans", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", list, 1);
    }

    public KwlReturnObject assignCommisionPlans(HashMap<String, Object> requestParams) throws ServiceException {
        try {
            String userid = requestParams.get("userid").toString();
            String loginuserid = requestParams.get("loginuserid").toString();

            String[] plan = requestParams.get("planid").toString().split(",");
            String[] planyear = requestParams.get("year").toString().split(",");
            for(int cnt =0;cnt<plan.length;cnt++) {
                Commission commision = (Commission) get(Commission.class, plan[cnt]);
                if(commision!=null) {
                    UserCommissionPlan userCommission = new UserCommissionPlan();
                    userCommission.setAffectfrom(new Date());
                    userCommission.setCommissionplan(commision);
                    userCommission.setIsactive(true);
                    userCommission.setPlany(planyear[cnt]);
                    User user = (User) get(User.class, userid);
                    userCommission.setUserid(user);

                    User loggeduser = (User) get(User.class, loginuserid);
                    userCommission.setUsersByCreatedbyid(loggeduser);
                    userCommission.setUsersByUpdatedbyid(loggeduser);
                    saveOrUpdate(userCommission);
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("accIntDAOImpl.assignCommisionPlan", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", null, 1);
    }

}
