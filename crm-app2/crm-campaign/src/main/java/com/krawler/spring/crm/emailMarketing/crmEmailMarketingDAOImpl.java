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
package com.krawler.spring.crm.emailMarketing;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;

import java.util.ArrayList;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.Snap;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.*;
import com.krawler.esp.handlers.CampaignEmailNotification;
import com.krawler.esp.handlers.CampaignMailDAO;
import com.krawler.esp.handlers.CampaignTestEmailNotification;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.crm.database.tables.DefaultTemplates;
import com.krawler.crm.database.tables.EnumEmailType;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.dao.BaseDAO;
import com.krawler.notify.SenderCache;
import com.krawler.notify.email.EmailSender;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.hibernate.HibernateException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

import sun.misc.BASE64Encoder;

public class crmEmailMarketingDAOImpl extends BaseDAO implements crmEmailMarketingDAO {
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private HibernateTransactionManager txnManager;
    private CampaignMailDAO campaignMailDAOObj;
    private crmCommonDAO crmCommonDAOObj;
    private SenderCache<EmailSender> mailSenderCache;
    
	public void setMailSenderCache(SenderCache<EmailSender> mailSenderCache) {
		this.mailSenderCache = mailSenderCache;
	}

	public void setCampaignMailDAO(CampaignMailDAO campaignMailDAOObj) {
        this.campaignMailDAOObj = campaignMailDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txnManager) {
        this.txnManager = txnManager;
    }
      
    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }
     public void setcrmCommonDAO(crmCommonDAO crmCommonDAOObj1) {
        this.crmCommonDAOObj = crmCommonDAOObj1;
    }
    @Override
    public KwlReturnObject getEmailTemplateList(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String companyid = "";
            if(requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            String templateList = "";
            if(requestParams.containsKey("templateList") && requestParams.get("templateList") != null) {
                templateList = requestParams.get("templateList").toString();
            }
            boolean excludeDefault=false;
            if(requestParams.containsKey("excludeDefault") && requestParams.get("excludeDefault") != null) {
                excludeDefault = Boolean.parseBoolean(requestParams.get("excludeDefault").toString());
            }
            boolean defaultOnly=false;
            if(requestParams.containsKey("defaultOnly") && requestParams.get("defaultOnly") != null) {
                 defaultOnly = Boolean.parseBoolean(requestParams.get("defaultOnly").toString());
            }
            int start = 0;
            int limit = 15;
            if (requestParams.containsKey("start") && requestParams.containsKey("limit") && requestParams.get("start") != null) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            if(!defaultOnly){
                String Hql = "select p from com.krawler.crm.database.tables.EmailTemplate p "+crmManagerCommon.getJoinQuery(requestParams)+
                        "where p.deleted=0 and p.creator.company.companyID = ? ";

                ArrayList filter_params = new ArrayList();
                filter_params.add(companyid);

                if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                    String ss=requestParams.get("ss").toString();
                    if(!StringUtil.isNullOrEmpty(ss)){
                        String[] searchcol = new String[]{"p.name","p.description"};
                        StringUtil.insertParamSearchString(filter_params, ss, 2);
                        String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                        Hql +=searchQuery;
                    }
                }
                String orderQuery = " order by p.createdOn desc ";
                if(requestParams.containsKey("field")) {
                    String dbname = crmManagerCommon.getFieldDbName(requestParams);
                    if(dbname!=null){
                        String dir = requestParams.get("direction").toString();
                        dbname = dbname.replaceFirst("c.", "p.");
                        orderQuery = " order by "+dbname+" "+dir+" ";
                    }
                }
                ll = executeQuery(Hql+orderQuery, filter_params.toArray());
            }
            if(!excludeDefault){
                 if(ll==null){
                    ll=new ArrayList();
                }
                getDefaultEmailTemplateList(ll,requestParams);
            }

            dl=ll.size();
            int end = 0;
            if(!StringUtil.equal(templateList, "true")) {
                if(dl>limit) {
                    if(dl-(start + limit)<0) {
                            end = dl;
                    } else {
                        end = start + limit ;
                    }

                    ll = ll.subList(start, end);
                }
            }
//                    
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getEmailTemplateList : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getEmailTypeList(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {

            int start = 0;
            int limit = 15;

            String Hql = "from EnumEmailType c "+crmManagerCommon.getJoinQuery(requestParams)+
                    "where  ((c.company.companyID = ? ) or (c.name not in( select t.name from EnumEmailType t where t.company.companyID = ? ) and c.company.companyID is null))";
            if (requestParams.containsKey("start") && requestParams.get("start") != null) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            String companyid = "";
            if(requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            ArrayList filter_names = new ArrayList(),filter_params = new ArrayList();
            filter_params.add(companyid);
            filter_params.add(companyid);

            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"c.name"};
                    StringUtil.insertParamSearchString(filter_params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    Hql +=searchQuery;
                }
            }
            String orderQuery = " order by c.createdOn desc ";
            
            ll = executeQuery(Hql+orderQuery, filter_params.toArray());
            dl=ll.size();
            ll = executeQueryPaging(Hql+orderQuery, filter_params.toArray(), new Integer[]{start,limit});
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getEmailTypeList : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    @Override
    public void getDefaultEmailTemplateList(List l1,HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        try {
            String Hql ;
            Hql = "Select d from DefaultTemplates d ";
            //Hql = "Select d from DefaultTemplates d where d.templateid not in ( Select defaulttemplateid from EmailTemplate where defaulttemplateid is not null ) ";
            ArrayList filter_params = new ArrayList();
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
               
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"d.name","d.description"};
                    StringUtil.insertParamSearchString(filter_params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "where", searchcol);
                    Hql +=searchQuery;
                }
            }
            String orderQuery = " order by d.name desc ";
            if(requestParams.containsKey("direction")) {
                String dir = requestParams.get("direction").toString();
                orderQuery = " order by d.name "+dir+" ";
            }
            ll = executeQuery(Hql+orderQuery, filter_params.toArray());
            l1.addAll(ll);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getDefaultEmailTemplate", e);
        }
        //return ll;
    }

    @Override
    public KwlReturnObject getDefaultEmailTemplate(HashMap requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql ;
            ArrayList filter_names = new ArrayList();
             ArrayList filter_params = new ArrayList();

            if(requestParams.containsKey("filter_names")){
                filter_names = (ArrayList) requestParams.get("filter_names");
            }
            if(requestParams.containsKey("filter_params")){
                filter_params = (ArrayList) requestParams.get("filter_params");
            }
            Hql = "from DefaultTemplates c";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            Hql += filterQuery;
            
            ll = executeQuery(Hql,filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getDefaultEmailTemplate", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    
    @Override
    public KwlReturnObject addEmailTemplate(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            EmailTemplate emailTemp = null;
            emailTemp = new EmailTemplate();
            if (jobj.has("userid")) {
                User userObj = (User) get(User.class, jobj.getString("userid"));
                emailTemp.setCreator(userObj);
            }
            String tBody = "";
            if (jobj.has("tbody")) {
                tBody = jobj.getString("tbody");
                emailTemp.setBody_html(URLEncoder.encode(tBody, "utf-8"));
            }
            if (jobj.has("body")) {
                tBody = jobj.getString("body");
                emailTemp.setBody(URLEncoder.encode(tBody, "utf-8"));
            }
            if (jobj.has("description")) {
                emailTemp.setDescription(jobj.getString("description"));
            }
            if (jobj.has("subject")) {
                emailTemp.setSubject(jobj.getString("subject"));
            }
            if (jobj.has("name")) {
                emailTemp.setName(jobj.getString("name"));
            }
            if (jobj.has("modifiedon")) {
                emailTemp.setModifiedon(new Date());
            }
            if (jobj.has("createdon")) {
                emailTemp.setCreatedon(authHandler.getCurrentDate());
            }
            if (jobj.has("deleted")) {
                emailTemp.setDeleted(Integer.parseInt(jobj.getString("deleted")));
            }

            saveOrUpdate(emailTemp);
            try {
                if (!jobj.has("thumbnail")) {
//                    String htmlFile = ConfigReader.getinstance().get("tempFilePath");
//                    java.io.File tempFile = new java.io.File(htmlFile);
//                    if(!tempFile.exists())
//                        tempFile.createNewFile();
//                    String htmlCont = "<html><body style='background-color:gray;'>" + tBody + "</body></html>";
//                    FileOutputStream fos = new FileOutputStream(tempFile);
//                    fos.write(htmlCont.getBytes());
//                    fos.flush();
//                    fos.close();
//                    String thumbnail = storageHandlerImpl.GetProfileImgStorePath();
//                    thumbnail += emailTemp.getTemplateid() + ".png";
//                    if(Snap.generateThumbnail(htmlFile, thumbnail))
//                        emailTemp.setThumbnail(emailTemp.getTemplateid() + ".png");
//                    tempFile.delete();
                    emailTemp.setThumbnail("");
                } else {
                    emailTemp.setThumbnail(jobj.getString("thumbnail"));
                }
            } catch(Exception e) {
                emailTemp.setThumbnail("");
            }

            ll.add(emailTemp);
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addEmailTemplate : "+e.getMessage(), e);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addEmailTemplate : "+e.getMessage(), e);
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addEmailTemplate : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addEmailTemplate : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addEmailTemplate : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject editEmailTemplate(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            EmailTemplate emailTemp = null;
            emailTemp = (EmailTemplate) get(EmailTemplate.class, jobj.getString("tid"));

            String tBody = "";
            if (jobj.has("tbody")) {
                tBody = jobj.getString("tbody");
                emailTemp.setBody_html(URLEncoder.encode(tBody, "utf-8"));
            }
            if (jobj.has("description")) {
                emailTemp.setDescription(jobj.getString("description"));
            }
            if (jobj.has("subject")) {
                emailTemp.setSubject(jobj.getString("subject"));
            }
            if (jobj.has("name")) {
                emailTemp.setName(jobj.getString("name"));
            }
            if (jobj.has("modifiedon")) {
                emailTemp.setModifiedOn(new Date().getTime());
            }
            if (jobj.has("createdon")) {
                emailTemp.setCreatedOn(System.currentTimeMillis());
            }
            if (jobj.has("deleted")) {
                emailTemp.setDeleted(jobj.getInt("deleted"));
            }

            saveOrUpdate(emailTemp);
            if (!jobj.has("deleted")) {
                try {
    //                String htmlFile = "C:\\temphtmlFile.html";
                    String htmlFile = ConfigReader.getinstance().get("tempFilePath");
                    java.io.File tempFile = new java.io.File(htmlFile);
                    if(!tempFile.exists())
                        tempFile.createNewFile();
                    String htmlCont = "<html><body style='background-color:gray;'>" + tBody + "</body></html>";
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(htmlCont.getBytes());
                    fos.flush();
                    fos.close();
                    String thumbnail = storageHandlerImpl.GetProfileImgStorePath();
                    thumbnail += emailTemp.getTemplateid() + ".png";
                    if(Snap.generateThumbnail(htmlFile, thumbnail))
                        emailTemp.setThumbnail(emailTemp.getTemplateid() + ".png");
                    tempFile.delete();
                } catch(Exception e) {
                    emailTemp.setThumbnail("");
                }
            }
            
            ll.add(emailTemp);
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editEmailTemplate : "+e.getMessage(), e);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editEmailTemplate : "+e.getMessage(), e);
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editEmailTemplate : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editEmailTemplate : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editEmailTemplate : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
@Override
    public KwlReturnObject addEmailType(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            EnumEmailType emailType  = new EnumEmailType();
            emailType.setTypeid(UUID.randomUUID().toString());
            if (jobj.has("userid")) {
                User userObj = (User) get(User.class, jobj.getString("userid"));
                emailType.setCreator(userObj);
                emailType.setModifier(userObj);
            }
            if (jobj.has("tbody")) {
                String tBody = jobj.getString("tbody");
                emailType.setBody_html(URLEncoder.encode(tBody, "utf-8"));
            }
            if (jobj.has("description")) {
                emailType.setDescription(jobj.getString("description"));
            }
            if (jobj.has("subject")) {
                emailType.setSubject(jobj.getString("subject"));
            }
            if (jobj.has("name")) {
                emailType.setName(jobj.getString("name"));
            }
            if (jobj.has("modifiedon")) {
                emailType.setModifiedOn(System.currentTimeMillis());
            }
            if (jobj.has("createdon")) {
                emailType.setCreatedOn(System.currentTimeMillis());
            }
            if (jobj.has("companyid")) {
                Company company = (Company) get(Company.class, jobj.getString("companyid"));
                emailType.setCompany(company);
            }
            if (jobj.has("plaintext")) {
                emailType.setPlaintext(jobj.getString("plaintext"));
            }
            saveOrUpdate(emailType);
            ll.add(emailType);
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addEmailType : "+e.getMessage(), e);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addEmailType : "+e.getMessage(), e);
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addEmailType : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addEmailType : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addEmailType : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject editEmailType(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            EnumEmailType emailType = (EnumEmailType) get(EnumEmailType.class, jobj.getString("tid"));
            if (jobj.has("tbody")) {
                String tBody = jobj.getString("tbody");
                emailType.setBody_html(URLEncoder.encode(tBody, "utf-8"));
            }
            if (jobj.has("description")) {
                emailType.setDescription(jobj.getString("description"));
            }
            if (jobj.has("subject")) {
                emailType.setSubject(jobj.getString("subject"));
            }
            if (jobj.has("name")) {
                emailType.setName(jobj.getString("name"));
            }
            if (jobj.has("modifiedon")) {
                emailType.setModifiedOn(System.currentTimeMillis());
            }
            if (jobj.has("createdon")) {
                emailType.setCreatedOn(System.currentTimeMillis());
            }
            if (jobj.has("companyid")) {
                Company company = (Company) get(Company.class, jobj.getString("companyid"));
                emailType.setCompany(company);
            }
            if (jobj.has("plaintext")) {
                emailType.setPlaintext(jobj.getString("plaintext"));
            }
            saveOrUpdate(emailType);
            ll.add(emailType);
        } catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editEmailType : "+e.getMessage(), e);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editEmailType : "+e.getMessage(), e);
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editEmailType : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editEmailType : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editEmailType : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getTargetList(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            int start = 0;
            int limit = 15;
            String Hql = "from com.krawler.crm.database.tables.TargetList t"+crmManagerCommon.getJoinQuery(requestParams);
            if (requestParams.containsKey("start") && requestParams.get("start") != null) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            ArrayList filter_names = new ArrayList(),filter_params = new ArrayList();
            if(requestParams.containsKey("filter_names") && requestParams.containsKey("filter_params")){
                filter_names = (ArrayList) requestParams.get("filter_names");
                filter_params = (ArrayList) requestParams.get("filter_params");
            }
            
            Hql +=com.krawler.common.util.StringUtil.filterQuery(filter_names, "where");
            int ind = Hql.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(Hql.substring(ind+1,ind+2));
                Hql = Hql.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }

            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"t.name"};
                    StringUtil.insertParamSearchString(filter_params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    Hql +=searchQuery;
                }
            }
            
            //checks whether list is empty or not.(Empty target list is not retrieved in the case if we are adding new campaign else allowed to get empty targetlist).
            if(requestParams.containsKey("avoidblanklist")){
            	Hql += "and t.id in (select targetlistid from TargetListTargets tt where tt.deleted = 0 )";
            }
            
            String orderQuery = " order by t.createdOn desc ";
            if(requestParams.containsKey("field")) {
                String dbname = crmManagerCommon.getFieldDbName(requestParams);
                if(dbname!=null){
                    String dir = requestParams.get("direction").toString();
                    dbname = dbname.replaceFirst("c.", "t.");
                    orderQuery = " order by "+dbname+" "+dir+" ";
                }
            }
            ll = executeQuery(Hql, filter_params.toArray());
            dl=ll.size();
            boolean allflag = false;
            if(requestParams.get("allflag")!=null) {
                allflag = Boolean.parseBoolean(requestParams.get("allflag").toString());
            }
            if(!allflag)
                    ll = executeQueryPaging(Hql+orderQuery, filter_params.toArray(), new Integer[]{start,limit});
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getTargetList : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject addTargetList(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        try {
            String userid = null;
            String id = "";
            TargetList targetList = null;
            id = jobj.getString("targetlistid");
            targetList = (TargetList) get(TargetList.class, id);
            if(targetList == null) {
                targetList = new TargetList();
            }
            if(jobj.has("name")) {
                targetList.setName(jobj.getString("name"));
            }
            if(jobj.has("description")) {
                targetList.setDescription(jobj.getString("description"));
            }
            if(jobj.has("userid")) {
                userid = jobj.getString("userid");
                targetList.setCreator((User) get(User.class, userid));
            }
            if(jobj.has("createdon")) {
                targetList.setCreatedOn(System.currentTimeMillis());
            }
            if(jobj.has("modifiedon")) {
                targetList.setModifiedOn(System.currentTimeMillis());
            }
            if(jobj.has("deleteflag")) {
                targetList.setDeleted(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if(jobj.has("saveflag")) {
                targetList.setSaveflag(Integer.parseInt(jobj.getString("saveflag")));
            }

            saveOrUpdate(targetList);
            ll.add(targetList);

        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addTargetList : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addTargetList : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addTargetList : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, 1);
    }

    @Override
    public KwlReturnObject saveTargetsForTemp(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        TargetListTargets targetObj = null;
        try {
            targetObj = new TargetListTargets();
            if(jobj.has("fname")) {
                targetObj.setFname(jobj.getString("fname"));
            }
            if(jobj.has("email")) {
                targetObj.setEmailid(jobj.getString("email"));
            }
            if(jobj.has("targetlistid")) {
                targetObj.setTargetlistid((TargetList) get(TargetList.class, jobj.getString("targetlistid")));
            }
            if(jobj.has("deleteflag")) {
                targetObj.setDeleted(Integer.parseInt(jobj.getString("deleteflag")));
            }
            if(jobj.has("relatedid")) {
                targetObj.setRelatedid(jobj.getString("relatedid"));
            }
            if(jobj.has("relatedto")) {
                targetObj.setRelatedto(Integer.parseInt(jobj.getString("relatedto")));
            }

            save(targetObj);
            ll.add(targetObj);
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveTargetsForTemp", ex);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveTargetsForTemp", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, 1);
    }

    @Override
    public KwlReturnObject getEmailMarkTargetList(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
                String emailmarkid = requestParams.get("emailmarkid").toString();
                EmailMarketing emObj = (EmailMarketing) get(EmailMarketing.class, emailmarkid);
                String Hql = "from com.krawler.crm.database.tables.EmailMarkteingTargetList em where em.emailmarketingid = ? and em.targetlistid.id in (select ct.targetlist.id from CampaignTarget ct where ct.deleted = 0 and em.emailmarketingid.campaignid.campaignid = ct.campaign.campaignid ) ";
                ll = executeQuery(Hql, new Object[]{emObj});
                dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getEmailMarkTargetList : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getUnAssignEmailMarkTargetList(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
           String campID = requestParams.get("campID").toString();
           if (!StringUtil.isNullOrEmpty(requestParams.get("emailmarkid").toString())) {
                String emailmarkid = requestParams.get("emailmarkid").toString();
                EmailMarketing emObj = (EmailMarketing) get(EmailMarketing.class, emailmarkid);
                String Hql = "from TargetList t where " +
                        " t.id not in (select et.targetlistid from EmailMarkteingTargetList et where et.emailmarketingid = ?)" +
                        " and t.id in (select ct.targetlist.id from CampaignTarget ct where ct.campaign.campaignid=? and ct.deleted=0 ) ";
                ll = executeQuery(Hql, new Object[]{emObj, campID});
           } else {
                String Hql = "from TargetList t where " +
                        " t.id in (select ct.targetlist.id from CampaignTarget ct where ct.campaign.campaignid=? and ct.deleted=0 ) ";
                ll = executeQuery(Hql, new Object[]{campID});
           }
           dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getUnAssignEmailMarkTargetList : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getTargetListTargets(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "from com.krawler.crm.database.tables.TargetListTargets targetlisttargets ";
            String companyid =requestParams.get("companyid").toString();
             ArrayList filter_names = new ArrayList();
             ArrayList filter_params = new ArrayList();

            if(requestParams.containsKey("filter_names")){
                filter_names = (ArrayList) requestParams.get("filter_names");
            }
            if(requestParams.containsKey("filter_params")){
                filter_params = (ArrayList) requestParams.get("filter_params");
            }
            
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            Hql += filterQuery;

            if(StringUtil.isNullOrEmpty(filterQuery)){
                Hql += " where ";
            }else{
                Hql += " and ";
            }
            Hql +=" targetlisttargets.deleted=0 and ((targetlisttargets.relatedto=1 and targetlisttargets.relatedid in " +
                    "(select l.leadid from CrmLead l where l.deleteflag=0 and l.isarchive='F' and l.company.companyID = ? ))" +
                    " or (targetlisttargets.relatedto=2 and targetlisttargets.relatedid in (select c.contactid from CrmContact c where c.deleteflag=0 and c.isarchive='F' and c.company.companyID = ? )) " +
                    " or (targetlisttargets.relatedto=3 and targetlisttargets.relatedid in (select t.userID from User t where t.deleteflag=0 and  t.company.companyID = ? ))"+
                    " or (targetlisttargets.relatedto=4 and targetlisttargets.relatedid in (select t.id from TargetModule t where t.deleteflag=0 and t.isarchive='F' and t.company.companyID = ? ))) ";
            filter_params.add(companyid);
            filter_params.add(companyid);
            filter_params.add(companyid);
            filter_params.add(companyid);
            ll = executeQuery(Hql,filter_params.toArray());
            dl = ll.size();
            int start = 0;
            int limit = 25;
            if (requestParams.containsKey("start") && requestParams.containsKey("limit")) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            if(requestParams.containsKey("allflag") && !Boolean.parseBoolean(requestParams.get("allflag").toString())) {
                ll = executeQueryPaging(Hql, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getTargetListTargets : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getCampEmailMarketList(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String strt="" ;
            String lim="" ;
            if(requestParams.containsKey("start") && requestParams.containsKey("limit")){
                strt = requestParams.get("start").toString();
                lim = requestParams.get("limit").toString();
            }
            int start = 0;
            int limit = 15;
            String Hql = "from com.krawler.crm.database.tables.EmailMarketing em";
            ArrayList filter_names = new ArrayList(),filter_params = new ArrayList();
            if(requestParams.containsKey("filter_names") && requestParams.containsKey("filter_params")){
                filter_names = (ArrayList) requestParams.get("filter_names");
                filter_params = (ArrayList) requestParams.get("filter_params");
            }
            Hql += StringUtil.filterQuery(filter_names, "where");
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"em.name"};
                    StringUtil.insertParamSearchString(filter_params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    Hql +=searchQuery;
                }
            }
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
            if(!StringUtil.isNullOrEmpty(strt) && !StringUtil.isNullOrEmpty(lim)) {
                start = Integer.parseInt(strt);
                limit = Integer.parseInt(lim);
            }
            ll = executeQueryPaging(Hql, filter_params.toArray(), new Integer[]{start,limit});
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getCampEmailMarketList : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getCampEmailMarketCount(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String emailmarketingid = requestParams.get("emailmarketingid").toString();

            String targCount = "from EmailMarkteingTargetList emtl where emtl.emailmarketingid.id = ? and emtl.targetlistid.deleted=0 ";
            ll = executeQuery(targCount,new Object[]{emailmarketingid});
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getCampEmailMarketCount : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getCampaignLog(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String emailmarketingid = requestParams.get("emailmarketingid").toString();
            String tzdiff = requestParams.get("tzdiff").toString();

            String campaignLog = "select (activitydate - mod(activitydate ,86400000) + "+tzdiff+") as adate,count(activitydate),sum(viewed),sum(sendingfailed), sum(case when viewed > 0 then 1 else 0 end), targetlistid, tl.name from campaign_log cl inner join targetlist tl on cl.targetlistid=tl.id  where cl.emailmarketingid = ?  group by targetlistid, adate";
            ll = executeNativeQuery(campaignLog,new Object[]{emailmarketingid});
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getCampaignLogCount : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject sendEmailMarketMail(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String userid = requestParams.get("userid").toString();
            String emailmarketingid = requestParams.get("emailmarkid").toString();
            boolean resume = requestParams.containsKey("resume") ? Boolean.TRUE.equals(requestParams.get("resume")) : false;
    		CampaignEmailNotification n = new CampaignEmailNotification(userid,emailmarketingid);
    		n.setResume(resume);
    		n.setCampaignMailDAO(campaignMailDAOObj);
    		n.setCommonTablesDAO(KwlCommonTablesDAOObj);
    		n.setCrmEmailMarketingDAO(this);
    		n.setSenderCache(mailSenderCache);
    		n.setTxnManager(txnManager);
            n.setCompanyBaseURL(requestParams.containsKey("baseurl") ? (String)requestParams.get("baseurl"):"");
    		n.queue();
            User sender = (User) get(User.class, userid);

            ll.add("We are now sending bulk emails for your marketing campaign. Depending upon the number of targets in the assigned Target List, the process may take anywhere from a few minutes to several hours. " +
                    "We will send you a notification at " + sender.getEmailID() + " as soon as this process is completed.");
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.sendEmailMarketMail : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }


    @Override
    public KwlReturnObject sendTestEmailMarketMail(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String userid = requestParams.get("userid").toString();
            String companyid = requestParams.get("companyid").toString();
            String emId = requestParams.get("emailmarkid").toString();
            String reciepientMailId = requestParams.get("reciepientMailId").toString();
    		CampaignTestEmailNotification n = new CampaignTestEmailNotification(userid,emId);
    		n.setCampaignMailDAO(campaignMailDAOObj);
    		n.setCommonTablesDAO(KwlCommonTablesDAOObj);
    		n.setCrmEmailMarketingDAO(this);
    		n.setSenderCache(mailSenderCache);
    		n.setTxnManager(txnManager);
    		n.setTestMailAddress(reciepientMailId);
            n.setCompanyBaseURL(requestParams.containsKey("baseurl") ? (String)requestParams.get("baseurl"):"");
    		n.send();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.sendEmailMarketMail : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getCampaignTarget(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String campID = "";
            int start = 0;
            int limit = 15;
            if (requestParams.containsKey("start") && requestParams.get("start") != null) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            ArrayList filter_names = new ArrayList() ,filter_params = new ArrayList();
            if(requestParams.containsKey("filter_names") && requestParams.containsKey("filter_params")){
                filter_names = (ArrayList) requestParams.get("filter_names");
                filter_params = (ArrayList) requestParams.get("filter_params");
            }

            String Hql = "from CampaignTarget t ";
            
            Hql +=com.krawler.common.util.StringUtil.filterQuery(filter_names, "where");
            int ind = Hql.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(Hql.substring(ind+1,ind+2));
                Hql = Hql.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"t.targetlist.name","t.targetlist.name"};
                    StringUtil.insertParamSearchString(filter_params, ss, 2);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    Hql +=searchQuery;
                }
            }
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
            boolean allflag = false;
            if(requestParams.containsKey("allflag") && requestParams.get("allflag")!=null) {
                allflag = Boolean.parseBoolean(requestParams.get("allflag").toString());
            }
            if(!allflag)
                ll = executeQueryPaging(Hql, filter_params.toArray(), new Integer[]{start, limit});
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getCampaignTarget : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject addCampaignTarget(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            CampaignTarget ct = null;
            ct = new CampaignTarget();

            if (jobj.has("camptargetid")) {
                ct.setId(jobj.getString("camptargetid"));
            }
            if (jobj.has("userid")) {
                User uu = (User) get(User.class, jobj.getString("userid"));
                ct.setCreator(uu);
            }
            if (jobj.has("createdon")) {
                ct.setCreatedOn(System.currentTimeMillis());
            }
            if (jobj.has("campid")) {
                CrmCampaign cc = (CrmCampaign) get(CrmCampaign.class, jobj.getString("campid"));
                ct.setCampaign(cc);
            }
            if (jobj.has("targetid")) {
                TargetList tl = (TargetList) get(TargetList.class, jobj.getString("targetid"));
                ct.setTargetlist(tl);
            }
            if (jobj.has("deleted")) {
                ct.setDeleted(jobj.getInt("deleted"));
            }

            save(ct);
            ll.add(ct);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addCampaignTarget : "+e.getMessage(), e);
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addCampaignTarget : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addCampaignTarget : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.addCampaignTarget : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject editCampaignTarget(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            CampaignTarget ct = null;
            ct = (CampaignTarget) get(CampaignTarget.class, jobj.getString("camptargetid"));
            if (jobj.has("userid")) {
                User uu = (User) get(User.class, jobj.getString("userid"));
                ct.setCreator(uu);
            }
            if (jobj.has("createdon")) {
                ct.setCreatedOn(System.currentTimeMillis());
            }
            if (jobj.has("campid")) {
                CrmCampaign cc = (CrmCampaign) get(CrmCampaign.class, jobj.getString("campid"));
                ct.setCampaign(cc);
            }
            if (jobj.has("targetid")) {
                TargetList tl = (TargetList) get(TargetList.class, jobj.getString("targetid"));
                ct.setTargetlist(tl);
            }
            if (jobj.has("deleted")) {
                ct.setDeleted(jobj.getInt("deleted"));
            }

            saveOrUpdate(ct);
            ll.add(ct);
        } catch (HibernateException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editCampaignTarget : "+e.getMessage(), e);
        } catch(JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editCampaignTarget : "+e.getMessage(), e);
        } catch(DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editCampaignTarget : "+e.getMessage(), e);
        } catch(Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.editCampaignTarget : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getUnAssignCampaignTarget(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String campID = "";
            if(requestParams.containsKey("campID") && requestParams.get("campID") != null) {
                campID = requestParams.get("campID").toString();
            }
            String companyid = requestParams.get("companyid").toString();
            String Hql = "from TargetList t where t.deleted=0 and t.id not in (select ct.targetlist.id from CampaignTarget ct where ct.campaign.campaignid = ? and ct.deleted=0 ) and t.creator.company.companyID=? ";
            ll = executeQuery(Hql, new Object[]{campID, companyid});
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getUnAssignCampaignTarget : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getColorThemes() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String hql = "SELECT t FROM templateColorTheme t WHERE t.deleted = 0";
            ll = executeQuery(hql);
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getColorThemes : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getColorThemeGroup() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String hql = "FROM colorThemeGroup grp WHERE grp.deleted != 1";
            ll = executeQuery(hql);
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getColorThemeGroup : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getEmailMrktContent(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String marketid = "";
            if(requestParams.containsKey("marketid") && requestParams.get("marketid") != null) {
                marketid = requestParams.get("marketid").toString();
            }
            String hql = "FROM EmailMarketing em WHERE em.id = ?";
            ll = executeQuery(hql, marketid);
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getEmailMrktContent : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getTemplateContent(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();

            if(requestParams.containsKey("filter_names")){
                filter_names = (ArrayList) requestParams.get("filter_names");
            }
            if(requestParams.containsKey("filter_params")){
                filter_params = (ArrayList) requestParams.get("filter_params");
            }
            String templatetaable = "EmailTemplate";
            if(requestParams.containsKey("templateclass")) {
                if(requestParams.get("templateclass").equals("DefaultTemplates"))
                templatetaable = "DefaultTemplates";
            }
            String hql = "FROM "+templatetaable+" c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            hql += filterQuery;
            
            ll = executeQuery(hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getTemplateContent : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getEmailTypeContent(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();

            if(requestParams.containsKey("filter_names")){
                filter_names = (ArrayList) requestParams.get("filter_names");
            }
            if(requestParams.containsKey("filter_params")){
                filter_params = (ArrayList) requestParams.get("filter_params");
            }
            String templatetaable = "EnumEmailType";
            
            String hql = "FROM "+templatetaable+" c ";
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            hql += filterQuery;

            ll = executeQuery(hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getEmailTypeContent : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject scheduleEmailMarketing(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            User sender = (User) get(User.class, requestParams.get("userid").toString());
            EmailMarketing eM = (EmailMarketing) get(EmailMarketing.class, requestParams.get("emailmarketingid").toString());
            scheduledmarketing sm = new scheduledmarketing();
            sm.setUserid(sender);
            sm.setEmailmarketingid(eM);
            sm.setScheduledDate(Long.parseLong(requestParams.get("scheduledate").toString()));
            sm.setScheduledtime(requestParams.get("scheduletime").toString());
            save(sm);

            ll.add(sm);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.scheduleEmailMarketing : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getScheduleEmailMarketing(HashMap<String, Object> requestParams) {
//        boolean success = false;
//        KwlReturnObject result = null;
        List lst = null;
        try {
            String hql = "";
//            ArrayList name = null;
//            ArrayList value = null;
//            ArrayList orderby = null;
//            ArrayList ordertype = null;
//            String[] searchCol = null;
//            hql = "from scheduledmarketing ";
//            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
//                name = new ArrayList((List<String>)requestParams.get("filter_names"));
//                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
//                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
//            }
//
//            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
//                searchCol = (String[])requestParams.get("searchcol");
//                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
//            }
//
//            if(requestParams.get("group_by")!=null){
//                orderby = new ArrayList((List<String>)requestParams.get("group_by"));
//                hql +=com.krawler.common.util.StringUtil.groupQuery(orderby);
//            }
//
//            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
//                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
//                ordertype = new ArrayList((List<Object>)requestParams.get("order_type"));
//                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
//            }
//
//            if(requestParams.get("select")!=null){
//                String selectstr = requestParams.get("select").toString()+" ";
//                hql = selectstr+hql;
//            }
//
//            result = StringUtil.getPagingquery(requestParams, searchCol, hql, value);
//            success = true;

            //Convert schedule date and schedule time to server timezone not GMT. Then compare the date.
            String serverTimezone = ConfigReader.getinstance().get("SERVER_TIMEZONE");//"-05:00";
//            hql = "select sm.id, DATE_FORMAT(CONVERT_TZ(ADDTIME(sm.scheduleddate, sm.scheduledtime), tz.difference,'"+serverTimezone+"'),'%H:%i:%S') as df from scheduledmarketing as sm " +
//                    " inner join users on sm.userid = users.userid " +
//                    " inner join timezone as tz on tz.timzoneid = users.timezone where " +
//                   " DATEDIFF(DATE_FORMAT(CONVERT_TZ(ADDTIME(sm.scheduleddate, sm.scheduledtime), tz.difference, '"+serverTimezone+"'),'%Y-%m-%d %H:%i:%S'), NOW()) = 0 ";
            hql = "select sm.id, DATE_FORMAT(FROM_UNIXTIME(sm.scheduleddate/1000),'%H:%i:%S') as df from scheduledmarketing as sm " +
                    " where DATEDIFF(DATE_FORMAT(FROM_UNIXTIME(sm.scheduleddate/1000),'%Y-%m-%d %H:%i:%S'), NOW()) = 0 and sm.deleted = 0";
            lst = executeNativeQuery(hql);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getScheduleEmailMarketing : "+e.getMessage(), e);
        } finally {
            return new KwlReturnObject(true, KWLErrorMsgs.S01, "", lst, lst.size());
        }
    }

    @Override
    public KwlReturnObject deleteEmailMarketingSchedule(HashMap<String, Object> requestParams) {
        List<scheduledmarketing> lst = null;
        try {
            String Hql = " from scheduledmarketing ";
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if(requestParams.containsKey("filter_names")) {
                filter_names = new ArrayList((List<String>) requestParams.get("filter_names"));
            }
            if(requestParams.containsKey("filter_values")) {
                filter_params = new ArrayList((List<String>) requestParams.get("filter_values"));
            }
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }

            Hql += filterQuery;
            lst = executeQuery(Hql, filter_params.toArray());
            if (lst != null && !lst.isEmpty()) {
                for (scheduledmarketing scheduleObj: lst) {
                    scheduleObj.setDeleted(1);
                }
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.deleteEmailMarketingSchedule : " + e.getMessage(), e);
        } finally {
            return new KwlReturnObject(true, KWLErrorMsgs.S01, "", lst, lst.size());
        }
    }
    @Override
    public KwlReturnObject getFutureScheduleEmailMarketingById(HashMap<String, Object> requestParams) {
        List lst = null;
        try {
            String Hql = " from scheduledmarketing ";
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            if(requestParams.containsKey("filter_names")) {
                filter_names = new ArrayList((List<String>) requestParams.get("filter_names"));
            }
            if(requestParams.containsKey("filter_values")) {
                filter_params = new ArrayList((List<String>) requestParams.get("filter_values"));
            }
            String filterQuery = " where scheduledDate - ? >= 0 and deleted = 0 ";
            filter_params.add(0, (new Date()).getTime());
            filterQuery += StringUtil.filterQuery(filter_names, " and ");
            int ind = filterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filterQuery.substring(ind+1,ind+2));
                filterQuery = filterQuery.replaceAll("("+index+")", filter_params.get(index).toString());
                filter_params.remove(index);
            }
            
            Hql += filterQuery;
            lst = executeQuery(Hql, filter_params.toArray());
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getFutureScheduleEmailMarketingById : " + e.getMessage(), e);
        } finally {
            return new KwlReturnObject(true, KWLErrorMsgs.S01, "", lst, lst.size());
        }
    }

    @Override
    public KwlReturnObject campEmailMarketingStatus(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        List ll1 = null;
        int dl = 0;
        try {
            String companyid = requestParams.get("companyid").toString();
            String usersList = requestParams.get("userslist").toString();
            ArrayList<Object> params=new ArrayList<Object>();
            String Hql = "select c.emailmarketingid.name, c.campaignid.campaignname, sum(c.viewed) ,count(*),c.emailmarketingid.id,sum(case when c.viewed > 0 then 1 else 0 end) from CampaignLog c where c.campaignid.company.companyID = ? ";
            params.add(companyid);
            if(requestParams.containsKey("activeCampaign")){
            	Hql += " and c.campaignid.startingdate <= ?  and  c.campaignid.endingdate >= ? ";
            	Long currentdate=System.currentTimeMillis();
            	params.add(currentdate);
            	params.add(currentdate);
            }
            String selectInQuery = Hql + " and c.campaignid.usersByUserid.userID in (" + usersList + ") and c.campaignid.deleteflag=0 group by c.emailmarketingid order by sum(c.viewed) desc";
            ll = executeQueryPaging(selectInQuery,params.toArray(), new Integer[]{0, 5});
            dl = ll.size();
            params.clear();
            String Hql1 = "select c.emailmarketingid.name, c.campaignid.campaignname, sum(c.viewed) ,count(*), c.emailmarketingid.id, sum(case when c.viewed > 0 then 1 else 0 end) from CampaignLog c where c.campaignid.company.companyID = ? ";
            params.add(companyid);
            String selectInQuery1 = Hql1 + " and c.campaignid.usersByUserid.userID in (" + usersList + ") and c.campaignid.deleteflag=0 group by c.emailmarketingid order by count(*) desc";
            ll1 = executeQueryPaging(selectInQuery1 ,params.toArray() , new Integer[]{0, 5});
            ll.addAll(ll1);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.campEmailMarketingStatus : "+e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getTemplateHTMLContent(HashMap<String, Object> requestParams) throws ServiceException {
        String htmlString = "";
        EmailTemplate emailTemp = null;
        EmailMarketing emailMar = null;
        TargetListTargets targetListTargets = null;
        User sender = null;
        StringBuffer pmsg = null;
        StringBuffer htmlmsg = null;
        Object invoker = null;
        Class arguments[] = new Class[]{};
        Object[] obj1 = new Object[]{};
        Class cl = null;
        Company company = null;
        List ll = new ArrayList();
        java.lang.reflect.Method objMethod = null;
        KwlReturnObject kmsg = null;
        String userid = "";
        String baseUrl = "";
        String marketingId = "";
        String targetListId = "";
        String domain = "";
        String recipients = "";
        String name = "";
        String classpath = "";
        int dl = 0;
        try {
            if (requestParams.containsKey("uid") && requestParams.get("uid") != null) {
                userid = requestParams.get("uid").toString();
                sender = (User) get(User.class, userid);
            }
            if (requestParams.containsKey("mid") && requestParams.get("mid") != null) {
                marketingId = requestParams.get("mid").toString();
                emailMar = (EmailMarketing) get(EmailMarketing.class, marketingId);
            }
            if (requestParams.containsKey("tuid") && requestParams.get("tuid") != null) {
                targetListId = requestParams.get("tuid").toString();
                targetListTargets = (TargetListTargets) get(TargetListTargets.class, targetListId);
            }
            if (requestParams.containsKey("cdomain") && requestParams.get("cdomain") != null) {
                domain = requestParams.get("cdomain").toString();
                baseUrl = com.krawler.common.util.URLUtil.getDomainURL(domain, false);
            }
            if(requestParams.containsKey("baseurl") && requestParams.get("baseurl") != null) {
                baseUrl = requestParams.get("baseurl").toString();
            }
            emailTemp = (EmailTemplate) get(EmailTemplate.class, emailMar.getTemplateid().getTemplateid());
            String pmsgX = emailMar.getPlaintext();
            pmsg = new StringBuffer(pmsgX);

            htmlString = URLDecoder.decode(emailMar.getHtmltext(), "utf-8");
            htmlString = htmlString.replaceAll("src=\"[^\"]*?video.jsp", "src=\""+baseUrl + "video.jsp");
            htmlmsg = new StringBuffer(htmlString);

            switch (targetListTargets.getRelatedto()) {
                case 1: // Lead
                    classpath = "com.krawler.crm.database.tables.CrmLead";
                    break;
                case 2: // Contact
                    classpath = "com.krawler.crm.database.tables.CrmContact";
                    break;
                case 3: // Users
                    classpath = "com.krawler.common.admin.User";
                    break;
                case 4: // Target Module
                    classpath = "com.krawler.crm.database.tables.TargetModule";
                    break;
                default:
                    break;
            }
            invoker = KwlCommonTablesDAOObj.getObject(classpath, targetListTargets.getRelatedid());
            cl = invoker.getClass();
            if (targetListTargets.getRelatedto() != 3) {
                objMethod = cl.getMethod("getFirstname", arguments);
                name = (String) objMethod.invoke(invoker, obj1);
                objMethod = cl.getMethod("getLastname", arguments);
                name = name + " " + (String) objMethod.invoke(invoker, obj1);
                objMethod = cl.getMethod("getEmail", arguments);
                recipients = (String) objMethod.invoke(invoker, obj1);
            } else {
                objMethod = cl.getMethod("getFirstName", arguments);
                name = (String) objMethod.invoke(invoker, obj1);
                objMethod = cl.getMethod("getLastName", arguments);
                name = name + " " + (String) objMethod.invoke(invoker, obj1);
                objMethod = cl.getMethod("getEmailID", arguments);
                recipients = (String) objMethod.invoke(invoker, obj1);
            }
            objMethod = cl.getMethod("getCompany", arguments);
            company = (Company) objMethod.invoke(invoker, obj1);
            CampaignEmailNotification n= new CampaignEmailNotification(null, null);
    		n.setCampaignMailDAO(campaignMailDAOObj);
    		n.setCommonTablesDAO(KwlCommonTablesDAOObj);
    		n.setCrmEmailMarketingDAO(this);
    		n.setSenderCache(mailSenderCache);
    		n.setTxnManager(txnManager);
            n.replaceConditionalBlock(htmlmsg, invoker, sender, company);
            n.regExMail2(pmsg, htmlmsg, invoker, sender, company, n.getDefaultsMap(emailMar.getId()));
            htmlmsg = new StringBuffer(htmlmsg.toString().replaceAll("@~@~", "#"));
            ll.add(htmlmsg);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getTemplateHTMLContent : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject unsubscribeUserMarketMail(HashMap<String, Object> requestParams) throws ServiceException {
        String response = "";
        String targetTrakid = "";
        List ll = null;
        int dl = 0;
            if (requestParams.containsKey("trackid") && requestParams.get("trackid") != null) {
                targetTrakid = requestParams.get("trackid").toString();
            }
            String Hql = "from com.krawler.crm.database.tables.CampaignLog cl where cl.targettrackerkey = ?";
            ll = executeQuery(Hql, new Object[]{targetTrakid});
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CampaignLog campaignLogobj = (CampaignLog) ite.next();
                campaignLogobj.setHits(campaignLogobj.getHits() + 1);
                campaignLogobj.setActivitytype(CampaignConstants.Crm_isunsubscribe);
                campaignLogobj.setModifiedon(new Date());
                save(campaignLogobj);
                String listtitle = campaignLogobj.getEmailmarketingid().getName();
                String headerContent = "<head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'>" +
                        "<meta name='description' content='" + listtitle + " Email Forms'>" +
                        "<meta name='keywords' content='" + listtitle + "'>" +
                        "<title>" + listtitle + "</title>" +
                        "<style type='text/css'>" +
                        "body {font-family: 'trebuchet MS', tahoma, verdana, arial, helvetica, sans-serif;" +
                        "background-color: #eee;margin: 0;} " +
                        "#content {width: 100%;position:absolute;padding:20px;top:5%;}" +
                        ".content {width: 600px;margin:auto;padding:20px;border:10px solid #ccc;background-color: white;}" +
                        ".alert {font-size:20px;line-height:200%;color:#FF0000;font-family:Arial;font-weight:bold;}" +
                        "p, label, .formText {line-height:150%;font-family:Arial;font-size: 14px;color: #333333;}" +
                        "</style>" +
                        "</head>";
                String bodyContent = "<body>" +
                        "<div id='content'><div id=unsubThankYouPage class='content'>" +
                        "<div class='alert' id='unsubTitle'>Unsubscribe Successful</div>" +
                        "<p id='unsubContent'><p>You have been successfully removed from this email list.</p>" +
                        "<p>You will receive one final email to confirm that we unsubscribed you.</p></p></div></div>" +
                        "</div>" +
                        "</body>";
                response = "<html>" + headerContent + bodyContent + "</html>";
                String unProtectedURL = com.krawler.common.util.URLUtil.getDomainURL(campaignLogobj.getCampaignid().getCompany().getSubDomain(), false);
                if (requestParams.containsKey("baseurl") && requestParams.get("baseurl") != null) {
                    unProtectedURL = requestParams.get("baseurl").toString();
                }

                String domainURL =  unProtectedURL+ "crm/emailMarketing/mail/subscribeUserMarketMail.do?trackid=" + targetTrakid;
                String recipients = campaignLogobj.getTargetid().getEmailid();
                String subject = listtitle + ": You are now unsubscribed";
                bodyContent = "<body>" +
                        "<div style='width: 100%;position:absolute;padding:20px;top:5%;'><div style='width: 600px;margin:auto;padding:20px;border:10px solid #ccc;background-color: white;'><div style='font-size: 20px; line-height: 200%; color: rgb(255, 0, 0); font-family: Arial; font-weight: bold;'>We have removed your email address from our list.</div>" +
                        "<div><p style='line-height: 150%; font-family: Arial; font-size: 14px; color: rgb(51, 51, 51);'>We're sorry to see you go.</p></div>" +
                        "<p style='line-height: 150%; font-family: Arial; font-size: 14px; color: rgb(51, 51, 51);'>Was this a mistake?" +
                        "<br/>Did you forward your email to a friend, and they accidentally clicked the unsubscribe link?" +
                        "<br/>If this was a mistake, you can re-subscribe at:" +
                        "<br/><a target='_blank' style='color: rgb(0, 0, 255);' href='" + domainURL + "'>Subscribe</a></p></div>" +
                        "</body>";
                String htmlmsg = "<html>" + headerContent + bodyContent + "</html>";
                String pmsg = htmlmsg;
                String fromAddress = campaignLogobj.getEmailmarketingid().getFromaddress();
                String fromName = campaignLogobj.getEmailmarketingid().getFromname();
                try {
                    SendMailHandler.postMail(new String[]{recipients}, subject, htmlmsg, pmsg, fromAddress, fromName);
                } catch (MessagingException mE) {
                    logger.warn(mE.getMessage(), mE);
                } catch (UnsupportedEncodingException ex) {
                    logger.warn(ex.getMessage(), ex);
                }
            }
            ll = new ArrayList();
            ll.add(response);
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject subscribeUserMarketMail(HashMap<String, Object> requestParams) throws ServiceException {
        String response = "";
        String targetTrakid = "";
        List ll = null;
        int dl = 0;
            if (requestParams.containsKey("trackid") && requestParams.get("trackid") != null) {
                targetTrakid = requestParams.get("trackid").toString();
            }
            String Hql = "from com.krawler.crm.database.tables.CampaignLog cl where cl.targettrackerkey = ?";
            ll = executeQuery(Hql, new Object[]{targetTrakid});
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CampaignLog campaignLogobj = (CampaignLog) ite.next();
                campaignLogobj.setActivitytype("targeted");
                saveOrUpdate(campaignLogobj);
                String listtitle = campaignLogobj.getEmailmarketingid().getName();
                String unProtectedURL = com.krawler.common.util.URLUtil.getDomainURL(campaignLogobj.getCampaignid().getCompany().getSubDomain(), false);
                if (requestParams.containsKey("baseurl") && requestParams.get("baseurl") != null) {
                    unProtectedURL = requestParams.get("baseurl").toString();
                }

                String domainURL = unProtectedURL + "crm/emailMarketing/mail/confirmSubscribeUserMarketMail.do?trackid=" + targetTrakid;
                String headerContent = "<head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'>" +
                        "<meta name='description' content='" + listtitle + " Email Forms'>" +
                        "<meta name='keywords' content='" + listtitle + "'>" +
                        "<title>" + listtitle + "</title>" +
                        "<style type='text/css'>" +
                        "body {font-family: 'trebuchet MS', tahoma, verdana, arial, helvetica, sans-serif;" +
                        "background-color: #eee;margin: 0;} " +
                        "#content {width: 100%;position:absolute;padding:20px;top:5%;}" +
                        ".content {width: 600px;margin:auto;padding:20px;border:10px solid #ccc;background-color: white;}" +
                        ".alert {font-size:20px;line-height:200%;color:#FF0000;font-family:Arial;font-weight:bold;}" +
                        "p, label, .formText {line-height:150%;font-family:Arial;font-size: 14px;color: #333333;}" +
                        "</style>" +
                        "</head>";
                String bodyContent = "<body>" +
                        "<div id='content'><div class='content'><div style='font-size: 20px; line-height: 200%; color: rgb(255, 0, 0); font-family: Arial; font-weight: bold;'>" +
                        "Almost Finished...</div>" +
                        "<p style='line-height: 150%; font-family: Arial; font-size: 14px; color: rgb(51, 51, 51);'>We need to confirm your email address." +
                        "<br/>To complete the subscription process, please click the link in the email we just sent you." +
                        "</p></div>" +
                        "</body>";

                response = "<html>" + headerContent + bodyContent + "</html>";

                String recipients = campaignLogobj.getTargetid().getEmailid();
                String subject = listtitle + ": Please Confirm Subscription";
                bodyContent = "<body>" +
                        "<div style='width: 100%;position:absolute;padding:20px;top:5%;'><div style='width: 600px;margin:auto;padding:20px;border:10px solid #ccc;background-color: white;'>" +
                        "<p style='line-height: 150%; font-family: Arial; font-size: 14px; color: rgb(51, 51, 51);'>" +
                        "<a target='_blank' style='color: rgb(0, 0, 255);' href='" + domainURL + "'>Click here to confirm your subscription to our list.</a>" +
                        "<br/>If you received this email by mistake, simply delete it. You won't be subscribed if you don't click the confirmation link above." +
                        "</p></div>" +
                        "</body>";
                String htmlmsg = "<html>" + headerContent + bodyContent + "</html>";
                String pmsg = htmlmsg;
                String fromAddress = campaignLogobj.getEmailmarketingid().getFromaddress();
                String fromName = campaignLogobj.getEmailmarketingid().getFromname();
                try {
                    SendMailHandler.postMail(new String[]{recipients}, subject, htmlmsg, pmsg, fromAddress, fromName);
                } catch (MessagingException mE) {
                    logger.warn(mE.getMessage(), mE);
                } catch (UnsupportedEncodingException ex) {
                    logger.warn(ex.getMessage(), ex);
                }
            }
            ll = new ArrayList();
            ll.add(response);
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject viewedEmailMarketMail(HashMap<String, Object> requestParams) throws ServiceException {
        String targetTrakid = "";
        List ll = null;
        int dl = 0;
            if (requestParams.containsKey("trackid") && requestParams.get("trackid") != null) {
                targetTrakid = requestParams.get("trackid").toString();
            }
            String Hql = "from com.krawler.crm.database.tables.CampaignLog cl where cl.targettrackerkey = ?";
            ll = executeQuery(Hql, new Object[]{targetTrakid});
            dl = ll.size();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CampaignLog campaignLogobj = (CampaignLog) ite.next();
                campaignLogobj.setViewed(campaignLogobj.getViewed()+1);
                campaignLogobj.setModifiedon(new Date());
                save(campaignLogobj);
                CampaignTimeLog ctLog = new CampaignTimeLog();
                ctLog.setCampaignLog(campaignLogobj);
                ctLog.setViewedOn(new Date());
                save(ctLog);
            }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    // Kuldeep Singh : Capture Lead from Campaign
    @Override
    public KwlReturnObject captureLeadFromCampaign(List ll) throws ServiceException {

        int dl=0;
        CampaignLog campaignLogobj = (CampaignLog)ll.get(0);
        boolean captureLeadFlag = campaignLogobj.getEmailmarketingid().isCaptureLead();
        if(captureLeadFlag){
            int relatedTo = campaignLogobj.getTargetid().getRelatedto();
            if(relatedTo==4){  // Capture Lead when it is related to Target Module
                try {

                    String recid = campaignLogobj.getTargetid().getRelatedid();
                    TargetModule target = (TargetModule) get(TargetModule.class, recid);

                    String companyid = target.getCompany().getCompanyID();
                    String userid = campaignLogobj.getEmailmarketingid().getCreator().getUserID();

                    String firstname = target.getFirstname();
                    String laststname = target.getLastname();
                    String email = target.getEmail();

                    // Check for Duplicate lead with same firstname,lastname and email
                    dl = crmCommonDAOObj.chekcDuplicateEntryForLead(firstname, laststname, email, companyid);

                    if(dl==0){
                        String id = java.util.UUID.randomUUID().toString();

                        JSONObject jobj = new JSONObject();
                        jobj.put("name", firstname+" "+laststname);
                        jobj.put("email", email);

                        // Capture Lead : Make entry of target in Lead module
                        saveRelatedToTarget(1,jobj,id,userid,companyid);

                        //Validate record
                        crmCommonDAOObj.validaterecord(Constants.Crm_Lead_modulename, id, companyid);
                    }
                    
                } catch (JSONException ex) {
                    logger.warn(ex.getMessage(), ex);
                    throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.captureLeadFromCampaign", ex);
                }
            }
        }
           
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    
   @Override
    public KwlReturnObject confirmsubscribeUserMarketMail(HashMap<String, Object> requestParams) throws ServiceException {
        String response = "";
        String targetTrakid = "";
        List ll = null;
        int dl = 0;
            if (requestParams.containsKey("trackid") && requestParams.get("trackid") != null) {
                targetTrakid = requestParams.get("trackid").toString();
            }
            String Hql = "from com.krawler.crm.database.tables.CampaignLog cl where cl.targettrackerkey = ?";
            ll = executeQuery(Hql, new Object[]{targetTrakid});
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                CampaignLog campaignLogobj = (CampaignLog) ite.next();
                campaignLogobj.setHits(campaignLogobj.getHits() - 1);
                campaignLogobj.setModifiedon(new Date());
                save(campaignLogobj);
                String listtitle = campaignLogobj.getEmailmarketingid().getName();
                String headerContent = "<head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'>" +
                        "<meta name='description' content='" + listtitle + " Email Forms'>" +
                        "<meta name='keywords' content='" + listtitle + "'>" +
                        "<title>" + listtitle + "</title>" +
                        "<style type='text/css'>" +
                        "body {font-family: 'trebuchet MS', tahoma, verdana, arial, helvetica, sans-serif;" +
                        "background-color: #eee;margin: 0;} " +
                        "#content {width: 100%;position:absolute;padding:20px;top:5%;}" +
                        ".content {width: 600px;margin:auto;padding:20px;border:10px solid #ccc;background-color: white;}" +
                        ".alert {font-size:20px;line-height:200%;color:#FF0000;font-family:Arial;font-weight:bold;}" +
                        "p, label, .formText {line-height:150%;font-family:Arial;font-size: 14px;color: #333333;}" +
                        "</style>" +
                        "</head>";
                String bodyContent = "<body>" +
                        "<div id='content'><div class='content'><div style='font-size: 20px; line-height: 200%; color: rgb(255, 0, 0); font-family: Arial; font-weight: bold;'>" +
                        "Subscription Confirmed</div>" +
                        "<p style='line-height: 150%; font-family: Arial; font-size: 14px; color: rgb(51, 51, 51);'>Your subscription to our list has been confirmed." +
                        "<br/>Thank you for subscribing!" +
                        "</p></div>" +
                        "</body>";

                response = "<html>" + headerContent + bodyContent + "</html>";
                String unProtectedURL = com.krawler.common.util.URLUtil.getDomainURL(campaignLogobj.getCampaignid().getCompany().getSubDomain(), false);
                if (requestParams.containsKey("baseurl") && requestParams.get("baseurl") != null) {
                    unProtectedURL = requestParams.get("baseurl").toString();
                }
                String domainURL = unProtectedURL + "crm/emailMarketing/mail/unsubscribeUserMarketMail.do?trackid=" + targetTrakid;
                String recipients = campaignLogobj.getTargetid().getEmailid();
                String subject = listtitle + ": Subscription Confirmed";
                bodyContent = "<body><div style='width:600px;margin:auto;padding:20px;background-color:white;border:10px solid rgb(204, 204, 204);'>" +
                        "<div style='font-size:20px;line-height:200%;color:rgb(255, 0, 0);font-family:Arial;font-weight:bold;'>" +
                        "Your subscription to our list has been confirmed." +
                        "</div><div><p style='line-height:150%;font-family:Arial;font-size:14px;color:rgb(51, 51, 51);'>" +
                        "If at any time you wish to stop receiving our emails, you can <a target=\"_blank\" href=\"" + domainURL + "\">Unsubscribe here</a>.</p></div>" +
                        "</div></body>";
                String htmlmsg = "<html>" + headerContent + bodyContent + "</html>";
                String pmsg = htmlmsg;
                String fromAddress = campaignLogobj.getEmailmarketingid().getFromaddress();
                String fromName = campaignLogobj.getEmailmarketingid().getFromname();
                try {
                    SendMailHandler.postMail(new String[]{recipients}, subject, htmlmsg, pmsg, fromAddress, fromName);
                } catch (MessagingException mE) {
                    logger.warn(mE.getMessage(), mE);
                } catch (UnsupportedEncodingException ex) {
                    logger.warn(ex.getMessage(), ex);
                }
            }
            ll = new ArrayList();
            ll.add(response);
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject saveTargetListTargets(HashMap<String, Object> requestParams) throws ServiceException {
        int mode = 0;
        int dl = 0;
        String listId = "";
        String userid = "";
        String companyid = "";
        String targets = "";
        String returnMsg = "{success:false}";
        List ll = new ArrayList();
        try {
            TargetList targetList = new TargetList();
            if (requestParams.containsKey("mode") && requestParams.get("mode") != null) {
                mode = Integer.parseInt(requestParams.get("mode").toString());
            }
            if (requestParams.containsKey("listid") && requestParams.get("listid") != null) {
                listId = requestParams.get("listid").toString();
                targetList = (TargetList) get(TargetList.class, listId);
            }
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            if (mode == 0) { // new targetList
                if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                    userid = requestParams.get("userid").toString();
                }
                targetList.setCreatedon(new Date());
                User creator = (User) get(User.class, userid);
                targetList.setCreator(creator);
            } else if(mode == 2) { // delete targetlist
                targetList.setDeleted(1);
            }
            targetList.setModifiedon(new Date());
            if (requestParams.containsKey("name") && requestParams.get("name") != null) {
                targetList.setName(requestParams.get("name").toString());
            }
            if (requestParams.containsKey("desc") && requestParams.get("desc") != null) {
                targetList.setDescription(StringUtil.serverHTMLStripper(requestParams.get("desc").toString()));
            }
            if (requestParams.containsKey("targetsource") && requestParams.get("targetsource") != null) {
                targetList.setTargetsource(StringUtil.serverHTMLStripper(requestParams.get("targetsource").toString()));
            }
            targetList.setSaveflag(1);
            save(targetList);
            returnMsg = "{success:true,listid:'"+targetList.getId()+"'}";
            ll.add(returnMsg);
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveTargetListTargets", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public void saveRelatedToTarget(int relatedTo, JSONObject target, String id, String userid, String companyid) throws ServiceException {
        String fname = "";
        String lname = "";
        String classpath = "";
        String classpath1 = "";
        java.lang.reflect.Constructor co = null;
        java.lang.reflect.Constructor co1 = null;
        Object invoker = null;
        Class cl = null;
        Object invoker1 = null;
        Class cl1 = null;
        java.lang.reflect.Method objMethod = null;
        Object[] obj1 = new Object[]{};
        int validflag = 1;
        String emailRegex = Constants.emailRegex;
        String email = "";
        int pos = 0;
        try {
            email = target.getString("email");
            pos = target.getString("name").indexOf(' ');
            if (!email.matches(emailRegex)) {
                validflag = 0;
            }
            if (pos <= 0) {
                lname = target.getString("name").trim();
            } else {
                String[] username = target.getString("name").split(" ");
                fname = username[0].trim();
                lname = target.getString("name").substring(pos, target.getString("name").length()).trim();
            }
            Company company = (Company) get(Company.class, companyid);
            User user = (User) get(User.class, userid);
            if (relatedTo == 1) {
                classpath = "com.krawler.crm.database.tables.CrmLead";
            } else if (relatedTo == 2) {
                classpath = "com.krawler.crm.database.tables.CrmContact";
            } else if (relatedTo == 4) {
                classpath = "com.krawler.crm.database.tables.TargetModule";

            }
            cl = Class.forName(classpath);
            co = cl.getConstructor();
            invoker = co.newInstance();

            objMethod = cl.getMethod("setCompany", Company.class);
            obj1 = new Object[]{company};
            objMethod.invoke(invoker, obj1);

            objMethod = cl.getMethod("setFirstname", String.class);
            obj1 = new Object[]{fname};
            objMethod.invoke(invoker, obj1);

            objMethod = cl.getMethod("setLastname", String.class);
            obj1 = new Object[]{lname};
            objMethod.invoke(invoker, obj1);

            objMethod = cl.getMethod("setEmail", String.class);
            obj1 = new Object[]{email};
            objMethod.invoke(invoker, obj1);
            
            if (relatedTo != 1) {
                objMethod = cl.getMethod("setUsersByUserid", User.class);
                obj1 = new Object[]{user};
                objMethod.invoke(invoker, obj1);
            }
            
            objMethod = cl.getMethod("setUsersByUpdatedbyid", User.class);
            obj1 = new Object[]{user};
            objMethod.invoke(invoker, obj1);

            objMethod = cl.getMethod("setUpdatedOn", Long.class);
            obj1 = new Object[]{System.currentTimeMillis()};
            objMethod.invoke(invoker, obj1);

            objMethod = cl.getMethod("setUsersByCreatedbyid", User.class);
            obj1 = new Object[]{user};
            objMethod.invoke(invoker, obj1);

            if (relatedTo == 4) {
                objMethod = cl.getMethod("setId", String.class);
                obj1 = new Object[]{id};
                objMethod.invoke(invoker, obj1);
            }

            if (relatedTo == 2) {
                objMethod = cl.getMethod("setContactid", String.class);
                obj1 = new Object[]{id};
                objMethod.invoke(invoker, obj1);
            }

            if (relatedTo == 1) {
                objMethod = cl.getMethod("setLeadid", String.class);
                obj1 = new Object[]{id};
                objMethod.invoke(invoker, obj1);

                objMethod = cl.getMethod("setType", String.class);
                obj1 = new Object[]{"0"};
                objMethod.invoke(invoker, obj1);
                
                objMethod = cl.getMethod("setIsconverted", String.class);
                obj1 = new Object[]{"0"};
                objMethod.invoke(invoker, obj1);

                objMethod = cl.getMethod("setIstransfered", String.class);
                obj1 = new Object[]{"0"};
                objMethod.invoke(invoker, obj1);
            }
            objMethod = cl.getMethod("setValidflag", int.class);
            obj1 = new Object[]{validflag};
            objMethod.invoke(invoker, obj1);

            objMethod = cl.getMethod("setCreatedOn", Long.class);
            obj1 = new Object[]{System.currentTimeMillis()};
            objMethod.invoke(invoker, obj1);

            save(invoker);
            if (relatedTo == 1) {

                classpath1 = "com.krawler.crm.database.tables.LeadOwners";
                cl1 = Class.forName(classpath1);
                co1 = cl1.getConstructor();
                invoker1 = co1.newInstance();

                objMethod = cl1.getMethod("setLeadid", cl);
                obj1 = new Object[]{invoker};
                objMethod.invoke(invoker1, obj1);

                objMethod = cl1.getMethod("setUsersByUserid", User.class);
                obj1 = new Object[]{user};
                objMethod.invoke(invoker1, obj1);

                objMethod = cl1.getMethod("setMainOwner", boolean.class);
                obj1 = new Object[]{true};
                objMethod.invoke(invoker1, obj1);

                save(invoker1);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveRelatedToTarget", e);
        }
    }
    @Override
    public KwlReturnObject deleteEmailmarketingTargetList(HashMap<String, Object> requestParams) throws ServiceException {
        
        List ll = null;
        int dl = 0;
        try {
            String emailMarketingId = requestParams.get("emailmarkid").toString();
            EmailMarketing emailMarketing = (EmailMarketing) get(EmailMarketing.class, emailMarketingId);
            String Hql = "DELETE from com.krawler.crm.database.tables.EmailMarkteingTargetList tl where tl.emailmarketingid=?";
            executeUpdate(Hql, new Object[]{emailMarketing});
            
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.deleteEmailmarketingTargetList", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject mapEmailmarketingTargetList(HashMap<String, Object> requestParams) throws ServiceException {

        List ll = new ArrayList();
        int dl = 0;
        String returnMsg = "{success:false}";
        try {
            String emailMarketingId = requestParams.get("emailmarkid").toString();
            EmailMarketing emailMarketing = (EmailMarketing) get(EmailMarketing.class, emailMarketingId);
            JSONArray targetDataArray = new JSONArray(requestParams.get("targetlist").toString());
            for (int cnt = 0; cnt < targetDataArray.length(); cnt++) {
                JSONObject target = targetDataArray.getJSONObject(cnt);
                EmailMarkteingTargetList emailMarkObj = new EmailMarkteingTargetList();
                TargetList targetObj = (TargetList) get(TargetList.class, target.getString("listid"));
                emailMarkObj.setTargetlistid(targetObj);
                emailMarkObj.setEmailmarketingid(emailMarketing);
                emailMarkObj.setModifiedon(new Date());
                save(emailMarkObj);
            }
            returnMsg = "{success:true}";
            ll.add(returnMsg);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveCampEmailMarketConfig", ex);
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.mapEmailmarketingTargetList", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    @Override
    public void deleteCampaignTarget(HashMap<String, Object> requestParams) throws ServiceException {

        try {
            String campaignid="";
            if(requestParams.containsKey("campaignid") && requestParams.get("campaignid") != null) {
                 campaignid = requestParams.get("campaignid").toString();
            }
            if(!StringUtil.isNullOrEmpty(campaignid)){
                String hql="delete from CampaignTarget c where c.campaign.campaignid = ? ";
                executeUpdate(hql,campaignid);
            }
            
        }catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.deleteCampaignTarget", ex);
        }
        
    }

    @Override
    public KwlReturnObject saveCampaignTargetList(HashMap<String, Object> requestParams) throws ServiceException {

        List ll = new ArrayList();
        int dl = 0;
        String returnMsg = "{success:false}";
        try {
            String userid = requestParams.get("userid").toString();
            User user = (User) get(User.class, userid);
            String campaignid = requestParams.get("campaignid").toString();
            CrmCampaign cc = (CrmCampaign) get(CrmCampaign.class, campaignid);
            JSONArray targetDataArray = new JSONArray(requestParams.get("targetlist").toString());
            for (int cnt = 0; cnt < targetDataArray.length(); cnt++) {
                JSONObject target = targetDataArray.getJSONObject(cnt);
                TargetList targetlistObj = (TargetList) get(TargetList.class, target.getString("listid"));

                CampaignTarget ct = new CampaignTarget();
                ct.setId(UUID.randomUUID().toString());
                ct.setCreator(user);
                ct.setCreatedon(new Date());
                ct.setCampaign(cc);
                ct.setTargetlist(targetlistObj);
                ct.setDeleted(0);
                save(ct);
            }
            returnMsg = "{success:true}";
            ll.add(returnMsg);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveCampaignTargetList", ex);
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveCampaignTargetList", ex);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveCampaignTargetList", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject saveCampEmailMarketConfig(HashMap<String, Object> requestParams) throws ServiceException {
        int mode = 0;
        String emailMarketingId = "";
        String userid = "";
        String unsub = "";
        String fwdfriend = "";
        String archive = "";
        String updatelink = "";
        String themeId = "defaulttheme";
        String returnMsg = "{success:false}";
        List ll = new ArrayList();
        int dl = 0;
        try {
            EmailMarketing emailMarketing = new EmailMarketing();
            if (requestParams.containsKey("mode") && requestParams.get("mode") != null) {
                mode = Integer.parseInt(requestParams.get("mode").toString());
            }
            if (requestParams.containsKey("emailmarkid") && requestParams.get("emailmarkid") != null) {
                emailMarketingId = requestParams.get("emailmarkid").toString();
            }
            if (mode == 1) {
                emailMarketing = (EmailMarketing) get(EmailMarketing.class, emailMarketingId);
                
            } else {
                if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                    userid = requestParams.get("userid").toString();
                }
                emailMarketing.setCreatedon(new Date());
                User creator = (User) get(User.class, userid);
                emailMarketing.setCreator(creator);
            }
            if (requestParams.containsKey("name") && requestParams.get("name") != null) {
                emailMarketing.setName(requestParams.get("name").toString());
            }
            if (requestParams.containsKey("marketingsubject") && requestParams.get("marketingsubject") != null) {
                emailMarketing.setSubject(requestParams.get("marketingsubject").toString());
            }
            if (requestParams.containsKey("fromname") && requestParams.get("fromname") != null) {
                emailMarketing.setFromname(requestParams.get("fromname").toString());
                emailMarketing.setReplytoname(requestParams.get("fromname").toString());
            }
            if (requestParams.containsKey("fromaddress") && requestParams.get("fromaddress") != null) {
                emailMarketing.setFromaddress(requestParams.get("fromaddress").toString());
            }
            if (requestParams.containsKey("replyaddress") && requestParams.get("replyaddress") != null) {
                emailMarketing.setReplytoaddress(requestParams.get("replyaddress").toString());
            }
            if (requestParams.containsKey("inboundemail") && requestParams.get("inboundemail") != null) {
                emailMarketing.setInboundemailid(requestParams.get("inboundemail").toString());
            }
            if (requestParams.containsKey("htmlcont") && requestParams.get("htmlcont") != null) {
                emailMarketing.setHtmltext(URLEncoder.encode(requestParams.get("htmlcont").toString(), "utf-8"));
            }
            if (requestParams.containsKey("plaincont") && requestParams.get("plaincont") != null) {
                emailMarketing.setPlaintext(requestParams.get("plaincont").toString());
            }
            if (requestParams.containsKey("unsub") && requestParams.get("unsub") != null) {
                unsub = requestParams.get("unsub").toString();
                emailMarketing.setUnsubscribelink(unsub);
            }
            if (requestParams.containsKey("fwdfriend") && requestParams.get("fwdfriend") != null) {
                fwdfriend = requestParams.get("fwdfriend").toString();
                emailMarketing.setFwdfriendlink(fwdfriend);
            }
            if (requestParams.containsKey("archive") && requestParams.get("archive") != null) {
                archive = requestParams.get("archive").toString();
                emailMarketing.setArchivelink(archive);
            }
            if (requestParams.containsKey("updatelink") && requestParams.get("updatelink") != null) {
                updatelink = requestParams.get("updatelink").toString();
                emailMarketing.setUpdateprofilelink(updatelink);
            }
            if (requestParams.containsKey("templateid") && requestParams.get("templateid") != null) {
                EmailTemplate emailTempl = (EmailTemplate) get(EmailTemplate.class, requestParams.get("templateid").toString());
                emailMarketing.setTemplateid(emailTempl);
            }
            if (requestParams.containsKey("campid") && requestParams.get("campid") != null) {
                CrmCampaign campaignObj = (CrmCampaign) get(CrmCampaign.class, requestParams.get("campid").toString());
                emailMarketing.setCampaignid(campaignObj);
            }
            if (requestParams.containsKey("colortheme") && requestParams.get("colortheme") != null) {
                if(!StringUtil.isNullOrEmpty(requestParams.get("colortheme").toString())){
                    themeId = requestParams.get("colortheme").toString();
                }
                templateColorTheme _ct = (templateColorTheme) get(templateColorTheme.class, themeId);
                emailMarketing.setColortheme(_ct);
            }
            if (requestParams.containsKey("csaccept") && requestParams.get("csaccept") != null) {
                emailMarketing.setCanSpamAccepted((Boolean)requestParams.get("csaccept"));
            }
            if (requestParams.containsKey("captureLead") && requestParams.get("captureLead") != null) {
                emailMarketing.setCaptureLead((Boolean)requestParams.get("captureLead"));
            }
            emailMarketing.setModifiedon(new Date());
            save(emailMarketing);
            returnMsg = "{success:true}";
            ll.add(emailMarketing);
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveCampEmailMarketConfig", ex);
        }  catch (UnsupportedEncodingException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveCampEmailMarketConfig", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
        public KwlReturnObject saveModuleTargetsForTemp(HashMap<String, Object> requestParams) throws ServiceException {
        TargetList targetList = null;
        Object invoker = null;
        Class arguments[] = new Class[]{};
        Object[] obj1 = new Object[]{};
        Class cl = null;
        java.lang.reflect.Method objMethod = null;
        int relatedto = 0;
        int dl = 0;
        String tlid = "";
        String name = "";
        String fname ="";
        String lname ="";
        String email = "";
        String classpath = "";
        boolean isValid = false;
        List ll = new ArrayList();
        KwlReturnObject kmsg = null;
        try {
            if (requestParams.containsKey("relatedto") && requestParams.get("relatedto") != null) {
                relatedto = Integer.parseInt(requestParams.get("relatedto").toString());
            }
            if (requestParams.containsKey("listid") && requestParams.get("listid") != null && requestParams.get("listid") != "") {
                tlid = requestParams.get("listid").toString();
                targetList = (TargetList) get(TargetList.class, tlid);
            } else {
                targetList = new TargetList();
                targetList.setCreatedon(new Date());
                if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                    User creator = (User) get(User.class, requestParams.get("userid").toString());
                    targetList.setCreator(creator);
                }
                save(targetList);
            }
            if (requestParams.containsKey("data") && requestParams.get("data") != null) {
                JSONArray jArr = new JSONArray(requestParams.get("data").toString());
                for (int i = 0; i < jArr.length(); i++) {
                    isValid = false;
                    String rid = jArr.getJSONObject(i).getString("rid");
                    switch (relatedto) {
                        case 1:
                            classpath = "com.krawler.crm.database.tables.CrmLead";
                            break;
                        case 2:
                            classpath = "com.krawler.crm.database.tables.CrmContact";
                            break;
                        case 3:
                            classpath = "com.krawler.common.admin.User";
                            break;
                        case 4: // Target Module
                            classpath = "com.krawler.crm.database.tables.TargetModule";
                            break;
                    }
                    invoker = KwlCommonTablesDAOObj.getObject(classpath, rid);
                    cl = invoker.getClass();
                    if (relatedto == 3) { // User
                        objMethod = cl.getMethod("getFirstName", arguments);
                        fname= (String) objMethod.invoke(invoker, obj1);
                        objMethod = cl.getMethod("getLastName", arguments);
                        lname= (String) objMethod.invoke(invoker, obj1);
                        objMethod = cl.getMethod("getEmailID", arguments);
                        email = (String) objMethod.invoke(invoker, obj1);
                        isValid = true;
                    } else if(relatedto == 1) {  // Lead 
                        objMethod = cl.getMethod("getFirstname", arguments);
                        fname= (String) objMethod.invoke(invoker, obj1);
                        objMethod = cl.getMethod("getLastname", arguments);
                        lname= (String) objMethod.invoke(invoker, obj1);
                        objMethod = cl.getMethod("getEmail", arguments);
                        email = (String) objMethod.invoke(invoker, obj1);
                        objMethod = cl.getMethod("getType");
                        String type = (String) objMethod.invoke(invoker);
                        if("1".equals(type)){
                        	List l=executeQuery("from com.krawler.crm.database.tables.CrmContact c where c.Lead.leadid = ?", rid);
                        	for(Object o:l){
                                String c_rid= (String) o.getClass().getMethod("getContactid").invoke(o);
                                String c_fname= (String) o.getClass().getMethod("getFirstname").invoke(o);
                                String c_lname= (String) o.getClass().getMethod("getLastname").invoke(o);
                                String c_email = (String) o.getClass().getMethod("getEmail").invoke(o);
                                saveTargetsForTemp(targetList, c_rid, 2, c_fname,c_lname, c_email);   
                                dl++;
                        	}
                        }
                        isValid = true;
                    } else if(relatedto == 2 || relatedto == 4) {  // Contact and Target
                        objMethod = cl.getMethod("getFirstname", arguments);
                        fname= (String) objMethod.invoke(invoker, obj1);
                        objMethod = cl.getMethod("getLastname", arguments);
                        lname= (String) objMethod.invoke(invoker, obj1);
                        objMethod = cl.getMethod("getEmail", arguments);
                        email = (String) objMethod.invoke(invoker, obj1);
                        isValid = true;
                    } 
                    if (isValid) {
                        saveTargetsForTemp(targetList, rid, relatedto, fname,lname, email);
                        dl++;
                    }
                }
            }
            ll.add(targetList);
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage());
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveModuleTargetsForTemp", ex);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage());
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveModuleTargetsForTemp", ex);
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveModuleTargetsForTemp", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

	@Override
	public TargetListTargets saveTargetsForTemp(TargetList targetId, String rid, int rto, String fname, String lname, String email) {
		
		String hql = "from TargetListTargets t where t.targetlistid= ?  and t.emailid=? and t.deleted=0 and t.relatedid= ?";
		if (!executeQuery(hql, new Object[] { targetId, email,rid }).isEmpty()) {
			throw new HibernateException("Duplicate target in target list :" + email);
		}
		TargetListTargets targetObj = new TargetListTargets();
		targetObj.setFname(fname);
		targetObj.setLname(lname);
		targetObj.setEmailid(email);
		targetObj.setTargetlistid(targetId);
		targetObj.setDeleted(0);
		targetObj.setRelatedid(rid);
		targetObj.setRelatedto(rto);
		save(targetObj);

		return targetObj;
	}

    @Override
    public KwlReturnObject importTargetList(HashMap<String, Object> requestParams) throws ServiceException {
        TargetList targetList = null;
        String importtlID = "";
        String TLID = "";
        String userid = "";
        List ll = new ArrayList();
        int dl = 0;
        try {
            if (requestParams.containsKey("importtl") && requestParams.get("importtl") != null) {
                importtlID = requestParams.get("importtl").toString();
            }
            if (requestParams.containsKey("listid") && requestParams.get("listid") != null) {
                TLID = requestParams.get("listid").toString();
                targetList = (TargetList) get(TargetList.class, TLID);
            }
            if (targetList == null) {
                targetList = new TargetList();
                targetList.setCreatedon(new Date());
                if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                    userid = requestParams.get("userid").toString();
                    User creator = (User) get(User.class, userid);
                    targetList.setCreator(creator);
                }
                save(targetList);
            }
            String Hql = "from com.krawler.crm.database.tables.TargetListTargets targetlisttargets where targetlisttargets.targetlistid.id = ? and targetlisttargets.deleted=0 ";
            ll = executeQuery(Hql, new Object[]{importtlID});
            dl = ll.size();
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                TargetListTargets obj = (TargetListTargets) ite.next();
                TargetListTargets newTL = new TargetListTargets();
                newTL = new TargetListTargets();
                newTL.setFname(obj.getFname());
                newTL.setEmailid(obj.getEmailid());
                newTL.setTargetlistid(targetList);
                newTL.setDeleted(0);
                newTL.setRelatedid(obj.getRelatedid());
                newTL.setRelatedto(obj.getRelatedto());
                save(newTL);
            }
            ll.clear();
            ll.add(targetList);
            dl = ll.size();
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.importTargetList", ex);
        } 
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject deleteTargets(HashMap<String, Object> requestParams) throws ServiceException {
        String targetlistID = "";
        String targetRelatedId = "";
        String targetid="";
        String Hql="";
        String retMsg = "{success:false}";
        List ll =null;
        int dl = 0;
        try {
            if (requestParams.containsKey("listid") && requestParams.get("listid") != null) {
                targetlistID =requestParams.get("listid").toString();
            }
            if (requestParams.containsKey("relatedid") && requestParams.get("relatedid") != null) {
                targetRelatedId =requestParams.get("relatedid").toString();
            }
            targetid=(String)requestParams.get("targetid");
            if(!StringUtil.isNullOrEmpty(targetid)){
            	Hql="from com.krawler.crm.database.tables.TargetListTargets targetlisttargets where targetlisttargets.id = ?";
            	ll = executeQuery(Hql, new Object[]{targetid});
            }else{
            	Hql = "from com.krawler.crm.database.tables.TargetListTargets targetlisttargets where targetlisttargets.targetlistid.id = ? and targetlisttargets.relatedid= ? ";
            	ll = executeQuery(Hql, new Object[]{targetlistID,targetRelatedId});
            }
            Iterator ite = ll.iterator();
            dl = ll.size();
            while (ite.hasNext()) {
                TargetListTargets obj = (TargetListTargets) ite.next();
                obj.setDeleted(1);
                save(obj);
            }
            retMsg = "{success:true}";
            ll = new ArrayList();
            ll.add(retMsg);
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.deleteTargets", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getEmailTemplateFiles(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        String fType = "";
        String companyid = "";
        int file_type = 1;
        int dl = 0;
        try {
            if (requestParams.containsKey("type") && requestParams.get("type") != null) {
                fType = requestParams.get("type").toString();
                if (fType != null && fType.compareTo("img") == 0) {
                    file_type = 0;
                }
            }
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            String query = "FROM EmailTemplateFiles AS et WHERE et.type = ? AND et.creator.company.companyID = ?";
            ll = executeQuery(query, new Object[]{file_type, companyid});
            dl = ll.size();
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getEmailTemplateFiles", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject saveEmailTemplateFiles(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        String retMsg = "{success:false}";
        int dl = 0;
        try {
            EmailTemplateFiles fileEntry = new EmailTemplateFiles();
            if (requestParams.containsKey("fileid") && requestParams.get("fileid") != null) {
                fileEntry.setId(requestParams.get("fileid").toString());
            }
            fileEntry.setCreatedon(new Date());
            if (requestParams.containsKey("userid") && requestParams.get("userid") != null) {
                fileEntry.setCreator((User) get(User.class, requestParams.get("userid").toString()));
            }
            if (requestParams.containsKey("file_ext") && requestParams.get("file_ext") != null) {
                fileEntry.setExtn(requestParams.get("file_ext").toString());
            }
            if (requestParams.containsKey("fname") && requestParams.get("fname") != null) {
                fileEntry.setName(requestParams.get("fname").toString());
            }
            if (requestParams.containsKey("filetype") && requestParams.get("filetype") != null) {
                fileEntry.setType(Integer.parseInt(requestParams.get("filetype").toString()));
            }
            save(fileEntry);
            retMsg = "{success:true}";
            ll.add(retMsg);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveEmailTemplateFiles", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

	@Override
	public void saveEmailTemplateFile(String id, String name, String extn, Date createdOn, int type, String creatorId) throws ServiceException {
		try {
			EmailTemplateFiles fileEntry = new EmailTemplateFiles();
			fileEntry.setId(id);
			fileEntry.setCreatedon(new Date());
			fileEntry.setCreator((User) get(User.class, creatorId));
			fileEntry.setExtn(extn);
			fileEntry.setName(name);
			fileEntry.setType(type);
			save(fileEntry);
		} catch (Exception ex) {
			logger.warn(ex.getMessage(), ex);
			throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.saveEmailTemplateFiles", ex);
		}
	}
    
    @Override
    public KwlReturnObject getThemeImages() throws ServiceException {
        List ll = new ArrayList();
        String Hql = "";
        int dl = 0;
        try {
            Hql = "FROM themeImages AS ti WHERE ti.deleted = 0";
            ll = executeQuery(Hql);
            dl = ll.size();
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.getThemeImages", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject copyDefaultTemplates(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        boolean isSuccess = false;
        try {
             User userObj = (User) get(User.class, requestParams.get("userid").toString());
             DefaultTemplates templateObj = (DefaultTemplates) get(DefaultTemplates.class,requestParams.get("templateid").toString());
             String query = "from DefaultTemplates where templateid=?";
             List list = executeQuery(query,new Object[]{requestParams.get("templateid")});
             Iterator iter = list.iterator();
             while (iter.hasNext()) {
                 DefaultTemplates defaultTemplates = (DefaultTemplates) iter.next();
                 EmailTemplate emailTemplate = new EmailTemplate();

                 emailTemplate.setBody(defaultTemplates.getBody());
                 emailTemplate.setBody_html(defaultTemplates.getBody_html());
                 emailTemplate.setCreatedon(authHandler.getCurrentDate());
                 emailTemplate.setCreator(userObj);
                 emailTemplate.setDeleted(0);
                 emailTemplate.setDescription(defaultTemplates.getDescription());
                 emailTemplate.setName(defaultTemplates.getName());
                 emailTemplate.setSubject(defaultTemplates.getSubject());
                 emailTemplate.setThumbnail("");
                 save(emailTemplate);
                 ll.add(emailTemplate.getTemplateid());
                 isSuccess = true;
             }
         } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
             throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.copyDefaultTemplates", ex);
         } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
             throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.copyDefaultTemplates", ex);
         }
         return new KwlReturnObject(isSuccess, KWLErrorMsgs.S01, "", ll, dl);
    }

	@Override
	public void trackUrl(String trackid, String origUrl, Date clickTime) throws ServiceException {
        String Hql = "from com.krawler.crm.database.tables.CampaignLog cl where cl.targettrackerkey = ?";
        try {
        List ll = executeQuery(Hql, new Object[]{trackid});
        if(ll.isEmpty())
        	throw ServiceException.FAILURE("No email found for given mail trackid : "+trackid, null);
        UrlTrackLog urlTrackLog = new UrlTrackLog();
        urlTrackLog.setCampaignLog((CampaignLog)ll.get(0));
        urlTrackLog.setClickedOn(clickTime.getTime());
        urlTrackLog.setUrl(origUrl);
        save(urlTrackLog);
        }catch (Exception e) {
        	logger.warn(e.getMessage(), e);
        	throw ServiceException.FAILURE("Error saving Tracked Url : "+trackid, e);
		}
	}

	@Override
	public KwlReturnObject getUrlTracking(String emailMarketingid, String orderbyField, String usernameOremail, int start, int limit) throws ServiceException{
		List ll;
		String filterStr="";
		List params=new ArrayList();
		params.add(emailMarketingid);
		if(!StringUtil.isNullOrEmpty(usernameOremail)){
			filterStr=" and (tt.fname like ? or tt.lname like ? or tt.emailid like ? )";
			params.add(usernameOremail+"%");
			params.add(usernameOremail+"%");
            params.add(usernameOremail+"%");
		}
		String sortStr = " order by cl.viewed desc";
		if(orderbyField!=null){
			sortStr=" order by "+orderbyField;
		}else{
		}
		int count;
		try {
			String sql = "select count(*) from (select cl.id, cc.campaignname, em.name, tt.fname, tt.lname, hitcount, latesttime, ul.url as latesturl, tt.emailid , tlst.name as targetlistname from campaign_log cl inner join crm_campaign cc on cl.campaignid=cc.campaignid inner join emailmarketing em on cl.emailmarketingid=em.id inner join targetlist_targets tt on cl.targetid=tt.id inner join targetlist tlst on tt.targetlistid=tlst.id inner join url_track_log ul on cl.id=ul.campaignlog inner join (select count(id) as hitcount,campaignlog, max(clickdate) as latesttime from url_track_log group by campaignlog) as ul1 on ul1.campaignlog=ul.campaignlog and ul1.latesttime=ul.clickdate where cl.emailmarketingid=? "+filterStr+") as tab;";
			count = queryForIntJDBC(sql, params.toArray());
			if(limit>0){
                sql = "select cl.id, cc.campaignname, em.name, tt.fname, tt.lname, hitcount, latesttime, ul.url as latesturl, tt.emailid , tlst.name as targetlistname from campaign_log cl inner join crm_campaign cc on cl.campaignid=cc.campaignid inner join emailmarketing em on cl.emailmarketingid=em.id inner join targetlist_targets tt on cl.targetid=tt.id inner join targetlist tlst on tt.targetlistid=tlst.id inner join url_track_log ul on cl.id=ul.campaignlog inner join (select count(id) as hitcount,campaignlog, max(clickdate) as latesttime from url_track_log group by campaignlog) as ul1 on ul1.campaignlog=ul.campaignlog and ul1.latesttime=ul.clickdate where cl.emailmarketingid=? "+filterStr+" "+sortStr+" limit ?,?;";					
                //sql = "select cl.id, cc.campaignname, em.name, tt.fname, tt.lname, count(ul.id) as hitcount, max(ul.clickedon) as latesttime, max(url) latesturl , tt.emailid , tlst.name as targetlistname from campaign_log cl inner join crm_campaign cc on cl.campaignid=cc.campaignid inner join emailmarketing em on cl.emailmarketingid=em.id inner join targetlist_targets tt on cl.targetid=tt.id inner join targetlist tlst on tt.targetlistid=tlst.id inner join url_track_log ul on cl.id=ul.campaignlog where cl.emailmarketingid=? "+filterStr+" group by cl.id "+sortStr+" limit ?,?;";
                params.add(start);params.add(limit);
            } else {
                sql = "select cl.id, cc.campaignname, em.name, tt.fname, tt.lname, hitcount, latesttime, ul.url as latesturl, tt.emailid , tlst.name as targetlistname from campaign_log cl inner join crm_campaign cc on cl.campaignid=cc.campaignid inner join emailmarketing em on cl.emailmarketingid=em.id inner join targetlist_targets tt on cl.targetid=tt.id inner join targetlist tlst on tt.targetlistid=tlst.id inner join url_track_log ul on cl.id=ul.campaignlog inner join (select count(id) as hitcount,campaignlog, max(clickdate) as latesttime from url_track_log group by campaignlog) as ul1 on ul1.campaignlog=ul.campaignlog and ul1.latesttime=ul.clickdate where cl.emailmarketingid=? "+filterStr+" "+sortStr+";";
                //sql = "select cl.id, cc.campaignname, em.name, tt.fname, tt.lname, count(ul.id) as hitcount, max(ul.clickedon) as latesttime, max(url) latesturl , tt.emailid , tlst.name as targetlistname from campaign_log cl inner join crm_campaign cc on cl.campaignid=cc.campaignid inner join emailmarketing em on cl.emailmarketingid=em.id inner join targetlist_targets tt on cl.targetid=tt.id inner join targetlist tlst on tt.targetlistid=tlst.id inner join url_track_log ul on cl.id=ul.campaignlog where cl.emailmarketingid=? "+filterStr+" group by cl.id "+sortStr+" ;";
            }
            
	        ll = queryJDBC(sql, params.toArray(), new RowMapper() {

				@Override
				public Object mapRow(ResultSet rs, int rowNum)
						throws SQLException {
					return new Object[]{rs.getString("id"),rs.getString("campaignname"),rs.getString("name"),rs.getString("fname"),rs.getString("lname"),rs.getInt("hitcount"),rs.getObject("latesttime"),rs.getString("latesturl"),rs.getString("emailid"),rs.getString("targetlistname")};
				}
			});
		}catch(Exception e){
			return new KwlReturnObject(false, e.getMessage(), null, Collections.EMPTY_LIST, 0);
		}
		
		return new KwlReturnObject(true, null, null, ll, count);
	}

	@Override
	public KwlReturnObject getViewedEmailMarketing(String emailMarketingid,
			String orderbyField, String usernameOremail, int start, int limit)
			throws ServiceException {
		List ll;
		String filterStr="";
		List params=new ArrayList();
		params.add(emailMarketingid);
		if(!StringUtil.isNullOrEmpty(usernameOremail)){
			filterStr=" and (tt.fname like ? or tt.lname like ? or tt.emailid like ?)";
			params.add(usernameOremail+"%");
			params.add(usernameOremail+"%");
            params.add(usernameOremail+"%");
			
		}
		String sortStr = " order by cl.viewed desc";
		if(!StringUtil.isNullOrEmpty(orderbyField)){
			sortStr=" order by "+orderbyField;
		}
		int count;
		try {
            String sql = "select count(*) from (select cl.id from campaign_log cl inner join emailmarketing em on cl.emailmarketingid=em.id inner join crm_campaign cc on cl.campaignid=cc.campaignid inner join targetlist_targets tt on cl.targetid=tt.id left join campaign_time_log tl on cl.id=tl.campaignlog where cl.emailmarketingid = ? "+filterStr+" group by cl.id) as tab";
			count = queryForIntJDBC(sql, params.toArray());
            if(limit>0){
                params.add(start);params.add(limit);
                sql = "select cl.id, cl.viewed, cc.campaignname, em.name, tt.fname, tt.lname, max(tl.vieweddate) as latestviewed ,tt.emailid , tlst.name as targetlistname  from campaign_log cl inner join emailmarketing em on cl.emailmarketingid=em.id inner join crm_campaign cc on cl.campaignid=cc.campaignid inner join targetlist_targets tt on cl.targetid=tt.id inner join targetlist tlst on tt.targetlistid=tlst.id left join campaign_time_log tl on cl.id=tl.campaignlog where cl.emailmarketingid = ?  "+filterStr+" group by cl.id  "+sortStr+" limit ?,?";

            } else {
                sql = "select cl.id, cl.viewed, cc.campaignname, em.name, tt.fname, tt.lname, max(tl.vieweddate) as latestviewed ,tt.emailid , tlst.name as targetlistname  from campaign_log cl inner join emailmarketing em on cl.emailmarketingid=em.id inner join crm_campaign cc on cl.campaignid=cc.campaignid inner join targetlist_targets tt on cl.targetid=tt.id inner join targetlist tlst on tt.targetlistid=tlst.id left join campaign_time_log tl on cl.id=tl.campaignlog where cl.emailmarketingid = ?  "+filterStr+" group by cl.id  "+sortStr+" ";
            }
            
	        ll = queryJDBC(sql, params.toArray(),new RowMapper() {

                @Override
                public Object mapRow(ResultSet rs, int rowNo) throws SQLException {

                    return new Object[]{rs.getString("id"),rs.getInt("viewed"), rs.getString("campaignname"), rs.getString("name"),rs.getString("fname"),rs.getString("lname"),rs.getObject("latestviewed"),rs.getString("emailid"),rs.getString("targetlistname")};
                }
            });
		}catch(Exception e){
			return new KwlReturnObject(false, e.getMessage(), null, Collections.EMPTY_LIST, 0);
		}
		
		return new KwlReturnObject(true, null, null, ll, count);
	}

	@Override
	public List<CampaignTimeLog> getViewedEmailMarketingTiming(String campaignlogid) throws ServiceException {
		List ll;
		String Hql = "from com.krawler.crm.database.tables.CampaignTimeLog tl where tl.campaignLog.id = ? ";
		int count;
		try {
			ll = executeQuery(Hql, new Object[]{campaignlogid});
		}catch(Exception e){
			ll = Collections.EMPTY_LIST;
		}
		return ll;
	}

	@Override
	public List<UrlTrackLog> getUrlTrackingDetail(String campaignlogid) throws ServiceException {
		List ll;
		String Hql = "from com.krawler.crm.database.tables.UrlTrackLog tl where tl.campaignLog.id = ? ";
		int count;
		try {
			ll = executeQuery(Hql, new Object[]{campaignlogid});
		}catch(Exception e){
			ll = Collections.EMPTY_LIST;
		}
		return ll;
	}

	@Override
	public List<EmailMarketingDefault> getEmailMarketingDefaults(String emid)
			throws ServiceException {
		List ll;
		String Hql = "from com.krawler.crm.database.tables.EmailMarketingDefault emd where emd.emailMarketing.id = ? ";
		int count;
		try {
			ll = executeQuery(Hql, emid);
		}catch(Exception e){
			ll = Collections.EMPTY_LIST;
		}
		return ll;
	}

	@Override
	public void saveEmailMarketingDefault(EmailMarketingDefault emDefault)
			throws ServiceException {
		save(emDefault);		
	}

	@Override
	public int removeEmailMarketingDefaults(String emid)
			throws ServiceException {
		String hql = "delete from com.krawler.crm.database.tables.EmailMarketingDefault emd where emd.emailMarketing.id = ? ";
		return executeUpdate(hql, emid);
	}

	@Override
	public EmailMarketing getEmailMarketing(String emid)
			throws ServiceException {
		// TODO Auto-generated method stub
		return (EmailMarketing)get(EmailMarketing.class, emid);
	}

    @Override
    public void deleteEmailType(String companyid) throws ServiceException{

        String hql="delete from EnumEmailType c where c.company.companyID = ? ";
        executeUpdate(hql, companyid);
    }
    
    @Override
    public List getInterruptedEmailMarketings()  {
        List ll = Collections.EMPTY_LIST;
        int dl = 0;
        try {

            String Hql = "select em.id, em.creator.userID, em.creator.company.companyID, em.creator.company.subDomain from com.krawler.crm.database.tables.EmailMarketing em where em.lastRunStatus = ? and em.deleted=0 and em.creator.company.deleted=0";
            ArrayList filter_names = new ArrayList(),filter_params = new ArrayList();

            ll = executeQuery(Hql, EmailMarketing.STAUS_RUNNING);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
        return ll;
    }
    
}
