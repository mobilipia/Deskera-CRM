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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.krawler.common.admin.User;
import com.krawler.common.notification.dao.NotificationConfigurationDao;
import com.krawler.common.notification.tables.NotificationProperties;
import com.krawler.common.notification.tables.NotificationSetting;
import com.krawler.common.query.Clause;
import com.krawler.common.query.Paging;
import com.krawler.common.util.StringUtil;
import com.krawler.notify.NotificationException;
import com.krawler.notify.SenderCache;
import com.krawler.notify.email.EmailNotification;
import com.krawler.notify.email.EmailSender;
import com.krawler.notify.email.EmailSetting;
import com.krawler.notify.email.SimpleEmailNotification;

public class NotificationConfigurationServiceImpl implements NotificationConfigurationService {
	private SenderCache<EmailSender> mailSenderCache;
	private NotificationConfigurationDao notificationMailConfigDAO;

	public void setMailSenderCache(SenderCache<EmailSender> mailSenderCache) {
		this.mailSenderCache = mailSenderCache;
	}

	public void setNotificationConfigDao(NotificationConfigurationDao notificationMailConfigDAO) {
		this.notificationMailConfigDAO = notificationMailConfigDAO;
	}

	@Override
	public void testEmailSetting(User user, String contact, String host, String port, String slayer, String protocol, String username, String password) throws NotificationException {
		String htmlmailMsg = "Hello, <br> This message is sent to check  <b>Custom OutBound Settings. <br> <font color='red'>The Custom OutBound Mail Settings are correct....!!</font></b>";
		NotificationSetting setting = prepareEmailSetting(null, user, contact, host, port, slayer, protocol, username, password);
		final EmailSetting e = new EmailSetting(setting);
		EmailNotification emailnotification = new SimpleEmailNotification(setting.getContact(), new String[] { setting.getContact() }, "Test Email for Outbound Settings", htmlmailMsg) {
			@Override
			public Map<String, Object> getCustomSetting() {
				return e.getProperties();
			}

			@Override
			public String getPassword() {
				return e.getPassword();
			}
			

			@Override
			public String getSenderKey() {
				return e.getKey();
			}

			@Override
			public String getUserName() {
				return e.getUserName();
			}
		};

		emailnotification.setSenderCache(mailSenderCache);
		emailnotification.send();
	}

	@Override
	public Map<String, Object> getPropertyMap(NotificationSetting setting) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		Set<NotificationProperties> props = setting.getProperties();
		for(NotificationProperties prop:props) {
			map.put(prop.getPropertyName(), prop.getPropertyValue());
		}
		return map;
	}

	@Override
	public Map<String, Object> getPropertyMap(String settingid) {
		return getPropertyMap(notificationMailConfigDAO.getNotificationSetting(settingid));
	}

	public NotificationSetting prepareEmailSetting(NotificationSetting setting, User user, String contact, String host, String port, String slayer, String protocol, String username, String password) {
		if (setting == null) {
			setting = new NotificationSetting();
			setting.setProperties(new HashSet<NotificationProperties>());
			setting.setType(SETTING_EMAIL);
			setting.setDeleted(false);
		}
		setting.setUser(user);
		setting.setContact(contact);
		Set<NotificationProperties> props = setting.getProperties();
		for(NotificationProperties prop:props){
			prop.setSetting(null);
		}
		props.clear();
		if(StringUtil.isNullOrEmpty(protocol))
			protocol = "smtp";
		props.add(attachProperty(setting, "protocol", protocol));
		if(!StringUtil.isNullOrEmpty(host))
			props.add(attachProperty(setting, "host", host));
		if(!StringUtil.isNullOrEmpty(port))
			props.add(attachProperty(setting, "port", port));
		if(!StringUtil.isNullOrEmpty(username)){
			props.add(attachProperty(setting, "user", username));
		}
		if(!StringUtil.isNullOrEmpty(password)){
			props.add(attachProperty(setting, "password", password));
		}
		if(!StringUtil.isNullOrEmpty(slayer))
			props.add(attachProperty(setting,"slayer", slayer));
	
		return setting;
	}
	
	private NotificationProperties attachProperty(NotificationSetting setting, String key, String value){
		NotificationProperties p = new NotificationProperties();
		p.setPropertyName(key);
		p.setSetting(setting);
		p.setPropertyValue(value);
		return p;		
	}

	@Override
	public void deleteSettings(String[] settingids) {
		notificationMailConfigDAO.deleteNotificationSettings(settingids);
	}

	@Override
	public List<NotificationSetting> getSettings(Clause[] clauses, List<Object> positionalParams, Map<String, Object> namedParams, Paging paging) {
		return notificationMailConfigDAO.getNotificationSettings(clauses, positionalParams, namedParams, paging);
	}

	@Override
	public void saveEmailSetting(String settingid, User user, String contact, String host, String port, String slayer, String protocol, String username, String password) {
		notificationMailConfigDAO.deleteNotificationProperties(settingid);
		NotificationSetting setting=prepareEmailSetting(notificationMailConfigDAO.getNotificationSetting(settingid), user, contact, host, port, slayer, protocol, username, password);
		notificationMailConfigDAO.saveNotificationSetting(setting);
	}
}
