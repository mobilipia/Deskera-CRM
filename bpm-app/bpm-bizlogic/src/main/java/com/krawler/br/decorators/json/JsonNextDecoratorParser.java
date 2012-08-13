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
import com.krawler.br.decorators.NextSupportedFlowNode;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

/**
 *
 * @author krawler-user
 */
public class JsonNextDecoratorParser extends JsonDecoratorParser {

    private static final String NEXT = "next";

    public JsonNextDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        JSONObject el = ((JsonDecorationsHolder) dHolder).getNodeObject();
        String nextid = el.optString(NEXT);
        if (nextid != null && nextid.length() > 0) {
            NextSupportedFlowNode node = new NextSupportedFlowNode(baseNode);
            node.setNextNode(nextid);
            baseNode = node;
        }
        return super.parse(baseNode, dHolder);
    }


    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if (wrappedNode instanceof NextSupportedFlowNode) {
            NextSupportedFlowNode temp = (NextSupportedFlowNode) wrappedNode;
            JSONObject el = ((JsonDecorationsHolder) dHolder).getNodeObject();
            try {
                el.put(NEXT, temp.getNextNode());
            } catch (JSONException ex) {
                throw new ProcessException(ex.getMessage(), ex);
            }
            return wrappedNode.getBaseNode(false);
        } else {
            return super.compose(wrappedNode, dHolder);
        }
    }
}
