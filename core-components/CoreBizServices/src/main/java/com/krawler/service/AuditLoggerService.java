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
package com.krawler.service;

import java.util.Collection;
import java.util.List;

import com.krawler.dao.IAuditLoggerDAO;
import com.krawler.model.AuditFeature;
import com.krawler.model.AuditLogger;
import com.krawler.model.AuditReportHitsPerModuleDTO;
import com.krawler.model.AuditReportModuleUsageDTO;
import com.krawler.model.AuditReportModuleUsageMapper;
import com.krawler.utils.AuditLogReportUtil;

/**
 * @author krawler
 * 
 */
public class AuditLoggerService implements IAuditLoggerService {

	private IAuditLoggerDAO auditLoggerDAO;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.krawler.crm.service.IAuditLoggerService#saveAuditLog(com.krawler.
	 * spring.interceptors.helpers.AuditLogger)
	 */
	public String saveAuditLog(AuditLogger auditLogger) {
		String logId = null;
		try {
			logId = auditLoggerDAO.save(auditLogger);
		} catch (Exception e) {
			// TODO log the stack
			e.printStackTrace();
		}
		return logId;
	}

	/* (non-Javadoc)
	 * @see com.krawler.crm.service.IAuditLoggerService#saveAll(java.util.Collection)
	 */
	public void saveAll(Collection auditLoggers) {
		try {
			auditLoggerDAO.saveAll(auditLoggers);
		} catch (Exception e) {
			// TODO log the stack
			e.printStackTrace();
		}
	}

	public void setAuditLoggerDAO(IAuditLoggerDAO auditLoggerDAO) {
		this.auditLoggerDAO = auditLoggerDAO;
	}

    @Override
    public Collection<AuditLogger> fetch(int start, int limit, String sortField) {
        return auditLoggerDAO.fetch(start, limit, sortField);
    }

    @Override
    public AuditFeature getAuditFeature(String pathInfo) {
        return auditLoggerDAO.getAuditFeature(pathInfo);
    }
    
    /* (non-Javadoc)
     * @see com.krawler.service.IAuditLoggerService#getHitCountReport()
     */
    @Override
    public List<AuditReportHitsPerModuleDTO> getHitCountReportData(){
        return auditLoggerDAO.getHitCountReportData();
    }
    
    public List<AuditReportModuleUsageDTO> getUsageReportData(){
        List<AuditReportModuleUsageMapper> lst = auditLoggerDAO.getUsageReportData();
        AuditLogReportUtil auditReportUtil = new AuditLogReportUtil();        
        return auditReportUtil.convertMapperToDTO(lst);
    }

    
}
