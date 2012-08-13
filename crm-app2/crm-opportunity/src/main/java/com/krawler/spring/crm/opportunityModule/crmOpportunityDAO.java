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
import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CrmOpportunity;
import com.krawler.crm.database.tables.CrmOpportunityCustomData;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.opportunity.dm.OpportunityOwnerInfo;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface crmOpportunityDAO {
    public KwlReturnObject addOpportunities(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject editOpportunities(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject getOpportunities(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject getOpportunities(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    public KwlReturnObject getAllOpportunities(HashMap<String, Object> requestParams) throws ServiceException;
     public void setMainOppOwner(String[] oppids,String ownerid) throws ServiceException;
    public KwlReturnObject saveOppOwners(HashMap<String, Object> requestParams) throws Exception ;
    public double getOpportunityRevenue( ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    public KwlReturnObject getOpportunityOwners(ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    public KwlReturnObject getOpportunityProducts(ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    public KwlReturnObject getCrmOpportunityCustomData(HashMap<String, Object> requestParams) throws ServiceException;
    
    List<CrmOpportunity> getOpportunities(List<String> recordIds);
    public HashMap<String, CrmOpportunityCustomData> getOpportunityCustomDataMap(List<String> list, String companyid) throws ServiceException;
    public void saveOpportunityProducts(String[] oppids,String[] products) throws ServiceException;
    public void setCustomData(CrmOpportunity crmOpp, CrmOpportunityCustomData crmOppCustomData);
    public void setCustomData(CrmOpportunity crmOpp, JSONArray cstmData);
    public Map<String, List<OpportunityOwnerInfo>> getOpportunityOwners(List<String> oppIds);
    public Map<String, List<CrmProduct>> getOpportunityProducts(List<String> oppIds);
    public KwlReturnObject updateMassOpportunities(JSONObject jobj) throws ServiceException;
	List getOwnerChangedOpportunities(String newOwnerId, String[] oppIds) throws ServiceException;
}
