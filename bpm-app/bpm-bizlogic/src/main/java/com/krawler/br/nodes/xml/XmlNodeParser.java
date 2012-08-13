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

package com.krawler.br.nodes.xml;

import com.krawler.br.nodes.*;
import com.krawler.br.ProcessException;
import com.krawler.br.modules.ModuleProperty;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.operations.XmlOperationDefinitionParser;
import com.krawler.br.operations.SimpleOperationParameter;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.XmlFactory;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * class to define some common work related to the XML parsing
 *
 * @author Vishnu Kant Gupta
 */
public abstract class XmlNodeParser extends NodeParser {
    private static final String ROOT="businessrules";
    public static final String VARS="vars";
    public static final String IN_VAR="in-var";
    public static final String OUT_VAR="out-var";
    public static final String LOCAL_VAR="local-var";
    protected XmlOperandParser operandParser; // operand parser if neccessory

    /**
     * associates an xml factory with this parser
     *
     * @param successor next node parser, to be used if current parser not able to recognise the type
     * @param factory xml factory
     */
    public XmlNodeParser(NodeParser successor) {
        super(successor);
    }

    /**
     * associates an operand parser with this parser to be used to parse the
     * operands for the node if required
     *
     * @param operandParser operand parser to be associate
     */
    public void setOperandParser(XmlOperandParser operandParser) {
        this.operandParser = operandParser;
    }

    public Map<String, OperationParameter> parseParams(Element el, String paramType) {
        List<Element> list = operandParser.util.getChildElements(operandParser.util.getChildElement(el,VARS),paramType);
        Map<String, OperationParameter> map = new HashMap<String, OperationParameter>();
        Iterator<Element> itr = list.iterator();
        while(itr.hasNext()){
            Element opEl = itr.next();
            OperationParameter op=parseParam(opEl);
            if(op!=null) map.put(op.getName(), op);
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
    public OperationParameter parseParam(Element el) {
        OperationParameter ap=null;
        if(el!=null){
            String initVal = el.getTextContent().trim();
            if(initVal.length()>0){
                InitializableOperationParameter temp = new InitializableOperationParameter();
                temp.setModuleBag(getOperationBag().getModuleBag());
                try {
                    temp.setInitialValue(new JSONObject(initVal));
                } catch (JSONException ex) {
                    try {
                        temp.setInitialValue(new JSONArray(initVal));
                    } catch (JSONException ex1) {
                        temp.setInitialValue(initVal);
                    }
                }
                ap = temp;
            }else
                ap = new SimpleOperationParameter();
            ap.setName(el.getAttribute(XmlOperationDefinitionParser.ATTRIBUTE.I_NAME.tagName()));
            ap.setType(el.getAttribute(XmlOperationDefinitionParser.ATTRIBUTE.I_TYPE.tagName()));
            String multi=el.getAttribute(XmlOperationDefinitionParser.ATTRIBUTE.I_MULTI.tagName());
            for(ModuleProperty.MULTI m:ModuleProperty.MULTI.values())
                if(m.tagName().equals(multi))
                    ap.setMulti(m);
        }
        return ap;
    }

    public Map<String, OperationParameter> composeParams(Element el, String paramType, Map<String, OperationParameter> map) {
        Element varsEl = operandParser.util.getChildElement(el,VARS);
        Iterator<String> itr = map.keySet().iterator();
        Document doc = el.getOwnerDocument();
        while(itr.hasNext()){
            OperationParameter op = map.get(itr.next());
            Element opEl = doc.createElement(paramType);
            composeParam(opEl, op);
            if(varsEl==null){
                varsEl=el.getOwnerDocument().createElement(VARS);
                el.appendChild(varsEl);
            }
            varsEl.appendChild(opEl);
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
    public void composeParam(Element el, OperationParameter ap) {
        if(el!=null && ap!=null){
            if(ap instanceof InitializableOperationParameter){
                el.setTextContent(((InitializableOperationParameter)ap).getInitialText());
            }

            el.setAttribute(XmlOperationDefinitionParser.ATTRIBUTE.I_NAME.tagName(),ap.getName());
            el.setAttribute(XmlOperationDefinitionParser.ATTRIBUTE.I_TYPE.tagName(),ap.getType());
            if(ap.getMulti()!=null)
                el.setAttribute(XmlOperationDefinitionParser.ATTRIBUTE.I_MULTI.tagName(),ap.getMulti().tagName());
        }
    }

    @Override
    public Set getIDs(SourceFactory src) {
        XmlFactory xFac = (XmlFactory)src;
        Element el = getDocumentElement(xFac.getDocument());
        Iterator<Element> itr = xFac.getDomUtil().getChildElements(el).iterator();
        Set pNames = new HashSet();
        while(itr.hasNext()){
            el = itr.next();
            pNames.add(el.getAttribute("id"));
        }

        return pNames;
    }

    protected Element getDocumentElement(Document doc){
        if(doc.getDocumentElement()==null){
            doc.appendChild(doc.createElement(ROOT));
        }
        return doc.getDocumentElement();
    }

    @Override
    public void remove(SourceFactory src, String id) throws ProcessException {
        XmlFactory xFac = (XmlFactory)src;
        Document doc = xFac.getDocument();
        Element docEl = getDocumentElement(doc);
        Element el = doc.getElementById(id);
        if(el!=null&&el.getParentNode().isSameNode(docEl)){
            doc.removeChild(el);
        }
    }
}
