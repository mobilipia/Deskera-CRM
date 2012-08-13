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

package com.krawler.br.nodes.exp;

import com.krawler.br.exp.Expression;
import com.krawler.br.ProcessException;
import com.krawler.br.exp.Scope;
import com.krawler.br.modules.ModuleBag;
import com.krawler.br.stmt.Statement;
import java.util.Iterator;
import java.util.List;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

/**
 *
 * @author krawler-user
 */
public class TextExpressionParser {
    private Scope[] scopes;
    private ModuleBag moduleBag;

    public void setScopes(Scope[] scopes) {
        this.scopes = scopes;
    }

    public void setModuleBag(ModuleBag moduleBag) {
        this.moduleBag = moduleBag;
    }

    public Expression parseExpression(String text, Scope localScope) throws ProcessException {
        return getExpressionParser(text, localScope).parseExpression();
    }

    public List<Statement> parseBlock(String text, Scope localScope) throws ProcessException {
        return getExpressionParser(text, localScope).parseBlock();
    }

    private ExpressionParser getExpressionParser(String text, Scope localScope){
        ExpressionLexer l = new ExpressionLexer(new ANTLRStringStream(text));
        ExpressionParser p = new ExpressionParser(new CommonTokenStream(l));
        p.setModuleBag(moduleBag);
        p.addScope(localScope);
        if(scopes!=null){
            for(int i=0;i<scopes.length;i++)
                p.addScope(scopes[i]);
        }
        return p;
    }

    public String composeExpression(Expression expression) throws ProcessException{
        return expression.toString();
    }

    public String composeBlock(List<Statement> statements) throws ProcessException{
        Iterator<Statement> itr = statements.iterator();
        String text="";
        while(itr.hasNext()){
            text+=itr.next().toString();
        }
        return text;
    }
}
