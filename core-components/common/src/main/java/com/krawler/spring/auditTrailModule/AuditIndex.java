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
package com.krawler.spring.auditTrailModule;

import com.krawler.esp.utils.KWLuceneException;
import com.krawler.luceneSearchService.LuceneSearch;
import com.krawler.luceneSearchService.LuceneSearchImpl;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *
 * @author krawler
 */
public class AuditIndex implements Runnable {

    private LuceneSearch LuceneSearchObj;
    
    ArrayList auditIndexQueue = new ArrayList();
    boolean isWorking = false;
    
    protected final Log logger = LogFactory.getLog(getClass());

    public void setLuceneSearch(LuceneSearch LuceneSearchObj) {
        this.LuceneSearchObj = LuceneSearchObj;
    }

    public boolean isIsWorking() {
        return isWorking;
    }

    public void setIsWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

    public void add(List<String> indexNames,List<Object> indexValues,List<String> sortableIndexName, String indexPath) {
        AuditIndexDataObject auditIndexObj = new AuditIndexDataObject(indexNames,indexValues,sortableIndexName,indexPath);
        auditIndexQueue.add(auditIndexObj);
    }

    @Override
    public void run() {
        try {
            while (!auditIndexQueue.isEmpty()) {
                this.isWorking = true;
                List<String> indexNames;
                List<Object> indexValues ;
                List<String> sortableIndexName;
                String indexPath;
                AuditIndexDataObject auditIndexObj = (AuditIndexDataObject) auditIndexQueue.get(0);
                try {
                    indexNames = auditIndexObj.indexNames;
                    indexValues = auditIndexObj.indexValues;
                    sortableIndexName = auditIndexObj.sortableIndexName;
                    indexPath = auditIndexObj.indexPath;
                    LuceneSearchImpl LuceneSearch = new LuceneSearchImpl();
                    LuceneSearch.createSortableIndex(indexValues, indexNames, sortableIndexName, indexPath);
                } catch(KWLuceneException ex) {
                    logger.warn("AuditIndexLogEntry: "+ex.getMessage(), ex);
                } finally{
                    auditIndexQueue.remove(auditIndexObj);
                }
            }
        } catch (Exception ex) {
           logger.warn("AuditIndexLogEntry: "+ex.getMessage(), ex);
        } finally {
            this.isWorking = false;
        }
    }
}
