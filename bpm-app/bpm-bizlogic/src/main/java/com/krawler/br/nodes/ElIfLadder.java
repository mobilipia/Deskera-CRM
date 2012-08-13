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

import com.krawler.br.*;
import com.krawler.br.operations.OperationParameter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * flow node that behaves as the else if ladder
 *
 * @author Vishnu Kant Gupta
 */
public class ElIfLadder extends AbstractFlowNode {

    private List<IfBlock> ifBlocks;      // list of if blocks
    private FlowNode elseNode;  // else node if no if block satisfied
    private Map<String, OperationParameter> inputParams;
    private OperationParameter outputParam;
    private Map<String, OperationParameter> localParams;

    /**
     * gives the list of all if blocks associated with this ladder
     *
     * @return a list of if blocks
     */
    public List<IfBlock> getIfBlocks() {
        return ifBlocks;
    }

    /**
     * sets the list of if blocks
     *
     * @param ifBlocks the if block list
     */
    public void setIfBlocks(List<IfBlock> ifBlocks) {
        this.ifBlocks = ifBlocks;
    }

    /**
     * gives the node which will be executed if none of the if blocks satisfied
     * for execution
     *
     * @return the flow node for else
     */
    public FlowNode getElseNode() {
        return elseNode;
    }

    /**
     * sets the node which will be executed if none of the if blocks satisfied
     * for execution
     *
     * @param elseNode the flow node for else
     */
    public void setElseNode(FlowNode elseNode) {
        this.elseNode = elseNode;
    }

    /**
     * finds the flow node to execute based on the available data
     *
     * @param hm the available data from previously executed blocks
     * @return the flow node to execute
     * @throws com.krawler.br.ProcessException if cannot check the match for the
     * if blocks due to mismatch in supplied data and the if condition
     */
    private FlowNode findNode() throws ProcessException {
        Iterator<IfBlock> itr = ifBlocks.iterator();
        while(itr.hasNext()){
            IfBlock blk = itr.next();
            if(blk.isMatch())
                return blk.getThen();
        }
        return this.elseNode;
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

