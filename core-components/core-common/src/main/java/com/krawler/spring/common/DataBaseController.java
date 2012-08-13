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
package com.krawler.spring.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.krawler.esp.utils.CompanyRoutingDataSource;

public class DataBaseController extends MultiActionController {
	private CompanyRoutingDataSource routingDataSource;

	public void setRoutingDataSource(CompanyRoutingDataSource routingDataSource) {
		this.routingDataSource = routingDataSource;
	}
	
	public ModelAndView updateCompanyLookup(HttpServletRequest request, HttpServletResponse response) {
		routingDataSource.updateLookup();
                routingDataSource.updateLookupForDefaultDB();
		return new ModelAndView("jsonView-ex", "model", "{\"success\":\"true\"}");
	}
	
	
}
