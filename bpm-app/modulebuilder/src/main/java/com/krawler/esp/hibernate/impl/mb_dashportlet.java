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

/**
 *
 * @author krawler
 */
public class mb_dashportlet implements Serializable{
    private String portletid;
    private String config;
    private String portlettitle;
    private com.krawler.esp.hibernate.impl.mb_dashboard dashboardid;
    private mb_reportlist reportid;

    public String getPortletid(){
        return this.portletid;
    }

    public void setPortletid(String id){
        if ((id == null && this.portletid != null) ||
            (id != null && this.portletid == null) ||
            (id != null && this.portletid != null && !id.equals(this.portletid))){
            this.portletid = id;
        }
    }

    public String getConfig(){
        return this.config;
    }

    public void setConfig(String conf){
        if ((conf == null && this.config != null) ||
            (conf != null && this.config == null) ||
            (conf != null && this.config != null && !conf.equals(this.config))){
            this.config = conf;
        }
    }

    public mb_dashboard getDashboardid(){
        return this.dashboardid;
    }

    public void setDashboardid(mb_dashboard dash){
        this.dashboardid = dash;
    }

    public mb_reportlist getReportid(){
        return this.reportid;
    }

    public void setReportid(mb_reportlist report){
        this.reportid = report;
    }

    public String getPortlettitle(){
        return this.portlettitle;
    }

    public void setPortlettitle(String title){
        if ((title == null && this.portlettitle != null) ||
            (title != null && this.portlettitle == null) ||
            (title != null && this.portlettitle != null && !title.equals(this.portlettitle))){
            this.portlettitle = title;
        }
    }
}
