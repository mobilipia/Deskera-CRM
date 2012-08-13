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

package com.krawler.spring.crm.sunrise;

import com.krawler.spring.crm.common.*;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.SunriseCalibration;
import com.krawler.crm.utils.AuditAction;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class calibrationController  extends MultiActionController {
    private String successView;
    private calibrationDAO calibrationDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private HibernateTransactionManager txnManager;
    private exportDAOImpl exportDAOImplObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setcalibrationDAO(calibrationDAO calibrationDAOObj) {
        this.calibrationDAOObj = calibrationDAOObj;
    }
    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    // Save Sunrise's Callibration Records
    public ModelAndView sunriseClientCallibrationSave(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        SunriseCalibration sunriseCalibration = null;
//        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String userid = sessionHandlerImplObj.getUserid(request);
            String Id = jobj.getString("id");
            jobj.put("userid ", userid);
            if (Id.equals("0")) { // new entry
                jobj.put("createdon", new Date());
                kmsg = calibrationDAOObj.saveCallibrationRecord(jobj);
            } else {
                kmsg = calibrationDAOObj.saveCallibrationRecord(jobj);
            }
            sunriseCalibration = (SunriseCalibration) kmsg.getEntityList().get(0);
            
            myjobj.put("success", true);
            myjobj.put("ID", sunriseCalibration.getId());
            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
//            logger.warn(e.getMessage(),e);
        } finally {

        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView getSunriseClientCallibrationData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
        	String ss = request.getParameter("ss");
        	
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            if(!StringUtil.isNullOrEmpty(request.getParameter("sort"))) {
                requestParams.put("field", request.getParameter("sort"));
                requestParams.put("direction", request.getParameter("dir"));
            }
            requestParams.put("ss", ss);
            
            kmsg = calibrationDAOObj.getSunriseClientCallibrationData(requestParams);
            jobj = getCallibrationJson(kmsg.getEntityList(), kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        } finally {

        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getCallibrationJson(List<SunriseCalibration> ll, int totalSize) throws ServiceException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            for (SunriseCalibration obj : ll) {
                JSONObject tmpObj = new JSONObject();
                tmpObj = getCallibraJsonObject(obj, tmpObj);
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return jobj;

    }

    public JSONObject getCallibraJsonObject(SunriseCalibration obj, JSONObject tmpObj)  {
        try {
            tmpObj.put("caldue", obj.getCaldue());
            tmpObj.put("calon", obj.getCalon());
            tmpObj.put("id", obj.getId());
            tmpObj.put("particulars", obj.getParticulars());
            tmpObj.put("srcal", obj.getSrcal());
            tmpObj.put("contactperson", obj.getContactperson());
            tmpObj.put("contactnumber", obj.getContactnumber());
            tmpObj.put("machinetype", obj.getMachinetype());
            tmpObj.put("paymentstatus", obj.getPaymentstatus());
            tmpObj.put("machinecalno", obj.getMachinecalno());
            tmpObj.put("state", obj.getState());
        } catch  (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return tmpObj;
    }
    
    
    
 // Delete Sunrise's Callibration Records
    public ModelAndView sunriseClientCallibrationDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        JSONObject myjobj = new JSONObject();
        JSONObject jobj ;
        KwlReturnObject kmsg = null;
        SunriseCalibration sunriseCalibration = null;

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
       
            String companyid = sessionHandlerImplObj.getCompanyid(request);
            String userid = sessionHandlerImplObj.getUserid(request);
            String id[]=request.getParameterValues("ids");
            kmsg=calibrationDAOObj.deleteCallibrationRecord(id);
            myjobj.put("success", kmsg.isSuccessFlag());
            myjobj.put("msg", kmsg.getMsg());
           
            txnManager.commit(status);
        } catch (Exception e) {
            txnManager.rollback(status);
//            logger.warn(e.getMessage(),e);
        } finally {

        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }
    
   //export
    
    public ModelAndView sunriseClientCallibrationExport(HttpServletRequest request, HttpServletResponse response)
    throws ServletException {
		JSONObject jobj = new JSONObject();
		KwlReturnObject kmsg = null;
		String view = "jsonView";
		List <SunriseCalibration>ll = null;
		try {
		    String selectExport = request.getParameter("selectExport");
			HashMap requestParams = new HashMap();
		    requestParams.put("isexport", true);
		    if(!StringUtil.isNullOrEmpty(request.getParameter("ss"))){
		        requestParams.put("ss", request.getParameter("ss"));
		    }
		    if(StringUtil.isNullOrEmpty(selectExport)){
		    	kmsg = calibrationDAOObj.getSunriseClientCallibrationData(requestParams);
		    	ll = kmsg.getEntityList();
		    } else {
	    	    JSONArray jarr = new JSONArray("[" + selectExport + "]");

                ArrayList ids = new ArrayList();
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobject = jarr.getJSONObject(i);
                    ids.add(jobject.getString("id").toString());
                }
                String[] arrayid = (String[]) ids.toArray(new String[]{});
                kmsg = calibrationDAOObj.getCalibration(arrayid);
                ll = kmsg.getEntityList();
		    }
		    
		    if(ll != null){
		    	jobj = getCallibrationJson(ll, kmsg.getRecordTotalCount());
		    }
		    
		    String fileType = request.getParameter("filetype");
		    if (StringUtil.equal(fileType, "print")) {
		        view = "jsonView-empty";
		    }
		    exportDAOImplObj.processRequest(request, response, jobj);
		    
		} catch (Exception e) {
		    logger.warn(e.getMessage(),e);
		}
		return new ModelAndView(view, "model", jobj.toString());
}

    
    
}
