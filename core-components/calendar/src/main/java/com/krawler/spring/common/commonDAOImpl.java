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

package com.krawler.spring.common;

import com.krawler.spring.common.commonDAO;
import com.krawler.utils.json.base.JSONObject;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import krawler.taglib.json.util.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class commonDAOImpl implements commonDAO {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
	    this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
    
    public String gettimezone(HttpServletRequest request) {
//        JSONObject jobj = new JSONObject();
        List result = null;
        String str = "";
        try {
            KwlSpringJsonConverter kjs = new KwlSpringJsonConverter();
            String query = "select id,name,difference from timezone order by sortorder";
            List list = this.jdbcTemplate.queryForList(query);
            result = kjs.convertListToJSON(list);
//            jobj.put("data", result);
            str = "{\"data\":"+result+"}";
            str="{\'valid\':true,\'data\':"+str+"}";
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        return str;
    }
    
    public String getcountry(HttpServletRequest request) {
//        JSONObject jobj = new JSONObject();
        List result = null;
        String str = "";
        try {
            KwlSpringJsonConverter kjs = new KwlSpringJsonConverter();
            String query = "select id as id ,countryname as name from country";
            List list = this.jdbcTemplate.queryForList(query);
            result = kjs.convertListToJSON(list);
//            jobj.put("data", result);
            str = "{\"data\":"+result+"}";
             str="{\'valid\':true,\'data\':"+str+"}";
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        return str;
    }

    public String getsharingdata(HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        try {
            KwlSpringJsonConverter kjs = new KwlSpringJsonConverter();
            String query = "select userid,username,emailid,CONCAT(fname,' ',lname) as fullname from users";
            SqlRowSet rs = this.jdbcTemplate.queryForRowSet(query);
            JSONObject users = kjs.GetJsonForGrid(rs);
//            jobj.put("data", users);
//            jobj.put("valid", true);
//            str = jobj.toString();
            if(users!=null)
            	jobj = users;
            else
            	jobj.put("data", new JSONArray());
        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        return jobj.toString();
    }
}
