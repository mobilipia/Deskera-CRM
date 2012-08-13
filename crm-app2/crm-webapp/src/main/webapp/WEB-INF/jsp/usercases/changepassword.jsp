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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.krawler.common.util.Constants" %>
<%@ page import="com.krawler.common.util.URLUtil" %>
<%@ page language="java" %>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.*"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.krawler.crm.database.tables.CrmCustomer"%><html>
<%@page import="com.krawler.esp.web.resource.Links"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="../../../images/deskera/deskera.png"/>
<%String domainname=URLUtil.getDomainName(request); %>
<title><%=domainname%> Workspace-CRM</title>
<link rel="stylesheet" type="text/css" href="../../../style/case.css"/>
<script type="text/javascript" src="../../../scripts/deskeracrm/validationScripts.js">
</script>
</head>
<body>
<%
if(request.getSession().getAttribute(Constants.SESSION_CUSTOMER_ID)==null){
	String url = URLUtil.getRequestPageURL(request, com.krawler.esp.web.resource.Links.UnprotectedLoginPageFull);
	response.sendRedirect(url+"caselogin.jsp");
}else{

	String email=(String)session.getAttribute(Constants.SESSION_CUSTOMER_EMAIL);
	    %>
		<div class="companylogo">
	        <%@include file="cust_header.jsp" %>
            <ul style="font-family:sans-serif;margin: 3px;font-size: 11px;float: right;">
    	        <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="getCustomerCases.do">Home</a></li>
        	    <li style="list-style: none;display: inline;float: left;!important;padding: 0px 4px"> | </li>
	           	<li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="newCaseForm.do">New Case</a></li>
    	        <li style="list-style: none;display: inline;float: left;!important;padding: 0px 4px"> | </li>
        	    <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="signout.do">Sign Out</a></li>
            </ul>
        </div>
		<form  action="custPassword_Change.do" method="post" ><br/><br/>
			<table   cellspacing='5' border='0' style='padding-left:4%;'>
				<tr>
					<td class="newcase">User Name:</td>
					<td><input type="text" name="uname" id="uname" readonly="readonly"  value="<%=email %>"></td>
				</tr>
				<tr>
					<td class="newcase">Enter Current Password:</td>
					<td><input type="password" name="curpass" id="curpass" onchange="hideError('errormsg')">
					<c:if test="${model.mis_pass==true}">
						<span id="errormsg" style='height: auto; display: block; overflow: auto;color: red;'>Current Password is not valid.</span>	
					</c:if>
						<span id="errormsg" style='height: auto; display: none; overflow: auto;color: red;'>Current Password is not valid.</span>
					
					</td>
					
					
				</tr>
				<tr>
					<td class="newcase">Enter New Password:</td>
					<td><input type="password" name="newpass" id ="newpass" onblur="checkvalidpassword('newpass','curpass')"></td>
				</tr>
				<tr>
					<td class="newcase"> Confirm New Password:</td>
					<td><input type="password" name="newpassagain" id ="newpassagain" onblur="confirmpasswordval('newpass','newpassagain')"></td>
				</tr>
				<tr>
					<td align="right"><input type="submit" value="Save" onclick="return validatechangepasswordForm()"/></td>
					<td align="left"><input type="button" value="Clear" onclick="clearAll('newpass','newpassagain','curpass')"/></td>
				</tr>
			</table>
			
					
			<input type="hidden" name="customerid" id="customerid" value="<%=session.getAttribute(Constants.SESSION_CUSTOMER_ID).toString()%>"/>
			<input type="hidden" name="email" id="email" value="<%=session.getAttribute(Constants.SESSION_CUSTOMER_EMAIL).toString()%>"/>
			<input type="hidden" name="cname" id="cname" value="<%=session.getAttribute(Constants.SESSION_CONTACT_NAME).toString()%>"/>
		</form>
<%
}
%>
</body>
</html>
