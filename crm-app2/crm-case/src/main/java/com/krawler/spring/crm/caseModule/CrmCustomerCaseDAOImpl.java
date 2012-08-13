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

import java.util.List;

import com.krawler.common.admin.CaseComment;
import com.krawler.common.admin.Docs;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.dao.BaseDAO;

public class CrmCustomerCaseDAOImpl extends BaseDAO implements CrmCustomerCaseDAO {


	@Override
	public List<CaseComment> getComments(String caseId) {
		
		String hql = " from CaseComment c where c.caseid=? and c.deleted ='F' order by c.postedon desc";
		List<CaseComment> caseCommentList= executeQuery(hql, new Object[]{caseId});
		return caseCommentList;
	}

	public List<Docs> getDocument(String docId) {
		
		String hql = " from com.krawler.common.admin.Docs c where c.docid=? ";
		List<Docs> docList= executeQuery(hql, new Object[]{docId});
		return docList;
	}
}
