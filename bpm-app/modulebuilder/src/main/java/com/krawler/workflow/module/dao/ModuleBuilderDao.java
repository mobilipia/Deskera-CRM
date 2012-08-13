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

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.krawler.common.service.ServiceException;
import java.text.DateFormat;

/**
 *
 * @author Vishnu Kant Gupta
 */
public interface ModuleBuilderDao {

    String buildModule(String modulename,String tablename);
    String getModuleRecords(Object moduleObj, DateFormat formatter) throws ServiceException;
    String getModuleRecords(String moduleid, Object moduleObj, List<ModuleClause> clauses, DateFormat formatter) throws ServiceException;
    String createNewRecord(Object moduleObj) throws ServiceException;
    String updateRecord(Object moduleObj, String key) throws ServiceException;
    String createNewRecord(Map arrParams, List fi, String moduleid) throws ServiceException;
    String deleteRecord(String key, String id, String moduleid) throws ServiceException;
    String editRecord(Map arrParams, List fi, String key, String moduleid) throws ServiceException;
    String getAttachment(HttpServletRequest request) throws ServiceException;
    String uploadFile(HttpServletRequest request) throws ServiceException;
    String getTableColumn(String parameter) throws ServiceException;
    String saveForm(String formid,String moduleid,String formjson, String parentmodule, String jsondata, String tbar, String bbar) throws SQLException,ServiceException;
    String getAllForms(String moduleid) throws IOException, ServiceException;
    String deleteForm(String formid) throws ServiceException;
    String getAllModules(String ss, String sort, String dir, int start, int limit) throws ServiceException;
    String createModule(HttpServletRequest request) throws ServiceException;
    String getAllModulesForCombo(HttpServletRequest request) throws ServiceException;
    String moduleData(String moduleid) throws ServiceException;
    String deleteModule(String moduleid);
    String getGridData(HttpServletRequest request) throws ServiceException;
    String getModuleConfig(String moduleid) throws ServiceException;
    String saveModuleGridConfig(String jsonstr) throws ServiceException;
    String getComboField(String moduleid)throws ServiceException;
    String getComboData(HttpServletRequest request) throws ServiceException;
    String openSubModules(String basemode, String moduleid, String reportid, String taskid) throws ServiceException;
    String getOtherModules(String moduleid, String mode) throws ServiceException;
    String configSubtabModules(String basemodule, String submodule, String mode, String columnname);
    String getStdCongifType(String reportid)throws ServiceException;
    String getModuleCongifType(int configId, String add, String reportid, String deleteconfig)throws ServiceException;
    String getReadOnlyFields(String moduleid,String[] appenid, List<ModuleClause> clauses, DateFormat formatter)throws ServiceException;
    String getModuleForCombo(HttpServletRequest request)throws ServiceException;
    String editModule(HttpServletRequest request) throws ServiceException;
    String getAllModules1(String moduleid) throws ServiceException;
    String getformWithParentvalue(String parentmodule,String childmodule,String modulevar)throws ServiceException;
    String getComboData(String moduleid, String fieldname) throws ServiceException;
    String getPortletData(HttpServletRequest request) throws ServiceException;
    
    /**
     * API for undeploying a module
     * 
     * @param moduleId module Id
     */
    void undeployModule(String moduleId);

    public List getModuleInfo(String moduleid);
    
    Object get(Class entityClass, Serializable id);
}
