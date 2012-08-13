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
package com.krawler.spring.documents;

import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author Karthik
 */
public interface documentDAO {

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getDocuments(HashMap<String, Object> requestParams) throws ServiceException;

    /**
     * @param fileItems
     * @param arrParam
     * @param fi
     * @param fileUpload
     * @throws ServiceException
     */
    public void parseRequest(List fileItems, HashMap<String, String> arrParam, ArrayList<FileItem> fi, boolean fileUpload) throws ServiceException;

    /**
     * @param fi
     * @param userid
     * @param companyId
     * @param servletContext
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject uploadFile(FileItem fi, String userid, String companyId, javax.servlet.ServletContext servletContext ) throws ServiceException;


    public KwlReturnObject saveFileWithDocEntry(ByteArrayOutputStream baos, String userid, String companyId, String fileName,
            javax.servlet.ServletContext servletContext ) throws ServiceException;

    /**
     * @param jobj
     * @throws ServiceException
     */
    public void saveDocumentMapping(JSONObject jobj) throws ServiceException;

    /**
     * @param id
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject downloadDocument(String id) throws ServiceException;

    /**
     * @param requestParams
     * @param usersList
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getDocumentList(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject addTag(HashMap<String, Object> requestParams) throws ServiceException;

    /**
     * @param queryParams
     * @param allflag
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getDocumentsForTable(HashMap<String, Object> queryParams, boolean allflag) throws ServiceException;

    /**
     * @param docid
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject deletedocument(String docid) throws ServiceException;

    public KwlReturnObject getReloadDocumentLuceneIndex()  throws ServiceException;
    public boolean checkReloadDocumentIndex();
    public void resetDocumentIndexFlag();

	String getDocUploadedCustomername(String docid);
    public KwlReturnObject getDocumentOwners(String docid) throws ServiceException;
    public KwlReturnObject saveDocOwners(HashMap<String, Object> requestParams) throws Exception ;
    public KwlReturnObject getSharedDocumentList(HashMap<String, Object> requestParams,List ll, String userid, StringBuffer docIds) throws ServiceException;
    public void insertDocumentOwnerEntry(String id, String userid, String docid) throws ServiceException;
    public KwlReturnObject deleteDocumentFromModule(String docid ) throws ServiceException;
}
