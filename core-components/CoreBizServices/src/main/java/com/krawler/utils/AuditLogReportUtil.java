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
package com.krawler.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.krawler.model.AuditReportModuleUsageDTO;
import com.krawler.model.AuditReportModuleUsageMapper;

/**
 * @author Johnson
 *
 */
public class AuditLogReportUtil
{

    private static final Long REQUEST_TIME_LIMIT = 900000L;  //15(Minutes) * 60 * 1000
    
    public List<AuditReportModuleUsageDTO> convertMapperToDTO(List<AuditReportModuleUsageMapper> lst)
    {
        Map<String, ModuleUsageDataHolder> map = new HashMap<String, ModuleUsageDataHolder>();
        ModuleUsageDataHolder dataHolder = this.new ModuleUsageDataHolder();
        for(AuditReportModuleUsageMapper mapper: lst){            
            dataHolder.add(mapper);
        }
        return dataHolder.makeDTO();
    }
    
    class ModuleUsageDataHolder{
        
        private Map<String, Map<String, List<Long>>> map = new HashMap<String, Map<String, List<Long>>>();
        
        private void add(AuditReportModuleUsageMapper mapper){
            Map<String, List<Long>> sessionToTimeMap = map.get(mapper.getModuleName());
            if(sessionToTimeMap==null){
                sessionToTimeMap = new HashMap<String, List<Long>>();
                List<Long> timeList = new ArrayList<Long>();
                timeList.add(Long.valueOf(mapper.getLogTime()));
                sessionToTimeMap.put(mapper.getSessionId(), timeList);
                map.put(mapper.getModuleName(), sessionToTimeMap);
            }else{
                List<Long> timeList = sessionToTimeMap.get(mapper.getSessionId());
                if(timeList==null){
                    timeList = new ArrayList<Long>();
                    timeList.add(Long.valueOf(mapper.getLogTime()));
                    sessionToTimeMap.put(mapper.getSessionId(), timeList);
                }else{
                    timeList.add(Long.valueOf(mapper.getLogTime()));
                }
            }
        }

        public List<AuditReportModuleUsageDTO> makeDTO()
        {
            Set<String> set = map.keySet();
            List<AuditReportModuleUsageDTO> lst = new ArrayList<AuditReportModuleUsageDTO>();
            for(String moduleId : set){
                AuditReportModuleUsageDTO auditReportModuleUsageDTO = new AuditReportModuleUsageDTO();
                Map<String, List<Long>> sessionToTimeMap = map.get(moduleId);
                Set<String> sessionSet = sessionToTimeMap.keySet();
                Long cumulitiveTime = 0L;
                
                
                for(String sessionId : sessionSet){
                    List<Long> timeList = sessionToTimeMap.get(sessionId);
                    Long previousTime = 0L;
                    Long runningTotal = 0L;
                    for(Long time : timeList){
                        if(previousTime==0){
                            previousTime = time;
                        }
                        Long requestTime = previousTime - time;
                        runningTotal += REQUEST_TIME_LIMIT > requestTime ? requestTime : REQUEST_TIME_LIMIT;
                        previousTime = time;
                    }
                    cumulitiveTime+=runningTotal;
                }
                auditReportModuleUsageDTO.setModuleName(moduleId);
                auditReportModuleUsageDTO.setTime(cumulitiveTime.toString());
                lst.add(auditReportModuleUsageDTO);
            }
            Collections.sort(lst);
            return lst;
        }
    }

}
