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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.LeadOwners;
import com.krawler.crm.database.tables.LeadProducts;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.lead.dm.LeadOwnerInfo;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.profileHandler.profileHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class crmLeadHandler {
    public static String getLeadTypeName (String type){
        if(!StringUtil.isNullOrEmpty(type)){
            return (type.equals("0") ? "Individual" :"Company");
        }else {
            return "";
        }
    }
    
    public static boolean  editConvertedLead(HttpServletRequest req)
			throws ServiceException,JSONException,SessionExpiredException {
		boolean editConvertedlead=false;
		try {
			JSONObject obj = new JSONObject();
            String cmppref = req.getSession().getAttribute("companyPreferences").toString();
            JSONObject jsnObj = new JSONObject(cmppref);
            editConvertedlead = jsnObj.getBoolean("convertedlead");

		} catch (com.krawler.utils.json.base.JSONException e) {
			throw ServiceException.FAILURE("Auth.editConvertedLead", e);
		}
		return editConvertedlead;
	}

    public static String getDefaultLeadType(HttpServletRequest request) throws JSONException {
        String cmppref = request.getSession().getAttribute("companyPreferences").toString();
        JSONObject jsnObj = new JSONObject(cmppref);
        boolean ldtype = jsnObj.getBoolean("leadtype");
        if(ldtype){
            return "0";
        }else{
            return "1";
        }
    }
    /**
     * This method should be absolute going forward as we would already have lead before we fetch lead owner.
     * Lead Owner information is already available in lead
     * @param crmLeadDAOObj
     * @param leadid
     * @return
     * @throws ServiceException
     */
    @Deprecated
    public static String[] getAllLeadOwners(crmLeadDAO crmLeadDAOObj, String leadid) throws ServiceException{
        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("lo.leadid.leadid");
        filter_params.add(leadid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();

        kmsg = crmLeadDAOObj.getLeadOwners(requestParams, filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();
        LeadOwners leadOwnersObj;
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        while(ite.hasNext()){
            Object row[] = (Object[]) ite.next();
            leadOwnersObj=(LeadOwners)row[0];
            if(leadOwnersObj.isMainOwner()){
                mainLeadOwner=leadOwnersObj.getUsersByUserid();
            }else{
                ownerId+=leadOwnersObj.getUsersByUserid().getUserID()+",";
                ownerNames+=profileHandler.getUserFullName(leadOwnersObj.getUsersByUserid())+", ";
            }
        }
        String mainOwner = "";
        if(mainLeadOwner!=null)
            mainOwner=profileHandler.getUserFullName(mainLeadOwner);

        String tooltip;
        String gridName;
        if(!StringUtil.isNullOrEmpty(ownerNames)){
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerId = ownerId.substring(0,ownerId.length()-1);
            tooltip="<b>"+mainOwner+".</b>, "+ownerNames;
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+".</b>";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Owners'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }    

    /**
     * @param crmLead
     * @return
     * @throws ServiceException
     */
    public static String[] getAllLeadOwners(CrmLead crmLead) throws ServiceException{
        Set leadOwners  = crmLead.getLeadOwners();
        Iterator ite = leadOwners.iterator();
        LeadOwners leadOwnersObj;
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        while(ite.hasNext()){
            leadOwnersObj=(LeadOwners)ite.next();
            if(leadOwnersObj.isMainOwner()){
                mainLeadOwner=leadOwnersObj.getUsersByUserid();
            }else{
                ownerId+=leadOwnersObj.getUsersByUserid().getUserID()+",";
                ownerNames+=profileHandler.getUserFullName(leadOwnersObj.getUsersByUserid())+", ";
            }
        }
        String mainOwner = "";
        if(mainLeadOwner!=null)
            mainOwner=profileHandler.getUserFullName(mainLeadOwner);

        String tooltip;
        String gridName;
        if(!StringUtil.isNullOrEmpty(ownerNames)){
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerId = ownerId.substring(0,ownerId.length()-1);
            tooltip="<b>"+mainOwner+".</b>, "+ownerNames;
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+".</b>";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Owners'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }   
    
    public static JSONObject getLeadOwners(crmLeadDAO crmLeadDAOObj, HttpServletRequest request,String leadid) throws ServiceException, SessionExpiredException{
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject temp;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String[] ownerInfo = getAllLeadOwners(crmLeadDAOObj,leadid);
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
    public static String getMainLeadOwner(crmLeadDAO crmLeadDAOObj, String leadid) throws ServiceException{
        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("lo.leadid.leadid");
        filter_params.add(leadid);
        filter_names.add("lo.mainOwner");
        filter_params.add(true);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();

        kmsg = crmLeadDAOObj.getLeadOwners(requestParams, filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();

        LeadOwners leadOwnersObj;
        String ownerId="";

        while(ite.hasNext()){
            Object row[] = (Object[]) ite.next();
            leadOwnersObj=(LeadOwners)row[0];
            ownerId+=leadOwnersObj.getUsersByUserid().getUserID();
        }
        return ownerId;
    }

    public static String[] getLeadOwners(crmLeadDAO crmLeadDAOObj, String leadid) throws ServiceException{
        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("lo.leadid.leadid");
        filter_params.add(leadid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();

        kmsg = crmLeadDAOObj.getLeadOwners(requestParams, filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();

        LeadOwners leadOwnersObj;
        String ownerId="";
        String ownerNames="";

        while(ite.hasNext()){
            Object row[] = (Object[]) ite.next();
            leadOwnersObj=(LeadOwners)row[0];
            ownerId+=leadOwnersObj.getUsersByUserid().getUserID()+",";
            ownerNames+=leadOwnersObj.getUsersByUserid().getFirstName()+StringUtil.hNull(leadOwnersObj.getUsersByUserid().getLastName())+", ";
        }
        if(!StringUtil.isNullOrEmpty(ownerId)){
            ownerId = ownerId.substring(0,ownerId.length()-1);
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerNames ="<div wtf:qtip=\""+ownerNames+"\"wtf:qtitle='Owners'>"+StringUtil.abbreviate(ownerNames,27)+"</div>";
        }
        String[] ownerInfo = {ownerId,ownerNames};
        return ownerInfo;
    }
    
    /**
     * Deprecated use getLeadProducts(CrmLead crmLead) instead
     * @param crmLeadDAOObj
     * @param leadId
     * @return
     * @throws ServiceException
     */
    @Deprecated
    public static String[] getLeadProducts(crmLeadDAO crmLeadDAOObj, String leadId) throws ServiceException{

        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("lp.leadid.leadid");
        filter_params.add(leadId);
        filter_names.add("lp.productId.deleteflag");
        filter_params.add(0);
        filter_names.add("lp.productId.isarchive");
        filter_params.add(false);
        filter_names.add("lp.productId.validflag");
        filter_params.add(1);
        kmsg = crmLeadDAOObj.getLeadProducts(filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();
        LeadProducts leadProductsObj;
        String productId="";
        String productNames="";
        String exportproductNames="";
        while(ite.hasNext()){
            leadProductsObj=(LeadProducts)ite.next();
            productId+=leadProductsObj.getProductId().getProductid()+",";
            productNames+=leadProductsObj.getProductId().getProductname()+",";
        }
        if(!StringUtil.isNullOrEmpty(productId)){
            productId = productId.substring(0,productId.length()-1);
            productNames = productNames.substring(0,productNames.length()-1);
//            productNames+=".";
            exportproductNames=productNames;
            productNames ="<div wtf:qtip=\""+productNames+"\"wtf:qtitle='Products'>"+StringUtil.abbreviate(productNames,27)+"</div>";
        }
        String[] productInfo = {productId,productNames,exportproductNames};
        return productInfo;
    }
    
    public static String[] getLeadProducts(CrmLead crmLead) throws ServiceException{
        Set leadProducts = crmLead.getCrmProducts();
        Iterator ite = leadProducts.iterator();
        LeadProducts leadProductsObj;
        String productId="";
        String productNames="";
        String exportproductNames="";
        while(ite.hasNext()){
            leadProductsObj=(LeadProducts)ite.next();
            productId+=leadProductsObj.getProductId().getProductid()+",";
            productNames+=leadProductsObj.getProductId().getProductname()+",";
        }
        if(!StringUtil.isNullOrEmpty(productId)){
            productId = productId.substring(0,productId.length()-1);
            productNames = productNames.substring(0,productNames.length()-1);
//            productNames+=".";
            exportproductNames=productNames;
            productNames ="<div wtf:qtip=\""+productNames+"\"wtf:qtitle='Products'>"+StringUtil.abbreviate(productNames,27)+"</div>";
        }
        String[] productInfo = {productId,productNames,exportproductNames};
        return productInfo;
    }

    public static String[] getAllLeadOwners(List<LeadOwnerInfo> owners)
    {
        if (owners == null || owners.isEmpty())
        {
            return null;
        }
        
        String ownerNames = "";
        User mainLeadOwner = null;
        String ownerId = "";

        for (LeadOwnerInfo owner : owners)
        {
            LeadOwners leadOwnersObj = owner.getOwner();
            User user = owner.getUser();
            if (leadOwnersObj.isMainOwner())
            {
                mainLeadOwner = leadOwnersObj.getUsersByUserid();
            } else
            {
                ownerId += user.getUserID() + ",";
                ownerNames += profileHandler.getUserFullName(user) + ", ";
            }
        }
        String mainOwner = "";
        if (mainLeadOwner != null)
            mainOwner = profileHandler.getUserFullName(mainLeadOwner);

        String tooltip;
        String gridName;
        if (!StringUtil.isNullOrEmpty(ownerNames))
        {
            ownerNames = ownerNames.substring(0, ownerNames.length() - 2);
            ownerId = ownerId.substring(0, ownerId.length() - 1);
            tooltip = "<b>" + mainOwner + ".</b>, " + ownerNames;
            gridName = mainOwner + ", " + ownerNames;
        } else
        {
            tooltip = "<b>" + mainOwner + ".</b>";
            gridName = mainOwner;
        }

        String displayOwnerNames = "<div wtf:qtip=\"" + tooltip + "\"wtf:qtitle='Owners'>" + StringUtil.abbreviate(gridName, 27) + "</div>";
        String[] ownerInfo = { mainOwner, ownerNames, mainLeadOwner.getUserID(), ownerId, displayOwnerNames, gridName };
        return ownerInfo;
    }

    public static String[] getLeadProducts(List<CrmProduct> products)
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
    
    public static HashMap<String, DefaultMasterItem> getLeadsReleatedDefaultMasterData (String companyid, StringBuffer usersList, kwlCommonTablesDAO kwlCommonTablesDAOObj, crmCommonDAO crmCommonDAO) throws ServiceException, JSONException {
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        List filter_names = new ArrayList();
        List filter_params = new ArrayList();
        //Get ids of crmCombomaster table
        String masterIds = "'" + Constants.LEAD_SOURCEID + "',";
        masterIds += "'" + Constants.CAMPAIGN_SOURCEID + "',";
        masterIds += "'" + Constants.LEAD_STATUSID + "',";
        masterIds += "'" + Constants.LEAD_RATINGID + "',";
        masterIds += "'" + Constants.LEAD_INDUSTRYID + "'";
        filter_names.add("INc.crmCombomaster");
        filter_params.add(masterIds);
        filter_names.add("c.company.companyID");
        filter_params.add(companyid);
        requestParams.put(Constants.filter_names, filter_names);
        requestParams.put(Constants.filter_params, filter_params);

        // if leadsource then also fetch campaign source data
        CompanyPreferences cmpPref = (CompanyPreferences) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CompanyPreferences", companyid);
        JSONObject jsnObj = crmManagerCommon.getCompanyPreferencesJSON(cmpPref, new JSONObject());
        boolean heirarchyPerm = jsnObj.getBoolean(Constants.Crm_campaign_modulename);
        if (!heirarchyPerm) {
            requestParams.put("userlist_value", usersList);
        }
        requestParams.put("companyid", companyid);

        return crmCommonDAO.getDefaultMasterItemsMap(requestParams);
    }
}
