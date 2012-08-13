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

package com.krawler.br.nodes;

import com.krawler.br.FlowNode;
import com.krawler.br.ProcessException;
import com.krawler.br.decorators.DecorationsHolder;
import com.krawler.br.decorators.DecoratorParser;
import com.krawler.br.operations.OperationBag;
import com.krawler.br.utils.HierarchicalSourceFactory;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.SourceParser;

/**
 * The class defines the abstact way of parsing the flow nodes
 * <p>
 * this class uses the do or delegate methodology foe parsing.
 * for that purpose, it holds a reference of the next parser
 * which can be used if there is a possibility that this parser cannot parse
 * the given type
 * <br />
 * also this parser holds a reference of header parse of the chain
 * which can be used to perform parsing recursively.
 * </p>
 *
 *
 * @author Vishnu Kant Gupta
 */
public abstract class NodeParser implements SourceParser {
    private NodeParser successor; // successor node parser, to be used if current parser not able to recognise the type
//    protected NodeParser starter; // the header node parser in case of there is a need for recursive parsing of type
    private OperationBag operationBag; // operation factory to be used for type check and argument count matching
    private DecoratorParser decoratorParser;

    /**
     * costructor for node parser with its successor
     *
     * @param successor next node parser, to be used if current parser not able to recognise the type
     */
    public NodeParser(NodeParser successor) {
        this.successor = successor;
    }

    public OperationBag getOperationBag() {
        return operationBag;
    }

    public void setOperationBag(OperationBag operationBag) {
        this.operationBag = operationBag;
    }

    public DecoratorParser getDecoratorParser() {
        return decoratorParser;
    }

    public void setDecoratorParser(DecoratorParser decoratorParser) {
        this.decoratorParser = decoratorParser;
    }

    protected FlowNode decorate(FlowNode node, DecorationsHolder dHolder) throws ProcessException{
        if(dHolder.getParser()!=null)
            return dHolder.getParser().parse(node, dHolder);
        return node;
    }

    protected FlowNode undecorate(FlowNode node, DecorationsHolder dHolder) throws ProcessException{
        FlowNode baseNode = node.getBaseNode(true);
        if(dHolder.getParser()!=null){
            while(baseNode!=node){
                dHolder.getParser().compose(node, dHolder);
                node = node.getBaseNode(false);
            }
        }
        return baseNode;
    }

    /**
     * parses the given type if possible, if this parser can not recognise
     * the type, then  the task will be forwarded to the next available parser
     *
     * @param type the type id of the element
     * @param id identifier to set if the type is parsable
     * @param params parameter definitions to set if type is parsable
     * @return flow node constructed
     * @throws com.krawler.br.InvalidFlowException if there is a problem in parsing the type
     * @throws com.krawler.br.ProcessException if there is a problem in parsing the type
     * @throws IllegalArgumentException if the type is not recognised
     * by any of the parser
     */
    public FlowNode parseNode(SourceFactory src, String sourceid, String id, DecorationsHolder dHolder) throws ProcessException {
        if(canParse(src, sourceid)){
            FlowNode node = parse(src, sourceid, id);
            if(dHolder!=null)node=decorate(node,dHolder);
            return node;
        }else{
            if(successor!=null){
                return successor.parseNode(src, sourceid, id, dHolder);
            }else if(src instanceof HierarchicalSourceFactory){
                SourceFactory pSrc = ((HierarchicalSourceFactory)src).getParentSourceFactory();
                if(pSrc!=null)
                    return ((NodeParser)pSrc.getParser()).parseNode(pSrc, sourceid, id, dHolder);
            }
            
            throw new IllegalArgumentException("Unparsable node found: "+sourceid+", Either node definition or node parser missing");
        }
    }

    /**
     * checks to see whether the given type can be parsed by this node parser
     *
     * @param type type id to check
     * @return true if the type can be parsed, false otherwise
     */
    public abstract boolean canParse(SourceFactory src, String type);
    
    /**
     * parses a flow node representing the flow node by given type
     * and parameters
     *
     * @param type the type id of the flow node (generally id  of tag)
     * @param id identifier to be set for the flow node
     * @param params parameters definitions for the flow node
     * @return the flow node representing the flow node
     * @throws InvalidFlowException if the flow node cannot be constructed because
     * of the invalid flow
     * @throws ProcessException if the flow node cannot be constructed because of
     * there is some problem in the structure
     * @throws IllegalArgumentException if the parameter count does not match
     */
    public abstract FlowNode parse(SourceFactory src, String sourceid, String id) throws ProcessException;

    /**
     * checks to see whether the given type can be parsed by this node parser
     *
     * @param type type id to check
     * @return true if the type can be parsed, false otherwise
     */
    public abstract boolean canCompose(FlowNode node);
    public void composeNode(SourceFactory src, FlowNode node, DecorationsHolder dHolder) throws ProcessException {
        if(canCompose(node.getBaseNode(true))){
            undecorate(node, dHolder);
            if(dHolder==null)
                compose(src,node.getBaseNode(true));
        }else{
            if(successor!=null){
                successor.composeNode(src, node, dHolder);
            }else

                throw new IllegalArgumentException("Noncomposable flownode : "+node.getId()+"["+node.getSourceid()+"]"+", Either node definition or node parser missing");
        }
    }

    public abstract void compose(SourceFactory src, FlowNode node) throws ProcessException;

    public abstract void remove(SourceFactory src, String id) throws ProcessException;
}
