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
 * Default implementation of Activity Definition
 *
 * @author Vishnu Kant Gupta
 */
public class SimpleOperationDefinition implements OperationDefinition {
    private String name; // operation definition name
    private String ID; // operation definition display name
    private String methodName; // name of method to execute
    private String entityName; // name of entity of method
    private KwlLoader loader; // loader type of entity
    private OperationParameter[] inputParams={}; // list of input parameter definition
    private OperationParameter outputParam; // output parameter definition
    private OperationBag ob;

    /**
     * gets the name of the operation definition
     *
     * @return the name of operation definition
     */
    public String getName() {
        return name;
    }

    /**
     * sets the name of operation definition
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * gets the name of method which will be executed if this operation will be used
     * in flow and that flow executed, this method should be present in actual class
     * that represented by the entity
     *
     * @return the method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * sets the name of method which will be executed if this operation will be used
     * in flow and that flow executed, this method should be present in actual class
     * that represented by the entity
     *
     * @param methodName the name of the method
     */
    public void setMethodName(String methodName) {
        this.methodName=methodName;
    }

    /**
     * gets the name of entity which will be executed if this operation will be used
     * in flow and that flow executed, the actual class should be present
     * that represented by the entity
     *
     * @return the entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * sets the name of entity which will be executed if this operation will be used
     * in flow and that flow executed, the actual class should be present
     * that represented by the entity
     *
     * @param entityName the name of entity
     */
    public void setEntityName(String entityName) {
        this.entityName=entityName;
    }

    /**
     * gets the loader type of entity, the entity type will decide how to load the entity
     *
     * @return loader type of entity
     */
    public KwlLoader getEntityLoader() {
        return loader;
    }

    /**
     * sets the loader type of entity, the entity type will decide how to load the entity
     *
     * @param loader loader type of the entity
     */
    public void setEntityLoader(KwlLoader loader) {
        this.loader = loader;
    }

    /**
     * associates the list of operation parameter definitions with this operation
     * definition's input
     *
     * @param params the parameter definition list
     */
    public void setInputParameters(OperationParameter[] params) {
        inputParams = params;
    }

    /**
     * checks whether the operation definition has the input parameter definition
     * for the supplied name
     *
     * @param paramName the name of input parameter
     * @return true if the input parameter with given name exists for this operation
     * definition, false otherwise
     */
    public boolean hasInputParameter(String paramName) {
        return getParameterIndex(paramName)>=0;
    }

    /**
     * gives the input operation parameter definition for the given parameter name
     *
     * @param paramName the name of input parameter
     * @return the operation parameter associated with this operation definition with
     * the given name
     */
    public OperationParameter getInputParameter(String paramName) {
        return inputParams[getParameterIndex(paramName)];
    }

    /**
     * gets the parameter index mapped to the parameter name from the list of
     * input parameters
     *
     * @param paramName the name of parameter
     * @return the parameter index
     */
    public int getParameterIndex(String paramName) {
        if(paramName==null||paramName.length()==0)
            return -1;
        for(int i=0; i<inputParams.length;i++){
            if(paramName.equals(inputParams[i].getName()))
                return i;
        }
        return -1;
    }

    /**
     * gives the input operation parameter definition for the given parameter index
     *
     * @param index the index of input parameter
     * @return the operation parameter associated with this operation definition at
     * the specified index
     */
    public OperationParameter getInputParameter(int index) {
        return inputParams[index];
    }

    /**
     * associates the operation parameter definition with this operation definition's
     * output
     *
     * @param param operation parameter definition to set for output
     */
    public void setOutputParameter(OperationParameter param) {
        this.outputParam = param;
    }

    /**
     * gives the output operation parameter definition associated with this operation
     * definition
     *
     * @return operation parameter definition or <tt>null</tt> if no output
     * parameter is set
     */
    public OperationParameter getOutputParameter() {
        return outputParam;
    }

    /**
     * gives the total number of input parameter definitions that are currently
     * associated with this operation definition
     *
     * @return number of input parameters required
     */
    public int getParameterCount() {
        return inputParams.length;
    }

    @Override
    public void setOperationBag(OperationBag ob) {
        this.ob = ob;
    }

    @Override
    public OperationBag getOperationBag() {
        return ob;
    }
}
