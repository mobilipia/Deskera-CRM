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
<%@ page import="com.krawler.common.util.URLUtil" %>
<%@ page import="com.krawler.esp.utils.ConfigReader"%>
<%@ page import="java.security.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.*"%>
<%@ page import="java.io.InputStream"%>
<%@ page import="java.net.URL"%>
<%@ page import="java.net.URLConnection"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="com.krawler.common.util.StringUtil"%>
<%@ page import="com.krawler.utils.json.base.JSONException"%>
<%@ page import="com.krawler.utils.json.base.JSONObject"%>
<%@ page import="com.krawler.utils.json.base.JSONArray"%>
<%@ page import="com.krawler.common.session.SessionExpiredException"%>
<%@ page import="java.sql.*" %>
<%@ page import="com.krawler.common.util.Constants" %>
<%@ page import="javax.servlet.http.HttpSession" %>
<%@page import="com.krawler.spring.sessionHandler.sessionHandlerImpl" %>
<%@page import="javax.servlet.http.HttpServletRequest" %>
<%@ page import="com.krawler.esp.utils.ConfigReader" %>
<%@page import="javax.servlet.http.HttpServletResponse" %>
<%@page import="com.krawler.esp.web.resource.Links"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title id="Deskeracrmtitle">Deskera</title>
        <style>
            body{
                font-family: tahoma, sans-serif, verdana, arial, times new roman;
                font-size: 14px;
            }
            ul{
                margin: 0px;
            }
            .companylogo {
                margin: 0 0 10px;
                border-bottom: 1px dotted #CCCCCC;
                height: 30px;
            }
            .appNameDiv{
                margin-bottom: 5px;
                margin-top: 5px;
            }
            a{
                color: #083772;
            }
        </style>
    </head>
    <body>
<%!
    String toMD5(String pass) throws Exception
    {
        MessageDigest m= MessageDigest.getInstance("SHA1");
        m.update(pass.getBytes(),0,pass.length());
        return (new BigInteger(1,m.digest()).toString(16)).toString();
    }
    
    public StringBuffer recursiveUsers(Connection conn, String userid) {
        StringBuffer usersList = new StringBuffer();
        try {
            recursiveUsers(conn, userid, usersList, 0, "");
            usersList.append("'" + userid + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersList;
    }

    public void recursiveUsers(Connection conn, String manID, StringBuffer appendUser, int exceptionAt, String extraQuery){
        try {
            /* This method is also called from remoteapi.java */
            PreparedStatement pstmt = null;
            pstmt = conn.prepareStatement("select * from assignmanager where manid = ?");
            pstmt.setString(1, manID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                appendUser.append("'" + rs.getString("empid") + "',");
                recursiveUsers(conn, rs.getString("empid") , appendUser, exceptionAt, extraQuery);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
%>
<%
        boolean isValid = false;
        String userid = "";
        String companyid = "";
        String timeformat = "";
        boolean notificationtype = false;
        StringBuffer usersList = new StringBuffer();
        String domain = URLUtil.getDomainName(request);
        String signoutURL = URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull) + "login.jsp";//"bbmobile/signOut.jsp";
        if(request.getParameter("verifyUser") != null && request.getParameter("verifyUser").equals("1")) {
            Connection conn = null;
            try{
                String dbName = ConfigReader.getinstance().get("dbname");
                String dbSeverIP = ConfigReader.getinstance().get("dbserverip");
                String dbUser = ConfigReader.getinstance().get("mysql_user");
                String dbPass = ConfigReader.getinstance().get("mysql_passwd");
                String connectionURL = "jdbc:mysql://"+dbSeverIP+":3306/"+dbName+"?user="+dbUser+"&password="+dbPass;
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection(connectionURL);

                String user = request.getParameter("UserName").trim();
                String pass = request.getParameter("Password");
                
                pass = toMD5(pass);//"7110eda4d09e062aa5e4a390b0a572ac0d2c0220";//toMD5(pass);
                PreparedStatement pstmt = null;
                String query = "select u.userid,u.company,u.timeformat,c.notificationtype from users u inner join userlogin ul on ul.userid = " +
                        " u.userid inner join company c on c.companyid = u.company where ul.username = ? and ul.password = ? and c.subdomain = ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, user);
                pstmt.setString(2, pass);
                pstmt.setString(3, domain);
                ResultSet rs = pstmt.executeQuery();
                if(rs.next())  {
                    isValid = true;
                    userid = rs.getString("userid");
                    companyid = rs.getString("company");
                    timeformat = rs.getString("timeformat");
                    notificationtype = rs.getInt("notificationtype") > 0 ? true : false;
                    usersList = recursiveUsers(conn, userid);
                    
                    session = request.getSession(true);
                    session.setAttribute(Constants.SESSION_USERID, userid);
                    session.setAttribute(Constants.SESSION_COMPANY_ID, companyid);
                    session.setAttribute(Constants.SESSION_TIMEFORMAT, timeformat);
                    session.setAttribute(Constants.SESSION_INITIALIZED, "true");
                    session.setAttribute(Constants.SESSION_NOTIFYON, notificationtype);
                    if (usersList != null)
                    {
                        session.setAttribute(Constants.SESSION_USERLIST, usersList);
                    }
                }
            } catch(Exception e) {
                response.sendRedirect(signoutURL+"?type=signout");
            } finally {
                if(conn != null){
                    conn.close();
                }
            }
        } else {
            try{
                userid = sessionHandlerImpl.getUserid(request);
                companyid = sessionHandlerImpl.getCompanyid(request);
                timeformat = sessionHandlerImpl.getUserTimeFormat(request);
                notificationtype = sessionHandlerImpl.getCompanyNotifyOnFlag(request);
                usersList = sessionHandlerImpl.getRecursiveUsersList(request);
                isValid = true;
            } catch(SessionExpiredException ex) {
                response.sendRedirect(signoutURL+"?type=timeout");
            }
        }
        
if(isValid) {
%>
            <div class="companylogo">
                <img id="deskeraimageq" src="../images/logo1.gif"/>
                <div style="font-size: 11px; float: right;"><a href=<%=signoutURL+"?type=signout" %>>Sign Out</a></div>
            </div>
            <div style="margin-left:4%;">
<%        
            String res = "{}";
            /*InputStream iStream = null;
            JSONObject responseObject = new JSONObject();
            try
            {

                JSONObject jData = new JSONObject();
                jData.put("userid", userid);
                jData.put("companyid", companyid);
                jData.put("subdomain", domain);
                jData.put("remoteapikey", ConfigReader.getinstance().get("remoteapikey"));
                String action = "11";
                String appURL = ConfigReader.getinstance().get("platformURL");
                String API_STRING = "remoteapi.jsp";
                String strSandbox = appURL + API_STRING;
                URL u = new URL(strSandbox);
                URLConnection uc = u.openConnection();
                uc.setDoOutput(true);
                uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                java.io.PrintWriter pw = new java.io.PrintWriter(uc.getOutputStream());
                pw.println("action=" + action + "&data=" + URLEncoder.encode(jData.toString()));
                pw.close();
                iStream = uc.getInputStream();
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(iStream));
                res = URLDecoder.decode(in.readLine());
                in.close();
                iStream.close();
            } catch (Exception iex)
            {
                iex.printStackTrace();
                out.println("Incorrect request. Please try again.");
            } finally
            {
                if (iStream != null)
                {
                    try
                    {
                        iStream.close();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        out.println("Incorrect request. Please try again.");
                    }
                }
            }
            if(!StringUtil.isNullOrEmpty(res)) {
                responseObject = new JSONObject(res);
                if(responseObject.has("data")) {
                    JSONArray subscribeData = responseObject.getJSONArray("data");
                    for(int cnt=0; cnt< subscribeData.length();cnt++) {
                        JSONObject appData = subscribeData.getJSONObject(cnt);
                        if(appData.has("appid")) {
                            int appid = Integer.parseInt(appData.getString("appid"));
                            switch(appid) {
                                case 1 :*/
                                    String overdueProjectTaskPage = ConfigReader.getinstance().get("projectManagementURL");
                                    overdueProjectTaskPage = overdueProjectTaskPage + "b/"+domain+"/bbUpdates.jsp?userid="+userid+"&subdomain="+domain;
%>
                                    <div class="appNameDiv">Deskera-Project Management</div>
                                    <ul start="4" >
                                    <li><a href=<%=overdueProjectTaskPage%>>Critical Tasks</a></li>
                                    </ul>
<%
   /*                                 break;
                                case 2:*/
                                    String mobilePagesPath = "bbmobile/";
                                    String assignGoalPage = mobilePagesPath + "assignGoal.jsp";
                                    assignGoalPage = URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull) + assignGoalPage;
%>
                                    <div class="appNameDiv">Deskera-CRM</div>
                                    <ul start="4" >
                                    <li><a href=<%=assignGoalPage %>>Assign Goal</a></li>
                                    </ul>
<%
    /*                                break;
                                case 4:*/
                                    String assignHRMSGoalPage = ConfigReader.getinstance().get("hrmsURL");
                                    assignHRMSGoalPage = assignHRMSGoalPage + "b/"+domain+"/assignGoals.jsp?userid="+userid+"&companyid="+companyid+"&subdomain="+domain;
%>
                                    <div class="appNameDiv">Deskera-HRMS</div>
                                    <ul start="4" >
                                    <li><a href=<%=assignHRMSGoalPage %>>Assign Goal</a></li>
                                    </ul>
<%
          /*                          break;
                            }
                        }
                    }
                }
            }
            else{
                 out.println("Incorrect request. Make sure you have access to Deskera applications.");
            }*/

} else {
    response.sendRedirect(signoutURL+"?type=Authentication Failed.");
}
%>
            </div>
    </body>
</html>
