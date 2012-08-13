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
                    pstmt1 = conn.prepareStatement("select d.id did,f.id fid,f.moduleid,f.fieldtype,f.maxlength,f.isessential,f.validationtype," +
                            "f.customregex,f.fieldname,f.fieldlabel,f.iseditable,f.comboname,f.comboid,f.moduleflag  from "
                            + toDb + ".fieldparams f inner join " + toDb + ".default_header d on f.id=d.pojoheadername " +
                            "inner join " + toDb + ".column_header c on d.id=c.defaultHeader where c.company=? ");
                    pstmt1.setString(1, companyid);
                    rs1 = pstmt1.executeQuery();
                    
                    while (rs1.next()) {
                        try {
                            Integer fieldid = Integer.parseInt(rs1.getObject("fid").toString());
                            Integer moduleid = Integer.parseInt(rs1.getObject("moduleid").toString());
                            Integer fieldtype = Integer.parseInt(rs1.getObject("fieldtype").toString());
                            if(fieldtype==7){
                                RefcolParams = getcolumn_number(companyid, moduleid, fieldtype,conn);
                                if (!Boolean.parseBoolean((String) RefcolParams.get("success"))) {
                                    colParams = RefcolParams;
                                } else {
                                    //  colnumber accessed as per normal field
                                    colParams =getcolumn_number(companyid, moduleid, 1,conn);
                                }
                            } else {
                                colParams = getcolumn_number(companyid, moduleid, fieldtype,conn);
                            }
                           
                            String fieldlabel = rs1.getObject("fieldlabel").toString();
                            String editable = rs1.getObject("iseditable").toString();

                            Integer fieldmaxlen = 12;
                            String maxlength =  rs1.getObject("maxlength").toString();
                            if (StringUtil.isNullOrEmpty(maxlength)) {
                                fieldmaxlen = Integer.parseInt(maxlength);
                            }

                            Integer validationtype = 0;

                            String isessential = rs1.getObject("isessential").toString();
                            String customregex = rs1.getObject("customregex").toString();


                            int essential = 0;
                            if (!com.krawler.common.util.StringUtil.isNullOrEmpty(isessential) && isessential.equals("false")) {
                                essential = 0;
                            } else if (!com.krawler.common.util.StringUtil.isNullOrEmpty(isessential)) {
                                essential = 1;
                            }

                            String Refcolumn_number = "0";
                            if(fieldtype == 7 && RefcolParams!=null){
                                Refcolumn_number = RefcolParams.get("column_number").toString();
                            }

                            JSONObject resultJson = new JSONObject();
                            FieldParams fp = null;
                            query = " insert into " + toDb + ".fieldParams (id,maxlength,isessential,fieldtype,validationtype," +
                                    "customregex,fieldname,fieldlabel,companyid,moduleid,iseditable,comboname,comboid," +
                                    "moduleflag,colnum,refcolnum,oldid)" +
                                    " values(?,?,?,?,?," +
                                    "?,?,?,?,?,?,?,?," +
                                    "?,?,?,?) ";

                            pstmt4 = conn.prepareStatement(query);
                            String fid = UUID.randomUUID().toString();
                            pstmt4.setString(1,fid);
                            pstmt4.setInt(2,fieldmaxlen);
                            pstmt4.setInt(3,essential);
                            pstmt4.setInt(4,fieldtype );
                            pstmt4.setInt(5, validationtype);
                            pstmt4.setString(6,customregex );
                            pstmt4.setString(7, Constants.Custom_Record_Prefix+fieldlabel);
                            pstmt4.setString(8, fieldlabel);
                            pstmt4.setString(9, companyid);
                            pstmt4.setInt(10,moduleid );
                            pstmt4.setString(11, editable);
                            pstmt4.setString(12,  rs1.getObject("comboname").toString());
                            pstmt4.setString(13,  rs1.getObject("comboid").toString());
                            pstmt4.setInt(14, Integer.parseInt(rs1.getObject("moduleflag").toString()));
                            pstmt4.setInt(15,Integer.parseInt(colParams.get("column_number").toString()));
                            pstmt4.setInt(16, Integer.parseInt(Refcolumn_number));
                            pstmt4.setInt(17, fieldid);
                            //pstmt4.setString(3, did);
                            int a = pstmt4.executeUpdate();

                            resultJson.put("success",a>0? true:false);
                            if (a>0) {
                                String did = rs1.getObject("did").toString();
                                query = " update " + toDb + ".default_header set pojoheadername= ?,dbcolumnname=?,dbcolumnrefname=?,recordname=? " +
                                        "  where id = ? ";
                                pstmt5 = conn.prepareStatement(query);
                                pstmt5.setString(1, fid);
                                pstmt5.setString(2, Constants.Custom_Column_Prefix+colParams.get("column_number"));
                                pstmt5.setString(3, Refcolumn_number);
                                pstmt5.setString(4, Constants.Custom_Record_Prefix+fieldlabel);
                                pstmt5.setString(5, did);
                                
                                a = pstmt5.executeUpdate();
                                
                                if(fieldtype==7 || fieldtype==4){
                                 pstmt3 = conn.prepareStatement("select fc.id,fc.name from " + toDb + ".fieldComboData fc where fieldid=? " );
                                 pstmt3.setInt(1, fieldid);
                                 rs3 = pstmt3.executeQuery();

                                              while (rs3.next()) {
                                                try {
                                                    String fcid = UUID.randomUUID().toString();
                                                    query = " insert into " + toDb + ".fieldcombodata (id,value,fieldid,oldid) values(?,?,?,?) ";
                                                     pstmt6 = conn.prepareStatement(query);
                                                    pstmt6.setString(1,fcid);
                                                    pstmt6.setString(2,rs3.getString("name") );
                                                    pstmt6.setString(3, fid);
                                                    pstmt6.setInt(4, rs3.getInt("id"));
                                                    a = pstmt6.executeUpdate();
                                                } catch (Exception ex) {
                                                    out.println("Migration not completed successfully excpetion occured for "+query );
                                                    success =false;
                                                    out.println(ex.getMessage());
                                                    conn.rollback();
                                                }
                                             }
                                 }
                             }

                            
                        } catch (Exception e) {
                            out.println("Migration not completed successfully"+query);
                            success =false;
                            out.println(e.getMessage());
                            conn.rollback();
                        }
                    }
                    
                }
            query = " alter table column_formulae modify fieldname varchar(255)  ";
            pstmt7 = conn.prepareStatement(query);

            int a = pstmt7.executeUpdate();

            query = " update column_formulae set fieldname=concat('Custom_',fieldname) where fieldname not like 'Custom_%' ";
            pstmt7 = conn.prepareStatement(query);

            int a1 = pstmt7.executeUpdate();
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

public  HashMap<String, Object> getcolumn_number(String companyid,Integer moduleid,Integer fieldtype,Connection conn) throws Exception{
        KwlReturnObject result = null;
        JSONObject jobj = new JSONObject();
        boolean Notreachedlimit = true;
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        Integer custom_column_start=0,Custom_Column_limit=0;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String query = "  select colnum,refcolnum,fieldtype from fieldParams where companyid=? and moduleid=?  ";
        
        switch(fieldtype){
            case 1:
            case 2:
            case 3:
            case 5:
            case 6:
                custom_column_start = Constants.Custom_Column_Normal_start;
                Custom_Column_limit=Constants.Custom_Column_Normal_limit;
                String fieldtypes = "1,2,3,5,6,7";
                requestParams.put("filter_values", Arrays.asList(companyid,moduleid,"1,2,3,5,6,7",custom_column_start,custom_column_start+Custom_Column_limit));
                query += " and fieldtype in ("+fieldtypes+") and colnum > ? and colnum <= ?";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,companyid);
                pstmt.setInt(2,moduleid);
                pstmt.setInt(3,custom_column_start);
                pstmt.setInt(4,custom_column_start+Custom_Column_limit);
                
                break;
            case 4:
            case 7:
                custom_column_start = Constants.Custom_Column_Combo_start;
                Custom_Column_limit=Constants.Custom_Column_Combo_limit;
                fieldtypes="4,7";
                query += " and fieldtype in ("+fieldtypes+") ";
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1,companyid);
                pstmt.setInt(2,moduleid);
                break;



            case 8:
                custom_column_start = Constants.Custom_Column_Master_start;
                Custom_Column_limit=Constants.Custom_Column_Master_limit;
                requestParams.put("filter_names", Arrays.asList("companyid","moduleid","fieldtype"));
                requestParams.put("filter_values", Arrays.asList(companyid,moduleid,fieldtype));
                 query += " and fieldtype=? ";
                 pstmt = conn.prepareStatement(query);
                 pstmt.setString(1,companyid);
                 pstmt.setInt(2,moduleid);
                 pstmt.setInt(3,fieldtype);
                break;
        }
                Integer colcount = 1;
                rs = pstmt.executeQuery();

   
              
                        int[] countchk = new int[Custom_Column_limit+1];
                        while (rs.next()) {
                            colcount++;
                            
                      // check added to refer to reference column in case of multiselect combo field instead of refering to column number field
                            if((fieldtype==4 || fieldtype==7) && rs.getInt("fieldtype")==7){
                                countchk[rs.getInt("refcolnum")-custom_column_start] = 1;
                            }else{
                                countchk[rs.getInt("colnum")-custom_column_start] = 1;
                            }
                        }
                        for (int i = 1; i <= Custom_Column_limit; i++) {
                            if (countchk[i] == 0) {
                                colcount = i;
                                break;
                            }
                        }
         
       requestParams.put("response", jobj);
       requestParams.put("column_number", colcount+custom_column_start);
       requestParams.put("success", Notreachedlimit?"True":"false");

       return requestParams;
    }
   

%>
