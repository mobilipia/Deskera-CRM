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
package com.krawler.crm.lead.dm;

import com.krawler.common.admin.User;
import com.krawler.crm.database.tables.LeadOwners;

public class LeadOwnerInfo
{
    private String leadId;
    
    private User user;
    
    private LeadOwners owner;

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

    /**
     * @return the user
     */
    public User getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /**
     * @return the owner
     */
    public LeadOwners getOwner()
    {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(LeadOwners owner)
    {
        this.owner = owner;
    }

}
