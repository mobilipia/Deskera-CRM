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


/**
 * A place to keep commonly-used constants.
 */
public class CrmCommonConstants {
    //Image Path
    
    public static final String Crm_configid = "configid";
    public static final String Crm_parentid = "parentid";
    public static final String Crm_companyid = "companyid";
    public static final String Crm_id = "id";
    public static final String Crm_Crm = "Crm";
    public static final String Crm_hql = "hql";
    public static final String Crm_deleteflag = "deleteflag";
    public static final String Crm_CustomDataobj = "CustomDataobj";
    public static final String Crm_company_companyID = "company.companyID";
    public static final String Crm_defaultheader_configidD = "defaultheader.configid";
    public static final String Crm_getCrmCampaignData_Hql = " select isarchive from CrmCampaign c where c.deleteflag=0 and  c.company.companyID= ? and c.validflag=1 and c.campaignid=? and c.isarchive=? ";
    public static final String Crm_getComboData_Hql1 =  "select d from DefaultMasterItem d";
    public static final String Crm_getComboData_Hql2 =  " and (d.crmCombomaster.comboname =? or (d.crmCombomaster.comboname =? and d.mainID in (select c.campaignid from CrmCampaign c where c.deleteflag=0 and  c.company.companyID= ? and c.validflag=1 ";
    public static final String Crm_getComboData_Hql3 =  " and d.crmCombomaster.comboname =? ";
    public static final String Crm_getComboData_Hql4 =  " and d.deleteflag=? ";
}
