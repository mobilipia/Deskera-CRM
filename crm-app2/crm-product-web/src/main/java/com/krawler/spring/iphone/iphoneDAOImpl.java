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

package com.krawler.spring.iphone;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.crm.database.tables.iDeskeraCrmAuth;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
/**
 *
 * @author krawler
 */
public class iphoneDAOImpl extends BaseDAO implements iphoneDAO {
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public KwlReturnObject generateAppID(HashMap<String, Object> requestParams) throws ServiceException,JSONException {
       int dl = 0;
       List ll = new ArrayList();
       JSONObject obj= new JSONObject();
       try{
            String appId = UUID.randomUUID().toString();
            String userid = requestParams.get("userid").toString();
            String domain = requestParams.get("domain").toString();
            iDeskeraCrmAuth idesk = new iDeskeraCrmAuth();
            idesk.setAppid(appId);
            User user = (User) KwlCommonTablesDAOObj.getObject("com.krawler.common.admin.User",userid );
            idesk.setUser(user);
            idesk.setDeviceid(requestParams.get("deviceid").toString());
            save(idesk);
            obj.put("success", true);
            obj.put("userid", userid);
            obj.put("appid", appId);
            obj.put("domain", domain);
       }catch (Exception e) {
            logger.warn(e.getMessage(), e);
            obj.put("success", false);
            obj.put("error", "Error occurred while authentication");
            throw ServiceException.FAILURE("iphoneDAOImpl.generateAppID", e);
        }finally{
            ll.add(obj);
        }

       return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    public String getLoginDateFormate(String userId)  {
        String dateFormate="";
        List ll = new ArrayList();
        try{
        	String hql="select u.dateFormat.javaForm from User u where u.userID=?";
        	ll=executeQuery(hql,new Object[]{userId});
        	dateFormate=(String)ll.get(0);
            
        }catch (Exception e) {
             logger.warn(e.getMessage(), e);
             
         }

        return dateFormate;
     }

    
}
