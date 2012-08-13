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

import com.krawler.br.exp.Scope;
import com.krawler.br.operations.OperationParameter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * The class represents the single node in the execution structure
 *
 * @author Vishnu Kant Gupta
 */
public abstract class AbstractFlowNode implements FlowNode, Scope {
    // local variable to hold name of thread to wait

    private String threadName;
    private FlowNodeContainer container;
    protected Map<String, Object> heap = new HashMap<String, Object>();

    // identifier for this node to recognise.
    private String id;
    private String sourceid;

    // number of parents on which this node is dependent to execute.
    private int parentCount = 0;
    private int status = UNINITIALIZED; // initially flow node is uninitialized
    // for execution

    /**
     * gettter for identifier
     *
     * @return node id
     */
    @Override
    public String getId() {
        return id;
    }

    @Override
    public FlowNode getBaseNode(boolean recurse) {
        return this;
    }

    @Override
    public String getIdentity() {
        return null;
    }

    /**
     * setter for identifier
     *
     * @param id id to set
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getSourceid() {
        return sourceid;
    }

    @Override
    public void setSourceid(String sourceid) {
        this.sourceid = sourceid;
    }

    @Override
    public Object getScopeValue(String key) throws ProcessException {
        return heap.get(key);
    }

    @Override
    public void removeScopeValue(String key) throws ProcessException {
        heap.remove(key);
    }

    @Override
    public void setScopeValue(String key, Object val) throws ProcessException {
        heap.put(key, val);
    }

    @Override
    public String getScopeModuleName(String key) {
        Map<String, OperationParameter> hm=getInputParams();
        if(hm!=null&&hm.containsKey(key))
            return hm.get(key).getType();
        hm = getLocalParams();
        if(hm!=null&&hm.containsKey(key))
            return hm.get(key).getType();
        if(getOutputParam()!=null)
            return getOutputParam().getType();

        return null;
    }

    @Override
    public void setArguments(Map<String, Object> args) {
        Iterator<String> itr = getInputParams().keySet().iterator();
        while (itr.hasNext()) {
            String paramName = itr.next();
            heap.put(paramName, args.get(paramName));
        }
    }

    @Override
    public Object getResult() {
        if (getOutputParam() == null) {
            return null;
        }
        return heap.get(getOutputParam().getName());
    }

    /**
     * initialization function, must be called atleast once before the execution
     * fo the node.
     */
    @Override
    public void init() {
        setStatus(WAITING);
        threadName = null;
    }

    @Override
    public int getParentCount() {
        return parentCount;
    }

    @Override
    synchronized public void setParentCount(int count) {
        parentCount = count;
        notifyAll();
    }

    @Override
    public int getStatus() {
        return status;
    }

    /**
     * function to determine whether or not the given ancestor stack and the
     * current node make flow circular
     * @param ancestorStack stack of predecessor nodes in sequence
     * @return true if flow is circular, false otherwise
     */
    @Override
    public boolean isInCircle(Stack ancestorStack) {
        if (ancestorStack.search(this) > 0) {
            return true;
        }
        return false;
    }

    /**
     * function to set the current status of the node ( and its successor )
     * @param status the status code
     * @param recurse true to set the status of successor nodes also.
     */
    @Override
    public void setStatus(int status, boolean recurse) {
        setStatus(status);
    }

    /**
     * function to set the current status of the node, it also notify the threads
     * waiting after setting status.
     * @param status the status code
     * @throws java.lang.IllegalArgumentException if the status code is not supported
     */
    @Override
    synchronized public void setStatus(int status) {
        switch (status) {
            case UNINITIALIZED:
            case WAITING:
            case RUNNING:
            case COMPLETED:
            case FAILED:
            case PARENT_FAILED:
                this.status = status;
                break;
            default:
                throw new IllegalArgumentException("Unknown status");
        }

        notifyAll();
    }

    @Override
    public final void exec(Token info) throws ProcessException {
        synchronized (this) {
            if (threadName == null || threadName.compareTo(Thread.currentThread().getName()) > 0) {
                threadName = Thread.currentThread().getName();
            }

            if (info.isSequential()) {
                setParentCount(getParentCount() - 1);
            }
        }

        while (threadName.equals(Thread.currentThread().getName()) && getStatus() != PARENT_FAILED) {
            if (getParentCount() > 0) {
                synchronized (this) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                    }
                }
            } else {
                _exec(info);
                break;
            }
        }
    }

    private final void _exec(Token info) throws ProcessException {
        synchronized (this) {
            if (getStatus() != WAITING) {
                return;
            }
            setStatus(RUNNING);
        }
        try {
            info.getWrapper().invoke();
            setStatus(COMPLETED);
        } catch (ProcessException ex) {
            setStatus(FAILED);
            throw ex;
        }
    }

    @Override
    public FlowNodeContainer getContainer() {
        return container;
    }

    @Override
    public void setContainer(FlowNodeContainer container) {
        this.container = container;
    }
}
