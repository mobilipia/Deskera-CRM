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

import com.krawler.common.service.ServiceException;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.apache.commons.lang.RandomStringUtils;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.Language;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Karthik
 */
public class authHandler {

    public static JSONObject getVerifyLoginJson(List ll, HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            if (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                User user = (User) row[0];
                UserLogin userLogin = (UserLogin) row[1];
                Company company = (Company) row[2];
                jobj.put("success", true);
                jobj.put("lid", userLogin.getUserID());
                jobj.put("username", userLogin.getUserName());
                jobj.put("companyid", company.getCompanyID());
                jobj.put("company", company.getCompanyName());
                Language lang=company.getLanguage();
                if(lang!=null)
                	jobj.put("language", lang.getLanguageCode()+(lang.getCountryCode()!=null?"_"+lang.getCountryCode():""));
                jobj.put("roleid", user.getRoleID());
                jobj.put("callwith", user.getCallwith());
                jobj.put("timeformat", user.getTimeformat());
//                KWLTimeZone timeZone = user.getTimeZone();
//                if (timeZone == null) {
//                    timeZone = company.getTimeZone();
//                }
//                if (timeZone == null) {
//                    timeZone = (KWLTimeZone) ll.get(1);
//                }
                KWLTimeZone timeZone = getTZforUser(user, company, (KWLTimeZone) ll.get(1));
                jobj.put("timezoneid", timeZone.getTimeZoneID());
                jobj.put("tzdiff", timeZone.getDifference());
                jobj.put(Constants.SESSION_TZ_ID, timeZone.getTzID());
                KWLDateFormat dateFormat = user.getDateFormat();
                if (dateFormat == null) {
                    dateFormat = (KWLDateFormat) ll.get(2);
                }
                jobj.put("dateformatid", dateFormat.getFormatID());
                KWLCurrency currency = company.getCurrency();
                if (currency == null) {
                    currency = (KWLCurrency) ll.get(3);
                }
                jobj.put("currencyid", currency.getCurrencyID());
                jobj.put("success", true);
            } else {
                jobj.put("failure", true);
                jobj.put("success", false);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public static DateFormat getDateFormatter(String userTimeFormatId, String timeZoneDiff) throws ServiceException {
        SimpleDateFormat sdf = null;
        try {
            String dateformat = "";
            if (userTimeFormatId.equals("1")) {
                dateformat = "MMMM d, yyyy hh:mm:ss aa";
            } else {
                dateformat = "MMMM d, yyyy HH:mm:ss";
            }
            sdf = new SimpleDateFormat(dateformat);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getDateFormatter", e);
        }
        return sdf;
    }

    public static DateFormat getDateFormatter(String userTimeFormatId)
        throws SessionExpiredException {
        String dateformat="";
        if(userTimeFormatId.equals("1")) {
            dateformat="MMMM d, yyyy hh:mm:ss aa";
        } else
            dateformat="MMMM d, yyyy HH:mm:ss";
        SimpleDateFormat sdf=new SimpleDateFormat(dateformat);
        return sdf;
    }

    public static DateFormat getDateFormatter(HttpServletRequest request)
        throws SessionExpiredException {
        String dateformat="";
        String timeformat=authHandler.getUserTimeFormat(request);
        if(timeformat.equals("1")) {
            dateformat="MMMM d, yyyy hh:mm:ss aa";
        } else
            dateformat="MMMM d, yyyy HH:mm:ss";
        SimpleDateFormat sdf=new SimpleDateFormat(dateformat);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"+getTimeZoneDifference(request)));
        return sdf;
    }

    public static String getUserTimeFormat(HttpServletRequest request)
			throws SessionExpiredException {
		String timeformat = NullCheckAndThrow(request.getSession().getAttribute(
				"timeformat"), SessionExpiredException.USERID_NULL);
		return timeformat;
	}

    public static String getTimeZoneDifference(HttpServletRequest request)
			throws SessionExpiredException {
		String userId = NullCheckAndThrow(request.getSession().getAttribute(
				"tzdiff"), SessionExpiredException.USERID_NULL);
		return userId;
    }

    private static String NullCheckAndThrow(Object objToCheck, String errorCode)
			throws SessionExpiredException {
		if (objToCheck != null) {
			String oStr = objToCheck.toString();
			if (!StringUtil.isNullOrEmpty(oStr))
				return oStr;
		}
		throw new SessionExpiredException("Session Invalidated", errorCode);
	}

    public static DateFormat getPrefDateFormatter(String userTimeFormatId, String timeZoneDiff, String pref) throws ServiceException {
        SimpleDateFormat sdf = null;
        try {
            String dateformat = "";
            if (userTimeFormatId.equals("1")) {
                dateformat = pref.replace('H', 'h');
                if (!dateformat.equals(pref)) {
                    dateformat += " a";
                }
            } else {
                dateformat = pref;
            }
            sdf = new SimpleDateFormat(dateformat);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getPrefDateFormatter", e);
        }
        return sdf;
    }

    public static DateFormat getUserPrefDateFormatter(String userTimeFormatId, String pref) throws ServiceException {
        SimpleDateFormat sdf = null;
        try {
            String dateformat = "";
            if (userTimeFormatId.equals("1")) {
                dateformat = pref.replace('H', 'h');
                if (!dateformat.equals(pref)) {
                    dateformat += " a";
                }
            } else {
                dateformat = pref;
            }
            sdf = new SimpleDateFormat(dateformat);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getUserPrefDateFormatter", e);
        }
        return sdf;
    }
    public static DateFormat getTimeFormatter(String userTimeFormatId) throws ServiceException {
        SimpleDateFormat sdf = null;
        try {
            String dateformat = "";
            if (userTimeFormatId.equals("1")) {
                dateformat = " hh:mm:ss aa ";
            } else {
                dateformat = "HH:mm:ss";
            }
            sdf = new SimpleDateFormat(dateformat);
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getTimeFormatter", e);
        }
        return sdf;
    }

   public static DateFormat getDateMDYFormatter(HttpServletRequest request)
			throws SessionExpiredException {
        return new SimpleDateFormat(Constants.MMMMdyyyy);
   }
   
   public static DateFormat getDateMDYFormatter()
   {
       return new SimpleDateFormat(Constants.MMMMdyyyy);
   }
   public static DateFormat getDateFormat(JSONObject jobj) throws JSONException{
            String tZStr = jobj.has(Constants.tzdiff) ? jobj.getString(Constants.tzdiff): null;
            DateFormat df = getDateMDYFormatter();
            if (tZStr != null)
            {
                TimeZone zone = TimeZone.getTimeZone(Constants.GMT + tZStr);
                df.setTimeZone(zone);
            }
            return df;
   }

    public static Date getCreatedonDate(DateFormat df,JSONObject jobj) throws JSONException {
        Date createdOnDate = null;
        if (jobj.has(Constants.createdon) && !StringUtil.isNullOrEmpty(jobj.getString(Constants.createdon))) {
            String createdOn = jobj.getString(Constants.createdon);
            if (createdOn != null) {
                try {
                    createdOnDate = df.parse(createdOn);
                } catch (Exception e) {
                    createdOnDate = authHandler.getCurrentDate();
                }
            }
        } else {
            createdOnDate = authHandler.getCurrentDate();
        }
        return createdOnDate;
    }

   public static Date getCurrentDate()
   {
       return new Date();
   }
   
   public static Date getDateMDYFormatted(String datestr) throws ParseException{
        return new SimpleDateFormat(Constants.MMMMdyyyy).parse(datestr);
   }
   public static DateFormat getDateFormatter(){
        return new SimpleDateFormat("MMMM d, yyyy hh:mm:ss aa");
   }

    public static DateFormat getNewDateFormatter(HttpServletRequest request)
			throws SessionExpiredException {
        return new SimpleDateFormat("yyyy-MM-dd");
   }

    public static DateFormat getDateFormatterWithTimeZone(HttpServletRequest request) throws SessionExpiredException {
    	String tZStr = sessionHandlerImpl.getTimeZoneDifference(request);

        DateFormat df = authHandler.getDateMDYFormatter(request);
    	if (tZStr != null)
    	{
        	TimeZone zone = TimeZone.getTimeZone("GMT" + tZStr);
    		df.setTimeZone(zone);
    	}

    	return df;
	}

    public static DateFormat getDateFormatterWithTimeZoneForExport(HttpServletRequest request) throws SessionExpiredException {
    	String tZStr = sessionHandlerImpl.getTimeZoneDifference(request);

        DateFormat df = authHandler.getNewDateFormatter(request);
    	if (tZStr != null)
    	{
        	TimeZone zone = TimeZone.getTimeZone("GMT" + tZStr);
    		df.setTimeZone(zone);
    	}

    	return df;
	}


    public static String generateNewPassword() throws ServiceException {
        String randomStr = "";
        try {
            randomStr = RandomStringUtils.random(8, true, true);
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.generateNewPassword", e);
        }
        return randomStr;
    }

    public static String getSHA1(String inStr) throws ServiceException {
        String outStr = inStr;
        try {
            byte[] theTextToDigestAsBytes = inStr.getBytes("utf-8");

            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] digest = sha.digest(theTextToDigestAsBytes);

            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                String h = Integer.toHexString(b & 0xff);
                if (h.length() == 1) {
                    sb.append("0" + h);
                } else {
                    sb.append(h);
                }
            }
            outStr = sb.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getSHA1", e);
        }
        return outStr;
    }

    public static KWLTimeZone getTZforUser(User user, Company company, KWLTimeZone tz) {
        KWLTimeZone timeZone = user.getTimeZone();
        if (timeZone == null) {
            timeZone = company.getTimeZone();
        }
        if (timeZone == null) {
            timeZone = tz;

        }
        return timeZone;
    }
    }
