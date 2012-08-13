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

/**
 *
 * @author krawler
 */
public class FieldComboData {
    private String id;
    private String value;
    private String fieldid;
    private FieldParams field;
    private Integer oldid;
    private int itemsequence; 
    private int deleteflag;
    public Integer getOldid() {
        return oldid;
    }

    public void setOldid(Integer oldid) {
        this.oldid = oldid;
    }

    public int getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(int deleteflag) {
        this.deleteflag = deleteflag;
    }
    
    public FieldParams getField() {
        return field;
    }

    public void setField(FieldParams field) {
        this.field = field;
    }

    public String getFieldid() {
        return fieldid;
    }

    public void setFieldid(String fieldid) {
        this.fieldid = fieldid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

	public int getItemsequence() {
		return itemsequence;
	}

	public void setItemsequence(int itemsequence) {
		this.itemsequence = itemsequence;
	}

}
