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
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.XmlFactory;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * class to parse the process tag to create a proper process
 *
 * @author Vishnu Kant Gupta
 */
public class XmlProcessNodeParser extends XmlNodeParser {
    public static final String PROCESS="process";
    public static final String NODELIST="node-list";
    public static final String NODE="node";
    public static final String NODE_ID="id";
    public static final String INIT="init";
    public static final String INVOKE="invoke";
    public XmlProcessNodeParser(NodeParser successor) {
        super(successor);
    }

    // TODO: modify this function to check the type of argument also. can we check the argument?
    @Override
    public FlowNode parse(SourceFactory src, String sourceid, String id) throws ProcessException {
        XmlFactory factory = (XmlFactory)src;
        Element el= factory.getDocument().getElementById(sourceid);
        BProcess p = new BProcess();
        p.setSourceid(sourceid);
        p.setId(id);
        p.setInputParams(parseParams(el, IN_VAR));
        p.setOutputParam(parseParam(operandParser.util.getChildElement(el,OUT_VAR)));
        p.setLocalParams(parseParams(el, LOCAL_VAR));

        List<Element> nl = factory.getDomUtil().getChildElements(factory.getDomUtil().getChildElement(el, NODELIST),NODE);
        String initid=el.getAttribute(INIT);
        List<Element> l= factory.getDomUtil().filterList(NODE_ID,el.getAttribute(INIT), nl);
        if(initid==null||initid.length()==0||l.isEmpty())
            throw new ProcessException("Initial node not defined for Process : "+sourceid);
        if(l.size()>1)
            throw new ProcessException("More than one Initial node defined for Process : "+sourceid);

        p.setInitialNode(initid);
        Iterator<Element> itr = nl.iterator();
        while(itr.hasNext()){
            Element e = itr.next();
            XmlDecorationsHolder dHolder =  new XmlDecorationsHolder();
            dHolder.setScope(p);
            dHolder.setParser(getDecoratorParser());
            dHolder.setNodeElement(e);
            FlowNode node = ((NodeParser)src.getParser()).parseNode(src, e.getAttribute(INVOKE), e.getAttribute(NODE_ID), dHolder);
            p.addFlowNode(node);
        }

        return p;
    }

    @Override
    public boolean canParse(SourceFactory src, String type) {
        XmlFactory factory = (XmlFactory)src;
        Element el= factory.getDocument().getElementById(type);
        return el!=null&&el.getTagName().equals(PROCESS);
    }

    @Override
    public boolean canCompose(FlowNode node) {
        return node!=null && node instanceof BProcess;
    }

    @Override
    public void compose(SourceFactory src, FlowNode node) throws ProcessException {
        Document doc = ((XmlFactory) src).getDocument();
        BProcess p = (BProcess) node;
        Element root=getDocumentElement(doc);
        Element oel = doc.getElementById(p.getSourceid());
        if(oel!=null)root.removeChild(oel);
        Element el = doc.createElement(PROCESS);
        root.appendChild(el);
        el.setAttribute("id", p.getSourceid());
        el.setAttribute("init", p.getInitialNode());
        composeParams(el, IN_VAR, p.getInputParams());
        composeParams(el, LOCAL_VAR, p.getLocalParams());
        OperationParameter out = p.getOutputParam();
        if(out!=null){
            Element outEl = doc.createElement(OUT_VAR);
            el.appendChild(outEl);
            composeParam(outEl, out);
        }
        if(p.getInitialNode()==null)
            throw new ProcessException("Process ["+p.getSourceid()+"] has no initial node");

        Element nl=doc.createElement(NODELIST);
        el.appendChild(nl);
        Map<String, FlowNode> nodes = p.getAllFlowNodes();
        Iterator<String> itr = nodes.keySet().iterator();
        while(itr.hasNext()){
            String nodeid=itr.next();
            FlowNode enode = nodes.get(nodeid);
            Element e=doc.createElement(NODE);
            nl.appendChild(e);
            e.setAttribute(NODE_ID, nodeid);
            e.setAttribute(INVOKE, enode.getSourceid());

            XmlDecorationsHolder dHolder =  new XmlDecorationsHolder();
            dHolder.setScope(p);
            dHolder.setParser(getDecoratorParser());
            dHolder.setNodeElement(e);
            ((NodeParser)src.getParser()).composeNode(src, enode, dHolder);
        }
    }
}
