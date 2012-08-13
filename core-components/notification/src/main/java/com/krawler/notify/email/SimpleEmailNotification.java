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

import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.krawler.notify.NotificationException;
import com.krawler.notify.NotificationResult;

public class SimpleEmailNotification extends EmailNotification {
	private String[] recipients;
	private String subject;
	private String htmlMsg;
	private String plainMsg;
	private String fromAddress;
	private String fromName;
	private String fileName;

	public String[] getRecipients() {
		return recipients;
	}

	public SimpleEmailNotification() {
		
	}
	
	public SimpleEmailNotification(String fromAddress, String[] recipients,
			String subject, String htmlMsg) {
		this.recipients = recipients;
		this.subject = subject;
		this.htmlMsg = htmlMsg;
		this.fromAddress = fromAddress;
	}

	public void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getHtmlMsg() {
		return htmlMsg;
	}

	public void setHtmlMsg(String htmlMsg) {
		this.htmlMsg = htmlMsg;
	}

	public String getPlainMsg() {
		return plainMsg;
	}

	public void setPlainMsg(String plainMsg) {
		this.plainMsg = plainMsg;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void prepare(Session session) throws NotificationException {
		InternetAddress addressFrom;
		Message msg = new MimeMessage(session);
		try {
			if (fromName != null)
				addressFrom = new InternetAddress(fromAddress, fromName);
			else
				addressFrom = new InternetAddress(fromAddress);

			msg.setFrom(addressFrom);

			InternetAddress[] addressTo = new InternetAddress[recipients.length];

			for (int i = 0; i < recipients.length; i++) {
				addressTo[i] = new InternetAddress(recipients[i].trim()
						.replace(" ", "+"));
			}

			msg.setRecipients(Message.RecipientType.TO, addressTo);

			msg.setSubject(subject);
			
			Multipart multipart = new MimeMultipart("alternative");
			
			if (plainMsg != null) {
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(plainMsg, "text/plain");
				multipart.addBodyPart(messageBodyPart);
			}
			
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setContent(htmlMsg, "text/html");
			multipart.addBodyPart(messageBodyPart);

			if (fileName != null) {
				File file = new File(fileName);
				if (file.exists()) {
					BodyPart fileBodyPart = new MimeBodyPart();
					fileBodyPart.setDataHandler(new DataHandler(
							new FileDataSource(file)));
					fileBodyPart.setFileName(fileName.substring(fileName
							.lastIndexOf("/") + 1));
					BodyPart temp = new MimeBodyPart();
					temp.setContent(multipart);
					multipart = new MimeMultipart();
					multipart.addBodyPart(temp);
					multipart.addBodyPart(fileBodyPart);
				}else
					throw new NotificationException("Attachment file '"+fileName+"' not found");
			}

			msg.setContent(multipart);
		} catch (UnsupportedEncodingException e) {
			throw new NotificationException("Unsupported 'From Name' encoding");
		} catch (AddressException e) {
			throw new NotificationException("Invalid email address given: ["
					+ e.getRef() + "]");
		} catch (MessagingException e) {
			throw new NotificationException(e.getMessage());
		}
		
		this.getMessageStore().put(new MessageInfo(msg));
	}
	
	@Override
	public void handleResult(NotificationResult res) {
		EmailNotificationResult result = (EmailNotificationResult)res;
		if(result.isFailed()){
			logger.warn("Email can't be delivered", result.getCause());
		}
	}

	@Override
	public long getMessageCount() throws NotificationException {
		return 1;
	}

	@Override
	public void afterComplete(long sent, long failed) {
		// TODO Auto-generated method stub
		
	}
}
