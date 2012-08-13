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
package com.krawler.spring.organizationChart;

import com.krawler.common.admin.Assignmanager;
import com.krawler.common.admin.AuditAction;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author Karthik
 */
public class organizationChartController extends MultiActionController {

    private organizationChartDAO organizationChartDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private permissionHandlerDAO permissionHandlerDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private String successView;
    private HibernateTransactionManager txnManager;

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setorganizationChartDAO(organizationChartDAO organizationChartDAOObj1) {
        this.organizationChartDAOObj = organizationChartDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setpermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
    }
    
    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    
    public void getJson4Am(List appendedList, JSONArray jarr, String parentid) throws ServiceException {
    	Map managers = new HashMap();
        KwlReturnObject kmsg = null;
        try {
            Iterator ite = appendedList.iterator();

            while (ite.hasNext()) {
                Assignmanager am = (Assignmanager) ite.next();
                JSONObject obj = new JSONObject();
                int level = 9;
                if (StringUtil.isNullOrEmpty(parentid) || !parentid.equals(am.getAssignemp().getUserID())) {
                    JSONArray mans = (JSONArray)managers.get(am.getAssignemp().getUserID());
                    if(mans==null){
                        mans = new JSONArray();
                        managers.put(am.getAssignemp().getUserID(), mans);
                        obj.put("fromuid", mans);
                        obj.put("userid", am.getAssignemp().getUserID());
                        obj.put("username", am.getAssignemp().getUserLogin().getUserName());
                        obj.put("emailid", am.getAssignemp().getEmailID());
                        obj.put("contactno", am.getAssignemp().getContactNumber());
                        obj.put("fname", am.getAssignemp().getFirstName());
                        obj.put("lname", am.getAssignemp().getLastName());
                        obj.put("image", am.getAssignemp().getImage());
                        obj.put("nodeid", am.getAssignemp().getUserID());
                        obj.put("address", am.getAssignemp().getAddress());
                        obj.put("level", level);

                        kmsg = permissionHandlerDAOObj.getRoleofUser(am.getAssignemp().getUserID());
                        Iterator ite2 = kmsg.getEntityList().iterator();
                        if (ite2.hasNext()) {
                            Object[] row = (Object[]) ite2.next();
                            obj.put("roleid", row[0]);
                            obj.put("designation", row[1]);
                        }
                        jarr.put(obj);
                    }
                    mans.put(am.getAssignman().getUserID());
                }
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmRoleOrg.getJson4Am", e);
        } 
    }

    public JSONObject getUnmappedUsersJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                User user = (User) ite.next();
                JSONObject obj = new JSONObject();
                obj.put("userid", user.getUserID());
                obj.put("username", user.getUserLogin().getUserName());
                obj.put("fname", user.getFirstName());
                obj.put("lname", user.getLastName());
                obj.put("image", user.getImage());
                obj.put("emailid", user.getEmailID());

                kmsg = permissionHandlerDAOObj.getRoleofUser(user.getUserID());
                Iterator ite2 = kmsg.getEntityList().iterator();
                if (ite2.hasNext()) {
                    Object[] row = (Object[]) ite2.next();
                    obj.put("roleid", row[0]);
                    obj.put("designation", row[1]);
                }
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public ModelAndView getUnmappedUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));
            requestParams.put("userid", sessionHandlerImplObj.getUserid(request));

            kmsg = organizationChartDAOObj.getUnmappedUsers(requestParams);
            jobj = getUnmappedUsersJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getMappedUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int exceptionAt = 0;
        List appendList = new ArrayList();
        String extraQuery = "";
        String parentid = "";
        try {
            String userid = sessionHandlerImplObj.getUserid(request);
            organizationChartDAOObj.rootUser(jarr, userid);
            KwlReturnObject kmsg = permissionHandlerDAOObj.getRoleofUser(userid);
            Iterator ite2 = kmsg.getEntityList().iterator();
            if (ite2.hasNext()) {
                Object[] row = (Object[]) ite2.next();
                jarr.getJSONObject(0).put("roleid", row[0]);
                jarr.getJSONObject(0).put("designation", row[1]+"<span id='rootnode'>(You are here.)</span>");
            }
            organizationChartDAOObj.getAssignManager(userid, appendList, exceptionAt, extraQuery);
            getJson4Am(appendList, jarr, parentid);
            jobj.put("data", jarr);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getGridMappedUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        List appendL = new ArrayList();
        KwlReturnObject kmsg = null;
        int dl = 0;
        try {
            String nodeid = request.getParameter("nodeid");
            String parentid = request.getParameter("parentid");
            String userid = sessionHandlerImplObj.getUserid(request);
            String extraQuery = " and assignemp.userID !='" + nodeid + "'";

            JSONArray jarr = new JSONArray();
            organizationChartDAOObj.rootUser(jarr, userid);
            if(jarr.length() > 0) {
                kmsg = permissionHandlerDAOObj.getRoleofUser(jarr.getJSONObject(0).getString("userid"));
                Iterator ite2 = kmsg.getEntityList().iterator();
                if (ite2.hasNext()) {
                    Object[] row = (Object[]) ite2.next();
                    jarr.getJSONObject(0).put("roleid", row[0]);
                    jarr.getJSONObject(0).put("designation", row[1]);
                }
            }
            organizationChartDAOObj.getAssignManager(userid, appendL, dl, extraQuery);
            getJson4Am(appendL, jarr, parentid);
            jobj.put("data", jarr);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteNode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String details = "";
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            String nodeid = request.getParameter("nodeId");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("nodeid", StringUtil.checkForNull(nodeid));


            kmsg = organizationChartDAOObj.deleteNode(requestParams);
            if (!StringUtil.isNullOrEmpty(kmsg.getEntityList().get(0).toString())) {
                details = (String) kmsg.getEntityList().get(0).toString();
                auditTrailDAOObj.insertAuditLog(AuditAction.ADMIN_Organization,
                        details, request, "0");
            }
            if (!StringUtil.isNullOrEmpty(kmsg.getEntityList().get(1).toString())) {
                details = (String) kmsg.getEntityList().get(1).toString();
                auditTrailDAOObj.insertAuditLog(AuditAction.ADMIN_Organization,
                        details, request, "0");
            }
            jobj.put("data", "{success:true}");
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
}
