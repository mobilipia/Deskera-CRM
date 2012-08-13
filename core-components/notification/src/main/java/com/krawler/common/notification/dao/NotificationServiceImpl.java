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
package com.krawler.common.notification.dao;

import com.krawler.common.admin.User;
import com.krawler.common.notification.dm.NotificationDefinition;
import com.krawler.common.notification.dm.NotificationRequest;
import com.krawler.common.notification.dm.Recepients;
import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.common.util.StringUtil;
import com.krawler.dao.BaseDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class NotificationServiceImpl extends BaseDAO implements NotificationService{
    private static final Log LOGGER = LogFactory.getLog(NotificationServiceImpl.class);
    @Override
    public NotificationRequest saveNotificationRequest(NotificationRequest notificationReq) {
        try {
            saveOrUpdate(notificationReq);
        } catch(Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);
        } finally {
            return notificationReq;
        }
    }

    private void addRecepients(List<String> recepientIds, NotificationRequest notificationReq) {
//        Set set = new HashSet();
        for(String recepientID : recepientIds) {
            Recepients recepient = new Recepients();
            recepient.setUserid((User)get(User.class, recepientID));
//            set.add(recepient);
            notificationReq.addRecepients(recepient);
        }
//        notificationReq.setRecepients(set);
    }

    @Override
    public NotificationDefinition getNotifyDefinition(int notificationType, int channelId) {
        NotificationDefinition notifyDef =null;
        try {
             ArrayList filter_names = new ArrayList();
             ArrayList filter_params = new ArrayList();

            filter_params.add(Long.parseLong(String.valueOf(notificationType)));
            filter_params.add(Long.parseLong(String.valueOf(channelId)));

            String Hql = "From NotificationDefinition c where c.notificationtype = ? and c.channel = ?";
            List<NotificationDefinition> ll = executeQuery(Hql,filter_params.toArray());
            if(ll.size()>0) {
                notifyDef = ll.get(0);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            return notifyDef;
        }

    }

    @Override
    public NotificationRequest updateNotificationRequest(Long notificationReqId, Map values) {
        NotificationRequest notificationReq = null;
        try {
            if(!StringUtil.isNullObject(notificationReqId)) {
                notificationReq = (NotificationRequest) get(NotificationRequest.class, (Serializable) notificationReqId);
                notificationReq.setUpdatedon((new Date()).getTime());
            } else {
                notificationReq = new NotificationRequest();
                notificationReq.setCreatedon((new Date()).getTime());
                notificationReq.setUpdatedon((new Date()).getTime());
            }
            if(values.containsKey("channel")) {
                notificationReq.setChannel((Long)values.get("channel"));
            }
            if(values.containsKey("type")) {
                notificationReq.setNotificationtype((Long) values.get("type"));
            }
            if(values.containsKey("status")) {
                notificationReq.setSendstatus((Long) values.get("status"));
            }
            if(values.containsKey("userid")) {
                notificationReq.setCreatedbyid(values.get("userid").toString());
            }
            if(values.containsKey("deleteflag")) {
                notificationReq.setDeleteflag(Integer.parseInt(values.get("deleteflag").toString()));
            }
            if(values.containsKey("recepients")) {
                List recepients = (List<String>) values.get("recepients");
                addRecepients(recepients, notificationReq);
            }
            saveOrUpdate(notificationReq);
        } catch(Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);
        } finally {
            return notificationReq;
        }
    }

    @Override
    public NotificationRequest getNotificationRequest(Long notifyReq) {
        return (NotificationRequest) get(NotificationRequest.class, notifyReq);
    }

    @Override
    public Recepients updateRecipientStatus(Long recipientId, Long statusId) {
        Recepients recipient = null;
        try {
            recipient = (Recepients) get(Recepients.class, (Serializable) recipientId);
            recipient.setSendstatus(statusId);
            saveOrUpdate(recipient);
        } catch(Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);
        } finally {
            return recipient;
        }
    }
}
