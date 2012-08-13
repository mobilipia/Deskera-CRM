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

import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.krawler.calendar.calendarmodule.DeskeraCalendar;
import com.krawler.calendar.calendarmodule.ExportCalendarView;
import com.krawler.calendar.calendarmodule.exportICSController;
import com.krawler.calendar.eventmodule.CalendarEvent;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.calendar.eventmodule.bizservice.CalendarUserService;
import com.krawler.spring.calendar.eventmodule.bizservice.ModuleEventService;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;

public class crmExportICSController extends exportICSController  implements MessageSourceAware{
	private CalendarUserService calendarUserService;
    private crmManagerDAO crmManagerDAOObj;
    private List<ModuleEventService> moduleEventServices;
    private MessageSource messageSource;
    
    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setModuleEventServices(List<ModuleEventService> moduleEventServices) {
		this.moduleEventServices = moduleEventServices;
	}
   
    public void setCalendarUserService(CalendarUserService calendarUserService) {
		this.calendarUserService = calendarUserService;
	}

	@Override
    public ModelAndView exportICS(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    	java.util.TimeZone tz;
    	ModelAndView modelAndView=new ModelAndView("", "", "");
    	boolean userFlag=false;
    	HashMap model = new HashMap();
		try {
			tz = java.util.TimeZone.getTimeZone("GMT"+sessionHandlerImpl.getTimeZoneDifference(request));
		} catch (SessionExpiredException e1) {
			tz = TimeZone.getDefault();
		}
		
		Calendar cal=new Calendar();
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone icalTZ = registry.getTimeZone(tz.getID());
        try {
            String userid = request.getParameter("uid");
            String[] calIds = request.getParameterValues("cid");
            if(StringUtil.isNullOrEmpty(userid)) {
            	if(calIds!=null){
            		List<DeskeraCalendar> cals = this.getCalendarService().getCalendars(calIds);
            		if(!cals.isEmpty()){
            			cal = exportCal(tz, cals);
            			if (cal.getComponents().size() == 0) {
							model.put("successMsg", messageSource.getMessage("crm.calendar.exportEmpty.msg", null, RequestContextUtils.getLocale(request)));
							model.put("successFlag", "0");
							modelAndView = new ModelAndView("messageview", "model", model);
						} else {
							modelAndView = new ModelAndView(new ExportCalendarView(), "calendar", cal);
						}
            		}else{
            			model.put("successMsg", messageSource.getMessage("crm.calendar.exportfail.msg", null, RequestContextUtils.getLocale(request)));
        				model.put("successFlag", "0");
            			modelAndView=new ModelAndView("messageview", "model", model);
            		}
            	}
            } else {
            	if(calIds!=null) {
            		User user = calendarUserService.getUser(userid);
            		List<DeskeraCalendar> cals = calendarUserService.getCalendars(user);
            		if(!cals.isEmpty()){
                    cal = exportCal(tz, cals);
						if (cal.getComponents().size() == 0) {
							model.put("successMsg", messageSource.getMessage("crm.calendar.exportEmpty.msg", null, RequestContextUtils.getLocale(request)));
							model.put("successFlag", "0");
							modelAndView = new ModelAndView("messageview", "model", model);
						} else {
							modelAndView = new ModelAndView(new ExportCalendarView(), "calendar", cal);
						}
					} else {
						model.put("successMsg", messageSource.getMessage("crm.calendar.exportfail.msg", null, RequestContextUtils.getLocale(request)));
						model.put("successFlag", "0");
						modelAndView = new ModelAndView("messageview", "model", model);
					}
            	}
            	
            	userFlag=fetchModuleEvents(cal.getComponents(), userid, icalTZ);
            	if(userFlag){
            	 modelAndView=new ModelAndView(new ExportCalendarView(), "calendar", cal);
            	}else{
            		model.put("successMsg", messageSource.getMessage("crm.calendar.exportfail.msg", null, RequestContextUtils.getLocale(request)));
    				model.put("successFlag", "0");
    				modelAndView=new ModelAndView("messageview", "model", model);
            	}
            } 

        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return modelAndView;
    }
	
	private boolean fetchModuleEvents(ComponentList components, String userid, TimeZone tz) throws ServiceException{
		boolean userFlag=false;
		StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
		if(moduleEventServices!=null){
			for(ModuleEventService mes:moduleEventServices){
				List<CalendarEvent> events = mes.getEvents(usersList,null);
				for(CalendarEvent event:events)
				components.add(convertEvent(event, tz));
				if(!components.isEmpty()){
				userFlag=true;
				}else{
					userFlag=false;
				}
			}
		}
		return userFlag;
	}
	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
		
	}
	
}
