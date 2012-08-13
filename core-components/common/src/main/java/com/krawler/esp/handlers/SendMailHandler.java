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
package com.krawler.esp.handlers;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.*;
import javax.mail.internet.*;

import com.krawler.esp.utils.ConfigReader;

import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.security.Security;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

public class SendMailHandler {

    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    
	private static String getSMTPPath() {
		return ConfigReader.getinstance().get("SMTPPath");
	}
	
	private static String getSMTPPort() {
		return ConfigReader.getinstance().get("SMTPPort");
	}

    public static JSONObject getSMTPPathAndPort(JSONObject smtpConfig) {
		JSONObject jobj = new JSONObject();
        try {
            String smtpserver = ConfigReader.getinstance().get("SMTPPath");
            String smtpport = ConfigReader.getinstance().get("SMTPPort");
            if(smtpConfig.has("smtpserver")) {
                smtpserver = smtpConfig.getString("smtpserver");
            }
            jobj.put("smtpserver", smtpserver);
            if(smtpConfig.has("smtpport")) {
                smtpport = smtpConfig.getString("smtpport");
            }
            jobj.put("smtpport", smtpport );
            if(smtpConfig.has("smtpuname")) {
                jobj.put("smtpuname", smtpConfig.getString("smtpuname"));
            }
            if(smtpConfig.has("smtppwd")) {
                jobj.put("smtppwd", smtpConfig.getString("smtppwd"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

       return jobj;
	}
    
	public static void postMail(String recipients[], String subject,
			String htmlMsg, String plainMsg, String from) throws MessagingException {
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", getSMTPPath());
		props.put("mail.smtp.port", getSMTPPort());

		// create some properties and get the default Session
		Session session = Session.getInstance(props, null);
		session.setDebug(debug);

		// create a message
		MimeMessage msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i].trim().replace(" ", "+"));
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);

		Multipart multipart = new MimeMultipart("alternative");

		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(plainMsg, "text/plain");
		multipart.addBodyPart(messageBodyPart);

		messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(htmlMsg, "text/html");
		multipart.addBodyPart(messageBodyPart);

		msg.setContent(multipart);
		Transport.send(msg);
	}

    public static void postMail(String recipients[], String subject,
			String htmlMsg, String plainMsg, String fromAddress, String fromName) throws MessagingException, UnsupportedEncodingException {
		boolean debug = false;

		// Set the host smtp address
		Properties props = new Properties();
		props.put("mail.smtp.host", getSMTPPath());
		props.put("mail.smtp.port", getSMTPPort());

		// create some properties and get the default Session
		Session session = Session.getInstance(props, null);
		session.setDebug(debug);

		// create a message
		MimeMessage msg = new MimeMessage(session);

		// set the from and to address
		InternetAddress addressFrom = new InternetAddress(fromAddress,fromName);
		msg.setFrom(addressFrom);

		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i].trim().replace(" ", "+"));
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);

		// Setting the Subject and Content Type
		msg.setSubject(subject);

		Multipart multipart = new MimeMultipart("alternative");

		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(plainMsg, "text/plain");
		multipart.addBodyPart(messageBodyPart);

		messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(htmlMsg, "text/html");
		multipart.addBodyPart(messageBodyPart);

		msg.setContent(multipart);
		Transport.send(msg);
	}

    public static void postMailAttachment(String recipients[], String subject,
            String htmlMsg, String plainMsg, String from, String filename, String filenameEmail) throws MessagingException {
        boolean debug = false;
        Properties props = new Properties();
        props.put("mail.smtp.host", getSMTPPath());
        props.put("mail.smtp.port", getSMTPPort());
        Session session = Session.getInstance(props, null);
        session.setDebug(debug);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i].trim().replace(" ", "+"));
        }
        message.setRecipients(Message.RecipientType.TO, addressTo);
//        message.setRecipient(Message.RecipientType.BCC, new InternetAddress("ajay.kulkarni@krawlernetworks.com"));
        message.setSubject(subject);

        // Create the message part
        BodyPart messageBodyPart = new MimeBodyPart();

        // Fill the message
        messageBodyPart.setContent(plainMsg, "text/plain");

        Multipart multipart = new MimeMultipart("alternative");
        multipart.addBodyPart(messageBodyPart);

        messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlMsg, "text/html");
        multipart.addBodyPart(messageBodyPart);

        // Part two is attachment
        messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filenameEmail);//filename.substring(filename.lastIndexOf("/")+1)
        multipart.addBodyPart(messageBodyPart);

        // Put parts in message
        message.setContent(multipart);

        // Send the message
        Transport.send(message);
    }

    public static void postCampaignMail(String recipients[], String subject,
			String htmlMsg, String plainMsg, String fromAddress, String replyAddress[], String fromName,String trackerid,JSONObject smtpconfig) throws MessagingException, UnsupportedEncodingException, JSONException {
		boolean debug = false;
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		Properties props = new Properties();
        Session session = null;
        JSONObject smtpPath = getSMTPPathAndPort(smtpconfig);
		props.put("mail.smtp.host", smtpPath.getString("smtpserver"));
		props.put("mail.smtp.port", smtpPath.getString("smtpport"));
        
     
        if(smtpPath.has("smtpuname")){
            props.put("mail.smtp.user", smtpPath.getString("smtpuname"));
        }
        if(smtpPath.getString("smtpport").equals("465")){
                props.put("mail.smtp.socketFactory.fallback", "true");
                props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
                props.put("mail.smtp.socketFactory.port", smtpPath.getString("smtpport"));
        }
        if(smtpPath.has("smtppwd")){
            props.put("mail.smtp.password", smtpPath.getString("smtppwd"));
            props.put("mail.smtp.auth", "true");
            final String username = smtpPath.getString("smtpuname");
            final String password= smtpPath.getString("smtppwd");
            session = Session.getInstance(props,
            new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
           });
        }else{
            session = Session.getInstance(props, null);
        }
        
		session.setDebug(debug);
		MimeMessage msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress(fromAddress,fromName);
		msg.setFrom(addressFrom);
                msg.setHeader("trackerid", trackerid);
                msg.setHeader("List-Unsubscribe", "<mailto:newsletter-unsubscribe@deskera.com>");
                msg.setHeader("Precedence","bulk");
		InternetAddress[] addressTo = new InternetAddress[recipients.length];
		for (int i = 0; i < recipients.length; i++) {
			addressTo[i] = new InternetAddress(recipients[i].trim().replace(" ", "+"));
		}
		msg.setRecipients(Message.RecipientType.TO, addressTo);
		msg.setSubject(subject);
        InternetAddress[] replyTo = new InternetAddress[replyAddress.length];
        for (int i = 0; i < recipients.length; i++) {
			replyTo[i] = new InternetAddress(replyAddress[i].trim().replace(" ", "+"));
		}
        msg.setReplyTo(replyTo);
		Multipart multipart = new MimeMultipart("alternative");
		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(plainMsg, "text/plain");
		multipart.addBodyPart(messageBodyPart);
		messageBodyPart = new PreencodedMimeBodyPart("base64");
                messageBodyPart.setHeader("charset", "utf-8");
		messageBodyPart.setContent(htmlMsg, "text/html");
		multipart.addBodyPart(messageBodyPart);
		msg.setContent(multipart);
		Transport.send(msg);
	}
}
