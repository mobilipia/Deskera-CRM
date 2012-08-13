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

package com.krawler.br.stmt;

import com.krawler.br.ProcessException;
import com.krawler.br.exp.Expression;
import com.krawler.br.exp.Variable;

/**
 *
 * @author krawler-user
 */
public class Assignment implements Statement {
    private Variable lhs;
    private Expression rhs;
    @Override
    public void execute() throws ProcessException {
        lhs.setValue(rhs.getValue());
    }

    public Variable getLhs() {
        return lhs;
    }

    public void setLhs(Variable lhs) {
        this.lhs = lhs;
    }

    public Expression getRhs() {
        return rhs;
    }

    public void setRhs(Expression rhs) {
        this.rhs = rhs;
    }

    @Override
    public String toString(){
        return lhs.toString()+'='+rhs.toString()+';';
    }
}
