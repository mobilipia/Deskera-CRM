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
<%@ page import="com.krawler.utils.json.base.*" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>
<%@page import="com.krawler.common.util.*" %>
<%@page import="com.krawler.spring.common.KwlReturnObject" %>
<%@page import="com.krawler.common.admin.*" %>
<%@page import="com.krawler.esp.hibernate.impl.*" %>
<%@page import="org.hibernate.*" %>

<%
        Connection conn =null;
        boolean success = true;
        try {
            ResultSet rs = null,rs1=null;
            PreparedStatement pstmt = null,pstmt7 = null;
            String query = "";
            String toDb = request.getParameter("todb");//stagingcrm25
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.111:3306/" + toDb + "?user=root&password=krawler");
            conn.setAutoCommit(false);


                query =" select companyid,f.id,f.fieldname,dh.maxlength from " + toDb + ".fieldParams f " +
                        " inner join " + toDb + ".default_header dh on dh.pojoheaderName=f.id and f.maxlength=12 and f.maxlength!=dh.maxlength and f.fieldtype=1 ";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    String companyid = rs.getString("companyid");
                    String fieldid = rs.getString("id");
                    String fieldname = rs.getString("fieldname");
                    Integer maxlength = Integer.parseInt(rs.getObject("maxlength").toString());
                    query = " update " + toDb + ".fieldParams set maxlength = ? where id=? and companyid=? ";
                    pstmt7 = conn.prepareStatement(query);
                    pstmt7.setInt(1,maxlength);
                    pstmt7.setString(2,fieldid);
                    pstmt7.setString(3,companyid);
                    int a1 = pstmt7.executeUpdate();
                    out.println("maxlength of " + fieldname+" with " + fieldid+"  updated to "+maxlength);
                }


            //output.close();
            rs.close();
            if(success){
                out.println("Migration completed successfully");
                conn.commit();
            }

            conn.close();

        } catch (Exception ex) {
            out.println("Migration not completed successfully");
            out.println(ex.getMessage());
            conn.rollback();

        }
%>
