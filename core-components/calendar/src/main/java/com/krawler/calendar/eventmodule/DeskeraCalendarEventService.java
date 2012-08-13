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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DeskeraCalendarEventService implements CalendarEventService {
	private CalendarEventDao calendarEventDao;
		
	public void setCalendarEventDao(CalendarEventDao calendarEventDao) {
		this.calendarEventDao = calendarEventDao;
	}

	@Override
	public CalendarEvent addEvent(String calendarId, Date startTime, Date endTime, String subject, String desc, String location, int showAs, int priority, String recpattern, Date recend, String resources, boolean allDay) {
		CalendarEvent event = new CalendarEvent();
        event.setCalendarId(calendarId);
        event.setDescription(desc);
        event.setAllDay(allDay);
        event.setLocation(location);
        event.setPriority(priority);
        event.setRepeatTill(recend);
        event.setRepeatPattern(recpattern);
        event.setResources(resources);
        event.setShowAs(showAs);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setSubject(subject);
		calendarEventDao.saveEvent(event);
		return event;
	}

	@Override
	public int deleteEvent(String eventid) {
		return calendarEventDao.deleteEvents(eventid);
	}

	@Override
	public CalendarEvent updateEvent(String id, String calendarId, Date startTime, Date endTime, String subject, String desc, String location, int showAs, int priority, String recpattern, Date recend, String resources, boolean allDay) {
		CalendarEvent event = new CalendarEvent();
		event.setId(id);
        event.setCalendarId(calendarId);
        event.setDescription(desc);
        event.setAllDay(allDay);
        event.setLocation(location);
        event.setPriority(priority);
        event.setRepeatTill(recend);
        event.setRepeatPattern(recpattern);
        event.setResources(resources);
        event.setShowAs(showAs);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setSubject(subject);
		calendarEventDao.updateEvent(event);
		return event;
	}

	@Override
	public List<CalendarEvent> getEvents(String[] cid) {
		return calendarEventDao.getCalendarEvents(Arrays.asList(cid), null, null);
	}

	@Override
	public List<CalendarEvent> getEvents(String[] cid, int start, int limit) {
		return calendarEventDao.getCalendarEvents(Arrays.asList(cid), null, null, new int[]{start, limit});
	}

	@Override
	public List<CalendarEvent> getEvents(String[] cid, Date from, Date to) {
		return calendarEventDao.getCalendarEvents(Arrays.asList(cid), from, to);
	}

	@Override
	public List<CalendarEvent> getEvents(String[] cid, Date from, Date to, int start, int limit) {
		return calendarEventDao.getCalendarEvents(Arrays.asList(cid), from, to, new int[]{start, limit});
	}
	
	@Override
	public List<EventDuration> breakDuration(EventDuration duration, TimeZone tz) {
		List<EventDuration> list = new ArrayList<EventDuration>();
		if(!duration.isValid())
			throw new IllegalArgumentException("Event startdate cannot be after enddate");
		Date tmpDate = new Date(duration.getStartTime().getTime()); 
		Calendar cal = Calendar.getInstance(tz);
		cal.setTime(tmpDate);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,-1);
		EventDuration d;
		do{
			cal.add(Calendar.DATE, 1);
			d = new EventDuration();
			d.setStartTime((Date)(tmpDate.clone()));
			d.setEndTime(cal.getTime());
			list.add(d);
			tmpDate.setTime(cal.getTimeInMillis()+1);
		}while(cal.getTime().before(duration.getEndTime()));
		d.setEndTime(duration.getEndTime());
		return list;
	}
	
	@Override
	public List<CalendarEvent> breakEvent(final CalendarEvent originalEvent, final List<EventDuration> durations) {
		List<CalendarEvent> events = new ArrayList<CalendarEvent>();
		if(durations!=null){
			int size = durations.size();
			for(int i = 0; i < size; i++){			
				EventDuration d = durations.get(i);
				CalendarEvent e = originalEvent.clone();
				e.setId(originalEvent.getId()+(i>0?"CNT_"+i:""));
				//e.setStartTime(d.getStartTime());
				//e.setEndTime(d.getEndTime());
				events.add(e);
			}
		}
		
		return events;
	}
	
}
