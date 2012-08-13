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
import com.krawler.br.decorators.NextSupportedFlowNode;
import com.krawler.br.nodes.xml.XmlProcessNodeParser;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class XmlNextDecoratorParser extends XmlDecoratorParser {

    private static final String NEXT = "next";

    public XmlNextDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        Element el = ((XmlDecorationsHolder) dHolder).getNodeElement();
        String nextid = el.getAttribute(NEXT);
        if (nextid != null && nextid.length() > 0) {
            List<Element> l = util.getChildElements((Element) el.getParentNode(), XmlProcessNodeParser.NODE);
            l = util.filterList(XmlProcessNodeParser.NODE_ID, nextid, l);
            if (l.size() != 1) {
                throw new ProcessException("Improper definition(s) for node id -" + nextid + "[total " + l.size() + " definitions]");
            }
            NextSupportedFlowNode node = new NextSupportedFlowNode(baseNode);
            node.setNextNode(nextid);
            baseNode = node;
        }
        return super.parse(baseNode, dHolder);
    }

    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if(wrappedNode instanceof NextSupportedFlowNode){
            NextSupportedFlowNode node = (NextSupportedFlowNode)wrappedNode;
            Element nodeEl = ((XmlDecorationsHolder) dHolder).getNodeElement();
            nodeEl.setAttribute(NEXT, node.getNextNode());
            return wrappedNode.getBaseNode(false);
        }else
            return super.compose(wrappedNode, dHolder);
    }
}
