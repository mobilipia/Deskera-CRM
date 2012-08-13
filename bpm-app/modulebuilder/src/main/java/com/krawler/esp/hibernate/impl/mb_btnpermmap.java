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

public class mb_btnpermmap implements Serializable{
    private String id;
    private com.krawler.esp.hibernate.impl.mb_buttonConf buttonid;
    private com.krawler.esp.hibernate.impl.mb_permmaster permid;

    public String getId(){
        return this.id;
    }

    public void setId(String id){
        if ((id == null && this.id != null) ||
					(id != null && this.id == null) ||
					(id != null && this.id != null && !id.equals(this.id))){
            this.id = id;
        }
    }

    public com.krawler.esp.hibernate.impl.mb_buttonConf getButtonid(){
        return this.buttonid;
    }

    public void setButtonid(com.krawler.esp.hibernate.impl.mb_buttonConf button){
        this.buttonid = button;
    }

    public com.krawler.esp.hibernate.impl.mb_permmaster getPermid(){
        return this.permid;
    }

    public void setPermid(com.krawler.esp.hibernate.impl.mb_permmaster permission){
        this.permid = permission;
    }
}
