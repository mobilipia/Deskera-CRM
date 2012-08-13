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
import com.krawler.br.operations.OperationDefinition;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.modules.ModuleBag;
import com.krawler.br.utils.SourceFactory;

/**
 * Class to parse the activities defined in the process structure
 *
 * @author Vishnu Kant Gupta
 */
public class XmlActivityNodeParser extends XmlNodeParser {
    public XmlActivityNodeParser(NodeParser successor) {
        super(successor);
    }

    // TODO: modify this function to check the type of argument also. can we check the argument?
    @Override
    public FlowNode parse(SourceFactory src, String sourceid, String id) throws ProcessException {
        OperationDefinition op=getOperationBag().getOperationDefinition(sourceid);
        Activity act=new Activity();
        act.setId(id);
        act.setSourceid(sourceid);
        act.setOperation(op);
        return act;
    }

    @Override
    public boolean canParse(SourceFactory src, String type) {
        return getOperationBag().hasOperation(type);
    }

    @Override
    public boolean canCompose(FlowNode node) {
        return node!=null && node instanceof Activity;
    }

    @Override
    public void compose(SourceFactory src, FlowNode node) throws ProcessException {
        //TODO
    }
}
