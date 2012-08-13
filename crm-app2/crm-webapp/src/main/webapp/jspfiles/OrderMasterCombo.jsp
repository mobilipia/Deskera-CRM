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
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>
<%
try{
	ResultSet rscombo=null;
	ResultSet rsvalue=null;
	
	ResultSet rsid=null;
	Statement stmt=null;
	Statement stmt1=null;
	Statement stmt2=null;
	
	String dbName = "crmutf8";
	String userName = "root";
    String password = "krawler";
    String url = "jdbc:mysql://192.168.0.107:3306/" + dbName + "?useUnicode=true&amp;characterEncoding=UTF-8";
    Class.forName("com.mysql.jdbc.Driver");
    Connection conn = DriverManager.getConnection(url, userName, password);
    if(conn!=null)
    	out.println("<BR/>Connection is OK...!<BR/>");
    stmt=conn.createStatement();
    rscombo=stmt.executeQuery("SELECT masterid FROM crm_combomaster");
    if(rscombo!=null)
    	out.println("Combos are retrived<BR/>");
    int k=0;
    int count=0;
    String val="";
    String combo="";
    int notify_combodata=0;
	long crm_combodatacount=0;
    while(rscombo.next()){
       	combo=rscombo.getString("masterid");
    	stmt1=conn.createStatement();
    	rsvalue=stmt1.executeQuery("SELECT valueid FROM crm_combodata WHERE comboid='"+combo+"'");
    	k=1;
    	while(rsvalue.next()){
    		val=rsvalue.getString("valueid");
    		stmt2=conn.createStatement();
    		notify_combodata=stmt2.executeUpdate("UPDATE crm_combodata SET itemsequence="+k+" WHERE comboid='"+combo+"' AND valueid='"+val+"'");
    		k++;
    		crm_combodatacount++;
    	}
    }
    out.println("<BR/>No of Records of crm_combodata updated=> "+ crm_combodatacount+"<BR/>");
   
}catch(Exception e){
	out.println(e.getMessage());
}
   

%>
