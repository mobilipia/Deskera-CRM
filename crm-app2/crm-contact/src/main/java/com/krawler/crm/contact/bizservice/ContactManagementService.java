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

package com.krawler.crm.contact.bizservice;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.contact.dm.ContactOwnerInfo;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author sm
 */
public interface ContactManagementService {
    JSONObject saveContact(String companyid, String userId, String timeZoneDiff, String ipAddress, JSONObject jsonData)  throws ServiceException, JSONException;

    JSONObject getContacts(String companyid, String userid, String currencyid, String selectExportJson, String LeadAccountFlag,
            String isarchive, String LeadAccountName, String mapid, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xtype, String xfield,
            String start, String limit, DateFormat dateFormat, StringBuffer usersList) throws ServiceException;

    JSONObject ContactExport(String companyid, String userid, String currencyid, String selectExportJson, String LeadAccountFlag,
            String isarchive, String LeadAccountName, String mapid, String searchJson, String ss, String config, String isExport,
            boolean heirarchyPerm, String field, String direction, DateFormat dateFormat, StringBuffer usersList) throws ServiceException;

    JSONObject getContactJsonObject(CrmContact obj, JSONObject tmpObj, String companyid, String currencyid, 
            HashMap<String, Integer> FieldMap,boolean isexport, DateFormat dateFormat, Map<String, List<ContactOwnerInfo>> owners);

	JSONObject updateMassContact(String companyid, String userid, String timeZoneDiff, String ipAddress, JSONObject jobj) throws ServiceException, JSONException;
    JSONObject saveLogins(User creator, String loginurl,String companyId, String contactId, String emailId,Boolean setActive, String partnerName) throws ServiceException, JSONException;
    void custPassword_Change(String newpass,String customerid)throws ServiceException;
    boolean verifyCurrentPass(String curpass, String customerid) throws ServiceException;
  }

