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
package com.krawler.spring.calendar.eventmodule.bizservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.krawler.calendar.eventmodule.CalendarEvent;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.spring.crm.activityModule.crmActivityDAO;

public class CampaignEventService implements ModuleEventService {
	private crmActivityDAO activityDao;
	
	public void setActivityDao(crmActivityDAO activityDao) {
		this.activityDao = activityDao;
	}
	
	@Override
	public List<CalendarEvent> getEvents(StringBuffer userList,HashMap<String,Object> params) {
        ArrayList filter_names = new ArrayList((List<String>) Arrays.asList("c.crmCampaign.deleteflag", "c.crmCampaign.validflag","c.crmActivityMaster.deleteflag", "c.crmActivityMaster.validflag"));
        ArrayList filter_params = new ArrayList((List) Arrays.asList(0, 1, 0, 1));
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        if(params!=null){
        	if(params.containsKey("from") && params.get("from")!=null && params.containsKey("to") && params.get("to")!=null){
        		requestParams.put("from", ((Long)params.get("from")));
        		requestParams.put("to", ((Long)params.get("to")));
        	}
        }
        requestParams.put("xfield", "startDate ASC, c.crmActivityMaster.endDate - c.crmActivityMaster.startDate");
        requestParams.put("direction", "DESC");
        requestParams.put("field", "startdate");
        requestParams.put("calflag", true);
        
		List<Object[]> rows= null;
		try {
			rows = (List <Object[]>)(activityDao.getCampaignActivity(requestParams, userList, filter_names, filter_params).getEntityList());
		} catch (ServiceException e) {}
		List<CalendarEvent> events = new ArrayList<CalendarEvent>();
		if(rows!=null){
			for(Object[] row:rows){
				events.add(prepareEvent((CrmCampaign)row[1], (CrmActivityMaster)row[0]));
			}
		}
		return events;
	}
	
	private CalendarEvent prepareEvent(CrmCampaign campaign,CrmActivityMaster activity){
		CalendarEvent event = new CalendarEvent();
		event.setId(activity.getActivityid());
		String flag = getString(activity.getFlag(), "Event");
		String subject = getString(campaign.getCampaignname(),"")+ " Campaign's "+flag;
		String desc = getString(activity.getSubject(),subject);	
		event.setSubject(subject);
		event.setDescription(desc);
		event.setStartTime(activity.getStartdate());
		event.setEndTime(activity.getEnddate());
		event.setCalendarId(activity.getCalendarid()!=null?activity.getCalendarid():"1");
		event.setDenyDelete(true);
		return event;
	}
	
	String getString(String value, String def){
		if(value==null||value.trim().length()==0)
			return def;
		
		return value;
	}

}
