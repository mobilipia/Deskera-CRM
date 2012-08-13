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

package com.krawler.common.notification.bizservice;

import com.krawler.common.notification.dm.NotificationDefinition;
import com.krawler.common.notification.dm.NotificationRequest;
import com.krawler.common.notification.web.NotificationConstants.*;
import com.krawler.common.service.ServiceException;
import java.util.List;
import java.util.Map;

public interface NotificationManagementService {
    public NotificationRequest saveNotificationRequest(CHANNEL channel, int Notification_type, NOTIFICATIONSTATUS notifyStatus, String userid, List<String> repecients, Long duedate, Map refIdMap, Map refTypeMap) throws ServiceException;
    public NotificationRequest updateNotificationRequest(CHANNEL channel, int Notification_type, NOTIFICATIONSTATUS notifyStatus, String userid, List<String> repecients);
    public NotificationRequest updateNotificationRequestStatus(NOTIFICATIONSTATUS notifyStatus, Long notificationReq);
    public void sendNotificationRequest(CHANNEL channel, int Notification_type, NOTIFICATIONSTATUS notifyStatus, String userid, 
            List<String> repecients, Map refIdMap, Map refTypeMap, Map<String, Object> extraParams)  throws ServiceException;
    public Map<String, Object> getExtractorInfo(Long notifyReqId, Map<String, Object> extraParams);
    public NotificationDefinition getNotificationDefinition(CHANNEL channel, int Notification_type);
}
