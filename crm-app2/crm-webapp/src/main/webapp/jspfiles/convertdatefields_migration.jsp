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
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="com.krawler.common.session.SessionExpiredException"%>
<%@ page import="com.krawler.common.util.StringUtil" %>
<%@ page import="com.krawler.esp.database.*" %>
<%@ page import="com.krawler.utils.json.base.JSONObject" %>
<%@ page import="com.krawler.utils.json.base.JSONArray" %>

<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.DriverManager" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="com.krawler.common.service.ServiceException"%>
<%@ page import="java.sql.SQLException"%>
<%@ page import=" org.hibernate.*" %>
<%@ page import=" com.krawler.esp.hibernate.impl.HibernateUtil" %>


<%@ page import="java.util.*"%>
<%@ page import=" java.io.*"%>
<%@ page import="java.text.*" %>

<jsp:useBean id="sessionbean" scope="session" class="com.krawler.esp.handlers.SessionHandler" />
<%
    Connection conn = null;

    try {
            PreparedStatement pstmt = null;
            ResultSet rs = null;


            String ip = request.getParameter("ip");
            String port = request.getParameter("port");
            String user = request.getParameter("user");
            String pass = request.getParameter("pass");
            String fromDb = request.getParameter("fromdb");
            //String app = request.getParameter("app");
            String toDb = request.getParameter("todb");
            //String companyid = request.getParameter("cid");
            int count=0;

            //Session session1 = HibernateUtil.getCurrentSession();

            Class.forName("com.mysql.jdbc.Driver");
            //conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" ,user,pass);
            conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + fromDb + "?user=" + user + "&password=" + pass);
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement("set foreign_key_checks = 0");
            int a1 = pstmt.executeUpdate();




            pstmt = conn.prepareStatement("alter table crm_activity_master add column temp_convert_date datetime");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_activity_master add column temp_convert_datetwo datetime");
            pstmt.executeUpdate();






            pstmt = conn.prepareStatement("select activityid,starttime,endtime,(unix_timestamp(startdate)*1000)," +
                    "(unix_timestamp(enddate)*1000),u.timeZone,tz.difference from" +
                    " crm_activity_master cam inner join users u on u.userid=cam.createdbyid" +
                    " left join timezone tz on tz.timzoneid=u.timeZone");
            rs = pstmt.executeQuery();


            while(rs.next()){
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
                String sttime = rs.getString(2).isEmpty()?"00:00 AM":rs.getString(2);
                String edtime = rs.getString(3).isEmpty()?"00:00 AM":rs.getString(3);
                Date startdate = converttz(rs.getString(7),new Date(rs.getLong(4)),sttime);
                Date enddate = converttz(rs.getString(7),new Date(rs.getLong(5)),edtime);

                pstmt = conn.prepareStatement("update crm_activity_master set temp_convert_date=?, temp_convert_datetwo=? where activityid=?");
                pstmt.setString(1, sdf1.format(startdate));
                pstmt.setString(2, sdf1.format(enddate));
                pstmt.setString(3, rs.getString(1));
                pstmt.executeUpdate();
            }
            pstmt = conn.prepareStatement("alter table crm_activity_master drop column startdate");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_activity_master add column startdate bigint");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_activity_master drop column enddate");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_activity_master add column enddate bigint");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_activity_master set startdate=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            
            pstmt = conn.prepareStatement("update crm_activity_master set enddate=(unix_timestamp(temp_convert_datetwo)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_activity_master drop column temp_convert_datetwo");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_activity_master set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_activity_master drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_activity_master add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_activity_master set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_activity_master set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_activity_master drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_activity_master add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_activity_master set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_activity_master drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed crm_activity_master");
            System.out.println("<br>"+count+"> Changed crm_activity_master");



            pstmt = conn.prepareStatement("alter table audit_trail add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update audit_trail set temp_convert_date=audittime");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table audit_trail drop column audittime");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table audit_trail add column audittime bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update audit_trail set audittime=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table audit_trail drop column temp_convert_date");
            pstmt.executeUpdate();
            
            count++;
            out.println("<br>"+count+"> Changed audit_trail");
            System.out.println("<br>"+count+"> Changed audit_trail");


            pstmt = conn.prepareStatement("alter table campaign_target add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update campaign_target set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table campaign_target drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table campaign_target add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update campaign_target set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table campaign_target drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed campaign_target");
            System.out.println("<br>"+count+"> Changed campaign_target");



            pstmt = conn.prepareStatement("alter table commission add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update commission set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table commission drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table commission add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update commission set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table commission drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed commission");
            System.out.println("<br>"+count+"> Changed commission");



            pstmt = conn.prepareStatement("alter table crm_account add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_account set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_account drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_account add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_account set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_account set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_account drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_account add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_account set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_account drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed crm_account");
            System.out.println("<br>"+count+"> Changed crm_account");



            pstmt = conn.prepareStatement("alter table crm_campaign add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_campaign set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_campaign drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_campaign add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_campaign set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_campaign set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_campaign drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_campaign add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_campaign set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_campaign drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed crm_campaign");
            System.out.println("<br>"+count+"> Changed crm_campaign");





            pstmt = conn.prepareStatement("alter table crm_case add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_case set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_case drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_case add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_case set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_case set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_case drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_case add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_case set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_case drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed crm_case");
            System.out.println("<br>"+count+"> Changed crm_case");






            pstmt = conn.prepareStatement("alter table crm_contact add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_contact set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_contact drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_contact add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_contact set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_contact set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_contact drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_contact add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_contact set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_contact drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed crm_contact");
            System.out.println("<br>"+count+"> Changed crm_contact");



            pstmt = conn.prepareStatement("alter table crm_docs add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_docs set temp_convert_date=uploadedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_docs drop column uploadedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_docs add column uploadedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_docs set uploadedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_docs drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed crm_docs");
            System.out.println("<br>"+count+"> Changed crm_docs");



            pstmt = conn.prepareStatement("alter table crm_lead add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_lead set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_lead drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_lead add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_lead set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_lead set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_lead drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_lead add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_lead set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_lead drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed crm_lead");
            System.out.println("<br>"+count+"> Changed crm_lead");




            pstmt = conn.prepareStatement("alter table crm_leadProducts add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_leadProducts set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_leadProducts drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_leadProducts add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_leadProducts set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_leadProducts drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed crm_leadProducts");
            System.out.println("<br>"+count+"> Changed crm_leadProducts");




            pstmt = conn.prepareStatement("alter table crm_opportunity add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_opportunity set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_opportunity drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_opportunity add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_opportunity set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_opportunity set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_opportunity drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_opportunity add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_opportunity set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_opportunity drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed crm_opportunity");
            System.out.println("<br>"+count+"> Changed crm_opportunity");


            pstmt = conn.prepareStatement("alter table crm_product add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_product set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_product drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_product add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_product set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update crm_product set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_product drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table crm_product add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update crm_product set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table crm_product drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed crm_product");
            System.out.println("<br>"+count+"> Changed crm_product");



            pstmt = conn.prepareStatement("alter table emailmarketing add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update emailmarketing set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailmarketing drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailmarketing add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update emailmarketing set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update emailmarketing set temp_convert_date=modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailmarketing drop column modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailmarketing add column modifiedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update emailmarketing set modifiedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table emailmarketing drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed emailmarketing");
            System.out.println("<br>"+count+"> Changed emailmarketing");




            pstmt = conn.prepareStatement("alter table emailmarkteing_targetlist add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update emailmarkteing_targetlist set temp_convert_date=modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailmarkteing_targetlist drop column modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailmarkteing_targetlist add column modifiedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update emailmarkteing_targetlist set modifiedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table emailmarkteing_targetlist drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed emailmarkteing_targetlist");
            System.out.println("<br>"+count+"> Changed emailmarkteing_targetlist");





            pstmt = conn.prepareStatement("alter table emailtemplate add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update emailtemplate set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailtemplate drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailtemplate add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update emailtemplate set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update emailtemplate set temp_convert_date=modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailtemplate drop column modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailtemplate add column modifiedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update emailtemplate set modifiedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table emailtemplate drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed emailtemplate");
            System.out.println("<br>"+count+"> Changed emailtemplate");




            pstmt = conn.prepareStatement("alter table emailtemplatefiles add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update emailtemplatefiles set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailtemplatefiles drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table emailtemplatefiles add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update emailtemplatefiles set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table emailtemplatefiles drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed emailtemplatefiles");
            System.out.println("<br>"+count+"> Changed emailtemplatefiles");





            pstmt = conn.prepareStatement("alter table finalgoalmanagement add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update finalgoalmanagement set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table finalgoalmanagement drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table finalgoalmanagement add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update finalgoalmanagement set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update finalgoalmanagement set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table finalgoalmanagement drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table finalgoalmanagement add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update finalgoalmanagement set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table finalgoalmanagement drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed finalgoalmanagement");
            System.out.println("<br>"+count+"> Changed finalgoalmanagement");





            pstmt = conn.prepareStatement("alter table spreadsheet_config add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update spreadsheet_config set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table spreadsheet_config drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table spreadsheet_config add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update spreadsheet_config set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table spreadsheet_config drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed spreadsheet_config");
            System.out.println("<br>"+count+"> Changed spreadsheet_config");



            pstmt = conn.prepareStatement("alter table target_module add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update target_module set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table target_module drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table target_module add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update target_module set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update target_module set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table target_module drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table target_module add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update target_module set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table target_module drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed target_module");
            System.out.println("<br>"+count+"> Changed target_module");



            pstmt = conn.prepareStatement("alter table targetlist add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update targetlist set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table targetlist drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table targetlist add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update targetlist set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update targetlist set temp_convert_date=modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table targetlist drop column modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table targetlist add column modifiedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update targetlist set modifiedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table targetlist drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed targetlist");
            System.out.println("<br>"+count+"> Changed targetlist");





            pstmt = conn.prepareStatement("alter table targetlist_targets add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update targetlist_targets set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table targetlist_targets drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table targetlist_targets add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update targetlist_targets set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update targetlist_targets set temp_convert_date=modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table targetlist_targets drop column modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table targetlist_targets add column modifiedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update targetlist_targets set modifiedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table targetlist_targets drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed targetlist_targets");
            System.out.println("<br>"+count+"> Changed targetlist_targets");



            pstmt = conn.prepareStatement("alter table usecommissionplan add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update usecommissionplan set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table usecommissionplan drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table usecommissionplan add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update usecommissionplan set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update usecommissionplan set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table usecommissionplan drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table usecommissionplan add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update usecommissionplan set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table usecommissionplan drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed usecommissionplan");
            System.out.println("<br>"+count+"> Changed usecommissionplan");



            pstmt = conn.prepareStatement("alter table users add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update users set temp_convert_date=createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table users drop column createdon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table users add column createdon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update users set createdon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update users set temp_convert_date=updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table users drop column updatedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table users add column updatedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update users set updatedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table users drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed users");
            System.out.println("<br>"+count+"> Changed users");





            pstmt = conn.prepareStatement("alter table widgetmanagement add column temp_convert_date datetime");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update widgetmanagement set temp_convert_date=modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table widgetmanagement drop column modifiedon");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table widgetmanagement add column modifiedon bigint");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("update widgetmanagement set modifiedon=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table widgetmanagement drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed widgetmanagement");
            System.out.println("<br>"+count+"> Changed widgetmanagement");




            conn.commit();
            pstmt.close();


    } catch(Exception e) {
        out.println("<br>Exception is <br>"+e);
        System.out.println("\n Exception is"+e);
        conn.rollback();
    } finally {
        conn.close();
    }



%><%! public String preferenceDatejsformat(String timeZoneDiff, Date date, DateFormat sdf) throws ServiceException {
        String result = "";
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));
            String prefDate = "";
            if (date != null) {
                result = sdf.format(date);
            } else {
                return result;
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.preferenceDate", e);
        }
        return result;
    }
/*
 * To convert a date and time selected separately by user into corresponding combined datetime
 * from users selected timezone to systems timezone
 *
 * The first step is to keep track of the time difference in order to change the date if required.
 * Two time only objects dtold and dtcmp are created for this purpose.
 *
 * The date passed and the time passed that are in system timezone are formatted without
 * timezone and then parsed into the required timezone and then the time values are set
 * back to the date value sent.
 *
 */
    public Date converttz(String timeZoneDiff,Date dt, String time){
        Calendar cal = Calendar.getInstance();
        try {
            if(timeZoneDiff==null||timeZoneDiff.isEmpty()) {
                timeZoneDiff="-7:00";
            }
            String val;
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm a");
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm a");
            sdf2.setTimeZone(TimeZone.getTimeZone("GMT"+timeZoneDiff));                // Setting the timezone passed
            Date dtold = sdf.parse("00:00 AM");                                        // Creating a time only object in server timezone


            if(time.contains("PM")||time.contains("12")) {
                val = time.substring(0, time.indexOf(":"));
                int val1 = Integer.parseInt(val);
                if(val1!=12) {
                    val1=val1+12;
                    time=time.replace(val, ""+val1);
                } else if(!time.contains("PM")) {
                    val1=val1-12;
                    time=time.replace(val, ""+val1);
                }
            }
            Date dt1 = sdf.parse(time);                                                // Setting the passed time to the date object in system timezone

            sdf.setTimeZone(TimeZone.getTimeZone("GMT"+timeZoneDiff));                 // Setting the timezone passed
            Date dtcmp = sdf.parse(time);                                              // Parsing the time to timezone using passed values
            dt1.setMonth(dt.getMonth());                                               // Setting the date values sent to the system time only value
            dt1.setDate(dt.getDate());
            dt1.setYear(dt.getYear());
            dt1 = sdf2.parse(sdf1.format(dt1));                                        // Parsing datetime into required timezone
            dt.setHours(dt1.getHours());                                               // Setting the time values into the sent date
            dt.setMinutes(dt1.getMinutes());
            dt.setSeconds(0);
            cal.setTime(dt);
            if (dtcmp.compareTo(dtold) < 0) {                                          // Comparing for time value change
                cal.add(Calendar.DATE, -1);                                            //  in order to change the date accordingly
            }
            dtold.setDate(2);
            if (dtcmp.compareTo(dtold) > 0) {
                cal.add(Calendar.DATE, 1);
              //System.out.println("\n Time:- "+time+" DTcmp:- "+dtcmp+" DTOLD:- "+dtold);

            }
            
            
        } catch (ParseException ex) {
            System.out.println(ex);
        }finally{
            return cal.getTime();
        }

    }%>




