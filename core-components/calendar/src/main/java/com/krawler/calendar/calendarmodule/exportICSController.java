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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.krawler.calendar.eventmodule.CalendarEvent;
import com.krawler.calendar.eventmodule.CalendarEventService;
import com.krawler.common.util.StringUtil;

public class exportICSController extends MultiActionController {
    private String successView;
    private CalendarEventService calendarEventService;
    private CalendarService calendarService;
    public String getSuccessView() {
		return successView;
	}
    
    public void setSuccessView(String successView) {
		this.successView = successView;
    }
    
    public void setCalendarEventService(CalendarEventService calendarEventService) {
        this.calendarEventService = calendarEventService;
    }

    public void setCalendarService(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    public CalendarEventService getCalendarEventService() {
		return calendarEventService;
	}

	public CalendarService getCalendarService() {
		return calendarService;
	}

	public ModelAndView exportICS(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
		Calendar cal=new Calendar();
        String calId = request.getParameter("cid");
        cal = exportCal(TimeZone.getDefault(), calendarService.getCalendars(calId));
        return new ModelAndView(new ExportCalendarView(), "calendar", cal);
    }

    public Calendar exportCal(final  java.util.TimeZone tz, List<DeskeraCalendar> cals) throws ServletException {
        Calendar cal = new Calendar();
        if(cals==null)
        	return cal;

        Map<String, DeskeraCalendar> calMap = getCalendarMap(cals);
        List<CalendarEvent> events = calendarEventService.getEvents(calMap.keySet().toArray(new String[]{}));
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        ComponentList componentList = cal.getComponents();
        for (CalendarEvent event:events) {
        	DeskeraCalendar dcal = calMap.get(event.getCalendarId());
        	TimeZone timezone = registry.getTimeZone(dcal.getTimeZone()==null?tz.getID():dcal.getTimeZone());
            componentList.add(convertEvent(event, timezone));
        }
                
        return cal;
    }
    
    protected Map<String, DeskeraCalendar> getCalendarMap(final Collection<DeskeraCalendar> cals){
    	Map<String, DeskeraCalendar> map = new HashMap<String, DeskeraCalendar>();
    	if(cals!=null){
    		for(DeskeraCalendar cal: cals){
    			map.put(cal.getId(), cal);
    		}
    	}
    	return map;
    }
    
    protected VEvent convertEvent(CalendarEvent event, TimeZone timeZone){
        VEvent vEvent = new VEvent();
        PropertyList props = vEvent.getProperties();
        props.add(new Uid(event.getId()));
        props.add(new Summary(event.getSubject()));
        if(!StringUtil.isNullOrEmpty(event.getDescription()))
            props.add(new Description(event.getDescription()));
        if(!StringUtil.isNullOrEmpty(event.getLocation()))
            props.add(new Location(event.getLocation()));
        DateTime sdt = new DateTime(event.getStartTime());
        sdt.setTimeZone(timeZone);
        props.add(new DtStart(sdt));
        DateTime edt = new DateTime(event.getEndTime());
        edt.setTimeZone(timeZone);
        props.add(new DtEnd(edt));
        
        return vEvent;
    }
}
