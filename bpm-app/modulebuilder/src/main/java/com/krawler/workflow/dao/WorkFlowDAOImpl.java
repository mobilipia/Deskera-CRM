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

import com.krawler.common.util.StringUtil;
import com.krawler.crm.utils.PropsValues;
import com.krawler.dao.BaseDAO;
import com.krawler.esp.hibernate.impl.mb_permactions;
import com.krawler.esp.hibernate.impl.mb_permgrmaster;
import com.krawler.esp.hibernate.impl.mb_processChart;
import com.krawler.esp.hibernate.impl.mb_reportlist;
import com.krawler.esp.hibernate.impl.pm_derivationmaster;
import com.krawler.esp.hibernate.impl.pm_taskderivationmap;
import com.krawler.esp.hibernate.impl.pm_taskmaster;
import com.krawler.esp.hibernate.impl.pm_taskstepmap;
import com.krawler.esp.hibernate.impl.pm_triggermaster;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author krawler-user
 */
public class WorkFlowDAOImpl extends BaseDAO implements WorkflowDAO {

    @Override
    public String saveTask(String taskName, String desc, String processid) {
            pm_taskmaster taskObj = new pm_taskmaster();
            mb_processChart procObj =  (mb_processChart)get(mb_processChart.class, processid);
            taskObj.setProcessid(procObj);
            taskObj.setDescription(desc);
            taskObj.setTaskname(taskName);
            return save(taskObj).toString();
    }

    @Override
    public void createDerivationRule(String fromid, String toid, Integer derivationId) {
            pm_taskderivationmap pm_taskderivationmapObj = new pm_taskderivationmap();
            pm_taskmaster childtaskidObj = (pm_taskmaster) get(pm_taskmaster.class, toid);
            pm_taskmaster parenttaskidObj = (pm_taskmaster) get(pm_taskmaster.class, fromid);
            pm_derivationmaster pm_derivationmasterObj = (pm_derivationmaster) get(pm_derivationmaster.class, derivationId);

            pm_taskderivationmapObj.setChildtaskid(childtaskidObj);
            pm_taskderivationmapObj.setParenttaskid(parenttaskidObj);
            pm_taskderivationmapObj.setDerivationid((pm_derivationmasterObj));

            save(pm_taskderivationmapObj);
    }

    @Override
    public List getTaskSteps(String taskid) {
            String hql = "Select pm_taskstepmap.id, pm_taskstepmap.stepid, pm_taskstepmap.steptype, mb_reportlist.reportname from " + PropsValues.PACKAGE_PATH + ".pm_taskstepmap as pm_taskstepmap, com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist where pm_taskstepmap.taskid.taskid = ? and pm_taskstepmap.stepid=mb_reportlist.reportid";
            return executeQuery(hql, taskid);
    }

    @Override
    public void addTaskSteps(String taskid, String moduleid) {
                mb_reportlist modObj = (mb_reportlist) get(mb_reportlist.class, moduleid);
                int steptype = modObj.getType();
                pm_taskmaster taskObj = (pm_taskmaster) get(pm_taskmaster.class, taskid);

                pm_taskstepmap taskStepObj = new pm_taskstepmap();
                taskStepObj.setStepid(moduleid);
                taskStepObj.setSteptype(steptype);
                taskStepObj.setTaskid(taskObj);
                save(taskStepObj);

                //Check for comments and documents
                String SELECT_QUERY = "select mb_moduleConfigMap.configid.configid from com.krawler.esp.hibernate.impl.mb_moduleConfigMap " +
                        "as mb_moduleConfigMap where mb_moduleConfigMap.moduleid = ? ";
                List configlist = executeQuery(SELECT_QUERY, new Object[]{modObj});
                Iterator ite = configlist.iterator();
                boolean commentFlag = false;
                boolean docFlag = false;
                while (ite.hasNext()) {
                    int configid = (Integer) ite.next();
                    if (configid == 1) { // Comments
                        commentFlag = true;
                    } else if (configid == 2) { // Documents
                        docFlag = true;
                    }
                }

                //Add access right permissions
                com.krawler.esp.hibernate.impl.mb_permmaster permmaster = null;
                mb_permgrmaster permgrmaster = new mb_permgrmaster();
                //To do - need to uncomment
//                accessRight.addPermGrp(session, permgrmaster, modObj, taskObj);
                for (int i = 2; i < 9; i++) {
                    if (i < 5 || ((i == 5 || i == 6) && commentFlag) || ((i == 7 || i == 8) && docFlag)) {
                        permmaster = new com.krawler.esp.hibernate.impl.mb_permmaster();
                        mb_permactions permaction = (mb_permactions) get(mb_permactions.class, i);
                        permmaster.setPermaction(permaction);
                        permmaster.setPermname(permaction.getName());
                        permmaster.setDescription(permaction.getName());
                        permmaster.setPermgrid(permgrmaster);
                        //permmaster.setPermid(accessRight.getMaxPermid(session, permgrmaster.getPermgrid()));
                        permmaster.setPermid(1);
                        save(permmaster);
                    }
                }
    }

    @Override
    public void removeTaskSteps(String taskstepid) {
                pm_taskstepmap taskstepObj = (pm_taskstepmap) get(pm_taskstepmap.class, taskstepid);

                //delete permissions
                mb_reportlist modObj = (mb_reportlist) get(mb_reportlist.class, taskstepObj.getStepid());
                String hql = "from " + PropsValues.PACKAGE_PATH + ".mb_permgrmaster as mb_permgrmaster where mb_permgrmaster.taskid = ? and mb_permgrmaster.reportid = ? ";
                List ls = executeQuery(hql, new Object[]{taskstepObj.getTaskid(), modObj});
                Iterator ite = ls.iterator();
                if (ite.hasNext()) {
                    mb_permgrmaster permgrObj = (mb_permgrmaster) ite.next();
                    hql = "delete from " + PropsValues.PACKAGE_PATH + ".mb_permmaster as mb_permmaster where mb_permmaster.permgrid = ?";
                    executeUpdate(hql, new Object[]{permgrObj});

                    hql = "delete from " + PropsValues.PACKAGE_PATH + ".mb_permgrmaster as mb_permgrmaster where mb_permgrmaster.permgrid = ?";
                    executeUpdate(hql, new Object[]{permgrObj.getPermgrid()});
                }

                hql = "delete from " + PropsValues.PACKAGE_PATH + ".pm_taskstepmap as pm_taskstepmap where pm_taskstepmap.id = ?";
                executeUpdate(hql, new Object[]{taskstepid});
    }

    @Override
    public void removeDerivations(String formid) {
            String hql = "delete from com.krawler.esp.hibernate.impl.pm_taskderivationmap as pm_taskderivationmap where pm_taskderivationmap.parenttaskid.taskid = ?  ";
            executeUpdate(hql, formid);
    }

    @Override
    public List getProcesses() {
            String hql = "from com.krawler.esp.hibernate.impl.mb_processChart as mb_processChart";
            return executeQuery(hql);
    }

    @Override
    public void createProcess(String processName) {
            mb_processChart processObj = new mb_processChart();
            processObj.setProcessname(processName);
            save(processObj);
    }

    @Override
    public int removeNodes(String[] ids) {
            String hql;
            int num = 0;
            for (int cnt = 0; cnt < ids.length; cnt++) {
                hql = "delete from com.krawler.esp.hibernate.impl.pm_taskderivationmap as pm_taskderivationmap where pm_taskderivationmap.parenttaskid.taskid = ?  ";
                executeUpdate(hql, ids[cnt]);
                hql = "delete from com.krawler.esp.hibernate.impl.pm_taskderivationmap as pm_taskderivationmap where pm_taskderivationmap.childtaskid.taskid = ?  ";
                executeUpdate(hql, ids[cnt]);
                hql = "delete from com.krawler.esp.hibernate.impl.pm_taskmaster as pm_taskmaster where pm_taskmaster.taskid = ?  ";
                num = executeUpdate(hql, ids[cnt]);
            }
            return num;
    }

    @Override
    public String makeEntryStartEnd(String type, String toId, String processid) {
            pm_taskmaster taskObj = new pm_taskmaster();
            mb_processChart procObj = (mb_processChart) get(mb_processChart.class, processid);
            taskObj.setProcessid(procObj);
            taskObj.setDescription("");
            taskObj.setTaskname(type);
            String taskid = save(taskObj).toString();

            pm_taskderivationmap pm_taskderivationmapObj = new pm_taskderivationmap();
            pm_taskmaster childtaskidObj;
            pm_taskmaster parenttaskidObj;
            if (type.equals("start")) {
                childtaskidObj = (pm_taskmaster) get(pm_taskmaster.class, toId);
                parenttaskidObj = taskObj;
            } else {
                childtaskidObj = taskObj;
                parenttaskidObj = (pm_taskmaster) get(pm_taskmaster.class, toId);
            }
            pm_derivationmaster pm_derivationmasterObj = (pm_derivationmaster) get(pm_derivationmaster.class, 0); // sequntial derivation
            pm_taskderivationmapObj.setChildtaskid(childtaskidObj);
            pm_taskderivationmapObj.setParenttaskid(parenttaskidObj);
            pm_taskderivationmapObj.setDerivationid((pm_derivationmasterObj));

            save(pm_taskderivationmapObj);
            return taskid;
    }

    @Override
    public void editActivityTitle(String taskid, String title) {
            pm_taskmaster taskObj = (pm_taskmaster) get(pm_taskmaster.class, taskid);
            taskObj.setTaskname(title);
            save(taskObj);
    }

    @Override
    public void createTrigger(String name, String desc, String script) {
                pm_triggermaster triggerObj = new pm_triggermaster();
                triggerObj.setName(name);
                triggerObj.setDescription(desc);
                triggerObj.setScript(script);
                save(triggerObj);
    }

    @Override
    public void updateTrigger(String triggerid, String name, String desc, String script) {
                pm_triggermaster triggerObj = (pm_triggermaster) get(pm_triggermaster.class, triggerid);
                triggerObj.setName(name);
                triggerObj.setDescription(desc);
                triggerObj.setScript(script);
                save(triggerObj);

    }

    @Override
    public void deleteTrigger(String triggerid) {
                String hql = "delete from " + PropsValues.PACKAGE_PATH + ".pm_triggermaster as pm_triggermaster where pm_triggermaster.triggerid = ?";
                executeUpdate(hql, triggerid);
    }

    @Override
    public List getTriggers(int start, int limit, String searchString) {
            ArrayList al = new ArrayList();
            String s1 = "";
            if (searchString != null) {
                s1 = StringUtil.getSearchString(searchString, "where", new String[]{"pm_triggermaster.name", "pm_triggermaster.description"});
                StringUtil.insertParamSearchString(al, searchString, 1);
            }

            String hql = "Select pm_triggermaster.triggerid, pm_triggermaster.name, pm_triggermaster.description, pm_triggermaster.script from " + PropsValues.PACKAGE_PATH + ".pm_triggermaster as pm_triggermaster " + s1 + " order by pm_triggermaster.name ";
            return executeQueryPaging(hql, al.toArray(), new Integer[]{start, limit});
    }

    @Override
    public List getUnassignedModulesForTask(String taskId) {
                String sql = "select mb_reportlist.reportid, mb_reportlist.reportname, mb_reportlist.tablename, 0 as type " +
                        "from com.krawler.esp.hibernate.impl.mb_reportlist as mb_reportlist " +
                        "where mb_reportlist.reportid not in (Select pm_taskstepmap.stepid from " + PropsValues.PACKAGE_PATH + ".pm_taskstepmap as pm_taskstepmap where pm_taskstepmap.taskid.taskid = ?) and mb_reportlist.deleteflag = 0 order by mb_reportlist.reportname ";
                return executeQuery(sql,taskId);
    }

    @Override
    public List getProcessTasks(String processId) {
            String hql = "from "+PropsValues.PACKAGE_PATH+".pm_taskmaster as pm_taskmaster where pm_taskmaster.processid.id = ?";
            return executeQuery(hql,processId);
    }

    @Override
    public List getderivedTasks(String parentTaskId) {
            String hql = "select childtaskid,pm_taskderivationmap.id from "+PropsValues.PACKAGE_PATH+".pm_taskderivationmap as pm_taskderivationmap " +
                    "where pm_taskderivationmap.parenttaskid.taskid = ?";
            return executeQuery(hql,parentTaskId);
    }
}
