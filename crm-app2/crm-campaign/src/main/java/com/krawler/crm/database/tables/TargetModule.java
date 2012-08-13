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

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import java.util.Date;

/**
 *
 * @author trainee
 */
public class TargetModule {

    private String id;
    private User usersByUpdatedbyid;
    private User usersByCreatedbyid;
    private Company company;
    private User usersByUserid;
    private String firstname;
    private String lastname;
    private String companyname;
    private String phoneno;
    private String mobileno;
    private String email;
    private String website;
    private String address;
    private String birthdate;
    private String department;
    private String description;
//    private Date updatedon;
//    private Date createdon;
    private Long updatedOn;
    private Long createdOn;
    private int deleteflag;
    private int validflag;
    private boolean isarchive;     
    private String cellstyle;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUsersByUpdatedbyid() {
        return this.usersByUpdatedbyid;
    }

    public void setUsersByUpdatedbyid(User usersByUpdatedbyid) {
        this.usersByUpdatedbyid = usersByUpdatedbyid;
    }

    public User getUsersByCreatedbyid() {
        return this.usersByCreatedbyid;
    }

    public void setUsersByCreatedbyid(User usersByCreatedbyid) {
        this.usersByCreatedbyid = usersByCreatedbyid;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public User getUsersByUserid() {
        return this.usersByUserid;
    }

    public void setUsersByUserid(User usersByUserid) {
        this.usersByUserid = usersByUserid;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCompanyname() {
        return this.companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }
    
    public String getPhoneno() {
        return this.phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getMobileno() {
        return this.mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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
            return new Date(this.updatedOn);
        return null;
    }

    public void setUpdatedon(Date updatedon) {
        this.updatedOn = updatedon.getTime();
    }

    public Date getCreatedon() {
        if(this.createdOn!=null)
            return new Date(this.createdOn);
        return null;
    }

    public void setCreatedon(Date createdon) {
        this.createdOn = createdon.getTime();
    }

    public int getDeleteflag() {
        return this.deleteflag;
    }

    public void setDeleteflag(int deleteflag) {
        this.deleteflag = deleteflag;
    }

    public int getValidflag() {
        return this.validflag;
    }

    public void setValidflag(int validflag) {
        this.validflag = validflag;
    }

    public boolean getIsarchive() {
        return this.isarchive;
    }

    public void setIsarchive(boolean isarchive) {
        this.isarchive = isarchive;
    }

    public String getStringObj(String objName){
        String obj = "";
        if(objName.equals("fname")){
            obj = this.firstname;
        }
        else if(objName.equals("lname")){
            obj = this.lastname;
        }
        else if(objName.equals("phone")){
            obj = this.phoneno;
        }
        else if(objName.equals("email")){
            obj = this.email;
        }
        else if(objName.equals("rname")){
            obj = this.companyname;
        }
        return obj;
    }

    public String getCellstyle() {
        return cellstyle;
    }

    public void setCellstyle(String cellstyle) {
        this.cellstyle = cellstyle;
    }

    
}
