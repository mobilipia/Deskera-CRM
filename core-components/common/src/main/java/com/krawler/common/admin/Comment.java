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



import java.io.Serializable;
import java.util.Date;
import com.krawler.common.admin.*;



public class Comment implements java.io.Serializable {

    
    private String comment;
    private String Id;
    private User userId;
    //@@@ - Change fildname leadid
    private String leadid;
    private long postedon;
    private long updatedon;
    private String relatedto;
    private boolean deleted;
    public Comment(){
    }

    
    public Comment(String comment,String Id,User userId,String leadid , long postedon, long updatedon){

        
        this.comment=comment;
        this.Id=Id;
        this.userId=userId;
        this.leadid=leadid;
        this.postedon = postedon;
        this.updatedon = updatedon;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getId() {
        return this.Id;
    }
    public void setId(String id) {
        this.Id = id;
    }
    public User getuserId() {
        return this.userId;
    }
    public void setuserId(User id) {
        this.userId = id;
    }
    public String getleadid() {
        return this.leadid;
    }
    public void setleadid(String id) {
        this.leadid = id;
    }
    public long getPostedon() {
        return this.postedon;
    }

    public void setPostedon(long postedon) {
        this.postedon = postedon;
    }

    public long getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(long updatedon) {
        this.updatedon = updatedon;
    }

    public String getRelatedto() {
        return this.relatedto;
    }

    public void setRelatedto(String relatedto) {
        this.relatedto = relatedto;
    }

}
