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
import com.krawler.common.util.StringUtil;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.BeanMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.dialect.Dialect;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * @author Ashutosh
 *
 */
public class DataObjectOperationDAOImpl extends JdbcDaoSupport implements DataObjectOperationDAO
{
    
    private static Log LOG = LogFactory.getLog(DataObjectOperationDAOImpl.class);

    private Dialect dialectObject;

    public Dialect getDialectObject() {
        return dialectObject;
    }

    public void setDialectObject(Dialect dialectObject) {
        this.dialectObject = dialectObject;
    }

    private static Map<String, String> tableNamesMap = new HashMap<String, String>();
    
    private static Map<String, DataObjectRowMapper> rowMapperMap = new HashMap<String, DataObjectRowMapper>();

    /* (non-Javadoc)
     * @see com.krawler.workflow.dao.module.DataObjectOperationDAO#createDataObject(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean createDataObject(Object dataObject)
    {
        boolean result = true;
        
        try
        {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(getJdbcTemplate()).withTableName(getTableName(dataObject.getClass().getSimpleName()));
            insert.execute(new BeanMap(dataObject));
        }
        catch (Exception e)
        {
            LOG.warn("Can not insert record", e);
            result = false;
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.dao.module.DataObjectOperationDAO#deleteDataObject(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean deleteDataObject(Object dataObject, String keyName)
    {
        boolean result = true;
        StringBuilder deleteQuery = new StringBuilder("DELETE from ");
        deleteQuery.append(getTableName(dataObject.getClass().getSimpleName()));
        deleteQuery.append(" where ");
        deleteQuery.append(keyName);
        deleteQuery.append(" = ?");
        
        try
        {
            Object id = BeanUtils.getProperty(dataObject, keyName);
            getJdbcTemplate().update(deleteQuery.toString(), new Object[]{id});
        }
        catch (DataAccessException e)
        {
            LOG.warn("Can not delete record", e);
            result = false;
        } catch (Exception e)
        {
            LOG.warn("Can not delete record", e);
            result = false;
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.dao.module.DataObjectOperationDAO#deleteDataObject(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean deleteDataObject(String objName, String pkeyName, String id)
    {
        boolean result = true;
        StringBuilder deleteQuery = new StringBuilder("DELETE from ");
        deleteQuery.append(getTableName(objName));
        deleteQuery.append(" where ");
        deleteQuery.append(pkeyName);
        deleteQuery.append(" = ?");
        
        try
        {
            getJdbcTemplate().update(deleteQuery.toString(), new Object[]{id});
        }
        catch (DataAccessException e)
        {
            LOG.warn("Can not delete record", e);
            result = false;
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.dao.module.DataObjectOperationDAO#getAllDataObjects(java.lang.String, java.lang.Class)
     */
    @Override
    public List getAllDataObjects(String objName, Class classObj)
    {
        List results = null;
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(getTableName(objName));
        try
        {
            results = getJdbcTemplate().query(query.toString(), new BeanPropertyRowMapper(classObj));
        }
        catch (DataAccessException e)
        {
            LOG.warn("Can not fetch all records", e);
        }
            
        return results;
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.dao.module.DataObjectOperationDAO#getDataObjectById(java.lang.String, java.lang.String, java.lang.String, java.lang.Class)
     */
    @Override
    public Object getDataObjectById(Object dataObject, String pKeyName)
    {
        Object result = null;
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(getTableName(dataObject.getClass().getSimpleName()));
        query.append(" where ");
        query.append(pKeyName);
        query.append(" = ?");
        
        try
        {
            Object id = BeanUtils.getProperty(dataObject, pKeyName);
            result = getJdbcTemplate().queryForObject(query.toString(), new Object[]{id}, new BeanPropertyRowMapper(dataObject.getClass()));
        }
        catch (DataAccessException e)
        {
            LOG.warn("Can not fetch all records", e);
        } catch (Exception e)
        {
            LOG.warn("Can not fetch all records", e);
        }
            
        return result;
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.dao.module.DataObjectOperationDAO#updateDataObject(java.lang.Object, java.lang.String)
     */
    @Override
    public boolean updateDataObject(Object dataObject, String keyName)
    {
        boolean result = true;

        try {
            StringBuilder query = new StringBuilder("UPDATE "+getTableName(dataObject.getClass().getSimpleName())+" SET ");
            BeanMap bm = new BeanMap(dataObject);
            ArrayList arr = new ArrayList();
            Iterator itr = bm.keyIterator();
            
            while(itr.hasNext()){
                String key = (String)itr.next();
                if(key.equals(keyName)||"class".equals(key))
                    continue;
                query.append(key).append("=?");
                arr.add(bm.get(key));

                if(itr.hasNext())
                        query.append(",");               
            }

            query.append(" WHERE ").append(keyName).append(" = ?");
            arr.add(bm.get(keyName));
            getJdbcTemplate().update(query.toString(), arr.toArray());
        } catch (Exception e) {
            LOG.warn("Can not update record", e);
            result = false;
        }

        return result;
    }
    @Override
    public boolean  createModuleTable(String tablename,List<ModuleProperty> fields){

        //FIXME: USE Dialect Class to create sql for create table query
        String createTableQuery = "CREATE TABLE "+tablename+" (";
        boolean result = false;
        StringBuilder buildCreateQuery = new StringBuilder(createTableQuery);
        String primaryKeyStr = "";
        

        List<ModuleProperty> foreignKeyFields = new ArrayList<ModuleProperty>();
            StringBuilder foreignKeyStr = new StringBuilder();
            int i = 0;
         for(ModuleProperty moduleField : fields){
            if(i>0){
            buildCreateQuery.append(" , ");
            }
            buildCreateQuery.append(moduleField.getAttributeString(dialectObject));
            if(moduleField.isForeignKey()){
               foreignKeyFields.add(moduleField);
            }
            if(moduleField.isPrimaryKey()){
                primaryKeyStr =ALTER_TABLE_DECLARATION +tablename+" ";
                primaryKeyStr += dialectObject.getAddPrimaryKeyConstraintString(PRIMARY_KEY_PREFIX+tablename);
                primaryKeyStr += " (`"+moduleField.getFieldName()+"`) ";
               
             }
            i++;
        }
         buildCreateQuery.append(")");
         buildCreateQuery.append(dialectObject.getTableTypeString());
         buildCreateQuery.append(";");
         getJdbcTemplate().execute(buildCreateQuery.toString());
        if(!StringUtil.isNullOrEmpty(primaryKeyStr)){
            getJdbcTemplate().execute(primaryKeyStr);
        }
        for(ModuleProperty moduleField :  foreignKeyFields){

            String fkQuery = ALTER_TABLE_DECLARATION +tablename+" ";
            fkQuery += dialectObject.getAddForeignKeyConstraintString("", new String[]{moduleField.getFieldName()}, moduleField.getRefTable(), new String[]{moduleField.getRefField()}, true);
            getJdbcTemplate().execute(fkQuery);

        }
      


//
//        for(int i=0;i<fields.length();i++){
//            try {
//                if(i>0){
//                    buildCreateQuery.append(" , ");
//                }
//                String str = fields.optString(i);
//                JSONObject jobj = new JSONObject(str);
//                buildCreateQuery.append(jobj.optString("name"));
//                buildCreateQuery.append(" ");
//                String strType = jobj.optString("type");
//
//                if(strType.equalsIgnoreCase("String")){
//                    if(jobj.has("maxlength")){
//                        strType = "varchar("+jobj.optString("maxlength", "255")+")";
//                    }else{
//                        strType = DEFAULT_VARCHAR_DECLARATION;
//                    }
//                }
//                buildCreateQuery.append(strType);
//                if (jobj.has("default")) {
//                    buildCreateQuery.append(" ");
//                    buildCreateQuery.append(jobj.optString("default"));
//                }
//                if (jobj.has("primaryid") && !primaryKeyflag) {
//                    buildCreateQuery.append(" ");
//                    if(jobj.optString("type").equals("int")){
//                        buildCreateQuery.append(" ");
//                        buildCreateQuery.append(AUTO_INCREMENT_DECLARATION);
//                    }
//                    buildCreateQuery.append(" ");
//                    buildCreateQuery.append(PRIMARY_KEY_DECLARATION);
//
//                    primaryKeyflag = true;
//                }
//                if(jobj.has("foreignid")){
//                    foreignKeyStr.append(CONSTRAINT_DECLARATION);
//                    foreignKeyStr.append(FOREIGN_KEY_PREFIX);
//                    foreignKeyStr.append(jobj.optString("name"));
//                    foreignKeyStr.append(" ");
//                    foreignKeyStr.append(FORIEGN_KEY_DECLARATION);
//                    foreignKeyStr.append("(");
//                    foreignKeyStr.append(jobj.optString("name"));
//                    foreignKeyStr.append(") ");
//                    foreignKeyStr.append(REFERENCE_DECLARATION);
//                    foreignKeyStr.append(jobj.optString("table"));
//                    foreignKeyStr.append("(");
//                    foreignKeyStr.append(jobj.optString("id"));
//                    foreignKeyStr.append(") ");
//                }
                
//            } catch (JSONException ex) {
//                Logger.getLogger(DataObjectOperationDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        }
        //buildCreateQuery.append(foreignKeyStr);
        
        result= true;
        return result;
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.module.dao.DataObjectOperationDAO#createDataObject(java.lang.String, java.util.Map)
     */
    public boolean dropTable(String tableName){
        boolean result = false;
        try{
            getJdbcTemplate().execute("drop table "+tableName);
            result = true;
        }catch(Exception ex){
            LOG.error("Cannot Drop Table - "+ex);
        }
        
        return result;
    }
    public boolean createDataObject(String objName, Map<String, Object> dataObject)
    {
        boolean result = true;
        
        try
        {
            SimpleJdbcInsert insert = new SimpleJdbcInsert(getJdbcTemplate()).withTableName(getTableName(objName));
            insert.execute(dataObject);
        }
        catch (Exception e)
        {
            LOG.warn("Can not insert record", e);
            result = false;
        }
        
        return result;
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.module.dao.DataObjectOperationDAO#getAllDataObjects(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAllDataObjects(String objName, DateFormat formatter)
    {
        List<Map<String, Object>> results = null;
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(getTableName(objName));
        try
        {
            results = getJdbcTemplate().query(query.toString(), getRowMapper(objName, formatter));
        }
        catch (DataAccessException e)
        {
            LOG.warn("Can not fetch all records", e);
        }
            
        return results;
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.module.dao.DataObjectOperationDAO#getDataObjectMapById(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDataObjectMapById(String objName, String keyName, Object pId)
    {
        Map<String, Object> result = null;
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(getTableName(objName));
        query.append(" where ");
        query.append(keyName);
        query.append(" = ?");
        
        try
        {
            result = (Map<String, Object>) getJdbcTemplate().queryForObject(query.toString(), new Object[]{pId}, getRowMapper(objName));
        }
        catch (DataAccessException e)
        {
            LOG.warn("Can not fetch the record with id:" + pId, e);
        } catch (Exception e)
        {
            LOG.warn("Can not fetch the record with id:" + pId, e);
        }
            
        return result;
    }

    /* (non-Javadoc)
     * @see com.krawler.workflow.module.dao.DataObjectOperationDAO#updateDataObject(java.lang.String, java.lang.String, java.util.Map)
     */
    public boolean updateDataObject(String objName, String keyName, Map<String, Object> dataObject)
    {
        Object pId = null;
        boolean result = true;
        Object[] args = new Object[dataObject.size()];
        String query = buildUpdateQuery(objName, keyName, dataObject, args);
        
        try
        {
            getJdbcTemplate().update(query, args);
        }
        catch (DataAccessException e)
        {
            LOG.warn("Can not update the record with id:" + pId + " in table:" + objName, e);
            result = false;
        } catch (Exception e)
        {
            LOG.warn("Can not update the record with id:" + pId + " in table:" + objName, e);
            result = false;
        }
            
        return result;
    }

    private String buildUpdateQuery(String objName, String keyName, Map<String, Object> dataObject, Object[] args)
    {
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(getTableName(objName));
        query.append(" set ");
        
        Set<Entry<String, Object>> entrySet = dataObject.entrySet();
        
        if (!entrySet.isEmpty())
        {
            int i = 0;
            for (Entry<String, Object> entry: entrySet)
            {
                if (keyName.equals(entry.getKey()))
                {
                    args[args.length - 1] = entry.getValue();
                    continue;
                }
                query.append(entry.getKey());
                {
                    query.append("= ?,");
                }
                args[i++] = entry.getValue();
            }
            
            query.deleteCharAt(query.length() - 1);
        }
        
        query.append(" where ");
        query.append(keyName);
        query.append(" = ?");
        return query.toString();
    }
    
    private String getTableName(String objectName)
    {
        if (tableNamesMap.containsKey(objectName))
        {
            return tableNamesMap.get(objectName);
        }
        
        StringBuilder tableName = new StringBuilder();
        if (objectName != null)
        {
            int length = objectName.length();
            for (int i = 0; i < length; i++)
            {
                char c = objectName.charAt(i);
                
                if (c >= 'A' && c <= 'Z')
                {
                    if (i > 0)
                    {
                        tableName.append('_');
                    }
                    tableName.append(c);
                }
                else
                {
                    tableName.append(c);
                }
            }
        }
        String tableNameStr = tableName.toString().toLowerCase();
        tableNamesMap.put(objectName, tableNameStr);
        return tableNameStr;
    }

    private DataObjectRowMapper getRowMapper(String objName)
    {
        DataObjectRowMapper mapper = null;
        if (rowMapperMap.containsKey(objName))
        {
            mapper = rowMapperMap.get(objName);
        }
        else
        {
            mapper = new DataObjectRowMapper();
            rowMapperMap.put(objName, mapper);
        }
        
        return mapper;
    }

    private DataObjectRowMapper getRowMapper(String objName, DateFormat formatter)
    {
        DataObjectRowMapper mapper = null;
        if (rowMapperMap.containsKey(objName))
        {
            mapper = rowMapperMap.get(objName);
        }
        else
        {
            mapper = new DataObjectRowMapper(formatter);
            rowMapperMap.put(objName, mapper);
        }

        return mapper;
    }
    
    @Override
    public List getAllDataObjects(String objName, Class classObj, List<ModuleClause> clauses) {
        List results = null;
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        String tableName = getTableName(objName);
        query.append(tableName);
        List params = new ArrayList();
        query.append(buildClause(tableName, clauses, params));
        try
        {
            results = getJdbcTemplate().query(query.toString(), params.toArray(), new BeanPropertyRowMapper(classObj));
        }
        catch (DataAccessException e)
        {
            LOG.warn("Can not fetch all records", e);
        }

        return results;
    }
    
    private String buildClause(String tableName, List<ModuleClause> clauses, List params) {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<clauses.size();i++){
            ModuleClause mc = clauses.get(i);
            if(sb.length()>0)
                sb.append(" AND ");
            else
                sb.append(" WHERE ");
            sb.append(tableName).append('.').append(mc.getFieldName());
            sb.append(' ').append(mc.getOperation()).append(' ');
            sb.append('?');
            params.add(mc.getValue());
        }
        return sb.toString();
    }
    
    @Override
    public List getAllDataObjects(String objName, List<ModuleClause> clauses, DateFormat formatter) {
        List results = null;
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        String tableName = getTableName(objName);
        query.append(tableName);
        ArrayList params = new ArrayList();
        query.append(buildClause(tableName, clauses, params));
        try
        {
            results = getJdbcTemplate().query(query.toString(), params.toArray(), getRowMapper(objName, formatter));
        }
        catch (DataAccessException e)
        {
            LOG.warn("Can not fetch all records", e);
        }

        return results;
    }
}
