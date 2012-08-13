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

import com.krawler.br.ProcessException;
import com.krawler.br.modules.ModuleBag;
import com.krawler.br.modules.ModuleDefinition;
import com.krawler.br.modules.ModuleProperty;
import com.krawler.br.operations.SimpleOperationParameter;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class InitializableOperationParameter extends SimpleOperationParameter {
    Object initialValue;
    ModuleBag bag;

    public void setModuleBag(ModuleBag bag) {
        this.bag = bag;
    }

    public  String getInitialText() {
        return initialValue.toString();
    }
    public Object getInitialValue() throws ProcessException {
        try {
            return parseValue(initialValue, bag.getModuleDefinition(getType()), getMulti());
        } catch (ClassNotFoundException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } catch (InstantiationException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } catch (NoSuchMethodException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } catch (JSONException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        }
    }

    private Object parseValue(Object value, ModuleDefinition def, ModuleProperty.MULTI multi) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ProcessException, JSONException, NoSuchMethodException{
        Object instance=null;
        if(multi==null && def.getType()==ModuleDefinition.TYPE.SIMPLE){
            ModuleBag.PRIMITIVE primitive=null;
            for(ModuleBag.PRIMITIVE prim : ModuleBag.PRIMITIVE.values()){
                if(prim.tagName().equals(def.getName())){
                    primitive = prim;
                    break;
                }
            }
            switch(primitive){
                case BOOLEAN:
                    instance = (Boolean)value;
                    break;
                case BYTE:
                    instance = ((Number)value).byteValue();
                    break;
                case CHAR:
                    instance = new Character(((String)value).charAt(0));
                    break;
                case DATE:
                    instance = new Date(((Number)value).longValue());
                    break;
                case DOUBLE:
                    instance = ((Number)value).doubleValue();
                    break;
                case FLOAT:
                    instance = ((Number)value).floatValue();
                    break;
                case INT:
                    instance = ((Number)value).intValue();
                    break;
                case LONG:
                    instance = ((Number)value).longValue();
                    break;
                case SHORT:
                    instance = ((Number)value).shortValue();
                    break;
                case STRING:
                    instance = (String)value;
                    break;
            }
        }else if(multi==null&&value instanceof JSONObject){
            JSONObject jObj = (JSONObject)value;
            Iterator itr = jObj.keys();
            switch(def.getType()){
                case JSON:
                    instance = new JSONObject();
                    while(itr.hasNext()){
                        String key = (String)itr.next();
                        ModuleProperty prop=def.getProperty(key);
                        ((JSONObject)instance).put(key, parseValue(jObj.opt(key), bag.getModuleDefinition(prop.getType()), prop.getMulti()));
                    }
                    break;
                case MAP:
                    instance = new java.util.HashMap();
                    while(itr.hasNext()){
                        String key = (String)itr.next();
                        ModuleProperty prop=def.getProperty(key);
                        ((HashMap)instance).put(key, parseValue(jObj.opt(key), bag.getModuleDefinition(prop.getType()), prop.getMulti()));
                    }
                    break;
                case POJO:
                    Class clazz=Class.forName(def.getClassName());
                    instance = clazz.newInstance();
                    while(itr.hasNext()){
                        String key = (String)itr.next();
                        ModuleProperty prop=def.getProperty(key);
                        String temp="";
                        if(key.length()>0){
                            temp=Character.toUpperCase(key.charAt(0))+key.substring(1);
                        }
                        Object val=parseValue(jObj.opt(key), bag.getModuleDefinition(prop.getType()), prop.getMulti());
                        Method m = clazz.getMethod("set" + temp,val.getClass());
                        m.invoke(instance,val);
                    }
                    break;
            }
        }else if(multi!=null && value instanceof JSONArray){
            JSONArray jArr = (JSONArray)value;
            switch(multi){
                case ARRAY:
                    instance = Array.newInstance(Class.forName(def.getClassName()), jArr.length());
                    for(int i=0;i<jArr.length();i++){
                        Array.set(instance,i,parseValue(jArr.opt(i), def, null));
                    }
                    break;
                case JSONARRAY:
                    instance = new JSONArray();
                    for(int i=0;i<jArr.length();i++){
                        ((JSONArray)instance).put(parseValue(jArr.opt(i), def, null));
                    }
                    break;
                case LIST:
                    instance = new ArrayList();
                    for(int i=0;i<jArr.length();i++){
                        ((Collection)instance).add(parseValue(jArr.opt(i), def, null));
                    }
                    break;
                case SET:
                    instance = new HashSet();
                    for(int i=0;i<jArr.length();i++){
                        ((HashSet)instance).add(parseValue(jArr.opt(i), def, null));
                    }
                    break;
            }
        }
        if(instance==null)
            throw new ProcessException("Initialization format not supported");

        return instance;
    }

    public void setInitialValue(Object initialValue) {
        this.initialValue = initialValue;
    }
}
