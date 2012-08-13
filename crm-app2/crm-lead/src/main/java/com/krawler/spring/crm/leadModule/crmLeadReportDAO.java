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
package com.krawler.spring.crm.leadModule;

import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;

/**
 *
 * @author Karthik
 */
public interface crmLeadReportDAO {

    public KwlReturnObject leadsByIndustryReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject convertedLeadsReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject leadsBySourceReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject qualifiedLeadsReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject contactedLeadsReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject openLeadsReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject leadsPipelineReport(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getLeadsByIndustryChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getLeadsBySourceChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getOpenLeadChart(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject leadsPipelineChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getConvertedLeadsWeekDayViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result, String leadsFlag) throws ServiceException;

    public KwlReturnObject getConvertedLeadsMonthViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result, String leadsFlag) throws ServiceException;

    public KwlReturnObject getConvertedLeadsYearViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result, String leadsFlag,Boolean isSeries) throws ServiceException;

    public KwlReturnObject getConvertedQuaterlyViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result, String leadsFlag) throws ServiceException ;

    public KwlReturnObject getConvertedLeadsToWeekDayViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result) throws ServiceException;

    public KwlReturnObject getConvertedLeadsToMonthViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result) throws ServiceException;

    public KwlReturnObject getConvertedLeadsToYearViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result) throws ServiceException;
    public KwlReturnObject getConvertedQuaterlyToViewChart(HashMap<String, Object> requestParams, StringBuffer usersList, String result) throws ServiceException;
    public KwlReturnObject getContactedLeadsChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;
    public KwlReturnObject getQualifiedLeadsChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;
    public KwlReturnObject getConvertedLeadsChart(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;

    public KwlReturnObject getConvertedLeadAccountPie(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;
    public KwlReturnObject getConvertedLeadOppPie(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;
    public KwlReturnObject getConvertedLeadContactPie(String name, HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException;
}
