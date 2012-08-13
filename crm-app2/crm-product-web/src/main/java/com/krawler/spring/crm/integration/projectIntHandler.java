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
package com.krawler.spring.crm.integration;
import com.krawler.common.admin.Company;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.AccountProject;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONException;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class projectIntHandler {
    private static final Log logger = LogFactory.getLog(projectIntHandler.class);
    public static JSONObject getAccountProjectDetails(projectIntDAO projectIntDAOObj, HttpServletRequest request, String recid, Company company) throws ServiceException {
            JSONObject jobj = new JSONObject();
            JSONArray jarr = new JSONArray();
            KwlReturnObject kmsg = null;
            try {
                //String url = request.getSession().getServletContext().getInitParameter("projectManagementUrl");
                String url = ConfigReader.getinstance().get("projectManagementURL");
                String projects="";
                String subdomain = company.getSubDomain();

                ArrayList filter_names = new ArrayList();
                ArrayList filter_params = new ArrayList();
                filter_names.add("c.accountId.accountid");
                filter_params.add(recid);

                kmsg = projectIntDAOObj.getAccountProjectDetails(filter_names, filter_params);
                Iterator ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    AccountProject ap = (AccountProject) ite.next();
                    projects+="<a href='#' onClick=onProjectLinkClick('"+url+"a/"+subdomain+"/#nickname_"+ap.getNickName()+"') >"+ap.getProjectName()+"</a>, ";

                }
                if(!StringUtil.isNullOrEmpty(projects)){
                    projects = projects.substring(0,projects.length()-2);
                    JSONObject temp = new JSONObject();
                    temp.put("projectnames",projects);
                    jarr.put(temp);
                }
                jobj.put("projList", jarr);
                jobj.put("addProjectPerm", crmManagerCommon.hasCreateProjPerm(request));
            } catch (JSONException e) {
                logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE(e.getMessage(), e);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE(e.getMessage(), e);
            }
            return jobj;
     }
}
