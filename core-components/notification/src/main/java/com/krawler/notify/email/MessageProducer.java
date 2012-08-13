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

import javax.mail.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.krawler.notify.NotificationException;

public class MessageProducer implements Runnable {
	private EmailNotification notification;
	private Session session;
	private Log logger = LogFactory.getLog(MessageProducer.class);

	public MessageProducer(EmailNotification notification, Session session) {
		this.notification = notification;
		this.session = session;
	}
	
	@Override
	public void run() {
		try {
			this.notification.prepare(session);
			this.notification.getMessageStore().put(null);
		} catch (NotificationException e) {
			logger.error(e);
		}
		
	}

}
