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
package com.krawler.spring.mailIntegration;
import com.krawler.common.admin.User;
import com.krawler.common.admin.UserLogin;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.profileHandler.profileHandlerDAO;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
//import com.krawler.crm.database.tables.AccountProject;
import javax.servlet.http.HttpServletRequest;
import com.krawler.common.service.ServiceException;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.common.util.StringUtil;
import com.krawler.esp.handlers.StorageHandler;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.documents.documentDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.net.*;
import java.text.DateFormat;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.hibernate.HibernateException;

public class mailIntegrationController extends MultiActionController {

    private profileHandlerDAO profileHandlerDAOObj;
    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private documentDAO crmDocumentDAOObj;
    private HibernateTransactionManager txnManager;
    private kwlCommonTablesDAO KwlCommonTablesDAOObj;
    private mailIntegrationDAO mailIntegrationDAOObj;

    public void setmailIntegrationDAO(mailIntegrationDAO mailIntegrationDAOObj) {
        this.mailIntegrationDAOObj = mailIntegrationDAOObj;
    }

    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setprofileHandlerDAO(profileHandlerDAO profileHandlerDAOObj1) {
        this.profileHandlerDAOObj = profileHandlerDAOObj1;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    public void setcrmDocumentDAO(documentDAO crmDocumentDAOObj1) {
        this.crmDocumentDAOObj = crmDocumentDAOObj1;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1) {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    public void setKwlCommonTablesDAO(kwlCommonTablesDAO KwlCommonTablesDAOObj1) {
        this.KwlCommonTablesDAOObj = KwlCommonTablesDAOObj1;
    }

    public ModelAndView mailIntegrate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        KwlReturnObject kmsg = null;
        JSONObject obj=new JSONObject();
        String url = storageHandlerImpl.GetSOAPServerUrl();
        String res = "";
        boolean isFormSubmit = false;
        boolean isDefaultModelView = true;
        ModelAndView mav = null;
        try {
            if(!StringUtil.isNullOrEmpty(request.getParameter("action")) && request.getParameter("action").equals("getoutboundconfid")) {
                String str = "";
                url += "getOutboundConfiId.php";
                URL u = new URL(url);
                URLConnection uc = u.openConnection();
                uc.setDoOutput(true);
                uc.setUseCaches(false);
                uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                PrintWriter pw = new PrintWriter(uc.getOutputStream());
                pw.println(str);
                pw.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String line="";

                while ((line = in.readLine()) != null) {
                    res+=line;
                }
                in.close();
            } else if(!StringUtil.isNullOrEmpty(request.getParameter("emailUIAction")) && request.getParameter("emailUIAction").equals("uploadAttachment")) {
                isDefaultModelView = false;
                url += "krawlermails.php";

                String pass = "";
                String currUser = sessionHandlerImplObj.getUserName(request) + "_";
                String userid = sessionHandlerImplObj.getUserid(request);

                String jsonStr = profileHandlerDAOObj.getUser_hash(userid);
                JSONObject currUserAuthInfo = new JSONObject(jsonStr);
                if(currUserAuthInfo.has("userhash")) {
                    currUser += currUserAuthInfo.getString("subdomain");
                    pass = currUserAuthInfo.getString("userhash");
                }

                Enumeration en = request.getParameterNames();
                String str = "username="+currUser+"&user_hash="+pass;
                while(en.hasMoreElements()){
                    String paramName = (String)en.nextElement();
                    String paramValue = request.getParameter(paramName);
                    str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
                }

                List fileItems = null;
                HashMap<String, String> arrParam = new HashMap<String, String>();
                ArrayList<FileItem> fi = new ArrayList<FileItem>();
//                if (request.getParameter("email_attachment") != null) {
                    DiskFileUpload fu = new DiskFileUpload();
                    fileItems = fu.parseRequest(request);
//                    crmDocumentDAOObj.parseRequest(fileItems, arrParam, fi, fileUpload);
                    FileItem fi1 = null;
                    for (Iterator k = fileItems.iterator(); k.hasNext();) {
                        fi1 = (FileItem) k.next();
                        if (fi1.isFormField()) {
                            arrParam.put(fi1.getFieldName(), fi1.getString("UTF-8"));
                        } else {
                            if (fi1.getSize() != 0) {
                                fi.add(fi1);
                            }
                        }
                    }
//                }
                long sizeinmb = 10; // 10 MB
                long maxsize = sizeinmb * 1024 * 1024;
                if(fi.size()>0) {
                    for (int cnt = 0; cnt < fi.size(); cnt++) {

                        if (fi.get(cnt).getSize() <= maxsize) {
                            try {
                                byte [] filecontent = fi.get(cnt).get();

                                URL u = new URL(url+"?"+str);
                                URLConnection uc = u.openConnection();
                                uc.setDoOutput(true);
                                uc.setUseCaches(false);
                                uc.setRequestProperty("Content-Type", "multipart/form-data; boundary=---------------------------4664151417711");
                                uc.setRequestProperty("Content-length", ""+filecontent.length);
                                uc.setRequestProperty("Cookie", "");

                                OutputStream outstream = uc.getOutputStream();

                                String newline = "\r\n";
                                String b1 = "";
                                b1 += "-----------------------------4664151417711" + newline;
                                b1 += "Content-Disposition: form-data; name=\"email_attachment\"; filename=\""+fi.get(cnt).getName()+"\""+newline;
                                b1 += "Content-Type: text" + newline;
                                b1 += newline;

                                String b2 = "";
                                b2 += newline + "-----------------------------4664151417711--" + newline;

                                String b3 = "";
                                b3 += "-----------------------------4664151417711" + newline;
                                b3 += "Content-Disposition: form-data; name=\"addval\";" + newline;
                                b3 += newline;

                                String b4 = "";
                                b4 += newline + "-----------------------------4664151417711--" + newline;

                                outstream.write(b1.getBytes());
                                outstream.write(b1.getBytes());
                                outstream.write(b1.getBytes());
                                outstream.write(fi.get(cnt).get());
                                outstream.write(b4.getBytes());
                                PrintWriter pw = new PrintWriter(outstream);
                                pw.println(str);
                                pw.close();
                                outstream.flush();

                                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                                String line="";

                                while ((line = in.readLine()) != null) {
                                    res+=line;
                                }
                                in.close();
                            } catch(Exception e) {
                                throw ServiceException.FAILURE(e.getMessage(), e);
                            }
                        } else {
                            JSONObject jerrtemp = new JSONObject();
                            jerrtemp.put("Success", "Fail");
                            jerrtemp.put("errmsg", "Attachment size should be upto " + sizeinmb + "mb");
                            res = jerrtemp.toString();
                        }
                    }
                } else {
                    JSONObject jerrtemp = new JSONObject();
                    jerrtemp.put("Success", "Fail");
                    jerrtemp.put("errmsg", "Attachment file size should be greater than zero");
                    res = jerrtemp.toString();
                }

            } else {

                url += "krawlermails.php";

                String pass = "";
                String currUser = sessionHandlerImplObj.getUserName(request) + "_";
                String userid = sessionHandlerImplObj.getUserid(request);

                String jsonStr = profileHandlerDAOObj.getUser_hash(userid);
                JSONObject currUserAuthInfo = new JSONObject(jsonStr);
                if(currUserAuthInfo.has("userhash")) {
                    currUser += currUserAuthInfo.getString("subdomain");
                    pass = currUserAuthInfo.getString("userhash");
                }

                Enumeration en = request.getParameterNames();
                String str = "username="+currUser+"&user_hash="+pass;
                while(en.hasMoreElements()){
                    String paramName = (String)en.nextElement();
                    String paramValue = request.getParameter(paramName);
                    str = str + "&" + paramName + "=" + URLEncoder.encode(paramValue);
                }

                URL u = new URL(url);
                URLConnection uc = u.openConnection();
                uc.setDoOutput(true);
                uc.setUseCaches(false);
                uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

                PrintWriter pw = new PrintWriter(uc.getOutputStream());
                pw.println(str);
                pw.close();

                if((!StringUtil.isNullOrEmpty(request.getParameter("emailUIAction")) && request.getParameter("emailUIAction").equals("getMessageListXML")) ||
                     (!StringUtil.isNullOrEmpty(request.getParameter("emailUIAction")) && request.getParameter("emailUIAction").equals("getMessageListKrawlerFoldersXML"))) {
                    //response.setContentType("text/xml;charset=UTF-8");
                    isFormSubmit=true;
                    int totalCnt = 0;
                    int unreadCnt = 0;
                    JSONObject jobj = new JSONObject();
                    JSONArray jarr = new JSONArray();
                    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
                    Document doc = docBuilder.parse(uc.getInputStream());

                    NodeList cntentity = doc.getElementsByTagName("TotalCount");
                    Node cntNode = cntentity.item(0);
                    if(cntNode.getNodeType()==Node.ELEMENT_NODE) {
                        Element firstPersonElement = (Element)cntNode;
                        totalCnt = Integer.parseInt(firstPersonElement.getChildNodes().item(0).getNodeValue());
                    }

                    cntentity = doc.getElementsByTagName("UnreadCount");
                    cntNode = cntentity.item(0);
                    if(cntNode.getNodeType()==Node.ELEMENT_NODE) {
                        Element firstPersonElement = (Element)cntNode;
                        unreadCnt = Integer.parseInt(firstPersonElement.getChildNodes().item(0).getNodeValue());
                    }
                    String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
                    String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
                    String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                    // check for Mail server protocol
                    boolean isYahoo = false;

                    if(request.getParameter("mbox").equals("INBOX")) {
                        u = new URL(url);
                        uc = u.openConnection();
                        uc.setDoOutput(true);
                        uc.setUseCaches(false);
                        uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                        str = "username="+currUser+"&user_hash="+pass+"&action=EmailUIAjax&emailUIAction=getIeAccount&ieId="+request.getParameter("ieId")+"&module=Emails&to_pdf=true";
                        pw = new PrintWriter(uc.getOutputStream());
                        pw.println(str);
                        pw.close();
                        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                        String line="";
                        while ((line = in.readLine()) != null) {
                            res+=line;
                        }
                        in.close();

                        JSONObject ieIDInfo = new JSONObject(res);
                        if(ieIDInfo.getString("server_url").equals("pop.mail.yahoo.com"))
                            isYahoo = true;
                    }
                    sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
                    DateFormat userdft = null;
                    if(!isYahoo)
                        userdft = KwlCommonTablesDAOObj.getUserDateFormatter(dateFormatId, timeFormatId, timeZoneDiff);
                    else
                        userdft = KwlCommonTablesDAOObj.getOnlyDateFormatter(dateFormatId, timeFormatId);


                    NodeList entity = doc.getElementsByTagName("Emails");
                    NodeList email = ((Element)entity.item(0)).getElementsByTagName("Email");
                    for(int i=0;i<email.getLength();i++){
                        JSONObject tmpObj = new JSONObject();
                        Node rowElement =  email.item(i);
                        if(rowElement.getNodeType()==Node.ELEMENT_NODE) {
                            NodeList childNodes =  rowElement.getChildNodes();
                            for(int j=0;j<childNodes.getLength();j++) {
                                Node  node = childNodes.item(j);
                                if(node.getNodeType()==Node.ELEMENT_NODE) {
                                  Element firstPersonElement = (Element)node;
                                  if(firstPersonElement!=null) {
                                      NodeList textFNList = firstPersonElement.getChildNodes();
                                      if(textFNList.item(0)!=null){
                                        if(request.getSession().getAttribute("iPhoneCRM") != null){
                                            String body = ((Node)textFNList.item(0)).getNodeValue().trim();
                                            if(body.contains("&lt;"))
                                               body = body.replace("&lt;", "<");
                                            if(body.contains("&gt;"))
                                               body = body.replace("&gt;", ">");
                                            tmpObj.put(node.getNodeName(), body);
                                        }
                                        else {
                                           String value = ((Node)textFNList.item(0)).getNodeValue().trim();
                                           if(node.getNodeName().equals("date")) {
                                                value = userdft.format(sdf.parse(value));
                                           }
                                           tmpObj.put(node.getNodeName(), value);
                                        }
                                      }
                                  }
                                }
                            }
                        }
                        jarr.put(tmpObj);
                    }
                    jobj.put("data", jarr);
                    jobj.put("totalCount", totalCnt);
                    jobj.put("unreadCount",unreadCnt);
                    res = jobj.toString();
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                    String line="";

                    while ((line = in.readLine()) != null) {
                        res+=line;
                    }
                    in.close();
                    if((!StringUtil.isNullOrEmpty(request.getParameter("emailUIAction")) && request.getParameter("emailUIAction").equals("fillComposeCache"))) {
                        isFormSubmit=true;
                    } else if(!StringUtil.isNullOrEmpty(request.getParameter("emailUIAction")) && request.getParameter("emailUIAction").equals("refreshKrawlerFolders")) {
                        if(res.equals("Not a valid entry method")) {
                            ArrayList filter_names = new ArrayList();
                            ArrayList filter_params = new ArrayList();
                            filter_names.add("u.userID");
                            filter_params.add(userid);
                            HashMap<String, Object> requestParams = new HashMap<String, Object>();
                            KwlReturnObject kwlobject = profileHandlerDAOObj.getUserDetails(requestParams, filter_names, filter_params);
                            List li = kwlobject.getEntityList();
                            if(li.size()>=0) {
                                if(li.iterator().hasNext()) {
                                    User user = (User) li.iterator().next();
                                    String returnStr = addUserEntryForEmails(user.getCompany().getCreator().getUserID(),user,user.getUserLogin(),user.getUserLogin().getPassword(),false);
                                    JSONObject emailresult = new JSONObject(returnStr);
                                    if(Boolean.parseBoolean(emailresult.getString("success"))) {
                                        pw = new PrintWriter(uc.getOutputStream());
                                        pw.println(str);
                                        pw.close();
                                        in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                                        line="";
                                        while ((line = in.readLine()) != null) {
                                            res+=line;
                                        }
                                        in.close();
                                    }
                                }
                            }
                        }
                    }else if(res.equals("bool(false)")) {
                        res= "false";
                    }
                }
            }
            if(!isFormSubmit){
                JSONObject resjobj=new JSONObject();
                resjobj.put("valid", true);
                resjobj.put("data", res);
                res = resjobj.toString();
            }
            if(isDefaultModelView)
                mav = new ModelAndView("jsonView-ex", "model", res);
            else
                mav = new ModelAndView("jsonView", "model", res);
        } catch (SessionExpiredException e) {
            mav = new ModelAndView("jsonView-ex", "model", "{'valid':false}");
            System.out.println(e.getMessage());
        } catch (Exception e) {
        	mav = new ModelAndView("jsonView-ex", "model", "{'valid':true,data:{success:false,errmsg:'"+e.getMessage()+"'}}");
            System.out.println(e.getMessage());
        }
        return mav;
    }

    public JSONObject getRecentEmailDetails(HttpServletRequest request, String recid,String emailadd) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            DateFormat userdft = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
            String dateFormatId = sessionHandlerImpl.getDateFormatID(request);
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            String userid = sessionHandlerImpl.getUserid(request);
            userdft = KwlCommonTablesDAOObj.getUserDateFormatter(dateFormatId, timeFormatId, timeZoneDiff);
            String url = StorageHandler.GetSOAPServerUrl();
            String res = "";
            String str = "";
            String pass = "";
            String currUser = sessionHandlerImplObj.getUserName(request) + "_";
            String jsonStr = profileHandlerDAOObj.getUser_hash(userid);
            //String emailadd = request.getParameter("email");
            JSONObject currUserAuthInfo = new JSONObject(jsonStr);
            if(currUserAuthInfo.has("userhash")) {
                currUser += currUserAuthInfo.getString("subdomain");
                pass = currUserAuthInfo.getString("userhash");
            }
            str = "username="+currUser+"&user_hash="+pass;
            str = str + "&action=EmailUIAjax&emailUIAction=rebuildShowAccount&krawler_body_only=true&module=Emails&to_pdf=true";

            URL u = new URL(url+"krawlermails.php");
            URLConnection uc = u.openConnection();
            uc.setDoOutput(true);
            uc.setUseCaches(false);
            uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            PrintWriter pw = new PrintWriter(uc.getOutputStream());
            pw.println(str);
            pw.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line="";

            while ((line = in.readLine()) != null) {
                res+=line;
            }
            in.close();
            boolean flag = true;
            try {
                JSONArray jarr = new JSONArray(res);
            } catch(JSONException ex) {
                flag = false;
            }
            if(flag) {
                String ieId = "";
                JSONArray jarr = new JSONArray(res);
                for(int cnt =0;cnt<jarr.length();cnt++) {
                    JSONObject tempObj = jarr.getJSONObject(cnt);
                    if(!StringUtil.isNullOrEmpty(tempObj.getString("value"))) {
                        ieId += tempObj.getString("value")+",";
                    }
                }

                if(ieId.length()>0) {
                    ieId = ieId.substring(0,ieId.length()-1);
                    str = "username="+currUser+"&user_hash="+pass;
                    str = str + "&ieId="+ieId+"&fromaddr="+emailadd+"&mbox=INBOX";
                    res = StringUtil.makeExternalRequest(url+"getSendersMails.php",str);
                } else {
                    res = "{\"count\":0,\"data\":{}}";
                }

                JSONObject resJobj = new JSONObject();
                try{
                    resJobj = new JSONObject(res);
                } catch(Exception ex) {
                    resJobj = new JSONObject("{\"count\":0,\"data\":{}}");
                }
                JSONArray FinalArr = new JSONArray();
                if(resJobj.optInt("count",0) > 0) {
                    JSONArray jArr = resJobj.getJSONArray("data");
                    for(int i=0;i<jArr.length();i++) {
                        JSONObject tmpobj = jArr.getJSONObject(i);
                        Date sendDateObj = sdf.parse(tmpobj.getString("senddate"));
                        String senddate = userdft.format(sendDateObj);
                        JSONObject obj = new JSONObject();
                        obj.put("docid", tmpobj.getString("imap_uid"));
                        obj.put("subject", tmpobj.getString("subject"));
                        obj.put("fromaddr", tmpobj.getString("fromaddr"));
                        obj.put("toaddr", tmpobj.getString("toaddr"));
                        obj.put("senddate", senddate);
                        obj.put("ie_id", tmpobj.getString("ie_id"));
                        obj.put("seen", tmpobj.getString("seen"));
    //                    obj.put("time", AuthHandler.getUserDateFormatter(request, session).format(sdf.parse(tmpobj.getString("senddate"))));
                        obj.put("time", senddate);
                        obj.put("timeobj", sendDateObj);
                        obj.put("details", tmpobj.getString("subject"));
                        obj.put("folder", "Inbox");
                        obj.put("imgsrc", "../../images/inbox.png");
                        FinalArr.put(obj);
                    }
                }

                // fetched Sent Items

                // get sent folder id
    //            String sentid = "";
    //            str = "username="+currUser+"&user_hash="+pass;
    //            str = str + "&action=EmailUIAjax&emailUIAction=refreshKrawlerFolders&krawler_body_only=true&module=Emails&to_pdf=true";
    //            res = StringUtil.makeExternalRequest(url+"getSendersMails.php",str);
    //            resJobj = new JSONObject(res);
    //            if(resJobj.has("children")) {
    //                JSONArray childArray = resJobj.getJSONArray("children");
    //                for(int cnt = 0; cnt< childArray.length(); cnt++) {
    //                    if(childArray.getJSONObject(cnt).getString("folder_type").equals("folder_type")) {
    //                        sentid = childArray.getJSONObject(cnt).getString("folder_type");
    //                    }
    //                }
    //            }
    //
    //            if(!StringUtil.isNullOrEmpty(sentid)) {
    //                str = "username="+currUser+"&user_hash="+pass;
    //                str = str + "&action=EmailUIAjax&emailUIAction=getMessageListKrawlerFoldersXML&module=Emails&to_pdf=true&start=0&limit=20&forceRefresh=false&mbox=Sent%20Emails&ieId="+sentid;
    //                res = StringUtil.makeExternalRequest(url+"krawlermails.php",str);
    //            }

                str = "userid="+userid+"&username="+currUser+"&user_hash="+pass;
                str = str + "&fromaddr="+emailadd+"&mbox=sent";
                res = StringUtil.makeExternalRequest(url+"getSendersMails.php",str);
                try{
                    resJobj = new JSONObject(res);
                } catch(Exception ex) {
                    resJobj = new JSONObject("{\"count\":0,\"data\":{}}");
                }
                if(resJobj.optInt("count",0) > 0) {
                    JSONArray jArr = resJobj.getJSONArray("data");
                    for(int i=0;i<jArr.length();i++) {
                        JSONObject tmpobj = jArr.getJSONObject(i);
                        Date sendDateObj = sdf.parse(tmpobj.getString("date_sent"));
                        String senddate = userdft.format(sendDateObj);
                        JSONObject obj = new JSONObject();
                        obj.put("docid", tmpobj.getString("id"));
                        obj.put("subject", tmpobj.getString("name"));
                        obj.put("fromaddr", tmpobj.getString("from_addr"));
                        obj.put("toaddr", tmpobj.getString("to_addrs"));
                        obj.put("senddate", senddate);
    //                    obj.put("ie_id", tmpobj.getString("ieId"));
                        obj.put("seen", tmpobj.getString("status").equals("unread") ? 0 : 1);
                        obj.put("time", senddate);
                        obj.put("details", tmpobj.getString("name"));
                        obj.put("timeobj", sendDateObj);
                        obj.put("folder", "Sent Item");
                        obj.put("imgsrc", "../../images/outbox.png");
                        FinalArr.put(obj);
                    }
                }

                for(int i = 0; i < FinalArr.length(); i++) {
                    for(int j =0; j < FinalArr.length(); j++) {
                        if(((Date) (FinalArr.getJSONObject(i).get("timeobj"))).after((Date)(FinalArr.getJSONObject(j).get("timeobj")))){
                            JSONObject jobj1 = FinalArr.getJSONObject(i);
                            FinalArr.put(i, FinalArr.getJSONObject(j));
                            FinalArr.put(j, jobj1);
                        }
                    }
                }
                jobj.put("emailList", FinalArr);
            } else {
                jobj.put("emailList",new JSONArray());
            }

        } catch (JSONException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (SessionExpiredException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (HibernateException e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (Exception e) {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }

    public String addUserEntryForEmails(String loginid, User user, UserLogin userLogin, String pwdtext, boolean isPasswordText)  throws ServiceException{
        String returnStr = "";
        try {
            String baseURLFormat = storageHandlerImpl.GetSOAPServerUrl();
            String url = baseURLFormat + "defaultUserEntry.php";
            URL u = new URL(url);
            URLConnection uc = u.openConnection();
            uc.setDoOutput(true);
            uc.setUseCaches(false);
            uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            String userName = userLogin.getUserName() +"_"+user.getCompany().getSubDomain();
            String strParam = "loginid="+loginid +"&userid="+user.getUserID()+"&username="+userName
                    +"&password="+userLogin.getPassword()+"&pwdtext="+pwdtext+"&fullname="+(user.getFirstName() + user.getLastName())+"&isadmin=0&ispwdtest="+isPasswordText;
            PrintWriter pw = new PrintWriter(uc.getOutputStream());
            pw.println(strParam);
            pw.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line="";
            while ((line = in.readLine()) != null) {
                returnStr+=line;
            }
            in.close();
            returnStr = returnStr.replaceAll("\\s+"," ").trim();
            JSONObject emailresult = new JSONObject(returnStr);
            if(Boolean.parseBoolean(emailresult.getString("success"))) {
                user.setUser_hash(emailresult.getString("pwd"));
            }
        } catch (IOException e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        } catch (JSONException e){
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return returnStr;
    }

     public ModelAndView synchMail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, ServiceException {
        String resp = "";
        try {
            mailIntegrationDAOObj.synchMail();
        } catch(Exception ex) {
            throw ServiceException.FAILURE(ex.getMessage(), ex);
        }
        return new ModelAndView("jsonView-ex", "model", resp);
    }

        }
