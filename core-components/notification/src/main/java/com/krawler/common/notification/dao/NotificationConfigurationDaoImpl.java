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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.krawler.common.notification.tables.NotificationSetting;
import com.krawler.common.query.Clause;
import com.krawler.common.query.Paging;
import com.krawler.dao.BaseDAO;



public class NotificationConfigurationDaoImpl extends BaseDAO implements NotificationConfigurationDao {

	@Override
	public NotificationSetting getNotificationSetting(String settingId) {
		return (NotificationSetting)get(NotificationSetting.class, settingId);
	}
	
	@Override
	public void saveNotificationSetting(NotificationSetting setting) {
		saveOrUpdate(setting);
	}

	@Override
	public void deleteNotificationSettings(String[] settingids) {
			 String hql="update NotificationSetting set deleted=? where id in (:ids)";
			 Map<String, Object> m = new HashMap<String, Object>();m.put("ids", settingids);
			executeUpdate(hql,new Object[]{true},m);

	}

	@Override
	public List<NotificationSetting> getNotificationSettings(Clause[] clauses, List<Object> positionalParams,Map<String, Object> namedParams, Paging paging) {
		List result=null;
		
		String hql=buildQuery("from NotificationSetting n",clauses);
		if(paging!=null)
			paging.updateTotalCount(this,hql, positionalParams.toArray(),namedParams);
		result=executeQuery(hql, positionalParams.toArray(), namedParams);
		return result;
	}

	@Override
	public void deleteNotificationProperties(String settingid) {
		 String hql="delete NotificationProperties where setting.id = ?";
		 executeUpdate(hql,settingid);
	}
}
