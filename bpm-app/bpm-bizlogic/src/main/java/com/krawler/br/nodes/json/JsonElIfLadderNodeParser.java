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
import com.krawler.br.exp.ConditionalExpression;
import com.krawler.br.exp.Expression;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.utils.JsonFactory;
import com.krawler.br.utils.SourceFactory;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class for parsing the if-elseif-elseif-...-else structure
 *
 * @author Vishnu Kant Gupta
 */
public class JsonElIfLadderNodeParser extends JsonNodeParser {
    
    private static final String LADDER="elseif-ledder";
    private static final String IF="if";
    private static final String THEN="then";
    private static final String ELSE="else";

    public JsonElIfLadderNodeParser(NodeParser successor) {
        super(successor);
    }

    // TODO: modify this function to check the type of argument also. can we check the argument?
    @Override
    public FlowNode parse(SourceFactory src, String sourceid, String id) throws ProcessException {
        JsonFactory factory = (JsonFactory)src;
        JSONObject el=factory.getJSONObject().optJSONObject(sourceid);
        ElIfLadder lad = new ElIfLadder();
        lad.setId(id);
        lad.setSourceid(sourceid);
        lad.setInputParams(parseParams(el, IN_VAR));
        lad.setLocalParams(parseParams(el, LOCAL_VAR));
        lad.setOutputParam(parseParam(el.optJSONObject(OUT_VAR)));

        try {
            JSONArray nl = el.getJSONArray(IF);
            ArrayList hm = new ArrayList();
            for (int i = 0; i < nl.length(); i++) {
                JSONObject e = nl.getJSONObject(i);
                String then = e.getString(THEN);
                IfBlock ifblk = new IfBlock();
                JsonDecorationsHolder dHolder = new JsonDecorationsHolder();
                dHolder.setScope(lad);
                dHolder.setParser(getDecoratorParser());
                dHolder.setNodeObject(e);
                ifblk.setThen(((NodeParser)src.getParser()).parseNode(src, then, id + "_" + then, dHolder));
                ifblk.setWhen((ConditionalExpression) operandParser.parseExpression(e, lad));
                hm.add(ifblk);
            }
            lad.setIfBlocks(hm);
        } catch (JSONException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        }
        
        if(el.has(ELSE)){
            JsonDecorationsHolder dHolder = new JsonDecorationsHolder();
            dHolder.setScope(lad);
            dHolder.setParser(getDecoratorParser());
            dHolder.setNodeObject(el);
            lad.setElseNode(((NodeParser)src.getParser()).parseNode(src, el.optString(ELSE), id+"_else", dHolder));
        }

        return lad;
    }

    @Override
    public boolean canParse(SourceFactory src, String type) {
        JsonFactory factory = (JsonFactory) src;
        JSONObject obj = factory.getJSONObject().optJSONObject(type);

        if (obj != null && obj.optString(FLOWNODE_TYPE).equals(LADDER))
            return true;

         return false;
    }

    @Override
    public boolean canCompose(FlowNode node) {
        return node != null && node instanceof ElIfLadder;
    }

    @Override
    public void compose(SourceFactory src, FlowNode node) throws ProcessException {
        try {
            JSONObject jobj = ((JsonFactory) src).getJSONObject();
            ElIfLadder ladder = (ElIfLadder) node;
            JSONObject el = new JSONObject();
            jobj.put(ladder.getSourceid(), el);
            el.put(FLOWNODE_TYPE, LADDER);
            composeParams(el, IN_VAR, ladder.getInputParams());
            composeParams(el, LOCAL_VAR, ladder.getLocalParams());
            OperationParameter out = ladder.getOutputParam();
            if (out != null) {
                JSONObject outEl = new JSONObject();
                el.put(OUT_VAR, outEl);
                composeParam(outEl, out);
            }
            List<IfBlock> list = ladder.getIfBlocks();
            Iterator<IfBlock> itr = list.iterator();
            JSONArray ifs=el.optJSONArray(IF);
            while (itr.hasNext()) {
                IfBlock ifblk = itr.next();
                JSONObject e = new JSONObject();//doc.createElement(IF);
                if(ifs==null){
                    ifs = new JSONArray();
                    el.put(IF, ifs);
                }
                ifs.put(e);
                e.put(THEN, ifblk.getThen().getSourceid());
                //JSONObject when = new JSONObject();//doc.createElement(WHEN);

                //e.appendChild(when);
                operandParser.composeExpression(e, (Expression) ifblk.getWhen());
                JsonDecorationsHolder dHolder = new JsonDecorationsHolder();
                dHolder.setScope(ladder);
                dHolder.setParser(getDecoratorParser());
                dHolder.setNodeObject(e);
                ((NodeParser) src.getParser()).composeNode(src, ifblk.getThen(), dHolder);
            }
            if (ladder.getElseNode() != null) {
                el.put(ELSE, ladder.getElseNode().getSourceid());
                JsonDecorationsHolder dHolder = new JsonDecorationsHolder();
                dHolder.setScope(ladder);
                dHolder.setParser(getDecoratorParser());
                dHolder.setNodeObject(el);
                ((NodeParser) src.getParser()).composeNode(src, ladder.getElseNode(), dHolder);
            }
        } catch (JSONException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        }
    }
}
