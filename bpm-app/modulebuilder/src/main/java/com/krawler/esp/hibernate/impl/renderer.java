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
import java.util.HashSet;
import java.util.Set;

public class renderer {
    private String id;
    private String name;
    private Set children = new HashSet();
    private boolean isstatic;
    private String rendererValue;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRendererValue() {
        return rendererValue;
    }

    public void setRendererValue(String rendererValue) {
        this.rendererValue = rendererValue;
    }

    public Set getChildren() {
        return children;
    }

    public void setChildren(Set children) {
        this.children = children;
    }

    public boolean isIsstatic() {
        return isstatic;
    }

    public void setIsstatic(boolean isstatic) {
        this.isstatic = isstatic;
    }

}
