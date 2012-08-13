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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

import com.krawler.esp.handlers.CampaignEmailNotification;
import com.krawler.esp.handlers.CampaignMailDAO;
import com.krawler.notify.*;
import com.krawler.spring.BaseTest;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.emailMarketing.crmEmailMarketingDAO;

public class EmailTest extends BaseTest {
	private SenderCache<EmailSender> senderCache;
    private HibernateTransactionManager txnManager;
    private kwlCommonTablesDAO commonTablesDAOObj;
    private CampaignMailDAO campaignMailDAOObj;
    private crmEmailMarketingDAO crmEmailMarketingDAOObj;

	@Autowired
	public void setTxnManager(HibernateTransactionManager txnManager) {
		this.txnManager = txnManager;
	}

	@Autowired
	public void setCommonTablesDAOObj(kwlCommonTablesDAO commonTablesDAOObj) {
		this.commonTablesDAOObj = commonTablesDAOObj;
	}

	@Autowired
	public void setCampaignMailDAOObj(CampaignMailDAO campaignMailDAOObj) {
		this.campaignMailDAOObj = campaignMailDAOObj;
	}

	@Autowired
	public void setCrmEmailMarketingDAOObj(
			crmEmailMarketingDAO crmEmailMarketingDAOObj) {
		this.crmEmailMarketingDAOObj = crmEmailMarketingDAOObj;
	}

	@Autowired
	public void setSenderCache(SenderCache<EmailSender> senderCache) {
		this.senderCache = senderCache;
	}

	@Test
	public void testSendEMail() {
		EmailNotification n = new SimpleEmailNotification(
				"newsletter@deskera.com",
				new String[] { "peter.b.rains@gmail.com"},
				"UnitTest",
				"<a target=\"_blank\" style=\"color: rgb(153, 0, 0); text-decoration: none;\" href=\"http://www.google.com\">Deskera CRM</a>")
				{

					@Override
					public String getPassword() {
						// TODO Auto-generated method stub
						return "googlecat";
					}

					@Override
					public String getUserName() {
						// TODO Auto-generated method stub
						return "peter.b.rains@gmail.com";
					}

					@Override
					public Map<String, Object> getCustomSetting() {
						HashMap m = new HashMap();
						m.put("mail.smtp.from", "peter.b.rains@gmail.com");
						m.put("mail.smtp.host", "smtp.gmail.com");
						m.put("mail.smtp.port", "465");
						m.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
						m.put("mail.smtp.auth", "true");
						return m;
					}

		};
		
		n.setSenderCache(senderCache);
		n.setMessageStore(new MessageStore());
		try {
			n.send();
		} catch (NotificationException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendEMailWrong() {
		EmailNotification n = new SimpleEmailNotification(
				"peter.b.rains@gmail.com",
				new String[] { "peter.b.rains@gmail.com"},
				"UnitTest",
				"<a target=\"_blank\" style=\"color: rgb(153, 0, 0); text-decoration: none;\" href=\"http://www.google.com\">Deskera CRM</a>");
		n.setSenderCache(senderCache);
		try {
			n.send();
		} catch (NotificationException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendEMailAttach() {
		SimpleEmailNotification n = new SimpleEmailNotification(
				"peter.b.rains@gmail.com",
				new String[] { "peter.b.rains@gmail.com"},
				"UnitTest",
				"<a target=\"_blank\" style=\"color: rgb(153, 0, 0); text-decoration: none;\" href=\"http://www.google.com\">Deskera CRM</a>");
		n.setSenderCache(senderCache);
		n.setMessageStore(new MessageStore());
		n.setFileName("/home/krawler-user/Downloads/3g_super_city.jpg");
		try {
			n.send();
		} catch (NotificationException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSendEMailAttachWithQ() {
		String subject = "UnitTest with 2 Minute Gap for 200 Mails New Impl 3 with load balancing "+new Date();
		for (int i = 0; i < 200; i++) {
			try {
				getEmailNotification(i,subject).queue();
			} catch (NotificationException e) {
				e.printStackTrace();
			}
		}
		//System.out.println(qManager.getQueueSize());
		try {
			Thread.sleep(15*60*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("Main Thread End");
	}

	private Notification getEmailNotification(int i, String subject) throws NotificationException{
		SimpleEmailNotification n = new SimpleEmailNotification(
				"peter.b.rains@gmail.com",
				new String[] { "peter.b.rains@gmail.com"},
				subject,
				"<a target=\"_blank\" style=\"color: rgb(153, 0, 0); text-decoration: none;\" href=\"http://www.google.com\">Deskera CRM</a>");
		n.setSenderCache(senderCache);
		n.setMessageStore(new MessageStore());
		n.setFileName("/home/krawler-user/Downloads/3g_super_city.jpg");
		return n;
	}
	
	@Test
	public void testSendCampEMailAttachWithQ() {
			try {
				getCampEmailNotification().queue();
			} catch (NotificationException e) {
				e.printStackTrace();
			}
		try {
			Thread.sleep(3000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("Main Thread End");
	}

	@Test
	public void testConnect() {
			try {
				Properties props = new Properties();
				props.put("mail.smtp.host", "smtp.google.com");
				props.put("mail.smtp.port", "465");
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
				props.put("mail.smtp.auth", "true");
				Transport t=Session.getInstance(props).getTransport("smtp");
				t.connect("", "");
				t.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		try {
			Thread.sleep(3000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println("Main Thread End");
	}

	private Notification getCampEmailNotification() {
		CampaignEmailNotification n = new CampaignEmailNotification("1e7d272b-6068-4573-a3e3-092434a0817f","402880e532f1cb6a0132f1d8901d0001");
		n.setCampaignMailDAO(campaignMailDAOObj);
		n.setCommonTablesDAO(commonTablesDAOObj);
		n.setCrmEmailMarketingDAO(crmEmailMarketingDAOObj);
		n.setSenderCache(senderCache);
		n.setMessageStore(new MessageStore());
		n.setTxnManager(txnManager);
		return n;
	}
	
	
}
