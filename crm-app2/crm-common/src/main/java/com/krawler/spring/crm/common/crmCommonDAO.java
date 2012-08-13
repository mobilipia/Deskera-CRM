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
package com.krawler.spring.crm.common;

import com.krawler.common.admin.FieldComboData;
import java.util.HashMap;
import java.util.List;

import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Karthik
 */
public interface crmCommonDAO {

    // Organisation Chart
    public boolean isPerfectRole(String parentid, String childid) throws ServiceException;
    // Organisation Chart
    public KwlReturnObject updateNode(HashMap<String, Object> requestParams) throws Exception;
    // Organisation Chart
    public KwlReturnObject insertNode(HashMap<String, Object> requestParams) throws Exception;

    public KwlReturnObject deleteNode(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject getAssignManagers(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject getColumnHeader(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject getDefaultColumnHeader(HashMap<String, Object> requestParams) throws ServiceException;

    KwlReturnObject getCustomColumnHeader(String reportid, String companyid) throws ServiceException;

    KwlReturnObject getDashboardReportConfig(ArrayList<String> filter_names, ArrayList<Object> filter_params) throws ServiceException;

    void setDashboardReportConfig(ArrayList<Object> filter_params, boolean editFlag) throws ServiceException;

    public KwlReturnObject saveColumnHeader(HashMap<String, Object> requestParams) throws ServiceException;

    public KwlReturnObject getDefaultMasterItem(HashMap<String, Object> requestParams) throws ServiceException;
    public HashMap<String, DefaultMasterItem> getDefaultMasterItemsMap(Map<String, Object> requestParams) throws ServiceException;
    public HashMap<String, FieldComboData> getCustomComboItemsMap(Map<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject validaterecords(String module, String companyid) throws ServiceException;
    public KwlReturnObject validaterecord(String module, String id, String companyid) throws ServiceException;
    public KwlReturnObject getMappingHeaders(HashMap<String, Object> requestParams,String companyid) throws ServiceException ;
    public KwlReturnObject getMappedHeaders(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject saveMappedheaders(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject createDefaultMapping(String companyid) throws ServiceException;

    public void ValidateMassupdate(String[] arrayId, String modulename, String companyid);
    public void ValidateMassupdate(String modulename, String companyid);

    public KwlReturnObject isDuplicatecolumn(HashMap<String, Object> requestParams)  throws ServiceException ;

    public KwlReturnObject validaterecorsingledHB(String module, String leadid, String companyid)throws ServiceException;

    List getAllMandatoryColumnHeader(HashMap<String, Object> requestParams,String DefaultHeaderColumnList0,String ColumnHeaderColumnList1) throws ServiceException ;
    /**
     *
     * @param requestParams
     * @return List contains - List 1 of defaultHeader and List 2 of columnHeader
     * @throws com.krawler.common.service.ServiceException
     */
    public List getAllMandatoryColumnHeader(HashMap<String, Object> requestParams) throws ServiceException;
    
    /**
     * @param modulename
     * @param companyid
     * @param newheader
     * @param headerid
     */
    public boolean columnExists(String modulename, String companyid, String newheader, String headerid);
    public List getLeadSourceId(String campaignid);
    public int  chekcDuplicateEntryForLead(String firstname, String lastname, String email, String companyid) throws ServiceException;
    public KwlReturnObject getCustomReport(HashMap<String, Object> requestParams)  throws ServiceException ;

}
