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
package com.krawler.spring.crm.common;
import com.krawler.common.util.SystemUtil;
import com.krawler.esp.handlers.zohoRequestHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.io.IOException;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.web.servlet.ModelAndView;

public class crmZohoImportController extends MultiActionController {
    private String successView;
    private HibernateTransactionManager txnManager;
    private zohoRequestHandler zohoRequestHandlerObj;

    public void setzohoRequestHandler(zohoRequestHandler zohoRequestHandlerObj1) {
        this.zohoRequestHandlerObj = zohoRequestHandlerObj1;
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

    public ModelAndView importZohoRecords(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        JSONObject obj = new JSONObject();
        JSONObject jobj = new JSONObject();
//        //Create transaction
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setName("JE_Tx");
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
//        TransactionStatus status = txnManager.getTransaction(def);
        try {
            boolean lead = Boolean.parseBoolean(request.getParameter("zleads"));
            boolean account = Boolean.parseBoolean(request.getParameter("zaccounts"));
            boolean contacts = Boolean.parseBoolean(request.getParameter("zcontacts"));
            boolean potential =Boolean.parseBoolean( request.getParameter("zpotentials"));
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            //String apikey = request.getParameter("apikey");
            String authToken = request.getParameter("authtoken");
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String ipAddress = SystemUtil.getIpAddress(request);
            String tzDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            String parterName = sessionHandlerImpl.getPartnerName(request);
            String sysEmailId = sessionHandlerImpl.getSystemEmailId(request);
            zohoRequestHandlerObj.add(username, password, authToken, lead, account, potential, contacts, userid, companyid,ipAddress, tzDiff,parterName,sysEmailId);
            if (!zohoRequestHandlerObj.isIsWorking()) {
                Thread t = new Thread(zohoRequestHandlerObj);
                t.start();
            }
//        zohoRequestHandlerObj.run();
            
            jobj.put("success", true);
//            txnManager.commit(status);
        } catch (Exception e) {
//            txnManager.rollback(status);
            logger.warn(e.getMessage(),e);
        } finally {
            
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView fetchLogs(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        String result = "";
        try {
            String companyid1 = sessionHandlerImpl.getCompanyid(request);
            result = zohoRequestHandlerObj.fetchLogs(companyid1);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        } finally {

        }
        return new ModelAndView("jsonView", "model", result);
    }
}
