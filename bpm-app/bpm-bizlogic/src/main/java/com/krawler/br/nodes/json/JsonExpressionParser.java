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

import com.krawler.br.exp.*;
import com.krawler.br.ProcessException;
import com.krawler.br.exp.EQExpression;
import com.krawler.br.exp.Scope;
import com.krawler.br.nodes.exp.TextExpressionParser;
import com.krawler.br.stmt.Statement;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.List;

/**
 * class to parse the condition from xml dom element
 *
 * @author Vishnu Kant Gupta
 */
public class JsonExpressionParser extends JsonOperandParser {
    private static final String EXPRESSION="expr";      // condition tag
    private static final String OPERATOR="operator";        // operator attribute
    private static final String OPERAND="operand";          // operand tag
    private static final String SIDE="side";                // side attribute
    private static final String SIDE_LEFT="left";           // supported side value as left
    private static final String SIDE_RIGHT="right";         // supported side value as right
    private static final String NOT = "not";
    private static final String LT = "lt";
    private static final String LE = "le";
    private static final String GT = "gt";
    private static final String GE = "ge";
    private static final String EQ = "eq";
    private static final String NQ = "nq";
    private static final String AND = "and";
    private static final String OR = "or";
    private static final String ADD = "add";
    private static final String SUB = "sub";
    private static final String MUL = "mul";
    private static final String DIV = "div";
    private static final String MOD = "mod";
    private static final String NEG = "neg";
   private TextExpressionParser expParser;
   
   public void setExpParser(TextExpressionParser expParser) {
        this.expParser = expParser;
    }

    public JsonExpressionParser(JsonOperandParser successor) {
        super(successor);
    }

    @Override
    public Expression parseExpression(JSONObject parent, Scope scope) throws ProcessException {
        String detailexp = parent.optString(DETAIL);
        if (detailexp.length()>0) {
            return expParser.parseExpression(detailexp, scope);
        } else {
            return super.parseExpression(parent, scope);
        }
    }

    @Override
    public List<Statement> parseBlock(JSONObject parent, Scope scope) throws ProcessException {
        String detailexp = parent.optString(DETAIL);
        if (detailexp.length()>0) {
            return expParser.parseBlock(detailexp, scope);
        } else {
            return super.parseBlock(parent, scope);
        }
    }

        @Override
    public void composeExpression(JSONObject parent, Expression expression) throws ProcessException {
        try{
            String text = expParser.composeExpression(expression);
            parent.put(DETAIL, text);
            parent.put(TYPE, "expression");
        }catch(ProcessException ex){
            super.composeExpression(parent, expression);
        }catch(JSONException je){
            throw new ProcessException(je.getMessage(), je);
        }
    }

    @Override
    public void composeBlock(JSONObject parent, List<Statement> statements) throws ProcessException {
        try{
            String text = expParser.composeBlock(statements);
            parent.put(DETAIL, text);
            parent.put(TYPE, "expression");
        }catch(ProcessException ex){
            super.composeBlock(parent, statements);
        }catch(JSONException je){
            throw new ProcessException(je.getMessage(), je);
        }
    }

    private Expression getExpression(String opr, JSONObject detail, Scope scope) throws ProcessException{
        Expression e=null;
            if( EQ.equals(opr)){
                e = new EQExpression();
                setBothOperand(detail, scope, e);
            }else if( NQ.equals(opr)){
                e = new NQExpression();
                setBothOperand(detail, scope, e);
            }else if( GE.equals(opr)){
                e = new GEExpression();
                setBothOperand(detail, scope, e);
            }else if( GT.equals(opr)){
                e = new GTExpression();
                setBothOperand(detail, scope, e);
            }else if( LE.equals(opr)){
                e = new LEExpression();
                setBothOperand(detail, scope, e);
            }else if( LT.equals(opr)){
                e = new LTExpression();
                setBothOperand(detail, scope, e);
            }else if( NOT.equals(opr)){
                e = new LogicalNotExpression();
                setOperand(detail, scope, e);
            }else if( AND.equals(opr)){
                e = new LogicalAndExpression();
                setBothOperand(detail, scope, e);
            }else if( OR.equals(opr)){
                e = new LogicalOrExpression();
                setBothOperand(detail, scope, e);
            }else if( ADD.equals(opr)){
                e = new AddExpression();
                setBothOperand(detail, scope, e);
            }else if( SUB.equals(opr)){
                e = new SubtractExpression();
                setBothOperand(detail, scope, e);
            }else if( MUL.equals(opr)){
                e = new MultiplyExpression();
                setBothOperand(detail, scope, e);
            }else if( DIV.equals(opr)){
                e = new DivideExpression();
                setBothOperand(detail, scope, e);
            }else if( MOD.equals(opr)){
                e = new ModuloExpression();
                setBothOperand(detail, scope, e);
            }else if( NEG.equals(opr)){
                e = new NegateExpression();
                setOperand(detail, scope, e);
            }else {
                throw new ProcessException("operator "+opr+" is not recognised");
            }

        return e;
    }

    private void setOperand(JSONObject detail, Scope scope, Expression e) throws ProcessException{
        ((UnaryExpression)e).setOperand(starter.parseExpression(detail.optJSONObject(SIDE_RIGHT), scope));
    }
    private void setBothOperand(JSONObject detail, Scope scope, Expression e) throws ProcessException{
        BinaryExpression exp = (BinaryExpression)e;

        exp.setOperand(starter.parseExpression(detail.optJSONObject(SIDE_LEFT), scope));
        exp.setOtherOperand(starter.parseExpression(detail.optJSONObject(SIDE_RIGHT), scope));
    }
}
