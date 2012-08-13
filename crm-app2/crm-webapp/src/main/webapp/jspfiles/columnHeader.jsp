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
<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@page language="java"%>
<%@ page import="com.krawler.common.session.SessionExpiredException"%>
<%@ page import="com.krawler.common.util.StringUtil" %>
<%@ page import="com.krawler.utils.json.base.JSONObject" %>
<%@ page import="com.krawler.utils.json.base.JSONArray" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>
<jsp:useBean id="sessionbean" scope="session" class="com.krawler.spring.sessionHandler.sessionHandlerImpl" />

<%
        try {
            ResultSet rs = null;
            PreparedStatement pstmt = null;
            String query = "";
            String dbName = "crmutf8";
            String userName = "root";
            String password = "krawler";
            String url = "jdbc:mysql://192.168.0.111:3306/" + dbName;

            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = (Connection) DriverManager.getConnection(url, userName, password);
            int count = 0;
            pstmt = (PreparedStatement) conn.prepareStatement("select docid ,userid from " + dbName + ".crm_docs ");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {

                try {
                    String docid = rs.getObject("docid").toString();
                    String userid = rs.getObject("userid").toString();
                    String randomid = java.util.UUID.randomUUID().toString();
                    if(!StringUtil.isNullOrEmpty(userid)){
                        query = " insert  into " + dbName + ".crm_docOwners values ('" + randomid + "' ,'" +userid+ "','" +docid+ "' ,'T');";
                        pstmt = (PreparedStatement) conn.prepareStatement(query);
                        int a = pstmt.executeUpdate();
                        count++;
                    }
                    
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
            rs.close();
            conn.close();
            System.out.println(count);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

%>
