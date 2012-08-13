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

import com.krawler.br.exp.Expression;
import com.krawler.br.*;
import com.krawler.br.exp.Constant;
import com.krawler.br.operations.OperationParameter;
import java.util.HashMap;
import java.util.Map;

/**
 * low node that behaves as the switch case
 *
 * @author Vishnu Kant Gupta
 */
public class Switch extends AbstractFlowNode {
    private Expression expr;
    private HashMap<Integer, FlowNode> cases = new HashMap();
    private FlowNode _default;
    private Map<String, OperationParameter> inputParams;
    private OperationParameter outputParam;
    private Map<String, OperationParameter> localParams;

    /**
     * associate a case with the switch to be used for execution
     *
     * @param label the label for the case
     * @param node the flow node to execute if the label matches with the expression
     */
    public void addCase(Constant label, FlowNode node) {
        this.cases.put((Integer)label.getValue(), node);
    }

    public Map<Integer, FlowNode> getCases() {
        return this.cases;
    }

    /**
     * gives the default flow node which will be used if there is no case exists
     * which will match with expression
     *
     * @return the default flow node
     */
    public FlowNode getDefault() {
        return _default;
    }

    /**
     * sets the default flow node which will be used if there is no case exists
     * which will match with expression
     *
     * @param node the default flow node
     */
    public void setDefault(FlowNode node) {
        this._default = node;
    }

    /**
     * gives the expression to be tested for this switch
     *
     * @return the operand that represent the expression for this switch
     */
    public Expression getExpression() {
        return expr;
    }

    /**
     * sets the expression to be tested for this switch
     *
     * @param expr the operand that represent the expression for this switch
     */
    public void setExpression(Expression expr) {
        this.expr = expr;
    }

    /**
     * finds the flow node to execute based on the available data
     * 
     * @param hm the available data from previously executed blocks
     * @return the flow node to execute
     * @throws com.krawler.br.ProcessException if cannot check the match for the
     * cases due to mismatch in supplied data and the expression
     */
    private FlowNode findNode() throws ProcessException {
        Object val = (Integer)expr.getValue();
        if(cases.containsKey(val)){
            return (FlowNode)cases.get(val);
        }
        return this._default;
    }

    @Override
    public void invoke() throws ProcessException {
        FlowNode node=findNode();
        if(node!=null){
            node.init();
            node.exec(new Token(node));
        }
    }


    @Override
    public Map<String, OperationParameter> getInputParams() {
        return inputParams;
    }

    @Override
    public OperationParameter getOutputParam() {
        return outputParam;
    }
    /**
     * setter for parameter definitions (operands)
     *
     * @param inputParams params operands to set
     */

    public void setInputParams(Map<String, OperationParameter> inputParams) {
        this.inputParams = inputParams;
    }

    public void setLocalParams(Map<String, OperationParameter> localParams) {
        this.localParams = localParams;
    }

    public void setOutputParam(OperationParameter outputParam) {
        this.outputParam = outputParam;
    }

    @Override
    public Map<String, OperationParameter> getLocalParams() {
        return localParams;
    }
}
