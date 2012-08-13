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

import org.antlr.runtime.*;

public class ExpressionLexer extends Lexer {

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
    public static final int FloatSuffix = 20;
    public static final int NonIntegerNumber = 19;
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
    public ExpressionLexer() {
    }

    public ExpressionLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }

    public ExpressionLexer(CharStream input, RecognizerSharedState state) {
        super(input, state);

    }

    @Override
    public String getGrammarFileName() {
        return "/home/krawler-user/Expression.g";
    }

    // $ANTLR start "T__33"
    public final void mT__33() throws RecognitionException {
        try {
            int _type = T__33;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:7:7: ( '=' )
            // /home/krawler-user/Expression.g:7:9: '='
            {
                match('=');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__33"

    // $ANTLR start "T__34"
    public final void mT__34() throws RecognitionException {
        try {
            int _type = T__34;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:8:7: ( ';' )
            // /home/krawler-user/Expression.g:8:9: ';'
            {
                match(';');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__34"

    // $ANTLR start "T__35"
    public final void mT__35() throws RecognitionException {
        try {
            int _type = T__35;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:9:7: ( 'if' )
            // /home/krawler-user/Expression.g:9:9: 'if'
            {
                match("if");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__35"

    // $ANTLR start "T__36"
    public final void mT__36() throws RecognitionException {
        try {
            int _type = T__36;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:10:7: ( '{' )
            // /home/krawler-user/Expression.g:10:9: '{'
            {
                match('{');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__36"

    // $ANTLR start "T__37"
    public final void mT__37() throws RecognitionException {
        try {
            int _type = T__37;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:11:7: ( '}' )
            // /home/krawler-user/Expression.g:11:9: '}'
            {
                match('}');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__37"

    // $ANTLR start "T__38"
    public final void mT__38() throws RecognitionException {
        try {
            int _type = T__38;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:12:7: ( '||' )
            // /home/krawler-user/Expression.g:12:9: '||'
            {
                match("||");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__38"

    // $ANTLR start "T__39"
    public final void mT__39() throws RecognitionException {
        try {
            int _type = T__39;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:13:7: ( '&&' )
            // /home/krawler-user/Expression.g:13:9: '&&'
            {
                match("&&");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__39"

    // $ANTLR start "T__40"
    public final void mT__40() throws RecognitionException {
        try {
            int _type = T__40;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:14:7: ( '==' )
            // /home/krawler-user/Expression.g:14:9: '=='
            {
                match("==");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__40"

    // $ANTLR start "T__41"
    public final void mT__41() throws RecognitionException {
        try {
            int _type = T__41;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:15:7: ( '!=' )
            // /home/krawler-user/Expression.g:15:9: '!='
            {
                match("!=");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__41"

    // $ANTLR start "T__42"
    public final void mT__42() throws RecognitionException {
        try {
            int _type = T__42;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:16:7: ( '<=' )
            // /home/krawler-user/Expression.g:16:9: '<='
            {
                match("<=");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__42"

    // $ANTLR start "T__43"
    public final void mT__43() throws RecognitionException {
        try {
            int _type = T__43;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:17:7: ( '>=' )
            // /home/krawler-user/Expression.g:17:9: '>='
            {
                match(">=");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__43"

    // $ANTLR start "T__44"
    public final void mT__44() throws RecognitionException {
        try {
            int _type = T__44;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:18:7: ( '<' )
            // /home/krawler-user/Expression.g:18:9: '<'
            {
                match('<');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__44"

    // $ANTLR start "T__45"
    public final void mT__45() throws RecognitionException {
        try {
            int _type = T__45;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:19:7: ( '>' )
            // /home/krawler-user/Expression.g:19:9: '>'
            {
                match('>');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__45"

    // $ANTLR start "T__46"
    public final void mT__46() throws RecognitionException {
        try {
            int _type = T__46;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:20:7: ( '!' )
            // /home/krawler-user/Expression.g:20:9: '!'
            {
                match('!');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__46"

    // $ANTLR start "T__47"
    public final void mT__47() throws RecognitionException {
        try {
            int _type = T__47;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:21:7: ( ':' )
            // /home/krawler-user/Expression.g:21:9: ':'
            {
                match(':');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__47"

    // $ANTLR start "T__48"
    public final void mT__48() throws RecognitionException {
        try {
            int _type = T__48;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:22:7: ( '.' )
            // /home/krawler-user/Expression.g:22:9: '.'
            {
                match('.');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__48"

    // $ANTLR start "T__49"
    public final void mT__49() throws RecognitionException {
        try {
            int _type = T__49;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:23:7: ( '[' )
            // /home/krawler-user/Expression.g:23:9: '['
            {
                match('[');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__49"

    // $ANTLR start "T__50"
    public final void mT__50() throws RecognitionException {
        try {
            int _type = T__50;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:24:7: ( ']' )
            // /home/krawler-user/Expression.g:24:9: ']'
            {
                match(']');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "T__50"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:314:5: ( 'true' )
            // /home/krawler-user/Expression.g:314:9: 'true'
            {
                match("true");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:318:5: ( 'false' )
            // /home/krawler-user/Expression.g:318:9: 'false'
            {
                match("false");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:322:5: ( 'null' )
            // /home/krawler-user/Expression.g:322:9: 'null'
            {
                match("null");


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "LONGLITERAL"
    public final void mLONGLITERAL() throws RecognitionException {
        try {
            int _type = LONGLITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:326:5: ( IntegerNumber LongSuffix )
            // /home/krawler-user/Expression.g:326:9: IntegerNumber LongSuffix
            {
                mIntegerNumber();
                mLongSuffix();

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "LONGLITERAL"

    // $ANTLR start "INTLITERAL"
    public final void mINTLITERAL() throws RecognitionException {
        try {
            int _type = INTLITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:331:5: ( IntegerNumber )
            // /home/krawler-user/Expression.g:331:9: IntegerNumber
            {
                mIntegerNumber();

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "INTLITERAL"

    // $ANTLR start "IntegerNumber"
    public final void mIntegerNumber() throws RecognitionException {
        try {
            // /home/krawler-user/Expression.g:336:5: ( '0' | '1' .. '9' ( '0' .. '9' )* | '0' ( '0' .. '7' )+ | HexPrefix ( HexDigit )+ )
            int alt4 = 4;
            int LA4_0 = input.LA(1);

            if ((LA4_0 == '0')) {
                switch (input.LA(2)) {
                    case 'X':
                    case 'x': {
                        alt4 = 4;
                    }
                    break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7': {
                        alt4 = 3;
                    }
                    break;
                    default:
                        alt4 = 1;
                }

            } else if (((LA4_0 >= '1' && LA4_0 <= '9'))) {
                alt4 = 2;
            } else {
                NoViableAltException nvae =
                        new NoViableAltException("", 4, 0, input);

                throw nvae;
            }
            switch (alt4) {
                case 1: // /home/krawler-user/Expression.g:336:9: '0'
                {
                    match('0');

                }
                break;
                case 2: // /home/krawler-user/Expression.g:337:9: '1' .. '9' ( '0' .. '9' )*
                {
                    matchRange('1', '9');
                    // /home/krawler-user/Expression.g:337:18: ( '0' .. '9' )*
                    loop1:
                    do {
                        int alt1 = 2;
                        int LA1_0 = input.LA(1);

                        if (((LA1_0 >= '0' && LA1_0 <= '9'))) {
                            alt1 = 1;
                        }


                        switch (alt1) {
                            case 1: // /home/krawler-user/Expression.g:337:19: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                break loop1;
                        }
                    } while (true);


                }
                break;
                case 3: // /home/krawler-user/Expression.g:338:9: '0' ( '0' .. '7' )+
                {
                    match('0');
                    // /home/krawler-user/Expression.g:338:13: ( '0' .. '7' )+
                    int cnt2 = 0;
                    loop2:
                    do {
                        int alt2 = 2;
                        int LA2_0 = input.LA(1);

                        if (((LA2_0 >= '0' && LA2_0 <= '7'))) {
                            alt2 = 1;
                        }


                        switch (alt2) {
                            case 1: // /home/krawler-user/Expression.g:338:14: '0' .. '7'
                            {
                                matchRange('0', '7');

                            }
                            break;

                            default:
                                if (cnt2 >= 1) {
                                    break loop2;
                                }
                                EarlyExitException eee =
                                        new EarlyExitException(2, input);
                                throw eee;
                        }
                        cnt2++;
                    } while (true);


                }
                break;
                case 4: // /home/krawler-user/Expression.g:339:9: HexPrefix ( HexDigit )+
                {
                    mHexPrefix();
                    // /home/krawler-user/Expression.g:339:19: ( HexDigit )+
                    int cnt3 = 0;
                    loop3:
                    do {
                        int alt3 = 2;
                        int LA3_0 = input.LA(1);

                        if (((LA3_0 >= '0' && LA3_0 <= '9') || (LA3_0 >= 'A' && LA3_0 <= 'F') || (LA3_0 >= 'a' && LA3_0 <= 'f'))) {
                            alt3 = 1;
                        }


                        switch (alt3) {
                            case 1: // /home/krawler-user/Expression.g:339:19: HexDigit
                            {
                                mHexDigit();

                            }
                            break;

                            default:
                                if (cnt3 >= 1) {
                                    break loop3;
                                }
                                EarlyExitException eee =
                                        new EarlyExitException(3, input);
                                throw eee;
                        }
                        cnt3++;
                    } while (true);


                }
                break;

            }
        } finally {
        }
    }
    // $ANTLR end "IntegerNumber"

    // $ANTLR start "HexPrefix"
    public final void mHexPrefix() throws RecognitionException {
        try {
            // /home/krawler-user/Expression.g:344:5: ( '0x' | '0X' )
            int alt5 = 2;
            int LA5_0 = input.LA(1);

            if ((LA5_0 == '0')) {
                int LA5_1 = input.LA(2);

                if ((LA5_1 == 'x')) {
                    alt5 = 1;
                } else if ((LA5_1 == 'X')) {
                    alt5 = 2;
                } else {
                    NoViableAltException nvae =
                            new NoViableAltException("", 5, 1, input);

                    throw nvae;
                }
            } else {
                NoViableAltException nvae =
                        new NoViableAltException("", 5, 0, input);

                throw nvae;
            }
            switch (alt5) {
                case 1: // /home/krawler-user/Expression.g:344:9: '0x'
                {
                    match("0x");


                }
                break;
                case 2: // /home/krawler-user/Expression.g:344:16: '0X'
                {
                    match("0X");


                }
                break;

            }
        } finally {
        }
    }
    // $ANTLR end "HexPrefix"

    // $ANTLR start "HexDigit"
    public final void mHexDigit() throws RecognitionException {
        try {
            // /home/krawler-user/Expression.g:349:5: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // /home/krawler-user/Expression.g:349:9: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
                if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'F') || (input.LA(1) >= 'a' && input.LA(1) <= 'f')) {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }


            }

        } finally {
        }
    }
    // $ANTLR end "HexDigit"

    // $ANTLR start "LongSuffix"
    public final void mLongSuffix() throws RecognitionException {
        try {
            // /home/krawler-user/Expression.g:354:5: ( 'l' | 'L' )
            // /home/krawler-user/Expression.g:
            {
                if (input.LA(1) == 'L' || input.LA(1) == 'l') {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }


            }

        } finally {
        }
    }
    // $ANTLR end "LongSuffix"

    // $ANTLR start "NonIntegerNumber"
    public final void mNonIntegerNumber() throws RecognitionException {
        try {
            // /home/krawler-user/Expression.g:360:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | '.' ( '0' .. '9' )+ ( Exponent )? | ( '0' .. '9' )+ Exponent | ( '0' .. '9' )+ | HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            int alt18 = 5;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1: // /home/krawler-user/Expression.g:360:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )?
                {
                    // /home/krawler-user/Expression.g:360:9: ( '0' .. '9' )+
                    int cnt6 = 0;
                    loop6:
                    do {
                        int alt6 = 2;
                        int LA6_0 = input.LA(1);

                        if (((LA6_0 >= '0' && LA6_0 <= '9'))) {
                            alt6 = 1;
                        }


                        switch (alt6) {
                            case 1: // /home/krawler-user/Expression.g:360:10: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                if (cnt6 >= 1) {
                                    break loop6;
                                }
                                EarlyExitException eee =
                                        new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);

                    match('.');
                    // /home/krawler-user/Expression.g:360:27: ( '0' .. '9' )*
                    loop7:
                    do {
                        int alt7 = 2;
                        int LA7_0 = input.LA(1);

                        if (((LA7_0 >= '0' && LA7_0 <= '9'))) {
                            alt7 = 1;
                        }


                        switch (alt7) {
                            case 1: // /home/krawler-user/Expression.g:360:28: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                break loop7;
                        }
                    } while (true);

                    // /home/krawler-user/Expression.g:360:41: ( Exponent )?
                    int alt8 = 2;
                    int LA8_0 = input.LA(1);

                    if ((LA8_0 == 'E' || LA8_0 == 'e')) {
                        alt8 = 1;
                    }
                    switch (alt8) {
                        case 1: // /home/krawler-user/Expression.g:360:41: Exponent
                        {
                            mExponent();

                        }
                        break;

                    }


                }
                break;
                case 2: // /home/krawler-user/Expression.g:361:9: '.' ( '0' .. '9' )+ ( Exponent )?
                {
                    match('.');
                    // /home/krawler-user/Expression.g:361:13: ( '0' .. '9' )+
                    int cnt9 = 0;
                    loop9:
                    do {
                        int alt9 = 2;
                        int LA9_0 = input.LA(1);

                        if (((LA9_0 >= '0' && LA9_0 <= '9'))) {
                            alt9 = 1;
                        }


                        switch (alt9) {
                            case 1: // /home/krawler-user/Expression.g:361:15: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                if (cnt9 >= 1) {
                                    break loop9;
                                }
                                EarlyExitException eee =
                                        new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);

                    // /home/krawler-user/Expression.g:361:29: ( Exponent )?
                    int alt10 = 2;
                    int LA10_0 = input.LA(1);

                    if ((LA10_0 == 'E' || LA10_0 == 'e')) {
                        alt10 = 1;
                    }
                    switch (alt10) {
                        case 1: // /home/krawler-user/Expression.g:361:29: Exponent
                        {
                            mExponent();

                        }
                        break;

                    }


                }
                break;
                case 3: // /home/krawler-user/Expression.g:362:9: ( '0' .. '9' )+ Exponent
                {
                    // /home/krawler-user/Expression.g:362:9: ( '0' .. '9' )+
                    int cnt11 = 0;
                    loop11:
                    do {
                        int alt11 = 2;
                        int LA11_0 = input.LA(1);

                        if (((LA11_0 >= '0' && LA11_0 <= '9'))) {
                            alt11 = 1;
                        }


                        switch (alt11) {
                            case 1: // /home/krawler-user/Expression.g:362:10: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                if (cnt11 >= 1) {
                                    break loop11;
                                }
                                EarlyExitException eee =
                                        new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);

                    mExponent();

                }
                break;
                case 4: // /home/krawler-user/Expression.g:363:9: ( '0' .. '9' )+
                {
                    // /home/krawler-user/Expression.g:363:9: ( '0' .. '9' )+
                    int cnt12 = 0;
                    loop12:
                    do {
                        int alt12 = 2;
                        int LA12_0 = input.LA(1);

                        if (((LA12_0 >= '0' && LA12_0 <= '9'))) {
                            alt12 = 1;
                        }


                        switch (alt12) {
                            case 1: // /home/krawler-user/Expression.g:363:10: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                if (cnt12 >= 1) {
                                    break loop12;
                                }
                                EarlyExitException eee =
                                        new EarlyExitException(12, input);
                                throw eee;
                        }
                        cnt12++;
                    } while (true);


                }
                break;
                case 5: // /home/krawler-user/Expression.g:365:9: HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+
                {
                    mHexPrefix();
                    // /home/krawler-user/Expression.g:365:19: ( HexDigit )*
                    loop13:
                    do {
                        int alt13 = 2;
                        int LA13_0 = input.LA(1);

                        if (((LA13_0 >= '0' && LA13_0 <= '9') || (LA13_0 >= 'A' && LA13_0 <= 'F') || (LA13_0 >= 'a' && LA13_0 <= 'f'))) {
                            alt13 = 1;
                        }


                        switch (alt13) {
                            case 1: // /home/krawler-user/Expression.g:365:20: HexDigit
                            {
                                mHexDigit();

                            }
                            break;

                            default:
                                break loop13;
                        }
                    } while (true);

                    // /home/krawler-user/Expression.g:366:9: ( () | ( '.' ( HexDigit )* ) )
                    int alt15 = 2;
                    int LA15_0 = input.LA(1);

                    if ((LA15_0 == 'P' || LA15_0 == 'p')) {
                        alt15 = 1;
                    } else if ((LA15_0 == '.')) {
                        alt15 = 2;
                    } else {
                        NoViableAltException nvae =
                                new NoViableAltException("", 15, 0, input);

                        throw nvae;
                    }
                    switch (alt15) {
                        case 1: // /home/krawler-user/Expression.g:366:14: ()
                        {
                            // /home/krawler-user/Expression.g:366:14: ()
                            // /home/krawler-user/Expression.g:366:15: 
                            {
                            }


                        }
                        break;
                        case 2: // /home/krawler-user/Expression.g:367:14: ( '.' ( HexDigit )* )
                        {
                            // /home/krawler-user/Expression.g:367:14: ( '.' ( HexDigit )* )
                            // /home/krawler-user/Expression.g:367:15: '.' ( HexDigit )*
                            {
                                match('.');
                                // /home/krawler-user/Expression.g:367:19: ( HexDigit )*
                                loop14:
                                do {
                                    int alt14 = 2;
                                    int LA14_0 = input.LA(1);

                                    if (((LA14_0 >= '0' && LA14_0 <= '9') || (LA14_0 >= 'A' && LA14_0 <= 'F') || (LA14_0 >= 'a' && LA14_0 <= 'f'))) {
                                        alt14 = 1;
                                    }


                                    switch (alt14) {
                                        case 1: // /home/krawler-user/Expression.g:367:20: HexDigit
                                        {
                                            mHexDigit();

                                        }
                                        break;

                                        default:
                                            break loop14;
                                    }
                                } while (true);


                            }


                        }
                        break;

                    }

                    if (input.LA(1) == 'P' || input.LA(1) == 'p') {
                        input.consume();

                    } else {
                        MismatchedSetException mse = new MismatchedSetException(null, input);
                        recover(mse);
                        throw mse;
                    }

                    // /home/krawler-user/Expression.g:370:9: ( '+' | '-' )?
                    int alt16 = 2;
                    int LA16_0 = input.LA(1);

                    if ((LA16_0 == '+' || LA16_0 == '-')) {
                        alt16 = 1;
                    }
                    switch (alt16) {
                        case 1: // /home/krawler-user/Expression.g:
                        {
                            if (input.LA(1) == '+' || input.LA(1) == '-') {
                                input.consume();

                            } else {
                                MismatchedSetException mse = new MismatchedSetException(null, input);
                                recover(mse);
                                throw mse;
                            }


                        }
                        break;

                    }

                    // /home/krawler-user/Expression.g:371:9: ( '0' .. '9' )+
                    int cnt17 = 0;
                    loop17:
                    do {
                        int alt17 = 2;
                        int LA17_0 = input.LA(1);

                        if (((LA17_0 >= '0' && LA17_0 <= '9'))) {
                            alt17 = 1;
                        }


                        switch (alt17) {
                            case 1: // /home/krawler-user/Expression.g:371:11: '0' .. '9'
                            {
                                matchRange('0', '9');

                            }
                            break;

                            default:
                                if (cnt17 >= 1) {
                                    break loop17;
                                }
                                EarlyExitException eee =
                                        new EarlyExitException(17, input);
                                throw eee;
                        }
                        cnt17++;
                    } while (true);


                }
                break;

            }
        } finally {
        }
    }
    // $ANTLR end "NonIntegerNumber"

    // $ANTLR start "Exponent"
    public final void mExponent() throws RecognitionException {
        try {
            // /home/krawler-user/Expression.g:376:5: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // /home/krawler-user/Expression.g:376:9: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
                if (input.LA(1) == 'E' || input.LA(1) == 'e') {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }

                // /home/krawler-user/Expression.g:376:23: ( '+' | '-' )?
                int alt19 = 2;
                int LA19_0 = input.LA(1);

                if ((LA19_0 == '+' || LA19_0 == '-')) {
                    alt19 = 1;
                }
                switch (alt19) {
                    case 1: // /home/krawler-user/Expression.g:
                    {
                        if (input.LA(1) == '+' || input.LA(1) == '-') {
                            input.consume();

                        } else {
                            MismatchedSetException mse = new MismatchedSetException(null, input);
                            recover(mse);
                            throw mse;
                        }


                    }
                    break;

                }

                // /home/krawler-user/Expression.g:376:38: ( '0' .. '9' )+
                int cnt20 = 0;
                loop20:
                do {
                    int alt20 = 2;
                    int LA20_0 = input.LA(1);

                    if (((LA20_0 >= '0' && LA20_0 <= '9'))) {
                        alt20 = 1;
                    }


                    switch (alt20) {
                        case 1: // /home/krawler-user/Expression.g:376:40: '0' .. '9'
                        {
                            matchRange('0', '9');

                        }
                        break;

                        default:
                            if (cnt20 >= 1) {
                                break loop20;
                            }
                            EarlyExitException eee =
                                    new EarlyExitException(20, input);
                            throw eee;
                    }
                    cnt20++;
                } while (true);


            }

        } finally {
        }
    }
    // $ANTLR end "Exponent"

    // $ANTLR start "FloatSuffix"
    public final void mFloatSuffix() throws RecognitionException {
        try {
            // /home/krawler-user/Expression.g:381:5: ( 'f' | 'F' )
            // /home/krawler-user/Expression.g:
            {
                if (input.LA(1) == 'F' || input.LA(1) == 'f') {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }


            }

        } finally {
        }
    }
    // $ANTLR end "FloatSuffix"

    // $ANTLR start "DoubleSuffix"
    public final void mDoubleSuffix() throws RecognitionException {
        try {
            // /home/krawler-user/Expression.g:386:5: ( 'd' | 'D' )
            // /home/krawler-user/Expression.g:
            {
                if (input.LA(1) == 'D' || input.LA(1) == 'd') {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }


            }

        } finally {
        }
    }
    // $ANTLR end "DoubleSuffix"

    // $ANTLR start "FLOATLITERAL"
    public final void mFLOATLITERAL() throws RecognitionException {
        try {
            int _type = FLOATLITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:390:5: ( NonIntegerNumber FloatSuffix )
            // /home/krawler-user/Expression.g:390:9: NonIntegerNumber FloatSuffix
            {
                mNonIntegerNumber();
                mFloatSuffix();

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "FLOATLITERAL"

    // $ANTLR start "DOUBLELITERAL"
    public final void mDOUBLELITERAL() throws RecognitionException {
        try {
            int _type = DOUBLELITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:394:5: ( NonIntegerNumber ( DoubleSuffix )? )
            // /home/krawler-user/Expression.g:394:9: NonIntegerNumber ( DoubleSuffix )?
            {
                mNonIntegerNumber();
                // /home/krawler-user/Expression.g:394:26: ( DoubleSuffix )?
                int alt21 = 2;
                int LA21_0 = input.LA(1);

                if ((LA21_0 == 'D' || LA21_0 == 'd')) {
                    alt21 = 1;
                }
                switch (alt21) {
                    case 1: // /home/krawler-user/Expression.g:394:26: DoubleSuffix
                    {
                        mDoubleSuffix();

                    }
                    break;

                }


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "DOUBLELITERAL"

    // $ANTLR start "CHARLITERAL"
    public final void mCHARLITERAL() throws RecognitionException {
        try {
            int _type = CHARLITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            int c;

            // /home/krawler-user/Expression.g:398:5: ( '\\'' ( ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) ) | c=~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) ) '\\'' )
            // /home/krawler-user/Expression.g:398:9: '\\'' ( ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) ) | c=~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) ) '\\''
            {
                match('\'');
                StringBuilder b = new StringBuilder();
                // /home/krawler-user/Expression.g:400:9: ( ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) ) | c=~ ( '\\'' | '\\\\' | '\\r' | '\\n' ) )
                int alt23 = 2;
                int LA23_0 = input.LA(1);

                if ((LA23_0 == '\\')) {
                    alt23 = 1;
                } else if (((LA23_0 >= '\u0000' && LA23_0 <= '\t') || (LA23_0 >= '\u000B' && LA23_0 <= '\f') || (LA23_0 >= '\u000E' && LA23_0 <= '&') || (LA23_0 >= '(' && LA23_0 <= '[') || (LA23_0 >= ']' && LA23_0 <= '\uFFFF'))) {
                    alt23 = 2;
                } else {
                    NoViableAltException nvae =
                            new NoViableAltException("", 23, 0, input);

                    throw nvae;
                }
                switch (alt23) {
                    case 1: // /home/krawler-user/Expression.g:400:13: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) )
                    {
                        // /home/krawler-user/Expression.g:400:13: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) )
                        // /home/krawler-user/Expression.g:400:14: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
                        {
                            match('\\');
                            // /home/krawler-user/Expression.g:400:19: ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
                            int alt22 = 8;
                            switch (input.LA(1)) {
                                case 'b': {
                                    alt22 = 1;
                                }
                                break;
                                case 't': {
                                    alt22 = 2;
                                }
                                break;
                                case 'n': {
                                    alt22 = 3;
                                }
                                break;
                                case 'f': {
                                    alt22 = 4;
                                }
                                break;
                                case 'r': {
                                    alt22 = 5;
                                }
                                break;
                                case '\"': {
                                    alt22 = 6;
                                }
                                break;
                                case '\'': {
                                    alt22 = 7;
                                }
                                break;
                                case '\\': {
                                    alt22 = 8;
                                }
                                break;
                                default:
                                    NoViableAltException nvae =
                                            new NoViableAltException("", 22, 0, input);

                                    throw nvae;
                            }

                            switch (alt22) {
                                case 1: // /home/krawler-user/Expression.g:401:18: 'b'
                                {
                                    match('b');
                                    b.append('\b');

                                }
                                break;
                                case 2: // /home/krawler-user/Expression.g:402:18: 't'
                                {
                                    match('t');
                                    b.append('\t');

                                }
                                break;
                                case 3: // /home/krawler-user/Expression.g:403:18: 'n'
                                {
                                    match('n');
                                    b.append('\n');

                                }
                                break;
                                case 4: // /home/krawler-user/Expression.g:404:18: 'f'
                                {
                                    match('f');
                                    b.append('\f');

                                }
                                break;
                                case 5: // /home/krawler-user/Expression.g:405:18: 'r'
                                {
                                    match('r');
                                    b.append('\r');

                                }
                                break;
                                case 6: // /home/krawler-user/Expression.g:406:18: '\"'
                                {
                                    match('\"');
                                    b.append('"');

                                }
                                break;
                                case 7: // /home/krawler-user/Expression.g:407:18: '\\''
                                {
                                    match('\'');
                                    b.append('\'');

                                }
                                break;
                                case 8: // /home/krawler-user/Expression.g:408:18: '\\\\'
                                {
                                    match('\\');
                                    b.append('\\');

                                }
                                break;

                            }


                        }


                    }
                    break;
                    case 2: // /home/krawler-user/Expression.g:410:13: c=~ ( '\\'' | '\\\\' | '\\r' | '\\n' )
                    {
                        c = input.LA(1);
                        if ((input.LA(1) >= '\u0000' && input.LA(1) <= '\t') || (input.LA(1) >= '\u000B' && input.LA(1) <= '\f') || (input.LA(1) >= '\u000E' && input.LA(1) <= '&') || (input.LA(1) >= '(' && input.LA(1) <= '[') || (input.LA(1) >= ']' && input.LA(1) <= '\uFFFF')) {
                            input.consume();

                        } else {
                            MismatchedSetException mse = new MismatchedSetException(null, input);
                            recover(mse);
                            throw mse;
                        }

                        b.appendCodePoint(c);

                    }
                    break;

                }

                match('\'');
                setText(b.toString());

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "CHARLITERAL"

    // $ANTLR start "STRINGLITERAL"
    public final void mSTRINGLITERAL() throws RecognitionException {
        try {
            int _type = STRINGLITERAL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            int c;

            // /home/krawler-user/Expression.g:417:5: ( '\"' ( ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) ) | c=~ ( '\\\\' | '\"' | '\\r' | '\\n' ) )* '\"' )
            // /home/krawler-user/Expression.g:417:9: '\"' ( ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) ) | c=~ ( '\\\\' | '\"' | '\\r' | '\\n' ) )* '\"'
            {
                match('\"');
                StringBuilder b = new StringBuilder();
                // /home/krawler-user/Expression.g:419:9: ( ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) ) | c=~ ( '\\\\' | '\"' | '\\r' | '\\n' ) )*
                loop25:
                do {
                    int alt25 = 3;
                    int LA25_0 = input.LA(1);

                    if ((LA25_0 == '\\')) {
                        alt25 = 1;
                    } else if (((LA25_0 >= '\u0000' && LA25_0 <= '\t') || (LA25_0 >= '\u000B' && LA25_0 <= '\f') || (LA25_0 >= '\u000E' && LA25_0 <= '!') || (LA25_0 >= '#' && LA25_0 <= '[') || (LA25_0 >= ']' && LA25_0 <= '\uFFFF'))) {
                        alt25 = 2;
                    }


                    switch (alt25) {
                        case 1: // /home/krawler-user/Expression.g:419:13: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) )
                        {
                            // /home/krawler-user/Expression.g:419:13: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' ) )
                            // /home/krawler-user/Expression.g:419:14: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
                            {
                                match('\\');
                                // /home/krawler-user/Expression.g:419:19: ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '\\\\' )
                                int alt24 = 8;
                                switch (input.LA(1)) {
                                    case 'b': {
                                        alt24 = 1;
                                    }
                                    break;
                                    case 't': {
                                        alt24 = 2;
                                    }
                                    break;
                                    case 'n': {
                                        alt24 = 3;
                                    }
                                    break;
                                    case 'f': {
                                        alt24 = 4;
                                    }
                                    break;
                                    case 'r': {
                                        alt24 = 5;
                                    }
                                    break;
                                    case '\"': {
                                        alt24 = 6;
                                    }
                                    break;
                                    case '\'': {
                                        alt24 = 7;
                                    }
                                    break;
                                    case '\\': {
                                        alt24 = 8;
                                    }
                                    break;
                                    default:
                                        NoViableAltException nvae =
                                                new NoViableAltException("", 24, 0, input);

                                        throw nvae;
                                }

                                switch (alt24) {
                                    case 1: // /home/krawler-user/Expression.g:420:18: 'b'
                                    {
                                        match('b');
                                        b.append('\b');

                                    }
                                    break;
                                    case 2: // /home/krawler-user/Expression.g:421:18: 't'
                                    {
                                        match('t');
                                        b.append('\t');

                                    }
                                    break;
                                    case 3: // /home/krawler-user/Expression.g:422:18: 'n'
                                    {
                                        match('n');
                                        b.append('\n');

                                    }
                                    break;
                                    case 4: // /home/krawler-user/Expression.g:423:18: 'f'
                                    {
                                        match('f');
                                        b.append('\f');

                                    }
                                    break;
                                    case 5: // /home/krawler-user/Expression.g:424:18: 'r'
                                    {
                                        match('r');
                                        b.append('\r');

                                    }
                                    break;
                                    case 6: // /home/krawler-user/Expression.g:425:18: '\"'
                                    {
                                        match('\"');
                                        b.append('"');

                                    }
                                    break;
                                    case 7: // /home/krawler-user/Expression.g:426:18: '\\''
                                    {
                                        match('\'');
                                        b.append('\'');

                                    }
                                    break;
                                    case 8: // /home/krawler-user/Expression.g:427:18: '\\\\'
                                    {
                                        match('\\');
                                        b.append('\\');

                                    }
                                    break;

                                }


                            }


                        }
                        break;
                        case 2: // /home/krawler-user/Expression.g:429:11: c=~ ( '\\\\' | '\"' | '\\r' | '\\n' )
                        {
                            c = input.LA(1);
                            if ((input.LA(1) >= '\u0000' && input.LA(1) <= '\t') || (input.LA(1) >= '\u000B' && input.LA(1) <= '\f') || (input.LA(1) >= '\u000E' && input.LA(1) <= '!') || (input.LA(1) >= '#' && input.LA(1) <= '[') || (input.LA(1) >= ']' && input.LA(1) <= '\uFFFF')) {
                                input.consume();

                            } else {
                                MismatchedSetException mse = new MismatchedSetException(null, input);
                                recover(mse);
                                throw mse;
                            }

                            b.appendCodePoint(c);

                        }
                        break;

                        default:
                            break loop25;
                    }
                } while (true);

                match('\"');
                setText(b.toString());

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "STRINGLITERAL"

    // $ANTLR start "IDENTIFIER"
    public final void mIDENTIFIER() throws RecognitionException {
        try {
            int _type = IDENTIFIER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:436:5: ( IdentifierStart ( IdentifierPart )* )
            // /home/krawler-user/Expression.g:436:9: IdentifierStart ( IdentifierPart )*
            {
                mIdentifierStart();
                // /home/krawler-user/Expression.g:436:25: ( IdentifierPart )*
                loop26:
                do {
                    int alt26 = 2;
                    int LA26_0 = input.LA(1);

                    if (((LA26_0 >= '0' && LA26_0 <= '9') || (LA26_0 >= 'A' && LA26_0 <= 'Z') || LA26_0 == '_' || (LA26_0 >= 'a' && LA26_0 <= 'z'))) {
                        alt26 = 1;
                    }


                    switch (alt26) {
                        case 1: // /home/krawler-user/Expression.g:436:25: IdentifierPart
                        {
                            mIdentifierPart();

                        }
                        break;

                        default:
                            break loop26;
                    }
                } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "IDENTIFIER"

    // $ANTLR start "IdentifierStart"
    public final void mIdentifierStart() throws RecognitionException {
        try {
            // /home/krawler-user/Expression.g:441:2: ( 'a' .. 'z' | 'A' .. 'Z' | '_' )
            // /home/krawler-user/Expression.g:
            {
                if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }


            }

        } finally {
        }
    }
    // $ANTLR end "IdentifierStart"

    // $ANTLR start "IdentifierPart"
    public final void mIdentifierPart() throws RecognitionException {
        try {
            // /home/krawler-user/Expression.g:448:2: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' )
            // /home/krawler-user/Expression.g:
            {
                if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z')) {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }


            }

        } finally {
        }
    }
    // $ANTLR end "IdentifierPart"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:455:5: ( ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' ) )
            // /home/krawler-user/Expression.g:455:9: ( ' ' | '\\r' | '\\t' | '\\u000C' | '\\n' )
            {
                if ((input.LA(1) >= '\t' && input.LA(1) <= '\n') || (input.LA(1) >= '\f' && input.LA(1) <= '\r') || input.LA(1) == ' ') {
                    input.consume();

                } else {
                    MismatchedSetException mse = new MismatchedSetException(null, input);
                    recover(mse);
                    throw mse;
                }


                _channel = HIDDEN;


            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:468:2: ( ',' )
            // /home/krawler-user/Expression.g:468:4: ','
            {
                match(',');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "LPAREN"
    public final void mLPAREN() throws RecognitionException {
        try {
            int _type = LPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:472:2: ( '(' )
            // /home/krawler-user/Expression.g:472:4: '('
            {
                match('(');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    public final void mRPAREN() throws RecognitionException {
        try {
            int _type = RPAREN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:476:2: ( ')' )
            // /home/krawler-user/Expression.g:476:4: ')'
            {
                match(')');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:480:2: ( '+' )
            // /home/krawler-user/Expression.g:480:4: '+'
            {
                match('+');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:484:2: ( '-' )
            // /home/krawler-user/Expression.g:484:4: '-'
            {
                match('-');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "MULTI"
    public final void mMULTI() throws RecognitionException {
        try {
            int _type = MULTI;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:488:2: ( '*' )
            // /home/krawler-user/Expression.g:488:4: '*'
            {
                match('*');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "MULTI"

    // $ANTLR start "DIV"
    public final void mDIV() throws RecognitionException {
        try {
            int _type = DIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:492:2: ( '/' )
            // /home/krawler-user/Expression.g:492:4: '/'
            {
                match('/');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "DIV"

    // $ANTLR start "MOD"
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // /home/krawler-user/Expression.g:496:2: ( '%' )
            // /home/krawler-user/Expression.g:496:4: '%'
            {
                match('%');

            }

            state.type = _type;
            state.channel = _channel;
        } finally {
        }
    }
    // $ANTLR end "MOD"

    public void mTokens() throws RecognitionException {
        // /home/krawler-user/Expression.g:1:8: ( T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | TRUE | FALSE | NULL | LONGLITERAL | INTLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | IDENTIFIER | WS | COMMA | LPAREN | RPAREN | PLUS | MINUS | MULTI | DIV | MOD )
        int alt27 = 37;
        alt27 = dfa27.predict(input);
        switch (alt27) {
            case 1: // /home/krawler-user/Expression.g:1:10: T__33
            {
                mT__33();

            }
            break;
            case 2: // /home/krawler-user/Expression.g:1:16: T__34
            {
                mT__34();

            }
            break;
            case 3: // /home/krawler-user/Expression.g:1:22: T__35
            {
                mT__35();

            }
            break;
            case 4: // /home/krawler-user/Expression.g:1:28: T__36
            {
                mT__36();

            }
            break;
            case 5: // /home/krawler-user/Expression.g:1:34: T__37
            {
                mT__37();

            }
            break;
            case 6: // /home/krawler-user/Expression.g:1:40: T__38
            {
                mT__38();

            }
            break;
            case 7: // /home/krawler-user/Expression.g:1:46: T__39
            {
                mT__39();

            }
            break;
            case 8: // /home/krawler-user/Expression.g:1:52: T__40
            {
                mT__40();

            }
            break;
            case 9: // /home/krawler-user/Expression.g:1:58: T__41
            {
                mT__41();

            }
            break;
            case 10: // /home/krawler-user/Expression.g:1:64: T__42
            {
                mT__42();

            }
            break;
            case 11: // /home/krawler-user/Expression.g:1:70: T__43
            {
                mT__43();

            }
            break;
            case 12: // /home/krawler-user/Expression.g:1:76: T__44
            {
                mT__44();

            }
            break;
            case 13: // /home/krawler-user/Expression.g:1:82: T__45
            {
                mT__45();

            }
            break;
            case 14: // /home/krawler-user/Expression.g:1:88: T__46
            {
                mT__46();

            }
            break;
            case 15: // /home/krawler-user/Expression.g:1:94: T__47
            {
                mT__47();

            }
            break;
            case 16: // /home/krawler-user/Expression.g:1:100: T__48
            {
                mT__48();

            }
            break;
            case 17: // /home/krawler-user/Expression.g:1:106: T__49
            {
                mT__49();

            }
            break;
            case 18: // /home/krawler-user/Expression.g:1:112: T__50
            {
                mT__50();

            }
            break;
            case 19: // /home/krawler-user/Expression.g:1:118: TRUE
            {
                mTRUE();

            }
            break;
            case 20: // /home/krawler-user/Expression.g:1:123: FALSE
            {
                mFALSE();

            }
            break;
            case 21: // /home/krawler-user/Expression.g:1:129: NULL
            {
                mNULL();

            }
            break;
            case 22: // /home/krawler-user/Expression.g:1:134: LONGLITERAL
            {
                mLONGLITERAL();

            }
            break;
            case 23: // /home/krawler-user/Expression.g:1:146: INTLITERAL
            {
                mINTLITERAL();

            }
            break;
            case 24: // /home/krawler-user/Expression.g:1:157: FLOATLITERAL
            {
                mFLOATLITERAL();

            }
            break;
            case 25: // /home/krawler-user/Expression.g:1:170: DOUBLELITERAL
            {
                mDOUBLELITERAL();

            }
            break;
            case 26: // /home/krawler-user/Expression.g:1:184: CHARLITERAL
            {
                mCHARLITERAL();

            }
            break;
            case 27: // /home/krawler-user/Expression.g:1:196: STRINGLITERAL
            {
                mSTRINGLITERAL();

            }
            break;
            case 28: // /home/krawler-user/Expression.g:1:210: IDENTIFIER
            {
                mIDENTIFIER();

            }
            break;
            case 29: // /home/krawler-user/Expression.g:1:221: WS
            {
                mWS();

            }
            break;
            case 30: // /home/krawler-user/Expression.g:1:224: COMMA
            {
                mCOMMA();

            }
            break;
            case 31: // /home/krawler-user/Expression.g:1:230: LPAREN
            {
                mLPAREN();

            }
            break;
            case 32: // /home/krawler-user/Expression.g:1:237: RPAREN
            {
                mRPAREN();

            }
            break;
            case 33: // /home/krawler-user/Expression.g:1:244: PLUS
            {
                mPLUS();

            }
            break;
            case 34: // /home/krawler-user/Expression.g:1:249: MINUS
            {
                mMINUS();

            }
            break;
            case 35: // /home/krawler-user/Expression.g:1:255: MULTI
            {
                mMULTI();

            }
            break;
            case 36: // /home/krawler-user/Expression.g:1:261: DIV
            {
                mDIV();

            }
            break;
            case 37: // /home/krawler-user/Expression.g:1:265: MOD
            {
                mMOD();

            }
            break;

        }

    }
    protected DFA18 dfa18 = new DFA18(this);
    protected DFA27 dfa27 = new DFA27(this);
    static final String DFA18_eotS =
            "\1\uffff\1\7\1\uffff\1\7\4\uffff";
    static final String DFA18_eofS =
            "\10\uffff";
    static final String DFA18_minS =
            "\2\56\1\uffff\1\56\4\uffff";
    static final String DFA18_maxS =
            "\1\71\1\170\1\uffff\1\145\4\uffff";
    static final String DFA18_acceptS =
            "\2\uffff\1\2\1\uffff\1\5\1\3\1\1\1\4";
    static final String DFA18_specialS =
            "\10\uffff}>";
    static final String[] DFA18_transitionS = {
        "\1\2\1\uffff\1\1\11\3",
        "\1\6\1\uffff\12\3\13\uffff\1\5\22\uffff\1\4\14\uffff\1\5\22"
        + "\uffff\1\4",
        "",
        "\1\6\1\uffff\12\3\13\uffff\1\5\37\uffff\1\5",
        "",
        "",
        "",
        ""
    };
    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    public class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }

        @Override
        public String getDescription() {
            return "358:1: fragment NonIntegerNumber : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( Exponent )? | '.' ( '0' .. '9' )+ ( Exponent )? | ( '0' .. '9' )+ Exponent | ( '0' .. '9' )+ | HexPrefix ( HexDigit )* ( () | ( '.' ( HexDigit )* ) ) ( 'p' | 'P' ) ( '+' | '-' )? ( '0' .. '9' )+ );";
        }
    }
    static final String DFA27_eotS =
            "\1\uffff\1\41\1\uffff\1\26\4\uffff\1\44\1\46\1\50\1\uffff\1\52\2"
            + "\uffff\3\26\2\61\16\uffff\1\71\6\uffff\1\62\1\uffff\3\26\2\uffff"
            + "\1\61\2\uffff\2\62\3\uffff\1\61\2\uffff\3\26\1\61\2\uffff\1\62\2"
            + "\uffff\1\62\1\uffff\1\62\1\117\1\26\1\121\1\uffff\1\62\2\uffff\1"
            + "\62\1\uffff\1\122\2\uffff";
    static final String DFA27_eofS =
            "\123\uffff";
    static final String DFA27_minS =
            "\1\11\1\75\1\uffff\1\146\4\uffff\3\75\1\uffff\1\60\2\uffff\1\162"
            + "\1\141\1\165\2\56\16\uffff\1\60\6\uffff\1\60\1\uffff\1\165\2\154"
            + "\3\56\2\uffff\1\56\1\60\1\53\2\uffff\1\56\1\uffff\1\53\1\145\1\163"
            + "\1\154\1\56\1\53\2\60\1\53\5\60\1\145\6\60\1\uffff\1\60\2\uffff";
    static final String DFA27_maxS =
            "\1\175\1\75\1\uffff\1\146\4\uffff\3\75\1\uffff\1\71\2\uffff\1\162"
            + "\1\141\1\165\1\170\1\154\16\uffff\1\172\6\uffff\1\146\1\uffff\1"
            + "\165\2\154\2\160\1\154\2\uffff\2\146\1\71\2\uffff\1\154\1\uffff"
            + "\1\71\1\145\1\163\1\154\1\160\1\71\1\160\1\146\2\71\1\146\1\71\1"
            + "\146\1\172\1\145\1\172\1\71\1\146\1\160\1\71\1\146\1\uffff\1\172"
            + "\2\uffff";
    static final String DFA27_acceptS =
            "\2\uffff\1\2\1\uffff\1\4\1\5\1\6\1\7\3\uffff\1\17\1\uffff\1\21\1"
            + "\22\5\uffff\1\32\1\33\1\34\1\35\1\36\1\37\1\40\1\41\1\42\1\43\1"
            + "\44\1\45\1\10\1\1\1\uffff\1\11\1\16\1\12\1\14\1\13\1\15\1\uffff"
            + "\1\20\6\uffff\1\27\1\31\3\uffff\1\26\1\30\1\uffff\1\3\25\uffff\1"
            + "\23\1\uffff\1\25\1\24";
    static final String DFA27_specialS =
            "\123\uffff}>";
    static final String[] DFA27_transitionS = {
        "\2\27\1\uffff\2\27\22\uffff\1\27\1\10\1\25\2\uffff\1\37\1\7"
        + "\1\24\1\31\1\32\1\35\1\33\1\30\1\34\1\14\1\36\1\22\11\23\1\13"
        + "\1\2\1\11\1\1\1\12\2\uffff\32\26\1\15\1\uffff\1\16\1\uffff\1"
        + "\26\1\uffff\5\26\1\20\2\26\1\3\4\26\1\21\5\26\1\17\6\26\1\4"
        + "\1\6\1\5",
        "\1\40",
        "",
        "\1\42",
        "",
        "",
        "",
        "",
        "\1\43",
        "\1\45",
        "\1\47",
        "",
        "\12\51",
        "",
        "",
        "\1\53",
        "\1\54",
        "\1\55",
        "\1\64\1\uffff\10\60\2\63\12\uffff\1\62\1\65\1\67\5\uffff\1"
        + "\66\13\uffff\1\57\13\uffff\1\62\1\65\1\67\5\uffff\1\66\13\uffff"
        + "\1\56",
        "\1\64\1\uffff\12\70\12\uffff\1\62\1\65\1\67\5\uffff\1\66\27"
        + "\uffff\1\62\1\65\1\67\5\uffff\1\66",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
        "",
        "",
        "",
        "",
        "",
        "",
        "\12\51\13\uffff\1\72\1\67\36\uffff\1\72\1\67",
        "",
        "\1\73",
        "\1\74",
        "\1\75",
        "\1\100\1\uffff\12\76\7\uffff\6\76\11\uffff\1\77\20\uffff\6"
        + "\76\11\uffff\1\77",
        "\1\100\1\uffff\12\76\7\uffff\6\76\11\uffff\1\77\20\uffff\6"
        + "\76\11\uffff\1\77",
        "\1\64\1\uffff\10\60\2\63\12\uffff\1\62\1\65\1\67\5\uffff\1"
        + "\66\27\uffff\1\62\1\65\1\67\5\uffff\1\66",
        "",
        "",
        "\1\64\1\uffff\12\63\13\uffff\1\65\1\67\36\uffff\1\65\1\67",
        "\12\101\13\uffff\1\102\1\67\36\uffff\1\102\1\67",
        "\1\103\1\uffff\1\103\2\uffff\12\104",
        "",
        "",
        "\1\64\1\uffff\12\70\12\uffff\1\62\1\65\1\67\5\uffff\1\66\27"
        + "\uffff\1\62\1\65\1\67\5\uffff\1\66",
        "",
        "\1\105\1\uffff\1\105\2\uffff\12\106",
        "\1\107",
        "\1\110",
        "\1\111",
        "\1\100\1\uffff\12\76\7\uffff\6\76\5\uffff\1\66\3\uffff\1\77"
        + "\20\uffff\6\76\5\uffff\1\66\3\uffff\1\77",
        "\1\112\1\uffff\1\112\2\uffff\12\113",
        "\12\114\7\uffff\6\114\11\uffff\1\77\20\uffff\6\114\11\uffff"
        + "\1\77",
        "\12\101\13\uffff\1\102\1\67\36\uffff\1\102\1\67",
        "\1\115\1\uffff\1\115\2\uffff\12\116",
        "\12\104",
        "\12\104\14\uffff\1\67\37\uffff\1\67",
        "\12\106",
        "\12\106\14\uffff\1\67\37\uffff\1\67",
        "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
        "\1\120",
        "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
        "\12\113",
        "\12\113\14\uffff\1\67\37\uffff\1\67",
        "\12\114\7\uffff\6\114\11\uffff\1\77\20\uffff\6\114\11\uffff"
        + "\1\77",
        "\12\116",
        "\12\116\14\uffff\1\67\37\uffff\1\67",
        "",
        "\12\26\7\uffff\32\26\4\uffff\1\26\1\uffff\32\26",
        "",
        ""
    };
    static final short[] DFA27_eot = DFA.unpackEncodedString(DFA27_eotS);
    static final short[] DFA27_eof = DFA.unpackEncodedString(DFA27_eofS);
    static final char[] DFA27_min = DFA.unpackEncodedStringToUnsignedChars(DFA27_minS);
    static final char[] DFA27_max = DFA.unpackEncodedStringToUnsignedChars(DFA27_maxS);
    static final short[] DFA27_accept = DFA.unpackEncodedString(DFA27_acceptS);
    static final short[] DFA27_special = DFA.unpackEncodedString(DFA27_specialS);
    static final short[][] DFA27_transition;

    static {
        int numStates = DFA27_transitionS.length;
        DFA27_transition = new short[numStates][];
        for (int i = 0; i < numStates; i++) {
            DFA27_transition[i] = DFA.unpackEncodedString(DFA27_transitionS[i]);
        }
    }

    public class DFA27 extends DFA {

        public DFA27(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 27;
            this.eot = DFA27_eot;
            this.eof = DFA27_eof;
            this.min = DFA27_min;
            this.max = DFA27_max;
            this.accept = DFA27_accept;
            this.special = DFA27_special;
            this.transition = DFA27_transition;
        }

        @Override
        public String getDescription() {
            return "1:1: Tokens : ( T__33 | T__34 | T__35 | T__36 | T__37 | T__38 | T__39 | T__40 | T__41 | T__42 | T__43 | T__44 | T__45 | T__46 | T__47 | T__48 | T__49 | T__50 | TRUE | FALSE | NULL | LONGLITERAL | INTLITERAL | FLOATLITERAL | DOUBLELITERAL | CHARLITERAL | STRINGLITERAL | IDENTIFIER | WS | COMMA | LPAREN | RPAREN | PLUS | MINUS | MULTI | DIV | MOD );";
        }
    }
}
