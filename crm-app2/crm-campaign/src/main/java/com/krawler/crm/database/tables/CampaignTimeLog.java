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

public class CampaignTimeLog {
	private String id;
	private CampaignLog campaignLog;
	private Long viewedon;
	
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
	public Date getViewedOn() {
		return new Date(viewedon);
	}
	public void setViewedOn(Date viewedOn) {
		if(viewedOn!=null)
			this.viewedon = viewedOn.getTime();
	}
	public Long getViewedon() {
		return viewedon;
	}
	public void setViewedon(Long viewedon) {
		this.viewedon = viewedon;
	}
	
}
