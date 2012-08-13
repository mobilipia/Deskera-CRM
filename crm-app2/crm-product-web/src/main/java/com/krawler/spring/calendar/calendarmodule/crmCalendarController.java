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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
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

import krawler.taglib.json.util.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;

import com.krawler.calendar.calendarmodule.CalendarController;
import com.krawler.calendar.calendarmodule.CalendarJSONMapper;
import com.krawler.calendar.calendarmodule.CalendarView;
import com.krawler.calendar.calendarmodule.DeskeraCalendar;
import com.krawler.calendar.eventmodule.CalendarEvent;
import com.krawler.calendar.eventmodule.CalendarEventService;
import com.krawler.calendar.eventmodule.EventJSONMapper;
import com.krawler.common.admin.User;
import com.krawler.common.comet.ServerEventManager;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.spring.calendar.eventmodule.bizservice.CalendarUserService;
import com.krawler.spring.calendar.eventmodule.bizservice.ModuleEventService;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONObject;

public class crmCalendarController extends CalendarController {
    private String successView;
    private ServerEventManager channelManager;
    private CalendarUserService calendarUserService;
    private List<ModuleEventService> moduleEventServices;
    private crmManagerDAO crmManagerDAOObj;
    private CalendarEventService calendarEventService;

    
    private static Log LOG = LogFactory.getLog(crmCalendarController.class);
    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }
    
    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setchannelManager(ServerEventManager channelManager) {
        this.channelManager = channelManager;
        super.setchannelManager(channelManager);
    }

    public void setCalendarUserService(CalendarUserService calendarUserService) {
		this.calendarUserService = calendarUserService;
	}
    
    public void setModuleEventServices(List<ModuleEventService> moduleEventServices) {
		this.moduleEventServices = moduleEventServices;
	}
    
    public void setCalendarEventService(CalendarEventService calendarEventService) {
		this.calendarEventService = calendarEventService;
	}

    

	@Override
	public ModelAndView deletecalendar(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		ModelAndView mv =  super.deletecalendar(request, response);
        Map<String, String> data = new HashMap<String, String>();
        data.put("action", "3");
        data.put("cid", request.getParameter("cid"));
        data.put("success","true");
        this.channelManager.publish("/calTree/"+request.getParameter("userid") , data, this.getServletContext());
        return mv;
	}

	@Override
	public ModelAndView updatecalendar(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		ModelAndView mv =  super.updatecalendar(request, response);
		try {
			String calendarId = request.getParameter("cid");
			JSONObject obj  = new CalendarJSONMapper().mapJSON(getCalendarService().getCalendar(calendarId));
			obj.put("permissionlevel", "");
			
			String userid = request.getParameter("userid");
			String calAction =  request.getParameter("action");
			Map<String, String> data = new HashMap<String, String>();
			data.put("action", calAction);
			data.put("data", new JSONObject().put("data", new JSONArray().put(obj)).toString());
			data.put("success","true");
			this.channelManager.publish("/calTree/"+userid, data, this.getServletContext());
		} catch (Exception e) {
			logger.warn(e.getMessage(),e);
		}
		
		return mv;
	}

	@Override
	public ModelAndView createcalendar(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject jbj = new JSONObject();
		try {
            jbj.put("valid", true);
            jbj.put("data", "{success:'false'}");
            String calAction =  request.getParameter("action");
	        String cname = request.getParameter("cname");
	        String description = request.getParameter("description");
	        String location = request.getParameter("location");
	        String timezone = request.getParameter("timezone");
	        String colorcode = request.getParameter("colorcode");
	        int caltype = Integer.parseInt(request.getParameter("caltype"));
	        String permission = request.getParameter("permission");
	        String userid = request.getParameter("userid");
	        DeskeraCalendar cal = getCalendarService().createCalendar(cname,description,location,timezone,colorcode,caltype);
			User user = calendarUserService.getUser(userid);
			calendarUserService.assignCalendar(cal, user);
			jbj.put("data", "{success:'true',cid:'"+cal.getId()+"'}");
			JSONObject obj  = new CalendarJSONMapper().mapJSON(cal);
			obj.put("permissionlevel", "");
            Map<String, String> data = new HashMap<String, String>();
            data.put("action", calAction);
            data.put("data", new JSONObject().put("data", new JSONArray().put(obj)).toString());
            data.put("success","true");
            this.channelManager.publish("/calTree/"+userid, data, this.getServletContext());
		}catch(Exception e){
			logger.warn(e.getMessage(),e);
		}
		
		return new ModelAndView("jsonView-ex", "model", jbj.toString());
	}

	public ModelAndView fetchCreateCalendarList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        //String result = "";
        //try {
    		
            User user = calendarUserService.getUser(request.getParameter("userid"));

            
            List<DeskeraCalendar> list= calendarUserService.getCalendars(user);
        //} catch(Exception e) {
        //    logger.warn(e.getMessage(),e);
        //}
        return new ModelAndView(new CalendarView(new CalendarJSONMapper(),"data"), "data", list);
    }
     public void setSelectCalendarList(HttpServletRequest request, HttpServletResponse response)
            throws ServiceException {

      try {
            String userid = request.getParameter("userid");
            String nodeid = request.getParameter("nodeid");
            String operation = request.getParameter("operation");
            HashMap requestparams = new HashMap();
            requestparams.put("userid", userid);
            requestparams.put("nodeid", nodeid);
            requestparams.put("operation", operation);
            if("select".equals(operation)){
            	calendarUserService.selectCalendar(nodeid, userid);
            }else
            	calendarUserService.deselectCalendar(nodeid, userid);
          
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
           
        }

    }
     public ModelAndView getSelectCalendarList(HttpServletRequest request, HttpServletResponse response)
            throws ServiceException {
         JSONObject selectedcalendar= new JSONObject();
         try {
                String ownerid=request.getParameter("ownerid");
                selectedcalendar=new JSONObject().put("select", new JSONArray(calendarUserService.getSelectedCalendarIds(ownerid)));
          } catch (Exception e) {
                logger.warn(e.getMessage(),e);
          }
          return new ModelAndView(successView, "model", selectedcalendar.toString());
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.calendar.calendarmodule.CalendarController#getCalendarlist(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ModelAndView getCalendarlist(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        List result = null;
        try {
            String userId = request.getParameter("userid");
            //String companyId = sessionHandlerImpl.getCompanyid(request);
            User user = calendarUserService.getUser(userId);
            result = this.calendarUserService.getCalendars(user);//.getCalendarlist(entityid, companyId);
        } catch(Exception e) {
            LOG.warn("Exception in getCalendarlist()", e);
        }
        return new ModelAndView(new CalendarView(new CalendarJSONMapper(),"data"), "data", result);
    }
     
	public ModelAndView getcalpermission(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		List<CalendarPermission> perms = null;
		try {
			String cid = request.getParameter("cid");
			perms = this.calendarUserService.getCalendarPermission(cid);

			Map<String, String> data = new HashMap<String, String>();
			String userid = request.getParameter("userid");
			data.put("action", "3");
			data.put("cid", cid);
			data.put("success", "true");
			ServerEventManager.publish("/calTree/" + userid, data, this.getServletContext());

		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return new ModelAndView(new CalendarView(new PermissionJSONMapper(), "data"), "data", perms);
	}

	public ModelAndView getAllEvents(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
        List<CalendarEvent> result = null;
        List<CalendarEvent> modEvents=null;
        TimeZone tz;
		try {
			tz = TimeZone.getTimeZone("GMT"+sessionHandlerImpl.getTimeZoneDifference(request));
		} catch (SessionExpiredException e1) {
			tz = TimeZone.getDefault();
		}
        try {
        	List<CalendarEvent> temp;
            if(request.getParameter("calView").equals("1")) {
                String[] cidList=request.getParameterValues("cidList");
                Long viewdt1=Long.parseLong(request.getParameter("viewdt1"));
                Long viewdt2=Long.parseLong(request.getParameter("viewdt2"));
                int limit=Integer.parseInt(request.getParameter("limit"));
                int offset=Integer.parseInt(request.getParameter("start"));

                temp = calendarEventService.getEvents(cidList,new Date(viewdt1),new Date(viewdt2),offset,limit);
            }
            else {
                String[] cid=request.getParameterValues("cid");
                temp = calendarEventService.getEvents(cid);
            }
            if(temp!=null){
            	result = new ArrayList<CalendarEvent>();
                for(CalendarEvent e:temp){
                	if(!e.isAllDay() && !(request.getParameter("calView").equals("1"))){
                		List durations = calendarEventService.breakDuration(e.getDuration(), tz);
                		if(durations.size()>1)
                			result.addAll(calendarEventService.breakEvent(e, durations));
                		else
                			result.add(e);	 
                	}else{
                		result.add(e);
                	}
                	
                }
            }
			StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
			if(moduleEventServices!=null){
				HashMap<String,Object> params=null;
				if(request.getParameter("calView").equals("1")) {
					params=new HashMap<String,Object>();
					Long stDate=Long.parseLong(request.getParameter("viewdt1"));
	                Long edDate=Long.parseLong(request.getParameter("viewdt2"));
	                params.put("from", stDate);
	                params.put("to", edDate);
				}
				
				for(ModuleEventService mes:moduleEventServices){
					modEvents = mes.getEvents(usersList,params);
					if(modEvents!=null){
						for(CalendarEvent e:modEvents){
							try{
								if(!e.isAllDay() && !(request.getParameter("calView").equals("1"))){
									List durations = calendarEventService.breakDuration(e.getDuration(), tz);
									if(durations.size()>1)
										result.addAll(calendarEventService.breakEvent(e, durations));
									else
										result.add(e);	                		 
								}else{
									result.add(e);
								}
							}catch(Exception ev){
								logger.warn(ev.getMessage());
							}
						}
					}
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
}
