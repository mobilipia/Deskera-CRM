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
package com.krawler.br.exp;

import com.krawler.br.*;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * represents the class for variables used in business rules
 *
 * @author Vishnu Kant Gupta
 */
public class Variable implements Expression {
    private static final String SEPARATOR=".";
    private String path;    // path of properties seperated with 'SEPERATOR'
    private String name;    // path of properties seperated with 'SEPERATOR'
    private Scope scope;
    private VALUE_TYPE valueType;
    Map<Integer, List<Expression>> indices;

    /**
     * creates a variable with the specified scope and the given name
     *
     * @param scope scope for the variable
     * @param name name of variable
     */
    public Variable (Scope scope, String name) throws ProcessException {
        name = name.trim();
        if(name==null||name.length()==0)
            throw new ProcessException("Can't create Variable without name");
        if(scope==null)
            throw new ProcessException("Can't create Variable("+name+") without scope");
        this.scope = scope;
        this.name = name;
    }

    public Scope getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public Map<Integer, List<Expression>> getIndices() {
        return indices;
    }

    public void setIndices(Map<Integer, List<Expression>> indices) {
        this.indices = indices;
    }

    /**
     * sets the list of properties of this variable to generate the path
     *
     * @param props property names in assending order
     */
    public void setPathProperties(String[] props) throws ProcessException {
        if(props==null||props.length==0)
            this.path=null;
        else{
            this.path="";
            for(int i=0; i<props.length;i++){
                if(props[i]==null||(props[i]=props[i].trim()).length()==0)
                    throw new ProcessException("Blank property name found in variable's path");
                this.path+=SEPARATOR+props[i];
            }
            this.path=this.path.substring(1);
        }
    }

    /**
     * gets the list of properties of this variable to generate the path
     *
     * @return property names in assending order
     */
    public String[] getPathProperties() {
        if(path==null)
            return null;
        return path.split("\\"+SEPARATOR);
    }

    @Override
    public Object getValue() throws ProcessException {
        String[] props= getPathProperties();
        Object val=scope.getScopeValue(name);
            try {
                int level = 0;
                val = getIndexedElement(val, level);
                while(props!=null&&level<props.length){
                    val = getProperty(val, props[level]);
                    level++;
                    val = getIndexedElement(val, level);
                }
            } catch (NoSuchMethodException ex) {
                throw new ProcessException("property not found: "+this,ex);
            } catch (IllegalAccessException ex) {
                throw new ProcessException("property not accessible: "+this,ex);
            } catch (IllegalArgumentException ex) {
                throw new ProcessException("property not found: "+this,ex);
            } catch (InvocationTargetException ex) {
                throw new ProcessException("exception occured in accessing property: "+this,ex.getCause());
            }
        return val;
    }

    /**
     * find the property within the given object
     *
     * @param container the container object of property
     * @param props array of property names in sequence
     * @param level level of property from its top container
     * @return object containing the value of property
     * @throws java.lang.IllegalAccessException if property is no accessible
     * @throws java.lang.IllegalArgumentException if arguments are incorrect
     * @throws java.lang.reflect.InvocationTargetException if the target method
     * can not be invoked
     * @throws com.krawler.utils.json.base.JSONException if exception occured
     * while accessing the property value from json
     * @throws java.lang.NoSuchMethodException if the container is a POJO and the
     * getter does not exists for the given property.
     */
    private Object getProperty(Object container, String prop) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ProcessException {
        if(container==null)
            return container;
        if(container instanceof java.util.Map){
            return ((java.util.Map)container).get(prop);
        }else if(container instanceof JSONObject){
            return ((JSONObject)container).opt(prop);
        }else {
            return PropertyUtils.getProperty(container, prop);
        }
    }

    private Object getIndexedElement(Object container, int index) throws ProcessException{
        if(indices!=null && indices.containsKey(index)){
            Iterator<Expression> itr = indices.get(index).iterator();
            while(container!=null&&itr.hasNext()){
                int idx = ((Number)itr.next().getValue()).intValue();
                if(container instanceof JSONArray){
                    container = ((JSONArray)container).opt(idx);
                }else if(container instanceof java.util.List){
                    container = ((java.util.List)container).get(idx);
                }else if(container.getClass().isArray()){
                    container = Array.get(container, idx);
                }else
                    container = null;
            }
        }

        return container;
    }

    private void setProperty(Object container, String prop, Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ProcessException {
        if(container==null){
            throw new ProcessException("container not found for variable: "+this);
        }

        if(container instanceof java.util.Map){
            ((java.util.Map)container).put(prop,val);
        }else if(container instanceof JSONObject){
            try {
                ((JSONObject) container).put(prop, val);
            } catch (JSONException ex) {
                throw new ProcessException(ex);
            }
        }else{
            PropertyUtils.setProperty(container, prop, val);
        }
    }

    private void setIndexedElement(Object cont, int index, Object val, boolean insert) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ProcessException {
        if (cont == null) {
            throw new ProcessException("container not found for variable: " + this);
        } else {
            if(indices!=null && indices.containsKey(index)){
                Iterator<Expression> itr = indices.get(index).iterator();
                Object container=null;
                while(cont!=null&&itr.hasNext()){
                    container = cont;
                    int idx = ((Number)itr.next().getValue()).intValue();
                    if(itr.hasNext()){
                        if(cont instanceof JSONArray){
                            cont = ((JSONArray)cont).opt(idx);
                        }else if(cont instanceof java.util.List){
                            cont = ((java.util.List)cont).get(idx);
                        }else if(cont.getClass().isArray()){
                            cont = Array.get(cont, idx);
                        }else
                            throw new ProcessException("Unsuported container type found in variable: " + this);
                    }else{
                        if(container==null)
                            throw new ProcessException("container not found for variable: " + this);
                        else if (container instanceof JSONArray) {
                            try {
                                JSONArray jArr = (JSONArray) container;
                                int last = jArr.length();
                                if (insert) {
                                    jArr.put(val);
                                }

                                if (insert && last > idx) {
                                    jArr.put(last, jArr.get(last - 1));
                                    last--;
                                }

                                jArr.put(idx, val);
                            } catch (JSONException ex) {
                                throw new ProcessException(ex);
                            }
                        } else if (container instanceof java.util.List) {
                            java.util.List list = (java.util.List) container;
                            if (idx == list.size()) {
                                list.add(val);
                            } else if (insert) {
                                list.add(idx, val);
                            } else {
                                list.set(idx, val);
                            }
                        } else if (container.getClass().isArray()) {
                            Array.set(container, idx, val);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void setValue(Object value) throws ProcessException {
        setValue(value, false);
    }


    public void setValue(Object val, boolean insert) throws ProcessException {
        try {
            if(path!=null){
                Object container = scope.getScopeValue(name);
                String[] props=getPathProperties();
                int lastIdx = props.length-1;
                int i=0;
                while(i<lastIdx){
                    container=getProperty(container, props[i]);
                    i++;
                    container=getIndexedElement(container, i);
                }
                if(indices!=null&&indices.containsKey(lastIdx+1)){
                    container=getProperty(container, props[lastIdx]);
                    setIndexedElement(container, lastIdx+1, val, insert);
                }else
                    setProperty(container, props[lastIdx], val);
            }else{
                if(indices!=null&&indices.containsKey(0)){
                    Object container=scope.getScopeValue(name);
                    setIndexedElement(container, 0, val, insert);
                }else
                    scope.setScopeValue(name, val);
            }
        } catch (NoSuchMethodException ex) {
            throw new ProcessException("property not found: "+this,ex);
        } catch (IllegalAccessException ex) {
            throw new ProcessException("property not accessible: "+this,ex);
        } catch (InvocationTargetException ex) {
            throw new ProcessException("exception occured in accessing property: "+this,ex.getCause());
        }
    }

    public boolean isEmpty() throws ProcessException{
        return length()==0;
    }

    public int length() throws ProcessException{
        Object obj = getValue();
        if(obj instanceof JSONObject) return ((JSONObject)obj).length();
        else if(obj instanceof Map) return ((Map)obj).size();
        else if(obj instanceof JSONArray) return ((JSONArray)obj).length();
        else if(obj instanceof Collection) return ((Collection)obj).size();

        throw new ProcessException("not supported for this type");
    }

    public void remove(String key, Object value) throws ProcessException {
        try {
            if(path!=null){
                Object container = scope.getScopeValue(name);
                String[] props=getPathProperties();
                int lastIdx = props.length-1;
                int i=0;
                while(i<lastIdx){
                    container=getProperty(container, props[i]);
                    i++;
                    container=getIndexedElement(container, i);
                }
                if(indices!=null&&indices.containsKey(lastIdx+1)){
                    container=getProperty(container, props[lastIdx]);
                    removeIndexedElement(container, lastIdx+1);
                }else
                    removeProperty(container, props[lastIdx]);
            }else{
                if(indices!=null&&indices.containsKey(0)){
                    Object container=scope.getScopeValue(name);
                    removeIndexedElement(container, 0);
                }else
                    scope.removeScopeValue(name);
            }
        } catch (NoSuchMethodException ex) {
            throw new ProcessException("property not found: "+this,ex);
        } catch (IllegalAccessException ex) {
            throw new ProcessException("property not accessible: "+this,ex);
        } catch (IllegalArgumentException ex) {
            throw new ProcessException("property not found: "+this,ex);
        } catch (InvocationTargetException ex) {
            throw new ProcessException("exception occured in accessing property: "+this,ex.getCause());
        }
    }

    private void removeProperty(Object container, String prop) throws ProcessException {
        if(container==null){
            throw new ProcessException("container not found for variable: "+this);
        }

        if(container instanceof java.util.Map){
            ((java.util.Map)container).remove(prop);
        }else if(container instanceof JSONObject){
            ((JSONObject) container).remove(prop);
        }else{
            throw new ProcessException("Remove not supported on "+container.getClass().getName());
        }
    }

    private void removeIndexedElement(Object cont, int index) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, ProcessException {
        if (cont == null) {
            throw new ProcessException("container not found for variable: " + this);
        } else {
            if(indices!=null && indices.containsKey(index)){
                Iterator<Expression> itr = indices.get(index).iterator();
                Object container=null;
                while(cont!=null&&itr.hasNext()){
                    container = cont;
                    int idx = ((Number)itr.next().getValue()).intValue();
                    if(itr.hasNext()){
                        if(cont instanceof JSONArray){
                            cont = ((JSONArray)cont).opt(idx);
                        }else if(cont instanceof java.util.List){
                            cont = ((java.util.List)cont).get(idx);
                        }else if(cont.getClass().isArray()){
                            cont = Array.get(cont, idx);
                        }else
                            throw new ProcessException("Unsuported container type found in variable: " + this);
                    }else{
                        if(container==null)
                            throw new ProcessException("container not found for variable: " + this);
                        else if (container instanceof JSONArray) {
                            JSONArray jArr = (JSONArray) container;
                            jArr.remove(index);
                        } else if (container instanceof java.util.List) {
                            java.util.List list = (java.util.List) container;
                            list.remove(index);
                        } else {
                            throw new ProcessException("Remove not supported on "+container.getClass().getName());
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        String[] props = getPathProperties();
        String text = name;
        int i=0;
        if(indices!=null&&indices.containsKey(i)){
            List<Expression> l = indices.get(i);
            if(l!=null&&!l.isEmpty()){
                Iterator<Expression> itr = l.iterator();
                while(itr.hasNext())
                    text+="["+itr.next().toString()+"]";
            }
        }
        if(props!=null){
            while(i<props.length){
                text+=SEPARATOR+props[i];
                i++;
                if(indices!=null&&indices.containsKey(i)){
                    List<Expression> l = indices.get(i);
                    if(l!=null&&!l.isEmpty()){
                        Iterator<Expression> itr = l.iterator();
                        while(itr.hasNext())
                            text+="["+itr.next().toString()+"]";
                    }
                }
            }
        }
        if(scope.getIdentity()!=null)
            text=scope.getIdentity()+Scope.SEPARATOR+text;
        return text;
    }

    public void setValueType(VALUE_TYPE valueType) {
        this.valueType = valueType;
    }

    @Override
    public VALUE_TYPE getValueType() {
        return valueType;
    }
}

