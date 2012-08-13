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
 *
 * @author krawler-user
 */
public class GEExpression extends BinaryExpression implements ConditionalExpression {

    @Override
    public Object getValue() throws ProcessException {
        return getConditionValue();
    }

    @Override
    public boolean getConditionValue() throws ProcessException {
        return ((Comparable)operand.getValue()).compareTo(otherOperand.getValue())>=0;
    }

    @Override
    public String toString() {
        return operand.toString()+">="+otherOperand.toString();
    }

    @Override
    public VALUE_TYPE getValueType() {
        return VALUE_TYPE.BOOLEAN;
    }
}
