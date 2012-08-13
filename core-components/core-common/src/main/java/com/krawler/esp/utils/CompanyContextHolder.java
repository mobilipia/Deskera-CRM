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
package com.krawler.esp.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CompanyContextHolder {
    private static Log LOG = LogFactory.getLog(CompanyContextHolder.class); 
    private static final ThreadLocal<String> contextHolder = new InheritableThreadLocal<String>();
    private static final ThreadLocal<String> contextCompanyIDHolder = new InheritableThreadLocal<String>();
    private static final ThreadLocal<String> contextUserIDHolder = new InheritableThreadLocal<String>();
    
    public static void setCompanySubdomain(String subdomain) {
    	   
    	if (contextHolder.get() != null && !contextHolder.get().equals(subdomain)) {
    		LOG.warn("Resetting subdomain from " + contextHolder.get() + " to "+ subdomain);
    	}
    	contextHolder.set(subdomain);
    }

    public static void setCompanyID(String companyId) {

    	if (contextCompanyIDHolder.get() != null && !contextCompanyIDHolder.get().equals(companyId)) {
    		LOG.warn("Resetting companyid from " + contextCompanyIDHolder.get() + " to "+ companyId);
    	}
    	contextCompanyIDHolder.set(companyId);
    }
   
    public static void setUserID(String userId) {

    	if (contextUserIDHolder.get() != null && !contextUserIDHolder.get().equals(userId)) {
    		LOG.warn("Resetting companyid from " + contextUserIDHolder.get() + " to "+ userId);
    	}
    	contextUserIDHolder.set(userId);
    }

    public static String getCompanySubdomain() {
    	   
    	if (contextHolder.get() == null) {
    		// default 
    	}
    	   
    	return (String) contextHolder.get();
    }

    public static String getCompanyID() {
    	if (contextCompanyIDHolder.get() == null) {
    		// default
    	}
    	return (String) contextCompanyIDHolder.get();
    }

    public static String getUserID() {
    	if (contextUserIDHolder.get() == null) {
    		// default
    	}
    	return (String) contextUserIDHolder.get();
    }
    
    public static void clearCompanySubdomain() {
    	contextHolder.remove();
        contextCompanyIDHolder.remove();
        contextUserIDHolder.remove();
    }
}
