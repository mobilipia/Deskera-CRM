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
package com.krawler.spring.crm.contactModule;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.Header;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.crm.contact.dm.ContactOwnerInfo;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.contactOwners;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.common.crmManagerDAO;
import com.krawler.spring.profileHandler.profileHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import org.hibernate.HibernateException;

public class crmContactHandler {

    public static JSONObject contactsReportJson(crmContactDAO crmContactDAOObj, List ll, HttpServletRequest request, boolean export, int totalSize,crmCommonDAO  crmCommonDAOObj, crmManagerDAO crmManagerDAOObj) {
        JSONArray jarr = new JSONArray();
        JSONObject jobjTemp = new JSONObject();
        JSONObject commData = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
            Iterator ite = ll.iterator();
            String companyid = sessionHandlerImpl.getCompanyid(request);
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();

                JSONObject tmpObj = new JSONObject();
                tmpObj.put("contactid", obj.getContactid());
                tmpObj.put("contactowner",StringUtil.checkForNull(obj.getFirstname()));
                tmpObj.put("firstname", StringUtil.checkForNull(obj.getFirstname()));
                tmpObj.put("accname", obj.getCrmAccount() != null ? obj.getCrmAccount().getAccountname() : "");
                tmpObj.put("accindustry", (obj.getCrmAccount() != null && obj.getCrmAccount().getCrmCombodataByIndustryid() != null) ? obj.getCrmAccount().getCrmCombodataByIndustryid().getValue() : "");
                tmpObj.put("acctype", (obj.getCrmAccount() != null && obj.getCrmAccount().getCrmCombodataByAccounttypeid() != null) ? obj.getCrmAccount().getCrmCombodataByAccounttypeid().getValue() : "");
                tmpObj.put("lastname", obj.getLastname());
                tmpObj.put("contactname", (StringUtil.checkForNull(obj.getFirstname()) + " " + StringUtil.checkForNull(obj.getLastname())).trim());
                tmpObj.put("phoneno", obj.getPhoneno());
                tmpObj.put("mobileno", obj.getMobileno() != null ? obj.getMobileno() : "");
                tmpObj.put("email", obj.getEmail());
                tmpObj.put("street", obj.getMailstreet());
                tmpObj.put("description", obj.getDescription() != null ? obj.getDescription() : "");
                tmpObj.put("createdon", crmManagerCommon.dateNull(obj.getCreatedon()));
                tmpObj.put("validflag", obj.getValidflag());
                tmpObj.put("leadsourceid", crmManagerCommon.comboNull(obj.getCrmCombodataByLeadsourceid()));
                tmpObj.put("leadsource", (obj.getCrmCombodataByLeadsourceid() != null ? obj.getCrmCombodataByLeadsourceid().getValue() : ""));
                tmpObj.put("titleid", "");
                tmpObj.put("title", obj.getTitle() != null ? obj.getTitle() : "");

                jarr.put(tmpObj);
            }
            commData.put("coldata", jarr);
            if (export) {
                List ll1=crmManagerCommon.getHeaderName(crmCommonDAOObj,"Contact");

                Iterator ite1 = ll1.iterator();

                while (ite1.hasNext()) {
                    DefaultHeader obj = (DefaultHeader) ite1.next();
                    String newHeader =crmManagerCommon.getNewColumnHeader(crmCommonDAOObj,obj.getDefaultHeader(),"Contact",companyid);
                    if(StringUtil.equal(Header.CONTACTLEADSOURCEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?Header.CONTACTLEADSOURCEHEADER:newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?Header.CONTACTLEADSOURCEHEADER:newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "leadsource");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CONTACTFIRSTNAMEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", "Contact Name");
                        jobjTemp.put("tip", "Contact Name");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "contactname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CONTACTTITLEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?Header.CONTACTTITLEHEADER:newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?Header.CONTACTTITLEHEADER:newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "title");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CONTACTACCOUNTHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?"Account Name":newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?"Account Name":newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "accname");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                    } else if(StringUtil.equal(Header.CONTACTEMAILHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?Header.CONTACTEMAILHEADER:newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?Header.CONTACTEMAILHEADER:newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "textfield");
                        jobjTemp.put("dataIndex", "email");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   } else if(StringUtil.equal(Header.CONTACTPHONEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?"Contact No.":newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?"Contact No.":newHeader);
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("xtype", "numberfield");
                        jobjTemp.put("dataIndex", "phoneno");
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   } else if(StringUtil.equal(Header.CONTACTCREATIONDATEHEADER, obj.getDefaultHeader())) {
                        jobjTemp = new JSONObject();
                        jobjTemp.put("header", StringUtil.isNullOrEmpty(newHeader)?Header.CONTACTCREATIONDATEHEADER:newHeader);
                        jobjTemp.put("tip", StringUtil.isNullOrEmpty(newHeader)?Header.CONTACTCREATIONDATEHEADER:newHeader);
                        jobjTemp.put("title", "createdon");
                        jobjTemp.put("pdfwidth", 60);
                        jobjTemp.put("align", "center");
                        jobjTemp.put("xtype", "datefield");
                        jobjTemp.put("dataIndex", "createdon");
                        jobjTemp.put("renderer", crmManagerCommon.dateRendererReport());
                        StringUtil.escapeJSONObject(jobjTemp, "header","tip");
                        jarrColumns.put(jobjTemp);
                   }	
                }
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "leadsource");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "contactname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "title");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "accname");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "email");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "phoneno");
                jarrRecords.put(jobjTemp);
                jobjTemp = new JSONObject();
                jobjTemp.put("name", "createdon");
                jobjTemp.put("type", "date");
                jarrRecords.put(jobjTemp);

                commData.put("columns", jarrColumns);
                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
                commData.put("metaData", jMeta);
            }
            commData.put("success", true);
            commData.put("totalCount", totalSize);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return commData;
    }

    /**
     * Older method to retrieve all contactOwners provided contactid. Use getAllContactOwners(crmContactDAOObj, CrmContact contact) instead if
     * CrmContact is available.
     * @param crmContactDAOObj
     * @param contactid
     * @return
     * @throws ServiceException
     */
    //TODO This method should be eventually removed unless there are places where the only available information at our
    //disposal is contactid.
    public static String[] getAllContactOwners(crmContactDAO crmContactDAOObj, String contactid) throws ServiceException{

        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("co.contact.contactid");
        filter_params.add(contactid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("totalCountFlag", true);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);

        kmsg = crmContactDAOObj.getContactOwners(requestParams);
        Iterator ite = kmsg.getEntityList().iterator();
        contactOwners contactOwnersObj;
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        while(ite.hasNext()){
            Object row[] = (Object[]) ite.next();
            contactOwnersObj=(contactOwners)row[0];
            if(contactOwnersObj.isMainOwner()){
                mainLeadOwner=contactOwnersObj.getUsersByUserid();
            }else{
                ownerId+=contactOwnersObj.getUsersByUserid().getUserID()+",";
                ownerNames+=profileHandler.getUserFullName(contactOwnersObj.getUsersByUserid())+", ";
            }
        }

        String mainOwner = "";
        if(mainLeadOwner!=null)
            mainOwner=profileHandler.getUserFullName(mainLeadOwner);

        String tooltip;
        String gridName;
        if(!StringUtil.isNullOrEmpty(ownerNames)){
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerId = ownerId.substring(0,ownerId.length()-1);
            tooltip="<b>"+mainOwner+"</b>, "+ownerNames+".";
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+"</b>.";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Opportunities'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }
    
    /**
     * @param crmContactDAOObj
     * @param contact
     * @return
     * @throws ServiceException
     */
    public static String[] getAllContactOwners(CrmContact contact) throws ServiceException{
    	Iterator ite = contact.getContactOwners().iterator();
        contactOwners contactOwnersObj;
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        while(ite.hasNext()){
            contactOwnersObj=(contactOwners)ite.next();
            if(contactOwnersObj.isMainOwner()){
                mainLeadOwner=contactOwnersObj.getUsersByUserid();
            }else{
                ownerId+=contactOwnersObj.getUsersByUserid().getUserID()+",";
                ownerNames+=profileHandler.getUserFullName(contactOwnersObj.getUsersByUserid())+", ";
            }
        }

        String mainOwner = "";
        if(mainLeadOwner!=null)
            mainOwner=profileHandler.getUserFullName(mainLeadOwner);

        String tooltip;
        String gridName;
        if(!StringUtil.isNullOrEmpty(ownerNames)){
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerId = ownerId.substring(0,ownerId.length()-1);
            tooltip="<b>"+mainOwner+"</b>, "+ownerNames+".";
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+"</b>.";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Owners'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }

    public static String[] getAllContactOwners(List<ContactOwnerInfo> owners) throws ServiceException
    {
        if (owners == null)
        {
            return new String[]{};
        }
        String ownerNames="";
        User mainLeadOwner=null;
        String ownerId="";

        for (ContactOwnerInfo owner : owners)
        {
            contactOwners accountOwnersObj = owner.getOwner();
            if (accountOwnersObj.isMainOwner())
            {
                mainLeadOwner = owner.getUser();
            } else
            {
                ownerId += owner.getUser().getUserID() + ",";
                ownerNames += StringUtil.getFullName(owner.getUser()) + ", ";
            }
        }

        String mainOwner = "";
        if(mainLeadOwner!=null)
            mainOwner=profileHandler.getUserFullName(mainLeadOwner);

        String tooltip;
        String gridName;
        if(!StringUtil.isNullOrEmpty(ownerNames)){
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerId = ownerId.substring(0,ownerId.length()-1);
            tooltip="<b>"+mainOwner+"</b>, "+ownerNames+".";
            gridName=mainOwner+", "+ownerNames;
        }else{
            tooltip="<b>"+mainOwner+"</b>.";
            gridName=mainOwner;
        }

        String displayOwnerNames ="<div wtf:qtip=\""+tooltip+"\"wtf:qtitle='Owners'>"+StringUtil.abbreviate(gridName,27)+"</div>";
        String[] ownerInfo = {mainOwner,ownerNames,mainLeadOwner.getUserID(),ownerId,displayOwnerNames,gridName};
        return ownerInfo;
    }
    
    public static JSONObject getContactOwners(crmContactDAO crmContactDAOObj, HttpServletRequest request,String contactid) throws ServiceException, SessionExpiredException{
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        JSONObject temp;
        try {
            String userid = sessionHandlerImpl.getUserid(request);
            String[] ownerInfo = getAllContactOwners(crmContactDAOObj,contactid);
            String mainOwner=ownerInfo[0];
            String ownerNames=ownerInfo[1];

            boolean addOwnerPerm=false;
            if(!StringUtil.isNullOrEmpty(ownerNames)){
                mainOwner+=", ";
            }
            temp = new JSONObject();
            temp.put("owners", ownerNames);
            temp.put("mainOwner",mainOwner );
            jarr.put(temp);

            if(mainOwner.equalsIgnoreCase(userid)){
                addOwnerPerm=true;
            }

            jobj.put("addOwnerPerm", addOwnerPerm);
            jobj.put("ownerList", jarr);

        } catch (JSONException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (HibernateException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }
    public static String getMainContactOwner(crmContactDAO crmContactDAOObj, String contactid) throws ServiceException{

        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("co.contact.contactid");
        filter_params.add(contactid);
        filter_names.add("co.mainOwner");
        filter_params.add(true);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("totalCountFlag", true);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);

        kmsg = crmContactDAOObj.getContactOwners(requestParams);
        Iterator ite = kmsg.getEntityList().iterator();
        contactOwners contactOwnersObj;
        String ownerId="";

        while(ite.hasNext()){
            Object row[] = (Object[]) ite.next();
            contactOwnersObj=(contactOwners)row[0];
            ownerId+=contactOwnersObj.getUsersByUserid().getUserID();
        }
        return ownerId;
    }

    public static String[] getContactOwners(crmContactDAO crmContactDAOObj, String contactid) throws ServiceException{
        KwlReturnObject kmsg = null;
        ArrayList filter_names = new ArrayList();
        ArrayList filter_params = new ArrayList();
        filter_names.add("co.contact.contactid");
        filter_params.add(contactid);
        HashMap<String, Object> requestParams = new HashMap<String, Object>();
        requestParams.put("totalCountFlag", true);
        requestParams.put("filter_names", filter_names);
        requestParams.put("filter_params", filter_params);

        kmsg = crmContactDAOObj.getContactOwners(requestParams);
        Iterator ite = kmsg.getEntityList().iterator();
        contactOwners contactOwnersObj;
        String ownerId="";
        String ownerNames="";
        while(ite.hasNext()){
            Object row[] = (Object[]) ite.next();
            contactOwnersObj=(contactOwners)row[0];
            ownerId+=contactOwnersObj.getUsersByUserid().getUserID()+",";
            ownerNames+=contactOwnersObj.getUsersByUserid().getFirstName()+StringUtil.hNull(contactOwnersObj.getUsersByUserid().getLastName())+", ";
        }
        if(!StringUtil.isNullOrEmpty(ownerId)){
            ownerId = ownerId.substring(0,ownerId.length()-1);
            ownerNames = ownerNames.substring(0,ownerNames.length()-2);
            ownerNames ="<div wtf:qtip=\""+ownerNames+"\"wtf:qtitle='Owners'>"+StringUtil.abbreviate(ownerNames,27)+"</div>";
        }
        String[] ownerInfo = {ownerId,ownerNames};
        return ownerInfo;
    }

    public static JSONObject getContacts(crmContactDAO crmContactDAOObj, StringBuffer usersList, boolean heirarchyPerm, HttpServletRequest request,String mapid,String relatedName) throws ServiceException {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try{
            String companyid = sessionHandlerImpl.getCompanyid(request);
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("start", "0");
            requestParams.put("limit", "10");
            requestParams.put("heirarchyPerm", heirarchyPerm);

            if( (!StringUtil.isNullOrEmpty(relatedName) && relatedName.equals("Lead"))  ){
                filter_names.add("c.Lead.leadid");
            }else{
                filter_names.add("c.crmAccount.accountid");
            }
            filter_names.add("c.company.companyID");
            filter_names.add("c.isarchive");
            filter_params.add(mapid);
            filter_params.add(companyid);
            filter_params.add(false);
            filter_names.add("c.deleteflag");
            filter_params.add(0);

            KwlReturnObject kmsg = crmContactDAOObj.getContacts(requestParams, usersList, filter_names, filter_params);
            List ll = kmsg.getEntityList();
            Iterator ite = ll.iterator();

            String contactNames="";
            while (ite.hasNext()) {
                CrmContact obj = (CrmContact) ite.next();
                String contactnaam = (StringUtil.checkForNull(obj.getFirstname())+ " " + StringUtil.checkForNull(obj.getLastname())).trim();
                contactNames+=contactnaam+", ";
            }
            if(!StringUtil.isNullOrEmpty(contactNames)){
                contactNames=contactNames.substring(0, contactNames.length()-2);
                JSONObject temp = new JSONObject();
                temp.put("contacts",contactNames );
                jarr.put(temp);
            }
            jobj.put("contactList", jarr);
        } catch (JSONException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (HibernateException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }
    public static boolean hasContactAccess(List<ContactOwnerInfo> owners ,StringBuffer usersList) {
        boolean hasAccess = false;
        Iterator ite1= owners.iterator();
        while(ite1.hasNext()){
            ContactOwnerInfo contactOwnersObj =(ContactOwnerInfo) ite1.next();
            if(usersList.indexOf(contactOwnersObj.getUser().getUserID()) != -1 ){
                hasAccess = true;
                break;
            }
        }
        return hasAccess;
    }

    public static HashMap<String, DefaultMasterItem> getContactDefaultMasterItemsMap(String companyid, StringBuffer usersList, crmCommonDAO crmCommonDAOObj, kwlCommonTablesDAO kwlCommonTablesDAOObj) throws ServiceException, JSONException {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            List filter_names = new ArrayList();
            List filter_params = new ArrayList();
            //Get ids of crmCombomaster table
            String masterIds = "'"+Constants.LEAD_SOURCEID+"',";
            masterIds += "'"+Constants.LEAD_INDUSTRYID+"'";
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(Constants.filter_names, filter_names);
            requestParams.put(Constants.filter_params, filter_params);

            // if leadsource then fetch campaign source data also
            CompanyPreferences cmpPref = (CompanyPreferences) kwlCommonTablesDAOObj.getClassObject("com.krawler.crm.database.tables.CompanyPreferences", companyid);
            JSONObject jsnObj = crmManagerCommon.getCompanyPreferencesJSON(cmpPref, new JSONObject());
            boolean heirarchyPerm = jsnObj.getBoolean(Constants.Crm_campaign_modulename);
            if(!heirarchyPerm) {
                requestParams.put("userlist_value", usersList);
            }
            requestParams.put("companyid", companyid);
            return crmCommonDAOObj.getDefaultMasterItemsMap(requestParams);
    }
}
