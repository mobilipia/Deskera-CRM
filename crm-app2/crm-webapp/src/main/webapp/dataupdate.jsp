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

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.sql.PreparedStatement"%><%@ page contentType="text/html"%>
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
            ResultSet rs = null;
            PreparedStatement stmt=null;
            Connection conn=null;
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss aaa");
            String query=null;
            String dbName = "DummyCRMUTF";  
            String userName = "root";
            String password = "krawler";
            String url = "jdbc:mysql://192.168.0.204:3306/" + dbName + "?useUnicode=true&amp;characterEncoding=UTF-8";
            Statement st=null,st1=null;
            
            try {			
             Class.forName("com.mysql.jdbc.Driver");
             conn = DriverManager.getConnection(url, userName, password);
            conn.setAutoCommit(false);
            if(conn==null)
            	out.println("<BR/>Connection problem...!");
            st=conn.createStatement();
            st1=conn.createStatement();
            
            rs = st.executeQuery("select fieldParams.fieldtype,fieldParams.colnum,fieldParams.moduleid from fieldParams where fieldParams.fieldtype = 3 ");
            while(rs.next()){ 
            	String table = "";
                String priId = "";
            	int moduleid = rs.getInt("moduleid");
            	String colName = "col"+ rs.getString("colnum");
            	if (moduleid == 1) {
            		table=  " crmaccountcustomdata ";  
            		priId = "accountid";
                } else if (moduleid == 2) {
                	table=  " crmleadcustomdata ";
                	priId = "leadid";
                } else if (moduleid == 6) {
                	table=  " crmcontactcustomdata "; 
                	priId = "contactid";
                } else if (moduleid == 5) {
                	table= " crmopportunitycustomdata "; 
                	priId = "oppid";
                } else if (moduleid == 4) {
                	table= " crmproductcustomdata ";
                	priId = "productid";
                } else if (moduleid == 3) {
                	table= " crmcasecustomdata "; 
                	priId = "caseid";
             	}
            	
            	String sql = " select "+priId+","+colName+" from "+table ;
            	ResultSet rs2 = st1.executeQuery(sql);
            	while(rs2.next()){
            		if(!StringUtil.isNullOrEmpty(rs2.getString(colName))){
            			if(rs2.getString(colName).contains(",") || rs2.getString(colName).contains("AM") || rs2.getString(colName).contains("PM") ){
            				String tempdate = rs2.getString(colName); 
            				out.println("<BR/> date val =  "+ tempdate );
            				Long longdate =sdf.parse(tempdate).getTime(); 
            				String longDateStr = Long.toString(longdate); 
            				out.println("     date long val =  "+ longDateStr );
            				String id = rs2.getString(priId);
            				String sqlUpdate = " update "+table+" set "+colName+" = "+longDateStr+ " where  "+priId+" = "+id;
            				stmt = conn.prepareStatement(sqlUpdate);
            				//stmt.executeUpdate();
      					    out.println("<BR/><BR/><BR/><BR/>");
      						stmt.close();
            			}
            		}
            	}
            } // loop 
            
           //  conn.commit();
           
        } catch (Exception ex) {
        	if(conn!=null)conn.rollback();
        	throw ex;
        } finally {
        	if(conn!=null)conn.close();
        	if(st!=null)st.close();
        	if(st1!=null)st1.close();
        	if(stmt!=null)stmt.close();
        }
        
%>
