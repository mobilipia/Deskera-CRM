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

package com.krawler.crm.hrmsintegration.bizservice;

import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.crm.database.tables.Finalgoalmanagement;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
    /**
 *
 * @author krawler
 */
public interface GoalManagementService {

    /**
     *
     * @param userid
     * @param viewAll
     * @param start
     * @param limit
     * @param ss
     * @param relatedto
     * @param field
     * @param direction
     * @param fromDate
     * @param toDate
     * @return
     * @throws com.krawler.common.service.ServiceException
     * @throws com.krawler.utils.json.base.JSONException
     * @throws java.text.ParseException
     * @throws com.krawler.common.session.SessionExpiredException
     */
    KwlReturnObject getActiveFinalGoals(String userid,boolean viewAll,String start,String limit,String ss,String relatedto,String field,String direction,Long fromDate,Long toDate)  throws ServiceException, JSONException, ParseException, SessionExpiredException;
    /**
     *
     * @param userid
     * @param viewAll
     * @param start
     * @param limit
     * @param ss
     * @param field
     * @param direction
     * @param fromDate
     * @param toDate
     * @return
     * @throws com.krawler.common.service.ServiceException
     * @throws com.krawler.utils.json.base.JSONException
     * @throws java.text.ParseException
     * @throws com.krawler.common.session.SessionExpiredException
     */
    JSONObject employeesGoal(String userid,boolean viewAll,String start,String limit,String ss,String field,String direction,Long fromDate,Long toDate,String timeFormatId,String companyid,String timeZoneDiff)  throws ServiceException, JSONException, ParseException, SessionExpiredException;
    /**
     *
     * @param userid
     * @param viewAll
     * @param start
     * @param limit
     * @param ss
     * @param relatedto
     * @param field
     * @param direction
     * @param fromDate
     * @param toDate
     * @return
     * @throws com.krawler.common.service.ServiceException
     * @throws com.krawler.utils.json.base.JSONException
     * @throws java.text.ParseException
     * @throws com.krawler.common.session.SessionExpiredException
     */
    JSONObject loginEmployeeGoals(User user,boolean viewAll,String start,String limit,String ss,String relatedto,String field,String direction,Long fromDate,Long toDate,String timeFormatId,String companyid,String timeZoneDiff)  throws ServiceException, JSONException, ParseException, SessionExpiredException;
    
    /**
     * 
     * @param fgmt
     * @param companyid
     * @param userid
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    public double  getAchievedTarget(Finalgoalmanagement fgmt,String companyid,User user ) throws ServiceException;
    /**
     *
     * @param fgmt
     * @param dl
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    public double  getPercentageTarget(Finalgoalmanagement fgmt,double dl ) throws ServiceException;
    /**
     *
     * @param relatedTo
     * @param startdate
     * @param enddate
     * @param userid
     * @param companyid
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    public HashMap getFilter(int relatedTo ,Long startdate,Long enddate,String userid,String companyid) throws ServiceException;
    /**
     *
     * @param userid
     * @param companyid
     * @param hrmsURL
     * @param ids
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    public KwlReturnObject assignedgoalsdelete(String userid,String companyid,String hrmsURL,String[] ids) throws ServiceException;
    /**
     *
     * @param reltatedto
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    public String getGoalName(int reltatedto ) throws ServiceException;
    /**
     *
     * @param companyid
     * @param userid
     * @param hrmsURL
     * @param jarr
     * @param archive
     * @param archiveids
     * @param fmt
     * @return
     * @throws com.krawler.common.service.ServiceException
     */
    public KwlReturnObject insertGoal(String companyid,String userid,String hrmsURL,JSONArray jarr,String archive,String [] archiveids,DateFormat fmt, boolean companyNotifyFlag, String loginURL, String partnerName) throws ServiceException ;

    JSONObject completedGoalReport(String userid, String companyid, DateFormat dateFormat, int relatedTo,
            int leadPerm, int accountPerm, int oppPerm, Long fromDate, Long toDate, String searchStr, String startStr, String limitStr, boolean exportall,Locale locale,String timeZoneDiff)
            throws ServiceException;


    public String completedGoalPieChart(StringBuffer usersList ) throws ServiceException;
    public String completedGoalBarChart(StringBuffer usersList ) throws ServiceException;
    public JSONObject getGoalHistoryJSON(boolean isexport, String goalid, String companyid, String userid, DateFormat dateFormat, String start, String limit) throws ServiceException;
}
