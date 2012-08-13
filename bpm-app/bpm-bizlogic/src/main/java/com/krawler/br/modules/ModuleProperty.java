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
 * Interface to represent the property of any module
 *
 * @author Vishnu Kant Gupta
 */
public interface ModuleProperty {
    /**
     * represents the multiplicity of the property
     */
    enum MULTI{
        LIST("list","java.util.List"),          // list representation
        SET("set","java.util.Set"),             // set representation
        ARRAY("array","[]"),                    // array representation
        JSONARRAY("jsonarray","com.krawler.utils.json.base.JSONArray");     // jsonarray representation
        MULTI(String tag, String cls){
            this.tag = tag;
            this.cls = cls;
        }
        public String className(String className){
            // if the multi type is array, then append the "[]" with supplied classname
            // else give only the multi class name
            return (this==ARRAY?className+cls:cls);
        }
        public String tagName(){
            return tag;
        }
        private final String cls;
        private final String tag;
    }

    public void setName(String name);

    /**
     * gives the name of the property
     *
     * @return the name of property
     */
    public String getName();

    /**
     * sets the type of property, this type is any existing module definition name
     *
     * @param type the type name
     */
    public void setType(String type);

    /**
     * provides the type name of the property
     *
     * @return name of the property type
     */
    public String getType();

    /**
     * sets the multi type of the given property for the container
     *
     * @param multi multi type
     */
    public void setMulti(MULTI multi);

    /**
     * gets the multi type of the given property for the container
     *
     * @return multi type
     */
    public MULTI getMulti();

    /**
     * checks whether this property is a multi property or not
     *
     * @return true if the property is any one of the multi type, false otherwise
     */
    public boolean isMulti();

    /**
     * provides the class name of property for a given module factory.
     *
     * @param factory the module factory on which context the class name to be
     * determined
     * @return fully qualified class name, or null if module factory doesnot
     * contain the type associated with the property.
     */
    public String getClassName(ModuleBag factory);
}
