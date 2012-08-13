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

/**
 *
 * @author mosin
 */
import java.io.Serializable;
public class mb_configmaster implements Serializable {
    private String configid;
    private int configtype;
    private String name;
    private int flag;

    public String getConfigid() {
        return configid;
    }

    public void setConfigid(String configid) {
        this.configid = configid;
    }

    public void setConfigtype(int configtype) {
        this.configtype = configtype;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getConfigtype() {
        return configtype;
    }

    public int getFlag() {
        return flag;
    }

    public String getName() {
        return name;
    }

}
