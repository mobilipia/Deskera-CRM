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
package com.krawler.crm.quotation;

import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.Quotation;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;
import java.util.List;

public interface QuotationDAO {
    public KwlReturnObject saveQuotation(HashMap<String, Object> dataMap) throws ServiceException;
    public KwlReturnObject getQuotationList(HashMap<String, Object> dataMap) throws ServiceException;
    public KwlReturnObject getQuotationCount(String entrynumber, String companyid) throws ServiceException;
    public List<Quotation> getQuotations(List<String> recordIds) throws ServiceException;
    public KwlReturnObject getQuotationItems(HashMap<String, Object> dataMap) throws ServiceException;
    public KwlReturnObject saveQuotationDetails(HashMap<String, Object> dataMap) throws ServiceException;
    public KwlReturnObject deleteQuotations(String[] arrayid) throws ServiceException;
    String getNextAutoNumber(String companyid, int from) throws ServiceException;
    public KwlReturnObject deleteQuotationItems(String billid,String productId) throws ServiceException;
    public KwlReturnObject editQuotation(HashMap<String, Object> dataMap) throws ServiceException;
    public KwlReturnObject getQuotationItemsId(String quotation,String productId) throws ServiceException;
    public KwlReturnObject getQuotation(HashMap<String, Object> requestParams) throws ServiceException;
}
