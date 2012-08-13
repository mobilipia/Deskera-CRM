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
import com.krawler.br.modules.ModuleBag;
import com.krawler.br.exp.*;
import com.krawler.br.stmt.*;
import java.util.HashMap;
import java.util.Map;


import org.antlr.runtime.*;
import java.util.List;
import java.util.ArrayList;

public class ExpressionParser extends Parser {

    public static final String[] tokenNames = new String[]{
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "IDENTIFIER", "INTLITERAL", "LONGLITERAL", "FLOATLITERAL", "DOUBLELITERAL", "CHARLITERAL", "STRINGLITERAL", "TRUE", "FALSE", "NULL", "IntegerNumber", "LongSuffix", "HexPrefix", "HexDigit", "Exponent", "NonIntegerNumber", "FloatSuffix", "DoubleSuffix", "IdentifierStart", "IdentifierPart", "WS", "COMMA", "LPAREN", "RPAREN", "PLUS", "MINUS", "MULTI", "DIV", "MOD", "'='", "';'", "'if'", "'{'", "'}'", "'||'", "'&&'", "'=='", "'!='", "'<='", "'>='", "'<'", "'>'", "'!'", "':'", "'.'", "'['", "']'"
    };
    public static final int IntegerNumber = 14;
    public static final int MOD = 32;
    public static final int Exponent = 18;
    public static final int MULTI = 30;
    public static final int EOF = -1;
    public static final int HexDigit = 17;
    public static final int LPAREN = 26;
    public static final int RPAREN = 27;
    public static final int COMMA = 25;
    public static final int IDENTIFIER = 4;
    public static final int NonIntegerNumber = 19;
    public static final int FloatSuffix = 20;
    public static final int PLUS = 28;
    public static final int T__50 = 50;
    public static final int IdentifierPart = 23;
    public static final int HexPrefix = 16;
    public static final int T__42 = 42;
    public static final int T__43 = 43;
    public static final int T__40 = 40;
    public static final int T__41 = 41;
    public static final int T__46 = 46;
    public static final int T__47 = 47;
    public static final int T__44 = 44;
    public static final int T__45 = 45;
    public static final int T__48 = 48;
    public static final int T__49 = 49;
    public static final int NULL = 13;
    public static final int DOUBLELITERAL = 8;
    public static final int IdentifierStart = 22;
    public static final int MINUS = 29;
    public static final int INTLITERAL = 5;
    public static final int TRUE = 11;
    public static final int LONGLITERAL = 6;
    public static final int LongSuffix = 15;
    public static final int T__33 = 33;
    public static final int WS = 24;
    public static final int T__34 = 34;
    public static final int T__35 = 35;
    public static final int T__36 = 36;
    public static final int DoubleSuffix = 21;
    public static final int T__37 = 37;
    public static final int T__38 = 38;
    public static final int T__39 = 39;
    public static final int STRINGLITERAL = 10;
    public static final int CHARLITERAL = 9;
    public static final int DIV = 31;
    public static final int FALSE = 12;
    public static final int FLOATLITERAL = 7;

    // delegates
    // delegators
    public ExpressionParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }

    public ExpressionParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);

    }

    @Override
    public String[] getTokenNames() {
        return ExpressionParser.tokenNames;
    }

    @Override
    public String getGrammarFileName() {
        return "/home/krawler-user/Expression.g";
    }
    private HashMap<String, Scope> scopes = new HashMap<String, Scope>();
    private ModuleBag moduleBag;

    public void addScope(Scope scope) {
        if (scope != null) {
            scopes.put(scope.getIdentity(), scope);
        }
    }

    public void setModuleBag(ModuleBag moduleBag) {
        this.moduleBag = moduleBag;
    }

    // $ANTLR start "parseBlock"
    // /home/krawler-user/Expression.g:36:1: parseBlock returns [List<Statement> value] : stmtBlock EOF ;
    public final List<Statement> parseBlock() throws ProcessException {
        List<Statement> value = null;

        List<Statement> stmtBlock1 = null;


        try {
            // /home/krawler-user/Expression.g:40:2: ( stmtBlock EOF )
            // /home/krawler-user/Expression.g:40:5: stmtBlock EOF
            {
                pushFollow(FOLLOW_stmtBlock_in_parseBlock42);
                stmtBlock1 = stmtBlock();

                state._fsp--;

                value = stmtBlock1;
                match(input, EOF, FOLLOW_EOF_in_parseBlock48);

            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "parseBlock"

    // $ANTLR start "parseExpression"
    // /home/krawler-user/Expression.g:44:1: parseExpression returns [Expression value] : expression EOF ;
    public final Expression parseExpression() throws ProcessException {
        Expression value = null;

        Expression expression2 = null;


        try {
            // /home/krawler-user/Expression.g:45:2: ( expression EOF )
            // /home/krawler-user/Expression.g:45:4: expression EOF
            {
                pushFollow(FOLLOW_expression_in_parseExpression65);
                expression2 = expression();

                state._fsp--;

                value = expression2;
                match(input, EOF, FOLLOW_EOF_in_parseExpression72);

            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "parseExpression"

    // $ANTLR start "stmtBlock"
    // /home/krawler-user/Expression.g:49:1: stmtBlock returns [List<Statement> value] : (s= statement )+ ;
    public final List<Statement> stmtBlock() throws ProcessException {
        List<Statement> value = null;

        Statement s = null;


        try {
            // /home/krawler-user/Expression.g:50:2: ( (s= statement )+ )
            // /home/krawler-user/Expression.g:50:5: (s= statement )+
            {
                List<Statement> list = new ArrayList();
                // /home/krawler-user/Expression.g:51:3: (s= statement )+
                int cnt1 = 0;
                loop1:
                do {
                    int alt1 = 2;
                    int LA1_0 = input.LA(1);

                    if ((LA1_0 == IDENTIFIER || LA1_0 == 35)) {
                        alt1 = 1;
                    }


                    switch (alt1) {
                        case 1: // /home/krawler-user/Expression.g:51:4: s= statement
                        {
                            pushFollow(FOLLOW_statement_in_stmtBlock95);
                            s = statement();

                            state._fsp--;

                            list.add(s);

                        }
                        break;

                        default:
                            if (cnt1 >= 1) {
                                break loop1;
                            }
                            EarlyExitException eee =
                                    new EarlyExitException(1, input);
                            throw eee;
                    }
                    cnt1++;
                } while (true);

                value = list;

            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "stmtBlock"

    // $ANTLR start "statement"
    // /home/krawler-user/Expression.g:55:1: statement returns [Statement value] : ( assignment | ifstatement );
    public final Statement statement() throws ProcessException {
        Statement value = null;

        Assignment assignment3 = null;

        ExpressionParser.ifstatement_return ifstatement4 = null;


        try {
            // /home/krawler-user/Expression.g:56:5: ( assignment | ifstatement )
            int alt2 = 2;
            int LA2_0 = input.LA(1);

            if ((LA2_0 == IDENTIFIER)) {
                alt2 = 1;
            } else if ((LA2_0 == 35)) {
                alt2 = 2;
            } else {
                NoViableAltException nvae =
                        new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1: // /home/krawler-user/Expression.g:56:9: assignment
                {
                    pushFollow(FOLLOW_assignment_in_statement123);
                    assignment3 = assignment();

                    state._fsp--;

                    value = assignment3;

                }
                break;
                case 2: // /home/krawler-user/Expression.g:57:7: ifstatement
                {
                    pushFollow(FOLLOW_ifstatement_in_statement133);
                    ifstatement4 = ifstatement();

                    state._fsp--;

                    value = (ifstatement4 != null ? ifstatement4.value : null);

                }
                break;

            }
        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "statement"

    // $ANTLR start "assignment"
    // /home/krawler-user/Expression.g:60:1: assignment returns [Assignment value] : v= variable '=' e= expression ';' ;
    public final Assignment assignment() throws ProcessException {
        Assignment value = null;

        Variable v = null;

        Expression e = null;


        try {
            // /home/krawler-user/Expression.g:61:5: (v= variable '=' e= expression ';' )
            // /home/krawler-user/Expression.g:61:9: v= variable '=' e= expression ';'
            {
                pushFollow(FOLLOW_variable_in_assignment164);
                v = variable();

                state._fsp--;

                match(input, 33, FOLLOW_33_in_assignment165);
                pushFollow(FOLLOW_expression_in_assignment169);
                e = expression();

                state._fsp--;

                match(input, 34, FOLLOW_34_in_assignment171);

                Assignment stmt = new Assignment();
                stmt.setLhs(v);
                stmt.setRhs(e);
                value = stmt;


            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "assignment"

    public static class ifstatement_return extends ParserRuleReturnScope {

        public IfStatement value;
    };

    // $ANTLR start "ifstatement"
    // /home/krawler-user/Expression.g:69:1: ifstatement returns [IfStatement value] : 'if' '(' e= expression ')' '{' s= stmtBlock '}' ;
    public final ExpressionParser.ifstatement_return ifstatement() throws ProcessException {
        ExpressionParser.ifstatement_return retval = new ExpressionParser.ifstatement_return();
        retval.start = input.LT(1);

        Expression e = null;

        List<Statement> s = null;


        try {
            // /home/krawler-user/Expression.g:70:5: ( 'if' '(' e= expression ')' '{' s= stmtBlock '}' )
            // /home/krawler-user/Expression.g:70:9: 'if' '(' e= expression ')' '{' s= stmtBlock '}'
            {
                match(input, 35, FOLLOW_35_in_ifstatement197);
                match(input, LPAREN, FOLLOW_LPAREN_in_ifstatement199);
                pushFollow(FOLLOW_expression_in_ifstatement203);
                e = expression();

                state._fsp--;

                match(input, RPAREN, FOLLOW_RPAREN_in_ifstatement205);
                match(input, 36, FOLLOW_36_in_ifstatement207);
                pushFollow(FOLLOW_stmtBlock_in_ifstatement211);
                s = stmtBlock();

                state._fsp--;

                match(input, 37, FOLLOW_37_in_ifstatement213);

                IfStatement stmt = new IfStatement();
                Expression cond = e;
                if (cond instanceof ConditionalExpression==false) {
                    throw new ProcessException("if statement contains wrong condition :" + input.toString(retval.start, input.LT(-1)));
                }
                stmt.setCondition((ConditionalExpression) cond);
                stmt.setBlock(s);
                retval.value = stmt;


            }

            retval.stop = input.LT(-1);

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return retval;
    }
    // $ANTLR end "ifstatement"

    // $ANTLR start "expression"
    // /home/krawler-user/Expression.g:81:1: expression returns [Expression value] : logicalOrExpression ;
    public final Expression expression() throws ProcessException {
        Expression value = null;

        ExpressionParser.logicalOrExpression_return logicalOrExpression5 = null;


        try {
            // /home/krawler-user/Expression.g:82:2: ( logicalOrExpression )
            // /home/krawler-user/Expression.g:82:5: logicalOrExpression
            {
                pushFollow(FOLLOW_logicalOrExpression_in_expression234);
                logicalOrExpression5 = logicalOrExpression();

                state._fsp--;

                value = (logicalOrExpression5 != null ? logicalOrExpression5.value : null);

            }

        } finally {
        }
        return value;
    }
    // $ANTLR end "expression"

    public static class logicalOrExpression_return extends ParserRuleReturnScope {

        public Expression value;
    };

    // $ANTLR start "logicalOrExpression"
    // /home/krawler-user/Expression.g:85:1: logicalOrExpression returns [Expression value] : e= logicalAndExpression ( '||' op= logicalAndExpression )* ;
    public final ExpressionParser.logicalOrExpression_return logicalOrExpression() throws ProcessException {
        ExpressionParser.logicalOrExpression_return retval = new ExpressionParser.logicalOrExpression_return();
        retval.start = input.LT(1);

        ExpressionParser.logicalAndExpression_return e = null;

        ExpressionParser.logicalAndExpression_return op = null;


        try {
            // /home/krawler-user/Expression.g:86:5: (e= logicalAndExpression ( '||' op= logicalAndExpression )* )
            // /home/krawler-user/Expression.g:86:9: e= logicalAndExpression ( '||' op= logicalAndExpression )*
            {
                pushFollow(FOLLOW_logicalAndExpression_in_logicalOrExpression257);
                e = logicalAndExpression();

                state._fsp--;

                retval.value = (e != null ? e.value : null);
                // /home/krawler-user/Expression.g:87:9: ( '||' op= logicalAndExpression )*
                loop3:
                do {
                    int alt3 = 2;
                    int LA3_0 = input.LA(1);

                    if ((LA3_0 == 38)) {
                        alt3 = 1;
                    }


                    switch (alt3) {
                        case 1: // /home/krawler-user/Expression.g:87:10: '||' op= logicalAndExpression
                        {
                            match(input, 38, FOLLOW_38_in_logicalOrExpression270);
                            pushFollow(FOLLOW_logicalAndExpression_in_logicalOrExpression274);
                            op = logicalAndExpression();

                            state._fsp--;


                            LogicalOrExpression exp = new LogicalOrExpression();
                            if (retval.value.getValueType() != Expression.VALUE_TYPE.BOOLEAN || (op != null ? op.value : null).getValueType() != Expression.VALUE_TYPE.BOOLEAN) {
                                throw new ProcessException("Wrong logical expression :" + input.toString(retval.start, input.LT(-1)));
                            }
                            exp.setOperand(retval.value);
                            exp.setOtherOperand((op != null ? op.value : null));
                            retval.value = exp;


                        }
                        break;

                        default:
                            break loop3;
                    }
                } while (true);


            }

            retval.stop = input.LT(-1);

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return retval;
    }
    // $ANTLR end "logicalOrExpression"

    public static class logicalAndExpression_return extends ParserRuleReturnScope {

        public Expression value;
    };

    // $ANTLR start "logicalAndExpression"
    // /home/krawler-user/Expression.g:98:1: logicalAndExpression returns [Expression value] : e= equalityExpression ( '&&' op= equalityExpression )* ;
    public final ExpressionParser.logicalAndExpression_return logicalAndExpression() throws ProcessException {
        ExpressionParser.logicalAndExpression_return retval = new ExpressionParser.logicalAndExpression_return();
        retval.start = input.LT(1);

        Expression e = null;

        Expression op = null;


        try {
            // /home/krawler-user/Expression.g:99:5: (e= equalityExpression ( '&&' op= equalityExpression )* )
            // /home/krawler-user/Expression.g:99:9: e= equalityExpression ( '&&' op= equalityExpression )*
            {
                pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression312);
                e = equalityExpression();

                state._fsp--;

                retval.value = e;
                // /home/krawler-user/Expression.g:100:9: ( '&&' op= equalityExpression )*
                loop4:
                do {
                    int alt4 = 2;
                    int LA4_0 = input.LA(1);

                    if ((LA4_0 == 39)) {
                        alt4 = 1;
                    }


                    switch (alt4) {
                        case 1: // /home/krawler-user/Expression.g:100:10: '&&' op= equalityExpression
                        {
                            match(input, 39, FOLLOW_39_in_logicalAndExpression325);
                            pushFollow(FOLLOW_equalityExpression_in_logicalAndExpression329);
                            op = equalityExpression();

                            state._fsp--;


                            LogicalAndExpression exp = new LogicalAndExpression();
                            if (retval.value.getValueType() != Expression.VALUE_TYPE.BOOLEAN || op.getValueType() != Expression.VALUE_TYPE.BOOLEAN) {
                                throw new ProcessException("Wrong logical expression :" + input.toString(retval.start, input.LT(-1)));
                            }
                            exp.setOperand(retval.value);
                            exp.setOtherOperand(op);
                            retval.value = exp;


                        }
                        break;

                        default:
                            break loop4;
                    }
                } while (true);


            }

            retval.stop = input.LT(-1);

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return retval;
    }
    // $ANTLR end "logicalAndExpression"

    // $ANTLR start "equalityExpression"
    // /home/krawler-user/Expression.g:111:1: equalityExpression returns [Expression value] : e= relationalExpression ( ( '==' | '!=' ) op= relationalExpression )* ;
    public final Expression equalityExpression() throws ProcessException {
        Expression value = null;

        Expression e = null;

        Expression op = null;


        try {
            // /home/krawler-user/Expression.g:112:5: (e= relationalExpression ( ( '==' | '!=' ) op= relationalExpression )* )
            // /home/krawler-user/Expression.g:112:9: e= relationalExpression ( ( '==' | '!=' ) op= relationalExpression )*
            {
                pushFollow(FOLLOW_relationalExpression_in_equalityExpression371);
                e = relationalExpression();

                state._fsp--;

                value = e;
                // /home/krawler-user/Expression.g:113:9: ( ( '==' | '!=' ) op= relationalExpression )*
                loop6:
                do {
                    int alt6 = 2;
                    int LA6_0 = input.LA(1);

                    if (((LA6_0 >= 40 && LA6_0 <= 41))) {
                        alt6 = 1;
                    }


                    switch (alt6) {
                        case 1: // /home/krawler-user/Expression.g:113:13: ( '==' | '!=' ) op= relationalExpression
                        {
                            BinaryExpression exp = null;
                            // /home/krawler-user/Expression.g:114:13: ( '==' | '!=' )
                            int alt5 = 2;
                            int LA5_0 = input.LA(1);

                            if ((LA5_0 == 40)) {
                                alt5 = 1;
                            } else if ((LA5_0 == 41)) {
                                alt5 = 2;
                            } else {
                                NoViableAltException nvae =
                                        new NoViableAltException("", 5, 0, input);

                                throw nvae;
                            }
                            switch (alt5) {
                                case 1: // /home/krawler-user/Expression.g:114:17: '=='
                                {
                                    match(input, 40, FOLLOW_40_in_equalityExpression405);
                                    exp = new EQExpression();

                                }
                                break;
                                case 2: // /home/krawler-user/Expression.g:115:17: '!='
                                {
                                    match(input, 41, FOLLOW_41_in_equalityExpression425);
                                    exp = new NQExpression();

                                }
                                break;

                            }

                            pushFollow(FOLLOW_relationalExpression_in_equalityExpression457);
                            op = relationalExpression();

                            state._fsp--;


                            exp.setOperand(value);
                            exp.setOtherOperand(op);
                            value = exp;


                        }
                        break;

                        default:
                            break loop6;
                    }
                } while (true);


            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "equalityExpression"

    // $ANTLR start "relationalExpression"
    // /home/krawler-user/Expression.g:125:1: relationalExpression returns [Expression value] : e= additiveExpression ( ( '<=' | '>=' | '<' | '>' ) op= additiveExpression )* ;
    public final Expression relationalExpression() throws ProcessException {
        Expression value = null;

        Expression e = null;

        Expression op = null;


        try {
            // /home/krawler-user/Expression.g:126:5: (e= additiveExpression ( ( '<=' | '>=' | '<' | '>' ) op= additiveExpression )* )
            // /home/krawler-user/Expression.g:126:9: e= additiveExpression ( ( '<=' | '>=' | '<' | '>' ) op= additiveExpression )*
            {
                pushFollow(FOLLOW_additiveExpression_in_relationalExpression495);
                e = additiveExpression();

                state._fsp--;

                value = e;
                // /home/krawler-user/Expression.g:127:9: ( ( '<=' | '>=' | '<' | '>' ) op= additiveExpression )*
                loop8:
                do {
                    int alt8 = 2;
                    int LA8_0 = input.LA(1);

                    if (((LA8_0 >= 42 && LA8_0 <= 45))) {
                        alt8 = 1;
                    }


                    switch (alt8) {
                        case 1: // /home/krawler-user/Expression.g:127:11: ( '<=' | '>=' | '<' | '>' ) op= additiveExpression
                        {
                            BinaryExpression exp = null;
                            // /home/krawler-user/Expression.g:128:10: ( '<=' | '>=' | '<' | '>' )
                            int alt7 = 4;
                            switch (input.LA(1)) {
                                case 42: {
                                    alt7 = 1;
                                }
                                break;
                                case 43: {
                                    alt7 = 2;
                                }
                                break;
                                case 44: {
                                    alt7 = 3;
                                }
                                break;
                                case 45: {
                                    alt7 = 4;
                                }
                                break;
                                default:
                                    NoViableAltException nvae =
                                            new NoViableAltException("", 7, 0, input);

                                    throw nvae;
                            }

                            switch (alt7) {
                                case 1: // /home/krawler-user/Expression.g:128:12: '<='
                                {
                                    match(input, 42, FOLLOW_42_in_relationalExpression522);
                                    exp = new LEExpression();

                                }
                                break;
                                case 2: // /home/krawler-user/Expression.g:129:11: '>='
                                {
                                    match(input, 43, FOLLOW_43_in_relationalExpression536);
                                    exp = new GEExpression();

                                }
                                break;
                                case 3: // /home/krawler-user/Expression.g:130:11: '<'
                                {
                                    match(input, 44, FOLLOW_44_in_relationalExpression550);
                                    exp = new LTExpression();

                                }
                                break;
                                case 4: // /home/krawler-user/Expression.g:131:11: '>'
                                {
                                    match(input, 45, FOLLOW_45_in_relationalExpression565);
                                    exp = new GTExpression();

                                }
                                break;

                            }

                            pushFollow(FOLLOW_additiveExpression_in_relationalExpression580);
                            op = additiveExpression();

                            state._fsp--;


                            exp.setOperand(value);
                            exp.setOtherOperand(op);
                            value = exp;


                        }
                        break;

                        default:
                            break loop8;
                    }
                } while (true);


            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "relationalExpression"

    // $ANTLR start "additiveExpression"
    // /home/krawler-user/Expression.g:140:1: additiveExpression returns [Expression value] : e= multiplicativeExpression ( ( '+' | '-' ) f= multiplicativeExpression )* ;
    public final Expression additiveExpression() throws ProcessException {
        Expression value = null;

        Expression e = null;

        Expression f = null;


        try {
            // /home/krawler-user/Expression.g:141:5: (e= multiplicativeExpression ( ( '+' | '-' ) f= multiplicativeExpression )* )
            // /home/krawler-user/Expression.g:141:9: e= multiplicativeExpression ( ( '+' | '-' ) f= multiplicativeExpression )*
            {
                pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression618);
                e = multiplicativeExpression();

                state._fsp--;

                value = e;
                // /home/krawler-user/Expression.g:142:9: ( ( '+' | '-' ) f= multiplicativeExpression )*
                loop10:
                do {
                    int alt10 = 2;
                    int LA10_0 = input.LA(1);

                    if (((LA10_0 >= PLUS && LA10_0 <= MINUS))) {
                        alt10 = 1;
                    }


                    switch (alt10) {
                        case 1: // /home/krawler-user/Expression.g:142:13: ( '+' | '-' ) f= multiplicativeExpression
                        {
                            BinaryExpression exp = null;
                            // /home/krawler-user/Expression.g:143:13: ( '+' | '-' )
                            int alt9 = 2;
                            int LA9_0 = input.LA(1);

                            if ((LA9_0 == PLUS)) {
                                alt9 = 1;
                            } else if ((LA9_0 == MINUS)) {
                                alt9 = 2;
                            } else {
                                NoViableAltException nvae =
                                        new NoViableAltException("", 9, 0, input);

                                throw nvae;
                            }
                            switch (alt9) {
                                case 1: // /home/krawler-user/Expression.g:143:17: '+'
                                {
                                    match(input, PLUS, FOLLOW_PLUS_in_additiveExpression652);
                                    exp = new AddExpression();

                                }
                                break;
                                case 2: // /home/krawler-user/Expression.g:144:17: '-'
                                {
                                    match(input, MINUS, FOLLOW_MINUS_in_additiveExpression672);
                                    exp = new SubtractExpression();

                                }
                                break;

                            }

                            pushFollow(FOLLOW_multiplicativeExpression_in_additiveExpression692);
                            f = multiplicativeExpression();

                            state._fsp--;


                            exp.setOperand(value);
                            exp.setOtherOperand(f);
                            value = exp;


                        }
                        break;

                        default:
                            break loop10;
                    }
                } while (true);


            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "additiveExpression"

    // $ANTLR start "multiplicativeExpression"
    // /home/krawler-user/Expression.g:153:1: multiplicativeExpression returns [Expression value] : e= unaryExpression ( ( '*' | '/' | '%' ) f= unaryExpression )* ;
    public final Expression multiplicativeExpression() throws ProcessException {
        Expression value = null;

        ExpressionParser.unaryExpression_return e = null;

        ExpressionParser.unaryExpression_return f = null;


        try {
            // /home/krawler-user/Expression.g:154:5: (e= unaryExpression ( ( '*' | '/' | '%' ) f= unaryExpression )* )
            // /home/krawler-user/Expression.g:155:6: e= unaryExpression ( ( '*' | '/' | '%' ) f= unaryExpression )*
            {
                pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression739);
                e = unaryExpression();

                state._fsp--;

                value = (e != null ? e.value : null);
                // /home/krawler-user/Expression.g:156:6: ( ( '*' | '/' | '%' ) f= unaryExpression )*
                loop12:
                do {
                    int alt12 = 2;
                    int LA12_0 = input.LA(1);

                    if (((LA12_0 >= MULTI && LA12_0 <= MOD))) {
                        alt12 = 1;
                    }


                    switch (alt12) {
                        case 1: // /home/krawler-user/Expression.g:156:10: ( '*' | '/' | '%' ) f= unaryExpression
                        {
                            BinaryExpression exp = null;
                            // /home/krawler-user/Expression.g:157:13: ( '*' | '/' | '%' )
                            int alt11 = 3;
                            switch (input.LA(1)) {
                                case MULTI: {
                                    alt11 = 1;
                                }
                                break;
                                case DIV: {
                                    alt11 = 2;
                                }
                                break;
                                case MOD: {
                                    alt11 = 3;
                                }
                                break;
                                default:
                                    NoViableAltException nvae =
                                            new NoViableAltException("", 11, 0, input);

                                    throw nvae;
                            }

                            switch (alt11) {
                                case 1: // /home/krawler-user/Expression.g:157:15: '*'
                                {
                                    match(input, MULTI, FOLLOW_MULTI_in_multiplicativeExpression769);
                                    exp = new MultiplyExpression();

                                }
                                break;
                                case 2: // /home/krawler-user/Expression.g:158:17: '/'
                                {
                                    match(input, DIV, FOLLOW_DIV_in_multiplicativeExpression789);
                                    exp = new DivideExpression();

                                }
                                break;
                                case 3: // /home/krawler-user/Expression.g:159:17: '%'
                                {
                                    match(input, MOD, FOLLOW_MOD_in_multiplicativeExpression809);
                                    exp = new ModuloExpression();

                                }
                                break;

                            }

                            pushFollow(FOLLOW_unaryExpression_in_multiplicativeExpression829);
                            f = unaryExpression();

                            state._fsp--;


                            exp.setOperand(value);
                            exp.setOtherOperand((f != null ? f.value : null));
                            value = exp;


                        }
                        break;

                        default:
                            break loop12;
                    }
                } while (true);


            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "multiplicativeExpression"

    public static class unaryExpression_return extends ParserRuleReturnScope {

        public Expression value;
    };

    // $ANTLR start "unaryExpression"
    // /home/krawler-user/Expression.g:168:1: unaryExpression returns [Expression value] : ( ( '-' op= unaryExpression ) | ( '!' op= unaryExpression ) | primary );
    public final ExpressionParser.unaryExpression_return unaryExpression() throws ProcessException {
        ExpressionParser.unaryExpression_return retval = new ExpressionParser.unaryExpression_return();
        retval.start = input.LT(1);

        ExpressionParser.unaryExpression_return op = null;

        Expression primary6 = null;


        try {
            // /home/krawler-user/Expression.g:169:5: ( ( '-' op= unaryExpression ) | ( '!' op= unaryExpression ) | primary )
            int alt13 = 3;
            switch (input.LA(1)) {
                case MINUS: {
                    alt13 = 1;
                }
                break;
                case 46: {
                    alt13 = 2;
                }
                break;
                case IDENTIFIER:
                case INTLITERAL:
                case LONGLITERAL:
                case FLOATLITERAL:
                case DOUBLELITERAL:
                case CHARLITERAL:
                case STRINGLITERAL:
                case TRUE:
                case FALSE:
                case NULL:
                case LPAREN: {
                    alt13 = 3;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 13, 0, input);

                    throw nvae;
            }

            switch (alt13) {
                case 1: // /home/krawler-user/Expression.g:169:8: ( '-' op= unaryExpression )
                {
                    // /home/krawler-user/Expression.g:169:8: ( '-' op= unaryExpression )
                    // /home/krawler-user/Expression.g:169:10: '-' op= unaryExpression
                    {
                        match(input, MINUS, FOLLOW_MINUS_in_unaryExpression870);
                        pushFollow(FOLLOW_unaryExpression_in_unaryExpression875);
                        op = unaryExpression();

                        state._fsp--;


                    }


                    NegateExpression e = new NegateExpression();
                    if ((op != null ? op.value : null).getValueType() != Expression.VALUE_TYPE.NUMBER) {
                        throw new ProcessException("Wrong negation expression :" + input.toString(retval.start, input.LT(-1)));
                    }
                    e.setOperand((op != null ? op.value : null));
                    retval.value = e;


                }
                break;
                case 2: // /home/krawler-user/Expression.g:176:9: ( '!' op= unaryExpression )
                {
                    // /home/krawler-user/Expression.g:176:9: ( '!' op= unaryExpression )
                    // /home/krawler-user/Expression.g:176:10: '!' op= unaryExpression
                    {
                        match(input, 46, FOLLOW_46_in_unaryExpression889);
                        pushFollow(FOLLOW_unaryExpression_in_unaryExpression893);
                        op = unaryExpression();

                        state._fsp--;


                    }


                    LogicalNotExpression e = new LogicalNotExpression();
                    if ((op != null ? op.value : null).getValueType() != Expression.VALUE_TYPE.BOOLEAN) {
                        throw new ProcessException("Wrong logical expression :" + input.toString(retval.start, input.LT(-1)));
                    }
                    e.setOperand((op != null ? op.value : null));
                    retval.value = e;


                }
                break;
                case 3: // /home/krawler-user/Expression.g:183:9: primary
                {
                    pushFollow(FOLLOW_primary_in_unaryExpression906);
                    primary6 = primary();

                    state._fsp--;

                    retval.value = primary6;

                }
                break;

            }
            retval.stop = input.LT(-1);

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return retval;
    }
    // $ANTLR end "unaryExpression"

    // $ANTLR start "primary"
    // /home/krawler-user/Expression.g:189:1: primary returns [Expression value] : ( parExpression | constant | variable );
    public final Expression primary() throws ProcessException {
        Expression value = null;

        Expression parExpression7 = null;

        Constant constant8 = null;

        Variable variable9 = null;


        try {
            // /home/krawler-user/Expression.g:190:2: ( parExpression | constant | variable )
            int alt14 = 3;
            switch (input.LA(1)) {
                case LPAREN: {
                    alt14 = 1;
                }
                break;
                case INTLITERAL:
                case LONGLITERAL:
                case FLOATLITERAL:
                case DOUBLELITERAL:
                case CHARLITERAL:
                case STRINGLITERAL:
                case TRUE:
                case FALSE:
                case NULL: {
                    alt14 = 2;
                }
                break;
                case IDENTIFIER: {
                    alt14 = 3;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 14, 0, input);

                    throw nvae;
            }

            switch (alt14) {
                case 1: // /home/krawler-user/Expression.g:190:4: parExpression
                {
                    pushFollow(FOLLOW_parExpression_in_primary933);
                    parExpression7 = parExpression();

                    state._fsp--;

                    value = parExpression7;

                }
                break;
                case 2: // /home/krawler-user/Expression.g:191:7: constant
                {
                    pushFollow(FOLLOW_constant_in_primary943);
                    constant8 = constant();

                    state._fsp--;

                    value = constant8;

                }
                break;
                case 3: // /home/krawler-user/Expression.g:192:9: variable
                {
                    pushFollow(FOLLOW_variable_in_primary955);
                    variable9 = variable();

                    state._fsp--;

                    value = variable9;

                }
                break;

            }
        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "primary"

    // $ANTLR start "parExpression"
    // /home/krawler-user/Expression.g:195:1: parExpression returns [Expression value] : '(' e= expression ')' ;
    public final Expression parExpression() throws ProcessException {
        Expression value = null;

        Expression e = null;


        try {
            // /home/krawler-user/Expression.g:196:5: ( '(' e= expression ')' )
            // /home/krawler-user/Expression.g:196:9: '(' e= expression ')'
            {
                match(input, LPAREN, FOLLOW_LPAREN_in_parExpression979);
                pushFollow(FOLLOW_expression_in_parExpression983);
                e = expression();

                state._fsp--;

                match(input, RPAREN, FOLLOW_RPAREN_in_parExpression985);
                value = e;

            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "parExpression"

    // $ANTLR start "variable"
    // /home/krawler-user/Expression.g:199:1: variable returns [Variable value] : ( IDENTIFIER ':' )? p= part[hm, i] ( '.' p= part[hm, ++i] )* ;
    public final Variable variable() throws ProcessException {
        Variable value = null;

        Token IDENTIFIER10 = null;
        String p = null;


        try {
            // /home/krawler-user/Expression.g:200:5: ( ( IDENTIFIER ':' )? p= part[hm, i] ( '.' p= part[hm, ++i] )* )
            // /home/krawler-user/Expression.g:201:6: ( IDENTIFIER ':' )? p= part[hm, i] ( '.' p= part[hm, ++i] )*
            {

                Scope scp = scopes.get(null);
                Map<Integer, List<Expression>> hm = new HashMap<Integer, List<Expression>>();
                int i = 0;

                // /home/krawler-user/Expression.g:206:6: ( IDENTIFIER ':' )?
                int alt15 = 2;
                int LA15_0 = input.LA(1);

                if ((LA15_0 == IDENTIFIER)) {
                    int LA15_1 = input.LA(2);

                    if ((LA15_1 == 47)) {
                        alt15 = 1;
                    }
                }
                switch (alt15) {
                    case 1: // /home/krawler-user/Expression.g:206:8: IDENTIFIER ':'
                    {
                        IDENTIFIER10 = (Token) match(input, IDENTIFIER, FOLLOW_IDENTIFIER_in_variable1027);
                        match(input, 47, FOLLOW_47_in_variable1029);

                        String key = (IDENTIFIER10 != null ? IDENTIFIER10.getText() : null);
                        scp = scopes.get(key);
                        if (scp == null) {
                            throw new ProcessException("Scope not available [" + key + ":<var>]");
                        }


                    }
                    break;

                }

                pushFollow(FOLLOW_part_in_variable1048);
                p = part(hm, i);

                state._fsp--;


                String name = p;
                ArrayList<String> props = new ArrayList<String>();

                // /home/krawler-user/Expression.g:217:9: ( '.' p= part[hm, ++i] )*
                loop16:
                do {
                    int alt16 = 2;
                    int LA16_0 = input.LA(1);

                    if ((LA16_0 == 48)) {
                        alt16 = 1;
                    }


                    switch (alt16) {
                        case 1: // /home/krawler-user/Expression.g:217:10: '.' p= part[hm, ++i]
                        {
                            match(input, 48, FOLLOW_48_in_variable1062);
                            pushFollow(FOLLOW_part_in_variable1066);
                            p = part(hm, ++i);

                            state._fsp--;

                            props.add(p);

                        }
                        break;

                        default:
                            break loop16;
                    }
                } while (true);


                Variable var = new Variable(scp, name);
                String module = scp.getScopeModuleName(name);
                if (!props.isEmpty()) {
                    String[] arr = props.toArray(new String[]{});
                    var.setPathProperties(arr);
                    module = moduleBag.getModuleName(module, arr);
                }
                for (ModuleBag.PRIMITIVE prim : ModuleBag.PRIMITIVE.values()) {
                    if (prim.tagName().equals(module)) {
                        var.setValueType(prim.valueType());
                        break;
                    }
                }
                if (!hm.isEmpty()) {
                    var.setIndices(hm);
                }
                value = var;


            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "variable"

    // $ANTLR start "part"
    // /home/krawler-user/Expression.g:240:1: fragment part[Map<Integer, List<Expression>> hm, int index] returns [String value] : IDENTIFIER (s= identifierSuffix )? ;
    public final String part(Map<Integer, List<Expression>> hm, int index) throws ProcessException {
        String value = null;

        Token IDENTIFIER11 = null;
        List<Expression> s = null;


        try {
            // /home/krawler-user/Expression.g:242:2: ( IDENTIFIER (s= identifierSuffix )? )
            // /home/krawler-user/Expression.g:242:4: IDENTIFIER (s= identifierSuffix )?
            {
                IDENTIFIER11 = (Token) match(input, IDENTIFIER, FOLLOW_IDENTIFIER_in_part1119);
                value = (IDENTIFIER11 != null ? IDENTIFIER11.getText() : null);
                // /home/krawler-user/Expression.g:243:3: (s= identifierSuffix )?
                int alt17 = 2;
                int LA17_0 = input.LA(1);

                if ((LA17_0 == 49)) {
                    alt17 = 1;
                }
                switch (alt17) {
                    case 1: // /home/krawler-user/Expression.g:243:4: s= identifierSuffix
                    {
                        pushFollow(FOLLOW_identifierSuffix_in_part1128);
                        s = identifierSuffix();

                        state._fsp--;

                        hm.put(index, s);

                    }
                    break;

                }


            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "part"

    // $ANTLR start "identifierSuffix"
    // /home/krawler-user/Expression.g:246:1: fragment identifierSuffix returns [List<Expression> value] : ( '[' e= expression ']' )+ ;
    public final List<Expression> identifierSuffix() throws ProcessException {
        List<Expression> value = null;

        Expression e = null;


        try {
            // /home/krawler-user/Expression.g:248:2: ( ( '[' e= expression ']' )+ )
            // /home/krawler-user/Expression.g:248:6: ( '[' e= expression ']' )+
            {
                List<Expression> list = new ArrayList();
                // /home/krawler-user/Expression.g:249:3: ( '[' e= expression ']' )+
                int cnt18 = 0;
                loop18:
                do {
                    int alt18 = 2;
                    int LA18_0 = input.LA(1);

                    if ((LA18_0 == 49)) {
                        alt18 = 1;
                    }


                    switch (alt18) {
                        case 1: // /home/krawler-user/Expression.g:249:4: '[' e= expression ']'
                        {
                            match(input, 49, FOLLOW_49_in_identifierSuffix1156);
                            pushFollow(FOLLOW_expression_in_identifierSuffix1160);
                            e = expression();

                            state._fsp--;

                            match(input, 50, FOLLOW_50_in_identifierSuffix1162);
                            list.add(e);

                        }
                        break;

                        default:
                            if (cnt18 >= 1) {
                                break loop18;
                            }
                            EarlyExitException eee =
                                    new EarlyExitException(18, input);
                            throw eee;
                    }
                    cnt18++;
                } while (true);

                value = list;

            }

        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "identifierSuffix"

    // $ANTLR start "constant"
    // /home/krawler-user/Expression.g:253:1: constant returns [Constant value] : ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL );
    public final Constant constant() throws ProcessException {
        Constant value = null;

        Token INTLITERAL12 = null;
        Token LONGLITERAL13 = null;
        Token FLOATLITERAL14 = null;
        Token DOUBLELITERAL15 = null;
        Token CHARLITERAL16 = null;
        Token STRINGLITERAL17 = null;

        try {
            // /home/krawler-user/Expression.g:254:5: ( INTLITERAL | LONGLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | TRUE | FALSE | NULL )
            int alt19 = 9;
            switch (input.LA(1)) {
                case INTLITERAL: {
                    alt19 = 1;
                }
                break;
                case LONGLITERAL: {
                    alt19 = 2;
                }
                break;
                case FLOATLITERAL: {
                    alt19 = 3;
                }
                break;
                case DOUBLELITERAL: {
                    alt19 = 4;
                }
                break;
                case CHARLITERAL: {
                    alt19 = 5;
                }
                break;
                case STRINGLITERAL: {
                    alt19 = 6;
                }
                break;
                case TRUE: {
                    alt19 = 7;
                }
                break;
                case FALSE: {
                    alt19 = 8;
                }
                break;
                case NULL: {
                    alt19 = 9;
                }
                break;
                default:
                    NoViableAltException nvae =
                            new NoViableAltException("", 19, 0, input);

                    throw nvae;
            }

            switch (alt19) {
                case 1: // /home/krawler-user/Expression.g:254:9: INTLITERAL
                {
                    INTLITERAL12 = (Token) match(input, INTLITERAL, FOLLOW_INTLITERAL_in_constant1199);

                    Constant c = new Constant();
                    String str = (INTLITERAL12 != null ? INTLITERAL12.getText() : null);
                    try {
                        c.setValue(Integer.parseInt(str));
                    } catch (NumberFormatException ex) {
                        c.setValue(Long.parseLong(str));
                    }
                    value = c;


                }
                break;
                case 2: // /home/krawler-user/Expression.g:264:9: LONGLITERAL
                {
                    LONGLITERAL13 = (Token) match(input, LONGLITERAL, FOLLOW_LONGLITERAL_in_constant1211);

                    Constant c = new Constant();
                    String str = (LONGLITERAL13 != null ? LONGLITERAL13.getText() : null);
                    c.setValue(Long.parseLong(str));
                    value = c;


                }
                break;
                case 3: // /home/krawler-user/Expression.g:270:9: FLOATLITERAL
                {
                    FLOATLITERAL14 = (Token) match(input, FLOATLITERAL, FOLLOW_FLOATLITERAL_in_constant1223);

                    Constant c = new Constant();
                    String str = (FLOATLITERAL14 != null ? FLOATLITERAL14.getText() : null);
                    c.setValue(Float.parseFloat(str));
                    value = c;


                }
                break;
                case 4: // /home/krawler-user/Expression.g:276:9: DOUBLELITERAL
                {
                    DOUBLELITERAL15 = (Token) match(input, DOUBLELITERAL, FOLLOW_DOUBLELITERAL_in_constant1235);

                    Constant c = new Constant();
                    String str = (DOUBLELITERAL15 != null ? DOUBLELITERAL15.getText() : null);
                    c.setValue(Double.parseDouble(str));
                    value = c;


                }
                break;
                case 5: // /home/krawler-user/Expression.g:282:9: CHARLITERAL
                {
                    CHARLITERAL16 = (Token) match(input, CHARLITERAL, FOLLOW_CHARLITERAL_in_constant1247);

                    Constant c = new Constant();
                    String str = (CHARLITERAL16 != null ? CHARLITERAL16.getText() : null);
                    c.setValue(str.charAt(0));
                    value = c;


                }
                break;
                case 6: // /home/krawler-user/Expression.g:288:9: STRINGLITERAL
                {
                    STRINGLITERAL17 = (Token) match(input, STRINGLITERAL, FOLLOW_STRINGLITERAL_in_constant1259);

                    Constant c = new Constant();
                    String str = (STRINGLITERAL17 != null ? STRINGLITERAL17.getText() : null);
                    c.setValue(str);
                    value = c;


                }
                break;
                case 7: // /home/krawler-user/Expression.g:294:9: TRUE
                {
                    match(input, TRUE, FOLLOW_TRUE_in_constant1271);

                    Constant c = new Constant();
                    c.setValue(true);
                    value = c;


                }
                break;
                case 8: // /home/krawler-user/Expression.g:299:9: FALSE
                {
                    match(input, FALSE, FOLLOW_FALSE_in_constant1283);

                    Constant c = new Constant();
                    c.setValue(false);
                    value = c;


                }
                break;
                case 9: // /home/krawler-user/Expression.g:304:9: NULL
                {
                    match(input, NULL, FOLLOW_NULL_in_constant1295);

                    Constant c = new Constant();
                    value = c;


                }
                break;

            }
        } catch (RecognitionException ex) {
            throw new ProcessException(ex.getMessage(), ex);
        } finally {
        }
        return value;
    }
    // $ANTLR end "constant"
    // Delegated rules
    public static final BitSet FOLLOW_stmtBlock_in_parseBlock42 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_parseBlock48 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expression_in_parseExpression65 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_parseExpression72 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_statement_in_stmtBlock95 = new BitSet(new long[]{0x0000000800000012L});
    public static final BitSet FOLLOW_assignment_in_statement123 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ifstatement_in_statement133 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_assignment164 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_33_in_assignment165 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_expression_in_assignment169 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_34_in_assignment171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_35_in_ifstatement197 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAREN_in_ifstatement199 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_expression_in_ifstatement203 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_ifstatement205 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_36_in_ifstatement207 = new BitSet(new long[]{0x0000000800000010L});
    public static final BitSet FOLLOW_stmtBlock_in_ifstatement211 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_37_in_ifstatement213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logicalOrExpression_in_expression234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_logicalAndExpression_in_logicalOrExpression257 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_38_in_logicalOrExpression270 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_logicalAndExpression_in_logicalOrExpression274 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_equalityExpression_in_logicalAndExpression312 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_39_in_logicalAndExpression325 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_equalityExpression_in_logicalAndExpression329 = new BitSet(new long[]{0x0000008000000002L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression371 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_40_in_equalityExpression405 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_41_in_equalityExpression425 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_relationalExpression_in_equalityExpression457 = new BitSet(new long[]{0x0000030000000002L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression495 = new BitSet(new long[]{0x00003C0000000002L});
    public static final BitSet FOLLOW_42_in_relationalExpression522 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_43_in_relationalExpression536 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_44_in_relationalExpression550 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_45_in_relationalExpression565 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_additiveExpression_in_relationalExpression580 = new BitSet(new long[]{0x00003C0000000002L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression618 = new BitSet(new long[]{0x0000000030000002L});
    public static final BitSet FOLLOW_PLUS_in_additiveExpression652 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_MINUS_in_additiveExpression672 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_multiplicativeExpression_in_additiveExpression692 = new BitSet(new long[]{0x0000000030000002L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression739 = new BitSet(new long[]{0x00000001C0000002L});
    public static final BitSet FOLLOW_MULTI_in_multiplicativeExpression769 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_DIV_in_multiplicativeExpression789 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_MOD_in_multiplicativeExpression809 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_multiplicativeExpression829 = new BitSet(new long[]{0x00000001C0000002L});
    public static final BitSet FOLLOW_MINUS_in_unaryExpression870 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression875 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_46_in_unaryExpression889 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_unaryExpression_in_unaryExpression893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_primary_in_unaryExpression906 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_parExpression_in_primary933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_constant_in_primary943 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_variable_in_primary955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAREN_in_parExpression979 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_expression_in_parExpression983 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAREN_in_parExpression985 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_variable1027 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_47_in_variable1029 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_part_in_variable1048 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_48_in_variable1062 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_part_in_variable1066 = new BitSet(new long[]{0x0001000000000002L});
    public static final BitSet FOLLOW_IDENTIFIER_in_part1119 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_identifierSuffix_in_part1128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_49_in_identifierSuffix1156 = new BitSet(new long[]{0x0000400024003FF0L});
    public static final BitSet FOLLOW_expression_in_identifierSuffix1160 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_50_in_identifierSuffix1162 = new BitSet(new long[]{0x0002000000000002L});
    public static final BitSet FOLLOW_INTLITERAL_in_constant1199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LONGLITERAL_in_constant1211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FLOATLITERAL_in_constant1223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLELITERAL_in_constant1235 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHARLITERAL_in_constant1247 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRINGLITERAL_in_constant1259 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_constant1271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_constant1283 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_constant1295 = new BitSet(new long[]{0x0000000000000002L});
}
