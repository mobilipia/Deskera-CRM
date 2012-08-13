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
package com.krawler.crm.migration.bizservice;

import java.util.List;

import com.krawler.common.admin.User;
import com.krawler.crm.migration.dao.UserMigrationDAO;
import com.krawler.crm.users.bizservice.UserService;
import com.krawler.service.SequencerService;

/**
 * @author User
 * 
 */
public class UsersMigrationServiceImpl implements UsersMigrationService
{
    private UserMigrationDAO userMigrationDAO;
    
    private UserService userService;
    
    private SequencerService sequencerService;
    
    public void addUserIdInUsers()
    {
        // get users 
        List<User> users = getUserService().getAllUsers(); 
        // iterate users
        for (User user: users)
        {
            // get sequence value
            if (user.getUserId() == null)
            {
                Long newId = getSequencerService().getNext(MigrationConstants.TABLE_USERS);
                user.setUserId(newId);
            }
        }
        
        getUserService().saveUsers(users);
    }

    /**
     * @return the userMigrationDAO
     */
    public UserMigrationDAO getUserMigrationDAO()
    {
        return userMigrationDAO;
    }

    /**
     * @param userMigrationDAO the userMigrationDAO to set
     */
    public void setUserMigrationDAO(UserMigrationDAO userMigrationDAO)
    {
        this.userMigrationDAO = userMigrationDAO;
    }

    /**
     * @return the userService
     */
    public UserService getUserService()
    {
        return userService;
    }

    /**
     * @param userService the userService to set
     */
    public void setUserService(UserService userService)
    {
        this.userService = userService;
    }

    /**
     * @return the sequencerService
     */
    public SequencerService getSequencerService()
    {
        return sequencerService;
    }

    /**
     * @param sequencerService the sequencerService to set
     */
    public void setSequencerService(SequencerService sequencerService)
    {
        this.sequencerService = sequencerService;
    }

}
