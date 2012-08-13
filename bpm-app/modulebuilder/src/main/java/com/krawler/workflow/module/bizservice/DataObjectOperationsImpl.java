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

import java.util.List;
import java.util.Map;

import com.krawler.workflow.module.dao.DataObjectOperationDAO;
import com.krawler.workflow.module.dao.ModuleClause;
import java.text.DateFormat;
import com.krawler.workflow.module.dao.ModuleProperty;

/**
 * @author Ashutosh
 *
 */
public class DataObjectOperationsImpl implements DataObjectOperations
{

    private DataObjectOperationDAO dataObjectOperationDAO;
    
    /* (non-Javadoc)
     * @see com.krawler.bizservice.module.DataObjectOperations#createDataObject(java.lang.Object)
     */
    @Override
    public boolean createDataObject(Object dataObject)
    {
        return getDataObjectOperationDAO().createDataObject(dataObject);
    }

    /* (non-Javadoc)
     * @see com.krawler.bizservice.module.DataObjectOperations#deleteDataObject(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean deleteDataObject(Object dataObject, String keyName)
    {
        return getDataObjectOperationDAO().deleteDataObject(dataObject, keyName);
    }

    /* (non-Javadoc)
     * @see com.krawler.bizservice.module.DataObjectOperations#deleteDataObject(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean deleteDataObject(String objName, String pkeyName, String id)
    {
        return getDataObjectOperationDAO().deleteDataObject(objName, pkeyName, id);
    }

    /* (non-Javadoc)
     * @see com.krawler.bizservice.module.DataObjectOperations#getAllDataObjects(java.lang.String, java.lang.Class)
     */
    @Override
    public List getAllDataObjects(String objName, Class className)
    {
        return getDataObjectOperationDAO().getAllDataObjects(objName, className);
    }

    /* (non-Javadoc)
     * @see com.krawler.bizservice.module.DataObjectOperations#getDataObjectById(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
     */
    @Override
    public Object getDataObjectById(Object dataObject, String pKeyName)
    {
        return getDataObjectOperationDAO().getDataObjectById(dataObject, pKeyName);
    }

    /* (non-Javadoc)
     * @see com.krawler.bizservice.module.DataObjectOperations#updateDataObject(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean updateDataObject(Object dataObject, String keyName)
    {
        return getDataObjectOperationDAO().updateDataObject(dataObject, keyName);
    }
    

    /* (non-Javadoc)
     * @see com.krawler.workflow.module.bizservice.DataObjectOperations#createDataObject(java.lang.String, java.util.Map)
     */
    public boolean createDataObject(String objName, Map<String, Object> dataObject)
    {
        return getDataObjectOperationDAO().createDataObject(objName, dataObject);
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.module.bizservice.DataObjectOperations#getAllDataObjects(java.lang.String)
     */
    /* (non-Javadoc)
     * @see com.krawler.workflow.module.bizservice.DataObjectOperations#getAllDataObjects(java.lang.String)
     */
    public List<Map<String, Object>> getAllDataObjects(String objName, DateFormat formatter)
    {
        return getDataObjectOperationDAO().getAllDataObjects(objName,formatter);
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.module.bizservice.DataObjectOperations#getDataObjectMapById(java.lang.String, java.lang.String)
     */
    public Map<String, Object> getDataObjectMapById(String objName, String keyName, Object pId)
    {
        return getDataObjectOperationDAO().getDataObjectMapById(objName, keyName, pId);
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.module.bizservice.DataObjectOperations#updateDataObject(java.lang.String, java.lang.String, java.util.Map)
     */
    public boolean updateDataObject(String objName, String keyName, Map<String, Object> dataObject)
    {
        return getDataObjectOperationDAO().updateDataObject(objName, keyName, dataObject);
    }

    public DataObjectOperationDAO getDataObjectOperationDAO()
    {
        return dataObjectOperationDAO;
    }

    public void setDataObjectOperationDAO(DataObjectOperationDAO dataObjectOperationDAO)
    {
        this.dataObjectOperationDAO = dataObjectOperationDAO;
    }

    @Override
    public List getAllDataObjects(String objName, Class className, List<ModuleClause> clauses) {
        return getDataObjectOperationDAO().getAllDataObjects(objName, className, clauses);
    }

    @Override
    public boolean createModuleTable(String tablename, List<ModuleProperty> fields) {
        return getDataObjectOperationDAO().createModuleTable(tablename,fields);
    }

    @Override
    public List getAllDataObjects(String objName, List<ModuleClause> clauses, DateFormat formatter) {
        return getDataObjectOperationDAO().getAllDataObjects(objName, clauses, formatter);
    }

    @Override
    public boolean dropTable(String tableName) {
       return getDataObjectOperationDAO().dropTable(tableName);
    }
}
