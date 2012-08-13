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

import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
public class CalendarEventRowMapper  implements RowMapper<CalendarEvent> {
    @Override
	public CalendarEvent mapRow(ResultSet rs, int line) throws SQLException {
		CalendarEvent cal = new CalendarEvent();
		cal.setId(rs.getString("eid"));
        cal.setCalendarId(rs.getString("cid"));
        cal.setDescription(rs.getString("descr"));
        cal.setAllDay(rs.getBoolean("allday"));
        cal.setLocation(rs.getString("location"));
        cal.setPriority(rs.getInt("priority"));
        cal.setRepeatTill(getDate(rs.getLong("recend")));
        cal.setRepeatPattern(rs.getString("recpattern"));
        cal.setResources(rs.getString("resources"));
        cal.setShowAs(rs.getInt("showas"));
        cal.setStartTime(getDate(rs.getLong("startdate")));
       	cal.setEndTime(getDate(rs.getLong("enddate")));
        cal.setCreatedOn(new Date(rs.getLong("createdon")));
        cal.setUpdatedOn(new Date(rs.getLong("updatedon")));
        cal.setSubject(rs.getString("subject"));
        return cal;
	}
    
    private Date getDate(Long val){
    	return (val!=null?new Date(val):null);
    }
}
