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
package com.krawler.formbuilder.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.krawler.common.service.ServiceException;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.io.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.utils.json.base.JSONArray;
import org.springframework.web.context.request.RequestAttributes;

public class AccessRightController extends MultiActionController {
    private AccessRightDao accessRightDao;
//    private permissionHandlerDAO permissionHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

//    public void setPermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
//        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
//    }

    public void setAccessRightDao(AccessRightDao accessRightDao) {
        this.accessRightDao = accessRightDao;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
   public ModelAndView accessRight(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            String result = "";
            boolean isFormSubmit=false;
            JSONObject jobj = new JSONObject();
            try {
                int action = Integer.parseInt(request.getParameter("action"));
                switch (action) {
                    case 0 :
                        result = accessRightDao.fetchAllRoleAuthData(request.getParameter("groupid"),request);
                        break;
                    case 1 :
                        result = accessRightDao.fetchrolegrdata(request);
                        break;
                        /*result = "{\"data\":[{\"description\":\"12\",\"del\":\"\",\"groupname\":\"Trainee/Trainee Related\",\"groupid\":\"1\",\"copy\":\"\"}"+

                        ",{\"description\":\"2\",\"del\":\"\",\"groupname\":\"Trainer/Trainer Ops\",\"groupid\":\"2\",\"copy\":\"\"},{\"description\""+

                        ":\"4\",\"del\":\"\",\"groupname\":\"Administration (Operations)\",\"groupid\":\"4\",\"copy\":\"\"},{\"description\":\"5\",\"del\""+

                        ":\"\",\"groupname\":\"Technical Services\",\"groupid\":\"5\",\"copy\":\"\"}]}";*/
                    case 2 :
                        String groupName = request.getParameter("groupname");
                        String groupDesc = " ";
                        if(request.getParameter("groupdesc")!=null && request.getParameter("groupdesc").compareTo("") !=0){
                            groupDesc = request.getParameter("groupdesc");
                        }
                        accessRightDao.addrolegr(groupName, groupDesc);
                        jobj.put("success","true");
                        result = jobj.toString();
                        break;
                    case 3 :
                        JSONArray jsonArray = new JSONArray(request.getParameter("deletedgroupid"));
                        JSONObject gridJObj = new JSONObject();
                        for (int k = 0; k < jsonArray.length(); k++) {
                            gridJObj = jsonArray.getJSONObject(k);
                            accessRightDao.deleteRoleGroup(gridJObj.getString("groupid"));
                        }
                        break;
                    case 4 :
                        accessRightDao.updaterolegr(request.getParameter("groupid"),request.getParameter("data"), request.getParameter("column"));
                        break;
                    case 5 :
                        result = accessRightDao.fetchroledata();
                        break;
                    case 6 :
                        accessRightDao.addrole(request.getParameter("rolename"),request.getParameter("roledesc"),request.getParameter("groupid"));
                        jobj.put("success","true");
                        result = jobj.toString();
                        break;
                    case 7 :
                        jsonArray = new JSONArray(request.getParameter("deletedroleid"));
                        JSONObject roleidJObj = new JSONObject();
                        for (int k = 0; k < jsonArray.length(); k++) {
                            roleidJObj = jsonArray.getJSONObject(k);
                            accessRightDao.deleteRole(roleidJObj.getString("roleid"));
                        }
                        break;
                    case 8 :
                           accessRightDao.updaterole(request.getParameter("roleid"),request.getParameter("rolename"),request.getParameter("roledesc"),request.getParameter("groupid"));
                           break;
                    case 9 :
                        result = accessRightDao.fetchSingleRoleGrpData(request.getParameter("grpid"));
                        break;
                    case 10 :
                        result = accessRightDao.fetchPermGrpData(request);
                        break;
                    case 11 :
                        result = accessRightDao.fetchSinglePermGrpData(request.getParameter("permid"));
                        break;
                    case 12 :
                        accessRightDao.addPermGrp(request.getParameter("groupname"));
                        jobj.put("success", "true");
                        result = jobj.toString();
                        break;
                    case 13 :
                        accessRightDao.addPerm(request.getParameter("permname"), request.getParameter("groupid"));
                        jobj.put("success", "true");
                        result = jobj.toString();
                        break;
                    case 15 :
                        accessRightDao.updatePerm(request.getParameter("permid"), request.getParameter("permname"), request.getParameter("groupid"));
                        break;
                    case 16 :
                        result = accessRightDao.deleteUPG(request.getParameter("groupid"));
                        break;
                    case 17 :
                        String json = request.getParameter("json");
                        JSONArray jStr;
                        if(com.krawler.common.util.StringUtil.isNullOrEmpty(json)) {
                            jStr = new JSONArray();
                        } else {
                            jStr = new JSONArray(json);
                        }

                        String gid = request.getParameter("gid");
                        String gname = request.getParameter("gname");
                        String gdesc = request.getParameter("gdesc");
                        result = accessRightDao.copyUPG(gid, gname, gdesc, jStr);
                        break;
                    case 18 :
                        result = accessRightDao.fetchSingleRoleGrpDataCopy(request.getParameter("grpid"));
                        break;
                    case 19 :
                        result = accessRightDao.fetchroledata(request);
                        break;
                    case 22://"addpermval":
                       try{
                        //JSONObject JObj = new JSONObject(request.getParameter("data"));
                        /*JSONArray jsonArray = new JSONArray(request.getParameter("data"));
                        JSONObject JObj = new JSONObject();
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JObj = jsonArray.getJSONObject(k);
                            accessRightDao.insertPermVal(JObj.getString("permgrid"),JObj.getString("roleid"),JObj.getString("permvalview"),JObj.getString("pervaledit"),request);
                        }*/
                        accessRightDao.insertPermVal(request);
                        result = "{\"success\" :true}";
                       }
                       catch(Exception e) {
                           logger.warn(e.getMessage(), e);
                       }
                       break;
                    case 20 :
                        result = accessRightDao.getAllRoles();
                        break;
                    case 21 :
                        result = accessRightDao.fetchGridColumns(request.getParameter("groupid"),request);
                        break;
                    case 23 :                        
                        String userid = request.getParameter("userid");
                        jobj.put("roleperms", accessRightDao.getMBRolePermisionSet(userid));
                        jobj.put("realroles",accessRightDao.getMBRealRoleIds(userid));
                        request.getSession().setAttribute("roleperms", jobj.optString("roleperms"));
                        request.getSession().setAttribute("realroles", jobj.optString("realroles"));
                        result = jobj.toString();
                        break;
                }
            } catch (NumberFormatException e) {
                logger.warn(e.getMessage(), e);
            } catch (ServiceException ex) {
                logger.warn(ex.getMessage(), ex);
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
            }
            if(isFormSubmit){
                return new ModelAndView("jsonView-ex", "model", result);
            }
            return new ModelAndView("jsonView", "model", result);
    }
}
