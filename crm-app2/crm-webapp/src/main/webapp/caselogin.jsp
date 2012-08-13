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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page import="com.krawler.common.util.URLUtil"%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
        <meta name="robots" content="noindex,nofollow" />
       <title>Login-Deskera CRM</title>
        <link rel="shortcut icon" href="../../images/deskera/deskera.png"/>
        <!--link rel="stylesheet" type="text/css" href="../../style/allYourBases_1.css"/-->
        <script type="text/javascript" src="../../lib/jquery-1.2.2.min.js"></script>
        <script type="text/javascript" src="../../scripts/core/42.js"></script>
        <script type="text/javascript" src="../../scripts/belongToUs.js?v=4"></script>
    </head>
    <style type="text/css">
        body {
            font-family: Tahoma,Verdana,Arial,Helvetica,sans-sarif;
            font-size: 12px;
        }
        .content {
            width: 240px;
            text-align: left;
            margin: auto;
            padding: 10px;
            border: 5px solid #ccc;
            background-color: white;
        }
        .errorprompt {
            font-weight: bold;
            color: red;
            visibility: hidden;
        }
        .labels {
            display: block;
            float: left;
            width: 65px;
        }

        .inputbox {
            display: block;
            float: left;
            width: 155px;
        }
        .content h1 {
            font-size: 1em;
            font-weight: bold;
            margin: 0 0 10px 0;
        }
        .content span {
            font-size: 0.8em;
        }

        .content input {
            font-size: 14px;
        }
        .content p {
            margin: 7px 0 13px;
        }
        #content {
            width: 100%;
            position: absolute;
            left: 0%;
            top: 15%;
            color: black;
        }
        #LoginButton {
            width: 63px;
        }
        #usrFeedback {
            margin-left: 12px;
        }
        .loadingFB {
            color: black;
            background: url('../images/loading.gif') no-repeat top left;
            padding-left: 16px;
        }
        .errorFB {
            color: red;
            background: none;
            padding-left: 0;
        }
        .companylogo {
            margin: 0 0 10px;
        }

    </style>
    <!---->
    <body onload="checkCookie(); formFocus();">
        <div id="content" align="center">
            <div>
               <%
               	String companyname=URLUtil.getDomainName(request);
			   	String imgurl="http://apps.deskera.com"+"/b/"+companyname+ "/images/store/?company=true&original=true";
			   %>
			   <img id="companyLogo" src=<%=imgurl%>/>
			</div>
			<br/>
            <div class="content">
                <form action="javascript:checkEmpty(true);" name="loginForm" id="loginForm">
                    <p>
                        <label for="UserName" id="UserNameLabel" class="labels">
                            Username:
                        </label>
                        <input name="UserName" type="text" id="UserName" class="inputbox"/><span id="UserNameRequired" class="errorprompt" title="Username is required.">*</span>
                    </p>
                    <p>
                        <label for="Password" id="PasswordLabel" class="labels">
                            Password:
                        </label>
                        <input name="Password" type="password" id="Password" class="inputbox"/><span id="PasswordRequired" class="errorprompt" title="Password is required.">*</span>
                    </p>
                    <p>
                        <input type="submit" name="LoginButton" value="Login" id="LoginButton"/><span id="usrFeedback"></span>
                    </p>
                </form>
                <div class="shortcuts">
                </div>
            </div>
        </div>
    </body>
    <script type="text/javascript">//doDirectLogin();
        function callSignupForm_form(){
            window.location="signupmain.html";
        }
        function gup( name )
        {
            name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
            var regexS = "[\\?&]"+name+"=([^&#]*)";
            var regex = new RegExp( regexS );
            var results = regex.exec( window.location.href );
            if( results == null )
                return "";
            else
                return results[1];
        }
        var demo=gup('demo');
        if(demo=="true"){
            document.forms['loginForm'].elements['UserName'].value="demo";
            document.forms['loginForm'].elements['Password'].value="demo";
            document.getElementById("deskeraimageq").src = "../../images/deskera/k-crm-big.gif";
            javascript:lValidateEmpty();
        }
    </script>
    <!---->
</html>
