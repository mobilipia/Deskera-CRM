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
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.krawler.calendar.calendarmodule.DeskeraCalendar;
import com.krawler.calendar.calendarmodule.JdbcCalendarDao;
import com.krawler.common.admin.User;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.common.KwlSpringJsonConverter;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;

public class JdbcCalendarUserDao extends JdbcCalendarDao implements CalendarUserDao {
    private static Log LOG = LogFactory.getLog(JdbcCalendarUserDao.class);

	@Override
	public List<CalendarPermission> getCalendarPermission(String calendarId) {
		String query = "select * from sharecalendarmap where cid=?";
		return queryJDBC(query, new Object[]{calendarId}, new CalendarPremissionRowMapper());
	}
	
	@Override
	public void deleteCalendarPermission(String calendarId) {
		String query = "delete from sharecalendarmap where cid=?";
		updateJDBC(query, new Object[]{calendarId});
	}

	@Override
	public void insertCalendarPermission(CalendarPermission perm) {
		if(perm.getId()==null)
			perm.setId(UUID.randomUUID().toString());
		if(perm.getUpdatedOn()==null)
			perm.setUpdatedOn(new Date());
		String query = "insert into sharecalendarmap (id, cid,userid,permissionlevel,updatedon) values( ?, ?, ?, ?)";
		updateJDBC(query, new Object[]{perm.getId(),perm.getCalendarId(),perm.getReferenceId(),perm.getPermissionLevel(), perm.getUpdatedOn().getTime()});
	}
	@Override
	public List<DeskeraCalendar> getSelectedCalendars(String referenceId) {
        String query = "select distinct m.cid from calendarusermap m where m.userid=?";
        List<String> list = queryJDBC(query, new Object[]{referenceId}, new RowMapper<String>() {

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				// TODO Auto-generated method stub
				return rs.getString("cid");
			}
		});
        
        return super.getCalendars(list.toArray(new String[]{}));
	}
	
	@Override
	public List<String> getSelectedCalendarIds(String referenceId) {
        String query = "select distinct cid from calendarusermap where userid=?";
        return queryJDBC(query, new Object[]{referenceId}, new RowMapper<String>(){

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("cid");
			}
		});
	}

	@Override
	public void selectCalendar(String calendarId, String referenceId) {
        String query = "select count(id) from calendarusermap where userid=? and cid=? ";
        int count = queryForIntJDBC(query, new Object[]{referenceId, calendarId});
        if(count<=0) {
            String id = UUID.randomUUID().toString();
            query = "insert into calendarusermap (id,userid,cid) values( ?, ?, ? )";
            updateJDBC(query, new Object[]{id, referenceId, calendarId});
        }
	}

	@Override
	public void deselectCalendar(String calendarId, String referenceId) {
        String query = "delete from calendarusermap where userid=? and cid=? ";
        updateJDBC(query, new Object[]{referenceId, calendarId});
	}

	@Override
	public void assignCalendar(User user, DeskeraCalendar cal) {
		String query = "update calendars set userid=? where cid=?";
		updateJDBC(query, new Object[]{user.getUserID(),cal.getId()});
	}

	@Override
	public List<String> getCalendarIds(User user) {
		String query = "select cid from calendars where userid=?";
		return queryJDBC(query, new Object[]{user.getUserID()}, new RowMapper<String>(){

			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getString("cid");
			}
		});
	}

	@Override
	public boolean hasDefaultCalendar(User user) {
		String query = "select count(cid) from calendars where userid=? and caltype=?";
		return queryForIntJDBC(query, new Object[]{user.getUserID(), DeskeraCalendar.CAL_DEFAULT}) > 0;
	}
}
