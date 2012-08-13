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
package com.krawler.br.spring;

import com.krawler.br.BusinessProcess;
import com.krawler.br.ProcessBag;
import com.krawler.br.ProcessException;
import com.krawler.br.nodes.xml.XmlNodeParser;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.XmlFactory;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONObject;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.multiaction.InternalPathMethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 *
 * @author Vishnu Kant Gupta
 */
public class ProcessController extends AbstractController {
    private RConverter converter;
    private ProcessBag bag;
    private SourceFactory defaultSourceFactory;
    private sessionHandlerImpl sessionHandlerImplObj;
    private XmlNodeParser xmlParser;
    private String successView;
    private MethodNameResolver processNameResolver = new InternalPathMethodNameResolver();
    private String factoryDirPath;

    public void setFactoryDirPath(String factoryDirPath) {
        this.factoryDirPath = factoryDirPath;
    }

    public void setConverter(RConverter converter) {
        this.converter = converter;
    }

    public void setBag(ProcessBag bag) {
        this.bag = bag;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setXmlParser(XmlNodeParser xmlParser) {
        this.xmlParser = xmlParser;
    }

    public void setDefaultSourceFactory(SourceFactory defaultSourceFactory) {
        this.defaultSourceFactory = defaultSourceFactory;
    }

    public String getSuccessView() {
        return successView;
    }

    public void setSuccessView(String successView) {
        this.successView = successView;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String processName;

        try {
                processName = this.processNameResolver.getHandlerMethodName(request);
        } catch (NoSuchRequestHandlingMethodException ex){
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return null;
        }

        Object res = null;
        String view=successView;
        try {
            BusinessProcess p = bag.getProcess(getSourceFactory(sessionHandlerImplObj.getCompanyid()), processName);
            Map params = converter.convert(request, p);
            res = p.execute(params);
            if(p.getView()!=null)
                view=p.getView();
        } catch (Exception ex) {
            view="jsonView";
            JSONObject obj = new JSONObject();
            obj.put("msg", ex.getMessage());
            obj.put("success", false);
            res = obj;
        }
        return new ModelAndView(view, "model", res);
    }
        
    private SourceFactory getSourceFactory(String companyid) throws ProcessException{
        Properties prop = new Properties();
        prop.setProperty("filesystempath", factoryDirPath+"/"+companyid+"businesslogicEx.xml");
        SourceFactory src = new XmlFactory(prop, xmlParser, defaultSourceFactory);
        return src;
    }
}
