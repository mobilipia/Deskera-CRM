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
package com.krawler.customFieldMaster;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.admin.CustomColumnFormulae;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.FieldComboData;
import com.krawler.common.admin.FieldParams;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.LeadConversionMappings;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;



/**
 * 
 * @author krawler
 */
public class fieldManagerDAOImpl extends BaseDAO implements fieldManagerDAO
{
    private class CrmAllHeaderNameComparator implements Comparator<Object> {
        @Override
        public int compare(Object row1, Object row2) {
            DefaultHeader obj1 = (DefaultHeader ) row1;
            DefaultHeader obj2 = (DefaultHeader ) row2;
            String header1=null;
            String header2=null;
            try {
                header1 = obj1.getDefaultHeader().toString();
                header2 = obj2.getDefaultHeader().toString();
            } catch (Exception ex) {
                Logger.getLogger(fieldManagerDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            return header1.compareTo(header2);
        }
    }

    private class CrmFieldSequenceComparator implements Comparator<Object> {
        @Override
        public int compare(Object row1, Object row2) {
            DefaultHeader obj1 = (DefaultHeader ) row1;
            DefaultHeader obj2 = (DefaultHeader ) row2;
            Integer fseq1=0;
            Integer fseq2=0;
            try {
                fseq1 = obj1.getFieldsequence();
                fseq2 = obj2.getFieldsequence();
            } catch (Exception ex) {
                Logger.getLogger(fieldManagerDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(StringUtil.isNullObject(fseq1)){
                fseq1 =99;
            }
            if(StringUtil.isNullObject(fseq2)){
                fseq2 =99;
            }
            return fseq1.compareTo(fseq2);
        }
    }
    public KwlReturnObject getColumnHeader(HashMap<String, Object> requestParams) throws ServiceException
    {
        int dl = 0;
        List ll = null;
        try
        {
            ArrayList filter_params = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            String Hql = "from ColumnHeader c ";
            if (requestParams.containsKey("filter_params"))
            {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "where");
                int ind = filterQuery.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                    filterQuery = filterQuery.replace("("+index+")", "("+filter_params.get(index).toString()+")");
                    filter_params.remove(index);
                }
                Hql += filterQuery;
            }
            
            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                Hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, filter_params);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>)requestParams.get("order_type"));
                Hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }

            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex)
        {
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManagerDAOImpl.getColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public List<Object[]> getColumnHeader(String companyId, List<String> headerIds) {
        List<Object[]> results = null;
        try
        {
            StringBuilder hql = new StringBuilder();
            StringBuilder headerStr = new StringBuilder();
            if (headerIds == null || headerIds.isEmpty())
            {
                return null;
            }

            for (String id : headerIds)
            {
                headerStr.append('\'').append(id).append("\',");
            }

            headerStr.deleteCharAt(headerStr.length() - 1);

            hql.append("select c.defaultheader, c from  ColumnHeader c where c.defaultheader.id in (").append(headerStr).append(") ").append("and c.company.companyID = '").append(companyId).append('\'');

            results = executeQuery(hql.toString());

        } catch (Exception ex)
        {
            logger.warn("ex", ex);
        }
        return results;
    }

    public KwlReturnObject getDefaultHeader(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        ArrayList name = null;
        String hql = "";
        String companyid="";
        String moduleName="";
        ArrayList value = null;
        List defaultList = null;
        List customList = null;
        List headerList = new ArrayList();
        int headerCount=0;
        boolean success= false;
        try {
            boolean fetchAutoNoCustomField = false;
            if(requestParams.containsKey("companyid") &&requestParams.get("companyid")!=null){
                companyid = requestParams.get("companyid").toString();
            }
            if(requestParams.containsKey("moduleName")&&requestParams.get("moduleName")!=null){
                moduleName = requestParams.get("moduleName").toString();
            }
            if(requestParams.containsKey("fetchautonofield")&&requestParams.get("fetchautonofield")!=null){
                fetchAutoNoCustomField = (Boolean) requestParams.get("fetchautonofield");
            }

            hql = " from  DefaultHeader dh " ;
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
                 int ind = hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(hql.substring(ind+1,ind+2));
                    hql = hql.replace("("+index+")", "("+value.get(index).toString()+")");
                    value.remove(index);
                }
            }


            if(!StringUtil.isNullOrEmpty(companyid)){
                hql+= " and dh.id not in ( select ch.defaultheader from ColumnHeader ch where ch.company.companyID=? and ch.moduleName=? )";
                value.add(companyid);
                value.add(moduleName);
                try{
                    defaultList = executeQuery(hql, value.toArray());
                    int defaultCount = defaultList.size();
                    success= true;
                } catch(Exception e) {
                    e.printStackTrace();
                    success= false;
                }
                hql = "from DefaultHeader dh where dh.id in ( select ch.defaultheader from  ColumnHeader ch where ch.company.companyID=? and ch.moduleName=? )";
                value.clear();
                value.add(companyid);
                value.add(moduleName);
                if(!fetchAutoNoCustomField) {
                    hql+= " and dh.allowImport = ?";
                    value.add(true);
                }
                try{
                    customList = executeQuery(hql, value.toArray());
                    int customCount = customList.size();
                    success= true;
                } catch(Exception e) {
                    success= false;
                    e.printStackTrace();
                }

                headerList.addAll(defaultList);
                headerList.addAll(customList);
                headerCount= headerList.size();

            } else {
                
                headerList = executeQuery(hql, value.toArray());
           }

           if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
               String orderType = requestParams.get("order_type").toString();

               if(StringUtil.equal(orderType, "defaultHeader")){

                   Collections.sort(headerList,new CrmAllHeaderNameComparator());

               } else if(StringUtil.equal(orderType, "fieldsequence")){

                   Collections.sort(headerList,new CrmFieldSequenceComparator());
               }
               
           }
           
           result = new KwlReturnObject(success, "", "-1", headerList, headerCount);
        } catch (Exception ex) {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally {
            return result;
        }
    }

    public KwlReturnObject getMandatoryDefaultColumnHeader(HashMap<String, Object> requestParams) throws ServiceException
    {
        int dl = 0;
        List ll = null;
        try
        {
            ArrayList filter_params = new ArrayList();
            String Hql = "from DefaultHeader c where c.mandatory = ? and c.moduleName = ? ";
            filter_params.add(true);
            filter_params.add(requestParams.get("moduleName").toString());
            Hql += " and c.id not in ( select ch.defaultheader.id from ColumnHeader ch where  ch.company.companyID = ? and ch.defaultheader.moduleName = ? ) ";
            Hql += " and c.pojoheadername not in ( select  fh.id from FieldParams fh where fh.company.companyID != ?  ) ";

            filter_params.add(requestParams.get("companyid").toString());
            filter_params.add(requestParams.get("moduleName").toString());
            filter_params.add(requestParams.get("companyid").toString());
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex)
        {
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManagerDAOImpl.getMandatoryDefaultColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public List getCustomData(HashMap<String, Object> requestParams) throws ServiceException
    {
        List ll = null;
        try
        {
            ArrayList filter_params = new ArrayList();
            String Hql = (String) requestParams.get("hql");
            filter_params.add(requestParams.get("deleteflag"));
            filter_params.add(requestParams.get("companyid"));
            filter_params.add("%" + requestParams.get("id") + "%");
            ll = executeQuery(Hql, filter_params.toArray());
        } catch (Exception ex)
        {
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManagerDAOImpl.getCustomData", ex);
        }
        return ll;
    }

    public KwlReturnObject validaterecordsHB(String module, String companyid, Integer validflag) throws ServiceException
    {
        boolean successFlag = false;
        KwlReturnObject result = null;
        try
        {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
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

            result = getColumnHeader(requestParams);
            List list = result.getEntityList();
            Iterator itr = list.iterator();
            String tableid = "";
            String tablecstm = "";
            String tablecstmref = "";
            String table = "";
            String tablealias = "maintable";
            if (module.equals("Account"))
            {
                tableid = Constants.Crm_accountid;
                tablecstm = Constants.Crm_account_custom_data_pojo;
                tablecstmref = Constants.Crm_account_pojo_ref;
                table = Constants.Crm_account_pojo;
                multiselecttable = Constants.Crm_account_product_pojo_ref;
            } else if (module.equals("Campaign"))
            {
                tableid = Constants.Crm_campaignid;
                table = Constants.Crm_campaign_pojo;
            } else if (module.equals("Case"))
            {
                tableid = Constants.Crm_caseid;
                tablecstm = Constants.Crm_case_custom_data_pojo;
                table = Constants.Crm_case_pojo;
                tablecstmref = Constants.Crm_case_pojo_ref;
                multiselecttable = Constants.Crm_case_productpojo_ref;
            } else if (module.equals("Contact"))
            {
                tableid = Constants.Crm_contactid;
                tablecstm = Constants.Crm_contact_custom_data_pojo;
                tablecstmref = Constants.Crm_contact_pojo_ref;
                table = Constants.Crm_contact_pojo;
            } else if (module.equals("Lead"))
            {
                tableid = Constants.Crm_leadid;
                tablecstm = Constants.Crm_lead_custom_data_pojo;
                table = Constants.Crm_lead_pojo;
                tablecstmref = Constants.Crm_lead_pojo_ref;
                multiselecttable = Constants.Crm_lead__productpojo_ref;
            } else if (module.equals("Opportunity"))
            {
                tableid = Constants.Crm_opportunityid;
                tablecstm = Constants.Crm_opportunity_custom_data_pojo;
                table = Constants.Crm_opportunity_pojo;
                tablecstmref = Constants.Crm_opportunity_pojo_ref;
                multiselecttable = Constants.Crm_opportunity_product_pojo_ref;
            } else if (module.equals("Product"))
            {
                tableid = Constants.Crm_productid;
                tablecstm = Constants.Crm_product_custom_data_pojo;
                table = Constants.Crm_product_pojo;
                tablecstmref = Constants.Crm_product_pojo_ref;
            } else if (module.equals("Target"))
            {
                tableid = Constants.Crm_targetid;
                table = Constants.Crm_target_pojo;
            }

            String str1 = " update " + table + "  set validflag=? ";
            String str = " update " + table + "  set validflag=?  where company.companyID=? and  validflag=?  ";
            String wherecondition = " where company.companyID=? and  validflag=?  ";

            String cstmdataquery = " and " + tableid + " in ( select " + tableid + " from " + tablecstm + "  where company.companyID=?  ";
            String cstmdataquery1 = " or " + tablecstmref + " is null or " + tableid + " in ( select " + tableid + " from " + tablecstm + "  where company.companyID=?  ";
            String cstmdatawherecondition = "", cstmdatawherecondition1 = "";

            String productcondition = "", productcondition1 = "";

            String cstmdatacondition = "", cstmdatacondition1 = "";
            String maincondition = "", maincondition1 = "";
            ArrayList params = new ArrayList();
            params.add(0);
            params.add(companyid);
            params.add(validflag);

            while (itr.hasNext())
            {
                ColumnHeader ch = (ColumnHeader) itr.next();
                DefaultHeader dh = ch.getDefaultheader();
                if (!dh.isCustomflag() && !StringUtil.isNullOrEmpty(dh.getPojoheadername()))
                {
                    String dbcolname = dh.getPojoheadername();
                    if ((dh.getXtype().equals("4") || dh.getXtype().equals("7")) && !StringUtil.isNullOrEmpty(dh.getValidateType()) && dh.getValidateType().equals("multiselect"))
                    {
                        productcondition += tableid + " in ( select " + tableid + "." + tableid + " from " + multiselecttable + " ) and ";
                        productcondition1 += tableid + " not in ( select " + tableid + "." + tableid + " from " + multiselecttable + " ) or ";
                    } else
                    {
                        maincondition += " ifnull(" + dbcolname + ",'')!='' and " + dbcolname + "!='' and ";
                        maincondition1 += " ifnull(" + dbcolname + ",'')='' or " + dbcolname + "='' or ";
                    }
                } else
                {
                    String dbcolname = dh.getDbcolumnname();
                    if (!StringUtil.isNullOrEmpty(dbcolname) && !dh.getConfigid().equals("1"))
                    {
                        dbcolname = dbcolname.toLowerCase();
                        cstmdatacondition += " " + dbcolname + " is not null and " + dbcolname + "!='' and ";
                        cstmdatacondition1 += " " + dbcolname + " is null or " + dbcolname + "='' or ";
                    }
                }
            }

            requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", module);
            result = getMandatoryDefaultColumnHeader(requestParams);
            list = result.getEntityList();
            itr = list.iterator();
            while (itr.hasNext())
            {
                DefaultHeader dh = (DefaultHeader) itr.next();
                if (!dh.isCustomflag())
                {
                    String dbcolname = dh.getPojoheadername();
                    if ((dh.getXtype().equals("4") || dh.getXtype().equals("7")) && !StringUtil.isNullOrEmpty(dh.getValidateType()) && dh.getValidateType().equals("multiselect"))
                    {
                        productcondition += tableid + " in ( select " + tableid + "." + tableid + " from " + multiselecttable + " where " + tableid + ".company.companyID=?" + " ) and ";
                        productcondition1 += tableid + " not in ( select " + tableid + "." + tableid + " from " + multiselecttable + " ) or ";
                    } else if (!StringUtil.isNullOrEmpty(dbcolname))
                    {
                        maincondition += " ifnull(" + dbcolname + ",'')!='' and " + dbcolname + "!='' and ";
                        maincondition1 += " ifnull(" + dbcolname + ",'')='' or " + dbcolname + "='' or ";
                    }
                } else
                {
                    String dbcolname = dh.getDbcolumnname();
                    if (!StringUtil.isNullOrEmpty(dbcolname) && !dh.getConfigid().equals("1"))
                    {
                        dbcolname = dbcolname.toLowerCase();
                        cstmdatacondition += " " + dbcolname + " is not null and " + dbcolname + "!='' and ";
                        cstmdatacondition1 += " " + dbcolname + " is null or " + dbcolname + "='' or ";
                    }
                }
            }

            if (maincondition.length() > 0)
            {
                maincondition = maincondition.substring(0, maincondition.length() - 4);
                wherecondition += " and " + maincondition;
            }

            if (cstmdatacondition.length() > 0)
            {
                cstmdatacondition = cstmdatacondition.substring(0, cstmdatacondition.length() - 4);
                cstmdatawherecondition += " and " + cstmdatacondition;
            }
            if (productcondition.length() > 0)
            {
                productcondition = productcondition.substring(0, productcondition.length() - 4);
                productcondition = " and ( " + productcondition + " ) ";
            }
            if (cstmdatawherecondition.length() > 0)
            {
                params.add(companyid);
                cstmdataquery += cstmdatawherecondition + " ) ";
            } else
            {
                cstmdataquery = " ";
            }
            str1 += wherecondition + cstmdataquery + productcondition;

            if (maincondition1.length() > 0)
            {
                maincondition1 = maincondition1.substring(0, maincondition1.length() - 3);

            }

            if (cstmdatacondition1.length() > 0)
            {
                cstmdatacondition1 = cstmdatacondition1.substring(0, cstmdatacondition1.length() - 3);
                cstmdatawherecondition1 += " and ( " + cstmdatacondition1 + " ) ";
            }
            if (productcondition1.length() > 0)
            {
                productcondition1 = productcondition1.substring(0, productcondition1.length() - 3);
                productcondition1 = " or ( " + productcondition1 + " ) ";
            }
            if (cstmdatawherecondition1.length() > 0)
            {
                if (cstmdatawherecondition.length() == 0)
                {
                    params.add(companyid);
                }
                cstmdataquery1 += cstmdatawherecondition1 + " ) ";
            } else
            {
                cstmdataquery1 = " ";
            }

            String subquery = maincondition1 + cstmdataquery1 + productcondition1;
            if(!StringUtil.isNullOrEmpty(subquery))
                str += " and ( " + subquery + " ) ";

            int num1 = executeUpdate(str, params.toArray());
            // params.clear();
            params.set(0, 1);
            params.set(2, validflag == 2 ? validflag : 0);
            /* subquery params */
            num1 = executeUpdate(str1, params.toArray());
        } catch (Exception ex)
        {
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManagerDAOImpl.validaterecordsHB", ex);
        }
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", result.getEntityList(), result.getRecordTotalCount());
    }

    public KwlReturnObject getFieldParams(HashMap<String, Object> requestParams)
    {
        KwlReturnObject result = null;
        try
        {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from FieldParams ";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null)
            {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");

                if (ind > -1)
                {
                    int index = Integer.valueOf(hql.substring(ind + 1, ind + 2));
                    hql = hql.replace("(" + index + ")", "(" + value.get(index).toString() + ")");
                    value.remove(index);
                }
            }

            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null)
            {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null)
            {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = executePagingQuery(requestParams, searchCol, hql, value);

        } catch (Exception ex)
        {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally
        {
            return result;
        }
    }

    public KwlReturnObject updateDefaultheader(HashMap<String, Object> requestParams)
    {
        ArrayList list = new ArrayList();
        boolean success = false;
        try
        {
            String hql = "";
            ArrayList value = new ArrayList();
            hql = " update DefaultHeader set allowImport='F',mandatory='F',allowMapping='F' where customflag = 'T' and pojoheadername = ( SELECT id from FieldParams where moduleid = ? and company.companyID = ? and id = ? ) ";
            value.add(requestParams.get("moduleid"));
            value.add(requestParams.get("companyid"));
            value.add(requestParams.get("fieldid"));
            int count = executeUpdate(hql, value.toArray());
            list.add(count);
            success = true;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            success = false;
        } finally
        {
            return new KwlReturnObject(success, "Defaultheader updated successfully", "-1", list, list.size());
        }
    }

    public KwlReturnObject updateDefaultheader(Integer fieldtype, Integer fieldmaxlen, String fieldid)
    {
        ArrayList list = new ArrayList();
        boolean success = false;
        String msg = "Defaultheader updated successfully";
        try
        {
            String hql = "";
            ArrayList value = new ArrayList();
            hql = " update DefaultHeader set maxLength=?,xtype=?,validateType='string' where  pojoheadername = ? ";
            value.add(fieldmaxlen);
            value.add(fieldtype.toString());
            value.add(fieldid);
            int count = executeUpdate(hql, value.toArray());
            list.add(count);
            success = true;
        } catch (Exception ex)
        {
            msg = "Error occured while updating Field";
            ex.printStackTrace();
            success = false;
        } finally
        {
            return new KwlReturnObject(success, msg, "-1", list, list.size());
        }
    }

    public KwlReturnObject updateFieldParams(Integer fieldtype, Integer fieldmaxlen, String fieldid, String companyid)
    {
        ArrayList list = new ArrayList();
        boolean success = false;
        String msg = "Field updated successfully. <br/> Please close the tab and open again to use updated field.";
        try
        {
            String hql = "";
            ArrayList value = new ArrayList();
            hql = " update FieldParams set maxlength=?,fieldtype=?  where company.companyID = ? and id = ? ";
            value.add(fieldmaxlen);
            value.add(fieldtype);
            value.add(companyid);
            value.add(fieldid);
            int count = executeUpdate(hql, value.toArray());
            list.add(count);
            success = true;
        } catch (Exception ex)
        {
            msg = "Error occured while updating Field";
            ex.printStackTrace();
            success = false;
        } finally
        {
            return new KwlReturnObject(success, msg, "-1", list, list.size());
        }
    }

    public KwlReturnObject updateColumnHeader(HashMap<String, Object> requestParams)
    {
        ArrayList list = new ArrayList();
        boolean success = false;
        try
        {
            String hql = "";
            HashMap<String, Object> table_requestParams = new HashMap<String, Object>();
            table_requestParams.put("tablename", "ColumnHeader");
            table_requestParams.put("filter_names", Arrays.asList("company.companyID", "defaultheader.pojoheadername"));
            table_requestParams.put("filter_values", Arrays.asList(requestParams.get("companyid"), requestParams.get("fieldid")));
            KwlReturnObject table_result = getTableRecord(table_requestParams);
            String newCustomHeader = requestParams.get("newCustomHeader").toString();
            List lst = table_result.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext())
            {
                ColumnHeader tmpcontyp = (ColumnHeader) ite.next();
                if(!StringUtil.isNullOrEmpty(newCustomHeader)){
                	tmpcontyp.setNewHeader(newCustomHeader);
                }
                tmpcontyp.setMandotory(false);
                saveOrUpdate(tmpcontyp);
                list.add(tmpcontyp);
            }

            success = true;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            success = false;
        } finally
        {
            return new KwlReturnObject(success, "Defaultheader updated successfully", "-1", list, list.size());
        }
    }

    public KwlReturnObject updateFieldParams(HashMap<String, Object> requestParams)
    {
        ArrayList list = new ArrayList();
        boolean success = false;
        try
        {
            String hql = "";
            ArrayList value = new ArrayList();
            hql = " update FieldParams set iseditable=?,isessential=?  where moduleid = ? and company.companyID = ? and id = ? ";
            value.add("false");
            value.add(0);
            value.add(requestParams.get("moduleid"));
            value.add(requestParams.get("companyid"));
            value.add(requestParams.get("fieldid"));
            int count = executeUpdate(hql, value.toArray());
            list.add(count);
            String ismandatory = requestParams.get("ismandatory").toString();
            if (ismandatory.equals("True"))
            {
                validaterecordsHB(getModuleName(Integer.parseInt(requestParams.get("moduleid").toString())), requestParams.get("companyid").toString(), 1);
            }
            success = true;
        } catch (Exception ex)
        {
            ex.printStackTrace();
            success = false;
        } finally
        {
            return new KwlReturnObject(success, "Defaultheader updated successfully", "-1", list, list.size());
        }
    }

    public KwlReturnObject insertformule(HashMap<String, Object> requestParams) throws ServiceException
    {
        List<CustomColumnFormulae> list = new ArrayList<CustomColumnFormulae>();
        boolean success = false;
        try
        {
            CustomColumnFormulae user = (CustomColumnFormulae) setterMethod(requestParams, "com.krawler.common.admin.CustomColumnFormulae", "Formulaeid");
            list.add(user);
            success = true;
        } catch (Exception e)
        {
            success = false;
            System.out.println("Error is " + e);
        } finally
        {
            return new KwlReturnObject(success, "New custom formula is set to the selected column successfully.<br/> Please refresh the tab to use formula.", "-1", list, list.size());
        }
    }

    public KwlReturnObject getCustomColumnFormulae(HashMap<String, Object> requestParams)
    {
        KwlReturnObject result = null;
        try
        {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from CustomColumnFormulae ";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null)
            {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");
                if (ind > -1)
                {
                    int index = Integer.valueOf(hql.substring(ind + 1, ind + 2));
                    hql = hql.replace("(" + index + ")", "(" + value.get(index).toString() + ")");
                    value.remove(index);
                }
            }

            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null)
            {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null)
            {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = executePagingQuery(requestParams, searchCol, hql, value);

        } catch (Exception ex)
        {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally
        {
            return result;
        }
    }

    public KwlReturnObject deletefield(HashMap<String, Object> requestParams) throws ServiceException
    {
        String resultmsg = "Custom column cannot be deleted.";
        // KwlReturnObject result = null;
        boolean success = false;
        List lst = null;
        try
        {
            String companyid = (String) requestParams.get("companyid");
            String moduleid = (String) requestParams.get("moduleid");
            String customdata = (String) requestParams.get("customdata");
            String fieldlabel = (String) requestParams.get("fieldlabel");
            String fieldid = (String) requestParams.get("fieldid");

            KwlReturnObject table_result = null;
            HashMap<String, Object> table_requestParams = new HashMap<String, Object>();
            table_requestParams.put("tablename", "ColumnHeader");
            table_requestParams.put("filter_names", Arrays.asList("company.companyID", "defaultheader.pojoheadername"));
            table_requestParams.put("filter_values", Arrays.asList(companyid, fieldid));
            table_result = getTableRecord(table_requestParams);

            lst = table_result.getEntityList();
            boolean ismandotory = false;
            Iterator ite = lst.iterator();
            while (ite.hasNext())
            {
                ColumnHeader tmpcontyp = (ColumnHeader) ite.next();
                ismandotory = tmpcontyp.isMandotory();
                this.delete(tmpcontyp);
            }

            table_requestParams.clear();
            table_requestParams.put("tablename", "LeadConversionMappings");
            table_requestParams.put("filter_names", Arrays.asList("modulefield.pojoheadername"));
            table_requestParams.put("filter_values", Arrays.asList(fieldid));
            table_result = getTableRecord(table_requestParams);

            lst = table_result.getEntityList();

            ite = lst.iterator();
            while (ite.hasNext())
            {
                LeadConversionMappings tmpcontyp = (LeadConversionMappings) ite.next();
                this.delete(tmpcontyp);
            }

            table_requestParams.clear();
            table_requestParams.put("tablename", "LeadConversionMappings");
            table_requestParams.put("filter_names", Arrays.asList("leadfield.pojoheadername"));
            table_requestParams.put("filter_values", Arrays.asList(fieldid));
            table_result = getTableRecord(table_requestParams);

            lst = table_result.getEntityList();

            ite = lst.iterator();
            while (ite.hasNext())
            {
                LeadConversionMappings tmpcontyp = (LeadConversionMappings) ite.next();
                this.delete(tmpcontyp);
            }

            table_requestParams.clear();
            table_requestParams.put("tablename", "DefaultHeader");
            table_requestParams.put("filter_names", Arrays.asList("pojoheadername"));
            table_requestParams.put("filter_values", Arrays.asList(fieldid));
            table_result = getTableRecord(table_requestParams);

            lst = table_result.getEntityList();
            String xtype = "", colname = "", updatequery = " update " + customdata + " set ", condition = "";
            ite = lst.iterator();

            while (ite.hasNext())
            {
                DefaultHeader tmpcontyp = (DefaultHeader) ite.next();
                xtype = tmpcontyp.getXtype();
                if (xtype.equals("7"))
                {
                    colname = Constants.Custom_column_Prefix + tmpcontyp.getDbcolumnrefname();
                    condition += colname + " = " + Constants.Custom_Column_Default_value + " , ";
                }
                if (!ismandotory)
                {
                    ismandotory = tmpcontyp.isMandatory();
                }
                colname = tmpcontyp.getDbcolumnname().toLowerCase();
                if(!tmpcontyp.getConfigid().equals("1"))
                    condition += colname + " = " + Constants.Custom_Column_Default_value + " ";
                this.delete(tmpcontyp);
            }
            updatequery = updatequery + condition + " where  company.companyID = '" + companyid + "'  ";
            table_requestParams.clear();
            table_requestParams.put("hql", updatequery);
            storeDefaultCstmData(table_requestParams);

            table_requestParams.clear();
            table_requestParams.put(FieldConstants.Crm_tablename, FieldConstants.Crm_FieldComboData);
            table_requestParams.put(Constants.filter_names, Arrays.asList(FieldConstants.Crm_fieldid, FieldConstants.Crm_deleteflag));
            table_requestParams.put(Constants.filter_values, Arrays.asList(fieldid, 0));
            table_result = getTableRecord(table_requestParams);

            lst = table_result.getEntityList();

            ite = lst.iterator();
            while (ite.hasNext())
            {
                FieldComboData tmpcontyp = (FieldComboData) ite.next();
                success = true;
                resultmsg = "Selected column has been deleted successfully.";
                tmpcontyp.setDeleteflag(1);
                this.delete(tmpcontyp);
            }

            table_requestParams.clear();
            table_requestParams.put("tablename", "CustomColumnFormulae");
            table_requestParams.put("filter_names", Arrays.asList("companyid.companyID", "fieldname", "moduleid"));
            table_requestParams.put("filter_values", Arrays.asList(companyid, fieldlabel, moduleid));
            table_result = getTableRecord(table_requestParams);

            lst = table_result.getEntityList();

            ite = lst.iterator();
            while (ite.hasNext())
            {
                CustomColumnFormulae tmpcontyp = (CustomColumnFormulae) ite.next();
                success = true;
                resultmsg = "Selected column has been deleted successfully.";
                this.delete(tmpcontyp);
            }

            table_requestParams.clear();
            table_requestParams.put("tablename", "FieldParams");
            table_requestParams.put("filter_names", Arrays.asList("id"));
            table_requestParams.put("filter_values", Arrays.asList(fieldid));
            table_result = getTableRecord(table_requestParams);

            lst = table_result.getEntityList();

            ite = lst.iterator();
            while (ite.hasNext())
            {
                FieldParams tmpcontyp = (FieldParams) ite.next();
                success = true;
                resultmsg = "Selected column has been deleted successfully.";
                this.delete(tmpcontyp);
            }
            if (ismandotory)
            {
                validaterecordsHB(getModuleName(Integer.parseInt(moduleid)), companyid, 1);
            }
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("fieldManager.deleteCustomColumn", e);
        } finally
        {
            return new KwlReturnObject(success, resultmsg, "-1", lst, lst.size());
        }
    }

    public static String getModuleName(int moduleid)
    {
        String module = "";
        if (moduleid == 1)
        {
            module = "Account";
        } else if (moduleid == 2)
        {
            module = "Lead";
        } else if (moduleid == 6)
        {
            module = "Contact";
        } else if (moduleid == 5)
        {
            module = "Opportunity";
        } else if (moduleid == 4)
        {
            module = "Product";
        } else if (moduleid == 3)
        {
            module = "Case";
        }
        return module;
    }

    public KwlReturnObject getTableRecord(HashMap<String, Object> requestParams)
    {
        KwlReturnObject result = null;
        try
        {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            String tablename = (String) requestParams.get("tablename");
            hql = "from  " + tablename + " ";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null)
            {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");
                if (ind > -1)
                {
                    int index = Integer.valueOf(hql.substring(ind + 1, ind + 2));
                    hql = hql.replace("(" + index + ")", "(" + value.get(index).toString() + ")");
                    value.remove(index);
                }
            }

            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null)
            {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null)
            {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = executePagingQuery(requestParams, searchCol, hql, value);

        } catch (Exception ex)
        {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally
        {
            return result;
        }
    }

    public KwlReturnObject getModules(HashMap<String, Object> requestParams)
    {
        KwlReturnObject result = null;
        try
        {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from Modules ";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null)
            {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");
                if (ind > -1)
                {
                    int index = Integer.valueOf(hql.substring(ind + 1, ind + 2));
                    hql = hql.replace("(" + index + ")", "(" + value.get(index).toString() + ")");
                    value.remove(index);
                }
            }

            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null)
            {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null)
            {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = executePagingQuery(requestParams, searchCol, hql, value);

        } catch (Exception ex)
        {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally
        {
            return result;
        }
    }

    public KwlReturnObject storeDefaultCstmDataPaging(HashMap<String, Object> requestParams) throws ServiceException
    {
        List<Integer> list = new ArrayList<Integer>();
        String hql = (String) requestParams.get("hql");
        ArrayList params = requestParams.containsKey("params") ? (ArrayList) requestParams.get("params") : new ArrayList();
        Integer start = 0, limit = 50;

        start = requestParams.containsKey("start") ? (Integer) requestParams.get("start") : 0;
        limit = requestParams.containsKey("limit") ? (Integer) requestParams.get("limit") : 50;
        boolean success = false;
        try
        {
            int user = executeUpdatePaging(hql, params.toArray(), new Integer[] { start, limit });
            list.add(user);
            success = true;
        } catch (Exception e)
        {
            success = false;
            System.out.println("Error is " + e);
        } finally
        {
            return new KwlReturnObject(success, "Field default data added successfully", "-1", list, list.size());
        }
    }

    public KwlReturnObject getBatchcount(HashMap<String, Object> requestParams)
    {
        KwlReturnObject result = null;
        try
        {
            String hql = (String) requestParams.get("hql");
            ArrayList value = new ArrayList();
            String[] searchCol = null;
            hql = (String) requestParams.get("hql");
            result = executePagingQuery(requestParams, searchCol, hql, value);

        } catch (Exception ex)
        {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally
        {
            return result;
        }
    }

    public KwlReturnObject removefield(HashMap<String, Object> requestParams) throws ServiceException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public KwlReturnObject insertfield(HashMap<String, Object> requestParams) throws ServiceException
    {
        List<FieldParams> list = new ArrayList<FieldParams>();
        boolean success = false;
        try
        {
            FieldParams user = (FieldParams) setterMethod(requestParams, "com.krawler.common.admin.FieldParams", "Id");
            list.add(user);
            success = true;
        } catch (Exception e)
        {
            success = false;
            System.out.println("Error is " + e);
        } finally
        {
            return new KwlReturnObject(success, "New column created successfully.<br/> Please close the tab and open again to use the new field", "-1", list, list.size());
        }
    }

    public KwlReturnObject storeDefaultCstmData(HashMap<String, Object> requestParams) throws ServiceException
    {
        List<Integer> list = new ArrayList<Integer>();
        String hql = (String) requestParams.get("hql");
        ArrayList params = requestParams.containsKey("params") ? (ArrayList) requestParams.get("params") : new ArrayList();
        boolean success = false;
        try
        {
            int user = executeUpdate(hql, params.toArray());
            list.add(user);
            success = true;
        } catch (Exception e)
        {
            e.printStackTrace();
            success = false;
            System.out.println("Error is " + e);
        } finally
        {
            return new KwlReturnObject(success, "Field default data added successfully", "-1", list, list.size());
        }
    }

    public void storeDefaultCstmData(HttpServletRequest request, HashMap<String, Object> requestParams)
    {
        if (requestParams.get(Constants.success).toString().equals("1") && requestParams.get(Constants.defaultvalue) != null && !StringUtil.isNullOrEmpty(requestParams.get(Constants.defaultvalue).toString()))
        {
            String refcolumnname = null;
            if (requestParams.containsKey(Constants.Refcolnum))
            {
                refcolumnname = Constants.Custom_column_Prefix + requestParams.get(Constants.Refcolnum).toString();
            }
            String Colnum = Constants.Custom_column_Prefix + requestParams.get(Constants.Colnum).toString();
            String defaultvalue = requestParams.get(Constants.defaultvalue).toString();
            Integer xtype = Integer.parseInt(requestParams.get(Constants.Fieldtype).toString());
            try
            {
                String companyid = (String) requestParams.get(Constants.companyid);
                Integer moduleid = (Integer) requestParams.get(Constants.moduleid);
                storeDefaultCstmData(companyid, request, refcolumnname, moduleid, Colnum, defaultvalue, xtype);
            } catch (com.krawler.utils.json.base.JSONException ex)
            {
                logger.warn(ex.getMessage(), ex);
                // throw ServiceException.FAILURE(
                // "fieldManagerDAOImpl.storeDefaultCstmData", ex);
            }
        }
    }

    public boolean storeDefaultCstmData(String companyid, HttpServletRequest request, String refcolumnname, int moduleid, String fieldcolumn, String fieldValue, int xtype) throws JSONException, com.krawler.utils.json.base.JSONException
    {
        String tablename = fieldManagerController.getTableName(moduleid);
        String primarycolumn = fieldManagerController.getPrimarycolumn(moduleid);
        String modulename = fieldManagerController.getmoduledataTableName(moduleid);
        String cstmcolumn = fieldManagerController.getmoduledataRefName(moduleid);
        if (!StringUtil.isNullOrEmpty(tablename))
        {

            try
            {
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                if(xtype == Constants.CUSTOM_FIELD_AUTONUMBER) {
                    ArrayList params = new ArrayList();
                    int startno = Integer.parseInt(request.getParameter("startingnumber"));
                    String prefix = request.getParameter("prefix");
                    String suffix = request.getParameter("suffix");
                    String selectQuery = "select "+ primarycolumn+" from "+tablename+" where companyid = ? and deleteflag = ? order by "+Constants.createdon;
                    List list = executeQuery(selectQuery, new Object[]{companyid, 0});
                    for(int cnt=0; cnt< list.size(); cnt++) {
                        params.clear();
                        requestParams.clear();

                        String id = list.get(cnt).toString();
                        String hqlselectquery = " select " + primarycolumn + " from " + modulename + " where " + primarycolumn + "=?";
                        List selll = executeQuery(hqlselectquery, new Object[]{id});
                        String nextAutoNO = fieldDataManager.getNextAutoNo(prefix, startno, suffix);
                        if (selll.size() > 0) {
                            String updatequery = "update " + modulename + " set " + fieldcolumn + "= ? where "+primarycolumn+"=?";
                            params.add(nextAutoNO);
                            params.add(id);
                            requestParams.put("params", params);
                            requestParams.put(Constants.hql, updatequery);
                            storeDefaultCstmData(requestParams);
                        } else {
                            hqlselectquery = " select " + primarycolumn + ",company,'" + nextAutoNO + "' from " + tablename + " where "+ primarycolumn + " = ?";
                            String hqlquery = "insert into " + modulename + " (" + primarycolumn + ",company," + fieldcolumn + ") " + hqlselectquery;
                            requestParams.clear();
                            params.clear();
                            params.add(id);
                            requestParams.put("params", params);
                            requestParams.put(Constants.hql, hqlquery);
                            storeDefaultCstmData(requestParams);

                            //Update customdata field in crmlead table
                            String updatequery = "update " + tablename + " set " + cstmcolumn + "=" + primarycolumn + " where  "+ primarycolumn + " = ?";
                            requestParams.clear();
                            params.clear();
                            params.add(id);
                            requestParams.put("params", params);
                            requestParams.put(Constants.hql, updatequery);
                            storeDefaultCstmData(requestParams);
                        }
                        startno+=1;
                    }
                } else {
                    String updatequery = null;
                    fieldcolumn = fieldcolumn.replace(Constants.C, Constants.c);
                    String where = " where  company.companyID = '" + companyid + "'  ";
                    if (!StringUtil.isNullOrEmpty(refcolumnname))
                    {
                        if (!StringUtil.isNullOrEmpty(fieldValue))//Update sort column and values column in case of multiselect drop-down for the leads whose entry present in the customdata table
                        {
                            updatequery = "update " + modulename + " set " + fieldcolumn + "='" + fieldValue + "' , " + refcolumnname + "='" + fieldValue.split(",")[0] + "' ";
                        }
                    } else
                    {
                        if (!StringUtil.isNullOrEmpty(fieldValue))//Update value column for the leads whose entry present in the customdata table
                        {
                            updatequery = "update " + modulename + " set " + fieldcolumn + "='" + fieldValue + "' ";
                        }
                    }
                    JSONObject resultJson = new JSONObject();
                    requestParams = new HashMap<String, Object>();
                    KwlReturnObject kmsg = null;
                    if (updatequery != null)
                    {
                        updatequery += where;
                        requestParams.put(Constants.hql, updatequery);
                        kmsg = storeDefaultCstmData(requestParams);
                        resultJson.put(Constants.success1, kmsg.isSuccessFlag());
                        resultJson.put(Constants.msg1, kmsg.getMsg());
                    }
                    where = " and company.companyID = '" + companyid + "'  and deleteflag=0 ";
                    if (!StringUtil.isNullOrEmpty(fieldValue))
                    {
                        //Batch insert for the leads whose entry not present in the customdata table.
                        String hqlselectquery = " select " + primarycolumn + ",company,'" + fieldValue + "' from " + tablename + " where " + cstmcolumn + " is  null ";

                        String hqlquery = "insert into " + modulename + " (" + primarycolumn + ",company," + fieldcolumn + ") " + hqlselectquery;
                        requestParams.clear();
                        requestParams.put(Constants.hql, hqlquery + where);
                        resultJson = new JSONObject();
                        kmsg = storeDefaultCstmData(requestParams);
                        resultJson.put(Constants.success2, kmsg.isSuccessFlag());
                        resultJson.put(Constants.msg2, kmsg.getMsg());

                        //Update customdata field in crmlead table
                        updatequery = "update " + tablename + " set " + cstmcolumn + "=" + primarycolumn + " where " + cstmcolumn + " is  null ";

                        requestParams.clear();
                        requestParams.put(Constants.hql, updatequery + where);
                        resultJson = new JSONObject();
                        kmsg = storeDefaultCstmData(requestParams);
                        resultJson.put(Constants.success, kmsg.isSuccessFlag());
                        resultJson.put(Constants.msg, kmsg.getMsg());
                    } else
                    {

                    }
                }
                return true;
            } catch (ServiceException e)
            {
                e.printStackTrace();
                logger.warn(e.getMessage(), e);
                return false;
            }
        }
        return false;
    }

    public KwlReturnObject insertdefaultheader(HashMap<String, Object> requestParams) throws ServiceException
    {
        List<DefaultHeader> list = new ArrayList<DefaultHeader>();
        boolean success = false;
        try
        {
            DefaultHeader dh = (DefaultHeader) setterMethod(requestParams, "com.krawler.common.admin.DefaultHeader", "Id");
            list.add(dh);
            success = true;
        } catch (Exception e)
        {
            success = false;
            System.out.println("Error is " + e);
        } finally
        {
            return new KwlReturnObject(success, "Field added successfully", "-1", list, list.size());
        }
    }

    public KwlReturnObject savedefaultheader(HashMap<String, Object> requestParams)
    {
        List<DefaultHeader> list = new ArrayList<DefaultHeader>();
        boolean success = false;
        try
        {
            DefaultHeader dh = (DefaultHeader) requestParams.get("DefaultHeader");
            save(dh);
            list.add(dh);
            success = true;
        } catch (Exception e)
        {
            success = false;
            System.out.println("Error is " + e);
        } finally
        {
            return new KwlReturnObject(success, "DefaultHeader changed successfully", "-1", list, list.size());
        }
    }

    public KwlReturnObject saveCustomColumnFormulae(HashMap<String, Object> requestParams)
    {
        List<CustomColumnFormulae> list = new ArrayList<CustomColumnFormulae>();
        boolean success = false;
        try
        {
            CustomColumnFormulae ccl = (CustomColumnFormulae) requestParams.get("CustomColumnFormulae");
            save(ccl);
            list.add(ccl);
            success = true;
        } catch (Exception e)
        {
            success = false;
            System.out.println("Error is " + e);
        } finally
        {
            return new KwlReturnObject(success, "Custom column formula changed successfully. <br/> Please close the tab and open again to use formula.", "-1", list, list.size());
        }
    }

    public KwlReturnObject insertcolumnheader(HashMap<String, Object> requestParams) throws ServiceException
    {
        List<ColumnHeader> list = new ArrayList<ColumnHeader>();
        boolean success = false;
        try
        {
            ColumnHeader user = (ColumnHeader) setterMethod(requestParams, "com.krawler.common.admin.ColumnHeader", "Id");
            list.add(user);
            success = true;
        } catch (Exception e)
        {
            success = false;
            System.out.println("Error is " + e);
        } finally
        {
            return new KwlReturnObject(success, "Field added successfully", "-1", list, list.size());
        }
    }

    public KwlReturnObject changefield(HashMap<String, Object> requestParams) throws ServiceException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public KwlReturnObject insertfieldcombodata(HashMap<String, Object> comborequestParams) throws ServiceException
    {
        List<FieldComboData> list = new ArrayList<FieldComboData>();
        boolean success = false;
        try
        {
            FieldComboData user = (FieldComboData) setterMethod(comborequestParams, "com.krawler.common.admin.FieldComboData", "Id");
            list.add(user);
            success = true;
        } catch (Exception e)
        {
            success = false;
            System.out.println("Error is " + e);
        } finally
        {
            return new KwlReturnObject(success, "FieldComboData added successfully", "-1", list, list.size());
        }
    }

    public String getFieldComboDatadata(HashMap<String, Object> requestParams)
    {
        FieldComboData fc = (FieldComboData) get(FieldComboData.class, (String) requestParams.get("dataid"));
        if (fc != null)
            return fc.getValue();
        else
            return "";
    }

    public KwlReturnObject getCustomCombodata(HashMap<String, Object> requestParams)
    {
        KwlReturnObject result = null;
        try
        {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from FieldComboData ";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null)
            {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");
                if (ind > -1)
                {
                    int index = Integer.valueOf(hql.substring(ind + 1, ind + 2));
                    hql = hql.replace("(" + index + ")", "(" + value.get(index).toString() + ")");
                    value.remove(index);
                }
            }

            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null)
            {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null)
            {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = executePagingQuery(requestParams, searchCol, hql, value);

        } catch (Exception ex)
        {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally
        {
            return result;
        }
    }

    public KwlReturnObject deleteCustomCombodata(HashMap<String, Object> requestParams) throws ServiceException
    {
        KwlReturnObject result = null;
        List ll = null;
        List ll1 = new ArrayList();
        try
        {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from FieldComboData ";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null)
            {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");
                if (ind > -1)
                {
                    int index = Integer.valueOf(hql.substring(ind + 1, ind + 2));
                    hql = hql.replace("(" + index + ")", "(" + value.get(index).toString() + ")");
                    value.remove(index);
                }
            }

            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null)
            {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null)
            {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            ll = executeQuery(hql, value.toArray());
            if (ll.size() > 0)
            {
                Iterator ite = ll.iterator();
                while (ite.hasNext())
                {
                    FieldComboData fieldComboData = (FieldComboData) ite.next();
                    fieldComboData.setDeleteflag(1);
                    ll1.add(fieldComboData.getValue());
                    ll1.add(((FieldParams) get(FieldParams.class, fieldComboData.getFieldid())).getFieldlabel());
                    delete(fieldComboData);
                }
            }
        } catch (Exception ex)
        {
            result.setSuccessFlag(false);
            ex.printStackTrace();
            throw ServiceException.FAILURE("fieldManagerDAOImpl.deleteCustomCombodata", ex);
        } finally
        {
            return new KwlReturnObject(true, "FieldComboData deleted successfully", "1", ll1, ll1.size());
        }
    }

    public void validateimportrecords(HashMap<String, Object> requestParams) throws ServiceException
    {
        Integer validflag = Integer.parseInt(requestParams.get("validflag").toString());
        String modulename = requestParams.get("modulename").toString();
        String companyid = requestParams.get("companyid").toString();
        validaterecordsHB(modulename, companyid, validflag);
    }

    public List<Object[]> getModuleCustomFormulae(int moduleid, String companyid) {
        List<Object[]> list = null;
        try {
            String hql = "select formulae,fieldname from CustomColumnFormulae where companyid.companyID=? and moduleid=? ";
            list = executeQuery(hql, new Object[]{companyid, String.valueOf(moduleid)});
        } catch(Exception ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            return list;
        }
    }

    public KwlReturnObject executePagingQuery(HashMap<String,Object> requestParams,String[] searchcol,String hql,ArrayList params) {
            boolean success = false;
            List lst = null;
            int count = 0;
        try {
            String allflag = "true";
            if (requestParams.containsKey("allflag"))
                allflag = requestParams.get("allflag").toString();
            int start = 0;
            int limit = 0;

            if (allflag.equals("false"))
            {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }

            lst = executeQuery(hql, params.toArray());
            count = lst.size();
            if (allflag.equals("false"))
                lst = executeQueryPaging(hql, params.toArray(), new Integer[] { start, limit });
            success = true;
        } catch (Exception e)
        {
            success = false;
            logger.warn(e.getMessage(), e);
        } finally
        {
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }

    public List getFieldData(String module, String moduleRecId)
    {
        List returnList = new ArrayList();
        String query1 = "show tables ";
        List l = executeNativeQuery(query1);
        if (!l.contains(module + "cstm"))
        {
            query1 = "CREATE TABLE  `" + module + "cstm`(`modulerecid` varchar(36) NOT NULL,`fieldparamid` int(11) NOT NULL, `fieldvalue` varchar(255) NOT NULL,   `fieldname` varchar(255) NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            executeNativeUpdate(query1);
        }
        String query = "";
        int lengthArr;
        if (l.contains(module + "cstmmultiselect"))
        {
            query = "SELECT cm.fieldname,cm.fieldvalue,cm.fieldparamid, fp.fieldtype FROM  " + module + "cstm as cm inner join fieldparams as fp on cm.fieldparamid = fp.id where modulerecid = ?" + " union " + "SELECT cm.fieldname,cm.fieldvalue,cm.fieldparamid, fp.fieldtype FROM  " + module + "cstmmultiselect as cm inner join fieldparams as fp on cm.fieldparamid = fp.id where modulerecid = ?";
            returnList = executeNativeQuery(query, new String[] { moduleRecId, moduleRecId });
            lengthArr = returnList.size();
        } else
        {
            query = "SELECT cm.fieldname,cm.fieldvalue,cm.fieldparamid, fp.fieldtype FROM  " + module + "cstm as cm inner join fieldparams as fp on cm.fieldparamid = fp.id where modulerecid = ?";
            returnList = executeNativeQuery(query, new String[] { moduleRecId });
            lengthArr = returnList.size();
        }

        Hashtable ht = new Hashtable();
        if (lengthArr > 0)
        {
            for (int n = 0; n < lengthArr; n++)
            {
                Object[] item = (Object[]) returnList.get(n);
                if (StringUtil.equal(item[3].toString(), "7"))
                {
                    if (ht.containsKey(item[2]))
                    {
                        ht.put(item[2], ht.get(item[2]).toString() + "," + item[1]);
                        Object[] item1 = (Object[]) returnList.get(n - 1);
                        item1[1] = ht.get(item[2]).toString();
                        returnList.set(n - 1, item1);
                        returnList.remove(n);
                        lengthArr--;
                        n--;
                    } else
                    {
                        ht.put(item[2], item[1]);
                    }
                }
            }
        }

        return returnList;

    }

    public List getOnlyFieldName(HttpServletRequest request, String moduleId) throws SessionExpiredException, ServiceException
    {
        List returnList = new ArrayList();
        try
        {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String query = "SELECT distinct `fieldname` FROM fieldparams where moduleid=? and companyid = ?";
            returnList = executeNativeQuery(query, new String[] { moduleId, companyid });
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("fieldManager.getOnlyFieldName", e);
        }
        return returnList;
    }

    public boolean storeCustomFields(JSONArray jarray, String modulename, boolean isNew, String modulerecid)
    {
        try
        {
            for (int i = 0; i < jarray.length(); i++)
            {
                try
                {
                    JSONObject jobj = jarray.getJSONObject(i);
                    Iterator ittr = jobj.keys();
                    String fieldid = "";
                    String fieldName = "";
                    String xtype = "";
                    String fieldValue = "";
                    if (!ittr.hasNext())
                        continue;
                    while (ittr.hasNext())
                    {
                        Object obj = ittr.next();
                        if (obj.toString().equals("filedid"))
                        {
                            fieldid = jobj.getString("filedid");
                        } else if (obj.toString().equals("xtype"))
                        {
                            xtype = jobj.getString("xtype");
                        } else
                        {
                            fieldName = obj.toString();

                        }
                    }
                    String query1 = "show tables ";
                    List l = executeNativeQuery(query1);
                    if (!l.contains(modulename + "cstm"))
                    {
                        query1 = "CREATE TABLE  `" + modulename + "cstm`(`modulerecid` varchar(36) NOT NULL,`fieldparamid` int(11) NOT NULL, `fieldvalue` varchar(255) NOT NULL,   `fieldname` varchar(255) NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                        executeNativeUpdate(query1);
                    }
                    if ((!l.contains(modulename + "cstmmultiselect")) && (StringUtil.equal("7", xtype)))
                    {
                        query1 = "CREATE TABLE  `" + modulename + "cstmmultiselect`(`modulerecid` varchar(36) NOT NULL,`fieldparamid` int(11) NOT NULL, `fieldvalue` varchar(255) NOT NULL,   `fieldname` varchar(255) NOT NULL ) ENGINE=InnoDB DEFAULT CHARSET=utf8;";
                        executeNativeUpdate(query1);
                    }
                    if (StringUtil.equal("7", xtype))
                    {

                        fieldValue = jobj.getString(fieldName);
                        String fieldValueArr[] = fieldValue.split(",");
                        String delquery = "delete FROM " + modulename + "cstmmultiselect where fieldparamid = ? and modulerecid = ? ";
                        executeNativeUpdate(delquery, new String[] { fieldid, modulerecid });
                        for (int m = 0; m < fieldValueArr.length; m++)
                        {
                            if ((!fieldValueArr[m].equals("undefined")) && (!fieldValueArr[m].equals("")))
                            {
                                String query = "insert into " + modulename + "cstmmultiselect values(?,?,?,?)";
                                executeNativeUpdate(query, new String[] { modulerecid, fieldid, fieldValueArr[m], fieldName });
                            }
                        }

                    } else
                    {
                        fieldValue = jobj.getString(fieldName);
                        String query = "SELECT modulerecid FROM " + modulename + "cstm where modulerecid = ? and fieldparamid = ?";
                        List list = executeNativeQuery(query, new String[] { modulerecid, fieldid });
                        boolean recExists = false;
                        if (list.size() > 0)
                        {
                            recExists = true;
                        }

                        if ((isNew || !recExists))
                        {
                            if ((!fieldValue.equals("undefined")) && (!fieldValue.equals("")))
                            {
                                query = "insert into " + modulename + "cstm values(?,?,?,?)";
                            }
                        } else
                        {
                            if ((!fieldValue.equals("undefined")) && (!fieldValue.equals("")))
                            {
                                query = "update " + modulename + "cstm set fieldvalue=? where modulerecid=? and fieldparamid = ?";
                            } else
                            {
                                query = "delete from " + modulename + "cstm where modulerecid=? and fieldparamid = ?";
                            }
                        }
                        List<String> paramList = null;
                        if ((isNew || !recExists))
                        {
                            if ((!fieldValue.equals("undefined")) && (!fieldValue.equals("")))
                            {
                                paramList = new ArrayList<String>();
                                paramList.add(modulerecid);
                                paramList.add(fieldid);
                                paramList.add(fieldValue);
                                paramList.add(fieldName);
                                executeNativeUpdate(query, paramList.toArray());
                            }
                        } else
                        {
                            if ((!fieldValue.equals("undefined")) && (!fieldValue.equals("")))
                            {
                                paramList = new ArrayList<String>();
                                paramList.add(fieldValue);
                                paramList.add(modulerecid);
                                paramList.add(fieldid);
                                executeNativeUpdate(query, paramList.toArray());
                            } else
                            {
                                paramList = new ArrayList<String>();
                                paramList.add(modulerecid);
                                paramList.add(fieldid);
                                executeNativeUpdate(query, paramList.toArray());
                            }
                        }

                    }
                } catch (Exception ex)
                {
                    logger.warn(ex.getMessage(), ex);
                }

            }
        } 
        catch (Exception e)
        {
            logger.warn(e.getMessage(), e);
        }
        return true;
    }
    /* (non-Javadoc)
     * @see com.krawler.customFieldMaster.fieldManagerDAO#addCustomComboData(java.lang.String, java.lang.String)
     */
    public KwlReturnObject addCustomComboData(String fieldid, String name, int seq){
        String result = "{success:false}";
        List ll = new ArrayList();
        int dl = 0;
        try{
            FieldComboData fcd=new FieldComboData();
            String uuid = StringUtil.generateUUID();
            fcd.setId(uuid);
            fcd.setFieldid(fieldid);
            fcd.setValue(name);
            fcd.setItemsequence(seq);
            save(fcd);
            result = "{success:true,data:{}}";
            ll.add(result);
            ll.add(fcd);
            ll.add((FieldParams) get(FieldParams.class, fieldid));
        }catch(Exception e){
            logger.warn(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    /* (non-Javadoc)
     * @see com.krawler.customFieldMaster.fieldManagerDAO#editCustomComboData(java.lang.String, java.lang.String)
     */
    public KwlReturnObject editCustomComboData(String id,String name){        
        String result = "{success:false}";
        List ll = new ArrayList();
        int dl = 0;
        FieldComboData fcd = null;
        String oldValue = "";
        try{
            fcd = (FieldComboData) get(FieldComboData.class, id);
            oldValue = fcd.getValue();
            fcd.setValue(name);
            saveOrUpdate(fcd);                
            result = "{success:true,data:{}}";
            ll.add(result);
            ll.add(fcd);
            ll.add((FieldParams) get(FieldParams.class, fcd.getFieldid()));
            ll.add(oldValue);            
        }catch(Exception e){
            logger.warn(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    public List getCustomComboNames(String companyid) throws ServiceException {
        List ls = new ArrayList();
        try{
            String query2 =  "SELECT id, fieldlabel, fieldname, moduleid ,maxlength FROM fieldParams where fieldtype in (4, 7) and companyid = ?";                        
            ls = executeNativeQuery(query2, new String[]{companyid});;
        }catch(Exception e){
            throw ServiceException.FAILURE("fieldManager.getModuleComboNames", e);
        }
        return ls;
    }

    @Override
    public List getRefComboNames() {
        List ls = new ArrayList();
        try {
            String hideCombos = Constants.MASTERCONFIG_HIDECOMBO+","+Constants.MASTERCONFIG_HIDECOMBO_EXTRA;
            String query = " SELECT `masterid` ,`comboname` from crm_combomaster ";
            query +=" where comboname not in ("+hideCombos+" ) order by comboname ";
            ls = executeNativeQuery(query);
        } catch (Exception ex) {
            Logger.getLogger(fieldManagerDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ls;
    }

    
    public String getCustomFieldID(String moduleName ,String fieldid, String companyid)
    {
   	 String HQL ="select ch.defaultheader.pojoheadername from ColumnHeader ch where ch.company.companyID = ? and ch.defaultheader.moduleName = ? and ch.defaultheader.defaultHeader in (select dh1.defaultHeader from DefaultHeader dh1 where dh1.pojoheadername = ?)"; 
   	 List l = executeQuery(HQL, new Object[]{companyid, moduleName, fieldid});
   	 if(l.isEmpty())
   		 return null;
   	 
   	 return (String)l.get(0);
    }
    
    public String checkCustomFieldID(String moduleNames, String fieldid, String companyid) {
        String msg = "";
        try {
            String HQL = "select ch.defaultheader, ch.defaultheader.defaultHeader, ch.defaultheader.moduleName from ColumnHeader ch where ch.company.companyID = ? "
                    + "and ch.defaultheader.moduleName in (" + moduleNames + ") and ch.defaultheader.defaultHeader in (select dh1.defaultHeader from DefaultHeader dh1 where dh1.pojoheadername = ?)";
            List<Object[]> l = executeQuery(HQL, new Object[]{companyid, fieldid});
            for (Object[] row : l) {
                DefaultHeader dheaderid = (DefaultHeader) row[0];
                String colName = row[1].toString();
                String moduleName = row[2].toString();
                HQL = "Select rc.reportno.rname from CustomReportColumns rc where rc.defaultheader = ? and rc.reportno.usersByCreatedbyid.company.companyID = ?";
                List<String> ll = executeQuery(HQL, new Object[]{dheaderid, companyid});
                for (String reportname : ll) {
                    msg += "<BR/> Module : - " + moduleName + ", Custom Report :- " + reportname;
                }
            }
            if (!StringUtil.isNullOrEmpty(msg)) {
                msg = "You can not delete selected column as it has been used in the following custom report/s - " + msg;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    public Boolean checkForDuplicateEntryInMasterData(String name, String configid)throws ServiceException{
        boolean chkForDuplicateEntryInMasterData = false;
        List li = null;
        try{
            String query2 =  "SELECT id FROM fieldcombodata where fieldid = ? and STRCMP(value, '"+name+"' )=0 ";
            li = executeNativeQuery(query2, new String[]{configid});
            if (li.size() > 0) {
                chkForDuplicateEntryInMasterData = true;
            }
        }catch(Exception e){
            throw ServiceException.FAILURE("fieldManager.checkForDuplicateEntryInMasterData", e);
        }
        return chkForDuplicateEntryInMasterData;
    }

    public String getMaxAutoNumber(String columnname, String tableName, String companyid, String prefix, String suffix)  throws ServiceException {
        String maxVal = "";
        try {
            ArrayList filter_values = new ArrayList();
            filter_values.add(prefix);
            filter_values.add(suffix);
            filter_values.add(companyid);
            String maxSubQuery = "select max(replace(replace("+columnname+",?,''),?,'')*1) from ";
            String Hql = maxSubQuery + tableName + " where company.companyID=?";
            List ll = executeQuery(Hql, filter_values.toArray());
            if(!ll.isEmpty()) {
                if(ll.get(0)!=null)
                    maxVal = ll.get(0).toString();
            }
        } catch(Exception ex)  {
            logger.warn(ex.getMessage(), ex);
        } finally{
            return maxVal;
        }
    }

	@Override
	public FieldParams getFieldParamsObject(String id) throws ServiceException {
		return (FieldParams)get(FieldParams.class,id);
	}
	
	@Override
	public void changeCustomDateToString(String field, int moduleid,Map<String,Object> dataMap ,String companyid) throws ServiceException {
		String Hql="";
		field=field.toLowerCase();
		Hql= getCustomUpdateQuery(moduleid,field);
		Set<String> keySet=dataMap.keySet();
		for(String key:keySet){
			executeUpdate(Hql, new Object[]{dataMap.get(key).toString(),key,false});
		}
	}
	private String getCustomUpdateQuery(int moduleid,String field){
		String Hql="";
		switch (moduleid){
			case 1:Hql=" Update CrmAccountCustomData c set "+ field +" = ? where c.accountid = ? and deleted = ? ";
					break;
			case 2:Hql=" Update CrmLeadCustomData c set "+ field +" = ? where c.leadid = ? and deleted = ? ";
					break;
			case 3:Hql=" Update CrmCaseCustomData c set "+ field +" = ? where c.caseid = ? and deleted = ? ";
					break;
			case 4:Hql=" Update CrmProductCustomData c set "+ field +" = ? where c.productid = ? and deleted = ? ";
					break;
			case 5:Hql=" Update CrmOpportunityCustomData c set "+ field +" = ? where c.oppid = ? and deleted = ? ";
					break;
			case 6:Hql=" Update CrmContactCustomData c set "+ field +" = ? where c.contactid = ? and deleted = ? ";
					break;
		}
		return Hql;
	}

	@Override
	public Map<String,Object> getCustomDataToBeChange(String field, int moduleid, DateFormat datepattern,String companyid) throws ServiceException {
		String Hql="";
		field=field.toLowerCase();
		Map customDataMap=new HashMap<String,Object>();
		List ll=null;
		switch (moduleid){
			case 1:Hql="Select c.accountid,c."+field+" from CrmAccountCustomData c where c.deleted = ? and c.account.company.companyID= ? and c."+field+" is not null and c."+field+" <> '' ";
						break;
			case 2:Hql="Select c.leadid,c."+field+" from CrmLeadCustomData c where c.deleted = ? and c.lead.company.companyID = ? and c."+field+" is not null and c."+field+" <> '' ";
						break;
			case 3:Hql="Select c.caseid,c."+field+" from CrmCaseCustomData c where c.deleted = ? and c.Case.company.companyID = ? and c."+field+" is not null and c."+field+" <> '' ";
						break;
			case 4:Hql="Select c.productid,c."+field+" from CrmProductCustomData c where c.deleted = ? and c.product.company.companyID = ? and c."+field+" is not null and c."+field+" <> '' ";
						break;
			case 5:Hql="Select c.oppid,c."+field+" from CrmOpportunityCustomData c where c.deleted = ? and c.opportunity.company.companyID = ? and c."+field+" is not null and c."+field+" <> '' ";
						break;
			case 6:Hql="Select c.contactid,c."+field+" from CrmContactCustomData c where c.deleted = ? and c.contact.company.companyID = ? and c."+field+" is not null and c."+field+" <> '' ";
						break;
		}
		ll = executeQuery(Hql,new Object[]{false,companyid});
		Iterator it=ll.iterator();
		Object[] obAr=null;
		String dateval="";
		while(it.hasNext()){
			obAr=(Object[])it.next();
			dateval= datepattern.format(new Date(Long.parseLong(obAr[1].toString())));
			customDataMap.put(obAr[0],dateval);
		}
		return customDataMap;
	}

}
