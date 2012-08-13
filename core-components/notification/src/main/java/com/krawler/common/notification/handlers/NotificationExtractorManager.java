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
package com.krawler.common.notification.handlers;

import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.User;
import com.krawler.common.notification.dao.NotificationService;
import com.krawler.common.notification.dm.NotificationDefinition;
import com.krawler.common.notification.dm.NotificationRequest;
import com.krawler.common.notification.dm.Recepients;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.notify.NotificationException;
import com.krawler.notify.NotificationResult;
import com.krawler.notify.email.EmailNotification;
import com.krawler.notify.email.MessageInfo;
import com.krawler.notify.email.SimpleEmailNotification;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.kwlCommonTablesDAO;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static com.krawler.common.notification.web.NotificationConstants.*;
public class NotificationExtractorManager extends EmailNotification {

    private kwlCommonTablesDAO commonTablesDAOObj;
    public NotificationService notifyServiceDAO;
    private static Log LOG = LogFactory.getLog(NotificationExtractorManager.class);
    public void setCommonTablesDAO(kwlCommonTablesDAO commonTablesDAOObj) {
        this.commonTablesDAOObj = commonTablesDAOObj;
    }

    public void setNotifyServiceDAO(NotificationService notifyServiceDAO) {
        this.notifyServiceDAO = notifyServiceDAO;
    }
    
    @Override
    public void prepare(Session session) throws NotificationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void extractAndSendNotificationDefination(NotificationRequest notificationReqObj, NotificationDefinition notifyDef, Map<String, Object> extraParams) {
        try {

            Set<Recepients> recepients = notificationReqObj.getRecepients();
            for(Recepients receiver : recepients) {
                User receiverUser = receiver.getUserid();
                KWLTimeZone timeZone = authHandler.getTZforUser(receiverUser, receiverUser.getCompany(), null);
                DateFormat df = new SimpleDateFormat("MMMM d, yyyy");//authHandler.getDateFormatter();
                String tZStr = timeZone.getDifference();
                if (tZStr != null)
                {
                    TimeZone zone = TimeZone.getTimeZone("GMT" + tZStr);
                    df.setTimeZone(zone);
                }
                StringBuffer htmlMsg = new StringBuffer(notifyDef.getMessage1());
                replacePlaceholders(htmlMsg, notificationReqObj, receiverUser, df);
                
                if(extraParams.containsKey(Constants.VARIABLEDATA) && extraParams.get(Constants.VARIABLEDATA)!=null) {
                	htmlMsg.toString().replaceAll(Constants.VARIABLEDATA, extraParams.get(Constants.VARIABLEDATA).toString());
                }
                
                replaceExtraPlaceHolders(htmlMsg, extraParams);
                StringBuffer subject = new StringBuffer(notifyDef.getSubject());
                replaceExtraPlaceHolders(subject, extraParams);
                replacePlaceholders(subject, notificationReqObj, receiverUser, df);
                User sender = (User)commonTablesDAOObj.getObject(Constants.USERS_CLASSPATH, notificationReqObj.getCreatedbyid()); // load placeholder class
                try {
                    SimpleEmailNotification mail = new SimpleEmailNotification(sender.getEmailID(), new String[]{receiverUser.getEmailID()}, subject.toString() , htmlMsg.toString());
                    mail.setSenderCache(senderCache);
                    mail.send();
                    // update status as "SENT" = 1;
                    notifyServiceDAO.updateRecipientStatus(receiver.getId(), Long.parseLong(String.valueOf(NOTIFICATIONSTATUS.SENT.ordinal())));
                } catch(Exception ex) {
                    LOG.info(ex.getMessage(), ex);
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            LOG.info(ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    private void replaceExtraPlaceHolders(StringBuffer htmlMsg, Map<String, Object> extraParams) {
        try {
            Set keySets = extraParams.keySet();
            for(Object key :  keySets) {
                String value = extraParams.get(key).toString();
                int i1 = htmlMsg.indexOf(key.toString());
                while (i1 >= 0) {
                    int i2 = i1 + key.toString().length();
                    if (StringUtil.isNullOrEmpty(value)) {
                        value = "";
                    }
                    htmlMsg.replace(i1, i2, value);
                    i1 = htmlMsg.indexOf(key.toString());
                }
            }
        } catch(Exception ex) {
            LOG.info(ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }
    
    public void replacePlaceholders(StringBuffer htmlMsg, NotificationRequest notificationReqObj, User recipientUser, DateFormat df) {
        try {
            // e.g 
            // First Type of placeholder => #reftype1:oppmainowner.firstName#
            // Second Type of placeholder => #reftype1:oppname#
            java.lang.reflect.Method objMethod;
            String expr = "[#]{1}[a-zA-Z0-9]+[:]{1}[a-zA-Z0-9]+(\\.){0,1}[a-zA-Z0-9]*[#]{1}";
            Pattern p = Pattern.compile(expr);
            Matcher m = p.matcher(htmlMsg);
            while (m.find()) {
                String table = m.group();
                String woHash = table.substring(1, table.length() - 1);
                String[] sp = woHash.split(":");
                if (!StringUtil.isNullOrEmpty(sp[0])) { // sp[0] having reftype1 which holds placeholder class path
                    Class cl = notificationReqObj.getClass();
                    String methodStr = sp[0].substring(0, 1).toUpperCase() + sp[0].substring(1); // Make first letter of operand capital.
                    String methodGetIdStr = methodStr.replace("type", "id");
                    objMethod = cl.getMethod("get" + methodStr + ""); // Gets the value of the operand
                    String classPath = (String) objMethod.invoke(notificationReqObj);
                    objMethod = cl.getMethod("get" + methodGetIdStr + ""); //refid1 which holds placeholder class object primary id
                    String classObjectId = (String) objMethod.invoke(notificationReqObj);

                    // Placeholder Class
                    String value = "-";
                    Object invoker = commonTablesDAOObj.getObject(classPath, classObjectId); // load placeholder class
                    Class placeHolderClass = invoker.getClass();
                    String[] operator = sp[1].split("\\.");
                    // 
                    if (operator.length > 1) { // if having oppmainowner.firstName
                        methodStr = operator[0].substring(0, 1).toUpperCase() + operator[0].substring(1); // Make first letter of operand capital.
                        objMethod = placeHolderClass.getMethod("get" + methodStr + "");
                        Object innerClassObject = objMethod.invoke(invoker); // get oppmainowner object
                        if(!StringUtil.isNullObject(innerClassObject)) {
                            placeHolderClass = innerClassObject.getClass();
                            methodStr = operator[1].substring(0, 1).toUpperCase() + operator[1].substring(1); // Make first letter of operand capital.
                            objMethod = placeHolderClass.getMethod("get" + methodStr + "");// get oppmainowner's firstName field
                            value = String.valueOf(objMethod.invoke(innerClassObject));
                        } 
                    } else if (operator.length == 1) { // if having oppname
                        methodStr = operator[0].substring(0, 1).toUpperCase() + operator[0].substring(1); // Make first letter of operand capital.
                        objMethod = placeHolderClass.getMethod("get" + methodStr + "");
                        java.util.Date.class.isAssignableFrom(objMethod.getReturnType());
                        if(java.util.Date.class.isAssignableFrom(objMethod.getReturnType()))
                            value = df.format(((java.util.Date) objMethod.invoke(invoker)));
                        else if((methodStr.equals("Startdate") && java.lang.Long.class.isAssignableFrom(objMethod.getReturnType()))||(methodStr.equals("Enddate") && java.lang.Long.class.isAssignableFrom(objMethod.getReturnType()))){
                        	value = df.format( new java.util.Date((java.lang.Long)objMethod.invoke(invoker)));
                        }
                        else
                            value = String.valueOf(objMethod.invoke(invoker));
                    } else {
                        value = table.replaceAll("#", "@~@~");
                    }

                    int i1 = htmlMsg.indexOf(table);
                    int i2 = htmlMsg.indexOf(table) + table.length();
                    if (StringUtil.isNullOrEmpty(value)) {
                        value = "";
                    }
                    if (i1 >= 0) {
                        htmlMsg.replace(i1, i2, value);
                    }
                }
                m = p.matcher(htmlMsg);
            }

            // replace receiver placeholders
            expr = "[$]{1}recipient[:]{1}[a-zA-Z0-9]+(\\.){0,1}[a-zA-Z0-9]*[$]{1}"; //$recipient:firstName$ $recipient:lastName$
            p = Pattern.compile(expr);
            m = p.matcher(htmlMsg);
            while (m.find()) {
                String table = m.group();
                String woHash = table.substring(1, table.length() - 1);
                String[] sp = woHash.split(":");
                String value= "-";
                if (!StringUtil.isNullOrEmpty(sp[0])) { // sp[0] having recipient which holds placeholder class path
                    Class placeHolderClass = recipientUser.getClass();
                    String[] operator = sp[1].split("\\.");
                    if (operator.length > 1) { // if having oppmainowner.firstName
                        String methodStr = operator[0].substring(0, 1).toUpperCase() + operator[0].substring(1); // Make first letter of operand capital.
                        objMethod = placeHolderClass.getMethod("get" + methodStr + "");
                        Object innerClassObject = objMethod.invoke(recipientUser); // get oppmainowner object
                        if(!StringUtil.isNullObject(innerClassObject)) {
                            placeHolderClass = innerClassObject.getClass();
                            methodStr = operator[1].substring(0, 1).toUpperCase() + operator[1].substring(1); // Make first letter of operand capital.
                            objMethod = placeHolderClass.getMethod("get" + methodStr + "");// get oppmainowner's firstName field
                            value = (String) objMethod.invoke(innerClassObject);
                        }
                    } else if (operator.length == 1) { // if having oppname
                        String methodStr = operator[0].substring(0, 1).toUpperCase() + operator[0].substring(1); // Make first letter of operand capital.
                        objMethod = placeHolderClass.getMethod("get" + methodStr + "");
                        java.util.Date.class.isAssignableFrom(objMethod.getReturnType());
                        if(java.util.Date.class.isAssignableFrom(objMethod.getReturnType()))
                            value = df.format(((java.util.Date) objMethod.invoke(recipientUser)));
                        else
                            value = (String) objMethod.invoke(recipientUser);
                    } else {
                        value = table.replaceAll("$", "@~@~");
                    }

                    int i1 = htmlMsg.indexOf(table);
                    int i2 = htmlMsg.indexOf(table) + table.length();
                    if (StringUtil.isNullOrEmpty(value)) {
                        value = "";
                    }
                    if (i1 >= 0) {
                        htmlMsg.replace(i1, i2, value);
                    }
                }
                m = p.matcher(htmlMsg);
            }
        } catch (IllegalAccessException ex) {
            LOG.info(ex.getMessage(), ex);
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            LOG.info(ex.getMessage(), ex);
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            LOG.info(ex.getMessage(), ex);
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            LOG.info(ex.getMessage(), ex);
            ex.printStackTrace();
        } catch (ServiceException ex) {
            LOG.info(ex.getMessage(), ex);
            ex.printStackTrace();
        }

    }

	@Override
	public long getMessageCount() throws NotificationException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void afterComplete(long sent, long failed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleResult(NotificationResult results) {
		// TODO Auto-generated method stub
		
	}
}
