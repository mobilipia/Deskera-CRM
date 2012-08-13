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
package com.krawler.calendar.calendarmodule;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractView;

import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;

public class CalendarView extends AbstractView {
	private JSONMapper mapper;
	private String root;
	public CalendarView(JSONMapper mapper, String root) {
		super();
		this.mapper = mapper;
		this.root = root;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List list = (List)model.get(root);
		JSONArray jArr = new JSONArray();
		if(list!=null){
			for(Object obj:list){
				jArr.put(mapper.mapJSON(obj));
			}
		}
			
		ByteArrayOutputStream baos = createTemporaryOutputStream();
		baos.write(new JSONObject().put("valid", true).put("success", true).put("data", new JSONObject().put(root, jArr)).toString().getBytes());
		writeToResponse(response, baos);
	}

	@Override
	public String getContentType() {
		return "text/html; charset=UTF-8";
	}
}
