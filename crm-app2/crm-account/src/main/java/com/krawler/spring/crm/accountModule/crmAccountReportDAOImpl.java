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
package com.krawler.spring.crm.accountModule;

import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class crmAccountReportDAOImpl extends BaseDAO implements crmAccountReportDAO {

	/* (non-Javadoc)
	 * @see com.krawler.spring.crm.accountModule.crmAccountReportDAO#keyAccountsReport(java.util.HashMap, java.lang.StringBuffer)
	 */
	public KwlReturnObject keyAccountsReport(HashMap<String, Object> requestParams, StringBuffer usersList) {
		String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
		String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
		int start = 0;
		int limit = 25;
		int dl = 0;
		ArrayList filter_params = new ArrayList();
		if (requestParams.containsKey("start") && requestParams.containsKey("limit")
				&& !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
			start = Integer.parseInt(requestParams.get("start").toString());
			limit = Integer.parseInt(requestParams.get("limit").toString());
		}
		List ll = null;
		String companyid = requestParams.get("companyid").toString();
		String Hql = "select distinct c from accountOwners ao inner join ao.account c  where  c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 ";
		filter_params.add(companyid);
		if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
			int chk = Integer.parseInt(requestParams.get("cd").toString());
			if (chk == 1) {
				if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
					Long fromDate =Long.parseLong((String)requestParams.get("frm"));
					Long toDate = Long.parseLong((String)requestParams.get("to"));
					Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
					filter_params.add(fromDate);
					filter_params.add(toDate);
				}
			}
		}
		if (!StringUtil.isNullOrEmpty(quickSearch)) {
			String[] searchcol = new String[] { "c.accountname" };
			Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
		}
		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and ao.usersByUserid.userID in (" + usersList + ") ";
		}
		selectInQuery += " order by (c.revenue*1) desc ";
		ll = executeQuery(selectInQuery, filter_params.toArray());
		dl = ll.size();
		if (StringUtil.isNullOrEmpty(export)) {
			ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
		}
		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	/* (non-Javadoc)
	 * @see com.krawler.spring.crm.accountModule.crmAccountReportDAO#monthlyAccountsReport(java.util.HashMap, java.lang.StringBuffer)
	 */
	public KwlReturnObject monthlyAccountsReport(HashMap<String, Object> requestParams, StringBuffer usersList) {
		String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
		String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
		int start = 0;
		int limit = 25;
		int dl = 0;
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = 0;
		if (requestParams.containsKey("month")) {
			month = Integer.valueOf(requestParams.get("month").toString());
		}
		ArrayList filter_params = new ArrayList();
		if (requestParams.containsKey("start") && requestParams.containsKey("limit")
				&& !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
			start = Integer.parseInt(requestParams.get("start").toString());
			limit = Integer.parseInt(requestParams.get("limit").toString());
		}
		if (requestParams.containsKey("year") && !StringUtil.isNullOrEmpty(requestParams.get("year").toString())) {
			year = Integer.parseInt(requestParams.get("year").toString());
		}
		List ll = null;
		String companyid = requestParams.get("companyid").toString();
		String Hql = "select distinct c from accountOwners ao inner join ao.account c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and YEAR(FROM_UNIXTIME(c.createdOn/1000)) = ?";
		filter_params.add(companyid);
		filter_params.add(year);
		if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
			int chk = Integer.parseInt(requestParams.get("cd").toString());
			if (chk == 1) {
				if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
					Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
					Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
					filter_params.add(fromDate);
					filter_params.add(toDate);
				}
			}
		}
		if (!StringUtil.isNullOrEmpty(quickSearch)) {
			String[] searchcol = new String[] { "c.accountname" };
			Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
		}
		if (month > 0 && month < 13) {
			Hql += " and MONTH(FROM_UNIXTIME(c.createdOn/1000))= " + month;
		}

		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and ao.usersByUserid.userID in (" + usersList + ") ";
		}
		selectInQuery += " order by c.createdOn ";
		ll = executeQuery(selectInQuery, filter_params.toArray());
		dl = ll.size();
		if (StringUtil.isNullOrEmpty(export)) {
			ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
		}
		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	/* (non-Javadoc)
	 * @see com.krawler.spring.crm.accountModule.crmAccountReportDAO#accountOwnersReport(java.util.HashMap, java.lang.StringBuffer)
	 */
	public KwlReturnObject accountOwnersReport(HashMap<String, Object> requestParams, StringBuffer usersList) {
		String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
		String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
		int start = 0;
		int limit = 25;
		int dl = 0;
		ArrayList filter_params = new ArrayList();
		if (requestParams.containsKey("start") && requestParams.containsKey("limit")
				&& !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
			start = Integer.parseInt(requestParams.get("start").toString());
			limit = Integer.parseInt(requestParams.get("limit").toString());
		}
		List ll = null;
		String companyid = requestParams.get("companyid").toString();
		String Hql = "select distinct c from accountOwners ao inner join ao.account c where c.deleteflag=0 and c.company.companyID= ? and c.validflag=1 and c.isarchive= 'F' ";
		filter_params.add(companyid);
		if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
			int chk = Integer.parseInt(requestParams.get("cd").toString());
			if (chk == 1) {
				if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
					Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
					Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
					filter_params.add(fromDate);
					filter_params.add(toDate);
				}
			}
		}
		if (!StringUtil.isNullOrEmpty(quickSearch)) {
			String[] searchcol = new String[] { "c.accountname" };
			Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
		}
		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and ao.usersByUserid.userID in (" + usersList + ") ";
		}
		selectInQuery += " order by ao.usersByUserid.userID ";
		ll = executeQuery(selectInQuery, filter_params.toArray());
		dl = ll.size();
		if (StringUtil.isNullOrEmpty(export)) {
			ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
		}
		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	/* (non-Javadoc)
	 * @see com.krawler.spring.crm.accountModule.crmAccountReportDAO#industryAccountTypeReport(java.util.HashMap, java.lang.StringBuffer)
	 */
	public KwlReturnObject industryAccountTypeReport(HashMap<String, Object> requestParams, StringBuffer usersList) {
		String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
		String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
		int start = 0;
		int limit = 25;
		int dl = 0;
		ArrayList filter_params = new ArrayList();
		if (requestParams.containsKey("start") && requestParams.containsKey("limit")
				&& !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
			start = Integer.parseInt(requestParams.get("start").toString());
			limit = Integer.parseInt(requestParams.get("limit").toString());
		}
		List ll = null;
		String companyid = requestParams.get("companyid").toString();
		String Hql = "select distinct c from accountOwners ao inner join ao.account c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByAccounttypeid is not NULL and c.crmCombodataByIndustryid is not NULL ";
		filter_params.add(companyid);
		if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
			int chk = Integer.parseInt(requestParams.get("cd").toString());
			if (chk == 1) {
				if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
					Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
					Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
					filter_params.add(fromDate);
					filter_params.add(toDate);
				}
			}
		}
		if (!StringUtil.isNullOrEmpty(quickSearch)) {
			String[] searchcol = new String[] { "c.accountname" };
			Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
		}
		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and ao.usersByUserid.userID in (" + usersList + ")  ";
		}
		ll = executeQuery(selectInQuery, filter_params.toArray());
		dl = ll.size();
		if (StringUtil.isNullOrEmpty(export)) {
			ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
		}

		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	/* (non-Javadoc)
	 * @see com.krawler.spring.crm.accountModule.crmAccountReportDAO#getKeyAccountschart(java.util.HashMap, java.lang.StringBuffer)
	 */
	public KwlReturnObject getKeyAccountschart(HashMap<String, Object> requestParams, StringBuffer usersList) {
		int dl = 0;
		Object[] params = null;
		List ll = null;
		String companyid = requestParams.get("companyid").toString();
		String Hql = "select distinct c from accountOwners ao inner join ao.account c  where  c.deleteflag=0 and c.company.companyID= ?  and c.validflag=1 and c.isarchive= 'F' ";
		params = new Object[] { companyid };

		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and ao.usersByUserid.userID in (" + usersList + ") ";
		}
		selectInQuery += " order by (c.revenue*1) desc ";
		ll = executeQueryPaging(selectInQuery, params, new Integer[] { 0, 10 });
		dl = ll.size();

		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	/* (non-Javadoc)
	 * @see com.krawler.spring.crm.accountModule.crmAccountReportDAO#getMonthlyAccountschart(int, java.util.HashMap, java.lang.StringBuffer)
	 */
	public KwlReturnObject getMonthlyAccountschart(int month, HashMap<String, Object> requestParams,
			StringBuffer usersList) {
		int dl = 0;
		Object[] params = null;
		List ll = null;
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		String companyid = requestParams.get("companyid").toString();
		if (requestParams.containsKey("year")) {
			year = Integer.parseInt(requestParams.get("year").toString());
		}

		String Hql = "select distinct c from accountOwners ao inner join ao.account c "
				+ "where  c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ?  and c.validflag=1 "
				+ "and MONTH(FROM_UNIXTIME(c.createdOn/1000))=? and YEAR(FROM_UNIXTIME(c.createdOn/1000))=?";
		params = new Object[] { companyid, month, year };
		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and ao.usersByUserid.userID in (" + usersList + ") ";
		}
		ll = executeQuery(selectInQuery, params);
		dl = ll.size();

		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	/* (non-Javadoc)
	 * @see com.krawler.spring.crm.accountModule.crmAccountReportDAO#getAccountsByOwnerChart(java.lang.String, java.util.HashMap, java.lang.StringBuffer)
	 */
	public KwlReturnObject getAccountsByOwnerChart(String id, HashMap<String, Object> requestParams,
			StringBuffer usersList) {
		int dl = 0;
		Object[] params = null;
		List ll = null;
		String companyid = requestParams.get("companyid").toString();
		String Hql = "select distinct c from accountOwners ao inner join ao.account c where   ao.usersByUserid.userID=? and c.deleteflag=0   and c.company.companyID= ?  and c.validflag=1 and c.isarchive= 'F' ";
		params = new Object[] { id, companyid };
		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and ao.usersByUserid.userID in (" + usersList + ")  ";
		}
		ll = executeQuery(selectInQuery, params);
		dl = ll.size();

		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	/* (non-Javadoc)
	 * @see com.krawler.spring.crm.accountModule.crmAccountReportDAO#getIndustryAccountTypeChart(java.lang.String, java.util.HashMap, java.lang.StringBuffer)
	 */
	public KwlReturnObject getIndustryAccountTypeChart(String name, HashMap<String, Object> requestParams,
			StringBuffer usersList) {
		int dl = 0;
		Object[] params = null;
		List ll = null;
		String companyid = requestParams.get("companyid").toString();
		String Hql = "select distinct c from accountOwners ao inner join ao.account c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByAccounttypeid is not NULL and c.crmCombodataByIndustryid is not NULL and c.crmCombodataByIndustryid.ID=?";
		params = new Object[] { companyid, name };
		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and ao.usersByUserid.userID in (" + usersList + ")   ";
		}
		ll = executeQuery(selectInQuery, params);
		dl = ll.size();

		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	// Lead Report
	/* (non-Javadoc)
	 * @see com.krawler.spring.crm.accountModule.crmAccountReportDAO#convertedLeadsToAccountReport(java.util.HashMap, java.lang.StringBuffer)
	 */
	public KwlReturnObject convertedLeadsToAccountReport(HashMap<String, Object> requestParams, StringBuffer usersList)
			throws ServiceException {
		String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
		String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
		int start = 0;
		int limit = 25;
		int dl = 0;
		ArrayList filter_params = new ArrayList();
		if (requestParams.containsKey("start") && requestParams.containsKey("limit")
				&& !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
			start = Integer.parseInt(requestParams.get("start").toString());
			limit = Integer.parseInt(requestParams.get("limit").toString());
		}
		List ll = null;
		try {
			String companyid = requestParams.get("companyid").toString();
			String Hql = "select distinct ca from CrmAccount ca inner join ca.crmLead cl inner join cl.leadOwners clo inner join ca.accountOwners cao where ca.company.companyID = ? and ca.deleteflag=0 and ca.validflag=1 and ca.isarchive='F' and cl.deleteflag =0 and cl.validflag = 1 and cl.isarchive='F' and cl.isconverted=1  ";
			filter_params.add(companyid);
			if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
				int chk = Integer.parseInt(requestParams.get("cd").toString());
				if (chk == 1) {
					if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
						Long fromDate =Long.parseLong(requestParams.get("frm").toString());
						Long toDate = Long.parseLong(requestParams.get("to").toString());
						Hql += " and cl.createdOn >= ? and cl.createdOn <= ? ";
						filter_params.add(fromDate);
						filter_params.add(toDate);
					}
				}
			}
			if (!StringUtil.isNullOrEmpty(quickSearch)) {
				String[] searchcol = new String[] { "cl.lastname" };
				Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
			}
			String selectInQuery = Hql;
			boolean heirarchyPermAcc = false;
			if (requestParams.containsKey("heirarchyPermAcc") && requestParams.get("heirarchyPermAcc") != null) {
				heirarchyPermAcc = Boolean.parseBoolean(requestParams.get("heirarchyPermAcc").toString());
			}
			if (!heirarchyPermAcc) {
				selectInQuery += " and cao.usersByUserid.userID in (" + usersList + ")  ";
			}
			boolean heirarchyPermLea = false;
			if (requestParams.containsKey("heirarchyPermLea") && requestParams.get("heirarchyPermLea") != null) {
				heirarchyPermLea = Boolean.parseBoolean(requestParams.get("heirarchyPermLea").toString());
			}
			if (!heirarchyPermLea) {
				selectInQuery += " and clo.usersByUserid.userID in (" + usersList + ")  ";
			}
			ll = executeQuery(selectInQuery, filter_params.toArray());
			dl = ll.size();
			if (StringUtil.isNullOrEmpty(export)) {
				ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
			}
		} catch (Exception ex) {
			throw ServiceException.FAILURE("crmLeadReportDAOImpl.convertedLeadsToAccountReport", ex);
		}
		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}
}
