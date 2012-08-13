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
<%@ page language="java" contentType="text/html" %>
<%@ page pageEncoding="UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="javax.xml.parsers.*"%>
<%@ page import="org.xml.sax.SAXException"%>
<%@ page import="javax.xml.transform.*"%>
<%@ page import="javax.xml.transform.stream.*"%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<style>body{font-family: Tahoma,sans-serif; font-size: 12px;}.csep{margin: 20px;}.ctitle{font-weight: bold; font-size: 12px;} .ctitle a, .slLinkWrapper a{color:#083772; text-decoration:none;}.ctitle a:hover, .slLinkWrapper a:hover{color:#445566; text-decoration:underline;}</style>
	</head>
	<body>
<%
	String FS = System.getProperty("file.separator");	
	String ctx = getServletContext().getRealPath("") + FS;
	String xslFile = ctx + "transformYahooWeather.xsl";
	TransformerFactory tFactory = TransformerFactory.newInstance();
    Transformer transformer = tFactory.newTransformer(new StreamSource(xslFile));
    Writer outWriter = new StringWriter();
    StreamResult result1 = new StreamResult();
    result1.setWriter(outWriter);
    String xmlPath = "http://weather.yahooapis.com/forecastrss?p=SNXX0006&u=c";
    transformer.transform(new StreamSource(xmlPath), result1);
    out.print(outWriter);
%>                        
	</body>
</html>
