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

import com.krawler.br.FlowNode;
import com.krawler.br.ProcessException;
import com.krawler.br.decorators.DecorationsHolder;
import com.krawler.br.decorators.DecoratorParser;
import com.krawler.br.decorators.ResultSupportedFlowNode;
import com.krawler.br.exp.Variable;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

/**
 *
 * @author krawler-user
 */
public class JsonResultDecoratorParser extends JsonDecoratorParser {
    private static final String OUTPUT="output";

    public JsonResultDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        JSONObject el = ((JsonDecorationsHolder)dHolder).getNodeObject();
        JSONObject path=el!=null?el.optJSONObject(OUTPUT):null;
        if(path!=null){
            ResultSupportedFlowNode rf = new ResultSupportedFlowNode(baseNode);
            rf.setResultHolder((Variable)operandParser.parseExpression(path,dHolder.getScope()));
            baseNode = rf;
        }
        return super.parse(baseNode, dHolder);
    }

    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if(wrappedNode instanceof ResultSupportedFlowNode){
            ResultSupportedFlowNode node = (ResultSupportedFlowNode)wrappedNode;
            Variable e = (Variable)node.getResultHolder();

            JSONObject nodeEl = ((JsonDecorationsHolder) dHolder).getNodeObject();
            JSONObject outEl=new JSONObject();
            try {
                nodeEl.put(OUTPUT, outEl);
            } catch (JSONException ex) {
                throw new ProcessException(ex.getMessage(), ex);
            }
            operandParser.composeExpression(outEl, e);
            return wrappedNode.getBaseNode(false);
        }else
            return super.compose(wrappedNode, dHolder);
    }
}
