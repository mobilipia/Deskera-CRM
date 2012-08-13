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

import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.orm.hibernate3.HibernateTransactionManager;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.utils.json.base.JSONObject;
/**
 *
 * @author krawler
 */
public class ImportHandler implements Runnable {
	
    private String TimeRegEx = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";   

    private HibernateTransactionManager txnManager;
    
    private ImportDAO importDao;
    
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;

    private fieldManagerDAO fieldManagerDAOobj;
    
    boolean isWorking = false;
    
    private HashMap<String, Object> requestParams = null;
    
    private static String[] masterTables = {"MasterItem"};
    
    private Log logger = LogFactory.getLog(ImportHandler.class);
    
   
    @Override
	public void run() {
		if (requestParams != null && !requestParams.isEmpty()) {
			try {
				this.isWorking = true;
				String modulename = requestParams.get("modName").toString();

				JSONObject jobj = importFileData(requestParams);
				// if(jobj.has("success") && jobj.getBoolean("success")) {
				User user = (User) KwlCommonTablesDAOObj.getClassObject(
						"com.krawler.common.admin.User", requestParams.get(
								"userid").toString());
				String htmltxt = "Report for data imported.<br/>";
				htmltxt += "<br/>Module Name: " + modulename + "<br/>";
				htmltxt += "<br/>File Name: " + jobj.get("filename") + "<br/>";
				htmltxt += "Total Records: " + jobj.get("totalrecords")
						+ "<br/>";
				htmltxt += "Records Imported Successfully: "
						+ jobj.get("successrecords");
				htmltxt += "<br/>Failed Records: " + jobj.get("failedrecords");
				htmltxt += "<br/><br/>Please check the import log in the system for more details.";
				htmltxt += "<br/>For queries, email us at support@deskera.com<br/>";
				htmltxt += "Deskera Team";

				String plainMsg = "Report for data imported.\n";
				plainMsg += "\nModule Name: " + modulename + "\n";
				plainMsg += "\nFile Name:" + jobj.get("filename") + "\n";
				plainMsg += "Total Records: " + jobj.get("totalrecords");
				// if transaction commit failed, then successrecords = 0
				plainMsg += "\nRecords Imported Successfully: "
						+ jobj.get("successrecords");
				plainMsg += "\nFailed Records: " + jobj.get("failedrecords");
				plainMsg += "\n\nPlease check the import log in the system for more details.";

				plainMsg += "\nFor queries, email us at support@deskera.com\n";
				plainMsg += "Deskera Team";

				SendMailHandler.postMail(new String[] { user.getEmailID() },
						"Deskera CRM - Report for data imported", htmltxt,
						plainMsg, "Deskera Admin<admin@deskera.com>");
				// }
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			} finally {
			}
		}
	}

    /**
     * @param requestParams
     * @return
     */
    public JSONObject validateFileData(HashMap<String, Object> requestParams) {
    	return ImportUtil.validateFileData(requestParams, txnManager, KwlCommonTablesDAOObj, importDao, fieldManagerDAOobj);
    }
    
    
    /**
     * @param requestParams
     * @return
     */
    public JSONObject importFileData(HashMap<String, Object> requestParams) {
    	logger.info("Import start : "+System.currentTimeMillis());
		JSONObject obj = ImportUtil.importFileData(requestParams, txnManager, KwlCommonTablesDAOObj, importDao, fieldManagerDAOobj);
		logger.info("Import end : "+System.currentTimeMillis());
    	return obj;
	}
    
    /**
     * @param filename
     * @param sheetNo
     * @param startindex
     * @param importDao
     * @return
     * @throws ServiceException
     */
    public void dumpXLSFileData(String filename, int sheetNo, int startindex, ImportDAO importDao) throws ServiceException {
    	ImportUtil.dumpXLSFileData(filename, sheetNo, startindex, importDao, txnManager);
    }

    public void dumpXLSXFileData(String filename, int sheetNo, int startindex, ImportDAO importDao) throws ServiceException {
    	ImportUtil.dumpXLSXFileData(filename, sheetNo, startindex, importDao, txnManager);
    }

    public void dumpCSVFileData(String filename, String delimiterType, int startindex, ImportDAO importDao) throws ServiceException {
    	ImportUtil.dumpCSVFileData(filename, delimiterType, startindex, importDao, txnManager);
    }
    /**
     * @param KwlCommonTablesDAOObj1
     */
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    /**
     * @param fieldManagerDAOobj
     */
    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }
    /**
     * @param txManager
     */
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    /**
     * @param importDao
     */
    public void setimportDAO(ImportDAO importDao) {
        this.importDao = importDao;
    }
    
    /**
     * @param isWorking
     */
    public void setIsWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

    /**
     * @return
     */
    public boolean isIsWorking() {
        return isWorking;
    }
    
    /**
     * @param module
     * @return
     */
    public static boolean isMasterTable(String module){
        boolean isMasterModule = false;
        for(int i=0; i<masterTables.length; i++){
            if(module.equalsIgnoreCase(masterTables[0])){
                isMasterModule = true;
                break;
            }
        }
        return isMasterModule;
    }
    
    /**
     * @param masterModules
     */
    public void setmasterTables(String[] masterModules) {
        masterTables = masterModules;
    }

	/**
	 * @param requestParams
	 */
	public void setRequestParams(HashMap<String, Object> requestParams) {
		this.requestParams = requestParams;
	}
}
