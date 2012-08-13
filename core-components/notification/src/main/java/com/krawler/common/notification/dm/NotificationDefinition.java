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

import java.util.HashSet;
import java.util.Set;

public class NotificationDefinition {
    public Long id;
    public Long channel;
    public Long notificationtype;
    public NotificationChannel refchannel;
    public NotificationType refnotificationtype;
    public String subject;
    public String message1;
    public String message2;
    public String message3;
    public Set extractorbeans;

    public Long getChannel() {
        return channel;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }

    public Set getExtractorbeans() {
        if(extractorbeans!=null)
            return extractorbeans;
        else
            return extractorbeans = new HashSet();
    }

    public void setExtractorbeans(Set extractorbeans) {
        this.extractorbeans = extractorbeans;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage1() {
        return message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public String getMessage2() {
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public String getMessage3() {
        return message3;
    }

    public void setMessage3(String message3) {
        this.message3 = message3;
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

    public NotificationType getRefnotificationtype() {
        return refnotificationtype;
    }

    public void setRefnotificationtype(NotificationType refnotificationtype) {
        this.refnotificationtype = refnotificationtype;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void addExtractorBeans(NotificationExtractor extractor) {
        extractor.setRefdefinition(this);
        getExtractorbeans().add(extractor);
    }

}
