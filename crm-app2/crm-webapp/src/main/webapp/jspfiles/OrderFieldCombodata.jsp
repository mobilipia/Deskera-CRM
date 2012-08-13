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
            Statement stmt=null;
            Statement stmt1=null;
            Statement stmt2=null;
            ResultSet rsfieldids=null;
            ResultSet rsids=null;
            
                
            String dbName = "crmutf8";
            String userName = "root";
            String password = "krawler";
            String url = "jdbc:mysql://192.168.0.107:3306/" + dbName + "?useUnicode=true&amp;characterEncoding=UTF-8";
			
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, userName, password);
            if(conn!=null)
            	out.println("<BR/>Connection is OK...!");
            	
            stmt=conn.createStatement();
            rsfieldids=stmt.executeQuery("SELECT DISTINCT fieldid FROM fieldcombodata WHERE deleteflag=0");
            if(rsfieldids!=null)
            out.println("<BR/>FieldIds are retrived..<BR/>");
            String fieldid="";
            String id="";
            int notify=0;
            int k=0;
            int successcount=0;
            while(rsfieldids.next()){
            	fieldid=rsfieldids.getString("fieldid");
              	stmt1=conn.createStatement();
                rsids=stmt1.executeQuery("SELECT id FROM fieldcombodata WHERE fieldid='"+ fieldid + "'");
            	k=1;
                while(rsids.next()){
            		id=rsids.getString("id");	
            		stmt2=conn.createStatement();
            		notify=stmt2.executeUpdate("UPDATE fieldcombodata SET itemsequence="+ k + " WHERE id='"+ id + "' AND fieldid='"+fieldid+"'  AND deleteflag=0 ");
            		if(notify!=0)
            			successcount++;
            		k++;
            	}
            }
        	
			out.println("<BR/>No of Records updated are=> "+ successcount);
        }catch(Exception e){
        	out.println(e.getMessage());
        }
%>
