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
public class AccountProducts {
    private String id;
    private CrmProduct productId;
    private CrmAccount accountid;
    
    private String accountId;

    public CrmAccount getAccountid() {
        return accountid;
    }

    public void setAccountid(CrmAccount accountid) {
        this.accountid = accountid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }   

    public CrmProduct getProductId() {
        return productId;
    }

    public void setProductId(CrmProduct productId) {
        this.productId = productId;
    }

    /**
     * @return the accountId
     */
    public String getAccountId()
    {
        return accountId;
    }

    /**
     * @param accountId the accountId to set
     */
    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }
    

}
