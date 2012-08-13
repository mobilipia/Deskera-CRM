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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.krawler.crm.database.tables.*"%>
<%@page import="com.krawler.common.admin.User"%>
<%@page import="com.krawler.esp.hibernate.impl.HibernateUtil"%>
<%@page import="org.hibernate.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.krawler.common.util.StringUtil"%>
<%@page import="com.krawler.utils.json.base.JSONObject"%>
<%@page import="com.krawler.utils.json.base.JSONException"%>
<%@page import="com.krawler.common.session.SessionExpiredException"%>
<%@page import="com.krawler.esp.handlers.SendMailHandler"%>
<%@page import="javax.mail.MessagingException"%>

<jsp:useBean id="sessionbean" scope="session" class="com.krawler.spring.sessionHandler.sessionHandlerImpl" />
<%
    Session hsession = null;
    String res = "";
    if(sessionbean.isValidSession(request, response)){
        String tid = request.getParameter("tid");
        JSONObject ret = new JSONObject();
        String bodyhtml = request.getParameter("bodyhtml");
        try{
            String mailid = "";
            String subject = "";
            hsession = HibernateUtil.getCurrentSession();
            String uid = sessionbean.getUserid(request);
            String hql = "FROM User u WHERE u.userID = ?";
            List lst = HibernateUtil.executeQuery(hsession, hql, uid);
            Iterator ite = lst.iterator();
            if(ite.hasNext()){
                User usr = (User)ite.next();
                mailid = usr.getEmailID();
            }
            hql = "FROM EmailTemplate t WHERE t.templateid = ?";
            lst = HibernateUtil.executeQuery(hsession, hql, tid);
            ite = lst.iterator();
            if(ite.hasNext()){
                EmailTemplate temp = (EmailTemplate) ite.next();
                subject = temp.getSubject();
            }
            ret.put("valid", true);
            if(!StringUtil.isNullOrEmpty(mailid)) {
                try {
                    SendMailHandler.postMail(new String[] {mailid}, subject, bodyhtml, "", "test@deskeracrm.com");
                    ret.put("data", "{success: true, msg: Test mail sent to your registerd mail id}");
                } catch(MessagingException e) {
                    ret.put("data", "{success: true, msg: " + e.getMessage() + "}");
                }
            } else {
                ret.put("data", "{success: false, errormsg: No emailid specified}");
            }
            res = ret.toString();
        } catch(SessionExpiredException e){
        } finally {
            HibernateUtil.closeSession(hsession);
        }
    }
%>
<%= res %>
