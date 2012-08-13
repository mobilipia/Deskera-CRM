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

public class NotificationStatusHistory {
    public Long id;
    public NotificationStatus sendstatus;
    public NotificationRequest notificationrequest;
    public Long updatedon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationRequest getNotificationrequest() {
        return notificationrequest;
    }

    public void setNotificationrequest(NotificationRequest notificationrequest) {
        this.notificationrequest = notificationrequest;
    }

    public NotificationStatus getSendstatus() {
        return sendstatus;
    }

    public void setSendstatus(NotificationStatus sendstatus) {
        this.sendstatus = sendstatus;
    }

    public Long getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(Long updatedon) {
        this.updatedon = updatedon;
    }
    
}
