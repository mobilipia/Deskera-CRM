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
package com.krawler.spring.crm.sunrise;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.SunriseCalibration;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class calibrationDAOImpl  extends BaseDAO implements calibrationDAO {

    @Override
    public KwlReturnObject saveCallibrationRecord(JSONObject jobj) throws ServiceException {
        JSONObject myjobj = new JSONObject();
        List ll = new ArrayList();
        int dl = 0;
        try {
            SunriseCalibration sunriseCalibration = null;
            String userid = null;
            boolean isNew = false;
            if(jobj.has("id")) {
                String ID = jobj.getString("id");
                if(ID.equals("0")) {
                    sunriseCalibration = new SunriseCalibration();
                    sunriseCalibration.setId(StringUtil.generateUUID());
                    isNew = true;
                } else {
                    sunriseCalibration = (SunriseCalibration) get(SunriseCalibration.class, ID);
                }
            }
            if(jobj.has("calon")) {
                sunriseCalibration.setCalon(jobj.getLong("calon"));
            }
            if(jobj.has("caldue")) {
                sunriseCalibration.setCaldue(jobj.getLong("caldue"));
            }
            if(jobj.has("particulars")) {
                sunriseCalibration.setParticulars(jobj.getString("particulars"));
            }
            if(jobj.has("srcal")) {
                sunriseCalibration.setSrcal(jobj.getString("srcal"));
            }
            if(jobj.has("contactperson")) {
                sunriseCalibration.setContactperson(jobj.getString("contactperson"));
            }
            if(jobj.has("contactnumber")) {
                sunriseCalibration.setContactnumber(jobj.getString("contactnumber"));
            }
            if(jobj.has("machinetype")) {
                sunriseCalibration.setMachinetype(jobj.getString("machinetype"));
            }
            if(jobj.has("paymentstatus")) {
                sunriseCalibration.setPaymentstatus(jobj.getString("paymentstatus"));
            }
            if(jobj.has("machinecalno")) {
                sunriseCalibration.setMachinecalno(jobj.getString("machinecalno"));
            }
            if(jobj.has("state")) {
                sunriseCalibration.setState(jobj.getString("state"));
            }

            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                sunriseCalibration.setUsersByUpdatedbyid((User) get(User.class, userid));
                if(isNew)
                    sunriseCalibration.setUsersByCreatedbyid((User) get(User.class, userid));
            }
            
            sunriseCalibration.setUpdatedOn(new Date().getTime());
            
            if(jobj.has("createdon") && isNew) {
                sunriseCalibration.setCreatedOn(new Date().getTime());
            }
            save(sunriseCalibration);
            ll.add(sunriseCalibration);
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("clientCustomizationDAOImpl.saveCallibrationRecord : " + ex.getMessage(), ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getSunriseClientCallibrationData(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        Long dl = 0l;
        String ss="";
        try {
             
            String selCountQuery = "select count(c.id) ";
            String selQuery = "select c ";
            String Hql = "from SunriseCalibration c ";
            String filterQuery = "where c.deleteflag= 0 ";
            if(requestParams.get("ss")!=null)
            {
             ss = requestParams.get("ss").toString();
            }
            
            int start = 0;
            int limit = 25;
            boolean ispaging = requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()) && !StringUtil.isNullOrEmpty(requestParams.get("start").toString());
            if (ispaging) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            
            String countQuery = selCountQuery + Hql + filterQuery;
            String query = selQuery + Hql + filterQuery;
            
            ArrayList filter_params = new ArrayList();
            if (!StringUtil.isNullOrEmpty(ss)) {
                StringUtil.insertParamSearchString(filter_params, ss, 2);
                String searchQuery = StringUtil.getSearchString(ss, "and", new String[]{"c.particulars", "c.contactperson"});
                query +=searchQuery;
            }
            String orderQuery = " order by c.createdOn desc ";
            if(requestParams.containsKey("field")) {
                String dbname = crmManagerCommon.getFieldDbName(requestParams);
                if(dbname!=null){
                    String dir = requestParams.get("direction").toString();
                    orderQuery = " order by "+dbname+" "+dir+" ";
                }
            }
            
            ll = executeQuery(countQuery);
            if (ll != null && !ll.isEmpty()) {
                dl = (Long) ll.get(0);
            }
            ll = executeQueryPaging(query+orderQuery,filter_params.toArray() , new Integer[]{start, limit});
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("clientCustomizationDAOImpl.getSunriseClientCallibrationData : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, "002", "", ll, dl.intValue());
    }

	@Override
	public KwlReturnObject deleteCallibrationRecord(String []ids) throws ServiceException {
			boolean successFlag = false;
			String resultmsg="";
			String id="";
			
	        try {
	            String HQL = "update SunriseCalibration set deleteflag = 1 where id in (:ids)";
	            Map<String, Object> map = new HashMap<String, Object>();
	            map.put("ids", ids);
	            executeUpdate(HQL,null, map);
	            
	            resultmsg = "Selected record deleted successfully.";

	        } catch (Exception e) {
	            logger.warn(e.getMessage(), e);
	            throw ServiceException.FAILURE("calibrationDAOImpl.deleteCallibrationRecord : "+e.getMessage(), e);
	        }
	      
	        successFlag = true;
	        return new KwlReturnObject(successFlag, resultmsg, "", null, 0);
	}
	
	public KwlReturnObject getCalibration(String []ids) throws ServiceException {
		List ll = null;
        try {
            String HQL = "from SunriseCalibration where id in (:ids)";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ids", ids);
            ll = executeQuery(HQL,null, map);
        
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("calibrationDAOImpl.getCalibration : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, "002", "", ll, 0);
	}
     
}
