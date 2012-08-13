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

package com.krawler.workflow.bizservice;

import com.krawler.common.service.ServiceException;

/**
 *
 * @author krawler-user
 */
public interface WorkflowService {

    public String saveTask(String oldid, String processid) throws ServiceException;

    public String createDerivationRule(String fromid, String toid, String derivationrule) throws ServiceException;

    public String getTaskSteps(String taskid) throws ServiceException;

    public String saveTaskSteps(String taskid, String moduleid, String taskstepid, String flag) throws ServiceException;

    public String removeDerivations(String formid) throws ServiceException;

    public String getProcesses() throws ServiceException;

    public String createProcess(String processName) throws ServiceException;

    public String removeNodes(String[] ids) throws ServiceException;

    public String makeEntryStartEnd(String type, String toId, String processid) throws ServiceException;

    public String editActivityTitle(String taskid, String title) throws ServiceException;

    public String getTriggers(int start, int limit, String ss) throws ServiceException;

    public String saveTriggers(String flag, String triggerid, String name, String desc, String script) throws ServiceException;

    public String reloadWorkflow(String processid) throws ServiceException ;

    public String getUnassignedModulesForTask(String reportFlag, String taskId) throws ServiceException;

    public String getProcessTasks(String processId) throws ServiceException;

    public String getderivedTasks(String parentTaskId) throws ServiceException;

}
