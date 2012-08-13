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

package com.krawler.spring.importFunctionality;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.DataInvalidateException;
import com.krawler.common.util.StringUtil;
import com.krawler.dao.BaseDAO;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.spring.common.KwlReturnObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ImportImpl extends BaseDAO implements ImportDAO {
    @Override
    public Object saveRecord(HashMap<String, Object> requestParams, HashMap<String, Object> dataMap, Object csvReader, String modeName, String classPath, String primaryKey, Object extraObj, JSONArray customfield) throws DataInvalidateException, SecurityException, IllegalArgumentException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return objectSetterMethod(true,dataMap, classPath, primaryKey);
    }
    public Object objectSetterMethod(boolean isImport, HashMap<String,Object> requestParams, String classstr, String primarykey) throws DataInvalidateException {
        Object obj = null;
        Object column = null;
        try {
            Class cl = Class.forName(classstr);
            if(requestParams.get(primarykey)!=null)
                obj = get(cl, requestParams.get(primarykey).toString());
            else{
                obj = cl.newInstance();
                Method setter = cl.getMethod("set"+primarykey,String.class);
                String id = UUID.randomUUID().toString();
                setter.invoke(obj, id);
            }
            for(Object key: requestParams.keySet()){
                column = key;
                Class rettype = cl.getMethod("get"+key).getReturnType();
                Method setter = cl.getMethod("set"+key,rettype);
                if(requestParams.get(key)!=null){
                    if(rettype.isPrimitive()||rettype.equals(String.class)||rettype.equals(Date.class)||rettype.equals(Integer.class)||rettype.equals(Boolean.class)){
                        // handled case for Revenue/Prize column in Lead/Account/Opportunity/Product
                        if(rettype.equals(String.class) && Number.class.isInstance(requestParams.get(key)))
                            setter.invoke(obj, String.valueOf(requestParams.get(key)));
                        else if(rettype.equals(String.class) && isImport){
                            String keyVal = String.valueOf(requestParams.get(key)).replaceAll("[\n\r]", " ");
                            setter.invoke(obj,keyVal);
                        }
                        else
                            setter.invoke(obj, requestParams.get(key));
                    }else{
                        setter.invoke(obj,get(rettype, requestParams.get(key).toString()));
                    }
                }
            }
           save(obj);
        } catch (ClassNotFoundException ex) {
            logger.warn(ex.getMessage(), ex);
            throw new DataInvalidateException(classstr+" class not found, check pojo class path from module setting.");
        } catch (NoSuchMethodException ex) {
            logger.warn(ex.getMessage(), ex);
            throw new DataInvalidateException("Incorrect pojo method name for column "+column);
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            throw new DataInvalidateException(""+ex.getMessage());
        }
        return obj;
    }
    @Override
    public Object saveImportLog(HashMap<String, Object> dataMap) throws SecurityException, IllegalArgumentException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return setterMethod(dataMap, "com.krawler.common.admin.ImportLog", "Id");
    }

    @Override
    public KwlReturnObject getImportLog(HashMap<String, Object> requestParams) throws ServiceException {
        ArrayList params = new ArrayList();
        params.add(requestParams.get("startdate"));
        params.add(requestParams.get("enddate"));
        params.add(requestParams.get("companyid"));
        String moduleid = requestParams.get("moduleid").toString();
        String query = "from ImportLog where (importDate>=? and importDate<=?) and company.companyID = ? ";
        if(!StringUtil.isNullOrEmpty(moduleid)){
            query += " and module.id in (" + moduleid + ")";
        }
        query +=" order by importDate desc";
        List list = executeQuery(query, params.toArray());
        int count = list.size();
        int start = Integer.parseInt(requestParams.get("start").toString());
        int limit = Integer.parseInt(requestParams.get("limit").toString());
        list = executeQueryPaging(query, params.toArray(), new Integer[]{start,limit});
        return new KwlReturnObject(true, null, null, list, count);
    }

    @Override
    //First value in filterValues must be name value of master combo for creating the master data in case it is not present.
    public List getRefModuleData(HashMap<String, Object> requestParams, String module, String fetchColumn, String comboConfigid, ArrayList<String> filterNames, ArrayList<Object> filterValues) throws ServiceException, DataInvalidateException {
        if(filterNames.size()!=filterValues.size()) {
            throw new DataInvalidateException("Count of Filternames and Filterparams are not same for module "+module);
        }
        String query = "select "+fetchColumn+" from "+module;
        try{
            String filter = StringUtil.filterQuery(filterNames, "where");
            int ind = filter.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(filter.substring(ind+1,ind+2));
                filter = filter.replaceAll("("+index+")", filterValues.get(index).toString());
                filterValues.remove(index);
            }
            return executeQuery(query+filter, filterValues.toArray());
        } catch(Exception e) {
            throw ServiceException.FAILURE("ImportImpl. : getRefModuleData" + e.getMessage(), e);
        }
    }

    @Override
    public List getCustomComboID(HashMap<String, Object> requestParams, String fetchColumn, ArrayList filterNames, ArrayList filterValues) throws ServiceException, DataInvalidateException {
        try{
//            "SELECT id FROM fieldComboData where name = ? and fieldid = ?"
            String query =  "SELECT "+fetchColumn+" FROM fieldcombodata ";
            String filter = StringUtil.filterQuery(filterNames, "where");
            query+=filter;
            int ind = query.indexOf("(");
            if(ind>-1){
                int index = Integer.valueOf(query.substring(ind+1,ind+2));
                query = query.replace("("+index+")", "("+filterValues.get(index).toString()+")");
                filterValues.remove(index);
            }
            if(filterValues==null){
                filterValues = new ArrayList();
            }
            return executeNativeQuery(query, filterValues.toArray());
        } catch(Exception e) {
            throw ServiceException.FAILURE("ImportImpl. : getCustomComboID" + e.getMessage(), e);
        }
    }

    public KwlReturnObject getColumnHeader(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = " from  ColumnHeader  " ;
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
                 int ind = hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(hql.substring(ind+1,ind+2));
                    hql = hql.replace("("+index+")", "("+value.get(index).toString()+")");
                    value.remove(index);
                }
            }

            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>)requestParams.get("order_type"));
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = executePagingQuery(requestParams, searchCol, hql, value);

        } catch (Exception ex) {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally {
            return result;
        }
    }

    public List<Object[]> getColumnHeader(String companyId, List<String> headerIds) {
        List<Object[]> results = null;
        try
        {
            StringBuilder hql = new StringBuilder();
            StringBuilder headerStr = new StringBuilder();
            if (headerIds == null || headerIds.isEmpty())
            {
                return null;
            }

            for (String id : headerIds)
            {
                headerStr.append('\'').append(id).append("\',");
            }

            headerStr.deleteCharAt(headerStr.length() - 1);

            hql.append("select c.defaultheader, c from  ColumnHeader c where c.defaultheader.id in (").append(headerStr).append(") ").append("and c.company.companyID = '").append(companyId).append('\'');

            results = executeQuery(hql.toString());

        } catch (Exception ex)
        {
            logger.warn("ex", ex);
        }
        return results;
    }

    public KwlReturnObject getDefaultHeader(HashMap<String, Object> requestParams) {
        KwlReturnObject result = null;
        try {
            ArrayList name = null;
            String hql = "";
            ArrayList value = null;
            ArrayList orderby = null;
            ArrayList ordertype = null;
            String[] searchCol = null;
            hql = " from  DefaultHeader  " ;
            if(requestParams.get("filter_names")!=null&&requestParams.get("filter_values")!=null){
                name = new ArrayList((List<String>)requestParams.get("filter_names"));
                value = new ArrayList((List<Object>)requestParams.get("filter_values"));
                hql +=com.krawler.common.util.StringUtil.filterQuery(name, "where");
                 int ind = hql.indexOf("(");
                if(ind>-1){
                    int index = Integer.valueOf(hql.substring(ind+1,ind+2));
                    hql = hql.replace("("+index+")", "("+value.get(index).toString()+")");
                    value.remove(index);
                }
            }

            if(requestParams.get("searchcol")!=null && requestParams.get("ss")!=null){
                searchCol = (String[])requestParams.get("searchcol");
                hql += StringUtil.getSearchquery(requestParams.get("ss").toString(), searchCol, value);
            }

            if(requestParams.get("order_by")!=null&&requestParams.get("order_type")!=null){
                orderby = new ArrayList((List<String>)requestParams.get("order_by"));
                ordertype = new ArrayList((List<Object>)requestParams.get("order_type"));
                hql +=com.krawler.common.util.StringUtil.orderQuery(orderby, ordertype);
            }
            result = executePagingQuery(requestParams, searchCol, hql, value);

        } catch (Exception ex) {
            result.setSuccessFlag(false);
            ex.printStackTrace();

        } finally {
            return result;
        }
    }

    @Override
    // TODO : check references and remove
    public List getModuleColumnConfig(String moduleId, String companyid) throws ServiceException {
        String query = "select * from ( select dh.id, dh.defaultHeader from default_header dh " +
                    "inner join modules mo on mo.id = dh.module " +
                    "inner join fieldparams fp on fp.id = dh.pojoheadername " +
                    "where dh.module=? and fp.companyid = ? " +
                    "and (dh.allowimport = ? or dh.allowimport = ?)  " +
                    " union  " +
                    "select dh.id, dh.defaultHeader from default_header dh " +
                    "inner join modules mo on mo.id = dh.module " +
                    "where dh.module=? and (dh.allowimport = ? or dh.allowimport = ?) " +
                    "and (dh.customflag = ? or dh.customflag = ?) ) " +
                    "as temp order by defaultHeader asc";
        return executeNativeQuery(query,
                new Object[]{moduleId, companyid, "T", "1", moduleId, "T", "1", "F", "0"});
//                new Object[]{moduleId, "T", "1", moduleId, "T", "1", "F", "0"});
    }

    @Override
    public List getModuleObject(String moduleName) throws ServiceException {
        String query = "from Modules where modulename=?";
        return executeQuery(query, moduleName);
    }

    public String getTableName(String fileName) {
        fileName = fileName.trim();
        int startIndex= fileName.contains("/")?(fileName.lastIndexOf("/")+1):0;
        int endIndex= fileName.contains(".")?fileName.lastIndexOf("."):fileName.length();
        String tablename = fileName.substring(startIndex, endIndex);
        tablename = tablename.replaceAll("\\.", "_");
        tablename = "IL_"+tablename;
        return tablename;
    }
    @Override
    public Object createFileTable(String tablename, int cols) throws ServiceException{
        if(cols==0){
            return 0;
        }
        try {
            String query= "", columns = "";
            query = "DROP TABLE IF EXISTS  `"+tablename+"`";
            executeNativeUpdate(query);

            for(int i=0; i<cols; i++){
                columns += "`col"+i+"` TEXT DEFAULT NULL,";
            }
            query = "create table `"+ tablename +"` ("+
                "id INT NOT NULL AUTO_INCREMENT,"+
                columns +
                "isvalid INT(1) DEFAULT 1,"+
                "invalidcolumns VARCHAR(255) DEFAULT NULL,"+
                "validatelog VARCHAR(1000) DEFAULT NULL,"+
                "PRIMARY KEY (id)"+
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8";

            return executeNativeUpdate(query);
        } catch(Exception ex){
            throw ServiceException.FAILURE("createFileTable:"+ex.getMessage(), ex);
        }
    }

    public Object removeFileTable(String tablename) throws ServiceException{
        try {
            String query = "DROP TABLE `"+tablename+"`";
            return executeNativeUpdate(query);
        } catch(Exception ex){
            throw ServiceException.FAILURE("removeFileTable:"+ex.getMessage(), ex);
        }
    }

    @Override
    public Object dumpFileRow(String tablename, Object[] dataArray) throws ServiceException {
        if(dataArray.length==0){
            return 0;
        }
        try {
            String columns = ") values (";
            for(int i=dataArray.length-1; i>=0; i--){
                columns = ",col"+i+columns+"?,";
            }
            String query = "insert into `"+ tablename +"` ("+ (columns.substring(1, columns.length()-1)) +")";
            return executeNativeUpdate(query, dataArray);
        } catch(Exception ex){
            throw ServiceException.FAILURE("dumpFileRow:"+ex.getMessage(), ex);
        }
    }

    public Object makeUploadedFileEntry(String filename, String onlyfilename, String tablename, String companyid) throws ServiceException {
        String query = "insert into uploadedfiles (id,filename,filepathname,tablename,company) values (UUID(), ?,?,?,?)";
        return executeNativeUpdate(query, new Object[]{onlyfilename, filename, tablename, companyid});
    }

    public Object markRecordValidation(String tablename, int id, int isvalid, String validateLog, String invalidColumns) throws ServiceException {
        Object returnObject = null;
        try {
            ArrayList params= new ArrayList();
            params.add(validateLog);
            params.add(invalidColumns);
            params.add(isvalid);

            String condition = "";
            if(id != -1){ // if id==-1 then update all else update respective record
                condition = " where id=?";
                params.add(id);
            }

            String query = "update `"+ tablename +"` set validatelog=?, invalidcolumns=?, isvalid=? "+condition;
            returnObject = executeNativeUpdate(query, params.toArray());
        } catch(Exception ex){
            throw ServiceException.FAILURE("markRecordValidation:"+ex.getMessage(), ex);
        }
        return returnObject;
    }
    
    public KwlReturnObject getFileData(String tablename, HashMap<String, Object>filterParams) throws ServiceException {
        List list = null;
        int count = 0;
        try {
            String condition = "";
            ArrayList params= new ArrayList();
            if(filterParams.containsKey("isvalid")){
                condition = (condition.length()==0?" where ":" and ")+" isvalid=? ";
                params.add(filterParams.get("isvalid"));
            }

            String query = "select * from `"+ tablename +"` "+condition ;
            list = executeNativeQuery(query, params.toArray());
            count = list.size();

            if(filterParams.containsKey("start") && filterParams.containsKey("limit")){
                condition = " limit ?,?";
                int start = Integer.parseInt(filterParams.get("start").toString());
                int limit = Integer.parseInt(filterParams.get("limit").toString());
                params.add(start);
                params.add(limit);
                list = executeNativeQuery(query, params.toArray());
            }
        } catch(Exception ex){
            throw ServiceException.FAILURE("getFileData:"+ex.getMessage(), ex);
        }
        return new KwlReturnObject(true, null, null, list, count);
    }


    public KwlReturnObject executePagingQuery(HashMap<String,Object> requestParams,String[] searchcol,String hql,ArrayList params) {
            boolean success = false;
            List lst = null;
            int count = 0;
        try {
            String allflag = "true";
            if(requestParams.containsKey("allflag"))
                allflag = requestParams.get("allflag").toString();
            int start = 0;
            int limit = 0;

            if(allflag.equals("false")){
                start = Integer.parseInt(requestParams.get("start").toString());
                limit = Integer.parseInt(requestParams.get("limit").toString());
            }

            lst = executeQuery(hql, params.toArray());
            count = lst.size();
            if(allflag.equals("false"))
                lst = executeQueryPaging(hql, params.toArray(), new Integer[]{start, limit});
            success = true;
        } catch (Exception e) {
            success = false;
            logger.warn(e.getMessage(), e);
        }finally{
            return new KwlReturnObject(success, "", "-1", lst, count);
        }
    }
	@Override
	public void linkCustomData(String module) {
		String query=null;
		if("lead".equalsIgnoreCase(module)){
			query="update crm_lead l inner join crmleadcustomdata cu on cu.leadid=l.leadid and l.crmleadcustomdataref is null set l.crmleadcustomdataref=l.leadid";
		}else if("account".equalsIgnoreCase(module)){
			query="update crm_account l inner join crmaccountcustomdata cu on cu.accountid=l.accountid and l.crmaccountcustomdataref is null set l.crmaccountcustomdataref=l.accountid";			
		}else if("contact".equalsIgnoreCase(module)){
			query="update crm_contact l inner join crmcontactcustomdata cu on cu.contactid=l.contactid and l.crmcontactcustomdataref is null set l.crmcontactcustomdataref=l.contactid";			
		}else if("Opportunity".equalsIgnoreCase(module)){
			query="update crm_opportunity l inner join crmopportunitycustomdata cu on cu.oppid=l.oppid and l.crmopportunitycustomdataref is null set l.crmopportunitycustomdataref=l.oppid";			
		}
		if(query!=null)
			updateJDBC(query,new Object[]{});
		
	}
}
