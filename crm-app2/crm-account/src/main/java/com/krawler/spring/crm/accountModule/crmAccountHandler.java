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
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Header;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.account.dm.AccountOwnerInfo;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.crm.database.tables.AccountProducts;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.accountOwners;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;

public class crmAccountHandler {
    public static JSONObject getAccountReportJson(crmAccountDAO crmAccountDAOObj, List<CrmAccount> ll, HttpServletRequest request, boolean export, int totalSize, crmManagerDAO crmManagerDAOObj, crmCommonDAO crmCommonDAOObj,
            Map<String, List<AccountOwnerInfo>> accowners, HashMap<String, DefaultMasterItem> defaultMasterMap) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String qucikSerachFields = request.getParameter("quickSearchFields");
            for (CrmAccount aCrmAccount : ll ) {
                JSONObject tmpObj = new JSONObject();

                tmpObj.put("accountid", aCrmAccount.getAccountid());
                tmpObj.put("account", aCrmAccount.getAccountname());
                String[] ownerInfo=crmAccountHandler.getAllAccOwners(accowners.get(aCrmAccount.getAccountid()));
                tmpObj.put("accountowner",ownerInfo[4] );
                tmpObj.put("exportmultiowners", ownerInfo[5]);

                tmpObj.put("website", (aCrmAccount.getWebsite() != null ? aCrmAccount.getWebsite() : ""));
                tmpObj.put("phone", aCrmAccount.getPhone());
                tmpObj.put("address", aCrmAccount.getMailstreet());
                tmpObj.put("revenue", !StringUtil.isNullOrEmpty(aCrmAccount.getRevenue()) ? crmManagerDAOObj.currencyRender(aCrmAccount.getRevenue(), currencyid) : "");
                tmpObj.put("description", (aCrmAccount.getDescription() != null ? aCrmAccount.getDescription() : ""));
          //      tmpObj.put("createdon", crmManagerDAOObj.preferenceDate(request, aCrmAccount.getCreatedon(), 0));
                tmpObj.put("createdon", crmManagerCommon.dateNull(aCrmAccount.getCreatedon()));
                tmpObj.put("accountname", aCrmAccount.getAccountname());
                tmpObj.put("productid", crmManagerCommon.moduleObjNull(aCrmAccount.getCrmProduct(), "Productid"));
                tmpObj.put("product", (aCrmAccount.getCrmProduct() != null ? aCrmAccount.getCrmProduct().getProductname() : ""));
                tmpObj.put("price", aCrmAccount.getPrice() != null && !aCrmAccount.getPrice().equals("") ? crmManagerDAOObj.currencyRender(aCrmAccount.getPrice(), currencyid) : "");

                tmpObj.put("accounttypeid", StringUtil.hNull(aCrmAccount.getAccounttypeID()));
                if(aCrmAccount.getAccounttypeID() != null && defaultMasterMap.containsKey(aCrmAccount.getAccounttypeID())) {
                    tmpObj.put("type", defaultMasterMap.get(aCrmAccount.getAccounttypeID()).getValue());
                }
                
                if(aCrmAccount.getIndustryID() != null && defaultMasterMap.containsKey(aCrmAccount.getIndustryID())) {
                    tmpObj.put("industry", defaultMasterMap.get(aCrmAccount.getIndustryID()).getValue());
                }
                tmpObj.put("industryid", StringUtil.hNull(aCrmAccount.getIndustryID()));
                tmpObj.put("validflag", aCrmAccount.getValidflag());

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List<DefaultHeader> ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Account");
                for(DefaultHeader obj : ll1) {
                    String newHeader = crmManagerCommon.getNewColumnHeader(crmCommonDAOObj, obj.getDefaultHeader(), "Account", companyid);
                    if(StringUtil.equal(Header.ACCOUNTOWNERHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?"Account Owner":newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?"Account Owner":newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "accountowner");
                        jobjTemp.put("title", "exportmultiowners");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        String Hdr = StringUtil.isNullOrEmpty(newHeader)?"Account Name":newHeader;
                        if(StringUtil.equal(Header.ACCOUNTNAMEHEADER, qucikSerachFields)){
                            jobjTemp.put("qucikSearchText", Hdr);
                        }
                        jobjTemp.put("header", Hdr);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?"Account Name":newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "account");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTTYPEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?Header.ACCOUNTTYPEHEADER:newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?Header.ACCOUNTTYPEHEADER:newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "type");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTINDUSTRYHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?Header.ACCOUNTINDUSTRYHEADER:newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?Header.ACCOUNTINDUSTRYHEADER:newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "industry");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTREVENUEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?"Revenue":newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?"Revenue":newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "revenue");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTPRICEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?"Price":newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?"Price":newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "price");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTPHONEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?Header.ACCOUNTPHONEHEADER:newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?Header.ACCOUNTPHONEHEADER:newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "phone");
                        jobjTemp.put("align", "right");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if(StringUtil.equal(Header.ACCOUNTWEBSITEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?Header.ACCOUNTWEBSITEHEADER:newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?Header.ACCOUNTWEBSITEHEADER:newHeader);
                        jobjTemp.put("sortable", true);
                        jobjTemp.put("dataIndex", "website");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }else if (StringUtil.equal(Header.ACCOUNTCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader) ? Header.ACCOUNTCREATIONDATEHEADER : newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader) ? Header.ACCOUNTCREATIONDATEHEADER : newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("align", "center");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accountowner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "account");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "type");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "industry");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "revenue");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "price");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "phone");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "website");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdon");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);

                commData.put("columns", jarrColumns);
                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return commData;
    }

    public static String[] getAllAccOwners(crmAccountDAO crmAccountDAOObj,String accid) throws ServiceException{
        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("ao.account.accountid");
        filter_params.add(accid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();

        kmsg = crmAccountDAOObj.getAccountOwners(requestParams, filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();
        accountOwners accountOwnersObj;
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        while(ite.hasNext()){
            Object row[] = (Object[]) ite.next();
            accountOwnersObj=(accountOwners)row[0];
            if(accountOwnersObj.isMainOwner()){
                mainLeadOwner=accountOwnersObj.getUsersByUserid();
            }else{
                ownerId+=accountOwnersObj.getUsersByUserid().getUserID()+",";
                ownerNames+=StringUtil.getFullName(accountOwnersObj.getUsersByUserid())+", ";
            }
        }

        String mainOwner = "";
        if(mainLeadOwner!=null)
            mainOwner=StringUtil.getFullName(mainLeadOwner);

        String tooltip;
        String gridName;
        if(!StringUtil.isNullOrEmpty(ownerNames)){
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerId = ownerId.substring(0,ownerId.length()-1);
            tooltip="<b>"+mainOwner+"</b>, "+ownerNames+".";
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+"</b>.";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Owners'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }
    
    
    /**
     * 
     * @param aCrmAccount
     * @param accid
     * @return
     * @throws ServiceException
     */
    public static String[] getAllAccOwners(CrmAccount crmAccount) throws ServiceException{
        Set accountOwners = crmAccount.getAccountOwners();        
        Iterator ite = accountOwners.iterator();
        accountOwners accountOwnersObj;
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        while(ite.hasNext()){
        	accountOwnersObj = (accountOwners) ite.next();
            if(accountOwnersObj.isMainOwner()){
                mainLeadOwner=accountOwnersObj.getUsersByUserid();
            }else{
                ownerId+=accountOwnersObj.getUsersByUserid().getUserID()+",";
                ownerNames+=StringUtil.getFullName(accountOwnersObj.getUsersByUserid())+", ";
            }
        }

        String mainOwner = "";
        if(mainLeadOwner!=null)
            mainOwner=StringUtil.getFullName(mainLeadOwner);

        String tooltip;
        String gridName;
        if(!StringUtil.isNullOrEmpty(ownerNames)){
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerId = ownerId.substring(0,ownerId.length()-1);
            tooltip="<b>"+mainOwner+"</b>, "+ownerNames+".";
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+"</b>.";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Owners'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }
    
    public static String[] getAllAccOwners(List<AccountOwnerInfo> owners) throws ServiceException
    {
        if (owners == null || owners.isEmpty())
        {
            return null;
        }
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        for (AccountOwnerInfo owner : owners)
        {
            accountOwners accountOwnersObj = owner.getOwner();
            if (accountOwnersObj.isMainOwner())
            {
                mainLeadOwner = owner.getUser();
            } else
            {
                ownerId += owner.getUser().getUserID() + ",";
                ownerNames += StringUtil.getFullName(owner.getUser()) + ", ";
            }
        }

        String mainOwner = "";
        if (mainLeadOwner != null)
        {
            mainOwner = StringUtil.getFullName(mainLeadOwner);
        }

        String tooltip;
        String gridName;
        if (!StringUtil.isNullOrEmpty(ownerNames))
        {
            ownerNames = ownerNames.substring(0, ownerNames.length() - 2);
            ownerId = ownerId.substring(0, ownerId.length() - 1);
            tooltip = "<b>" + mainOwner + "</b>, " + ownerNames + ".";
            gridName = mainOwner + ", " + ownerNames;
        } else
        {
            tooltip = "<b>" + mainOwner + "</b>.";
            gridName = mainOwner;
        }

        String displayOwnerNames = "<div wtf:qtip=\"" + tooltip + "\"wtf:qtitle='Owners'>" + StringUtil.abbreviate(gridName, 27) + "</div>";
        String[] ownerInfo = { mainOwner, ownerNames, mainLeadOwner.getUserID(), ownerId, displayOwnerNames, gridName };
        return ownerInfo;
    }

    public static JSONObject getAccOwners(crmAccountDAO crmAccountDAOObj, HttpServletRequest request,String accid) throws ServiceException, SessionExpiredException{
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject temp;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String[] ownerInfo = getAllAccOwners(crmAccountDAOObj,accid);
            String mainOwner=ownerInfo[0];
            String ownerNames=ownerInfo[1];

            boolean addOwnerPerm=false;
            if(!StringUtil.isNullOrEmpty(ownerNames)){
                mainOwner+=", ";
            }
            temp = new JSONObject();
            temp.put("owners", ownerNames);
            temp.put("mainOwner",mainOwner );
            jarr.put(temp);

            if(mainOwner.equalsIgnoreCase(userid)){
                addOwnerPerm=true;
            }

            jobj.put("addOwnerPerm", addOwnerPerm);
            jobj.put("ownerList", jarr);

        } catch (JSONException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (HibernateException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }
    public static String getMainAccOwner(crmAccountDAO crmAccountDAOObj, String accid) throws ServiceException{
        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("ao.account.accountid");
        filter_params.add(accid);
        filter_names.add("ao.mainOwner");
        filter_params.add(true);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();

        kmsg = crmAccountDAOObj.getAccountOwners(requestParams, filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();
        accountOwners accountOwnersObj;
        String ownerId="";

        while(ite.hasNext()){
            Object row[] = (Object[]) ite.next();
            accountOwnersObj=(accountOwners)row[0];
            ownerId+=accountOwnersObj.getUsersByUserid().getUserID();
        }
        return ownerId;
    }

    public static List<CrmAccount> getOwnerChangedAccounts(crmAccountDAO crmAccountDAOObj,String[] accIds,String newOwnerId)throws ServiceException{
    	return crmAccountDAOObj.getOwnerChangedAccounts(newOwnerId, accIds);
    } 
    public static String[] getAccOwners(crmAccountDAO crmAccountDAOObj, String accid) throws ServiceException{
        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("ao.account.accountid");
        filter_params.add(accid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();

        kmsg = crmAccountDAOObj.getAccountOwners(requestParams, filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();
        accountOwners accountOwnersObj;
        String ownerId="";
        String ownerNames="";

        while(ite.hasNext()){
            Object row[] = (Object[]) ite.next();
            accountOwnersObj=(accountOwners)row[0];
            ownerId+=accountOwnersObj.getUsersByUserid().getUserID()+",";
            ownerNames+=accountOwnersObj.getUsersByUserid().getFirstName()+StringUtil.hNull(accountOwnersObj.getUsersByUserid().getLastName())+", ";
        }
        if(!StringUtil.isNullOrEmpty(ownerId)){
            ownerId = ownerId.substring(0,ownerId.length()-1);
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerNames ="<div wtf:qtip=\""+ownerNames+"\"wtf:qtitle='Owners'>"+StringUtil.abbreviate(ownerNames,27)+"</div>";
        }
        String[] ownerInfo = {ownerId,ownerNames};
        return ownerInfo;
    }
    public static boolean hasAccountAccess(List<AccountOwnerInfo> owners,StringBuffer usersList) {
        boolean hasAccess = false;
        for (AccountOwnerInfo accountOwnersObj : owners){
            if(usersList.indexOf(accountOwnersObj.getUser().getUserID()) != -1 ){
                hasAccess = true;
                break;
            }
        }
        return hasAccess;
    }
     public static String[] getAccountProducts(crmAccountDAO crmAccountDAOObj, String accountId) throws ServiceException{

        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("ap.accountid.accountid");
        filter_params.add(accountId);
        filter_names.add("ap.productId.deleteflag");
        filter_params.add(0);
        filter_names.add("ap.productId.isarchive");
        filter_params.add(false);
        filter_names.add("ap.productId.validflag");
        filter_params.add(1);
        kmsg = crmAccountDAOObj.getAccountProducts(filter_names, filter_params);
        List<AccountProducts> accProdLL= kmsg.getEntityList();
        String productId="";
        String productNames="";
        String exportproductNames="";
        for (AccountProducts accountProductsObj : accProdLL){
            productId+=accountProductsObj.getProductId().getProductid()+",";
            productNames+=accountProductsObj.getProductId().getProductname()+",";
        }
        if(!StringUtil.isNullOrEmpty(productId)){
            productId = productId.substring(0,productId.length()-1);
            productNames = productNames.substring(0,productNames.length()-1);
            exportproductNames=productNames;
            productNames ="<div wtf:qtip=\""+productNames+"\"wtf:qtitle='Products'>"+StringUtil.abbreviate(productNames,27)+"</div>";
        }
        String[] productInfo = {productId,productNames,exportproductNames};
        return productInfo;
    }
     
     /**
     * @param crmAccountDAOObj
     * @param crmAccount
     * @return
     * @throws ServiceException
     */
    public static String[] getAccountProducts(CrmAccount crmAccount) throws ServiceException {
		List<AccountProducts> accProdLL = (List<AccountProducts>) crmAccount.getCrmProducts();
		String productId = "";
		String productNames = "";
		String exportproductNames = "";
		for (AccountProducts accountProductsObj : accProdLL) {
			productId += accountProductsObj.getProductId().getProductid() + ",";
			productNames += accountProductsObj.getProductId().getProductname()
					+ ",";
		}
		if (!StringUtil.isNullOrEmpty(productId)) {
			productId = productId.substring(0, productId.length() - 1);
			productNames = productNames.substring(0, productNames.length() - 1);
			exportproductNames = productNames;
			productNames = "<div wtf:qtip=\"" + productNames
					+ "\"wtf:qtitle='Products'>"
					+ StringUtil.abbreviate(productNames, 27) + "</div>";
		}
		String[] productInfo = { productId, productNames, exportproductNames };
		return productInfo;
	}

    public static String[] getAccountProducts(List<CrmProduct> products)
    {
        if (products == null || products.isEmpty())
        {
            return null;
        }
        String productId = "";
        String productNames = "";
        String exportproductNames = "";
        for (CrmProduct product : products)
        {
            productId += product.getProductid() + ",";
            productNames += product.getProductname() + ",";
        }
        if (!StringUtil.isNullOrEmpty(productId))
        {
            productId = productId.substring(0, productId.length() - 1);
            productNames = productNames.substring(0, productNames.length() - 1);
            exportproductNames = productNames;
            productNames = "<div wtf:qtip=\"" + productNames + "\"wtf:qtitle='Products'>" + StringUtil.abbreviate(productNames, 27) + "</div>";
        }
        String[] productInfo = { productId, productNames, exportproductNames };
        return productInfo;
    }

    public static HashMap<String, DefaultMasterItem> getAccountDefaultMasterItemsMap(String companyid, crmCommonDAO crmCommonDAOObj) throws ServiceException {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            List filter_names = new ArrayList();
            List filter_params = new ArrayList();
            //Get ids of crmCombomaster table
            String masterIds = "'"+Constants.ACCOUNT_TYPEID+"',";
            masterIds += "'"+Constants.LEAD_INDUSTRYID+"'";
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(Constants.filter_names, filter_names);
            requestParams.put(Constants.filter_params, filter_params);
            return crmCommonDAOObj.getDefaultMasterItemsMap(requestParams);
    }
}
