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
import com.krawler.common.util.DataInvalidateException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.TargetList;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.emailMarketing.crmEmailMarketingDAO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.importFunctionality.ImportController;
import com.krawler.spring.importFunctionality.ImportDAO;
import com.krawler.spring.importFunctionality.ImportHandler;
import com.krawler.spring.importFunctionality.ImportThreadExecutor;
import com.krawler.spring.importFunctionality.ImportUtil;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.Date;
import java.util.HashMap;
import org.springframework.web.servlet.ModelAndView;

public class crmImportController extends ImportController {
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private crmCommonDAO crmCommonDAOObj;
    private ImportDAO importDao;

    public void setimportDAO(ImportDAO importDao) {
        this.importDao = importDao;
        super.setimportDAO(importDao);
    }
    
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    private crmEmailMarketingDAO crmEmailMarketingDAOObj;

    public void setcrmEmailMarketingDAO(crmEmailMarketingDAO crmEmailMarketingDAOObj1) {
        this.crmEmailMarketingDAOObj = crmEmailMarketingDAOObj1;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    @Override
    public ModelAndView importRecords(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj=new JSONObject();
        String View = "jsonView";
        try {
            String doAction = request.getParameter("do");
            logger.debug("A(( "+doAction+" start : "+new Date());
            if (doAction.compareToIgnoreCase("import") == 0  || doAction.compareToIgnoreCase("validateData") == 0) {
                jobj.put("success", false);
                HashMap<String, Object> requestParams = ImportUtil.getImportRequestParams(request);

                String module = request.getParameter("modName");
                Object extraObj = null;
                TargetList targetList = null;
                if(module.equals("Target")) {
                    String tlid = request.getParameter("tlid");
                    String uid = sessionHandlerImpl.getUserid(request);
                    if(tlid!=null)
                        targetList = (TargetList) KwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.TargetList", tlid);

                    if(targetList == null) {
                        KwlReturnObject kmsg = null;
                        JSONObject jobjTList = new JSONObject();
                        jobjTList.put("createdon", new Date());
                        jobjTList.put("modifiedon", new Date());
                        jobjTList.put("targetlistid", tlid);
                        jobjTList.put("userid", uid);
                        kmsg = crmEmailMarketingDAOObj.addTargetList(jobjTList);
                        targetList = (TargetList) kmsg.getEntityList().get(0);
                    }
                    extraObj = targetList;
                }
                if(module.equalsIgnoreCase("contact")) {
                    requestParams.put("mapid", request.getParameter("mapid"));
                    requestParams.put("relatedName", request.getParameter("relatedName"));
                }
                String eParams = request.getParameter("extraParams");
                JSONObject extraParams = StringUtil.isNullOrEmpty(eParams)?new JSONObject():new JSONObject(eParams);
                requestParams.put("extraParams", extraParams);
                requestParams.put("extraObj", extraObj);

                if (doAction.compareToIgnoreCase("import") == 0) {
                    logger.debug("A(( Import start : "+new Date());
                    String exceededLimit = request.getParameter("exceededLimit");
                    if(exceededLimit.equalsIgnoreCase("yes")){ //If file contains records more than 1500 then Import file in background using thread
                        String logId = ImportUtil.addPendingImportLog(requestParams, importDao);
                        requestParams.put("logId", logId);
                        importHandler.setRequestParams(requestParams);
                        importThreadExecutor.startThread(importHandler); 
                        jobj.put("success", true);
                    } else {
                        jobj = importHandler.importFileData(requestParams);
                    }
                    jobj.put("exceededLimit", exceededLimit);
                    logger.debug("A(( Import end : "+new Date());
                } else if (doAction.compareToIgnoreCase("validateData") == 0) {
                    requestParams.put("servletContext", this.getServletContext());
                    jobj = importHandler.validateFileData(requestParams);
                    jobj.put("success", true);
                }

//                importHandlerObj.add(requestParams);
//                if (!importHandlerObj.isIsWorking()) {
//                    Thread t = new Thread(importHandlerObj);
//                    t.start();
//                }
                if(module.equals("Target")) {
                    jobj.put("TLID", targetList.getId());
                }
//                jobj.put("success", true);
            } else if (doAction.compareToIgnoreCase("getMapCSV") == 0) {
                jobj = ImportUtil.getMappingCSVHeader(request);
                View = "jsonView-ex";
                String filename = jobj.getString("name");
                String tableName = importDao.getTableName(filename);
                if(tableName.length()>64){ // To fixed Mysql ERROR 1103 (42000): Incorrect table name
                    throw new DataInvalidateException("Filename is too long, use upto 28 characters.");
                }
                importDao.createFileTable(tableName, jobj.getInt("cols"));
                importHandler.dumpCSVFileData(filename, jobj.getString("delimiterType"), 0, importDao);
            } else if (doAction.compareToIgnoreCase("getXLSData") == 0) {
                try{
                    String filename = request.getParameter("filename");
                    int sheetNo = Integer.parseInt(request.getParameter("index"));
                    jobj = ImportUtil.parseXLS(filename, sheetNo);
                } catch(Exception e) {
                    logger.warn(e.getMessage(), e);
                    try{
                        jobj.put("msg", e.getMessage());
                        jobj.put("lsuccess", false);
                        jobj.put("valid", true);
                    }catch(JSONException ex){
                        logger.warn(ex.getMessage(), ex);
                    }
                }
                View = "jsonView-ex";
            }  else if (doAction.compareToIgnoreCase("dumpXLS") == 0) {
                int sheetNo = Integer.parseInt(request.getParameter("index"));
                int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
                int columns = Integer.parseInt(request.getParameter("totalColumns"));
                String filename = request.getParameter("onlyfilename");
//                String actualfilename = ImportLog.getActualFileName(filename);
                String tableName = importDao.getTableName(filename);
                importDao.createFileTable(tableName, columns);
                importHandler.dumpXLSFileData(filename, sheetNo, rowIndex, importDao);
//                importDao.makeUploadedFileEntry(filename, actualfilename, tableName, companyId);
                jobj.put("success", true);
            } else if (doAction.compareToIgnoreCase("getXLSXData") == 0) {
                try{
                    String filename = request.getParameter("filename");
                    int sheetNo = Integer.parseInt(request.getParameter("index"));
                    jobj = ImportUtil.parseXLSX(filename, sheetNo);
                } catch(Exception e) {
                    logger.warn(e.getMessage(), e);
                    try{
                        jobj.put("msg", e.getMessage());
                        jobj.put("lsuccess", false);
                        jobj.put("valid", true);
                    }catch(JSONException ex){
                        logger.warn(ex.getMessage(), ex);
                    }
                }
                View = "jsonView-ex";
            }  else if (doAction.compareToIgnoreCase("dumpXLSX") == 0) {
                int sheetNo = Integer.parseInt(request.getParameter("index"));
                int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
                int columns = Integer.parseInt(request.getParameter("totalColumns"));
                String filename = request.getParameter("onlyfilename");
//                String actualfilename = ImportLog.getActualFileName(filename);
                String tableName = importDao.getTableName(filename);
                importDao.createFileTable(tableName, columns);
                importHandler.dumpXLSXFileData(filename, sheetNo, rowIndex, importDao);
//                importDao.makeUploadedFileEntry(filename, actualfilename, tableName, companyId);
                jobj.put("success", true);
            }

            logger.debug("A(( "+doAction+" end : "+new Date());
        } catch (Exception ex) {
            try {
                jobj.put("success", false);
                jobj.put("msg", ""+ex.getMessage());
            } catch (JSONException jex) {
                logger.warn(jex.getMessage(), jex);
            }
        } finally {
        }
        return new ModelAndView(View, "model", jobj.toString());
    }

}
