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

<%@ page contentType="text/html"  pageEncoding="UTF-8" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.krawler.common.util.URLUtil" %>
<%@page import="com.krawler.esp.web.resource.Links"%>
<%
       HashMap model = (HashMap)request.getAttribute("model");
       if(model.get("successFlag").toString().equals("1") && model.get("redirectFlag").toString().equals("1")){
           response.sendRedirect(model.get("returnurl").toString());
       } else {
           String mobilePagesPath = "bbmobile/welcomepage.jsp";
           String domain = URLUtil.getDomainName(request);
           mobilePagesPath = URLUtil.getRequestPageURL(request, Links.loginpageFull) + mobilePagesPath;

%>
<html>
<head>
<style>
    body {
        background-color:#EEEEEE;
        font-family:"trebuchet MS",tahoma,verdana,arial,helvetica,sans-serif;
        margin:0;
    }
    .content {
        background-color:white;
        border:10px solid #CCCCCC;
        margin:auto;
        padding:20px;
        text-align:left;
        width:200px;

    }
    #content {
        color:black;
        left:0;
        position:absolute;
        top:25%;
        width:100%;
    }
    #wrapper {
        margin:0;
        padding:0;
        text-align:center;
    }

</style>
</head>
<body>
    <div id="wrapper">
        <div id="content">
            <div class="content">                
                <div id="msg" class="success">
                    <p>
                        <%=model.get("successMsg")%>
                    </p>
                </div>
                <center><div>Click <a href=<%=mobilePagesPath %>>here</a> to go back</div></center>
            </div>
        </div>
    </div>
</body>
</html>
<%}%>
