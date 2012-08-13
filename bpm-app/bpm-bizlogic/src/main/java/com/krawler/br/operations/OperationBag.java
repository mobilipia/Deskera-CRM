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
import java.util.Set;

/**
 * Interface to provide activity definition's group
 *
 * @author Vishnu Kant Gupta
 */
public interface OperationBag {
    /**
     * the module factory to which it is depends on, this module factory will
     * be used to validate input and out put parameters of any operation
     *
     * @param mb the module bag to associate
     */
    void setModuleBag(ModuleBag mb);

    /**
     * a getter method to provide access to the module factory to be used for
     * validation
     *
     * @return the associated module bag
     */
    ModuleBag getModuleBag();

    /**
     * validates the input parameters for their types, the method will check to
     * see whether the input parameter types are supported by associated module
     * factory
     *
     * @param od operation definition to check
     * @return true if has all valid parameters, false otherwise
     */
    boolean hasValidParameters(OperationDefinition od);

    /**
     * provides a way to get the operation definition from this factory by supplying
     * the id of it from definition
     *
     * @param id the id of the operation definition
     * @return the associated operation definition or null if no operation present
     * with that id
     */
    OperationDefinition getOperationDefinition(String id);

    /**
     * checks to see whether any operation definition presents in the factory with
     * the given id
     *
     * @param id the id of the operation definition
     * @return true if the operation definition present, false otherwise
     */
    boolean hasOperation(String id);

    /**
     * returns the set of names of all operation definitions present in the factory
     * currently
     *
     * @return the set of names
     */
    Set getOperationIDs();
}
