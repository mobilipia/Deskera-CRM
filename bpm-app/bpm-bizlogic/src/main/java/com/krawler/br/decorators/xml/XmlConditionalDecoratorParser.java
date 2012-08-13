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

package com.krawler.br.decorators.xml;

import com.krawler.br.FlowNode;
import com.krawler.br.ProcessException;
import com.krawler.br.decorators.ConditionalFlowNode;
import com.krawler.br.decorators.DecorationsHolder;
import com.krawler.br.decorators.DecoratorParser;
import com.krawler.br.exp.ConditionalExpression;
import com.krawler.br.exp.Expression;
import org.w3c.dom.Element;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class XmlConditionalDecoratorParser extends XmlDecoratorParser {
    private static final String CONDITION="condition";

    public XmlConditionalDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        Element el = util.getChildElement(((XmlDecorationsHolder) dHolder).getNodeElement(), CONDITION);
        if(el!=null){
            ConditionalExpression c = (ConditionalExpression)operandParser.parseExpression(el, dHolder.getScope());
            ConditionalFlowNode cf = new ConditionalFlowNode(baseNode);
            cf.setCondition(c);
            baseNode = cf;
        }
        return super.parse(baseNode, dHolder);
    }

    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if(wrappedNode instanceof ConditionalFlowNode){
            ConditionalFlowNode node = (ConditionalFlowNode)wrappedNode;
            Element nodeEl = ((XmlDecorationsHolder) dHolder).getNodeElement();
            ConditionalExpression c = node.getCondition();
            Element el= nodeEl.getOwnerDocument().createElement(CONDITION);
            operandParser.composeExpression(el, (Expression)c);
            nodeEl.appendChild(el);
            return wrappedNode.getBaseNode(false);
        }else
            return super.compose(wrappedNode, dHolder);
    }
}
