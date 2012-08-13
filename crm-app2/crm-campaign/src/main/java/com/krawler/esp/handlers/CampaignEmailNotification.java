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
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.FolderClosedException;
import javax.mail.FolderNotFoundException;
import javax.mail.IllegalWriteException;
import javax.mail.Message;
import javax.mail.MessageRemovedException;
import javax.mail.MessagingException;
import javax.mail.MethodNotSupportedException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.ReadOnlyFolderException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.StoreClosedException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.PreencodedMimeBodyPart;

import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import sun.misc.BASE64Encoder;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.notification.tables.NotificationSetting;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CampaignLog;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.EmailMarketing;
import com.krawler.crm.database.tables.EmailMarketingDefault;
import com.krawler.crm.database.tables.EmailMarkteingTargetList;
import com.krawler.crm.database.tables.EmailTemplate;
import com.krawler.crm.database.tables.EnumEmailType;
import com.krawler.crm.database.tables.TargetList;
import com.krawler.crm.database.tables.TargetListTargets;
import com.krawler.crm.utils.Constants;
import com.krawler.notify.NotificationException;
import com.krawler.notify.NotificationResult;
import com.krawler.notify.email.EmailNotification;
import com.krawler.notify.email.EmailNotificationResult;
import com.krawler.notify.email.EmailSetting;
import com.krawler.notify.email.SimpleEmailNotification;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.emailMarketing.CampaignConstants;
import com.krawler.spring.crm.emailMarketing.crmEmailMarketingDAO;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class CampaignEmailNotification extends EmailNotification {
    private String creatorId;
    private boolean resume;
    private String emailMarketingId;
    private String companyBaseURL = "";
    protected HibernateTransactionManager txnManager;
    protected kwlCommonTablesDAO commonTablesDAOObj;
    protected CampaignMailDAO campaignMailDAOObj;
    protected crmEmailMarketingDAO crmEmailMarketingDAOObj;
    private EmailSetting setting;
    private int previousSent,previousFailed;
    protected String replacerUrl = "jspfiles/trackUrl.jsp";// "crm/emailMarketing/action/trackUrl.do";
    
    public CampaignEmailNotification(){   	
    }

    public String getCompanyBaseURL() {
        return companyBaseURL;
    }

    public void setCompanyBaseURL(String companyBaseURL) {
        this.companyBaseURL = companyBaseURL;
    }
    
	public CampaignEmailNotification(String creatorId, String emailMarketingId) {
		this.creatorId = creatorId;
		this.emailMarketingId = emailMarketingId;
	}

	public void setTxnManager(HibernateTransactionManager txnManager) {
		this.txnManager = txnManager;
	}

	public void setCommonTablesDAO(kwlCommonTablesDAO commonTablesDAOObj) {
		this.commonTablesDAOObj = commonTablesDAOObj;
	}

	public void setCampaignMailDAO(CampaignMailDAO campaignMailDAOObj) {
		this.campaignMailDAOObj = campaignMailDAOObj;
	}

	public void setCrmEmailMarketingDAO(crmEmailMarketingDAO crmEmailMarketingDAOObj) {
		this.crmEmailMarketingDAOObj = crmEmailMarketingDAOObj;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

	public String getEmailMarketingId() {
		return emailMarketingId;
	}
	public void setEmailMarketingId(String emailMarketingId) {
		this.emailMarketingId = emailMarketingId;
	}
	
	public void setResume(boolean resume){
		this.resume = resume;
	}
	
	protected void _prepare(Session session) throws NotificationException {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
			User sender = (User) campaignMailDAOObj.get(User.class, creatorId);
			Company company = sender.getCompany();
			String companyName = company.getCompanyName();
            String domainURL = "";
            if(StringUtil.isNullOrEmpty(getCompanyBaseURL())) {
                domainURL = com.krawler.common.util.URLUtil.getDomainURL(company.getSubDomain(),false) + "crm/emailMarketing/mail/unsubscribeUserMarketMail.do?";
            } else {
                domainURL = getCompanyBaseURL() + "crm/emailMarketing/mail/unsubscribeUserMarketMail.do?";
            }
			
			EmailMarketing emObj = (EmailMarketing)campaignMailDAOObj.get(EmailMarketing.class, emailMarketingId);
			if(!resume){
		        DefaultTransactionDefinition def1 = new DefaultTransactionDefinition();
		        def.setName("JE_Tx");
				def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		        TransactionStatus status1 = txnManager.getTransaction(def1);
		        try{
					emObj.setLastRunOn(System.currentTimeMillis());
					emObj.setLastRunStatus(EmailMarketing.STAUS_RUNNING);
					campaignMailDAOObj.saveOrUpdate(emObj);
					txnManager.commit(status1);
				} catch (Exception e) {
					txnManager.rollback(status1);
					logger.warn(e.getMessage());
					throw new NotificationException(e.getMessage());
				}
			}else{
				if(emObj.getLastRunStatus()==EmailMarketing.STAUS_COMPLETED){
					logger.warn("Trying to resume a completed Email Campaign : "+emailMarketingId);
					return;
				}
			}
			CrmCampaign campaignObj = emObj.getCampaignid();
			String campaignName = campaignObj.getCampaignname();
			String emailMarkName = emObj.getName();
			EmailTemplate emTemplate = emObj.getTemplateid();
			String fromAddress = emObj.getFromaddress();
			String replyAddress = emObj.getReplytoaddress();
			String fromName = emObj.getFromname();
			Map defaultsMap = getDefaultsMap(emObj.getId());
			String baseUrl = "";
            if(StringUtil.isNullOrEmpty(getCompanyBaseURL())) {
                baseUrl = com.krawler.common.util.URLUtil.getDomainURL(company.getSubDomain(),false);
            } else {
                baseUrl = getCompanyBaseURL();
            }
			String htmlMessage = URLDecoder.decode(emObj.getHtmltext(),"utf-8").replaceAll("src=\"[^\"]*?video.jsp", "src=\""+baseUrl + "video.jsp");
			String plainMessage = emObj.getPlaintext();        
			String subject = emTemplate.getSubject();
            if(!StringUtil.isNullOrEmpty(emObj.getSubject())){
                subject = emObj.getSubject();
            }

			String Hql = "from com.krawler.crm.database.tables.EmailMarkteingTargetList em where em.emailmarketingid = ? and " +
			" em.targetlistid.id in ( select ct.targetlist.id from CampaignTarget ct where ct.deleted=0 ) ";
			Iterator Targetite = campaignMailDAOObj.executeQuery(Hql, new Object[]{emObj}).iterator();
			while (Targetite.hasNext()) {
			    EmailMarkteingTargetList emTargets = (EmailMarkteingTargetList) Targetite.next();
			    TargetList TL = emTargets.getTargetlistid();
			    String targetSource = TL.getTargetsource();
			    String SubHql = "from com.krawler.crm.database.tables.TargetListTargets tlTargets " +
			        "where tlTargets.targetlistid = ? and tlTargets.deleted=0 ";
			    Iterator Targetsite = campaignMailDAOObj.executeQuery(SubHql, new Object[]{TL}).iterator();
			    while (Targetsite.hasNext()) {
			        StringBuffer pBuffer = new StringBuffer(plainMessage);
			        StringBuffer hBuffer = new StringBuffer(htmlMessage);
			        Object invoker = null;
			        Company cmpObj = null;
			        String recipients = "";
			        String name = "";
			        boolean putArch = false;
			        int putDel = 0;
			        TargetListTargets targetUser = (TargetListTargets) Targetsite.next();
			        if(resume){
			            String chkHql = "select sendingfailed from com.krawler.crm.database.tables.CampaignLog cl where cl.campaignid = ? and cl.emailmarketingid = ?" +
	                    " and cl.targetlistid =? and cl.targetid =? and cl.activityDate > cl.emailmarketingid.lastRunOn";
			            List l = campaignMailDAOObj.executeQuery(chkHql, new Object[]{campaignObj, emObj, TL, targetUser});
			            if(l.size()>0){
			            	int val = ((Number)l.get(0)).intValue();
			            	if(val==1)
			            		previousFailed++;
			            	else
			            		previousSent++;
			            	continue;
			            }
			        }
			        String classpath = "";
			        switch (targetUser.getRelatedto()) {
			            case 1: // Lead
			                classpath = "com.krawler.crm.database.tables.CrmLead";
			                invoker = commonTablesDAOObj.getObject(classpath, targetUser.getRelatedid());
			                if (invoker != null) {
			                    Class cl = invoker.getClass();
			                    Class arguments[] = new Class[]{};
			                    Object[] obj1 = new Object[]{};
			
			                    
			                   java.lang.reflect.Method objMethod = cl.getMethod("getLastname", arguments);
			                    name = name + " " + (String) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getEmail", arguments);
			                    recipients = (String) objMethod.invoke(invoker, obj1);
			                  
			                    objMethod = cl.getMethod("getIsarchive", arguments);
			                    putArch = (Boolean) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getDeleteflag", arguments);
			                    putDel = (Integer) objMethod.invoke(invoker, obj1);
			                }
			                break;
			            case 2: // Contact
			                classpath = "com.krawler.crm.database.tables.CrmContact";
			                invoker = commonTablesDAOObj.getObject(classpath, targetUser.getRelatedid());
			                if (invoker != null) {
			                    Class cl = invoker.getClass();
			                    Class arguments[] = new Class[]{};
			                    Object[] obj1 = new Object[]{};
			
			                    java.lang.reflect.Method objMethod = cl.getMethod("getFirstname", arguments);
			                    name = (String) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getLastname", arguments);
			                    name = name + " " + (String) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getEmail", arguments);
			                    recipients = (String) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getCompany", arguments);
			                    cmpObj = (Company) objMethod.invoke(invoker, obj1);
			
			                    objMethod = cl.getMethod("getIsarchive", arguments);
			                    putArch = (Boolean) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getDeleteflag", arguments);
			                    putDel = (Integer) objMethod.invoke(invoker, obj1);
			                }
			                break;
			            case 3: // Users
			                classpath = "com.krawler.common.admin.User";
			                invoker = commonTablesDAOObj.getObject(classpath, targetUser.getRelatedid());
			                if (invoker != null) {
			                    Class cl = invoker.getClass();
			                    Class arguments[] = new Class[]{};
			                    Object[] obj1 = new Object[]{};
			
			                    java.lang.reflect.Method objMethod = cl.getMethod("getFirstName", arguments);
			                    name = (String) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getLastName", arguments);
			                    name = name + " " + (String) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getEmailID", arguments);
			                    recipients = (String) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getCompany", arguments);
			                    cmpObj = (Company) objMethod.invoke(invoker, obj1);
			
			                    objMethod = cl.getMethod("getDeleteflag", arguments);
			                    putDel = (Integer) objMethod.invoke(invoker, obj1);
			                }
			                break;
			            case 4: // Target Module
			                classpath = "com.krawler.crm.database.tables.TargetModule";
			                invoker = commonTablesDAOObj.getObject(classpath, targetUser.getRelatedid());
			                if (invoker != null) {
			                    Class cl = invoker.getClass();
			                    Class arguments[] = new Class[]{};
			                    Object[] obj1 = new Object[]{};
			
			                    java.lang.reflect.Method objMethod = cl.getMethod("getFirstname", arguments);
			                    name = (String) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getLastname", arguments);
			                    name = name + " " + (String) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getEmail", arguments);
			                    recipients = (String) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getCompany", arguments);
			                    cmpObj = (Company) objMethod.invoke(invoker, obj1);
			                    
			                    
			                    objMethod = cl.getMethod("getIsarchive", arguments);
			                    putArch = (Boolean) objMethod.invoke(invoker, obj1);
			                    objMethod = cl.getMethod("getDeleteflag", arguments);
			                    putDel = (Integer) objMethod.invoke(invoker, obj1);
			                }
			                break;
			            default:
			                break;
			        }
			        
			        if(targetUser.getRelatedto()>=1 && targetUser.getRelatedto()<=4){
			        	replaceConditionalBlock(hBuffer, invoker, sender, company);
			        	replaceConditionalBlock(pBuffer, invoker, sender, company);
			        	regExMail2(pBuffer, hBuffer, invoker, sender, cmpObj, defaultsMap);
			        }
			        
			        if (putArch || putDel == 1) {
			            recipients = "";
			        } 
			        // TODO - optimize code to send mail to all target users at a time
			        if (!StringUtil.isNullOrEmpty(recipients)) {
			            // send emails to target users
			            String activityType = "targeted";
			            String ClHql = "from com.krawler.crm.database.tables.CampaignLog cl where cl.campaignid = ? and cl.emailmarketingid = ?" +
			                    " and cl.targetlistid =? and cl.targetid =? and cl.hits=1";
			            List CLll = campaignMailDAOObj.executeQuery(ClHql, new Object[]{campaignObj, emObj, TL, targetUser});
			            if (CLll.size() > 0) {
			                activityType = CampaignConstants.Crm_isunsubscribe;
			            }
			
			            CampaignLog campaignLog = createCampaignLog(campaignObj, TL, targetUser, emObj, activityType);
			            if (activityType.equals("targeted")) {
			                String targettrkId = campaignLog.getTargettrackerkey();
			                String unsubscr = "<br>If you no longer want to receive communication from this email list, please <a target=\"_blank\" href=\"" + domainURL + "flag=1&trackid=" + targettrkId + "\">click here</a> to unsubscribe.";
			                String imageSrc = "<img src='" + baseUrl + "images/store/?flag=3&trackid=" + targettrkId + "'></img>";
			                String finalPmsg = "";
			                String finalHtmlmsg = "<style>a>img{border-style:none;}</style>" +
			        		"Having trouble viewing this email? <a target=\"_blank\" href=\"" + baseUrl + "newsletter.jsp?tuid="+targetUser.getId()+"&mid="+emObj.getId()+"&uid="+sender.getUserID()+"\">Click here</a> to view it in your browser. <br>";
//			                if(emObj.isCanSpamAccepted()){
//			                	String lText=(targetSource==null?"&nbsp;":"For your Information:<br>"+targetSource);
//			                	String rText=fromName+"<br>"+fromAddress+"<br>"+companyName;
//			                	finalHtmlmsg += "<table cellspacing=\"5px\" cellpadding=\"0\" width=\"100%\"><tbody><tr><td width=\"50%\" style=\"border-top: 0px none rgb(0, 0, 0); border-bottom: 0px none rgb(255, 204, 102); text-align: left; padding: 0px;\"><div style=\"font-size: 12px; color: rgb(100, 100, 100); font-family: Helvetica; text-decoration: none;\">"+lText+"</div></td><td width=\"20px\" style=\"border-left:1px dotted rgb(100, 100, 100)\">&nbsp;</td><td style=\"border-top: 0px none rgb(0, 0, 0); border-bottom: 0px none rgb(255, 204, 102); text-align: left; padding: 0px;\"><div style=\"font-size: 12px; color: rgb(100, 100, 100); font-family: Helvetica; text-decoration: none;\">"+rText+"</div></td></tr></tbody></table>";
//			                }
			                hBuffer=new StringBuffer(replaceUrl(hBuffer.toString(),baseUrl+replacerUrl, targettrkId));
			                pBuffer=new StringBuffer(replaceUrl(pBuffer.toString(),baseUrl+replacerUrl, targettrkId));
			                finalHtmlmsg = finalHtmlmsg + hBuffer.toString();
			                finalHtmlmsg = finalHtmlmsg.replaceAll("@~@~", "#");
			                finalPmsg = finalPmsg + pBuffer.toString();
			                finalPmsg = finalPmsg.replaceAll("@~@~", "#");
			                finalPmsg += unsubscr + imageSrc;
			                finalHtmlmsg += unsubscr + imageSrc;
			                BASE64Encoder enc = new BASE64Encoder();
			                finalHtmlmsg = enc.encode(finalHtmlmsg.getBytes());
			                Message msg = _prepare1(session, recipients, subject, finalHtmlmsg, finalPmsg, fromAddress, replyAddress, fromName,targettrkId);			            		
			                getMessageStore().put(new CampaignMessageInfo(msg, campaignLog));			             
			            }
			        }
			    }
			}
			emObj.setLastRunStatus(EmailMarketing.STAUS_COMPLETED);
			campaignMailDAOObj.saveOrUpdate(emObj);
			txnManager.commit(status);
		} catch (Exception e) {
			txnManager.rollback(status);
			logger.warn("Can't prepare email", e);
	        DefaultTransactionDefinition def1 = new DefaultTransactionDefinition();
	        def.setName("JE_Tx");
			def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
	        TransactionStatus status1 = txnManager.getTransaction(def1);
	        try{
	        	EmailMarketing emObj = (EmailMarketing)campaignMailDAOObj.get(EmailMarketing.class, emailMarketingId);
				emObj.setLastRunStatus(EmailMarketing.STAUS_INTERRUPTED);
				campaignMailDAOObj.saveOrUpdate(emObj);
				txnManager.commit(status1);
			} catch (Exception ie) {
				txnManager.rollback(status1);
			}
			throw new NotificationException(e.getMessage());
		}
	}

	protected Message _prepare1(Session session, String recipient, String subject, String htmlMsg, String plainMsg, String fromAddress, String replyAddress, String fromName, String trackerId) throws NotificationException {
		InternetAddress addressFrom;
		Message msg = new MimeMessage(session);
		try {
			if (fromName != null)
				addressFrom = new InternetAddress(fromAddress, fromName);
			else
				addressFrom = new InternetAddress(fromAddress);

			msg.setFrom(addressFrom);

			InternetAddress[] addressTo = new InternetAddress[1];

			addressTo[0] = new InternetAddress(recipient.trim().replace(" ", "+"));

			msg.setRecipients(Message.RecipientType.TO, addressTo);

			msg.setSubject(subject);
			
			InternetAddress[] replyTo = new InternetAddress[1];

			replyTo[0] = new InternetAddress(replyAddress.trim().replace(" ", "+"));
			
			msg.setReplyTo(replyTo);
			
			Multipart multipart = new MimeMultipart("alternative");
			
			if (plainMsg != null) {
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(plainMsg, "text/plain");
				multipart.addBodyPart(messageBodyPart);
			}
			
			BodyPart messageBodyPart = new PreencodedMimeBodyPart("base64");
            messageBodyPart.setHeader("charset", "utf-8");
            messageBodyPart.setContent(htmlMsg, "text/html");
			multipart.addBodyPart(messageBodyPart);
			msg.setContent(multipart);
            msg.setHeader("trackerid", trackerId);
            msg.setHeader("List-Unsubscribe", "<mailto:newsletter-unsubscribe@deskera.com>");
            msg.setHeader("Precedence","bulk");

		} catch (UnsupportedEncodingException e) {
			throw new NotificationException("Unsupported 'From Name' encoding");
		} catch (AddressException e) {
			throw new NotificationException("Invalid email address given: ["+ e.getRef() + "]");
		} catch (MessagingException e) {
			throw new NotificationException(e.getMessage());
		}
		return msg;
	}
	
    private String replaceUrl(String htmlmsg, String url, String trackid) {
		return htmlmsg.replaceAll("(href\\s*\\=\\s*\")(.+?)(\")", "$1"+url+"?trackid="+trackid+"&origurl=$2$3");		
	}
    
	public Map<String, String> getDefaultsMap(String emid){
		Map<String, String> map = new HashMap<String, String>();
		try {
			List<EmailMarketingDefault> ll = crmEmailMarketingDAOObj.getEmailMarketingDefaults(emid);
			for(EmailMarketingDefault emd: ll){
				map.put(emd.getVariableName(), emd.getDefaultValue());
			}
		} catch (ServiceException e) {
		}
		return map;
	}

	public CampaignLog createCampaignLog(CrmCampaign campaign, TargetList targetList, TargetListTargets target,	EmailMarketing marketingid, String activityType) {
		CampaignLog campaignLog = new CampaignLog();
		campaignLog.setCampaignid(campaign);
		campaignLog.setEmailmarketingid(marketingid);
		campaignLog.setTargetlistid(targetList);
		campaignLog.setTargetid(target);
		campaignLog.setTargettrackerkey(java.util.UUID.randomUUID().toString());
		campaignLog.setActivitydate(new Date());
		campaignLog.setModifiedon(new Date());
		campaignLog.setActivitytype(activityType);
		campaignLog.setRelatedtype("Emails");
		campaignLog.setTargettype("Target");
		return campaignLog;
	}
	
	@Override
	public Map<String, Object> getCustomSetting() {
		if(setting!=null) return setting.getProperties();
		List<Object> params = new ArrayList<Object>(); 
        String smtpConfHql = "from NotificationSetting  where user= ? and deleted=? and contact=? ";
        params.add(((User) campaignMailDAOObj.get(User.class, creatorId)));
        params.add(false);
        params.add(((EmailMarketing)campaignMailDAOObj.get(EmailMarketing.class, emailMarketingId)).getFromaddress());
        List<NotificationSetting>  lst = campaignMailDAOObj.executeQuery(smtpConfHql, params.toArray());
        if(!lst.isEmpty()) {
        	setting = new EmailSetting(lst.get(0));
        }else{
        	setting = new EmailSetting(null);
        }
		return setting.getProperties();
	}

	@Override
	public String getSenderKey() {
		getCustomSetting();
		return setting.getKey();
	}
	
	@Override
	public String getPassword() {
		getCustomSetting();
		return setting.getPassword();
	}

	@Override
	public String getUserName() {
		getCustomSetting();
		return setting.getUserName();
	}

	@Override
	public void prepare(Session session) throws NotificationException {
		_prepare(session);
	}
	
	public void replaceConditionalBlock(StringBuffer message, Object invoker, User sender, Company company) {
		Pattern exp= Pattern.compile("(#(condition:(.+?)\\s+var:(.+?))#)(<br>)*(.+?)(<br>)*(#condition#)");
		Matcher m = exp.matcher(message);
		StringBuffer buf=new StringBuffer();
		while(m.find()){
			String condStr = m.group(3);
			String varStr = m.group(4);
			String varVal = getValueFor(varStr, invoker, sender, company);
		    if(condStr.equals("nullorempty")&&StringUtil.isNullOrEmpty(varVal)){
		        String groupStr = m.group(6);
		    	m.appendReplacement(buf, groupStr);
		    }else if(condStr.equals("notnullorempty")&&!StringUtil.isNullOrEmpty(varVal)){
			        String groupStr = m.group(6);
			    	m.appendReplacement(buf, groupStr);
		    }else{
		    	m.appendReplacement(buf, "");		    	
		    }
		}
		m.appendTail(buf);
		message.replace(0, message.length(), buf.toString());
	}
	
	private String getValueFor(String var, Object invoker, User sender, Company company){
        String[] sp = var.split(":");
        String value = "";
        if( invoker!=null && invoker instanceof JSONObject && sp[0].equals("campaign")){
          JSONObject jobj = (JSONObject) invoker;
          
            try {
                if(sp[1].compareToIgnoreCase("campaignname") == 0) {
                    value = jobj.getString("campaignname");
                } else if(sp[1].compareToIgnoreCase("campaignstarted") == 0) {
                    value = jobj.getString("campaignstarted");
                } else if(sp[1].compareToIgnoreCase("emailcampaigname") == 0) {
                    value = jobj.getString("emailcampaigname");
                } else if(sp[1].compareToIgnoreCase("totalemailsent") == 0) {
                    value = jobj.getString("totalemailsent");
                } else if(sp[1].compareToIgnoreCase("failbouncedmail") == 0) {
                    value = jobj.getString("failbouncedmail");
                } else if(sp[1].compareToIgnoreCase("sentcount") == 0) {
                    value = jobj.getString("sentcount");
                }
            } catch (JSONException ex) {
                logger.warn("JSONException exception in getValueFor()", ex);
                value="";
            }
 
        } else if (invoker != null && sp.length > 1 && sp[0].equals("mailrecipient")) {
            Class cl = invoker.getClass();
            Class  arguments[] = new Class[] {String.class};
            Object[] obj1 = new Object[]{sp[1]};
            try {
                java.lang.reflect.Method objMethod = cl.getMethod("getStringObj", arguments);
                value = (String) objMethod.invoke(invoker, obj1);
            } catch (IllegalAccessException e) {
                logger.warn("IllegalAccessException exception in getValueFor()", e);
                value="";
            } catch (IllegalArgumentException e) {
                logger.warn("IllegalArgumentException exception in getValueFor()", e);
                value="";
            } catch (InvocationTargetException e) {
                logger.warn("InvocationTargetException exception in getValueFor()", e);
                value="";
            } catch (NoSuchMethodException e) {
                logger.warn("NoSuchMethodException exception in getValueFor()", e);
                value="";
            }
        } else if(sp[0].equals("mailsender")){
        	value = sender.getStringObj(sp[1]);
        } else if(sp[0].equals("mailrecipient")) {
        	value = " ";
        } else if(sp[0].equals("other")) {
            if(sp[1].compareToIgnoreCase("currentyear") == 0) {
                Date dt = new Date();
                value = Integer.toString(dt.getYear() + 1900);
            }
        } else if(sp[0].compareToIgnoreCase("company") == 0) {
            if(sp[1].compareToIgnoreCase("cname") == 0 && company != null) {
            	value = company.getCompanyName();
            } else if(sp[1].compareToIgnoreCase("caddress") == 0 && company != null) {
            	value = company.getAddress();
            }  else if(sp[1].compareToIgnoreCase("cmail") == 0 && company != null) {
            	value = company.getEmailID();
            }
        }
		
        return value;
	}
    
    public void regExMail2(StringBuffer pmsg, StringBuffer htmlmsg, Object invoker, User sender, Company company, Map<String, String> defaultsMap) throws ServiceException, InvocationTargetException {
        String expr = "[#]{1}[a-z]+[:]{1}[a-z]+[#]{1}";
        Pattern p = Pattern.compile(expr);
        Matcher m = p.matcher(htmlmsg);
        while (m.find()) {
            String table = "";
            String woHash = "";
            table = m.group();
            woHash = table.substring(1, table.length() - 1);
            String[] sp = woHash.split(":");
            String replacer1 = "";
            if (invoker != null && sp.length > 1 && sp[0].equals("mailrecipient")) {
                Class cl = invoker.getClass();
                Class  arguments[] = new Class[] {String.class};
                Object[] obj1 = new Object[]{sp[1]};
                try {
                    java.lang.reflect.Method objMethod = cl.getMethod("getStringObj", arguments);
                    replacer1 = (String) objMethod.invoke(invoker, obj1);
                } catch (IllegalAccessException e) {
                    logger.warn("IllegalAccessException exception in regExMail2()", e);
                    throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.regExMail2() : "+e.getMessage(), e);
                } catch (IllegalArgumentException e) {
                    logger.warn("IllegalArgumentException exception in regExMail2()", e);
                    throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.regExMail2() : "+e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    logger.warn("InvocationTargetException exception in regExMail2()", e);
                    throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.regExMail2() : "+e.getMessage(), e);
                } catch (NoSuchMethodException e) {
                    logger.warn("NoSuchMethodException exception in regExMail2()", e);
                    throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.regExMail2() : "+e.getMessage(), e);
                }
            } else if(sp[0].equals("mailsender")){
                replacer1 = sender.getStringObj(sp[1]);
            } else if(sp[0].equals("mailrecipient")) {
                replacer1 = " ";
            } else if(sp[0].equals("other")) {
                if(sp[1].compareToIgnoreCase("currentyear") == 0) {
                    Date dt = new Date();
                    replacer1 = Integer.toString(dt.getYear() + 1900);
                }
            } else if(sp[0].compareToIgnoreCase("company") == 0) {
                if(sp[1].compareToIgnoreCase("cname") == 0 && company != null) {
                    replacer1 = company.getCompanyName();
                } else if(sp[1].compareToIgnoreCase("caddress") == 0 && company != null) {
                    replacer1 = company.getAddress();
                }  else if(sp[1].compareToIgnoreCase("cmail") == 0 && company != null) {
                    replacer1 = company.getEmailID();
                }
            } else {
                replacer1 = table.replaceAll("#","@~@~");
            }
            int i1 = htmlmsg.indexOf(table);
            int i2 = htmlmsg.indexOf(table) + table.length();
            if(StringUtil.isNullOrEmpty(replacer1)){
            	replacer1 = defaultsMap.get(woHash);
            }

            if(StringUtil.isNullOrEmpty(replacer1)){
                replacer1="";
            }
            if (i1 >= 0) {
                htmlmsg.replace(i1, i2, replacer1);
            }
            int j1 = pmsg.indexOf(table);
            int j2 = pmsg.indexOf(table) + table.length();
            if (j1 >= 0) {
                pmsg.replace(j1, j2, replacer1);
            }
            m = p.matcher(htmlmsg);
        }
    }
    
    private String prepareBounceStatus(Exception nex){
    	return getCode(nex,0);
    }
    
    private String getCode(Exception ex, int level){
    	Throwable cex = ex.getCause();
    	if(cex!=null && cex instanceof Exception&&level< 5){
    		return getCode((Exception)cex, level++);
    	}else if(ex instanceof MessagingException){
    		Exception nex = ((MessagingException)ex).getNextException();
    		if(nex==null||level>=5){
            	if(ex instanceof AuthenticationFailedException){
            		return "m.a.g";
            	}else if(ex instanceof FolderClosedException){
                	return "m.f.c";	
            	}else if(ex instanceof FolderNotFoundException){
                	return "m.f.n";	
            	}else if(ex instanceof IllegalWriteException){
                	return "m.i.w";	
            	}else if(ex instanceof MessageRemovedException){
                	return "m.m.r";	
            	}else if(ex instanceof MethodNotSupportedException){
                	return "m.m.n";	
            	}else if(ex instanceof NoSuchProviderException){
                	return "m.n.p";	
            	}else if(ex instanceof ReadOnlyFolderException){
                	return "m.r.o";	
            	}else if(ex instanceof SendFailedException){
                	return "m.s.f";
            	}else if(ex instanceof StoreClosedException){
                	return "m.s.c";	
            	}    			
    		}else
    			return getCode(nex, level++);
    	}else if(ex instanceof ConnectException){
    		return "m.c.r";
    	}
    	return "m.m.g";
    }
    
    public void handleResult(NotificationResult res){
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
    	
		try{	
				EmailNotificationResult result=(EmailNotificationResult)res;
				CampaignLog log = ((CampaignMessageInfo)result.getMessageInfo()).getCampaignLog();
				if(result.isFailed()){
					log.setSendingfailed(1);
					log.setBounceStatus(prepareBounceStatus(result.getCause()));				 
				}else{
					log.setSendingfailed(0);
				}
			
				campaignMailDAOObj.saveOrUpdate(log);
			txnManager.commit(status);
	    } catch(Exception ex) {
	        logger.warn(ex.getMessage(), ex);
	        txnManager.rollback(status);
	    }
    }

	@Override
	public void afterComplete(long sent, long failed) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
		try{			
            User creater= (User)campaignMailDAOObj.get(User.class, creatorId);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String  startdate = sdf.format(new Date());
            EmailMarketing emobj = (EmailMarketing)campaignMailDAOObj.get(EmailMarketing.class, emailMarketingId);
            JSONObject jobj = new JSONObject();
            jobj.put("campaignname", emobj.getCampaignid().getCampaignname());
            jobj.put("campaignstarted", startdate);
            jobj.put("emailcampaigname", emobj.getName());
            jobj.put("totalemailsent", previousSent+previousFailed+sent+failed);
            jobj.put("failbouncedmail", previousFailed+failed);
            jobj.put("sentcount", previousSent+sent);

            StringBuffer htmlmsg=new StringBuffer();
            StringBuffer pmsg=new StringBuffer();
            StringBuffer subject=new StringBuffer();
            Company company = creater.getCompany();
            
            getMailHTMLMsg(Constants.EMAILTYPE_STATUSREPORT, Constants.EMAILTYPE_STATUSREPORT_ID, company.getCompanyID(),htmlmsg,pmsg,subject, company.getSubDomain());
            replaceConditionalBlock(htmlmsg, jobj, creater, company);
            replaceConditionalBlock(pmsg, jobj, creater, company);
            regExMailType(pmsg, htmlmsg,  creater, company,jobj, getDefaultsMap(emailMarketingId));
            String fromName=StringUtil.getFullName(creater.getFirstName(), creater.getLastName());
           try {
        	   SimpleEmailNotification mail = new SimpleEmailNotification(creater.getEmailID(),new String[] { creater.getEmailID() },subject+"'"+emobj.getName()+"' Status Report",htmlmsg.toString());
        	   mail.setFromName(fromName);
        	   mail.setPlainMsg(pmsg.toString());
        	   mail.setSenderCache(senderCache);
        	   mail.send();
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            txnManager.commit(status);
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        }	
	}

    public String regExMailType(StringBuffer pmsg, StringBuffer htmlmsg,  User sender, Company company,JSONObject jobj, Map<String, String> defaultsMap) throws ServiceException, InvocationTargetException, JSONException {
        String expr = "[#]{1}[a-z]+[:]{1}[a-z]+[#]{1}";
        Pattern p = Pattern.compile(expr);
        Matcher m = p.matcher(htmlmsg);
        while (m.find()) {
            String table = "";
            String woHash = "";
            table = m.group();
            woHash = table.substring(1, table.length() - 1);
            String[] sp = woHash.split(":");
            String replacer1 = "";
            if(sp[0].equals("mailsender")){
                replacer1 = sender.getStringObj(sp[1]);
            } else if(sp[0].equals("mailrecipient")) {
                replacer1 = " ";
            } else if(sp[0].equals("other")) {
                if(sp[1].compareToIgnoreCase("currentyear") == 0) {
                    Date dt = new Date();
                    replacer1 = Integer.toString(dt.getYear() + 1900);
                }
            } else if(sp[0].compareToIgnoreCase("company") == 0) {
                if(sp[1].compareToIgnoreCase("cname") == 0 && company != null) {
                    replacer1 = company.getCompanyName();
                } else if(sp[1].compareToIgnoreCase("caddress") == 0 && company != null) {
                    replacer1 = company.getAddress();
                }  else if(sp[1].compareToIgnoreCase("cmail") == 0 && company != null) {
                    replacer1 = company.getEmailID();
                }
            } else if(sp[0].compareToIgnoreCase("campaign") == 0) {
                if(sp[1].compareToIgnoreCase("campaignname") == 0 && jobj.get("campaignname") != null) {
                    replacer1 = jobj.getString("campaignname");
                } else if(sp[1].compareToIgnoreCase("campaignstarted") == 0 && jobj.get("campaignstarted") != null) {
                    replacer1 = jobj.getString("campaignstarted");
                }  else if(sp[1].compareToIgnoreCase("emailcampaigname") == 0 && jobj.get("emailcampaigname") != null) {
                    replacer1 = jobj.getString("emailcampaigname");
                } else if(sp[1].compareToIgnoreCase("totalemailsent") == 0 && jobj.get("totalemailsent") != null) {
                    replacer1 = jobj.getString("totalemailsent");
                } else if(sp[1].compareToIgnoreCase("failbouncedmail") == 0 && jobj.get("failbouncedmail") != null) {
                    replacer1 = jobj.getString("failbouncedmail");
                } else if(sp[1].compareToIgnoreCase("sentcount") == 0 && jobj.get("sentcount") != null) {
                    replacer1 = jobj.getString("sentcount");
                } 
            } else {
                replacer1 = table.replaceAll("#","@~@~");
            }
            int i1 = htmlmsg.indexOf(table);
            int i2 = htmlmsg.indexOf(table) + table.length();
            if(StringUtil.isNullOrEmpty(replacer1)){
                replacer1=defaultsMap.get(woHash);
            }
            if(StringUtil.isNullOrEmpty(replacer1)){
                replacer1="";
            }
            if (i1 >= 0) {
                htmlmsg.replace(i1, i2, replacer1);
            }
            int j1 = pmsg.indexOf(table);
            int j2 = pmsg.indexOf(table) + table.length();
            if (j1 >= 0) {
                pmsg.replace(j1, j2, replacer1);
            }
            m = p.matcher(htmlmsg);
        }
        return "";
    }

    public void getMailHTMLMsg(String emailname,String emailtypeid , String companyid,StringBuffer htmlmsg,StringBuffer pmsg,StringBuffer subject, String subdomain) throws ServiceException, InvocationTargetException, UnsupportedEncodingException {
	       
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("c.name");
        filter_params.add(emailname);
        filter_names.add("c.company.companyID");
        filter_params.add(companyid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);
        KwlReturnObject kmsg = crmEmailMarketingDAOObj.getEmailTypeContent(requestParams);

        if(kmsg.getRecordTotalCount()==0){
            filter_names.clear();
            filter_params.clear();
            requestParams.clear();
            filter_names.add("c.typeid");
            filter_params.add(emailtypeid);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            kmsg = crmEmailMarketingDAOObj.getEmailTypeContent(requestParams);
        }

        List<EnumEmailType> enumEmailList = kmsg.getEntityList();
        for(EnumEmailType eet : enumEmailList) {
            String htmlmsgX = URLDecoder.decode(eet.getBody_html(),"utf-8");
            String baseUrl = getCompanyBaseURL();
            if(StringUtil.isNullOrEmpty(baseUrl)) {
                baseUrl = com.krawler.common.util.URLUtil.getDomainURL(subdomain,false);
            } 
            htmlmsgX = htmlmsgX.replaceAll("src=\"[^\"]*?video.jsp", "src=\""+baseUrl + "video.jsp");
            htmlmsg.replace(0, htmlmsg.length(), StringUtil.checkForNull(htmlmsgX));
            pmsg.replace(0, pmsg.length(), StringUtil.checkForNull(eet.getPlaintext()));
            subject.replace(0, subject.length(), StringUtil.checkForNull(eet.getSubject()));
        }
        
    }

	@Override
	public long getMessageCount() throws NotificationException {
		try {
		String hql = "select count(*) from com.krawler.crm.database.tables.TargetListTargets tlTargets " +
        "where tlTargets.targetlistid.id in (select em.targetlistid.id from com.krawler.crm.database.tables.EmailMarkteingTargetList em where em.emailmarketingid.id = ? and " +
		" em.targetlistid.id in ( select ct.targetlist.id from CampaignTarget ct where ct.deleted=0 )) and tlTargets.deleted=0 and tlTargets not in (select cl.targetid from com.krawler.crm.database.tables.CampaignLog cl where cl.emailmarketingid.id = ? and (cl.hits=1 ";
		if(resume){
			hql+=" or cl.activityDate > cl.emailmarketingid.lastRunOn";
		}
		hql+="))";
		List l =campaignMailDAOObj.executeQuery(hql, new Object[]{emailMarketingId,emailMarketingId});
		if(l.isEmpty())
			return 0;
		return ((Number)l.get(0)).longValue();
		}catch(Exception e){
			return 1;
		}
	}
}
