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
package com.krawler.esp.handlers;

import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import com.krawler.common.util.StringUtil;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.*;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class synchMailHandler implements Runnable {
    boolean isWorking = false;
    ArrayList mailQueue = new ArrayList();
    private HibernateTemplate hibernateTemplate;
    private profileHandlerDAO profileHandlerDAOObj;
    
    public boolean isIsWorking() {
        return isWorking;
    }
    
    public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public String getmailQueue(){
        String result = "";
        while (!mailQueue.isEmpty()) {
         synchMailObject eobj = (synchMailObject) mailQueue.get(0);
         result += eobj.companyid;
        }
        return result;
    }
    
    public boolean isMailQueueEmpty(){
        return mailQueue.isEmpty();
    }
    
    public void add(String userid,String companyid,String subdomain) {
        synchMailObject emailObj = new synchMailObject(userid,companyid,subdomain);
        mailQueue.add(emailObj);
    }
    
    public void run() {
        try{
            while (!mailQueue.isEmpty()) {
                this.isWorking = true;
                synchMailObject eobj = (synchMailObject) mailQueue.get(0);
                try {
                    String url = StorageHandler.GetSOAPServerUrl();
                    String companyid = eobj.companyid;
                    String subdomain = eobj.subdomain;
                    String userQuery = " from User u where u.deleteflag = 0 and u.company.companyID = '"+companyid+"'";
                    List lst = this.hibernateTemplate.find(userQuery);
                    if (lst.size() > 0) {
                        Iterator ite = lst.iterator();
                        while(ite.hasNext()) {
                            String res = "";
                            String str = "";
                            String pass = "";
                            User user = (User) ite.next();
                            String userid = user.getUserID();
                            UserLogin userlogin = (UserLogin) this.hibernateTemplate.get(UserLogin.class , userid);
                            String currUser = userlogin.getUserName() + "_";
                            currUser += subdomain;
                            pass = user.getUser_hash();
                            // Fetch configured User Email IDs
                            
                            String ieId = "";
                            String mainstr = "username="+currUser+"&user_hash="+pass;
                            str = mainstr + "&action=EmailUIAjax&emailUIAction=rebuildShowAccount&krawler_body_only=true&module=Emails&to_pdf=true";
                            res = StringUtil.makeExternalRequest(url+"krawlermails.php",str);
                            
                            try {
                                JSONArray jarr = new JSONArray(res);
                                for(int cnt =0;cnt<jarr.length();cnt++) {
                                    JSONObject tempObj = jarr.getJSONObject(cnt);
                                    String status = "continue";
                                    int currentcount =0;
                                    if(!StringUtil.isNullOrEmpty(tempObj.getString("value"))) {
                                        ieId = tempObj.getString("value");
                                        while(status.equals("continue")) {
                                            str = mainstr + "&module=Emails&cdomain="+subdomain+"&currentCount="+currentcount+"&to_pdf=true&action=EmailUIAjax&ieId="+ieId+"&synch=false&emailUIAction=checkEmailProgress&krawler_body_only=true&mbox=INBOX";
                                            res = StringUtil.makeExternalRequest(url+"krawlermails.php",str);
                                            try {
                                                JSONObject checkResultObj = new JSONObject(res);
                                                if(checkResultObj.has("status")) {
                                                    status = checkResultObj.getString("status");
                                                    currentcount = checkResultObj.getInt("count");
                                                }
                                            } catch(Exception ex) {
                                                System.out.print("JSONException "+ex.getMessage());
                                                status = "failed";
                                            }
                                        }
                                    }
                                }
                            } catch(Exception ex) {
//                                System.out.print("Not possible to sync mails for User : "+user.getUserID()+" having subdomain : "+user.getCompany().getSubDomain());
                            }
                        }
                    }
                } catch(Exception ex) {

                } finally{
                    mailQueue.remove(eobj);
                }
            }
        } catch(Exception ex) {
            
        }finally{
                this.isWorking = false;
        }
    }

}
