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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class to provide the busines process functionality
 *
 * @author Vishnu Kant Gupta
 */
public class BProcess extends AbstractFlowNode implements BusinessProcess, FlowNodeContainer {
    private String initialNode;

    private Map<String,FlowNode> nodes=new HashMap<String, FlowNode>();   // initial node of the business process
    private Map<String, OperationParameter> inputParams=new HashMap<String, OperationParameter>();
    private OperationParameter outputParam;
    private Map<String, OperationParameter> localParams=new HashMap<String, OperationParameter>();
    private String view;

    /**
     * gives the initial node of the process
     *
     * @return initial flow node
     */
    public String getInitialNode() {
        return initialNode;
    }

    /**
     * sets the initial flownode for the process,
     * <p>
     * the process excution will start from this intial node.
     * </p>
     *
     * @param initialNode the flow node to set as initial node
     */
    public void setInitialNode(String initialNode) {
        this.initialNode = initialNode;
    }

    @Override
    public void invoke() throws ProcessException {
        FlowNode initial = nodes.get(initialNode);
        if(initial==null)
            throw new ProcessException("Process '"+getId()+"' does not have structure");
        initHeap();
        initial.setStatus(FlowNode.UNINITIALIZED, true);
        initial.init();
        initial.exec(new Token(initial));
    }

    @Override
    public Object execute(Map params) throws ProcessException {
        setArguments(params);
        invoke();
        return getResult();
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

    private void initHeap() throws ProcessException{
        Iterator<String> itr=this.localParams.keySet().iterator();
        while(itr.hasNext()){
            String param = itr.next();
            OperationParameter op = this.localParams.get(param);
            if(op instanceof InitializableOperationParameter){
                InitializableOperationParameter temp = (InitializableOperationParameter)op;
                heap.put(param, temp.getInitialValue());
            }
        }
        
        if(this.outputParam!=null&& this.outputParam instanceof  InitializableOperationParameter){
                InitializableOperationParameter temp = (InitializableOperationParameter)this.outputParam;
                heap.put(this.outputParam.getName(), temp.getInitialValue());            
        }
    }

    @Override
    public void addFlowNode(FlowNode node) throws ProcessException{
        if(node.getId()==null)
            throw new ProcessException("Node definition with no id in '"+this.getId()+"'");
        if(nodes.containsKey(node.getId()))
            throw new ProcessException("Node definition already exists for nodeid '"+node.getId()+"'");

        node.setContainer(this);
        nodes.put(node.getId(), node);
    }

    public void setView(String view) {
        this.view = view;
    }

    @Override
    public FlowNode getFlowNode(String nodeid) {
        return nodes.get(nodeid);
    }

    @Override
    public boolean hasFlowNode(String nodeid) {
        return nodes.containsKey(nodeid);
    }

    @Override
    public Map<String, FlowNode> getAllFlowNodes() {
        return nodes;
    }

    @Override
    public Map<String, OperationParameter> getLocalParams() {
        return localParams;
    }

    @Override
    public String getView() {
        return view;
    }
}

