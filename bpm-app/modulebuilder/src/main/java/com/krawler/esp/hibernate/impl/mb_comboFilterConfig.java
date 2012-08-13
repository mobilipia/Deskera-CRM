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


public class mb_comboFilterConfig {
    private mb_gridconfig gridconfigid;
    private String fieldname;
    private String xtype;
    private String reftable;
    private String refcol;
    private String id;
    private mb_reportlist refmoduleid;
    private String displayfield;

    public String getDisplayfield() {
        return displayfield;
    }

    public void setDisplayfield(String displayfield) {
        this.displayfield = displayfield;
    }

    public mb_reportlist getRefmoduleid() {
        return refmoduleid;
    }

    public void setRefmoduleid(mb_reportlist refmoduleid) {
        this.refmoduleid = refmoduleid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public mb_gridconfig getGridconfigid() {
        return gridconfigid;
    }

    public void setGridconfigid(mb_gridconfig gridconfigid) {
        this.gridconfigid = gridconfigid;
    }

    public String getRefcol() {
        return refcol;
    }

    public void setRefcol(String refcol) {
        this.refcol = refcol;
    }

    public String getReftable() {
        return reftable;
    }

    public void setReftable(String reftable) {
        this.reftable = reftable;
    }

    public String getXtype() {
        return xtype;
    }

    public void setXtype(String xtype) {
        this.xtype = xtype;
    }
}
