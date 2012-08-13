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

import com.krawler.br.loader.KwlClassLoader;
import com.krawler.br.modules.ModuleBag;
import com.krawler.br.utils.Helper;
import com.krawler.br.utils.SourceFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * class to provide the abstract view of the activity factory
 *
 * @author Vishnu Kant Gupta
 */
public class SimpleOperationBag implements OperationBag {
    /**
     * hash map to hold the operation definitions for this factory, initially
     * empty
     */
    private HashMap operations = new HashMap();

    private Map loaders;

    /**
     * the reference for the associated module factory for validation check
     */
    private ModuleBag mb;

    public void addHelpers(){
        Class helperClass = Helper.class;
        OperationDefinition od;
        Method[] methods=helperClass.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            od= new SimpleOperationDefinition();
            od.setID("kwl:"+method.getName());
            od.setName("kwl:"+method.getName());
            od.setMethodName(method.getName());
            od.setEntityName(helperClass.getCanonicalName());
            od.setEntityLoader(new KwlClassLoader());
            String[][] pdef=(String[][])Helper.DEFINITION.get(method.getName());
            OperationParameter op[] = new SimpleOperationParameter[pdef.length-1];
            if(pdef[0]!=null){
                OperationParameter out = new SimpleOperationParameter();
                out.setName(pdef[0][0]);
                out.setType(pdef[0][1]);
                od.setOutputParameter(out);
            }
            for (int j = 1; j < pdef.length; j++) {
                op[j-1] = new SimpleOperationParameter();
                op[j-1].setName(pdef[j][0]);
                op[j-1].setType(pdef[j][1]);
//                if(Boolean.getBoolean(pdef[j][2]))
//                    ap.setMulti(multi);


            }
            od.setInputParameters(op);
            addOperationDefinition(od);
         }
    }

    /**
     * provides a way to get the operation definition from this factory by supplying
     * the name/id of it from definition
     *
     * @param id the id of the operation definition
     * @return the associated operation definition or null if no operation present
     * with that id
     */
    @Override
    public OperationDefinition getOperationDefinition(String id) {
        return (OperationDefinition)operations.get(id);
    }

    /**
     * adds new operation definition in the factory, the operation definition will
     * be added only if it has valid parameters,<br />
     * note that only one operation definition with the given name can be exists in
     * the one factory. If the factory previously contained an operation definition
     * for the name, the old operation definition is replaced
     *
     * @param od the operation definition to add
     * @throws IllegalArgumentException if the operation definition does not have
     * valid parameters
     */
    protected void addOperationDefinition(OperationDefinition od) {
        if(!hasValidParameters(od))
            throw new IllegalArgumentException("Input/Output parameters are not valid for operation: "+od.getID());
        od.setOperationBag(this);
        operations.put(od.getID(), od);
    }

    /**
     * checks to see whether any operation definition presents in the factory with
     * the given name/id
     *
     * @param name the name/id of the operation definition
     * @return true if the operation definition present, false otherwise
     */
    @Override
    public boolean hasOperation(String id) {
        return operations.containsKey(id);
    }

    /**
     * returns the set of ids of all operation definitions present in the factory
     * currently
     *
     * @return the set of ids
     */
    @Override
    public Set getOperationIDs() {
        return operations.keySet();
    }

    /**
     * loads the operation definitions that can be parsed by the supplied operation
     * definition parser
     *
     * @param parser the operation definition parser from which the operations will be load
     */
    public void load(SourceFactory src) {
        OperationDefinition[] ads=((OperationDefinitionParser)src.getParser()).parse(loaders, src);
        for(int i=0;i<ads.length;i++){
            addOperationDefinition(ads[i]);
        }
    }

    /**
     * the module factory to which it is depends on, this module factory will
     * be used to validate input and out put parameters of any operation
     *
     * @param mf the module factory to associate
     * @throws IllegalArgumentException if module factory donot have sufficient
     * module definitions needed to validate already present operation definitions
     */
    @Override
    public void setModuleBag(ModuleBag mb) {
        Iterator itr = operations.keySet().iterator();
        while(itr.hasNext()){
            if(!hasValidParameters(getOperationDefinition((String)itr.next()), mb))
                throw new IllegalArgumentException("module factory does not define all required module definitions");
        }
        this.mb = mb;
    }

    /**
     * a getter method to provide access to the module factory to be used for
     * validation
     *
     * @return the associated module factory
     */
    @Override
    public ModuleBag getModuleBag() {
        return mb;
    }

    /**
     * validates the input parameters for their types, the method will check to
     * see whether the input parameter types are supported by given module
     * factory
     *
     * @param ad activity definition to check
     * @param mFac module factory for validation
     * @return true if has all valid parameters, false otherwise
     */
    public boolean hasValidParameters(OperationDefinition ad, ModuleBag mFac) {
        OperationParameter out = ad.getOutputParameter();
        if(out!=null&&!mFac.hasModule(out.getType()))
            return false;
            int count = ad.getParameterCount();
            for(int i=0;i<count;i++){
                OperationParameter ap = ad.getInputParameter(i);
                if(!mFac.hasModule(ap.getType()))
                    return false;
            }

        return true;
    }

    /**
     * validates the input parameters for their types, the method will check to
     * see whether the input parameter types are supported by associated module
     * factory
     *
     * @param ad activity definition to check
     * @return true if has all valid parameters, false otherwise
     */
    @Override
    public boolean hasValidParameters(OperationDefinition ad) {
        return hasValidParameters(ad, mb);
    }

    public Map getLoaders() {
        return loaders;
    }

    public void setLoaders(Map loaders) {
        this.loaders = loaders;
    }
}
