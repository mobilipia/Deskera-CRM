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

import java.util.HashMap;
import java.util.Set;

/**
 * Default implementation of Module definition
 *
 * @author Vishnu Kant Gupta
 */
public class SimpleModuleDefinition implements ModuleDefinition  {
    private String name;                        // name of module definition
    private TYPE type;                          // type of module definition
    private String className;                   // fully qualified class name for the module definition
    private HashMap propertyMap=new HashMap();  // a hashmap for storing all the property definition

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setType(TYPE type) {
        this.type = type;
    }

    @Override
    public TYPE getType() {
        return type;
    }

    @Override
    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void addProperty(ModuleProperty prop) {
        propertyMap.put(prop.getName(), prop);
    }

    @Override
    public boolean hasProperty(String propName) {
        return propertyMap.containsKey(propName);
    }

    @Override
    public ModuleProperty getProperty(String propName) {
        return (ModuleProperty)propertyMap.get(propName);
    }

    @Override
    public Set getPropertyNames() {
        return propertyMap.keySet();
    }
}
