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
 * Interface to provide the representation for operation parameter
 *
 * @author Vishnu Kant Gupta
 */
public interface OperationParameter {

    /**
     * gets the name of this operation parameter definition
     *
     * @return name of this operation parameter
     */
    String getName();

    /**
     * sets the name of this operation parameter definition
     *
     * @param name name of parameter
     */
    void setName(String name);

    /**
     * gets the type name of the parameter
     *
     * @return name of type
     */
    String getType();

    /**
     * sets the type of parameter to the given type name
     *
     * @param type name of type
     */
    void setType(String type);

    /**
     * returns multi type of the parameter
     *
     * @return multi type
     */
    MULTI getMulti();

    /**
     * sets the multi type of the parameter
     * @param multi multi type
     */
    void setMulti(MULTI multi);

    String getClassName(ModuleBag mb);
}
