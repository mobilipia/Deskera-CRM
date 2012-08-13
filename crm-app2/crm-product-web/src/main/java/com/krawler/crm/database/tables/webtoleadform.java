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
 * @author krawler
 */
public class webtoleadform {

    int formid;
    Company companyid;
    String formname;
    String formdomain;
    String redirecturl;
    String formfield;
    User leadowner;
    Date lastupdatedon;

    public User getLeadowner() {
        return leadowner;
    }

    public void setLeadowner(User leadowner) {
        this.leadowner = leadowner;
    }
    

    public Company getCompanyid() {
        return companyid;
    }

    public void setCompanyid(Company companyid) {
        this.companyid = companyid;
    }

    public String getFormfield() {
        return formfield;
    }

    public void setFormfield(String formfield) {
        this.formfield = formfield;
    }


    public String getFormdomain() {
        return formdomain;
    }

    public void setFormdomain(String formdomain) {
        this.formdomain = formdomain;
    }

    public int getFormid() {
        return formid;
    }

    public void setFormid(int formid) {
        this.formid = formid;
    }

    public String getFormname() {
        return formname;
    }

    public void setFormname(String formname) {
        this.formname = formname;
    }

    public Date getLastupdatedon() {
        return lastupdatedon;
    }

    public void setLastupdatedon(Date lastupdatedon) {
        this.lastupdatedon = lastupdatedon;
    }

    

    public String getRedirecturl() {
        return redirecturl;
    }

    public void setRedirecturl(String redirecturl) {
        this.redirecturl = redirecturl;
    }



}
