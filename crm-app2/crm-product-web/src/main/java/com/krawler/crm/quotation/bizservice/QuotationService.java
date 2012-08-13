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
package com.krawler.crm.quotation.bizservice;

import com.krawler.crm.database.tables.Quotation;
import com.krawler.utils.json.base.JSONObject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface QuotationService {
    JSONObject getQuotationList(String companyid, String userid, String ss, String moduleid,int start,int limit);
    JSONObject getRecordName(String moduleid, StringBuffer usersList, String companyid, boolean heirarchyPerm, String ss);
    JSONObject getQuotationItems(String id);
    void invoiceExport(HttpServletRequest request, HttpServletResponse response, String currencyid);
    Quotation sendInvoiceMail(HttpServletRequest request,HttpServletResponse response,String companyid, String userid, String currencyid, String billid,String
            customername,String address, String[] emails, String personid, String htmlMsg, String plainMsg, String subject, boolean sendPdf,ServletContext servletContext);
}
