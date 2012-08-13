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

package com.krawler.br.utils;

import com.krawler.br.ProcessException;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.PropertyUtils;
/**
 *
 * @author Vishnu Kant Gupta
 */
public class Helper {
    public static final HashMap DEFINITION = new HashMap();
    static{
        DEFINITION.put("assign", new String[][]{null,{"container", "object","false"},{"key","string","false"},{"val","object","false"}});
        DEFINITION.put("assignCurrentDate", new String[][]{{"currentdate","date","false"},{"container", "object","false"},{"key","string","false"}});
        DEFINITION.put("assignUUID", new String[][]{{"uuid","string", "false"},{"container", "object","false"},{"key","string","false"}});
        DEFINITION.put("obj2Json", new String[][]{{"jobj","object", "false"},{"obj", "KwlReturnObject","false"}});
    }

    public void assign(Object container, String key, Object val) throws ProcessException{
        if(container==null){
            throw new ProcessException("container not found for key: "+key);
        }

        if(container instanceof java.util.Map){
            ((java.util.Map)container).put(key,val);
        }else if(container instanceof JSONObject){
            try {
                ((JSONObject) container).put(key, val);
            } catch (JSONException ex) {
                throw new ProcessException(ex);
            }
        }else{
            try {
                PropertyUtils.setProperty(container, key, val);
            } catch (Exception ex) {
                throw new ProcessException("property not writable: "+key,ex);
            }
        }
    }

    public Date assignCurrentDate(Object container, String key) throws ProcessException{
        Date date=new Date();
        assign(container, key, date);
        return date;
    }
    public String assignUUID(Object container, String key) throws ProcessException{
        String id = java.util.UUID.randomUUID().toString();
        assign(container, key, id);
        return id;
    }

    public JSONObject obj2Json(KwlReturnObject obj){
        JSONObject jobj = new JSONObject();
        try {
            jobj.put("msg", obj.getMsg());
            jobj.put("success", obj.isSuccessFlag());
            if(!obj.isSuccessFlag()){
                jobj.put("errorcode", obj.getErrorCode());
            }
        } catch (Exception ex) {
        }
        return jobj;
    }
}
