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
package com.krawler.spring.crm.campaignModule;

import com.krawler.common.admin.Company;
import com.krawler.common.admin.User;
import java.util.ArrayList;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmCampaign;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.dbhandler.crmManagerCommon;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.crm.database.tables.CrmCombodata;
import com.krawler.crm.database.tables.CrmCombomaster;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.dao.DataAccessException;

public class crmCampaignDAOImpl extends BaseDAO implements crmCampaignDAO {

    @Override
    public KwlReturnObject getActiveCampaign(String campaignid) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select c from CrmCampaign c  where  c.deleteflag=0  and c.isarchive= ? and c.id = ?";
            ll = executeQuery(Hql, new Object[]{false, campaignid});
            dl = ll.size();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.getActiveCampaign : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, "002", "", ll, dl);
    }

    @Override
    public KwlReturnObject getCampaigns(HashMap<String, Object> requestParams, StringBuffer usersList) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            boolean archive = false;
            requestParams.put(Constants.moduleid, 7);
            if (requestParams.containsKey("isarchive") && requestParams.get("isarchive") != null) {
                archive = Boolean.parseBoolean(requestParams.get("isarchive").toString());
            }
            String companyid = requestParams.get("companyid").toString();
            String appendCase = "and";
            ArrayList filter_params = new ArrayList();
            String leftjoin = crmManagerCommon.getJoinQuery(requestParams);
            String Hql = "select distinct c from CrmCampaign c left join c.crmCombodataByCampaigntypeid cc" + leftjoin + " where  c.deleteflag=0  and c.isarchive= ? and c.company.companyID= ? ";
            filter_params.add(archive);
            filter_params.add(companyid);
            String Searchjson = "";
            if (requestParams.containsKey("searchJson") && requestParams.get("searchJson") != null) {
                Searchjson = requestParams.get("searchJson").toString();
                if (!StringUtil.isNullOrEmpty(Searchjson)) {
                    requestParams.put(Constants.Searchjson, Searchjson);
                    requestParams.put(Constants.appendCase, appendCase);
                    String mySearchFilterString = String.valueOf(StringUtil.getMyAdvanceSearchString(requestParams).get(Constants.myResult));
                    Hql += mySearchFilterString;
                    StringUtil.insertParamAdvanceSearchString(filter_params, Searchjson);
                }
            }

            Object config = null;
            String emailcampaign = "";
            if (requestParams.containsKey("config")) {
                config = requestParams.get("config");
            }
            if (config != null) {
                Hql += " and c.validflag=1 ";
            }

            if (requestParams.containsKey("emailcampaign")) {
                emailcampaign = requestParams.get("emailcampaign").toString();
            }
            String[] searchcol = new String[]{"c.campaignname", "c.objective", "cc.value"};
            if (!StringUtil.isNullOrEmpty(emailcampaign)) {
                Hql += " and cc.crmCombodata.valueid='b0e71040-b46d-4fc0-bfe3-1fccca96016f' ";
                searchcol = new String[]{"c.campaignname"};
            }
            
            if (requestParams.containsKey("ss") && requestParams.get("ss") != null) {
                String ss = requestParams.get("ss").toString();
                if (!StringUtil.isNullOrEmpty(ss)) {
                    StringUtil.insertParamSearchString(filter_params, ss, searchcol.length);
                    String searchQuery = StringUtil.getSearchString(ss, "and", searchcol);
                    Hql += searchQuery;
                }
            }

            int start = 0;
            int limit = 25;
            if (requestParams.containsKey("start") && requestParams.containsKey("limit")) {
                if (requestParams.containsKey("iPhoneCRM") && requestParams.get("iPhoneCRM") != null) {
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                    start = Integer.parseInt(requestParams.get("start").toString()) * limit;
                } else {
                    start = Integer.parseInt(requestParams.get("start").toString());
                    limit = Integer.parseInt(requestParams.get("limit").toString());
                }
            }
            String selectInQuery = Hql;
            boolean heirarchyPerm = false;
            if (requestParams.containsKey("heirarchyPerm") && requestParams.get("heirarchyPerm") != null) {
                heirarchyPerm = Boolean.parseBoolean(requestParams.get("heirarchyPerm").toString());
            }
            if (!heirarchyPerm) {
                selectInQuery = Hql + " and c.usersByUserid.userID in (" + usersList + ") ";
            }
            String orderQuery = " order by c.createdOn desc ";
            if (requestParams.containsKey("field") && requestParams.get("xfield") != null) {
                String dbname = crmManagerCommon.getFieldDbName(requestParams);
                if (dbname != null) {
                    String dir = requestParams.get("direction").toString();
                    orderQuery = " order by " + dbname + " " + dir + " ";
                }
            }

            selectInQuery = selectInQuery + orderQuery;

            ll = executeQuery(selectInQuery, filter_params.toArray());
            dl = ll.size();
            String export = "";
            if (requestParams.containsKey("export") && requestParams.get("export") != null) {
                export = requestParams.get("export").toString();
            }
            if (StringUtil.isNullOrEmpty(export)) {
                ll = executeQueryPaging(selectInQuery, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.getCampaigns : " + e.getMessage(), e);
        }

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject addCampaigns(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            CrmCampaign crmCampaign = new CrmCampaign();
            if (jobj.has("campaignid")) {
                id = jobj.getString("campaignid");
                crmCampaign.setCampaignid(id);
            }
            if (jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmCampaign.setCompany((Company) get(Company.class, companyid));
            }
            if (jobj.has("campaignname")) {
                crmCampaign.setCampaignname(jobj.getString("campaignname"));
            }
            if (jobj.has("objective")) {
                crmCampaign.setObjective(jobj.getString("objective"));
            }
            if (jobj.has("campaigntypeid")) {
                crmCampaign.setCrmCombodataByCampaigntypeid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("campaigntypeid")));
            }
            if (jobj.has("campaignstatusid")) {
                crmCampaign.setCrmCombodataByCampaignstatusid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("campaignstatusid")));
            }
            if (jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmCampaign.setUsersByUpdatedbyid((User) get(User.class, userid));
                crmCampaign.setUsersByCreatedbyid((User) get(User.class, userid));
            }
            if (jobj.has("startdate")) {
                crmCampaign.setStartingdate(jobj.getLong("startdate"));
            }
            if (jobj.has("enddate")) {
                crmCampaign.setEndingdate(jobj.getLong("enddate"));
            }
            if (jobj.has("expectedresponse")) {
                crmCampaign.setExpectedresponse(jobj.getString("expectedresponse"));
            }
            if (jobj.has("campaignownerid")) {
                crmCampaign.setUsersByUserid((User) get(User.class, jobj.getString("campaignownerid")));
            }
            if (jobj.has("updatedon")) {
                crmCampaign.setUpdatedOn(jobj.getLong("updatedon"));
            }
            crmCampaign.setCreatedOn(new Date().getTime());
            if (jobj.has("validflag")) {
                crmCampaign.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }

            saveOrUpdate(crmCampaign);

            ll.add(crmCampaign);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.addCampaigns : " + e.getMessage(), e);
        } catch (DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.addCampaigns : " + e.getMessage(), e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.addCampaigns : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
     public CrmCampaign getCampaignById(String id){
        return (CrmCampaign) get(CrmCampaign.class, id);
    }
    @Override
    public KwlReturnObject editCampaigns(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        try {
            String companyid = null;
            String userid = null;
            String id = "";
            if (jobj.has("campaignid")) {
                id = jobj.getString("campaignid");
            }
            CrmCampaign crmCampaign = (CrmCampaign) get(CrmCampaign.class, id);

            if (jobj.has("companyid")) {
                companyid = jobj.getString("companyid");
                crmCampaign.setCompany((Company) get(Company.class, companyid));
            }
            if (jobj.has("campaignname")) {
                crmCampaign.setCampaignname(jobj.getString("campaignname"));
            }
            if (jobj.has("objective")) {
                crmCampaign.setObjective(jobj.getString("objective"));
            }
            if (jobj.has("campaigntypeid")) {
                crmCampaign.setCrmCombodataByCampaigntypeid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("campaigntypeid")));
            }
            if (jobj.has("campaignstatusid")) {
                crmCampaign.setCrmCombodataByCampaignstatusid((DefaultMasterItem) get(DefaultMasterItem.class, jobj.getString("campaignstatusid")));
            }

            if (jobj.has("userid")) {
                userid = jobj.getString("userid");
                crmCampaign.setUsersByUpdatedbyid((User) get(User.class, userid));

            }
            if (jobj.has("startdate")) {
                crmCampaign.setStartingdate(jobj.getLong("startdate"));
            }
            if (jobj.has("enddate")) {
                crmCampaign.setEndingdate(jobj.getLong("enddate"));
            }
            if (jobj.has("expectedresponse")) {
                crmCampaign.setExpectedresponse(jobj.getString("expectedresponse"));
            }
            if (jobj.has("campaignownerid")) {
                crmCampaign.setUsersByUserid((User) get(User.class, jobj.getString("campaignownerid")));
            }
            if (jobj.has("updatedon")) {
                crmCampaign.setUpdatedOn(System.currentTimeMillis());
            }
            if (jobj.has("createdon")){
                crmCampaign.setCreatedOn(jobj.getLong("createdon"));
            }
            if (jobj.has("validflag")) {
                crmCampaign.setValidflag(Integer.parseInt(jobj.getString("validflag")));
            }
            if (jobj.has("deleteflag")) {
                crmCampaign.setDeleteflag(Integer.parseInt(jobj.getString("deleteflag")));
            }
            saveOrUpdate(crmCampaign);

            ll.add(crmCampaign);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.editCampaigns : " + e.getMessage(), e);
        } catch (DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.editCampaigns : " + e.getMessage(), e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.editCampaigns : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getCampaignsForTable(HashMap<String, Object> queryParams, boolean allFlag) throws ServiceException {
        KwlReturnObject kmsg = null;
        try {
            kmsg = getTableData(queryParams, allFlag);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.getCampaignsForTable : " + e.getMessage(), e);
        }
        return kmsg;
    }

    @Override
    public void updateDefaultMasterItemForCampaign(CrmCampaign crmCampaign) throws ServiceException {
        String query = "from CrmCombomaster where comboname = ? ";
        List li = executeQuery(query, "Campaign Source");
        CrmCombomaster crmCombomasterObj;
        if (li.size() > 0) {
            crmCombomasterObj = (CrmCombomaster) li.get(0);
            String comboMasterid = crmCombomasterObj.getMasterid();

            String campaignId = crmCampaign.getCampaignid();
            String campaignName = crmCampaign.getCampaignname();
            CrmCombodata crmCombodataObj = (CrmCombodata) get(CrmCombodata.class, campaignId);
            if (crmCombodataObj == null) {
                crmCombodataObj = new CrmCombodata();
                crmCombodataObj.setValueid(campaignId);
                crmCombodataObj.setCrmCombomaster(crmCombomasterObj);
            }
            crmCombodataObj.setRawvalue(campaignName);
            saveOrUpdate(crmCombodataObj);

            query = " from DefaultMasterItem c where c.crmCombodata.valueid = ?";
            li = executeQuery(query, campaignId);
            DefaultMasterItem defaultMasterItemObj;
            if (li.isEmpty()) {
                defaultMasterItemObj = new DefaultMasterItem();
                defaultMasterItemObj.setID(java.util.UUID.randomUUID().toString());
                defaultMasterItemObj.setCrmCombomaster(crmCombomasterObj);
                defaultMasterItemObj.setCrmCombodata(crmCombodataObj);
                defaultMasterItemObj.setCompany(crmCampaign.getCompany());
                defaultMasterItemObj.setMainID(campaignId);
                defaultMasterItemObj.setIsEdit(1);
            } else {
                defaultMasterItemObj = (DefaultMasterItem) li.get(0);
            }
            defaultMasterItemObj.setValue(campaignName);
            if (crmCampaign.getIsarchive() || crmCampaign.getValidflag() == 0) {
                defaultMasterItemObj.setValidflag(0);
            } else {
                defaultMasterItemObj.setValidflag(1);
            }
            saveOrUpdate(defaultMasterItemObj);
        }
    }

    @Override
    public KwlReturnObject getCampaignLog(HashMap<String, Object> requestParams) {
        boolean success = false;
        List lst = null;
        int count = 0;
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = "from CampaignLog ";
            if (requestParams.get("filter_names") != null && requestParams.get("filter_values") != null) {
                name = new ArrayList((List<String>) requestParams.get("filter_names"));
                value = new ArrayList((List<Object>) requestParams.get("filter_values"));
                hql += com.krawler.common.util.StringUtil.filterQuery(name, "where");
                int ind = hql.indexOf("(");
                if (ind > -1) {
                    int index = Integer.valueOf(hql.substring(ind + 1, ind + 2));
                    hql = hql.replaceAll("(" + index + ")", value.get(index).toString());
                    value.remove(index);
                }
            }

            if (requestParams.get("searchcol") != null && requestParams.get("ss") != null) {
                searchCol = (String[]) requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if (requestParams.get("group_by") != null) {
                orderby = new ArrayList((List<String>) requestParams.get("group_by"));
                hql += com.krawler.common.util.StringUtil.groupQuery(orderby);
            }

            if (requestParams.get("order_by") != null && requestParams.get("order_type") != null) {
                orderby = new ArrayList((List<String>) requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>) requestParams.get("order_type"));
                hql += com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }

            if (requestParams.get("select") != null) {
                String selectstr = requestParams.get("select").toString() + " ";
                hql = selectstr + hql;
            }

            lst = executeQuery(hql, value.toArray());
            count = lst.size();
            if ("false".equals(requestParams.get("allflag"))) {
                lst = executeQueryPaging(hql, value.toArray(), new Integer[]{Integer.parseInt(requestParams.get("start").toString()), Integer.parseInt(requestParams.get("limit").toString())});
            }

            success = true;
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            success = false;
        } finally {
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }

    @Override
    public KwlReturnObject getDetailPanelRecentCampaign(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String recid = requestParams.get("recid").toString();
            String tzdiff = requestParams.get("tzdiff").toString();
            String ClHql = "select (activitydate -mod((activitydate ,86400000) + "+tzdiff+") as adate,count(activitydate),sum(viewed),sum(sendingfailed),id,cl.campaignid"
                    + " from campaign_log cl where cl.campaignid = ?  group by adate";
            ll = executeNativeQuery(ClHql, new Object[]{recid});
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmActivityDAOImpl.getAccountActivity : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public List<CrmCampaign> getCampaigns(List<String> recordIds)
    {
        if (recordIds == null || recordIds.isEmpty())
        {
            return null;
        }
        StringBuilder hql = new StringBuilder("from CrmCampaign where campaignid in (");
        
        for (String record: recordIds)
        {
            hql.append("'" + record + "',");
        }
        
        hql.deleteCharAt(hql.length() - 1);
        hql.append(")");

        return executeQuery(hql.toString());
    }
//TODO (Kuldeep Singh) : Please test it Porperly for all fields while using this function for Mass update
// Now it is working fine for delete.
    @Override
    public KwlReturnObject updateMassCampaigns(JSONObject jobj) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        String hqlVarPart = "";
        List<Object> params = new ArrayList<Object>();
        try {
            String userid = null;
            
            CrmCampaign crmCampaign = null;

            String[] campaignids = (String[])jobj.get("campaignid");
           
            if(jobj.has("companyid")) {
            	hqlVarPart += " company = ?,";
            	params.add(get(Company.class, jobj.getString("companyid")));
            }
            if (jobj.has("campaignname")) {
                hqlVarPart += " campaignname = ?,";
            	params.add(jobj.getString("campaignname"));
            }
            if (jobj.has("objective")) {
                hqlVarPart += " objective = ?,";
            	params.add(jobj.getString("objective"));
            }
            if (jobj.has("campaigntypeid")) {
                hqlVarPart += " crmCombodataByCampaigntypeid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("campaigntypeid")));
            }
            if (jobj.has("campaignstatusid")) {
                hqlVarPart += " crmCombodataByCampaignstatusid = ?,";
            	params.add(get(DefaultMasterItem.class, jobj.getString("campaignstatusid")));
            }
            if (jobj.has("startdate")) {
                hqlVarPart += " startdate = ?,";
            	params.add(jobj.getLong("startdate"));
            }
            if (jobj.has("enddate")) {
                hqlVarPart += " enddate = ?,";
            	params.add(jobj.getLong("enddate"));
            }
            if (jobj.has("userid")) {
                userid = jobj.getString("userid");
                hqlVarPart += " usersByUpdatedbyid = ?,";
            	params.add(get(User.class, userid));
            }
            if (jobj.has("startdate")) {
                hqlVarPart += " startingdate = ?,";
            	params.add(jobj.getLong("startdate"));
            }
            if (jobj.has("enddate")) {
                hqlVarPart += " endingdate = ?,";
            	params.add(jobj.getLong("enddate"));
            }
            if (jobj.has("expectedresponse")) {
                hqlVarPart += " expectedresponse = ?,";
            	params.add(jobj.getString("expectedresponse"));
            }
            if (jobj.has("campaignownerid")) {
                hqlVarPart += " usersByUserid = ?,";
            	params.add(get(User.class, jobj.getString("campaignownerid")));
            }
            if (jobj.has("updatedon")) {
                hqlVarPart += " updatedOn = ?,";
            	params.add(new Date().getTime());
            }
            if (jobj.has("createdon")) {
            	Long createdOn = jobj.getLong("createdon");
	            		hqlVarPart += " createdOn = ?,";
	                	params.add(createdOn);
            }
            if(jobj.has("validflag")) {
                hqlVarPart += " validflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("validflag")));
            }
            if(jobj.has("deleteflag")) {
                hqlVarPart += " deleteflag = ?,";
            	params.add(Integer.parseInt(jobj.getString("deleteflag")));
            }

            hqlVarPart = hqlVarPart.substring(0, Math.max(0,hqlVarPart.lastIndexOf(',')));
            String hql = "update CrmCampaign set "+hqlVarPart+" where campaignid in (:campaignids)";
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("campaignids", campaignids);
            executeUpdate(hql, params.toArray(), map);
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.editCampaigns : " + e.getMessage(), e);
        } catch (DataAccessException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.editCampaigns : " + e.getMessage(), e);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmCampaignDAOImpl.editCampaigns : " + e.getMessage(), e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

}
