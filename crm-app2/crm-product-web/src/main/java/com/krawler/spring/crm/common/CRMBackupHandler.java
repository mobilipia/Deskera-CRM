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

import java.util.logging.Level;
import java.util.logging.Logger;
import com.krawler.crm.common.bizservice.CRMBackupService;
import java.util.Map;

/**
 *
 * @author sagar
 */
public class CRMBackupHandler implements Runnable {

    boolean isWorking = false;
    private Map<String, Object> requestParams = null;
    private StringBuffer usersList = null;

    private CRMBackupService crmBackupService;

    /**
     *
     * @return crmBackupService
     */
    public CRMBackupService getCRMBackupService()
    {
        return crmBackupService;
    }

    /**
     *
     * @param crmBackupService
     */
    public void setcrmBackupService(CRMBackupService crmBackupService)
    {
        this.crmBackupService = crmBackupService;
    }

    @Override
    public void run() {
        if (requestParams != null && !requestParams.isEmpty()) {
			try {
				this.isWorking = true;
                getCRMBackupService().backupData(requestParams, usersList);

			} catch (Exception ex) {
				Logger.getLogger(CRMBackupHandler.class.getName()).log(
						Level.SEVERE, null, ex);
			} finally {
			}
		}
    }

    /**
	 * @param requestParams
	 */
	public void setRequestParams(Map<String, Object> requestParams) {
		this.requestParams = requestParams;
	}

    /**
     * @return the usersList
     */
    public StringBuffer getUsersList()
    {
        return usersList;
    }

    /**
     * @param usersList the usersList to set
     */
    public void setUsersList(StringBuffer usersList)
    {
        this.usersList = usersList;
    }

}
