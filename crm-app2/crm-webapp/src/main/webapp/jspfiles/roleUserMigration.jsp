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
            PreparedStatement pstmt = null;
            String query = "";
            String roleid = "";
            String app = request.getParameter("app");
            String frmDb = request.getParameter("fromdb");
            String toDb = request.getParameter("todb");
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.0.111:3306/" + toDb + "?user=root&password=krawler");

            //File file = new File("/home/trainee/roleusermap.txt");
            //Writer output = null;
            //output = new BufferedWriter(new FileWriter(file));

            pstmt = conn.prepareStatement("set foreign_key_checks = 0");
            int a1 = pstmt.executeUpdate();

            pstmt = conn.prepareStatement("select userid,roleid from " + toDb + ".users");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                try {
                    String uuid = java.util.UUID.randomUUID().toString();
                    query = "insert into " + toDb + ".role_user_mapping (id,userId,roleId)values(" +
                            "'" + uuid + "'" + "," +
                            fun1(rs, "userId") + "," +
                            fun1(rs, "roleId");
                    query = query + ");\n";
                    PreparedStatement pstmt1 = conn.prepareStatement(query);
                    int a = pstmt1.executeUpdate();
                    pstmt1.close();
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
            }
            rs.close();
            if (app.equals("crm")) {
                pstmt.close();
                pstmt = conn.prepareStatement("select feature,userlogin,permissioncode from " + frmDb + ".userpermission");
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    String userid = rs.getObject("userlogin").toString();
                    try {
                        PreparedStatement pstmt1 = conn.prepareStatement("select id from " + toDb + ".role_user_mapping where userId='" + userid + "'");
                        ResultSet rs2 = pstmt1.executeQuery();
                        if (rs2.next()) {
                            roleid = rs2.getObject("id").toString();
                        }
                        pstmt1.close();
                        query = " insert into " + toDb + ".userpermission(feature,roleId,permissioncode) values(" +
                                fun1(rs, "feature") + "," +
                                "'" + roleid + "'" + "," +
                                fun1(rs, "permissioncode");
                        query = query + ");\n";
                        PreparedStatement pstmt2 = conn.prepareStatement(query);
                        int a = pstmt2.executeUpdate();
                        pstmt2.close();
                        rs2.close();
                    } catch (Exception e) {
                        out.println(e.getMessage());
                    }
                }
                pstmt.close();
                rs.close();
                

                // company specific "Lead Tpye" master entry on masterdata table
                
                pstmt = conn.prepareStatement("select companyid,subdomain from " + toDb + ".company");
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    try {
                        String companyid = rs.getObject("companyid").toString();
                        String uid = java.util.UUID.randomUUID().toString();
                        query = "insert into defaultmasteritem(id,value,crmCombomasterId,crmCombodataId,companyid,mainID,isEdit,percentStage,validflag) values('"+uid+"','Company','b6ad01f9-0f3f-457d-8adb-f3a3d1969b68','1','"+companyid+"','1',1,'',1)";
                        PreparedStatement pstmt1 = conn.prepareStatement(query);
                        int a = pstmt1.executeUpdate();
                        pstmt1.close();
                        uid = java.util.UUID.randomUUID().toString();
                        query = "insert into defaultmasteritem(id,value,crmCombomasterId,crmCombodataId,companyid,mainID,isEdit,percentStage,validflag) values('"+uid+"','Individual','b6ad01f9-0f3f-457d-8adb-f3a3d1969b68','0','"+companyid+"','0',1,'',1)";
                        PreparedStatement pstmt2 = conn.prepareStatement(query);
                        a = pstmt2.executeUpdate();
                        pstmt2.close();
                    } catch (Exception e) {
                        out.println(e.getMessage());
                    }
                }
            }
            //output.close();
            rs.close();
            conn.close();

        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
%>
