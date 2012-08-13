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

import java.util.List;

import com.krawler.calendar.calendarmodule.DeskeraCalendar;
import com.krawler.common.admin.User;
import com.krawler.spring.calendar.calendarmodule.CalendarPermission;

public interface CalendarUserService {
	String DEFAULT_COLORCODE = "2";
	String DEFAULT_LOCATION =  "";
	User getUser(String userId);
	void assignCalendar(DeskeraCalendar cal,User user);
	List<CalendarPermission> getCalendarPermission(String calendarId);
	void insertCalendarPermission(String calendarId, String userId, int permissionLevel);
	void deleteCalendarPermission(String calendarId);
	List<DeskeraCalendar> getCalendars(User user);
	void selectCalendar(final String calendarId, final String userId);
	void deselectCalendar(final String calendarId, final String userId);
	List<String> getSelectedCalendarIds(final String userId);
	DeskeraCalendar getActivityCalendar();
}
