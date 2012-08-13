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
package com.krawler.spring.common;

import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.BuildCriteria;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Karthik
 */
public class kwlCommonTablesDAOImpl extends BaseDAO implements kwlCommonTablesDAO{

    /* (non-Javadoc)
     * @see com.krawler.spring.common.kwlCommonTablesDAO#getClassObject(java.lang.String, java.lang.String)
     */
    public Object getClassObject(String classpath, String id) throws ServiceException {
        Object obj = null;
        try {
            Class cls = Class.forName(classpath);
            obj = get(cls, id);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return obj;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.common.kwlCommonTablesDAO#getRelatedClassObject(java.lang.String, java.lang.String,Object)
     */
    
    public Object getRelatedClassObject(String classpath,String modulePropertyName, String activityPropertyName,Object relatedId) throws ServiceException {
        Object s = null;
        try {
            Class cls = Class.forName(classpath);
            DetachedCriteria crit = DetachedCriteria.forClass(cls,"cls");
            BuildCriteria.buildCriteria(relatedId, BuildCriteria.EQ, crit,activityPropertyName);
            List<Object> obj = null;
            obj = findByCriteria(crit);
            Method m = cls.getMethod("get" + modulePropertyName);
            s = m.invoke(obj.get(0));

        } catch (IllegalAccessException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (SecurityException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return s;
    }
    /* (non-Javadoc)
     * @see com.krawler.spring.common.kwlCommonTablesDAO#getObject(java.lang.String, java.lang.String)
     */
    public Object getObject(String classpath, String id) throws ServiceException {
        Object obj = null;
        try {
            Class cls = Class.forName(classpath);
            obj = get(cls, id);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(kwlCommonTablesDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return obj;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.common.kwlCommonTablesDAO#getAllTimeZones()
     */
    public KwlReturnObject getAllTimeZones() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String query = "from KWLTimeZone order by sortOrder";
            ll = executeQuery(query);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getAllTimeZones", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.common.kwlCommonTablesDAO#getAllCurrencies()
     */
    public KwlReturnObject getAllCurrencies() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String query = "from KWLCurrency";
            ll = executeQuery(query);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getAllCurrencies", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.common.kwlCommonTablesDAO#getAllCountries()
     */
    public KwlReturnObject getAllCountries() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String query = "from Country";
            ll = executeQuery(query);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getAllCountries", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.common.kwlCommonTablesDAO#getAllDateFormats()
     */
    public KwlReturnObject getAllDateFormats() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String query = "from KWLDateFormat";
            ll = executeQuery(query);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getAllDateFormats", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.common.kwlCommonTablesDAO#getUserDateFormatter(java.lang.String, java.lang.String, java.lang.String)
     */
    public DateFormat getUserDateFormatter(String dateFormatId, String userTimeFormatId, String timeZoneDiff) throws ServiceException {
        SimpleDateFormat sdf = null;
        try {
            KWLDateFormat df = (KWLDateFormat) get(KWLDateFormat.class, dateFormatId);
            String dateformat = "";
            if (userTimeFormatId.equals("1")) {
                dateformat = df.getJavaForm().replace('H', 'h');
                if (!dateformat.equals(df.getJavaForm())) {
                    dateformat += " a";
                }
            } else {
                dateformat = df.getJavaForm();
            }
            sdf = new SimpleDateFormat(dateformat);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getUserDateFormatter", e);
        }
        return sdf;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.common.kwlCommonTablesDAO#getOnlyDateFormatter(java.lang.String, java.lang.String)
     */
    public DateFormat getOnlyDateFormatter(String dateFormatId, String userTimeFormatId) throws ServiceException {
        SimpleDateFormat sdf = null;
        try {
            KWLDateFormat df = (KWLDateFormat) get(KWLDateFormat.class, dateFormatId);
            String dateformat = "";
            if (userTimeFormatId.equals("1")) {
                dateformat = df.getJavaForm().replace('H', 'h');
                if (!dateformat.equals(df.getJavaForm())) {
                    dateformat += " a";
                }
            } else {
                dateformat = df.getJavaForm();
            }
            sdf = new SimpleDateFormat(dateformat);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getOnlyDateFormatter", e);
        }
        return sdf;
    }

    /**
     * @param currency
     * @param currencyid
     * @return
     * @throws SessionExpiredException
     */
    public String currencyRender(String currency, String currencyid) throws SessionExpiredException {
        KWLCurrency cur = (KWLCurrency) get(KWLCurrency.class, currencyid);
        String symbol = cur.getHtmlcode();
        char temp = (char) Integer.parseInt(symbol, 16);
        symbol = Character.toString(temp);
        float v = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        if (currency.equals("")) {
            return symbol;
        }
        v = Float.parseFloat(currency);
        String fmt = decimalFormat.format(v);
        fmt = symbol + fmt;
        return fmt;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.common.kwlCommonTablesDAO#getUserDateFormatter1(com.krawler.common.admin.User, int, java.lang.String, java.lang.String)
     */
    public DateFormat getUserDateFormatter1(User user, int part, String dateFormatId, String timeZoneID) throws ServiceException {
        KWLDateFormat df=user.getDateFormat();
        SimpleDateFormat sdf = null;
        try {
            if(df==null){
                df = (KWLDateFormat)get(KWLDateFormat.class, dateFormatId);
            }
            String dateformat=df.getJavaForm();
            switch(part){
                case KWLDateFormat.DATE_PART:
                    dateformat = dateformat.substring(0, df.getJavaSeperatorPosition());
                    break;
                case KWLDateFormat.TIME_PART:
                    dateformat = dateformat.substring(df.getJavaSeperatorPosition());
                    break;
            }
            sdf=new SimpleDateFormat(dateformat);
            KWLTimeZone tz=user.getTimeZone();
            if(tz == null)
                tz = user.getCompany().getTimeZone();
            if(tz == null)
                tz = (KWLTimeZone)get(KWLTimeZone.class, timeZoneID);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"+tz.getDifference()));
        } catch(Exception e) {
            throw ServiceException.FAILURE("remoteApi.getUserDateFormatter1", e);
        }
        return sdf;
    }
}
