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
import com.krawler.br.FlowNode;
import com.krawler.br.ProcessException;
import com.krawler.br.decorators.xml.XmlDecorationsHolder;
import com.krawler.br.exp.Constant;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.XmlFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * class used to parse the switch-case:m-...-case:n-otherwise structure
 *
 * @author Vishnu Kant Gupta
 */
public class XmlSwitchNodeParser extends XmlNodeParser {
    
    private static final String SWITCH="switch";
    private static final String CASE_LABEL="label";
    private static final String EXPR="expr";
    private static final String CASE="case";
    private static final String CASE_NODE="then";
    private static final String OTHERWISE="otherwise";

    public XmlSwitchNodeParser(NodeParser successor) {
        super(successor);
    }

    // TODO: modify this function to check the type of argument also. can we check the argument?
    @Override
    public FlowNode parse(SourceFactory src, String sourceid, String id) throws ProcessException {
        XmlFactory factory = (XmlFactory)src;
        Element el= factory.getDocument().getElementById(sourceid);
        Switch s = new Switch();
        s.setSourceid(sourceid);
        s.setId(id);
        s.setInputParams(parseParams(el, IN_VAR));
        s.setOutputParam(parseParam(operandParser.util.getChildElement(el,OUT_VAR)));
        s.setLocalParams(parseParams(el, LOCAL_VAR));

        List<Element> nl = factory.getDomUtil().getChildElements(el);
        for(int i=0; i<nl.size();i++){
                Element e=nl.get(i);
                if(e.getTagName().equals(CASE)){
                    String then=e.getAttribute(CASE_NODE);
                    XmlDecorationsHolder dHolder =  new XmlDecorationsHolder();
                    dHolder.setScope(s);
                    dHolder.setParser(getDecoratorParser());
                    dHolder.setNodeElement(e);
                    FlowNode node=((NodeParser)src.getParser()).parseNode(src, then, id+"_"+then, dHolder);

                    Constant cons=new Constant();
                    cons.setValue(Integer.parseInt(e.getAttribute(CASE_LABEL)));
                    s.addCase(cons, node);
                }else if(e.getTagName().equals(EXPR)){
                    s.setExpression(operandParser.parseExpression(e, s));
                }
        }
        if(s.getExpression()==null){
            throw new ProcessException("switch without an expression!");
        }
        if(el.hasAttribute(OTHERWISE)){
            XmlDecorationsHolder dHolder =  new XmlDecorationsHolder();
            dHolder.setScope(s);
            dHolder.setParser(getDecoratorParser());
            dHolder.setNodeElement(el);
            FlowNode node=((NodeParser)src.getParser()).parseNode(src, el.getAttribute(OTHERWISE),id+"_ow", dHolder);
            s.setDefault(node);
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
        XmlFactory factory = (XmlFactory)src;
        Element el= factory.getDocument().getElementById(type);
        return el!=null&&el.getTagName().equals(SWITCH);
    }

    @Override
    public boolean canCompose(FlowNode node) {
        return node!=null && node instanceof Switch;
    }

    @Override
    public void compose(SourceFactory src, FlowNode node) throws ProcessException {
        Document doc = ((XmlFactory) src).getDocument();
        Switch s = (Switch) node;
        Element root=getDocumentElement(doc);
        Element el = doc.createElement(SWITCH);
        root.appendChild(el);
        el.setAttribute("id", s.getSourceid());
        composeParams(el, IN_VAR, s.getInputParams());
        composeParams(el, LOCAL_VAR, s.getLocalParams());
        OperationParameter out = s.getOutputParam();
        if(out!=null){
            Element outEl = doc.createElement(OUT_VAR);
            el.appendChild(outEl);
            composeParam(outEl, out);
        }
        Map<Integer, FlowNode> map=s.getCases();
        Iterator<Integer> itr = map.keySet().iterator();
        while(itr.hasNext()){
            Integer label = itr.next();
            FlowNode cnode = map.get(label);
            Element e = doc.createElement(CASE);
            el.appendChild(e);
            e.setAttribute(CASE_LABEL, label.toString());
            e.setAttribute(CASE_NODE, cnode.getSourceid());
            XmlDecorationsHolder dHolder =  new XmlDecorationsHolder();
            dHolder.setScope(s);
            dHolder.setParser(getDecoratorParser());
            dHolder.setNodeElement(e);
            ((NodeParser)src.getParser()).composeNode(src, cnode, dHolder);
        }
        if(s.getDefault()!=null){
            el.setAttribute(OTHERWISE, s.getDefault().getSourceid());
            XmlDecorationsHolder dHolder =  new XmlDecorationsHolder();
            dHolder.setScope(s);
            dHolder.setParser(getDecoratorParser());
            dHolder.setNodeElement(el);
            ((NodeParser)src.getParser()).composeNode(src, s.getDefault(), dHolder);
        }
    }
}
