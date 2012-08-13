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

package com.krawler.profileHandler.bizservice;

import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import java.util.HashMap;

/**
 *
 * @author sagar
 */
public class ProfileHandlerServiceImpl implements ProfileHandlerService {

    private profileHandlerDAO profileHandlerDAOObj;
    private static final Log LOGGER = LogFactory.getLog(ProfileHandlerServiceImpl.class);

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    @Override
    public KwlReturnObject updateHelpflag(HashMap requestParams) {
        KwlReturnObject kmsg = null;
        try {
            kmsg = profileHandlerDAOObj.saveUser(requestParams);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            System.out.println(e.getMessage());
        }
        return kmsg;
    }

}
