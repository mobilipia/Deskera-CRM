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
package com.krawler.esp.hibernate.impl;

import java.io.Serializable;
public class mb_moduleConfigMap implements Serializable {
    private mb_stdConfigs configid;
    private mb_reportlist moduleid;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public mb_stdConfigs getConfigid() {
        return configid;
    }

    public void setConfigid(mb_stdConfigs configid) {
        this.configid = configid;
    }

    public mb_reportlist getModuleid() {
        return moduleid;
    }

    public void setModuleid(mb_reportlist moduleid) {
        this.moduleid = moduleid;
    }
    
}
