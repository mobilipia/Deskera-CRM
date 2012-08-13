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
import com.krawler.br.exp.Expression;
import com.krawler.br.ProcessException;
import com.krawler.br.decorators.ArgsSupportedFlowNode;
import com.krawler.br.decorators.DecorationsHolder;
import com.krawler.br.decorators.DecoratorParser;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class XmlArgsDecoratorParser extends XmlDecoratorParser {
    private static final String INPUTS="inputs";
    private static final String VAR = "var";
    private static final String NAME = "name";

    public XmlArgsDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        List<Element> list = util.getChildElements(util.getChildElement(((XmlDecorationsHolder) dHolder).getNodeElement(), INPUTS), VAR);
        HashMap ops = new HashMap();
        for (int i = 0; i < list.size(); i++) {
            Element opEl = list.get(i);
            Expression op = operandParser.parseExpression(opEl, dHolder.getScope());
            if (op != null) {
                String name = opEl.getAttribute(NAME);
                ops.put(name, op);
            }
        }
        if (!ops.isEmpty()) {
            ArgsSupportedFlowNode af = new ArgsSupportedFlowNode(baseNode);
            af.setArgumentsHolder(ops);
            baseNode = af;
        }
        return super.parse(baseNode, dHolder);
    }

    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if(wrappedNode instanceof ArgsSupportedFlowNode){
            ArgsSupportedFlowNode node = (ArgsSupportedFlowNode)wrappedNode;
            Map<String, Expression> ops = node.getArgumentsHolder();
            Iterator<String> itr = ops.keySet().iterator();
            Element nodeEl = ((XmlDecorationsHolder) dHolder).getNodeElement();
            Element varsEl = util.getChildElement(nodeEl, INPUTS);
            while(itr.hasNext()){
                String name = itr.next();
                Expression op = ops.get(name);
                if(varsEl==null){
                    varsEl=nodeEl.getOwnerDocument().createElement(INPUTS);
                    nodeEl.appendChild(varsEl);
                }
                Element el = nodeEl.getOwnerDocument().createElement(VAR);
                varsEl.appendChild(el);
                el.setAttribute(NAME, name);
                operandParser.composeExpression(el, op);
            }
            return wrappedNode.getBaseNode(false);
        }else
            return super.compose(wrappedNode, dHolder);
    }
}
