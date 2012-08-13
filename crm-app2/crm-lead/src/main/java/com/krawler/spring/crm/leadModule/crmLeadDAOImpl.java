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
package com.krawler.spring.crm.leadModule; 

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.BuildCriteria;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmLeadCustomData;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.LeadOwners;
import com.krawler.crm.database.tables.LeadProducts;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.lead.dm.LeadOwnerInfo;
import com.krawler.crm.utils.Constants;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class crmLeadDAOImpl extends BaseDAO implements crmLeadDAO {
        
    private sessionHandlerImpl sessionHandlerImplObj;

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    public CrmLead getLead(String leadid) throws ServiceException {
        return (CrmLead) get(CrmLead.class, leadid);
    }
    public KwlReturnObject getLeads(HashMap<String, Object> requestParams) throws ServiceException {
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

            String Hql = "select distinct c from CrmLead c ";
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
            throw ServiceException.FAILURE("crmLeadDAOImpl.getActiveLead : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, "002", "", ll, dl);
    }

    public KwlReturnObject getLeads(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        List ll = null;
        Long dl = 0l;
        try {
            boolean archive = false;
             requestParams.put(Constants.moduleid,2);
            if(requestParams.containsKey("isarchive") && requestParams.get("isarchive") != null) {
                archive = Boolean.parseBoolean(requestParams.get("isarchive").toString());
            }
            String transfer = "0";
            if(requestParams.containsKey("transfered") && requestParams.get("transfered") != null) {
                if(requestParams.containsKey("iPhoneCRM") && requestParams.get("iPhoneCRM") != null) {
                    transfer = "0";
                } else {
                    transfer =  requestParams.get("transfered").toString();
                }
            }
            String companyid = requestParams.get("companyid").toString();
            String appendCase = "and";
            ArrayList filter_params = new ArrayList();
            String selQuery = "select distinct c ";
            String selExportQuery = "select distinct c ";
            String selCountQuery = "select count(distinct c.leadid) ";
            String joinQuery = crmManagerCommon.getJoinQuery(requestParams);
            String Hql = " from LeadOwners lo inner join lo.leadid c left join c.crmCombodataByLeadstatusid dm " +joinQuery;
            String filterQuery = "where c.deleteflag=0 and c.istransfered= ? and c.isarchive= ? and c.company.companyID= ? ";
            String convert = "";
            if(requestParams.containsKey("email") && requestParams.get("email") != null) {
                filterQuery += " and c.email != '' ";
            }
            if(requestParams.containsKey("status") && requestParams.get("status") != null) {
                convert = requestParams.get("status").toString();
                if (!StringUtil.isNullOrEmpty(convert)) {
                    filterQuery += " and c.crmCombodataByLeadstatusid.ID='" + convert + "'";
                }
            }
            filter_params.add(transfer);
            filter_params.add(archive);
            filter_params.add(companyid);

            filterQuery += " and (dm.mainID != ? or dm is null)";
            filter_params.add(Constants.LEADSTATUSID_QUALIFIED);

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
                    String[] searchcol = new String[]{"c.lastname","c.title"};
                    if(requestParams.containsKey("searchcolarray") && requestParams.get("searchcolarray") != null) {
                        searchcol = (String[]) requestParams.get("searchcolarray");
                    }
                    
                    if(requestParams.containsKey("email") && requestParams.get("email") != null) {
                        searchcol = new String[]{"c.lastname","c.firstname"};
                    }

                    StringUtil.insertParamSearchString(filter_params, ss, searchcol.length);
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
                filterQuery += " and lo.usersByUserid.userID in (" + usersList + ") ";
            }

            if(requestParams.containsKey("type")) {
                filterQuery += " and lo.leadid.type=? ";
                filter_params.add(requestParams.get("type"));
            }

            String orderQuery = " order by c.createdOn desc";
            String dir = null;
            String dbname = null;
            if(requestParams.containsKey("field") && requestParams.get("xfield") != null) {
                dbname = crmManagerCommon.getFieldDbName(requestParams);
                if(dbname!=null){
                    dir = requestParams.get("direction").toString();
                    if(dbname.equals("c.type")){
                          dir = dir.equals(BuildCriteria.OPERATORORDERASC)?BuildCriteria.OPERATORORDERDESC:BuildCriteria.OPERATORORDERASC;
                    }
                    orderQuery = " order by "+dbname+" "+dir+" ";
                }
            }
            
            String countQuery = selCountQuery + Hql + filterQuery;
            String query = selQuery + Hql + filterQuery + orderQuery;
            String export = "";
            if(requestParams.containsKey("export") && requestParams.get("export") != null) {
                countQuery = selExportQuery + Hql + filterQuery + orderQuery ;
                export = requestParams.get("export").toString();
            }
            ll = executeQuery(countQuery, filter_params.toArray());
            
            if (ll != null && !ll.isEmpty() && StringUtil.isNullOrEmpty(export))
            {
                dl = (Long) ll.get(0);
            }
            if(StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(query, filter_params.toArray(), new Integer[]{start, limit});
//                if(ll!=null && !ll.isEmpty()){
//                            Criteria crit = getSession().createCriteria(CrmLead.class, "c");
//                            crit = crit.add(Restrictions.in("leadid", ll));
//                            if(dbname!=null){
//                                BuildCriteria.buildCriteria(dir, BuildCriteria.ORDER, crit, dbname.replaceFirst("c.", ""));
//                            }
//                            ll = crit.list();
//                      }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.getLeads : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl.intValue());
    }

    public KwlReturnObject getAllLeads(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select distinct c from LeadOwners lo inner join lo.leadid c left join c.crmCombodataByLeadstatusid dm ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            Hql += filterQuery;
            int ind = Hql.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(Hql.substring(ind+1,ind+2));
                Hql = Hql.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            String selectInQuery = Hql;
            boolean pagingFlag = false;
            if(requestParams.containsKey("pagingFlag") && requestParams.get("pagingFlag") != null) {
                pagingFlag = Boolean.parseBoolean(requestParams.get("pagingFlag").toString());
            }

            if(requestParams.containsKey("filterQaulified") && requestParams.get("filterQaulified") != null) {
                if(Boolean.parseBoolean(requestParams.get("filterQaulified").toString())) {
                    selectInQuery += " and (dm.mainID != ? or dm is null)";
                    filter_params.add(Constants.LEADSTATUSID_QUALIFIED);
                }
            }

            ArrayList order_by = null;
            ArrayList order_type = null;
            if(requestParams.containsKey("order_by"))
                order_by =(ArrayList) requestParams.get("order_by");
            if(requestParams.containsKey("order_type"))
                order_type = (ArrayList) requestParams.get("order_type");
            String orderQuery = StringUtil.orderQuery(order_by,order_type);
            if(StringUtil.isNullOrEmpty(orderQuery)) {
                selectInQuery += " order by c.lastname ";
            } else {
                selectInQuery += orderQuery;
            }

            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl= ll.size();
            if(pagingFlag) {
                int start = 0;
                int limit = 25;
                if (requestParams.containsKey("start") && requestParams.containsKey("limit")) {
                    start = Integer.parseInt(requestParams.get("start").toString());
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                }
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmLeadDAOImpl.getAllLeads : "+ ex.getMessage(), ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject addLeads(JSONObject jobj) throws ServiceException {
        JSONObject myjobj = new JSONObject();
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            CrmLead crmLead = new CrmLead();
            if(jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmLead.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmLead.setUsersByUpdatedbyid((User) get(User.class, userid));
                crmLead.setUsersByCreatedbyid((User) get(User.class, userid));
            }
//            if(jobj.has("firstname")) {
//                crmLead.setFirstname(jobj.getString("firstname"));
//            }
            if(jobj.has("lastname")) {
                crmLead.setLastname(jobj.getString("lastname"));
            }
            if(jobj.has("firstname")) {
                crmLead.setFirstname(jobj.getString("firstname"));
            }
//            if(jobj.has("company")) {
//                crmLead.setCompanyname(jobj.getString("company"));
//            }
            if(jobj.has("type")) {
                crmLead.setType(jobj.getString("type"));
            }
            if(jobj.has("phone")) {
                crmLead.setPhone(jobj.getString("phone"));
            }
            if(jobj.has("email")) {
                crmLead.setEmail(jobj.getString("email"));
            }
            if(jobj.has("street")) {
                crmLead.setAddstreet(jobj.getString("street"));
            }
            if(jobj.has("ratingid")) {
                crmLead.setCrmCombodataByRatingid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("ratingid")));
            }
            if(jobj.has("title")) {
                crmLead.setTitle(jobj.getString("title"));
            }
            if(jobj.has("leadstatusid")) {
                crmLead.setCrmCombodataByLeadstatusid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("leadstatusid")));
            }
            if(jobj.has("industryid")) {
                crmLead.setCrmCombodataByIndustryid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("industryid")));
            }
            if(jobj.has("leadsourceid")) {
                crmLead.setCrmCombodataByLeadsourceid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("leadsourceid")));
            }
            if(jobj.has("updatedon")) {
                crmLead.setUpdatedOn(new Date().getTime());
            }
            if(jobj.has("createdon")) {
            	Long createdOn = jobj.getLong("createdon");
	            crmLead.setCreatedOn(createdOn);
	            	
            } else {
            	// set server date
                crmLead.setCreatedOn(new Date().getTime());
            }
            if(jobj.has("isconverted")) {
                crmLead.setIsconverted(jobj.getString("isconverted"));
            }
            if(jobj.has("istransfered")) {
                crmLead.setIstransfered(jobj.getString("istransfered"));
            }
            if(jobj.has("validflag")) {
                crmLead.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("leadid")) {
                id = jobj.getString("leadid");
                crmLead.setLeadid(id);
            }
            if(jobj.has("deleteflag")) {
                crmLead.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if(jobj.has("conversiontype")) {
                crmLead.setConversiontype(jobj.getString("conversiontype"));
            }
            if(jobj.has("price")) {
                crmLead.setPrice(jobj.getString("price"));
            }
            if(jobj.has("revenue")) {
                crmLead.setRevenue(jobj.getString("revenue"));
            }
            if(jobj.has("ownerconfirm")) {
                crmLead.setOwnerconfirm(Boolean.TRUE.equals(Boolean.parseBoolean(jobj.getString("ownerconfirm"))));
            }
            if(jobj.has("CrmLeadCustomDataobj")){
                crmLead.setCrmLeadCustomDataobj((CrmLeadCustomData) get(CrmLeadCustomData.class, jobj.getString("CrmLeadCustomDataobj")));
            }

            save(crmLead);
            if(jobj.has("leadownerid")) {
                setMainLeadOwner(new String[]{id},jobj.getString("leadownerid"));
//                crmLead.setUsersByUserid((User) hibernateTemplate.get(User.class, jobj.getString("leadownerid")));
            }
            if(jobj.has("productid")) {
                saveLeadProducts(new String[]{id},(String[]) jobj.get("productsId"));
            }
            //for zoho import
            if(jobj.has("isarchive")){     
            	crmLead.setIsarchive(jobj.getBoolean("isarchive"));
            }
            ll.add(crmLead);

        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.addLeads : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.addLeads : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.addLeads : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject editLeads(JSONObject jobj) throws ServiceException {
        JSONObject myjobj = new JSONObject();
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            if(jobj.has("leadid")) {
                id = jobj.getString("leadid");
            }
            CrmLead crmLead = (CrmLead) get(CrmLead.class, id);
            if(jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmLead.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmLead.setUsersByUpdatedbyid((User) get(User.class, userid));
//                crmLead.setUsersByCreatedbyid((User) hibernateTemplate.get(User.class, userid));
            }
//            if(jobj.has("firstname")) {
//                crmLead.setFirstname(jobj.getString("firstname"));
//            }
            if(jobj.has("lastname")) {
                crmLead.setLastname(jobj.getString("lastname"));
            }
            if(jobj.has("firstname")) {
                crmLead.setFirstname(jobj.getString("firstname"));
            }
//            if(jobj.has("company")) {
//                crmLead.setCompanyname(jobj.getString("company"));
//            }
            if(jobj.has("type")) {
                crmLead.setType(jobj.getString("type"));
            }
            if(jobj.has("phone")) {
                crmLead.setPhone(jobj.getString("phone"));
            }
            if(jobj.has("email")) {
                crmLead.setEmail(jobj.getString("email"));
            }
            if(jobj.has("street")) {
                crmLead.setAddstreet(jobj.getString("street"));
            }
            if(jobj.has("ratingid")) {
                crmLead.setCrmCombodataByRatingid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("ratingid")));
            }
            if(jobj.has("title")) {
                crmLead.setTitle(jobj.getString("title"));
            }
            if(jobj.has("leadstatusid")) {
                crmLead.setCrmCombodataByLeadstatusid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("leadstatusid")));
            }
            if(jobj.has("industryid")) {
                crmLead.setCrmCombodataByIndustryid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("industryid")));
            }
            if(jobj.has("leadsourceid")) {
                crmLead.setCrmCombodataByLeadsourceid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("leadsourceid")));
            }
            if(jobj.has("updatedon")) {
                crmLead.setUpdatedon(new Date());
            }
            if(jobj.has("leadconversiondate")) {
                crmLead.setConvertedOn(new Date().getTime());
            }
            if(jobj.has("createdon")) 
            {
            	Long createdOn = jobj.getLong("createdon");
            	crmLead.setCreatedOn(createdOn);
            }
            if(jobj.has("isconverted")) {
                crmLead.setIsconverted(jobj.getString("isconverted"));
            }
            if(jobj.has("istransfered")) {
                crmLead.setIstransfered(jobj.getString("istransfered"));
            }
            if(jobj.has("validflag")) {
                crmLead.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("deleteflag")) {
                crmLead.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
                try{
                	if(Integer.parseInt(jobj.getString("deleteflag"))==1){
                		String query = "delete from LeadProducts where leadid.leadid=?";
                		executeUpdate(query, id);
                	}
                }catch(Exception ae){
                	logger.warn("Products related to the lead could not be deleted :", ae);
                }
            }
            if(jobj.has("conversiontype")) {
                crmLead.setConversiontype(jobj.getString("conversiontype"));
            }
            if(jobj.has("price")) {
                crmLead.setPrice(jobj.getString("price"));
            }
            if(jobj.has("revenue")) {
                crmLead.setRevenue(jobj.getString("revenue"));
            }
            if(jobj.has("ownerconfirm")) {
                crmLead.setOwnerconfirm(Boolean.TRUE.equals(Boolean.parseBoolean(jobj.getString("ownerconfirm"))));
            }
            if(jobj.has("productid")) {
                saveLeadProducts(new String[]{id},(String[]) jobj.get("productsId"));
//                crmLead.setCrmProduct((CrmProduct) hibernateTemplate.get(CrmProduct.class, jobj.getString("productid")));
            }
            if(jobj.has("leadownerid")) {
                setMainLeadOwner(new String[]{id},jobj.getString("leadownerid"));
//                crmLead.setUsersByUserid((User) hibernateTemplate.get(User.class, jobj.getString("leadownerid")));
            }
            if(jobj.has("CrmLeadCustomDataobj")){
                crmLead.setCrmLeadCustomDataobj((CrmLeadCustomData) get(CrmLeadCustomData.class, jobj.getString("CrmLeadCustomDataobj")));
            }
            if(jobj.has("isarchive")){
            	crmLead.setIsarchive(jobj.getBoolean("isarchive"));
            }
            saveOrUpdate(crmLead);

            ll.add(crmLead);

        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.editLeads : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.editLeads : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.editLeads : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public void saveLeadProducts(String[] leadids,String[] productIds) throws ServiceException{
        String query="delete from LeadProducts where leadid.leadid in (:leadids)";
        Map map = new HashMap();
        map.put("leadids", leadids);
        executeUpdate(query,null, map);
        if(productIds!=null ){
			for (String leadid : leadids) {
				for (int i = 0; i < productIds.length; i++) {
					if (!StringUtil.isNullOrEmpty(productIds[i])) {
						LeadProducts leadProductsObj = new LeadProducts();
						leadProductsObj.setLeadid((CrmLead) get(CrmLead.class, leadid));
						leadProductsObj.setProductId((CrmProduct) get(CrmProduct.class, productIds[i]));
						leadProductsObj.setCreatedon(new Date());
						leadProductsObj.setNumbering(i);
						save(leadProductsObj);
					}
				}
			}
        }
    }

    public KwlReturnObject getLeadOwners(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        String Hql = "";
        if (requestParams.containsKey("usersList") && requestParams.get("usersList") != null) {
            Hql = " select distinct c from LeadOwners lo inner join lo.leadid c ";
            Hql += StringUtil.filterQuery(filter_names, "where");
            StringBuffer usersList = (StringBuffer) requestParams.get("usersList");
            if(filter_names.size() > 0) {
                Hql += "  and ";
            } else {
                Hql += "  where ";
            }
            Hql += " lo.usersByUserid.userID in (" + usersList + ") ";
        } else {
            boolean distinctFlag = false;
            if(requestParams.containsKey("distinctFlag") && requestParams.get("distinctFlag") != null){
                distinctFlag = (Boolean) requestParams.get("distinctFlag");
            }

            if(distinctFlag) {
                Hql = "select distinct c from LeadOwners lo inner join lo.leadid c ";
            } else {
                Hql = "select lo, c from LeadOwners lo inner join lo.leadid c ";
            }
            Hql += StringUtil.filterQuery(filter_names, "where");
        }

        ll = executeQuery(Hql, filter_params.toArray());
        dl= ll.size();
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public void setMainLeadOwner(String[] leadids,String ownerid) throws ServiceException{
        String hql="delete from LeadOwners where leadid.leadid in (:leadids) and mainOwner = true";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("leadids", leadids);
        executeUpdate(hql ,null, map);
        User user = (User)get(User.class, ownerid);
        for(String leadid:leadids){
	        LeadOwners leadOwnersObj = new LeadOwners();
	        leadOwnersObj.setLeadid((CrmLead)get(CrmLead.class, leadid));
	        leadOwnersObj.setUsersByUserid(user);
	        leadOwnersObj.setMainOwner(true);
	        save(leadOwnersObj);
        }
    }
    public KwlReturnObject saveLeadOwners(HashMap<String, Object> requestParams) throws Exception {
//        JSONObject myjobj = new JSONObject();
        boolean b = false;
        try {
            String leadid = "";
            if(requestParams.containsKey("leadid") && requestParams.get("leadid") != null) {
                leadid =  requestParams.get("leadid").toString();
            }
            String owners = "";
            if(requestParams.containsKey("owners") && requestParams.get("owners") != null) {
                owners =  requestParams.get("owners").toString();
            }
            String mainowner = "";
            if(requestParams.containsKey("mainOwner") && requestParams.get("mainOwner") != null) {
                mainowner =  requestParams.get("mainOwner").toString();
            }

            String hql="delete from LeadOwners where leadid.leadid = ? ";
            executeUpdate( hql, leadid);

            LeadOwners leadOwnersObj = new LeadOwners();
            leadOwnersObj.setLeadid((CrmLead)get(CrmLead.class, leadid));
            leadOwnersObj.setUsersByUserid((User)get(User.class, mainowner));
            leadOwnersObj.setMainOwner(true);
            save(leadOwnersObj);

            if(!StringUtil.isNullOrEmpty(owners) && !owners.equalsIgnoreCase("undefined") ){
                String[] ownerIds = owners.split(",");
                for (int i = 0;i < ownerIds.length;i++){
                    leadOwnersObj = new LeadOwners();
                    leadOwnersObj.setLeadid((CrmLead)get(CrmLead.class, leadid));
                    leadOwnersObj.setUsersByUserid((User)get(User.class, ownerIds[i]));
                    leadOwnersObj.setMainOwner(false);
                    save(leadOwnersObj);
                }
            }
//            myjobj.put("success", true);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.saveLeadSubOwners", e);
        }
//        return myjobj;
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", null, 0);
    }
    public KwlReturnObject getLeadProducts(ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        String Hql = "from LeadProducts lp ";
        String filterQuery = StringUtil.filterQuery(filter_names, "where");
        Hql += filterQuery;
        String selectInQuery = Hql+" order by lp.numbering" ;
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl= ll.size();
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    public List<CrmLead> getLeads(List<String> recordIds) throws ServiceException {
        if (recordIds == null || recordIds.isEmpty())
        {
            return null;
        }
        StringBuilder hql = new StringBuilder("from CrmLead where leadid in (");
        
        for (String record: recordIds)
        {
            hql.append("'" + record + "',");
        }
        
        hql.deleteCharAt(hql.length() - 1);
        hql.append(")");

        return executeQuery(hql.toString());
    }

    public KwlReturnObject getCrmLeadCustomData(HashMap<String, Object> requestParams) throws ServiceException {
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

            String Hql = "from CrmLeadCustomData ";

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
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.getCrmLeadCustomData : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll,dl);
    }

    @Override
    public void setCustomData(CrmLead crmLead, CrmLeadCustomData crmLeadCustomData) {
        crmLead.setCrmLeadCustomDataobj(crmLeadCustomData);
        save(crmLead);
    }

    public void setCustomData(CrmLead crmLead, JSONArray cstmData) {
    	StringBuffer fields=new StringBuffer("leadid,company");
    	StringBuffer qmarks=new StringBuffer("?,?");
    	ArrayList params = new ArrayList(); 
    	params.add(crmLead.getLeadid());
    	params.add(crmLead.getCompany().getCompanyID());
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
    		StringBuffer sql= new StringBuffer("insert into crmleadcustomdata (").append(fields).append(")VALUES(").append(qmarks).append(')');
    		updateJDBC(sql.toString(), params.toArray());
    	}
    }

    @Override
    public HashMap<String, CrmLeadCustomData> getLeadCustomDataMap(List<String> list, String companyid) throws ServiceException {
        List<CrmLeadCustomData> ll = null;
        HashMap<String, CrmLeadCustomData> leadCustomDataMap = new HashMap<String, CrmLeadCustomData>();
        try {
            String filterQuery = "";
            List<List> paramll = new ArrayList();
            List<String> paramnames = new ArrayList();
            if(!list.isEmpty()){
                filterQuery = " and c.leadid in (:recordlist) ";
                paramll.add(list);
                paramnames.add("recordlist");
            }
            String Hql = "select c from CrmLeadCustomData c where c.company.companyID = '"+companyid+"' "+filterQuery;
            ll = executeCollectionQuery(Hql,paramnames,paramll);
            for(CrmLeadCustomData leadCustomObj : ll){
                leadCustomDataMap.put(leadCustomObj.getLeadid(), leadCustomObj);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmLeadDAOImpl.getLeadCustomDataMap", ex);
        }
        return leadCustomDataMap;
    }
    @Override
    public Map<String, List<LeadOwnerInfo>> getLeadOwners(List<String> leadList)
    {
        Map<String, List<LeadOwnerInfo>> ownerMap = new HashMap<String, List<LeadOwnerInfo>>();
        if (leadList != null && !leadList.isEmpty())
        {
            String query = "select lo.leadId, lo.usersByUserid, lo from LeadOwners lo where lo.leadId in (:leadList)";
            List<List> values = new ArrayList<List>();
            values.add(leadList);
            List<Object[]> results = executeCollectionQuery(query, Collections.singletonList("leadList"), values);
            
            if (results != null)
            {
                for (Object[] result: results)
                {
                    String leadId = (String) result[0];
                    LeadOwnerInfo info = new LeadOwnerInfo();
                    info.setUser((User) result[1]);
                    info.setOwner((LeadOwners) result[2]);
                    
                    if (ownerMap.containsKey(leadId))
                    {
                        List<LeadOwnerInfo> ownerList = ownerMap.get(leadId);
                        ownerList.add(info);
                    }
                    else
                    {
                        List<LeadOwnerInfo> ownerList = new ArrayList<LeadOwnerInfo>();
                        ownerList.add(info);
                        ownerMap.put(leadId, ownerList);
                    }
                }
            }
        }
        return ownerMap;
    }
    @Override
    public Map<String, List<CrmProduct>> getLeadProducts(List<String> leadList)
    {
        Map<String, List<CrmProduct>> productMap = new HashMap<String, List<CrmProduct>>();
        if (leadList != null && !leadList.isEmpty())
        {
            String query = "select lp.leadId, lp.productId from LeadProducts lp where lp.leadId in (:leadList)";
            List<List> values = new ArrayList<List>();
            values.add(leadList);
            List<Object[]> results = executeCollectionQuery(query, Collections.singletonList("leadList"), values);
            
            if (results != null)
            {
                for (Object[] result: results)
                {
                    String leadId = (String) result[0];
                    CrmProduct product = (CrmProduct) result[1];
                    
                    if (productMap.containsKey(leadId))
                    {
                        List<CrmProduct> productList = productMap.get(leadId);
                        productList.add(product);
                    }
                    else
                    {
                        List<CrmProduct> productList = new ArrayList<CrmProduct>();
                        productList.add(product);
                        productMap.put(leadId, productList);
                    }
                }
            }
        }
        return productMap;
    }
	@Override
	public KwlReturnObject updateMassLeads(JSONObject jobj) throws ServiceException {
        String hqlVarPart = "";
        List<Object> params = new ArrayList<Object>();
        List ll = new ArrayList();
        int dl = 0;
        try {
            String[] leadids = (String[])jobj.get("leadid");
            if(jobj.has("companyid")) {
            	hqlVarPart += " company = ?,";
            	params.add(get(Company.class, jobj.getString("companyid")));
            }
            if(jobj.has("userid")) {
            	hqlVarPart += " usersByUpdatedbyid = ?,";
            	params.add(get(User.class, jobj.getString("userid")));
            }
            if(jobj.has("lastname")) {
            	hqlVarPart += " lastname = ?,";
            	params.add(jobj.getString("lastname"));
            }
            if(jobj.has("firstname")) {
            	hqlVarPart += " firstname = ?,";
            	params.add(jobj.getString("firstname"));
            }
            if(jobj.has("type")) {
            	hqlVarPart += " type = ?,";
            	params.add(jobj.getString("type"));
            }
            if(jobj.has("phone")) {
            	hqlVarPart += " phone = ?,";
            	params.add(jobj.getString("phone"));
            }
            if(jobj.has("email")) {
            	hqlVarPart += " email = ?,";
            	params.add(jobj.getString("email"));
            }
            if(jobj.has("street")) {
            	hqlVarPart += " addstreet = ?,";
            	params.add(jobj.getString("street"));
            }
            if(jobj.has("ratingid")) {
            	hqlVarPart += " crmCombodataByRatingid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("ratingid")));
            }
            if(jobj.has("title")) {
            	hqlVarPart += " title = ?,";
            	params.add(jobj.getString("title"));
            }
            if(jobj.has("leadstatusid")) {
            	hqlVarPart += " crmCombodataByLeadstatusid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("leadstatusid")));
            }
            if(jobj.has("industryid")) {
            	hqlVarPart += " crmCombodataByIndustryid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("industryid")));
            }
            if(jobj.has("leadsourceid")) {
            	hqlVarPart += " crmCombodataByLeadsourceid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("leadsourceid")));
            }
            if(jobj.has("updatedon")) {
            	hqlVarPart += " updatedOn = ?,";
            	params.add(jobj.getLong("updatedon"));
            }
            if(jobj.has("leadconversiondate")) {
            	hqlVarPart += " convertedOn = ?,";
            	params.add(new Date().getTime());
            }
            if(jobj.has("createdon")) 
            {
            	Long createdOn = jobj.getLong("createdon");
            		hqlVarPart += " createdOn = ?,";
	                	params.add(createdOn);
            }
            if(jobj.has("isconverted")) {
            	hqlVarPart += " isconverted = ?,";
            	params.add(jobj.getString("isconverted"));
            }
            if(jobj.has("istransfered")) {
            	hqlVarPart += " istransfered = ?,";
            	params.add(jobj.getString("istransfered"));
            }
            if(jobj.has("validflag")) {
            	hqlVarPart += " validflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("deleteflag")) {
            	hqlVarPart += " deleteflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("deleteflag")));
                String query = "delete from LeadProducts where leadid.leadid in (:leadids)";
                Map map = new HashMap();
                map.put("leadids", leadids);
                executeUpdate(query, null, map);
            }
            if(jobj.has("conversiontype")) {
            	hqlVarPart += " conversiontype = ?,";
            	params.add(jobj.getString("conversiontype"));
            }
            if(jobj.has("price")) {
            	hqlVarPart += " price = ?,";
            	params.add(jobj.getString("price"));
            }
            if(jobj.has("revenue")) {
            	hqlVarPart += " revenue = ?,";
            	params.add(jobj.getString("revenue"));
            }
            if(jobj.has("productid")) {
                saveLeadProducts(leadids,(String[]) jobj.get("productsId"));
            }
            if(jobj.has("leadownerid")) {
                setMainLeadOwner(leadids,jobj.getString("leadownerid"));
            }
            
            if(jobj.has("CrmLeadCustomDataobj")&&jobj.getBoolean("CrmLeadCustomDataobj")){
            	linkCustomData();
            }

            hqlVarPart = hqlVarPart.substring(0, Math.max(0,hqlVarPart.lastIndexOf(',')));
            String hql = "update CrmLead set "+hqlVarPart+" where leadid in (:leadids)";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("leadids", leadids);
            executeUpdate(hql, params.toArray(), map);
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.editLeads : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.editLeads : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.editLeads : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}
	
	public void linkCustomData() {
        String query="update crm_lead l inner join crmleadcustomdata cu on cu.leadid=l.leadid and l.crmleadcustomdataref is null set l.crmleadcustomdataref=l.leadid";
        updateJDBC(query,new Object[]{});		
	}

    public boolean checkWebLeadAssignedOwner(String leadid) throws ServiceException{
        boolean isAlreadyAssigned = true;
        try {
            ArrayList filter_params = new ArrayList();
            String Hql = "select distinct c from CrmLead c where c.ownerconfirm = ? and c.leadid = ?";
            filter_params.add(false);
            filter_params.add(leadid);
            List<CrmLead> ll = executeQuery(Hql,filter_params.toArray());
            if(ll.size()>0)
                isAlreadyAssigned = false;
        } catch(Exception e) {
            throw ServiceException.FAILURE("crmLeadDAOImpl.checkWebLeadAssignedOwner : "+e.getMessage(), e);
        } finally{
            return isAlreadyAssigned;
        }
    }

    public String confirmWebLeadOwner(String leadid, String ownerid) throws ServiceException{
        String oldOwner = null;
        try {
            CrmLead crmLead = (CrmLead) get(CrmLead.class, leadid);
            // get old owner id
            List<String> idsList = new ArrayList<String>();
            idsList.add(leadid);
            Map<String, List<LeadOwnerInfo>> owners = getLeadOwners(idsList);
            String[] ownerInfo = crmLeadHandler.getAllLeadOwners(owners.get(leadid));
            oldOwner = ownerInfo[2];
            setMainLeadOwner(new String[]{leadid},ownerid);
            crmLead.setOwnerconfirm(true);
            save(crmLead);
        } catch(Exception e) {
            throw ServiceException.FAILURE("crmLeadDAOImpl.confirmWebLeadOwner : "+e.getMessage(), e);
        } finally{
            return oldOwner;
        }
    }
}
