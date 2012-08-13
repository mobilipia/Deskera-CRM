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

import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.krawler.model.AuditFeature;
import com.krawler.model.AuditLogger;
import com.krawler.model.AuditReportHitsPerModuleDTO;
import com.krawler.model.AuditReportModuleUsageMapper;

/**
 * @author Johnson
 *
 */
public class AuditLoggerDAO extends BaseDAO implements IAuditLoggerDAO {
    
    private static final String HIT_COUNT_QUERY = "SELECT am.NAME as moduleName, COUNT(al.id) as hitCount " +
    		"FROM audit_module am " +
    		"LEFT JOIN audit_feature af ON am.id=af.a_module " +
    		"LEFT JOIN audit_log al ON af.id=al.a_feature " +
    		"GROUP BY am.NAME " +
    		"order by hitCount desc";
    
    private static final String APP_USAGE_QUERY = "SELECT am.NAME as moduleName, al.log_time, al.session_id  " +
    		"FROM audit_module am LEFT JOIN audit_feature af ON am.id=af.a_module " +
    		"LEFT JOIN audit_log al ON af.id=al.a_feature " +
    		"where al.log_time is not null " +
    		"order by am.id, al.log_time desc";

	/* (non-Javadoc)
	 * @see com.krawler.crm.dao.IAuditLoggerDAO#saveAuditLog(com.krawler.spring.interceptors.helpers.AuditLogger)
	 */
	public String save(AuditLogger auditLogger) {
		return super.save(auditLogger).toString();
	}

    @Override
    public Collection<AuditLogger> fetch(int start, int limit, String sortField) {
        String query = "select * from AuditLogger";
        if(sortField!=null&&sortField.length()>0)
            query+=" order by `"+sortField+"`";
        return executeQueryPaging(query,  new Integer[]{start, limit});
    }

    @Override
    public AuditFeature getAuditFeature(String pathInfo) {
        String query = "from AuditFeature where controllerTarget=?";
        List<AuditFeature> l=executeQuery(query, pathInfo);
        if(!l.isEmpty()){
            return l.get(0);
        }
        
        return null;
    }
    
    /* (non-Javadoc)
     * @see com.krawler.dao.IAuditLoggerDAO#getHitCountReport()
     */
    @Override
    public List<AuditReportHitsPerModuleDTO> getHitCountReportData(){
        return queryJDBC(HIT_COUNT_QUERY, null, new BeanPropertyRowMapper<AuditReportHitsPerModuleDTO>(AuditReportHitsPerModuleDTO.class));        
    }
    
    /* (non-Javadoc)
     * @see com.krawler.dao.IAuditLoggerDAO#getUsageReportData()
     */
    public List<AuditReportModuleUsageMapper> getUsageReportData(){
        return queryJDBC(APP_USAGE_QUERY, null, new BeanPropertyRowMapper<AuditReportModuleUsageMapper>(AuditReportModuleUsageMapper.class));
    }
}
