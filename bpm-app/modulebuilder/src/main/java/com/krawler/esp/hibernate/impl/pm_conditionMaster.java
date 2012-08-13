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

public class pm_conditionMaster {
    private String ruleid;
//    private pm_taskmaster taskid;
    private pm_taskderivationmap taskderid;
    private mb_gridconfig attributeid;
    private String attribute;
    private String attributename;
    private int ruletype;
    private String value1;
    private String value2;
    private String xtype;
    private int seq;
    private String conditiontype;

    public String getConditiontype() {
        return conditiontype;
    }

    public void setConditiontype(String conditiontype) {
        this.conditiontype = conditiontype;
    }

    public pm_taskderivationmap getTaskderid() {
        return taskderid;
    }

    public void setTaskderid(pm_taskderivationmap taskderid) {
        this.taskderid = taskderid;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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
