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
import com.krawler.br.stmt.Statement;
import com.krawler.br.utils.DomUtil;
import java.util.List;
import org.w3c.dom.Element;

/**
 * operand parser class to parse the element to generate corresponding operand
 *
 * @author Vishnu Kant Gupta
 */
public class XmlOperandParser {
    private XmlOperandParser successor; // successor operand parser, to be used if current parser not able to recognise the element
    protected DomUtil util = new DomUtil();
    protected XmlOperandParser starter;

    /**
     * costructor for XML operand parser with its successor
     *
     * @param successor next operand parser, to be used if current parser not able to recognise the element
     */
    public XmlOperandParser(XmlOperandParser successor) {
        this.successor = successor;
    }

    /**
     * parses the given element if possible, if this parser can not recognise
     * the element, then  the task will be forwarded to the next available parser
     *
     * @param parent the element for which the operand to be parsed
     * @param starter the header operand parser in case of there is a need for
     * recursive parsing of element
     *
     * @return the operand corresponding to the given element
     * @throws com.krawler.br.ProcessException if there is a problem in parsing the element
     * @throws IllegalArgumentException if the element is not recognised
     * by any of the parser
     */
    public Expression parseExpression(Element parent, Scope scope) throws ProcessException {
        if(starter==null)
            starter = this;
        if(successor!=null){
            if(successor.starter==null)
                successor.starter = starter;
            return successor.parseExpression(parent, scope);
        }

//        throw new IllegalArgumentException("Unparsable operand found:"+parent.getAttribute("id")+", Either operand definition or operand parser missing");
        return null;
    }

    /**
     * parses the given element if possible, if this parser can not recognise
     * the element, then  the task will be forwarded to the next available parser
     *
     * @param parent the element for which the operand to be parsed
     * @param starter the header operand parser in case of there is a need for
     * recursive parsing of element
     *
     * @return the operand corresponding to the given element
     * @throws com.krawler.br.ProcessException if there is a problem in parsing the element
     * @throws IllegalArgumentException if the element is not recognised
     * by any of the parser
     */
    public List<Statement> parseBlock(Element parent, Scope scope) throws ProcessException {
        if(starter==null)
            starter = this;
        if(successor!=null){
            if(successor.starter==null)
                successor.starter = starter;
            return successor.parseBlock(parent, scope);
        }

//        throw new IllegalArgumentException("Unparsable operand found:"+parent.getAttribute("id")+", Either operand definition or operand parser missing");
        return null;
    }

    public void composeExpression(Element parent, Expression expression) throws ProcessException {
        if(starter==null)
            starter = this;
        if(successor!=null){
            if(successor.starter==null)
                successor.starter = starter;
            successor.composeExpression(parent, expression);
        }else
            throw new IllegalArgumentException("Noncomposable operand found:"+parent.getTagName()+", Either operand definition or operand parser missing");
    }

    public void composeBlock(Element parent, List<Statement> statements) throws ProcessException {
        if(starter==null)
            starter = this;
        if(successor!=null){
            if(successor.starter==null)
                successor.starter = starter;
            successor.composeBlock(parent, statements);
        }else
            throw new IllegalArgumentException("Noncomposable operand found:"+parent.getTagName()+", Either operand definition or operand parser missing");
    }
}
