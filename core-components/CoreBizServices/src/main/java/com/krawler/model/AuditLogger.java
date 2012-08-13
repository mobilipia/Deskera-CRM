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
package com.krawler.model;

/**
 * @author Johnson
 * 
 *         This class is used to track the user activity of the logged in user.
 *         Should be ideally used via a a spring aspect.
 */
public class AuditLogger {


    public static final String STATUS_SUCCESS = "Success";
    public static final String STATUS_FAILURE = "failed";
    private static final String STATUS_PROGRESS = "progress";

    private String id;
    private AuditFeature auditFeature;
    private String userName;
    private String sessionId;
    private long time;
    private String ipAddress;
    private String userid;


    public AuditLogger() {
        super();
    }

    /**
     * @param captureFailure
     */
    public AuditLogger(String captureFailure) {
        userName = captureFailure;
        sessionId = captureFailure;
    }

    /**
     * @return controllerTarget
     */
    public AuditFeature getAuditFeature() {
        return auditFeature;
    }

    /**
     * Sets the name/action of the controller called
     *
     * @param controllerTarget
     */
    public void setAuditFeature(AuditFeature auditFeature) {
        this.auditFeature = auditFeature;
    }

    /**
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the userName of the user
     *
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return sessionId of the session
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Sets the sessionId of the HttpSession
     *
     * @param sessionId
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @return logtime
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets the Log Time
     *
     * @param time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the ipAddress
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * @param ipAddress the ipAddress to set
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
