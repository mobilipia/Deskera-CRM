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

import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.User;

public class CustomReportColumns {
	private String id;
    private String columname;
    private String dataIndex;
    private String refTable;
    private String displayname;
    private String summarytype;
    private String xtype;
    private int displayorder;
    private boolean hidden;
    private boolean quicksearch;
    private String renderer;
    private int deleteflag;
    private User usersByUpdatedbyid;
    private User usersByCreatedbyid;
    private Long createdon;
    private Long updatedon;
    private DefaultHeader defaultheader;
    private CustomReportList reportno;
    private boolean groupflag;
    private int grouporder;

    public boolean isGroupflag() {
        return groupflag;
    }

    public void setGroupflag(boolean groupflag) {
        this.groupflag = groupflag;
    }

    public int getGrouporder() {
        return grouporder;
    }

    public void setGrouporder(int grouporder) {
        this.grouporder = grouporder;
    }

    public boolean isQuicksearch() {
        return quicksearch;
    }

    public void setQuicksearch(boolean quicksearch) {
        this.quicksearch = quicksearch;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public String getRefTable() {
        return refTable;
    }

    public void setRefTable(String refTable) {
        this.refTable = refTable;
    }

    public String getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
    }

	public String getColumname() {
        return columname;
    }

    public void setColumname(String columname) {
        this.columname = columname;
    }

    public Long getCreatedon() {
        return createdon;
    }

    public void setCreatedon(Long createdon) {
        this.createdon = createdon;
    }

    public DefaultHeader getDefaultheader() {
        return defaultheader;
    }

    public void setDefaultheader(DefaultHeader defaultheader) {
        this.defaultheader = defaultheader;
    }

    public int getDeleteflag() {
        return deleteflag;
    }

    public void setDeleteflag(int deleteflag) {
        this.deleteflag = deleteflag;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public int getDisplayorder() {
        return displayorder;
    }

    public void setDisplayorder(int displayorder) {
        this.displayorder = displayorder;
    }

    public String getRenderer() {
        return renderer;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public CustomReportList getReportno() {
        return reportno;
    }

    public void setReportno(CustomReportList reportno) {
        this.reportno = reportno;
    }

    public String getSummarytype() {
        return summarytype;
    }

    public void setSummarytype(String summarytype) {
        this.summarytype = summarytype;
    }

    public Long getUpdatedon() {
        return updatedon;
    }

    public void setUpdatedon(Long updatedon) {
        this.updatedon = updatedon;
    }

    public User getUsersByCreatedbyid() {
        return usersByCreatedbyid;
    }

    public void setUsersByCreatedbyid(User usersByCreatedbyid) {
        this.usersByCreatedbyid = usersByCreatedbyid;
    }

    public User getUsersByUpdatedbyid() {
        return usersByUpdatedbyid;
    }

    public void setUsersByUpdatedbyid(User usersByUpdatedbyid) {
        this.usersByUpdatedbyid = usersByUpdatedbyid;
    }

    public String getXtype() {
        return xtype;
    }

    public void setXtype(String xtype) {
        this.xtype = xtype;
    }
}
