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
package com.krawler.spring.crm.opportunityModule;

import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Karthik
 */
public interface crmOpportunityReportDAO {

    public KwlReturnObject revenueByOppSourceReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject oppByStageReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    KwlReturnObject oppBySalesPersonReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    List<Object[]> oppBySalesPersonCountList(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    KwlReturnObject oppByRegionHReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    long oppByRegionwiseFinalStageCount(HashMap<String, Object> requestParams) throws ServiceException;

    List<Object[]> oppByRegionCountList(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    List<Object[]> oppSalesamountDashboardPieChart(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    Object getCustomColumnAvgGM(HashMap<String, Object> requestParams) throws ServiceException;

    KwlReturnObject oppByRegionFunnelReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject closedOppReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject oppByTypeReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject stuckOppReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject sourceOfOppReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject oppByLeadSourceReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject oppProductReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject salesReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject oppPipelineReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject allOppPipelineReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getOpportunityByTypeChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getOpportunityByProductChart(String id, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getStuckOpportunitiesChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getOpportunityBySourceChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getOpportunityByStageChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getSalesBySourceChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getSourceOffOppChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getClosedOppChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getLeadOpportunityChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject accountsWithOpportunityReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getAccountOpportunityChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject oppPipelineChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    //Lead Report
    public KwlReturnObject convertedLeadsToOpportunityReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;
}
