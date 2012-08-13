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

package com.krawler.br.loader;

/**
 * Class to load the simple class and get an instance
 *
 * @author Vishnu Kant Gupta
 */
public class KwlClassLoader implements KwlLoader {

    /**
     * loads the class for given classname and provides one instance
     * @param loaderName fully qualified name of class to load
     * @return instance of the class as object
     */
    @Override
    public Object load(String loaderName) {
        Object instance = null;
        try {
            Class klass = Class.forName(loaderName);
            instance = klass.newInstance();
        } catch (Exception ex) {
        }
        return instance;
    }

    /**
     * loads the class for given name and provides one instance
     * @param loaderName fully qualified name of class to load
     * @param T type of class to load
     * @return instance of the loader as T
     * @throws java.lang.ClassCastException if the object can not be cast to the
     * given class
     */
    @Override
    public <T> T load(String loaderName, Class T) {
        return (T)load(loaderName);
    }
}
