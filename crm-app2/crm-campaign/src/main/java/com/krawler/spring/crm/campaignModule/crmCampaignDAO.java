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
package com.krawler.spring.crm.campaignModule;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.List;
public interface crmCampaignDAO {
    public KwlReturnObject addCampaigns(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject editCampaigns(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject getCampaigns(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException ;
    public KwlReturnObject getActiveCampaign(String campaignid) throws ServiceException;
    public KwlReturnObject getCampaignsForTable(HashMap<String, Object> queryParams, boolean allFlag) throws ServiceException ;
    public void updateDefaultMasterItemForCampaign(CrmCampaign crmCampaign ) throws ServiceException ;
    public KwlReturnObject getCampaignLog(HashMap<String, Object> requestParams);
    public KwlReturnObject getDetailPanelRecentCampaign(HashMap<String, Object> requestParams) throws ServiceException;
    public CrmCampaign getCampaignById(String id);
    
    List<CrmCampaign> getCampaigns(List<String> recordIds);

    public KwlReturnObject updateMassCampaigns(JSONObject jobj) throws ServiceException;
}
