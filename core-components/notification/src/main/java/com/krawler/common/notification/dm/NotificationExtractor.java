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

package com.krawler.common.notification.dm;

public class NotificationExtractor {
    public Long id;
    public String beanid;
    public Long definition;
    public NotificationDefinition refdefinition;

    public String getBeanid() {
        return beanid;
    }

    public void setBeanid(String beanid) {
        this.beanid = beanid;
    }

    public Long getDefinition() {
        return definition;
    }

    public void setDefinition(Long definition) {
        this.definition = definition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationDefinition getRefdefinition() {
        return refdefinition;
    }

    public void setRefdefinition(NotificationDefinition refdefinition) {
        this.refdefinition = refdefinition;
    }
}
