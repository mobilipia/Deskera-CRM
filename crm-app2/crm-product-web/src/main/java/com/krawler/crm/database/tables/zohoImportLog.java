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

/**
 *
 * @author krawler-user
 */
public class zohoImportLog {
private String zusername;
private String userid;
private String companyid;
private Date date;
private int leads;
private int accounts;
private int contacts;
private int potentials;
private int failedLeads;
private int failedContacts;
private int failedPotentials;
private int failedAccounts;
private int id;

    public zohoImportLog() {
    }

    public zohoImportLog(String zusername, String userid, String companyid, Date date, int leads, int accounts, int contacts, int potentials, int failedLeads, int failedContacts, int failedPotentials, int failedAccounts, int id) {
        this.zusername = zusername;
        this.userid = userid;
        this.companyid = companyid;
        this.date = date;
        this.leads = leads;
        this.accounts = accounts;
        this.contacts = contacts;
        this.potentials = potentials;
        this.failedLeads = failedLeads;
        this.failedContacts = failedContacts;
        this.failedPotentials = failedPotentials;
        this.failedAccounts = failedAccounts;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public int getAccounts() {
        return accounts;
    }

    public void setAccounts(int accounts) {
        this.accounts = accounts;
    }

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public int getContacts() {
        return contacts;
    }

    public void setContacts(int contacts) {
        this.contacts = contacts;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getFailedAccounts() {
        return failedAccounts;
    }

    public void setFailedAccounts(int failedAccounts) {
        this.failedAccounts = failedAccounts;
    }

    public int getFailedContacts() {
        return failedContacts;
    }

    public void setFailedContacts(int failedContacts) {
        this.failedContacts = failedContacts;
    }

    public int getFailedLeads() {
        return failedLeads;
    }

    public void setFailedLeads(int failedLeads) {
        this.failedLeads = failedLeads;
    }

    public int getFailedPotentials() {
        return failedPotentials;
    }

    public void setFailedPotentials(int failedPotentials) {
        this.failedPotentials = failedPotentials;
    }

    public int getLeads() {
        return leads;
    }

    public void setLeads(int leads) {
        this.leads = leads;
    }

    public int getPotentials() {
        return potentials;
    }

    public void setPotentials(int potentials) {
        this.potentials = potentials;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getZusername() {
        return zusername;
    }

    public void setZusername(String zusername) {
        this.zusername = zusername;
    }


}
