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

package com.krawler.crm.database.tables;

import com.krawler.common.admin.User;

/**
 *
 * @author krawler
 */
public class LeadOwners {
    private String id;
    private User usersByUserid;
    private CrmLead leadid;
    private boolean mainOwner;
    private String usersbyuserid;
    
    private String leadId;

    public String getUsersbyuserid() {
        return usersbyuserid;
    }

    public void setUsersbyuserid(String usersbyuserid) {
        this.usersbyuserid = usersbyuserid;
    }

    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUsersByUserid() {
        return usersByUserid;
    }

    public void setUsersByUserid(User usersByUserid) {
        this.usersByUserid = usersByUserid;
    }

    public CrmLead getLeadid() {
        return leadid;
    }

    public void setLeadid(CrmLead leadid) {
        this.leadid = leadid;
    }

    public boolean isMainOwner() {
        return mainOwner;
    }

    public void setMainOwner(boolean mainOwner) {
        this.mainOwner = mainOwner;
    }

    /**
     * @return the leadId
     */
    public String getLeadId()
    {
        return leadId;
    }

    /**
     * @param leadId the leadId to set
     */
    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }
}
