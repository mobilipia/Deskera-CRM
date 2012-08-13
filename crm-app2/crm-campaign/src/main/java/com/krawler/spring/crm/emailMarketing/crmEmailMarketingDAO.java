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
import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CampaignTimeLog;
import com.krawler.crm.database.tables.EmailMarketing;
import com.krawler.crm.database.tables.EmailMarketingDefault;
import com.krawler.crm.database.tables.TargetList;
import com.krawler.crm.database.tables.TargetListTargets;
import com.krawler.crm.database.tables.UrlTrackLog;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface crmEmailMarketingDAO {
    public KwlReturnObject getEmailTemplateList(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getEmailTypeList(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getDefaultEmailTemplate(HashMap requestParams) throws ServiceException ;
    public KwlReturnObject addEmailTemplate(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject editEmailTemplate(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject addEmailType(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject editEmailType(JSONObject jobj) throws ServiceException ;

    public KwlReturnObject getTargetList(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject addTargetList(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject saveTargetsForTemp(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject getEmailMarkTargetList(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getUnAssignEmailMarkTargetList(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getTargetListTargets(HashMap<String, Object> requestParams) throws ServiceException ;

    public KwlReturnObject getCampEmailMarketList(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getCampEmailMarketCount(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getCampaignLog(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject sendEmailMarketMail(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject sendTestEmailMarketMail(HashMap<String, Object> requestParams) throws ServiceException ;

    public KwlReturnObject getCampaignTarget(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject addCampaignTarget(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject editCampaignTarget(JSONObject jobj) throws ServiceException ;
    public KwlReturnObject getUnAssignCampaignTarget(HashMap<String, Object> requestParams) throws ServiceException ;

    public KwlReturnObject getColorThemes() throws ServiceException ;
    public KwlReturnObject getColorThemeGroup() throws ServiceException ;
    public KwlReturnObject getEmailMrktContent(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getTemplateContent(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getEmailTypeContent(HashMap<String, Object> requestParams) throws ServiceException ;

    public KwlReturnObject scheduleEmailMarketing(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject deleteEmailMarketingSchedule(HashMap<String, Object> requestParams) throws ServiceException ;

    public KwlReturnObject campEmailMarketingStatus(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getTemplateHTMLContent(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject unsubscribeUserMarketMail(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject subscribeUserMarketMail(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject viewedEmailMarketMail(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject captureLeadFromCampaign(List ll) throws ServiceException;
    public KwlReturnObject confirmsubscribeUserMarketMail(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject saveTargetListTargets(HashMap<String, Object> requestParams) throws ServiceException;
    public void deleteCampaignTarget(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject saveCampaignTargetList(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject saveCampEmailMarketConfig(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject deleteEmailmarketingTargetList(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject mapEmailmarketingTargetList(HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject saveModuleTargetsForTemp(HashMap<String, Object> requestParams) throws ServiceException;
    public TargetListTargets saveTargetsForTemp(TargetList targetId, String rid, int rto, String fname, String lname,String email);
    public KwlReturnObject importTargetList(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject deleteTargets(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject getEmailTemplateFiles(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject saveEmailTemplateFiles(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject getThemeImages() throws ServiceException;
    public KwlReturnObject getScheduleEmailMarketing(HashMap<String, Object> requestParams);
    public KwlReturnObject getFutureScheduleEmailMarketingById(HashMap<String, Object> requestParams);
    public void getDefaultEmailTemplateList(List l1,HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject copyDefaultTemplates(HashMap<String, Object> requestParams) throws ServiceException;
	public void trackUrl(String trackid, String origUrl, Date clickTime) throws ServiceException;
	public KwlReturnObject getViewedEmailMarketing(String emailMarketingid, String orderbyField, String username, int start, int limit) throws ServiceException;
	public KwlReturnObject getUrlTracking(String emailMarketingid, String orderbyField, String username, int start, int limit) throws ServiceException;
	public List<CampaignTimeLog> getViewedEmailMarketingTiming(String campaignlogid)throws ServiceException;
	public List<UrlTrackLog> getUrlTrackingDetail(String campaignlogid)throws ServiceException;
	public List<EmailMarketingDefault> getEmailMarketingDefaults(String emid) throws ServiceException;
	public void saveEmailMarketingDefault(EmailMarketingDefault emDefault)throws ServiceException;
	public int removeEmailMarketingDefaults(String emid) throws ServiceException;
	public EmailMarketing getEmailMarketing(String emid) throws ServiceException;
	void saveEmailTemplateFile(String id, String name, String extn, Date createdOn, int type, String creatorId) throws ServiceException;
    public void deleteEmailType(String companyid) throws ServiceException;
	public List getInterruptedEmailMarketings();
}
