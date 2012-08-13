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
package com.krawler.spring.authHandler;
import com.krawler.common.admin.Company;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.Rolelist;
import com.krawler.common.admin.User;
import com.krawler.common.admin.SpreadSheetConfig;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import com.krawler.common.util.Constants;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.esp.handlers.APICallHandlerService;
import com.krawler.esp.handlers.Receiver;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.esp.web.resource.Links;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.companyDetails.companyDetailsDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.crm.spreadSheet.spreadSheetDAO;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.permissionHandler.permissionHandler;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.HashMap; 
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 *
 * @author Karthik
 */
public class authHandlerController extends MultiActionController {

    private authHandlerDAO authHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private permissionHandlerDAO permissionHandlerDAOObj;
    private companyDetailsDAO companyDetailsDAOObj;
    private String successView;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private spreadSheetDAO spreadSheetDAOObj;
    private APICallHandlerService apiCallHandlerService;
    private CometManagementService cometManagementService;
    
    private crmManagerDAO managerDAO;
    
    private static Log LOG = LogFactory.getLog(authHandlerController.class); 

    public void setApiCallHandlerService(APICallHandlerService apiCallHandlerService)
    {
        this.apiCallHandlerService = apiCallHandlerService;
    }
    
    public void setspreadSheetDAO(spreadSheetDAO spreadSheetDAOObj1) {
        this.spreadSheetDAOObj = spreadSheetDAOObj1;
    }
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setauthHandlerDAO(authHandlerDAO authHandlerDAOObj1) {
        this.authHandlerDAOObj = authHandlerDAOObj1;
    }

    public void setpermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
    }
    
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public void setcompanyDetailsDAO(companyDetailsDAO companyDetailsDAOObj1) {
        this.companyDetailsDAOObj = companyDetailsDAOObj1;
    }
    public JSONObject verifyUserLogin(HttpServletRequest request, HttpServletResponse response,String user,String pass,String login,String subdomain)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject rjobj = new JSONObject();
        JSONObject ujobj = new JSONObject();
        KwlReturnObject kmsg = null;
        String result = "";
        String userid = "";
        String companyid = "";
        HashMap<String, Object> requestParams2 = null;
        JSONObject obj = null, jret = new JSONObject();
        boolean isvalid = false;
        try {
            boolean isValidUser = false;

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("user", StringUtil.checkForNull(user));
            requestParams.put("pass", StringUtil.checkForNull(pass));
            requestParams.put("subdomain", StringUtil.checkForNull(subdomain));
            if (StringUtil.isNullOrEmpty(login)) {
                kmsg = authHandlerDAOObj.verifyLogin(requestParams);
                jobj = authHandler.getVerifyLoginJson(kmsg.getEntityList(), request);
                
                if (jobj.has("success") && (jobj.get("success").equals(true))) {
                    User userObject = null;
                    UserLogin userLogin = null;
                    Company company = null;

                    if (kmsg.getEntityList() != null && !kmsg.getEntityList().isEmpty()) {
                        Object[] row = (Object[]) kmsg.getEntityList().get(0);
                        userObject = (User) row[0];
                        userLogin = (UserLogin) row[1];
                        company = (Company) row[2];
                    }

                    obj = new JSONObject();
                    companyid = jobj.getString("companyid");
                    userid = jobj.getString("lid");

                    kmsg = permissionHandlerDAOObj.getRoleofUser(userid);
                    Iterator ite3 = kmsg.getEntityList().iterator();
                    while(ite3.hasNext()) {
                        Object[] row = (Object[]) ite3.next();
                        String roleid = row[0].toString();
                        jobj.put("roleid", roleid);
                    }

                    CompanyPreferences cmpPref = (CompanyPreferences) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CompanyPreferences", companyid);
                    obj = crmManagerCommon.getCompanyPreferencesJSON(cmpPref, obj);
                    jobj.put("companyPreferences", obj);

                    //company = company == null ? (Company) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyid) : company;
                    company = (Company) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyid);
                    jobj.put("notifyon", company.getNotificationtype() >0 ? true : false);

                    userObject = (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
                    jobj.put("helpFlag", userObject.getHelpflag());

                    requestParams2 = new HashMap<String, Object>();
                    requestParams2.put("userid", userid);
                    kmsg = permissionHandlerDAOObj.getUserPermission(requestParams2);
                    Iterator ite = kmsg.getEntityList().iterator();
                    JSONArray jarr = new JSONArray();
                    while (ite.hasNext()) {
                        JSONObject jo = new JSONObject();
                        Object[] roww = (Object[]) ite.next();
                        jo.put(roww[0].toString(), roww[1]);
                        jarr.put(jo);
                        if(roww[0].toString().equals(Constants.Crm_Case_modulename)) {// Case
                            jo = new JSONObject();
                            jo.put(roww[0].toString()+"s", roww[1]);// Cases
                            jarr.put(jo);
                        }
                    }
                    jobj.put("perms", jarr);
                    
                    // store company info in session,  store user hierarchy in session
                    StringBuffer usersList = getManagerDAO().recursiveUsers(userid);

                    sessionHandlerImplObj.createUserSession(request, jobj, userObject, company, userLogin, usersList);
                    sessionHandlerImplObj.setAttribute(Constants.SESSION_SYS_EMAILID, StringUtil.getSysEmailIdByCompanyID(company));
                    setLocale(request, response, jobj.optString("language",null));
                    requestParams.put("userloginid", StringUtil.checkForNull(userid));
                    profileHandlerDAOObj.saveUserLogin(requestParams);
                    isvalid = true;
                } else {
                    jobj = new JSONObject();
                    jobj.put("success", false);
                    jobj.put("reason", "noaccess");
                    jobj.put("message", "Authentication failed");
                    isvalid = false;
                }

            } else {
                String username = request.getRemoteUser();
                if (!StringUtil.isNullOrEmpty(username)) {
//                jbj = DBCon.AuthUser(username, subdomain);
                    boolean toContinue = true;
                    if(sessionHandlerImplObj.validateSession(request, response)){
                            String companyid_session =  sessionHandlerImplObj.getCompanyid(request);
                            String subdomainFromSession = companyDetailsDAOObj.getSubDomain(companyid_session);
                            if( !subdomain.equalsIgnoreCase(subdomainFromSession)){
                            result = "alreadyloggedin";
                            toContinue = false;
                            }

                    }
                    if(toContinue){
                    requestParams = new HashMap<String, Object>();
                    requestParams.put("user", username);
                    requestParams.put("subdomain", subdomain);
                    kmsg = authHandlerDAOObj.verifyLogin(requestParams);
                    
                    jobj = authHandler.getVerifyLoginJson(kmsg.getEntityList(), request);
                    if (jobj.has("success") && (jobj.get("success").equals(true))) {
                        User userObject = null;
                        UserLogin userLogin = null;
                        Company company = null;

                        if (kmsg.getEntityList() != null && !kmsg.getEntityList().isEmpty()) {
                            Object[] row = (Object[]) kmsg.getEntityList().get(0);
                            userObject = (User) row[0];
                            userLogin = (UserLogin) row[1];
                            company = (Company) row[2];
                        }
//                    sessionbean.createUserSession(request, jbj);
                        obj = new JSONObject();
                        userid = jobj.getString("lid");

                        kmsg = permissionHandlerDAOObj.getRoleofUser(userid);
                        Iterator ite3 = kmsg.getEntityList().iterator();
                        while(ite3.hasNext()) {
                            Object[] row = (Object[]) ite3.next();
                            String roleid = row[0].toString();
                            jobj.put("roleid", roleid);
                        }
                        
                        CompanyPreferences cmpPref = (CompanyPreferences) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CompanyPreferences", jobj.getString("companyid"));
                        obj = crmManagerCommon.getCompanyPreferencesJSON(cmpPref, obj);
                        jobj.put("companyPreferences", obj);
                        
                      //company = company == null ? (Company) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyid) : company;
                        company = (Company) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", jobj.getString("companyid"));
                        jobj.put("notifyon", company.getNotificationtype() >0 ? true : false);

                        //userObject = userObject == null ? (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid): userObject;
                        userObject = (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
                        jobj.put("helpFlag", userObject.getHelpflag());
                        requestParams2 = new HashMap<String, Object>();
                        requestParams2.put("userid", userid);
                        kmsg = permissionHandlerDAOObj.getUserPermission(requestParams2);

                        Iterator ite = kmsg.getEntityList().iterator();
                        JSONArray jarr = new JSONArray();
                        while (ite.hasNext()) {
                            JSONObject jo = new JSONObject();
                            Object[] roww = (Object[]) ite.next();
                            jo.put(roww[0].toString(), roww[1]);
                            jarr.put(jo);
                            if(roww[0].toString().equals(Constants.Crm_Case_modulename)) {// Case
                                jo = new JSONObject();
                                jo.put(roww[0].toString()+"s", roww[1]);// Cases
                                jarr.put(jo);
                            }
                        }
                        jobj.put("perms", jarr);
                        
                     // store company info in session,  store user hierarchy in session
                        StringBuffer usersList = getManagerDAO().recursiveUsers(userid);

                        sessionHandlerImplObj.createUserSession(request, jobj, userObject, company, userLogin, usersList);
                        sessionHandlerImplObj.setAttribute(Constants.SESSION_SYS_EMAILID, StringUtil.getSysEmailIdByCompanyID(company));
                        setLocale(request, response, jobj.optString("language",null));
                        isValidUser = true;
                    } else {
                        result = "noaccess";
                    }
                }
                } else {
                    if (sessionHandlerImpl.isValidSession(request, response)) {
                        isValidUser = true;
                    } else {
                        result = "timeout";
                    }
                }

                if (isValidUser) {
                    JSONObject temp = new JSONObject();
                    companyid = sessionHandlerImpl.getCompanyid(request);
                    String companyName = sessionHandlerImplObj.getCompanyName(request);
                    temp.put("companyid", companyid);
                    String platformUrl = ConfigReader.getinstance().get("platformURL"); //getServletContext().getInitParameter("platformUrl");
                    sessionHandlerImplObj.setAttribute(Constants.SESSION_PARTNERNAME, companyName);
                    if(platformUrl!=null) {
                        apiCallHandlerService.callApp(platformUrl, temp,companyid, "7", false, getReceiver());
                        apiCallHandlerService.callApp(platformUrl, temp,companyid, "13", false, new Receiver() {
                            String companyName = Constants.DESKERA;
                            public Receiver setCompany(String companyName) {
                                this.companyName = companyName;
                                return this;
                            }
                            @Override
                            public void receive(Object obj) {
                                JSONObject jobj=null;
                                if(obj instanceof JSONObject)
                                    jobj = (JSONObject)obj;
                                try {
                                    sessionHandlerImplObj.setAttribute(Constants.SESSION_PARTNERNAME, jobj.optString(Constants.SESSION_PARTNERNAME, this.companyName));
                                    if(jobj.optString(Constants.SESSION_PARTNERNAME, this.companyName).equals(Constants.DESKERA)) {
                                        sessionHandlerImplObj.setAttribute(Constants.SESSION_PARTNERNAME, this.companyName);
                                    }
                                } catch (Exception ex) {
                                    logger.warn("Cannot store partnername: "+ex.toString());
                                }
                            }
                        }.setCompany(companyName));
                    }
                    userid = sessionHandlerImpl.getUserid(request);
                    User userObj = (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
                    String userName = sessionHandlerImplObj.getUserName(request);
                    jobj.put("fullname", profileHandlerDAOObj.getUserFullName(userid));
                    jobj.put("lid", userid);
                    jobj.put("companyid", companyid);
                    jobj.put("company", companyName);
                    jobj.put("username", userName);
                    jobj.put("email", userObj.getEmailID());
                    jobj.put("timeformat", userObj.getTimeformat());
                    jobj.put("callwith", sessionHandlerImplObj.getUserCallWith(request));
                    CompanyPreferences cmpPref = (CompanyPreferences) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CompanyPreferences", companyid);
                    obj = new JSONObject();
                    obj = crmManagerCommon.getCompanyPreferencesJSON(cmpPref, obj);
                    jobj.put("companyPreferences", obj);
                    jobj.put("helpFlag", userObj.getHelpflag());
                    if (!permissionHandlerDAOObj.isSuperAdmin(userid, companyid)) {
                        JSONObject permJobj = new JSONObject();
                        kmsg = permissionHandlerDAOObj.getActivityFeature();
                        permJobj = permissionHandler.getAllPermissionJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());

                        requestParams2 = new HashMap<String, Object>();
                        requestParams2.put("userid", userid);
                        kmsg = permissionHandlerDAOObj.getUserPermission(requestParams2);
                        permJobj = permissionHandler.getRolePermissionJson(kmsg.getEntityList(), permJobj);

                        jobj.put("perm", permJobj);
                    } else {
                        jobj.put("deskeraadmin", true);
                    }
                    requestParams2 = new HashMap<String, Object>();
                    requestParams2.put("timezoneid", sessionHandlerImplObj.getTimeZoneID(request));
                    requestParams2.put("dateformatid", sessionHandlerImpl.getDateFormatID(request));
                    requestParams2.put("currencyid", sessionHandlerImpl.getCurrencyID(request));

                    JSONObject prefJson = new JSONObject();
                    kmsg = authHandlerDAOObj.getPreferences(requestParams2);
                    prefJson = getPreferencesJson(kmsg.getEntityList(), request);
                    jobj.put("preferences", prefJson.getJSONArray("data").get(0));

                    JSONObject roleJson = new JSONObject();
                    kmsg = permissionHandlerDAOObj.getRoleList();
                    Iterator ite = kmsg.getEntityList().iterator();
                    int inc = 0;
                    while (ite.hasNext()) {
                        Object row = (Object) ite.next();
                        String rname = ((Rolelist) row).getRolename();
                        rjobj.put(rname, (int) Math.pow(2, inc));
                        inc++;
                    }
                    kmsg = permissionHandlerDAOObj.getRoleofUser(userid);
                    ite = kmsg.getEntityList().iterator();
                    if(ite.hasNext()) {
                        Object[] row = (Object[]) ite.next();
                        ujobj.put("roleid", row[0].toString());
                    }
                    roleJson.put("Role", rjobj);
                    roleJson.put("URole", ujobj);
                    jobj.put("role", roleJson);
                    jobj.put("subdomain", subdomain);
                    jobj.put("base_url", URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull));

                    createAccountingSession(request);
                    jobj.put("subscribedAccounting", crmManagerCommon.hasSyncAccountingPerm(request));
                    createProjectSession(request);
                    jobj.put("createProject", crmManagerCommon.hasCreateProjPerm(request));
                    jobj.put("viewProject", crmManagerCommon.hasViewProjPerm(request));

                    Iterator ite5 = spreadSheetDAOObj.getSpreadsheetConfig(userid).iterator();
                    JSONObject obj5 = new JSONObject();
                    while (ite5.hasNext()) {
                        SpreadSheetConfig cm = (SpreadSheetConfig) ite5.next();
                         if (cm.getState() != null && !StringUtil.isNullOrEmpty(cm.getState()) ){
                            JSONObject state = new JSONObject(cm.getState());
                            if(state.has("sort")){
                                obj5.put(cm.getModule(), state.get("sort"));
                            }
                        }
                    }
                    jobj.put("modulestates", obj5);
                    jobj.put("tzdiff", sessionHandlerImplObj.getTimeZoneDifference(request));
                    
                    cometManagementService.initBayeuxVariable(getServletContext());

                    isvalid = true;
                } else {
                    jobj.put("success", false);
                    jobj.put("reason", result);
                    jobj.put("message", "Authentication failed");
                    isvalid = false;
                }
            }
        } catch (Exception e) {
            LOG.info(e.getMessage(), e);
            e.printStackTrace();
        } finally {
            try {
                jret.put("valid", isvalid);
                jret.put("data", jobj);
            } catch (JSONException ex) {
                Logger.getLogger(authHandlerController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jret;
    }

    public ModelAndView verifyLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jret = new JSONObject();
        try {
            String user = request.getParameter("u");
            String pass = request.getParameter("p");
            String login = request.getParameter("blank");
            String subdomain = URLUtil.getDomainName(request);
            jret=verifyUserLogin(request,response,user,pass,login,subdomain);
        } catch (Exception e) {
            LOG.info(e.getMessage(), e);
            e.printStackTrace();
        }
        return new ModelAndView("jsonView-ex", "model", jret.toString());
    }
    
    public ModelAndView verifyLoginForIphone(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jret = new JSONObject();
        try {
            String user = request.getParameter("u");
            String pass = request.getParameter("p");
            String login = request.getParameter("blank");
            String subdomain = request.getParameter("d");
            jret=verifyUserLogin(request,response,user,pass,login,subdomain);
        } catch (Exception e) {
            LOG.info(e.getMessage(), e);
            e.printStackTrace();
        }
        return new ModelAndView("jsonView-ex", "model", jret.toString());
    }

    public JSONObject getPreferencesJson(List ll, HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        JSONObject retJobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        String dateformat = "";
        try {
            String timeformat = sessionHandlerImpl.getUserTimeFormat(request);

            KWLTimeZone timeZone = (KWLTimeZone) ll.get(0);
            KWLDateFormat dateFormat = (KWLDateFormat) ll.get(1);
            KWLCurrency currency = (KWLCurrency) ll.get(2);

            jobj.put("Timezone", timeZone.getName());
            jobj.put("Timezoneid", timeZone.getTimeZoneID());
            jobj.put("Timezonediff", timeZone.getDifference());
            jobj.put("tzoffset", TimeZone.getTimeZone("GMT"+timeZone.getDifference()).getOffset(System.currentTimeMillis()));
            if (timeformat.equals("1")) {
                dateformat = dateFormat.getScriptForm().replace('H', 'h');
                if (!dateformat.equals(dateFormat.getScriptForm())) {
                    dateformat += " T";
                }
            } else {
                dateformat = dateFormat.getScriptForm();
            }
            jobj.put("DateFormat", dateformat);
            jobj.put("DateFormatid", dateFormat.getFormatID());
            jobj.put("seperatorpos", dateFormat.getScriptSeperatorPosition());
            jobj.put("Currency", currency.getHtmlcode());
            jobj.put("CurrencyName", currency.getName());
            jobj.put("CurrencySymbol", currency.getSymbol());
            jobj.put("Currencyid", currency.getCurrencyID());
            jarr.put(jobj);

            retJobj.put("data", jarr);
            retJobj.put("success", true);
        } catch (Exception e) {
            LOG.info(e.getMessage(), e);
            e.printStackTrace();
        }
        return retJobj;
    }

    public ModelAndView getPreferences(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("timezoneid", sessionHandlerImplObj.getTimeZoneID(request));
            requestParams.put("dateformatid", sessionHandlerImpl.getDateFormatID(request));
            requestParams.put("currencyid", sessionHandlerImpl.getCurrencyID(request));

            kmsg = authHandlerDAOObj.getPreferences(requestParams);
            jobj = getPreferencesJson(kmsg.getEntityList(), request);
        } catch (Exception e) {
            LOG.info(e.getMessage(), e);
            e.printStackTrace();
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    private Receiver getReceiver() {
        return new Receiver() {

            @Override
            public void receive(Object obj) {
                JSONObject jobj=null;
                if(obj instanceof JSONObject)
                    jobj = (JSONObject)obj;
                try {
                    sessionHandlerImplObj.setAttribute("isfreeuser", jobj.optBoolean("isFree"));
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("isFree",jobj.optBoolean("isFree"));
                    cometManagementService.publishInformation(map,new String[]{"crm","upgradelink",sessionHandlerImplObj.getCompanyid()});
                } catch (Exception ex) {
                    logger.warn("Cannot store isFree: "+ex.toString());
                }
            }
        };
    }
	/**
	 * @return the cometManagementService
	 */
	public CometManagementService getCometManagementService() {
		return cometManagementService;
	}
	/**
	 * @param cometManagementService the cometManagementService to set
	 */
	public void setCometManagementService(
			CometManagementService cometManagementService) {
		this.cometManagementService = cometManagementService;
	}
    /**
     * @return the managerDAO
     */
    public crmManagerDAO getManagerDAO()
    {
        return managerDAO;
    }
    /**
     * @param managerDAO the managerDAO to set
     */
    public void setManagerDAO(crmManagerDAO managerDAO)
    {
        this.managerDAO = managerDAO;
    }
    
    private void createAccountingSession(HttpServletRequest request) throws SessionExpiredException, ServiceException {
        HttpSession session = request.getSession(true);

        try {
            boolean syncAccounting = false;
            if (session.getAttribute("syncAccounting") == null) {
                JSONObject userData = new JSONObject();
                String companyid=sessionHandlerImpl.getCompanyid(request);
                String userid=sessionHandlerImpl.getUserid(request);
                userData.put("companyid",companyid);
                userData.put("userid",userid);
                userData.put("remoteapikey",storageHandlerImpl.GetRemoteAPIKey());
                String accURL = ConfigReader.getinstance().get("accURL");
                if(accURL!=null) {
                    JSONObject resObj = apiCallHandlerService.callApp(accURL, userData, companyid, "0", true);
                    if(resObj.has("success") && resObj.getBoolean("success")){
                        String companyexist = resObj.getString("infocode");
                        if(StringUtil.equal(companyexist, "m01")){
                            syncAccounting = true;
                        }
                   }
                }
               session.setAttribute("syncAccounting",syncAccounting );
            }
        } catch (JSONException e) {
            throw ServiceException.FAILURE("crmManagerCommon.createAccountingSession", e);
        }
    }
    
    /**
     * @param request
     * @throws SessionExpiredException
     */
    private void createProjectSession(HttpServletRequest request) throws SessionExpiredException {
        HttpSession session = request.getSession(true);

        try {
            boolean createPerm = false, viewPerm = false;
            if (session.getAttribute("viewProject") == null) {
                JSONObject userData = new JSONObject();
                String companyid=sessionHandlerImpl.getCompanyid(request);
                String userid=sessionHandlerImpl.getUserid(request);
                userData.put("companyid",companyid);
                userData.put("userid",userid);
                userData.put("remoteapikey",storageHandlerImpl.GetRemoteAPIKey());
                String projectManagementUrl = ConfigReader.getinstance().get("projectManagementURL");
                if(projectManagementUrl!=null) {
                    JSONObject resObj = apiCallHandlerService.callApp(projectManagementUrl, userData, companyid, "11", true);
                    if(resObj.has("success") && resObj.getBoolean("success")){
                        JSONObject perm = resObj.getJSONObject("permissions");
                        createPerm = perm.getBoolean("create");
                        viewPerm = perm.getBoolean("view");
                    }
                }
                session.setAttribute("viewProject",viewPerm );
                session.setAttribute("createProject",createPerm );
            }
        } catch (JSONException e) {
        }
    }
    
    
	public JSONObject verifyCustomerLogin(HttpServletRequest request, HttpServletResponse response, String user, String pass, String login, String subdomain) throws ServletException {
		JSONObject jobj = new JSONObject();
		KwlReturnObject kmsg = null;
		String result = "";
		HashMap<String, Object> requestParams2 = null;
		JSONObject obj = null, jret = new JSONObject();
		boolean isvalid = false;
		try {
			boolean isValidUser = false;
			String email = null;
			String companyId = null;
			String contactId = null;
			String customerId = null;
			String contactName=null;
			String userid=null;
			String mainusername=null;
			boolean isActiveCustomer=false;
			if (StringUtil.isNullOrEmpty(login)) {
				Object[] row = authHandlerDAOObj.verifyCaseLogin(user,pass, subdomain);
				if (row!=null) {
					isActiveCustomer=(Boolean)row[6];
					if(isActiveCustomer){
						email = (String) row[0];
						companyId = (String) row[1];
						contactId = (String) row[2];
						customerId = (String) row[3];
						contactName = (String) (row[4]!=null?row[4]:"") + " " + (String) (row[5]!=null?row[5]:"");// combining first name and last name for full name
						sessionHandlerImplObj.createCustomerSession(request,email, companyId, contactId,customerId,contactName.trim());
                        createSessionForCustomPartnerURLAndSysEmailId(companyId);
						jobj = new JSONObject();
						jobj.put("email", email);
						jobj.put("success", true);
						isvalid = true;
					}
					else{
						jobj = new JSONObject();
						jobj.put("success", false);
						jobj.put("reason", "noaccess");
						jobj.put("message", "Login has been deactivated");
						isvalid = false;
					}
				} else {
					jobj = new JSONObject();
					jobj.put("success", false);
					jobj.put("reason", "noaccess");
					jobj.put("message", "Authentication failed");
					isvalid = false;
				}

			} else {
				String username = request.getRemoteUser();
				if (!StringUtil.isNullOrEmpty(username)) {
					// jbj = DBCon.AuthUser(username, subdomain);
					boolean toContinue = true;
					if (sessionHandlerImplObj.validateSession(request, response)) {
						result = "alreadyloggedin";
						toContinue = false;

					}
					if (toContinue) {
						Object[] row = authHandlerDAOObj.verifyCaseLogin(username,subdomain);
						if (row!=null) {
							isActiveCustomer=(Boolean)row[6];
							if(isActiveCustomer){
								email = (String) row[0];
								companyId = (String) row[1];
								contactId = (String) row[2];
								customerId = (String) row[3];
								contactName = (String) row[4] + " " + (String) row[5];// combining first name and last name for full name
								sessionHandlerImplObj.createCustomerSession(request,email, companyId, contactId,customerId,contactName);
                                createSessionForCustomPartnerURLAndSysEmailId(companyId);
								isvalid = true;
							}else{
								result="Login has been deactivated";
							}
						} else {
							result = "noaccess";
						}

					}
				} else {
					if (sessionHandlerImpl.isValidSession(request, response)) {
						isValidUser = true;
					} else {
						result = "timeout";
					}
				}
				if (isValidUser) {
					isvalid = true;

				} else {
					jobj.put("success", false);
					jobj.put("reason", result);
					isvalid = false;
				}
			}
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			e.printStackTrace();
		} finally {
			try {
				jret.put("valid", isvalid);
				jret.put("data", jobj);
			} catch (JSONException ex) {
				Logger.getLogger(authHandlerController.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return jret;
	}

    private void createSessionForCustomPartnerURLAndSysEmailId(String companyId) {
        try{
            Company company = (Company) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyId);
            sessionHandlerImplObj.setAttribute(Constants.SESSION_SYS_EMAILID, StringUtil.getSysEmailIdByCompanyID(company));
            String companyName = company.getCompanyName();
            // set platform url
            sessionHandlerImplObj.setAttribute(Constants.SESSION_PARTNERNAME, companyName);
            String platformUrl = ConfigReader.getinstance().get("platformURL");
            if (platformUrl != null) {
                JSONObject temp = new JSONObject();
                temp.put("companyid", companyId);
                apiCallHandlerService.callApp(platformUrl, temp, companyId, "13", false, new Receiver() {
                    String companyName = Constants.DESKERA;
                    public Receiver setCompany(String companyName) {
                        this.companyName = companyName;
                        return this;
                    }
                    @Override
                    public void receive(Object obj) {
                        JSONObject jobj = null;
                        if (obj instanceof JSONObject) {
                            jobj = (JSONObject) obj;
                        }
                        try {
                            sessionHandlerImplObj.setAttribute(Constants.SESSION_PARTNERNAME, jobj.optString(Constants.SESSION_PARTNERNAME, this.companyName));
                            if(jobj.optString(Constants.SESSION_PARTNERNAME, this.companyName).equals(Constants.DESKERA)) {
                                sessionHandlerImplObj.setAttribute(Constants.SESSION_PARTNERNAME, this.companyName);
                            }
                        } catch (Exception ex) {
                            logger.warn("Cannot store partnername: " + ex.toString());
                        }
                    }
                }.setCompany(companyName));
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
	public ModelAndView verifyCaseLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject jret = new JSONObject();
		try {
			String user = request.getParameter("u");
			String pass = request.getParameter("p");
			String login = request.getParameter("blank");
			String subdomain = URLUtil.getDomainName(request);
			jret = verifyCustomerLogin(request, response, user, pass, login, subdomain);
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			e.printStackTrace();
		}
		return new ModelAndView("jsonView-ex", "model", jret.toString());
	}
	
	protected void setLocale(HttpServletRequest request, HttpServletResponse response, String newLocale) {
		if (newLocale != null) {
			LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
			if (localeResolver == null) {
				LOG.debug("No LocaleResolver found: not in a DispatcherServlet request?");
				return;
			}
			LocaleEditor localeEditor = new LocaleEditor();
			localeEditor.setAsText(newLocale);
			localeResolver.setLocale(request, response, (Locale) localeEditor.getValue());
		}
	}
	public ModelAndView getPartnerLinks(HttpServletRequest request,HttpServletResponse response){
        JSONObject jResult = new JSONObject();
        try {                       
            String platformURL = ConfigReader.getinstance().get("platformURL");
            String companyid = request.getParameter("companyid");
            JSONObject jobj = new JSONObject();
            jobj.put("companyid",companyid);
            JSONObject appdata = apiCallHandlerService.callApp(platformURL, jobj, companyid,"14",false);           
            jResult.put("valid", true);
            jResult.put("success", true);
            jResult.put("data", appdata);
        } catch (JSONException ex) {
            LOG.warn(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView-ex", "model", jResult.toString());
    }
}
