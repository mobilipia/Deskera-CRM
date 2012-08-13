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

package com.krawler.crm.contact.dm;
import com.krawler.common.admin.User;
import com.krawler.crm.database.tables.contactOwners;

/**
 *
 * @author sagar
 */
public class ContactOwnerInfo {
    private String contactId;

    private User user;

    private contactOwners owner;

    /**
     * @return the contactId
     */
    public String getContactId()
    {
        return contactId;
    }

    /**
     * @param contactId the contactId to set
     */
    public void setContactId(String contactId)
    {
        this.contactId = contactId;
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
    public contactOwners getOwner()
    {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(contactOwners owner)
    {
        this.owner = owner;
    }
}
