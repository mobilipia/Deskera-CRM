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

package com.krawler.br;

import com.krawler.br.operations.OperationParameter;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author krawler-user
 */
public interface FlowNode {
    int COMPLETED = 2;
    int FAILED = 3;
    int FORK = 1;
    int NEXT = 2;
    int PARENT_FAILED = 4;
    int RUNNING = 1;
    int UNINITIALIZED = -1;
    int WAITING = 0;

    FlowNode getBaseNode(boolean recurse);

    FlowNodeContainer getContainer();

    void setContainer(FlowNodeContainer container);

//    /**
//     * getter for the fork node attached to this node
//     * @return node or null if no fork node is attached at all
//     */
//    FlowNode getFork();

    /**
     * gettter for identifier
     *
     * @return node id
     */
    String getId();

    /**
     * getter for parameter definitions (operands)
     *
     * @return operands
     */
    Map<String, OperationParameter> getInputParams();

    Map<String, OperationParameter> getLocalParams();
//    /**
//     * getter for the next node attached to this node
//     * @return node or null if no next node is attached at all
//     */
//    FlowNode getNext();

    OperationParameter getOutputParam();

    Object getResult();

    String getSourceid();

    /**
     * function to get the current status of the node
     * the value may be :<p>
     * FlowNode.UNINITIALIZED<br />
     * FlowNode.WAITING<br />
     * FlowNode.RUNNING<br />
     * FlowNode.COMPLETED<br />
     * FlowNode.FAILED<br />
     * FlowNode.PARENT_FAILED
     * </p>
     * @return current status
     */
    int getStatus();

    /**
     * initialization function, must be called atleast once before the execution
     * fo the node.
     */
    void init();

    /**
     * function to execute the node with the threading facility, which behave as
     * the joining point for the threads. any thread coming to this point will
     * wait until any other thread comes which is the ancestor of waiting thread.
     * the excution will be continued by the oldest thread.
     * @param sequential true to tell that this function is executed by a node for
     * which this node is a NEXT type node.
     * @throws ProcessException if the execution can not be completed due to
     * some reason
     */
    public void exec(Token info) throws ProcessException;
    /**
     * a function to execute specifilly this node only.
     * @param params parameters require for execution
     * @return result object of the execution
     * @throws java.lang.Throwable if the execution can not be completed due to
     * some reason
     */
    void invoke() throws ProcessException;

    /**
     * function to determine whether or not the given ancestor stack and the
     * current node make flow circular
     * @param ancestorStack stack of predecessor nodes in sequence
     * @return true if flow is circular, false otherwise
     */
    boolean isInCircle(Stack ancestorStack);

    /**
     * function to attach a node with this node
     * @param node node to attach in the flow
     * @param type node type (FlowNode.NEXT or FlowNode.FORK)
     * @throws com.krawler.br.InvalidFlowException if type is not supported or
     * attaching the given node make the structure illegal
     */
//    void set(FlowNode node, int type) throws InvalidFlowException;

    void setArguments(Map<String, Object> args);

//    /**
//     * convenient function to set a node of type FlowNode.FORK
//     * @param node  node to attach in the flow
//     * @throws com.krawler.br.InvalidFlowException if attaching the given node
//     * make the structure illegal
//     */
//    void setFork(FlowNode node) throws InvalidFlowException;

    /**
     * setter for identifier
     *
     * @param id id to set
     */
    void setId(String id);

//    /**
//     * convenient function to set a node of type FlowNode.NEXT
//     * @param node  node to attach in the flow
//     * @throws com.krawler.br.InvalidFlowException if attaching the given node
//     * make the structure illegal
//     */
//    void setNext(FlowNode node) throws InvalidFlowException;

    void setSourceid(String sourceid);

    /**
     * function to set the current status of the node ( and its successor )
     * @param status the status code
     * @param recurse true to set the status of successor nodes also.
     */
    void setStatus(int status, boolean recurse);

    /**
     * function to set the current status of the node, it also notify the threads
     * waiting after setting status.
     * @param status the status code
     * @throws java.lang.IllegalArgumentException if the status code is not supported
     */
    void setStatus(int status);

    void setParentCount(int count);
    int getParentCount();
}
