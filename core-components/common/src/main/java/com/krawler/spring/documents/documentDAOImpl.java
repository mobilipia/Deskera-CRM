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

import com.krawler.common.admin.Company;
import com.krawler.common.admin.DocOwners;
import com.krawler.common.admin.Docmap;
import com.krawler.common.admin.Docs;
import com.krawler.common.admin.NewComment;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.esp.utils.KrawlerApp;
import com.krawler.luceneSearchService.LuceneSearch;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import org.apache.commons.fileupload.FileItem;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import static com.krawler.esp.utils.LuceneSearchConstants.*;
/**
 *
 * @author Karthik
 */
public class documentDAOImpl extends BaseDAO implements documentDAO {
    private storageHandlerImpl storageHandlerImplObj;
    private LuceneSearch LuceneSearchObj;

    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }

    public void setLuceneSearch(LuceneSearch LuceneSearchObj) {
        this.LuceneSearchObj = LuceneSearchObj;
    }
    
    /* (non-Javadoc)
     * @see com.krawler.spring.documents.documentDAO#getDocuments(java.util.HashMap)
     */
    public KwlReturnObject getDocuments(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        String recid = "";
        try {
            if (requestParams.containsKey("recid") && requestParams.get("recid") != null) {
                recid = requestParams.get("recid").toString();
            }
            String Hql = "select dm.docid FROM com.krawler.common.admin.Docmap dm where dm.recid=?  and dm.docid.deleteflag=0";
            ll = executeQuery(Hql, new Object[]{recid});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("documentDAOImpl.getDocuments : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.documents.documentDAO#parseRequest(java.util.List, java.util.HashMap, java.util.ArrayList, boolean)
     */
    public void parseRequest(List fileItems, HashMap<String, String> arrParam, ArrayList<FileItem> fi, boolean fileUpload) throws ServiceException {

        FileItem fi1 = null;
        for (Iterator k = fileItems.iterator(); k.hasNext();) {
            fi1 = (FileItem) k.next();
            if (fi1.isFormField()) {
                try {
					arrParam.put(fi1.getFieldName(), fi1.getString("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage());
				}
            } else {
                if (fi1.getSize() != 0) {
                    fi.add(fi1);
                    fileUpload = true;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.documents.documentDAO#uploadFile(org.apache.commons.fileupload.FileItem, java.lang.String, javax.servlet.ServletContext)
     */
    public KwlReturnObject uploadFile(FileItem fi, String userid, String companyId, ServletContext servletContext) throws ServiceException {
        Docs docObj = new Docs();
        List ll = new ArrayList();
        int dl = 0;
        try {
            String fileName = new String(fi.getName().getBytes(), "UTF8");
            String Ext = "";
            String a = "";
            int index = fileName.lastIndexOf(".");
            if (index >= 0) {
                String dupExt = fileName.substring(fileName.lastIndexOf("."));
                Ext = fileName.substring(index+1);
                a = Ext.toUpperCase();
                Ext = dupExt;
            }
            
            User userObj;
            if(userid!=null){
            	userObj = (User) get(User.class, userid);
            	docObj.setUserid(userObj);
            }
            docObj.setDocname(fileName);
            docObj.setStorename("");
            docObj.setDoctype(a + " " + "File");
            docObj.setUploadedon(new Date());
            docObj.setStorageindex(1);
            docObj.setDocsize(fi.getSize() + "");

            save(docObj);

            String fileid = docObj.getDocid();
            if (Ext.length() > 0) {
                fileid = fileid + Ext;
            }
            docObj.setStorename(fileid);

            saveOrUpdate(docObj);

            ll.add(docObj);

//            String temp = "/home/trainee";
            String temp = storageHandlerImpl.GetDocStorePath();
            uploadFile(fi, temp, fileid);
            indexDocument(temp +fileid, docObj, companyId, servletContext);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    

    public KwlReturnObject saveFileWithDocEntry(ByteArrayOutputStream baos, String userid, String companyId,
            String fileName, ServletContext servletContext) throws ServiceException {
        Docs docObj = new Docs();
        List ll = new ArrayList();
        int dl = 0;
        try {
            String Ext = "";
            String a = "";
            int index = fileName.lastIndexOf(".");
            if (index >= 0) {
                String dupExt = fileName.substring(fileName.lastIndexOf("."));
                Ext = fileName.substring(index+1);
                a = Ext.toUpperCase();
                Ext = dupExt;
            }

            User userObj;
            if(userid!=null){
            	userObj = (User) get(User.class, userid);
            	docObj.setUserid(userObj);
            }
            Company companyObj;
            if(userid!=null){
            	companyObj = (Company) get(Company.class, companyId);
            	docObj.setCompany(companyObj);
            }

            docObj.setDocname(fileName);
            docObj.setStorename("");
            docObj.setDoctype(a + " " + "File");
            docObj.setUploadedon(new Date());
            docObj.setStorageindex(1);
//            docObj.setDocsize(fi.getSize() + "");

            save(docObj);

            String fileid = docObj.getDocid();
            if (Ext.length() > 0) {
                fileid = fileid + Ext;
            }
            docObj.setStorename(fileid);

            saveOrUpdate(docObj);

            ll.add(docObj);

            String temp = storageHandlerImpl.GetDocStorePath();
            File storeFile = storeFile(baos, temp, fileid);
            ll.add(storeFile.getAbsolutePath());
            
            docObj.setDocsize(storeFile.length() + "");
            saveOrUpdate(docObj);
            indexDocument(temp +fileid, docObj, companyId, servletContext);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    @Override
    public String getDocUploadedCustomername(String docid){
    	String custname="";
    	List ll=null;
    	String sql="select con.firstname, con.lastname from crm_contact as con inner join crm_customer as cus on con.contactid=cus.contactid inner join customer_docs doc on doc.customerid = cus.id where docid=?";
    	ll=executeNativeQuery(sql, docid);
    	   	if(ll.size()>0){
    	   		Iterator it=ll.iterator();
    	   		while(it.hasNext()){
    	   			Object[] row=(Object[])it.next();
    	   			if(row!=null)
    	   			custname=(StringUtil.isNullOrEmpty((String) row[0])?"":(String) row[0]+""+(StringUtil.isNullOrEmpty((String) row[1])?"":(String) row[1]));
    	   		}
    	   	}
    	return custname;
    } 

    
    
    /**
     * @param filePath
     * @param docObj
     * @param servletContext
     * @throws ServiceException
     */
    public void indexDocument(String filePath, Docs docObj, String companyId, ServletContext servletContext) throws  ServiceException {
        try {
            File uploadFile = new File(filePath);

            FileInputStream fin = null;
            byte[] b = null;
            try {
                fin = new FileInputStream(uploadFile);
                b = new byte[(int) uploadFile.length()];
                fin.read(b);
            } finally {
                if (fin != null) {
                    fin.close();
                }
            }
            String fileType = servletContext.getMimeType(filePath);
            String contentType = KrawlerApp.getContentType(fileType, filePath, b);
            int flag1 = 0;
            if (!StringUtil.isNullOrEmpty(contentType)) {
                if (contentType.equals("application/vnd.ms-excel") ||
                        contentType.equals("application/msword") || contentType.equals("application/vnd.ms-word") ||
                        contentType.equals("application/vnd.ms-powerpoint") ||
                        contentType.equals("text/plain") || contentType.equals("text/csv") || contentType.equals("text/xml") || contentType.equals("text/css") || contentType.equals("text/html") || contentType.equals("text/cs") || contentType.equals("text/x-javascript") || contentType.equals("File") ||
                        contentType.equals("application/pdf") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
                    flag1 = 1;
                }
            }
            if (flag1 == 1) {
                Hashtable ht = new Hashtable();
                ht.put(DOCUMENT_FilePath, filePath);
                ht.put(DOCUMENT_FileName, docObj.getDocname());
                if(docObj.getUserid()!=null)
                	ht.put(DOCUMENT_Author, docObj.getUserid().getUserId());
                else
                	ht.put(DOCUMENT_Author, "Unknown");
                ht.put(DOCUMENT_DateModified, new java.util.Date());
                ht.put(DOCUMENT_Size, uploadFile.length());
                ht.put(DOCUMENT_Type, contentType);
                ht.put(DOCUMENT_DocumentId, docObj.getDocid());
                ht.put(DOCUMENT_Revision_No, 1);
                ht.put(DOCUMENT_IndexPath, StorageHandler.GetDocIndexPath());
                ht.put(DOCUMENT_CompanyId, companyId);
                LuceneSearchObj.createDocumentIndex(ht, servletContext);
            }
        } catch (java.io.IOException ex) {
             throw ServiceException.FAILURE("fileUploader.indexDocument", ex);
        }catch (Exception ex) {
             throw ServiceException.FAILURE("fileUploader.indexDocument", ex);
        }
    }
    
    /**
     * @param fi
     * @param destinationDirectory
     * @param fileName
     * @throws ServiceException
     */
    public void uploadFile(FileItem fi, String destinationDirectory, String fileName) throws ServiceException {
        try {
            File destDir = new File(destinationDirectory);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File uploadFile = new File(destinationDirectory + "/" + fileName);
            fi.write(uploadFile);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("documentDAOImpl.uploadFile", ex);
        }

    }

    public File storeFile(ByteArrayOutputStream baos, String destinationDirectory, String fileName) throws ServiceException {
        File uploadFile = null;
        try {
            File destDir = new File(destinationDirectory);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            uploadFile = new File(destinationDirectory + "/" + fileName);
            FileOutputStream oss = new FileOutputStream(uploadFile);
            baos.writeTo(oss);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("documentDAOImpl.storeFile", ex);
        } finally {
            return uploadFile;
        }

    }


    /* (non-Javadoc)
     * @see com.krawler.spring.documents.documentDAO#saveDocumentMapping(com.krawler.utils.json.base.JSONObject)
     */
    public void saveDocumentMapping(JSONObject jobj) throws ServiceException {
        try {
            Docmap docMap = new Docmap();

            if (jobj.has("docid") && !StringUtil.isNullOrEmpty(jobj.getString("docid"))) {
                Docs doc = (Docs) get(Docs.class, jobj.getString("docid"));
                docMap.setDocid(doc);
                if (jobj.has("companyid") && !StringUtil.isNullOrEmpty(jobj.getString("companyid"))) {
                    Company company = (Company) get(Company.class, jobj.getString("companyid"));
                    doc.setCompany(company);
                }
                if (jobj.has("userid") && !StringUtil.isNullOrEmpty(jobj.getString("userid"))) {
                    User user = (User) get(User.class, jobj.getString("userid"));
                    doc.setUserid(user);
                }
            }
            if (jobj.has("refid")) {
                docMap.setRecid(jobj.getString("refid"));
            }
            if (jobj.has("map")) {
                docMap.setRelatedto(jobj.getString("map"));
            }
            save(docMap);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("documentDAOImpl.saveDocumentMapping", ex);
        }
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.documents.documentDAO#downloadDocument(java.lang.String)
     */
    public KwlReturnObject downloadDocument(String id) throws ServiceException {

        List ll = null;
        int dl = 0;
        try {
            ll = executeQuery("FROM " +
                    "com.krawler.common.admin.Docmap AS crmdocs1 where crmdocs1.docid.docid =?", new Object[]{id});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.documents.documentDAO#getDocumentList(java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject getDocumentList(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException
    {
        String tagSearch = requestParams.containsKey("tag") ? requestParams.get("tag").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        if (requestParams.containsKey("tagSearch") && requestParams.get("tagSearch") != null)
        {
            tagSearch = quickSearch;
            quickSearch = "";
        }
        int start = 0;
        int limit = 20;
        int dl = 0;
        Object[] params = null;
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()))
        {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = new ArrayList();
        List llall = new ArrayList();
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c from com.krawler.common.admin.Docmap c where c.docid.company.companyID=? and c.docid.deleteflag=0 ";
        params = new Object[] { companyid };

        if (!StringUtil.isNullOrEmpty(tagSearch))
        {
            tagSearch = tagSearch.replaceAll("'", "");
            Hql += " and c.docid.tags like '%" + tagSearch + "%' ";
        }
        if (!StringUtil.isNullOrEmpty(quickSearch))
        {
            Hql += " and c.docid.docname like '" + quickSearch + "%' ";
        }
        String filterPermQuery="";
        String selectInQuery ="";

        // Campaign Permission

        boolean modulePerm_Campaign = false;
        if(requestParams.containsKey("campaign_module") && requestParams.get("campaign_module") != null) {
                modulePerm_Campaign = Boolean.parseBoolean(requestParams.get("campaign_module").toString());
        }
        
        if(modulePerm_Campaign){
            boolean heirarchyPerm_Campaign = false;
            filterPermQuery=" and c.relatedto = 0 ";
            if(requestParams.containsKey("campaign_heirarchyPerm") && requestParams.get("campaign_heirarchyPerm") != null) {
                heirarchyPerm_Campaign = Boolean.parseBoolean(requestParams.get("campaign_heirarchyPerm").toString());
            }
            if(!heirarchyPerm_Campaign){
                filterPermQuery+= " and c.docid.userid.userID in (" + usersList + ") ";
            }
            selectInQuery = Hql +filterPermQuery;
            ll = executeQuery(selectInQuery, params);
            if(ll!=null && !ll.isEmpty())
                llall.addAll(ll);

        }

        // Lead Permission

        boolean modulePerm_Lead = false;
        if(requestParams.containsKey("lead_module") && requestParams.get("lead_module") != null) {
                modulePerm_Lead = Boolean.parseBoolean(requestParams.get("lead_module").toString());
        }
        if(modulePerm_Lead){
            boolean heirarchyPerm_Lead = false;
            filterPermQuery=" and c.relatedto = 1 ";
            if(requestParams.containsKey("lead_heirarchyPerm") && requestParams.get("lead_heirarchyPerm") != null) {
                heirarchyPerm_Lead = Boolean.parseBoolean(requestParams.get("lead_heirarchyPerm").toString());
            }
            if(!heirarchyPerm_Lead){
                filterPermQuery+= " and c.docid.userid.userID in (" + usersList + ")  ";
            }
            selectInQuery = Hql +filterPermQuery;
            ll = executeQuery(selectInQuery, params);
            if(ll!=null && !ll.isEmpty())
                llall.addAll(ll);
        }

        // Contact Permission

        boolean modulePerm_Contact = false;
        if(requestParams.containsKey("contact_module") && requestParams.get("contact_module") != null) {
                modulePerm_Contact = Boolean.parseBoolean(requestParams.get("contact_module").toString());
        }
        if(modulePerm_Contact){
            boolean heirarchyPerm_Contact = false;
            filterPermQuery=" and c.relatedto = 2 ";
            if(requestParams.containsKey("account_heirarchyPerm") && requestParams.get("account_heirarchyPerm") != null) {
                heirarchyPerm_Contact = Boolean.parseBoolean(requestParams.get("account_heirarchyPerm").toString());
            }
            if(!heirarchyPerm_Contact){
                filterPermQuery+= " and c.docid.userid.userID in (" + usersList + ") ";
            }
            selectInQuery = Hql +filterPermQuery;
            ll = executeQuery(selectInQuery, params);
            if(ll!=null && !ll.isEmpty())
                llall.addAll(ll);
        }

        // Product Permission
        
        boolean modulePerm_Product = false;
        if(requestParams.containsKey("product_module") && requestParams.get("product_module") != null) {
                modulePerm_Product = Boolean.parseBoolean(requestParams.get("product_module").toString());
        }
        if(modulePerm_Product){
            boolean heirarchyPerm_Product = false;
            filterPermQuery=" and c.relatedto = 3 ";
            if(requestParams.containsKey("contact_heirarchyPerm") && requestParams.get("contact_heirarchyPerm") != null) {
                heirarchyPerm_Product = Boolean.parseBoolean(requestParams.get("contact_heirarchyPerm").toString());
            }
            if(!heirarchyPerm_Product){
                filterPermQuery+= " and c.docid.userid.userID in (" + usersList + ") ";
            }
            selectInQuery = Hql +filterPermQuery;
            ll = executeQuery(selectInQuery, params);
            if(ll!=null && !ll.isEmpty())
                llall.addAll(ll);
        }

        // Account Permission

        boolean modulePerm_Account = false;
        if(requestParams.containsKey("account_module") && requestParams.get("account_module") != null) {
                modulePerm_Account = Boolean.parseBoolean(requestParams.get("account_module").toString());
        }
        if(modulePerm_Account){
            boolean heirarchyPerm_Account = false;
            filterPermQuery=" and c.relatedto = 4 ";
            if(requestParams.containsKey("opp_heirarchyPerm") && requestParams.get("opp_heirarchyPerm") != null) {
                heirarchyPerm_Account = Boolean.parseBoolean(requestParams.get("opp_heirarchyPerm").toString());
            }
            if(!heirarchyPerm_Account){
                filterPermQuery+= " and c.docid.userid.userID in (" + usersList + ") ";
            }
            selectInQuery = Hql +filterPermQuery;
            ll = executeQuery(selectInQuery, params);
            if(ll!=null && !ll.isEmpty())
                llall.addAll(ll);
        }

        // Opportunity Permission

        boolean modulePerm_Opportunity = false;
        if(requestParams.containsKey("opportunity_module") && requestParams.get("opportunity_module") != null) {
                modulePerm_Opportunity = Boolean.parseBoolean(requestParams.get("opportunity_module").toString());
        }
        if(modulePerm_Opportunity){
            boolean heirarchyPerm_Opportunity = false;
            filterPermQuery=" and c.relatedto = 5 ";
            if(requestParams.containsKey("product_heirarchyPerm") && requestParams.get("product_heirarchyPerm") != null) {
                heirarchyPerm_Opportunity = Boolean.parseBoolean(requestParams.get("product_heirarchyPerm").toString());
            }
            if(!heirarchyPerm_Opportunity){
                filterPermQuery+= " and c.docid.userid.userID in (" + usersList + ")  ";
            }
            selectInQuery = Hql +filterPermQuery;
            ll = executeQuery(selectInQuery, params);
            if(ll!=null && !ll.isEmpty())
                llall.addAll(ll);
        }

        // Case Permission
        boolean modulePerm_Case = false;
        if(requestParams.containsKey("case_module") && requestParams.get("case_module") != null) {
                modulePerm_Case = Boolean.parseBoolean(requestParams.get("case_module").toString());
        }
        if(modulePerm_Case){
            boolean heirarchyPerm_Cases = false;
            filterPermQuery=" and c.relatedto = 6 ";
            List lc=null;
            if(requestParams.containsKey("case_heirarchyPerm") && requestParams.get("case_heirarchyPerm") != null) {
                heirarchyPerm_Cases = Boolean.parseBoolean(requestParams.get("case_heirarchyPerm").toString());
            }
            if(!heirarchyPerm_Cases){
                filterPermQuery+= " and c.docid.userid.userID in (" + usersList + ") ";
            }
            selectInQuery = Hql +filterPermQuery;
            ll = executeQuery(selectInQuery, params);
            String custDocquery=Hql + "  and c.relatedto = 6  and c.docid.userid IS NULL ";
            lc = executeQuery(custDocquery, params);
            if(ll!=null && !ll.isEmpty())
                llall.addAll(ll);
            if(lc!=null && !lc.isEmpty()){
                llall.addAll(lc);
            }
        }

        // Acitivity Permission
        boolean modulePerm_Activity = false;
        if(requestParams.containsKey("activity_module") && requestParams.get("activity_module") != null) {
                modulePerm_Activity = Boolean.parseBoolean(requestParams.get("activity_module").toString());
        }
        if(modulePerm_Activity){
            boolean heirarchyPerm_Activity = false;
            filterPermQuery=" and c.relatedto = 7 ";
            if(requestParams.containsKey("activity_heirarchyPerm") && requestParams.get("activity_heirarchyPerm") != null) {
                heirarchyPerm_Activity = Boolean.parseBoolean(requestParams.get("activity_heirarchyPerm").toString());
            }
            if(!heirarchyPerm_Activity){
                filterPermQuery+= " and c.docid.userid.userID in (" + usersList + ") ";
            }
            selectInQuery = Hql +filterPermQuery;
            ll = executeQuery(selectInQuery, params);
            if(ll!=null && !ll.isEmpty())
                llall.addAll(ll);

        }
        
        filterPermQuery = " and c.docid.userid.userID in (" + usersList + ") and c.relatedto = -1 ";
        selectInQuery = Hql +filterPermQuery;
        ll = executeQuery(selectInQuery, params);
        if(ll!=null && !ll.isEmpty())
            llall.addAll(ll);

        dl = llall.size();

        //ll = executeQueryPaging(selectInQuery, params, new Integer[] { start, limit });
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", llall, dl);
    }

   public KwlReturnObject getSharedDocumentList(HashMap<String, Object> requestParams,List ll, String userid, StringBuffer docIds) throws ServiceException {
        int dl = 0;
        List sharedll = new ArrayList();
        Object[] params = null;
        try {
            String tagSearch = requestParams.containsKey("tag") ? requestParams.get("tag").toString() : "";
            String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
            if (requestParams.containsKey("tagSearch") && requestParams.get("tagSearch") != null)
            {
                tagSearch = quickSearch;
                quickSearch = "";
            }

            String Hql = " select c from com.krawler.common.admin.Docmap c where c.docid.docid in ( select d.document.docid from com.krawler.common.admin.DocOwners d where d.usersByUserid.userID=?   ";
            if(docIds.length()>0){
                Hql += " and d.document.docid not in ("+docIds+") ) ";
            } else {
                Hql += "  ) ";
            }
            params = new Object[] { userid };

            if (!StringUtil.isNullOrEmpty(tagSearch))
            {
                tagSearch = tagSearch.replaceAll("'", "");
                Hql += " and c.docid.tags like '%" + tagSearch + "%' ";
            }
            if (!StringUtil.isNullOrEmpty(quickSearch))
            {
                Hql += " and c.docid.docname like '" + quickSearch + "%' ";
            }
            
            sharedll = executeQuery(Hql, params);
            if(sharedll!=null && !sharedll.isEmpty()){
                ll.addAll(sharedll);
            }
            dl = ll.size();
            
        } catch (Exception ex) {
            throw ServiceException.FAILURE("documentDAOImpl.getSharedDocumentList", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    /* (non-Javadoc)
     * @see com.krawler.spring.documents.documentDAO#addTag(java.util.HashMap)
     */
    public KwlReturnObject addTag(HashMap<String, Object> requestParams) throws ServiceException {
        String tag = requestParams.containsKey("tag") ? requestParams.get("tag").toString() : "";
        List ll = new ArrayList();
        int dl = 0;
        try {
            String tags[] = tag.split(",,");
            Docs c = (Docs) get(Docs.class,tags[0]);
            c.setTags(tags[1]);
            ll.add(c);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("documentDAOImpl.addTag", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.documents.documentDAO#getDocumentsForTable(java.util.HashMap, boolean)
     */
    public KwlReturnObject getDocumentsForTable(HashMap<String, Object> queryParams, boolean allflag) throws ServiceException {
        KwlReturnObject kmsg = null;
        try {
            kmsg = getTableData(queryParams, allflag);
        } catch (Exception e) {
            throw ServiceException.FAILURE("documentDAOImpl.getDocumentsForTable : "+e.getMessage(), e);
        }
        return kmsg;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.documents.documentDAO#deletedocument(java.lang.String)
     */
    public KwlReturnObject deletedocument(String docids) throws ServiceException {
        Boolean success = false;
        try {
            String[] docid = docids.split(",");
            String indexPath = storageHandlerImpl.GetDocIndexPath();
            for(int ctr=0;ctr<docid.length;ctr++){
                Docs docObj = (Docs) get(com.krawler.common.admin.Docs.class, docid[ctr]);
                if(docObj!=null){
                    docObj.setDeleteflag(1);
                    save(docObj);

                    deleteDocMapEntry(docObj.getDocid());

                    try{ //Remove entry from lucene index
                        LuceneSearchObj.deleteIndex(DOCUMENT_DocumentId, docObj.getDocid(), indexPath);
                    } catch(Exception ex){
                        logger.warn(ex.getMessage(), ex);
                    }
                }
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("documentDAOImpl.deletedocument : "+e.getMessage(), e);
        }
        return new KwlReturnObject(success, "", "", null, 0);
    }

    public KwlReturnObject getReloadDocumentLuceneIndex()  throws ServiceException{
        List ll = null;
        int dl = 0;
        try {
            String query = "select d from com.krawler.common.admin.Docs d where d.deleteflag=0 ";
            ll = executeQuery(query);
            dl = ll.size();
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public boolean checkReloadDocumentIndex(){
        boolean doReload = false;
        String query = "select reloadindex from lucene_master where activity=? and reloadindex=?";
        SqlRowSet rs = queryForRowSetJDBC(query, new Object[]{"documents", 1});
        if (rs.next()) {
            doReload = true;
        }
        return doReload;
    }

    public void resetDocumentIndexFlag(){
        String query = "update lucene_master set reloadindex=? where activity=? and reloadindex=?";
        updateJDBC(query, new Object[]{0, "documents", 1});
    }

    public void deleteDocMapEntry(String docid) throws ServiceException{
        String hql="delete from com.krawler.common.admin.Docmap  where docid.docid = ? ";
        executeUpdate(hql, docid);

    }
    public KwlReturnObject getDocumentOwners(String docid) throws ServiceException {

        List ll = null;
        int dl = 0;
        try {
            ll = executeQuery("FROM " +
                    "com.krawler.common.admin.DocOwners AS docOwner where docOwner.document.docid =?", new Object[]{docid});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject saveDocOwners(HashMap<String, Object> requestParams) throws Exception {
        List ll = new ArrayList();
        try {
            String docid = "";
            if(requestParams.containsKey("docid") && requestParams.get("docid") != null) {
                docid =  requestParams.get("docid").toString();
            }
            String owners = "";
            if(requestParams.containsKey("owners") && requestParams.get("owners") != null) {
                owners =  requestParams.get("owners").toString();
            }
            String mainowner = "";
            if(requestParams.containsKey("mainOwner") && requestParams.get("mainOwner") != null) {
                mainowner =  requestParams.get("mainOwner").toString();
            }

            String hql="delete from DocOwners c where c.document.docid = ? ";
            executeUpdate(hql, docid);

            DocOwners docOwnersObj = new DocOwners();
            docOwnersObj.setDocument((Docs)get(Docs.class, docid));
            docOwnersObj.setUsersByUserid((User)get(User.class, mainowner));
            docOwnersObj.setMainOwner(true);
            save(docOwnersObj);

            if(!StringUtil.isNullOrEmpty(owners) && !owners.equalsIgnoreCase("undefined") ){
                String[] ownerIds = owners.split(",");
                for (int i = 0;i < ownerIds.length;i++){
                    docOwnersObj = new DocOwners();
                    docOwnersObj.setDocument((Docs)get(Docs.class, docid));
                    docOwnersObj.setUsersByUserid((User)get(User.class, ownerIds[i]));
                    docOwnersObj.setMainOwner(false);
                    save(docOwnersObj);
                }
            }
            ll.add(docOwnersObj);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmOpportunityDAOImpl.saveOppOwners", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, 1);
    }

     public void insertDocumentOwnerEntry(String id, String userid, String docid) throws ServiceException {
        try {
            DocOwners docOwn = new DocOwners();
            if(!StringUtil.isNullOrEmpty(id)){
                docOwn.setId(id);
            }
            if (!StringUtil.isNullOrEmpty(docid)) {
                Docs doc = (Docs) get(Docs.class, docid);
                docOwn.setDocument(doc);
                
            }
            if (!StringUtil.isNullOrEmpty(userid)) {
                User user = (User) get(User.class, userid);
                docOwn.setUsersByUserid(user);
            }
            docOwn.setMainOwner(true);
            save(docOwn);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("documentDAOImpl.saveDocumentMapping", ex);
        }
    }
     public KwlReturnObject deleteDocumentFromModule(String docid ) throws ServiceException {
    	 List ll = new ArrayList();
         int dl = 0;
         try {
             String hql = "update  com.krawler.common.admin.Docs d set d.deleteflag=1  where d.docid=? ";
             executeUpdate(hql, new Object[]{docid});
         }catch(Exception e) {
             logger.warn(e.getMessage(), e);
             throw ServiceException.FAILURE("documentDAOImpl.deleteDocumentFormModule : "+e.getMessage(), e);
         }
         return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
        
     }
     }
