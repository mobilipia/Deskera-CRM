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
import com.krawler.esp.hibernate.impl.mb_processChart;
import com.krawler.esp.hibernate.impl.pm_taskmaster;
import com.krawler.esp.utils.ConfigReader;
import com.krawler.formbuilder.servlet.ObjectInfo;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import com.krawler.workflow.dao.WorkflowDAO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author krawler-user
 */
public class WorkflowServiceImpl implements WorkflowService {
    private static final Log logger = LogFactory.getLog(WorkflowServiceImpl.class);
    private WorkflowDAO workflowDAOObj;

    public void setWorkflowDAOObj(WorkflowDAO workflowDAOObj) {
        this.workflowDAOObj = workflowDAOObj;
    }
    
    @Override
    public String saveTask(String oldId, String processid) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            JSONObject jtemp2 = new JSONObject();
            jtemp2.put("refId", workflowDAOObj.saveTask("Activity", "", processid));
            jtemp2.put("oldId", oldId);

            result = jtemp2.toString();

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.saveTask", e);
        }
        return result;
    }

    @Override
    public String createDerivationRule(String fromid, String toid, String derivationrule) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            workflowDAOObj.createDerivationRule(fromid, toid, getDerivationId(derivationrule));
            result = "{\"success\":true}";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("reportbuilder.createComboFilter", e);
        }
        return result;
    }

    private Integer getDerivationId(String derivationRule) throws ServiceException {
        Integer derivationId = 0;
        if (derivationRule.equals("sequence")) {
            derivationId = 0;
        } else if (derivationRule.equals("selection")) {
            derivationId = 1;
        } else if (derivationRule.equals("evaluation")) {
            derivationId = 2;
        } else if (derivationRule.equals("parallel")) {
            derivationId = 3;
        } else if (derivationRule.equals("parallelevaluation")) {
            derivationId = 4;
        } else if (derivationRule.equals("paralleljoin")) {
            derivationId = 5;
        }
        return derivationId;
    }

    @Override
    public String getTaskSteps(String taskid) throws ServiceException{
        String result = "{\"data\":[]}";
        try {
            JSONObject jbj = new JSONObject();
            List ls = workflowDAOObj.getTaskSteps(taskid);
            Iterator ite = ls.iterator();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject jtemp2 = new JSONObject();
                jtemp2.put("moduleid", row[1]);
                jtemp2.put("modulename", row[3]);
                jtemp2.put("id", row[0]);
                jbj.append("data", jtemp2);
            }
            if (jbj.has("data")) {
                result = jbj.toString();
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.getTaskSteps", e);
        }
        return result;
    }

    @Override
    public String saveTaskSteps(String taskid, String moduleid, String taskstepid, String flag) throws ServiceException {
        String result = "{\"success\":false, 'msg':'Error occured at server'}";
        try {
            if (flag.equals("add")) {
                workflowDAOObj.addTaskSteps(taskid, moduleid);
                result = "{\"success\":true, 'msg':'Step assigned successfully'}";
            } else if (flag.equals("delete")) {
                workflowDAOObj.removeTaskSteps(taskstepid);

                result = "{\"success\":true, 'msg':'Assigned step removed successfully'}";
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false, 'msg':'Error occured at server'}";
            throw ServiceException.FAILURE("workflow.saveTaskSteps", e);
        }
        return result;
    }

    @Override
    public String removeDerivations(String formid) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            workflowDAOObj.removeDerivations(formid);
            result = "{\"success\":true}";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.getTaskSteps", e);
        }
        return result;
    }

    @Override
    public String getProcesses() throws ServiceException{
        String result = "";
        JSONObject jobj = new JSONObject();
        try {
            List ls = workflowDAOObj.getProcesses();
            Iterator ite3 = ls.iterator();
            while (ite3.hasNext()) {
                JSONObject jtemp1 = new JSONObject();
                mb_processChart rowObj = (mb_processChart) ite3.next();
                jtemp1.put("id", "process_" + rowObj.getProcessid());
                jtemp1.put("processid", rowObj.getProcessid());
                jtemp1.put("processname", rowObj.getProcessname());
                jobj.append("data", jtemp1);
            }
            if (jobj.length() > 0) {
                result = jobj.toString();
            } else {
                result = "{'data':[]}";
            }
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("Workflowhandler.fetchTreeData", e);
        }
        return result;
    }

    @Override
    public String createProcess(String processName) throws ServiceException{
        String result = "{'success' : false}";
        try {
            workflowDAOObj.createProcess(processName);
            JSONObject jobj = new JSONObject();
            jobj.put("success", "true");
            result = jobj.toString();
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("Workflowhandler.createProject", e);
        }
        return result;
    }

    @Override
    public String removeNodes(String[] ids) throws ServiceException{
        String result = "{\"success\":true}";
        try {
            int num = workflowDAOObj.removeNodes(ids);
            if (num == 0) {
                result = "{\"success\":false,\"msg\":\"Error occured while deleting derivations\"}";
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.getTaskSteps", e);
        }
        return result;
    }

    @Override
    public String makeEntryStartEnd(String type, String toId, String processid) throws ServiceException{
        String result = "{\"success\":false}";
        try {
            String taskid=workflowDAOObj.makeEntryStartEnd(type, toId, processid);
            result = "{\"success\":true,\"refId\":\"" + taskid + "\"}";
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.makeEntryStartEnd", e);
        }
        return result;
    }

    @Override
    public String editActivityTitle(String taskid, String title) throws ServiceException{
        String result = "{\"success\":true}";
        try {
            workflowDAOObj.editActivityTitle(taskid, title);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.getTaskSteps", e);
        }
        return result;
    }

    @Override
    public String getTriggers(int start, int limit, String ss) throws ServiceException{
         String result = "{'TotalCount':0,'data':[]}";
        try {
            List ls=workflowDAOObj.getTriggers(start, limit, ss);
            JSONObject jbj = new JSONObject();
            Iterator ite = ls.iterator();
            while (ite.hasNext()) {
                Object[] row = (Object[]) ite.next();
                JSONObject jtemp2 = new JSONObject();
                jtemp2.put("triggerid", row[0]);
                jtemp2.put("name", row[1]);
                jtemp2.put("description", row[2]);
                jtemp2.put("script1", row[3]);
                jbj.append("data", jtemp2);
            }
            if (jbj.has("data")) {
                result = jbj.toString();
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.getTriggers", e);
        }
        return result;
   }

    @Override
    public String saveTriggers(String flag, String triggerid, String name, String desc, String script) throws ServiceException {
        String result = "{\"success\":false, 'msg':'Error occured at server'}";
        try {
            if ("0".equals(flag)) {
                workflowDAOObj.createTrigger(name, desc, script);
                result = "{\"success\":true, 'msg':'Trigger created successfully'}";
            } else if ("1".equals(flag)) {
                workflowDAOObj.updateTrigger(triggerid, name, desc, script);
                result = "{\"success\":true, 'msg':'Trigger edited successfully'}";
            } else if ("2".equals(flag)) {
                workflowDAOObj.deleteTrigger(triggerid);
                result = "{\"success\":true, 'msg':'Trigger deleted successfully'}";
            }

        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            result = "{\"success\":false, 'msg':'Error occured at server'}";
            throw ServiceException.FAILURE("workflow.saveTriggers", e);
        }
        return result;
    }

    @Override
    public String reloadWorkflow(String processid) throws ServiceException {
        String result = "{\"success\":false}";
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            String path = ConfigReader.getinstance().get("workflowpath") + processid;

            File fdir = new File(path);
            File file = new File(fdir + System.getProperty("file.separator") + "bpmn.xml");
            Document doc = docBuilder.parse(file);
            int s;
            int a;
            String name="";
            ObjectInfo obj = new ObjectInfo();
            ArrayList taskContainer = new ArrayList();

            String nodeName="";
            NodeList nodeList = doc.getElementsByTagName("Pool");
            for (s = 1; s < nodeList.getLength(); s++) {
                Node node = nodeList.item(s);
                ObjectInfo processObj = new ObjectInfo();
                processObj.type="process";
                getNodeInfo(node,processObj);
                NodeList childrenList = node.getChildNodes();
                for (int cnt = 0; cnt < childrenList.getLength(); cnt++) {
                    node = childrenList.item(cnt);
                    nodeName=node.getNodeName();
                    if (nodeName.compareToIgnoreCase("Lanes") == 0){
                        NodeList laneList = node.getChildNodes();
                        for (int laneCount = 0; laneCount < laneList.getLength(); laneCount++) {
                            node = laneList.item(laneCount);
                            nodeName=node.getNodeName();
                            if (nodeName.compareToIgnoreCase("Lane") == 0) {
                                ObjectInfo laneObj = new ObjectInfo();
                                laneObj.type = "lane";
                                getNodeInfo(node,laneObj);
                                NodeList laneChildren = node.getChildNodes();
                                for (int laneChildrencnt = 0; laneChildrencnt < laneChildren.getLength(); laneChildrencnt++) {
                                    node = laneChildren.item(laneChildrencnt);
                                    nodeName=node.getNodeName();
                                    if (nodeName.compareToIgnoreCase("NodeGraphicsInfos") == 0) {
                                        getGraphicsNodeInfo(getActivityNode(node, 1), laneObj);
                                    }
                                }
                                taskContainer.add(laneObj);
                            }
                        }
                    }else{
                        if (nodeName.compareToIgnoreCase("NodeGraphicsInfos") == 0) {
                            getGraphicsNodeInfo(getActivityNode(node, 1),processObj);
                        }
                    }

                }
                taskContainer.add(processObj);
            }

            nodeList = doc.getElementsByTagName("Activities");

            for (s = 0; s < nodeList.getLength(); s++) {
                name = "";
                Node node = nodeList.item(s);
                NodeList childrenList = node.getChildNodes();
                for (int cnt = 0; cnt < childrenList.getLength(); cnt++) {
                    node = childrenList.item(cnt);
                    Node activityNode = getActivityNode(node, 0);
                    if (activityNode.getNodeType() == Node.ELEMENT_NODE) {
                        obj = new ObjectInfo();
                        obj.type="activity";
                        getNodeInfo(activityNode,obj);
                        Node graphicsInfoNode = getActivityNode(activityNode, 1);
                        getGraphicsNodeInfo(graphicsInfoNode,obj);
                        taskContainer.add(obj);
                    }
                }
            }


            JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
            for (int j = 0; j < taskContainer.size(); j++) {
                obj = (ObjectInfo) taskContainer.get(j);
                JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
                jobj.put("Id", obj.objId);
                jobj.put("name", obj.name);
                jobj.put("xpos", obj.xpos);
                jobj.put("ypos", obj.ypos);
                jobj.put("height", obj.height);
                jobj.put("width", obj.width);
                jobj.put("parent", obj.parentId);
                jobj.put("refId", obj.refId);
                jobj.put("hasStart", obj.hasStart);
                jobj.put("hasEnd", obj.hasEnd);
                jobj.put("startRefId", obj.startRefId);
                jobj.put("endRefId", obj.endRefId);
                jobj.put("derivationRule", obj.derivationRule);
                jobj.put("domEl", obj.domEl);
                if (obj.type.compareToIgnoreCase("activity") == 0){
                    jtemp.append("data", jobj);
                }else if (obj.type.compareToIgnoreCase("process") == 0){
                    jtemp.append("processes", jobj);
                }else if (obj.type.compareToIgnoreCase("lane") == 0){
                    jtemp.append("lanes", jobj);
                }
            }

            NodeList transitionList = doc.getElementsByTagName("Transitions");
            for (int i = 0; i < transitionList.getLength(); i++) {
                Node node = transitionList.item(i);
                NodeList childrenList = node.getChildNodes();

                for (int cnt = 0; cnt < childrenList.getLength(); cnt++) {
                    node = childrenList.item(cnt);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        JSONObject jobj = new com.krawler.utils.json.base.JSONObject();
                        NamedNodeMap attr = node.getAttributes();
                        for (int b = 0; b < attr.getLength(); b++) {
                            Node attribute = attr.item(b);
                            name = attribute.getNodeName();
                            if (name.compareToIgnoreCase("From") == 0) {
                                jobj.put("fromId", attribute.getNodeValue());
                            } else if (name.compareToIgnoreCase("To") == 0) {
                                jobj.put("toId", attribute.getNodeValue());
                            }
                        }
                        jtemp.append("Lines", jobj);
                    }
                }
            }

            return jtemp.toString();
        } catch (ParserConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.reloadWorkflow", ex);
        } catch (SAXException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.reloadWorkflow", ex);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.reloadWorkflow", ex);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.reloadWorkflow", ex);
        }
    }

    @Override
    public String getUnassignedModulesForTask(String reportFlag, String taskId) throws ServiceException{
        JSONObject jobj = new JSONObject();
        String result = "{'data':[]}";
        try {
            String sql = "";
            List ls = null;
            Iterator ite = null;
            if(reportFlag == null) {
                ite = workflowDAOObj.getUnassignedModulesForTask(taskId).iterator();
                while (ite.hasNext()) {
                    Object[] row = (Object[]) ite.next();
                    JSONObject  jtemp2 = new JSONObject();
                    jtemp2.put("moduleid", row[0]);
                    jtemp2.put("modulename", row[1]);
                    jtemp2.put("tablename", row[2]);
                    jtemp2.put("mastertype", row[3]);
                    jobj.append("data", jtemp2);
                }
            }
            result = jobj.toString();
        } catch (JSONException e) {
            logger.warn(e.getMessage(), e);
            result = "{'data':[]}";
            throw ServiceException.FAILURE("FormServlet.getAllModulesForCombo", e);
        } finally {
            return result;
        }
    }

    @Override
    public String getProcessTasks(String processId) throws ServiceException {
         String result = "{'success' : false}";
         JSONObject jobj = new JSONObject();
        try {
            Iterator ite3 = workflowDAOObj.getProcessTasks(processId).iterator();
            while(ite3.hasNext()){
                JSONObject jtemp1 = new JSONObject();
                pm_taskmaster rowObj = (pm_taskmaster) ite3.next();
                jtemp1.put("taskid",rowObj.getTaskid());
                jtemp1.put("taskname",rowObj.getTaskname());
                jobj.append("data",jtemp1);
            }
            result = jobj.toString();
       } catch(Exception e){
           logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("Workflowhandler.getProcessTasks", e);
        }
        return result;
    }

    @Override
    public String getderivedTasks(String parentTaskId) throws ServiceException {
        String result = "{'success' : false}";
        JSONObject jobj = new JSONObject();
        try {
            Iterator ite3 = workflowDAOObj.getderivedTasks(parentTaskId).iterator();
            while(ite3.hasNext()){
                JSONObject jtemp1 = new JSONObject();
                Object[] row = (Object[]) ite3.next();
                pm_taskmaster taskObj = (pm_taskmaster) row[0];
                jtemp1.put("taskid",taskObj.getTaskid());
                jtemp1.put("taskname",taskObj.getTaskname());
//                jtemp1.put("processid",taskObj.getProcessid().getProcessid());

                // insert code to retrieve condition rule according to taskid
                jtemp1.put("id",row[1]);
                jobj.append("data",jtemp1);
            }
            result = jobj.toString();
        } catch(Exception e){
            logger.warn(e.getMessage(), e);
            throw ServiceException.FAILURE("Workflowhandler.getderivedTasks", e);
        }
        return result;
    }

    private void getNodeInfo(Node node, ObjectInfo obj) {
        int a;
        String name = "";
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            NamedNodeMap attributes = node.getAttributes();
            for (a = 0; a < attributes.getLength(); a++) {
                Node attribute = attributes.item(a);
                name = attribute.getNodeName();
                if (name.compareToIgnoreCase("Id") == 0) {
                    obj.objId = attribute.getNodeValue();
                } else if (name.compareToIgnoreCase("Name") == 0) {
                    obj.name = attribute.getNodeValue();
                } else if (name.compareToIgnoreCase("Parent") == 0) {
                    obj.parentId = attribute.getNodeValue();
                }else if (name.compareToIgnoreCase("ParentPool") == 0) {
                    obj.domEl = attribute.getNodeValue();
                } else if (name.compareToIgnoreCase("refId") == 0) {
                    obj.refId = attribute.getNodeValue();
                } else if (name.compareToIgnoreCase("hasStart") == 0) {
                    obj.hasStart = attribute.getNodeValue();
                } else if (name.compareToIgnoreCase("hasEnd") == 0) {
                    obj.hasEnd = attribute.getNodeValue();
                } else if (name.compareToIgnoreCase("startRefId") == 0) {
                    obj.startRefId = attribute.getNodeValue();
                } else if (name.compareToIgnoreCase("endRefId") == 0) {
                    obj.endRefId = attribute.getNodeValue();
                } else if (name.compareToIgnoreCase("derivationRule") == 0) {
                    obj.derivationRule = attribute.getNodeValue();
                } else if (name.compareToIgnoreCase("domEl") == 0) {
                    obj.domEl = attribute.getNodeValue();
                }

            }
        }
    }
    private Node getActivityNode(Node child, int status) {
        if (child.getNodeType() == Node.ELEMENT_NODE) {
            if (child.getNodeName().compareToIgnoreCase("Activity") == 0 && status == 0) {
                return child;
            } else if (child.getNodeName().compareToIgnoreCase("NodeGraphicsInfo") == 0 && status == 1) {
                return child;
            } else {
                NodeList innerchildren = child.getChildNodes();
                for (int j = 0; j < innerchildren.getLength(); j++) {
                    Node innerchild = innerchildren.item(j);
                    child = getActivityNode(innerchild, status);
                    if (child.getNodeName().compareToIgnoreCase("Activity") == 0) {
                        break;
                    } else if (child.getNodeName().compareToIgnoreCase("NodeGraphicsInfo") == 0) {
                        break;
                    }
                }
            }
        }
        return child;
    }
    
    private void getGraphicsNodeInfo(Node node, ObjectInfo obj) {
        String name = "";
        NamedNodeMap attribute = node.getAttributes();
        for (int b = 0; b < attribute.getLength(); b++) {
            Node attribute1 = attribute.item(b);
            name = attribute1.getNodeName();
            if (name.compareToIgnoreCase("Height") == 0) {
                obj.height = attribute1.getNodeValue();
            } else if (name.compareToIgnoreCase("Width") == 0) {
                obj.width = attribute1.getNodeValue();
            }
        }
        NodeList innerchildren = node.getChildNodes();
        for (int c = 0; c < innerchildren.getLength(); c++) {
            Node cordinateNode = innerchildren.item(c);
            if (cordinateNode.getNodeType() == Node.ELEMENT_NODE) {
                NamedNodeMap attr = cordinateNode.getAttributes();
                for (int b = 0; b < attr.getLength(); b++) {
                    Node attribute1 = attr.item(b);
                    name = attribute1.getNodeName();
                    if (name.compareToIgnoreCase("XCoordinate") == 0) {
                        obj.xpos = attribute1.getNodeValue();
                    } else if (name.compareToIgnoreCase("YCoordinate") == 0) {
                        obj.ypos = attribute1.getNodeValue();
                    }
                }
            }
        }
    }

    public String importWorkflow(String processid) throws ServiceException {
        String result = "{\"success\":false}";
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            String path = ConfigReader.getinstance().get("workflowpath") + processid;

            File fdir = new File(path);
            File file = new File(fdir + System.getProperty("file.separator") + "bpmn.xml");
            Document doc = docBuilder.parse(file);
            int s;
            int a;
            String name="";
            ObjectInfo obj = new ObjectInfo();

            HashMap<String, ObjectInfo> activityHm = new HashMap<String, ObjectInfo>();
            NodeList nodeList = doc.getElementsByTagName("Activity");

            for (s = 0; s < nodeList.getLength(); s++) {
                name = "";
                Node node = nodeList.item(s);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    obj = new ObjectInfo();
                    obj.type = getNodeType(node);
                    getNodeInfo(node, obj);
                    if (obj.type.equals("activity")) {
                        Node graphicsInfoNode = getActivityNode(node, 1);
                        getGraphicsNodeInfo(graphicsInfoNode, obj);
                    }
                    activityHm.put(obj.objId, obj);
                }

            }

            NodeList transitionList = doc.getElementsByTagName("Transitions");
            String fromId="";
            String toId="";
            ObjectInfo fromObj;
            ObjectInfo toObj;
            ObjectInfo tempObj;
            JSONObject jobj;
            JSONObject jtemp = new com.krawler.utils.json.base.JSONObject();
            HashMap<String, String> fromConditionHm = new HashMap<String, String>();
            MultiMap toConditionHm = new MultiHashMap();
            for (int i = 0; i < transitionList.getLength(); i++) {
                Node node = transitionList.item(i);
                NodeList childrenList = node.getChildNodes();
                for (int cnt = 0; cnt < childrenList.getLength(); cnt++) {
                    node = childrenList.item(cnt);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        NamedNodeMap attr = node.getAttributes();
                        for (int b = 0; b < attr.getLength(); b++) {
                            Node attribute = attr.item(b);
                            name = attribute.getNodeName();
                            if (name.compareToIgnoreCase("From") == 0) {
                                fromId=attribute.getNodeValue();
                            } else if (name.compareToIgnoreCase("To") == 0) {
                                toId=attribute.getNodeValue();
                            }
                        }
                        fromObj=activityHm.get(fromId);
                        toObj=activityHm.get(toId);
                        if (fromObj.type.equals("start")){
                            tempObj = new ObjectInfo();
                            tempObj = activityHm.get(toId);
                            tempObj.hasStart="true";
                            activityHm.put(toId,tempObj);
                            continue;
                        }if (toObj.type.equals("end")){
                            tempObj = new ObjectInfo();
                            tempObj = activityHm.get(fromId);
                            tempObj.hasEnd="true";
                            activityHm.put(fromId,tempObj);
                            continue;
                        }if (fromObj.type.equals("activity") && toObj.type.equals("activity")){
                            jobj = new com.krawler.utils.json.base.JSONObject();
                            jobj.put("fromId", "flowPanel"+fromId);
                            jobj.put("toId", "flowPanel"+toId);
                            jtemp.append("Lines", jobj);
                            tempObj = new ObjectInfo();
                            tempObj = activityHm.get(fromId);
                            tempObj.derivationRule="sequence";
                            activityHm.put(fromId,tempObj);
                            continue;
                        }if (fromObj.type.equals("activity") && toObj.type.equals("condition")){
                            fromConditionHm.put(toId, fromId);
                            tempObj = new ObjectInfo();
                            tempObj = activityHm.get(fromId);
                            tempObj.derivationRule="evaluation";
                            activityHm.put(fromId,tempObj);
                            continue;
                        }if (fromObj.type.equals("condition") && toObj.type.equals("activity")){
                            toConditionHm.put(fromId, toId);
                            continue;
                        }
                    }
                }
            }

            Set keys=activityHm.keySet();
            Iterator ite=keys.iterator();
            while(ite.hasNext()){
                String key = (String) ite.next();
                obj = new ObjectInfo();
                obj=activityHm.get(key);
                if (obj.type.equals("activity")) {
                    jobj = new com.krawler.utils.json.base.JSONObject();
                    jobj.put("Id", "flowPanel"+obj.objId);
                    jobj.put("name", obj.name);
                    jobj.put("xpos", obj.xpos);
                    jobj.put("ypos", obj.ypos);
                    jobj.put("height", obj.height);
                    jobj.put("width", obj.width);
                    jobj.put("parent", obj.parentId);
                    jobj.put("refId", obj.refId);
                    jobj.put("hasStart",obj.hasStart );
                    jobj.put("hasEnd", obj.hasEnd);
                    jobj.put("startRefId", obj.startRefId);
                    jobj.put("endRefId", obj.endRefId);
                    jobj.put("derivationRule", obj.derivationRule);
                    jobj.put("domEl", obj.domEl);
                    jtemp.append("data", jobj);
                }
            }

            keys=fromConditionHm.keySet();
            ite=keys.iterator();
            Iterator ite1=null;
            String key="";
            while(ite.hasNext()){
                key = (String) ite.next();
                fromId=fromConditionHm.get(key);
                List toList = (List) toConditionHm.get(key);
                ite1=toList.iterator();
                while (ite1.hasNext()) {
                    toId = (String) ite1.next();
                    jobj = new com.krawler.utils.json.base.JSONObject();
                    jobj.put("fromId", "flowPanel" + fromId);
                    jobj.put("toId", "flowPanel" + toId);
                    jtemp.append("Lines", jobj);
                }
            }
            return jtemp.toString();
        } catch (ParserConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.reloadWorkflow", ex);
        } catch (SAXException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.reloadWorkflow", ex);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.reloadWorkflow", ex);
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            result = "{\"success\":false}";
            throw ServiceException.FAILURE("workflow.reloadWorkflow", ex);
        }
    }

    private String getNodeType(Node node) {
        String name="";
        String type="";
        NodeList childrenList = node.getChildNodes();

        for (int cnt = 0; cnt < childrenList.getLength(); cnt++) {
            node = childrenList.item(cnt);
            name = node.getNodeName();
            if (name.equals("Implementation")) {
                type = "activity";
                break;
            } else if (name.equals("Route")) {
                type = "condition";
                break;
            }else if (name.equals("Event")) {
                NodeList innerchildren = node.getChildNodes();
                for (int c = 0; c < innerchildren.getLength(); c++) {
                    Node eventNode = innerchildren.item(c);
                    name = eventNode.getNodeName();
                    if (name.equals("StartEvent")) {
                        type = "start";
                        break;
                    } else if (name.equals("EndEvent")) {
                        type = "end";
                        break;
                    }
                }
                break;
            }
        }
        return type;
    }
}
