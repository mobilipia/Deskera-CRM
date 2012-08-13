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
 * @author training
 */
public class DefaultMasterItem implements java.io.Serializable {

    private String ID;
    private String value;
    private CrmCombomaster crmCombomaster;
    private CrmCombodata crmCombodata;
    private Company company;
    private String mainID;
//    private String aliasName;
    private int isEdit;
    private int itemsequence;
    private String percentStage;
    private int validflag;
    private int deleteflag;

    public String getPercentStage() {
        return percentStage;
    }

    public int getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(int deleteflag) {
        this.deleteflag = deleteflag;
    }

    public void setPercentStage(String percentStage) {
        this.percentStage = percentStage;
    }
    
    public int isIsEdit() {
        return isEdit;
    }

    public void setIsEdit(int isEdit) {
        this.isEdit = isEdit;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public CrmCombomaster getCrmCombomaster() {
        return crmCombomaster;
    }

    public void setCrmCombomaster(CrmCombomaster crmCombomaster) {
        this.crmCombomaster = crmCombomaster;
    }
    
//    public String getAliasName() {
//        return aliasName;
//    }
//
//    public void setAliasName(String aliasName) {
//        this.aliasName = aliasName;
//    }

    public CrmCombodata getCrmCombodata() {
        return crmCombodata;
    }

    public void setCrmCombodata(CrmCombodata crmCombodata) {
        this.crmCombodata = crmCombodata;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getMainID() {
        return mainID;
    }

    public void setMainID(String mainID) {
        this.mainID = mainID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getValidflag() {
        return validflag;
    }

    public void setValidflag(int validflag) {
        this.validflag = validflag;
    }
    
    public int getItemsequence() {
		return itemsequence;
	}

	public void setItemsequence(int itemsequence) {
		this.itemsequence = itemsequence;
	}
    
}
