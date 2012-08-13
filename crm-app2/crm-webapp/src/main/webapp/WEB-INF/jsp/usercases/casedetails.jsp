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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page import="com.krawler.esp.web.resource.Links"%>

<html>
<head>
<link rel="stylesheet" type="text/css" href="../../../style/case.css"/>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="shortcut icon" href="../../../images/deskera/deskera.png"/>

<title><c:out value="${model.cdomain}" /> Workspace-CRM</title>
<script type="text/javascript" src="../../../scripts/deskeracrm/validationScripts.js"/>
<script type="text/javascript">
function validateForm()
{
	if (document.getElementById("addcomment").value==null || document.getElementById("addcomment").value=="")
  	{
  		alert("Value must be filled out");
  		return false;
  	}
}



</script>

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
                <ul style="font-family:sans-serif;margin: 3px;font-size: 11px;float: right;">
                <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="getCustomerCases.do">Home</a></li>
                <li style="list-style: none;display: inline;float: left;!important;padding: 0px 4px"> | </li>
                <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="newCaseForm.do">New Case</a></li>
                <li style="list-style: none;display: inline;float: left;!important;padding: 0px 4px"> | </li>
               <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="changePasswordForm.do"> Change Password</a> </li>
                <li style="list-style: none;display: inline;float: left;!important;padding: 0px 4px"> | </li>
                <li style="list-style: none;display: inline;"><a style="float: left;" class='shortcuts' href="signout.do">Sign Out</a></li>
                </ul>
            </div>
            

<table cellspacing="0" class="x-table-layout">
	<tbody><tr><td valign="top" colspan="1" class="x-table-layout-cell">
	<div class="x-panel x-panel-noborder" style="width: 600px;">
	<div class="x-panel-bwrap">
	<div class="x-panel-body x-panel-body-noheader x-panel-body-noborder"  style="margin-bottom: 50px; width: 600px;">
	<div  style="overflow: auto; width: auto; height: auto; margin-left: 8px;">
	<c:set var="caseObj" value="${model.caseObj}"/>
	<table border="0" width="100%" style="padding-left: 5%; padding-top: 6%;">
		<tbody>
				<tr>
				<td style='color: rgb(21, 66, 139);' class='caseDetailTD'>Subject: </td>
				<td class='caseDetailTD'><c:out value="${caseObj.subject}"/></td>
				</tr>
				<tr>
				<td style='color: rgb(21, 66, 139);' class='caseDetailTD'>Description: </td>
				<td class='caseDetailTD'><c:out value="${caseObj.description}"/></td>
				</tr>	
				<tr>
				<td style='color: rgb(21, 66, 139);' class='caseDetailTD'>Case Name: </td>
				<td class='caseDetailTD'><c:out value="${caseObj.casename}"/></td>
				</tr>
				<tr>
				<td style='color: rgb(21, 66, 139);' class='caseDetailTD'>Status: </td>
				<td class='caseDetailTD' ><c:out value="${caseObj.crmCombodataByCasestatusid.value}"/></td>
				</tr>
				<tr>
				<td style='color: rgb(21, 66, 139);' class='caseDetailTD' >Assigned To: </td>
				<td class='caseDetailTD'><c:out value="${caseObj.assignedto.firstName}"/> <c:out value="${caseObj.assignedto.lastName}"/></td>
				</tr>
				<tr>
				<td style='color: rgb(21, 66, 139);' class='caseDetailTD'>Priority: </td>
				<td class='caseDetailTD' ><c:out value="${caseObj.crmCombodataByCasepriorityid.value}"/></td>
				</tr>
 		</tbody>
 	</table>
	</div>
	</div>
	</div>
	</div>
	</td>
	<td valign="top" colspan="1" class="x-table-layout-cell">
	<div class="x-panel x-panel-noborder"  style="width: 600px;">
	<div class="x-panel-bwrap" >
	<div class="x-panel-body x-panel-body-noheader x-panel-body-noborder"  style="margin-bottom: 50px; width: 600px;">
	<div  style="overflow: auto; width: auto; height: auto; margin-left: 8px;">
	<table border="0" width="100%" style="padding-left: 5%; padding-top: 6%;">
	<tbody>
		<tr>
		<td style='color: rgb(21, 66, 139);' class='caseDetailTD'>Account Name: </td>
		<td class='caseDetailTD'><c:out value="${caseObj.crmAccount.accountname}"/></td>
		</tr>
		<tr>
		<td style='color: rgb(21, 66, 139);' class='caseDetailTD'>Contact Name: </td>
		<td class='caseDetailTD'><c:out value="${caseObj.crmContact.firstname}"/> <c:out value="${caseObj.crmContact.lastname}"/></td>
		</tr>	
		<tr>
		<td style='color: rgb(21, 66, 139);' class='caseDetailTD'>Product Name: </td>
		<td class='caseDetailTD'><c:forEach items="${caseObj.crmProducts}" var="prodObj" varStatus="rowCounter"><c:if test="${rowCounter.first==false}">, </c:if><c:out value="${prodObj.productId.productname}"/></c:forEach>
		</td>
		</tr>
		<tr>
		<td style='color: rgb(21, 66, 139);' class='caseDetailTD'>Case Creation Date: </td>
		<td class='caseDetailTD' ><fmt:formatDate pattern="dd-MM-yyyy" value="${caseObj.createdon}"/></td>
		</tr>
		<tr>
		<td style='color: rgb(21, 66, 139);' class='caseDetailTD' >Case Updation Date: </td>
		<td class='caseDetailTD'><fmt:formatDate pattern="dd-MM-yyyy" value="${caseObj.updatedon}"/></td>
		</tr>
	</tbody>
	</table>
	</div>
	</div>
	</div>
	</div>
	</td>
	</tr>

 					<tr>
 						<td colspan='2' class='x-table-layout-cell'>
 						<div class='x-panel x-panel-noborder' style='width: 1200px;'>
 						<div class='x-panel-bwrap'>
 						<div class='x-panel-body x-panel-body-noheader x-panel-body-noborder' style='width: 1200px;'>
 						<div class='x-panel x-panel-noborder'>
 						<div class='x-panel-bwrap'>
 						<div class='x-panel-body x-panel-body-noheader x-panel-body-noborder'>
 						<div class='x-panel x-panel-noborder'>
 						<div class='x-panel-bwrap'>
 						<div class='x-panel-body x-panel-body-noheader x-panel-body-noborder' style='background: none repeat scroll 0% 0% rgb(255, 255, 255);'>
 						<div class='x-panel x-panel-noborder'>
 						<div class='x-panel-bwrap'>
 						<div class='x-panel-body x-panel-body-noheader x-panel-body-noborder'>
 						<hr color='#000000;' style='margin-left: 3%;'>
 						<div style='margin: 2% 2% 2% 3%;'>
 						<span class='dpTitleHead'> <img class='imgMidVA' src='../../../images/document12.gif'>  <b>Uploaded Files : </b> </span>
 							<br>
 						</div>
 						<div style='margin-left: 4%; color: rgb(0, 0, 0);'>
 						<c:choose>
 			            <c:when test="${empty model.documents}">
								<span style='height: auto; display: block; overflow: auto;'>No documents have been added. <a onclick="show_upload_win('show')" class='shortcuts' href='#'>Add a document now.</a></span>
 			            </c:when>
 			            <c:otherwise>
 			            <c:forEach items="${model.documents}" var="doc" varStatus="counter">    
 			                	<div style='height: auto; display: block; overflow: auto;'>
 			                			<span style='height: auto; display: block; overflow: auto;color: gray;'> 
 			                				<c:out value="${counter.count}"/>) <font color='blue'><a href="downloadFile.do?docid=${doc.docid}&caseid=${caseObj.caseid}"><c:out value="${doc.docname}"/></a></font>&nbsp;&nbsp;<a href="downloadFile.do?docid=${doc.docid}&caseid=${caseObj.caseid}"><img class="imgMidVA" src="../../../images/download12.gif" style='border: 0'/> </a>(size <c:out value="${doc.docsize/1000} KB"/>):Uploaded by <c:choose><c:when test="${doc.userid==null}"><c:out value="${model.customername}"/></c:when><c:otherwise><c:out value="${doc.userid.firstName}"/> <c:out value="${doc.userid.lastName}"/></c:otherwise> </c:choose>, on <fmt:formatDate value="${doc.uploadedon}"/> 
 			                	 		</span>
 			                	 		<br/>
 			               		</div>
 			               </c:forEach> 	
 			               <br/><br/><span style='height: auto; display: block; overflow: auto;'><a onclick="show_upload_win('show')" class='shortcuts' href='#'>Attach More Documents...</a></span>
 			            </c:otherwise>					
 						</c:choose>
 						</div>
 						<br/><br/>
 						<div style='margin-left: 4%; color: rgb(0, 0, 0);'>
 						<div id='uploadWin' class='uploadWin' style='margin-left: 4%; color: rgb(0, 0, 0); display:none;' align="center">
 						 						
 						<form action="uploadCustomerCaseDocs.do" enctype="multipart/form-data" method="POST" onsubmit="return validateFilesize()">
 						<input type="hidden" name="caseid" value="${caseObj.caseid}" />
 						<fieldset style='width:30' style="align:center">
 							<legend><strong>Add Files</strong></legend>
 								<table border=0>
		 							<tr>
 										<td>Select file to upload: </td>
 										<td><input type="file" name="attachment" id="attachment"/>
 										<span style='height: auto; display: block; overflow: auto;color: gray;'>(Maximum file size 10 MB)</span>
 										</td>
 									</tr>
 									<tr>
 										<td align="right"><input type="submit" value='Upload'/></td>
 										<td align="left"><input type="button" value='Cancel' onclick="show_upload_win('cancel')"/></td>
 									</tr>
 								</table>
 							</fieldset>
 							</form>
						</div>
						<c:if test="${modelup!=null&&model.up==false}">
							<p><font color="red"><span style='height: auto; display: block; overflow: auto;'>File could not be uploaded Successfully..!!</span></font></p>
						</c:if>
						</div>
 						<br><br><br>
 						<hr color='#000000;' style='margin-left: 3%;'><br>
 						<div style='margin: 3px 2% 2% 3%;'>
 						<span class='dpTitleHead'> <img class='imgMidVA' src='../../../images/comment12.gif'>  <b>Comments : </b> </span>
 						<c:if test="${empty model.comments}">
								<span style='height: auto; display: block; overflow: auto;' id='wtf-gen2609'><br>No comments have been added.</span>
 			            </c:if>
 						<br>
 						</div>
 						<div style='margin-left:  color: rgb(0, 0, 0);overflow:auto' >
 						 
 			            
 			            <c:forEach items="${model.comments}" var="comment"> 
 						
 						 
 						
 						      		            			 
                 		        	  				<div  style='margin-left: 1%; float: left; padding-left: 30px;'>
                 		        	 				<span style='color: rgb(0, 0, 0);'></span>
                 		        	 				<span  style='color: gray ! important;'><c:choose><c:when test="${comment.userflag=='1'}"><c:out value="${model.customername}"/></c:when><c:otherwise><c:out value="${caseObj.usersByUserid.firstName}"/> <c:out value="${caseObj.usersByUserid.lastName}"/></c:otherwise> </c:choose> on   <fmt:formatDate  type="date" dateStyle="long" value="${comment.postedOn}"/></span>
                 		        	 				${comment.comment}
                 		        					</div>
                 		        					<br>
                 		        					<br>
                 		        	 			
 						
 						 </div>
 						 </c:forEach>
 						 <br><br><br>
 						 <hr color='#000000;' style='margin-left: 3%;'><br><div style='margin: 3px 2% 2% 3%;'>
 						 
 					   </div>
 					   <div style='margin: 2% 2% 2% 3%;'>
 						<span class='dpTitleHead'><b>New Comments :</b></span>
 							<br>
 						</div>
 						<form method="POST" name='addcommentform' action="addComment.do" onsubmit="return validateForm()">
 						
 							<div style='margin-left: 11%; color: rgb(0, 0, 0);'>
 						  	<span style='height: auto; display: block; overflow: auto;'>
 						 	<textarea rows="10" cols="130" style="resize:none;" name="addcomment" id="addcomment"></textarea>
 						 	<input type='hidden' name='caseid' value='${caseObj.caseid}'>
 						 	</span>
 						 	</div>
 						 	<br>
 						  	<div style='margin-left: 11%; color: rgb(0, 0, 0);'>
 						   	<input  type="submit" value="Save" name="save" onClick=" return commentHTMLCheck()">
 						   	<input  type="reset" value="Clear" name="clear">
 						 	</div>
 						  	
 						</form>

 					  </div>
 					  </div>
 					  </div>
 					  </div>
 					   </div>
 					   </div>
 					  </div>
 					</div>
 				</div>
 			</div>
 		</div>
 		</div>
 		</td>
 		</tr>
 		
</tbody>
</table>
</c:otherwise>					
</c:choose>
</body>
</html>
