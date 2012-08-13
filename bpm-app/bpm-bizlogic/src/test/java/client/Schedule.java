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

package client;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class Schedule {
    private Person manager;
    private Person caller;
    private Date startTime;
    private Date endTime;

    public Person getCaller() {
        return caller;
    }

    public void setCaller(Person caller) {
        this.caller = caller;
    }

    public Date getEnd() {
        return endTime;
    }

    public void setEnd(Date endTime) {
        this.endTime = endTime;
    }

    public String getEndTime(){
        return getTimeString(endTime);
    }

    public Date getStart() {
        return startTime;
    }

    public void setStart(Date startTime) {
        this.startTime = startTime;
    }

    public String getStartTime(){
        return getTimeString(startTime);
    }

    private String getTimeString(Date date){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0:00"));
        cal.setTime(date);
        int h = cal.get(Calendar.HOUR);
        int m = cal.get(Calendar.MINUTE);
        int ap = cal.get(Calendar.AM_PM);
        return h+":"+(m<10?"0":"")+m+" "+(ap==Calendar.AM?"AM":"PM");
    }

    public Person getManager() {
        return manager;
    }

    public void setManager(Person manager) {
        this.manager = manager;
    }

    public Schedule(Person manager, Person caller, Date startTime, Date endTime) {
        this.manager = manager;
        this.caller = caller;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
