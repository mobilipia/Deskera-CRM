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
package com.krawler.calendar.eventmodule;

import java.text.DateFormat;
import java.util.Date;

import com.krawler.calendar.calendarmodule.JSONMapper;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class EventJSONMapper implements JSONMapper<CalendarEvent> {
	DateFormat dateFormat;
	
	
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}


	@Override
	public JSONObject mapJSON(CalendarEvent e) throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("resources", e.getResources());
		obj.put("flagEvent", 0);
		obj.put("endts", formatDate(e.getEndTime()));
		obj.put("descr", e.getDescription());
		obj.put("location", e.getLocation());
		obj.put("subject", e.getSubject());
		obj.put("showas", e.getShowAs());
		obj.put("eid", e.getId());
		obj.put("cid", e.getCalendarId());
		obj.put("startts", formatDate(e.getStartTime()));
		obj.put("timestamp", formatDate(e.getCreatedOn()));
		obj.put("priority", e.getPriority());
		obj.put("recpattern", e.getRepeatPattern());
		obj.put("allday", e.isAllDay());
		obj.put("peid", e.getId());
		obj.put("recend", formatDate(e.getRepeatTill()));
		obj.put("deleteflag", e.isDenyDelete()?1:0);
		return obj;
	}
	
	private String formatDate(Date date){
		return date==null?null:(dateFormat==null?date.toString():dateFormat.format(date));
	}
}
