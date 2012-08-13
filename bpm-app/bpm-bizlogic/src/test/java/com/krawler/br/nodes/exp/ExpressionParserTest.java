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

import com.krawler.br.ProcessException;
import com.krawler.br.exp.Expression;
import com.krawler.br.exp.Scope;
import com.krawler.br.modules.ModuleBag;
import com.krawler.br.stmt.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import junit.framework.TestCase;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;

/**
 *
 * @author krawler-user
 */
public class ExpressionParserTest extends TestCase {
    
    public ExpressionParserTest(String testName) {
        super(testName);
    }

    /**
     * Test of expression method, of class ExpressionParser.
     */
    public void testParse() {
        String str= "a=1; scp:b=5; c=a+scp:b; name=\"He said, \\\"How are you?\\\"\"; t=a>2||scp:b<7;";
        try {
            ExpressionParser p = getParser(str);
            Scope sc = getScope(null);
            p.addScope(sc);
            p.addScope(getScope("scp"));
            List<Statement> list = p.parseBlock();
            Iterator<Statement> itr = list.iterator();
            System.out.println("block :");
            while (itr.hasNext()) {
                Statement s = itr.next();
                s.execute();
                System.out.println(s.toString());
            }
            System.out.println(sc.getScopeValue("name"));
            System.out.println("End");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            fail(ex.getMessage());
        }
        
    }

    public void testEscapeSequence() {
        String str= "\"Hello\\'\\n\\tWorld\"";
        try {
            ExpressionParser p = getParser(str);

            Expression e = p.parseExpression();
            System.out.println(e.getValue());
            System.out.println(e);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            fail(ex.getMessage());
        }

    }

    public void testIfStatement() {
        String str= "\na=3;name=\"Krawler\";if(a<3){name=\"Vishnu Kant\";}";
        try {
            ExpressionParser p = getParser(str);
            Scope sc = getScope(null);
            p.addScope(sc);
            List<Statement> list = p.parseBlock();
            Iterator<Statement> itr = list.iterator();
            System.out.println("block :");
            while (itr.hasNext()) {
                Statement s = itr.next();
                s.execute();
                System.out.println(s.toString());
            }
            assertEquals("Krawler", sc.getScopeValue("name"));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            fail(ex.getMessage());
        }

    }

    private ExpressionParser getParser(String str){
            ANTLRStringStream input = new ANTLRStringStream(str);
            ExpressionLexer l = new ExpressionLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(l);
            return new ExpressionParser(tokens);
    }

    private Scope getScope(String identity) {

        return new Scope() {
            private String identity;
            private HashMap hm = new HashMap();
            
            public Scope setScope(String identity){
                this.identity=identity;
                return this;
            }

            @Override
            public Object getScopeValue(String key) throws ProcessException {
                return hm.get(key);
            }

            @Override
            public void setScopeValue(String key, Object val) throws ProcessException {
                hm.put(key, val);
            }

            @Override
            public void removeScopeValue(String key) throws ProcessException {
                hm.remove(key);
            }

            @Override
            public String getScopeModuleName(String key) {
                Object o=hm.get(key);
                if("a".equals(key))
                    return ModuleBag.PRIMITIVE.LONG.tagName();
                else if("b".equals(key))
                    return ModuleBag.PRIMITIVE.INT.tagName();
                else if("c".equals(key))
                    return ModuleBag.PRIMITIVE.FLOAT.tagName();
                else if("t".equals(key))
                    return ModuleBag.PRIMITIVE.BOOLEAN.tagName();
                else if("name".equals(key))
                    return ModuleBag.PRIMITIVE.STRING.tagName();
                return null;
            }

            @Override
            public String getIdentity() {
                return identity;
            }
        }.setScope(identity);
    }
}
