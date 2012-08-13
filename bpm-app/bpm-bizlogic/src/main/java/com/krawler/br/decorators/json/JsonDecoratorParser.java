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

package com.krawler.br.decorators.json;

import com.krawler.br.decorators.DecoratorParser;
import com.krawler.br.nodes.json.JsonOperandParser;

/**
 *
 * @author krawler-user
 */
public class JsonDecoratorParser extends DecoratorParser {
    protected JsonOperandParser operandParser;

    public JsonDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    /**
     * associates an operand parser with this parser to be used to parse the
     * operands for the node if required
     *
     * @param operandParser operand parser to be associate
     */
    public void setOperandParser(JsonOperandParser operandParser) {
        this.operandParser = operandParser;
    }
}
