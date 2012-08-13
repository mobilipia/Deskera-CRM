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

import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.hibernate.SessionFactory;

/**
 *
 * @author Karthik
 */
public class authHandlerDAOImpl extends BaseDAO implements authHandlerDAO {

    private sessionHandlerImpl sessionHandlerImplObj;

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public KwlReturnObject verifyLogin(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        String username = "";
        String passwd = "";
        String subdomain = "";
        try {
            List params = new ArrayList();
            String filterString = "";

            if (requestParams.containsKey("user") && !StringUtil.isNullOrEmpty(requestParams.get("user").toString())) {
                username = requestParams.get("user").toString();
                params.add(username);
                filterString += " and u.userLogin.userName = ?";
            }
            if (requestParams.containsKey("pass") && !StringUtil.isNullOrEmpty(requestParams.get("pass").toString())) {
                passwd = requestParams.get("pass").toString();
                params.add(passwd);
                filterString += " and u.userLogin.password = ?";
            }
            if (requestParams.containsKey("subdomain") && !StringUtil.isNullOrEmpty(requestParams.get("subdomain").toString())) {
                subdomain = requestParams.get("subdomain").toString();
                params.add(subdomain);
                filterString += " and u.company.subDomain=?";
            }

            String Hql = "select u, u.userLogin, u.company from User as u where u.company.deleted=0 and u.deleteflag = 0 and u.company.activated=true "+filterString;
            ll = executeQuery(Hql, params.toArray());
            dl = ll.size();
            if(dl!=0) {
                KWLTimeZone timeZone = (KWLTimeZone) get(KWLTimeZone.class, "1");
                KWLDateFormat dateFormat = (KWLDateFormat) get(KWLDateFormat.class, "1");
                KWLCurrency currency = (KWLCurrency) get(KWLCurrency.class, "1");

                ll.add(timeZone);
                ll.add(dateFormat);
                ll.add(currency);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.verifyLogin", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject getPreferences(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        String timeZoneId = "";
        String dateFormatId = "";
        String currencyId = "";
        try {
            if (requestParams.containsKey("timezoneid") && !StringUtil.isNullOrEmpty(requestParams.get("timezoneid").toString())) {
                timeZoneId = requestParams.get("timezoneid").toString();
            }
            if (requestParams.containsKey("dateformatid") && !StringUtil.isNullOrEmpty(requestParams.get("dateformatid").toString())) {
                dateFormatId = requestParams.get("dateformatid").toString();
            }
            if (requestParams.containsKey("currencyid") && !StringUtil.isNullOrEmpty(requestParams.get("currencyid").toString())) {
                currencyId = requestParams.get("currencyid").toString();
            }

            KWLTimeZone timeZone = (KWLTimeZone) get(KWLTimeZone.class, timeZoneId);
            KWLDateFormat dateFormat = (KWLDateFormat) get(KWLDateFormat.class, dateFormatId);
            KWLCurrency currency = (KWLCurrency) get(KWLCurrency.class, currencyId);

            ll.add(timeZone);
            ll.add(dateFormat);
            ll.add(currency);
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getPreferences", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    public Object[] verifyCaseLogin(String cname, String subdomain) throws ServiceException {
    	return _verifyCaseLogin(cname, null, subdomain);
    }
    
    public Object[] verifyCaseLogin(String cname, String pass, String subdomain) throws ServiceException {
    	if(pass==null) ServiceException.FAILURE("No password provided", null);
    	return _verifyCaseLogin(cname, pass, subdomain);
    }

    private Object[] _verifyCaseLogin(String cname, String pass, String subdomain) throws ServiceException {
    	String filterStr="";
    	List params = new ArrayList();
    	params.add(cname);
    	params.add(subdomain);
    	if(pass!=null){
    		filterStr = " and  c.passwd = ?";
    		params.add(pass);
    	}
        String Hql = "select c.email, c.company.companyID, c.contact.contactid, c.id, c.contact.firstname, c.contact.lastname, c.active from CrmCustomer  c where  c.email = ? and c.company.subDomain=? "+filterStr;
        List ll = executeQuery(Hql, params.toArray());
        if(ll.isEmpty())
        	return null;
        return (Object[])ll.get(0);
    }
}
