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
package com.krawler.spring.profileHandler;

import com.krawler.common.util.Constants;
import com.krawler.esp.handlers.FileUploadHandler;
import com.krawler.spring.authHandler.authHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.common.admin.AuditAction;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.esp.web.resource.Links;
import com.krawler.profileHandler.bizservice.ProfileHandlerService;
import com.krawler.spring.auditTrailModule.auditTrailDAO;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.companyDetails.companyDetailsDAO;
import com.krawler.spring.mailIntegration.mailIntegrationController;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import javax.mail.MessagingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class profileHandlerController extends MultiActionController implements MessageSourceAware{
	public static final int IMG_SIZE_LARGE = 200;
	public static final int IMG_SIZE_MEDIUM = 100;
	public static final int IMG_SIZE_SMALL = 35;

    private profileHandlerDAO profileHandlerDAOObj;
    private companyDetailsDAO companyDetailsDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private permissionHandlerDAO permissionHandlerDAOObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private mailIntegrationController mailIntDAOObj;
    private auditTrailDAO auditTrailDAOObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private ProfileHandlerService profileHandlerServiceObj;
    private MessageSource messageSource;
    
    
	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource=messageSource;		
	}

    private static final Log logger = LogFactory.getLog(profileHandlerController.class);
    /**
     * @return the ProfileHandlerService
     */
    public ProfileHandlerService getProfileHandlerService()
    {
        return profileHandlerServiceObj;
    }

    /**
     * @param ProfileHandlerService
     */
    public void setProfileHandlerService(ProfileHandlerService profileHandlerServiceObj)
    {
        this.profileHandlerServiceObj = profileHandlerServiceObj;
    }

    public void setcompanyDetailsDAO(companyDetailsDAO companyDetailsDAOObj1) {
        this.companyDetailsDAOObj = companyDetailsDAOObj1;
    }
    
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public void setmailIntDAO(mailIntegrationController mailIntDAOObj1) {
        this.mailIntDAOObj = mailIntDAOObj1;
    }

    public void setAuditTrailDAO(auditTrailDAO auditTrailDAOObj1) {
        this.auditTrailDAOObj = auditTrailDAOObj1;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }
    
    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public void setpermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj1) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj1;
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

    public ModelAndView getAllUserDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            String lid = StringUtil.checkForNull(request.getParameter("lid"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("lid", lid);
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            requestParams.put("ss", StringUtil.checkForNull(request.getParameter("ss")));

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("u.company.companyID");
            filter_params.add(companyid);
            filter_names.add("u.deleteflag");
            filter_params.add(0);
            if (!StringUtil.isNullOrEmpty(lid)) {
                filter_names.add("u.userID");
                filter_params.add(lid);
            }
            
            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);
            jobj = getUserDetailsJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn("General exception in getAllUserDetails()", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getUserToEmailJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        JSONObject jobjTemp = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                User obj = (User) ite.next();
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("relatedto", 3);
                tmpObj.put("relatedid", obj.getUserID());
                tmpObj.put("fname", obj.getFirstName());
                tmpObj.put("name", obj.getLastName());
                tmpObj.put("emailid", obj.getEmailID());
                jarr.put(tmpObj);
            }

            jobjTemp = new JSONObject();
            jobjTemp.put("header",messageSource.getMessage("crm.campaigndetails.bouncereport.header.fname",null , RequestContextUtils.getLocale(request)));//"First Name");
            jobjTemp.put("tip", messageSource.getMessage("crm.campaigndetails.bouncereport.header.fname",null , RequestContextUtils.getLocale(request)));//"First Name");
            jobjTemp.put("pdfwidth", 60);
            jobjTemp.put("dataIndex", "fname");
            jarrColumns.put(jobjTemp);

            jobjTemp = new JSONObject();
            jobjTemp.put("header", messageSource.getMessage("crm.campaigndetails.bouncereport.header.lname",null , RequestContextUtils.getLocale(request)));//"Last Name");
            jobjTemp.put("tip", messageSource.getMessage("crm.campaigndetails.bouncereport.header.lname",null , RequestContextUtils.getLocale(request)));//"Last Name");
            jobjTemp.put("pdfwidth", 60);
            jobjTemp.put("dataIndex", "name");
            jarrColumns.put(jobjTemp);

            jobjTemp = new JSONObject();
            jobjTemp.put("header", messageSource.getMessage("crm.EMAIL",null , RequestContextUtils.getLocale(request)));// "Email");
            jobjTemp.put("tip", messageSource.getMessage("crm.EMAIL",null , RequestContextUtils.getLocale(request)));//"Email");
            jobjTemp.put("pdfwidth", 60);
            jobjTemp.put("dataIndex", "emailid");
            jarrColumns.put(jobjTemp);

            jobjTemp = new JSONObject();
            jobjTemp.put("name", "targetscount");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "name");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "fname");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "emailid");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "relatedto");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "company");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "targetlistDescription");
            jarrRecords.put(jobjTemp);
            jobjTemp = new JSONObject();
            jobjTemp.put("name", "relatedid");
            jarrRecords.put(jobjTemp);

            jobj.put("columns", jarrColumns);
            jMeta.put("totalProperty", "totalCount");
            jMeta.put("root", "data");
            jMeta.put("fields", jarrRecords);
            jobj.put("metaData", jMeta);

            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", totalSize);
       } catch(Exception e) {
            logger.warn("General exception in getUserToEmailJson()", e);
       }
       return jobj;
    }
    
    public ModelAndView getUserToEmail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("email", true);
            requestParams.put("start", StringUtil.checkForNull(request.getParameter("start")));
            requestParams.put("limit", StringUtil.checkForNull(request.getParameter("limit")));
            if(request.getParameter("ss") != null && !StringUtil.isNullOrEmpty(request.getParameter("ss"))) {
                requestParams.put("ss", request.getParameter("ss"));
            }
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("u.company.companyID");
            filter_params.add(companyid);
            filter_names.add("u.deleteflag");
            filter_params.add(0);

            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);
            jobj = getUserToEmailJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn("General exception in getUserToEmail()", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
    
    public JSONObject getUserDetailsJson(List ll, HttpServletRequest request, int totalSize) {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            Iterator ite = ll.iterator();
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            while (ite.hasNext()) {
                User user = (User) ite.next();
                UserLogin ul = user.getUserLogin();
                JSONObject obj = new JSONObject();
                obj.put("userid", user.getUserID());
                obj.put("username", ul.getUserName());
                obj.put("fname", user.getFirstName());
                obj.put("lname", user.getLastName());
                obj.put("fullname", user.getFirstName().trim() + " " +user.getLastName().trim());
                obj.put("image", getAppsImagePath(user.getUserID(), IMG_SIZE_SMALL, user.getImage()));
                obj.put("emailid", user.getEmailID());
                obj.put("lastlogin", (ul.getLastActivityDate() == null ? "" : authHandler.getDateFormatter(timeFormatId, timeZoneDiff).format(ul.getLastActivityDate())));
                obj.put("aboutuser", user.getAboutUser());
                obj.put("address", user.getAddress());
                obj.put("contactno", user.getContactNumber());
                obj.put("formatid", (user.getDateFormat() == null ? "" : user.getDateFormat().getFormatID()));
                obj.put("tzid", (user.getTimeZone() == null ? Constants.NEWYORK_TIMEZONE_ID : user.getTimeZone().getTimeZoneID())); // 23 is id of New York Time Zone. [default]
                obj.put("callwithid", user.getCallwith());
                obj.put("timeformat", (user.getTimeformat() != 1 && user.getTimeformat() != 2) ? 2 : user.getTimeformat()); // 2 is id for '24 hour timeformat'. [default]
                obj.put("notificationtype", user.getNotificationtype() == 1 ? true : false);
                kmsg = permissionHandlerDAOObj.getRoleofUser(user.getUserID());
                Iterator ite2 = kmsg.getEntityList().iterator();
                while(ite2.hasNext()) {
                    Object[] row = (Object[]) ite2.next();
                    obj.put("roleid", row[0]);
                    obj.put("rolename", row[1]);
                }
                jarr.put(obj);
            }
            jobj.put("data", jarr);
            jobj.put("count", totalSize);
        } catch (Exception e) {
            logger.warn("General exception in getUserDetailsJson()", e);
        }
        return jobj;
    }
    
    public String getAppsImagePath(String userid, int size, String defaultPath){
    	try {
        String platformURL = com.krawler.esp.utils.ConfigReader.getinstance().get("platformURL");
        String imgPath = platformURL.concat("images/store/?uid_size=").concat(userid)
                .concat("_").concat(Integer.toString(size)).concat("&userflag=true");
        	return imgPath;
    	}catch (Exception e) {
			return defaultPath;
		}
    }

    public ModelAndView getAllManagers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject jobj = new JSONObject();
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));

            kmsg = profileHandlerDAOObj.getAllManagers(requestParams);
            jobj = getUserDetailsJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn("General exception in getAllManagers()", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveDateFormat(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            String dateid = request.getParameter("newformat");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));
            requestParams.put("dateformat", StringUtil.checkForNull(dateid));
            requestParams.put("addUser", false);

            profileHandlerDAOObj.saveUser(requestParams);
            request.getSession().setAttribute("dateformatid", dateid);
            jobj.put("success", true);
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn("General exception in saveDateFormat()", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            String[] ids = request.getParameterValues("userids");
            for (int i = 0; i < ids.length; i++) {
                profileHandlerDAOObj.deleteUser(ids[i]);
            }
            jobj.put("msg", messageSource.getMessage("crm.userprofile.deleteusersuccessmsg", null, RequestContextUtils.getLocale(request)));//"User deleted successfully");
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn("General exception in deleteUser()", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView saveUser(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
       KWLTimeZone timeZone = null;
        try {
            KwlReturnObject kmsg = null;
            HashMap hm=null;
            if(ServletFileUpload.isMultipartContent(request)){
                hm=new FileUploadHandler().getItems(request);
            }
            if(hm==null)throw new Exception("Form does not support file upload");

            String id=(String)hm.get("userid");


            String auditDetails="";
            User user = null;
            String pwd = null;

            if(id!=null&&id.length()>0){
                user = (User)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", id);
            }
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("userid", id);
            requestParams.put("companyid", companyid);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("u.userLogin.userName");
            filter_names.add("u.company.companyID");
            filter_names.add("!u.userID");
            filter_params.add(hm.get("username"));
            filter_params.add(sessionHandlerImpl.getCompanyid(request));
            filter_params.add(id);

            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);
            if(kmsg.getRecordTotalCount() > 0) {
                throw new Exception("User Name already exists");
            }
            
            String name=(String)hm.get("username");
            String oldname = user.getUserLogin().getUserName();
            if(!name.equals(oldname))
                auditDetails += "User Name '"+oldname+"' updated to '"+name+"',";

            name=(String)hm.get("fname");
            oldname = user.getFirstName();
            if(!name.equals(oldname))
                if(!StringUtil.isNullOrEmpty(oldname))
                    auditDetails += " First Name '"+oldname+"' updated to '"+name+"',";
                else
                    auditDetails += " First Name '"+name+"' added,";
            name=(String)hm.get("lname");
            oldname = user.getLastName();
            if(!name.equals(oldname))
                if(!StringUtil.isNullOrEmpty(oldname))
                    auditDetails += " Last Name '"+oldname+"' updated to '"+name+"',";
                else
                    auditDetails += " Last Name '"+name+"' added,";
            name=(String)hm.get("emailid");
            oldname = user.getEmailID();
            if(!name.equals(oldname))
                if(!StringUtil.isNullOrEmpty(oldname))
                    auditDetails += " E-mail '"+oldname+"' updated to '"+name+"',";
                else
                    auditDetails += " E-mail '"+name+"' added,";
            name=StringUtil.serverHTMLStripper((String)hm.get("address"));
            oldname = user.getAddress();
            if(!name.equals(oldname))
                if(!StringUtil.isNullOrEmpty(oldname))
                    auditDetails += " Address '"+oldname+"' updated to '"+name+"',";
                else
                    auditDetails += " Address '"+name+"' added,";

            if (hm.get("callwithid") != null) {
                int callid = Integer.parseInt(hm.get("callwithid").toString());
                if(callid != user.getCallwith())
                    auditDetails += " Call With updated,";
            }
            if (hm.get("timeformat") != null) {
                int timeformat = Integer.parseInt(hm.get("timeformat").toString());
                if(timeformat != user.getTimeformat())
                    auditDetails += " Time format updated,";
            }
            name=(String)hm.get("contactno");
            oldname = user.getContactNumber();
            if(!name.equals(oldname))
                if(!StringUtil.isNullOrEmpty(oldname))
                    auditDetails += " Contact Number '"+oldname+"' updated to '"+name+"',";
                else
                    auditDetails += " Contact Number '"+name+"' added,";

            if(StringUtil.isNullOrEmpty((String)hm.get("formatid"))==false) {
                KWLDateFormat kdf=(KWLDateFormat)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.KWLDateFormat", (String)hm.get("formatid"));
                if(!kdf.equals(user.getDateFormat()))
                    auditDetails += " Date format updated to '"+kdf.getJavaForm()+"',";
            }
            String diff=null,tzid=null;
            if(StringUtil.isNullOrEmpty((String)hm.get("tzid"))==false){
                timeZone=(KWLTimeZone)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.KWLTimeZone", (String)hm.get("tzid"));
                diff=timeZone.getDifference();
                tzid=timeZone.getTzID();
                if(!timeZone.equals(user.getTimeZone()))
                    auditDetails += " Timezone updated to "+timeZone.getName()+",";
            }
            if(StringUtil.isNullOrEmpty((String)hm.get("aboutuser"))==false){
                name=(String)hm.get("aboutuser");
                oldname = user.getAboutUser();
                if(!name.equals(oldname))
                    if(!StringUtil.isNullOrEmpty(oldname))
                        auditDetails += " About User '"+oldname+"' updated to '"+name+"',";
                    else
                        auditDetails += " About User '"+name+"' added,";
            }

            requestParams.put("username", hm.get("username"));
            requestParams.put("firstName", hm.get("fname"));
            requestParams.put("lastName", hm.get("lname"));
            requestParams.put("emailID", hm.get("emailid"));
            requestParams.put("address", (String)hm.get("address"));
            requestParams.put("callwith", hm.get("callwithid"));
            requestParams.put("timeformat", hm.get("timeformat"));
            requestParams.put("contactNumber", hm.get("contactno"));
            requestParams.put("dateFormat", hm.get("formatid"));
            requestParams.put("timeZone", hm.get("tzid"));
            requestParams.put("aboutUser", hm.get("aboutuser"));
            requestParams.put("image", hm.get("userimage"));
            
            int notificationtype=0;
            if(hm.containsKey("notificationtype") && hm.get("notificationtype").equals("on"))
                notificationtype = 1;
            requestParams.put("notificationtype", notificationtype);
            sessionHandlerImpl.updateNotifyOnFlag(request, notificationtype == 1 ? true : false);
            
            requestParams.put("addUser", false);

            sessionHandlerImpl.updatePreferences(request, null, (StringUtil.isNullOrEmpty((String)hm.get("formatid"))?null:(String)hm.get("formatid")), (StringUtil.isNullOrEmpty((String)hm.get("tzid"))?null:(String)hm.get("tzid")),diff,tzid,true);
            sessionHandlerImpl.updatePreferences(request, null, (StringUtil.isNullOrEmpty((String)hm.get("formatid"))?null:(String)hm.get("formatid")), (StringUtil.isNullOrEmpty((String)hm.get("tzid"))?null:(String)hm.get("tzid")),diff,(StringUtil.isNullOrEmpty((String)hm.get("timeformat"))?null:(String)hm.get("timeformat")));
            
            if(id==null||id.length()<=0){
                //permissionHandlerDAOObj.setDefaultPermissions(1,newuser.getUserID());
                HashMap<String, Object> userRoleParams = new HashMap<String, Object>();
                userRoleParams.put("userid", sessionHandlerImpl.getUserid(request));
                userRoleParams.put("roleid", 4);
                permissionHandlerDAOObj.saveRoleList(userRoleParams);
                User creater= (User)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", sessionHandlerImpl.getUserid(request));
				//String fullnameCreator = (creater.getFirstName() + " " + creater.getLastName()).trim();
                String fullnameCreator = creater.getFirstName();
                if(fullnameCreator!=null && creater.getLastName()!=null) fullnameCreator+=" "+creater.getLastName();

				if (StringUtil.isNullOrEmpty(creater.getFirstName()) && StringUtil.isNullOrEmpty(creater.getLastName())) {
					fullnameCreator = creater.getUserLogin().getUserName();
				}
                String uri = URLUtil.getPageURL(request, Links.loginpageFull);
                String pmsg = String.format(KWLErrorMsgs.msgMailInvite,user.getFirstName(),fullnameCreator, user.getUserLogin().getUserName(), pwd, uri,fullnameCreator);
                String htmlmsg = String.format(KWLErrorMsgs.msgMailInviteUsernamePassword, user.getFirstName(),fullnameCreator, sessionHandlerImplObj.getCompanyName(request), user.getUserLogin().getUserName(),
                            pwd, uri,uri,fullnameCreator);
                try {
                    SendMailHandler.postMail(new String[] { user.getEmailID() },"[Deskera] Welcome to Deskera CRM", htmlmsg, pmsg, creater.getEmailID());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            kmsg = profileHandlerDAOObj.saveUser(requestParams);
            if(kmsg.isSuccessFlag()) {
                jobj.put("msg", messageSource.getMessage("crm.userprofile.updateusersuccessmsg", null, RequestContextUtils.getLocale(request)));//"Profile has been updated successfully.");
                if(!StringUtil.isNullObject(timeZone)) {
                    jobj.put("tzdiff", timeZone.getDifference());
                }
                // create user entry for emails in krawlercrm database
                if(StringUtil.isNullOrEmpty(id)) {
                    mailIntDAOObj.addUserEntryForEmails(sessionHandlerImpl.getUserid(request),user,user.getUserLogin(),pwd,true);
                }
            } else {
                jobj.put("msg", messageSource.getMessage("crm.userprofile.updateuserfailuremsg", null, RequestContextUtils.getLocale(request)));//"Sorry! User information could not be saved successfully. Please try again.");
            }

            if(auditDetails.length()>0) {
                auditDetails = auditDetails.substring(0, auditDetails.length()-1);
                if(!sessionHandlerImpl.getUserid(request).equals(id)){
                    auditDetails += " for user "+user.getFirstName()+" "+user.getLastName()+" ";
                }
                auditTrailDAOObj.insertAuditLog(AuditAction.User_Profile_update, auditDetails, request, id);
            }
            
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn("General exception in saveUser()", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView updateHelpflag(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jobj = new JSONObject();        
        try {
            String companyId = sessionHandlerImpl.getCompanyid(request);
            String companySubdomain = companyDetailsDAOObj.getSubDomain(companyId);
            boolean isDemo = StringUtil.equal(companySubdomain, ConfigReader.getinstance().get("do_not_disable_deskera_tour"));
            if(isDemo){
                jobj.put("msg", messageSource.getMessage("crm.userprofile.updateusersuccessmsg", null, RequestContextUtils.getLocale(request)));//"Profile has been updated successfully");
            }else{
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("userid", request.getParameter("userid"));
                requestParams.put("helpflag", request.getParameter("helpflag"));
                requestParams.put("addUser", false);

                KwlReturnObject kmsg = getProfileHandlerService().updateHelpflag(requestParams);
                if(kmsg.isSuccessFlag()) {
                    jobj.put("msg", messageSource.getMessage("crm.userprofile.updateusersuccessmsg", null, RequestContextUtils.getLocale(request)));//"Profile has been updated successfully.");
                } else {
                    jobj.put("msg", messageSource.getMessage("crm.userprofile.updateuserfailuremsg", null, RequestContextUtils.getLocale(request)));//"Sorry! User information could not be saved successfully. Please try again.");
                }
            }
        } catch (Exception e) {
            logger.warn("General exception in updateHelpflag()", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView getUserofCompany(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            String companyid = sessionHandlerImpl.getCompanyid(request);
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("u.company.companyID");
            filter_params.add(companyid);
            filter_names.add("u.deleteflag");
            filter_params.add(0);
            
            kmsg = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);
            jobj = getUserDetailsJson(kmsg.getEntityList(), request, kmsg.getRecordTotalCount());
        } catch (Exception e) {
            logger.warn("General exception in getUserofCompany()", e);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        //Create transaction
       DefaultTransactionDefinition def = new DefaultTransactionDefinition();
       def.setName("JE_Tx");
       def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
       def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
       TransactionStatus status = txnManager.getTransaction(def);
        try {
            //String platformURL = this.getServletContext().getInitParameter("platformURL");
            String platformURL = ConfigReader.getinstance().get("platformURL");
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("currentpassword", StringUtil.checkForNull(request.getParameter("currentpassword")));
            requestParams.put("changepassword", StringUtil.checkForNull(request.getParameter("changepassword")));
            requestParams.put("platformURL", platformURL);
            requestParams.put("userid", sessionHandlerImpl.getUserid(request));
            requestParams.put("companyid", sessionHandlerImpl.getCompanyid(request));

            kmsg = profileHandlerDAOObj.changePassword(requestParams);
            jobj = (JSONObject) kmsg.getEntityList().get(0);
            jobj.put("success", kmsg.isSuccessFlag());
            txnManager.commit(status);
        } catch (Exception e) {
            logger.warn("General exception in changePassword()", e);
            txnManager.rollback(status);
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
}
