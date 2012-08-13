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
package com.krawler.esp.hibernate.impl;

import java.io.Serializable;
public class mb_forms implements Serializable {
    private String formid;
    private String name;
    private String data;
    
    private Boolean deployedInd;
    
    private Boolean abstractInd;
    
    private String companyid;
    
    private mb_reportlist moduleid;

    public mb_reportlist getModuleid() {
        return moduleid;
    }

    public void setModuleid(mb_reportlist moduleid) {
        this.moduleid = moduleid;
    }

    public String getData() {
        return data;
    }

    public String getFormid() {
        return formid;
    }

    public String getName() {
        return name;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the deployedInd
     */
    public Boolean getDeployedInd()
    {
        return deployedInd;
    }

    /**
     * @param deployedInd the deployedInd to set
     */
    public void setDeployedInd(Boolean deployedInd)
    {
        this.deployedInd = deployedInd;
    }

    /**
     * @return the abstractInd
     */
    public Boolean getAbstractInd()
    {
        return abstractInd;
    }

    /**
     * @param abstractInd the abstractInd to set
     */
    public void setAbstractInd(Boolean abstractInd)
    {
        this.abstractInd = abstractInd;
    }

    /**
     * @return the companyid
     */
    public String getCompanyid()
    {
        return companyid;
    }

    /**
     * @param companyid the companyid to set
     */
    public void setCompanyid(String companyid)
    {
        this.companyid = companyid;
    }
}
