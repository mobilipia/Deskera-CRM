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

public class prereq implements Serializable {
    private String ruleid;
    private mb_reportlist moduleid;
    private mb_gridconfig attributeid;
    private String attribute;
    private String attributename;
    private int ruletype;
    private String value1;
    private String value2;
    private int filterflag;
    private String xtype;
    private int seq;

    public mb_gridconfig getAttributeid() {
        return attributeid;
    }

    public void setAttributeid(mb_gridconfig attributeid) {
        this.attributeid = attributeid;
    }

    public String getAttributename() {
        return attributename;
    }

    public void setAttributename(String attributename) {
        this.attributename = attributename;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public int getFilterflag() {
        return filterflag;
    }

    public void setFilterflag(int filterflag) {
        this.filterflag = filterflag;
    }

    public mb_reportlist getModuleid() {
        return moduleid;
    }

    public void setModuleid(mb_reportlist moduleid) {
        this.moduleid = moduleid;
    }

    public String getRuleid() {
        return ruleid;
    }

    public void setRuleid(String ruleid) {
        this.ruleid = ruleid;
    }

    public int getRuletype() {
        return ruletype;
    }

    public void setRuletype(int ruletype) {
        this.ruletype = ruletype;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getXtype() {
        return xtype;
    }

    public void setXtype(String xtype) {
        this.xtype = xtype;
    }
    
}
