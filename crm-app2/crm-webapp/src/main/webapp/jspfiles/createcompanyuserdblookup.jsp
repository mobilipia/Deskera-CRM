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
<%@ page import="com.krawler.common.util.StringUtil" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>
<%
        Connection conn = null;
        try {
            String sourceDb = request.getParameter("sourcedb");
            String targetDb = request.getParameter("targetdb");
            String dbuser = "root";
            String dbpass = "krawler";
            /*                  */
            ResultSet rs = null;
            PreparedStatement pstmt = null;
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + sourceDb + "?user="+dbuser+"&password="+dbpass);
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement("set foreign_key_checks = 0");
            int a1 = pstmt.executeUpdate();
            int count=0;

            pstmt = conn.prepareStatement("select userid,company,subdomain from users u inner join company c on" +
                    " u.company = c.companyid order by subdomain");
            rs = pstmt.executeQuery();
            while (rs.next()) {

                    String companyid = rs.getString("company");
                    String userid = rs.getString("userid");
                    String subdomain = rs.getString("subdomain");
                    pstmt = conn.prepareStatement("insert into "+targetDb+".companyuserdbmap values (uuid(),'"+sourceDb+"','"+subdomain+"','"+userid+"','"+companyid+"')");
                    pstmt.executeUpdate();
                    out.println("Company:"+companyid+" User:"+userid+" Subdomain:"+subdomain);
                    count++;
                    
            }
            rs.close();
            conn.commit();
            pstmt.close();
        } catch (Exception ex) {
            out.println(ex.getMessage());
        } finally {
            conn.close();
        }
%>




