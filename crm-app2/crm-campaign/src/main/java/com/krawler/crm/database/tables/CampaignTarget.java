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

/**
 *
 * @author trainee
 */
public class CampaignTarget {
    private String id;
    private TargetList targetlist;
    private CrmCampaign campaign;
    private User creator;
//    private Date createdon;
    private Long createdOn;
    private int deleted;

    public CrmCampaign getCampaign() {
        return campaign;
    }

    public Date getCreatedon() {
        if(createdOn!=null)
            return new Date(createdOn);
        return null;
    }

    public User getCreator() {
        return creator;
    }

    public int getDeleted() {
        return deleted;
    }

    public String getId() {
        return id;
    }

    public TargetList getTargetlist() {
        return targetlist;
    }

    public void setCampaign(CrmCampaign campaign) {
        this.campaign = campaign;
    }

    public void setCreatedon(Date createdon) {
        this.createdOn = createdon.getTime();
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTargetlist(TargetList targetlist) {
        this.targetlist = targetlist;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }


}
