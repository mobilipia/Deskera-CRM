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
import com.krawler.common.admin.DefaultHeader;

/**
 *
 * @author trainee
 */
public class LeadConversionMappings {
    private String id;
    private DefaultHeader modulefield;
    private DefaultHeader leadfield;
    private Company company; //company
    private boolean defaultMapping;
    private String moduleName;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DefaultHeader getModulefield() {
        return modulefield;
    }

    public void setModulefield(DefaultHeader modulefield) {
        this.modulefield = modulefield;
    }

    public DefaultHeader getLeadfield() {
        return leadfield;
    }

    public void setLeadfield(DefaultHeader leadfield) {
        this.leadfield = leadfield;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public boolean isDefaultMapping() {
        return defaultMapping;
    }

    public void setDefaultMapping(boolean defaultMapping) {
        this.defaultMapping = defaultMapping;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

}
