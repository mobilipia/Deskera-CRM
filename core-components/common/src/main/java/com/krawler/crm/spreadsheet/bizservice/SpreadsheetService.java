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
package com.krawler.crm.spreadsheet.bizservice;

import com.krawler.common.admin.SpreadSheetConfig;
import com.krawler.common.service.ServiceException;
import com.krawler.utils.json.base.JSONObject;

/**
 * @author Ashutosh
 *
 */
public interface SpreadsheetService
{
    /**
     * @param module
     * @param userId
     * @return
     * @throws ServiceException
     */
    SpreadSheetConfig getSpreadsheetConfig(String module, String userId) throws ServiceException;

    /**
     * @param jobj
     * @return
     * @throws ServiceException
     */
    SpreadSheetConfig saveSpreadsheetConfig(String module, String cid, String userId, String rule, String state) throws ServiceException;

	void saveModuleRecordStyle(String id, String classname, String cellstyle) throws ServiceException;
}
