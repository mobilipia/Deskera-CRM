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
package com.krawler.spring.crm.caseModule;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.Docs;
import com.krawler.common.admin.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmCustomer;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.CustomerDocs;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.crm.database.tables.CaseProducts;
import com.krawler.crm.database.tables.CrmCaseCustomData;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import org.springframework.dao.DataAccessException;

public class crmCaseDAOImpl extends BaseDAO implements crmCaseDAO
{

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseDAO#addCases(com.krawler.utils
     * .json.base.JSONObject)
     */
    public KwlReturnObject addCases(JSONObject jobj) throws ServiceException
    {
        List ll = new ArrayList();
        int dl = 0;
        try
        {
            String companyid = null;
            String userid = null;
            String id = "";
            CrmCase crmCase = new CrmCase();
            if (jobj.has("caseid"))
            {
                id = jobj.getString("caseid");
                crmCase.setCaseid(id);
            }
            if (jobj.has("casename"))
            {
                crmCase.setCasename(jobj.getString("casename"));
            }
            if (jobj.has("contactnameid"))
            {
                crmCase.setCrmContact((CrmContact) get(CrmContact.class, jobj.getString("contactnameid")));
            }
            if (jobj.has("companyid"))
            {
                companyid = jobj.getString("companyid");
                crmCase.setCompany((Company) get(Company.class, companyid));
            }            
            if (jobj.has("casetypeid"))
            {
                crmCase.setCrmCombodataByCasetypeid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("casetypeid")));
            }
            if (jobj.has("casestatusid"))
            {
                crmCase.setCrmCombodataByCasestatusid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("casestatusid")));
            }
            if (jobj.has("casepriorityid"))
            {
                crmCase.setCrmCombodataByCasepriorityid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("casepriorityid")));
            }
            if (jobj.has("accountnameid"))
            {
                crmCase.setCrmAccount((CrmAccount) get(CrmAccount.class, jobj.getString("accountnameid")));
            }
            if (jobj.has("description"))
            {
                crmCase.setDescription(jobj.getString("description"));
            }
            if (jobj.has("subject"))
            {
                crmCase.setSubject(jobj.getString("subject"));
            }
            if (jobj.has("updatedon"))
            {
                crmCase.setUpdatedOn(System.currentTimeMillis());
            }
            if (jobj.has("userid"))
            {
                User user = (User) get(User.class, jobj.getString("userid"));
                crmCase.setUsersByUpdatedbyid(user);
                crmCase.setUsersByCreatedbyid(user);
            }
            if (jobj.has("caseownerid"))
            {
                crmCase.setUsersByUserid((User) get(User.class, jobj.getString("caseownerid")));
            }
            if (jobj.has("caseassignedtoid"))
            {
                crmCase.setAssignedto((User) get(User.class, jobj.getString("caseassignedtoid")));
            }
            if (jobj.has("validflag"))
            {
                crmCase.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }

            crmCase.setCreatedOn(jobj.optLong("createdon",System.currentTimeMillis()));
            crmCase.setCreatedOnNE(jobj.optLong("createdon",System.currentTimeMillis()));

            if (jobj.has("deleteflag"))
            {
                crmCase.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if (jobj.has("createdbyflag"))
            {
                crmCase.setCreatedByFlag(Integer.parseInt(jobj.getString("createdbyflag")));
            }
            if (jobj.has("createdbyflag")&&jobj.getString("createdbyflag").equals("1"))
            {
                crmCase.setCaseCreatedBy(jobj.getString("contactnameid"));
            }else{
            	crmCase.setCaseCreatedBy(jobj.getString("userid"));
            }
            

            saveOrUpdate(crmCase);
            if (jobj.has("productnameid"))
            {
                saveCaseProducts(new String[]{id},jobj.getString("productnameid").split(","));
            }
            ll.add(crmCase);

        } catch (JSONException e)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.addCases : " + e.getMessage(), e);
        } catch (DataAccessException e)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.addCases : " + e.getMessage(), e);
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.addCases : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseDAO#editCases(com.krawler.utils
     * .json.base.JSONObject)
     */
    public KwlReturnObject editCases(JSONObject jobj) throws ServiceException
    {
        List ll = new ArrayList();
        int dl = 0;
        try
        {
            String companyid = null;
            String userid = null;
            String id = "";
            CrmCase crmCase = null;
            if (jobj.has("caseid"))
            {
                id = jobj.getString("caseid");
                crmCase = (CrmCase) get(CrmCase.class, id);
            }
            if (jobj.has("casename"))
            {
                crmCase.setCasename(jobj.getString("casename"));
            }
            if (jobj.has("contactnameid"))
            {
                crmCase.setCrmContact((CrmContact) get(CrmContact.class, jobj.getString("contactnameid")));
            }
            if (jobj.has("companyid"))
            {
                companyid = jobj.getString("companyid");
                crmCase.setCompany((Company) get(Company.class, companyid));
            }
            if (jobj.has("productnameid"))
            {
                saveCaseProducts(new String[]{id},jobj.getString("productnameid").split(","));
            }
            if (jobj.has("casetypeid"))
            {
                crmCase.setCrmCombodataByCasetypeid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("casetypeid")));
            }
            if (jobj.has("casestatusid"))
            {
                crmCase.setCrmCombodataByCasestatusid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("casestatusid")));
            }
            if (jobj.has("casepriorityid"))
            {
                crmCase.setCrmCombodataByCasepriorityid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("casepriorityid")));
            }
            if (jobj.has("accountnameid"))
            {
                crmCase.setCrmAccount((CrmAccount) get(CrmAccount.class, jobj.getString("accountnameid")));
            }
            if (jobj.has("description"))
            {
                crmCase.setDescription(jobj.getString("description"));
            }
            if (jobj.has("subject"))
            {
                crmCase.setSubject(jobj.getString("subject"));
            }
            if (jobj.has("updatedon"))
            {
                crmCase.setUpdatedOn(System.currentTimeMillis());
            }
            if (jobj.has("userid"))
            {
                userid = jobj.getString("userid");
                crmCase.setUsersByUpdatedbyid((User) get(User.class, userid));
            }
            if (jobj.has("caseownerid"))
            {
                crmCase.setUsersByUserid((User) get(User.class, jobj.getString("caseownerid")));
            }
            if (jobj.has("caseassignedtoid"))
            {
                    crmCase.setAssignedto((User) get(User.class, jobj.getString("caseassignedtoid")));
            }
            if (jobj.has("validflag"))
            {
                crmCase.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if (jobj.has("createdon"))
            {
                crmCase.setCreatedOn(jobj.optLong("createdon"));
            }
            if (jobj.has("deleteflag"))
            {
                crmCase.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
                String query = "delete from CaseProducts where caseid.caseid=?";
                executeUpdate(query, id);
            }

            if (jobj.has("CrmCaseCustomDataobj"))
            {
                crmCase.setCrmCaseCustomDataobj((CrmCaseCustomData) get(CrmCaseCustomData.class, jobj.getString("CrmCaseCustomDataobj")));
            }
            saveOrUpdate(crmCase);
            ll.add(crmCase);

        } catch (JSONException e)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.editCases : " + e.getMessage(), e);
        } catch (DataAccessException e)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.editCases : " + e.getMessage(), e);
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.editCases : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseDAO#getCases(java.util.HashMap)
     */
    public KwlReturnObject getCases(HashMap<String, Object> requestParams) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        try
        {
            requestParams.put(Constants.moduleid, 3);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();

            if (requestParams.containsKey("filter_names"))
            {
                filter_names = (ArrayList) requestParams.get("filter_names");
            }
            if (requestParams.containsKey("filter_params"))
            {
                filter_params = (ArrayList) requestParams.get("filter_params");
            }
            String appendCase = "and";
            String selCountQuary = "select count(distinct c.caseid) ";
            String selQuery = "select distinct c ";
            String Hql = " from CrmCase c " + crmManagerCommon.getJoinQuery(requestParams);
            boolean productJoin = false;
            if (filter_names.contains("INp.productId.productid"))
            {
                Hql += " inner join c.crmProducts as p ";
                productJoin = true;
            }

            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if (ind > -1)
            {
                int index = Integer.valueOf(filterQuery.substring(ind + 1, ind + 2));
                filterQuery = filterQuery.replaceAll("(" + index + ")", filter_params.get(index).toString());
                filter_params.remove(index);
            }

            String Searchjson = "";
            if (requestParams.containsKey("searchJson") && requestParams.get("searchJson") != null)
            {
                Searchjson = requestParams.get("searchJson").toString();
                if (Searchjson.contains("p.productId.productid") && !productJoin)
                {
                    Hql += " inner join c.crmProducts as p ";
                }
                if (!StringUtil.isNullOrEmpty(Searchjson))
                {
                    requestParams.put(Constants.Searchjson, Searchjson);
                    requestParams.put(Constants.appendCase, appendCase);
                    String mySearchFilterString = String.valueOf(StringUtil.getMyAdvanceSearchString(requestParams).get(Constants.myResult));
                    filterQuery += mySearchFilterString;
                    StringUtil.insertParamAdvanceSearchString(filter_params, Searchjson);
                }
            }

            Object config = null;
            if (requestParams.containsKey("config"))
            {
                config = requestParams.get("config");
            }
            if (config != null)
            {
                filterQuery += " and c.validflag=1 ";
            }
            if (requestParams.containsKey("ss") && requestParams.get("ss") != null)
            {
                String ss = requestParams.get("ss").toString();
                if (!StringUtil.isNullOrEmpty(ss))
                {
                    String[] searchcol = new String[] { "c.subject" };
                    StringUtil.insertParamSearchString(filter_params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    filterQuery += searchQuery;
                }
            }

            int start = 0;
            int limit = 25;
            boolean ispaging = requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()) && !StringUtil.isNullOrEmpty(requestParams.get("start").toString());
            if (ispaging)
            {
                if (requestParams.containsKey("iPhoneCRM") && requestParams.get("iPhoneCRM") != null)
                {
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                    start = Integer.parseInt(requestParams.get("start").toString()) * limit;
                } else
                {
                    start = Integer.parseInt(requestParams.get("start").toString());
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                }
            }

            boolean heirarchyPerm = false;
            if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null)
            {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if (!heirarchyPerm)
            {
                StringBuffer usersList = (StringBuffer) requestParams.get("userlist");
                filterQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
            }

            String orderQuery = " order by c.createdOn desc ";
            if (requestParams.containsKey("field") && requestParams.get("xfield") != null)
            {
                String dbname = crmManagerCommon.getFieldDbName(requestParams);
                if (dbname != null)
                {
                    String dir = requestParams.get("direction").toString();
                    orderQuery = " order by " + dbname + " " + dir + " ";
                }
            }

            String countQuery = selCountQuary + Hql + filterQuery;
            String query = selQuery + Hql + filterQuery + orderQuery;
            String export = "";
            if (requestParams.containsKey("export") && requestParams.get("export") != null)
            {
                countQuery = selQuery + Hql + filterQuery + orderQuery;
                export = requestParams.get("export").toString();
            }

            ll = executeQuery(countQuery, filter_params.toArray());
            if (ll != null && !ll.isEmpty() && StringUtil.isNullOrEmpty(export))
            {
                dl = ((Number) ll.get(0)).intValue();
            }

            if (StringUtil.isNullOrEmpty(export))
            {
                ll = executeQueryPaging(query, filter_params.toArray(), new Integer[] { start, limit });
            }
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.getCases : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseDAO#getAllCases(java.util.HashMap
     * )
     */
    public KwlReturnObject getAllCases(HashMap<String, Object> requestParams) throws ServiceException
    {
        int dl = 0;
        List ll = null;
        try
        {
            boolean pagingFlag = false;
            if (requestParams.containsKey("pagingFlag") && requestParams.get("pagingFlag") != null)
            {
                pagingFlag = Boolean.parseBoolean(requestParams.get("pagingFlag").toString());
            }
            boolean countFlag = true;
            if (requestParams.containsKey("countFlag") && requestParams.get("countFlag") != null)
            {
                countFlag = Boolean.parseBoolean(requestParams.get("countFlag").toString());
            }

            ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
            ArrayList filter_params = (ArrayList) requestParams.get("filter_values");
            String selQuery = "select distinct c ";
            String selCountQuery = "select count(distinct c) ";
            String Hql = "from CrmCase c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if (ind > -1)
            {
                int index = Integer.valueOf(filterQuery.substring(ind + 1, ind + 2));
                filterQuery = filterQuery.replaceAll("(" + index + ")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            Hql += filterQuery;

            String selectInQuery = Hql;
            String orderQuery = "";
            ArrayList order_by = null;
            ArrayList order_type = null;
            if (requestParams.containsKey("order_by"))
                order_by = (ArrayList) requestParams.get("order_by");
            if (requestParams.containsKey("order_type"))
                order_type = (ArrayList) requestParams.get("order_type");

            orderQuery = StringUtil.orderQuery(order_by, order_type);
            if (StringUtil.isNullOrEmpty(orderQuery))
            {
                orderQuery = " order by c.casename ";
            }
            selectInQuery += orderQuery;

            if (countFlag)
            {
                ll = executeQuery(selCountQuery+selectInQuery, filter_params.toArray());
                if (ll != null && !ll.isEmpty()){
                	dl = ((Number)ll.get(0)).intValue();
                }
            }
            if (pagingFlag)
            {
                int start = 0;
                int limit = 25;
                if (requestParams.containsKey("start") && requestParams.containsKey("limit"))
                {
                    start = Integer.parseInt(requestParams.get("start").toString());
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                }
                ll = executeQueryPaging(selQuery+selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
                if (!countFlag)
                {
                    dl = ll.size();
                }
            }else{
            	ll = executeQuery(selQuery+selectInQuery, filter_params.toArray());
            }
        } catch (Exception ex)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.getAllCases", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject getCaseProducts(ArrayList filter_names, ArrayList filter_params) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        String Hql = "from CaseProducts cp ";
        String filterQuery = StringUtil.filterQuery(filter_names, "where");
        Hql += filterQuery;
        String selectInQuery = Hql;

        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public Map<String, List<CrmProduct>> getCaseProducts(List<String> caseIds)
    {
        Map<String, List<CrmProduct>> productMap = new HashMap<String, List<CrmProduct>>();
        if (caseIds != null && !caseIds.isEmpty())
        {
            StringBuilder query = new StringBuilder("select cp.caseId, cp.productId from CaseProducts cp where cp.caseId in (");
            
            for (String caseId: caseIds)
            {
                query.append('\'');
                query.append(caseId);
                query.append('\'');
                query.append(',');
            }
            
            query.deleteCharAt(query.length() - 1);
            query.append(')');
            
            List<Object[]> results = getHibernateTemplate().find(query.toString());
            
            if (results != null)
            {
                for (Object[] result: results)
                {
                    String caseId = (String) result[0];
                    CrmProduct product = (CrmProduct) result[1];
                    
                    if (productMap.containsKey(caseId))
                    {
                        List<CrmProduct> productList = productMap.get(caseId);
                        productList.add(product);
                    }
                    else
                    {
                        List<CrmProduct> productList = new ArrayList<CrmProduct>();
                        productList.add(product);
                        productMap.put(caseId, productList);
                    }
                }
            }
        }
        return productMap;
    }
    /**
     * @param Caseid
     * @param products
     * @throws ServiceException
     */

    public void saveCaseProducts(String[] caseids,String[] products) throws ServiceException{
        String query="delete from CaseProducts where caseid.caseid in (:caseids)";
        Map map = new HashMap();
        map.put("caseids", caseids);
        executeUpdate(query,null,map);
        if(products!=null ){
			for (String caseid : caseids) {
				for (int i = 0; i < products.length; i++) {
					if (!StringUtil.isNullOrEmpty(products[i])) {
						CaseProducts caseProductObj = new CaseProducts();
						caseProductObj.setCaseid((CrmCase) get(CrmCase.class, caseid));
						caseProductObj.setProductId((CrmProduct) get(CrmProduct.class, products[i]));

						save(caseProductObj);
					}
				}
			}
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.krawler.spring.crm.caseModule.crmCaseDAO#getCrmCaseCustomData(java
     * .util.HashMap)
     */
    public KwlReturnObject getCrmCaseCustomData(HashMap<String, Object> requestParams) throws ServiceException
    {
        List ll = null;
        int dl = 0;
        try
        {
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();

            if (requestParams.containsKey("filter_names"))
            {
                filter_names = new ArrayList((List<String>) requestParams.get("filter_names"));
            }
            if (requestParams.containsKey("filter_values"))
            {
                filter_params = new ArrayList((List<String>) requestParams.get("filter_values"));
            }

            String Hql = " from CrmCase ";

            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if (ind > -1)
            {
                int index = Integer.valueOf(filterQuery.substring(ind + 1, ind + 2));
                filterQuery = filterQuery.replaceAll("(" + index + ")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            Hql += filterQuery;

            ll = executeQueryPaging(Hql, filter_params.toArray(), new Integer[] { 0, 1 });
            dl = ll.size();
        } catch (Exception e)
        {
            e.printStackTrace();
            throw ServiceException.FAILURE("crmCaseDAOImpl.getCrmCaseCustomData : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public List<CrmCase> getCases(List<String> recordIds)
    {
        if (recordIds == null || recordIds.isEmpty())
        {
            return null;
        }
        StringBuilder hql = new StringBuilder("from CrmCase where caseid in (");
        
        for (String record: recordIds)
        {
            hql.append("'" + record + "',");
        }
        
        hql.deleteCharAt(hql.length() - 1);
        hql.append(")");

        return executeQuery(hql.toString());
    }

    /**
     *
     * @param list
     * @param companyid
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    @Override
    public HashMap<String, CrmCaseCustomData> getCaseCustomDataMap(List<String> list, String companyid) throws ServiceException {
        List<CrmCaseCustomData> ll = null;
        HashMap<String, CrmCaseCustomData> caseCustomDataMap = new HashMap<String, CrmCaseCustomData>();
        try {
            String filterQuery = "";
            List<List> paramll = new ArrayList();
            List<String> paramnames = new ArrayList();
            if(!list.isEmpty()){
                filterQuery = " and c.caseid in (:recordlist) ";
                paramll.add(list);
                paramnames.add("recordlist");
            }
            String Hql = "select c from CrmCaseCustomData c where c.company.companyID = '"+companyid+"' "+filterQuery;
            ll = executeCollectionQuery(Hql,paramnames,paramll);
            for(CrmCaseCustomData caseCustomObj : ll){
                caseCustomDataMap.put(caseCustomObj.getCaseid(), caseCustomObj);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCaseDAOImpl.getCaseCustomDataMap", ex);
        }
        return caseCustomDataMap;
    }

	@Override
	public Map<String, CrmAccount> getCaseAccounts(List<String> caseIds) {
        Map<String, CrmAccount> map = new HashMap<String, CrmAccount>();
        if (caseIds != null && !caseIds.isEmpty())
        {
            StringBuilder query = new StringBuilder("select c.caseid, c.crmAccount from CrmCase c where c.caseid in (");
            for (String caseId: caseIds)
            {
                query.append('\'');
                query.append(caseId);
                query.append('\'');
                query.append(',');
            }
            
            query.deleteCharAt(query.length() - 1);
            query.append(')');
            
            List<Object[]> results = getHibernateTemplate().find(query.toString());
            
            if (results != null)
            {
                for (Object[] result: results)
                {
                    String caseId = (String) result[0];
                    CrmAccount account = (CrmAccount) result[1];
                    map.put(caseId, account);
                }
            }
        }
        return map;
	}

	@Override
	public Map<String, CrmContact> getCaseContacts(List<String> caseIds) {
        Map<String, CrmContact> map = new HashMap<String, CrmContact>();
        if (caseIds != null && !caseIds.isEmpty())
        {
            StringBuilder query = new StringBuilder("select c.caseid, c.crmContact from CrmCase c where c.caseid in (");
            for (String caseId: caseIds)
            {
                query.append('\'');
                query.append(caseId);
                query.append('\'');
                query.append(',');
            }
            
            query.deleteCharAt(query.length() - 1);
            query.append(')');
            
            List<Object[]> results = getHibernateTemplate().find(query.toString());
            
            if (results != null)
            {
                for (Object[] result: results)
                {
                    String caseId = (String) result[0];
                    CrmContact contact = (CrmContact) result[1];
                    map.put(caseId, contact);
                }
            }
        }
        return map;
	}

	@Override
	public Map<String, User> getCaseOwners(List<String> caseIds) {
        Map<String, User> map = new HashMap<String, User>();
        if (caseIds != null && !caseIds.isEmpty())
        {
            StringBuilder query = new StringBuilder("select c.caseid, c.usersByUserid from CrmCase c where c.caseid in (");
            for (String caseId: caseIds)
            {
                query.append('\'');
                query.append(caseId);
                query.append('\'');
                query.append(',');
            }
            
            query.deleteCharAt(query.length() - 1);
            query.append(')');
            
            List<Object[]> results = getHibernateTemplate().find(query.toString());
            
            if (results != null)
            {
                for (Object[] result: results)
                {
                    String caseId = (String) result[0];
                    User contact = (User) result[1];
                    map.put(caseId, contact);
                }
            }
        }
        return map;
	}

    //TODO (Kuldeep Singh) : Please test it Porperly for all fields while using this function for Mass update
    // Now it is working fine for delete.
    public KwlReturnObject updateMassCases(JSONObject jobj) throws ServiceException
    {
        String hqlVarPart = "";
        List<Object> params = new ArrayList<Object>();
        List ll = new ArrayList();
        int dl = 0;
        try
        {
            String companyid = null;
            String userid = null;
            String id = "";
            String[] caseids = (String[])jobj.get("caseid");
            CrmCase crmCase = null;
            
            if (jobj.has("casename"))
            {
                hqlVarPart += " casename = ?,";
            	params.add(jobj.getString("casename"));
            }
            if (jobj.has("contactnameid"))
            {
                hqlVarPart += " crmContact = ?,";
            	params.add(get(CrmContact.class, jobj.getString("contactnameid")));
            }
            if (jobj.has("companyid"))
            {
                hqlVarPart += " company = ?,";
            	params.add(get(Company.class, jobj.getString("companyid")));
            }
            if (jobj.has("productnameid"))
            {
                saveCaseProducts(caseids,jobj.getString("productnameid").split(","));
            }
            if (jobj.has("casetypeid"))
            {
                hqlVarPart += " crmCombodataByCasetypeid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("casetypeid")));
            }
            if (jobj.has("casestatusid"))
            {
                hqlVarPart += " crmCombodataByCasestatusid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("casestatusid")));
            }
            if (jobj.has("casepriorityid"))
            {
                hqlVarPart += " crmCombodataByCasepriorityid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("casepriorityid")));
            }
            if (jobj.has("accountnameid"))
            {
                hqlVarPart += " crmAccount = ?,";
            	params.add(get(CrmAccount.class, jobj.getString("accountnameid")));
            }
            if (jobj.has("description"))
            {
                hqlVarPart += " description = ?,";
            	params.add(jobj.getString("description"));
            }
            if (jobj.has("subject"))
            {
                hqlVarPart += " subject = ?,";
            	params.add(jobj.getString("subject"));
            }
            if (jobj.has("updatedon"))
            {
                hqlVarPart += " updatedOn = ?,";
            	params.add(jobj.getLong("updatedon"));
            }
            if (jobj.has("userid"))
            {
                hqlVarPart += " usersByUpdatedbyid = ?,";
            	params.add(get(User.class, jobj.getString("userid")));
            }
            if (jobj.has("caseownerid"))
            {
                hqlVarPart += " usersByUserid = ?,";
            	params.add(get(User.class, jobj.getString("caseownerid")));
            }
            if (jobj.has("caseassignedtoid"))
            {
                hqlVarPart += " assignedto = ?,";
            	params.add(get(User.class, jobj.getString("caseassignedtoid")));
            }
            if (jobj.has("validflag"))
            {
                hqlVarPart += " validflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("validflag")));
            }
            if (jobj.has("createdon"))
            {          Long createdOn = jobj.getLong("createdon");
	            		hqlVarPart += " createdOn = ?,";
	                	params.add(createdOn);
            }
            	
            if (jobj.has("deleteflag"))
            {
                hqlVarPart += " deleteflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("deleteflag")));
                String query = "delete from CaseProducts where caseid.caseid in (:caseids)";
                Map map = new HashMap();
                map.put("caseids", caseids);
                executeUpdate(query, null, map);
            }

            if (jobj.has("CrmCaseCustomDataobj")&&jobj.getBoolean("CrmCaseCustomDataobj"))
            {
                linkCustomData();
            }

            hqlVarPart = hqlVarPart.substring(0, Math.max(0,hqlVarPart.lastIndexOf(',')));
            String hql = "update CrmCase set "+hqlVarPart+" where caseid in (:cases)";
            Map map = new HashMap();
            map.put("cases", caseids);
            executeUpdate(hql, params.toArray(), map);

        } catch (JSONException e)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.editCases : " + e.getMessage(), e);
        } catch (DataAccessException e)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.editCases : " + e.getMessage(), e);
        } catch (Exception e)
        {
            throw ServiceException.FAILURE("crmCaseDAOImpl.editCases : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public void linkCustomData() {
		String query="update crm_case c inner join crmcasecustomdata cu on cu.caseid=c.caseid and c.crmcasecustomdataref is null set c.crmcasecustomdataref=c.caseid";
        updateJDBC(query,new Object[]{});
	}

    @Override
    public List<Object[]> getCaseSLA(int cronDuration) throws ServiceException {
        List<Object[]> caselist = new ArrayList<Object[]>();
        try {
            DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String currentTime = dt.format(cal.getTime());
            cal.add(Calendar.MINUTE, -cronDuration);//15 Minutes by default
            String lastTime = dt.format(cal.getTime());

            //String serverTimezone = ConfigReader.getinstance().get("SERVER_TIMEZONE");//"-05:00";
            ArrayList filter_params = new ArrayList();
            filter_params.add("Case");
            filter_params.add(Constants.CASE_SLAID);
            filter_params.add("T");
            String Hql = "select dh.dbcolumnname, dh.pojoheadername, ch.company from default_header dh " +
                    " inner join column_header ch on dh.id = ch.defaultheader where dh.modulename = ? and dh.configid = ? and dh.customflag = ? ";
            List<Object[]> ll = executeNativeQuery(Hql, filter_params.toArray());
            String dbcolumnname = "";
            String companyid = "";
            for(Object[] row : ll) {
                dbcolumnname = row[0]!=null?row[0].toString():"";
                companyid = row[2].toString();

                Hql = "select cc.subject, concat(fname,' ',lname) as name, ca.accountname, dm.`value`, cc.userid, users.emailid " +
                    " from crm_case as cc " +
                    " inner join users on cc.userid = users.userid " +//
//                    " inner join timezone as tz on tz.timzoneid = users.timezone "+
                    " inner join crmcasecustomdata as ccd on ccd.caseid = cc.caseid "+
                    " inner join defaultmasteritem dm on dm.id = ccd."+dbcolumnname+" "+
                    " left join defaultmasteritem dms on dms.id = cc.casestatusid "+
                    " left join crm_account ca on ca.accountid = cc.accountnameid "+ //INTERVAL 01.30 HOUR_MINUTE
//                    " where DATE_ADD(CONVERT_TZ(FROM_UNIXTIME(cc.createdonNE/1000),'"+serverTimezone+"',tz.difference), INTERVAL dm.percentStage HOUR) < ? " +
//                    " and DATE_ADD(CONVERT_TZ(FROM_UNIXTIME(cc.createdonNE/1000),'"+serverTimezone+"',tz.difference), INTERVAL dm.percentStage HOUR) >= ? " +
                    " where DATE_ADD(FROM_UNIXTIME(cc.createdonNE/1000), INTERVAL dm.percentStage HOUR) < ? " +
                    " and DATE_ADD(FROM_UNIXTIME(cc.createdonNE/1000), INTERVAL dm.percentStage HOUR) >= ? " +
                    " and cc.companyid = ?  and cc.validflag = 1 and cc.deleteflag = 0 and cc.isarchive = 'F' " +
                    " and isNull(dm.percentStage) != 1 and (dms.mainID != ? or cc.casestatusid is null) ";
                filter_params.clear();
                filter_params.add(currentTime);
                filter_params.add(lastTime);
                filter_params.add(companyid);
                filter_params.add(Constants.CASESTATUS_CLOSED);
                List<Object[]> list = executeNativeQuery(Hql, filter_params.toArray());
                for(Object[] casedata1 : list) {
                    String ownerid = casedata1[4].toString();

                    Hql = "select concat(fname,' ',lname) as managername, users.emailid, am.manid, company.subdomain, company.notificationtype, users.notificationtype,  company.companyname, company.emailid, creator" +
                        " from assignmanager as am " +
                        " inner join users on am.manid = users.userid " +
                        " inner join company on company.companyid = users.company " +
                        " and am.empid = ?  ";
                    filter_params.clear();
                    filter_params.add(ownerid);
                    List<Object[]> manlist = executeNativeQuery(Hql, filter_params.toArray());
                    for(Object[] mandata : manlist) {
                        String managername = mandata[0].toString();
                        String manageremail = mandata[1].toString();
                        String managerid = mandata[2].toString();
                        String subdomain = mandata[3].toString();
                        int cnotify = Integer.parseInt(mandata[4].toString());
                        int unotify = Integer.parseInt(mandata[5].toString());
                        String companyname = mandata[6].toString();
                        String companyemailid = mandata[7].toString();
                        String creatorid = mandata[8].toString();
                        Object[] casedata = new Object[11];
                        casedata[0] = casedata1[0];
                        casedata[1] = casedata1[1];
                        casedata[2] = casedata1[2];
                        casedata[3] = casedata1[3];
                        casedata[4] = casedata1[4];
                        casedata[5] = casedata1[5];
                        casedata[6] = managername;
                        casedata[7] = manageremail;
                        casedata[8] = subdomain;
                        casedata[9] = cnotify;
                        casedata[10] = unotify;
                        casedata[11] = companyname;
                        casedata[12] = companyemailid;
                        casedata[13] = creatorid;
                        caselist.add(casedata);
                    }
                    
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ServiceException.FAILURE("crmCaseDAOImpl.getCaseSLA", ex);            
        }
        return caselist;
    }

    @Override
    public List<String> getCaseCustomUserID(HashMap<String, Object> requestParams) throws ServiceException {
        List<String> userIds = new ArrayList();
        try {
            String companyid = "";
            if(requestParams.containsKey("companyid")) {
                companyid = requestParams.get("companyid").toString();
            }
            String caseid = "";
            if(requestParams.containsKey("caseid")) {
                caseid = requestParams.get("caseid").toString();
            }
            ArrayList filter_params = new ArrayList();
            filter_params.add("Case");
            filter_params.add(Constants.CASE_USERID_CUSTOM);
            filter_params.add("T");
            filter_params.add(companyid);
            String Hql = "select dh.dbcolumnname from default_header dh " +
                    " inner join column_header ch on dh.id = ch.defaultheader where dh.modulename = ? and dh.configid = ? and dh.customflag = ? and ch.company = ? ";
            List<String> ll = executeNativeQuery(Hql, filter_params.toArray());
            String custom_col_name = "";
            for(String name : ll) {
                custom_col_name = name.toString().toLowerCase();

                Hql = "select ccd."+custom_col_name+" from crmcasecustomdata as ccd where ccd.caseid = ? and ccd."+custom_col_name+" is not Null ";
                filter_params.clear();
                filter_params.add(caseid);
                List<String> list = executeNativeQuery(Hql, filter_params.toArray());
                for(String userid : list) {
                    if(!userIds.contains(userid)) {
                        userIds.add(userid);
                    }
                }
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCaseDAOImpl.getCaseCustomUserID", ex);
        }
        return userIds;
    }
    
    public String getCompanyCaseDefaultOwnerID(String companyId) throws ServiceException {
        
            
        	String Hql = "select c.user.userID from CustomerCaseDefaultOwner c where  c.company.companyID=?";
            List ll = executeQuery(Hql, companyId);
            if(ll.isEmpty())
        	return null;
        return (String)ll.get(0);
        
        
    }
    @Override
    public void  saveCustomerDocs(String customerId,String docId,String caseId) throws ServiceException {
		 try{
			 CustomerDocs custdoc=new CustomerDocs();
			 custdoc.setCustomer((CrmCustomer)get(CrmCustomer.class,customerId));
			 custdoc.setCrmCase((CrmCase)get(CrmCase.class,caseId));
			 custdoc.setDoc((Docs)get(Docs.class,docId));
		 	 save(custdoc);
		 }catch(Exception e){
			 logger.warn(e.getMessage());
		 }
	 }
 
    
}
