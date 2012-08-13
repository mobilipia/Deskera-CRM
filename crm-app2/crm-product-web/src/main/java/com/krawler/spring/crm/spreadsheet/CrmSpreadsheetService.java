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
package com.krawler.spring.crm.spreadsheet;

import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public interface CrmSpreadsheetService {
    JSONObject getSpreadsheetConfig(HashMap<String, Integer> reportsPermMap, String module, String userId, String companyid,Locale locale) throws ServiceException;
    HashMap<String, Integer> getReportsPermission(HttpServletRequest request, String module) throws ServiceException, SessionExpiredException;
}
