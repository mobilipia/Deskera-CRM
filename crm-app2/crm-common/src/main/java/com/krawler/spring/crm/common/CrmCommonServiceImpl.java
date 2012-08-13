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


import com.krawler.common.admin.ColumnHeader;
import com.krawler.common.admin.DefaultHeader;
import com.krawler.common.util.StringUtil;
import com.krawler.customFieldMaster.fieldManagerDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Johnson
 *
 */
public class CrmCommonServiceImpl implements CrmCommonService
{

    private crmCommonDAO commonDAO;
    private crmManagerDAO managerDAO;
    private fieldManagerDAO fieldManagerDAOobj;

    public void setFieldManagerDAO(fieldManagerDAO fieldManagerDAOobj) {
        this.fieldManagerDAOobj = fieldManagerDAOobj;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.CrmCommonService#ValidateMassupdate(java.lang.String[], java.lang.String, java.lang.String)
     */
    public void validateMassupdate(String arrayId[], String modulename, String companyid){
        commonDAO.ValidateMassupdate(arrayId, modulename, companyid);
    }
    
    @Override
    public void validateMassupdate(String modulename, String companyid)
    {
        commonDAO.ValidateMassupdate(modulename, companyid);
        
    }
    
    /**
     * @param commonDAO the commonDAO to set
     */
    public void setCommonDAO(crmCommonDAO commonDAO)
    {
        this.commonDAO = commonDAO;
    }
    public void setManagerDAO(crmManagerDAO managerDAO)
    {
        this.managerDAO = managerDAO;
    }

    /* (non-Javadoc)
     * @see com.krawler.spring.crm.common.CrmCommonService#columnExists(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean columnExists(String modulename, String companyid, String newheader, String headerid)
    {
        return commonDAO.columnExists(modulename, companyid, newheader, headerid);
    }
    
	public void saveMasterDataSequence(Map<String, Integer> seq,String customflag) {
			for (Map.Entry<String, Integer> entry:seq.entrySet()) {
				managerDAO.saveMasterDataSequence(entry.getKey(), entry.getValue(), customflag);
			}
	}

	@Override
	public void saveDefaultCaseOwner(String companyid, String ownerid) {
		managerDAO.saveDefaultCaseOwner(companyid,ownerid);
	}

	@Override
	public String getDefaultCaseOwner(String companyid) {
		return managerDAO.getDefaultCaseOwner(companyid);
	}

    @Override
    public JSONArray getModuleColumns(HashMap<String, Object> requestParams, String companyid, String modulName) {
        JSONArray jArr = new JSONArray();
        try {
            KwlReturnObject kmsg = fieldManagerDAOobj.getDefaultHeader(requestParams);
            List<DefaultHeader> defaultHeaders = kmsg.getEntityList();
            List<String> headerIds = new ArrayList<String>();
            for (DefaultHeader obj: defaultHeaders) {
                headerIds.add(obj.getId());
            }

            Map<String, Object[]> results = getColumnHeaderMap(headerIds, companyid);

            for (DefaultHeader obj: defaultHeaders) {
                if (results.containsKey(obj.getDefaultHeader()))
                {
                    Object[] mapEntry = results.get(obj.getDefaultHeader());
                    ColumnHeader obj1 = (ColumnHeader) mapEntry[1];
                    DefaultHeader obj2 = (DefaultHeader) mapEntry[0];
                    JSONObject jtemp = getObject(obj2);
                    jtemp.put("columnName", StringUtil.isNullOrEmpty(obj1.getNewHeader())? obj2.getDefaultHeader() :obj1.getNewHeader());
                    jtemp.put("isMandatory", obj1.isMandotory());
                    jArr.put(jtemp);
                }
                else if(!obj.isCustomflag()) {
                    JSONObject jtemp = getObject(obj);
                    jArr.put(jtemp);
                }
            }

        } catch (JSONException ex) {
            Logger.getLogger(CrmCommonServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jArr;
    }

	/**
	 * @param dh
	 * @return
	 * @throws JSONException
	 */
	private static JSONObject getObject(DefaultHeader dh) throws JSONException {
        JSONObject jtemp = new JSONObject();
        jtemp.put("id", dh.getId());
        jtemp.put("columnName", dh.getDefaultHeader());
        jtemp.put("pojoName", dh.getPojoMethodName());
        jtemp.put("isMandatory", dh.isMandatory());
        jtemp.put("isNotNull", dh.isHbmNotNull());
        jtemp.put("maxLength", dh.getMaxLength());
        jtemp.put("defaultValue", dh.getDefaultValue());
        jtemp.put("validatetype", dh.getValidateType());
        jtemp.put("refModule", dh.getRefModule_PojoClassName());
        jtemp.put("refFetchColumn", dh.getRefFetchColumn_HbmName());
        jtemp.put("refDataColumn", dh.getRefDataColumn_HbmName());
        jtemp.put("customflag", dh.isCustomflag());
        jtemp.put("pojoHeader", dh.getPojoheadername());
        jtemp.put("recordname", dh.getRecordname());
        jtemp.put("xtype", dh.getXtype());
        jtemp.put("configid", dh.getConfigid());
        jtemp.put("refcolumn_number",dh.getDbcolumnrefname());
        jtemp.put("dbcolumnname",dh.getDbcolumnname());
        return jtemp;
    }
    
    private Map<String, Object[]> getColumnHeaderMap(List<String> headerIds, String companyId)
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
}
