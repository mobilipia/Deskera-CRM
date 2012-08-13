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
<%@ page language="java" contentType="text/html"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<%@ page language="java" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="com.krawler.common.util.URLUtil" %>
<%@ page import="com.krawler.esp.utils.ConfigReader" %>
<%@page import="com.krawler.spring.sessionHandler.sessionHandlerImpl" %>
<%@page import="com.krawler.common.util.Constants" %>
<%@page import="com.krawler.common.util.StringUtil"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="../../../images/deskera/deskera.png"/>
<%String domainName=URLUtil.getDomainName(request); %>
<title><%=domainName%> Workspace-CRM</title>
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
%>

 
			<div class="companylogo">
                <%@include file="cust_header.jsp" %>
                <ul style="font-family: sans-serif;margin: 3px;font-size: 11px;float: right;">
                <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="getCustomerCases.do">Home</a></li>
                <li style="list-style: none;display: inline;float: left;!important;padding: 0px 4px"> | </li>
                <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="changePasswordForm.do"> Change Password</a> </li>
                <li style="list-style: none;display: inline;float: left;!important;padding: 0px 4px"> | </li>
                <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="signout.do">Sign Out</a></li>
                </ul>
            </div>
                  <form action="saveCustomerCases.do" method="POST"   name="addnewcase" enctype="multipart/form-data" ><br><br>
                  
                  
                  <table   cellspacing='0' border='0' style='padding-left:4%;'>
                  <tr><td style='color: rgb(21, 66, 139);'><h4 align="center" class='newcase'>NEW CASE</h4></td></tr>
                  <tr><td>
                  		<table >
                  		
                 	    <tr>
                  			    <td  class='newcase'>Subject:</td>
                  			    <td>
                  			    	<input type="text" value="" name="subject" id="subject" width="150px">
                  			    	<span style='height: auto; display: block; overflow: auto;color: gray;'>(Maximum 100 characters only)</span>
                  			    </td>
                  	   </tr>
                  	   
                  	   <tr>
                  			    <td  class='newcase'>Description:</td>
                 		        <td>
                 		        	<textarea rows="6" cols="23" style="resize:none;" name="description" id="description" style="width:200"></textarea>
                 		        	<span style='height: auto; display: block; overflow: auto;color: gray;'>(Maximum 250 characters only)</span>
                 		        </td>
                  	  </tr>
                  	   <tr>
                  			    <td  class='newcase'>Attachment (if any):</td>
                 		        <td><input type="file" name="attachment" id="attachment"/>
                 		        <span style='height: auto; display: block; overflow: auto;color: gray;'>(Maximum file size 10 MB)</span>
                 		        </td>
                  	  </tr>
                  	 </table>
                                 <br>
                                  <br>
                              <table align="center" width="350">
                                   <tr>
                                        <td align="right"><input type="submit" value="Save" name="save" onclick="return validatenewcaseForm()"></td>
                                        <td><input type="reset" value="clear" name="clear"></td>
                                     </tr>
                               </table>
                  </td>
                  </tr>
             </table>
         </form>
<%} %>
</body>
</html>
