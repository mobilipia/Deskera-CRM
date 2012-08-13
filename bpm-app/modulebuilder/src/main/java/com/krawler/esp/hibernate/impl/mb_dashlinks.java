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
public class mb_dashlinks implements Serializable {
    private String linkid;
    private String linktext;
//    private mb_processChart processid;
    private mb_reportlist processid;
    private mb_dashboard dashboardid;
    private mb_linkgroup groupid;

    public String getLinkid(){
        return this.linkid;
    }

    public void setLinkid(String id){
        if ((id == null && this.linkid != null) ||
            (id != null && this.linkid == null) ||
            (id != null && this.linkid != null && !id.equals(this.linkid))){
            this.linkid = id;
        }
    }

    public String getLinktext(){
        return this.linktext;
    }
    public void setLinktext(String text){
        if ((text == null && this.linktext != null) ||
                (text != null && this.linktext == null) ||
                (text != null && this.linktext != null && !text.equals(this.linktext))){
                this.linktext = text;
            }
    }

//    public mb_processChart getProcessid() {
//        return processid;
//    }
//
//    public void setProcessid(mb_processChart processid) {
//        this.processid = processid;
//    }

    public mb_reportlist getProcessid() {
        return processid;
    }

    public void setProcessid(mb_reportlist processid) {
        this.processid = processid;
    }

    public mb_dashboard getDashboardid(){
        return this.dashboardid;
    }

    public void setDashboardid(mb_dashboard did){
        this.dashboardid = did;
    }

    public mb_linkgroup getGroupid(){
        return this.groupid;
    }

    public void setGroupid(mb_linkgroup grp){
        this.groupid = grp;
    }
}
