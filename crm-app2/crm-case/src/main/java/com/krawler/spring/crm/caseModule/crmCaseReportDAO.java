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
package com.krawler.spring.crm.caseModule;

import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;

/**
 * 
 * @author Karthik
 */
public interface crmCaseReportDAO
{

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject caseByStatusReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject monthlyCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject newlyAddedCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject pendingCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject escalatedCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param name
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getCasesByStatusChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param month
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getMonthlyCasesChart(int month, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param name
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getNewlyAddedCasesChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param name
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getPendingCasesChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param name
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getEscalatedCasesChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject contactsWithCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param name
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getContactCaseChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject contactsHighPriorityCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param name
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getContactHighPriorityChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject accountsWithCaseReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject accountHighPriorityCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param id
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAccountCasesChart(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param id
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAccountHighPriorityChart(String id, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return KwlReturnObject
     * @throws com.krawler.common.service.ServiceException
     */
    public KwlReturnObject productCasesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param name
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getProductHighPriorityChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;
}
