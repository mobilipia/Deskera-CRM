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

package com.krawler.br.operations;

import com.krawler.br.modules.ModuleBag;
import com.krawler.br.modules.ModuleProperty.MULTI;

/**
 * Default implementation of operation's parameter
 *
 * @author Vishnu Kant Gupta
 */
public class SimpleOperationParameter implements OperationParameter {
    private String name; // name of operation parameter
    private String type=ModuleBag.PRIMITIVE.OBJECT.tagName(); // type of operation parameter
    private MULTI multi; // multi type of operation parameter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MULTI getMulti() {
        return multi;
    }

    public void setMulti(MULTI multi) {
        this.multi = multi;
    }

    @Override
    public String getClassName(ModuleBag mb) {
        String cname= mb.getModuleDefinition(type).getClassName();
        return multi!=null?multi.className(cname):cname;
    }
}
