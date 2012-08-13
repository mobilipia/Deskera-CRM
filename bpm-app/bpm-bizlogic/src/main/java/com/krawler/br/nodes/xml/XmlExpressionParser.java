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
package com.krawler.br.nodes.xml;

import com.krawler.br.exp.Expression;
import com.krawler.br.ProcessException;
import com.krawler.br.exp.Scope;
import com.krawler.br.nodes.exp.TextExpressionParser;
import com.krawler.br.stmt.Statement;

import java.util.List;
import org.w3c.dom.Element;

/**
 *
 * @author krawler-user
 */
public class XmlExpressionParser extends XmlOperandParser {

    private TextExpressionParser expParser;

    public XmlExpressionParser(XmlOperandParser successor) {
        super(successor);
    }

    public void setExpParser(TextExpressionParser expParser) {
        this.expParser = expParser;
    }

    @Override
    public Expression parseExpression(Element parent, Scope scope) throws ProcessException {
        if (util.getChildElements(parent).isEmpty() && parent.hasChildNodes()) {
            return expParser.parseExpression(parent.getTextContent().trim(), scope);
        } else {
            return super.parseExpression(parent, scope);
        }
    }

    @Override
    public List<Statement> parseBlock(Element parent, Scope scope) throws ProcessException {
        if (util.getChildElements(parent).isEmpty() && parent.hasChildNodes()) {
            return expParser.parseBlock(parent.getTextContent().trim(), scope);
        } else {
            return super.parseBlock(parent, scope);
        }
    }

    @Override
    public void composeExpression(Element parent, Expression expression) throws ProcessException {
        try{
            String text = expParser.composeExpression(expression);
            appendText(parent, text);
        }catch(ProcessException ex){
            super.composeExpression(parent, expression);
        }
    }

    @Override
    public void composeBlock(Element parent, List<Statement> statements) throws ProcessException {
        try{
            String text = expParser.composeBlock(statements);
            appendText(parent, text);
        }catch(ProcessException ex){
            super.composeBlock(parent, statements);
        }
    }

    private void appendText(Element e, String text) {
        if (text.indexOf('<') >= 0 || text.indexOf('>') >= 0 || text.indexOf('&') >= 0 || text.indexOf('"') >= 0) {
            e.appendChild(e.getOwnerDocument().createCDATASection(text));
        } else {
            e.appendChild(e.getOwnerDocument().createTextNode(text));
        }

    }
}
