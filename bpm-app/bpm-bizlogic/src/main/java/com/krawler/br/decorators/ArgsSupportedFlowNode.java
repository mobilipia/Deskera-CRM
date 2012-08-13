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

import com.krawler.br.FlowNode;
import com.krawler.br.exp.Expression;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.krawler.br.ProcessException;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class ArgsSupportedFlowNode extends FlowNodeDecorator {
    private Map<String, Expression> argumentsHolder;

    public ArgsSupportedFlowNode(FlowNode flowNode) {
        super(flowNode);
    }

    public Map<String, Expression> getArgumentsHolder() {
        return argumentsHolder;
    }

    public void setArgumentsHolder(Map<String, Expression> argumentsHolder) {
        this.argumentsHolder = argumentsHolder;
    }

    @Override
    public void invoke() throws ProcessException {
        super.setArguments(getArgumentValues());
        super.invoke();
    }

    /**
     * function to extract the actual parameters required to pass in the execution
     * the parameter definitions will be used to extract data.
     *
     * @return object map containing actual data
     * @throws com.krawler.br.ProcessException if the actual data cannot be extracted
     * because of mismatch in hashmap and parameter definition.
     */

    private Map getArgumentValues() throws ProcessException{
        Map vals=new HashMap();
        Iterator<String> itr = argumentsHolder.keySet().iterator();
        while(itr.hasNext()){
            String paramName = itr.next();
            Expression opnd=argumentsHolder.get(paramName);
            vals.put(paramName, opnd.getValue());
        }
        return vals;
    }
}
