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
 * Interface to load the given object
 *
 * @author Vishnu Kant Gupta
 */
public interface KwlLoader {

    /**
     * loads the given class for given name and provides one instance
     * @param loaderName name of loader to load
     * @return instance of the loader as object
     */
    public Object load(String loaderName);

    /**
     * loads the given class for given name and provides one instance
     * @param loaderName loaderName name of loader to load
     * @param T type of class to load
     * @return instance of the loader as T
     */
    public <T> T load(String loaderName, Class T);
}
