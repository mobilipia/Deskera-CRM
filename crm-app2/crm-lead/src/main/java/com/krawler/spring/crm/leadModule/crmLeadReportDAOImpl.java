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
package com.krawler.spring.crm.leadModule;
 
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.hibernate.HibernateException;

public class crmLeadReportDAOImpl extends BaseDAO implements crmLeadReportDAO {

    @Override
    public KwlReturnObject getConvertedLeadsWeekDayViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result, String leadsFlag) throws ServiceException {
        List ll = null;
        String Hql = "";
        Object[] params = null;
        int dl = 0;
        try {
            String companyid = requestParams.get("companyid").toString();
            Hql = "select count(distinct c.leadid ), weekday(FROM_UNIXTIME(c.createdOn/1000)) from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.company.companyID= ? and c.validflag=1 and c.isarchive='F'   ";

            if(!StringUtil.isNullOrEmpty(leadsFlag)) {
                if (leadsFlag.equals("11")) {
                     Hql += " and c.isconverted= 1  ";
                } else if (leadsFlag.equals("39")) {
                    Hql += " and c.crmCombodataByLeadstatusid.mainID = '" + Constants.LEADSTATUSID_QUALIFIED + "' ";
                } else if (leadsFlag.equals("42")) {
                    Hql += " and c.crmCombodataByLeadstatusid.mainID = '"+Constants.LEADSTATUSID_CONTACTED+"' and c.istransfered=0 ";
                }
            }
            params = new Object[]{companyid};
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
               Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
               params = new Object[]{companyid, fromDate, toDate};
            } else {
                Calendar cal = Calendar.getInstance();
                Hql += " and year(FROM_UNIXTIME(c.createdOn/1000))="+cal.get(Calendar.YEAR)+" ";
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            selectInQuery += " group by weekday(FROM_UNIXTIME(c.createdOn/1000)) order by weekday(FROM_UNIXTIME(c.createdOn/1000)) ";
            ll = executeQuery(selectInQuery, params);
            Iterator ite = ll.iterator();
            dl = ll.size();
            int k = 0;
            while(ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                Long cnt = (Long) row[0];
                int weekDay = (Integer) row[1];
                if(k < weekDay) {
                    while(k != weekDay) {
                        result += "<value xid=\"" + k + "\" >0</value>";
                        k++;
                    }
                }
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
                k++;
            }
            while(k <= 7){
                result += "<value xid=\"" + k + "\" >0</value>";
                k++;
            }
            ll = new ArrayList();
            ll.add(result);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getConvertedLeadsMonthViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result, String leadsFlag) throws ServiceException {
        List ll = null;
        String Hql = "";
        Object[] params = null;
        int dl = 0;
        try {
            String companyid = requestParams.get("companyid").toString();
            Hql = "select count(distinct c.leadid), month(FROM_UNIXTIME(c.createdOn/1000)) from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.company.companyID= ? and c.validflag=1 and c.isarchive='F' ";

            if(!StringUtil.isNullOrEmpty(leadsFlag)) {
                if (leadsFlag.equals("11")) {
                     Hql += " and c.isconverted= 1  ";
                } else if (leadsFlag.equals("39")) {
                    Hql += " and c.crmCombodataByLeadstatusid.mainID = '" + Constants.LEADSTATUSID_QUALIFIED + "' ";
                } else if (leadsFlag.equals("42")) {
                    Hql += " and c.crmCombodataByLeadstatusid.mainID = '"+Constants.LEADSTATUSID_CONTACTED+"' and  c.istransfered=0 ";
                }
            }

            params = new Object[]{companyid};
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                params = new Object[]{companyid, fromDate, toDate};
            } else {
                Calendar cal = Calendar.getInstance();
                Hql += " and year(FROM_UNIXTIME(c.createdOn/1000))="+cal.get(Calendar.YEAR)+" ";
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            selectInQuery += " group by month(FROM_UNIXTIME(c.createdOn/1000)) order by month(FROM_UNIXTIME(c.createdOn/1000)) ";
            ll = executeQuery(selectInQuery, params);
            Iterator ite = ll.iterator();
            dl = ll.size();
            int k = 1;
            while(ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                Long cnt = (Long) row[0];
                int month = (Integer) row[1];
                if(k < month) {
                    while(k != month) {
                        result += "<value xid=\"" + (k-1) + "\" >0</value>";
                        k++;
                    }
                }
                result += "<value xid=\"" + (k-1) + "\" >" + cnt.toString() + "</value>";
                k++;
            }
            while(k <= 12){
                result += "<value xid=\"" + (k-1) + "\" >0</value>";
                k++;
            }
            ll = new ArrayList();
            ll.add(result);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getConvertedLeadsYearViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result, String leadsFlag,Boolean isSeries) throws ServiceException {
        List ll = null;
        String Hql = "";
        String result1 = result;
        Object[] params = null;
        int dl = 0;
        try {
            String companyid = requestParams.get("companyid").toString();
            Hql = "select count(distinct c.leadid), year(FROM_UNIXTIME(c.createdOn/1000)) from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.company.companyID= ? and c.validflag=1 and c.isarchive='F'";

            if(!StringUtil.isNullOrEmpty(leadsFlag)) {
                if (leadsFlag.equals("11")) {
                     Hql += " and c.isconverted= 1   ";
                } else if (leadsFlag.equals("39")) {
                    Hql += " and c.crmCombodataByLeadstatusid.mainID = '" + Constants.LEADSTATUSID_QUALIFIED + "' ";
                } else if (leadsFlag.equals("42")) {
                    Hql += " and c.crmCombodataByLeadstatusid.mainID = '"+Constants.LEADSTATUSID_CONTACTED+"'  and c.istransfered=0 ";
                }
            }

            params = new Object[]{companyid};
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                params = new Object[]{companyid, fromDate, toDate};
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            selectInQuery += " group by year(FROM_UNIXTIME(c.createdOn/1000)) order by year(FROM_UNIXTIME(c.createdOn/1000)) ";
            ll = executeQuery(selectInQuery, params);
            Iterator ite = ll.iterator();
            dl = ll.size();
            int k = 0;
            result = "";
            while(ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                Long cnt = ((Number) row[0]).longValue();
                Long  year = ((Number) row[1]).longValue();
                if(isSeries)
                    result += "<value xid=\"" + k + "\" >" + year.toString() + "</value>";
                else
                    result1 += "<value xid=\"" + k + "\" >"+cnt.toString()+"</value>";
                k++;
            }           
            result = result1 + result;
            ll = new ArrayList();
            ll.add(result);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getConvertedQuaterlyViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result, String leadsFlag) throws ServiceException {
        List ll = null;
        String Hql = "";
        Object[] params = null;
        int k = 0;
        Long cnt;
        try {
            String companyid = requestParams.get("companyid").toString();
            Hql = "select count(distinct c.leadid) from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.company.companyID= ? and c.validflag=1 and c.isarchive='F' ";

            if(!StringUtil.isNullOrEmpty(leadsFlag)) {
                if (leadsFlag.equals("11")) {
                     Hql += " and c.isconverted= 1  ";
                } else if (leadsFlag.equals("39")) {
                    Hql += " and c.crmCombodataByLeadstatusid.mainID = '" + Constants.LEADSTATUSID_QUALIFIED + "' ";
                } else if (leadsFlag.equals("42")) {
                    Hql += " and c.crmCombodataByLeadstatusid.mainID = '"+Constants.LEADSTATUSID_CONTACTED+"' and c.istransfered=0 ";
                }
            }

            params = new Object[]{companyid};
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
               Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                params = new Object[]{companyid, fromDate, toDate};
            } else {
                Calendar cal = Calendar.getInstance();
                Hql += " and year(FROM_UNIXTIME(c.createdOn/1000))="+cal.get(Calendar.YEAR)+" ";
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            selectInQuery += " and month(FROM_UNIXTIME(c.createdOn/1000)) in (1,2,3) ";
            ll = executeQuery(selectInQuery, params);
            if(ll.get(0) != null) {
                cnt = (Long) ll.get(0);
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
            } else {
                result += "<value xid=\"" + k + "\" >0</value>";
            }
            k++;
            selectInQuery = Hql ;
            selectInQuery += " and month(FROM_UNIXTIME(c.createdOn/1000)) in (4,5,6) ";
            ll = executeQuery(selectInQuery, params);
            if(ll.get(0) != null) {
                cnt = (Long) ll.get(0);
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
            } else {
                result += "<value xid=\"" + k + "\" >0</value>";
            }
            k++;
            selectInQuery = Hql ;
            selectInQuery += " and month(FROM_UNIXTIME(c.createdOn/1000)) in (7,8,9) ";
            ll = executeQuery(selectInQuery, params);
            if(ll.get(0) != null) {
                cnt = (Long) ll.get(0);
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
            } else {
                result += "<value xid=\"" + k + "\" >0</value>";
            }
            k++;
            selectInQuery = Hql ;
            selectInQuery += " and month(FROM_UNIXTIME(c.updatedOn/1000)) in (10,11,12) ";
            ll = executeQuery(selectInQuery, params);
            if(ll.get(0) != null) {
                cnt = (Long) ll.get(0);
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
            } else {
                result += "<value xid=\"" + k + "\" >0</value>";
            }
            ll = new ArrayList();
            ll.add(result);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, k);
    }

    @Override
    public KwlReturnObject getConvertedLeadsToWeekDayViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result) throws ServiceException {
        List ll = null;
        String Hql = "";
        Object[] params = null;
        int dl = 0;
        try {
            String reportId = requestParams.get("reportid").toString();
            String module = "";
            String table="";
            String field="";
            if(reportId.equals("52")) {
                module = "Account";
                table=" accountOwners oo inner join oo.account c  inner join c.crmLead cl inner join cl.leadOwners clo ";
                field="count(distinct c.accountid)";
            } else if(reportId.equals("53")) {
                module = "Opportunity";
                table=" opportunityOwners oo inner join oo.opportunity c inner join c.crmLead cl inner join cl.leadOwners clo ";
                field="count(distinct c.oppid)";
            } else if(reportId.equals("54")) {
                table=" contactOwners oo inner join oo.contact c inner join c.Lead cl inner join cl.leadOwners clo ";
                field="count(distinct c.contactid)";
                module = "Contact";
            }
            String companyid = requestParams.get("companyid").toString();

            Hql = "select "+field+", weekday(FROM_UNIXTIME(cl.createdOn/1000)) from "+table+" where c.deleteflag=0 and c.isarchive='F' and c.company.companyID= ? and c.validflag=1 and cl.isconverted =1 and  cl.deleteflag=0 and cl.validflag =1 and cl.isarchive='F'";
            params = new Object[]{companyid};
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                Hql += " and cl.createdOn >= ? and cl.createdOn <= ? ";
                params = new Object[]{companyid, fromDate, toDate};
            } else {
                Calendar cal = Calendar.getInstance();
                Hql += " and year(FROM_UNIXTIME(cl.createdOn/1000))="+cal.get(Calendar.YEAR)+" ";
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
            }
            selectInQuery += " group by weekday(FROM_UNIXTIME(cl.createdOn/1000)) order by weekday(FROM_UNIXTIME(cl.createdOn/1000)) ";
            ll = executeQuery(selectInQuery, params);
            Iterator ite = ll.iterator();
            dl = ll.size();
            int k = 0;
            while(ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                Long cnt = (Long) row[0];
                int weekDay = (Integer) row[1];
                if(k < weekDay) {
                    while(k != weekDay) {
                        result += "<value xid=\"" + k + "\" >0</value>";
                        k++;
                    }
                }
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
                k++;
            }
            while(k <= 7){
                result += "<value xid=\"" + k + "\" >0</value>";
                k++;
            }
            ll = new ArrayList();
            ll.add(result);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getConvertedLeadsToMonthViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result) throws ServiceException {
        List ll = null;
        String Hql = "";
        Object[] params = null;
        int dl = 0;
        try {
            String reportId = requestParams.get("reportid").toString();
            String module = "";
            String table="";
            String field="";
            if(reportId.equals("52")) {
                module = "Account";
                table=" accountOwners oo inner join oo.account c  inner join c.crmLead cl inner join cl.leadOwners clo";
                field="count(distinct c.accountid)";
            } else if(reportId.equals("53")) {
                module = "Opportunity";
                table=" opportunityOwners oo inner join oo.opportunity c inner join c.crmLead cl inner join cl.leadOwners clo ";
                field="count(distinct c.oppid)";
            } else if(reportId.equals("54")) {
                table=" contactOwners oo inner join oo.contact c inner join c.Lead cl inner join cl.leadOwners clo  ";
                field="count(distinct c.contactid)";
                module = "Contact";
            }
            String companyid = requestParams.get("companyid").toString();

            Hql = "select "+field+", month(FROM_UNIXTIME(cl.createdOn/1000)) from "+table+" where c.deleteflag=0  and c.company.companyID= ? and c.validflag=1 and cl.isconverted =1 and  cl.deleteflag=0 and cl.validflag =1 and cl.isarchive='F'";
            params = new Object[]{companyid};
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                Hql += " and cl.createdOn >= ? and cl.createdOn <= ? ";
                params = new Object[]{companyid, fromDate, toDate};
            } else {
                Calendar cal = Calendar.getInstance();
                Hql += " and year(FROM_UNIXTIME(cl.createdOn/1000))="+cal.get(Calendar.YEAR)+" ";
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
            }
            selectInQuery += " group by month(FROM_UNIXTIME(cl.createdOn/1000)) order by month(FROM_UNIXTIME(cl.createdOn/1000)) ";
            ll = executeQuery(selectInQuery, params);
            Iterator ite = ll.iterator();
            dl = ll.size();
            int k = 1;
            while(ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                Long cnt = (Long) row[0];
                int month = (Integer) row[1];
                if(k < month) {
                    while(k != month) {
                        result += "<value xid=\"" + (k - 1) + "\" >0</value>";
                        k++;
                    }
                }
                result += "<value xid=\"" + (k - 1) + "\" >" + cnt.toString() + "</value>";
                k++;
            }
            while(k <= 12){
                result += "<value xid=\"" + (k - 1) + "\" >0</value>";
                k++;
            }
            ll = new ArrayList();
            ll.add(result);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getConvertedLeadsToYearViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result) throws ServiceException {
        List ll = null;
        String Hql = "";
        String result1 = result;
        Object[] params = null;
        int dl = 0;
        try {
            String reportId = requestParams.get("reportid").toString();
            String module = "";
            String table="";
            String field="";
            if(reportId.equals("52")) {
                module = "Account";
                table=" accountOwners oo inner join oo.account c  inner join c.crmLead cl inner join cl.leadOwners clo ";
                field="count(distinct c.accountid)";
            } else if(reportId.equals("53")) {
                module = "Opportunity";
                table=" opportunityOwners oo inner join oo.opportunity c inner join c.crmLead cl inner join cl.leadOwners clo ";
                field="count(distinct c.oppid)";
            } else if(reportId.equals("54")) {
                table=" contactOwners oo inner join oo.contact c inner join c.Lead cl inner join cl.leadOwners clo ";
                field="count(distinct c.contactid)";
                module = "Contact";
            }
            String companyid = requestParams.get("companyid").toString();

            Hql = "select "+field+", year(FROM_UNIXTIME(cl.createdOn/1000)) from "+table+" where c.deleteflag=0 and c.isarchive='F' and c.company.companyID= ? and c.validflag=1 and cl.isconverted =1 and  cl.deleteflag=0 and cl.validflag =1 and cl.isarchive='F' ";
            params = new Object[]{companyid};
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                Hql += " and cl.createdOn >= ? and cl.createdOn <= ? ";
                params = new Object[]{companyid, fromDate, toDate};
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
            }
            selectInQuery += " group by year(FROM_UNIXTIME(cl.createdOn/1000)) order by year(FROM_UNIXTIME(cl.createdOn/1000)) ";
            ll = executeQuery(selectInQuery, params);
            Iterator ite = ll.iterator();
            dl = ll.size();
            int k = 0;
            result = "";
            while(ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                Long cnt = (Long) row[0];
                int year = (Integer) row[1];
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
                result1 += "<value xid=\"" + k + "\" >"+year+"</value>";
                k++;
            }
            result1 += "</series><graphs><graph gid=\"0\">";
            result = result1 + result;
            ll = new ArrayList();
            ll.add(result);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getConvertedQuaterlyToViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result) throws ServiceException {
        List ll = null;
        String Hql = "";
        Object[] params = null;
        int k = 0;
        Long cnt;
        try {
            String reportId = requestParams.get("reportid").toString();
            String module = "";
            String table="";
            String field="";
            if(reportId.equals("52")) {
                module = "Account";
                table=" accountOwners oo inner join oo.account c inner join c.crmLead cl inner join cl.leadOwners clo ";
                field="count(distinct c.accountid)";
            } else if(reportId.equals("53")) {
                module = "Opportunity";
                table=" opportunityOwners oo inner join oo.opportunity c inner join c.crmLead cl inner join cl.leadOwners clo ";
                field="count(distinct c.oppid)";
            } else if(reportId.equals("54")) {
                table=" contactOwners oo inner join oo.contact c inner join c.Lead cl inner join cl.leadOwners clo ";
                field="count(distinct c.contactid)";
                module = "Contact";
            }
            String companyid = requestParams.get("companyid").toString();

            Hql = "select "+field+" from "+table+" where c.deleteflag=0 and c.isarchive='F' and c.company.companyID= ? and c.validflag=1 and cl.isconverted =1 and  cl.deleteflag=0 and cl.validflag =1 and cl.isarchive='F' ";
            params = new Object[]{companyid};
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                Hql += " and cl.createdOn >= ? and cl.createdOn <= ? ";
                params = new Object[]{companyid, fromDate, toDate};
            } else {
                Calendar cal = Calendar.getInstance();
                Hql += " and year(FROM_UNIXTIME(cl.createdOn/1000))="+cal.get(Calendar.YEAR)+" ";
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
            }
            selectInQuery += " and month(FROM_UNIXTIME(cl.createdOn/1000)) in (1,2,3) ";
            ll = executeQuery(selectInQuery, params);
            if(ll.get(0) != null) {
                cnt = (Long) ll.get(0);
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
            } else {
                result += "<value xid=\"" + k + "\" >0</value>";
            }
            k++;
            selectInQuery = Hql ;
            selectInQuery += " and month(FROM_UNIXTIME(cl.createdOn/1000)) in (4,5,6) ";
            ll = executeQuery(selectInQuery, params);
            if(ll.get(0) != null) {
                cnt = (Long) ll.get(0);
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
            } else {
                result += "<value xid=\"" + k + "\" >0</value>";
            }
            k++;
            selectInQuery = Hql ;
            selectInQuery += " and month(FROM_UNIXTIME(cl.createdOn/1000)) in (7,8,9) ";
            ll = executeQuery(selectInQuery, params);
            if(ll.get(0) != null) {
                cnt = (Long) ll.get(0);
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
            } else {
                result += "<value xid=\"" + k + "\" >0</value>";
            }
            k++;
            selectInQuery = Hql ;
            selectInQuery += " and month(FROM_UNIXTIME(cl.createdOn/1000)) in (10,11,12) ";
            ll = executeQuery(selectInQuery, params);
            if(ll.get(0) != null) {
                cnt = (Long) ll.get(0);
                result += "<value xid=\"" + k + "\" >" + cnt.toString() + "</value>";
            } else {
                result += "<value xid=\"" + k + "\" >0</value>";
            }
            ll = new ArrayList();
            ll.add(result);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, k);
    }

    @Override
    public KwlReturnObject leadsByIndustryReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
            String companyid = requestParams.get("companyid").toString();
            String Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where  c.deleteflag=0  and c.isconverted= '0' and c.company.companyID= ?  and c.validflag=1 and c.isarchive='F'";
            ArrayList params = new ArrayList();
            params.add(companyid);
            if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
                int chk = Integer.parseInt(requestParams.get("cd").toString());
                if (chk == 1) {
                	if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
                    	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
        				Long toDate = Long.parseLong(requestParams.get("to").toString());
                            Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                            params.add(fromDate);
                            params.add(toDate);
                    }
                }
            }
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                String[] searchcol = new String[]{"c.lastname"};
                Hql +=StringUtil.getSearchquery(quickSearch, searchcol,params);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            if (requestParams.containsKey("industryid") && requestParams.get("industryid") != "") {
                selectInQuery += " and c.crmCombodataByIndustryid.ID = ?";
                params.add(requestParams.get("industryid"));
            }
            selectInQuery += " and c.crmCombodataByIndustryid.ID is not null";
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, params.toArray(), new Integer[]{start, limit});
            }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject convertedLeadsReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
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
            String companyid = requestParams.get("companyid").toString();
            String Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where  c.deleteflag=0  and  c.company.companyID= ?   and c.validflag=1 and c.isarchive= 'F'  and c.crmCombodataByLeadstatusid.mainID='" + Constants.LEADSTATUSID_QUALIFIED + "' and c.isconverted= 1 ";
            filter_params.add(companyid);
            if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
                int chk = Integer.parseInt(requestParams.get("cd").toString());
                if (chk == 1) {
                	if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
                    	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
        				Long toDate = Long.parseLong(requestParams.get("to").toString());
                        Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                        filter_params.add(fromDate);
                        filter_params.add(toDate);
                    }
                }
            }
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                String[] searchcol = new String[]{"c.lastname"};
                Hql +=StringUtil.getSearchquery(quickSearch, searchcol,filter_params);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject leadsPipelineReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        ArrayList params = new ArrayList();
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
        Long fromDate=null;
        Long toDate=null;
        boolean heirarchyPerm = false;
        try {
            String companyid = requestParams.get("companyid").toString();
            String condition = " where  c.deleteflag=0 and c.company.companyID= ?  and c.validflag=1 and c.isarchive='F' ";
            String Hql = "select c.crmCombodataByLeadstatusid.value, c.crmCombodataByLeadstatusid.percentStage, sum(c.revenue), sum(c.price), count(c.leadid) from CrmLead c  ";
            params.add(companyid);
            SimpleDateFormat sdf=new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
            if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	fromDate =Long.parseLong(requestParams.get("frm").toString());
				toDate = Long.parseLong(requestParams.get("to").toString());
                condition += " and c.createdOn >= ? and c.createdOn <= ? ";
                params.add(fromDate);
                params.add(toDate);
            }
            String[] searchcol = new String[]{"c.crmCombodataByLeadstatusid.value"};
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                condition +=StringUtil.getSearchquery(quickSearch, searchcol,params);
            }
            String selectInQuery = Hql+condition;
                       
            if (!heirarchyPerm) {
                selectInQuery += " and c.leadid in ( select distinct c.leadid from LeadOwners lo inner join lo.leadid c "+ condition+" and lo.usersByUserid.userID in (" + usersList + ") )  ";
                params.add(companyid);
                if(fromDate!=null && toDate!=null)
                {
                		params.add(fromDate);
                		params.add(toDate);
                }
                StringUtil.insertParamSearchString(params, quickSearch, searchcol.length);
            }
            selectInQuery += "group by c.crmCombodataByLeadstatusid.value order by (c.crmCombodataByLeadstatusid.percentStage*1) desc";
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, params.toArray(), new Integer[]{start, limit});
                dl = start + limit;
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmLeadReportDAOImpl.leadsPipelineReport", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject leadsBySourceReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        String export = requestParams.containsKey("reportid") ? requestParams.get("reportid").toString() : "";
        String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
        int start = 0;
        int limit = 25;
        int dl = 0;
        if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
            start = Integer.parseInt(requestParams.get("start").toString());
            limit = Integer.parseInt(requestParams.get("limit").toString());
        }
        List ll = null;
            String companyid = requestParams.get("companyid").toString();
            String Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where  c.deleteflag=0  and c.istransfered= '0' and c.company.companyID= ?  and c.validflag=1 and c.isarchive='F' ";
            ArrayList params = new ArrayList();
            params.add(companyid);
            if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
                int chk = Integer.parseInt(requestParams.get("cd").toString());
                if (chk == 1) {
                	if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
                    	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
        				Long toDate = Long.parseLong(requestParams.get("to").toString());
                        Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                        params.add(fromDate);
                        params.add(toDate);
                    }
                }
            }
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                String[] searchcol = new String[]{"c.lastname"};
                Hql +=StringUtil.getSearchquery(quickSearch, searchcol,params);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            if (requestParams.containsKey("sourceid") && requestParams.get("sourceid") != "") {
                selectInQuery += " and c.crmCombodataByLeadsourceid.ID = ?";
                params.add(requestParams.get("sourceid"));
            }
            selectInQuery += " and c.crmCombodataByLeadsourceid.ID is not null";
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, params.toArray(), new Integer[]{start, limit});
            }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject qualifiedLeadsReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
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
            String companyid = requestParams.get("companyid").toString();
            String Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadstatusid.mainID = '" + Constants.LEADSTATUSID_QUALIFIED + "' ";
            filter_params.add(companyid);
            if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
                int chk = Integer.parseInt(requestParams.get("cd").toString());
                if (chk == 1) {
                	if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
                    	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
        				Long toDate = Long.parseLong(requestParams.get("to").toString());
                        Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                        filter_params.add(fromDate);
                        filter_params.add(toDate);
                    }
                }
            }
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                String[] searchcol = new String[]{"c.lastname"};
                Hql +=StringUtil.getSearchquery(quickSearch, searchcol,filter_params);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ") ";
            }
            selectInQuery += " order by c.updatedOn desc ";
            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject contactedLeadsReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
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
            String companyid = requestParams.get("companyid").toString();
            String Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadstatusid.mainID = '" +Constants.LEADSTATUSID_CONTACTED + "'";
            filter_params.add(companyid);
            if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
                int chk = Integer.parseInt(requestParams.get("cd").toString());
                if (chk == 1) {
                	if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
                    	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
        				Long toDate = Long.parseLong(requestParams.get("to").toString());
                        Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                        filter_params.add(fromDate);
                        filter_params.add(toDate);
                    }
                }
            }
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                String[] searchcol = new String[]{"c.lastname"};
                Hql +=StringUtil.getSearchquery(quickSearch, searchcol,filter_params);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ") ";
            }
            selectInQuery += " order by c.updatedOn desc ";
            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject openLeadsReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
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
            String companyid = requestParams.get("companyid").toString();
            String Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadstatusid.mainID = '" + Constants.LEADSTATUSID_OPEN + "'";
            filter_params.add(companyid);
            if (requestParams.containsKey("cd") && !requestParams.get("cd").toString().equals("")) {
                int chk = Integer.parseInt(requestParams.get("cd").toString());
                if (chk == 1) {
                	if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
                    	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
        				Long toDate = Long.parseLong(requestParams.get("to").toString());
                        Hql += " and c.createdOn >= ? and c.createdOn <= ? ";
                        filter_params.add(fromDate);
                        filter_params.add(toDate);
                    }
                }
            }
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                String[] searchcol = new String[]{"c.lastname"};
                Hql +=StringUtil.getSearchquery(quickSearch, searchcol,filter_params);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ") ";
            }
            selectInQuery += " order by c.createdOn desc ";
            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl = ll.size();
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getLeadsByIndustryChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        List ll = null;
        String Hql = "";
            ArrayList params = new ArrayList();
            String companyid = requestParams.get("companyid").toString();
            if (!name.equals("Undefined")) {
                Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.crmCombodataByIndustryid.ID=? and c.deleteflag=0  and c.isconverted= '0' and c.company.companyID= ?  and c.validflag=1 and c.isarchive= 'F' ";
                params.add(name);
                params.add(companyid);                
            } else {
                Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.crmCombodataByIndustryid is null and c.deleteflag=0  and c.isconverted= '0' and c.company.companyID= ?  and c.validflag=1 and c.isarchive= 'F' ";
                params.add(companyid);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                selectInQuery += " and c.createdOn >= ? and c.createdOn <= ? ";
                params.add(fromDate);
                params.add(toDate);
            }
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    @Override
    public KwlReturnObject getContactedLeadsChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        List ll = null;
        String Hql = "";
            ArrayList params = new ArrayList();
            String companyid = requestParams.get("companyid").toString();
            if (!name.equals("Undefined")) {
                Hql ="select distinct c from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid.ID = ? and c.crmCombodataByLeadstatusid.mainID = '"+Constants.LEADSTATUSID_CONTACTED+"'";
                params.add(companyid);
                params.add(name);                
            } else {
                Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid is null and c.crmCombodataByLeadstatusid.mainID = '"+Constants.LEADSTATUSID_CONTACTED+"'";
                params.add(companyid);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                selectInQuery += " and c.createdOn >= ? and c.createdOn <= ? ";
                params.add(fromDate);
                params.add(toDate);
            }
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getQualifiedLeadsChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        List ll = null;
        String Hql = "";
            ArrayList params = new ArrayList();
            String companyid = requestParams.get("companyid").toString();
            if (!name.equals("Undefined")) {
                Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid.ID = ? and c.crmCombodataByLeadstatusid.mainID='" + Constants.LEADSTATUSID_QUALIFIED + "' and c.istransfered=0";
                params.add(companyid);
                params.add(name);                
            } else {
                Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadsourceid is null and c.crmCombodataByLeadstatusid.mainID='" + Constants.LEADSTATUSID_QUALIFIED + "' and c.istransfered=0";
                params.add(companyid);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                selectInQuery += " and c.createdOn >= ? and c.createdOn <= ? ";
                params.add(fromDate);
                params.add(toDate);
            }
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getConvertedLeadsChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        List ll = null;
        String Hql = "";
            ArrayList params = new ArrayList();
            String companyid = requestParams.get("companyid").toString();
            if (name.equals("None")) {
                Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.crmCombodataByLeadsourceid is null and c.deleteflag=0  and c.isarchive= 'F' and c.isconverted= 1 and c.company.companyID= ?  and c.validflag=1 ";
                params.add(companyid);
            } else {
                Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where   c.crmCombodataByLeadsourceid.ID=? and c.deleteflag=0 and c.isarchive= 'F' and c.isconverted= 1 and c.company.companyID= ?   and c.validflag=1 ";
                params.add(name);
                params.add(companyid);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                selectInQuery += " and c.createdOn >= ? and c.createdOn <= ? ";
                params.add(fromDate);
                params.add(toDate);
            }
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    @Override
    public KwlReturnObject getConvertedLeadAccountPie(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        List ll = null;
        String Hql = "";
            ArrayList params = new ArrayList();
            String companyid = requestParams.get("companyid").toString();
            if (!name.equals("Undefined")) {
                Hql = "select distinct c from accountOwners ao inner join ao.account c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmLead is not null and c.crmLead.crmCombodataByLeadsourceid.ID=?";
                params.add(companyid);
                params.add(name);                
            } else {
                Hql = "select distinct c from accountOwners ao inner join ao.account c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmLead is not null and c.crmLead.crmCombodataByLeadsourceid is null";
                params.add(companyid);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and ao.usersByUserid.userID in (" + usersList + ")   ";
            }
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                selectInQuery += " and c.createdOn >= ? and c.createdOn <= ? ";
                params.add(fromDate);
                params.add(toDate);
            }
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    @Override
    public KwlReturnObject getConvertedLeadOppPie(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        List ll = null;
        String Hql = "";
             ArrayList params = new ArrayList();
            String companyid = requestParams.get("companyid").toString();
            if (!name.equals("Undefined")) {
                Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmLead is not null and c.crmLead.crmCombodataByLeadsourceid.ID=?";
                params.add(companyid);
                params.add(name);
            } else {
                Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmLead is not null and c.crmLead.crmCombodataByLeadsourceid is null ";
                params.add(companyid);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and oo.usersByUserid.userID in (" + usersList + ")   ";
            }
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                selectInQuery += " and c.createdOn >= ? and c.createdOn <= ? ";
                params.add(fromDate);
                params.add(toDate);
            }
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    @Override
    public KwlReturnObject getConvertedLeadContactPie(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        List ll = null;
        String Hql = "";
            ArrayList params = new ArrayList();
            String companyid = requestParams.get("companyid").toString();
            if (!name.equals("Undefined")) {
                Hql = "select distinct c from contactOwners co inner join co.contact c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmLead is not null and c.crmLead.crmCombodataByLeadsourceid.ID=?";
                params.add(companyid);
                params.add(name);                
            } else {
                Hql = "select distinct c from contactOwners co inner join co.contact c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmLead is not null and c.crmLead.crmCombodataByLeadsourceid is null ";
                params.add(companyid);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and co.usersByUserid.userID in (" + usersList + ")   ";
            }
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                selectInQuery += " and c.createdOn >= ? and c.createdOn <= ? ";
                params.add(fromDate);
                params.add(toDate);
            }
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject leadsPipelineChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        ArrayList params = new ArrayList();
        List ll = null;
        String Hql = "";
        SimpleDateFormat sdf=new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss");
            String companyid = requestParams.get("companyid").toString();
            String condition=" where  c.leadid.deleteflag=0 and c.leadid.company.companyID= ?  and c.leadid.validflag=1 and c.leadid.isarchive='F' and c.leadid.crmCombodataByLeadstatusid.ID=?";
            Hql = "select c.crmCombodataByLeadstatusid.percentStage, sum(c.revenue), sum(c.price) from CrmLead c ";
            params.add(companyid);
            params.add(name);
            if(requestParams.containsKey("frm")){
            	condition+=" and c.createdOn >= ? ";
            	params.add(Long.parseLong(requestParams.get("frm").toString()));
            }
            if(requestParams.containsKey("to")){
            	condition+=" and c.createdOn <= ? ";
            	params.add(Long.parseLong(requestParams.get("to").toString()));
            }
            String selectInQuery = Hql+condition;
            boolean heirarchyPerm = false;
            if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if (!heirarchyPerm) {
                selectInQuery += " and c.leadid in ( select distinct c.leadid from LeadOwners lo inner join lo.leadid c "+ condition+" and lo.usersByUserid.userID in (" + usersList + ") )  ";
                params.add(companyid);
                params.add(name);
            }
            selectInQuery += " group by c.crmCombodataByLeadstatusid.value ";
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getLeadsBySourceChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        List ll = null;
        String Hql = "";
            ArrayList params = new ArrayList();
            String companyid = requestParams.get("companyid").toString();
            if (!name.equals("Undefined")) {
                Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.crmCombodataByLeadsourceid.ID=? and c.deleteflag=0  and c.istransfered= '0' and c.company.companyID= ?   and c.validflag=1 and c.isarchive= 'F' ";
                params.add(name);
                params.add(companyid);                
            } else {
                Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.crmCombodataByLeadsourceid is null and c.deleteflag=0  and c.istransfered= '0' and c.company.companyID= ?   and c.validflag=1 and c.isarchive= 'F' ";
                params.add(companyid);
            }
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            if ((requestParams.containsKey("frm") && !StringUtil.isNullOrEmpty(requestParams.get("frm").toString()))
                    && (requestParams.containsKey("to") && !StringUtil.isNullOrEmpty(requestParams.get("to").toString()))){
            	Long fromDate =Long.parseLong(requestParams.get("frm").toString());
				Long toDate = Long.parseLong(requestParams.get("to").toString());
                selectInQuery += " and c.createdOn >= ? and c.createdOn <= ? ";
                params.add(fromDate);
                params.add(toDate);
            }
            ll = executeQuery(selectInQuery, params.toArray());
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getOpenLeadChart(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        int dl = 0;
        Object[] params = null;
        List ll = null;
        String Hql = "";
            String companyid = requestParams.get("companyid").toString();
            Hql = "select distinct c from LeadOwners lo inner join lo.leadid c where c.deleteflag=0 and c.isarchive= 'F' and c.company.companyID= ? and c.validflag=1 and c.crmCombodataByLeadstatusid.mainID = '"+Constants.LEADSTATUSID_OPEN+"'";
            params = new Object[]{companyid};
            
            String selectInQuery = Hql ;
            boolean heirarchyPerm = false;
            if(requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if(!heirarchyPerm){
                selectInQuery += " and lo.usersByUserid.userID in (" + usersList + ")   ";
            }
            ll = executeQuery(selectInQuery, params);
            dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
}
