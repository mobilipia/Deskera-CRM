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

import java.util.List;

import com.krawler.common.util.StringUtil;

public class DeskeraCalendarService implements CalendarService {
	private CalendarDao calendarDao;
	
	public void setCalendarDao(CalendarDao calendarDao) {
		this.calendarDao = calendarDao;
	}

	@Override
	public DeskeraCalendar createCalendar(String name, String description, String location, String timeZone, String colorCode, int type) {
		DeskeraCalendar cal = new DeskeraCalendar();
		cal.setName(name);
		cal.setDescription(description);
		cal.setLocation(location);
		cal.setTimeZone(timeZone);
		cal.setColorCode(colorCode);
		cal.setType(type);
		calendarDao.saveCalendar(cal);
		return cal;
//		String cid = cal.getId();
//		if(cid!=null&&cal.getType()==DeskeraCalendar.CAL_DEFAULT&&!StringUtil.isNullOrEmpty(permission)){
//            String[] userPermission = permission.split(",");
//            for(int i=0;i<userPermission.length;i++){
//                String[] permissions = userPermission[i].split("_");
//                int per = Integer.parseInt(permissions[1]);
//                insertCalendarPermission(cid,permissions[0],per);
//            }
//		}
//	TODO : do in controller publish data                
//                if(!res.equals("0")){
//                    if(returnStr.compareTo("{data:{}}") != 0){
//                        JSONObject jbj = new JSONObject(returnStr.toString());
//                        jbj.getJSONArray("data").getJSONObject(0).put("permissionlevel", per);
//                        Map<String, String> data = new HashMap<String, String>();
//                        data.put("action", calAction);
//                        data.put("data", jbj.toString());
//                        data.put("success","true");
//                        channelManager.publish("/calTree/"+permissions[0], data, servletcontext);
//                    }
//                }
            //}
			
		//}
	}

	@Override
	public void deleteCalendar(String cid) {
		calendarDao.deleteCalendars(cid);
	}

	@Override
	public List<DeskeraCalendar> getCalendars(String... calendarIds) {
		return calendarDao.getCalendars(calendarIds);
	}

	@Override
	public DeskeraCalendar getCalendar(String calendarId) {
		List<DeskeraCalendar> list = calendarDao.getCalendars(calendarId);
		if(list.isEmpty())
			return null;
		
		return list.get(0);
	}

	@Override
	public void updateCalendar(String cid, String name, String description, String location, String timeZone, String colorCode, int type) {
		DeskeraCalendar cal = new DeskeraCalendar();
		cal.setId(cid);
		cal.setName(name);
		cal.setDescription(description);
		cal.setLocation(location);
		cal.setTimeZone(timeZone);
		cal.setColorCode(colorCode);
		cal.setType(type);
		calendarDao.updateCalendar(cal);
//		deleteCalendarPermission(cid);
//		if(cid!=null&&cal.getType()==DeskeraCalendar.CAL_DEFAULT&&!StringUtil.isNullOrEmpty(permission)){
//            String[] userPermission = permission.split(",");
//            for(int i=0;i<userPermission.length;i++){
//                String[] permissions = userPermission[i].split("_");
//                int per = Integer.parseInt(permissions[1]);
//                insertCalendarPermission(cid,permissions[0],per);
//            }
//		}
//        	TODO : do in controller (publish data)            
//        if(caltype != 3) {
//            jbj.getJSONArray("data").getJSONObject(0).put("permissionlevel", "");
//            if(caltype==0){
//                    JSONObject sharedUserIds= getCalendarSharedUserIds(cid);
//                    if(sharedUserIds!=null){
//                        for(int ind=0;ind<(sharedUserIds.getJSONArray("data").length());ind++){
//                            String usrid=sharedUserIds.getJSONArray("data").getJSONObject(ind).getString("userid");
//                            Map<String, String> data = new HashMap<String, String>();
//                            data.put("action", "3");
//                            data.put("cid", cid);
//                            data.put("success","true");
//                            channelManager.publish("/calTree/"+usrid , data, servletcontext);
//                        }
//                    }
//                    int del = deleteCalendarPermission(cid);
//                    if(!StringUtil.isNullOrEmpty(permission)){
//                        String userPermission [];
//                        userPermission = permission.split(",");
//                        for(int i=0;i<userPermission.length;i++){
//                            String permissions [];
//                            permissions = userPermission[i].split("_");
//                            int per = Integer.parseInt(permissions[1]);
//                            String res=insertCalPermission(cid,permissions[0],per);
//                            if(!res.equals("0")){
//                                if(returnStr.compareTo("{data:{}}") != 0){
//                                    JSONObject jbj1 = new JSONObject(returnStr.toString());
//                                    jbj1.getJSONArray("data").getJSONObject(0).put("permissionlevel", per);
//                                    Map<String, String> data = new HashMap<String, String>();
//                                    data.put("action", "1");
//                                    data.put("data", jbj1.toString());
//                                    data.put("success","true");
//                                    channelManager.publish("/calTree/"+permissions[0], data, servletcontext);
//                                }
//                            }
//                        }
//                        //if(userPermission.length != 0)
//                            //LogHandler.InsertLogForCalendar(conn, userid,userid,jstr,52);
//                    }
//                }
//        }
	}
}
