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
package com.krawler.spring.sessionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

/**
 *
 * @author karthik
 */
public class sessionHandlerImpl {

    private sessionHandlerImpl sessionHandlerImplObj;

    public sessionHandlerImpl(){
    }
    
    public Object getAttribute(String property) {
        return getAttributeHolder().getAttribute(property, RequestAttributes.SCOPE_SESSION);
    }

    public void setAttribute(String property, Object obj) {
        getAttributeHolder().setAttribute(property, obj, RequestAttributes.SCOPE_SESSION);
    }
    
    public String[] getAttributeNames() {
        return getAttributeHolder().getAttributeNames(RequestAttributes.SCOPE_SESSION);
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public static boolean isValidSession(HttpServletRequest request,
            HttpServletResponse response) {
        boolean bSuccess = false;
        try {
            if (request.getSession().getAttribute(Constants.SESSION_INITIALIZED) != null) {
                bSuccess = true;
            }
        } catch (Exception ex) {
        }
        return bSuccess;
    }

    public static void updatePreferences(HttpServletRequest request,
            String currencyid, String dateformatid, String timezoneid,
            String tzdiff) {
        if (currencyid != null) {
            request.getSession().setAttribute(Constants.SESSION_CURRENCY_ID, currencyid);
        }
        if (timezoneid != null) {
            request.getSession().setAttribute(Constants.SESSION_TIMEZONE_ID, timezoneid);
            request.getSession().setAttribute(Constants.SESSION_TZDIFF, tzdiff);
        }
        if (dateformatid != null) {
            request.getSession().setAttribute(Constants.SESSION_DATEFORMAT_ID, dateformatid);
        }
    }
    public static void updatePreferences(HttpServletRequest request,
            String currencyid, String dateformatid, String timezoneid,
            String tzdiff,String tzid,boolean bool) {
        updatePreferences(request,currencyid, dateformatid, timezoneid,tzdiff);
        if (timezoneid != null) {
            request.getSession().setAttribute(Constants.SESSION_TZ_ID, tzid);
        }
        
    }

    /* Update date preference only. */
    public static void updateDatePreferences(HttpServletRequest request, String dateformatid) {
        if (dateformatid != null) {
            request.getSession().setAttribute(Constants.SESSION_DATEFORMAT_ID, dateformatid);
        }
    }

    /* Update date preference only. */
    public static void updateNotifyOnFlag(HttpServletRequest request, boolean notifyFlag) {
        request.getSession().setAttribute(Constants.SESSION_NOTIFYON, notifyFlag);
    }

    /* Time Format included here. */
    public static void updatePreferences(HttpServletRequest request,
            String currencyid, String dateformatid, String timezoneid,
            String tzdiff, String timeformat) {
        if (currencyid != null) {
            request.getSession().setAttribute(Constants.SESSION_CURRENCY_ID, currencyid);
        }
        if (timezoneid != null) {
            request.getSession().setAttribute(Constants.SESSION_TIMEZONE_ID, timezoneid);
            request.getSession().setAttribute(Constants.SESSION_TZDIFF, tzdiff);
        }
        if (dateformatid != null) {
            request.getSession().setAttribute(Constants.SESSION_DATEFORMAT_ID, dateformatid);
        }
        if (timeformat != null) {
            request.getSession().setAttribute(Constants.SESSION_TIMEFORMAT, timeformat);
        }
    }

    public boolean validateSession(HttpServletRequest request,
            HttpServletResponse response) {
        return sessionHandlerImpl.isValidSession(request, response);
    }

    public void createUserSession(HttpServletRequest request, JSONObject jObj, User user, Company company, UserLogin userLogin, StringBuffer usersList) throws ServiceException {
        HttpSession session = request.getSession(true);
        try {
            RequestAttributes attribs = getAttributeHolder();
            attribs.setAttribute(Constants.SESSION_USERNAME, jObj.getString("username"), RequestAttributes.SCOPE_SESSION);
            attribs.setAttribute(Constants.SESSION_USERID, jObj.getString("lid"), RequestAttributes.SCOPE_SESSION);
            attribs.setAttribute(Constants.SESSION_COMPANY_ID, jObj.getString("companyid"), RequestAttributes.SCOPE_SESSION);
            attribs.setAttribute(Constants.SESSION_COMPANY_NAME, jObj.getString("company"), RequestAttributes.SCOPE_SESSION);
            attribs.setAttribute(Constants.SESSION_TIMEZONE_ID, jObj.getString("timezoneid"), RequestAttributes.SCOPE_SESSION);
            attribs.setAttribute(Constants.SESSION_TZDIFF, jObj.getString("tzdiff"), RequestAttributes.SCOPE_SESSION);

            attribs.setAttribute(Constants.SESSION_DATEFORMAT_ID, jObj.getString("dateformatid"), RequestAttributes.SCOPE_SESSION);
            attribs.setAttribute(Constants.SESSION_CURRENCY_ID, jObj.getString("currencyid"), RequestAttributes.SCOPE_SESSION);

            attribs.setAttribute(Constants.SESSION_CALL_WITH, jObj.getString("callwith"), RequestAttributes.SCOPE_SESSION);
            attribs.setAttribute(Constants.SESSION_TIMEFORMAT, jObj.getString("timeformat"), RequestAttributes.SCOPE_SESSION);
            attribs.setAttribute(Constants.SESSION_COMPANY_PREF, jObj.getString("companyPreferences"), RequestAttributes.SCOPE_SESSION);
            attribs.setAttribute(Constants.SESSION_ROLE_ID, jObj.getString("roleid"), RequestAttributes.SCOPE_SESSION);
            attribs.setAttribute(Constants.SESSION_INITIALIZED, "true", RequestAttributes.SCOPE_SESSION);

            session.setAttribute(Constants.SESSION_USERNAME, jObj.getString("username"));
            session.setAttribute(Constants.SESSION_USERID, jObj.getString("lid"));
            session.setAttribute(Constants.SESSION_COMPANY_ID, jObj.getString("companyid"));
            session.setAttribute(Constants.SESSION_COMPANY_NAME, jObj.getString("company"));
            session.setAttribute(Constants.SESSION_TIMEZONE_ID, jObj.getString("timezoneid"));
            session.setAttribute(Constants.SESSION_TZDIFF, jObj.getString("tzdiff"));
            session.setAttribute(Constants.SESSION_DATEFORMAT_ID, jObj.getString("dateformatid"));
            session.setAttribute(Constants.SESSION_CURRENCY_ID, jObj.getString("currencyid"));
            session.setAttribute(Constants.SESSION_CALL_WITH, jObj.getString("callwith"));
            session.setAttribute(Constants.SESSION_TIMEFORMAT, jObj.getString("timeformat"));
            session.setAttribute(Constants.SESSION_TZ_ID, jObj.getString(Constants.SESSION_TZ_ID));
            session.setAttribute(Constants.SESSION_COMPANY_PREF, jObj.getString("companyPreferences"));
            session.setAttribute(Constants.SESSION_ROLE_ID, jObj.getString("roleid"));
            session.setAttribute(Constants.SESSION_INITIALIZED, "true");
            session.setAttribute(Constants.SESSION_NOTIFYON, jObj.getBoolean("notifyon"));
            
            if (user != null)
            {
                session.setAttribute(Constants.SESSION_USEROBJECT, user);
            }
            if (company != null)
            {
                session.setAttribute(Constants.SESSION_USERCOMPANY, company);
            }
            if (usersList != null)
            {
                session.setAttribute(Constants.SESSION_USERLIST, usersList);
            }
            
            JSONArray jarr = jObj.getJSONArray("perms");
            for (int l = 0; l < jarr.length(); l++) {
                String keyName = jarr.getJSONObject(l).names().get(0).toString();
                session.setAttribute(keyName, jarr.getJSONObject(l).get(keyName));
                attribs.setAttribute(keyName, jarr.getJSONObject(l).get(keyName), RequestAttributes.SCOPE_SESSION);
            }
//            TimeZone.setDefault(TimeZone.getTimeZone(Constants.DefaultTimeZone));
        } catch (JSONException e) {
            throw ServiceException.FAILURE("sessionHandlerImpl.createUserSession", e);
        }
    }
    
    
    public void createCustomerSession(HttpServletRequest request,String email,String companyid, String contactid,String customerId,String contactName) throws ServiceException {
        HttpSession session = request.getSession(true);
        try {
           
        	session.setAttribute(Constants.SESSION_INITIALIZED, "true");
            if (email != null)
            {
                session.setAttribute(Constants.SESSION_CUSTOMER_EMAIL, email);
            }
            if (companyid != null)
            {
                session.setAttribute(Constants.SESSION_COMPANY_ID, companyid);
            }
            if (contactid != null)
            {
                session.setAttribute(Constants.SESSION_CONTACT_ID, contactid);
            }
            if (customerId != null)
            {
                session.setAttribute(Constants.SESSION_CUSTOMER_ID, customerId);
            }
            if (contactName != null)
            {
                session.setAttribute(Constants.SESSION_CONTACT_NAME, contactName);
            }
                       
//            TimeZone.setDefault(TimeZone.getTimeZone(Constants.DefaultTimeZone));
        } catch (Exception e) {
            throw ServiceException.FAILURE("sessionHandlerImpl.createCustomerSession", e);
        }
    }
    

    public RequestAttributes getAttributeHolder() {

        return RequestContextHolder.getRequestAttributes();
    }

    public void destroyUserSession(HttpServletRequest request,
            HttpServletResponse response) {
        request.getSession().invalidate();
    }

    public String getUserid()
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_USERID, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public String getTimeZoneID()
            throws SessionExpiredException {
        String timezoneid = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_TIMEZONE_ID, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERID_NULL);
        return timezoneid;
    }

    public static StringBuffer getRecursiveUsersList(HttpServletRequest request) throws SessionExpiredException
    {
        HttpSession session = request.getSession();
        if (session.getAttribute(Constants.SESSION_USERLIST) != null)
        {
            return (StringBuffer) session.getAttribute(Constants.SESSION_USERLIST);
        }
        return null;
    }
    
    public static User getUser(HttpServletRequest request) throws SessionExpiredException
    {
        HttpSession session = request.getSession();
        if (session.getAttribute(Constants.SESSION_USEROBJECT) != null)
        {
            return (User) session.getAttribute(Constants.SESSION_USEROBJECT);
        }
        return null;
    }
    
    public static Company getCompany(HttpServletRequest request) throws SessionExpiredException
    {
        HttpSession session = request.getSession();
        if (session.getAttribute(Constants.SESSION_USERCOMPANY) != null)
        {
            return (Company) session.getAttribute(Constants.SESSION_USERCOMPANY);
        }
        return null;
    }
    
    public String getTimeZoneDifference() throws SessionExpiredException
    {
        String tzdiff = NullCheckAndThrow(getAttributeHolder().getAttribute(Constants.SESSION_TZDIFF, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERID_NULL);
        return tzdiff;
    }

    public String getUserCallWith()
            throws SessionExpiredException {
        String callwith = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_CALL_WITH, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERID_NULL);
        return callwith;
    }

    public String getUserTimeFormat()
            throws SessionExpiredException {
        String timeformat = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_TIMEFORMAT, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERID_NULL);
        return timeformat;
    }

    public String getUserName()
            throws SessionExpiredException {
        String userName = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_USERNAME, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERNAME_NULL);
        return userName;
    }

    public String getRole()
            throws SessionExpiredException {
        String roleid = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_ROLE_ID, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERID_NULL);
        return roleid;
    }

    public String getDateFormatID()
            throws SessionExpiredException {
        String dateformatID = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_DATEFORMAT_ID, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERID_NULL);
        return dateformatID;
    }

    public String getCompanyid()
            throws SessionExpiredException {
        String companyID = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_COMPANY_ID, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERID_NULL);
        return companyID;
    }

    public String getCompanyName()
            throws SessionExpiredException {
        String userName = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_COMPANY_NAME, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERNAME_NULL);
        return userName;
    }

    public String getSystemEmailId() throws SessionExpiredException {
        String sysemailid = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_SYS_EMAILID, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERNAME_NULL);
        return sysemailid;
    }

    public String getPartnerName() throws SessionExpiredException {
        String partnaer = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_PARTNERNAME, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERNAME_NULL);
        return partnaer;
    }

    public static String getSystemEmailId(HttpServletRequest request) throws SessionExpiredException {
        String sysemailid = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_SYS_EMAILID), SessionExpiredException.USERNAME_NULL);
        return sysemailid;
    }

    public static String getPartnerName(HttpServletRequest request) throws SessionExpiredException {
        String partner = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_PARTNERNAME), SessionExpiredException.USERNAME_NULL);
        return partner;
    }

    public String getCurrencyID()
            throws SessionExpiredException {
        String currencyID = NullCheckAndThrow(getAttributeHolder().getAttribute(
                Constants.SESSION_CURRENCY_ID, RequestAttributes.SCOPE_SESSION), SessionExpiredException.USERID_NULL);
        return currencyID;
    }

    public Integer getPerms(String keyName)
            throws SessionExpiredException {
        long perl = 0;
        int per = 0;
        try {
            if (getAttributeHolder().getAttribute(keyName, RequestAttributes.SCOPE_SESSION) != null) {
                perl = (Long) getAttributeHolder().getAttribute(keyName, RequestAttributes.SCOPE_SESSION);
            }
            per = (int) perl;
        } catch (Exception e) {
            per = 0;
        }
        return per;
    }

    public static String getCompanyPreference(HttpServletRequest request)
            throws SessionExpiredException {
        String companypref = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_COMPANY_PREF), SessionExpiredException.USERID_NULL);
        return companypref;
    }

    public static String getUserid(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_USERID), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public String getTimeZoneID(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_TIMEZONE_ID), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public static String getTimeZoneDifference(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_TZDIFF), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public String getUserCallWith(HttpServletRequest request)
            throws SessionExpiredException {
        String callwith = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_CALL_WITH), SessionExpiredException.USERID_NULL);
        return callwith;
    }

    public static String getUserTimeFormat(HttpServletRequest request)
            throws SessionExpiredException {
        String timeformat = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_TIMEFORMAT), SessionExpiredException.USERID_NULL);
        return timeformat;
    }

    public String getUserName(HttpServletRequest request)
            throws SessionExpiredException {
        String userName = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_USERNAME), SessionExpiredException.USERNAME_NULL);
        return userName;
    }

    public static String getRole(HttpServletRequest request)
            throws SessionExpiredException {
        String roleid = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_ROLE_ID), SessionExpiredException.USERID_NULL);
        return roleid;
    }

    public static String getDateFormatID(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_DATEFORMAT_ID), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public static String getCompanyid(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_COMPANY_ID), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public static String getCompanyName(HttpServletRequest request)
            throws SessionExpiredException {
        String userName = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_COMPANY_NAME), SessionExpiredException.USERNAME_NULL);
        return userName;
    }
    public static String getTzId(HttpServletRequest request)
            throws SessionExpiredException {
        String userName = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_TZ_ID), SessionExpiredException.USERNAME_NULL);
        return userName;
    }
    public static String getCurrencyID(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_CURRENCY_ID), SessionExpiredException.USERID_NULL);
        return userId;
    }

    public static boolean getCompanyNotifyOnFlag(HttpServletRequest request)
            throws SessionExpiredException {
        String userId = NullCheckAndThrow(request.getSession().getAttribute(
                Constants.SESSION_NOTIFYON), SessionExpiredException.NOTIFYFLAG_NULL);
        return Boolean.parseBoolean(userId);
    }
    public static Integer getPerms(HttpServletRequest request, String keyName)
            throws SessionExpiredException {
        long perl = 0;
        int per = 0;
        try {
            if (request.getSession().getAttribute(keyName) != null) {
                perl = (Long) request.getSession().getAttribute(keyName);
            }
            per = (int) perl;
        } catch (Exception e) {
            per = 0;
        }
        return per;
    }

    public static String NullCheckAndThrow(Object objToCheck, String errorCode)
            throws SessionExpiredException {
        if (objToCheck != null) {
            String oStr = objToCheck.toString();
            if (!StringUtil.isNullOrEmpty(oStr)) {
                return oStr;
            }
        }
        throw new SessionExpiredException("Session Invalidated", errorCode);
    }
}
