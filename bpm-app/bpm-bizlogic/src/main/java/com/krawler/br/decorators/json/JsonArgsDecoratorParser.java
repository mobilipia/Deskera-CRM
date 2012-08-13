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
import com.krawler.br.exp.Expression;
import com.krawler.br.ProcessException;
import com.krawler.br.decorators.ArgsSupportedFlowNode;
import com.krawler.br.decorators.DecorationsHolder;
import com.krawler.br.decorators.DecoratorParser;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author krawler-user
 */
public class JsonArgsDecoratorParser extends JsonDecoratorParser {

    private static final String VARIABLES = "variables";
    private static final String VAR = "value";
    private static final String NAME = "name";

    public JsonArgsDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        JSONArray list = ((JsonDecorationsHolder) dHolder).getNodeObject().optJSONArray(VARIABLES);
        HashMap ops = new HashMap();
        if(list!=null){
            for (int i = 0; i < list.length(); i++) {
                JSONObject opEl = list.optJSONObject(i);
                Expression op = operandParser.parseExpression(opEl, dHolder.getScope());

                if (op != null) {
                    String name = opEl.optString(NAME);
                    ops.put(name, op);
                }
            }
        }
        if (!ops.isEmpty()) {
            ArgsSupportedFlowNode af = new ArgsSupportedFlowNode(baseNode);
            af.setArgumentsHolder(ops);
            baseNode = af;
        }
        return super.parse(baseNode, dHolder);
    }

    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if(wrappedNode instanceof ArgsSupportedFlowNode){
            ArgsSupportedFlowNode node = (ArgsSupportedFlowNode)wrappedNode;
            Map<String, Expression> ops = node.getArgumentsHolder();
            Iterator<String> itr = ops.keySet().iterator();
            JSONObject nodeEl = ((JsonDecorationsHolder) dHolder).getNodeObject();
            JSONArray varsEl = nodeEl.optJSONArray(VARIABLES);
            try {
                while (itr.hasNext()) {
                    String name = itr.next();
                    Expression op = ops.get(name);
                    if (varsEl == null) {
                        varsEl = new JSONArray();
                        nodeEl.put(VARIABLES, varsEl);
                    }
                    JSONObject el = new JSONObject();
                    varsEl.put(el);
                    el.put(NAME, name);
                    operandParser.composeExpression(el, op);
                }
            } catch (JSONException ex) {
                throw new ProcessException(ex.getMessage(), ex);
            }
            return wrappedNode.getBaseNode(false);
        }else
            return super.compose(wrappedNode, dHolder);
    }
}
