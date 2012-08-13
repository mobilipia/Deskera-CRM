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

import com.krawler.br.decorators.DecoratorParser;
import com.krawler.br.nodes.xml.XmlOperandParser;
import com.krawler.br.utils.DomUtil;

/**
 * The class defines the abstact way of parsing the flow nodes
 * <p>
 * this class uses the do or delegate methodology foe parsing.
 * for that purpose, it holds a reference of the next parser
 * which can be used if there is a possibility that this parser cannot parse
 * the given type
 * <br />
 * also this parser holds a reference of header parse of the chain
 * which can be used to perform parsing recursively.
 * </p>
 *
 *
 * @author Vishnu Kant Gupta
 */
public abstract class XmlDecoratorParser extends DecoratorParser {
    protected DomUtil util = new DomUtil();
    protected XmlOperandParser operandParser;

    public XmlDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    public XmlOperandParser getOperandParser() {
        return operandParser;
    }

    public void setOperandParser(XmlOperandParser operandParser) {
        this.operandParser = operandParser;
    }
}
