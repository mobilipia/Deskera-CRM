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
package com.krawler.common.comet;

import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

public class CrmPublisherDAOImpl implements  CrmPublisherDAO {

    public void initBayeuxVariable(ServletContext context) {
        ServerEventManager.initBayeuxVariable(context);
    }
    @Override
    public void publishModuleInformation(StringBuffer usersList, StringBuffer manUserList, String ownerId, String moduleName,String moduleRecId, Integer updateId, JSONObject jobj, Integer channelId, ServletContext sc) {
        try {
            JSONObject cometJsonObj = new JSONObject();
            cometJsonObj.append("data", jobj);
            cometJsonObj.put("moduleName", moduleName);
            cometJsonObj.put("operationcode", updateId);
            cometJsonObj.put("moduleRecId", moduleRecId);
            cometJsonObj.put("usersList", usersList);
            cometJsonObj.put("manUsersList", manUserList);
            cometJsonObj.put("owner", ownerId);
            ServerEventManager.publish(CometConstants.channelMap.get(channelId), cometJsonObj, sc);
        } catch (JSONException ex) {
            Logger.getLogger(CrmPublisherDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void publishModuleInformation(StringBuffer usersList, String ownerId, String moduleName,String moduleRecId, Integer updateId, JSONObject jobj, Integer channelId, ServletContext sc) {
        try {
            JSONObject cometJsonObj = new JSONObject();
            cometJsonObj.append("data", jobj);
            cometJsonObj.put("moduleName", moduleName);
            cometJsonObj.put("operationcode", updateId);
            cometJsonObj.put("moduleRecId", moduleRecId);
            cometJsonObj.put("usersList", usersList);
            cometJsonObj.put("owner", ownerId);
            ServerEventManager.publish(CometConstants.channelMap.get(channelId), cometJsonObj, sc);
        } catch (JSONException ex) {
            Logger.getLogger(CrmPublisherDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void publishModuleInformation(StringBuffer usersList, StringBuffer manUserList, String ownerId, String moduleName,String moduleRecId, Integer updateId, JSONObject jobj, Integer channelId) {
        try {
            JSONObject cometJsonObj = new JSONObject();
            cometJsonObj.append("data", jobj);
            cometJsonObj.put("moduleName", moduleName);
            cometJsonObj.put("operationcode", updateId);
            cometJsonObj.put("moduleRecId", moduleRecId);
            cometJsonObj.put("usersList", usersList);
            cometJsonObj.put("manUsersList", manUserList);
            cometJsonObj.put("owner", ownerId);
            ServerEventManager.publish(CometConstants.channelMap.get(channelId), cometJsonObj);
        } catch (JSONException ex) {
            Logger.getLogger(CrmPublisherDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void publishModuleInformation(StringBuffer usersList, String ownerId, String moduleName,String moduleRecId, Integer updateId, JSONObject jobj, Integer channelId) {
        try {
            JSONObject cometJsonObj = new JSONObject();
            cometJsonObj.append("data", jobj);
            cometJsonObj.put("moduleName", moduleName);
            cometJsonObj.put("operationcode", updateId);
            cometJsonObj.put("moduleRecId", moduleRecId);
            cometJsonObj.put("usersList", usersList);
            cometJsonObj.put("owner", ownerId);
            ServerEventManager.publish(CometConstants.channelMap.get(channelId), cometJsonObj);
        } catch (JSONException ex) {
            Logger.getLogger(CrmPublisherDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void publishModuleInformation(String companyId, String ownerId, String randomNumber, String moduleName,String moduleRecId, Integer updateId, JSONObject jobj, Integer channelId) {
        try {
            JSONObject cometJsonObj = new JSONObject();
            cometJsonObj.append("data", jobj);
            cometJsonObj.put("moduleName", moduleName);
            cometJsonObj.put("operationcode", updateId);
            cometJsonObj.put("moduleRecId", moduleRecId);
            cometJsonObj.put("randomnumber", randomNumber);
            cometJsonObj.put("owner", ownerId);
            ServerEventManager.publish(CometConstants.channelMap.get(channelId).concat("/").concat(companyId), cometJsonObj);
        } catch (JSONException ex) {
            Logger.getLogger(CrmPublisherDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void publishModuleInformation(StringBuffer usersList, String companyId, String ownerId, String randomNumber, String moduleName,String moduleRecId, Integer updateId, JSONObject jobj, Integer channelId) {
        try {
            JSONObject cometJsonObj = new JSONObject();
            cometJsonObj.append("data", jobj);
            cometJsonObj.put("moduleName", moduleName);
            cometJsonObj.put("operationcode", updateId);
            cometJsonObj.put("moduleRecId", moduleRecId);
            cometJsonObj.put("usersList", usersList);
            cometJsonObj.put("randomnumber", randomNumber);
            cometJsonObj.put("owner", ownerId);
            ServerEventManager.publish(CometConstants.channelMap.get(channelId).concat("/").concat(companyId), cometJsonObj);
        } catch (JSONException ex) {
            Logger.getLogger(CrmPublisherDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
