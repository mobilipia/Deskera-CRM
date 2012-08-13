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
package com.krawler.spring.crm.activityModule;

import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;

/**
 *
 * @author Karthik
 */
public interface crmActivityReportDAO {

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject accountActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject leadActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject contactActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject opportunityActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject caseActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject campaignActivities(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param name
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getHighPriorityActivityChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;
}
