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
import com.krawler.br.decorators.RepetitiveFlowNode;
import com.krawler.br.exp.Expression;
import com.krawler.br.exp.Variable;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class XmlRepetitiveDecoratorParser extends XmlDecoratorParser {

    private static final String REPEAT = "repeat";      // condition tag
    private static final String REPEAT_ON = "on";      // condition tag
    private static final String LIMIT = "max-limit";
    private static final String CURRENT_INDEX = "index";
    private static final String CURRENT_ELEMENT = "element";

    public XmlRepetitiveDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        List<Element> list = util.getChildElements(((XmlDecorationsHolder) dHolder).getNodeElement(), REPEAT);
        Iterator<Element> itr = list.iterator();
        while (itr.hasNext()) {
            Element rep = itr.next();
            String limitStr = rep.getAttribute(LIMIT);
            Expression o = operandParser.parseExpression(util.getChildElement(rep,REPEAT_ON), dHolder.getScope());
            Element currentIdx = util.getChildElement(rep,CURRENT_INDEX);
            Element currentElem = util.getChildElement(rep,CURRENT_ELEMENT);
            if (o != null) {
                RepetitiveFlowNode rf = new RepetitiveFlowNode(baseNode);
                rf.setMultiInstance(o);
                try {
                    rf.setLimit(Integer.parseInt(limitStr));
                } catch (NumberFormatException ex) {
                }
                if (currentIdx != null) {
                    rf.setCurrentIndex((Variable)operandParser.parseExpression(currentIdx, dHolder.getScope()));
                }
                if (currentElem != null) {
                    rf.setCurrentElement((Variable)operandParser.parseExpression(currentElem, dHolder.getScope()));
                }
                baseNode = rf;
            }
        }
        return super.parse(baseNode, dHolder);
    }

    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if(wrappedNode instanceof RepetitiveFlowNode){
            RepetitiveFlowNode node = (RepetitiveFlowNode)wrappedNode;
            Element nodeEl = ((XmlDecorationsHolder) dHolder).getNodeElement();
            Element el = nodeEl.getOwnerDocument().createElement(REPEAT);
            nodeEl.appendChild(el);
            Element onel = nodeEl.getOwnerDocument().createElement(REPEAT_ON);
            el.appendChild(onel);
            operandParser.composeExpression(onel, node.getMultiInstance());
            el.setAttribute(LIMIT, Integer.toString(node.getLimit()));
            if(node.getCurrentIndex()!=null){
                Variable v = (Variable)node.getCurrentIndex();
                Element idxEl = nodeEl.getOwnerDocument().createElement(CURRENT_INDEX);
                operandParser.composeExpression(idxEl, v);
                el.appendChild(idxEl);
            }
            if(node.getCurrentElement()!=null){
                Variable v = (Variable)node.getCurrentElement();
                Element elemEl = nodeEl.getOwnerDocument().createElement(CURRENT_ELEMENT);
                operandParser.composeExpression(elemEl, v);
                el.appendChild(elemEl);
            }
            return wrappedNode.getBaseNode(false);
        }else
            return super.compose(wrappedNode, dHolder);
    }
}
