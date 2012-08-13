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
package com.krawler.luceneSearchService;

import com.krawler.esp.utils.DocumentFields;
import com.krawler.esp.utils.KWLuceneException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.apache.lucene.search.Hits;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Sort;

/**
 *
 * @author krawler
 */
public interface LuceneSearch {
    Document createLuceneDocument(List<Object> indexdetails, List<String> indexFields, List<String> sortableIndexName) throws KWLuceneException;
    
    int writeIndex(List<Document> lucendDocs, String indexPath);
    int createIndex(List<Object> indexdetails, List<String> indexFields, String indexPath) throws KWLuceneException;
    int createSortableIndex(List<Object> indexdetails,List<String> indexFields, List<String> sortableIndexName, String indexPath) throws KWLuceneException;

    void deleteIndex(String indexName, String searchValue, String indexPath) throws KWLuceneException;
    List<DocumentFields> getDocumentFields(List<Object> indexdetails, List<String> indexFields);

    Hits searchIndex(String query, String Field, String indexPath) throws KWLuceneException;
    Hits searchMultiIndex(String query, String[] Field, String indexPath) throws KWLuceneException;
    Hits searchIndexWithSort(String query, String[] Field, String indexPath, Sort sort) throws KWLuceneException;

    void createDocumentIndex(Map ht, ServletContext servletContext);
    void clearIndex(String indexPath);

    public String parseDocument(String filePath, String contentType) throws Exception;
}
