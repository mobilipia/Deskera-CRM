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
import java.util.List;
import java.util.Map;

import javax.mail.Session;

import com.krawler.notify.Notification;
import com.krawler.notify.NotificationException;

public class SwichableEmailSender implements EmailSender {
	private List<EmailSender> emailSenders;
	private Map<Notification, EmailSender> notificationMap = new HashMap<Notification, EmailSender>();

	public SwichableEmailSender(List<EmailSender> emailSenders) {
		this.emailSenders = emailSenders;
	}

	public void addSender(EmailSender sender) {
		emailSenders.add(sender);
	}

	public boolean removeSender(EmailSender sender) {
		return emailSenders.remove(sender);
	}

	@Override
	public void queue(Notification notification) throws NotificationException {
		getSenderWithLeastLoad(notification).queue(notification);
	}

	@Override
	public synchronized double getLoadFactor() {
		double loadFactor = 0;
		for (EmailSender sender : emailSenders) {
			loadFactor += sender.getLoadFactor();
		}
		return loadFactor / emailSenders.size();
	}

	@Override
	public void send(Notification notification) throws NotificationException {
		getSenderWithLeastLoad(notification).send(notification);
		notificationMap.remove(notification);
	}

	@Override
	public Session createSession(EmailNotification notification) {
		return getSenderWithLeastLoad(notification).createSession(notification);
	}

	private EmailSender getSenderWithLeastLoad(Notification notification) {		
		EmailSender sender = notificationMap.get(notification);
		if (sender == null) {
			sender = emailSenders.get(0);
			double minLoadFactor = sender.getLoadFactor();
			for (EmailSender s : emailSenders) {
				double lf = s.getLoadFactor();
				if (lf < minLoadFactor) {
					sender = s;
					minLoadFactor = lf;
				}
			}
			notificationMap.put(notification, sender);
		}

		return sender;
	}

}
