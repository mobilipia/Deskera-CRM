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
<%@ page import="com.krawler.common.session.SessionExpiredException"%>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.spring.sessionHandler.sessionHandlerImpl" />
<%

        String message = "";
        if (sessionbean.validateSession(request, response)) {
            try {
                int flag = Integer.parseInt(request.getParameter("flag"));

                switch (flag) {
                    case 1:                                 ///////get lead grid
                      //  message = crmDbcon.getCompany(request);
                        break;




                    default:
                        break;
                }

            } catch (Exception e) {

            } finally {
            }
        } else {
            sessionbean.destroyUserSession(request, response);

        }
     //   out.println(message);
%>




<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <meta name="robots" content="noindex,nofollow" />
        <title>Application Usage - Deskera</title>
        <link rel="shortcut icon" href="images/deskera.png"/>
    </head>
    <body>
        <%=message%>
    </body>
</html>
