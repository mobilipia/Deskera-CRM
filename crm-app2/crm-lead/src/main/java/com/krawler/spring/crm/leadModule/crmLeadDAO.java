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
import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.CrmLeadCustomData;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.lead.dm.LeadOwnerInfo;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface crmLeadDAO {
    public KwlReturnObject getLeads(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException ;
    public KwlReturnObject getLeads(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject getAllLeads(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    public KwlReturnObject addLeads(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject editLeads(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject getLeadOwners(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    public KwlReturnObject saveLeadOwners(HashMap<String, Object> requestParams) throws Exception ;
    public void setMainLeadOwner(String[] leadids,String ownerid) throws ServiceException;
    public KwlReturnObject getLeadProducts(ArrayList filter_names, ArrayList filter_params) throws ServiceException;
    public KwlReturnObject getCrmLeadCustomData(HashMap<String, Object> requestParams) throws ServiceException;
    public void saveLeadProducts(String[] leadids,String[] productIds) throws ServiceException;
    CrmLead getLead(String leadid) throws ServiceException ;
    public void setCustomData(CrmLead crmLead, CrmLeadCustomData crmLeadCustomData);
    
    List<CrmLead> getLeads(List<String> recordIds) throws ServiceException;
    
    public void setCustomData(CrmLead crmLead, JSONArray cstmData);

    public HashMap<String, CrmLeadCustomData> getLeadCustomDataMap(List<String> recordIds, String companyid) throws ServiceException;
    
    Map<String, List<LeadOwnerInfo>> getLeadOwners(List<String> leadList);
    
    Map<String, List<CrmProduct>> getLeadProducts(List<String> leadList);
	public KwlReturnObject updateMassLeads(JSONObject jobj) throws ServiceException;
    public boolean checkWebLeadAssignedOwner(String leadid) throws ServiceException;
    public String confirmWebLeadOwner(String leadid, String ownerid) throws ServiceException;
}
