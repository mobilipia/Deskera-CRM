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
package com.krawler.spring.crm.activityModule;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface crmActivityDAO {
    
    /**
     * @param requestParams
     * @param usersList
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAccountActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;

    /**
     * @param requestParams
     * @param usersList
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getLeadActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    
    /**
     * @param requestParams
     * @param usersList
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getCampaignActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    
    /**
     * @param requestParams
     * @param usersList
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getOpportunityActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    
    /**
     * @param requestParams
     * @param usersList
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getContactActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    
    /**
     * @param requestParams
     * @param usersList
     * @param filter_names
     * @param filter_params
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getCaseActivity(HashMap<String, Object> requestParams, StringBuffer usersList, ArrayList filter_names, ArrayList filter_params) throws ServiceException ;
    
    /**
     * @param jobj
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject addActivity(JSONObject jobj) throws ServiceException ;
    
    /**
     * @param jobj
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject editActivity(JSONObject jobj) throws ServiceException ;
    
    /**
     * @param contactid
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getActiveActivity(String contactid) throws ServiceException;
    
    /**
     * @param queryParams
     * @param allflag
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAccountActivityForTable(HashMap<String, Object> queryParams, boolean allflag) throws ServiceException;
    
    /**
     * @param queryParams
     * @param allflag
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getLeadActivityForTable(HashMap<String, Object> queryParams, boolean allflag) throws ServiceException;
    
    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getDetailPanelRecentActivity(HashMap<String, Object> requestParams) throws ServiceException;
    KwlReturnObject getDetailPanelTopActivity(long todayLong, long uptoLong, StringBuffer userList) throws ServiceException;
    /**
     * returns the ID of related module such as ( Lead,Contact,Account ) of AuditTrailDetail Id
     * @param recordId
     * @param moduleId
     * @return
     */
    String getPrimaryKey(String recordId,int moduleId);

    List<CrmActivityMaster> getActivities(List<String> recordIds);

    KwlReturnObject getOverdueactivities();

    public KwlReturnObject updateMassActivity(JSONObject jobj) throws ServiceException;
}
