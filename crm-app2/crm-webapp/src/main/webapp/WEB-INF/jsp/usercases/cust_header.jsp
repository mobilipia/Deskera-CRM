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
<%@page import="com.krawler.common.util.Constants" %>
<%@ page import="com.krawler.common.util.URLUtil"%>
<%@ page import="java.text.*"%>
<%@ page import="java.util.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<!--link rel="stylesheet" type="text/css" href="../../lib/resources/css/wtf-all.css"/>
<script type="text/javascript" src="../../lib/adapter/wtf/wtf-base.js"></script>
<script type="text/javascript" src="../../lib/wtf-all-debug.js"></script-->
</head>
<body>
<%
String companyname=URLUtil.getDomainName(request);
String imgurl="http://apps.deskera.com"+"/b/"+companyname+ "/images/store/?company=true";
%>
<img id="companyLogo" style="float: left;" src=<%=imgurl%>/>
<img id="companyLogo" style="float: left; margin-left: 4px; margin-top: 1px;" alt="crm" src="../../../images/crm-right-logo.gif">
<span style="font-family: 'Helvetica Neue', Helvetica, Arial, Verdana, sans-serif;margin: 3px;font-size: 14px; color:#083772"  ><%=(String)session.getAttribute(Constants.SESSION_CUSTOMER_EMAIL)%></span>
<%
try{
DateFormat df1 = new SimpleDateFormat("'GMT' Z");
%>
<span style="font-family: 'Helvetica Neue', Helvetica, Arial, Verdana, sans-serif;margin: 3px;font-size: 14px; color:#083772"  ><%=" (TimeZone "+df1.format(new Date())+")"%></span>

<%
}catch(Exception e)
{
}%>
</body>
</html>
