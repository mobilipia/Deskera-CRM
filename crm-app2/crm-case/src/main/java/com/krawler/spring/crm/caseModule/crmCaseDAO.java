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

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmCaseCustomData;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface crmCaseDAO
{
    /**
     * @param jobj
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject addCases(JSONObject jobj) throws ServiceException;

    /**
     * @param jobj
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject editCases(JSONObject jobj) throws ServiceException;

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getCases(HashMap<String, Object> requestParams) throws ServiceException;

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAllCases(HashMap<String, Object> requestParams) throws ServiceException;

    /**
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getCaseProducts(ArrayList filter_names, ArrayList filter_params) throws ServiceException;

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getCrmCaseCustomData(HashMap<String, Object> requestParams) throws ServiceException;

    List<CrmCase> getCases(List<String> recordIds);

    /**
     * 
     * @param list
     * @param companyid
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    public HashMap<String, CrmCaseCustomData> getCaseCustomDataMap(List<String> list, String companyid) throws ServiceException;
    
    Map<String, List<CrmProduct>> getCaseProducts(List<String> caseIds);
    Map<String, CrmAccount> getCaseAccounts(List<String> caseIds);
    Map<String, CrmContact> getCaseContacts(List<String> caseIds);
    Map<String, User> getCaseOwners(List<String> caseIds);
    public KwlReturnObject updateMassCases(JSONObject jobj) throws ServiceException;
    List<Object[]> getCaseSLA(int cronDuration) throws ServiceException;
    List<String> getCaseCustomUserID(HashMap<String, Object> requestParams) throws ServiceException;
    public String getCompanyCaseDefaultOwnerID(String companyId) throws ServiceException;

	void saveCustomerDocs(String customerId, String docId, String caseId) throws ServiceException;
}
