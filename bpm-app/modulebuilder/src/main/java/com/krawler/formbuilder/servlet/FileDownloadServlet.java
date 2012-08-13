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
package com.krawler.formbuilder.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.krawler.esp.utils.PropsValues;

import com.krawler.esp.hibernate.impl.*;
import com.krawler.workflow.module.bizservice.ModuleBuilderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class FileDownloadServlet extends HttpServlet {
	private static final long serialVersionUID = -7262043406413106392L;
        private static final Log logger = LogFactory.getLog(FileDownloadServlet.class);

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
		    
		    WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());		    
		    ModuleBuilderService moduleBuilderService = (ModuleBuilderService)applicationContext.getBean("moduleBuilderService");
            mb_docs docsObj = (mb_docs) moduleBuilderService.getMBDocById(mb_docs.class,request.getParameter("docid"));
            String src = PropsValues.STORE_PATH+request.getParameter("url");

			File fp = new File(src);
			byte[] buff = new byte[(int) fp.length()];
			FileInputStream fis = new FileInputStream(fp);
			int read = fis.read(buff);
			javax.activation.FileTypeMap mmap = new javax.activation.MimetypesFileTypeMap();
            String fileName = docsObj.getDocname();
			response.setContentType(mmap.getContentType(fp));
			response.setContentLength((int) fp.length());
            String contentDisposition = "";
            if (request.getParameter("attachment")!=null) {
				contentDisposition = "attachment";
			}else {
				contentDisposition = "inline";
			}

			response.setHeader("Content-Disposition", contentDisposition
					+ "; filename=\"" + fileName+ "\";");
            response.getOutputStream().write(buff, 0, buff.length);
			response.getOutputStream().flush();
		} catch (Exception ex) {
			logger.warn("Unable To Download File :" + ex.toString(), ex);
		}

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	public String getServletInfo() {
		return "Short description";
	}
}
