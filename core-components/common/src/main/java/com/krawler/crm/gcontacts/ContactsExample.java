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

package com.krawler.crm.gcontacts;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.client.http.HttpGDataRequest;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.NoLongerAvailableException;
import com.google.gdata.util.ServiceException;
import com.google.gdata.util.common.base.StringUtil;
import com.krawler.common.util.Constants;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.crm.gcontacts.ContactsExampleParameters.Actions;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ContactsExample {

  private enum SystemGroup {
    MY_CONTACTS("Contacts", "My Contacts"),
    FRIENDS("Friends", "Friends"),
    FAMILY("Family", "Family"),
    COWORKERS("Coworkers", "Coworkers");

    private final String systemGroupId;
    private final String prettyName;

    SystemGroup(String systemGroupId, String prettyName) {
      this.systemGroupId = systemGroupId;
      this.prettyName = prettyName;
    }

    static SystemGroup fromSystemGroupId(String id) {
      for(SystemGroup group : SystemGroup.values()) {
        if (id.equals(group.systemGroupId)) {
          return group;
        }
      }
      throw new IllegalArgumentException("Unrecognized system group id: " + id);
    }

    @Override
    public String toString() {
      return prettyName;
    }
  }

  /**
   * Base URL for the feed
   */
  private final URL feedUrl;

  /**
   * Service used to communicate with contacts feed.
   */
  private final ContactsService service;

  /**
   * Projection used for the feed
   */
  private final String projection;

  /**
   * The ID of the last added contact or group.
   * Used in case of script execution - you can add and remove contact just
   * created.
   */
  private static String lastAddedId;

  /**
   * Reference to the logger for setting verbose mode.
   */
  private static final Logger httpRequestLogger =
      Logger.getLogger(HttpGDataRequest.class.getName());

  /**
   * Contacts Example.
   *
   * @param parameters command line parameters
   */
  public ContactsExample(ContactsExampleParameters parameters)
      throws MalformedURLException, AuthenticationException {
    projection = parameters.getProjection();
    String url = parameters.getBaseUrl()
        + (parameters.isGroupFeed() ? "groups/" : "contacts/")
        + parameters.getUserName() + "/" + projection;

    feedUrl = new URL(url);
    service = new ContactsService("Google-contactsExample3");

    String userName = parameters.getUserName();
    String password = parameters.getPassword();
    if (userName == null || password == null) {
      return;
    }
    service.setUserCredentials(userName, password);
  }


  private static JSONObject printContact(ContactEntry contact) {

    JSONObject tmpObj = new JSONObject();
    String lname="";
    String fname="";
    try{
        if (contact.getTitle() != null) {

            if(!StringUtil.isEmptyOrWhitespace(contact.getTitle().getPlainText())){
                String username=contact.getTitle().getPlainText();
                int pos=Math.max(0, username.indexOf(" "));
                int posComma=Math.max(0, username.indexOf(","));
                if((pos-1)==posComma){  //  for Contact Name is in format  "Singh, Kuldeep"
                    lname=username.substring(0, (pos-1));
                    if(pos>0){
                        fname=username.substring(pos);
                    }
                }else if(pos==0){  // for Contact Name is in format "Kuldeep" or "Singh"
                    lname=username;

                }else {
                    fname=username.substring(0, pos);
                    if(pos>0){
                        lname=username.substring(pos);
                    }
                }
          }
          tmpObj.put("firstName", fname);
          tmpObj.put("lastName", lname);
          tmpObj.put("contactName", contact.getTitle().getPlainText());
        }

        ByteArrayOutputStream bo=new ByteArrayOutputStream();
        PrintStream st= new PrintStream(bo);

        ElementHelper.printContact(st, contact);
        String jsondata = bo.toString();
        String email="";
        String phoneNos="";
        String phone="";
        String address="";
        String emailIds="";
        JSONArray jarr = new JSONArray("[" + jsondata + "]");
        for (int i = 0; i < jarr.length(); i++) {
            JSONObject jobj = jarr.getJSONObject(i);
            if(jobj.has("email addresses")){
                 emailIds = jobj.getString("email addresses");
                 JSONArray jarrEmail = new JSONArray(emailIds);
                 email = jarrEmail.getString(0);
            }
            if(jobj.has("phone numbers")){
                phoneNos = jobj.getString("phone numbers");
                JSONArray jarrPhone = new JSONArray(phoneNos);
                for(int j=0 ;j< jarrPhone.length();j++){
                    phone = jarrPhone.getString(j);
                    if(phone.matches(Constants.contactRegex)){
                        break;
                    }

                }
            }
            if(jobj.has("addresses")){
                String addresses = jobj.getString("addresses");
                JSONArray jarrAddress = new JSONArray(addresses);
                for(int j=0 ;j< jarrAddress.length();j++){
                     address = address+jarrAddress.getString(j)+" ,";

                }
            }

        }

        tmpObj.put("email", email);
        tmpObj.put("phone", phone);
        tmpObj.put("address", address);
    } catch (JSONException ex){
        ex.getMessage();
    }
    return tmpObj;

  }


  /**
   * Performs action specified as action parameter.
   *
   * @param example object controlling the execution
   * @param parameters parameters from command line or script
   */
  private static JSONObject processAction(ContactsExample example,
      ContactsExampleParameters parameters) throws IOException,ServiceException,JSONException {
    Actions action = parameters.getAction();
    JSONObject jobj = new JSONObject();

    switch (action) {

        case QUERY:
            jobj = example.queryEntries(parameters);
            break;
        default:
            break;
    }
    return jobj;
  }

  /**
   * Query entries (Contacts/Groups) according to parameters specified.
   *
   * @param parameters parameter for contact quest
   */
  private JSONObject queryEntries(ContactsExampleParameters parameters)
      throws IOException, ServiceException, JSONException {
    Query myQuery = new Query(feedUrl);

    JSONObject jobj = new JSONObject();
    JSONArray jarr = new JSONArray();

    if (parameters.getUpdatedMin() != null) {
      DateTime startTime = DateTime.parseDateTime(parameters.getUpdatedMin());
      myQuery.setUpdatedMin(startTime);
    }
    if (parameters.getMaxResults() != null) {
      myQuery.setMaxResults(parameters.getMaxResults().intValue());
    }
    if (parameters.getStartIndex() != null) {
      myQuery.setStartIndex(parameters.getStartIndex());
    }
    if (parameters.isShowDeleted()) {
      myQuery.setStringCustomParameter("showdeleted", "true");
    }
    if (parameters.getRequireAllDeleted() != null) {
      myQuery.setStringCustomParameter("requirealldeleted",
          parameters.getRequireAllDeleted());
    }
    if (parameters.getSortorder() != null) {
      myQuery.setStringCustomParameter("sortorder", parameters.getSortorder());
    }
    if (parameters.getOrderBy() != null) {
      myQuery.setStringCustomParameter("orderby", parameters.getOrderBy());
    }
    if (parameters.getGroup() != null) {
      myQuery.setStringCustomParameter("group", parameters.getGroup());
    }
    try {


        ContactFeed resultFeed = service.query(myQuery, ContactFeed.class);
        for (ContactEntry entry : resultFeed.getEntries()) {
          JSONObject contactJobj = new JSONObject();
          contactJobj =  printContact(entry);
          jarr.put(contactJobj);
        }

            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", resultFeed.getEntries().size());
    } catch (NoLongerAvailableException ex) {
        ex.getMessage();
   }
    return jobj;
  }



  public static JSONObject getGoogleContacts(String email, String pass) throws ServiceException{
      JSONObject jobj=null;
      try{
        String[] args1 = new String[6];
        args1[0] = "--username="+email;
        args1[1] = "--password="+pass;
        args1[2] = "--action=query";
        args1[3]="--sortorder=ascending";
        args1[4]="--max-results=1000";
        args1[5]="--start-index=1";
        ContactsExampleParameters parameters = new ContactsExampleParameters(args1);
        if (parameters.isVerbose()) {
          httpRequestLogger.setLevel(Level.FINEST);
          ConsoleHandler handler = new ConsoleHandler();
          handler.setLevel(Level.FINEST);
          httpRequestLogger.addHandler(handler);
          httpRequestLogger.setUseParentHandlers(false);
        }

        // Check that at most one of contactfeed and groupfeed has been provided
        if (parameters.isContactFeed() && parameters.isGroupFeed()) {
          throw new RuntimeException("Only one of contactfeed / groupfeed should" +
              "be specified");
        }

        ContactsExample example = new ContactsExample(parameters);

          jobj = processAction(example, parameters);
    } catch (IOException ex){
        ex.getMessage();
    } catch (JSONException ex){
        ex.getMessage();
    }
    return jobj;
  }
}
