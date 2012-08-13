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
import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;

/**
 *
 * @author krawler-user
 */
public interface RemoteAPIDAO {
    String DEFAULTTIMEZONE="23";
    String DEFAULTDATEFORMATZONE="18";
    String signupCompany(String companyid, String userid, String id, String password, String emailid, String companyname, String fname, String subdomain, String lname, int action, Long companyId, Long userId) throws ServiceException;
    KwlReturnObject getUpdatesAudit(String userid, int start, int limit) throws ServiceException;
    String getMessage(int type, int mode, int action);
    String saveMB_userRoleMapping(User userObj, String roleid) throws ServiceException;
    int executeNativeUpdateForDeleteCompany(String query, Object[] Params);
    void changeCompanyCreator(User user,Company company) throws ServiceException;
}
