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
package com.krawler.spring.comments;

import com.krawler.common.admin.Comment;
import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Karthik
 */
/**
 * @author krawler
 *
 */
/**
 * @author krawler
 *
 */
public interface commentDAO {

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getComments(HashMap requestParams) throws ServiceException;
    public KwlReturnObject getCaseComments(HashMap requestParams) throws ServiceException;
    public Object[] getCustomerName(String contactid) throws ServiceException;

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public Object getCommentsCountForRecord(HashMap requestParams) throws ServiceException;

    public HashMap<String, String> getTotalCommentsCount(List<String> recordIds, String companyid) throws ServiceException;
    public HashMap<String, String> getTotalCaseCommentsCount(List<String> list, String companyid) throws ServiceException;

    /**
     * @param userid
     * @param id
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject deleteComments(String userid, String id) throws ServiceException;

    public KwlReturnObject deleteOriginalComment(String id) throws ServiceException;
    public KwlReturnObject deleteCaseComment(String id) throws ServiceException;

    /**
     * @param jobj
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject addComments(JSONObject jobj) throws ServiceException;
    public KwlReturnObject addCaseComments(JSONObject jobj) throws ServiceException;

     public KwlReturnObject editComments(JSONObject jobj) throws ServiceException;
     public KwlReturnObject editCaseComments(JSONObject jobj) throws ServiceException;

    /**
     * @param userid
     * @return
     * @throws ServiceException
     */
    public HashMap<String, String> getNewCommentCount(String userid) throws ServiceException;

    /**
     * @param userid
     * @param parentid
     * @return
     * @throws ServiceException
     */
    public JSONObject getAllCommentList(String userid, String parentid) throws ServiceException;

	/**
     * 
     * @param recordId
     * @param newRecordId
     * Create duplicate entries of records with leadId=recordId with leadId=newrecordId 
     * @return
     * @throws ServiceException
     */
    public void CreateDuplicateComments(String recordId,String newRecordId);
  
       public Map<String, List<String>> getCommentz(List<String> recordids);
       public Map<String, List<String>> getCaseCommentz(List<String> recordids);

}
