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
package com.krawler.spring.crm.common;

import com.krawler.common.admin.AuditGroup;
import com.krawler.common.admin.AuditTrail;
import com.krawler.common.util.StringUtil;
import com.krawler.common.service.ServiceException;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.luceneSearchService.LuceneSearch;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.utils.json.base.JSONException;
import java.util.ArrayList;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.lucene.document.Document;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import static com.krawler.common.util.Constants.AUDIT_INDEX_auditTime;
import static com.krawler.common.util.Constants.AUDIT_INDEX_ipAddr;
import static com.krawler.common.util.Constants.AUDIT_INDEX_userName;

/**
 *
 * @author Karthik
 */
public class auditTrailController extends MultiActionController {

    private auditTrailDAO auditTrailDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private String successView;
    private LuceneSearch LuceneSearchObj;
    private storageHandlerImpl storageHandlerImplObj;

    public void setstorageHandlerImpl(storageHandlerImpl storageHandlerImplObj1) {
        this.storageHandlerImplObj = storageHandlerImplObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setLuceneSearch(LuceneSearch LuceneSearchObj) {
        this.LuceneSearchObj = LuceneSearchObj;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public JSONObject getAuditJSONData(List ll, HttpServletRequest request, int totalSize) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            Iterator itr = ll.iterator();
            JSONArray jArr = new JSONArray();
//            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
//					"yyyy-MM-dd HH:mm:ss.0");
//            java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat(
//                    "yyyy-MM-dd HH:mm:ss");
            while (itr.hasNext()) {
                AuditTrail auditTrail = (AuditTrail) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("id", auditTrail.getID());
                obj.put(AUDIT_INDEX_userName, auditTrail.getUser().getUserLogin().getUserName() + " [ " +  profileHandlerDAOObj.getUserFullName(auditTrail.getUser().getUserID()) + " ]");
                obj.put(AUDIT_INDEX_ipAddr, auditTrail.getIPAddress());
                obj.put("details", (auditTrail.getDetails()).trim());
                obj.put("actionname", auditTrail.getAction().getActionName());
//                java.util.Date tempdate = sdf.parse(auditTrail.getAuditTime().toString());
//                String timeStamp="";
//                timeStamp= sdf1.format(tempdate).toString();
//                obj.put("timestamp", timeStamp);
                obj.put(AUDIT_INDEX_auditTime, auditTrail.getAudittime());
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj.put("count", totalSize);

        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }

    public JSONObject getAuditGroupJsonData(List ll, HttpServletRequest request, int totalSize) throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONArray jArr = new JSONArray();
        try {
            JSONObject objN = new JSONObject();
            objN.put("groupid", "");
            objN.put("groupname", "--All--");
            jArr.put(objN);
            Iterator itr = ll.iterator();
            while (itr.hasNext()) {
                AuditGroup auditGroup = (AuditGroup) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("groupid", auditGroup.getID());
                obj.put("groupname", auditGroup.getGroupName());
                jArr.put(obj);
            }
            jobj.put("data", jArr);
            jobj.put("count", totalSize);

        } catch (JSONException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }

    public ModelAndView getAuditData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            if (!StringUtil.isNullOrEmpty(request.getParameter("frm")) &&
                !StringUtil.isNullOrEmpty(request.getParameter("to"))) {
                requestParams.put("frm", StringUtil.checkForNull(request.getParameter("frm")));
                requestParams.put("to", StringUtil.checkForNull(request.getParameter("to")));
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("sort"))) {
                requestParams.put("field", request.getParameter("sort"));
                requestParams.put("direction", request.getParameter("dir"));
            }
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("groupid", StringUtil.checkForNull(request.getParameter("groupid")));
            requestParams.put("search", StringUtil.checkForNull(request.getParameter("search")));
            requestParams.put("companyid", sessionHandlerImplObj.getCompanyid(request));

            kmsg = auditTrailDAOObj.getAuditIndexData(requestParams);
            jobj = getAuditJSONData(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView reloadLuceneIndex(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        String msg = "";
        int batchSize = 1000;
        int i = 1;
        try {
            msg = "LU(ENE Reload started at " + new Date();

            if(auditTrailDAOObj.doReloadAuditIndex()){
                String indexPath = storageHandlerImpl.GetAuditTrailIndexPath();
                //Delete all old indexed file
                LuceneSearchObj.clearIndex(indexPath);
                //get total count
                // variable cnt = 0; cnt<totalcnt; cnt=cnt+10000
                // kmsg = auditTrailDAOObj.reloadLuceneIndex(cnt, cnt+10000);
                kmsg = auditTrailDAOObj.reloadLuceneIndex();
                int rowlimit = 10000;
                long totalcnt = (Long) kmsg.getEntityList().get(0);
                for (int cnt = 0; cnt < totalcnt; cnt = cnt + rowlimit) {
                    kmsg = auditTrailDAOObj.reloadLuceneIndex(cnt, rowlimit);
                    List<AuditTrail> auditTrailList = kmsg.getEntityList();
                    ArrayList<String> indexNames = auditTrailDAOObj.getAuditIndexNamesList();
                    ArrayList<String> sortableIndexName = auditTrailDAOObj.getAuditSortIndexList();
                    ArrayList<Document> auditDocuments = new ArrayList<Document>();

                    for (AuditTrail auditTrail : auditTrailList) {
                        ArrayList<Object> indexValues = auditTrailDAOObj.getAuditIndexValuesList(auditTrail);
                        Document luceneDoc = LuceneSearchObj.createLuceneDocument(indexValues, indexNames, sortableIndexName);
                        auditDocuments.add(luceneDoc);

                        if (i % batchSize == 0) { //Index in batch
                            LuceneSearchObj.writeIndex(auditDocuments, indexPath);
                            auditDocuments = new ArrayList<Document>();
                        }
                        i++;
                    }

                    if (auditDocuments.size() > 0) { //Index remaining entries
                        LuceneSearchObj.writeIndex(auditDocuments, indexPath);
                    }
                    auditDocuments.clear();
                    sortableIndexName.clear();
                    indexNames.clear();
                    auditTrailList.clear();
                }// end of for loop
                auditTrailDAOObj.resetAuditIndexFlag();//Make reloadIndex=0
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            msg += "\nLU(ENE Reload Exception " + e.getMessage();
        }
        msg += ", completed at " + new Date() + ", Indexed "+(i-1)+ " records";
        return new ModelAndView("jsonView", "model", msg);
    }


    public ModelAndView getAuditGroupData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            kmsg = auditTrailDAOObj.getAuditGroupData();
            jobj = getAuditGroupJsonData(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
}
