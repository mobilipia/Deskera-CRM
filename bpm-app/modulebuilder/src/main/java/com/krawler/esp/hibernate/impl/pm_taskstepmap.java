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

public class pm_taskstepmap {
    private String id;
    private String stepid;
    private Integer steptype;
    private pm_taskmaster taskid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStepid() {
        return stepid;
    }

    public void setStepid(String stepid) {
        this.stepid = stepid;
    }

    public Integer getSteptype() {
        return steptype;
    }

    public void setSteptype(Integer steptype) {
        this.steptype = steptype;
    }

    public pm_taskmaster getTaskid() {
        return taskid;
    }

    public void setTaskid(pm_taskmaster taskid) {
        this.taskid = taskid;
    }
}
