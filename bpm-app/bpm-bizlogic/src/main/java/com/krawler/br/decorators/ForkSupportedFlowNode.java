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

import com.krawler.br.FlowThread;
import com.krawler.br.FlowNode;
import com.krawler.br.ProcessException;
import java.util.Stack;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class ForkSupportedFlowNode extends FlowNodeDecorator {
    /**
     * Object to hold the sequencial direction of execution
     * this will help to determine that where to proceed after complition of
     * current node
     */
    private String forkNode;

    /**
     * getter for the next node attached to this node
     * @return node or null if no next node is attached at all
     */
    public String getForkNode() {
        return forkNode;
    }

    /**
     * convenient function to set a node of type FlowNode.NEXT
     * @param nextNode node to attach in the flow
     * @throws com.krawler.br.InvalidFlowException if attaching the given node
     * make the structure illegal
     */
    public void setForkNode(String forkNode) {
        this.forkNode = forkNode;
    }

    public ForkSupportedFlowNode(FlowNode flowNode) {
        super(flowNode);
    }

    @Override
    public void invoke() throws ProcessException {
        Thread parallelNode = new Thread(new FlowThread(getContainer().getFlowNode(forkNode)), Thread.currentThread().getName()+"1");
        parallelNode.start();
        super.invoke();
    }

    @Override
    public void init() {
        FlowNode fork = getContainer().getFlowNode(forkNode);
        if(fork.getStatus()==UNINITIALIZED)
            fork.init();
        super.init();
    }

    @Override
    public void setStatus(int status, boolean recurse) {
        super.setStatus(status, recurse);
        if(recurse)
            getContainer().getFlowNode(forkNode).setStatus(status, recurse);
    }
    
    @Override
    public boolean isInCircle(Stack ancestorStack) {
        boolean flag=super.isInCircle(ancestorStack);
        if(flag) return flag;
        ancestorStack.push(this);
        flag = getContainer().getFlowNode(forkNode).isInCircle(ancestorStack);
        ancestorStack.pop();
        return flag;
    }
}
