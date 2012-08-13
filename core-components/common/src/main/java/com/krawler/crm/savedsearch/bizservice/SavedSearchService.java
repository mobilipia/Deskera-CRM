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
package com.krawler.crm.savedsearch.bizservice;

import com.krawler.common.admin.SavedSearchQuery;
import com.krawler.common.service.ServiceException;
import java.util.List;
/**
 * @author Ashutosh
 *
 */
public interface SavedSearchService
{
    /**
     * @param userId
     * @param firstResult
     * @param maxResults
     * @return
     * @throws ServiceException
     */
    List<SavedSearchQuery> getSavedSearchQueries( String userId,int firstResult,int maxResults) throws ServiceException;

    
    /**
     *
     * @param searchId
     * @return
     * @throws ServiceException
     */
    SavedSearchQuery getSavedSearchQuery( String searchId) throws ServiceException;

    /**
     *
     * @param searchId
     * @return
     * @throws ServiceException
     */
    boolean deleteSavedSearchQuery( String searchId) throws ServiceException;

    /**
     *
     * @param userId
     * @return
     * @throws ServiceException
     */
    int getSavedSearchQueries(String userId) throws ServiceException;
             
    /**
     *
     * @param module
     * @param userId
     * @param searchstate
     * @param searchname
     * @return
     * @throws ServiceException
     */
    SavedSearchQuery saveSearchQuery(int module, String userId, String searchstate,String searchname)  throws ServiceException;
    public List<SavedSearchQuery> getSavedSearchQueries(String userid,String searchname) throws ServiceException;
}
