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
package com.krawler.br.exp;

import com.krawler.br.ProcessException;

/**
 * represents the class for constant values used in business rules
 *
 * @author Vishnu Kant Gupta
 */
public class Constant implements Expression {
    boolean assigned;
    private Object val;

    public Constant() {
    }

    @Override
    public Object getValue() {
        return val;
    }
    public synchronized void setValue(Object val) throws ProcessException {
        if(assigned)
            throw new ProcessException("Can't change a constant");
        this.val = val;
        assigned = true;
    }

    @Override
    public String toString() {
        if(val==null)
            return null;
        if(val instanceof  Character)
            return "'"+convertEscapeChar((Character)val)+"'";
        else if(val instanceof String)
            return "\""+convertEscapeChar((String)val)+'"';
        else
            return val.toString();
    }

    private String convertEscapeChar(String str){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<str.length();i++)
            sb.append(convertEscapeChar(str.charAt(i)));
        return sb.toString();
    }
    private String convertEscapeChar(char ch){
        switch(ch){
            case '\b': return "\\b";
            case '\t': return "\\t";
            case '\n': return "\\n";
            case '\f': return "\\f";
            case '\r': return "\\r";
            case '\'': return "\\'";
            case '\"': return "\\\"";
            case '\\': return "\\\\";
            default: return ""+ch;
        }
    }

    @Override
    public VALUE_TYPE getValueType() {
        if(val != null){
            if(val instanceof Boolean)
                return VALUE_TYPE.BOOLEAN;
            else if(val instanceof Number)
                return VALUE_TYPE.NUMBER;
            else
                return null;
        }
        return null;
    }
}

