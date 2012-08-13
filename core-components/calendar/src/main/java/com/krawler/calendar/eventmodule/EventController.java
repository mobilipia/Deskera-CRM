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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.krawler.calendar.calendarmodule.CalendarView;
import com.krawler.calendar.calendarmodule.JSONMapper;
import com.krawler.common.comet.ServerEventManager;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class EventController extends MultiActionController {
    private String successView;
    private CalendarEventService calendarEventService;
    private ServerEventManager channelManager;

    public String getSuccessView() {
		return successView;
	}
    public void setSuccessView(String successView) {
		this.successView = successView;
    }

    public void setCalendarEventService(CalendarEventService calendarEventService) {
		this.calendarEventService = calendarEventService;
	}
	public void setchannelManager(ServerEventManager channelManager) {
        this.channelManager = channelManager;
    }

    public ModelAndView getcalendarevents(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        List<CalendarEvent> result = null;
        TimeZone tz;
		try {
			tz = TimeZone.getTimeZone("GMT"+sessionHandlerImpl.getTimeZoneDifference(request));
		} catch (SessionExpiredException e1) {
			tz = TimeZone.getDefault();
		}
        try {
        	List<CalendarEvent> temp;
            if(request.getParameter("calView").equals("0")) {
                String[] cid=request.getParameterValues("cid");
                String viewdt1=request.getParameter("startts");
                String viewdt2=request.getParameter("endts");
                temp = calendarEventService.getEvents(cid);
            }
            else {
                String[] cidList=request.getParameterValues("cidList");
                int limit=Integer.parseInt(request.getParameter("limit"));
                int offset=Integer.parseInt(request.getParameter("start"));
                temp = calendarEventService.getEvents(cidList,offset,limit);
            }
            if(temp!=null){
            	result = new ArrayList<CalendarEvent>();
                for(CalendarEvent e:temp){
                	 List durations = calendarEventService.breakDuration(e.getDuration(), tz);
                	 if(durations.size()>1)
                		 result.addAll(calendarEventService.breakEvent(e, durations));
                	 else
                		 result.add(e);	                		 
                }
            }

        } catch(Exception e) {
            logger.warn(e.getMessage());
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt.setTimeZone(tz);
        EventJSONMapper mapper = new EventJSONMapper();
        mapper.setDateFormat(fmt);
        return new ModelAndView(new CalendarView(mapper, "data"), "data", result);
    }
    

    public ModelAndView insertcalevent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        TimeZone tz;
		try {
			tz = TimeZone.getTimeZone("GMT"+sessionHandlerImpl.getTimeZoneDifference(request));
		} catch (SessionExpiredException e1) {
			tz = TimeZone.getDefault();
		}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(tz);
        JSONObject jbj = new JSONObject();
        try {
            jbj.put("valid", true);
            jbj.put("data", "{\"success\":\"false\"}");
            String cid=request.getParameter("cid");
            Date startts=null, endts=null;
            try {
            	startts=sdf.parse(request.getParameter("startts"));
            	endts=sdf.parse(request.getParameter("endts"));
            }catch(Exception e){}
            String subject=request.getParameter("subject");
            String descr=request.getParameter("descr");
            String location=request.getParameter("location");
            String tmp =request.getParameter("showas");
            int showas="b".equals(tmp)?0:("t".equals(tmp)?1:2);//Integer.parseInt(request.getParameter("showas"));
            tmp = request.getParameter("priority");
            int priority="l".equals(tmp)?0:("m".equals(tmp)?1:2);//Integer.parseInt(request.getParameter("priority"));
            String recpattern=request.getParameter("recpattern");
            Date recend=null;
            try{
            	recend=sdf.parse(request.getParameter("recend"));
            }catch(Exception e){}
            String resources=request.getParameter("resources");
            boolean allday=Boolean.parseBoolean(request.getParameter("allDay"));
            CalendarEvent event = calendarEventService.addEvent(cid,startts,endts,subject,descr,location,showas,
                priority,recpattern,recend,resources,allday);
            List<EventDuration> durations = calendarEventService.breakDuration(event.getDuration(), tz);
            List<CalendarEvent> temp = calendarEventService.breakEvent(event, durations);
            EventJSONMapper mapper = new EventJSONMapper();
            mapper.setDateFormat(sdf);
            JSONArray jobj = mapJSON(temp, mapper);
            jbj.put("data", "{\"success\":\"true\"}");
            String calView=request.getParameter("calView");
            String calAction=request.getParameter("action");
            Map<String, String> data = new HashMap<String, String>();
            data.put("action", calAction);
            data.put("calView", calView);
            data.put("success","true");
            data.put("data", new JSONObject().put("data",jobj).toString());
            channelManager.publish("/calEvent/"+cid, data, this.getServletContext());
            result = jbj.toString();
        } catch(Exception e) {
            logger.warn(e.getMessage());
        }
        return new ModelAndView("jsonView-ex", "model", result);
    }

    public ModelAndView updatecalevent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        TimeZone tz;
		try {
			tz = TimeZone.getTimeZone("GMT"+sessionHandlerImpl.getTimeZoneDifference(request));
		} catch (SessionExpiredException e1) {
			tz = TimeZone.getDefault();
		}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(tz);
        JSONObject jbj = new JSONObject();
        try {
            jbj.put("valid", true);
            jbj.put("data", "{\"success\":\"false\"}");
            String eid=request.getParameter("eid");
            String cid=request.getParameter("cid");
            Date startts=null, endts=null;
            try {
            	startts=sdf.parse(request.getParameter("startts"));
            	endts=sdf.parse(request.getParameter("endts"));
            }catch(Exception e){}
            String subject=request.getParameter("subject");
            String descr=request.getParameter("descr");
            String location=request.getParameter("location");
            int showas=Integer.parseInt(request.getParameter("showas"));
            int priority=Integer.parseInt(request.getParameter("priority"));
            String recpattern=request.getParameter("recpattern");
            Date recend=null;
            try{
            	recend=sdf.parse(request.getParameter("recend"));
            }catch(Exception e){}
            String resources=request.getParameter("resources");
            boolean allDay=Boolean.parseBoolean(request.getParameter("allDay"));
            CalendarEvent event = calendarEventService.updateEvent(eid,cid,startts,endts,subject,descr,location,
                showas,priority,recpattern,recend, resources, allDay);
            List<EventDuration> durations = calendarEventService.breakDuration(event.getDuration(), tz);
            List<CalendarEvent> tmp = calendarEventService.breakEvent(event, durations);
            EventJSONMapper mapper = new EventJSONMapper();
            mapper.setDateFormat(sdf);
            JSONArray jobj = mapJSON(tmp, mapper);
            jbj.put("data", "{\"success\":\"true\"}");
            String calView=request.getParameter("calView");
            String calAction=request.getParameter("action");
            Map<String, String> data = new HashMap<String, String>();
            data.put("action", calAction);
            data.put("calView", calView);
            data.put("success","true");
            data.put("data", new JSONObject().put("data",jobj).toString());
            channelManager.publish("/calEvent/"+cid, data, this.getServletContext());
            result = jbj.toString();
        } catch(Exception e) {
            logger.warn(e.getMessage());
        }
        return new ModelAndView("jsonView-ex", "model", result);
    }
    
    private JSONArray mapJSON(List list, JSONMapper mapper) throws JSONException{
		JSONArray jArr = new JSONArray();
		if(list!=null){
			for(Object obj:list){
				jArr.put(mapper.mapJSON(obj));
			}
		}
    	
    	return jArr;
    }

    public ModelAndView deletecalevent(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        int result = 0;
        JSONObject jbj = new JSONObject();
        try {
            jbj.put("valid", true);
            String eid=request.getParameter("eid");
            if (eid.contains(",")) {
                eid = eid.substring(0, eid.length() - 1);
            }
            result=calendarEventService.deleteEvent(eid);
            if(result!=0) {
                jbj.put("data", "{\"success\":\"true\"}");
                String calView = request.getParameter("calView");
                String cid = request.getParameter("cid");
                String calAction = request.getParameter("action");
                Map<String, String> data = new HashMap<String, String>();
                data.put("action", calAction);
                data.put("calView", calView);
                data.put("eid", eid);
                data.put("success","true");
                channelManager.publish("/calEvent/"+cid, data, this.getServletContext());
            } else {
                jbj.put("data", "{\"success\":\"false\"}");
            }
        } catch(Exception e) {
            logger.warn(e.getMessage());
        }
        return new ModelAndView("jsonView-ex", "model", jbj.toString());
    }
}
