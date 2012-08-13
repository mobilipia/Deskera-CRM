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
package com.krawler.common.admin;

public class FieldParams {
    private String id;
    private int maxlength;
    private int isessential;
    private int fieldtype;
    private int validationtype;
    private String customregex;
    private String fieldname;
    private String fieldlabel;
    private String companyid;
    private int moduleid;
    private String iseditable;
    private String comboname;
    private String comboid;
    private int moduleflag;
    private int colnum;
    private int refcolnum;
    private Company company;
    private Integer oldid;
    
    // following 3 fields added for auto number custom column
    private int startingnumber;
    private String prefix;
    private String suffix;
    // End
    public Integer getOldid() {
        return oldid;
    }

    public void setOldid(Integer oldid) {
        this.oldid = oldid;
    }



    public int getRefcolnum() {
        return refcolnum;
    }

    public void setRefcolnum(int refcolnum) {
        this.refcolnum = refcolnum;
    }


    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getColnum() {
        return colnum;
    }

    public void setColnum(int colnum) {
        this.colnum = colnum;
    }

    public String getComboid() {
        return comboid;
    }

    public void setComboid(String comboid) {
        this.comboid = comboid;
    }

    public String getComboname() {
        return comboname;
    }

    public void setComboname(String comboname) {
        this.comboname = comboname;
    }

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public String getCustomregex() {
        return customregex;
    }

    public void setCustomregex(String customregex) {
        this.customregex = customregex;
    }

    public String getFieldlabel() {
        return fieldlabel;
    }

    public void setFieldlabel(String fieldlabel) {
        this.fieldlabel = fieldlabel;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public int getFieldtype() {
        return fieldtype;
    }

    public void setFieldtype(int fieldtype) {
        this.fieldtype = fieldtype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIseditable() {
        return iseditable;
    }

    public void setIseditable(String iseditable) {
        this.iseditable = iseditable;
    }

    public int getIsessential() {
        return isessential;
    }

    public void setIsessential(int isessential) {
        this.isessential = isessential;
    }

    public int getMaxlength() {
        return maxlength;
    }

    public void setMaxlength(int maxlength) {
        this.maxlength = maxlength;
    }

    public int getModuleflag() {
        return moduleflag;
    }

    public void setModuleflag(int moduleflag) {
        this.moduleflag = moduleflag;
    }

    public int getModuleid() {
        return moduleid;
    }

    public void setModuleid(int moduleid) {
        this.moduleid = moduleid;
    }

    public int getValidationtype() {
        return validationtype;
    }

    public void setValidationtype(int validationtype) {
        this.validationtype = validationtype;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getStartingnumber() {
        return startingnumber;
    }

    public void setStartingnumber(int startingnumber) {
        this.startingnumber = startingnumber;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
