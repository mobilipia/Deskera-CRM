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
import com.krawler.br.exp.ConditionalExpression;

/**
 *  class to mimic the behaviour of conditional statement
 *
 * @author Vishnu Kant Gupta
 */
public class IfBlock {
    private ConditionalExpression when; // condition ofr if block
    private FlowNode then;  // node to execute if condition satisfied

    /**
     * gives the flow node for this if block
     * 
     * @return the flow node
     */
    public FlowNode getThen() {
        return then;
    }

    /**
     * associate a flow node with this if block that will bre executed if the
     * condition satisfied for the if block
     *
     * @param then the flow node
     */
    public void setThen(FlowNode then) {
        this.then = then;
    }

    /**
     * checks whether this fi block satisfies the match for execution with the
     * supplied hash map data
     *
     * @param hm the hashmap with the previously executed result data
     * @return true if the condition satisfies, false otherwise
     * @throws com.krawler.br.ProcessException if the comparison cannot be
     * performed
     */
    public boolean isMatch() throws ProcessException {
        return when.getConditionValue();
    }

    /**
     * sets the condition for this if block to be tested
     * 
     * @param when the condition
     */
    public void setWhen(ConditionalExpression when) {
        this.when = when;
    }

    /**
     * gives the flow node for this if block
     *
     * @return the flow node
     */
    public ConditionalExpression getWhen() {
        return when;
    }
}
