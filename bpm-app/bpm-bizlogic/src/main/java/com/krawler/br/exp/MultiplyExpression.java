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
public class MultiplyExpression extends BinaryExpression {
    @Override
    public Object getValue() throws ProcessException {
        Object v1=getOperand().getValue();
        Object v2=getOtherOperand().getValue();

        if(v1 instanceof Number && v2 instanceof Number)
            return multiply((Number)v1,(Number)v2);

        throw new UnsupportedOperationException("multiplication of value other than numbers not supported");
    }

    private Number multiply(Number a, Number b) {
        Number val=null;
        val = a.doubleValue()*b.doubleValue();
        return val;
    }

    @Override
    public String toString() {
        return operand.toString()+"*"+otherOperand.toString();
    }

    @Override
    public VALUE_TYPE getValueType() {
        return VALUE_TYPE.NUMBER;
    }
}
