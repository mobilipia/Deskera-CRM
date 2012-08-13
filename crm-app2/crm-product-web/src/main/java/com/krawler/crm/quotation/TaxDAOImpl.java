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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.krawler.common.admin.Company;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.Quotation;
import com.krawler.crm.database.tables.Tax;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;

public class TaxDAOImpl extends BaseDAO implements TaxDAO{
	public KwlReturnObject saveTax(HashMap<String, Object> dataMap) throws ServiceException {
		List list = new ArrayList();

		return new KwlReturnObject(true, null, null, list, list.size());
	}

	public KwlReturnObject getTax(String companyId) throws ServiceException {
		List list = new ArrayList();

		String Hql = "select t from Tax t where t.company.companyID = ?";
		list = executeQuery(Hql, new Object[] { companyId });

		return new KwlReturnObject(true, null, null, list, list.size());
	}

	public KwlReturnObject deleteTax(String taxId, String companyId) throws ServiceException {
		String delQuery = "delete from Tax  where ID=? and company.companyID=?";
		int numRows = executeUpdate(delQuery, new Object[] { taxId, companyId });
		return new KwlReturnObject(true, "Tax has been deleted successfully.", null, null, numRows);
	}

	public KwlReturnObject addTax(Map<String, Object> taxMap) throws ServiceException {
		List list = new ArrayList();
		try {
			Tax tax = new Tax();
			String soid = (String) taxMap.get("id");
			if (StringUtil.isNullOrEmpty(soid)) {

				tax.setId(StringUtil.generateUUID());
			} else {
				tax = (Tax) get(Tax.class, soid);
			}

			if (taxMap.containsKey("taxname")) {
				tax.setName((String) taxMap.get("taxname"));
			}
//			if (taxMap.containsKey("taxcode")) {
//				tax.setTaxCode((String) taxMap.get("taxcode"));
//			}
			if (taxMap.containsKey("percent")) {
				tax.setPercent(Float.valueOf(taxMap.get("percent").toString()));
			}
//			if (taxMap.containsKey("applydate")) {
//				tax.setApplyDate(Long.valueOf(taxMap.get("applydate").toString()));
//			}
			if (taxMap.containsKey("companyid")) {
				Company company = taxMap.get("companyid") == null ? null : (Company) get(Company.class, (String) taxMap.get("companyid"));
				tax.setCompany(company);
			}
			save(tax);
			list.add(tax);
		} catch (Exception e) {
			throw ServiceException.FAILURE("addTax : " + e.getMessage(), e);
		}
		return new KwlReturnObject(true, "Tax has been added successfully", null, list, list.size());
	}
	
	public KwlReturnObject getTaxReferencesFromQuotationDetails(String taxId) throws ServiceException {
		List list = new ArrayList();

		String Hql = "select t from QuotationDetail t where t.tax.id = ?";
		list = executeQuery(Hql, new Object[] { taxId });

		return new KwlReturnObject(true, null, null, list, list.size());
	}
	public KwlReturnObject getTaxReferencesFromQuotation(String taxId) throws ServiceException {
		List list = new ArrayList();

		String Hql = "select t from Quotation t where t.tax.id = ?";
		list = executeQuery(Hql, new Object[] { taxId });

		return new KwlReturnObject(true, null, null, list, list.size());
	}

}
