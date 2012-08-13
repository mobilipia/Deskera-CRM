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
<%@ page import="com.krawler.crm.utils.Constants"%>
<%
        Connection conn = null;
        try {
            ResultSet rs = null;
            String fromDb = "crmstaging";//request.getParameter("fromdb");
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.12:3306/" + fromDb + "?user=root&password=krawler");
            conn.setAutoCommit(false);

            int id = 0;
            PreparedStatement pstmt = conn.prepareStatement("select max(id) as id from userrolemapping");
            rs = pstmt.executeQuery();
            if (rs.next()) {
                id = rs.getInt("id");
            }
            id++;

            pstmt = conn.prepareStatement("select distinct userId, roleId from role_user_mapping ");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String userid = rs.getString("userId");
                String roleid = rs.getString("roleId");
                if(roleid.equals(Constants.COMPANY_ADMIN)) {
                    roleid = "1";
                }else if(roleid.equals(Constants.COMPANY_SALES_MANAGER)) {
                    roleid = "2";
                }else if(roleid.equals(Constants.COMPANY_SALES_EXECUTIVE)) {
                    roleid = "3";
                }

                String query = "insert into "+fromDb+".userrolemapping (id, roleid, userid) values ('"+id+"', '"+roleid+"', '"+userid+"')";
                PreparedStatement pstmt1 = conn.prepareStatement(query);
                pstmt1.executeUpdate();
                pstmt1.close();
                id++;
                
                System.out.println(query);
            }

            rs.close();
            conn.commit();
            pstmt.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
%>




