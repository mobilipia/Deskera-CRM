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
package com.krawler.spring.crm.common;

import com.krawler.common.admin.CaseComment;
import com.krawler.common.admin.Comment;
import com.krawler.common.admin.Docs;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.accountModule.crmAccountHandler;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.contactModule.crmContactHandler;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.leadModule.crmLeadHandler;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityHandler;
import com.krawler.spring.documents.documentDAO;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;

import java.net.URLDecoder;
import java.text.ParseException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
//import com.krawler.crm.database.tables.AccountProject;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.common.admin.AuditTrail;
import com.krawler.common.admin.Company;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.text.SimpleDateFormat;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.activityModule.crmActivityDAO;
import com.krawler.spring.crm.campaignModule.crmCampaignDAO;
import com.krawler.spring.crm.integration.projectIntDAO;
import com.krawler.spring.crm.integration.projectIntHandler;
import com.krawler.spring.mailIntegration.mailIntegrationController;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;



public class detailPanelController extends MultiActionController {
    private static final long DAY_MILLI=86400000;
    private crmManagerDAO crmManagerDAOObj;
    private commentDAO crmCommentDAOObj;
    private documentDAO crmDocumentDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private profileHandlerDAO profileHandlerDAOObj;
    private String successView;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;

    private crmAccountDAO crmAccountDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private crmContactDAO crmContactDAOObj;
    private crmLeadDAO crmLeadDAOObj;
    private projectIntDAO projectIntDAOObj;
    private mailIntegrationController mailIntDAOObj;

    private crmActivityDAO crmActivityDAOObj;
    private crmCampaignDAO crmCampaignDAOObj;

    public void setmailIntDAO(mailIntegrationController mailIntDAOObj1) {
        this.mailIntDAOObj = mailIntDAOObj1;
    }

    public void setcrmCampaignDAO(crmCampaignDAO crmCampaignDAOObj1) {
        this.crmCampaignDAOObj = crmCampaignDAOObj1;
    }
    
    public void setcrmActivityDAO(crmActivityDAO crmActivityDAOObj1) {
        this.crmActivityDAOObj = crmActivityDAOObj1;
    }

    public void setprojectIntDAO(projectIntDAO projectIntDAOObj1) {
        this.projectIntDAOObj = projectIntDAOObj1;
    }
    
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }

    public void setcrmContactDAO(crmContactDAO crmContactDAOObj1) {
        this.crmContactDAOObj = crmContactDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setcrmManagerDAO(crmManagerDAO crmManagerDAOObj1) {
        this.crmManagerDAOObj = crmManagerDAOObj1;
    }
    
    public void setcrmCommentDAO(commentDAO crmCommentDAOObj1) {
        this.crmCommentDAOObj = crmCommentDAOObj1;
    }

    public void setcrmDocumentDAO(documentDAO crmDocumentDAOObj1) {
        this.crmDocumentDAOObj = crmDocumentDAOObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImpl1) {
        this.sessionHandlerImplObj = sessionHandlerImpl1;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }
    
    public JSONObject getDetailPanelJson(List ll, HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        JSONArray jArr = new JSONArray();
        try {
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            Iterator itr = ll.iterator();
            while (itr.hasNext()) {
                AuditTrail auditTrail = (AuditTrail) itr.next();
                JSONObject obj = new JSONObject();
                obj.put("id", auditTrail.getID());
                obj.put("user", profileHandlerDAOObj.getUserFullName(auditTrail.getUser().getUserID()));
                obj.put("ipaddr", auditTrail.getIPAddress());
                obj.put("details", auditTrail.getDetails());
                obj.put("action", auditTrail.getAction().getActionName());
                obj.put("time",  auditTrail.getAudittime());
                if(auditTrail.getDetails().contains("Task for")) {
                    obj.put("imgsrc", "../../images/task.gif");
                    obj.put("marginbottom", "19");
                    obj.put("width", "20px");
                    obj.put("height", "20px");
                }else if(auditTrail.getDetails().contains("Event for")) {
                    obj.put("imgsrc", "../../images/event.gif");
                    obj.put("marginbottom", "19");
                    obj.put("width", "20px");
                    obj.put("height", "20px");
                }else if(auditTrail.getAction().getActionName().contains("Activity")){
                    obj.put("imgsrc", "../../images/activity1.gif");
                    obj.put("marginbottom", "17");
                    obj.put("width", "20px");
                    obj.put("height", "25px");

                } else {
                    obj.put("imgsrc", "../../images/activity1.gif");
                    obj.put("marginbottom", "17");
                    obj.put("width", "20px");
                    obj.put("height", "25px");
                }
                jArr.put(obj);
            }

            // recent activities
            Date today = new Date();

            GregorianCalendar tempCal = new GregorianCalendar();
            tempCal.add(Calendar.MONTH, 1);
            Date maxDateUpdate = tempCal.getTime();

            String module = request.getParameter("module");
            String recid = request.getParameter("recid");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("module", module);
            requestParams.put("recid", recid);
            requestParams.put("OneTimeFlag", false);
            requestParams.put("usersList", usersList);
            requestParams.put("today", today);
            requestParams.put("maxDateUpdate", maxDateUpdate);

            List list = null;
            list = crmActivityDAOObj.getDetailPanelRecentActivity(requestParams).getEntityList();

            if(list != null){
                itr = list.iterator();
                while (itr.hasNext()) {
                    Object[] rows = (Object[]) itr.next();
                    CrmActivityMaster actMasterobj = (CrmActivityMaster) rows[0];
                    JSONObject obj = new JSONObject();

                    Date eventDate = null;
                    Date eventNxtDate = null;

                    Date tillDate = tempCal.getTime();
                    if(actMasterobj.getTilldate()!=null)
                        tillDate = actMasterobj.getTilldate();
                    Date actualStDt = actMasterobj.getStartdate();
                    Date tmpDate = actualStDt;
                    int scheduleType = actMasterobj.getScheduleType();
                    if(scheduleType!=0) {
                        Date currDate = new Date();
                        long datediff = ((currDate.getTime() - actualStDt.getTime()) / DAY_MILLI);
                            int days = 0;
                            switch(scheduleType) {
                                case 1 :
                                    tmpDate = currDate;
                                    if(tmpDate.compareTo(tillDate)<=0) {
                                        eventDate = tmpDate;
                                    } 
                                    tmpDate =new Date(eventDate.getTime()+DAY_MILLI);
                                    if(tmpDate.compareTo(tillDate)<=0) {
                                        eventNxtDate = tmpDate;
                                    }

                                    break;
                                case 2 :
									days = (int) ((datediff / 7) + (datediff % 7 == 0 ? 0 : 1)) * 7;
									tmpDate = new Date(actualStDt.getTime() + DAY_MILLI * 7);
									if (tmpDate.compareTo(tillDate) <= 0) {
										eventDate = tmpDate;
									}
									tmpDate = new Date(eventDate.getTime() + DAY_MILLI * days);
									if (tmpDate.compareTo(tillDate) <= 0) {
										eventNxtDate = tmpDate;
									}
		
									break;
                                case 3 :
                                    GregorianCalendar date1 = new GregorianCalendar();
                                    GregorianCalendar date2 = new GregorianCalendar();
                                    date1.setTime(currDate);
                                    date2.setTime(actualStDt);
                                    int monthdiff = 0;
                                    if(currDate.compareTo(actualStDt)>=0)
                                        monthdiff = getMonthDifference(date2,date1);
                                    else
                                        monthdiff = getMonthDifference(date1,date2);
                                    tempCal.setTime(actualStDt);
                                    tempCal.set(Calendar.DAY_OF_MONTH, actualStDt.getDate());
                                    tempCal.add(Calendar.MONTH, monthdiff);
                                    tmpDate = tempCal.getTime();
                                    if(tmpDate.compareTo(currDate)>=0 && tmpDate.compareTo(tillDate)<=0) {
                                        eventNxtDate = tmpDate;
                                        tempCal.setTime(actualStDt);
                                        tempCal.add(Calendar.MONTH, monthdiff-1);
                                        tempCal.set(Calendar.DAY_OF_MONTH, actualStDt.getDate());
                                        tmpDate = tempCal.getTime();
                                        eventDate = tmpDate;
                                    } else {
                                        eventDate = tmpDate;
                                        tempCal.setTime(actualStDt);
                                        tempCal.set(Calendar.DAY_OF_MONTH, actualStDt.getDate());
                                        tempCal.add(Calendar.MONTH, monthdiff+1);
                                        tmpDate = tempCal.getTime();
                                        eventNxtDate = tmpDate;
                                    }
                                    break;
                            }
                            String details = "";
                            if(actMasterobj.getFlag().equals("Task")) {
                                obj.put("imgsrc", "../../images/task.gif");
                                obj.put("marginbottom", "16");
                                details = "Task ";
                            } else if(actMasterobj.getFlag().equals("Event")) {
                                obj.put("imgsrc", "../../images/event.gif");
                                obj.put("marginbottom", "15");
                                details = "Event";
                            } else if(actMasterobj.getFlag().equals("Phone Call")){
                                obj.put("imgsrc", "../../images/phone_call.gif");
                                obj.put("marginbottom", "16");
                                details = "Phone Call";
                            } else {
                                obj.put("imgsrc", "../../images/activity1.gif");
                                obj.put("marginbottom", "17");
                            }
                            if(!StringUtil.isNullOrEmpty(actMasterobj.getSubject()))
                                details += " \""+actMasterobj.getSubject()+"\"";
                            if(eventDate!=null) {
                                obj.put("id", actMasterobj.getActivityid());
                                obj.put("user", StringUtil.getFullName(actMasterobj.getUsersByUserid()));
                                obj.put("action", details + " scheduled ");
                                obj.put("details", details + " on ");
                                obj.put("time", eventDate.getTime());
                                jArr.put(obj);
                            }
                            if(eventNxtDate!=null) {
                                JSONObject nextEvent = new JSONObject();
                                nextEvent.put("id", actMasterobj.getActivityid());
                                nextEvent.put("user", StringUtil.getFullName(actMasterobj.getUsersByUserid()));
                                nextEvent.put("action", details + " scheduled ");
                                nextEvent.put("imgsrc", obj.getString("imgsrc"));
                                nextEvent.put("marginbottom", obj.getString("marginbottom"));
                                nextEvent.put("details", details + " on ");
                                nextEvent.put("time", eventNxtDate.getTime());
                                jArr.put(nextEvent);
                            }
    //                        }
                    }

                }
            }
//            }
            //  One Time event
            if(Constants.moduleMap.containsKey(module)) {
                int temp = Constants.moduleMap.get(module);
                if(temp == 1 || temp == 2 || temp == 3 || temp == 4 || temp == 6 || temp == 8) {
                    GregorianCalendar lastMaxDtCal = new GregorianCalendar();
                    lastMaxDtCal.add(Calendar.DATE, -5);
                    Date lastMaxDateUpdate = lastMaxDtCal.getTime();

                    requestParams.put("lastMaxDateUpdate", lastMaxDateUpdate);
                    requestParams.put("OneTimeFlag", true);
                    list = crmActivityDAOObj.getDetailPanelRecentActivity(requestParams).getEntityList();

                    Iterator ite = list.iterator();
                    while (ite.hasNext()) {
                    	
                        Object[] rows = (Object[]) ite.next();
                        CrmActivityMaster actMasterobj = (CrmActivityMaster) rows[0];
                        JSONObject obj = new JSONObject();
                        String details = "";
                        if(actMasterobj.getFlag().equals("Task")) {
                            obj.put("imgsrc", "../../images/task.gif");
                            obj.put("marginbottom", "16");
                            details = "Task ";
                        } else if(actMasterobj.getFlag().equals("Event")) {
                            obj.put("imgsrc", "../../images/event.gif");
                            obj.put("marginbottom", "15");
                            details = "Event";
                        } else if(actMasterobj.getFlag().equals("Phone Call")){
                            obj.put("imgsrc", "../../images/phone_call.gif");
                            obj.put("marginbottom", "16");
                            details = "Phone Call";
                        } else {
                            obj.put("imgsrc", "../../images/activity1.gif");
                            obj.put("marginbottom", "17");
                        }
                        if(!StringUtil.isNullOrEmpty(actMasterobj.getSubject()))
                            details += " \""+actMasterobj.getSubject()+"\"";
                        obj.put("id", actMasterobj.getActivityid());
                        obj.put("user", StringUtil.getFullName(actMasterobj.getUsersByUserid()));
                        obj.put("action", details + " scheduled ");
                        obj.put("details", details + " on " +(actMasterobj.getPhone()!=""?actMasterobj.getPhone():""));
                        obj.put("time", actMasterobj.getStartDate());
                        jArr.put(obj);
                    }
                }
            }

           if(module.equals("campaign")) {
                requestParams.clear();
                requestParams.put("recid", recid);
                int tzdiff=TimeZone.getTimeZone("GMT"+sessionHandlerImpl.getTimeZoneDifference(request)).getOffset(System.currentTimeMillis());
                requestParams.put("tzdiff", tzdiff);
                List CLll = crmCampaignDAOObj.getDetailPanelRecentCampaign(requestParams).getEntityList();
                Iterator campaignLogList = CLll.iterator();
                while (campaignLogList.hasNext()) {
                    JSONObject tempCampaignLogData = new JSONObject();
                    JSONObject obj = new JSONObject();
                    Object[] logObj = (Object[])campaignLogList.next();

                    obj.put("id", logObj[4]);
                    CrmCampaign crmCamp = (CrmCampaign) logObj[5];
//                    String eventDate = dateFmt.format((Date)logObj[0]);
                    //crmManagerDAOObj.preferenceDatejsformat(timeZoneDiff, (Date)logObj[0], dateFmt);
         
                    String details = "Email Marketing Campaign";
                    obj.put("imgsrc", "../../images/activity1.gif");
                    obj.put("marginbottom", "17");
                    obj.put("user", StringUtil.getFullName(crmCamp.getUsersByUserid()));
                    obj.put("action", details + " scheduled ");
                    obj.put("details", details + " on ");
                    obj.put("sent",logObj[1]);
                    obj.put("viewed",logObj[2]);
                    obj.put("failed",logObj[3]);
                    obj.put("time",logObj[0]==null? null: ((Date)logObj[0]).getTime());
                    jArr.put(obj);
                }
            }

            for(int i = 0; i < jArr.length(); i++) {
                for(int j =0; j < jArr.length(); j++) {
                    if(jArr.getJSONObject(i).optLong("time")>jArr.getJSONObject(j).optLong("time")){
                        JSONObject jobj1 = jArr.getJSONObject(i);
                        jArr.put(i, jArr.getJSONObject(j));
                        jArr.put(j, jobj1);
                    }
                }
            }

            jobj.put("auditList", jArr);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }

    public static int getMonthDifference(GregorianCalendar fromCalendar, GregorianCalendar toCalendar) {
        int count = 0;
        for(fromCalendar.add(Calendar.MONTH, 1); fromCalendar.compareTo(toCalendar) <= 0; fromCalendar.add(
            Calendar.MONTH, 1)) {
            count++;
        }
        return count;
    }

    private Date addTimePart(Date date, String timePart) throws ParseException {
        Date datePart = new Date();
        SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dfWithNoTime = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Calendar cal = new GregorianCalendar();
            datePart = dfWithNoTime.parse(dfWithNoTime.format(date));
            cal.setTime(datePart);
            Date sttime = timeformat.parse(timePart);
            cal.add(Calendar.HOUR, sttime.getHours());
            cal.add(Calendar.MINUTE, sttime.getMinutes());
            datePart =  cal.getTime();
        } catch(ParseException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return datePart;
    }

    public JSONObject getCommentJson(List ll, HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                JSONObject temp = new JSONObject();
                Comment t = (Comment) ite.next();
                temp.put("comment", t.getComment());
                temp.put("postedon", crmManagerDAOObj.userPreferenceDate(request, new Date(t.getPostedon()), 0));
                temp.put("addedby", profileHandlerDAOObj.getUserFullName(t.getuserId().getUserID()));
                temp.put("commentid", t.getId());
                temp.put("deleteflag", userid.equals(t.getuserId().getUserID()));
                jarr.put(temp);
                kmsg = crmCommentDAOObj.deleteComments(userid, t.getId());
            }
            jobj.put("commPerm", ((sessionHandlerImpl.getPerms(request, "Comments") & 2) == 2));
            jobj.put("commList", jarr);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }
    public JSONObject getCaseCommentJson(List ll, HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        KwlReturnObject kmsg = null;
        String firstName="";
        String lastName="";
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                JSONObject temp = new JSONObject();
                CaseComment t = (CaseComment) ite.next();
                temp.put("comment", t.getComment());
                temp.put("postedon", crmManagerDAOObj.userPreferenceDate(request, new Date(t.getPostedon()), 0));
                if(t.getUserflag()==com.krawler.crm.utils.Constants.CASE_COMMENT_USERFLAG){
                	String contactId=t.getuserId().toString();
                	Object[] row =crmCommentDAOObj.getCustomerName(contactId);
            		if (row!=null) {    					
        				firstName = StringUtil.isNullOrEmpty((String) row[0])?"":(String) row[0];;
        				lastName = StringUtil.isNullOrEmpty((String) row[1])?"":(String) row[1];;
        			}               	
            		temp.put("addedby", firstName+" "+lastName.trim());
                }else{
            		temp.put("addedby", profileHandlerDAOObj.getUserFullName(t.getuserId()));
                }
                temp.put("commentid", t.getId());
                temp.put("deleteflag", userid.equals(t.getuserId()));
                jarr.put(temp);
               // kmsg = crmCommentDAOObj.deleteComments(userid, t.getId());
            }
            jobj.put("commPerm", ((sessionHandlerImpl.getPerms(request, "Comments") & 2) == 2));
            jobj.put("commList", jarr);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }

    public JSONObject getDocumentJson(List ll, HttpServletRequest request) {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        int count = 1;
        try {
            DateFormat dateFormat = authHandler.getDateFormatterWithTimeZoneForExport(request);
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                JSONObject temp = new JSONObject();
                Docs t = (Docs) ite.next();
                temp.put("srno", count++);
                temp.put("docid", t.getDocid());
                temp.put("Name", t.getDocname());
                temp.put("Size", StringUtil.sizeRenderer(t.getDocsize()));
                temp.put("Type", t.getDoctype());
                if(t.getUserid()==null)
                	temp.put("uploadedby", crmDocumentDAOObj.getDocUploadedCustomername(t.getDocid()));
                else
                	temp.put("uploadedby", profileHandlerDAOObj.getUserFullName(t.getUserid().getUserID()));
                temp.put("uploadedon", t.getUploadedOn()!=null?dateFormat.format(t.getUploadedon()):"");
                jarr.put(temp);
            }
            jobj.put("docPerm", ((sessionHandlerImpl.getPerms(request, "Document") & 4) == 4));
            jobj.put("docList", jarr);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return jobj;
    }

    public ModelAndView getDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        JSONObject jobj1 = new JSONObject();
        JSONObject jobj2 = new JSONObject();
        JSONObject jobj3 = new JSONObject();
        JSONArray FinalArr = new JSONArray();
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            String recid = request.getParameter("recid");
            String detailFlag = request.getParameter("detailFlag");
            String module = request.getParameter("module");
            requestParams.put("recid", StringUtil.checkForNull(recid));
            requestParams.put("detailFlag", StringUtil.checkForNull(detailFlag));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));
            StringBuffer usersList = sessionHandlerImpl.getRecursiveUsersList(request);
            boolean heirarchyPerm = crmManagerCommon.chkHeirarchyPerm(request, "Contact");

            kmsg = auditTrailDAOObj.getRecentActivityDetails(requestParams);
            jobj3 = getDetailPanelJson(kmsg.getEntityList(), request);
            HashMap<String, Object> requestParams1 = new HashMap<String, Object>();
            requestParams1.put("recid", recid);
            requestParams1.put("module", module);
            if(module.equals(com.krawler.crm.utils.Constants.Case)){
            	kmsg = crmCommentDAOObj.getCaseComments(requestParams1);
            	jobj1 = getCaseCommentJson(kmsg.getEntityList(), request);
            }
            else{
            kmsg = crmCommentDAOObj.getComments(requestParams1);
            jobj1 = getCommentJson(kmsg.getEntityList(), request);
            }

            kmsg = crmDocumentDAOObj.getDocuments(requestParams);
            jobj2 = getDocumentJson(kmsg.getEntityList(), request);
            jobj.put("emailData",new JSONObject().put("emailList", new JSONArray()));
            if(module.equals("Case")) {
                boolean flag = false;
                CrmCase obj = (CrmCase) KwlCommonTablesDAOObj.getObject("com.krawler.crm.database.tables.CrmCase", StringUtil.checkForNull(recid));
                if (obj.getCrmAccount() != null) {
                    String email = obj.getCrmAccount().getEmail();
                    if (!StringUtil.isNullOrEmpty(email)) {
                        JSONObject jobjEmail = new JSONObject();
                        jobjEmail=mailIntDAOObj.getRecentEmailDetails(request, recid,email);
                        FinalArr=jobjEmail.getJSONArray("emailList");
                        flag=true;
                    }
                }
                if (obj.getCrmContact() != null) {
                    String email = obj.getCrmContact().getEmail();
                    if (!StringUtil.isNullOrEmpty(email)) {
                        JSONObject jobjEmail = new JSONObject();
                        JSONArray tempArray = new JSONArray();
                        jobjEmail = mailIntDAOObj.getRecentEmailDetails(request, recid, email);
                        tempArray=jobjEmail.optJSONArray("emailList");
                        for (int i = 0; i < tempArray.length(); i++) {
                            FinalArr.put(tempArray.getJSONObject(i));
                        }
                        flag=true;
                    }
                }
                if(flag) {
                    JSONObject jobjEmail = new JSONObject();
                    jobjEmail.put("emailList", FinalArr);
                    jobj.put("emailData", jobjEmail);
                }
            }else{
                String email = request.getParameter("email");                
                if(!StringUtil.isNullOrEmpty(email)){
                    jobj.put("emailData", mailIntDAOObj.getRecentEmailDetails(request, recid,email));
                }
            }
            if(Constants.moduleMap.containsKey(module)){
                switch(Constants.moduleMap.get(module)){
                    case 1:
                            jobj.put("subownersData", crmLeadHandler.getLeadOwners(crmLeadDAOObj, request, recid));
                            jobj.put("contactsData", crmContactHandler.getContacts(crmContactDAOObj, usersList, heirarchyPerm, request, request.getParameter("mapid"),"Lead"));
                            break;
                    case 2:
                            jobj.put("subownersData", crmOpportunityHandler.getOppOwners(crmOpportunityDAOObj, request, recid));
                            jobj.put("contactsData", crmContactHandler.getContacts(crmContactDAOObj, usersList, heirarchyPerm, request, request.getParameter("mapid"),"Opportunity"));
                            break;
                    case 3:
                            jobj.put("subownersData", crmAccountHandler.getAccOwners(crmAccountDAOObj, request, recid));
                            jobj.put("contactsData", crmContactHandler.getContacts(crmContactDAOObj, usersList, heirarchyPerm, request, request.getParameter("mapid"),"Account"));
                            if(crmManagerCommon.hasViewProjPerm(request) ) {
                                Company companyObj = (Company) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyid);
                                jobj.put("projData", projectIntHandler.getAccountProjectDetails(projectIntDAOObj, request, recid, companyObj));
                            }
                            break;
                    case 4:
                            jobj.put("subownersData", crmContactHandler.getContactOwners(crmContactDAOObj, request, recid));
                            break;
                }
            }

            jobj.put("commData", jobj1);
            jobj.put("docData", jobj2);
            jobj.put("auditData", jobj3);
        } catch (Exception e) {
            logger.warn(e.getMessage(),e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public ModelAndView getComment(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		JSONObject jobj = new JSONObject();
		List<String> idsList = new ArrayList<String>();
		Map kwlcommentz = new HashMap();
		String id = request.getParameter("id");
		String module = request.getParameter("module");
		KwlReturnObject kmsg = null;
		String view = "jsonView";
		try {

			idsList.add(id);
			 if(module.equals(com.krawler.crm.utils.Constants.Case)){
				 kwlcommentz = crmCommentDAOObj.getCaseCommentz(idsList);
			 }else{
				 kwlcommentz = crmCommentDAOObj.getCommentz(idsList);
			 }
			String comment ="";
			
		        StringBuilder sb = new StringBuilder();
		        List commentList = (List) kwlcommentz.get(id);
		        if(commentList!=null &&!commentList.isEmpty()){
		            Iterator ite = commentList.iterator();
		            int count = 1;
		            while (ite.hasNext()) {
                        String cmo = "";
                        try {
    		                cmo = URLDecoder.decode((String) ite.next(),"UTF-8");
                        } catch (Exception ex) {
                            cmo = "Can't decode comments for record : "+id+"<br>"+ex.getMessage();
                        }
		                cmo = " "+count+")"+cmo+"\n";
		                count++;
		                sb.append(cmo);
		            }
		        }
		        if(!StringUtil.isNullObject(sb)){
		            comment = sb.toString();
		        }
		        
			jobj.put("comment", comment);
			jobj.put("success", true);
		} catch (Exception e) {
			 logger.warn(e.getMessage(),e);
		}
		return new ModelAndView(view, "model", jobj.toString());
	}

}
