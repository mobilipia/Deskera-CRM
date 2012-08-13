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
            String toDb = request.getParameter("todb");
            int count=0;

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + fromDb + "?user=" + user + "&password=" + pass);
            conn.setAutoCommit(false);

            pstmt = conn.prepareStatement("set foreign_key_checks = 0");
            int a1 = pstmt.executeUpdate();




            pstmt = conn.prepareStatement("alter table scheduledmarketing add column temp_convert_date datetime");
            pstmt.executeUpdate();


            pstmt = conn.prepareStatement("select id,scheduledtime,(unix_timestamp(scheduleddate)*1000)," +
                    "tz.difference from scheduledmarketing sm inner join users u on u.userid=sm.userid" +
                    " left join timezone tz on tz.timzoneid=u.timeZone");
            rs = pstmt.executeQuery();


            while(rs.next()){
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
                String sttime = rs.getString(2).isEmpty()?"00:00":rs.getString(2);
                Date startdate = converttz(rs.getString(4),new Date(rs.getLong(3)),sdf2.format(sdf.parse(sttime)));

                pstmt = conn.prepareStatement("update scheduledmarketing set temp_convert_date=? where id=?");
                pstmt.setString(1, sdf1.format(startdate));
                pstmt.setString(2, rs.getString(1));
                pstmt.executeUpdate();
            }
            pstmt = conn.prepareStatement("alter table scheduledmarketing drop column scheduleddate");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("alter table scheduledmarketing add column scheduleddate bigint");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("update scheduledmarketing set scheduleddate=(unix_timestamp(temp_convert_date)*1000)");
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement("alter table scheduledmarketing drop column temp_convert_date");
            pstmt.executeUpdate();

            count++;
            out.println("<br>"+count+"> Changed scheduledmarketing");
            System.out.println(count+"> Changed scheduledmarketing");

            conn.commit();
            pstmt.close();


    } catch(Exception e) {
        out.println("<br>Exception is <br>"+e);
        System.out.println("\n Exception is"+e);
        conn.rollback();
    } finally {
        conn.close();
    }



%><%!
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
                    time=time.replaceFirst(val, ""+val1);
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
            if (dtcmp.compareTo(dtold) > 0 || dtcmp.compareTo(dtold)==0) {
                cal.add(Calendar.DATE, 1);
            }


        } catch (ParseException ex) {
            System.out.println(ex);
        }finally{
            return cal.getTime();
        }

    }%>




