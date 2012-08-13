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

public class scheduledmarketing implements java.io.Serializable {
    private String id;
    private User userid;
    private EmailMarketing emailmarketingid;
//    private Date scheduleddate;
    private Long scheduledDate;
    private String scheduledtime;
    private int deleted;

    public String getId(){
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public int getDeleted(){
        return this.deleted;
    }
    public void setDeleted(int delete) {
        this.deleted = delete;
    }

    public User getUserid(){
        return this.userid;
    }
    public void setUserid(User userid) {
        this.userid = userid;
    }

    public EmailMarketing getEmailmarketingid(){
        return this.emailmarketingid;
    }
    public void setEmailmarketingid(EmailMarketing marketingid) {
        this.emailmarketingid = marketingid;
    }

    public Long getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Long scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Date getScheduleddate(){
        if(this.scheduledDate!=null)
            return new Date(this.scheduledDate);
        return null;
    }
    public void setScheduleddate(Date dt) {
        this.scheduledDate = dt.getTime();
    }

    public String getScheduledtime(){
        return this.scheduledtime;
    }
    public void setScheduledtime(String time) {
        this.scheduledtime = time;
    }
}
