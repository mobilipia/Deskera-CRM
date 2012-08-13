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

import com.krawler.utils.json.JSONSerializer;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

public class KwlSpringJsonConverter {
    public KwlSpringJsonConverter() {
    }

    /**
     * this function will return a jsonstring given a resultset
     * in a format that is required for WTF.Grid just use this inside the
     * ur jsp file and set the url for the grid datastore as that jsp file
     */
    public JSONObject GetJsonForGrid(SqlRowSet rs) {
        JSONObject jb = null;
        try {
            SqlRowSetMetaData rsmd = rs.getMetaData();
            JSONArray jArr = new JSONArray();
            while (rs.next()) {
                JSONObject jobj = new JSONObject();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if(rs.getObject(i)!=null)
                    jobj.put(rsmd.getColumnName(i), rs.getObject(i));
                    else
                    jobj.put(rsmd.getColumnName(i),"");
                }
                jArr.put(jobj);
            }
            if(jArr.length()>0)
            	jb = new JSONObject().put("data", jArr);
        } catch (Exception ex) {
            System.out.println("exception -->" + ex);

        }

        return jb;
    }

    public static List convertListToJSON(List objects) {
        List jsonObjects =  new ArrayList();
//        JSONArray jArr = new JSONArray();
        for (Object object : objects) {
           jsonObjects.add(JSONSerializer.toJSON(object));
//           jArr.put(JSONSerializer.toJSON(object));
        }
        return jsonObjects;
    }
}
