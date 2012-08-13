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

public class pm_derivationtriggermap {
    private String id;
    private Integer triggertype;
    private pm_taskderivationmap taskderid;
    private pm_triggermaster triggerid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public pm_taskderivationmap getTaskderid() {
        return taskderid;
    }

    public void setTaskderid(pm_taskderivationmap taskderid) {
        this.taskderid = taskderid;
    }

    public pm_triggermaster getTriggerid() {
        return triggerid;
    }

    public void setTriggerid(pm_triggermaster triggerid) {
        this.triggerid = triggerid;
    }

    public Integer getTriggertype() {
        return triggertype;
    }

    public void setTriggertype(Integer triggertype) {
        this.triggertype = triggertype;
    }
}
