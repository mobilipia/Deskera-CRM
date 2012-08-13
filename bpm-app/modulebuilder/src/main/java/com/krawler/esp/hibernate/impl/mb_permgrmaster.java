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

public class mb_permgrmaster {
    private String description;
    private String permgrname;
    private int permgrid;
    private int num;
    private mb_reportlist reportid;
//    private pm_taskmaster taskid;
    private Integer taskflag;

    public Integer getTaskflag() {
        return taskflag;
    }

    public void setTaskflag(Integer taskflag) {
        this.taskflag = taskflag;
    }

//    public pm_taskmaster getTaskid() {
//        return taskid;
//    }
//
//    public void setTaskid(pm_taskmaster taskid) {
//        this.taskid = taskid;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getPermgrid() {
        return permgrid;
    }

    public void setPermgrid(int permgrid) {
        this.permgrid = permgrid;
    }

    public String getPermgrname() {
        return permgrname;
    }

    public void setPermgrname(String permgrname) {
        this.permgrname = permgrname;
    }

    public mb_reportlist getReportid() {
        return reportid;
    }

    public void setReportid(mb_reportlist reportid) {
        this.reportid = reportid;
    }
}
