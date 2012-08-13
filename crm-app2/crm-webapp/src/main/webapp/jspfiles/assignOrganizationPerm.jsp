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
            String fromDb = "stagingcrm130911";//request.getParameter("fromdb");
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.204:3306/" + fromDb + "?user=root&password=krawler");
            conn.setAutoCommit(false);
            String flag = request.getParameter("flag");
            if(flag != null && flag.equals("1")) {
                String feature = "ff808081222afa0701222b7f91d3000b";//Custom Reports
                PreparedStatement pstmt = conn.prepareStatement("select distinct userId, roleId, id from role_user_mapping where roleId in ('"+Constants.COMPANY_ADMIN+"') ");
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String userid = rs.getString("userId");
                    String roleid = rs.getString("roleId");
                    String roleuserid = rs.getString("id");
                    
                    PreparedStatement pstmt1 = conn.prepareStatement("select permissioncode from userpermission where feature = '"+feature+"' and roleId = '"+roleuserid+"'");
                    ResultSet rs1 = pstmt1.executeQuery();
                    while (rs1.next()) {
                        int permissioncode = 0;
                        int newpermCode = 0;
                        permissioncode = rs1.getInt("permissioncode");
                        if ((permissioncode & 1) == 1) {//View Product 
                            newpermCode += 1;
                        }
                        if ((permissioncode & 2) == 2) {//Manage Product
                            newpermCode += 2;
                        }
                        if ((permissioncode & 4) == 4) {//Delete Product
                            newpermCode += 4;
                        }
                        if ((permissioncode & 8) == 8) {//Archive Product
                            newpermCode += 8;
                        }
                        if ((permissioncode & 16) == 16) {//Export Product
                            newpermCode += 16;
                        }
                        if ((permissioncode & 32) == 32) {//Import From Accounting
                            newpermCode += 64;
                        }
                        if ((permissioncode & 64) == 64) {//View All
                            newpermCode += 128;
                        }
                        newpermCode += 32;
                        String query = "update "+fromDb+".userpermission set permissioncode = "+newpermCode+" where feature= '"+feature+"' and  roleId= '"+roleuserid+"'";
                        PreparedStatement pstmt2 = conn.prepareStatement(query);
                        pstmt2.executeUpdate();
                        pstmt2.close();
                    }
                    rs1.close();
                    pstmt1.close();
                }
                System.out.println("Finished");
                rs.close();            
                conn.commit();
                pstmt.close();
                
            } else {
                int permissioncode = 7;
                String feature = "BC787CF6-AEC6-11E0-BB0E-41414824019B";//Custom Reports
                PreparedStatement pstmt = conn.prepareStatement("select distinct userId, roleId, id from role_user_mapping where roleId in ('"+Constants.COMPANY_ADMIN+"') ");
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String userid = rs.getString("userId");
                    String roleid = rs.getString("roleId");
                    String roleuserid = rs.getString("id");
                    
                    //if(roleid.equals(Constants.COMPANY_ADMIN) || roleid.equals(Constants.COMPANY_SALES_MANAGER)) {
                    try{
                        String query = "insert into "+fromDb+".userpermission (feature, roleId, permissioncode) values ('"+feature+"', '"+roleuserid+"', "+permissioncode+")";
                        PreparedStatement pstmt1 = conn.prepareStatement(query);
                        pstmt1.executeUpdate();
                        pstmt1.close();
                    }catch(Exception e){
                        String query = "update "+fromDb+".userpermission set permissioncode = "+permissioncode+" where feature= '"+feature+"' and  roleId= '"+roleuserid+"'";
                        PreparedStatement pstmt1 = conn.prepareStatement(query);
                        pstmt1.executeUpdate();
                        pstmt1.close();
                    }
                        //System.out.println(query);
                    //}
                }
                System.out.println("Finished");
                rs.close();            
                conn.commit();
                pstmt.close();
           }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            conn.close();
        }
%>
