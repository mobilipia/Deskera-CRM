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

import com.krawler.utils.json.base.JSONArray;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Johnson
 *
 */
public interface CrmCommonService
{

    /**
     * @param arrayId
     * @param modulename
     * @param companyid
     */
    public void validateMassupdate(String arrayId[], String modulename, String companyid);
    
    /**
     * @param modulename
     * @param companyid
     */
    public void validateMassupdate(String modulename, String companyid);
    
    /**
     * @param modulename
     * @param companyid
     * @param newheader
     * @param headerid
     * @return
     */
    public boolean columnExists(String modulename, String companyid, String newheader, String headerid);

    /**
     * 
     * @param jArr
     * @return String
     */
    public void saveMasterDataSequence(Map<String,Integer> map,String customflag);   
    
    /**
     * 
     *@param companyid
     *@param ownerid
     *@return 
     */
    public void saveDefaultCaseOwner(String companyid, String ownerid);
    
    /**
     * 
     * @param companyid
     * @return String
     */
    public String getDefaultCaseOwner(String companyid);

    public JSONArray getModuleColumns(HashMap<String, Object> requestParams, String companyid, String modulName);
}
