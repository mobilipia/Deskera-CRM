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

import com.krawler.br.exp.Variable;
import com.krawler.br.FlowNode;
import com.krawler.br.exp.Expression;
import com.krawler.br.ProcessException;

/**
 *
 * @author krawler-user
 */
public class RemovalSupportedFlowNode extends FlowNodeDecorator {
    Variable valueContainer;
    Expression value;
    boolean after;
    String key;

    public RemovalSupportedFlowNode(FlowNode flowNode) {
        super(flowNode);
    }

    public Variable getValueContainer() {
        return valueContainer;
    }

    public void setValueContainer(Variable valueContainer) {
        this.valueContainer = valueContainer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isAfter() {
        return after;
    }

    public void setAfter(boolean after) {
        this.after = after;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    @Override
    public void invoke() throws ProcessException {
        if(!after)
            valueContainer.remove(key, value);

        super.invoke();

        if(after)
            valueContainer.remove(key, value);
    }

}
