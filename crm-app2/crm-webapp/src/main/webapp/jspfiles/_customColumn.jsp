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
<%@page import="com.krawler.customFieldMaster.*"%>
<%@page import="java.util.*"%>
<%@page import="com.krawler.utils.json.base.*"%>
<%@page import="com.krawler.common.util.StringUtil"%>
<%@page import="com.krawler.esp.handlers.*"%>
<%@page import="com.krawler.esp.hibernate.impl.HibernateUtil"%>
<%@page import="com.krawler.spring.crm.common.crmCommonDAOImpl"%>
<%@page import="org.hibernate.*"%>
<%@page import="com.krawler.spring.sessionHandler.sessionHandlerImpl"%>

<%

String mode = request.getParameter("mode");
if(com.krawler.common.util.StringUtil.isNullOrEmpty(mode)){
    ArrayList list = null;
    if(request.getParameter("fetchtype") == "0"){
list =  fieldManager.getColumnModel(request.getParameter("moduleid"),sessionHandlerImpl.getCompanyid(request));
}else{
    list =  fieldManager.getColumnModel(sessionHandlerImpl.getCompanyid(request));
}
    JSONObject jresult = new JSONObject();
    for(int i=0;i<list.size();i++){
     Object[] item = (Object[])list.get(i);
     JSONObject jobj = new JSONObject();
     jobj.put("fieldname", item[0]);
     jobj.put("fieldlabel", item[1]);
     jobj.put("isessential", item[2]);
     jobj.put("maxlength", item[3]);
     jobj.put("validationtype", item[4]);
     jobj.put("fieldid", item[5]);
     jobj.put("fieldtype", item[6]);
     jobj.put("iseditable", item[7]);
     jobj.put("comboid", item[8]);
     jobj.put("comboname", item[9]);
     jobj.put("moduleflag", item[10]);
     jobj.put("moduleid", item[11]);
     jresult.append("data",jobj);
    }
    jresult.put("valid",true);
    out.print(jresult);
}else if(mode.equals("1")){
    String retMsg = "";
    String fieldlabel = request.getParameter("fieldlabel");
    String moduleid = request.getParameter("moduleid");
    String formulae = request.getParameter("rules");
    String editable = request.getParameter("iseditable");
    String companyid = sessionHandlerImpl.getCompanyid(request);
    if(Boolean.parseBoolean(request.getParameter("createFieldFlag"))) {
        String maxlength = request.getParameter("maxlength");
        if(maxlength.equals("")) {
            maxlength = "12";
        }
        String validationtype = "0";
        if(validationtype.equals(""))
            validationtype = "0";
        String fieldtype = request.getParameter("fieldType");
        String isessential = request.getParameter("isessential");
        String customregex = request.getParameter("customregex");
        String combodata = request.getParameter("combodata");
        int essential = 0;
        if(!com.krawler.common.util.StringUtil.isNullOrEmpty(isessential) && isessential.equals("false")) {
            essential = 0;
        } else if(!com.krawler.common.util.StringUtil.isNullOrEmpty(isessential)){
            essential = 1;
        }
        Integer[] successflag = new Integer[1];
        successflag[0] = 0;
        retMsg = fieldManager.createNewField(fieldlabel, essential, Integer.parseInt(maxlength), Integer.parseInt(validationtype), Integer.parseInt(fieldtype), Integer.parseInt(moduleid), customregex, companyid,combodata, formulae, editable, request,successflag);
        
        String moduleName = "";
        if(Integer.parseInt(moduleid) == 1) {//Account
            moduleName = "Account";
        }else if(Integer.parseInt(moduleid) == 2) {//Lead
            moduleName = "Lead";
        }else if(Integer.parseInt(moduleid) == 6) {//Contact
            moduleName = "Contact";
        }else if(Integer.parseInt(moduleid) == 5) {
            moduleName = "Opportunity";
        }else if(Integer.parseInt(moduleid) == 4) {
            moduleName = "Product";
        }else if(Integer.parseInt(moduleid) == 3) {
            moduleName = "Case";
        }
        if(!StringUtil.isNullOrEmpty(moduleName)&&successflag[0]==1) {
            Session session1=null;
            try{
                session1=HibernateUtil.getCurrentSession();
                Transaction tx = session1.beginTransaction();
                tx = session1.beginTransaction();
                crmCommonDAOImpl obj = new crmCommonDAOImpl();
                    obj.validaterecordsjsp(moduleName, companyid, session1);

                tx.commit();
            }catch(Exception e){
                //retMsg = "Error occured while validating records.";
            }finally{
                HibernateUtil.closeSession(session1);
            }
        }
        
    } else {
        if(!StringUtil.isNullOrEmpty(formulae)) {
            Session session1=null;
            try{
                session1=HibernateUtil.getCurrentSession();
                Transaction tx = session1.beginTransaction();
                tx = session1.beginTransaction();
                retMsg = fieldManager.setCustomColumnFormulae(session1,request);
                tx.commit();
            }catch(Exception e){
                retMsg="{success:false,valid:true,data:{msg:'Custom column formula cannot be set.'}}";
            }finally{
                HibernateUtil.closeSession(session1);
            }
        }
    }
    
    out.print(retMsg);

}else if(mode.equals("2")){//Load combo data
    String fieldid = request.getParameter("fieldid");
    String flag = request.getParameter("flag"); //flag added to send requestex response
    ArrayList list1 = fieldManager.getComboData(fieldid,request.getSession().getAttribute("companyid").toString());
    com.krawler.utils.json.base.JSONObject jobj1 = new com.krawler.utils.json.base.JSONObject();
    for(int cnt=0;cnt<list1.size();cnt++){
    com.krawler.utils.json.base.JSONObject jobjTemp = new com.krawler.utils.json.base.JSONObject();
         Object[] item = (Object[])list1.get(cnt);
                jobjTemp.put("id", item[0]);
                jobjTemp.put("name", item[1]);
                jobj1.append("data", jobjTemp);
    }
    com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
    String str = jobj1.toString();
    if(flag!=null&&flag.equals("1")){
        jobj.put("valid", true);
        jobj.put("data", jobj1);
        str = jobj.toString();
    }
    out.print(str);
}else if(mode.equals("3")){// Delete Custom Column
    String retMsg = "";
    String fieldid = request.getParameter("fieldid");
    String modulename = request.getParameter("modulename");
    String companyid = sessionHandlerImpl.getCompanyid(request);
    if(!StringUtil.isNullOrEmpty(fieldid)) {
        Session session1=null;
        try{
            session1=HibernateUtil.getCurrentSession();
            Transaction tx = session1.beginTransaction();
            tx = session1.beginTransaction();
             Integer[] successflag = new Integer[1];
            successflag[0] = 0;
            retMsg = fieldManager.deleteCustomColumn(session1,request,successflag);
            if(successflag[0]==1){
                crmCommonDAOImpl obj = new crmCommonDAOImpl();
                obj.validaterecordsjsp(modulename, companyid, session1);
            }
            tx.commit();
        }catch(Exception e){
            retMsg = "Custom column cannot be deleted.";
        }finally{
            HibernateUtil.closeSession(session1);
        }
    }
    out.print("{success:true,valid:true,data:{msg:'"+retMsg+"'}}");
}else if(mode.equals("4")){//Load reference module combo
    com.krawler.utils.json.base.JSONObject jobj1 = new com.krawler.utils.json.base.JSONObject();
    try{
        ArrayList list1 = fieldManager.getModuleComboNames();
        for(int cnt=0;cnt<list1.size();cnt++){
        com.krawler.utils.json.base.JSONObject jobjTemp = new com.krawler.utils.json.base.JSONObject();
             Object[] item = (Object[])list1.get(cnt);
                    jobjTemp.put("id", item[0]);
                    jobjTemp.put("name", item[1]);
                    jobjTemp.put("moduleflag", item[2]);
                    jobj1.append("data", jobjTemp);
        }
    } catch(Exception e) {

    } finally {
      out.print(jobj1.toString());
    }
}
%>
