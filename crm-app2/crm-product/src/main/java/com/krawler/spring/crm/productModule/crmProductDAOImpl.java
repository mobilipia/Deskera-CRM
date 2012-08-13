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
package com.krawler.spring.crm.productModule; 
import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import java.util.ArrayList;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.crm.database.tables.CrmProductCustomData;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.dao.DataAccessException;

public class crmProductDAOImpl extends BaseDAO implements crmProductDAO {
    /**
     *
     * @param requestParams
     * @param usersList
     * @return KwlReturnObject
     * @throws com.krawler.common.service.ServiceException
     */
    @Override
    public KwlReturnObject getProducts(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        List ll = null;
        Long dl=0l;
        try {
            boolean archive = false;
            requestParams.put(Constants.moduleid,4);
            if(requestParams.containsKey("isarchive") && requestParams.get("isarchive") != null) {
                archive = Boolean.parseBoolean(requestParams.get("isarchive").toString());
            }
            
            String companyid = requestParams.get("companyid").toString();            
            String appendCase = "and";
            ArrayList filter_params = new ArrayList();

            String selCountQuary = "select count(distinct c.productid) ";
            String selQuery = "select distinct c ";
            String Hql = " from CrmProduct c left join c.crmCombodataByCategoryid cc "+crmManagerCommon.getJoinQuery(requestParams);
            String filterQuery = " where c.deleteflag=0 and c.isarchive= ? and c.company.companyID= ?  ";

            filter_params.add(archive);
            filter_params.add(companyid);
            String Searchjson = "";
            if(requestParams.containsKey("searchJson") && !requestParams.get("searchJson").toString().equals("")) {
                Searchjson = requestParams.get("searchJson").toString();
                if (!StringUtil.isNullOrEmpty(Searchjson)) {
                    requestParams.put(Constants.Searchjson, Searchjson);
                    requestParams.put(Constants.appendCase, appendCase);
                    String mySearchFilterString = String.valueOf(StringUtil.getMyAdvanceSearchString(requestParams).get(Constants.myResult));
                    filterQuery += mySearchFilterString;
                    StringUtil.insertParamAdvanceSearchString(filter_params, Searchjson);
                }
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
                    String[] searchcol = new String[]{"c.productname","c.vendornamee", "cc.value"};
                    StringUtil.insertParamSearchString(filter_params, ss, 3);
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
            if(!heirarchyPerm && usersList !=null){
                filterQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
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
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmProductDAOImpl.getProducts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl.intValue());
    }

    /**
     *
     * @param companyid
     * @param usersList
     * @return KwlReturnObject
     * @throws com.krawler.common.service.ServiceException
     */
    @Override
    public KwlReturnObject getAllProducts(HashMap<String, Object> requestParams) throws ServiceException {
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
            String Hql = "select distinct c from CrmProduct c ";
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
                orderQuery = " order by c.productname ";
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
            throw ServiceException.FAILURE("crmProductDAOImpl.getAllProducts", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /**
     *
     * @param JSONObject which contains fields of CrmProduct class.
     * @return KwlReturnObject
     * @throws com.krawler.common.service.ServiceException
     */
    @Override
    public KwlReturnObject addProducts(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            DateFormat df = authHandler.getDateFormat(jobj);
            CrmProduct crmProduct = new CrmProduct();
            if(jobj.has("productid")) {
                id = jobj.getString("productid");
                crmProduct.setProductid(id);
            }
            if(jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmProduct.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("pname")) {
                crmProduct.setProductname(jobj.getString("pname"));
            }
            if(jobj.has("productname")) {
                crmProduct.setProductname(jobj.getString("productname"));
            }
            //   crmProduct.setCrmCombodataByVendornameid((CrmCombodata) session.get(CrmCombodata.class, jobj.getString("vendornameid")));
            if(jobj.has("categoryid")) {
                crmProduct.setCrmCombodataByCategoryid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("categoryid")));
            }
            if(jobj.has("description")) {
                crmProduct.setDescription(jobj.getString("description"));
            }
            if(jobj.has("vendornameid")) {
                crmProduct.setVendornamee(jobj.getString("vendornameid"));
            }
            if(jobj.has("vendornamee")) {
                crmProduct.setVendornamee(jobj.getString("vendornamee"));
            }
            if(jobj.has("vendorphoneno")) {
                crmProduct.setVendorphoneno(jobj.getString("vendorphoneno"));
            }
            if(jobj.has("vendoremail")) {
                crmProduct.setVendoremail(jobj.getString("vendoremail"));
            }
            if(jobj.has("unitprice")) {
                crmProduct.setUnitprice(jobj.getString("unitprice"));
            }
            if(jobj.has("ownerid")) {
                crmProduct.setUsersByUserid((User) get(User.class, jobj.getString("ownerid")));
            }
            if(jobj.has("updatedon")) {
                crmProduct.setUpdatedOn(new Date().getTime());
            }
            crmProduct.setCreatedOn(jobj.optLong("createdon",System.currentTimeMillis()));
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmProduct.setUsersByUpdatedbyid((User) get(User.class, userid));
                crmProduct.setUsersByCreatedbyid((User) get(User.class, userid));
            }
            if(jobj.has("validflag")) {
                crmProduct.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("syncaccounting")) {
                crmProduct.setSyncaccounting(jobj.getBoolean("syncaccounting"));
            } else{
                crmProduct.setSyncaccounting(false);
            }

            saveOrUpdate(crmProduct);
            
            ll.add(crmProduct);
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmProductDAOImpl.addProducts : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmProductDAOImpl.addProducts : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmProductDAOImpl.addProducts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /**
     *
     * @param JSONObject which contains fields of CrmProduct class.
     * @return KwlReturnObject
     * @throws com.krawler.common.service.ServiceException
     */
    @Override
    public KwlReturnObject editProducts(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            DateFormat df = authHandler.getDateFormat(jobj);
            CrmProduct crmProduct = null;
            if(jobj.has("productid")) {
                id = jobj.getString("productid");
                crmProduct = (CrmProduct) get(CrmProduct.class, id);
                crmProduct.setProductid(id);
            }
            if(jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmProduct.setCompany((Company) get(Company.class, companyid));
            }
            if(jobj.has("pname")) {
                crmProduct.setProductname(jobj.getString("pname"));
            }
            if(jobj.has("productname")) {
                crmProduct.setProductname(jobj.getString("productname"));
            }
            //   crmProduct.setCrmCombodataByVendornameid((CrmCombodata) session.get(CrmCombodata.class, jobj.getString("vendornameid")));
            if(jobj.has("categoryid")) {
                crmProduct.setCrmCombodataByCategoryid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("categoryid")));
            }
            if(jobj.has("description")) {
                crmProduct.setDescription(jobj.getString("description"));
            }
            if(jobj.has("vendornameid")) {
                crmProduct.setVendornamee(jobj.getString("vendornameid"));
            }
            if(jobj.has("vendornamee")) {
                crmProduct.setVendornamee(jobj.getString("vendornamee"));
            }
            if(jobj.has("vendorphoneno")) {
                crmProduct.setVendorphoneno(jobj.getString("vendorphoneno"));
            }
            if(jobj.has("vendoremail")) {
                crmProduct.setVendoremail(jobj.getString("vendoremail"));
            }
            if(jobj.has("unitprice")) {
                crmProduct.setUnitprice(jobj.getString("unitprice"));
            }
            if(jobj.has("ownerid")) {
                crmProduct.setUsersByUserid((User) get(User.class, jobj.getString("ownerid")));
            }
            if(jobj.has("updatedon")) {
                crmProduct.setUpdatedOn(new Date().getTime());
            }
            if(jobj.has("createdon")) {
                crmProduct.setCreatedOn(jobj.getLong("createdon"));
            } 
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmProduct.setUsersByUpdatedbyid((User) get(User.class, userid));
//                crmProduct.setUsersByCreatedbyid((User) hibernateTemplate.get(User.class, userid));
            }
            if(jobj.has("deleteflag")) {
                crmProduct.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if(jobj.has("validflag")) {
                crmProduct.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("syncaccounting")) {
                crmProduct.setSyncaccounting(jobj.getBoolean("syncaccounting"));
            } else{
                crmProduct.setSyncaccounting(false);
            }

            if(jobj.has("CrmProductCustomDataobj")){
                crmProduct.setCrmProductCustomDataobj((CrmProductCustomData) get(CrmProductCustomData.class, jobj.getString("CrmProductCustomDataobj")));
            }
            saveOrUpdate(crmProduct);
            
            ll.add(crmProduct);
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmProductDAOImpl.editProducts : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmProductDAOImpl.editProducts : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmProductDAOImpl.editProducts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getCrmProductCustomData(HashMap<String, Object> requestParams) throws ServiceException {
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

            String Hql = "from CrmProductCustomData ";

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
            throw ServiceException.FAILURE("crmProductDAOImpl.getCrmProductCustomData : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll,dl);
    }

    @Override
    public List<CrmProduct> getProducts(List<String> recordIds)
    {
        if (recordIds == null || recordIds.isEmpty())
        {
            return null;
        }
        StringBuilder hql = new StringBuilder("from CrmProduct where productid in (");
        
        for (String record: recordIds)
        {
            hql.append("'" + record + "',");
        }
        
        hql.deleteCharAt(hql.length() - 1);
        hql.append(")");

        return executeQuery(hql.toString());
    }

    @Override
    public HashMap<String, CrmProductCustomData> getProductCustomDataMap(List<String> list, String companyid) throws ServiceException {
        List<CrmProductCustomData> ll = null;
        HashMap<String, CrmProductCustomData> productCustomDataMap = new HashMap<String, CrmProductCustomData>();
        try {
            String filterQuery = "";
            List<List> paramll = new ArrayList();
            List<String> paramnames = new ArrayList();
            if(!list.isEmpty()){
                filterQuery = " and c.productid in (:recordlist) ";
                paramll.add(list);
                paramnames.add("recordlist");
            }
            String Hql = "select c from CrmProductCustomData c where c.company.companyID = '"+companyid+"' "+filterQuery;
            ll = executeCollectionQuery(Hql,paramnames,paramll);
            for(CrmProductCustomData productCustomObj : ll){
                productCustomDataMap.put(productCustomObj.getProductid(), productCustomObj);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmProductDAOImpl.getProductCustomDataMap", ex);
        }
        return productCustomDataMap;
    }

    @Override
    public KwlReturnObject updateMassProducts(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        String hqlVarPart = "";
        List<Object> params = new ArrayList<Object>();
        
        try {
            CrmProduct crmProduct = null;
            
            String[] productids = (String[])jobj.get("productid");
            if(jobj.has("companyid")) {
            	hqlVarPart += " company = ?,";
            	params.add(get(Company.class, jobj.getString("companyid")));
            }
            if(jobj.has("productname")) {
                hqlVarPart += " productname = ?,";
            	params.add(jobj.getString("productname"));
            }

            if(jobj.has("categoryid")) {
                hqlVarPart += " crmCombodataByCategoryid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("categoryid")));
            }
            if(jobj.has("description")) {
                hqlVarPart += " description = ?,";
            	params.add(jobj.getString("description"));
            }
            if(jobj.has("vendornamee")) {
                hqlVarPart += " vendornamee = ?,";
            	params.add(jobj.getString("vendornamee"));
            }
            if(jobj.has("vendorphoneno")) {
                hqlVarPart += " vendorphoneno = ?,";
            	params.add(jobj.getString("vendorphoneno"));
            }
            if(jobj.has("vendoremail")) {
                hqlVarPart += " vendoremail = ?,";
            	params.add(jobj.getString("vendoremail"));
            }
            if(jobj.has("unitprice")) {
                hqlVarPart += " unitprice = ?,";
            	params.add(jobj.getString("unitprice"));
            }
            if(jobj.has("ownerid")) {
                hqlVarPart += " usersByUserid = ?,";
            	params.add(get(User.class, jobj.getString("ownerid")));
            }
            if(jobj.has("updatedon")) {
                hqlVarPart += " updatedOn = ?,";
            	params.add(new Date().getTime());
            }
            if(jobj.has("createdon")) {
            	hqlVarPart += " createdOn = ?,";
            	 params.add(jobj.getLong("createdon"));
            }
            if(jobj.has("userid")) {
                hqlVarPart += " usersByUpdatedbyid = ?,";
            	params.add(get(User.class, jobj.getString("userid")));
            }
            if(jobj.has("deleteflag")) {
                hqlVarPart += " deleteflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if(jobj.has("validflag")) {
                hqlVarPart += " validflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("syncaccounting")) {

                hqlVarPart += " syncaccounting = ?,";
            	params.add(jobj.getBoolean("syncaccounting"));
            } else{
                hqlVarPart += " syncaccounting = ?,";
            	params.add(false);
            }

            if(jobj.has("CrmProductCustomDataobj")){

                linkCustomData();
            }
            
            hqlVarPart = hqlVarPart.substring(0, Math.max(0,hqlVarPart.lastIndexOf(',')));
            String hql = "update CrmProduct set "+hqlVarPart+" where productid in (:productids)";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("productids", productids);
            executeUpdate(hql, params.toArray(), map);

        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmProductDAOImpl.updateMassProducts : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmProductDAOImpl.updateMassProducts : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmProductDAOImpl.updateMassProducts : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public void linkCustomData() {
        String query="update crm_product p inner join crmproductcustomdata cu on cu.productid=p.productid and p.crmproductcustomdataref is null set p.crmproductcustomdataref=p.productid";
        updateJDBC(query,new Object[]{});
	}
    
    public void setCustomData(CrmProduct crmProd, CrmProductCustomData crmProdCustomData) {
        crmProd.setCrmProductCustomDataobj(crmProdCustomData);
        save(crmProd);
    }

    public void setCustomData(CrmProduct crmProd, JSONArray cstmData) {
    	StringBuffer fields=new StringBuffer("productid,company");
    	StringBuffer qmarks=new StringBuffer("?,?");
    	ArrayList params = new ArrayList();
    	params.add(crmProd.getProductid());
    	params.add(crmProd.getCompany().getCompanyID());
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
    		StringBuffer sql= new StringBuffer("insert into crmproductcustomdata (").append(fields).append(")VALUES(").append(qmarks).append(')');
    		updateJDBC(sql.toString(), params.toArray());
    	}
    }

    public Map<String, User> getProductOwners(List<String> idsList) {
        Map<String, User> ownerMap = new HashMap<String, User>();
        if (idsList != null && !idsList.isEmpty())
        {
            String query = "select p.productid, p.usersByUserid from CrmProduct p where p.productid in (:prodList)";
            List<List> values = new ArrayList<List>();
            values.add(idsList);
            List<Object[]> results = executeCollectionQuery(query, Collections.singletonList("prodList"), values);

            if (results != null)
            {
                for (Object[] result: results)
                {
                    String ProdId = (String) result[0];
                    ownerMap.put(ProdId, (User) result[1]);
                }
            }
        }
        return ownerMap;
    }
}
