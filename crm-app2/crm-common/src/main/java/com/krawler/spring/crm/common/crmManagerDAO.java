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
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.spring.common.KwlReturnObject;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface crmManagerDAO {
    public StringBuffer recursiveUsers(String userid) throws ServiceException ;
    public StringBuffer recursiveManagerUsers(String userid) throws ServiceException ;
    public List getComboData(String comboname,HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getComboDataPaging(String comboname,HashMap<String, Object> requestParams) throws ServiceException ;
    public KwlReturnObject getComboMasterData() throws ServiceException;
    public KwlReturnObject getMasterIDCompany(String companyid, String mainID) throws ServiceException ;
    public String preferenceDate(HttpServletRequest request, Date date, int timeflag) throws ServiceException ;
    public String userPreferenceDate(HttpServletRequest request, Date date, int timeflag) throws ServiceException ;
    public String currencyRender(String currency, String currencyid) throws ServiceException ;
     public String currencySymbol( String currencyid) throws ServiceException ;
    public KwlReturnObject addMasterData(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject editMasterData(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject deleteMasterData(HashMap<String, Object> requestParams) throws ServiceException;
    public KwlReturnObject getMaster(HashMap<String, Object> requestParams) throws ServiceException;
    
    public KwlReturnObject setCompanyPref(HashMap hm) throws ServiceException;
    public String preferenceDatejsformat(String timeZoneDiff, Date date, DateFormat sdf) throws ServiceException;
    public String preferenceDatejsformat(Date date, DateFormat sdf) throws ServiceException;
    public Date converttz(String timeZoneDiff,Date dt, String time);

    /**
     * @working return false if campaign is archived otherwise returns true
     * @param requestParams contains companyid , campaignid
     * @return true or false
     * @throws ServiceException
     */
    boolean isCrmCampaignArchived(HashMap<String, Object> requestParams) throws ServiceException ;
    public Boolean checkMasterDataisUsed(String mainId, String id, String leadsource_Combo, String companyid) throws ServiceException ;
    
    StringBuffer recursiveUserIds(String userid) throws ServiceException;
    public void recursiveManagerUsers(String empID, StringBuffer appendUser,List appendUserList, List appendList, int exceptionAt, String extraQuery) throws ServiceException;
    public void saveMasterDataSequence(String id,int seq,String customflag);
    public String getComboName( String configid) throws ServiceException ;
    public void saveDefaultCaseOwner(String companyid, String ownerid);
    public String getDefaultCaseOwner(String companyid);
    public Boolean checkForDuplicateEntryInMasterData(String name, String configid, String companyid, String id)throws ServiceException;
    KwlReturnObject getUnAssignedLeadRoutingUsers(String companyid, HashMap<String, Object> requestParams) throws ServiceException;
    KwlReturnObject getAssignedLeadRoutingUsers(String companyid, HashMap<String, Object> requestParams) throws ServiceException;
    void deleteLeadRoutingUsers(String companyid) throws ServiceException;
    void addLeadRoutingUsers(String[] delList) throws ServiceException;
    List<User> getNextLeadRoutingUsers(String companyid) throws ServiceException;
    void setLastUsedFlagForLeadRouting(String userid, String companyid) throws ServiceException;
}
