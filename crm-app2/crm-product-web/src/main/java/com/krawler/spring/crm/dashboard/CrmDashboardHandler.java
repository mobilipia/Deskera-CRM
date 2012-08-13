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
package com.krawler.spring.crm.dashboard;

import com.krawler.common.admin.ProjectFeature;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CustomReportList;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;

public class CrmDashboardHandler implements MessageSourceAware{
    private static final Log logger = LogFactory.getLog(CrmDashboardHandler.class);
    private MessageSource messageSource;

    @Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
		
	}

    public JSONArray getLeadsReportsLink(int reportPerm,List li,Locale locale) throws ServiceException{
        JSONArray arr = new JSONArray();
        JSONObject obj;
        String tooltip="";
        String link="";
        try {            
            if ((reportPerm & 1) == 1) {
                tooltip=messageSource.getMessage("crm.reportlink.leadsbyindustry.ttip", null,locale );//"Monitor your leads grouped by type of industry.";
                link=messageSource.getMessage("crm.reportlink.leadsbyindustry", null,locale );//"Leads by Industry";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(0)' wtf:qtip='"+tooltip+"'>"+link+" </a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "0");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 2) == 2) {
                tooltip=messageSource.getMessage("crm.reportlink.convertedleads.ttip", null,locale );//"View list of converted leads.";
                link=messageSource.getMessage("crm.reportlink.convertedleads", null,locale );//"Converted Leads";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(1)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "1");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 4) == 4) {
                tooltip=messageSource.getMessage("crm.reportlink.leadsbysource.ttip", null,locale );//"Monitor your leads grouped by corresponding source.";
                link=messageSource.getMessage("crm.reportlink.leadsbysource", null,locale );//"Leads by Source";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(7)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "7");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 8) == 8) {
                tooltip=messageSource.getMessage("crm.reportlink.qualifiedleads.ttip", null,locale );//"Get the list of leads who have their status as qualified.";
                link=messageSource.getMessage("crm.reportlink.qualifiedleads", null,locale );//"Qualified Leads";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(23)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "23");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 16) == 16) {
                tooltip=messageSource.getMessage("crm.reportlink.contactedleads.ttip", null,locale );//"Get the list of Leads who have their status as Contacted.";
                link=messageSource.getMessage("crm.reportlink.contactedleads", null,locale );//"Contacted Leads";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(26)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "26");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 32) == 32) {
                tooltip=messageSource.getMessage("crm.reportlink.openleads.ttip", null,locale );//"Get the list of Leads who have their status as Open.";
                link=messageSource.getMessage("crm.reportlink.openleads", null,locale );//"Open Leads";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(34)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "34");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 64) == 64) {
                tooltip=messageSource.getMessage("crm.reportlink.convertleadstoaccount.ttip", null,locale );//"Get the list of Leads who are converted to Accounts.";
                link=messageSource.getMessage("crm.reportlink.convertleadstoaccount", null,locale );//"Converted Leads to Account";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(36)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "36");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 128) == 128) {
                tooltip=messageSource.getMessage("crm.reportlink.convertleadstoopportunity.ttip", null,locale );//"Get the list of Leads who are converted to Opportunity.";
                link=messageSource.getMessage("crm.reportlink.convertleadstoopportunity", null,locale );//"Converted Leads to Opportunity";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(37)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "37");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 256) == 256) {
                tooltip=messageSource.getMessage("crm.reportlink.convertleadstocontacts.ttip", null,locale );//"Get the list of Leads who are converted to Contacts.";
                link=messageSource.getMessage("crm.reportlink.convertleadstocontacts", null,locale );//"Converted Leads to Contacts";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(38)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "38");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 512) == 512) {
                tooltip=messageSource.getMessage("crm.reportlink.leadspipelinereport.ttip", null,locale );//"Get the list of Leads Pipeline data.";
                link=messageSource.getMessage("crm.reportlink.leadspipelinereport", null,locale );//"Leads Pipeline Report";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(41)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "41");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
        }catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboard.getLeadsReportsLink", e);
        }
        return arr;
    }

    public JSONArray getOpportunityReportsLink(int reportPerm, List li, Locale locale) throws ServiceException {
        JSONArray arr = new JSONArray();
        JSONObject obj;
        String tooltip="";
        String link="";
        try {            
            if((reportPerm & 1) == 1) {
                tooltip=messageSource.getMessage("crm.reportlink.revenuebyopportunitysource.ttip", null,locale );//"Monitor your opportunities grouped by type of lead source.";
                link=messageSource.getMessage("crm.reportlink.revenuebyopportunitysource", null,locale );//"Revenue by Opportunity Source";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(2)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "2");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 2) == 2) {
                tooltip=messageSource.getMessage("crm.reportlink.opportunitiesbystage.ttip", null,locale );//"Monitor your opportunities grouped by corresponding stage such as qualified, closed and won.";
                link=messageSource.getMessage("crm.reportlink.opportunitiesbystage", null,locale );//"Opportunities by Stage";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(3)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "3");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 4) == 4) {
                tooltip=messageSource.getMessage("crm.reportlink.closedopportunities.ttip", null,locale );//"Get the list of Opportunities who are Closed-won.";
                link=messageSource.getMessage("crm.reportlink.closedopportunities", null,locale );//"Closed Opportunities";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(8)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "8");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 8) == 8) {
                tooltip=messageSource.getMessage("crm.reportlink.opportunitiesbytype.ttip", null,locale );//"Get the list of Opportunities with respect to their Type.";
                link=messageSource.getMessage("crm.reportlink.opportunitiesbytype", null,locale );//"Opportunities by Type";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(9)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "9");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 16) == 16) {
                tooltip=messageSource.getMessage("crm.reportlink.stuckopportunities.ttip", null,locale );//"Get the list of Opportunities whose Probability is less than 50%.";
                link=messageSource.getMessage("crm.reportlink.stuckopportunities", null,locale );//"Stuck Opportunities";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(11)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "11");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 32) == 32) {
                tooltip=messageSource.getMessage("crm.reportlink.sourcesofopportunities.ttip", null,locale );//"Get the list of Opportunities with respect to their Source.";
                link=messageSource.getMessage("crm.reportlink.sourcesofopportunities", null,locale );//"Sources of Opportunities";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(14)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "14");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
//            if ((sessionHandlerImpl.getPerms(request, "Opportunity Report") & 64) == 64) {//Need to verify
            if ((reportPerm & 64) == 64) {
                tooltip=messageSource.getMessage("crm.reportlink.opportunitiesbyleadsource.ttip", null,locale );//"Get the list of lead source who have Opportunities.";
                link=messageSource.getMessage("crm.reportlink.opportunitiesbyleadsource", null,locale );//"Opportunities by Lead Source";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(29)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "29");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 128) == 128) {//256 - All opp report which is subtab in opp pipeline report
                tooltip=messageSource.getMessage("crm.reportlink.opportunitypipelinereport.ttip", null,locale );//"Get the list of Opportunities Pipeline data for converted leads.";
                link=messageSource.getMessage("crm.reportlink.opportunitypipelinereport", null,locale );//"Opportunities Pipeline Report";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(40)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "40");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 512) == 512) {
                tooltip=messageSource.getMessage("crm.reportlink.opportunitiesbysalesperson.ttip", null,locale );//"Monitor your opportunities grouped by sales person.";
                link=messageSource.getMessage("crm.reportlink.opportunitiesbysalesperson", null,locale );//"Opportunities by Sales Person";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(44)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "44");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 1024) == 1024) {
                tooltip=messageSource.getMessage("crm.reportlink.opportunitiesbyregion.ttip", null,locale );//"Monitor your opportunities grouped by region.";
                link=messageSource.getMessage("crm.reportlink.opportunitiesbyregion", null,locale );//"Opportunities by Region";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(45)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "45");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboard.getOpportunityReportsLink", e);
        }
        return arr;
    }

    public JSONArray getAccountReportsLink(int reportPerm,List li, Locale locale) throws ServiceException {
        JSONArray arr = new JSONArray();
        JSONObject obj;
        String tooltip="";
        String link="";
        try {            
            if((reportPerm & 1) == 1) {
                tooltip=messageSource.getMessage("crm.reportlink.keyaccounts.ttip", null,locale );//"Monitor your key accounts ordered by corresponding revenues.";
                link=messageSource.getMessage("crm.reportlink.keyaccounts", null,locale );//"Key Accounts";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(5)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "5");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 2) == 2) {
                tooltip=messageSource.getMessage("crm.reportlink.monthlyaccounts.ttip", null,locale );//"Get the list of Accounts created by month.";
                link=messageSource.getMessage("crm.reportlink.monthlyaccounts", null,locale );//"Monthly Accounts";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(12)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "12");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 4) == 4) {
                tooltip=messageSource.getMessage("crm.reportlink.accountowners.ttip", null,locale );//"Get the list of Accounts and their respective Owners.";
                link=messageSource.getMessage("crm.reportlink.accountowners", null,locale );//"Account Owners";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(13)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "13");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 8) == 8) {
                tooltip=messageSource.getMessage("crm.reportlink.accountwithcasesbypriority.ttip", null,locale );//"Get the list of Accounts who have Cases with Priority and are yet to Start.";
                link=messageSource.getMessage("crm.reportlink.accountwithcasesbypriority", null,locale );//"Account with Cases by Priority";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(18)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "18");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 16) == 16) {
                tooltip=messageSource.getMessage("crm.reportlink.industryaccounttypereport.ttip", null,locale );//"Get the list of Accounts and the Industry they belong. Select an Account Type to populate the record in the report.";
                link=messageSource.getMessage("crm.reportlink.industryaccounttypereport", null,locale );//"Industry-Account Type Report";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(20)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "20");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 32) == 32) {
                tooltip=messageSource.getMessage("crm.reportlink.accountswithcontacts", null,locale );//"Accounts with Contacts";
                link=messageSource.getMessage("crm.reportlink.accountswithcontacts", null,locale );//"Accounts with Contacts";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(24)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "24");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 64) == 64) {
                tooltip=messageSource.getMessage("crm.reportlink.accountswithopportunities.ttip", null,locale );//"Get the list of Accounts who have most number of opportunities.";
                link=messageSource.getMessage("crm.reportlink.accountswithopportunities", null,locale );//"Accounts with Opportunities";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(28)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "28");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 128) == 128) {
                tooltip=messageSource.getMessage("crm.reportlink.accountswithcases.ttip", null,locale );//"Get the list of Accounts who have Cases.";
                link=messageSource.getMessage("crm.reportlink.accountswithcases", null,locale );//"Accounts with Cases";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(33)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "33");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
        }catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboard.getAccountReportsLink", e);
        }
        return arr;
    }

    public JSONArray getProductReportsLink(int reportPerm,List li, Locale locale) throws ServiceException {
        JSONArray arr = new JSONArray();
        JSONObject obj;
        String tooltip="";
        String link="";
        try {
            if((reportPerm & 1) == 1) {
                tooltip=messageSource.getMessage("crm.reportlink.productswithcasesbypriority.ttip", null,locale );//"Get the list of Products who have Cases with High Priority and are yet to Start.";
                link=messageSource.getMessage("crm.reportlink.productswithcasesbypriority", null,locale );//"Products with Cases by Priority";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(17)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "17");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
        }catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboard.getProductReportsLink", e);
        }
        return arr;
    }

    public JSONArray getContactReportsLink(int reportPerm,List li, Locale locale) throws ServiceException {
        JSONArray arr = new JSONArray();
        JSONObject obj;
        String tooltip="";
        String link="";
        try {
            if((reportPerm & 1) == 1) {
                tooltip=messageSource.getMessage("crm.reportlink.contactswithhighprioritycases.ttip", null,locale );//"Get the list of Contacts who have Cases with High Priority and are yet to Start.";
                link=messageSource.getMessage("crm.reportlink.contactswithhighprioritycases", null,locale );//"Contacts with High Priority Cases";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(16)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "16");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 2) == 2) {
                tooltip=messageSource.getMessage("crm.reportlink.contactsbyleadsource.ttip", null,locale );//"Get the list of lead source who have Contacts.";
                link=messageSource.getMessage("crm.reportlink.contactsbyleadsource", null,locale );//"Contacts by Lead Source";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(27)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "27");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if ((reportPerm & 4) == 4) {
                tooltip=messageSource.getMessage("crm.reportlink.contactswithcases.ttip", null,locale );//"Get the list of Contacts who have Cases.";
                link=messageSource.getMessage("crm.reportlink.contactswithcases", null,locale );//"Contacts with Cases";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(35)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "35");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
        }catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboard.getContactReportsLink", e);
        }
        return arr;
    }

    public void getOpportunityProductReportsLink(HttpServletRequest request,List li, Locale locale) throws ServiceException {
        try {
            if((sessionHandlerImpl.getPerms(request, "OpporunityProductReport") & 1) == 1) {
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(10)' wtf:qtip="+messageSource.getMessage("crm.reportlink.opportunityproductreport.ttip", null,locale )+">"+messageSource.getMessage("crm.reportlink.opportunityproductreport", null,locale )+"</a>\",\"name\":"+messageSource.getMessage("crm.reportlink.opportunityproductreport", null,locale )+"}");
            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public JSONObject getSalesReportsLink(int reportPerm,List li, Locale locale) throws ServiceException {
        JSONObject obj = null;
        String tooltip="";
        String link="";
        try {            
            if((reportPerm & 1) == 1) {
                tooltip=messageSource.getMessage("crm.reportlink.salesbysource.ttip", null,locale );//"Monitor your sales grouped by type of lead source.";
                link=messageSource.getMessage("crm.reportlink.salesbysource", null,locale );//"Sales by Source";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(6)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "6");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
            }
        }catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboard.getCaseReportsLink", e);
        }
        return obj;
    }

    public void getTargetReportsLink(HttpServletRequest request,List li, Locale locale) throws ServiceException {
        try {
            if((sessionHandlerImpl.getPerms(request, "TargetReport") & 1) == 1) {
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(39)' wtf:qtip="+messageSource.getMessage("crm.reportlink.targetsbyowner.ttip", null,locale )+">"+messageSource.getMessage("crm.reportlink.targetsbyowner", null,locale )+"</a>\",\"name\":"+messageSource.getMessage("crm.reportlink.targetsbyowner", null,locale )+"}");
            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
    }

    public JSONArray getCaseReportsLink(int reportPerm,List li, Locale locale) throws ServiceException {
        JSONArray arr = new JSONArray();
        JSONObject obj;
        String tooltip="";
        String link="";
        try {            
            if((reportPerm & 1) == 1) {
                tooltip=messageSource.getMessage("crm.reportlink.casesbystatus.ttip", null,locale );//"Monitor customer cases grouped by corresponding status such as new, pending and escalated.";
                link=messageSource.getMessage("crm.reportlink.casesbystatus", null,locale );//"Cases by Status";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(4)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "4");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 2) == 2) {
                tooltip=messageSource.getMessage("crm.reportlink.monthlycases.ttip", null,locale );//"Get the list of Cases created by month.";
                link=messageSource.getMessage("crm.reportlink.monthlycases", null,locale );//"Monthly Cases";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(19)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "19");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 4) == 4) {
                tooltip=messageSource.getMessage("crm.reportlink.newlyaddedcases.ttip", null,locale );//"Get the list of Newly Added Cases.";
                link=messageSource.getMessage("crm.reportlink.newlyaddedcases", null,locale );//"Newly Added Cases";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(30)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "30");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 8) == 8) {
                tooltip=messageSource.getMessage("crm.reportlink.pendingcases.ttip", null,locale );//"Get the list of Pending Cases.";
                link=messageSource.getMessage("crm.reportlink.pendingcases", null,locale );//"Pending Cases";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(31)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "31");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 16) == 16) {
                tooltip=messageSource.getMessage("crm.reportlink.escalatedcases.ttip", null,locale );//"Get the list of Escalated Cases.";
                link=messageSource.getMessage("crm.reportlink.escalatedcases", null,locale );//"Escalated Cases";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(32)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "32");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
        }catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboard.getCaseReportsLink", e);
        }
        return arr;
    }

    public JSONArray getActivityReportsLink(int reportPerm,List li, Locale locale) throws ServiceException {
        JSONArray arr = new JSONArray();
        JSONObject obj;
        String tooltip="";
        String link="";
        try {            
            if((reportPerm & 1) == 1) {
                tooltip=messageSource.getMessage("crm.reportlink.highpriorityactivities.ttip", null,locale );//"Get the list of Activities who are of High Priority and having status as Not Started.";
                link=messageSource.getMessage("crm.reportlink.highpriorityactivities", null,locale );//"High Priority Activities";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(15)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "15");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
        }catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboard.getActivityReportsLink", e);
        }
        return arr;
    }

    public JSONArray getGoalReportsLink(HttpServletRequest request,List li, Locale locale) throws ServiceException {
        JSONArray arr = new JSONArray();
        JSONObject obj;
        String tooltip="";
        String link="";
        try {
            if((sessionHandlerImpl.getPerms(request, "GoalReport") & 1) == 1) {
                tooltip=messageSource.getMessage("crm.reportlink.completedgoalsbyusers.ttip", null,locale );//"Get the list of completed goals by users.";
                link=messageSource.getMessage("crm.reportlink.completedgoalsbyusers", null,locale );//"Completed Goals by Users";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(43)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "43");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
        } catch (SessionExpiredException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboard.getGoalReportsLink", e);
        }
        return arr;
    }

    public JSONArray getCampaignReportsLink(int reportPerm,List li, Locale locale) throws ServiceException {
        JSONArray arr = new JSONArray();
        JSONObject obj;
        String tooltip="";
        String link="";
        try {
            if((reportPerm & 1) == 1) {
                tooltip=messageSource.getMessage("crm.reportlink.campaignsbytype.ttip", null,locale );//"Get the list of Campaigns according to their type.";
                link=messageSource.getMessage("crm.reportlink.campaignsbytype", null,locale );//"Campaigns by Type";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(21)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "21");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 2) == 2) {
                tooltip=messageSource.getMessage("crm.reportlink.completedcampaignsbytype.ttip", null,locale );//"Get the list of Campaigns who have their status marked as complete.";
                link=messageSource.getMessage("crm.reportlink.completedcampaignsbytype", null,locale );//"Completed Campaigns by Type";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(22)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "22");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 4) == 4) {
                tooltip=messageSource.getMessage("crm.reportlink.campaignsbygoodresponse.ttip", null,locale );//"Get the list of Campaigns who have generated response more than 70%.";
                link=messageSource.getMessage("crm.reportlink.campaignsbygoodresponse", null,locale );//"Campaigns with Good Response";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(25)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "25");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
            }
            if((reportPerm & 8) == 8) {
                tooltip=messageSource.getMessage("crm.reportlink.campaignsinfluence.ttip", null,locale );//"Get the list of Campaigns who have generated response more than 70%";
                link=messageSource.getMessage("crm.reportlink.campaignsinfluence", null,locale );//"Campaigns Influence";
                li.add("{\"update\":\"<a href=# onclick='addAllReportTab(42)' wtf:qtip='"+tooltip+"'>"+link+"</a>\",\"name\":\""+link+"\"}");
                obj= new JSONObject();
                obj.put("id", "42");
                obj.put("link", link);
                obj.put("tooltip", tooltip);
                arr.put(obj);
           }
        }catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmDashboard.getCampaignReportsLink", e);
        }
        return arr;
    }

    public List  getCustomReportLinks(HttpServletRequest request, List li, Locale locale) throws ServiceException {
        ArrayList ll = new ArrayList();
        try {
            if((sessionHandlerImpl.getPerms(request, ProjectFeature.customReport) & 2) == 2) {
                if(li!=null && li.size()>0){
                    boolean delPerm = ((sessionHandlerImpl.getPerms(request, ProjectFeature.customReport) & 1) == 1)?true:false;
                    List<CustomReportList> customReportList = li;
                    for ( CustomReportList obj: customReportList) {
                       String reportname = obj.getRname().toString();
                       reportname=reportname.replaceAll("\'", "&#39;");
                       reportname=reportname.replaceAll("\"", "");
                       reportname=reportname.replaceAll("\\\\", "");
                       String tooltip = obj.getRdescription().toString();
                       tooltip=tooltip.replaceAll("\'", "&#39;");
                       tooltip=tooltip.replaceAll("\"", "");
                       tooltip=tooltip.replaceAll("\\\\", "");
                       if(StringUtil.isNullOrEmpty(tooltip)) {
                            tooltip = reportname;
                       }
                       int reportno = obj.getRno();
                       String shortTooltip = "";
                       if(tooltip.length() > 36) {
                           shortTooltip = tooltip.substring(0, 36)+ "...";
                       } else {
                           shortTooltip = tooltip;
                       }
                       
                       String reportcategory = obj.getRcategory();
                       if(delPerm) {
                           ll.add("{delete:\"<img class='stop' onclick='javascript:deleteCustomReport(\\\""+reportno+"\\\");'  alt='Delete' src='../../images/deleteLink.gif' style='' id=\\\""+reportno+"\\\" />\"," +
                                   "update:\"<a href=# onclick='openCustomReportTab(\\\""+reportno+"\\\",\\\""+reportname+"\\\",\\\""+reportcategory+"\\\",\\\""+tooltip+"\\\")' wtf:qtip='"+shortTooltip+"'>"+reportname+"</a>\",name:\""+reportname+"\",reportno:\""+reportno+"\"}");
                       } else {
                            ll.add("{delete:\"\",update:\"<a href=# onclick='openCustomReportTab(\\\""+reportno+"\\\",\\\""+reportname+"\\\",\\\""+reportcategory+"\\\",\\\""+tooltip+"\\\")' wtf:qtip='"+shortTooltip+"'>"+reportname+"</a>\",name:\""+reportname+"\",reportno:\""+reportno+"\"}");
                       }
                    }
                }// else {
//                    ll.add("{delete:\"\",update:"+messageSource.getMessage("crm.reportlink.customreport.mtytxt", null,locale )+",name:\"\",reportno:\"0\"}");
//                }
            } else {
                ll.add("{delete:\"\",update:"+messageSource.getMessage("crm.reportlink.nopermission.mtytxt", null,locale )+",name:\"\",reportno:\"0\"}");
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return ll;
    }
}
