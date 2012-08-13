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

public class CustomReportList {
    private int rno;
    private String rname;
//    private String runiquename;
    private String rdescription;
    private String rcategory;
    private String rfilterjson;
    private User usersByUpdatedbyid;
    private User usersByCreatedbyid;
    private Long createdon;
    private Long updatedon;
    private int deleteflag;
    private boolean summaryflag;
    private boolean groupflag;
    private String groupfield;

    public String getGroupfield() {
        return groupfield;
    }

    public void setGroupfield(String groupfield) {
        this.groupfield = groupfield;
    }

    public boolean isGroupflag() {
        return groupflag;
    }

    public void setGroupflag(boolean groupflag) {
        this.groupflag = groupflag;
    }

    public String getRfilterjson() {
        return rfilterjson;
    }

    public void setRfilterjson(String rfilterjson) {
        this.rfilterjson = rfilterjson;
    }

    public String getRcategory() {
        return rcategory;
    }

    public void setRcategory(String rcategory) {
        this.rcategory = rcategory;
    }
    
    public String getRdescription() {
        return rdescription;
    }

    public void setRdescription(String rdescription) {
        this.rdescription = rdescription;
    }   

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public int getRno() {
        return rno;
    }

    public void setRno(int rno) {
        this.rno = rno;
    }

//    public String getRuniquename() {
//        return runiquename;
//    }
//
//    public void setRuniquename(String runiquename) {
//        this.runiquename = runiquename;
//    }

    public Long getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Long createdon) {
        this.createdon = createdon;
    }

    public int getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(int deleteflag) {
        this.deleteflag = deleteflag;
    }

    public Long getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(Long updatedon) {
        this.updatedon = updatedon;
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

    public boolean isSummaryflag() {
        return summaryflag;
    }

    public void setSummaryflag(boolean summaryflag) {
        this.summaryflag = summaryflag;
    }
}
