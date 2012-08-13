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

package com.krawler.portal.tools;
import com.krawler.portal.util.TextFormatter;
public class EntityColumn implements Cloneable{
    private String _name;
    private String _type;
    private String _default;
    private boolean _primary;
    private boolean _foreign;
    private String _methodName;
    private boolean _convertNull;
    public EntityColumn(String name) {
            this(name, null,false,true,"");
    }

    public Object clone() {
        return new EntityColumn(
                getName(), getType(), isPrimary(),isConvertNull(), getDefault());
    }

    public boolean equals(Object obj) {
        EntityColumn col = (EntityColumn)obj;

        String name = col.getName();

        if (_name.equals(name)) {
                return true;
        }
        else {
                return false;
        }
    }
    public EntityColumn(String name, String type, boolean primary, boolean convertNull, String defaultValue) {
        _name = name;
        _type = type;
        _default = defaultValue;
        _primary = primary;
        _methodName = TextFormatter.format(name, TextFormatter.G);
        _convertNull = convertNull;
    }
    public EntityColumn(String name, String type, boolean primary,boolean foreign,boolean convertNull, String defaultValue) {
        _name = name;
        _type = type;
        _default = defaultValue;
        _primary = primary;
        _foreign = foreign;
        _methodName = TextFormatter.format(name, TextFormatter.G);
        _convertNull = convertNull;
    }
    
    public String getName() {
        return _name;
    }
    public String getMethodName() {
            return _methodName;
    }
    public String getType() {
        return _type;
    }
    
    public String getDefault() {
        return _default;
    }

    public boolean isPrimary() {
        return _primary;
    }
    public boolean isForeign() {
        return _foreign;
    }
    public boolean isConvertNull() {
		return _convertNull;
	}
    public boolean isPrimitiveType() {
		if (Character.isLowerCase(_type.charAt(0))) {
			return true;
		}
		else {
			return false;
		}
	}
    
    public boolean isCollection() {
		if (_type.equals("Collection")) {
			return true;
		}
		else {
			return false;
		}
	}
}
