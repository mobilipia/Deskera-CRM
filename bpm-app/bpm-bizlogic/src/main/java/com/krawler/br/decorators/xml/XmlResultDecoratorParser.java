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
import com.krawler.br.decorators.DecorationsHolder;
import com.krawler.br.decorators.DecoratorParser;
import com.krawler.br.decorators.ResultSupportedFlowNode;
import com.krawler.br.exp.Variable;
import org.w3c.dom.Element;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class XmlResultDecoratorParser extends XmlDecoratorParser {
    private static final String OUTPUT="output";

    public XmlResultDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        Element el = util.getChildElement(((XmlDecorationsHolder)dHolder).getNodeElement(),OUTPUT);
        if(el!=null){
            ResultSupportedFlowNode rf = new ResultSupportedFlowNode(baseNode);

            rf.setResultHolder((Variable)operandParser.parseExpression(el, dHolder.getScope()));
            baseNode = rf;
        }
        return super.parse(baseNode, dHolder);
    }

    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if(wrappedNode instanceof ResultSupportedFlowNode){
            ResultSupportedFlowNode node = (ResultSupportedFlowNode)wrappedNode;
            Variable e = (Variable)node.getResultHolder();

            Element nodeEl = ((XmlDecorationsHolder) dHolder).getNodeElement();
            Element outEl = util.getChildElement(nodeEl, OUTPUT);
            if(outEl==null){
                outEl=nodeEl.getOwnerDocument().createElement(OUTPUT);
                nodeEl.appendChild(outEl);
            }
            operandParser.composeExpression(outEl, e);
            return wrappedNode.getBaseNode(false);
        }else
            return super.compose(wrappedNode, dHolder);
    }

}
