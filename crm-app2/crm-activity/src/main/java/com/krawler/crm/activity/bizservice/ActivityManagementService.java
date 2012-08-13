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
package com.krawler.crm.activity.bizservice;

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;

/**
 * @author Ashutosh
 *
 */
public interface ActivityManagementService
{
    /**
     * @param companyid
     * @param userId
     * @param timeFormatId
     * @param timeZoneDiff
     * @param ipAddress
     * @param jsonData
     * @return
     */
    JSONObject saveActivity(String companyid, String userId, String timeFormatId, String timeZoneDiff, String ipAddress, JSONObject jsonData, boolean notifyFlag, String loginURL, String partnerName)  throws ServiceException, JSONException, ParseException, SessionExpiredException;

    /**
     *
     * @param companyid
     * @param userid
     * @param currencyid
     * @param selectExportJson
     * @param isArchive
     * @param searchJson
     * @param ss
     * @param config
     * @param isExport
     * @param status
     * @param heirarchyPerm
     * @param field
     * @param direction
     * @param iscustomcolumn
     * @param xfield
     * @param xtype
     * @param timeZoneDiff
     * @param timeFormat
     * @param module
     * @param mapid
     * @param dateFormat
     * @param start
     * @param limit
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    public JSONObject getActivity(String companyid, String userid, String currencyid, String selectExportJson,
            boolean isArchive, String searchJson, String ss, String config, String isExport, String status,
            boolean heirarchyPerm, String field, String direction, String iscustomcolumn, String xfield, String xtype,
            String timeZoneDiff, String timeFormat, String module, String mapid, DateFormat dateFormat, String start, String limit, StringBuffer userList) throws ServiceException;

    /**
     *
     * @param activityid
     * @param moduleId
     * @return
     */
    JSONObject getActivityRelatedJson(String activityid, int moduleId);
}
