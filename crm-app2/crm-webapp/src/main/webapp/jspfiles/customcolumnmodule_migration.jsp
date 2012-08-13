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
            boolean success = true;
            try {
                ResultSet rs = null, rs1 = null, rs3 = null, rs2 =null, rs5 =null,rs7=null,rs8=null;
                PreparedStatement pstmt = null, pstmt1 = null, pstmt2 = null, pstmt3 = null, pstmt4 = null, pstmt5 = null, pstmt6 = null, pstmt7 = null, pstmt8 = null;
                String query = "";
                String toDb = request.getParameter("todb");//stagingcrm25
                Integer start = Integer.parseInt(request.getParameter("start"));
                Integer limit = Integer.parseInt(request.getParameter("limit"));
                Class.forName("com.mysql.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://192.168.0.244:3306/" + toDb + "?user=krawler&password=krawler");
                conn.setAutoCommit(false);
                HashMap<String, Object> colParams = null;
                HashMap<String, Object> RefcolParams = null;
                // Update Script to insert valid entry for defaultHeader column of column_header table
                int modulerange = 6,modulestart=1;
                int batchlimit =25;
                
                pstmt = conn.prepareStatement("select companyid,companyname from " + toDb + ".company where migrationstatus = ? order by companyid limit ?,?  ");//and companyname=?
                pstmt.setInt(1,0);
                pstmt.setInt(2,start);
                pstmt.setInt(3,limit);
                //pstmt.setString(2,"Demo");
                rs = pstmt.executeQuery();
                String com;
                int commitcount = 0,total=0,totalcompany=0;
                while (rs.next()) {
                    //out.println("Migration started for company '"+rs.getString("companyname")+"'");
                    out.println();
                    int modulecount =0;
                    for (int moduleid = modulestart; moduleid <= modulerange; moduleid++) {
                        
                        String companyid = rs.getString("companyid");
                        String tablename = getTableName(moduleid).toLowerCase();
                        String modulename = getModuleName(moduleid).toLowerCase();

                        if(modulename.equals("case")){
                            modulename="cases";
                        }
                        
                        String primarykey = getPrimarycolumn(moduleid).toLowerCase();
                        
                        HashMap<String, Object> requestParams = new HashMap<String, Object>();
                        requestParams.put("filter_names", Arrays.asList("companyid", "moduleid"));
                        requestParams.put("filter_values", Arrays.asList(companyid, moduleid));
                        query = "  select colnum,refcolnum,fieldtype,oldid from " + toDb + ".fieldParams where companyid=? and moduleid=?  ";
                        pstmt5 = conn.prepareStatement(query);
                        pstmt5.setString(1,companyid);
                        pstmt5.setInt(2,moduleid);
                        rs5= pstmt5.executeQuery();
                        HashMap<Object, Object> FieldColMap = new HashMap<Object, Object>();
                        HashMap<Object, Object> FieldXtypeMap = new HashMap<Object, Object>();
                        while (rs5.next()) {
                            Integer oldid =  Integer.parseInt(rs5.getObject("oldid").toString());
                            Integer fieldtype =  Integer.parseInt(rs5.getObject("fieldtype").toString());
                            Integer colnum =  Integer.parseInt(rs5.getObject("colnum").toString());
                            Integer refcolnum =  Integer.parseInt(rs5.getObject("refcolnum").toString());
                            FieldColMap.put(oldid, Constants.Custom_column_Prefix+colnum);
                            FieldColMap.put(oldid+"Ref", Constants.Custom_column_Prefix+refcolnum);
                            FieldXtypeMap.put(oldid,fieldtype);
                        }

                        if(rs5!=null){
                             rs5.close();
                        }
                         if(FieldColMap.size()>0){
                             //pstmt8 = conn.prepareStatement("select "+primarykey+" from  "+toDb+"."+tablename+" where companyid=? " );
                         query =" select modulerecid from ( select modulerecid from " + toDb + ". "+modulename+"cstm inner join  "+toDb+"."+tablename+" on modulerecid="+primarykey +" where companyid=?  " +
                         " union select modulerecid from " + toDb + ". "+modulename+"cstmmultiselect inner join  "+toDb+"."+tablename+" on modulerecid="+primarykey +" where companyid=? ) unitables group by modulerecid ";
                         pstmt8 = conn.prepareStatement(query);
                         pstmt8.setString(1,companyid);
                         pstmt8.setString(2,companyid);
                         rs8 = pstmt8.executeQuery();
                         while (rs8.next()) {
                         //String modulerecordid = rs8.getString(primarykey);
                         String modulerecordid = rs8.getString("modulerecid");
                         pstmt1 = conn.prepareStatement("select modulerecid,fieldparamid,fieldvalue from " + toDb + ". "+modulename+"cstm inner join  "+toDb+"."+tablename+" on modulerecid="+primarykey +" where companyid=? and modulerecid=?  " );
                         pstmt1.setString(1,companyid);
                         pstmt1.setString(2,modulerecordid);
                         rs1 = pstmt1.executeQuery();
                         int currentrecid=0;
                         JSONArray jarray = new JSONArray();
                         JSONObject jobj = new JSONObject();
                         int count1=0;
                         while (rs1.next()) {
                           
                                currentrecid = Integer.parseInt(rs1.getObject("fieldparamid").toString());

                               
                                        try {
                                            Integer xtype =0;
                                            String fieldvalue ="",currentid="",colnum="";
                                            if(FieldXtypeMap.containsKey(currentrecid)){
                                                jobj = new JSONObject();
                                                xtype =(Integer) FieldXtypeMap.get(currentrecid);
                                                fieldvalue =(String) rs1.getObject("fieldvalue"); // this is the old id for fieldcombodata
                                                currentid = String.valueOf(currentrecid);
                                                jobj.put(Constants.Crm_custom_field,currentid);
                                                colnum = FieldColMap.get(currentrecid).toString();
                                                jobj.put(currentid,colnum);
                                                jobj.put("xtype",xtype);
                                                if(xtype==4){
                                                        pstmt7 = conn.prepareStatement("select id from " + toDb + ".fieldcombodata where oldid=? " );
                                                        pstmt7.setInt(1,Integer.parseInt(fieldvalue));
                                                        rs7 = pstmt7.executeQuery();
                                                        String newfieldvalue = "";
                                                        while (rs7.next()) {
                                                            newfieldvalue = rs7.getString("id");
                                                        }
                                                        jobj.put(colnum,newfieldvalue);
                                                 }else{
                                                      jobj.put(colnum,fieldvalue);
                                                      // insert fieldvalue directly
                                                  }
                                               jarray.put(jobj);
                                            }

                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                            out.println(tablename+" Problem occured to create array of object for module record with id " +modulerecordid);
                                            out.println(ex.getMessage());
                                        }

                        }
                        if(rs1!=null){
                            rs1.close();
                        }
                        if(rs7!=null){
                            rs7.close();
                        }
                         // fetch values of field with xtype 7
                                    // and save record in modulecstmdata
                                         try {
                                        // query = "select modulerecid,fieldparamid,fieldvalue from " + toDb + ". "+modulename+"cstmmultiselect inner join  "+toDb+"."+tablename+" on modulerecid="+primarykey +" where companyid=?  and modulerecid=? " ;
                                         query = " select fc.id,multi.fieldparamid,multi.fieldvalue from " + toDb + ".fieldcombodata fc " +
                                                 " inner join " + toDb + ". "+modulename+"cstmmultiselect multi " +
                                                 " on multi.fieldvalue=fc.oldid  inner join  "+toDb+"."+tablename+" table1 on multi.modulerecid=table1."+primarykey +"  where table1.companyid=?  and multi.modulerecid=? order by multi.fieldparamid ";
                                         pstmt3 = conn.prepareStatement(query);
                                         pstmt3.setString(1,companyid);
                                         pstmt3.setString(2,modulerecordid);
                                         rs3 = pstmt3.executeQuery();
                                         String newfieldvalue = "";
                                         currentrecid=0;
                                         Integer previd=0;
                                         while (rs3.next()) {
                                             
                                             currentrecid = Integer.parseInt(rs3.getObject("fieldparamid").toString());
                                             if(previd!=currentrecid && previd!=0){
                                                 if(newfieldvalue!="" &&  FieldXtypeMap.containsKey(previd)){

                                                    jobj = new JSONObject();
                                                    Integer xtype =(Integer) FieldXtypeMap.get(previd);
                                                    // this is the old id for fieldcombodata
                                                    String currentid = String.valueOf(previd);
                                                    jobj.put(Constants.Crm_custom_field,currentid);
                                                    String colnum = FieldColMap.get(previd).toString();
                                                    jobj.put("xtype",xtype);
                                                    jobj.put(currentid,colnum);
                                                    jobj.put(colnum,newfieldvalue.substring(0,newfieldvalue.length()-1));
                                                    String refcolnum = FieldColMap.get(previd+"Ref").toString();
                                                    jobj.put("refcolumn_name",refcolnum);
                                                    jarray.put(jobj);
                                                    newfieldvalue ="";
                                                }
                                             }
                                             newfieldvalue =newfieldvalue+ rs3.getString("id")+",";
                                             previd=currentrecid;
                                         }
                                              if(newfieldvalue!="" && currentrecid!=0 && FieldXtypeMap.containsKey(currentrecid)){

                                                    jobj = new JSONObject();
                                                    Integer xtype =(Integer) FieldXtypeMap.get(currentrecid);
                                                    // this is the old id for fieldcombodata
                                                    String currentid = String.valueOf(currentrecid);
                                                    jobj.put(Constants.Crm_custom_field,currentid);
                                                    String colnum = FieldColMap.get(currentrecid).toString();
                                                    jobj.put("xtype",xtype);
                                                    jobj.put(currentid,colnum);
                                                    jobj.put(colnum,newfieldvalue.substring(0,newfieldvalue.length()-1));
                                                    String refcolnum = FieldColMap.get(currentrecid+"Ref").toString();
                                                    jobj.put("refcolumn_name",refcolnum);
                                                    jarray.put(jobj);
                                                    newfieldvalue ="";
                                                }
                                          

                                         }catch (Exception ex) {
                                            out.println(tablename+" Problem occured to create array of object for module record with id " +modulerecordid);
                                            ex.printStackTrace();
                                            out.println(ex.getMessage());
                                         }


                                         
                                         if(rs3!=null){
                                            rs1.close();
                                         }
                           if(jarray.length()>0){
                                    Boolean success1 = setcustomdata(jarray,moduleid,modulerecordid,companyid,out,conn,toDb);
                                    if(success1){

                                        String customtableref =  getCustomTableNameRef(moduleid);
                                        /*String for_query = " SET foreign_key_checks = 0 " ;
                                        String for_query1 = " SET foreign_key_checks = 1 " ;
                                        pstmt5 = conn.prepareStatement(for_query);
                                        pstmt5.executeQuery();
    */
                                        pstmt4 = conn.prepareStatement("update  "+toDb+"."+tablename+" set "+customtableref+ "=?  where "+primarykey+"=? ");
                                        pstmt4.setString(1,modulerecordid);
                                        pstmt4.setString(2,modulerecordid);
                                        int count = pstmt4.executeUpdate();
                                        commitcount=commitcount+1;
                                        if(commitcount>batchlimit){
                                            conn.commit();
                                            total+=commitcount;
                                            modulecount+=commitcount;
                                            commitcount=0;
                                        }
      /*                                  pstmt6 = conn.prepareStatement(for_query1);
                                        pstmt6.executeQuery();*/
                                      //  out.println(tablename+" Module Record updated with record id " +moduleprevrecid);
                                    }else{
                                        out.println(tablename+" NO custom data record exist for module record with id " +modulerecordid);
                                    }
                                }


                        }
                        }
                        if(commitcount>0){
                            conn.commit();
                            total+=commitcount;
                            modulecount+=commitcount;
                            out.println("'"+rs.getString("companyname")+"' => "+ tablename + " => "+modulecount);
                            modulecount=0;
                            commitcount=0;
                        }
                    }
                     if(success){
                         pstmt7 = conn.prepareStatement("update  "+toDb+".company"+" set migrationstatus=?  where companyid=? ");
                         pstmt7.setInt(1,total);
                         pstmt7.setString(2,rs.getString("companyid"));
                         int count = pstmt7.executeUpdate();
                         out.println("'"+rs.getString("companyname")+"' => "+ total);
                         totalcompany+=1;
                         total=0;
                         conn.commit();
                     }else{
                            out.println("Migration not completed successfully"+rs.getString("companyname")+"'");

                     }
                   
                    if(rs8!=null){
                        rs8.close();
                    }
                   
                    
                }
                //output.close();
                pstmt = conn.prepareStatement("select count(*) count from " + toDb + ".company where migrationstatus > ?  ");//and companyname=?
                pstmt.setInt(1,0);
                //pstmt.setString(2,"Demo");
                rs = pstmt.executeQuery();
                if(rs.next()){
                    out.println("Migrated total companys are => "+ rs.getInt("count"));
                }
                rs.close();

                out.println("Currently Migrated total companys are => "+ totalcompany);
                conn.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                success =false;
                out.println(ex.getMessage());
                conn.rollback();
            }
%>
<%!

    
   
    public Boolean setcustomdata(JSONArray jarray,Integer moduleid,String modulerecid,String companyid,JspWriter out,Connection conn,String todb)throws Exception{
        Boolean success = false;
        HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
        customrequestParams.put("customarray", jarray);
        customrequestParams.put("modulename",getModuleName(moduleid).toLowerCase());
        customrequestParams.put("moduleprimarykey", getPrimarycolumn(moduleid));
        customrequestParams.put("modulerecid", modulerecid);
        customrequestParams.put("companyid", companyid);
        customrequestParams.put("customdataclasspath", getmoduledataTableName(moduleid));
        int count = setCustomData(customrequestParams,conn,todb);
        if(count>0){
            success = true;
        }else{
            success =false;
         }
        return success;
            
    }
    public int setCustomData(HashMap<String, Object> customrequestParams,Connection conn,String todb) throws Exception {
        int count =0 ;
        boolean atleatonefield = false;
        JSONArray jarray = (JSONArray) customrequestParams.get("customarray");

        String modulename= (String) customrequestParams.get("modulename");
        String moduleprimarykey= (String) customrequestParams.get("moduleprimarykey");
        String modulerecid=(String) customrequestParams.get("modulerecid");
        String companyid =(String) customrequestParams.get("companyid");
        ArrayList params = new ArrayList();
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("Company", companyid);
        String colnames = moduleprimarykey+",company,";
        String values ="?,?,";
        if(moduleprimarykey.equals("078aebff-ce1c-488a-9d21-3b1e474b1328")){
                System.out.print("maatched");
        }
         params.add(modulerecid);
         params.add(companyid);
        for (int i = 0; i < jarray.length(); i++) {
                    JSONObject jobj = jarray.getJSONObject(i);
                    if(jobj.has(Constants.Crm_custom_field)){
                        String fieldname = jobj.getString(Constants.Crm_custom_field);
                        String fielddbname = jobj.getString(fieldname);
                        String fieldValue = jobj.getString(fielddbname);
                        atleatonefield = true;
                        if(!StringUtil.isNullOrEmpty(fieldValue) && !fieldValue.equalsIgnoreCase(Constants.field_data_undefined) && !requestParams.containsKey(fielddbname)){
                            requestParams.put(fielddbname, String.valueOf(fieldValue));
                            colnames += fielddbname+",";
                            params.add(fieldValue);
                            values += "?,";
                            
                            Integer xtype = Integer.parseInt(jobj.getString("xtype"));
                            if(xtype==7 && jobj.has("refcolumn_name")){
                                    String reffielddbname = jobj.getString("refcolumn_name");
                                    if(!StringUtil.isNullOrEmpty(reffielddbname)){
                                        colnames += reffielddbname+",";
                                        params.add(String.valueOf(fieldValue.split(Constants.Custom_Column_Sep)[0]));
                                        values += "?,";
                                    }
                            }
                        }
                    }
        }
        if(atleatonefield){
            String query ="";
            try{
            colnames = colnames.substring(0,colnames.length()-1);
            values = values.substring(0,values.length()-1);
            query = " insert into "+todb+".crm"+modulename+"customdata ("+colnames+") values("+values+") ";
            PreparedStatement pstmt = conn.prepareStatement(query);
            for(int j=0;j<params.size();j++){
                pstmt.setString(j+1,params.get(j).toString());
            }
            count = pstmt.executeUpdate();
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Error occured while executing query "+query + " Parameters are "+ params);
            }
        }

        return count;
    }
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
