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
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@page import="org.springframework.transaction.support.DefaultTransactionDefinition"%>
<%@page import="org.springframework.transaction.TransactionDefinition"%>
<%@page import="org.springframework.transaction.TransactionStatus"%>

<%@page import="com.krawler.common.service.ServiceException"%>
<%@page import="org.springframework.orm.hibernate3.HibernateTransactionManager"%>
<%@page import="com.krawler.spring.crm.emailMarketing.crmEmailMarketingDAO"%>
<%@page import="java.util.Date"%>
<%@page import="com.krawler.common.util.StringUtil"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(application);
HibernateTransactionManager txnManager = (HibernateTransactionManager)context.getBean("txManager");
crmEmailMarketingDAO crmEmailMarketingDAOObj = (crmEmailMarketingDAO)context.getBean("crmEmailMarketingdao");

String origUrl = request.getParameter("origurl");
String trackid = request.getParameter("trackid");
DefaultTransactionDefinition def = new DefaultTransactionDefinition();
def.setName("JE_Tx");
def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
TransactionStatus status = txnManager.getTransaction(def);
try {
    crmEmailMarketingDAOObj.trackUrl(trackid, origUrl, new Date());
    txnManager.commit(status);
} catch (ServiceException e) {

    txnManager.rollback(status);
}
if(!StringUtil.isNullOrEmpty(origUrl)){
	response.sendRedirect(origUrl);
}
%>
</body>
</html>
