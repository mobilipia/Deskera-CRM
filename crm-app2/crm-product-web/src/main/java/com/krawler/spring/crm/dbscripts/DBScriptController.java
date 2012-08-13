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
package com.krawler.spring.crm.dbscripts;

import com.krawler.common.admin.Company;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.esp.utils.CompanyContextHolder;
import com.krawler.esp.utils.CompanyRoutingDataSource;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.companyDetails.companyDetailsDAO;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class DBScriptController  extends MultiActionController {

    private static Log logger = LogFactory.getLog(DBScriptController.class);
    private fieldManagerDAO fieldManagerDAOobj;
    private CompanyRoutingDataSource routingDataSource;
    private HibernateTransactionManager txnManager;
    private companyDetailsDAO companyDetailsDAOObj;

    public void setcompanyDetailsDAO(companyDetailsDAO companyDetailsDAOObj1) {
        this.companyDetailsDAOObj = companyDetailsDAOObj1;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setRoutingDataSource(CompanyRoutingDataSource routingDataSource) {
        this.routingDataSource = routingDataSource;
    }

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    private void callURL(URL url, StringBuffer params) throws IOException {
        java.io.BufferedReader in = null;

        try {
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
            uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            java.io.PrintWriter pw = new java.io.PrintWriter(uc.getOutputStream());
            pw.println(params);
            pw.close();
            try {
                in = new java.io.BufferedReader(new java.io.InputStreamReader(uc.getInputStream()));
                String res = URLDecoder.decode(in.readLine(), "UTF-8");
            } catch (Exception ex) {
            }
            in.close();
        } catch (IOException ex) {
            logger.warn("Diversion not possible for " + url + " [" + params + "]", ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public ModelAndView validateRecords(HttpServletRequest request,
            HttpServletResponse response) {
        try {
            Collection<String> subdomains = routingDataSource.getOneCompanyPerDataSource();
            StringBuffer url = request.getRequestURL();
            int idx = url.indexOf("/validateRecords");
            URL u = new URL(url.replace(idx, idx + 16, "/validateRecordForParticularDB").toString());
            if(request.getParameter("cdomain")!=null||request.getParameter("cdomain").length()>=0) {
                StringBuffer pramsStr = new StringBuffer();
                appendParam(pramsStr, "cdomain", request.getParameter("cdomain"));
                callURL(u, pramsStr);
            } else {
                for (String subdomain : subdomains) {
                    StringBuffer pramsStr = new StringBuffer();
                    appendParam(pramsStr, "cdomain", subdomain);
                    callURL(u, pramsStr);
                }
            }
        } catch (Exception ex) {
            logger.debug("DBScriptController.validateRecords : " + ex.getMessage());
        }
        return new ModelAndView("jsonView", "model", "{success:true}");
    }

    private void appendParam(StringBuffer url, String key, String value) throws UnsupportedEncodingException {
        StringBuffer paramStr = new StringBuffer();
        paramStr.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"));
        if (url.length() > 0) {
            url.append("&");
        }
        url.append(paramStr);
    }

    public ModelAndView validateRecordForParticularDB(HttpServletRequest request,
            HttpServletResponse response) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String[] moduleNames = {Constants.MODULE_ACCOUNT, Constants.MODULE_CONTACT, Constants.MODULE_LEAD, Constants.MODULE_OPPORTUNITY, Constants.MODULE_PRODUCT, Constants.Crm_Case_modulename, Constants.MODULE_Campaign};
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.deleted");
            filter_params.add(0);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            KwlReturnObject kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams);
            List<Company> companyList = kmsg.getEntityList();
            for (Company company : companyList) {
                for (int cnt = 0; cnt < moduleNames.length; cnt++) {
                    try {
                        requestParams.clear();
                        requestParams.put("modulename", moduleNames[cnt]);
                        requestParams.put("validflag", 1);
                        requestParams.put("companyid", company.getCompanyID());
                        fieldManagerDAOobj.validateimportrecords(requestParams);
                    } catch(Exception ex) {
                        System.out.print(ex.getMessage());
                        logger.debug("DBScriptController.validateRecordForParticularDB for companyid: "+company.getCompanyID()+": " + ex.getMessage());
                    }
                }
            }
            txnManager.commit(status);
        } catch (Exception ex) {
            logger.debug("DBScriptController.validateRecordForParticularDB : " + ex.getMessage());
            txnManager.rollback(status);
        } finally {

        }
        return new ModelAndView("jsonView", "model", "{success:true}");
    }
}
