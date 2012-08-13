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

import java.util.*;

import com.krawler.common.notification.tables.NotificationSetting;
import com.krawler.common.query.Clause;
import com.krawler.common.query.Paging;

public interface NotificationConfigurationDao {
	NotificationSetting getNotificationSetting(String settingId);
	void saveNotificationSetting(NotificationSetting setting);
	void deleteNotificationSettings(String[] settingids);
	void deleteNotificationProperties(String settingId);
	List<NotificationSetting> getNotificationSettings(Clause[] clauses,List<Object> positionalParams,Map<String, Object> namedParams, Paging paging);
}
