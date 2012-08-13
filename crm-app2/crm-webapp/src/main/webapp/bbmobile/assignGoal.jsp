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
<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
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
<%@page import="com.krawler.esp.web.resource.Links"%>
<%@page import="com.krawler.spring.sessionHandler.sessionHandlerImpl" %>
<%
        String domainurl = URLUtil.getDomainName(request);
        domainurl = URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull);
        String dbName = ConfigReader.getinstance().get("dbname");
        String dbSeverIP = ConfigReader.getinstance().get("dbserverip");
        String dbUser = ConfigReader.getinstance().get("mysql_user");
        String dbPass = ConfigReader.getinstance().get("mysql_passwd");
        String connectionURL = "jdbc:mysql://"+dbSeverIP+":3306/"+dbName+"?user="+dbUser+"&password="+dbPass;
        String submitURL = domainurl+"crm/common/HRMSIntegration/insertGoalBlackBerry.do";
        String mobilePagesPath = "bbmobile/welcomepage.jsp";
        mobilePagesPath = domainurl + mobilePagesPath;
//        String signoutURL = domainurl + "jspfiles/signOut.jsp?type=signout";

        String userid = sessionHandlerImpl.getUserid(request);
        String companyid = sessionHandlerImpl.getCompanyid(request);
        StringBuffer userList = sessionHandlerImpl.getRecursiveUsersList(request);
        String res = "";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String defaultDateVal = sdf.format(new java.util.Date());
        String startStr = "<div class=\"form_wrapper\">"+
                        "<h4>Assign Goals</h4>"+
                        "   <form method=\"post\" action='"+submitURL+"'>"+
                        "     <table style='font-size:13px'>";
        String userComboStr = "";
        String goalType = "";
//        String logoutText = "<tr><td colspan=2><div style='font-size:11px; float:right;'><a href=\""+signoutURL+"\"'>Signout</a></div></td></tr>";
        String target = "<tr><td><label for='target'>Target *</label></td><td><input type='text' name='target' maxlength='15' rel='numberfield'/></td></tr>";
        String fmDate = "<tr><td><label for='fromdate'>From Date *</label></td><td><input type='text' name='fromdate' maxlength='10' rel='datefield' value='"+defaultDateVal+"'/></td></tr>"+
                        "<tr><td>&nbsp;</td><td><span style='font-size:11px'>(yyyy-mm-dd )</span></td></tr>";
        String toDate = "<tr><td><label for='todate'>To Date *</label></td><td><input type='text' name='todate' maxlength='10' rel='datefield' value='"+defaultDateVal+"'/></td></tr>"+
                        "<tr><td>&nbsp;</td><td><span style='font-size:11px'>(yyyy-mm-dd )</span></td></tr>";
        String goalCreationDate = "<tr><td><label for='creationdate'>Goal Creation Date *</label></td><td><input type='text' name='creationdate' maxlength='10' rel='datefield' value='"+defaultDateVal+"'/></td></tr>"+
                                  "<tr><td>&nbsp;</td><td><span style='font-size:11px'>(yyyy-mm-dd )</span></td></tr>";
        String hiddenFields = "<tr><td><input type='hidden' name='userid' value='"+userid+"'/>"+
                              "<input type='hidden' name='companyid' value='"+companyid+"'/>"+
                              "<input type='hidden' name='returnurl' value=''/></td></tr>";
        String endStr = "<tr><td>&nbsp;</td><td><input type='submit' name='submit' value='Submit' style='width:80px;'/>" +
                        "&nbsp;<input type='button' onclick='location.href=\""+mobilePagesPath+"\"' value='Cancel' style='width:80px;'/></td></tr>" +
                        "</table>"+
                        "</form>"+
                        "</div>";
        Connection conn = null;
        try {
            ResultSet rs = null;
            PreparedStatement pstmt = null;
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(connectionURL);            
            pstmt = conn.prepareStatement("select userid, concat(fname, ' ', lname) as name from users where company = ? and deleteflag = ? and userid in ("+userList+") ");
            pstmt.setString(1, companyid);
            pstmt.setInt(2, 0);
            rs = pstmt.executeQuery();
            userComboStr = "<tr><td><label for='employee'>Employee Name *</label></td><td><select name='employee'>";
            while (rs.next()) {
                userComboStr += "<option value='"+rs.getObject("userid")+"'>"+rs.getObject("name")+"</option>";
            }
            userComboStr += "</select></td></tr>";
            rs.close();

            goalType = "<tr><td><label for='goaltype'>Goal Type *</label></td><td><select name='goaltype'>";
            goalType += "<option value='1'>No of Leads</option>";
            goalType += "<option value='2'>Total revenue from closed leads</option>";
            goalType += "<option value='3'>No of Accounts</option>";
            goalType += "<option value='4'>Total revenue from accounts</option>";
            goalType += "<option value='5'>No of Opportunities</option>";
            goalType += "<option value='6'>Total sales amount from opportunities</option>";
            goalType += "</select></td></tr>";

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally{
            if(conn != null) {
                conn.close();
            }
        }
        res = startStr + userComboStr + goalType + target + fmDate + toDate + goalCreationDate + hiddenFields + endStr;
%>
<%=res%>
