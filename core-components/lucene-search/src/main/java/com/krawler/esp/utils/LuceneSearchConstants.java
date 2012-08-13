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

package com.krawler.esp.utils;

/**
 *
 * @author krawler
 */
public class LuceneSearchConstants {
    public static final int Summarizer_SUM_CONTEXT = 5; //Used in Summarizer
    public static final int Summarizer_SUM_LENGTH = 35; //Used in Summarizer
    public static final String FETCHER_BEAN_ID = "fetch"; //Used in Fetcher Constructor

    //Constants to Build/Extract HashTable used to index document
    public static final String DOCUMENT_FileName = "FileName";
    public static final String DOCUMENT_FilePath = "FilePath";
    public static final String DOCUMENT_Author = "Author";
    public static final String DOCUMENT_DateModified = "DateModified";
    public static final String DOCUMENT_Size = "Size";
    public static final String DOCUMENT_Type = "Type";
    public static final String DOCUMENT_DocumentId = "DocumentId";
    public static final String DOCUMENT_PlainText = "PlainText";
    public static final String DOCUMENT_IndexedText = "IndexedText";
    public static final String DOCUMENT_Revision_No = "Revision No";
    public static final String DOCUMENT_IndexPath = "IndexPath";
    public static final String DOCUMENT_CompanyId = "CompanyId";
}
