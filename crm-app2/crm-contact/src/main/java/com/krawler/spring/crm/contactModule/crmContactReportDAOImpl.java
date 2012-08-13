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
package com.krawler.spring.crm.contactModule; 

import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class crmContactReportDAOImpl extends BaseDAO implements crmContactReportDAO {

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactReportDAO#contactsByLeadSourceReport(java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject contactsByLeadSourceReport(HashMap<String, Object> requestParams, StringBuffer usersList) {
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
		String Hql = "select distinct c from contactOwners co inner join co.contact c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid is not NULL ";
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
			String[] searchcol = new String[] { "c.firstname", "c.lastname" };
			Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
		}
		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and co.usersByUserid.userID in (" + usersList + ") ";
		}
		selectInQuery += " order by c.crmCombodataByLeadsourceid ";
		ll = executeQuery(selectInQuery, filter_params.toArray());
		dl = ll.size();
		if (StringUtil.isNullOrEmpty(export)) {
			ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
		}

		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	} 

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactReportDAO#getLeadSourceContactsChart(java.lang.String, java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject getLeadSourceContactsChart(String name, HashMap<String, Object> requestParams,
			StringBuffer usersList) {
		int dl = 0;
		Object[] params = null;
		List ll = null;
		String Hql = "";
		String companyid = requestParams.get("companyid").toString();
		if (!name.equals("Undefined")) {
			Hql = "select distinct c from contactOwners co inner join co.contact c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid.ID = ?";
			params = new Object[] { companyid, name };
		} else {
			Hql = "select distinct c from contactOwners co inner join co.contact c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid.value is null ";
			params = new Object[] { companyid };
		}
		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and co.usersByUserid.userID in (" + usersList + ")   ";
		}
		ll = executeQuery(selectInQuery, params);
		dl = ll.size();

		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

    //Account Report
	/* (non-Javadoc)
	 * @see com.krawler.spring.crm.contactModule.crmContactReportDAO#accountsWithContactReport(java.util.HashMap, java.lang.StringBuffer)
	 */
	public KwlReturnObject accountsWithContactReport(HashMap<String, Object> requestParams, StringBuffer usersList) {
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

		String Hql = "select distinct cc from CrmContact cc inner join cc.crmAccount ca inner join cc.contactOwners co inner join ca.accountOwners cao where ca.company.companyID = ? and ca.deleteflag =0 and ca.validflag=1 and ca.isarchive='F' and cc.deleteflag=0 and cc.validflag=1 and cc.isarchive='F' ";
		filter_params.add(companyid);
		if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
			int chk = Integer.parseInt(requestParams.get("cd").toString());
			if (chk == 1) {
				if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
					Long fromDate =Long.parseLong(requestParams.get("frm").toString());
					Long toDate = Long.parseLong(requestParams.get("to").toString());
					Hql += " and ca.createdOn >= ? and ca.createdOn <= ? ";
					filter_params.add(fromDate);
					filter_params.add(toDate);
				}
			}
		}
		if (!StringUtil.isNullOrEmpty(quickSearch)) {
			String[] searchcol = new String[] { "cc.crmAccount.accountname" };
			Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
		}
		String selectInQuery = Hql;
		boolean heirarchyPermCon = false;
		if (requestParams.containsKey("heirarchyPermCon") && requestParams.get("heirarchyPermCon") != null) {
			heirarchyPermCon = Boolean.parseBoolean(requestParams.get("heirarchyPermCon").toString());
		}
		if (!heirarchyPermCon) {
			selectInQuery += " and co.usersByUserid.userID in (" + usersList + ") ";
		}
		boolean heirarchyPermAcc = false;
		if (requestParams.containsKey("heirarchyPermAcc") && requestParams.get("heirarchyPermAcc") != null) {
			heirarchyPermAcc = Boolean.parseBoolean(requestParams.get("heirarchyPermAcc").toString());
		}
		if (!heirarchyPermAcc) {
			selectInQuery += " and cao.usersByUserid.userID in (" + usersList + ") ";
		}
		selectInQuery += " order by cc.crmAccount.accountname ";
		ll = executeQuery(selectInQuery, filter_params.toArray());
		dl = ll.size();
		if (StringUtil.isNullOrEmpty(export)) {
			ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[] { start, limit });
		}

		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactReportDAO#getAccountContactChart(java.lang.String, java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject getAccountContactChart(String id, HashMap<String, Object> requestParams,
			StringBuffer usersList) {
		int dl = 0;
		Object[] params = null;
		List ll = null;
		String Hql = "";
		String companyid = requestParams.get("companyid").toString();
		if (!id.equals("Undefined")) {
			Hql = "select distinct c from contactOwners co inner join co.contact c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmAccount.accountid =?";
			params = new Object[] { companyid, id };
		} else {
			Hql = "select distinct c from contactOwners co inner join co.contact c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmAccount.accountname is null ";
			params = new Object[] { companyid };
		}
		String selectInQuery = Hql;
		boolean heirarchyPerm = false;
		if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
			heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
		}
		if (!heirarchyPerm) {
			selectInQuery += " and co.usersByUserid.userID in (" + usersList + ")   ";
		}
		ll = executeQuery(selectInQuery, params);
		dl = ll.size();

		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

    //Lead Report
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.contactModule.crmContactReportDAO#convertedLeadsToContactReport(java.util.HashMap, java.lang.StringBuffer)
     */
    public KwlReturnObject convertedLeadsToContactReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        try {
            String companyid = requestParams.get("companyid").toString();
            String Hql = "select distinct cc,cl from CrmContact cc inner join cc.Lead cl inner join cl.leadOwners clo inner join cc.contactOwners cco where cc.company.companyID=? and cc.deleteflag=0 and cc.validflag=1 and cc.isarchive='F' and cl.deleteflag=0 and cl.validflag=1 and cl.isarchive='F' and cl.isconverted=1 ";
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
                String[] searchcol = new String[]{"cl.lastname"};
                Hql +=StringUtil.getSearchquery(quickSearch, searchcol,filter_params);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPermCon = false;
            if(requestParams.containsKey("heirarchyPermCon") && requestParams.get("heirarchyPermCon") != null) {
                heirarchyPermCon = Boolean.parseBoolean(requestParams.get("heirarchyPermCon").toString());
            }
            if(!heirarchyPermCon){
                selectInQuery += " and cco.usersByUserid.userID in (" + usersList + ")   ";
            }
            boolean heirarchyPermLea = false;
            if(requestParams.containsKey("heirarchyPermLea") && requestParams.get("heirarchyPermLea") != null) {
                heirarchyPermLea = Boolean.parseBoolean(requestParams.get("heirarchyPermLea").toString());
            }
            if(!heirarchyPermLea){
                selectInQuery += " and clo.usersByUserid.userID in (" + usersList + ")   ";
            }
            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmLeadReportDAOImpl.convertedLeadsToContactReport", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
}
