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

import com.krawler.esp.utils.ConfigReader;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.transform.Transformer;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.*;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class workflowHandler {
    private static final Log logger = LogFactory.getLog(workflowHandler.class);
    private Document dom;
    private ArrayList poolContainer;
    private ArrayList taskContainer;
    private String parentSplit;

    public workflowHandler() {
        this.poolContainer = new ArrayList();
        this.taskContainer = new ArrayList();
        this.parentSplit = "";
    }

    public String saveWorkFLow(HttpServletRequest request, HttpServletResponse response) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        try {

            String workflow = request.getParameter("jsonnodeobj");
            String linejson = request.getParameter("linejson");
            String processId = request.getParameter("flowid");
            String containerId = request.getParameter("containerId");
            this.parentSplit = request.getParameter("parentSplit");

            String path = ConfigReader.getinstance().get("workflowpath") + processId;

            File fdir = new File(path);
            if (!fdir.exists()) {
                fdir.mkdirs();
            }
            File file = new File(fdir + System.getProperty("file.separator") + "bpmn.xml");
            if (file.exists()) {
                file.delete();
            }

            Writer output = new BufferedWriter(new FileWriter(file));

            JSONObject jsonobj = new JSONObject(workflow);
            JSONObject json_line = new JSONObject(linejson);
            createDocument();
            writeXml(jsonobj, containerId, processId, json_line);

            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans1 = transfac.newTransformer();
            trans1.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans1.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter sw = new StringWriter();
            StreamResult kwcresult = new StreamResult(sw);
            DOMSource kwcsource = new DOMSource(dom);
            trans1.transform(kwcsource, kwcresult);

            output.write(sw.toString());

            output.close();
            return "{success:true}";
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            return "{success:true}";
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
            return "{success:false}";
        }
    }


    public String exportBPELFile(HttpServletRequest request, HttpServletResponse response){
        Writer output = null;
        try {
            String workflow = request.getParameter("jsonnodeobj");
            String linejson = request.getParameter("linejson");
            String processId = request.getParameter("flowid");
            String containerId = request.getParameter("containerId");
            this.parentSplit = request.getParameter("parentSplit");
            String path = ConfigReader.getinstance().get("workflowpath") + processId;
            path = path + System.getProperty("file.separator") + "Export";
            File fdir = new File(path);
            if (!fdir.exists()) {
                fdir.mkdirs();
            }
            File file = new File(fdir + System.getProperty("file.separator") + "flow.bpel");
            if (file.exists()) {
                file.delete();
            }
            output = new BufferedWriter(new FileWriter(file));
            JSONObject jsonobj = new JSONObject(workflow);
            JSONObject json_line = new JSONObject(linejson);
            createDocument();
            expwritebpel(jsonobj, containerId, processId, json_line);
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans1 = transfac.newTransformer();
            trans1.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans1.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter sw = new StringWriter();
            StreamResult kwcresult = new StreamResult(sw);
            DOMSource kwcsource = new DOMSource(dom);
            trans1.transform(kwcsource, kwcresult);
            output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            output.write("\n");
            output.write(sw.toString());

            output.close();
            return "{success:true}";
        } catch (TransformerConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
            return "{success:true}";
        } catch (TransformerException ex) {
            logger.warn(ex.getMessage(), ex);
            return "{success:true}";
        } catch (ParserConfigurationException ex) {
            logger.warn(ex.getMessage(), ex);
            return "{success:true}";
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
            return "{success:true}";
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
            return "{success:true}";
        } finally {
            try {
                output.close();
            } catch (IOException ex) {
                logger.warn(ex.getMessage(), ex);
                return "{success:true}";
            }
        }

    }

    public void exportWorkFLow(HttpServletRequest request, HttpServletResponse response) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        try {

            String workflow = request.getParameter("jsonnodeobj");
            String linejson = request.getParameter("linejson");
            String processId = request.getParameter("flowid");
            String containerId = request.getParameter("containerId");
            this.parentSplit = request.getParameter("parentSplit");

            String path = ConfigReader.getinstance().get("workflowpath") + processId;

            path = path + System.getProperty("file.separator") + "Export";

            File fdir = new File(path);
            if (!fdir.exists()) {
                fdir.mkdirs();
            }
            File file = new File(fdir + System.getProperty("file.separator") + "bpmn.xpdl");
            if (file.exists()) {
                file.delete();
            }

            Writer output = new BufferedWriter(new FileWriter(file));

            JSONObject jsonobj = new JSONObject(workflow);
            JSONObject json_line = new JSONObject(linejson);
            createDocument();
            expwriteXml(jsonobj, containerId, processId, json_line);


            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans1 = transfac.newTransformer();
            trans1.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans1.setOutputProperty(OutputKeys.INDENT, "yes");

            StringWriter sw = new StringWriter();
            StreamResult kwcresult = new StreamResult(sw);
            DOMSource kwcsource = new DOMSource(dom);
            trans1.transform(kwcsource, kwcresult);

            output.write(sw.toString());

            output.close();

        /*response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=export.xml");
        OutputStream out = response.getOutputStream();
        OutputFormat format = new OutputFormat(dom);
        format.setIndenting(true);
        format.setEncoding("UTF-8");
        XMLSerializer serializer = new XMLSerializer(out, format);
        serializer.serialize(dom);
        out.close();*/
        } catch (JSONException ex) {
            logger.warn(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

    private void createDocument() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        dom = db.newDocument();
    }

    private void writeXml(JSONObject jsonobj, String containerId, String processId, JSONObject linejson) throws TransformerConfigurationException, TransformerException, JSONException {
        JSONArray jarr = jsonobj.getJSONArray("data");
        JSONArray linearr = linejson.getJSONArray("data");
        String split = ":";
        for (int i = 0; i < jarr.length(); i++) {

            Pattern p = Pattern.compile(split);

            JSONObject jobj = jarr.getJSONObject(i);
            String id = jobj.getString("id");
            String value = jobj.getString("value");
            String[] ObjectVal = null;

            ObjectVal = p.split(value);
            if (ObjectVal[0].equals("process-swim")) {
                ObjectInfo obj = new ObjectInfo();
                obj.objId = ObjectVal[1];
                obj.name = ObjectVal[2];
                obj.xpos = ObjectVal[3];
                obj.ypos = ObjectVal[4];
                obj.width = ObjectVal[5];
                obj.height = ObjectVal[6];
                obj.parentId = ObjectVal[7];
                obj.type = "Pool";
                obj.handId = "";
                obj.refId = ObjectVal[8];
                this.poolContainer.add(obj);
            } else if (ObjectVal[0].equals("lane-swim")) {
                ObjectInfo obj = new ObjectInfo();
                obj.objId = ObjectVal[1];
                obj.name = ObjectVal[2];
                obj.xpos = ObjectVal[3];
                obj.ypos = ObjectVal[4];
                obj.width = ObjectVal[5];
                obj.height = ObjectVal[6];
                obj.parentId = ObjectVal[7];
                obj.processId = ObjectVal[8];
                obj.refId = ObjectVal[9];
                obj.type = "Lane";
                obj.handId = "";
                this.poolContainer.add(obj);
            } else {
                ObjectInfo obj = new ObjectInfo();
                if (ObjectVal[0].equals("task-activity")) {
                    obj.type = "task";
                } else if (ObjectVal[0].equals("start")) {
                    obj.type = "start";
                    obj.handId = ObjectVal[7];
                } else if (ObjectVal[0].equals("end")) {
                    obj.type = "end";
                    obj.handId = ObjectVal[7];
                }
                obj.objId = ObjectVal[1];
                obj.name = ObjectVal[2];
                obj.xpos = ObjectVal[3];
                obj.ypos = ObjectVal[4];
                obj.width = ObjectVal[5];
                obj.height = ObjectVal[6];
                obj.parentId = ObjectVal[7];
                obj.processId = ObjectVal[8];
                obj.refId = ObjectVal[9];
                obj.hasStart = ObjectVal[10];
                obj.hasEnd = ObjectVal[11];
                obj.startRefId = ObjectVal[12];
                obj.endRefId = ObjectVal[13];
                obj.derivationRule = ObjectVal[14];
                obj.domEl = ObjectVal[15];
                this.taskContainer.add(obj);
            }
        }


        Element rootElet = dom.createElement("Package");
        rootElet.setAttribute("xmlns", "http://www.wfmc.org/2008/XPDL2.1");
        dom.appendChild(rootElet);
        Element ele = dom.createElement("PackageHeader");
        Element childElement = dom.createElement("XPDLVersion");
        Text text = dom.createTextNode("2.1");
        childElement.appendChild(text);
        ele.appendChild(childElement);
        rootElet.appendChild(ele);

        addPools(rootElet, containerId, processId);

        addWorkflow(rootElet, processId, linearr);




    }

    private void addPools(Element rootElet, String containerId, String processId) {
        Element ele = dom.createElement("Pools");
        rootElet.appendChild(ele);

        Element mainPool = dom.createElement("Pool");
        ele.appendChild(mainPool);
        mainPool.setAttribute("Id", containerId);
        mainPool.setAttribute("Process", processId);

        Element lanes = dom.createElement("Lanes");
        mainPool.appendChild(lanes);

        Element graphicsInfos = dom.createElement("NodeGraphicsInfos");
        mainPool.appendChild(graphicsInfos);

        Element graphicsInfo = dom.createElement("NodeGraphicsInfo");
        graphicsInfos.appendChild(graphicsInfo);
        graphicsInfo.setAttribute("Height", "0");
        graphicsInfo.setAttribute("Width", "0");

        Element cordinates = dom.createElement("Coordinates");
        graphicsInfo.appendChild(cordinates);
        cordinates.setAttribute("XCoordinate", "0");
        cordinates.setAttribute("YCoordinate", "0");

        for (int i = 0; i < this.poolContainer.size(); i++) {
            ObjectInfo obj = (ObjectInfo) this.poolContainer.get(i);
            if (obj.type.equals("Pool")) {
                int cnt = 0;
                Element childElement = dom.createElement(obj.type);
                ele.appendChild(childElement);
                childElement.setAttribute("Id", obj.objId);
                childElement.setAttribute("Name", obj.name);
                childElement.setAttribute("refId", obj.refId);
                for (int j = 0; j < this.poolContainer.size(); j++) {
                    ObjectInfo innerObj = (ObjectInfo) this.poolContainer.get(j);
                    if (innerObj.parentId.equals(obj.objId) && innerObj.type.equals("Lane")) {
                        cnt++;
                        if (cnt <= 1) {
                            lanes = dom.createElement("Lanes");
                            childElement.appendChild(lanes);
                        }

                        Element laneElement = dom.createElement(innerObj.type);
                        lanes.appendChild(laneElement);
                        laneElement.setAttribute("Id", innerObj.objId);
                        laneElement.setAttribute("Name", innerObj.name);
                        laneElement.setAttribute("ParentPool", innerObj.parentId);
                        laneElement.setAttribute("refId", innerObj.refId);
                        graphicsInfos = dom.createElement("NodeGraphicsInfos");
                        laneElement.appendChild(graphicsInfos);

                        graphicsInfo = dom.createElement("NodeGraphicsInfo");
                        graphicsInfos.appendChild(graphicsInfo);
                        graphicsInfo.setAttribute("Height", innerObj.height);
                        graphicsInfo.setAttribute("Width", innerObj.width);

                        cordinates = dom.createElement("Coordinates");
                        graphicsInfo.appendChild(cordinates);
                        cordinates.setAttribute("XCoordinate", innerObj.xpos);
                        cordinates.setAttribute("YCoordinate", innerObj.ypos);
                    }
                }

                graphicsInfos = dom.createElement("NodeGraphicsInfos");
                childElement.appendChild(graphicsInfos);

                graphicsInfo = dom.createElement("NodeGraphicsInfo");
                graphicsInfos.appendChild(graphicsInfo);
                graphicsInfo.setAttribute("Height", obj.height);
                graphicsInfo.setAttribute("Width", obj.width);

                cordinates = dom.createElement("Coordinates");
                graphicsInfo.appendChild(cordinates);
                cordinates.setAttribute("XCoordinate", obj.xpos);
                cordinates.setAttribute("YCoordinate", obj.ypos);
            }
        }
    }

    private void addWorkflow(Element rootElet, String processId, JSONArray linearr) throws JSONException {
        Element ele = dom.createElement("WorkflowProcesses");
        rootElet.appendChild(ele);

        for (int i = 0; i < this.poolContainer.size(); i++) {
            int cnt = 0;
            Element processWorkflow = null;
            Element activities = null;
            ObjectInfo obj = (ObjectInfo) this.poolContainer.get(i);
            if (obj.type.equals("Pool")) {
                for (int j = 0; j < this.taskContainer.size(); j++) {

                    ObjectInfo taskObj = (ObjectInfo) this.taskContainer.get(j);
                    if (obj.objId.equals(taskObj.processId)) {
                        cnt++;
                        if (cnt <= 1) {
                            processWorkflow = dom.createElement("WorkflowProcess");
                            ele.appendChild(processWorkflow);
                            processWorkflow.setAttribute("Id", taskObj.processId);
                            processWorkflow.setAttribute("Name", obj.name);
                            processWorkflow.setAttribute("refId", obj.refId);
                            activities = dom.createElement("Activities");
                            processWorkflow.appendChild(activities);
                        }
                        Element activity = dom.createElement("Activity");
                        activities.appendChild(activity);
                        activity.setAttribute("Id", taskObj.objId);
                        activity.setAttribute("Name", taskObj.name);
                        activity.setAttribute("refId", taskObj.refId);
                        activity.setAttribute("Parent", taskObj.parentId);
                        activity.setAttribute("hasStart", taskObj.hasStart);
                        activity.setAttribute("hasEnd", taskObj.hasEnd);
                        activity.setAttribute("startRefId", taskObj.startRefId);
                        activity.setAttribute("endRefId", taskObj.endRefId);
                        activity.setAttribute("derivationRule", taskObj.derivationRule);
                        activity.setAttribute("domEl", taskObj.domEl);
                        Element eventEle = dom.createElement("Event");
                        activity.appendChild(eventEle);
                        Element event = null;
                        Element Implementation = null;
                        if (taskObj.type.equals("start")) {
                        event = dom.createElement("StartEvent");
                        eventEle.appendChild(event);
                        } else if (taskObj.type.equals("end")) {
                        event = dom.createElement("EndEvent");
                        eventEle.appendChild(event);
                        } else if (taskObj.type.equals("task")) {
                        Implementation = dom.createElement("Implementation");
                        eventEle.appendChild(Implementation);
                        Element task = dom.createElement("Task");
                        Implementation.appendChild(task);
                        }


                        Element graphicsInfos = dom.createElement("NodeGraphicsInfos");
                        activity.appendChild(graphicsInfos);

                        Element graphicsInfo = dom.createElement("NodeGraphicsInfo");
                        graphicsInfos.appendChild(graphicsInfo);
                        graphicsInfo.setAttribute("Height", taskObj.height);
                        graphicsInfo.setAttribute("Width", taskObj.width);

                        Element cordinates = dom.createElement("Coordinates");
                        graphicsInfo.appendChild(cordinates);
                        cordinates.setAttribute("XCoordinate", taskObj.xpos);
                        cordinates.setAttribute("YCoordinate", taskObj.ypos);
                    }
                }
                if (cnt >= 1) {
                    Element transitions = dom.createElement("Transitions");
                    processWorkflow.appendChild(transitions);
                    for (int l = 0; l < linearr.length(); l++) {
                        JSONObject lineObj = linearr.getJSONObject(l);
                        String lineid = lineObj.getString("id");
                        String[] line = lineid.split("_");
                        String[] fromLinearr = line[0].split(this.parentSplit);
                        String[] toLinearr = line[1].split(this.parentSplit);
                        if (obj.objId.equals(fromLinearr[1])) {
                            Element transition = dom.createElement("Transition");
                            transitions.appendChild(transition);
                            transition.setAttribute("Id", lineid);
                            transition.setAttribute("From", fromLinearr[0]);
                            transition.setAttribute("To", toLinearr[0]);
                            transition.setAttribute("Name", "");
                        }
                    }
                }
            }
        }
        Element outerWorkflow = dom.createElement("WorkflowProcess");
        ele.appendChild(outerWorkflow);
        outerWorkflow.setAttribute("Id", processId);
        outerWorkflow.setAttribute("Name", "Main Processs");
        Element activities = null;
        for (int j = 0; j < this.taskContainer.size(); j++) {
            ObjectInfo taskObj = (ObjectInfo) this.taskContainer.get(j);
            if (!taskObj.processId.startsWith("process")) {

                if (j < 1) {
                    activities = dom.createElement("Activities");
                    outerWorkflow.appendChild(activities);
                }
                Element activity = dom.createElement("Activity");
                activities.appendChild(activity);
                activity.setAttribute("Id", taskObj.objId);
                activity.setAttribute("Name", taskObj.name);
                activity.setAttribute("refId", taskObj.refId);
                activity.setAttribute("Parent", taskObj.parentId);
                activity.setAttribute("hasStart", taskObj.hasStart);
                activity.setAttribute("hasEnd", taskObj.hasEnd);
                activity.setAttribute("startRefId", taskObj.startRefId);
                activity.setAttribute("endRefId", taskObj.endRefId);
                activity.setAttribute("derivationRule", taskObj.derivationRule);
                activity.setAttribute("domEl", taskObj.domEl);
                Element eventEle = dom.createElement("Event");
            //    activity.appendChild(eventEle);
                Element event = null;
                Element Implementation = null;
                if (taskObj.type.equals("start")) {
                event = dom.createElement("StartEvent");
                eventEle.appendChild(event);
                } else if (taskObj.type.equals("end")) {
                event = dom.createElement("EndEvent");
                eventEle.appendChild(event);
                } else if (taskObj.type.equals("task")) {
                    Implementation = dom.createElement("Implementation");
                    activity.appendChild(Implementation);
                    Element task = dom.createElement("Task");
                    Implementation.appendChild(task);
                }


                Element graphicsInfos = dom.createElement("NodeGraphicsInfos");
                activity.appendChild(graphicsInfos);

                Element graphicsInfo = dom.createElement("NodeGraphicsInfo");
                graphicsInfos.appendChild(graphicsInfo);
                graphicsInfo.setAttribute("Height", taskObj.height);
                graphicsInfo.setAttribute("Width", taskObj.width);

                Element cordinates = dom.createElement("Coordinates");
                graphicsInfo.appendChild(cordinates);
                cordinates.setAttribute("XCoordinate", taskObj.xpos);
                cordinates.setAttribute("YCoordinate", taskObj.ypos);
            }
        }

        Element transitions = dom.createElement("Transitions");
        outerWorkflow.appendChild(transitions);
        for (int l = 0; l < linearr.length(); l++) {
            JSONObject lineObj = linearr.getJSONObject(l);
            String lineid = lineObj.getString("id");
            String[] line = lineid.split("_");
            String[] fromLinearr = line[0].split(this.parentSplit);
            String[] toLinearr = line[1].split(this.parentSplit);
            if (!fromLinearr[1].startsWith("process")) {
                Element transition = dom.createElement("Transition");
                transitions.appendChild(transition);
                transition.setAttribute("Id", lineid);
                transition.setAttribute("From", fromLinearr[0]);
                transition.setAttribute("To", toLinearr[0]);
                transition.setAttribute("Name", "");
            }
        }
    }

    private void expwriteXml(JSONObject jsonobj, String containerId, String processId, JSONObject linejson) throws TransformerConfigurationException, TransformerException, JSONException {
        JSONArray jarr = jsonobj.getJSONArray("data");
        JSONArray linearr = linejson.getJSONArray("data");
        String split = ":";
        for (int i = 0; i < jarr.length(); i++) {

            Pattern p = Pattern.compile(split);

            JSONObject jobj = jarr.getJSONObject(i);
            String id = jobj.getString("id");
            String value = jobj.getString("value");
            String[] ObjectVal = null;

            ObjectVal = p.split(value);
            if (ObjectVal[0].equals("process-swim")) {
                ObjectInfo obj = new ObjectInfo();
                obj.objId = ObjectVal[1];
                obj.name = ObjectVal[2];
                obj.xpos = ObjectVal[3];
                obj.ypos = ObjectVal[4];
                obj.width = ObjectVal[5];
                obj.height = ObjectVal[6];
                obj.parentId = ObjectVal[7];
                obj.type = "Pool";
                obj.handId = "";
                obj.refId = ObjectVal[8];
                this.poolContainer.add(obj);
            } else if (ObjectVal[0].equals("lane-swim")) {
                ObjectInfo obj = new ObjectInfo();
                obj.objId = ObjectVal[1];
                obj.name = ObjectVal[2];
                obj.xpos = ObjectVal[3];
                obj.ypos = ObjectVal[4];
                obj.width = ObjectVal[5];
                obj.height = ObjectVal[6];
                obj.parentId = ObjectVal[7];
                obj.processId = ObjectVal[8];
                obj.refId = ObjectVal[9];
                obj.type = "Lane";
                obj.handId = "";
                this.poolContainer.add(obj);
            } else {
                ObjectInfo obj = new ObjectInfo();
                if (ObjectVal[0].equals("task-activity")) {
                    obj.type = "task";
                } else if (ObjectVal[0].equals("start")) {
                    obj.type = "start";
                    obj.handId = ObjectVal[7];
                } else if (ObjectVal[0].equals("end")) {
                    obj.type = "end";
                    obj.handId = ObjectVal[7];
                }
                obj.objId = ObjectVal[1];
                obj.name = ObjectVal[2];
                obj.xpos = ObjectVal[3];
                obj.ypos = ObjectVal[4];
                obj.width = ObjectVal[5];
                obj.height = ObjectVal[6];
                obj.parentId = ObjectVal[7];
                obj.processId = ObjectVal[8];
                obj.refId = ObjectVal[9];
                obj.hasStart = ObjectVal[10];
                obj.hasEnd = ObjectVal[11];
                obj.startRefId = ObjectVal[12];
                obj.endRefId = ObjectVal[13];
                obj.derivationRule = ObjectVal[14];
                obj.domEl = ObjectVal[15];
                this.taskContainer.add(obj);
            }
        }


        Element rootElet = dom.createElement("Package");
        rootElet.setAttribute("xmlns", "http://www.wfmc.org/2008/XPDL2.1");
        dom.appendChild(rootElet);
        Element ele = dom.createElement("PackageHeader");
        Element childElement = dom.createElement("XPDLVersion");
        Text text = dom.createTextNode("2.1");
        childElement.appendChild(text);
        ele.appendChild(childElement);
        rootElet.appendChild(ele);

        expaddPools(rootElet, containerId, processId);

        expaddWorkflow(rootElet, processId, linearr);




    }

    private void expwritebpel(JSONObject jsonobj, String containerId, String processId, JSONObject linejson) throws TransformerConfigurationException, TransformerException, JSONException {
        JSONArray jarr = jsonobj.getJSONArray("data");
        JSONArray linearr = linejson.getJSONArray("data");
        String split = ":";
        for (int i = 0; i < jarr.length(); i++) {

            Pattern p = Pattern.compile(split);

            JSONObject jobj = jarr.getJSONObject(i);
            String id = jobj.getString("id");
            String value = jobj.getString("value");
            String[] ObjectVal = null;

            ObjectVal = p.split(value);
            if (ObjectVal[0].equals("process-swim")) {
                ObjectInfo obj = new ObjectInfo();
                obj.objId = ObjectVal[1];
                obj.name = ObjectVal[2];
                obj.xpos = ObjectVal[3];
                obj.ypos = ObjectVal[4];
                obj.width = ObjectVal[5];
                obj.height = ObjectVal[6];
                obj.parentId = ObjectVal[7];
                obj.type = "Pool";
                obj.handId = "";
                obj.refId = ObjectVal[8];
                this.poolContainer.add(obj);
            } else if (ObjectVal[0].equals("lane-swim")) {
                ObjectInfo obj = new ObjectInfo();
                obj.objId = ObjectVal[1];
                obj.name = ObjectVal[2];
                obj.xpos = ObjectVal[3];
                obj.ypos = ObjectVal[4];
                obj.width = ObjectVal[5];
                obj.height = ObjectVal[6];
                obj.parentId = ObjectVal[7];
                obj.processId = ObjectVal[8];
                obj.refId = ObjectVal[9];
                obj.type = "Lane";
                obj.handId = "";
                this.poolContainer.add(obj);
            } else {
                ObjectInfo obj = new ObjectInfo();
                if (ObjectVal[0].equals("task-activity")) {
                    obj.type = "task";
                } else if (ObjectVal[0].equals("start")) {
                    obj.type = "start";
                    obj.handId = ObjectVal[7];
                } else if (ObjectVal[0].equals("end")) {
                    obj.type = "end";
                    obj.handId = ObjectVal[7];
                }
                obj.objId = ObjectVal[1];
                obj.name = ObjectVal[2];
                obj.xpos = ObjectVal[3];
                obj.ypos = ObjectVal[4];
                obj.width = ObjectVal[5];
                obj.height = ObjectVal[6];
                obj.parentId = ObjectVal[7];
                obj.processId = ObjectVal[8];
                obj.refId = ObjectVal[9];
                obj.hasStart = ObjectVal[10];
                obj.hasEnd = ObjectVal[11];
                obj.startRefId = ObjectVal[12];
                obj.endRefId = ObjectVal[13];
                obj.derivationRule = ObjectVal[14];
                obj.domEl = ObjectVal[15];
                this.taskContainer.add(obj);
            }
        }


        Element rootElet = dom.createElement("process");
        rootElet.setAttribute("name", "mybiz_flow");
        rootElet.setAttribute("targetNamespace", "http://ibm/dw/ode/bpel/executor");
        rootElet.setAttribute("xmlns", "http://docs.oasis-open.org/wsbpel/2.0/process/executable");
        rootElet.setAttribute("xmlns:tns", "http://ibm/dw/ode/bpel/executor");
        rootElet.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        rootElet.setAttribute("xmlns:test", "http://ibm/dw/ode/bpel/executor.wsdl");
        rootElet.setAttribute("xmlns:sns", "http://ibm/dw/ode/bpel/service.wsdl");
        rootElet.setAttribute("queryLanguage", "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath2.0");
        rootElet.setAttribute("expressionLanguage", "urn:oasis:names:tc:wsbpel:2.0:sublang:xpath2.0");
        dom.appendChild(rootElet);

        Element partnerLinksele = dom.createElement("partnerLinks");
        Element childink1 = dom.createElement("partnerLink");
        childink1.setAttribute("name", "mybiz_flowPartnerLink");
        childink1.setAttribute("partnerLinkType", "test:mybiz_flowPartnerLinkType");
        childink1.setAttribute("myRole", "executor");
        partnerLinksele.appendChild(childink1);

        Element childink2 = dom.createElement("partnerLink");
        childink2.setAttribute("name", "mybiz_flow_servicePartnerLink");
        childink2.setAttribute("partnerLinkType", "sns:mybiz_flow_servicePartnerLinkType");
        childink2.setAttribute("partnerRole", "service");
        childink2.setAttribute("initializePartnerRole", "yes");
        partnerLinksele.appendChild(childink2);
        
        rootElet.appendChild(partnerLinksele);
        
        //expaddPools(rootElet, containerId, processId);

        expanddWorkflow(rootElet, processId, linearr);
       




    }

    private void expaddPools(Element rootElet, String containerId, String processId) {
        Element ele = dom.createElement("Pools");
        rootElet.appendChild(ele);

        Element mainPool = dom.createElement("Pool");
        ele.appendChild(mainPool);
        mainPool.setAttribute("Id", containerId);
        mainPool.setAttribute("Process", processId);

        Element lanes = dom.createElement("Lanes");
        mainPool.appendChild(lanes);

        Element graphicsInfos = dom.createElement("NodeGraphicsInfos");
        mainPool.appendChild(graphicsInfos);

        Element graphicsInfo = dom.createElement("NodeGraphicsInfo");
        graphicsInfos.appendChild(graphicsInfo);
        graphicsInfo.setAttribute("Height", "0");
        graphicsInfo.setAttribute("Width", "0");

        Element cordinates = dom.createElement("Coordinates");
        graphicsInfo.appendChild(cordinates);
        cordinates.setAttribute("XCoordinate", "0");
        cordinates.setAttribute("YCoordinate", "0");

        for (int i = 0; i < this.poolContainer.size(); i++) {
            ObjectInfo obj = (ObjectInfo) this.poolContainer.get(i);
            if (obj.type.equals("Pool")) {
                int cnt = 0;
                Element childElement = dom.createElement(obj.type);
                ele.appendChild(childElement);
                childElement.setAttribute("Id", obj.objId);
                childElement.setAttribute("Name", obj.name);
                childElement.setAttribute("refId", obj.refId);
                for (int j = 0; j < this.poolContainer.size(); j++) {
                    ObjectInfo innerObj = (ObjectInfo) this.poolContainer.get(j);
                    if (innerObj.parentId.equals(obj.objId) && innerObj.type.equals("Lane")) {
                        cnt++;
                        if (cnt <= 1) {
                            lanes = dom.createElement("Lanes");
                            childElement.appendChild(lanes);
                        }

                        Element laneElement = dom.createElement(innerObj.type);
                        lanes.appendChild(laneElement);
                        laneElement.setAttribute("Id", innerObj.objId);
                        laneElement.setAttribute("Name", innerObj.name);
                        laneElement.setAttribute("ParentPool", innerObj.parentId);
                        laneElement.setAttribute("refId", innerObj.refId);
                        graphicsInfos = dom.createElement("NodeGraphicsInfos");
                        laneElement.appendChild(graphicsInfos);

                        graphicsInfo = dom.createElement("NodeGraphicsInfo");
                        graphicsInfos.appendChild(graphicsInfo);
                        graphicsInfo.setAttribute("Height", innerObj.height);
                        graphicsInfo.setAttribute("Width", innerObj.width);

                        cordinates = dom.createElement("Coordinates");
                        graphicsInfo.appendChild(cordinates);
                        cordinates.setAttribute("XCoordinate", innerObj.xpos);
                        cordinates.setAttribute("YCoordinate", innerObj.ypos);
                    }
                }

                graphicsInfos = dom.createElement("NodeGraphicsInfos");
                childElement.appendChild(graphicsInfos);

                graphicsInfo = dom.createElement("NodeGraphicsInfo");
                graphicsInfos.appendChild(graphicsInfo);
                graphicsInfo.setAttribute("Height", obj.height);
                graphicsInfo.setAttribute("Width", obj.width);

                cordinates = dom.createElement("Coordinates");
                graphicsInfo.appendChild(cordinates);
                cordinates.setAttribute("XCoordinate", obj.xpos);
                cordinates.setAttribute("YCoordinate", obj.ypos);
            }
        }
    }

    private void expaddWorkflow(Element rootElet, String processId, JSONArray linearr) throws JSONException {
        Element ele = dom.createElement("WorkflowProcesses");
        rootElet.appendChild(ele);

        for (int i = 0; i < this.poolContainer.size(); i++) {
            int cnt = 0;
            Element processWorkflow = null;
            Element activities = null;
            ObjectInfo obj = (ObjectInfo) this.poolContainer.get(i);
            if (obj.type.equals("Pool")) {
                for (int j = 0; j < this.taskContainer.size(); j++) {

                    ObjectInfo taskObj = (ObjectInfo) this.taskContainer.get(j);
                    if (obj.objId.equals(taskObj.processId)) {
                        cnt++;
                        if (cnt <= 1) {
                            processWorkflow = dom.createElement("WorkflowProcess");
                            ele.appendChild(processWorkflow);
                            processWorkflow.setAttribute("Id", taskObj.processId);
                            processWorkflow.setAttribute("Name", obj.name);
                            processWorkflow.setAttribute("refId", obj.refId);
                            activities = dom.createElement("Activities");
                            processWorkflow.appendChild(activities);
                        }
                        Element activity = dom.createElement("Activity");
                        activities.appendChild(activity);
                        activity.setAttribute("Id", taskObj.objId);
                        activity.setAttribute("Name", taskObj.name);
                        activity.setAttribute("refId", taskObj.refId);
                        activity.setAttribute("Parent", taskObj.parentId);
                        activity.setAttribute("hasStart", taskObj.hasStart);
                        activity.setAttribute("hasEnd", taskObj.hasEnd);
                        activity.setAttribute("startRefId", taskObj.startRefId);
                        activity.setAttribute("endRefId", taskObj.endRefId);
                        activity.setAttribute("derivationRule", taskObj.derivationRule);
                        activity.setAttribute("domEl", taskObj.domEl);
                        Element eventEle = dom.createElement("Event");
                       // activity.appendChild(eventEle);
                        Element event = null;
                        Element Implementation = null;
                        if (taskObj.type.equals("start")) {
                        event = dom.createElement("StartEvent");
                        eventEle.appendChild(event);
                        } else if (taskObj.type.equals("end")) {
                        event = dom.createElement("EndEvent");
                        eventEle.appendChild(event);
                        } else if (taskObj.type.equals("task")) {
                            Implementation = dom.createElement("Implementation");
                            activity.appendChild(Implementation);
                            Element task = dom.createElement("Task");
                            Implementation.appendChild(task);
                        }


                        Element graphicsInfos = dom.createElement("NodeGraphicsInfos");
                        activity.appendChild(graphicsInfos);

                        Element graphicsInfo = dom.createElement("NodeGraphicsInfo");
                        graphicsInfos.appendChild(graphicsInfo);
                        graphicsInfo.setAttribute("Height", taskObj.height);
                        graphicsInfo.setAttribute("Width", taskObj.width);

                        Element cordinates = dom.createElement("Coordinates");
                        graphicsInfo.appendChild(cordinates);
                        cordinates.setAttribute("XCoordinate", taskObj.xpos);
                        cordinates.setAttribute("YCoordinate", taskObj.ypos);
                    }
                }
                if (cnt >= 1) {
                    Element transitions = dom.createElement("Transitions");
                    processWorkflow.appendChild(transitions);
                    for (int l = 0; l < linearr.length(); l++) {
                        JSONObject lineObj = linearr.getJSONObject(l);
                        String lineid = lineObj.getString("id");
                        String[] line = lineid.split("_");
                        String[] fromLinearr = line[0].split(this.parentSplit);
                        String[] toLinearr = line[1].split(this.parentSplit);
                        if (obj.objId.equals(fromLinearr[1])) {
                            Element transition = dom.createElement("Transition");
                            transitions.appendChild(transition);
                            transition.setAttribute("Id", lineid);
                            transition.setAttribute("From", fromLinearr[0]);
                            transition.setAttribute("To", toLinearr[0]);
                            transition.setAttribute("Name", "");
                        }
                    }
                }
            }
        }
        Element outerWorkflow = dom.createElement("WorkflowProcess");
        ele.appendChild(outerWorkflow);
        outerWorkflow.setAttribute("Id", processId);
        outerWorkflow.setAttribute("Name", "Main Processs");
        Element activities = null;
        Element transitions = dom.createElement("Transitions");
        ObjectInfo starttaskObj=null;
        for (int j = 0; j < this.taskContainer.size(); j++) {
            ObjectInfo taskObj = (ObjectInfo) this.taskContainer.get(j);
            if (!taskObj.processId.startsWith("process")) {

                if (j < 1) {
                    activities = dom.createElement("Activities");
                    outerWorkflow.appendChild(activities);
                }
                activities.appendChild(createActivityNode(taskObj,true));
                if (Boolean.parseBoolean(taskObj.hasStart)) {
                    Element activity = dom.createElement("Activity");
                    activities.appendChild(activity);
                    activity.setAttribute("Id", taskObj.startRefId);
                    Element eventEle = dom.createElement("Event");

                    activity.appendChild(eventEle);
                    Element event = dom.createElement("StartEvent");
                    event.setAttribute("Trigger", "None");
                    eventEle.appendChild(event);

                    Element graphicsInfos = dom.createElement("NodeGraphicsInfos");
                    activity.appendChild(graphicsInfos);

                    Element graphicsInfo = dom.createElement("NodeGraphicsInfo");
                    graphicsInfos.appendChild(graphicsInfo);

                    int adj = Integer.parseInt(taskObj.width) / 2;

                    String xpos = (Integer.parseInt(taskObj.xpos) + adj) + "";
                    String ypos = (Integer.parseInt(taskObj.ypos) - 75) + "";
                    Element cordinates = dom.createElement("Coordinates");
                    graphicsInfo.appendChild(cordinates);
                    cordinates.setAttribute("XCoordinate", xpos);
                    cordinates.setAttribute("YCoordinate", ypos);

                    starttaskObj = new ObjectInfo();
                    starttaskObj.objId = taskObj.startRefId + this.parentSplit + taskObj.objId;
                    starttaskObj.fromTransition = taskObj.startRefId;
                    starttaskObj.toTransition = taskObj.objId;
                    starttaskObj.transitionType = "";
                    transitions.appendChild(createTransition(starttaskObj));

                } else if (Boolean.parseBoolean(taskObj.hasEnd)) {
                    Element activity = dom.createElement("Activity");
                    activities.appendChild(activity);
                    activity.setAttribute("Id", taskObj.endRefId);
                    Element eventEle = dom.createElement("Event");

                    activity.appendChild(eventEle);
                    Element event = dom.createElement("EndEvent");
                    event.setAttribute("Trigger", "None");
                    eventEle.appendChild(event);

                    Element graphicsInfos = dom.createElement("NodeGraphicsInfos");
                    activity.appendChild(graphicsInfos);

                    Element graphicsInfo = dom.createElement("NodeGraphicsInfo");
                    graphicsInfos.appendChild(graphicsInfo);

                    int adj = Integer.parseInt(taskObj.width) / 2;

                    String xpos = (Integer.parseInt(taskObj.xpos) + adj) + "";
                    String ypos = (Integer.parseInt(taskObj.ypos) + 95) + "";
                    Element cordinates = dom.createElement("Coordinates");
                    graphicsInfo.appendChild(cordinates);
                    cordinates.setAttribute("XCoordinate", xpos);
                    cordinates.setAttribute("YCoordinate", ypos);

                    starttaskObj = new ObjectInfo();
                    starttaskObj.objId = taskObj.objId + this.parentSplit + taskObj.endRefId;
                    starttaskObj.fromTransition = taskObj.objId;
                    starttaskObj.toTransition = taskObj.endRefId;
                    starttaskObj.transitionType = "";
                    transitions.appendChild(createTransition(starttaskObj));
                }
            }
        }

        outerWorkflow.appendChild(transitions);
        String derivationRule="";
        String gatewayId="";
        HashMap<String, String> fromGatewayeHm = new HashMap<String, String>();
        for (int l = 0; l < linearr.length(); l++) {
            JSONObject lineObj = linearr.getJSONObject(l);
            String lineid = lineObj.getString("id");
            derivationRule=lineObj.getString("derivationRule");
            String[] line = lineid.split("_");
            String[] fromLinearr = line[0].split(this.parentSplit);
            String[] toLinearr = line[1].split(this.parentSplit);
            if (!fromLinearr[1].startsWith("process")) {
               ObjectInfo taskObj=new ObjectInfo();
               if (derivationRule.equals("sequence")){
                taskObj.objId=lineid;
                taskObj.fromTransition=fromLinearr[0];
                taskObj.toTransition=toLinearr[0];
                taskObj.transitionType="";
                transitions.appendChild(createTransition(taskObj));
               }else{
                   boolean addGateway=false;
                   if (fromGatewayeHm.containsKey(fromLinearr[0])){
                        gatewayId=fromGatewayeHm.get(fromLinearr[0]);
                   }else{
                        gatewayId=UUID.randomUUID().toString();
                        fromGatewayeHm.put(fromLinearr[0], gatewayId);
                        addGateway=true;
                   }
                   if (addGateway) {
                       taskObj = new ObjectInfo();
                       taskObj.objId = gatewayId;
                       taskObj.height = "65";
                       taskObj.width = "135";
                       taskObj.xpos = lineObj.getString("xpos");
                       taskObj.ypos = lineObj.getString("ypos");
                       activities.appendChild(createActivityNode(taskObj, false));
                       taskObj=new ObjectInfo();
                       taskObj.objId = fromLinearr[0]+this.parentSplit+gatewayId;
                       taskObj.fromTransition = fromLinearr[0];
                       taskObj.toTransition = gatewayId;
                       taskObj.transitionType="";
                       transitions.appendChild(createTransition(taskObj));
                   }
                   taskObj=new ObjectInfo();
                   taskObj.objId = gatewayId+this.parentSplit+toLinearr[0];
                   taskObj.fromTransition = gatewayId;
                   taskObj.toTransition = toLinearr[0];
                   taskObj.transitionType="Condition";
                   transitions.appendChild(createTransition(taskObj));
               }
            }
        }
    }

    private void expanddWorkflow(Element rootElet, String processId, JSONArray linearr) throws JSONException {
        /*Element ele = dom.createElement("WorkflowProcesses");
        rootElet.appendChild(ele);*/

        /*for (int i = 0; i < this.poolContainer.size(); i++) {
            int cnt = 0;
            Element processWorkflow = null;
            Element activities = null;
            ObjectInfo obj = (ObjectInfo) this.poolContainer.get(i);
            if (obj.type.equals("Pool")) {
                for (int j = 0; j < this.taskContainer.size(); j++) {

                    ObjectInfo taskObj = (ObjectInfo) this.taskContainer.get(j);
                    if (obj.objId.equals(taskObj.processId)) {
                        cnt++;
                        if (cnt <= 1) {
                            processWorkflow = dom.createElement("WorkflowProcess");
                            ele.appendChild(processWorkflow);
                            processWorkflow.setAttribute("Id", taskObj.processId);
                            processWorkflow.setAttribute("Name", obj.name);
                            processWorkflow.setAttribute("refId", obj.refId);
                            activities = dom.createElement("Activities");
                            processWorkflow.appendChild(activities);
                        }
                        Element activity = dom.createElement("Activity");
                        activities.appendChild(activity);
                        activity.setAttribute("Id", taskObj.objId);
                        activity.setAttribute("Name", taskObj.name);
                        activity.setAttribute("refId", taskObj.refId);
                        activity.setAttribute("Parent", taskObj.parentId);
                        activity.setAttribute("hasStart", taskObj.hasStart);
                        activity.setAttribute("hasEnd", taskObj.hasEnd);
                        activity.setAttribute("startRefId", taskObj.startRefId);
                        activity.setAttribute("endRefId", taskObj.endRefId);
                        activity.setAttribute("derivationRule", taskObj.derivationRule);
                        activity.setAttribute("domEl", taskObj.domEl);
                        Element eventEle = dom.createElement("Event");
                       // activity.appendChild(eventEle);
                        Element event = null;
                        Element Implementation = null;
                        if (taskObj.type.equals("start")) {
                        event = dom.createElement("StartEvent");
                        eventEle.appendChild(event);
                        } else if (taskObj.type.equals("end")) {
                        event = dom.createElement("EndEvent");
                        eventEle.appendChild(event);
                        } else if (taskObj.type.equals("task")) {
                            Implementation = dom.createElement("Implementation");
                            activity.appendChild(Implementation);
                            Element task = dom.createElement("Task");
                            Implementation.appendChild(task);
                        }


                        Element graphicsInfos = dom.createElement("NodeGraphicsInfos");
                        activity.appendChild(graphicsInfos);

                        Element graphicsInfo = dom.createElement("NodeGraphicsInfo");
                        graphicsInfos.appendChild(graphicsInfo);
                        graphicsInfo.setAttribute("Height", taskObj.height);
                        graphicsInfo.setAttribute("Width", taskObj.width);

                        Element cordinates = dom.createElement("Coordinates");
                        graphicsInfo.appendChild(cordinates);
                        cordinates.setAttribute("XCoordinate", taskObj.xpos);
                        cordinates.setAttribute("YCoordinate", taskObj.ypos);
                    }
                }
                if (cnt >= 1) {
                    Element transitions = dom.createElement("Transitions");
                    processWorkflow.appendChild(transitions);
                    for (int l = 0; l < linearr.length(); l++) {
                        JSONObject lineObj = linearr.getJSONObject(l);
                        String lineid = lineObj.getString("id");
                        String[] line = lineid.split("_");
                        String[] fromLinearr = line[0].split(this.parentSplit);
                        String[] toLinearr = line[1].split(this.parentSplit);
                        if (obj.objId.equals(fromLinearr[1])) {
                            Element transition = dom.createElement("Transition");
                            transitions.appendChild(transition);
                            transition.setAttribute("Id", lineid);
                            transition.setAttribute("From", fromLinearr[0]);
                            transition.setAttribute("To", toLinearr[0]);
                            transition.setAttribute("Name", "");
                        }
                    }
                }
            }
        }*/
        Element sequenceflow = dom.createElement("sequence");
        
        sequenceflow.setAttribute("name", "sequenceComponent_1");
        //outerWorkflow.setAttribute("Name", "Main Processs");
        Element activities = null;
        Element transitions = dom.createElement("Transitions");
        ObjectInfo starttaskObj=null;
        Element activity = dom.createElement("receive");
        sequenceflow.appendChild(activity);
        activity.setAttribute("name", "ProcessInstantiation");
        activity.setAttribute("partnerLink","mybiz_flowPartnerLink");
        activity.setAttribute("portType","test:mybiz_flowPortType");
        activity.setAttribute("operation","execute");
        activity.setAttribute("variable","toyChosen");
        activity.setAttribute("createInstance","yes");
        Element innersequenceflow = dom.createElement("sequence");
        
        for (int j = 0; j < this.taskContainer.size(); j++) {
            ObjectInfo taskObj = (ObjectInfo) this.taskContainer.get(j);
            if (!taskObj.processId.startsWith("process")) {

                /*if (j < 1) {
                    activities = dom.createElement("Activities");
                    outerWorkflow.appendChild(activities);
                }
                activities.appendChild(createActivityNode(taskObj,true));*/
                /*if (Boolean.parseBoolean(taskObj.hasStart)) {
                    Element activity = dom.createElement("receive");
                    sequenceflow.appendChild(activity);
                    activity.setAttribute("name", "ProcessInstantiation");
                    activity.setAttribute("partnerLink","mybiz_flowPartnerLink");
                    activity.setAttribute("portType","test:mybiz_flowPortType");
                    activity.setAttribute("operation","execute");
                    activity.setAttribute("variable","toyChosen");
                    activity.setAttribute("createInstance","yes");
                    
                    //Element eventEle = dom.createElement("Event");

                    //activity.appendChild(eventEle);
                    /*Element event = dom.createElement("StartEvent");
                    event.setAttribute("Trigger", "None");
                    eventEle.appendChild(event);

                    Element graphicsInfos = dom.createElement("NodeGraphicsInfos");
                    activity.appendChild(graphicsInfos);

                    Element graphicsInfo = dom.createElement("NodeGraphicsInfo");
                    graphicsInfos.appendChild(graphicsInfo);

                    int adj = Integer.parseInt(taskObj.width) / 2;

                    String xpos = (Integer.parseInt(taskObj.xpos) + adj) + "";
                    String ypos = (Integer.parseInt(taskObj.ypos) - 75) + "";
                    Element cordinates = dom.createElement("Coordinates");
                    graphicsInfo.appendChild(cordinates);
                    cordinates.setAttribute("XCoordinate", xpos);
                    cordinates.setAttribute("YCoordinate", ypos);

                    starttaskObj = new ObjectInfo();
                    starttaskObj.objId = taskObj.startRefId + this.parentSplit + taskObj.objId;
                    starttaskObj.fromTransition = taskObj.startRefId;
                    starttaskObj.toTransition = taskObj.objId;
                    starttaskObj.transitionType = "";
                    transitions.appendChild(createTransition(starttaskObj));*/

                /*} else if (Boolean.parseBoolean(taskObj.hasEnd)) {
                    Element activity = dom.createElement("receive");
                    sequenceflow.appendChild(activity);
                    activity.setAttribute("name", taskObj.name);
                    activity.setAttribute("partnerLink","mybiz_flowPartnerLink");
                    activity.setAttribute("portType","test:mybiz_flowPortType");
                    activity.setAttribute("operation","execute");
                    activity.setAttribute("variable","toyChosen");
                    activity.setAttribute("createInstance","yes");
                } else {*/
                    Element sequence = dom.createElement("invoke");
                    innersequenceflow.appendChild(sequence);
                    sequence.setAttribute("name", taskObj.name);
                    sequence.setAttribute("partnerLink", "mybiz_flow_servicePartnerLink");
                    sequence.setAttribute("portType","sns:mybiz_flow_servicePortType");
                    sequence.setAttribute("operation",taskObj.name);
                    sequence.setAttribute("inputVariable",taskObj.name+"_data_in");
                    sequence.setAttribute("outputVariable",taskObj.name+"_data_out");
                    
                //}
            }
        }
        sequenceflow.appendChild(innersequenceflow);
        rootElet.appendChild(sequenceflow);

        //sequenceflow.appendChild(transitions);
        /*String derivationRule="";
        String gatewayId="";
        /*HashMap<String, String> fromGatewayeHm = new HashMap<String, String>();
        for (int l = 0; l < linearr.length(); l++) {
            JSONObject lineObj = linearr.getJSONObject(l);
            String lineid = lineObj.getString("id");
            derivationRule=lineObj.getString("derivationRule");
            String[] line = lineid.split("_");
            String[] fromLinearr = line[0].split(this.parentSplit);
            String[] toLinearr = line[1].split(this.parentSplit);
            if (!fromLinearr[1].startsWith("process")) {
               ObjectInfo taskObj=new ObjectInfo();
               if (derivationRule.equals("sequence")){
                taskObj.objId=lineid;
                taskObj.fromTransition=fromLinearr[0];
                taskObj.toTransition=toLinearr[0];
                taskObj.transitionType="";
                transitions.appendChild(createTransition(taskObj));
               }else{
                   boolean addGateway=false;
                   if (fromGatewayeHm.containsKey(fromLinearr[0])){
                        gatewayId=fromGatewayeHm.get(fromLinearr[0]);
                   }else{
                        gatewayId=UUID.randomUUID().toString();
                        fromGatewayeHm.put(fromLinearr[0], gatewayId);
                        addGateway=true;
                   }
                   if (addGateway) {
                       taskObj = new ObjectInfo();
                       taskObj.objId = gatewayId;
                       taskObj.height = "65";
                       taskObj.width = "135";
                       taskObj.xpos = lineObj.getString("xpos");
                       taskObj.ypos = lineObj.getString("ypos");
                       activities.appendChild(createActivityNode(taskObj, false));
                       taskObj=new ObjectInfo();
                       taskObj.objId = fromLinearr[0]+this.parentSplit+gatewayId;
                       taskObj.fromTransition = fromLinearr[0];
                       taskObj.toTransition = gatewayId;
                       taskObj.transitionType="";
                       transitions.appendChild(createTransition(taskObj));
                   }
                   taskObj=new ObjectInfo();
                   taskObj.objId = gatewayId+this.parentSplit+toLinearr[0];
                   taskObj.fromTransition = gatewayId;
                   taskObj.toTransition = toLinearr[0];
                   taskObj.transitionType="Condition";
                   transitions.appendChild(createTransition(taskObj));
               }
            }
        }*/
    }

    private Element createActivityNode(ObjectInfo taskObj,boolean actaulTask) {
        Element activity = dom.createElement("Activity");
        activity.setAttribute("Id", taskObj.objId);
        activity.setAttribute("Name", taskObj.name);
        activity.setAttribute("refId", taskObj.refId);
        activity.setAttribute("Parent", taskObj.parentId);
        activity.setAttribute("hasStart", taskObj.hasStart);
        activity.setAttribute("hasEnd", taskObj.hasEnd);
        activity.setAttribute("startRefId", taskObj.startRefId);
        activity.setAttribute("endRefId", taskObj.endRefId);
        activity.setAttribute("derivationRule", taskObj.derivationRule);
        activity.setAttribute("domEl", taskObj.domEl);

        if (actaulTask) {
            Element eventEle = dom.createElement("Event");
//                activity.appendChild(eventEle);
            Element event = null;
            Element Implementation = null;
            if (taskObj.type.equals("task")) {
                Implementation = dom.createElement("Implementation");
                activity.appendChild(Implementation);
                Element task = dom.createElement("Task");
                Implementation.appendChild(task);
            }
        }else{
            Element route = dom.createElement("Route");
            activity.appendChild(route);
        }

        Element graphicsInfos = dom.createElement("NodeGraphicsInfos");
        activity.appendChild(graphicsInfos);

        Element graphicsInfo = dom.createElement("NodeGraphicsInfo");
        graphicsInfos.appendChild(graphicsInfo);
        graphicsInfo.setAttribute("Height", taskObj.height);
        graphicsInfo.setAttribute("Width", taskObj.width);

        Element cordinates = dom.createElement("Coordinates");
        graphicsInfo.appendChild(cordinates);
        cordinates.setAttribute("XCoordinate", taskObj.xpos);
        cordinates.setAttribute("YCoordinate", taskObj.ypos);
        return activity;
    }

    private Element createTransition(ObjectInfo taskObj) {
        Element transition = dom.createElement("Transition");
        transition.setAttribute("Id", taskObj.objId);
        transition.setAttribute("From", taskObj.fromTransition);
        transition.setAttribute("To", taskObj.toTransition);
        transition.setAttribute("Name", "");
        Element condition = dom.createElement("Condition");
        transition.appendChild(condition);
        if (!taskObj.transitionType.equals("")) {
            condition.setAttribute("Type", "CONDITION");//Expression
            Element expression = dom.createElement("Expression");
            condition.appendChild(expression);
        }
        return transition;
    }
}


