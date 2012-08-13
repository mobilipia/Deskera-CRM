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
<%@page import="com.krawler.spring.crm.common.crmCommonDAOImpl"%>
<%@page import="com.krawler.common.service.ServiceException"%>
<%@page import="org.hibernate.SQLQuery"%>
<%@page import="org.hibernate.*"%>
<%@page import="com.krawler.common.util.KWLErrorMsgs"%>
<%@page import="com.krawler.crm.utils.Constants"%>
<%!

 public KwlReturnObject getColumnHeader(Session session,HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = null;
            String Hql = "from ColumnHeader c ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "where");
                Hql += filterQuery;
            }
            ll = HibernateUtil.executeQuery(session,Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
 public KwlReturnObject getMandatoryDefaultColumnHeader(Session session,HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = new ArrayList();
            String Hql = "from DefaultHeader c where c.mandatory = ? and c.moduleName = ? ";
            filter_params.add(true);
            filter_params.add(requestParams.get("moduleName").toString());
            Hql+=" and c.id not in ( select ch.defaultheader.id from ColumnHeader ch where ch.company.companyID = ? and ch.defaultheader.moduleName = ? ) ";
            Hql+=" and c.pojoheadername not in ( select  fh.id from FieldParams fh where fh.company.companyID != ?  ) ";
            filter_params.add(requestParams.get("companyid").toString());
            filter_params.add(requestParams.get("moduleName").toString());
            filter_params.add(requestParams.get("companyid").toString());
            ll = HibernateUtil.executeQuery(session, Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getMandatoryDefaultColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
public KwlReturnObject validaterecordsHB(String module, String companyid, Session session) throws ServiceException {
        boolean successFlag = false;
        KwlReturnObject result = null;
        List ll = null;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_values = new ArrayList();
            String multiselecttable = "";
            filter_names.add("defaultheader.moduleName");
            filter_values.add(module);

            filter_names.add("mandotory");
            filter_values.add(true);

            filter_names.add("company.companyID");
            filter_values.add(companyid);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_values);

            result = getColumnHeader(session,requestParams);
            List list = result.getEntityList();
            Iterator itr = list.iterator();
            String tableid = "";
            String tablecstm = "";
            String tablecstmref = "";
            String table = "";
            String tablealias = "maintable";
            if(module.equals("Account")){
                tableid = Constants.Crm_accountid;
                tablecstm = Constants.Crm_account_custom_data_pojo;
                tablecstmref = Constants.Crm_account_pojo_ref;
                table = Constants.Crm_account_pojo;
                multiselecttable =Constants.Crm_account_product_pojo_ref;
            }
            else if(module.equals("Campaign")){
                tableid = Constants.Crm_campaignid;
                table = Constants.Crm_campaign_pojo;
            }
            else if(module.equals("Case")){
                tableid = Constants.Crm_caseid;
                tablecstm = Constants.Crm_case_custom_data_pojo;
                table = Constants.Crm_case_pojo;
                tablecstmref = Constants.Crm_case_pojo_ref;
                multiselecttable= Constants.Crm_case_productpojo_ref;
            }
            else if(module.equals("Contact")){
                tableid = Constants.Crm_contactid;
                tablecstm = Constants.Crm_contact_custom_data_pojo;
                tablecstmref = Constants.Crm_contact_pojo_ref;
                table = Constants.Crm_contact_pojo;
            }
            else if(module.equals("Lead")){
                tableid = Constants.Crm_leadid;
                tablecstm = Constants.Crm_lead_custom_data_pojo;
                table = Constants.Crm_lead_pojo;
                tablecstmref = Constants.Crm_lead_pojo_ref;
                multiselecttable= Constants.Crm_lead__productpojo_ref;
            }
            else if(module.equals("Opportunity")){
                tableid = Constants.Crm_opportunityid;
                tablecstm = Constants.Crm_opportunity_custom_data_pojo;
                table = Constants.Crm_opportunity_pojo;
                tablecstmref = Constants.Crm_opportunity_pojo_ref;
                multiselecttable = Constants.Crm_opportunity_product_pojo_ref;
            }
            else if(module.equals("Product")){
                tableid = Constants.Crm_productid;
                tablecstm = Constants.Crm_product_custom_data_pojo;
                table = Constants.Crm_product_pojo;
                tablecstmref = Constants.Crm_product_pojo_ref;
            }

            String str1 = " update "+table+"  set validflag=? ";
            String str = " update "+table+"  set validflag=?  where company.companyID=? and  validflag=?  ";
            String wherecondition = " where company.companyID=? and  validflag=?  ";

            String cstmdataquery = " and " +  tableid+" in ( select "+ tableid+" from " +tablecstm + "  where company.companyID=?  ";
            String cstmdataquery1 = " or " + tablecstmref + " is null or " +  tableid+" in ( select "+ tableid+" from " +tablecstm + "  where company.companyID=?  ";
            String cstmdatawherecondition = "",cstmdatawherecondition1 = "";

            String productcondition= "",productcondition1= "";
            boolean productc = true;

            String cstmdatacondition= "",cstmdatacondition1= "";
            String maincondition = "",maincondition1="";
            ArrayList params = new ArrayList();
            params.add(0);
            params.add(companyid);
            params.add(1);

            while(itr.hasNext()){
                ColumnHeader ch = (ColumnHeader) itr.next();
                DefaultHeader dh = ch.getDefaultheader();
                if(!dh.isCustomflag() && !StringUtil.isNullOrEmpty(dh.getPojoheadername())){
                        String dbcolname = dh.getPojoheadername();
                         if((dh.getXtype().equals("4") || dh.getXtype().equals("7")) && !StringUtil.isNullOrEmpty(dh.getValidateType()) && dh.getValidateType().equals("multiselect")) {
                            if(productc){
                              productcondition += tableid + " in ( select " + tableid + "." + tableid + " from " + multiselecttable +  " ) and ";
                              productcondition1 += tableid + " not in ( select " + tableid + "." + tableid + " from " + multiselecttable +" ) or ";
                              productc = false;
                             }
                         } else{
                            maincondition += " ifnull("+ dbcolname+",'')!='' and " +  dbcolname+"!='' and ";
                            maincondition1 += " ifnull("+ dbcolname+",'')='' or " +  dbcolname+"='' or ";
                         }
                }else{
                    String dbcolname = dh.getDbcolumnname();
                    if (!StringUtil.isNullOrEmpty(dbcolname)) {
                            dbcolname = dbcolname.toLowerCase();
                            cstmdatacondition += " " + dbcolname + " is not null and " + dbcolname + "!='' and ";
                            cstmdatacondition1 += " " + dbcolname + " is null or " + dbcolname + "='' or ";
                    }
                }
            }

            requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", module);
            result = getMandatoryDefaultColumnHeader(session,requestParams);
            list = result.getEntityList();
            itr = list.iterator();
            while(itr.hasNext()){
                DefaultHeader dh = (DefaultHeader) itr.next();
                if (!dh.isCustomflag()) {
                    String dbcolname = dh.getPojoheadername();
                    if ((dh.getXtype().equals("4") || dh.getXtype().equals("7")) && !StringUtil.isNullOrEmpty(dh.getValidateType()) && dh.getValidateType().equals("multiselect")) {
                        if(productc){
                        productcondition += tableid + " in ( select " + tableid + "." + tableid + " from " + multiselecttable + " where "+tableid + ".company.companyID=?" + " ) and ";
                        productcondition1 += tableid + " not in ( select " + tableid + "." + tableid + " from " + multiselecttable + " ) or ";
                        productc = false;
                        }
                    } else if (!StringUtil.isNullOrEmpty(dbcolname)) {
                        maincondition += " ifnull(" + dbcolname + ",'')!='' and " + dbcolname + "!='' and ";
                        maincondition1 += " ifnull(" + dbcolname + ",'')='' or " + dbcolname + "='' or ";
                    }
                }else{
                    String dbcolname = dh.getDbcolumnname();
                    if (!StringUtil.isNullOrEmpty(dbcolname)) {
                            dbcolname = dbcolname.toLowerCase();
                            cstmdatacondition += " " + dbcolname + " is not null and " + dbcolname + "!='' and ";
                            cstmdatacondition1 += " " + dbcolname + " is null or " + dbcolname + "='' or ";
                    }
                }
            }

            if(maincondition.length()>0){
                maincondition = maincondition.substring(0, maincondition.length()-4);
                wherecondition += " and " + maincondition;
            }

            if(cstmdatacondition.length()>0){
                cstmdatacondition = cstmdatacondition.substring(0, cstmdatacondition.length()-4);
                cstmdatawherecondition += " and "+ cstmdatacondition ;
            }
            if(productcondition.length()>0){
                productcondition = productcondition.substring(0, productcondition.length()-4);
                productcondition = " and ( "+ productcondition + " ) ";
            }
            if(cstmdatawherecondition.length()>0) {
                params.add(companyid);
                cstmdataquery += cstmdatawherecondition + " ) ";
            } else {
                cstmdataquery =" ";
            }
            str1+= wherecondition + cstmdataquery +  productcondition;



            if(maincondition1.length()>0){
                maincondition1 = maincondition1.substring(0, maincondition1.length()-3);

            }

            if(cstmdatacondition1.length()>0){
                cstmdatacondition1 = cstmdatacondition1.substring(0, cstmdatacondition1.length()-3);
                cstmdatawherecondition1 += " and ( "+ cstmdatacondition1 + " ) " ;
            }
            if(productcondition1.length()>0){
                productcondition1 = productcondition1.substring(0, productcondition1.length()-3);
                productcondition1 = " or ( "+ productcondition1 + " ) " ;
            }
            if(cstmdatawherecondition1.length()>0) {
                if(cstmdatawherecondition.length()==0) {
                    params.add(companyid);
                }
                cstmdataquery1 += cstmdatawherecondition1 + " ) ";
            } else {
                cstmdataquery1 = " ";
            }
            str+= " and ( " +  maincondition1 + cstmdataquery1 +  productcondition1 + " ) ";
            ll=new ArrayList();
            int num1 = HibernateUtil.executeUpdate(session, str,params.toArray());
            ll.add(num1);
//            params.clear();
            params.set(0,1);
            params.set(2,0);
            /* subquery params */
            num1 = HibernateUtil.executeUpdate(session, str1,params.toArray());
            ll.add(num1);
            
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.validaterecordsHB", ex);
        }
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", ll, result.getRecordTotalCount());
    }

%>
<%
            boolean successFlag = false;
            KwlReturnObject result = null;
            String module = "";
            String modulestr = "Account,Case,Contact,Lead,Opportunity,Product";
            String[] modulearr = modulestr.split(",");
            String companyid = "";
            String companyname = "";
            Session session2 = null;
            Transaction htc = null;
            try {
                session2 = HibernateUtil.getCurrentSession();
                String query = "select companyid,companyname from company";
                SQLQuery sql = session2.createSQLQuery(query);
                List list1 = sql.list();
                for (int i = 0; i < list1.size(); i++) {
                    Object[] companyobj = (Object[]) list1.get(i);
                    companyid = companyobj[0].toString();
                    companyname = companyobj[1].toString();
                    out.println("validating data for company " + companyname);
                    for (int j = 0; j < modulearr.length; j++) {
                        module = modulearr[j];
                        out.println("validating data for module " + module);
                                htc = session2.getTransaction();
                                session2.beginTransaction();
                                try {
                                    KwlReturnObject kl = validaterecordsHB(module, companyid, session2);
                                    List ll =kl.getEntityList();
                                    out.println("Record updated from 1 to 0 of " + module + " are "+ll.get(0));
                                    out.println("Record updated from 0 to 1 of " + module + " are "+ll.get(1));
                                    htc.commit();
                                } catch (ServiceException ex) {
                                    out.println("validation for module " + module + " failed ");
                                    htc.rollback();
                                } catch (Exception e) {
                                    out.println("validation for module " + module + " failed ");
                                    htc.rollback();
                                } finally {
                                    session2.flush();
                                }
                    }
                    out.println("validation for company " + companyname + " completed successfully ");
                }
            } catch (Exception ex) {
                out.println(ex.toString());
                out.println("validation for company " + companyname + " failed ");
            } finally {
                HibernateUtil.closeSession(session2);
            }
%>
