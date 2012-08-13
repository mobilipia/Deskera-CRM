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
package com.krawler.spring.auditTrailModule;

public class DashboardUpdate
{
    private Long auditGroupId;
    private String userId;
    private String details;
    private Long auditTime;
    private String recid;
    private String firstName;
    private String lastName;

    public String getUserId()
    {
        return userId;
    }
    
    public void setUserId(String userId)
    {
        this.userId = userId;
    }
    public String getDetails()
    {
        return details;
    }
    public void setDetails(String details)
    {
        this.details = details;
    }
    public Long getAuditTime()
    {
        return auditTime;
    }
    public void setAuditTime(Long auditTime)
    {
        this.auditTime = auditTime;
    }
    public String getRecid()
    {
        return recid;
    }
    public void setRecid(String recid)
    {
        this.recid = recid;
    }
    public String getFirstName()
    {
        return firstName;
    }
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }
    public String getLastName()
    {
        return lastName;
    }
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
    /**
     * @return the auditGroupId
     */
    public Long getAuditGroupId()
    {
        return auditGroupId;
    }
    /**
     * @param auditGroupId the auditGroupId to set
     */
    public void setAuditGroupId(Long auditGroupId)
    {
        this.auditGroupId = auditGroupId;
    }
    
}
