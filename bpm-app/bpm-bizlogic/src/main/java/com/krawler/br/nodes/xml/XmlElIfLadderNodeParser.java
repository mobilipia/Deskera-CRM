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
import com.krawler.br.exp.ConditionalExpression;
import com.krawler.br.exp.Expression;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.XmlFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class for parsing the if-elseif-elseif-...-else structure
 *
 * @author Vishnu Kant Gupta
 */
public class XmlElIfLadderNodeParser extends XmlNodeParser {
    
    private static final String LADDER="elseif-ledder";
    private static final String IF="if";
    private static final String THEN="then";
    private static final String WHEN="when";
    private static final String ELSE="else";

    public XmlElIfLadderNodeParser(NodeParser successor) {
        super(successor);
    }

    // TODO: modify this function to check the type of argument also. can we check the argument?
    @Override
    public FlowNode parse(SourceFactory src, String sourceid, String id) throws ProcessException {
        XmlFactory factory = (XmlFactory)src;
        Element el= factory.getDocument().getElementById(sourceid);
        ElIfLadder lad = new ElIfLadder();
        lad.setId(id);
        lad.setSourceid(sourceid);
        lad.setInputParams(parseParams(el, IN_VAR));
        lad.setOutputParam(parseParam(operandParser.util.getChildElement(el,OUT_VAR)));
        lad.setLocalParams(parseParams(el, LOCAL_VAR));

        List<Element> nl = factory.getDomUtil().getChildElements(el,IF);
        ArrayList<IfBlock> hm= new ArrayList();
        for(int i=0; i<nl.size();i++){
                Element e=nl.get(i);
                String then=e.getAttribute(THEN);
                IfBlock ifblk = new IfBlock();
                XmlDecorationsHolder dHolder =  new XmlDecorationsHolder();
                dHolder.setScope(lad);
                dHolder.setParser(getDecoratorParser());
                dHolder.setNodeElement(e);
                FlowNode node=((NodeParser)src.getParser()).parseNode(src, then, id+"_"+then,dHolder);
                ifblk.setThen(node);
                ifblk.setWhen((ConditionalExpression)operandParser.parseExpression(operandParser.util.getChildElement(e,WHEN),lad));
                hm.add(ifblk);
        }
        lad.setIfBlocks(hm);
        if(el.hasAttribute(ELSE)){
            XmlDecorationsHolder dHolder =  new XmlDecorationsHolder();
            dHolder.setScope(lad);
            dHolder.setParser(getDecoratorParser());
            dHolder.setNodeElement(el);
            FlowNode node=((NodeParser)src.getParser()).parseNode(src, el.getAttribute(ELSE),id+"_else",dHolder);
            lad.setElseNode(node);
        }

        return lad;
    }

    @Override
    public boolean canParse(SourceFactory src, String type) {
        XmlFactory factory = (XmlFactory)src;
        Element el= factory.getDocument().getElementById(type);
        return el!=null&&el.getTagName().equals(LADDER);
    }

    @Override
    public boolean canCompose(FlowNode node) {
        return node!=null && node instanceof ElIfLadder;
    }

    @Override
    public void compose(SourceFactory src, FlowNode node) throws ProcessException {
        Document doc = ((XmlFactory) src).getDocument();
        ElIfLadder ladder = (ElIfLadder) node;
        Element root=getDocumentElement(doc);
        Element el = doc.createElement(LADDER);
        root.appendChild(el);
        el.setAttribute("id", ladder.getSourceid());
        composeParams(el, IN_VAR, ladder.getInputParams());
        composeParams(el, LOCAL_VAR, ladder.getLocalParams());
        OperationParameter out = ladder.getOutputParam();
        if(out!=null){
            Element outEl = doc.createElement(OUT_VAR);
            el.appendChild(outEl);
            composeParam(outEl, out);
        }
        List<IfBlock> list=ladder.getIfBlocks();
        Iterator<IfBlock> itr = list.iterator();
        while(itr.hasNext()){
            IfBlock ifblk = itr.next();
            Element e = doc.createElement(IF);
            el.appendChild(e);
            e.setAttribute(THEN, ifblk.getThen().getSourceid());
            Element when = doc.createElement(WHEN);
            e.appendChild(when);
            operandParser.composeExpression(when, (Expression)ifblk.getWhen());
            XmlDecorationsHolder dHolder =  new XmlDecorationsHolder();
            dHolder.setScope(ladder);
            dHolder.setParser(getDecoratorParser());
            dHolder.setNodeElement(e);
            ((NodeParser)src.getParser()).composeNode(src, ifblk.getThen(), dHolder);
        }
        if(ladder.getElseNode()!=null){
            el.setAttribute(ELSE, ladder.getElseNode().getSourceid());
            XmlDecorationsHolder dHolder =  new XmlDecorationsHolder();
            dHolder.setScope(ladder);
            dHolder.setParser(getDecoratorParser());
            dHolder.setNodeElement(el);
            ((NodeParser)src.getParser()).composeNode(src, ladder.getElseNode(), dHolder);
        }
    }
}
