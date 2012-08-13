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
package com.krawler.dao;

import com.krawler.model.AuditFeature;
import com.krawler.model.AuditReportHitsPerModuleDTO;
import com.krawler.model.AuditReportModuleUsageMapper;

import java.util.Collection;
import java.util.List;

import com.krawler.model.AuditLogger;

/**
 * @author Johnson
 *
 */
public interface IAuditLoggerDAO {

	/**
	 * Persists the provided AuditLog
	 * @param auditLogger
	 * @return String id of the saved Log
	 */
	String save(AuditLogger auditLogger);
	
	/**
	 * Persists all the auditLoggers provided in the collection.
	 * @param auditLoggers
	 */
	void saveAll(Collection auditLoggers);

    public Collection<AuditLogger> fetch(int start, int limit, String sortField);

    public AuditFeature getAuditFeature(String pathInfo);
    
    /**
     * @return List of HitsPerModuleReportDTO
     */
    List<AuditReportHitsPerModuleDTO> getHitCountReportData();
    
    /**
     * @return List of AuditReportModuleUsageMapper
     */
    List<AuditReportModuleUsageMapper> getUsageReportData();
}
