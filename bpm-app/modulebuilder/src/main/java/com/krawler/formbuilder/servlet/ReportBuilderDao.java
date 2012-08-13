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

package com.krawler.formbuilder.servlet;

import com.krawler.common.service.ServiceException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Vishnu Kant Gupta
 */
public interface ReportBuilderDao {
    void staticEntries() throws ServiceException;
    boolean isReferred(String formTableName, String moduleid) throws ServiceException;
    String getReportIdFromTable(String tName) throws ServiceException;
    String getTableColumn(String parameter) throws ServiceException;
    String getTables() throws ServiceException;
    String createNewReport(HttpServletRequest request) throws ServiceException;
    String getReportsList(HttpServletRequest request) throws ServiceException;
    String saveReportGridConfig(String jsondata, String reportid, boolean flag, String tbar, String bbar) throws ServiceException;
    String reportData(HttpServletRequest request) throws ServiceException;
    String reportConfig(HttpServletRequest request) throws ServiceException;
    String deleteReport(HttpServletRequest request) throws ServiceException;
    String insertReportData(HttpServletRequest request) throws ServiceException;
    String loadComboStore(HttpServletRequest request) throws ServiceException;
    String getReportData(HttpServletRequest request) throws ServiceException;
    String updateReportData(HttpServletRequest request) throws ServiceException;
    String deleteReportRecord(HttpServletRequest request) throws ServiceException;
    String insertComment(HttpServletRequest request) throws ServiceException;
    String createRenderer(HttpServletRequest request) throws ServiceException;
    String getRendererFunctions(HttpServletRequest request) throws ServiceException;
    String editRenderer(HttpServletRequest request) throws ServiceException;
    String moduleGridData(HttpServletRequest request) throws ServiceException;
    String getComments(HttpServletRequest request) throws ServiceException;
    String deleteComment(HttpServletRequest request) throws ServiceException;
    String getUsers(HttpServletRequest request) throws ServiceException;
    String getModuleDisplayFields(HttpServletRequest request) throws ServiceException;
    String getAllLinkGroups(HttpServletRequest request) throws ServiceException;
    String getAllLinks(HttpServletRequest request) throws ServiceException;
    String getAllPortlets(HttpServletRequest request) throws ServiceException;
    String getAllModules(HttpServletRequest request) throws ServiceException;
    String storeDashboardConf(HttpServletRequest request) throws ServiceException;
    String getDashboardLinks(HttpServletRequest request) throws ServiceException;
    String getDashboardGroupLinks(HttpServletRequest request) throws ServiceException;
    String getReportDetails(HttpServletRequest request) throws ServiceException;
    String getDashboardData(HttpServletRequest request) throws ServiceException;
    String storeStortcutConf(HttpServletRequest request) throws ServiceException;
    String getColumns(HttpServletRequest request) throws ServiceException;
    String createComboFilterConfig(HttpServletRequest request) throws ServiceException;
    String getComboFiltersConfig(HttpServletRequest request) throws ServiceException;
    String deleteComboFilterConfig(HttpServletRequest request) throws ServiceException;
    String comboFilterConfig(HttpServletRequest request) throws ServiceException;
    String comboFilterData(HttpServletRequest request) throws ServiceException;
    String getModuleFieldsForFilter(HttpServletRequest request) throws ServiceException;
    String getAllProcesses(HttpServletRequest request) throws ServiceException;
    String getReportTableName(String reportid) throws ServiceException;
    void storeToolbarConf(String moduleid, String tbar, String bbar) throws ServiceException;
    String getButtonConf(String reportid) throws ServiceException;
    public String getModulePermission(String moduleid) throws ServiceException;
    public String checkCompanyMBPermission(String companyid) throws ServiceException;
    String toLZ(int i, int len);

    String applicationid = "01";
}
