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
package com.krawler.spring.organizationChart;

import com.krawler.common.admin.Assignmanager;
import com.krawler.common.admin.User;

import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 *
 * @author Karthik
 */
public class organizationChartDAOImpl extends BaseDAO implements organizationChartDAO {

    /* (non-Javadoc)
     * @see com.krawler.spring.organizationChart.organizationChartDAO#getUnmappedUsers(java.util.HashMap)
     */
    public KwlReturnObject getUnmappedUsers(HashMap<String, Object> requestParams) throws ServiceException
    {
        int dl = 0;
        List ll = null;
        String companyid = requestParams.get("companyid").toString();
        String UserId = requestParams.get("userid").toString();
        
        String Hql = "from User where userID NOT IN (select assignemp.userID from Assignmanager) and userID NOT IN (select assignman.userID from Assignmanager) and company.companyID = ? and deleteflag=0";
        ll = executeQuery(Hql, new Object[] { companyid});
        dl = ll.size();

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.organizationChart.organizationChartDAO#deleteNode(java.util.HashMap)
     */
    public KwlReturnObject deleteNode(HashMap<String, Object> requestParams) throws Exception
    {
        int dl = 0;
        List ll = new ArrayList();
        String nodeid = "";
        String details = "";
        String details2 = "";
        if (requestParams.containsKey("nodeid") && requestParams.get("nodeid").toString() != null)
        {
            nodeid = requestParams.get("nodeid").toString();
        }
        String getChild = "from Assignmanager a where a.assignman.userID = ? ";
        String getParent = "select a.id from Assignmanager a where a.assignemp.userID = ? ";

        List childList = executeQuery(getChild, new Object[] { nodeid });
        List parentList = executeQuery(getParent, new Object[] { nodeid });

        Object amPidObj = (Object) parentList.iterator().next();

        String ampid = amPidObj.toString();
        Assignmanager amP = (Assignmanager) get(Assignmanager.class, ampid);
        User u = (User) amP.getAssignemp();
        User p = (User) amP.getAssignman();
        details = u.getUserLogin().getUserName() + " [ " + u.getFirstName() + " " + u.getLastName() + " ] Un-assigned from " + p.getFirstName() + " " + p.getLastName() + " , and removed from Organization.";
        Iterator iteC = childList.iterator();
        while (iteC.hasNext())
        {
            Assignmanager amC = (Assignmanager) iteC.next();
            amC.setAssignman(amP.getAssignman());
            saveOrUpdate(amC);
            User u2 = (User) amC.getAssignemp();
            details2 = u2.getFirstName() + " " + u2.getLastName() + "  re-assigned to " + p.getFirstName() + " " + p.getLastName() + "  ";
        }
        ll.add(details);
        ll.add(details2);
        delete(amP);

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.organizationChart.organizationChartDAO#getAssignManager(java.lang.String, java.util.List, int, java.lang.String)
     */
    public void getAssignManager(String manID, List appendList, int exceptionAt, String extraQuery) throws ServiceException {
    	Stack mStack = new Stack();mStack.add(manID);
    	Map map = new HashMap();
    	List list = new ArrayList();
    	visit(mStack, null, map, new ArrayList(), list, extraQuery);
    	Collections.reverse(list);
    	appendList.addAll(list);
    	appendList.addAll(map.values());
    }
    
    private void visit(Stack mStack, Assignmanager am, Map map,  List visited, List appendList, String extraQuery) throws ServiceException{
    	if(!visited.contains(mStack.peek())){
    		visited.add(mStack.peek());
    		String Hql = "from Assignmanager where assignman.userID = ? " + extraQuery;
            List<Assignmanager> ll = executeQuery(Hql, new Object[]{mStack.peek()});
            for(Assignmanager aman:ll) {
            	String empID = aman.getAssignemp().getUserID();
            	if(mStack.contains(empID))
            		throw ServiceException.FAILURE("Circular Hierarchy found", null);
            	mStack.push(empID);
            	visit(mStack, aman, map, visited, appendList, extraQuery);
            	mStack.pop();
            }
            if(am!=null)
            	appendList.add(am);
    	}else{
            if(am!=null) map.put(mStack.peek(), am);
    	}
    }
    
    /* (non-Javadoc)
     * @see com.krawler.spring.organizationChart.organizationChartDAO#rootUser(com.krawler.utils.json.base.JSONArray, java.lang.String)
     */
    public void rootUser(JSONArray jarr, String userid) throws ServiceException {
        JSONObject objU = new JSONObject();
        try {
            User user = (User) get(User.class, userid);
            int dl = 0;
            List ll = null;
            
            String Hql = "from User where userID NOT IN (select assignemp.userID from Assignmanager) and userID NOT IN (select assignman.userID from Assignmanager) and company.companyID = ? and deleteflag=0 and userID = ?";
            ll = executeQuery(Hql, new Object[] { user.getCompany().getCompanyID(),user.getUserID()});
            dl = ll.size();
            if(user.getUserId()==user.getCompany().getCreator().getUserId()|| dl==0){
            objU.put("fromuid", new String[]{user.getUserID()});
            objU.put("userid", user.getUserID());
            objU.put("username", user.getUserLogin().getUserName());
            objU.put("emailid", user.getEmailID());
            objU.put("contactno", user.getContactNumber());
            objU.put("fname", user.getFirstName());
            objU.put("lname", user.getLastName());
            objU.put("image", user.getImage());
            objU.put("nodeid", user.getUserID());
            objU.put("address", user.getAddress());
            objU.put("level", 0);
            jarr.put(objU);
            }
        } catch (JSONException ex) {
            throw ServiceException.FAILURE("organizationChartDAOImpl.rootUser", ex);
        }
    }
}
