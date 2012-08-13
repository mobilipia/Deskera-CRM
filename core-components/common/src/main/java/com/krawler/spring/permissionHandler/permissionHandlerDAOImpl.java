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
package com.krawler.spring.permissionHandler;

import com.krawler.common.admin.ProjectActivity;
import com.krawler.common.admin.ProjectFeature;
import com.krawler.common.admin.RoleUserMapping;
import com.krawler.common.admin.Rolelist;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.admin.UserPermission;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import com.krawler.common.admin.RoleFeatureMapping;
import com.krawler.common.util.Constants;
import com.krawler.dao.BaseDAO;

/**
 *
 * @author Karthik
 */
public class permissionHandlerDAOImpl extends BaseDAO implements permissionHandlerDAO {

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#getFeatureList()
     */
    public KwlReturnObject getFeatureList() throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String Hql = "select featureID, featureName, displayFeatureName from ProjectFeature";
            ll = executeQuery(Hql);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getFeatureList", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#getRoleList()
     */
    public KwlReturnObject getRoleList() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "from Rolelist order by roleid";
            ll = executeQuery(Hql);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getRoleList", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#getRoleofUser(java.lang.String)
     */
    public KwlReturnObject getRoleofUser(String userid) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select roleId.roleid, roleId.displayrolename, id from RoleUserMapping where userId.userID=?";
            ll = executeQuery(Hql, userid);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getRoleofUser", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#getActivityList()
     */
    public KwlReturnObject getActivityList() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select feature.featureID, activityID, activityName, displayActivityName from ProjectActivity";
            ll = executeQuery(Hql);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getActivityList", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#saveFeatureList(java.util.HashMap)
     */
    public KwlReturnObject saveFeatureList(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String id = requestParams.containsKey("featureid") && requestParams.get("featureid") != null ? requestParams.get("featureid").toString() : "";
            ProjectFeature feature;
            if (!StringUtil.isNullOrEmpty(id)) {
                feature = (ProjectFeature) get(ProjectFeature.class, id);
            } else {
                feature = new ProjectFeature();
            }
            if (requestParams.containsKey("featurename") && requestParams.get("featurename") != null) {
                feature.setFeatureName(requestParams.get("featurename").toString());
            }
            if (requestParams.containsKey("displayfeaturename") && requestParams.get("displayfeaturename") != null) {
                feature.setDisplayFeatureName(requestParams.get("displayfeaturename").toString());
            }
            saveOrUpdate(feature);
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.saveFeatureList", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#saveRoleList(java.util.HashMap)
     */
    public KwlReturnObject saveRoleList(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String id = requestParams.containsKey("roleid") && requestParams.get("roleid") != null ? requestParams.get("roleid").toString() : "";
            String userroleid = requestParams.containsKey("userroleid") && requestParams.get("userroleid") != null ? requestParams.get("userroleid").toString() : "";
            String userid = requestParams.containsKey("userid") && requestParams.get("userid") != null ? requestParams.get("userid").toString() : "";
            Rolelist role;
            RoleUserMapping rum;
            if (!StringUtil.isNullOrEmpty(id)) {
                role = (Rolelist) get(Rolelist.class, id);
            } else {
                role = new Rolelist();
            }
            if (requestParams.containsKey("rolename") && requestParams.get("rolename") != null) {
                role.setRolename(requestParams.get("rolename").toString());
            }
            if (requestParams.containsKey("displayrolename") && requestParams.get("displayrolename") != null) {
                role.setDisplayrolename(requestParams.get("displayrolename").toString());
            }
            saveOrUpdate(role);

            if (!StringUtil.isNullOrEmpty(userroleid)) {
                rum = (RoleUserMapping) get(RoleUserMapping.class, userroleid);
            } else {
                rum = new RoleUserMapping();
            }
            rum.setRoleId(role);
            if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                rum.setUserId((User)get(User.class, requestParams.get("userid").toString()));
            }
            saveOrUpdate(rum);
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.saveRoleList", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#saveUserRole(java.lang.String, com.krawler.common.admin.User)
     */
    public KwlReturnObject saveUserRole(String roleid, User user) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            ll = new ArrayList();
            Rolelist role;
            if (!StringUtil.isNullOrEmpty(roleid)) {
                role = (Rolelist) get(Rolelist.class, roleid);
            } else {
                role = new Rolelist();
            }

            RoleUserMapping rum = new RoleUserMapping();
            rum.setRoleId(role);
            rum.setUserId(user);
            save(rum);
            ll.add(rum);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.saveUserRole", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#deleteUserRole(com.krawler.common.admin.User)
     */
    public KwlReturnObject deleteUserRole(User user) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            ll = new ArrayList();
            String Hql = "from RoleUserMapping where userId = ?";
            ll = executeQuery(Hql, user);
            if (ll.size() > 0) {
                Iterator ite = ll.iterator();
                while(ite.hasNext()) {
                    RoleUserMapping rum = (RoleUserMapping) ite.next();
                    delete(rum);
                }
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.deleteUserRole", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#saveActivityList(java.util.HashMap)
     */
    public KwlReturnObject saveActivityList(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String id = requestParams.containsKey("activityid") && requestParams.get("activityid") != null ? requestParams.get("activityid").toString() : "";
            ProjectActivity activity;
            ProjectFeature feature = null;
            if (!StringUtil.isNullOrEmpty(id)) {
                activity = (ProjectActivity) get(ProjectActivity.class, id);
            } else {
                activity = new ProjectActivity();
                feature = (ProjectFeature) get(ProjectFeature.class, requestParams.get("featureid").toString());
                activity.setFeature(feature);
            }
            if (requestParams.containsKey("activityname") && requestParams.get("activityname") != null) {
                activity.setActivityName(requestParams.get("activityname").toString());
            }
            if (requestParams.containsKey("displayactivityname") && requestParams.get("displayactivityname") != null) {
                activity.setDisplayActivityName(requestParams.get("displayactivityname").toString());
            }
            saveOrUpdate(activity);
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.saveActivityList", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#deleteFeature(java.util.HashMap)
     */
    public KwlReturnObject deleteFeature(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String id = requestParams.containsKey("featureid") && requestParams.get("featureid") != null ? requestParams.get("featureid").toString() : "";
            ProjectFeature feature;
            if (!StringUtil.isNullOrEmpty(id)) {
                feature = (ProjectFeature) get(ProjectFeature.class, id);
                delete(feature);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.deleteFeature", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#deleteRole(java.util.HashMap)
     */
    public KwlReturnObject deleteRole(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        String Hql = "";
        Rolelist role = null;
        String msg = "";
        try {
            String id = requestParams.containsKey("roleid") && requestParams.get("roleid") != null ? requestParams.get("roleid").toString() : "";
            if (!StringUtil.isNullOrEmpty(id)) {
                role = (Rolelist) get(Rolelist.class, id);
                Hql = "from RoleUserMapping where roleId=?";
                ll = executeQuery(Hql, role);
                if (ll.size() > 0) {
                    msg = "Role cannot be deleted as it is assigned to user(s)";
                } else {
                    delete(role);
                    msg = "Role deleted successfully";
                }
            }
            ll = new ArrayList();
            ll.add(msg);
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.deleteRole", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#deleteActivity(java.util.HashMap)
     */
    public KwlReturnObject deleteActivity(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String id = requestParams.containsKey("activityid") && requestParams.get("activityid") != null ? requestParams.get("activityid").toString() : "";
            ProjectActivity activity;
            if (StringUtil.isNullOrEmpty(id)) {
                activity = (ProjectActivity) get(ProjectActivity.class, id);
                delete(activity);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.deleteActivity", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#getActivityFeature()
     */
    public KwlReturnObject getActivityFeature() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select pf, pa from ProjectActivity pa right outer join pa.feature pf order by activityID";
            ll = executeQuery(Hql);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getActivityFeature", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#getUserPermission(java.util.HashMap)
     */
    public KwlReturnObject getUserPermission(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            String userid = requestParams.containsKey("userid") && requestParams.get("userid") != null ? requestParams.get("userid").toString() : "";
            String roleid = requestParams.containsKey("roleid") && requestParams.get("roleid") != null ? requestParams.get("roleid").toString() : "";
            String Hql = " select feature.featureName, permissionCode, feature.featureID from UserPermission up where roleId.userId.userID=? ";
            filter_params.add(userid);

            if(!StringUtil.isNullOrEmpty(roleid)) {
                Hql += " and roleId.roleId.roleid=? ";
                filter_params.add(roleid);
            }
            if(requestParams.containsKey("filter_names")){
                filter_names = (ArrayList) requestParams.get("filter_names");
            }
            if(requestParams.containsKey("filter_params")){
                filter_params.addAll((ArrayList) requestParams.get("filter_params"));
            }
            String filterQuery = StringUtil.filterQuery(filter_names, "and");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            Hql += filterQuery;
            
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.getUserPermission", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#isSuperAdmin(java.lang.String, java.lang.String)
     */
    public boolean isSuperAdmin(String userid, String companyid) throws ServiceException {
        boolean admin = false;
        try {
            // Hardcoded id of admin user and admin company.
            if (userid.equals("ff808081227d4f5801227d535ebb0009") && companyid.equals("ff808081227d4f5801227d535eba0008")) {
                admin = true;
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.isSuperAdmin", e);
        }
        return admin;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#setPermissions(java.util.HashMap, java.lang.String[], java.lang.String[])
     */
    public KwlReturnObject setPermissions(HashMap<String, Object> requestParams, String[] features, String[] permissions) throws ServiceException {
        List ll = null;
        int dl = 0;
        String rid = "";
        String permAuditMsg = "";
        try {
            String id = requestParams.containsKey("userid") && requestParams.get("userid") != null ? requestParams.get("userid").toString() : "";
            String roleId = requestParams.containsKey("roleid") && requestParams.get("roleid") != null ? requestParams.get("roleid").toString() : "";

            String Hql = "select id from RoleUserMapping where userId.userID=? ";
            ll = executeQuery(Hql, id);

            rid = ll.get(0).toString();
            RoleUserMapping rum = (RoleUserMapping) get(RoleUserMapping.class, rid);
            rum.setRoleId((Rolelist)get(Rolelist.class, roleId));
            save(rum);

            User user = (User) get(User.class, id);
            user.setRoleID(roleId);
            save(user);

            HashMap<ProjectFeature, Long> featurePermCodes = new HashMap<ProjectFeature, Long>();
            Hql = "select u.permissionCode, u.feature from UserPermission u where roleId=?";
            List<Object[]> ll2 = executeQuery(Hql, rum);
            for(Object[] row : ll2) {
                long permissionCode = Long.parseLong(row[0].toString());
                ProjectFeature pfeature = (ProjectFeature) row[1];
                featurePermCodes.put(pfeature, permissionCode);
            }

            Hql = "delete from UserPermission where roleId=?";
            executeUpdate(Hql, rum);

            for (int i = 0; i < features.length; i++) {
                if (permissions[i].equals("0")) {
                    continue;
                }
                ProjectFeature pfeature = (ProjectFeature) get(ProjectFeature.class, features[i]);
                long newPermCode = Long.parseLong(permissions[i]);
                if(featurePermCodes.containsKey(pfeature) && newPermCode != featurePermCodes.get(pfeature)) {
                    permAuditMsg += "<BR/> Feature Name ("+ pfeature.getDisplayFeatureName() + ") -> Old permission code : "+ featurePermCodes.get(pfeature) + ", New permission code : "+ newPermCode;
                }
                UserPermission permission = new UserPermission();
                permission.setRoleId(rum);
                permission.setFeature(pfeature);
                permission.setPermissionCode(newPermCode);
                save(permission);
            }
            UserLogin userLogin = (UserLogin)get(UserLogin.class, id);
            ll = new ArrayList();
            ll.add(userLogin);
            ll.add(permAuditMsg);
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.setPermissions", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#setDefaultPermissions(int, java.lang.String)
     */
    public KwlReturnObject setDefaultPermissions(int flag, String userid)
            throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            //UserLogin ulog = (UserLogin) session.get(UserLogin.class, userid);
            String userroleid = "";
            RoleUserMapping roleUserMapObj = null;
            KwlReturnObject kwlrole = getRoleofUser(userid);
            Iterator itrDomain = kwlrole.getEntityList().iterator();
            if (itrDomain.hasNext()) {
                Object[] roleInfo = (Object[]) itrDomain.next();
                userroleid = roleInfo[3].toString();
            }
            roleUserMapObj = (RoleUserMapping) get(RoleUserMapping.class,userroleid);
            String query = "from ProjectFeature ";
            List list = executeQuery(query);
            Iterator ite = list.iterator();
            while (ite.hasNext()) {
                ProjectFeature projectFeature = (ProjectFeature) ite.next();
                int actsize = projectFeature.getActivities().size();
                int permcode=0;
                if(flag==1 || flag==0){     // admin
                    permcode = (int) Math.pow(2, actsize) -1 ;
                }
                if(flag==2){     // manager
                    permcode = (int) Math.pow(2, actsize);
                }
                if(flag==4){     // employee
                    permcode = 1;
                }
                UserPermission uperm = new UserPermission();
                uperm.setFeature(projectFeature);
                uperm.setPermissionCode(permcode);
                uperm.setRoleId(roleUserMapObj);
                save(uperm);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.setDefaultPermissions", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", null, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.permissionHandler.permissionHandlerDAO#setDefaultPermissions(int, com.krawler.common.admin.RoleUserMapping)
     */
    public KwlReturnObject setDefaultPermissions(int flag, RoleUserMapping roleUserObj)
            throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String rolesid = "";
            String query = "from ProjectFeature ";
            List list = executeQuery(query);
            Iterator ite = list.iterator();
            while (ite.hasNext()) {
                ProjectFeature projectFeature = (ProjectFeature) ite.next();
                String featureName = projectFeature.getFeatureName();
                String featuresID = projectFeature.getFeatureID();
                int permcode = 0;
                if (flag == 1 || flag==0) {     // admin
                    String rolequery = "from Rolelist where rolename='"+Constants.ROLENAME_ADMIN+"' ";
                    List lists = executeQuery(rolequery);
                    Iterator iter = lists.iterator();
                    while (iter.hasNext()) {
                        Rolelist rl = (Rolelist) iter.next();
                        rolesid = rl.getRoleid();
                    }

                    String rolefeaturequery = "from RoleFeatureMapping";
                    List listss = executeQuery(rolefeaturequery);
                    Iterator iter2 = listss.iterator();
                    while (iter2.hasNext()) {
                        RoleFeatureMapping rfm = (RoleFeatureMapping) iter2.next();
                        com.krawler.common.admin.Rolelist rfmroleid = rfm.getRoleid();
                        String rfmrid = rfmroleid.getRoleid();
                        com.krawler.common.admin.ProjectFeature rfmfeatureid = rfm.getFeatureID();
                        String rfmfeature = rfmfeatureid.getFeatureID();
                        if (rfmrid.equals(rolesid) && rfmfeature.equals(featuresID)) {
                            permcode = rfm.getPermissioncode();
                        }
                    }
                }
                if (flag == 2) {     // manager
                   String rolequery = "from Rolelist where rolename='"+Constants.ROLENAME_MANAGER+"' ";
                    List lists = executeQuery(rolequery);
                    Iterator iter = lists.iterator();
                    while (iter.hasNext()) {
                        Rolelist rl = (Rolelist) iter.next();
                        rolesid = rl.getRoleid();
                    }

                    String rolefeaturequery = "from RoleFeatureMapping";
                    List listss = executeQuery(rolefeaturequery);
                     Iterator iter2 = listss.iterator();
                    while (iter2.hasNext()) {
                        RoleFeatureMapping rfm = (RoleFeatureMapping) iter2.next();
                        com.krawler.common.admin.Rolelist rfmroleid = rfm.getRoleid();
                        String rfmrid = rfmroleid.getRoleid();
                        com.krawler.common.admin.ProjectFeature rfmfeatureid = rfm.getFeatureID();
                        String rfmfeature = rfmfeatureid.getFeatureID();
                        if (rfmrid.equals(rolesid) && rfmfeature.equals(featuresID)) {
                            permcode = rfm.getPermissioncode();
                        }
                    }
                }
                if (flag == 4) {     // employee
                   String rolequery = "from Rolelist where rolename='"+Constants.ROLENAME_EMPLOYEE+"' ";
                    List lists = executeQuery(rolequery);
                    Iterator iter = lists.iterator();
                    while (iter.hasNext()) {
                        Rolelist rl = (Rolelist) iter.next();
                        rolesid = rl.getRoleid();
                    }

                    String rolefeaturequery = "from RoleFeatureMapping";
                    List listss = executeQuery(rolefeaturequery);
                     Iterator iter2 = listss.iterator();
                    while (iter2.hasNext()) {
                        RoleFeatureMapping rfm = (RoleFeatureMapping) iter2.next();
                        com.krawler.common.admin.Rolelist rfmroleid = rfm.getRoleid();
                        String rfmrid = rfmroleid.getRoleid();
                        com.krawler.common.admin.ProjectFeature rfmfeatureid = rfm.getFeatureID();
                        String rfmfeature = rfmfeatureid.getFeatureID();
                        if (rfmrid.equals(rolesid) && rfmfeature.equals(featuresID)) {
                            permcode = rfm.getPermissioncode();
                        }
                    }
                }
                UserPermission uperm = new UserPermission();
                uperm.setFeature(projectFeature);
                uperm.setPermissionCode(permcode);
                uperm.setRoleId(roleUserObj);
                save(uperm);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("permissionHandlerDAOImpl.setDefaultPermissions", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", null, dl);
    }

}
