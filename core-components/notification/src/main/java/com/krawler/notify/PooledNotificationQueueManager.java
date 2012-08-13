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
package com.krawler.notify;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PooledNotificationQueueManager implements NotificationQueueManager {
    private static final Log logger = LogFactory.getLog(PooledNotificationQueueManager.class);
	private final ThreadPoolExecutor pool;

	public PooledNotificationQueueManager(ThreadPoolExecutor executor) {
		this.pool = executor;
	}
	
	@Override
	public void add(Notification notification) throws NotificationException{
		try {
			if(notification==null)
				throw new NotificationException("No notification given");
			pool.execute(new RunnableNotification(notification));
		}catch(RejectedExecutionException rex){
			throw new NotificationException("Notification rejected");
		}
	}

	@Override
	public int getQueueSize() {
		return pool.getQueue().size();
	}
	
	private static class RunnableNotification implements Runnable {
		private Notification notification; 
		
		public RunnableNotification(Notification notification) {
			this.notification = notification;
		}
		@Override
		public void run() {			
			try {
				notification.send();
				logger.debug(new java.util.Date());
			} catch (NotificationException e) {
				logger.error("Notification can not be sent["+notification+"]", e);
			}
		}		
	}
}
