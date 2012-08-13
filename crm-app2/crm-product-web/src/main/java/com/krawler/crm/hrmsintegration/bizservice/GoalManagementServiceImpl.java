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
package com.krawler.crm.hrmsintegration.bizservice;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.User;
import com.krawler.common.notification.bizservice.NotificationManagementService;
import com.krawler.common.notification.web.NotificationConstants;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.common.utils.DateUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.Finalgoalmanagement;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.integration.hrmsIntDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.utils.json.base.JSONException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import com.krawler.crm.utils.Constants;
import com.krawler.esp.handlers.APICallHandlerService;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountHandler;
import com.krawler.spring.profileHandler.profileHandler;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;


import static com.krawler.common.notification.web.NotificationConstants.*;
/**
 *
 * @author krawler
 */
public class GoalManagementServiceImpl implements GoalManagementService,MessageSourceAware {

    private static SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd");
    private crmLeadDAO crmLeadDAOObj;
    private crmAccountDAO crmAccountDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private hrmsIntDAO hrmsIntDAOObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private static final Log logger = LogFactory.getLog(GoalManagementServiceImpl.class);
    private APICallHandlerService apiCallHandlerService;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private NotificationManagementService NotificationManagementServiceDAO;
    private MessageSource messageSource;

	public NotificationManagementService getNotificationManagementServiceDAO() {
        return NotificationManagementServiceDAO;
    }

    public void setNotificationManagementServiceDAO(NotificationManagementService NotificationManagementServiceDAO) {
        this.NotificationManagementServiceDAO = NotificationManagementServiceDAO;
    }
    
    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

    public void setApiCallHandlerService(APICallHandlerService apiCallHandlerService)
    {
        this.apiCallHandlerService = apiCallHandlerService;
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

    public void sethrmsIntDAO(hrmsIntDAO hrmsIntDAOObj1) {
        this.hrmsIntDAOObj = hrmsIntDAOObj1;
    }
    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }
    public KwlReturnObject getActiveFinalGoals(String userid, boolean viewAll, String start, String limit, String ss,String relatedto,String field, String direction, Long fromDate, Long toDate) throws ServiceException, JSONException, ParseException, SessionExpiredException {
        KwlReturnObject kmsg = null;
        try {
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.userID.userID");
            filter_names.add("c.deleted");
            filter_params.add(userid);
            filter_params.add(false);
            HashMap requestParams = new HashMap();
            if (!StringUtil.isNullOrEmpty(ss)) {
                requestParams.put("ss", ss);
            }
            if (!StringUtil.isNullOrEmpty(start)) {
                requestParams.put("start", Integer.parseInt(start));
                requestParams.put("limit", Integer.parseInt(limit));
            }
            if (!StringUtil.isNullOrEmpty(field)) {
                requestParams.put("field", field);
            }
            if (!StringUtil.isNullOrEmpty(direction)) {
                requestParams.put("direction", direction);
            }
            if(!StringUtil.isNullOrEmpty(relatedto)){
            	filter_names.add("c.relatedto");
            	filter_params.add(Integer.parseInt(relatedto));
            }
            
            if (!viewAll) {
                if (fromDate != null && toDate != null) {//Get the goals whose start date is falling in between the selected date range.
                    filter_names.add(">=c.startdate");
                    filter_names.add("<=c.startdate");
                    filter_params.add(fromDate);
                    filter_params.add(toDate);
                } else {//Get future dated goals if date is not selected.
                    filter_names.add(">=c.enddate");
                    Date now = new Date();
                    now = dateOnlyFormat.parse(dateOnlyFormat.format(now));
                    filter_params.add(now.getTime());
                }
            }
            kmsg = hrmsIntDAOObj.getFinalGoals(requestParams, filter_names, filter_params);

        } catch (Exception e) {
            logger.warn("General exception in getActiveFinalGoals()", e);
            throw ServiceException.FAILURE("GoalManagementServiceImpl.getActiveFinalGoals", e);
        }
        return kmsg;
    }

    public double getAchievedTarget(Finalgoalmanagement fgmt, String companyid, User user) throws ServiceException {
        double achieved = 0;
        try {

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            KwlReturnObject kmsg = null;
            requestParams.put("distinctFlag", true);
            int reltTo = fgmt.getRelatedto() == null ? 0 : fgmt.getRelatedto();

            Company company = (Company) kwlCommonTablesDAOObj.getObject(Company.class.getName(), companyid);
            KWLTimeZone gmtTimeZone = (KWLTimeZone) kwlCommonTablesDAOObj.getObject(KWLTimeZone.class.getName(), "1");//GMT

            KWLTimeZone timeZone = authHandler.getTZforUser(user, company, gmtTimeZone);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZone.getDifference()));

            requestParams = getFilter(reltTo, fgmt.getStartdate(), fgmt.getEnddate(), user.getUserID(), companyid);
            filter_names = (ArrayList) requestParams.get("filter_names");
            filter_params = (ArrayList) requestParams.get("filter_params");
            switch(reltTo) {
                case 1 : // for Leads
                    kmsg = crmLeadDAOObj.getLeadOwners(requestParams, filter_names, filter_params);
                    achieved = kmsg.getRecordTotalCount();
                    break;
                case 2 :// for Lead Revenue
                    achieved = crmAccountDAOObj.getAccountRevenue(requestParams, filter_names, filter_params);
                    break;
                case 3 : // for Account
                    kmsg = crmAccountDAOObj.getAccountOwners(requestParams, filter_names, filter_params);
                    achieved = kmsg.getRecordTotalCount();
                    break;
                case 4 : // for Account Revenue
                    achieved = crmAccountDAOObj.getAccountRevenue(requestParams, filter_names, filter_params);
                    break;
                case 5 : // for Opportunity
                    requestParams.put("filter_values", filter_params);
                    kmsg = crmOpportunityDAOObj.getAllOpportunities(requestParams);
                    achieved = kmsg.getRecordTotalCount();
                    break;
                case 6 : // for Opportunity Revenue
                    achieved = crmOpportunityDAOObj.getOpportunityRevenue(filter_names, filter_params);
                    break;
            }
        } catch (Exception e) {
            logger.warn("General exception in getAchievedTarget()", e);
            throw ServiceException.FAILURE("GoalManagementServiceImpl.getAchievedTarget", e);
        }
        return achieved;
    }

    public HashMap getFilter(int relatedTo, Long startdate, Long enddate, String userid, String companyid) throws ServiceException {

        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        try {
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if (relatedTo == 1) { // No of Leads
                filter_names.add("c.deleteflag");
                filter_names.add("c.isarchive");
                filter_names.add("c.company.companyID");
                filter_names.add("c.validflag");
                filter_names.add("lo.mainOwner");
                filter_names.add("lo.usersByUserid.userID");
                filter_params.add(0);
                filter_params.add(false);
                filter_params.add(companyid);
                filter_params.add(1);
                filter_params.add(true);
                filter_params.add(userid);
                filter_names.add(">=c.createdOn");
                filter_names.add("<c.createdOn");
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(startdate), 0));
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(enddate), 1));
            } else if (relatedTo == 2) {  // Total revenue from closed leads
                requestParams.put("distinctFlag", true);

                filter_names.add("c.deleteflag");
                filter_names.add("c.isarchive");
                filter_names.add("c.company.companyID");
                filter_names.add("c.validflag");
                filter_names.add("c.crmLead.isconverted");
                filter_names.add("ao.mainOwner");
                filter_names.add("ao.usersByUserid.userID");
                filter_params.add(0);
                filter_params.add(false);
                filter_params.add(companyid);
                filter_params.add(1);
                filter_params.add("1");
                filter_params.add(true);
                filter_params.add(userid);
                filter_names.add(">=c.createdOn");
                filter_names.add("<c.createdOn");
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(startdate), 0));
                filter_params.add(DateUtil.getStrippedDateAsLong( new Date(enddate), 1));
            } else if (relatedTo == 3) {  // No of Accounts
                requestParams.put("distinctFlag", true);

                filter_names.add("c.deleteflag");
                filter_names.add("c.isarchive");
                filter_names.add("c.company.companyID");
                filter_names.add("c.validflag");
                filter_names.add("ao.mainOwner");
                filter_names.add("ao.usersByUserid.userID");
                filter_params.add(0);
                filter_params.add(false);
                filter_params.add(companyid);
                filter_params.add(1);
                filter_params.add(true);
                filter_params.add(userid);
                filter_names.add(">=c.createdOn");
                filter_names.add("<c.createdOn");
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(startdate), 0));
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(enddate), 1));
            } else if (relatedTo == 4) {  // Total revenue from Accounts
                requestParams.put("distinctFlag", true);

                filter_names.add("c.deleteflag");
                filter_names.add("c.isarchive");
                filter_names.add("c.company.companyID");
                filter_names.add("c.validflag");
                filter_names.add("ao.mainOwner");
                filter_names.add("ao.usersByUserid.userID");
                filter_params.add(0);
                filter_params.add(false);
                filter_params.add(companyid);
                filter_params.add(1);
                filter_params.add(true);
                filter_params.add(userid);
                filter_names.add(">=c.createdOn");
                filter_names.add("<c.createdOn");
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(startdate), 0));
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(enddate), 1));
            } else if (relatedTo == 5) {  // No of Opportunity
                requestParams.put("distinctFlag", true);

                filter_names.add("c.deleteflag");
                filter_names.add("c.isarchive");
                filter_names.add("c.company.companyID");
                filter_names.add("c.validflag");
                filter_names.add("oo.mainOwner");
                filter_names.add("oo.usersByUserid.userID");
                filter_params.add(0);
                filter_params.add(false);
                filter_params.add(companyid);
                filter_params.add(1);
                filter_params.add(true);
                filter_params.add(userid);
                filter_names.add(">=c.createdOn");
                filter_names.add("<c.createdOn");
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(startdate), 0));
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(enddate), 1));
            } else if (relatedTo == 6) {  // Total revenue from Opportunity
                requestParams.put("distinctFlag", true);

                filter_names.add("c.deleteflag");
                filter_names.add("c.isarchive");
                filter_names.add("c.company.companyID");
                filter_names.add("c.validflag");
                filter_names.add("oo.mainOwner");
                filter_names.add("oo.usersByUserid.userID");
                filter_params.add(0);
                filter_params.add(false);
                filter_params.add(companyid);
                filter_params.add(1);
                filter_params.add(true);
                filter_params.add(userid);
                filter_names.add(">=c.createdOn");
                filter_names.add("<c.createdOn");
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(startdate), 0));
                filter_params.add(DateUtil.getStrippedDateAsLong(new Date(enddate), 1));
            }

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
        } catch (Exception e) {
            logger.warn("General exception in getFilter()", e);
            throw ServiceException.FAILURE("GoalManagementServiceImpl.getFilter", e);
        }
        return requestParams;
    }

    public KwlReturnObject insertGoal(String companyid, String userid, String hrmsURL, JSONArray jarr, String archive, String[] archiveids, DateFormat fmt, boolean companyNotifyFlag, String loginURL, String partnerName) throws ServiceException {
        String apprmanager = "";
        int logtext = 0;
        String gid;
        List ll = new ArrayList();
        int dl = 0;
        boolean successflag = false;
        String msg = KWLErrorMsgs.F01;
        try {
            Finalgoalmanagement fgmt = null;
            if (StringUtil.isNullOrEmpty(archive)) {
                User user = profileHandlerDAOObj.getUserObject(userid);
                apprmanager = profileHandler.getUserFullName(user);

                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject jobj = jarr.getJSONObject(i);
//                    Date startdate = fmt.parse(jobj.getString("gstartdate"));
//                    Date enddate = fmt.parse(jobj.getString("genddate"));

                    User employee = profileHandlerDAOObj.getUserObject(jobj.getString("empid"));
                    int relatedTo = Integer.parseInt(jobj.getString("relatedto"));
                    HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
                    requestParams1.put("relatedTo", jobj.getString("relatedto"));
                    requestParams1.put("startdate", jobj.getLong("gstartdate"));
                    requestParams1.put("enddate", jobj.getLong("genddate"));
                    requestParams1.put("employee", employee);
                    int ispresent=0;
                    if(!jobj.has("gid")){
                     ispresent = hrmsIntDAOObj.goalsDateCheck(requestParams1);
                    }
                      if (ispresent == 0 ){
                    	
                        String id = "";
                        if (jobj.optString("gid",null)==null) {
                            logtext = 0;
                            msg = KWLErrorMsgs.hrmsGoalADD;
                        } else {
                            id = jobj.getString("gid");
                            logtext = 1;
                            msg = KWLErrorMsgs.hrmsGoalEDIT;
                        }

                       // Date createdon = fmt.parse(jobj.getString("gcreatedate"));
                        String description = jobj.getString("gdescription");
                        String goalname = jobj.getString("gname");
                        String targeted = jobj.getString("targeted");

                        HashMap<String, Object> requestParams2 = new HashMap<String, Object>();
                        requestParams2.put("id", id);
                        requestParams2.put("apprmanager", apprmanager);
                        requestParams2.put("user", user);
                        requestParams2.put("description", description);
                        requestParams2.put("goalname", goalname);
                        requestParams2.put("startdate", jobj.getLong("gstartdate"));
                        requestParams2.put("enddate", jobj.getLong("genddate"));
                        requestParams2.put("createdon", jobj.getLong("gcreatedate"));
                        requestParams2.put("employee", employee);
                        requestParams2.put("relatedTo", jobj.getString("relatedto"));
                        requestParams2.put("targeted", targeted);

                        gid = hrmsIntDAOObj.goalsInsert(requestParams2);

                        String relatedName = getGoalName(relatedTo);

                        // HRMS call
                        JSONObject userData = new JSONObject();

                        userData.put("iscommit", true);
                        userData.put("gname", "Target for " + relatedName + " between " + fmt.format(new Date(jobj.getLong("gstartdate"))) + " and " + fmt.format(new Date(jobj.getLong("genddate"))) + ": " + targeted);
                        userData.put("gdescription", description);
                        userData.put("gstartdate", jobj.getString("gstartdate"));
                        userData.put("genddate", jobj.getString("genddate"));
                        userData.put("userid", userid);//"f2ccc62a-aae3-4305-9134-64b7fd83164e"
                        userData.put("empid", employee.getUserID());//"463d289d-1fe1-4799-969b-6bc72b66c4a4"
                        userData.put("companyid", companyid);
                        userData.put("logtext", logtext);
                        userData.put("gid", gid);
                        userData.put("remoteapikey", storageHandlerImpl.GetRemoteAPIKey());
                        String action = "11";

                        JSONObject resObj = apiCallHandlerService.callApp(hrmsURL, userData, companyid, action, true);
                        successflag = true;

                       if (companyNotifyFlag && logtext==0) {// send Notification if company flag set
                           List<String> recepients = new ArrayList();
                           recepients.add(employee.getUserID());
                           Map refTypeMap = new HashMap();
                           Map refIdMap = new HashMap();
                           refIdMap.put("refid1", gid);
                           refTypeMap.put("reftype1", Constants.GOAL_MANAGEMENT_CLASSPATH);
                           refIdMap.put("refid2", user.getUserID());
                           refTypeMap.put("reftype2", Constants.USERS_CLASSPATH);
                           refIdMap.put("refid3", String.valueOf(relatedTo));
                           refTypeMap.put("reftype3", Constants.GOAL_TYPE_CLASSPATH);
                           HashMap<String, Object> extraParams = new HashMap<String, Object>();
                           extraParams.put(NotificationConstants.LOGINURL, loginURL);
                           extraParams.put(NotificationConstants.PARTNERNAME, partnerName);
                           if (recepients.size() > 0) {
                               NotificationManagementServiceDAO.sendNotificationRequest(CHANNEL.EMAIL, GOAL_ASSIGNED, NOTIFICATIONSTATUS.REQUEST, user.getUserID(), recepients, refIdMap, refTypeMap, extraParams);
                           }
                       }

                    } else {
                        successflag = false;
                        msg = KWLErrorMsgs.hrmsGoalFailure;
                    }

                }
            } else {
                for (int i = 0; i < archiveids.length; i++) {
                    hrmsIntDAOObj.goalsArchive(archiveids[i]);
                }
                successflag = true;
            }
            ll.add(fgmt);
        } catch (Exception e) {
            logger.warn("General exception in getFilter()", e);
            throw ServiceException.FAILURE("GoalManagementServiceImpl.insertGoals : " + e.getMessage(), e);
        }
        return new KwlReturnObject(successflag, msg, "", ll, dl);
    }

    public KwlReturnObject assignedgoalsdelete(String userid, String companyid, String hrmsURL, String[] ids) throws ServiceException {
        boolean successflag = false;
        String msg = KWLErrorMsgs.F01;
        try {
            if (!StringUtil.isNullOrEmpty(userid) && !StringUtil.isNullOrEmpty(companyid)) {
                for (int i = 0; i < ids.length; i++) {
                    String usrid = hrmsIntDAOObj.goalsdelete(ids[i]);
                    if(!StringUtil.isNullOrEmpty(hrmsURL)) {
                        //HRMS Call
                        JSONObject userData = new JSONObject();
                        userData.put("gid", ids[i]);
                        userData.put("empid", usrid);
                        userData.put("userid", userid);
                        userData.put("iscommit", true);
                        userData.put("remoteapikey", storageHandlerImpl.GetRemoteAPIKey());

                        String action = "12";
                        JSONObject resObj = apiCallHandlerService.callApp(hrmsURL, userData, companyid, action, true);
                    }
                }
            }
            successflag = true;
            msg = KWLErrorMsgs.S01;
        } catch (Exception ex) {
            logger.warn("General exception in assignedgoalsdelete()", ex);
            throw ServiceException.FAILURE("GoalManagementServiceImpl.assignedgoalsdelete", ex);
        }
        return new KwlReturnObject(successflag, msg, "", null, 0);

    }

    public double getPercentageTarget(Finalgoalmanagement fgmt, double dl) throws ServiceException {
        double percentageTarget = 0;
        if (fgmt.getTargeted() != 0) {
            percentageTarget = (dl / fgmt.getTargeted()) * 100;
        }
        return percentageTarget;
    }

    public String getGoalName(int reltatedto) throws ServiceException {
        String goalName = "";
        if (reltatedto == 1) { // no of Leads
            goalName = Constants.GOAL_TYPE_NO_OF_LEADS;
        } else if (reltatedto == 2) { // for Lead Revenue
            goalName = Constants.GOAL_TYPE_LEAD_REVENUE;
        } else if (reltatedto == 3) { // no of Accounts
            goalName = Constants.GOAL_TYPE_NO_OF_ACCOUNTS;
        } else if (reltatedto == 4) { // for Accounts Revenue
            goalName = Constants.GOAL_TYPE_ACCOUNT_REVENUE;
        } else if (reltatedto == 5) { // no of Opportunity
            goalName = Constants.GOAL_TYPE_NO_OF_OPPORTUNITY;
        } else if (reltatedto == 6) { // for Opportunity Revenue
            goalName = Constants.GOAL_TYPE_OPPORTUNITY_REVENUE;
        }
        return goalName;
    }

    public JSONObject completedGoalReport(String userid, String companyid, DateFormat dateFormat, int relatedTo,
            int leadPerm, int accountPerm, int oppPerm, Long fromDate, Long toDate, String searchStr, String startStr, String limitStr, boolean exportall,Locale locale,String timeZoneDiff)
            throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        JSONArray jarr = new JSONArray();
        int count = 0;        
        int start = 0;
        int limit = 25;
        int offSet=TimeZone.getTimeZone("GMT"+timeZoneDiff).getOffset(System.currentTimeMillis());
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();

            filter_names.add("c.userID.userID");
            filter_names.add("c.userID.company.companyID");
            filter_names.add("c.deleted");
            filter_params.add(userid);
            filter_params.add(companyid);
            filter_params.add(false);
            if(relatedTo>0){
                filter_names.add("c.relatedto");
                filter_params.add(relatedTo);
            } else if(relatedTo==0){ // Kuldeep Singh : When user select view 'All' goals then fetch goal data according to module's permission
                StringBuffer relatedlist = new StringBuffer();
                if ((leadPerm & 1) == 1) {
                    relatedlist.append("" + 1 + ",");
                    relatedlist.append("" + 2 + ",");
                }
                if ((accountPerm & 1) == 1) {
                    relatedlist.append("" + 3 + ",");
                    relatedlist.append("" + 4 + ",");
                }
                if ((oppPerm & 1) == 1) {
                    relatedlist.append("" + 5 + ",");
                    relatedlist.append("" + 6 + ",");
                }
                String rltedlist="0";
                if(relatedlist.length()>0){
                    rltedlist = relatedlist.substring(0, (relatedlist.length()-1));
                }
                filter_names.add("INc.relatedto");
                filter_params.add(rltedlist);
            }
                if (toDate != null && fromDate != null) {
                    Date fmDate = new Date(fromDate);
                    Date tDate = new Date(toDate);
                    filter_names.add(">=c.startdate");
                    filter_names.add("<=c.startdate");
                    filter_params.add(dateOnlyFormat.parse(dateOnlyFormat.format(fmDate)).getTime());
                    filter_params.add(dateOnlyFormat.parse(dateOnlyFormat.format(tDate)).getTime());
                }

                filter_names.add("<=c.enddate");
                Date now = new Date();
                now = dateOnlyFormat.parse(dateOnlyFormat.format(now));
                filter_params.add(now.getTime());

                requestParams.clear();
                if (startStr != null) {
                    start = Integer.parseInt(startStr);
                    limit = Integer.parseInt(limitStr);
                }
                if(!StringUtil.isNullOrEmpty(searchStr)){
                    requestParams.put("ss", searchStr);
                }
                if(!exportall) {
                    requestParams.put("start", start);
                    requestParams.put("limit", limit);
                }
                kmsg = hrmsIntDAOObj.getFinalGoals(requestParams, filter_names, filter_params);
                count = kmsg.getRecordTotalCount();
                if (count > 0) {
                    User empuser = profileHandlerDAOObj.getUserObject(userid);
                    List<Finalgoalmanagement> finalGoalmanagementList = kmsg.getEntityList();
                    for(Finalgoalmanagement fgmt : finalGoalmanagementList) {
                        JSONObject tmpObj = new JSONObject();
                        double dl = 0;
                        String relatedName="";
                        double percentageTarget=0;
                        filter_names.clear();
                        filter_params.clear();
                        requestParams.clear();
                        requestParams.put("distinctFlag", true);

                        dl = getAchievedTarget( fgmt, companyid, empuser );
                        percentageTarget = getPercentageTarget(fgmt, dl);
                        int reltTo = fgmt.getRelatedto() == null ? 0 : fgmt.getRelatedto();
                        relatedName = getGoalName(reltTo);
                        String gid = fgmt.getId();
                        int pastGoals = getPastGoal(fgmt ,percentageTarget);
                        DecimalFormat decimalFormat = new DecimalFormat("#0");
                        String percentAchvd = decimalFormat.format(percentageTarget)+" %";

                        tmpObj.put("gid", gid);
                        tmpObj.put("gname", fgmt.getGoalname());
                        tmpObj.put("empname", empuser.getFirstName() + " " + empuser.getLastName());
                        tmpObj.put("empid", empuser.getUserID());
                        tmpObj.put("gdescription", fgmt.getGoaldesc());
                        tmpObj.put("targeted", fgmt.getTargeted());
                        tmpObj.put("relatedto", reltTo);
                        tmpObj.put("relatedName", relatedName);
                        tmpObj.put("achieved", dl!=0.0?dl:"0");
                        tmpObj.put("percentageachieved", percentageTarget!=0.0?percentAchvd:"0");
                        tmpObj.put("pastgoals", pastGoals);
                        tmpObj.put("gstartdate", dateFormat.format(fgmt.getStartdate()+offSet));
                        tmpObj.put("genddate", dateFormat.format(fgmt.getEnddate()+offSet));
                        tmpObj.put("gassignedby", fgmt.getManager().getFirstName() + " " + (fgmt.getManager().getLastName() == null ? "" : fgmt.getManager().getLastName()));
                        jarr.put(tmpObj);
                    }
                }
                jobj.put("coldata", jarr);
                jobj.put("data", jarr);
                jobj.put("totalCount", count);

                JSONObject jobjTemp = new JSONObject();
                JSONArray jarrColumns = new JSONArray();
                JSONArray jarrRecords = new JSONArray();
                JSONObject jMeta = new JSONObject();

                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.goalsettings.header.empname", null, locale));//"Employee Name");
                jobjTemp.put("tip", messageSource.getMessage("crm.goalsettings.header.empname", null, locale));//"Employee Name");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "empname");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.goals.header.goaltype", null, locale));//"Goal Type");
                jobjTemp.put("tip", messageSource.getMessage("crm.goals.header.goaltype", null, locale));//"Goal Type");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "relatedName");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.goals.header.target", null, locale));//"Target");
                jobjTemp.put("tip", messageSource.getMessage("crm.goals.header.target", null, locale));//"Target");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("align", "right");
                jobjTemp.put("dataIndex", "targeted");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.goals.header.achieved", null, locale));//"Achieved");
                jobjTemp.put("tip", messageSource.getMessage("crm.goals.header.achieved", null, locale));//"Achieved");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("align", "right");
                jobjTemp.put("dataIndex", "achieved");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.goals.header.percentageachieved", null, locale));//"Percentage Achieved");
                jobjTemp.put("tip", messageSource.getMessage("crm.goals.header.percentageachieved", null, locale));//"Percentage Achieved");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("align", "right");
                jobjTemp.put("dataIndex", "percentageachieved");
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.goals.header.fromdate", null, locale));//"From Date");
                jobjTemp.put("tip", messageSource.getMessage("crm.goals.header.fromdate", null, locale));//"From Date");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("title", "gstartdate");
                jobjTemp.put("dataIndex", "gstartdate");
                jobjTemp.put("align", "center");
                jobjTemp.put("renderer", crmManagerCommon.dateRendererReport());
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.goals.header.todate", null, locale));//"To Date");
                jobjTemp.put("tip", messageSource.getMessage("crm.goals.header.todate", null, locale));//"To Date");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("title", "gstartdate");
                jobjTemp.put("dataIndex", "genddate");
                jobjTemp.put("align", "center");
                jobjTemp.put("renderer", crmManagerCommon.dateRendererReport());
                jarrColumns.put(jobjTemp);

                jobjTemp = new JSONObject();
                jobjTemp.put("header", messageSource.getMessage("crm.goals.header.assignedby", null, locale));//"Assigned By");
                jobjTemp.put("tip", messageSource.getMessage("crm.goals.header.assignedby", null, locale));//"Assigned By");
                jobjTemp.put("pdfwidth", 60);
                jobjTemp.put("dataIndex", "gassignedby");
                jarrColumns.put(jobjTemp);


                jobjTemp = new JSONObject();
                jobjTemp.put("name", "empname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "relatedName");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "targeted");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "achieved");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "percentageachieved");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "gassignedby");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "gstartdate");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "genddate");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);
                jobj.put("columns", jarrColumns);

                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                jMeta.put("id", "asd");
                jobj.put("metaData", jMeta);

        } catch (JSONException e) {
            logger.warn("JSONException exception in completedGoalReport()", e);
            throw ServiceException.FAILURE("GoalManagementServiceImpl.assignedgoalsdelete", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in completedGoalReport()", e);
            throw ServiceException.FAILURE("GoalManagementServiceImpl.assignedgoalsdelete", e);
        } catch (Exception e) {
            logger.warn("General exception in completedGoalReport()", e);
            throw ServiceException.FAILURE("GoalManagementServiceImpl.assignedgoalsdelete", e);
        }
        return jobj;
    }

    private int getPastGoal(Finalgoalmanagement fgmt,double percentageTarget ) throws ServiceException{
        int pastGoals=-1;
        if(StringUtil.isLessthanDates(fgmt.getEnddate(),new Date().getTime())){
            if(percentageTarget>=100){
                pastGoals=1;
            } else if(percentageTarget < 100 ){
                pastGoals=0;
            }
        }
        return pastGoals;
    }

    public JSONObject employeesGoal(String userid, boolean viewAll, String start, String limit, String ss,String field, String direction, Long fromDate, Long toDate, String timeFormatId, String companyid,String timeZoneDiff) throws ServiceException, JSONException, ParseException, SessionExpiredException {

        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        int offSet=TimeZone.getTimeZone("GMT"+timeZoneDiff).getOffset(System.currentTimeMillis());
        try {
        	String relatedto="";
            KwlReturnObject kmsg = getActiveFinalGoals(userid, viewAll, start, limit, ss, relatedto,field, direction, fromDate, toDate);
            count = kmsg.getRecordTotalCount();
            if (count > 0) {

                User empuser = profileHandlerDAOObj.getUserObject(userid);
                String username = StringUtil.getFullName(empuser);

                String role = getUserRole(empuser);

                Iterator ite = kmsg.getEntityList().iterator();
                while (ite.hasNext()) {
                    Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                    JSONObject tmpObj = new JSONObject();
                    double dl = 0;
                    double percentageTarget = 0;

                    requestParams.put("distinctFlag", true);
                    int reltTo = fgmt.getRelatedto() == null ? 0 : fgmt.getRelatedto();

                    dl = getAchievedTarget(fgmt, companyid, empuser);

                    percentageTarget = getPercentageTarget(fgmt, dl);

                    int pastGoals = getPastGoal(fgmt, percentageTarget);

                    String percentTargt = "0";
                    if (StringUtil.isLessthanDates(new Date().getTime(), fgmt.getStartdate())) {
                        if (percentageTarget <= 0) {
                            percentTargt = "Future Dated";
                        }
                    }
                    String gid = fgmt.getId();
                    tmpObj.put("gid", gid);
                    tmpObj.put("gname", fgmt.getGoalname());
                    tmpObj.put("empname", username);
                    tmpObj.put("empdetails", username + " (" + role + ")");
                    tmpObj.put("empid", empuser.getUserID());
                    tmpObj.put("createdon", fgmt.getCreatedOn());
                    tmpObj.put("gdescription", fgmt.getGoaldesc());
                    tmpObj.put("targeted", fgmt.getTargeted());
                    tmpObj.put("relatedto", reltTo);
                    tmpObj.put("achieved", dl != 0.0 ? dl : "0");
                    tmpObj.put("percentageachieved", percentageTarget != 0.0 ? percentageTarget : percentTargt);
                    tmpObj.put("pastgoals", pastGoals);
                    tmpObj.put("gstartdate", fgmt.getStartdate());
                    tmpObj.put("genddate", fgmt.getEnddate());
                    tmpObj.put("gassignedby", StringUtil.getFullName(fgmt.getManager().getFirstName(), fgmt.getManager().getLastName()));
                    jarr.put(tmpObj);
                }
            }

            jobj.put("data", jarr);
            jobj.put("count", count);

        } catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in employeesGoal()", e);
        } catch (JSONException e) {
            logger.warn("JSONException exception in employeesGoal()", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in employeesGoal()", e);
        } catch (Exception e) {
            logger.warn("Exception exception in employeesGoal()", e);
        }

        return jobj;
    }
    
    public String getUserRole(User user ) throws ServiceException{

        String role="";
        String roleid = user.getRoleID();
        if (roleid.equals(Constants.COMPANY_ADMIN)) {
            role = "CA";
        }
        if (roleid.equals(Constants.COMPANY_SALES_MANAGER)) {
            role = "SM";
        }
        if (roleid.equals(Constants.COMPANY_SALES_EXECUTIVE)) {
            role = "SE";
        }
 
        return role;
    }

    public JSONObject loginEmployeeGoals(User user, boolean viewAll, String start, String limit, String ss, String relatedto, String field, String direction, Long fromDate, Long toDate, String timeFormatId, String companyid,String timeZoneDiff) throws ServiceException, JSONException, ParseException, SessionExpiredException {

        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 0;
        int offSet=TimeZone.getTimeZone("GMT"+timeZoneDiff).getOffset(System.currentTimeMillis());
        try {

            KwlReturnObject kmsg = getActiveFinalGoals(user.getUserID(), viewAll,start,limit,ss,relatedto,field,direction,fromDate,toDate);

            count = kmsg.getRecordTotalCount();
            Iterator ite = kmsg.getEntityList().iterator();
            while (ite.hasNext()) {
                Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();
                JSONObject tmpObj = new JSONObject();
                double dl=0;
                double percentageTarget=0;

                dl = getAchievedTarget( fgmt, companyid, user );

                percentageTarget = getPercentageTarget(fgmt, dl);

                int pastGoals = getPastGoal(fgmt ,percentageTarget);

                String gid=fgmt.getId();
                int reltTo = fgmt.getRelatedto()== null?0:fgmt.getRelatedto();
                tmpObj.put("gid",gid);
                tmpObj.put("gname", fgmt.getGoalname());
                tmpObj.put("gdescription", fgmt.getGoaldesc());
                tmpObj.put("targeted", fgmt.getTargeted());
                tmpObj.put("relatedto", reltTo);
                tmpObj.put("createdon",fgmt.getCreatedOn());
                tmpObj.put("achieved", dl!=0.0?dl:"0");
                tmpObj.put("pastgoals", pastGoals);
                tmpObj.put("gstartdate", fgmt.getStartdate());
                tmpObj.put("genddate", fgmt.getEnddate());
                tmpObj.put("gassignedby", StringUtil.getFullName(fgmt.getManager().getFirstName(), fgmt.getManager().getLastName()));
                jarr.put(tmpObj);
            }
            jobj.put("data", jarr);
            jobj.put("count", count);

        } catch (SessionExpiredException e) {
            logger.warn("SessionExpiredException exception in employeesGoal()", e);
        } catch (JSONException e) {
            logger.warn("JSONException exception in employeesGoal()", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in employeesGoal()", e);
        } catch (Exception e) {
            logger.warn("Exception exception in employeesGoal()", e);
        }

        return jobj;
    }

    public String completedGoalPieChart(StringBuffer usersList ) throws ServiceException{
        String result="";
        KwlReturnObject kmsg = null;
        String[] userIds = usersList.toString().split(",");

        result = "<pie>";
        for (int i = 0;i < userIds.length;i++){
            String usrid= userIds[i].replaceAll("'", "").trim();
            kmsg = getCompletedFinalGoals(usrid);
            if (kmsg.getRecordTotalCount() > 0) {
                User empuser = (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", usrid);
                result += "<slice title=\"" + StringUtil.getFullName(empuser) + "\" >" + kmsg.getRecordTotalCount() + "</slice>";
            }
        }
        result += "</pie>";
        return result;
    }

    public String completedGoalBarChart(StringBuffer usersList ) throws ServiceException{
        String result="";
        KwlReturnObject kmsg = null;
        String[] userIds = usersList.toString().split(",");

        result = "<chart><series>";
        for (int j = 0; j < userIds.length; j++) {
            String usrid= userIds[j].replaceAll("'", "").trim();
            User empuser = (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", usrid);
            result += "<value xid=\"" + j + "\" >" + StringUtil.getFullName(empuser) + "</value>";
        }
        result += "</series><graphs><graph gid=\"0\">";
        for (int k = 0; k < userIds.length; k++) {
            String usrid= userIds[k].replaceAll("'", "").trim();

            kmsg = getCompletedFinalGoals(usrid);

            result += "<value xid=\"" + k + "\" >" + kmsg.getRecordTotalCount() + "</value>";
        }

        result += "</graph></graphs></chart>";
        return result;
    }

    public KwlReturnObject getCompletedFinalGoals(String usrid) {
        KwlReturnObject kmsg = null;
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        try {
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.userID.userID");
            filter_names.add("c.deleted");
            filter_names.add("<c.enddate");
            filter_params.add(usrid);
            filter_params.add(false);
            Date now = new Date();
            now = dateOnlyFormat.parse(dateOnlyFormat.format(now));
            filter_params.add(now.getTime());
            kmsg = hrmsIntDAOObj.getFinalGoals(requestParams, filter_names, filter_params);

        } catch (ServiceException e) {
            logger.warn("ServiceException exception in getCompletedFinalGoals()", e);
        } catch (Exception e) {
            logger.warn("General exception in getCompletedFinalGoals()", e);
        }
        return kmsg;
    }

    public JSONObject getGoalHistoryJSON(boolean isexport, String goalid, String companyid, String userid, DateFormat dateFormat, String start, String limit) throws ServiceException {
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        int count = 0;
        List ll=null;
        List lst=null;
        try {

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            filter_names.add("c.id");
            filter_params.add(goalid);
            kmsg = hrmsIntDAOObj.getFinalGoals(requestParams, filter_names, filter_params);
            count = kmsg.getRecordTotalCount();
            lst = kmsg.getEntityList();
            Iterator ite = lst.iterator();
            while (ite.hasNext()) {
                Finalgoalmanagement fgmt = (Finalgoalmanagement) ite.next();

                filter_names.clear();
                filter_params.clear();
                requestParams.clear();

                requestParams = getFilter(2,fgmt.getStartdate(),fgmt.getEnddate(),userid,companyid);
                if (!StringUtil.isNullOrEmpty(start)) {
                    requestParams.put("start", start);
                    requestParams.put("limit", limit);
                    requestParams.put("pagingFlag", true);
                }
                filter_names = (ArrayList) requestParams.get("filter_names");
                filter_params = (ArrayList) requestParams.get("filter_params");
                kmsg = crmAccountDAOObj.getAccountOwners(requestParams, filter_names, filter_params);
                ll = kmsg.getEntityList();
                count = kmsg.getRecordTotalCount();
                Iterator itetype2 = ll.iterator();
                while (itetype2.hasNext()) {
                    CrmAccount ca = (CrmAccount) itetype2.next();
                    JSONObject tmpObj = new JSONObject();
                    String[] productInfo=crmAccountHandler.getAccountProducts(crmAccountDAOObj, ca.getAccountid());

                    tmpObj.put("accountid",ca.getAccountid());
                    tmpObj.put("accountname", ca.getAccountname());
                    tmpObj.put("revenue", StringUtil.isNullOrEmpty(ca.getRevenue())?"0":ca.getRevenue());
                    tmpObj.put("createdon", isexport? crmManagerCommon.exportDateNull(ca.getCreatedon(), dateFormat): crmManagerCommon.dateNull(ca.getCreatedon()));
                    tmpObj.put("productid",productInfo[0]);
                    tmpObj.put("product", productInfo[1]);
                    tmpObj.put("exportmultiproduct", productInfo[2]);
                    tmpObj.put("type", (ca.getCrmCombodataByAccounttypeid() != null ? ca.getCrmCombodataByAccounttypeid().getValue() : ""));
                    tmpObj.put("typeid", crmManagerCommon.comboNull(ca.getCrmCombodataByAccounttypeid()));
                    tmpObj.put("industryid", crmManagerCommon.comboNull(ca.getCrmCombodataByIndustryid()));
                    tmpObj.put("industry", (ca.getCrmCombodataByIndustryid() != null ? ca.getCrmCombodataByIndustryid().getValue() : ""));
                    tmpObj.put("website", (ca.getWebsite() != null ? ca.getWebsite() : ""));
                    jarr.put(tmpObj);
                }
            }
            jobj.put("data", jarr);
            jobj.put("count", count);

        } catch (JSONException e) {
            logger.warn("JSONException exception in getGoalHistoryJSON()", e);
        } catch (ServiceException e) {
            logger.warn("ServiceException exception in getGoalHistoryJSON()", e);
        }
        return jobj;
    }
    
	@Override
	public void setMessageSource(MessageSource messageSource){ 
		this.messageSource=messageSource;
	}


}
