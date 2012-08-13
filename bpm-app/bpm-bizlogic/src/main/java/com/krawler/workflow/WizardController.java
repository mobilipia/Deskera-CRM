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
package com.krawler.workflow;

import com.krawler.br.BusinessProcess;
import com.krawler.br.ProcessBag;
import com.krawler.br.ProcessException;
import com.krawler.br.operations.OperationDefinition;
import com.krawler.br.operations.OperationParameter;
import com.krawler.br.modules.ModuleBag;
import com.krawler.br.modules.ModuleDefinition;
import com.krawler.br.modules.ModuleProperty;
import com.krawler.br.modules.SimpleModuleBag;
import com.krawler.br.nodes.BProcess;
import com.krawler.br.nodes.xml.XmlNodeParser;
import com.krawler.br.nodes.json.JsonNodeParser;
import com.krawler.br.operations.OperationBag;
import com.krawler.br.utils.JsonFactory;
import com.krawler.br.utils.SourceFactory;
import com.krawler.br.utils.XmlFactory;
import com.krawler.common.session.SessionExpiredException;
import com.krawler.esp.handlers.SendMailHandler;
import com.krawler.spring.sessionHandler.sessionHandlerImpl;
import com.krawler.utils.json.base.JSONArray;
import com.krawler.utils.json.base.JSONException;
import com.krawler.utils.json.base.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 *
 * @author krawler
 */
public class WizardController extends MultiActionController {
    private ModuleBag moduleBag;
    private OperationBag operationBag;
    private ProcessBag processBag;
    private SourceFactory defaultSourceFactory;
    private sessionHandlerImpl sessionHandlerImplObj;
    private XmlNodeParser xmlParser;
    private JsonNodeParser jsonParser;
    private SourceFactory defaultModuleSrcFactory;
    private String factoryDirPath;

    public void setFactoryDirPath(String factoryDirPath) {
        this.factoryDirPath = factoryDirPath;
    }

    public void setDefaultModuleSrcFactory(SourceFactory defaultModuleSrcFactory) {
        this.defaultModuleSrcFactory = defaultModuleSrcFactory;
    }


    public void setModuleBag(ModuleBag moduleBag) {
        this.moduleBag = moduleBag;
    }

    public void setOperationBag(OperationBag operationBag) {
        this.operationBag = operationBag;
    }

    public void setDefaultSourceFactory(SourceFactory defaultSourceFactory) {
        this.defaultSourceFactory = defaultSourceFactory;
    }

    public void setJsonParser(JsonNodeParser jsonParser) {
        this.jsonParser = jsonParser;
    }

    public void setProcessBag(ProcessBag processBag) {
        this.processBag = processBag;
    }

    public void setSessionHandlerImpl(sessionHandlerImpl sessionHandlerImplObj) {
        this.sessionHandlerImplObj = sessionHandlerImplObj;
    }

    public void setXmlParser(XmlNodeParser xmlParser) {
        this.xmlParser = xmlParser;
    }

    public ModelAndView getOperationsList(HttpServletRequest request, HttpServletResponse response) {
        JSONArray jArr = new JSONArray();
        try {
            Iterator itr = operationBag.getOperationIDs().iterator();

            while (itr.hasNext()) {
                OperationDefinition odef = operationBag.getOperationDefinition((String) itr.next());
                OperationParameter out = odef.getOutputParameter();
                int count = odef.getParameterCount();
                JSONObject tempObj = new JSONObject();
                tempObj.put("operationname", odef.getName());
                tempObj.put("operationid", odef.getID());
                tempObj.put("returnmodule", out == null ? null : getParamJson(out));
                JSONArray pArr = new JSONArray();
                for(int i=0;i<count;i++){
                    OperationParameter in = odef.getInputParameter(i);
                    pArr.put(getParamJson(in));
                }
                tempObj.put("inmodule", pArr);
                jArr.put(tempObj);
            }
        } catch (Exception ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return new ModelAndView("jsonView", "model", jArr.toString());
        }
    }

    public ModelAndView getOperationsAndProcessList(HttpServletRequest request, HttpServletResponse response) {
        JSONArray jArr = new JSONArray();
        try {
            Iterator itr = operationBag.getOperationIDs().iterator();

            while (itr.hasNext()) {
                OperationDefinition odef = operationBag.getOperationDefinition((String) itr.next());
                OperationParameter out = odef.getOutputParameter();
                int count = odef.getParameterCount();
                JSONObject tempObj = new JSONObject();
                tempObj.put("operationname", odef.getName());
                tempObj.put("operationid", odef.getID());
                tempObj.put("returnmodule", out == null ? null : getParamJson(out));
                JSONArray pArr = new JSONArray();
                for(int i=0;i<count;i++){
                    OperationParameter in = odef.getInputParameter(i);
                    pArr.put(getParamJson(in));
                }
                tempObj.put("inmodule", pArr);
                jArr.put(tempObj);
            }
            SourceFactory xsrc = getXmlFactory(sessionHandlerImplObj.getCompanyid());
            itr = processBag.getProcessIDs(xsrc).iterator();
            while(itr.hasNext()){
                String pName = (String)itr.next();
                BProcess p = (BProcess)processBag.getProcess(xsrc, pName);
                OperationParameter out = p.getOutputParam();
                JSONObject tempObj = new JSONObject();
                tempObj.put("operationname", pName);
                tempObj.put("operationid", pName);
                tempObj.put("returnmodule", out == null ? null : getParamJson(out));
                Map m = p.getInputParams();
                Iterator itr1 = m.keySet().iterator();
                JSONArray pArr = new JSONArray();
                while(itr1.hasNext()){
                    OperationParameter in = (OperationParameter)m.get(itr1.next());
                    pArr.put(getParamJson(in));
                }
                tempObj.put("inmodule", pArr);
                jArr.put(tempObj);
            }
        } catch (Exception ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return new ModelAndView("jsonView", "model", jArr.toString());
        }
    }

    private JSONObject getParamJson(OperationParameter para) throws JSONException{
        JSONObject tmpObj = new JSONObject();
        tmpObj.put("text", para.getName());
        tmpObj.put("name", para.getName());
        tmpObj.put("type", para.getType());
        tmpObj.put("multi", para.getMulti());
        tmpObj.put("leaf", moduleBag.getModuleDefinition(para.getType()).getType() == ModuleDefinition.TYPE.SIMPLE);

        return tmpObj;
    }

    public ModelAndView reloadModules(HttpServletRequest request, HttpServletResponse respone){
        try {
            String tablename = request.getParameter("tablename");
            XmlFactory defFactory = (XmlFactory) defaultModuleSrcFactory;

            XmlFactory srcFactoryObj = new XmlFactory(defFactory.getProps(), defFactory.getParser(), defFactory.getParentSourceFactory());
            ((SimpleModuleBag)moduleBag).load(srcFactoryObj);

        } catch (ProcessException ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ModelAndView("jsonView", "model", "{moduleLoaded:true}");
    }
    public ModelAndView getModuleNames(HttpServletRequest request, HttpServletResponse respone) {
        JSONArray jArr = new JSONArray();

        try {
            Iterator itr = moduleBag.getModuleNames().iterator();
            while (itr.hasNext()) {
                ModuleDefinition md = moduleBag.getModuleDefinition((String) itr.next());
                JSONObject tempObj = new JSONObject();
                tempObj.put("text", md.getName());
                tempObj.put("leaf", md.getType() == ModuleDefinition.TYPE.SIMPLE);
                tempObj.put("name", md.getName());
                jArr.put(tempObj);
            }

        } catch (JSONException ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return new ModelAndView("jsonView", "model", jArr.toString());
        }
    }
    public ModelAndView getSessionAttributes(HttpServletRequest request, HttpServletResponse respone) {
        JSONObject jObj = new JSONObject();

        try {
            String[] sessionObj = sessionHandlerImplObj.getAttributeNames();
            for (int i=0;i<sessionObj.length;i++) {
                JSONObject tempObj = new JSONObject();
                tempObj.put("name",sessionObj[i]);
                tempObj.put("type",sessionHandlerImplObj.getAttribute(sessionObj[i]).getClass().getSimpleName());
                jObj.append("data", tempObj);
            }

        } catch (JSONException ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return new ModelAndView("jsonView", "model", jObj.toString());
        }
    }
    public ModelAndView getModuleParams(HttpServletRequest request, HttpServletResponse respone) {
        String modulename = request.getParameter("type");
        String nType = request.getParameter("nodetype");
        if("process".equals(nType)){
            return new ModelAndView("jsonView_ex", "model", "");
        }
        return new ModelAndView("jsonView_ex", "model", getModuleParams(modulename).toString());
    }

    public JSONArray getModuleParams(String modulename) {
        JSONArray jArr = new JSONArray();

        try {
            ModuleDefinition md = moduleBag.getModuleDefinition(modulename);
            Set module_def_set = md.getPropertyNames();
            Iterator itr = module_def_set.iterator();
            while (itr.hasNext()) {
                ModuleProperty p = md.getProperty((String) itr.next());

                JSONObject tempObj = new JSONObject();
                tempObj.put("text", p.getName());
                tempObj.put("name", p.getName());
                tempObj.put("type", p.getType());
                tempObj.put("multi", p.getMulti());
                  String iconCls = "othermodules";
                if(md.getType() == ModuleDefinition.TYPE.SIMPLE){
                    iconCls = md.getName()+"Icon";
                }
                tempObj.put("iconCls", iconCls);
                tempObj.put("leaf", moduleBag.getModuleDefinition(p.getType()).getType() == ModuleDefinition.TYPE.SIMPLE);
                jArr.put(tempObj);
            }

        } catch (JSONException ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return jArr;
        }
    }

    public ModelAndView getInputParams(HttpServletRequest request, HttpServletResponse response) {
        JSONArray jArr = new JSONArray();

        String activityName = request.getParameter("activityname");
        try {
            OperationDefinition activity_definition = operationBag.getOperationDefinition(activityName);

            int param_count = activity_definition.getParameterCount();

            for (int i = 0; i < param_count; i++) {
                OperationParameter aparam = activity_definition.getInputParameter(i);
                JSONObject tempObj = new JSONObject();
                tempObj.put("name", aparam.getName());
                tempObj.put("type", aparam.getType());
                jArr.put(tempObj);

            }

        } catch (Exception ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return new ModelAndView("jsonView", "model", jArr.toString());
        }
    }
    private String getProcessParams(String processName){
        JSONObject jobj = new JSONObject();
        try {
            SourceFactory xsrc = getXmlFactory(sessionHandlerImplObj.getCompanyid());
            BProcess p = (BProcess) processBag.getProcess(xsrc,processName);
            Map<String,OperationParameter> m = p.getInputParams();
            Set mapKeys  = m.keySet();
                Iterator itr = mapKeys.iterator();
                while(itr.hasNext()){
                    Object key = itr.next();
                    Object mapVal = m.get(key.toString()).getClassName(moduleBag);
                    jobj.put(key.toString(), mapVal);
                }

        } catch (JSONException ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SessionExpiredException ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProcessException ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jobj.toString();
    }

//    private JSONArray getProcessParams(String processName) {
//        JSONArray jArr = new JSONArray();
//        try {
//            SourceFactory xsrc = getXmlFactory(sessionHandlerImplObj.getCompanyid());
//            HashMap hm = new HashMap();
//            BProcess p = (BProcess)processBag.getProcess(xsrc,processName);
//            Iterator itr = hm.keySet().iterator();
//            while(itr.hasNext()){
//                String pName = (String)itr.next();
//                HashMap o=(HashMap)hm.get(pName);
//                JSONObject tempObj = new JSONObject();
//                tempObj.put("text", pName);
//                tempObj.put("name", pName);
//                FlowNode f=findNode(p.getInitialNode(), pName);
//                if(f instanceof BusinessProcess){
//                    tempObj.put("type", f.getSourceid());
//                    tempObj.put("nodetype", "process");
//                }else{
//                    tempObj.put("type", f.getSourceid());
//                    JSONArray pArr = new JSONArray();
//                    Iterator itr1 =o.keySet().iterator();
//                    while(itr1.hasNext()){
//                        String prm = (String)itr1.next();
//                        OperationParameter in = (OperationParameter)o.get(prm);
//                        JSONObject tmpObj = new JSONObject();
//                        tmpObj.put("text", in.getName());
//                        tmpObj.put("name", in.getName());
//                        tmpObj.put("type", in.getType());
//                        tmpObj.put("multi", in.getMulti());
//                        tmpObj.put("leaf", moduleBag.getModuleDefinition(in.getType()).getType() == ModuleDefinition.TYPE.SIMPLE);
//
//                        pArr.put(tmpObj);
//                    }
//                    tempObj.put("children", pArr);
//                }
//                jArr.put(tempObj);
//            }
//        } catch (Exception ex) {
//            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            return jArr;
//        }
//    }
//
//    private FlowNode findNode(FlowNode node, String id){
//        FlowNode sid=null;
//        if(node==null)
//            return null;
//        else if(node.getId().equals(id))
//            return node;
//
//        if(sid==null&&node.getNext()!=null)
//            sid = findNode(node.getNext(), id);
//        if(sid==null&&node.getFork()!=null)
//            sid = findNode(node.getFork(), id);
//
//        return sid;
//    }

    private SourceFactory getXmlFactory(String companyid) throws ProcessException{
        Properties prop = new Properties();
        prop.setProperty("filesystempath", factoryDirPath+"/"+companyid+"businesslogicEx.xml");
        SourceFactory src = new XmlFactory(prop, xmlParser, defaultSourceFactory);
        return src;
    }

    private SourceFactory getJsonFactory(String json) throws ProcessException{
        Properties prop = new Properties();
        prop.setProperty("json", json);
        SourceFactory src = new JsonFactory(prop, jsonParser, defaultSourceFactory);
        return src;
    }

    public ModelAndView getProcessList(HttpServletRequest request, HttpServletResponse response) {
        JSONArray jArr = new JSONArray();
        try {
            SourceFactory xsrc = getXmlFactory(sessionHandlerImplObj.getCompanyid());
            Iterator itr = processBag.getProcessIDs(xsrc).iterator();
            while(itr.hasNext()){
                String pName = (String)itr.next();
                jArr.put(pName);
            }
        } catch (Exception ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return new ModelAndView("jsonView", "model", jArr);
        }
    }


    public ModelAndView getProcess(HttpServletRequest request, HttpServletResponse response) {
        String retdata = "{msg:'process created successfully',success:true}";
        JSONObject pobj=null;
        try {
            HashMap prm=new HashMap();
            String processid = request.getParameter("processid");
            SourceFactory xsrc = getXmlFactory(sessionHandlerImplObj.getCompanyid());
            BusinessProcess p = processBag.getProcess(xsrc, processid);
            Properties prop = new Properties();
            prop.setProperty("json", "{}");
            JsonFactory jsrc = new JsonFactory(prop, jsonParser);
            processBag.addProcess(jsrc, p);
            retdata=jsrc.getJSONObject().getJSONObject(processid).toString(4);
        } catch (Exception ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
            retdata = "{msg:'"+ex.getMessage()+"',success:false}";
        } finally {
            return new ModelAndView("jsonView", "model", retdata);
        }
    }

    public ModelAndView saveProcess(HttpServletRequest request, HttpServletResponse response) {
        String retdata = "{msg:'process created successfully',success:true}";
        try {
            HashMap prm=new HashMap();
            String processid = request.getParameter("processid");
            JSONObject jobj = new JSONObject(request.getParameter("process"));
            JSONObject jobjproc = new JSONObject();
            jobjproc.put(processid, jobj);
            SourceFactory jsrc = getJsonFactory(jobjproc.toString());
            BusinessProcess p= processBag.getProcess(jsrc,processid);
            SourceFactory xsrc = getXmlFactory(sessionHandlerImplObj.getCompanyid());
            processBag.addProcess(xsrc, p);
            xsrc.save();
        } catch (Exception ex) {
            Logger.getLogger(WizardController.class.getName()).log(Level.SEVERE, null, ex);
            retdata = "{msg:'"+ex.getMessage()+"',success:false}";
        } finally {
            return new ModelAndView("jsonView", "model", retdata);
        }
    }

    public static void postMail(String recipient, String subject, String htmlmsg, String plainmsg, String from) {
        try {
        SendMailHandler.postMail(new String[]{recipient}, subject, htmlmsg, plainmsg, from);
        }catch(MessagingException ex){ex.printStackTrace();}
    }
}
