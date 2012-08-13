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

import com.krawler.calendar.calendarmodule.DeskeraCalendar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

public class CalendarRowMapper implements RowMapper<DeskeraCalendar> {

	@Override
	public DeskeraCalendar mapRow(ResultSet rs, int line) throws SQLException {
		DeskeraCalendar cal = new DeskeraCalendar();
        cal.setId(rs.getString("cid"));
        cal.setName(rs.getString("cname"));
        cal.setColorCode(rs.getString("colorcode"));
        cal.setDescription(rs.getString("description"));
        cal.setLocation(rs.getString("location"));
        cal.setTimeZone(rs.getString("timezone"));
        cal.setType(rs.getInt("caltype"));
        cal.setCreatedOn(getDate(rs.getLong("createdon")));
        cal.setUpdatedOn(getDate(rs.getLong("updatedon")));
        return cal;
	}
	
    private Date getDate(Long val){
    	return (val!=null?new Date(val):null);
    }
}
