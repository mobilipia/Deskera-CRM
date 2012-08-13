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
package com.krawler.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.krawler.esp.utils.CompanyContextHolder;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReuqestListenerForDB implements ServletRequestListener {
    private static Log logger = LogFactory.getLog(ReuqestListenerForDB.class);
    public static final String COMPANY_PARAM = "cdomain";
    public static final String COMPANY_SUBDOMAIN = "subdomain";
    public static final String COMPANY_ID = "companyid";
    public static final String USER_ID = "userid";
    Pattern pattern = Pattern.compile("/[ab]/([^\\/]*)/(.*)");

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        CompanyContextHolder.clearCompanySubdomain();
    }
    
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
    	HttpServletRequest request = (HttpServletRequest)sre.getServletRequest();
        String path = request.getRequestURL().toString();
        String subdomain = null;
        if ((subdomain = extractSubdomain(path, pattern))!=null) {
            CompanyContextHolder.setCompanySubdomain(subdomain);
        } else if((subdomain = request.getParameter(COMPANY_PARAM))!=null){
        	CompanyContextHolder.setCompanySubdomain(subdomain.trim());
        } else if((subdomain = request.getParameter(COMPANY_SUBDOMAIN))!=null){
        	CompanyContextHolder.setCompanySubdomain(subdomain.trim());
        } else if(request.getParameter(COMPANY_ID)!=null){
            CompanyContextHolder.setCompanyID(request.getParameter(COMPANY_ID));
        } else if(request.getParameter(USER_ID)!=null){
            CompanyContextHolder.setUserID(request.getParameter(USER_ID));
        } else if(path.contains("deskeraCRMMOB_V1.jsp") && (subdomain = request.getParameter("d"))!=null){// handle case for iphone user authentication
        	CompanyContextHolder.setCompanySubdomain(subdomain.toLowerCase().trim());
        } else {
            clearCompanySubdomain();
        }
        logger.debug("Current subdomain : "+subdomain);
    }

    private void clearCompanySubdomain() {
        CompanyContextHolder.clearCompanySubdomain();
    }
    private String extractSubdomain(String path, Pattern p){
    	Matcher m  = p.matcher(path);
    	if(m.find())
    		return m.group(1);
    	return null;
    }
}
