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
import com.krawler.br.decorators.ConditionalFlowNode;
import com.krawler.br.decorators.DecorationsHolder;
import com.krawler.br.decorators.DecoratorParser;
import com.krawler.br.exp.ConditionalExpression;
import com.krawler.br.exp.Expression;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author krawler-user
 */
public class JsonConditionalDecoratorParser extends JsonDecoratorParser{
    private static final String CONDITION="condition";
    public JsonConditionalDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        JSONObject jobj = ((JsonDecorationsHolder)dHolder).getNodeObject().optJSONObject(CONDITION);
        if(jobj!=null){
            ConditionalExpression c =(ConditionalExpression) operandParser.parseExpression(jobj, dHolder.getScope());
            if(c!=null){
                ConditionalFlowNode cf = new ConditionalFlowNode(baseNode);
                cf.setCondition(c);
                baseNode = cf;
            }
        }
        return super.parse(baseNode, dHolder);
    }


    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if(wrappedNode instanceof ConditionalFlowNode){
            ConditionalFlowNode node = (ConditionalFlowNode)wrappedNode;
            JSONObject nodeEl = ((JsonDecorationsHolder) dHolder).getNodeObject();
            ConditionalExpression c = node.getCondition();
            JSONObject el = new JSONObject();
            operandParser.composeExpression(el, (Expression)c);
            try {
                nodeEl.put(CONDITION, el);
            } catch (JSONException ex) {
                throw new ProcessException(ex.getMessage(), ex);
            }
            return wrappedNode.getBaseNode(false);
        }else
            return super.compose(wrappedNode, dHolder);
    }
}
