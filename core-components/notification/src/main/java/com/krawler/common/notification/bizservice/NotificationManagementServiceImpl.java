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

package com.krawler.common.notification.bizservice;

import com.krawler.common.admin.User;
import com.krawler.common.notification.dao.NotificationService;
import com.krawler.common.notification.dm.NotificationDefinition;
import com.krawler.common.notification.dm.NotificationRequest;
import com.krawler.common.notification.dm.Recepients;
import com.krawler.common.notification.handlers.NotificationExtractorManager;
import com.krawler.common.notification.web.NotificationConstants.*;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.notify.SenderCache;
import com.krawler.notify.email.EmailSender;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class NotificationManagementServiceImpl implements NotificationManagementService{
    public NotificationService notifyServiceDAO;
    private profileHandlerDAO profileHandlerDAOObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private SenderCache<EmailSender> mailSenderCache;
    private static final Log LOGGER = LogFactory.getLog(NotificationManagementServiceImpl.class);
    
    public void setMailSenderCache(SenderCache<EmailSender> mailSenderCache) {
		this.mailSenderCache = mailSenderCache;
	}

	public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }
    
    public NotificationService getNotifyServiceDAO() {
        return notifyServiceDAO;
    }

    public void setNotifyServiceDAO(NotificationService notifyServiceDAO) {
        this.notifyServiceDAO = notifyServiceDAO;
    }
    
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    @Override
    public NotificationRequest saveNotificationRequest(CHANNEL channel, int Notification_type, NOTIFICATIONSTATUS notifyStatus, String userid, List<String> recepients, Long duedate, Map refIdMap, Map refTypeMap) throws ServiceException{
        NotificationRequest notificationReq =new NotificationRequest();
        try {
//        Map<String, Object> hashTable = new Hashtable<String, Object>();
//        hashTable.put("channel", Long.parseLong(String.valueOf(channel.ordinal())));
//        hashTable.put("type", Long.parseLong(String.valueOf(Notification_type)));
//        hashTable.put("status", Long.parseLong(String.valueOf(notifyStatus.ordinal())));
//        hashTable.put("userid", userid);
//        hashTable.put("recepients", recepients);
            addRecepients(recepients, channel, notificationReq);
            if(notificationReq.getRecepients().size()>0) {
                notificationReq.setCreatedon((new Date()).getTime());
                notificationReq.setUpdatedon((new Date()).getTime());
                notificationReq.setChannel(Long.parseLong(String.valueOf(channel.ordinal())));
                notificationReq.setNotificationtype(Long.parseLong(String.valueOf(Notification_type)));
                notificationReq.setSendstatus(Long.parseLong(String.valueOf(notifyStatus.ordinal())));
                notificationReq.setCreatedbyid(userid);
                notificationReq.setDuedate(duedate);

                if (!refIdMap.isEmpty()) {
                    if (refIdMap.containsKey("refid1")) {
                        notificationReq.setRefid1(refIdMap.get("refid1").toString());
                    }
                    if (refIdMap.containsKey("refid2")) {
                        notificationReq.setRefid2(refIdMap.get("refid2").toString());
                    }
                    if (refIdMap.containsKey("refid3")) {
                        notificationReq.setRefid3(refIdMap.get("refid3").toString());
                    }
                }

                if (!refTypeMap.isEmpty()) {
                    if (refTypeMap.containsKey("reftype1")) {
                        notificationReq.setReftype1(refTypeMap.get("reftype1").toString());
                    }
                    if (refTypeMap.containsKey("reftype2")) {
                        notificationReq.setReftype2(refTypeMap.get("reftype2").toString());
                    }
                    if (refTypeMap.containsKey("reftype3")) {
                        notificationReq.setReftype3(refTypeMap.get("reftype3").toString());
                    }
                }
                notificationReq.setRefnotificationdefinition(getNotificationDefinition(channel, Notification_type));
                notificationReq = notifyServiceDAO.saveNotificationRequest(notificationReq);
            }
        } catch (ServiceException ex) {
            LOGGER.warn(ex.getMessage(), ex);
        } finally {
            return notificationReq;
        }
    }

    public NotificationRequest updateNotificationRequest(CHANNEL channel, int Notification_type, NOTIFICATIONSTATUS notifyStatus, String userid, List<String> recepients) {
        NotificationRequest notificationReq = null;
        try {
//        Map<String, Object> hashTable = new Hashtable<String, Object>();
//        hashTable.put("channel", Long.parseLong(String.valueOf(channel.ordinal())));
//        hashTable.put("type", Long.parseLong(String.valueOf(Notification_type)));
//        hashTable.put("status", Long.parseLong(String.valueOf(notifyStatus.ordinal())));
//        hashTable.put("userid", userid);
//        hashTable.put("recepients", recepients);
            notificationReq.setUpdatedon((new Date()).getTime());
            addRecepients(recepients, channel, notificationReq);
            notificationReq = notifyServiceDAO.saveNotificationRequest(notificationReq);
        } catch (ServiceException ex) {
            LOGGER.warn(ex.getMessage(), ex);
        } finally {
            return notificationReq;
        }
    }


     private void addRecepients(List<String> recepientIds, CHANNEL channel, NotificationRequest notificationReq) throws ServiceException {
//        Set set = new HashSet();
        for(String recepientID : recepientIds) {
            Recepients recepient = new Recepients();
            User userObj = profileHandlerDAOObj.getUserObject(recepientID);
            if(userObj.getNotificationtype() == channel.ordinal()) {
                recepient.setUserid(userObj);
                notificationReq.addRecepients(recepient);
            }
        }
//        notificationReq.setRecepients(set);
    }
     
    @Override
    public void sendNotificationRequest(CHANNEL channel, int Notification_type, NOTIFICATIONSTATUS notifyStatus, String userid, 
            List<String> repecients, Map refIdMap, Map refTypeMap, Map<String, Object> extraParams) throws ServiceException{
        NotificationRequest notifyReq = saveNotificationRequest(channel, Notification_type, notifyStatus, userid, repecients, (new Date()).getTime(), refIdMap, refTypeMap);
        NotificationDefinition notifyDef = notifyReq.getRefnotificationdefinition();
        NotificationExtractorManager extractManager = new NotificationExtractorManager();
        extractManager.setCommonTablesDAO(KwlCommonTablesDAOObj);
        extractManager.setNotifyServiceDAO(notifyServiceDAO);
        extractManager.setSenderCache(mailSenderCache);
        extractManager.extractAndSendNotificationDefination(notifyReq,notifyDef,extraParams);

        // update Status as "SENT"
        Map<String, Object> hashTable = new Hashtable<String, Object>();
        hashTable.put("status", Long.parseLong(String.valueOf(NOTIFICATIONSTATUS.SENT.ordinal())));
        notifyReq = notifyServiceDAO.updateNotificationRequest(notifyReq.getId(), hashTable);

    }

    @Override
    public NotificationRequest updateNotificationRequestStatus(NOTIFICATIONSTATUS notifyStatus, Long notificationReq) {
        NotificationRequest notificationReqObj = null;
        try{
            Map<String, Object> hashTable = new Hashtable<String, Object>();
            hashTable.put("status", Long.parseLong(String.valueOf(notifyStatus.ordinal())));
            notificationReqObj = notifyServiceDAO.updateNotificationRequest(notificationReq, hashTable);
        } catch (Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);
        } finally {
            return notificationReqObj;
        }
    }

    @Override
    public Map<String, Object> getExtractorInfo(Long notifyReqId, Map<String, Object> extraParams) {
        NotificationRequest notificationReqObj = notifyServiceDAO.getNotificationRequest(notifyReqId);
        NotificationDefinition notifyDef = notificationReqObj.getRefnotificationdefinition();
        NotificationExtractorManager extractManager = new NotificationExtractorManager();
        extractManager.setCommonTablesDAO(KwlCommonTablesDAOObj);
        extractManager.extractAndSendNotificationDefination(notificationReqObj,notifyDef, extraParams);
        
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public NotificationDefinition getNotificationDefinition(CHANNEL channel, int Notification_type) {
        return notifyServiceDAO.getNotifyDefinition(Notification_type, channel.ordinal());
    }
}
