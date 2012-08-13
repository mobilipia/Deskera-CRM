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
public class opportunityOwners {
    private String id;
    private User usersByUserid;
    private CrmOpportunity opportunity;
    private boolean mainOwner;
    private String opportunityId;

    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
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

    public CrmOpportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(CrmOpportunity opportunity) {
        this.opportunity = opportunity;
    }

    public boolean isMainOwner() {
        return mainOwner;
    }

    public void setMainOwner(boolean mainOwner) {
        this.mainOwner = mainOwner;
    }
}
