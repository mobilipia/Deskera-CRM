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
import com.krawler.crm.savedsearch.dao.SavedSearchDAO;
import java.util.List;
import java.util.Date;
/**
 * @author krawler
 *
 */
public class SavedSearchServiceImpl implements SavedSearchService {

    private SavedSearchDAO SaveSearchDAO;


    public SavedSearchDAO getSaveSearchDAO() {
        return SaveSearchDAO;
    }

    public void setSaveSearchDAO(SavedSearchDAO SaveSearchDAO) {
        this.SaveSearchDAO = SaveSearchDAO;
    }

    @Override
    public List<SavedSearchQuery> getSavedSearchQueries(String userId,int firstResult,int maxResults) throws ServiceException {
        List<SavedSearchQuery> ll = SaveSearchDAO.getSavedSearchQueries(userId,firstResult,maxResults);
        return ll;
    }
    
    @Override
    public SavedSearchQuery getSavedSearchQuery(String searchId) throws ServiceException {
        SavedSearchQuery as = SaveSearchDAO.getSavedSearchQuery(searchId);
        return as;
    }
    
    @Override
    public boolean  deleteSavedSearchQuery(String searchId) throws ServiceException {
        return SaveSearchDAO.deleteSavedSearchQuery(searchId);
    }
    @Override
    public int getSavedSearchQueries(String userId) throws ServiceException {
        return SaveSearchDAO.getSavedSearchQueries(userId).size();
    }

    public List<SavedSearchQuery> getSavedSearchQueries(String userid,String searchname) throws ServiceException {
        List<SavedSearchQuery> ll=null;
        return ll = SaveSearchDAO.getSavedSearchQuery(userid, searchname);
    }
    @Override
    public SavedSearchQuery saveSearchQuery(int module, String userId, String SEARCH_QUERY, String searchname) throws ServiceException {
        SavedSearchQuery SavedSearchQueryObj = null;
        try {
            List<SavedSearchQuery> ll = SaveSearchDAO.getSavedSearchQuery(userId, searchname);
            SavedSearchQueryObj = ll.isEmpty()?new SavedSearchQuery():ll.get(0);
            SavedSearchQueryObj.setModuleid(module);
            SavedSearchQueryObj.setUser(SaveSearchDAO.getUser(userId));
            SavedSearchQueryObj.setSearchquery(SEARCH_QUERY);
            SavedSearchQueryObj.setSearchName(searchname);
            SavedSearchQueryObj.setUpdatedon(new Date());
            SavedSearchQueryObj = SaveSearchDAO.saveSearchQuery(SavedSearchQueryObj);
        } catch (Exception e) {
            throw ServiceException.FAILURE("SaveSearchServiceImpl.saveSearchQuery : " + e.getMessage(), e);
        }
        return SavedSearchQueryObj;
    }
}
