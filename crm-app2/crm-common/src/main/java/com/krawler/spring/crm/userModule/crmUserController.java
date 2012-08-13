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
package com.krawler.spring.crm.userModule;
import com.krawler.common.admin.User;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.dbhandler.crmManagerCommon;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.List;

public class crmUserController extends MultiActionController {
    private crmUserDAO crmUserDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;

    public void setcrmUserDAO(crmUserDAO crmUserDAOObj1) {
        this.crmUserDAOObj = crmUserDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public String getSuccessView() {
		return successView;
	}

	public void setSuccessView(String successView) {
		this.successView = successView;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public crmUserDAO getcrmUserDAO(){
        return crmUserDAOObj;
    }
     
    public ModelAndView getOwner(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       KwlReturnObject kmsg = null;
       try{
            String userid = sessionHandlerImpl.getUserid(request);
            String module ="";
            boolean allUsers = false;
            if(!StringUtil.isNullOrEmpty(request.getParameter("allUsers"))) {
                allUsers = Boolean.parseBoolean(request.getParameter("allUsers"));
            }
            boolean noneFlag = false;
            if(!StringUtil.isNullOrEmpty(request.getParameter("noneFlag"))) {
                noneFlag = Boolean.parseBoolean(request.getParameter("noneFlag"));
            }
            StringBuffer usersList = null;
            if(!allUsers) {
                boolean heirarchyPerm = false;
                if(!StringUtil.isNullOrEmpty(request.getParameter("module")))
                    heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, request.getParameter("module"));
                if(!heirarchyPerm) {
                    usersList = sessionHandlerImpl.getRecursiveUsersList(request);
                }
            }
            
            JSONArray jarr = new JSONArray();
            if(noneFlag) {//For case assigned to user combo.
                JSONObject tmpOb = crmManagerCommon.insertNone();
                jarr.put(tmpOb);
            }
            int dl = 0;
            kmsg = crmUserDAOObj.getOwner(sessionHandlerImpl.getCompanyid(request), userid, usersList);
            dl = kmsg.getRecordTotalCount();
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                User obj = (User) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("id", obj.getUserID());
                tmpObj.put("name", obj.getFirstName() + "  " + obj.getLastName());
                jarr.put(tmpObj);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", dl);
       } catch(Exception e) {
          System.out.println(e.getMessage());
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getSubOrdinateUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
       JSONObject jobj = new JSONObject();
       KwlReturnObject kmsg = null;
       try{
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            JSONArray jarr = new JSONArray();
            int dl = 0;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));

            kmsg = crmUserDAOObj.getSubOrdinateUsers(requestParams,usersList);
            dl = kmsg.getRecordTotalCount();
            List <User> list = kmsg.getEntityList();
            if(list !=null && !list.isEmpty()){
                for(User obj : list) {
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("userid", obj.getUserID());
                    tmpObj.put("fullname", StringUtil.getFullName(obj.getFirstName(), obj.getLastName()));
                    tmpObj.put("username", obj.getUserLogin().getUserName());
                    jarr.put(tmpObj);
                }
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("count", dl);
       } catch(Exception e) {
          System.out.println(e.getMessage());
       }
       return new ModelAndView("jsonView", "model", jobj.toString());
    }
}
