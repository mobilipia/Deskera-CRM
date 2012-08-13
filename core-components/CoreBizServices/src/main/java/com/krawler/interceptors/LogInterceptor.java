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
package com.krawler.interceptors;

import com.krawler.esp.utils.ConfigReader;
import com.krawler.model.AuditFeature;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.krawler.model.AuditLogger;
import com.krawler.service.IAuditLoggerService;
import javax.servlet.http.HttpSession;

/**
 * @author Johnson 
 * Intecepts request to log the user activity
 */
public class LogInterceptor extends HandlerInterceptorAdapter {

    private AuditLogger auditLogger;

    private static List<AuditLogger> auditLogs;

    private IAuditLoggerService auditLoggerService;

    private static final String CAPTURE_FAILURE = "System failed to Capture Field";

    private static final Log LOG = LogFactory.getLog(LogInterceptor.class);

    public LogInterceptor() {
        super();
        if (auditLogs == null) {
            auditLogs = new ArrayList<AuditLogger>();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
            throws Exception {
        HttpSession session = request.getSession();
        String userid = (String) session.getAttribute("userid");
        String userName = (String) session.getAttribute("username");
        Boolean freeFlag = (Boolean) session.getAttribute("isfreeuser"); // null check require
        String ipAddress = request.getHeader("x-real-ip");
        boolean logUnknown = ConfigReader.getinstance().getBoolean("logUnknownUser", false);

        if(ipAddress==null || ipAddress.length()==0){
            ipAddress = request.getRemoteAddr();
        }
        
        boolean loggingAllowed =  (freeFlag == null && logUnknown) || Boolean.TRUE.equals(freeFlag);
        
        if(loggingAllowed){
            AuditFeature aFeature = auditLoggerService.getAuditFeature(request.getPathInfo());

            if(aFeature==null){
                LOG.warn("No feature exists for url:" + request.getPathInfo());
            } else {
                auditLogger = new AuditLogger();
                auditLogger.setUserid(userid);
                auditLogger.setUserName(userName);
                auditLogger.setAuditFeature(aFeature);
                auditLogger.setSessionId(request.getSession().getId());
                auditLogger.setTime(System.currentTimeMillis());
                auditLogger.setIpAddress(ipAddress);
                addAuditLog(auditLogger);
            }
        }
    }

    /**
     * Adds the audit log to the list. If there are already 50 logs in the list
     * those 50 will be persisted, the list will be cleared and the audit log
     * will be added to the list for next batch insert.
     *
     * @param auditLogger2
     */
    private void addAuditLog(AuditLogger auditLogger) {
        if (auditLogs.size() >= 20) {
            try {
                auditLoggerService.saveAll(auditLogs);
                //clear the saved list
                auditLogs.clear();
            } catch (HibernateException e) {
                LOG.error("Error while persisting Audit Log entries. Current batch of"
                        + "logs dropped", e);
                //clear the list anyway as these entries might have probably caused
                //this exception
                auditLogs.clear();
            } catch (Exception e) {
                LOG.error("Error in Audit Logging", e);
            }

        }
        auditLogs.add(auditLogger);

    }

    /**
     * @param auditLogger
     */
    public void setAuditLogger(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    /**
     * @param auditLoggerService
     */
    public void setAuditLoggerService(IAuditLoggerService auditLoggerService) {
        this.auditLoggerService = auditLoggerService;
    }

    /**
     * @param auditLogs
     *            the auditLogs to set
     */
    public void setAuditLogs(List<AuditLogger> auditLogs) {
        this.auditLogs = auditLogs;
    }
}
