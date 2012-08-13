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

import com.krawler.common.util.StringUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.krawler.dao.BaseDAO;

public class JdbcCalendarEventDao extends BaseDAO implements CalendarEventDao {

	@Override
	public int deleteEvents(String... eventids) {
		if(eventids==null||eventids.length==0)
			return 0;
		
		StringBuffer qMarks=new StringBuffer();
		for(String id:eventids){
			if(qMarks.length()>0)
				qMarks.append(',');
			qMarks.append('?');
		}
		String query = "delete from calendarevents where eid in ("+qMarks+")";
        return updateJDBC(query, eventids);
	}

	@Override
	public List<CalendarEvent> getCalendarEvents(List<String> calendars, Date from, Date to) {
		return getCalendarEvents(calendars, from, to, null);
	}

	@Override
	public List<CalendarEvent> getCalendarEvents(List<String> calendars, Date from, Date to, int[] limitParams) {
		StringBuffer qMarks=new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        String query = " select ce.* from calendars c inner join calendarevents ce on c.cid=ce.cid  ";
        String filterQuery = "";
        if(!calendars.isEmpty()) {
            for(String id:calendars){
                if(qMarks.length()>0)
                    qMarks.append(',');
                qMarks.append('?');
            }
            filterQuery += " ce.cid in(" + qMarks + ")";
            params.addAll(calendars);
        }
        if(from!=null&&to!=null) {
            if(!StringUtil.isNullOrEmpty(filterQuery)) {
                 filterQuery +=" and ";
            }
        	filterQuery+=" ((ce.startdate >= ? and ce.startdate <= ?) or (ce.enddate >= ? and ce.enddate <= ?) or (ce.startdate <= ? and ce.enddate >= ?)) ";
            params.add(from.getTime());
            params.add(to.getTime());
            params.add(from.getTime());
            params.add(to.getTime());
            params.add(from.getTime());
            params.add(to.getTime());
        }
        if(!StringUtil.isNullOrEmpty(filterQuery))
            filterQuery = " where ".concat(filterQuery);
        query +=filterQuery+" ORDER BY ce.startdate DESC, ce.enddate - ce.startdate DESC";
        if(limitParams!=null){
        	query+=" limit ? offset ?";
        	params.add(limitParams[1]);
            params.add(limitParams[0]);
        }
        
		return queryJDBC(query, params.toArray(), new CalendarEventRowMapper());
	}

	@Override
	public void saveEvent(CalendarEvent event) {
		String query = "insert into calendarevents  (cid, startdate, enddate, subject, descr, location, showas," +
	        "priority,recpattern,recend,resources, createdon, updatedon, allday,eid) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		if(event.getId()==null){
			event.setId(UUID.randomUUID().toString());
		}
		Date date = new Date();
		if(event.getCreatedOn()==null){
			event.setCreatedOn(date);
		}
		if(event.getUpdatedOn()==null){
			event.setUpdatedOn(date);
		}
		updateJDBC(query, new Object[]{event.getCalendarId(), timeInMillis(event.getStartTime()), timeInMillis(event.getEndTime()),event.getSubject(),
		event.getDescription(), event.getLocation(), event.getShowAs(), event.getPriority(),event.getRepeatPattern(), timeInMillis(event.getRepeatTill()),
		event.getResources(), event.getCreatedOn().getTime(),event.getUpdatedOn().getTime(), event.isAllDay(), event.getId()});
	}

	@Override
	public void updateEvent(CalendarEvent event) {
		String query = "update calendarevents set cid=?, startdate=?, enddate=?, subject=?, descr=?, location=?, showas=?," +
                    "priority=?,recpattern=?,recend=?,resources=?, updatedon=?, allday=? where eid=?";
		if(event.getUpdatedOn()==null)
			event.setUpdatedOn(new Date());
		updateJDBC(query, new Object[]{event.getCalendarId(), timeInMillis(event.getStartTime()), timeInMillis(event.getEndTime()),event.getSubject(),
				event.getDescription(), event.getLocation(), event.getShowAs(), event.getPriority(),event.getRepeatPattern(), timeInMillis(event.getRepeatTill()),
				event.getResources(), event.getUpdatedOn().getTime(), event.isAllDay(), event.getId()});
	}
	
	private Long timeInMillis(Date date){
		return date==null?null:date.getTime();
	}
}
