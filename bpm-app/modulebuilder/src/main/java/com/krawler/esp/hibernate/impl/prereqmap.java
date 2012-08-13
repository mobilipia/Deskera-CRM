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

public class prereqmap implements Serializable {
    private String id;
    private prereq rule1;
    private prereq rule2;
    private prereqgroup groupid;
    private String ruletype;

    public prereqgroup getGroupid() {
        return groupid;
    }

    public void setGroupid(prereqgroup groupid) {
        this.groupid = groupid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public prereq getRule1() {
        return rule1;
    }

    public void setRule1(prereq rule1) {
        this.rule1 = rule1;
    }

    public prereq getRule2() {
        return rule2;
    }

    public void setRule2(prereq rule2) {
        this.rule2 = rule2;
    }

    public String getRuletype() {
        return ruletype;
    }

    public void setRuletype(String ruletype) {
        this.ruletype = ruletype;
    }
    
}
