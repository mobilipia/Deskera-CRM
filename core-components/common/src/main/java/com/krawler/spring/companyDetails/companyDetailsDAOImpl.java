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
import com.krawler.common.admin.CompanyHoliday;
import com.krawler.common.admin.Country;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.FileUploadHandler;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.apache.commons.fileupload.FileItem;
import org.hibernate.SessionFactory;

/**
 *
 * @author Karthik
 */
public class companyDetailsDAOImpl extends BaseDAO implements companyDetailsDAO{

    private storageHandlerImpl storageHandlerImplObj;

    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }
    
    @Override
    public KwlReturnObject getCompanyInformation(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
            ArrayList filter_params = (ArrayList) requestParams.get("filter_params");

            String query = "from Company c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            query += filterQuery;

            ll = executeQuery(query, filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.getCompanyInformation", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getCompanyHolidays(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = null;
        int dl = 0;
        String companyid = "";
        try {
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            String query = "from CompanyHoliday c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            query += filterQuery;
            
            ll = executeQuery(query, new Object[]{companyid});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.getCompanyInformation", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject updateCompany(HashMap hm) throws ServiceException {
        String companyid = "";
        DateFormat dateformat = null;
        List ll = null;
        int dl = 0;
        try {
            Company company = null;
            if (hm.containsKey("companyid") && hm.get("companyid") != null) {
                companyid = hm.get("companyid").toString();
                if(hm.containsKey("addCompany") && (Boolean) hm.get("addCompany")){
                    company = new Company();
                    company.setCompanyID(companyid);
                    
                    if (hm.containsKey("companyId") && hm.get("companyId") != null) {
                        Long companyId = (Long) hm.get("companyId");
                        company.setCompanyId(companyId);
                    }
                } else {
                    company = (Company) get(Company.class, companyid);
                }
            }
            if (hm.containsKey("dateformat") && hm.get("dateformat") != null) {
                dateformat = (DateFormat) hm.get("dateformat");
            }
            if (hm.containsKey("creater") && hm.get("creater") != null) {
                company.setCreator((User) get(User.class, (String) hm.get("creater")));
            }
            if (hm.containsKey("companyname") && hm.get("companyname") != null) {
                company.setCompanyName((String) hm.get("companyname"));
            }
            if (hm.containsKey("address") && hm.get("address") != null) {
                company.setAddress((String) hm.get("address"));
            }
            if (hm.containsKey("city") && hm.get("city") != null) {
                company.setCity((String) hm.get("city"));
            }
            if (hm.containsKey("state") && hm.get("state") != null) {
                company.setState((String) hm.get("state"));
            }
            if (hm.containsKey("zip") && hm.get("zip") != null) {
                company.setZipCode((String) hm.get("zip"));
            }
            if (hm.containsKey("phone") && hm.get("phone") != null) {
                company.setPhoneNumber((String) hm.get("phone"));
            }
            if (hm.containsKey("fax") && hm.get("fax") != null) {
                company.setFaxNumber((String) hm.get("fax"));
            }
            if (hm.containsKey("website") && hm.get("website") != null) {
                company.setWebsite((String) hm.get("website"));
            }
            if (hm.containsKey("mail") && hm.get("mail") != null) {
                company.setEmailID((String) hm.get("mail"));
            }
            if (hm.containsKey("domainname") && hm.get("domainname") != null) {
                company.setSubDomain((String) hm.get("domainname"));
            }
            if (hm.containsKey("country") && hm.get("country") != null) {
                company.setCountry((Country) get(Country.class, hm.get("country").toString()));
            }
            if (hm.containsKey("currency") && hm.get("currency") != null) {
                company.setCurrency((KWLCurrency) get(KWLCurrency.class, (String) hm.get("currency")));
            }
            if (hm.containsKey("timezone") && hm.get("timezone") != null) {
                KWLTimeZone timeZone = (KWLTimeZone) get(KWLTimeZone.class, (String) hm.get("timezone"));
                company.setTimeZone(timeZone);
            }
            if (hm.containsKey("deleteflag") && hm.get("deleteflag") != null) {
                company.setDeleted((Integer) hm.get("deleteflag"));
            }
            if (hm.containsKey("createdon") && hm.get("createdon") != null) {
                company.setCreatedOn(new Date());
            }
            if (hm.containsKey("activated") && hm.get("activated") != null) {
                company.setActivated(Boolean.TRUE.getBoolean(hm.get("activated").toString()));
            }
            company.setModifiedOn(new Date());
            if (hm.containsKey("holidays") && hm.get("holidays") != null) {
                JSONArray jArr = new JSONArray((String) hm.get("holidays"));
                Set<CompanyHoliday> holidays = company.getHolidays();
                holidays.clear();
                DateFormat formatter = dateformat;
                for (int i = 0; i < jArr.length(); i++) {
                    CompanyHoliday day = new CompanyHoliday();
                    JSONObject obj = jArr.getJSONObject(i);
                    day.setDescription(obj.getString("description"));
                    day.setHolidayDate(formatter.parse(obj.getString("day")));
                    day.setCompany(company);
                    holidays.add(day);
                }
            }
            if (hm.containsKey("logo") && hm.get("logo") != null && !StringUtil.isNullOrEmpty(hm.get("logo").toString())) {
                String imageName = ((FileItem) (hm.get("logo"))).getName();
                if (imageName != null && imageName.length() > 0) {
                    String fileName = companyid + FileUploadHandler.getCompanyImageExt();
                    company.setCompanyLogo(Constants.ImgBasePath + fileName);
                    new FileUploadHandler().uploadImage((FileItem) hm.get("logo"),
                            fileName,
                            storageHandlerImpl.GetProfileImgStorePath(), 130, 25, true, false);
                }
            }
            save(company);
            ll = new ArrayList();
            ll.add(company);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.updateCompany", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public void deleteCompany(HashMap<String, Object> requestParams) throws ServiceException {
        String companyid = "";
        try {
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            
            Company company = (Company) get(Company.class, companyid);
            company.setDeleted(1);
            
            saveOrUpdate(company);
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.deleteCompany", e);
        }
    }

    @Override
    public String getSubDomain(String companyid) throws ServiceException {
        String subdomain = "";
        try {
            Company company = (Company) get(Company.class, companyid);
            subdomain = company.getSubDomain();
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.getSubDomain", e);
        }
        return subdomain;
    }

    @Override
    public String getCompanyid(String domain) throws ServiceException {
        String companyId = "";
        List ll = new ArrayList();
        try {
            String Hql = "select companyID from Company where subDomain = ?";
            ll = executeQuery(Hql, new Object[]{domain});
            companyId = companyDetailsHandler.getCompanyid(ll);
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.getCompanyid", e);
        }
        return companyId;
    }
}
