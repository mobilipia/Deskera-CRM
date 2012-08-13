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

package com.krawler.br.modules;

import com.krawler.br.utils.SourceFactory;
import java.util.HashMap;
import java.util.Set;

/**
 * class representing the abstract behaviour of mudule factory
 *
 * @author Vishnu Kant Gupta
 */
public class SimpleModuleBag implements ModuleBag {
    /**
     * hash map to hold the module definitions for this factory, initially
     * empty
     */
    private HashMap modules=new HashMap();

    /**
     * default constructor, used to automatically add primitive types in the module
     * factory
     */
    public SimpleModuleBag() {
        addPrimitives();
    }

    /**
     * function to add module definitions for primitive types
     */
    synchronized public void addPrimitives(){
        for(PRIMITIVE prim: PRIMITIVE.values()){
            ModuleDefinition md=new SimpleModuleDefinition();
            md.setName(prim.tagName());
            md.setType(prim.type());
            md.setClassName(prim.className());
            addModuleDefinition(md);
        }
    }

    @Override
    public ModuleDefinition getModuleDefinition(String name) {
        return (ModuleDefinition)modules.get(name);
    }

    /**
     * adds new module definition in the factory, the module definition will
     * be added only if it is not null,<br />
     * note that only one module definition with the given name can be exists in
     * the one factory. If the factory previously contained a module definition
     * for the name, the old module definition is replaced
     *
     * @param md the module definition to add
     * @throws NullPointerException if the module definition is null
     */
    protected void addModuleDefinition(ModuleDefinition md) {
        modules.put(md.getName(), md);
    }
    
    @Override
    public boolean hasModule(String name) {
        return modules.containsKey(name);
    }

    @Override
    public Set getModuleNames() {
        return modules.keySet();
    }

    @Override
    public boolean isValidPropertyPath(String moduleName, String[] props) {
        return getModuleName(moduleName, props)!=null;
    }

    @Override
    public String getModuleName(String moduleName, String[] props) {
        if(props==null||props.length==0)
            return moduleName;
        int i=0,len=props.length;
        do{
            if(hasModule(moduleName)){
                if(i>=len)
                    return moduleName;
                ModuleDefinition md = getModuleDefinition(moduleName);
                if(md.hasProperty(props[i])){
                    moduleName = md.getProperty(props[i]).getType();
                    i++;
                }else
                    return null;
            }else
                return null;
        }while(true);
    }

    /**
     * loads the module definitions that can be parsed by the supplied module
     * definition parser
     *
     * @param parser the module definition parser from which the modules will be load
     */
    public void load(SourceFactory src){
        ModuleDefinition[] mds=((ModuleDefinitionParser)src.getParser()).parse(src);
        for(int i=0;i<mds.length;i++){
            addModuleDefinition(mds[i]);
        }
    }
}
