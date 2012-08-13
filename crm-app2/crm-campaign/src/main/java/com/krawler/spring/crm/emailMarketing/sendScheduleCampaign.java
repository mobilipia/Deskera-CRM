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

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.database.tables.scheduledmarketing;
import com.krawler.esp.handlers.APICallHandlerService;
import com.krawler.esp.handlers.Receiver;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.esp.web.resource.Links;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class sendScheduleCampaign extends MultiActionController {
    private crmEmailMarketingDAO crmEmailMarketingDAOObj;
    private HibernateTransactionManager txnManager;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private bounceHandlerImpl bounceHandlerImplObj;
    private APICallHandlerService apiCallHandlerService;

    public void setApiCallHandlerService(APICallHandlerService apiCallHandlerService)
    {
        this.apiCallHandlerService = apiCallHandlerService;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
            this.txnManager = txManager;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }
    
    public void setcrmEmailMarketingDAO(crmEmailMarketingDAO crmEmailMarketingDAOObj1) {
        this.crmEmailMarketingDAOObj = crmEmailMarketingDAOObj1;
    }

    public void setbounceHandlerImpl(bounceHandlerImpl bounceHandlerImplObj) {
        this.bounceHandlerImplObj = bounceHandlerImplObj;
    }

    public ModelAndView checkBounceStatus(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            myjobj.put("success", false);
            kmsg = bounceHandlerImplObj.checkBounceStatus();
            myjobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
     public ModelAndView sendScheduleCampaign(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject result = null;
        //Create transaction
        try {
            String platformUrl = ConfigReader.getinstance().get("platformURL",null);
            jobj.put("success", false);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
//            requestParams.put("filter_names",Arrays.asList("DATEDIFF(scheduleddate, NOW())"));
//            requestParams.put("filter_values",Arrays.asList(0));
            result = crmEmailMarketingDAOObj.getScheduleEmailMarketing(requestParams);
            List list = result.getEntityList();
            Date now = new Date();
            Iterator ite = list.iterator();
            while(ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                int hrs = now.getHours();
                int mins = now.getMinutes();
//                String[] sTime = sm.getScheduledtime().split(":");
                String[] sTime = row[1].toString().split(":");
                if(Integer.parseInt(sTime[0]) == hrs && ((Integer.parseInt(sTime[1]) == 30 && mins >= 30) || (Integer.parseInt(sTime[1]) == 0 && mins < 30)) )
                {
                    scheduledmarketing sm = (scheduledmarketing) KwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.scheduledmarketing", row[0].toString());
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("userid", sm.getUserid().getUserID());
                    requestParams.put("companyid", sm.getUserid().getCompany().getCompanyID());
                    requestParams.put("subdomain", sm.getUserid().getCompany().getSubDomain());
                    requestParams.put("campaignid", sm.getEmailmarketingid().getCampaignid().getCampaignid());
                    requestParams.put("emailmarkid", sm.getEmailmarketingid().getId());
                    if(platformUrl==null) {
                        String loginUrl = URLUtil.getDomainURL(sm.getUserid().getCompany().getSubDomain(), false);
                        requestParams.put("baseurl", loginUrl);
                        sendScheduleCampaign(requestParams);
                    } else {
                        String companyid = sm.getUserid().getCompany().getCompanyID();
                        JSONObject temp = new JSONObject();
                        temp.put("companyid", companyid);
                        apiCallHandlerService.callApp(platformUrl, temp, companyid, "13", false, sendScheduleCampaignReceiver(requestParams));
                    }
                }
            }
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    private void sendScheduleCampaign(HashMap<String, Object> requestParams) {
        try {
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.sendEmailMarketMail(requestParams);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private Receiver sendScheduleCampaignReceiver(HashMap<String, Object> requestParams) {
        return new Receiver() {
            private HashMap<String, Object> requestParams = null;
            public Receiver setValues(HashMap<String, Object> requestParams) {
                this.requestParams = requestParams;
                return this;
            }
            @Override
            public void receive(Object resultObj) {
                JSONObject jobj = null;
                if (resultObj instanceof JSONObject) {
                    jobj = (JSONObject) resultObj;
                }
                try {
                    String loginUrl = ConfigReader.getinstance().get("crmURL",null);
                    if(jobj.has(com.krawler.common.util.Constants.CRMURL) && !StringUtil.isNullOrEmpty(jobj.getString(com.krawler.common.util.Constants.CRMURL))) {
                        loginUrl = jobj.getString(com.krawler.common.util.Constants.CRMURL);
                    }
                    requestParams.put("baseurl", StringUtil.appendSubDomain(loginUrl, requestParams.get("subdomain").toString(), false));
                    sendScheduleCampaign(requestParams);
                } catch (Exception ex) {
                    logger.warn("Cannot store isFree: " + ex.toString());
                }
            }
        }.setValues(requestParams);
    }
     
      public ModelAndView unsubscribeUserMarketMail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String htmlString = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><link rel=\"shortcut icon\" href=\"../../images/deskera/deskera.png\"/></head>";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("trackid", request.getParameter("trackid"));
            requestParams.put("baseurl", URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull));
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.unsubscribeUserMarketMail(requestParams);
            htmlString += kmsg.getEntityList().get(0).toString();
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            htmlString = "<div style='font-color:red;'><b>Failed to process request</b></div>";
            txnManager.rollback(status);
        } finally {
            htmlString += "</html>";
        }
        return new ModelAndView("chartView", "model", htmlString);
    }

       public ModelAndView subscribeUserMarketMail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String htmlString = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><link rel=\"shortcut icon\" href=\"../../images/deskera/deskera.png\"/></head>";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("trackid", request.getParameter("trackid"));
            requestParams.put("baseurl", URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull));
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.subscribeUserMarketMail(requestParams);
            htmlString += kmsg.getEntityList().get(0).toString();
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            htmlString = "<div style='font-color:red;'><b>Failed to process request</b></div>";
            txnManager.rollback(status);
        } finally {
            htmlString += "</html>";
        }
        return new ModelAndView("chartView", "model", htmlString);
    }

    public ModelAndView confirmSubscribeUserMarketMail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String htmlString = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><link rel=\"shortcut icon\" href=\"../../images/deskera/deskera.png\"/></head>";
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("trackid", request.getParameter("trackid"));
            requestParams.put("baseurl", URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull));
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.confirmsubscribeUserMarketMail(requestParams);
            htmlString += kmsg.getEntityList().get(0).toString();
            txnManager.commit(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            htmlString = "<div style='font-color:red;'><b>Failed to process request</b></div>";
            txnManager.rollback(status);
        } finally {
            htmlString += "</html>";
        }
        return new ModelAndView("chartView", "model", htmlString);
    }
}
