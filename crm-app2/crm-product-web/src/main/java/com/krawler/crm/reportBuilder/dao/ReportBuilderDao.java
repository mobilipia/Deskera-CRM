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

package com.krawler.crm.reportBuilder.dao;

import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CustomReportList;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Sagar A
 */
public interface ReportBuilderDao {
    KwlReturnObject getCustomReportConfig(HashMap<String, Object> requestParams) throws ServiceException;
    KwlReturnObject getModules() throws ServiceException;
    KwlReturnObject getRendererFunctions() throws ServiceException;
    KwlReturnObject saveReportDesc(Map<String,Object> requestParamsas) throws ServiceException;
    int deleteReportColumnConfig(int rNo) throws ServiceException;
    KwlReturnObject saveReportColumnConfig(String reportcolumnsetting, CustomReportList CustomReportListObj, String userid) throws ServiceException;
    boolean deleteCustomReport(int reportno) throws ServiceException;
    KwlReturnObject getDataCustomReport(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException;
}
