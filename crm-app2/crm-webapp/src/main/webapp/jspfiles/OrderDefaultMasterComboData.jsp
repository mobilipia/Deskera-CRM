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
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>

<%
        try {
            ResultSet rscombo = null;
            ResultSet rsid = null;
            ResultSet rscompany=null;
            Statement stmt=null;
            Statement stmt1=null;
            Statement stmt2=null;
            Statement stmt3=null;
            
            String dbName = "crmutf8";
            String userName = "root";
            String password = "krawler";
            String url = "jdbc:mysql://192.168.0.107:3306/" + dbName + "?useUnicode=true&amp;characterEncoding=UTF-8";
			
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, userName, password);
            if(conn!=null)
            	out.println("<BR/>Connection is OK...!");
            stmt=conn.createStatement();
            rscombo=stmt.executeQuery("SELECT masterid FROM crm_combomaster");
            if(rscombo!=null)
            	out.println("Combos are retrived<BR/>");
            String company="";
            String id="";
            String combo="";
            int notify_default=0;
            long defaultmasteritemcount=0;
            int k=0;
            while(rscombo.next()){
            	combo=rscombo.getString("masterid");
            	stmt1=conn.createStatement();
            	rscompany=stmt1.executeQuery("SELECT DISTINCT companyid FROM company");
            	while(rscompany.next()){
            		company=rscompany.getString("companyid");
            		stmt2=conn.createStatement();
            		rsid=stmt2.executeQuery("SELECT id FROM defaultmasteritem WHERE crmCombomasterId='"+combo+"' AND companyid='"+company+"'");
            		k=1;	
            		while(rsid.next()){
            			id=rsid.getString("id");	
            			stmt3=conn.createStatement();	
                		notify_default=stmt3.executeUpdate("UPDATE defaultmasteritem SET itemsequence="+k+" WHERE deleteflag=0 AND crmCombomasterId='"+combo+"' AND companyid='"+company+"' AND id='"+id+"'");	
                		k++;
                		defaultmasteritemcount++;
            		}
            	}
            }          
            out.println("<BR/>No of Records of defaultmasteritem updated=> "+ defaultmasteritemcount+"<BR/>");
        } catch (Exception ex) {
            out.println(ex.getMessage());
        }
%>
