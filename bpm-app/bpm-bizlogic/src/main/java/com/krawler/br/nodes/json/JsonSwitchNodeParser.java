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
import com.krawler.br.exp.Constant;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.utils.JsonFactory;
import com.krawler.br.utils.SourceFactory;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.Iterator;
import java.util.Map;

/**
 * class used to parse the switch-case:m-...-case:n-otherwise structure
 *
 * @author Vishnu Kant Gupta
 */
public class JsonSwitchNodeParser extends JsonNodeParser {
    
    private static final String SWITCH="switch";
    private static final String EXPR="expr";
    private static final String CASE="case";
    private static final String CASE_NODE="then";
    private static final String OTHERWISE="otherwise";

    public JsonSwitchNodeParser(NodeParser successor) {
        super(successor);
    }

    // TODO: modify this function to check the type of argument also. can we check the argument?
    @Override
    public FlowNode parse(SourceFactory src, String sourceid, String id) throws ProcessException {
        JsonFactory factory = (JsonFactory) src;
        JSONObject el = factory.getJSONObject().optJSONObject(sourceid);
        Switch s = new Switch();
        s.setId(id);
        s.setSourceid(sourceid);
        s.setInputParams(parseParams(el, IN_VAR));
        s.setLocalParams(parseParams(el, LOCAL_VAR));
        s.setOutputParam(parseParam(el.optJSONObject(OUT_VAR)));

        try {
            JSONArray nl = el.getJSONArray(CASE);
            for (int i = 0; i < nl.length(); i++) {
                JSONObject e = nl.getJSONObject(i);
                String then = e.getString(CASE_NODE);
                JsonDecorationsHolder dHolder = new JsonDecorationsHolder();
                dHolder.setScope(s);
                dHolder.setParser(getDecoratorParser());
                dHolder.setNodeObject(e);
                FlowNode node = ((NodeParser)src.getParser()).parseNode(src, then, id + "_" + then, dHolder);
                Constant cons = (Constant) operandParser.parseExpression(e, s);
                s.addCase(cons, node);
            }
            s.setExpression(operandParser.parseExpression(el.getJSONObject(EXPR), s));
        } catch (JSONException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        }
        if(s.getExpression()==null){
            throw new ProcessException("switch without an expression!");
        }
        if(el.has(OTHERWISE)){
            JsonDecorationsHolder dHolder = new JsonDecorationsHolder();
            dHolder.setScope(s);
            dHolder.setParser(getDecoratorParser());
            dHolder.setNodeObject(el);
            s.setDefault(((NodeParser)src.getParser()).parseNode(src, el.optString(OTHERWISE), id+"_ow", dHolder));
        }
        return s;
    }

    /**
     * checks to see whether the given type is an switch or not
     *
     * @param type type id to check
     * @return true if the type is an switch, false otherwise
     */
    @Override
    public boolean canParse(SourceFactory src, String type) {
        JsonFactory factory = (JsonFactory) src;
        JSONObject obj = factory.getJSONObject().optJSONObject(type);

        if (obj != null && obj.optString(FLOWNODE_TYPE).equals(SWITCH))
            return true;

         return false;
    }

    @Override
    public boolean canCompose(FlowNode node) {
        return node!=null && node instanceof Switch;
    }

    @Override
    public void compose(SourceFactory src, FlowNode node) throws ProcessException {
        try {
            JSONObject jobj = ((JsonFactory) src).getJSONObject();
            Switch s = (Switch) node;
            JSONObject el = new JSONObject();
            jobj.put(s.getSourceid(), el);
            el.put(FLOWNODE_TYPE, SWITCH);
            composeParams(el, IN_VAR, s.getInputParams());
            composeParams(el, LOCAL_VAR, s.getLocalParams());
            OperationParameter out = s.getOutputParam();
            if (out != null) {
                JSONObject outEl = new JSONObject();
                el.put(OUT_VAR, outEl);
                composeParam(outEl, out);
            }
            Map<Integer, FlowNode> map = s.getCases();
            Iterator<Integer> itr = map.keySet().iterator();
            JSONArray cases=el.optJSONArray(CASE);
            while (itr.hasNext()) {
                Integer label = itr.next();
                FlowNode cnode = map.get(label);
                JSONObject e = new JSONObject();
                if(cases==null){
                    cases = new JSONArray();
                    el.put(CASE, cases);
                }
                cases.put(e);
                Constant c = new Constant();
                c.setValue(label);
                operandParser.composeExpression(e, c);
                e.put(CASE_NODE, cnode.getSourceid());
                JsonDecorationsHolder dHolder = new JsonDecorationsHolder();
                dHolder.setScope(s);
                dHolder.setParser(getDecoratorParser());
                dHolder.setNodeObject(e);
                ((NodeParser) src.getParser()).composeNode(src, cnode, dHolder);
            }
            if (s.getDefault() != null) {
                el.put(OTHERWISE, s.getDefault().getSourceid());
                JsonDecorationsHolder dHolder = new JsonDecorationsHolder();
                dHolder.setScope(s);
                dHolder.setParser(getDecoratorParser());
                dHolder.setNodeObject(el);
                ((NodeParser) src.getParser()).composeNode(src, s.getDefault(), dHolder);
            }
        } catch (JSONException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        }
    }
}
