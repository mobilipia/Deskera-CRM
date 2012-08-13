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
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.CrmProductCustomData;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface crmProductDAO {

    public Map<String, User> getProductOwners(List<String> idsList);
    /**
     *
     * @param requestParams
     * @param usersList
     * @return KwlReturnObject
     * @throws com.krawler.common.service.ServiceException
     */
    public KwlReturnObject getProducts(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException ;

    /**
     *
     * @param requestParams
     * @return KwlReturnObject
     * @throws com.krawler.common.service.ServiceException
     */
    public KwlReturnObject getAllProducts(HashMap<String, Object> requestParams) throws ServiceException ;

    /**
     *
     * @param jobj
     * @return KwlReturnObject
     * @throws com.krawler.common.service.ServiceException
     */
    public KwlReturnObject addProducts(JSONObject jobj) throws ServiceException ;

    /**
     *
     * @param jobj
     * @return KwlReturnObject
     * @throws com.krawler.common.service.ServiceException
     */
    public KwlReturnObject editProducts(JSONObject jobj) throws ServiceException ;

    public KwlReturnObject getCrmProductCustomData(HashMap<String, Object> requestParams) throws ServiceException;

    List<CrmProduct> getProducts(List<String> recordIds);

    public HashMap<String, CrmProductCustomData> getProductCustomDataMap(List<String> list, String companyid) throws ServiceException;

    public KwlReturnObject updateMassProducts(JSONObject jobj) throws ServiceException;
    
    void setCustomData(CrmProduct crmProd, CrmProductCustomData crmProdCustomData);
    void setCustomData(CrmProduct crmProd, JSONArray cstmData);
    
}
