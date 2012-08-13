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
import com.krawler.common.admin.ProjectFeature;
import com.krawler.crm.hrmsintegration.bizservice.GoalManagementService;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import java.io.IOException;
import java.text.ParseException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.utils.Constants;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.esp.web.resource.Links;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class hrmsIntController extends MultiActionController  {
    private static SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
    private crmManagerDAO crmManagerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private profileHandlerDAO profileHandlerDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private permissionHandlerDAO permissionHandlerDAOObj;

    private hrmsIntDAO hrmsIntDAOObj;
    private crmLeadDAO crmLeadDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private GoalManagementService goalManagementService;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public void setpermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
    }
    
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }
    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setCrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }

    public void setGoalManagementService(GoalManagementService goalManagementServiceObj) {
        this.goalManagementService = goalManagementServiceObj;
    }
   
    public void sethrmsIntDAO(hrmsIntDAO hrmsIntDAOObj1) {
        this.hrmsIntDAOObj = hrmsIntDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public ModelAndView employeesGoalFinal(HttpServletRequest request, HttpServletResponse response) throws ServiceException, SessionExpiredException, ParseException {
        JSONObject jobj = new JSONObject();
        String start="" ;
        String limit="";
        String ss="";
        String field="";
        String direction="";
        Long fromDate = null;
        Long toDate = null;
        boolean viewAll = false;
        try {
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff=sessionHandlerImpl.getTimeZoneDifference(request);
            if (request.getParameter("start") != null) {
                start = request.getParameter("start");
                limit = request.getParameter("limit");
            }
            if (request.getParameter("viewAll") != null) {
                viewAll = Boolean.parseBoolean(request.getParameter("viewAll"));
            }
            String companyid = sessionHandlerImpl.getCompanyid(request);

            String userid = request.getParameter("userid");
            if (!StringUtil.isNullOrEmpty(request.getParameter("to")) && request.getParameter("to") != null && !StringUtil.isNullOrEmpty(request.getParameter("frm")) && request.getParameter("frm") != null) {
                fromDate = Long.parseLong(request.getParameter("frm"));
                toDate =  Long.parseLong(request.getParameter("to"));
            }
            if (!StringUtil.isNullOrEmpty(request.getParameter("to")) && request.getParameter("to") != null){
            	toDate =  Long.parseLong(request.getParameter("to"));
            }
            jobj = goalManagementService.employeesGoal(userid, viewAll,start,limit,ss,field,direction,fromDate,toDate,timeFormatId,companyid,timeZoneDiff);

        }  catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in employeesGoalFinal()", e);
        } catch (JSONException e) {
            logger.warn("JSONException exception in employeesGoalFinal()", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in employeesGoalFinal()", e);
        } catch (Exception e) {
            logger.warn("Exception exception in employeesGoalFinal()", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView loginEmployeeGoals(HttpServletRequest request, HttpServletResponse responce) throws ServiceException, ParseException {
        JSONObject jobj = new JSONObject();
        boolean viewAll = false;
        String start="" ;
        String limit="";
        String ss="";
        String field="";
        Long fromDate = null;
        Long toDate = null;
        String direction="";
        String relatedto="";
        
        try {
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String companyid= sessionHandlerImpl.getCompanyid(request);
            String timeZoneDiff=sessionHandlerImpl.getTimeZoneDifference(request);
            User user = sessionHandlerImpl.getUser(request);
            if (request.getParameter("viewAll") != null) {
                viewAll = Boolean.parseBoolean(request.getParameter("viewAll"));
            }

            if (!StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                ss=request.getParameter("ss");
            }
            if (!StringUtil.isNullOrEmpty(request.getParameter("start"))) {
                start = request.getParameter("start");
                limit = request.getParameter("limit");
            }
            if(!StringUtil.isNullOrEmpty(request.getParameter("sort"))) {
                field = request.getParameter("sort");
                direction = request.getParameter("dir");
            }
            if (!StringUtil.isNullOrEmpty(request.getParameter("to")) && request.getParameter("to") != null && !StringUtil.isNullOrEmpty(request.getParameter("frm")) && request.getParameter("frm") != null) {
                fromDate = Long.parseLong(request.getParameter("frm"));
                toDate = Long.parseLong(request.getParameter("to"));
            }
            if (!StringUtil.isNullOrEmpty(request.getParameter("to")) && request.getParameter("to") != null){
            	toDate = Long.parseLong(request.getParameter("to"));     	
            }
            
            if(!StringUtil.isNullOrEmpty(request.getParameter("relatedto"))){
            	relatedto=request.getParameter("relatedto");
            }

            jobj = goalManagementService.loginEmployeeGoals( user, viewAll, start, limit, ss, relatedto,field, direction, fromDate, toDate, timeFormatId, companyid,timeZoneDiff);
            
        } catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in loginEmployeeGoals()", e);
        } catch (JSONException e) {
            logger.warn("JSONException exception in loginEmployeeGoals()", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in loginEmployeeGoals()", e);
        } catch (Exception e) {
            logger.warn("Exception exception in loginEmployeeGoals()", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView insertGoal(HttpServletRequest request, HttpServletResponse responce)
            throws ServletException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try{
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            JSONArray jarr = new JSONArray();
            String archive = "";
            DateFormat fmt = authHandler.getDateFormatter(timeFormatId);
            String userid =sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String archiveids[] = request.getParameterValues("archiveid");
            if (StringUtil.isNullOrEmpty(request.getParameter("archive"))) {
                String jsondata = request.getParameter("jsondata");
                jarr = new JSONArray("[" + jsondata + "]");
            }
            //String hrmsURL = this.getServletContext().getInitParameter("hrmsURL");
            String hrmsURL = ConfigReader.getinstance().get("hrmsURL");
            boolean companyNotifyFlag = sessionHandlerImpl.getCompanyNotifyOnFlag(request);
            String loginURL = URLUtil.getRequestPageURL(request, Links.loginpageFull);
            String partnerName = sessionHandlerImplObj.getPartnerName(request);
            kmsg = goalManagementService.insertGoal(companyid,userid,hrmsURL,jarr,archive,archiveids,fmt,companyNotifyFlag,loginURL,partnerName);
            myjobj.put("success", kmsg.isSuccessFlag());
            myjobj.put("msg", kmsg.getMsg());
            txnManager.commit(status);
        } catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in insertGoal()", e);
            txnManager.rollback(status);
        } catch (JSONException e) {
            logger.warn("JSONException exception in insertGoal()", e);
            txnManager.rollback(status);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in insertGoal()", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

    public ModelAndView deleteAssignedGoals(HttpServletRequest request, HttpServletResponse responce) throws ServiceException {
        JSONObject myjobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String userid =sessionHandlerImpl.getUserid(request);
            String companyid = sessionHandlerImpl.getCompanyid(request);
            requestParams.put("userid", userid);
            requestParams.put("companyid", companyid);
            String[] ids = request.getParameterValues("ids");
            requestParams.put("ids", ids);
            //String hrmsURL = this.getServletContext().getInitParameter("hrmsURL");
            String hrmsURL = ConfigReader.getinstance().get("hrmsURL");
            requestParams.put("hrmsURL", hrmsURL);
            kmsg = goalManagementService.assignedgoalsdelete(userid,companyid,hrmsURL,ids);
            myjobj.put("success", kmsg.isSuccessFlag());
            myjobj.put("msg", kmsg.getMsg());
            txnManager.commit(status);

        } catch (Exception e) {
            logger.warn("General exception in deleteAssignedGoals()", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", myjobj.toString());
    }

   public ModelAndView showGoalHistory(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
        JSONObject jobj = new JSONObject();
        String view = "jsonView";
        try {
            boolean isexport = false;
            String goalid= request.getParameter("goalid");
            String companyid= sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            DateFormat dateFormat = authHandler.getNewDateFormatter(request);
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");

            jobj = goalManagementService.getGoalHistoryJSON(isexport,goalid,companyid,userid,dateFormat,start,limit);

        } catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in exportGoalHistory()", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

   public ModelAndView exportGoalHistory(HttpServletRequest request, HttpServletResponse response) throws ServiceException {
        
        JSONObject jobj = new JSONObject();
        String view = "jsonView";
        try {
            boolean isexport = true;
            String goalid= request.getParameter("goalid");
            String companyid= sessionHandlerImpl.getCompanyid(request);
            String userid = sessionHandlerImpl.getUserid(request);
            DateFormat dateFormat = authHandler.getNewDateFormatter(request);
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            String fileType = request.getParameter("filetype");
            
            jobj = goalManagementService.getGoalHistoryJSON(isexport,goalid,companyid,userid,dateFormat,start,limit);
            
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);
 
        } catch (IOException ex) {
            logger.warn("IOException exception in exportGoalHistory()", ex);
        } catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in exportGoalHistory()", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in exportGoalHistory()", e);
        } 
        return new ModelAndView(view, "model", jobj.toString());
    }

 public ModelAndView completedGoalReport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, SessionExpiredException {
        JSONObject jobj = new JSONObject();
        String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
        String view="jsonView";
        Long fromDate = null;
        Long toDate = null;
        String timeZoneDiff=sessionHandlerImpl.getTimeZoneDifference(request);
        try {
            DateFormat dateFormat = authHandler.getDateFormatter(timeFormatId);
            String userid = sessionHandlerImpl.getUserid(request);
            if(request.getParameter("userCombo")!=null){
                userid = request.getParameter("userCombo");
            }
            int relatedTo = 0;
            if(request.getParameter("filterCombo")!=null){
               relatedTo = Integer.parseInt(request.getParameter("filterCombo"));
            }
            String companyid = sessionHandlerImpl.getCompanyid(request);
            int leadPerm = 0;
            int accountPerm = 0;
            int oppPerm = 0;
            if(relatedTo==0){ // Kuldeep Singh : When user select view 'All' goals then fetch goal data according to module's permission
                leadPerm = sessionHandlerImpl.getPerms(request, ProjectFeature.leadFName);
                accountPerm = sessionHandlerImpl.getPerms(request, ProjectFeature.accountFName);
                oppPerm = sessionHandlerImpl.getPerms(request, ProjectFeature.opportunityFName);
            }
            if (!StringUtil.isNullOrEmpty(request.getParameter("to")) && request.getParameter("to") != null && !StringUtil.isNullOrEmpty(request.getParameter("frm")) && request.getParameter("frm") != null) {
                fromDate = Long.parseLong(request.getParameter("frm"));
                toDate =  Long.parseLong(request.getParameter("to"));
            }
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            String searchStr = request.getParameter("ss");

            jobj = goalManagementService.completedGoalReport(userid, companyid, dateFormat, relatedTo, leadPerm, accountPerm, oppPerm, fromDate, toDate, searchStr, start, limit, false,RequestContextUtils.getLocale(request),timeZoneDiff);

        } catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in completedGoalReport()", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in completedGoalReport()", e);
        } catch (Exception e) {
            logger.warn("General exception in completedGoalReport()", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView completedGoalReportExport(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        String view="jsonView";
        Long fromDate = null;
        Long toDate = null;
        try {
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            String userid = sessionHandlerImpl.getUserid(request);
            String timeZoneDiff=sessionHandlerImpl.getTimeZoneDifference(request);
            if(request.getParameter("userCombo")!=null){
                userid = request.getParameter("userCombo");
            }
            int relatedTo = 0;
            if(request.getParameter("filterCombo")!=null){
               relatedTo = Integer.parseInt(request.getParameter("filterCombo"));
            }
            String companyid = sessionHandlerImpl.getCompanyid(request);
            int leadPerm = 0;
            int accountPerm = 0;
            int oppPerm = 0;
            if(relatedTo==0){ // Kuldeep Singh : When user select view 'All' goals then fetch goal data according to module's permission
                leadPerm = sessionHandlerImpl.getPerms(request, ProjectFeature.leadFName);
                accountPerm = sessionHandlerImpl.getPerms(request, ProjectFeature.accountFName);
                oppPerm = sessionHandlerImpl.getPerms(request, ProjectFeature.opportunityFName);
            }
            if (!StringUtil.isNullOrEmpty(request.getParameter("to")) && request.getParameter("to") != null && !StringUtil.isNullOrEmpty(request.getParameter("frm")) && request.getParameter("frm") != null) {
                fromDate = Long.parseLong(request.getParameter("frm"));
                toDate =  Long.parseLong(request.getParameter("to"));
            }
            String start = request.getParameter("start");
            String limit = request.getParameter("limit");
            String searchStr = request.getParameter("ss");

            jobj = goalManagementService.completedGoalReport(userid, companyid, dateFormat, relatedTo, leadPerm, accountPerm, oppPerm, fromDate, toDate, searchStr, start, limit, true,RequestContextUtils.getLocale(request),timeZoneDiff);

            String fileType = request.getParameter("filetype");
            if (StringUtil.equal(fileType, "print")) {
                view = "jsonView-empty";
            }
            exportDAOImplObj.processRequest(request, response, jobj);

        } catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in completedGoalReport()", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in completedGoalReport()", e);
        } catch (Exception e) {
            logger.warn("General exception in completedGoalReport()", e);
        }
        return new ModelAndView(view, "model", jobj.toString());
    }

    public ModelAndView completedGoalPieChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        try {
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            result = goalManagementService.completedGoalPieChart(usersList);

        } catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in completedGoalPieChart()", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in completedGoalPieChart()", e);
        } catch (Exception e) {
            logger.warn("General exception in completedGoalPieChart()", e);
        }
        return new ModelAndView("chartView", "model", result);
    }
    public ModelAndView completedGoalBarChart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String result = "";
        try {
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            result = goalManagementService.completedGoalBarChart(usersList);

        } catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in completedGoalBarChart()", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in completedGoalBarChart()", e);
        } catch (Exception e) {
            logger.warn("General exception in completedGoalBarChart()", e);
        }
        return new ModelAndView("chartView", "model", result);
    }

      public ModelAndView insertGoalBlackBerry(HttpServletRequest request, HttpServletResponse responce)
            throws ServletException, SessionExpiredException {
        String successMsg = "Goal created successfully.";
        boolean successFlag = true;
        HashMap model = new HashMap();
        KwlReturnObject kmsg = null;
        //Create transaction
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
       try{
            model.put("successFlag", "1");
            model.put("redirectFlag", "0");
            JSONArray jarr = new JSONArray();
            String archive = "";
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String userid = request.getParameter("userid");//sessionHandlerImpl.getUserid(request);
            String companyid = request.getParameter("companyid");//sessionHandlerImpl.getCompanyid(request);
            String archiveids[] = new String[0];
            String employeeid = request.getParameter("employee");
            String target = request.getParameter("target");
            if(!target.matches(Constants.numberRegex)) {
                successMsg = "Problem while submitting your form. Invalid data for the Target field. ";
                successFlag = false;
            }
            String relatedto = request.getParameter("goaltype");
            String fromdate = request.getParameter("fromdate");//Apr 20, 2011 12:00:00 AM
            try{
                sdf.parse(fromdate);
            }catch(Exception e) {
                successMsg = "Problem while submitting your form. Invalid data for the Start Date field. ";
                successFlag = false;
            }
            String todate = request.getParameter("todate");
            try{
                sdf.parse(todate);
            }catch(Exception e) {
                successMsg = "Problem while submitting your form. Invalid data for the To Date field. ";
                successFlag = false;
            }
            String creationdate = request.getParameter("creationdate");
            try{
                sdf.parse(creationdate);
            }catch(Exception e) {
                successMsg = "Problem while submitting your form. Invalid data for the Goal Creation Date field. ";
                successFlag = false;
            }
            if(!successFlag) {
                model.put("successMsg", successMsg);
                model.put("successFlag", 0);
                txnManager.rollback(status);
                return new ModelAndView("goalsuccess", "model", model);
            }
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            DateFormat fmt = authHandler.getDateFormatter(timeFormatId);
            fromdate = fmt.format(sdf.parse(fromdate));
            todate = fmt.format(sdf.parse(todate));
            creationdate = fmt.format(sdf.parse(creationdate));
            if (StringUtil.isNullOrEmpty(request.getParameter("archive"))) {
                String jsondata = "    {'gname':'','gid':'undefined','empid':'"+employeeid+"','gdescription':'','gwth':'','gcontext':'','gpriority':''," +
                        "'targeted':'"+target+"','relatedto':'"+relatedto+"','gstartdate':'"+fromdate+"','genddate':'"+todate+"','gcreatedate':'"+creationdate+"','gcomment':''}";
                jarr = new JSONArray("[" + jsondata + "]");
            }
            String hrmsURL = ConfigReader.getinstance().get("hrmsURL");
            boolean companyNotifyFlag = sessionHandlerImpl.getCompanyNotifyOnFlag(request);
            String loginURL = URLUtil.getRequestPageURL(request, Links.loginpageFull);
            String partnerName = sessionHandlerImplObj.getPartnerName(request);
            kmsg = goalManagementService.insertGoal(companyid,userid,hrmsURL,jarr,archive,archiveids,fmt,companyNotifyFlag,loginURL, partnerName);
            model.put("successMsg", kmsg.getMsg());
            model.put("successFlag", kmsg.isSuccessFlag()?1:0);
            String returnurl = request.getParameter("returnurl");
            if (!StringUtil.isNullOrEmpty(returnurl)) {
                model.put("redirectFlag", "1");
                model.put("returnurl", returnurl);
            }
            txnManager.commit(status);
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage());
            txnManager.rollback(status);
        } catch (ParseException ex) {
            logger.warn(ex.getMessage());
            txnManager.rollback(status);
        } catch (JSONException e) {
            logger.warn(e.getMessage());
            txnManager.rollback(status);
        } catch (ServiceException e) {
            logger.warn(e.getMessage());
            txnManager.rollback(status);
        }
        return new ModelAndView("goalsuccess", "model", model);
    }
}
