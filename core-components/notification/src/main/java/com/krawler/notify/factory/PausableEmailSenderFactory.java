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
package com.krawler.notify.factory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.krawler.notify.PooledNotificationQueueManager;
import com.krawler.notify.email.PausableEmailSender;

public class PausableEmailSenderFactory<E> implements SenderFactory<E>{
	private static final long PAUSE_TIME = 120000;
	private static final long PAUSE_SIZE = 40;
	private Properties defaults;

	public void setDefaults(Properties defaults) {
		this.defaults = defaults;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E createSender(Map<String, Object> settings, String userName, String password) {
		PausableEmailSender sender= new PausableEmailSender();
		sender.setPauseTime(PAUSE_TIME);
		sender.setPauseSize(PAUSE_SIZE);
		Properties props=new Properties(defaults);
		if(settings!=null){
			props.putAll(settings);
		}
		sender.setDefaults(props);
		sender.setLoginName(userName);
		sender.setPassword(password);
		sender.setQueueManager(new PooledNotificationQueueManager(new ThreadPoolExecutor(1, 2, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>())));
		
		return (E) sender;
	}

}
