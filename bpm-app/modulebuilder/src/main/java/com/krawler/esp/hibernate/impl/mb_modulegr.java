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

public class mb_modulegr implements Serializable {
    private String id;
    private mb_reportlist basemodule;
    private mb_reportlist submodule;
    private String columnname;
    
    public mb_reportlist getBasemodule() {
        return basemodule;
    }

    public void setBasemodule(mb_reportlist basemodule) {
        this.basemodule = basemodule;
    }

    public String getColumnname() {
        return columnname;
    }

    public void setColumnname(String columnname) {
        this.columnname = columnname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public mb_reportlist getSubmodule() {
        return submodule;
    }

    public void setSubmodule(mb_reportlist submodule) {
        this.submodule = submodule;
    }
}
