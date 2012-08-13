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

import java.util.List;

public class Entity {
    private List<EntityColumn> _columnList;
    private String _name;    
    private String _packagePath;
    private boolean _reportFlag;
    private boolean _createTableFlag;
    private String _className;
    
    private List<EntityColumn> _regularColList;
    private String _table;
    private List<EntityColumn> _pkList;
    private List<EntityColumn> _fkList;
    public Entity(String packagePath,String name,String table,List<EntityColumn> regularColList, List<EntityColumn> columnList,List<EntityColumn> pkList, List<EntityColumn> fkList) {
        _packagePath = packagePath;
        _columnList = columnList;
        _name = name;
        _regularColList = regularColList;
        _table = table;
        _pkList = pkList;
        _fkList = fkList;
    }

    public Entity(String packagePath,String name,boolean reportFlag,String className,boolean createTableFlg) {
        _packagePath = packagePath;
        _name = name;
        _reportFlag = reportFlag;
        _className = className;
        _createTableFlag = createTableFlg;
    }

    public boolean getCreateTableFlag() {
        return _createTableFlag;
    }
    public String getClassName(){
        return _className;
    }
    public String getPackagePath() {
        return _packagePath;
    }   
    public EntityColumn getColumn(String name) {
            return getColumn(name, _columnList);
    }
    public String getTable() {
            return _table;
    }
    public String getName() {
            return _name;
    }
    public boolean getReportFlag() {
            return _reportFlag;
    }
    public List<EntityColumn> getRegularColList() {
            return _regularColList;
    }
    public List<EntityColumn> getColumnList() {
            return _columnList;
    }
    public List<EntityColumn> getPKList() {
		return _pkList;
	}
    public List<EntityColumn> getFKList() {
		return _fkList;
	}
    public boolean equals(Object obj) {
            Entity entity = (Entity)obj;

            String name = entity.getName();

            if (_name.equals(name)) {
                    return true;
            }
            else {
                    return false;
            }
    }
    public boolean hasColumns() {
            if ((_columnList == null) || (_columnList.size() == 0)) {
                    return false;
            }
            else {
                    return true;
            }
    }  
    public boolean hasCompoundPK() {
		if (_pkList.size() > 1) {
			return true;
		}
		else {
			return false;
		}
    }
    public boolean hasForeignKey() {
        if (_fkList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean hasPrimitivePK() {
		if (hasCompoundPK()) {
			return false;
		}
		else {
			EntityColumn col = _pkList.get(0);

			if (col.isPrimitiveType()) {
				return true;
			}
			else {
				return false;
			}
		}
    }
    public static EntityColumn getColumn(
        String name, List<EntityColumn> columnList) {
        int pos = columnList.indexOf(new EntityColumn(name));
        if (pos == -1) {
                throw new RuntimeException("Column " + name + " not found");
        }
        return columnList.get(pos);
    }
}
