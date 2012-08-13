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

import com.krawler.calendar.calendarmodule.DeskeraCalendar;

public class CalendarEvent implements Cloneable {
    private String id;
    private String calendarId;
    private Date startTime;
    private Date endTime;
    private String subject;
    private String description;
    private String location;
    private int showAs;
    private int priority;
    private String repeatPattern;
    private Date repeatTill;
    private String resources;
    private Date createdOn;
    private Date updatedOn;
    private boolean allDay;
    private boolean denyDelete=false; //This flag is used to allow delete particular event.
	
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public int getShowAs() {
		return showAs;
	}
	public void setShowAs(int showAs) {
		this.showAs = showAs;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getRepeatPattern() {
		return repeatPattern;
	}
	public void setRepeatPattern(String repeatPattern) {
		this.repeatPattern = repeatPattern;
	}
	public Date getRepeatTill() {
		return repeatTill;
	}
	public void setRepeatTill(Date repeatTill) {
		this.repeatTill = repeatTill;
	}
	public String getResources() {
		return resources;
	}
	public void setResources(String resources) {
		this.resources = resources;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	public boolean isAllDay() {
		return allDay;
	}
	public void setAllDay(boolean allDay) {
		this.allDay = allDay;
	}
	
	@Override
	public CalendarEvent clone() {
		try {
			return (CalendarEvent)super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return null;
	}
	
	public EventDuration getDuration(){
		return new EventDuration(startTime, endTime);
	}
	public void setDenyDelete(boolean denyDelete) {
		this.denyDelete = denyDelete;
	}
	public boolean isDenyDelete() {
		return denyDelete;
	}
}
