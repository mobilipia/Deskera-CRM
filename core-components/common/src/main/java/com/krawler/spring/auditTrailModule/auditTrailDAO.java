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
package com.krawler.spring.auditTrailModule;
import com.krawler.common.admin.AuditTrail;
import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface auditTrailDAO {

    /**
     * @param actionid
     * @param details
     * @param request
     * @param recid
     * @throws ServiceException
     */
    public void insertAuditLog(String actionid, String details, HttpServletRequest request, String recid) throws ServiceException ;

    /**
     * @param actionid
     * @param details
     * @param request
     * @param recid
     * @param extraid
     * @throws ServiceException
     */
    public void insertAuditLog(String actionid, String details, HttpServletRequest request, String recid, String extraid)  throws ServiceException;

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getRecentActivityDetails(HashMap<String, Object> requestParams) throws ServiceException;

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAuditDetails(HashMap<String, Object> requestParams) throws ServiceException;

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAuditData(HashMap<String, Object> requestParams) throws ServiceException;

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAuditIndexData(HashMap<String, Object> requestParams) throws ServiceException;

    public ArrayList<String> getAuditIndexNamesList();
    public ArrayList<String> getAuditSortIndexList();
    public ArrayList<Object> getAuditIndexValuesList(AuditTrail auditTrail);
    public boolean doReloadAuditIndex();
    public void resetAuditIndexFlag();
    /**
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAuditGroupData() throws ServiceException;

    /**
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject reloadLuceneIndex()  throws ServiceException;
    public KwlReturnObject reloadLuceneIndex(int start , int limit)  throws ServiceException;

    /**
     * @param actionid
     * @param details
     * @param ipAddress
     * @param userid
     * @param recid
     * @throws ServiceException
     */
    void insertAuditLog(String actionid, String details, String ipAddress, String userid, String recid)  throws ServiceException;

    List<KwlReturnObject> getAuditDetails(List<Map<String, Object>> requestParamsList, int start, int limit, int interval, String companyId) throws ServiceException;

    public void indexAuditLogEntry(AuditTrail auditTrail);

}
