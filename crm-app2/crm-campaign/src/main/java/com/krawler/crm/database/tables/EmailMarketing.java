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

public class EmailMarketing implements java.io.Serializable {
	public static final int STAUS_RUNNING = 1;
	public static final int STAUS_COMPLETED = 2;
	public static final int STAUS_INTERRUPTED = 3;
    private String id;
    private String name;
    private String fromname;
    private String fromaddress;
    private String replytoname;
    private String replytoaddress;
    private String inboundemailid;
    private int lastRunStatus;
    private Long lastRunOn;
    private User creator;
    private CrmCampaign campaignid;
    private EmailTemplate templateid;
//    private Date createdon;
//    private Date modifiedon;
    private Long createdOn;
    private Long modifiedOn;
    private String htmltext;
    private String plaintext;
    private templateColorTheme colortheme;
    private String unsubscribelink;
    private String fwdfriendlink;
    private String archivelink;
    private String updateprofilelink;
    private int deleted;
    private boolean canSpamAccepted;
    private String subject;
    private boolean captureLead;

    public CrmCampaign getCampaignid() {
        return campaignid;
    }

    public void setCampaignid(CrmCampaign campaignid) {
        this.campaignid = campaignid;
    }

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public Long getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Long modifiedOn) {
        this.modifiedOn = modifiedOn;
    }


    public Date getCreatedon() {
        if(createdOn!=null)
            return new Date(createdOn);
        return null;
    }

    public void setCreatedon(Date createdon) {
        this.createdOn = createdon.getTime();
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public String getFromaddress() {
        return fromaddress;
    }

    public void setFromaddress(String fromaddress) {
        this.fromaddress = fromaddress;
    }

    public String getFromname() {
        return fromname;
    }

    public void setFromname(String fromname) {
        this.fromname = fromname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInboundemailid() {
        return inboundemailid;
    }

    public void setInboundemailid(String inboundemailid) {
        this.inboundemailid = inboundemailid;
    }

    public Date getModifiedon() {
        if(modifiedOn!=null)
            return new Date(modifiedOn);
        return null;
    }

    public void setModifiedon(Date modifiedon) {
        this.modifiedOn = modifiedon.getTime();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReplytoaddress() {
        return replytoaddress;
    }

    public void setReplytoaddress(String replytoaddress) {
        this.replytoaddress = replytoaddress;
    }

    public String getReplytoname() {
        return replytoname;
    }

    public void setReplytoname(String replytoname) {
        this.replytoname = replytoname;
    }

    public int getLastRunStatus() {
		return lastRunStatus;
	}

	public void setLastRunStatus(int lastRunStatus) {
		this.lastRunStatus = lastRunStatus;
	}

	public Long getLastRunOn() {
		return lastRunOn;
	}

	public void setLastRunOn(Long lastRunOn) {
		this.lastRunOn = lastRunOn;
	}

	public EmailTemplate getTemplateid() {
        return templateid;
    }

    public void setTemplateid(EmailTemplate templateid) {
        this.templateid = templateid;
    }

    public void setHtmltext(String htm) {
        this.htmltext = htm;
    }
    public String getHtmltext(){
        return this.htmltext;
    }

    public String getPlaintext(){
        return this.plaintext;
    }
    public void setPlaintext(String pln) {
        this.plaintext = pln;
    }

    public templateColorTheme getColortheme(){
        return this.colortheme;
    }
    public void setColortheme(templateColorTheme theme) {
        this.colortheme = theme;
    }

    public String getUnsubscribelink() {
        return this.unsubscribelink;
    }
    public void setUnsubscribelink(String unsub) {
        this.unsubscribelink = unsub;
    }

    public String getFwdfriendlink() {
        return this.fwdfriendlink;
    }
    public void setFwdfriendlink(String fwd) {
        this.fwdfriendlink = fwd;
    }

    public String getUpdateprofilelink() {
        return this.updateprofilelink;
    }
    public void setUpdateprofilelink(String up) {
        this.updateprofilelink = up;
    }

    public String getArchivelink() {
        return this.archivelink;
    }
    public void setArchivelink(String archive) {
        this.archivelink = archive;
    }

	public boolean isCanSpamAccepted() {
		return canSpamAccepted;
	}

	public void setCanSpamAccepted(boolean canSpamAccepted) {
		this.canSpamAccepted = canSpamAccepted;
	}

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean isCaptureLead() {
        return captureLead;
    }

    public void setCaptureLead(boolean captureLead) {
        this.captureLead = captureLead;
    }
    

}
