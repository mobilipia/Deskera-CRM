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

package com.krawler.crm.reportBuilder.bizservice;

import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CustomReportColumns;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author sagar
 */
public interface ReportBuilderService {
    JSONObject getReportMetadata(JSONObject commData, boolean export, int reportno, StringBuffer searchJson, ArrayList<CustomReportColumns> quickSearchCol, ArrayList<CustomReportColumns> groupCol, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap, boolean detailFlag);
    JSONObject getModules() throws ServiceException;
    JSONObject getModuleColumns(String companyid, String modulename) throws ServiceException;
    JSONObject getRendererFunctions() throws ServiceException;
    JSONObject saveReportConfig(String reportno,String rname,String runiquename,String rdescription,String rcategory,String reportcolumnsetting, String userid, String rfilterjson) throws ServiceException;
    boolean deleteCustomReport(int reportno) throws ServiceException;
    JSONObject getLeadsDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByText, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm,
        String field, String direction, String start, String limit, StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException;
    JSONObject getProductsDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByText, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm,
        String field, String direction, String start, String limit, StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException;
    JSONObject getAccountsDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByText, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm,
        String field, String direction, String start, String limit, StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException;
    JSONObject getContactsDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByText, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm,
        String field, String direction, String start, String limit, StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException;
    JSONObject getOpportunitiesDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByText, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm,
        String field, String direction, String start, String limit, StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException;
    JSONObject getCasesDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByText, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm,
        String field, String direction, String start, String limit, StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException;
    StringBuilder buildSelectQuery(ArrayList<String> field_names);
    StringBuilder buildJoinQuery(ArrayList<String> refTableList, String report_categoty);
}
