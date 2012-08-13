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
import com.krawler.common.session.SessionExpiredException;
import com.krawler.service.SequencerService;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.net.MalformedURLException;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.mailIntegration.mailIntegrationController;
import com.krawler.common.admin.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.SystemUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.contact.bizservice.ContactManagementService;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmCombodata;
import com.krawler.crm.database.tables.DefaultTemplates;
import com.krawler.crm.database.tables.Finalgoalmanagement;
import com.krawler.crm.database.tables.TargetList;
import com.krawler.crm.hrmsintegration.bizservice.GoalManagementService;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.*;
import javax.mail.MessagingException;
import com.krawler.utils.json.base.JSONArray;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.krawler.crm.utils.Constants;
import com.krawler.esp.handlers.APICallHandlerService;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.companyDetails.companyDetailsDAO;
import com.krawler.spring.crm.emailMarketing.crmEmailMarketingDAO;
import com.krawler.crm.utils.AuditAction;
import com.krawler.esp.utils.CompanyRoutingDataSource;
import com.krawler.esp.web.resource.Links;
import com.krawler.spring.crm.integration.hrmsIntDAO;
import com.krawler.spring.crm.productModule.crmProductDAO;
import com.krawler.spring.crm.targetModule.crmTargetDAO;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;

public class remoteAPIController extends MultiActionController {
    private mailIntegrationController mailIntDAOObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private crmManagerDAO crmManagerDAOObj;
    private crmProductDAO crmProductDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private String successView;
    private permissionHandlerDAO permissionHandlerDAOObj;

    private RemoteAPIDAO remoteAPIDAOObj;

    private crmLeadDAO crmLeadDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmCommonDAO crmCommonDAOObj;
    private crmContactDAO crmContactDAOObj;
    private companyDetailsDAO companyDetailsDAOObj;
    private crmTargetDAO crmTargetDAOObj;
    private crmEmailMarketingDAO crmEmailMarketingDAOObj;
    private hrmsIntDAO hrmsIntDAOObj;
    private GoalManagementService goalManagementService;
    private HibernateTransactionManager txnManager;
    private APICallHandlerService apiCallHandlerService;
    private ContactManagementService contactManagementService;

    private SequencerService sequencerService;
    private CompanyRoutingDataSource routingDataSource;

	public void setRoutingDataSource(CompanyRoutingDataSource routingDataSource) {
		this.routingDataSource = routingDataSource;
	}

    public void setContactManagementService(ContactManagementService contactManagementService) {
        this.contactManagementService = contactManagementService;
    }
    public void setApiCallHandlerService(APICallHandlerService apiCallHandlerService)
    {
        this.apiCallHandlerService = apiCallHandlerService;
    }

    public void setRemoteAPIDAO(RemoteAPIDAO remoteAPIDAOObj) {
        this.remoteAPIDAOObj = remoteAPIDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void sethrmsIntDAO(hrmsIntDAO hrmsIntDAOObj1) {
        this.hrmsIntDAOObj = hrmsIntDAOObj1;
    }

    public void setcrmEmailMarketingDAO(crmEmailMarketingDAO crmEmailMarketingDAOObj1) {
        this.crmEmailMarketingDAOObj = crmEmailMarketingDAOObj1;
    }

    public void setcrmTargetDAO(crmTargetDAO crmTargetDAOObj1) {
        this.crmTargetDAOObj = crmTargetDAOObj1;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setcompanyDetailsDAO(companyDetailsDAO companyDetailsDAOObj1) {
        this.companyDetailsDAOObj = companyDetailsDAOObj1;
    }

    public void setpermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
    }

    public void setmailIntDAO(mailIntegrationController mailIntDAOObj1) {
        this.mailIntDAOObj = mailIntDAOObj1;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setcrmProductDAO(crmProductDAO crmProductDAOObj1) {
        this.crmProductDAOObj = crmProductDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }
    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImpl1) {
        this.sessionHandlerImplObj = sessionHandlerImpl1;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }
    public void setGoalManagementService(GoalManagementService goalManagementServiceObj) {
        this.goalManagementService = goalManagementServiceObj;
    }
    
    private static final Integer[] casesToBeCheckedForCompanyActivatedFlag = new Integer[]{2, 4, 5, 6, 7, 8, 9, 10};

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */

    public ModelAndView remoteapi(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String result = "";
        boolean testParam = false;
        boolean syncAccountingValidate = false;
        boolean commit =false;
        String validkey = storageHandlerImpl.GetRemoteAPIKey();
        String remoteapikey = "";
        int action=-1;
        if (!StringUtil.isNullOrEmpty(request.getParameter("data"))) {
            //Create transaction
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setName("JE_Tx");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
            TransactionStatus status = txnManager.getTransaction(def);

            try {
                JSONObject jobj = new JSONObject(request.getParameter("data"));
                testParam = (jobj.has("test") && jobj.getBoolean("test"));
                if(jobj.has("iscommit"))
                    commit = jobj.getBoolean("iscommit");

                if(jobj.has("remoteapikey"))
                    remoteapikey = jobj.getString("remoteapikey");

                action = Integer.parseInt(request.getParameter("action"));
                boolean isCompanyActive = true;
                if (Arrays.binarySearch(casesToBeCheckedForCompanyActivatedFlag, action) > -1) {
                    isCompanyActive = isCompanyActivated(request, jobj, action);
                }
                if (isCompanyActive) {
                    switch (action) {
                        case 0:
                            result = CompanyidExits(request, action);
                            break;
                        case 1:
                            result = UserExits(request, action);
                            break;
                        case 2:
                        result = createUser(request, action,remoteapikey);
                            break;
                        case 3:
                            result = createCompany(request, action);
                            break;
                        case 4:
                            result = UserDelete(request, action);
                            break;
                        case 5:
                            result = assignRole(request, action);
                            break;
                        case 6:
                            result = activateUser(request, action);
                            break;
                        case 7:
                            result = deActivateUser(request, action);
                            break;
                        case 8:
                            result = editCompany(request, action);
                            break;
                        case 9:
                            //@@@ - Need to convert
                            result = getUpdates(request);
                            break;
                        case 10:
                            result = editUser(request, action);
                            break;
                        // Need to check following cases
                        case 11:
                            // made changes
                            result = targetListEntry(request);
                            break;
                        case 12:
                            // made changes
                            result = getUserTargets(request);
                            break;

                        case 14:
                            result = onLineSubscriber(request);
                            break;

                        // SagarM - not in use
//                    case 15:
//                        // request has subdomain
//                        result = saveOutlookContacts(request);
//                        break;

                        case 15:
                            result = deleteCompany(request, action, jobj);
                            break;
                        case 16:
                            result = deactivateCompany(request, action, jobj);
                            break;
                        case 99:
                            // made changes
                            result = syncAccountingProduct(request, response, action);
                            break;

                        case 100:
                            viewedEmailMarketMail(request);
                            break;
                        case 101:// internal call from case-no:11
                            result = addTargetListEntry(request);
                            break;
                        case 102:// internal call from case-no:12
                            result = getUserTargetsFromDB(request);
                            break;
                        case 103:// internal call from case-no:16
                            result = syncAndAddAccountingProduct(request, response, action);
                            JSONObject jobjSyncAccounting = new JSONObject(result);
                            syncAccountingValidate = jobjSyncAccounting.optBoolean("syncaccounting");
                            break;
                        case 104:// internal call from case-no:100
                            result = markViewedEmailMarketMail(request);
                            break;
                        case 105:// internal call from case-no:2
                            result = getNextSequencerForUser(request, action);
                            break;
                    }
                } else {
                    result = remoteAPIDAOObj.getMessage(2, 99, action);
                }
                if (commit && (validkey.equals(remoteapikey)) || action == 15 || action == 100 || action == 104) {
                    txnManager.commit(status);
                } else {
                    txnManager.rollback(status);
                }
                if (testParam) {
                    result = result.substring(0, (result.length()));
                }
                if(syncAccountingValidate){
                        String data= request.getParameter("data");
                        JSONObject jobj2 = new JSONObject(data);
                        String companyid = jobj2.get("companyid").toString();
                        crmCommonDAOObj.validaterecords("Product", companyid);
                }
            } catch (JSONException e) {
                result =remoteAPIDAOObj.getMessage(2, 2, action);
                if (testParam) {
                    result += ",\"action\": " + Integer.toString(action);
                    result += ",\"success\":false}";
                }
                txnManager.rollback(status);
                logger.warn(e.getMessage(), e);
            } catch (ServiceException e) {
                result =remoteAPIDAOObj.getMessage(2, 2, action);
                if (testParam) {
                    result += ",\"action\": " + Integer.toString(action) + "}";
                }
                txnManager.rollback(status);
                logger.warn(e.getMessage(), e);
            } catch(SQLException e) {
                result =remoteAPIDAOObj.getMessage(2, 2, action);
                txnManager.rollback(status);
                logger.warn(e.getMessage(), e);
            } catch(Exception e) {
                result =remoteAPIDAOObj.getMessage(2, 2, action);
                txnManager.rollback(status);
                logger.warn(e.getMessage(), e);
            } finally {
                return new ModelAndView("jsonView-ex", "model", result);
            }
        } else {
            return new ModelAndView("jsonView-ex", "model",remoteAPIDAOObj.getMessage(2, 1, action));
        }
    }

    private String assignRole(HttpServletRequest request, int action) throws SQLException, ServiceException {
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = jobj.getString("userid");
            String role = jobj.getString("role");
            String query = "";
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("u.userID");
            filter_params.add(userid);
            User user = (!StringUtil.isNullOrEmpty(userid))?(User)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid):null;
            
            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);

            int count = kmsg.getRecordTotalCount();

            if (count > 0) {
                int roleVal = role.equals("c0")? 0 :(role.equals("c1") ? 1 : (role.equals("c2") ? 2 : 4));
                String roleId =  (role.equals("c1") ||  role.equals("c0"))? Constants.COMPANY_ADMIN : (role.equals("c2") ? Constants.COMPANY_SALES_MANAGER : Constants.COMPANY_SALES_EXECUTIVE);
                String mb_roleId =  (role.equals("c1")||  role.equals("c0")) ? "1" : (role.equals("c2") ? "2" : "3");
                
                
                // Assign Role
                String userroleid = "";
                kmsg = permissionHandlerDAOObj.getRoleofUser(userid);
                List ll = kmsg.getEntityList();
                Iterator itetemp = ll.iterator();
                while(itetemp.hasNext()) {
                    Object[] roleobj = (Object[]) itetemp.next();
                    userroleid = roleobj[2].toString();
                }
                if(StringUtil.isNullOrEmpty(userroleid)) {
                    RoleUserMapping roleUserMapObj = null;
                    HashMap<String, Object> userroleReqParams = new HashMap<String, Object>();
                    userroleReqParams.put("roleid", roleId);
                    userroleReqParams.put("userid", userid);
                    //User user = (User)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
                    kmsg = permissionHandlerDAOObj.saveUserRole(roleId, user);
                    Iterator ite2 = kmsg.getEntityList().iterator();
                    if (ite2.hasNext()) {
                        roleUserMapObj = (RoleUserMapping) ite2.next();
                    }
                    permissionHandlerDAOObj.setDefaultPermissions(roleVal,roleUserMapObj);
                } else {

                    checkUserRoleChange(roleId, userid);
                    HashMap<String, Object> userroleReqParams = new HashMap<String, Object>();
                    userroleReqParams.put("roleid", roleId);
                    userroleReqParams.put("userid", userid);
                    userroleReqParams.put("userroleid",userroleid);
                    permissionHandlerDAOObj.saveRoleList(userroleReqParams);
                }
                if(roleVal == 0){
                	changeCompanyCreator(user,user.getCompany());
                }
                //Map crm user with module builder roles.
                //To do - need to change this.
                //User user = (User)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
                remoteAPIDAOObj.saveMB_userRoleMapping(user,mb_roleId);

                HashMap<String, Object> userRequestParams = new HashMap<String, Object>();
                userRequestParams.put("userid", userid);
                userRequestParams.put("deleteflag", 0);
                kmsg = profileHandlerDAOObj.saveUser(userRequestParams);
                result =remoteAPIDAOObj.getMessage(1, 8, action);
            } else {
                result =remoteAPIDAOObj.getMessage(2, 6, action);
            }
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while assigning roles:'"+e.getMessage()+"' \"}";
            throw ServiceException.FAILURE("remoteApi.assignRole:" + e.getMessage(), e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while assigning roles:'"+e.getMessage()+"' \"}";
            throw ServiceException.FAILURE("remoteApi.assignRole", e);
        }
        return result;
    }
    
    private void changeCompanyCreator(User user,Company company) throws ServiceException{
    	remoteAPIDAOObj.changeCompanyCreator(user,company);
    }

    private void checkUserRoleChange(String role, String userId) {
        KwlReturnObject kmsg = null;
        try {
            boolean makeChanges = false;
            String parentRole = "", childRole ="";
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("assignman.userID");
            filter_params.add(userId);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);

            List<Assignmanager> childList = crmCommonDAOObj.getAssignManagers(requestParams).getEntityList();

            filter_names.clear();
            filter_params.clear();
            filter_names.add("assignemp.userID");
            filter_params.add(userId);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);

            List parentList = crmCommonDAOObj.getAssignManagers(requestParams).getEntityList();

            if (parentList.size() > 0) {
                Assignmanager amP = (Assignmanager) parentList.iterator().next();
                
                kmsg = permissionHandlerDAOObj.getRoleofUser(amP.getAssignman().getUserID());
                Iterator ite2 = kmsg.getEntityList().iterator();
                if (ite2.hasNext()) {
                    Object[] row = (Object[]) ite2.next();
                    parentRole = row[0].toString();
                }

                // if parent's role is lower than new role assigning to this user,
                // then remove that use from organization and his childs become parent node's child
                if (!crmCommonDAOObj.isPerfectRole(parentRole, role)) {
                    makeChanges = true;
                }
            }

            if(!makeChanges && childList.size()>0) {
                for(Assignmanager amC : childList ) {
                    kmsg = permissionHandlerDAOObj.getRoleofUser(amC.getAssignemp().getUserID());
                    Iterator ite2 = kmsg.getEntityList().iterator();
                    if (ite2.hasNext()) {
                        Object[] row = (Object[]) ite2.next();
                        childRole = row[0].toString();
                    }
                    // if child's role is higher than new role assigning to this user,
                    // then remove that use from organization and his childs became parent node's child
                    if (!crmCommonDAOObj.isPerfectRole(role, childRole)) {
                        makeChanges = true;
                    }
                }
            }

            if(makeChanges) {
                changeManager(childList, parentList);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private void changeManager(List<Assignmanager> childList,List<Assignmanager> parentList) {
        KwlReturnObject kmsg = null;
        try{
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            // if having parent then his childs became parent node's child
            if (parentList.size() > 0) {
                Assignmanager amP = (Assignmanager) parentList.iterator().next();
                for(Assignmanager amC : childList ) {
                    requestParams.clear();
                    requestParams.put("fromId", amP.getAssignman().getUserID());
                    requestParams.put("assignid", amC.getId());
                    kmsg = crmCommonDAOObj.insertNode(requestParams);
                }
                // removed node entry from assignmanager
                requestParams.clear();
                requestParams.put("assignid", amP.getId());
                crmCommonDAOObj.deleteNode(requestParams);
            } else {
                // If no parent, then remove every successive child node from organization 
                for(Assignmanager amC : childList ) {
                    requestParams.clear();
                    requestParams.put("assignid", amC.getId());
                    kmsg = crmCommonDAOObj.deleteNode(requestParams);
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }

    private String createUser(HttpServletRequest request, int action, String remoteapikey) throws SQLException, ServiceException {
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            Company company = null;
            String id = "";
            String pwdText = "";
            String companyid = jobj.isNull("companyid") ? "" : jobj.getString("companyid");
            String username = jobj.isNull("username") ? "" : jobj.getString("username");
            String pwd = jobj.isNull("password") ? "" : jobj.getString("password");
            String fname = jobj.isNull("fname") ? "" : jobj.getString("fname");
            String lname = jobj.isNull("lname") ? "" : jobj.getString("lname");
            String emailid = jobj.isNull("emailid") ? "" : jobj.getString("emailid");
            String userid = jobj.isNull("userid") ? "" : jobj.getString("userid");
            String nextid = jobj.isNull("nextid") ? "" : jobj.getString("nextid");

            if (StringUtil.isNullOrEmpty(companyid) || StringUtil.isNullOrEmpty(username) || StringUtil.isNullOrEmpty(fname) ||
                    StringUtil.isNullOrEmpty(lname) || StringUtil.isNullOrEmpty(emailid) || StringUtil.isNullOrEmpty(userid)
                    //|| StringUtil.isNullOrEmpty(subDomain)
                    ) {
                return remoteAPIDAOObj.getMessage(2, 1,action);
            }
            /*Company ID Check */
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.companyID");
            filter_params.add(companyid);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams);

//            String hql = "from Company where companyID=?";
//            if (HibernateUtil.executeQuery(session, hql, new Object[]{companyid}).isEmpty()) {
            if(kmsg.getRecordTotalCount() == 0){
                return remoteAPIDAOObj.getMessage(2, 4, action);
            } else {
                company = (Company) kmsg.getEntityList().iterator().next();
            }

//            /* Email Check for its company id*/
//            String query1 = "from User u where u.emailID = ? and u.company.companyID = ? ";
//            List list1 = HibernateUtil.executeQuery(session, query1, new Object[]{ emailid, companyid } );
//            Iterator itr1 = list1.iterator();
//            if (itr1.hasNext()) {
//                returnremoteAPIDAOObj.getMessage(2, 9);
//            }

            User user = (User)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
            if (user != null) {
                return remoteAPIDAOObj.getMessage(2, 7, action);
                }
            HashMap<String, Object> userRequestParams = new HashMap<String, Object>();
            userRequestParams.put("addUser", true);
            userRequestParams.put("userid", userid);
            userRequestParams.put("company", companyid);
            userRequestParams.put("username", username);
            userRequestParams.put("role", Constants.COMPANY_SALES_EXECUTIVE);
            userRequestParams.put("firstName", fname);
            userRequestParams.put("lastName", lname);
            userRequestParams.put("emailID", emailid);
            userRequestParams.put("address", "");
            userRequestParams.put("contactNumber", "");
            userRequestParams.put("password", pwd);
            userRequestParams.put("dateFormat", RemoteAPIDAO.DEFAULTDATEFORMATZONE);
            userRequestParams.put("timeZone", company.getTimeZone().getTimeZoneID());

           /*username check for its company */
            requestParams.clear();
            filter_names.clear();
            filter_params.clear();
            filter_names.add("u.userLogin.userName");
            filter_params.add(username);
            filter_names.add("u.company.companyID");
            filter_params.add(companyid);
            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);

//            String q = "from User where userLogin.userName=? and company.companyID = ? ";// and company.companyID=?";
//            if (HibernateUtil.executeQuery(session, q, new Object[]{username,companyid}).isEmpty() == false && username.equals(userLogin.getUserName()) == false) {
            if (kmsg.getRecordTotalCount() > 0 && username.equals(((User) kmsg.getEntityList().get(0)).getUserLogin().getUserName()) == false) {
                return remoteAPIDAOObj.getMessage(2, 3,action);
            }

            // get next incremented user_Id from original database
            Long nextId = 0l;
            if(StringUtil.isNullOrEmpty(nextid)) {
                StringBuffer params = new StringBuffer();
                appendParam(params, "action", "105");
                appendParam(params, "data", "{\"iscommit\":true,\"remoteapikey\":\""+remoteapikey+"\"}");
                URL u = new URL(request.getRequestURL().toString());
                result = callURL(u, params);
                JSONObject nextSeqJSON = new JSONObject(result);
                if(nextSeqJSON.has("success") && !nextSeqJSON.getBoolean("success")) {
                    return remoteAPIDAOObj.getMessage(2, 105,action);
                } else {
                    if(nextSeqJSON.has("nextid") && !StringUtil.isNullOrEmpty(nextSeqJSON.getString("nextid"))) {
                        nextId = Long.parseLong(nextSeqJSON.getString("nextid"));
                    } else
                        return remoteAPIDAOObj.getMessage(2, 105,action);
                }
            } else {
                nextId = Long.parseLong(nextid);
            }
            // set user id
//            Long nextId = getSequencerService().getNext(APIConstants.TABLE_USERS);
            userRequestParams.put("user_id", nextId);
            kmsg = profileHandlerDAOObj.saveUser(userRequestParams);
            user = (User) kmsg.getEntityList().get(0);
            String diff = null;
            requestParams.clear();
            requestParams.put("userid", user.getUserID());
            requestParams.put("fromId", user.getCompany().getCreator().getUserID());
            requestParams.put("addFlag", true);
            requestParams.put("assignid", java.util.UUID.randomUUID().toString());
            // get Parent and Child Use's role
            String parentRole = "", childRole = "";
            kmsg = permissionHandlerDAOObj.getRoleofUser(user.getCompany().getCreator().getUserID());
            Iterator ite2 = kmsg.getEntityList().iterator();
            if (ite2.hasNext()) {
                Object[] row = (Object[]) ite2.next();
                parentRole = row[0].toString();
            }
            kmsg = permissionHandlerDAOObj.getRoleofUser(user.getUserID());
            ite2 = kmsg.getEntityList().iterator();
            if (ite2.hasNext()) {
                Object[] row = (Object[]) ite2.next();
                childRole = row[0].toString();
            }
            if (crmCommonDAOObj.isPerfectRole(parentRole, childRole)) {
                crmCommonDAOObj.insertNode(requestParams);
            }

            // Assign User role
//            RoleUserMapping roleUserMapObj = null;
//            HashMap<String, Object> userroleReqParams = new HashMap<String, Object>();
//            userroleReqParams.put("roleid", Constants.COMPANY_SALES_EXECUTIVE);
//            userroleReqParams.put("userid", user.getUserID());
//            kmsg = permissionHandlerDAOObj.saveUserRole(Constants.COMPANY_SALES_EXECUTIVE, user);
//            ite2 = kmsg.getEntityList().iterator();
//            if (ite2.hasNext()) {
//                roleUserMapObj = (RoleUserMapping) ite2.next();
//            }
//            permissionHandlerDAOObj.setDefaultPermissions(4,roleUserMapObj);
            updatePreferences(request, null, (jobj.isNull("formatid") || StringUtil.isNullOrEmpty(jobj.getString("formatid")) ? null : jobj.getString("formatid")), (jobj.isNull("formatid") || StringUtil.isNullOrEmpty(jobj.getString("tzid")) ? null : jobj.getString("tzid")), diff);
            if (jobj.has("sendmail") && jobj.getBoolean("sendmail")) {
                user.setRoleID(Constants.COMPANY_SALES_EXECUTIVE);
                Company companyObj = (Company) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyid);
                User creater = (User) (companyObj.getCreator());
                String fullnameCreator = creater.getFirstName();
                if (fullnameCreator != null && creater.getLastName() != null) {
                    fullnameCreator += " " + creater.getLastName();
                }
                if (StringUtil.isNullOrEmpty(creater.getFirstName()) && StringUtil.isNullOrEmpty(creater.getLastName())) {
                    fullnameCreator = creater.getUserLogin().getUserName();
                }
                String passwordString = "";
                if (jobj.isNull("password")) {
                    passwordString = "\n\nUsername: " + username + " \nPassword: " + pwdText;
                }
                String uri = URLUtil.getPageURL(request, com.krawler.esp.web.resource.Links.loginpageFull);
                String companyName = companyObj.getCompanyName();
                String msgMailInvite = "Dear %s,\n\n%s has created an account for you at "+companyName+" CRM." + passwordString + "\n\nYou can log in at:\n%s\n\n\nSee you on "+companyName+" \n\n - %s and The "+companyName+" Team";
                String pmsg = String.format(msgMailInvite, user.getFirstName(), fullnameCreator, uri, fullnameCreator);
                if (jobj.isNull("password")) {
                    passwordString = "		<p>Username: <strong>%s</strong> </p>" + "               <p>Password: <strong>%s</strong></p>";
                }
                String msgMailInviteUsernamePassword = "<html><head><title>"+companyName+" CRM - Your "+companyName+" CRM Account</title></head><style type='text/css'>" + "a:link, a:visited, a:active {\n" + " 	color: #03C;" + "}\n" + "body {\n" + "	font-family: Arial, Helvetica, sans-serif;" + "	color: #000;" + "	font-size: 13px;" + "}\n" + "</style><body>" + "	<div>" + "		<p>Dear <strong>%s</strong>,</p>" + "		<p>%s has created an account for you at %s.</p>" + passwordString + "		<p>You can log in to "+companyName+" CRM  at: <a href=%s>%s</a>.</p>" + "		<br/><p>See you on "+companyName+" CRM !</p><p> - %s and The "+companyName+" CRM Team</p>" + "	</div></body></html>";
                String htmlmsg = String.format(msgMailInviteUsernamePassword, user.getFirstName(), fullnameCreator, companyObj.getCompanyName(),
                        uri, uri, fullnameCreator);
                try {
                    SendMailHandler.postMail(new String[]{user.getEmailID()}, "["+companyName+"] Welcome to "+companyName+" CRM", htmlmsg, pmsg, creater.getEmailID());
                } catch (MessagingException e) {
                    logger.warn(e.getMessage(), e);
                    result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while creating user:'"+e.getMessage()+"' \"}";
                }
            }
            result =remoteAPIDAOObj.getMessage(1, 5,action);
            try {
                mailIntDAOObj.addUserEntryForEmails(user.getCompany().getCreator().getUserID(),user,user.getUserLogin(),pwd,false);
            } catch(Exception ex) {
                logger.warn("Error while creating user entry in mail engine : "+ex.getMessage(), ex);
                System.out.println("For User : ");
                System.out.println("Userid : "+user.getUserID());
                System.out.println("Username : "+user.getUserLogin().getUserName());
                System.out.println("password : "+user.getUserLogin().getPassword());
                System.out.println("Companyid : "+user.getCompany().getCompanyID());
            }
        } catch (ServiceException  e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while creating user:'"+e.getMessage()+"' \"}";
            throw ServiceException.FAILURE("remoteApi.createUser", e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while creating user:'"+e.getMessage()+"' \"}";
            throw ServiceException.FAILURE("remoteApi.createUser", e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while creating user:'"+e.getMessage()+"' \"}";
            throw ServiceException.FAILURE("remoteApi.createUser", e);
        }
        return result;
    }

    private String getNextSequencerForUser(HttpServletRequest request, int action) {
        String result = "{\"success\":false}";
        try{
            Long nextId = getSequencerService().getNext(APIConstants.TABLE_USERS);
            result = "{\"success\":true, \"nextid\":\""+nextId+"\"}";
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while creating sequence id :'"+ex.getMessage()+"' \"}";
        } finally {
            return result;
        }
    }
    static void updatePreferences(HttpServletRequest request, String currencyid, String dateformatid, String timezoneid, String tzdiff) {
        if (currencyid != null) {
            request.getSession().setAttribute("currencyid", currencyid);
        }
        if (timezoneid != null) {
            request.getSession().setAttribute("timezoneid", timezoneid);
            request.getSession().setAttribute("tzdiff", tzdiff);
        }
        if (dateformatid != null) {
            request.getSession().setAttribute("dateformatid", dateformatid);
        }
    }

    private String createCompany(HttpServletRequest request, int action) throws SQLException, ServiceException, MalformedURLException, UnsupportedEncodingException {
        String result = "{\"success\":false}";
        try {
            boolean createCompany = true;
            boolean createUSer = true;
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String companyid = jobj.isNull("companyid")?"":jobj.getString("companyid");
            String userid = jobj.isNull("userid")?"":jobj.getString("userid");
            String subdomain = jobj.isNull("subdomain")?"":jobj.getString("subdomain");
            String userid2 = jobj.isNull("username") ? "" : jobj.getString("username");
            String emailid2 = jobj.isNull("emailid") ? "" : jobj.getString("emailid");
            String password = jobj.isNull("password") ? "" : jobj.getString("password");
            String companyname = jobj.isNull("companyname") ? "" : jobj.getString("companyname");
            String fname = jobj.isNull("fname") ? "" : jobj.getString("fname");
            String lname = jobj.isNull("lname") ? "" : jobj.getString("lname");
            if (StringUtil.isNullOrEmpty(companyname) || StringUtil.isNullOrEmpty(userid2) ||
                    StringUtil.isNullOrEmpty(fname) || StringUtil.isNullOrEmpty(emailid2) ||
                    StringUtil.isNullOrEmpty(companyid) || StringUtil.isNullOrEmpty(userid) || StringUtil.isNullOrEmpty(subdomain)) {
                return remoteAPIDAOObj.getMessage(2, 1,action);
            }
            String pwdtext = "";
            if (jobj.isNull("password")) {
                pwdtext = authHandler.generateNewPassword();
                password = authHandler.getSHA1(pwdtext);
            }

            // check for companyid or userid exist
            StringBuffer pramsStrCompany = new StringBuffer();
            appendParam(pramsStrCompany, "action", "0");
            appendParam(pramsStrCompany, "data", "{\"companyid\":\""+companyid+"\"}");
            String pStrCompany = pramsStrCompany.toString();

            StringBuffer pramsStrUser = new StringBuffer();
            appendParam(pramsStrUser, "action", "1");
            appendParam(pramsStrUser, "data", "{\"userid\":\""+userid+"\"}");
            String pStrUser = pramsStrUser.toString();

            URL u = new URL(request.getRequestURL().toString());
            Collection<String> subdomains =  routingDataSource.getOneCompanyPerDataSource();
			for(String subDomain: subdomains) {
                // check for companyid
				StringBuffer buff = new StringBuffer(pStrCompany);
				if(request.getParameter("cdomain")==null||request.getParameter("cdomain").length()<=0)
					appendParam(buff, "cdomain", subDomain);
				String res = callURL(u, buff);
                int currentAction = 0;
                if(res.equals(remoteAPIDAOObj.getMessage(1, 1,currentAction))) {
                    result = remoteAPIDAOObj.getMessage(2, 8,action);
                    createCompany = false;
                    break;
                }

                // check for userid
                buff = new StringBuffer(pStrUser);
				if(request.getParameter("cdomain")==null||request.getParameter("cdomain").length()<=0)
					appendParam(buff, "cdomain", subDomain);
				res = callURL(u, buff);
                currentAction = 1;
                if(res.equals(remoteAPIDAOObj.getMessage(1, 3,currentAction))) {
                    result = remoteAPIDAOObj.getMessage(2, 7, action);
                    createUSer = false;
                    break;
                }

			}

            if(createCompany && createUSer) {
                if (!(StringUtil.isNullOrEmpty(userid2) || StringUtil.isNullOrEmpty(emailid2))) {
                    emailid2 = emailid2.replace(" ", "+");
                    Long newCompanyId = getSequencerService().getNext(APIConstants.TABLE_COMPANY);
                    Long nextUserId = getSequencerService().getNext(APIConstants.TABLE_USERS);
                    result = remoteAPIDAOObj.signupCompany(companyid, userid, userid2, password, emailid2, companyname,fname,subdomain,lname, action, newCompanyId, nextUserId);

                    if (result.equals("success")) {
                        result =remoteAPIDAOObj.getMessage(1, 6,action);
                    }
                }
            }
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            result =remoteAPIDAOObj.getMessage(2, 2,action);
            throw ServiceException.FAILURE("remoteApi.createCompany:" + e.getMessage(), e);
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while creating company:'"+e.getMessage()+"' \"}";
            throw ServiceException.FAILURE("remoteApi.createCompany", e);
        }
        return result;
    }

    private String callURL(URL url, StringBuffer params){
		java.io.BufferedReader in=null;
        String res = "";
        try{
            URLConnection uc = url.openConnection();
            uc.setDoOutput(true);
            uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            java.io.PrintWriter pw = new java.io.PrintWriter(uc.getOutputStream());
            pw.println(params);
            pw.close();
            in = new java.io.BufferedReader(new java.io.InputStreamReader(uc.getInputStream()));
            res = URLDecoder.decode(in.readLine(),"UTF-8");
            in.close();
        } catch (IOException ex) {
            logger.warn("Diversion not possible for "+url+" ["+params+"]",ex);
        } finally {
        	if(in!=null)
				try {in.close();} catch (IOException e) {}
            return res;
        }
	}

    private String appendOriginalParams(HttpServletRequest request, StringBuffer params){
		java.io.BufferedReader in=null;
        String res = "";
        try{
            Enumeration en = request.getParameterNames();
            while(en.hasMoreElements()){
                String paramName = (String)en.nextElement();
                String paramValue = request.getParameter(paramName);
                // exclude action parameter
                if(!paramName.equals("action")) {
                    appendParam(params,paramName,paramValue);
                }
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("remoteApi.appendOriginalParams:" + ex.getMessage(), ex);
        } finally {
        	if(in!=null)
				try {in.close();} catch (IOException e) {}
            return res;
        }
	}
    
	private void appendParam(StringBuffer url,String key, String value) throws UnsupportedEncodingException{
		StringBuffer paramStr= new StringBuffer();
		paramStr.append(URLEncoder.encode(key,"UTF-8")).append("=").append(URLEncoder.encode(value,"UTF-8"));
		if(url.length()>0){
			url.append("&");
		}
		url.append(paramStr);
	}

    public String CompanyidExits(HttpServletRequest request, int action) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        KwlReturnObject kmsg = null;
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String companyid = "";

            if (!jobj.isNull("companyid")) {
                companyid = jobj.getString("companyid");
            } else {
                return remoteAPIDAOObj.getMessage(2, 1,action);
            }

            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.companyID");
            filter_params.add(companyid);
            filter_names.add("c.deleted");
            filter_params.add(0);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams);

//            String query = "from Company c where c.companyID= ? and c.deleted=0 ";
//            List list = HibernateUtil.executeQuery(session, query, companyid);
            int count = kmsg.getEntityList().size();
            if (count > 0) {
                result =remoteAPIDAOObj.getMessage(1, 1,action);
            } else {
                result =remoteAPIDAOObj.getMessage(1, 2,action);
            }
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while checking company id exists:'"+e.getMessage()+"' \"}";
            throw ServiceException.FAILURE("remoteApi.CompanyidExits:" + e.getMessage(), e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while checking company id exists:'"+e.getMessage()+"' \"}";
            throw ServiceException.FAILURE("remoteApi.CompanyidExits", e);
        }
        return result;
    }

    public String UserExits(HttpServletRequest request, int action) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        KwlReturnObject kmsg = null;
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = "";
            boolean flag = false;

            if (jobj.has("userid")) {
                userid = jobj.getString("userid");
            } else if (jobj.has("username")) {
                userid = jobj.getString("username");
                flag = true;
            }
            if (StringUtil.isNullOrEmpty(userid)) {
                return remoteAPIDAOObj.getMessage(2, 1,action);
            }
            String msgStr = "";
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if (!flag) {
                filter_names.add("u.userID");
                filter_params.add(userid);
                msgStr = "Userid";
            } else {
                filter_names.add("u.userLogin.userName");
                filter_params.add(userid);
                msgStr = "Username";
            }
            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);
            int count = kmsg.getEntityList().size();
            if (count > 0) {
                result =remoteAPIDAOObj.getMessage(1, 3,action);
            } else {
                result =remoteAPIDAOObj.getMessage(1, 4,action);
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while checking user id exists:'"+e.getMessage()+"' \"}";
            throw ServiceException.FAILURE("remoteApi.UserExits", e);
        }
        return result;
    }
    
    public String UserDelete(HttpServletRequest request, int action) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        KwlReturnObject kmsg = null;
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = "";
            if (jobj.has("userid")) {
                userid = jobj.getString("userid");

                String[] userArr = userid.split(",");
                for (int i = 0; i < userArr.length; i++) {
                    User u = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userArr[i]);
                    if (u != null) {
                        if (deleteUserEntry(u.getUserID())) {

                            HashMap<String, Object> userRequestParams = new HashMap<String, Object>();
                            userRequestParams.put("userid", userid);
                            userRequestParams.put("deleteflag", 1);
                            userRequestParams.put("userName", u.getUserLogin().getUserName() + "_del");
                            userRequestParams.put("userloginid", u.getUserLogin().getUserID());
                            kmsg = profileHandlerDAOObj.saveUser(userRequestParams);
                            profileHandlerDAOObj.saveUserLogin(userRequestParams);

                            result =remoteAPIDAOObj.getMessage(1, 7,action);
                        } else {
                            result =remoteAPIDAOObj.getMessage(2, 11, action); // random msg for failure
                        }
                    } else {
                        result =remoteAPIDAOObj.getMessage(2, 6,action);
                    }
                }
            } else {
                return remoteAPIDAOObj.getMessage(2, 1,action);
            }
            if (StringUtil.isNullOrEmpty(userid)) {
                return remoteAPIDAOObj.getMessage(2, 1,action);
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"', 'errormsg': 'Following error occured while deleting user : '" + e.getMessage() + "}";
            throw ServiceException.FAILURE("remoteApi.UserDelete:" + e.getMessage(), e);
        }
        return result;
    }

    private String activateUser(HttpServletRequest request, int action) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        KwlReturnObject kmsg = null;
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = jobj.getString("userid");

            if (jobj.has("userid")) {
                userid = jobj.getString("userid");

                String[] userArr = userid.split(",");
                for (int i = 0; i < userArr.length; i++) {
                    User u = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userArr[i]);
                    if (u != null) {
                        HashMap<String, Object> userRequestParams = new HashMap<String, Object>();
                        userRequestParams.put("userid", userArr[i]);
                        userRequestParams.put("deleteflag", 0);
                        kmsg = profileHandlerDAOObj.saveUser(userRequestParams);

                        HashMap<String, Object> requestParams = new HashMap<String, Object>();
                        requestParams.put("userid", u.getUserID());
                        requestParams.put("fromId", u.getCompany().getCreator().getUserID());
                        requestParams.put("addFlag", true);
                        requestParams.put("assignid", java.util.UUID.randomUUID().toString());
                        // get Parent and Child Use's role
                        String parentRole = "", childRole = "";
                        kmsg = permissionHandlerDAOObj.getRoleofUser(u.getCompany().getCreator().getUserID());
                        Iterator ite2 = kmsg.getEntityList().iterator();
                        if (ite2.hasNext()) {
                            Object[] row = (Object[]) ite2.next();
                            parentRole = row[0].toString();
                        }
                        kmsg = permissionHandlerDAOObj.getRoleofUser(u.getUserID());
                        ite2 = kmsg.getEntityList().iterator();
                        if (ite2.hasNext()) {
                            Object[] row = (Object[]) ite2.next();
                            childRole = row[0].toString();
                        }
                        if (crmCommonDAOObj.isPerfectRole(parentRole, childRole)) {
                            crmCommonDAOObj.insertNode(requestParams);
                        }
                        result =remoteAPIDAOObj.getMessage(1, 9,action);
                    } else {
                        result =remoteAPIDAOObj.getMessage(2, 6,action);
                    }
                }
            }
            else {
                return remoteAPIDAOObj.getMessage(2, 1,action);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"', 'errormsg': 'Following error occured while activating user : '"  + e.getMessage() + "}";
            throw ServiceException.FAILURE("remoteApi.activateUser:" + e.getMessage(), e);
        }
        return result;
    }

    private String deActivateUser(HttpServletRequest request, int action) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        KwlReturnObject kmsg = null;
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = jobj.getString("userid");
            if (jobj.has("userid")) {
                userid = jobj.getString("userid");

                String[] userArr = userid.split(",");
                for (int i = 0; i < userArr.length; i++) {
                    User u = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userArr[i]);
                    if (u != null) {
                        if (deleteUserEntry(u.getUserID())) {
                            HashMap<String, Object> userRequestParams = new HashMap<String, Object>();
                            userRequestParams.put("userid", userArr[i]);
                            userRequestParams.put("deleteflag", 1);
                            kmsg = profileHandlerDAOObj.saveUser(userRequestParams);
                            result =remoteAPIDAOObj.getMessage(1, 10, action);
                        } else {
                            result =remoteAPIDAOObj.getMessage(2, 11, action);   // random msg for failure
                        }
                    } else {
                        result =remoteAPIDAOObj.getMessage(2, 6, action);
                    }
                }
            }
            else {
                return remoteAPIDAOObj.getMessage(2, 1,action);
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"', 'errormsg': 'Following error occured while de-activating user : '"  + e.getMessage() + "}";
            throw ServiceException.FAILURE("remoteApi.deActivateUser:" + e.getMessage(), e);
        }
        return result;
    }

    private String editCompany(HttpServletRequest request, int action) throws ServiceException, JSONException {
            String result = "";
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String address = jobj.isNull("address") ? "" : jobj.getString("address");
            String city = jobj.isNull("city") ? "" : jobj.getString("city");
            String state = jobj.isNull("state") ? "" : jobj.getString("state");
            String companyname = jobj.isNull("companyname") ? "" : jobj.getString("companyname");
            String companyid = jobj.isNull("companyid") ? "" : jobj.getString("companyid");
            String phone = jobj.isNull("phone") ? "" : jobj.getString("phone");
            String subdomain = jobj.isNull("subdomain") ? "" : jobj.getString("subdomain");
            String fax = jobj.isNull("fax") ? "" : jobj.getString("fax");
            String zip = jobj.isNull("zip") ? "" : jobj.getString("zip");
            String website = jobj.isNull("website") ? "" : jobj.getString("website");
            String emailid = jobj.isNull("emailid") ? "" : jobj.getString("emailid");
            String currency = jobj.isNull("currency") ? "" : jobj.getString("currency");
            String country = jobj.isNull("country") ? "" : jobj.getString("country");
            String timezone = jobj.isNull("timezone") ? "" : jobj.getString("timezone");
            String imgPath = jobj.isNull("image") ? "" : jobj.getString("image");
            if (StringUtil.isNullOrEmpty(companyid) || StringUtil.isNullOrEmpty(subdomain) || StringUtil.isNullOrEmpty(companyname) || StringUtil.isNullOrEmpty(currency) || StringUtil.isNullOrEmpty(country) || StringUtil.isNullOrEmpty(timezone)) {
                return remoteAPIDAOObj.getMessage(2, 1,action);
            }
            KwlReturnObject kmsg = null;
            try {
                Company company = null;
                HashMap<String, Object> requestParams = new HashMap<String, Object>();

                ArrayList filter_names = new ArrayList();
                ArrayList filter_params = new ArrayList();
                filter_names.add("c.companyID");
                filter_params.add(companyid);
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_params", filter_params);
                kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams);

//                String query1 = "from Company c where c.companyID= ?";
//                List list1 = HibernateUtil.executeQuery(session, query1, companyid);
                Iterator itr1 = kmsg.getEntityList().iterator();
                if (!itr1.hasNext()) {
                    return remoteAPIDAOObj.getMessage(2, 4,action);
                } else {
                    filter_names.clear();
                    filter_params.clear();
                    filter_names.add("c.subDomain");
                    filter_params.add(subdomain);
                    filter_names.add("!c.companyID");
                    filter_params.add(companyid);
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_params", filter_params);
                    kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams);

//                    query1 = "from Company c where c.subDomain= ? and c.companyID <> ?";
//                    list1 = HibernateUtil.executeQuery(session, query1, new Object[]{ subdomain,companyid});
                    itr1 = kmsg.getEntityList().iterator();
                    if (itr1.hasNext()) {
                        return remoteAPIDAOObj.getMessage(2, 10,action);
                    }
                    HashMap<String, Object> companyRequestParams = new HashMap<String, Object>();
                    companyRequestParams.put("addCompany", false);
                    companyRequestParams.put("address", address);
                    companyRequestParams.put("deleteflag", 0);
                    companyRequestParams.put("companyid", companyid);
                    companyRequestParams.put("domainname", subdomain);
                    companyRequestParams.put("companyname", companyname);
                    companyRequestParams.put("country", country);
                    companyRequestParams.put("city", city);
                    companyRequestParams.put("state", state);
                    companyRequestParams.put("mail", emailid);
                    companyRequestParams.put("timezone", timezone);
                    companyRequestParams.put("currency", currency);
                    companyRequestParams.put("website", website);
                    companyRequestParams.put("phone", phone);
                    companyRequestParams.put("fax", fax);
                    companyRequestParams.put("zip", zip);
                    companyRequestParams.put("logo", imgPath);
                    companyDetailsDAOObj.updateCompany(companyRequestParams);
                    result =remoteAPIDAOObj.getMessage(1, 11,action);
                }
            } catch (ServiceException e) {
                logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE("remoteapi.editCompany", e);
            }
            return result;
    }

     public boolean deleteUserEntry(String nodeid) throws SQLException, ServiceException {
        boolean result = false;
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("assignman.userID");
            filter_params.add(nodeid);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);

            List childList = crmCommonDAOObj.getAssignManagers(requestParams).getEntityList();

            filter_names.clear();
            filter_params.clear();
            filter_names.add("assignemp.userID");
            filter_params.add(nodeid);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);

            List parentList = crmCommonDAOObj.getAssignManagers(requestParams).getEntityList();

            if (parentList.size() > 0) {


                Assignmanager amP = (Assignmanager) parentList.iterator().next();
                Iterator iteC = childList.iterator();
                while (iteC.hasNext()) {
                    Assignmanager amC = (Assignmanager) iteC.next();

                    requestParams.clear();
                    requestParams.put("fromId", amP.getAssignman().getUserID());
                    requestParams.put("assignid", amC.getId());
                    kmsg = crmCommonDAOObj.insertNode(requestParams);
                }
                requestParams.clear();
                requestParams.put("assignid", amP.getId());
                crmCommonDAOObj.deleteNode(requestParams);
            }
            result = true;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
                result = false;
            throw ServiceException.FAILURE("remoteApi.deleteUserEntry:" + e.getMessage(), e);
        }
        return result;
    }

     public JSONObject getUpdatesAudit(HttpServletRequest request, String userid, int start, int limit) throws SQLException, ServiceException {
       JSONObject jobj = new JSONObject();
       try {
           /* get user date format */
           User user = (User)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
           String tzDiff = "";
           String dateJava = "";

           SimpleDateFormat sdf = null;

           if(user.getTimeZone() != null) {
               tzDiff = user.getTimeZone().getDifference();
           } else {
               tzDiff = user.getCompany().getTimeZone().getDifference();
           }
           if(user.getDateFormat() != null) {
               dateJava = user.getDateFormat().getJavaForm();
           } else {
               dateJava = "EEEE, MMMM dd, yyyy h:mm:ss a";
           }
           sdf = new SimpleDateFormat(dateJava);
           sdf.setTimeZone(TimeZone.getTimeZone("GMT" + tzDiff));
           KwlReturnObject res=remoteAPIDAOObj.getUpdatesAudit(userid, start, limit);
           Iterator itr = res.getEntityList().iterator();

           JSONArray jArr = new JSONArray();
           JSONObject jHead = new JSONObject();
           jHead.put("head", "<div style='padding:10px 0 10px 0;font-size:13px;font-weight:bold;color:#10559a;border-bottom:solid 1px #EEEEEE;'>Updates</div>");
           jArr.put(jHead);
           while (itr.hasNext()) {
               AuditTrail auditTrail = (AuditTrail)itr.next();
               JSONObject obj = new JSONObject();
               String username =  StringUtil.getFullName(auditTrail.getUser()) ;
               String details ="";
                try {
                    details = URLDecoder.decode(auditTrail.getDetails(), "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    details = URLDecoder.decode(auditTrail.getDetails());
                }

               String actions = auditTrail.getAction().getActionName();
               String time = sdf.format(auditTrail.getAuditTime());
            //   String time = auditTrail.getAuditTime().toString();
               String updateDiv = "";

               updateDiv += details;
               updateDiv += " by <span style=\"color:rgb(16, 85, 154); !important;\">  "+username+" </span>";
               updateDiv += " <span style=\"color:gray;font-size:11px\"> on " + time+"</span>";
               obj.put("update", getContentSpan(updateDiv));
               jArr.put(obj);
           }
           jobj.put("data", jArr);
           jobj.put("count", res.getRecordTotalCount());



        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("remoteApi.getUpdatesAudit:" + e.getMessage(), e);
        }
        return jobj;
    }

    public static String getContentSpan(String textStr) {
        String span = "<div style='padding:0 0 5px 0;border-bottom:solid 1px #EEEEEE;'>" + textStr + "<div style='clear:both;visibility:hidden;height:0;line-height:0;'></div></div>";
        return span;
    }

    public String getUpdates(HttpServletRequest request) throws SQLException, ServiceException {
        String result = "{\"success\":false}";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = jobj.getString("userid");
            String start = jobj.getString("offset");
            String limit = jobj.getString("limit");
           // String companyid = jobj.getString("companyid");
            int start1 = Integer.parseInt(start);
            int limit1 = Integer.parseInt(limit);
            String output = getUpdatesAudit(request, userid, start1, limit1).toString();

            result = "{\"valid\":true,\"success\":true,\"data\":"+ output +"}";

        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"valid\":false,\"data\":{} }";
            throw ServiceException.FAILURE("remoteApi.getUpdates", e);
        }
        return result;
    }

    public String editUser(HttpServletRequest request, int action) throws ServiceException {
        String r =remoteAPIDAOObj.getMessage(1, 11, action);//"{\"success\": true, \"infocode\": \"m07\"}";
        KwlReturnObject kmsg = null;
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = "";
            boolean flag = false;
            if (jobj.has("userid")) {
                userid = StringUtil.serverHTMLStripper(jobj.get("userid").toString());
            } else {
                flag = true;
                r =remoteAPIDAOObj.getMessage(2, 1,action);//"{\"success\": false, \"errorcode\": \"e01\"}";
            }
            if (!flag) {
                String emailid = jobj.has("emailid")?jobj.getString("emailid").trim().replace(" ", "+"):"";
                String fname = jobj.has("fname")?StringUtil.serverHTMLStripper(jobj.get("fname").toString()):"";
                String lname = jobj.has("lname")?StringUtil.serverHTMLStripper(jobj.get("lname").toString()):"";
                String timezone = jobj.has("timezone")?StringUtil.serverHTMLStripper(jobj.get("timezone").toString()):"";
                emailid = jobj.has("emailid")?StringUtil.serverHTMLStripper(emailid):"";
                String contactno = jobj.has("contactno")?StringUtil.serverHTMLStripper(jobj.get("contactno").toString()):"";
                String address = jobj.has("address")?StringUtil.serverHTMLStripper(jobj.get("address").toString()):"";
                User u = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
                if(u!=null) {
                    HashMap<String, Object> userRequestParams = new HashMap<String, Object>();
                    userRequestParams.put("addUser", false);
                    userRequestParams.put("userid", userid);
                    userRequestParams.put("firstName", fname);
                    userRequestParams.put("lastName", lname);
                    userRequestParams.put("emailID", emailid);
                    userRequestParams.put("address", address);
                    userRequestParams.put("contactNumber", contactno);
                    userRequestParams.put("timeZone", timezone);
                    kmsg = profileHandlerDAOObj.saveUser(userRequestParams);
                } else {
                    r =remoteAPIDAOObj.getMessage(2, 6, action);
                }
            }
        } catch (JSONException e) {
            r =remoteAPIDAOObj.getMessage(2, 2, action);//"{\"success\": false, \"errorcode\": \"e02\"}";
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(r, e);
        } catch (Exception e) {
            r =remoteAPIDAOObj.getMessage(2, 2, action);//"{\"success\": false, \"errorcode\": \"e02\"}";
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(r, e);
        }
        return r;
    }

    private boolean checkTargetListIdEntry(DataSource ds, String targetListId) {
        boolean isPresent = false;
        try{
            JdbcTemplate template = new JdbcTemplate(ds);
            List<String> list = template.queryForList("select id from targetlist where id='"+targetListId+"'", String.class);
            for(String id:list) {
                if(!StringUtil.isNullOrEmpty(id))
                    isPresent = true;
            }
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            return isPresent;
        }
    }
    private String targetListEntry(HttpServletRequest request) throws SQLException, ServiceException, MalformedURLException {
        String result = "";
        try {
            boolean isPresent = false;
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String targetListId = jobj.getString("targetListId");
            Map<Object, Object> targetDataSources = routingDataSource.getTargetDataSources();
			for(Object entry:targetDataSources.entrySet()) {
				Map.Entry<Object, Object> e=(Map.Entry<Object, Object>)entry;
				isPresent = checkTargetListIdEntry(routingDataSource.getDataSourceFromKey(e.getValue()),targetListId);
                if(isPresent) {
                    StringBuffer params = new StringBuffer();
                    appendOriginalParams(request,params);
                    params.append("&action=101");
                    params.append("&cdomain="+routingDataSource.getDSSingleLookupMap().get(e.getKey()));
                    URL u = new URL(request.getRequestURL().toString());
                    result = callURL(u, params);
                    break;
                }
			}
            // for default database
            if(!isPresent) {
                result = addTargetListEntry(request);
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"errormsg:\" \"Error while adding entry in Target List:'" + e.getMessage() + "' \"}";
            throw ServiceException.FAILURE("remoteApi.targetListEntry", e);
        }
        return result;
    }

    private String addTargetListEntry(HttpServletRequest request) throws SQLException, ServiceException{
        String result = "";
        KwlReturnObject kmsg = null;
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String username = jobj.getString("username");
            String pwd = jobj.getString("password");
            String targetName = jobj.getString("targetName");
            String targetEmail = jobj.getString("targetEmail");
            String targetListId = jobj.getString("targetListId");
            String fname = "";
            String lname = "";
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("t.id");
            filter_params.add(targetListId);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);

            kmsg = crmEmailMarketingDAOObj.getTargetList(requestParams);
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                TargetList targetListObj = (TargetList) ite.next();
                String uname = targetListObj.getCreator().getUserLogin().getUserName();
                String password = targetListObj.getCreator().getUserLogin().getPassword();
                String userid = targetListObj.getCreator().getUserLogin().getUserID();
                String companyid = targetListObj.getCreator().getCompany().getCompanyID();

                if(uname.equals(username)&& password.equals(pwd)){
                    String id = java.util.UUID.randomUUID().toString();

                    JSONObject ttListJobj = new JSONObject();
                    ttListJobj.put("fname", targetName);
                    ttListJobj.put("email", targetEmail);
                    ttListJobj.put("targetlistid", targetListId);
                    ttListJobj.put("deleteflag", 0);
                    ttListJobj.put("relatedid", id);
                    ttListJobj.put("relatedto", 4);
                    crmEmailMarketingDAOObj.saveTargetsForTemp(ttListJobj);

                    String emailRegex = Constants.emailRegex;
                    int validflag = 1;
                    if (!targetEmail.matches(emailRegex)) {
                        validflag = 0;
                    }
                    int pos = targetName.indexOf(' ');
                    if (pos <= 0) {
                        lname = targetName.trim();
                    } else {
                        String[] tname = targetName.split(" ");
                        fname = tname[0];
                        lname = targetName.substring(pos, targetName.length());
                    }
                    JSONObject tmodJobj = new JSONObject();
                    tmodJobj.put("companyid", companyid);
                    tmodJobj.put("firstname", fname);
                    tmodJobj.put("lastname", lname);
                    tmodJobj.put("email", targetEmail);
                    tmodJobj.put("userid", userid);
                    tmodJobj.put("updatedon", new Date());
                    tmodJobj.put("targetModuleownerid", userid);
                    tmodJobj.put("targetModuleid", id);
                    tmodJobj.put("validflag", validflag);
                    tmodJobj.put("createdon", new Date());
                    crmTargetDAOObj.addTargets(tmodJobj);
                }

            }
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"errormsg:\" \"Error while adding entry in Target List:'" + e.getMessage() + "' \"}";
            throw ServiceException.FAILURE("remoteApi.targetListEntry:" + e.getMessage(), e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"errormsg:\" \"Error while adding entry in Target List:'" + e.getMessage() + "' \"}";
            throw ServiceException.FAILURE("remoteApi.targetListEntry", e);
        }
        return result;
    }

    private boolean checkUserIdEntry(DataSource ds, String userid) {
        boolean isPresent = false;
        try{
            JdbcTemplate template = new JdbcTemplate(ds);
            List<String> list = template.queryForList("select userid from users where userid='"+userid+"'", String.class);
            for(String id:list) {
                if(!StringUtil.isNullOrEmpty(id))
                    isPresent = true;
            }
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            return isPresent;
        }
    }

    private String getUserTargets(HttpServletRequest request) throws SQLException, ServiceException, MalformedURLException {
        String result = "";
        try {
            boolean isPresent = false;
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = jobj.getString("userid");
            Map<Object, Object> targetDataSources = routingDataSource.getTargetDataSources();

			for(Object entry:targetDataSources.entrySet()) {
				Map.Entry<Object, Object> e=(Map.Entry<Object, Object>)entry;
				isPresent = checkUserIdEntry(routingDataSource.getDataSourceFromKey(e.getValue()),userid);
                if(isPresent) {
                    StringBuffer params = new StringBuffer();
                    appendOriginalParams(request,params);
                    params.append("&action=102");
                    params.append("&cdomain="+routingDataSource.getDSSingleLookupMap().get(e.getKey()));
                    URL u = new URL(request.getRequestURL().toString());
                    result = callURL(u, params);
                    break;
                }
            }
            // for default database
            if(!isPresent) {
                try {
                    result = getUserTargetsFromDB(request);
                } catch(SessionExpiredException ex) {
                    result = "{\"success\":false,\"errormsg:\" \"Error while adding entry in Target List:'" + ex.getMessage() + "' \"}";
                }  catch(ParseException ex) {
                    result = "{\"success\":false,\"errormsg:\" \"Error while adding entry in Target List:'" + ex.getMessage() + "' \"}";
                }
            }

        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"errormsg:\" \"Error while adding entry in Target List:'" + e.getMessage() + "' \"}";
            throw ServiceException.FAILURE("remoteApi.targetListEntry", e);
        }
        return result;
    }

    private String getUserTargetsFromDB(HttpServletRequest request) throws SQLException, ServiceException, SessionExpiredException, ParseException, MalformedURLException {
        String result = "";
        JSONArray resultArr = new JSONArray();
        KwlReturnObject kmsg = null;
        String start="" ;
        String limit="";
        String ss="";
        String field="";
        String direction="";
        Long fromDate = null;
        Long toDate = null;
        String relatedto="";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String userid = jobj.getString("userid");
            String companyid = jobj.getString("companyid");
            User userObj = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
            if(userObj != null) {
                ArrayList filter_names = new ArrayList();
                ArrayList filter_params = new ArrayList();
                kmsg = goalManagementService.getActiveFinalGoals(userid, false,start,limit,ss,relatedto,field,direction,fromDate,toDate);
                Iterator ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    JSONObject tmpObj = new JSONObject();
                    Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                    filter_names.clear();
                    filter_params.clear();
                    double dl=0;
                    double percentVal = 0;
                    int reltTo = fgmt.getRelatedto()== null?0:fgmt.getRelatedto();
                    dl = goalManagementService.getAchievedTarget( fgmt, companyid, userObj );
                    percentVal = goalManagementService.getPercentageTarget(fgmt, dl);
                    String relatedName = goalManagementService.getGoalName(reltTo);

                    DateFormat udf= KwlCommonTablesDAOObj.getUserDateFormatter1(userObj, KWLDateFormat.DATE_PART, RemoteAPIDAO.DEFAULTDATEFORMATZONE, RemoteAPIDAO.DEFAULTTIMEZONE);
                    tmpObj.put("gname", "Target for "+relatedName+" between "+udf.format(fgmt.getStartdate())+" and "+udf.format(fgmt.getEnddate())+": "+fgmt.getTargeted()+", Achieved : "+dl);
                    tmpObj.put("gid",fgmt.getId());
                    tmpObj.put("gdescription", fgmt.getGoaldesc());
                    tmpObj.put("percentgoal", percentVal);
                    tmpObj.put("gstartdate", fgmt.getStartdate());
                    tmpObj.put("genddate", fgmt.getEnddate());
                    tmpObj.put("userid", userid);
                    tmpObj.put("logtext", 1); // for edit mode
                    resultArr.put(tmpObj);
                }
                result = "{\"valid\":true,\"success\":true,\"data\":"+ resultArr.toString() +"}";
            } else {
                result =remoteAPIDAOObj.getMessage(2, 6, 12);
            }

        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"errormsg:\" \"Error while fetching users targets :'" + e.getMessage() + "' \"}";
            throw ServiceException.FAILURE("remoteApi.getUserTargets:" + e.getMessage(), e);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"errormsg:\" \"Error while fetching users targets :'" + e.getMessage() + "' \"}";
            throw ServiceException.FAILURE("remoteApi.getUserTargets", e);
        }
        return result;
    }

    private String copyMasterItems(Company company) throws ServiceException {
        String result = "";
         try {
             HashMap<String, Object> requestParams = new HashMap<String, Object>();
//             User user = (User)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
             KwlReturnObject kmsg = crmManagerDAOObj.getComboMasterData();
             List list = kmsg.getEntityList();
             Iterator iter = list.iterator();
             while (iter.hasNext()) {
                 CrmCombodata crmCombodata = (CrmCombodata) iter.next();

                 requestParams.put("name", crmCombodata.getRawvalue());
                 requestParams.put("configid", crmCombodata.getCrmCombomaster().getMasterid());
                 requestParams.put("companyid", company.getCompanyID());
                 requestParams.put("mainid", crmCombodata.getValueid());
                 requestParams.put("comboid", crmCombodata.getValueid());
                 requestParams.put("isedit", crmCombodata.isIsEdit());
                 requestParams.put("percentStage", crmCombodata.getPercentStage());
                 crmManagerDAOObj.addMasterData(requestParams);
             }
         } catch (Exception ex) {
             logger.warn(ex.getMessage(), ex);
             throw ServiceException.FAILURE("remoteApi.copyMasterItems", ex);
         }
         return result;
    }

    private String copyDefaultTemplates(User user) throws ServiceException {
        String result = "";
        try {
             HashMap requestParams = new HashMap();
             KwlReturnObject kmsg = crmEmailMarketingDAOObj.getDefaultEmailTemplate(requestParams);
             List list = kmsg.getEntityList();
             Iterator iter = list.iterator();
             while (iter.hasNext()) {
                DefaultTemplates defaultTemplates = (DefaultTemplates) iter.next();
                JSONObject jobj = new JSONObject();
                jobj.put("body", defaultTemplates.getBody());
                jobj.put("tbody", defaultTemplates.getBody_html());
                jobj.put("createdon", new Date());
                jobj.put("userid", user.getUserID());
                jobj.put("deleted", 0);
                jobj.put("description", defaultTemplates.getDescription());
                jobj.put("name", defaultTemplates.getName());
                jobj.put("subject", defaultTemplates.getSubject());
                jobj.put("thumbnail", defaultTemplates.getThumbnail());
                crmEmailMarketingDAOObj.addEmailTemplate(jobj);

            //                 emailTemplate.setDeleted(0);
            //                 emailTemplate.setDescription(defaultTemplates.getDescription());
            //                 emailTemplate.setName(defaultTemplates.getName());
            //                 emailTemplate.setSubject(defaultTemplates.getSubject());
            //                 emailTemplate.setThumbnail(defaultTemplates.getThumbnail());
            //                 session.save(emailTemplate);
             }
        } catch (Exception ex) {
             logger.warn(ex.getMessage(), ex);
             throw ServiceException.FAILURE("remoteApi.copyDefaultTemplates", ex);
        }
         return result;
    }

    private String onLineSubscriber(HttpServletRequest request) throws SQLException, ServiceException {
        String result = "";
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));
            String targetName = jobj.getString("name");
            String targetEmail = jobj.getString("email");
            String targetListId = jobj.getString("newsletters");
            String fname = "";
            String lname = "";
            JSONArray jarr =new JSONArray(targetListId);
            for(int i = 0 ; i< jarr.length() ;i++ ) {

                String id = java.util.UUID.randomUUID().toString();
                String listid ="";

                if(jarr.getJSONObject(i).has("crm"))
                    listid = jarr.getJSONObject(i).getString("crm");
                else if(jarr.getJSONObject(i).has("hrms"))
                    listid = jarr.getJSONObject(i).getString("hrms");
                else if(jarr.getJSONObject(i).has("projectmanagement"))
                    listid = jarr.getJSONObject(i).getString("projectmanagement");
                else if(jarr.getJSONObject(i).has("accounting"))
                    listid = jarr.getJSONObject(i).getString("accounting");
                else if(jarr.getJSONObject(i).has("lms"))
                    listid = jarr.getJSONObject(i).getString("lms");
                else if(jarr.getJSONObject(i).has("eleave"))
                    listid = jarr.getJSONObject(i).getString("eleave");


                TargetList targetObj = (TargetList) KwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.TargetList", listid);
                User user = targetObj.getCreator();

                // Target entry in Target module
                String emailRegex = Constants.emailRegex;
                int validflag = 1;
                if (!targetEmail.matches(emailRegex)) {
                    validflag = 0;
                }
                int pos = targetName.indexOf(' ');
                if (pos <= 0) {
                    lname = targetName.trim();
                } else {
                    String[] tname = targetName.split(" ");
                    fname = tname[0];
                    lname = targetName.substring(pos, targetName.length());
                }
                JSONObject tmodJobj = new JSONObject();
                tmodJobj.put("companyid", user.getCompany().getCompanyID());
                tmodJobj.put("firstname", fname);
                tmodJobj.put("lastname", lname);
                tmodJobj.put("email", targetEmail);
                tmodJobj.put("userid", user.getUserID());
                tmodJobj.put("updatedon", new Date());
                tmodJobj.put("targetModuleownerid", user.getUserID());
                tmodJobj.put("targetModuleid", id);
                tmodJobj.put("validflag", validflag);
                tmodJobj.put("createdon", new Date());
                crmTargetDAOObj.addTargets(tmodJobj);

                // Target entry in Target List
                JSONObject ttListJobj = new JSONObject();
                ttListJobj.put("fname", targetName);
                ttListJobj.put("email", targetEmail);
                ttListJobj.put("targetlistid", targetListId);
                ttListJobj.put("deleteflag", 0);
                ttListJobj.put("relatedid", id);
                ttListJobj.put("relatedto", 4);
                crmEmailMarketingDAOObj.saveTargetsForTemp(ttListJobj);

            }
            result = "{\"valid\":true,\"success\":true,\"data\":"+ jarr.toString() +"}";

        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"errormsg:\" \"Error while adding entry in Target List:'" + e.getMessage() + "' \"}";
            throw ServiceException.FAILURE("remoteApi.assignRole", e);
        }
        return result;
    }

    private String saveOutlookContacts(HttpServletRequest request) throws SQLException, ServiceException {
        String result = "";
        KwlReturnObject kmsg;
        try {
            JSONObject jobj = new JSONObject(request.getParameter("data"));

            String action1 = jobj.getString("action");
            String recArr = jobj.getString("data");
            String username = request.getParameter("username");
            String subdomain = request.getParameter("subdomain");
            String password = request.getParameter("password");
            String ipAddress = SystemUtil.getIpAddress(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("u.userLogin.userName");
            filter_names.add("u.userLogin.password");
            filter_names.add("u.company.subDomain");
            filter_params.add(username);
            filter_params.add(password);
            filter_params.add(subdomain);

            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);

//            String query = "select u.userID ,u.company.companyID from User u  where u.userLogin.userName= ? and u.userLogin.password= ? and u.company.subDomain= ? ";
//            List list = HibernateUtil.executeQuery(session, query, new Object[]{username, password, subdomain});
            Iterator ite = kmsg.getEntityList().iterator();
            List list = kmsg.getEntityList();
            while (ite.hasNext()) {
                User userObj = (User) ite.next();
                String userid = userObj.getUserID();
                String companyid = userObj.getCompany().getCompanyID();
                KWLTimeZone tzdiff = authHandler.getTZforUser(userObj,userObj.getCompany(),(KWLTimeZone) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.KWLTimeZone", "1"));

                ////  ADD
                if (StringUtil.equal(action1, "add")) {
                    String[] arr = recArr.split(";");
                    for (int i = 0; i < arr.length; i++) {
                        String[] pArr = arr[i].split(",");
                        String contactid = java.util.UUID.randomUUID().toString();
                        saveContact(pArr, userid, companyid, false, contactid,tzdiff, ipAddress);
                    }

                } else if (StringUtil.equal(action1, "update")) {  //Update


                    String[] arr = recArr.split(";");
                    for (int i = 0; i < arr.length; i++) {
                        String[] pArr = arr[i].split(",");

                        filter_names.clear();
                        filter_params.clear();
                        requestParams.clear();
                        filter_names.add("c.deleteflag");
                        filter_params.add(0);
                        filter_names.add("c.validflag");
                        filter_params.add(1);
                        filter_names.add("c.isarchive");
                        filter_params.add(false);
                        filter_names.add("c.company.companyID");
                        filter_params.add(companyid);
                        filter_names.add("c.email");
                        filter_params.add(pArr[2]);
                        requestParams.put("heirarchyPerm", true);
                        kmsg = crmContactDAOObj.getContacts(requestParams, null, filter_names, filter_params);

//                        String query1 = "select contact.contactid ,contact.email from CrmContact as contact where contact.deleteflag=0 and contact.validflag=1  and contact.email = ? and contact.isarchive= ? and contact.company.companyID= ? ";
//                        List list1 = HibernateUtil.executeQuery(session, query1, new Object[]{pArr[2], false, companyid});
                        Iterator ite1 = kmsg.getEntityList().iterator();
                        while (ite1.hasNext()) {
                            CrmContact contactObj = (CrmContact) ite1.next();
                            String contactid = (String) contactObj.getContactid();
                            saveContact(pArr, userid, companyid, true, contactid,tzdiff,ipAddress);
                        }

                    }
                } else if (StringUtil.equal(action1, "sync")) { // Sync
                    String[] arr = recArr.split(";");
                    for (int i = 0; i < arr.length; i++) {
                        if (!StringUtil.isNullOrEmpty(arr[i])) {
                            String[] pArr = arr[i].split(",");
                            try {
                                filter_names.clear();
                                filter_params.clear();
                                requestParams.clear();
                                filter_names.add("c.deleteflag");
                                filter_params.add(0);
                                filter_names.add("c.validflag");
                                filter_params.add(1);
                                filter_names.add("c.isarchive");
                                filter_params.add(false);
                                filter_names.add("c.company.companyID");
                                filter_params.add(companyid);
                                filter_names.add("c.email");
                                filter_params.add(pArr[2]);
                                requestParams.put("heirarchyPerm", true);
                                kmsg = crmContactDAOObj.getContacts(requestParams, null, filter_names, filter_params);

    //                            String query2 = "select contact.contactid ,contact.email from CrmContact as contact where contact.deleteflag=0 and contact.validflag=1  and contact.email = ? and contact.isarchive= ? and contact.company.companyID= ? ";
    //                            List list2 = HibernateUtil.executeQuery(session, query2, new Object[]{pArr[2], false, companyid});
                                Iterator ite2 = kmsg.getEntityList().iterator();
                                if (kmsg.getEntityList().size() > 0) {
                                    while (ite2.hasNext()) {
                                        CrmContact contactObj = (CrmContact) ite2.next();
                                        String contactid = (String) contactObj.getContactid();
                                        saveContact(pArr, userid, companyid, true, contactid,tzdiff,ipAddress);
                                    }
                                } else {
                                    String contactid = java.util.UUID.randomUUID().toString();
                                    saveContact(pArr, userid, companyid, false, contactid,tzdiff,ipAddress);
                                }
                            } catch(Exception ex) {
                                logger.warn(ex.getMessage(), ex);
                            }
                        }
                    }


                    filter_names.clear();
                    filter_params.clear();
                    //ToDo: Add sync flag in contacts table after implementing delete functionality
                    filter_names.add("c.deleteflag");
                    filter_names.add("c.isarchive");
                    filter_names.add("c.company.companyID");
                    filter_names.add("c.validflag");
                    filter_params.add(0);
                    filter_params.add(false);
                    filter_params.add(companyid);
                    filter_params.add(1);
//                    filter_params.add(true);
                    StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
                    filter_names.add("INco.usersByUserid.userID");
                    filter_params.add(usersList);
//                    requestParams.put("usersList", usersList);

                    ArrayList order_by =  new ArrayList();
                    ArrayList order_type =  new ArrayList();
                    order_by.add("c.firstname");
                    order_type.add("asc");

                    if(!StringUtil.isNullOrEmpty(request.getParameter("start")))
                        requestParams.put("start", Integer.parseInt(request.getParameter("start")));
                    if(!StringUtil.isNullOrEmpty(request.getParameter("limit")))
                        requestParams.put("limit", Integer.parseInt(request.getParameter("limit")));
                    requestParams.put("pagingFlag", true);
                    requestParams.put("totalCountFlag", true);
                    requestParams.put("distinctFlag", true);
                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_params", filter_params);
                    requestParams.put("order_by", order_by);
                    requestParams.put("order_type", order_type);

                    kmsg = crmContactDAOObj.getContactOwners(requestParams);


//                    String Hql = "select distinct c from contactOwners co inner join co.contact c where  c.deleteflag=0 and c.company.companyID= ?  and c.validflag=1 and c.isarchive=false";
//                    String selectInQuery = Hql;
//                        selectInQuery += "  and co.usersByUserid.userID in (" + usersList + ")   ";
//                        selectInQuery += " order by c.firstname  ";
//                    int start = Integer.parseInt(request.getParameter("start"));
//                    int limit = Integer.parseInt(request.getParameter("limit"));
//                    List list3 = null;
//
//                    list3 =HibernateUtil.executeQueryPaging(session, selectInQuery, new Object[]{companyid},new Integer[]{start,limit});
                    double count = 0;
                    Iterator ite3 = kmsg.getEntityList().iterator();
                    count = kmsg.getEntityList().size();
                    String data = "";
                    int recCount = 0;
                    while (ite3.hasNext()) {
                        CrmContact contactObj = (CrmContact) ite3.next();
                        if (recCount != 0) {
                            data += "\\n";
                        }
                        data += contactObj.getFirstname();
                        data += "," + contactObj.getLastname();
                        data += "," + contactObj.getEmail();
                        data += "," + contactObj.getPhoneno();
                        data += "," + contactObj.getMobileno();
                        data += ",";
                        recCount++;
                    }
                    result = "{\"success\":true,\"errormsg\":\"\",\"data\":\"" + data + "\",\"count\":\"" + count + "\"}";

                }

            }
            if (list.size() == 0) {
                result = "{\"success\":false,\"errormsg\":\"User Authentication Failed.Please check your account information\"}";
            }

        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"errormsg:\" \"Error while adding entry in Contact List:'" + e.getMessage() + "' \"}";
            throw ServiceException.FAILURE("remoteApi.assignRole", e);
        }
        return result;
    }

    public void saveContact(String[] pArr, String userid, String companyid, boolean editFlag, String contactid, KWLTimeZone tzdiff, String ipAddress) throws ServiceException {
        try {
            KwlReturnObject kmsg = null;
            if(!editFlag)
                contactid = "0";
            int validflag = 1;
            if (StringUtil.isNullOrEmpty(pArr[1].trim()) || StringUtil.isNullOrEmpty(pArr[2].trim())) {
                validflag = 0;
            }

            JSONObject jobj = new JSONObject();
            jobj.put("firstname", pArr[0]);
            jobj.put("lastname", pArr[1]);
            jobj.put("email", pArr[2]);
            jobj.put("phone", pArr[3]);
            jobj.put("mobile", pArr[4]);
            jobj.put("street", pArr[5]);
            jobj.put("contactid", contactid);
            jobj.put("contactownerid", userid);
            jobj.put("validflag", validflag);
            jobj.put("accountid", "0");
            contactManagementService.saveContact(companyid, userid, tzdiff.getDifference(), ipAddress, jobj);

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("remoteAPIController.saveContact", ex);
        }
    }

    public String syncAccountingProduct(HttpServletRequest request, HttpServletResponse response, int action) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            boolean isPresent = false;
            String data= request.getParameter("data");
            JSONObject jobj = new JSONObject(data);
            String userid = jobj.get("userid").toString();
            Map<Object, Object> targetDataSources = routingDataSource.getTargetDataSources();
			for(Object entry:targetDataSources.entrySet()) {
				Map.Entry<Object, Object> e=(Map.Entry<Object, Object>)entry;
				isPresent = checkUserIdEntry(routingDataSource.getDataSourceFromKey(e.getValue()),userid);
                if(isPresent) {
                    StringBuffer params = new StringBuffer();
                    appendOriginalParams(request,params);
                    params.append("&action=103");
                    params.append("&cdomain="+routingDataSource.getDSSingleLookupMap().get(e.getKey()));
                    URL u = new URL(request.getRequestURL().toString());
                    result = callURL(u, params);
                    break;
                }
			}
            // for default database
            if(!isPresent) {
                result = syncAndAddAccountingProduct(request,response,action);
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"errormsg:\" \"Error while synchronizing product list with accounting:'" + e.getMessage() + "' \",'syncaccounting' : false,\"companyexist\":true}";

        }
        return result;
    }

    public String syncAndAddAccountingProduct(HttpServletRequest request, HttpServletResponse response, int action) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            String data= request.getParameter("data");
            JSONObject jobj = new JSONObject(data);
            String companyid = jobj.get("companyid").toString();
            String userid = jobj.get("userid").toString();
            String companyExists = CompanyidExits(request, action);
            JSONObject companyExistsJson = new JSONObject(companyExists);
            if(companyExistsJson.has("infocode") && StringUtil.equal(companyExistsJson.getString("infocode"), "m01")){
                fetchAccountingProducts(request,response,companyid,userid,true);
            } else{
                return result = "{\"success\":true, \"msg\" : \"Company does not exist.\",\"companyexist\":false}";
            }
            result = "{\"success\":true, 'msg': 'Product list has been synchronized successfully.','syncaccounting' : true,\"companyexist\":true}";

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"errormsg:\" \"Error while synchronizing product list with accounting:'" + e.getMessage() + "' \",'syncaccounting' : false,\"companyexist\":true}";

        }
        return result;
    }

    public JSONObject fetchAccountingProducts(HttpServletRequest request, HttpServletResponse response,String companyId, String userid,boolean remoteCall) throws ServiceException, SessionExpiredException, ParseException {
        JSONObject jobj1 = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            if(remoteCall){
                String data = request.getParameter("data");
                jobj1 = new JSONObject(data);
                jobj1 = new JSONObject(jobj1.getString("data"));
                jarr = new JSONArray(jobj1.getString("typedata"));

                // Save Product Type (Category Type) if it doesn't exist in CRM Product Category list
                saveProductType(jarr,companyId);

                // Processing json according to CRM and Save Products
                jobj1=beforeSaveProduct(request,companyId,userid,jobj1);

            } else {
                JSONObject userData = new JSONObject();
                userData.put("iscommit", true);
                userData.put("start", request.getParameter("start"));
                userData.put("limit", request.getParameter("limit"));
                userData.put("companyid", companyId);
                String action = "11";
                //String accURL = this.getServletContext().getInitParameter("accURL");
                String accURL = ConfigReader.getinstance().get("accURL");
                JSONObject resObj = apiCallHandlerService.callApp(accURL, userData, companyId, action, true);

                if (!resObj.isNull("success") && resObj.getBoolean("success")) {
                    jobj1=new JSONObject(resObj.getString("data"));
                    jarr = jobj1.getJSONArray("typedata");

                    // Save Product Type (Category Type) if it doesn't exist in CRM Product Category list
                    saveProductType(jarr,companyId);

                    // Processing json according to CRM and Save Products
                    jobj1=beforeSaveProduct(request,companyId,userid,jobj1);
                } else {
                    return resObj;
             }

            }

        } catch (Exception ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return jobj1;
    }

    public void saveProductType( JSONArray jarr,String companyid) throws ServletException {
       List ll = null;
        try{
             for (int j = 0; j < jarr.length(); j++) {
                JSONObject tempJson = jarr.getJSONObject(j);
                String id= tempJson.getString("id");
                ArrayList filter_names = new ArrayList();
                ArrayList filter_params = new ArrayList();
                filter_names.add("d.ID");
                filter_params.add(id);
                HashMap<String, Object> requestParamscombo = new HashMap<String, Object>();
                requestParamscombo.put("companyid", companyid);
                requestParamscombo.put("filter_names", filter_names);
                requestParamscombo.put("filter_params", filter_params);
                ll = crmManagerDAOObj.getComboData("Product Category", requestParamscombo);

                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("companyid", companyid);
                requestParams.put("name", tempJson.getString("name"));
                requestParams.put("configid", Constants.PRODUCTTYPE_COMBOID);
                requestParams.put("id", id);
                requestParams.put("mainid", id);

                if(ll.size()>0) {
                    crmManagerDAOObj.editMasterData(requestParams);
                } else {
                    crmManagerDAOObj.addMasterData(requestParams);
                }
            }

       } catch (Exception e) {
           logger.warn(e.getMessage(),e);
       }
    }

    public JSONObject beforeSaveProduct(HttpServletRequest request, String companyid, String userid,JSONObject jobj1) throws ServiceException, SessionExpiredException, ParseException {
         JSONArray jarr = new JSONArray();
         try {
            jarr = jobj1.getJSONArray("productdata");

            for(int i = 0 ; i < jarr.length() ; i++){
                JSONObject jobj2=jarr.getJSONObject(i);
                jobj2.put("unitprice", jobj2.getString("purchaseprice"));
                jobj2.put("categoryid", jobj2.getString("type"));
                jobj2.put("pname", jobj2.getString("productname"));
                jobj2.put("deleteflag", "0");

                saveProduct(request,jobj2,companyid,userid);
            }

            jobj1.put("success", true);
            jobj1.put("msg", "Product(s) added successfully.");

        } catch (Exception ex) {
            logger.warn(ex.getMessage(),ex);
        }
        return jobj1;
    }
    public void saveProduct(HttpServletRequest request, JSONObject jobj,String companyid,String userid) throws ServletException {
       KwlReturnObject kmsg = null;
       try{

            String id = jobj.getString("productid");
            jobj.put("ownerid", userid);
            jobj.put("userid", userid);
            jobj.put("companyid", companyid);
            jobj.put("updatedon", new Date());
            jobj.put("syncaccounting", true);
            KWLTimeZone kt = (KWLTimeZone) authHandler.getTZforUser((User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid), (Company) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyid), (KWLTimeZone) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.KWLTimeZone", "1"));
            jobj.put("tzdiff",kt.getDifference());

            Iterator tempite = null;
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.productid");
            filter_params.add(id);
            filter_names.add("c.deleteflag");
            filter_params.add(0);
            filter_names.add("c.isarchive");
            filter_params.add(false);
            HashMap<String, Object> requestParamscombo = new HashMap<String, Object>();
            requestParamscombo.put("filter_names", filter_names);
            requestParamscombo.put("filter_values", filter_params);

            kmsg = crmProductDAOObj.getAllProducts(requestParamscombo);
            tempite = kmsg.getEntityList().iterator();
            if(tempite.hasNext()) {
                kmsg = crmProductDAOObj.editProducts(jobj);
            } else {
                kmsg = crmProductDAOObj.addProducts(jobj);

            }
            String ipaddr = null;
            if(StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))){
                ipaddr = request.getRemoteAddr();
            }else{
                ipaddr = request.getHeader("x-real-ip");
            }
            auditTrailDAOObj.insertAuditLog(AuditAction.PRODUCT_IMPORT_ACCOUNTING,
                            ((jobj.getString("productname"))) + " - Product imported from Accounting ",
                            ipaddr,userid, id);

       } catch (Exception e) {
           logger.warn(e.getMessage(),e);
       }
    }

    private boolean checkEmailTrackIdEntry(DataSource ds, String trackid) {
        boolean isPresent = false;
        try{
            JdbcTemplate template = new JdbcTemplate(ds);
            List<String> list = template.queryForList("select id from campaign_log where targettrackerkey='"+trackid+"'", String.class);
            for(String id:list) {
                if(!StringUtil.isNullOrEmpty(id))
                    isPresent = true;
            }
        } catch(Exception ex) {
            isPresent = false;
            logger.warn(ex.getMessage(), ex);
        } finally {
            return isPresent;
        }
    }
    
    public String viewedEmailMarketMail(HttpServletRequest request) throws ServletException, JSONException, MalformedURLException {
        String result = "";
        try {
            boolean isPresent = false;
            String trackid = request.getParameter("trackid");
            Map<Object, Object> targetDataSources = routingDataSource.getTargetDataSources();
            for (Object entry : targetDataSources.entrySet()) {
                Map.Entry<Object, Object> e = (Map.Entry<Object, Object>) entry;
                isPresent = checkEmailTrackIdEntry(routingDataSource.getDataSourceFromKey(e.getValue()), trackid);
                if (isPresent) {
                    StringBuffer params = new StringBuffer();
                    appendOriginalParams(request, params);
                    params.append("&action=104");
                    params.append("&cdomain=" + routingDataSource.getDSSingleLookupMap().get(e.getKey()));
                    URL u = new URL(URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull)+"remoteapi.jsp");
                    result = callURL(u, params);
                    break;
                }
            }
            // for default database
            if (!isPresent) {
                result = markViewedEmailMarketMail(request);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return result;
    }
    
    public String markViewedEmailMarketMail(HttpServletRequest request) throws ServletException, JSONException, MalformedURLException {
        String htmlString = "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><link rel=\"shortcut icon\" href=\"../../images/deskera/deskera.png\"/></head>";
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("trackid", request.getParameter("trackid"));
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.viewedEmailMarketMail(requestParams);

            // Capture Lead while reading email : Make an entry of target in Lead module while he/she opens email
            if(kmsg.getRecordTotalCount()>0){
                kmsg = crmEmailMarketingDAOObj.captureLeadFromCampaign(kmsg.getEntityList());
            }

        } catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
            htmlString = "<div style='font-color:red;'><b>Failed to process request</b></div>";
        } finally {
            htmlString += "</html>";
        }
        return htmlString;
    }

    public String deleteCompany(HttpServletRequest request, int action, JSONObject jobj) throws ServiceException {
        String result = "";
        try {
            String comp = CompanyidExits(request, action);
            JSONObject cj = new JSONObject(comp);
            if (cj.has("infocode") && cj.getString("infocode").equals("m01")) {
                String companyid = jobj.getString("companyid");
                String[] queries = createQueryArray();
                remoteAPIDAOObj.executeNativeUpdateForDeleteCompany("SET foreign_key_checks =?", new Object[]{0});
                for(int i = 0; i < queries.length; i++) {
            		int rowcnt = remoteAPIDAOObj.executeNativeUpdateForDeleteCompany(queries[i], new Object[]{companyid});
                    System.out.println("Row count "+rowcnt+" for query "+queries[i]);
                }
                remoteAPIDAOObj.executeNativeUpdateForDeleteCompany("SET foreign_key_checks = ?", new Object[]{1});
                result = remoteAPIDAOObj.getMessage(1, 15, action);
            } else {
                result = cj.toString();
            }
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(),ex);
            throw ServiceException.FAILURE("", ex);
        } catch (SQLException ex) {
            logger.warn(ex.getMessage(),ex);
            throw ServiceException.FAILURE("", ex);
        }
        return result;
    }

    public String[] createQueryArray() throws ServiceException{
    	String[] queries = {"DELETE FROM  CompanyPreferences where companyid=?",
                    "DELETE FROM  apiresponse where companyid=?",
                    "DELETE FROM  assignmanager where empid in (select userid from  users where company=?)",
                    "DELETE FROM  audit_trail where user in (select userid from  users where company=?)",
                    "DELETE FROM  userlogin where userid in (select userid from  users where company=?)",
                    "DELETE FROM  userpermission where roleId in (select id from  role_user_mapping where userId in (select userid from  users where company=?))",
                    "DELETE FROM  crm_docsmap where docid in (select docid from  crm_docs where userid in (select userid from  users where company=?))",
                    "DELETE FROM  widgetmanagement where user in (select userid from  users where company=?)",
                    "DELETE FROM  column_formulae where companyid = ?",
                    "DELETE FROM  column_header where company = ?",
                    "DELETE FROM  commission where company = ?",
                    "DELETE FROM  companyholiday where company = ?",
                    "DELETE FROM  crm_accountOwners where accountid in (select accountid from  crm_account where companyid = ?)",
                    "DELETE FROM  crm_accountProducts where accountid in (select accountid from  crm_account where companyid = ?)",
                    "DELETE FROM  crm_account_activity where accountid in (select accountid from  crm_account where companyid = ?)",
                    "DELETE FROM  crm_account_project where accountId in (select accountid from  crm_account where companyid = ?)",
                    "DELETE FROM  crm_campaign_activity where campaignid in (select campaignid from  crm_campaign where companyid = ?)",
                    "DELETE FROM  campaign_target where campaign in (select campaignid from  crm_campaign where companyid = ?)",
                    "DELETE FROM  emailmarkteing_targetlist where emailmarketingid in (select id from  emailmarketing where campaignid in (select campaignid from  crm_campaign where companyid = ?))",
                    "DELETE FROM  emailtemplate where creator in (select userid from  users where company=?)",
                    "DELETE FROM  emailtemplatefiles where creator in (select userid from  users where company=?)",
                    "DELETE FROM  crm_activity_master where companyid = ?",
                    "DELETE FROM  crm_caseProducts where caseid in (select caseid from  crm_case where companyid = ?)",
                    "DELETE FROM  crm_case_activity where caseid in (select caseid from  crm_case where companyid = ?)",
                    "DELETE FROM  crm_comment where userid in (select userid from  users where company=?)",
                    "DELETE FROM  crm_new_comment where userid in (select userid from  users where company=?)",
                    "DELETE FROM  crm_contactOwners where contactid in (select contactid from  crm_contact where companyid = ?)",
                    "DELETE FROM  crm_contact_activity where contactid in (select contactid from  crm_contact where companyid = ?)",
                    "DELETE FROM  crm_leadOwners where leadid in (select leadid from  crm_lead where companyid = ?)",
                    "DELETE FROM  crm_leadProducts where leadid in (select leadid from  crm_lead where companyid = ?)",
                    "DELETE FROM  crm_lead_activity where leadid in (select leadid from  crm_lead where companyid = ?)",
                    "DELETE FROM  crm_leadconversionmappings where companyid = ?",
                    "DELETE FROM  crm_opportunityOwners where opportunityid in (select oppid from  crm_opportunity where companyid = ?)",
                    "DELETE FROM  crm_oppurtunityProducts where oppid in (select oppid from  crm_opportunity where companyid = ?)",
                    "DELETE FROM  crm_opportunity_activity where opportunityid in (select oppid from  crm_opportunity where companyid = ?)",
                    "DELETE FROM  crm_product where companyid = ?",
                    "DELETE FROM  crmaccountcustomdata where company = ?",
                    "DELETE FROM  crmcasecustomdata where company = ?",
                    "DELETE FROM  crmcontactcustomdata where company = ?",
                    "DELETE FROM  crmleadcustomdata where company = ?",
                    "DELETE FROM  crmopportunitycustomdata where company = ?",
                    "DELETE FROM  crmproductcustomdata where company = ?",
                    "DELETE FROM  defaultmasteritem where companyid = ?",
                    "DELETE FROM  fieldcombodata where fieldid in (select id from  fieldParams where companyid = ?)",
                    "DELETE FROM  finalgoalmanagement where userid in (select userid from  users where company=?)",
                    "DELETE FROM  ideskeracrm_auth where user in (select userid from  users where company=?)",
                    "DELETE FROM  importlog where company = ?",
                    "DELETE FROM  myprofile where company = ?",
                    "DELETE FROM  outbound_smtpserver where companyid = ?",
                    "DELETE FROM  projreport_template where userid in (select userid from  users where company=?)",
                    "DELETE FROM  resetpwd where user in (select userid from  users where company=?)",
                    "DELETE FROM  scheduledmarketing where userid in (select userid from  users where company=?)",
                    "DELETE FROM  spreadsheet_config where user in (select userid from  users where company=?)",
                    "DELETE FROM  target_module where companyid = ?",
                    "DELETE FROM  targetlist_targets where targetlistid in (select id from  targetlist where creator in (select userid from  users where company=?))",
                    "DELETE FROM  usecommissionplan where userid in (select userid from  users where company=?)",
                    "DELETE FROM  webtolead_form where companyid = ?",
                    "DELETE FROM  zohoimport_log where companyid = ?",
                    "DELETE FROM  company_caseowner where companyid = ?",
                    "DELETE FROM  dashreport_config where companyid = ?",
                    "DELETE FROM  enum_email_type where company = ?",
                    "DELETE FROM  opp_reportconfig where companyid = ?",
                    "DELETE FROM  SAVED_SEARCH_QUERY where user in (select userid from  users where company = ?)",
                    "DELETE FROM  audit_log where userid in (select userid from  users where company = ?)",
                    "DELETE FROM  calendarusermap where userid in (select userid from  users where company = ?)",
                    "DELETE FROM  case_comment where userid in (select userid from  users where company = ?)",
                    "DELETE FROM  cr_reportcolumns where createdbyid in (select userid from  users where company = ?)",
                    "DELETE FROM  cr_reportlist where createdbyid in (select userid from  users where company = ?)",
                    "DELETE FROM  crm_docOwners where usersByUserid in (select userid from  users where company = ?)",
                    "DELETE FROM  crm_leadroutingusers where user in (select userid from  users where company = ?)",
                    "DELETE FROM  notification_recepients where userid in (select userid from  users where company = ?)",
                    "DELETE FROM  notification_request where createdbyid in (select userid from  users where company = ?)",
                    "DELETE FROM  notification_setting where userid in (select userid from  users where company = ?)",
                    "DELETE FROM  sharecalendarmap where userid in (select userid from  users where company = ?)",
                    "DELETE FROM  userrolemapping where userid in (select userid from  users where company = ?)",
                    "DELETE FROM  quotationdetails where quotation in (select id from  quotation where company = ?)",
                    "DELETE FROM  tax where company = ?",
                    "DELETE FROM  customer_docs where customerid in (select id from  crm_customer where companyid = ?)",
                    "DELETE FROM  calendarevents where cid in (SELECT cid from  calendars where userid in (select userid from  users where company = ?))",
                    "DELETE FROM  campaign_time_log where campaignlog in (SELECT id from  campaign_log where campaignid in (select campaignid from  crm_campaign where companyid = ?))",
                    "DELETE FROM  url_track_log where campaignlog in (SELECT id from  campaign_log where campaignid in (select campaignid from  crm_campaign where companyid = ?))",
                    "DELETE FROM  em_defaults where email_marketing in (SELECT id from  emailmarketing where campaignid in (select campaignid from  crm_campaign where companyid = ?))",
                    "DELETE FROM  calendars where userid in (select userid from  users where company = ?)",
                    "DELETE FROM  quotation where company = ?",
                    "DELETE FROM  crm_customer where companyid = ?",
                    "DELETE FROM  targetlist where creator in (select userid from  users where company=?)",
                    "DELETE FROM  fieldParams where companyid = ?",
                    "DELETE FROM  crm_opportunity where companyid = ?",
                    "DELETE FROM  crm_account where convertedleadid in (select leadid from crm_lead where companyid = ?)",
                    "DELETE FROM  crm_lead where companyid = ?",
                    "DELETE FROM  crm_contact where companyid = ?",
                    "DELETE FROM  crm_case where companyid = ?",
                    "DELETE FROM  campaign_log where campaignid in (select campaignid from  crm_campaign where companyid = ?)",
                    "DELETE FROM  emailmarketing where campaignid in (select campaignid from  crm_campaign where companyid = ?)",
                    "DELETE FROM  crm_campaign where companyid = ?",
                    "DELETE FROM  crm_account where companyid = ?",
                    "DELETE FROM  crm_docs where userid in (select userid from  users where company=?)",
                    "DELETE FROM  role_user_mapping where userId in (select userid from  users where company=?)",
                    "UPDATE company set creator=null where companyid= ?",
                    "DELETE FROM  users where company=?",
                    "DELETE FROM  company where companyid=?"};

    	return queries;
    }
    
    public String deactivateCompany(HttpServletRequest request, int action, JSONObject jobj) throws ServiceException {
        String result = "";
        try {
            String comp = CompanyidExits(request, action);
            JSONObject cj = new JSONObject(comp);
            if (cj.has("infocode") && cj.getString("infocode").equals("m01")) {
                String companyid = jobj.getString("companyid");
                HashMap<String, Object> companyRequestParams = new HashMap<String, Object>();
                companyRequestParams.put("companyid", companyid);
                companyRequestParams.put("activated", false);
                companyDetailsDAOObj.updateCompany(companyRequestParams);
                result = remoteAPIDAOObj.getMessage(1, 15, action);
            } else {
                result = cj.toString();
            }
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(),ex);
            throw ServiceException.FAILURE("", ex);
        } catch (SQLException ex) {
            logger.warn(ex.getMessage(),ex);
            throw ServiceException.FAILURE("", ex);
        }
        return result;
    }
    
    public boolean isCompanyActivated(HttpServletRequest request, JSONObject jobj, int action) throws ServiceException {
        boolean result = false;
        try {
            String comp = CompanyidExits(request, action);
            JSONObject cj = new JSONObject(comp);
            if (cj.has("infocode") && cj.getString("infocode").equals("m01")) {
                String companyid = jobj.getString("companyid");
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                ArrayList filter_names = new ArrayList();
                ArrayList filter_params = new ArrayList();
                filter_names.add("c.companyID");
                filter_params.add(companyid);
                filter_names.add("c.activated");
                filter_params.add(true);
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_params", filter_params);
                KwlReturnObject kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams);
                int count = kmsg.getEntityList().size();
                if (count > 0) {
                    result = true;
                }
            }
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(),ex);
            throw ServiceException.FAILURE("", ex);
        } catch (SQLException ex) {
            logger.warn(ex.getMessage(),ex);
            throw ServiceException.FAILURE("", ex);
        }
        return result;
    }
    /**
     * @return the sequencerService
     */
    public SequencerService getSequencerService()
    {
        return sequencerService;
    }
    /**
     * @param sequencerService the sequencerService to set
     */
    public void setSequencerService(SequencerService sequencerService)
    {
        this.sequencerService = sequencerService;
    }

}
