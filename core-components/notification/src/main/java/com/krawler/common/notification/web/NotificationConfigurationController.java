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
package com.krawler.common.notification.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.krawler.common.admin.User;
import com.krawler.common.notification.bizservice.NotificationConfigurationService;
import com.krawler.common.notification.tables.NotificationSetting;
import com.krawler.common.query.Clause;
import com.krawler.common.query.FilterClause;
import com.krawler.common.query.OrderClause;
import com.krawler.common.query.SimplePaging;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.notify.NotificationException;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class NotificationConfigurationController extends MultiActionController {

	private NotificationConfigurationService notificationConfigService;

	private HibernateTransactionManager txnManager;
	private sessionHandlerImpl sessionHandlerImplObj;

	public void setNotificationConfigService(NotificationConfigurationService service) {
		this.notificationConfigService = service;
	}

	public void setTxnManager(HibernateTransactionManager txManager) {
		this.txnManager = txManager;
	}

	public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
		this.sessionHandlerImplObj = sessionHandlerImplObj;
	}

	// Save and Update Notification Settings.
	public ModelAndView saveEmailSetting(HttpServletRequest request, HttpServletResponse response) throws Exception {
		boolean success = false;
		try {
			User user = sessionHandlerImplObj.getUser(request);
			String settingid = request.getParameter("settingid");
			String host = request.getParameter("server");
			String port = request.getParameter("port");
			String contact = request.getParameter("contact");
			String slayer = request.getParameter("slayer");
			String protocol = request.getParameter("protocol");
			String username = null;
			String password = null;
			if(Boolean.parseBoolean(request.getParameter("auth"))){
				username = request.getParameter("user");
				password = request.getParameter("pass");				
			}
			notificationConfigService.saveEmailSetting(settingid, user, contact, host, port, slayer, protocol,username, password);
			success=true;
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}

		return new ModelAndView("jsonView", "model", "{success:"+success+"}");
	}

	// Sending test mail for checking Notification settings...
	public ModelAndView sendCustomSettingTestMail(HttpServletRequest request, HttpServletResponse response) throws ServletException, SessionExpiredException, JSONException {
		JSONObject testjobj = new JSONObject();
		try {
			User user = sessionHandlerImplObj.getUser(request);
			String host = request.getParameter("server");
			String port = request.getParameter("port");
			String contact = request.getParameter("contact");
			String slayer = request.getParameter("slayer");
			String protocol = request.getParameter("protocol");
			String username = null;
			String password = null;
			if(Boolean.parseBoolean(request.getParameter("auth"))){
				username = request.getParameter("user");
				password = request.getParameter("pass");				
			}
			notificationConfigService.testEmailSetting(user, contact, host, port, slayer, protocol,username, password);
			testjobj.put("success", true);
			
		} catch (NotificationException e) {
			logger.warn(e.getMessage(), e);
			testjobj.put("success", false);
			testjobj.put("message", e.getMessage());
		} 

		return new ModelAndView("jsonView", "model", testjobj.toString());
	}

	//Retrieve NotificationSettings
	public ModelAndView getNotificationSettings(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONArray jarr = new JSONArray();
		JSONObject myjobj = new JSONObject();
		boolean successflag = false;
		try {

			List<NotificationSetting> result = null;
			ArrayList<Object> positionalParams = new ArrayList<Object>();
			String companyid = sessionHandlerImpl.getCompanyid(request);
			String userid = sessionHandlerImpl.getUserid(request);
			FilterClause filterclause = new FilterClause("n.user.userID=?");
			positionalParams.add(userid);
			filterclause.addFilter(FilterClause.AND, "n.user.company.companyID=?");
			positionalParams.add(companyid);
			filterclause.addFilter(FilterClause.AND, "n.deleted=?");
			positionalParams.add(false);

			String ss = request.getParameter(("ss"));
			if (!StringUtil.isNullOrEmpty(ss)) {
				filterclause.addFilter(FilterClause.AND, "(n.contact like ? or n.contact like ?)");
				positionalParams.add(Constants.percent+" "+ss+Constants.percent);
                positionalParams.add(ss+Constants.percent);
			}

			OrderClause orderclause = new OrderClause("n.contact");
			SimplePaging paging = null;
			if ((request.getParameter("start") != null) && (request.getParameter("limit") != null))

				paging = new SimplePaging(Integer.parseInt(request.getParameter("start")), Integer.parseInt(request.getParameter("limit")));

			result = notificationConfigService.getSettings(new Clause[] { filterclause, orderclause }, positionalParams, null, paging);
			if (result != null)
				successflag = true;

			Iterator ite = result.iterator();
			while (ite.hasNext()) {
				JSONObject jobj = new JSONObject();
				NotificationSetting settings = (NotificationSetting) ite.next();
				Map<String ,Object> tempmap = notificationConfigService.getPropertyMap(settings);
				jobj.put("id", settings.getId());
				jobj.put("email", settings.getContact());
				jobj.put("name", settings.getContact());
				if (tempmap != null && !tempmap.isEmpty()) {
					jobj.put("protocol", tempmap.get("protocol"));
					jobj.put("server", tempmap.get("host"));
					jobj.put("port", tempmap.get("port"));
					jobj.put("authenticate", tempmap.containsKey("user"));
					jobj.put("seclayer", tempmap.get("slayer"));
					jobj.put("username", tempmap.get("user"));
					jobj.put("password", tempmap.get("password"));
				}
				jarr.put(jobj);
			}

			myjobj.put("data", jarr);
			if(paging!=null)
				myjobj.put("totalcount", paging.getTotalCount());
			myjobj.put("success", successflag);
		} catch (JSONException e) {
			logger.warn(e.getMessage(), e);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
		return new ModelAndView("jsonView", "model", myjobj.toString());
	}

	// Delete Outbound Email Settings
	public ModelAndView deleteSettings(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject jobj = new JSONObject();

		// Create transaction
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		try {
			String[] ids = request.getParameterValues("ids");
			notificationConfigService.deleteSettings(ids);
			txnManager.commit(status);
		} catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
			txnManager.rollback(status);
		}
		return new ModelAndView("jsonView", "model", jobj.toString());
	}

}
