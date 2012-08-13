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
<%@page import="com.krawler.common.admin.*" %>


<%
            Connection conn =null;
            try {
                ResultSet rs = null, rs1 = null;
                PreparedStatement pstmt = null, pstmt1 = null, pstmt2 = null;
                String toDb = request.getParameter("todb");
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://192.168.0.244:3306/" + toDb + "?user=krawler&password=krawler");
                conn.setAutoCommit(false);
                // wont update for company with id '034c0f89-e501-47d1-8e52-74abb5fe6958'
                pstmt = conn.prepareStatement("select s.state,s.cid from " + toDb + ".spreadsheet_config s inner join " + toDb + ".users u on s.user=u.userid   where  s.state like '%custom_field%'  and u.company!='034c0f89-e501-47d1-8e52-74abb5fe6958' ");//and companyname=?
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String cid = rs.getString("cid");
                    String state = rs.getString("state");
                    JSONObject jobj = new JSONObject(state);
                    try{
                            if(jobj.has("columns")){
                                    JSONObject newjarr = new JSONObject();
                                    JSONArray jarr = jobj.getJSONArray("columns");
                                    int j=0;
                                    for(int i=0;i<jarr.length();i++){
                                        JSONObject tempjobj = jarr.getJSONObject(i);
                                        String columnname = tempjobj.getString("id");
                                        if(columnname.startsWith("custom_field")){

                                                Integer fieldid = Integer.parseInt(columnname.split("custom_field")[1]);
                                                pstmt1 = conn.prepareStatement("select id from " + toDb + ".fieldParams where oldid=?  ");//and companyname=?
                                                pstmt1.setInt(1,fieldid);
                                                rs1 = pstmt1.executeQuery();
                                                 while (rs1.next()) {
                                                     String id =  rs1.getString("id");
                                                     columnname = "custom_field"+id;
                                                     tempjobj.put("id",columnname);
                                                     j++;
                                                 }
                                        }
                                        newjarr.append("columns",tempjobj);
                                    }
                                    if(jobj.has("sort")){
                                        newjarr.put("sort",jobj.get("sort"));
                                    }
                                    if(j>0){
                                        pstmt2 = conn.prepareStatement(" update " + toDb + ".spreadsheet_config  set state=? where cid=? ");
                                        pstmt2.setString(1,newjarr.toString());
                                        pstmt2.setString(2,cid);
                                        int c = pstmt2.executeUpdate();
                                        out.println("Updated record with cid = "+cid +" with state "+newjarr.toString());
                                    }
                            }
                    }catch(Exception e){
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                    }
                    
                }

                rs1.close();
                rs.close();
                conn.commit();
                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                out.println(ex.getMessage());
                conn.rollback();
            }
%>
