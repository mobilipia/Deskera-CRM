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

import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class DummyActivity {

    public void display(double d){
        System.out.println("Value: "+d);
    }
    public int getValue4A(){
        return 23;
    }

    public int getValue4B(){
        //throw new ArrayIndexOutOfBoundsException("can't give value for b");
        return 70;
    }

    public JSONObject getValue4C() throws JSONException{
        return new JSONObject("{first:5, sec:[10, 20, 30]}");
    }

//    public HashMap getValue4C(){
//        HashMap l = new HashMap();
//
//        l.put("first",5);
//        l.put("sec",new int[]{10,20,30});
//        return l;
//    }
    public int add(int a, int b){
        int c = a+b;
        System.out.print("(Start Add|");
        System.out.print(a+"+"+b+"="+c);
        System.out.println("|End Add)");
        return c;
    }

    public int sub(int a, int b){
        int c = a-b;
        System.out.print("(Start Sub|");
        System.out.print(a+"-"+b+"="+c);
        System.out.println("|End Sub)");
        return c;
    }

    public long mul(int a, int b){
        long c = (long)a*b;
        System.out.print("(Start Mul|");
        System.out.print(a+"*"+b+"="+c);
        System.out.println("|End Mul)");
        return c;
    }

    public double div(int a, int b){
        double c = (double)a/b;
        System.out.print("(Start Div|");
        System.out.print(a+"/"+b+"="+c);
        System.out.println("|End Div)");
        return c;
    }

    public List getManagers(){
        ArrayList<Person> mans = new ArrayList<Person>();
        mans.add(new Person("P1", "GMT-8:00", "9:00 AM", "9:00 PM"));
        mans.add(new Person("P2", "GMT-4:00", "9:00 AM", "9:00 PM"));
        mans.add(new Person("P3", "GMT-0:00", "9:00 AM", "9:00 PM"));
        mans.add(new Person("P4", "GMT+4:00", "9:00 AM", "9:00 PM"));
        mans.add(new Person("P5", "GMT+8:00", "9:00 AM", "9:00 PM"));
        return mans;
    }
    public List getCallers(){
        ArrayList<Person> cals = new ArrayList<Person>();
        cals.add(new Person("C1", "GMT-8:00", "9:00 AM", "9:00 PM"));
        cals.add(new Person("C2", "GMT-4:00", "9:00 AM", "9:00 PM"));
        cals.add(new Person("C3", "GMT-0:00", "9:00 AM", "9:00 PM"));
        cals.add(new Person("C4", "GMT+4:00", "9:00 AM", "9:00 PM"));
        cals.add(new Person("C5", "GMT+8:00", "9:00 AM", "9:00 PM"));
        return cals;
    }

    public void printPerson(Person p){
        System.out.println("Person: name="+p.getName()+" is available from "+p.getStart()+" to "+p.getEnd());
    }

    public void schedule(List sch,Person manager, Person caller, Date from, Date to){
        sch.add(new Schedule(manager, caller, from, to));
    }

    public void printSchedules(List schedule){
        Iterator<Schedule> itr = schedule.iterator();
        JSONArray jArr = new JSONArray();
        try {
            while (itr.hasNext()) {
                Schedule sch = itr.next();
                JSONObject obj = new JSONObject();
                obj.put("manager", sch.getManager().getName());
                obj.put("caller", sch.getCaller().getName());
                obj.put("from", sch.getStartTime());
                obj.put("to", sch.getEndTime());
                jArr.put(obj);
            }
            System.out.println(jArr.toString(4));
        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }

//    public Date[] findFree(Person person, int hours){
//        Iterator<Schedule> itr = schedule.iterator();
//        if(e.after(person.getEnd())) return null;
//        while(itr.hasNext()){
//            Schedule sch = itr.next();
//            if(sch.getManager().equals(person)||sch.getCaller().equals(person)){
//
//            }
//        }
//    }

    public Date addTime(Date date, int hours){
        return new Date(date.getTime()+hours*3600000);
    }

    public void testVal() {
        Person i=new Person("a","+5:30","6:00 PM","10:00 PM");
        printInt(i);
        System.out.println("Val:"+i.getName());
    }

    private void printInt(Person i) {
        i=new Person("sfsffs","+5:30","6:00 PM","10:00 PM");
    }

}
