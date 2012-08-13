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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

public class CalendarPremissionRowMapper implements RowMapper<CalendarPermission> {

	@Override
	public CalendarPermission mapRow(ResultSet rs, int rowNum) throws SQLException {
		CalendarPermission perm = new CalendarPermission();
		perm.setId(rs.getString("id"));
		perm.setCalendarId(rs.getString("cid"));
		perm.setPermissionLevel(rs.getInt("permissionlevel"));
		perm.setUpdatedOn(getDate(rs.getLong("updatedon")));
		perm.setReferenceId(rs.getString("userid"));
		perm.setColorCode(rs.getString("colorcode"));
		return perm;
	}

    private Date getDate(Long val){
    	return (val!=null?new Date(val):null);
    }
}
