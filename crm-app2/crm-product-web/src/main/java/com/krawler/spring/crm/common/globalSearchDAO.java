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
package com.krawler.spring.crm.common;

import com.krawler.common.service.ServiceException;
import com.krawler.esp.Search.SearchBean;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Karthik
 */
public interface globalSearchDAO {

    public KwlReturnObject globalQuickSearch(HashMap<String, Object> requestParams) throws ServiceException;
 
    public KwlReturnObject searchIndex(SearchBean bean, String querytxt, String numhits, String perpage, String startIn,
			String companyid, String userid, DateFormat dateFmt) throws ServiceException, IOException, JSONException;
}
