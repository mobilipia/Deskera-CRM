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

package com.krawler.workflow.module.dao;

import com.krawler.common.util.StringUtil;
import java.sql.Types;
import org.hibernate.dialect.Dialect;

/**
 *
 * @author Shrinivas
 */
public class ModuleProperty {

    private String fieldName;
    private int fieldType;
    private int length;
    private String defaultValue;
    private boolean primaryKey;
    private boolean foreignKey;
    private String refTable;
    private boolean autoIncrement;
    private String refField;

    public String getRefField() {
        return refField;
    }

    public void setRefField(String refField) {
        this.refField = refField;
    }


    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    

    public boolean isForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(boolean foreignKey) {
        this.foreignKey = foreignKey;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getRefTable() {
        return refTable;
    }

    public void setRefTable(String refTable) {
        this.refTable = refTable;
    }
    
    public ModuleProperty() {

    }
    public ModuleProperty(String fieldName, int fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public ModuleProperty(String fieldName, int fieldType, int length) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.length = length;
    }
    public String getAttributeString(Dialect dialectObj){
        StringBuilder attributeString = new StringBuilder();
        attributeString.append(" ");
        attributeString.append(this.fieldName);
        attributeString.append(" ");
        attributeString.append(dialectObj.getTypeName(this.fieldType,(this.length == 0 ? 255 : this.length),4,0));

        if(!StringUtil.isNullOrEmpty(this.defaultValue)){
        attributeString.append(" default ");

        attributeString.append(this.defaultValue);
        }
        
        return attributeString.toString();
    }

   



}
