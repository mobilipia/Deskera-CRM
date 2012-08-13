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
import com.krawler.br.decorators.DecorationsHolder;
import com.krawler.br.decorators.DecoratorParser;
import com.krawler.br.decorators.RepetitiveFlowNode;
import com.krawler.br.exp.Variable;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

/**
 *
 * @author krawler-user
 */
public class JsonRepetitiveDecoratorParser extends JsonDecoratorParser {

    private static final String REPEAT = "repeat";      // condition tag
    private static final String LIMIT = "maxlimit";
    private static final String CURRENT_INDEX = "currentindex";
    private static final String CURRENT_ELEMENT = "currentelement";

    public JsonRepetitiveDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        JSONArray list = ((JsonDecorationsHolder) dHolder).getNodeObject().optJSONArray(REPEAT);
        if(list!=null){
            for(int i = 0; i<list.length(); i++) {
                JSONObject rep = list.optJSONObject(i);
                Expression o = operandParser.parseExpression(rep, dHolder.getScope());
                
                if (o != null) {
                    RepetitiveFlowNode rf = new RepetitiveFlowNode(baseNode);
                    rf.setMultiInstance(o);
                    try {
                        rf.setLimit(Integer.parseInt(rep.optString(LIMIT)));
                    } catch (Exception ex) {
                    }
                    JSONObject currentIdx = rep.optJSONObject(CURRENT_INDEX);
                    if (currentIdx != null) {
                        rf.setCurrentIndex((Variable)operandParser.parseExpression(currentIdx,dHolder.getScope()));
                    }
                    JSONObject currentElem = rep.optJSONObject(CURRENT_ELEMENT);
                    if (currentElem != null) {
                        rf.setCurrentElement((Variable)operandParser.parseExpression(currentElem,dHolder.getScope()));
                    }
                    baseNode = rf;
                }
            }
        }
        return super.parse(baseNode, dHolder);
    }


    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if(wrappedNode instanceof RepetitiveFlowNode){
            try {
                RepetitiveFlowNode node = (RepetitiveFlowNode) wrappedNode;
                JSONObject nodeEl = ((JsonDecorationsHolder) dHolder).getNodeObject();
                JSONArray rpts = nodeEl.optJSONArray(REPEAT);
                if (rpts == null) {
                    rpts = new JSONArray();
                    nodeEl.put(REPEAT, rpts);
                }
                JSONObject onel = new JSONObject();
                rpts.put(onel);
                operandParser.composeExpression(onel, node.getMultiInstance());
                onel.put(LIMIT, Integer.toString(node.getLimit()));
                if (node.getCurrentIndex() != null) {
                    Variable v = (Variable) node.getCurrentIndex();
                    JSONObject idxEl = new JSONObject();//nodeEl.getOwnerDocument().createElement(CURRENT_INDEX);
                    operandParser.composeExpression(idxEl, v);
                    onel.put(CURRENT_INDEX, idxEl);
                }
                if (node.getCurrentElement() != null) {
                    Variable v = (Variable) node.getCurrentElement();
                    JSONObject elemEl = new JSONObject();//nodeEl.getOwnerDocument().createElement(CURRENT_ELEMENT);
                    operandParser.composeExpression(elemEl, v);
                    onel.put(CURRENT_ELEMENT, elemEl);
                }
            } catch (JSONException ex) {
                throw new ProcessException(ex.getMessage(), ex);
            }
            return wrappedNode.getBaseNode(false);
        }else
            return super.compose(wrappedNode, dHolder);
    }
}
