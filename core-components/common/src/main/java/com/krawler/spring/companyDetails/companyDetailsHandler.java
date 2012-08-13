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
package com.krawler.spring.companyDetails;

import com.krawler.common.admin.Company;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;

/**
 *
 * @author Karthik
 */
public class companyDetailsHandler {
    
    public static JSONObject getCompanyJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            while (ite.hasNext()) {
//                Object[] row = (Object[]) ite.next();
                Company company =(Company)ite.next();

                JSONObject obj = new JSONObject();
                obj.put("phone", company.getPhoneNumber());
                obj.put("state", company.getState());
                obj.put("currency", (company.getCurrency() == null ? Constants.CURRENCY_DEFAULT : company.getCurrency().getCurrencyID()));
                obj.put("city", company.getCity());
                obj.put("emailid", company.getEmailID());
                obj.put("companyid", company.getCompanyID());
                obj.put("timezone", (company.getTimeZone() == null ? Constants.TIMEZONE_DEFAULT : company.getTimeZone().getTimeZoneID()));
                obj.put("zip", company.getZipCode());
                obj.put("fax", company.getFaxNumber());
                obj.put("website", company.getWebsite());
                obj.put("image", company.getCompanyLogo());
                obj.put("modifiedon", (company.getModifiedOn() == null ? "" : authHandler.getDateFormatter(timeFormatId, timeZoneDiff).format(company.getModifiedOn())));
                obj.put("createdon", (company.getCreatedOn() == null ? "" : authHandler.getDateFormatter(timeFormatId, timeZoneDiff).format(company.getCreatedOn())));
                obj.put("companyname", company.getCompanyName());
                obj.put("country", (company.getCountry() == null ? "" : company.getCountry().getID()));
                obj.put("address", company.getAddress());
                obj.put("subdomain", company.getSubDomain());
                obj.put("companyid", company.getCompanyID());
                obj.put("companyname", company.getCompanyName());
                obj.put("admin_fname", company.getCreator().getFirstName());
                obj.put("admin_lname", company.getCreator().getLastName());
                obj.put("admin_uname", company.getCreator().getUserLogin().getUserName());
                obj.put("phoneno", company.getPhoneNumber());
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return jobj;
    }

    public static String getCompanyid(List ll) throws ServiceException {
        String companyId = "";
        try {
            Iterator ite = ll.iterator();
            if (ite.hasNext()) {
                companyId = (String) ite.next();
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsHandler.getCompanyid", e);
        }
        return companyId;
    }
}
