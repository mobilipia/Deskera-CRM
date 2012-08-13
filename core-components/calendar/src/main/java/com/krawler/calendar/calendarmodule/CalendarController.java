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

import com.krawler.common.comet.ServerEventManager;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import krawler.taglib.json.util.JSONArray;
public class CalendarController extends MultiActionController {
    private CalendarService calendarService;
    private String successView;
    private ServerEventManager channelManager;
    public String getSuccessView() {
		return successView;
	}
    public void setSuccessView(String successView) {
		this.successView = successView;
    }

    public void setCalendarService(CalendarService calendarService) {
		this.calendarService = calendarService;
	}
    
    public CalendarService getCalendarService() {
		return calendarService;
	}

    public void setchannelManager(ServerEventManager channelManager) {
        this.channelManager = channelManager;
    }
    
    public ModelAndView getCalendarlist(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        List result = null;
        try {
            result = this.calendarService.getCalendars(request.getParameter("cid"));
        } catch(Exception e) {
            logger.warn(e.getMessage());
        }
        return new ModelAndView(new CalendarView(new CalendarJSONMapper(),"data"), "data", result);
    }

    public ModelAndView createcalendar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        JSONObject jbj = new JSONObject();
        try {
            jbj.put("valid", true);
            JSONObject jobj = new JSONObject().put("success", false);
            jbj.put("data", jobj);
            String calAction = request.getParameter("action");
            String cname = request.getParameter("cname");
            String description = request.getParameter("description");
            String location = request.getParameter("location");
            String timezone = request.getParameter("timezone");
            String colorcode = request.getParameter("colorcode");
            int caltype = Integer.parseInt(request.getParameter("caltype"));
            String permission = request.getParameter("permission");
            String entityid = request.getParameter("userid");

            DeskeraCalendar cal = this.calendarService.createCalendar(cname,description,location,timezone,colorcode,caltype);
            jobj.put("success", true);
            jobj.put("cid", cal.getId());
            
            result = jbj.toString();
        } catch(JSONException e) {
            logger.warn(e.getMessage());
        } 
        return new ModelAndView("jsonView-ex", "model", result);
    }

    public ModelAndView updatecalendar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        JSONObject jbj = new JSONObject();
        try {
            jbj.put("valid", true);
            jbj.put("data", "{\"success\":\"false\"}");
            String cid = request.getParameter("cid");
            String cname = request.getParameter("cname");
            String description = request.getParameter("description");
            String location = request.getParameter("location");
            String timezone = request.getParameter("timezone");
            String colorcode = request.getParameter("colorcode");
            String permission = request.getParameter("permission");
            int caltype = Integer.parseInt(request.getParameter("caltype"));
            
            this.calendarService.updateCalendar(cid, cname, description, location, timezone, colorcode,caltype);
            jbj.put("data", "{\"success\":\"true\"}");
            result = jbj.toString();
        } catch(Exception e) {
            logger.warn(e.getMessage());
        }
        return new ModelAndView("jsonView-ex", "model", result);
    }

    public ModelAndView deletecalendar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        JSONObject jbj = new JSONObject();
        try {
            jbj.put("valid", true).put("data", "{success:'false'}");
            String cid = request.getParameter("cid");
            this.calendarService.deleteCalendar(cid);
            jbj.put("data", "{success:'true'}");
            result = jbj.toString();
        } catch(Exception e) {
            logger.warn(e.getMessage());
        }
        return new ModelAndView("jsonView-ex", "model", result);
    }
}
