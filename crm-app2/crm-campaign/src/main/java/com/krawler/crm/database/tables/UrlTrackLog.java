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

public class UrlTrackLog {
	private String id;
	private CampaignLog campaignLog;
	private Long clickedOn;
	private String url;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public CampaignLog getCampaignLog() {
		return campaignLog;
	}
	public void setCampaignLog(CampaignLog campaignLog) {
		this.campaignLog = campaignLog;
	}
	public Date getClickedon() {
		return new Date(this.clickedOn);
	}
	public void setClickedon(Date clickedOn) {
		if(clickedOn!=null)
			this.clickedOn= clickedOn.getTime();
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Long getClickedOn() {
		return clickedOn;
	}
	public void setClickedOn(Long clickedOn) {
		this.clickedOn = clickedOn;
	}
}
