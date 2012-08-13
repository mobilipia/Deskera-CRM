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

package com.krawler.spring.importFunctionality;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.krawler.common.admin.ImportLog;
import com.krawler.common.admin.Modules;
import com.krawler.common.admin.ProjectFeature;
import com.krawler.common.util.Constants;
import com.krawler.common.util.DataInvalidateException;
import com.krawler.common.util.KrawlerLog;
import com.krawler.common.util.StringUtil;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.TimeZone;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ImportController extends MultiActionController implements MessageSourceAware{
    private ImportDAO importDao;
    public ImportHandler importHandler;
    private fieldManagerDAO fieldManagerDAOobj;
    private MessageSource mSource;

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    protected ImportThreadExecutor importThreadExecutor;
    
	public void setimportDAO(ImportDAO importDao) {
        this.importDao = importDao;
    }
    public void setimportHandler(ImportHandler importHandler) {
        this.importHandler = importHandler;
    }

    public ModelAndView getColumnConfig(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        boolean issuccess = false;
        String msg = "";
        try{
            String moduleName = request.getParameter("module");

            String ModuleId = null;
            String companyId = sessionHandlerImpl.getCompanyid(request);
            try {
                List list = importDao.getModuleObject(moduleName);
                Modules module = (Modules) list.get(0); //Will throw null pointer if no module entry found
                ModuleId = module.getId();
            } catch(Exception ex) {
                throw new DataInvalidateException("Column config entries are not available for "+moduleName+" module.");
            }
            JSONObject job=null;
            JSONArray DataJArr = ImportUtil.getModuleColumnConfig1(ModuleId, companyId,fieldManagerDAOobj,moduleName,false);
            for(int k=0;k<DataJArr.length();k++){
            	job= DataJArr.getJSONObject(k);
            	if(job.has("columnName") && job.has("localekey") && !StringUtil.isNullOrEmpty(job.getString("localekey"))){
            		job.put("columnName", mSource.getMessage(job.getString("localekey"),null, RequestContextUtils.getLocale(request)));
            		DataJArr.put(k, job);
            	}
            }
            jobj.put("data", DataJArr);
            jobj.put("count", DataJArr.length());
            issuccess = true;
        } catch (Exception ex) {
            msg = ""+ex.getMessage();
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
            } catch (JSONException ex) {
                Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView importRecords(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj=new JSONObject();
        String View = "jsonView";
        try {
            String doAction = request.getParameter("do");
            System.out.println("A(( "+doAction+" start : "+new Date());
            String companyId = sessionHandlerImpl.getCompanyid(request);
            if (doAction.compareToIgnoreCase("import") == 0 || doAction.compareToIgnoreCase("validateData") == 0) {
                HashMap<String, Object> requestParams = ImportUtil.getImportRequestParams(request);
                String eParams = request.getParameter("extraParams");
                JSONObject extraParams = StringUtil.isNullOrEmpty(eParams)?new JSONObject():new JSONObject(eParams);
                requestParams.put("extraParams", extraParams);
                requestParams.put("extraObj", null);
                requestParams.put("servletContext", this.getServletContext());
                if (doAction.compareToIgnoreCase("import") == 0) {
                    System.out.println("A(( Import start : "+new Date());
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
                    System.out.println("A(( Import end : "+new Date());
                } else if (doAction.compareToIgnoreCase("validateData") == 0) {
                    jobj = importHandler.validateFileData(requestParams);
                    jobj.put("success", true);
                }
            }else if (doAction.compareToIgnoreCase("getMapCSV") == 0) {
                jobj = ImportUtil.getMappingCSVHeader(request);
                View = "jsonView-ex";
                // Dump csv data in DB
                String filename = jobj.getString("name");
//                String actualfilename = ImportLog.getActualFileName(filename);
                String tableName = importDao.getTableName(filename);
                if(tableName.length()>64){ // To fixed Mysql ERROR 1103 (42000): Incorrect table name
                    throw new DataInvalidateException("Filename is too long, use upto 28 characters.");
                }
                importDao.createFileTable(tableName, jobj.getInt("cols"));
                importHandler.dumpCSVFileData(filename, jobj.getString("delimiterType"), 0, importDao);
//                importDao.makeUploadedFileEntry(filename, actualfilename, tableName, companyId);
            } else if (doAction.compareToIgnoreCase("getXLSData") == 0) {
                try{
                    String filename = request.getParameter("filename");
                    int sheetNo = Integer.parseInt(request.getParameter("index"));
                    jobj = ImportUtil.parseXLS(filename, sheetNo);
                } catch(Exception e) {
                    Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, e);
                    try{
                        jobj.put("msg", e.getMessage());
                        jobj.put("lsuccess", false);
                        jobj.put("valid", true);
                    }catch(JSONException ex){}
                }
                View = "jsonView-ex";
            } else if (doAction.compareToIgnoreCase("dumpXLS") == 0) {
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
                    Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, e);
                    try{
                        jobj.put("msg", e.getMessage());
                        jobj.put("lsuccess", false);
                        jobj.put("valid", true);
                    }catch(JSONException ex){}
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
            System.out.println("A(( "+doAction+" end : "+new Date());
        } catch (Exception ex) {
            try {
                jobj.put("success", false);
                jobj.put("msg", ""+ex.getMessage());
            } catch (JSONException jex) {
                Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, jex);
            }
        }
        return new ModelAndView(View, "model", jobj.toString());
    }

    public ModelAndView fileUploadXLS(HttpServletRequest request, HttpServletResponse response) {
        String View = "jsonView-ex";
        JSONObject jobj=new JSONObject();
        try {
                System.out.println("A(( Upload XLS start : "+new Date());
                jobj.put("success", true);
                FileItemFactory factory = new DiskFileItemFactory(4096,new File(ConfigReader.getinstance().get("UploadTempDir", "/tmp")));
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setSizeMax(10485760);
                List fileItems = upload.parseRequest(request);
                Iterator i = fileItems.iterator();
                String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "xlsfiles";
                String fileName=null;
                String fileid = UUID.randomUUID().toString();
                fileid = fileid.replaceAll("-", ""); // To append UUID without "-" [SK]
                String Ext = "";
                while(i.hasNext()){
                    java.io.File destDir = new java.io.File(destinationDirectory);
                    if (!destDir.exists()) { //Create xls file's folder if not present
                        destDir.mkdirs();
                    }

                    FileItem fi = (FileItem)i.next();
                    if(fi.isFormField())continue;
                    fileName = fi.getName();
                    if (fileName.contains(".")) {
                        Ext = fileName.substring(fileName.lastIndexOf("."));
                        int startIndex= fileName.contains("\\")?(fileName.lastIndexOf("\\")+1):0;
                        fileName = fileName.substring(startIndex, fileName.lastIndexOf("."));
                    }

                    if(fileName.length()>28){ // To fixed Mysql ERROR 1103 (42000): Incorrect table name
                        throw new DataInvalidateException("Filename is too long, use upto 28 characters.");
                    }
                    fi.write(new File(destinationDirectory, fileName+"_"+fileid+Ext));
                }

                POIFSFileSystem fs      =
                new POIFSFileSystem(new FileInputStream(destinationDirectory+"/"+ fileName+"_"+fileid+Ext));
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                int count=wb.getNumberOfSheets();
                JSONArray jArr=new JSONArray();
                for(int x=0;x<count;x++){
                    JSONObject obj=new JSONObject();
                    obj.put("name", wb.getSheetName(x));
                    obj.put("index", x);
                    jArr.put(obj);
                }
                jobj.put("file", destinationDirectory+"/"+ fileName+"_"+fileid+Ext);
                jobj.put("filename", fileName+"_"+fileid+Ext);
                jobj.put("data", jArr);
                jobj.put("msg", "Image has been successfully uploaded");
                jobj.put("lsuccess", true);
                jobj.put("valid", true);
        }catch(FileUploadBase.SizeLimitExceededException ex) {
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
            jobj.put("msg", "File exceeds max size limit i.e 10MB.");
            jobj.put("lsuccess", false);
            jobj.put("valid", true);
        }catch(Exception e){
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, e);
            try{
            	String msg =  e.getMessage().contains("Invalid header signature;")?"Not a real xls file. File contents are not XLS content/corrupted content.":e.getMessage();
                jobj.put("msg", msg);
                jobj.put("lsuccess", false);
                jobj.put("valid", true);
            }catch(Exception ex){}
        } finally {
            System.out.println("A(( Upload XLS end : "+new Date());
            return new ModelAndView(View, "model", jobj.toString());
        }
    }

    public ModelAndView fileUploadXLSX(HttpServletRequest request, HttpServletResponse response) {
        String View = "jsonView-ex";
        JSONObject jobj=new JSONObject();
        try {
                System.out.println("A(( Upload XLSX start : "+new Date());
                jobj.put("success", true);
                FileItemFactory factory = new DiskFileItemFactory(4096,new File(ConfigReader.getinstance().get("UploadTempDir", "/tmp")));
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setSizeMax(10485760); // 10Mb
                List fileItems = upload.parseRequest(request);
                Iterator i = fileItems.iterator();
                String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "xlsfiles";
                String fileName=null;
                String fileid = UUID.randomUUID().toString();
                fileid = fileid.replaceAll("-", ""); // To append UUID without "-" [SK]
                String Ext = "";
                while(i.hasNext()){
                    java.io.File destDir = new java.io.File(destinationDirectory);
                    if (!destDir.exists()) { //Create xls file's folder if not present
                        destDir.mkdirs();
                    }

                    FileItem fi = (FileItem)i.next();
                    if(fi.isFormField())continue;
                    fileName = fi.getName();
                    if (fileName.contains(".")) {
                        Ext = fileName.substring(fileName.lastIndexOf("."));
                        int startIndex= fileName.contains("\\")?(fileName.lastIndexOf("\\")+1):0;
                        fileName = fileName.substring(startIndex, fileName.lastIndexOf("."));
                    }

                    if(fileName.length()>28){ // To fixed Mysql ERROR 1103 (42000): Incorrect table name
                        throw new DataInvalidateException("Filename is too long, use upto 28 characters.");
                    }
                    fi.write(new File(destinationDirectory, fileName+"_"+fileid+Ext));
                }

                FileInputStream fs = new FileInputStream(destinationDirectory+"/"+ fileName+"_"+fileid+Ext);
                XSSFWorkbook wb = new XSSFWorkbook(fs);
                int count=wb.getNumberOfSheets();
                JSONArray jArr=new JSONArray();
                for(int x=0;x<count;x++){
                    JSONObject obj=new JSONObject();
                    obj.put("name", wb.getSheetName(x));
                    obj.put("index", x);
                    jArr.put(obj);
                }
                jobj.put("file", destinationDirectory+"/"+ fileName+"_"+fileid+Ext);
                jobj.put("filename", fileName+"_"+fileid+Ext);
                jobj.put("data", jArr);
                jobj.put("msg", "Image has been successfully uploaded");
                jobj.put("lsuccess", true);
                jobj.put("valid", true);
        }catch(FileUploadBase.SizeLimitExceededException ex) {
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
            jobj.put("msg", "File exceeds max size limit i.e 10MB.");
            jobj.put("lsuccess", false);
            jobj.put("valid", true);
        }catch(Exception e){
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, e);
            try{
                jobj.put("msg", e.getMessage());
                jobj.put("lsuccess", false);
                jobj.put("valid", true);
            }catch(Exception ex){}
        } finally {
            System.out.println("A(( Upload XLS end : "+new Date());
            return new ModelAndView(View, "model", jobj.toString());
        }
    }

    public ModelAndView getImportLog(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        boolean issuccess = false;
        String msg = "";
        try{
            DateFormat sdf = new SimpleDateFormat(Constants.yyyyMMddHHmmss);
            String tzDiff = authHandler.getTimeZoneDifference(request);
            TimeZone zone = TimeZone.getTimeZone("GMT" + tzDiff);
            sdf.setTimeZone(zone);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", request.getParameter("start"));
            requestParams.put("limit", request.getParameter("limit"));
            requestParams.put("startdate", sdf.parse(request.getParameter("startdate")).getTime());
            requestParams.put("enddate",  sdf.parse(request.getParameter("enddate")).getTime());
            requestParams.put("companyid",  sessionHandlerImpl.getCompanyid(request));
            StringBuffer moduleid = new StringBuffer();
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.leadFName) & 64) == 64) {
                moduleid.append("'" + Constants.MODULEID_LEAD + "',");
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.productFName) & 32) == 32) {
                moduleid.append("'" + Constants.MODULEID_PRODUCT + "',");
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.accountFName) & 32) == 32) {
                moduleid.append("'" + Constants.MODULEID_ACCOUNT + "',");
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.contactFName) & 32) == 32) {
               moduleid.append("'" + Constants.MODULEID_CONTACT + "',");
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.targetFName) & 32) == 32) {
               moduleid.append("'" + Constants.MODULEID_TARGET + "',");
            }
            if ((sessionHandlerImpl.getPerms(request, ProjectFeature.opportunityFName) & 32) == 32) {
                moduleid.append("'" + Constants.MODULEID_OPPORTUNITY + "',");
            }
            // fetch calibration report log entry (For sunrise client)
            moduleid.append("'" + Constants.MODULEID_CALIBRATION + "',");
            String moduleids = moduleid.substring(0, (moduleid.length()-1));
            requestParams.put("moduleid", moduleids);
            KwlReturnObject result = importDao.getImportLog(requestParams);
            List list = result.getEntityList();
            DateFormat df = authHandler.getDateFormatter(request);
            df.setTimeZone(zone);
            JSONArray jArr = new JSONArray();
            Iterator itr = list.iterator();
            while(itr.hasNext()) {
                ImportLog ilog = (ImportLog) itr.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("id", ilog.getId());
                jtemp.put("filename", ilog.getFileName());
                jtemp.put("storename", ilog.getStorageName());
                jtemp.put("failurename", ilog.getFailureFileName());
                jtemp.put("log", ilog.getLog());
                jtemp.put("imported", ilog.getImported());
                jtemp.put("total", ilog.getTotalRecs());
                jtemp.put("rejected", ilog.getRejected());
                jtemp.put("type", ilog.getType());
                jtemp.put("importon", df.format(ilog.getImportDate()));
                jtemp.put("module", ilog.getModule().getModuleName());
                jtemp.put("importedby", (ilog.getUser().getFirstName()==null?"":ilog.getUser().getFirstName())+" "+(ilog.getUser().getLastName()==null?"":ilog.getUser().getLastName()));
                jtemp.put("company", ilog.getCompany().getCompanyName());
                jArr.put(jtemp);
            }
            jobj.put("data", jArr);
            jobj.put("count", result.getRecordTotalCount());
            issuccess = true;
        } catch (Exception ex){
            msg = ""+ ex.getMessage();
            Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
            } catch (JSONException ex) {
                Logger.getLogger(ImportController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public void downloadFileData(HttpServletRequest request, HttpServletResponse response) {
        try {
            String filename = request.getParameter("filename");
            String storagename = request.getParameter("storagename");
            String filetype = request.getParameter("type");
            String destinationDirectory = storageHandlerImpl.GetDocStorePath();
            destinationDirectory += filetype.equalsIgnoreCase("csv")?"importplans":"xlsfiles";
            File intgfile = new File(destinationDirectory + "/" + storagename);
            byte[] buff = new byte[(int) intgfile.length()];

            try {
                FileInputStream fis = new FileInputStream(intgfile);
                int read = fis.read(buff);
            } catch (IOException ex) {
                filename = "file_not_found.txt";
            }

            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentType("application/octet-stream");
            response.setContentLength(buff.length);
            response.getOutputStream().write(buff);
            response.getOutputStream().flush();
        } catch (IOException ex) {
            KrawlerLog.op.warn("Unable To Download File :" + ex.toString());
        } catch (Exception ex) {
            KrawlerLog.op.warn("Unable To Download File :" + ex.toString());
        }

    }
    
    /**
     * @param importThreadExecutor
     */
    public void setImportThreadExecutor(ImportThreadExecutor importThreadExecutor) {
		this.importThreadExecutor = importThreadExecutor;
	}
	
	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.mSource=messageSource;
	}
}
