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
package com.krawler.spring.calendar.calendarmodule;

import java.util.List;

import com.krawler.calendar.calendarmodule.DeskeraCalendar;
import com.krawler.common.admin.User;

public interface CalendarUserDao {
	List<CalendarPermission> getCalendarPermission(final String calendarId);
	void deleteCalendarPermission(String calendarId);
	void insertCalendarPermission(CalendarPermission permission);
	void selectCalendar(final String calendarId, final String userId);
	void deselectCalendar(final String calendarId, final String userId);
	List<DeskeraCalendar> getSelectedCalendars(final String userId);
	List<String> getSelectedCalendarIds(final String userId);
	boolean hasDefaultCalendar(User user);
	void assignCalendar(User user, DeskeraCalendar cal);
	List<String> getCalendarIds(User user);
}
