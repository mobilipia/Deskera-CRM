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
import java.util.Set;

public class CampaignLog implements java.io.Serializable {
    private String id;
    private String targettrackerkey;
    private String targettype;
    private String activitytype;
    private String relatedtype;
    private CrmCampaign campaignid;
    private EmailMarketing emailmarketingid;
    private TargetList targetlistid;
    private TargetListTargets targetid;
    private Long activitydate;
    private Long modifiedon;
    private int hits;
    private int viewed;
    private int sendingfailed;
    private int deleted;
    private String bounceStatus;
    private Set<CampaignTimeLog> timeLogs;

    public Set<CampaignTimeLog> getTimeLogs() {
		return timeLogs;
	}

	public void setTimeLogs(Set<CampaignTimeLog> timeLogs) {
		this.timeLogs = timeLogs;
	}

	public String getBounceStatus() {
        return bounceStatus;
    }

    public void setBounceStatus(String bounceStatus) {
        this.bounceStatus = bounceStatus;
    }


    public Date getActivitydate() {
        return activitydate==null?null:new Date(activitydate);
    }

    public void setActivitydate(Date activitydate) {
    	if(activitydate!=null)
        this.activitydate = activitydate.getTime();
    }

    public String getActivitytype() {
        return activitytype;
    }

    public void setActivitytype(String activitytype) {
        this.activitytype = activitytype;
    }

    public CrmCampaign getCampaignid() {
        return campaignid;
    }

    public void setCampaignid(CrmCampaign campaignid) {
        this.campaignid = campaignid;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public EmailMarketing getEmailmarketingid() {
        return emailmarketingid;
    }

    public void setEmailmarketingid(EmailMarketing emailmarketingid) {
        this.emailmarketingid = emailmarketingid;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getModifiedon() {
        return modifiedon==null?null:new Date(modifiedon);
    }

    public void setModifiedon(Date modifiedon) {
    	if(modifiedon!=null)
        this.modifiedon = modifiedon.getTime();
    }

    public String getRelatedtype() {
        return relatedtype;
    }

    public void setRelatedtype(String relatedtype) {
        this.relatedtype = relatedtype;
    }

    public TargetListTargets getTargetid() {
        return targetid;
    }

    public void setTargetid(TargetListTargets targetid) {
        this.targetid = targetid;
    }

    public TargetList getTargetlistid() {
        return targetlistid;
    }

    public void setTargetlistid(TargetList targetlistid) {
        this.targetlistid = targetlistid;
    }

    public String getTargettrackerkey() {
        return targettrackerkey;
    }

    public void setTargettrackerkey(String targettrackerkey) {
        this.targettrackerkey = targettrackerkey;
    }

    public String getTargettype() {
        return targettype;
    }

    public void setTargettype(String targettype) {
        this.targettype = targettype;
    }

    public int getViewed() {
        return viewed;
    }

    public void setViewed(int viewed) {
        this.viewed = viewed;
    }

    public int getSendingfailed() {
        return sendingfailed;
    }

    public void setSendingfailed(int sendingfailed) {
        this.sendingfailed = sendingfailed;
    }

    public Long getActivityDate() {
        return activitydate;
    }
	public void setActivityDate(Long activitydate) {
		this.activitydate = activitydate;
	}

	public void setModifiedOn(Long modifiedon) {
		this.modifiedon = modifiedon;
	}
    
    public Long getModifiedOn() {
        return modifiedon;
    }
}
