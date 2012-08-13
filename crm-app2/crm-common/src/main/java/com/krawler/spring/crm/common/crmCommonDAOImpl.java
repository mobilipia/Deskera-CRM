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
import com.krawler.common.admin.User;
import java.util.List;
import com.krawler.common.service.ServiceException;
import com.krawler.crm.utils.Constants;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.FieldComboData;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.database.tables.LeadConversionMappings;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.common.KwlReturnObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 *
 * @author Karthik
 */
public class crmCommonDAOImpl extends BaseDAO implements crmCommonDAO {
    private static String MandatoryColumnHeaderQuery =" from ColumnHeader ch where ch.company.companyID = ? and ch.defaultheader.moduleName = ? and ch.mandotory = ?" ;
    private static String MandatoryDefaultHeaderQuery = " from DefaultHeader c where c.mandatory = ? and c.moduleName = ? and c.customflag = ? "
            + " and c.id not in ( select ch.defaultheader.id from ColumnHeader ch where ch.company.companyID = ? and ch.defaultheader.moduleName = ? ) ";
    private static String SELECT = " select ";
    public KwlReturnObject getAssignManagers(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = null;
            String Hql = "from Assignmanager ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "where");
                Hql += filterQuery;
            }
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getAssignManagers", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject deleteNode(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String id = "";
            if (requestParams.containsKey("assignid") && requestParams.get("assignid") != null) {
                id = requestParams.get("assignid").toString();
                Assignmanager am = (Assignmanager) get(Assignmanager.class, id);
                delete(am);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getAssignManagers", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    // Organisation Chart - insert node
    public KwlReturnObject insertNode(HashMap<String, Object> requestParams) throws Exception {
        int dl = 0;
        String result = "";
        List ll = new ArrayList();
        String userid = "";
        String parentid = "";
        Assignmanager am = null;
        try {
            User u = null;
            User p = null;
            String id = "";
            if (requestParams.containsKey("assignid") && requestParams.get("assignid") != null) {
                id = requestParams.get("assignid").toString();
            }
            if (requestParams.containsKey("addFlag") && requestParams.get("addFlag") != null) {
                am = new Assignmanager();
                am.setId(id);
            } else {
                am = (Assignmanager) get(Assignmanager.class, id);
            }
            if (requestParams.containsKey("userid") && requestParams.get("userid").toString() != null) {
                userid = requestParams.get("userid").toString();
                u = (User) get(User.class, userid);
                am.setAssignemp(u);
            }
            if (requestParams.containsKey("fromId") && requestParams.get("fromId").toString() != null) {
                parentid = requestParams.get("fromId").toString();
                p = (User) get(User.class, parentid);
                am.setAssignman(p);
            }
            String Hql = "from Assignmanager where assignemp.userID = ? and assignman.userID = ?";
            ll = executeQuery(Hql, new Object[]{userid,parentid});
            if(ll.size()==0) {
                save(am);
            }
            ll.add(u);
            ll.add(p);
            ll.add(am);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("organizationChartDAOImpl.insertNode", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    // Organisation Chart - update node
    public KwlReturnObject updateNode(HashMap<String, Object> requestParams) throws Exception {
        int dl = 0;
        List ll = null;
        String userid = "",childRole = "";
        String parentid = "", parentRole = "";
        String result = "";
        try {
            if (requestParams.containsKey("nodeid") && requestParams.get("nodeid").toString() != null) {
                userid = requestParams.get("nodeid").toString();
            }
            if (requestParams.containsKey("fromId") && requestParams.get("fromId").toString() != null) {
                parentid = requestParams.get("fromId").toString();
            }
            if (requestParams.containsKey("childRole") && requestParams.get("childRole").toString() != null) {
                childRole = requestParams.get("childRole").toString();
            }
            if (requestParams.containsKey("parentRole") && requestParams.get("parentRole").toString() != null) {
                parentRole = requestParams.get("parentRole").toString();
            }
            User u = (User) get(User.class, userid);
            User p = (User) get(User.class, parentid);
            Assignmanager am = null;
            if (isPerfectRole(parentRole, childRole)) {
                String Hql = "from Assignmanager where assignemp.userID = ? ";
                ll = executeQuery(Hql, new Object[]{userid});
                Iterator ite = ll.iterator();
                while (ite.hasNext()) {
                    am = (Assignmanager) ite.next();
                    am.setAssignman(p);
                    saveOrUpdate(am);
                }
                result = "{success:true}";
            } else {
                result = "{success:false,msg:\"Couldn't assign, parent node has lower role.\"}";
            }

            ll = new ArrayList();
            ll.add(result);
            ll.add(u);
            ll.add(p);
            ll.add(am);
        } catch (ServiceException ex) {
            throw ServiceException.FAILURE("organizationChartDAOImpl.updateNode", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    // Organisation Chart - isperfect role
    public boolean isPerfectRole(String parentRole, String childRole) throws ServiceException {
        boolean perfect = false;
        
        if (parentRole.equals(Constants.COMPANY_ADMIN)) {
            perfect = true;
        } else if ((parentRole.equals(Constants.COMPANY_SALES_MANAGER)) && !(childRole.equals(Constants.COMPANY_ADMIN))) {
            perfect = true;
        } else if (parentRole.equals(Constants.COMPANY_SALES_EXECUTIVE) && !(childRole.equals(Constants.COMPANY_ADMIN)) && !(childRole.equals(Constants.COMPANY_SALES_MANAGER))) {
            perfect = true;
        } else if (parentRole.equals(Constants.COMPANY_SALES_EXECUTIVE)) {
            perfect = false;
        }
        return perfect;
    }

    public KwlReturnObject getColumnHeader(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = null;
            String Hql = "from ColumnHeader c ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "where");
                Hql += filterQuery;
            }
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject getMandatoryDefaultColumnHeader(Session session, HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = new ArrayList();
            String Hql = "from DefaultHeader c where c.mandatory = ? and c.moduleName = ? ";
            filter_params.add(true);
            filter_params.add(requestParams.get("moduleName").toString());
            Hql+=" and c.id not in ( select ch.defaultheader.id from ColumnHeader ch where ch.mandotory = ? and ch.company.companyID = ? and ch.defaultheader.moduleName = ? ) ";
            filter_params.add(true);
            filter_params.add(requestParams.get("companyid").toString());
            filter_params.add(requestParams.get("moduleName").toString());
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getMandatoryDefaultColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject getMandatoryDefaultColumnHeader(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = new ArrayList();
            String Hql = "from DefaultHeader c where c.mandatory = ? and c.moduleName = ? ";
            filter_params.add(true);
            filter_params.add(requestParams.get("moduleName").toString());
            Hql+=" and c.id not in ( select ch.defaultheader.id from ColumnHeader ch where ch.company.companyID = ? and ch.defaultheader.moduleName = ? ) ";
            Hql+=" and c.pojoheadername not in ( select  fh.id from FieldParams fh where fh.company.companyID != ?  ) ";
            filter_params.add(requestParams.get("companyid").toString());
            filter_params.add(requestParams.get("moduleName").toString());
            filter_params.add(requestParams.get("companyid").toString());
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getMandatoryDefaultColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public List getAllMandatoryColumnHeader(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        List parentList = new ArrayList();
        try {
            ArrayList filter_params = new ArrayList();
            filter_params.add(true);
            filter_params.add(requestParams.get("moduleName").toString());
            filter_params.add(false);
            filter_params.add(requestParams.get("companyid").toString());
            filter_params.add(requestParams.get("moduleName").toString());
            ll = executeQuery(MandatoryDefaultHeaderQuery, filter_params.toArray());
            parentList.add(ll);
            
            filter_params = new ArrayList();
            filter_params.add(requestParams.get("companyid").toString());
            filter_params.add(requestParams.get("moduleName").toString());
            filter_params.add(true);
            List<ColumnHeader> ll1 = executeQuery(MandatoryColumnHeaderQuery, filter_params.toArray());
            parentList.add(ll1);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getAllMandatoryColumnHeader", ex);
        }
        return parentList;
    }

    public KwlReturnObject getDefaultColumnHeader(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = null;
            String Hql = "from DefaultHeader c ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "where");
                Hql += filterQuery;
            }
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getDefaultColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public KwlReturnObject getDashboardReportConfig(ArrayList<String> filter_names, ArrayList<Object> filter_params) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String filterQuery = StringUtil.filterQuery(filter_names, "where");
            String Hql = " select dc.companyid, dc.userid, dc.reportid, dc.reportname, dc.dashboard, dc.emailreport, concat(u.fname, ' ', u.lname) as username, u.emailid " +
                    " from dashreport_config dc inner join users u on u.userid = dc.userid " + filterQuery;
            ll = executeNativeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getDashboardReportConfig", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public void setDashboardReportConfig(ArrayList<Object> filter_params, boolean editFlag) throws ServiceException {
        try {
            String Hql = "";
            if(editFlag) {
                Hql = " update dashreport_config set reportname = ?, dashboard = ?, emailreport = ?  " +
                        " where userid = ? and reportid = ? ";
            } else {
                Hql = " insert into dashreport_config (id, companyid, userid, reportid, reportname, dashboard, emailreport)  " +
                        " values (?, ?, ?, ?, ?, ?, ?) ";
            }
            executeNativeUpdate(Hql, filter_params.toArray());
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.setDashboardReportConfig", ex);
        }
    }

    public KwlReturnObject getCustomColumnHeader(String reportid, String companyid) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = new ArrayList();
            filter_params.add(reportid);
            filter_params.add(companyid);
            String Hql = "select oc.fieldlabel, oc.fieldtip, fp.fieldname, oc.avgflag, oc.fieldid from opp_reportconfig oc " +
                    "inner join fieldParams fp on oc.fieldid = fp.id where oc.reportid = ? and oc.companyid = ? ";
            ll = executeNativeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getCustomColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject getMappingHeaders(HashMap<String, Object> requestParams,String companyid) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String moduleName = requestParams.get("moduleName").toString();
            ArrayList filter_params = null;
            // distinct is not added since there is companyid check
            String Hql = "select d,ch from DefaultHeader d left join d.headerinfo ch where (ch is null or (ch is not null and ch.company.companyID = ?) or ( ch is not null and ch.company.companyID = ? and d.customflag = false   )  ) ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "and");
                Hql += filterQuery;
            }
            Hql += " group by d.id ";
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getDefaultColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    public KwlReturnObject getMappedHeaders(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = null;
            String Hql = " from LeadConversionMappings m ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "where");
                Hql += filterQuery;
            }
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ServiceException.FAILURE("crmCommonDAOImpl.getDefaultColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject saveMappedheaders(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
           ArrayList filter_params = null;
           String Hql = "delete from LeadConversionMappings m ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "where");
                Hql += filterQuery;
            }
           executeUpdate(Hql,  filter_params.toArray());

           if(!StringUtil.isNullOrEmpty(requestParams.get("modulefieldid").toString()) && !StringUtil.isNullOrEmpty(requestParams.get("leadfieldid").toString()) ){
               LeadConversionMappings leadConversionMappingsObj = new LeadConversionMappings();
               leadConversionMappingsObj.setCompany((Company)get(Company.class, requestParams.get("companyid").toString()));
               leadConversionMappingsObj.setDefaultMapping(false);
               leadConversionMappingsObj.setModuleName(requestParams.get("moduleName").toString());
               leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, requestParams.get("modulefieldid").toString()));
               leadConversionMappingsObj.setLeadfield((DefaultHeader) get(DefaultHeader.class, requestParams.get("leadfieldid").toString()));
               save(leadConversionMappingsObj);
           }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    public KwlReturnObject createDefaultMapping(String companyid) throws ServiceException {
        int dl = 0;
        List ll = null;
        try{

            Company companyObj = (Company)get(Company.class, companyid);
            DefaultHeader leadNameObj = (DefaultHeader) get(DefaultHeader.class, Constants.LEAD_NAME);
            DefaultHeader leadRevenueObj = (DefaultHeader) get(DefaultHeader.class, Constants.LEAD_REVENUE);
            DefaultHeader leadPriceeObj = (DefaultHeader) get(DefaultHeader.class, Constants.LEAD_PRICE);
            DefaultHeader leadPhoneObj = (DefaultHeader) get(DefaultHeader.class, Constants.LEAD_PHONE);
            DefaultHeader leadIndustryObj = (DefaultHeader) get(DefaultHeader.class, Constants.LEAD_INDUSTRY);
            DefaultHeader leadProductObj = (DefaultHeader) get(DefaultHeader.class, Constants.LEAD_PRODUCT);
            DefaultHeader leadEmailObj = (DefaultHeader) get(DefaultHeader.class, Constants.LEAD_EMAIL);
            DefaultHeader leadLeadSourceObj = (DefaultHeader) get(DefaultHeader.class, Constants.LEAD_LEADSOURCE);
            DefaultHeader leadTitleObj = (DefaultHeader) get(DefaultHeader.class, Constants.LEAD_TITLE);

           String moduleName = "Account";

           LeadConversionMappings leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.ACCOUNT_NAME));
           leadConversionMappingsObj.setLeadfield(leadNameObj);
           save(leadConversionMappingsObj);

           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.ACCOUNT_REVENUE));
           leadConversionMappingsObj.setLeadfield(leadRevenueObj);
           save(leadConversionMappingsObj);

           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.ACCOUNT_PRICE));
           leadConversionMappingsObj.setLeadfield(leadPriceeObj);
           save(leadConversionMappingsObj);
           
           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.ACCOUNT_PHONE));
           leadConversionMappingsObj.setLeadfield(leadPhoneObj);
           save(leadConversionMappingsObj);
           
           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.ACCOUNT_INDUSTRY));
           leadConversionMappingsObj.setLeadfield(leadIndustryObj);
           save(leadConversionMappingsObj);

           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.ACCOUNT_PRODUCT));
           leadConversionMappingsObj.setLeadfield(leadProductObj);
           save(leadConversionMappingsObj);

           // Contact
           moduleName = "Contact";
           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.CONTACT_LASTNAME));
           leadConversionMappingsObj.setLeadfield(leadNameObj);
           save(leadConversionMappingsObj);
           
           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.CONTACT_EMAIL));
           leadConversionMappingsObj.setLeadfield(leadEmailObj);
           save(leadConversionMappingsObj);
           
           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.CONTACT_LEADSOURCE));
           leadConversionMappingsObj.setLeadfield(leadLeadSourceObj);
           save(leadConversionMappingsObj);

           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.CONTACT_PHONE));
           leadConversionMappingsObj.setLeadfield(leadPhoneObj);
           save(leadConversionMappingsObj);
           
           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.CONTACT_TITLE));
           leadConversionMappingsObj.setLeadfield(leadTitleObj);
           save(leadConversionMappingsObj);

           // Opportunity
           moduleName ="Opportunity";
           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.OPPORTUNITY_LEADSOURCE));
           leadConversionMappingsObj.setLeadfield(leadLeadSourceObj);
           save(leadConversionMappingsObj);
           
           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.OPPORTUNITY_NAME));
           leadConversionMappingsObj.setLeadfield(leadNameObj);
           save(leadConversionMappingsObj);

           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.OPPORTUNITY_PRICE));
           leadConversionMappingsObj.setLeadfield(leadPriceeObj);
           save(leadConversionMappingsObj);

           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.OPPORTUNITY_PRODUCT));
           leadConversionMappingsObj.setLeadfield(leadProductObj);
           save(leadConversionMappingsObj);

           leadConversionMappingsObj = new LeadConversionMappings();
           leadConversionMappingsObj.setCompany(companyObj);
           leadConversionMappingsObj.setDefaultMapping(true);
           leadConversionMappingsObj.setModuleName(moduleName);
           leadConversionMappingsObj.setModulefield((DefaultHeader) get(DefaultHeader.class, Constants.OPPORTUNITY_SALESAMOUNT));
           leadConversionMappingsObj.setLeadfield(leadRevenueObj);
           save(leadConversionMappingsObj);

        }catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.createDefaultMapping", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
    public KwlReturnObject getDefaultMasterItem(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            ArrayList filter_params = null;
            String Hql = "from DefaultMasterItem c ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                String filterQuery = StringUtil.filterQuery(filter_names, "where");
                Hql += filterQuery;
            }
            ll = executeQuery(Hql, filter_params.toArray());
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getDefaultColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public HashMap<String, DefaultMasterItem> getDefaultMasterItemsMap(Map<String, Object> requestParams) throws ServiceException {
        List<DefaultMasterItem> ll = null;
        HashMap<String, DefaultMasterItem> defaultMasterMap = new HashMap<String, DefaultMasterItem>();
        try {
            ArrayList filter_params = null;
            boolean isLeadSource = false;
            String Hql = "from DefaultMasterItem c ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                Hql += StringUtil.filterQuery(filter_names, "where");
                int ind = Hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(Hql.substring(ind+1,ind+2));
                    if(filter_params.get(index).toString().contains(Constants.LEAD_SOURCEID) &&
                            !filter_params.get(index).toString().contains(Constants.CAMPAIGN_SOURCEID)) {
                        isLeadSource = true;
                    }
                    Hql = Hql.replaceAll("("+index+")", filter_params.get(index).toString());
                    filter_params.remove(index);
                }
            }
            ll = executeQuery(Hql, filter_params.toArray());
            for(DefaultMasterItem dmasterObj : ll){
                defaultMasterMap.put(dmasterObj.getID(), dmasterObj);
            }
            
            if(isLeadSource) {
                filter_params.clear();
                Hql = "from DefaultMasterItem c ";
                String subQry = " where c.crmCombomaster.masterid = ? and c.mainID in (select d.campaignid from CrmCampaign d " +
                        "where d.deleteflag=0 and d.validflag=1 ";
                filter_params.add(Constants.CAMPAIGN_SOURCEID);
                if(requestParams.containsKey(Constants.companyid)) {
                    filter_params.add(requestParams.get(Constants.companyid));
                    subQry += "and d.company.companyID= ? ";
                }
                if(requestParams.containsKey(Constants.userlist_value) && requestParams.get(Constants.userlist_value)!=null) {
                     subQry += " and d.usersByUserid.userID in (" + requestParams.get(Constants.userlist_value) + ")";
                }
                subQry = subQry + ")";
                Hql +=subQry;
                ll = executeQuery(Hql, filter_params.toArray());
                for(DefaultMasterItem dmasterObj : ll){
                    defaultMasterMap.put(dmasterObj.getID(), dmasterObj);
                }
            }
            
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getDefaultMasterItemsMap", ex);
        }
        return defaultMasterMap;
    }

    public HashMap<String, FieldComboData> getCustomComboItemsMap(Map<String, Object> requestParams) throws ServiceException {
        List<FieldComboData> ll = null;
        HashMap<String, FieldComboData> defaultMasterMap = new HashMap<String, FieldComboData>();
        try {
            ArrayList filter_params = null;
            String Hql = "from FieldComboData c ";
            if(requestParams.containsKey("filter_params")) {
                filter_params = (ArrayList) requestParams.get("filter_params");
                ArrayList filter_names = (ArrayList) requestParams.get("filter_names");
                Hql += StringUtil.filterQuery(filter_names, "where");
                int ind = Hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(Hql.substring(ind+1,ind+2));
                    Hql = Hql.replaceAll("("+index+")", filter_params.get(index).toString());
                    filter_params.remove(index);
                }
                ll = executeQuery(Hql, filter_params.toArray());
            } else {
                ll = executeQuery(Hql);
            }
            
            for(FieldComboData dmasterObj : ll){
                defaultMasterMap.put(dmasterObj.getId(), dmasterObj);
            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getCustomComboItemsMap", ex);
        }
        return defaultMasterMap;
    }

    public KwlReturnObject saveColumnHeader(HashMap<String, Object> requestParams) throws ServiceException {
        boolean successFlag = false;
        List list = new ArrayList();
        try {
            String newheader = null;
            if(requestParams.get("newheader")!=null)
                newheader = requestParams.get("newheader").toString();
            String oldheader = requestParams.get("oldheader").toString();
            String modulename = requestParams.get("modulename").toString();
            String headerid = requestParams.get("id").toString();
            String userid = requestParams.get("userid").toString();
            String companyid = requestParams.get("companyid").toString();
            boolean mandatory = false;
            String ismandatory = null;
            if(requestParams.get("isMandatory")!=null)
                ismandatory = requestParams.get("isMandatory").toString();
            String pojoname = requestParams.get("pojoname").toString();
            String xtype = requestParams.get("xtype").toString();
            String hid = requestParams.get("headerid").toString();
            String uRole = requestParams.get("roleid").toString();
            if (uRole.equals(Constants.COMPANY_ADMIN)) {
                ColumnHeader ch=null;
                if(!StringUtil.isNullOrEmpty(headerid)){
                    ch = (ColumnHeader) get(ColumnHeader.class, headerid);
                }else{
                    headerid = UUID.randomUUID().toString();
                    Company company=(Company) get(Company.class, companyid);
                    ch = new ColumnHeader();
                    ch.setId(headerid);
                    ch.setCompany(company);
                    DefaultHeader dh = (DefaultHeader) get(DefaultHeader.class, hid);
                    ch.setDefaultheader(dh);
                }
                if(newheader!=null){
                    newheader=StringUtil.serverHTMLStripperWithoutQuoteReplacement(newheader);
                    ch.setNewHeader(newheader);
                }
                if(ismandatory!=null){
                    mandatory = Boolean.parseBoolean(ismandatory);
                    ch.setMandotory(mandatory);
                }
                ch.setModuleName(modulename);
                if(StringUtil.isNullOrEmpty(newheader)&&!mandatory&&!ch.getDefaultheader().isCustomflag()){
                    headerid = "";
                    delete(ch);
                }
                else{
                    save(ch);
                }
                successFlag = true;
                list.add(headerid);

            }
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.saveColumnHeader", ex);
        }
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", list, list.size());
    }


    @Override
     public KwlReturnObject validaterecords(String module, String companyid) throws ServiceException {
        boolean successFlag = false;
        KwlReturnObject result = validaterecordsHB(module,companyid);
        return result;
        
    }
    public void ValidateMassupdate(String arrayId[], String modulename, String companyid) {
        try {
            for (int i = 0; i < arrayId.length; i++) {
                validaterecorsingledHB(modulename, arrayId[i], companyid);
            }            
        } catch (ServiceException ex) {
            logger.warn(ex.getMessage(), ex);                        
        }catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }
    }
    public void ValidateMassupdate(String modulename, String companyid)
    {
        try
        {
            validaterecordsHB(modulename, companyid);
        } catch (ServiceException ex)
        {
            Logger.getLogger(crmCommonDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e)
        {
            Logger.getLogger(crmCommonDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    public KwlReturnObject validaterecord(String module, String id, String companyid, Session session) throws ServiceException {
        boolean successFlag = false;
        KwlReturnObject result = null;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_values = new ArrayList();
            String multiselecttable = "";
            filter_names.add("defaultheader.moduleName");
            filter_values.add(module);

            filter_names.add("mandotory");
            filter_values.add(true);

            filter_names.add("company.companyID");
            filter_values.add(companyid);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_values);

            result = getColumnHeader(requestParams);
            List list = result.getEntityList();
            Iterator itr = list.iterator();
            String tableid = "";
            String tablecstm = "";
            String table = "";
            if(module.equals("Account")){
                tableid = "account";
                tablecstm = "account";
                table = "account";
                multiselecttable = "crm_accountProducts";
            }
            else if(module.equals("Campaign")){
                tableid = "CrmCampaign";
            }
            else if(module.equals("Case")){
                tableid = "case";
                tablecstm = "cases";
                table = "case";
                multiselecttable = "crm_caseProducts";
            }
            else if(module.equals("Contact")){
                tableid = "contact";
                tablecstm = "contact";
                table = "contact";
            }
            else if(module.equals("Lead")){
                tableid = "lead";
                tablecstm = "lead";
                table = "lead";
                multiselecttable = "crm_leadProducts";
            }
            else if(module.equals("Opportunity")){
                tableid = "opp";
                tablecstm = "opportunity";
                table = "opportunity";
                multiselecttable = "crm_oppurtunityProducts";
            }
            else if(module.equals("Product")){
                tableid = "product";
                tablecstm = "product";
                table = "product";
            }

            String str1 = "update crm_"+table+" set validflag=1 where companyid='"+companyid+"' and validflag=0 and "+tableid+"id=? ";

            String selectdef1 = "";
            String selectcstm1 = "";
            String filterquery1 = "";
            ArrayList params = new ArrayList();
            params.add(id);
            int ctr = 0;

            while(itr.hasNext()){
                ColumnHeader ch = (ColumnHeader) itr.next();
                if(!ch.getDefaultheader().isCustomflag() && !ch.getDefaultheader().getConfigid().equals("1")){
                    if((ch.getDefaultheader().getXtype().equals("4") || ch.getDefaultheader().getXtype().equals("7")) && !StringUtil.isNullOrEmpty(ch.getDefaultheader().getValidateType()) && ch.getDefaultheader().getValidateType().equals("multiselect")) {
                        selectcstm1 += " and "+tableid+"id in (select "+tableid+"id from "+multiselecttable+") ";
                    } else
                        selectdef1 += " ifnull("+ch.getDefaultheader().getDbcolumnname() +",'')!='' and " + ch.getDefaultheader().getDbcolumnname() +"!='' and ";
                }else{
                    if(ch.getDefaultheader().getXtype().equals("7")){
                        selectcstm1 += " and "+tableid+"id in (select modulerecid from "+tablecstm+"cstmmultiselect where fieldparamid=?) ";
                        params.add(ch.getDefaultheader().getPojoheadername());
                    }else{
                        selectcstm1 += " and "+tableid+"id in (select modulerecid from "+tablecstm+"cstm where fieldparamid=?) ";
                        params.add(ch.getDefaultheader().getPojoheadername());
                    }
                    ctr++;
                }
            }

            requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", module);
            result = getMandatoryDefaultColumnHeader(requestParams);
            list = result.getEntityList();
            itr = list.iterator();
            while(itr.hasNext()){
                DefaultHeader dh = (DefaultHeader) itr.next();
                if(!StringUtil.isNullOrEmpty(dh.getDbcolumnname()) && !dh.getConfigid().equals("1")){ // Account Owner is now have value and used in custom report
                    selectdef1 +=" ifnull("+ dh.getDbcolumnname() +",'')!='' and " + dh.getDbcolumnname() +"!='' and ";
                }
            }

            if(selectdef1.length()>0){
                selectdef1 = selectdef1.substring(0, selectdef1.length()-4);
                 filterquery1 += selectdef1;
            }
            filterquery1 += selectcstm1;
            if(filterquery1.length()>0){
                 str1 += " and ("+filterquery1+")" ;
            }


            executeNativeUpdate(str1,params.toArray());
            
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.saveColumnHeader", ex);
        }
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", result.getEntityList(), result.getRecordTotalCount());
    }
    @Override
    public List getAllMandatoryColumnHeader(HashMap<String, Object> requestParams,String DefaultHeaderColumnList0,String ColumnHeaderColumnList1) throws ServiceException {
        List ll = null;
        List parentList = new ArrayList();
        try {
            ArrayList filter_params = new ArrayList();

            StringBuilder Hql = new StringBuilder(SELECT).append(DefaultHeaderColumnList0).append(MandatoryDefaultHeaderQuery);
            filter_params.add(true);
            filter_params.add(requestParams.get(Constants.moduleName).toString());
            filter_params.add(false);
            filter_params.add(requestParams.get(Constants.companyid).toString());
            filter_params.add(requestParams.get(Constants.moduleName).toString());
            ll = executeQuery(Hql.toString(), filter_params.toArray());
            parentList.add(ll);

            filter_params = new ArrayList();
            Hql =new StringBuilder(SELECT).append(ColumnHeaderColumnList1).append(MandatoryColumnHeaderQuery);
            filter_params.add(requestParams.get(Constants.companyid).toString());
            filter_params.add(requestParams.get(Constants.moduleName).toString());
            filter_params.add(true);
            List ll1 = executeQuery(Hql.toString(), filter_params.toArray());
            parentList.add(ll1);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getAllMandatoryColumnHeader", ex);
        }
        return parentList;
    }

    public KwlReturnObject validaterecorsingledHB(String module, String id, String companyid) throws ServiceException {
            boolean successFlag = false;
        KwlReturnObject result = null;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_values = new ArrayList();
            String multiselecttable = "";
            filter_names.add("defaultheader.moduleName");
            filter_values.add(module);

            filter_names.add("mandotory");
            filter_values.add(true);

            filter_names.add("company.companyID");
            filter_values.add(companyid);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_values);
            
            result = getColumnHeader(requestParams);
            List list = result.getEntityList();
            Iterator itr = list.iterator();
            String tableid = "";
            String tablecstm = "";
            String table = "";
            String tablealias = "maintable";
            if(module.equals("Account")){
                tableid = Constants.Crm_accountid;
                tablecstm = Constants.Crm_account_custom_data_pojo;
                table = Constants.Crm_account_pojo;
                multiselecttable =Constants.Crm_account_product_pojo_ref;
            }
            else if(module.equals("Campaign")){
                tableid = Constants.Crm_campaignid;
                table = Constants.Crm_campaign_pojo;
            }
            else if(module.equals("Case")){
                tableid = Constants.Crm_caseid;
                tablecstm = Constants.Crm_case_custom_data_pojo;
                table = Constants.Crm_case_pojo;
                multiselecttable= Constants.Crm_case_productpojo_ref;
            }
            else if(module.equals("Contact")){
                tableid = Constants.Crm_contactid;
                tablecstm = Constants.Crm_contact_custom_data_pojo;
                table = Constants.Crm_contact_pojo;
            }
            else if(module.equals("Lead")){
                tableid = Constants.Crm_leadid;
                tablecstm = Constants.Crm_lead_custom_data_pojo;
                table = Constants.Crm_lead_pojo;
                multiselecttable= Constants.Crm_lead__productpojo_ref;
            }
            else if(module.equals("Opportunity")){
                tableid = Constants.Crm_opportunityid;
                tablecstm = Constants.Crm_opportunity_custom_data_pojo;
                table = Constants.Crm_opportunity_pojo;
                multiselecttable = Constants.Crm_opportunity_product_pojo_ref;
            }
            else if(module.equals("Product")){
                tableid = Constants.Crm_productid;
                tablecstm = Constants.Crm_product_custom_data_pojo;
                table = Constants.Crm_product_pojo;
            }
            
            String str1 = " update "+table+" ";
            String setquery = " set validflag=? ";
            String validdationquery =  " ( select "+tablealias+"."+ tableid + " from " + table + " " + tablealias;
            String cstmdataquery = " and " +  tableid+" in ( select "+ tableid+" from " +tablecstm + "  where company.companyID=? and  "+ tableid+"=? ";
            String cstmdatawherecondition = "";
            String wherecondition = " where company.companyID=? and  validflag=? and "+ tableid+"=? ";
            String productcondition= "";
            
            
            
            String cstmdatacondition= "";
            String maincondition = "";
            ArrayList params = new ArrayList();
            params.add(1);
            params.add(companyid);
            params.add(0);
            params.add(id);
            /* subquery params */
            

            while(itr.hasNext()){
                ColumnHeader ch = (ColumnHeader) itr.next();
                DefaultHeader dh = ch.getDefaultheader();
                if(!dh.isCustomflag() && !StringUtil.isNullOrEmpty(dh.getPojoheadername())){
                        String dbcolname = ch.getDefaultheader().getPojoheadername();
                         if((dh.getXtype().equals("4") || dh.getXtype().equals("7")) && !StringUtil.isNullOrEmpty(dh.getValidateType()) && dh.getValidateType().equals("multiselect")) {
                            productcondition += tableid+" in ( select "+tableid+"."+tableid+" from "+multiselecttable+" where "+ tableid+"."+tableid+"=? ) and ";
                            params.add(id);
                         } else
                            maincondition += " ifnull("+ dbcolname+",'')!='' and " +  dbcolname+"!='' and ";
                }else{
                    String dbcolname = dh.getDbcolumnname();
                    if (!StringUtil.isNullOrEmpty(dbcolname) && !dh.getConfigid().equals("1")) {
                            dbcolname = dbcolname.toLowerCase();
                            cstmdatacondition += " ifnull(" + dbcolname + ",' ')!=' ' and " + dbcolname + "!=' ' and ";
                    }
                }
            }

            requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", module);
            result = getMandatoryDefaultColumnHeader(requestParams);
            list = result.getEntityList();
            itr = list.iterator();
            while(itr.hasNext()){
                DefaultHeader dh = (DefaultHeader) itr.next();
                if (!dh.isCustomflag()) {
                    String dbcolname = dh.getPojoheadername();
                    if((dh.getXtype().equals("4") || dh.getXtype().equals("7")) && !StringUtil.isNullOrEmpty(dh.getValidateType()) && dh.getValidateType().equals("multiselect")) {
                            productcondition += tableid+" in ( select "+tableid+"."+tableid+" from "+multiselecttable+" where "+ tableid+"."+tableid+"=? ) and ";
                            params.add(id);
                    } else if (!StringUtil.isNullOrEmpty(dbcolname)) {
                        maincondition += " ifnull(" + dbcolname + ",'')!='' and " + dbcolname + "!='' and ";
                    }
                }else{
                    String dbcolname = dh.getDbcolumnname();
                    if (!StringUtil.isNullOrEmpty(dbcolname) && !dh.getConfigid().equals("1")) {
                            dbcolname = dbcolname.toLowerCase();
                            cstmdatacondition += " ifnull(" + dbcolname + ",' ')!=' ' and " + dbcolname + "!=' ' and ";
                    }
                }
            }

            if(maincondition.length()>0){
                maincondition = maincondition.substring(0, maincondition.length()-4);
                wherecondition += " and " + maincondition;
            }

            if(cstmdatacondition.length()>0){
                cstmdatacondition = cstmdatacondition.substring(0, cstmdatacondition.length()-4);
                cstmdatawherecondition += " and "+ cstmdatacondition ;
            }
            if(productcondition.length()>0){
                productcondition = productcondition.substring(0, productcondition.length()-4);
                productcondition = " and "+ productcondition ;
            }
            if(cstmdatawherecondition.length() > 0){
                cstmdataquery += cstmdatawherecondition + " ) ";
                params.add(companyid);
                params.add(id);
            }else{
                cstmdataquery = " ";
            }
            str1+= setquery;
            str1+= wherecondition + cstmdataquery +  productcondition;
            
            int num1 = executeUpdate(str1,params.toArray());
            System.out.print(num1);
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.saveColumnHeader", ex);
        }
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", result.getEntityList(), result.getRecordTotalCount());
    }
    public KwlReturnObject validaterecordsHB(String module, String companyid) throws ServiceException {
        boolean successFlag = false;
        KwlReturnObject result = null;
        try {
            String multiselecttable = "";
            String tableid = "";
            String tablecstm = "";
            String table = "";
            if(module.equals("Account")){
                tableid = Constants.Crm_accountid;
                tablecstm = Constants.Crm_account_custom_data_pojo.toLowerCase();
                table = Constants.Crm_account_table;
                multiselecttable =Constants.Crm_account_product_pojo_table;
            }
            else if(module.equals("Campaign")){
                tableid = Constants.Crm_campaignid;
                table = Constants.Crm_campaign_table;
            }
            else if(module.equals("Case")){
                tableid = Constants.Crm_caseid;
                tablecstm = Constants.Crm_case_custom_data_pojo.toLowerCase();
                table = Constants.Crm_case_table;
                multiselecttable= Constants.Crm_case_productpojo_table;
            }
            else if(module.equals("Contact")){
                tableid = Constants.Crm_contactid;
                tablecstm = Constants.Crm_contact_custom_data_pojo.toLowerCase();
                table = Constants.Crm_contact_table;
            }
            else if(module.equals("Lead")){
                tableid = Constants.Crm_leadid;
                tablecstm = Constants.Crm_lead_custom_data_pojo.toLowerCase();
                table = Constants.Crm_lead_table;
                multiselecttable= Constants.Crm_lead__productpojo_table;
            }
            else if(module.equals("Opportunity")){
                tableid = Constants.Crm_opportunityid;
                tablecstm = Constants.Crm_opportunity_custom_data_pojo.toLowerCase();
                table = Constants.Crm_opportunity_table;
                multiselecttable = Constants.Crm_opportunity_product_pojo_table;
            }
            else if(module.equals("Product")){
                tableid = Constants.Crm_productid;
                tablecstm = Constants.Crm_product_custom_data_pojo.toLowerCase();
                table = Constants.Crm_product_table;
            }
            String validquery = "  set validflag=?  ";
            StringBuilder str1 = new StringBuilder(" update ").append(table).append(" cl ");
            StringBuilder str = new StringBuilder(" update ").append(table).append(" cl ");
            StringBuilder str2 = new StringBuilder(" update ").append(table).append(" cl ");
            StringBuilder wherecondition = new StringBuilder(" where cl.companyid=?  and  validflag=?   ");
            StringBuilder wherecondition1 = new StringBuilder(" where cl.companyid=? and  validflag=?  ");

            StringBuilder cstmdataquery = new StringBuilder(" inner join  ").append(tablecstm).append(" clc on clc.")
                    .append(tableid).append("=cl.").append(tableid).append(" ");
            StringBuilder cstmdataquery1 = new StringBuilder(" inner join  ").append(tablecstm).append(" clc on clc.")
                    .append(tableid).append("=cl.").append(tableid).append(" ");

            StringBuilder cstmdataquery2 = new StringBuilder(" and ").append(tablecstm).append("ref is null ");
            str2.append(validquery);
            str2.append(" where companyid=?  and  validflag=? ");
            str2.append(cstmdataquery2);

            StringBuilder productcondition=  new StringBuilder(),productcondition1=  new StringBuilder();
            boolean productc = true;

            StringBuilder cstmdatacondition= new StringBuilder(),cstmdatacondition1= new StringBuilder();
            StringBuilder maincondition= new StringBuilder(),maincondition1= new StringBuilder();

            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_values = new ArrayList();
            filter_names.add("defaultheader.moduleName");
            filter_values.add(module);
            filter_names.add("mandotory");
            filter_values.add(true);
            filter_names.add("company.companyID");
            filter_values.add(companyid);
            
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_values);
            result = getColumnHeader(requestParams);
            List list = result.getEntityList();
            Iterator itr = list.iterator();
            
            while(itr.hasNext()){
                ColumnHeader ch = (ColumnHeader) itr.next();
                DefaultHeader dh = ch.getDefaultheader();
                String dbcolname = dh.getDbcolumnname();
                String configid = dh.getConfigid();
                String xtype = dh.getXtype();
                if(!StringUtil.isNullOrEmpty(dbcolname) && !configid.equals("1")){ // configid 1 for Owner
                    dbcolname = dbcolname.toLowerCase();
                    if(!dh.isCustomflag() && !StringUtil.isNullOrEmpty(dh.getPojoheadername())){
                             if((dh.getXtype().equals("4") || dh.getXtype().equals("7")) && !StringUtil.isNullOrEmpty(dh.getValidateType()) && dh.getValidateType().equals("multiselect")) {
                                if(productc){
                                  productcondition.append(" cl.").append(tableid).append(" in ( select ").append(tableid).append(" from ").append(multiselecttable).append(" ) and ");
                                  productcondition1.append(" cl.").append(tableid).append(" not in ( select ").append(tableid).append(" from ").append(multiselecttable).append(" ) or ");
                                  productc = false;
                                 }
                             } else{
                                maincondition.append( dbcolname).append("  is not null and ");
                                maincondition1.append( dbcolname).append(" is null  or ");
                                if(!xtype.equals("3")){
                                        maincondition.append(dbcolname).append("!='' and ");
                                        maincondition1.append(dbcolname).append("='' or ");
                                 }
                             }
                    }else{
                                cstmdatacondition.append("  clc.").append(dbcolname).append(" is not null and  clc.").append( dbcolname).append( "!='' and clc.").append(dbcolname).append( "!='null' and ");
                                cstmdatacondition1.append("  clc.").append(dbcolname).append( " is null or  clc.").append(dbcolname).append( "='' or clc.").append(dbcolname).append( "='null' or ");
                    }
                }
            }

            requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", module);
            result = getMandatoryDefaultColumnHeader(requestParams);
            list = result.getEntityList();
            itr = list.iterator();
            while(itr.hasNext()){
                DefaultHeader dh = (DefaultHeader) itr.next();
                String dbcolname = dh.getDbcolumnname();
                if(!StringUtil.isNullOrEmpty(dbcolname) && !dh.getConfigid().equals("1")){ // configid 1 for Owner. Previously dbcolumn
                    dbcolname = dbcolname.toLowerCase();
                    String xtype = dh.getXtype();
                    if (!dh.isCustomflag()) {
                        if ((dh.getXtype().equals("4") || dh.getXtype().equals("7")) && !StringUtil.isNullOrEmpty(dh.getValidateType()) && dh.getValidateType().equals("multiselect")) {
                            if(productc){
                            productcondition.append(" cl.").append(tableid).append(" in ( select ").append(tableid).append(" from ").append(multiselecttable).append(" where ").append(tableid).append(".companyid=? ) and ");
                            productcondition1.append(" cl.").append(tableid).append( " not in ( select ").append( tableid).append(" from " ).append(multiselecttable).append(" ) or ");
                            productc = false;
                            }
                        } else {
                            maincondition.append( dbcolname).append("  is not null and ");
                            maincondition1.append( dbcolname).append(" is null  or ");
                                if(!xtype.equals("3")){
                                        maincondition.append(dbcolname).append("!='' and ");
                                        maincondition1.append(dbcolname).append("='' or ");
                                 }
                        }
                    }else{
                                cstmdatacondition.append(" clc.").append(dbcolname).append(" is not null and clc.").append(dbcolname).append( "!='' and clc.").append(dbcolname).append( "!='null' and ");
                                cstmdatacondition1.append(" clc.").append(dbcolname).append(" is null or  clc.").append(dbcolname).append( "='' or clc.").append(dbcolname).append( "='null' or ");
                    }
                }
            }

            ArrayList params = new ArrayList();
            params.add(1);
            params.add(companyid);
            params.add(0);
            

            if(cstmdatacondition.length()>0){
                wherecondition.append(" and clc.company=? " );
                wherecondition1.append(" and clc.company=? " );
                params.add(companyid);
            }
            wherecondition1.append(" and ( ");
            if(maincondition.length()>0){
                wherecondition.append(" and ").append( maincondition.substring(0, maincondition.length()-4));
                wherecondition1.append( maincondition1.substring(0, maincondition1.length()-3));
            }

            if(cstmdatacondition.length()>0){
                wherecondition.append(" and ").append(cstmdatacondition.substring(0, cstmdatacondition.length()-4));
                str1.append(cstmdataquery);

                wherecondition1.append(" or ").append(cstmdatacondition1.substring(0, cstmdatacondition1.length()-3)).append(" " );
                str.append(cstmdataquery1);
            }
            if(productcondition.length()>0){
                productcondition = new StringBuilder(" and ( ").append(productcondition.substring(0, productcondition.length()-4)).append(" ) ");
                productcondition1 =new StringBuilder("  or ( ").append(productcondition1.substring(0, productcondition1.length()-3)).append(" ) " );
            }
            
            str1.append(validquery).append(wherecondition).append(productcondition);
            str.append(validquery).append(wherecondition1).append(productcondition1).append(" ) " );;

            Object num1 = executeNativeUpdate(str1.toString(),params.toArray());
            params.set(0,0);
            params.set(2,1);
            /* subquery params */
            num1 = executeNativeUpdate(str.toString(),params.toArray());

            if(cstmdatacondition1.length()>0){
                params.remove(3);
                num1 = executeNativeUpdate(str2.toString(),params.toArray());
            }

        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.validaterecordsHB", ex);
        }
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", result.getEntityList(), result.getRecordTotalCount());
    }

    public KwlReturnObject validaterecord(String module, String id, String companyid) throws ServiceException {
        boolean successFlag = false;
        KwlReturnObject result = null;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_values = new ArrayList();
            String multiselecttable = "";
            filter_names.add("defaultheader.moduleName");
            filter_values.add(module);

            filter_names.add("mandotory");
            filter_values.add(true);

            filter_names.add("company.companyID");
            filter_values.add(companyid);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_values);

            result = getColumnHeader(requestParams);
            List list = result.getEntityList();
            Iterator itr = list.iterator();
            String tableid = "";
            String tablecstm = "";
            String table = "";
            if(module.equals("Account")){
                tableid = "account";
                tablecstm = "account";
                table = "account";
                multiselecttable = "crm_accountProducts";
            }
            else if(module.equals("Campaign")){
                tableid = "CrmCampaign";
            }
            else if(module.equals("Case")){
                tableid = "case";
                tablecstm = "cases";
                table = "case";
                multiselecttable = "crm_caseProducts";
            }
            else if(module.equals("Contact")){
                tableid = "contact";
                tablecstm = "contact";
                table = "contact";
            }
            else if(module.equals("Lead")){
                tableid = "lead";
                tablecstm = "lead";
                table = "lead";
                multiselecttable = "crm_leadProducts";
            }
            else if(module.equals("Opportunity")){
                tableid = "opp";
                tablecstm = "opportunity";
                table = "opportunity";
                multiselecttable = "crm_oppurtunityProducts";
            }
            else if(module.equals("Product")){
                tableid = "product";
                tablecstm = "product";
                table = "product";
            }

            String str1 = "update crm_"+table+" set validflag=1 where companyid='"+companyid+"' and validflag=0 and "+tableid+"id=? ";

            String selectdef1 = "";
            String selectcstm1 = "";
            String filterquery1 = "";
            ArrayList params = new ArrayList();
            params.add(id);
            int ctr = 0;

            while(itr.hasNext()){
                ColumnHeader ch = (ColumnHeader) itr.next();
                if(!ch.getDefaultheader().isCustomflag() && !ch.getDefaultheader().getConfigid().equals("1")){
                    if((ch.getDefaultheader().getXtype().equals("4") || ch.getDefaultheader().getXtype().equals("7")) && !StringUtil.isNullOrEmpty(ch.getDefaultheader().getValidateType()) && ch.getDefaultheader().getValidateType().equals("multiselect")) {
                        selectcstm1 += " and "+tableid+"id in (select "+tableid+"id from "+multiselecttable+") ";
                    } else
                        selectdef1 += " ifnull("+ch.getDefaultheader().getDbcolumnname() +",'')!='' and " + ch.getDefaultheader().getDbcolumnname() +"!='' and ";
                }else{
                    if(ch.getDefaultheader().getXtype().equals("7")){
                        selectcstm1 += " and "+tableid+"id in (select modulerecid from "+tablecstm+"cstmmultiselect where fieldparamid=?) ";
                        params.add(ch.getDefaultheader().getPojoheadername());
                    }else{
                        selectcstm1 += " and "+tableid+"id in (select modulerecid from "+tablecstm+"cstm where fieldparamid=?) ";
                        params.add(ch.getDefaultheader().getPojoheadername());
                    }
                    ctr++;
                }
            }

            requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", module);
            result = getMandatoryDefaultColumnHeader(requestParams);
            list = result.getEntityList();
            itr = list.iterator();
            while(itr.hasNext()){
                DefaultHeader dh = (DefaultHeader) itr.next();
                if(!StringUtil.isNullOrEmpty(dh.getDbcolumnname()) && !dh.getConfigid().equals("1")){ // Account Owner is now have some value used in custom report
                    selectdef1 +=" ifnull("+ dh.getDbcolumnname() +",'')!='' and " + dh.getDbcolumnname() +"!='' and ";
                }
            }
            
            if(selectdef1.length()>0){
                selectdef1 = selectdef1.substring(0, selectdef1.length()-4);
                 filterquery1 += selectdef1;
            }
            filterquery1 += selectcstm1;
            if(filterquery1.length()>0){
                 str1 += " and ("+filterquery1+")" ;
            }
            executeNativeUpdate(str1,params.toArray());
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.validaterecord", ex);
        }
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", result.getEntityList(), result.getRecordTotalCount());
    }

    public KwlReturnObject validaterecordsjsp(String module, String companyid, Session session) throws ServiceException {
        boolean successFlag = false;
        KwlReturnObject result = null;
        try {
            HashMap<String,Object> requestParams = new HashMap<String, Object>();
            ArrayList filter_names = new ArrayList();
            ArrayList filter_values = new ArrayList();
            String multiselecttable = "";
            filter_names.add("defaultheader.moduleName");
            filter_values.add(module);

            filter_names.add("mandotory");
            filter_values.add(true);

            filter_names.add("company.companyID");
            filter_values.add(companyid);

            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_params", filter_values);

            result = getColumnHeader(requestParams);
            List list = result.getEntityList();
            Iterator itr = list.iterator();
            String tableid = "";
            String tablecstm = "";
            String table = "";
            if(module.equals("Account")){
                tableid = "account";
                tablecstm = "account";
                table = "account";
                multiselecttable = "crm_accountProducts";
            }
            else if(module.equals("Campaign")){
                tableid = "CrmCampaign";
            }
            else if(module.equals("Case")){
                tableid = "case";
                tablecstm = "cases";
                table = "case";
                multiselecttable = "crm_caseProducts";
            }
            else if(module.equals("Contact")){
                tableid = "contact";
                tablecstm = "contact";
                table = "contact";
            }
            else if(module.equals("Lead")){
                tableid = "lead";
                tablecstm = "lead";
                table = "lead";
                multiselecttable = "crm_leadProducts";
            }
            else if(module.equals("Opportunity")){
                tableid = "opp";
                tablecstm = "opportunity";
                table = "opportunity";
                multiselecttable = "crm_oppurtunityProducts";
            }
            else if(module.equals("Product")){
                tableid = "product";
                tablecstm = "product";
                table = "product";
            }

            String str = "update crm_"+table+" set validflag=0 where companyid='"+companyid+"' and validflag=1 ";
            String str1 = "update crm_"+table+" set validflag=1 where companyid='"+companyid+"' and validflag=0 ";

            String selectdef = "";
            String selectdef1 = "";
            String selectcstm = "";
            String selectcstm1 = "";
            String filterquery = "";
            String filterquery1 = "";
            ArrayList params = new ArrayList();
            int ctr = 0;

            while(itr.hasNext()){
                ColumnHeader ch = (ColumnHeader) itr.next();
                if(!ch.getDefaultheader().isCustomflag() && !ch.getDefaultheader().getConfigid().equals("1")) {
                    if((ch.getDefaultheader().getXtype().equals("4") || ch.getDefaultheader().getXtype().equals("7")) && !StringUtil.isNullOrEmpty(ch.getDefaultheader().getValidateType()) && ch.getDefaultheader().getValidateType().equals("multiselect")) {
                        selectcstm += " or "+tableid+"id not in (select "+tableid+"id from "+multiselecttable+") ";
                        selectcstm1 += " and "+tableid+"id in (select "+tableid+"id from "+multiselecttable+") ";
                    } else {
                        selectdef += " ifnull("+ch.getDefaultheader().getDbcolumnname() +",'')='' or " + ch.getDefaultheader().getDbcolumnname() +"='' or ";
                        selectdef1 += " ifnull("+ch.getDefaultheader().getDbcolumnname() +",'')!='' and " + ch.getDefaultheader().getDbcolumnname() +"!='' and ";
                    }
                }else{
                  if(ch.getDefaultheader().getXtype().equals("7")){
                        selectcstm += " or "+tableid+"id not in(select modulerecid from "+tablecstm+"cstmmultiselect where fieldparamid=?) ";
                        selectcstm1 += " and "+tableid+"id in (select modulerecid from "+tablecstm+"cstmmultiselect where fieldparamid=?) ";
                        params.add(ch.getDefaultheader().getPojoheadername());
                    }else{
                        selectcstm += " or "+tableid+"id not in(select modulerecid from "+tablecstm+"cstm where fieldparamid=?) ";
                        selectcstm1 += " and "+tableid+"id in (select modulerecid from "+tablecstm+"cstm where fieldparamid=?) ";
                        params.add(ch.getDefaultheader().getPojoheadername());
                    }
                  ctr++;
                }
            }

            requestParams = new HashMap<String, Object>();
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", module);
            result = getMandatoryDefaultColumnHeader(session, requestParams);
            list = result.getEntityList();
            itr = list.iterator();
            while(itr.hasNext()){
                DefaultHeader dh = (DefaultHeader) itr.next();
                if(!StringUtil.isNullOrEmpty(dh.getDbcolumnname()) && !dh.getConfigid().equals("1")){ // Account Owner is now have some values used in custom report
                    selectdef += " ifnull("+dh.getDbcolumnname() +",'')='' or " + dh.getDbcolumnname() +"='' or ";
                    selectdef1 +=" ifnull("+ dh.getDbcolumnname() +",'')!='' and " + dh.getDbcolumnname() +"!='' and ";
                }
            }
            if(selectdef.length()>0){
                selectdef = selectdef.substring(0, selectdef.length()-3);
                filterquery += selectdef;
            }
            filterquery += selectcstm;
            if(filterquery.length()>0){
                 str += " and ("+filterquery+")" ;
            }


            if(selectdef1.length()>0){
                selectdef1 = selectdef1.substring(0, selectdef1.length()-4);
                 filterquery1 += selectdef1;
            }
            filterquery1 += selectcstm1;
            if(filterquery1.length()>0){
                 str1 += " and ("+filterquery1+")" ;
            }


            
            executeNativeUpdate(str,params.toArray());
            executeNativeUpdate(str1,params.toArray());

        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.saveColumnHeader", ex);
        }
        return new KwlReturnObject(successFlag, KWLErrorMsgs.S01, "", result.getEntityList(), result.getRecordTotalCount());
    }

     public KwlReturnObject isDuplicatecolumn(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String newheader = (String) requestParams.get("newheader");
            String modulename = (String) requestParams.get("modulename");
            String headerid = (String) requestParams.get("headerid");
            String companyid = (String) requestParams.get("companyid");
            Object[] params = {modulename,companyid,newheader,headerid, companyid,modulename,headerid,newheader,newheader};
            String selectQuery = "select dh.id from default_header dh inner join modules mo on mo.id = dh.module where mo.modulename = ? " +
                "and (dh.customflag = 'F' or dh.customflag = '0') and dh.id not in (select defaultHeader " +
                "from column_header where company = ?)  and defaultHeader = ? and dh.id !=?" +
                " UNION " +
                "select ch.id from column_header ch inner join default_header dh on dh.id = ch.defaultHeader " +
                "inner join modules mo on mo.id = dh.module where company = ? and mo.modulename = ? and dh.id !=? and " +
                "(ch.newHeader = ? or dh.defaultHeader = ?)";
            List dll = executeNativeQuery(selectQuery, params);
            dl = dll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getDefaultColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }

    @Override
    public boolean columnExists(String modulename, String companyid, String newheader, String headerid)
    {
        String selectQuery = "select dh.id from default_header dh inner join modules mo on mo.id = dh.module where mo.modulename = ? " +
        "and (dh.customflag = 'F' or dh.customflag = '0') and dh.id not in (select defaultHeader " +
        "from column_header where company = ?)  and defaultHeader = ? and dh.id !=?" +
        " UNION " +
        "select ch.id from column_header ch inner join default_header dh on dh.id = ch.defaultHeader " +
        "inner join modules mo on mo.id = dh.module where company = ? and mo.modulename = ? and dh.id !=? and " +
        "(ch.newHeader = ? or dh.defaultHeader = ?)";
        List lst = executeNativeQuery(selectQuery, new String[]{modulename, companyid, newheader, headerid, companyid, modulename, headerid, newheader, newheader});
        return (!lst.isEmpty() && !StringUtil.isNullOrEmpty(newheader));
    }
    public List getLeadSourceId(String campaignid)
    {
       	List<DefaultMasterItem> ll=executeQuery("from com.krawler.crm.database.tables.DefaultMasterItem  dm where dm.crmCombodata.valueid  in ("+campaignid+")");
    	
    return ll;
    }

    public int  chekcDuplicateEntryForLead(String firstname, String lastname, String email, String companyid) throws ServiceException {
        List ll = null;
        int dl = 0;
        try {
            String Hql = "select id from com.krawler.crm.database.tables.CrmLead c where c.firstname=? and c.lastname=? and c.email=? and c.validflag=1 and c.company.companyID =? and c.deleteflag=0 and c.istransfered='0' and c.isarchive='F' and c.isconverted= '0' ";
            ll = executeQuery(Hql, new Object[]{firstname,lastname,email,companyid});
            dl = ll.size();
        } catch (HibernateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw ServiceException.FAILURE("crmEmailMarketingDAOImpl.chekcDuplicateEntryForLead", ex);
        }
        return dl;
    }

    public KwlReturnObject getCustomReport(HashMap<String, Object> requestParams) throws ServiceException {
        int dl = 0;
        List ll = null;
        try {
            String companyid = (String) requestParams.get("companyid");
            Object[] params = {companyid};
            String selectQuery = " from com.krawler.crm.database.tables.CustomReportList c where c.deleteflag=0 and c.usersByCreatedbyid.company.companyID=? ";
            ll = executeQuery(selectQuery, params);
            dl = ll.size();
        } catch (Exception ex) {
            throw ServiceException.FAILURE("crmCommonDAOImpl.getDefaultColumnHeader", ex);
        }
        return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
    }
}
