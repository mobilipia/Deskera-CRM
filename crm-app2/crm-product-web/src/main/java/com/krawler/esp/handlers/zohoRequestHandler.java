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
package com.krawler.esp.handlers;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.contact.bizservice.ContactManagementService;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmCombodata;
import com.krawler.crm.database.tables.CrmCombomaster;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.zohoImportLog;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.contactModule.crmContactDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class zohoRequestHandler implements Runnable {

    public static String loginrequestformat = "servicename=%s&FROM_AGENT=%s&LOGIN_ID=%s&PASSWORD=%s";
    public static String logoutrequestformat = "FROM_AGENT=%s&scope=%s";
    public static String moduleURLFormatAll = "https://crm.zoho.com/crm/private/json/%s/getRecords";
    public static String moduleURLFormatOwner = "http://crm.zoho.com/crm/private/json/%s/getMyRecords";
    public static String getContactsformat = "newFormat=2&authtoken=%s&scope=%s&fromIndex=%s&toIndex=%s&sortColumnString=%s&sortOrderString=%s";
    public static String loginurl = "https://accounts.zoho.com/login";
    public static String logouturl = "https://accounts.zoho.com/logout";
    public static String scope="crmapi";
//    public static String zohoApiKey = ConfigReader.getinstance().get("zohoApiKey", "sisYEXsbTYUKb64YmJ-0zNNeUzhdIJn0kQaMGWeIdlI$");
    boolean isWorking = false;
    ArrayList processQueue = new ArrayList();
    private ZohoRequestDAO zohoRequestDAO;
    private crmAccountDAO crmAccountDAOObj;
    private crmOpportunityDAO crmOpportunityDAOObj;
    private HibernateTransactionManager txnManager;
    private crmLeadDAO crmLeadDAOObj;
    private ContactManagementService contactManagementService;
    public void setContactManagementService(ContactManagementService contactManagementService) {
        this.contactManagementService = contactManagementService;
    }

    private static final Log logger = LogFactory.getLog(zohoRequestHandler.class);

    public void setTxnManager(HibernateTransactionManager txnManager) {
        this.txnManager = txnManager;
    }

    public void setZohoRequestDAO(ZohoRequestDAO zohoRequestDAO) {
        this.zohoRequestDAO = zohoRequestDAO;
    }

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }

    public void setIsWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

    public boolean isIsWorking() {
        return isWorking;
    }

    public void add(String username, String password, String authToken, boolean lead, boolean accounts, boolean potentials, boolean contacts,
            String userid, String companyid, String ipAddress, String tzDiff, String partnerName, String sysEmailId) {
        try {
            JSONObject jobj = new JSONObject();
            jobj.accumulate("username", username);
            jobj.accumulate("password", password);
            jobj.accumulate("leads", lead);
            jobj.accumulate("accounts", accounts);
            jobj.accumulate("potentials", potentials);
            jobj.accumulate("contacts", contacts);
            jobj.accumulate("userid", userid);
            jobj.accumulate("companyid", companyid);
            jobj.accumulate("ipaddress", ipAddress);
            jobj.accumulate(Constants.SESSION_PARTNERNAME, partnerName);
            jobj.accumulate("sysemailid", sysEmailId);
            jobj.accumulate("tzdiff", tzDiff);
            //jobj.accumulate("apikey", apikey);
            jobj.accumulate("authtoken", authToken);
            processQueue.add(jobj);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

    public static String getAuthTicket(String username, String password) {
        Pattern pattern = Pattern.compile("TICKET\\=(.*)RESULT.*");
        String loginrequestpostdata = "";

        loginrequestpostdata = String.format(loginrequestformat, new Object[]{"ZohoCRM", "true", username, password});
        String result = StringUtil.makeExternalRequest(loginurl, loginrequestpostdata);
        Matcher matcher = pattern.matcher(result);
        String authticket = "";
        if (matcher.find()) {
            authticket = matcher.group(1);
        }
        return authticket;
    }

    public static void logoutWithTicket() {
        String logoutrequestpostdata = "";
        logoutrequestpostdata = String.format(logoutrequestformat, new Object[]{"true", scope});
        String result = StringUtil.makeExternalRequest(logouturl, logoutrequestpostdata);
    }

    public static String getCrmData(String zohoAuthTicket, String zohoAuthToken, String start, String limit, String sortColumn, String sortOrder, String module) {
        String contactsRequest = String.format(getContactsformat, new Object[]{zohoAuthToken, zohoAuthTicket, start, limit, sortColumn, sortOrder});
        String result = StringUtil.makeExternalRequest(String.format(moduleURLFormatAll, new Object[]{module}), contactsRequest);
        return result;
    }

    public static String getCrmData(String zohoAuthToken, String start, String limit, String module) {
        String sortColumn = "Account Name";
        String sortOrder = "desc";
        String contactsRequest = String.format(getContactsformat, new Object[]{zohoAuthToken, scope, start, limit, sortColumn, sortOrder});
        String result = StringUtil.makeExternalRequest(String.format(moduleURLFormatAll, new Object[]{module}), contactsRequest);
        return result;
    }

    public static String getCrmData(String zohoAuthToken, String module, int Page) {
        String sortColumn = "Account Name";
        String sortOrder = "desc";

        int end = 200 * Page;
        int start = (end - 200) + (Page - 1);
        String contactsRequest = String.format(getContactsformat, new Object[]{zohoAuthToken, scope, start, end, sortColumn, sortOrder});
        String result = StringUtil.makeExternalRequest(String.format(moduleURLFormatAll, new Object[]{module}), contactsRequest);
        return result;
    }

    public static JSONObject getRecordJson(String module, String username, String password, String zohoAuthToken) {
        JSONObject finalResult = new JSONObject();
        boolean resultflag = false;
        String message = "Unable to fetch data. Please try again later";
        try {
            //String authTicket = getAuthTicket(username, password);
            int page = 0;
            int recCount = 0;
            do {
                page++;
                resultflag = false;
                String result = getCrmData(zohoAuthToken, module, page);
                JSONObject jobj = new JSONObject(result);
                JSONObject responseObj = jobj.getJSONObject("response");
                Iterator ittr = responseObj.keys();

                while (ittr.hasNext()) {
                    String key = (String) ittr.next();
                    if (key.equalsIgnoreCase("nodata")) {
                        break;
                    } else if (key.equalsIgnoreCase("result")) {
                        resultflag = true;
                        break;
                    }
                }
                if (resultflag) {
                    Object rowObj = jobj.getJSONObject("response").getJSONObject("result").getJSONObject(module).get("row");

                    JSONArray rowArray = null;
                    if (rowObj.getClass().getName().equals(JSONObject.class.getName())) {
                        rowArray = new JSONArray("[" + rowObj.toString() + "]");
                    } else {
                        rowArray = (JSONArray) rowObj;
                    }

                    for (int i = 0; i < rowArray.length(); i++) {
                        recCount++;
                        JSONArray nameObj = rowArray.getJSONObject(i).getJSONArray("FL");
                        JSONObject temp = new JSONObject();
                        for (int cnt = 0; cnt < nameObj.length(); cnt++) {
                            JSONObject tempobj = nameObj.getJSONObject(cnt);

                            temp.accumulate(tempobj.get("val").toString(), tempobj.get("content"));


                        }

                        finalResult.append("data", temp);


                    }
                    message = recCount + " Record(s) fetched successfully";
                    finalResult.putOpt("recordCount", recCount);
                } else {
                    if (finalResult.get("data") == null) {
                        message = responseObj.getJSONObject("nodata").get("message").toString();
                    } else {
                        resultflag = true;
                    }
                    break;

                }
            } while (true);

            //Clear/Logout the ticket, once you have completed the operation
            logoutWithTicket();
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
            try {
                finalResult.accumulate("success", resultflag);
                finalResult.accumulate("msg", message);
            } catch (JSONException ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }

        return finalResult;
    }

    public static void main(String[] args) {

        //Session h = zohoRequestDAO.getCurrentSession();

        logger.debug(getRecordJson("Potentials", "peter.rains", "googlecat","b1601bd7e64cc08813e7a50ab4bedd2c"));
        //  zohoRequestHandler z = new zohoRequestHandler();
    }

    public String saveUpdateZohoContact(String username, String password, String authToken, String userid, String companyid, String ipAddress, String tzdiff) {

//        Session s = zohoRequestDAO.getCurrentSession();
        String result = "{success:false,recCount:0,totalRecords:0}";
        int recCount = 0;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String Hql = "from CrmContact c where  c.contactid= ?";
            JSONObject jobj = getRecordJson("Contacts", username, password, authToken);  //Potential , Leads, Accounts
            if (jobj.get("success").equals(true)) {
                JSONArray dataArray = jobj.getJSONArray("data");
                for (int cnt = 0; cnt < dataArray.length(); cnt++) {
//                    Transaction tx = (Transaction) s.beginTransaction();
                    JSONObject recObj = dataArray.getJSONObject(cnt);
                    List existingContact = zohoRequestDAO.executeQuery(Hql, new Object[]{recObj.get("CONTACTID")});

                    JSONObject conJObj = new JSONObject();
                    String conid = recObj.get("CONTACTID").toString();
                    conJObj.put("contactid", conid);
                    conJObj.put("companyid", companyid);
                    conJObj.put("userid", userid);
                    conJObj.put("contactownerid", userid);
                    conJObj.put("updatedon", new Date());
                    conJObj.put("description", "");
                    conJObj.put("phone", recObj.getString("Phone").equals("null") ? "" : recObj.getString("Phone"));
                    conJObj.put("isarchive", false);
                    conJObj.put("deleteflag", 0);
                    CrmAccount ca = null;
                    try {
                        ca = (CrmAccount) zohoRequestDAO.get(CrmAccount.class, recObj.get("ACCOUNTID").toString());
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    }
                    if (ca != null) {
                        conJObj.put("accountid", recObj.get("ACCOUNTID"));
                    }
                    conJObj.put("email", recObj.getString("Email").equals("null") ? "" : recObj.getString("Email"));
                    conJObj.put("firstname", recObj.getString("First Name").equals("null") ? "" : recObj.getString("First Name"));
                    conJObj.put("lastname", recObj.getString("Last Name").equals("null") ? "" : recObj.getString("Last Name"));
                    conJObj.put("validflag", 1);

                    if (existingContact.size() == 0) {
                        conJObj.put("contactid", "0");
                    } 
                    contactManagementService.saveContact(companyid, userid, tzdiff, ipAddress, conJObj);
//                    tx.commit();
                    recCount++;
                }
                txnManager.commit(status);
                result = "{success:true,recCount:" + recCount + ",totalRecords:" + jobj.get("recordCount") + "}";
            }
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
//            s.close();
            return result;
        }
    }

    public String saveUpdateZohoLeads(String username, String password, String authToken, String userid, String companyid) {

//        Session s = zohoRequestDAO.getCurrentSession();
        String result = "{success:false,recCount:0,totalRecords:0}";
        int recCount = 0;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {

            String Hql = "from CrmLead c where  c.leadid= ?";
            JSONObject jobj = getRecordJson("Leads", username, password, authToken);  //Potential , Leads, Accounts
            if (jobj.get("success").equals(true)) {

                JSONArray dataArray = jobj.getJSONArray("data");
                for (int cnt = 0; cnt < dataArray.length(); cnt++) {
//                    Transaction tx = (Transaction) hibernateTemplate.beginTransaction();
                    JSONObject recObj = dataArray.getJSONObject(cnt);
                    List existingContact = zohoRequestDAO.executeQuery(Hql, new Object[]{recObj.get("LEADID")});

                    JSONObject jobjret = new JSONObject();
                    String fname = recObj.getString("First Name");
                    String lname = recObj.getString("Last Name");
                    lname = fname.equalsIgnoreCase("null") ? lname : fname+" "+lname;
                    lname=lname.trim();
                    jobjret.put("lastname", recObj.getString("Last Name").equals("null") ? "" : lname);
                    jobjret.put("email", recObj.getString("Email").equals("null") ? "" : recObj.getString("Email"));
                    jobjret.put("phone", recObj.getString("Phone").equals("null") ? "" : recObj.getString("Phone"));
                    jobjret.put("validflag", 1);
                    jobjret.put("userid", userid);
                    jobjret.put("companyid", companyid);
                    jobjret.put("isconverted", "0");
                    jobjret.put("istransfered", "0");
                    jobjret.put("type", "0");
                    jobjret.put("updatedon", new Date());
                    jobjret.put("leadid", recObj.getString("LEADID"));
                    jobjret.put("leadownerid", userid);
                    jobjret.put("isarchive", false);
                    jobjret.put("deleteflag", 0);

                    if (existingContact.size() > 0) {
                        KwlReturnObject kmsg = crmLeadDAOObj.editLeads(jobjret);
                    } else {
                    	KwlReturnObject kmsg = crmLeadDAOObj.addLeads(jobjret);
                    }
                    recCount++;
                }
                txnManager.commit(status);
                result = "{success:true,recCount:" + recCount + ",totalRecords:" + jobj.get("recordCount") + "}";
            }
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        } finally {
//            s.close();
            return result;
        }
    }

    public String saveUpdateZohoAccounts(String username, String password, String authToken, String userid, String companyid) {

//        Session s = zohoRequestDAO.getCurrentSession();
        String result = "{success:false,recCount:0,totalRecords:0}";
        int recCount = 0;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String Hql = "from CrmAccount c where  c.accountid= ?";
            JSONObject jobj = getRecordJson("Accounts", username, password, authToken);  //Potential , Leads, Accounts
            if (jobj.get("success").equals(true)) {
                JSONArray dataArray = jobj.getJSONArray("data");
                for (int cnt = 0; cnt < dataArray.length(); cnt++) {
//                    Transaction tx = (Transaction) s.beginTransaction();
                    JSONObject recObj = dataArray.getJSONObject(cnt);
                    List existingContact = zohoRequestDAO.executeQuery(Hql, new Object[]{recObj.get("ACCOUNTID")});

                    JSONObject accJObj = new JSONObject();
                    accJObj.put("accountid", recObj.get("ACCOUNTID"));
                    accJObj.put("companyid", companyid);
                    accJObj.put("userid", userid);
                    accJObj.put("updatedon", new Date());
                    accJObj.put("accountname", recObj.getString("Account Name").equals("null") ? "" : recObj.getString("Account Name"));
                    accJObj.put("description", "");
                    accJObj.put("email", recObj.getString("Website").equals("null") ? "" : recObj.getString("Website"));
                    accJObj.put("phone", recObj.getString("Phone").equals("null") ? "" : recObj.getString("Phone"));
                    accJObj.put("revenue", recObj.getString("Annual Revenue").equals("null") ? "" : recObj.getString("Annual Revenue"));
                    accJObj.put("validflag", 1);
                    accJObj.put("accountownerid", userid);
                    accJObj.put("isarchive",false);
                    accJObj.put("deleteflag", 0);

                    if (existingContact.size() > 0) {
                        KwlReturnObject kmsg = crmAccountDAOObj.editAccounts(accJObj);
                    } else {
//                        accJObj.put("createdon", new Date());
                        KwlReturnObject kmsg = crmAccountDAOObj.addAccounts(accJObj);
                    }
                    recCount++;
                }
                txnManager.commit(status);
                result = "{success:true,recCount:" + recCount + ",totalRecords:" + jobj.get("recordCount") + "}";
            }
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
//            s.close();
            return result;
        }


    }

    public String saveUpdateZohoPotentials(String username, String password, String authToken, String userid, String companyid) {

//        Session s = zohoRequestDAO.getCurrentSession();
        String result = "{success:false,recCount:0,totalRecords:0}";
        int recCount = 0;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("JE_Tx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        TransactionStatus status = txnManager.getTransaction(def);
        try {
            String Hql = "from CrmOpportunity c where  c.oppid= ?";
            JSONObject jobj = getRecordJson("Potentials", username, password, authToken);  //Potential , Leads, Accounts
            if (jobj.get("success").equals(true)) {
                JSONArray dataArray = jobj.getJSONArray("data");
                for (int cnt = 0; cnt < dataArray.length(); cnt++) {
//                    Transaction tx = (Transaction) s.beginTransaction();
                    JSONObject recObj = dataArray.getJSONObject(cnt);
                    List existingPotential = zohoRequestDAO.executeQuery( Hql, new Object[]{recObj.get("POTENTIALID")});
                    String masterData = "FROM CrmCombodata c where c.crmCombomaster.masterid=?";
                    List masterdata = zohoRequestDAO.executeQuery( masterData, new Object[]{"d49609c2-0abc-47ce-8d5a-5850c03b7291"});
                    Iterator itr = masterdata.iterator();
                    HashMap combodata = new HashMap();

                    while (itr.hasNext()) {
                        CrmCombodata cdata = (CrmCombodata) itr.next();
                        combodata.put(cdata.getRawvalue().toLowerCase(), cdata.getValueid());
                    }

                    Object stagevalue = combodata.get(recObj.getString("Stage").toLowerCase());
                    DefaultMasterItem dmasterItem = null;
                    if (stagevalue == null) {
                        stagevalue = combodata.get("prospecting");
                        Company companyObj = (Company) zohoRequestDAO.get(Company.class, companyid);
                        CrmCombomaster crmComboMasterObj = (CrmCombomaster) zohoRequestDAO.get(CrmCombomaster.class, "d49609c2-0abc-47ce-8d5a-5850c03b7291");
                        dmasterItem = new DefaultMasterItem();
                        dmasterItem.setMainID("");
                        dmasterItem.setCompany(companyObj);
                        dmasterItem.setID(UUID.randomUUID().toString());
                        dmasterItem.setCrmCombomaster(crmComboMasterObj);
                        dmasterItem.setValue(recObj.getString("Stage"));
                    } else {
                        String dmasteritemid = "from DefaultMasterItem d where d.crmCombodata.valueid = ? and d.company.companyID=?";
                        List templist = zohoRequestDAO.executeQuery( dmasteritemid, new Object[]{stagevalue,companyid});
                        itr = templist.iterator();
                        while(itr.hasNext()){
                            dmasterItem = (DefaultMasterItem)itr.next();
                        }
                    }

                    JSONObject oppJObj = new JSONObject();
                    oppJObj.put("oppid", recObj.get("POTENTIALID"));
                    oppJObj.put("companyid", companyid);
                    oppJObj.put("userid", userid);
                    oppJObj.put("updatedon", new Date());
                    oppJObj.put("oppname", recObj.getString("Potential Name").equals("null") ? "" : recObj.getString("Potential Name"));
                    oppJObj.put("oppownerid", userid);
                    oppJObj.put("opptypeid", "0");
                    oppJObj.put("currencyid", "0");
                    oppJObj.put("oppstageid", dmasterItem.getID());
                    oppJObj.put("isarchive", false);
                    oppJObj.put("deleteflag", 0);
                    CrmAccount ca = null;
                    try {
                        ca = (CrmAccount) zohoRequestDAO.get(CrmAccount.class, recObj.get("ACCOUNTID").toString());
                    } catch (Exception e) {
                        logger.warn(e.getMessage(), e);
                    }
                    if (ca != null) {
                        oppJObj.put("accountnameid", recObj.get("ACCOUNTID"));
                    }
//                    String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
//                    String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
//                    if(recObj.has("closedate") && ! recObj.getString("Closing Date").equals("")){
//                        oppJObj.put("closedate",authHandler.getDateFormatter(timeFormatId, timeZoneDiff).parse(recObj.getString("Closing Date").toString()));
//                    }
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date closingdate = null;
                    if (!StringUtil.isNullOrEmpty(recObj.getString("Closing Date"))) {
                        closingdate = sdf.parse(recObj.getString("Closing Date").toString());
                        oppJObj.put("closingdate", closingdate.getTime());
                    }
                    oppJObj.put("validflag", 1);

                    if (existingPotential.size() > 0) {
                        KwlReturnObject kmsg = crmOpportunityDAOObj.editOpportunities(oppJObj);
                    } else {
//                        oppJObj.put("createdon", new Date());
                        KwlReturnObject kmsg = crmOpportunityDAOObj.addOpportunities(oppJObj);
                    }
//                    tx.commit();
                    recCount++;
                }
                txnManager.commit(status);
                result = "{success:true,recCount:" + recCount + ",totalRecords:" + jobj.get("recordCount") + "}";
            }
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            txnManager.rollback(status);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        } finally {
//            s.close();
            return result;
        }
    }

    @Override
    public void run() {
//        new TransactionTemplate(transactionManager)
//					.execute(new TransactionCallbackWithoutResult() {
//						@Override
//						public void doInTransactionWithoutResult(TransactionStatus status) {
//							dao.lock(client, NONE);
//							dao.save(new Report(client, client.getTotal()));
//							client.setLastCreated(new Date());
//						}
//					});

//        Session session = null;
        try{		
        while (!processQueue.isEmpty()) {
            JSONObject tempObj = (JSONObject) processQueue.get(0);
            try {
                this.isWorking = true;

                String username = tempObj.getString("username");
                String password = tempObj.getString("password");
                //String apiKey  = tempObj.getString("apikey");
                String authToken = tempObj.getString("authtoken");
                String userid = tempObj.getString("userid");
                String companyid = tempObj.getString("companyid");
                String ipAddress = tempObj.getString("ipaddress");
                String tzDiff = tempObj.getString("tzdiff");
                String parterName = tempObj.getString(Constants.SESSION_PARTNERNAME);
                String sysEmailId = tempObj.getString("sysemailid");
                JSONObject accountResult = null;
                JSONObject leadResult = null;
                JSONObject potentialResult = null;
                JSONObject contactResult = null;
                String result = "";
                if (tempObj.getBoolean("accounts")) {
                    result = saveUpdateZohoAccounts(username, password, authToken, userid, companyid);
                    accountResult = new JSONObject(result);
                }
                if (tempObj.getBoolean("leads")) {
                    result = saveUpdateZohoLeads(username, password, authToken, userid, companyid);
                    leadResult = new JSONObject(result);
                }
                if (tempObj.getBoolean("potentials")) {
                    result = saveUpdateZohoPotentials(username, password, authToken, userid, companyid);
                    potentialResult = new JSONObject(result);
                }
                if (tempObj.getBoolean("contacts")) {
                    result = saveUpdateZohoContact(username, password, authToken, userid, companyid, ipAddress, tzDiff);
                    contactResult = new JSONObject(result);
                }

                String htmltxt = "Report for data imported from zoho.<br/>";
                String plainMsg = "Report for data imported from zoho.\n";

                zohoImportLog zlog = new zohoImportLog();
                if (accountResult != null) {

                    zlog.setAccounts(Integer.parseInt(accountResult.getString("recCount")));
                    zlog.setFailedAccounts((accountResult.getInt("totalRecords") - accountResult.getInt("recCount")));
                    htmltxt += "<br/><br/>Accounts:<br/>";
                    htmltxt += "Total Records Imported: " + accountResult.getString("recCount");
                    htmltxt += "<br/>Failed Records: " + (accountResult.getInt("totalRecords") - accountResult.getInt("recCount"));
                    plainMsg += "\nAccounts:\n";
                    plainMsg += "Total Records Imported: " + accountResult.getString("recCount");
                    plainMsg += "\nFailed Records: " + (accountResult.getInt("totalRecords") - accountResult.getInt("recCount"));
                }
                if (leadResult != null) {
                    zlog.setLeads(Integer.parseInt(leadResult.getString("recCount")));
                    zlog.setFailedLeads((leadResult.getInt("totalRecords") - leadResult.getInt("recCount")));
                    htmltxt += "<br/><br/>Leads:<br/>";
                    htmltxt += "Total Records Imported: " + leadResult.getString("recCount");
                    htmltxt += "<br/>Failed Records: " + (leadResult.getInt("totalRecords") - leadResult.getInt("recCount"));
                    plainMsg += "\nLeads:\n";
                    plainMsg += "Total Records Imported: " + leadResult.getString("recCount");
                    plainMsg += "\nFailed Records: " + (leadResult.getInt("totalRecords") - leadResult.getInt("recCount"));
                }
                if (potentialResult != null) {
                    zlog.setPotentials(Integer.parseInt(potentialResult.getString("recCount")));
                    zlog.setFailedPotentials((potentialResult.getInt("totalRecords") - potentialResult.getInt("recCount")));
                    htmltxt += "<br/><br/>Potentials:<br/>";
                    htmltxt += "Total Records Imported: " + potentialResult.getString("recCount");
                    htmltxt += "<br/>Failed Records: " + (potentialResult.getInt("totalRecords") - potentialResult.getInt("recCount"));
                    plainMsg += "\nPotentials:\n";
                    plainMsg += "Total Records Imported: " + potentialResult.getString("recCount");
                    plainMsg += "\nFailed Records: " + (potentialResult.getInt("totalRecords") - potentialResult.getInt("recCount"));
                }
                if (contactResult != null) {
                    zlog.setContacts(Integer.parseInt(contactResult.getString("recCount")));
                    zlog.setFailedContacts((contactResult.getInt("totalRecords") - contactResult.getInt("recCount")));
                    htmltxt += "<br/><br/>Contacts:<br/>";
                    htmltxt += "Total Records Imported: " + contactResult.getString("recCount");
                    htmltxt += "<br/>Failed Records: " + (contactResult.getInt("totalRecords") - contactResult.getInt("recCount"));
                    plainMsg += "\nContacts:\n";
                    plainMsg += "Total Records Imported: " + contactResult.getString("recCount");
                    plainMsg += "\nFailed Records: " + (contactResult.getInt("totalRecords") - contactResult.getInt("recCount"));
                }
                htmltxt += "<br/><br/>For queries, email us at support@deskera.com<br/>";
                htmltxt += parterName+" Team";
                plainMsg += "\nFor queries, email us at support@deskera.com\n";
                plainMsg += parterName+" Team";

//                session = zohoRequestDAO.getCurrentSession();
//                Transaction tx = session.beginTransaction();
                DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                def.setName("JE_Tx");
                def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
                TransactionStatus status = txnManager.getTransaction(def);
                User u = (User) zohoRequestDAO.get(User.class, userid);

                zlog.setUserid(userid);
                zlog.setCompanyid(companyid);
                zlog.setDate(new java.util.Date());
                zlog.setZusername(username);
                zohoRequestDAO.save(zlog);
                txnManager.commit(status);
//                tx.commit();
                SendMailHandler.postMail(new String[]{u.getEmailID()}, parterName+" CRM - Report for data imported from zoho", htmltxt, plainMsg, parterName+" Admin<"+sysEmailId+">");

            } catch (MessagingException ex) {
                logger.warn(ex.getMessage(), ex);
            } catch (JSONException ex) {
                logger.warn(ex.getMessage(), ex);
            } finally {
//                session.close();
				processQueue.remove(tempObj);
            }
        }
        }catch(Exception e){
            logger.warn(e.getMessage(), e);
        }finally{
            this.isWorking = false;
        }
    }

    public String fetchLogs(String companyid) {
//        Session session = null;
        String result = "{success:false,data{}}";
        try {

//            session = zohoRequestDAO.getCurrentSession();
            String Hql = " from zohoImportLog z where z.companyid = ? order by date desc";
            List zoholog = zohoRequestDAO.executeQuery( Hql, new Object[]{companyid});

            //List user =
            Iterator itr = zoholog.iterator();
            JSONObject resultJson = new JSONObject();
            while (itr.hasNext()) {
                JSONObject jobj = new JSONObject();
                zohoImportLog zlogObj = (zohoImportLog) itr.next();
                User u = (User) zohoRequestDAO.get(User.class, zlogObj.getUserid().toString());
                jobj.accumulate("importdate", zlogObj.getDate());
                jobj.accumulate("zuname", zlogObj.getZusername());
                jobj.accumulate("username", StringUtil.getFullName(u));
                jobj.accumulate("accounts", zlogObj.getAccounts());
                jobj.accumulate("leads", zlogObj.getLeads());
                jobj.accumulate("potentials", zlogObj.getPotentials());
                jobj.accumulate("contacts", zlogObj.getContacts());
                resultJson.append("data", jobj);

            }
            resultJson.accumulate("success", true);
            result = resultJson.toString();

        } finally {
//            session.close();
            return result;
        }



    }
}
