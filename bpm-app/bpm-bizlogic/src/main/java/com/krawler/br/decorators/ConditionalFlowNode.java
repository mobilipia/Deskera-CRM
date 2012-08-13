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

import com.krawler.br.*;
import com.krawler.br.exp.ConditionalExpression;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class ConditionalFlowNode extends FlowNodeDecorator {
    /**
     * Object to hold the condition of execution
     * this will be used to determine that if the node can be executed or not
     */
    private ConditionalExpression condition;

    public ConditionalFlowNode(FlowNode flowNode) {
        super(flowNode);
    }

    /**
     * getter for condition
     *
     * @return condition on which the execution depends or null(means without condition)
     */
    public ConditionalExpression getCondition() {
        return condition;
    }

    /**
     * setter for condition
     *
     * @param condition condition to set
     */
    public void setCondition(ConditionalExpression condition) {
        this.condition = condition;
    }

    @Override
    public void invoke() throws ProcessException {
        if(condition==null||condition.getConditionValue()==true)
            super.invoke();
    }
}
