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

import com.krawler.br.exp.Expression.VALUE_TYPE;
import com.krawler.br.modules.ModuleDefinition.TYPE;
import java.util.Set;

/**
 * Interface to provide module definition's group
 *
 * @author Vishnu Kant Gupta
 */
public interface ModuleBag {
    /**
     * default primitive types currently supported by module factory
     */
    enum PRIMITIVE{
        BOOLEAN("boolean","java.lang.Boolean", TYPE.SIMPLE,VALUE_TYPE.BOOLEAN), // represents boolean primitive type
        BYTE("byte","java.lang.Byte", TYPE.SIMPLE, VALUE_TYPE.NUMBER),          // represents byte primitive type
        CHAR("char","java.lang.Character", TYPE.SIMPLE, VALUE_TYPE.NUMBER),     // represents char primitive type
        SHORT("short","java.lang.Short", TYPE.SIMPLE, VALUE_TYPE.NUMBER),       // represents short primitive type
        INT("int","java.lang.Integer", TYPE.SIMPLE, VALUE_TYPE.NUMBER),         // represents int primitive type
        LONG("long","java.lang.Long", TYPE.SIMPLE, VALUE_TYPE.NUMBER),          // represents long primitive type
        FLOAT("float","java.lang.Float", TYPE.SIMPLE, VALUE_TYPE.NUMBER),       // represents float primitive type
        DOUBLE("double","java.lang.Double", TYPE.SIMPLE, VALUE_TYPE.NUMBER),    // represents double primitive type
        DATE("date","java.util.Date", TYPE.SIMPLE, null),          // represents java.util.Date class
        STRING("string","java.lang.String", TYPE.SIMPLE, null),    // represents java.lang.String class
        EMPTYJSON("emptyjson","com.krawler.utils.json.base.JSONObject", TYPE.JSON, null),    // represents java.lang.String class
        EMPTYMAP("emptymap","java.util.HashMap", TYPE.MAP, null),    // represents java.lang.String class
        OBJECT("object","java.lang.Object", TYPE.SIMPLE, null);

        PRIMITIVE(String tag, String cls, TYPE type, VALUE_TYPE t) {
            this.tag = tag;
            this.type = type;
            this.cls = cls;
            this.t = t;
        }

        public VALUE_TYPE valueType(){
            return t;
        }
        public String tagName(){
            return tag;
        }
        public TYPE type(){
            return type;
        }
        public String className(){
            return cls;
        }
        private final String cls;
        private final String tag;
        private final VALUE_TYPE t;
        private final TYPE type;
    }

    /**
     * provides a way to get the module definition from this factory by supplying
     * the name of it from definition
     *
     * @param name the name of the module definition
     * @return the associated module definition or null if no module present
     * with that name
     */
    ModuleDefinition getModuleDefinition(String name);

    /**
     * checks to see whether any module definition presents in the factory with
     * the given name
     *
     * @param name the name of the module definition
     * @return true if the module definition present, false otherwise
     */
    boolean hasModule(String name);

    /**
     * returns the set of names of all module definitions present in the factory
     * currently
     *
     * @return the set of names
     */
    Set getModuleNames();

    /**
     * checks to see whether the given module name & path construct a valid property
     * 
     * @param moduleName the name of module
     * @param path "/" seperated property path in the given module
     * @return true if the modulename is not null and path is null
     * or if path is not null and valid, null otherwise
     */
    boolean isValidPropertyPath(String moduleName, String[] props);

    /**
     * gives fully qualified class name for given modulename & path
     *
     * @param moduleName name of module
     * @param path "/" seperated property path in the given module
     * @return classname of module if the modulename is not null and path is null
     * or class name of property module if path is not null and valid, null otherwise
     */
    String getModuleName(String moduleName,String props[]);
}
