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
package com.krawler.spring.crm.caseModule;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItem;
import org.springframework.dao.DataAccessException;


import com.krawler.common.admin.CaseComment;
import com.krawler.common.admin.Company;
import com.krawler.common.admin.Docmap;
import com.krawler.common.admin.Docs;
import com.krawler.common.admin.User;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.KWLErrorMsgs;
import com.krawler.common.util.StringUtil;
import com.krawler.crm.database.tables.CrmAccount;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.crm.database.tables.CrmContact;
import com.krawler.crm.database.tables.CrmCustomer;
import com.krawler.crm.database.tables.CustomerDocs;
import com.krawler.crm.database.tables.DefaultMasterItem;
import com.krawler.crm.utils.Constants;
import com.krawler.spring.comments.commentDAO;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.documents.documentDAO;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;

public class CrmCustomerCaseService {
	public crmCaseDAO caseDao;
	private documentDAO crmDocumentDAO;
	private CrmCustomerCaseDAO crmCustomerCaseDAO;
	private commentDAO crmCommentDAO;
	
	public void setCrmCustomerCaseDAO(CrmCustomerCaseDAO crmCustomerCaseDAO) {
		this.crmCustomerCaseDAO = crmCustomerCaseDAO;
	}
	public void setcrmDocumentDAO(documentDAO crmDocumentDAO) {
        this.crmDocumentDAO = crmDocumentDAO;
    }
	public void setCaseDAO(crmCaseDAO caseDao) {
		this.caseDao = caseDao;
	}
	public void setcrmCommentDAO(commentDAO crmCommentDAO) {
        this.crmCommentDAO = crmCommentDAO;
    }

	public List<CrmCase> getCases(String companyId, String contactId) {
		HashMap map = new HashMap();
		ArrayList pn = new ArrayList();
		pn.add("c.company.companyID");
		pn.add("c.crmContact.contactid");
		ArrayList pv = new ArrayList();
		pv.add(companyId);
		pv.add(contactId);
		map.put("filter_names", pn);
		map.put("filter_params", pv);
		map.put("heirarchyPerm", true);
		List caseList=new ArrayList();
		try {
			caseList = caseDao.getCases(map).getEntityList();
		} catch (ServiceException e) {
		}
		
		//List<CrmCase> caseList=caseDao.getCases(companyId, contactId);
		

		return caseList;
	}

	public List<CaseComment> getComments(String caseId) {
		List<CaseComment> CaseCommentList=crmCustomerCaseDAO.getComments(caseId);
		return CaseCommentList;
	}

	public List<Docs> getDocuments(String caseId) {
		HashMap map = new HashMap();
		map.put("recid", caseId);
		List docList = new ArrayList();

		try {
			docList=crmDocumentDAO.getDocuments(map).getEntityList();
		} catch (ServiceException e) {
			
			e.printStackTrace();
		}
		return docList;
	}
	public List<Docs> getDocument(String docId) {
		
		List<Docs> docList=crmCustomerCaseDAO.getDocument(docId);
		
		return docList;
	}

	public void addComment(String caseId,String userId,String comment) {
		
		JSONObject jobj = new JSONObject();

		try {
			jobj.put("userid", userId);
			jobj.put("refid", caseId);
			jobj.put("id", java.util.UUID.randomUUID().toString());
			jobj.put("comment", comment);
			jobj.put("mapid", Constants.RELATEDTO_ID);
			jobj.put("userflag", Constants.USER_FLAG);
			jobj.put("deleted", false);
			
			crmCommentDAO.addCaseComments(jobj);

		} catch (ServiceException e) {

			e.printStackTrace();

		} catch (JSONException e) {

			e.printStackTrace();
		}
				
	}

	public CrmCase getCase(String caseid) {
		HashMap map = new HashMap();
		ArrayList pn = new ArrayList();
		pn.add("c.caseid");
		ArrayList pv = new ArrayList();
		pv.add(caseid);
		map.put("filter_names", pn);
		map.put("filter_params", pv);
		map.put("heirarchyPerm", true);
		List l=new ArrayList();
		try {
			l = caseDao.getCases(map).getEntityList();
		} catch (ServiceException e) {
		}
		if(l.isEmpty())
			return null;
		return (CrmCase)l.get(0);
		
	}

	public void saveCustomerDocs(String customerId, String docId, String caseId) {
		try {
			caseDao.saveCustomerDocs(customerId, docId, caseId);
		} catch (Exception e) {

		}
	}

	public void saveDocumentMapping(JSONObject jobj) {
		try {
			crmDocumentDAO.saveDocumentMapping(jobj);
		} catch (Exception ex) {

		}
	}

	public KwlReturnObject uploadFile(FileItem fileItem, String userid, String companyId, ServletContext servletContext) throws ServiceException {

		List ll = new ArrayList();
		int dl = 0;
		try {
			ll = crmDocumentDAO.uploadFile(fileItem, null, companyId, servletContext).getEntityList();
		} catch (Exception e) {
			throw ServiceException.FAILURE(e.getMessage(), e);
		}
		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	public KwlReturnObject addCases(JSONObject jobj) throws ServiceException {
		List ll = new ArrayList();
		int dl = 0;
		try {
			ll = caseDao.addCases(jobj).getEntityList();
		} catch (DataAccessException e) {
			throw ServiceException.FAILURE("crmCaseDAOImpl.addCases : " + e.getMessage(), e);
		} catch (Exception e) {
			throw ServiceException.FAILURE("crmCaseDAOImpl.addCases : " + e.getMessage(), e);
		}
		return new KwlReturnObject(true, KWLErrorMsgs.S01, "", ll, dl);
	}

	public String getCompanyCaseDefaultOwnerID(String companyId) throws ServiceException {

		String ll = caseDao.getCompanyCaseDefaultOwnerID(companyId);

		return ll;

	}

}
