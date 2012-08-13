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

/**
 *
 * @author krawler
 */
public class CrmContactCustomData extends CrmCustomData {

    private String contactid;
    private CrmContact contact;

    public CrmContact getContact() {
        return contact;
    }

    public void setContact(CrmContact contact) {
        this.contact = contact;
    }

    public String getContactid() {
        return contactid;
    }

    public void setContactid(String contactid) {
        this.contactid = contactid;
    }
}
