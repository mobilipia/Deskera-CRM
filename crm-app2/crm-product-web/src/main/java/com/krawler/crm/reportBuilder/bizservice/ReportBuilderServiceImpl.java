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

package com.krawler.crm.reportBuilder.bizservice;

import com.krawler.common.util.Log;
import com.krawler.common.util.LogFactory;
import com.krawler.crm.reportBuilder.dao.ReportBuilderDao;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.admin.FieldComboData;
import com.krawler.common.admin.User;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.crm.database.tables.CustomReportColumns;
import com.krawler.crm.database.tables.CustomReportList;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.lead.dm.LeadOwnerInfo;
import com.krawler.crm.utils.Constants;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.common.kwlCommonTablesDAO;
import com.krawler.spring.crm.accountModule.crmAccountDAO;
import com.krawler.spring.crm.caseModule.crmCaseDAO;
import com.krawler.spring.crm.common.crmCommonDAO;
import com.krawler.spring.crm.leadModule.crmLeadDAO;
import com.krawler.spring.crm.opportunityModule.crmOpportunityDAO;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

/**
 *
 * @author sagar
 */
public class ReportBuilderServiceImpl implements ReportBuilderService {
    private ReportBuilderDao reportBuilderDaoObj;
    private static final Log LOGGER = LogFactory.getLog(ReportBuilderServiceImpl.class);
    private fieldManagerDAO fieldManagerDAOobj;
    private crmCommonDAO crmCommonDAO;
    private kwlCommonTablesDAO kwlCommonTablesDAOObj;
    private crmLeadDAO crmLeadDAOObj;

    public void setcrmLeadDAO(crmLeadDAO crmLeadDAOObj1) {
        this.crmLeadDAOObj = crmLeadDAOObj1;
    }
    
    private crmAccountDAO crmAccountDAOObj;

    public void setcrmAccountDAO(crmAccountDAO crmAccountDAOObj1) {
        this.crmAccountDAOObj = crmAccountDAOObj1;
    }
    
    private crmOpportunityDAO crmOpportunityDAOObj;

    public void setcrmOpportunityDAO(crmOpportunityDAO crmOpportunityDAOObj1) {
        this.crmOpportunityDAOObj = crmOpportunityDAOObj1;
    }
    
    private crmCaseDAO crmCaseDAOObj;

    public void setcrmCaseDAO(crmCaseDAO crmCaseDAOObj1) {
        this.crmCaseDAOObj = crmCaseDAOObj1;
    }

    public void setkwlCommonTablesDAO(kwlCommonTablesDAO kwlCommonTablesDAOObj1) {
        this.kwlCommonTablesDAOObj = kwlCommonTablesDAOObj1;
    }

     /**
     * @return the crmCommonDAO
     */
    public crmCommonDAO getCrmCommonDAO()
    {
        return crmCommonDAO;
    }

    /**
     * @param crmCommonDAO the crmCommonDAO to set
     */
    public void setCrmCommonDAO(crmCommonDAO crmCommonDAO)
    {
        this.crmCommonDAO = crmCommonDAO;
    }

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    public void setReportBuilderDao(ReportBuilderDao reportBuilderDaoObj1) {
        this.reportBuilderDaoObj = reportBuilderDaoObj1;
    }

    @Override
    public JSONObject getReportMetadata(JSONObject commData, boolean export, int reportno, StringBuffer searchJson, ArrayList<CustomReportColumns> quickSearchCol, ArrayList<CustomReportColumns> groupCol, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap, boolean detailFlag) {
        JSONObject jobjTemp = new JSONObject();
        JSONArray jarrColumns = new JSONArray();
        JSONArray jarrRecords = new JSONArray();
        JSONArray jarrDateFields = new JSONArray();
        JSONObject jMeta = new JSONObject();
        try {
                String rcategory = "";
                boolean groupflag = false;
                boolean summaryflag = false;
                String reportdesc = "";
                String groupcolumn = "";
                CustomReportList customReportObj = null;
                String refTable = "";
                ArrayList filter_params = new ArrayList();
                ArrayList filter_names = new ArrayList();
                filter_names.add("c.reportno.rno");
                filter_params.add(reportno);
                HashMap<String, Object> requestParams = new HashMap<String, Object>();
                requestParams.put("filter_names", filter_names);
                requestParams.put("filter_params", filter_params);
                KwlReturnObject kmsg=reportBuilderDaoObj.getCustomReportConfig(requestParams);
                List<CustomReportColumns> ll = kmsg.getEntityList();
                for (CustomReportColumns obj : ll) {
                    customReportObj = obj.getReportno();
                    rcategory = customReportObj.getRcategory();
                    groupflag = (customReportObj.isGroupflag() && !detailFlag);
                    summaryflag = customReportObj.isSummaryflag();
                    if(!StringUtil.isNullOrEmpty(customReportObj.getRdescription())) {
                        reportdesc = customReportObj.getRdescription();
                    } else {
                        reportdesc = customReportObj.getRname();
                    }
                    String xtype = obj.getXtype();
                    if(groupflag) {
                       if(!obj.isGroupflag()){
                           if(!(xtype.equals("2") && !obj.getSummarytype().isEmpty())) {
                               continue;
                           }
                       }
                    }
                    jobjTemp = new JSONObject();
                    jobjTemp.put("header", obj.getDisplayname());
                    jobjTemp.put("tip", StringEscapeUtils.escapeHtml(obj.getDisplayname()));
//                    jobjTemp.put("title", obj.getDisplayname());// Not require 
                    jobjTemp.put("pdfwidth", 60);
//                    jobjTemp.put("sortable", true);
                    jobjTemp.put("dataIndex", obj.getDataIndex().replace(".", "#"));
                    if(groupflag) {
                        String dataindex ="";
                        if(obj.isGroupflag()) {
                            jobjTemp.put("groupcolumn", true);
                            groupcolumn = obj.getDataIndex().replace(".", "#");
//                            if(xtype.equals("8") && obj.getDataIndex().contains("customdata")) {
//                                refTableList.add(obj.getRefTable());
//                            }
                        }
                        if(!obj.getSummarytype().isEmpty()) {
                            dataindex = obj.getSummarytype()+"("+obj.getDataIndex().replace(".", "#")+"*1)";
                            jobjTemp.put("dataIndex", dataindex);
                        }
                    }
                    jobjTemp.put("renderer", StringUtil.isNullOrEmpty(obj.getRenderer())?"":getCustomRenderer(obj.getRenderer()));
                    jobjTemp.put("summaryType", StringUtil.isNullOrEmpty(obj.getSummarytype())?"":obj.getSummarytype());
                    String align = "left";
                    if(xtype.equals("2")) {//Number field
                        align = "right";
                    } else if(xtype.equals("3")){//Date field
                        align = "center";
                        jobjTemp.put("xtype", "datefield");
                    }
                    jobjTemp.put("align", align);
                    jarrColumns.put(jobjTemp);

                    jobjTemp = new JSONObject();
                    jobjTemp.put("name", obj.getDataIndex().replace(".", "#"));
                    if(groupflag) {
                        if(!obj.getSummarytype().isEmpty()) {
                            jobjTemp.put("name", obj.getSummarytype()+"("+obj.getDataIndex().replace(".", "#")+"*1)");
                        }
                        if(obj.isGroupflag()) {
                            JSONObject jobjTemp1 = new JSONObject();
                            jobjTemp1.put("name", obj.getDataIndex().replace(".", "#")+"_id");
                            jarrRecords.put(jobjTemp1);
                        }
                    }
                    if(xtype.equals("3")) {//For date field
                        jobjTemp.put("type", "date");
                        jobjTemp.put("dateFormat", "time");

                        JSONObject jobjTemp1 = new JSONObject();
                        jobjTemp1.put("dataindex", obj.getDataIndex());
                        jobjTemp1.put("displayname", obj.getDisplayname());
                        jarrDateFields.put(jobjTemp1);
                    }
                    jarrRecords.put(jobjTemp);
                    if(groupflag) {
                        if(!obj.getSummarytype().isEmpty()) {
                            dataIndexList.add(obj.getSummarytype()+"("+obj.getDataIndex()+"*1)");
                        } else {
                            dataIndexList.add(obj.getDataIndex());
                        }
                        groupCol.add(obj);
                    } else {
                        dataIndexList.add(obj.getDataIndex());
                    }
                    String rcategoryTable = "";
                    if(rcategory.equals(Constants.MODULE_LEAD)) {
                        rcategoryTable = Constants.Crm_lead;
                    }else if(rcategory.equals(Constants.MODULE_PRODUCT)) {
                        rcategoryTable = Constants.Crm_product;
                    }else if(rcategory.equals(Constants.MODULE_CONTACT)) {
                        rcategoryTable = Constants.Crm_contact;
                    }else if(rcategory.equals(Constants.MODULE_OPPORTUNITY)) {
                        rcategoryTable = Constants.Crm_opportunity;
                    }else if(rcategory.equals(Constants.MODULE_ACCOUNT)) {
                        rcategoryTable = Constants.Crm_account;
                    }else if(rcategory.equals(Constants.Crm_Case_modulename)) {
                        rcategoryTable = Constants.Crm_case;
                    }

                    if(!rcategoryTable.equals(obj.getDataIndex().split("\\.")[0])) {
                         refTableList.add(obj.getDataIndex().split("\\.")[0]);
                    }
                    refTableList.add(obj.getRefTable());
                    dataIndexReftableMap.put(obj.getDataIndex().replace(".", "#"), obj.getRefTable());

                    if(StringUtil.isNullOrEmpty(searchJson.toString())){
                        searchJson.append(obj.getReportno().getRfilterjson());
                    }
                    if(obj.isQuicksearch()) {
                        quickSearchCol.add(obj);
                    }
                }

                commData.put("columns", jarrColumns);
                if(jarrDateFields.length() == 0) {//Add date columns in Date filter dropdown
                    if(rcategory.equals(Constants.MODULE_LEAD)) {
                        refTable = Constants.Crm_lead;
                    }else if(rcategory.equals(Constants.MODULE_PRODUCT)) {
                        refTable = Constants.Crm_product;
                    }else if(rcategory.equals(Constants.MODULE_CONTACT)) {
                        refTable = Constants.Crm_contact;
                    }else if(rcategory.equals(Constants.MODULE_OPPORTUNITY)) {
                        refTable = Constants.Crm_opportunity;
                    }else if(rcategory.equals(Constants.MODULE_ACCOUNT)) {
                        refTable = Constants.Crm_account;
                    }else if(rcategory.equals(Constants.Crm_Case_modulename)) {
                        refTable = Constants.Crm_case;
                    }
                    JSONObject jobjTemp1 = new JSONObject();
                    jobjTemp1.put("dataindex", refTable+".createdon");
                    jobjTemp1.put("displayname", rcategory+" Creation Date");
                    jarrDateFields.put(jobjTemp1);
                }
                commData.put("datecolumns", jarrDateFields);
                commData.put("summaryflag", summaryflag);
                commData.put("groupflag", groupflag);
                commData.put("groupcolumn", groupcolumn);
                commData.put("reportdesc", reportdesc);
                jMeta.put("totalProperty", "totalCount");
                jMeta.put("root", "coldata");
                jMeta.put("fields", jarrRecords);
//                jMeta.put("sortInfo", "{field: 'crm_lead#industryid',direction: 'ASC'}");
                commData.put("metaData", jMeta);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return commData;
    }

    public static String getCustomRenderer(String rendererId) {
        return Constants.rendererData.get(rendererId);
    }
    
    @Override
    public JSONObject getModules() throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
             kmsg = reportBuilderDaoObj.getModules();
             List<Object[]> list = (List<Object[]>) kmsg.getEntityList();
             for(Object[] row : list){
                JSONObject t = new JSONObject();
                t.put("name", row[1]);
//                t.put("column", reportBuilderDaoObj.getTableColumn(row[0].toString()));
                t.put("column", "");
                t.put("modulename", row[1]);
                t.put("moduleid", row[0]);
                jobj.append("data", t);
             }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    @Override
    public JSONObject getRendererFunctions() throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
             kmsg = reportBuilderDaoObj.getRendererFunctions();
             List<Object[]> list = (List<Object[]>) kmsg.getEntityList();
             for(Object[] row : list){
                JSONObject jtemp = new JSONObject();
                jtemp.put("id", row[0]);
                jtemp.put("name", row[1]);
                jtemp.put("value", row[2]);
                jtemp.put("isstatic", row[3]);
                jobj.append("data", jtemp);
             }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    @Override
    public JSONObject saveReportConfig(String reportno,String rname,String runiquename,String rdescription,String rcategory,
            String reportcolumnsetting, String userid, String rfilterjson) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
             Map<String,Object> requestParams = new HashMap();
             requestParams.put("id", reportno);
             requestParams.put("rname", rname);
             requestParams.put("runiquename", runiquename);
             requestParams.put("rdescription", rdescription);
             requestParams.put("rcategory", rcategory);
             requestParams.put("rfilterjson", rfilterjson);
             requestParams.put("userid", userid);
             kmsg = reportBuilderDaoObj.saveReportDesc(requestParams);             
             List<CustomReportList> list =  kmsg.getEntityList();
             CustomReportList CustomReportListObj = list.get(0);
             kmsg = reportBuilderDaoObj.saveReportColumnConfig(reportcolumnsetting,CustomReportListObj,userid);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public boolean deleteCustomReport(int reportno) throws ServiceException {
        boolean successFlag = false;
        try {
            successFlag = reportBuilderDaoObj.deleteCustomReport(reportno);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return successFlag;
    }
        
    public JSONObject getModuleColumns(String companyid, String modulename) throws ServiceException {
        JSONArray jarr = new JSONArray();
        JSONObject jobj = new JSONObject();
        try {
            String tableName = getModuleTableName(modulename);
            String customtableName = getModuleCustomTableName(modulename);
            jarr = getModuleColumnsArray(jarr, companyid, modulename,tableName,customtableName);
            if(modulename.equals(Constants.MODULE_LEAD)) {
//                String tblname = getModuleTableName(Constants.MODULE_PRODUCT);
//                String cstmtblname = getModuleCustomTableName(Constants.MODULE_PRODUCT);
//                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_PRODUCT, tblname,cstmtblname);
            }else if(modulename.equals(Constants.MODULE_ACCOUNT)) {
//                String tblname = getModuleTableName(Constants.MODULE_PRODUCT);
//                String cstmtblname = getModuleCustomTableName(Constants.MODULE_PRODUCT);
//                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_PRODUCT, tblname,cstmtblname);

                String tblname = getModuleTableName(Constants.MODULE_LEAD);
                String cstmtblname = getModuleCustomTableName(Constants.MODULE_LEAD);
                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_LEAD, tblname,cstmtblname);
            }else if(modulename.equals(Constants.MODULE_CONTACT)) {
                String tblname = getModuleTableName(Constants.MODULE_ACCOUNT);
                String cstmtblname = getModuleCustomTableName(Constants.MODULE_ACCOUNT);
                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_ACCOUNT, tblname,cstmtblname);

                tblname = getModuleTableName(Constants.MODULE_LEAD);
                cstmtblname = getModuleCustomTableName(Constants.MODULE_LEAD);
                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_LEAD, tblname,cstmtblname);
            }else if(modulename.equals(Constants.MODULE_OPPORTUNITY)) {
//                String tblname = getModuleTableName(Constants.MODULE_PRODUCT);
//                String cstmtblname = getModuleCustomTableName(Constants.MODULE_PRODUCT);
//                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_PRODUCT, tblname,cstmtblname);

                String tblname = getModuleTableName(Constants.MODULE_ACCOUNT);
                String cstmtblname = getModuleCustomTableName(Constants.MODULE_ACCOUNT);
                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_ACCOUNT,tblname,cstmtblname);

                tblname = getModuleTableName(Constants.MODULE_LEAD);
                cstmtblname = getModuleCustomTableName(Constants.MODULE_LEAD);
                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_LEAD, tblname,cstmtblname);
            }else if(modulename.equals(Constants.Crm_Case_modulename)) {
//                String tblname = getModuleTableName(Constants.MODULE_PRODUCT);
//                String cstmtblname = getModuleCustomTableName(Constants.MODULE_PRODUCT);
//                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_PRODUCT, tblname, cstmtblname);

                String tblname = getModuleTableName(Constants.MODULE_ACCOUNT);
                String cstmtblname = getModuleCustomTableName(Constants.MODULE_ACCOUNT);
                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_ACCOUNT, tblname, cstmtblname);

                tblname = getModuleTableName(Constants.MODULE_CONTACT);
                cstmtblname = getModuleCustomTableName(Constants.MODULE_CONTACT);
                jarr = getModuleColumnsArray(jarr, companyid, Constants.MODULE_CONTACT, tblname, cstmtblname);
            }
            jobj.put("success", true);
            jobj.put("data", jarr);
            jobj.put("totalCount", jarr.length());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

    public String getModuleTableName(String modulename) {
        String tableName = "";
        if(modulename.equals(Constants.MODULE_ACCOUNT)) {
            tableName = Constants.Crm_account_table;
        } else if(modulename.equals(Constants.MODULE_LEAD)) {
            tableName = Constants.Crm_lead_table;
        } else if(modulename.equals(Constants.MODULE_CONTACT)) {
            tableName = Constants.Crm_contact_table;
        } else if(modulename.equals(Constants.MODULE_OPPORTUNITY)) {
            tableName = Constants.Crm_opportunity_table;
        } else if(modulename.equals(Constants.MODULE_PRODUCT)) {
            tableName = Constants.Crm_product_table;
        } else if(modulename.equals(Constants.MODULE_Campaign)) {
            tableName = Constants.Crm_campaign_table;
        } else if(modulename.equals(Constants.Crm_Case_modulename)) {
            tableName = Constants.Crm_case_table;
        }
        return tableName;
    }

     public String getModuleCustomTableName(String modulename) {
        String tableName = "";
        if(modulename.equals(Constants.MODULE_ACCOUNT)) {
            tableName = Constants.CRM_CUSTOM_ACCOUNT_TABLE;
        } else if(modulename.equals(Constants.MODULE_LEAD)) {
            tableName = Constants.CRM_CUSTOM_LEAD_TABLE;
        } else if(modulename.equals(Constants.MODULE_CONTACT)) {
            tableName = Constants.CRM_CUSTOM_CONTACT_TABLE;
        } else if(modulename.equals(Constants.MODULE_OPPORTUNITY)) {
            tableName = Constants.CRM_CUSTOM_OPPORTUNITY_TABLE;
        } else if(modulename.equals(Constants.MODULE_PRODUCT)) {
            tableName = Constants.CRM_CUSTOM_PRODUCT_TABLE;
        } else if(modulename.equals(Constants.Crm_Case_modulename)) {
            tableName = Constants.CRM_CUSTOM_CASE_TABLE;
        }
        return tableName;
    }


    public JSONArray getModuleColumnsArray(JSONArray jarr, String companyid, String modulename, String tableName,String customTablename) throws ServiceException {
        ArrayList filter_params = new ArrayList();
        ArrayList filter_names = new ArrayList();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            filter_names.add("dh.moduleName");
            filter_params.add(modulename);
            // No need to add configid check of owner. Now allowed owner in custom reports
//            filter_names.add("!dh.configid");
//            filter_params.add("1");
//            filter_names.add("dh.allowImport");
//            filter_params.add(true);
            filter_names.add("dh.customflag");
            filter_params.add(false);
            filter_names.add("NOTINdh.id");
            if(modulename.equalsIgnoreCase(Constants.Crm_Account_modulename)) {
                filter_params.add(Constants.CUSTOMREPORT_HIDEFIELD_ACCOUNT);
            }else if(modulename.equalsIgnoreCase(Constants.Crm_Lead_modulename)) {
                filter_params.add(Constants.CUSTOMREPORT_HIDEFIELD_LEAD);
            }else if(modulename.equalsIgnoreCase(Constants.Crm_Opportunity_modulename)) {
                filter_params.add(Constants.CUSTOMREPORT_HIDEFIELD_OPPORTUNITY);
            }else if(modulename.equalsIgnoreCase(Constants.Crm_Case_modulename)) {
                filter_params.add(Constants.CUSTOMREPORT_HIDEFIELD_CASE);
            }else {
                filter_params.add(Constants.CUSTOMREPORT_HIDEFIELD);
            }
            requestParams.put("filter_names", filter_names);
            requestParams.put("filter_values", filter_params);
            requestParams.put("companyid", companyid);
            requestParams.put("moduleName", modulename);

//            if(!quickinsertform){
//                requestParams.put("order_by", true);
//                requestParams.put("order_type", "defaultHeader");
//            } else {
                requestParams.put("order_by", true);
                requestParams.put("order_type", "fieldsequence");
//            }

            kmsg = fieldManagerDAOobj.getDefaultHeader(requestParams);
            int dl = kmsg.getRecordTotalCount();
            List<DefaultHeader> defaultHeaders = kmsg.getEntityList();
            List<String> headerIds = new ArrayList<String>();
            for (DefaultHeader obj: defaultHeaders) {
                headerIds.add(obj.getId());
            }

            Map<String, Object[]> results = getColumnHeaderMap(fieldManagerDAOobj, headerIds, companyid);
            for (DefaultHeader obj: defaultHeaders) {
                String refTableName = tableName;
                String dataIndex = "";
                String customcol ="Default Column";
                boolean isCustomColumn = false;
                if (results.containsKey(obj.getDefaultHeader()))
                {
                    Object[] mapEntry = results.get(obj.getDefaultHeader());
                    ColumnHeader obj1 = (ColumnHeader) mapEntry[1];
                    DefaultHeader obj2 = (DefaultHeader) mapEntry[0];

                    dataIndex = refTableName+"."+obj2.getDbcolumnname();
                    if(obj.isCustomflag()){
                        customcol ="Custom Column";
                        isCustomColumn = true;
                        refTableName = customTablename;
                        dataIndex = refTableName+"."+obj2.getDbcolumnname();
                        if(Integer.parseInt(obj.getXtype())==4 || Integer.parseInt(obj.getXtype())==7) {//Custom dropdown
                            refTableName = "fieldcombodata";
                        } else if(Integer.parseInt(obj.getXtype())==8) {//Custom reference dropdown
                            if(obj2.getRefModule_PojoClassName().equals("DefaultMasterItem")) {
                                refTableName = "defaultmasteritem";
                            } else if(obj2.getRefModule_PojoClassName().equals("User")) {
                                refTableName = "users";
                            }
                        }
                    }
                    // for default Combobox
                    if(Integer.parseInt(obj.getXtype())==4 && !(obj.isCustomflag())){
                        refTableName = "defaultmasteritem";
                        if(obj.getRefModule_PojoClassName()!=null && obj.getRefModule_PojoClassName().equals(Constants.Crm_account_pojo)) {
                            refTableName = Constants.Crm_account;
                        }
                    }

                    JSONObject jtemp = getHeaderObject(obj2);
                    jtemp.put("newheader", StringUtil.isNullOrEmpty(obj1.getNewHeader())? "" :obj1.getNewHeader());
                    jtemp.put("ismandotory",obj1.isMandotory());
                    jtemp.put("id", obj1.getId());
                    jtemp.put("configid", obj1.getDefaultheader().getConfigid());
                    jtemp.put("columntype", customcol);
                    jtemp.put("pojoname", obj2.getPojoheadername());
                    jtemp.put("iscustomcolumn", isCustomColumn);
                    jtemp.put("defaultname", obj2.getDefaultHeader());
                    jtemp.put("refcolumn_number",Constants.Custom_Column_Prefix+obj2.getDbcolumnrefname());
                    jtemp.put("column_number", StringUtil.isNullOrEmpty(obj2.getDbcolumnname())?"":obj2.getDbcolumnname());
                    jtemp.put("dbcolname", obj1.getDefaultheader().getDbcolumnname());
                    jtemp.put("column", StringUtil.isNullOrEmpty(obj1.getNewHeader())? obj2.getDefaultHeader() :obj1.getNewHeader());
                    jtemp.put("type", obj.getXtype());
                    jtemp.put("displayname",StringUtil.isNullOrEmpty(obj1.getNewHeader())? obj2.getDefaultHeader() :obj1.getNewHeader());
                    jtemp.put("modulename",modulename);
                    jtemp.put("tablename",refTableName);
                    jtemp.put("dataindex",dataIndex);
                    jarr.put(jtemp);
                } else if(!obj.isCustomflag()) {
                    dataIndex = refTableName+"."+obj.getDbcolumnname();
                    // for default Combobox
                    // Check config id == 1 for owner field and add rettable users
                    if(Integer.parseInt(obj.getXtype())==4  && obj.getConfigid().equals("1")) {
                            dataIndex = obj.getDbcolumnname();
                            refTableName = "users";
                    } else if(Integer.parseInt(obj.getXtype())==4 ){
                        refTableName = "defaultmasteritem";
                        if(obj.getRefModule_PojoClassName()!=null && obj.getRefModule_PojoClassName().equals(Constants.Crm_account_pojo)) {
                            refTableName = Constants.Crm_account;
                        }
                    }
                    JSONObject jtemp = getHeaderObject(obj);
                    jtemp.put("newheader", "");
                    jtemp.put("columntype", customcol);
                    jtemp.put("iscustomcolumn", isCustomColumn);
                    jtemp.put("pojoname", obj.getPojoheadername());
                    jtemp.put("ismandotory",obj.isMandatory());
                    jtemp.put("id","");
                    jtemp.put("defaultname", obj.getDefaultHeader());
                    jtemp.put("dbcolname", obj.getDbcolumnname());
                    jtemp.put("column", obj.getDefaultHeader());
                    jtemp.put("type", obj.getXtype());
                    jtemp.put("displayname",obj.getDefaultHeader());
                    jtemp.put("modulename",modulename);
                    jtemp.put("tablename",refTableName);
                    jtemp.put("dataindex",dataIndex);
                    jarr.put(jtemp);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jarr;
    }

    private JSONObject getHeaderObject(DefaultHeader obj) throws JSONException {
        JSONObject tmpObj = new JSONObject();
        tmpObj.put("header", obj.getDefaultHeader());
        tmpObj.put("headerid", obj.getId());
        tmpObj.put("defaultheader",obj.getDefaultHeader());
        tmpObj.put("pojoname", obj.getPojoheadername());
        tmpObj.put("xtype", obj.getXtype());
        tmpObj.put("required", obj.isRequired());
        tmpObj.put("recordname", obj.getRecordname());
        tmpObj.put("masterconfid", obj.getConfigid());
        tmpObj.put("customflag", obj.isCustomflag());
        tmpObj.put("modulename", obj.getModuleName());
        tmpObj.put("pojomethodname", obj.getPojoMethodName());
        tmpObj.put("dbcolumnname", obj.getDbcolumnname());
        tmpObj.put("maxlength", obj.getMaxLength());
        return tmpObj;
    }

    private static Map<String, Object[]> getColumnHeaderMap(fieldManagerDAO fieldManagerDAOobj, List<String> headerIds, String companyId)
    {
	    Map<String, Object[]> result = new HashMap<String, Object[]>();
	    List<Object[]> colList = fieldManagerDAOobj.getColumnHeader(companyId, headerIds);

	    if (colList != null)
	    {
	        for (Object[] col: colList)
	        {
	            DefaultHeader dh = (DefaultHeader) col[0];
	            result.put(dh.getDefaultHeader(), col);
	        }
	    }
        return result;
    }

    public StringBuilder buildSelectQuery(ArrayList<String> field_names) {
        StringBuilder selectQuery = new StringBuilder();
        if(field_names!=null) {
            for(int i = 0; i < field_names.size(); i++) {
                String fieldname = field_names.get(i);
                String fieldnameAlise = fieldname.replace(".", "#");                
                if(i == 0) {
//                    selectQuery.append("Select ");
                    selectQuery.append(" ").append(fieldname).append(" as `").append(fieldnameAlise).append("` ");
                } else {
                    selectQuery.append(", ").append(fieldname).append(" as `").append(fieldnameAlise).append("` ");
                }
            }
        }
        return selectQuery;
    }

    public StringBuilder buildJoinQuery(ArrayList<String> refTableList, String report_categoty) {
        StringBuilder joinQuery = new StringBuilder();
        HashMap<String, String> usedRefTableList = new HashMap<String, String>();
        if(report_categoty.equals(Constants.modulenameMap.get(Constants.Crm_Product_modulename))){//Product
            joinQuery.append(Constants.productJoinMap.get("mainQuery"));
            if(refTableList!=null) {
                for(int i = 0; i < refTableList.size(); i++) {
                    String reftable = refTableList.get(i);
                    if(reftable.equals("fieldcombodata") || reftable.equals("users")) {//For custom drop downs && users reference dropdown
                        reftable = Constants.CRM_CUSTOM_PRODUCT_TABLE;
                    }
                    if(usedRefTableList.containsKey(reftable)) {
                        continue;
                    }
                    if(reftable.equals(Constants.CRM_CUSTOM_PRODUCT_TABLE)) {
                        joinQuery.append(Constants.productJoinMap.get(Constants.CRM_CUSTOM_PRODUCT_TABLE));
                    }
                    usedRefTableList.put(reftable, reftable);
                }
            }
        }else if(report_categoty.equals(Constants.modulenameMap.get(Constants.Crm_Lead_modulename))){//Lead
            joinQuery.append(Constants.leadJoinMap.get("mainQuery"));
            if(refTableList!=null) {
                for(int i = 0; i < refTableList.size(); i++) {
                    String reftable = refTableList.get(i);
                    if(reftable.equals("fieldcombodata") || reftable.equals("users")) {//For custom drop downs && users reference dropdown
                        reftable = Constants.CRM_CUSTOM_LEAD_TABLE;
                    }
                    if(usedRefTableList.containsKey(reftable)) {
                        continue;
                    }
                    if(reftable.equals(Constants.Crm_product)) {
                        joinQuery.append(Constants.leadJoinMap.get(Constants.Crm_product));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_LEAD_TABLE)) {
                        joinQuery.append(Constants.leadJoinMap.get(Constants.CRM_CUSTOM_LEAD_TABLE));
                    }/*else if(reftable.equals("crm_leadOwners")) {
//                        joinQuery.append(Constants.leadJoinMap.get("users"));
                    }*/
                    usedRefTableList.put(reftable, reftable);
                }
            }
        }else if(report_categoty.equals(Constants.modulenameMap.get(Constants.Crm_Account_modulename))){//Account
            joinQuery.append(Constants.accountJoinMap.get("mainQuery"));
            if(refTableList!=null) {
                for(int i = 0; i < refTableList.size(); i++) {
                    String reftable = refTableList.get(i);
                    if(reftable.equals("fieldcombodata") || reftable.equals("users")) {//For custom drop downs && users reference dropdown
                        reftable = Constants.CRM_CUSTOM_ACCOUNT_TABLE;
                    }
                    if(usedRefTableList.containsKey(reftable)) {
                        continue;
                    }
                    if(reftable.equals(Constants.Crm_product)) {
                        joinQuery.append(Constants.accountJoinMap.get(Constants.Crm_product));
                    }else if(reftable.equals(Constants.Crm_lead)) {
                        joinQuery.append(Constants.accountJoinMap.get(Constants.Crm_lead));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_LEAD_TABLE)) {
                        if(!usedRefTableList.containsKey(Constants.Crm_lead)) {
                            joinQuery.append(Constants.accountJoinMap.get(Constants.Crm_lead));
                            usedRefTableList.put(Constants.Crm_lead, Constants.Crm_lead);
                        }
                        joinQuery.append(Constants.accountJoinMap.get(Constants.CRM_CUSTOM_LEAD_TABLE));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_ACCOUNT_TABLE)) {
                        joinQuery.append(Constants.accountJoinMap.get(Constants.CRM_CUSTOM_ACCOUNT_TABLE));
                    }
                    // already having inner join on owner table in main query
                    /*else if(reftable.equals("crm_accountOwners")) {
                        joinQuery.append(Constants.accountJoinMap.get("crm_accountOwners"));
                    }*/
                    usedRefTableList.put(reftable, reftable);
                }
            }
        }else if(report_categoty.equals(Constants.modulenameMap.get(Constants.Crm_Contact_modulename))){//Contact
            joinQuery.append(Constants.contactJoinMap.get("mainQuery"));
            if(refTableList!=null) {
                for(int i = 0; i < refTableList.size(); i++) {
                    String reftable = refTableList.get(i);
                    if(reftable.equals("fieldcombodata") || reftable.equals("users")) {//For custom drop downs && users reference dropdown
                        reftable = Constants.CRM_CUSTOM_CONTACT_TABLE;
                    }
                    if(usedRefTableList.containsKey(reftable)) {
                        continue;
                    }
                    if(reftable.equals(Constants.Crm_product)) {
                        joinQuery.append(Constants.contactJoinMap.get(Constants.Crm_product));
                    }else if(reftable.equals(Constants.Crm_lead)) {
                        joinQuery.append(Constants.contactJoinMap.get(Constants.Crm_lead));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_LEAD_TABLE)) {
                        if(!usedRefTableList.containsKey(Constants.Crm_lead)) {
                            joinQuery.append(Constants.contactJoinMap.get(Constants.Crm_lead));
                            usedRefTableList.put(Constants.Crm_lead, Constants.Crm_lead);
                        }
                        joinQuery.append(Constants.contactJoinMap.get(Constants.CRM_CUSTOM_LEAD_TABLE));
                    }else if(reftable.equals(Constants.Crm_account)) {
                        joinQuery.append(Constants.contactJoinMap.get(Constants.Crm_account));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_ACCOUNT_TABLE)) {
                        if(!usedRefTableList.containsKey(Constants.Crm_account)) {
                            joinQuery.append(Constants.contactJoinMap.get(Constants.Crm_account));
                            usedRefTableList.put(Constants.Crm_account, Constants.Crm_account);
                        }
                        joinQuery.append(Constants.contactJoinMap.get(Constants.CRM_CUSTOM_ACCOUNT_TABLE));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_CONTACT_TABLE)) {
                        joinQuery.append(Constants.contactJoinMap.get(Constants.CRM_CUSTOM_CONTACT_TABLE));
                    }
                    /*else if(reftable.equals("crm_contactOwners")) {
                        joinQuery.append(Constants.contactJoinMap.get("crm_contactOwners"));
                    }*/
                    usedRefTableList.put(reftable, reftable);
                }
            }
        }else if(report_categoty.equals(Constants.modulenameMap.get(Constants.Crm_Opportunity_modulename))){//Opportunity
            joinQuery.append(Constants.opportunityJoinMap.get("mainQuery"));
            if(refTableList!=null) {
                for(int i = 0; i < refTableList.size(); i++) {
                    String reftable = refTableList.get(i);
                    if(reftable.equals("fieldcombodata") || reftable.equals("users")) {//For custom drop downs && users reference dropdown
                        reftable = Constants.CRM_CUSTOM_OPPORTUNITY_TABLE;
                    }
                    if(usedRefTableList.containsKey(reftable)) {
                        continue;
                    }
                    if(reftable.equals(Constants.Crm_lead)) {
                        joinQuery.append(Constants.opportunityJoinMap.get(Constants.Crm_lead));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_LEAD_TABLE)) {
                        if(!usedRefTableList.containsKey(Constants.Crm_lead)) {
                            joinQuery.append(Constants.opportunityJoinMap.get(Constants.Crm_lead));
                            usedRefTableList.put(Constants.Crm_lead, Constants.Crm_lead);
                        }
                        joinQuery.append(Constants.opportunityJoinMap.get(Constants.CRM_CUSTOM_LEAD_TABLE));
                    }else if(reftable.equals(Constants.Crm_account)) {
                        joinQuery.append(Constants.opportunityJoinMap.get(Constants.Crm_account));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_ACCOUNT_TABLE)) {
                        if(!usedRefTableList.containsKey(Constants.Crm_account)) {
                            joinQuery.append(Constants.opportunityJoinMap.get(Constants.Crm_account));
                            usedRefTableList.put(Constants.Crm_account, Constants.Crm_account);
                        }
                        joinQuery.append(Constants.opportunityJoinMap.get(Constants.CRM_CUSTOM_ACCOUNT_TABLE));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_OPPORTUNITY_TABLE)) {
                        joinQuery.append(Constants.opportunityJoinMap.get(Constants.CRM_CUSTOM_OPPORTUNITY_TABLE));
                    }
                    /*else if(reftable.equals("crm_opportunityOwners")) {
                        joinQuery.append(Constants.opportunityJoinMap.get("crm_opportunityOwners"));
                    }*/
                    usedRefTableList.put(reftable, reftable);
                }
            }
        }else if(report_categoty.equals(Constants.modulenameMap.get(Constants.Crm_Case_modulename))){//Case
            joinQuery.append(Constants.caseJoinMap.get("mainQuery"));
            if(refTableList!=null) {
                for(int i = 0; i < refTableList.size(); i++) {
                    String reftable = refTableList.get(i);
                    if(reftable.equals("fieldcombodata") || reftable.equals("users")) {//For custom drop downs && users reference dropdown
                        reftable = Constants.CRM_CUSTOM_CASE_TABLE;
                    }
                    if(usedRefTableList.containsKey(reftable)) {
                        continue;
                    }
                    if(reftable.equals(Constants.Crm_account)) {
                        joinQuery.append(Constants.caseJoinMap.get(Constants.Crm_account));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_ACCOUNT_TABLE)) {
                        if(!usedRefTableList.containsKey(Constants.Crm_account)) {
                            joinQuery.append(Constants.caseJoinMap.get(Constants.Crm_account));
                            usedRefTableList.put(Constants.Crm_account, Constants.Crm_account);
                        }
                        joinQuery.append(Constants.caseJoinMap.get(Constants.CRM_CUSTOM_ACCOUNT_TABLE));
                    }else if(reftable.equals(Constants.Crm_contact)) {
                        joinQuery.append(Constants.caseJoinMap.get(Constants.Crm_contact));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_CONTACT_TABLE)) {
                        if(!usedRefTableList.containsKey(Constants.Crm_contact)) {
                            joinQuery.append(Constants.caseJoinMap.get(Constants.Crm_contact));
                            usedRefTableList.put(Constants.Crm_contact, Constants.Crm_contact);
                        }
                        joinQuery.append(Constants.caseJoinMap.get(Constants.CRM_CUSTOM_CONTACT_TABLE));
                    }else if(reftable.equals(Constants.CRM_CUSTOM_CASE_TABLE)) {
                        joinQuery.append(Constants.caseJoinMap.get(Constants.CRM_CUSTOM_CASE_TABLE));
                    }
                    usedRefTableList.put(reftable, reftable);
                }
            }
        }
        return joinQuery;
    }

     public JSONObject getLeadsDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByColumns, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm, String field, String direction, String start, String limit,
            StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            int moduleid = Constants.Crm_lead_moduleid;
            String masterIds = "'"+Constants.LEAD_SOURCEID+"',";
            masterIds += "'"+Constants.CAMPAIGN_SOURCEID+"',";
            masterIds += "'"+Constants.LEAD_STATUSID+"',";
            masterIds += "'"+Constants.LEAD_RATINGID+"',";
            masterIds += "'"+Constants.LEAD_INDUSTRYID+"'";

            StringBuilder selectQuery = buildSelectQuery(dataIndexList);
            StringBuilder joinQuery = buildJoinQuery(refTableList, report_category);
            String query = "";
            String countquery = "";
            if(!StringUtil.isNullOrEmpty(groupByColumns)) {
                query = "select " + selectQuery.append(joinQuery);
                countquery = " select count(*) "+joinQuery;
            } else {
                query = "select distinct crm_lead.leadid as leadid, " + selectQuery.append(joinQuery);
                countquery = " select count(distinct crm_lead.leadid) "+joinQuery;
            }
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("crm_lead.deleteflag");
            filter_params.add(0);
            filter_names.add("crm_lead.validflag");
            filter_params.add(1);
            filter_names.add("crm_lead.companyid");
            filter_params.add(companyid);
            filter_names.add("crm_lead.istransfered");
            filter_params.add(0);
            filter_names.add("crm_lead.isarchive");
            filter_params.add("F");
            String extraFilter = "(dm.mainID!='"+Constants.LEADSTATUSID_QUALIFIED+"' or dm.id is null)";
            if(!heirarchyPerm) {
                filter_names.add("INcrm_leadOwners.usersByUserid");
                filter_params.add(usersList);
            }            
            jobj = getDataCustomReport(companyid, searchJson, groupByColumns, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit,

                    moduleid, query, countquery, masterIds, quicksearchcol, filter_names, filter_params, dataIndexReftableMap,extraFilter);

        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

     public JSONObject getProductsDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByColumns, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm, String field, String direction, String start, String limit,
            StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            int moduleid = Constants.Crm_product_moduleid;
            String masterIds = "'"+Constants.PRODUCT_CATEGORYID+"'";
            StringBuilder selectQuery = buildSelectQuery(dataIndexList);
            StringBuilder joinQuery = buildJoinQuery(refTableList, report_category);
            String query = "";
            String countquery = "";
            if(!StringUtil.isNullOrEmpty(groupByColumns)) {
                query = "select " + selectQuery.append(joinQuery);
                countquery = " select count(*) "+joinQuery;
            } else {
                query = "select distinct crm_product.productid as productid, " + selectQuery.append(joinQuery);
                countquery = " select count(distinct crm_product.productid) "+joinQuery;
            }
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("crm_product.deleteflag");
            filter_params.add(0);
            filter_names.add("crm_product.validflag");
            filter_params.add(1);
            filter_names.add("crm_product.companyid");
            filter_params.add(companyid);
            filter_names.add("crm_product.isarchive");
            filter_params.add("F");

            if(!heirarchyPerm) {
                filter_names.add("INcrm_product.userid");
                filter_params.add(usersList);
            }
            jobj = getDataCustomReport(companyid, searchJson, groupByColumns, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit,
                    moduleid, query, countquery, masterIds, quicksearchcol, filter_names, filter_params, dataIndexReftableMap, new String());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

     public JSONObject getAccountsDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByColumns, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm, String field, String direction, String start, String limit,
            StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            int moduleid = Constants.Crm_account_moduleid;
            String masterIds = "'"+Constants.ACCOUNT_TYPEID+"',";
            masterIds += "'"+Constants.LEAD_INDUSTRYID+"',";
            masterIds += "'"+Constants.LEAD_SOURCEID+"',";
            masterIds += "'"+Constants.CAMPAIGN_SOURCEID+"',";
            masterIds += "'"+Constants.LEAD_STATUSID+"',";
            masterIds += "'"+Constants.LEAD_RATINGID+"'";
            StringBuilder selectQuery = buildSelectQuery(dataIndexList);
            StringBuilder joinQuery = buildJoinQuery(refTableList, report_category);
            String query = "";
            String countquery = "";
            if(!StringUtil.isNullOrEmpty(groupByColumns)) {
                query = "select " + selectQuery.append(joinQuery);
                countquery = " select count(*) "+joinQuery;
            } else {
                query = "select distinct crm_account.accountid as accountid, " + selectQuery.append(joinQuery);
                countquery = " select count(distinct crm_account.accountid) "+joinQuery;
            }
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("crm_account.deleteflag");
            filter_params.add(0);
            filter_names.add("crm_account.validflag");
            filter_params.add(1);
            filter_names.add("crm_account.companyid");
            filter_params.add(companyid);
            filter_names.add("crm_account.isarchive");
            filter_params.add("F");

            if(!heirarchyPerm) {
                filter_names.add("INcrm_accountOwners.usersByUserid");
                filter_params.add(usersList);
            }
            jobj = getDataCustomReport(companyid, searchJson, groupByColumns, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit,
                    moduleid, query, countquery, masterIds, quicksearchcol, filter_names, filter_params, dataIndexReftableMap, new String());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

     public JSONObject getContactsDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByColumns, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm, String field, String direction, String start, String limit,
            StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            int moduleid = Constants.Crm_contact_moduleid;
            String masterIds = "'"+Constants.ACCOUNT_TYPEID+"',";
            masterIds += "'"+Constants.LEAD_INDUSTRYID+"',";
            masterIds += "'"+Constants.LEAD_SOURCEID+"',";
            masterIds += "'"+Constants.CAMPAIGN_SOURCEID+"',";
            masterIds += "'"+Constants.LEAD_STATUSID+"',";
            masterIds += "'"+Constants.LEAD_RATINGID+"'";

            StringBuilder selectQuery = buildSelectQuery(dataIndexList);
            StringBuilder joinQuery = buildJoinQuery(refTableList, report_category);
            String query = "";
            String countquery = "";
            if(!StringUtil.isNullOrEmpty(groupByColumns)) {
                query = "select " + selectQuery.append(joinQuery);
                countquery = " select count(*) "+joinQuery;
            } else {
                query = "select distinct crm_contact.contactid as contactid, " + selectQuery.append(joinQuery);
                countquery = " select count(distinct crm_contact.contactid) "+joinQuery;
            }
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("crm_contact.deleteflag");
            filter_params.add(0);
            filter_names.add("crm_contact.validflag");
            filter_params.add(1);
            filter_names.add("crm_contact.companyid");
            filter_params.add(companyid);
            filter_names.add("crm_contact.isarchive");
            filter_params.add("F");

            if(!heirarchyPerm) {
                filter_names.add("INcrm_contactOwners.usersByUserid");
                filter_params.add(usersList);
            }
            jobj = getDataCustomReport(companyid, searchJson, groupByColumns, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit,
                    moduleid, query, countquery, masterIds, quicksearchcol, filter_names, filter_params, dataIndexReftableMap, new String());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

     public JSONObject getOpportunitiesDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByColumns, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm, String field, String direction, String start, String limit,
            StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            int moduleid = Constants.Crm_opportunity_moduleid;
            String masterIds = "'"+Constants.ACCOUNT_TYPEID+"',";
            masterIds += "'"+Constants.LEAD_INDUSTRYID+"',";
            masterIds += "'"+Constants.LEAD_SOURCEID+"',";
            masterIds += "'"+Constants.CAMPAIGN_SOURCEID+"',";
            masterIds += "'"+Constants.LEAD_STATUSID+"',";
            masterIds += "'"+Constants.LEAD_RATINGID+"',";
            masterIds += "'"+Constants.OPP_TYPEID+"',";
            masterIds += "'"+Constants.OPP_REGIONID+"',";
            masterIds += "'"+Constants.OPP_STAGEID+"'";

            StringBuilder selectQuery = buildSelectQuery(dataIndexList);
            StringBuilder joinQuery = buildJoinQuery(refTableList, report_category);
            String query = "";
            String countquery = "";
            if(!StringUtil.isNullOrEmpty(groupByColumns)) {
                query = "select " + selectQuery.append(joinQuery);
                countquery = " select count(*) "+joinQuery;
            } else {
                query = "select distinct crm_opportunity.oppid as oppid, " + selectQuery.append(joinQuery);
                countquery = " select count(distinct crm_opportunity.oppid) "+joinQuery;
            }
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("crm_opportunity.deleteflag");
            filter_params.add(0);
            filter_names.add("crm_opportunity.validflag");
            filter_params.add(1);
            filter_names.add("crm_opportunity.companyid");
            filter_params.add(companyid);
            filter_names.add("crm_opportunity.isarchive");
            filter_params.add("F");

            if(!heirarchyPerm) {
                filter_names.add("INcrm_opportunityOwners.usersByUserid");
                filter_params.add(usersList);
            }
            jobj = getDataCustomReport(companyid, searchJson, groupByColumns, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit,
                    moduleid, query, countquery, masterIds, quicksearchcol, filter_names, filter_params, dataIndexReftableMap, new String());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

     public JSONObject getCasesDataCustomReport(String companyid, String searchJson, String[] quicksearchcol, String groupByColumns, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm, String field, String direction, String start, String limit,
            StringBuffer usersList, String report_category, ArrayList<String> dataIndexList, ArrayList<String> refTableList, HashMap<String, String> dataIndexReftableMap) throws ServiceException {
        JSONObject jobj = new JSONObject();
        try {
            int moduleid = Constants.Crm_case_moduleid;
            String masterIds = "'"+Constants.ACCOUNT_TYPEID+"',";
            masterIds += "'"+Constants.LEAD_INDUSTRYID+"',";
            masterIds += "'"+Constants.LEAD_SOURCEID+"',";
            masterIds += "'"+Constants.CAMPAIGN_SOURCEID+"',";
            masterIds += "'"+Constants.LEAD_STATUSID+"',";
            masterIds += "'"+Constants.LEAD_RATINGID+"',";
            masterIds += "'"+Constants.CASE_PRIORITYID+"',";
            masterIds += "'"+Constants.CASE_STATUSID+"',";
            masterIds += "'"+Constants.CASE_TYPEID+"'";

            StringBuilder selectQuery = buildSelectQuery(dataIndexList);
            StringBuilder joinQuery = buildJoinQuery(refTableList, report_category);
            String query = "";
            String countquery = "";
            if(!StringUtil.isNullOrEmpty(groupByColumns)) {
                query = "select " + selectQuery.append(joinQuery);
                countquery = " select count(*) "+joinQuery;
            } else {
                query = "select distinct crm_case.caseid as caseid, " + selectQuery.append(joinQuery);
                countquery = " select count(distinct crm_case.caseid) "+joinQuery;
            }
            ArrayList filter_names = new ArrayList();
            ArrayList filter_params = new ArrayList();
            filter_names.add("crm_case.deleteflag");
            filter_params.add(0);
            filter_names.add("crm_case.validflag");
            filter_params.add(1);
            filter_names.add("crm_case.companyid");
            filter_params.add(companyid);
            filter_names.add("crm_case.isarchive");
            filter_params.add("F");

            if(!heirarchyPerm) {
                filter_names.add("INcrm_case.userid");
                filter_params.add(usersList);
            }
            jobj = getDataCustomReport(companyid, searchJson, groupByColumns, ss, filterCol, filterSS, detailFlag, fromDate, toDate, filterCombo, heirarchyPerm, field, direction, start, limit,
                    moduleid, query, countquery, masterIds, quicksearchcol, filter_names, filter_params, dataIndexReftableMap, new String());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

     public JSONObject getDataCustomReport(String companyid, String searchJson, String groupByColumns, String ss, String filterCol, String filterSS, boolean detailFlag, String fromDate, String toDate, String filterCombo, boolean heirarchyPerm, String field, String direction, String start, String limit,
            int moduleid, String query, String countquery, String masterIds, String[] quicksearchcol, ArrayList filter_names, ArrayList filter_params, HashMap<String, String> dataIndexReftableMap, String extraFilter) throws ServiceException {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            if (!StringUtil.isNullOrEmpty(fromDate) && !StringUtil.isNullOrEmpty(toDate) && !StringUtil.isNullOrEmpty(filterCombo)){
                requestParams.put("fromDate", fromDate);
                requestParams.put("toDate", toDate);
                requestParams.put("filterCombo", filterCombo);
            }
            requestParams.put("ss", ss);
            if (!StringUtil.isNullOrEmpty(filterCol) && !StringUtil.isNullOrEmpty(filterSS)) {
                requestParams.put("filterCol", filterCol);
                requestParams.put("filterSS", filterSS);
                requestParams.put("detailFlag", detailFlag);
            }
            requestParams.put("searchJson", searchJson);
            if(!StringUtil.isNullOrEmpty(start)) {
                requestParams.put("start", StringUtil.checkForNull(start));
                requestParams.put("limit", StringUtil.checkForNull(limit));
                requestParams.put("pagingFlag", true);
            } else {
                requestParams.put("pagingFlag", false);
            }
            requestParams.put("companyid", companyid);
            if(!StringUtil.isNullOrEmpty(field)) {
                requestParams.put("field", field);
                requestParams.put("direction", direction);
            }
            requestParams.put("heirarchyPerm", heirarchyPerm);
            requestParams.put("query", query);
            requestParams.put("countquery", countquery);
            if(!StringUtil.isNullOrEmpty(groupByColumns)) {
                requestParams.put("groupByColumns", groupByColumns);
            }
            if(!StringUtil.isNullOrEmpty(extraFilter)) {
                requestParams.put("extrafilter", extraFilter);
            }
            requestParams.put(Constants.moduleid, moduleid);
            requestParams.put("quicksearchcol", quicksearchcol);
            kmsg = reportBuilderDaoObj.getDataCustomReport(requestParams, filter_names, filter_params);
            SqlRowSet rs = (SqlRowSet) kmsg.getEntityList().get(0);

            //ToDo- Get ids of crmCombomaster table - Only fetch those combo which are present in report.
            filter_names.clear();
            filter_params.clear();
            filter_names.add("INc.crmCombomaster");
            filter_params.add(masterIds);
            filter_names.add("c.company.companyID");
            filter_params.add(companyid);
            requestParams.put(Constants.filter_names, filter_names);
            requestParams.put(Constants.filter_params, filter_params);
            HashMap<String, DefaultMasterItem> defaultMasterMap = getCrmCommonDAO().getDefaultMasterItemsMap(requestParams);
            //ToDo - fetch map only custom drop-down columns present in the report. Add companyid filter while fetching customComboMap.
            requestParams.clear();
            HashMap<String, FieldComboData> customComboMap = getCrmCommonDAO().getCustomComboItemsMap(requestParams);
            
            List<Object> ll = getIdsList(rs);
            List<String> idsList = (List<String>) ll.get(0);
            Boolean productColFlag = (Boolean) ll.get(1);
            
//             Map<String, List<LeadOwnerInfo>> ownersMap = crmLeadDAOObj.getLeadOwners(idsList);
            Map ownersMap = new HashMap();
            Map<String, List<CrmProduct>> productsMap = new HashMap<String, List<CrmProduct>> ();
            if(productColFlag) {
                switch(moduleid) {
                    case 1: //Account
                            productsMap = crmAccountDAOObj.getAccountProducts(idsList);
                            break;
                    case 2: //Lead
//                            ownersMap = crmLeadDAOObj.getLeadOwners(idsList);
                            productsMap = crmLeadDAOObj.getLeadProducts(idsList);
                            break;
                    case 3: //Case
                            productsMap = crmCaseDAOObj.getCaseProducts(idsList);
                            break;
                    case 5: //Opportunity
                            productsMap = crmOpportunityDAOObj.getOpportunityProducts(idsList);
                            break;
                }
            }
            
            jobj = getReportJSON(moduleid, rs, defaultMasterMap, customComboMap, productsMap, 
                    ownersMap, dataIndexReftableMap, detailFlag, productColFlag);
            jobj.put("totalCount", kmsg.getRecordTotalCount());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return jobj;
    }

     public List getIdsList(SqlRowSet rs) {
        List<Object> ll = new ArrayList<Object>();
        List<String> idList = new ArrayList<String>();
        Boolean productColFlag = false;
        try {
            SqlRowSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String columnLabel = rsmd.getColumnLabel(i);
                    if(columnLabel.equals(Constants.Crm_leadid) || columnLabel.equals(Constants.Crm_productid) || 
                            columnLabel.equals(Constants.Crm_accountid) || columnLabel.equals(Constants.Crm_contactid) || 
                            columnLabel.equals(Constants.Crm_caseid) || columnLabel.equals(Constants.Crm_opportunityid)) {
                        if(rs.getObject(i)!=null) {
                            idList.add(rs.getObject(i).toString());
                        }
                    }else if(columnLabel.equals(Constants.Crm_lead_product_key) ||
                            columnLabel.equals(Constants.Crm_opportunity_product_key) ||
                            columnLabel.equals(Constants.Crm_account_product_key) ||
                            columnLabel.equals(Constants.Crm_case_product_key)) {
                        productColFlag = true;
                    }
                }
            }
            rs.beforeFirst();
            ll.add(idList);
            ll.add(productColFlag);
        } catch (Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);
        }
        return ll;
    }

    public JSONObject getReportJSON(int moduleId, SqlRowSet rs, HashMap<String, DefaultMasterItem> defaultMasterMap, HashMap<String, FieldComboData> customComboMap, 
            Map<String, List<CrmProduct>> productsMap, Map ownersMap, HashMap<String, String> dataIndexReftableMap, boolean detailFlag, Boolean productColFlag) {
        JSONObject jb = new JSONObject();
        try {
            SqlRowSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                String primaryKeyVal = "";
                JSONObject jobj = new JSONObject();
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    String columnLabel = rsmd.getColumnLabel(i);
//                    boolean linkFlag = !detailFlag && customReportObj.isGroupflag() &&  columnLabel.equals(customReportObj.getGroupfield());
                    if(rs.getObject(i)!=null) {
                        if(columnLabel.equals(Constants.Crm_leadid) ||
                                columnLabel.equals(Constants.Crm_contactid) ||
                                columnLabel.equals(Constants.Crm_accountid) ||
                                columnLabel.equals(Constants.Crm_caseid) ||
                                columnLabel.equals(Constants.Crm_opportunityid) ||
                                columnLabel.equals(Constants.Crm_productid)) {
                            primaryKeyVal = rs.getObject(i).toString();
                        }

                        if(dataIndexReftableMap.containsKey(columnLabel)
                                && dataIndexReftableMap.get(columnLabel).equals("defaultmasteritem")) {
                            if(defaultMasterMap.containsKey(rs.getObject(i))) {
                                jobj.put(columnLabel, defaultMasterMap.get(rs.getObject(i)).getValue());
                                jobj.put(columnLabel+"_id", rs.getObject(i));
                            } else {
                                jobj.put(columnLabel, "");
                            }
                        }else if(dataIndexReftableMap.containsKey(columnLabel)
                                && dataIndexReftableMap.get(columnLabel).equals("users")) {
                            User userObj = (User) kwlCommonTablesDAOObj.getClassObject("com.krawler.common.admin.User", rs.getObject(i).toString());
                            if(userObj != null) {
                                jobj.put(columnLabel, userObj.getFullname());
                                jobj.put(columnLabel+"_id", rs.getObject(i));
                            } else {
                                jobj.put(columnLabel, "(Blank)");
                            }
                        } else if(dataIndexReftableMap.containsKey(columnLabel)
                                && dataIndexReftableMap.get(columnLabel).equals(Constants.Crm_account)) {
                            CrmAccount accObj = (CrmAccount) kwlCommonTablesDAOObj.getClassObject(Constants.Crm_account_classpath, rs.getObject(i).toString());
                            if(accObj != null) {
                                jobj.put(columnLabel, accObj.getAccountname());
                                jobj.put(columnLabel+"_id", rs.getObject(i));
                            } else {
                                jobj.put(columnLabel, rs.getObject(i));
                            }
                        } else if(dataIndexReftableMap.containsKey(columnLabel)
                                && dataIndexReftableMap.get(columnLabel).equals("fieldcombodata")) {
                            String[] comboIds = rs.getObject(i).toString().split(",");
                            String comboValue = " ";
                            String comboValueId = "";
                            for(int cnt = 0; cnt < comboIds.length; cnt++) {
                                String comboid = comboIds[cnt];
                                if(customComboMap.containsKey(comboid)) {
                                    comboValue += customComboMap.get(comboid).getValue() + ",";
                                    comboValueId += customComboMap.get(comboid).getId() + ",";
                                }
                            }
                            jobj.put(columnLabel, comboValue.substring(0, comboValue.length()-1));
                            jobj.put(columnLabel+"_id", (comboValueId.length()>0?comboValueId.substring(0, comboValueId.length()-1):comboValueId));
                        } else {
                            jobj.put(columnLabel, rs.getObject(i));
                        }
                    } else {
                        jobj.put(columnLabel,"");
                    }
                }
                if(productColFlag && productsMap.containsKey(primaryKeyVal)) {                    
                    List<CrmProduct> crmProducts = productsMap.get(primaryKeyVal);
                    String productNames = "";
                    for(CrmProduct product : crmProducts) {
                        productNames += product.getProductname()+", ";
                    }
                    String key = "";
                    productNames = productNames.substring(0, productNames.lastIndexOf(","));
                    switch(moduleId) {
                        case 1: key = Constants.Crm_account_product_key; break;
                        case 2: key = Constants.Crm_lead_product_key; break;                        
                        case 3: key = Constants.Crm_case_product_key; break;
                        case 5: key = Constants.Crm_opportunity_product_key; break;
                    }
                    jobj.put(key, productNames);
                }
                jb.append("data", jobj);
            }
        } catch (Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);
        }
        return jb;
    }
}
