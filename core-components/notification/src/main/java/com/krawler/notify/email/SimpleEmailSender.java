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
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.krawler.notify.Notification;
import com.krawler.notify.NotificationException;
import com.krawler.notify.NotificationListener;
import com.krawler.notify.NotificationQueueManager;
import com.krawler.notify.NotificationResult;

public class SimpleEmailSender implements EmailSender {
	private static final Log logger = LogFactory.getLog(SimpleEmailSender.class);
	private NotificationQueueManager queueManager;
	private Properties defaults;
	private String loginName;
	private String password;
	private double loadSize=0;
		
	public void setQueueManager(NotificationQueueManager queueManager) {
		this.queueManager = queueManager;
	}

	public void setDefaults(Properties defaults) {
		this.defaults = defaults;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void queue(Notification notification) throws NotificationException {
		if(!(notification instanceof EmailNotification))
			throw new NotificationException("Only Emails can be queued using email sender");
		
		queueManager.add(notification);		
		loadSize+=((EmailNotification)notification).getMessageCount();
	}

	@Override
	public void send(Notification notification) throws NotificationException {
		if(!(notification instanceof EmailNotification))
			throw new NotificationException("Only Emails can be sent using email sender");
		EmailNotification en = (EmailNotification)notification;
		Thread t = new Thread(new MessageConsumer(en.getMessageStore(),en));
		en.produce();
		t.start();
	}
	
	public synchronized void send(Message msg) throws NotificationException {
		try {
			logger.info(defaults);
			Transport.send(msg);
		} catch (Exception e) {
			throw new NotificationException(captureMessage(e), e);
		}finally{
			loadSize--;
		}
	}
	
	private String captureMessage(Exception e){
		String msg=e.getMessage();
		if(msg==null && e instanceof MessagingException){
			Exception ne = ((MessagingException)e).getNextException();
			if(ne!=null)
				msg = ne.getMessage();
		}
		if(msg==null && e.getCause()!=null){
			msg=e.getCause().getMessage();
		}
		if(msg==null){
			msg = "Could not connect to mail server because of inappropriate information.";
		}
		return msg;
	}
	
	public synchronized Session createSession(EmailNotification notification){
		Properties props = new Properties(defaults);
		Map<String, Object> customProps = notification.getCustomSetting();
		if(customProps!=null){
			props.putAll(customProps);
		}
				
		return Session.getInstance(props, getAuthenticator(notification.getPasswordAuthentication()));
	}
	
	private Authenticator getAuthenticator(final PasswordAuthentication authInfo){
		if(authInfo!=null)
			return new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return authInfo;
            }
		};			

		if(loginName!=null&&loginName.length()>0&&password!=null&&password.length()>0){
			return new Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(loginName, password);
	            }
			};
		}
		
		return null;
	}
	
	@Override
	public double getLoadFactor() {
		return loadSize;
	}


	private class MessageConsumer implements Runnable {
		private MessageStore store;
		private NotificationListener listener;
		private Log logger = LogFactory.getLog(MessageConsumer.class);

		public MessageConsumer(MessageStore store, NotificationListener listener) {
			this.store = store;
			this.listener = listener;
		}

		@Override
		public void run() {
			long sent = 0, failed = 0;
			do{
				MessageInfo info = store.get();
				
				if(info==null)break;
				NotificationResult result=null;
				try {
						send(info.getMessage());
						result= new EmailNotificationResult(null, info);
						sent++;
				} catch (NotificationException e) {
					result= new EmailNotificationResult(e,info);
					failed++;
				} finally {
					listener.handleResult(result);
				}				
			}while(true);
			listener.afterComplete(sent,failed);
		}	
	}
}
