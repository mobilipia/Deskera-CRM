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

package com.krawler.br.modules;

/**
 * Defualt implementation of property of any module
 *
 * @author Vishnu Kant Gupta
 */
public class SimpleModuleProperty implements ModuleProperty {
    private String name;            // name of property
    private String type;            // type of property
    private MULTI multi;            // multi type of the property

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setMulti(MULTI multi) {
        this.multi = multi;
    }

    @Override
    public boolean isMulti() {
        return this.multi!=null;
    }

    @Override
    public String getClassName(ModuleBag factory) {
        String cname=factory.getModuleDefinition(type).getClassName();
        return multi!=null?multi.className(cname):cname;
    }

    @Override
    public MULTI getMulti() {
        return multi;
    }

}
