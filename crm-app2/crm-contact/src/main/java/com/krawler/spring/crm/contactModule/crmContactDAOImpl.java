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
package com.krawler.spring.crm.contactModule;
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
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.LeadOwners;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.crm.contact.dm.ContactOwnerInfo;
import com.krawler.crm.database.tables.CrmContactCustomData;
import com.krawler.crm.database.tables.contactOwners;
import com.krawler.crm.database.tables.CrmCustomer;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.trilead.ssh2.log.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class crmContactDAOImpl extends BaseDAO implements crmContactDAO {

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactDAO#getContacts(java.util.HashMap)
     */
    public KwlReturnObject getContacts(HashMap<String, Object> requestParams) throws ServiceException {
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

            String Hql = "select c from CrmContact c  ";
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
            throw ServiceException.FAILURE("crmContactDAOImpl.getActiveContact : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, "002", "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactDAO#addContacts(com.krawler.utils.json.base.JSONObject)
     */
    public KwlReturnObject addContacts(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String tZStr = jobj.has("tzdiff") ? jobj.getString("tzdiff"): null;
            DateFormat df = authHandler.getDateMDYFormatter();
            if (tZStr != null)
            {
                TimeZone zone = TimeZone.getTimeZone("GMT" + tZStr);
                df.setTimeZone(zone);
            }
            String companyid = null;
            String userid = null;
            String id = "";
            CrmContact crmContact = new CrmContact();
            if(jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmContact.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("description")) {
                crmContact.setDescription(jobj.getString("description"));
            }
            if(jobj.has("email")) {
                crmContact.setEmail(jobj.getString("email"));
            }
            if(jobj.has("firstname")) {
                crmContact.setFirstname(jobj.getString("firstname"));
            }
            if(jobj.has("industryid")) {
                crmContact.setCrmCombodataByIndustryid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("industryid")));
            }
            if(jobj.has("lastname")) {
                crmContact.setLastname(jobj.getString("lastname"));
            }
            if(jobj.has("street")) {
                crmContact.setMailstreet(jobj.getString("street"));
            }
            if(jobj.has("mobileno")) {
                crmContact.setMobileno(jobj.getString("mobileno"));
            }
            if(jobj.has("phoneno")) {
                crmContact.setPhoneno(jobj.getString("phoneno"));
            }
            if(jobj.has("title")) {
                crmContact.setTitle(jobj.getString("title"));
            }
            if(jobj.has("leadsourceid")) {
                crmContact.setCrmCombodataByLeadsourceid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("leadsourceid")));
            }

            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmContact.setUsersByUpdatedbyid((User) get(User.class, userid));
                crmContact.setUsersByCreatedbyid((User) get(User.class, userid));
            }
            if(jobj.has("updatedon")) {
                crmContact.setUpdatedOn(new Date().getTime());
            }
            if(jobj.has("validflag")) {
                crmContact.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("isarchive")){
            	crmContact.setIsarchive(jobj.getBoolean("isarchive"));
            }
            if(jobj.has("accountid")) {
                CrmAccount ca = (CrmAccount) get(CrmAccount.class, jobj.getString("accountid"));
                if (ca != null) {
                    crmContact.setCrmAccount(ca);
                }
            }
            if(jobj.has("relatednameid")) {
                CrmAccount ca = (CrmAccount) get(CrmAccount.class, jobj.getString("relatednameid"));
                if (ca != null) {
                    crmContact.setCrmAccount(ca);
                }
            }
            if(jobj.has("leadid")){
                CrmLead crmLead = (CrmLead) get(CrmLead.class, jobj.getString("leadid"));
                if(crmLead != null) {
                    crmContact.setLead(crmLead);
                }
            }
            if (jobj.has("contactid")) {
                id = jobj.getString("contactid");
                crmContact.setContactid(jobj.getString("contactid"));
            }
            if(jobj.has("createdon")) {
                 crmContact.setCreatedOn(jobj.getLong("createdon"));
                }
           else {
                crmContact.setCreatedOn(new Date().getTime());
            }
            if(jobj.has("deleteflag")) {
                crmContact.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if(jobj.has("CrmContactCustomDataobj")){
                crmContact.setCrmContactCustomDataobj((CrmContactCustomData) get(CrmContactCustomData.class, jobj.getString("CrmContactCustomDataobj")));
            }
            saveOrUpdate(crmContact);
            if(jobj.has("contactownerid")) {
                setMainContactOwner(new String[]{id},jobj.getString("contactownerid"));
            }
            ll.add(crmContact);

        } catch(JSONException e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.addContacts : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.addContacts : "+e.getMessage(), e);
        } catch(Exception e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.addContacts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactDAO#editContacts(com.krawler.utils.json.base.JSONObject)
     */
    public KwlReturnObject editContacts(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String tZStr = jobj.has("tzdiff") ? jobj.getString("tzdiff"): null;
            DateFormat df = authHandler.getDateMDYFormatter();
            if (tZStr != null)
            {
                TimeZone zone = TimeZone.getTimeZone("GMT" + tZStr);
                df.setTimeZone(zone);
            }
            String companyid = null;
            String userid = null;
            String id = "";
            CrmContact crmContact = null;
            if(jobj.has("contactid")) {
                id = jobj.getString("contactid");
                crmContact = (CrmContact) get(CrmContact.class, id);
            }
            if(jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmContact.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("description")) {
                crmContact.setDescription(jobj.getString("description"));
            }
            if(jobj.has("email")) {
                crmContact.setEmail(jobj.getString("email"));
            }
            if(jobj.has("firstname")) {
                crmContact.setFirstname(jobj.getString("firstname"));
            }
            if(jobj.has("industryid")) {
                crmContact.setCrmCombodataByIndustryid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("industryid")));
            }
            if(jobj.has("lastname")) {
                crmContact.setLastname(jobj.getString("lastname"));
            }
            if(jobj.has("street")) {
                crmContact.setMailstreet(jobj.getString("street"));
            }
            if(jobj.has("mobileno")) {
                crmContact.setMobileno(jobj.getString("mobileno"));
            }
            if(jobj.has("phoneno")) {
                crmContact.setPhoneno(jobj.getString("phoneno"));
            }
            if(jobj.has("title")) {
                crmContact.setTitle(jobj.getString("title"));
            }
            if(jobj.has("leadsourceid")) {
                crmContact.setCrmCombodataByLeadsourceid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("leadsourceid")));
            }
            if(jobj.has("contactownerid")) {
                setMainContactOwner(new String[]{id},jobj.getString("contactownerid"));
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmContact.setUsersByUpdatedbyid((User) get(User.class, userid));
            }
            if(jobj.has("updatedon")) {
                crmContact.setUpdatedOn(new Date().getTime());
            }
            if(jobj.has("validflag")) {
                crmContact.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("accountid")) {
                CrmAccount ca = (CrmAccount) get(CrmAccount.class, jobj.getString("accountid"));
                crmContact.setCrmAccount(ca);
            }
            if(jobj.has("leadid")){
                CrmLead crmLead = (CrmLead) get(CrmLead.class, jobj.getString("leadid"));
                if(crmLead != null) {
                    crmContact.setLead(crmLead);
                }
            }
            if(jobj.has("isarchive")){
            	crmContact.setIsarchive(jobj.getBoolean("isarchive"));
            }
            if(jobj.has("leadid")){
                CrmLead crmLead = (CrmLead) get(CrmLead.class, jobj.getString("leadid"));
                if(crmLead != null) {
                    crmContact.setLead(crmLead);
                }
            }
            if(jobj.has("createdon")) {
                crmContact.setCreatedOn(jobj.getLong("createdon"));
               }
            if(jobj.has("deleteflag")) {
                crmContact.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }

            if(jobj.has("CrmContactCustomDataobj")){
                crmContact.setCrmContactCustomDataobj((CrmContactCustomData) get(CrmContactCustomData.class, jobj.getString("CrmContactCustomDataobj")));
            }
            saveOrUpdate(crmContact);
            ll.add(crmContact);

        } catch(JSONException e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.editContacts : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.editContacts : "+e.getMessage(), e);
        } catch(Exception e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.editContacts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    
    public KwlReturnObject loginMail(JSONObject jobj) throws ServiceException {
        int dl = 0;
        CrmCustomer crmCustomer = new CrmCustomer();
        List ll=null;
        List ll1=null;
        List ll2=null;
        Boolean success=true;
        String msg="";
		try {
			if(jobj.has("contactId")) {
				String contactid=jobj.getString("contactId");
				String Hql="select count(*) as count from CrmCustomer c where c.contact.contactid=? ";
		        ll = executeQuery(Hql, new Object[]{contactid});
			if( Integer.parseInt(ll.toArray()[0].toString()) ==0){ //
            	crmCustomer.setContact((CrmContact) get(CrmContact.class, jobj.getString("contactId")));
            if(jobj.has("uuid")) {
                crmCustomer.setId(jobj.getString("uuid"));
            }
            if(jobj.has("emailId")) {
                crmCustomer.setEmail(jobj.getString("emailId"));
            }
            if(jobj.has("pswd")) {
                crmCustomer.setPasswd(jobj.getString("pswd"));
            }
            if(jobj.has("companyId")) {	
                crmCustomer.setCompany((Company) get(Company.class, jobj.getString("companyId")));
            }
            if(jobj.has("setActive")){
            	crmCustomer.setActive(jobj.getBoolean("setActive"));
            }
            String Hqlquery="select c.firstname, c.lastname from CrmContact c where c.contactid=? ";
            String hql="select companyName from Company c where c.companyID=? ";
            ll1 = executeQuery(Hqlquery, new Object[]{contactid});
            ll2=executeQuery(hql,new Object[]{jobj.getString("companyId")});
            save(crmCustomer);
            ll.addAll(ll1);
            ll.addAll(ll2);
            ll.add(crmCustomer);
            msg="Login information is sent to the contact person";
            success=true;
			}else{
				msg= "SORRY ! This contact person has already login";
			    success=false;
            }
			}
        } catch(JSONException e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.loginMail : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.loginMail : "+e.getMessage(), e);
        } catch(Exception e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.loginMail : "+e.getMessage(), e);
        }
        return new KwlReturnObject(success, msg, "", ll, 0);
        
    }
 public void activate_deactivateLogin(String contactid, boolean active) throws ServiceException {
	 try {
	        String hql = "update CrmCustomer c set active = ? where c.contact.contactid =? ";
	        executeUpdate(hql, new Object[]{ active,contactid});
	       	        
		}  catch (Exception e) {
    	logger.warn("Exception in crmContactDaoImpl.activate_deactivateLogin:", e);
	}
	 
 }
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactDAO#getAllContacts(java.util.HashMap)
     */
    public KwlReturnObject getAllContacts(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
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
            String Hql = "select distinct c from contactOwners co inner join co.contact c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"c.lastname","c.firstname"};
                    StringUtil.insertParamSearchString(filter_params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    filterQuery +=searchQuery;
                }
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
                orderQuery = " order by c.lastname ";
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
            throw ServiceException.FAILURE("crmContactDAOImpl.getAllContacts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactDAO#getContacts(java.util.HashMap, java.lang.StringBuffer, java.util.ArrayList, java.util.ArrayList)
     */
    public KwlReturnObject getContacts(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        Long dl = 0l;
        try {
            requestParams.put(Constants.moduleid,6);
            String appendCase = "and";
            String selQuery = "select distinct c.contactid ";
            String selExportQuery = "select distinct c ";
            String selCountQuery = "select count(distinct c.contactid) ";
            String selQuery2 = "select distinct c ";
            String joinQuery = crmManagerCommon.getJoinQuery(requestParams);
            String Hql = " from contactOwners co inner join co.contact c "+joinQuery;
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            String Searchjson = "";
            if(requestParams.containsKey("searchJson") && requestParams.get("searchJson") != null) {
                Searchjson = requestParams.get("searchJson").toString();
                if (!StringUtil.isNullOrEmpty(Searchjson)) {
                    requestParams.put(Constants.Searchjson, Searchjson);
                    requestParams.put(Constants.appendCase, appendCase);
                    String mySearchFilterString = String.valueOf(StringUtil.getMyAdvanceSearchString(requestParams).get(Constants.myResult));
                    filterQuery += mySearchFilterString;
                    StringUtil.insertParamAdvanceSearchString(filter_params, Searchjson);
                }
            }
            if(requestParams.containsKey("email") && requestParams.get("email") != null) {
                filterQuery += " and c.email != '' ";
            }
            Object config = null;
            if(requestParams.containsKey("config")) {
                config = requestParams.get("config");
            }
            if (config != null) {
                filterQuery += " and c.validflag=1 ";
            }
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"c.firstname","c.lastname", "c.title"};
                    int searchCount = 3;
                    if(requestParams.containsKey("email") && requestParams.get("email") != null) {
                        searchcol = new String[]{"c.firstname","c.lastname"};
                        searchCount = 2;
                    }
                    
                    StringUtil.insertParamSearchString(filter_params, ss, searchCount);
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
                filterQuery += " and co.usersByUserid.userID in (" + usersList + ") ";
            }

            String orderQuery = " order by c.createdOn desc ";
            String dir = null;
            String dbname = null;
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
                countQuery = selExportQuery + Hql + filterQuery + orderQuery;
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
                if(ll!=null && !ll.isEmpty()){
                      String contactquery = " from CrmContact c "+joinQuery+" where c.contactid in (:recordlist) ";
                      String critquery = selQuery2 + contactquery + orderQuery;
                      List<List> paramll = new ArrayList();
                      List<String> paramnames = new ArrayList();
                      paramll.add(ll);
                      paramnames.add("recordlist");
                      ll = executeCollectionQuery(critquery,paramnames,paramll);
//                    DetachedCriteria crit = DetachedCriteria.forClass(CrmContact.class, "c");
//                    crit = crit.add(Restrictions.in("contactid", ll));
//                    if(dbname!=null){
//                        if(association != null && !association.isEmpty()){
//                            if(association!=null && association.split("\\.").length>2){
//                                 String association1 = association.substring(0, association.lastIndexOf("."));
//                                 String association2 = association.substring(association.lastIndexOf(".") + 1, association.length());
//                                 DetachedCriteria addcrit = crit.createCriteria(association1, "assn1");
//                                 crit = addcrit.createCriteria(association2, "assn");
//                            } else{
//                                crit = crit.createCriteria(association, "assn");
//                            }
//                            if(dir!=null && dir.equalsIgnoreCase("DESC")){
//                                crit = crit.addOrder(Order.desc(property));
//                            }else{
//                                crit = crit.addOrder(Order.asc(property));
//            }
//                        }else{
//                            if(dir!=null && dir.equalsIgnoreCase("DESC")){
//                                crit = crit.addOrder(Order.desc(dbname));
//                            }else{
//                                crit = crit.addOrder(Order.asc(dbname));
//                            }
//                        }
//                    }
//                    ll = findByCriteria(crit);
                }
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.getContacts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl.intValue());
    }

    /**
     * @param contactid
     * @param ownerid
     * @throws ServiceException
     */
    @Override
    public void setMainContactOwner(String[] contactids,String ownerid) throws ServiceException{
        String hql="delete from contactOwners where contact.contactid in (:contactids) and mainOwner ='T'";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("contactids", contactids);
        executeUpdate(hql, null, map);
        User user = (User)get(User.class, ownerid);
        for(String contactid:contactids){
	        contactOwners contactOwnersObj = new contactOwners();
	        contactOwnersObj.setContact((CrmContact)get(CrmContact.class,contactid));
	        contactOwnersObj.setUsersByUserid(user);
	        contactOwnersObj.setMainOwner(true);
	        save(contactOwnersObj);
        }
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactDAO#saveContactOwners(java.util.HashMap)
     */
    public KwlReturnObject saveContactOwners(HashMap<String, Object> requestParams) throws Exception {
        List ll = new ArrayList();
        try {
            String contactid = "";
            if(requestParams.containsKey("contactid") && requestParams.get("contactid") != null) {
                contactid =  requestParams.get("contactid").toString();
            }
            String owners = "";
            if(requestParams.containsKey("owners") && requestParams.get("owners") != null) {
                owners =  requestParams.get("owners").toString();
            }
            String mainowner = "";
            if(requestParams.containsKey("mainOwner") && requestParams.get("mainOwner") != null) {
                mainowner =  requestParams.get("mainOwner").toString();
            }
            String hql="delete from contactOwners c where c.contact.contactid = ? ";
            executeUpdate(hql, contactid);

            contactOwners contactOwnersObj = new contactOwners();
            contactOwnersObj.setContact((CrmContact)get(CrmContact.class, contactid));
            contactOwnersObj.setUsersByUserid((User)get(User.class, mainowner));
            contactOwnersObj.setMainOwner(true);
            save(contactOwnersObj);

            if(!StringUtil.isNullOrEmpty(owners) && !owners.equalsIgnoreCase("undefined") ){
                String[] ownerIds = owners.split(",");
                for (int i = 0;i < ownerIds.length;i++){
                    contactOwnersObj = new contactOwners();
                    contactOwnersObj.setContact((CrmContact)get(CrmContact.class, contactid));
                    contactOwnersObj.setUsersByUserid((User)get(User.class, ownerIds[i]));
                    contactOwnersObj.setMainOwner(false);
                    save(contactOwnersObj);
                }
            }
            ll.add(contactOwnersObj);
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.saveContactOwners", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, 1);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactDAO#getContactOwners(java.util.HashMap)
     */
    public KwlReturnObject getContactOwners(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
            boolean pagingFlag = false;
            boolean totalCountFlag = false;
            int start = 0;
            int limit = 25;
            if(requestParams.containsKey("pagingFlag") && requestParams.get("pagingFlag") != null
                    && requestParams.containsKey("start") && requestParams.get("start") != null
                    && requestParams.containsKey("limit") && requestParams.get("limit") != null) {
                pagingFlag = (Boolean) requestParams.get("pagingFlag");
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            if(requestParams.containsKey("totalCountFlag") && requestParams.get("totalCountFlag") != null) {
                totalCountFlag = (Boolean) requestParams.get("totalCountFlag");
            }
            String Hql = "";
            ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
            ArrayList filter_params = (ArrayList) requestParams.get("filter_params");
            ArrayList order_by = new ArrayList();
            ArrayList order_type = new ArrayList();
            if(requestParams.containsKey("order_by"))
                order_by =(ArrayList) requestParams.get("order_by");
            if(requestParams.containsKey("order_type"))
                order_type = (ArrayList) requestParams.get("order_type");

            boolean distinctFlag = false;
            if(requestParams.containsKey("distinctFlag") && requestParams.get("distinctFlag") != null){
                distinctFlag = (Boolean) requestParams.get("distinctFlag");
            }

            if(distinctFlag) {
                Hql = "select distinct c from contactOwners co inner join co.contact c ";
            } else {
                Hql = "select co, c from contactOwners co inner join co.contact c ";
            }
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            Hql += filterQuery;
            String orderQuery = StringUtil.orderQuery(order_by,order_type);
            Hql += orderQuery;

            if(totalCountFlag) {
                ll = executeQuery(Hql, filter_params.toArray());
                dl= ll.size();
            }
            if(pagingFlag) {
                ll = executeQueryPaging(Hql, filter_params.toArray(), new Integer[]{start,limit});
            }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactDAO#getCrmContactCustomData(java.util.HashMap)
     */
    public KwlReturnObject getCrmContactCustomData(HashMap<String, Object> requestParams) throws ServiceException {
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

            String Hql = "from CrmContactCustomData ";

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
            throw ServiceException.FAILURE("crmContactDAOImpl.CrmContactCustomData : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll,dl);
    }

    @Override
    public void setCustomData(CrmContact crmContact, CrmContactCustomData crmContactCustomData) {
        crmContact.setCrmContactCustomDataobj(crmContactCustomData);
        save(crmContact);
    }


    public void setCustomData(CrmContact crmContact, JSONArray cstmData) {
    	StringBuffer fields=new StringBuffer("contactid,company");
    	StringBuffer qmarks=new StringBuffer("?,?");
    	ArrayList params = new ArrayList(); 
    	params.add(crmContact.getContactid());
    	params.add(crmContact.getCompany().getCompanyID());
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
    		StringBuffer sql= new StringBuffer("insert into crmcontactcustomdata (").append(fields).append(")VALUES(").append(qmarks).append(')');
    		updateJDBC(sql.toString(), params.toArray());
    		//TODO how to set CrmContactCustomDataobj to crmContact
    		//crmContact.setCrmContactCustomDataobj(crmContactCustomData);
    		//saveOrUpdate(crmContact);
    	}
    }

    @Override
    public List<CrmContact> getContacts(List<String> recordIds)
    {
        if (recordIds == null || recordIds.isEmpty())
        {
            return null;
        }
        StringBuilder hql = new StringBuilder("from CrmContact where contactid in (");

        for (String record: recordIds)
        {
            hql.append("'" + record + "',");
        }

        hql.deleteCharAt(hql.length() - 1);
        hql.append(")");

        return executeQuery(hql.toString());
    }

    @Override
    public HashMap<String, CrmContactCustomData> getContactCustomDataMap(List<String> list, String companyid) throws ServiceException {
        List<CrmContactCustomData> ll = null;
        HashMap<String, CrmContactCustomData> contactCustomDataMap = new HashMap<String, CrmContactCustomData>();
        try {
            String filterQuery = "";
            List<List> paramll = new ArrayList();
            List<String> paramnames = new ArrayList();
            if(!list.isEmpty()){
                filterQuery = " and c.contactid in (:recordlist) ";
                paramll.add(list);
                paramnames.add("recordlist");
            }
            String Hql = "select c from CrmContactCustomData c where c.company.companyID = '"+companyid+"' "+filterQuery;
            ll = executeCollectionQuery(Hql,paramnames,paramll);
            for(CrmContactCustomData contactCustomObj : ll){
                contactCustomDataMap.put(contactCustomObj.getContactid(), contactCustomObj);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmContactDAOImpl.getContactCustomDataMap", ex);
        }
        return contactCustomDataMap;
    }

    @Override
    public Map<String, List<ContactOwnerInfo>> getContactOwners(List<String> contactIds)
    {
        Map<String, List<ContactOwnerInfo>> ownerMap = new HashMap<String, List<ContactOwnerInfo>>();
        if (contactIds != null && !contactIds.isEmpty())
        {
            StringBuilder query = new StringBuilder("select co.contactId, co.usersByUserid, co from contactOwners co where co.contactId in (");

            for (String contactId: contactIds)
            {
                query.append('\'');
                query.append(contactId);
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
                    String contactId = (String) result[0];
                    ContactOwnerInfo info = new ContactOwnerInfo();
                    info.setUser((User) result[1]);
                    info.setOwner((contactOwners) result[2]);

                    if (ownerMap.containsKey(contactId))
                    {
                        List<ContactOwnerInfo> ownerList = ownerMap.get(contactId);
                        ownerList.add(info);
                    }
                    else
                    {
                        List<ContactOwnerInfo> ownerList = new ArrayList<ContactOwnerInfo>();
                        ownerList.add(info);
                        ownerMap.put(contactId, ownerList);
                    }
                }
            }
        }
        return ownerMap;
    }

    public Map<String, CrmAccount> getContactAccount(List<String> contactIds)
    {
        Map<String, CrmAccount> ownerMap = new HashMap<String, CrmAccount>();
        if (contactIds != null && !contactIds.isEmpty())
        {
            StringBuilder query = new StringBuilder("select c.contactid, c.crmAccount from CrmContact c where contactid in (");

            for (String contactId: contactIds)
            {
                query.append('\'');
                query.append(contactId);
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
                    String contactId = (String) result[0];
                    ownerMap.put(contactId, (CrmAccount)result[1]);
                }
            }
        }
        return ownerMap;
    }
    
	@Override
	public KwlReturnObject updateMassContacts(JSONObject jobj) throws ServiceException {
        String hqlVarPart = "";
        List<Object> params = new ArrayList<Object>();
        List ll = new ArrayList();
        int dl = 0;
        try {
            String tZStr = jobj.has("tzdiff") ? jobj.getString("tzdiff"): null;
            DateFormat df = authHandler.getDateMDYFormatter();
            if (tZStr != null)
            {
                TimeZone zone = TimeZone.getTimeZone("GMT" + tZStr);
                df.setTimeZone(zone);
            }
            String companyid = null;
            String userid = null;
            String id = "";
//            CrmContact crmContact = null;
            String[] contactids = (String[])jobj.get("contactid");
            if(jobj.has("companyid")) {
            	hqlVarPart += " company = ?,";
            	params.add(get(Company.class, jobj.getString("companyid")));
            }
            if(jobj.has("description")) {
            	hqlVarPart += " description = ?,";
            	params.add(jobj.getString("description"));
            }
            if(jobj.has("email")) {
            	hqlVarPart += " email = ?,";
            	params.add(jobj.getString("email"));
            }
            if(jobj.has("firstname")) {
            	hqlVarPart += " firstname = ?,";
            	params.add(jobj.getString("firstname"));
            }
            if(jobj.has("industryid")) {
            	hqlVarPart += " crmCombodataByIndustryid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("industryid")));
            }
            if(jobj.has("lastname")) {
            	hqlVarPart += " lastname = ?,";
            	params.add(jobj.getString("lastname"));
            }
            if(jobj.has("street")) {
            	hqlVarPart += " mailstreet = ?,";
            	params.add(jobj.getString("street"));
            }
            if(jobj.has("mobileno")) {
            	hqlVarPart += " mobileno = ?,";
            	params.add(jobj.getString("mobileno"));
            }
            if(jobj.has("phoneno")) {
            	hqlVarPart += " phoneno = ?,";
            	params.add(jobj.getString("phoneno"));
            }
            if(jobj.has("title")) {
            	hqlVarPart += " title = ?,";
            	params.add(jobj.getString("title"));
            }
            if(jobj.has("leadsourceid")) {
            	hqlVarPart += " crmCombodataByLeadsourceid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("leadsourceid")));
            }
            if(jobj.has("contactownerid")) {
                setMainContactOwner(contactids,jobj.getString("contactownerid"));
            }
            if(jobj.has("userid")) {
            	hqlVarPart += " usersByUpdatedbyid = ?,";
            	params.add(get(User.class, jobj.getString("userid")));
            }
            if(jobj.has("updatedon")) {
            	hqlVarPart += " updatedOn = ?,";
            	params.add(jobj.getLong("updatedon"));
            }
            if(jobj.has("validflag")) {
            	hqlVarPart += " validflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("accountid")) {
            	hqlVarPart += " crmAccount = ?,";
            	params.add(get(CrmAccount.class, jobj.getString("accountid")));
            }
            if(jobj.has("relatednameid")) {
            	hqlVarPart += " crmAccount = ?,";
            	params.add(get(CrmAccount.class, jobj.getString("relatednameid")));
            }
            if(jobj.has("leadid")){
                CrmLead crmLead = (CrmLead) get(CrmLead.class, jobj.getString("leadid"));
                if(crmLead != null) {
                	hqlVarPart += " lead = ?,";
                	params.add(crmLead);
                }
            }
            if(jobj.has("createdon")){
            	      hqlVarPart += " createdOn = ?,";
                    	params.add(jobj.getLong("createdon"));
                    }
                   
            if(jobj.has("deleteflag")) {
            	hqlVarPart += " deleteflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("deleteflag")));
            }

            if(jobj.has("CrmContactCustomDataobj")&&jobj.getBoolean("CrmContactCustomDataobj")){
            	linkCustomData();
            }
            
            hqlVarPart = hqlVarPart.substring(0, Math.max(0,hqlVarPart.lastIndexOf(',')));
            String hql = "update CrmContact set "+hqlVarPart+" where contactid in (:contactids)";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("contactids", contactids);
            executeUpdate(hql, params.toArray(), map);
            
        } catch(JSONException e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.editContacts : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.editContacts : "+e.getMessage(), e);
        } catch(Exception e) {
            throw ServiceException.FAILURE("crmContactDAOImpl.editContacts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	public void linkCustomData() {
		String query="update crm_contact l inner join crmcontactcustomdata cu on cu.contactid=l.contactid and l.crmcontactcustomdataref is null set l.crmcontactcustomdataref=l.contactid";			
        updateJDBC(query,new Object[]{});		
	}

	@Override
	public int getLoginState(String contactid) {
		int state=0;
		List<CrmCustomer> ll=null;
		String hql="from CrmCustomer c where c.contact.contactid=?";
		ll = executeQuery(hql, contactid);
		if(!ll.isEmpty()){
			for(CrmCustomer ob : ll){
				if(ob.isActive())
                        state=1;
                    else
                        state=2;
			}
		}
		return state;
	}

	public Map<String, Integer> getLoginState(List<String> contactIds) {
		int state=0;
        Map<String, Integer> contactCustomloginDataMap = new HashMap<String, Integer>();
		List<CrmCustomer> ll=null;
        String filterQuery = "";
		String hql="from CrmCustomer c";
        List<List> paramll = new ArrayList();
        List<String> paramnames = new ArrayList();
        if(!contactIds.isEmpty()){
            filterQuery = "  where c.contact.contactid in (:recordlist) ";
            paramll.add(contactIds);
            paramnames.add("recordlist");
        }
        hql += filterQuery;
        ll = executeCollectionQuery(hql,paramnames,paramll);
        if(!ll.isEmpty()){
			for(CrmCustomer ob : ll){
				if(ob.isActive())
                        state=1;
                    else
                        state=2;
                contactCustomloginDataMap.put(ob.getContact().getCompanyid(), state);
			}
		}
		return contactCustomloginDataMap;
	}
    
	@Override
	public void custPassword_Change(String newpassword, String customerid) {
		String hql = "update CrmCustomer c set c.passwd = ? where c.id =? ";
		executeUpdate(hql, new Object[] { newpassword, customerid });

	}

	@Override
	public CrmCustomer getCustomer(String customerid) {
		return (CrmCustomer)get(CrmCustomer.class,customerid);
	}

	@Override
	public boolean isEmailIdExist(String emailId, String companyid) {
		List ll=executeQuery("select c.id from CrmCustomer as c  inner join c.contact as cc where cc.deleteflag=0 and cc.validflag=1 and c.email = ? and c.company.companyID=?",new Object[]{emailId,companyid});
		if(!ll.isEmpty())
			return true;
		else
			return false;
	}
	
}
