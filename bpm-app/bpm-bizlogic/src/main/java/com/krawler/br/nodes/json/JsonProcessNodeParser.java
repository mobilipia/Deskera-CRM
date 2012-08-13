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

package com.krawler.br.nodes.json;

import com.krawler.br.nodes.*;
import com.krawler.br.FlowNode;
import com.krawler.br.ProcessException;
import com.krawler.br.decorators.json.JsonDecorationsHolder;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.utils.JsonFactory;
import com.krawler.br.utils.SourceFactory;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.Iterator;
import java.util.Map;

/**
 * class to parse the process tag to create a proper process
 *
 * @author Vishnu Kant Gupta
 */
public class JsonProcessNodeParser extends JsonNodeParser {
    public static final String PROCESS="process";
    public static final String NODELIST="nodelist";
    public static final String NODE_ID="id";
    public static final String INIT="init";
    public static final String INVOKE="invoke";
    public JsonProcessNodeParser(NodeParser successor) {
        super(successor);
    }

    // TODO: modify this function to check the type of argument also. can we check the argument?
    @Override
    public FlowNode parse(SourceFactory src, String sourceid, String id) throws ProcessException {
        JsonFactory factory = (JsonFactory) src;
        try {
            JSONObject jobj = factory.getJSONObject().optJSONObject(sourceid);
            BProcess p = new BProcess();
            p.setId(id);
            p.setSourceid(sourceid);
            p.setInitialNode(jobj.getString(INIT));
            p.setInputParams(parseParams(jobj, IN_VAR));
            p.setLocalParams(parseParams(jobj, LOCAL_VAR));
            p.setOutputParam(parseParam(jobj.optJSONObject(OUT_VAR)));

            JSONArray nl = jobj.getJSONArray(NODELIST);

            for (int i = 0; i < nl.length(); i++) {
                JSONObject e = nl.getJSONObject(i);
                JsonDecorationsHolder dHolder = new JsonDecorationsHolder();
                dHolder.setScope(p);
                dHolder.setParser(getDecoratorParser());
                dHolder.setNodeObject(e);
                p.addFlowNode(((NodeParser)src.getParser()).parseNode(src, e.getString(INVOKE), e.getString(NODE_ID), dHolder));
            }
            return p;
        } catch (JSONException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean canParse(SourceFactory src, String type) {
        JsonFactory factory = (JsonFactory) src;
        JSONObject obj = factory.getJSONObject().optJSONObject(type);

        if (obj != null && obj.optString(FLOWNODE_TYPE).equals(PROCESS))
            return true;
                
         return false;
    }

    @Override
    public boolean canCompose(FlowNode node) {
        return node!=null && node instanceof BProcess;
    }

    @Override
    public void compose(SourceFactory src, FlowNode node) throws ProcessException {
        try {
            JSONObject jobj = ((JsonFactory) src).getJSONObject();
            BProcess p = (BProcess) node;
            JSONObject el = new JSONObject();
            jobj.put(p.getSourceid(), el);
            el.put(FLOWNODE_TYPE, PROCESS);
            el.put(INIT, p.getInitialNode());
            composeParams(el, IN_VAR, p.getInputParams());
            composeParams(el, LOCAL_VAR, p.getLocalParams());
            OperationParameter out = p.getOutputParam();
            if (out != null) {
                JSONObject outEl = new JSONObject();
                el.put(OUT_VAR, outEl);
                composeParam(outEl, out);
            }
            if (p.getInitialNode() == null) {
                throw new ProcessException("Process [" + p.getSourceid() + "] has no initial node");


            }
            JSONArray nl = new JSONArray();
            el.put(NODELIST, nl);
            Map<String, FlowNode> nodes = p.getAllFlowNodes();
            Iterator<String> itr = nodes.keySet().iterator();
            while (itr.hasNext()) {
                String nodeid = itr.next();
                FlowNode enode = nodes.get(nodeid);
                JSONObject e = new JSONObject();
                nl.put(e);
                e.put(NODE_ID, nodeid);
                e.put(INVOKE, enode.getSourceid());

                JsonDecorationsHolder dHolder = new JsonDecorationsHolder();
                dHolder.setScope(p);
                dHolder.setParser(getDecoratorParser());
                dHolder.setNodeObject(e);
                ((NodeParser) src.getParser()).composeNode(src, enode, dHolder);
            }
        } catch (JSONException ex) {
            throw new ProcessException(ex.getMessage(),ex);
        }
    }
}
