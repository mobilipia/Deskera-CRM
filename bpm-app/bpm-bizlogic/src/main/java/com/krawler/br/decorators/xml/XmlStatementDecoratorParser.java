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
import com.krawler.br.decorators.StatementsSupportedFlowNode;
import com.krawler.br.decorators.DecorationsHolder;
import com.krawler.br.decorators.DecoratorParser;
import com.krawler.br.stmt.Statement;
import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class XmlStatementDecoratorParser extends XmlDecoratorParser {

    private static final String PRE_STATEMENTS="prestatements";
    private static final String POST_STATEMENTS="poststatements";

    public XmlStatementDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        Element asEl = util.getChildElement(((XmlDecorationsHolder) dHolder).getNodeElement(), PRE_STATEMENTS);
        if(asEl!=null){
            List<Statement> statements= operandParser.parseBlock(asEl, dHolder.getScope());
            if (statements != null&&!statements.isEmpty()) {
                StatementsSupportedFlowNode af = new StatementsSupportedFlowNode(baseNode);
                af.setAfter(false);
                af.setStatements(statements);
                baseNode = af;
            }
        }
        asEl = util.getChildElement(((XmlDecorationsHolder) dHolder).getNodeElement(), POST_STATEMENTS);
        if(asEl!=null){
            List<Statement> statements= operandParser.parseBlock(asEl, dHolder.getScope());
            if (statements != null&&!statements.isEmpty()) {
                StatementsSupportedFlowNode af = new StatementsSupportedFlowNode(baseNode);
                af.setAfter(true);
                af.setStatements(statements);
                baseNode = af;
            }
        }
        return super.parse(baseNode, dHolder);
    }

    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if (wrappedNode instanceof StatementsSupportedFlowNode) {
            StatementsSupportedFlowNode node = (StatementsSupportedFlowNode) wrappedNode;
            Element nodeEl = ((XmlDecorationsHolder) dHolder).getNodeElement();
            Element el;

            if (node.isAfter()) {
                el= nodeEl.getOwnerDocument().createElement(POST_STATEMENTS);
            }else{
                el= nodeEl.getOwnerDocument().createElement(PRE_STATEMENTS);
            }
            nodeEl.appendChild(el);
            operandParser.composeBlock(el, node.getStatements());
            return wrappedNode.getBaseNode(false);
        } else {
            return super.compose(wrappedNode, dHolder);
        }
    }
}
