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

/**
 *
 * @author krawler
 */
public class CompanyPreferences implements java.io.Serializable {
      private String companyid;
      private Company company;
      private boolean  campaign;
      private boolean  lead;
      private boolean  account;
      private boolean  contact;
      private boolean  opportunity;
      private boolean  cases;
      private boolean  product;
      private boolean  activity;
      private boolean  editconvertedlead;
      private boolean  defaultleadtype;
      private int leadrouting;
      
    public CompanyPreferences() {
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    public boolean isCampaign() {
        return campaign;
    }

    public void setCampaign(boolean campaign) {
        this.campaign = campaign;
    }

    public boolean isLead() {
        return lead;
    }

    public void setLead(boolean lead) {
        this.lead = lead;
    }

    public boolean isAccount() {
        return account;
    }

    public void setAccount(boolean account) {
        this.account = account;
    }

    public boolean isContact() {
        return contact;
    }

    public void setContact(boolean contact) {
        this.contact = contact;
    }

    public boolean isOpportunity() {
        return opportunity;
    }

    public void setOpportunity(boolean opportunity) {
        this.opportunity = opportunity;
    }

    public boolean isCases() {
        return cases;
    }

    public void setCases(boolean cases) {
        this.cases = cases;
    }

    public boolean isProduct() {
        return product;
    }

    public void setProduct(boolean product) {
        this.product = product;
    }

    public boolean isActivity() {
        return activity;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    public boolean isEditconvertedlead() {
        return editconvertedlead;
    }

    public void setEditconvertedlead(boolean editconvertedlead) {
        this.editconvertedlead = editconvertedlead;
    }

    public boolean isDefaultleadtype() {
        return defaultleadtype;
    }

    public void setDefaultleadtype(boolean defaultleadtype) {
        this.defaultleadtype = defaultleadtype;
    }

    public int getLeadrouting() {
        return leadrouting;
    }

    public void setLeadrouting(int leadrouting) {
        this.leadrouting = leadrouting;
    }
 }
