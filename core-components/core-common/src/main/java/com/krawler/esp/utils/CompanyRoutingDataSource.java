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
package com.krawler.esp.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class CompanyRoutingDataSource extends AbstractRoutingDataSource {
    private Map<String, Object> dsLookupMap = new HashMap<String, Object>();
    private Map<Object, Object> dsLookupCompanyIdMap = new HashMap<Object, Object>();
    private Map<Object, Object> dsLookupUserIdMap = new HashMap<Object, Object>();
    private Map<Object, String> dsSingleLookupMap = new HashMap<Object, String>();
    private Map<Object, Object> targetDataSources;
    private Object defaultTargetDataSource;

    @Override
    protected Object determineCurrentLookupKey() {
        if (CompanyContextHolder.getCompanySubdomain() != null) {
            return dsLookupMap.get(CompanyContextHolder.getCompanySubdomain());
        } else if (CompanyContextHolder.getCompanyID() != null) {
            return dsLookupCompanyIdMap.get(CompanyContextHolder.getCompanyID());
        } else if (CompanyContextHolder.getUserID() != null) {
            return dsLookupCompanyIdMap.get(dsLookupUserIdMap.get(CompanyContextHolder.getUserID()));
        }
        return dsLookupMap.get(CompanyContextHolder.getCompanySubdomain());
    }

    @Override
    public void setTargetDataSources(Map targetDataSources) {
        this.targetDataSources = targetDataSources;
        updateLookup();
        super.setTargetDataSources(targetDataSources);
    }

    @Override
    public void setDefaultTargetDataSource(Object defaultTargetDataSource) {
        this.defaultTargetDataSource = defaultTargetDataSource;
        updateLookupForDefaultDB();
        super.setDefaultTargetDataSource(defaultTargetDataSource);
    }

    public Map<Object, Object> getTargetDataSources() {
        return targetDataSources;
    }

    public Collection<String> getOneCompanyPerDataSource() {
        return dsSingleLookupMap.values();
    }

    public Map<Object, String> getDSSingleLookupMap() {
        return dsSingleLookupMap;
    }

    public DataSource getDataSourceFromKey(Object key) {
        return resolveSpecifiedDataSource(key);
    }

    public DataSource getDefaultDataSource() {
        return resolveSpecifiedDataSource(this.defaultTargetDataSource);
    }

    public synchronized void updateLookup() {
        if (targetDataSources != null) {
            dsLookupMap.clear();
            dsLookupCompanyIdMap.clear();
            dsLookupUserIdMap.clear();
            dsSingleLookupMap.clear();
            for (Object entry : targetDataSources.entrySet()) {
                Map.Entry<Object, Object> e = (Map.Entry<Object, Object>) entry;
                fillLookup(e.getKey(), resolveSpecifiedDataSource(e.getValue()));
            }
        }
    }

    // add for default datasource
    public synchronized void updateLookupForDefaultDB() {
        fillLookup("", resolveSpecifiedDataSource(defaultTargetDataSource));
    }

    private void fillLookup(Object key, DataSource ds) {
        try {
            JdbcTemplate template = new JdbcTemplate(ds);
            List<Map<String, Object>> list = template.queryForList("select subdomain,companyid from company where deleteflag=0");
            if (list != null) {
                for (Map map : list) {
                    String subdomain = map.get("subdomain").toString();
                    dsLookupMap.put(subdomain, key);
                    dsLookupCompanyIdMap.put(map.get("companyid"), key);
                }
                if (!list.isEmpty()) {
                    dsSingleLookupMap.put(key, list.get(0).get("subdomain").toString());
                }
                list = template.queryForList("select userid,company from users where deleteflag=0");
                if (list != null) {
                    for (Map map : list) {
                        dsLookupUserIdMap.put(map.get("userid"), map.get("company"));
                    }
                }
            }
        } catch (DataAccessException e) {
            logger.warn("Can not load companies for Datasource [" + key + "]", e);
        }
    }
}
