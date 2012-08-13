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
package com.krawler.br.nodes;

import com.krawler.br.loader.KwlLoader;
import com.krawler.br.*;
import com.krawler.br.operations.OperationDefinition;
import com.krawler.br.modules.ModuleBag;
import com.krawler.br.operations.OperationParameter;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.MethodUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * performs the actual execution of existing application specific operation
 *
 * @author Vishnu Kant Gupta
 */
public class Activity extends AbstractFlowNode {
    private OperationDefinition operation;    // operation to perform

    /**
     * gives the operation definition associated with this activity
     *
     * @return the operation definition
     */
    public OperationDefinition getOperation() {
        return operation;
    }

    /**
     * associates the operation definition with this activity to be used
     *
     * @param operation the operation definition
     */
    public void setOperation(OperationDefinition operation) {
        this.operation = operation;
    }

    @Override
    synchronized public void invoke() throws ProcessException {
        KwlLoader loader = operation.getEntityLoader();
        if(loader==null)
            throw new ProcessException("No loader available for entity "+operation.getEntityName());
        Object obj = loader.load(operation.getEntityName());
        if(obj==null)
            throw new ProcessException("Entity not found ["+operation.getEntityName()+"] for operation :"+operation.getName());
        try {
            Object res = MethodUtils.invokeMethod(obj, operation.getMethodName(), getParams());
            if(operation.getOutputParameter()!=null){
                heap.put(operation.getOutputParameter().getName(), res);
            }
        } catch (InvocationTargetException ex) {
            throw new ProcessException(ex.getCause().getMessage(), ex.getCause());
        } catch (NoSuchMethodException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        }
    }

    /**
     * provides the actual values for parameters to execute with conversion of
     * types if possible
     *
     * @param types the list of parameter types
     * @param params the actual parameter values
     * @return the converted list of values
     */
    private Object[] getParams(){
        Object res[] = new Object[operation.getParameterCount()];
        int count=operation.getParameterCount();
        ModuleBag mf = operation.getOperationBag().getModuleBag();
        for (int i = 0; i < count; i++) {
            OperationParameter op=operation.getInputParameter(i);
            res[i]=convert(op.getClassName(mf), heap.get(operation.getInputParameter(i).getName()));
        }
        return res;
    }

    /**
     * converts the given object value to the given primitive type's wrapper class
     * if possible (if it is a <tt>Number</tt>)
     *
     * @param type the premitive type name, may be:
     * <ul>
     * <li><tt>byte</tt>
     * <li><tt>char</tt>
     * <li><tt>short</tt>
     * <li><tt>int</tt>
     * <li><tt>long</tt>
     * <li><tt>float</tt>
     * <li><tt>double</tt>
     * </ul>
     * @param value the original value
     * @return the converted value
     */
    private Object convert(String type, Object value){
        Object val=value;

        if(value instanceof Number){
            Number num = (Number)value;

            if(Byte.class.getCanonicalName().equals(type))
                val = new Byte(num.byteValue());
            else if(Character.class.getCanonicalName().equals(type))
                val = new Character((char)num.intValue());
            else if(Short.class.getCanonicalName().equals(type))
                val = new Short(num.shortValue());
            else if(Integer.class.getCanonicalName().equals(type))
                val = new Integer(num.intValue());
            else if(Long.class.getCanonicalName().equals(type))
                val = new Long(num.longValue());
            else if(Float.class.getCanonicalName().equals(type))
                val = new Float(num.floatValue());
            else if(Double.class.getCanonicalName().equals(type))
                val = new Double(num.doubleValue());
        }
        return val;
    }

    @Override
    public Map<String, OperationParameter> getInputParams() {
        Map<String, OperationParameter> map=new HashMap<String, OperationParameter>();
        int count=operation.getParameterCount();
        for (int i = 0; i < count; i++) {
            OperationParameter param = operation.getInputParameter(i);
            map.put(param.getName(), param);
        }
        return map;
    }

    @Override
    public OperationParameter getOutputParam() {
        return operation.getOutputParameter();
    }

    @Override
    public Map<String, OperationParameter> getLocalParams() {
        return null;
    }
}

