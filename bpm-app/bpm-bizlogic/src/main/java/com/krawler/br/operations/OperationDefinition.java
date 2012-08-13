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

import com.krawler.br.loader.KwlLoader;

/**
 * Interface to to be used to represent operation definition
 *
 * @author Vishnu Kant Gupta
 */
public interface OperationDefinition {
    /**
     * gets the id of the operation definition
     *
     * @return the id of operation definition
     */
    String getID();

    /**
     * sets the id of operation definition
     *
     * @param ID the id to set
     */
    void setID(String ID);

    /**
     * gets the name of the operation definition
     *
     * @return the name of operation definition
     */
    String getName();

    /**
     * sets the name of operation definition
     *
     * @param name the name to set
     */
    void setName(String name);
    /**
     * gets the name of method which will be executed if this operation will be used
     * in flow and that flow executed, this method should be present in actual class
     * that represented by the entity
     *
     * @return the method name
     */
    String getMethodName();

    /**
     * sets the name of method which will be executed if this operation will be used
     * in flow and that flow executed, this method should be present in actual class
     * that represented by the entity
     *
     * @param methodName the name of the method
     */
    void setMethodName(String methodName);

    /**
     * gets the name of entity which will be executed if this operation will be used
     * in flow and that flow executed, the actual class should be present
     * that represented by the entity
     *
     * @return the entity name
     */
    String getEntityName();

    /**
     * sets the name of entity which will be executed if this operation will be used
     * in flow and that flow executed, the actual class should be present
     * that represented by the entity
     *
     * @param name the name of the entity
     */
    void setEntityName(String name);

    /**
     * gets the loader type of entity, the entity type will decide how to load the entity
     * @return loader type of entity
     */
    KwlLoader getEntityLoader();

    /**
     * sets the loader type of entity, the entity type will decide how to load the entity
     *
     * @param loader loader type of entity
     */
    void setEntityLoader(KwlLoader loader);

    /**
     * checks whether the operation definition has the input parameter definition
     * for the supplied name
     *
     * @param paramName the name of input parameter
     * @return true if the input parameter with given name exists for this operation
     * definition, false otherwise
     */
    boolean hasInputParameter(String paramName);

    /**
     * gives the input operation parameter definition for the given parameter name
     *
     * @param paramName the name of input parameter
     * @return the operation parameter associated with this operation definition with
     * the given name
     */
    OperationParameter getInputParameter(String paramName);

    /**
     * gives the input operation parameter definition for the given parameter index
     *
     * @param index the index of input parameter
     * @return the operation parameter associated with this operation definition at
     * the specified index
     */
    OperationParameter getInputParameter(int index);

    /**
     * gets the parameter index mapped to the parameter name from the list of
     * input parameters
     *
     * @param paramName the name of parameter
     * @return the parameter index
     */
    public int getParameterIndex(String paramName);

     /**
     * gives the total number of input parameter definitions that are currently
     * associated with this operation definition
     *
     * @return number of input parameters required
     */
    int getParameterCount();

    /**
     * associates the list of operation parameter definitions with this operation
     * definition's input
     *
     * @param param list of operation parameter definition to set for input
     */
    void setInputParameters(OperationParameter[] param);

    /**
     * associates the operation parameter definition with this operation definition's
     * output
     *
     * @param param operation parameter definition to set for output
     */
    void setOutputParameter(OperationParameter param);

    /**
     * gives the output operation parameter definition associated with this operation
     * definition
     * 
     * @return operation parameter definition or <tt>null</tt> if no output
     * parameter is set
     */
    OperationParameter getOutputParameter();

    /**
     * associates the operation factory with this operation definition's as the container
     * output
     *
     * @param factory operation factory to set
     */
    void setOperationBag(OperationBag factory);

    /**
     * gives the operation factory associated with this activity as container
     * definition
     *
     * @return operation factory or <tt>null</tt> if no factory
     * parameter is set
     */
    OperationBag getOperationBag();
}
