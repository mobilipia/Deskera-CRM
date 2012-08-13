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
package com.krawler.spring.crm.spreadSheet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.admin.SpreadSheetConfig;
import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;

import com.krawler.common.util.StringUtil;
import com.krawler.crm.spreadsheet.bizservice.SpreadsheetService;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class spreadSheetController extends MultiActionController {
    private String successView;
    private HibernateTransactionManager txnManager;
    private sessionHandlerImpl sessionHandlerImplObj;
    private SpreadsheetService spreadsheetService;
    
    private static Log LOG = LogFactory.getLog(spreadSheetController.class);

    private CometManagementService CometManagementService;
    public void setCometManagementService(CometManagementService CometManagementService) {
        this.CometManagementService = CometManagementService;
    }
    
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImpl1) {
        this.sessionHandlerImplObj = sessionHandlerImpl1;
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

	public void setSpreadsheetService(SpreadsheetService spreadsheetService) {
		this.spreadsheetService = spreadsheetService;
	}
	
    public ModelAndView getSpreadsheetConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException, JSONException
    {
        JSONObject jobj = new JSONObject("{cid:0,rules:{rules:[]},state:{columns:false}}");
        try
        {
            String module = request.getParameter("module");
            String userId = sessionHandlerImpl.getUserid(request);
            SpreadSheetConfig config = spreadsheetService.getSpreadsheetConfig(module, userId);
            if (config != null){
            	jobj.put("cid",config.getCid());
                if (!StringUtil.isNullOrEmpty(config.getRules())){
                	jobj.put("rules",new JSONObject(config.getRules()));
                }
                if (!StringUtil.isNullOrEmpty(config.getState())){
                	jobj.put("state",new JSONObject(config.getState()));
                }
            }

        } catch (Exception e){
            LOG.warn("Can't load spreadsheet config", e);
        }
        return new ModelAndView("jsonView", "model", "{success:true,data:["+jobj.toString()+"]}");
    }
    
    public ModelAndView saveSpreadsheetConfig(HttpServletRequest request, HttpServletResponse response) throws ServletException, JSONException
    {
        JSONObject jobj = new JSONObject("{cid:0,rules:{rules:[]},state:{columns:false}}"), jobj1=new JSONObject();
        try
        {
        String module = request.getParameter("module");
        String cid = request.getParameter("cid");
        String userId = sessionHandlerImpl.getUserid(request);
        String rule = request.getParameter("rules");
        String state = request.getParameter("state");
        SpreadSheetConfig config = spreadsheetService.saveSpreadsheetConfig(module, cid, userId, rule, state);
        if (config != null){
        	jobj.put("cid",config.getCid());
            if (!StringUtil.isNullOrEmpty(config.getRules())){
            	jobj.put("rules",new JSONObject(config.getRules()));
            }
            if (!StringUtil.isNullOrEmpty(config.getState())){
            	jobj.put("state",new JSONObject(config.getState()));
            }
        }
        jobj1.put("data", new JSONArray().put(jobj));
        jobj1.put("success", true);
        // publish spread-sheet config
        JSONObject cometObj = jobj1;
        publishSpreadSheetInformation(request, cometObj, module, CrmPublisherHandler.USERSPREADSHEETCONFIG, sessionHandlerImplObj.getCompanyid(request));

        }
        catch (Exception e){
            jobj1.put("data", new JSONArray());
            jobj1.put("success", false);
            LOG.info(e.getMessage(), e);
        }

        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    private void publishSpreadSheetInformation(HttpServletRequest request, JSONObject cometObj, String moduleName, int operationCode, String companyid) throws SessionExpiredException {
        String userid = sessionHandlerImplObj.getUserid(request);
        String randomNumber = request.getParameter("randomnumber");
        CometManagementService.publishModuleInformation(companyid, userid, randomNumber, moduleName, "", operationCode, cometObj, CometConstants.CRMUPDATES);
    }
    
    public ModelAndView saveModuleRecordStyle(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	boolean success=false;
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            if (StringUtil.bNull(request.getParameter("jsondata"))) {
                String jsondata = request.getParameter("jsondata");
                JSONArray jarr = new JSONArray( jsondata );
                String classname = "com.krawler.crm.database.tables.Crm"+request.getParameter("module");
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jo = jarr.getJSONObject(i);
                    spreadsheetService.saveModuleRecordStyle(jo.getString("id"), classname, jo.getString("cellStyle"));
                }
            }
            txnManager.commit(status);
            success=true;
        } catch (Exception e) {
            LOG.warn(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", "{success:"+success+"}");
    }
}
