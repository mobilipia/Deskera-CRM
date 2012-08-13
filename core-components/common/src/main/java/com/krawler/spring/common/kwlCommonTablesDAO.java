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
package com.krawler.spring.common;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import java.text.DateFormat;
import java.util.List;

/**
 *
 * @author Karthik
 */
public interface kwlCommonTablesDAO {
    
    /**
     * @param classpath
     * @param id
     * @return
     * @throws ServiceException
     */
    public Object getObject(String classpath, String id) throws ServiceException;

    /**
     * @param classpath
     * @param id
     * @return
     * @throws ServiceException
     */
    public Object getClassObject(String classpath, String id) throws ServiceException;
    
    /**
     *
     * @param classpath
     * @param id
     * @return
     * @throws ServiceException
     */
    public Object getRelatedClassObject(String classpath,String modulePropertyName,String activityPropertyName, Object relatedId) throws ServiceException;

    /**
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAllTimeZones() throws ServiceException;

    /**
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAllCurrencies() throws ServiceException;

    /**
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAllDateFormats() throws ServiceException;

    /**
     * @return
     * @throws ServiceException
     */
    public KwlReturnObject getAllCountries() throws ServiceException;

    /**
     * @param dateFormatId
     * @param userTimeFormatId
     * @param timeZoneDiff
     * @return
     * @throws ServiceException
     */
    public DateFormat getUserDateFormatter(String dateFormatId, String userTimeFormatId, String timeZoneDiff) throws ServiceException;
    
    /**
     * @param dateFormatId
     * @param userTimeFormatId
     * @return
     * @throws ServiceException
     */
    public DateFormat getOnlyDateFormatter(String dateFormatId, String userTimeFormatId) throws ServiceException;

    /**
     * @param user
     * @param part
     * @param dateFormatId
     * @param timeZoneID
     * @return
     * @throws ServiceException
     */
    public DateFormat getUserDateFormatter1(User user, int part, String dateFormatId, String timeZoneID) throws ServiceException;
}
