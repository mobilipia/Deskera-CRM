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
import com.krawler.br.exp.Expression;
import com.krawler.br.ProcessException;
import com.krawler.br.exp.Scope;
import com.krawler.br.modules.ModuleProperty;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.operations.SimpleOperationParameter;
import com.krawler.br.operations.XmlOperationDefinitionParser;
import com.krawler.br.utils.JsonFactory;
import com.krawler.br.utils.SourceFactory;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * class to define some common work related to the Json parsing
 *
 * @author Vishnu Kant Gupta
 */
public abstract class JsonNodeParser extends NodeParser {
    public static final String FLOWNODE_TYPE="type";
    public static final String VARIABLES="variables";
    public static final String IN_VAR="invars";
    public static final String OUT_VAR="outvar";
    public static final String LOCAL_VAR="localvars";
    public static final String VAL="value";
    public static final String INDEX="index";
    public static final String NAME="name";
    protected JsonOperandParser operandParser; // operand parser if neccessory

    /**
     * associates an xml factory with this parser
     *
     * @param successor next node parser, to be used if current parser not able to recognise the type
     * @param factory xml factory
     */
    public JsonNodeParser(NodeParser successor) {
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

    /**
     * gives the number of elements which represnt parameters for the given
     * element
     *
     * @param el element to be checked for parameter count
     * @return an integer greater than or equal to zero
     */
//    protected int getArgumentCount(Element el){
//        return factory.getDomUtil().getChildElements(factory.getDomUtil().getChildElement(el,XmlActivityDefinitionParser.ELEMENT.INVARS.tagName()),XmlActivityDefinitionParser.ELEMENT.INVAR.tagName()).size();
//    }

    /**
     * gives the operands for elements which represnt parameters for the given
     * element
     *
     * @param el element to be used for parameter extraction
     * @return a map of operands corresponding to the parameters
     * @throws com.krawler.br.ProcessException if the operands can not be extracted
     * due to some reason
     */
    protected Map getParams(JsonFactory factory, JSONObject obj, Scope scope) throws ProcessException {
        JSONArray list = obj.optJSONArray(VARIABLES);
        Map ops=new HashMap();
        if(list!=null){
            try {
                for (int i = 0; i < list.length(); i++) {
                    JSONObject opEl = list.getJSONObject(i);
                    Expression op = operandParser.parseExpression(opEl.getJSONObject(VAL), scope);
                    if (op != null) {
                        String name = opEl.getString(NAME);
                        ops.put(name, op);
                    }
                }
            } catch (JSONException ex) {
                throw new ProcessException(ex.getMessage(), ex);
            }
        }
        return ops;
    }
    /**
     * gives the operands for elements which represnt parameters for the given
     * element
     *
     * @param el element to be used for parameter extraction
     * @return a map of operands corresponding to the parameters
     * @throws com.krawler.br.ProcessException if the operands can not be extracted
     * due to some reason
     */
//    protected Condition getCondition(JSONObject el) {
//        Condition cond = null;
//        try {
//            cond = (Condition) operandParser.parse(el.getJSONObject("condition"), operandParser);
//        } catch (Exception ex) {}
//        return cond;
//    }

    public Map<String, OperationParameter> parseParams(JSONObject el, String paramType) {
        JSONArray list = el.optJSONArray(paramType);
        Map<String, OperationParameter> map = new HashMap<String, OperationParameter>();
        if (list != null) {
            for (int i = 0; i < list.length(); i++) {
                JSONObject opEl = list.optJSONObject(i);
                OperationParameter op = parseParam(opEl);
                if (op != null) {
                    map.put(op.getName(), op);
                }
            }
        }
        return map;
    }

    public OperationParameter parseParam(JSONObject el) {
        OperationParameter ap=null;
        if(el!=null){
            Object initVal = el.opt(VAL);
            if(initVal!=null){
                InitializableOperationParameter temp = new InitializableOperationParameter();
                temp.setModuleBag(getOperationBag().getModuleBag());
                temp.setInitialValue(initVal);
                ap = temp;
            }else
                ap = new SimpleOperationParameter();
            ap.setName(el.optString(XmlOperationDefinitionParser.ATTRIBUTE.I_NAME.tagName()));
            ap.setType(el.optString(XmlOperationDefinitionParser.ATTRIBUTE.I_TYPE.tagName()));
            String multi=el.optString(XmlOperationDefinitionParser.ATTRIBUTE.I_MULTI.tagName());
            for(ModuleProperty.MULTI m:ModuleProperty.MULTI.values())
                if(m.tagName().equals(multi))
                    ap.setMulti(m);
        }
        return ap;
    }

    public Map<String, OperationParameter> composeParams(JSONObject el, String paramType, Map<String, OperationParameter> map) throws JSONException {
        JSONArray varsEl = el.optJSONArray(paramType);
        Iterator<String> itr = map.keySet().iterator();
        while(itr.hasNext()){
            OperationParameter op = map.get(itr.next());
            JSONObject opEl = new JSONObject();
            composeParam(opEl, op);
            if(varsEl==null){
                varsEl=new JSONArray();
                el.put(paramType,varsEl);
            }
            varsEl.put(opEl);
        }
        return map;
    }

    /**
     * parses the operation parameter represented by the given DOM element
     *
     * @param el DOM element to be parsed
     * @return operation parameter or null if element doesn't represent an operation
     * parameter
     */
    public void composeParam(JSONObject el, OperationParameter ap) throws JSONException {
        if(el!=null && ap!=null){
            if(ap instanceof InitializableOperationParameter){
                el.put(VAL,((InitializableOperationParameter)ap).getInitialText());
            }

            el.put(XmlOperationDefinitionParser.ATTRIBUTE.I_NAME.tagName(),ap.getName());
            el.put(XmlOperationDefinitionParser.ATTRIBUTE.I_TYPE.tagName(),ap.getType());
            if(ap.getMulti()!=null)
                el.put(XmlOperationDefinitionParser.ATTRIBUTE.I_MULTI.tagName(),ap.getMulti().tagName());
        }
    }

    @Override
    public Set getIDs(SourceFactory src) {
        JsonFactory factory = (JsonFactory)src;
        Set pNames = new HashSet();
        JSONObject jobj = factory.getJSONObject();
        try {
            JSONArray jArr = jobj.getJSONArray("process");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                pNames.add(obj.getString("id"));
            }
        } catch (JSONException ex) {
        }
        return pNames;
    }

    @Override
    public void remove(SourceFactory src, String id) throws ProcessException {
        JsonFactory factory = (JsonFactory)src;
        JSONObject jobj = factory.getJSONObject();
        try {
            JSONArray jArr = jobj.getJSONArray("process");
            for (int i = 0; i < jArr.length(); i++) {
                JSONObject obj = jArr.getJSONObject(i);
                if(obj.getString("id").equals(id)){
//TODO              remove obj from jArr
                    throw new UnsupportedOperationException("can not remove from json array.");
                }
            }
        } catch (JSONException ex) {
        }
    }
}
