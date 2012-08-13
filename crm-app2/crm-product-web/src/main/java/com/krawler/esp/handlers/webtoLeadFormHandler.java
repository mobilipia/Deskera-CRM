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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.FieldParams;
import com.krawler.common.admin.KWLTimeZone;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmLead;
import com.krawler.crm.database.tables.LeadRoutingUsers;
import com.krawler.crm.database.tables.webtoleadform;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.fieldDataManager;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

/**
 *
 * @author krawler
 */
public class webtoLeadFormHandler extends BaseDAO {
    private crmLeadDAO crmLeadDAOObj;
    private fieldDataManager fieldDataManagercntrl;
    private fieldManagerDAO fieldManagerDAOobj;

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    public void setFieldDataManager(fieldDataManager fieldDataManagercntrl) {
        this.fieldDataManagercntrl = fieldDataManagercntrl;
    }
    
    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }
    

    public JSONObject saveEditWTLForm(String formname, String formdomain, String redirecturl, String formfields, String companyid, String formid,String leadowner) {

        JSONObject jobj = new JSONObject();
        try {
            webtoleadform webformObj = null;

            webformObj = (webtoleadform) get(webtoleadform.class, Integer.parseInt(formid));
            if (webformObj == null) {
                webformObj = new webtoleadform();
            }
            Company companyObj = (Company) get(Company.class, companyid);
            User userObj = (User) get(User.class, leadowner);
            webformObj.setCompanyid(companyObj);
            webformObj.setFormdomain(formdomain);
            webformObj.setFormname(formname);
            webformObj.setRedirecturl(redirecturl);
            webformObj.setFormfield(formfields);
            webformObj.setLeadowner(userObj);
            webformObj.setLastupdatedon(new Date());
            save(webformObj);
            jobj.accumulate("success", true);

        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return jobj;
    }

    public JSONObject deleteWTLForm(String formid) {
        JSONObject jobj = new JSONObject();
        try {
            jobj.accumulate("success", false);
            webtoleadform webformObj = null;

            webformObj = (webtoleadform) get(webtoleadform.class, Integer.parseInt(formid));
            if (webformObj != null) {
                delete(webformObj);
                jobj.accumulate("success", true);
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return jobj;
    }

    public KwlReturnObject getWebtoleadFormlist(HashMap<String, Object> requestParams) throws ServiceException {
        List wtlformList = null;
        int totalCnt = 0;
        try {
            ArrayList filter_params = new ArrayList();
            String companyid = requestParams.get("companyid").toString();
            filter_params.add(companyid);
            String HQL = "select w from webtoleadform w where w.companyid.companyID=? ";
            if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss=requestParams.get("ss").toString();
                if(!StringUtil.isNullOrEmpty(ss)){
                    String[] searchcol = new String[]{"w.formname"};
                    StringUtil.insertParamSearchString(filter_params, ss, 1);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    HQL +=searchQuery;
                }
            }

            wtlformList = executeQuery(HQL, filter_params.toArray());
            totalCnt = wtlformList.size();
            int start = 0;
            int limit = 25;
            if (requestParams.containsKey("start") && requestParams.containsKey("limit")) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            wtlformList = executeQueryPaging(HQL, filter_params.toArray(), new Integer[]{start, limit});
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmLeadDAOImpl.getLeadss : "+e.getMessage(), e);
        } 
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", wtlformList, totalCnt);
       
    }

	public JSONObject storeLead(HashMap paramMap, String companyID,String userID, String acceptURL, int leadRoutingUser) {
		Set paramKeys = paramMap.keySet();
		Iterator itr = paramKeys.iterator();
		JSONObject resObj = new JSONObject();
		ArrayList<String> invalidInput = new ArrayList<String>();
		String successMsg = "Problem while submitting your form.";
		try {
			CrmLead crmLead = null;
			Company company = null;
			String id = java.util.UUID.randomUUID().toString();
			User user = (User) get(User.class, userID);
			company = (Company) get(Company.class, companyID);
			KWLTimeZone timeZone = authHandler.getTZforUser(user, company,(KWLTimeZone) get(KWLTimeZone.class, "1"));
			java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
			java.text.SimpleDateFormat sdf1 = new java.text.SimpleDateFormat("MMM dd, yyyy");
			JSONObject jobj = new JSONObject();
			if (paramMap.get("wl_productid") != null) {
				String[] productsId = (String[]) paramMap.get("wl_productid");
				jobj.put("productsId", productsId);
				jobj.put("productid", productsId);
			}
			if (paramMap.get("wl_email") != null && ((String[])paramMap.get("wl_email"))[0] != "") {
				String eml = ((String[])paramMap.get("wl_email"))[0];
				if (!eml.matches(Constants.emailRegex)) {
					invalidInput.add("<b>Email</b> is not valid. It must be in the format 'abc@xyz.com'.");
				}
			}
			if (paramMap.get("wl_createdon") != null && ((String[])paramMap.get("wl_createdon"))[0] != "") {
				String dt = ((String[])paramMap.get("wl_createdon"))[0];
				if (!dt.matches(Constants.dateRegex_yyyymmdd)) {
					invalidInput.add("Date is not valid. It must be in this format: 'yyyy-mm-dd'.");
				}
			}

			JSONArray jcustomarray = new JSONArray();
			boolean custom_flag = false;
			while (itr.hasNext()) {
				Object keyObj = itr.next();
				if (keyObj.toString().startsWith("wl_custom_field")) {
					custom_flag = true;
					String fieldid = keyObj.toString().replace("wl_custom_field", "");
					FieldParams fieldPObj = (FieldParams) get(FieldParams.class, fieldid);
					int xtype = fieldPObj.getFieldtype();
					String colValue = "";
					if (xtype == 7) {// Multi select dropdown
						String[] valuesArr = (String[]) paramMap.get(keyObj);
						for (int i = 0; i < valuesArr.length; i++) {
							colValue += valuesArr[i] + ",";
						}
						if (colValue.length() > 0) {
							colValue = colValue.substring(0,colValue.length() - 1);
						}
					} else if (xtype == 2) {// Number field
						colValue = ((String[]) paramMap.get(keyObj))[0];
						if (colValue!="" && !colValue.matches(Constants.numberRegex)) {
							invalidInput.add("Value for field <b>"+fieldPObj.getFieldlabel()+"</b> is not valid. It must be a number.");
						}
						
                        if(colValue.length()>fieldPObj.getMaxlength()){
                        	invalidInput.add("Maximum length for <b>"+fieldPObj.getFieldlabel()+"</b> is "+ fieldPObj.getMaxlength()+ " digits only.");
                        }
                    
					} else if (xtype == 3) { // datefield
						String datestr = ((String[]) paramMap.get(keyObj))[0].trim();
						if (datestr!="" && !datestr.matches(Constants.dateRegex_yyyymmdd)) {
							invalidInput.add(fieldPObj.getFieldlabel()+" is not a valid date.It must be in this format: 'yyyy-mm-dd' .");
						} else if(datestr!="") {
							colValue = ((Object) (sdf.parse(datestr)).getTime()).toString();
                        }
					} else {
						colValue = ((String[]) paramMap.get(keyObj))[0];
						  if(colValue.length()>fieldPObj.getMaxlength() && xtype != 4){
							  invalidInput.add("Maximum length for <b>"+fieldPObj.getFieldlabel()+"</b> is "+ fieldPObj.getMaxlength()+ " characters.");
	                      }
					}
					JSONObject jobjTemp = new JSONObject();
					jobjTemp.accumulate("filedid", fieldid);
					jobjTemp.accumulate("xtype", xtype);
					jobjTemp.accumulate("fieldname", fieldPObj.getFieldname());
					jobjTemp.accumulate("refcolumn_name", "Col"	+ fieldPObj.getRefcolnum());
					jobjTemp.accumulate(fieldPObj.getFieldname(), "Col"	+ fieldPObj.getColnum());
					jobjTemp.accumulate("Col" + fieldPObj.getColnum(), colValue);
					jcustomarray.put(jobjTemp);
				}
			}

			if (invalidInput.isEmpty()) {
				CompanyPreferences cmpPref = (CompanyPreferences) get(CompanyPreferences.class, companyID);
				String lead_type = cmpPref.isDefaultleadtype() ? "0" : "1";
				jobj.put("lastname",paramMap.get("wl_lastname") != null ? ((String[]) paramMap.get("wl_lastname"))[0]: "");
				jobj.put("firstname",paramMap.get("wl_firstname") != null ? ((String[]) paramMap.get("wl_firstname"))[0]: "");
				jobj.put("companyid", companyID);
				jobj.put("type",paramMap.get("wl_type") != null ? ((String[]) paramMap.get("wl_type"))[0] : lead_type);
				jobj.put("phone",paramMap.get("wl_phone") != null ? ((String[]) paramMap.get("wl_phone"))[0] : "");
				jobj.put("email",paramMap.get("wl_email") != null ? ((String[]) paramMap.get("wl_email"))[0] : "");
				jobj.put("street",paramMap.get("wl_addstreet") != null ? ((String[]) paramMap.get("wl_addstreet"))[0]: "");
				jobj.put("ratingid",paramMap.get("wl_rating") != null ? ((String[]) paramMap.get("wl_rating"))[0]: "");
				jobj.put("title",paramMap.get("wl_title") != null ? ((String[]) paramMap.get("wl_title"))[0] : "");
				jobj.put("leadstatusid",paramMap.get("wl_leadstatus") != null ? ((String[]) paramMap.get("wl_leadstatus"))[0]: "");
				jobj.put("industryid",paramMap.get("wl_industry") != null ? ((String[]) paramMap.get("wl_industry"))[0]	: "");
				jobj.put("leadsourceid",paramMap.get("wl_leadsource") != null ? ((String[]) paramMap.get("wl_leadsource"))[0]: "");
				jobj.put("userid", user.getUserID());
				jobj.put("isconverted", "0");
				jobj.put("istransfered", "0");
				jobj.put("updatedon", new Date());
				if (paramMap.get("wl_createdon") != null && !StringUtil.isNullOrEmpty(((String[]) paramMap.get("wl_createdon"))[0])) {
					jobj.put("createdon", (sdf.parse(((String[]) paramMap.get("wl_createdon"))[0])).getTime());
				}
				jobj.put("validflag", "0");
				jobj.put("price",paramMap.get("wl_price") != null ? ((String[]) paramMap.get("wl_price"))[0] : "");
				jobj.put("revenue",paramMap.get("wl_revenue") != null ? ((String[]) paramMap.get("wl_revenue"))[0]: "");
				jobj.put("leadid", id);
				jobj.put("tzdiff", timeZone.getDifference());
				jobj.put("leadownerid", user.getUserID());
				if (leadRoutingUser == LeadRoutingUsers.FCFS)
					jobj.put("ownerconfirm", false);
				KwlReturnObject kmsg = crmLeadDAOObj.addLeads(jobj);
				crmLead = (CrmLead) kmsg.getEntityList().get(0);

				if (custom_flag) {
					HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
					customrequestParams.put("customarray", jcustomarray);
					customrequestParams.put("modulename",Constants.Crm_Lead_modulename);
					customrequestParams.put("moduleprimarykey",Constants.Crm_Leadid);
					customrequestParams.put("modulerecid", crmLead.getLeadid());
					customrequestParams.put("companyid", companyID);
					customrequestParams.put("customdataclasspath",Constants.Crm_lead_custom_data_classpath);
					KwlReturnObject customDataresult = fieldDataManagercntrl.setCustomData(customrequestParams);
					if (customDataresult != null && customDataresult.getEntityList().size() > 0) {
						jobj = new JSONObject();
						jobj.put("leadid", crmLead.getLeadid());
						jobj.put("CrmLeadCustomDataobj", crmLead.getLeadid());
						jobj.put("tzdiff", timeZone.getDifference());
						kmsg = crmLeadDAOObj.editLeads(jobj);
					}
                    // fetch auto-number columns only
                    HashMap<String, Object> fieldrequestParams = new HashMap<String, Object>();
                    fieldrequestParams.put("isexport", true);
                    fieldrequestParams.put("filter_names", Arrays.asList("companyid", "moduleid", "fieldtype"));
                    fieldrequestParams.put("filter_values", Arrays.asList(companyID, Constants.Crm_lead_moduleid, Constants.CUSTOM_FIELD_AUTONUMBER));
                    KwlReturnObject autoNoFieldMap = fieldManagerDAOobj.getFieldParams(fieldrequestParams);
                    if (autoNoFieldMap.getEntityList().size() > 0) {
                        fieldDataManagercntrl.setAutoNumberCustomData(customrequestParams, autoNoFieldMap.getEntityList());
                    }
				}
				successMsg = "Success";
				resObj.put("leadid", crmLead.getLeadid());
			} else {
				successMsg = "Problem while submitting your form. Please provide proper values : <br/><br/>";
				int n=0;
				for (String entry : invalidInput) {
					successMsg += ++n+ ". "+ entry + "<br/>";
				}
				return resObj.put("msg", successMsg);
			}
		} catch (JSONException ex) {
			logger.warn(ex.getMessage(), ex);
		} catch (Exception ex) {
			successMsg += "<br/>";
			successMsg += ex.getMessage();
			logger.warn(ex.getMessage(), ex);
		} finally {
			try {
				resObj.put("msg", successMsg);
			} catch (JSONException ex) {
				logger.warn(ex.getMessage(), ex);
			}
		}
		return resObj;
	}
}
