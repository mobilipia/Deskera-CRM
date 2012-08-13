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
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.OppurtunityProducts;
import com.krawler.crm.database.tables.opportunityOwners;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.opportunity.dm.OpportunityOwnerInfo;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.profileHandler.profileHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;

public class crmOpportunityHandler {
    private static final Log logger = LogFactory.getLog(crmOpportunityHandler.class);
    public static String[] getAllOppOwners(crmOpportunityDAO crmOpportunityDAOObj,String oppid) throws ServiceException{

        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("c.opportunity.oppid");
        filter_params.add(oppid);

        kmsg = crmOpportunityDAOObj.getOpportunityOwners(filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();
        opportunityOwners oppOwnersObj;
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        while(ite.hasNext()){
            oppOwnersObj=(opportunityOwners)ite.next();
            if(oppOwnersObj.isMainOwner()){
                mainLeadOwner=oppOwnersObj.getUsersByUserid();
            }else{
                ownerId+=oppOwnersObj.getUsersByUserid().getUserID()+",";
                ownerNames+=profileHandler.getUserFullName(oppOwnersObj.getUsersByUserid())+", ";
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
            tooltip="<b>"+mainOwner+"</b>, "+ownerNames+".";
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+"</b>.";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Opportunities'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }

    public static String[] getAllOppOwners(CrmOpportunity oppObj) throws ServiceException{
        Iterator ite = oppObj.getOppOwners().iterator();
        opportunityOwners oppOwnersObj;
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        while(ite.hasNext()){
            oppOwnersObj=(opportunityOwners)ite.next();
            if(oppOwnersObj.isMainOwner()){
                mainLeadOwner=oppOwnersObj.getUsersByUserid();
            }else{
                ownerId+=oppOwnersObj.getUsersByUserid().getUserID()+",";
                ownerNames+=profileHandler.getUserFullName(oppOwnersObj.getUsersByUserid())+", ";
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
            tooltip="<b>"+mainOwner+"</b>, "+ownerNames+".";
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+"</b>.";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Opportunities'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }

    public static String[] getAllOppOwners(List<OpportunityOwnerInfo> owners) throws ServiceException
    {
        if (owners == null)
        {
            return new String[]{};
        }
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        for (OpportunityOwnerInfo owner : owners)
        {
            opportunityOwners oppOwnersObj = owner.getOwner();
            if (oppOwnersObj.isMainOwner())
            {
                mainLeadOwner = owner.getUser();
            } else
            {
                ownerId += owner.getUser().getUserID() + ",";
                ownerNames += StringUtil.getFullName(owner.getUser()) + ", ";
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
            tooltip="<b>"+mainOwner+"</b>, "+ownerNames+".";
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+"</b>.";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Opportunities'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }

    public static JSONObject getOppOwners(crmOpportunityDAO crmOpportunityDAOObj,HttpServletRequest request,String oppid) throws ServiceException, SessionExpiredException{
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject temp;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String[] ownerInfo = getAllOppOwners(crmOpportunityDAOObj,oppid);
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
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }
    public static String getMainOppOwner(crmOpportunityDAO crmOpportunityDAOObj,String oppid) throws ServiceException{
        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("c.opportunity.oppid");
        filter_params.add(oppid);
        filter_names.add("c.mainOwner");
        filter_params.add(true);

        kmsg = crmOpportunityDAOObj.getOpportunityOwners(filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();
        opportunityOwners oppOwnersObj;
        String ownerId="";

        while(ite.hasNext()){
            oppOwnersObj=(opportunityOwners)ite.next();
            ownerId+=oppOwnersObj.getUsersByUserid().getUserID();
        }
        return ownerId;
    }
    public static String[] getOppOwners(crmOpportunityDAO crmOpportunityDAOObj,String oppid) throws ServiceException{
        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("c.opportunity.oppid");
        filter_params.add(oppid);

        kmsg = crmOpportunityDAOObj.getOpportunityOwners(filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();
        opportunityOwners oppOwnersObj;
        String ownerId="";
        String ownerNames="";

        while(ite.hasNext()){
            oppOwnersObj=(opportunityOwners)ite.next();
            ownerId+=oppOwnersObj.getUsersByUserid().getUserID()+",";
            ownerNames+=oppOwnersObj.getUsersByUserid().getFirstName()+StringUtil.hNull(oppOwnersObj.getUsersByUserid().getLastName())+", ";
        }
        if(!StringUtil.isNullOrEmpty(ownerId)){
            ownerId = ownerId.substring(0,ownerId.length()-1);
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerNames ="<div wtf:qtip=\""+ownerNames+"\"wtf:qtitle='Owners'>"+StringUtil.abbreviate(ownerNames,27)+"</div>";
        }
        String[] ownerInfo = {ownerId,ownerNames};
        return ownerInfo;
    }

    public static String[] getOpportunityProducts(CrmOpportunity oppObj) throws ServiceException{
        Iterator ite = oppObj.getCrmProducts().iterator();

        OppurtunityProducts oppurtunityProductsObj;
        String productId="";
        String productNames="";
        String exportproductNames="";

        while(ite.hasNext()){
            oppurtunityProductsObj=(OppurtunityProducts)ite.next();
            productId+=oppurtunityProductsObj.getProductId().getProductid()+",";
            productNames+=oppurtunityProductsObj.getProductId().getProductname()+",";
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
    
    public static List<CrmOpportunity> getOwnerChangedOpportunities(crmOpportunityDAO crmOpportunityDAO,String[] oppIds,String newOwnerId)throws ServiceException{
    	return crmOpportunityDAO.getOwnerChangedOpportunities(newOwnerId, oppIds);
    } 

    public static String[] getOpportunityProducts(List<CrmProduct> products)
    {
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

    public static HashMap<String, DefaultMasterItem> getOpportunityDefaultMasterItemsMap(String companyid, crmCommonDAO crmCommonDAOObj, kwlCommonTablesDAO kwlCommonTablesDAOObj, StringBuffer usersList) throws ServiceException, JSONException {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            List filter_names = new ArrayList();
            List filter_params = new ArrayList();
            //Get ids of crmCombomaster table
            String masterIds = "'"+Constants.LEAD_SOURCEID+"',";
            masterIds += "'"+Constants.OPP_TYPEID+"',";
            masterIds += "'"+Constants.OPP_REGIONID+"',";
            masterIds += "'"+Constants.OPP_STAGEID+"'";
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(Constants.filter_names, filter_names);
            requestParams.put(Constants.filter_params, filter_params);

            // if leadsource then fetch campaign source data also
            CompanyPreferences cmpPref = (CompanyPreferences) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CompanyPreferences", companyid);
            JSONObject jsnObj = crmManagerCommon.getCompanyPreferencesJSON(cmpPref, new JSONObject());
            boolean heirarchyPerm = jsnObj.getBoolean(Constants.Crm_campaign_modulename);
            if(!heirarchyPerm) {
                requestParams.put("userlist_value", usersList);
            }
            requestParams.put("companyid", companyid);
            return crmCommonDAOObj.getDefaultMasterItemsMap(requestParams);
    }
}
