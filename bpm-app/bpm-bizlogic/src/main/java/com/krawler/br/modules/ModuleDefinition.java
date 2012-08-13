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

import java.util.Set;

/**
 * Interface to to be used to represent module definition
 *
 * @author Vishnu Kant Gupta
 */
public interface ModuleDefinition {
    /**
     * module types supported currently, all module types will have a tag name
     * to be used in representation
     */
    enum TYPE{
        SIMPLE("simple"),   // simple type
        POJO("pojo"),       // plain old java object
        MAP("map"),         // map type
        JSON("json");       // json type
        TYPE(String tag){
            this.tag = tag;
        }
        public String tagName(){
            return tag;
        }
        private final String tag;
    }

    /**
     * sets the name of module definition
     *
     * @param name the name to set
     */
    void setName(String name);

    /**
     * gets the name of the module definition
     *
     * @return the name of module definition
     */
    String getName();
    void setType(TYPE type);
    TYPE getType();

    /**
     * sets the class name for the module.
     *
     * @param className the fully qualified class name
     */
    void setClassName(String className);

    /**
     * gives fully qualified class name for this module definition
     *
     * @return classname of module definition or <tt>null</tt> if no class name
     * is set
     */
    String getClassName();

    /**
     * checks whether the module definition has the property definition
     * for the supplied property name
     *
     * @param propName the name of property
     * @return true if the property with given name exists for this module
     * definition, false otherwise
     */
    boolean hasProperty(String propName);

    /**
     * gives the property definition for the given property name
     *
     * @param propName the name of property
     * @return the property definition associated with this module definition with
     * the given name
     */
    ModuleProperty getProperty(String propName);

    /**
     * adds new property definition in the module definition, the property
     * definition will be added only if it is not null,<br />
     * note that only one property definition with the given name can be exists in
     * the one module definition. If the module definition previously contained
     * a property definition for the name, the old property definition is replaced
     *
     * @param prop the property definition to add
     * @throws NullPointerException if the module definition is null
     */
    void addProperty(ModuleProperty prop);

    /**
     * returns the set of names of all property definitions present in the module
     * definition currently
     *
     * @return the set of names
     */
    Set getPropertyNames();
}
