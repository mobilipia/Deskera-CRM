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

package com.krawler.common.notification.tables;

import java.util.Set;

import com.krawler.common.admin.User;

/**
 *
 * @author krawler
 */
public class NotificationSetting {
    private String id;
    private int type;
    private String contact;
    private Set<NotificationProperties> properties;
    private User user;
    private boolean deleted;

    public String getId() {
        return id;
    }

	public void setId(String id) {
        this.id = id;
    }

    public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

    public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public boolean isDeleted() {
        return deleted;
    }

	public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	public Set<NotificationProperties> getProperties() {
		return properties;
	}

	public void setProperties(Set<NotificationProperties> properties) {
		this.properties = properties;
	} 
	
	
}
