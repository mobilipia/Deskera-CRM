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
package com.krawler.esp.handlers;

import com.krawler.common.admin.Apiresponse;
import com.krawler.common.admin.Company;
import com.krawler.utils.json.base.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class APICallHandlerServiceImpl implements APICallHandlerService
{
    private APICallHandlerDAO apiCallHandlerDAO;
    private static Log logger = LogFactory.getLog(APICallHandlerServiceImpl.class);

    private static String API_STRING = "remoteapi.jsp";

    private Apiresponse makePreEntry(String companyid, String requestData, String action){
        Apiresponse apires = new Apiresponse();
        apires.setApiid(UUID.randomUUID().toString());
        apires.setCompanyid((Company) apiCallHandlerDAO.get(Company.class, companyid));
        apires.setApirequest("action=" + action + "&data=" + requestData);
        apires.setStatus(0);
        apiCallHandlerDAO.save(apires); 
        return apires;
    }
    
    private void makePostEntry(Apiresponse apires, String responseData) {
        apires.setApiresponse(responseData);
        apires.setStatus(1);
        apiCallHandlerDAO.saveOrUpdate(apires);
	}
    
    @Override
    public JSONObject callApp(String appURL, JSONObject jData, String companyid, String action, boolean storeCall)
    {
        	String requestData = (jData==null?"":jData.toString());
        	
        	Apiresponse apires = null;
        	if(storeCall){
        		apires = makePreEntry(companyid, requestData, action);
        	}
            String res = "{success:false}";
            InputStream iStream = null;
            String strSandbox = appURL + API_STRING;
            try
            {               
                URL u = new URL(strSandbox);
                URLConnection uc = u.openConnection();
                uc.setDoOutput(true);
                uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                java.io.PrintWriter pw = new java.io.PrintWriter(uc.getOutputStream());
                pw.println("action=" + action + "&data=" + URLEncoder.encode(requestData,"UTF-8"));
                pw.close();
                iStream = uc.getInputStream();
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(iStream));
                res = URLDecoder.decode(in.readLine(),"UTF-8");
                in.close();
                iStream.close();
            } catch (IOException iex) {
                logger.warn("Remote API call for '"+strSandbox+"' failed because " +iex.getMessage());                
            } finally {
                if (iStream != null) {
                    try {
                        iStream.close();
                    } catch (Exception e) {
                    }
                }
            }
            JSONObject resObj;
            try{
                resObj = new JSONObject(res);
            }catch(Exception ex){
            	logger.warn("Improper response from Remote API: " + res);
                resObj = new JSONObject();
                try{resObj.put("success", false);}catch(Exception e){}
            }
            
            if(storeCall){
            	makePostEntry(apires, res);
            }


        return resObj;
    }

    /**
     * @param apiCallHandlerDAO
     *            the apiCallHandlerDAO to set
     */
    public void setApiCallHandlerDAO(APICallHandlerDAO apiCallHandlerDAO)
    {
        this.apiCallHandlerDAO = apiCallHandlerDAO;
    }

    @Override
    public void callApp(String appURL, JSONObject jData, String companyid, String action, boolean storeCall, Receiver receiver) {
        new Thread(){
            private String appURL;
            private JSONObject jData;
            private String companyid;
            private String action;
            private boolean storeCall;
            private Receiver receiver ;
            public Thread setValues(String appURL, JSONObject jData, String companyid, String action, boolean storeCall, Receiver receiver){
                this.appURL = appURL;
                this.jData = jData;
                this.companyid = companyid;
                this.action = action;
                this.receiver = receiver;
                this.storeCall = storeCall;
                return this;
            }
            @Override
            public void run(){
                receiver.receive(callApp(appURL, jData, companyid, action, storeCall));
            }
        }.setValues(appURL, jData, companyid, action, storeCall, receiver).start();
    }
}
