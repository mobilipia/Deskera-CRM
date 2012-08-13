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

package com.krawler.crm.notification.web;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

import com.krawler.esp.handlers.APICallHandlerService;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.esp.utils.ConfigReader;



import com.krawler.spring.sessionHandler.sessionHandlerImpl;

import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.utils.json.base.JSONArray;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class NotificationController extends MultiActionController {
    private HibernateTransactionManager txnManager;
    private APICallHandlerService apiCallHandlerService;
    private String successView;

    public void setApiCallHandlerService(APICallHandlerService apiCallHandlerService) {
        this.apiCallHandlerService = apiCallHandlerService;
    }

   public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
		return successView;
	}

    public void setSuccessView(String successView) {
		this.successView = successView;
    }

     public ModelAndView getMaintainanceDetails(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        boolean issuccess = false;
        String msg = "";
        try{
            JSONArray jarr=null;
            //String platformURL=this.getServletContext().getInitParameter("platformURL");
            String platformURL = ConfigReader.getinstance().get("platformURL");
            String crmURL = ConfigReader.getinstance().get("crmURL");
            //String crmURL=this.getServletContext().getInitParameter("crmURL");
            String action = "9";
            String companyID = sessionHandlerImpl.getCompanyid(request);
            JSONObject userData = new JSONObject();
            userData.put("remoteapikey",StorageHandler.GetRemoteAPIKey());
            userData.put("companyid",sessionHandlerImpl.getCompanyid(request));
            userData.put("requesturl",crmURL);
            JSONObject resObj = apiCallHandlerService.callApp(platformURL, userData, companyID, action, false);
            if (!resObj.isNull("success") && resObj.getBoolean("success")) {
                jarr=resObj.getJSONArray("data");
            }
            if (jarr!=null&&jarr.length()>0) {
                jobj.put("data", jarr);
                msg="Data fetched successfully";
                issuccess = true;
            } else {
                msg="Error occurred while fetching data ";
            }
        } catch (Exception ex){
            logger.warn("Error occured", ex);
         } finally {
            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
            } catch (JSONException ex) {
                logger.warn("cannot create json object", ex);
            }
        }
        return new ModelAndView("jsonView","model", jobj.toString());
    }
}
