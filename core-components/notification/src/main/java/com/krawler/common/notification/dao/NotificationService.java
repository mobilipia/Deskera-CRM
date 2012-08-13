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
package com.krawler.common.notification.dao;

import com.krawler.common.notification.dm.NotificationDefinition;
import com.krawler.common.notification.dm.NotificationRequest;
import com.krawler.common.notification.dm.Recepients;
import java.util.Map;

public interface NotificationService {
    public NotificationRequest getNotificationRequest(Long notifyReq);
    public NotificationRequest saveNotificationRequest(NotificationRequest notificationReq);
    public NotificationRequest updateNotificationRequest(Long notificationReqId, Map<String, Object> values);
    public Recepients updateRecipientStatus(Long recipientId, Long statusId);
    public NotificationDefinition getNotifyDefinition(int notificationType, int channelId);
}
