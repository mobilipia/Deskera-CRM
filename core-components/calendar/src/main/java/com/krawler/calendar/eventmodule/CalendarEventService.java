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

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public interface CalendarEventService {
	public List<CalendarEvent> getEvents(String[] cid);
	public List<CalendarEvent> getEvents(String[] cid, int start, int limit);
	public List<CalendarEvent> getEvents(String[] cid, Date from, Date to);
	public List<CalendarEvent> getEvents(String[] cid, Date from, Date to, int start, int limit);
	public CalendarEvent addEvent(String calendarId, Date startTime, Date endTime, String subject, String desc, String location, int showAs, int priority, String recpattern, Date recend, String resources, boolean allDay);
	public CalendarEvent updateEvent(String id, String calendarId, Date startTime, Date endTime, String subject, String desc, String location, int showAs, int priority, String recpattern, Date recend, String resources, boolean allDay);
	public int deleteEvent(String eventid);
	List<EventDuration> breakDuration(EventDuration duration, TimeZone tz);
	List<CalendarEvent> breakEvent(CalendarEvent originalEvent, List<EventDuration> durations);
}
