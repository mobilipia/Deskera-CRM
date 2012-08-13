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
package com.krawler.spring.crm.accountModule; 
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.dao.DataAccessException;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.account.dm.AccountOwnerInfo;
import com.krawler.crm.database.tables.AccountProducts;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmAccountCustomData;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.accountOwners;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class crmAccountDAOImpl extends BaseDAO implements crmAccountDAO {	

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.accountModule.crmAccountDAO#getAccounts(java.util.HashMap)
     */
    public KwlReturnObject getAccounts(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
             ArrayList filter_names = new ArrayList();
             ArrayList filter_params = new ArrayList();

            if(requestParams.containsKey("filter_names")){
                filter_names = (ArrayList) requestParams.get("filter_names");
            }
            if(requestParams.containsKey("filter_params")){
                filter_params = (ArrayList) requestParams.get("filter_params");
            }

            String Hql = "select distinct c from CrmAccount c ";
            if(filter_names.contains("INp.productId.productid")) {
                Hql += " inner join c.crmProducts as p ";
            }
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            Hql += filterQuery;

            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.getActiveAccount : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, "002", "", ll, dl);
    }
    
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.accountModule.crmAccountDAO#getAccounts(java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject getAccounts(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
    	List ll = null;
        Long dl = 0l;
        try {
            requestParams.put(Constants.moduleid,1);
            boolean archive = false;
            if(requestParams.containsKey("isarchive") && requestParams.get("isarchive") != null) {
                archive = Boolean.parseBoolean(requestParams.get("isarchive").toString());
            }
            String companyid = requestParams.get("companyid").toString();
            String appendCase = "and";
            ArrayList filter_params = new ArrayList();

            String selQuery = "select distinct c ";
            String selCountQuery = "select count(distinct c.accountid) ";
            String Hql = " from accountOwners ao inner join ao.account c "+crmManagerCommon.getJoinQuery(requestParams);
            String filterQuery = " where  c.deleteflag=0  and c.isarchive= ? and c.company.companyID= ? ";

            filter_params.add(archive);
            filter_params.add(companyid);

            String Searchjson = "";
            if(requestParams.containsKey("searchJson") && requestParams.get("searchJson") != null) {
                Searchjson = requestParams.get("searchJson").toString();
                if(Searchjson.contains("p.productId.productid")) {
                    Hql += " inner join c.crmProducts as p ";
                }
                if (!StringUtil.isNullOrEmpty(Searchjson)) {
                    requestParams.put(Constants.Searchjson, Searchjson);
                    requestParams.put(Constants.appendCase, appendCase);
                    String mySearchFilterString = String.valueOf(StringUtil.getMyAdvanceSearchString(requestParams).get(Constants.myResult));
                    filterQuery += mySearchFilterString;
                    StringUtil.insertParamAdvanceSearchString(filter_params, Searchjson);
                }
            }

            if(requestParams.containsKey("config") && requestParams.get("config") != null) {
                filterQuery += " and c.validflag=1 ";
            }
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"c.accountname","c.website"};
                    StringUtil.insertParamSearchString(filter_params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    filterQuery +=searchQuery;
                }
            }

            int start = 0;
            int limit = 25;
            boolean ispaging = requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()) && !StringUtil.isNullOrEmpty(requestParams.get("start").toString());
            if (ispaging) {
                if(requestParams.containsKey("iPhoneCRM") && requestParams.get("iPhoneCRM") != null){
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                    start = Integer.parseInt(requestParams.get("start").toString()) * limit;
                }
                else{
                    start = Integer.parseInt(requestParams.get("start").toString());
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                }
            }
            
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                filterQuery += " and ao.usersByUserid.userID in (" + usersList + ") ";
            }

            String orderQuery = " order by c.createdOn desc";
            String dbname = null;
            String dir = null;
            if(requestParams.containsKey("field") && requestParams.get("xfield") != null) {
                dbname = crmManagerCommon.getFieldDbName(requestParams);
                if(dbname!=null){
                    dir = requestParams.get("direction").toString();
                    orderQuery = " order by "+dbname+" "+dir+" ";
                }
            }
            String countQuery = selCountQuery + Hql + filterQuery;
            String query = selQuery + Hql + filterQuery + orderQuery;
            String export = "";
            if(requestParams.containsKey("export") && requestParams.get("export") != null) {
                countQuery = query;
                export = requestParams.get("export").toString();
            }

            ll = executeQuery(countQuery, filter_params.toArray());
            
            if (ll != null && !ll.isEmpty() && StringUtil.isNullOrEmpty(export))
            {
                dl = (Long) ll.get(0);
            }
            String association = null;
            String property = null;
            if(dbname!=null && dbname.split("\\.").length>2){
            	association = dbname.substring(0, dbname.lastIndexOf("."));
            	property = dbname.substring(dbname.lastIndexOf(".") + 1, dbname.length());
            }
            if(StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(query, filter_params.toArray(), new Integer[]{start, limit});
                
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.getAccounts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl.intValue());
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.accountModule.crmAccountDAO#getAllAccounts(java.util.HashMap, java.util.ArrayList, java.util.ArrayList)
     */
    public KwlReturnObject getAllAccounts(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select distinct c from accountOwners ao inner join ao.account c ";
//            String Hql = "select c from CrmAccount c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;
            int ind = Hql.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(Hql.substring(ind+1,ind+2));
                Hql = Hql.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }

            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=StringEscapeUtils.escapeJavaScript(requestParams.get("ss").toString());
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"c.accountname"};
                    StringUtil.insertParamSearchString(filter_params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    Hql +=searchQuery;
                }
            }

            String selectInQuery = Hql;
            boolean pagingFlag = false;
            if(requestParams.containsKey("pagingFlag") && requestParams.get("pagingFlag") != null) {
                pagingFlag = Boolean.parseBoolean(requestParams.get("pagingFlag").toString());
            }
            boolean countFlag = true;
            if(requestParams.containsKey("countFlag") && requestParams.get("countFlag") != null) {
                countFlag = Boolean.parseBoolean(requestParams.get("countFlag").toString());
            }

            ArrayList order_by = null;
            ArrayList order_type = null;
            if(requestParams.containsKey("order_by"))
                order_by =(ArrayList) requestParams.get("order_by");
            if(requestParams.containsKey("order_type"))
                order_type = (ArrayList) requestParams.get("order_type");
            String orderQuery = StringUtil.orderQuery(order_by,order_type);
            if(StringUtil.isNullOrEmpty(orderQuery)) {
                orderQuery = " order by c.accountname ";
            }
            selectInQuery += orderQuery;

            if(countFlag) {
                ll = executeQuery(selectInQuery, filter_params.toArray());
                dl= ll.size();
            }
            if(pagingFlag) {
                int start = 0;
                int limit = 25;
                if (requestParams.containsKey("start") && requestParams.containsKey("limit")) {
                    start = Integer.parseInt(requestParams.get("start").toString());
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                }
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
                if(!countFlag) {
                    dl = ll.size();
                }
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.getAllAccounts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.accountModule.crmAccountDAO#addAccounts(com.krawler.utils.json.base.JSONObject)
     */
        public KwlReturnObject addAccounts(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            CrmAccount crmAccount= new CrmAccount();
            if(jobj.has("accountname")) {
                crmAccount.setAccountname(jobj.getString("accountname"));
            }
            if(jobj.has("accounttypeid")) {
                crmAccount.setCrmCombodataByAccounttypeid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("accounttypeid")));
            }
            if(jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmAccount.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("description")) {
                crmAccount.setDescription(jobj.getString("description"));
            }
            if(jobj.has("website")) {
                crmAccount.setWebsite(jobj.getString("website"));
            }
            if(jobj.has("email")) {
                crmAccount.setEmail(jobj.getString("email"));
            }
            if(jobj.has("address")) {
                crmAccount.setMailstreet(jobj.getString("address"));
            }

            if(jobj.has("price")) {
                crmAccount.setPrice(jobj.getString("price"));
            }
            if(jobj.has("phone")) {
                crmAccount.setPhone(jobj.getString("phone"));
            }
            if(jobj.has("revenue")) {
                crmAccount.setRevenue(jobj.getString("revenue"));
            }
            if(jobj.has("industryid")) {
                crmAccount.setCrmCombodataByIndustryid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("industryid")));
            }
            if(jobj.has("updatedon")) {
                crmAccount.setUpdatedOn(System.currentTimeMillis());
            }
            if(jobj.has("createdon")) {
                 crmAccount.setCreatedOn(jobj.getLong("createdon"));
            } else {
                crmAccount.setCreatedOn(System.currentTimeMillis());
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmAccount.setUsersByUpdatedbyid((User) get(User.class, userid));
                crmAccount.setUsersByCreatedbyid((User) get(User.class, userid));
            }
            if(jobj.has("validflag")) {
                crmAccount.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("accountid")) {
                id = jobj.getString("accountid");
                crmAccount.setAccountid(id);
            }
            if(jobj.has("deleteflag")) {
                crmAccount.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if(jobj.has("isarchive")){
            	crmAccount.setIsarchive(jobj.getBoolean("isarchive"));
            }
            if(jobj.has("CrmAccountCustomDataobj")){
                crmAccount.setCrmAccountCustomDataobj((CrmAccountCustomData) get(CrmAccountCustomData.class, jobj.getString("CrmAccountCustomDataobj")));
            }
            if(jobj.has("leadid")) {
                crmAccount.setCrmLead((CrmLead) get(CrmLead.class, jobj.getString("leadid")));
            }

            save(crmAccount);
            if(jobj.has("accountownerid")) {
                setMainAccOwner(new String[]{id},jobj.getString("accountownerid"));
//                crmAccount.setUsersByUserid((User) hibernateTemplate.get(User.class, jobj.getString("accountownerid")));
            }
            if(jobj.has("productid")) {
                saveAccountProducts(new String[]{id},jobj.getString("productid").split(","));
            }
            ll.add(crmAccount);
        } catch(JSONException e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.addAccounts : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.addAccounts : "+e.getMessage(), e);
        } catch(Exception e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.addAccounts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.accountModule.crmAccountDAO#editAccounts(com.krawler.utils.json.base.JSONObject)
     */
    public KwlReturnObject editAccounts(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            if(jobj.has("accountid")) {
                id = jobj.getString("accountid");
            }
            CrmAccount crmAccount = (CrmAccount) get(CrmAccount.class, id);
            if(jobj.has("accountname")) {
                crmAccount.setAccountname(jobj.getString("accountname"));
            }
            if(jobj.has("accounttypeid")) {
                crmAccount.setCrmCombodataByAccounttypeid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("accounttypeid")));
            }
            if(jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmAccount.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("description")) {
                crmAccount.setDescription(jobj.getString("description"));
            }
            if(jobj.has("website")) {
                crmAccount.setWebsite(jobj.getString("website"));
            }
            if(jobj.has("email")) {
                crmAccount.setEmail(jobj.getString("email"));
            }
            if(jobj.has("address")) {
                crmAccount.setMailstreet(jobj.getString("address"));
            }
            if(jobj.has("productid")) {
                saveAccountProducts(new String[]{id},jobj.getString("productid").split(","));
            }
            if(jobj.has("price")) {
                crmAccount.setPrice(jobj.getString("price"));
            }
            if(jobj.has("phone")) {
                crmAccount.setPhone(jobj.getString("phone"));
            }
            if(jobj.has("revenue")) {
                crmAccount.setRevenue(jobj.getString("revenue"));
            }
            if(jobj.has("industryid")) {
                crmAccount.setCrmCombodataByIndustryid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("industryid")));
            }
            if(jobj.has("updatedon")) {
                crmAccount.setUpdatedOn(System.currentTimeMillis());
            }
            if(jobj.has("createdon")) {
                crmAccount.setCreatedOn(jobj.getLong("createdon"));
            }
            else{
                crmAccount.setCreatedOn(System.currentTimeMillis());
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmAccount.setUsersByUpdatedbyid((User) get(User.class, userid));
//                crmAccount.setUsersByCreatedbyid((User) hibernateTemplate.get(User.class, userid));
            }
            if(jobj.has("accountownerid")) {
                setMainAccOwner(new String[]{id},jobj.getString("accountownerid"));
//                crmAccount.setUsersByUserid((User) hibernateTemplate.get(User.class, jobj.getString("accountownerid")));
            }
            if(jobj.has("validflag")) {
                crmAccount.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }     
            if(jobj.has("isarchive")){
            	crmAccount.setIsarchive(jobj.getBoolean("isarchive"));
            }
            if(jobj.has("deleteflag")) {
                crmAccount.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
                try{
                	if(Integer.parseInt(jobj.getString("deleteflag"))==1){
                		String query = "delete from AccountProducts where accountid.accountid=?";
                		executeUpdate(query, id);
                	}
                }catch(Exception te){
                	logger.warn("Products related to the account could not be deleted :", te);
                }
            }
            if(jobj.has("CrmAccountCustomDataobj")){
                crmAccount.setCrmAccountCustomDataobj((CrmAccountCustomData) get(CrmAccountCustomData.class, jobj.getString("CrmAccountCustomDataobj")));
            }
            if(jobj.has("leadid")) {
                crmAccount.setCrmLead((CrmLead) get(CrmLead.class, jobj.getString("leadid")));
            }
            saveOrUpdate(crmAccount);

            ll.add(crmAccount);
        } catch(JSONException e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.editAccounts : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.editAccounts : "+e.getMessage(), e);
        } catch(Exception e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.editAccounts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /**
     * @param accid
     * @param ownerid
     * @throws ServiceException
     */
    @Override
    public void setMainAccOwner(String[] accids,String ownerid) throws ServiceException{
        String hql="delete from accountOwners c where c.account.accountid in (:accids) and mainOwner = true";
        Map map = new HashMap();
        map.put("accids", accids);
        executeUpdate(hql,null, map);
        User user = (User)get(User.class, ownerid);
        for(String accid : accids){
            accountOwners accountOwnersObj = new accountOwners();
            accountOwnersObj.setAccount((CrmAccount)get(CrmAccount.class, accid));
            accountOwnersObj.setUsersByUserid(user);
            accountOwnersObj.setMainOwner(true);
            save(accountOwnersObj);
        }
        
    }
    
    @Override
    public List<CrmAccount> getOwnerChangedAccounts(String newOwnerId,String[] accountIds)throws ServiceException{
    	String hql="select ao.account from accountOwners ao where ao.mainOwner = ? and ao.account.accountid in (:accountIds) "+
    	           "and ao.usersByUserid.userID not in(:newOwnerId) ";
    	Map<String, Object> namedParams=new HashMap<String, Object>();
    	namedParams.put("accountIds", accountIds);
    	namedParams.put("newOwnerId", new String[]{newOwnerId});
    	return executeQuery(hql, new Object[]{true}, namedParams);
 	}

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.accountModule.crmAccountDAO#saveAccOwners(java.util.HashMap)
     */
    public KwlReturnObject saveAccOwners(HashMap<String, Object> requestParams) throws Exception {
        JSONObject myjobj = new JSONObject();
        List ll = new ArrayList();
        try {
            String accid = "";
            if(requestParams.containsKey("accid") && requestParams.get("accid") != null) {
                accid =  requestParams.get("accid").toString();
            }
            String owners = "";
            if(requestParams.containsKey("owners") && requestParams.get("owners") != null) {
                owners =  requestParams.get("owners").toString();
            }
            String mainowner = "";
            if(requestParams.containsKey("mainOwner") && requestParams.get("mainOwner") != null) {
                mainowner =  requestParams.get("mainOwner").toString();
            }
//            saveAccOwners(accid,mainowner,owners) ;
//            myjobj.put("success", true);
            String hql="delete from accountOwners c where c.account.accountid = ? ";
            executeUpdate( hql, accid);

            accountOwners accountOwnersObj = new accountOwners();
            accountOwnersObj.setAccount((CrmAccount)get(CrmAccount.class, accid));
            accountOwnersObj.setUsersByUserid((User)get(User.class, mainowner));
            accountOwnersObj.setMainOwner(true);
            save(accountOwnersObj);

            if(!StringUtil.isNullOrEmpty(owners) && !owners.equalsIgnoreCase("undefined") ){
                String[] ownerIds = owners.split(",");
                for (int i = 0;i < ownerIds.length;i++){
                    accountOwnersObj = new accountOwners();
                    accountOwnersObj.setAccount((CrmAccount)get(CrmAccount.class, accid));
                    accountOwnersObj.setUsersByUserid((User)get(User.class, ownerIds[i]));
                    accountOwnersObj.setMainOwner(false);
                    save(accountOwnersObj);
                }
            }
            ll.add(accountOwnersObj);
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.saveAccOwners", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, 1);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.accountModule.crmAccountDAO#getAccountOwners(java.util.HashMap, java.util.ArrayList, java.util.ArrayList)
     */
    public KwlReturnObject getAccountOwners(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "";
            if (requestParams.containsKey("usersList") && requestParams.get("usersList") != null) {
                Hql = " select distinct c from accountOwners ao inner join ao.account c where c.crmLead.usersByCreatedbyid = c.usersByCreatedbyid.userID ";
                Hql += StringUtil.filterQuery(filter_names, "and");
                StringBuffer usersList = (StringBuffer) requestParams.get("usersList");
                Hql += " and ao.usersByUserid.userID in (" + usersList + ") ";
            } else {
                boolean distinctFlag = false;
                if(requestParams.containsKey("distinctFlag") && requestParams.get("distinctFlag") != null){
                    distinctFlag = (Boolean) requestParams.get("distinctFlag");
                }

                if(distinctFlag) {
                    Hql = "select distinct c from accountOwners ao inner join ao.account c ";
                } else {
                    Hql = "select ao, c from accountOwners ao inner join ao.account c ";
                }
                Hql += StringUtil.filterQuery(filter_names, "where");
            }
            
            ll = executeQuery(Hql, filter_params.toArray());
            dl= ll.size();
            boolean pagingFlag = false;
            if(requestParams.containsKey("pagingFlag") && requestParams.get("pagingFlag") != null) {
                pagingFlag = Boolean.parseBoolean(requestParams.get("pagingFlag").toString());
            }
            if(pagingFlag) {
                int start = 0;
                int limit = 25;
                if (requestParams.containsKey("start") && requestParams.containsKey("limit")) {
                    start = Integer.parseInt(requestParams.get("start").toString());
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                }
                ll = executeQueryPaging(Hql, filter_params.toArray(), new Integer[]{start, limit});
                
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.getAccountOwners : "+ ex.getMessage(), ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.accountModule.crmAccountDAO#getAccountRevenue(java.util.HashMap, java.util.ArrayList, java.util.ArrayList)
     */
    public double getAccountRevenue(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        double revenue =0.0;
        try {
            String hql = "select distinct c.accountid from accountOwners ao inner join ao.account c ";
            hql += StringUtil.filterQuery(filter_names, "where");
            hql = " select sum(ca.revenue) from CrmAccount ca where ca.accountid in("+hql+")";
            ll = executeQuery(hql, filter_params.toArray());
            if(ll!= null && ll.size() >0){
                revenue = ll.get(0)==null?0:Double.parseDouble((String)ll.get(0));
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.getAccountOwners : "+ ex.getMessage(), ex);
        }
        return revenue;
    }

    /**
     * @param Accountid
     * @param products
     * @throws ServiceException
     */
    @Override
    public void saveAccountProducts(String[] accids,String[] products) throws ServiceException{
        String query="delete from AccountProducts where accountid.accountid in (:accids)";
        Map map = new HashMap();
        map.put("accids", accids);
        executeUpdate(query,null,map);
        if(products!=null ){
			for (String accid : accids) {
				for (int i = 0; i < products.length; i++) {
					if (!StringUtil.isNullOrEmpty(products[i])) {
						AccountProducts accountProductObj = new AccountProducts();
						accountProductObj.setAccountid((CrmAccount) get(CrmAccount.class, accid));
						accountProductObj.setProductId((CrmProduct) get(CrmProduct.class, products[i]));
						
						save(accountProductObj);
					}
				}
			}
        }
    }
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.accountModule.crmAccountDAO#getAccountProducts(java.util.ArrayList, java.util.ArrayList)
     */
    public KwlReturnObject getAccountProducts(ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "from AccountProducts ap ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;
            String selectInQuery = Hql ;

            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl= ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.getAccountProducts : "+ ex.getMessage(), ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.accountModule.crmAccountDAO#getCrmAccountCustomData(java.util.HashMap)
     */
    public KwlReturnObject getCrmAccountCustomData(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            ArrayList filter_names = new ArrayList();
             ArrayList filter_params = new ArrayList();

            if(requestParams.containsKey("filter_names")){
                filter_names = new ArrayList((List<String>) requestParams.get("filter_names"));
            }
            if(requestParams.containsKey("filter_values")){
                filter_params = new ArrayList((List<String>) requestParams.get("filter_values"));
            }

            String Hql = "from CrmAccountCustomData ";

            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            Hql += filterQuery;

            ll = executeQueryPaging(Hql,filter_params.toArray(),new Integer[]{0, 1});
            dl = ll.size();
        } catch (Exception e) {
            e.printStackTrace();
            throw ServiceException.FAILURE("crmAccountDAOImpl.getCrmAccountCustomData : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll,dl);
    }

    @Override
    public void setCustomData(CrmAccount crmAccount, CrmAccountCustomData crmAccountCustomData) {
        crmAccount.setCrmAccountCustomDataobj(crmAccountCustomData);
        save(crmAccount);
    }


    public void setCustomData(CrmAccount crmAcc, JSONArray cstmData) {
    	StringBuffer fields=new StringBuffer("accountid,company");
    	StringBuffer qmarks=new StringBuffer("?,?");
    	ArrayList params = new ArrayList(); 
    	params.add(crmAcc.getAccountid());
    	params.add(crmAcc.getCompany().getCompanyID());
    	boolean hasValue = false;
        try {
			for (int i = 0; i < cstmData.length(); i++) {
			    JSONObject jobj = cstmData.getJSONObject(i);
			    if(jobj.has(Constants.Crm_custom_field)){
			        String fieldname = jobj.getString(Constants.Crm_custom_field);
			        String fielddbname = jobj.getString(fieldname);
			        String fieldValue = jobj.getString(fielddbname);
			        hasValue = true;
			        fielddbname = fielddbname.replace("c", "C");
			        Integer xtype = Integer.parseInt(jobj.getString("xtype"));
			        if(!StringUtil.isNullOrEmpty(fieldValue) && !StringUtil.isNullOrEmpty(fieldValue.trim()) && !fieldValue.equalsIgnoreCase(Constants.field_data_undefined)){			        	
			        	fields.append(',').append(fielddbname);
			        	qmarks.append(",?");
			        	params.add(fieldValue);
			        }else{
			            if(xtype==7 || xtype==8 || xtype==4){
				        	fields.append(',').append(fielddbname);
				        	qmarks.append(",?");
				        	params.add(null);
			            }
			            else{
			            	fields.append(',').append(fielddbname);
			            	qmarks.append(",?");
				        	params.add("");
			            }
			        }
			    }
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	if(hasValue){
    		StringBuffer sql= new StringBuffer("insert into crmaccountcustomdata (").append(fields).append(")VALUES(").append(qmarks).append(')');
    		updateJDBC(sql.toString(), params.toArray());
    	}
    }
    
    public List<CrmAccount> getAccounts(List<String> recordIds)
    {
        if (recordIds == null || recordIds.isEmpty())
        {
            return null;
        }
        StringBuilder hql = new StringBuilder("from CrmAccount where accountid in (");
        
        for (String record: recordIds)
        {
            hql.append("'" + record + "',");
        }
        
        hql.deleteCharAt(hql.length() - 1);
        hql.append(")");

        return executeQuery(hql.toString());
    }

    @Override
    public HashMap<String, CrmAccountCustomData> getAccountCustomDataMap(List<String> list, String companyid) throws ServiceException {
        List<CrmAccountCustomData> ll = null;
        HashMap<String, CrmAccountCustomData> accountCustomDataMap = new HashMap<String, CrmAccountCustomData>();
        try {
            String filterQuery = "";
            List<List> paramll = new ArrayList();
            List<String> paramnames = new ArrayList();
            if(!list.isEmpty()){
                filterQuery = " and c.accountid in (:recordlist) ";
                paramll.add(list);
                paramnames.add("recordlist");
            }
            String Hql = "select c from CrmAccountCustomData c where c.company.companyID = '"+companyid+"' "+filterQuery;
            ll = executeCollectionQuery(Hql,paramnames,paramll);
            for(CrmAccountCustomData accountCustomObj : ll){
                accountCustomDataMap.put(accountCustomObj.getAccountid(), accountCustomObj);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.getAccountCustomDataMap", ex);
        }
        return accountCustomDataMap;
    }

    @Override
    public Map<String, List<AccountOwnerInfo>> getAccountOwners(List<String> accountIds)
    {
        Map<String, List<AccountOwnerInfo>> ownerMap = new HashMap<String, List<AccountOwnerInfo>>();
        if (accountIds != null && !accountIds.isEmpty())
        {
            StringBuilder query = new StringBuilder("select ao.accountId, ao.usersByUserid, ao from accountOwners ao where ao.accountId in (");
            
            for (String accoundId: accountIds)
            {
                query.append('\'');
                query.append(accoundId);
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
                    String accountId = (String) result[0];
                    AccountOwnerInfo info = new AccountOwnerInfo();
                    info.setUser((User) result[1]);
                    info.setOwner((accountOwners) result[2]);
                    
                    if (ownerMap.containsKey(accountId))
                    {
                        List<AccountOwnerInfo> ownerList = ownerMap.get(accountId);
                        ownerList.add(info);
                    }
                    else
                    {
                        List<AccountOwnerInfo> ownerList = new ArrayList<AccountOwnerInfo>();
                        ownerList.add(info);
                        ownerMap.put(accountId, ownerList);
                    }
                }
            }
        }
        return ownerMap;
    }
    
    public Map<String, List<CrmProduct>> getAccountProducts(List<String> accountIds)
    {
        Map<String, List<CrmProduct>> productMap = new HashMap<String, List<CrmProduct>>();
        if (accountIds != null && !accountIds.isEmpty())
        {
            StringBuilder query = new StringBuilder("select ap.accountId, ap.productId from AccountProducts ap where ap.accountId in (");
            
            for (String accoundId: accountIds)
            {
                query.append('\'');
                query.append(accoundId);
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
                    String accountId = (String) result[0];
                    CrmProduct product = (CrmProduct) result[1];
                    
                    if (productMap.containsKey(accountId))
                    {
                        List<CrmProduct> productList = productMap.get(accountId);
                        productList.add(product);
                    }
                    else
                    {
                        List<CrmProduct> productList = new ArrayList<CrmProduct>();
                        productList.add(product);
                        productMap.put(accountId, productList);
                    }
                }
            }
        }
        return productMap;
    }

    @Override
    public KwlReturnObject updateMassAccount(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        String hqlVarPart = "";
        List<Object> params = new ArrayList<Object>();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String[] accountids = (String[])jobj.get("accountid");
            
            if(jobj.has("accountname")) {
                hqlVarPart += " accountname = ?,";
            	params.add(jobj.getString("accountname"));
            }
            if(jobj.has("accounttypeid")) {
                hqlVarPart += " crmCombodataByAccounttypeid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("accounttypeid")));
            }
            if(jobj.has("companyid")) {
                hqlVarPart += " company = ?,";
            	params.add(get(Company.class, jobj.getString("companyid")));
            }
            if(jobj.has("description")) {
                hqlVarPart += " description = ?,";
            	params.add(jobj.getString("description"));
                
            }
            if(jobj.has("website")) {
                hqlVarPart += " website = ?,";
            	params.add(jobj.getString("website"));

            }
            if(jobj.has("email")) {
                hqlVarPart += " email = ?,";
            	params.add(jobj.getString("email"));
            }
            if(jobj.has("address")) {
                hqlVarPart += " mailstreet = ?,";
            	params.add(jobj.getString("address"));
            }
            if(jobj.has("productid")) {
                saveAccountProducts(accountids,jobj.getString("productid").split(","));
            }
            if(jobj.has("price")) {
                hqlVarPart += " price = ?,";
            	params.add(jobj.getString("price"));
            }
            if(jobj.has("phone")) {
                hqlVarPart += " phone = ?,";
            	params.add(jobj.getString("phone"));
            }
            if(jobj.has("revenue")) {
                hqlVarPart += " revenue = ?,";
            	params.add(jobj.getString("revenue"));
            }
            if(jobj.has("industryid")) {
                hqlVarPart += " crmCombodataByIndustryid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("industryid")));
            }
            if(jobj.has("updatedon")) {
                hqlVarPart += " updatedOn = ?,";
            	params.add(jobj.getLong("updatedon"));
            }
            if(jobj.has("createdon")) {
                Long createdOn = jobj.getLong("createdon");
                hqlVarPart += " createdOn = ?,";
                params.add(createdOn);
                
            }
            if(jobj.has("userid")) {
                
                hqlVarPart += " usersByUpdatedbyid = ?,";
            	params.add(get(User.class, jobj.getString("userid")));
            }
            if(jobj.has("accountownerid")) {
                setMainAccOwner(accountids,jobj.getString("accountownerid"));
            }
            if(jobj.has("validflag")) {
                hqlVarPart += " validflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("deleteflag")) {
                hqlVarPart += " deleteflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("deleteflag")));
                String query = "delete from AccountProducts where accountid.accountid in (:accountids)";
                Map map = new HashMap();
                map.put("accountids", accountids);
                executeUpdate(query, null, map);
            }
            if(jobj.has("CrmAccountCustomDataobj")&&jobj.getBoolean("CrmAccountCustomDataobj")){
            	linkCustomData();
            }
            if(jobj.has("leadid")) {
                hqlVarPart += " crmLead = ?,";
            	params.add(get(CrmLead.class, jobj.getString("leadid")));
            }

            hqlVarPart = hqlVarPart.substring(0, Math.max(0,hqlVarPart.lastIndexOf(',')));
            String hql = "update CrmAccount set "+hqlVarPart+" where accountid in (:accountids)";
            Map map = new HashMap();
            map.put("accountids", accountids);
            executeUpdate(hql, params.toArray(), map);

        } catch(JSONException e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.updateMassAccount : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.updateMassAccount : "+e.getMessage(), e);
        } catch(Exception e) {
            throw ServiceException.FAILURE("crmAccountDAOImpl.updateMassAccount : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);

    }
        
	public void linkCustomData() {
		String query="update crm_account l inner join crmaccountcustomdata cu on cu.accountid=l.accountid and l.crmaccountcustomdataref is null set l.crmaccountcustomdataref=l.accountid";			
        updateJDBC(query,new Object[]{});		
	}
}
