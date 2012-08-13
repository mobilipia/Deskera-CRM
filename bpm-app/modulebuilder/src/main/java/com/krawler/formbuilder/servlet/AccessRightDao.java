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

package com.krawler.formbuilder.servlet;
import javax.servlet.http.HttpServletRequest;
import com.krawler.common.service.ServiceException;
import com.krawler.esp.hibernate.impl.*;
import com.krawler.utils.json.base.JSONArray;


/**
 *
 * @author sagar
 */
public interface AccessRightDao {
    String fetchrolegrdata(HttpServletRequest request)throws ServiceException ;
    String fetchroledata(HttpServletRequest request)throws ServiceException ;
    String fetchAllRoleAuthData(String groupid,HttpServletRequest request)throws ServiceException ;

    String fetchGridColumns(String groupid,HttpServletRequest request)throws ServiceException ;

    void addrolegr(String grname, String grdesc)throws ServiceException;

    void deleteRoleGroup(String groupid)throws ServiceException ;

    void updaterolegr(String groupid, String data, String column)throws ServiceException ;


    String fetchroledata()throws ServiceException;

    void addrole(String rolename,String roledesc, String grid)throws ServiceException;

    void deleteRole(String roleid)throws ServiceException ;

    void updaterole(String roleid,String rolename, String roledesc, String groupid)throws ServiceException ;

     String fetchSingleRoleGrpData(String grpid) throws ServiceException;

      String fetchPermGrpData(HttpServletRequest request) throws ServiceException ;
    String fetchSinglePermGrpData(String permid) throws ServiceException;

    String addPermGrp(String grname) throws ServiceException ;

    void addPermGrp(mb_permgrmaster permgrmaster, mb_reportlist report, pm_taskmaster taskObj) throws ServiceException ;

    int getModPermGrp(String reportid) throws ServiceException ;

    void addPerm(String permname, String grid) throws ServiceException ;

    int getMaxPermid(int grid) throws ServiceException ;

    void updatePerm(String permid, String permname, String groupid) throws ServiceException ;


    void insertPermVal(HttpServletRequest request)throws ServiceException ;
      String deleteUPG(String groupid)throws ServiceException ;

      String fetchSingleRoleGrpDataCopy(String grpid) throws ServiceException;

    String getAllRoles() throws ServiceException ;

    String copyUPG(String originalGrid, String grname, String grdesc, JSONArray jarr)throws ServiceException;

    JSONArray getMBRolePermisionSet(String userid) throws ServiceException;

    JSONArray getMBRealRoleIds(String userid) throws ServiceException;    
}
