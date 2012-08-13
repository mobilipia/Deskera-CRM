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

import java.util.Date;

public class TargetListTargets implements java.io.Serializable {
    private String id;
    private String fname;
    private String lname;
    private String emailid;
    private TargetList targetlistid;
//    private Date modifiedon;
//    private Date createdon;
    private Long modifiedOn;
    private Long createdOn;
    private int deleted;
    private int relatedto;
    private String relatedid;

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

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public Date getModifiedon() {
        if(modifiedOn!=null)
            return new Date(modifiedOn);
        return null;
    }

    public void setModifiedon(Date modifiedon) {
        this.modifiedOn = modifiedon.getTime();
    }

    public TargetList getTargetlistid() {
        return targetlistid;
    }

    public void setTargetlistid(TargetList targetlistid) {
        this.targetlistid = targetlistid;
    }

    public int getRelatedto() {
        return relatedto;
    }

    public void setRelatedto(int relatedto) {
        this.relatedto = relatedto;
    }

    public String getRelatedid() {
        return relatedid;
    }

    public void setRelatedid(String relatedid) {
        this.relatedid = relatedid;
    }
}
