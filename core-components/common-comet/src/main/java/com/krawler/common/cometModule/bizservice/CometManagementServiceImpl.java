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

package com.krawler.common.cometModule.bizservice;

import java.util.HashMap;
import com.krawler.common.comet.CrmPublisherDAO;
import com.krawler.common.comet.ServerEventManager;
import com.krawler.utils.json.base.JSONObject;
import javax.servlet.ServletContext;

/**
 *
 * @author sm
 */
public class CometManagementServiceImpl implements CometManagementService{
    private CrmPublisherDAO crmPublisherDAOObj;
    
    public void setCrmPublisherDAO(CrmPublisherDAO crmPublisherDAOObj) {
        this.crmPublisherDAOObj = crmPublisherDAOObj;
    }

    public void initBayeuxVariable(ServletContext context) {
       crmPublisherDAOObj.initBayeuxVariable(context);
    }
    

    @Override
    public void publishModuleInformation(StringBuffer usersList, StringBuffer manUserList, String ownerId, String moduleName,String moduleRecId, Integer updateId, JSONObject jobj, Integer channelId) {
        crmPublisherDAOObj.publishModuleInformation(usersList, manUserList, ownerId, moduleName, moduleRecId, updateId, jobj, channelId);
    }

    @Override
    public void publishModuleInformation(StringBuffer usersList, String ownerId, String moduleName,String moduleRecId, Integer updateId, JSONObject jobj, Integer channelId) {
        crmPublisherDAOObj.publishModuleInformation(usersList, ownerId, moduleName, moduleRecId, updateId, jobj, channelId);
    }

    @Override
    public void publishModuleInformation(String companyId, String ownerId, String randomNumber, String moduleName,String moduleRecId, Integer updateId, JSONObject jobj, Integer channelId) {
        crmPublisherDAOObj.publishModuleInformation(companyId, ownerId, randomNumber, moduleName, moduleRecId, updateId, jobj, channelId);
    }

    @Override
    public void publishModuleInformation(StringBuffer usersList, String companyId, String ownerId, String randomNumber, String moduleName,String moduleRecId, Integer updateId, JSONObject jobj, Integer channelId) {
        crmPublisherDAOObj.publishModuleInformation(usersList, companyId, ownerId, randomNumber, moduleName, moduleRecId, updateId, jobj, channelId);
    }

	
	private String constructChannelPath(String[] channelParts) {
		StringBuffer sb = new StringBuffer();
		for (String part : channelParts) {
			if(!(part==null||part.trim().equals("")))
				sb.append('/').append(part.trim());
		}
		if(sb.equals(""))
			throw new IllegalArgumentException("Channel path can not be blank");
		return sb.toString();
	}

	@Override
	public void publishInformation(HashMap<String, Object> info, String[] channelParts) {
		ServerEventManager.publish(constructChannelPath(channelParts), new JSONObject(info));
	}
}
