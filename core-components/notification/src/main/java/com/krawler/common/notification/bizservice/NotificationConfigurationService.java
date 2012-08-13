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

import java.util.List;
import java.util.Map;
import com.krawler.common.admin.User;
import com.krawler.common.notification.tables.NotificationSetting;
import com.krawler.common.query.Clause;
import com.krawler.common.query.Paging;
import com.krawler.notify.NotificationException;

public interface NotificationConfigurationService {
	int SETTING_EMAIL = 0;

    void deleteSettings(String[] settingids);
    Map<String,Object> getPropertyMap(NotificationSetting setting);
    Map<String,Object> getPropertyMap(String settingid);
	void testEmailSetting(User user, String contact, String host, String port, String slayer, String protocol, String username, String password) throws NotificationException;
	List<NotificationSetting> getSettings(Clause[] clauses, List<Object> positionalParams,Map<String,Object> namedParams, Paging paging);
	void saveEmailSetting(String settingid, User user, String contact, String host, String port, String slayer, String protocol, String username, String password);
}
