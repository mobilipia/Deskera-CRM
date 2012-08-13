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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.krawler.common.service.ServiceException;
import com.krawler.crm.account.dm.AccountOwnerInfo;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmAccountCustomData;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;

public interface crmAccountDAO {
	
    /**
     * Gets all accounts for the provided users
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAccounts(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException ;
    
    /**
     * @param requestParams
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAllAccounts(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    
    /**
     * @param jobj
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject addAccounts(JSONObject jobj) throws ServiceException ;
    /**
     * @param jobj
     * @return
     * @throws ServiceException
     */
    
    public KwlReturnObject editAccounts(JSONObject jobj) throws ServiceException ;
    
    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAccounts(HashMap<String, Object> requestParams) throws ServiceException ;
    
    /**
     * @param requestParams
     * @return
     * @throws Exception
     */
    public KwlReturnObject saveAccOwners(HashMap<String, Object> requestParams) throws Exception;
    
    /**
     * @param requestParams
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAccountOwners(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException;
   
    /**
     * @param requestParams
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public double getAccountRevenue(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException;
    
    /**
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAccountProducts(ArrayList filter_names, ArrayList filter_params) throws ServiceException;
    
    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getCrmAccountCustomData(HashMap<String, Object> requestParams) throws ServiceException;

    public void setMainAccOwner(String[] accountid, String ownerid) throws ServiceException;

    public void saveAccountProducts(String[] accountid, String[] productid) throws ServiceException;

    public void setCustomData(CrmAccount crmAccount, CrmAccountCustomData crmAccountCustomData);

    public KwlReturnObject updateMassAccount(JSONObject jobj) throws ServiceException;

    List<CrmAccount> getAccounts(List<String> recordIds);
    
    public void setCustomData(CrmAccount crmAcc, JSONArray cstmData);

    HashMap<String, CrmAccountCustomData> getAccountCustomDataMap(List<String> list, String companyid) throws ServiceException;

    Map<String, List<AccountOwnerInfo>> getAccountOwners(List<String> accountIds);

    Map<String, List<CrmProduct>> getAccountProducts(List<String> accountIds);

	List getOwnerChangedAccounts(String newOwnerId, String[] accountIds) throws ServiceException;
}
