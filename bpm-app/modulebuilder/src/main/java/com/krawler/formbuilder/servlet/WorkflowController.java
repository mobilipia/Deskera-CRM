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
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.krawler.common.service.ServiceException;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.workflow.bizservice.WorkflowService;
import java.io.FileInputStream;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class WorkflowController extends MultiActionController {

    private WorkflowService workflowService;
    private HibernateTransactionManager txnManager;
    public void setTxnManager(HibernateTransactionManager txManager) {
        this.txnManager = txManager;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
   public ModelAndView workflow(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
            String result = "";
            boolean isFormSubmit=false;
            boolean commit =false;
            //Create transaction
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setName("JE_Tx");
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
            TransactionStatus status = txnManager.getTransaction(def);
            try {
                int action = Integer.parseInt(request.getParameter("action"));
                switch (action) {
                     case 0:
                        result = workflowService.saveTask(request.getParameter("oldid"), request.getParameter("processid"));
                        break;
                    case 1:
                        isFormSubmit = false;
                        workflowHandler w = new workflowHandler();
                        //result = w.exportBPELFile(request, response);
                        result = w.saveWorkFLow(request, response);

                        break;
                    case 2:
                        String path = ConfigReader.getinstance().get("workflowpath") + request.getParameter("flowid");
                        String fileSep = System.getProperty("file.separator");
                        path = path + System.getProperty("file.separator") + "Export";
                        File fdir = new File(path);
                        if (!fdir.exists()) {
                            fdir.mkdirs();
                        }
                        File file = new File(fdir + System.getProperty("file.separator") + "flow.bpel");
                        FileInputStream fis = null;
                        //File fp = new File(file);
                        if (file.exists() && file.canRead())  {
                            byte[] buf = new byte[(int) file.length()];
                            fis = new FileInputStream(fdir + System.getProperty("file.separator") + "flow.bpel");
                            fis.read(buf, 0, buf.length);
                            fis.close();

                            response.setHeader("Content-Disposition", request.getParameter("dtype")+ "; filename=\"" + "flow.bpel"+ "\";");
                            response.setContentType("application/octet-stream");
                            response.getOutputStream().write(buf);

                            response.getOutputStream().flush();
                        }
                        /*isFormSubmit = true;
                        w = new workflowHandler();
                        w.exportWorkFLow(request, response);*/
                        break;
                    case 3:
                        isFormSubmit = true;
                        result = workflowService.createDerivationRule(request.getParameter("fromId"), request.getParameter("toId"), request.getParameter("derivationRule"));
                        break;
                    case 4:
                        result = workflowService.getTaskSteps(request.getParameter("taskid"));
                        break;
                    case 5:
                        result = workflowService.saveTaskSteps(
                                request.getParameter("taskid"),
                                request.getParameter("moduleid"),
                                request.getParameter("taskstepid"),
                                request.getParameter("flag"));
                        break;
                    case 6:
                        result = workflowService.removeDerivations(request.getParameter("fromId"));
                        break;
                    case 7:
                        result = workflowService.getProcesses();
                        break;
                    case 8:
                        isFormSubmit = true;
                        result = workflowService.createProcess(request.getParameter("name"));
                        break;
                    case 9:
                        result = workflowService.removeNodes(getIdArray(request));
                        break;
                    case 10:
                        result = workflowService.makeEntryStartEnd(
                                request.getParameter("type"),
                                request.getParameter("toId"),
                                request.getParameter("processid"));
                        break;
                    case 11:
                        result = workflowService.editActivityTitle(
                                request.getParameter("taskId"),
                                request.getParameter("title"));
                        break;
                    case 12:
                        result = workflowService.getTriggers(
                                Integer.parseInt(request.getParameter("start")),
                                Integer.parseInt(request.getParameter("limit")),
                                request.getParameter("ss"));
                        break;
                    case 13:
                        isFormSubmit = true;
                        result = workflowService.saveTriggers(
                                request.getParameter("flag"),
                                request.getParameter("triggerid"),
                                request.getParameter("name"),
                                request.getParameter("description"),
                                request.getParameter("script"));
                        break;
                    case 14:
                        result = workflowService.reloadWorkflow(request.getParameter("processid"));
                        break;
                    case 15:
                        result = workflowService.getUnassignedModulesForTask(
                                request.getParameter("reportFlag"),
                                request.getParameter("taskId"));
                        break;
                    case 16:
                        result = workflowService.getProcessTasks(request.getParameter("processid"));
                        break;
                    case 17:
                        result = workflowService.getderivedTasks(request.getParameter("parenttaskid"));
                        break;
                }
                if (commit) {
                    txnManager.commit(status);
                } else {
                    txnManager.rollback(status);
                }
            } catch (NumberFormatException e) {
                logger.warn(e.getMessage(), e);
                txnManager.rollback(status);
            } catch (ServiceException ex) {
                logger.warn(ex.getMessage(), ex);
                txnManager.rollback(status);
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
                txnManager.rollback(status);
            }
            if(isFormSubmit){
                return new ModelAndView("jsonView-ex", "model", result);
            }
            return new ModelAndView("jsonView", "model", result);
    }

    private String[] getIdArray(HttpServletRequest request) {
        String[] ids = null;
        JSONArray temp = null;
        try {
            temp = new JSONArray(request.getParameter("ids"));
            ids = new String[temp.length()];
            for (int cnt = 0; cnt < temp.length(); cnt++) {
                JSONObject obj = temp.getJSONObject(cnt);
                ids[cnt] = obj.getString("id");
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
        }
        return ids;
    }
}
