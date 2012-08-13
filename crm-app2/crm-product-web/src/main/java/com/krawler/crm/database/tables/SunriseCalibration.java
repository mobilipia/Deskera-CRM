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

public class SunriseCalibration {
    private String id;
    private Long calon;
    private Long caldue;
    private String particulars;
    private String srcal;
    private String contactperson;
    private String contactnumber;
    private String machinetype;
    private String paymentstatus;
    private String machinecalno;
    private String state;
    private User usersByUpdatedbyid;
    private User usersByCreatedbyid;
    private Long createdOn;
    private Long updatedOn;
    private Date updatedon;
    private Date createdon;
    private int deleteflag;

    public Long getCaldue() {
        return caldue;
    }
    
    public Date getCalDue() {
        if(this.caldue!=null)
            return new Date(this.caldue);
        return null;
    }
    
    public void setCalDue(Date calDue) {
        this.caldue = calDue.getTime();
    }

    public void setCaldue(Long caldue) {
        this.caldue = caldue;
    }

    public Long getCalon() {
        return calon;
    }
    
    public Date getCalOn() {
        if(this.calon!=null)
            return new Date(this.calon);
        return null;
    }
    
    public void setCalOn(Date calOn) {
        this.calon = calOn.getTime();
    }

    public void setCalon(Long calon) {
        this.calon = calon;
    }
    
    public String getContactnumber() {
        return contactnumber;
    }

    public void setContactnumber(String contactnumber) {
        this.contactnumber = contactnumber;
    }

    public String getContactperson() {
        return contactperson;
    }

    public void setContactperson(String contactperson) {
        this.contactperson = contactperson;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMachinetype() {
        return machinetype;
    }

    public void setMachinetype(String machinetype) {
        this.machinetype = machinetype;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }

    public String getSrcal() {
        return srcal;
    }

    public void setSrcal(String srcal) {
        this.srcal = srcal;
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
        if(this.updatedOn!=null)
            updatedon= new Date(this.updatedOn);
        return updatedon;
    }

    public void setUpdatedon(Date updatedon) {
        this.updatedOn = updatedon.getTime();
    }
    public Date getCreatedon() {
        if(this.createdOn!=null)
            createdon=new Date(this.createdOn);
        return createdon;
    }
    public void setCreatedon(Date createdon) {
        this.createdOn = createdon.getTime();
    }

    public int getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(int deleteflag) {
        this.deleteflag = deleteflag;
    }

    public String getMachinecalno() {
        return machinecalno;
    }

    public void setMachinecalno(String machinecalno) {
        this.machinecalno = machinecalno;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    
}
