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
package com.krawler.notify.email;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.krawler.common.notification.bizservice.NotificationConfigurationService;
import com.krawler.common.notification.tables.NotificationProperties;
import com.krawler.common.notification.tables.NotificationSetting;

public class EmailSetting {
	private String uname;
	private String pass;
	private String key=null;
	private Map props = new HashMap();

	public EmailSetting(NotificationSetting setting) {
		if(setting!=null&&setting.getType()==NotificationConfigurationService.SETTING_EMAIL)
			populate(setting.getProperties());
	}
	
	private void populate(Set<NotificationProperties> properties) {
		Map<String, String> tmp = new HashMap<String, String>();
		for(NotificationProperties prop:properties){
			tmp.put(prop.getPropertyName(), prop.getPropertyValue());
		}
		String protocol = tmp.get("protocol");
		key = "";
		if(tmp.containsKey("host")){
			props.put("mail."+protocol+".host", tmp.get("host"));
			if(tmp.get("host")!=null)key+=tmp.get("host");
		}
		if(tmp.containsKey("port"))
			props.put("mail."+protocol+".port", tmp.get("port"));
		if(tmp.containsKey("user")){
			props.put("mail."+protocol+".user", tmp.get("user"));
			uname = tmp.get("user");
			if(uname!=null)key=uname+":"+(key==null?"":key);
		}
		if(tmp.containsKey("password")){
			props.put("mail."+protocol+".password", tmp.get("password"));
			props.put("mail."+protocol+".auth", "true");
			pass = tmp.get("password");
		}
		if("ssl".equals(tmp.get("slayer"))){
			props.put("mail."+protocol+".socketFactory.fallback", "true");
			props.put("mail."+protocol+".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			if(tmp.containsKey("port"))
				props.put("mail."+protocol+".socketFactory.port", tmp.get("port"));			
		}else if("tls".equals(tmp.get("slayer"))){
			props.put("mail."+protocol+".socketFactory.fallback", "false");
			props.put("mail."+protocol+".starttls.enable", "true");
			props.put("mail."+protocol+".socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			if(tmp.containsKey("port"))
				props.put("mail."+protocol+".socketFactory.port", tmp.get("port"));			
		}
	}
	
	public String getKey(){
		return key;
	}

	public Map getProperties(){
		return props;
	}
	
	public String getPassword(){
		return pass;
	}
	
	public String getUserName() {
		return uname;
	}
}
