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
import com.krawler.common.service.ServiceException;
import com.krawler.crm.contact.dm.ContactOwnerInfo;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmContactCustomData;
import com.krawler.crm.database.tables.CrmCustomer;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface crmContactDAO {
    /**
     * @param jobj
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject addContacts(JSONObject jobj) throws ServiceException ;
    /**
     * @param jobj
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject editContacts(JSONObject jobj) throws ServiceException ;
    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getContacts(HashMap<String, Object> requestParams) throws ServiceException;
    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject loginMail(JSONObject jobj)throws ServiceException;
    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAllContacts(HashMap<String, Object> requestParams) throws ServiceException ;
    /**
     * @param requestParams
     * @param usersList
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getContacts(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    /**
     * @param requestParams
     * @return
     * @throws Exception
     */
    public KwlReturnObject saveContactOwners(HashMap<String, Object> requestParams) throws Exception;
    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getContactOwners(HashMap<String, Object> requestParams) throws ServiceException ;
    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getCrmContactCustomData(HashMap<String, Object> requestParams) throws ServiceException;

    /**
     * 
     * @param newpassword
     * @param customerid
     */
    void custPassword_Change(String newpassword, String customerid);
    
    public void setMainContactOwner(String[] contactid, String ownerid) throws ServiceException;

    public void setCustomData(CrmContact crmContact, CrmContactCustomData crmContactCustomData);
    
    List<CrmContact> getContacts(List<String> recordIds);
    
    public void setCustomData(CrmContact crmContact, JSONArray cstmData);

    public HashMap<String, CrmContactCustomData> getContactCustomDataMap(List<String> list, String companyid) throws ServiceException;

    public Map<String, List<ContactOwnerInfo>> getContactOwners(List<String> contactIds);
	public KwlReturnObject updateMassContacts(JSONObject jsonData) throws ServiceException;
	public  void activate_deactivateLogin(String contactid, boolean active)throws ServiceException;
	int getLoginState(String contactid);
    public Map<String, Integer> getLoginState(List<String> contactIds);
	CrmCustomer getCustomer(String customerid);
	boolean isEmailIdExist(String emailId,String companyid);
    public Map<String, CrmAccount> getContactAccount(List<String> contactIds);
}
