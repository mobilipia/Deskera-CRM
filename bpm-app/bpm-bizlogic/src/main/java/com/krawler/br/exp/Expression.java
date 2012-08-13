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
package com.krawler.br.exp;

import com.krawler.br.ProcessException;

/**
 * Interface for operands used with in process/nodes
 *
 * @author Vishnu Kant Gupta
 */
public interface Expression {

    enum VALUE_TYPE{BOOLEAN, NUMBER};
    /**
     * convenient function to extract the actual value of the operand required
     * to pass in the execution.
     * @param hm hashmap containing previous execution's result as key-value pair
     * @return actual data object
     * @throws com.krawler.br.ProcessException if the operand's value can not be
     * extracted due to mismatch in actual data in hashmap and data definition.
     */
    Object getValue () throws ProcessException;
    VALUE_TYPE getValueType();
}

