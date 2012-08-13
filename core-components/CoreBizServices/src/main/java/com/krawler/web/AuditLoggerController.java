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

package com.krawler.web;

import com.krawler.model.AuditFeature;
import com.krawler.model.AuditLogger;
import com.krawler.model.AuditModule;
import com.krawler.service.IAuditLoggerService;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author krawler-user
 */
public class AuditLoggerController extends MultiActionController {
    private IAuditLoggerService auditLoggerService;

    public void setAuditLoggerService(IAuditLoggerService auditLoggerService) {
        this.auditLoggerService = auditLoggerService;
    }

    public ModelAndView fetchLog(HttpServletRequest request, HttpServletResponse response) {
        JSONObject result=new JSONObject();
        
        try {
            int start = Integer.parseInt(request.getParameter("start"));
            int limit = Integer.parseInt(request.getParameter("limit"));
            
            Collection<AuditLogger> list =  auditLoggerService.fetch(start, limit, request.getParameter("sortfield"));
            JSONArray jArr = new JSONArray();
            for (AuditLogger auditLogger : list) {
                AuditFeature feature = auditLogger.getAuditFeature();
                AuditModule module = feature.getAuditModule();
                JSONObject obj = new JSONObject();
                obj.put("id", auditLogger.getId());
                obj.put("userid", auditLogger.getUserid());
                obj.put("username", auditLogger.getUserName());
                obj.put("ipaddress", auditLogger.getIpAddress());
                obj.put("sessionid", auditLogger.getSessionId());
                obj.put("featureid", feature.getId());
                obj.put("featurename", feature.getName());
                obj.put("moduleid", module.getId());
                obj.put("modulename", module.getName());
                jArr.put(obj);
            }
            
            result.put("data", jArr);
        }catch(JSONException ex){
            logger.warn("Cannot create Json", ex);
        }

        return new ModelAndView("jsonView", "model", result.toString());
    }
}
