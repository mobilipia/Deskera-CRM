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

import java.util.Date;
import java.util.Set;

/**
 *
 * @author krawler-user
 */
public class Company {
    private String companyID;
    private String companyLogo;
    private String companyName;
    private String subDomain;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private String faxNumber;
    private String website;
    private User creator;
    private int deleted;
    private String emailID;
    private Date createdOn;
    private Long createdon;
    private Date modifiedOn;
    private Long modifiedon;
    private KWLTimeZone timeZone;
    private KWLCurrency currency;
    private Country country;
    private Language language;
    private Set<CompanyHoliday> holidays;
    private int migrationstatus;
    private int notificationtype;
    private boolean activated;
    
    private Long companyId;

    public int getMigrationstatus() {
        return migrationstatus;
    }

    public void setMigrationstatus(int migrationstatus) {
        this.migrationstatus = migrationstatus;
    }
    
    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public KWLTimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(KWLTimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public KWLCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(KWLCurrency currency) {
        this.currency = currency;
    }

    public String getSubDomain() {
        return subDomain;
    }

    public void setSubDomain(String subDomain) {
        this.subDomain = subDomain;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getCreatedOn() {
    	if(this.createdon!=null)
        createdOn=new Date(this.createdon);
    	return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdon = createdOn.getTime();
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public Long getModifiedon() {
		return modifiedon;
	}

	public void setModifiedon(Long modifiedon) {
		this.modifiedon = modifiedon;
	}

	public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Long getCreatedon() {
		return createdon;
	}

	public void setCreatedon(Long createdon) {
		this.createdon = createdon;
	}

	public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public Set<CompanyHoliday> getHolidays() {
        return holidays;
    }

    public void setHolidays(Set<CompanyHoliday> holidays) {
        this.holidays = holidays;
    }

    public User getCreator() {
        return creator;
}

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public int isDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    /**
     * @return the companyId
     */
    public Long getCompanyId()
    {
        return companyId;
    }

    /**
     * @param companyId the companyId to set
     */
    public void setCompanyId(Long companyId)
    {
        this.companyId = companyId;
    }

    public int getNotificationtype() {
        return notificationtype;
    }

    public void setNotificationtype(int notificationtype) {
        this.notificationtype = notificationtype;
    }

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
