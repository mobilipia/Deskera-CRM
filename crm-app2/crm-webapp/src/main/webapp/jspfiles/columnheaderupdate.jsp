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

<%!
    public static String fun1(ResultSet rs, String fieldName) {
        String str = null;
        try {
            str = (rs.getObject(fieldName) != null ? "'" + rs.getObject(fieldName) + "'" : rs.getObject(fieldName)).toString();
        } catch (Exception e) {
            str = null;
            return str;
        }
        return str;
    }
%>

<%
        try {
            ResultSet rs = null;
            ResultSet rs2 = null;
            PreparedStatement pstmt = null;
            String roleid = "";
            String query = "";
            String query1 = "";
            String dbName = "crmsom";
            String userName = "root";
            String password = "krawler";
            String url = "jdbc:mysql://192.168.0.111:3306/" + dbName;

            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, userName, password);
            int count = 0;
  
                pstmt = conn.prepareStatement("select * from " + dbName + ".role_user_mapping ");
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    try {
                        String pk = rs.getObject("id").toString();
                        String uuid = java.util.UUID.randomUUID().toString();
                        query = "insert into " + dbName + ".userpermission(feature,roleid,permissioncode) values (" +
                            "'ff808081257c3abf01257c3bb5da7772'" + "," +
                            "'" + pk + "'" + "," +

                            "'7'";

                        query = query + ");";

                        pstmt = conn.prepareStatement(query);
                        int a = pstmt.executeUpdate();

                        query1 = "insert into " + dbName + ".userpermission(feature,roleid,permissioncode) values (" +
                            "'ff808081257c3abf01257c3bb5da7773'" + "," +
                            "'" + pk + "'" + "," +

                            "'1'";

                        query1 = query1 + ");";

                        pstmt = conn.prepareStatement(query1);
                        int a1 = pstmt.executeUpdate();
                        count++;
                    } catch (Exception e) {
                        out.println(e.getMessage());
                    }
                }
            
            //output.close();
            rs.close();
            conn.close();
            out.println(count+"Records executed Successfully");
        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
%>
