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

public class pm_taskderivationmap {
    private String id;
    private pm_taskmaster childtaskid;
    private pm_taskmaster parenttaskid;
    private pm_derivationmaster derivationid;

    public pm_taskmaster getChildtaskid() {
        return childtaskid;
    }

    public void setChildtaskid(pm_taskmaster childtaskid) {
        this.childtaskid = childtaskid;
    }

    public pm_derivationmaster getDerivationid() {
        return derivationid;
    }

    public void setDerivationid(pm_derivationmaster derivationid) {
        this.derivationid = derivationid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public pm_taskmaster getParenttaskid() {
        return parenttaskid;
    }

    public void setParenttaskid(pm_taskmaster parenttaskid) {
        this.parenttaskid = parenttaskid;
    }
}
