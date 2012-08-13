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
 * @author krawler
 */
public class LeadProducts {
    private String id;
    private CrmProduct productId;
    private CrmLead leadid;
//     private Date createdon;
    private Long createdOn;
     private int numbering;
     
    private String leadId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CrmLead getLeadid() {
        return leadid;
    }

    public void setLeadid(CrmLead leadid) {
        this.leadid = leadid;
    }

    public CrmProduct getProductId() {
        return productId;
    }

    public void setProductId(CrmProduct productId) {
        this.productId = productId;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }


    public Date getCreatedon() {
        if(createdOn!=null)
            return new Date(createdOn);
        return null;
    }

    public void setCreatedon(Date createdon) {
        this.createdOn = createdon.getTime();
    }

    public int getNumbering() {
        return numbering;
    }

    public void setNumbering(int numbering) {
        this.numbering = numbering;
    }

    /**
     * @return the leadId
     */
    public String getLeadId()
    {
        return leadId;
    }

    /**
     * @param leadId the leadId to set
     */
    public void setLeadId(String leadId)
    {
        this.leadId = leadId;
    }
    
}
