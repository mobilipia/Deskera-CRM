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
import com.krawler.common.service.ServiceException;
import com.krawler.esp.utils.ConfigReader;

import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONObject;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class projectIntController extends MultiActionController {
    private String successView;
    private HibernateTransactionManager txnManager;
    private projectIntDAO projectIntDAOObj;
    
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setprojectIntDAO(projectIntDAO projectIntDAOObj1) {
        this.projectIntDAOObj = projectIntDAOObj1;
    }
    
    public ModelAndView saveAccountProject(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String projectName = request.getParameter("projectName");
            String accId = request.getParameter("accId");
            //String appURL = request.getSession().getServletContext().getInitParameter("projectManagementUrl");
            String appURL = ConfigReader.getinstance().get("projectManagementURL");
            jobj.put("projectName", projectName);
            jobj.put("companyid", companyid);
            jobj.put("userid", userid);
            jobj.put("accId", accId);
            jobj.put("appURL", appURL);
            kmsg = projectIntDAOObj.saveAccountProject(jobj);
            jobj = (JSONObject) kmsg.getEntityList().get(0);
            txnManager.commit(status);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
}
