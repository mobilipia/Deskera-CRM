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

package com.krawler.br.nodes.json;

import com.krawler.br.FlowNode;
import com.krawler.br.nodes.*;
import com.krawler.br.ProcessException;
import com.krawler.br.exp.Expression;
import com.krawler.br.exp.Scope;
import com.krawler.br.exp.Variable;
import com.krawler.br.modules.ModuleBag;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * class to parse the variable from xml dom element
 *
 * @author Vishnu Kant Gupta
 */
public class JsonVariableParser extends JsonOperandParser {
    private static final String VAR="var";      // condition tag
    private static final String PATH="path";      // condition tag
    private static final String SCOPE="scope";      // condition tag
    private static final String SEPARATOR="/";      // condition tag
    private static final String INDICES="indices";      // condition tag
    private Scope[] scopes;
    private ModuleBag moduleBag;
    
    public void setScopes(Scope[] scopes) {
        this.scopes = scopes;
    }

    public void setModuleBag(ModuleBag moduleBag) {
        this.moduleBag = moduleBag;
    }

    public JsonVariableParser(JsonOperandParser successor) {
        super(successor);
    }

    private Scope findScope(Scope localScope, String scopeName) throws ProcessException{
        Scope scope = null;
        if(scopeName!=null&&scopeName.length()>0){
            for(int i = 0; i<scopes.length; i++){
                if(scopeName.equals(scopes[i].getIdentity())){
                    scope = scopes[i];
                    break;
                }
            }
        }else if(localScope.getIdentity()==null){
            scope=localScope;
        }
        if(scope==null)
            throw new ProcessException("Scope not available [" + scopeName + ":<var>]");
        return scope;
    }

    @Override
    public Expression parseExpression(JSONObject parent, Scope localScope) throws ProcessException {
        JSONObject detail = parent.optJSONObject(DETAIL);
        if(VAR.equals(parent.optString(TYPE))&&detail!=null){
            String val = detail.optString(PATH);
            String scpname = detail.optString(SCOPE);
            if(val!=null&&val.startsWith("/root/current/")){
                String path = val.substring(14);
                if(!"".equals(path)){
                    int sep = path.indexOf(SEPARATOR);
                    String name = path;
                    if(sep>=0){
                        name = path.substring(0,sep);
                        path = path.substring(sep+1);
                    }
                    Scope scp = findScope(localScope, scpname);
                    Variable var = new Variable(scp, name);
                    String module = scp.getScopeModuleName(name);
                    if(sep>=0&&!"".equals(path)){
                        String[] arr = path.split(SEPARATOR);
                        var.setPathProperties(arr);
                        module = moduleBag.getModuleName(module, arr);
                    }
                    for (ModuleBag.PRIMITIVE prim : ModuleBag.PRIMITIVE.values()) {
                        if (prim.tagName().equals(module)) {
                            var.setValueType(prim.valueType());
                            break;
                        }
                    }
                    JSONObject inds = detail.optJSONObject(INDICES);

                    if(inds!=null){
                        HashMap<Integer,List<Expression>> indices=new HashMap<Integer, List<Expression>>();
                        Iterator itr = inds.keys();
                        while(itr.hasNext()){
                            String key = (String) itr.next();
                            JSONArray arr = inds.optJSONArray(key);
                            ArrayList<Expression> list = new ArrayList<Expression>();
                            for(int i=0;i<arr.length();i++){
                                list.add(starter.parseExpression(arr.optJSONObject(i), localScope));
                            }
                            int i= Integer.parseInt(key);
                            indices.put(i, list);
                        }
                        var.setIndices(indices);
                    }
                    return var;
                }else
                    throw new ProcessException("wrong variable syntax: "+parent);
            }else
                return super.parseExpression(parent, localScope);
        }else
            return super.parseExpression(parent, localScope);
    }
}
