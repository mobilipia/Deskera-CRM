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

public class EmailMarkteingTargetList implements java.io.Serializable {
    private String id;
    private TargetList targetlistid;
    private EmailMarketing emailmarketingid;
//    private Date modifiedon;
    private Long modifiedOn;
    private int deleted;

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public EmailMarketing getEmailmarketingid() {
        return emailmarketingid;
    }

    public void setEmailmarketingid(EmailMarketing emailmarketingid) {
        this.emailmarketingid = emailmarketingid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
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
}
