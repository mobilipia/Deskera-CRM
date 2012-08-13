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

package com.krawler.workflow.dao;

import java.util.List;

/**
 *
 * @author krawler-user
 */
public interface WorkflowDAO {
    public String saveTask(String taskName, String desc, String processid);

    public void createDerivationRule(String fromid, String toid, Integer derivationId);

    public List getTaskSteps(String taskid);

    public void addTaskSteps(String taskid, String moduleid);

    public void removeTaskSteps(String taskstepid);

    public void removeDerivations(String formid);

    public List getProcesses();

    public void createProcess(String processName);

    public int removeNodes(String[] ids);

    public String makeEntryStartEnd(String type, String toId, String processid);

    public void editActivityTitle(String taskid, String title);

    public void createTrigger(String name, String desc, String script);

    public void updateTrigger(String triggerid, String name, String desc, String script);

    public void deleteTrigger(String triggerid);

    public List getTriggers(int start, int limit, String searchString);

    public List getUnassignedModulesForTask(String taskId);

    public List getProcessTasks(String processId);

    public List getderivedTasks(String parentTaskId);

}
