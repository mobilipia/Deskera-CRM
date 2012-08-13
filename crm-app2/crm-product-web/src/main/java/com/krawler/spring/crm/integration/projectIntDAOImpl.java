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
import com.krawler.crm.database.tables.AccountProject;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.handlers.APICallHandlerService;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import org.hibernate.HibernateException;
import com.krawler.esp.utils.ConfigReader;

public class projectIntDAOImpl extends BaseDAO implements projectIntDAO {

	private APICallHandlerService apiCallHandlerService;
    
    public void setApiCallHandlerService(APICallHandlerService apiCallHandlerService)
    {
        this.apiCallHandlerService = apiCallHandlerService;
    }

    //******************* Account Project *************************
    @Override
    public KwlReturnObject getAccountProjectDetails(ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = " Select c FROM AccountProject c ";
            int start = 0;
            int limit = 10;
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;

//            ll = executeQuery( Hql, filter_params.toArray());
//            dl = ll.size();
            ll = executeQueryPaging( Hql, filter_params.toArray(), new Integer[]{start, limit});
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmAccountDAOImpl.getAccounts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject saveAccountProject(JSONObject jobj1) throws ServiceException {
        JSONObject jobj = new JSONObject();
        List ll = new ArrayList();
        try {
            String companyid = jobj1.getString("companyid");
            String userid = jobj1.getString("userid");
            String projectName = jobj1.getString("projectName");
            String accId = jobj1.getString("accId");
            jobj.put("projectname", projectName);
            jobj.put("companyid", companyid);
            jobj.put("userid", userid);
            jobj.put("remoteapikey", ConfigReader.getinstance().get("remoteapikey"));

            jobj = apiCallHandlerService.callApp(jobj1.getString("appURL"), jobj, companyid, "12", true);
            if (jobj.has("success") && jobj.getBoolean("success")) {
                jobj = jobj.getJSONObject("data");
                String projectId = jobj.getString("projectid");
                String nickName = jobj.getString("nickname");

                AccountProject ap = new AccountProject();

                if (!StringUtil.isNullOrEmpty(accId)) {
                    ap.setAccountId((CrmAccount) get(CrmAccount.class, accId));
                }
                if (!StringUtil.isNullOrEmpty(nickName)) {
                    ap.setNickName(nickName);
                }
                if (!StringUtil.isNullOrEmpty(projectId)) {
                    ap.setProjectId(projectId);
                }
                if (!StringUtil.isNullOrEmpty(projectName)) {
                    ap.setProjectName(projectName);
                }

                save(ap);
                jobj = new JSONObject();
                jobj.put("projectid", ap.getProjectId());
            } else {
                String errorCode=jobj.getString("errorcode");
                jobj = new JSONObject();
                jobj.put("errorcode",errorCode);
            }
            ll.add(jobj);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmAccountDAOImpl.saveAccountProject : "+e.getMessage(), e);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmAccountDAOImpl.saveAccountProject : "+e.getMessage(), e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmAccountDAOImpl.saveAccountProject : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, 0);
    }
}
