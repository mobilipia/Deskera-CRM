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
import com.krawler.common.admin.Country;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.RoleUserMapping;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmCombodata;
import com.krawler.crm.database.tables.CrmCombomaster;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.DefaultTemplates;
import com.krawler.crm.database.tables.EmailTemplate;
import com.krawler.crm.utils.Constants;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.hibernate.impl.mb_rolemaster;
import com.krawler.esp.hibernate.impl.userrolemapping;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.companyDetails.companyDetailsDAO;
import com.krawler.spring.mailIntegration.mailIntegrationController;
import com.krawler.spring.permissionHandler.permissionHandlerDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author krawler-user
 */
public class RemoteAPIDAOImpl extends BaseDAO implements RemoteAPIDAO {
    private companyDetailsDAO companyDetailsDAOObj;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private permissionHandlerDAO permissionHandlerDAOObj;
    private crmManagerDAO crmManagerDAOObj;
    private crmCommonDAO crmCommonDAOObj;
    private mailIntegrationController mailIntDAOObj;

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj;
    }

    public void setCompanyDetailsDAO(companyDetailsDAO companyDetailsDAOObj) {
        this.companyDetailsDAOObj = companyDetailsDAOObj;
    }

    public void setCrmCommonDAO(crmCommonDAO crmCommonDAOObj) {
        this.crmCommonDAOObj = crmCommonDAOObj;
    }

    public void setCrmManagerDAO(crmManagerDAO crmManagerDAOObj) {
        this.crmManagerDAOObj = crmManagerDAOObj;
    }

    public void setMailIntDAO(mailIntegrationController mailIntDAOObj) {
        this.mailIntDAOObj = mailIntDAOObj;
    }

    public void setPermissionHandlerDAO(permissionHandlerDAO permissionHandlerDAOObj) {
        this.permissionHandlerDAOObj = permissionHandlerDAOObj;
    }

    /**
     * Create company with passed company name and company created user
     * @param session A current transaction session
     * @param request A HttpServletRequest
     * @param id Unique username
     * @param password User password
     * @param emailid User emailid
     * @param fname User's name
     * @return JSON with success/failure response.
     * @throws ServiceException
     */

    @Override
    public String signupCompany(String companyid, String userid, String id, String password, String emailid, String companyname,
        String fname, String subdomain, String lname, int action, Long companyId, Long userId) throws ServiceException {
        String result = "failure";
        KwlReturnObject kmsg = null;
        try {
            Company company = null;
            User user;
            UserLogin userLogin;
            HashMap<String, Object> requestParams = new HashMap<String, Object>();

            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("c.subDomain");
            filter_params.add(subdomain);
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_params);
            kmsg = companyDetailsDAOObj.getCompanyInformation(requestParams);

//            String queryDomain = "from Company where subDomain =? ";
//            List listDomain = HibernateUtil.executeQuery(session, queryDomain, subdomain);
            Iterator itrDomain = kmsg.getEntityList().iterator();

            if (itrDomain.hasNext()) {
                // rename company's invalid subdomain
                Company oldcompany = (Company) itrDomain.next();

                requestParams.clear();
                requestParams.put("companyid", oldcompany.getCompanyID());
                requestParams.put("domainname", "old_"+oldcompany.getSubDomain());
                companyDetailsDAOObj.updateCompany(requestParams);
//                result = getMessage(2, 8);
//                return result;
            }
            if (userid != null && userid.length() > 0) {
                user = (User)KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", userid);
                if (user != null) {
                    return getMessage(2, 7, action);
                }
            }

            company = (Company) KwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.Company", companyid);
            if (company != null) {
                return getMessage(2, 8,action);
            }

            company = new Company();
            company.setCompanyID(companyid);

            company.setAddress("");
            company.setCompanyId(companyId);
            company.setDeleted(0);
            company.setSubDomain(subdomain);
            company.setCompanyName(companyname);
            company.setEmailID(emailid);
            company.setActivated(true);
            company.setCountry((Country) get(Country.class, "244"));
            company.setTimeZone((KWLTimeZone)get(KWLTimeZone.class, DEFAULTTIMEZONE));
            company.setCreatedOn(new Date());
            company.setModifiedOn(new Date());
            company.setCreatedon(new Date().getTime());
            company.setModifiedon(new Date().getTime());

//            HashMap<String, Object> companyRequestParams = new HashMap<String, Object>();
//            companyRequestParams.put("addCompany", true);
//            companyRequestParams.put("address", "");
//            companyRequestParams.put("deleteflag", 0);
//            companyRequestParams.put("companyid", companyid);
//            companyRequestParams.put("domainname", subdomain);
//            companyRequestParams.put("companyname", companyname);
//            companyRequestParams.put("country", 244);
//            companyRequestParams.put("mail", emailid);
//            companyRequestParams.put("timezone", DEFAULTTIMEZONE);
//            companyRequestParams.put("createdon", new Date());
//            companyRequestParams.put("modifiedon", new Date());
//            companyDetailsDAOObj.updateCompany(companyRequestParams);

            user = new User();
            userLogin = new UserLogin();
            userLogin.setUserID(userid);

            userLogin.setUser(user);
            userLogin.setUserName(id);
            userLogin.setPassword(password);

            user.setUserId(userId);
            user.setUserLogin(userLogin);
            user.setFirstName(fname);
            user.setLastName(lname);
            user.setRoleID(Constants.COMPANY_ADMIN);
            user.setEmailID(emailid);
            user.setCompany(company);
            KWLDateFormat kdf = (KWLDateFormat)get(KWLDateFormat.class, DEFAULTDATEFORMATZONE);
            user.setDateFormat(kdf);

//            HashMap<String, Object> userRequestParams = new HashMap<String, Object>();
//            userRequestParams.put("addUser", true);
//            userRequestParams.put("password", password);
//            userRequestParams.put("userid", userid);
//            userRequestParams.put("company", companyid);
//            userRequestParams.put("username", id);
//            userRequestParams.put("firstName", fname);
//            userRequestParams.put("lastName", lname);
//            userRequestParams.put("emailID", emailid);
//            userRequestParams.put("role", Constants.COMPANY_ADMIN);
//            userRequestParams.put("dateFormat", DEFAULTDATEFORMATZONE);
//            kmsg = profileHandlerDAOObj.saveUser(userRequestParams);
//            user = (User) kmsg.getEntityList().get(0);

            company.setCreator(user);
            save(company);
            save(userLogin);
            save(user);
//            companyRequestParams.clear();
//            companyRequestParams.put("companyid", companyid);
//            companyRequestParams.put("creater", userid);
//            companyRequestParams.put("addCompany", false);
//            kmsg = companyDetailsDAOObj.updateCompany(companyRequestParams);
//            company = (Company) kmsg.getEntityList().get(0);

            //@@@ - Need to uncomment
            copyMasterItems(company);
            //copyDefaultTemplates(session,user);
            
            crmManagerDAOObj.saveDefaultCaseOwner(company.getCompanyID(), company.getCreator().getUserID());
            CompanyPreferences cmpPref=new CompanyPreferences();
            cmpPref.setCompany(company);
            save(cmpPref);

//            HashMap<String, Object> compPrefRequestParams = new HashMap<String, Object>();
//            compPrefRequestParams.put("companyid", companyid);
//            kmsg = crmManagerDAOObj.setCompanyPref(compPrefRequestParams);

            // Assign Admin Role
            RoleUserMapping roleUserMapObj = null;
            HashMap<String, Object> userroleReqParams = new HashMap<String, Object>();
            userroleReqParams.put("roleid", Constants.COMPANY_ADMIN);
            userroleReqParams.put("userid", user.getUserID());
            kmsg = permissionHandlerDAOObj.saveUserRole(Constants.COMPANY_ADMIN, user);
            Iterator ite2 = kmsg.getEntityList().iterator();
            if (ite2.hasNext()) {
                roleUserMapObj = (RoleUserMapping) ite2.next();
            }
             crmCommonDAOObj.createDefaultMapping(companyid);
            // Assign Permission
            permissionHandlerDAOObj.setDefaultPermissions(1,roleUserMapObj);
            try {
                mailIntDAOObj.addUserEntryForEmails(user.getUserID(),user,user.getUserLogin(),password,false);
            } catch(ServiceException ex) {
                logger.warn("Error while creating user entry in mail engine : "+ex.getMessage(), ex);
                System.out.println("For User : ");
                System.out.println("Userid : "+user.getUserID());
                System.out.println("Username : "+user.getUserLogin().getUserName());
                System.out.println("password : "+user.getUserLogin().getPassword());
                System.out.println("Companyid : "+user.getCompany().getCompanyID());
            }
            result = "success";
        } catch (ServiceException e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false,\"action\":'"+action+"',\"errormsg:\" \"Error while signing up user:'"+e.getMessage()+"' \"}";
            throw ServiceException.FAILURE("remoteApi.signupCompany", e);
        }
        return result;
    }

     public KwlReturnObject getUpdatesAudit(String userid, int start, int limit) throws ServiceException {

            StringBuffer usersList = crmManagerDAOObj.recursiveUsers(userid);
            Map map = getPerm(userid);
            String groups="";
            for(Object o:map.entrySet()) {
                Entry e = (Entry)o;
                String keyName = (String)e.getKey();
                int perm = (Integer)e.getValue();
                if ((perm & 1) == 1) {
                    groups += "'"+keyName+"',";
                }
            }
            if(groups.length() > 0) {
                groups = groups.substring(0, groups.length() - 1);
            }

           int interval = 7;
           /* get audit action, details for user. with permission and also audit of users under him  */
           String query = "from AuditTrail at where at.user.userID in ("+usersList+")  and DATEDIFF(date(now()),date(FROM_UNIXTIME(at.audittime/1000))) <= ? and "  +
                   "at.action.auditGroup.groupName in ("+groups+") order by at.audittime desc";
           int listCount = executeQuery(query, new Object[]{interval}).size();
           List list = executeQueryPaging(query, new Object[]{interval}, new Integer[]{start, limit});
        return new KwlReturnObject(true, "", "", list, listCount);
    }
     
    public Map getPerm(String userid) throws ServiceException {
        Map map = new HashMap();
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("userid", userid);
        KwlReturnObject kmsg = permissionHandlerDAOObj.getUserPermission(requestParams);

        List ll = kmsg.getEntityList();
        Iterator ite = ll.iterator();
        while(ite.hasNext()) {
            Object[] roww = (Object[]) ite.next();
            map.put(roww[0].toString(), Integer.parseInt(roww[1].toString()));
        }
        return map;
    }

    public String CompanyDelete(String companyid, int action) throws ServiceException {
        String result = "{\"success\":false}";

        if (companyid==null) {
            return getMessage(2, 1, action);
        }
        String query = "update Company c set c.deleted=1 where c.companyID= ?";
        int count = executeUpdate(query, companyid);
        if (count > 0) {
            result = "{\"success\":true, 'msg': 'Company deleted successfully.'}";
        } else {
            result = getMessage(2, 4, action);
        }
        return result;
    }

    @Override
    public String getMessage(int type, int mode, int action) {
        String r = "";
        String temp = "";
        switch (type) {
            case 1:     // success messages
                temp = "m" + String.format("%02d", mode);
                r = "{\"success\": true, \"infocode\": \"" + temp + "\", \"action\": \"" + action + "\" }";
                break;
            case 2:     // error messages
                temp = "e" + String.format("%02d", mode);

                r = "{\"success\": false, \"errorcode\": \"" + temp + "\", \"action\": \"" + action + "\"}";
                break;
        }
        return r;
    }

    private String copyMasterItems(Company company) throws ServiceException {
        String result = "";
         try {
             String query = " from CrmCombodata where valueid not in ( select campaignid from CrmCampaign ) ";
             List list = executeQuery(query);
             Iterator iter = list.iterator();
             while (iter.hasNext()) {
                 CrmCombodata crmCombodata = (CrmCombodata) iter.next();
                 DefaultMasterItem defaultMasterItem = new DefaultMasterItem();

                 String id = java.util.UUID.randomUUID().toString();

                 defaultMasterItem.setID(id);
                 defaultMasterItem.setValue(crmCombodata.getRawvalue());
                 defaultMasterItem.setCrmCombodata(crmCombodata);
                 defaultMasterItem.setMainID(crmCombodata.getValueid());
                 defaultMasterItem.setCrmCombomaster((CrmCombomaster) get(CrmCombomaster.class, crmCombodata.getCrmCombomaster().getMasterid()));
                 defaultMasterItem.setCompany(company);
                 defaultMasterItem.setIsEdit(crmCombodata.isIsEdit());
                 defaultMasterItem.setPercentStage(crmCombodata.getPercentStage());
                 save(defaultMasterItem);
             }
         } catch (Exception ex) {
             logger.warn(ex.getMessage(), ex);
             throw ServiceException.FAILURE("remoteAPIController.copyMasterItems", ex);
         }
         return result;
    }

    private String copyDefaultTemplates(User user) throws ServiceException {
        String result = "";
         try {
             String query = "from DefaultTemplates";
             List list = executeQuery(query);
             Iterator iter = list.iterator();
             while (iter.hasNext()) {
                 DefaultTemplates defaultTemplates = (DefaultTemplates) iter.next();
                 EmailTemplate emailTemplate = new EmailTemplate();

                 emailTemplate.setBody(defaultTemplates.getBody());
                 emailTemplate.setBody_html(defaultTemplates.getBody_html());
                 emailTemplate.setCreatedon(new Date());
                 emailTemplate.setCreator(user);
                 emailTemplate.setDeleted(0);
                 emailTemplate.setDescription(defaultTemplates.getDescription());
                 emailTemplate.setName(defaultTemplates.getName());
                 emailTemplate.setSubject(defaultTemplates.getSubject());
                 emailTemplate.setThumbnail(defaultTemplates.getThumbnail());
                 save(emailTemplate);
             }
         } catch (Exception ex) {
             logger.warn(ex.getMessage(), ex);
             throw ServiceException.FAILURE("remoteApi.copyMasterItems", ex);
         }
         return result;
    }

    @Override
    public String saveMB_userRoleMapping(User userObj, String roleid) throws ServiceException {
        String result = "failure";
        try {
            String query = "select max(id) as id from userrolemapping";
            List list = executeNativeQuery(query);
            Iterator iter = list.iterator();
            int id = 0;
            if (iter.hasNext()) {
                 id = (Integer) iter.next();
            }
            id++;

            userrolemapping userrole = new userrolemapping();
            userrole.setId(id);
            userrole.setRoleid((mb_rolemaster) get(mb_rolemaster.class, Integer.parseInt(roleid)));
            userrole.setUserid(userObj);
            save(userrole);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("remoteApi.mb_userRoleMapping", e);
        }
        return result;
    }

    public int executeNativeUpdateForDeleteCompany(String query, Object[] Params) {
        return updateJDBC(query, Params);
    }

	@Override
	public void changeCompanyCreator(User user, Company company) throws ServiceException {
		try{
			company.setCreator(user);
			crmManagerDAOObj.saveDefaultCaseOwner(company.getCompanyID(), user.getUserID());
			save(company);
		}catch(Exception ex){
			logger.warn("Company creator could not change due to "+ex.getMessage());
			throw ServiceException.FAILURE("remoteApi.changeCompanyCreator", ex);
		}
	}
}
