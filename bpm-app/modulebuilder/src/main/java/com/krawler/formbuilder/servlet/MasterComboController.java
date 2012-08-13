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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.krawler.common.service.ServiceException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class MasterComboController extends MultiActionController {
    private MasterComboDao masterComboDao;

    public void setMasterComboDao(MasterComboDao masterComboDao) {
        this.masterComboDao = masterComboDao;
    }

    public ModelAndView getConfig(HttpServletRequest request, HttpServletResponse response){
        String result = "";
        result = masterComboDao.getConfig();
        return new ModelAndView("jsonView", "model", result);
    }
    public ModelAndView insertConfig(HttpServletRequest request, HttpServletResponse response){
        String result = "";
        try {
            String configid = request.getParameter("configid");
            String configtype = request.getParameter("configtype");
            String name = request.getParameter("fieldname");

            result = masterComboDao.insertConfig(configid, configtype, name);
        }catch(ServiceException ex){
            logger.warn(ex.getMessage(), ex);
            result="{'success':'false'}";
        }

        return new ModelAndView("jsonView", "model", result);
    }
    public ModelAndView deleteConfig(HttpServletRequest request, HttpServletResponse response){
        String result = "";
        try {
            String mode = request.getParameter("mode");
            String delid = request.getParameter("delid");
            result = masterComboDao.deleteConfig(mode, delid);
        }catch(ServiceException ex){
            logger.warn(ex.getMessage(), ex);
            result="{'success':'false'}";
        }
        return new ModelAndView("jsonView", "model", result);
    }
    public ModelAndView insertMaster(HttpServletRequest request, HttpServletResponse response){
        String result = "";
        try {
            String masterid = request.getParameter("masterid");
            String configid = request.getParameter("configid");
            String masterdata = request.getParameter("masterdata");
            result = masterComboDao.insertMaster(masterid, configid, masterdata);
        }catch(ServiceException ex){
            logger.warn(ex.getMessage(), ex);
            result="{'success':'false'}";
        }
        return new ModelAndView("jsonView", "model", result);
    }
    public ModelAndView getMaster(HttpServletRequest request, HttpServletResponse response){
        String result = "";
        try {
            result = masterComboDao.getMaster(request.getParameter("configid"));
        }catch(ServiceException ex){
            logger.warn(ex.getMessage(), ex);
            result="{'success':'false'}";
        }
        return new ModelAndView("jsonView", "model", result);
    }
    public ModelAndView getMasterAttributes(HttpServletRequest request, HttpServletResponse response){
        String result = "";
        try {
            result = masterComboDao.getMasterAttributes(request.getParameter("mode"));
        }catch(ServiceException ex){
            logger.warn(ex.getMessage(), ex);
            result="{'success':'false'}";
        }
        return new ModelAndView("jsonView", "model", result);
    }
}
