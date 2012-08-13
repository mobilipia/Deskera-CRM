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

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CaseProducts;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.common.crmCommonDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class crmCaseHandler {
    public static String[] getCaseProducts(crmCaseDAO crmCaseDAOObj, String caseId) throws ServiceException{
        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("cp.caseid.caseid");
        filter_params.add(caseId);
        filter_names.add("cp.productId.deleteflag");
        filter_params.add(0);
        filter_names.add("cp.productId.validflag");
        filter_params.add(1);
        kmsg = crmCaseDAOObj.getCaseProducts(filter_names, filter_params);
        Iterator ite = kmsg.getEntityList().iterator();
        CaseProducts leadProductsObj;
        String productId="";
        String productNames="";
        String exportproductNames="";
        while(ite.hasNext()){
            leadProductsObj=(CaseProducts)ite.next();
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

    public static String[] getCaseProducts(List<CrmProduct> products)
    {
        String productId = "";
        String productNames = "";
        String exportproductNames = "";
        if(products!=null){
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
        }
        String[] productInfo = { productId, productNames, exportproductNames };
        return productInfo;
    }

    public static HashMap<String, DefaultMasterItem> getCasesDefaultMasterItemsMap(String companyid, crmCommonDAO crmCommonDAOObj) throws ServiceException {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            List filter_names = new ArrayList();
            List filter_params = new ArrayList();
            //Get ids of crmCombomaster table
            String masterIds = "'"+Constants.CASE_PRIORITYID+"',";
            masterIds += "'"+Constants.CASE_STATUSID+"',";
            masterIds += "'"+Constants.CASE_TYPEID+"'";
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(Constants.filter_names, filter_names);
            requestParams.put(Constants.filter_params, filter_params);
            return crmCommonDAOObj.getDefaultMasterItemsMap(requestParams);
    }
}
