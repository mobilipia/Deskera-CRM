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

package com.krawler.spring.crm.emailMarketing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.EmailMarketing;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class crmEmailMarketingHandler {
    private static final Log logger = LogFactory.getLog(crmEmailMarketingHandler.class);
    public static List<EmailMarketing> getCampEmailMarketList(crmEmailMarketingDAO crmEmailMarketingDAOObj,String templateid) throws ServiceException {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
           // requestParams.put("start", 0);
          //  requestParams.put("limit", 1);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("em.templateid.templateid");
            filter_params.add(templateid);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getCampEmailMarketList(requestParams);
            return kmsg.getEntityList();
    }
    public static JSONObject getcampEmailMarketingStatusJson(List ll, int totalSize) {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        boolean isExists = false;
        try {
            Iterator ite = ll.iterator();
            int firstlist = totalSize;
            for (int cnt=0;cnt<firstlist; cnt++) {
                Object row[] = (Object[]) ite.next();
                String obj = (String) row[0];
                String obj2 = (String) row[1];
                Long obj3 = Long.parseLong( row[2].toString());
                Long obj4 = Long.parseLong( row[3].toString());
                String marketingId = (String) row[4];
                Long usercount = Long.parseLong( row[5].toString());
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("marketingid", marketingId);
                tmpObj.put("marketingname", obj);
                tmpObj.put("campaignname", obj2);
                tmpObj.put("viewed", obj3);
                tmpObj.put("sentmail", obj4);
                tmpObj.put("usercount", usercount);
                jarr.put(tmpObj);
            }

            while (ite.hasNext()) {
                Object row[] = (Object[]) ite.next();
                String obj = (String) row[0];
                String obj2 = (String) row[1];
                Long obj3 = Long.parseLong(row[2].toString());
                Long obj4 = Long.parseLong(row[3].toString());
                String marketingId = (String) row[4];
                Long usercount = Long.parseLong( row[5].toString());

                isExists = isAlreadyExists(jarr, marketingId);
                if (!isExists) {
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("marketingid", marketingId);
                    tmpObj.put("marketingname", obj);
                    tmpObj.put("campaignname", obj2);
                    tmpObj.put("viewed", obj3);
                    tmpObj.put("sentmail", obj4);
                    tmpObj.put("usercount", usercount);
                    jarr.put(tmpObj);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("campaignReport", true);
            jobj.put("totalCount", totalSize);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }

    public static boolean isAlreadyExists(JSONArray jarr, String id) throws ServiceException {
        boolean b = false;
        JSONObject jobj = new JSONObject();
        try {
            for(int i=0;i<jarr.length();i++) {
                jobj = jarr.getJSONObject(i);
                if (jobj.getString("marketingid").equals(id)) {
                    b = true;
                }
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.isAlreadyExists", e);
        }
        return b;
    }

}
