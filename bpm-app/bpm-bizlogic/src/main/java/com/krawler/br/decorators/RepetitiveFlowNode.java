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

package com.krawler.br.decorators;

import com.krawler.br.exp.Expression;
import com.krawler.br.*;
import com.krawler.br.exp.Variable;
import com.krawler.utils.json.base.JSONArray;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class RepetitiveFlowNode extends FlowNodeDecorator {
    private Expression multiInstance;
    private Variable currentIndex, currentElement;
    private int limit=500;

    public Expression getMultiInstance() {
        return multiInstance;
    }

    public int getLimit() {
        return limit;
    }

    public Variable getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(Variable currentIndex) {
        this.currentIndex = currentIndex;
    }

    public Variable getCurrentElement() {
        return currentElement;
    }

    public void setCurrentElement(Variable currentElement) {
        this.currentElement = currentElement;
    }

    public void setLimit(int limit) {
        if(limit>0)
            this.limit = limit;
    }

    public void setMultiInstance(Expression multiInstance) {
        this.multiInstance = multiInstance;
    }
    
    public RepetitiveFlowNode(FlowNode flowNode) {
        super(flowNode);
    }
    @Override
    public void invoke() throws ProcessException {
        Object obj= multiInstance.getValue();
        if(obj instanceof Number){
            for(int i=0;i<((Number)obj).intValue()&&i<limit&&i>=0;i++){
                invokeCurrent(i, null);
            }
        }else if(obj instanceof Set){
            Iterator itr=((Set)obj).iterator();
            while(itr.hasNext()){
                invokeCurrent(-1,itr.next());
            }
        }else if(obj instanceof Map){ // Should index be a non-integer?
            Iterator itr=((Map)obj).keySet().iterator();
            while(itr.hasNext()){
                Object key = itr.next();
                invokeCurrent(-1,((Map)obj).get(key));
            }
        }else if(obj instanceof List){
            List l = (List)obj;
            for(int i=0;i<l.size();i++){
                invokeCurrent(i, l.get(i));
            }
        }else if(obj instanceof JSONArray){
            JSONArray jArr=(JSONArray)obj;
            for(int i=0;i<jArr.length();i++){
                invokeCurrent(i, jArr.opt(i));
            }
        }else if(obj!=null && obj.getClass().isArray()){
            int len = Array.getLength(obj);
            for(int i=0;i<len;i++){
                invokeCurrent(i,Array.get(obj, i));
            }
        }else{
            throw new UnsupportedOperationException("Unrecognised MultiInstance: "+multiInstance);
        }
    }

    private void invokeCurrent(int curIdx, Object elem) throws ProcessException {
        if(currentIndex!=null)currentIndex.setValue(curIdx);
        if(currentElement!=null)currentElement.setValue(elem);
        super.invoke();
    }
}
