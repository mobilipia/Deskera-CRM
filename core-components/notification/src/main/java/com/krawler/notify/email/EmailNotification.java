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

import java.util.Map;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.krawler.notify.Notification;
import com.krawler.notify.NotificationException;
import com.krawler.notify.NotificationListener;
import com.krawler.notify.SenderCache;

public abstract class EmailNotification implements Notification, NotificationListener {
	protected SenderCache<EmailSender> senderCache;
	protected final Log logger = LogFactory.getLog(getClass());
	private MessageStore messageStore = new MessageStore();
	
	public void setMessageStore(MessageStore messageStore) {
		this.messageStore = messageStore;
	}

	public void setSenderCache(SenderCache<EmailSender> senderCache) {
		this.senderCache = senderCache;
	}
	
	public MessageStore getMessageStore() {
		return messageStore;
	}

	public final void produce() throws NotificationException {
		Session session;
		session = senderCache.getSender(getSenderKey(), getCustomSetting(), getUserName(), getPassword()).createSession(this);
		MessageProducer producer = new MessageProducer(this, session);
		Thread t = new Thread(producer);
		t.start();
	}
	
	public Map<String, Object> getCustomSetting(){
		return null;
	}
	
	public String getUserName(){
		return null;
	}
	
	public String getPassword(){
		return null;
	}
	
	protected PasswordAuthentication getPasswordAuthentication(){
		String userName = getUserName();
		String password = getPassword();
		
		if(userName!=null&&userName.length()>0&&password!=null&&password.length()>0){
			return new PasswordAuthentication(userName, password);
		}
		
		return null;
	}
	
	public abstract void prepare(Session session) throws NotificationException;
	public abstract long getMessageCount() throws NotificationException;

	@Override
	public void send() throws NotificationException {
		senderCache.getSender(getSenderKey(), getCustomSetting(), getUserName(), getPassword()).send(this);
	}

	@Override
	public void queue() throws NotificationException {
		senderCache.getSender(getSenderKey(), getCustomSetting(), getUserName(), getPassword()).queue(this);		
	}

	public String getSenderKey() {
		return null;
	}
}
