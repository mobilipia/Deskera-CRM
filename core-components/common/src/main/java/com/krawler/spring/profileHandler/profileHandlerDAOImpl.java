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

import com.krawler.common.admin.Company;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.handlers.APICallHandlerService;
import com.krawler.esp.handlers.FileUploadHandler;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.apache.commons.fileupload.FileItem;
import org.springframework.dao.DataAccessException;

/**
 *
 * @author Karthik
 */
public class profileHandlerDAOImpl extends BaseDAO implements profileHandlerDAO {
	private APICallHandlerService apiCallHandlerService;    

    public void setApiCallHandlerService(APICallHandlerService apiCallHandlerService) {
        this.apiCallHandlerService = apiCallHandlerService;
    }
	/* (non-Javadoc)
     * @see com.krawler.spring.profileHandler.profileHandlerDAO#getUserObject(java.lang.String)
     */
    public User getUserObject(String userId) throws ServiceException
    {
        User user = null;
        try 
        {
            user = (User) get(User.class, userId);
        }catch (DataAccessException dae)
        {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getUserObject", dae);
        }
        return user;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.profileHandler.profileHandlerDAO#getUserFullName(java.lang.String)
     */
    public String getUserFullName(String userid) throws ServiceException {
        String name = "";
        List ll = new ArrayList();
        try {
            String SELECT_USER_INFO = "select u.firstName, u.lastName from User as u " +
                    "where u.userID = ?  and u.deleteflag=0 ";
            ll = executeQuery(SELECT_USER_INFO, new Object[]{userid});
            name = profileHandler.getUserFullName(ll);
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getUserFullName", e);
        }
        return name;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.profileHandler.profileHandlerDAO#getUserDetails(java.util.HashMap, java.util.ArrayList, java.util.ArrayList)
     */
    public KwlReturnObject getUserDetails(HashMap<String, Object> requestParams, ArrayList filter_names, ArrayList filter_params) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        int start = 0;
        int limit = 0;
        String serverSearch = "";
        try {
            if (requestParams.containsKey("start") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
                start = Integer.parseInt(requestParams.get("start").toString());
            }
            if (requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("limit").toString())) {
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            if (requestParams.containsKey("ss") && !StringUtil.isNullOrEmpty(requestParams.get("ss").toString())) {
                serverSearch = requestParams.get("ss").toString();
            }
            String SELECT_USER_INFO = "from User u ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            SELECT_USER_INFO += filterQuery;

            if (requestParams.containsKey("email") && requestParams.get("email") != null) {
                SELECT_USER_INFO += " and emailID != '' ";
            }
            if (!StringUtil.isNullOrEmpty(serverSearch)) {
                String[] searchcol = new String[]{"u.firstName","u.lastName"};
                StringUtil.insertParamSearchString(filter_params, serverSearch, 2);
                String searchQuery = StringUtil.getSearchString(serverSearch, "and", searchcol);
                SELECT_USER_INFO +=searchQuery;
            }
            if (requestParams.containsKey("usersList") && requestParams.get("usersList") != null) {
                StringBuffer usersList = (StringBuffer) requestParams.get("usersList");
                SELECT_USER_INFO += "  and u.userID in (" + usersList + ")  order by firstName||' '||lastName ";
            }
            ll = executeQuery(SELECT_USER_INFO, filter_params.toArray());
            dl = ll.size();
            if (requestParams.containsKey("start") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())
                    && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("limit").toString())) {
                ll = executeQueryPaging(SELECT_USER_INFO, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getUserDetails", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.profileHandler.profileHandlerDAO#getAllManagers(java.util.HashMap)
     */
    public KwlReturnObject getAllManagers(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        String companyid = "";
        try {
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            String role = " and ( bitwise_and( roleID , 2 ) = 2 ) ";
            String SELECT_USER_INFO = "from User u where company.companyID=?  and deleteflag=0 " + role;
            ll = executeQuery(SELECT_USER_INFO, new Object[]{companyid});
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.getAllManagers", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.profileHandler.profileHandlerDAO#saveUser(java.util.HashMap)
     */
    public KwlReturnObject saveUser(HashMap<String, Object> requestParams) throws ServiceException {
        String id = "";
        String dateid = "";
        User user = null;
        UserLogin userLogin = null;
        String pwd=null;
        boolean userLoginFlag = false;
        List ll = new ArrayList();
        try {
            if(requestParams.containsKey("addUser") && (Boolean) requestParams.get("addUser")) {
                String userID = UUID.randomUUID().toString();
                if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                    userID = requestParams.get("userid").toString();
                }
                user = new User();
                userLogin=new UserLogin();
                userLogin.setUserID(userID);
                userLogin.setUserName((String)requestParams.get("username"));
                if(requestParams.containsKey("password") && requestParams.get("password") != null) {
                    userLogin.setPassword(requestParams.get("password").toString());
                } else {
                    pwd=authHandler.generateNewPassword();
                    userLogin.setPassword(authHandler.getSHA1(pwd));
                }
                userLoginFlag = true;
                user.setUserLogin(userLogin);
            } else {
                if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                    id = requestParams.get("userid").toString();
                    user = (User) get(User.class, id);
                    if (requestParams.containsKey("userlogin") && requestParams.get("userlogin") != null) {
                        String userLoginId = requestParams.get("userlogin").toString();
                        user.setUserLogin((UserLogin) get(UserLogin.class, userLoginId));
                    }
                }
            }
            if (requestParams.containsKey("dateformat") && requestParams.get("dateformat") != null) {
                dateid = requestParams.get("dateformat").toString();
                user.setDateFormat((KWLDateFormat) get(KWLDateFormat.class, dateid));
            }
            if (requestParams.containsKey("image") && requestParams.get("image") != null) {
                String imageName = requestParams.get("image").toString();
                if(imageName!=null&&imageName.length()>0){
                    try{
                        String fileName=user.getUserID()+FileUploadHandler.getImageExt();
                        user.setImage(com.krawler.common.util.Constants.ImgBasePath+fileName);
                        new FileUploadHandler().uploadImage((FileItem)requestParams.get("image"), fileName,
                                storageHandlerImpl.GetProfileImgStorePath(),100,100,false,false);
                    } catch(Exception e) {
                        
                    }
                }
            }
            if (requestParams.containsKey("firstName") && requestParams.get("firstName") != null) {
                String firstName = requestParams.get("firstName").toString();
                user.setFirstName(firstName);
            }
            if (requestParams.containsKey("lastName") && requestParams.get("lastName") != null) {
                String lastName = requestParams.get("lastName").toString();
                user.setLastName(lastName);
            }
            if (requestParams.containsKey("role") && requestParams.get("role") != null) {
                String role = requestParams.get("role").toString();
                user.setRoleID(role);
            }
            if (requestParams.containsKey("emailID") && requestParams.get("emailID") != null) {
                String emailID = requestParams.get("emailID").toString();
                user.setEmailID(emailID);
            }
            if (requestParams.containsKey("address") && requestParams.get("address") != null) {
                String address = requestParams.get("address").toString();
                user.setAddress(address);
            }
            if (requestParams.containsKey("designation") && requestParams.get("designation") != null) {
                String designation = requestParams.get("designation").toString();
                user.setDesignation(designation);
            }
            if (requestParams.containsKey("contactNumber") && requestParams.get("contactNumber") != null) {
                String contactNumber = requestParams.get("contactNumber").toString();
                user.setContactNumber(contactNumber);
            }
            if (requestParams.containsKey("aboutUser") && requestParams.get("aboutUser") != null) {
                String aboutUser = requestParams.get("aboutUser").toString();
                user.setAboutUser(aboutUser);
            }
            if (requestParams.containsKey("userStatus") && requestParams.get("userStatus") != null) {
                String userStatus = requestParams.get("userStatus").toString();
                user.setUserStatus(userStatus);
            }
            if (requestParams.containsKey("timeZone") && requestParams.get("timeZone") != null) {
                String timeZone = requestParams.get("timeZone").toString();
                user.setTimeZone((KWLTimeZone) get(KWLTimeZone.class, timeZone));
            }
            if (requestParams.containsKey("company") && requestParams.get("company") != null) {
                String company = requestParams.get("company").toString();
                user.setCompany((Company) get(Company.class, company));
            }
            if (requestParams.containsKey("fax") && requestParams.get("fax") != null) {
                String fax = requestParams.get("fax").toString();
                user.setFax(fax);
            }
            if (requestParams.containsKey("alternateContactNumber") && requestParams.get("alternateContactNumber") != null) {
                String alternateContactNumber = requestParams.get("alternateContactNumber").toString();
                user.setAlternateContactNumber(alternateContactNumber);
            }
            if (requestParams.containsKey("phpBBID") && requestParams.get("phpBBID") != null) {
                int phpBBID = Integer.parseInt(requestParams.get("phpBBID").toString());
                user.setPhpBBID(phpBBID);
            }
            if (requestParams.containsKey("panNumber") && requestParams.get("panNumber") != null) {
                String panNumber = requestParams.get("panNumber").toString();
                user.setPanNumber(panNumber);
            }
            if (requestParams.containsKey("ssnNumber") && requestParams.get("ssnNumber") != null) {
                String ssnNumber = requestParams.get("ssnNumber").toString();
                user.setSsnNumber(ssnNumber);
            }
            if (requestParams.containsKey("dateFormat") && requestParams.get("dateFormat") != null) {
                String dateFormat = requestParams.get("dateFormat").toString();
                user.setDateFormat((KWLDateFormat) get(KWLDateFormat.class, dateFormat));
            }
            if (requestParams.containsKey("timeformat") && requestParams.get("timeformat") != null) {
                int timeformat = Integer.parseInt(requestParams.get("timeformat").toString());
                user.setTimeformat(timeformat);
            }
            if (requestParams.containsKey("createdon") && requestParams.get("createdon") != null) {
                Date created = (Date) requestParams.get("createdon");
                user.setCreatedon(created);
            } else {
                user.setCreatedon(new Date());
            }
            if (requestParams.containsKey("updatedon") && requestParams.get("updatedon") != null) {
                Date updatedon = (Date) requestParams.get("updatedon");
                user.setUpdatedon(updatedon);
            } else {
                user.setUpdatedon(new Date());
            }
            if (requestParams.containsKey("deleteflag") && requestParams.get("deleteflag") != null) {
                int deleteflag = Integer.parseInt(requestParams.get("deleteflag").toString());
                user.setDeleteflag(deleteflag);
            }
            if (requestParams.containsKey("helpflag") && requestParams.get("helpflag") != null) {
                int helpflag = Integer.parseInt(requestParams.get("helpflag").toString());
                user.setHelpflag(helpflag);
            }
            if (requestParams.containsKey("callwith") && requestParams.get("callwith") != null) {
                int callwith = Integer.parseInt(requestParams.get("callwith").toString());
                user.setCallwith(callwith);
            }
            if (requestParams.containsKey("userhash") && requestParams.get("userhash") != null) {
                String user_hash = requestParams.get("userhash").toString();
                user.setUser_hash(user_hash);
            }
            
            if (requestParams.containsKey("user_id") && requestParams.get("user_id") != null) {
                Long userId = (Long) requestParams.get("user_id");
                user.setUserId(userId);
            }
            if (requestParams.containsKey("notificationtype") && requestParams.get("notificationtype") != null) {
                Integer notificationType = (Integer) requestParams.get("notificationtype");
                user.setNotificationtype(notificationType);
            }
            if(userLoginFlag) {
                save(userLogin);
            }
            save(user);            
            ll.add(user);
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.saveUser", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, 1);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.profileHandler.profileHandlerDAO#deleteUser(java.lang.String)
     */
    public void deleteUser(String id) throws ServiceException {
        try {
            User u = (User) get(User.class, id);
            if (u.getUserID().equals(u.getCompany().getCreator().getUserID())) {
                throw new Exception("Cannot delete Company Administrator");
            }
            UserLogin userLogin = (UserLogin) get(UserLogin.class, id);
            delete(userLogin);
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.deleteUser", e);
        }
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.profileHandler.profileHandlerDAO#saveUserLogin(java.util.HashMap)
     */
    public void saveUserLogin(HashMap<String, Object> requestParams) throws ServiceException {
        String userLoginId = "";
        UserLogin userLogin = null;
        try {
            if (requestParams.containsKey("userloginid") && requestParams.get("userloginid") != null) {
                userLoginId = requestParams.get("userloginid").toString();
                userLogin = (UserLogin) get(UserLogin.class, userLoginId);
            } else {
                userLogin = new UserLogin();
            }
            userLogin.setLastActivityDate(new Date());
            if (requestParams.containsKey("userName") && requestParams.get("userName") != null) {
                String userName = requestParams.get("userName").toString();
                userLogin.setUserName(userName);
            }
            if (requestParams.containsKey("password") && requestParams.get("password") != null) {
                String password = requestParams.get("password").toString();
                userLogin.setPassword(password);
            }
            saveOrUpdate(userLogin);
        } catch (Exception e) {
            throw ServiceException.FAILURE("profileHandlerDAOImpl.saveUserLogin", e);
        }
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.profileHandler.profileHandlerDAO#changePassword(java.util.HashMap)
     */
    public KwlReturnObject changePassword(HashMap<String, Object> requestParams) throws ServiceException {
        JSONObject jobj = new JSONObject();
        String password = "";
        String userid = "";
        String companyid = "";
        String pwd = "";
        String msg="";
        String platformURL = "";
        List ll = new ArrayList();
        int dl = 0;
        boolean isSuccess = false;
        try {
            if (requestParams.containsKey("currentpassword") && requestParams.get("currentpassword") != null) {
                password = requestParams.get("currentpassword").toString();
            }
            if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                userid = requestParams.get("userid").toString();
            }
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            if (requestParams.containsKey("changepassword") && requestParams.get("changepassword") != null) {
                pwd = requestParams.get("changepassword").toString();
            }
            if (requestParams.containsKey("platformURL") && requestParams.get("platformURL") != null) {
                platformURL = requestParams.get("platformURL").toString();
            }

            if (password == null || password.length() <= 0) {
                msg="Invalid Password";
            } else {
                if (!StringUtil.isNullOrEmpty(platformURL)) {
                    JSONObject userData = new JSONObject();
                    userData.put("pwd", pwd);
                    userData.put("oldpwd", password);
                    userData.put("userid", userid);
                    userData.put("remoteapikey",storageHandlerImpl.GetRemoteAPIKey());
                    String action = "3";
                    JSONObject resObj = apiCallHandlerService.callApp(platformURL, userData, companyid, action, true);
                    if (!resObj.isNull("success") && resObj.getBoolean("success")) {
                        User user = (User) get(User.class, userid);
                        UserLogin userLogin = user.getUserLogin();
                           userLogin.setPassword(pwd);
                            saveOrUpdate(userLogin);
                            msg="Password Changed Successfully";
                            isSuccess = true;
                    } else {
                        if (resObj.has("errorcode") &&  resObj.get("errorcode").equals("e12")) {
                            msg = "This is a Demo account. You do not have sufficient permissions to edit this field.";
                        } else if (resObj.has("errorcode") &&  resObj.get("errorcode").equals("e10")) {
                            msg = "Old password is incorrect. Please try again.";
                        } else {
                            msg = "Error in changing Password";
                        }
                    }
                } else {
                    User user = (User) get(User.class, userid);
                    UserLogin userLogin = user.getUserLogin();
                    String currentpass = userLogin.getPassword();
                    if (StringUtil.equal(password, currentpass)) {
                        userLogin.setPassword(pwd);
                        saveOrUpdate(userLogin);
                        msg = "Password Changed Successfully";
                    } else {
                        msg = "Old password is incorrect. Please try again.";
                    }
                }
            }
            jobj.put("msg", msg);

            ll.add(jobj);
        } catch (Exception e) {
            throw ServiceException.FAILURE("ProfileHandler.changePassword", e);
        }
        return new KwlReturnObject(isSuccess, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.profileHandler.profileHandlerDAO#getUser_hash(java.lang.String)
     */
    public String getUser_hash(String userid) throws ServiceException {
        String res = "";
        try {
            JSONObject resObj = new JSONObject();
            User user = (User) get(User.class, userid);
            resObj.put("userhash", user.getUser_hash());
            resObj.put("subdomain", user.getCompany().getSubDomain());
            res = resObj.toString();
        } catch (Exception e) {
            throw ServiceException.FAILURE("authHandlerDAOImpl.getUser_hash", e);
        }
        return res;
    }
}
