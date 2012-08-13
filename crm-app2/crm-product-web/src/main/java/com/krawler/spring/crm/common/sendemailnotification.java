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

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.database.tables.CrmActivityMaster;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.esp.handlers.APICallHandlerService;
import com.krawler.esp.handlers.Receiver;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.esp.web.resource.Links;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.activityModule.crmActivityDAO;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityReportDAO;
import com.krawler.spring.exportFunctionality.exportDAOImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class sendemailnotification extends MultiActionController {
    private HibernateTransactionManager txnManager;
    private crmActivityDAO crmActivityDAOObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private exportDAOImpl exportDAOImplObj;
    private crmCommonDAO crmCommonDAOObj;
    private crmOpportunityReportDAO opportunityReportDAOObj;
    private crmCaseDAO crmCaseDAOObj;
    private APICallHandlerService apiCallHandlerService;
    
    public void setApiCallHandlerService(APICallHandlerService apiCallHandlerService)
    {
        this.apiCallHandlerService = apiCallHandlerService;
    }

    public void setcrmOpportunityReportDAO(crmOpportunityReportDAO opportunityReportDAOObj1) {
        this.opportunityReportDAOObj = opportunityReportDAOObj1;
    }

    public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }

    public void setexportDAOImpl(exportDAOImpl exportDAOImplObj) {
        this.exportDAOImplObj = exportDAOImplObj;
    }

    public void setCrmManagerDAO(crmManagerDAO crmManagerDAOObj) {
        this.crmManagerDAOObj = crmManagerDAOObj;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj;
    }


    public void setCrmActivityDAO(crmActivityDAO crmActivityDAOObj) {
        this.crmActivityDAOObj = crmActivityDAOObj;
    }

    public void setCrmCaseDAO(crmCaseDAO crmCaseDAOObj) {
        this.crmCaseDAOObj = crmCaseDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
            this.txnManager = txManager;
    }

    //Overdue Activity
    public ModelAndView sendemailOverdueActivity(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            String platformUrl = ConfigReader.getinstance().get("platformURL",null);
            KwlReturnObject result = crmActivityDAOObj.getOverdueactivities();
            List list = result.getEntityList();
            Iterator itr = list.iterator();
            while (itr.hasNext()) {
                Object[] obj = (Object[]) itr.next();
                User user = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", ((obj[4] == null) ? "" : obj[4].toString()));
                Company company = user.getCompany();
                if(platformUrl==null) {
                    String loginUrl = URLUtil.getDomainURL(company.getSubDomain(), true);
                    sendOverDueTasksEmail(obj, loginUrl);
                } else {
                    String companyid = company.getCompanyID();
                    JSONObject temp = new JSONObject();
                    temp.put("companyid", companyid);
                    apiCallHandlerService.callApp(platformUrl, temp, companyid, "13", false, sendOverDueTasksEmailReceiver(obj));
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView", "model", "");
        }
    }

    private void sendOverDueTasksEmail(Object[] obj, String loginURL) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            User user = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", ((obj[4] == null) ? "" : obj[4].toString()));
            com.krawler.common.admin.Company company = user.getCompany();
            String companyName = company.getCompanyName();
            if (company.getNotificationtype() == 1 && user.getNotificationtype() == 1) {
                CrmActivityMaster activityObj = (CrmActivityMaster) KwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CrmActivityMaster", obj[5].toString());
                String dateFormatId = user.getDateFormat().getFormatID();
                String timeFormatId = String.valueOf(user.getTimeformat());
                String timeZoneDiff = user.getTimeZone().getDifference();
                DateFormat dateformat = KwlCommonTablesDAOObj.getUserDateFormatter(dateFormatId, timeFormatId, timeZoneDiff);
                SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm a");

                String startdate = (activityObj.getStartdate() == null ? "" : dateformat.format(activityObj.getStartdate()));
                String enddate = (activityObj.getEnddate() == null ? "" : dateformat.format(activityObj.getEnddate()));
                String starttime = (activityObj.getStartdate() == null ? "" : crmManagerDAOObj.preferenceDatejsformat(timeZoneDiff, activityObj.getStartdate(), timeformat));
                String endtime = (activityObj.getEnddate() == null ? "" : crmManagerDAOObj.preferenceDatejsformat(timeZoneDiff, activityObj.getEnddate(), timeformat));

                DefaultMasterItem statusobj = (DefaultMasterItem) KwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.DefaultMasterItem", ((obj[2] == null) ? "" : obj[2].toString()));
                DefaultMasterItem priorityobj = (DefaultMasterItem) KwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.DefaultMasterItem", ((obj[3] == null) ? "" : obj[3].toString()));

                loginURL = StringUtil.appendSubDomain(loginURL, company.getSubDomain(), true);
                String emailhtml = "Hi <b>" + ((obj[0] != null) ? obj[0] : "") + "</b>,<br><br>Following Event/Task in " + companyName + " CRM is overdue:<br>";
                emailhtml += "<b>Subject: " +((obj[1] != null) ? obj[1] : "") + "</b><br>";
                emailhtml += "Start Date: " + startdate + "<br>";
                emailhtml += "Start Time: " + starttime + "<br>";
                emailhtml += "End Date: " + enddate + "<br>";
                emailhtml += "End Time: " + endtime + "<br>";
                emailhtml += "Status: " + ((statusobj != null) ? statusobj.getValue() : "") + "<br>";
                emailhtml += "Priority: " + ((priorityobj != null) ? priorityobj.getValue() : "") + "<br><br>";
                emailhtml += "You can log in at" + "<br>";
                emailhtml += loginURL + "<br><br>";
                emailhtml += "--" + companyName + " Admin";


                String plaintext = "Hi " + ((obj[0] != null) ? obj[0] : "") + " ,\n\nFollowing Event/Task in " + companyName + " CRM is overdue:\n";
                plaintext += "<b>Subject: " + ((obj[1] != null) ? obj[1] : "") + "</b>\n";
                plaintext += "Start Date: " + startdate + "\n";
                plaintext += "Start Time: " + starttime + "\n";
                plaintext += "End Date: " + enddate + "\n";
                plaintext += "End Time: " + endtime + "\n";
                plaintext += "Status: " + ((statusobj != null) ? statusobj.getValue() : "") + "\n";
                plaintext += "Priority: " + ((priorityobj != null) ? priorityobj.getValue() : "") + "\n\n";
                plaintext += "You can log in at" + "\n";
                plaintext += loginURL + "\n\n";
                plaintext += "--" + companyName + " Admin";
                String rEmail = user.getEmailID();
                String sysEmailId = StringUtil.getSysEmailIdByCompanyID(company);
                if (!StringUtil.isNullOrEmpty(rEmail)) {
                    SendMailHandler.postMail(new String[]{rEmail}, "[" + companyName + " CRM] Event/Task Notification", emailhtml, plaintext, sysEmailId, companyName + " Admin");
                }
            }
        } catch (Exception ex) {
            txnManager.rollback(status);
            ex.printStackTrace();
        }
    }

    private Receiver sendOverDueTasksEmailReceiver(Object[] obj) {
        return new Receiver() {

            private Object[] obj;

            public Receiver setValues(Object[] obj) {
                this.obj = obj;
                return this;
            }

            @Override
            public void receive(Object resultObj) {
                JSONObject jobj = null;
                if (resultObj instanceof JSONObject) {
                    jobj = (JSONObject) resultObj;
                }
                try {
                    String loginUrl = ConfigReader.getinstance().get("crmURL",null);
                    if(jobj.has(com.krawler.common.util.Constants.CRMURL) && !StringUtil.isNullOrEmpty(jobj.getString(com.krawler.common.util.Constants.CRMURL))) {
                        loginUrl = jobj.getString(com.krawler.common.util.Constants.CRMURL);
                    }
                    sendOverDueTasksEmail(obj, loginUrl);

                } catch (Exception ex) {
                    logger.warn("Cannot store isFree: " + ex.toString());
                }
            }
        }.setValues(obj);
    }

    public ModelAndView sendemailCaseSLA(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            int cronDuration = 15;//Run after 15 minutes
            String platformUrl = ConfigReader.getinstance().get("platformURL");
            if (request.getParameter("cronDuration") != null) {
                cronDuration = Integer.parseInt(request.getParameter("cronDuration"));
            }
            List<Object[]> caselist = crmCaseDAOObj.getCaseSLA(cronDuration);
            for (Object[] obj : caselist) {
                int cnotify = Integer.parseInt(obj[9].toString());
                int unotify = Integer.parseInt(obj[10].toString());
                if (cnotify == 1 && unotify == 1) {
                    User creator = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", ((obj[13] == null) ? "" : obj[13].toString()));
                    Company company = creator.getCompany();
                    if (platformUrl == null) {
                        String loginUrl = URLUtil.getDomainURL(company.getSubDomain(), true);
                        sendemailCaseSLA(obj, loginUrl);
                    } else {
                        String companyid = company.getCompanyID();
                        JSONObject temp = new JSONObject();
                        temp.put("companyid", companyid);
                        apiCallHandlerService.callApp(platformUrl, temp, companyid, "13", false, sendemailCaseSLAReceiver(obj));
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
        } finally {
            return new ModelAndView("jsonView", "model", "");
        }
    }

    private void sendemailCaseSLA(Object[] obj, String loginURL) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String managername = obj[6] != null ? obj[6].toString() : "";
            String manageremail = obj[7] != null ? obj[7].toString() : "";
            String casename = obj[0] != null ? obj[0].toString() : "";
            String accountname = obj[2] != null ? obj[2].toString() : "";
            String username = obj[1] != null ? obj[1].toString() : "";
            String sla = obj[3] != null ? obj[3].toString() : "";
            String companyname = obj[11].toString();
            String companyemailid = obj[12].toString();
            User creator = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", ((obj[13] == null) ? "" : obj[13].toString()));
            Company company = creator.getCompany();
            companyemailid = StringUtil.getSysEmailIdByCompanyID(company);
            String emailhtml = "Hi <b>" + managername + "</b>,<br><br>";
            emailhtml += "Case - <b>" + casename + "</b>" + (!StringUtil.isNullOrEmpty(accountname) ? " for the Account - <b>" + accountname + "</b>" : "") + " assigned to <b>" + username + "</b> has exceeded the " + sla + ".<br><br>";
            emailhtml += "You can log in at" + "<br>";
            
            loginURL = StringUtil.appendSubDomain(loginURL, company.getSubDomain(), true);

            emailhtml += loginURL + "<br><br>";
            emailhtml += "--" + companyname + " Admin";

            String plaintext = "Hi <b>" + managername + "</b>,\n\n";
            plaintext += "Case - <b>" + casename + "</b>" + (!StringUtil.isNullOrEmpty(accountname) ? " for the Account - <b>" + accountname : "") + "</b> assigned to <b>" + username + "</b> has exceeded the " + sla + ".\n\n";
            plaintext += "You can log in at" + "\n";
            plaintext += loginURL + "\n\n";
            plaintext += "--" + companyname + " Admin";

            if (!StringUtil.isNullOrEmpty(manageremail)) {
                SendMailHandler.postMail(new String[]{manageremail}, "[" + companyname + " CRM] Case SLA Notification", emailhtml, plaintext, companyemailid, companyname + " Admin");
            }
        } catch (Exception ex) {
            txnManager.rollback(status);
            logger.warn(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Receiver sendemailCaseSLAReceiver(Object[] obj) {
        return new Receiver() {
            private Object[] obj;
            public Receiver setValues(Object[] obj) {
                this.obj = obj;
                return this;
            }
            @Override
            public void receive(Object resultObj) {
                JSONObject jobj = null;
                if (resultObj instanceof JSONObject) {
                    jobj = (JSONObject) resultObj;
                }
                try {
                    String loginUrl = ConfigReader.getinstance().get("crmURL",null);
                    if(jobj.has(com.krawler.common.util.Constants.CRMURL) && !StringUtil.isNullOrEmpty(jobj.getString(com.krawler.common.util.Constants.CRMURL))) {
                        loginUrl = jobj.getString(com.krawler.common.util.Constants.CRMURL);
                    }
                    sendemailCaseSLA(obj, loginUrl);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.warn("Cannot store isFree: " + ex.toString());
                }
            }
        }.setValues(obj);
    }
    
    public ModelAndView sendOpportunityReportsEmail(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            String platformUrl = ConfigReader.getinstance().get("platformURL");
            String userid = "";
            ArrayList<String> filter_names = new ArrayList<String>();
            ArrayList<Object> filter_values = new ArrayList<Object>();
            filter_names.add("dc.emailreport");
            filter_values.add(1);
            KwlReturnObject kr = crmCommonDAOObj.getDashboardReportConfig(filter_names, filter_values);
            List<Object[]> configList = kr.getEntityList();
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            boolean heirarchyPerm = false;
            requestParams.put("heirarchyPerm", heirarchyPerm);
            for (Object[] row : configList) {
                String companyid = row[0].toString();
                userid = row[1].toString();
                User user = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
                com.krawler.common.admin.Company company = user.getCompany();
                if (company.getNotificationtype() == 1 && user.getNotificationtype() == 1) {
                    if (platformUrl == null) {
                        String loginUrl = URLUtil.getDomainURL(company.getSubDomain(), true);
                        sendOpportunityReportsEmail(row, loginUrl, company.getCompanyName());
                    } else {
                        JSONObject temp = new JSONObject();
                        temp.put("companyid", companyid);
                        apiCallHandlerService.callApp(platformUrl, temp, companyid, "13", false, sendOpportunityReportsEmailReceiver(row,company.getCompanyName()));
                    }
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        } finally {
            return new ModelAndView("jsonView", "model", "");
        }
    }

    private void sendOpportunityReportsEmail(Object[] row, String loginUrl, String partnerName) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            ByteArrayOutputStream byRegion = new ByteArrayOutputStream();
            ByteArrayOutputStream byStage = new ByteArrayOutputStream();
            ByteArrayOutputStream byPerson = new ByteArrayOutputStream();
            HashMap<String, ByteArrayOutputStream> regionMap = new HashMap<String, ByteArrayOutputStream>();
            HashMap<String, ByteArrayOutputStream> stageMap = new HashMap<String, ByteArrayOutputStream>();
            HashMap<String, ByteArrayOutputStream> personMap = new HashMap<String, ByteArrayOutputStream>();
            String reportname = "Opportunity Sales Reports";
            String userEmailID = "";
            String username = "";
            String config = "";
            String title = "";
            String width = "285,285";
            String header = "name,salesamount";
            String align = "center,right";
            String xtype = "";

            String companyid = row[0].toString();
            String userid = row[1].toString();
            JSONObject jobj = new JSONObject();
            User user = (User) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
            com.krawler.common.admin.Company company = user.getCompany();
            HashMap<String, StringBuffer> userListMap = new HashMap<String, StringBuffer>();
            HashMap<String, User> userMap = new HashMap<String, User>();
            StringBuffer usersList = new StringBuffer();
            if (userListMap.containsKey(userid)) {
                usersList = userListMap.get(userid);
            } else {
                usersList = crmManagerDAOObj.recursiveUsers(userid);
                userListMap.put(userid, usersList);
            }
            int rid = Integer.parseInt(row[2].toString());
            username = row[6].toString();
            userEmailID = (row[7] != null) ? row[7].toString() : "";

            userMap.put(userid, user);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            List<Object[]> dataList = new ArrayList<Object[]>();
            if (rid == 46) {//Opp Sales By Region
                requestParams.put("valueField", "c.crmCombodataByRegionid.value");
                requestParams.put("groupbyField", "c.crmCombodataByRegionid");
                requestParams.put("idField", "c.crmCombodataByRegionid.ID");
                dataList = opportunityReportDAOObj.oppSalesamountDashboardPieChart(requestParams, usersList);

                title = "Region,Sales Amount";
                config = "{\"landscape\":\"true\",\"pageBorder\":\"true\",\"gridBorder\":\"true\",\"title\":\"Sales By Region\",\"subtitles\":\"\",\"headNote\":\"Opportunity Report\",\"showLogo\":\"false\",\"headDate\":\"false\",\"footDate\":\"true\",\"footPager\":\"true\",\"headPager\":\"false\",\"footNote\":\"\",\"textColor\":\"000000\",\"bgColor\":\"FFFFFF\"}";
                jobj = getSalesJSONdata(dataList);
                JSONArray gridmap = null;
                byRegion = exportDAOImplObj.getPdfData(gridmap, config, title, header, width, align, xtype, jobj);
                regionMap.put(userid, byRegion);
            } else if (rid == 47) {//Opp Sales By Stage
                requestParams.put("valueField", "c.crmCombodataByOppstageid.value");
                requestParams.put("groupbyField", "c.crmCombodataByOppstageid");
                requestParams.put("idField", "c.crmCombodataByOppstageid.ID");
                dataList = opportunityReportDAOObj.oppSalesamountDashboardPieChart(requestParams, usersList);

                title = "Opportunity Stage,Sales Amount";
                config = "{\"landscape\":\"true\",\"pageBorder\":\"true\",\"gridBorder\":\"true\",\"title\":\"Sales By Stage\",\"subtitles\":\"\",\"headNote\":\"Opportunity Report\",\"showLogo\":\"false\",\"headDate\":\"false\",\"footDate\":\"true\",\"footPager\":\"true\",\"headPager\":\"false\",\"footNote\":\"\",\"textColor\":\"000000\",\"bgColor\":\"FFFFFF\"}";
                jobj = getSalesJSONdata(dataList);
                JSONArray gridmap = null;
                byStage = exportDAOImplObj.getPdfData(gridmap, config, title, header, width, align, xtype, jobj);
                stageMap.put(userid, byStage);
            } else if (rid == 48) {//Opp Sales By Sales Person
                requestParams.put("valueField", "oo.usersByUserid.firstName||' '||oo.usersByUserid.lastName as name");
                requestParams.put("groupbyField", "oo.usersByUserid");
                requestParams.put("idField", "oo.usersByUserid.userID");
                dataList = opportunityReportDAOObj.oppSalesamountDashboardPieChart(requestParams, usersList);

                title = "Sales Person,Sales Amount";
                config = "{\"landscape\":\"true\",\"pageBorder\":\"true\",\"gridBorder\":\"true\",\"title\":\"Sales By Person\",\"subtitles\":\"\",\"headNote\":\"Opportunity Report\",\"showLogo\":\"false\",\"headDate\":\"false\",\"footDate\":\"true\",\"footPager\":\"true\",\"headPager\":\"false\",\"footNote\":\"\",\"textColor\":\"000000\",\"bgColor\":\"FFFFFF\"}";
                jobj = getSalesJSONdata(dataList);
                JSONArray gridmap = null;
                byPerson = exportDAOImplObj.getPdfData(gridmap, config, title, header, width, align, xtype, jobj);
                personMap.put(userid, byPerson);
            }

            Iterator ite = userMap.keySet().iterator();
            while (ite.hasNext()) {
                userid = (String) ite.next();
                User userObj = userMap.get(userid);
                username = StringUtil.getFullName(userObj);
                userEmailID = userObj.getEmailID();
                if (regionMap.containsKey(userid)) {
                    byRegion = regionMap.get(userid);
                } else {
                    byRegion = new ByteArrayOutputStream();
                }
                if (stageMap.containsKey(userid)) {
                    byStage = stageMap.get(userid);
                } else {
                    byStage = new ByteArrayOutputStream();
                }
                if (personMap.containsKey(userid)) {
                    byPerson = personMap.get(userid);
                } else {
                    byPerson = new ByteArrayOutputStream();
                }
                exportPDF(reportname, userid, username, userEmailID, byRegion, byStage, byPerson, partnerName, StringUtil.getSysEmailIdByCompanyID(company));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            txnManager.rollback(status);
        }
    }
    
    private Receiver sendOpportunityReportsEmailReceiver(Object[] obj, String companyName) {
        return new Receiver() {
            private Object[] obj;
            private String companyName;
            public Receiver setValues(Object[] obj,String companyName) {
                this.obj = obj;
                this.companyName = companyName;
                return this;
            }
            @Override
            public void receive(Object resultObj) {
                JSONObject jobj = null;
                if (resultObj instanceof JSONObject) {
                    jobj = (JSONObject) resultObj;
                }
                try {
                    String loginUrl = ConfigReader.getinstance().get("crmURL",null);
                    if(jobj.has(com.krawler.common.util.Constants.CRMURL) && !StringUtil.isNullOrEmpty(jobj.getString(com.krawler.common.util.Constants.CRMURL))) {
                        loginUrl = jobj.getString(com.krawler.common.util.Constants.CRMURL);
                    }
                    String partnerName = jobj.optString(Constants.SESSION_PARTNERNAME,this.companyName);
                    if(partnerName.equals(Constants.DESKERA))
                        partnerName = this.companyName;
                    sendOpportunityReportsEmail(obj, loginUrl,partnerName);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.warn("Cannot store isFree: " + ex.toString());
                }
            }
        }.setValues(obj,companyName);
    }
    public JSONObject getSalesJSONdata(List<Object[]> dataList) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try{
            for(Object[] datarow : dataList) {
                Object salesamount = (Object) datarow[0];
                String mastername = "";
                if(datarow[1] != null) {
                    mastername = datarow[2].toString();
                } else {
                    mastername = "None";
                }

                if (Double.parseDouble(String.valueOf(salesamount)) > 0) {
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("name",mastername);
                    tmpObj.put("salesamount",salesamount);
                    jarr.put(tmpObj);
                }
             }
             jobj.put("success", true);
             jobj.put("data", jarr);
        } catch(Exception e) {
            logger.warn(e.getMessage());
        }
        return jobj;
    }

    public void exportPDF(String reportname, String userid, String username, String userEmailID, ByteArrayOutputStream byStage,
            ByteArrayOutputStream byRegion, ByteArrayOutputStream bySalesPerson, String partnerName, String sysEmailId) {
        try {
                PdfReader reader = null;
                PdfReader reader2 = null;
                PdfReader reader3 = null;
                // new reader with the PDF just created
                if(byStage.size() > 0) {
                    reader = new PdfReader(byStage.toByteArray());
                }
                // 2nd reader with pdf
                if(byRegion.size() > 0) {
                    reader2 = new PdfReader(byRegion.toByteArray());
                }

                // 3rd reader with pdf
                if(bySalesPerson.size() > 0) {
                    reader3 = new PdfReader(bySalesPerson.toByteArray());
                }

                PdfCopyFields copy = new PdfCopyFields(byStage);
                if(reader!=null)copy.addDocument(reader);
                if(reader2!=null)copy.addDocument(reader2);
                if(reader3!=null)copy.addDocument(reader3);
                copy.close();

//                response.setHeader("Content-Disposition", "attachment; filename=\"" + reportname + ".pdf\"");
//                response.setContentType("application/octet-stream");
//                response.setContentLength(byStage.size());
//                response.getOutputStream().write(byStage.toByteArray());
//                response.getOutputStream().flush();
//                response.getOutputStream().close();

                if(byStage.size() > 0) {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String  GenerateDate = df.format(new Date());
                    String fileName = reportname+"_"+userid+"_"+GenerateDate+".pdf";
                    String destinationDirectory = storageHandlerImpl.GetDocStorePath() + "opportunityreport";
                    java.io.File destDir = new java.io.File(destinationDirectory);
                    if (!destDir.exists()) { //Create destination folder if not present
                        destDir.mkdirs();
                    }
                    fileName = destinationDirectory + "/" + fileName;

                    java.io.FileOutputStream fileOut = new java.io.FileOutputStream(fileName);
                    fileOut.write(byStage.toByteArray());
                    fileOut.flush();
                    fileOut.close();

                    String htmltxt = "Hi <b>"+username+"</b>,<br><br>";
                    htmltxt += "<br/><br/>Attachment is the pdf file containing Opportunity sales report.";
                    htmltxt += "<br/>For queries, email us at "+sysEmailId+"<br/>";
                    htmltxt += "<br/>"+partnerName+" Admin";

                    String plainMsg = "Hi "+username+" ,\n\n";
                    plainMsg += "\n\nAttachment is the pdf file containing Opportunity sales report.";
                    plainMsg += "\nFor queries, email us at "+sysEmailId+"\n";
                    plainMsg += "\n"+partnerName+" Admin";
                    if(!StringUtil.isNullOrEmpty(userEmailID)) {
                        SendMailHandler.postMailAttachment(new String[] { userEmailID }, "["+partnerName+" CRM] - Opportuniy sales reports.", htmltxt,
                                plainMsg, partnerName+" Admin<"+sysEmailId+">", fileName, reportname+".pdf");
                    }
                }
        } catch (Exception e) {
            Logger.getLogger(sendemailnotification.class.getName()).log(Level.SEVERE, null, e);
        }
    }

//    public KwlReturnObject getOverdueactivities() {
//        List lst = null;
//        try {
//            String hql = "";
//            //Convert schedule date and schedule time to server timezone not GMT. Then compare the date.
//            String serverTimezone = ConfigReader.getinstance().get("SERVER_TIMEZONE");//"-05:00";
//            serverTimezone = "-05:00";
//            hql = "select concat(fname,' ',lname) as name,subject,startdate,starttime,enddate,endtime,statusid,priorityid,companyid,users.userid from crm_activity_master as am " +
//                    " inner join users on am.userid = users.userid " +
//                    " inner join timezone as tz on tz.timzoneid = users.timezone where " +
//                   " DATEDIFF(DATE_FORMAT(CONVERT_TZ(ADDTIME(am.enddate, am.endtime), '+00:00', '"+serverTimezone+"'),'%Y-%m-%d %H:%i:%S'), NOW()) = -1 ";
//            lst = executeNativeQuery(hql);
//        } catch (Exception e)
//        {
//            throw ServiceException.FAILURE("crmActivityDAOImpl.getOverdueactivities : "+e.getMessage(), e);
//        } finally {
//            return new KwlReturnObject(true, KWLErrorMsgs.S01, "", lst, lst.size());
//        }
//    }
}
