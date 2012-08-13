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

import com.krawler.esp.fileparser.excelparser.MsExcelParser;
import com.krawler.esp.fileparser.excelparser.XlsxParser;
import com.krawler.esp.fileparser.pdfparser.KWLPdfParser;
import com.krawler.esp.fileparser.pptparser.MsPPTParser;
import com.krawler.esp.fileparser.wordparser.DocxParser;
import com.krawler.esp.fileparser.wordparser.ExtractWordFile;
import com.krawler.esp.indexer.Fetcher;
import com.krawler.esp.utils.DocumentFields;
import com.krawler.esp.utils.KWLuceneException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.LockObtainFailedException;

/**
 *
 * @author krawler
 */
public class LuceneSearchImpl implements LuceneSearch {
    protected static Thread fetcher;
    private static Fetcher fetchFile;
    private WhitespaceAnalyzer KWLAnalyzer = new WhitespaceAnalyzer();

    @Override
    public Document createLuceneDocument(List<Object> indexdetails, List<String> indexFields, List<String> sortableIndexName) throws KWLuceneException {
        Document doc = new Document();
        if(indexdetails.size() != indexdetails.size()){
            throw new KWLuceneException("Total number of IndexNames and IndexValues does not match");
        }

        int i = 0;
        Field docfield;
        String indexValue="";
        for(String indexName: indexFields){
            indexValue = indexdetails.get(i)==null ? "" : indexdetails.get(i).toString();
            if(sortableIndexName.contains(indexName)){ //Sortable index has to be UN_TOKENIZED
                docfield = new Field(indexName, indexValue, Field.Store.YES, Field.Index.UN_TOKENIZED);
            } else {
                docfield = new Field(indexName, indexValue, Field.Store.YES, Field.Index.TOKENIZED);
            }
            doc.add(docfield);
            i++;
        }

        return doc;
    }

    @Override
    public int writeIndex(List<Document> lucendDocs, String indexPath) {
        try {
            boolean CreateIndex = true;
            File f = new File(indexPath);
            if (f.exists()) {
                CreateIndex = false;
            }
            IndexWriter indWriter = new IndexWriter(indexPath, this.KWLAnalyzer, CreateIndex);

            for(Document luceneDoc: lucendDocs){
                indWriter.addDocument(luceneDoc);
            }

            indWriter.optimize();
            indWriter.close();
        } catch (Exception ex) {
            Logger.getLogger(LuceneSearchImpl.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return 1;
    }

    @Override
    public int createIndex(List<Object> indexdetails, List<String> indexFields, String indexPath) throws KWLuceneException {
        return createSortableIndex(indexdetails, indexFields, new ArrayList(), indexPath);
    }

    @Override
    public int createSortableIndex(List<Object> indexdetails, List<String> indexFields, List<String> sortableIndexName, String indexPath) throws KWLuceneException {
        ArrayList<Document> lucendDocs = new ArrayList<Document>();
        Document lucenDoc = createLuceneDocument(indexdetails, indexFields, sortableIndexName);
        lucendDocs.add(lucenDoc);
        return writeIndex(lucendDocs, indexPath);
    }

    @Override
    public void deleteIndex(String indexName, String searchValue, String indexPath) throws KWLuceneException {
        Term t = new Term(indexName, searchValue);
		try {
			IndexReader docInRead = IndexReader.open(indexPath);
			docInRead.deleteDocuments(t);
			docInRead.close();
			IndexWriter inw = new IndexWriter(indexPath, this.KWLAnalyzer, false);
			inw.optimize();
			inw.close();
		} catch (Exception ex) {
            throw new KWLuceneException("DeleteIndex: "+ex.toString(), ex);
		}
    }

    @Override
    public List<DocumentFields> getDocumentFields(List<Object> indexdetails, List<String> indexFields) {
        ArrayList<DocumentFields> indexfieldArray = new ArrayList<DocumentFields>();
        Iterator itr = indexdetails.iterator();
        int i = 0;
        while (itr.hasNext()) {
            DocumentFields docFields = new DocumentFields();
            docFields.SetFieldName(indexFields.get(i));
            docFields.SetFieldValue(itr.next().toString());
            indexfieldArray.add(docFields);
            i++;
        }
        return indexfieldArray;
    }

    @Override
    public Hits searchIndex(String query, String Field, String indexPath) throws KWLuceneException {
        String[] search_fields = {Field};
        return searchIndexWithSort(query, search_fields, indexPath, null);
    }

    @Override
    public Hits searchMultiIndex(String query, String[] Field, String indexPath) throws KWLuceneException {
        return searchIndexWithSort(query, Field, indexPath, null);
    }

    @Override
    public Hits searchIndexWithSort(String query, String[] Field, String indexPath, Sort sort) throws KWLuceneException {
        Hits result = null;
        try {
            IndexSearcher searcher = new IndexSearcher(indexPath);
            MultiFieldQueryParser multiparser = new MultiFieldQueryParser(Field, this.KWLAnalyzer);
            multiparser.setAllowLeadingWildcard(true);
            multiparser.setDefaultOperator(QueryParser.Operator.OR);
            Query lucenequery = multiparser.parse(query);
            if(sort==null){
                result = searcher.search(lucenequery);
            } else {
                result = searcher.search(lucenequery, sort);
            }
        } catch (Exception ex) {
            throw new KWLuceneException("searchIndexWithSort: "+ex.toString(), ex);
        }
        return result;
    }

    @Override
    public void createDocumentIndex(Map ht, ServletContext servletContext) {
        fetchFile = Fetcher.get(servletContext);
        if (fetchFile.isWorking()) {
            fetchFile.add(ht);
        } else {
            fetcher = new Thread(fetchFile);
            fetchFile.add(ht);
            fetcher.start();
        }
    }

    @Override
    public void clearIndex(String indexPath) {
        File indexDirectory = new File(indexPath);
        deleteDirectory(indexDirectory);
    }

    public static boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDirectory(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
}

    public String parseDocument(String filePath, String contentType) throws Exception{
        String plaintext = "";
        if (contentType.equals("application/vnd.ms-excel")) {
            MsExcelParser eParser = new MsExcelParser();
            plaintext = eParser.extractText(filePath);
        } else if (contentType.equals("application/msword") || contentType.equals("application/vnd.ms-word")) {
            ExtractWordFile wParser = new ExtractWordFile();
            plaintext = wParser.extractText(filePath);
        } else if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            DocxParser docx = new DocxParser();
            plaintext = docx.extractText(filePath);
        } else if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            XlsxParser xlx = new XlsxParser();
            plaintext = xlx.extractText(filePath);
        } else if (contentType.equals("application/vnd.ms-powerpoint")) {
            MsPPTParser wParser = new MsPPTParser();
            plaintext = wParser.extractText(filePath);
        } else if (contentType.equals("application/pdf")) {
            KWLPdfParser pdfParse = new KWLPdfParser();
            plaintext = pdfParse.getPlaintextpdf(filePath);
        } else if (contentType.equals("text/plain") || contentType.equals("text/csv") ||contentType.equals("text/xml") || contentType.equals("text/css") || contentType.equals("text/html") || contentType.equals("text/cs") || contentType.equals("text/x-javascript") || contentType.equals("File")) {
            File f = new File(filePath);
            FileInputStream fin = null;
            byte[] b = null;
            try {
                fin = new FileInputStream(f);
                b = new byte[(int) f.length()];
                fin.read(b);
            } finally {
                if (fin != null) {
                    fin.close();
                }
            }
            String s = new String(b);
            plaintext = s;
        }
        return plaintext;
    }
}
