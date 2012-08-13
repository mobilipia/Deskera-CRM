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
package com.krawler.spring.crm.productModule; 
import com.krawler.common.comet.CometConstants;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.cometModule.bizservice.CometManagementService;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.crm.utils.AuditAction;
import com.krawler.common.util.StringUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.spring.crm.common.CrmCommonService;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.JSONException;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.HashMap;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import com.krawler.common.util.Constants;
import com.krawler.crm.product.bizservice.ProductManagementService;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.authHandler.authHandler;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class crmProductController extends MultiActionController {
    private crmProductDAO crmProductDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private auditTrailDAO auditTrailDAOObj;
    private commentDAO crmCommentDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private fieldDataManager fieldDataManagercntrl;
    private ProductManagementService productManagementService;
    private CometManagementService CometManagementService;
    private CrmCommonService crmCommonService;
    private fieldManagerDAO fieldManagerDAOobj;

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    public void setCrmCommonService(CrmCommonService crmCommonService) {
		this.crmCommonService = crmCommonService;
	}
    
	public void setCometManagementService(CometManagementService CometManagementService) {
        this.CometManagementService = CometManagementService;
    }
    /**
     * @return the activityManagementService
     */
    public ProductManagementService getProductManagementService()
    {
        return productManagementService;
    }

    /**
     * @param leadManagementService the leadManagementService to set
     */
    public void setProductManagementService(ProductManagementService productManagementService)
    {
        this.productManagementService = productManagementService;
    }

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }

    public crmProductDAO getcrmProductDAO(){
        return crmProductDAOObj;
    }
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setcrmProductDAO(crmProductDAO crmProductDAOObj1) {
        this.crmProductDAOObj = crmProductDAOObj1;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
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

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }
    /**
     *
     * @param request
     * @param response
     * @return org.springframework.web.servlet.ModelAndView : JSonObject of all products which contains nodes -
     * 1.Success 2. totalCount 3.Data - which contains nodes -
     * 1.id 2.name 3.unitprice
     * @throws javax.servlet.ServletException
     */
    public ModelAndView getProductname(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       KwlReturnObject kmsg = null;
       try{
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            JSONArray jarr = new JSONArray();
            int dl = 0;

            ArrayList filter_names = new ArrayList();ArrayList filter_values = new ArrayList();
            filter_names.add("c.deleteflag");filter_values.add(0);
            filter_names.add("c.company.companyID");filter_values.add(companyid);
            filter_names.add("c.validflag");filter_values.add(1);

            ArrayList order_by = new ArrayList();ArrayList order_type = new ArrayList();
            order_by.add("c.productname");
            order_type.add("asc");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("order_by", order_by);
            requestParams.put("order_type", order_type);
            String hierarchy= request.getParameter("hierarchy");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            if(!StringUtil.isNullOrEmpty(hierarchy) && Boolean.parseBoolean(hierarchy)){
                if(!heirarchyPerm){
                    filter_names.add("INc.usersByUserid.userID");filter_values.add(usersList);
                }
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_values);
           
            kmsg = crmProductDAOObj.getAllProducts(requestParams);
            dl = kmsg.getRecordTotalCount();
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                boolean hasAccess =true;
                CrmProduct obj = (CrmProduct) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getProductid());
                tmpObj.put("productid", obj.getProductid());
                tmpObj.put("name", obj.getProductname());
                tmpObj.put("productname", obj.getProductname());
                tmpObj.put("unitprice", StringUtil.isNullOrEmpty(obj.getUnitprice())?"":obj.getUnitprice());
                tmpObj.put("salespricedatewise", StringUtil.isNullOrEmpty(obj.getUnitprice())?"":obj.getUnitprice());
                tmpObj.put("desc", StringUtil.isNullOrEmpty(obj.getDescription())?"":obj.getDescription());
                if(StringUtil.isNullOrEmpty(hierarchy) ){
                    if(!heirarchyPerm) {
                        hasAccess = false;
                        if(usersList.indexOf(obj.getUsersByUserid().getUserID()) != -1 ){
                            hasAccess = true;
                        }
                    }else{
                        hasAccess = !obj.getIsarchive();
                    }
                }else{
                    hasAccess = !obj.getIsarchive();
                }
                tmpObj.put(ProductConstants.Crm_hasAccess,hasAccess);
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
       } catch(Exception e) {
          logger.warn(e.getMessage(), e);
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    /**
     *
     * @param request
     * @param response
     * @return org.springframework.web.servlet.ModelAndView : JSONObject - It contains all the fields of CrmProduct class
     * including total comments and unread comments count
     * @throws javax.servlet.ServletException
     */
    public ModelAndView getProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       try{
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            String isarchive = request.getParameter("isarchive");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            String iscustomcolumn = request.getParameter("iscustomcolumn");
            String xtype = request.getParameter("xtype");
            String xfield = request.getParameter("xfield");
            String type = request.getParameter("type");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZone(request);
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);

            jobj = getProductManagementService().getProducts(companyid, userid, currencyid, selectExport, isarchive,
                    searchJson, ss, config, isExport, heirarchyPerm, field, direction, iscustomcolumn,
                    xtype, xfield, type, start, limit, dateFormat, usersList);
            
       } catch(Exception e) {
          logger.warn(e.getMessage(), e);
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }

    /**
     *
     * @param request - JSONObject with array jsondata which contains fields of CrmProduct class.
     * @param response -
     * @return - org.springframework.web.servlet.ModelAndView : JSONObject - Contains node 1.success 2.productid
     * @throws javax.servlet.ServletException
     */
    public ModelAndView saveProducts(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject myjobj = new JSONObject();
       KwlReturnObject kmsg = null;
       CrmProduct prod = null;
       //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
       try{
            JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
            Integer operationCode = CrmPublisherHandler.ADDRECORDCODE;
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String id = jobj.getString("productid");
            jobj.put("userid", userid);
            jobj.put("companyid", companyid);
            jobj.put("updatedon", new Date().getTime());
            jobj.put("tzdiff",sessionHandlerImpl.getTimeZoneDifference(request));
            JSONArray jcustomarray = null;
            if(jobj.has("customfield")){
                jcustomarray = jobj.getJSONArray("customfield");
            }
            if (id.equals("0")) {
                id = java.util.UUID.randomUUID().toString();
                //jobj.put("createdon", new Date());
                jobj.put("productid", id);
//                if(jobj.has("customfield")){
//                    fieldManager.storeCustomFields(jcustomarray,"product",true,id);
//                }
//                jobj = StringUtil.setcreatedonDate(jobj,request);
                kmsg = crmProductDAOObj.addProducts(jobj);
                prod = (CrmProduct) kmsg.getEntityList().get(0);
                if (prod.getValidflag() == 1) {
                    auditTrailDAOObj.insertAuditLog(AuditAction.PRODUCT_CREATE,
                            ((prod.getProductname()==null)?"":prod.getProductname()) + "  - Product created ",
                            request, id);
                }
                HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                customrequestParams.put("customarray", jcustomarray);
                customrequestParams.put("modulename", Constants.Crm_Product_modulename);
                customrequestParams.put("moduleprimarykey", Constants.Crm_Productid);
                customrequestParams.put("modulerecid", id);
                customrequestParams.put("companyid", companyid);
                customrequestParams.put("customdataclasspath", Constants.Crm_product_custom_data_classpath);
                if(jobj.has("customfield")){
                    KwlReturnObject customDataresult =fieldDataManagercntrl.setCustomData(customrequestParams);
                    if(customDataresult!=null && customDataresult.getEntityList().size() > 0){
                        jobj.put("CrmProductCustomDataobj", id);
                        kmsg = crmProductDAOObj.editProducts(jobj);
                    }
                }
                // fetch auto-number columns only
                HashMap<String, Object> fieldrequestParams = new HashMap<String, Object>();
                fieldrequestParams.put("isexport", true);
                fieldrequestParams.put("filter_names", Arrays.asList("companyid", "moduleid", "fieldtype"));
                fieldrequestParams.put("filter_values", Arrays.asList(companyid, Constants.Crm_product_moduleid, Constants.CUSTOM_FIELD_AUTONUMBER));
                KwlReturnObject AutoNoFieldMap = fieldManagerDAOobj.getFieldParams(fieldrequestParams);

                // increment auto number if exist
                if(AutoNoFieldMap.getEntityList().size()>0) {
                     JSONArray autoNoData = fieldDataManagercntrl.setAutoNumberCustomData(customrequestParams, AutoNoFieldMap.getEntityList());
                     jobj.put(com.krawler.common.util.Constants.AUTOCUSTOMFIELD, autoNoData);
                }
                // END logic - auto no
                
            } else {
//                if(jobj.has("customfield")){
//                    fieldManager.storeCustomFields(jcustomarray,"product",false,id);
//                }
                operationCode = CrmPublisherHandler.UPDATERECORDCODE;
                if(jobj.has("customfield")){
                        HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
                        customrequestParams.put("customarray", jcustomarray);
                        customrequestParams.put("modulename", Constants.Crm_Product_modulename);
                        customrequestParams.put("moduleprimarykey", Constants.Crm_Productid);
                        customrequestParams.put("modulerecid", id);
                        customrequestParams.put("companyid", companyid);
                        customrequestParams.put("customdataclasspath", Constants.Crm_product_custom_data_classpath);
                        KwlReturnObject customDataresult =fieldDataManagercntrl.setCustomData(customrequestParams);
                        if(customDataresult!=null && customDataresult.getEntityList().size() > 0){
                            jobj.put("CrmProductCustomDataobj", id);
                        }
                    }
                if(jobj.has("createdon")){
                    jobj.put("createdon", jobj.getLong("createdon"));
                }
                kmsg = crmProductDAOObj.editProducts(jobj);
                prod = (CrmProduct) kmsg.getEntityList().get(0);
                if (prod.getValidflag() == 1) {
                    auditTrailDAOObj.insertAuditLog(AuditAction.PRODUCT_UPDATE,
                            jobj.getString("auditstr") + " Product - " + ((prod.getProductname()==null)?"":prod.getProductname()) + " ",
                            request, id);
                }
            }
            myjobj.put("success", true);
            myjobj.put("ID", prod.getProductid());
            myjobj.put("createdon",jobj.has("createdon") ? jobj.getLong("createdon"):"");
            myjobj.put(Constants.AUTOCUSTOMFIELD, jobj.has(Constants.AUTOCUSTOMFIELD) ? jobj.getJSONArray(Constants.AUTOCUSTOMFIELD) : "");
            txnManager.commit(status);

            JSONObject cometObj = jobj;
            if(!StringUtil.isNullObject(prod)) {
                if(prod.getCreatedon()!=null) {
                    cometObj.put("createdon", prod.getCreatedOn());
                }
            }

           cometObj.put("productname", jobj.has("pname") ? jobj.getString("pname") : "");   // added for commet... JS side, store's dataIndex kye must contains in the json object also
           cometObj.put("vendornamee", jobj.has("vendornameid") ? jobj.getString("vendornameid") : "");   // added for commet
           publishProductModuleInformation(request, cometObj, operationCode, companyid, userid);
       } catch (Exception e) {
          logger.warn(e.getMessage(), e);
           txnManager.rollback(status);
       }
       return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    private void publishProductModuleInformation(HttpServletRequest request, JSONObject cometObj, int operationCode, String companyid, String userid) throws ServiceException, JSONException, SessionExpiredException, ServiceException, com.krawler.utils.json.base.JSONException {
        String randomNumber = request.getParameter("randomnumber");
        CometManagementService.publishModuleInformation(companyid, userid, randomNumber, Constants.Crm_Product_modulename, Constants.Crm_productid, operationCode, cometObj, CometConstants.CRMUPDATES);
    }

    /**
     *
     * @param request - JSONObject with array jsondata which contains productid of CrmProduct class.
     * @param response
     * @return - org.springframework.web.servlet.ModelAndView : JSONObject - Contains node 1.success 2.productid
     * @throws javax.servlet.ServletException
     */
//    public ModelAndView deleteProduct(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException {
//        JSONObject myjobj = null;
//        KwlReturnObject kmsg = null;
//        CrmProduct prod = null;
//        //Create transaction
//        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
//        def.setName("JE_Tx");
//        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
//        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
//        TransactionStatus status = txnManager.getTransaction(def);
//        try {
//            String userid = sessionHandlerImpl.getUserid(request);
//            myjobj = new JSONObject();
//            myjobj.put("success", false);
//            JSONArray jarr = null;
//            String companyid = sessionHandlerImpl.getCompanyid(request);
//            if (request.getAttribute("deletejarr") != null) {
//                jarr = (JSONArray) request.getAttribute("deletejarr");
//            } else {
//                String jsondata = request.getParameter("jsondata");
//                jarr = new JSONArray("[" + jsondata + "]");
//            }
//            for (int i = 0; i < jarr.length(); i++) {
//                JSONObject jobj = jarr.getJSONObject(i);
//                jobj.put("deleteflag", 1);
//                jobj.put("userid", userid);
//                jobj.put("updatedon", new Date());
//                kmsg = crmProductDAOObj.editProducts(jobj);
//                prod = (CrmProduct) kmsg.getEntityList().get(0);
//                if (prod.getValidflag() == 1) {
//                    auditTrailDAOObj.insertAuditLog(AuditAction.PRODUCT_DELETE,
//                            prod.getProductname() + " - Product deleted ",
//                            request, jobj.getString("productid"));
//                }
//            }
//            if(request.getAttribute("failDelete") != null){
//                myjobj.put("failDelete", (JSONArray)request.getAttribute("failDelete"));
//            }
//            myjobj.put("successDeleteArr", jarr);
//            myjobj.put("success", true);
//            myjobj.put("ID", prod.getProductid());
//            txnManager.commit(status);
//
//            JSONObject cometObj = new JSONObject();
//            cometObj.put("ids",  jarr);
//            publishProductModuleInformation(request, cometObj, CrmPublisherHandler.DELETERECORDCODE, companyid, userid);
//        } catch (JSONException e) {
//            logger.warn(e.getMessage(), e);
//            txnManager.rollback(status);
//        } catch (Exception e) {
//            logger.warn(e.getMessage(), e);
//            txnManager.rollback(status);
//        }
//        return new ModelAndView("jsonView", "model", myjobj.toString());
//    }

    public ModelAndView deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject myjobj = null;
        KwlReturnObject kmsg = null;
        CrmProduct prod = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            myjobj = new JSONObject();
            myjobj.put("success", false);
            JSONArray jarr = null;
            String companyid = sessionHandlerImpl.getCompanyid(request);
            if (request.getAttribute("deletejarr") != null) {
                jarr = (JSONArray) request.getAttribute("deletejarr");
            } else {
                String jsondata = request.getParameter("jsondata");
                jarr = new JSONArray("[" + jsondata + "]");
            }

            ArrayList productids = new ArrayList();
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jobject = jarr.getJSONObject(i);
                productids.add(jobject.getString("productid").toString());
            }
            
            String[] arrayid = (String[]) productids.toArray(new String[]{});
            JSONObject jobj = new JSONObject();
            jobj.put("deleteflag", 1);
            jobj.put("userid", userid);
            jobj.put("productid", arrayid);
            jobj.put("updatedon", new Date().getTime());
            jobj.put("tzdiff",timeZoneDiff);

            kmsg = crmProductDAOObj.updateMassProducts(jobj);
            List<CrmProduct> ll = crmProductDAOObj.getProducts(productids);

            if(ll!=null){
                for(int i =0 ; i< ll.size() ; i++){
                    CrmProduct productaudit = (CrmProduct)ll.get(i);
                    if (productaudit.getValidflag() == 1) {
                        auditTrailDAOObj.insertAuditLog(AuditAction.PRODUCT_DELETE,
                              productaudit.getProductname() + " - Product deleted ",
                                request, productaudit.getProductid());
                    }
                }

            }
           
            if(request.getAttribute("failDelete") != null){
                myjobj.put("failDelete", (JSONArray)request.getAttribute("failDelete"));
            }
            myjobj.put("successDeleteArr", jarr);
            myjobj.put("success", true);
            myjobj.put("ID", productids);
            txnManager.commit(status);

            JSONObject cometObj = new JSONObject();
            cometObj.put("ids",  jarr);
            publishProductModuleInformation(request, cometObj, CrmPublisherHandler.DELETERECORDCODE, companyid, userid);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    /**
     *
     * @param request
     * @param response
     * @return org.springframework.web.servlet.ModelAndView : JSONObject - It contains all the fields of CrmProduct class
     * including total comments and unread comments count
     * @throws javax.servlet.ServletException
     */
    public ModelAndView exportProduct(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        String view = "jsonView";
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String currencyid = sessionHandlerImpl.getCurrencyID(request);
            String selectExport = request.getParameter("selectExport");
            String isarchive = request.getParameter("isarchive");
            String searchJson = request.getParameter("searchJson");
            String ss = request.getParameter("ss");
            String config = request.getParameter("config");
            String isExport = request.getParameter("reportid");
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Product");
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);

            jobj = getProductManagementService().ProductExport(companyid, userid, currencyid, selectExport,
                    isarchive, searchJson, ss, config, isExport, heirarchyPerm, field, direction, dateFormat);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);
            auditTrailDAOObj.insertAuditLog(AuditAction.PRODUCT_EXPORT,
                    "Product list exported in " + StringUtil.chkExportFileTypeForAuditMsg(request.getParameter("filetype")),
                    request, "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }


    public ModelAndView updateMassProducts(HttpServletRequest request,HttpServletResponse response) throws ServletException {
		JSONObject myjobj = new JSONObject();
		KwlReturnObject kmsg = null;
		CrmProduct product = null;
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		try {
			JSONObject jobj = new JSONObject(request.getParameter("jsondata"));
			String userid = sessionHandlerImpl.getUserid(request);
			String productIds = jobj.getString("productid");
			String arrayId[] = productIds.split(",");
			Integer operationCode = CrmPublisherHandler.UPDATERECORDCODE;
			String companyid = sessionHandlerImpl.getCompanyid(request);
			jobj.put("userid", userid);
			jobj.put("companyid", companyid);
			if (jobj.has("updatedon")
					&& !StringUtil.isNullOrEmpty(jobj.getString("updatedon"))) {
				jobj.put("updatedon", jobj.getLong("updatedon"));
			} else {
				jobj.put("updatedon", new Date().getTime());
			}
			jobj.put("productid", arrayId);
			jobj.put("tzdiff", sessionHandlerImpl
					.getTimeZoneDifference(request));
			JSONArray jcustomarray = null;
			if (jobj.has("customfield")) {
				jcustomarray = jobj.getJSONArray("customfield");
			}
			if (jobj.has("customfield")) {
				HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
				customrequestParams.put("customarray", jcustomarray);
				customrequestParams.put("modulename",Constants.Crm_Product_modulename);
				customrequestParams.put("moduleprimarykey",	Constants.Crm_Productid);
				customrequestParams.put("companyid", companyid);
				customrequestParams.put("customdataclasspath",Constants.Crm_product_custom_data_classpath);
				KwlReturnObject customDataresult = null;
				for (String id : arrayId) {
					customrequestParams.put("modulerecid", id);
					customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
				}
				if (customDataresult != null && customDataresult.getEntityList().size() > 0) {
					jobj.put("CrmProductCustomDataobj", true);
				}
			}
			if (jobj.has("createdon")) {
				jobj.put("createdon", jobj.getLong("createdon"));
			}
			kmsg = crmProductDAOObj.updateMassProducts(jobj);
			// TODO : How to insert audit log when mass update
			txnManager.commit(status);
			JSONObject cometObj = jobj;
			if (request.getParameter("massEdit") != null && Boolean.parseBoolean(request.getParameter("massEdit"))) {
				crmCommonService.validateMassupdate(arrayId, "Product",	companyid);
				cometObj.put("productid", productIds);
				cometObj.put("ismassedit", true);
			}
			myjobj.put("success", true);
			myjobj.put("ID", productIds);
			myjobj.put("createdon", jobj.has("createdon") ? jobj.getLong("createdon") : "");
			publishProductModuleInformation(request, cometObj, operationCode, companyid, userid);
		} catch (Exception e) {
			logger.warn("Exception while Product Mass Update:", e);
			txnManager.rollback(status);
		}
		return new ModelAndView("jsonView", "model", myjobj.toString());
	}

}
