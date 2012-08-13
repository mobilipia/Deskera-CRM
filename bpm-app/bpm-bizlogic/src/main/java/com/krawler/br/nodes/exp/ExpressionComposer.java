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

package com.krawler.br.nodes.exp;

import com.krawler.br.ProcessException;
import com.krawler.br.exp.*;
import com.krawler.br.exp.Variable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author krawler-user
 */
public class ExpressionComposer {

    static boolean canCompose(Expression operand) {
        if(operand instanceof AddExpression || operand instanceof SubtractExpression||
                operand instanceof MultiplyExpression || operand instanceof DivideExpression||
                operand instanceof ModuloExpression || operand instanceof Variable ||
                operand instanceof Constant)
            return true;
        return false;
    }

    public String compose(Expression expression) throws ProcessException {
        try {
            Method m = ExpressionComposer.class.getMethod("compose" + expression.getClass().getSimpleName(), Expression.class);
            return (String)m.invoke(this, expression);
        } catch (Exception ex) {
            throw new ProcessException("Uncomposable expression"+expression);
        }
    }

    public String composeAddExpression(Expression expression) throws ProcessException {
        AddExpression e=(AddExpression)expression;
        return compose(e.getOperand())+"+"+compose(e.getOtherOperand());
    }
    public String composeSubtractExpression(Expression expression) throws ProcessException {
        SubtractExpression e=(SubtractExpression)expression;
        return compose(e.getOperand())+"-"+compose(e.getOtherOperand());
    }
    public String composeMultiplyExpression(Expression expression) throws ProcessException {
        MultiplyExpression e=(MultiplyExpression)expression;
        return compose(e.getOperand())+"*"+compose(e.getOtherOperand());
    }
    public String composeDivideExpression(Expression expression) throws ProcessException {
        DivideExpression e=(DivideExpression)expression;
        return compose(e.getOperand())+"/"+compose(e.getOtherOperand());
    }
    public String composeModuloExpression(Expression expression) throws ProcessException {
        ModuloExpression e=(ModuloExpression)expression;
        return compose(e.getOperand())+"%"+compose(e.getOtherOperand());
    }
    public String composeVariable(Expression expression) throws ProcessException {
        final String SEPARATOR = ".";
        Variable var = (Variable) expression;
        String[] props = var.getPathProperties();
        String text = var.getName();
        Map<Integer, List<Expression>> indices = var.getIndices();
        int i=0;
        if(indices!=null&&indices.containsKey(i)){
            List<Expression> l = indices.get(i);
            if(l!=null&&!l.isEmpty()){
                Iterator<Expression> itr = l.iterator();
                while(itr.hasNext())
                    text+="["+itr.next().toString()+"]";
            }
        }
        if(props!=null){
            while(i<props.length){
                text+=SEPARATOR+props[i];
                i++;
                if(indices!=null&&indices.containsKey(i)){
                    List<Expression> l = indices.get(i);
                    if(l!=null&&!l.isEmpty()){
                        Iterator<Expression> itr = l.iterator();
                        while(itr.hasNext())
                            text+="["+itr.next().toString()+"]";
                    }
                }
            }
        }
        if(var.getScope().getIdentity()!=null)
            text = var.getScope().getIdentity()+Scope.SEPARATOR+text;
        return text;
    }
    public String composeConstant(Expression expression) {
        Constant var = (Constant)expression;
        return var.toString();
    }
}
