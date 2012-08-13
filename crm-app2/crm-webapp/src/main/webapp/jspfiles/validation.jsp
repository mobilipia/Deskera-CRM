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
<%@page import="com.krawler.spring.common.KwlReturnObject"%>
<%@page import="com.krawler.spring.sessionHandler.sessionHandlerImpl"%>
<%@page import="org.hibernate.Session"%>
<%@page import="com.krawler.esp.hibernate.impl.HibernateUtil"%>
<%@page import="com.krawler.common.admin.ColumnHeader"%>
<%@page import="com.krawler.common.admin.DefaultHeader"%>
<%@page import="org.hibernate.SQLQuery"%>
<%@page import="org.hibernate.*"%>
<%
boolean successFlag = false;
        KwlReturnObject result = null;
        String module = "";
        String modulestr = "Account,Case,Contact,Lead,Opportunity,Product";
        String[] modulearr = modulestr.split(",");
        String companyid = "";
        String companyname = "";
        Session session1 = null;
        Session session2 = null;

            try{
            session2=HibernateUtil.getCurrentSession();
            String query = "select companyid,companyname from company";
            SQLQuery sql = session2.createSQLQuery(query);
            List list1 = sql.list();
            for (int i = 0; i < list1.size(); i++) {
                Object [] companyobj = (Object []) list1.get(i);
                companyid = companyobj[0].toString();
                companyname = companyobj[1].toString();
            for (int j = 0; j < modulearr.length; j++) {
                try {
                    session1=HibernateUtil.getCurrentSession();
                    Transaction tx = session1.beginTransaction();
                    module = modulearr[j];
                    HashMap<String,Object> requestParams = new HashMap<String, Object>();
                    ArrayList filter_names = new ArrayList();
                    ArrayList filter_values = new ArrayList();

                    filter_names.add("defaultheader.moduleName");
                    filter_values.add(module);

                    filter_names.add("mandotory");
                    filter_values.add(true);

                    filter_names.add("company.companyID");
                    filter_values.add(companyid);

                    requestParams.put("filter_names", filter_names);
                    requestParams.put("filter_params", filter_values);

                    crmCommonDAOImpl comimp = new crmCommonDAOImpl();
                    result = comimp.getColumnHeader(session1, requestParams);
                    List list = result.getEntityList();
                    Iterator itr = list.iterator();
                    String tableid = "";
                    String tablecstm = "";
                    String table = "";
                    if(module.equals("Account")){
                        tableid = "account";
                        tablecstm = "account";
                        table = "account";
                    }
                    else if(module.equals("Campaign")){
                        tableid = "CrmCampaign";
                    }
                    else if(module.equals("Case")){
                        tableid = "case";
                        tablecstm = "cases";
                        table = "case";
                    }
                    else if(module.equals("Contact")){
                        tableid = "contact";
                        tablecstm = "contact";
                        table = "contact";
                    }
                    else if(module.equals("Lead")){
                        tableid = "lead";
                        tablecstm = "lead";
                        table = "lead";
                    }
                    else if(module.equals("Opportunity")){
                        tableid = "opp";
                        tablecstm = "opportunity";
                        table = "opportunity";
                    }
                    else if(module.equals("Product")){
                        tableid = "product";
                        tablecstm = "product";
                        table = "product";
                    }

                    String str = "update crm_"+table+" set validflag=0 where companyid='"+companyid+"' and validflag=1 ";
                    String str1 = "update crm_"+table+" set validflag=1 where companyid='"+companyid+"' and validflag=0 ";

                    String selectdef = "";
                    String selectdef1 = "";
                    String selectcstm = "";
                    String selectcstm1 = "";
                    String filterquery = "";
                    String filterquery1 = "";
                    ArrayList params = new ArrayList();
                    int ctr = 0;

                    while(itr.hasNext()){
                        ColumnHeader ch = (ColumnHeader) itr.next();
                        if(!ch.getDefaultheader().isCustomflag()){
                            selectdef += " ifnull("+ch.getDefaultheader().getDbcolumnname() +",'')='' or " + ch.getDefaultheader().getDbcolumnname() +"='' or ";
                            selectdef1 += " ifnull("+ch.getDefaultheader().getDbcolumnname() +",'')!='' and " + ch.getDefaultheader().getDbcolumnname() +"!='' and ";
                        }else{
                             if(ch.getDefaultheader().getXtype().equals("7")){
                                selectcstm += " or "+tableid+"id not in(select modulerecid from "+tablecstm+"cstmmultiselect where fieldparamid='"+ch.getDefaultheader().getPojoheadername()+"') ";
                                selectcstm1 += " and "+tableid+"id in (select modulerecid from "+tablecstm+"cstmmultiselect where fieldparamid='"+ch.getDefaultheader().getPojoheadername()+"') ";
                            }else{
                                selectcstm += " or "+tableid+"id not in(select modulerecid from "+tablecstm+"cstm where fieldparamid='"+ch.getDefaultheader().getPojoheadername()+"') ";
                                selectcstm1 += " and "+tableid+"id in (select modulerecid from "+tablecstm+"cstm where fieldparamid='"+ch.getDefaultheader().getPojoheadername()+"') ";
                            }
                            ctr++;
                        }
                    }

                    requestParams = new HashMap<String, Object>();
                    requestParams.put("companyid", companyid);
                    requestParams.put("moduleName", module);
                    result = comimp.getMandatoryDefaultColumnHeader(session1, requestParams);
                    list = result.getEntityList();
                    itr = list.iterator();
                    while(itr.hasNext()){
                        DefaultHeader dh = (DefaultHeader) itr.next();
                        if(!StringUtil.isNullOrEmpty(dh.getDbcolumnname())){ // Account Owner is empty
                            selectdef += " ifnull("+dh.getDbcolumnname() +",'')='' or " + dh.getDbcolumnname() +"='' or ";
                            selectdef1 +=" ifnull("+ dh.getDbcolumnname() +",'')!='' and " + dh.getDbcolumnname() +"!='' and ";
                        }
                    }
                    if(selectdef.length()>0){
                        selectdef = selectdef.substring(0, selectdef.length()-3);
                        filterquery += selectdef;
                    }
                    filterquery += selectcstm;
                    if(filterquery.length()>0){
                         str += " and ("+filterquery+")" ;
                    }


                    if(selectdef1.length()>0){
                        selectdef1 = selectdef1.substring(0, selectdef1.length()-4);
                         filterquery1 += selectdef1;
                    }
                    filterquery1 += selectcstm1;
                    if(filterquery1.length()>0){
                         str1 += " and ("+filterquery1+")" ;
                    }

                    str += ";";
                    str1 += ";";
                    out.println(str);
                    out.println(str1);
                    
                    tx.commit();
                } catch (Exception ex) {
                    out.println(ex.toString());
                }finally{
                    HibernateUtil.closeSession(session1);
                }

            }
            }
            }catch (Exception ex) {
                out.println(ex.toString());
            }finally{
                HibernateUtil.closeSession(session2);
            }
                
            

        
%>
