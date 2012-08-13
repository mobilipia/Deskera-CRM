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
package com.krawler.crm.database.tables; 

import com.krawler.common.admin.User;
import java.util.Date;

public class TargetList implements java.io.Serializable {
    private String id;
    private String name;
    private String description;
    private User creator;
//    private Date createdon;
//    private Date modifiedon;
    private Long createdOn;
    private Long modifiedOn;
    private int deleted;
    private int saveflag;
    private String targetsource;

    public int getSaveflag() {
        return saveflag;
    }

    public void setSaveflag(int saveflag) {
        this.saveflag = saveflag;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }
    
    public Date getCreatedon() {
        if(createdOn!=null)
            return new Date(createdOn);
        return null;
    }

    public void setCreatedon(Date createdon) {
        this.createdOn = createdon.getTime();
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getModifiedon() {
        if(modifiedOn!=null)
            return new Date(modifiedOn);
        return null;
    }

    public void setModifiedon(Date modifiedon) {
        this.modifiedOn = modifiedon.getTime();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetsource() {
        return targetsource;
    }

    public void setTargetsource(String targetsource) {
        this.targetsource = targetsource;
    }
    
}
