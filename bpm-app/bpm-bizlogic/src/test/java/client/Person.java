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
public class Person {
    private String name;
    private int s_hr, s_mn, s_ap, e_hr, e_mn, e_ap;
    private TimeZone tz;

    public Person() {
    }

    public Person(String name, String tz, String stime, String etime) {
        this.name= name;
        this.setTimeZone(tz);
        this.setStartTime(stime);
        this.setEndTime(etime);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeZone(String id){
        tz = TimeZone.getTimeZone(id);
    }

    public String getTimeZone(){
        return tz.getID();
    }

    public void setStartTime(String time){
        int[] t=checkValidTime(time);
        s_hr=t[0];
        s_mn=t[1];
        s_ap=t[2];
    }

    public String getStartTime(){
        return getTimeString(s_hr, s_mn, s_ap);
    }

    private String getTimeString(int h,int m,int ap){
        return h+":"+(m<10?"0":"")+m+" "+(ap==Calendar.AM?"AM":"PM");
    }

    public Date getStart(){
        Calendar cal=Calendar.getInstance(tz);
        cal.clear();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), s_hr+(s_ap==Calendar.AM?0:12), s_mn, 0);
        return cal.getTime();
    }

    public Date getEnd(){
        Calendar cal=Calendar.getInstance(tz);
        cal.clear();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), e_hr+(e_ap==Calendar.AM?0:12), e_mn, 0);
        return cal.getTime();

    }

    public void setEndTime(String time){
        int[] t=checkValidTime(time);
        e_hr=t[0];
        e_mn=t[1];
        e_ap=t[2];
    }

    public String getEndTime(){
        return getTimeString(e_hr, e_mn, e_ap);
    }

    private int[] checkValidTime(String time){
        String arr[]=time.split(":| ");
        int[] tmp=null;
        try{
            int i=Integer.parseInt(arr[0]);
            int j=Integer.parseInt(arr[1]);
            if(!(arr.length==3&&(arr[2].equalsIgnoreCase("AM")||arr[2].equalsIgnoreCase("PM")))||i<0||i>12||j<0||j>59)
                throw new IllegalArgumentException("Not a Valid Time");
            int k = Calendar.PM;
            if(arr[2].equalsIgnoreCase("AM"))
                k= Calendar.AM;
            tmp=new int[]{i,j,k};
        }catch(Exception e){
            throw new IllegalArgumentException("Not a Valid Time");
        }
        return tmp;
    }

    @Override
    public String toString(){
        return name+'['+getStartTime()+'-'+getEndTime()+']'+'('+tz+')';
    }
}
