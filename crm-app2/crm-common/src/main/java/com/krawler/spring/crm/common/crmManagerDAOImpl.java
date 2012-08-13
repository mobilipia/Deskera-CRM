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
import com.krawler.common.admin.Assignmanager;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.FieldComboData;
import com.krawler.common.admin.KWLCurrency;
import com.krawler.common.admin.KWLDateFormat;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CompanyPreferences;
import com.krawler.crm.database.tables.CrmCombomaster;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.crm.database.tables.CrmCombodata;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import com.krawler.crm.utils.Constants;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.util.BuildCriteria;
import com.krawler.crm.database.tables.LeadRoutingUsers;
import com.krawler.dao.BaseDAO;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.criterion.DetachedCriteria;
public class crmManagerDAOImpl extends BaseDAO implements crmManagerDAO {
    
    private fieldManagerDAO fieldManagerDAOobj;
    private sessionHandlerImpl sessionHandlerImpl;
	
    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }
    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImpl = sessionHandlerImplObj;
    }
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.crmManagerDAO#getComboData(java.lang.String, java.util.HashMap)
     */
    public List getComboData(String comboname, HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        try {
            String Hql ;
            String companyid = "";
            Hql = CrmCommonConstants.Crm_getComboData_Hql1;
            ArrayList filter_names = (ArrayList) requestParams.get(Constants.filter_names);
            ArrayList filter_params = (ArrayList) requestParams.get(Constants.filter_params);
            ArrayList order_by = new ArrayList();
            ArrayList order_type = new ArrayList();
            if(requestParams.containsKey(Constants.order_by))
                order_by =(ArrayList) requestParams.get(Constants.order_by);
            if(requestParams.containsKey(Constants.order_type))
                order_type = (ArrayList) requestParams.get(Constants.order_type);
            if(requestParams.containsKey(Constants.companyid) && requestParams.get(Constants.companyid)!=null)
                companyid = requestParams.get(Constants.companyid).toString();
            
            Hql += StringUtil.filterQuery(filter_names, Constants.where);
            filter_params.add(comboname);
            if(comboname.equalsIgnoreCase(Constants.Lead_Source)) {
                Hql += CrmCommonConstants.Crm_getComboData_Hql2;
                if(requestParams.containsKey(Constants.userlist_value) && requestParams.get(Constants.userlist_value)!=null) {
                     Hql += " and c.usersByUserid.userID in (" + requestParams.get(Constants.userlist_value) + ")";
                }
                Hql = Hql + ")))";
                
                filter_params.add(Constants.Campaign_Source);
                filter_params.add(companyid);
            }else{
                Hql +=  CrmCommonConstants.Crm_getComboData_Hql3;
            }
            Hql +=  CrmCommonConstants.Crm_getComboData_Hql4;
            String orderQuery = StringUtil.orderQuery(order_by,order_type);
            Hql += orderQuery;
            filter_params.add(0);
            ll = executeQuery(Hql, filter_params.toArray());
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.getComboData", e);
        }
        return ll;
    }

    public KwlReturnObject getComboDataPaging(String comboname, HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql ;
            String companyid = "";
            String quickSearch = requestParams.containsKey("ss") ? requestParams.get("ss").toString() : "";
            Hql = CrmCommonConstants.Crm_getComboData_Hql1;
            ArrayList filter_names = (ArrayList) requestParams.get(Constants.filter_names);
            ArrayList filter_params = (ArrayList) requestParams.get(Constants.filter_params);
            ArrayList order_by = new ArrayList();
            ArrayList order_type = new ArrayList();
            if(requestParams.containsKey(Constants.order_by))
                order_by =(ArrayList) requestParams.get(Constants.order_by);
            if(requestParams.containsKey(Constants.order_type))
                order_type = (ArrayList) requestParams.get(Constants.order_type);
            if(requestParams.containsKey(Constants.companyid) && requestParams.get(Constants.companyid)!=null)
                companyid = requestParams.get(Constants.companyid).toString();

            Hql += StringUtil.filterQuery(filter_names, Constants.where);
            filter_params.add(comboname);
            if(comboname.equalsIgnoreCase(Constants.Lead_Source)) {
                Hql += CrmCommonConstants.Crm_getComboData_Hql2;
                if(requestParams.containsKey(Constants.userlist_value) && requestParams.get(Constants.userlist_value)!=null) {
                     Hql += " and c.usersByUserid.userID in (" + requestParams.get(Constants.userlist_value) + ")";
                }
                Hql = Hql + ")))";

                filter_params.add(Constants.Campaign_Source);
                filter_params.add(companyid);
            }else{
                Hql +=  CrmCommonConstants.Crm_getComboData_Hql3;
            }
            
            if (!StringUtil.isNullOrEmpty(quickSearch)) {
                String[] searchcol = new String[]{"d.value"};
                Hql +=StringUtil.getSearchquery(quickSearch, searchcol,filter_params);
            }
            
            Hql +=  CrmCommonConstants.Crm_getComboData_Hql4;
            String orderQuery = StringUtil.orderQuery(order_by,order_type);
            Hql += orderQuery;
            filter_params.add(0);
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
            if (requestParams.containsKey("start") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString())) {
                int start= Integer.parseInt(requestParams.get("start").toString());
                int limit= Integer.parseInt(requestParams.get("limit").toString());
                ll = executeQueryPaging(Hql, filter_params.toArray(), new Integer[]{start, limit});
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.getComboDataPaging", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.crmManagerDAO#isCrmCampaignArchived(java.util.HashMap)
     */
    public boolean isCrmCampaignArchived(HashMap<String, Object> requestParams) throws ServiceException {
        boolean hasaccess = true;
        List ll = null;
        String companyid = null,campaignid=null;
        try{
            if(requestParams.containsKey(Constants.companyid) && requestParams.get(Constants.companyid)!=null)
                companyid = requestParams.get(Constants.companyid).toString();

            if(requestParams.containsKey(Constants.Crm_campaignid) && requestParams.get(Constants.Crm_campaignid)!=null)
                campaignid = requestParams.get(Constants.Crm_campaignid).toString();

            ArrayList filter_params = new ArrayList();
            String Hql = CrmCommonConstants.Crm_getCrmCampaignData_Hql;
            filter_params.add(companyid);
            filter_params.add(campaignid);
            filter_params.add(true);
            ll = executeQuery( Hql, filter_params.toArray());
            if(ll!=null && ll.size()>0){
                hasaccess = false;
            }
        }catch(Exception e){
            return hasaccess;
        }
        return hasaccess;
    }
    
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.crmManagerDAO#getComboMasterData()
     */
    public KwlReturnObject getComboMasterData() throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql ;
            Hql = "from CrmCombodata";
            ll = executeQuery(Hql);
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.getComboData", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.crmManagerDAO#addMasterData(java.util.HashMap)
     */
    public KwlReturnObject addMasterData(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = new ArrayList();
        String result = "{success:false}";
        try {
            String percentStage = requestParams.containsKey("percentStage")  && requestParams.get("percentStage") != null ? requestParams.get("percentStage").toString() : "";

            String id = UUID.randomUUID().toString();
            DefaultMasterItem defaultMasterItem = new DefaultMasterItem();
            if (requestParams.containsKey("id") && !(StringUtil.isNullOrEmpty(requestParams.get("id").toString())) ) {
                defaultMasterItem.setID(requestParams.get("id").toString());
                id = requestParams.get("id").toString();
            } else {
                defaultMasterItem.setID(id);
            }
            if (requestParams.containsKey("name") && requestParams.get("name") != null) {
                defaultMasterItem.setValue(requestParams.get("name").toString());
            }
            if (requestParams.containsKey("configid") && requestParams.get("configid") != null) {
                defaultMasterItem.setCrmCombomaster((CrmCombomaster) get(CrmCombomaster.class, requestParams.get("configid").toString()));
            }
            if (requestParams.containsKey("comboid") && requestParams.get("comboid") != null) {
                defaultMasterItem.setCrmCombodata((CrmCombodata) get(CrmCombodata.class, requestParams.get("comboid").toString()));
            }
            if (requestParams.containsKey(Constants.companyid) && requestParams.get(Constants.companyid) != null) {
                defaultMasterItem.setCompany((Company) get(Company.class, requestParams.get(Constants.companyid).toString()));
            }
            if (requestParams.containsKey("sequence") && requestParams.get("sequence") != null) {
                defaultMasterItem.setItemsequence(Integer.parseInt(requestParams.get("sequence").toString()));
            }
            if (requestParams.containsKey("mainid") && requestParams.get("mainid") != null) {
                defaultMasterItem.setMainID(requestParams.get("mainid").toString());
            } else {
                defaultMasterItem.setMainID("");
            }
            if (!StringUtil.isNullOrEmpty(percentStage)) {
                defaultMasterItem.setPercentStage(percentStage);
            }
            save(defaultMasterItem);
            result = "{success:true,data:{'id':'"+id+"'}}";
            ll.add(result);
            ll.add(defaultMasterItem);
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.addMasterData", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.crmManagerDAO#editMasterData(java.util.HashMap)
     */
    public KwlReturnObject editMasterData(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = new ArrayList();
        String result = "{success:false}";
        DefaultMasterItem defaultMasterItem = null;
        String oldValue = "";
        try {
            String percentStage = requestParams.containsKey("percentStage") ? requestParams.get("percentStage").toString() : "";

            if (requestParams.containsKey("id") && requestParams.get("id") != null) {
                defaultMasterItem = (DefaultMasterItem) get(DefaultMasterItem.class, requestParams.get("id").toString());
                oldValue = defaultMasterItem.getValue();
                if (requestParams.containsKey("name") && requestParams.get("name") != null) {
                    defaultMasterItem.setValue(requestParams.get("name").toString());
                }
                if (!StringUtil.isNullOrEmpty(percentStage)) {
                    defaultMasterItem.setPercentStage(percentStage);
                }
                saveOrUpdate(defaultMasterItem);
            }
            result = "{success:true,data:{}}";
            ll.add(result);
            ll.add(defaultMasterItem);
            ll.add(oldValue);
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.editMasterData", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.crmManagerDAO#deleteMasterData(java.util.HashMap)
     */
    @Override
    public KwlReturnObject deleteMasterData(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List resultll = new ArrayList();
        DefaultMasterItem dmObj = null;
        String result = "{success:false, msg:'Error while deleting master data.'}";
        String failureResult = "{success:false, msg:'Selected master data can not be deleted as it is already in use.'}";
        try {
            if (requestParams.containsKey("id") && requestParams.get("id") != null) {
                dmObj = (DefaultMasterItem) get(DefaultMasterItem.class, requestParams.get("id").toString());

                if(Constants.mastersData.containsValue(dmObj.getMainID())) {
                    resultll.add("{success:false, msg:'Selected master data can not be deleted.'}");
                    return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
                }
                
                //Check for product
                String Hql = " select count(productid) as count from CrmProduct where deleteflag = 0 " +
                        "and (crmCombodataByCategoryid = ?) ";
                List ll = executeQuery(Hql, new Object[] {dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    resultll.add(failureResult);
                    return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
                }

                //Check for lead
                Hql = " select count(leadid) as count from CrmLead where deleteflag = 0 " +
                        "and (crmCombodataByLeadstatusid = ? or crmCombodataByIndustryid = ? " +
                        " or crmCombodataByRatingid = ?) ";
                ll = executeQuery(Hql, new Object[] {dmObj, dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    resultll.add(failureResult);
                    return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
                }

                //Check for account
                Hql = " select count(accountid) as count from CrmAccount where deleteflag = 0 " +
                        "and (crmCombodataByAccounttypeid = ? or crmCombodataByIndustryid = ?)";
                ll = executeQuery(Hql, new Object[] {dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    resultll.add(failureResult);
                    return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
                }

                //Check for contact
                Hql = " select count(contactid) as count from CrmContact where deleteflag = 0 " +
                        "and (crmCombodataByLeadsourceid = ? or crmCombodataByIndustryid = ? )";
                ll = executeQuery(Hql, new Object[] {dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    resultll.add(failureResult);
                    return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
                }

                //Check for case
                Hql = " select count(caseid) as count from CrmCase where deleteflag = 0 " +
                        "and (crmCombodataByCasetypeid = ? or crmCombodataByCasestatusid = ? or crmCombodataByCasepriorityid = ? )";
                ll = executeQuery(Hql, new Object[] {dmObj, dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    resultll.add(failureResult);
                    return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
                }

                //Check for opportunity
                Hql = " select count(oppid) as count from CrmOpportunity where deleteflag = 0 " +
                        "and (crmCombodataByOppstageid = ? or crmCombodataByOpptypeid = ? or crmCombodataByLeadsourceid = ? or crmCombodataByRegionid = ? )";
                ll = executeQuery(Hql, new Object[] {dmObj, dmObj, dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    resultll.add(failureResult);
                    return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
                }

                //Check for campaign
                Hql = " select count(campaignid) as count from CrmCampaign where deleteflag = 0 " +
                        "and ( crmCombodataByCampaignstatusid = ? or crmCombodataByCampaigntypeid = ? )";
                ll = executeQuery(Hql, new Object[] {dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    resultll.add(failureResult);
                    return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
                }

                //Check for activity
                Hql = " select count(activityid) as count from CrmActivityMaster where deleteflag = 0 " +
                        "and (crmCombodataByPriorityid = ? or crmCombodataByStatusid = ? or crmCombodataByTypeid = ? )";
                ll = executeQuery(Hql, new Object[] {dmObj, dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    resultll.add(failureResult);
                    return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
                }

                //Check for custom fields in product  resolve for case table case. gives error
                
                boolean isConfigIDused = isConfigIdusedAsCustomColumn(requestParams);
                if(isConfigIDused) {
                    resultll.add(failureResult);
                    return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
                }
                dmObj.setDeleteflag(1);
                saveOrUpdate(dmObj);
                result = "{success:true,  msg:'Selected master data has been deleted successfully.'}";
            }
            resultll.add(result);
            resultll.add(dmObj);
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.deleteMasterData", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", resultll, dl);
    }
    
    /**
     * @param requestParams
     * @return
     * @throws ServiceException
     */
    public boolean isConfigIdusedAsCustomColumn(HashMap<String, Object> requestParams) throws ServiceException {
        KwlReturnObject kmsg = null;
        HashMap<String, Object> customrequestParams = new HashMap<String, Object>();
        ArrayList filter_names = new ArrayList();
        ArrayList filter_values = new ArrayList();
        filter_names.add(CrmCommonConstants.Crm_company_companyID);
        filter_values.add(requestParams.get(CrmCommonConstants.Crm_companyid));
        filter_names.add(CrmCommonConstants.Crm_defaultheader_configidD);
        filter_values.add(requestParams.get(CrmCommonConstants.Crm_configid));

        customrequestParams.put(Constants.filter_names, filter_names);
        customrequestParams.put(Constants.filter_params, filter_values);
        kmsg = fieldManagerDAOobj.getColumnHeader(customrequestParams);
        List list = kmsg.getEntityList();
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            ColumnHeader ch = (ColumnHeader) itr.next();
            DefaultHeader dh = ch.getDefaultheader();
            if (dh.isCustomflag()) {
                String table = CrmCommonConstants.Crm_Crm+dh.getModuleName();
                String tablecstmref = table+CrmCommonConstants.Crm_CustomDataobj;
                String dbcolname = tablecstmref +"."+dh.getDbcolumnname().toLowerCase();
                
                
                String hql = "  Select count("+dbcolname+")  from "+table+" where deleteflag=? and company.companyID=? and "+ tablecstmref +" is not null " +
                        " and "+dbcolname +" is not null and "+dbcolname +" like ? ";
                requestParams.put(CrmCommonConstants.Crm_hql, hql);
                requestParams.put(CrmCommonConstants.Crm_deleteflag, 0);
                List ll = fieldManagerDAOobj.getCustomData(requestParams);
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
	public String getDefaultCaseOwner(String companyid) {
		List ll=null;
		String caseowner="";
		String sql="select cc.defaultcaseowner from company_caseowner as cc inner join users as us on cc.defaultcaseowner = us.userid where cc.companyid=?";
		ll=executeNativeQuery(sql, new Object[] {companyid});
		if(!ll.isEmpty()){
				caseowner=(String)ll.get(0);
		}
		return caseowner;
	}
    
    @Override
    public void saveDefaultCaseOwner(String companyid, String ownerid){
    	ArrayList<String> params= new ArrayList<String>();
    	String caseowner="";
    	List ll=null;
    	String query="select defaultcaseowner from company_caseowner where companyid = ?";
    	ll=executeNativeQuery(query, new Object[] {companyid});
    	if(!ll.isEmpty()){
    		query = "update company_caseowner set defaultcaseowner = ? where companyid = ?";
    		params.add(ownerid);
    		params.add(companyid);
    		executeNativeUpdate(query, params.toArray());
    	}else{
    		Company company = (Company) get(Company.class, companyid);
            User user = (User) get(User.class, ownerid);
            try {
				Class cls = Class.forName("com.krawler.crm.database.tables.CustomerCaseDefaultOwner");
				Object obj = cls.newInstance();
				BeanUtils.setProperty(obj, "company", company);
				BeanUtils.setProperty(obj, "user", user);
				save(obj);
			} catch (Exception e) {
				logger.warn("Cannot Set Default case owner for company: "+company.getSubDomain());
			}
            
    		
       }
    }
    
       
	@Override
	public void saveMasterDataSequence(String id, int seq, String customflag) {
			if(customflag.equalsIgnoreCase("0")){
				DefaultMasterItem dmi = (DefaultMasterItem) get(DefaultMasterItem.class, id);
				dmi.setItemsequence(seq);
				saveOrUpdate(dmi);
			}
			if(customflag.equalsIgnoreCase("1")){
				FieldComboData fcd=(FieldComboData) get(FieldComboData.class,id);
				fcd.setItemsequence(seq);				
			}
	}
     
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.crmManagerDAO#getMaster(java.util.HashMap)
     */
    public KwlReturnObject getMaster(HashMap<String, Object> requestParams) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            ArrayList filter_params = new ArrayList();
            ArrayList hqlfilter_names = new ArrayList();
            hqlfilter_names.add("NOTINcomboname");
            String hql="from CrmCombomaster cm";
            ArrayList hqlfilter_params = new ArrayList();
            String filtercombos=Constants.MASTERCONFIG_HIDECOMBO;
            if(!requestParams.containsKey("allowLeadType")){
                filtercombos +=",'Lead Type'";
            } 

            hqlfilter_params.add(filtercombos);
            String hqlfilterQuery = StringUtil.filterQuery(hqlfilter_names, "where");
            int ind = hqlfilterQuery.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(hqlfilterQuery.substring(ind+1,ind+2));
                hqlfilterQuery = hqlfilterQuery.replaceAll("("+index+")", hqlfilter_params.get(index).toString());
                hqlfilter_params.remove(index);
            }
            hql += hqlfilterQuery;
            if(requestParams.containsKey(Constants.filter_params)) {
                ArrayList  filter_params1 = (ArrayList) requestParams.get(Constants.filter_params);
                filter_params.addAll(filter_params1);
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "and");
                hql += filterQuery;
            }
            hql += " order by comboname";
            ll=executeQuery(hql,filter_params.toArray());
            dl = ll.size();
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManager.getMaster", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject getMasterIDCompany(String companyid, String mainID) throws ServiceException {
        List ll = null;
        try {
            String Hql = " from DefaultMasterItem c where c.company.companyID= ? and c.mainID= ? ";
            ll = executeQuery(Hql, new Object[]{companyid, mainID});
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.getMasterIDCompany", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, ll.size());
    }
    
    public String preferenceDate(HttpServletRequest request, Date date, int timeflag) throws ServiceException {
        String result = "";
        try {
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            String timeZoneDiff = sessionHandlerImpl.getTimeZoneDifference(request);
            KWLDateFormat dateFormat = (KWLDateFormat) get(KWLDateFormat.class, sessionHandlerImpl.getDateFormatID(request));
            String prefDate = "";
            if (timeflag == 0) {// 0 - No time only Date
                int spPoint = dateFormat.getJavaSeperatorPosition();
                prefDate = dateFormat.getJavaForm().substring(0, spPoint);
            } else // DateTime
            {
                prefDate = dateFormat.getJavaForm();
            }
            if (date != null) {
                result = authHandler.getPrefDateFormatter(timeFormatId, timeZoneDiff, prefDate).format(date);
            } else {
                return result;
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.preferenceDate", e);
        }
        return result;
    }

    public String userPreferenceDate(HttpServletRequest request, Date date, int timeflag) throws ServiceException {
        String result = "";
        try {
            String timeFormatId = sessionHandlerImpl.getUserTimeFormat(request);
            KWLDateFormat dateFormat = (KWLDateFormat) get(KWLDateFormat.class, sessionHandlerImpl.getDateFormatID(request));
            String prefDate = "";
            if (timeflag == 0) {// 0 - No time only Date
                int spPoint = dateFormat.getJavaSeperatorPosition();
                prefDate = dateFormat.getJavaForm().substring(0, spPoint);
            } else // DateTime
            {
                prefDate = dateFormat.getJavaForm();
            }
            if (date != null) {
                result = authHandler.getUserPrefDateFormatter( timeFormatId,  prefDate).format(date);
            } else {
                return result;
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.userPreferenceDate", e);
        }
        return result;
    }


    public String preferenceDatejsformat(String timeZoneDiff, Date date, DateFormat sdf) throws ServiceException {
        String result = "";
        try {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));
            String prefDate = "";
            if (date != null) {
                result = sdf.format(date);
            } else {
                return result;
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.preferenceDate", e);
        }
        return result;
    }

    public String preferenceDatejsformat(Date date, DateFormat sdf) throws ServiceException {
        String result = "";
        try {
            String prefDate = "";
            if (date != null) {
                result = sdf.format(date);
            } else {
                return result;
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.preferenceDate", e);
        }
        return result;
    }

    /**
     * To convert a date and time selected separately by user into corresponding combined datetime
     * from users selected timezone to systems timezone
     *
     * The first step is to keep track of the time difference in order to change the date if required.
     * Two time only objects dtold and dtcmp are created for this purpose.
     *
     * The date passed and the time passed that are in system timezone are formatted without
     * timezone and then parsed into the required timezone and then the time values are set
     * back to the date value sent.
     *
     **/
    public Date converttz(String timeZoneDiff, Date dt, String time) {
        Calendar cal = Calendar.getInstance();
        try {
            if (timeZoneDiff == null || timeZoneDiff.isEmpty()) {
                timeZoneDiff = "-7:00";
            }
            String val;
            SimpleDateFormat sdf = new SimpleDateFormat("HHmm 'Hrs'");
            Date dtold = sdf.parse("0000 Hrs");
            if(!time.endsWith("Hrs"))
            {
                sdf = new SimpleDateFormat("hh:mm a");
                dtold = sdf.parse("00:00 AM");
            }
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            sdf2.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));                // Setting the timezone passed

            Date dt1 = sdf.parse(time);                                                // Setting the passed time to the date object in system timezone

            sdf.setTimeZone(TimeZone.getTimeZone("GMT" + timeZoneDiff));                 // Setting the timezone passed
            Date dtcmp = sdf.parse(time);                                              // Parsing the time to timezone using passed values
            dt1.setMonth(dt.getMonth());                                               // Setting the date values sent to the system time only value
            dt1.setDate(dt.getDate());
            dt1.setYear(dt.getYear());
            dt1 = sdf2.parse(sdf1.format(dt1));                                        // Parsing datetime into required timezone
            dt.setHours(dt1.getHours());                                               // Setting the time values into the sent date
            dt.setMinutes(dt1.getMinutes());
            dt.setSeconds(0);
            cal.setTime(dt);
            if (dtcmp.compareTo(dtold) < 0) {                                          // Comparing for time value change
                cal.add(Calendar.DATE, -1);                                            //  in order to change the date accordingly
            }
            dtold.setDate(2);
            if (dtcmp.compareTo(dtold) > 0 || dtcmp.compareTo(dtold) == 0 ) {
                cal.add(Calendar.DATE, 1);
            }


        } catch (ParseException ex) {
            System.out.println(ex);
        } finally {
            return cal.getTime();
        }
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.crmManagerDAO#currencyRender(java.lang.String, java.lang.String)
     */
    public String currencyRender(String currency, String currencyid) throws ServiceException {
        try {
            if (!StringUtil.isNullOrEmpty(currency)) {

                String symbol = currencySymbol(currencyid);

                double v = 0;
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                if (currency.equals("")) {
                    return symbol;
                }
                currency = currency.replaceAll("([^\\d\\.\\-])", "");
                v = Double.parseDouble(currency);
                String fmt = decimalFormat.format(v);
                fmt = symbol + " " + fmt;
                return fmt;
            } else {
                return "";
            }
        } catch (NumberFormatException e) {
            return "";
        }
    }

    @Override
    public String currencySymbol(String currencyid) throws ServiceException {
        try {
            
            String symbol = "";
            KWLCurrency cur = (KWLCurrency) get(KWLCurrency.class, currencyid);
                if(!currencyid.equals("5")) {
                    symbol = cur.getHtmlcode();
                    char temp = (char) Integer.parseInt(symbol, 16);
                    symbol = Character.toString(temp);
                } else {
                    symbol = "Rs";
                }

            return symbol;
            
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param manID
     * @param appendUser
     * @param appendList
     * @param exceptionAt
     * @param extraQuery
     * @throws ServiceException
     */
    public void recursiveUsers(String manID, StringBuffer appendUser, List appendList, int exceptionAt, String extraQuery) throws ServiceException {
        try {
            /* This method is also called from remoteapi.java */
            String getUsers = "from Assignmanager where assignman.userID = ? " + extraQuery;
            List ll = executeQuery(getUsers, new Object[]{manID});
            appendList.addAll(ll);
            exceptionAt++;
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Assignmanager am = (Assignmanager) ite.next();
                appendUser.append("'" + am.getAssignemp().getUserID() + "',");
                recursiveUsers(am.getAssignemp().getUserID(), appendUser, appendList, exceptionAt, extraQuery);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.recursiveUsers Loop no-" + exceptionAt, e);
        } finally {
        }
    }
    
    public void recursiveUserIds(String manID, StringBuffer appendUser, List appendList, int exceptionAt, String extraQuery) throws ServiceException {
        try {
            /* This method is also called from remoteapi.java */
            String getUsers = "from Assignmanager where assignman.userID = ? " + extraQuery;
            List ll = executeQuery(getUsers, new Object[]{manID});
            appendList.addAll(ll);
            exceptionAt++;
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Assignmanager am = (Assignmanager) ite.next();
                appendUser.append(am.getAssignemp().getUserId() + ",");
                recursiveUserIds(am.getAssignemp().getUserID(), appendUser, appendList, exceptionAt, extraQuery);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.recursiveUsers Loop no-" + exceptionAt, e);
        } finally {
        }
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.crmManagerDAO#recursiveUsers(java.lang.String)
     */
    public StringBuffer recursiveUsers(String userid) throws ServiceException {
        StringBuffer usersList = new StringBuffer();
        try {
            List appendList = new ArrayList();
            recursiveUsers(userid, usersList, appendList, 0, "");
            usersList.append("'" + userid + "'");
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.recursiveUsers", e);
        }
        return usersList;
    }
    
    public StringBuffer recursiveUserIds(String userid) throws ServiceException {
        StringBuffer usersList = new StringBuffer();
        try {
            List appendList = new ArrayList();
            recursiveUserIds(userid, usersList, appendList, 0, "");
            
            User user = (User) get(User.class, userid);
            
            usersList.append(user.getUserId());
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.recursiveUsers", e);
        }
        return usersList;
    }

    public StringBuffer recursiveManagerUsers(String userid) throws ServiceException {
        StringBuffer usersList = new StringBuffer();
        try {
            List appendList = new ArrayList();
            List appendUserList = new ArrayList();
            recursiveManagerUsers(userid, usersList, appendUserList, appendList, 0, "");
            if(usersList.length()>0)
                usersList = usersList.deleteCharAt(usersList.lastIndexOf(","));
//            usersList.append("'" + userid + "'");
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.recursiveManagerUsers", e);
        }
        return usersList;
    }

    @Override
    public void recursiveManagerUsers(String empID, StringBuffer appendUser,List appendUserList, List appendList, int exceptionAt, String extraQuery) throws ServiceException {
        try {
            /* This method is also called from remoteapi.java */
            String getUsers = "from Assignmanager where assignemp.userID = ? " + extraQuery;
            List ll = executeQuery(getUsers, new Object[]{empID});
            appendList.addAll(ll);
            exceptionAt++;
            Iterator ite = ll.iterator();
            while (ite.hasNext()) {
                Assignmanager am = (Assignmanager) ite.next();
                String manUserId = am.getAssignman().getUserID();
                appendUser.append("'" + manUserId + "',");
                appendUserList.add(manUserId);
                recursiveManagerUsers(am.getAssignman().getUserID(), appendUser, appendUserList, appendList, exceptionAt, extraQuery);
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.recursiveManagerUsers Loop no-" + exceptionAt, e);
        } finally {
        }
    }
    //Company Preference
    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.crmManagerDAO#setCompanyPref(java.util.HashMap)
     */
    public KwlReturnObject setCompanyPref(HashMap hm) throws ServiceException {
        List ll = new ArrayList();
        int dl = 0;
        String auditMsg = "";
        try {
            CompanyPreferences cmpPref = null;
            Company company = null;
            String cmpid = null;
            if (hm.containsKey(Constants.companyid) && hm.get(Constants.companyid) != null) {
                cmpid = hm.get(Constants.companyid).toString();
                company = (Company) get(Company.class, cmpid);

                cmpPref = (CompanyPreferences) get(CompanyPreferences.class, cmpid);
                if (cmpPref == null) {
                    cmpPref = new CompanyPreferences();
                    cmpPref.setCompany(company);
                }
            }
            if (hm.get("category").toString().equalsIgnoreCase("upper")) {
                
                boolean prevValue = cmpPref.isCampaign()?cmpPref.isCampaign():false;
                if (!hm.containsKey("heirarchypermisssioncampaign")) {
                    cmpPref.setCampaign(false);
                    auditMsg += (prevValue?"'Campaign'(enabled),":"");
                } else {
                    cmpPref.setCampaign(true);
                    auditMsg += (prevValue?"":"'Campaign'(disabled),");
                }
                
                prevValue = cmpPref.isLead()?cmpPref.isLead():false;
                if (!hm.containsKey("heirarchypermisssionleads")) {
                    cmpPref.setLead(false);
                    auditMsg += (prevValue?"'Lead'(enabled),":"");
                } else {
                    cmpPref.setLead(true);
                    auditMsg += (prevValue?"":"'Lead'(disabled),");
                }
                
                prevValue = cmpPref.isAccount()?cmpPref.isAccount():false;
                if (!hm.containsKey("heirarchypermisssionaccounts")) {
                    cmpPref.setAccount(false);
                    auditMsg += (prevValue?"'Account'(enabled),":"");
                } else {
                    cmpPref.setAccount(true);
                    auditMsg += (prevValue?"":"'Account'(disabled),");
                }
                
                prevValue = cmpPref.isContact()?cmpPref.isContact():false;
                if (!hm.containsKey("heirarchypermisssioncontacts")) {
                    cmpPref.setContact(false);
                    auditMsg += (prevValue?"'Contact'(enabled),":"");
                } else {
                    cmpPref.setContact(true);
                    auditMsg += (prevValue?"":"'Contact'(disabled),");
                }
                
                prevValue = cmpPref.isOpportunity()?cmpPref.isOpportunity():false;
                if (!hm.containsKey("heirarchypermisssionopportunity")) {
                    cmpPref.setOpportunity(false);
                    auditMsg += (prevValue?"'Opportunity'(enabled),":"");
                } else {
                    cmpPref.setOpportunity(true);
                    auditMsg += (prevValue?"":"'Opportunity'(disabled),");
                }
                
                prevValue = cmpPref.isCases()?cmpPref.isCases():false;
                if (!hm.containsKey("heirarchypermisssioncases")) {
                    cmpPref.setCases(false);
                    auditMsg += (prevValue?"'Case'(enabled),":"");
                } else {
                    cmpPref.setCases(true);
                    auditMsg += (prevValue?"":"'Case'(disabled),");
                }
                
                prevValue = cmpPref.isProduct()?cmpPref.isProduct():false;
                if (!hm.containsKey("heirarchypermisssionproduct")) {
                    cmpPref.setProduct(false);
                    auditMsg += (prevValue?"'Product'(enabled),":"");
                } else {
                    cmpPref.setProduct(true);
                    auditMsg += (prevValue?"":"'Product'(disabled),");
                }
                
                prevValue = cmpPref.isActivity()?cmpPref.isActivity():false;
                if (!hm.containsKey("heirarchypermisssionactivity")) {
                    cmpPref.setActivity(false);
                    auditMsg += (prevValue?"'Activity'(enabled),":"");
                } else {
                    cmpPref.setActivity(true);
                    auditMsg += (prevValue?"":"'Activity'(disabled),");
                }
                
                if(!StringUtil.isNullOrEmpty(auditMsg)) {
                    auditMsg = "Hierarchy Permission has been modified for " + auditMsg.substring(0, auditMsg.length()-1);
                }
                
                if (!hm.containsKey("convertedleadeditpermisssion")) {
                    cmpPref.setEditconvertedlead(false);
                } else {
                    cmpPref.setEditconvertedlead(true);
                }
            } else if (hm.get("category").toString().equalsIgnoreCase("notificationtype")) {
                int emailnotification = (Integer)hm.get("emailnotification");
                if(company.getNotificationtype()!=emailnotification){
                    auditMsg = emailnotification == 0?"Email notification has been disabled for the company.":"Email notification has been enabled for the company.";
                }
                company.setNotificationtype(emailnotification);
                saveOrUpdate(company);
            } else if(hm.containsKey("leadrounting")) {
                int leadroutingoption = (Integer)hm.get("leadrounting");
                cmpPref.setLeadrouting(leadroutingoption);
            } else {
                if (hm.containsKey("companydependentLeadTyperadio")) {
                    boolean leaddtype = Boolean.parseBoolean(hm.get("companydependentLeadTyperadio").toString());
                    if(cmpPref.isDefaultleadtype()!=leaddtype) {
                        auditMsg = leaddtype?"Default Lead Type 'Individual' has been set for the company.":"Default Lead Type 'Company' has been set for the company.";
                    }
                    cmpPref.setDefaultleadtype(leaddtype);
                }
            }
            saveOrUpdate(cmpPref);
            
            ll.add(cmpPref);
            ll.add(auditMsg);
        } catch (Exception e) {
            throw ServiceException.FAILURE("companyDetailsDAOImpl.setCompanyPref", e);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
   public Boolean checkMasterDataisUsed(String mainId, String id, String configid, String companyid)throws ServiceException  {
        List li = null,ll=new ArrayList();
        ArrayList filter_names = new ArrayList();
        ArrayList filter_values = new ArrayList();
        boolean isRecordused = false;
        DetachedCriteria crit = DetachedCriteria.forClass(DefaultMasterItem.class, "c");
        filter_names.add("mainID");
        filter_values.add(mainId);
        filter_names.add("company.companyID");
        filter_values.add(companyid);
       crit = BuildCriteria.filterQuery(crit, filter_names, filter_values, "and");
        li = findByCriteria(crit);
        if (li.size() > 0) {
            for (Object ditem : li) {
                DefaultMasterItem dmObj = (DefaultMasterItem) ditem;
                ll.add(dmObj.getValue());
                isRecordused = checkMasterDataisUsed(dmObj.getID(), configid, companyid);
                return isRecordused;
            }
        }
         return isRecordused;
    }
    public Boolean checkMasterDataisUsed(String id,String configid,String companyid) throws ServiceException {
        try {
            if (id != null) {
                DefaultMasterItem dmObj = (DefaultMasterItem) get(DefaultMasterItem.class, id);

                if(Constants.mastersData.containsValue(dmObj.getMainID())) {
                    return true;
                }

                //Check for product
                String Hql = " select count(productid) as count from CrmProduct where deleteflag = 0 " +
                        "and (crmCombodataByCategoryid = ?) ";
                List ll = executeQuery( Hql, new Object[] {dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    return true;
                }

                //Check for lead
                Hql = " select count(leadid) as count from CrmLead where deleteflag = 0 " +
                        "and (crmCombodataByLeadsourceid = ?  or crmCombodataByLeadstatusid = ? or crmCombodataByIndustryid = ? " +
                        " or crmCombodataByRatingid = ? ) ";
                ll = executeQuery( Hql, new Object[] {dmObj,dmObj, dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    return true;
                }

                //Check for account
                Hql = " select count(accountid) as count from CrmAccount where deleteflag = 0 " +
                        "and (crmCombodataByAccounttypeid = ? or crmCombodataByIndustryid = ?)";
                ll = executeQuery( Hql, new Object[] {dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    return true;
                }

                //Check for contact
                Hql = " select count(contactid) as count from CrmContact where deleteflag = 0 " +
                        "and (crmCombodataByLeadsourceid = ? or crmCombodataByIndustryid = ? )";
                ll = executeQuery( Hql, new Object[] {dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    return true;
                }

                //Check for case
                Hql = " select count(caseid) as count from CrmCase where deleteflag = 0 " +
                        "and (crmCombodataByCasetypeid = ? or crmCombodataByCasestatusid = ? or crmCombodataByCasepriorityid = ?)";
                ll = executeQuery( Hql, new Object[] {dmObj, dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    return true;
                }

                //Check for opportunity
                Hql = " select count(oppid) as count from CrmOpportunity where deleteflag = 0 " +
                        "and (crmCombodataByOppstageid = ? or crmCombodataByOpptypeid = ? or crmCombodataByLeadsourceid = ? or crmCombodataByRegionid = ? )";
                ll = executeQuery( Hql, new Object[] {dmObj, dmObj, dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                   return true;
                }

                //Check for campaign
                Hql = " select count(campaignid) as count from CrmCampaign where deleteflag = 0 " +
                        "and ( crmCombodataByCampaignstatusid = ? or crmCombodataByCampaigntypeid = ? )";
                ll = executeQuery( Hql, new Object[] {dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                   return true;
                }

                //Check for activity
                Hql = " select count(activityid) as count from CrmActivityMaster where deleteflag = 0 " +
                        "and (crmCombodataByPriorityid = ? or crmCombodataByStatusid = ? or crmCombodataByTypeid = ? )";
                ll = executeQuery( Hql, new Object[] {dmObj, dmObj, dmObj});
                if(Integer.parseInt(ll.get(0).toString()) > 0) {
                    return true;
                }

                //Check for custom fields in product  resolve for case table case. gives error
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put(CrmCommonConstants.Crm_id, id);
                requestParams.put(CrmCommonConstants.Crm_configid, configid);
                requestParams.put(CrmCommonConstants.Crm_companyid, companyid);
                boolean isConfigIDused = isConfigIdusedAsCustomColumn(requestParams);
                if(isConfigIDused) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.deleteMasterData", e);
        }
        return false;
    }

    @Override
    public String getComboName(String configid) throws ServiceException {
        String result = "";
        List ll = null;
        try {
            String Hql = " select comboname from CrmCombomaster c where c.masterid= ? ";
            ll = executeQuery(Hql, new Object[]{configid});
            if(ll.size() >0){
                result = (String) ll.get(0);
            }
                
        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.getMasterIDCompany", e);
        }
        return result;
    }

    @Override
    public Boolean checkForDuplicateEntryInMasterData(String name, String configid, String companyid, String id)throws ServiceException  {
        List ll=new ArrayList();
        boolean chkForDuplicateEntryInMasterData = false;
        try {
            String Hql = " select value from DefaultMasterItem c where STRCMP(c.value, '"+name+"' )=0  and c.ID not in ('"+id+"') and c.deleteflag= ? and c.crmCombomaster.masterid=? and c.company.companyID=? ";
            ll = executeQuery(Hql, new Object[]{ 0, configid, companyid});
            if(ll.size() >0){
                chkForDuplicateEntryInMasterData = true;
            }

        } catch (Exception e) {
            throw ServiceException.FAILURE("crmManagerDAOImpl.checkForDuplicateEntryInMasterData", e);
        }
        return chkForDuplicateEntryInMasterData;
    }

    @Override
    public KwlReturnObject getUnAssignedLeadRoutingUsers(String companyid, HashMap<String, Object> requestParams) throws ServiceException {
        String selCountQuery = "select count(distinct u.userID) ";
        StringBuilder hql = new StringBuilder("from User u where u.userID not in (select user.userID from LeadRoutingUsers where user.company.companyID = ?) and u.deleteflag=0 and " +
                " company.companyID = ? ");
        String orderBy = " order by u.firstName,u.lastName";
        ArrayList filter_params = new ArrayList();
        filter_params.add(companyid);
        filter_params.add(companyid);
        String filterQuery = "";
        if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
            String ss=requestParams.get("ss").toString();
            if(!StringUtil.isNullOrEmpty(ss)){
                String[] searchcol = new String[]{"u.lastName","u.firstName"};
                StringUtil.insertParamSearchString(filter_params, ss, searchcol.length);
                filterQuery = StringUtil.getSearchString(ss, "and", searchcol);
            }
        }
        String countQuery = selCountQuery + hql + filterQuery;
        List ll = executeQuery(countQuery, filter_params.toArray());
        Long dl = 0l;
        if (ll != null && !ll.isEmpty())
        {
            dl = (Long) ll.get(0);
        }
        if(requestParams.containsKey("pagingFlag") && Boolean.TRUE.equals(requestParams.get("pagingFlag"))) {
            int start = 0;
            int limit = 15;
            boolean ispaging = requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()) && !StringUtil.isNullOrEmpty(requestParams.get("start").toString());
            if(ispaging) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            ll = executeQueryPaging(hql + filterQuery + orderBy, filter_params.toArray(), new Integer[]{start, limit});
        } else {
            ll = executeQuery(hql + filterQuery + orderBy, filter_params.toArray());
        }
        
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl.intValue());
    }

    @Override
    public KwlReturnObject getAssignedLeadRoutingUsers(String companyid, HashMap<String, Object> requestParams) throws ServiceException {
        ArrayList filter_params = new ArrayList();
        String selCountQuery = "select count(distinct u.userID) ";
//        String orderBy = " order by u.firstName,u.lastName";
        StringBuilder hql = new StringBuilder("from User u where u.userID in (select user.userID from LeadRoutingUsers where user.company.companyID = ? order by ordernum) and u.deleteflag=0 ");
        filter_params.add(companyid);
        String filterQuery = "";
        if(requestParams.containsKey("ss") && requestParams.get("ss") != null) {
            String ss=requestParams.get("ss").toString();
            if(!StringUtil.isNullOrEmpty(ss)){
                String[] searchcol = new String[]{"u.lastName","u.firstName"};
                StringUtil.insertParamSearchString(filter_params, ss, searchcol.length);
                filterQuery = StringUtil.getSearchString(ss, "and", searchcol);
            }
        }
        String countQuery = selCountQuery + hql + filterQuery;
        List ll = executeQuery(countQuery, filter_params.toArray());
        Long dl = 0l;
        if (ll != null && !ll.isEmpty())
        {
            dl = (Long) ll.get(0);
        }
        if(requestParams.containsKey("pagingFlag") && Boolean.TRUE.equals(requestParams.get("pagingFlag"))) {
            int start = 0;
            int limit = 15;
            boolean ispaging = requestParams.containsKey("start") && requestParams.containsKey("limit") && !StringUtil.isNullOrEmpty(requestParams.get("start").toString()) && !StringUtil.isNullOrEmpty(requestParams.get("start").toString());
            if(ispaging) {
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }
            ll = executeQueryPaging(hql + filterQuery, filter_params.toArray(), new Integer[]{start, limit});
        } else {
            ll = executeQuery(hql + filterQuery, filter_params.toArray());
        }

        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl.intValue());
    }

    @Override
    public List<User> getNextLeadRoutingUsers(String companyid) throws ServiceException {
        StringBuilder maxNumhql = new StringBuilder("select max(ordernum) from LeadRoutingUsers where user.company.companyID = ?");
        ArrayList filter_params = new ArrayList();
        filter_params.add(companyid);
        List maxList = executeQuery(maxNumhql.toString(),filter_params.toArray());
        int maxcnt = Integer.parseInt(maxList.get(0).toString());
        
        StringBuilder hql = new StringBuilder("from User where userID in (select user.userID from LeadRoutingUsers " +
                "where ordernum = (select CASE when (ordernum+1) > ? then 0 else (ordernum+1) END from LeadRoutingUsers " +
                "where lastused = ? and user.company.companyID = ?) and user.company.companyID = ?)");
        filter_params.clear();
        filter_params.add(maxcnt);
        filter_params.add(true);
        filter_params.add(companyid);
        filter_params.add(companyid);
        List<User> userList = executeQuery(hql.toString(), filter_params.toArray());
        if(userList.isEmpty()) {
            hql = new StringBuilder("from User where userID in (select user.userID from LeadRoutingUsers " +
                    "where ordernum = 0 and user.company.companyID = ?)");
            filter_params.clear();
            filter_params.add(companyid);
            userList = executeQuery(hql.toString(), filter_params.toArray());
        }
        return userList;
    }

    @Override
    public void deleteLeadRoutingUsers(String companyid) throws ServiceException {
        String hql="delete from LeadRoutingUsers where user.userID in (select userID from User where company.companyID = ?)";
        ArrayList filter_params = new ArrayList();
        filter_params.add(companyid);
        executeUpdate(hql ,filter_params.toArray());
    }

    @Override
    public void setLastUsedFlagForLeadRouting(String userid, String companyid) throws ServiceException {
        
        String hql="UPDATE LeadRoutingUsers set lastused = ? where user.userID in (select userID from User where company = ?)";
        ArrayList filter_params = new ArrayList();
        filter_params.add(false);
        Company company = (Company) get(Company.class, (companyid));
        filter_params.add(company);
        executeUpdate(hql ,filter_params.toArray());

        hql="UPDATE LeadRoutingUsers set lastused = ? where user.userID = ?";
        filter_params.clear();
        filter_params.add(true);
        filter_params.add(userid);
        executeUpdate(hql ,filter_params.toArray());
    }

    public void addLeadRoutingUsers(String[] addList) throws ServiceException {
        try {
            for (int i = 0;i < addList.length;i++) {
                LeadRoutingUsers leadRoutingUsersObj = new LeadRoutingUsers();
                leadRoutingUsersObj.setUserid(addList[i]);
                leadRoutingUsersObj.setOrdernum(i);
                save(leadRoutingUsersObj);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("crmManagerDAOImpl.addLeadRoutingUsers", e);
        }
    }
    
}
