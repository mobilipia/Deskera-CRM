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
package com.krawler.common.notification.dm;

import com.krawler.common.admin.User;

public class Recepients {
    public Long id;
    public User userid;
    public NotificationRequest requestid;
    public Long sendstatus;
    public NotificationStatus refsendstatus;

    public NotificationStatus getRefsendstatus() {
        return refsendstatus;
    }

    public void setRefsendstatus(NotificationStatus refsendstatus) {
        this.refsendstatus = refsendstatus;
    }

    public Long getSendstatus() {
        return sendstatus;
    }

    public void setSendstatus(Long sendstatus) {
        this.sendstatus = sendstatus;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationRequest getRequestid() {
        return requestid;
    }

    public void setRequestid(NotificationRequest requestid) {
        this.requestid = requestid;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }
}
