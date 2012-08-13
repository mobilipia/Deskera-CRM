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
package com.krawler.crm.reportBuilder;

import com.krawler.crm.database.tables.CustomReportColumns;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.lead.bizservice.LeadManagementService;
import com.krawler.crm.reportBuilder.bizservice.ReportBuilderService;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class CrmReportBuilderController extends MultiActionController {
    public static final String USER_DATEPREF = "yyyy-MM-dd HH:mm:ss";

    private String successView;
    private HibernateTransactionManager txnManager;
    private ReportBuilderService reportBuilderService;
    private exportDAOImpl exportDAOImplObj;

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    /**
     *
     * @return ReportBuilderService
     */
    public ReportBuilderService getReportBuilderService()
    {
        return reportBuilderService;
    }

    /**
     *
     * @param reportBuilderService
     */
    public void setReportBuilderService(ReportBuilderService reportBuilderService)
    {
        this.reportBuilderService = reportBuilderService;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
   public ModelAndView getModules(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String result = "";
        try {
            result = reportBuilderService.getModules().toString();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView", "model", result);
    }

   public ModelAndView getModuleColumns(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String result = "";
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String modulename = request.getParameter("modulename");
            result = reportBuilderService.getModuleColumns(companyid, modulename).toString();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView", "model", result);
    }

   public ModelAndView getCustomReportData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject commData = new JSONObject();
        JSONObject reportData = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
//            String searchJson = request.getParameter("searchJson");
            String fromDate = "";
            String toDate = "";
            String filterCombo = "";
            if (request.getParameter("cd") != null && !request.getParameter("cd").equals("")) {
                int chk = Integer.parseInt(request.getParameter("cd").toString());
                if (chk == 1) {
                	if (!request.getParameter("frm").equals("") && !request.getParameter("to").equals("") && !request.getParameter("filterCombo").equals("")){
                    	fromDate = request.getParameter("frm");
                        toDate = request.getParameter("to");
                        filterCombo = request.getParameter("filterCombo");
                    }
                }
            }
            boolean dataflag = false;
            if(request.getParameter("dataflag") != null) {
                dataflag = Boolean.parseBoolean(request.getParameter("dataflag"));
            }
            String ss = request.getParameter("ss");
            String filterSS = request.getParameter("filterSS");
            String filterCol = request.getParameter("filterCol");
            boolean detailFlag = false;
            if(request.getParameter("detailFlag") != null) {
                detailFlag = Boolean.parseBoolean(request.getParameter("detailFlag"));
            }
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            int reportno = Integer.parseInt(request.getParameter("reportno"));
            String report_category = request.getParameter("report_categoty");
            boolean export = false;
            StringBuffer searchJson = new StringBuffer();
            ArrayList<CustomReportColumns> groupCol = new ArrayList<CustomReportColumns>();
            ArrayList<CustomReportColumns> quickSearchCol = new ArrayList<CustomReportColumns>();
            ArrayList<String> dataIndexList = new ArrayList<String>();
            ArrayList<String> refTableList = new ArrayList<String>();
            HashMap<String, String> dataIndexReftableMap = new HashMap<String, String>();
            commData = reportBuilderService.getReportMetadata(commData, export, reportno, searchJson, quickSearchCol, groupCol, dataIndexList, refTableList, dataIndexReftableMap, detailFlag);            
            String[] quicksearchcol = new String[0];
            String quickSearchText = "Quick search by";
            int qsize = quickSearchCol.size();
            if(qsize > 0) {
                quicksearchcol = new String[qsize];
                for(int cnt = 0; cnt < qsize; cnt++){
                    quicksearchcol[cnt] = quickSearchCol.get(cnt).getDataIndex();
                    quickSearchText += " " + quickSearchCol.get(cnt).getDisplayname() + ",";
                }
                quickSearchText = quickSearchText.substring(0, quickSearchText.length()-1);
            }
            String groupByText = "";
            int grsize = groupCol.size();
            if(grsize > 0) {
                for(int cnt = 0; cnt < grsize; cnt++){
                    if(groupCol.get(cnt).isGroupflag()) {
                        groupByText += " " + groupCol.get(cnt).getDataIndex() + ",";
                    }
                }
                groupByText = groupByText.substring(0, groupByText.length()-1);
            }
            if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Lead_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_lead.lastname","crm_lead.firstname","crm_lead.title"};
                    quickSearchText += " Last Name, First Name, Title";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Lead_modulename);
                    reportData = reportBuilderService.getLeadsDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }else if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Product_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_product.productname","crm_product.description"};
                    quickSearchText += " Product Name, Description";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Product_modulename);
                    reportData = reportBuilderService.getProductsDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }else if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Account_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_account.accountname"};
                    quickSearchText += " Account Name";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Account_modulename);
                    reportData = reportBuilderService.getAccountsDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }else if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Contact_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_contact.firstname", "crm_contact.lastname"};
                    quickSearchText += " First Name, Last Name";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Contact_modulename);
                    reportData = reportBuilderService.getContactsDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }else if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Opportunity_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_opportunity.oppname"};
                    quickSearchText += " opportunity name";
                }
                if(dataflag) {                
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Opportunity_modulename);
                    reportData = reportBuilderService.getOpportunitiesDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }else if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Case_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_case.subject", "crm_case.casename"};
                    quickSearchText += " Subject, Case Name";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Case_modulename);
                    reportData = reportBuilderService.getCasesDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }
            JSONArray jarr = new JSONArray();
            if(reportData.has("data")) {
                jarr = reportData.getJSONArray("data");
            }
            commData.put("coldata", jarr);
            commData.put("quickSearchText", quickSearchText);
            commData.put("success", true);
            commData.put("totalCount", reportData.has("totalCount") ? reportData.get("totalCount") : "0");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView("jsonView", "model", commData.toString());
   }

   public  ModelAndView saveReportConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        String result = "";
        JSONObject successMsg = new JSONObject();
        successMsg.put("success", false);
//        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String reportno = request.getParameter("reportno");
            String rname = request.getParameter("rname");
            String runiquename = request.getParameter("runiquename");
            String rdescription = request.getParameter("rdescription");
            String rcategory = request.getParameter("rcategory");
            String rfilterjson = request.getParameter("rfilterjson");
            String reportcolumnsetting = request.getParameter("reportcolumnsetting");
            String userid = sessionHandlerImpl.getUserid(request);
            result = reportBuilderService.saveReportConfig(reportno,rname,runiquename,rdescription,rcategory,reportcolumnsetting,userid,rfilterjson).toString();
            txnManager.commit(status);
            successMsg.put("success", true);
            successMsg.put("msg", "Report configuration has been saved successfully.");
            result = successMsg.toString();
        } catch (Exception ex) {
            txnManager.rollback(status);
            logger.warn(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView", "model", result);
    }

   public ModelAndView getCustomReportExportData(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject commData = new JSONObject();
        JSONObject reportData = new JSONObject();
        String view = "jsonView";
        try {
            String fileType = request.getParameter("filetype");
            String companyid = sessionHandlerImpl.getCompanyid(request);
//            String searchJson = request.getParameter("searchJson");
            String fromDate = "";
            String toDate = "";
            String filterCombo = "";
            if (request.getParameter("cd") != null && !request.getParameter("cd").equals("")) {
                int chk = Integer.parseInt(request.getParameter("cd").toString());
                if (chk == 1) {
                	if (!request.getParameter("frm").equals("") && !request.getParameter("to").equals("") && !request.getParameter("filterCombo").equals("")){
                    	fromDate = request.getParameter("frm");
                        toDate = request.getParameter("to");
                        filterCombo = request.getParameter("filterCombo");
                    }
                }
            }
            boolean dataflag = true;
            if(request.getParameter("dataflag") != null) {
                dataflag = Boolean.parseBoolean(request.getParameter("dataflag"));
            }
            String ss = request.getParameter("ss");
            String filterSS = null;
            String filterCol = null;
            boolean detailFlag = false;
            String field = request.getParameter("field");
            String direction = request.getParameter("direction");
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            int reportno =0;
            String report_category ="";
            if(request.getParameter("extraconfig")!=null) {
                JSONObject extraconfig = new JSONObject(request.getParameter("extraconfig"));
                if(extraconfig.has("reportno")) {
                    reportno = Integer.parseInt(extraconfig.getString("reportno"));
                }
                if(extraconfig.has("report_categoty")) {
                    report_category = extraconfig.getString("report_categoty");
                }
                if(extraconfig.has("filterCol")){
                	filterCol = extraconfig.getString("filterCol");
                }
                if(extraconfig.has("filterSS")){
                	filterSS = extraconfig.getString("filterSS");
                }
                if(extraconfig.has("detailFlag")){
                	detailFlag = Boolean.parseBoolean(extraconfig.getString("detailFlag"));
                }
            }
            boolean export = false;
            StringBuffer searchJson = new StringBuffer();
            ArrayList<CustomReportColumns> groupCol = new ArrayList<CustomReportColumns>();
            ArrayList<CustomReportColumns> quickSearchCol = new ArrayList<CustomReportColumns>();
            ArrayList<String> dataIndexList = new ArrayList<String>();
            ArrayList<String> refTableList = new ArrayList<String>();
            HashMap<String, String> dataIndexReftableMap = new HashMap<String, String>();
            commData = reportBuilderService.getReportMetadata(commData, export, reportno, searchJson, quickSearchCol, groupCol, dataIndexList, refTableList, dataIndexReftableMap, detailFlag);
//            if(commData.has("columns")) {
//                JSONArray jarrColumns = commData.getJSONArray("columns");
//                for(int cnt =0; cnt< jarrColumns.length(); cnt++) {
//                    JSONObject jtempObj = jarrColumns.getJSONObject(cnt);
//                    if(jtempObj.has("dataIndex")) {
//                        jtempObj.put("header",jtempObj.has("dataIndex"));
//                    }
//                }
//            }
            String[] quicksearchcol = new String[0];
            String quickSearchText = "Quick search by";
            int qsize = quickSearchCol.size();
            if(qsize > 0) {
                quicksearchcol = new String[qsize];
                for(int cnt = 0; cnt < qsize; cnt++){
                    quicksearchcol[cnt] = quickSearchCol.get(cnt).getDataIndex();
                    quickSearchText += " " + quickSearchCol.get(cnt).getDisplayname() + ",";
                }
                quickSearchText = quickSearchText.substring(0, quickSearchText.length()-1);
            }
            String groupByText = "";
            int grsize = groupCol.size();
            if(grsize > 0) {
                for(int cnt = 0; cnt < grsize; cnt++){
                    if(groupCol.get(cnt).isGroupflag()) {
                        groupByText += " " + groupCol.get(cnt).getDataIndex() + ",";
                    }
                }
                groupByText = groupByText.substring(0, groupByText.length()-1);
            }
            if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Lead_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_lead.lastname","crm_lead.firstname","crm_lead.title"};
                    quickSearchText += " Last Name, First Name, Title";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Lead_modulename);
                    reportData = reportBuilderService.getLeadsDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }else if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Product_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_product.productname","crm_product.description"};
                    quickSearchText += " Product Name, Description";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Product_modulename);
                    reportData = reportBuilderService.getProductsDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }else if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Account_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_account.accountname"};
                    quickSearchText += " Account Name";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Account_modulename);
                    reportData = reportBuilderService.getAccountsDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }else if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Contact_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_contact.firstname", "crm_contact.lastname"};
                    quickSearchText += " First Name, Last Name";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Contact_modulename);
                    reportData = reportBuilderService.getContactsDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }else if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Opportunity_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_opportunity.oppname"};
                    quickSearchText += " opportunity name";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Opportunity_modulename);
                    reportData = reportBuilderService.getOpportunitiesDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }else if(report_category.equals(Constants.modulenameMap.get(Constants.Crm_Case_modulename))){
                if(quicksearchcol.length == 0) {
                    quicksearchcol = new String[]{"crm_case.subject", "crm_case.casename"};
                    quickSearchText += " Subject, Case Name";
                }
                if(dataflag) {
                    boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, Constants.Crm_Case_modulename);
                    reportData = reportBuilderService.getCasesDataCustomReport(companyid, searchJson.toString(), quicksearchcol, groupByText, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit, usersList, report_category, dataIndexList, refTableList, dataIndexReftableMap);
                }
            }
            JSONArray jarr = new JSONArray();
            if(reportData.has("data")) {
                jarr = reportData.getJSONArray("data");
            }
            commData.put("coldata", jarr);
            commData.put("quickSearchText", quickSearchText);
            commData.put("success", true);
            commData.put("totalCount", reportData.get("totalCount"));
            exportDAOImplObj.processRequest(request, response, commData);
            if (fileType.equals("print")) {
                view = "jsonView-empty";
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return new ModelAndView(view, "model", (new JSONObject()).toString());
   }

   public ModelAndView deleteCustomReport(HttpServletRequest request, HttpServletResponse response) throws ServletException, JSONException {
        JSONObject jobj = new JSONObject();
        boolean success = false;
//        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            int reportno = Integer.parseInt(request.getParameter("reportno"));
            success = reportBuilderService.deleteCustomReport(reportno);
            if(success) {
                txnManager.commit(status);
            } else {
                txnManager.rollback(status);
            }
        } catch (Exception ex) {
            txnManager.rollback(status);
            logger.warn(ex.getMessage(), ex);
        } finally {
            jobj.put("success", success);
        }
        return new ModelAndView("jsonView", Constants.model, jobj.toString());
    }

}
