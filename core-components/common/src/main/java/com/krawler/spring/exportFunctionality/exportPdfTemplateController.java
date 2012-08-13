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
package com.krawler.spring.exportFunctionality;

import com.krawler.common.service.ServiceException;
import com.krawler.common.util.StringUtil;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.krawler.common.admin.Projreport_Template;
import com.krawler.spring.common.KwlReturnObject;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

/**
 * 
 * @author Karthik
 */
public class exportPdfTemplateController extends MultiActionController
{

    private sessionHandlerImpl sessionHandlerImplObj;
    private String successView;
    private HibernateTransactionManager txnManager;
    private ExportPdfTemplateService exportPdfTemplateService;

    public void setTxnManager(HibernateTransactionManager txManager)
    {
        this.txnManager = txManager;
    }

    public String getSuccessView()
    {
        return successView;
    }

    public void setSuccessView(String successView)
    {
        this.successView = successView;
    }

    public void setsessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj1)
    {
        this.sessionHandlerImplObj = sessionHandlerImplObj1;
    }

    /**
     * @param exportPdfTemplateService
     *            the exportPdfTemplateService to set
     */
    public void setExportPdfTemplateService(ExportPdfTemplateService exportPdfTemplateService)
    {
        this.exportPdfTemplateService = exportPdfTemplateService;
    }

    public ModelAndView saveReportTemplate(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try
        {
            String jsondata = request.getParameter("data");
            String userid = request.getParameter("userid");
            String name = request.getParameter("name");
            String desc = request.getParameter("desc");
            String preText = request.getParameter("pretext");
            String postText = request.getParameter("posttext");
            String letterHead = request.getParameter("letterhead");
            String tempId = java.util.UUID.randomUUID().toString();
            int templatetype = 0;
            if(request.getParameter("templatetype")!=null && !StringUtil.isNullOrEmpty(request.getParameter("templatetype")))
                templatetype = Integer.valueOf(request.getParameter("templatetype"));
            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("jsondata", StringUtil.checkForNull(jsondata));
            requestParams.put("tempid", StringUtil.checkForNull(tempId));
            requestParams.put("userid", StringUtil.checkForNull(userid));
            requestParams.put("name", StringUtil.checkForNull(name));
            requestParams.put("desc", StringUtil.checkForNull(desc));
            requestParams.put("templatetype", templatetype);
            requestParams.put("pretext", StringUtil.checkForNull(preText));
            requestParams.put("posttext", StringUtil.checkForNull(postText));
            requestParams.put("letterhead", StringUtil.checkForNull(letterHead));

            kmsg = exportPdfTemplateService.saveReportTemplate(requestParams);
            jobj.put("tempid", tempId);
            jobj.put("success", kmsg.isSuccessFlag());
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public JSONObject getReportTemplateJson(List ll) throws ServiceException
    {
        JSONObject jobj = new JSONObject();
        JSONArray jarr = new JSONArray();
        try
        {
            Iterator ite = ll.iterator();
            while (ite.hasNext())
            {
                Projreport_Template obj = (Projreport_Template) ite.next();
                JSONObject jtemp = new JSONObject();

                jtemp.put("tempid", obj.getTempid());
                jtemp.put("tempname", obj.getTempname());
                jtemp.put("description", obj.getDescription());
                jtemp.put("configstr", obj.getConfigstr());
                jtemp.put("letterhead", StringUtil.isNullOrEmpty(obj.getLetterHead())?"":obj.getLetterHead());
                jtemp.put("pretext", StringUtil.isNullOrEmpty(obj.getPreText())?"":obj.getPreText());
                jtemp.put("posttext", StringUtil.isNullOrEmpty(obj.getPostText())?"":obj.getPostText());
                jarr.put(jtemp);
            }
            jobj.put("data", jarr);
        } catch (JSONException e)
        {
            throw ServiceException.FAILURE(e.getMessage(), e);
        }
        return jobj;
    }

    public ModelAndView getAllReportTemplate(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try
        {

            String userid = sessionHandlerImplObj.getUserid(request);
            int templatetype = Integer.valueOf(request.getParameter("templatetype"));
            kmsg = exportPdfTemplateService.getAllReportTemplate(userid,templatetype);
            jobj = getReportTemplateJson(kmsg.getEntityList());
            jobj.put("success", kmsg.isSuccessFlag());
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView deleteReportTemplate(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        try
        {
            String tempid = StringUtil.checkForNull(request.getParameter("deleteflag"));
            kmsg = exportPdfTemplateService.deleteReportTemplate(tempid);

            jobj.put("success", kmsg.isSuccessFlag());
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }

    public ModelAndView editReportTemplate(HttpServletRequest request, HttpServletResponse response) throws ServletException
    {
        JSONObject jobj = new JSONObject();
        KwlReturnObject kmsg = null;
        // Create transaction
        try
        {
            String tempid = request.getParameter("edit");
            String newconfig = request.getParameter("data");
            String preText = request.getParameter("pretext");
            String postText = request.getParameter("posttext");
            String letterHead = request.getParameter("letterhead");

            HashMap<String, Object> requestParams = new HashMap<String, Object>();
            requestParams.put("tempid", StringUtil.checkForNull(tempid));
            requestParams.put("newconfig", StringUtil.checkForNull(newconfig));
            requestParams.put("pretext", StringUtil.checkForNull(preText));
            requestParams.put("posttext", StringUtil.checkForNull(postText));
            requestParams.put("letterhead", StringUtil.checkForNull(letterHead));

            kmsg = exportPdfTemplateService.editReportTemplate(requestParams);
            jobj.put("success", kmsg.isSuccessFlag());
            jobj.put("tempid", StringUtil.checkForNull(tempid));
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return new ModelAndView("jsonView", "model", jobj.toString());
    }
}
