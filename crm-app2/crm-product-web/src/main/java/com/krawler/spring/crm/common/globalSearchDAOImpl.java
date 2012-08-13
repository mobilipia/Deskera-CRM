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
package com.krawler.spring.crm.common;

import com.krawler.utils.json.base.JSONException;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.Search.SearchBean;
import com.krawler.esp.Search.Summarizer;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Karthik
 */
public class globalSearchDAOImpl extends BaseDAO implements globalSearchDAO {

    public KwlReturnObject globalQuickSearch(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        String keyword = "";
        String type = "";
        String companyid = "";
        String Hql = "";
        try {
            if (requestParams.containsKey("type") && requestParams.get("type") != null) {
                type = requestParams.get("type").toString();
            }
            if (requestParams.containsKey("keyword") && requestParams.get("keyword") != null) {
                keyword = StringEscapeUtils.escapeJavaScript(requestParams.get("keyword").toString());
            }
            if (requestParams.containsKey("companyid") && requestParams.get("companyid") != null) {
                companyid = requestParams.get("companyid").toString();
            }
            Pattern p = Pattern.compile("(?i)tag:['?(\\s*\\w+)'?]*",
                    Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(keyword);
            boolean tagQuery = m.matches();
            ArrayList filter_params = new ArrayList();
            if (!tagQuery) {
                String MyQuery = keyword;
                String MyQuery1 = keyword;
                if (keyword.length() > 2) {
                    MyQuery = keyword + "%";
                    MyQuery1 = "% " + MyQuery;
                }
                if (type.equals("user")) {
                    Hql = "select u from User u where  u.deleteflag = 0  and u.company.companyID= ? and ( u.firstName like ? or u.lastName like ?) ";
                    filter_params.add(companyid);
                    filter_params.add(MyQuery);
                    filter_params.add(MyQuery);
                    if(requestParams.containsKey("usersList") && requestParams.get("usersList")!= null){
                        Hql +=" and u.userID in (" + requestParams.get("usersList").toString() + ") ";
                    }
                } else if (type.equals("cam")) {
                    Hql = "select c from CrmCampaign c where c.deleteflag = 0  and c.company.companyID= ? and ( c.campaignname like ? or c.campaignname like ?) and c.isarchive=false and c.validflag=1";
                    filter_params.add(companyid);
                    filter_params.add(MyQuery);
                    filter_params.add(MyQuery1);
                    if(requestParams.containsKey("usersList") && requestParams.get("usersList")!= null){
                        Hql +=" and c.usersByUserid.userID in (" + requestParams.get("usersList").toString() + ") ";
                    }
                } else if (type.equals("acc")) {
                    Hql = "select distinct c from accountOwners ao inner join ao.account c  left join c.crmProducts as p where c.deleteflag = 0  and c.company.companyID= ? and ( c.accountname like ? or c.accountname like ?) and c.isarchive=false and c.validflag=1";
                    filter_params.add(companyid);
                    filter_params.add(MyQuery);
                    filter_params.add(MyQuery1);
                    if(requestParams.containsKey("usersList") && requestParams.get("usersList")!= null){
                        Hql +=" and ao.usersByUserid.userID in (" + requestParams.get("usersList").toString() + ") ";
                    }
                    
                } else if (type.equals("opp")) {
                    Hql = "select distinct c from opportunityOwners oo inner join oo.opportunity c left join c.crmProducts as p where c.deleteflag = 0  and c.company.companyID= ? and ( c.oppname like ? or c.oppname like ?) and c.isarchive=false and c.validflag=1";
                    filter_params.add(companyid);
                    filter_params.add(MyQuery);
                    filter_params.add(MyQuery1);
                    if(requestParams.containsKey("usersList") && requestParams.get("usersList")!= null){
                        Hql +=" and oo.usersByUserid.userID in (" + requestParams.get("usersList").toString() + ") ";
                    }
                } else if (type.equals("lea")) {
                    Hql = "select distinct c from LeadOwners lo inner join lo.leadid c  left join c.crmProducts as p where c.deleteflag = 0 and c.company.companyID= ? and ( c.lastname like ? or c.lastname like ?) and c.isarchive=false and c.validflag=1 and c.isconverted= 0";
                    filter_params.add(companyid);
                    filter_params.add(MyQuery);
                    filter_params.add(MyQuery1);
                    if(requestParams.containsKey("usersList") && requestParams.get("usersList")!= null){
                        Hql +=" and lo.usersByUserid.userID in (" + requestParams.get("usersList").toString() + ") ";
                    }
                    
                } else if (type.equals("con")) {
                    Hql = "select distinct c from contactOwners co inner join co.contact c where c.deleteflag = 0  and c.company.companyID= ? and ( c.firstname like ? or c.firstname like ? or c.lastname like ? or c.lastname like ?) and c.isarchive=false and c.validflag=1 ";
                    filter_params.add(companyid);
                    filter_params.add(MyQuery);
                    filter_params.add(MyQuery1);
                    filter_params.add(MyQuery);
                    filter_params.add(MyQuery1);
                    if(requestParams.containsKey("usersList") && requestParams.get("usersList")!= null){
                        Hql +=" and co.usersByUserid.userID in (" + requestParams.get("usersList").toString() + ") ";
                    }
                } else if (type.equals("cas")) {
                    Hql = "select c from CrmCase c where  c.deleteflag = 0  and c.company.companyID= ? and ( c.subject like ? or c.subject like ? ) and c.isarchive=false and c.validflag=1";
                    filter_params.add(companyid);
                    filter_params.add(MyQuery);
                    filter_params.add(MyQuery1);
                    if(requestParams.containsKey("usersList") && requestParams.get("usersList")!= null){
                        Hql +=" and c.usersByUserid.userID in (" + requestParams.get("usersList").toString() + ") ";
                    }
                } else if (type.equals("pro")) {
                    Hql = "select c from CrmProduct c where  c.deleteflag = 0  and c.company.companyID= ? and ( c.productname like ? or c.productname like ?) and c.isarchive=false and c.validflag=1";
                    filter_params.add(companyid);
                    filter_params.add(MyQuery);
                    filter_params.add(MyQuery1);
                    if(requestParams.containsKey("usersList") && requestParams.get("usersList")!= null){
                        Hql +=" and c.usersByUserid.userID in (" + requestParams.get("usersList").toString() + ") ";
                    }
                } else if (type.equals("docs")) {
                    Hql = "select c from com.krawler.common.admin.Docs c  where c.company.companyID=? and c.deleteflag=0 and ( c.docname like ? or c.docname like ?)";
                    filter_params.add(companyid);
                    filter_params.add(MyQuery);
                    filter_params.add(MyQuery1);
                    if(requestParams.containsKey("usersList") && requestParams.get("usersList")!= null){
                        Hql +=" and c.userid.userID in (" + requestParams.get("usersList").toString() + ") ";
                    }
                }
            }
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("globalSearchDAOImpl.globalQuickSearch", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    
    public KwlReturnObject searchIndex(SearchBean bean, String querytxt, String numhits, String perpage, String startIn,
			String companyid, String userid, DateFormat dateFmt) throws ServiceException, IOException, JSONException {
        List ll1 = new ArrayList();
		Pattern p = Pattern
				.compile("^(?i)tag:[[\\s]*([\\w\\s]+[(/|\\{1})]?)*[\\s]*[\\w]+[\\s]*]*$");
		/*
		 * "^([\'" + '"' + "]?)\\s*([\\w]+[(/|\\{1})]?)*[\\w]\\1$";
		 */
		Matcher m = p.matcher(querytxt);
		boolean b = m.matches();
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
		if (!b) {
			String query = querytxt;
			String qfield = "PlainText";
			// TODO:numhits and hits per page to be used when paging tolbar is
			// attached to search grid
			int start = 0;
			int numofHitsPerPage = 10;
			int numofhits = 10;
			String resString = "{data:[";
			try {
				if (numhits != null) {
					numofhits = Integer.parseInt(numhits);
				}
				if (perpage != null) {
					numofHitsPerPage = Integer.parseInt(perpage);
				}
				if (startIn != null) {
					start = Integer.parseInt(startIn);
				}
				Hits hitresult = null;
                ArrayList filter_params = new ArrayList();
				String Hql = "select c.docid from com.krawler.common.admin.Docs c  where c.company.companyID=? and c.deleteflag=0";
                filter_params.add(companyid);
                List ll = executeQuery(Hql, filter_params.toArray());
                Iterator ite = ll.iterator();
                int dl = ll.size();
				if (query.length() > 2) {
					query += "*";
				}
				query = qfield + ":" + query;
				boolean flag = true;
				boolean found = false;
                Object row;
                while (ite.hasNext()) {
                    row=(Object)ite.next();
					found = true;
					if (flag) {
						query += " AND (";

					} else {
    						query += " OR ";
					}
					query += "DocumentId:" + row;
					flag = false;
                }
				query += ")";
				if (found) {
					hitresult = bean.skynetsearch(query, qfield);

					Iterator itr = hitresult.iterator();

                    while (itr.hasNext()) {

                        Hit hit1 = (Hit) itr.next();
                        org.apache.lucene.document.Document doc = hit1.getDocument();
                        Enumeration docfields = doc.fields();
                        Summarizer summary = new Summarizer();
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                                "EEE MMM d HH:mm:ss z yyyy");
                        java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss.0");
                        java.util.Date dt = sdf.parse(doc.get("DateModified"));
                        JSONObject tmpObj = new JSONObject();
                        tmpObj.put("DocumentId", doc.get("DocumentId"));
                        tmpObj.put("FileName", doc.get("FileName"));
                        if(!StringUtil.isNullOrEmpty(doc.get("DateModified"))){
                            tmpObj.put("DateModified",dateFmt.format(dt) );
                        }else{
                            tmpObj.put("DateModified","" );
                        }
                        tmpObj.put("Author", doc.get("Author"));
                        String fileimage = com.krawler.esp.handlers.FileHandler.getFileImage(doc.get("FileName"), 1);
                        tmpObj.put("fileimage",fileimage);
                        tmpObj.put("Size", com.krawler.esp.handlers.FileHandler.getSizeKb(doc.get("Size")));
                        tmpObj.put("Type", doc.get("Type"));
                        tmpObj.put("Summary", URLEncoder.encode(
                                summary.getSummary(
                                doc.get("PlainText"), querytxt).toString(), "UTF8").replace(
                                "+", "%20"));
                        jarr.put(tmpObj);
                    }
				}
                jobj.put("data", jarr);
				resString += "]}";
			} catch (java.text.ParseException e) {
                            logger.warn(e.getMessage(), e);
                throw ServiceException.FAILURE("SearchHandler.searchIndex", e);
            }
		} else {
			String resString = "{data:[";
			querytxt = querytxt.replaceFirst("(?i)tag:", "");
			querytxt = querytxt.trim();
			if (querytxt.contains(" ")) {
				querytxt = querytxt.replaceAll("\\s+", ",");
			}
		//	resString += docTagSearch(conn, querytxt, userid);

			if (resString.charAt(resString.length() - 1) != '[') {
				resString = resString.substring(0, (resString.length() - 1));
			}
			resString += "]}";
		}
        ll1.add(jobj);
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll1, 1);
	}
}
