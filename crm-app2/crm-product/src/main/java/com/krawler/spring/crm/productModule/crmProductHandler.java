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
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Header;
import com.krawler.common.util.StringUtil;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.common.utils.DateUtil;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class crmProductHandler {
    private static final Log logger = LogFactory.getLog(crmProductHandler.class);
    /**
     *
     * @param ll
     * @param request
     * @param export
     * @param totalSize
     * @param crmManagerDAOObj
     * @return JSONObject
     */
    public static JSONObject getProductReportJson(List<CrmProduct> ll, HttpServletRequest request, boolean export, int totalSize, crmManagerDAO crmManagerDAOObj,crmCommonDAO crmCommonDAOObj, DateFormat dateFormat) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
//            Iterator ite = ll.iterator();
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String currencySymbol=crmManagerDAOObj.currencySymbol(currencyid);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            for (CrmProduct crmProduct : ll)  {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("productid", crmProduct.getProductid());
                tmpObj.put("name", crmProduct.getProductname());
                tmpObj.put("ownerid", crmProduct.getUsersByUserid().getUserID());
                tmpObj.put("owner", crmProduct.getUsersByUserid().getFirstName() + " " + crmProduct.getUsersByUserid().getLastName());
                tmpObj.put("code", crmProduct.getCode());
                tmpObj.put("commisionrate", crmProduct.getCommisionrate());
                tmpObj.put("createdon",crmProduct.getCreatedOn()!= null?crmProduct.getCreatedOn():"");
                tmpObj.put("updatedon", crmProduct.getUpdatedOn() != null? crmProduct.getUpdatedOn():"");
                tmpObj.put("currencyid", crmProduct.getCurrencyid());
                tmpObj.put("description", crmProduct.getDescription());
                tmpObj.put("productname", crmProduct.getProductname());
                tmpObj.put("quantityindemand", crmProduct.getQuantityindemand());
                tmpObj.put("quantitylevel", crmProduct.getQuantitylevel());
                tmpObj.put("stockquantity", crmProduct.getStockquantity());
                tmpObj.put("taxincurred", crmProduct.getTaxincurred());
                tmpObj.put("threshold", crmProduct.getThreshold());
                tmpObj.put("unitprice", crmProduct.getUnitprice() != null && !crmProduct.getUnitprice().equals("") ? crmManagerDAOObj.currencyRender(crmProduct.getUnitprice(), currencyid) : "");
                tmpObj.put("categoryid", crmManagerCommon.comboNull(crmProduct.getCrmCombodataByCategoryid()));
                tmpObj.put("category", (crmProduct.getCrmCombodataByCategoryid() != null ? crmProduct.getCrmCombodataByCategoryid().getValue() : ""));
                tmpObj.put("vendornameid", crmProduct.getVendornamee());
                tmpObj.put("validflag", crmProduct.getValidflag());

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll2=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Product");
                Iterator ite2 = ll2.iterator();
                while (ite2.hasNext()) {
                    DefaultHeader obj1 = (DefaultHeader) ite2.next();
                    String newHeader2 =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj1.getDefaultHeader(),"Product",companyid);
                    if(StringUtil.equal(Header.PRODUCTNAMEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)?"Product Name":newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)?"Product Name":newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("dataIndex", "name");
                        jobjTemp.put("xtype", "textfield");
                        jarrColumns.put(jobjTemp);
                    }if(StringUtil.equal(Header.PRODUCTOWNERHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)?"Product Owner":newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)?"Product Owner":newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "owner");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.PRODUCTUNITPRICEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)?"Unit Price"+"("+currencySymbol+")":newHeader2+"("+currencySymbol+")");
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)?"Unit Price"+"("+currencySymbol+")":newHeader2+"("+currencySymbol+")");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "unitprice");
                        jobjTemp.put("align", "right");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.PRODUCTCATEGORYHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)?Header.PRODUCTCATEGORYHEADER:newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)?Header.PRODUCTCATEGORYHEADER:newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "combo");
                        jobjTemp.put("dataIndex", "category");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.PRODUCTVENDORNAMEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)?Header.PRODUCTVENDORNAMEHEADER:newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)?Header.PRODUCTVENDORNAMEHEADER:newHeader2);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "vendornameid");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.PRODUCTCREATIONDATEHEADER, obj1.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader2)?Header.PRODUCTCREATIONDATEHEADER:newHeader2);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader2)?Header.PRODUCTCREATIONDATEHEADER:newHeader2);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("editor", "new Wtf.form.DateField({format:WtfGlobal.getOnlyDateFormat()})");
                        jobjTemp.put("renderer", "WtfGlobal.onlyDateRendererTZ");
                        jarrColumns.put(jobjTemp);
                   }
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "owner");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "name");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "unitprice");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "category");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "vendornameid");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdon");
                jobjTemp.put("dateFormat", "time");
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
            logger.warn(e.getMessage(),e);
        }
        return commData;
    }

    public static HashMap<String, DefaultMasterItem> getProductDefaultMasterItemsMap(String companyid, crmCommonDAO crmCommonDAOObj) throws ServiceException {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            List filter_names = new ArrayList();
            List filter_params = new ArrayList();
            //Get ids of crmCombomaster table
            String masterIds = "'"+Constants.PRODUCT_CATEGORYID+"'";
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(Constants.filter_names, filter_names);
            requestParams.put(Constants.filter_params, filter_params);
            return crmCommonDAOObj.getDefaultMasterItemsMap(requestParams);
    }
}
