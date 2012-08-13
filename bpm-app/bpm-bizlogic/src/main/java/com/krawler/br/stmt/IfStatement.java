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

package com.krawler.br.stmt;

import com.krawler.br.ProcessException;
import com.krawler.br.exp.ConditionalExpression;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author krawler-user
 */
public class IfStatement implements Statement {
    private ConditionalExpression condition;
    private List<Statement> block;
    @Override
    public void execute() throws ProcessException {
        if(condition.getConditionValue()){
            Iterator<Statement> itr = block.iterator();
            while(itr.hasNext())
                itr.next().execute();
        }
    }

    public List<Statement> getBlock() {
        return block;
    }

    public void setBlock(List<Statement> block) {
        this.block = block;
    }

    public ConditionalExpression getCondition() {
        return condition;
    }

    public void setCondition(ConditionalExpression condition) {
        this.condition = condition;
    }


    @Override
    public String toString(){
        StringBuilder blk = new StringBuilder();
        Iterator<Statement> itr = block.iterator();
        while(itr.hasNext())
            blk.append("\n\t"+itr.next().toString());
        return "if("+condition.toString()+"){"+blk.toString()+"\n}";
    }
}
