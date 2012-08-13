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
package com.krawler.br.decorators.json;

import com.krawler.br.FlowNode;
import com.krawler.br.exp.Expression;
import com.krawler.br.ProcessException;
import com.krawler.br.decorators.StatementsSupportedFlowNode;
import com.krawler.br.decorators.DecorationsHolder;
import com.krawler.br.decorators.DecoratorParser;
import com.krawler.br.decorators.RemovalSupportedFlowNode;
import com.krawler.br.exp.Variable;
import com.krawler.br.stmt.Assignment;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.utils.json.base.JSONArray;
import java.util.ArrayList;
import com.krawler.br.stmt.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author krawler-user
 */
public class JsonStatementDecoratorParser extends JsonDecoratorParser {

    private static final String PRE_STATEMENTS = "prestatements";
    private static final String POST_STATEMENTS = "poststatements";
    public static final String LHS = "lhs";
    public static final String RHS = "rhs";

    public JsonStatementDecoratorParser(DecoratorParser successor) {
        super(successor);
    }

    @Override
    public FlowNode parse(FlowNode baseNode, DecorationsHolder dHolder) throws ProcessException {
        JSONArray jArr = ((JsonDecorationsHolder) dHolder).getNodeObject().optJSONArray(PRE_STATEMENTS);
        if(jArr!=null&&jArr.length()>0){
            try {
                List<Statement> statements = operandParser.parseBlock(jArr.getJSONObject(0), dHolder.getScope());
                if (statements != null && !statements.isEmpty()) {
                    StatementsSupportedFlowNode af = new StatementsSupportedFlowNode(baseNode);
                    af.setAfter(false);
                    af.setStatements(statements);
                    baseNode = af;
                }
            } catch (JSONException ex) {
                Logger.getLogger(JsonStatementDecoratorParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        jArr = ((JsonDecorationsHolder) dHolder).getNodeObject().optJSONArray(POST_STATEMENTS);
        if(jArr!=null&&jArr.length()>0){
            try {
                List<Statement> statements = operandParser.parseBlock(jArr.getJSONObject(0), dHolder.getScope());
                if (statements != null && !statements.isEmpty()) {
                    StatementsSupportedFlowNode af = new StatementsSupportedFlowNode(baseNode);
                    af.setAfter(true);
                    af.setStatements(statements);
                    baseNode = af;
                }
                return super.parse(baseNode, dHolder);
            } catch (JSONException ex) {
                Logger.getLogger(JsonStatementDecoratorParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.parse(baseNode, dHolder);
    }


    @Override
    public FlowNode compose(FlowNode wrappedNode, DecorationsHolder dHolder) throws ProcessException {
        if (wrappedNode instanceof StatementsSupportedFlowNode) {
            StatementsSupportedFlowNode node = (StatementsSupportedFlowNode) wrappedNode;
            JSONObject nodeEl = ((JsonDecorationsHolder) dHolder).getNodeObject();
            JSONObject el=new JSONObject();

            JSONArray jArr = new JSONArray();
            jArr.put(el);
            try {
                nodeEl.put(node.isAfter() ? POST_STATEMENTS : PRE_STATEMENTS, jArr);
            } catch (JSONException ex) {
                throw new ProcessException(ex.getMessage(), ex);
            }
            operandParser.composeBlock(el, node.getStatements());
            return wrappedNode.getBaseNode(false);
        } else {
            return super.compose(wrappedNode, dHolder);
        }
    }

    public void parseStatements(FlowNode baseNode, DecorationsHolder dHolder, String stmtType) throws ProcessException{
        JSONArray jArr = ((JsonDecorationsHolder) dHolder).getNodeObject().optJSONArray(stmtType);
        if (jArr != null && jArr.length() > 0) {
            ArrayList list = new ArrayList();
            for (int i = 0; i < jArr.length(); i++) {
                list.add(parseAssignment(jArr.optJSONObject(i), dHolder));
            }
            if (!list.isEmpty()) {
                StatementsSupportedFlowNode af = new StatementsSupportedFlowNode(baseNode);
                    af.setAfter(true);
                af.setStatements(list);
                baseNode = af;
            }

        }
    }
 
    public Assignment parseAssignment(JSONObject stmtObj, DecorationsHolder dHolder) throws ProcessException {
        Assignment assignment = new Assignment();
        assignment.setLhs((Variable) operandParser.parseExpression(stmtObj.optJSONObject(LHS), dHolder.getScope()));
        assignment.setRhs(operandParser.parseExpression(stmtObj.optJSONObject(RHS), dHolder.getScope()));
        return assignment;
    }
}
