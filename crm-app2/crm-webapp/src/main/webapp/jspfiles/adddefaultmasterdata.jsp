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

<%
        try {
            ResultSet rs = null;
            ResultSet rs2 = null;
            PreparedStatement pstmt = null;
            String roleid = "";
            String query = "";
            String query1 = "";
            String dbName = "crmutf8";
            String userName = "root";
            String password = "krawler";
            String url = "jdbc:mysql://192.168.0.12:3306/" + dbName + "?useUnicode=true&amp;characterEncoding=UTF-8";
			String crmcomboMasterid= "500d6f95-a6ab-4f5f-bb26-2ef6161760a1";
			String crmcomboDataid = "e436d5c7-e369-4b12-b4b8-deeb393d1234";
			String mainid=crmcomboDataid;
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, userName, password);
            if(conn!=null)
            	out.println("Connection is OK...!");
            int a=0,count=0;
  				String uuid="";
                pstmt = conn.prepareStatement("select distinct companyid from " + dbName + ".company ");
                rs = pstmt.executeQuery();
				if(rs!=null)
					out.println("Company ids are retrived");
                String compid="";
                while (rs.next()) {
                    try {
                    	compid = rs.getString("companyid");
                    	uuid= java.util.UUID.randomUUID().toString();
                        query = "insert into " + dbName + ".defaultmasteritem(id,value,crmCombomasterId,crmCombodataId,companyid,mainID,aliasName,isEdit,percentStage,validflag,deleteflag) values ('" + uuid + "', 'Closed' ,'" + crmcomboMasterid + "', '" + crmcomboDataid + "', '"+ compid +"', '"+ mainid +"', '', 1,"+ null +", 1,0)";
                            
                        pstmt = conn.prepareStatement(query);
                         a = pstmt.executeUpdate();
                         if(a!=0)
                         	count++;
                         else
                        	 count--;
						
                     } catch (Exception e) {
                        out.println(e.getMessage());
                    }
                     
                }
                out.println("Query executed successfully for "+ count +" Companies");
				
            
            //output.close();
            rs.close();
            conn.close();
            
        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
%>
