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

import com.krawler.common.service.ServiceException;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.hibernate.impl.mb_configmaster;
import com.krawler.esp.hibernate.impl.mb_configmasterdata;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author krawler-user
 */
public class MasterComboDaoImpl extends BaseDAO implements MasterComboDao {

    @Override
    public String getConfig() {
        JSONObject jobj = new JSONObject();
        try {
            List list = executeQuery( "select mb_configmaster.configid, mb_configmaster.name, mb_configmaster.configtype from " +
                    "com.krawler.esp.hibernate.impl.mb_configmaster as mb_configmaster");
            Iterator ite = list.iterator();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                JSONObject jtemp2 = new JSONObject();
                jtemp2.put("configid", row[0]);
                jtemp2.put("fieldname", row[1]);
                jtemp2.put("configtype", row[2]);
                jobj.append("data", jtemp2);
            }
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE("ModuleBuilderMain.getConfig", e);
        } finally {
            if(jobj.length() > 0) {
                return jobj.toString();
            } else {
                return "{'data':[]}";
            }
        }
    }

    @Override
    public String insertConfig(String configid, String configtype, String name) throws ServiceException {
        int i=0,j=0;
        String result = "{'success':'false'}";
        try{
             if(configid.equals("config")){
                List list = executeQuery("select mb_configmaster.configid from " +
                    "com.krawler.esp.hibernate.impl.mb_configmaster as mb_configmaster where mb_configmaster.name=?", new Object[] {name});
                Iterator ite = list.iterator();
                if(ite.hasNext()) {
                    result = "{'success':'msg',title:'Alert',msg:'FieldName exists. Please provide a different FieldName'}";
                    i=1;
                } else {
                    mb_configmaster mb_configmaster = new mb_configmaster();
//                    mb_configmaster.setConfigid(id);
                    mb_configmaster.setConfigtype(Integer.parseInt(configtype));
                    mb_configmaster.setName(name);
                    save(mb_configmaster);
                    i = 1;
                    String actionType = "Add Config";
                    String details = "Config added for "+name;
//                    long actionId = AuditTrialHandler.getActionId(session, actionType);
//                    AuditTrialHandler.insertAuditLog(session, actionId, details, request);
                }
            }
             else if(configid.equals("clone")){
                List list = find( "select mb_configmaster.configid, mb_configmaster.configtype from " +
                    "com.krawler.esp.hibernate.impl.mb_configmaster as mb_configmaster where mb_configmaster.name=?", new Object[] {configtype});
                Iterator ite = list.iterator();
                String configid1 = null;
                int configtype1 = -1;
                if(ite.hasNext()) {
                    Object[] row = (Object[]) ite.next();
                    configid1 = row[0].toString();
                    configtype1 = Integer.parseInt(row[1].toString());
                }

                mb_configmaster mb_configmaster = new mb_configmaster();
//                mb_configmaster.setConfigid(id);
                mb_configmaster.setConfigtype(configtype1);
                mb_configmaster.setName(name);
                save(mb_configmaster);
                i = 1;
                String actionType = "Clone Master";
                String details = "Clone Added for config " + name + " From "+configtype;
//                long actionId = AuditTrialHandler.getActionId(session, actionType);
//                AuditTrialHandler.insertAuditLog(session, actionId, details, request);
                list = find( "select mb_configmasterdata.masterid, mb_configmasterdata.masterdata from " +
                        "com.krawler.esp.hibernate.impl.mb_configmasterdata as mb_configmasterdata where mb_configmasterdata.configid=?", new Object[] {configid1});
                ite = list.iterator();
                while(ite.hasNext()) {
                    Object[] row = (Object[]) ite.next();
                    com.krawler.esp.hibernate.impl.mb_configmasterdata mb_configmasterdata = new com.krawler.esp.hibernate.impl.mb_configmasterdata();
                    mb_configmasterdata.setMasterid(row[0].toString());
                    mb_configmasterdata.setConfigid(mb_configmaster.getConfigid());
                    mb_configmasterdata.setMasterdata(row[1].toString());
                    save(mb_configmasterdata);
                }
            }else {
                List list = find( "select mb_configmaster.flag from com.krawler.esp.hibernate.impl.mb_configmaster " +
                        "as mb_configmaster where mb_configmaster.configid = ?", new Object[] {configid});
                Iterator ite = list.iterator();
                if(ite.hasNext()) {
                    int flag = (Integer) ite.next();
                    if(flag == 0) {
                        com.krawler.esp.hibernate.impl.mb_configmaster mb_configmaster = (com.krawler.esp.hibernate.impl.mb_configmaster)
                                get(com.krawler.esp.hibernate.impl.mb_configmaster.class, configid);
                        int oldType = mb_configmaster.getConfigtype();
                        String oldName = mb_configmaster.getName();
                        mb_configmaster.setConfigtype(Integer.parseInt(configtype));
                        mb_configmaster.setName(name);
                        saveOrUpdate(mb_configmaster);
                        i = 1;
                        String actionType = "Edit Config";
                        String msg = "";
                        if(!oldName.equals(name)){
                            msg = "Config Name changes from "+oldName+" To "+name+", ";
                        }
                        if(oldType != Integer.parseInt(configtype)) {
                            msg += "config type changes, ";
                        }
                        String details = msg+name + " Config edited";
//                        long actionId = AuditTrialHandler.getActionId(session, actionType);
//                        AuditTrialHandler.insertAuditLog(session, actionId, details, request);
                    } else {
                        result = "{'success':'msg',title:'Alert',msg:'System generated Attribute. Cannot be edited'}";
                        i=1;
                    }
                }
            }
            if (i > 0) {
                    result = "{'success':'true'}";
            }
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE("ModuleBuilderMain.insertConfig", e);
        }
        return result;
    }

    @Override
    public String deleteConfig(String mode, String delid) throws ServiceException {
            String result = "{'success':'false'}";
            int i=0,j=0;
            try{
                JSONArray jarr = new JSONArray(delid);
                for(int ctr=0;ctr<jarr.length();ctr++){
                   if(mode.equals("config")){
                       List list = find( "select mb_configmaster.flag from com.krawler.esp.hibernate.impl.mb_configmaster as mb_configmaster where mb_configmaster.configid = ?", new Object[] {jarr.get(ctr)});
                       Iterator ite = list.iterator();
                       if(ite.hasNext()){
                            int flag = (Integer) ite.next();
                            if(flag == 0) {
                                com.krawler.esp.hibernate.impl.mb_configmaster mb_configmaster = (com.krawler.esp.hibernate.impl.mb_configmaster)
                                        get(com.krawler.esp.hibernate.impl.mb_configmaster.class, jarr.get(ctr).toString());
                                String configName = mb_configmaster.getName();
                                delete(mb_configmaster);
                                i = 1;

                                j = executeUpdate( "delete from com.krawler.esp.hibernate.impl.mb_configmasterdata as " +
                                        "mb_configmasterdata where mb_configmasterdata.configid = ?", new Object[] {jarr.get(ctr)});
    //                            com.krawler.esp.hibernate.impl.mb_configmasterdata mb_configmasterdata = (com.krawler.esp.hibernate.impl.mb_configmasterdata)
    //                                    session.load(com.krawler.esp.hibernate.impl.mb_configmasterdata.class, jarr.get(ctr).toString());
    //                            session.delete(mb_configmasterdata);
    //                            j = 1;
                                String actionType = "Delete Config";
                                String details = configName+" Config deleted";
//                                long actionId = AuditTrialHandler.getActionId(session, actionType);
//                                AuditTrialHandler.insertAuditLog(session, actionId, details, request);
                            } else {
                                result = "{'success':'msg',title:'Alert',msg:'System generated Attribute. Cannot be deleted'}";
                                i=1;
                                break;
                            }
                       }
                   }else{
                       mb_configmasterdata mb_configmasterdata = (mb_configmasterdata)
                                get(mb_configmasterdata.class, jarr.get(ctr).toString());
                       String configId = mb_configmasterdata.getConfigid();
                       delete(mb_configmasterdata);
                       i = 1;
                       String actionType = "Delete Master Record";

//                       String configName = AuditTrialHandler.getConfigName(session, configId);
//                       String details = "Record deleted in Master " + configName;
//                       long actionId = AuditTrialHandler.getActionId(session, actionType);
//                       AuditTrialHandler.insertAuditLog(session, actionId, details, request);

                   }
                }
                if(i > 0){
                    result = "{'success':'true'}";
                }
            } catch(Exception e) {
                logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE("ModuleBuilderMain.deleteConfig", e);
            }
            return result;
    }

    @Override
    public String insertMaster(String masterid, String configid, String masterdata) throws ServiceException {
            String result = "{'success':'false'}";
            int i = 0;
            try{
                if(masterid.equals("")){
                    String id =java.util.UUID.randomUUID().toString();
                    mb_configmasterdata mb_configmasterdata = new mb_configmasterdata();
                    mb_configmasterdata.setMasterid(id);
                    mb_configmasterdata.setConfigid(configid);
                    mb_configmasterdata.setMasterdata(masterdata);
                    save(mb_configmasterdata);
                    i = 1;
                    String actionType = "Add Master Record";
//                    String configName = AuditTrialHandler.getConfigName(session, configid);
//                    String details = "Record added in Master "+configName;
//                    long actionId = AuditTrialHandler.getActionId(session, actionType);
//                    AuditTrialHandler.insertAuditLog(session, actionId, details, request);
                }else{
                    mb_configmasterdata mb_configmasterdata = (mb_configmasterdata)
                                get(mb_configmasterdata.class, masterid);
                    mb_configmasterdata.setMasterdata(masterdata);
                    saveOrUpdate(mb_configmasterdata);
                    i = 1;
                    String actionType = "Edit Master Record";
//                    String configName = AuditTrialHandler.getConfigName(session, configid);
//                    String details = "Record edited in Master "+configName;
//                    long actionId = AuditTrialHandler.getActionId(session, actionType);
//                    AuditTrialHandler.insertAuditLog(session, actionId, details, request);

                 }
                if(i > 0){
                    result = "{'success':'true'}";
                }
            } catch(Exception e) {
                logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE("ModuleBuilderMain.insertmaster", e);
            }
            return result;
    }

    @Override
    public String getMaster(String configid) throws ServiceException {
       com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
       try{
            List list = find( "select mb_configmasterdata.masterid, mb_configmasterdata.masterdata from " +
                    "com.krawler.esp.hibernate.impl.mb_configmasterdata as mb_configmasterdata where mb_configmasterdata.configid=?", new Object[] {configid});
            Iterator ite = list.iterator();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("masterid", row[0]);
                jtemp.put("masterdata", row[1]);
                jobj.append("data", jtemp);
            }
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("ModuleBuilderMain.getmaster", e);
        }
        return jobj.toString();
    }

    @Override
    public String getMasterAttributes(String mode) throws ServiceException {
       com.krawler.utils.json.base.JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
       String sql="";
       try{
            List list = executeQuery( "select mb_configmaster.configid, mb_configmaster.name from com.krawler.esp.hibernate.impl.mb_configmaster as mb_configmaster where mb_configmaster.configtype in (4,7)");
            Iterator ite = list.iterator();
            while( ite.hasNext() ) {
                Object[] row = (Object[]) ite.next();
                com.krawler.utils.json.base.JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
                jtemp.put("configid", row[0]);
                jtemp.put("name", row[1]);
                jobj.append("data", jtemp);
            }
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("ModuleBuilderMain.getmaster", e);
        }
        return jobj.toString();
    }
}
