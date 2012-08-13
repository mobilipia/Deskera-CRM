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
import com.krawler.br.operations.OperationParameter;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author Vishnu Kant Gupta
 */
public abstract class FlowNodeDecorator implements FlowNode {
    private FlowNode flowNode;

    public FlowNodeDecorator(FlowNode flowNode) {
        this.flowNode = flowNode;
    }

    @Override
    public FlowNode getBaseNode(boolean recurse){
        if(recurse)
            return flowNode.getBaseNode(recurse);
        
        return flowNode;
    }

    @Override
    public Map<String, OperationParameter> getInputParams() {
        return flowNode.getInputParams();
    }

    @Override
    public OperationParameter getOutputParam() {
        return flowNode.getOutputParam();
    }

    @Override
    public Map<String, OperationParameter> getLocalParams() {
        return flowNode.getLocalParams();
    }

    @Override
    public void invoke() throws ProcessException {
        flowNode.invoke();
    }

    @Override
    public void exec(Token info) throws ProcessException {
        flowNode.exec(info);
    }

    @Override
    public Object getResult() {
        return flowNode.getResult();
    }

    @Override
    public void setArguments(Map<String, Object> args) {
        flowNode.setArguments(args);
    }

    @Override
    public int getParentCount() {
        return flowNode.getParentCount();
    }

    @Override
    public void setParentCount(int count) {
        flowNode.setParentCount(count);
    }

    @Override
    public String getId() {
        return  flowNode.getId();
    }

    @Override
    public String getSourceid() {
        return flowNode.getSourceid();
    }

    @Override
    public int getStatus() {
        return flowNode.getStatus();
    }

    @Override
    public void init() {
        flowNode.init();
    }

    @Override
    public boolean isInCircle(Stack ancestorStack) {
        return flowNode.isInCircle(ancestorStack);
    }

    @Override
    public void setId(String id) {
        flowNode.setId(id);
    }

    @Override
    public void setSourceid(String sourceid) {
        flowNode.setSourceid(sourceid);
    }

    @Override
    public void setStatus(int status, boolean recurse) {
        flowNode.setStatus(status, recurse);
    }

    @Override
    public void setStatus(int status) {
        flowNode.setStatus(status);
    }

    @Override
    public FlowNodeContainer getContainer() {
        return flowNode.getContainer();
    }

    @Override
    public void setContainer(FlowNodeContainer container) {
        flowNode.setContainer(container);
    }

}
