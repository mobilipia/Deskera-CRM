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

package com.krawler.esp.indexer;



import com.krawler.esp.fileparser.excelparser.MsExcelParser;
import com.krawler.esp.fileparser.pdfparser.KWLPdfParser;
import com.krawler.esp.fileparser.pptparser.MsPPTParser;
import com.krawler.esp.fileparser.wordparser.ExtractWordFile;
import com.krawler.esp.fileparser.wordparser.FastSavedException;
import com.krawler.luceneSearchService.LuceneSearch;
import com.krawler.luceneSearchService.LuceneSearchImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.*;
import java.util.Hashtable;
import static com.krawler.esp.utils.LuceneSearchConstants.*;

public class Fetcher implements Runnable {
    private LuceneSearch luceneSearchObj;
	ArrayList filequeue = new ArrayList();
	boolean working;

	public boolean isWorking() {
		return working;
	}

	public boolean add(Object o) {
		return filequeue.add(o);
	}

	public void clear() {
		filequeue.clear();
	}

	public boolean contains(Object o) {
		return filequeue.contains(o);
	}

	public boolean remove(Object o) {
		return filequeue.remove(o);
	}

	public boolean isEmpty() {
		return filequeue.isEmpty();
	}

    @Override
	public void run() {
		while (!filequeue.isEmpty()) {
			this.working = true;
			Hashtable tempfileinfo = (Hashtable) filequeue.get(0);
			IndexDocument(tempfileinfo);
			filequeue.remove(tempfileinfo);
		}
		this.working = false;
	}

	public static Fetcher get(ServletContext app) {
		Fetcher bean = (Fetcher) app.getAttribute(FETCHER_BEAN_ID);
		if (bean == null) {
			bean = new Fetcher();
			app.setAttribute(FETCHER_BEAN_ID, bean);
		}
		return bean;
	}

	public Fetcher() {
        this.luceneSearchObj = new LuceneSearchImpl();
	}

    public void IndexDocument(Hashtable tempfileinfo) {
        String filePath = tempfileinfo.get(DOCUMENT_FilePath).toString();
        String Author="";
        if(tempfileinfo.get(DOCUMENT_Author)!=null && tempfileinfo.get(DOCUMENT_Author).toString()!="")
        	Author = tempfileinfo.get(DOCUMENT_Author).toString();
        String DocId = tempfileinfo.get(DOCUMENT_DocumentId).toString();
        String fname = tempfileinfo.get(DOCUMENT_FileName).toString();
        String contentType = tempfileinfo.get(DOCUMENT_Type).toString();
        String revisionno = tempfileinfo.get(DOCUMENT_Revision_No).toString();
        String indexpath = tempfileinfo.get(DOCUMENT_IndexPath).toString();
        String companyId = tempfileinfo.get(DOCUMENT_CompanyId).toString();

        
        Logger fetchLogger = Logger.getLogger(Fetcher.class.getName());
		try {
			ArrayList<Object> fileDetails = new ArrayList<Object>();
            ArrayList<String> fileFields = new ArrayList<String>();
            fileFields.add(DOCUMENT_FileName);
            fileDetails.add(fname);
            fileFields.add(DOCUMENT_Author);
            fileDetails.add(Author);
            fileFields.add(DOCUMENT_DocumentId);
            fileDetails.add(DocId);
            fileFields.add(DOCUMENT_CompanyId);
            fileDetails.add(companyId);

			String plaintext = luceneSearchObj.parseDocument(filePath, contentType);
            fileFields.add(DOCUMENT_PlainText);
            fileDetails.add(plaintext);
            fileFields.add(DOCUMENT_IndexedText);
            fileDetails.add(plaintext.toLowerCase());//All lower case for comparision
			if (revisionno.equals("-")) {
                luceneSearchObj.deleteIndex(DOCUMENT_DocumentId, DocId, indexpath);
			}
			luceneSearchObj.createIndex(fileDetails, fileFields, indexpath);
			System.out.print("Lucene index : "+fname + " added.");
        } catch (FastSavedException e) {
            fetchLogger.log(Level.SEVERE, null, e);
		} catch (IOException e) {
            fetchLogger.log(Level.SEVERE, null, e);
		} catch (Exception ex) {
            fetchLogger.log(Level.SEVERE, null, ex);
		}
	}
}
