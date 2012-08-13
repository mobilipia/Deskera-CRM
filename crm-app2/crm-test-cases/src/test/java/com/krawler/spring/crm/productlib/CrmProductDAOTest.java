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
package com.krawler.spring.crm.productlib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.krawler.common.service.ServiceException;
import com.krawler.crm.database.tables.CrmProduct;
import com.krawler.dao.BaseDAO;
import com.krawler.spring.BaseTest;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.crm.productModule.crmProductDAO;


public class CrmProductDAOTest extends BaseTest{	
	private crmProductDAO productDAO; 
	private KwlReturnObject kwlReturnObject;
	private BaseDAO baseDAO;
	Log logger = LogFactory.getLog(CrmProductDAOTest.class);
	@Autowired
	public void setBaseDAO(@Qualifier("crmProductdao")BaseDAO baseDAO) {
		this.baseDAO = baseDAO;
	}
	@Autowired
	public void setCrmProductDAO(crmProductDAO productDAOObj){
		productDAO = productDAOObj;
	}
	@Test
	public void testGetProducts(){
		List<String> recordIds= new ArrayList<String>();
		recordIds.add("c340667e266e177d01266f3331f300df");
		recordIds.add("d1531cd7-c3eb-47b6-8a0f-b2ced47eafa7");
		List<CrmProduct> CrmProductList = productDAO.getProducts(recordIds);
		
		//Assert.assertNotNull(CrmProductList);
		//Assert.assertTrue("productDAO.getProducts(recordIds) should return null if no recordid matches", CrmProductList.isEmpty());
		for(CrmProduct crmProduct:CrmProductList)
		{
			logger.info(crmProduct.getProductid());
		}
		
		logger.info("testGetProducts successfully completed");
	}
	
	
	/**
	 * testing to get all products
	 */
	
	@Test
	public void testGetAllProducts()
	{
		HashMap<String, Object> requestParams = new HashMap<String, Object>();
		 ArrayList order_by = new ArrayList();
		 ArrayList order_type = new ArrayList();
         order_by.add("c.productname");
         order_type.add("asc");
		requestParams.put("order_by", order_by);
        requestParams.put("order_type", order_type);
		requestParams.put("filter_names", new ArrayList());
        requestParams.put("filter_values", new ArrayList());
		try {
			
			kwlReturnObject=productDAO.getAllProducts(requestParams);
			Assert.assertNotNull("kwlReturnObject must not be null", kwlReturnObject);
			logger.info(kwlReturnObject.getRecordTotalCount());
			Assert.assertNotNull("product list must not be null", kwlReturnObject.getEntityList());
			logger.info(kwlReturnObject.getEntityList().size());
			logger.info(kwlReturnObject.getMsg());
			logger.info(kwlReturnObject.getErrorCode());
			for(CrmProduct crmProduct:(ArrayList<CrmProduct>)kwlReturnObject.getEntityList())
			{
				logger.info(crmProduct.getProductid());
			}
		} 
		catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void 	testGetCrmProductCustomData()
	{
		HashMap<String, Object> requestParams = new HashMap<String, Object>();
		try {
			
			kwlReturnObject=productDAO.getCrmProductCustomData(requestParams);
			//Assert.assertNotNull("product list must not be null", kwlReturnObject.getEntityList());
			//logger.info(kwlReturnObject.getEntityList().size());
		} 
		catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
