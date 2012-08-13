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

import java.text.DateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author Ashutosh
 *
 */
public interface DataObjectOperationDAO
{
    String PRIMARY_KEY_DECLARATION = " PRIMARY KEY ";
    String PRIMARY_KEY_PREFIX = "PK_";
    String ALTER_TABLE_DECLARATION = " ALTER TABLE ";
    String AUTO_INCREMENT_DECLARATION = " AUTO_INCEREMENT ";
    String DEFAULT_VARCHAR_DECLARATION = " varchar(255) ";
    String FORIEGN_KEY_DECLARATION = " FORIEGN KEY ";
    String REFERENCE_DECLARATION = " REFERENCES ";
    String CONSTRAINT_DECLARATION = " CONTRAINT ";
    String FOREIGN_KEY_PREFIX = " FK_";
    Object getDataObjectById(Object dataObject, String pKeyName);
    
    List getAllDataObjects(String objName, Class className);
    
    boolean dropTable(String tableName);
    
    boolean createDataObject(Object dataObject);
    
    boolean updateDataObject(Object dataObject, String pKeyName);
    
    boolean deleteDataObject(Object dataObject, String pKeyName);
    
    boolean deleteDataObject(String objName, String pkeyName, String pId);

    boolean createDataObject(String objName, Map<String, Object> dataObject);

    List<Map<String, Object>> getAllDataObjects(String objName, DateFormat formatter);

    Map<String, Object> getDataObjectMapById(String objName, String keyName, Object pId);

    boolean updateDataObject(String objName, String keyName, Map<String, Object> dataObject);

    List getAllDataObjects(String objName, Class className, List<ModuleClause> clauses);
    
    List<Map<String, Object>> getAllDataObjects(String objName, List<ModuleClause> clauses, DateFormat formatter);
    boolean createModuleTable(String tablename,List<ModuleProperty> fields);
}
