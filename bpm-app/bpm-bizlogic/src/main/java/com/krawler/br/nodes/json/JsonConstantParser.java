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

import com.krawler.br.ProcessException;
import com.krawler.br.exp.Constant;
import com.krawler.br.exp.Expression;
import com.krawler.br.exp.Scope;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;

/**
 * class to parse the constant from xml dom element
 *
 * @author Vishnu Kant Gupta
 */
public class JsonConstantParser extends JsonOperandParser {
    private static final String CONST = "const";
    private static final String VALUE = "val";
    private static final String C_TYPE = "type";

    private static final String STRING = "string";
    private static final String BYTE = "byte";
    private static final String SHORT = "short";
    private static final String INT = "int";
    private static final String LONG = "long";
    private static final String FLOAT = "float";
    private static final String DOUBLE = "double";
    private static final String DATE = "date";
    private static final String BOOLEAN = "boolean";
    private static final String CHARACTER = "char";

    public JsonConstantParser(JsonOperandParser successor) {
        super(successor);
    }

    @Override
    public Expression parseExpression(JSONObject parent, Scope scope) throws ProcessException {
        JSONObject detail = parent.optJSONObject(DETAIL);
        if(CONST.equals(parent.optString(TYPE))&&detail!=null){
            String val=detail.optString(VALUE);
            if(val!=null){
                String type = detail.optString(C_TYPE);
                Object value = null;
                    if( type==null||type.length()==0||STRING.equals(type)){
                        value = val;
                    }else if( BOOLEAN.equals(type)){
                        value = new Byte(val);
                    }else if( BYTE.equals(type)){
                        value = new Byte(val);                      
                    }else if( CHARACTER.equals(type)){
                        value = new Character(val.charAt(0));                        
                    }else if( DATE.equals(type)){
                        value = new Date(new Long(val));                        
                    }else if( DOUBLE.equals(type)){
                        value = new Double(val);                        
                    }else if( FLOAT.equals(type)){
                        value = new Float(val);                       
                    }else if( INT.equals(type)){
                        value = new Integer(val);                        
                    }else if( LONG.equals(type)){
                        value = new Long(val);                        
                    }else if( SHORT.equals(type)){
                        value = new Short(val);                        
                    }else{
                        throw new ProcessException("Constant type not supported: "+type);
                    }

                Constant c=new Constant();
                c.setValue(value);
                return c;
            }else
                throw new ProcessException("wrong constant syntax: "+parent.toString());
        }else
            return super.parseExpression(parent, scope);
    }
}
