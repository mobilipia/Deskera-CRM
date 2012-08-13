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
import java.util.List;
import java.util.TimeZone;

import com.krawler.calendar.calendarmodule.CalendarDao;
import com.krawler.calendar.calendarmodule.DeskeraCalendar;
import com.krawler.calendar.calendarmodule.DeskeraCalendarService;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.calendar.calendarmodule.CalendarPermission;
import com.krawler.spring.calendar.calendarmodule.CalendarUserDao;
import com.krawler.spring.profileHandler.profileHandlerDAO;

public class CrmCalendarService extends DeskeraCalendarService implements CalendarUserService {
	private CalendarUserDao calendarUserDao;
	private profileHandlerDAO profileHandlerDao;

	public void setCalendarUserDao(CalendarUserDao calendarUserDao) {
		this.calendarUserDao = calendarUserDao;
	}

	public void setProfileHandlerDao(profileHandlerDAO profileHandlerDao) {
		this.profileHandlerDao = profileHandlerDao;
	}

	@Override
	public List<CalendarPermission> getCalendarPermission(String cid) {
		return calendarUserDao.getCalendarPermission(cid);
	}

	@Override
	public void deleteCalendarPermission(String calendarId) {
		calendarUserDao.deleteCalendarPermission(calendarId);		
	}

	@Override
	public void insertCalendarPermission(String calendarId, String referenceId, int permissionLevel) {
		CalendarPermission permission = new CalendarPermission();
		permission.setCalendarId(calendarId);
		permission.setReferenceId(referenceId);
		permission.setPermissionLevel(permissionLevel);
		calendarUserDao.insertCalendarPermission(permission);
	}

	@Override
	public void assignCalendar(DeskeraCalendar cal, User user) {
		calendarUserDao.assignCalendar(user, cal);
		
	}
	
	@Override
	public List<DeskeraCalendar> getCalendars(User user) {
		boolean hasDefault = calendarUserDao.hasDefaultCalendar(user);
		if(!hasDefault){
			DeskeraCalendar cal = createCalendar(user.getFirstName()+" "+user.getLastName(), null, null, user.getTimeZone() == null ? TimeZone.getDefault().getDisplayName(): user.getTimeZone().getName(), CalendarUserService.DEFAULT_COLORCODE, DeskeraCalendar.CAL_DEFAULT);
			assignCalendar(cal, user);
		}
		List<String> ids = calendarUserDao.getCalendarIds(user);
		List<DeskeraCalendar> cals= getCalendars(ids.toArray(new String[]{}));
		cals.add(getActivityCalendar());
		return cals;
	}
	
	@Override
	public DeskeraCalendar getActivityCalendar(){
		DeskeraCalendar cal = new DeskeraCalendar();
		cal.setId("1");
		cal.setColorCode("0");
		cal.setName(Constants.DEFAULT_CALENDAR_NAME);
		cal.setType(DeskeraCalendar.CAL_ACTIVITY);
		cal.setDenyDelete(true);
		return cal;
	}

	@Override
	public User getUser(String userId) {
		User user = null;
		try {
			user = profileHandlerDao.getUserObject(userId);
		} catch (ServiceException e) {

		}
		return user;
	}

	@Override
	public void deselectCalendar(String calendarId, String userId) {
		calendarUserDao.deselectCalendar(calendarId, userId);
	}

	@Override
	public List<String> getSelectedCalendarIds(String userId) {
		return calendarUserDao.getSelectedCalendarIds(userId);
	}

	@Override
	public void selectCalendar(String calendarId, String userId) {
		calendarUserDao.selectCalendar(calendarId, userId);
	}

}
