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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.krawler.common.admin.CaseComment;
import com.krawler.common.admin.Docs;
import com.krawler.common.comet.CrmPublisherHandler;
import com.krawler.common.service.ServiceException;
import com.krawler.common.util.Constants;
import com.krawler.common.util.StringUtil;
import com.krawler.common.util.URLUtil;
import com.krawler.crm.contact.bizservice.ContactManagementService;
import com.krawler.crm.database.tables.CrmCase;
import com.krawler.esp.handlers.FileUploadHandler;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.esp.web.resource.Links;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.spring.storageHandler.storageHandlerImpl;
import com.krawler.utils.json.base.JSONObject;



public class CrmCustomerCaseController extends MultiActionController{
	
	public CrmCustomerCaseService crmCustomerCaseService;
	private HibernateTransactionManager txnManager;
	private ContactManagementService contactManagementService;
	private sessionHandlerImpl sessionHandlerImplObj;
	
	public void setCrmCustomerCaseService(CrmCustomerCaseService crmCustomerCaseService) {
		this.crmCustomerCaseService = crmCustomerCaseService;
	}

	public void setTxnManager(HibernateTransactionManager txManager) {
		this.txnManager = txManager;
	}

	public void setContactManagementService(ContactManagementService contactManagementService) {
		this.contactManagementService = contactManagementService;
	}

	public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImpl1) {
		this.sessionHandlerImplObj = sessionHandlerImpl1;
	}
	
	public ModelAndView getCustomerCases(HttpServletRequest request, HttpServletResponse response) {
		Map model=new HashMap();
		try {
			String companyId = sessionHandlerImpl.getCompanyid(request);
			String contactId = (String) request.getSession().getAttribute("contactid");
			List<CrmCase> caseList=crmCustomerCaseService.getCases(companyId, contactId);
			model.put("cases", caseList);
			model.put("allowNew", true);
			model.put("cdomain", URLUtil.getDomainName(request));
			model.put("pchanged", request.getParameter("pchanged"));
			model.put("customername", request.getSession().getAttribute(Constants.SESSION_CONTACT_NAME));
		}

		catch (Exception e) {
			logger.warn(e.getMessage(),e);
		}
		return new ModelAndView("usercases/caselist", "model", model);
	}

	public ModelAndView addComment(HttpServletRequest request, HttpServletResponse response) {
		Map model = new HashMap();
		String responseMessage = "";
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		try {
			if ((String) request.getSession().getAttribute(Constants.SESSION_CUSTOMER_ID) != null) {
				String caseId = request.getParameter("caseid");
				String userId = (String) request.getSession().getAttribute("contactid");
				String comment = request.getParameter("addcomment");
				crmCustomerCaseService.addComment(caseId, userId, comment);
				txnManager.commit(status);
				request.setAttribute("casedetails", "true");
				request.setAttribute("caseid", caseId);
			} else {
				request.setAttribute("logout", "true");
			}
			responseMessage = "usercases/redirect";
		}

		catch (Exception e) {
			logger.warn(e.getMessage(), e);
			txnManager.rollback(status);
		}
		return new ModelAndView(responseMessage, "model", model);
	}

	public ModelAndView getDetails(HttpServletRequest request, HttpServletResponse response) {
		Map model=new HashMap();
		try {
			 String caseid=request.getParameter("caseid");
			 model.put("caseObj", crmCustomerCaseService.getCase(caseid));
			 model.put("comments", crmCustomerCaseService.getComments(caseid));  
			 model.put("documents", crmCustomerCaseService.getDocuments(caseid));
			 model.put("cdomain", URLUtil.getDomainName(request));	
			 model.put("customername", request.getSession().getAttribute(Constants.SESSION_CONTACT_NAME));
		}

		catch (Exception e) {
			logger.warn(e.getMessage(), e);

		}
		return new ModelAndView("usercases/casedetails", "model", model);
	}
	public ModelAndView saveCustomerCases(HttpServletRequest request, HttpServletResponse response) throws ServletException, ServiceException {
		JSONObject myjobj = new JSONObject();
		KwlReturnObject kmsg = null;
		CrmCase cases = null;
		String responseMessage="";
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		FileUploadHandler uh=new FileUploadHandler();
		HashMap hm = uh.getItems(request);
		Map model=new HashMap();
		
		try {
			
			String companyid = sessionHandlerImpl.getCompanyid(request);
			String customerId= (String) request.getSession().getAttribute("customerid");
			String contactId = (String) request.getSession().getAttribute("contactid");
			String caseOwnerID =crmCustomerCaseService.getCompanyCaseDefaultOwnerID(companyid);
			Integer operationCode = CrmPublisherHandler.ADDRECORDCODE;
			JSONObject jobj = new JSONObject();
			JSONObject jobj1 = new JSONObject();
			String id = java.util.UUID.randomUUID().toString();
			
			jobj.put("caseid", id);
			jobj.put("contactnameid", contactId);
			jobj.put("userid", caseOwnerID);
			jobj.put("caseownerid", caseOwnerID);
			jobj.put("subject", hm.get("subject"));
			jobj.put("description", hm.get("description"));
			jobj.put("companyid", companyid);
			jobj.put("createdon", System.currentTimeMillis());
			jobj.put("updatedon", System.currentTimeMillis());
			jobj.put("casecreatedby", contactId);
			jobj.put("createdbyflag", "1");

			kmsg = crmCustomerCaseService.addCases(jobj);
			cases = (CrmCase) kmsg.getEntityList().get(0);
			
			try {
				FileItem fileItem=(FileItem) hm.get("attachment");
				String filename= fileItem.getName();				
	            String docID="";
            	if(filename!=null && filename!=""){
            		if( fileItem.getSize() <= 10485760){  //limit 10 mb
	            		kmsg = crmCustomerCaseService.uploadFile(fileItem, null, companyid, getServletContext());//Since document is uploaded by customer ,userid is null for uploadfile function
	            		Docs doc = (Docs) kmsg.getEntityList().get(0);
	            		docID = doc.getDocid();
	            		jobj1.put("docid", docID);
	            		jobj1.put("companyid", companyid);
	            		jobj1.put("id", id);
	            		jobj1.put("map", "6");
	            		jobj1.put("refid", id);
	            		crmCustomerCaseService.saveDocumentMapping(jobj1);
	            		crmCustomerCaseService.saveCustomerDocs(customerId,docID,id);
            		}
            	}
			} catch (Exception e) {
				logger.warn("Attachment upload failed with Customer case :"+e.getMessage());
			}
			
			myjobj.put("success", true);
			myjobj.put("ID", cases.getCaseid());
			myjobj.put("createdon",  jobj.optLong("createdon"));
			txnManager.commit(status);

			JSONObject cometObj = jobj;
			if (!StringUtil.isNullObject(cases)) {
				if (!StringUtil.isNullObject(cases.getCreatedon())) {
					cometObj.put("createdon", cases.getCreatedonGMT());
				}
			}
			//publishCasesModuleInformation(request, cometObj, operationCode, companyid, caseOwnerID);
			request.setAttribute("caselist", "true");
			responseMessage="usercases/redirect";
			
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			txnManager.rollback(status);
		}
		return new ModelAndView(responseMessage, "model", model);
	}
	
	public ModelAndView uploadCustomerCaseDocs(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		String responseMessage="";
		String caseid="";
		Map model = new HashMap();
		try {
			if ((String) request.getSession().getAttribute(Constants.SESSION_CUSTOMER_ID) != null) {
			FileUploadHandler uh=new FileUploadHandler();
			HashMap hm = uh.getItems(request);
			String companyid = sessionHandlerImpl.getCompanyid(request);
			String customerId= (String) request.getSession().getAttribute("customerid");
			String contactId = (String) request.getSession().getAttribute("contactid");
			JSONObject jobj = new JSONObject();
			caseid=hm.get("caseid").toString();
			KwlReturnObject kmsg=null;

					FileItem fileItem=(FileItem) hm.get("attachment");
					String filename=fileItem.getName();

		            String docID="";
		            	if(filename!=null && filename!="" ){
		            		if( fileItem.getSize() <= 10485760){  //limit 10 mb
		            			kmsg = crmCustomerCaseService.uploadFile(fileItem, null, companyid, getServletContext());//Since document is uploaded by customer ,userid is null for uploadfile function
		            			Docs doc = (Docs) kmsg.getEntityList().get(0);
		            			docID = doc.getDocid();
		            			jobj.put("docid", docID);
		            			jobj.put("companyid", companyid);
		            			jobj.put("map", "6");
		            			jobj.put("refid", caseid);
		            			crmCustomerCaseService.saveDocumentMapping(jobj);
		            			crmCustomerCaseService.saveCustomerDocs(customerId,docID,caseid);
		            		}
				}
				request.setAttribute("casedetails", "true");
				request.setAttribute("caseid", caseid);
			} else {
				request.setAttribute("logout", "true");
			}
		} catch (Exception e) {
			logger.warn("Uploading Error");
			e.printStackTrace();
		}

		responseMessage = "usercases/redirect";

		return new ModelAndView(responseMessage, "model", model);
	}
	
	public ModelAndView custPassword_Change(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		
		String newpass = request.getParameter("newpass");
		String currentpass = request.getParameter("curpass");
		String customerid = request.getParameter("customerid");
		String email = request.getParameter("email");
		Map model=new HashMap();
		// String replaceStr=request.getParameter("cdomain");
		String url = URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull);
		String loginurl = url.concat("caselogin.jsp");
		String name = request.getParameter("cname");
		boolean verify_pass = false;
		String responseMessage = "";
		if ((String) request.getSession().getAttribute(Constants.SESSION_CUSTOMER_ID) != null) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setName("JE_Tx");
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		TransactionStatus status = txnManager.getTransaction(def);
		try {
			
				verify_pass = contactManagementService.verifyCurrentPass(currentpass, customerid);
				if (verify_pass) {
					contactManagementService.custPassword_Change(newpass, customerid);
					txnManager.commit(status);
					request.setAttribute("homepage", "true");
					request.setAttribute("success", "true");
					responseMessage = "usercases/redirect";
					String partnerNames = sessionHandlerImplObj.getPartnerName();
					String sysEmailId = sessionHandlerImplObj.getSystemEmailId();
					String passwordString = "Username: " + email + " <br/><br/>Password: <b>" + newpass + "</b>";
					String htmlmsg = "Dear <b>" + name + "</b>,<br/><br/> Your <b>password has been changed</b> for your account on " + partnerNames + " CRM. <br/><br/>" + passwordString
							+ "<br/><br/>You can log in at:\n" + loginurl + "<br/><br/>See you on " + partnerNames + " CRM <br/><br/> -The " + partnerNames + " CRM Team";
					try {
						SendMailHandler.postMail(new String[] { email }, "[" + partnerNames + "] Password Changed", htmlmsg, "", sysEmailId, partnerNames + " Admin");
					} catch (MessagingException e) {
						e.printStackTrace();
					}
					
				} else {
					request.setAttribute("changepassword", "true");
					request.setAttribute("mis_pass", "true");
					txnManager.commit(status);
				}
			

		} catch (Exception e) {
			logger.warn("custPassword_change Error:", e);
			txnManager.rollback(status);
			responseMessage = "../../usercases/failure";
		}
		} else {
			request.setAttribute("logout", "true");
		}
		responseMessage = "usercases/redirect";
		return new ModelAndView(responseMessage, "model", model);
	}

	public ModelAndView downloadFile(HttpServletRequest request, HttpServletResponse response) {
		Map model = new HashMap();
		String responseMessage="";
		
		try {
			if((String) request.getSession().getAttribute(Constants.SESSION_CUSTOMER_ID)!=null){
			String caseId = request.getParameter("caseid");
			String docid = request.getParameter("docid");
			List<Docs> dl =crmCustomerCaseService.getDocument(docid);
			Docs doc= dl.get(0);
			String src = storageHandlerImpl.GetDocStorePath();
			src = src +  "/" + doc.getStorename();//ht.get("userid").toString()
			File fp = new File(src);
			byte[] buff = new byte[(int) fp.length()];
			FileInputStream fis = new FileInputStream(fp);
			int read = fis.read(buff);
			javax.activation.FileTypeMap mmap = new javax.activation.MimetypesFileTypeMap();
			response.setContentType(mmap.getContentType(src));
			response.setContentLength((int) fp.length());
			response.setHeader("Content-Disposition", request.getParameter("dtype") + "; filename=\"" + doc.getDocname()+ "\";");
			response.getOutputStream().write(buff);
			response.getOutputStream().flush();
			response.getOutputStream().close();
			
			request.setAttribute("casedetails", "true");
		     request.setAttribute("caseid", caseId);
			
				
			}else{
				request.setAttribute("logout", "true");
			}
			 responseMessage="usercases/redirect";
		}

		catch (Exception e) {
			logger.warn(e.getMessage(), e);
			
		}
		return new ModelAndView(responseMessage, "model", model);
	}


	public ModelAndView newCaseForm(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("usercases/newcase");
	}
	
	public ModelAndView changePasswordForm(HttpServletRequest request, HttpServletResponse response) {
		Map model=new HashMap();
		model.put("mis_pass", request.getParameter("mis_pass"));
		return new ModelAndView("usercases/changepassword","model",model);
	}
	
	public ModelAndView signout(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("usercases/signout");
	}

}
