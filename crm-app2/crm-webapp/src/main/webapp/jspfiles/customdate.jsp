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
            ResultSet rs = null,rs1=null,rs3=null;
            PreparedStatement pstmt = null,pstmt1 = null,pstmt2 = null,pstmt3 = null,pstmt4 = null,pstmt5 = null,pstmt6 = null,pstmt7 = null;
            String query = "";
            String toDb = request.getParameter("todb");//stagingcrm25
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://192.168.0.244:3306/" + toDb + "?user=krawler&password=krawler");
            conn.setAutoCommit(false);

            HashMap<String, Object> colParams =null;
            HashMap<String, Object> RefcolParams = null;
                // Update Script to insert valid entry for defaultHeader column of column_header table

                pstmt = conn.prepareStatement("select companyid from " + toDb + ".company ");
                rs = pstmt.executeQuery();
             //   fieldManagerDAO fieldManagerDAOobj = new fieldManagerDAOImpl();
                
                while (rs.next()) {
                    String companyid = rs.getString("companyid");
                    pstmt1 = conn.prepareStatement("select moduleid,dbcolumnname from " + toDb + ".default_header" +
                            " inner join " + toDb + ".fieldParams fp on pojoheadername=fp.id where fieldtype=3 and companyid=? ");
                    pstmt1.setString(1, companyid);
                    rs1 = pstmt1.executeQuery();
                    
                    while (rs1.next()) {
                        try {
                            String dbcolumnname =rs1.getObject("dbcolumnname").toString();
                            Integer moduleid = Integer.parseInt(rs1.getObject("moduleid").toString());
                            String modulename = getModuleName(moduleid).toLowerCase();
                            
                            
                            
                            
                        } catch (Exception e) {
                            out.println("Migration not completed successfully"+query);
                            success =false;
                            out.println(e.getMessage());
                            conn.rollback();
                        }
                    }
                    
                }
         
            //output.close();
            rs3.close();
            rs1.close();
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
<%!

    public static String getPrimarycolumn(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.Crm_accountid;
                break;
            case 2:
                module = Constants.Crm_leadid;
                break;
            case 3:
                module = Constants.Crm_caseid;
                break;
            case 4:
                module = Constants.Crm_productid;
                break;
            case 5:
                module = Constants.Crm_opportunityid;
                break;
            case 6:
                module = Constants.Crm_contactid;
                break;
        }
        return module;
    }
     public static String getmoduledataTableName(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.Crm_account_custom_data_classpath;
                break;
            case 2:
                module = Constants.Crm_lead_custom_data_classpath;
                break;
            case 3:
                module = Constants.Crm_case_custom_data_classpath;
                break;
            case 4:
                module = Constants.Crm_product_custom_data_classpath;
                break;
            case 5:
                module = Constants.Crm_opportunity_custom_data_classpath;
                break;
            case 6:
                module = Constants.Crm_contact_custom_data_classpath;
                break;
        }
        return module;
    }
     public String getTableName(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.Crm_account;
                break;
            case 2:
                module = Constants.Crm_lead;
                break;
            case 3:
                module = Constants.Crm_case;
                break;
            case 4:
                module = Constants.Crm_product;
                break;
            case 5:
                module = Constants.Crm_opportunity;
                break;
            case 6:
                module = Constants.Crm_contact;
                break;
        }
        return module;
    }
     public String getCustomTableNameRef(int moduleid) {
        String module = "";
        switch (moduleid) {
            case 1:
                module = Constants.Crm_account_pojo;
                break;
            case 2:
                module = Constants.Crm_lead_pojo;
                break;
            case 3:
                module = Constants.Crm_case_pojo;
                break;
            case 4:
                module = Constants.Crm_product_pojo;
                break;
            case 5:
                module = Constants.Crm_opportunity_pojo;
                break;
            case 6:
                module = Constants.Crm_contact_pojo;
                break;
        }
        return module.toLowerCase()+"customdataref";
    }
     public static String getModuleName(int moduleid) {
        String module="";
            if (moduleid == 1) {
                module= "Account";
            } else if (moduleid == 2) {
                module= "Lead";
            } else if (moduleid == 6) {
                module= "Contact";
            } else if (moduleid == 5) {
                module= "Opportunity";
            } else if (moduleid == 4) {
                module= "Product";
            } else if (moduleid == 3) {
                module= "Case";
            }
        return module;
    }



%>
