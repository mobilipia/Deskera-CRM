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
package com.krawler.customFieldMaster;

import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FieldManagerServiceImpl implements FieldManagerService {

    private fieldManagerDAO fieldManagerDAOobj;

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    @Override
    public JSONObject getRefComboNames() {
        JSONObject jobj1 = new JSONObject();
        try {
            JSONArray jarr = new JSONArray();
            JSONObject jobjTemp = new JSONObject();            
            List<Object[]> ll = fieldManagerDAOobj.getRefComboNames();
            for(Object[] item: ll){
                jobjTemp = new JSONObject();
                jobjTemp.put("id", item[0]);
                jobjTemp.put("name", item[1]);
                jobjTemp.put("moduleflag", "0");
//                jobjTemp.put("moduleflag", item[2]);
                jarr.put(jobjTemp);
            }
            jobjTemp = new JSONObject();
            jobjTemp.put("id", "users");
            jobjTemp.put("name", "Users");
            jobjTemp.put("moduleflag", "1");
            jarr.put(jobjTemp);
            jobj1.put("data", jarr);
        }  catch (JSONException ex) {
            Logger.getLogger(FieldManagerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FieldManagerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jobj1;
    }

}
