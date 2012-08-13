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
            PreparedStatement pstmt = null,pstmt1 = null,pstmt2 = null,pstmt3 = null,pstmt4 = null;
            String query = "";
            String toDb = request.getParameter("todb");//stagingcrm25
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.111:3306/" + toDb + "?user=root&password=krawler");
            conn.setAutoCommit(false);

                // Update Script to insert valid entry for defaultHeader column of column_header table

                pstmt = conn.prepareStatement("select companyid,companyname from " + toDb + ".company ");
                rs = pstmt.executeQuery();
             //   fieldManagerDAO fieldManagerDAOobj = new fieldManagerDAOImpl();
                
                while (rs.next()) {
                    String companyid = rs.getString("companyid");
                    String companyname = rs.getString("companyname");
                   // companyid="9b09f6b3-9d4a-4552-878a-b25026f6d8a6";
                    pstmt1 = conn.prepareStatement("select count(d.id) as count from " + toDb + ".defaultmasteritem d  "
                            + " where d.crmCombomasterId=?  " +
                            " and d.companyid=? "+
                            " and d.crmCombomasterId in ( select cc.masterid from  " + toDb + ".crm_combomaster cc "
                            + " where cc.comboname=? ) " +
                            " and d.mainID in ( select cca.campaignid from  " + toDb + ".crm_campaign cca )  ");
                    pstmt1.setString(1,"3b1e9726-12ea-4adf-a6ef-f0950075fec4");
                    pstmt1.setString(2, companyid);
                    pstmt1.setString(3, "Campaign Source");
                    rs1 = pstmt1.executeQuery();
                    while(rs1!=null && rs1.next()){
                        out.println("Total records count is "+rs1.getInt("count") + " of "+companyname);
                    }

                    pstmt2 = conn.prepareStatement("select count(d.id) as count from " + toDb + ".defaultmasteritem d  "
                            + " where d.crmCombomasterId=? and " +
                            " d.companyid=? "+
                            " and d.crmCombomasterId in ( select cc.masterid from  " + toDb + ".crm_combomaster cc"
                            + " where cc.comboname=? ) " +
                            " and d.mainID in ( select cca.campaignid from  " + toDb + ".crm_campaign cca "
                            + " where  cca.companyid!=?  ) ");
                    pstmt2.setString(1,"3b1e9726-12ea-4adf-a6ef-f0950075fec4");
                    pstmt2.setString(2, companyid);
                    pstmt2.setString(3, "Campaign Source");
                    pstmt2.setString(4, companyid);
                    rs1 = pstmt2.executeQuery();
                    while(rs1!=null && rs1.next()){
                        out.println("Total invalid record count is "+rs1.getInt("count") + " of "+companyname);
                    }

                    pstmt3 = conn.prepareStatement("select count(d.id) as count from " + toDb + ".defaultmasteritem d"
                            + "  where d.crmCombomasterId=? and " +
                            " d.companyid=? "+
                            " and d.crmCombomasterId in ( select cc.masterid from  " + toDb + ".crm_combomaster cc "
                            + "where cc.comboname=? ) " +
                            " and d.mainID in ( select cca.campaignid from  " + toDb + ".crm_campaign cca "
                            + " where  cca.companyid=?  ) ");
                    pstmt3.setString(1,"3b1e9726-12ea-4adf-a6ef-f0950075fec4");
                    pstmt3.setString(2, companyid);
                    pstmt3.setString(3, "Campaign Source");
                    pstmt3.setString(4, companyid);
                    rs1 = pstmt3.executeQuery();
                    while(rs1!=null && rs1.next()){
                        out.println("Total valid record count is "+rs1.getInt("count") + " of "+companyname);
                    }

                    pstmt4 = conn.prepareStatement("update " + toDb + ".defaultmasteritem d "
                            + " set d.deleteflag=1"
                            + " where d.crmCombomasterId=? and " +
                            " d.companyid=? "+
                            " and d.crmCombomasterId in ( select cc.masterid from  " + toDb + ".crm_combomaster cc"
                            + " where cc.comboname=? ) " +
                            " and d.mainID in ( select cca.campaignid from  " + toDb + ".crm_campaign cca"
                            + " where  cca.companyid!=?  ) ");
                    pstmt4.setString(1,"3b1e9726-12ea-4adf-a6ef-f0950075fec4");
                    pstmt4.setString(2, companyid);
                    pstmt4.setString(3, "Campaign Source");
                    pstmt4.setString(4, companyid);
                    int count = pstmt4.executeUpdate();

                    out.println("updated "+count + " invalid records of "+companyname);
                    rs1.close();
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
