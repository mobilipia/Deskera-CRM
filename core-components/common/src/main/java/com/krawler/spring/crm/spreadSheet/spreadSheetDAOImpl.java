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
package com.krawler.spring.crm.spreadSheet;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.krawler.common.admin.SpreadSheetConfig;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.dao.BaseDAO;

public class spreadSheetDAOImpl extends BaseDAO implements spreadSheetDAO {   
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.spreadSheet.spreadSheetDAO#getSpreadsheetConfig(java.lang.String, java.lang.String)
     */
    public SpreadSheetConfig getSpreadsheetConfig(String module, String userId) throws ServiceException {
        SpreadSheetConfig config = null;
        try {
            String hql = "from SpreadSheetConfig where module=? and user.userID=? ";
            List<SpreadSheetConfig> results = executeQuery(hql, new Object[]{module, userId});
            
            if (results != null && !results.isEmpty())
            {
                config = results.get(0);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.getSpreadsheetConfig : "+e.getMessage(), e);
        }
        return config;
    }
    
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.spreadSheet.spreadSheetDAO#getSpreadsheetConfig(java.lang.String)
     */
    public List getSpreadsheetConfig(String userid) throws ServiceException {
        List ll =null;
        try {
            String hql = "from SpreadSheetConfig where user.userID=? ";
            ll = executeQuery(hql, new Object[]{userid});
        } catch (Exception e) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.getSpreadsheetConfig : "+e.getMessage(), e);
        }
        return ll;
    }
    
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.spreadSheet.spreadSheetDAO#saveSpreadsheetConfig(com.krawler.utils.json.base.JSONObject)
     */
    public void saveSpreadsheetConfig(SpreadSheetConfig config, String userid) throws ServiceException {
        try {
        	config.setUser((User)get(User.class,userid));
            saveOrUpdate(config);
        } catch (Exception e) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveSpreadsheetConfig : "+e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.spreadSheet.spreadSheetDAO#saveModuleRecordStyle(com.krawler.utils.json.base.JSONObject)
     */
    public void saveModuleRecordStyle(String id, String className, String cellStyle) throws ServiceException {
        try{
            Class cl = Class.forName(className);
            Object invoker = get(cl, id);
            if(invoker != null) {
                Field  field= invoker.getClass().getDeclaredField("cellstyle");
                Class type = field.getType();
                Class  arguments[] = new Class[] {type};
                java.lang.reflect.Method objMethod;
                objMethod = cl.getMethod("setCellstyle", arguments);
                Object[] obj = new Object[]{cellStyle};
                Object result1 = objMethod.invoke(invoker, obj);
                saveOrUpdate(invoker);
            }
        } catch (IllegalAccessException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (NoSuchFieldException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (ClassNotFoundException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (NoSuchMethodException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        } catch (SecurityException ex) {
            throw ServiceException.FAILURE("spreadSheetDAOImpl.saveModuleRecordStyle : "+ex.getMessage(), ex);
        }
    }
}
