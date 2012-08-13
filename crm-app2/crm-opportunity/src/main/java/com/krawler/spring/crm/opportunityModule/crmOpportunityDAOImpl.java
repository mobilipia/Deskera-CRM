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
package com.krawler.spring.crm.opportunityModule;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.crm.database.tables.CrmOpportunityCustomData;
import com.krawler.crm.database.tables.OppurtunityProducts;
import com.krawler.crm.database.tables.opportunityOwners;
import com.krawler.crm.opportunity.dm.OpportunityOwnerInfo;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataAccessException;

public class crmOpportunityDAOImpl extends BaseDAO implements crmOpportunityDAO {
    @Override
    public KwlReturnObject getOpportunities(HashMap<String, Object> requestParams) throws ServiceException {
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

            String Hql = "select distinct c from CrmOpportunity c ";
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

            ll = executeQuery(Hql,filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.getActiveOpportunity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, "002", "", ll, dl);
    }

     public KwlReturnObject addOpportunities(JSONObject jobj) throws ServiceException {
        JSONObject myjobj = new JSONObject();
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            CrmOpportunity crmOpportunity = new CrmOpportunity();
            if(jobj.has("oppid")) {
                id = jobj.getString("oppid");
                crmOpportunity.setOppid(id);
            }
            //    crmOpportunity.setClosindate(new Date(jobj.getString("closedate")));
            if(jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmOpportunity.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmOpportunity.setUsersByUpdatedbyid((User) get(User.class, userid));
//                crmOpportunity.setUsersByCreatedbyid((User) hibernateTemplate.get(User.class, userid));
            }
            if(jobj.has("oppname")) {
                crmOpportunity.setOppname(jobj.getString("oppname"));
            }
            if(jobj.has("closingdate")) {
                crmOpportunity.setClosingdate(jobj.getLong("closingdate"));
            }
            if(jobj.has("validflag")) {
                crmOpportunity.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            } else {
                crmOpportunity.setValidflag(0);
            }
            if(jobj.has("oppstageid")) {
                crmOpportunity.setCrmCombodataByOppstageid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("oppstageid")));
            }
            if(jobj.has("opptypeid")) {
                crmOpportunity.setCrmCombodataByOpptypeid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("opptypeid")));
            }
            if(jobj.has("oppregionid")) {
                crmOpportunity.setCrmCombodataByRegionid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("oppregionid")));
            }
            if(jobj.has("leadsourceid")) {
                crmOpportunity.setCrmCombodataByLeadsourceid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("leadsourceid")));
            }
            if(jobj.has("probability")) {
                crmOpportunity.setProbability(jobj.getString("probability"));
            }
            if(jobj.has("currencyid")) {
                crmOpportunity.setCurrencyid(jobj.getString("currencyid"));
            }
            if(jobj.has("salesamount")) {
                crmOpportunity.setSalesamount(jobj.getString("salesamount"));
            }
            if(jobj.has("accountnameid")) {
                crmOpportunity.setCrmAccount((CrmAccount) get(CrmAccount.class, jobj.getString("accountnameid")));
            }
            if(jobj.has("price")) {
                crmOpportunity.setPrice(jobj.getString("price"));
            }
            if(jobj.has("updatedon")) {
                crmOpportunity.setUpdatedOn(new Date().getTime());
            }
            if(jobj.has("createdon")) {
                crmOpportunity.setCreatedOn(jobj.getLong("createdon"));
            } 
            else {
                crmOpportunity.setCreatedOn(new Date().getTime());
            }
            if(jobj.has("deleteflag")) {
                crmOpportunity.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if(jobj.has("isarchive")){
            	crmOpportunity.setIsarchive(jobj.getBoolean("isarchive"));
            }
            if(jobj.has("leadid")) {
                crmOpportunity.setCrmLead((CrmLead) get(CrmLead.class, jobj.getString("leadid")));
            }
            if(jobj.has("regionid")){
            	crmOpportunity.setCrmCombodataByRegionid((DefaultMasterItem)get(DefaultMasterItem.class,jobj.getString("regionid")));
            }

            saveOrUpdate(crmOpportunity);
            if(jobj.has("oppownerid")) {
                setMainOppOwner(new String[]{id},jobj.getString("oppownerid"));
            }
            if(jobj.has("productserviceid")) {
                saveOpportunityProducts(new String[]{id},jobj.getString("productserviceid").split(","));
            }
            ll.add(crmOpportunity);

        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.addOpportunity : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.addOpportunity : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.addOpportunity : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject editOpportunities(JSONObject jobj) throws ServiceException {
        JSONObject myjobj = new JSONObject();
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            CrmOpportunity crmOpportunity = null;
            if(jobj.has("oppid")) {
                id = jobj.getString("oppid");
                crmOpportunity = (CrmOpportunity) get(CrmOpportunity.class, id);
                crmOpportunity.setOppid(id);
            }
            //    crmOpportunity.setClosindate(new Date(jobj.getString("closedate")));
            if(jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmOpportunity.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmOpportunity.setUsersByUpdatedbyid((User) get(User.class, userid));
            }
            if(jobj.has("oppownerid")) {
                setMainOppOwner(new String[]{id},jobj.getString("oppownerid"));
            }
            if(jobj.has("oppname")) {
                crmOpportunity.setOppname(jobj.getString("oppname"));
            }
            if(jobj.has("closingdate")) {
                crmOpportunity.setClosingdate(jobj.getLong("closingdate"));
            }
            if(jobj.has("validflag")) {
                crmOpportunity.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("oppstageid")) {
                crmOpportunity.setCrmCombodataByOppstageid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("oppstageid")));
            }
            if(jobj.has("opptypeid")) {
                crmOpportunity.setCrmCombodataByOpptypeid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("opptypeid")));
            }
            if(jobj.has("oppregionid")) {
                crmOpportunity.setCrmCombodataByRegionid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("oppregionid")));
            }
            if(jobj.has("leadsourceid")) {
                crmOpportunity.setCrmCombodataByLeadsourceid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("leadsourceid")));
            }
            if(jobj.has("probability")) {
                crmOpportunity.setProbability(jobj.getString("probability"));
            }
            if(jobj.has("currencyid")) {
                crmOpportunity.setCurrencyid(jobj.getString("currencyid"));
            }
            if(jobj.has("salesamount")) {
                crmOpportunity.setSalesamount(jobj.getString("salesamount"));
            }
            if(jobj.has("productserviceid")) {
                saveOpportunityProducts(new String[]{id},jobj.getString("productserviceid").split(","));
            }
            if(jobj.has("accountnameid")) {
                crmOpportunity.setCrmAccount((CrmAccount) get(CrmAccount.class, jobj.getString("accountnameid")));
            }
            if(jobj.has("price")) {
                crmOpportunity.setPrice(jobj.getString("price"));
            }
            if(jobj.has("updatedon")) {
                crmOpportunity.setUpdatedOn(new Date().getTime());
            }
            if(jobj.has("createdon")) {
                    crmOpportunity.setCreatedOn(jobj.getLong("createdon"));
            }
            else{
            	crmOpportunity.setCreatedOn(new Date().getTime());
            }
            if(jobj.has("deleteflag")) {
                crmOpportunity.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if(jobj.has("isarchive")){
            	crmOpportunity.setIsarchive(jobj.getBoolean("isarchive"));
            }
            if(jobj.has("leadid")) {
                crmOpportunity.setCrmLead((CrmLead) get(CrmLead.class, jobj.getString("leadid")));
            }
            if(jobj.has("CrmOpportunityCustomDataobj")){
                crmOpportunity.setCrmOpportunityCustomDataobj((CrmOpportunityCustomData) get(CrmOpportunityCustomData.class, jobj.getString("CrmOpportunityCustomDataobj")));
            }
            saveOrUpdate(crmOpportunity);
            ll.add(crmOpportunity);

        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.editOpportunity : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.editOpportunity : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.editOpportunity : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getOpportunities(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        Long dl = 0l;
        try {
            requestParams.put(Constants.moduleid,5);
            String appendCase = "and";
            String selCountQuary = " select count(distinct c.oppid) ";
            String selQuery = " select distinct c ";
            String Hql = " from opportunityOwners oo inner join oo.opportunity c "+crmManagerCommon.getJoinQuery(requestParams);

            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }

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
                    String[] searchcol = new String[]{"c.oppname"};
                    StringUtil.insertParamSearchString(filter_params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    filterQuery +=searchQuery;
                }
            }

            int start = 0;
            int limit = 25;
            boolean ispaging = requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()) && !StringUtil.isNullOrEmpty(requestParams.get("start").toString());
            if (ispaging) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }

            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm && usersList!=null){
                filterQuery += " and oo.usersByUserid.userID in (" + usersList + ") ";
            }

            String orderQuery = " order by c.createdOn desc ";
            if(requestParams.containsKey("field") && requestParams.get("xfield") != null) {
                String dbname = crmManagerCommon.getFieldDbName(requestParams);
                if(dbname!=null){
                    String dir = requestParams.get("direction").toString();
                    orderQuery = " order by "+dbname+" "+dir+" ";
                }
            }

            String countQuery = selCountQuary + Hql + filterQuery;
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

            if(StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(query, filter_params.toArray(), new Integer[]{start, limit});
            }
            List temp = new ArrayList();
            for(Object a:ll){
            	temp.add((CrmOpportunity)a);
            }
            ll=temp;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.getOpportunities : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl.intValue());
    }
    
    public double getOpportunityRevenue( ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        double revenue =0.0;
        String hql = "select distinct c.oppid from opportunityOwners oo inner join oo.opportunity c ";
        hql += StringUtil.filterQuery(filter_names, "where");
        hql = " select sum(oa.salesamount*1) from CrmOpportunity oa where oa.oppid in("+hql+")";
        ll = executeQuery(hql, filter_params.toArray());
        if(ll!= null && ll.size() >0){
            revenue = ll.get(0)==null?0:((Number)ll.get(0)).doubleValue();
        }
        return revenue;
    }
    public KwlReturnObject getAllOpportunities(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            boolean pagingFlag = false;
            if(requestParams.containsKey("pagingFlag") && requestParams.get("pagingFlag") != null) {
                pagingFlag = Boolean.parseBoolean(requestParams.get("pagingFlag").toString());
            }
            boolean countFlag = true;
            if(requestParams.containsKey("countFlag") && requestParams.get("countFlag") != null) {
                countFlag = Boolean.parseBoolean(requestParams.get("countFlag").toString());
            }
            ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
            ArrayList filter_params = (ArrayList) requestParams.get("filter_values");
            String Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            Hql += filterQuery;

            String selectInQuery = Hql ;
            String orderQuery = "";
            ArrayList order_by = null;
            ArrayList order_type = null;
            if(requestParams.containsKey("order_by"))
                order_by =(ArrayList) requestParams.get("order_by");
            if(requestParams.containsKey("order_type"))
                order_type = (ArrayList) requestParams.get("order_type");

            orderQuery = StringUtil.orderQuery(order_by,order_type);
            if(StringUtil.isNullOrEmpty(orderQuery)) {
                orderQuery = " order by c.oppname ";
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
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.getAllOpportunities", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

 
    public void setMainOppOwner(String[] oppids,String ownerid) throws ServiceException{
        String hql="delete from opportunityOwners c where c.opportunity.oppid in (:oppids) and mainOwner ='T'";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("oppids", oppids);
        executeUpdate(hql, null, map);
        User user = (User)get(User.class, ownerid);
        for(String oppid:oppids){
	        opportunityOwners oppOwnersObj = new opportunityOwners();
	        oppOwnersObj.setOpportunity((CrmOpportunity)get(CrmOpportunity.class,oppid));
	        oppOwnersObj.setUsersByUserid(user);
	        oppOwnersObj.setMainOwner(true);
	        save(oppOwnersObj);
        }
    }
    
    @Override
    public List<CrmOpportunity> getOwnerChangedOpportunities(String newOwnerId,String[] oppIds)throws ServiceException{

       	String hql="select o.opportunity from opportunityOwners o where o.mainOwner = ? and o.opportunity.oppid in (:oppIds) "+
       				"and o.usersByUserid.userID not in (:newOwnerId) ";
       	Map<String, Object> namedParams=new HashMap<String, Object>();
       	namedParams.put("oppIds", oppIds);
       	namedParams.put("newOwnerId", new String[]{newOwnerId});
       	return executeQuery(hql, new Object[]{true}, namedParams);
 	}
    
    
    
    @Override
    public KwlReturnObject saveOppOwners(HashMap<String, Object> requestParams) throws Exception {
        List ll = new ArrayList();
        try {
            String oppid = "";
            if(requestParams.containsKey("oppid") && requestParams.get("oppid") != null) {
                oppid =  requestParams.get("oppid").toString();
            }
            String owners = "";
            if(requestParams.containsKey("owners") && requestParams.get("owners") != null) {
                owners =  requestParams.get("owners").toString();
            }
            String mainowner = "";
            if(requestParams.containsKey("mainOwner") && requestParams.get("mainOwner") != null) {
                mainowner =  requestParams.get("mainOwner").toString();
            }
//            saveOppOwners(oppid,mainowner,owners);
//            myjobj.put("success", true);
            String hql="delete from opportunityOwners c where c.opportunity.oppid = ? ";
            executeUpdate(hql, oppid);

            opportunityOwners oppOwnersObj = new opportunityOwners();
            oppOwnersObj.setOpportunity((CrmOpportunity)get(CrmOpportunity.class, oppid));
            oppOwnersObj.setUsersByUserid((User)get(User.class, mainowner));
            oppOwnersObj.setMainOwner(true);
            save(oppOwnersObj);

            if(!StringUtil.isNullOrEmpty(owners) && !owners.equalsIgnoreCase("undefined") ){
                String[] ownerIds = owners.split(",");
                for (int i = 0;i < ownerIds.length;i++){
                    oppOwnersObj = new opportunityOwners();
                    oppOwnersObj.setOpportunity((CrmOpportunity)get(CrmOpportunity.class, oppid));
                    oppOwnersObj.setUsersByUserid((User)get(User.class, ownerIds[i]));
                    oppOwnersObj.setMainOwner(false);
                    save(oppOwnersObj);
                }
            }
            ll.add(oppOwnersObj);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.saveOppOwners", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, 1);
    }
    @Override
    public KwlReturnObject getOpportunityOwners(ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        String Hql = "from opportunityOwners c ";
        String filterQuery = StringUtil.filterQuery(filter_names, "where");
        Hql += filterQuery;
        String selectInQuery = Hql ;

        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl= ll.size();
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public void saveOpportunityProducts(String[] oppids,String[] products) throws ServiceException{
        String query="delete from OppurtunityProducts where oppid.oppid in (:oppids)";
        Map map = new HashMap();
        map.put("oppids", oppids);
        executeUpdate(query,null,map);
        if(products!=null ){
			for (String oppid : oppids) {
				for (int i = 0; i < products.length; i++) {
					if (!StringUtil.isNullOrEmpty(products[i])) {
						OppurtunityProducts oppProductObj = new OppurtunityProducts();
						oppProductObj.setOppid((CrmOpportunity) get(CrmOpportunity.class, oppid));
						oppProductObj.setProductId((CrmProduct) get(CrmProduct.class, products[i]));

						save(oppProductObj);
					}
				}
			}
        }
    }
    @Override
    public KwlReturnObject getOpportunityProducts(ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        String Hql = "from OppurtunityProducts op ";
        String filterQuery = StringUtil.filterQuery(filter_names, "where");
        Hql += filterQuery;
        String selectInQuery = Hql ;

        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl= ll.size();
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getCrmOpportunityCustomData(HashMap<String, Object> requestParams) throws ServiceException {
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

            String Hql = "from CrmOpportunityCustomData ";

            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            Hql += filterQuery;

            ll = executeQueryPaging( Hql,filter_params.toArray(),new Integer[]{0, 1});
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.getCrmOpportunityCustomData : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll,dl);
    }

    @Override
    public List<CrmOpportunity> getOpportunities(List<String> recordIds)
    {
        if (recordIds == null || recordIds.isEmpty())
        {
            return null;
        }
        StringBuilder hql = new StringBuilder("from CrmOpportunity where oppid in (");
        
        for (String record: recordIds)
        {
            hql.append("'" + record + "',");
        }
        
        hql.deleteCharAt(hql.length() - 1);
        hql.append(")");

        return executeQuery(hql.toString());
    }

    @Override
    public HashMap<String, CrmOpportunityCustomData> getOpportunityCustomDataMap(List<String> list, String companyid) throws ServiceException {
        List<CrmOpportunityCustomData> ll = null;
        HashMap<String, CrmOpportunityCustomData> oppCustomDataMap = new HashMap<String, CrmOpportunityCustomData>();
        try {
            String filterQuery = "";
            List<List> paramll = new ArrayList();
            List<String> paramnames = new ArrayList();
            if(!list.isEmpty()){
                filterQuery = " and c.oppid in (:recordlist) ";
                paramll.add(list);
                paramnames.add("recordlist");
            }
            String Hql = "select c from CrmOpportunityCustomData c where c.company.companyID = '"+companyid+"' "+filterQuery;
            ll = executeCollectionQuery(Hql,paramnames,paramll);
            for(CrmOpportunityCustomData oppCustomObj : ll){
                oppCustomDataMap.put(oppCustomObj.getOppid(), oppCustomObj);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.getOpportunityCustomDataMap", ex);
        }
        return oppCustomDataMap;
    }

    @Override
    public void setCustomData(CrmOpportunity crmOpp, CrmOpportunityCustomData crmOppCustomData) {
        crmOpp.setCrmOpportunityCustomDataobj(crmOppCustomData);
        save(crmOpp);
    }

    public void setCustomData(CrmOpportunity crmOpp, JSONArray cstmData) {
    	StringBuffer fields=new StringBuffer("oppid,company");
    	StringBuffer qmarks=new StringBuffer("?,?");
    	ArrayList params = new ArrayList();
    	params.add(crmOpp.getOppid());
    	params.add(crmOpp.getCompany().getCompanyID());
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
    		StringBuffer sql= new StringBuffer("insert into crmopportunitycustomdata (").append(fields).append(")VALUES(").append(qmarks).append(')');
    		updateJDBC(sql.toString(), params.toArray());
    	}
    }

    @Override
    public Map<String, List<OpportunityOwnerInfo>> getOpportunityOwners(List<String> oppIds)
    {
        Map<String, List<OpportunityOwnerInfo>> ownerMap = new HashMap<String, List<OpportunityOwnerInfo>>();
        if (oppIds != null && !oppIds.isEmpty())
        {
            StringBuilder query = new StringBuilder("select oo.opportunityId, oo.usersByUserid, oo from opportunityOwners oo where oo.opportunityId in (");

            for (String oppId: oppIds)
            {
                query.append('\'');
                query.append(oppId);
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
                    String oppId = (String) result[0];
                    OpportunityOwnerInfo info = new OpportunityOwnerInfo();
                    info.setUser((User) result[1]);
                    info.setOwner((opportunityOwners) result[2]);

                    if (ownerMap.containsKey(oppId))
                    {
                        List<OpportunityOwnerInfo> ownerList = ownerMap.get(oppId);
                        ownerList.add(info);
                    }
                    else
                    {
                        List<OpportunityOwnerInfo> ownerList = new ArrayList<OpportunityOwnerInfo>();
                        ownerList.add(info);
                        ownerMap.put(oppId, ownerList);
                    }
                }
            }
        }
        return ownerMap;
    }

    public Map<String, List<CrmProduct>> getOpportunityProducts(List<String> oppIds)
    {
        Map<String, List<CrmProduct>> productMap = new HashMap<String, List<CrmProduct>>();
        if (oppIds != null && !oppIds.isEmpty())
        {
            StringBuilder query = new StringBuilder("select ap.opportunityId, ap.productId from OppurtunityProducts ap where ap.opportunityId in (");

            for (String oppId: oppIds)
            {
                query.append('\'');
                query.append(oppId);
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
                    String oppId = (String) result[0];
                    CrmProduct product = (CrmProduct) result[1];

                    if (productMap.containsKey(oppId))
                    {
                        List<CrmProduct> productList = productMap.get(oppId);
                        productList.add(product);
                    }
                    else
                    {
                        List<CrmProduct> productList = new ArrayList<CrmProduct>();
                        productList.add(product);
                        productMap.put(oppId, productList);
                    }
                }
            }
        }
        return productMap;
    }
//TODO (Kuldeep Singh) : Please test it Porperly for all fields while using this function for Mass update
// Now it is working fine for delete.
    
    public KwlReturnObject updateMassOpportunities(JSONObject jobj) throws ServiceException {
        String hqlVarPart = "";
        List<Object> params = new ArrayList<Object>();
        List ll = new ArrayList();
        int dl = 0;
        try {
            String userid = null;
            String[] oppids = (String[])jobj.get("oppid");
            
            if(jobj.has("companyid")) {
                hqlVarPart += " company = ?,";
            	params.add(get(Company.class, jobj.getString("companyid")));
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                hqlVarPart += " usersByUpdatedbyid = ?,";
            	params.add(get(User.class, userid));
            }
            if(jobj.has("oppownerid")) {
                setMainOppOwner(oppids,jobj.getString("oppownerid"));
            }
            if(jobj.has("oppname")) {
                hqlVarPart += " oppname = ?,";
            	params.add(jobj.getString("oppname"));
            }
            if(jobj.has("closingdate")) {
                hqlVarPart += " closingdate = ?,";
            	params.add(jobj.getLong("closingdate"));
            }
            if(jobj.has("validflag")) {
                hqlVarPart += " validflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("oppstageid")) {
                hqlVarPart += " crmCombodataByOppstageid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("oppstageid")));
            }
            if(jobj.has("opptypeid")) {
                hqlVarPart += " crmCombodataByOpptypeid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("opptypeid")));
            }
            if(jobj.has("oppregionid")) {
                hqlVarPart += " crmCombodataByRegionid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("oppregionid")));
            }
            if(jobj.has("leadsourceid")) {
                hqlVarPart += " crmCombodataByLeadsourceid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("leadsourceid")));
            }
            if(jobj.has("probability")) {
                hqlVarPart += " probability = ?,";
            	params.add(jobj.getString("probability"));
            }
            if(jobj.has("currencyid")) {
                hqlVarPart += " currencyid = ?,";
            	params.add(jobj.getString("currencyid"));
            }
            if(jobj.has("salesamount")) {
                hqlVarPart += " salesamount = ?,";
            	params.add(jobj.getString("salesamount"));
            }
            if(jobj.has("productserviceid")) {
               
                saveOpportunityProducts(oppids,jobj.getString("productserviceid").split(","));
            }
            if(jobj.has("accountnameid")) {
                hqlVarPart += " crmAccount = ?,";
            	params.add(get(CrmAccount.class, jobj.getString("accountnameid")));
            }
            if(jobj.has("price")) {
                hqlVarPart += " price = ?,";
            	params.add(jobj.getString("price"));
            }
            if(jobj.has("updatedon")) {
                hqlVarPart += " updatedOn = ?,";
            	params.add(jobj.getLong("updatedon"));
            }
            if(jobj.has("createdon")) {
                hqlVarPart += " createdOn = ?,";
                params.add(jobj.getLong("createdon"));
            }
            if(jobj.has("deleteflag")) {
                hqlVarPart += " deleteflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if(jobj.has("leadid")) {
                hqlVarPart += " crmLead = ?,";
            	params.add(get(CrmLead.class, jobj.getString("leadid")));
            }
            if(jobj.has("CrmOpportunityCustomDataobj") && jobj.getBoolean("CrmOpportunityCustomDataobj")){
                linkCustomData();
            }
            hqlVarPart = hqlVarPart.substring(0, Math.max(0,hqlVarPart.lastIndexOf(',')));
            String hql = "update CrmOpportunity set "+hqlVarPart+" where oppid in (:oppids)";
            Map map = new HashMap();
            map.put("oppids", oppids);
            executeUpdate(hql, params.toArray(), map);

        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.updateMassOpportunities : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.updateMassOpportunities : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.updateMassOpportunities : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public void linkCustomData() {
		String query="update crm_opportunity o inner join crmopportunitycustomdata co on co.oppid=o.oppid and o.crmopportunitycustomdataref is null set o.crmopportunitycustomdataref=o.oppid";
        updateJDBC(query,new Object[]{});
	}

}
