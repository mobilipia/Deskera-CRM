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
package com.krawler.spring.crm.campaignModule;

import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Karthik
 */
public class crmCampaignReportDAOImpl extends BaseDAO implements crmCampaignReportDAO {

    @Override
    public KwlReturnObject campaignByTypeReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.get("reportid").toString();
        String quickSearch = requestParams.get("ss").toString();
        String filterCombo = requestParams.containsKey("filterCombo") ? requestParams.get("filterCombo").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (!StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c from CrmCampaign c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCampaigntypeid is not NULL ";
        filter_params.add(companyid);
        if (!requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                filter_params.add(fromDate);
                filter_params.add(toDate);
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.campaignname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        if (!StringUtil.isNullOrEmpty(filterCombo)) {
            Hql += " and c.crmCombodataByCampaigntypeid.ID='" + filterCombo + "' ";
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")  ";
        }
        selectInQuery += " order by c.crmCombodataByCampaigntypeid ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject completedCampaignByTypeReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.get("reportid").toString();
        String quickSearch = requestParams.get("ss").toString();
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (!StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c from CrmCampaign c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCampaignstatusid.mainID = '" + Constants.CAMPAIGN_COMPLETE + "' and c.crmCombodataByCampaigntypeid is NOT NULL";
        filter_params.add(companyid);
        if (!requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                filter_params.add(fromDate);
                filter_params.add(toDate);
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.campaignname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by c.crmCombodataByCampaigntypeid ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject campaignWithGoodResponseReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.get("reportid").toString();
        String quickSearch = requestParams.get("ss").toString();
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList filter_params = new ArrayList();
        if (!StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c from CrmCampaign c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and CONVERT(c.expectedresponse,DECIMAL(64,4)) > 70";
        filter_params.add(companyid);
        if (!requestParams.get("cd").toString().equals("")) {
            int chk = Integer.parseInt(requestParams.get("cd").toString());
            if (chk == 1) {
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                filter_params.add(fromDate);
                filter_params.add(toDate);
            }
        }
        if (!StringUtil.isNullOrEmpty(quickSearch)) {
            String[] searchcol = new String[]{"c.campaignname"};
            Hql += StringUtil.getSearchquery(quickSearch, searchcol, filter_params);
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
        }
        selectInQuery += " order by CONVERT(c.expectedresponse,UNSIGNED) ";
        ll = executeQuery(selectInQuery, filter_params.toArray());
        dl = ll.size();
        if (StringUtil.isNullOrEmpty(export)) {
            ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getCampaignResponseChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String Hql = "";
        String companyid = requestParams.get("companyid").toString();
        if (!name.equals("Undefined")) {
            Hql = "select c.crmCombodataByCampaigntypeid.value,count(c) from CrmCampaign c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and CONVERT(c.expectedresponse,DECIMAL(64,4)) > 70";
            params = new Object[]{companyid};
        } else {
            Hql = "select c from CrmCampaign c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCampaigntypeid is null and CONVERT(c.expectedresponse,DECIMAL(64,4)) > 70";
            params = new Object[]{companyid};
        }
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")   ";
        }
        if (!name.equals("Undefined")) {
            selectInQuery += " group by  c.crmCombodataByCampaigntypeid ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getCampaignTypeChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c from CrmCampaign c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCampaigntypeid.ID =?";
        params = new Object[]{companyid, name};
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ")  ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);

    }

    @Override
    public KwlReturnObject getCompletedCampaignChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String Hql = "select c from CrmCampaign c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByCampaignstatusid.mainID = '" + Constants.CAMPAIGN_COMPLETE + "' and c.crmCombodataByCampaigntypeid.ID=?";
        params = new Object[]{companyid, name};
        String selectInQuery = Hql;
        boolean heirarchyPerm = false;
        if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
            heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
        }
        if (!heirarchyPerm) {
            selectInQuery += " and c.usersByUserid.userID in (" + usersList + ") ";
        }
        ll = executeQuery(selectInQuery, params);
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
}
