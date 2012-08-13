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

package com.krawler.spring.crm.emailMarketing;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.URLUtil;
import com.krawler.esp.web.resource.Links;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import javax.servlet.http.HttpServletRequest;
import com.krawler.spring.common.KwlReturnObject;
import java.util.HashMap;

public class EmailViewerController  extends MultiActionController {
    private crmEmailMarketingDAO crmEmailMarketingDAOObj;
    private String successView;
    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }
    public void setcrmEmailMarketingDAO(crmEmailMarketingDAO crmEmailMarketingDAOObj1) {
        this.crmEmailMarketingDAOObj = crmEmailMarketingDAOObj1;
    }

 public ModelAndView newsletter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        String htmlString = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><link rel=\"shortcut icon\" href=\"../../images/deskera/deskera.png\"/>" +
        		"<style>body, td, input, textarea, select {font-family: arial,sans-serif;} a img{border-style:none}</style>" +
        		"</head>";
        try {
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("uid", request.getParameter("uid"));
            requestParams.put("mid", request.getParameter("mid"));
            requestParams.put("tuid", request.getParameter("tuid"));
            requestParams.put("cdomain", request.getParameter("cdomain"));
            requestParams.put("baseurl", URLUtil.getRequestPageURL(request, Links.UnprotectedLoginPageFull));
            KwlReturnObject kmsg = crmEmailMarketingDAOObj.getTemplateHTMLContent(requestParams);
            htmlString += kmsg.getEntityList().get(0).toString();
            htmlString=htmlString+"";
        }catch (ServiceException e) {
            logger.warn(e.getMessage(),e);
        } finally {
            htmlString += "</html>";
        }
        return new ModelAndView("jsonView-ex", "model", htmlString);
    }
}
