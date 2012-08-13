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
import com.krawler.br.ProcessException;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class ResultSupportedFlowNode extends FlowNodeDecorator {
    private Variable resultHolder;

    public ResultSupportedFlowNode(FlowNode flowNode) {
        super(flowNode);
    }

    public Variable getResultHolder() {
        return resultHolder;
    }

    public void setResultHolder(Variable resultHolder) {
        this.resultHolder = resultHolder;
    }

    @Override
    public void invoke() throws ProcessException {
        super.invoke();
        setResultValues(super.getResult());
    }

    private void setResultValues(Object val) throws ProcessException {
        resultHolder.setValue(val);
    }
}
