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
package com.krawler.workflow.module.bizservice;

import com.krawler.utils.json.base.JSONArray;
import com.krawler.workflow.module.dao.ModuleClause;
import com.krawler.workflow.module.dao.ModuleProperty;
import java.text.DateFormat;
import java.util.List;
import java.util.Map;

/**
 * @author Ashutosh
 *
 */
public interface DataObjectOperations
{
    Object getDataObjectById(Object dataObject, String pKeyName);
    
    List getAllDataObjects(String objName, Class className);
    
    boolean createDataObject(Object dataObject);
    
    boolean updateDataObject(Object dataObject, String pKeyName);
    
    boolean deleteDataObject(Object dataObject, String pKeyName);
    boolean dropTable(String tableName);
    boolean deleteDataObject(String objName, String pkeyName, String pId);
    boolean createModuleTable(String tablename,List<ModuleProperty> fields);
    /**
     * @param objName
     * @param pKeyName
     * @return
     */
    Map<String, Object> getDataObjectMapById(String objName, String pKeyName, Object pId);
    
    /**
     * @param objName
     * @return
     */
    List<Map<String, Object>> getAllDataObjects(String objName, DateFormat formatter);
    
    /**
     * @param objName
     * @param dataObject
     * @return
     */
    boolean createDataObject(String objName, Map<String, Object> dataObject);
    
    /**
     * @param objName
     * @param pKeyName
     * @param dataObject
     * @return
     */
    boolean updateDataObject(String objName, String pKeyName, Map<String, Object> dataObject);

    List getAllDataObjects(String objName, Class className, List<ModuleClause> clauses);

    List<Map<String, Object>> getAllDataObjects(String objName , List<ModuleClause> clauses, DateFormat formatter);
}
