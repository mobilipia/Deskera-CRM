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
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="com.krawler.esp.web.resource.Links"%>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html">
		<link rel="shortcut icon" href="../../../images/deskera/deskera.png"/>
<title><c:out value="${model.cdomain}" /> Workspace-CRM</title>
 <link rel="stylesheet" type="text/css" href="../../../style/case.css"/>
</head>
<body>
             
            	 <c:choose>
 			            <c:when test="${model.customername==null}">
						<%  
						String url = URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull);
						String suburl="caselogin.jsp";
						response.sendRedirect(url+suburl); 
						
						%>		
 			            </c:when>
 			            <c:otherwise>

			<div class="companylogo">
               <%@include file="cust_header.jsp" %>
                <ul style="font-family: 'Helvetica Neue', Helvetica, Arial, Verdana, sans-serif;margin: 3px;font-size: 11px;float: right;">
                <c:if test="${model.allowNew}">
				<li style='list-style: none;display: inline;'><a style='float: left;' class='shortcuts' href="newCaseForm.do">New Case</a></li>
                <li style='list-style: none;display: inline;float: left;!important;padding: 0px 4px'> | </li>
                </c:if>
                <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="changePasswordForm.do">Change Password </a></li>
                <li style="list-style: none;display: inline;float: left;!important;padding: 0px 4px"> | </li>
                <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="signout.do"> Sign Out</a></li>
                </ul>
            </div>


 <c:if test="${model.pchanged==true}">
	
	<script type="text/javascript">
		alert("Account Password has been changed.\n You can verify by login again.");
	</script>	
</c:if>	
    
<table cellspacing='0' border='0' width='100%' >
<tr>
<th width='4%' style='color: rgb(21, 66, 139);font-size:15px' class='caseListlTD'>No</th>
<th width='10%' style='color: rgb(21, 66, 139);font-size:15px' class='caseListlTD'>Case Name</th>
<th width='12%' style='color: rgb(21, 66, 139);font-size:15px' class='caseListlTD'>Subject</th>
<th width='40%' style='color: rgb(21, 66, 139);font-size:15px' class='caseListlTD'>Description</th>
<th width='10%' style='color: rgb(21, 66, 139);font-size:15px' class='caseListlTD'>Creation Date</th>
<th width='5%' style='color: rgb(21, 66, 139);font-size:15px' class='caseListlTD'>Status</th>
</tr>
<c:if test="${empty model.cases}">
	<tr>
			<td style="padding-top: 50px; color: rgb(153, 153, 153); text-align: center; font-size: 15px;" colspan="6">No case has been added</td>
	</tr>
</c:if>	
<c:set var="i" value="0"/>
<c:forEach items="${model.cases}" var="caseObj">
			<c:set var="i" value="${i+1}" />
			<tr>
				<td class='caseListlTD' ><a class='case' href='getDetails.do?caseid=${caseObj.caseid}'><c:out value="${i}" />
				<td class='caseListlTD'>
					<div class='caseName'>
						<a class='case' href='getDetails.do?caseid=${caseObj.caseid}'><c:out value="${caseObj.casename}" />
		 					<!-- = !StringUtil.isNullOrEmpty(rs.getString(1))?rs.getString(1):"&nbsp;"-->
						</a>
					</div>
				</td>
		
			<td class='caseListlTD'>
				<div class='caseSubject'>
					<a class='case' href='getDetails.do?caseid=${caseObj.caseid}'><c:out value="${caseObj.subject}" />
						<!-- =!StringUtil.isNullOrEmpty(rs.getString(2))?rs.getString(2):"&nbsp;"-->
					</a>	
				</div>
			</td>
			<td class='caseListlTD'>
				<div class='caseDescription'>
					<a class='case' href='getDetails.do?caseid=${caseObj.caseid}'><c:out value="${caseObj.description}" />
						<!-- =!StringUtil.isNullOrEmpty(rs.getString(3))?rs.getString(3):"&nbsp;"-->
					</a>
				</div>
			</td>
			<td class='caseListlTD'>
				<a class='case' href='getDetails.do?caseid=${caseObj.caseid}'><fmt:formatDate pattern="dd-MM-yyyy" value="${caseObj.createdon}" />
					<!-- =!StringUtil.isNullOrEmpty(rs.getString(4))?df.format(Long.parseLong(rs.getString(4))):"&nbsp;"-->
				</a>
			</td>
			<td class='caseListlTD'>
				<a class='case' href='getDetails.do?caseid=${caseObj.caseid}'><c:out value="${caseObj.crmCombodataByCasestatusid.value}" />
					<!-- =!StringUtil.isNullOrEmpty(rs.getString(5))?rs.getString(5):"&nbsp;"-->
				</a>
			</td>
		</tr>
</c:forEach>
		
</table>
</c:otherwise>					
</c:choose>
</body>
</html>
