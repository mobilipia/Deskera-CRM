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

import com.krawler.spring.crm.emailMarketing.crmEmailTemplateInterface;
import java.util.Date;

/**
 *
 * @author krawler
 */
public class DefaultTemplates implements java.io.Serializable, crmEmailTemplateInterface{
    private String templateid;
    private String name;
    private String description;
    private String subject;
    private String body;
    private String body_html;
    private String thumbnail;
    private Long createdOn;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody_html() {
        return body_html;
    }

    public void setBody_html(String body_html) {
        this.body_html = body_html;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTemplateid() {
        return templateid;
    }
    public void setTemplateid(String templateid) {
        this.templateid = templateid;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }
    public void setThumbnail(String tn) {
        this.thumbnail = tn;
    }

    public Date getCreatedon() {
        return new Date();
    }

	@Override
	 public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

}
