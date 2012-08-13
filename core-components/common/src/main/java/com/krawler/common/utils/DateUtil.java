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
package com.krawler.common.utils;

import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Ashutosh
 *
 */
public class DateUtil
{
    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;
    
    public static String getUserTimeFormat(String timeFormat) throws SessionExpiredException
    {
        return checkForNullString(timeFormat, SessionExpiredException.USERID_NULL);
    }
    
    public static String checkForNullString(String objToCheck, String errorCode) throws SessionExpiredException
    {
        if (!StringUtil.isNullOrEmpty(objToCheck))
        {
            return objToCheck;
        }
        throw new SessionExpiredException("Session Invalidated", errorCode);
    }

    public static long getStrippedDateAsLong(Date date, int daysToAdd)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.add(Calendar.DAY_OF_YEAR, daysToAdd);

        return cal.getTimeInMillis();
    }
    
    /**
     * Returns provided duration formatted as 99Days 99Hours 99Min 99s
     * @param duration to format
     * @return formatted string
     */
    public static String millisToShortDHMS(long duration)
    {
        String res = "";
        duration /= ONE_SECOND;
        int seconds = (int) (duration % SECONDS);
        duration /= SECONDS;
        int minutes = (int) (duration % MINUTES);
        duration /= MINUTES;
        int hours = (int) (duration % HOURS);
        int days = (int) (duration / HOURS);
        if (days == 0)
        {
            res = String.format("%02dHr %02dMin %02ds", hours, minutes, seconds);
        } else
        {
            res = String.format("%dDays %02dHr %02dMin %02ds", days, hours, minutes, seconds);
        }
        return res;
    }
    public static Date addTimePart(Date date, String timePart) throws ParseException, java.text.ParseException, java.text.ParseException {
        Date datePart = new Date();
        SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
        SimpleDateFormat dfWithNoTime = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        Calendar cal = new GregorianCalendar();
        datePart = dfWithNoTime.parse(dfWithNoTime.format(date));
        cal.setTime(datePart);
        Date sttime = timeformat.parse(timePart);
        cal.add(Calendar.HOUR, sttime.getHours());
        cal.add(Calendar.MINUTE, sttime.getMinutes());
        datePart =  cal.getTime();
        return datePart;
    }
}
