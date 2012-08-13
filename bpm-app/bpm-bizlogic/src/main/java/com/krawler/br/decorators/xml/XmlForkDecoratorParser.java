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
import com.krawler.br.decorators.FlowNodeDecorator;
import com.krawler.br.decorators.ForkSupportedFlowNode;
import com.krawler.br.nodes.xml.XmlProcessNodeParser;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class XmlForkDecoratorParser extends XmlDecoratorParser {

    private static final String FORK = "fork";

    public XmlForkDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        Element el = ((XmlDecorationsHolder) dHolder).getNodeElement();
        String forkid = el.getAttribute(FORK);
        if (forkid != null && forkid.length() > 0) {
            List<Element> l = util.getChildElements((Element) el.getParentNode(), XmlProcessNodeParser.NODE);
            l = util.filterList(XmlProcessNodeParser.NODE_ID, forkid, l);
            if (l.size() != 1) {
                throw new ProcessException("Improper definition(s) for node id -" + forkid + "[total " + l.size() + " definitions]");
            }
            ForkSupportedFlowNode node = new ForkSupportedFlowNode(baseNode);
            node.setForkNode(forkid);
            baseNode = node;
        }
        return super.parse(baseNode, dHolder);
    }

    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
            if(wrappedNode instanceof ForkSupportedFlowNode){
                ForkSupportedFlowNode temp = (ForkSupportedFlowNode)wrappedNode;
                Element nodeEl = ((XmlDecorationsHolder) dHolder).getNodeElement();
                nodeEl.setAttribute(FORK, temp.getForkNode());
                return wrappedNode.getBaseNode(false);
            }else
                return super.compose(wrappedNode, dHolder);
    }
}
