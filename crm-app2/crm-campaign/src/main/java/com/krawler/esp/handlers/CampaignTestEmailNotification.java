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

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.Session;

import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import sun.misc.BASE64Encoder;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.EmailMarketing;
import com.krawler.crm.database.tables.EmailTemplate;
import com.krawler.notify.NotificationException;
import com.krawler.notify.NotificationResult;
import com.krawler.notify.email.EmailNotificationResult;
import com.krawler.notify.email.MessageInfo;

public class CampaignTestEmailNotification extends CampaignEmailNotification {
	private String testMailAddress;

	public void setTestMailAddress(String testMailAddress) {
		this.testMailAddress = testMailAddress;
	}

	public CampaignTestEmailNotification(String creatorId, String emailMarketingId) {
		super(creatorId, emailMarketingId);
	}

	protected void _prepare(Session session) throws NotificationException {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
		try {
			User sender = (User) campaignMailDAOObj.get(User.class, getCreatorId());
			Company company = sender.getCompany();
			EmailMarketing emObj = (EmailMarketing) campaignMailDAOObj.get(EmailMarketing.class, getEmailMarketingId());
			EmailTemplate emTemplate = emObj.getTemplateid();
			String fromAddress = emObj.getFromaddress();
			String replyAddress = emObj.getReplytoaddress();
			String fromName = emObj.getFromname();
			Map<String, String> defaultsMap = getDefaultsMap(emObj.getId());
			String baseUrl = getCompanyBaseURL();
            if(StringUtil.isNullOrEmpty(baseUrl)) {
                baseUrl = com.krawler.common.util.URLUtil.getDomainURL(company.getSubDomain(), false);
            }
			String htmlMessage = URLDecoder.decode(emObj.getHtmltext(), "utf-8").replaceAll("src=\"[^\"]*?video.jsp", "src=\""+baseUrl + "video.jsp");
			String plainMessage = emObj.getPlaintext();
			String subject = emTemplate.getSubject();
			if (!StringUtil.isNullOrEmpty(emObj.getSubject())) {
				subject = emObj.getSubject();
			}

			StringBuffer pBuffer = new StringBuffer(plainMessage);
			StringBuffer hBuffer = new StringBuffer(htmlMessage);
			String recipients = testMailAddress == null ? sender.getEmailID() : testMailAddress;

			replaceConditionalBlock(hBuffer, sender, sender, company);
			replaceConditionalBlock(pBuffer, sender, sender, company);
			regExMail2(pBuffer, hBuffer, sender, sender, company, defaultsMap);

			if (!StringUtil.isNullOrEmpty(recipients)) {
				String targettrkId = "test";
				String finalPmsg = "";
				String finalHtmlmsg = "<style>a>img{border-style:none;}</style>";
				finalHtmlmsg = finalHtmlmsg + hBuffer.toString();
				finalHtmlmsg = finalHtmlmsg.replaceAll("@~@~", "#");
				finalPmsg = finalPmsg + pBuffer.toString();
				finalPmsg = finalPmsg.replaceAll("@~@~", "#");
				BASE64Encoder enc = new BASE64Encoder();
				finalHtmlmsg = enc.encode(finalHtmlmsg.getBytes());
				Message msg = _prepare1(session, recipients, subject, finalHtmlmsg, finalPmsg, fromAddress, replyAddress, fromName, targettrkId);
				getMessageStore().put(new MessageInfo(msg));

			}
			txnManager.commit(status);
		} catch (Exception e) {
			txnManager.rollback(status);
			logger.warn("Can't prepare email", e);
			throw new NotificationException(e.getMessage());
		}
	}

	@Override
	public void handleResult(NotificationResult res) {
		EmailNotificationResult result = (EmailNotificationResult)res;
		if(result.isFailed()){
			logger.warn("Test email can't be delivered", result.getCause());
		}
	}

	@Override
	public void afterComplete(long sent, long failed) {
		logger.debug("Test mail send to: "+testMailAddress);
	}

	@Override
	public long getMessageCount() throws NotificationException {
		return 1;
	}
}
