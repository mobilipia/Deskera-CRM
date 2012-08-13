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
package com.krawler.esp.handlers;

import com.krawler.common.util.StringUtil;
import com.krawler.utils.json.JSONArray;
import com.krawler.utils.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JsonArrayHandler {
    private static final Log logger = LogFactory.getLog(JsonArrayHandler.class);
    public static String getFileteredJson(String jsonString,String[] var){
        try {
            
            for(int c=0;c<var.length;c++) {
                JSONArray jarr = new JSONArray();
                jarr.add(jsonString);
                jsonString = getextaractedString(jarr,var[c]);
                if(jsonString.length()>0){
                    jsonString = jsonString.substring(1,jsonString.length()-1);
                }
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return jsonString;
    }

    private static String getextaractedString(JSONArray conf,String filter) {
        String ret = "";
        try{
            for(int i = 0; i < conf.size(); i++){
                JSONObject temp = conf.getJSONObject(i);
                if(temp.has("name") && temp.getString("name").compareToIgnoreCase(filter) == 0){
                    conf.remove(temp);
                } else if(temp.has("items")){
                    JSONArray items = temp.getJSONArray("items");

                    if(items.size() != 0){
                        ret = JsonArrayHandler.getextaractedString(items,filter);
                    }
                }
            }
            ret=conf.toString();
        } catch (Exception e){
            logger.warn(e.getMessage(), e);
        }
        return ret;
    }
}
