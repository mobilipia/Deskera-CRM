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
package com.krawler.spring.crm.userModule;
import org.hibernate.SessionFactory;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.HashMap;

public class crmUserDAOImpl extends BaseDAO implements crmUserDAO {
    
    public KwlReturnObject getOwner(String companyid, String userid, StringBuffer usersList) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select c from User c where  c.company.companyID = ? and c.deleteflag = 0 ";
            if(usersList !=null && !StringUtil.isNullOrEmpty(usersList.toString())) {
                Hql = Hql + " and c.userID in (" + usersList + ")";
            }
            Hql = Hql + " order by c.firstName";
            ll = executeQuery(Hql, new Object[]{companyid});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmUserDAOImpl.getOwner", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject getSubOrdinateUsers( HashMap<String,Object> requestParams,StringBuffer usersList) throws ServiceException {
        List ll = null;
        int dl = 0;
        int start = 0;
        int limit = 0;
        try {
            ArrayList filter_params = new ArrayList();
            filter_params.add(requestParams.get("companyid").toString());
            if (requestParams.containsKey("start") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
                start = Integer.parseInt(requestParams.get("start").toString());
            }
            if (requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("limit").toString())) {
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            
            String Hql = "select c from User c where  c.company.companyID = ? and c.deleteflag = 0 ";
            if(usersList !=null && !StringUtil.isNullOrEmpty(usersList.toString())) {
                Hql = Hql + " and c.userID in (" + usersList + ")";
            }

            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"c.firstName","c.lastName"};
                    StringUtil.insertParamSearchString(filter_params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    Hql +=searchQuery;
                }
            }

            Hql = Hql + " order by c.firstName";
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();

            if (requestParams.containsKey("start") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())
                    && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("limit").toString())) {
                ll = executeQueryPaging(Hql, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmUserDAOImpl.getSubOrdinateUsers", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
}
