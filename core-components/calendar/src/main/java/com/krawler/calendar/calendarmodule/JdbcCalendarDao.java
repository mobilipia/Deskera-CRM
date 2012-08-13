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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;

import com.krawler.dao.BaseDAO;

public class JdbcCalendarDao extends BaseDAO implements CalendarDao {

	@Override
	public int deleteCalendars(String... calendarIds) {
		if(calendarIds==null||calendarIds.length==0)
			return 0;
		
		StringBuffer qMarks=new StringBuffer();
		for(String calid:calendarIds){
			if(qMarks.length()>0)
				qMarks.append(',');
			qMarks.append('?');
		}
		List<Object> params = new ArrayList<Object>(Arrays.asList(calendarIds));
		String query = "delete from calendarevents where cid in ("+qMarks+")";
        int count = updateJDBC(query, params.toArray());
        params.add(DeskeraCalendar.CAL_DEFAULT);
		query = "delete from calendars where cid in ("+qMarks+") and caltype<>?";
        count += updateJDBC(query, params.toArray());
		return count;
	}

	@Override
	public void updateCalendar(DeskeraCalendar calendar) {
		String query = "update calendars set cname=?, description=?, location=?, timezone=?, colorcode=?, caltype=?, " +
        	" updatedon=? where cid=?";
		if(calendar.getUpdatedOn()==null)
			calendar.setUpdatedOn(new Date());
		updateJDBC(query, new Object[]{calendar.getName(), calendar.getDescription(), calendar.getLocation(),
			calendar.getTimeZone(), calendar.getColorCode(), calendar.getType(), calendar.getUpdatedOn().getTime(), calendar.getId()});
	}

	@Override
	public void saveCalendar(DeskeraCalendar calendar) {
		String query = "insert into calendars (cname,description,location,timezone,colorcode,caltype,createdon, updatedon,cid)" +
        	" values( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		if(calendar.getId()==null)
			calendar.setId(UUID.randomUUID().toString());
		Date date = new Date();
		if(calendar.getCreatedOn()==null)
			calendar.setCreatedOn(date);
		if(calendar.getUpdatedOn()==null)
			calendar.setUpdatedOn(date);
		updateJDBC(query, new Object[]{calendar.getName(), calendar.getDescription(), calendar.getLocation(),
			calendar.getTimeZone(), calendar.getColorCode(), calendar.getType(), calendar.getCreatedOn().getTime(),calendar.getUpdatedOn().getTime(), calendar.getId()});
	}

	@Override
	public List<DeskeraCalendar> getCalendars(String... calendarIds) {
		StringBuffer qMarks=new StringBuffer();
		if(calendarIds!=null){
			for(String calid:calendarIds){
				if(qMarks.length()>0)
					qMarks.append(',');
				qMarks.append('?');
			}
		}
        String query = "select * from calendars where cid in("+qMarks+") order by caltype DESC ";
		return queryJDBC(query, calendarIds, new CalendarRowMapper());
	}

//	@Override
//	public List<CalendarPermission> getCalendarPermission(String calendarId) {
//		String query = "select * from sharecalendarmap where cid=?";
//		return queryJDBC(query, new Object[]{calendarId}, new CalendarPremissionRowMapper());
//	}
//	
//	@Override
//	public void deleteCalendarPermission(String calendarId) {
//		String query = "delete from sharecalendarmap where cid=?";
//		updateJDBC(query, new Object[]{calendarId});
//	}
//
//	@Override
//	public void insertCalendarPermission(CalendarPermission perm) {
//		if(perm.getId()==null)
//			perm.setId(UUID.randomUUID().toString());
//		if(perm.getUpdatedOn()==null)
//			perm.setUpdatedOn(new Date());
//		String query = "insert into sharecalendarmap (id, cid,userid,permissionlevel,updatedon) values( ?, ?, ?, ?)";
//		updateJDBC(query, new Object[]{perm.getId(),perm.getCalendarId(),perm.getReferenceId(),perm.getPermissionLevel(), perm.getUpdatedOn().getTime()});
//	}
//	@Override
//	public List<DeskeraCalendar> getSelectedCalendars(String referenceId) {
//        String query = "select distinct c.* from calendars c inner join calendarusermap m on c.cid = m.cid  where m.userid=?";
//        return queryJDBC(query, new Object[]{referenceId}, new CalendarRowMapper());
//	}
//	
//	@Override
//	public List<String> getSelectedCalendarIds(String referenceId) {
//        String query = "select distinct cid from calendarusermap where userid=?";
//        return queryJDBC(query, new Object[]{referenceId}, new RowMapper<String>(){
//
//			@Override
//			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
//				return rs.getString("cid");
//			}
//		});
//	}
//
//	@Override
//	public void selectCalendar(String calendarId, String referenceId) {
//        String query = "select count(id) from calendarusermap where userid=? and cid=? ";
//        int count = queryForIntJDBC(query, new Object[]{referenceId, calendarId});
//        if(count<=0) {
//            String id = UUID.randomUUID().toString();
//            query = "insert into calendarusermap (id,userid,cid) values( ?, ?, ? )";
//            updateJDBC(query, new Object[]{id, referenceId, calendarId});
//        }
//	}
//
//	@Override
//	public void deselectCalendar(String calendarId, String referenceId) {
//        String query = "delete from calendarusermap where userid=? and cid=? ";
//        updateJDBC(query, new Object[]{referenceId, calendarId});
//	}
}
