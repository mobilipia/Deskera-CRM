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
package com.krawler.spring.crm.emailMarketing;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CampaignLog;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class bounceHandlerImpl extends BaseDAO {

    static Map status_class = new HashMap();
    static Map status_sub_class = new HashMap();

    public KwlReturnObject checkBounceStatus() throws ServiceException {
        boolean successFlag = false;
        try {

            JSONObject jobj = new JSONObject(StringUtil.makeExternalRequest(ConfigReader.getinstance().get("soap_server_url") + "getbouncereport.php", ""));
            String HQL = " from CampaignLog c where c.targettrackerkey = ?";

            if (jobj.getBoolean("success")) {
                JSONArray jarry = jobj.getJSONArray("data");
                for (int i = 0; i < jarry.length(); i++) {

                    List clogList = executeQuery(HQL, new Object[]{jarry.getJSONObject(i).getString("trackerid")});
                    Iterator itr = clogList.iterator();
                    while (itr.hasNext()) {
//                        tx = session.beginTransaction();
                        CampaignLog c = (CampaignLog) itr.next();
                        c.setSendingfailed(1);
                        c.setBounceStatus(jarry.getJSONObject(i).getString("status"));
                        save(c);
//                        tx.commit();
                    }
                }
            }
            
            successFlag = true;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("bounceHandlerImpl.checkBounceStatus : "+e.getMessage(), e);
        }
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", null, 0);
    }

    public KwlReturnObject getBounceReport(HashMap requestParams) throws ServiceException {
        JSONObject resultObj = new JSONObject();
        boolean successFlag = false;
        boolean isexport = false;
        List ll = new ArrayList();
        int totalCount = 0;
        initStatusCodes();
        try {
            String filter = "";
            int type=-1;
            int start=0;
            int limit=25;
            String emailmarketingid="";
            if(requestParams.containsKey("type") && requestParams.get("type")!=null){
                type = Integer.parseInt(requestParams.get("type").toString());
            }
            if(requestParams.containsKey("isexport") && requestParams.get("isexport")!=null){
                isexport = Boolean.parseBoolean(requestParams.get("isexport").toString());
            }
            if(requestParams.containsKey("emailmarketingid") && requestParams.get("emailmarketingid")!=null){
                emailmarketingid = requestParams.get("emailmarketingid").toString();
            }
            if(requestParams.containsKey("start") && requestParams.get("start")!=null){
                start = Integer.parseInt(requestParams.get("start").toString());
            }
            if(requestParams.containsKey("limit") && requestParams.get("limit")!=null){
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            ArrayList object = new ArrayList();
			switch(type) {
                case 0:
                    filter = "c.sendingfailed = ?";
                    object.add(1);
                    break;
                case 1:// view mails
                    filter = "c.viewed = ? and c.sendingfailed = ?";
                    object.add(1);
                    object.add(0);
                    break;
                case 2:// unssen mails
                    filter = "c.viewed = ? and c.sendingfailed = ?";
                    object.add(0);
                    object.add(0);
                    break;
                case 3:
                    filter = "c.activitytype=?";
                    object.add(CampaignConstants.Crm_isunsubscribe);
                    break;
            }
            
            String HQL = " from CampaignLog c where "+filter+" and c.emailmarketingid.id = ? and c.targetid.deleted=0";
            object.add(emailmarketingid);
            if(requestParams.containsKey("ss") && requestParams.get("ss")!=null){
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"c.targetid.emailid"};
                    StringUtil.insertParamSearchString(object, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    HQL +=searchQuery;
                }
            }
            List bounceLogListCount = executeQuery(HQL,object.toArray());
            Iterator itr = bounceLogListCount.iterator();
            if(!isexport){
                List bounceLogList = executeQueryPaging(HQL,object.toArray(), new Integer[]{start, limit});
                itr = bounceLogList.iterator();
            }
            
            while (itr.hasNext()) {
                JSONObject tempobj = new JSONObject();
                CampaignLog c = (CampaignLog) itr.next();
                tempobj.accumulate("email", c.getTargetid().getEmailid());
                tempobj.accumulate("fname", c.getTargetid().getFname());
                    tempobj.accumulate("lname", c.getTargetid().getLname());
                tempobj.accumulate("status", getReadableStatus(c.getBounceStatus(), 0));
                tempobj.accumulate("description", getReadableStatus(c.getBounceStatus(), 1));
                tempobj.accumulate("targetid", c.getTargetid().getId());
                tempobj.accumulate("subcription", c.getActivitytype());
                tempobj.accumulate("statustype", type); 
                if(isexport)
                    resultObj.append("coldata", tempobj);
                else
                    resultObj.append("data", tempobj);
            }
            if(isexport){
                if(!resultObj.has("coldata")){
                    resultObj.append("coldata", new JSONObject());
                }
            }else{
                if(!resultObj.has("data")){
                    resultObj.accumulate("data","");
                }
            }

            resultObj.accumulate("success", true);
            resultObj.accumulate("totalCount", bounceLogListCount.size());
            successFlag = true;
            totalCount = bounceLogListCount.size();
            ll.add(resultObj);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("bounceHandlerImpl.getBounceReport : "+e.getMessage(), e);
        }
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", ll, totalCount);

    }

    public KwlReturnObject deleteBouncedTargets(String[] targets) throws ServiceException {
        String targetstr = "";
        boolean successFlag = false;
        for (int cnt = 0; cnt < targets.length; cnt++) {

            targetstr += "'" + targets[cnt] + "'";
            if (cnt != (targets.length-1)) {
                targetstr += ",";
            }
        }
        try {
            String HQL = "update TargetListTargets set deleted = 1 where id in (" + targetstr + ")";
            executeUpdate(HQL);

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("bounceHandlerImpl.deleteBouncedTargets : "+e.getMessage(), e);
        }
        successFlag = true;
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", null, 0);

    }

    public static String getReadableStatus(String code, int description) {
        String result = "";
        if (!StringUtil.isNullOrEmpty(code)) {
            String[] codesplit = code.split("\\.");

            if (description == 0) {
                result = status_class.get(codesplit[0]).toString();
            } else {
                result = status_sub_class.get(codesplit[1] + "." + codesplit[2] + "-descr").toString();
            }
        } else {
            result = "Status undetermined yet";
        }
        return result;
    }

    public static void initStatusCodes() {
        status_class.put("m", "Failure");
        status_class.put("m-descr", "Message can not be queued for sending because of some problem");
        
        status_class.put("2", "Success");
        status_class.put("2-descr", "Success specifies that the DSN is reporting a positive delivery action.  Detail sub-codes may provide notification of transformations required for delivery.");

        status_class.put("4", "Persistent Transient Failure");
        status_class.put("4-descr", "A persistent transient failure is one in which the message as sent is valid, but some temporary event prevents the successful sending of the message.  Sending in the future may be successful.");

        status_class.put("5", "Permanent Failure");
        status_class.put("5-descr", "A permanent failure is one which is not likely to be resolved by resending the message in the current form.  Some change to the message or the destination must be made for successful delivery.");

        status_sub_class.put("0.0", "Other undefined Status");
        status_sub_class.put("0.0-descr", "Other undefined status is the only undefined error code. It should be used for all errors for which only the class of the error is known.");

        status_sub_class.put("1.0", "Other address status");
        status_sub_class.put("1.0-descr", "Something about the address specified in the message caused this DSN.");

        status_sub_class.put("1.1", "Bad destination mailbox address");
        status_sub_class.put("1.1-descr", "The mailbox specified in the address does not exist.  For Internet mail names, this means the address portion to the left of the @ sign is invalid.");

        status_sub_class.put("1.2", "Bad destination system address");
        status_sub_class.put("1.2-descr", "The destination system specified in the address does not exist or is incapable of accepting mail.  For Internet mail names, this means the address portion to the right of the @ is invalid for mail.");

        status_sub_class.put("1.3", "Bad destination mailbox address syntax");
        status_sub_class.put("1.3-descr", "The destination address was syntactically invalid.  This can apply to any field in the address.");

        status_sub_class.put("1.4", "Destination mailbox address ambiguous");
        status_sub_class.put("1.4-descr", "The mailbox address as specified matches one or more recipients on the destination system.  This may result if a heuristic address mapping algorithm is used to map the specified address to a local mailbox name.");

        status_sub_class.put("1.5", "Destination address valid");
        status_sub_class.put("1.5-descr", "This mailbox address as specified was valid.  This status code should be used for positive delivery reports.");

        status_sub_class.put("1.6", "Destination mailbox has moved, No forwarding address");
        status_sub_class.put("1.6-descr", "The mailbox address provided was at one time valid, but mail is no longer being accepted for that address.");

        status_sub_class.put("1.7", "Bad sender's mailbox address syntax");
        status_sub_class.put("1.7-descr", "The sender's address was syntactically invalid.  This can apply to any field in the address.");

        status_sub_class.put("1.8", "Bad sender's system address");
        status_sub_class.put("1.8-descr", "The sender's system specified in the address does not exist or is incapable of accepting return mail.  For domain names, this means the address portion to the right of the @ is invalid for mail. ");

        status_sub_class.put("2.0", "Other or undefined mailbox status");
        status_sub_class.put("2.0-descr", "The mailbox exists, but something about the destination mailbox has caused the sending of this DSN.");

        status_sub_class.put("2.1", "Mailbox disabled, not accepting messages");
        status_sub_class.put("2.1-descr", "The mailbox exists, but is not accepting messages.  This may be a permanent error if the mailbox will never be re-enabled or a transient error if the mailbox is only temporarily disabled.");

        status_sub_class.put("2.2", "Mailbox full");
        status_sub_class.put("2.2-descr", "The mailbox is full because the user has exceeded a per-mailbox administrative quota or physical capacity.  The general semantics implies that the recipient can delete messages to make more space available.");

        status_sub_class.put("2.3", "Message length exceeds administrative limit");
        status_sub_class.put("2.3-descr", "A per-mailbox administrative message length limit has been exceeded.  This status code should be used when the per-mailbox message length limit is less than the general system limit. ");

        status_sub_class.put("2.4", "Mailing list expansion problem");
        status_sub_class.put("2.4-descr", "The mailbox is a mailing list address and the mailing list was unable to be expanded.");

        status_sub_class.put("3.0", "Other or undefined mail system status");
        status_sub_class.put("3.0-descr", "The destination system exists and normally accepts mail, but something about the system has caused the generation of this DSN.");

        status_sub_class.put("3.1", "Mail system full");
        status_sub_class.put("3.1-descr", "Mail system storage has been exceeded.  The general semantics imply that the individual recipient may not be able to delete material to make room for additional messages.  ");

        status_sub_class.put("3.2", "System not accepting network messages");
        status_sub_class.put("3.2-descr", "The host on which the mailbox is resident is not accepting messages.  Examples of such conditions include an immanent shutdown, excessive load, or system maintenance.  ");

        status_sub_class.put("3.3", "System not capable of selected features");
        status_sub_class.put("3.3-descr", "Selected features specified for the message are not supported by the destination system.  This can occur in gateways when features from one domain cannot be mapped onto the supported feature in another.");

        status_sub_class.put("3.4", "Message too big for system");
        status_sub_class.put("3.4-descr", "The message is larger than per-message size limit.  This limit may either be for physical or administrative reasons.");

        status_sub_class.put("3.5", "System incorrectly configured");
        status_sub_class.put("3.5-descr", "The system is not configured in a manner which will permit it to accept this message.");

        status_sub_class.put("4.0", "Other or undefined network or routing status");
        status_sub_class.put("4.0-descr", "Something went wrong with the networking, but it is not clear what the problem is, or the problem cannot be well expressed with any of the other provided detail codes.");

        status_sub_class.put("4.1", "No answer from host");
        status_sub_class.put("4.1-descr", "The outbound connection attempt was not answered, either because the remote system was busy, or otherwise unable to take a call.  ");

        status_sub_class.put("4.2", "Bad connection");
        status_sub_class.put("4.2-descr", "The outbound connection was established, but was otherwise unable to complete the message transaction, either because of time-out, or inadequate connection quality. ");

        status_sub_class.put("4.3", "Directory server failure");
        status_sub_class.put("4.3-descr", "The network system was unable to forward the message, because a directory server was unavailable.");

        status_sub_class.put("4.4", "Unable to route");
        status_sub_class.put("4.4-descr", "The mail system was unable to determine the next hop for the message because the necessary routing information was unavailable from the directory server. A DNS lookup returning only an SOA (Start of Administration) record for a domain name is one example of the unable to route error.");

        status_sub_class.put("4.5", "Mail system congestion");
        status_sub_class.put("4.5-descr", "The mail system was unable to deliver the message because the mail system was congested. ");

        status_sub_class.put("4.6", "Routing loop detected");
        status_sub_class.put("4.6-descr", "A routing loop caused the message to be forwarded too many times, either because of incorrect routing tables or a user forwarding loop. ");

        status_sub_class.put("4.7", "Delivery time expired");
        status_sub_class.put("4.7-descr", "The message was considered too old by the rejecting system, either because it remained on that host too long or because the time-to-live value specified by the sender of the message was exceeded.");

        status_sub_class.put("5.0", "Other or undefined protocol status");
        status_sub_class.put("5.0-descr", "Something was wrong with the protocol necessary to deliver the message to the next hop and the problem cannot be well expressed with any of the other provided detail codes.");

        status_sub_class.put("5.1", "Invalid command");
        status_sub_class.put("5.1-descr", "A mail transaction protocol command was issued which was either out of sequence or unsupported.");

        status_sub_class.put("5.2", "Syntax error");
        status_sub_class.put("5.2-descr", "A mail transaction protocol command was issued which could not be interpreted, either because the syntax was wrong or the command is unrecognized. ");

        status_sub_class.put("5.3", "Too many recipients");
        status_sub_class.put("5.3-descr", "More recipients were specified for the message than could have been delivered by the protocol.  This error should normally result in the segmentation of the message into two, the remainder of the recipients to be delivered on a subsequent delivery attempt.  It is included in this list in the event that such segmentation is not possible.");

        status_sub_class.put("5.4", "Invalid command arguments");
        status_sub_class.put("5.4-descr", "A valid mail transaction protocol command was issued with invalid arguments, either because the arguments were out of range or represented unrecognized features.");

        status_sub_class.put("5.5", "Wrong protocol version");
        status_sub_class.put("5.5-descr", "A protocol version mis-match existed which could not be automatically resolved by the communicating parties.");

        status_sub_class.put("6.0", "Other or undefined media error");
        status_sub_class.put("6.0-descr", "Something about the content of a message caused it to be considered undeliverable and the problem cannot be well expressed with any of the other provided detail codes. ");

        status_sub_class.put("6.1", "Media not supported");
        status_sub_class.put("6.1-descr", "The media of the message is not supported by either the delivery protocol or the next system in the forwarding path.");

        status_sub_class.put("6.2", "Conversion required and prohibited");
        status_sub_class.put("6.2-descr", "The content of the message must be converted before it can be delivered and such conversion is not permitted.  Such prohibitions may be the expression of the sender in the message itself or the policy of the sending host.");

        status_sub_class.put("6.3", "Conversion required but not supported");
        status_sub_class.put("6.3-descr", "The message content must be converted to be forwarded but such conversion is not possible or is not practical by a host in the forwarding path.  This condition may result when an ESMTP gateway supports 8bit transport but is not able to downgrade the message to 7 bit as required for the next hop.");

        status_sub_class.put("6.4", "Conversion with loss performed");
        status_sub_class.put("6.4-descr", "This is a warning sent to the sender when message delivery was successfully but when the delivery required a conversion in which some data was lost.  This may also be a permanant error if the sender has indicated that conversion with loss is prohibited for the message.");

        status_sub_class.put("6.5", "Conversion Failed");
        status_sub_class.put("6.5-descr", "A conversion was required but was unsuccessful.  This may be useful as a permanent or persistent temporary notification.");

        status_sub_class.put("7.0", "Other or undefined security status");
        status_sub_class.put("7.0-descr", "Something related to security caused the message to be returned, and the problem cannot be well expressed with any of the other provided detail.  This status may also be used when the condition cannot be further described because of security policies in force.");

        status_sub_class.put("7.1", "Delivery not authorized, message refused");
        status_sub_class.put("7.1-descr", "The sender is not authorized to send to the destination. This can be the result of per-host or per-recipient filtering.  This memo does not discuss the merits of any such filtering, but provides a mechanism to report such.");

        status_sub_class.put("7.2", "Mailing list expansion prohibited");
        status_sub_class.put("7.2-descr", "The sender is not authorized to send a message to the intended mailing list.");

        status_sub_class.put("7.3", "Security conversion required but not possible");
        status_sub_class.put("7.3-descr", "A conversion from one secure messaging protocol to another was required for delivery and such conversion was not possible.");

        status_sub_class.put("7.4", "Security features not supported");
        status_sub_class.put("7.4-descr", "A message contained security features such as secure authentication which could not be supported on the delivery protocol.");

        status_sub_class.put("7.5", "Cryptographic failure");
        status_sub_class.put("7.5-descr", "A transport system otherwise authorized to validate or decrypt a message in transport was unable to do so because necessary information such as key was not available or such information was invalid.");

        status_sub_class.put("7.6", "Cryptographic algorithm not supported");
        status_sub_class.put("7.6-descr", "A transport system otherwise authorized to validate or decrypt a message was unable to do so because the necessary algorithm was not supported. ");

        status_sub_class.put("7.7", "Message integrity failure");
        status_sub_class.put("7.7-descr", "A transport system otherwise authorized to validate a message was unable to do so because the message was corrupted or altered.  This may be useful as a permanent, transient persistent, or successful delivery.");
        
        status_sub_class.put("m.g", "Message undelivered");
        status_sub_class.put("m.g-descr", "Message can not be sent because of unidentified reason.");   
        status_sub_class.put("c.r", "Connection refused");
        status_sub_class.put("c.r-descr", "Message can not be sent because mail server refused to connect(service not available on that server).");   
        status_sub_class.put("a.g", "Authentication failed");
        status_sub_class.put("a.g-descr", "The connect method on a Store or transport object failed due to an authentication failure (e.g., bad user name or password).");
        status_sub_class.put("f.c", "Folder closed");
        status_sub_class.put("f.c-descr", "A method is invoked on a messaging object and the folder that owns that object has died due to some reason.");
        status_sub_class.put("f.n", "Folder not found");
        status_sub_class.put("f.n-descr", "Folder method is invoked on a non existent folder");
        status_sub_class.put("i.w", "Illegal write");
        status_sub_class.put("i.w-descr", "Read only part of the message cannot be changed");
        status_sub_class.put("m.r", "Message removed");
        status_sub_class.put("m.r-descr", "Message does not exist.");
        status_sub_class.put("m.n", "Method not suported");
        status_sub_class.put("m.n-descr", "Internal error (Method not supported)");
        status_sub_class.put("n.p", "No such provider");
        status_sub_class.put("n.p-descr", "Given provider doesn't exist.");
        status_sub_class.put("r.o", "Read Only Folder");
        status_sub_class.put("r.o-descr", "Message resides in a read only folder");
        status_sub_class.put("p.g", "Parsing failed");
        status_sub_class.put("p.g-descr", "Message cannot be parsed successfully");
        status_sub_class.put("s.g", "Searching failed");
        status_sub_class.put("s.g-descr", "Wrong search expression given, cannot search on given expression");
        status_sub_class.put("s.f", "Sending failed");
        status_sub_class.put("s.f-descr", "The message cannot be sent. the reason may be invalid address or the mail server problem");
        status_sub_class.put("s.i", "Invalid email address");
        status_sub_class.put("s.i-descr", "The message cannot be sent because email address is invalid.");
        status_sub_class.put("s.c", "Store closed");
        status_sub_class.put("s.c-descr", "A method is invoked on the message and the store that owns that message has died due to some reason.");
    }
}
