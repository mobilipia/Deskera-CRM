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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.FieldParams;
import com.krawler.common.admin.ImportLog;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.Modules;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.CsvReader;
import com.krawler.common.util.DataInvalidateException;
import com.krawler.common.util.KrawlerLog;
import com.krawler.common.util.StringUtil;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.common.comet.ServerEventManager;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ImportUtil {
	private static final DecimalFormat dfmt= new DecimalFormat("#.#####");
	
    private static Log LOG = LogFactory.getLog(ImportUtil.class);
	
	private static final int IMPORT_LIMIT = 1500;
	
	private static final String df = "yyyy-MM-dd";
	
	private static final String df_full = "yyyy-MM-dd hh:mm:ss";
	
	private static final String df_customfield = "MMM dd, yyyy hh:mm:ss aaa";
	
	private static final String EmailRegEx = "^[\\w-]+([\\w!#$%&'*+/=?^`{|}~-]+)*(\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@[\\w-]+(\\.[\\w-]+)*(\\.[\\w-]+)$";
    
	private static Object[] tempFileData=null;
	/**
	 * @param moduleId
	 * @param companyid
	 * @param importDao
	 * @return
	 * @throws ServiceException
	 */
	public static JSONArray getModuleColumnConfig(String moduleId, String companyid, fieldManagerDAO fieldManagerDAOobj) throws ServiceException {
        JSONArray jArr = new JSONArray();
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("filter_names", Arrays.asList("dh.Module.id", "dh.allowImport"));
        requestParams.put("filter_values", Arrays.asList(moduleId, true));
        KwlReturnObject kmsg = fieldManagerDAOobj.getDefaultHeader(requestParams);

        try {
            Iterator  itr = kmsg.getEntityList().iterator();
            while (itr.hasNext()) {
                DefaultHeader obj = (DefaultHeader) itr.next();
                requestParams.clear();
                requestParams.put("filter_names", Arrays.asList("c.defaultheader.id", "c.company.companyID"));
                requestParams.put("filter_param", Arrays.asList(obj.getId(), companyid));
                kmsg = fieldManagerDAOobj.getColumnHeader(requestParams);
                Iterator ite1 = kmsg.getEntityList().iterator();
                if(ite1.hasNext()) {
                    ColumnHeader obj1 = (ColumnHeader) ite1.next();
                    JSONObject jtemp = getObject(obj);
                    jtemp.put("columnName", StringUtil.isNullOrEmpty(obj1.getNewHeader())? obj1.getDefaultheader().getDefaultHeader() :obj1.getNewHeader());
                    jtemp.put("isMandatory", obj1.isMandotory());
                    jArr.put(jtemp);
                } else if(!obj.isCustomflag()) {
                    JSONObject jtemp = getObject(obj);
                    jArr.put(jtemp);
                }
            }

        } catch (JSONException ex) {
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jArr;
    }
	
	public static JSONArray getModuleColumnConfig1(String moduleId, String companyid, fieldManagerDAO fieldManagerDAOobj,String modulName, boolean allowAutoNoField) throws ServiceException {
        // THIS is creating issue
        JSONArray jArr = new JSONArray();
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        ArrayList filter_params = new ArrayList();
        ArrayList filter_names = new ArrayList();
        filter_names.add("dh.moduleName");
        filter_params.add(modulName);
        filter_names.add("!dh.configid");
        filter_params.add("1");
        filter_names.add("dh.allowImport");
        filter_params.add(true);
        filter_names.add("dh.customflag");
        filter_params.add(false);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_values", filter_params);
        requestParams.put("companyid", companyid);
        requestParams.put("moduleName", modulName);
        requestParams.put("fetchautonofield", allowAutoNoField);
        

        KwlReturnObject kmsg = fieldManagerDAOobj.getDefaultHeader(requestParams);

        try {
            List<DefaultHeader> defaultHeaders = kmsg.getEntityList();
            List<String> headerIds = new ArrayList<String>();
            for (DefaultHeader obj: defaultHeaders) {
                headerIds.add(obj.getId());
            }
            
            Map<String, Object[]> results = getColumnHeaderMap(fieldManagerDAOobj, headerIds, companyid);
            
            for (DefaultHeader obj: defaultHeaders) {
                if (results.containsKey(obj.getDefaultHeader()))
                {
                    Object[] mapEntry = results.get(obj.getDefaultHeader());
                    ColumnHeader obj1 = (ColumnHeader) mapEntry[1];
                    DefaultHeader obj2 = (DefaultHeader) mapEntry[0];
                    JSONObject jtemp = getObject(obj2);
                    jtemp.put("columnName", StringUtil.isNullOrEmpty(obj1.getNewHeader())? obj2.getDefaultHeader() :obj1.getNewHeader());
                    jtemp.put("isMandatory", obj1.isMandotory());
                    jArr.put(jtemp);
                }
                else if(!obj.isCustomflag()) {
                    JSONObject jtemp = getObject(obj);
                    jArr.put(jtemp);
                }
            } 

        } catch (JSONException ex) {
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jArr;
    }
	
	private static Map<String, Object[]> getColumnHeaderMap(fieldManagerDAO fieldManagerDAOobj, List<String> headerIds, String companyId)
    {
	    Map<String, Object[]> result = new HashMap<String, Object[]>(); 
	    List<Object[]> colList = fieldManagerDAOobj.getColumnHeader(companyId, headerIds);
	    
	    if (colList != null)
	    {
	        for (Object[] col: colList)
	        {
	            DefaultHeader dh = (DefaultHeader) col[0];
	            result.put(dh.getDefaultHeader(), col);
	        }
	    }
        return result;
    }

	/**
	 * @param dh
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getObject(DefaultHeader dh) throws JSONException {
        JSONObject jtemp = new JSONObject();
        jtemp.put("id", dh.getId());
        jtemp.put("columnName", dh.getDefaultHeader());
        jtemp.put("pojoName", dh.getPojoMethodName());
        jtemp.put("isMandatory", dh.isMandatory());
        jtemp.put("isNotNull", dh.isHbmNotNull());
        jtemp.put("maxLength", dh.getMaxLength());
        jtemp.put("defaultValue", dh.getDefaultValue());
        jtemp.put("validatetype", dh.getValidateType());
        jtemp.put("refModule", dh.getRefModule_PojoClassName());
        jtemp.put("refFetchColumn", dh.getRefFetchColumn_HbmName());
        jtemp.put("refDataColumn", dh.getRefDataColumn_HbmName());
        jtemp.put("customflag", dh.isCustomflag());
        jtemp.put("pojoHeader", dh.getPojoheadername());
        jtemp.put("recordname", dh.getRecordname());
        jtemp.put("xtype", dh.getXtype());
        jtemp.put("configid", dh.getConfigid());
        jtemp.put("refcolumn_number",dh.getDbcolumnrefname());
        jtemp.put("dbcolumnname",dh.getDbcolumnname());
        jtemp.put("localekey",dh.getLocalekey());
        return jtemp;
    }
	
	/**
	 * @param requestParams
	 * @param txnManager
	 * @param kwlCommonTablesDAOObj
	 * @param importDao
	 * @return
	 */
	public static JSONObject validateFileData(HashMap<String, Object> requestParams, HibernateTransactionManager txnManager, kwlCommonTablesDAO kwlCommonTablesDAOObj, ImportDAO importDao, fieldManagerDAO fieldManagerDAOobj) {
        JSONObject jobj = new JSONObject();
        String msg = "";
        boolean issuccess = true;

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("import_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);
        boolean commitedEx = false;

        int total = 0, failed = 0, fileSize=0;
        String fileName = "", extn = "";
        Modules module = null;
        String exceededLimit = "no", channelName="";
        try {
            String companyid = (String) requestParams.get("companyid");
            String mode = (String) requestParams.get("modName");
            fileName = (String) requestParams.get("filename");
            extn = fileName.substring(fileName.lastIndexOf(".")+1);
            channelName = "/ValidateFile/"+fileName;

            Object extraObj = requestParams.get("extraObj");
            JSONObject extraParams = (JSONObject) requestParams.get("extraParams");

            String jsondata = (String) requestParams.get("resjson");
            JSONObject rootcsvjobj = new JSONObject(jsondata);
            JSONArray mapping = rootcsvjobj.getJSONArray("root");

            String dateFormat=null, dateFormatId = (String) requestParams.get("dateFormat");
            if(extn.equalsIgnoreCase("csv") && !StringUtil.isNullOrEmpty(dateFormatId)){
                KWLDateFormat kdf = (KWLDateFormat) kwlCommonTablesDAOObj.getClassObject(KWLDateFormat.class.getName(), dateFormatId);
                dateFormat = kdf!=null ? kdf.getJavaForm() : null;
            }

            String classPath="", primaryKey="", uniqueKeyMethodName="", uniqueKeyHbmName="";
            try {
                List list = importDao.getModuleObject(mode);
                module = (Modules) list.get(0); //Will throw null pointer if no module entry found
            } catch (Exception ex) {
                throw new DataInvalidateException("Column config not available for module "+mode);
            }

            try {
                classPath = module.getPojoClassPathFull().toString();
                primaryKey = module.getPrimaryKey_MethodName().toString();
            } catch (Exception ex) {
                throw new DataInvalidateException("Please set proper properties for module "+mode);
            }
            uniqueKeyMethodName = module.getUniqueKey_MethodName();
            uniqueKeyHbmName = module.getUniqueKey_HbmName();

            JSONArray columnConfig = getModuleColumnConfig1(module.getId(), companyid, fieldManagerDAOobj,module.getModuleName(),false);
            String tableName = importDao.getTableName(fileName);
            KwlReturnObject kresult = importDao.getFileData(tableName, new HashMap<String, Object>());
            List fileDataList = kresult.getEntityList();
            Iterator itr = fileDataList.iterator();

            importDao.markRecordValidation(tableName, -1, 1, "", ""); //reset all invalidation
            JSONArray recordJArr = new JSONArray(), columnsJArr = new JSONArray(), DataJArr = new JSONArray();
            if (itr.hasNext()) { //
                Object[] fileData = (Object[]) itr.next();
                JSONObject jtemp = new JSONObject();
                jtemp.put("header", "Row No.");
                jtemp.put("dataIndex", "col0");
                jtemp.put("width", 50);
                columnsJArr.put(jtemp);

                for (int i = 1; i < fileData.length - 3; i++) {    //Discard columns, id at index 0 and isvalid,validationlog at last 2.
                    jtemp = new JSONObject();
                    jtemp.put("header", fileData[i] == null ? "" : fileData[i].toString());
                    jtemp.put("dataIndex", "col" + i);
                    columnsJArr.put(jtemp);
                }

                jtemp = new JSONObject();
                jtemp.put("header", "Validation Log");
//                jtemp.put("hidden", true);
                jtemp.put("dataIndex", "validateLog");
                columnsJArr.put(jtemp);


                //Create record Obj for grid's store
                for (int i = 0; i < fileData.length-1; i++) {
                    jtemp = new JSONObject();
                    jtemp.put("name", "col"+i);
                    recordJArr.put(jtemp);
                }
                jtemp = new JSONObject();
                jtemp.put("name", "validateLog");
                recordJArr.put(jtemp);
            }

            try {
                jobj.put("record", recordJArr);
                jobj.put("columns", columnsJArr);
                jobj.put("data", DataJArr);
                jobj.put("count", failed);
                jobj.put("valid", 0);
                jobj.put("totalrecords", total);
                jobj.put("isHeader", true);
                jobj.put("finishedValidation", false);
                ServerEventManager.publish(channelName, jobj.toString(), (ServletContext) requestParams.get("servletContext"));
            } catch(Exception ex) {
                throw ex;
            }

            fileSize = fileDataList.size()-1;
            fileSize = fileSize>=IMPORT_LIMIT?IMPORT_LIMIT:fileSize; // fileSize used for showing progress bar[Client Side]

            jobj.put("isHeader", false);
            int recIndex = 0;
            Session session = txnManager.getSessionFactory().getCurrentSession();
            int batchCounter = 0;
            while (itr.hasNext()) {
                Object[] fileData= (Object[])itr.next();
                tempFileData=null;
                tempFileData=fileData;
                recIndex= (Integer) fileData[0];
                HashMap<String,Object> dataMap = new HashMap<String,Object>();
                HashMap<String,Object> columnHeaderMap = new HashMap<String,Object>();
                HashMap<String,Object> columnCSVindexMap = new HashMap<String,Object>();
                JSONArray customfield = new JSONArray();
                for (int k = 0; k < mapping.length(); k++) {
                    JSONObject mappingJson = mapping.getJSONObject(k);
                    String datakey = mappingJson.getString("columnname");
                    Object dataValue = cleanHTML((String) fileData[mappingJson.getInt("csvindex")+1]); //+1 for id column at index-0
                    dataMap.put(datakey, dataValue);
                    columnHeaderMap.put(datakey, mappingJson.getString("csvheader"));
                    columnCSVindexMap.put(datakey, mappingJson.getInt("csvindex")+1);
                }

                for (int j=0; j< extraParams.length(); j++) {
                    String datakey = (String) extraParams.names().get(j);
                    Object dataValue = extraParams.get(datakey);
                    dataMap.put(datakey, dataValue);
                }

                try {
                    if(total>=IMPORT_LIMIT){
                        exceededLimit = "yes";
                        break;
                    }
                    //Update processing status at client side
                    if(total>0 && total%10==0){
                        try {
                            ServerEventManager.publish(channelName, "{parsedCount:"+total+",invalidCount:"+failed+", fileSize:"+fileSize+", finishedValidation:false}", (ServletContext) requestParams.get("servletContext"));
                        } catch(Exception ex) {
                            throw ex;
                        }
                    }

//                    CheckUniqueRecord(requestParams, dataMap, classPath, uniqueKeyMethodName, uniqueKeyHbmName);
                    validateDataMAP2(requestParams, dataMap, columnConfig, customfield, columnHeaderMap, columnCSVindexMap, dateFormat, importDao,new HashMap<String, String>());
                } catch(Exception ex) {
                    failed++;
                    String errorMsg = ex.getMessage(), invalidColumns = "";
                     try {
                        JSONObject errorLog = new JSONObject(errorMsg);
                        errorMsg = errorLog.getString("errorMsg");
                        invalidColumns = errorLog.getString("invalidColumns");
                    } catch (JSONException jex) {
                    }

                    importDao.markRecordValidation(tableName, recIndex, 0, errorMsg, invalidColumns);
                    if(batchCounter%30==0){
                    	session.flush();
                    	session.clear();
                    }
                    batchCounter++;
                    JSONObject jtemp = new JSONObject();
                    if(tempFileData!=null){
                    	for (int i = 0; i < tempFileData.length-2; i++) {
                    		jtemp.put("col"+i, tempFileData[i] == null ? "" : tempFileData[i].toString());
                    	}
                    }else{
                    	for (int i = 0; i < fileData.length-2; i++) {
                    		jtemp.put("col"+i, fileData[i] == null ? "" : fileData[i].toString());
                    	}
                    }
                    jtemp.put("invalidcolumns", invalidColumns);
                    jtemp.put("validateLog", errorMsg);
                    DataJArr.put(jtemp);

//                    try {
//                        jtemp.put("count", failed);
//                        jtemp.put("totalrecords", total+1);
//                        jtemp.put("fileSize", fileSize);
//                        jtemp.put("finishedValidation", false);
//                        ServerEventManager.publish(channelName, jtemp.toString(), (ServletContext) requestParams.get("servletContext"));
//                    } catch(Exception dex) {
//                        throw dex;
//                    }
                }
                total++;
            }

            int success = total-failed;
            if(total == 0) {
                msg = "Empty file.";
            } else if(success == 0) {
                msg = "All the records are invalid.";
            } else if(success == total) {
                msg = "All the records are valid.";
            } else {
                msg = ""+success+" valid record"+(success>1?"s":"")+"";
                msg += (failed==0?".":" and "+failed+" invalid record"+(failed>1?"s":"")+".");
            }

            jobj.put("record", recordJArr);
            jobj.put("columns", columnsJArr);
            jobj.put("data", DataJArr);
            jobj.put("count", failed);
            jobj.put("valid", success);
            jobj.put("totalrecords", total);

            try {
                ServerEventManager.publish(channelName, "{parsedCount:"+total+",invalidCount:"+failed+", fileSize:"+fileSize+", finishedValidation:true}", (ServletContext) requestParams.get("servletContext"));
            } catch(Exception ex) {
                throw ex;
            }

            try {
                txnManager.commit(status);
            } catch(Exception ex) {
                commitedEx = true;
                throw ex;
            }
        } catch (Exception e) {
            if (!commitedEx) { //if exception occurs during commit then dont call rollback
                txnManager.rollback(status);
            }
            issuccess = false;
            msg = ""+e.getMessage();
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
                jobj.put("exceededLimit", exceededLimit);
            } catch (JSONException ex) {
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jobj;
    }
	
	/**
	 * @param strText
	 * @return
	 * @throws IOException
	 */
	private static String cleanHTML(String strText) throws IOException {
        return strText!=null?StringUtil.serverHTMLStripper(strText):null;
	}
	
	/**
	 * @param requestParams
	 * @param dataMap
	 * @param columnConfigArray
	 * @param customfield
	 * @param columnHeaderMap
	 * @param columnCSVindexMap
	 * @param dateFormat
	 * @param importDao
	 * @throws DataInvalidateException
	 */
	private static void validateDataMAP2(HashMap<String, Object> requestParams, HashMap<String, Object> dataMap, JSONArray columnConfigArray, JSONArray customfield, HashMap<String, Object> columnHeaderMap,  HashMap<String, Object> columnCSVindexMap, String dateFormat, ImportDAO importDao,HashMap<String, String> autoNoMap) throws DataInvalidateException {
        String errorMsg = "", invalidColumns="";
        Integer csvIndex=null;
        for (int k = 0; k < columnConfigArray.length(); k++) {
            JSONObject columnConfig = null;
            String column = "";
            try {
                columnConfig = columnConfigArray.getJSONObject(k);
                column = columnConfig.getString("pojoName");
                csvIndex=(Integer)columnCSVindexMap.get(column);

                if (dataMap.containsKey(column)) {
                    validateColumnData(requestParams, dataMap, columnConfig, column, customfield, columnHeaderMap, dateFormat, importDao,csvIndex, autoNoMap);
                } else {
                    if (columnConfig.has("xtype") && columnConfig.getString("xtype").equals(String.valueOf(Constants.CUSTOM_FIELD_AUTONUMBER))) {
                        if(autoNoMap.containsKey(column)) {
                            int nextAutoNo = Integer.parseInt(autoNoMap.get(column));
                            Object vDataValue = fieldDataManager.getNextAutoNo(autoNoMap.get(column+"_"+Constants.CUSTOM_FIELD_PREFIX), nextAutoNo, autoNoMap.get(column+"_"+Constants.CUSTOM_FIELD_SUFFIX));
                            autoNoMap.put(column,String.valueOf(nextAutoNo+1));
                            JSONObject jobj = new JSONObject();
                            createCustomColumnJSON(jobj, columnConfig, vDataValue);
                            customfield.put(jobj);
                        }
                    } else if (columnConfig.has("defaultValue")) {
                        dataMap.put(column, getDefaultValue(columnConfig));
                    }
                }
            } catch (Exception ex) {
                errorMsg += ex.getMessage();
                invalidColumns += ("col"+columnCSVindexMap.get(column)+",");
            }
        }
        if(errorMsg.length()>0) {
            try {
                JSONObject errorLog = new JSONObject();
                errorLog.put("errorMsg", errorMsg);
                errorLog.put("invalidColumns", invalidColumns);
                errorMsg = errorLog.toString();
            } catch (JSONException ex) {
            }
            throw new DataInvalidateException(errorMsg);
        }
    }
	
	/**
	 * @param requestParams
	 * @param dataMap
	 * @param columnConfig
	 * @param column
	 * @param customfield
	 * @param columnHeaderMap
	 * @param dateFormat
	 * @param importDao
	 * @throws JSONException
	 * @throws DataInvalidateException
	 * @throws ParseException
	 */
	private static void validateColumnData(HashMap<String, Object> requestParams, HashMap<String, Object> dataMap, JSONObject columnConfig, String column, JSONArray customfield, HashMap<String, Object> columnHeaderMap, String dateFormat, ImportDAO importDao,Integer colCsvIndex,HashMap<String, String> autoNoMap) throws JSONException, DataInvalidateException, ParseException {
        int maxLength = columnConfig.getInt("maxLength");
        String csvHeader = (String) columnHeaderMap.get(column);
        csvHeader = (csvHeader==null?csvHeader:csvHeader.replaceAll("\\."," "));//remove '.' from csv Header
        String columnHeader = columnConfig.getString("columnName");
        String data = dataMap.get(column)==null?null:String.valueOf(dataMap.get(column));
        Object vDataValue = data;
        
        if(columnConfig.has("validatetype")) {
           String validatetype = columnConfig.getString("validatetype");
           boolean customflag = false;
           if(columnConfig.has("customflag")) {
                customflag = columnConfig.getBoolean("customflag");
           }
           if(validatetype.equalsIgnoreCase("integer")) {
                try {
                    if(!StringUtil.isNullOrEmpty(data)) { // Remove ","(comma) from number
                        data = data.replaceAll(",", "");
                    }
                    if(maxLength>0 && data!=null && data.length() > maxLength) { // Added null value check for data[Sandeep k]
                        throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
                    }
                    vDataValue = StringUtil.isNullOrEmpty(data) ? "" : Integer.parseInt(data);
                } catch(DataInvalidateException dex) {
                    throw dex;
                } catch(Exception ex) {
                    throw new DataInvalidateException("Incorrect numeric value for "+csvHeader+", Please ensure that value type of "+csvHeader+" matches with the "+ columnHeader+".");
                }
           } else if(validatetype.equalsIgnoreCase("double")) {
                try {
                    if(!StringUtil.isNullOrEmpty(data)) { // Remove ","(comma) from number
                        data = data.replaceAll(",", "");
                    }
                    if(maxLength>0 && data!=null && data.length() > maxLength) {
                        throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
                    }
                    vDataValue = StringUtil.isNullOrEmpty(data) ? "" : Double.parseDouble(data);
                } catch(DataInvalidateException dex) {
                    throw dex;
                } catch(Exception ex) {
                    throw new DataInvalidateException("Incorrect numeric value for "+csvHeader+", Please ensure that value type of "+csvHeader+" matches with the "+ columnHeader+".");
                }
           } else if(validatetype.equalsIgnoreCase("date")) {
               if(!StringUtil.isNullOrEmpty(data)) {
                    String ldf = dateFormat!=null ? dateFormat : (data.length()>10 ? df_full : df);
                    try {
                        if(maxLength>0 && data==null) {
                            throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
                        }
                        DateFormat sdf = new SimpleDateFormat(ldf);sdf.setLenient(false);
                        sdf.setTimeZone(TimeZone.getTimeZone("GMT"+ requestParams.get("tzdiff")));
                        vDataValue = StringUtil.isNullOrEmpty(data) ? null : sdf.parse(data);
                        if(customflag && vDataValue != null) {
                            vDataValue = ((Date) vDataValue).getTime();
                            tempFileData[colCsvIndex]=vDataValue;
                            if(tempFileData!=null){//In case of invalid records date field is showing number(Long value) so replacing by proper format in tempFileData
                          		tempFileData[colCsvIndex.intValue()]=new Date((Long)vDataValue);
                            }
                        }
                    } catch(DataInvalidateException dex) {
                        throw dex;
                    } catch(Exception ex) {
                    	try{
                    		vDataValue = Long.parseLong(data);
                    		 if(!customflag && vDataValue != null) {
                    			 vDataValue = new Date((Long)vDataValue);
                    		 }
                    		
                    		if(tempFileData!=null){//In case of invalid records date field is showing number(Long value) so replacing by proper format in tempFileData
                    			tempFileData[colCsvIndex.intValue()]=customflag? new Date((Long)vDataValue):vDataValue;
                    		}
                    	}catch(Exception e){
                    		throw new DataInvalidateException("Incorrect date format for "+csvHeader+", Please specify values in "+ldf+" format.");
                    	}
                    }
               } else {
                   vDataValue = null;
               }
           } else if(validatetype.equalsIgnoreCase("time")) {
               if(!StringUtil.isNullOrEmpty(data)) {
                   //@@@ need to uncomment
//                    Pattern pattern = Pattern.compile(EmailRegEx);
//                    if(!pattern.matcher(data).matches()){
//                        throw new DataInvalidateException("Incorrect time format for "+columnConfig.getString("columnName")+" use HH:MM AM or PM");
//                    }
                    vDataValue = data;
               } else {
                    vDataValue = null;
               }
           } else if(validatetype.equalsIgnoreCase("ref")) {
                if(!StringUtil.isNullOrEmpty(data)) {
                    try {
                        String pref = (String) requestParams.get("masterPreference"); //0:Skip Record, 1:Skip Column, 2:Add new
                        if(columnConfig.has("refModule") && columnConfig.has("refDataColumn") && columnConfig.has("refFetchColumn") && columnConfig.has("configid")) {
                            requestParams.put("defaultheader", columnConfig.getString("columnName"));
                            List list = getRefData(requestParams, columnConfig.getString("refModule"), columnConfig.getString("refDataColumn"), columnConfig.getString("refFetchColumn"), columnConfig.getString("configid"), data, importDao);
                            if(list.size()==0){
//                                String configid = columnConfig.getString("configid");
//                                // Configid =>> 1:Owner, 2:Product, 3:Account, 4:Contact
//                                // then we can't create new entry for such module
//                                if(pref.equalsIgnoreCase("2") && (configid.equals("1") || configid.equals("2") || configid.equals("3") || configid.equals("4"))) {
//                                    throw new DataInvalidateException(csvHeader+" entry not found in master list for "+ columnHeader +" dropdown."); // Throw ex to skip record.
//                                } else
                                if(pref.equalsIgnoreCase("0")){ //Skip Record
                                    if(!ImportHandler.isMasterTable(columnConfig.getString("refModule"))){ // Cant't create entry for ref. module
                                        throw new DataInvalidateException(csvHeader+" entry not present in "+ columnHeader +" list, Please create new "+ columnHeader +" entry for '"+(data.replaceAll("\\.", ""))+"' as it requires some other details.");
                                    } else
                                        throw new DataInvalidateException(csvHeader+" entry not found in master list for "+ columnHeader +" dropdown."); // Throw ex to skip record.
                                } else if(pref.equalsIgnoreCase("1")) {
                                    vDataValue = null;  // Put 'null' value to skip column data.
                                } else if(pref.equalsIgnoreCase("2")) {
                                    if(!ImportHandler.isMasterTable(columnConfig.getString("refModule"))){ // Cant't create entry for ref. module
                                        throw new DataInvalidateException(csvHeader+" entry not present in "+ columnHeader +" list, Please create new "+ columnHeader +" entry for '"+(data.replaceAll("\\.", ""))+"' as it requires some other details.");
                                    }
                                }
                            } else {
                                vDataValue = list.get(0).toString();
                            }
                        } else {
                            throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                        }
                    } catch(ServiceException ex) {
                        throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                    } catch(DataInvalidateException ex) {
                        throw ex;
                    } catch(Exception ex) {
                        throw new DataInvalidateException(csvHeader+" entry not found in master list for "+ columnHeader +" dropdown.");
                    }
                } else {
                    vDataValue = null;
                }
           } else if(!customflag && validatetype.equalsIgnoreCase("multiselect")) {
                if(!StringUtil.isNullOrEmpty(data)) {
                    try {
                        String pref = (String) requestParams.get("masterPreference"); //0:Skip Record, 1:Skip Column, 2:Add new
                        
                        if (columnConfig.has("refModule") && columnConfig.has("refDataColumn") && columnConfig.has("refFetchColumn") && columnConfig.has("configid")) {
                            String[] multiData = data.toString().split(Constants.Custom_Column_Sep);
                            String mdata = "";
                            Boolean isRefData = columnConfig.has("refModule") && columnConfig.has("refDataColumn") && columnConfig.has("refFetchColumn") && columnConfig.has("configid");
                            List list = null;
                            for (int i = 0; isRefData && i < multiData.length; i++) { // Reference module
                                list = getRefData(requestParams, columnConfig.getString("refModule"), columnConfig.getString("refDataColumn"), columnConfig.getString("refFetchColumn"), columnConfig.getString("configid"), multiData[i], importDao);
                                if (list.size() == 0) {
                                    if (pref.equalsIgnoreCase("0")) { //Skip Record
                                        if (!ImportHandler.isMasterTable(columnConfig.getString("refModule"))) { // Cant't create entry for ref. module
                                            throw new DataInvalidateException(csvHeader + " entry not present in " + columnHeader + " list, Please create new " + columnHeader + " entry for '" + (multiData[i].replaceAll("\\.", "")) + "' as it requires some other details.");
                                        } else {
                                            throw new DataInvalidateException(csvHeader + " entry not found in master list for " + columnHeader + " dropdown."); // Throw ex to skip record.
                                        }
                                    } else if (pref.equalsIgnoreCase("2")) {
                                        if (!ImportHandler.isMasterTable(columnConfig.getString("refModule"))) { // Cant't create entry for ref. module
                                            throw new DataInvalidateException(csvHeader + " entry not present in " + columnHeader + " list, Please create new " + columnHeader + " entry for '" + (multiData[i].replaceAll("\\.", "")) + "' as it requires some other details.");
                                        }
                                    }
                                } else {
                                    mdata += list.get(0).toString() + Constants.Custom_Column_Sep;
                                }
                            }
                            if (mdata.length() > 0) {
                                vDataValue = mdata.substring(0, mdata.length() - 1);
                            } else {
                                if(pref.equalsIgnoreCase("1")) {
                                    vDataValue = null;
                                } else
                                    vDataValue = "";
                            }
                        } else {
                            throw new DataInvalidateException("Incorrect reference mapping(" + columnHeader + ") for " + csvHeader + ".");
                        }

                    } catch(ServiceException ex) {
                        throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                    } catch (DataInvalidateException ex) {
                        throw ex;
                    } catch (Exception ex) {
                        throw new DataInvalidateException(csvHeader + " entry not found in master list for " + columnHeader + " dropdown.");
                    }
                }else {
                    vDataValue = null;
                }
           } else if(validatetype.equalsIgnoreCase("email")) {
               if(maxLength>0 && data!=null && data.length() > maxLength) {
                   throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
               }
               if(!StringUtil.isNullOrEmpty(data)) {
                    Pattern pattern = Pattern.compile(EmailRegEx);
                    if(!pattern.matcher(data).matches()){
                        throw new DataInvalidateException("Invalid email address for "+csvHeader+".");
                    }
                    vDataValue = data;
               } else {
                    vDataValue = null;
               }
           } else if(validatetype.equalsIgnoreCase("boolean")) {
               if(data.equalsIgnoreCase("true") || data.equalsIgnoreCase("1") || data.equalsIgnoreCase("T")) {
                   vDataValue = true;
               } else if(data.equalsIgnoreCase("false") || data.equalsIgnoreCase("0") || data.equalsIgnoreCase("F")) {
                   vDataValue = false;
               } else {
                   throw new DataInvalidateException("Incorrect boolean value for "+csvHeader+".");
               }
           }

           if(vDataValue==null && columnConfig.has("isNotNull") && columnConfig.getBoolean("isNotNull")) {
                throw new DataInvalidateException("Empty data found in "+csvHeader+", Can not set empty data for "+columnHeader+".");
           } else {
               if(customflag) {
                   JSONObject jobj = new JSONObject();
                   if(columnConfig.getString("xtype").equals("4") || columnConfig.getString("xtype").equals("7")) {//Drop down & Multi Select Drop down
                        try {
                            if(vDataValue!=null) {
                                if (!StringUtil.isNullOrEmpty(vDataValue.toString())) {
                                    HashMap<String, Object> comborequestParams = new HashMap<String, Object>();
                                    comborequestParams = requestParams;
                                    String pref = (String) requestParams.get("masterPreference"); //0:Skip Record, 1:Skip Column, 2:Add new
                                    KwlReturnObject result = null;
                                    if (columnConfig.getString("xtype").equals("4")) {
                                        comborequestParams.put("filtergetColumn_names", Arrays.asList("fieldid", "value"));
                                        comborequestParams.put("filter_values", Arrays.asList(columnConfig.getString("pojoHeader"), vDataValue.toString()));

                                        List lst = getCustomComboID(requestParams, vDataValue.toString(), columnConfig.getString("pojoHeader"),"id", importDao);
//                                        List lst = result.getEntityList();
                                        if (lst.size() == 0) {
                                            if(pref.equalsIgnoreCase("0")){ //Skip Record
                                                throw new DataInvalidateException(csvHeader+" entry not found in the list for "+ columnHeader +" dropdown."); // Throw ex to skip record.
                                            } else if(pref.equalsIgnoreCase("1")) {
                                                vDataValue = null;  // Put 'null' value to skip column data.
                                            }
                                        } else  {
//                                                FieldComboData tmpcontyp = (FieldComboData) ite.next();
                                            vDataValue = lst.get(0).toString();
                                        }
                                    } else if (columnConfig.getString("xtype").equals("7")) {
                                        String[] multiData = vDataValue.toString().split(Constants.Custom_Column_Sep);
                                        String mdata = "";
                                        for (int i = 0; i < multiData.length; i++) {

                                            // if for master  multiselect data item and else for custom multiselect
                                            if (columnConfig.has("refModule") && columnConfig.has("refDataColumn") && columnConfig.has("refFetchColumn") && columnConfig.has("configid")) {
                                                List list = getRefData(requestParams, columnConfig.getString("refModule"), columnConfig.getString("refDataColumn"), columnConfig.getString("refFetchColumn"), columnConfig.getString("configid"), multiData[i], importDao);
                                                mdata += list.get(0).toString() + Constants.Custom_Column_Sep;
                                            } else {
                                                comborequestParams.put("filter_names", Arrays.asList("fieldid", "value"));
                                                comborequestParams.put("filter_values", Arrays.asList(columnConfig.getString("pojoHeader"), multiData[i]));

                                                List lst = getCustomComboID(requestParams, multiData[i], columnConfig.getString("pojoHeader"),"id", importDao);
//                                                List lst = result.getEntityList();
                                                if (lst.size() == 0) {
                                                    if(pref.equalsIgnoreCase("0")){ //Skip Record
                                                        throw new DataInvalidateException(csvHeader+" entry not found in the list for "+ columnHeader +" dropdown."); // Throw ex to skip record.
                                                    } else if(pref.equalsIgnoreCase("1")) {
                                                        vDataValue = null;  // Put 'null' value to skip column data.
                                                    }
                                                } else {
//                                                FieldComboData tmpcontyp = (FieldComboData) ite.next();
                                                    mdata += lst.get(0).toString() + Constants.Custom_Column_Sep;
                                                }
                                            }
                                        }
                                        if (mdata.length() > 0) {
                                            vDataValue = mdata.substring(0, mdata.length() - 1);
                                        }
                                    }
                                }
                            }/* else {
                                throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                            }*/
                        } catch(DataInvalidateException ex) {
                            throw ex;
                        } catch(Exception ex) {
                            throw new DataInvalidateException(csvHeader+" entry not found in master list for "+ columnHeader +" dropdown.");
                        }
                   } else if(columnConfig.getString("xtype").equals("8")) {//Reference Drop down
                        try {
                            if(vDataValue!=null) {
                                if(!StringUtil.isNullOrEmpty(vDataValue.toString())) {
                                    String pref = (String) requestParams.get("masterPreference"); //0:Skip Record, 1:Skip Column, 2:Add new
                                    if(columnConfig.has("refModule") && columnConfig.has("refDataColumn") && columnConfig.has("refFetchColumn") && columnConfig.has("configid")) {
                                        List list = getRefData(requestParams, columnConfig.getString("refModule"), columnConfig.getString("refDataColumn"), columnConfig.getString("refFetchColumn"), columnConfig.getString("configid"), vDataValue, importDao);
                                        if(list.size()==0) {
                                            if(pref.equalsIgnoreCase("0")){ //Skip Record
                                                throw new DataInvalidateException(csvHeader+" entry not found in the list for "+ columnHeader +" dropdown."); // Throw ex to skip record.
                                            } else if(pref.equalsIgnoreCase("1")) {
                                                vDataValue = null;  // Put 'null' value to skip column data.
                                            } else if(pref.equalsIgnoreCase("2")) { //2:Add new
                                                if(columnConfig.getString("refModule").equalsIgnoreCase(Constants.Crm_users_pojo)) {//For users custom column.
                                                    throw new DataInvalidateException(csvHeader+" entry not found in the list for "+ columnHeader +" dropdown."); // Throw ex to skip record.
                                                }
                                            }
                                        } else {
                                            vDataValue = list.get(0).toString();
                                        }
                                    } else {
                                        throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                                    }
                                }
                            }/* else {
                                throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                            }*/
                        } catch(ServiceException ex) {
                            throw new DataInvalidateException("Incorrect reference mapping("+columnHeader+") for "+csvHeader+".");
                        } catch(DataInvalidateException ex) {
                            throw ex;
                        } catch(Exception ex) {
                            throw new DataInvalidateException(csvHeader+" entry not found in master list for "+ columnHeader +" dropdown.");
                        }
                   } else {
                        if(!validatetype.equalsIgnoreCase("date") && maxLength>0 && data!=null && data.length() > maxLength) {
                            throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
                   }
                   }
                   createCustomColumnJSON(jobj, columnConfig, vDataValue);

                   customfield.put(jobj);
                   if(dataMap.containsKey(column)) {
                       dataMap.remove(column);
                   }
               } else {
                   if(validatetype.equalsIgnoreCase("string") && maxLength>0 && data!=null && data.length() > maxLength) {
                        throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
                   }
                   dataMap.put(column, vDataValue);
               }
           }
        } else { // If no validation type then check allow null property[SK]
            if (vDataValue == null && columnConfig.has("isNotNull") && columnConfig.getBoolean("isNotNull")) {
                throw new DataInvalidateException("Empty data found in " + csvHeader + ". Can not set empty data for " + columnHeader + ".");
            }
            if(data != null && maxLength>0 && data.length() > maxLength) {
                throw new DataInvalidateException("Data length greater than "+maxLength +" for column "+csvHeader +".");
            }
        }
    }

    private static void createCustomColumnJSON(JSONObject jobj, JSONObject columnConfig, Object vDataValue) {
        try {
            jobj.put(columnConfig.getString("pojoName"), vDataValue == null ? "" : vDataValue);
            jobj.put("refcolumn_name", Constants.Custom_Column_Prefix + columnConfig.getString("refcolumn_number"));
            jobj.put("fieldname", columnConfig.getString("columnName"));
            jobj.put(columnConfig.getString("dbcolumnname"), vDataValue == null ? "" : vDataValue);
            jobj.put(columnConfig.getString("columnName"), columnConfig.getString("dbcolumnname"));

            jobj.put("filedid", columnConfig.getString("pojoHeader"));
            jobj.put("xtype", columnConfig.getString("xtype"));
        } catch(JSONException ex) {
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	/**
	 * @param columnConfig
	 * @return
	 * @throws ParseException
	 * @throws JSONException
	 * @throws DataInvalidateException
	 */
	private static Object getDefaultValue(JSONObject columnConfig) throws ParseException, JSONException, DataInvalidateException {
        Object defaultValue = columnConfig.get("defaultValue");
        if(columnConfig.has("validatetype")) {
            String validatetype = columnConfig.getString("validatetype");
            if(validatetype.equalsIgnoreCase("integer")) {
                defaultValue = StringUtil.isNullOrEmpty(defaultValue.toString())?0:Integer.parseInt(defaultValue.toString());
            } else if(validatetype.equalsIgnoreCase("double")) {
                defaultValue = StringUtil.isNullOrEmpty(defaultValue.toString())?0.0:Double.parseDouble(defaultValue.toString());
            } else if(validatetype.equalsIgnoreCase("date")) {
                String ddateStr = defaultValue.toString();
                //DateFormat sdf = new SimpleDateFormat(ddateStr.length()>10 ? df_full : df);
                boolean customflag = columnConfig.optBoolean("customflag");
                if(ddateStr.equals("now")) {
                    defaultValue = Long.toString(System.currentTimeMillis());
                } else {
                    defaultValue = StringUtil.isNullOrEmpty(ddateStr)? null : ddateStr;
                }
                if(!customflag && defaultValue!=null){
                	defaultValue=new Date(Long.parseLong((String)defaultValue));
                }
            } else if(validatetype.equalsIgnoreCase("boolean")) {
                String data = defaultValue.toString();
                if (data.equalsIgnoreCase("true") || data.equalsIgnoreCase("1") || data.equalsIgnoreCase("T")) {
                    defaultValue = true;
                } else if (data.equalsIgnoreCase("false") || data.equalsIgnoreCase("0") || data.equalsIgnoreCase("F")) {
                    defaultValue = false;
                } else {
                    throw new DataInvalidateException("Incorrect default boolean value for "+columnConfig.getString("columnName")+".");
                }
           }
        }
        if (defaultValue == null && columnConfig.has("isNotNull") && columnConfig.getBoolean("isNotNull")) {
            throw new DataInvalidateException("Can not set default empty data for " + columnConfig.getString("columnName")+".");
        }
        return defaultValue;
    }
	
	/**
	 * @param requestParams
	 * @param table
	 * @param dataColumn
	 * @param fetchColumn
	 * @param comboConfigid
	 * @param token
	 * @param importDao
	 * @return
	 * @throws ServiceException
	 * @throws DataInvalidateException
	 */
	private static List getRefData(HashMap<String, Object> requestParams, String table, String dataColumn, String fetchColumn, String comboConfigid, Object token, ImportDAO importDao) throws ServiceException, DataInvalidateException {
        ArrayList<String> filterNames = new ArrayList<String>();
        ArrayList<Object> filterValues = new ArrayList<Object>();
        filterNames.add(dataColumn);
        filterValues.add(token);
        filterNames.add(Constants.deleteflag);
        filterValues.add(0);
        return importDao.getRefModuleData(requestParams, table, fetchColumn, comboConfigid, filterNames , filterValues);
    }
		
	/**
	 * @param requestParams
	 * @param combovalue
	 * @param fieldid
	 * @param fetchColumn
	 * @param importDao
	 * @return
	 * @throws ServiceException
	 * @throws DataInvalidateException
	 */
	private static List getCustomComboID(HashMap<String, Object> requestParams, String combovalue, String fieldid,String fetchColumn, ImportDAO importDao) throws ServiceException, DataInvalidateException {
        ArrayList filterNames = new ArrayList<String>();
        ArrayList filterValues = new ArrayList<Object>();
        filterNames.add(ImportConstants.Crm_value);
        filterValues.add(combovalue);
        filterNames.add(ImportConstants.Crm_fieldid);
        filterValues.add(fieldid);
        filterNames.add(ImportConstants.Crm_deleteflag);
        filterValues.add(0);
        return importDao.getCustomComboID(requestParams, fetchColumn, filterNames, filterValues);
    }
	
	
	/**
	 * @param request
	 * @return
	 * @throws SessionExpiredException
	 */
	public static HashMap<String, Object> getImportRequestParams(HttpServletRequest request) throws SessionExpiredException{
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("modName", request.getParameter("modName"));
        requestParams.put("moduleName", request.getParameter("moduleName"));
        requestParams.put("delimiterType", request.getParameter("delimiterType"));
        requestParams.put("filename", request.getParameter("filename"));
        requestParams.put("resjson", request.getParameter("resjson"));
        requestParams.put("sheetindex", request.getParameter("sheetindex"));
        requestParams.put("onlyfilename", request.getParameter("onlyfilename"));
        requestParams.put("dateFormat", request.getParameter("dateFormat"));
        requestParams.put("masterPreference", request.getParameter("masterPreference"));

        requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
        requestParams.put("tzdiff", sessionHandlerImpl.getTimeZoneDifference(request));
        requestParams.put("userid", sessionHandlerImpl.getUserid(request));
        requestParams.put("doAction", request.getParameter("do"));
        return requestParams;
    }
	
	/**
	 * @param requestParams
	 * @param importDao
	 * @return
	 */
	public static String addPendingImportLog(HashMap<String, Object> requestParams, ImportDAO importDao){
        String logId = null;
        try {
            //Insert Integration log
            String fileName = (String) requestParams.get("filename");
            String Module = (String) requestParams.get("modName");
            try {
                List list = importDao.getModuleObject(Module);
                Modules module = (Modules) list.get(0); //Will throw null pointer if no module entry found
                Module = module.getId();
            } catch (Exception ex) {
                throw new DataInvalidateException("Column config not available for module "+Module);
            }
            HashMap<String, Object> logDataMap = new HashMap<String, Object>();
            logDataMap.put("FileName", ImportLog.getActualFileName(fileName));
            logDataMap.put("StorageName", fileName);
            logDataMap.put("Log", "Pending");
            logDataMap.put("Type", fileName.substring(fileName.lastIndexOf(".")+1));
            logDataMap.put("Module", Module);
            logDataMap.put("ImportDate", new Date());
            logDataMap.put("User", (String) requestParams.get("userid"));
            logDataMap.put("Company", (String) requestParams.get("companyid"));
            ImportLog importlog = (ImportLog)importDao.saveImportLog(logDataMap);
            logId = importlog.getId();
        } catch (Exception ex) {
            logId = null;
        }
        return logId;
    }
	
	/**
	 * @param requestParams
	 * @param txnManager
	 * @param KwlCommonTablesDAOObj
	 * @param importDao
	 * @param fieldManagerDAOobj
	 * @return
	 */
	public static JSONObject importFileData(HashMap<String, Object> requestParams, HibernateTransactionManager txnManager, kwlCommonTablesDAO KwlCommonTablesDAOObj, ImportDAO importDao, fieldManagerDAO fieldManagerDAOobj ) {
        
		JSONObject jobj = new JSONObject();
        String msg = "";
        boolean issuccess = true;

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("import_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);
        boolean commitedEx = false;

        int total = 0, failed = 0;
        String fileName = "", tableName="", extn="";
        Modules module = null;

        try {
            String moduleID = "";
            String mode = (String) requestParams.get("modName");
            fileName = (String) requestParams.get("filename");
            String companyid = (String) requestParams.get("companyid");
            extn = fileName.substring(fileName.lastIndexOf(".")+1);
            StringBuilder failedRecords = new StringBuilder();

            String dateFormat=null, dateFormatId = (String) requestParams.get("dateFormat");
            if(extn.equalsIgnoreCase("csv") && !StringUtil.isNullOrEmpty(dateFormatId)){
                KWLDateFormat kdf = (KWLDateFormat) KwlCommonTablesDAOObj.getClassObject(KWLDateFormat.class.getName(), dateFormatId);
                dateFormat = kdf!=null ? kdf.getJavaForm() : null;
            }

            Object extraObj = requestParams.get("extraObj");
            JSONObject extraParams = (JSONObject) requestParams.get("extraParams");

            String jsondata = (String) requestParams.get("resjson");
            JSONObject rootcsvjobj = new JSONObject(jsondata);
            JSONArray mapping = rootcsvjobj.getJSONArray("root");

            String classPath="", primaryKey="", uniqueKeyMethodName="", uniqueKeyHbmName="";
            try {
                List list = importDao.getModuleObject(mode);
                module = (Modules) list.get(0); //Will throw null pointer if no module entry found
            } catch (Exception ex) {
                throw new DataInvalidateException("Column config not available for module "+mode);
            }

            try {
                classPath = module.getPojoClassPathFull().toString();
                primaryKey = module.getPrimaryKey_MethodName().toString();
                moduleID = module.getId();
            } catch (Exception ex) {
                throw new DataInvalidateException("Please set proper properties for module "+mode);
            }
            uniqueKeyMethodName = module.getUniqueKey_MethodName();
            uniqueKeyHbmName = module.getUniqueKey_HbmName();

            JSONArray columnConfig = getModuleColumnConfig1(module.getId(), companyid, fieldManagerDAOobj,module.getModuleName(),true);
            tableName = importDao.getTableName(fileName);
            HashMap<String, Object> filterParams = new HashMap<String, Object>();
//            filterParams.put("isvalid", 1); //To fetch valid records
            KwlReturnObject kresult = importDao.getFileData(tableName, filterParams); //Fetch all valid records
            List fileDataList = kresult.getEntityList();
            Iterator itr = fileDataList.iterator();
            if(itr.hasNext()){
                Object[] fileData= (Object[])itr.next(); //Skip header row
                failedRecords.append(createCSVrecord(fileData)+"\"Error Message\"");//failedRecords.append("\"Row No.\","+createCSVrecord(fileData)+"\"Error Message\"");
            }
            int recIndex = 0;
            importDao.markRecordValidation(tableName, -1, 1, "", ""); //reset all invalidation
            int batchCounter = 0;
            Session session = txnManager.getSessionFactory().getCurrentSession();

            /*-Auto no custom column changes*/
            String customdataclasspath = "";
            int intModuleId = 0;
            if (moduleID.equals(Constants.MODULEID_LEAD)) {
                intModuleId = Constants.Crm_lead_moduleid;
                customdataclasspath = Constants.Crm_lead_custom_data_classpath;
            } else if (moduleID.equals(Constants.MODULEID_ACCOUNT)) {
                intModuleId = Constants.Crm_account_moduleid;
                customdataclasspath = Constants.Crm_account_custom_data_classpath;
            } else if (moduleID.equals(Constants.MODULEID_CONTACT)) {
                intModuleId = Constants.Crm_contact_moduleid;
                customdataclasspath = Constants.Crm_contact_custom_data_classpath;
            } else if (moduleID.equals(Constants.MODULEID_OPPORTUNITY)) {
                intModuleId = Constants.Crm_opportunity_moduleid;
                customdataclasspath = Constants.Crm_opportunity_custom_data_classpath;
            } else if (moduleID.equals(Constants.MODULEID_CASE)) {
                intModuleId = Constants.Crm_case_moduleid;
                customdataclasspath = Constants.Crm_case_custom_data_classpath;
            } else if (moduleID.equals(Constants.MODULEID_PRODUCT)) {
                intModuleId = Constants.Crm_product_moduleid;
                customdataclasspath = Constants.Crm_product_custom_data_classpath;
            }
            List autoNoFieldName = new ArrayList();
            HashMap<String, String> autoNoMap = new HashMap<String, String>();
            HashMap<String, Object> fieldrequestParams = new HashMap<String, Object>();
            fieldrequestParams.put("isexport", true);
            fieldrequestParams.put("filter_names", Arrays.asList("companyid", "moduleid", "fieldtype"));
            fieldrequestParams.put("filter_values", Arrays.asList(companyid, intModuleId, Constants.CUSTOM_FIELD_AUTONUMBER));
            KwlReturnObject AutoNoFieldMap = fieldManagerDAOobj.getFieldParams(fieldrequestParams);
            if (AutoNoFieldMap.getEntityList().size() != 0) {
                List<FieldParams> autNoList = AutoNoFieldMap.getEntityList();
                for (FieldParams obj : autNoList) {
                    String maxNo = fieldManagerDAOobj.getMaxAutoNumber(Constants.Custom_column_Prefix + obj.getColnum(), customdataclasspath, companyid, obj.getPrefix(), obj.getSuffix());
                    Integer maxNumber=Integer.parseInt(maxNo)+1;
                    autoNoMap.put(obj.getFieldname(), maxNumber.toString());
                    autoNoFieldName.add(obj.getFieldname());
                    autoNoMap.put(obj.getFieldname()+"_"+Constants.CUSTOM_FIELD_PREFIX, obj.getPrefix());
                    autoNoMap.put(obj.getFieldname()+"_"+Constants.CUSTOM_FIELD_SUFFIX, obj.getSuffix());
                }
            }
            // End
            while (itr.hasNext()) {            	
                total++;
                Object[] fileData= (Object[])itr.next();
                recIndex= (Integer) fileData[0];
                HashMap<String,Object> dataMap = new HashMap<String,Object>();
                HashMap<String,Object> columnHeaderMap = new HashMap<String,Object>();
                HashMap<String,Object> columnCSVindexMap = new HashMap<String,Object>();
                JSONArray customfield = new JSONArray();
                for (int k = 0; k < mapping.length(); k++) {
                    JSONObject mappingJson = mapping.getJSONObject(k);
                    String datakey = mappingJson.getString("columnname");
                    Object dataValue = cleanHTML(fileData[mappingJson.getInt("csvindex")+1]==null?null:String.valueOf(fileData[mappingJson.getInt("csvindex")+1])); //+1 for id column at index-0
                    dataMap.put(datakey, dataValue);
                    columnHeaderMap.put(datakey, mappingJson.getString("csvheader"));
                    columnCSVindexMap.put(datakey, mappingJson.getInt("csvindex")+1);
                }

                for (int j=0; j< extraParams.length(); j++) {
                    String datakey = (String) extraParams.names().get(j);
                    Object dataValue = extraParams.get(datakey);
                    dataMap.put(datakey, dataValue);
                }

                Object object = null;
                try {
//                    CheckUniqueRecord(requestParams, dataMap, classPath, uniqueKeyMethodName, uniqueKeyHbmName);
                    validateDataMAP2(requestParams, dataMap, columnConfig, customfield, columnHeaderMap, columnCSVindexMap, dateFormat, importDao, autoNoMap);
                    object = importDao.saveRecord(requestParams, dataMap, null, mode, classPath, primaryKey, extraObj, customfield);
                    if(batchCounter % 100 == 0){
                    	session.flush();
                    	session.clear();
                    }
                    batchCounter++;
                } catch(Exception ex) {
                    failed++;
                    String errorMsg = ex.getMessage(), invalidColumns = "";
                     try {
                        JSONObject errorLog = new JSONObject(errorMsg);
                        errorMsg = errorLog.getString("errorMsg");
                        invalidColumns = errorLog.getString("invalidColumns");
                    } catch (JSONException jex) {
                    }
                    failedRecords.append("\n"+createCSVrecord(fileData)+"\""+errorMsg+"\"");//failedRecords.append("\n"+(total)+","+createCSVrecord(fileData)+"\""+ex.getMessage()+"\"");
                    importDao.markRecordValidation(tableName, recIndex, 0, errorMsg, invalidColumns);
                }
            }

            if(failed > 0) {
                createFailureFiles(fileName, failedRecords, ".csv");
            }

            int success = total-failed;
            if(total == 0) {
                msg = "Empty file.";
            } else if(success == 0) {
                msg = "Failed to import all the records.";
            } else if(success == total) {
                msg = "All records are imported successfully.";
            } else {
                msg = "Imported "+success+" record"+(success>1?"s":"")+" successfully";
                msg += (failed==0?".":" and failed to import "+failed+" record"+(failed>1?"s":"")+".");
            }

            try {
                txnManager.commit(status);
                importDao.linkCustomData(mode);
            } catch(Exception ex) {
                commitedEx = true;
                throw ex;
            }
        } catch (Exception e) {
            if (!commitedEx) { //if exception occurs during commit then dont call rollback
                txnManager.rollback(status);
            }
            issuccess = false;
            msg = ""+e.getMessage();
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            DefaultTransactionDefinition ldef = new DefaultTransactionDefinition();
            ldef.setName("import_Tx");
            ldef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus lstatus = txnManager.getTransaction(ldef);
            boolean exCommit = false;
            try {
                //Insert Integration log

                requestParams.put("modulename", module.getModuleName());
                requestParams.put("validflag",2);
                if(!module.getModuleName().equals("Target") && !module.getModuleName().equals("Calibration"))
                   fieldManagerDAOobj.validateimportrecords(requestParams);

                HashMap<String, Object> logDataMap = new HashMap<String, Object>();
                String logId = (String) requestParams.get("logId");
                if(!StringUtil.isNullOrEmpty(logId)){
                    logDataMap.put("Id", logId);
                }
                failed = issuccess ? failed : total;
                logDataMap.put("FileName", ImportLog.getActualFileName(fileName));
                logDataMap.put("StorageName", fileName);
                logDataMap.put("Log", msg);
                logDataMap.put("Type", fileName.substring(fileName.lastIndexOf(".")+1));
                logDataMap.put("TotalRecs", total);
                logDataMap.put("Rejected", failed);
                logDataMap.put("Module", module.getId());
                logDataMap.put("ImportDate", new Date());
                logDataMap.put("User", (String) requestParams.get("userid"));
                logDataMap.put("Company", (String) requestParams.get("companyid"));
                importDao.saveImportLog(logDataMap);
                importDao.removeFileTable(tableName);//Remove table after importing all records
                try {
                    txnManager.commit(lstatus);
                } catch(Exception ex){
                    exCommit = true;
                    throw ex;
                }
            } catch (Exception ex) {
                if(!exCommit) { //if exception occurs during commit then dont call rollback
                    txnManager.rollback(lstatus);
                }
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                jobj.put("success", issuccess);
                jobj.put("msg", msg);
                jobj.put("totalrecords", total);
                jobj.put("successrecords", total-failed);
                jobj.put("failedrecords", failed);
                jobj.put("filename", ImportLog.getActualFileName(fileName));
            } catch (JSONException ex) {
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
        return jobj;
    }
	
	/**
	 * @param listArray
	 * @return
	 */
	private static String createCSVrecord(Object[] listArray) {
        String rec = "";
        for(int i=1; i<listArray.length-3; i++){    //Discard columns id at index 0 and isvalid,invalidColumns, validationlog at last 3 indexes.
            rec += "\""+(listArray[i]==null?"":listArray[i].toString())+"\",";
        }
        return rec;
    }	
	
	/**
	 * @param filename
	 * @param failedRecords
	 * @param ext
	 */
	private static void createFailureFiles(String filename, StringBuilder failedRecords, String ext) {
        String destinationDirectory;
        try {
            destinationDirectory = storageHandlerImpl.GetDocStorePath() + "importplans";
            if(StringUtil.isNullOrEmpty(ext)) {
                ext = filename.substring(filename.lastIndexOf("."));
            }
            filename = filename.substring(0,filename.lastIndexOf("."));

            java.io.FileOutputStream failurefileOut = new java.io.FileOutputStream(destinationDirectory + "/" + filename+ImportLog.failureTag+ext);
            failurefileOut.write(failedRecords.toString().getBytes());
            failurefileOut.flush();
            failurefileOut.close();
        } catch (Exception ex) {
            System.out.println("\nError file write [success/failed] " + ex);
        }
    }
	
	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static JSONObject getMappingCSVHeader(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        CsvReader csvReader = null;
        JSONObject jtemp1 = new JSONObject();
        JSONObject jobj = new JSONObject();
        JSONObject jsnobj = new JSONObject();
        String delimiterType=request.getParameter("delimiterType");
        String str = "";
        {
            FileInputStream fstream = null;
            try {
                if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0)) {

                    String fileid = UUID.randomUUID().toString();
                    fileid = fileid.replaceAll("-", ""); // To append UUID without "-" [SK]
//                    String Module = request.getParameter("type")==null?"":"_"+request.getParameter("type");
                    String f1 = uploadDocument(request, fileid);

                    if (f1.length() != 0) {
                        String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "importplans";
                        File csv = new File(destinationDirectory + "/" + f1);
                        fstream = new FileInputStream(csv);
                        csvReader = new CsvReader(new InputStreamReader(fstream),delimiterType);

                        csvReader.readHeaders();

                        int cols = csvReader.getHeaderCount();
                        for (int k = 0; k < csvReader.getHeaderCount(); k++) {
                            jtemp1 = new JSONObject();
                            if(!StringUtil.isNullOrEmpty(csvReader.getHeader(k).trim())) {
                                jtemp1.put("header", csvReader.getHeader(k));
                                jtemp1.put("index", k);
                                jobj.append("Header", jtemp1);
                            }
                        }

                        if (jobj.isNull("Header")) {
                            jsnobj.put("success", "true");

                            str = jsnobj.toString();
                        } else {
                            jobj.append("success", "true");
                            jobj.append("FileName", f1);
                            jobj.put("name", f1);
                            jobj.put("delimiterType", delimiterType);
                            jobj.put("cols", cols);
                            str = jobj.toString();
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                csvReader.close();
                fstream.close();
            }
        }
        return jobj;
    }
	
	/**
	 * @param request
	 * @param fileid
	 * @return
	 * @throws ServiceException
	 */
	private static String uploadDocument(HttpServletRequest request, String fileid) throws ServiceException {
        String result = "";
        try {
            String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "importplans";
            org.apache.commons.fileupload.DiskFileUpload fu = new org.apache.commons.fileupload.DiskFileUpload();
            org.apache.commons.fileupload.FileItem fi = null;
            org.apache.commons.fileupload.FileItem docTmpFI = null;

            List fileItems = null;
            try {
                fileItems = fu.parseRequest(request);
            } catch (FileUploadException e) {
                KrawlerLog.op.warn("Problem While Uploading file :" + e.toString());
            }

            long size = 0;
            String Ext = "";
            String fileName = null;
            boolean fileupload = false;
            java.io.File destDir = new java.io.File(destinationDirectory);
            fu.setSizeMax(-1);
            fu.setSizeThreshold(4096);
            fu.setRepositoryPath(destinationDirectory);
            java.util.HashMap arrParam = new java.util.HashMap();
            for (java.util.Iterator k = fileItems.iterator(); k.hasNext();) {
                fi = (org.apache.commons.fileupload.FileItem) k.next();
                arrParam.put(fi.getFieldName(), fi.getString("UTF-8"));
                if (!fi.isFormField()) {
                    size = fi.getSize();
                    fileName = new String(fi.getName().getBytes(), "UTF8");

                    docTmpFI = fi;
                    fileupload = true;
                }
            }

            if (fileupload) {

                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                if (fileName.contains(".")) {
                    Ext = fileName.substring(fileName.lastIndexOf("."));
                }
                if (size != 0) {
                    int startIndex= fileName.contains("\\")?(fileName.lastIndexOf("\\")+1):0;
                    fileName = fileName.substring(startIndex, fileName.lastIndexOf("."));
                    fileName = fileName.replaceAll(" ", "");
                    fileName = fileName.replaceAll("/", "");
                    result = fileName+"_"+fileid + Ext;

                    File uploadFile = new File(destinationDirectory + "/" + result);
                    docTmpFI.write(uploadFile);
//                    fildoc(fileid, fileName, fileid + Ext, AuthHandler.getUserid(request), size);

                }
            }

        }
//        catch (ConfigurationException ex) {
//            Logger.getLogger(ExportImportContacts.class.getName()).log(Level.SEVERE, null, ex);
//            throw ServiceException.FAILURE("ExportImportContacts.uploadDocument", ex);
//        }
        catch (Exception ex) {
            Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw ServiceException.FAILURE("ExportImportContacts.uploadDocument", ex);
        }
        return result;
    }
	
	/**
	 * @param filename
	 * @param delimiterType
	 * @param startindex
	 * @param importDao
	 * @return
	 * @throws ServiceException
	 */
	public static void dumpCSVFileData(String filename, String delimiterType, int startindex, ImportDAO importDao, HibernateTransactionManager txnManager) throws ServiceException {
        boolean commitedEx = false;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("import_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {        	
            CsvReader csvReader = null;
            FileInputStream fstream = null;
            String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "importplans";
            File csv = new File(destinationDirectory + "/" + filename);
            fstream = new FileInputStream(csv);
            csvReader = new CsvReader(new InputStreamReader(fstream), delimiterType);
//            csvReader.readHeaders();
            String tableName = importDao.getTableName(filename);
            Session session = txnManager.getSessionFactory().getCurrentSession();
            int flushCounter = 0;
            while (csvReader.readRecord()) {
                ArrayList<String> dataArray = new ArrayList<String>();
                for (int i = 0; i < csvReader.getColumnCount(); i++) {
                    dataArray.add(cleanHTML(csvReader.get(i)));
                }
                importDao.dumpFileRow(tableName, dataArray.toArray());
                if(flushCounter%30==0){
                	session.flush();
                	session.clear(); 
                }
                flushCounter++;
            }
            try {
                txnManager.commit(status);
            } catch(Exception ex) {
                commitedEx = true;
                throw ex;
            }
        } catch (IOException ex) {
            throw ServiceException.FAILURE("dumpCSVFileData: "+ex.getMessage(), ex);
        } catch (Exception ex) {
        	if (!commitedEx) { //if exception occurs during commit then dont call rollback
                txnManager.rollback(status);
            }
            throw ServiceException.FAILURE("dumpCSVFileData: "+ex.getMessage(), ex);
        }
    }
	
	
    /**
     * Generate the preview of the xls grid
     * @param filename
     * @param sheetNo
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject parseXLS(String filename, int sheetNo) throws FileNotFoundException, IOException, JSONException{
            JSONObject jobj=new JSONObject();
                    POIFSFileSystem fs      =
            new POIFSFileSystem(new FileInputStream(filename));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFFormulaEvaluator evaluator = new HSSFFormulaEvaluator(wb);
            HSSFSheet sheet = wb.getSheetAt(sheetNo);
            //DateFormat sdf = new SimpleDateFormat(df);

            int startRow=0;
            int maxRow=sheet.getLastRowNum();
            int maxCol=0;
            int maxSheetColCnt = 0;
            int noOfRowsDisplayforSample = 20;
            if(noOfRowsDisplayforSample > sheet.getLastRowNum()){
                noOfRowsDisplayforSample = sheet.getLastRowNum();
            }
            int firstValidRec = 0;
            JSONArray jArr=new JSONArray();
            try {
                for(int i=0;i <= noOfRowsDisplayforSample;i++) {
                    HSSFRow row = sheet.getRow(i);
                    JSONObject obj=new JSONObject();
                    JSONObject jtemp1 = new JSONObject();
                    if(row==null){
                        continue;
                    }
                    if(i!=0 && firstValidRec==0 && !jobj.has("Header")) // get first valid row which having some columns with data as a header
                        firstValidRec = i;
                        
//                    if(i==0) {
                        maxCol=row.getLastCellNum();
                        if(maxSheetColCnt < maxCol) // get max column count
                           maxSheetColCnt = maxCol;
//                    }
                    for(int cellcount=0; cellcount<maxCol; cellcount++){
                        HSSFCell cell = row.getCell(cellcount);
                        CellReference cref = new CellReference(i, cellcount);
                        String colHeader=cref.getCellRefParts()[2];
                        String val=null;

                        if(cell!=null){
                            switch(cell.getCellType()){
                                case HSSFCell.CELL_TYPE_NUMERIC: 
                                                                 if(HSSFDateUtil.isCellDateFormatted(cell)){
                                                                    val = cell.toString();//Long.toString(cell.getDateCellValue().getTime());
                                                                 }else{
                                                                	val=dfmt.format(cell.getNumericCellValue());
                                                                 }
                                                                 break;
                                case HSSFCell.CELL_TYPE_STRING: val=ImportUtil.cleanHTML(cell.getRichStringCellValue().getString()); break;
                            }
                        }

                        if(i==firstValidRec){ // List of Headers (Consider first row as Headers)
                            if(val!=null){
                                jtemp1 = new JSONObject();
                                jtemp1.put("header", val==null?"":val);
                                jtemp1.put("index", cellcount);
                                jobj.append("Header", jtemp1);
                            }
                        }
                        obj.put(colHeader,val);
                    }
//                    if(obj.length()>0){ //Don't show blank row in preview grid[SK]
                        jArr.put(obj);
//                    }
                }
            } catch(Exception ex) {
               Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            jobj.put("startrow", startRow);
            jobj.put("maxrow", maxRow);
            jobj.put("maxcol", maxSheetColCnt);
            jobj.put("index", sheetNo);
            jobj.put("data", jArr);
            jobj.put("filename", filename);

            jobj.put("msg", "XLS has been successfully uploaded");
            jobj.put("lsuccess", true);
            jobj.put("valid", true);
            return jobj;
    }
    
    /**
     * @param filename
     * @param sheetNo
     * @param startindex
     * @param importDao
     * @return
     * @throws ServiceException
     */
    public static void dumpXLSFileData(String filename, int sheetNo, int startindex, ImportDAO importDao, HibernateTransactionManager txnManager) throws ServiceException {
    	boolean commitedEx = false;
    	DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("import_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);
        Session session = txnManager.getSessionFactory().getCurrentSession();
        try {
            String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "xlsfiles";
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(destinationDirectory + "/" + filename));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(sheetNo);
            //DateFormat sdf = new SimpleDateFormat(df_full);
            int maxRow = sheet.getLastRowNum();
            int maxCol = 0;
            String tableName = importDao.getTableName(filename);
            int flushCounter = 0;
            for (int i = startindex; i <= maxRow; i++) {
                HSSFRow row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                if (i == startindex) {
                    maxCol = row.getLastCellNum();  //Column Count
                }
                ArrayList<String> dataArray = new ArrayList<String>();
                JSONObject dataObj=new JSONObject();
                for (int j = 0; j < maxCol; j++) {
                    HSSFCell cell = row.getCell(j);
                    String val = null;
                    if (cell == null) {
                        dataArray.add(val);
                        continue;
                    }
                    String colHeader = new CellReference(i, j).getCellRefParts()[2];
                    switch (cell.getCellType()) {
                        case HSSFCell.CELL_TYPE_NUMERIC:
                            if (HSSFDateUtil.isCellDateFormatted(cell)) {
                                val = Long.toString(cell.getDateCellValue().getTime());
                            }else{
                            	val = dfmt.format(cell.getNumericCellValue());
                            }
                            break;
                        case HSSFCell.CELL_TYPE_STRING:
                            val = ImportUtil.cleanHTML(cell.getRichStringCellValue().getString());
                            break;
                    }
                    dataObj.put(colHeader,val);
                    dataArray.add(val); //Collect row data
                }
                //Insert Query
                if(dataObj.length()>0){ // Empty row check (if lenght==0 then all columns are empty)
                    importDao.dumpFileRow(tableName, dataArray.toArray());
                    if(flushCounter%30==0){
                    	session.flush();
                    	session.clear(); 
                    }
                    flushCounter++;
                }
                
            }
            try {
                txnManager.commit(status);
            } catch(Exception ex) {
                commitedEx = true;
                throw ex;
            }            
        } catch (IOException ex) {
            throw ServiceException.FAILURE("dumpXLSFileData: "+ex.getMessage(), ex);
        } catch (Exception ex) {
        	if (!commitedEx) { //if exception occurs during commit then dont call rollback
                txnManager.rollback(status);
            }
            throw ServiceException.FAILURE("dumpXLSFileData: "+ex.getMessage(), ex);
        }
    }

    /**
     * Generate the preview of the xls grid
     * @param filename
     * @param sheetNo
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject parseXLSX(String filename, int sheetNo) throws FileNotFoundException, IOException, JSONException{
            JSONObject jobj=new JSONObject();
            FileInputStream fs = new FileInputStream(filename);
            XSSFWorkbook wb = new XSSFWorkbook(fs);
            XSSFSheet sheet = wb.getSheetAt(sheetNo);
            //DateFormat sdf = new SimpleDateFormat(df);

            int startRow=0;
            int maxRow=sheet.getLastRowNum();
            int maxCol=0;
            int noOfRowsDisplayforSample = 20;
            if(noOfRowsDisplayforSample > sheet.getLastRowNum()){
                noOfRowsDisplayforSample = sheet.getLastRowNum();
            }

            JSONArray jArr=new JSONArray();
            try {
                for(int i=0;i <= noOfRowsDisplayforSample;i++) {
                    XSSFRow row = sheet.getRow(i);
                    JSONObject obj=new JSONObject();
                    JSONObject jtemp1 = new JSONObject();
                    if(row==null){
                        continue;
                    }
                    if(i==0) {
                        maxCol=row.getLastCellNum();
                    }
                    for(int cellcount=0; cellcount<maxCol; cellcount++){
                        XSSFCell cell = row.getCell(cellcount);
                        CellReference cref = new CellReference(i, cellcount);
                        String colHeader=cref.getCellRefParts()[2];
                        String val=null;

                        if(cell!=null){
                            switch(cell.getCellType()){
                                case XSSFCell.CELL_TYPE_NUMERIC: 
                                                                 if(DateUtil.isCellDateFormatted(cell)){
                                                                    val = Long.toString(cell.getDateCellValue().getTime());
                                                                 }else{
                                                                	 val=dfmt.format(cell.getNumericCellValue()); 
                                                                 }
                                                                 break;
                                case XSSFCell.CELL_TYPE_STRING: val=ImportUtil.cleanHTML(cell.getRichStringCellValue().getString()); break;
                            }
                        }

                        if(i==0){ // List of Headers (Consider first row as Headers)
                            if(val!=null){
                                jtemp1 = new JSONObject();
                                jtemp1.put("header", val==null?"":val);
                                jtemp1.put("index", cellcount);
                                jobj.append("Header", jtemp1);
                            }
                        }
                        obj.put(colHeader,val);
                    }
//                    if(obj.length()>0){ //Don't show blank row in preview grid[SK]
                        jArr.put(obj);
//                    }
                }
            } catch(Exception ex) {
               Logger.getLogger(ImportHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            jobj.put("startrow", startRow);
            jobj.put("maxrow", maxRow);
            jobj.put("maxcol", maxCol);
            jobj.put("index", sheetNo);
            jobj.put("data", jArr);
            jobj.put("filename", filename);

            jobj.put("msg", "XLSX has been successfully uploaded");
            jobj.put("lsuccess", true);
            jobj.put("valid", true);
            return jobj;
    }

    /**
     * @param filename
     * @param sheetNo
     * @param startindex
     * @param importDao
     * @return
     * @throws ServiceException
     */
    public static void dumpXLSXFileData(String filename, int sheetNo, int startindex, ImportDAO importDao, HibernateTransactionManager txnManager) throws ServiceException {
    	boolean commitedEx = false;
    	DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("import_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txnManager.getTransaction(def);
        Session session = txnManager.getSessionFactory().getCurrentSession();
        try {
            String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "xlsfiles";
            FileInputStream fs = new FileInputStream(destinationDirectory + "/" + filename);
            XSSFWorkbook wb = new XSSFWorkbook(fs);
            XSSFSheet sheet = wb.getSheetAt(sheetNo);
            //DateFormat sdf = new SimpleDateFormat(df_full);
            int maxRow = sheet.getLastRowNum();
            int maxCol = 0;
            String tableName = importDao.getTableName(filename);
            int flushCounter = 0;
            for (int i = startindex; i <= maxRow; i++) {
                XSSFRow row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                if (i == startindex) {
                    maxCol = row.getLastCellNum();  //Column Count
                }
                ArrayList<String> dataArray = new ArrayList<String>();
                JSONObject dataObj=new JSONObject();
                for (int j = 0; j < maxCol; j++) {
                    XSSFCell cell = row.getCell(j);
                    String val = null;
                    if (cell == null) {
                        dataArray.add(val);
                        continue;
                    }
                    String colHeader = new CellReference(i, j).getCellRefParts()[2];
                    switch (cell.getCellType()) {
                        case XSSFCell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                val = Long.toString(cell.getDateCellValue().getTime());
                            } else {
                                val = dfmt.format(cell.getNumericCellValue());
                            }
                            break;
                        case XSSFCell.CELL_TYPE_STRING:
                            val = ImportUtil.cleanHTML(cell.getRichStringCellValue().getString());
                            break;
                    }
                    dataObj.put(colHeader,val);
                    dataArray.add(val); //Collect row data
                }
                //Insert Query
                if(dataObj.length()>0){ // Empty row check (if lenght==0 then all columns are empty)
                    importDao.dumpFileRow(tableName, dataArray.toArray());
                    if(flushCounter%30==0){
                    	session.flush();
                    	session.clear();
                    }
                    flushCounter++;
                }

            }
            try {
                txnManager.commit(status);
            } catch(Exception ex) {
                commitedEx = true;
                throw ex;
            }
        } catch (IOException ex) {
            throw ServiceException.FAILURE("dumpXLSXFileData: "+ex.getMessage(), ex);
        } catch (Exception ex) {
        	if (!commitedEx) { //if exception occurs during commit then dont call rollback
                txnManager.rollback(status);
            }
            throw ServiceException.FAILURE("dumpXLSXFileData: "+ex.getMessage(), ex);
        }
    }
}
