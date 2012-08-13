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

public class prereqgroup implements Serializable {
//    private String id;
    private String groupid;
    private mb_reportlist moduleid;
    private mb_rolemaster roleid;
    private int seq;
    private int filterflag;

    public int getFilterflag() {
        return filterflag;
    }

    public void setFilterflag(int filterflag) {
        this.filterflag = filterflag;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

//    public groupmap getGroupid() {
//        return groupid;
//    }
//
//    public void setGroupid(groupmap groupid) {
//        this.groupid = groupid;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

    public mb_reportlist getModuleid() {
        return moduleid;
    }

    public void setModuleid(mb_reportlist moduleid) {
        this.moduleid = moduleid;
    }

    public mb_rolemaster getRoleid() {
        return roleid;
    }

    public void setRoleid(mb_rolemaster roleid) {
        this.roleid = roleid;
    }

//    public mb_permmaster getPermid() {
//        return permid;
//    }
//
//    public void setPermid(mb_permmaster permid) {
//        this.permid = permid;
//    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
