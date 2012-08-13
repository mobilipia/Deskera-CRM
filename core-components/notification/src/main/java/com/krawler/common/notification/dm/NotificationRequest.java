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
import java.util.HashSet;
import java.util.Set;

public class NotificationRequest {
    public Long id;
    public Long channel;
    public Long notificationtype;
    public NotificationChannel refchannel;
    public NotificationType refnotificationtype;
    public String refid1;
    public String reftype1;
    public String refid2;
    public String reftype2;
    public String refid3;
    public String reftype3;
    public Long createdon;
    public Long updatedon;
    public Long duedate;
    public String createdbyid;
    public User refcreatedbyid;
    public Long sendstatus;
    public NotificationStatus refsendstatus;
    public Long notificationdefinition;
    NotificationDefinition refnotificationdefinition;
    public Set recepients;
    public int deleteflag;

    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }

    public String getCreatedbyid() {
        return createdbyid;
    }

    public void setCreatedbyid(String createdbyid) {
        this.createdbyid = createdbyid;
    }

    public Long getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Long createdon) {
        this.createdon = createdon;
    }

    public int getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(int deleteflag) {
        this.deleteflag = deleteflag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNotificationtype() {
        return notificationtype;
    }

    public void setNotificationtype(Long notificationtype) {
        this.notificationtype = notificationtype;
    }

    public NotificationChannel getRefchannel() {
        return refchannel;
    }

    public void setRefchannel(NotificationChannel refchannel) {
        this.refchannel = refchannel;
    }

    public User getRefcreatedbyid() {
        return refcreatedbyid;
    }

    public void setRefcreatedbyid(User refcreatedbyid) {
        this.refcreatedbyid = refcreatedbyid;
    }

    public String getRefid1() {
        return refid1;
    }

    public void setRefid1(String refid1) {
        this.refid1 = refid1;
    }

    public String getRefid2() {
        return refid2;
    }

    public void setRefid2(String refid2) {
        this.refid2 = refid2;
    }

    public String getRefid3() {
        return refid3;
    }

    public void setRefid3(String refid3) {
        this.refid3 = refid3;
    }

    public NotificationType getRefnotificationtype() {
        return refnotificationtype;
    }

    public void setRefnotificationtype(NotificationType refnotificationtype) {
        this.refnotificationtype = refnotificationtype;
    }

    public NotificationStatus getRefsendstatus() {
        return refsendstatus;
    }

    public void setRefsendstatus(NotificationStatus refsendstatus) {
        this.refsendstatus = refsendstatus;
    }

    public String getReftype1() {
        return reftype1;
    }

    public void setReftype1(String reftype1) {
        this.reftype1 = reftype1;
    }

    public String getReftype2() {
        return reftype2;
    }

    public void setReftype2(String reftype2) {
        this.reftype2 = reftype2;
    }

    public String getReftype3() {
        return reftype3;
    }

    public void setReftype3(String reftype3) {
        this.reftype3 = reftype3;
    }

    public Long getSendstatus() {
        return sendstatus;
    }

    public void setSendstatus(Long sendstatus) {
        this.sendstatus = sendstatus;
    }

    public Long getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(Long updatedon) {
        this.updatedon = updatedon;
    }

    public Long getDuedate() {
        return duedate;
    }

    public void setDuedate(Long duedate) {
        this.duedate = duedate;
    }
    
    public Set getRecepients() {
        if(recepients!=null)
            return recepients;
        else
            return recepients = new HashSet();
    }

    public void setRecepients(Set recepients) {
        this.recepients = recepients;
    }

    public Long getNotificationdefinition() {
        return notificationdefinition;
    }

    public void setNotificationdefinition(Long notificationdefinition) {
        this.notificationdefinition = notificationdefinition;
    }

    public NotificationDefinition getRefnotificationdefinition() {
        return refnotificationdefinition;
    }

    public void setRefnotificationdefinition(NotificationDefinition refnotificationdefinition) {
        this.refnotificationdefinition = refnotificationdefinition;
    }

    public void addRecepients(Recepients recepient) {
        recepient.setRequestid(this);
        getRecepients().add(recepient);
    }
}
