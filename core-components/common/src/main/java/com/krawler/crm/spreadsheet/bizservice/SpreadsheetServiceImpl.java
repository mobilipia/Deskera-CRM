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
package com.krawler.crm.spreadsheet.bizservice;

import java.util.UUID;

import com.krawler.common.admin.SpreadSheetConfig;
import com.krawler.common.service.ServiceException;
import com.krawler.spring.crm.spreadSheet.spreadSheetDAO;
import com.krawler.utils.json.base.JSONObject;

/**
 * @author krawler
 *
 */
public class SpreadsheetServiceImpl implements SpreadsheetService
{
    
    private spreadSheetDAO spreadsheetDAO;

    /* (non-Javadoc)
     * @see com.krawler.crm.spreadsheet.bizservice.SpreadsheetService#getSpreadsheetConfig(java.lang.String, java.lang.String)
     */
    @Override
    public SpreadSheetConfig getSpreadsheetConfig(String module, String userId) throws ServiceException
    {
        return spreadsheetDAO.getSpreadsheetConfig(module, userId);
    }
    

    @Override
    public SpreadSheetConfig saveSpreadsheetConfig(String module, String cid, String userId, String rule, String state) throws ServiceException
    {
    	SpreadSheetConfig config = spreadsheetDAO.getSpreadsheetConfig(module, userId);
    	if(config==null){
    		config = new SpreadSheetConfig();
    		config.setCid(UUID.randomUUID().toString());
    		config.setModule(module);
    	}
    	
    	config.setUpdatedOn(System.currentTimeMillis());
    	
    	if(rule!=null)
    		config.setRules(rule);
    	if(state!=null)
    		config.setState(state);
    	spreadsheetDAO.saveSpreadsheetConfig(config, userId);
        return config;
    }

    /**
     * @return the spreadsheetDAO
     */
    public spreadSheetDAO getSpreadsheetDAO()
    {
        return spreadsheetDAO;
    }

    /**
     * @param spreadsheetDAO the spreadsheetDAO to set
     */
    public void setSpreadsheetDAO(spreadSheetDAO spreadsheetDAO)
    {
        this.spreadsheetDAO = spreadsheetDAO;
    }

	@Override
	public void saveModuleRecordStyle(String id, String classname, String cellstyle) throws ServiceException {
		spreadsheetDAO.saveModuleRecordStyle(id, classname, cellstyle);
		
	}

}
