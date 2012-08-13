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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.krawler.common.service.ServiceException;
import com.krawler.spring.authHandler.authHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import java.io.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class ReportBuilderController extends MultiActionController {
    public static final String USER_DATEPREF = "yyyy-MM-dd HH:mm:ss";
    private ReportBuilderDao reportDao;

    public void setReportDao(ReportBuilderDao reportDao) {
        this.reportDao = reportDao;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
   public ModelAndView report(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            String result = "";
            boolean isFormSubmit=false;
            try {
            int action = Integer.parseInt(request.getParameter("action"));
            switch (action) {
                case 0 :
                    result = reportDao.getTables();
                    break;
                case 1:
                    isFormSubmit=true;
                    result = reportDao.createNewReport(request);
                    break;
                case 2:
                    result = reportDao.getReportsList(request);
                    break;
                case 3:
                    boolean flag = Boolean.parseBoolean(request.getParameter("createtable"));
                    String tbar = request.getParameter("tbar");
                    String bbar = request.getParameter("bbar");
                    result = reportDao.saveReportGridConfig(request.getParameter("jsondata"), request.getParameter("reportid"), flag, tbar, bbar);
                    break;
                case 4:
                    result = reportDao.reportData(request);
                    break;
                case 5:
                    isFormSubmit=true;
                    result = reportDao.reportConfig(request);
                    break;
                case 6:
                    result = reportDao.deleteReport(request);
                    break;
                case 7:
                    result = reportDao.insertReportData(request);
                    break;
                case 8:
                    result = reportDao.loadComboStore(request);
                    break;
                case 9:
                    result = reportDao.getReportData(request);
                    break;
                case 10:
                    result = reportDao.updateReportData(request);
                    break;
                case 11:
                    result = reportDao.deleteReportRecord(request);
                    break;
                case 12:
                    result = reportDao.insertComment(request);
                    break;
                case 13:
                    result = reportDao.createRenderer(request);
                    break;
                case 14:
                     isFormSubmit=true;
                    result = reportDao.getRendererFunctions(request);
                    break;
                case 15:
                    result = reportDao.editRenderer(request);
                    break;
                case 16:
                    result = reportDao.moduleGridData(request);
                    break;
                case 17:
                    result = reportDao.getComments(request);
                    break;
                case 18:
                    result = reportDao.deleteComment(request);
                    break;
                case 19:
                    result = reportDao.getUsers(request);
                    break;
//                case 20 :
//                    result = getModulePermission(session,request.getParameter("reportid"), null);
//                    break;
                case 21 :
                    result = reportDao.getModuleDisplayFields(request);
                    break;
                case 22:
                    isFormSubmit = true;
                    result = reportDao.getAllLinkGroups(request);
                    break;
                case 23:
                    isFormSubmit = true;
                    result = reportDao.getAllLinks(request);
                    break;
                case 24:
                    isFormSubmit = true;
                    result = reportDao.getAllPortlets(request);
                    break;
                case 25:
                    //get all modules
                    isFormSubmit = true;
                    result = reportDao.getAllModules(request);
                    break;
                case 26:
                    isFormSubmit = true;
                    result = reportDao.storeDashboardConf(request);
                    break;
                case 27:
                    isFormSubmit = true;
                    result = reportDao.getDashboardLinks(request);
                    break;
                case 28:
                    result = reportDao.getReportDetails(request);
                    break;
                case 29:
                    result = reportDao.getDashboardData(request);
                    break;
				case 31:
                    result = reportDao.storeStortcutConf(request);
                    break;
                case 32:
                    result = reportDao.getColumns(request);
                    break;
                case 33:
                    result = reportDao.createComboFilterConfig(request);
                    break;
                case 34:
                    result = reportDao.getComboFiltersConfig(request);
                    break;
                case 35:
                    result = reportDao.deleteComboFilterConfig(request);
                    break;
                case 36:
                    result = reportDao.comboFilterConfig(request);
                    break;
                case 37:
                    result = reportDao.comboFilterData(request);
                    break;
                case 38:
                    result = reportDao.getModuleFieldsForFilter(request);
                    break;
                case 39:
                    //get all prcesses
                    isFormSubmit = true;
                    result = reportDao.getAllProcesses(request);
                    break;
                case 40:
                    isFormSubmit = true;
                    result = reportDao.getDashboardGroupLinks(request);
                    break;
                case 41:
                    reportDao.staticEntries();
                    break;
                case 42:
                    result = reportDao.checkCompanyMBPermission(sessionHandlerImpl.getCompanyid(request));
                    break;
                }
            } catch (NumberFormatException e) {
                logger.warn(e.getMessage(), e);
            } catch (ServiceException ex) {
                logger.warn(ex.getMessage(), ex);
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
            }
            if(isFormSubmit){
                return new ModelAndView("jsonView-ex", "model", result);
            }
            return new ModelAndView("jsonView", "model", result);
    }
}
