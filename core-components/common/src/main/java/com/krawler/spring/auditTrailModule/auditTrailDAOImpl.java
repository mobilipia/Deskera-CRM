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
package com.krawler.spring.auditTrailModule;
import static com.krawler.common.util.Constants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.krawler.common.admin.AuditAction;
import com.krawler.common.admin.AuditTrail;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.Search.SearchBean;
import com.krawler.luceneSearchService.LuceneSearch;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;

public class auditTrailDAOImpl extends BaseDAO implements auditTrailDAO {
    private storageHandlerImpl storageHandlerImplObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private LuceneSearch LuceneSearchObj;
    private AuditIndex AuditIndexObj;

    public void setAuditIndex(AuditIndex AuditIndexObj) {
        this.AuditIndexObj = AuditIndexObj;
    }

    /**
     * @param storageHandlerImplObj1
     */
    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }

    /**
     * @param sessionHandlerImplObj1
     */
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    /**
     *
     * @param LuceneSearchObj
     */
    public void setLuceneSearch(LuceneSearch LuceneSearchObj) {
        this.LuceneSearchObj = LuceneSearchObj;
    }
    /* (non-Javadoc)
     * @see com.krawler.spring.auditTrailModule.auditTrailDAO#insertAuditLog(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String)
     */
    public void insertAuditLog(String actionid, String details, HttpServletRequest request, String recid, String extraid)  throws ServiceException{
        try {
            AuditAction action=(AuditAction)get(AuditAction.class, actionid);
            insertAuditLog(action, details, request, recid, extraid);
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.auditTrailModule.auditTrailDAO#insertAuditLog(java.lang.String, java.lang.String, javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    public void insertAuditLog(String actionid, String details, HttpServletRequest request, String recid)  throws ServiceException{
        try {
            AuditAction action=(AuditAction)get(AuditAction.class, actionid);
            insertAuditLog(action, details, request, recid, "0");
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.auditTrailModule.auditTrailDAO#insertAuditLog(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void insertAuditLog(String actionid, String details, String ipAddress, String userid, String recid)  throws ServiceException{
        try {
            AuditAction action=(AuditAction)get(AuditAction.class, actionid);
            User user=(User)get(User.class, userid);
            insertAuditLog(action, details, ipAddress, user, recid, "0");
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    /**
     * @param action
     * @param details
     * @param request
     * @param recid
     * @param extraid
     * @throws ServiceException
     */
    public void insertAuditLog(AuditAction action, String details, HttpServletRequest request, String recid, String extraid)  throws ServiceException{
        try {
            User user=(User)get(User.class, sessionHandlerImplObj.getUserid(request));
            String ipaddr = null;
            if(StringUtil.isNullOrEmpty(request.getHeader("x-real-ip"))){
                ipaddr = request.getRemoteAddr();
            }else{
                ipaddr = request.getHeader("x-real-ip");
            }

            insertAuditLog(action, details, ipaddr, user, recid, extraid);
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    /**
     * @param action
     * @param details
     * @param ipAddress
     * @param user
     * @param recid
     * @param extraid
     * @throws ServiceException
     */
    public void insertAuditLog(AuditAction action, String details, String ipAddress, User user, String recid, String extraid)  throws ServiceException{
        try {
            String aid = UUID.randomUUID().toString();
            AuditTrail auditTrail=new AuditTrail();
            auditTrail.setID(aid);
            auditTrail.setAction(action);
            auditTrail.setAuditTime(new Date());
            auditTrail.setDetails(details);
            auditTrail.setIPAddress(ipAddress);
            auditTrail.setRecid(recid);
            auditTrail.setUser(user);
            auditTrail.setExtraid(extraid);

            // set action id
            if (action != null)
            {
                auditTrail.setAuditGroupId(action.getAuditGroupId());
            }

            // set user id
            if (user != null)
            {
                auditTrail.setUserId(user.getUserId());
            }

            // set company id
            if (user != null)
            {
                auditTrail.setCompanyId(user.getCompany().getCompanyId());
            }

            save(auditTrail);

            indexAuditLogEntry(auditTrail);
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    /**
     *
     * @param auditTrail
     */
    public void indexAuditLogEntry(AuditTrail auditTrail) {
        try {
            List<String> indexNames = getAuditIndexNamesList();
            List<Object> indexValues = getAuditIndexValuesList(auditTrail);
            List<String> sortableIndexName = getAuditSortIndexList();
            String indexPath = storageHandlerImpl.GetAuditTrailIndexPath();
            AuditIndexObj.add(indexNames,indexValues, sortableIndexName, indexPath);
            if (!AuditIndexObj.isIsWorking()) {
                Thread auditIndexWriteThread = new Thread(AuditIndexObj);
                auditIndexWriteThread.start();
            }
        } catch(Exception ex){
            logger.warn("indexAuditLogEntry: "+ex.getMessage(), ex);
        }
    }

    public ArrayList<String> getAuditIndexNamesList() {
        ArrayList<String> indexFieldName = new ArrayList<String>();
        indexFieldName.add(AUDIT_INDEX_details);
        indexFieldName.add(AUDIT_INDEX_transactionId);
        indexFieldName.add(AUDIT_INDEX_ipAddr);
        indexFieldName.add(AUDIT_INDEX_userName);
        indexFieldName.add(AUDIT_INDEX_companyId);
        indexFieldName.add(AUDIT_INDEX_auditGroupId);
        indexFieldName.add(AUDIT_INDEX_action);
        indexFieldName.add(AUDIT_INDEX_auditTime);
        return indexFieldName;
    }

    public ArrayList<String> getAuditSortIndexList() {
        ArrayList<String> sortableIndexName = new ArrayList<String>();
        sortableIndexName.add(AUDIT_INDEX_auditTime);
        return sortableIndexName;
    }

    public ArrayList<Object> getAuditIndexValuesList(AuditTrail auditTrail) {
        ArrayList<Object> indexFieldDetails = new ArrayList<Object>();
        indexFieldDetails.add(auditTrail.getDetails().toLowerCase()); //Convert to lowercase for comparision
        indexFieldDetails.add(auditTrail.getID());
        indexFieldDetails.add(auditTrail.getIPAddress());
        User user = auditTrail.getUser();
        String userName = user.getUserLogin().getUserName() + " " + user.getFirstName() + " " + user.getLastName();
        indexFieldDetails.add(userName.toLowerCase()); //Convert to lowercase for comparision
        indexFieldDetails.add(user.getCompany().getCompanyID());
        indexFieldDetails.add(auditTrail.getAction().getAuditGroup().getID());
        indexFieldDetails.add(auditTrail.getAction().getID());
        indexFieldDetails.add(Long.toString(auditTrail.getAudittime()));
        return indexFieldDetails;
    }


    /* (non-Javadoc)
     * @see com.krawler.spring.auditTrailModule.auditTrailDAO#reloadLuceneIndex()
     */
    public KwlReturnObject reloadLuceneIndex()  throws ServiceException{
        List ll = null;
        int dl = 0;
        try {
            String query = "select count(id) from AuditTrail order by audittime ";
            ll = executeQuery(query);
            dl = ll.size();
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    public KwlReturnObject reloadLuceneIndex(int start , int limit)  throws ServiceException{
        List ll = null;
        int dl = 0;
        try {
            String query = "from AuditTrail order by audittime ";
            ll = executeQueryPaging(query,new Integer[]{start, limit});
            dl = ll.size();
        } catch (Exception e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public boolean doReloadAuditIndex(){
        boolean doReload = false;
        String query = "select reloadindex from lucene_master where activity=? and reloadindex=?";
        SqlRowSet rs = queryForRowSetJDBC(query, new Object[]{"audittrail", 1});
        if (rs.next()) {
            doReload = true;
        }
        return doReload;
    }

    public void resetAuditIndexFlag(){
        String query = "update lucene_master set reloadindex=? where activity=? and reloadindex=?";
        updateJDBC(query, new Object[]{0, "audittrail", 1});
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.auditTrailModule.auditTrailDAO#getRecentActivityDetails(java.util.HashMap)
     */
    public KwlReturnObject getRecentActivityDetails(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        String recid = "";
        String companyid = "";
        String detailFlag = "";
        try {
            if (requestParams.containsKey("recid") && requestParams.get("recid") != null) {
                recid = requestParams.get("recid").toString();
            }
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            if (requestParams.containsKey("detailFlag") && requestParams.get("detailFlag") != null) {
                detailFlag = requestParams.get("detailFlag").toString();
            }
            String query = "from AuditTrail at where user.company.companyID=? and recid=? ";
            //@@@ - Need to change this crm specific query.
            //Instead of hardcoding string in the query, use seprate flag in audittrail table.
            if(!StringUtil.isNullOrEmpty(detailFlag)){
                query += " and ((details like 'Lead Status%') or (at.action.actionName like 'Lead converted') or (details like 'Owner%') or (details like '%Task for Lead%') or (details like '%Event for Lead%')) ";
            }
            query += " order by audittime desc ";

            ll = executeQueryPaging(query, new Object[]{companyid, recid}, new Integer[]{0, 15});
            dl = ll.size();

        } catch (Exception e) {
            throw ServiceException.FAILURE("detailPanelDAOImpl.getRecentActivityDetails : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.auditTrailModule.auditTrailDAO#getAuditData(java.util.HashMap)
     */
    public KwlReturnObject getAuditData(HashMap<String, Object> requestParams) throws ServiceException {
        int start = 0;
        int limit = 30;
        String groupid = "";
        String searchtext = "";
        String companyid = "";
        List ll = null;
        String query="";
        String query1="";
        Date fromDate=null;
        Date toDate=null;

        int dl = 0;
        try {
            if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            if (requestParams.containsKey("groupid") && requestParams.get("groupid") != null) {
                groupid = requestParams.get("groupid").toString();
            }
            if (requestParams.containsKey("search") && requestParams.get("search") != null) {
                searchtext = requestParams.get("search").toString();
            }
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            if (requestParams.containsKey("frm") && requestParams.get("frm") != null) {
                fromDate = new Date(requestParams.get("frm").toString());
            }
            if (requestParams.containsKey("to") && requestParams.get("to") != null) {
                toDate = new Date(requestParams.get("to").toString());
            }

            String auditID = "";
            ArrayList filter_params = new ArrayList();
            if (searchtext.compareTo("") != 0) {
                String query2 = QueryParser.escape(searchtext) + "*";
                SearchBean bean = new SearchBean();
                String indexPath = storageHandlerImpl.GetAuditTrailIndexPath();
                String[] searchWithIndex = {"details", "ipaddr", "username"};
                Hits hitResult = bean.skynetsearchMulti(query2, searchWithIndex, indexPath);
                if (hitResult != null) {
                    Iterator itrH = hitResult.iterator();
                    while (itrH.hasNext()) {
                        Hit hit1 = (Hit) itrH.next();
                        org.apache.lucene.document.Document doc = hit1.getDocument();
                        auditID += "'" + doc.get("transactionid") + "',";
                    }
                    if (auditID.length() > 0) {
                        auditID = auditID.substring(0, auditID.length() - 1);
                    }
                }
            }

            if (groupid.compareTo("") != 0 && searchtext.compareTo("") != 0 && !requestParams.containsKey("frm") && !requestParams.containsKey("to")) {  /* query for both gid and search  */
                if (auditID.length() > 0) {
                    query = "from AuditTrail where user.company.companyID=? and ID in (" + auditID + ") and action.auditGroup.ID = ? order by audittime desc";
                    ll = executeQuery(query, new Object[]{companyid, groupid});
                    dl = ll.size();
                    ll = executeQueryPaging(query, new Object[]{companyid, groupid}, new Integer[]{start, limit});
                } else {
                    dl = 0;
                    ll = new ArrayList();
                }
            }
            else if(groupid.compareTo("") != 0 && searchtext.compareTo("") != 0 && requestParams.containsKey("frm") && requestParams.containsKey("to")) {  /* query for both gid and search and date filter*/
                if (auditID.length() > 0) {
                    query = "from AuditTrail where user.company.companyID=? and ID in (" + auditID + ") and action.auditGroup.ID = ? and audittime >= ? and audittime <= ? order by audittime desc";
                    ll = executeQuery(query, new Object[]{companyid, groupid, fromDate.getTime(), toDate.getTime()});
                    dl = ll.size();
                    ll = executeQueryPaging(query, new Object[]{companyid, groupid, fromDate.getTime(), toDate.getTime()}, new Integer[]{start, limit});
                } else {
                    dl = 0;
                    ll = new ArrayList();
                }
            } else if (groupid.compareTo("") != 0 && searchtext.compareTo("") == 0 && !requestParams.containsKey("frm") && !requestParams.containsKey("to")) { /* query only for gid  */
                query = "from AuditTrail where user.company.companyID=? and action.auditGroup.ID = ? order by audittime desc";
                ll = executeQuery(query, new Object[]{companyid, groupid});
                dl = ll.size();
                ll = executeQueryPaging(query, new Object[]{companyid, groupid}, new Integer[]{start, limit});
            }
             else if (groupid.compareTo("") != 0 && searchtext.compareTo("") == 0 && requestParams.containsKey("frm") && requestParams.containsKey("to")) { /* query for gid and date filter */
                query = "from AuditTrail where user.company.companyID=? and action.auditGroup.ID = ? and audittime >= ? and audittime <= ? order by audittime desc";
                ll = executeQuery(query, new Object[]{companyid, groupid, fromDate.getTime(), toDate.getTime()});
                dl = ll.size();
                ll = executeQueryPaging(query, new Object[]{companyid, groupid, fromDate.getTime(), toDate.getTime()}, new Integer[]{start, limit});
            } else if (groupid.compareTo("") == 0 && searchtext.compareTo("") != 0 && !requestParams.containsKey("frm") && !requestParams.containsKey("to")) {  /* query only for search  */
                if (auditID.length() > 0) {
                    query = "from AuditTrail where user.company.companyID=? and ID in (" + auditID + ")  order by audittime desc";
                    ll = executeQuery(query, new Object[]{companyid});
                    dl = ll.size();
                    ll = executeQueryPaging(query, new Object[]{companyid}, new Integer[]{start, limit});
                } else {
                    dl = 0;
                    ll = new ArrayList();
                }
            }
            else if (groupid.compareTo("") == 0 && searchtext.compareTo("") != 0 && requestParams.containsKey("frm") && requestParams.containsKey("to")) {  /* query for search and date filter */
                if (auditID.length() > 0) {
                    query = "from AuditTrail where user.company.companyID=? and ID in (" + auditID + ") and audittime >= ? and audittime <= ? order by audittime desc";
                    ll = executeQuery(query, new Object[]{companyid, fromDate.getTime(), toDate.getTime()});
                    dl = ll.size();
                    ll = executeQueryPaging(query, new Object[]{companyid, fromDate.getTime(), toDate.getTime()}, new Integer[]{start, limit});
                } else {
                    dl = 0;
                    ll = new ArrayList();
                }
            } else {        /* query for all  */
                query1 = "from AuditTrail where user.company.companyID=?  ";
                filter_params.add(companyid);
                if (requestParams.containsKey("frm") && requestParams.containsKey("to")) {
                    fromDate = new Date(requestParams.get("frm").toString());
                    toDate = new Date(requestParams.get("to").toString());
                    query1+= " and audittime >= ? and audittime <= ?";
                    filter_params.add(fromDate.getTime());
                    filter_params.add(toDate.getTime());
                }
                    query1+= " order by audittime desc ";
                ll = executeQuery(query1, filter_params.toArray());
                dl = ll.size();
                ll = executeQueryPaging(query1, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (IOException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }


    /**
     *
     * @param requestParams
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    public KwlReturnObject getAuditIndexData(HashMap<String, Object> requestParams) throws ServiceException {
        int start = 0;
        int limit = 30;
        List ll = new ArrayList();
        int dl = 0;
        try {
            StringBuilder query = new StringBuilder();
            String[] searchWithIndex = {AUDIT_INDEX_details, AUDIT_INDEX_userName, AUDIT_INDEX_ipAddr};
            String indexPath = storageHandlerImpl.GetAuditTrailIndexPath();

            String searchtext = "";
            if (requestParams.containsKey("search") && requestParams.get("search") != null) {
                searchtext = requestParams.get("search").toString().toLowerCase();
                if(searchtext.length()>0){
                    searchtext = QueryParser.escape(searchtext);
                    if(searchtext.length()>2){
                        searchtext = !searchtext.contains(" ") ? "*"+searchtext+"*" : searchtext; //Add '*'(wildcard) for searching on single token
                    }
                    query.append(searchtext);
                }
            }

            String groupid = "";
            if (requestParams.containsKey("groupid") && requestParams.get("groupid") != null) {
                groupid = requestParams.get("groupid").toString();
                if(groupid.length()>0){
                	if(!groupid.equals(Document)){ // document groupid
                    query.append(query.length()>0?" AND ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_auditGroupId);
                    query.append(":");
                    query.append(groupid);
                    query.append(")");
                }
                }
            }
         // check for document group id if yes, then include all related audit actions also

            String companyid = "";
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
                query.append(query.length()>0?" AND ":"");
                query.append("(");
                query.append(AUDIT_INDEX_companyId);
                query.append(":");
                query.append(companyid);
                query.append(")");
            }
            if(Document.equals(groupid)){

            	if(query.length()>0){
                    query.append(query.length()>0?" AND ":"");
                    query.append("((");
                    query.append(AUDIT_INDEX_auditGroupId);
                    query.append(":");
                    query.append(groupid);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(CAMPAIGN_DOC_UPLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(LEAD_DOC_UPLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(CONTACT_DOC_UPLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(PRODUCT_DOC_UPLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(ACCOUNT_DOC_UPLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(OPPORTUNITY_DOC_UPLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(CASE_DOC_UPLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(ACTIVITY_DOC_UPLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(CAMPAIGN_DOC_DOWNLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(LEAD_DOC_DOWNLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(CONTACT_DOC_DOWNLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(PRODUCT_DOC_DOWNLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(ACCOUNT_DOC_DOWNLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(OPPORTUNITY_DOC_DOWNLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(CASE_DOC_DOWNLOAD);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(ACTIVITY_DOC_DOWNLOAD);
                    query.append(")");
                }

           
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(CAMPAIGN_DOC_DELETED);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(LEAD_DOC_DELETED);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(CONTACT_DOC_DELETED);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(PRODUCT_DOC_DELETED);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(ACCOUNT_DOC_DELETED);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(OPPORTUNITY_DOC_DELETED);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(CASE_DOC_DELETED);
                    query.append(")");
                }
            	if(query.length()>0){
                    query.append(query.length()>0?" OR ":"");
                    query.append("(");
                    query.append(AUDIT_INDEX_action);
                    query.append(":");
                    query.append(ACTIVITY_DOC_DELETED);
                    query.append("))");
                }
            	}
            Date fromDate=null;
            Date toDate=null;
            if (requestParams.containsKey("frm") && requestParams.get("frm") != null && requestParams.containsKey("to") && requestParams.get("to") != null) {
                fromDate = new Date(requestParams.get("frm").toString());
                toDate = new Date(requestParams.get("to").toString());
                query.append(query.length()>0?" AND ":"");
                query.append("(");
                query.append(AUDIT_INDEX_auditTime);
                query.append(":[");
                query.append(fromDate.getTime());
                query.append(" TO ");
                query.append(toDate.getTime());
                query.append("])");
            }

            if (query.length()>0) {
                Sort sort = null;
                if(requestParams.containsKey("field")) {
                    sort = new Sort(new SortField(requestParams.get("field").toString(), SortField.STRING, requestParams.get("direction").toString().equals("DESC") ? true : false));
                } else
                    sort = new Sort(new SortField(AUDIT_INDEX_auditTime, SortField.STRING, true));
                if (requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
                    start = Integer.parseInt(requestParams.get("start").toString());
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                }
                Hits hitResult = LuceneSearchObj.searchIndexWithSort(query.toString(), searchWithIndex, indexPath, sort);
                if (hitResult != null) {
                    dl = hitResult.length();
                    int count = Math.min(dl, start+limit);
                    String auditUUID;
                    AuditTrail audittrail;
                    for(int i=start; i<count; i++){
                        org.apache.lucene.document.Document doc = hitResult.doc(i);
                        auditUUID = doc.get(AUDIT_INDEX_transactionId);
                        audittrail = (AuditTrail) get(AuditTrail.class, auditUUID);
                        if(audittrail==null){
                        	count=Math.min(dl, count+1);
                        }else
                        	ll.add(audittrail);
                    }
                }
            }
        } catch (IOException ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.auditTrailModule.auditTrailDAO#getAuditGroupData()
     */
    public KwlReturnObject getAuditGroupData() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String query = "from AuditGroup";
            ll = executeQuery(query);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.auditTrailModule.auditTrailDAO#getAuditDetails(java.util.HashMap)
     */
    public KwlReturnObject getAuditDetails(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            StringBuffer usersList = null;
            List<String> groupList = (List<String>) requestParams.get("groups");
            StringBuilder groups = new StringBuilder();
            if(groupList != null && !groupList.isEmpty()){
                for (String group: groupList)
                {
                    Long groupId = AuditTrailConstants.auditGroupMap.get(group);
                    if (groupId != null)
                    {
                        groups.append(groupId);
                        groups.append(',');
                    }
                }
            }

            if (groups.length() > 0)
            {
                groups.deleteCharAt(groups.length() - 1);
            }

            int start = Integer.parseInt(requestParams.get("start").toString());
            int limit = Integer.parseInt(requestParams.get("limit").toString());
            int interval = Integer.parseInt(requestParams.get("interval").toString());

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, interval * -1);

            String countquery = "select count(at.ID) from AuditTrail at where";
            String query = "select at from AuditTrail at where";
            String filterQuery = "";
            if(requestParams.containsKey("userslist") && !StringUtil.isNullOrEmpty(requestParams.get("userslist").toString())) {
                usersList = (StringBuffer) requestParams.get("userslist");
                filterQuery += " at.userId in (" + usersList + ")  and";
            }
            if(requestParams.containsKey("companyid") && !StringUtil.isNullOrEmpty(requestParams.get("companyid").toString()))
            {
                String companyid = requestParams.get("companyid").toString();
                Company company = (Company) get(Company.class, companyid);
                Long newCompanyId = company == null? null: company.getCompanyId();
                filterQuery += " at.companyId  = "+newCompanyId+" and at.audittime >= ?";
            }
            if(!StringUtil.isNullOrEmpty(groups.toString())){
            filterQuery += " and " +
                    "at.auditGroupId in (" + groups.toString() + ") ";
            }
            countquery += filterQuery;
            query += filterQuery + " order by at.audittime desc ";
            ll = executeQuery(countquery, new Object[]{ cal.getTimeInMillis()});
            if (ll != null && !ll.isEmpty())
            {
                try
                {
                    dl = Integer.parseInt(ll.get(0).toString());
                } catch (Exception e){}
            }

            ll = executeQueryPaging(query, new Object[]{ cal.getTimeInMillis()}, new Integer[]{start, limit});
        } catch (Exception e) {
            throw ServiceException.FAILURE("auditTrailDAOImpl.getAuditDetails} : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, "002", "", ll, dl);
    }

    public List<KwlReturnObject> getAuditDetails(List<Map<String, Object>> requestParamsList, int start, int limit, int interval, String companyId) throws ServiceException {

        Company company = (Company) get(Company.class, companyId);

        Long newCompanyId = company == null? null: company.getCompanyId();

        List<KwlReturnObject> results = new ArrayList<KwlReturnObject>();

        String userList = null;
        StringBuilder groupListWithUserSb = new StringBuilder();
        StringBuilder groupListWithoutUserSb = new StringBuilder();
        List<Long> groupListWithUser = new ArrayList<Long>();
        List<Long> groupListWithoutUser = new ArrayList<Long>();

        List<Map<String, Object>> requestParamsWithUsers = new ArrayList<Map<String,Object>>();
        List<Map<String, Object>> requestParamsWithoutUsers = new ArrayList<Map<String,Object>>();

        for (Map<String, Object> requestParams : requestParamsList)
        {
            // divide the map in two parts based on users list
            if (requestParams.containsKey("userslist") && !StringUtil.isNullOrEmpty(requestParams.get("userslist").toString()))
            {
                requestParamsWithUsers.add(requestParams);
                if (userList == null)
                {
                    userList = requestParams.get("userslist").toString();
                }
                String groups = requestParams.get("groups").toString();
                if (!StringUtil.isNullOrEmpty(groups))
                {
                    Long groupId = AuditTrailConstants.auditGroupMap.get(groups);
                    groupListWithUserSb.append(groupId).append(",");
                    groupListWithUser.add(groupId);
                }
            }
            else
            {
                requestParamsWithoutUsers.add(requestParams);
                String groups = requestParams.get("groups").toString();
                if (!StringUtil.isNullOrEmpty(groups))
                {
                    Long groupId = AuditTrailConstants.auditGroupMap.get(groups);
                    groupListWithoutUserSb.append(groupId).append(",");
                    groupListWithoutUser.add(groupId);
                }
            }
        }

        if (groupListWithUserSb.length() > 0)
        {
            groupListWithUserSb.deleteCharAt(groupListWithUserSb.length() - 1);
        }

        if (groupListWithoutUserSb.length() > 0)
        {
            groupListWithoutUserSb.deleteCharAt(groupListWithoutUserSb.length() - 1);
        }

        String groupsWithUser = groupListWithUserSb.length() > 0 ? groupListWithUserSb.toString() : null;
        String groupsWithoutUser = groupListWithoutUserSb.length() > 0 ? groupListWithoutUserSb.toString() : null;

        try
        {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, interval * -1);

            Map<Long, Long> countWithUser = convertToGroupMap(getAuditDetailsCount(userList, groupsWithUser, start, limit, newCompanyId, cal));
            Map<Long, Long> countWithoutUser = convertToGroupMap(getAuditDetailsCount(userList, groupsWithoutUser, start, limit, newCompanyId, cal));

            Map<Long, List<DashboardUpdate>> auditListWithUser = convertToGroupListMap(getAuditDetails(userList, groupListWithUser, start, limit, newCompanyId, cal));
            Map<Long, List<DashboardUpdate>> auditListWithoutUser = convertToGroupListMap(getAuditDetails(userList, groupListWithoutUser, start, limit, newCompanyId, cal));

            for (Long groupId: groupListWithUser)
            {
                Long count = 0l;
                List<DashboardUpdate> records = null;
                if (countWithUser.containsKey(groupId))
                {
                    count = countWithUser.get(groupId);
                }
                if (auditListWithUser.containsKey(groupId))
                {
                    records = auditListWithUser.get(groupId);
                }
                String group = AuditTrailConstants.auditGroupReverseMap.get(groupId);
                results.add(new KwlReturnObject(true, group, "", records, count.intValue()));
            }

            for (Long groupId: groupListWithoutUser)
            {
                Long count = 0l;
                List<DashboardUpdate> records = null;
                if (countWithoutUser.containsKey(groupId))
                {
                    count = countWithoutUser.get(groupId);
                }

                if (auditListWithoutUser.containsKey(groupId))
                {
                    records = auditListWithoutUser.get(groupId);
                }
                String group = AuditTrailConstants.auditGroupReverseMap.get(groupId);
                results.add(new KwlReturnObject(true, group, "", records, count.intValue()));
            }

        } catch (Exception e)
        {
            logger.error("auditTrailDAOImpl.getAuditDetails} : " + e.getMessage(), e);
        }
        return results;
    }

    private Map<Long, Long> convertToGroupMap(List<Object[]> records)
    {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();
        if (records != null)
        {
            for (Object[] rec: records)
            {
                Long key = (Long) rec[0];
                Long count = 0l;
                try
                {
                    count = Long.parseLong(String.valueOf(rec[1]));
                } catch (NumberFormatException ne){}

                resultMap.put(key, count);
            }
        }
        return resultMap;
    }

    private Map<Long, List<DashboardUpdate>> convertToGroupListMap(List<DashboardUpdate> records)
    {
        Map<Long, List<DashboardUpdate>> resultMap = new HashMap<Long, List<DashboardUpdate>>();
        if (records != null)
        {
            for (DashboardUpdate rec: records)
            {
                Long key = rec.getAuditGroupId();

                if (resultMap.containsKey(key))
                {
                    List<DashboardUpdate> list = resultMap.get(key);
                    list.add(rec);
                }
                else
                {
                    List<DashboardUpdate> list = new ArrayList<DashboardUpdate>();
                    list.add(rec);
                    resultMap.put(key, list);
                }
            }
        }
        return resultMap;
    }

    @SuppressWarnings("unchecked")
    private List<DashboardUpdate> getAuditDetails(String userList, List<Long> groupList, int start, int limit, Long companyId, Calendar cal)
    {
        if (groupList == null || groupList.isEmpty())
        {
            return null;
        }
        StringBuilder query = new StringBuilder();
        int count = 0;
        for (Long groupId: groupList)
        {
            if (count++ > 0)
            {
                query.append(" UNION ");
            }
            query.append("(select atr.audit_group_id AS audit_group_id, atr.USER AS user_id, atr.details AS details, atr.audittime AS audit_time, atr.recid AS recid, u.fname AS first_name, u.lname AS last_name ");
            query.append("from audit_trail atr ");
            query.append("INNER JOIN users u ON atr.user_id = u.user_id ");
            query.append("WHERE ");
            if (userList != null)
            {
                query.append(" atr.user_id in (" + userList + ")  and");
            }
            if (companyId != null)
            {
                query.append(" atr.company_id  = ").append(companyId).append(" and ");
            }

            query.append(" atr.audittime >= ").append(cal.getTimeInMillis()).append(" and ").append(("atr.audit_group_id = " + groupId));
            query.append(" order by atr.audittime desc limit ").append(start).append(',').append(limit).append(')');
        }
        return queryJDBC(query.toString(), null, new BeanPropertyRowMapper<DashboardUpdate>(DashboardUpdate.class));
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> getAuditDetailsCount(String userList, String groupList, int start, int limit, Long companyId, Calendar cal)
    {
        if (groupList == null || groupList.isEmpty())
        {
            return null;
        }
        List<Object[]> results = null;

        StringBuilder countquery = new StringBuilder("select at.auditGroupId, count(at.auditGroupId) from AuditTrail at where");
        if (userList != null)
        {
            countquery.append(" at.userId in (" + userList + ")  and");
        }
        if (companyId != null)
        {
            countquery.append(" at.companyId  = ").append(companyId).append(" and ");
        }

        countquery.append(" at.audittime >= ? ").append((groupList == null ? "" : "and at.auditGroupId in (" + groupList + ") "));
        countquery.append("group by at.auditGroupId");
        results = executeQuery(countquery.toString(), cal.getTimeInMillis());
        return results;
    }
}
