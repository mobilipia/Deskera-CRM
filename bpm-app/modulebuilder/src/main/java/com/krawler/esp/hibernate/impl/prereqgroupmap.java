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

public class prereqgroupmap implements Serializable {
    private String id;
    private prereqgroup group1;
    private prereqgroup group2;
    private String ruletype;

    public prereqgroup getGroup1() {
        return group1;
    }

    public void setGroup1(prereqgroup group1) {
        this.group1 = group1;
    }

    public prereqgroup getGroup2() {
        return group2;
    }

    public void setGroup2(prereqgroup group2) {
        this.group2 = group2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRuletype() {
        return ruletype;
    }

    public void setRuletype(String ruletype) {
        this.ruletype = ruletype;
    }
    
}
