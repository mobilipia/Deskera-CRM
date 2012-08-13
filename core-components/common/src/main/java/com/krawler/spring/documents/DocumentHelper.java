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

import com.krawler.common.admin.Docs;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.esp.Search.Summarizer;
import com.krawler.esp.utils.LuceneSearchConstants;
import com.krawler.luceneSearchService.LuceneSearch;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import static com.krawler.esp.utils.LuceneSearchConstants.*;
import static com.krawler.spring.documents.DocumentConstants.*;

/**
 *
 * @author krawler
 */
public class DocumentHelper {
    private LuceneSearch LuceneSearchObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private documentDAO crmDocumentDAOObj;

    public void setLuceneSearch(LuceneSearch LuceneSearchObj) {
        this.LuceneSearchObj = LuceneSearchObj;
    }
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOobj) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOobj;
    }
    
    public void setcrmDocumentDAO(documentDAO crmDocumentDAOObj1) {
        this.crmDocumentDAOObj = crmDocumentDAOObj1;
    }
    
    public KwlReturnObject documentIndexSearch(HashMap<String, Object> requestParams) throws ServiceException {
        List ll1 = new ArrayList();
        int totalRecords = 0;
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try {
            String querytxt = requestParams.get("ss")!=null?requestParams.get("ss").toString():"";
            String filterStr = QueryParser.escape(querytxt.toLowerCase());
            String companyid = requestParams.get("companyid")!=null?requestParams.get("companyid").toString():"";
            StringBuffer query = new StringBuffer();
            String qfield = LuceneSearchConstants.DOCUMENT_IndexedText;

            String indexPath = storageHandlerImpl.GetDocIndexPath();

            Hits hitresult = null;

            if (filterStr.length() > 2) {
                filterStr = "*"+filterStr+"*";
            }

            if(!StringUtil.isNullOrEmpty(filterStr)){
                query.append("(");
                query.append(qfield);
                query.append(":");
                query.append(filterStr);
                query.append(")");
                query.append(" AND ");
            }

            query.append("(");
            query.append(DOCUMENT_CompanyId);
            query.append(":");
            query.append(companyid);
            query.append(")");

            // userlist filter
            if(requestParams.containsKey("usersListIds")){
                StringBuffer usersListIds = (StringBuffer)requestParams.get("usersListIds");
                String filterUsers = usersListIds.toString().replaceAll("'", "");
                query.append(" OR (");

                String[] filterUsersIds = filterUsers.split(",");

                for(int i=0; i<filterUsersIds.length-1; i++){
                    query.append(DOCUMENT_Author);
                    query.append(":");
                    query.append(filterUsersIds[i]);
                    query.append(" OR ");
                }
                query.append(DOCUMENT_Author);
                query.append(":");
                query.append(filterUsersIds[filterUsersIds.length-1]);

                query.append(")");
            }

            hitresult = LuceneSearchObj.searchIndex(query.toString(), qfield, indexPath);
            if (hitresult != null) {
                totalRecords = hitresult.length();
                int start = 0;
                int limit = totalRecords;
                if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
                    start = Integer.parseInt(requestParams.get("start").toString());
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                }
                int count = Math.min(totalRecords, start+limit);
                Summarizer summary = new Summarizer();
                String documentUUID= "";
                Docs kwlDoc;
                DateFormat df = authHandler.getDateFormatter();

                for(int i=start; i<count; i++){
                    org.apache.lucene.document.Document doc = hitresult.doc(i);
                    JSONObject tmpObj = new JSONObject();
                    documentUUID = doc.get(DOCUMENT_DocumentId);
                    kwlDoc = (Docs) KwlCommonTablesDAOObj.getObject(Docs.class.getName(), documentUUID);

                    tmpObj.put(JSON_docid, documentUUID);
                    String author="";
                    if(kwlDoc.getUserid()!=null){
                       	author = StringUtil.getFullName(kwlDoc.getUserid());
                    }
                    else{
                    	author = crmDocumentDAOObj.getDocUploadedCustomername(documentUUID);
                    }	
                    tmpObj.put(JSON_author, author);
                    tmpObj.put(JSON_uploadername, author);
                    tmpObj.put(JSON_fileimage, getDocImg(doc.get(DOCUMENT_FileName)));
                    tmpObj.put(JSON_Summary, summary.getSummary(doc.get(DOCUMENT_PlainText), querytxt).toString());

                    tmpObj.put(JSON_name, kwlDoc.getDocname());
                    tmpObj.put(JSON_uploadeddate, kwlDoc.getUploadedon()!=null?df.format(kwlDoc.getUploadedon()):"");
                    tmpObj.put(JSON_size, StringUtil.sizeRenderer(kwlDoc.getDocsize()));
                    tmpObj.put(JSON_Tags, kwlDoc.getTags());
                    tmpObj.put(JSON_type, kwlDoc.getDoctype());
                    jarr.put(tmpObj);
                }
            }
            jobj.put("data", jarr);
            jobj.put("totalCount", totalRecords);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        ll1.add(jobj);
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll1, totalRecords);
	}

    public String getDocImg (String docname){
        String docimg ="file60.png";
        String extension = docname.substring(docname.lastIndexOf('.')+1);
        if(StringUtil.equal(extension, "txt")){
            docimg = "TXT52.png";
        } else if(StringUtil.equal(extension, "pdf")){
            docimg = "pdf60.png";
        } else if(StringUtil.equal(extension, "html")){
            docimg = "HTML52.png";
        } else if(StringUtil.equal(extension, "csv")){
            docimg = "csv-icon.jpg";
        } else if(StringUtil.equal(extension, "xls")){
            docimg= "excel-icon.jpg";
        }
        return docimg;
    }
}
