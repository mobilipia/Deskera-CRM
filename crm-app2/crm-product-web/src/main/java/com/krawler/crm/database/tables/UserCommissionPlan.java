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

public class UserCommissionPlan {
    private String id;
    private String plany;
    private User userid;
    private Commission commissionplan;
    private Boolean isactive;
    private Date affectfrom;
//    private Date createdon;
//    private Date updatedon;
    private Long createdOn;
    private Long updatedOn;
    private int deleted;
    private User usersByUpdatedbyid;
    private User usersByCreatedbyid;

    public String getPlany() {
        return plany;
    }

    public void setPlany(String planyear) {
        this.plany = planyear;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Long updatedOn) {
        this.updatedOn = updatedOn;
    }
    
    public Date getUpdatedon() {
        if(updatedOn!=null)
            return new Date(updatedOn);
        return null;
    }

    public void setUpdatedon(Date updatedon) {
        this.updatedOn = updatedon.getTime();
    }

    public User getUsersByCreatedbyid() {
        return usersByCreatedbyid;
    }

    public void setUsersByCreatedbyid(User usersByCreatedbyid) {
        this.usersByCreatedbyid = usersByCreatedbyid;
    }

    public User getUsersByUpdatedbyid() {
        return usersByUpdatedbyid;
    }

    public void setUsersByUpdatedbyid(User usersByUpdatedbyid) {
        this.usersByUpdatedbyid = usersByUpdatedbyid;
    }

    public Date getAffectfrom() {
        return affectfrom;
    }

    public void setAffectfrom(Date affectfrom) {
        this.affectfrom = affectfrom;
    }

    public Commission getCommissionplan() {
        return commissionplan;
    }

    public void setCommissionplan(Commission commissionplan) {
        this.commissionplan = commissionplan;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsactive() {
        return isactive;
    }

    public void setIsactive(Boolean isactive) {
        this.isactive = isactive;
    }

    public User getUserid() {
        return userid;
    }

    public void setUserid(User userid) {
        this.userid = userid;
    }
    
}
