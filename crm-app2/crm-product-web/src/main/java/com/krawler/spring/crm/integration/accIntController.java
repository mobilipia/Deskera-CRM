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
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import java.text.ParseException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import com.krawler.crm.database.tables.Commission;
import com.krawler.crm.database.tables.UserCommissionPlan;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.common.remoteAPIController;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class accIntController extends MultiActionController {
    private String successView;
    private HibernateTransactionManager txnManager;
    private accIntDAO accIntDAOObj;
    private crmLeadDAO crmLeadDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmCommonDAO crmCommonDAOObj;
    private remoteAPIController remoteAPIControllerObj;

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setaccIntDAO(accIntDAO accIntDAOObj1) {
        this.accIntDAOObj = accIntDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

   
    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setcrmRemoteAPIController(remoteAPIController remoteAPIControllerObj1) {
        this.remoteAPIControllerObj = remoteAPIControllerObj1;
    }

    public ModelAndView getCommision(HttpServletRequest request, HttpServletResponse response) throws ServiceException, SessionExpiredException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("company.companyID");
            filter_names.add("deleted");
            filter_params.add(companyid);
            filter_params.add(0);

            kmsg = accIntDAOObj.getCommisionPlans(requestParams, filter_names, filter_params);
            Iterator itr = kmsg.getEntityList().iterator();
            JSONArray jArr=new JSONArray();
            while(itr.hasNext()) {
                Commission commision = (Commission) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("id", commision.getId());
                obj.put("planid", commision.getId());
                obj.put("name",  commision.getName());
                obj.put("value", commision.getValue());
                obj.put("ispercent", commision.getIsPercent());
                obj.put("goaltype", commision.getGoaltype());
                obj.put("goalperiod", commision.getGoalperiod());
                obj.put("target", commision.getTarget());
                jArr.put(obj);
            }
            jobj.put("data", jArr);

        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView userCommissionPlans(HttpServletRequest request, HttpServletResponse response) throws ServiceException, SessionExpiredException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            JSONArray jArr=new JSONArray();
            String userid = request.getParameter("userid");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("userid.userID");
            filter_names.add("deleted");
            filter_names.add("isactive");
            filter_params.add(userid);
            filter_params.add(0);
            filter_params.add(true);

            kmsg = accIntDAOObj.getUserCommisionPlans(requestParams, filter_names, filter_params);
            Iterator itr = kmsg.getEntityList().iterator();
            while(itr.hasNext()) {
                UserCommissionPlan userCommision = (UserCommissionPlan) itr.next();
                Commission commision = userCommision.getCommissionplan();
                JSONObject obj = new JSONObject();
                obj.put("id", userCommision.getId());
                obj.put("planid", commision.getId());
                obj.put("name",  commision.getName());
                obj.put("value", commision.getValue());
                obj.put("inpercent", commision.getIsPercent());
                obj.put("goaltype", commision.getGoaltype());
                obj.put("goalperiod", commision.getGoalperiod());
                obj.put("target", commision.getTarget());
                obj.put("year", userCommision.getPlany());
                jArr.put(obj);
            }
            jobj.put("data", jArr);

        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView viewUserCommission(HttpServletRequest request, HttpServletResponse response) throws ServiceException, SessionExpiredException, ParseException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            JSONArray jArr=new JSONArray();
            String userid = request.getParameter("userid");
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String planyear = request.getParameter("year");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("userid.userID");
            filter_names.add("plany");
            filter_names.add("deleted");
            filter_names.add("isactive");
            
            filter_params.add(userid);
            filter_params.add(planyear);
            filter_params.add(0);
            filter_params.add(true);

            kmsg = accIntDAOObj.getUserCommisionPlans(requestParams, filter_names, filter_params);
            Iterator itr = kmsg.getEntityList().iterator();
            while(itr.hasNext()) {
                UserCommissionPlan userCommision = (UserCommissionPlan) itr.next();
                Commission commision = userCommision.getCommissionplan();
                JSONObject obj = new JSONObject();
                obj.put("id", userCommision.getId());
                obj.put("planid", commision.getId());
                obj.put("name",  commision.getName());
                obj.put("value", commision.getValue());
                obj.put("ispercent", commision.getIsPercent());
                obj.put("goaltype", commision.getGoaltype());
                obj.put("goalperiod", commision.getGoalperiod());
                obj.put("target", commision.getTarget());
                obj.put("year", userCommision.getPlany());

                filter_params.clear();
                filter_names.clear();
                requestParams.clear();
                requestParams.put("distinctFlag", true);
                if(commision.getGoalperiod()==1) {  // for company FY
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date frmDate = sdf.parse(planyear + "-01-01");
                    Date toDate = sdf.parse(planyear + "-12-31");
                    if(commision.getGoaltype()==1) { // for lead count
                        filter_names.add("c.deleteflag");
                        filter_names.add("c.isarchive");
                        filter_names.add("c.company.companyID");
                        filter_names.add("c.validflag");
                        filter_names.add("lo.mainOwner");
                        filter_names.add("lo.usersByUserid.userID");
                        filter_params.add(0);
                        filter_params.add(false);
                        filter_params.add(companyid);
                        filter_params.add(1);
                        filter_params.add(true);
                        filter_params.add(userid);
                        filter_names.add(">=date(c.createdOn)");
                        filter_names.add("<=date(c.createdOn)");
                        filter_params.add(frmDate.getTime());
                        filter_params.add(toDate.getTime());
                        kmsg = crmLeadDAOObj.getLeadOwners(requestParams, filter_names, filter_params);

                        int dl = kmsg.getRecordTotalCount();
                        obj.put("achived", dl);
                        if(commision.getTarget()!=0)
                            obj.put("percentachiv", StringUtil.convertToTwoDecimal((dl/commision.getTarget())*100));
                    } else if(commision.getGoaltype()==2) { // for total revenue
                        filter_names.add("c.deleteflag");
                        filter_names.add("c.isarchive");
                        filter_names.add("c.company.companyID");
                        filter_names.add("c.validflag");
                        filter_names.add("ao.mainOwner");
                        filter_names.add("ao.usersByUserid.userID");
                        filter_params.add(0);
                        filter_params.add(false);
                        filter_params.add(companyid);
                        filter_params.add(1);
                        filter_params.add(true);
                        filter_params.add(userid);

                        filter_names.add(">=date(c.createdOn)");
                        filter_names.add("<=date(c.createdOn)");
                        filter_params.add(frmDate.getTime());
                        filter_params.add(toDate.getTime());
                        kmsg = crmAccountDAOObj.getAccountOwners(requestParams, filter_names, filter_params);

//                        int dl = kmsg.getRecordTotalCount();
                        double accountrevenue = 0;
                        Iterator itetype2 = kmsg.getEntityList().iterator();
                        while (itetype2.hasNext()) {
                            CrmAccount ca = (CrmAccount) itetype2.next();
                            if(!StringUtil.isNullOrEmpty(ca.getRevenue()))
                                accountrevenue += Double.parseDouble(ca.getRevenue());
                        }
//                        dl=accountrevenue;
                        obj.put("achived", accountrevenue);
                        if(commision.getTarget()!=0)
                            obj.put("percentachiv", StringUtil.convertToTwoDecimal((accountrevenue/commision.getTarget())*100));
                    }
                }
                jArr.put(obj);
            }
            jobj.put("data", jArr);

        } catch (ParseException e) {
            logger.warn(e.getMessage(),e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveCommision(HttpServletRequest request, HttpServletResponse response) throws ServiceException, SessionExpiredException, ParseException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            jobj.put("success", false);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("data", request.getParameter("data"));
            requestParams.put("deleteddata", request.getParameter("deleteddata"));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));

            kmsg = accIntDAOObj.saveCommisionPlans(requestParams);
            jobj.put("success", kmsg.isSuccessFlag());
            JSONArray jArrRes = (JSONArray) kmsg.getEntityList().get(0);            
            jobj.put("data", jArrRes.toString());
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView assignCommisionPlan(HttpServletRequest request, HttpServletResponse response) throws ServiceException, SessionExpiredException, ParseException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            jobj.put("success", false);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("year", request.getParameter("year"));
            requestParams.put("planid", request.getParameter("planid"));
            requestParams.put("userid", request.getParameter("userid"));
            requestParams.put("loginuserid", sessionHandlerImpl.getUserid(request));

            kmsg = accIntDAOObj.assignCommisionPlans(requestParams);
            jobj.put("success", kmsg.isSuccessFlag());
            if(kmsg.isSuccessFlag()) {
                jobj.put("msg", "Commission plan applied successfully");
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(),e);
        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView syncAccountingProducts(HttpServletRequest request, HttpServletResponse response) throws ServiceException, SessionExpiredException, ParseException {
       JSONObject jobj1 = new JSONObject();
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            jobj1 = remoteAPIControllerObj.fetchAccountingProducts(request, response, companyid, userid, false);
            txnManager.commit(status);
            crmCommonDAOObj.validaterecords("Product", companyid);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj1.toString());
    }

    
}
